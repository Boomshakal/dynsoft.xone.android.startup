package dynsoft.xone.android.wms;

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
import dynsoft.xone.android.control.DecimalCell;
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class pn_so_return_editor extends pn_editor {

    public pn_so_return_editor(Context context) {
        super(context);
    }

    public TextCell txt_return_order_cell;
    public ButtonTextCell txt_item_code_cell;
    public TextCell txt_item_name_cell;
    public TextCell txt_customer_name_cell;
    public TextCell txt_date_code_cell;
    public TextCell txt_lot_number_cell;
    public TextCell txt_order_quantity_cell;
    public DecimalCell txt_return_quantity_cell;
    public TextCell txt_location_cell;
    public ImageButton btn_prev;
    public ImageButton btn_next;

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_so_return_editor, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {

        super.onPrepared();

        this.txt_return_order_cell = (TextCell) this.findViewById(R.id.txt_return_order_cell);
        this.txt_item_code_cell = (ButtonTextCell) this.findViewById(R.id.txt_item_code_cell);
        this.txt_item_name_cell = (TextCell) this.findViewById(R.id.txt_item_name_cell);
        this.txt_customer_name_cell = (TextCell) this.findViewById(R.id.txt_customer_name_cell);
        this.txt_date_code_cell = (TextCell) this.findViewById(R.id.txt_date_code_cell);
        this.txt_lot_number_cell = (TextCell) this.findViewById(R.id.txt_lot_number_cell);
        this.txt_order_quantity_cell = (TextCell) this.findViewById(R.id.txt_order_quantity_cell);
        this.txt_return_quantity_cell = (DecimalCell) this.findViewById(R.id.txt_return_quantity_cell);
        this.txt_location_cell = (TextCell) this.findViewById(R.id.txt_location_cell);
        this.btn_prev = (ImageButton) this.findViewById(R.id.btn_prev);
        this.btn_next = (ImageButton) this.findViewById(R.id.btn_next);

        if (this.txt_return_order_cell != null) {
            this.txt_return_order_cell.setLabelText("销退通知");
            this.txt_return_order_cell.setReadOnly();
        }

        if (this.txt_customer_name_cell != null) {
            this.txt_customer_name_cell.setLabelText("客户");
            this.txt_customer_name_cell.setReadOnly();
            this.txt_customer_name_cell.TextBox.setSingleLine(true);
        }

        if (this.txt_item_code_cell != null) {
            this.txt_item_code_cell.setLabelText("产品编号");
            this.txt_item_code_cell.setReadOnly();
            this.txt_item_code_cell.setButtonImage(App.Current.ResourceManager.getImage("core_right_light"));
            this.txt_item_code_cell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    openInitLotNumber();
                }
            });
        }

        if (this.txt_item_name_cell != null) {
            this.txt_item_name_cell.setLabelText("产品描述");
            this.txt_item_name_cell.setReadOnly();
        }

        if (this.txt_order_quantity_cell != null) {
            this.txt_order_quantity_cell.setLabelText("退货数量");
            this.txt_order_quantity_cell.setReadOnly();
            this.txt_order_quantity_cell.TextBox.setSingleLine(true);
        }

        if (this.txt_lot_number_cell != null) {
            this.txt_lot_number_cell.setLabelText("批次");
            this.txt_lot_number_cell.setReadOnly();
        }

        if (this.txt_date_code_cell != null) {
            this.txt_date_code_cell.setLabelText("D/C");
        }

        if (this.txt_location_cell != null) {
            this.txt_location_cell.setLabelText("储位");
            this.txt_location_cell.setReadOnly();
        }

        if (this.txt_return_quantity_cell != null) {
            this.txt_return_quantity_cell.setLabelText("数量");
        }

        if (this.btn_prev != null) {
            this.btn_prev.setImageBitmap(App.Current.ResourceManager.getImage("@/core_prev_white"));
            this.btn_prev.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_so_return_editor.this.prev();
                }
            });
        }

        if (this.btn_next != null) {
            this.btn_next.setImageBitmap(App.Current.ResourceManager.getImage("@/core_next_white"));
            this.btn_next.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_so_return_editor.this.next();
                }
            });
        }

        _order_id = this.Parameters.get("order_id", 0L);
        _order_code = this.Parameters.get("order_code", "");
        this.txt_return_order_cell.setContentText(_order_code);

        this.loadTaskOrderItem(1L);
    }

    private Long _order_id;
    private String _order_code;
    private Long _rownum;
    private Integer _total = 0;
    private DataRow _order_row;
    private DataRow _lot_row;
    private String _scan_quantity;

    public void prev() {
        if (_rownum > 1) {
            this.loadTaskOrderItem(_rownum - 1);
        } else {
            App.Current.showInfo(pn_so_return_editor.this.getContext(), "已经是第一条。");
        }
    }

    public void next() {
        if (_rownum < _total) {
            this.loadTaskOrderItem(_rownum + 1);
        } else {
            App.Current.showInfo(pn_so_return_editor.this.getContext(), "已经是最后一条。");
        }
    }

    public void openInitLotNumber() {
        if (_order_row != null) {
            String item_code = _order_row.getValue("item_code", "");
            Link link = new Link("pane://x:code=mm_begin_stock_editor");
            link.Parameters.add("item_code", item_code);
            link.Open(this, this.getContext(), null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        this.txt_return_quantity_cell.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onScan(final String barcode) {
        final String bar_code = barcode.trim();

        //扫描批次条码
        if (bar_code.startsWith("CRQ:")) {
            String lot = bar_code.substring(4);
            String[] arr = lot.split("-");
            if (arr != null && arr.length > 2) {
                _scan_quantity = arr[2];
                this.txt_return_quantity_cell.setContentText(_scan_quantity);
                this.txt_lot_number_cell.setContentText(arr[0]);
                this.txt_date_code_cell.setContentText(arr[3]);
                //this.loadLotNumber(arr[0]);
            }
        } else if (bar_code.startsWith("C:")) {
            String lot = bar_code.substring(2, bar_code.length());
            this.txt_lot_number_cell.setContentText(lot);
            //this.loadLotNumber(lot);
        } else if (bar_code.startsWith("L:")) {
            String loc = bar_code.substring(2, bar_code.length());
            String locs = this.txt_location_cell.getContentText().trim();
            if (locs.contains(loc)) {
                return;
            }

            if (locs.length() > 0) {
                locs += ", ";
            }

            this.txt_location_cell.setContentText(locs + loc);
        } else {
            this.txt_lot_number_cell.setContentText(bar_code);
        }

    }

    public void loadTaskOrderItem(long index) {
        this.ProgressDialog.show();

        String sql = "exec p_mm_so_return_get_item ?,?,?";
        Parameters p = new Parameters().add(1, App.Current.UserID).add(2, _order_id).add(3, index);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_return_editor.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_return_editor.this.getContext(), result.Error);
                    return;
                }

                _order_row = result.Value;
                if (_order_row == null) {
                    App.Current.showError(pn_so_return_editor.this.getContext(), "没有数据。");
                    pn_so_return_editor.this.clearAll();
                    pn_so_return_editor.this.Header.setTitleText("销退通知");
                    return;
                }

                _total = _order_row.getValue("total", Integer.class);
                _rownum = _order_row.getValue("rownum", Long.class);

                if (_total > 0) {
                    pn_so_return_editor.this.Header.setTitleText("销退通知(共" + String.valueOf(_total) + "条)");
                } else {
                    pn_so_return_editor.this.Header.setTitleText("销退通知");
                }

                String order_code = _order_row.getValue("code", "") + ", 第" + String.valueOf(_rownum) + "条";
                String item_code = _order_row.getValue("warehouse_code", "") + ", " + _order_row.getValue("item_code", "");
                pn_so_return_editor.this.txt_return_order_cell.setContentText(order_code);
                pn_so_return_editor.this.txt_item_code_cell.setContentText(item_code);
                pn_so_return_editor.this.txt_item_name_cell.setContentText(_order_row.getValue("item_name", ""));
                pn_so_return_editor.this.txt_customer_name_cell.setContentText(_order_row.getValue("customer_name", ""));

                BigDecimal planned_qty = _order_row.getValue("quantity", BigDecimal.ZERO);
                BigDecimal returned_qty = _order_row.getValue("closed_quantity", BigDecimal.ZERO);
                BigDecimal open_qty = planned_qty.subtract(returned_qty);

                String qty_str = "总数:" + App.formatNumber(planned_qty, "0.##");
                qty_str += "/已退:" + App.formatNumber(returned_qty, "0.##");
                qty_str += "/未退:" + App.formatNumber(open_qty, "0.##");
                pn_so_return_editor.this.txt_order_quantity_cell.setContentText(qty_str);

                pn_so_return_editor.this.txt_lot_number_cell.setContentText("");
                pn_so_return_editor.this.txt_date_code_cell.setContentText("");
                pn_so_return_editor.this.txt_location_cell.setContentText("");
                pn_so_return_editor.this.txt_return_quantity_cell.setContentText("");
            }
        });
    }

    public void loadLotNumber(String lotNumber) {
        this.ProgressDialog.show();

        if (_order_row == null) {
            App.Current.showError(this.getContext(), "没有指定退货通知，无法扫描条码。");
            return;
        }

        final long order_id = _order_row.getValue("head_id", Long.class);
        final int line_id = _order_row.getValue("line_id", Integer.class);

        String sql = "exec p_mm_so_return_get_lot_number ?,?,?";
        Parameters p = new Parameters().add(1, order_id).add(2, line_id).add(3, lotNumber);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {

                pn_so_return_editor.this.ProgressDialog.dismiss();

                Result<DataRow> r = this.Value;
                if (r.HasError) {
                    App.Current.showError(pn_so_return_editor.this.getContext(), r.Error);
                    return;
                }

                _lot_row = r.Value;

                String result = _lot_row.getValue("result", "");
                if (result.length() > 0) {
                    App.Current.showError(pn_so_return_editor.this.getContext(), result);
                    return;
                }

                pn_so_return_editor.this.txt_lot_number_cell.setContentText(_lot_row.getValue("lot_number", ""));
                pn_so_return_editor.this.txt_date_code_cell.setContentText(_lot_row.getValue("date_code", ""));
                pn_so_return_editor.this.txt_return_quantity_cell.setContentText(_scan_quantity);
            }
        });

    }

    @Override
    public void commit() {
        if (_order_row == null) {
            App.Current.showError(this.getContext(), "没有退货通知数据，不能提交。");
            return;
        }
        String dc = this.txt_date_code_cell.getContentText().trim();
        if (_lot_row == null) {
            //App.Current.showError(this.getContext(), "没有批号数据，不能提交。");

            //return;
            if (dc == null || dc.length() == 0) {
                App.Current.showError(this.getContext(), "请输入DC后提交！");
                return;
            }

        } else {
            dc = _lot_row.getValue("date_code", "");
        }

        String lot_number = this.txt_lot_number_cell.getContentText();

        if (lot_number == null || lot_number.length() == 0) {
            //App.Current.showError(this.getContext(), "没有批号数据，不能提交。");
            //return;
            lot_number = "NULL";
        }

        final String issue = this.txt_return_quantity_cell.getContentText();
        if (issue == null || issue.length() == 0) {
            App.Current.showError(this.getContext(), "没有输入数量，不能提交。");
            return;
        }

        final BigDecimal issue_quantity = new BigDecimal(issue);
        if (issue_quantity.equals(BigDecimal.ZERO)) {
            App.Current.showError(this.getContext(), "数量为0，不能提交。");
            return;
        }
        final String txt_location = this.txt_location_cell.getContentText().trim();

        if (txt_location == null || txt_location.length() == 0) {
            App.Current.showError(this.getContext(), "没有输入储位不允许提交。");
            return;
        }

        Map<String, String> entry = new HashMap<String, String>();
        entry.put("order_id", String.valueOf(_order_row.getValue("head_id", 0L)));
        entry.put("line_id", String.valueOf(_order_row.getValue("line_id", 0)));
        entry.put("quantity", String.valueOf(issue_quantity));
        entry.put("lot_number", lot_number);
        entry.put("date_code", dc);
        entry.put("vendor_id", "6048");
        entry.put("vendor_model", _order_row.getValue("item_name", ""));
        entry.put("vendor_lot", dc);
        entry.put("locations", txt_location);
        entry.put("user_id", String.valueOf(App.Current.UserID));

        ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
        entries.add(entry);
        //生成XML数据，并传给存储过程
        String xml = XmlHelper.createXml("so_returns", null, null, "so_return", entries);
        String sql = "exec p_mm_so_return_create_v1 ?";
        Parameters p = new Parameters().add(1, xml);
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_return_editor.this.ProgressDialog.dismiss();

                Result<DataTable> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_return_editor.this.getContext(), result.Error);
                    return;
                }
                DataTable dt = result.Value;
                App.Current.toastInfo(pn_so_return_editor.this.getContext(), "提交成功");
                String str = pn_so_return_editor.this.txt_lot_number_cell.getContentText().trim();
                if (str == null || str.length() == 0) {
                    if (dt.Rows.size() > 0) {
                        printLabel(dt.Rows.get(0));

                    }
                }
                pn_so_return_editor.this.clearAll();
            }
        });
    }

    public void printLabel(final DataRow row) {
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
                                        App.Current.toastError(pn_so_return_editor.this.getContext(), value.Error);
                                        return;
                                    }
                                    if (value.Value != null) {
                                        Intent intent = new Intent();
                                        intent.setClass(App.Current.Workbench, Demo_ad_escActivity.class);
                                        String cur_date = value.Value.getValue("cur_date", "");
                                        intent.putExtra("cur_date", cur_date);
                                        intent.putExtra("org_code", row.getValue("org_code", ""));
                                        intent.putExtra("item_code", row.getValue("item_code", ""));
                                        intent.putExtra("vendor_model", row.getValue("vendor_model", ""));
                                        intent.putExtra("lot_number", row.getValue("lot_number", ""));
                                        intent.putExtra("vendor_lot", row.getValue("vendor_lot", ""));
                                        intent.putExtra("date_code", row.getValue("date_code", ""));
                                        intent.putExtra("quantity", App.formatNumber(
                                                row.getValue("quantity", BigDecimal.ZERO), "0.####"));
                                        intent.putExtra("ut", row.getValue("ut", ""));
                                        intent.putExtra("msl_grade", row.getValue("msl_grade", -1));
                                        App.Current.Workbench.startActivity(intent);
                                    }
                                }
                            });
                        } else {
                            Map<String, String> parameters = new HashMap<String, String>();
                            parameters.put("org_code", row.getValue("org_code", ""));
                            parameters.put("item_code", row.getValue("item_code", ""));
                            parameters.put("vendor_model", row.getValue("vendor_model", ""));
                            parameters.put("lot_number", row.getValue("lot_number", ""));
                            parameters.put("vendor_lot", row.getValue("vendor_lot", ""));
                            parameters.put("date_code", row.getValue("date_code", ""));
                            parameters.put("quantity", App.formatNumber(
                                    row.getValue("quantity", BigDecimal.ZERO), "0.####"));
                            parameters.put("ut", row.getValue("ut", ""));
                            parameters.put("pack_sn_no", row.getValue("pack_sn_no", ""));
                            App.Current.Print("mm_item_lot_label", "打印销售退货标签", parameters);
                        }
                    }
                }).create();
        dialog1.show();


