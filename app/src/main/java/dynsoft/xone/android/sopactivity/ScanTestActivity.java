package dynsoft.xone.android.sopactivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcB;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.barcodescandemo.ScannerInerface;
import com.chice.scangun.ScanGun;
import com.google.gson.Gson;
import com.motorolasolutions.adc.decoder.ScannerController;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import org.nfc.read.TextRecord;

import dynsoft.xone.android.base.BaseActivity;
import dynsoft.xone.android.bean.ExceptionBean;
import dynsoft.xone.android.bean.IotBean;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.core.Workbench;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.retrofit.RetrofitDownUtil;
import dynsoft.xone.android.retrofit.uploadcloud.BackSuccessBean;
import dynsoft.xone.android.retrofit.uploadcloud.IotServiceApi;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Created by Administrator on 2017/12/11.
 */

public class ScanTestActivity extends BaseActivity implements View.OnTouchListener {
    private final static char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final int MAINSCAN = 0;
    private static final int CHILDSCAN = 1;
    private static final int MIXED_FLOW = 3;    //����

    public static final boolean ScannerEnabled = true;
    public static final boolean CameraScannerEnabled = false;
    public static final boolean JieBaoScannerEnabled = false;
    public static final boolean LianXinScannerEnabled = true;
    public static final boolean UrovoScannerEnabled = false;
    public static final boolean IntermecScannerEnabled = true;
    public static final boolean CilicoScannerEnabled = true;
    public static final boolean SeuicScannerEnabled = true;
    private static final String SCANACTION = "com.exmple.broadcast";
    ;

    private ArrayList<Map<String, String>> mixedParameter;   //�������������������к�

    private static final int TIMENUMBER = 1000;

    private ArrayList<ExceptionBean> exceptionAll;
    private AddmoreAdapter addmoreAdapter;

