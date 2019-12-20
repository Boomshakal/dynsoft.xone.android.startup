package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.bean.SmtWhBean;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.sopactivity.ScanTestActivity;

/**
 * Created by Administrator on 2017/12/18.
 */

public class pn_qm_smt_rejects_record_mgr extends pn_editor {
    private ButtonTextCell textcell_1;
    private TextCell textcell_2;
    private ButtonTextCell textcell_3;
    private ButtonTextCell textcell_4;
    private TextCell textcell_5;
    private ListView listView;
    private LinearLayout relativeLayout;
    private ButtonTextCell editText;
    private TextView textViewTitle;

    private ImageButton btn_commit;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    private View view;
    private int seq_id = 0;
    private int item_id;
    private long task_id;
    private boolean isNew;
    private String org_code;
    private String item_name;
    private String segment;
    private String production;
    private String task_order_code;
    private int childScanCount;    //记录需要扫描子码的个数
    private int scanCounts;
    private String[] split;
    private String mainCode;
    private int width;
    private PopupWindow popupWindow;
    private PopupWindow popupOldWindow;
    private ButtonTextCell buttonTextCell9;
    private String work_type;
    private int process_id;
    private int sequence_id;
    private Long order_id;
    private String xml;
    private int resultId;
    private int worker_id;
    private long fore_man_id;
    private int height;
    private DataTable value;
    private ArrayList<String> childNumber;
    private String sn_number;   //扫描的序列号
    private int to_seq_id;
    private LinearLayout linearLayout;
    private ListView listView1;
    private ButtonTextCell button_textcell_3;
    private ArrayList<SmtWhBean> listViewDatas;
    private MyAdapter myAdapter;
    private TextCell text_cell_2;