//        String sql = "exec get_pint_date";
//        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, new ResultHandler<DataRow>() {
//            @Override
//            public void handleMessage(Message msg) {
//                Result<DataRow> value = Value;
//                if (value.HasError) {
//                    App.Current.toastError(pn_so_return_editor.this.getContext(), value.Error);
//                    return;
//                }
//                if (value.Value != null) {
//                    Intent intent = new Intent();
//                    intent.setClass(App.Current.Workbench, Demo_ad_escActivity.class);
//                    String cur_date = value.Value.getValue("cur_date", "");
//                    intent.putExtra("cur_date", cur_date);
//                    intent.putExtra("org_code", row.getValue("org_code", ""));
//                    intent.putExtra("item_code", row.getValue("item_code", ""));
//                    intent.putExtra("vendor_model", row.getValue("vendor_model", ""));
//                    intent.putExtra("lot_number", row.getValue("lot_number", ""));
//                    intent.putExtra("vendor_lot", row.getValue("vendor_lot", ""));
//                    intent.putExtra("date_code", row.getValue("date_code", ""));
//                    intent.putExtra("quantity", App.formatNumber(
//                            row.getValue("quantity", BigDecimal.ZERO), "0.####"));
//                    intent.putExtra("ut", row.getValue("ut", ""));
//                    intent.putExtra("msl_grade", row.getValue("msl_grade", -1));
//                    App.Current.Workbench.startActivity(intent);
//
//                }
//            }
//        });
//		Map<String, String> parameters = new HashMap<String, String>();
//		parameters.put("org_code", row.getValue("org_code", ""));
//		parameters.put("item_code", row.getValue("item_code", ""));
//		parameters.put("vendor_model", row.getValue("vendor_model", ""));
//		parameters.put("lot_number", row.getValue("lot_number", ""));
//		parameters.put("vendor_lot", row.getValue("vendor_lot", ""));
//		parameters.put("date_code", row.getValue("date_code", ""));
//		parameters.put("quantity", App.formatNumber(
//				row.getValue("quantity", BigDecimal.ZERO), "0.####"));
//		parameters.put("ut", row.getValue("ut", ""));
//		parameters.put("pack_sn_no", row.getValue("pack_sn_no", ""));
//		App.Current.Print("mm_item_lot_label", "打印销售退货标签", parameters);
    }


