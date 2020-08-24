package dynsoft.xone.android.wms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.blueprint.Demo_ad_escActivity;
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

public class pn_skd_tr_issue_editor extends pn_editor {

    public pn_skd_tr_issue_editor(Context context) {
        super(context);
    }

    public TextCell txt_tr_order_cell;
    public ButtonTextCell txt_item_code_cell;
    public TextCell txt_item_name_cell;
    public TextCell txt_vendor_name_cell;
    public TextCell txt_date_code_cell;
    public TextCell txt_lot_number_cell;
    public TextCell txt_quantity_cell;
    public ButtonTextCell txt_issue_quantity_cell;
    public TextCell txt_location_cell;
    public TextCell txt_onhand_cell;
    public ButtonTextCell txt_surplus_cell;
    public ImageButton btn_prev;
    public ImageButton btn_next;
    public ImageButton btn_print;
    public ButtonTextCell txt_sn_no_cell;


    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_skd_tr_issue_editor, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {

        super.onPrepared();

        this.txt_tr_order_cell = (TextCell) this.findViewById(R.id.txt_tr_order_cell);
        this.txt_item_code_cell = (ButtonTextCell) this.findViewById(R.id.txt_item_code_cell);
        this.txt_item_name_cell = (TextCell) this.findViewById(R.id.txt_item_name_cell);
        this.txt_vendor_name_cell = (TextCell) this.findViewById(R.id.txt_vendor_name_cell);
//        this.txt_date_code_cell = (TextCell) this.findViewById(R.id.txt_date_code_cell);
        this.txt_lot_number_cell = (TextCell) this.findViewById(R.id.txt_lot_number_cell);
//        this.txt_quantity_cell = (TextCell) this.findViewById(R.id.txt_quantity_cell);
//        this.txt_issue_quantity_cell = (ButtonTextCell) this.findViewById(R.id.txt_issue_quantity_cell);
        this.txt_location_cell = (TextCell) this.findViewById(R.id.txt_location_cell);
//        this.txt_onhand_cell = (TextCell) this.findViewById(R.id.txt_onhand_cell);
//        this.txt_surplus_cell = (ButtonTextCell) this.findViewById(R.id.txt_surplus_cell);
        this.btn_prev = (ImageButton) this.findViewById(R.id.btn_prev);
        this.btn_next = (ImageButton) this.findViewById(R.id.btn_next);
        this.btn_print = (ImageButton) this.findViewById(R.id.btn_print);

        this.txt_sn_no_cell = (ButtonTextCell) this.findViewById(R.id.txt_sn_no_cell);

        if (this.txt_tr_order_cell != null) {
            this.txt_tr_order_cell.setLabelText("任务单号");
            this.txt_tr_order_cell.setReadOnly();
        }

        if (this.txt_item_code_cell != null) {
            this.txt_item_code_cell.setLabelText("物料编码");
            this.txt_item_code_cell.setReadOnly();
            this.txt_item_code_cell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_skd_tr_issue_editor.this.showStockQuantity();
                }
            });
        }

        if (this.txt_item_name_cell != null) {
            this.txt_item_name_cell.setLabelText("订单号");
            this.txt_item_name_cell.setReadOnly();
            this.txt_item_name_cell.TextBox.setSingleLine(true);
        }

        if (this.txt_vendor_name_cell != null) {
            this.txt_vendor_name_cell.setLabelText("箱号");
            this.txt_vendor_name_cell.setReadOnly();
            this.txt_vendor_name_cell.TextBox.setSingleLine(true);
        }

        if (this.txt_sn_no_cell != null) {
            this.txt_sn_no_cell.setLabelText("产品序列");
            this.txt_sn_no_cell.setReadOnly();
            this.txt_sn_no_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_right_light"));
            this.txt_sn_no_cell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String lot_number = pn_skd_tr_issue_editor.this.txt_lot_number_cell.getContentText();
                    String issue_qty = pn_skd_tr_issue_editor.this.txt_issue_quantity_cell.getContentText();
                    pn_skd_tr_issue_editor.this.loadsnno(lot_number, issue_qty);
                }
            });
        }

        if (this.txt_date_code_cell != null) {
            this.txt_date_code_cell.setLabelText("D/C");
            this.txt_date_code_cell.setReadOnly();
        }

        if (this.txt_lot_number_cell != null) {
            this.txt_lot_number_cell.setLabelText("批次");
            this.txt_lot_number_cell.setReadOnly();
        }

        if (this.txt_quantity_cell != null) {
            this.txt_quantity_cell.setLabelText("数量");
            this.txt_quantity_cell.setReadOnly();
        }

        if (this.txt_issue_quantity_cell != null) {
            this.txt_issue_quantity_cell.setLabelText("转移数量");
            this.txt_issue_quantity_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_label_light"));
            this.txt_issue_quantity_cell.TextBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    DataRow row = (DataRow) pn_skd_tr_issue_editor.this.txt_onhand_cell.getTag();
                    if (row != null) {
                        String str = s.toString();
                        if (str == null || str.length() == 0) {
                            str = "0";
                        }
                        BigDecimal onhand_quantity = row.getValue("quantity", BigDecimal.ZERO);
                        BigDecimal issue_quantity = new BigDecimal(str);
                        BigDecimal surplus = onhand_quantity.subtract(issue_quantity);
                        pn_skd_tr_issue_editor.this.txt_surplus_cell.setContentText(App.formatNumber(surplus, "0.##"));
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
                    String qty = pn_skd_tr_issue_editor.this.txt_issue_quantity_cell.getContentText();
                    pn_skd_tr_issue_editor.this.printLabel(qty);
                }
            });
        }

        if (this.txt_location_cell != null) {
            this.txt_location_cell.setLabelText("转入储位");
            this.txt_location_cell.setReadOnly();
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
                    String qty = pn_skd_tr_issue_editor.this.txt_surplus_cell.getContentText();
                    pn_skd_tr_issue_editor.this.printLabel(qty);
                }
            });
        }

        if (this.btn_prev != null) {
            this.btn_prev.setImageBitmap(App.Current.ResourceManager.getImage("@/core_prev_white"));
            this.btn_prev.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_skd_tr_issue_editor.this.prev();
                }
            });
        }

        if (this.btn_next != null) {
            this.btn_next.setImageBitmap(App.Current.ResourceManager.getImage("@/core_next_white"));
            this.btn_next.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_skd_tr_issue_editor.this.next();
                }
            });
        }
        if (this.btn_print != null) {
            this.btn_print.setImageBitmap(App.Current.ResourceManager.getImage("@/core_print_white"));
            this.btn_print.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_skd_tr_issue_editor.this.print_status();
                }
            });
        }
        _order_id = this.Parameters.get("order_id", 0L);
        _order_code = this.Parameters.get("order_code", "");