    public pn_qm_smt_rejects_record_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_smt_rejects_record_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        sharedPreferences = getContext().getSharedPreferences("smt", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();

        relativeLayout = (LinearLayout) findViewById(R.id.relativelayout);
        listViewDatas = new ArrayList<SmtWhBean>();

        textcell_1 = (ButtonTextCell) view.findViewById(R.id.textcell_1);
        textcell_2 = (TextCell) view.findViewById(R.id.textcell_2);
        textcell_3 = (ButtonTextCell) view.findViewById(R.id.textcell_3);
        textcell_4 = (ButtonTextCell) view.findViewById(R.id.textcell_4);
        textcell_5 = (TextCell) view.findViewById(R.id.textcell_5);
        listView = (ListView) findViewById(R.id.listview);

        item_name = sharedPreferences.getString("item_name", "");
//        item_id = this.Parameters.get("item_id", 0);
//        item_name = sharedPreferences.getString("item_name", "");
        segment = sharedPreferences.getString("segment", "");
        production = sharedPreferences.getString("production", "");
        isNew = this.Parameters.get("isNew", false);
        if (isNew) {
            task_id = this.Parameters.get("task_id", 0L);
            edit.putInt("order_task_id", (int) task_id);
            edit.commit();
        }

        String code = this.Parameters.get("code", "");
        String item_name = sharedPreferences.getString("item_name", "");
        String work_line = sharedPreferences.getString("work_line", "");
        String segment = sharedPreferences.getString("segment", "");
        String worker_code = sharedPreferences.getString("worker_code", "");

        if (textcell_1 != null) {
            textcell_1.setLabelText("生产任务");
            textcell_1.setReadOnly();
//            textcell_1.setContentText(sharedPreferences.getString("task_order", ""));
            textcell_1.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadComfirmName(textcell_1);
                }
            });
            if (!TextUtils.isEmpty(code)) {
                textcell_1.setContentText(code);
            } else {
                code = sharedPreferences.getString("code", "");
                textcell_1.setContentText(code);
            }
        }

        if (!TextUtils.isEmpty(textcell_1.getContentText())) {
            String sql1 = "select * from mm_wo_task_order_head where code = ?";
            Parameters p1 = new Parameters().add(1, textcell_1.getContentText().trim());
            App.Current.DbPortal.ExecuteRecordAsync("core_and", sql1, p1, new ResultHandler<DataRow>() {
                @Override
                public void handleMessage(Message msg) {
                    Result<DataRow> value = Value;
                    if (value.HasError) {
                        App.Current.showError(getContext(), value.Error);
                        return;
                    }
                    if (value.Value != null) {
                        String plan_quantity = value.Value.getValue("plan_quantity", new BigDecimal(0)).toString();
                        edit.putString("plan_quantity", plan_quantity);
                        item_id = value.Value.getValue("item_id", 0);
                        if (item_id != 0) {
                            edit.putInt("item_id", item_id);
                        }
                        edit.commit();
                    }
                }
            });
        }

        if (textcell_2 != null) {
            textcell_2.setLabelText("机型");
            textcell_2.setReadOnly();
            if (!TextUtils.isEmpty(item_name) && !TextUtils.isEmpty(textcell_1.getContentText())) {
                textcell_2.setContentText(this.item_name);
            }
        }

        if (textcell_3 != null) {
            textcell_3.setLabelText("线别");
            textcell_3.setReadOnly();
            if (!TextUtils.isEmpty(work_line)) {
                textcell_3.setContentText(work_line);
            }
            textcell_3.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(textcell_1.getContentText())) {
                        App.Current.showError(getContext(), "请先选择生产任务");
                    } else {
                        loadWorkLineName(textcell_3);
                    }
                }
            });
        }

        if (textcell_4 != null) {
            textcell_4.setLabelText("工序");
            textcell_4.setReadOnly();
            if (!TextUtils.isEmpty(segment)) {
                textcell_4.setContentText(segment);
            }

            textcell_4.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(textcell_1.getContentText())) {
                        App.Current.showError(getContext(), "请线选择生产任务");
                    } else {
                        loadProductionName(textcell_4);
                    }
                }
            });
        }

        if (textcell_5 != null) {
            textcell_5.setLabelText("作业人");
            textcell_5.setReadOnly();
            textcell_5.setContentText(App.Current.UserCode);
            textcell_5.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.Current.Workbench.scanByCamera();
                }
            });
        }
    }

    private void loadComfirmName(ButtonTextCell textcell_1) {
        Link link = new Link("pane://x:code=pn_qm_smt_rejects_record_parameter_mgr");
//        link.Parameters.add("textcell", textcell_1);
        link.Open(null, getContext(), null);
        this.close();
    }

    private void loadWorkLineName(final ButtonTextCell text) {
        String sql = "exec p_qm_sop_work_line_items ?,?";
        Parameters p = new Parameters().add(1, App.Current.UserID).add(2, textcell_1.getContentText().replace("\n", ""));
        final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (result.HasError) {
            App.Current.showError(this.getContext(), result.Error);
            return;
        }
        if (result.Value != null) {
            if (result.Value.Rows.size() == 1) {
                DataRow row = result.Value.Rows.get(0);
                textcell_3.setContentText(row.getValue("name", ""));
                org_code = row.getValue("org_code", "");
                edit.putInt("work_line_id", row.getValue("id", 0));
                edit.commit();
                fore_man_id = row.getValue("responsible_man_id", 0L);
            } else {
                ArrayList<String> names = new ArrayList<String>();
                for (DataRow row : result.Value.Rows) {
                    StringBuffer name = new StringBuffer();
                    name.append(row.getValue("name", ""));
                    names.add(name.toString());
                }

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which >= 0) {
                            DataRow row = result.Value.Rows.get(which);
                            StringBuffer name = new StringBuffer();
                            name.append(row.getValue("name", ""));
                            text.setContentText(name.toString());
                            textcell_3.setContentText(row.getValue("name", ""));
                            org_code = row.getValue("org_code", "");
                            edit.putInt("work_line_id", row.getValue("id", 0));
                            edit.commit();
                            fore_man_id = row.getValue("responsible_man_id", 0L);
                        }
                        dialog.dismiss();
                    }
                };
                new AlertDialog.Builder(pn_qm_smt_rejects_record_mgr.this.getContext()).setTitle("请选择")
                        .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                                (text.getContentText().toString()), listener)
                        .setNegativeButton("取消", null).show();
            }
        } else {
            App.Current.toastInfo(getContext(), "工单有误！");
        }
    }

    private void loadListView() {           //加载ListView数据
        String sql = "exec p_qm_smt_rejects_get_items ?,?";
        Parameters p = new Parameters().add(1, textcell_1.getContentText().replace("\n", "")).add(2, split[0]);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> result = Value;
                if (result.HasError) {
                    App.Current.showError(getContext(), result.Error);
                    return;
                }
                if (result.Value != null) {
                    value = result.Value;
                    listView.setAdapter(new MyListViewAdapter(value));
                }
            }
        });
    }

    @Override
    public void onScan(String barcode) {
        String lot_number;
        if (barcode.contains("\n")) {
            lot_number = barcode.replace("\n", "");
        } else {
            lot_number = barcode;
        }
        Log.e("len", "lot_number:" + lot_number);
        if (lot_number.startsWith("WO:") || lot_number.startsWith("TO:")) {    //生产任务
            String sql = "exec p_qm_smt_rejects_record_get_task ?";
            String substring = lot_number.substring(3, lot_number.length());
            Parameters p = new Parameters().add(1, substring);
            pn_qm_smt_rejects_record_mgr.this.ProgressDialog.show();
            App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
                @Override
                public void handleMessage(Message msg) {
                    Result<DataRow> value = Value;
                    if (value.HasError) {
                        pn_qm_smt_rejects_record_mgr.this.ProgressDialog.dismiss();
                        App.Current.toastInfo(getContext(), value.Error);
                        return;
                    }
                    if (value.Value != null) {
                        pn_qm_smt_rejects_record_mgr.this.ProgressDialog.dismiss();
                        task_order_code = value.Value.getValue("task_order_code", "");
                        item_name = value.Value.getValue("item_name", "");
                        textcell_1.setContentText(task_order_code);
                        textcell_2.setContentText(item_name);
                    } else {
                        pn_qm_smt_rejects_record_mgr.this.ProgressDialog.dismiss();
                        App.Current.toastInfo(getContext(), "扫描生产任务有误");
                    }
                }
            });
        } else if ((lot_number.startsWith("M") || lot_number.startsWith("YH") || lot_number.startsWith("XR")) && lot_number.length() <= 10) {      //扫描的工作员
            String sql = "select * from fm_worker where code = ?";
            Parameters p = new Parameters().add(1, lot_number);
            App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
                @Override
                public void handleMessage(Message msg) {
                    Result<DataRow> value = Value;
                    if (value.HasError) {
                        App.Current.toastInfo(getContext(), value.Error);
                        return;
                    }
                    if (value.Value != null) {
                        worker_id = value.Value.getValue("id", 0);
                        textcell_5.setContentText(value.Value.getValue("code", ""));
                        edit.putInt("work_id", worker_id);
                        edit.commit();
                    } else {
                        App.Current.toastInfo(getContext(), "扫描作业人员不存在");
                    }
                }
            });
        } else if (lot_number.startsWith("CRQ:")) {          //手贴 料号
            String substring = lot_number.substring(4, lot_number.length());
            String[] split = substring.split("-");
            if (split.length > 2) {
                String number = split[1];     //料号
                if (text_cell_2 != null) {
                    text_cell_2.setContentText(number);
                    getWhFromPartNumber(button_textcell_3, number);
                }
            }
        } else if (lot_number.matches("^[1-9]\\d*$")) {
            //判断扫描的是条码 还是故障代码
            if (popupOldWindow != null && popupOldWindow.isShowing()) {
                if (buttonTextCell9 != null) {
                    getScanDatas(buttonTextCell9, lot_number);
                }
            } else {
                initScanNumber(lot_number);
            }
        } else {
            if (TextUtils.isEmpty(textcell_1.getContentText())) {
                App.Current.toastInfo(getContext(), "请先选择生产任务");
            } else if (TextUtils.isEmpty(textcell_3.getContentText())) {
                App.Current.toastInfo(getContext(), "请先选择线别");
            } else if (TextUtils.isEmpty(textcell_4.getContentText())) {
                App.Current.toastInfo(getContext(), "请先选择工序");
            } else if (TextUtils.isEmpty(textcell_5.getContentText())) {
                App.Current.toastInfo(getContext(), "请先选择作业员");
            } else {
                String procedure = textcell_4.getContentText();
                if (popupWindow != null && popupWindow.isShowing() && procedure.contains("手贴")) {
//                    getWhFromPartNumber(button_textcell_3, lot_number);
//                    text_cell_2.setContentText(lot_number);
//                    getWhFromPartNumber(button_textcell_3, lot_number);
                    App.Current.showError(getContext(), "扫描条码有误");
                    return;
                } else {
                    split = procedure.split(",");
                    sn_number = lot_number;
                    initScanNumber(lot_number);
                }

            }
        }
    }

    private void getScanDatas(final ButtonTextCell buttonTextCell9, String lot_number) {
        String sql = "select top 1 * from fm_bad_apperance where code = ?";
        Parameters p = new Parameters().add(1, lot_number);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.toastError(pn_qm_smt_rejects_record_mgr.this.getContext(), value.Error);
                    App.Current.playSound(R.raw.error);
                    return;
                }
                if (value.Value != null) {
                    String bad_type = value.Value.getValue("bad_type", "");
                    String code = value.Value.getValue("code", "");
                    buttonTextCell9.setContentText(code + "-" + bad_type);
                } else {
                    App.Current.toastError(pn_qm_smt_rejects_record_mgr.this.getContext(), "扫描错误");
                    App.Current.playSound(R.raw.error);
                }
            }
        });
    }

    private void CheckMainScanNumber(final String text) {
        String sql = "exec p_fm_work_check_barcode_and ?,?,?,?,?";
        Parameters p = new Parameters().add(1, textcell_1.getContentText().replace("\n", ""))
                .add(2, split[0]).add(3, textcell_5.getContentText()).add(4, text).add(5, "MAIN");
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    App.Current.playSound(R.raw.hook);
//                    App.Current.showError(pn_qm_smt_rejects_record_mgr.this.getContext() value.Error);
//                    finish();
                    return;
                }
                if (value.Value != null) {
                    String resultd = value.Value.getValue("rtnstr", "").toString();
                    if (resultd.equals("OK")) {
                        mainCode = text.toUpperCase();
                        if (scanCounts == 0) {
                            if (work_type.equals("sampling_test")) {
                                if (textcell_4.getContentText().contains("手贴")) {      //手贴
                                    handHandler(text);
                                } else {
                                    toastChooseResult();      //选择不良原因
                                }
                            } else if (work_type.equals("defect") || work_type.equals("in_repair")) {   //缺陷登记
//                                textview_result.setText("缺陷登记：" + text);
//                                textview_result.setTextColor(getResources().getColor(R.color.megmeet_green));
//                                textview_result.setVisibility(View.VISIBLE);
////                                status = PASS;
                                if (popupWindow != null && popupWindow.isShowing()) {
                                    App.Current.toastError(getContext(), "重复扫描");
                                } else {
                                    if (split[0].equals("M087") && work_type.equals("defect")) {    //老化的缺陷登记
                                        initRepairPopupWindow();
                                    } else {
                                        initPopupWindow();
                                    }
                                }
                                //再扫描一个PASS或者FAIL的码
                            } else {
                                xml = "";
                                CommitScanNumberCreate(text.toUpperCase(), "PASS");
                            }
                        }
                    } else {
                        App.Current.showError(getContext(), resultd);
                        App.Current.playSound(R.raw.hook);
                        return;
//                        finish();
                    }
                } else {
                    App.Current.playSound(R.raw.hook);
                    App.Current.showError(getContext(), "提交的二维码有误");
                }
            }
        });
    }

    private void initRepairPopupWindow() {      //老化，缺陷登记
        View popupView = View.inflate(pn_qm_smt_rejects_record_mgr.this.getContext(), R.layout.smt_repair_popupwindow, null);
        initRepairPopupView(popupView);
        popupOldWindow = new PopupWindow();
        popupOldWindow.setContentView(popupView);
        popupOldWindow.setFocusable(true);
        popupOldWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupOldWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.style_divider_color));
        popupOldWindow.showAtLocation(relativeLayout, Gravity.CENTER, 20, 30);
    }

    private void initRepairPopupView(final View popupView) {
        final ButtonTextCell buttonTextCell1 = (ButtonTextCell) popupView.findViewById(R.id.text_cell_1);
        final ButtonTextCell buttonTextCell2 = (ButtonTextCell) popupView.findViewById(R.id.text_cell_2);
        final ButtonTextCell buttonTextCell3 = (ButtonTextCell) popupView.findViewById(R.id.text_cell_3);
        final ButtonTextCell buttonTextCell4 = (ButtonTextCell) popupView.findViewById(R.id.text_cell_4);
        final ButtonTextCell buttonTextCell5 = (ButtonTextCell) popupView.findViewById(R.id.text_cell_5);
        final ButtonTextCell buttonTextCell6 = (ButtonTextCell) popupView.findViewById(R.id.text_cell_6);
        final ButtonTextCell buttonTextCell7 = (ButtonTextCell) popupView.findViewById(R.id.text_cell_7);
        final ButtonTextCell buttonTextCell8 = (ButtonTextCell) popupView.findViewById(R.id.text_cell_8);
        buttonTextCell9 = (ButtonTextCell) popupView.findViewById(R.id.text_cell_9);
        TextView cancel = (TextView) popupView.findViewById(R.id.cancel);
        TextView confirm = (TextView) popupView.findViewById(R.id.confirm);

        buttonTextCell1.setLabelText("水温");
        buttonTextCell2.setLabelText("座温");
        buttonTextCell3.setLabelText("漏水");
        buttonTextCell4.setLabelText("清洗器");
        buttonTextCell5.setLabelText("按键");
        buttonTextCell6.setLabelText("水温数据");
        buttonTextCell7.setLabelText("座温数据");
        buttonTextCell8.setLabelText("风温数据");
        buttonTextCell9.setLabelText("不良现象");

        buttonTextCell1.setReadOnly();
        buttonTextCell2.setReadOnly();
        buttonTextCell3.setReadOnly();
        buttonTextCell4.setReadOnly();
        buttonTextCell5.setReadOnly();
        buttonTextCell9.setReadOnly();
        buttonTextCell6.Button.setVisibility(INVISIBLE);
        buttonTextCell7.Button.setVisibility(INVISIBLE);
        buttonTextCell8.Button.setVisibility(INVISIBLE);

        buttonTextCell1.Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseOkAndNg(buttonTextCell1);
            }
        });
        buttonTextCell2.Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseOkAndNg(buttonTextCell2);
            }
        });
        buttonTextCell3.Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseOkAndNg(buttonTextCell3);
            }
        });
        buttonTextCell4.Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseOkAndNg(buttonTextCell4);
            }
        });
        buttonTextCell5.Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseOkAndNg(buttonTextCell5);
            }
        });

        buttonTextCell9.Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseRepairType(buttonTextCell9);
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupOldWindow.dismiss();
            }
        });
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(buttonTextCell1.getContentText())) {
                    App.Current.toastError(getContext(), "请选择水温结果");
                } else if (TextUtils.isEmpty(buttonTextCell2.getContentText())) {
                    App.Current.toastError(getContext(), "请选择座温结果");
                } else if (TextUtils.isEmpty(buttonTextCell3.getContentText())) {
                    App.Current.toastError(getContext(), "请选择漏水结果");
                } else if (TextUtils.isEmpty(buttonTextCell4.getContentText())) {
                    App.Current.toastError(getContext(), "请选择清洗器结果");
                } else if (TextUtils.isEmpty(buttonTextCell5.getContentText())) {
                    App.Current.toastError(getContext(), "请选择按键结果");
                } else if (TextUtils.isEmpty(buttonTextCell6.getContentText())) {
                    App.Current.toastError(getContext(), "请输入水温数据");
                } else if (TextUtils.isEmpty(buttonTextCell7.getContentText())) {
                    App.Current.toastError(getContext(), "请输入座温数据");
                } else if (TextUtils.isEmpty(buttonTextCell8.getContentText())) {
                    App.Current.toastError(getContext(), "请输入风温数据");
                } else {
                    //用一个集合来添加结果，判断最终结果是PASS还是FAIL
                    ArrayList<String> results = new ArrayList<>();
                    results.add(buttonTextCell1.getContentText());
                    results.add(buttonTextCell2.getContentText());
                    results.add(buttonTextCell3.getContentText());
                    results.add(buttonTextCell4.getContentText());
                    results.add(buttonTextCell5.getContentText());
                    String result = "";
                    if (results.contains("NG")) {
                        result = "FAIL";
                        if (TextUtils.isEmpty(buttonTextCell9.getContentText())) {
                            App.Current.toastError(getContext(), "请先选择不良现象");
                            return;
                        }
                    } else {
                        result = "PASS";
                    }

                    Map<String, String> head_entry = new HashMap<String, String>();
                    ArrayList<Map<String, String>> item_entries = new ArrayList<Map<String, String>>();
                    Map<String, String> item_entry = new HashMap<String, String>();
                    item_entry.put("sn_no", mainCode);
                    item_entry.put("sn_type", work_type);
                    item_entry.put("ng_reason", buttonTextCell9.getContentText());
                    item_entry.put("water_temp", buttonTextCell1.getContentText());
                    item_entry.put("seat_temp", buttonTextCell2.getContentText());
                    item_entry.put("lost_water", buttonTextCell3.getContentText());
                    item_entry.put("clear_machine", buttonTextCell4.getContentText());
                    item_entry.put("press_key", buttonTextCell5.getContentText());
                    item_entry.put("water_data", buttonTextCell6.getContentText());
                    item_entry.put("seat_data", buttonTextCell7.getContentText());
                    item_entry.put("wind_data", buttonTextCell8.getContentText());
                    item_entries.add(item_entry);
                    xml = XmlHelper.createXml("bindings", head_entry, "bindings", "binding", item_entries);
                    Log.e("len", "XMLXML:" + xml);
                    CommitScanNumberCreate(mainCode, result);
                    popupOldWindow.dismiss();
                }
            }
        });
    }

    private void chooseRepairType(final ButtonTextCell buttonTextCell9) {
        String sql = "SELECT code + bad_type name  FROM fm_bad_apperance";
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastInfo(getContext(), value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    final ArrayList<String> names = new ArrayList<String>();
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        names.add(value.Value.Rows.get(i).getValue("name", ""));
                    }
                    new AlertDialog.Builder(pn_qm_smt_rejects_record_mgr.this.getContext()).setTitle("请选择")
                            .setItems(names.toArray(new String[0]), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int position) {
                                    buttonTextCell9.setContentText(names.get(position));
                                }
                            })
                            .setNegativeButton("取消", null).show();
                }
            }
        });
    }

    private void chooseOkAndNg(final ButtonTextCell buttonTextCell) {
        final String chooseItems[] = {"OK", "NG"};
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("请选择")
                .setSingleChoiceItems(chooseItems, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buttonTextCell.setContentText(chooseItems[i]);
                        dialogInterface.dismiss();
                    }
                }).create();
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    private void handHandler(String text) {
        View popupView = View.inflate(pn_qm_smt_rejects_record_mgr.this.getContext(), R.layout.smt_hand_popupwindow, null);
        initHandPopupView(popupView, text);
        popupWindow = new PopupWindow();
        popupWindow.setContentView(popupView);
        popupWindow.setFocusable(true);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.style_divider_color));
        popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 20, 30);
    }

    private void initHandPopupView(View popupView, String text) {
        final TextCell text_cell_1 = (TextCell) popupView.findViewById(R.id.text_cell_1);
        text_cell_2 = (TextCell) popupView.findViewById(R.id.text_cell_2);
        button_textcell_3 = (ButtonTextCell) popupView.findViewById(R.id.button_textcell_3);
        final ButtonTextCell button_textcell_4 = (ButtonTextCell) popupView.findViewById(R.id.button_textcell_4);
        listView1 = (ListView) popupView.findViewById(R.id.listview_1);
        myAdapter = new MyAdapter(listViewDatas);
        listView1.setAdapter(myAdapter);
        listView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                listViewDatas.remove(i);
                myAdapter.fresh(listViewDatas);
                return false;
            }
        });
        TextView confirm = (TextView) popupView.findViewById(R.id.confirm);
        TextView cancel = (TextView) popupView.findViewById(R.id.cancel);

        if (text_cell_1 != null) {
            text_cell_1.setLabelText("序列号");
            text_cell_1.setContentText(text);
        }

        if (text_cell_2 != null) {
            text_cell_2.setLabelText("料号");
            text_cell_2.setReadOnly();
            text_cell_2.TextBox.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        text_cell_2.setContentText(text_cell_2.getContentText().replace("\n", ""));
                        getWhFromPartNumber(button_textcell_3, text_cell_2.getContentText());
                    }
                    return false;
                }
            });
        }

        if (button_textcell_3 != null) {
            button_textcell_3.setLabelText("位号");
            button_textcell_3.setReadOnly();
            button_textcell_3.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    getWhFromPartNumber(button_textcell_3, text_cell_2.getContentText());
                }
            });
        }

        if (button_textcell_4 != null) {
            button_textcell_4.setLabelText("流向");
            button_textcell_4.setReadOnly();
        }

        String sql = "exec p_qm_smt_check_seq ?,?,?";
        Parameters p = new Parameters().add(1, "PASS").add(2, sequence_id).add(3, process_id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    return;
                }
                if (value.Value != null) {
                    if (value.Value.Rows.size() == 1) {
                        button_textcell_4.setReadOnly();
                        button_textcell_4.setContentText(value.Value.Rows.get(0).getValue("to_seq_name", ""));
//                        buttonTextCell3.setContentText("FAIL-" + value.Value.Rows.get(0).getValue("to_seq_name", ""));
                    } else {
                        loadToSeq(value.Value, button_textcell_4);
                    }
                }
            }
        });

        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listViewDatas.size() < 1) {
                    if (TextUtils.isEmpty(text_cell_2.getContentText())) {
                        App.Current.showInfo(pn_qm_smt_rejects_record_mgr.this.getContext(), "请输入料号");
                    } else if (TextUtils.isEmpty(button_textcell_3.getContentText())) {
                        App.Current.showInfo(pn_qm_smt_rejects_record_mgr.this.getContext(), "请选择位号");
                    }
                } else {
                    xml = getHandBindXml();
                    CommitScanNumberCreate(mainCode, "PASS");
                    popupWindow.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }

    private String getHandBindXml() {
        Map<String, String> head_entry = new HashMap<String, String>();
        ArrayList<Map<String, String>> item_entries = new ArrayList<Map<String, String>>();
        Map<String, String> item_entry = new HashMap<String, String>();
        item_entry.put("sn_no", mainCode);
        item_entry.put("sn_type", work_type);
        item_entry.put("ng_remark", "少件");
        item_entry.put("ng_reason", "手贴");
        item_entry.put("ng_type", "机器抛料");
        item_entries.add(item_entry);
        String xml = XmlHelper.createXml("bindings", head_entry, "bindings", "binding", item_entries);
        return xml;
    }

    private String getHandXml(int work_id) {
        Map<String, String> head_entry = new HashMap<String, String>();
        ArrayList<Map<String, String>> item_entries = new ArrayList<Map<String, String>>();
        for (int i = 0; i < listViewDatas.size(); i++) {
            Map<String, String> item_entry = new HashMap<String, String>();
            String partNumber = listViewDatas.get(i).getPartNumber();
            String itemNumber = listViewDatas.get(i).getItemNumber();
            item_entry.put("ref_wh", itemNumber);
            item_entry.put("work_id", String.valueOf(work_id));
            item_entry.put("item_num", partNumber);
            item_entry.put("create_date", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            item_entry.put("create_by", String.valueOf(worker_id));
            item_entries.add(item_entry);
        }
        String xml = XmlHelper.createXml("head", head_entry, "items", "smt_record", item_entries);
        return xml;
    }

    private void getWhFromPartNumber(final ButtonTextCell button_textcell_3, final String contentText) {
        String sql = "exec p_fm_smt_get_position ?,?";
        String[] split = textcell_1.getContentText().split("-");
        String task_order = "";
        if (split.length > 1) {
            task_order = split[0];
        }
        Parameters p = new Parameters().add(1, task_order).add(2, contentText);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    if (value.Value.Rows.size() > 1) {           //多个位号
                        chooseDialogWh(contentText, value.Value);
                    } else {                                    //一个位号
                        button_textcell_3.Button.setVisibility(GONE);
                        button_textcell_3.setContentText(value.Value.Rows.get(0).getValue("Value", ""));
                        SmtWhBean smtWhBean = new SmtWhBean();
                        smtWhBean.setPartNumber(contentText);    //料号
                        smtWhBean.setItemNumber(value.Value.Rows.get(0).getValue("Value", ""));    //位号
                        listViewDatas.add(smtWhBean);
                        myAdapter.fresh(listViewDatas);
                    }
                } else {
                    App.Current.toastError(pn_qm_smt_rejects_record_mgr.this.getContext(), "扫描的料号有误！");
                    App.Current.playSound(R.raw.error);
                    text_cell_2.setContentText("");
                }
            }
        });
    }

    private void chooseDialogWh(final String partNumber, final DataTable dataTable) {               //选择位号的dialog
        ArrayList<String> itemNumbers = new ArrayList<String>();
        for (DataRow row : dataTable.Rows) {
            StringBuffer itemNumber = new StringBuffer();
            itemNumber.append(row.getValue("Value", ""));
            itemNumbers.add(itemNumber.toString());
        }

        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    DataRow row = dataTable.Rows.get(which);
                    button_textcell_3.setContentText(row.getValue("Value", ""));
                    //添加到ListView
                    SmtWhBean smtWhBean = new SmtWhBean();
                    smtWhBean.setPartNumber(partNumber);
                    smtWhBean.setItemNumber(row.getValue("Value", ""));
                    listViewDatas.add(smtWhBean);
                    myAdapter.fresh(listViewDatas);
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(getContext()).setTitle("请选择")
                .setSingleChoiceItems(itemNumbers.toArray(new String[0]), itemNumbers.indexOf
                        (button_textcell_3.getContentText().toString()), listener)
                .setNegativeButton("取消", null).show();
    }

    private void initPopupWindow() {
        View popupView = View.inflate(getContext(), R.layout.smt_rejects_popupwindow1, null);
        popupWindow = new PopupWindow();
        initPopupView1(popupView, popupWindow);
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
//                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.style_divider_color));
        popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 20, 30);
    }

    private void initPopupView1(final View popupView, final PopupWindow popupWindow) {
        editText = (ButtonTextCell) popupView.findViewById(R.id.edittext);
        linearLayout = (LinearLayout) popupView.findViewById(R.id.linearlayout);
        final ButtonTextCell buttonTextCell1 = (ButtonTextCell) popupView.findViewById(R.id.button_text_cell_1);
        final ButtonTextCell buttonTextCell2 = (ButtonTextCell) popupView.findViewById(R.id.button_text_cell_2);
        final ButtonTextCell buttonTextCell3 = (ButtonTextCell) popupView.findViewById(R.id.button_text_cell_3);
        textViewTitle = (TextView) popupView.findViewById(R.id.text_title);
        TextView confirm = (TextView) popupView.findViewById(R.id.confirm);
        TextView cancel = (TextView) popupView.findViewById(R.id.cancel);
        if (work_type.equals("defect")) {      //缺陷登记
            if (textViewTitle != null) {
                textViewTitle.setText("缺陷登记");
            }
            if (editText != null) {
                editText.setLabelText("判定结果");
                editText.Button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadPassResult(editText, buttonTextCell1, buttonTextCell2, buttonTextCell3);
                    }
                });
            }
            if (buttonTextCell3 != null) {
                buttonTextCell3.setLabelText("流向工序");
            }
        } else {
            if (work_type.equals("in_repair")) {    //维修
                linearLayout.setVisibility(VISIBLE);
                if (textViewTitle != null) {
                    textViewTitle.setText("维修");
                }
                String sql = "select * from fm_repair where lot_number = ?  order by create_time desc";
                Parameters p = new Parameters().add(1, sn_number);
                App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
                    @Override
                    public void handleMessage(Message msg) {
                        Result<DataTable> value = Value;
                        if (value.HasError) {
                            App.Current.showInfo(getContext(), value.Error);
                            return;
                        }
                        if (value.Value != null) {
                            if (value.Value.Rows.size() > 0) {
                                String ng_reason = value.Value.Rows.get(0).getValue("ng_reason", "");
                                String ng_remark = value.Value.Rows.get(0).getValue("ng_remark", "");
                                editText.setContentText(ng_reason + ng_remark);
                            }
                        }
                    }
                });
                if (editText != null) {
                    editText.setLabelText("不良原因");
                    editText.setReadOnly();
                }
                if (buttonTextCell1 != null) {
                    buttonTextCell1.setLabelText("不良类型");
                    buttonTextCell1.Button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            String sql = "select * from repair_and where remarks = ?";
                            Parameters p = new Parameters().add(1, "bad");
                            App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
                                @Override
                                public void handleMessage(Message msg) {
                                    Result<DataTable> value = Value;
                                    if (value.HasError) {
                                        App.Current.toastInfo(getContext(), value.Error);
                                        return;
                                    }
                                    if (value.Value != null && value.Value.Rows.size() > 0) {
                                        if (value.Value.Rows.size() == 1) {
                                            String describle = value.Value.Rows.get(0).getValue("describle", "");
                                            buttonTextCell1.setContentText(describle);
                                        } else {
                                            ArrayList<String> names = new ArrayList<String>();
                                            for (int i = 0; i < value.Value.Rows.size(); i++) {
                                                names.add(value.Value.Rows.get(i).getValue("describle", ""));
                                            }
                                            final StringBuffer nameMessage = new StringBuffer();
                                            final boolean[] selected = new boolean[names.size()];
                                            multiChoiceDialog(nameMessage, selected, names, buttonTextCell1);
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
                if (buttonTextCell2 != null) {
                    buttonTextCell2.setLabelText("维修说明");
                    buttonTextCell2.Button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String sql = "select * from repair_and where remarks = ?";
                            Parameters p = new Parameters().add(1, "repair");
                            App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
                                @Override
                                public void handleMessage(Message msg) {
                                    Result<DataTable> value = Value;
                                    if (value.HasError) {
                                        App.Current.toastInfo(getContext(), value.Error);
                                        return;
                                    }
                                    if (value.Value != null && value.Value.Rows.size() > 0) {
                                        if (value.Value.Rows.size() == 1) {
                                            String describle = value.Value.Rows.get(0).getValue("describle", "");
                                            buttonTextCell2.setContentText(describle);
                                        } else {
                                            ArrayList<String> names = new ArrayList<String>();
                                            for (int i = 0; i < value.Value.Rows.size(); i++) {
                                                names.add(value.Value.Rows.get(i).getValue("describle", ""));
                                            }
                                            final StringBuffer nameMessage = new StringBuffer();
                                            final boolean[] selected = new boolean[names.size()];
                                            multiChoiceDialog(nameMessage, selected, names, buttonTextCell2);
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
                if (buttonTextCell3 != null) {
                    buttonTextCell3.setLabelText("流向工序");
                    buttonTextCell3.setReadOnly();
                }
            }

            String sql = "exec p_qm_smt_check_seq ?,?,?";
            Parameters p = new Parameters().add(1, "PASS").add(2, sequence_id).add(3, process_id);
            App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
                @Override
                public void handleMessage(Message msg) {
                    final Result<DataTable> value = Value;
                    if (value.HasError) {
                        App.Current.showError(getContext(), value.Error);
                        return;
                    }
                    if (value.Value != null) {
                        if (value.Value.Rows.size() == 1) {
                            buttonTextCell3.setReadOnly();
                            buttonTextCell3.setContentText(editText.getContentText().replace("\n", "") + value.Value.Rows.get(0).getValue("to_seq_name", ""));
                            to_seq_id = value.Value.Rows.get(0).getValue("to_seq_id", 0);
                        } else {
                            buttonTextCell3.Button.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    loadToSeq(value.Value, buttonTextCell3);
                                }
                            });
                        }
                    }
                }
            });
        }
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (work_type.equals("defect")) {
                    if (TextUtils.isEmpty(editText.getContentText())) {
                        App.Current.showInfo(getContext(), "请选择判定结果");
                    } else if (TextUtils.isEmpty(buttonTextCell3.getContentText())) {
                        App.Current.showInfo(getContext(), "请选择流向");
                    } else {
                        xml = getTestBindXml(buttonTextCell1.getContentText(), buttonTextCell2.getContentText());
                        CommitScanNumberCreate(mainCode, editText.getContentText().toString().replace("\n", ""));
                        popupWindow.dismiss();
                    }
                } else if (work_type.equals("in_repair")) {
                    if (TextUtils.isEmpty(editText.getContentText())) {
                        App.Current.toastInfo(getContext(), "扫描的序列号不存在维修");
                        popupWindow.dismiss();
                    } else {
                        xml = getRepairXml(buttonTextCell1.getContentText(), buttonTextCell2.getContentText());
                        CommitScanNumberCreate(mainCode, "PASS");
                        popupWindow.dismiss();
                    }
                }
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }

    private void loadToSeq(final DataTable dataTable, final ButtonTextCell buttonTextCell3) {
        ArrayList<String> names = new ArrayList<String>();
        for (DataRow row : dataTable.Rows) {
            StringBuffer name = new StringBuffer();
            name.append("FAIL" + row.getValue("to_seq_name", ""));
            names.add(name.toString());
        }

        singleChoiceDialog(dataTable, buttonTextCell3, names);
    }

    private void singleChoiceDialog(final DataTable dataTable, final ButtonTextCell buttonTextCell3, ArrayList<String> names) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    DataRow row = dataTable.Rows.get(which);
                    buttonTextCell3.setContentText(row.getValue("to_seq_name", ""));
                    to_seq_id = row.getValue("to_seq_id", 0);