//	public void printLabel(DataRow row) {
//		Map<String, String> parameters = new HashMap<String, String>();
//		parameters.put("org_code", row.getValue("org_code", ""));
//		parameters.put("item_code", row.getValue("item_code", ""));
//		parameters.put("vendor_model", row.getValue("vendor_model", ""));
//		parameters.put("lot_number", row.getValue("lot_number", ""));
//		parameters.put("vendor_lot", row.getValue("vendor_lot", ""));
//		parameters.put("date_code", row.getValue("date_code", ""));
//		parameters.put("quantity", App.formatNumber(
//				row.getValue("quantity", BigDecimal.ZERO), "0.####"));
//		parameters.put("ut", row.getValue("ut", ""));
//		parameters.put("pack_sn_no", row.getValue("pack_sn_no", ""));
//		App.Current.Print("mm_item_lot_label", "打印销售退货标签", parameters);
//	}

    public void clearAll() {
        //this.txt_return_order_cell.setContentText("");
        //this.txt_item_code_cell.setContentText("");
        //this.txt_item_name_cell.setContentText("");
        ///this.txt_order_quantity_cell.setContentText("");
        //this.txt_customer_name_cell.setContentText("");
        this.txt_date_code_cell.setContentText("");
        this.txt_lot_number_cell.setContentText("");
        //this.txt_location_cell.setContentText("");
        this.txt_return_quantity_cell.setContentText("");
    }
}
