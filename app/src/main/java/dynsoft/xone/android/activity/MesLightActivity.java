package dynsoft.xone.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.ResponseBody;
import com.tcpclient.TCPClient;
import com.tcpclient.socket.TcpClientConnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dynsoft.xone.android.bean.RequestBean;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.PrintRequest;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.PrintHelper;
import dynsoft.xone.android.retrofit.DingdingService;
import dynsoft.xone.android.retrofit.MarkDownBean;
import dynsoft.xone.android.retrofit.RespondBean;
import dynsoft.xone.android.retrofit.RetrofitDownUtil;
import dynsoft.xone.android.retrofit.TextBean;
import dynsoft.xone.android.sopactivity.ScanTestActivity;
import dynsoft.xone.android.util.SmsUtilV;
import dynsoft.xone.android.wms.pn_qm_electric_equipment_check_mgr_editor;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Administrator on 2018/4/11.
 */

public class MesLightActivity extends Activity {
    private static final String appKey = "dingwcdrldanxpmn4xzm";
    private static final String appSecret = "4Zlck9BJD_P2INzxCaaUauDoYNCs8WHZ_VxU8vN3vKnuGGJNEGzcjN8qY-YgQTlY";
    //    private static final String TCP_IP = "192.168.152.80";
    private HashMap<String, String> Line_IP;
    private static final int PORT = 6000;
    private GridView gridView;
    private ListView listView;
    private ArrayList<String> allExceptionTypes;
    private ArrayList<String> selectedExceptions;
    private SharedPreferences sharedPreferences;
    private String station;
    private String stage_name;
    private int currentSelect;
    private LightAdapter lightAdapter;
    private HashMap<Integer, String> starMap;
    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private CheckBox checkBox3;
    private int task_order_id;
    private ArrayList<DataRow> dataRows;
    private String workLine;
    private Integer work_line_id;
    private String production;
    private TcpClientConnector connector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_mes);
        gridView = (GridView) findViewById(R.id.gridview_light);
        listView = (ListView) findViewById(R.id.listview_light);
        sharedPreferences = getSharedPreferences("sop", MODE_PRIVATE);
        station = sharedPreferences.getString("station", "");
        stage_name = sharedPreferences.getString("stage_name", "");
        task_order_id = sharedPreferences.getInt("order_task_id", 0);
        workLine = sharedPreferences.getString("segment", "");
        work_line_id = sharedPreferences.getInt("work_line_id", 0);
        Log.e("Len", String.valueOf(work_line_id));
        production = sharedPreferences.getString("production", "");

        //"差", "一般", "满意", "非常满意", "无可挑剔"
        starMap = new HashMap<Integer, String>();
        starMap.put(1, "差");
        starMap.put(2, "一般");
        starMap.put(3, "满意");
        starMap.put(4, "非常满意");
        starMap.put(5, "无可挑剔");

        Line_IP = new HashMap<String, String>();
        // 各个线体对应的PLC IP地址
        Line_IP.put("Line1", "192.168.152.80");
        Line_IP.put("Line2", "192.168.152.80");
        Line_IP.put("Line3", "192.168.152.80");
        Line_IP.put("Line4", "192.168.152.80");

        allExceptionTypes = new ArrayList<String>();
        selectedExceptions = new ArrayList<String>();
        initGridView();
        initListView();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        try {
//            connector.disconnect();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void initGridView() {
        String sql = "select * from fm_excpt_class";
        Parameters p = new Parameters();
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(MesLightActivity.this, value.Error + "222222");
                    return;
                }
                if (value.Value != null) {
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        String name = value.Value.Rows.get(i).getValue("exception_class", "");
                        allExceptionTypes.add(name);
                    }
                    lightAdapter = new LightAdapter(selectedExceptions);
                    gridView.setAdapter(lightAdapter);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            TextView textView = (TextView) view.findViewById(R.id.textview_light);
                            ImageView imageView = (ImageView) view.findViewById(R.id.imageview_light);
                            if (imageView.isSelected()) {     //异常处理评分
                                initStarPopupWindow(textView.getText().toString());
                            } else {                         //异常申请
                                initPopupwindow(textView.getText().toString());
                            }
                        }
                    });
                } else {
                    App.Current.showError(MesLightActivity.this, "查询结果为空");
                }
            }
        });
    }

    private void initStarPopupWindow(final String s) {
        final PopupWindow popupWindow = new PopupWindow();
        View view = View.inflate(MesLightActivity.this, R.layout.item_star_popup_light, null);
        RatingBar ratingBarStar = (RatingBar) view.findViewById(R.id.ratingbar1);
        final TextView textView = (TextView) view.findViewById(R.id.textview_star);
        final EditText editTextStar = (EditText) view.findViewById(R.id.edittext_star);
        TextView buttonConfirmStar = (TextView) view.findViewById(R.id.confirm);
        TextView buttonCancelStar = (TextView) view.findViewById(R.id.cancel);
        ratingBarStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                currentSelect = Math.round(v);
                textView.setText("您的评分是:" + starMap.get(currentSelect));
            }
        });
        buttonConfirmStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commitRate(s, editTextStar.getText().toString(), popupWindow);

                // TODO：发起TCP请求 PLC 关闭报警灯
                try {


                    int value = get_error_time();

                    int msg = value - 1;

                    send_TCP(Integer.toString(msg));
                    update_error_time(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        buttonCancelStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setContentView(view);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
//                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.style_divider_color));
        popupWindow.showAtLocation(gridView, Gravity.CENTER, 20, 30);
    }

    private void commitRate(final String exception_type, String s, PopupWindow popupWindow) {
        String sql = "exec p_fm_light_exception_workline_update_and ?,?,?,?,?";
        Parameters p = new Parameters().add(1, workLine).add(2, production).add(3, exception_type).add(4, s).add(5, (long) currentSelect);
        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                Result<Integer> value = Value;
                if (value.HasError) {
                    App.Current.toastInfo(MesLightActivity.this, "提交失败" + value.Error);
                    App.Current.playSound(R.raw.error);
                } else {
                    if (value.Value > 0) {
                        selectedExceptions.remove(exception_type);
                        lightAdapter.fresh(selectedExceptions);
                        App.Current.toastInfo(MesLightActivity.this, "提交成功");
                        App.Current.playSound(R.raw.pass);
                        initListView();
                    } else {
                        App.Current.toastInfo(MesLightActivity.this, "提交失败");
                        App.Current.playSound(R.raw.error);
                    }
                }
            }
        });
        popupWindow.dismiss();
    }

    //influences_counts
    private void initPopupwindow(final String s) {
        final PopupWindow popupWindow = new PopupWindow();
        View view = View.inflate(MesLightActivity.this, R.layout.item_popup_light, null);
        final EditText editTextException = (EditText) view.findViewById(R.id.popup_edittext);
        ImageView imageButton1 = (ImageView) view.findViewById(R.id.imageview1);
        if (editTextException != null) {      //异常描述
            editTextException.setOnKeyListener(null);
            imageButton1.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_white"));
            imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseException(editTextException, s);
                }
            });
        }

        //影响人数
        final EditText editTextInfluencesCounts = (EditText) view.findViewById(R.id.popup_edittext_1);
        //处理人
        final EditText responEditText = (EditText) view.findViewById(R.id.popup_edittext_2);
        ImageView imageButton = (ImageView) view.findViewById(R.id.imageview);
        TextView confirmButton = (TextView) view.findViewById(R.id.confirm);
        TextView cancelButton = (TextView) view.findViewById(R.id.cancel);
        checkBox1 = (CheckBox) view.findViewById(R.id.checkbox_1);
        checkBox2 = (CheckBox) view.findViewById(R.id.checkbox_2);
        checkBox3 = (CheckBox) view.findViewById(R.id.checkbox_3);
        checkBox1.setChecked(true);
        checkBox2.setChecked(false);
        checkBox3.setChecked(false);
        if (responEditText != null) {      //处理人
            responEditText.setOnKeyListener(null);
            imageButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_white"));
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseRespondMan(responEditText, getMacAddress(), s);
                }
            });
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = dataRows.get(0).getValue("email", "");
                String mobile = dataRows.get(0).getValue("mobile", "");
                if (checkBox2.isChecked() && TextUtils.isEmpty(email)) {
                    App.Current.showError(MesLightActivity.this, "选择的责任人邮箱地址没有维护");
                } else if (checkBox3.isChecked() && TextUtils.isEmpty(mobile)) {
                    App.Current.showError(MesLightActivity.this, "选择的责任人电话号码没有维护");
                } else {
                    Pattern pattern = Pattern.compile("^[0-9]*$");
                    Matcher m = pattern.matcher(editTextInfluencesCounts.getText());
                    if (TextUtils.isEmpty(editTextInfluencesCounts.getText().toString()) || !m.matches()) {
                        App.Current.toastError(MesLightActivity.this, "请输入正确的影响人数");
                    } else if (TextUtils.isEmpty(editTextException.getText()) && "缺料".equals(s)) {
                        App.Current.showError(MesLightActivity.this, "请在异常描述中输入缺料料号！");
                        App.Current.playSound(R.raw.error);
                    } else if (TextUtils.isEmpty(responEditText.getText())) {
                        App.Current.showError(MesLightActivity.this, "请选择处理人！");
                        App.Current.playSound(R.raw.error);
                    } else {
                        if ("缺料".equals(s)) {
                            String[] split = editTextException.getText().toString().split(",");
                            String sql = "exec fm_check_item_code_and_v01 ?";
                            Parameters p = new Parameters().add(1, split[0]);
                            App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
                                @Override
                                public void handleMessage(Message msg) {
                                    Result<DataRow> value = Value;
                                    if (value.HasError) {
                                        App.Current.toastError(MesLightActivity.this, value.Error);
                                        return;
                                    }
                                    if (value.Value != null) {   //料号正确
                                        int result = value.Value.getValue("result", new BigDecimal(0)).intValue();
                                        if (result == -1) {
                                            //料号输入有误！
                                            App.Current.toastError(MesLightActivity.this, "输入的料号有误，请重新输入！");
                                            App.Current.playSound(R.raw.error);
                                        } else if (result == 0) {
                                            //无库存，可提交
                                            commitException(s, popupWindow, editTextException.getText().toString(), Integer.parseInt(editTextInfluencesCounts.getText().toString()));
                                        } else {
                                            //有库存，提示
                                            App.Current.toastError(MesLightActivity.this, "当前物料有库存，库位4101,库存" + result);
                                            App.Current.playSound(R.raw.error);
                                        }
                                    } else {
                                        App.Current.toastError(MesLightActivity.this, "输入的料号有误，请重新输入！");
                                        App.Current.playSound(R.raw.error);
                                    }
                                }
                            });
                        } else if ("物料异常".equals(s)) {
                            String[] split = editTextException.getText().toString().split(",");
                            String sql = "exec fm_check_item_code_and ?";
                            Parameters p = new Parameters().add(1, split[0]);
                            App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
                                @Override
                                public void handleMessage(Message msg) {
                                    Result<DataRow> value = Value;
                                    if (value.HasError) {
                                        App.Current.showError(MesLightActivity.this, value.Error);
                                        return;
                                    }
                                    if (value.Value != null) {
                                        commitException(s, popupWindow, editTextException.getText().toString(), Integer.parseInt(editTextInfluencesCounts.getText().toString()));
                                    } else {
                                        App.Current.showError(MesLightActivity.this, "输入物料编码有误！");
                                        App.Current.playSound(R.raw.error);
                                    }
                                }
                            });
                        } else {
                            commitException(s, popupWindow, editTextException.getText().toString(), Integer.parseInt(editTextInfluencesCounts.getText().toString()));
                            if ("设备故障".equals(s)) {
                                startFlow(editTextException);
                            }
                        }
                        // TODO：发起TCP请求 PLC 打开报警灯
                        try {
                            int value = get_error_time();
                            value = Math.max(value, 0);
                            int msg = value + 1;

                            send_TCP("1");
                            update_error_time(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                if (dataRows != null) {
                    dataRows.removeAll(dataRows);
                    dataRows.clear();
                }
            }
        });
        popupWindow.setContentView(view);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
