package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

public class pn_wo_part_online_editor extends pn_editor {

    public ButtonTextCell txt_wo_order_cell;
    public TextCell txt_item_code_cell;
    public TextCell txt_item_name_cell;
    public TextCell txt_vendor_name_cell;
    public TextCell txt_date_code_cell;
    public TextCell txt_lot_number_cell;
    public TextCell txt_create_user_cell;
    public TextCell txt_check_user_cell;
    public DecimalCell txt_quantity_cell;
    public ButtonTextCell txt_location_cell;
    public TextCell txt_warehouse_cell;
    public TextCell txt_vendor_model_cell;
    public TextCell txt_vendor_lot_cell;
    public ButtonTextCell textCell;

    private Long id;
    private Long task_order_id;
    private DataRow _order_row;
    private String task_order_code;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    private int check_user_id;

    public pn_wo_part_online_editor(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_wo_part_online_editor, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {

        super.onPrepared();

        sharedPreferences = getContext().getSharedPreferences("part_online_scan", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();

        this.txt_wo_order_cell = (ButtonTextCell) this.findViewById(R.id.txt_wo_order_cell);
        this.txt_item_code_cell = (TextCell) this.findViewById(R.id.txt_item_code_cell);
        this.txt_item_name_cell = (TextCell) this.findViewById(R.id.txt_item_name_cell);
        this.txt_vendor_name_cell = (TextCell) this.findViewById(R.id.txt_vendor_name_cell);
        this.txt_date_code_cell = (TextCell) this.findViewById(R.id.txt_date_code_cell);
        this.txt_lot_number_cell = (TextCell) this.findViewById(R.id.txt_lot_number_cell);
        this.txt_create_user_cell = (TextCell) this.findViewById(R.id.txt_create_user_cell);
        this.txt_check_user_cell = (TextCell) this.findViewById(R.id.txt_check_user_cell);
        this.txt_quantity_cell = (DecimalCell) this.findViewById(R.id.txt_quantity_cell);
        this.txt_location_cell = (ButtonTextCell) this.findViewById(R.id.txt_location_cell);
        this.txt_warehouse_cell = (TextCell) this.findViewById(R.id.txt_warehouse_cell);
        this.txt_vendor_model_cell = (TextCell) this.findViewById(R.id.txt_vendor_model_cell);
        this.txt_vendor_lot_cell = (TextCell) this.findViewById(R.id.txt_vendor_lot_cell);
        textCell = (ButtonTextCell) this.findViewById(R.id.test);

        id = this.Parameters.get("id", 0L);
        task_order_id = this.Parameters.get("task_order_id", 0L);
        task_order_code = this.Parameters.get("task_order_code", "");

        if (task_order_id == 0) {
            task_order_id = sharedPreferences.getLong("order_task_id", 0L);
            id = sharedPreferences.getLong("id", 0L);
        }
        edit.putLong("order_task_id", task_order_id);
        edit.putLong("id", id);
        edit.commit();


        if (this.txt_wo_order_cell != null) {
            this.txt_wo_order_cell.setLabelText("工单号");
            this.txt_wo_order_cell.setReadOnly();
            this.txt_wo_order_cell.Button.setVisibility(GONE);
            this.txt_wo_order_cell.setContentText(task_order_code);
//            this.txt_wo_order_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_right_light"));
//            this.txt_wo_order_cell.Button.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    loadComfirmName(txt_wo_order_cell);
//                }
//            });

        }

        if (this.txt_item_code_cell != null) {
            this.txt_item_code_cell.setLabelText("物料编码");
            this.txt_item_code_cell.setReadOnly();
        }

        if (this.txt_item_name_cell != null) {
            this.txt_item_name_cell.setLabelText("物料名称");
            this.txt_item_name_cell.setReadOnly();
            this.txt_item_name_cell.TextBox.setSingleLine(true);
        }

        if (this.txt_vendor_name_cell != null) {
            this.txt_vendor_name_cell.setLabelText("供应商");
            this.txt_vendor_name_cell.setReadOnly();
            this.txt_vendor_name_cell.TextBox.setSingleLine(true);
        }

        if (this.txt_date_code_cell != null) {
            this.txt_date_code_cell.setLabelText("D/C");
            this.txt_date_code_cell.setReadOnly();
        }

        if (this.txt_lot_number_cell != null) {
            this.txt_lot_number_cell.setLabelText("批次");
            this.txt_lot_number_cell.setReadOnly();
        }

        if (this.txt_create_user_cell != null) {
            this.txt_create_user_cell.setLabelText("上料员");
            this.txt_create_user_cell.setContentText(App.Current.UserCode + "-" + App.Current.UserName);
            this.txt_create_user_cell.setReadOnly();
        }

        if (this.txt_check_user_cell != null) {
            this.txt_check_user_cell.setLabelText("确认员");
            this.txt_check_user_cell.setReadOnly();
        }

        if (this.txt_quantity_cell != null) {
            this.txt_quantity_cell.setLabelText("数量");
            this.txt_quantity_cell.setReadOnly();
        }

        if (this.txt_location_cell != null) {

            this.txt_location_cell.setLabelText("上线工位");
            this.txt_location_cell.setReadOnly();
            this.txt_location_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_light"));
            this.txt_location_cell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    pn_wo_part_online_editor.this.txt_location_cell.setContentText("");

                }

            });
        }

