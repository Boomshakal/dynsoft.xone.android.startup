package dynsoft.xone.android.start;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.google.gson.Gson;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.LoginInfo;
import dynsoft.xone.android.core.LoginRequest;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.core.Workbench;
import dynsoft.xone.android.data.*;
import dynsoft.xone.android.helper.StorageHelper;
import dynsoft.xone.android.start.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class CopyOfFrmLogin extends Activity {

    public final static Network[] NetworkList = new Network[] {
        Network.Network1,
        Network.Network2,
        Network.Network3,
        Network.Network4,
        Network.Network5,
        Network.Network6,
        Network.Network7
    };

	private ProgressDialog _progressDialog;
	private TextView _txtVersion;
	private ImageButton _btnLang;
	private ImageButton _btnNetwork;
	private ImageButton _btnBook;
	private ImageButton _btnLogin;
	private ImageButton _btnExit;
	private EditText _txtLang;
	private EditText _txtNetwork;
	private EditText _txtServer;
	private EditText _txtPort;
	private EditText _txtBook;
	private EditText _txtUser;
	private EditText _txtPassword;
	private ToggleButton _chkRemember;
	public ButtonTextCell LangCell;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
            StrictMode.setThreadPolicy(policy);
        }

        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.frm_login);
        
        _txtVersion = (TextView)this.findViewById(R.id.txtVersion);
        //_btnLang = (ImageButton)this.findViewById(R.id.btnLang);
        //_btnNetwork = (ImageButton)this.findViewById(R.id.btnNetwork);
        _btnBook = (ImageButton)this.findViewById(R.id.btnBook);
        _btnLogin = (ImageButton)this.findViewById(R.id.btnLogin);
    	_btnExit = (ImageButton)this.findViewById(R.id.btnExit);
    	//_txtLang = (EditText)this.findViewById(R.id.txtLang);
    	//_txtNetwork = (EditText)this.findViewById(R.id.txtNetwork);
    	_txtServer = (EditText)this.findViewById(R.id.txtServer);
    	_txtPort = (EditText)this.findViewById(R.id.txtPort);
    	_txtBook = (EditText)this.findViewById(R.id.txtBook);
        _txtUser = (EditText)this.findViewById(R.id.txtUser);
        _txtPassword = (EditText)this.findViewById(R.id.txtPassword);
        _chkRemember = (ToggleButton)this.findViewById(R.id.chkRemember);
        
		try {
			PackageInfo packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			_txtVersion.setText("v"+packInfo.versionName);
		} catch (NameNotFoundException e) {
		}
		
//		_btnLang.setOnClickListener(new Button.OnClickListener(){
//			@Override
//			public void onClick(View view) {
//				FrmLogin.this.chooseLanguage();
//			}
//		});
//		
//		_txtNetwork.setTag(0);
//		_txtNetwork.setText(this.getString(R.string.frm_login_net_lan));
//        
//		_btnNetwork.setOnClickListener(new Button.OnClickListener(){
//			@Override
//			public void onClick(View view) {
//				FrmLogin.this.chooseNetwork();
//			}
//		});
		
		_btnBook.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View view) {
				CopyOfFrmLogin.this.chooseBook();
			}
		});
		
        _btnLogin.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View view) {
				((CopyOfFrmLogin)view.getContext()).login();
			}
		});
        
        _btnExit.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View view) {
				CopyOfFrmLogin.this.finish();
			}
		});
        
        _txtServer.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
            	_txtServer.setError(null);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        }); 
        
        _txtUser.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
            	_txtUser.setError(null);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
        
        _progressDialog = new ProgressDialog(this);
		_progressDialog.setMessage(this.getString(R.string.frm_login_logining));
		_progressDialog.setCancelable(false);

		this.loadConfig();
    }
    