//                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.style_divider_color));
        popupWindow.showAtLocation(gridView, Gravity.CENTER, 20, 30);
    }

    private int get_error_time() {
        final String sql_str = "exec get_work_line_error_time ?";
        Parameters pr = new Parameters().add(1, work_line_id);
        return (Integer) App.Current.DbPortal.ExecuteScalar("core_and", sql_str, pr).Value;
    }

    private void update_error_time(int msg) {

        String sql = "exec update_work_line_error_time ?,?";
        Parameters p = new Parameters().add(1, work_line_id).add(2, msg);
        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                Result<Integer> value = Value;
                if (value.HasError) {
                    App.Current.showError(MesLightActivity.this, value.Error);
                    return;
                }
                if (value.Value != null) {
                    int value1 = value.Value;
                    if (value1 > 0) {
                        App.Current.toastInfo(MesLightActivity.this, "提交成功");
                        App.Current.playSound(R.raw.pass);
                    } else {
                        App.Current.toastError(MesLightActivity.this, "提交失败,没有更新到数据。");
                        App.Current.playSound(R.raw.error);
                    }
                } else {
                    App.Current.toastError(MesLightActivity.this, "提交失败");
                    App.Current.playSound(R.raw.error);
                }
            }
        });
    }

    private void send_TCP(String msg) throws IOException {
        String tcp_ip = Line_IP.get(workLine);
//        connector = TcpClientConnector.getInstance();
//        connector.setOnConnectLinstener(new TcpClientConnector.ConnectLinstener() {
//            @Override
//            public void onReceiveData(String data) {
//                //do somethings.
//            }
//        });
//        connector.creatConnect(tcp_ip,PORT);
//        connector.send(msg);
        TCPClient tcpClient = new TCPClient(tcp_ip, PORT) {
            @Override
            protected void onDataReceive(byte[] bytes, int size) {
                String content = "TCPServer say :" + new String(bytes, 0, size);
                Log.i("TCPServer", content);
                App.Current.toastInfo(MesLightActivity.this, content);
            }
        };
        tcpClient.connect();//连接TCPServer
        if (tcpClient.isConnected()) {
            //发送数据
            tcpClient.send(msg.getBytes());
        }
        //关闭连接
        tcpClient.close();
    }

    private void chooseException(final EditText edittext, final String s) {
        String sql = "exec fm_get_choose_exception_and ?";
        Parameters p = new Parameters().add(1, s);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(MesLightActivity.this, value.Error);
                    App.Current.playSound(R.raw.error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    ArrayList<String> names = new ArrayList<String>();
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        names.add(value.Value.Rows.get(i).getValue("name", ""));
                    }
                    dataRows = new ArrayList<DataRow>();
                    multiChoiceDialog_Exception(value.Value, edittext);
                } else {
                    App.Current.toastError(MesLightActivity.this, s + "的异常描述没有维护。");
                    App.Current.playSound(R.raw.error);
                }
            }
        });
    }

    private void chooseRespondMan(final EditText edittext, String macAddress, final String s) {    //选择异常处理人
        String sql = "exec fm_get_choose_exception_respond_workline_and ?,?";
        //s  异常类型
        Parameters p = new Parameters().add(1, work_line_id).add(2, s);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(MesLightActivity.this, value.Error);
                    App.Current.playSound(R.raw.error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    ArrayList<String> names = new ArrayList<String>();
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        names.add(value.Value.Rows.get(i).getValue("name", ""));
                    }
                    dataRows = new ArrayList<DataRow>();
                    multiChoiceDialog(value.Value, edittext);
                } else {
                    App.Current.toastError(MesLightActivity.this, s + "的责任人没有维护。");
                    App.Current.playSound(R.raw.error);
                }
            }
        });
    }

    private void multiChoiceDialog_Exception(final DataTable dataTable, final EditText editText) {
        ArrayList<String> names = new ArrayList<String>();
        for (DataRow dataRow : dataTable.Rows) {
            String name = dataRow.getValue("name", "");
            Log.e("len", "name : " + name);
            names.add(name);
        }
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    String name = dataTable.Rows.get(which).getValue("name", "");
                    editText.setText(name);
                    dataRows.add(dataTable.Rows.get(which));
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(MesLightActivity.this).setTitle("请选择")
                .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                        (editText.getText().toString()), listener)
                .setNegativeButton("取消", null).show();
    }

    private void multiChoiceDialog(final DataTable dataTable, final EditText editText) {
        ArrayList<String> names = new ArrayList<String>();
        for (DataRow dataRow : dataTable.Rows) {
            String name = dataRow.getValue("name", "");
            Log.e("len", "name : " + name);
            names.add(name);
        }
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    String name = dataTable.Rows.get(which).getValue("name", "");
                    editText.setText(name);
                    dataRows.add(dataTable.Rows.get(which));
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(MesLightActivity.this).setTitle("请选择")
                .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                        (editText.getText().toString()), listener)
                .setNegativeButton("取消", null).show();
    }

    /**
     * 提交异常
     *
     * @param exception_type    异常类型
     * @param popupWindow       弹出的界面
     * @param exception_comment 异常描述
     */
    private void commitException(final String exception_type, PopupWindow popupWindow, String exception_comment, int influencesCounts) {
        final String sql = "exec p_fm_light_exception_commit_and ?, ?, ?, ?, ?, ?, ?, ?";
        String code = dataRows.get(0).getValue("code", "");
        Parameters p = new Parameters().add(1, work_line_id).add(2, production).add(3, exception_type)
                .add(4, exception_comment).add(5, task_order_id).add(6, station)
                .add(7, influencesCounts).add(8, code);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(MesLightActivity.this, value.Error + "33333");
                    App.Current.playSound(R.raw.error);
                    return;
                }
                if (value.Value != null) {
                    if (checkBox1.isChecked()) {      //发送待办
//                        sendMessageForEIP(value.Value);
                        sendMessageToDingding(value.Value);
                    }
                    if (checkBox2.isChecked()) {     //发送邮件
                        sendEmail(value.Value);
                    }
                    if (checkBox3.isChecked()) {     //发送短信
                        sendMessageByAli(value.Value);
                    }
                    selectedExceptions.add(exception_type);
                    lightAdapter.fresh(selectedExceptions);
                    initListView();
//                    sendMessageForEIP(dt);
//                    sendMessageByAli(dt);
//                    App.Current.toastInfo(MesLightActivity.this, "提交成功");
//                    App.Current.playSound(R.raw.pass);
                } else {
                    App.Current.toastError(MesLightActivity.this, "提交成功,但是责任人没有维护，请检查责任人设置，短信发送失败！");
                    App.Current.playSound(R.raw.error);
                    selectedExceptions.add(exception_type);
                    lightAdapter.fresh(selectedExceptions);
                    initListView();
                }
            }
        });
        popupWindow.dismiss();
    }

    private void sendEmail(DataRow dataRow) {
        String email = dataRow.getValue("email", "").trim();
        if (!TextUtils.isEmpty(email)) {    //email不为空，
            String exception_type = dataRow.getValue("exception_type", "").trim();
            String exception_comment = dataRow.getValue("exception_comment", "").trim();
            String dev_name = dataRow.getValue("dev_name", "").trim();
            final Date create_time = dataRow.getValue("create_time", new Date());
            String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(create_time).trim();
            String work_line = dataRow.getValue("work_line", "").trim();
            String use_name = dataRow.getValue("use_name", "").trim();
            String phone_number = dataRow.getValue("phone_number", "").trim();

            String subject = "异常呼叫";
            String sb = "";
            if (TextUtils.isEmpty(phone_number)) {
                sb = work_line + "线" + dev_name + "在" + createTime + "有" + exception_type + exception_comment;
            } else {
                sb = work_line + "线" + dev_name + "在" + createTime + "有" + exception_type + exception_comment + ",请联系" + use_name + "处理,电话:" + phone_number;
            }
            String body = sb.toString();
            Log.e("len", sb + "BODY:" + body);
            String sql = "exec p_qm_exception_email_send_add ?,?,?";
            Parameters p = new Parameters().add(1, subject).add(2, body).add(3, email);
            App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
                @Override
                public void handleMessage(Message msg) {
                    Result<DataRow> value = Value;
                    if (value.HasError) {
                        App.Current.showError(MesLightActivity.this, value.Error);
                        return;
                    }
                    if (value.Value != null) {
                        long id = value.Value.getValue("id", 0L);
                        App.Current.playSound(R.raw.pass);
                        App.Current.toastInfo(MesLightActivity.this, "发送邮件成功！");
                    } else {
                        App.Current.playSound(R.raw.error);
                        App.Current.toastInfo(MesLightActivity.this, "发送邮件失败！");
                    }
                }
            });
        }
    }

    private void sendMessageByAli(DataRow dataRow) {
        if (dataRow != null) {
            String exception_type = dataRow.getValue("exception_type", "").trim();
            String exception_comment = dataRow.getValue("exception_comment", "").trim();
            Date create_time = dataRow.getValue("create_time", new Date());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String createTime = simpleDateFormat.format(create_time).trim();
            String dev_name = dataRow.getValue("dev_name", "").trim();
            String work_line = dataRow.getValue("work_line", "").trim();
            String responsible_name = dataRow.getValue("responsible_name", "").trim();
            String mobile = dataRow.getValue("mobile", "").trim();
            String use_name = dataRow.getValue("use_name", "").trim();
            String phone_number = dataRow.getValue("phone_number", "").trim();

            if (TextUtils.isEmpty(phone_number)) {  //电话为空
                String jsonDatas = "{\"exception_type\":" + "\"" + exception_type + "\"" + "," +
                        "\"exception_comment\":" + "\"" + exception_comment + "\"" + "," +
                        "\"name\":" + "\"" + responsible_name + "\"" + "," +
                        "\"work_line\":" + "\"" + work_line + "\"" + "," +
                        "\"create_time\":" + "\"" + createTime + "\"" + "," +
                        "\"dev_name\":" + "\"" + dev_name + "\"" + "}";
                try {
                    String sendSmsResponse = SmsUtilV.calculate("麦格米特", "SMS_130917810", mobile, jsonDatas);
                    if ("OK".equals(sendSmsResponse.toUpperCase())) {
                        App.Current.toastInfo(MesLightActivity.this, "发送短信成功！");
                        App.Current.playSound(R.raw.pass);
                    } else {
                        App.Current.showError(MesLightActivity.this, sendSmsResponse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("len", e.getMessage() + "发送短信");
                }
            } else {
                //SMS_141582019
                String jsonDatas = "{\"exception_type\":" + "\"" + exception_type + "\"" + "," +
                        "\"exception_comment\":" + "\"" + exception_comment + "\"" + "," +
                        "\"name\":" + "\"" + responsible_name + "\"" + "," +
                        "\"work_line\":" + "\"" + work_line + "\"" + "," +
                        "\"create_time\":" + "\"" + createTime + "\"" + "," +
                        "\"use_name\":" + "\"" + use_name + "\"" + "," +
                        "\"phone_number\":" + "\"" + phone_number + "\"" + "," +
                        "\"dev_name\":" + "\"" + dev_name + "\"" + "}";
                try {
                    String sendSmsResponse = SmsUtilV.calculate("麦格米特", "SMS_141582019", mobile, jsonDatas);
                    if ("OK".equals(sendSmsResponse.toUpperCase())) {
                        App.Current.toastInfo(MesLightActivity.this, "发送短信成功！");
                        App.Current.playSound(R.raw.pass);
                    } else {
                        App.Current.showError(MesLightActivity.this, sendSmsResponse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("len", e.getMessage() + "发送短信");
                }
            }
        }
    }

    private void sendMessageForEIP(DataRow rw) {
//        String exception_type = dataTable.Rows.get(0).getValue("exception_type", "");
//        String exception_comment = dataTable.Rows.get(0).getValue("exception_comment", "");
//        Date create_time = dataTable.Rows.get(0).getValue("create_time", new Date());
//        String createTime = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss").format(create_time);
//        String dev_name = dataTable.Rows.get(0).getValue("dev_name", "");
//        String work_line = dataTable.Rows.get(0).getValue("work_line", "");
//        String responsible_code = dataTable.Rows.get(0).getValue("responsible_code", "");
//        String mobile = dataTable.Rows.get(0).getValue("mobile", "");
//        String use_name = dataTable.Rows.get(0).getValue("use_name", "");
//        String phone_number = dataTable.Rows.get(0).getValue("phone_number", "");
//
//        for (DataRow dataRow : dataRows) {
//
//        }

        if (rw != null) {
            String exception_type = rw.getValue("exception_type", "").trim();
            String exception_comment = rw.getValue("exception_comment", "").trim();
            Date create_time = rw.getValue("create_time", new Date());
            String createTime = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss").format(create_time).trim();
            String dev_name = rw.getValue("dev_name", "").trim();
            String work_line = rw.getValue("work_line", "").trim();
            String responsible_code = rw.getValue("responsible_code", "").trim();
            String mobile = rw.getValue("mobile", "").trim();
            String use_name = rw.getValue("use_name", "").trim();
            String phone_number = rw.getValue("phone_number", "").trim();

//                String path = "http://192.168.0.10:8000/plmweb/soap.nsf/QsendEIP_ems?OpenAgent&nr="
//                        + work_line + dev_name + "在" + createTime + "有" + exception_type + "异常" + exception_comment + "。"
//                        + "&gh={\"LoginName\":\"" + "M3933" + "\"}";
            StringBuilder sb = new StringBuilder();
            if (TextUtils.isEmpty(phone_number)) {
                sb.append("EIP待办:" + work_line + "线" + dev_name + "在" + createTime + "有" + exception_type + "异常" + exception_comment);
            } else {
                sb.append("EIP待办:" + work_line + "线" + dev_name + "在" + createTime + "有" + exception_type + "异常" + exception_comment + ",可以联系" + use_name + ",号码:" + phone_number);
            }
            String nrContent = sb.toString();
//                String nrCode = "";
//                try {
//                    nrCode = URLEncoder.encode(nrContent, "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
            responsible_code = "M3933";
            String nrCode = nrContent;
            String path = "http://10.3.1.11:8443/plmweb/soap.nsf/QsendEIP_ems?OpenAgent&nr=" + nrCode + "&gh={\"LoginName\":\"" + responsible_code + "\"}";

            Log.e("len", path);

            try {
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.connect();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    Log.e("len", line);
                }
                bufferedReader.close();
                conn.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("len", "URL:" + e.getLocalizedMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("len", "CONN:" + e.getMessage());
            }

//                try {
//                    StringBuilder path = new StringBuilder();
//                    path.append("http://192.168.0.10:8000/plmweb/soap.nsf/QsendEIP_ems?OpenAgent");
//                    path.append(stringBuilder.toString());
//                    URL url = new URL(path.toString());
////                    URL url = new URL("http://192.168.0.10:8000/plmweb/soap.nsf/QsendEIP_ems?OpenAgent&nr=你好EIP22&gh={\"LoginName\":\"M3933\"}");
//                    Log.e("len", "PATH:" + path);
//                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                    httpURLConnection.setRequestMethod("POST");// 提交模式
//                    // conn.setConnectTimeout(10000);//连接超时 单位毫秒
//                    // conn.setReadTimeout(2000);//读取超时 单位毫秒
//                    // 发送POST请求必须设置如下两行
//                    httpURLConnection.setDoOutput(true);
//                    httpURLConnection.setDoInput(true);
//                    //开始获取数据
//                    BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
//                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                    int len;
//                    byte[] arr = new byte[1024];
//                    while ((len = bis.read(arr)) != -1) {
//                        bos.write(arr, 0, len);
//                        bos.flush();
//                    }
//                    bos.close();
//                    Log.e("len", bos.toString("utf-8"));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

//                try {
////                    StringBuilder path = new StringBuilder();
////                    path.append("http://192.168.0.10:8000/plmweb/soap.nsf/QsendEIP_ems?OpenAgent");
////                    path.append(stringBuilder.toString());
//
//                    String path = "http://192.168.0.10:8000/plmweb/soap.nsf/QsendEIP_ems?OpenAgent&nr="
//                            + work_line + dev_name + "在" + createTime + "有" + exception_type + "异常" + exception_comment + "。"
//                            + "&gh={\"LoginName\":\"" + "M3933" + "\"}";
//                    URL url = new URL(path.toString());
//                    Log.e("len", "PATH:" + path);
//                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                    if (200 == httpURLConnection.getResponseCode()) {
//                        App.Current.toastInfo(MesLightActivity.this, "发送待办成功！");
//                        //得到输入流
//                        InputStream is = httpURLConnection.getInputStream();
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        byte[] buffer = new byte[1024];
//                        int len = 0;
//                        while (-1 != (len = is.read(buffer))) {
//                            baos.write(buffer, 0, len);
//                            baos.flush();
//                        }
//                        Log.e("len", baos.toString());
//                    } else {
//                        App.Current.toastInfo(MesLightActivity.this, "发送待办失败！" + httpURLConnection.getResponseCode());
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.e("len", e.getMessage() + "待办");
//                }
//
//            }

        } else {
            App.Current.showError(MesLightActivity.this, "提交成功,但是责任人没有维护，请检查责任人设置，待办发送失败！");
        }

//        try {
//            SmsUtilV.lightMessage(properties);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("len", "EEEE:" + e.getMessage());
//        }

//        HttpUtils http = new HttpUtils(10 * 1000);
//        http.configCurrentHttpCacheExpiry(0);
//        String xml = null;
//        xml = stringBuilder.toString();
//
//        Log.e("len", "xml:" + xml);
//
////        String url = "http://192.168.0.111:8008/sys/webservice/sysNotifyTodoWebService";
////        String url = "http://eip.megmeet.com:8008/sys/webservice/sysNotifyTodoWebService?user=megmeet&password=122970535830cb3b8f6039afe52faedf";
//        String url = "http://eip.megmeet.com:8008/sys/webservice/sysNotifyTodoWebService.wsdl";
//
//        if (url.contains(" ")){
//            if(url.substring(url.length()-1)==" "){
//                url= url.substring(0,url.length()-1);
//            }else{
//                url= url.replace(" ","%20");
//            }
//        }
//        if (url.contains("\"")){
//            url= url.replace("\"","%22");
//        }
//        if (url.contains("{")){
//            url= url.replace("{","%7B");
//        }
//        if (url.contains("}")){
//            url= url.replace("{","%7D");
//        }
//
//        RequestParams params = new RequestParams();
//        params.setContentType("application/soap+xml; charset=utf-8");
//        HeaderElement[] header = new HeaderElement[1];
//        header[0] = new Element().createElement(nameSpace, "SoapHeader");
//        try {
//            params.setBodyEntity(new StringEntity(xml, "utf-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            Log.e("len", "EE:" + e.getMessage());
//        }
//
//        http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
//
//            @Override
//            public void onFailure(HttpException e, String arg1) {
//                e.printStackTrace();
//                Log.e("len", "onFailure:" + arg1);
//            }
//
//            @Override
//            public void onSuccess(ResponseInfo<String> responseInfo) {
//                Log.e("len", "onSuccess:" + responseInfo.result);
//            }
//        });

//        String namespace = "http://webservice.notify.sys.kmss.landray.com/";
//        String transUrl = "http://192.168.0.111:8008/sys/webservice/sysNotifyTodoWebService";
//        String method = "sendTodo";
////注意版本使用，这个需要跟后台询问或者从wsdl文档或者服务说明中查看
//        int envolopeVersion = SoapEnvelope.VER11;
////可能是namspace+method拼接
//        SoapObject request = new SoapObject(namespace, method);
////参数一定注意要有序，尽管是addProperty（），不要当作HttpUrl可以使用LinkedHashMap封装
//        for (Iterator<Map.Entry<String, String>> it = properties.entrySet()
//                .iterator(); it.hasNext(); ) {
//            Map.Entry<String, String> entry = it.next();
//            request.addProperty(entry.getKey(), entry.getValue());
//        }
//
//        Log.e("len", properties.toString());
////                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(envolopeVersion);
//        envelope.setOutputSoapObject(request);
//        envelope.dotNet = true;
//        HttpTransportSE se = new HttpTransportSE(transUrl);
//        try {
//            //                    se.call(soapAction, envelope);    //ver11，第一个参数不能为空
//            se.call(null, envelope);//envolopeVersion为ver12第一个参数可以为空，必须接口支持ver12才行
//            SoapObject response = (SoapObject) envelope.bodyIn;
//            //response的处理需要根据返回的具体情况，基本都要进行下面一步
//            SoapObject o = (SoapObject) response.getProperty(0);
//            //当前方法返回的结果为一个数组
//            Log.e("zjy", "MainActivity.java->run(): size=" + o.getPropertyCount());
//            for (int i = 0; i < o.getPropertyCount(); i++) {
//                Log.e("zjy", "MainActivity.java->run(): ==" + o.getProperty(i));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e("len", e.getMessage() + "22222");
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            Log.e("len", e.getMessage() + "11111");
//        }

//        try {
//            byte[] xmlbyte = stringBuilder.toString().getBytes("UTF-8");
//            Log.e("len", "StringBuilder:" + stringBuilder.toString());
//            try {
//                URL url = new URL("http://192.168.0.111:8008/sys/webservice/sysNotifyTodoWebService");
//                try {
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setConnectTimeout(5000);
////                    conn.setUseCaches(false);// 不使用缓存
//                    conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
//                    conn.setRequestProperty("Charset", "UTF-8");
////                    conn.setRequestProperty("Content-Length", String.valueOf(xmlbyte.length));
//                    conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
//                    conn.setRequestMethod("POST");
//                    conn.setDoOutput(true);// 允许输出
//                    conn.setDoInput(true);
////                    conn.setRequestProperty("X-ClientType", "2");//发送自定义的头信息
//
//                    conn.getOutputStream().write(xmlbyte);
//                    conn.getOutputStream().flush();
//                    conn.getOutputStream().close();
//
//
//                    if (conn.getResponseCode() != 200) {
//                        throw new RuntimeException("请求url失败");
//                    } else {
//                        Log.e("len", "返回码为:" + conn.getResponseCode());
//                    }
//                    InputStream is = conn.getInputStream();// 获取返回数据
//
//                    // 使用输出流来输出字符(可选)
//                    ByteArrayOutputStream out = new ByteArrayOutputStream();
//                    byte[] buf = new byte[1024];
//                    int len;
//                    while ((len = is.read(buf)) != -1) {
//                        out.write(buf, 0, len);
//                    }
//                    String string = out.toString("UTF-8");
//                    Log.e("len", "BACK:" + string);
//                    out.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.e("len", e.getMessage() + "4444444");
//                }
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//                Log.e("len", e.getMessage() + "2222");
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            Log.e("len", e.getMessage() + "11111");
//        }
//    }

//        String nameSpace = "http://webservice.notify.sys.kmss.landray.com/";
//        String transUrl = "http://192.168.0.111:8008/sys/webservice/sysNotifyTodoWebService.wsdl";
//        String method = "sendTodo";
//        String soapAction = "http://webservice.notify.sys.kmss.landray.com/sendTodo";
//
//        int envolopeVersion = SoapEnvelope.VER10;
//
//        Element[] header = new Element[1];
//        header[0] = new Element().createElement(nameSpace, "RequestSOAPHeader");
//        Element username = new Element().createElement(nameSpace, "tns:user");
//        username.addChild(Node.TEXT, "megmeet");
//        header[0].addChild(Node.ELEMENT, username);
//        Element pass = new Element().createElement(nameSpace, "password");
//        pass.addChild(Node.TEXT, "122970535830cb3b8f6039afe52faedf");
//        header[0].addChild(Node.ELEMENT, pass);
//
//        SoapObject soapObject = new SoapObject(nameSpace, method);
//
//        soapObject.addProperty("appName", "MES");
//        soapObject.addProperty("modelName", "MES");
//        soapObject.addProperty("modelId", "77777");
//        soapObject.addProperty("subject", "异常呼叫");
//        soapObject.addProperty("link", "http://www.baidu.com");
//        soapObject.addProperty("type", "2");
//        soapObject.addProperty("targets", "{\"LoginName\":\"M3933\"}");
//        soapObject.addProperty("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(envolopeVersion);
//        //SoapEnvelope.VER11 表示使用的soap协议的版本号 1.1 或者是1.2
//
//        // 第四步：注册Envelope
//        envelope.env = "http://schemas.xmlsoap.org/soap/envelope/";
//        envelope.enc = "http://schemas.xmlsoap.org/soap/encoding/";
//        envelope.xsi = "http://webservice.notify.sys.kmss.landray.com/";
//        envelope.xsd = "http://login.kmss.landray.com/";
//
//        envelope.headerOut = header;
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = false; //指定webservice的类型的（java，PHP，dotNet）
//        envelope.setOutputSoapObject(soapObject);
//
////        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(envolopeVersion);
////        envelope.enc="";
////        envelope.env="";
////        envelope.xsd="";
////        envelope.xsi="";
////        envelope.setOutputSoapObject(soapObject);
////        envelope.dotNet = false;
//        HttpTransportSEYL se = new HttpTransportSEYL(transUrl);
//        byte[] xmlbyte = stringBuilder.toString().getBytes();
//        se.setDatas(xmlbyte);
//        try {
//            se.call(soapAction, envelope);
//            SoapObject response = (SoapObject) envelope.bodyIn;
//            SoapObject property = (SoapObject) response.getProperty(0);
//            //当前方法返回的结果为一个数组
//            Log.e("zjy", "MainActivity.java->run(): size=" + property.getPropertyCount());
//            for (int i = 0; i < property.getPropertyCount(); i++) {
//                Log.e("zjy", "MainActivity.java->run(): ==" + property.getProperty(i));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e("len", "EE:" + e.getMessage());
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            Log.e("len", "AA:" + e.getLocalizedMessage());
//        }
    }

    private void startFlow(EditText edittext) {
        App.Current.question(MesLightActivity.this, "确定要发起流程吗？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                String code = null, name = null, location = null;
                String sql = "select code,\n" +
                        "       name,\n" +
                        "       location\n" +
                        "from fm_work_dev where  mac_address=? ";
                Parameters p = new Parameters().add(1, getMacAddress());

                Result<DataRow> r = App.Current.DbPortal.ExecuteRecord("core_and", sql, p);
                if (r != null && r.Value != null) {

                    code = r.Value.getValue("code", String.class);
                    name = r.Value.getValue("name", String.class);
                    location = r.Value.getValue("location", String.class);

                }


                PrintRequest request = new PrintRequest();
                request.Server = "http://192.168.151.130:6683";
                request.Printer = "#15";
                request.Code = "start_eip_flow_comm";

                //准备打印参数
                request.Parameters = new HashMap<String, String>();

                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("{\"fd_sheBeiBianHao\":\"");
                stringBuffer.append(code);
                stringBuffer.append("\",\"fd_sheBeiMingChen\":\"");
                stringBuffer.append(name);
                stringBuffer.append("\",\"fd_sheBeiSuoZaiWeiZhi\":\"");
                stringBuffer.append(location);
                stringBuffer.append("\",\"fd_guZhangMiaoShu\":\"");
                stringBuffer.append(edittext.getText());
                stringBuffer.append("\"}");


                request.Parameters.put("fdTemplateId", "174a03db3abe33e629c760d4dac89e64");
                request.Parameters.put("docSubject", "设备保修流程");
                request.Parameters.put("formValues", stringBuffer.toString().replace("\\", ""));
//                request.Parameters.put("formValues:", jsonDatas);
                request.Parameters.put("TaskUser", App.Current.UserCode);
                Result<String> result = PrintHelper.print(request);
                if (result.HasError) {
                    App.Current.showError(MesLightActivity.this, result.Error + "1111");
                    App.Current.playSound(R.raw.error);
                } else {
                    App.Current.showInfo(MesLightActivity.this, "提交成功");
                }
            }
        });
    }

    public void sendMessageToDingding(DataRow rw) {
        if (rw != null) {
            String exception_type = rw.getValue("exception_type", "").trim();
            String exception_comment = rw.getValue("exception_comment", "").trim();
            Date create_time = rw.getValue("create_time", new Date());
            String createTime = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss").format(create_time).trim();
            String dev_name = rw.getValue("dev_name", "").trim();
            String work_line = rw.getValue("work_line", "").trim();
            String responsible_code = rw.getValue("responsible_code", "").trim();
            String mobile = rw.getValue("mobile", "").trim();
            String use_name = rw.getValue("use_name", "").trim();
            String phone_number = rw.getValue("phone_number", "").trim();

//                String path = "http://192.168.0.10:8000/plmweb/soap.nsf/QsendEIP_ems?OpenAgent&nr="
//                        + work_line + dev_name + "在" + createTime + "有" + exception_type + "异常" + exception_comment + "。"
//                        + "&gh={\"LoginName\":\"" + "M3933" + "\"}";
            StringBuilder sb = new StringBuilder();
            if (TextUtils.isEmpty(phone_number)) {
                sb.append(exception_type + "异常，" + exception_comment + "。");
            } else {
                sb.append(exception_type + "异常，" + exception_comment + ",可以联系" + use_name + ",号码:" + phone_number);
            }

            //发送钉钉工作通知
            Retrofit dingdingRetrofit = RetrofitDownUtil.getInstence().getDingdingRetrofit();
            DingdingService dingdingService = dingdingRetrofit.create(DingdingService.class);
            Call<ResponseBody> token = dingdingService.getToken(appKey, appSecret);
            token.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                    if (response.code() == 200) {
                        try {
                            String string = response.body().string();
                            Gson gson = new Gson();
                            RespondBean respondBean = gson.fromJson(string, RespondBean.class);
                            String access_token = respondBean.getAccess_token();

                            //通过电话获取Userid
                            Call<ResponseBody> userIdByMobile = dingdingService.getUserIdByMobile(access_token, mobile);
                            userIdByMobile.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                                    if (response.code() == 200) {
                                        try {
                                            String userMessage = response.body().string();
                                            Gson gson = new Gson();
                                            RespondBean respondBean = gson.fromJson(userMessage, RespondBean.class);
                                            String userid = respondBean.getUserid();

                                            RequestBean requestBean = new RequestBean();
                                            requestBean.setAgent_id("648372786");
                                            requestBean.setUserid_list(userid);
                                            MarkDownBean markDownBean = new MarkDownBean();
                                            TextBean textBean = new TextBean();
                                            textBean.setTitle("MES安灯通知");
                                            String content = ""
                                                    + "  \n  线体：" + workLine + "," + stage_name + "," + station
                                                    + "  \n  工序：" + production
                                                    + "  \n  原因：" + sb.toString()
                                                    + "  \n  时间：" + createTime;
                                            String text = "<font color=#FF0000 size=6 face=\"黑体\">MES安灯通知 </font> " +
                                                    " ![](https://www.ikahe.com/style/images/logo.png)\n" +
                                                    "<font color=#000000 size=4 face=\"黑体\">" + content + "</font> ";
                                            markDownBean.setMarkdown(textBean);
                                            textBean.setText(text);
                                            markDownBean.setMsgtype("markdown");
                                            requestBean.setMsg(markDownBean);
                                            Log.e("len", ":::" + new Gson().toJson(requestBean));
                                            Call<ResponseBody> responseBodyCall = dingdingService.sendMessage(access_token, requestBean);
                                            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                                                    int code = response.code();
                                                    if (code == 200) {
                                                        try {
                                                            Log.e("len", "sss:" + response.body().string());
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                            Log.e("len", e.getLocalizedMessage());
                                                        }
                                                    } else {
                                                        App.Current.toastError(MesLightActivity.this, code + "");
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Throwable throwable) {
                                                    Log.e("len", "Fail:" + throwable.getLocalizedMessage());
                                                }
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Throwable throwable) {

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.e("len", "ERR: " + throwable.getLocalizedMessage());
                }
            });

        } else {
            App.Current.showError(MesLightActivity.this, "提交成功,但是责任人没有维护，请检查责任人设置，待办发送失败！");
        }
    }

    private String getMacAddress() {
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

    public void initListView() {
        final String sql = "exec p_fm_work_light_exception_and ?,?";
        Parameters p = new Parameters().add(1, workLine).add(2, production); //MAC_ADRESS mac地址
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(MesLightActivity.this, value.Error + "11111");
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        String status = value.Value.Rows.get(i).getValue("status", "");
                        String exception_type = value.Value.Rows.get(i).getValue("exception_type", "");
                        if ("呼叫".equals(status) && !selectedExceptions.contains(exception_type)) {
                            selectedExceptions.add(exception_type);
                        }
                    }
                    if (lightAdapter != null) {
                        lightAdapter.fresh(selectedExceptions);
                    }
                    LightListAdapter lightListAdapter = new LightListAdapter(value.Value);
                    listView.setAdapter(lightListAdapter);
//                    String result = value.Value.Rows.get(0).getValue("result", "");
//                    if ("OK".equals(result)) {
//                        for (int i = 0; i < value.Value.Rows.size(); i++) {
//                            String status = value.Value.Rows.get(i).getValue("status", "");
//                            String exception_type = value.Value.Rows.get(i).getValue("exception_type", "");
//                            if ("呼叫".equals(status) && !selectedExceptions.contains(exception_type)) {
//                                selectedExceptions.add(exception_type);
//                            }
//                        }
//                        if (lightAdapter != null) {
//                            lightAdapter.fresh(selectedExceptions);
//                        }
//                        LightListAdapter lightListAdapter = new LightListAdapter(value.Value);
//                        listView.setAdapter(lightListAdapter);
//                    } else {
//                        App.Current.toastError(MesLightActivity.this, result + getMacAddress());
//                        App.Current.playSound(R.raw.error);
//                        finish();
//                    }
                } else {
                }
            }
        });
    }

    public String getTimeDifference(Date starTime, Date endTime) {   //获取时间间隔
        String timeString = "";
        long diff = endTime.getTime() - starTime.getTime();

        long day = diff / (24 * 60 * 60 * 1000);
        long hour = (diff / (60 * 60 * 1000) - day * 24);
        long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long ms = (diff - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
                - min * 60 * 1000 - s * 1000);
        // System.out.println(day + "天" + hour + "小时" + min + "分" + s +
        // "秒");
        long hour1 = diff / (60 * 60 * 1000);
        String hourString = hour1 + "";
        long min1 = ((diff / (60 * 1000)) - hour1 * 60);
        long s1 = (diff / 1000) - (hour1 * 60 * 60) - (min1 * 60);

        if (hour1 > 0) {
            timeString = hour1 + "小时" + min1 + "分" + s1 + "秒";
        } else {
            timeString = min1 + "分" + s1 + "秒";
        }
        // System.out.println(day + "天" + hour + "小时" + min + "分" + s +
        // "秒");
        return timeString;
    }

    class LightAdapter extends BaseAdapter {
        private ArrayList<String> selectedExceptions;

        public LightAdapter(ArrayList<String> exceptions) {
            this.selectedExceptions = exceptions;
        }

        public void fresh(ArrayList<String> selectExceptions) {
            selectedExceptions = selectExceptions;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return allExceptionTypes.size();
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
            LightViewHolder lightViewHolder;
            if (view == null) {
                view = View.inflate(MesLightActivity.this, R.layout.item_light_exception, null);
                lightViewHolder = new LightViewHolder();
                lightViewHolder.imageView = (ImageView) view.findViewById(R.id.imageview_light);
                lightViewHolder.textView = (TextView) view.findViewById(R.id.textview_light);
                view.setTag(lightViewHolder);
            } else {
                lightViewHolder = (LightViewHolder) view.getTag();
            }
            lightViewHolder.textView.setText(allExceptionTypes.get(i));
            if (selectedExceptions.size() > 0) {    //有异常请求
                for (int j = 0; j < selectedExceptions.size(); j++) {
                    String s = lightViewHolder.textView.getText().toString();
                    if (selectedExceptions.contains(s)) {
                        lightViewHolder.imageView.setSelected(true);
                    } else {
                        lightViewHolder.imageView.setSelected(false);
                    }
                }
            } else {
                lightViewHolder.imageView.setSelected(false);
            }
            return view;
        }
    }

    class LightViewHolder {
        private ImageView imageView;
        private TextView textView;
    }

    class LightListAdapter extends BaseAdapter {
        private DataTable dataTable;

        public LightListAdapter(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        @Override
        public int getCount() {
            return dataTable.Rows.size();
        }

        @Override
        public Object getItem(int i) {
            return dataTable.Rows.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LightListViewHolder lightListViewHolder;
            if (view == null) {
                view = View.inflate(MesLightActivity.this, R.layout.item_listview_light, null);
                lightListViewHolder = new LightListViewHolder();
                lightListViewHolder.textView1 = (TextView) view.findViewById(R.id.text_view_1);
                lightListViewHolder.textView2 = (TextView) view.findViewById(R.id.text_view_2);
                lightListViewHolder.textView3 = (TextView) view.findViewById(R.id.text_view_3);
                lightListViewHolder.textView4 = (TextView) view.findViewById(R.id.text_view_4);
                lightListViewHolder.textView5 = (TextView) view.findViewById(R.id.text_view_5);
                lightListViewHolder.textView6 = (TextView) view.findViewById(R.id.text_view_6);
                lightListViewHolder.textView7 = (TextView) view.findViewById(R.id.text_view_7);
                view.setTag(lightListViewHolder);
            } else {
                lightListViewHolder = (LightListViewHolder) view.getTag();
            }
            //异常名称
            String exception_type = dataTable.Rows.get(i).getValue("exception_type", "");
            //开始时间
            Date create_time = dataTable.Rows.get(i).getValue("create_time", new Date());
            //响应时间
            Date respond_time = dataTable.Rows.get(i).getValue("respond_time", new Date(0));
            //状态
            String status = dataTable.Rows.get(i).getValue("status", "");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String createTime = simpleDateFormat.format(create_time);
            String respondTime = simpleDateFormat.format(respond_time);
            String rep_user = dataTable.Rows.get(i).getValue("rep_user", "");
            String timeDifference = getTimeDifference(create_time, respond_time);  //时间差
            lightListViewHolder.textView1.setText(station);
            lightListViewHolder.textView2.setText(exception_type);
            lightListViewHolder.textView3.setText(createTime);
            lightListViewHolder.textView7.setText(rep_user);
            if (respondTime.startsWith("1970")) {
                lightListViewHolder.textView4.setText("");
                lightListViewHolder.textView5.setText("");
            } else {
                lightListViewHolder.textView4.setText(respondTime);
                lightListViewHolder.textView5.setText(timeDifference);
            }
            lightListViewHolder.textView6.setText(status);
            if ("呼叫".equals(status)) {
                lightListViewHolder.textView1.setTextColor(getResources().getColor(R.color.red));
                lightListViewHolder.textView2.setTextColor(getResources().getColor(R.color.red));
                lightListViewHolder.textView3.setTextColor(getResources().getColor(R.color.red));
                lightListViewHolder.textView4.setTextColor(getResources().getColor(R.color.red));
                lightListViewHolder.textView5.setTextColor(getResources().getColor(R.color.red));
                lightListViewHolder.textView6.setTextColor(getResources().getColor(R.color.red));
                lightListViewHolder.textView7.setTextColor(getResources().getColor(R.color.red));
            } else {
                lightListViewHolder.textView1.setTextColor(getResources().getColor(R.color.black));
                lightListViewHolder.textView2.setTextColor(getResources().getColor(R.color.black));
                lightListViewHolder.textView3.setTextColor(getResources().getColor(R.color.black));
                lightListViewHolder.textView4.setTextColor(getResources().getColor(R.color.black));
                lightListViewHolder.textView5.setTextColor(getResources().getColor(R.color.black));
                lightListViewHolder.textView6.setTextColor(getResources().getColor(R.color.black));
                lightListViewHolder.textView7.setTextColor(getResources().getColor(R.color.black));
            }
            return view;
        }
    }

    class LightListViewHolder {
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private TextView textView4;
        private TextView textView5;
        private TextView textView6;
        private TextView textView7;
    }
}