    private LinearLayout linearlayout_scantest;
    private View relativeLayout;
    private ListView listview_1;
    private ListView listview_2;
    private ImageView image_view;
    private EditText edittext_1;
    private TextView textview_1;
    private TextView textview_2;
    private TextView textview_result;
    private TextView textview_time;
    private TextView textViewScan;
    private ArrayList<String> scanString;
    private ArrayList<String> scanString2;
    private ArrayList<String> childNumber;
    private MyListAdapter myListAdapter;
    private MyListAdapter2 myListAdapter2;
    private SharedPreferences sharedPreferences;
    private String production;
    private int scanCounts;
    private String[] split;
    private long order_id;
    private int sequence_id;
    private String station;
    private int foreman_id;
    private int worker_id;
    private int process_id;
    private String work_type;
    private String product_type;
    private float width_value;    //խ�߱�׼
    private float length_value;   //��߱�׼
    private float diameter_value; //ֱ����׼
    private String Nominal_size;
    private String edittext;
    private int childScanCount;    //��¼��Ҫɨ������ĸ���
    private int resultId;
    private String mainCode;
    private int status = MAINSCAN;
    private String check = "";
    private String segment;
    private String device;
    private String org_code;
    private SharedPreferences.Editor edit;
    private boolean isSnBinding;   //�Ƿ�����Ű�
    private boolean isConsistencyCheck;   //�Ƿ���һ���Լ��
    private boolean isHandleKey = true;
    private ScanGun mScanGun = null;
    private String task_order_code;
    private int task_order_id;
    private String old_task_order_code;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (textview_time != null) {
                textview_time.setText(getFormatTime(new Date()));
                handler.postDelayed(runnable, TIMENUMBER);
            }
        }
    };
    private String xml;
    private DataRow row;
    private PopupWindow popupWindow;
    private int width;
    private boolean isKeySHIFT;
    private StringBuilder _sb;
    private String badString;
    private int checkCounts;    //����һ���Լ��ĸ���
    private PopupWindow popupPassFailWindow;
    private ImageView imageViewGreen;
    private ImageView imageViewRed;
    private boolean defectfail;
    private String mac_address;
    private ScannerInerface _idata_controller;
    private IntentFilter _idata_filter;
    private BroadcastReceiver _idata_receiver;
    private BroadcastReceiver receiver;
    private ScannerController JieBaoScanner;
    private IntentFilter seuicfilter;
    private NfcAdapter defaultAdapter;
    private PendingIntent pendingIntent;
    private BroadcastReceiver seuicreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
        }

    };

    @Override
    public View setContentView() {
        sharedPreferences = getApplication().getSharedPreferences("sop", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();
        exceptionAll = new ArrayList<ExceptionBean>();
        production = sharedPreferences.getString("production", "");
        segment = sharedPreferences.getString("segment", "");
        org_code = sharedPreferences.getString("org_code", "");
        device = sharedPreferences.getString("device", "");
        station = sharedPreferences.getString("station", "");
        task_order_code = sharedPreferences.getString("task_order", "");
        task_order_id = sharedPreferences.getInt("order_task_id", 0);
        old_task_order_code = sharedPreferences.getString("task_order", "");
        String foreman = sharedPreferences.getString("foreman", "");
        String worker = sharedPreferences.getString("work_code", "");
        mixedParameter = new ArrayList<Map<String, String>>();
        getForeManId(foreman);
        getWorkerId(worker);

        View view = View.inflate(getBaseContext(), R.layout.activity_scantest, null);
        scanString = new ArrayList();
        scanString2 = new ArrayList();
        childNumber = new ArrayList<String>();
        linearlayout_scantest = (LinearLayout) view.findViewById(R.id.linearlayout_scantest);
        relativeLayout = view.findViewById(R.id.relativelayout);
        listview_1 = (ListView) view.findViewById(R.id.listview_1);
        listview_2 = (ListView) view.findViewById(R.id.listview_2);
        edittext_1 = (EditText) view.findViewById(R.id.edittext_1);
        image_view = (ImageView) view.findViewById(R.id.image_view);
        textview_1 = (TextView) view.findViewById(R.id.textview_1);
        textview_2 = (TextView) view.findViewById(R.id.textview_2);
        textview_result = (TextView) view.findViewById(R.id.textview);
        textview_time = (TextView) view.findViewById(R.id.textview_time);
        textViewScan = (TextView) view.findViewById(R.id.textview_scan);
        listview_1.setFocusable(false);
        listview_1.setFocusableInTouchMode(false);
        listview_2.setFocusable(false);
        listview_2.setFocusableInTouchMode(false);
        _sb = new StringBuilder();

        if (production.contains(",")) {
            split = production.split(",");
            textViewScan.setText(split[1]);
        }

        handler.postDelayed(runnable, TIMENUMBER);

        int seq_id = sharedPreferences.getInt("seq_id", 0);
        getCurCounts(textview_1, "to_seq_id", 83);
        getCurCounts(textview_2, "sequence_id", seq_id);
        if (image_view != null) {
            image_view.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_white"));
            image_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

        initData();
        initScanNumber();
        initScanner();
        edittext_1.setOnKeyListener(null);
//        edittext_1.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
//                    onScanned(edittext_1.getText().toString().replace("\n", ""));
//                }
//                return false;
//            }
//        });

        NfcManager nfcManager = (NfcManager) getSystemService(NFC_SERVICE);
        defaultAdapter = nfcManager.getDefaultAdapter();
        if (defaultAdapter == null) {
            Log.e("len", "��ǰ�豸��֧��NFC����");
        } else if (!defaultAdapter.isEnabled()) {
            Log.e("len", "��ǰ�豸NFC����û�д�");
        } else {
            pendingIntent = PendingIntent.getActivity(
                    this, 0, new Intent(this, getClass())
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        }

        return view;
    }

    private void checkShift(char ascallNoShift, char ascallOnShift) {
        if (this.isKeySHIFT) {
            _sb.append(ascallOnShift);
            this.isKeySHIFT = false;
        } else {
            _sb.append(ascallNoShift);
        }
    }

    private void handleNumPadKeys(int keyCode) {
        if (keyCode <= 153) {
            _sb.append((char) (keyCode - 96));
        } else if (keyCode == 154) {
            _sb.append('/');
        } else if (keyCode == 155) {
            _sb.append('*');
        } else if (keyCode == 156) {
            _sb.append('-');
        } else if (keyCode == 157) {
            _sb.append('+');
        } else if (keyCode == 158) {
            _sb.append('.');
        }
    }

    private void handleTopNumKeys(int keyCode) {
        if (keyCode >= 7 && keyCode <= 16) {
            switch (keyCode) {
                case 7:
                    this.checkShift('0', ')');
                    break;
                case 8:
                    this.checkShift('1', '!');
                    break;
                case 9:
                    this.checkShift('2', '@');
                    break;
                case 10:
                    this.checkShift('3', '#');
                    break;
                case 11:
                    this.checkShift('4', '$');
                    break;
                case 12:
                    this.checkShift('5', '%');
                    break;
                case 13:
                    this.checkShift('6', '^');
                    break;
                case 14:
                    this.checkShift('7', '&');
                    break;
                case 15:
                    this.checkShift('8', '*');
                    break;
                case 16:
                    this.checkShift('9', '(');
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode <= 6 && keyCode != 3 && keyCode != 4) {
            return super.dispatchKeyEvent(event);
        } else if (keyCode == 3 || keyCode == 4) {
            return super.onKeyDown(keyCode, event);
        } else {
            if (keyCode == 59) {
                this.isKeySHIFT = true;
            }

            if (keyCode == 66) {
                this.isKeySHIFT = false;
                edittext = _sb.toString().toUpperCase().replace("\n", "");
                textview_result.setText("");
                _sb.delete(0, _sb.length());
                onScanned(edittext);
                return true;
            } else {
                if (keyCode >= 7 && keyCode <= 16) {
                    this.handleTopNumKeys(keyCode);
                } else if (keyCode >= 29 && keyCode <= 54) {
                    this.checkShift((char) (keyCode + 68), (char) (keyCode + 36));
                } else {
                    if (keyCode < 144 || keyCode > 158) {
                        switch (keyCode) {
                            case 55:
                                this.checkShift(',', '<');
                                break;
                            case 56:
                                this.checkShift('.', '>');
                                break;
                            case 57:
                            case 58:
                            case 59:
                            case 60:
                            case 61:
                            case 63:
                            case 64:
                            case 65:
                            case 66:
                            case 67:
                            default:
                                return false;
                            case 62:
                                _sb.append(' ');
                                break;
                            case 68:
                                this.checkShift('`', '~');
                                break;
                            case 69:
                                this.checkShift('-', '_');
                                break;
                            case 70:
                                this.checkShift('=', '+');
                                break;
                            case 71:
                                this.checkShift('[', '{');
                                break;
                            case 72:
                                this.checkShift(']', '}');
                                break;
                            case 73:
                                this.checkShift('\\', '|');
                                break;
                            case 74:
                                this.checkShift(';', ':');
                                break;
                            case 75:
                                this.checkShift('\'', '"');
                                break;
                            case 76:
                                this.checkShift('/', '?');
                        }
                        return true;
                    }
                    this.handleNumPadKeys(keyCode);
                }
            }
            return super.onKeyDown(keyCode, event);
        }
    }

    private void onScanned(String edittext) {
        if (work_type != null && work_type.equals("defect")) {
            if (edittext.length() < 3) {     //���˰�ť
                defectfail = true;
                textview_result.setText("FAILɨ��");
                textview_result.setVisibility(View.VISIBLE);
                textview_result.setTextColor(Color.BLUE);
                check = String.valueOf(System.currentTimeMillis());
            }
        }
        if (edittext.length() > 2 && !check.equals(edittext)) {
            check = edittext;
            switch (status) {
                case MAINSCAN:
                    CheckMainScanNumber(edittext);
                    break;
                case CHILDSCAN:
                    if (isSnBinding) {     //��Ű�
                        if (mainCode.equals(edittext.toUpperCase())) {     //ɨ��������븸����һ��
                            textview_result.setText("��һ�������뵱ǰɨ��������ظ�:" + mainCode);
                            textview_result.setTextColor(Color.RED);
                            App.Current.playSound(R.raw.hook);
                        } else {
                            childNumber.add(edittext.toUpperCase());
                            scanString.add(edittext.toUpperCase());
                            myListAdapter.notifyDataSetChanged();
                            xml = getBindXml();
                            CommitScanNumberCreate(mainCode, "PASS");
                        }
                    } else if (isConsistencyCheck) {     //�Ƿ���һ���Լ��
                        if (scanCounts > 0) {
                            check = String.valueOf(System.currentTimeMillis());
                            if (checkCounts > scanCounts) {
                                if (scanString.contains(edittext.toUpperCase())) {
                                    App.Current.showError(ScanTestActivity.this, "ɨ�������ظ���");
                                    App.Current.playSound(R.raw.hook);
                                } else {
                                    scanString.add(edittext.toUpperCase());
                                    myListAdapter.notifyDataSetChanged();
                                    checkCounts--;
                                }
                            } else if (checkCounts <= scanCounts && checkCounts > 0) {
                                if (scanString2.contains(edittext.toUpperCase())) {
                                    App.Current.showError(ScanTestActivity.this, "ɨ�������ظ���");
                                    App.Current.playSound(R.raw.hook);
                                } else {
                                    scanString2.add(edittext.toUpperCase());
                                    myListAdapter2.notifyDataSetChanged();
                                    checkCounts--;
                                    if (checkCounts == 0) {
                                        boolean isSame = checkIsSame(scanString, scanString2);
                                        if (isSame) {            //�ύ����
                                            xml = getCheckXml();
                                            CommitScanNumberCreate(mainCode, "PASS");
                                            scanString.removeAll(scanString);
                                            scanString2.removeAll(scanString2);
                                            myListAdapter.notifyDataSetChanged();
                                            myListAdapter2.notifyDataSetChanged();
                                            status = MAINSCAN;
                                        } else {
                                            App.Current.showError(ScanTestActivity.this, "����ɨ������ݲ�һ�£�");
                                            App.Current.playSound(R.raw.hook);
                                            scanString.removeAll(scanString);
                                            scanString2.removeAll(scanString2);
                                            myListAdapter.notifyDataSetChanged();
                                            myListAdapter2.notifyDataSetChanged();
                                            status = MAINSCAN;
                                        }
                                    }
                                }
                            }
                        } else {
                            App.Current.showError(ScanTestActivity.this, "���õ�ɨ���������Ϊ0��");
                            App.Current.playSound(R.raw.hook);
                        }
                    } else {
                        if (childNumber.contains(edittext.toUpperCase())) {
                            textview_result.setText("�����ظ�ɨ��");
                        } else {
                            if (mainCode.equals(edittext.toUpperCase())) {     //ɨ��������븸����һ��
                                textview_result.setText("�������뵱ǰɨ��������ظ�:" + mainCode);
                                textview_result.setTextColor(Color.RED);
                                App.Current.playSound(R.raw.hook);
                            } else {
                                final String sql = "exec p_fm_work_check_barcode_v3 ?,?,?,?";
                                Parameters p = new Parameters().add(1, task_order_code).add(2, sequence_id).add(3, edittext).add(4, work_type);
                                String value = App.Current.DbPortal.ExecuteScalar("core_and", sql, p).Value.toString();
                                if (!value.equals("OK")) {
                                    App.Current.toastError(ScanTestActivity.this, value);
                                    break;
                                }

                                final String sql_str = "exec p_fm_work_check_barcode_for_part_and ?,?";
                                Parameters pr = new Parameters().add(1, edittext).add(2, task_order_id);
                                String value_r = App.Current.DbPortal.ExecuteScalar("core_and", sql_str, pr).Value.toString();
                                if (!value_r.equals("OK")) {
                                    App.Current.toastError(ScanTestActivity.this, value_r);
                                    break;
                                }

                                childNumber.add(edittext.toUpperCase());
                                scanString.add(edittext.toUpperCase());
                                myListAdapter.notifyDataSetChanged();
                                childScanCount--;
                                if (childScanCount > 0) {
                                    status = CHILDSCAN;
                                } else if (childScanCount == 0) {
                                    xml = getBindXml();
                                    CommitScanNumberCreate(mainCode, "PASS");
                                } else {
                                    clear();
                                }
                            }
                        }
                    }
                    break;
                case MIXED_FLOW:     //����
                    if (scanString.contains(edittext.toUpperCase())) {     //ɨ���������֮ǰ����һ��
                        textview_result.setText("����ɨ�������ظ�:" + edittext.trim());
                        textview_result.setTextColor(Color.RED);
                        App.Current.playSound(R.raw.hook);
                    } else {            //��ȡ�������ӹ���ID���ж��Ƿ���Ϲ���
                        final String sql = "exec fm_get_mixed_task_message_and ?,?";
                        Parameters p = new Parameters().add(1, old_task_order_code).add(2, scanString.size());
                        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
                            @Override
                            public void handleMessage(Message msg) {
                                Result<DataRow> value = Value;
                                if (value.HasError) {
                                    App.Current.toastError(ScanTestActivity.this, value.Error);
                                } else {
                                    if (value.Value != null) {     //�����˻����Ĺ�������

                                    } else {   //�������� ����   �ύ����
                                        StringBuffer stringBuffer = new StringBuffer();    //���lot_number ��ƴ��
                                        for (int i = 0; i < scanString.size(); i++) {
                                            stringBuffer.append(scanString.get(i) + ",");
                                        }
                                        String xml = XmlHelper.createXml("mixed_head", null, "mixed_items", "mixed_item", mixedParameter);
                                        Log.e("len", "xml:" + xml);
                                        CommitScanNumberCreate(mainCode, "PASS");
                                    }
                                }
                            }
                        });
                    }
                    break;
            }
        } else {
            textview_result.setText("�����ظ�ɨ��:" + edittext);
            textview_result.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkIsSame(ArrayList<String> scanString, ArrayList<String> scanString2) {
        if (scanString.size() != scanString2.size()) {
            return false;
        }
        Collections.sort(scanString);
        Collections.sort(scanString2);
        for (int i = 0; i < scanString.size(); i++) {
            if (!scanString.get(i).equals(scanString2.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (isHandleKey && event.getKeyCode() > 6) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                this.onKeyDown(event.getKeyCode(), event);
                return true;

            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                this.onKeyUp(event.getKeyCode(), event);
                return true;
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SeuicScannerEnabled) {
            seuicfilter.addAction(SCANACTION);
            seuicfilter.setPriority(Integer.MAX_VALUE);
            registerReceiver(seuicreceiver, seuicfilter);
        }
        if (defaultAdapter != null) {
            defaultAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
        handler.postDelayed(runnable, TIMENUMBER);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (SeuicScannerEnabled) {
            unregisterReceiver(seuicreceiver);
        }
        if (defaultAdapter != null) {
            defaultAdapter.disableForegroundDispatch(this);
        }
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (SeuicScannerEnabled) {
            seuicreceiver = null;
            seuicfilter = null;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] parcelableArrayExtra = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Ndef ndef = Ndef.get(tag);
            try {
                ndef.connect();
                NdefMessage ndefMessage = ndef.getNdefMessage();
                NdefRecord[] records = ndefMessage.getRecords();
                for (int i = 0; i < records.length; i++) {
                    String parse = parse(records[i]);
                    String code = parse;
                    if (code.contains("=")) {
                        code = parse.split("=")[1];
                    }
                    Log.e("len", "���� �� " + code);
                    onScanned(code);

                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("len", e.getLocalizedMessage());
            }

            NdefMessage[] message = null;
            if (parcelableArrayExtra != null) {
                message = new NdefMessage[parcelableArrayExtra.length];
                for (int i = 0; i < parcelableArrayExtra.length; i++) {
                    message[i] = (NdefMessage) parcelableArrayExtra[i];
                }
            }

            if (message != null) {
                NdefRecord record = message[0].getRecords()[0];
                TextRecord textRecord = TextRecord.parse(record);
                String text = textRecord.getText();
            }
        }

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            NfcB nfcbId = NfcB.get(tagFromIntent);
            if (nfcbId != null) {
                try {
                    nfcbId.connect();
                    if (nfcbId.isConnected()) {

                        byte[] protocolInfo = nfcbId.getProtocolInfo();
                        byte[] applicationData = nfcbId.getApplicationData();
                        for (int i = 0; i < protocolInfo.length; i++) {
                            Log.e("len", "Datas : " + protocolInfo[i]);
                        }
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NfcB nfcbId = NfcB.get(tagFromIntent);
            if (nfcbId != null) {
                try {
                    nfcbId.connect();
                    if (nfcbId.isConnected()) {
                        byte[] protocolInfo = nfcbId.getProtocolInfo();
                        for (int i = 0; i < protocolInfo.length; i++) {
                            Log.e("len", "Datas : " + protocolInfo[i]);
                        }
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * ���� ndefRecord �ı�����
     *
     * @param ndefRecord
     * @return
     */
    public String parse(NdefRecord ndefRecord) {
        // verify tnf   �õ�TNF��ֵ
        if (ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN) {
            return null;
        }
        // �õ��ֽ���������ж�
        if (Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
            try {
                // ���һ���ֽ���
                byte[] payload = ndefRecord.getPayload();
                // payload[0]ȡ��һ���ֽڡ� 0x80��ʮ�����ƣ����λ��1ʣ��ȫ��0��
                String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8"
                        : "UTF-16";
                // ������Ա��볤��
                int languageCodeLength = payload[0] & 0x3f;
                // ������Ա���
                String languageCode = new String(payload, 1, languageCodeLength,
                        "US-ASCII");
                //
                String text = new String(payload, languageCodeLength + 1,
                        payload.length - languageCodeLength - 1, textEncoding);

                return text;

            } catch (Exception e) {
                throw new IllegalArgumentException();
            }
        } else if (Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_URI)) {
            try {
                // ���һ���ֽ���
                byte[] payload = ndefRecord.getPayload();
                // payload[0]ȡ��һ���ֽڡ� 0x80��ʮ�����ƣ����λ��1ʣ��ȫ��0��
                String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8"
                        : "UTF-16";
                // ������Ա��볤��
                int languageCodeLength = payload[0] & 0x3f;
                // ������Ա���
                String languageCode = new String(payload, 1, languageCodeLength,
                        "US-ASCII");
                //
                String text = new String(payload, 1,
                        payload.length - 1, textEncoding);

                return text;

            } catch (Exception e) {
                throw new IllegalArgumentException();
            }
        } else {
            return null;
        }
    }

    public String toHexString(byte[] d, int s, int n) {
        final char[] ret = new char[n * 2];
        final int e = s + n;

        int x = 0;
        for (int i = s; i < e; ++i) {
            final byte v = d[i];
            ret[x++] = HEX[0x0F & (v >> 4)];
            ret[x++] = HEX[0x0F & v];
        }
        return new String(ret);
    }

    private void initScanner() {
        if (ScannerEnabled) {
            if (LianXinScannerEnabled) {
                _idata_controller = new ScannerInerface(this);
                _idata_controller.open();
                _idata_controller.setOutputMode(1);//ʹ�ù㲥ģʽ
                _idata_filter = new IntentFilter("android.intent.action.SCANRESULT");
                _idata_receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // �˴���ȡɨ������Ϣ
                        final String barcode = intent.getStringExtra("value");
                        if (barcode != null && barcode.length() > 0) {
                            onScanned(barcode);
                        }
                    }
                };
            }

            if (JieBaoScannerEnabled) {
                try {
                    JieBaoScanner = new ScannerController(this, new ScannerController.ScanListener() {
                        @Override
                        public void onScan(String result, String error) {
                            if (result != null) {
                                onScanned(result.trim());
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (SeuicScannerEnabled) {

                seuicfilter = new IntentFilter(SCANACTION);
                seuicreceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent.getAction().equals(SCANACTION)) {
                            String code = intent.getStringExtra("scannerdata").replace("\n", "");
                            onScanned(code);
                        }
                    }

                };

            }
        }
    }

    private void CheckMainScanNumber(final String text) {
        String sql = "exec p_fm_work_check_barcode_and ?,?,?,?,?";
        Parameters p = new Parameters().add(1, task_order_code)
                .add(2, split[0]).add(3, App.Current.UserID).add(4, text).add(5, "MAIN");
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataRow> value = Value;
                if (value.HasError) {
                    textview_result.setText("������:" + value.Error);
                    textview_result.setTextColor(Color.RED);
                    textview_result.setVisibility(View.VISIBLE);
                    App.Current.playSound(R.raw.hook);
//                    App.Current.showError(ScanTestActivity.this, value.Error);
                    edittext_1.setText("");
                    check = String.valueOf(System.currentTimeMillis());
//                    finish();
                    return;
                }
                if (value.Value != null) {
                    String resultd = value.Value.getValue("rtnstr", "").toString();
                    childScanCount = scanCounts;
                    if (resultd.equals("OK")) {
                        isSnBinding = false;
                        isConsistencyCheck = false;
                        mainCode = text.toUpperCase();
                        String sql = "DECLARE @rtnstr nvarchar(50)  exec p_fm_work_check_first_check ?,?,?,@rtnstr output select @rtnstr rtnstr ";
                        Parameters p = new Parameters().add(1, order_id).add(2, sequence_id).add(3, segment);
                        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
                            @Override
                            public void handleMessage(Message msg) {
                                Result<DataRow> value1 = Value;
                                if (value1.HasError) {
                                    textview_result.setText("�׼�����:" + value1.Error);
                                    textview_result.setVisibility(View.VISIBLE);
                                    textview_result.setTextColor(Color.RED);
                                    App.Current.playSound(R.raw.hook);
                                } else {
                                    String value2 = value1.Value.getValue("rtnstr", "");
                                    if (value2.toUpperCase().equals("OK")) {
                                        if (work_type.toLowerCase().equals("mixed_flow")) {         //����
                                            scanString.add(edittext.toUpperCase());
                                            myListAdapter.notifyDataSetChanged();
                                            status = MIXED_FLOW;
                                            Map<String, String> mixedParam = new HashMap<String, String>();
                                            mixedParam.put(task_order_code, edittext.trim());
                                            mixedParameter.add(mixedParam);
                                        } else {
                                            if (work_type.toLowerCase().equals("consistency_check")) {  //�����һ���Լ��
//                            checkCounts -= 1;
//                            if (!scanString.contains(text.toUpperCase())) {
//                                scanString.add(text.toUpperCase());
//                            }
//                            myListAdapter.notifyDataSetChanged();
//                            status = CHILDSCAN;
//                            isConsistencyCheck = true;
                                                if (scanCounts > 0) {
                                                    check = String.valueOf(System.currentTimeMillis());
                                                    if (checkCounts > scanCounts) {
                                                        if (scanString.contains(edittext.toUpperCase())) {
                                                            App.Current.showError(ScanTestActivity.this, "ɨ�������ظ���");
                                                            App.Current.playSound(R.raw.hook);
                                                        } else {
                                                            scanString.add(edittext.toUpperCase());
                                                            myListAdapter.notifyDataSetChanged();
                                                            checkCounts--;
                                                        }
                                                    } else if (checkCounts <= scanCounts && checkCounts > 0) {
                                                        if (scanString2.contains(edittext.toUpperCase())) {
                                                            App.Current.showError(ScanTestActivity.this, "ɨ�������ظ���");
                                                            App.Current.playSound(R.raw.hook);
                                                        } else {
                                                            scanString2.add(edittext.toUpperCase());
                                                            myListAdapter2.notifyDataSetChanged();
                                                            checkCounts--;
                                                            if (checkCounts == 0) {
                                                                boolean isSame = checkIsSame(scanString, scanString2);
                                                                if (isSame) {            //�ύ����
                                                                    xml = getCheckXml();
                                                                    CommitScanNumberCreate(mainCode, "PASS");
                                                                    scanString.removeAll(scanString);
                                                                    scanString2.removeAll(scanString2);
                                                                    myListAdapter.notifyDataSetChanged();
                                                                    myListAdapter2.notifyDataSetChanged();
                                                                    status = MAINSCAN;
                                                                } else {
                                                                    App.Current.showError(ScanTestActivity.this, "����ɨ������ݲ�һ�£�");
                                                                    App.Current.playSound(R.raw.hook);
                                                                    scanString.removeAll(scanString);
                                                                    scanString2.removeAll(scanString2);
                                                                    myListAdapter.notifyDataSetChanged();
                                                                    myListAdapter2.notifyDataSetChanged();
                                                                    checkCounts = scanCounts * 2;
                                                                    status = MAINSCAN;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    status = MAINSCAN;
                                                } else {
                                                    App.Current.showError(ScanTestActivity.this, "���õ�ɨ���������Ϊ0��");
                                                    App.Current.playSound(R.raw.hook);
                                                }


                                            } else {
                                                if (scanCounts == 0) {
                                                    if (work_type.equals("defect")) {   //ȱ�ݵǼ�
                                                        textview_result.setText("ȱ�ݵǼǣ�" + text);
                                                        textview_result.setTextColor(getResources().getColor(R.color.megmeet_green));
                                                        textview_result.setVisibility(View.VISIBLE);
                                                        //����һ�����棬ѡ���ɫ������ɫ�ĵƣ���ɫ��վ����ɫѡ�������
//                                                    status = PASS;
                                                        if (defectfail) {      //ȱ�ݵǼ� FAIL
                                                            toastChooseResult();
                                                        } else {               //ȱ�ݵǼ� PASS
                                                            xml = getBindXml();
                                                            CommitScanNumberCreate(mainCode, "PASS");
                                                        }
                                                    } else if (work_type.toLowerCase().equals("sn_binding")) {      //��Ű�
                                                        status = CHILDSCAN;
                                                        isSnBinding = true;
                                                        scanString.add(text.toUpperCase());
                                                        myListAdapter.notifyDataSetChanged();
                                                    } else if (work_type.toLowerCase().equals("sampling_test")) {
                                                        toastChooseResult();      //ѡ����ԭ��
                                                    } else {
                                                        xml = "";
                                                        CommitScanNumberCreate(text.toUpperCase(), "PASS");
                                                    }
                                                } else if (scanCounts > 0) {
                                                    scanString.add(text.toUpperCase());
                                                    myListAdapter.notifyDataSetChanged();
                                                    status = CHILDSCAN;
                                                }
                                            }
                                        }

                                    } else {
                                        App.Current.playSound(R.raw.hook);
                                        textview_result.setText(value2);
                                        textview_result.setTextColor(Color.RED);
                                        textview_result.setVisibility(View.VISIBLE);
                                        edittext_1.setText("");
                                    }
                                }
                            }
                        });


                    } else {
                        App.Current.playSound(R.raw.hook);
                        textview_result.setText("ɨ�����뷵�س���" + resultd);
                        textview_result.setTextColor(Color.RED);
                        textview_result.setVisibility(View.VISIBLE);
                        edittext_1.setText("");
                        clear();
                        return;
//                        finish();
                    }
                } else {
                    App.Current.playSound(R.raw.hook);
                    textview_result.setText("�ύ�Ķ�ά������");
                    textview_result.setTextColor(Color.RED);
                    textview_result.setVisibility(View.VISIBLE);
                    edittext_1.setText("");
                    clear();
                }
                edittext_1.setText("");
            }
        });
    }

//    private void popupCheckWindow() {
//        PopupWindow popupWindow = new PopupWindow();
//        WindowManager wm = (WindowManager) ScanTestActivity.this.getSystemService(Context.WINDOW_SERVICE);
//        DisplayMetrics dm = new DisplayMetrics();
//        wm.getDefaultDisplay().getMetrics(dm);
//
//        View popupView = View.inflate(ScanTestActivity.this, R.layout.scan_check_popupwindow, null);
//        initCheckPopupView(popupView, popupWindow);
//        popupWindow.setContentView(popupView);
//        Display defaultDisplay = getWindowManager().getDefaultDisplay();
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        defaultDisplay.getMetrics(displayMetrics);
//        popupWindow.setWidth(displayMetrics.widthPixels / 2);
//        popupWindow.setHeight(displayMetrics.heightPixels * 3 / 4);
//        popupWindow.setFocusable(true);
////                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.style_divider_color));
//        popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 20, 30);
//    }

//    private void initCheckPopupView(final View popupView, final PopupWindow popupWindow) {
//        TextView textViewNarrow = (TextView) popupView.findViewById(R.id.textview_narrow);
//        TextView textViewWide = (TextView) popupView.findViewById(R.id.textview_wide);
//        TextView textViewRadius = (TextView) popupView.findViewById(R.id.textview_radius);
//        LinearLayout linearLayoutFlat = (LinearLayout) popupView.findViewById(R.id.linearlayout_flat);     //����
//        final TextCell textCellNarrow = (TextCell) popupView.findViewById(R.id.text_cell_narrow);
//        final TextCell textCellWide = (TextCell) popupView.findViewById(R.id.text_cell_wide);
//        LinearLayout linearLayoutCircle = (LinearLayout) popupView.findViewById(R.id.linearlayout_circle); //Բ��
//        final TextCell textCellRadius = (TextCell) popupView.findViewById(R.id.text_cell_radius);
//
//        final TextCell textCell1 = (TextCell) popupView.findViewById(R.id.text_cell_1);
//        final TextCell textCell2 = (TextCell) popupView.findViewById(R.id.text_cell_2);
//        final TextCell textCell3 = (TextCell) popupView.findViewById(R.id.text_cell_3);
//        final ButtonTextCell buttonTextCell1 = (ButtonTextCell) popupView.findViewById(R.id.button_text_cell_1);
//        final TextCell textCell4 = (TextCell) popupView.findViewById(R.id.text_cell_4);
//        TextView cancel = (TextView) popupView.findViewById(R.id.cancel);
//        TextView confirm = (TextView) popupView.findViewById(R.id.confirm);
//
//        if (textCell1 != null) {
//            textCell1.setLabelText("��ˮ���");
//        }
//        if (textCell2 != null) {
//            textCell2.setLabelText("ˮ����ѹ");
//        }
//        if (textCell3 != null) {
//            textCell3.setLabelText("��������");
//        }
//        if (textCell4 != null) {
//            textCell4.setLabelText("��ע");
//        }
//        if (buttonTextCell1 != null) {
//            buttonTextCell1.setLabelText("�ж����");
//            buttonTextCell1.Button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    chooseOKNG(buttonTextCell1);
//                }
//            });
//        }
//        if (product_type.contains("����")) {
//            linearLayoutFlat.setVisibility(View.VISIBLE);
//            linearLayoutCircle.setVisibility(View.GONE);
//            textViewNarrow.setText(String.valueOf(width_value));
//            textViewWide.setText(String.valueOf(length_value));
//            if (textCellNarrow != null) {
//                textCellNarrow.setLabelText("խ��");
//            }
//            if (textCellWide != null) {
//                textCellWide.setLabelText("���");
//            }
//        } else {
//            linearLayoutFlat.setVisibility(View.GONE);
//            linearLayoutCircle.setVisibility(View.VISIBLE);
//            textViewRadius.setText(String.valueOf(diameter_value));
//            if (textCellRadius != null) {
//                textCellRadius.setLabelText("����⾶mm");
//            }
//        }
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                textview_result.setText("");
//                textview_result.setVisibility(View.INVISIBLE);
//                check = SystemClock.currentThreadTimeMillis() + "";
//                popupWindow.dismiss();
//            }
//        });
//
//        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Pattern p = Pattern.compile("^(\\-|\\+)?\\d+(\\.\\d+)?$");
//                if (product_type.contains("����")) {
//                    Matcher m1 = p.matcher(textCellNarrow.getContentText());
//                    Matcher m2 = p.matcher(textCellWide.getContentText());
//                    if (TextUtils.isEmpty(textCellNarrow.getContentText())) {
//                        App.Current.toastError(ScanTestActivity.this, "��������խ������");
//                        return;
//                    } else if (TextUtils.isEmpty(textCellWide.getContentText())) {
//                        App.Current.toastError(ScanTestActivity.this, "��������������");
//                        return;
//                    } else if (!m1.matches()) {
//                        App.Current.toastError(ScanTestActivity.this, "խ�����ݸ�ʽ����");
//                        return;
//                    } else if (!m2.matches()) {
//                        App.Current.toastError(ScanTestActivity.this, "������ݸ�ʽ����");
//                        return;
//                    }
//                } else {
//                    Matcher m3 = p.matcher(textCellRadius.getContentText());
//                    if (TextUtils.isEmpty(textCellRadius.getContentText())) {
//                        App.Current.toastError(ScanTestActivity.this, "���������⾶����");
//                        return;
//                    } else if (!m3.matches()) {
//                        App.Current.toastError(ScanTestActivity.this, "�⾶���ݸ�ʽ����");
//                        return;
//                    }
//                }
//                if ("NG".equals(buttonTextCell1.getContentText())) {
//                    if (TextUtils.isEmpty(textCell3.getContentText())) {
//                        App.Current.toastError(ScanTestActivity.this, "�����벻��ԭ��");
//                        return;
//                    }
//                }
//                if (TextUtils.isEmpty(textCell1.getContentText())) {
//                    App.Current.toastError(ScanTestActivity.this, "����������ˮ���");
//                } else if (TextUtils.isEmpty(textCell2.getContentText())) {
//                    App.Current.toastError(ScanTestActivity.this, "��������ˮ����ѹ");
//                } else {
//                    //ƴ�����ݣ��ύ
//                    ArrayList<Map<String, String>> exceptions = new ArrayList<>();
//                    HashMap<String, String> exception = new HashMap<>();
//                    exception.put("sn_no", mainCode);
//                    exception.put("sn_type", work_type);
//                    exception.put("width_value", textCellNarrow.getContentText().trim());
//                    exception.put("length_value", textCellWide.getContentText().trim());
//                    exception.put("diameter_value", textCellRadius.getContentText().trim());
//                    exception.put("salt_water_pinhole", textCell1.getContentText());
//                    exception.put("water_pressure_resistance", textCell2.getContentText());
//                    exception.put("ng_content", textCell3.getContentText());
//                    exception.put("ng_result", buttonTextCell1.getContentText());
//                    exception.put("comment", textCell4.getContentText());
//                    exceptions.add(exception);
//                    //"bindings", head_entry, "bindings", "binding", item_entries)
//                    xml = XmlHelper.createXml("bindings", null, "bindings", "binding", exceptions);
////                xml = getTestBindXml(buttonTextCell.getContentText(), editText.getText().toString());
//                    check = String.valueOf(System.currentTimeMillis());
//                    if ("OK".equals(buttonTextCell1.getContentText())) {
//                        CommitScanNumberCreate(mainCode, "PASS");
//                    } else {
//                        CommitScanNumberCreate(mainCode, "FAIL");
//                    }
////                    CommitScanNumberCreate(mainCode, buttonTextCell1.getContentText());
//                    popupWindow.dismiss();
//                }
//            }
//        });
//    }

    private void chooseOKNG(final ButtonTextCell buttonTextCell) {
        final ArrayList<String> result = new ArrayList<String>();
        result.add("OK");
        result.add("NG");

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    String time = result.get(which);
                    buttonTextCell.setContentText(time);
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(ScanTestActivity.this).setTitle("��ѡ��")
                .setSingleChoiceItems(result.toArray(new String[0]), result.indexOf(buttonTextCell.getContentText()), listener)
                .setNegativeButton("ȡ��", null).show();
    }

    private void setAnimation(ImageView imageViewGreen, ImageView imageViewRed) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.1f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setRepeatMode(Animation.RESTART);
        alphaAnimation.setRepeatCount(AlphaAnimation.INFINITE);
//        AlphaAnimation alphaAnimationa = new AlphaAnimation(0.1f, 1f);
//        alphaAnimationa.setDuration(1000);
//        alphaAnimationa.setRepeatMode(Animation.RESTART);
//        alphaAnimationa.setRepeatCount(AlphaAnimation.INFINITE);
        imageViewGreen.setAnimation(alphaAnimation);
        imageViewRed.setAnimation(alphaAnimation);

    }

    private void toastChooseResult() {
        String sql = "SELECT code + bad_type name\n" +
                "FROM fm_bad_apperance\n" +
                "where sequence_names like + '%' + (select name from fm_eng_sequence where sequence_id = ?) + '%'";
        Parameters p = new Parameters().add(1, sequence_id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(ScanTestActivity.this, value.Error);
                    return;
                }
                if (value.Value != null) {
                    ArrayList<String> names = new ArrayList<String>();
                    for (DataRow row : value.Value.Rows) {
                        String name = row.getValue("name", "");
                        names.add(name);
                    }


                    WindowManager wm = (WindowManager) ScanTestActivity.this.getSystemService(Context.WINDOW_SERVICE);
                    DisplayMetrics dm = new DisplayMetrics();
                    wm.getDefaultDisplay().getMetrics(dm);
                    // ��Ļ��ȣ����أ�
                    width = dm.widthPixels;

                    View popupView = View.inflate(ScanTestActivity.this, R.layout.scan_popupwindow, null);
                    initPopupView(popupView, names);
                    popupWindow = new PopupWindow();
                    popupWindow.setContentView(popupView);
                    popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                    popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    popupWindow.setFocusable(true);
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
//                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.style_divider_color));
                    popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 20, 30);
                }
            }
        });
    }

    private void initPopupView(final View popupView, final ArrayList<String> names) {
        final ButtonTextCell buttonTextCell = (ButtonTextCell) popupView.findViewById(R.id.button_text_cell);
        final EditText editText = (EditText) popupView.findViewById(R.id.edittext);
        LinearLayout linearLayout = (LinearLayout) popupView.findViewById(R.id.linearlayout);
        final TextView textViewChange = (TextView) popupView.findViewById(R.id.textview_change);
        TextView confirm = (TextView) popupView.findViewById(R.id.confirm);
        final TextView add = (TextView) popupView.findViewById(R.id.add);
        TextView cancel = (TextView) popupView.findViewById(R.id.cancel);
        buttonTextCell.Label.setTextColor(Color.BLACK);
        final ListView listView = (ListView) popupView.findViewById(R.id.listview);
        addmoreAdapter = new AddmoreAdapter();
        listView.setAdapter(addmoreAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                exceptionAll.remove(i);
                addmoreAdapter.notifyDataSetChanged();
                return false;
            }
        });

        //����mllExpand��parent layout��RelativeLayout�������Ҫ����RelativeLayout.LayoutParams����
        LinearLayout.LayoutParams lpExpand = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        //��ȡpopup window�Ŀ����Ҫ�Ȼ�ȡcontent view��Ȼ���ٻ�ȡ���
        lpExpand.width = width / 2;
        linearLayout.setLayoutParams(lpExpand);

        if (buttonTextCell != null) {
            buttonTextCell.setLabelText("�������");
            buttonTextCell.Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(ScanTestActivity.this).setTitle("��ѡ��")
                            .setItems(names.toArray(new String[0]), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int position) {
                                    buttonTextCell.setContentText(names.get(position));
                                }
                            })
                            .setNegativeButton("ȡ��", null).show();
                }
            });
        }
//��۲���ɨ��
        buttonTextCell.TextBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    badString = buttonTextCell.TextBox.getText().toString().replace("\n", "");
                    buttonTextCell.TextBox.setText("");
                    String sql = "select top 1 * from fm_bad_apperance where code = ?";
                    Parameters p = new Parameters().add(1, badString);
                    App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
                        @Override
                        public void handleMessage(Message msg) {
                            Result<DataRow> value = Value;
                            if (value.HasError) {
                                App.Current.toastError(ScanTestActivity.this, value.Error);
                                App.Current.playSound(R.raw.error);
                                return;
                            }
                            if (value.Value != null) {
                                String bad_type = value.Value.getValue("bad_type", "");
                                String code = value.Value.getValue("code", "");
                                buttonTextCell.TextBox.setText(code + "-" + bad_type);
                                textViewChange.setText("��������");
                            } else {
                                App.Current.toastError(ScanTestActivity.this, "ɨ�����");
                                App.Current.playSound(R.raw.error);
                            }
                        }
                    });
                }
                return false;
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExceptionBean exceptionBean = new ExceptionBean();
                exceptionBean.setWh_f(editText.getText().toString());
                exceptionBean.setExceptionType(buttonTextCell.getContentText());
                exceptionAll.add(exceptionBean);
                buttonTextCell.setContentText("");
                editText.setText("");
                buttonTextCell.TextBox.requestFocus();
                buttonTextCell.TextBox.setFocusable(true);
                addmoreAdapter.notifyDataSetChanged();
                check = String.valueOf(System.currentTimeMillis());
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                    App.Current.showInfo(ScanTestActivity.this, "�����벻�����");
//                } else if (TextUtils.isEmpty(editText.getText().toString())) {
//                    App.Current.showInfo(ScanTestActivity.this, "�����벻������");
//                } else {
//                    xml = getTestBindXml(buttonTextCell.getContentText(), editText.getText().toString());
//                    check = edittext + "a";
//                    CommitScanNumberCreate(mainCode, "FAIL");
//                    popupWindow.dismiss();
                Map<String, String> exception;
                ArrayList<Map<String, String>> exceptions = new ArrayList<Map<String, String>>();
                if (!TextUtils.isEmpty(buttonTextCell.getContentText())) {            //���������һ������
                    exception = new HashMap<String, String>();
                    exception.put("ng_reason", buttonTextCell.getContentText().trim());
                    exception.put("ng_remark", editText.getText().toString().trim());
                    exception.put("sn_no", mainCode);
                    exception.put("sn_type", work_type);
                    exceptions.add(exception);
                }
                for (int i = 0; i < exceptionAll.size(); i++) {
                    exception = new HashMap<String, String>();
                    exception.put("ng_reason", exceptionAll.get(i).getExceptionType());
                    exception.put("ng_remark", exceptionAll.get(i).getWh_f());
                    exception.put("sn_no", mainCode);
                    exception.put("sn_type", work_type);
                    exceptions.add(exception);
                }
                //"bindings", head_entry, "bindings", "binding", item_entries)
                xml = XmlHelper.createXml("bindings", null, "bindings", "binding", exceptions);