//	private void chooseLanguage()
//    {
//    	final Locale[] locales = Locale.getAvailableLocales();
//    	ArrayList<String> names = new ArrayList<String>();
//    	for (Locale l : locales) {
//    		names.add(l.getDisplayName());
//    	}
//    	
//    	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (which >= 0) {
//					String item = (String)((AlertDialog)dialog).getListView().getAdapter().getItem(which);
//					if (item != null) {
//						for (Locale l : locales) {
//							if (l.getDisplayName().equals(item)) {
//								FrmLogin.this.refreshLocale(l);
//								break;
//							}
//						}
//					}
//				}
//				dialog.dismiss();
//			}
//		};
//
//	    new AlertDialog.Builder(this)
//	    .setTitle(this.getString(R.string.frm_login_lang_selector))
//	    .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(App.Current.Locale.getDisplayName()), listener)
//	    .setNegativeButton(this.getString(R.string.frm_login_cancel), null).show();
//    }
	
//	private void chooseNetwork()
//	{
//		BaseAdapter adapter = new BaseAdapter(){
//
//            @Override
//            public int getCount() {
//                return NetworkList.length;
//            }
//
//            @Override
//            public Object getItem(int position) {
//                return NetworkList[position];
//            }
//
//            @Override
//            public long getItemId(int position) {
//                return position;
//            }
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                Network net = NetworkList[position];
//                TextView view = new TextView(FrmLogin.this);
//                view.setTextSize(24);
//                view.setPadding(20, 10, 10, 10);
//                view.setText(net.Name);
//                return view;
//            }
//        };
//    	
//    	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (which >= 0) {
//					Network net = NetworkList[which];
//					_txtNetwork.setTag(net);
//					_txtNetwork.setText(net.Name);
//				}
//				dialog.dismiss();
//			}
//		};
//		
//		int index = 0;
//		if (_txtNetwork.getTag() != null) {
//		    index = Arrays.asList(NetworkList).indexOf(_txtNetwork.getTag());
//		}
//
//	    new AlertDialog.Builder(this)
//	    .setTitle(this.getString(R.string.frm_login_net_selector))
//	    .setSingleChoiceItems(adapter, index, listener)
//	    .setNegativeButton(this.getString(R.string.frm_login_cancel), null).show();
//	}
	
	private void chooseBook()
	{
	    final String server = _txtServer.getText().toString();
        if (server == null || server.length() < 1) {
            _txtServer.setError(this.getString(R.string.frm_login_miss_server));
            return;
        }
        
        final String port = _txtPort.getText().toString();
        if (port == null || server.length() < 1) {
            _txtPort.setError(this.getString(R.string.frm_login_miss_port));
            return;
        }
        
	    App.Current.Server.Address = server;
	    App.Current.Server.Port = port;
	    
	    App.Current.Server.GetBooksAsync(new ResultHandler<Book[]>(){
	        @Override
	        public void handleMessage(Message msg) {
	            if (this.Value.Value != null) {
	                final Book[] books = this.Value.Value;
	                ArrayList<String> names = new ArrayList<String>();
	                for (Book book : books) {
	                    names.add(book.Name);
	                }
	                
	                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        if (which >= 0) {
	                            _txtBook.setTag(books[which]);
	                            _txtBook.setText(books[which].Name);
	                        }
	                        dialog.dismiss();
	                    }
	                };

	                new AlertDialog.Builder(CopyOfFrmLogin.this)
	                .setTitle(CopyOfFrmLogin.this.getString(R.string.frm_login_book_selector))
	                .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(_txtBook.getText().toString()), listener)
	                .setNegativeButton(CopyOfFrmLogin.this.getString(R.string.frm_login_cancel), null).show();
	            }
	        }
	    });
	}
	