        if (this.txt_warehouse_cell != null) {
            this.txt_warehouse_cell.setLabelText("产线/站位");
            this.txt_warehouse_cell.setReadOnly();
        }

        if (this.txt_vendor_lot_cell != null) {
            this.txt_vendor_lot_cell.setLabelText("厂家批号");
            this.txt_vendor_lot_cell.setReadOnly();
        }

        if (this.textCell != null) {
            this.textCell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_light"));
            this.textCell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.Current.Workbench.onShake();
                }
            });
        }

        if (this.txt_vendor_model_cell != null) {
            this.txt_vendor_model_cell.setLabelText("厂家型号");
            this.txt_vendor_model_cell.setReadOnly();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        this.txt_quantity_cell.onActivityResult(requestCode, resultCode, intent);
    }

    private String _scan_quantity;
    private String _date_code;
    private String _item_code;

    @Override
    public void onScan(final String barcode) {
        final String bar_code = barcode.trim();

        //扫描批次条码
        if (bar_code.startsWith("CRQ:")) {
            String str = bar_code.substring(4, bar_code.length());
            String[] arr = str.split("-");
            if (arr.length > 3) {
                String lot = arr[0];
                _item_code = arr[1];
                _scan_quantity = arr[2];
                _date_code = arr[3];
                this.loadOrderItem(lot, _item_code, _date_code);
            } else {
                App.Current.showError(this.getContext(), "批次条码错误,请确认条码格式");
            }
        } else if (bar_code.startsWith("Z:")) {
            String loc = bar_code.substring(2, bar_code.length());
            String locs = this.txt_location_cell.getContentText().trim();
            if (locs.contains(loc)) {
                return;
            }

            if (locs.length() > 0) {
                locs += ", ";
            }

            this.txt_location_cell.setContentText(locs + loc);
        }
// else if (bar_code.startsWith("WO:") || bar_code.startsWith("TO:")) {    //生产任务
//            String sql = "exec p_qm_smt_rejects_record_get_task ?";
//            String substring = bar_code.substring(3, bar_code.length());
//            Parameters p = new Parameters().add(1, substring);
//            pn_wo_part_online_editor.this.ProgressDialog.show();
//            App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
//                @Override
//                public void handleMessage(Message msg) {
//                    Result<DataRow> value = Value;
//                    if (value.HasError) {
//                        pn_wo_part_online_editor.this.ProgressDialog.dismiss();
//                        App.Current.toastInfo(getContext(), value.Error);
//                        return;
//                    }
//                    if (value.Value != null) {
//                        pn_wo_part_online_editor.this.ProgressDialog.dismiss();
//                        String task_order_code = value.Value.getValue("task_order_code", "");
//                        _order_id = value.Value.getValue("task_order_id", 0L);
//                        pn_wo_part_online_editor.this.txt_wo_order_cell.setContentText(task_order_code);
//                    } else {
//                        pn_wo_part_online_editor.this.ProgressDialog.dismiss();
//                        App.Current.toastInfo(getContext(), "扫描生产任务有误");
//                    }
//                }
//            });
//        }
        else if (bar_code.startsWith("M")) {
            String sql = "select t0.code, t0.name, t1.id from fm_worker t0\n" +
                         "LEFT JOIN core_user t1 ON t0.code = t1.code\n" +
                         " where t0.code = ?";
            Parameters p = new Parameters().add(1, bar_code);
            App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
                @Override
                public void handleMessage(Message msg) {
                    Result<DataRow> value = Value;
                    if (value.HasError) {
                        App.Current.toastInfo(getContext(), value.Error);
                    } else {
                        if (value.Value != null) {
                            String code = value.Value.getValue("code", "");
                            String name = value.Value.getValue("name", "");
                            check_user_id = value.Value.getValue("id", 0);
                            txt_check_user_cell.setContentText(code + "-" + name);
                            if (txt_check_user_cell.getContentText().trim().equals(txt_create_user_cell.getContentText().trim())) {
                                App.Current.toastError(pn_wo_part_online_editor.this.getContext(), "上料员和确认员不能为同一个人！");
                                txt_check_user_cell.setContentText("");
                            }
                        } else {
                            App.Current.showError(pn_wo_part_online_editor.this.getContext(), "此作业人员没有维护！");
                        }
                    }
                }
            });
        }


    }