//                xml = getTestBindXml(buttonTextCell.getContentText(), editText.getText().toString());
                check = String.valueOf(System.currentTimeMillis());
                CommitScanNumberCreate(mainCode, "FAIL");
                popupWindow.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check = String.valueOf(System.currentTimeMillis());
                exceptionAll.removeAll(exceptionAll);
                addmoreAdapter.notifyDataSetChanged();
                popupWindow.dismiss();
                textview_result.setText("");
                defectfail = false;
            }
        });
    }

    private void CommitScanNumberCreate(final String txt, final String rslt) {
        //���ж��Ƿ��Ǹ��Ӽ��󶨣��ж��Ƿ���������Ҫ�ϴ����ݵ���ƽ̨���ǵĻ����ϴ�
        if (childNumber.size() > 0) {
            String sqlCloud = "exec fm_upload_cloud_and ?,?";
            Parameters pCloud = new Parameters().add(1, process_id).add(2, sequence_id);
            App.Current.DbPortal.ExecuteRecordAsync("core_and", sqlCloud, pCloud, new ResultHandler<DataRow>() {
                @Override
                public void handleMessage(Message msg) {
                    final Result<DataRow> value = Value;
                    if (value.HasError) {
                        textview_result.setText(value.Error);
                        textview_result.setTextColor(Color.RED);
                        textview_result.setVisibility(View.VISIBLE);
                        App.Current.playSound(R.raw.hook);
                        clear();
                    } else {
                        boolean upload_cloud = value.Value.getValue("upload_cloud", false);
                        final String product_id = value.Value.getValue("product_id", "");
                        //�豸����
                        final String equipment_name = value.Value.getValue("equipment_name", "");
                        if (upload_cloud) {
                            //ͨ��SN����MAC��ַ���ٰ�MAC��ַ�ϴ���ƽ̨
                            String sql = "exec fm_search_mac_by_sn_and ?";
                            Parameters p = new Parameters().add(1, childNumber.get(0));
                            App.Current.DbPortal.ExecuteRecordAsync("core_zhuzhou_and", sql, p, new ResultHandler<DataRow>() {
                                @Override
                                public void handleMessage(Message msg) {
                                    Result<DataRow> value1 = Value;
                                    if (value1.HasError) {
                                        textview_result.setText(value1.Error);
                                        textview_result.setTextColor(Color.RED);
                                        textview_result.setVisibility(View.VISIBLE);
                                        App.Current.playSound(R.raw.hook);
                                        clear();
                                    } else {
                                        if (value1.Value != null) {
                                            mac_address = value1.Value.getValue("mac_number", "");
                                            if (mac_address.length() == 12) {
                                                //�ϴ����ݵ���ƽ̨
                                                uploadCloud(txt, rslt, product_id, equipment_name, mac_address);
                                            } else {
                                                textview_result.setVisibility(View.VISIBLE);
                                                textview_result.setText("��ѯ���Ĳ���MAC��ַ��" + mac_address);
                                                textview_result.setTextColor(Color.RED);
                                                App.Current.playSound(R.raw.hook);
                                                clear();
                                            }
                                        } else {
                                            textview_result.setText("�Ҳ����󶨵�MAC��ַ");
                                            textview_result.setTextColor(Color.RED);
                                            textview_result.setVisibility(View.VISIBLE);
                                            App.Current.playSound(R.raw.hook);
                                            clear();
                                        }
                                    }
                                }
                            });
                        } else {
                            insertIntoSQL(txt, rslt);
                        }
                    }
                }
            });
        } else {
            insertIntoSQL(txt, rslt);
        }
    }

    private void uploadCloud(final String mainCode, final String result, String product_id, String equipment_name, final String mac_number) {
        Retrofit uploadCloudRetrofit = RetrofitDownUtil.getInstence().getUploadCloudRetrofit();
        IotBean info = new IotBean(mac_number, mainCode, TextUtils.isEmpty(equipment_name) ? "SmartBidet" : equipment_name, "", childNumber);  /*** ����Gson ������תjson�ַ���*/
        Gson gson = new Gson();
        String obj = gson.toJson(info);
        IotServiceApi iotServiceApi = uploadCloudRetrofit.create(IotServiceApi.class);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), obj);
        Log.e("len", "��Ʒ��" + product_id);
        Call<ResponseBody> message = iotServiceApi.addDeviceToCloud(product_id, body);