//        if (_order_code == null || _order_code.length() == 0) {
//            this.close();
//            return;
//        }

        this.txt_tr_order_cell.setContentText(_order_code);
        _line_id = this.Parameters.get("line_id", 0L);
        if (_line_id > 0) {
            String sql = "exec p_mm_skd_tr_issue_get_row_number ?,?,?";
            Parameters p = new Parameters().add(1, App.Current.UserID).add(2, _order_id).add(3, _line_id);
            Result<Long> r = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, Long.class);
            if (r.HasError) {
                App.Current.showError(pn_skd_tr_issue_editor.this.getContext(), "没有指定转移申请编号。");
                this.close();
            }

            if (r.Value != null && r.Value > 0L) {
                _rownum = r.Value;
                this.loadTaskOrderItem(_rownum);
            }
        } else {
            this.close();
            return;
        }
    }

    private Long _order_id;
    private Long _line_id;
    private String _order_code;
    private Long _rownum;
    private Integer _total;
    private String from_war_code;
    private String to_war_code;
    private String store_keeper;
    private String tr_date;
    private String comment;


    public void prev() {
        if (_rownum > 1) {
            this.loadTaskOrderItem(_rownum - 1);
        } else {
            App.Current.showError(pn_skd_tr_issue_editor.this.getContext(), "已经是第一条。");
        }
    }

    public void next() {
        if (_rownum < _total) {
            this.loadTaskOrderItem(_rownum + 1);
        } else {
            App.Current.showError(pn_skd_tr_issue_editor.this.getContext(), "已经是最后一条。");
        }
    }

    private String _scan_quantity;

    @Override
    public void onScan(final String barcode) {
        final String bar_code = barcode.trim();

        //扫描批次条码
        if (bar_code.startsWith("CRQ:")) {
            String str = bar_code.substring(4, bar_code.length());
            String[] arr = str.split("-");
            String lot_number = arr[0];
            _scan_quantity = arr[2];
            if (_scan_quantity != null || _scan_quantity.length() > 0) {
//                this.loadLotNumber(lot_number);
                this.txt_lot_number_cell.setContentText(lot_number);
            }
        } else if (bar_code.startsWith("C:")) {
            String lot = bar_code.substring(2, bar_code.length());
            this.loadLotNumber(lot);
        } else if (bar_code.startsWith("L:")) {
            String loc = bar_code.substring(2, bar_code.length());
            this.txt_location_cell.setContentText(loc);
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

    private DataRow _order_row;
    private DataRow _lot_row;

    public void loadsnno(String lot_number, String issue_qty) {
        this.ProgressDialog.show();

        if (_lot_row == null) {
            App.Current.showError(this.getContext(), "请扫描需要转库的批次。");
            return;
        }
        String sql = "exec p_mm_skd_tr_issue_get_prod_sn_no ?,?";
        Parameters p = new Parameters().add(1, lot_number).add(2, issue_qty);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {

                pn_skd_tr_issue_editor.this.ProgressDialog.dismiss();

                Result<DataRow> r = this.Value;
                if (r.HasError) {
                    App.Current.showError(pn_skd_tr_issue_editor.this.getContext(), r.Error);
                    return;
                }
                ;

                String result = r.Value.getValue("result", "");
                if (result.length() > 0) {
                    App.Current.showError(pn_skd_tr_issue_editor.this.getContext(), result);
                    return;
                }

                String pack_sn_no = r.Value.getValue("pack_snno", "");

                if (pack_sn_no != null && pack_sn_no.length() != 0) {
                    pn_skd_tr_issue_editor.this.txt_sn_no_cell.setContentText(pack_sn_no);
                }
            }
        });


    }


    public void loadTaskOrderItem(long index) {
        this.ProgressDialog.show();

//        String code = this.txt_tr_order_cell.getContentText();
//        if (code == null || code.length() == 0) {
//            App.Current.showError(pn_skd_tr_issue_editor.this.getContext(), "没有指定转出申请编号。");
//            return;
//        }

        String sql = "exec p_mm_skd_tr_issue_get_item ?,?,?";
        Parameters p = new Parameters().add(1, App.Current.UserID).add(2, _order_id).add(3, index);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_skd_tr_issue_editor.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_skd_tr_issue_editor.this.getContext(), result.Error);
                    return;
                }

                _order_row = result.Value;
                if (_order_row == null) {
                    App.Current.showError(pn_skd_tr_issue_editor.this.getContext(), "没有数据。");
                    pn_skd_tr_issue_editor.this.clearAll();
                    pn_skd_tr_issue_editor.this.Header.setTitleText("库存转移");
                    return;
                }

                _total = _order_row.getValue("total", Integer.class);
                _rownum = _order_row.getValue("rownum", Long.class);
                if (_total > 0) {
                    pn_skd_tr_issue_editor.this.Header.setTitleText("库存转移(共" + String.valueOf(_total) + "条)");
                } else {
                    pn_skd_tr_issue_editor.this.Header.setTitleText("库存转移");
                }

                String order_code = _order_row.getValue("shipment_num", "");
                String item_code = _order_row.getValue("item_code", "") + ", 从" + _order_row.getValue("warehouse_code", "") + "(" + _order_row.getValue("stock_quantity", "") + ") 到" + _order_row.getValue("to_warehouse_code", "");
                from_war_code = _order_row.getValue("warehouse_code", "");
                to_war_code = _order_row.getValue("to_warehouse_code", "");
                tr_date = "";
                comment = _order_row.getValue("comment", "");
                store_keeper = _order_row.getValue("store_keeper_name", "");
                pn_skd_tr_issue_editor.this.txt_tr_order_cell.setContentText(order_code);
