package dynsoft.xone.android.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;



//import org.apache.http.HttpException;
//import org.apache.http.HttpRequest;
import org.nfc.read.ParsedNdefRecord;


import dynsoft.xone.android.util.Bimp;
import dynsoft.xone.android.util.FileUtils;
import dynsoft.xone.android.util.ImageItem;

import dynsoft.xone.android.util.Res;
import com.motorolasolutions.adc.decoder.ScannerController;
import com.motorolasolutions.adc.decoder.ScannerController.ScanListener;

import dynsoft.xone.android.core.R;
import dynsoft.xone.android.hr.NdefMessageParser;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.scanner.lianxin.IDataBarcodeScanner;
import android.hardware.barcode.Scanner;
import dynsoft.xone.android.start.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import com.android.barcodescandemo.ScannerInerface;
import com.intermec.aidc.*; 



import com.seuic.scanner.DecodeInfo;
import com.seuic.scanner.DecodeInfoCallBack;
import com.seuic.scanner.ScannerFactory;
import com.seuic.scanner.ScannerKey;
import com.seuic.scannerapitest.service.ScannerService;
//import com.seuic.scanner.Scanner;

import dynsoft.xone.android.util.PublicWay;



@SuppressLint("HandlerLeak") public class Workbench extends Activity implements BarcodeReadListener {

    public static final int RQ_SCAN_BY_CAMERA = 1001213;
    private static final int TAKE_PICTURE = 1001214;
    
    public static final boolean ScannerEnabled = true;
    public static final boolean CameraScannerEnabled = false;
    public static final boolean JieBaoScannerEnabled = false;
    public static final boolean LianXinScannerEnabled = true;
    public static final boolean UrovoScannerEnabled = false;
    public static final boolean IntermecScannerEnabled = true;
    public static final boolean CilicoScannerEnabled=true;
    public static final boolean SeuicScannerEnabled=true;
    

   
	private final int duration = 1; // seconds
	private final int sampleRate = 2000;
	private final int numSamples = duration * sampleRate;
	private final double sample[] = new double[numSamples];
	private final double freqOfTone = 1600; // hz

	private final byte generatedSnd[] = new byte[2 * numSamples];
	
	private Handler mHandler = new MainHandler();
	
	private static final String SCANACTION = "com.exmple.broadcast";
	
	private static final DateFormat TIME_FORMAT = SimpleDateFormat
			.getDateTimeInstance();
	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private NdefMessage mNdefPushMessage;
	private AlertDialog mDialog;
	private String m_Broadcastname;
	