//        Call<ResponseBody> message = iotServiceApi.addDeviceToCloud(product_id);
        message.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                try {
                    if (response.errorBody() == null) {
                        if (response.code() == 200) {
                            String string = null;
                            string = response.body().string();
                            Gson gson = new Gson();
                            BackSuccessBean backBodyBean = gson.fromJson(string, BackSuccessBean.class);
                            textview_result.setVisibility(View.VISIBLE);
                            textview_result.setText("�ύ�ɹ���" + backBodyBean.getMac());
                            App.Current.playSound(R.raw.pass);
                            edittext_1.setText("");
                            xml = getCloudXML(mac_number);
                            Log.e("len", "XML:" + xml);
                            insertIntoSQL(mainCode, result);
                        } else {
                            textview_result.setVisibility(View.VISIBLE);
                            textview_result.setText("����" + response.code());
                            textview_result.setTextColor(Color.RED);
                            App.Current.playSound(R.raw.hook);
                            edittext_1.setText("");
                            clear();
                        }
                    } else {
                        textview_result.setVisibility(View.VISIBLE);
                        textview_result.setText(response.errorBody().string());
                        textview_result.setTextColor(Color.RED);
                        App.Current.playSound(R.raw.hook);
                        edittext_1.setText("");
                        clear();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                textview_result.setVisibility(View.VISIBLE);
                textview_result.setText(throwable.getMessage());
                textview_result.setTextColor(Color.RED);
                App.Current.playSound(R.raw.hook);
                edittext_1.setText("");
                clear();
            }
        });