//                                    StringBuffer name = new StringBuffer();
//                                    name.append(row.getValue("name", ""));
//                                    buttonTextCell3.setContentText(name.toString());
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(getContext()).setTitle("请选择")
                .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                        (buttonTextCell3.getContentText().toString()), listener)
                .setNegativeButton("取消", null).show();
    }

    private void loadPassResult(final ButtonTextCell edittext,
                                final ButtonTextCell buttonTextCell1, final ButtonTextCell buttonTextCell2, final ButtonTextCell buttonTextCell3) {
        final ArrayList<String> names = new ArrayList<String>();
        names.add("PASS");
        names.add("FAIL");

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    String name = names.get(which);
                    edittext.setContentText(name);
                    if (name.equals("FAIL")) {
                        linearLayout.setVisibility(VISIBLE);
                        if (buttonTextCell1 != null) {
                            buttonTextCell1.setLabelText("不良描述");
                            buttonTextCell1.Button.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String sql = "exec p_fm_smt_get_reason ?";
                                    Parameters p = new Parameters().add(1, 1);
                                    App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
                                        @Override
                                        public void handleMessage(Message msg) {
                                            final Result<DataTable> value = Value;
                                            if (value.HasError) {
                                                App.Current.showError(pn_qm_smt_rejects_record_mgr.this.getContext(), value.Error);
                                                return;
                                            }
                                            if (value.Value != null) {
                                                ArrayList<String> names = new ArrayList<String>();
                                                for (DataRow row : value.Value.Rows) {
                                                    String name = row.getValue("name", "");
                                                    names.add(name);
                                                }
                                                final StringBuffer nameMessage = new StringBuffer();
                                                final boolean[] selected = new boolean[names.size()];
                                                multiChoiceDialog(nameMessage, selected, names, buttonTextCell1);
                                            }
                                        }
                                    });
                                }
                            });
                            if (buttonTextCell2 != null) {
                                buttonTextCell2.setLabelText("不良位号");
                                buttonTextCell2.Button.setVisibility(GONE);
                            }
                        }
                    } else {
                        linearLayout.setVisibility(GONE);
                    }
                    chooseNext(buttonTextCell3);
                    dialog.dismiss();
                }
            }
        };
        new AlertDialog.Builder(getContext()).setTitle("请选择")
                .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                        (buttonTextCell1.getContentText().toString()), listener)
                .setNegativeButton("取消", null).show();
    }

    private void chooseNext(final ButtonTextCell buttonTextCell3) {
        String sql = "exec p_qm_smt_check_seq ?,?,?";
        Parameters p = new Parameters().add(1, editText.getContentText()).add(2, sequence_id).add(3, process_id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    return;
                }
                if (value.Value != null) {
                    if (value.Value.Rows.size() == 1) {
                        buttonTextCell3.setReadOnly();
                        buttonTextCell3.setContentText(editText.getContentText().replace("\n", "") + "-" + value.Value.Rows.get(0).getValue("to_seq_name", ""));
                    } else {
                        loadToSeq(value.Value, buttonTextCell3);
                    }
                }
            }
        });
    }

    private void CommitScanNumberCreate(final String txt, final String rslt) {
        Time time = new Time();
        time.setToNow();
        final int hour = time.hour;
        final int toSeqId;
        if (work_type.equals("in_repair")) {
            toSeqId = to_seq_id;
        } else {
            toSeqId = getToSeqId(rslt);
        }
        if (worker_id == 0) {
            String sql = "select id from fm_worker where code = ?";
            Parameters p = new Parameters().add(1, textcell_5.getContentText().replace("\n", ""));
            App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
                @Override
                public void handleMessage(Message msg) {
                    Result<DataRow> value = Value;
                    if (value.HasError) {
                        App.Current.showError(getContext(), value.Error);
                        return;
                    }
                    if (value.Value != null) {
                        worker_id = value.Value.getValue("id", 0);
                        if (toSeqId > 0) {
                            String sql = "exec p_fm_work_create_and_v1 ?,?,?,?,?,?,?,?,?,?,?,?,?,?";
                            Parameters p = new Parameters().add(1, order_id).add(2, sequence_id).add(3, org_code).add(4, textcell_3.getContentText())
                                    .add(5, "Hi").add(6, hour > 18 ? "夜班" : "白班").add(7, "PDA").add(8, fore_man_id).add(9, worker_id)
                                    .add(10, txt).add(11, toSeqId).add(12, rslt).add(13, App.Current.UserID).add(14, xml);
                            Log.e("len", order_id + "," + sequence_id + "," + org_code + "," + textcell_3.getContentText() + "," + "HI"
                                    + "," + "白班" + "," + "PDA" + "," + fore_man_id + "," + worker_id + "," + txt + "," + toSeqId + "," + rslt + "," + App.Current.UserID
                                    + "," + xml);
                            App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
                                @Override
                                public void handleMessage(Message msg) {
                                    Result<DataRow> result = Value;
                                    if (result.HasError) {
                                        App.Current.showError(getContext(), result.Error);
                                        App.Current.playSound(R.raw.hook);
                                        return;
                                    }
                                    if (result.Value != null) {
                                        resultId = Integer.parseInt(result.Value.getValue(0).toString());
                                        Log.e("len", "resultId:" + resultId);
                                        if (resultId > 0) {
                                            if (textcell_4.getContentText().contains("手贴")) {
                                                Log.e("len", textcell_4.getContentText() + "********");
                                                commitHand(resultId);
                                            } else {
                                                App.Current.toastInfo(getContext(), "提交成功");
                                                App.Current.playSound(R.raw.pass);
                                                loadListView();
                                            }
                                        } else {
                                            App.Current.showError(getContext(), "提交失败");
                                        }
                                    } else {
                                        App.Current.showInfo(getContext(), "提交失败！");
                                        return;
                                    }
                                }
                            });
                        }
                    } else {
                        App.Current.toastInfo(getContext(), "提交失败！");
                        App.Current.playSound(R.raw.error);
                    }
                }
            });
        } else {
            if (toSeqId > 0) {
                String sql = "exec p_fm_work_create_and_v1 ?,?,?,?,?,?,?,?,?,?,?,?,?,?";
                Parameters p = new Parameters().add(1, order_id).add(2, sequence_id).add(3, org_code).add(4, textcell_3.getContentText())
                        .add(5, "Hi").add(6, hour > 18 ? "夜班" : "白班").add(7, "PDA").add(8, fore_man_id).add(9, worker_id)
                        .add(10, txt).add(11, toSeqId).add(12, rslt).add(13, App.Current.UserID).add(14, xml);
                Log.e("len", order_id + "," + sequence_id + "," + org_code + "," + textcell_3.getContentText() + "," + "HI"
                        + "," + "白班" + "," + "PDA" + "," + fore_man_id + "," + worker_id + "," + txt + "," + toSeqId + "," + rslt + "," + App.Current.UserID
                        + "," + xml);
                App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
                    @Override
                    public void handleMessage(Message msg) {
                        Result<DataRow> result = Value;
                        if (result.HasError) {
                            App.Current.showError(getContext(), result.Error);
                            App.Current.playSound(R.raw.hook);
                            return;
                        }
                        if (result.Value != null) {
                            resultId = Integer.parseInt(result.Value.getValue(0).toString());
                            Log.e("len", "IDresultId:" + resultId);
                            if (resultId > 0) {
                                if (textcell_4.getContentText().contains("手贴")) {
                                    Log.e("len", textcell_4.getContentText() + "********");
                                    commitHand(resultId);
                                } else {
                                    App.Current.toastInfo(getContext(), "提交成功");
                                    App.Current.playSound(R.raw.pass);
                                    loadListView();
                                }
                            } else {
                                App.Current.showError(getContext(), "提交失败");
                            }
                        } else {
                            App.Current.toastInfo(getContext(), "提交失败！");
                            App.Current.playSound(R.raw.error);
                        }
                    }
                });
            } else {
                Toast.makeText(getContext(), "工序有误", Toast.LENGTH_SHORT);
            }
        }
    }

    private void commitHand(int work_id) {
        String xml = getHandXml(work_id);
        Log.e("len", "XML:" + xml);
        String sql = "exec p_fm_work_smt_paster_create_and ?,?";
        listViewDatas.clear();
        Connection conn = App.Current.DbPortal.CreateConnection(pn_qm_smt_rejects_record_mgr.this.Connector);
        CallableStatement stmt;
        try {
            stmt = conn.prepareCall(sql);
            stmt.setObject(1, xml);
            stmt.registerOutParameter(2, Types.VARCHAR);
            stmt.execute();

            String val = stmt.getString(2);
            if (val != null) {
                Result<String> rs = XmlHelper.parseResult(val);
                if (rs.HasError) {
                    App.Current.showError(pn_qm_smt_rejects_record_mgr.this.getContext(), rs.Error);
                    return;
                }
                App.Current.toastInfo(pn_qm_smt_rejects_record_mgr.this.getContext(), "提交成功");
            }
        } catch (SQLException e) {
            App.Current.showInfo(pn_qm_smt_rejects_record_mgr.this.getContext(), e.getMessage());
            e.printStackTrace();
            pn_qm_smt_rejects_record_mgr.this.close();
        }
    }

    private int getToSeqId(String rslt) {
        String sql = "select to_seq_id from fm_transition where result =? and from_seq_id = ? and process_id = ?";
        Parameters p = new Parameters().add(1, rslt).add(2, sequence_id).add(3, process_id);
        Result<DataRow> result = App.Current.DbPortal.ExecuteRecord("core_and", sql, p);
        if (result.HasError) {
            App.Current.showError(pn_qm_smt_rejects_record_mgr.this.getContext(), result.Error);
            return -2;
        }
        if (result.Value == null) {
            App.Current.showInfo(pn_qm_smt_rejects_record_mgr.this.getContext(), "请扫描PASS或者FAIL");
            return -3;
        } else {
            return result.Value.getValue("to_seq_id", 0);
        }
    }

    private void toastChooseResult() {
        String sql = "exec p_fm_smt_get_reason ?";
        Parameters p = new Parameters().add(1, 1);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(pn_qm_smt_rejects_record_mgr.this.getContext(), value.Error);
                    return;
                }
                if (value.Value != null) {
                    ArrayList<String> names = new ArrayList<String>();
                    for (DataRow row : value.Value.Rows) {
                        String name = row.getValue("name", "");
                        names.add(name);
                    }

                    WindowManager wm = (WindowManager) App.Current.Workbench.getSystemService(Context.WINDOW_SERVICE);
                    DisplayMetrics dm = new DisplayMetrics();
                    wm.getDefaultDisplay().getMetrics(dm);
                    // 屏幕宽度（像素）
                    width = dm.widthPixels;
                    height = dm.heightPixels;

                    View popupView = View.inflate(pn_qm_smt_rejects_record_mgr.this.getContext(), R.layout.smt_rejects_popupwindow, null);
                    initPopupView(popupView, names);
                    popupWindow = new PopupWindow();
                    popupWindow.setContentView(popupView);
                    popupWindow.setFocusable(true);
                    popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                    popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.style_divider_color));
                    popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 20, 30);
                }
            }
        });
    }

    private void initPopupView(final View popupView, final ArrayList<String> names) {
        final ButtonTextCell buttonTextCell = (ButtonTextCell) popupView.findViewById(R.id.button_text_cell);
        final ButtonTextCell buttonTextCell3 = (ButtonTextCell) popupView.findViewById(R.id.button_text_cell_3);
        final TextCell textCell1 = (TextCell) popupView.findViewById(R.id.text_cell_1);
        LinearLayout linearLayout = (LinearLayout) popupView.findViewById(R.id.linearlayout);
        TextView confirm = (TextView) popupView.findViewById(R.id.confirm);
        TextView cancel = (TextView) popupView.findViewById(R.id.cancel);
        buttonTextCell.Label.setTextColor(Color.BLACK);
        buttonTextCell3.Label.setTextColor(Color.BLACK);
        textCell1.Label.setTextColor(Color.BLACK);

//        //由于mllExpand的parent layout是RelativeLayout，因此需要采用RelativeLayout.LayoutParams类型
//        LinearLayout.LayoutParams lpExpand = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
//        //获取popup window的宽度需要先获取content view，然后再获取宽度
//        lpExpand.width = width / 2;
//        linearLayout.setLayoutParams();

        if (buttonTextCell != null) {
            buttonTextCell.setLabelText("不良描述");
            buttonTextCell.setReadOnly();
            buttonTextCell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    final StringBuffer nameMessage = new StringBuffer();
                    final boolean[] selected = new boolean[names.size()];
                    multiChoiceDialog(nameMessage, selected, names, buttonTextCell);
                }
            });
        }

        if (textCell1 != null) {
            textCell1.setLabelText("不良位号");
        }

        if (buttonTextCell3 != null) {
            buttonTextCell3.setLabelText("流向工序");
            buttonTextCell3.setReadOnly();
        }

        String sql = "exec p_qm_smt_check_seq ?,?,?";
        Parameters p = new Parameters().add(1, "FAIL").add(2, sequence_id).add(3, process_id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    return;
                }
                if (value.Value != null) {
                    if (value.Value.Rows.size() == 1) {
                        buttonTextCell3.setReadOnly();
                        buttonTextCell3.setContentText(value.Value.Rows.get(0).getValue("to_seq_name", ""));
//                        buttonTextCell3.setContentText("FAIL-" + value.Value.Rows.get(0).getValue("to_seq_name", ""));
                    } else {
                        loadToSeq(value.Value, buttonTextCell3);
                    }
                }
            }
        });

        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(buttonTextCell.getContentText())) {
                    App.Current.showInfo(pn_qm_smt_rejects_record_mgr.this.getContext(), "请输入不良情况");
                } else if (TextUtils.isEmpty(textCell1.getContentText())) {
                    App.Current.showInfo(pn_qm_smt_rejects_record_mgr.this.getContext(), "请输入不良描述");
                } else {
                    xml = getTestBindXml(buttonTextCell.getContentText(), textCell1.getContentText());
                    CommitScanNumberCreate(mainCode, "FAIL");
                    popupWindow.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }

    private void multiChoiceDialog(final StringBuffer nameMessage, final boolean[] selected, final ArrayList<String> names, final ButtonTextCell buttonTextCell) {
        new AlertDialog.Builder(pn_qm_smt_rejects_record_mgr.this.getContext()).setTitle("请选择")
                .setNegativeButton("取消", null).setPositiveButton("确定", null)
                .setMultiChoiceItems(names.toArray(new String[0]), selected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        selected[i] = b;
                        if (selected[i] == true) {
                            nameMessage.append(names.get(i));
                            nameMessage.append(",");
                        }
                        buttonTextCell.setContentText(nameMessage.toString());
                    }
                }).show();
    }

    /**
     * @param contentText 不良情况
     * @param s           不良位号
     * @return 提交的xml
     */
    private String getTestBindXml(String contentText, String s) {  //work_type为检查提交的xml
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

    private String getRepairXml(String contentText, String s) {  //work_type为检查提交的xml
        Map<String, String> head_entry = new HashMap<String, String>();
        ArrayList<Map<String, String>> item_entries = new ArrayList<Map<String, String>>();
        Map<String, String> item_entry = new HashMap<String, String>();
        item_entry.put("sn_no", mainCode);
        item_entry.put("sn_type", work_type);
        item_entry.put("ng_type", contentText);
        item_entry.put("ng_analysis", s);
        item_entries.add(item_entry);
        String xml = XmlHelper.createXml("bindings", head_entry, "bindings", "binding", item_entries);
        return xml;
    }

    private void initScanNumber(final String lot_number) {
        String sql = "exec p_qm_sop_get_scan_number ?,?";
        Parameters p = new Parameters().add(1, textcell_1.getContentText().replace("\n", "")).add(2, split[0]);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.toastInfo(getContext(), value.Error);
                    return;
                }
                if (value.Value != null) {
                    scanCounts = value.Value.getValue("partcount", -1);
                    order_id = value.Value.getValue("id", 0L);
                    sequence_id = value.Value.getValue("sequence_id", 0);
                    process_id = value.Value.getValue("process_id", 0);
                    work_type = value.Value.getValue("work_type", "");
                    CheckMainScanNumber(lot_number);
                } else {
                    App.Current.showInfo(getContext(), "序列号有误，请重新扫描！");
                    return;
                }
            }
        });
    }

    private void loadProductionName(final ButtonTextCell text) {
//        String sql = "exec p_qm_sop_production_items ?,?";
        String sql = "exec p_qm_sop_change_production ?,?";
        Parameters p = new Parameters().add(1, App.Current.UserID).add(2, textcell_1.getContentText().replace("\n", ""));
        final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (result.HasError) {
            App.Current.showError(this.getContext(), result.Error);
            return;
        }
        if (result.Value != null) {

            ArrayList<String> names = new ArrayList<String>();
            for (DataRow row : result.Value.Rows) {
                StringBuffer name = new StringBuffer();
                name.append(row.getValue("name", ""));
                names.add(name.toString());
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which >= 0) {
                        DataRow row = result.Value.Rows.get(which);
                        StringBuffer name = new StringBuffer();
                        name.append(row.getValue("name", ""));
                        text.setContentText(name.toString());
                        seq_id = row.getValue("id", 0);
                        edit.putInt("seq_id", seq_id);
                        split = name.toString().split(",");
                        loadListView();
                    }
                    dialog.dismiss();
                }
            };
            new AlertDialog.Builder(pn_qm_smt_rejects_record_mgr.this.getContext()).setTitle("请选择")
                    .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                            (text.getContentText().toString()), listener)
                    .setNegativeButton("取消", null).show();
        }
    }

    @Override
    public void commit() {
        super.commit();
        edit.putString("code", textcell_1.getContentText());
        edit.putString("item_name", textcell_2.getContentText());
        edit.putString("work_line", textcell_3.getContentText());
        edit.putString("segment", textcell_4.getContentText());
//        edit.putString("worker_code", textcell_5.getContentText());
        edit.commit();
        App.Current.toastInfo(getContext(), "提交成功");
        close();
    }

    private class MyListViewAdapter extends BaseAdapter {
        private DataTable dataTable;

        public MyListViewAdapter(DataTable value) {
            this.dataTable = value;
        }

        @Override
        public int getCount() {
            return dataTable.Rows.size();
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
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.smt_record_items, null);
                viewHolder = new ViewHolder();
                viewHolder.textView_1 = (TextView) convertView.findViewById(R.id.textview_1);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String lot_number = dataTable.Rows.get(i).getValue("lot_number", "");
            String sequence_name = dataTable.Rows.get(i).getValue("sequence_name", "");
            String to_sequence_name = dataTable.Rows.get(i).getValue("to_sequence_name", "");
            String worker_name = dataTable.Rows.get(i).getValue("worker_name", "");
            String work_type = dataTable.Rows.get(i).getValue("work_type", "");
            viewHolder.textView_1.setText(lot_number + "," + sequence_name + "," + to_sequence_name + "," +
                    worker_name + "," + work_type);
            return convertView;
        }
    }

    class ViewHolder {
        private TextView textView_1;
    }

    private class MyAdapter extends BaseAdapter {
        private ArrayList<SmtWhBean> smtWhBeanArrayList;

        public MyAdapter(ArrayList<SmtWhBean> listViewDatas) {
            smtWhBeanArrayList = listViewDatas;
        }

        public void fresh(ArrayList<SmtWhBean> smtWhBeans) {
            smtWhBeanArrayList = smtWhBeans;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return smtWhBeanArrayList.size();
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
            MyViewHolder viewHolder;
            if (view == null) {
                viewHolder = new MyViewHolder();
                view = View.inflate(getContext(), R.layout.item_smt_rejects, null);
                viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
                viewHolder.num = (TextView) view.findViewById(R.id.num);
                viewHolder.textView1 = (TextView) view.findViewById(R.id.textview_1);
                view.setTag(viewHolder);
            } else {
                viewHolder = (MyViewHolder) view.getTag();
            }
            viewHolder.icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
            viewHolder.num.setText(String.valueOf(i + 1));
            if (viewHolder.textView1 != null) {
                viewHolder.textView1.setText(smtWhBeanArrayList.get(i).getPartNumber() + "," + smtWhBeanArrayList.get(i).getItemNumber());
            }
            return view;
        }
    }

    class MyViewHolder {
        private ImageView icon;
        private TextView num;
        private TextView textView1;
    }
}