//                pn_skd_tr_issue_editor.this.txt_item_code_cell.setContentText(item_code);
                pn_skd_tr_issue_editor.this.txt_item_name_cell.setContentText(_order_row.getValue("os_no", ""));
                pn_skd_tr_issue_editor.this.txt_vendor_name_cell.setContentText(_order_row.getValue("pack_number", ""));
//                pn_skd_tr_issue_editor.this.txt_date_code_cell.setContentText(_order_row.getValue("date_code", ""));

                BigDecimal planned_qty = _order_row.getValue("quantity", BigDecimal.ZERO);
                BigDecimal issued_qty = _order_row.getValue("issued_quantity", BigDecimal.ZERO);
                BigDecimal open_qty = planned_qty.subtract(issued_qty);

                String qty_str = "总数:" + App.formatNumber(planned_qty, "0.##");
                qty_str += "/已转:" + App.formatNumber(issued_qty, "0.##");
                qty_str += "/未转:" + App.formatNumber(open_qty, "0.##");

//                pn_skd_tr_issue_editor.this.txt_quantity_cell.setContentText(qty_str);
                pn_skd_tr_issue_editor.this.txt_location_cell.setContentText(_order_row.getValue("locations", ""));

                pn_skd_tr_issue_editor.this.txt_lot_number_cell.setTag(null);
                pn_skd_tr_issue_editor.this.txt_lot_number_cell.setContentText("");