//        String baseUrl = "http://api-iot.megmeet.com";
////        String baseUrl = "http://manager-iot.megmeet.com";
//        HttpUrl httpUrl = HttpUrl.parse(baseUrl);
//        Retrofit build = new Retrofit.Builder()
//                .addConverterFactory(GsonConverterFactory.create())
//                .baseUrl(httpUrl)
//                .build();
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), obj);
//        IotServiceApi iotServiceApi = build.create(IotServiceApi.class);
//        retrofit2.Call<ResponseBody> data = iotServiceApi.getMessage(body);
//        data.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    Log.e("len", response.isSuccessful() + "SUCCESS:" + response.errorBody().string());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e("len", call.toString() + t.getMessage());
//            }
//        });
    }

    private String getCloudXML(String macNumber) {  //��ȡ�ύ��ƽ̨���ݵ�xml
        Map<String, String> head_entry = new HashMap<>();
        ArrayList<Map<String, String>> item_entries = new ArrayList<>();
        Map<String, String> item_entry = new HashMap<>();
        Log.e("len", "Chile:" + childNumber.size());
        for (int i = 0; i < childNumber.size(); i++) {
            item_entry.put("sn_no", macNumber);
            item_entry.put("sn_no_mac_father", childNumber.get(i));
            item_entry.put("sn_type", work_type);
            item_entries.add(item_entry);
        }
        String xml = XmlHelper.createXml("bindings", head_entry, "bindings", "binding", item_entries);
        return xml;
    }


    public void insertIntoSQL(String txt, String rslt) {
        Time time = new Time();
        time.setToNow();
        int hour = time.hour;
        int toSeqId = getToSeqId(rslt);
        if (toSeqId > 0) {
            String sql = "exec p_fm_work_create_and_v1 ?,?,?,?,?,?,?,?,?,?,?,?,?,?";
            Parameters p = new Parameters().add(1, order_id).add(2, sequence_id).add(3, org_code).add(4, segment)
                    .add(5, station).add(6, hour > 18 ? "ҹ��" : "�װ�").add(7, device).add(8, foreman_id).add(9, worker_id)
                    .add(10, txt).add(11, toSeqId).add(12, rslt).add(13, App.Current.UserID).add(14, xml);
            Log.e("len", order_id + "," + sequence_id + "," + org_code + "," + segment + "," + station + "," + device + "," + foreman_id + "," +
                    worker_id + "," + txt + "," + toSeqId + "," + rslt + "," + xml);
            App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
                @Override
                public void handleMessage(Message msg) {
                    Result<DataRow> result = Value;
                    if (result.HasError) {
                        textview_result.setText(result.Error);
                        textview_result.setTextColor(Color.RED);
                        textview_result.setVisibility(View.VISIBLE);
                        App.Current.playSound(R.raw.hook);
                        clear();
                        return;
                    }
                    if (result.Value != null) {
                        resultId = Integer.parseInt(result.Value.getValue(0).toString());
                        if (resultId > 0) {
                            textview_result.setVisibility(View.VISIBLE);
                            textview_result.setText("�ύ�ɹ�");
                            textview_result.setTextColor(Color.BLUE);
                            App.Current.playSound(R.raw.pass);
                            edittext_1.setText("");
                            int seq_id = sharedPreferences.getInt("seq_id", 0);
                            getCurCounts(textview_1, "to_seq_id", 83);
                            getCurCounts(textview_2, "sequence_id", seq_id);
                            status = MAINSCAN;
                            check = "SUCCESS";
                        } else {
                            textview_result.setVisibility(View.VISIBLE);
                            textview_result.setText("�ύʧ��");
                            textview_result.setTextColor(Color.BLUE);
                        }
                    }
                    clear();
                    defectfail = false;
                }
            });
        }
        checkCounts = scanCounts * 2;
    }

    private void clear() {
        if (childNumber != null) {
            childNumber.clear();
        }
        if (scanString != null) {
            scanString.clear();
        }
        if (scanString2 != null) {
            scanString2.clear();
        }
        myListAdapter.notifyDataSetChanged();
        if (myListAdapter2 != null) {
            myListAdapter2.notifyDataSetChanged();
        }
        edittext_1.setText("");
        check = String.valueOf(System.currentTimeMillis());
        status = MAINSCAN;
    }

    private String getCheckXml() {                      //����һ���Լ��
        Map<String, String> head_entry = new HashMap<String, String>();
        ArrayList<Map<String, String>> item_entries = new ArrayList<Map<String, String>>();
        Map<String, String> item_entry = null;
        for (int i = 0; i < scanString.size(); i++) {
            item_entry = new HashMap<String, String>();
            item_entry.put("sn_no", scanString.get(i));
            item_entry.put("sn_type", work_type);
            item_entries.add(item_entry);
        }
        String xml = XmlHelper.createXml("bindings", head_entry, "bindings", "binding", item_entries);
        return xml;
    }

    private String getBindXml() {                       //��ȡ�ύ���ݵ�xml
        Map<String, String> head_entry = new HashMap<String, String>();
        ArrayList<Map<String, String>> item_entries = new ArrayList<Map<String, String>>();
        Map<String, String> item_entry = null;
        for (int i = 0; i < childNumber.size(); i++) {
            item_entry = new HashMap<String, String>();
            item_entry.put("sn_no", childNumber.get(i));
            item_entry.put("sn_type", work_type);
            item_entries.add(item_entry);
        }
        String xml = XmlHelper.createXml("bindings", head_entry, "bindings", "binding", item_entries);
        return xml;
    }

    private String getTestBindXml(String contentText, String s) {  //work_typeΪ����ύ��xml
        Map<String, String> head_entry = new HashMap<String, String>();
        ArrayList<Map<String, String>> item_entries = new ArrayList<Map<String, String>>();
        Map<String, String> item_entry = new HashMap<String, String>();
        item_entry.put("sn_no", mainCode);
        item_entry.put("sn_type", work_type);
        item_entry.put("ng_reason", contentText);
        item_entry.put("ng_remark", s);
        item_entries.add(item_entry);
        String xml = XmlHelper.createXml("bindings", head_entry, "bindings", "binding", item_entries);
        return xml;
    }

    private void getCurCounts(final TextView text, String id_name, int seq_id) {
        String task_order = sharedPreferences.getString("task_order", "");
        String task_sql = "exec fm_get_scan_count ?,?,?";
        Parameters task_p = new Parameters().add(1, task_order).add(2, id_name).add(3, seq_id);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", task_sql, task_p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(ScanTestActivity.this, value.Error);
                    return;
                }
                if (value.Value != null) {
                    text.setText(value.Value.getValue("completed_counts", 0).toString());
                }
            }
        });
    }

    private void getForeManId(String code) {
        String sql = "select * from fm_worker where code = ?";
        Parameters p = new Parameters().add(1, code);
        Result<DataRow> result = App.Current.DbPortal.ExecuteRecord("core_and", sql, p);
        if (result.HasError) {
            textview_result.setText(result.Error);
            textview_result.setTextColor(Color.RED);
            textview_result.setVisibility(View.VISIBLE);
            return;
        }

        if (result.Value != null) {
            foreman_id = result.Value.getValue("id", 0);
            edit.putInt("foreman_id", foreman_id);
            edit.commit();
        }
    }

    private void getWorkerId(String code) {
        String sql = "select * from fm_worker where code = ?";
        Parameters p = new Parameters().add(1, code);
        Result<DataRow> result = App.Current.DbPortal.ExecuteRecord("core_and", sql, p);
        if (result.HasError) {
            textview_result.setText(result.Error);
            textview_result.setTextColor(Color.RED);
            textview_result.setVisibility(View.VISIBLE);
            return;
        }
        if (result.Value != null) {
            worker_id = result.Value.getValue("id", 0);
        }
    }

    private int getToSeqId(String rslt) {
        String sql = "exec p_getToSeqId ?,?,?";
        Parameters p = new Parameters().add(1, rslt).add(2, sequence_id).add(3, process_id);
        Result<DataRow> result = App.Current.DbPortal.ExecuteRecord("core_and", sql, p);
        if (result.HasError) {
            App.Current.showError(ScanTestActivity.this, result.Error);
            return -2;
        }
        if (result.Value == null) {
            App.Current.showInfo(ScanTestActivity.this, "��ɨ��PASS����FAIL");
            return -3;
        } else {
            return result.Value.getValue("to_seq_id", 0);
        }
    }

    private void initScanNumber() {
        String sql = "exec p_qm_sop_get_scan_number ?,?";
        Parameters p = new Parameters().add(1, sharedPreferences.getString("task_order", "")).add(2, split[0]);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    textview_result.setText(value.Error);
                    textview_result.setTextColor(Color.RED);
                    textview_result.setVisibility(View.VISIBLE);
                    return;
                }
                if (value.Value != null) {
                    scanCounts = value.Value.getValue("partcount", -1);
                    order_id = value.Value.getValue("id", 0L);
                    sequence_id = value.Value.getValue("sequence_id", 0);
                    process_id = value.Value.getValue("process_id", 0);
                    work_type = value.Value.getValue("work_type", "");
                    if (work_type.equals("consistency_check")) {
                        checkCounts = scanCounts * 2;
                        myListAdapter2 = new MyListAdapter2();
                        listview_2.setAdapter(myListAdapter2);
                        listview_2.setVisibility(View.VISIBLE);
                        WindowManager m = getWindowManager();
                        Display d = m.getDefaultDisplay();  //Ϊ��ȡ��Ļ����
                        WindowManager.LayoutParams p = getWindow().getAttributes();  //��ȡ�Ի���ǰ�Ĳ���ֵ
                        p.height = (int) (d.getHeight() * 0.8);   //�߶�����Ϊ��Ļ��1.0
                        p.width = (int) (d.getWidth() * 1);    //�������Ϊ��Ļ��0.8
//                        p.height = (int) (d.getHeight());   //�߶�����Ϊ��Ļ��1.0
//                        p.width = (int) (d.getWidth());    //�������Ϊ��Ļ��0.8
//        p.alpha = 1.0f;      //���ñ���͸����
//        p.dimAmount = 0.0f;      //���úڰ���

                        getWindow().setAttributes(p);     //������Ч
                        getWindow().setGravity(Gravity.RIGHT | Gravity.BOTTOM);       //���ÿ��Ҷ���

                        android.widget.RelativeLayout.LayoutParams linearParams = (android.widget.RelativeLayout.LayoutParams) linearlayout_scantest.getLayoutParams(); //ȡ�ؼ�textView��ǰ�Ĳ��ֲ��� linearParams.height = 20;// �ؼ��ĸ�ǿ�����20
                        linearParams.height = p.height;// �ؼ��Ŀ�ǿ�����30
                        linearParams.width = p.width;// �ؼ��Ŀ�ǿ�����30

                        linearlayout_scantest.setLayoutParams(linearParams);
                    }
                }
            }
        });
    }

    private void initData() {
        myListAdapter = new MyListAdapter();
        listview_1.setAdapter(myListAdapter);
    }

    private String getFormatTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        InputMethodManager systemService = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        systemService.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        return true;
    }

    class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return scanString.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = View.inflate(getBaseContext(), R.layout.scantext_list_item, null);
                viewHolder = new ViewHolder();
