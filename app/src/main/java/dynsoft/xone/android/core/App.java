package dynsoft.xone.android.core;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.tencent.smtt.sdk.QbSdk;
import com.uuzuche.lib_zxing.DisplayUtil;

import dynsoft.xone.android.data.*;
import dynsoft.xone.android.manager.ClassManager;
import dynsoft.xone.android.manager.ConfigManager;
import dynsoft.xone.android.manager.ConnectorManager;
import dynsoft.xone.android.manager.EvaluatorManager;
import dynsoft.xone.android.manager.LinkerManager;
import dynsoft.xone.android.manager.ResourceManager;
import dynsoft.xone.android.manager.ServiceManager;

public class App extends MultiDexApplication {

    public static App Current;

    private Timer _timer;

    @SuppressWarnings("static-access")
    public App() {
        App.Current = this;
        this.Platform = "AND";
        this.DbPortal = new DbPortal();
        this.Server = new Server();
//        this.MegApi =new MegApi();
        this.ClassManager = new ClassManager();
        this.ConnectorManager = new ConnectorManager();
        this.EvaluatorManager = new EvaluatorManager();
        this.LinkerManager = new LinkerManager();
        this.ResourceManager = new ResourceManager();
        this.ServiceManager = new ServiceManager();
        this.ConfigManager = new ConfigManager();
        this.FormParameters = new LinkedHashMap<String, Object>();
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化扫描
        QbSdk.initX5Environment(getApplicationContext(), new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(boolean b) {

            }
        });
        //初始化测量工具
        initDisplayOpinion();
        //初始化文件下载
        initFileDownloader();
    }

    private void initFileDownloader() {
        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(new FileDownloadUrlConnection.Creator(new FileDownloadUrlConnection.Configuration()
                        .readTimeout(5000)
                        .connectTimeout(5000)))
                .commit();
    }

    private void initDisplayOpinion() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        DisplayUtil.density = dm.density;
        DisplayUtil.densityDPI = dm.densityDpi;
        DisplayUtil.screenWidthPx = dm.widthPixels;
        DisplayUtil.screenhightPx = dm.heightPixels;
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(getApplicationContext(), dm.widthPixels);
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(getApplicationContext(), dm.heightPixels);
    }

    public final static String CommonConnectorCode = "common_and";

    public final static String BookConnectorCode = "core_and";

    public String Platform;

    public String FeatureCode;

    public String HostName;

    public Locale Locale;

    public Network Network;

    public String AppDirectory;

    public String BookDirectory;

    public DbPortal DbPortal;

    public Server Server;