//	private void refreshLocale(Locale locale)
//	{
//		App.Current.Locale = locale;
//		Locale.setDefault(locale);
//		FrmLogin.this._txtLang.setText(locale.getDisplayName());
//		Context context = App.Current.getBaseContext();
//		Resources resources = context.getResources();
//		Configuration config = resources.getConfiguration();
//		config.locale = locale;
//		resources.updateConfiguration(config, null);
//		
//		((TextView)this.findViewById(R.id.lblLang)).setText(this.getString(R.string.frm_login_lang));
//		((TextView)this.findViewById(R.id.lblNetwork)).setText(this.getString(R.string.frm_login_net));
//		((TextView)this.findViewById(R.id.lblServer)).setText(this.getString(R.string.frm_login_server));
//		((TextView)this.findViewById(R.id.lblPort)).setText(this.getString(R.string.frm_login_port));
//		((TextView)this.findViewById(R.id.lblBook)).setText(this.getString(R.string.frm_login_book));
//		((TextView)this.findViewById(R.id.lblUser)).setText(this.getString(R.string.frm_login_user));
//		((TextView)this.findViewById(R.id.lblPassword)).setText(this.getString(R.string.frm_login_pwd));
//		((TextView)this.findViewById(R.id.lblRemember)).setText(this.getString(R.string.frm_login_remember));
//
//		((TextView)this.findViewById(R.id.lblLang)).setTextAppearance(this, R.style.line_label);
//		((TextView)this.findViewById(R.id.lblNetwork)).setTextAppearance(this, R.style.line_label);
//		((TextView)this.findViewById(R.id.lblServer)).setTextAppearance(this, R.style.line_label);
//		((TextView)this.findViewById(R.id.lblPort)).setTextAppearance(this, R.style.line_label);
//		((TextView)this.findViewById(R.id.lblBook)).setTextAppearance(this, R.style.line_label);
//		((TextView)this.findViewById(R.id.lblUser)).setTextAppearance(this, R.style.line_label);
//		((TextView)this.findViewById(R.id.lblPassword)).setTextAppearance(this, R.style.line_label);
//	}
	
	private String getEncryptedPassword(String text)
	{
	    try {
	    	
            MessageDigest md5 = MessageDigest.getInstance("MD5" );
            return Base64.encodeToString(md5.digest(text.getBytes("utf-8")), Base64.NO_WRAP);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
	    
	    return null;
	}

    private void login()
    {
//        Network network = (Network)_txtNetwork.getTag();
//        if (network == null) {
//            _txtNetwork.setError(this.getString(R.string.frm_login_miss_network));
//            return;
//        }
        
    	final String server = _txtServer.getText().toString();
    	if (server == null || server.length() < 1) {
    		_txtServer.setError(this.getString(R.string.frm_login_miss_server));
    		return;
    	}
    	
    	final String port = _txtPort.getText().toString();
    	if (port == null || server.length() < 1) {
    		_txtPort.setError(this.getString(R.string.frm_login_miss_port));
    		return;
    	}
    	
    	final Book book = (Book)_txtBook.getTag();
    	if (book == null || book.Code == null || book.Code.length() == 0) {
    		_txtBook.setError(this.getString(R.string.frm_login_miss_book));
    		return;
    	}
    	
    	final String userCode = _txtUser.getText().toString();
    	if (userCode == null || userCode.length() < 1) {
    		_txtUser.setError(this.getString(R.string.frm_login_miss_user));
    		return;
    	}
    	
    	App.Current.Network = new Network("NET1","NET1");
    	App.Current.Server.Address = server;
        App.Current.Server.Port = port;
        App.Current.Locale = Locale.getDefault();
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.Platform = App.Current.Platform;
        loginRequest.Network = App.Current.Network.Code;
        loginRequest.HostName = App.Current.HostName;
        loginRequest.BookCode = book.Code;
        loginRequest.UserCode = userCode;
        loginRequest.Password = this.getEncryptedPassword(_txtPassword.getText().toString());
        loginRequest.BookConnectorCode = "core_and";
        loginRequest.CommonConnectorCode = "common_and";
        loginRequest.UserTypes = "1";
        
        Gson gson = new Gson();
    	Request request = new Request();
    	request.Feature = App.Current.FeatureCode;
        request.Data = gson.toJson(loginRequest);
    	
    	_progressDialog.show();
    	App.Current.Server.LoginAsync(request, new ResultHandler<LoginInfo>(){
    		@Override
    	    public void handleMessage(Message msg) {
    		    
    	        Result<LoginInfo> result = this.Value;
    	        if (result.HasError) {
    	            CopyOfFrmLogin.this._progressDialog.dismiss();
    	            App.Current.showError(CopyOfFrmLogin.this, result.Error);
    	            return;
    	        }
    	        
    	        dynsoft.xone.android.core.LoginInfo info = result.Value;
                if (info == null || info.Session == null || info.Session.length() == 0) {
                    CopyOfFrmLogin.this._progressDialog.dismiss();
                    App.Current.showError(CopyOfFrmLogin.this, "@/core_frm_login_logon_fail");
                    return;
                }
                
                App.Current.Session = info.Session;
                App.Current.UserID = info.UserID;
                App.Current.UserCode = info.UserCode;
                App.Current.UserName = info.UserName;
                App.Current.BookCode = info.BookCode;
                App.Current.BookName = info.BookName;
                
                App.Current.BranchID = "81";

                if (_chkRemember.isChecked()) {
                    CopyOfFrmLogin.this.saveConfig(false);
                } else {
                    CopyOfFrmLogin.this.saveConfig(true);
                }
                
                if (info.BookConnector != null) {
                    App.Current.BookConnector = new SqlServerConnector();
                    App.Current.BookConnector.LoginServer = App.Current.Server.Address;
                    App.Current.BookConnector.Network = App.Current.Network.Code;
                    info.BookConnector.AssignConnector(App.Current.BookConnector);
                } else {
                    App.Current.Server.Logout();
                    CopyOfFrmLogin.this._progressDialog.dismiss();
                    App.Current.showError(CopyOfFrmLogin.this, "@/core_frm_login_book_conn_null");
                    return;
                }
                
                if (info.CommonConnector != null) {
                    App.Current.CommonConnector = new SqlServerConnector();
                    App.Current.CommonConnector.LoginServer = App.Current.Server.Address;
                    App.Current.CommonConnector.Network = App.Current.Network.Code;
                    info.CommonConnector.AssignConnector(App.Current.CommonConnector);
                } else {
                    App.Current.Server.Logout();
                    CopyOfFrmLogin.this._progressDialog.dismiss();
                    App.Current.showError(CopyOfFrmLogin.this, "@/core_frm_login_common_conn_null");
                    return;
                }
 
                CopyOfFrmLogin.this.saveNetworks();
                App.Current.startCore();
                CopyOfFrmLogin.this._progressDialog.dismiss();
                
                checkUpdate();
    		}
    	});
    }
    
    private void saveNetworks()
    {
        SQLiteDatabase db = App.Current.openOrCreateDatabase(App.Current.BookCode, Context.MODE_PRIVATE, null);
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS network(code nvarchar(500),name nvarchar(500))");
            db.execSQL("delete from network");
            String sql = "select * from core_network";
            Result<DataTable> r = App.Current.CommonConnector.ExecuteDataTable(sql);
            if (r.Value != null) {
                for (DataRow row : r.Value.Rows) {
                    String code = row.getValue("code", String.class);
                    String name = row.getValue("name", String.class);
                    db.execSQL("insert into network(code,name)values(?,?)", new Object[]{ code, name });
                }
            }
        } finally {
            db.close();
        }
    }
    
    public void checkUpdate()
	{
		FutureTask<Result<String>> task = new FutureTask<Result<String>>(new Callable<Result<String>>(){
			@Override
			public Result<String> call() {
				String sql = "select version from core_program (nolock) where code='android_client_v01'";
				return App.Current.DbPortal.ExecuteScalar(App.Current.CommonConnector, sql, String.class);
			}
		});
		
		new Thread(task).start();
		try {
			Result<String> r = task.get();
			if (r.HasError) {
				CopyOfFrmLogin.this._progressDialog.dismiss();
				App.Current.showError(CopyOfFrmLogin.this, r.Error);
				return;
			}
			
			final String serverVersion = r.Value;
			if (serverVersion == null) {
				this.afterLogin();
				return;
			}
			
			PackageInfo packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String localVersion =  packInfo.versionName;
			if (serverVersion.equals(localVersion) == true) {
				this.afterLogin();
				return;
			}
			
			final AlertDialog alertDialog = new AlertDialog.Builder(CopyOfFrmLogin.this)
    		.setTitle("提示")
    		.setMessage("服务器上存在不同的程序版本（v"+ serverVersion +"），将替换当前版本（v"+ localVersion +"）")
    		.setPositiveButton("是", new DialogInterface.OnClickListener(){
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				dialog.dismiss();
    				_progressDialog.setMessage("正在下载更新...");
    				_progressDialog.show();
    				
    				FutureTask<byte[]> t = new FutureTask<byte[]>(new Callable<byte[]>(){
    					@Override
    					public byte[] call() {
    						String sql = "select data from core_program where code='android_client_v01'";
    						byte[] data = App.Current.DbPortal.ExecuteScalar(App.Current.CommonConnector, sql, byte[].class).Value;
        					_progressDialog.dismiss();
        					if (data != null && data.length > 0) {
        						String path = Environment.getExternalStorageDirectory() + File.separator + "dynsoft.xone.android.startup_v"+ serverVersion +".apk";
        						StorageHelper.writeAllBytesToFile(path, data);
        						Intent intent = new Intent(Intent.ACTION_VIEW);  
        						intent.setDataAndType(Uri.fromFile(new File(path)),"application/vnd.android.package-archive");  
        						startActivity(intent);  
        					}
        					return data;
    					}
    				});
    				
    				new Thread(t).start();
    			}}).create();
    		alertDialog.show();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
    
    private void afterLogin()
    {
		Intent intent = new Intent();
		intent.setClass(CopyOfFrmLogin.this, Workbench.class);
		startActivity(intent);
		CopyOfFrmLogin.this.finish();
    }
    
    private void loadConfig()
    {
    	SharedPreferences sp = this.getSharedPreferences("X1_LOGIN_INFO", 0);
//    	Locale def = Locale.getDefault();
//    	String defloc = def.getDisplayName();
//    	String loc = sp.getString("lang", def.getDisplayName());
//    	if (loc == null || loc.length() ==0 || loc.equals(defloc)) {
//    	    _txtLang.setText(defloc);
//    	    App.Current.Locale = def;
//    	} else {
//    	    Locale[] locales = Locale.getAvailableLocales();
//            for (Locale l : locales) {
//                if (loc.equals(l.getDisplayName())) {
//                    this.refreshLocale(l);
//                }
//            }
//    	}
//    	
//    	
//    	if (App.Current.Locale == null) {
//    	    _txtLang.setText(defloc);
//            App.Current.Locale = def;
//    	}
//    
//    	Network network = Network.Network1;
//    	String net = sp.getString("net", Network.Network1.Code);
//    	for (Network n : NetworkList) {
//    	    if (n.Code.equals(net)) {
//    	        network = n;
//    	        break;
//    	    }
//    	}
//    	_txtNetwork.setTag(network);
//    	_txtNetwork.setText(network.Name);

    	_txtServer.setText(sp.getString("server", ""));
    	_txtPort.setText(sp.getString("port", "6680"));
    	
    	Book book = new Book();
    	book.Code = sp.getString("bookcode", "");
    	book.Name = sp.getString("bookname", "");
    	
    	_txtBook.setTag(book);
    	_txtBook.setText(book.Name);
    	
    	_txtUser.setText(sp.getString("user", ""));
    	_txtPassword.setText(sp.getString("password", ""));
    	_chkRemember.setChecked(sp.getBoolean("remember", true));
    }
    
    private void saveConfig(boolean empty)
    {
    	SharedPreferences sp = this.getSharedPreferences("X1_LOGIN_INFO", 0);
    	SharedPreferences.Editor editor = sp.edit();
//    	editor.putString("lang", _txtLang.getText().toString());
//    	Network network = (Network)_txtNetwork.getTag();
//    	editor.putString("net", network.Code);
    	editor.putString("server", _txtServer.getText().toString());
	    editor.putString("port", _txtPort.getText().toString());
	    editor.putString("bookname", _txtBook.getText().toString());
	    editor.putString("bookcode", ((Book)_txtBook.getTag()).Code);
	    editor.putString("user", _txtUser.getText().toString());
	    if(empty) {
	        editor.putString("password", "");
	    } else {
	        editor.putString("password", _txtPassword.getText().toString());
	    }
	    editor.putBoolean("remember", _chkRemember.isChecked());
	    editor.commit();
    }

}