//                viewHolder.textcell_1 = (TextCell) view.findViewById(R.id.textcell_1);
                viewHolder.textView = (TextView) view.findViewById(R.id.textview);
                viewHolder.textView_content = (TextView) view.findViewById(R.id.textview_content);
//                viewHolder.textcell_1.setReadOnly();
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            if (work_type.toLowerCase().equals("consistency_check")) {
                viewHolder.textView.setText((position + 1) + ":");
            } else {
                if (position == 0) {
                    viewHolder.textView.setText("��");
                } else {
                    if (work_type.toLowerCase().equals("sn_binding")) {
                        viewHolder.textView.setText("��");
                    } else {
                        viewHolder.textView.setText("��");
                    }
                }
            }


            viewHolder.textView_content.setText(scanString.get(position));
            viewHolder.textView_content.setSingleLine();
            return view;
        }
    }

    class ViewHolder {
        //        private TextCell textcell_1;
        private TextView textView;
        private TextView textView_content;
    }

    class MyListAdapter2 extends BaseAdapter {

        @Override
        public int getCount() {
            return scanString2.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = View.inflate(getBaseContext(), R.layout.scantext_list_item, null);
                viewHolder = new ViewHolder();
//                viewHolder.textcell_1 = (TextCell) view.findViewById(R.id.textcell_1);
                viewHolder.textView = (TextView) view.findViewById(R.id.textview);
                viewHolder.textView_content = (TextView) view.findViewById(R.id.textview_content);
//                viewHolder.textcell_1.setReadOnly();
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.textView.setText((position + 1) + ":");
            viewHolder.textView_content.setText(scanString2.get(position));
            viewHolder.textView_content.setSingleLine();
            return view;
        }
    }

    class AddmoreAdapter extends BaseAdapter {    //ȱ�ݵǼ���Ӹ����������

        @Override
        public int getCount() {
            return exceptionAll == null ? 0 : exceptionAll.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            AddmoreViewHolder viewHolder;
            if (view == null) {
                view = View.inflate(ScanTestActivity.this, R.layout.item_addmore_scan, null);
                viewHolder = new AddmoreViewHolder();
                viewHolder.textViewWh = (TextView) view.findViewById(R.id.text_wh);
                viewHolder.textViewExceptionType = (TextView) view.findViewById(R.id.text_exceptiontype);
                view.setTag(viewHolder);
            } else {
                viewHolder = (AddmoreViewHolder) view.getTag();
            }
            viewHolder.textViewWh.setText(exceptionAll.get(i).getWh_f());
            viewHolder.textViewExceptionType.setText(exceptionAll.get(i).getExceptionType());
            return view;
        }
    }

    class AddmoreViewHolder {
        private TextView textViewWh;
        private TextView textViewExceptionType;
    }
}