	private String filepath;
	private IntentFilter seuicfilter;
	private SparseIntArray seuicmapParams;
	private com.seuic.scanner.Scanner seuicscanner;
 
	
	BroadcastReceiver seuicreceiver=new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
		}

	};
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
		}

	};

	private class MainHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Scanner.BARCODE_READ: {
				Workbench.this.onScan((String) msg.obj );
				play();
				break;
			}
			case Scanner.BARCODE_NOREAD: {
				break;
			}
			default:
				break;
			}
		}
	};
	
	void playSound() {

		AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, numSamples,
				AudioTrack.MODE_STATIC);
		audioTrack.write(generatedSnd, 0, numSamples);
		audioTrack.play();
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		audioTrack.release();
	}

	void play() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				genTone();
				playSound();
			}
		});
		thread.start();
	}
	
	void genTone() {
		// fill out the array
		for (int i = 0; i < numSamples; ++i) {
			sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
		}

		// convert to 16 bit pcm sound array
		// assumes the sample buffer is normalised.
		int idx = 0;
		for (double dVal : sample) {
			short val = (short) (dVal * 32767);
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
		}
	}
	
	
    public Dialog MenuDialog;
    public BaseAdapter MenuAdapter;
    public ViewPager ViewPager;
    public PaneAdapter PaneAdapter;
    public Pane WorkSpace;
    public static Bitmap bimap ;
    
    private ShakeListener _shakerListener;
    private com.intermec.aidc.BarcodeReader bcr;
    private com.intermec.aidc.VirtualWedge wedg;
    String strDeviceId, strBarcodeData, strSymbologyId; 
    
    private BroadcastReceiver _idata_receiver;
	private IntentFilter _idata_filter;
	private ScannerInerface _idata_controller;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        App.Current.Workbench = this;
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
            StrictMode.setThreadPolicy(policy);
        }
        
        
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.workbench);
        
        
        this.initScanner();
        
        this.ViewPager = (ViewPager)this.findViewById(R.id.viewpager);
        this.PaneAdapter = new PaneAdapter(this, this.ViewPager);
        this.ViewPager.setAdapter(this.PaneAdapter);
        this.ViewPager.setOnPageChangeListener(this.PaneAdapter);
        
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        //SFC扫描
    	if  (mAdapter !=null )
		{
    	resolveIntent(getIntent());
		mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null)
				.create();
		// 获取默认的NFC控制器
		
		//拦截系统级的NFC扫描，例如扫描蓝牙
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord("",
				Locale.ENGLISH, true) });
		
		}
		
        
        View menuView = this.getLayoutInflater().inflate(R.layout.lo_menulist, null);
        this.MenuDialog = new Dialog(this);
        this.MenuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.MenuDialog.getWindow().setBackgroundDrawable(null);
        this.MenuDialog.setContentView(menuView);
        this.MenuDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU) {
                    dialog.dismiss();
                }
                return false;
            }
        });
        

       
        
        ListView lvMenu = (ListView)menuView.findViewById(R.id.lvMenu);
        this.MenuAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return Workbench.this.PaneAdapter.PaneList.size();
            }

            @Override
            public Object getItem(int position) {
                return Workbench.this.PaneAdapter.PaneList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = Workbench.this.getLayoutInflater().inflate(R.layout.lo_menuitem, null);
                    ImageButton btn = (ImageButton)convertView.findViewById(R.id.btnClose);
                    btn.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_white"));
                    btn.setOnClickListener(new OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Pane pane = (Pane)Workbench.this.MenuAdapter.getItem(position);
                            if (pane != null) {
                                ((Workbench)App.Current.Workbench).closePane(pane);
                            }
                        }
                    });
                }
                
                ImageView icon = (ImageView)convertView.findViewById(R.id.imgIcon);
                TextView title = (TextView)convertView.findViewById(R.id.txtTitle);

                Pane pane = (Pane)this.getItem(position);
                icon.setImageBitmap(App.Current.ResourceManager.getImage(pane.Icon));
                
                if (pane instanceof pn_base) {
                	pn_base pn = (pn_base)pane;
                    title.setText(pn.Header.Title.getText().toString());
                } else {
                	title.setText(pane.Title);
                }

                if (Workbench.this.ViewPager.getCurrentItem() == position) {
                    title.setTextColor(Color.parseColor("#FFD000"));
                } else {
                    title.setTextColor(Color.WHITE);
                }
                
                convertView.setTag(pane);
                return convertView;
            }
        };
        lvMenu.setAdapter(this.MenuAdapter);
        lvMenu.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pane pane = Workbench.this.PaneAdapter.PaneList.get(position);
                if (pane != null) {
                    Workbench.this.showPane(pane);
                }
                Workbench.this.MenuDialog.dismiss();
            }
        });
        
        Link link = new Link("pane://x:code=core_and_workspace");
        this.WorkSpace = (Pane)link.CreateObject(null, this, null);
        if (this.WorkSpace != null) {
            this.showPane(this.WorkSpace);
        }
        
      
        PublicWay.activityList.add(this);
        
       
        
    }
    


	private long getDec(byte[] bytes) {
		long result = 0;
		long factor = 1;
		for (int i = 0; i < bytes.length; ++i) {
			long value = bytes[i] & 0xffl;
			result += value * factor;
			factor *= 256l;
		}
		return result;
	}


	//一般公家卡，扫描的信息
	private String dumpTagData(Parcelable p) {
		String IdNo ="";
		Tag tag = (Tag) p;
		byte[] id = tag.getId();
		IdNo=String.valueOf(getDec(id));
		return IdNo;
	}
	//初步判断是什么类型NFC卡
	private void resolveIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage[] msgs;
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[0];
				byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
				Parcelable tag = intent
						.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				Workbench.this.onScan(dumpTagData(tag));
			   
			}
		}
	}
	
	private NdefRecord newTextRecord(String text, Locale locale,
			boolean encodeInUtf8) {
		byte[] langBytes = locale.getLanguage().getBytes(
				Charset.forName("US-ASCII"));

		Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset
				.forName("UTF-16");
		byte[] textBytes = text.getBytes(utfEncoding);

		int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		char status = (char) (utfBit + langBytes.length);

		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		data[0] = (byte) status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length,
				textBytes.length);

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
				new byte[0], data);
	}
    
    private ScannerController JieBaoScanner;
    
    private void initScanner()
    {
        if (ScannerEnabled) {
            if (LianXinScannerEnabled) {
            	_idata_controller = new ScannerInerface(this);
            	_idata_controller.open();
            	_idata_controller.setOutputMode(1);//使用广播模式
            	_idata_filter = new IntentFilter("android.intent.action.SCANRESULT");
            	_idata_receiver = new BroadcastReceiver() {
        			@Override
        			public void onReceive(Context context, Intent intent) {
        				// 此处获取扫描结果信息
        				final String barcode = intent.getStringExtra("value");
        				if (barcode != null && barcode.length() > 0) {
        					Workbench.this.onScan(barcode);
        				}
        			}
        		};
            }
            if (CilicoScannerEnabled)
            {
   			 //TODO Auto-generated method stub
   			 //赋值handle句柄
   			//Scanner.m_handler = mHandler;
   			// 初始化扫描头
   			//Scanner.InitSCA();
            	 receiver = new BroadcastReceiver() {
            		@Override
            		public void onReceive(Context arg0, Intent arg1) {
            			if (arg1.getAction().equals(m_Broadcastname)) {
            				String str = arg1.getStringExtra("BARCODE");
            				Workbench.this.onScan(str);
            			}
            		}

            	};
   			
            }
            
            
            if (JieBaoScannerEnabled) {
                try {
                    JieBaoScanner = new ScannerController(Workbench.this, new ScanListener(){
                        @Override
                        public void onScan(String result, String error) {
                            if (result != null) {
                                Workbench.this.onScan(result.trim());
                            }                            
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if (IntermecScannerEnabled) {
            	if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    AidcManager.connectService(this, new AidcManager.IServiceListener() {
                        public void onConnect()
                        {
                            // The dependent service is connected and it is ready
                            // to receive barcode requests.
                            doBarcodReader();
                        }

                        public void onDisconnect()
                        {
                        }
                    });
                }

            }
            if (SeuicScannerEnabled){
          	  
            	seuicfilter = new IntentFilter(SCANACTION);
            	seuicreceiver = new BroadcastReceiver() {
             		@Override
             		public void onReceive(Context context, Intent intent) {
            			if (intent.getAction().equals(SCANACTION)) {
            				String code = intent.getStringExtra("scannerdata");
            				Workbench.this.onScan(code);
            				}
             		}

             	};
    			
              }
        }
    }

    public void doBarcodReader()
    {
         try
         { 
             //disable virtual wedge
             wedg = new VirtualWedge();
             wedg.setEnable(false);  
             
             //set barcode reader object for internal scanner
             bcr = new BarcodeReader();
             
             //add barcode reader listener
             bcr.addBarcodeReadListener(this);

         }
         catch (BarcodeReaderException e) {
             e.printStackTrace();
         }
         catch (SymbologyException e)
         {
        	 e.printStackTrace();
         }
         catch (SymbologyOptionsException e)
         {
        	 e.printStackTrace();
         } catch (VirtualWedgeException e) {
			e.printStackTrace();
		}
    }

    public void barcodeRead(BarcodeReadEvent aBarcodeReadEvent)
    {
         strDeviceId =  aBarcodeReadEvent.getDeviceId();
         strBarcodeData =  aBarcodeReadEvent.getBarcodeData();
         strSymbologyId = aBarcodeReadEvent.getSymbolgyId();

         //update data to edit fields
         runOnUiThread(new Runnable() {
             public void run() {
            	 Workbench.this.onScan(strBarcodeData);
             }
         });
    }

    public void onScan(String barcode)
    {
        //Workbench.this.playBeepSound(R.raw.scan_beep);
        int index = Workbench.this.ViewPager.getCurrentItem();
        if (index > -1) {
            Pane pane = Workbench.this.PaneAdapter.PaneList.get(index);
            if (pane != null) {
                pane.onScan(barcode);
            }
        }
    }
    
    public void onRefrsh()
    {
    	   int index = Workbench.this.ViewPager.getCurrentItem();
           if (index > -1) {
               Pane pane = Workbench.this.PaneAdapter.PaneList.get(index);
               if (pane != null) {
                   pane.onRefrsh();
               }
           }
    }
   
    @Override       
	public boolean onContextItemSelected(MenuItem menuItem) {
		
    	int index = Workbench.this.ViewPager.getCurrentItem();
        if (index > -1) {
            Pane pane = Workbench.this.PaneAdapter.PaneList.get(index);
            if (pane != null) {
                return pane.onContextItemSelected(menuItem);
            }
        }
		
		return false;
	}

    @SuppressWarnings("deprecation")
	@Override
    protected void onResume() {
        super.onResume();
        
        if (LianXinScannerEnabled) {
        	registerReceiver(_idata_receiver, _idata_filter);
        }
        if(SeuicScannerEnabled)
        {
        	seuicfilter.addAction(SCANACTION);
        	seuicfilter.setPriority(Integer.MAX_VALUE);
        	registerReceiver(seuicreceiver, seuicfilter);
        }

        if (_shakerListener == null) {
            _shakerListener = new ShakeListener(this);
            _shakerListener.setOnShakeListener(new ShakeListener.OnShakeListener () {
                public void onShake() {
                    Workbench.this.onShake();
                }
            });
        }
        
        _shakerListener.resume();
        
    	if (mAdapter == null) {
			//if (!mAdapter.isEnabled()) {
			//	showWirelessSettingsDialog();
			//}

			//showMessage(R.string.error, R.string.no_nfc);
			//App.Current.showError(this, "设备不支持NFC！");
			//return;
		}else
		{
		if (!mAdapter.isEnabled()) {
			App.Current.showError(this, "请在系统设置中先启用NFC功能！");
			return;
		}
		
		if (mAdapter != null) {
			//隐式启动
			mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
			mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
		}
		}
    	if (CilicoScannerEnabled)
    	{
    		final IntentFilter intentFilter = new IntentFilter();
    		m_Broadcastname = "com.barcode.sendBroadcast";
    		intentFilter.addAction(m_Broadcastname);
    		registerReceiver(receiver, intentFilter);
    	}
    	  this.onRefrsh();        
        
    }
    
	private void showWirelessSettingsDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//builder.setMessage(R.string.nfc_disabled);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						Intent intent = new Intent(
								Settings.ACTION_WIRELESS_SETTINGS);
						startActivity(intent);
					}
				});
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						finish();
					}
				});
		builder.create().show();
		return;
	}


    @Override
    protected void onPause() {
        super.onPause();
        
        if (LianXinScannerEnabled) {
        	this.unregisterReceiver(_idata_receiver);
        }
        if (SeuicScannerEnabled)
        {
        	unregisterReceiver(seuicreceiver);
        	
        }

        if (_shakerListener != null) {
            _shakerListener.pause();
        }
        
    	if (mAdapter != null) {
			//隐式启动
			mAdapter.disableForegroundDispatch(this);
			mAdapter.disableForegroundNdefPush(this);
		}
    	unregisterReceiver(receiver);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        if (LianXinScannerEnabled) {
        	_idata_receiver = null;
    		_idata_filter = null;
    		_idata_controller.close();
        }
        if (SeuicScannerEnabled)
        {
        	seuicreceiver =null;
        	seuicfilter=null;	
        }
        
        if (JieBaoScannerEnabled) {
            if (JieBaoScanner != null) {
                try {
                    JieBaoScanner.close();
                } catch (Exception ex) {
                }
            }
        }
        
        if (IntermecScannerEnabled){
        	if(bcr != null)
            {
                try {
					bcr.removeBarcodeReadListener(this);
				} catch (BarcodeReaderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                bcr.close();
                bcr = null;
            }
                           
            wedg = null;
            AidcManager.disconnectService();
        }
    }

    public void onShake()
    {
        if (CameraScannerEnabled) {
            this.scanByCamera();
        }
    }
    
   
    public void photo() {
		Intent openCameraIntent = new Intent();
		openCameraIntent.setAction("android.media.action.IMAGE_CAPTURE");
		openCameraIntent.addCategory("android.intent.category.DEFAULT"); 
		String fileName = String.valueOf(System.currentTimeMillis());
		filepath=Environment.getExternalStorageDirectory()+"/"+fileName+".jpg";
		File file = new File(filepath);
		Uri uri = Uri.fromFile(file);  
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}
    
 
    public void scanByCamera()
    {
        PackageInfo packageInfo;
        try {
            packageInfo = this.getPackageManager().getPackageInfo("com.google.zxing.client.android", 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
        }
        
        if(packageInfo ==null){
            Dialog dialog = new AlertDialog.Builder(this)
            .setTitle("错误")
            .setMessage("没有安装 Barcode Scanner")
            .setPositiveButton("确定", null).create();
            dialog.show();
            
        } else {
            App.Current.playSound(R.raw.shake_beep);
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.setPackage("com.google.zxing.client.android");
            startActivityForResult(intent, RQ_SCAN_BY_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        String filePath = "";
        if (requestCode == Workbench.RQ_SCAN_BY_CAMERA) {
            if (resultCode == Workbench.RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                this.onScan(contents);
            }
        }   
        else if (requestCode==TAKE_PICTURE)
        {
	        if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
				
				FileInputStream fis;
				try {
				fis = new FileInputStream(filepath);
				Bitmap bm=BitmapFactory.decodeStream(fis);
				//FileUtils.saveBitmap(bm, fileName);	
				ImageItem takePhoto = new ImageItem();
				takePhoto.setBitmap(bm);
				takePhoto.setImagePath(filepath);
				Bimp.tempSelectBitmap.add(takePhoto);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
        }
        else  {
            int index = this.ViewPager.getCurrentItem();
            if (index > -1) {
                Pane pane = this.PaneAdapter.PaneList.get(index);
                if (pane != null) {
                    pane.onActivityResult(requestCode, resultCode, intent);
                }
            }
        }
       
    }
    
    public boolean containsPane(String code, Object flag)
    {
        return this.PaneAdapter.containsPane(code, flag);
    }
    
    public Pane getPane(String code, Object flag)
    {
        return this.PaneAdapter.getPane(code, flag);
    }
    
    public Pane[] GetPanes(String code)
    {
    	return this.PaneAdapter.getPanes(code);
    }
    
    public void showPane(Pane pane)
    {
        if (pane == null) return;
        
        int index = this.PaneAdapter.PaneList.indexOf(pane);
        if (index < 0) {
            index = this.PaneAdapter.addPane(pane);
            this.PaneAdapter.notifyDataSetChanged();
            this.MenuAdapter.notifyDataSetChanged();
        }
        this.ViewPager.setCurrentItem(index, true);
    }
    
    public void closePane(Pane pane)
    {
        if (pane == this.WorkSpace) return;

        int current = this.ViewPager.getCurrentItem();
        int index = this.PaneAdapter.PaneList.indexOf(pane);
        Pane newPane = null;

        if (current == index) {
            if (index < this.PaneAdapter.PaneList.size() - 1) {
                newPane = this.PaneAdapter.PaneList.get(index+1);
            } else {
                index = this.PaneAdapter.PaneList.size() - 2;
                if (index < 0) index = 0;
                newPane = this.PaneAdapter.PaneList.get(index);
            }
        } else {
            newPane = this.PaneAdapter.PaneList.get(current);
        }
        
        this.PaneAdapter.removePane(pane);
        this.PaneAdapter.notifyDataSetChanged();
        this.MenuAdapter.notifyDataSetChanged();
        
        if (newPane != null) {
            this.showPane(newPane);
        }
    }
    
    public void showMenuDialog()
    {
        this.openOptionsMenu();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("menu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        this.MenuDialog.show();
        return false;
    }

    private static int JieBaoPdaScanKeyUpFlag = 1;
    private static int JieBaoPdaScanKeyDownFlag = 0;
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
            this.exit();
            return false;
        } 

        if (LianXinScannerEnabled) {
            if (keyCode == KeyEvent.KEYCODE_F9 || keyCode == KeyEvent.KEYCODE_F10 || keyCode == KeyEvent.KEYCODE_F11) {
                IDataBarcodeScanner.StartScan();
                return true;
            }
        }
        
        if (JieBaoScannerEnabled) {
            if (keyCode == 132) {
                if (JieBaoScanner != null) {
                    JieBaoPdaScanKeyDownFlag = 1;
                    if (JieBaoPdaScanKeyUpFlag == 1 && JieBaoPdaScanKeyDownFlag == 1) {
                        JieBaoScanner.start();
                        JieBaoPdaScanKeyUpFlag = 0;
                    }

                }
            }
        }
        if (CilicoScannerEnabled)
        {
        	
        }
        
        return super.onKeyDown(keyCode, event);
    }
   
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (LianXinScannerEnabled) {
            if (keyCode == KeyEvent.KEYCODE_F9 || keyCode == KeyEvent.KEYCODE_F10 || keyCode == KeyEvent.KEYCODE_F11) {
                IDataBarcodeScanner.StopScan();
                return true;
            }
        }
        
        if (JieBaoScannerEnabled) {
            if (keyCode == 132) {
                JieBaoPdaScanKeyUpFlag = 1;
            }
        }
        
        return super.onKeyUp(keyCode, event);
    }
    
    public void exit()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
        .setTitle("询问")
        .setMessage("确定要退出程序吗？")
        .setPositiveButton("确定", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                App.Current.Server.Logout();
                App.Current.ServiceManager.stopServices();
                Workbench.this.finish();
                System.exit(0);
                
            }}).setNegativeButton("取消",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
        }).create();
        alertDialog.show();
    }
    
    public class PaneAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener
    {
        public ArrayList<Pane> PaneList;
        public Context Context;
        public ViewPager ViewPager;
        
        public PaneAdapter(Context context, ViewPager pager)
        {
            this.PaneList = new ArrayList<Pane>();
            this.Context = context;
            this.ViewPager = pager;
        }
        
        public boolean containsPane(String code, Object flag)
        {
            if (code == null) return false;
            for (Pane pane : this.PaneList) {
                if (pane.Code != null && pane.Code.equals(code) == true && pane.Flag == flag) {
                    return true;
                }
            }
            return false;
        }
        
        public Pane getPane(String code, Object flag)
        {
            if (code == null) return null;
            for (Pane pane : this.PaneList) {
                if (pane.Code != null && pane.Code.equals(code) == true && pane.Flag == flag) {
                    return pane;
                }
            }
            return null;
        }
        
        public Pane[] getPanes(String code)
        {
        	if (code == null) 
        		return null;
        	
        	ArrayList<Pane> arr = new ArrayList<Pane>();
            for (Pane pane : this.PaneList) {
                if (pane.Code != null && pane.Code.equals(code) == true) {
                    arr.add(pane);
                }
            }
            
            return arr.toArray(new Pane[arr.size()]);
        }
        
        public int addPane(Pane pane)
        {
            return this.insertPane(pane, this.PaneList.size());
        }
        
        public int insertPane(Pane pane, int index)
        {
            this.PaneList.add(index, pane);
            return index;
        }
        
        public void removePane(Pane pane)
        {
            this.ViewPager.setAdapter(null);
            this.PaneList.remove(pane);
            this.ViewPager.setAdapter(this);
        }
        
        public void removePane(int index)
        {
            this.ViewPager.setAdapter(null);
            this.PaneList.remove(index);
            this.ViewPager.setAdapter(this);
        }
        
        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
            //return super.getItemPosition(object);
        }

        @Override
        public int getCount() {
            return this.PaneList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(this.PaneList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = this.PaneList.get(position);
            container.addView(view);
            return view;
        }
        
        @Override
        public float getPageWidth(int position) {
            
            DisplayMetrics metric = this.Context.getApplicationContext().getResources().getDisplayMetrics();
            float density = metric.density;
            
            int width = (int)(metric.widthPixels/density); //screen width in DP
            int height = (int)(metric.heightPixels/density); //screen height in DP
            
            if (width > height) {
                if (width >= 900) {
                    return (float)(0.33);
                } else if (width > 600) {
                    return (float)0.5;
                } else {
                    return (float)1.0;
                }
            } else {
                if (width >= 1200) {
                    return (float)0.5;
                } else {
                    return (float)1.0;
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
            
        }
        
        

        @Override
        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
            
        }    
       
    }
    @Override
	public void onNewIntent(Intent intent) {
		setIntent(intent);
		if (mAdapter!=null)
		{
		  resolveIntent(intent);
		}
	}
}
