package dynsoft.xone.android.wms;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.blueprint.Demo_ad_escActivity;
import dynsoft.xone.android.blueprint.So_issue_Activity;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class pn_so_issue_editor extends pn_editor {

    private Boolean binding_carton;

    public pn_so_issue_editor(Context context) {
        super(context);
    }

    public TextCell txt_issue_order_cell;
    public TextCell txt_item_name_cell;
    public TextCell txt_customer_name_cell;
    public TextCell txt_date_code_cell;
    public TextCell txt_carton_no_cell;
    public ButtonTextCell txt_lot_number_cell;
    public ButtonTextCell txt_sn_no_cell;
    public TextCell txt_quantity_cell;
    public TextCell txt_warehouse_cell;
    public ButtonTextCell txt_issue_quantity_cell;

    public TextCell txt_location_cell;
    public TextCell txt_onhand_cell;
    public ButtonTextCell txt_surplus_cell;

    public Long order_id;
    public BigDecimal sum_qty;
    public BigDecimal sum_issued_qty;

    public ImageButton btn_prev;
    public ImageButton btn_next;

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_so_issue_editor, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {

        super.onPrepared();

        this.txt_issue_order_cell = (TextCell) this.findViewById(R.id.txt_issue_order_cell);
        this.txt_item_name_cell = (TextCell) this.findViewById(R.id.txt_item_name_cell);
        this.txt_customer_name_cell = (TextCell) this.findViewById(R.id.txt_customer_name_cell);
        this.txt_date_code_cell = (TextCell) this.findViewById(R.id.txt_date_code_cell);
        this.txt_carton_no_cell = (TextCell) this.findViewById(R.id.txt_carton_no_cell);
        this.txt_lot_number_cell = (ButtonTextCell) this.findViewById(R.id.txt_lot_number_cell);
        this.txt_quantity_cell = (TextCell) this.findViewById(R.id.txt_quantity_cell);
        this.txt_warehouse_cell = (TextCell) this.findViewById(R.id.txt_warehouse_cell);
        this.txt_sn_no_cell = (ButtonTextCell) this.findViewById(R.id.txt_sn_no_cell);
        this.txt_issue_quantity_cell = (ButtonTextCell) this.findViewById(R.id.txt_issue_quantity_cell);
        this.txt_location_cell = (TextCell) this.findViewById(R.id.txt_location_cell);
        this.txt_onhand_cell = (TextCell) this.findViewById(R.id.txt_onhand_cell);
        this.txt_surplus_cell = (ButtonTextCell) this.findViewById(R.id.txt_surplus_cell);
        this.btn_prev = (ImageButton) this.findViewById(R.id.btn_prev);
        this.btn_next = (ImageButton) this.findViewById(R.id.btn_next);

        order_id = this.Parameters.get("order_id", 0L);

        if (this.txt_issue_order_cell != null) {
            this.txt_issue_order_cell.setLabelText("销货通知");
            this.txt_issue_order_cell.setReadOnly();
        }

        if (this.txt_item_name_cell != null) {
            this.txt_item_name_cell.setLabelText("产品");
            this.txt_item_name_cell.setReadOnly();
            this.txt_item_name_cell.TextBox.setSingleLine(true);
        }

        if (this.txt_customer_name_cell != null) {
            this.txt_customer_name_cell.setLabelText("客户");
            this.txt_customer_name_cell.setReadOnly();
            this.txt_customer_name_cell.TextBox.setSingleLine(true);
        }

        if (this.txt_date_code_cell != null) {
            this.txt_date_code_cell.setLabelText("D/C");
            this.txt_date_code_cell.setReadOnly();
        }

        if (this.txt_carton_no_cell != null) {
            this.txt_carton_no_cell.setLabelText("箱号");
            this.txt_carton_no_cell.TextBox.setInputType(InputType.TYPE_CLASS_NUMBER);
            //this.txt_carton_no_cell.setReadOnly();
        }

        if (this.txt_lot_number_cell != null) {
            this.txt_lot_number_cell.setLabelText("批次");
            this.txt_lot_number_cell.setReadOnly();
            this.txt_lot_number_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_label_light"));
            this.txt_lot_number_cell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sn_nos = pn_so_issue_editor.this.txt_sn_no_cell.getContentText();
                    String qty = pn_so_issue_editor.this.txt_surplus_cell.getContentText();
                    pn_so_issue_editor.this.printLabel2(sn_nos, qty);
                }
            });
        }

        if (this.txt_quantity_cell != null) {
            this.txt_quantity_cell.setLabelText("数量");
            this.txt_quantity_cell.setReadOnly();
        }

        if (this.txt_warehouse_cell != null) {
            this.txt_warehouse_cell.setLabelText("库位");
            this.txt_warehouse_cell.setReadOnly();
        }

        if (this.txt_location_cell != null) {
            this.txt_location_cell.setLabelText("储位");
            this.txt_location_cell.setReadOnly();
        }

        if (this.txt_sn_no_cell != null) {
            this.txt_sn_no_cell.setLabelText("产品序列");
            this.txt_sn_no_cell.setReadOnly();
            this.txt_sn_no_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_right_light"));
            this.txt_sn_no_cell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sn_nos = pn_so_issue_editor.this.txt_lot_number_cell.getContentText();
                    if (!sn_nos.equals("")) print_Label();
//                    pn_so_issue_editor.this.loadsnno(sn_nos);
                }
            });
        }

        if (this.txt_issue_quantity_cell != null) {
            this.txt_issue_quantity_cell.setLabelText("实发数量");
            this.txt_issue_quantity_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_label_light"));
            this.txt_issue_quantity_cell.TextBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (_lot_row != null) {
                        String str = s.toString();
                        if (str == null || str.length() == 0) {
                            str = "0";
                        }
                        BigDecimal onhand_quantity = _lot_row.getValue("quantity", BigDecimal.ZERO);
                        BigDecimal issue_quantity = new BigDecimal(str);
                        BigDecimal surplus = onhand_quantity.subtract(issue_quantity);
                        pn_so_issue_editor.this.txt_surplus_cell.setContentText(App.formatNumber(surplus, "0.##"));
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            this.txt_issue_quantity_cell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String qty = pn_so_issue_editor.this.txt_issue_quantity_cell.getContentText();
                    pn_so_issue_editor.this.printLabel(qty);
                }
            });
        }

        if (this.txt_onhand_cell != null) {
            this.txt_onhand_cell.setLabelText("现有量");
            this.txt_onhand_cell.setReadOnly();
        }

        if (this.txt_surplus_cell != null) {
            this.txt_surplus_cell.setLabelText("剩余量");
            this.txt_surplus_cell.setReadOnly();
            this.txt_surplus_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_label_light"));
            this.txt_surplus_cell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String qty = pn_so_issue_editor.this.txt_surplus_cell.getContentText();
                    pn_so_issue_editor.this.printLabel(qty);
                }
            });
        }

        if (this.btn_prev != null) {
            this.btn_prev.setImageBitmap(App.Current.ResourceManager.getImage("@/core_prev_white"));
            this.btn_prev.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_so_issue_editor.this.prev();
                }
            });
        }

        if (this.btn_next != null) {
            this.btn_next.setImageBitmap(App.Current.ResourceManager.getImage("@/core_next_white"));
            this.btn_next.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_so_issue_editor.this.next();
                }
            });
        }

        _order_id = this.Parameters.get("order_id", 0L);
        _order_code = this.Parameters.get("order_code", "");
        this.txt_issue_order_cell.setContentText(_order_code);

        this.loadTaskOrderItem(1L);
        init_so_issue_order_head_info();
    }

    private Long _order_id;
    private String _order_code;
    private Long _rownum;
    private Integer _total = 0;
    private DataRow _order_row;
    private DataRow _lot_row;
    private DataRow so_issue_row;

    public void prev() {
        if (_rownum > 1) {
            this.loadTaskOrderItem(_rownum - 1);
        } else {
            App.Current.showInfo(pn_so_issue_editor.this.getContext(), "已经是第一条。");
        }
    }

    public void next() {
        if (_rownum < _total) {
            this.loadTaskOrderItem(_rownum + 1);
        } else {
            App.Current.showInfo(pn_so_issue_editor.this.getContext(), "已经是最后一条。");
        }
    }

    @Override
    public void onScan(final String barcode) {
        final String bar_code = barcode.trim();

        //扫描批次条码
        if (bar_code.startsWith("CRQ:")) {
            int pos = bar_code.indexOf("-");
            String lot = bar_code.substring(4, pos);
            this.loadLotNumber(lot);
        } else if (bar_code.startsWith("C:")) {
            String lot = bar_code.substring(2, bar_code.length());
            this.loadLotNumber(lot);
        } else {

            String snnos = this.txt_sn_no_cell.getContentText().trim();
            if (snnos.contains(bar_code)) {
                App.Current.showError(this.getContext(), "重复序列号！");
                return;
            }

            if (snnos.length() > 0) {
                snnos += ", ";
            }

            this.txt_sn_no_cell.setContentText(snnos + bar_code);
            String sn = snnos + barcode;
            String[] v = sn.split(",");
            this.txt_issue_quantity_cell.setContentText(String.valueOf(v.length));
            //loadsnno(bar_code);
        }

    }

    public void loadTaskOrderItem(long index) {
        this.ProgressDialog.show();

        String sql = "exec p_mm_so_issue_get_item ?,?,?";
        Parameters p = new Parameters().add(1, App.Current.UserID).add(2, _order_id).add(3, index);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_issue_editor.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_issue_editor.this.getContext(), result.Error);
                    return;
                }

                DataRow row = result.Value;
                if (row == null) {
                    App.Current.showError(pn_so_issue_editor.this.getContext(), "没有数据。");
                    pn_so_issue_editor.this.clearAll();
                    pn_so_issue_editor.this.Header.setTitleText("销货通知");
                    return;
                }

                _order_row = row;
                _total = row.getValue("total", Integer.class);
                _rownum = row.getValue("rownum", Long.class);

                if (_total > 0) {
                    pn_so_issue_editor.this.Header.setTitleText("销货通知(共" + String.valueOf(_total) + "条)");
                } else {
                    pn_so_issue_editor.this.Header.setTitleText("销货通知");
                }

                String order_code = row.getValue("code", "") + ", 第" + String.valueOf(_rownum) + "条";
                pn_so_issue_editor.this.txt_issue_order_cell.setContentText(order_code);
                pn_so_issue_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
                pn_so_issue_editor.this.txt_customer_name_cell.setContentText(row.getValue("customer_name", ""));
                pn_so_issue_editor.this.txt_date_code_cell.setContentText(row.getValue("date_code", ""));
                pn_so_issue_editor.this.txt_warehouse_cell.setContentText(row.getValue("warehouse_code", ""));
                pn_so_issue_editor.this.txt_location_cell.setContentText(row.getValue("locations", ""));
                binding_carton = row.getValue("binding_carton", false);

                BigDecimal planned_qty = row.getValue("quantity", BigDecimal.ZERO);
                sum_qty = row.getValue("sum_qty", BigDecimal.ZERO);
                sum_issued_qty = row.getValue("issued_qty", BigDecimal.ZERO);
                BigDecimal issued_qty = row.getValue("closed_quantity", BigDecimal.ZERO);
                BigDecimal open_qty = planned_qty.subtract(issued_qty);

                String qty_str = "总数:" + App.formatNumber(planned_qty, "0.##");
                qty_str += "/已发:" + App.formatNumber(issued_qty, "0.##");
                qty_str += "/未发:" + App.formatNumber(open_qty, "0.##");
                qty_str += "/合计数:" + App.formatNumber(sum_qty, "0.##");

                pn_so_issue_editor.this.txt_quantity_cell.setContentText(qty_str);


                pn_so_issue_editor.this.txt_lot_number_cell.setTag(null);
                pn_so_issue_editor.this.txt_lot_number_cell.setContentText("");
                pn_so_issue_editor.this.txt_issue_quantity_cell.setContentText("");
                pn_so_issue_editor.this.txt_onhand_cell.setContentText("");
                pn_so_issue_editor.this.txt_onhand_cell.setTag(null);
                pn_so_issue_editor.this.txt_surplus_cell.setContentText("");
            }
        });
    }

    public void loadsnno(String sn_no) {
        this.ProgressDialog.show();

        if (_order_row == null) {
            App.Current.showError(this.getContext(), "没有指定出货通知，无法扫描条码。");
            return;
        }
        String sql = "exec get_pack_snno ?";
        Parameters p = new Parameters().add(1, sn_no);
        App.Current.DbPortal.ExecuteRecordAsync("sim_mes_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {

                pn_so_issue_editor.this.ProgressDialog.dismiss();

                Result<DataRow> r = this.Value;
                if (r.HasError) {
                    App.Current.showError(pn_so_issue_editor.this.getContext(), r.Error);
                    return;
                }
                ;

                String result = r.Value.getValue("result", "");
                if (result.length() > 0) {
                    App.Current.showError(pn_so_issue_editor.this.getContext(), result);
                    return;
                }

                String pack_sn_no = r.Value.getValue("pack_snno", "");

                if (pack_sn_no != null && pack_sn_no.length() != 0) {
                    pn_so_issue_editor.this.txt_sn_no_cell.setContentText(pack_sn_no);
                    pn_so_issue_editor.this.txt_quantity_cell.setContentText(r.Value.getValue("count_v", 0).toString());
                }
            }
        });


    }

    public void loadLotNumber(String lotNumber) {
        this.ProgressDialog.show();

        if (_order_row == null) {
            App.Current.showError(this.getContext(), "没有指定出货通知，无法扫描条码。");
            return;
        }

        final long order_id = _order_row.getValue("head_id", Long.class);
        final int line_id = _order_row.getValue("line_id", Integer.class);

        String sql = "exec p_mm_so_issue_get_lot_number ?,?,?";
        Parameters p = new Parameters().add(1, order_id).add(2, line_id).add(3, lotNumber);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {

                pn_so_issue_editor.this.ProgressDialog.dismiss();

                Result<DataRow> r = this.Value;
                if (r.HasError) {
                    App.Current.showError(pn_so_issue_editor.this.getContext(), r.Error);
                    return;
                }

                _lot_row = r.Value;

                if (so_issue_row.getValue("attribute8", "").equals("08")
                        || so_issue_row.getValue("attribute8", "").equals("14")
                        || so_issue_row.getValue("attribute8", "").equals("16")) {
                    print_Label();
                } else {
                    App.Current.question(pn_so_issue_editor.this.getContext(), "确定要打印吗？", (arg0, arg1) -> {
                        print_Label();
                    });
                }

                pn_so_issue_editor.this.showLotNumber(r.Value);


            }
        });

    }

    public void showLotNumber(DataRow row) {
        _lot_row = row;
        String result = _lot_row.getValue("result", "");
        if (result.length() > 0) {

            if (result.indexOf("温馨提示：") == -1) {
                App.Current.showError(pn_so_issue_editor.this.getContext(), result);
                return;
            } else {
                App.Current.question(pn_so_issue_editor.this.getContext(), result, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loaditem();
                    }
                });
            }

        } else {
            loaditem();
        }

    }

    public void loaditem() {
        BigDecimal open_qty = _order_row.getValue("open_quantity", BigDecimal.ZERO);
        BigDecimal onhand_qty = _lot_row.getValue("quantity", BigDecimal.ZERO);

        String issue_qty_str = "";
        String surplus_qty_str = "";
        String onhand_qty_str = App.formatNumber(onhand_qty, "0.##");
        if (open_qty.compareTo(onhand_qty) > 0) {
            issue_qty_str = onhand_qty_str;
        } else {
            issue_qty_str = App.formatNumber(open_qty, "0.##");
            surplus_qty_str = App.formatNumber(onhand_qty.subtract(open_qty), "0.##");
        }
        pn_so_issue_editor.this.txt_lot_number_cell.setTag(_lot_row);
        pn_so_issue_editor.this.txt_lot_number_cell.setContentText(_lot_row.getValue("lot_number", ""));
        pn_so_issue_editor.this.txt_date_code_cell.setContentText(_lot_row.getValue("date_code", ""));
        pn_so_issue_editor.this.txt_location_cell.setContentText(_lot_row.getValue("locations", ""));
        String sn_no = pn_so_issue_editor.this.txt_sn_no_cell.getContentText();
        if (sn_no == null || sn_no.length() == 0) {
            pn_so_issue_editor.this.txt_issue_quantity_cell.setContentText(issue_qty_str);
            pn_so_issue_editor.this.txt_sn_no_cell.setContentText(_lot_row.getValue("sn_no", ""));
        }
        pn_so_issue_editor.this.txt_onhand_cell.setContentText(onhand_qty_str);
        pn_so_issue_editor.this.txt_surplus_cell.setContentText(surplus_qty_str);
    }

    public void printLabel2(String sn_nos, String quantity) {
        if (sn_nos == null || sn_nos.length() == 0) {
            App.Current.showError(this.getContext(), "没有指定需要出货的产品序列号。");
            return;
        }

        if (_order_row == null) {
            App.Current.showError(this.getContext(), "没有销货通知数据。");
            return;
        }

        if (_lot_row == null) {
            App.Current.showError(this.getContext(), "没有批号数据。");
            return;
        }


        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("lot_number", _lot_row.getValue("lot_number", ""));
        parameters.put("quantity", quantity);
        parameters.put("sn_nos", sn_nos);
        App.Current.Print("mm_prod_sn_lot_label", "打印产品标签", parameters);


    }

    public void printLabel(final String quantity) {
        if (quantity == null || quantity.length() == 0) {
            App.Current.showError(this.getContext(), "没有指定标签数量。");
            return;
        }

        if (_order_row == null) {
            App.Current.showError(this.getContext(), "没有销货通知数据。");
            return;
        }

        if (_lot_row == null) {
            App.Current.showError(this.getContext(), "没有批号数据。");
            return;
        }

        final String sn_nos = pn_so_issue_editor.this.txt_sn_no_cell.getContentText();

        if (sn_nos == null || sn_nos.length() == 0) {
            App.Current.showError(this.getContext(), "没有指定需要出货的产品序列号。");
            return;
        }


        final String items[] = {"霍尼韦尔", "芝柯"};

        AlertDialog dialog1 = new AlertDialog.Builder(App.Current.Workbench).setTitle("请选择打印机")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (i == 1) {

                            String sql = "exec get_pint_date";
                            App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, new ResultHandler<DataRow>() {
                                @Override
                                public void handleMessage(Message msg) {
                                    Result<DataRow> value = Value;
                                    if (value.HasError) {
                                        App.Current.toastError(pn_so_issue_editor.this.getContext(), value.Error);
                                        return;
                                    }
                                    if (value.Value != null) {
                                        Intent intent = new Intent();
                                        intent.setClass(App.Current.Workbench, Demo_ad_escActivity.class);
                                        String cur_date = value.Value.getValue("cur_date", "");
                                        intent.putExtra("cur_date", cur_date);
                                        intent.putExtra("org_code", _lot_row.getValue("org_code", ""));
                                        intent.putExtra("item_code", _lot_row.getValue("item_code", ""));
                                        intent.putExtra("vendor_model", _lot_row.getValue("vendor_model", ""));
                                        intent.putExtra("lot_number", _lot_row.getValue("lot_number", ""));
                                        intent.putExtra("vendor_lot", _lot_row.getValue("vendor_lot", ""));
                                        intent.putExtra("date_code", _lot_row.getValue("date_code", ""));
                                        intent.putExtra("quantity", quantity);
                                        intent.putExtra("ut", _order_row.getValue("uom_code", ""));
                                        intent.putExtra("pack_sn_no", sn_nos);
                                        App.Current.Workbench.startActivity(intent);
                                    }
                                }
                            });


                        } else {
                            Map<String, String> parameters = new HashMap<String, String>();
                            parameters.put("org_code", _lot_row.getValue("org_code", ""));
                            parameters.put("item_code", _lot_row.getValue("item_code", ""));
                            parameters.put("vendor_model", _lot_row.getValue("vendor_model", ""));
                            parameters.put("lot_number", _lot_row.getValue("lot_number", ""));
                            parameters.put("vendor_lot", _lot_row.getValue("vendor_lot", ""));
                            parameters.put("date_code", _lot_row.getValue("date_code", ""));
                            parameters.put("quantity", quantity);
                            parameters.put("ut", _order_row.getValue("uom_code", ""));
                            parameters.put("pack_sn_no", sn_nos);
                            App.Current.Print("mm_prod_lot_label", "打印产品标签", parameters);
                        }

                    }
                }).create();
        dialog1.show();


    }

    private void init_so_issue_order_head_info() {
        String sql = "exec p_get_mm_so_issue_order_info ?";
        Parameters p = new Parameters().add(1, order_id);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {

                pn_so_issue_editor.this.ProgressDialog.dismiss();

                Result<DataRow> r = this.Value;
                if (r.HasError) {
                    App.Current.showError(pn_so_issue_editor.this.getContext(), r.Error);
                    return;
                }

                so_issue_row = r.Value;


            }
        });
    }

    public void print_Label() {


        final String[] items = {"霍尼韦尔", "芝柯"};

        AlertDialog dialog1 = new AlertDialog.Builder(App.Current.Workbench).setTitle("请选择打印机")
                .setItems(items, (dialogInterface, i) -> {
                            if (i == 1) {
                                String sql = "exec get_pint_date";
                                Result<DataRow> value = App.Current.DbPortal.ExecuteRecord("core_and", sql);

                                if (value.HasError) {
                                    App.Current.toastError(pn_so_issue_editor.this.getContext(), value.Error);
                                    return;
                                }
                                if (value.Value != null) {
                                    Intent intent = new Intent();
                                    intent.setClass(App.Current.Workbench, So_issue_Activity.class);
                                    String cur_date = value.Value.getValue("cur_date", "");
                                    intent.putExtra("cur_date", cur_date);
                                    intent.putExtra("receive_address", so_issue_row.getValue("attribute1", ""));
                                    intent.putExtra("receiver", so_issue_row.getValue("attribute10", ""));
                                    intent.putExtra("phone", so_issue_row.getValue("attribute9", ""));
                                    intent.putExtra("sum_qty", sum_qty.intValue());
                                    intent.putExtra("issued_qty", sum_issued_qty.intValue());

                                    intent.putExtra("item_name", _lot_row.getValue("item_name", ""));
                                    intent.putExtra("quantity", _lot_row.getValue("quantity", 0));
                                    intent.putExtra("ut", _lot_row.getValue("ut", 0));
                                    intent.putExtra("lot_number", _lot_row.getValue("lot_number", ""));
                                    intent.putExtra("date_code", _lot_row.getValue("date_code", ""));

                                    App.Current.Workbench.startActivity(intent);
                                }
                            }
                        }
                ).create();
        dialog1.show();
    }

    @Override
    public void commit() {
        if (_order_row == null) {
            App.Current.showError(this.getContext(), "没有出货通知数据，不能提交。");
            return;
        }

        if (_lot_row == null) {
            App.Current.showError(this.getContext(), "没有批号数据，不能提交。");
            return;
        }

        final String lot_number = this.txt_lot_number_cell.getContentText();
        if (lot_number == null || lot_number.length() == 0) {
            App.Current.showError(this.getContext(), "没有批号数据，不能提交。");
            return;
        }

        final String issue = this.txt_issue_quantity_cell.getContentText();
        if (issue == null || issue.length() == 0) {
            App.Current.showError(this.getContext(), "没有输入实发数量，不能提交。");
            return;
        }

        final BigDecimal issue_quantity = new BigDecimal(issue);
        if (issue_quantity.equals(BigDecimal.ZERO)) {
            App.Current.showError(this.getContext(), "实发数量为0，不能提交。");
            return;
        }

        BigDecimal onhand_quantity = _lot_row.getValue("quantity", BigDecimal.ZERO);
        if (issue_quantity.compareTo(onhand_quantity) > 0) {
            App.Current.showError(this.getContext(), "实发数量超过现有量，不能提交。");
            return;
        }

        final String sn_no = this.txt_sn_no_cell.getContentText();

        if (sn_no == null || sn_no.length() == 0) {
            App.Current.showError(this.getContext(), "没有产品序列数据，不能提交。");
            return;
        }
        if (!sn_no.equals("原材料无序号")) {
            BigDecimal sn_qty = new BigDecimal(sn_no.split(",").length);

            if (!sn_qty.equals(issue_quantity)) {
                App.Current.showError(this.getContext(), "实发数量不等于实际序列号数量，不能提交。");
                return;
            }
        }


        if (binding_carton) {
            final EditText txt_split_qty = new EditText(getContext());
            new AlertDialog.Builder(getContext())
                    .setTitle("请输入卡板号")
                    .setView(txt_split_qty)
                    //.setText()
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String carton_no = txt_carton_no_cell.getContentText();

                            Map<String, String> entry = new HashMap<String, String>();
                            entry.put("code", _order_row.getValue("code", ""));
                            entry.put("create_user", App.Current.UserID);
                            entry.put("order_id", String.valueOf(_order_row.getValue("head_id", 0L)));
                            entry.put("order_line_id", String.valueOf(_order_row.getValue("line_id", 0)));
                            entry.put("quantity", String.valueOf(issue_quantity));
                            entry.put("lot_number", lot_number);
                            entry.put("sn_no", sn_no);
                            entry.put("card_no", txt_split_qty.getText().toString());
                            entry.put("date_code", _lot_row.getValue("date_code", ""));
                            entry.put("carton_no", carton_no);
                            entry.put("vendor_id", String.valueOf(_lot_row.getValue("vendor_id", 0)));
                            entry.put("vendor_model", _lot_row.getValue("vendor_model", ""));
                            entry.put("vendor_lot", _lot_row.getValue("vendor_lot", ""));
                            entry.put("warehouse_id", String.valueOf(_order_row.getValue("warehouse_id", 0)));

                            ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
                            entries.add(entry);

                            //生成XML数据，并传给存储过程
                            String xml = XmlHelper.createXml("so_issues", null, null, "so_issue", entries);
                            String sql = "exec p_mm_so_issue_create ?,?";
                            Connection conn = App.Current.DbPortal.CreateConnection(pn_so_issue_editor.this.Connector);
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
                                        App.Current.showError(pn_so_issue_editor.this.getContext(), rs.Error);
                                        return;
                                    }

                                    //更改Oracle出货通知行状态
                                    sql = "update ";

                                    App.Current.toastInfo(pn_so_issue_editor.this.getContext(), "提交成功");
                                    _order_row = null;
                                    _lot_row = null;
                                    pn_so_issue_editor.this.clearAll();
                                    pn_so_issue_editor.this.loadTaskOrderItem(_rownum);
                                }
                            } catch (SQLException e) {
                                App.Current.showInfo(pn_so_issue_editor.this.getContext(), e.getMessage());
                                e.printStackTrace();

                                pn_so_issue_editor.this.clearAll();
                                pn_so_issue_editor.this.loadTaskOrderItem(_rownum);
                            }
                        }
                    }).setNegativeButton("取消", null).show();
        } else {
            String carton_no = this.txt_carton_no_cell.getContentText();

            Map<String, String> entry = new HashMap<String, String>();
            entry.put("code", _order_row.getValue("code", ""));
            entry.put("create_user", App.Current.UserID);
            entry.put("order_id", String.valueOf(_order_row.getValue("head_id", 0L)));
            entry.put("order_line_id", String.valueOf(_order_row.getValue("line_id", 0)));
            entry.put("quantity", String.valueOf(issue_quantity));
            entry.put("lot_number", lot_number);
            entry.put("sn_no", sn_no);
            entry.put("date_code", _lot_row.getValue("date_code", ""));
            entry.put("carton_no", carton_no);
            entry.put("vendor_id", String.valueOf(_lot_row.getValue("vendor_id", 0)));
            entry.put("vendor_model", _lot_row.getValue("vendor_model", ""));
            entry.put("vendor_lot", _lot_row.getValue("vendor_lot", ""));
            entry.put("warehouse_id", String.valueOf(_order_row.getValue("warehouse_id", 0)));

            ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
            entries.add(entry);

            //生成XML数据，并传给存储过程
            String xml = XmlHelper.createXml("so_issues", null, null, "so_issue", entries);
            String sql = "exec p_mm_so_issue_create ?,?";
            Connection conn = App.Current.DbPortal.CreateConnection(pn_so_issue_editor.this.Connector);
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
                        App.Current.showError(pn_so_issue_editor.this.getContext(), rs.Error);
                        return;
                    }

                    //更改Oracle出货通知行状态
                    sql = "update ";

                    App.Current.toastInfo(pn_so_issue_editor.this.getContext(), "提交成功");
                    _order_row = null;
                    _lot_row = null;
                    pn_so_issue_editor.this.clearAll();
                    pn_so_issue_editor.this.loadTaskOrderItem(_rownum);
                }
            } catch (SQLException e) {
                App.Current.showInfo(pn_so_issue_editor.this.getContext(), e.getMessage());
                e.printStackTrace();

                pn_so_issue_editor.this.clearAll();
                pn_so_issue_editor.this.loadTaskOrderItem(_rownum);
            }
        }


    }

    public void clearAll() {
        this.txt_issue_order_cell.setContentText("");
        this.txt_item_name_cell.setContentText("");
        this.txt_quantity_cell.setContentText("");
        this.txt_warehouse_cell.setContentText("");
        this.txt_customer_name_cell.setContentText("");
        this.txt_date_code_cell.setContentText("");
        this.txt_lot_number_cell.setContentText("");
        this.txt_location_cell.setContentText("");
        this.txt_onhand_cell.setContentText("");
        this.txt_surplus_cell.setContentText("");
        this.txt_issue_quantity_cell.setContentText("");
        this.txt_sn_no_cell.setContentText("");
        this.txt_carton_no_cell.setContentText("");
    }
}
