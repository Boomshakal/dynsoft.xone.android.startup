package dynsoft.xone.android.core;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
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
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import dynsoft.xone.android.data.*;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.manager.ClassManager;
import dynsoft.xone.android.manager.ConfigManager;
import dynsoft.xone.android.manager.ConnectorManager;
import dynsoft.xone.android.manager.EvaluatorManager;
import dynsoft.xone.android.manager.LinkerManager;
import dynsoft.xone.android.manager.ResourceManager;
import dynsoft.xone.android.manager.ServiceManager;
import dynsoft.xone.android.wms.frm_item_lot_printer;
import dynsoft.xone.android.wms.pn_wo_issue_mgr;

public class App extends MultiDexApplication  {

	public static App Current;

	private Timer _timer;

	@SuppressWarnings("static-access")
	public App() {
		App.Current = this;
		this.Platform = "AND";
		this.DbPortal = new DbPortal();
		this.Server = new Server();
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
	
	public void prompt(Context context, String message, String text, final PromptCallback callback)
	{
		final EditText txt = new EditText(context);
		new AlertDialog.Builder(context)
		.setTitle(message)
		.setView(txt)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (callback != null) {
					callback.onReturn(txt.getText().toString());
				}
			}
		}).setNegativeButton("取消", null)
		.show();
	}
	
	public void toastInfo(Context context, String message)
	{
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
	
	public void toastWarning(Context context, String message)
	{
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
	
	public void toastError(Context context, String message)
	{
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
	
	private MediaPlayer _mediaPlayer;
	
	public void playSound(int resource)
    {
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
	
	public void Print(String code, String title, Map<String,String> parameters)
	{
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
	 * Generate a value suitable for use in {@link #setId(int)}. This value will
	 * not collide with ID values generated at build time by aapt for R.id.
	 * 
	 * @return a generated ID value
	 */
	public static int generateViewId() {

		for (;;) {
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