//                pn_skd_tr_issue_editor.this.txt_issue_quantity_cell.setContentText("");
//                pn_skd_tr_issue_editor.this.txt_onhand_cell.setContentText("");
//                pn_skd_tr_issue_editor.this.txt_onhand_cell.setTag(null);
//                pn_skd_tr_issue_editor.this.txt_surplus_cell.setContentText("");
                pn_skd_tr_issue_editor.this.txt_location_cell.setContentText(_order_row.getValue("locations", ""));
            }
        });
    }

    public void showStockQuantity() {
        if (_order_row == null) {
            App.Current.showError(this.getContext(), "没有转移申请数据，不能提交。");
            return;
        }

        Integer item_id = _order_row.getValue("item_id", Integer.class);
        Integer warehouse_id = _order_row.getValue("warehouse_id", 0);

        String sql = "select (select top 1 locations from mm_stock_lot where item_id=? and warehouse_id=? order by date_code) locations,";
        sql += "(SELECT cast(cast(quantity as real) as varchar)+' '+uom_code FROM dbo.mm_stock_item WHERE item_id=? AND warehouse_id=?) quantity";
        Parameters p = new Parameters().add(1, item_id).add(2, warehouse_id).add(3, item_id).add(4, warehouse_id);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> r = this.Value;
                if (r.Value != null) {
                    DataRow row = r.Value;
                    String str = "库存：" + row.getValue("quantity", "") + "\n";
                    str += "储位：" + row.getValue("locations", "");
                    App.Current.showInfo(pn_skd_tr_issue_editor.this.getContext(), str);
                }
            }
        });
    }

    public void loadLotNumber(String lotNumber) {
        if (_order_row == null) {
            App.Current.toastError(pn_skd_tr_issue_editor.this.getContext(), "缺少转移申请数据");
            return;
        }

        final long order_id = _order_row.getValue("id", Long.class);
        final long line_id = _order_row.getValue("pack_id", Long.class);

        this.ProgressDialog.show();
        String sql = "exec p_mm_skd_tr_issue_get_lot_number ?,?,?";
        Parameters p = new Parameters().add(1, order_id).add(2, line_id).add(3, lotNumber);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {

                pn_skd_tr_issue_editor.this.ProgressDialog.dismiss();

                Result<DataRow> r = this.Value;
                if (r.HasError) {
                    App.Current.showError(pn_skd_tr_issue_editor.this.getContext(), r.Error);
                    return;
                }

                _lot_row = r.Value;
                String result = _lot_row.getValue("result", "");

                if (result.length() > 0) {
                    if (result.indexOf("温馨提示：") == -1) {
                        App.Current.showError(pn_skd_tr_issue_editor.this.getContext(), result);
                        return;
                    } else {
                        App.Current.question(pn_skd_tr_issue_editor.this.getContext(), result, new DialogInterface.OnClickListener() {
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
        });

    }


    public void loaditem() {
        BigDecimal issue_quantity = BigDecimal.ZERO;
        BigDecimal scan_quantity = new BigDecimal(_scan_quantity);
        BigDecimal open_quantity = _order_row.getValue("open_quantity", BigDecimal.ZERO);
        BigDecimal onhand_quantity = _lot_row.getValue("quantity", BigDecimal.ZERO);

        issue_quantity = scan_quantity;
        if (open_quantity.compareTo(scan_quantity) < 0) {
            issue_quantity = open_quantity;
        }

        if (onhand_quantity.compareTo(issue_quantity) < 0) {
            issue_quantity = onhand_quantity;
        }
        pn_skd_tr_issue_editor.this.txt_vendor_name_cell.setContentText(_lot_row.getValue("vendor_name", ""));
        pn_skd_tr_issue_editor.this.txt_vendor_name_cell.setTag(_lot_row.getValue("vendor_id"));
        pn_skd_tr_issue_editor.this.txt_lot_number_cell.setTag(_lot_row);
        pn_skd_tr_issue_editor.this.txt_lot_number_cell.setContentText(_lot_row.getValue("lot_number", ""));
        pn_skd_tr_issue_editor.this.txt_date_code_cell.setContentText(_lot_row.getValue("date_code", ""));
        pn_skd_tr_issue_editor.this.txt_location_cell.setContentText(_lot_row.getValue("locations", ""));
        pn_skd_tr_issue_editor.this.txt_onhand_cell.setTag(_lot_row);
        pn_skd_tr_issue_editor.this.txt_onhand_cell.setContentText(App.formatNumber(onhand_quantity, "0.##"));
        pn_skd_tr_issue_editor.this.txt_issue_quantity_cell.setContentText(App.formatNumber(issue_quantity, "0.##"));
    }

    public void print_status() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("code", this._order_code);
        parameters.put("wo_code", "");
        parameters.put("from_war_code", from_war_code);
        parameters.put("to_war_code", to_war_code);
        parameters.put("store_keeper", store_keeper);
        parameters.put("tr_date", tr_date);
        App.Current.Print("mm_win_allocation_states_brand", "打印调拨状态牌标签", parameters);
    }

    public void printLabel(final String quantity) {
        if (quantity == null || quantity.length() == 0) {
            App.Current.showError(this.getContext(), "没有指定标签数量。");
            return;
        }

        if (_order_row == null) {
            App.Current.showError(this.getContext(), "没有转移申请数据。");
            return;
        }

        final DataRow lot_row = (DataRow) this.txt_lot_number_cell.getTag();
        if (lot_row == null) {
            App.Current.showError(this.getContext(), "没有批号数据。");
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
                                        App.Current.toastError(pn_skd_tr_issue_editor.this.getContext(), value.Error);
                                        return;
                                    }
                                    if (value.Value != null) {
                                        Intent intent = new Intent();
                                        intent.setClass(App.Current.Workbench, Demo_ad_escActivity.class);
                                        String cur_date = value.Value.getValue("cur_date", "");
                                        intent.putExtra("cur_date", cur_date);
                                        intent.putExtra("org_code", lot_row.getValue("org_code", ""));
                                        intent.putExtra("item_code", lot_row.getValue("item_code", ""));
                                        intent.putExtra("vendor_model", lot_row.getValue("vendor_model", ""));
                                        intent.putExtra("lot_number", lot_row.getValue("lot_number", ""));
                                        intent.putExtra("vendor_lot", lot_row.getValue("vendor_lot", ""));
                                        intent.putExtra("date_code", lot_row.getValue("date_code", ""));
                                        intent.putExtra("quantity", quantity);
                                        intent.putExtra("ut", _order_row.getValue("uom_code", ""));
                                        App.Current.Workbench.startActivity(intent);
                                    }
                                }
                            });


                        } else {
                            Intent intent = new Intent();
                            intent.setClass(App.Current.Workbench, frm_item_lot_printer.class);
                            intent.putExtra("org_code", lot_row.getValue("org_code", ""));
                            intent.putExtra("item_code", lot_row.getValue("item_code", ""));
                            intent.putExtra("vendor_model", lot_row.getValue("vendor_model", ""));
                            intent.putExtra("lot_number", lot_row.getValue("lot_number", ""));
                            intent.putExtra("vendor_lot", lot_row.getValue("vendor_lot", ""));
                            intent.putExtra("date_code", lot_row.getValue("date_code", ""));
                            intent.putExtra("quantity", quantity);
                            intent.putExtra("ut", _order_row.getValue("uom_code", ""));
                            App.Current.Workbench.startActivity(intent);
                        }

                    }
                }).create();
        dialog1.show();


        //Intent intent = new Intent();
        //intent.setClass(this.getContext(), frm_item_lot_printer.class);
        //intent.putExtra("org_code", lot_row.getValue("org_code", ""));
        //intent.putExtra("item_code", lot_row.getValue("item_code", ""));
        //intent.putExtra("vendor_model", lot_row.getValue("vendor_model", ""));
        //intent.putExtra("lot_number", lot_row.getValue("lot_number", ""));
        //intent.putExtra("vendor_lot", lot_row.getValue("vendor_lot", ""));
        //intent.putExtra("date_code", lot_row.getValue("date_code", ""));
        //intent.putExtra("quantity", quantity);
        //intent.putExtra("ut", _order_row.getValue("uom_code", ""));

        //this.getContext().startActivity(intent);
    }

    @Override
    public void commit() {
        if (_order_row == null) {
            App.Current.showError(this.getContext(), "没有转移申请数据，不能提交。");
            return;
        }


        final String pack_number = this.txt_vendor_name_cell.getContentText();
        final String lot_number = this.txt_lot_number_cell.getContentText();
        if (lot_number == null || lot_number.length() == 0) {
            App.Current.showError(this.getContext(), "没有批号数据，不能提交。");
            return;
        }
        if (!pack_number.equals(lot_number)) {
            App.Current.showError(this.getContext(), "不是此批号，不能提交。");
            return;
        }

        final Long order_id = _order_row.getValue("id", Long.class);
        final Long pack_id = _order_row.getValue("pack_id", Long.class);
        final String order_code = _order_row.getValue("code", "");
        final int order_line_id = _order_row.getValue("line_id", 0);
        final int organization_id = _order_row.getValue("org_id", 0);
        final String locations = pn_skd_tr_issue_editor.this.txt_location_cell.getContentText();


        App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();


                String sql = "declare @rslt nvarchar(max) exec p_mm_skd_tr_issue_get_lot_number ?,?,?,@rslt output; select @rslt rtnstr";
                Parameters p = new Parameters().add(1, order_id).add(2, pack_id).add(3, lot_number);
                String value_r = App.Current.DbPortal.ExecuteScalar("core_and", sql, p).Value.toString();
                if (!value_r.equals("")) {
                    Result<String> rs = XmlHelper.parseResult(value_r);

                    if (rs.HasError) {
                        App.Current.showError(pn_skd_tr_issue_editor.this.getContext(), rs.Error);
                        return;
                    }

                    _order_row = null;
                    App.Current.toastInfo(pn_skd_tr_issue_editor.this.getContext(), "提交成功");
//                    pn_skd_tr_issue_editor.this.clear();

                    close();
//                    pn_skd_tr_issue_editor.this.loadTaskOrderItem(_rownum);
                } else {
                    _order_row = null;
                }
            }
        });
    }

    public void clear() {
        this.txt_vendor_name_cell.setContentText("");
//        this.txt_date_code_cell.setContentText("");
        this.txt_lot_number_cell.setContentText("");
        this.txt_location_cell.setContentText("");
//        this.txt_sn_no_cell.setContentText("");
//        this.txt_issue_quantity_cell.setContentText("0");
    }

    public void clearAll() {
        this.txt_tr_order_cell.setContentText("");
//        this.txt_item_code_cell.setContentText("");
        this.txt_item_name_cell.setContentText("");
//        this.txt_quantity_cell.setContentText("");
        this.txt_vendor_name_cell.setContentText("");
//        this.txt_date_code_cell.setContentText("");
        this.txt_lot_number_cell.setContentText("");
        this.txt_location_cell.setContentText("");
//        this.txt_issue_quantity_cell.setContentText("0");
//        this.txt_surplus_cell.setContentText("");
//        this.txt_onhand_cell.setContentText("");
    }
}
