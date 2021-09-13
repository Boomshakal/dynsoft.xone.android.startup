package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.event.EventBase;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.FormEncodingBuilder;
import com.uuzuche.lib_zxing.activity.CaptureActivity;

//import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.activity.MesLightActivity;
import dynsoft.xone.android.activity.PreSopActivity;
import dynsoft.xone.android.adapter.MyBaseAdapter;
import dynsoft.xone.android.bean.PreItemBean;
import dynsoft.xone.android.blueprint.PreItemActivity;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.PromptCallback;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.ui.EdittextDrawableClicklistener;
import dynsoft.xone.android.ui.SearchEdittext;
import oracle.jdbc.driver.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;

public class pn_qm_pre_work_onine_v2_editor extends pn_editor {
    private static final int PREREQUESTCODE = 1212121;
    public ButtonTextCell textcell_usercode;
    public ButtonTextCell textcell_taskordercode;
    public ButtonTextCell textcell_work_line;
    public TextCell textcell_lotnumber;
    public TextCell textcell_wh;
    public TextCell textcell_machine;
    public ButtonTextCell textcell_workmethod;
    public TextCell txtMinSize;
    public TextCell txtMaxSize;
    public TextCell txtSize1;
    public TextCell txtSize2;
    public TextCell txtSize3;
    public TextCell txtSize4;
    public TextCell txtSize5;
    private EditText searchEdittext;
    public ListView listview;
    private ImageButton btn_refresh;
    private ArrayList<String> selectedWhs;
    private String itemWh;
    private String processStation = "";       //站位信息，用来检索
    private ArrayList<PreItemBean> itemIDs;
    private int item_count;
    private DataTable items;
    private ArrayList<String> selectedItems;        //选中的物料ID
    private String itemCode;
    private MyListStationAdapter myListStationAdapter;
    private String lot_ids;
    private String pre_lot_number;
    private String qjgIdent;
    private MyWhListAdapter myWhListAdapter;
    private DataTable dataTable;
    private StringBuffer stringSelectedItems;
    private PopupWindow popupWindow;
    private EditText editText;
    private MyItemListAdapter myItemListAdapter;
    private AlertDialog builder;
    private EditText editText1;
    private AlertDialog alertDialog;
    private EditText txt;
    private EditText txtLotNumber;
    private String indexString = "";
    private Boolean isStatus = false;
    private Integer item_id;
    private String work_line;
    private Long methods_head_id;
    private Long methods_item_number;


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;

    public pn_qm_pre_work_onine_v2_editor(Context context) {
        super(context);
    }

    //设置对于的XML文件
    @Override
    public void setContentView() {

        sharedPreferences = App.Current.Workbench.getSharedPreferences("pre_work", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();

        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(
                R.layout.pn_qm_pre_work_online_v2_editor, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        selectedItems = new ArrayList<>();
        stringSelectedItems = new StringBuffer();
        this.textcell_usercode = this.findViewById(R.id.textcell_usercode);
        this.textcell_taskordercode = this.findViewById(R.id.textcell_taskordercode);
        this.textcell_work_line = this.findViewById(R.id.textcell_work_line);
        this.textcell_lotnumber = this.findViewById(R.id.textcell_lotnumber);
//        this.textcell_wh = this.findViewById(R.id.textcell_wh);
//        this.textcell_machine = this.findViewById(R.id.textcell_machine);
        this.textcell_workmethod = this.findViewById(R.id.textcell_workmethod);
//        this.txtMinSize = this.findViewById(R.id.txtMinSize);
//        this.txtMaxSize = this.findViewById(R.id.txtMaxSize);
//        this.txtSize1 = this.findViewById(R.id.txtSize1);
//        this.txtSize2 = this.findViewById(R.id.txtSize2);
//        this.txtSize3 = this.findViewById(R.id.txtSize3);
//        this.txtSize4 = this.findViewById(R.id.txtSize4);
//        this.txtSize5 = this.findViewById(R.id.txtSize5);
        searchEdittext = findViewById(R.id.searchview);
        this.listview = this.findViewById(R.id.listview);
        btn_refresh = findViewById(R.id.btn_refresh);
        btn_refresh.setImageBitmap(App.Current.ResourceManager.getImage("@/core_refresh_white"));
        btn_refresh.setOnClickListener((view) -> {
            refreshWorker("");
        });

        if (searchEdittext != null) {
            searchEdittext.setOnTouchListener((view, event) -> {
                Drawable searchDrawable = searchEdittext.getCompoundDrawables()[2];
                float rawX = event.getRawX();
                if (searchDrawable != null && rawX >= (searchEdittext.getRight() - searchDrawable.getBounds().width())) {
                    String string = searchEdittext.getText().toString();
                    if (TextUtils.isEmpty(string)) {
                        App.Current.toastError(getContext(), "请扫描或者输入数据再搜索");
                    } else {
                        //搜索，重新加载数据
                        refreshWorker(string);
                    }
                }
                return false;
            });
        }

        String code = this.Parameters.get("code", "");
        String user_code = this.Parameters.get("usercode", "");
        item_id = this.Parameters.get("item_id", 0);

        methods_head_id = sharedPreferences.getLong("methods_head_id", 0L);
        methods_item_number = sharedPreferences.getLong("methods_item_number", 0L);

        if (item_id == 0) {
            item_id = sharedPreferences.getInt("item_id", 0);
        }

        if (this.textcell_usercode != null) {
            this.textcell_usercode.setLabelText("打卡工号");
            this.textcell_usercode.setReadOnly();

            textcell_usercode.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_gray"));
            textcell_usercode.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadusercode(textcell_usercode);
                }
            });
            if (TextUtils.isEmpty(user_code)) {
                user_code = sharedPreferences.getString("usercode", "");
            }
            textcell_usercode.setContentText(user_code);
        }

        if (this.textcell_taskordercode != null) {
            this.textcell_taskordercode.setLabelText("生产任务");
            this.textcell_taskordercode.setReadOnly();
            textcell_taskordercode.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_gray"));
            textcell_taskordercode.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadComfirmName(textcell_taskordercode);
                }
            });
            if (TextUtils.isEmpty(code)) {
                code = sharedPreferences.getString("code", "");
            }
            textcell_taskordercode.setContentText(code);
        }

        if (this.textcell_work_line != null) {
            this.textcell_work_line.setLabelText("线别");
            this.textcell_work_line.setReadOnly();
            textcell_work_line.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_gray"));
            textcell_work_line.Button.setOnClickListener(v -> selectwork_line());
            if (TextUtils.isEmpty(work_line)) {
                work_line = sharedPreferences.getString("work_line", "");
            }
            textcell_work_line.setContentText(work_line);
        }

        if (this.textcell_lotnumber != null) {
            this.textcell_lotnumber.setLabelText("条码信息");
            this.textcell_lotnumber.setReadOnly();
        }

//        if (this.textcell_wh != null) {
//            this.textcell_wh.setLabelText("位号");
//            this.textcell_wh.setReadOnly();
//        }
//
//        if (textcell_machine != null) {
//            textcell_machine.setLabelText("机台");
//            textcell_machine.setReadOnly();
//        }
//        if (txtMinSize != null) {
//            txtMinSize.setLabelText("工艺要求最小尺寸");
//        }
//        if (txtMaxSize != null) {
//            txtMaxSize.setLabelText("工艺要求最大尺寸");
//        }
//        if (txtSize1 != null) {
//            txtSize1.setLabelText("首件1");
//        }
//        if (txtSize2 != null) {
//            txtSize2.setLabelText("首件2");
//        }
//        if (txtSize3 != null) {
//            txtSize3.setLabelText("首件3");
//        }
//        if (txtSize4 != null) {
//            txtSize4.setLabelText("首件4");
//        }
//        if (txtSize5 != null) {
//            txtSize5.setLabelText("首件5");
//        }

        if (this.textcell_workmethod != null) {
            this.textcell_workmethod.setLabelText("作业方式");
            this.textcell_workmethod.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_gray"));
            this.textcell_workmethod.setReadOnly();
            textcell_workmethod.Label.setClickable(true);

            textcell_workmethod.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseworkmethod();
                }
            });

            this.textcell_workmethod.Label.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    App.Current.Workbench.scanByCamera();
                    sendByOKHttp();
                }
            });
        }

        refreshWorker("");
    }

    private void sendByOKHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = "http://192.168.1.159:5000/code";

                FormEncodingBuilder builder = new FormEncodingBuilder();
                builder.add("Secret", "GEZVMQJTHEYDSVBU");

                Request request = new Request.Builder().url(url).post(builder.build()).build();