//    public MegApi MegApi;

    public String Session;

    public String BookCode;

    public String BookName;

    public String BranchID;

    public String BranchCode;

    public String BranchName;

    public String UserID;

    public String UserCode;

    public String UserName;

    public String Passowrd;

    public String ErpUserName;

    public String ErpPassword;

    public Workbench Workbench;

    public Connector CommonConnector;

    public Connector BookConnector;

    public ClassManager ClassManager;

    public ConnectorManager ConnectorManager;

    public EvaluatorManager EvaluatorManager;

    public ServiceManager ServiceManager;

    public LinkerManager LinkerManager;

    public ResourceManager ResourceManager;

    public ConfigManager ConfigManager;

    public Map<String, Object> FormParameters;

    public void startCore() {
        App.Current.FeatureCode = this.getDeviceUUID();
        App.Current.HostName = this.getDeviceName();
        App.Current.BookDirectory = App.Current.AppDirectory + File.separator + App.Current.BookCode;
        App.this.ClassManager.syncData();
        App.this.ServiceManager.loadServices();

        _timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                App.Current.Server.Hello();
            }
        };
        _timer.schedule(task, 60000, 120000);
    }

    public String getDeviceUUID() {
        TelephonyManager tm = (TelephonyManager) this.getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceID = tm.getDeviceId();

        if (deviceID == null || deviceID.length() == 0) {
            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            deviceID = info.getMacAddress();
        }

        if (deviceID == null || deviceID.length() == 0) {
            deviceID = UUID.randomUUID().toString();
        }

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            deviceID = UUID.nameUUIDFromBytes(
                    md5.digest(deviceID.getBytes("utf-8"))).toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return deviceID;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    /**
     * 获取MAC地址
     *
     * @return
     */
    public String getMacAddress() {
        String strMacAddr = null;
        try {
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip)
                    .getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return strMacAddr;
    }

    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    protected static InetAddress getLocalInetAddress() {

        InetAddress ip = null;
        try {
            //列举
            Enumeration en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Enumeration en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = (InetAddress) en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }
                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public Object getExpressionValue(String expression) {
        if (expression == null || expression.length() == 0)
            return null;

        if (expression.equals("app:bookcode")) {
            return App.Current.BookCode;
        }

        return null;
    }

    public void stopCore() {
        this.Server.Logout();
        if (this.ServiceManager != null) {
            this.ServiceManager.stopServices();
        }
    }

    public Request createRequest() {
        Request request = new Request();
        request.Session = App.Current.Session;
        request.Feature = App.Current.FeatureCode;
        return request;
    }

    public void showDebug(Context context, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("调试").setMessage(message)
                .setPositiveButton("确定", null).create();
        alertDialog.show();
    }

    public void showInfo(Context context, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("信息").setMessage(message)
                .setPositiveButton("确定", null).create();
        alertDialog.show();
    }

    public void showWarning(Context context, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("警告").setMessage(message)
                .setPositiveButton("确定", null).create();
        alertDialog.show();
    }

    public void showError(Context context, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("错误").setMessage(message)
                .setPositiveButton("确定", null).create();
        alertDialog.show();
    }

    public void question(Context context, String message, OnClickListener okListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("询问").setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", null).create();
        alertDialog.show();
    }

    public void question2(Context context, String message, OnClickListener okListener, OnClickListener csncelListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("询问").setMessage(message)
                .setPositiveButton("是", okListener)
                .setNegativeButton("否", csncelListener).create();
        alertDialog.show();
    }

    public void prompt(Context context, String message, String text, final PromptCallback callback) {
        final EditText txt = new EditText(context);
        txt.setHint(text);
        new AlertDialog.Builder(context)
                .setTitle(message)
                .setView(txt)
                .setPositiveButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callback != null) {
                            callback.onReturn(txt.getText().toString());
                        }
                    }
                }).setNegativeButton("取消", null)
                .show();
    }

    public void toastInfo(Context context, String message) {
        View toastRoot = App.Current.Workbench.getLayoutInflater().inflate(R.layout.toast, null);
        TextView txt = (TextView) toastRoot.findViewById(R.id.txt_toast);
        txt.setBackgroundResource(R.drawable.bg_toast_green);
        txt.setTextColor(Color.BLACK);
        txt.setText(message);

        Toast toast = new Toast(this);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastRoot);
        toast.show();
    }

    public void toastWarning(Context context, String message) {
        View toastRoot = App.Current.Workbench.getLayoutInflater().inflate(R.layout.toast, null);
        TextView txt = (TextView) toastRoot.findViewById(R.id.txt_toast);
        txt.setBackgroundResource(R.drawable.bg_toast_yellow);
        txt.setTextColor(Color.BLACK);
        txt.setText(message);

        Toast toast = new Toast(this);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastRoot);
        toast.show();
    }

    public void toastError(Context context, String message) {
        View toastRoot = App.Current.Workbench.getLayoutInflater().inflate(R.layout.toast, null);
        TextView txt = (TextView) toastRoot.findViewById(R.id.txt_toast);
        txt.setBackgroundResource(R.drawable.bg_toast_red);
        txt.setTextColor(Color.WHITE);
        txt.setText(message);

        Toast toast = new Toast(this);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastRoot);
        toast.show();
    }

