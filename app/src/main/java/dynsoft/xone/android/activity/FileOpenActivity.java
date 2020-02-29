package dynsoft.xone.android.activity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.util.FileUtils;
import dynsoft.xone.android.util.WpsModel;

public class FileOpenActivity extends Activity implements TbsReaderView.ReaderCallback {
    private static final int DOWNLOADFINISH = 1;
    private TbsReaderView mTbsReaderView;
    private Button mDownloadBtn;

    private DownloadManager mDownloadManager;
    private long mRequestId;
    private DownloadObserver mDownloadObserver;
    private String mFileUrl;
    private String mFileName;
    private WpsInterface wpsInterface;
    private WpsCloseListener wpsCloseListener;
    private String downloadFileUrl;
    private String curFilePath;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DOWNLOADFINISH) {
                displayFile();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openfile);

        Intent intent = getIntent();
        mFileUrl = intent.getStringExtra("url");
        mTbsReaderView = new TbsReaderView(this, this);
        mDownloadBtn = (Button) findViewById(R.id.btn);
        RelativeLayout rootRl = (RelativeLayout) findViewById(R.id.relativelayout);
        rootRl.addView(mTbsReaderView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        mFileName = parseName(mFileUrl);

        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFile();
            }
        });
        if (isLocalExist()) {
            mDownloadBtn.setText("打开文件");
        }

        if (isLocalExist()) {
            mDownloadBtn.setVisibility(View.GONE);
            displayFile();
        } else {
            Log.e("len", "开始下载");
            startDownload();
        }

        initWpsCloseListener();
    }

    @Override
    public void onBackPressed() {
        finish();//不关掉此界面，之后加载文件会无法加载
    }


    public void displayFile() {
        String filePath = getFilesDir() + "/" + mFileName;
//        openFile(filePath);
        File file = new File(filePath);
        Bundle bundle = new Bundle();
        bundle.putString("filePath", filePath);
        bundle.putString("tempPath", getFilesDir().getAbsolutePath());
        boolean result = mTbsReaderView.preOpen(parseFormat(mFileName), true);
        Log.e("len", "RESULT:" + result);
        if (result) {
            mTbsReaderView.openFile(bundle);
//            mDownloadBtn.setVisibility(View.GONE);
        } else {
            //通过WPS打卡文件
            if (checkWps()) {
                if (mFileName.endsWith(".zip") || mFileName.endsWith(".rar")) {
                    if (null == file || !file.exists()) {
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.fromFile(file), "file/*");
                    try {
                        startActivity(intent);
// ? ? ? ? ? ?startActivity(Intent.createChooser(intent,"选择浏览工具"));
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent intent = getPackageManager().getLaunchIntentForPackage("cn.wps.moffice_eng");
                    openDoc(intent);
                    finish();
                }

            } else {
                App.Current.toastError(this, "请先安装WPS");
            }
        }
    }

    private boolean checkWps() {
        Intent intent = getPackageManager().getLaunchIntentForPackage("cn.wps.moffice_eng");//WPS个人版的包名
        if (intent == null) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 从服务器下载文件
     *
     * @param path     下载文件的地址
     * @param FileName 文件名字
     */
    public void downLoad(final String path, final String FileName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(5000);
                    con.setConnectTimeout(5000);
                    con.setRequestProperty("Charset", "UTF-8");
                    con.setRequestMethod("GET");
                    if (con.getResponseCode() == 200) {
                        InputStream is = con.getInputStream();//获取输入流
                        FileOutputStream fileOutputStream = null;//文件输出流
                        if (is != null) {
                            FileUtils.createSDDir(FileName);
                            fileOutputStream = new FileOutputStream(new File(FileName));//指定文件保存路径，代码看下一步
                            byte[] buf = new byte[1024];
                            int ch;
                            while ((ch = is.read(buf)) != -1) {
                                fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
                            }
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Message message = new Message();
                    message.what = DOWNLOADFINISH;
                    handler.sendMessage(message);
//                    displayFile();
                }
            }
        }).start();
    }


    // 回调接口
    public interface WpsInterface {
        void doRequest(String filePath);//filePath为文档的保存路径

        void doFinish();
    }

    // 广播接收器
    private class WpsCloseListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals("cn.wps.moffice.file.save")) {
//                    String fileSavePath = intent.getExtras().getString(Environment.getExternalStorageDirectory() + File.separator + "Download/" + mFileName);
//                    if (canWrite) {
//                        wpsInterface.doRequest(fileSavePath);// 保存回调
//                    }
                } else if (intent.getAction().equals("cn.wps.moffice.file.close") ||
                        intent.getAction().equals("com.kingsoft.writer.back.key.down")) {
                    wpsInterface.doFinish();// 关闭,返回回调
                    unregisterReceiver(wpsCloseListener);//注销广播
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //注册广播
    public void initWpsCloseListener() {
        wpsCloseListener = new WpsCloseListener();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.kingsoft.writer.back.key.down");//按下返回键
        filter.addAction("com.kingsoft.writer.home.key.down");//按下home键
        filter.addAction("cn.wps.moffice.file.save");//保存
        filter.addAction("cn.wps.moffice.file.close");//关闭
        registerReceiver(wpsCloseListener, filter);//注册广播
    }

    public void openDoc(Intent intent) {
        Bundle bundle = new Bundle();
//        if (canWrite) {// 判断是否可以编辑文档
//            bundle.putString("OpenMode", "Normal");// 一般模式
//        } else {
        bundle.putString("OpenMode", "ReadOnly");// 只读模式
//        }
        bundle.putBoolean("SendSaveBroad", true);// 关闭保存时是否发送广播
        bundle.putBoolean("SendCloseBroad", true);// 关闭文件时是否发送广播
        bundle.putBoolean("HomeKeyDown", true);// 按下Home键
        bundle.putBoolean("BackKeyDown", true);// 按下Back键
        bundle.putBoolean("IsShowView", false);// 是否显示wps界面
        bundle.putBoolean("AutoJump", true);// //第三方打开文件时是否自动跳转
        //设置广播
        bundle.putString("ThirdPackage", getPackageName());
        //第三方应用的包名，用于对改应用合法性的验证
        //bundle.putBoolean(Define.CLEAR_FILE, true);
        //关闭后删除打开文件
        intent.setAction(android.content.Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.setData(Uri.parse(mFileUrl));
        intent.putExtras(bundle);
        startActivity(intent);
    }


    boolean openFile(String path) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(WpsModel.OPEN_MODE, WpsModel.OpenMode.NORMAL); // 打开模式
        bundle.putBoolean(WpsModel.SEND_CLOSE_BROAD, true); // 关闭时是否发送广播
        bundle.putString(WpsModel.THIRD_PACKAGE, getPackageName()); // 第三方应用的包名，用于对改应用合法性的验证
        bundle.putBoolean(WpsModel.CLEAR_TRACE, true);// 清除打开记录
        // bundle.putBoolean(CLEAR_FILE, true); //关闭后删除打开文件
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setClassName(WpsModel.PackageName.NORMAL, WpsModel.ClassName.NORMAL);

        File file = new File(path);
        if (file == null || !file.exists()) {
            System.out.println("文件为空或者不存在");
            return false;
        }

        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        intent.putExtras(bundle);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            System.out.println("打开wps异常：" + e.toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String parseFormat(String fileName) {
        Log.e("len", fileName.substring(fileName.lastIndexOf(".") + 1));
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private String parseName(String url) {
        String fileName = null;
        try {
            fileName = url.substring(url.lastIndexOf("/") + 1);
        } finally {
            if (TextUtils.isEmpty(fileName)) {
                fileName = String.valueOf(System.currentTimeMillis());
            }
        }
        return fileName;
    }

    private boolean isLocalExist() {
        return getLocalFile().exists();
    }

    private File getLocalFile() {
        return new File(getFilesDir(), mFileName);
    }

    private void startDownload() {
        //删除目录下所有文件
        FileDownloader.setup(this);
        final FileDownloader fileDownloader = new FileDownloader();
        curFilePath = getFilesDir() + "/" + mFileName;

        File dir = getFilesDir();
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile() && file.getName().compareToIgnoreCase(mFileName) != 0) {
                    file.delete();
                }
            }
        }

        downloadFileUrl = null;
        try {
            downloadFileUrl = URLEncoder.encode(mFileUrl, "utf-8").replaceAll("\\+",
                    "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        downloadFileUrl = downloadFileUrl.replaceAll("%3A", ":").replaceAll("%2F", "/");
        downLoad(downloadFileUrl, curFilePath);

//        new Thread(){
//            @Override
//            public void run() {
//                Aria.download(this)
//                        .load(downloadFileUrl)     //读取下载地址
//                        .setDownloadPath(curFilePath) //设置文件保存的完整路径
//                        .start();   //启动下载
//            }
//        }.start();

//        fileDownloader.getImpl().create(downloadFileUrl)
//                .setPath(curFilePath)
//                .setForceReDownload(true)
//                .setListener(new FileDownloadLargeFileListener() {
//                    @Override
//                    protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
//                        if (totalBytes > 0) {
//                            mDownloadBtn.setText("正在下载..." + (soFarBytes / totalBytes) * 100 + "%");
//                            Log.e("len", "正在下载..." + (soFarBytes / totalBytes) * 100 + "%");
//                        }
//                    }
//
//                    @Override
//                    protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
//                        mDownloadBtn.setText("正在下载..." + (soFarBytes / totalBytes) * 100 + "%");
//                        Log.e("len", "正在下载..." + (soFarBytes / totalBytes) * 100 + "%");
//                    }
//
//                    @Override
//                    protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
//
//                    }
//
//                    @Override
//                    protected void completed(BaseDownloadTask task) {
////                        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
////                        if (dir.isDirectory()) {
////                            for (File file : dir.listFiles()) {
////                                String name = file.getName();
////                                if(name.endsWith(".temp")) {
////                                    String replace = name.replace(".temp", "");
////                                    file.renameTo(new File(replace));
////                                }
////                            }
////                        }
//                        displayFile();
//                    }
//
//                    @Override
//                    protected void error(BaseDownloadTask task, Throwable e) {
//                        Log.e("len", e.getMessage());
//                        Toast.makeText(FileOpenActivity.this, "错误：" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//
//                    @Override
//                    protected void warn(BaseDownloadTask task) {
//                        Log.e("len", task.getFilename());
//                    }
//                }).start();
//        mDownloadObserver = new DownloadObserver(new Handler());
//        getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, mDownloadObserver);
//
//        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mFileUrl));
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mFileName);
//        request.allowScanningByMediaScanner();
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
//        mRequestId = mDownloadManager.enqueue(request);
    }


//    @Download.onTaskComplete
//    void taskComplete(DownloadTask task) {
//        //在这里处理任务完成的状态
//        displayFile();
//    }

    private void queryDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(mRequestId);
        Cursor cursor = null;
        try {
            cursor = mDownloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                //已经下载的字节数
                int currentBytes = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //总需下载的字节数
                int totalBytes = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                //状态所在的列索引
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                Log.i("len", currentBytes + " " + totalBytes + " " + status);
                mDownloadBtn.setText("正在下载：" + currentBytes + "/" + totalBytes);
                if (DownloadManager.STATUS_SUCCESSFUL == status && mDownloadBtn.getVisibility() == View.VISIBLE) {
                    mDownloadBtn.setVisibility(View.GONE);
                    mDownloadBtn.performClick();
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mTbsReaderView) {
            mTbsReaderView.onStop();
        }
//        if (mDownloadObserver != null) {
//            getContentResolver().unregisterContentObserver(mDownloadObserver);
//        }
    }

    private class DownloadObserver extends ContentObserver {

        private DownloadObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.i("downloadUpdate: ", "onChange(boolean selfChange, Uri uri)");
            queryDownloadStatus();
        }
    }
}