//                Request request = new Request.Builder().url(url).build();
                try {
                    Response response = client.newCall(request).execute();//发送请求
                    String result = response.body().string();
                    Log.e("len", result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 选择线体
     */
    private void selectwork_line() {
        String sql = "exec p_get_work_line_v1";
        Parameters p = new Parameters();
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                    App.Current.playSound(R.raw.error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {

                    multiChoiceDialog_work_line(value.Value);
                } else {
                    App.Current.toastError(getContext(), "没有维护作业方式。");
                    App.Current.playSound(R.raw.error);
                }
            }
        });
    }

    private void multiChoiceDialog_work_line(final DataTable dataTable) {
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
                    edit.putString("work_line", name);
                    edit.commit();
                    textcell_work_line.setContentText(name);
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(getContext()).setTitle("请选择")
                .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                        (textcell_workmethod.TextBox.getText().toString()), listener)
                .setNegativeButton("取消", null).show();
    }

    private void chooseworkmethod() {    //选择作业方式
        String sql = "exec p_get_workmethod_v1 ?";
        Parameters p = new Parameters();
        p.add(1, item_id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                    App.Current.playSound(R.raw.error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {

                    multiChoiceDialog(value.Value);
                } else {
                    App.Current.toastError(getContext(), "没有维护作业方式。");
                    App.Current.playSound(R.raw.error);
                }
            }
        });
    }

    private void multiChoiceDialog(final DataTable dataTable) {
        ArrayList<String> codes = new ArrayList<String>();
        for (DataRow dataRow : dataTable.Rows) {
            String code = dataRow.getValue("code", "");
            Log.e("len", "code : " + code);
            codes.add(code);
        }
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    String code = dataTable.Rows.get(which).getValue("code", "");
                    methods_head_id = dataTable.Rows.get(which).getValue("id", 0L);
                    edit.putLong("methods_head_id", methods_head_id);
                    edit.commit();
                    chooseworkmethod_item(code);
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(getContext()).setTitle("请选择")
                .setSingleChoiceItems(codes.toArray(new String[0]), codes.indexOf
                        (textcell_workmethod.TextBox.getText().toString()), listener)
                .setNegativeButton("取消", null).show();
    }

    private void chooseworkmethod_item(String processStation) {    //选择明细
        String sql = "exec fm_pre_get_process_methods_by_station_test_v1 ?,?";
        Parameters p = new Parameters().add(1, processStation).add(2, item_id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                } else {
                    if (value.Value != null) {
                        multiChoiceDialog_method_item(value.Value, processStation);
                    } else {
                        App.Current.toastError(getContext(), "没有维护数据");
                    }
                }
            }
        });
    }

    private void multiChoiceDialog_method_item(final DataTable dataTable, String processStation) {
        ArrayList<String> codes = new ArrayList<String>();
        for (DataRow dataRow : dataTable.Rows) {
            String processing_methods = dataRow.getValue("processing_methods", "");
            Log.e("len", "processing_methods : " + processing_methods);
            codes.add(processing_methods);
        }
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    String processing_methods = dataTable.Rows.get(which).getValue("processing_methods", "");
                    String type_station = dataTable.Rows.get(which).getValue("type_station", "");
                    methods_item_number = dataTable.Rows.get(which).getValue("number", 0L);
                    edit.putLong("methods_item_number", methods_item_number);
                    edit.commit();
                    onlineWorker(textcell_usercode.getContentText(), "", type_station + "-" + processing_methods, processStation);
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(getContext()).setTitle("请选择")
                .setSingleChoiceItems(codes.toArray(new String[0]), codes.indexOf
                        (textcell_workmethod.TextBox.getText().toString()), listener)
                .setNegativeButton("取消", null).show();
    }

    private void loadComfirmName(final ButtonTextCell textcell_taskordercode) {
        Link link = new Link("pane://x:code=pn_qm_and_pre_parameter_mgr");
//        link.Parameters.add("textcell", textcell_1);
        link.Open(null, getContext(), null);
        this.close();
    }

    private void loadusercode(final ButtonTextCell textcell_taskordercode) {
        Link link = new Link("pane://x:code=pn_qm_and_pre_user_mgr");
//        link.Parameters.add("textcell", textcell_1);
        link.Open(null, getContext(), null);
        this.close();
    }

    //扫条码
    @Override
    public void onScan(final String barcode) {
        if (!isStatus) {
            if (barcode.startsWith("M0")) {
                if (builder != null && builder.isShowing()) {
                    editText1.setText(barcode.replace("\n", "").trim());
                } else {
                    textcell_usercode.setContentText(barcode.replace("\n", "").trim());
                }
                edit.putString("usercode", barcode.trim());
                edit.commit();

            } else if (barcode.startsWith("WO:")) {
                if (TextUtils.isEmpty(textcell_usercode.getContentText())) {
                    App.Current.toastError(getContext(), "请先扫描打卡人员工号");
                    return;
                }
                String substring = barcode.replace("\n", "").substring(3);
                textcell_taskordercode.setContentText(substring);
            } else if (barcode.startsWith("MO")) {
                if (TextUtils.isEmpty(textcell_usercode.getContentText())) {
                    App.Current.toastError(getContext(), "请先扫描打卡人员工号");
                    return;
                }
//                String substring = barcode.substring(0, 15);
                textcell_taskordercode.setContentText(barcode.trim());
            } else if (barcode.startsWith("e") || barcode.startsWith("E")) {
                String taskCode = barcode.split("-").length > 2 ? barcode.split("-")[0] + "-" +
                        barcode.split("-")[1] : barcode;

                textcell_taskordercode.setContentText(taskCode);
                return;
            }
//            else if (barcode.startsWith("CRQ:") || barcode.startsWith("CQR:")) {
//                if (TextUtils.isEmpty(textcell_taskordercode.getContentText())) {
//                    String itemCode = "";
//                    if (barcode.startsWith("CRQ:") || barcode.startsWith("CQR:")) {
//                        String[] split = barcode.split("-");
//                        String s = split[1];
//
//                        itemCode = s;
//
//                    } else {
//                        itemCode = barcode.replace("\n", "").trim();
//                    }
//                    searchEdittext.setText(itemCode);
//                    refreshWorker(itemCode);
//                } else {
//                    if (popupWindow != null && popupWindow.isShowing()) {
//                        //检查数据是否正确
//                        String[] split = barcode.substring(4).split("-");
//                        if (split.length > 3) {
//                            String lot_number = split[0];
//                            String item_code = split[1];
//                            String date_code = split[3].replace("\n", "").trim();
//                            PreItemBean preItemBean = new PreItemBean(item_code, date_code);
//                            if (itemIDs != null && itemIDs.size() == 0) {
//                                checkItemMessage(item_code, date_code, qjgIdent, myItemListAdapter, popupWindow);
//                                itemIDs.add(preItemBean);
//                            } else {
//                                itemIDs.add(preItemBean);
//                                myItemListAdapter.frash(itemIDs);
//                            }
//                            if (itemIDs.size() == item_count) {
//                                commitDatas(qjgIdent, popupWindow, myItemListAdapter);
//                            }
//                        } else {
//                            App.Current.showError(getContext(), "扫描有误，请重新扫描！");
//                        }
//                    } else {
//                        String coverPlate = barcode.substring(4).replace("\n", "");
//                        String[] split = coverPlate.split("-");
//                        String lot_number = split[0];
//                        String s = split[1];
//                        String quantity = split[2];
//                        if (split.length < 2) {
//                            App.Current.toastError(getContext(), "扫描有误，请检查！");
//                        }
//                        if (s.startsWith("QJG")) {
//                            textcell_lotnumber.setContentText(coverPlate);
//                            pre_lot_number = split[0];
//                            qjgIdent = split[1];
//                            popupwindowShowItems(split[0]);
//                        } else {
//                            String itemCode = s;
//                            Map<String, String> headMap = new HashMap<>();
//                            headMap.put("quantity", quantity);
//                            headMap.put("item_code", itemCode);
//                            headMap.put("lot_number", lot_number);
//                            headMap.put("task_order_code", textcell_taskordercode.getContentText());
//                            String xml = XmlHelper.createXml("head", headMap, "items", "item", null);
//                            checkItemCode(xml, coverPlate);
//                            lot_ids = coverPlate;
//                            this.itemCode = itemCode;
//                            stringSelectedItems.append(itemCode);
//                            selectedItems.add(itemCode);
//                        }
//                    }
//                }
//            }
            else if (barcode.startsWith("FA:")) {
                String machineCode = barcode.substring(3).replace("\n", "").trim();
                checkMachine(machineCode);
            } else if (barcode.startsWith("GZ:") || barcode.startsWith("B40")) {
                String machineCode = barcode.startsWith("GZ:") ? barcode.substring(3).replace("\n", "").trim() : barcode.replace("\n", "").trim();

                checkMachine(machineCode);
            } else if (barcode.startsWith("S:")) {
                final String SeqCode = barcode.replace("\n", "").substring(2);
                //选择加工方式
                chooseProcessingMethod(SeqCode);

            } else {
                App.Current.toastError(getContext(), "扫描的条码有误，请检查！");
            }
        } else {
            txtLotNumber.setText(barcode.substring(4));
        }
    }

    private String CheckFristValue() {
        String result = "";
        if (TextUtils.isEmpty(txtMinSize.getContentText()) ||
                TextUtils.isEmpty(txtMaxSize.getContentText())) {
            result = "请输入工艺要求最大和最小尺寸";
        } else if (!(txtMinSize.getContentText().matches("^[0-9]*$") || txtMinSize.getContentText().contains("."))) {
            result = "最小尺寸请输入有效值";
        } else if (!(txtMaxSize.getContentText().matches("^[0-9]*$") || txtMaxSize.getContentText().contains("."))) {
            result = "最大尺寸请输入有效值";
        } else {
            if (TextUtils.isEmpty(txtSize1.getContentText()) ||
                    TextUtils.isEmpty(txtSize2.getContentText()) ||
                    TextUtils.isEmpty(txtSize3.getContentText()) ||
                    TextUtils.isEmpty(txtSize4.getContentText()) ||
                    TextUtils.isEmpty(txtSize5.getContentText())) {
                result = "必须填满5个首件尺寸";
            } else if (!(txtSize1.getContentText().matches("^[0-9]*$") || txtSize1.getContentText().contains(".")) ||
                    !(txtSize2.getContentText().matches("^[0-9]*$") || txtSize2.getContentText().contains(".")) ||
                    !(txtSize3.getContentText().matches("^[0-9]*$") || txtSize3.getContentText().contains(".")) ||
                    !(txtSize4.getContentText().matches("^[0-9]*$") || txtSize4.getContentText().contains(".")) ||
                    !(txtSize5.getContentText().matches("^[0-9]*$") || txtSize5.getContentText().contains("."))) {
                result = "请输入有效值";
            } else if (txtSize1.getContentText().compareTo(txtMaxSize.getContentText()) > 0 ||
                    txtSize1.getContentText().compareTo(txtMinSize.getContentText()) < 0) {
                result = "首件1的尺寸不在范围内，请重新输入";
            } else if (txtSize2.getContentText().compareTo(txtMaxSize.getContentText()) > 0 ||
                    txtSize2.getContentText().compareTo(txtMinSize.getContentText()) < 0) {
                result = "首件2的尺寸不在范围内，请重新输入";
            } else if (txtSize3.getContentText().compareTo(txtMaxSize.getContentText()) > 0 ||
                    txtSize3.getContentText().compareTo(txtMinSize.getContentText()) < 0) {
                result = "首件3的尺寸不在范围内，请重新输入";
            } else if (txtSize4.getContentText().compareTo(txtMaxSize.getContentText()) > 0 ||
                    txtSize4.getContentText().compareTo(txtMinSize.getContentText()) < 0) {
                result = "首件4的尺寸不在范围内，请重新输入";
            } else if (txtSize5.getContentText().compareTo(txtMaxSize.getContentText()) > 0 ||
                    txtSize5.getContentText().compareTo(txtMinSize.getContentText()) < 0) {
                result = "首件5的尺寸不在范围内，请重新输入";
            }
        }

        return result;
    }

    private void checkMachine(String machineCode) {
        String sql = "";
        Parameters p;
        if (textcell_workmethod.getContentText().contains("散热器")) {
            sql = "exec fm_check_radiator_machine_status ?,?,? ";
            p = new Parameters().add(1, machineCode).add(2, textcell_taskordercode.getContentText()).add(3, textcell_wh.getContentText());
        } else {
            sql = "exec fm_check_machine_status ?";
            p = new Parameters().add(1, machineCode);
        }
        App.Current.DbPortal.ExecuteNonQueryAsync(Connector, sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                Result<Integer> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                } else {
                    if (alertDialog != null && alertDialog.isShowing()) {
                        if (txt != null) {
                            textcell_machine.setContentText(machineCode);
                            txt.setText(machineCode);
                        } else {
                            App.Current.toastError(getContext(), "有问题，请重新打卡。");
                        }
                    } else if (TextUtils.isEmpty(textcell_workmethod.getContentText())) {
                        App.Current.toastError(getContext(), "请先扫描加工方式！");
                    } else {
                        App.Current.toastError(getContext(), "有问题，请您重新打卡。");
                    }
                }
            }
        });
    }

    private void popupwindowShowItems(String qjgIdent) {
        setRefCode();
        itemIDs = new ArrayList<>();
        popupWindow = new PopupWindow();
        View view = View.inflate(getContext(), R.layout.item_preonline_popup_2, null);
        editText = view.findViewById(R.id.edittext);
        ListView listView = view.findViewById(R.id.listview);
        myItemListAdapter = new MyItemListAdapter(itemIDs);
        listView.setAdapter(myItemListAdapter);
        WindowManager windowManager = App.Current.Workbench.getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        popupWindow.setContentView(view);
        popupWindow.setWidth(width);
        popupWindow.setHeight(height / 2);
        popupWindow.setBackgroundDrawable(new ShapeDrawable());
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(textcell_usercode, Gravity.CENTER, 0, 0);
        editText.setOnKeyListener((view1, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String itemCode = editText.getText().toString().replace("\n", "");
                editText.setText("");
                if (itemCode.startsWith("CRQ:")) {
                    //检查数据是否正确
                    String[] split = itemCode.substring(4).split("-");
                    if (split.length > 3) {
                        String lot_number = split[0];
                        String item_code = split[1];
                        String date_code = split[3];
                        PreItemBean preItemBean = new PreItemBean(item_code, date_code);

                        if (itemIDs != null && itemIDs.size() == 0) {
                            checkItemMessage(item_code, date_code, qjgIdent, myItemListAdapter, popupWindow);
                            itemIDs.add(preItemBean);
                        } else {
                            itemIDs.add(preItemBean);
                            myItemListAdapter.frash(itemIDs);
                        }

                        if (itemIDs.size() == item_count) {
                            commitDatas(qjgIdent, popupWindow, myItemListAdapter);
                        }

                    } else {
                        App.Current.showError(getContext(), "扫描有误，请重新扫描！");
                    }

                } else {
                    App.Current.showError(getContext(), "扫描有误，请重新扫描！当前扫描的并非物料条码");
                }
            }
            return false;
        });
    }

    private void commitDatas(String qjgIdent, PopupWindow popupWindow, MyItemListAdapter myItemListAdapter) {
        ArrayList<Map<String, String>> itemMaps = new ArrayList<>();
        for (int i = 0; i < itemIDs.size(); i++) {
            Map<String, String> itemMap = new HashMap<>();
            itemMap.put("item_code", itemIDs.get(i).getItem_code());
            itemMap.put("date_code", itemIDs.get(i).getDate_code());
            itemMaps.add(itemMap);
        }
        String xml = XmlHelper.createXml("head", null, "items", "item", itemMaps);
        String sql = "exec fm_pre_datas_commit ?,?";
        Parameters p = new Parameters().add(1, qjgIdent).add(2, xml);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    itemIDs.removeAll(itemIDs);
                    myItemListAdapter.frash(itemIDs);
                } else {
                    if (value.Value.Rows != null && value.Value.Rows.size() > 0) {
                        items = new DataTable();
                        for (int i = 0; i < value.Value.Rows.size(); i++) {
                            items.Rows.add(value.Value.Rows.get(i));
                            selectedItems.add(value.Value.Rows.get(i).getValue("item_code", ""));
                        }
                        chooseWh();
                        popupWindow.dismiss();
                    } else {
                        App.Current.showError(getContext(), "没有返回数据");
                        itemIDs.removeAll(itemIDs);
                        myItemListAdapter.frash(itemIDs);
                    }
                }
            }
        });
    }

    private void checkItemMessage(String item_code, String date_code, String qjgIdent, MyItemListAdapter myItemListAdapter, PopupWindow popupWindow) {
        String sql = "exec fm_get_qjg_item_count ?,?,?,?";
        Parameters p = new Parameters().add(1, item_code).add(2, date_code).add(3, qjgIdent).add(4, textcell_taskordercode.getContentText());
        Log.e("len", item_code + "," + date_code + "," + qjgIdent + "," + textcell_taskordercode.getContentText());
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    itemIDs.removeAll(itemIDs);
                    popupWindow.dismiss();
                } else {
                    if (value.Value != null) {
                        item_count = value.Value.getValue("item_count", 0);
                        PreItemBean preItemBean = new PreItemBean(item_code, date_code);
                        myItemListAdapter.frash(itemIDs);
                    } else {
                        App.Current.showError(getContext(), "没有查询到当前工单当前物料的信息，请检查是否扫描有误");
                    }
                }
            }
        });
    }

    private void checkItemCode(String xml, String coverPlate) {
        String sql = "exec fm_pre_get_item_message_by_code_v1 ?";
        Parameters p = new Parameters().add(1, xml);
        Log.e("len", "XML ; " + xml);
        App.Current.DbPortal.ExecuteRecordAsync(Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                } else {
                    if (value.Value != null) {
                        textcell_lotnumber.setContentText(coverPlate);
                        chooseWh();
                    } else {
                        App.Current.toastError(getContext(), "您扫描的物料有问题：" + itemCode);
                    }
                }
            }
        });
    }

    private void chooseWh() {
        final PopupWindow popupWindow = new PopupWindow();
//        selectedItems = new ArrayList<>();
        selectedWhs = new ArrayList<>();
        View view = View.inflate(getContext(), R.layout.item_preonline_popup, null);
        final EditText editText = (EditText) view.findViewById(R.id.edittext);
        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        final ListView listView = (ListView) view.findViewById(R.id.listview);
        editText.setHint("请输入位号查询");
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    String trim = editText.getText().toString().trim();
                    if (trim.startsWith("CRQ:") && trim.contains("-")) {     //获取扫描后的物料编码
                        itemWh = trim.split("-")[1].trim();
                    } else if (trim.startsWith("R:")) {
                        itemWh = trim.substring(2);
                    } else {
                        itemWh = trim;
                    }
                    editText.setText(itemWh);
                    afterWhKeyListener(listView);
                    return true;
                }
                return false;
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取已经选中的物料，隐藏popup，清空selecteditems
                StringBuffer stringSelectedWhs = new StringBuffer();
                for (int i = 0; i < selectedWhs.size(); i++) {
                    if (i == selectedWhs.size() - 1) {
                        stringSelectedWhs.append(selectedWhs.get(i));
                    } else {
                        stringSelectedWhs.append(selectedWhs.get(i) + ",");
                    }
                }

                if (TextUtils.isEmpty(textcell_wh.getContentText()))
                    textcell_wh.setContentText(stringSelectedWhs.toString());
                selectedWhs.removeAll(selectedWhs);
                popupWindow.dismiss();