//    public String getToken() {
//        Retrofit uploadCloudRetrofit = RetrofitDownUtil.getInstence().getUploadCloudRetrofit();
//        final TokanBean tokanBean = new TokanBean("3207d4b9c49d7000", "1eedd049ee3e81a4f2996d5ff3251d67");
//        Gson gson = new Gson();
//        String obj = gson.toJson(tokanBean);
//        IotServiceApi iotServiceApi = uploadCloudRetrofit.create(IotServiceApi.class);
//        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), obj);
//        Call<ResponseBody> responseBodyCall = iotServiceApi.getToken(body);
//        responseBodyCall.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
//                Log.e("len", "EEEEE:" + response.code());
//                if (response.code() == 200) {
//                    try {
//                        Gson gson = new Gson();
//                        String string = response.body().string();
//                        TokanResponseBean tokanResponseBean = gson.fromJson(string, TokanResponseBean.class);
//                        Log.e("len", string + "**" + tokanResponseBean.getToken());
//                        token = tokanResponseBean.getToken();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    if (response.errorBody() != null) {
//                        try {
//                            App.Current.toastError(App.Current.Workbench, "删除失败。" + response.errorBody().string());
//                            Log.e("len", response.errorBody().string());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        App.Current.toastError(App.Current.Workbench, "删除失败。" + response.message());
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable throwable) {
//                App.Current.showError(App.Current.Workbench, "删除失败Failure。" + throwable.getMessage());
//            }
//        });
//    }

    private MediaPlayer _mediaPlayer;

    public void playSound(int resource) {
        if (_mediaPlayer == null) {
            _mediaPlayer = new MediaPlayer();
        }

        AssetFileDescriptor file = getResources().openRawResourceFd(resource);
        try {
            _mediaPlayer.reset();
            _mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            file.close();
            _mediaPlayer.prepare();
            _mediaPlayer.start();
        } catch (IOException e) {
        }
    }

    public void Print(String code, String title, Map<String, String> parameters) {
        if (code != null && code.length() > 0) {
            Intent intent = new Intent();
            intent.setClass(this, frm_printer.class);
            intent.putExtra("__print_code__", code);
            intent.putExtra("__print_title__", title);

            if (parameters != null) {
                for (Entry<String, String> entry : parameters.entrySet()) {
                    intent.putExtra(entry.getKey(), entry.getValue());
                }
            }

            App.Current.Workbench.startActivity(intent);
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static String formatCalendar(Calendar cal, String format) {
        return (String) DateFormat.format(format, cal);
    }

    public static String formatDateTime(Object date, String format) {
        return formatDateTime(date, format, "");
    }

    public static String formatDateTime(Object date, String format, String def) {
        if (date == null)
            return def;
        return new SimpleDateFormat(format).format(date);
    }

    public static String formatNumber(Object number, String format) {
        return formatNumber(number, format, "");
    }

    public static String formatNumber(Object number, String format, String def) {
        if (number == null)
            return def;

        DecimalFormat f = new DecimalFormat(format);
        return f.format(number);
    }

    public static int parseInteger(String str, int def) {

        if (str == null || str.length() == 0)
            return def;

        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
        }
        return def;
    }

    public static double parseDouble(String str, double def) {

        if (str == null || str.length() == 0)
            return def;

        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
        }
        return def;
    }

    public static BigDecimal parseDecimal(String str, BigDecimal def) {

        if (str == null || str.length() == 0)
            return def;

        try {
            return new BigDecimal(str);
        } catch (NumberFormatException nfe) {
        }
        return def;
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * @return a generated ID value
     */
    public static int generateViewId() {

        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range
            // under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF)
                newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }

        // SDK_INT_17: Build.VERSION_CODES.JELLY_BEAN_MR1
        // if (Build.VERSION.SDK_INT < 17) {
        // for (;;) {
        // final int result = sNextGeneratedId.get();
        // // aapt-generated IDs have the high byte nonzero; clamp to the range
        // under that.
        // int newValue = result + 1;
        // if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
        // if (sNextGeneratedId.compareAndSet(result, newValue)) {
        // return result;
        // }
        // }
        // } else {
        // return View.generateViewId();
        // }
    }
}