//    private void loadComfirmName(ButtonTextCell textcell_1) {
//        Link link = new Link("pane://x:code=pn_fm_wo_part_online_parameter_mgr");
////        link.Parameters.add("textcell", textcell_1);
//        link.Open(null, getContext(), null);
//        this.close();
//    }

    public void loadOrderItem(String lot_number, String item_code, String date_code) {
        this.ProgressDialog.show();
        String sql = "exec p_mm_wo_part_online_get_order_item_and ?,?,?,?,?";
        Parameters p = new Parameters().add(1, task_order_id).add(2, lot_number).add(3, item_code).add(4, date_code).add(5, id);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_wo_part_online_editor.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_wo_part_online_editor.this.getContext(), result.Error);
                    return;
                }

                _order_row = result.Value;
                if (_order_row == null) {
                    App.Current.showError(pn_wo_part_online_editor.this.getContext(), "没有数据。");
                    pn_wo_part_online_editor.this.clear();
                    pn_wo_part_online_editor.this.Header.setTitleText("工单配膳");
                    return;
                }

                //_total = _order_row.getValue("total", Integer.class);

                boolean disable_item = _order_row.getValue("disable_item", Boolean.class);
                if (disable_item) {
                    new AlertDialog.Builder(pn_wo_part_online_editor.this.getContext())
                            .setMessage("你扫描的物料是禁用物料，请确定物料是否正确？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            initUi();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            _order_row = null;
                        }
                    })
                            .show();
                } else {
                    initUi();
                }

            }
        });
    }

    public void initUi() {
//        pn_wo_part_online_editor.this.txt_wo_order_cell.setContentText(_order_row.getValue("task_order_code", ""));
        pn_wo_part_online_editor.this.txt_item_code_cell.setContentText(_item_code);
        pn_wo_part_online_editor.this.txt_item_name_cell.setContentText(_order_row.getValue("item_name", ""));
        pn_wo_part_online_editor.this.txt_vendor_name_cell.setContentText(_order_row.getValue("vendor_name", ""));
        pn_wo_part_online_editor.this.txt_date_code_cell.setContentText(_date_code);
        pn_wo_part_online_editor.this.txt_vendor_lot_cell.setContentText(_order_row.getValue("vendor_lot", ""));
        pn_wo_part_online_editor.this.txt_vendor_model_cell.setContentText(_order_row.getValue("vendor_model", ""));
        pn_wo_part_online_editor.this.txt_lot_number_cell.setContentText(_order_row.getValue("lot_number", ""));
        pn_wo_part_online_editor.this.txt_warehouse_cell.setContentText(_order_row.getValue("prd_line_name", "") + '/' + _order_row.getValue("work_station_code", ""));
        pn_wo_part_online_editor.this.txt_quantity_cell.setContentText(_scan_quantity);
    }

    @Override
    public void commit() {
        if (_order_row == null) {
            App.Current.showError(this.getContext(), "没有退料指令数据，不能提交。");
            return;
        }

        final String quantity = txt_quantity_cell.getContentText();
        if (quantity == null || quantity.length() == 0) {
            App.Current.showError(this.getContext(), "没有输入数量，不能提交。");
            return;
        }

        if (TextUtils.isEmpty(txt_create_user_cell.getContentText())) {
            App.Current.showError(this.getContext(), "上料员不能为空！");
            return;
        }

        if (TextUtils.isEmpty(txt_check_user_cell.getContentText())) {
            App.Current.showError(this.getContext(), "确认员不能为空！");
            return;
        }

        final String lot_number = this.txt_lot_number_cell.getContentText();


        final String locations = txt_location_cell.getContentText().trim();
        if (locations == null || locations.length() == 0) {
            App.Current.showError(this.getContext(), "没有扫描位，不能提交。");
            return;
        }

        final String order_code = _order_row.getValue("task_order_code", "");

        final long vendor_id = _order_row.getValue("vendor_id", 0L);
        final String vendor_name = _order_row.getValue("vendor_name", "");
        final String date_code = _order_row.getValue("date_code", "");
        final String vendor_lot = _order_row.getValue("vendor_lot", "");
        final String vendor_model = _order_row.getValue("vendor_model", "");
        final String work_line = _order_row.getValue("prd_line_name", "");
        final long item_id = _order_row.getValue("item_id", 0L);
        final boolean disable_item = _order_row.getValue("disable_item", Boolean.class);

        String sql = "exec fm_check_online_qty ?,?";
        Parameters p = new Parameters().add(1, task_order_id).add(2, lot_number);
        App.Current.DbPortal.ExecuteRecordAsync(Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                } else {
                    if (value.Value != null) {
                        int send_quantity = value.Value.getValue("send_quantity", new BigDecimal(0)).intValue();
                        int online_qiantity = value.Value.getValue("online_qiantity", new BigDecimal(0)).intValue();
                        if ((online_qiantity + Integer.valueOf(quantity)) > send_quantity) {         //上料数量超过发料数量
                            App.Current.toastError(getContext(), "发料数量为" + send_quantity + ",已上料数量为" + online_qiantity + ",当前预备上料数量为" + quantity);
                        } else {
                            App.Current.question(getContext(), "确定要提交吗？", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();

                                    Map<String, String> entry = new HashMap<String, String>();
                                    entry.put("code", order_code);
                                    entry.put("create_user", App.Current.UserID);
                                    entry.put("check_user", String.valueOf(check_user_id));
                                    entry.put("vendor_id", String.valueOf(vendor_id));
                                    entry.put("item_id", String.valueOf(item_id));
                                    entry.put("vendor_name", vendor_name);
                                    entry.put("date_code", date_code);
                                    entry.put("vendor_lot", vendor_lot);
                                    entry.put("vendor_model", vendor_model);
                                    if (disable_item) {
                                        entry.put("disable_item", "1");
                                    } else {
                                        entry.put("disable_item", "0");
                                    }
                                    entry.put("task_order_id", String.valueOf(task_order_id));
                                    entry.put("lot_number", lot_number);
                                    entry.put("quantity", quantity);
                                    entry.put("work_line", work_line);
                                    entry.put("locations", String.valueOf(locations));

                                    ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
                                    entries.add(entry);

                                    //生成XML数据，并传给存储过程
                                    String xml = XmlHelper.createXml("wo_online", null, null, "wo_online_item", entries);
                                    Log.e("len", "XML:" + xml);
                                    String sql = "exec p_mm_wo_part_online_create ?,?";
                                    Connection conn = App.Current.DbPortal.CreateConnection(pn_wo_part_online_editor.this.Connector);
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
                                                App.Current.showError(pn_wo_part_online_editor.this.getContext(), rs.Error);
                                                return;
                                            }

                                            App.Current.toastInfo(pn_wo_part_online_editor.this.getContext(), "提交成功");
                                            _order_row = null;
                                            pn_wo_part_online_editor.this.clear();
                                        }
                                    } catch (SQLException e) {
                                        App.Current.showInfo(pn_wo_part_online_editor.this.getContext(), e.getMessage());
                                        e.printStackTrace();

                                        pn_wo_part_online_editor.this.clear();
                                    }
                                }
                            });
                        }
                    } else {
                        App.Current.question(getContext(), "确定要提交吗？", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                                Map<String, String> entry = new HashMap<String, String>();
                                entry.put("code", order_code);
                                entry.put("create_user", App.Current.UserID);
                                entry.put("check_user", String.valueOf(check_user_id));
                                entry.put("vendor_id", String.valueOf(vendor_id));
                                entry.put("item_id", String.valueOf(item_id));
                                entry.put("vendor_name", vendor_name);
                                entry.put("date_code", date_code);
                                entry.put("vendor_lot", vendor_lot);
                                entry.put("vendor_model", vendor_model);
                                if (disable_item) {
                                    entry.put("disable_item", "1");
                                } else {
                                    entry.put("disable_item", "0");
                                }
                                entry.put("task_order_id", String.valueOf(task_order_id));
                                entry.put("lot_number", lot_number);
                                entry.put("quantity", quantity);
                                entry.put("work_line", work_line);
                                entry.put("locations", String.valueOf(locations));

                                ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
                                entries.add(entry);

                                //生成XML数据，并传给存储过程
                                String xml = XmlHelper.createXml("wo_online", null, null, "wo_online_item", entries);
                                Log.e("len", "XML:" + xml);
                                String sql = "exec p_mm_wo_part_online_create ?,?";
                                Connection conn = App.Current.DbPortal.CreateConnection(pn_wo_part_online_editor.this.Connector);
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
                                            App.Current.showError(pn_wo_part_online_editor.this.getContext(), rs.Error);
                                            return;
                                        }

                                        App.Current.toastInfo(pn_wo_part_online_editor.this.getContext(), "提交成功");
                                        _order_row = null;
                                        pn_wo_part_online_editor.this.clear();
                                    }
                                } catch (SQLException e) {
                                    App.Current.showInfo(pn_wo_part_online_editor.this.getContext(), e.getMessage());
                                    e.printStackTrace();

                                    pn_wo_part_online_editor.this.clear();
                                }
                            }
                        });
                    }
                }
            }
        });

    }


    public void SaveWotask() {
        edit.putString("code", this.txt_wo_order_cell.getContentText());
        edit.putLong("order_task_id", task_order_id);
        edit.putString("work_line", this.txt_warehouse_cell.getContentText());
        edit.putString("station", this.txt_location_cell.getContentText());
//        edit.putString("worker_code", textcell_5.getContentText());
        edit.commit();
        //App.Current.toastInfo(getContext(), "提交成功");
        //close();
    }

    public void clear() {
        this.txt_item_code_cell.setContentText("");
        this.txt_item_name_cell.setContentText("");
        this.txt_check_user_cell.setContentText("");
        this.txt_vendor_name_cell.setContentText("");
        this.txt_date_code_cell.setContentText("");
        this.txt_lot_number_cell.setContentText("");
        this.txt_warehouse_cell.setContentText("");
        this.txt_location_cell.setContentText("");
        this.txt_quantity_cell.setContentText("");
        this.txt_vendor_lot_cell.setContentText("");
        this.txt_vendor_model_cell.setContentText("");
    }
}