//                //选择加工方式
//                chooseProcessingMethod();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏popup，清空selecteditems
                popupWindow.dismiss();
                selectedWhs.removeAll(selectedWhs);
            }
        });
        afterWhKeyListener(listView);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);
        Display defaultDisplay = App.Current.Workbench.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        popupWindow.setWidth(width);
        popupWindow.setHeight(height - (height / 5));
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(listView, Gravity.CENTER, 0, 0);
        //listview点击事件，选中与非选中
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view.findViewById(R.id.textview_1);
                String trim = textView.getText().toString().trim();
                if (selectedWhs.contains(trim)) {
                    selectedWhs.remove(trim);
                } else {
                    selectedWhs.add(trim);
                }
                myWhListAdapter.frash(selectedWhs);
            }
        });
    }

    private void afterWhKeyListener(final ListView listView) {
        StringBuffer stringSelectedItems = new StringBuffer();
        String sql = "exec fm_get_wh_by_item_code ?,?,?";
        for (int i = 0; i < selectedItems.size(); i++) {
            stringSelectedItems.append(selectedItems.get(i));
            stringSelectedItems.append(",");
        }
        Parameters p = new Parameters().add(1, textcell_taskordercode.getContentText()).add(2, stringSelectedItems.toString()).add(3, itemWh);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {

            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                } else {
                    if (value.Value != null) {
                        ArrayList<String> refWhs = new ArrayList<>();
                        String ref_wh = value.Value.getValue("ref_wh", "");
                        if (ref_wh.contains(",")) {
                            String[] split = ref_wh.split(",");
                            for (int i = 0; i < split.length; i++) {
                                refWhs.add(split[i]);
                            }
                        } else {
                            refWhs.add(ref_wh);
                        }
                        myWhListAdapter = new MyWhListAdapter(refWhs);
                        listView.setAdapter(myWhListAdapter);
                    }
                }
            }
        });
    }

    /**
     * 选择加工站位和方式
     *
     * @param SeqCode 工序编号
     */
    private void chooseProcessingMethod(String SeqCode) {
        final PopupWindow popupWindow = new PopupWindow();
        StringBuffer stringSelectedProcess = new StringBuffer();
        ArrayList<String> selectedProcess = new ArrayList<>();
        ArrayList<String> selectedListItems = new ArrayList<>();
        View view = View.inflate(getContext(), R.layout.item_preonline_station_popup, null);
        final TextCell txt_SeqCode_cell = (TextCell) view.findViewById(R.id.txt_SeqCode_cell);
        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        TextView watch_sop = (TextView) view.findViewById(R.id.watch_sop);
        watch_sop.setVisibility(View.VISIBLE);
        final ListView listView = (ListView) view.findViewById(R.id.listview);
        if (txt_SeqCode_cell != null) {
            txt_SeqCode_cell.setLabelText("加工站别");
            txt_SeqCode_cell.setReadOnly();
            txt_SeqCode_cell.setContentText(SeqCode);
        }
        processStation = SeqCode;
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果没有选，不让进行下一步
                if (selectedProcess != null && selectedProcess.size() < 1) {
                    App.Current.toastError(getContext(), "请先选择加工方式！");
                } else {
                    //获取已经选中的站位，隐藏popup，清空selecteditems
                    StringBuffer stringSelectedProcess = new StringBuffer();
                    for (int i = 0; i < selectedProcess.size(); i++) {
                        if (i == selectedProcess.size() - 1) {
                            stringSelectedProcess.append(selectedProcess.get(i));
                        } else {
                            stringSelectedProcess.append(selectedProcess.get(i) + ",");
                        }
                    }
                    selectedProcess.removeAll(selectedProcess);
                    textcell_workmethod.setContentText(stringSelectedProcess.toString());
                    popupWindow.dismiss();
                    //选择加工方式
//                chooseProcessingMethods(cardNumber);
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < selectedItems.size(); i++) {
                        stringBuffer.append(selectedItems.get(i) + ",");
                    }
                    Log.e("WBBB", stringSelectedProcess.toString());
                    if (stringSelectedProcess.toString().contains("机加工") ||
                            stringSelectedProcess.toString().contains("散热器")) {
                        txt = new EditText(getContext());
                        txt.setHint("请扫描机台");
                        alertDialog = new AlertDialog.Builder(getContext())
                                .setTitle("请扫描")
                                .setView(txt)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (TextUtils.isEmpty(textcell_machine.getContentText().toString())) {
                                            App.Current.toastError(getContext(), " 机台不能为空 ");
                                        } else {
                                            textcell_machine.setContentText(txt.getText().toString());
                                            //上线
                                            onlineWorker(textcell_usercode.getContentText(), stringBuffer.toString(), stringSelectedProcess.toString(), SeqCode);
                                            //触发首件
//                                            startFirstItem(stringSelectedProcess.toString());
                                        }
                                    }
                                }).setNegativeButton("取消", null)
                                .show();
                    } else {
                        //上线
                        Log.e("len", "Select : " + selectedItems.toString());
                        onlineWorker(textcell_usercode.getContentText(), stringBuffer.toString(), stringSelectedProcess.toString(), SeqCode);
                        //触发首件
//                        startFirstItem(stringSelectedProcess.toString());
                    }

                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏popup，清空selecteditems
                popupWindow.dismiss();
                selectedProcess.removeAll(selectedProcess);
            }
        });

        watch_sop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(App.Current.Workbench, PreSopActivity.class);
                intent.putExtra("task_order_code", textcell_taskordercode.getContentText());
                App.Current.Workbench.startActivity(intent);
            }
        });
        afterStationKeyListener(listView);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);
        Display defaultDisplay = App.Current.Workbench.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        popupWindow.setWidth(width);
        popupWindow.setHeight(height - (height / 5));
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(listView, Gravity.CENTER, 0, 0);
        //listview点击事件，选中与非选中
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView1 = (TextView) view.findViewById(R.id.textview_1);
                TextView textView3 = (TextView) view.findViewById(R.id.textview_3);
                String trim1 = textView1.getText().toString().trim();
                String trim3 = textView3.getText().toString().trim();
                String trim = trim1 + "-" + trim3;

                if (selectedProcess.contains(trim)) {
                    selectedProcess.remove(trim);
                } else {
                    selectedProcess.add(trim);
                }
                if (selectedListItems.contains(String.valueOf(i))) {
                    selectedListItems.remove(String.valueOf(i));
                } else {
                    selectedListItems.add(String.valueOf(i));
                }
                myListStationAdapter.frash(selectedListItems);
            }
        });
    }

    /**
     * @param cardNumber     员工打卡工号
     * @param item_ids       加工物料ID集
     * @param processMethods 加工方式集
     */
    private void onlineWorker(String cardNumber, String item_ids, String processMethods, String seqCode) {
        Time time = new Time();
        time.setToNow();
        int hour = time.hour;
        Map<String, String> headMap = new HashMap<>();
        headMap.put("task_order_code", textcell_taskordercode.getContentText());
        headMap.put("work_line", textcell_work_line.getContentText());
        headMap.put("sequence_code", seqCode);
        headMap.put("worker_code", cardNumber);
        headMap.put("work_shift", hour > 18 ? "夜班" : "白班");
        headMap.put("work_station", " ");
        headMap.put("work_device", "一体机");
        headMap.put("department", "1014");
        headMap.put("work_leader", "3993");
        headMap.put("user_id", App.Current.UserID);
        headMap.put("item_ids", item_ids);
        headMap.put("methods_head_id", methods_head_id.toString());
        headMap.put("methods_item_number", methods_item_number.toString());
        headMap.put("process_methods", processMethods);
        headMap.put("mac_address", App.Current.getMacAddress());
//        headMap.put("ref_wh", textcell_wh.getContentText());
        headMap.put("pre_code", qjgIdent);
        headMap.put("pre_lot_number", pre_lot_number);
//        headMap.put("machine_code", textcell_machine.getContentText());
        headMap.put("lot_ids", lot_ids);
//        headMap.put("minsize", txtMinSize.getContentText());
//        headMap.put("maxsize", txtMaxSize.getContentText());
//        headMap.put("size1", txtSize1.getContentText());
//        headMap.put("size2", txtSize2.getContentText());
//        headMap.put("size3", txtSize3.getContentText());
//        headMap.put("size4", txtSize4.getContentText());
//        headMap.put("size5", txtSize5.getContentText());
        String xml = XmlHelper.createXml("head", headMap, "items", "item", null);
        Log.e("len", xml);

        String sql = "exec fm_sign_on_pre_mac_wh_work_time_v1 ?";
        Parameters p = new Parameters().add(1, xml);
        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                Result<Integer> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), "Online" + value.Error);
                    clear();
                    return;
                }
                if (value.Value != null) {
//                    Integer id = value.Value.getValue("id", 0);
                    App.Current.toastInfo(getContext(), "上线打卡成功");
                    lot_ids = "";
                    clear();
                    refreshWorker("");   //刷新，获取当前打卡集合
                } else {
                    clear();
                }
            }
        });
    }

    private void get_Lot_number_Sn(String lot_number, String item_code, String qty, String DateCode) {
        Map<String, String> headMap = new HashMap<>();
        headMap.put("lot_number", lot_number);
        headMap.put("quantity", qty);
        headMap.put("item_code", item_code);
        headMap.put("lot_ids", lot_number + "-" + item_code + "-" + qty + "-" + DateCode);
        String xml = XmlHelper.createXml("head", headMap, "items", "item", null);
        String sql = "exec fm_get_wo_issue_lot_number_sn ?";
        Parameters p = new Parameters().add(1, xml);
        Result<DataRow> r = App.Current.DbPortal.ExecuteRecord("core_and", sql, p);
        if (r.HasError) {
            App.Current.toastError(getContext(), r.Error);
            return;
        } else {
            if (r.Value != null) {

                indexString = r.Value.getValue("index_string", "");
            }
        }
    }

    private void setRefCode() {
        String sql = "select t0.ref from fm_pre_item_control_head t0 where t0.code = ?";
        Parameters p = new Parameters().add(1, qjgIdent);
        Result<DataRow> dataRowResult = App.Current.DbPortal.ExecuteRecord(Connector, sql, p);
        if (dataRowResult.HasError) {
            App.Current.showError(getContext(), dataRowResult.Error);
            return;
        }

        if (dataRowResult.Value != null) {
            textcell_wh.setContentText(dataRowResult.Value.getValue("ref", ""));
        } else {
            App.Current.toastError(getContext(), "该虚拟料号不存在");
        }
    }

    private void refreshWorker(String search) {
        String sql = "exec p_fm_pre_work_sign_get_item_list ?,?,?,?";
        Parameters p = new Parameters().add(1, App.Current.getMacAddress()).add(2, textcell_work_line.getContentText()).add(3, 15).add(4, search);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), "Refresh" + value.Error);
                    clear();
                    return;
                }
                if (value.Value != null) {
                    dataTable = value.Value;
                    PreWorkOnlineAdapter adapter = new PreWorkOnlineAdapter(getContext(), value.Value);
                    listview.setAdapter(adapter);
                    listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String seq_code = dataTable.Rows.get(i).getValue("seq_code", "");
                            if (seq_code.equals("M205") || seq_code.equals("M206")) {
                                isStatus = true;
                                txtLotNumber = new EditText(getContext());
                                txtLotNumber.setHint("请扫描条码");
                                alertDialog = new AlertDialog.Builder(getContext())
                                        .setTitle("请扫描")
                                        .setView(txtLotNumber)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String sql = "insert into fm_pre_print_record (sign_id,lots) VALUES(?,?)";
                                                Parameters p = new Parameters().add(1, dataTable.Rows.get(i).getValue("id", 0L)).add(2, String.valueOf(txtLotNumber.getText()));
                                                App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                                                    @Override
                                                    public void handleMessage(Message msg) {
                                                        Result<Integer> value = Value;
                                                        if (value.HasError) {
                                                            App.Current.toastError(getContext(), value.Error);
                                                        } else {
                                                            if (value.Value > 0) {
                                                                String[] lot_split = String.valueOf(txtLotNumber.getText()).split("-");
                                                                print(dataTable, i, String.valueOf(txtLotNumber.getText()), lot_split[2]);
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        }).setNegativeButton("取消", null)
                                        .show();
                            } else {
                                App.Current.prompt(getContext(), "请输入", "请输入数量", new PromptCallback() {
                                    @Override
                                    public void onReturn(String result) {
                                        print(dataTable, i, "", result);
                                    }
                                });

                            }
                            return false;
                        }
                    });
                }
            }
        });
    }

    private void print(DataTable dataTable, int i, String lot_number, String result) {
        Log.e("WBBB", result);
        long worker_sign_id = dataTable.Rows.get(i).getValue("id", 0L);
        String task_order_code = dataTable.Rows.get(i).getValue("task_order_code", "");
        int plan_quantity = dataTable.Rows.get(i).getValue("plan_quantity", new BigDecimal(0)).intValue();
        Map<String, String> headMap = new HashMap<>();
        headMap.put("worker_sign_id", worker_sign_id + "");
        headMap.put("input_number", result);
        headMap.put("lot_number", lot_number);
        String xml = XmlHelper.createXml("head", headMap, "items", "item", null);
        Log.e("WBBB", xml);
        String sql = "exec get_label_message ?";
        Parameters p = new Parameters().add(1, xml);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                } else {
                    if (value.Value.Rows != null && value.Value.Rows.size() > 0) {
                        //获取位号
                        int organization_id = value.Value.Rows.get(0).getValue("organization_id", 0);
                        int top_item_id = value.Value.Rows.get(0).getValue("top_item_id", 0);
                        Connection conn = null;
                        CallableStatement stmt;
                        ArrayList<Map<String, String>> itemArray = new ArrayList<>();
                        ArrayList<String> itemMessages = new ArrayList<>();
                        for (int j = 0; j < value.Value.Rows.size(); j++) {
                            String item_code = value.Value.Rows.get(j).getValue("item_code", "");
                            String date_code = value.Value.Rows.get(j).getValue("date_code", "");
                            int quantity = value.Value.Rows.get(j).getValue("quantity", new BigDecimal(0)).intValue();
                            BigDecimal quantity_per_assembly = value.Value.Rows.get(j).getValue("quantity_per_assembly", new BigDecimal(0));
                            itemMessages.add(item_code + " " + date_code + " " + (Integer.valueOf(result) * quantity_per_assembly.intValue()));
                        }
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int weekYear = calendar.get(Calendar.WEEK_OF_YEAR);
                        String date_code = "";
                        String type = value.Value.Rows.get(0).getValue("type", "");
                        if ("SINGLE".equals(type)) {
                            date_code = value.Value.Rows.get(0).getValue("date_code", "");
                        } else {
                            date_code = String.valueOf(year).substring(2) + String.valueOf(weekYear);
                        }
                        int onscan_quantity = value.Value.Rows.get(0).getValue("onscan_quantity", 0);
                        String pre_code = value.Value.Rows.get(0).getValue("pre_code", "");
                        String lot_number = value.Value.Rows.get(0).getValue("lot_number", "");
                        String top_item_name = value.Value.Rows.get(0).getValue("top_item_name", "");
                        String user_message = value.Value.Rows.get(0).getValue("user_message", "");
                        String ref = value.Value.Rows.get(0).getValue("ref", "");
                        get_Lot_number_Sn(lot_number, pre_code, result, date_code); //生产批次号的序列号
                        Intent intent = new Intent(getContext(), PreItemActivity.class);
                        intent.putExtra("task_order_code", task_order_code);
                        intent.putExtra("plan_quantity", plan_quantity + "");
                        intent.putExtra("cur_quantity", result);
                        intent.putExtra("lot_number", lot_number);
                        intent.putExtra("number", indexString);
                        intent.putExtra("date_code", date_code);
                        intent.putExtra("top_item_name", top_item_name);
                        intent.putExtra("radiatorWh", ref);
                        intent.putExtra("pre_item_code", pre_code);
                        intent.putExtra("user_message", user_message);
                        intent.putStringArrayListExtra("itemMessages", itemMessages);
                        App.Current.Workbench.startActivity(intent);
                    } else {
                        App.Current.toastError(getContext(), "没有数据");
                    }
                }
            }
        });
    }

    private void loadItemFromOracle(final int org_id, final long item_id) {
        Connection conn = null;
        CallableStatement stmt;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@10.3.1.55:1609/PROD1", "apps", "apps");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String sql = "{call pkg_getrecord.p_rtn_bomdtlpz(?,?,?,?)}";
            stmt = conn.prepareCall(sql);
            stmt.setInt(1, (int) item_id);
            stmt.setInt(2, org_id);
            stmt.setInt(3, 30);

            stmt.registerOutParameter(4, OracleTypes.CURSOR);
            stmt.execute();
            ResultSet cursor = ((OracleCallableStatement) stmt).getCursor(4);
            ResultSetMetaData metaData = cursor.getMetaData();
            int columnCount = metaData.getColumnCount();
            Map<String, String> headMap = new HashMap<>();
//            headMap.put("task_order_code", textCell2.getContentText().trim());
//            headMap.put("first_item_code", textCell1.getContentText().trim());
            ArrayList<Map<String, String>> itemArray = new ArrayList<>();
            ;
            while (cursor.next()) {
                Map<String, String> item = new HashMap<>();
                String item_code = cursor.getString("ITEM_CODE");
                String item_name = cursor.getString("ITEM_NAME");
                String bom_purpose = cursor.getString("BOM_PURPOSE");
                String sub_item = cursor.getString("SUB_ITEM");
                String ref_wh = cursor.getString("REF_WH");
                String change_notice = cursor.getString("CHANGE_NOTICE");
                String key_id = cursor.getString("KEYID");
                String parent_id = cursor.getString("PATENTID");
                String unid = cursor.getString("UNID");
                int component_quantity = cursor.getInt("COMPONENT_QUANTITY");
                item.put("item_code", item_code);
                item.put("item_name", item_name);
                item.put("bom_purpose", bom_purpose);
                item.put("sub_item", sub_item);
                item.put("ref_wh", ref_wh);
                item.put("change_notice", change_notice);
                item.put("unid", unid);
                item.put("key_id", key_id);
                item.put("parent_id", parent_id);
                item.put("component_quantity", String.valueOf(component_quantity));
                itemArray.add(item);
//                String item_name = cursor.getString("ITEM_NAME");
            }

            String xml = XmlHelper.createXml("root", headMap, "items", "item", itemArray);

        } catch (SQLException e) {
            e.printStackTrace();
            App.Current.showInfo(getContext(), e.getMessage() + "111111");
        }
    }

    private void clear() {
        if (textcell_workmethod.getContentText().contains("散热器")) {
            textcell_machine.setContentText("");
        }
//        textcell_taskordercode.setContentText("");
        textcell_usercode.setContentText("");
        textcell_lotnumber.setContentText("");
//        textcell_wh.setContentText("");
        textcell_workmethod.setContentText("");
        if (itemCode != null) {
            itemCode = "";
        }
        if (itemIDs != null) {
            itemIDs.removeAll(itemIDs);
        }
        itemWh = "";
        qjgIdent = "";
        pre_lot_number = "";
        if (selectedItems != null) {
            selectedItems.removeAll(selectedItems);
        }
    }

    private void startFirstItem(String processMethod) {
        //发起首件
        Map<String, String> headMap = new HashMap<>();
        headMap.put("task_order_code", textcell_taskordercode.getContentText());
        headMap.put("user_id", App.Current.UserID);
        headMap.put("process_method", processMethod);

        ArrayList<Map<String, String>> itemLists = new ArrayList<>();
        if (itemIDs != null && itemIDs.size() > 0) {
            for (int i = 0; i < itemIDs.size(); i++) {
                Map<String, String> itemMap = new HashMap<>();
                String item_code = itemIDs.get(i).getItem_code();
                itemMap.put("item_code", item_code);
                itemLists.add(itemMap);
            }
        }
        if (!TextUtils.isEmpty(itemCode)) {
            Map<String, String> itemMap = new HashMap<>();
            itemMap.put("item_code", itemCode);
            itemLists.add(itemMap);
        }
        String xml = XmlHelper.createXml("head", headMap, "items", "item", itemLists);
        Log.e("len", "XML : " + xml);
        String sql = "exec fm_pre_first_item_start ?";
        Parameters p = new Parameters().add(1, xml);
        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                Result<Integer> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                } else {
                    if (value.Value > 0) {
                        App.Current.toastError(getContext(), "触发首件成功");
                    }
                }
            }
        });
    }

    private void chooseStation(final ButtonTextCell buttonTextCell, final ListView listView) {
        String sql = "exec fm_pre_get_process_station_all";
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                } else {
                    if (value.Value != null && value.Value.Rows.size() > 0) {
                        ArrayList<String> stations = new ArrayList<>();
                        for (int i = 0; i < value.Value.Rows.size(); i++) {
                            String type_station = value.Value.Rows.get(i).getValue("type_station", "");
                            stations.add(type_station);
                        }
                        //弹出对话框，选择对应站别
                        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String type_station = value.Value.Rows.get(i).getValue("type_station", "");
                                buttonTextCell.setContentText(type_station);
                                processStation = type_station;
                                afterStationKeyListener(listView);
                                dialogInterface.dismiss();
                            }
                        };

                        new AlertDialog.Builder(getContext())
                                .setTitle("请选择")
                                .setSingleChoiceItems(stations.toArray(new String[0]), stations.indexOf(buttonTextCell.getContentText().trim()), listener).show();
                    }
                }
            }
        });
    }

    private void afterStationKeyListener(final ListView listView) {     //选择站位时候的结果
        String sql = "exec fm_pre_get_process_methods_by_station_test_v1 ?,?";
        Parameters p = new Parameters().add(1, processStation).add(2, item_id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                } else {
                    if (value.Value != null) {
                        myListStationAdapter = new MyListStationAdapter(value.Value);
                        listView.setAdapter(myListStationAdapter);
                    } else {
                        App.Current.toastError(getContext(), "没有维护数据");
                    }
                }
            }
        });
    }

    class MyListStationAdapter extends BaseAdapter {
        private DataTable dataTable;
        private ArrayList<String> selectedItems = new ArrayList<>();

        public MyListStationAdapter(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        public void frash(ArrayList<String> selectedItems) {
            this.selectedItems = selectedItems;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return dataTable == null ? 0 : dataTable.Rows.size();
        }

        @Override
        public Object getItem(int position) {
            return dataTable.Rows.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View contextView, ViewGroup viewGroup) {
            ItemViewHolder viewHolder;
            if (contextView == null) {
                contextView = View.inflate(getContext(), R.layout.pre_card_choose_item, null);
                viewHolder = new ItemViewHolder();
                viewHolder.textview_1 = (TextView) contextView.findViewById(R.id.textview_1);
                viewHolder.textview_2 = (TextView) contextView.findViewById(R.id.textview_2);
                viewHolder.textview_3 = (TextView) contextView.findViewById(R.id.textview_3);
                viewHolder.linearLayout = (LinearLayout) contextView.findViewById(R.id.linearlayout);
                contextView.setTag(viewHolder);
            } else {
                viewHolder = (ItemViewHolder) contextView.getTag();
            }

            DataRow dataRow = dataTable.Rows.get(position);
            String type_station = dataRow.getValue("type_station", "");
            Long number = dataRow.getValue("number", 0L);
            String processing_methods = dataRow.getValue("processing_methods", "");
            viewHolder.textview_1.setText(type_station);
            viewHolder.textview_2.setText(number.toString());
            viewHolder.textview_3.setText(processing_methods);

            if (selectedItems.contains(String.valueOf(position))) {
                viewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.parameter_color));
            } else {
                viewHolder.linearLayout.setBackgroundColor(Color.WHITE);
            }
            return contextView;
        }
    }

    class MyWhListAdapter extends BaseAdapter {
        private ArrayList<String> ref_whs;
        private ArrayList<String> selectedItems = new ArrayList<>();

        public MyWhListAdapter(ArrayList<String> ref_whs) {
            this.ref_whs = ref_whs;
        }

        public void frash(ArrayList<String> selectedItems) {
            this.selectedItems = selectedItems;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return ref_whs == null ? 0 : ref_whs.size();
        }

        @Override
        public Object getItem(int position) {
            return ref_whs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View contextView, ViewGroup viewGroup) {
            ItemViewHolder viewHolder;
            if (contextView == null) {
                contextView = View.inflate(getContext(), R.layout.pre_card_choose_item, null);
                viewHolder = new ItemViewHolder();
                viewHolder.textview_1 = (TextView) contextView.findViewById(R.id.textview_1);
                viewHolder.textview_2 = (TextView) contextView.findViewById(R.id.textview_2);
                viewHolder.textview_3 = (TextView) contextView.findViewById(R.id.textview_3);
                viewHolder.linearLayout = (LinearLayout) contextView.findViewById(R.id.linearlayout);
                contextView.setTag(viewHolder);
            } else {
                viewHolder = (ItemViewHolder) contextView.getTag();
            }

            String ref_wh = ref_whs.get(position);
            viewHolder.textview_1.setText(ref_wh);
            viewHolder.textview_2.setVisibility(View.GONE);
            viewHolder.textview_3.setVisibility(View.GONE);

            if (selectedItems.contains(ref_wh)) {
                viewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.parameter_color));
            } else {
                viewHolder.linearLayout.setBackgroundColor(Color.WHITE);
            }
            return contextView;
        }
    }


    class MyItemListAdapter extends BaseAdapter {
        private ArrayList<PreItemBean> mItems = new ArrayList<>();

        public MyItemListAdapter(ArrayList<PreItemBean> selectedItems) {
            this.mItems = selectedItems;
        }

        public void frash(ArrayList<PreItemBean> mItems) {
            this.mItems = mItems;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mItems == null ? 0 : mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View contextView, ViewGroup viewGroup) {
            ItemViewHolder viewHolder;
            if (contextView == null) {
                contextView = View.inflate(getContext(), R.layout.pre_card_choose_item, null);
                viewHolder = new ItemViewHolder();
                viewHolder.textview_1 = (TextView) contextView.findViewById(R.id.textview_1);
                viewHolder.textview_2 = (TextView) contextView.findViewById(R.id.textview_2);
                viewHolder.textview_3 = (TextView) contextView.findViewById(R.id.textview_3);
                viewHolder.linearLayout = (LinearLayout) contextView.findViewById(R.id.linearlayout);
                contextView.setTag(viewHolder);
            } else {
                viewHolder = (ItemViewHolder) contextView.getTag();
            }

            String item_code = mItems.get(position).getItem_code();
            String date_code = mItems.get(position).getDate_code();
            viewHolder.textview_1.setText(item_code);
            viewHolder.textview_2.setText(date_code);
            viewHolder.textview_3.setText("");
            viewHolder.textview_3.setVisibility(View.GONE);

//            if (selectedItems.contains(String.valueOf(position))) {
//                viewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.parameter_color));
//            } else {
//                viewHolder.linearLayout.setBackgroundColor(Color.WHITE);
//            }
            return contextView;
        }
    }

    class MyListAdapter extends BaseAdapter {
        private DataTable dataTable;
        private ArrayList<String> selectedItems = new ArrayList<>();

        public MyListAdapter(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        public void frash(ArrayList<String> selectedItems) {
            this.selectedItems = selectedItems;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return dataTable == null ? 0 : dataTable.Rows.size();
        }

        @Override
        public Object getItem(int position) {
            return dataTable.Rows.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View contextView, ViewGroup viewGroup) {
            ItemViewHolder viewHolder;
            if (contextView == null) {
                contextView = View.inflate(getContext(), R.layout.pre_card_choose_item, null);
                viewHolder = new ItemViewHolder();
                viewHolder.textview_1 = (TextView) contextView.findViewById(R.id.textview_1);
                viewHolder.textview_2 = (TextView) contextView.findViewById(R.id.textview_2);
                viewHolder.textview_3 = (TextView) contextView.findViewById(R.id.textview_3);
                viewHolder.linearLayout = (LinearLayout) contextView.findViewById(R.id.linearlayout);
                contextView.setTag(viewHolder);
            } else {
                viewHolder = (ItemViewHolder) contextView.getTag();
            }

            DataRow dataRow = dataTable.Rows.get(position);
            int item_id = dataRow.getValue("item_id", 0);
            String item_code = dataRow.getValue("item_code", "");
            String item_name = dataRow.getValue("item_name", "");
            viewHolder.textview_1.setText(String.valueOf(item_id));
            viewHolder.textview_2.setText(item_code);
            viewHolder.textview_3.setText(item_name);

            if (selectedItems.contains(String.valueOf(position))) {
                viewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.parameter_color));
            } else {
                viewHolder.linearLayout.setBackgroundColor(Color.WHITE);
            }
            return contextView;
        }
    }

    class ItemViewHolder {
        private TextView textview_1;
        private TextView textview_2;
        private TextView textview_3;
        private LinearLayout linearLayout;
    }

    class PreWorkOnlineAdapter extends MyBaseAdapter {

        public PreWorkOnlineAdapter(Context context, DataTable dataTable) {
            super(context, dataTable);
        }

        @Override
        public View setAdapterView(int i, View view, ViewGroup viewGroup, Context context, DataTable dataTable) {
            ViewHolder viewHolder;
            if (view == null) {
                view = View.inflate(context, R.layout.item_preonline_pda, null);
                viewHolder = new ViewHolder();
                viewHolder.linearlayout = view.findViewById(R.id.linearlayout);
                viewHolder.textView_1 = view.findViewById(R.id.textview_1);
                viewHolder.textView_2 = view.findViewById(R.id.textview_2);
                viewHolder.textView_3 = view.findViewById(R.id.textview_3);
                viewHolder.textView_31 = view.findViewById(R.id.textview_31);
                viewHolder.textView_4 = view.findViewById(R.id.textview_4);
                viewHolder.textView_5 = view.findViewById(R.id.textview_5);
                viewHolder.textView_51 = view.findViewById(R.id.textview_51);
                viewHolder.textView_6 = view.findViewById(R.id.textview_6);
                viewHolder.textView_7 = view.findViewById(R.id.textview_7);
                viewHolder.textView_8 = view.findViewById(R.id.textview_8);
                viewHolder.textView_9 = view.findViewById(R.id.textview_9);
                viewHolder.textView_10 = view.findViewById(R.id.textview_10);
                viewHolder.textView_11 = view.findViewById(R.id.textview_11);
                viewHolder.txtFristheckInfo = view.findViewById(R.id.txtFristheckInfo);
                viewHolder.txtFristheckConfirm = view.findViewById(R.id.txtFristheckConfirm);
                viewHolder.textView_12 = view.findViewById(R.id.textview_12);
                viewHolder.buttonIpqcSure = view.findViewById(R.id.button_ipqcsure);
                viewHolder.buttonOnline = view.findViewById(R.id.button_online);
                viewHolder.buttonOffline = view.findViewById(R.id.button_offline);
                viewHolder.buttonStop = view.findViewById(R.id.button_stop);
                viewHolder.buttonResume = view.findViewById(R.id.button_resume);
                viewHolder.btnFristCheck = view.findViewById(R.id.btnFristCheck);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            String worker_name = dataTable.Rows.get(i).getValue("worker_name", "");
            Long id = dataTable.Rows.get(i).getValue("id", 0L);
            final String task_order_code = dataTable.Rows.get(i).getValue("task_order_code", "");
            String pre_process_wh = dataTable.Rows.get(i).getValue("pre_process_wh", "");
            String item_code = dataTable.Rows.get(i).getValue("item_code", "");
            String item_name = dataTable.Rows.get(i).getValue("item_name", "");
            Date on_time = dataTable.Rows.get(i).getValue("on_time", date);
            Date start_time = dataTable.Rows.get(i).getValue("start_time", date);
            final BigDecimal work_hours = dataTable.Rows.get(i).getValue("work_hours", new BigDecimal(0));
            BigDecimal plan_quantity = dataTable.Rows.get(i).getValue("plan_quantity", new BigDecimal(0));
            Long pre_count = dataTable.Rows.get(i).getValue("pre_count", new Long(0));
            String status = dataTable.Rows.get(i).getValue("status", "");
            String process_method = dataTable.Rows.get(i).getValue("process_method", "");
            String remark = dataTable.Rows.get(i).getValue("remark", "");
            String machine_code = dataTable.Rows.get(i).getValue("machine_code", "");
            final String item_ids = dataTable.Rows.get(i).getValue("item_ids", "");
            String ipqc_code = dataTable.Rows.get(i).getValue("ipqc_code", "");
            String item_codes = dataTable.Rows.get(i).getValue("item_codes", "");
            String fristheckInfo = dataTable.Rows.get(i).getValue("size1", "");
            if (!(TextUtils.isEmpty(dataTable.Rows.get(i).getValue("size1", "")))) {
                fristheckInfo = "[" + dataTable.Rows.get(i).getValue("minsize", "") + "-"
                        + dataTable.Rows.get(i).getValue("maxsize", "") + "] "
                        + fristheckInfo;
                fristheckInfo = fristheckInfo + " - " + dataTable.Rows.get(i).getValue("size2", "");
                fristheckInfo = fristheckInfo + " - " + dataTable.Rows.get(i).getValue("size3", "");
                fristheckInfo = fristheckInfo + " - " + dataTable.Rows.get(i).getValue("size4", "");
                fristheckInfo = fristheckInfo + " - " + dataTable.Rows.get(i).getValue("size5", "");
            }
            String onTime = simpleDateFormat.format(on_time);
            String format = simpleDateFormat.format(date);
            String startTime = simpleDateFormat.format(start_time);
            viewHolder.textView_1.setText(context.getResources().getString(R.string.work_user) + worker_name);
            viewHolder.textView_2.setText(context.getResources().getString(R.string.task_order_code) + ":" + task_order_code);
            viewHolder.textView_3.setText(context.getResources().getString(R.string.plan_quantity) + ":" + plan_quantity.stripTrailingZeros().toPlainString());
            viewHolder.textView_31.setText("已加工数量" + ":" + pre_count.toString());
            viewHolder.textView_4.setText(context.getResources().getString(R.string.online_time) + onTime);
            viewHolder.textView_5.setText(context.getResources().getString(R.string.item_code) + ":" + item_code);
            viewHolder.textView_51.setText(context.getResources().getString(R.string.item_name) + ":" + item_name);
            viewHolder.textView_6.setText(context.getResources().getString(R.string.wh) + ":" + pre_process_wh);
            viewHolder.textView_7.setText(context.getResources().getString(R.string.process_method) + process_method);
            viewHolder.textView_8.setText(context.getResources().getString(R.string.time_counts) + work_hours);
            viewHolder.textView_9.setText(context.getResources().getString(R.string.status) + status);
            viewHolder.textView_10.setText(context.getResources().getString(R.string.remark) + ":" + remark);
            viewHolder.textView_11.setText(context.getResources().getString(R.string.ipqc) + ":" + ipqc_code);
            viewHolder.txtFristheckInfo.setText("首件信息:" + fristheckInfo);
            viewHolder.txtFristheckConfirm.setText("首件确认:" + dataTable.Rows.get(i).getValue("confirm_user", ""));
            viewHolder.textView_12.setText(context.getResources().getString(R.string.machine_code) + ":" + machine_code);

            viewHolder.buttonIpqcSure.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    editText1 = new EditText(getContext());
                    editText1.setHint("请IPQC打卡");
                    editText1.setOnKeyListener(null);
                    editText1.setClickable(false);
                    editText1.setFocusable(false);
                    builder = new AlertDialog.Builder(getContext())
                            .setTitle(getResources().getString(R.string.notication))
                            .setView(editText1)
                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (TextUtils.isEmpty(editText1.getText().toString())) {
                                        App.Current.toastError(getContext(), "请先扫描IPQC工号");
                                    } else {
                                        dialogInterface.dismiss();
                                        String sql = "exec fm_pre_ipqc_sure_card_v1 ?,?,?,?,?";
                                        Parameters p = new Parameters().add(1, editText1.getText().toString().replace("\n", "").trim()).add(2, task_order_code).add(3, id).add(4, item_ids.trim()).add(5, worker_name.split("-")[0].trim());
                                        Log.e("len", item_ids);
                                        Log.e("len", worker_name.split("-")[0].trim());
                                        Log.e("len", editText1.getText().toString() + "," + task_order_code + "," + App.Current.getMacAddress().trim());
                                        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                                            @Override
                                            public void handleMessage(Message msg) {
                                                Result<Integer> value = Value;
                                                if (value.HasError) {
                                                    App.Current.toastError(context, value.Error);
                                                } else {
                                                    App.Current.toastInfo(context, "IPQC确认成功");
                                                    refreshWorker("");
                                                }
                                            }
                                        });
                                    }
                                }
                            }).create();
                    builder.show();
                }
            });

            viewHolder.buttonOnline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] worker = worker_name.split("-");
                    final String workerCode = worker[0].trim();
                    String sql = "exec fm_sign_start_pre_work_time_v1 ?,?,?";
                    Parameters p = new Parameters().add(1, workerCode).add(2, task_order_code).add(3, work_line);
                    App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                        @Override
                        public void handleMessage(Message msg) {
                            Result<Integer> value = Value;
                            if (value.HasError) {
                                App.Current.toastError(context, value.Error);
                            } else {
                                if (value.Value > 0) {
                                    App.Current.toastInfo(context, "启动成功" + workerCode);
                                    refreshWorker("");
                                } else {
                                    App.Current.toastError(context, context.getResources().getString(R.string.commit_fail));
                                }
                            }
                        }
                    });
                }
            });
            viewHolder.buttonOffline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.Current.prompt(context, "提示", "请输入加工数量", new PromptCallback() {
                        @Override
                        public void onReturn(String result) {
                            String[] worker = worker_name.split("-");
                            String taskOrderCode = task_order_code;
                            final String workerCode = worker[0].trim();
                            if (TextUtils.isEmpty(result)) {
                                App.Current.toastError(context, "请先输入加工数量");
                            } else if (result.matches("^[0-9]*$")) {
                                String sql = "exec fm_sign_off_pre_work_time_v2 ?,?,?,?,?,?";
                                Parameters p = new Parameters().add(1, result).add(2, workerCode).add(3, taskOrderCode).add(4, work_line).add(5, methods_head_id).add(6, methods_item_number);
                                App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        Result<Integer> value = Value;
                                        if (value.HasError) {
                                            App.Current.toastError(context, "OffItem" + value.Error);
                                        } else {
                                            App.Current.toastInfo(context, "离线成功");
                                            refreshWorker("");
                                        }
                                    }
                                });
                            } else {
                                App.Current.toastError(context, "请输入正确的数值");
                            }
                        }
                    });
                }
            });

            viewHolder.buttonStop.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("询问")
                            .setMessage("确定要将" + worker_name + "暂停吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    pauseSelectedUser(id);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
            });

            viewHolder.buttonResume.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("询问")
                            .setMessage("确定要将" + worker_name + "恢复吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    resumeSelectedUser(id);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
            });
            viewHolder.btnFristCheck.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    editText1 = new EditText(getContext());
                    editText1.setHint("请首件确认人打卡");
                    editText1.setOnKeyListener(null);
                    editText1.setClickable(false);
                    editText1.setFocusable(false);
                    builder = new AlertDialog.Builder(getContext())
                            .setTitle(getResources().getString(R.string.notication))
                            .setView(editText1)
                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (TextUtils.isEmpty(editText1.getText().toString())) {
                                        App.Current.toastError(getContext(), "请先扫描首件确认人工号");
                                    } else {
                                        dialogInterface.dismiss();
                                        String sql = "exec fm_pre_frist_check_confirm ?,?";
                                        Parameters p = new Parameters().add(1, editText1.getText().toString().replace("\n", "").trim()).add(2, id);

                                        Log.e("len", editText1.getText().toString() + "," + task_order_code + "," + App.Current.getMacAddress().trim());
                                        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                                            @Override
                                            public void handleMessage(Message msg) {
                                                Result<Integer> value = Value;
                                                if (value.HasError) {
                                                    App.Current.toastError(context, value.Error);
                                                } else {
                                                    App.Current.toastInfo(context, "首件确认成功");
                                                    refreshWorker("");
                                                }
                                            }
                                        });
                                    }
                                }
                            }).create();
                    builder.show();
                }
            });

            if (TextUtils.isEmpty(ipqc_code)) {
                if ("上线".equals(status)) {
                    viewHolder.linearlayout.setBackgroundColor(getResources().getColor(R.color.greenyellow));
                } else if ("暂停".equals(status)) {
                    viewHolder.linearlayout.setBackgroundColor(Color.YELLOW);
                }
            } else {
                if ("上线".equals(status)) {
                    viewHolder.linearlayout.setBackgroundColor(getResources().getColor(R.color.mediumturquoise));
                } else if ("暂停".equals(status)) {
                    viewHolder.linearlayout.setBackgroundColor(Color.YELLOW);
                }
            }


            return view;
        }
    }

    private void pauseSelectedUser(Long id) {
        String sql = "exec p_fm_work_sign_pre_stop_and ?";
        Parameters p = new Parameters().add(1, id);
        refreshAfterSql(sql, p);
    }

    private void resumeSelectedUser(Long id) {
        String sql = "exec p_fm_work_sign_resume_and ?";
        Parameters p = new Parameters().add(1, id);
        refreshAfterSql(sql, p);
    }

    private void fristConfirm(Long id) {
        String sql = "exec p_fm_work_sign_resume_and ?";
        Parameters p = new Parameters().add(1, id);
        refreshAfterSql(sql, p);
    }

    private void refreshAfterSql(String sql, Parameters p) {
        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                Result<Integer> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                    return;
                }
                if (value.Value != null) {
                    refreshWorker("");
                }
            }
        });
    }

    class ViewHolder {
        private LinearLayout linearlayout;
        private TextView textView_1;
        private TextView textView_2;
        private TextView textView_3;
        private TextView textView_31;
        private TextView textView_4;
        private TextView textView_5;
        private TextView textView_51;
        private TextView textView_6;
        private TextView textView_7;
        private TextView textView_8;
        private TextView textView_9;
        private TextView textView_10;
        private TextView textView_11;
        private TextView txtFristheckInfo;
        private TextView txtFristheckConfirm;
        private TextView textView_12;
        private Button buttonIpqcSure;
        private Button buttonOnline;
        private Button buttonOffline;
        private Button buttonStop;
        private Button buttonResume;
        private Button btnFristCheck;
    }

}
