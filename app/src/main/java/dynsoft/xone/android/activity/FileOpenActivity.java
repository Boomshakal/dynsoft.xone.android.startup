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
            mDownloadBtn.setText("���ļ�");
        }

        if (isLocalExist()) {
            mDownloadBtn.setVisibility(View.GONE);
            displayFile();
        } else {
            Log.e("len", "��ʼ����");
            startDownload();
        }

        initWpsCloseListener();
    }

    @Override
    public void onBackPressed() {
        finish();//���ص��˽��棬֮������ļ����޷�����
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
            //ͨ��WPS���ļ�
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
// ? ? ? ? ? ?startActivity(Intent.createChooser(intent,"ѡ���������"));
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent intent = getPackageManager().getLaunchIntentForPackage("cn.wps.moffice_eng");
                    openDoc(intent);
                    finish();
                }

            } else {
                App.Current.toastError(this, "���Ȱ�װWPS");
            }
        }
    }

    private boolean checkWps() {
        Intent intent = getPackageManager().getLaunchIntentForPackage("cn.wps.moffice_eng");//WPS���˰�İ���
        if (intent == null) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * �ӷ����������ļ�
     *
     * @param path     �����ļ��ĵ�ַ
     * @param FileName �ļ�����
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
                        InputStream is = con.getInputStream();//��ȡ������
                        FileOutputStream fileOutputStream = null;//�ļ������
                        if (is != null) {
                            FileUtils.createSDDir(FileName);
                            fileOutputStream = new FileOutputStream(new File(FileName));//ָ���ļ�����·�������뿴��һ��
                            byte[] buf = new byte[1024];
                            int ch;
                            while ((ch = is.read(buf)) != -1) {
                                fileOutputStream.write(buf, 0, ch);//����ȡ������д���ļ���
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


    // �ص��ӿ�
    public interface WpsInterface {
        void doRequest(String filePath);//filePathΪ�ĵ��ı���·��

        void doFinish();
    }

    // �㲥������
    private class WpsCloseListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals("cn.wps.moffice.file.save")) {
//                    String fileSavePath = intent.getExtras().getString(Environment.getExternalStorageDirectory() + File.separator + "Download/" + mFileName);
//                    if (canWrite) {
//                        wpsInterface.doRequest(fileSavePath);// ����ص�
//                    }
                } else if (intent.getAction().equals("cn.wps.moffice.file.close") ||
                        intent.getAction().equals("com.kingsoft.writer.back.key.down")) {
                    wpsInterface.doFinish();// �ر�,���ػص�
                    unregisterReceiver(wpsCloseListener);//ע���㲥
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //ע��㲥
    public void initWpsCloseListener() {
        wpsCloseListener = new WpsCloseListener();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.kingsoft.writer.back.key.down");//���·��ؼ�
        filter.addAction("com.kingsoft.writer.home.key.down");//����home��
        filter.addAction("cn.wps.moffice.file.save");//����
        filter.addAction("cn.wps.moffice.file.close");//�ر�
        registerReceiver(wpsCloseListener, filter);//ע��㲥
    }

    public void openDoc(Intent intent) {
        Bundle bundle = new Bundle();
//        if (canWrite) {// �ж��Ƿ���Ա༭�ĵ�
//            bundle.putString("OpenMode", "Normal");// һ��ģʽ
//        } else {
        bundle.putString("OpenMode", "ReadOnly");// ֻ��ģʽ
//        }
        bundle.putBoolean("SendSaveBroad", true);// �رձ���ʱ�Ƿ��͹㲥
        bundle.putBoolean("SendCloseBroad", true);// �ر��ļ�ʱ�Ƿ��͹㲥
        bundle.putBoolean("HomeKeyDown", true);// ����Home��
        bundle.putBoolean("BackKeyDown", true);// ����Back��
        bundle.putBoolean("IsShowView", false);// �Ƿ���ʾwps����
        bundle.putBoolean("AutoJump", true);// //���������ļ�ʱ�Ƿ��Զ���ת
        //���ù㲥
        bundle.putString("ThirdPackage", getPackageName());
        //������Ӧ�õİ��������ڶԸ�Ӧ�úϷ��Ե���֤
        //bundle.putBoolean(Define.CLEAR_FILE, true);
        //�رպ�ɾ�����ļ�
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
        bundle.putString(WpsModel.OPEN_MODE, WpsModel.OpenMode.NORMAL); // ��ģʽ
        bundle.putBoolean(WpsModel.SEND_CLOSE_BROAD, true); // �ر�ʱ�Ƿ��͹㲥
        bundle.putString(WpsModel.THIRD_PACKAGE, getPackageName()); // ������Ӧ�õİ��������ڶԸ�Ӧ�úϷ��Ե���֤
        bundle.putBoolean(WpsModel.CLEAR_TRACE, true);// ����򿪼�¼
        // bundle.putBoolean(CLEAR_FILE, true); //�رպ�ɾ�����ļ�
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setClassName(WpsModel.PackageName.NORMAL, WpsModel.ClassName.NORMAL);

        File file = new File(path);
        if (file == null || !file.exists()) {
            System.out.println("�ļ�Ϊ�ջ��߲�����");
            return false;
        }

        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        intent.putExtras(bundle);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            System.out.println("��wps�쳣��" + e.toString());
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
        //ɾ��Ŀ¼�������ļ�
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
//                        .load(downloadFileUrl)     //��ȡ���ص�ַ
//                        .setDownloadPath(curFilePath) //�����ļ����������·��
//                        .start();   //��������
//            }
//        }.start();

//        fileDownloader.getImpl().create(downloadFileUrl)
//                .setPath(curFilePath)
//                .setForceReDownload(true)
//                .setListener(new FileDownloadLargeFileListener() {
//                    @Override
//                    protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
//                        if (totalBytes > 0) {
//                            mDownloadBtn.setText("��������..." + (soFarBytes / totalBytes) * 100 + "%");
//                            Log.e("len", "��������..." + (soFarBytes / totalBytes) * 100 + "%");
//                        }
//                    }
//
//                    @Override
//                    protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
//                        mDownloadBtn.setText("��������..." + (soFarBytes / totalBytes) * 100 + "%");
//                        Log.e("len", "��������..." + (soFarBytes / totalBytes) * 100 + "%");
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
//                        Toast.makeText(FileOpenActivity.this, "����" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
//        //�����ﴦ��������ɵ�״̬
//        displayFile();
//    }

    private void queryDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(mRequestId);
        Cursor cursor = null;
        try {
            cursor = mDownloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                //�Ѿ����ص��ֽ���
                int currentBytes = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //�������ص��ֽ���
                int totalBytes = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                //״̬���ڵ�������
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                Log.i("len", currentBytes + " " + totalBytes + " " + status);
                mDownloadBtn.setText("�������أ�" + currentBytes + "/" + totalBytes);
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