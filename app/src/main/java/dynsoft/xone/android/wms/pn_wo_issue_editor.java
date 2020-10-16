package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.blueprint.Demo_ad_escActivity;
import dynsoft.xone.android.blueprint.Demo_ad_escActivity_for_wo_issue;
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class pn_wo_issue_editor extends pn_editor {

    public pn_wo_issue_editor(Context context) {
        super(context);
    }

    public ButtonTextCell txt_wo_issue_order_cell;
    public ButtonTextCell txt_item_code_cell;
    public TextCell txt_item_name_cell;
    public TextCell txt_warehouse_cell;
    public TextCell txt_vendor_name_cell;
    public TextCell txt_date_code_cell;
    public ButtonTextCell txt_lot_number_cell;
    public TextCell txt_planned_quantity_cell;
    public ButtonTextCell txt_issue_quantity_cell;
    public TextCell txt_onhand_cell;
    public TextCell txt_location_cell;
    public ButtonTextCell txt_surplus_cell;
    public ImageButton btn_prev;
    public ImageButton btn_next;

    @Override
    public void setContentView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_wo_issue_editor, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {

        super.onPrepared();

        this.txt_wo_issue_order_cell = (ButtonTextCell) this.findViewById(R.id.txt_wo_issue_order_cell);
        this.txt_item_code_cell = (ButtonTextCell) this.findViewById(R.id.txt_item_code_cell);
        this.txt_item_name_cell = (TextCell) this.findViewById(R.id.txt_item_name_cell);
        this.txt_warehouse_cell = (TextCell) this.findViewById(R.id.txt_warehouse_cell);
        this.txt_vendor_name_cell = (TextCell) this.findViewById(R.id.txt_vendor_name_cell);
        this.txt_date_code_cell = (TextCell) this.findViewById(R.id.txt_date_code_cell);
        this.txt_lot_number_cell = (ButtonTextCell) this.findViewById(R.id.txt_lot_number_cell);
        this.txt_planned_quantity_cell = (TextCell) this.findViewById(R.id.txt_planned_quantity_cell);
        this.txt_issue_quantity_cell = (ButtonTextCell) this.findViewById(R.id.txt_issue_quantity_cell);
        this.txt_location_cell = (TextCell) this.findViewById(R.id.txt_location_cell);
        this.txt_onhand_cell = (TextCell) this.findViewById(R.id.txt_onhand_cell);
        this.txt_surplus_cell = (ButtonTextCell) this.findViewById(R.id.txt_surplus_cell);
        this.btn_prev = (ImageButton) this.findViewById(R.id.btn_prev);
        this.btn_next = (ImageButton) this.findViewById(R.id.btn_next);

        if (this.txt_wo_issue_order_cell != null) {
            this.txt_wo_issue_order_cell.setLabelText("发料申请");
            this.txt_wo_issue_order_cell.setReadOnly();
            this.txt_wo_issue_order_cell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_wo_issue_editor.this.showList(false);
                }
            });
        }

        if (this.txt_item_code_cell != null) {
            this.txt_item_code_cell.setLabelText("物料编码");
            this.txt_item_code_cell.setReadOnly();
            this.txt_item_code_cell.setLabelText("发料申请");
            this.txt_item_code_cell.setReadOnly();
            this.txt_item_code_cell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_wo_issue_editor.this.showList(true);
                }
            });
        }

        if (this.txt_item_name_cell != null) {
            this.txt_item_name_cell.setLabelText("物料名称");
            this.txt_item_name_cell.setReadOnly();
            this.txt_item_name_cell.TextBox.setSingleLine(true);
        }

        if (this.txt_warehouse_cell != null) {
            this.txt_warehouse_cell.setLabelText("发出库位");
            this.txt_warehouse_cell.setReadOnly();
            this.txt_warehouse_cell.TextBox.setSingleLine(true);
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
            this.txt_lot_number_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_right_light"));
            this.txt_lot_number_cell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String lot_number = pn_wo_issue_editor.this.txt_lot_number_cell.getContentText().trim();
                    if (lot_number == null || lot_number.length() == 0) {
                        App.Current.showError(pn_wo_issue_editor.this.getContext(), "没有指定批次。");
                    } else {
                        Link link = new Link("pane://x:code=mm_and_tr_move_editor");
                        link.Parameters.add("lot_number", lot_number);
                        link.Open(pn_wo_issue_editor.this, pn_wo_issue_editor.this.getContext(), null);
                    }

                }
            });
        }

        if (this.txt_planned_quantity_cell != null) {
            this.txt_planned_quantity_cell.setLabelText("数量");
            this.txt_planned_quantity_cell.setReadOnly();
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
                        pn_wo_issue_editor.this.txt_surplus_cell.setContentText(App.formatNumber(surplus, "0.##"));
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
                    String qty = pn_wo_issue_editor.this.txt_issue_quantity_cell.getContentText();
                    pn_wo_issue_editor.this.printLabel(qty);
                }
            });
        }

        if (this.txt_location_cell != null) {
            this.txt_location_cell.setLabelText("储位");
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
                    String qty = pn_wo_issue_editor.this.txt_surplus_cell.getContentText();
                    pn_wo_issue_editor.this.printLabel(qty);
                }
            });
        }

        if (this.btn_prev != null) {
            this.btn_prev.setImageBitmap(App.Current.ResourceManager.getImage("@/core_prev_white"));
            this.btn_prev.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_wo_issue_editor.this.prev();
                }
            });
        }

        if (this.btn_next != null) {
            this.btn_next.setImageBitmap(App.Current.ResourceManager.getImage("@/core_next_white"));
            this.btn_next.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_wo_issue_editor.this.next();
                }
            });
        }

        _order_id = this.Parameters.get("order_id", 0L);
        _order_code = this.Parameters.get("order_code", "");
        _rownum = 1L;

        this.txt_wo_issue_order_cell.setContentText(_order_code);
        this.loadTaskOrderItem(_rownum);
    }

    private Long _rownum;
    private Long _order_id;
    private String _order_code;
    private Integer _total = 0;
    private String _scan_quantity;
    private DataRow _order_row;
    private DataRow _lot_row;

    public void prev() {
        if (_rownum > 1) {
            this.loadTaskOrderItem(_rownum - 1);
        } else {
            App.Current.showError(pn_wo_issue_editor.this.getContext(), "已经是第一条。");
        }
    }

    public void next() {
        if (_rownum < _total) {
            this.loadTaskOrderItem(_rownum + 1);
        } else {
            App.Current.showError(pn_wo_issue_editor.this.getContext(), "已经是最后一条。");
        }
    }

    @Override
    public void onScan(final String barcode) {
        final String bar_code = barcode.trim();

        //扫描批次条码
        if (bar_code.startsWith("CRQ:")) {
            _loc_lot_tb = null;
            String str = bar_code.substring(4, bar_code.length());
            String[] arr = str.split("-");
            if (arr.length > 2) {
                String lot_number = arr[0].trim();

                String pack = "";
                if (arr.length > 3) {
                    pack = arr[3];
                }

                if ("P".equals(pack) == false) {
                    _scan_quantity = arr[2].trim();
                }

                this.txt_lot_number_cell.setContentText(lot_number);
                this.txt_issue_quantity_cell.setContentText(_scan_quantity);
                this.loadLotNumber(lot_number);
            }
        }

        if (bar_code.startsWith("C:")) {
            _loc_lot_tb = null;
            String lot = bar_code.substring(2, bar_code.length());
            this.loadLotNumber(lot);
        }

        if (bar_code.startsWith("TL:")) {
            _loc_lot_tb = null;
            String tl = bar_code.substring(3);
            if (tl.length() > 0) {
                this.loadTransferLocation(tl);
            }
        }

        if (bar_code.startsWith("L:")) {
            _loc_lot_tb = null;
            String tl = bar_code.substring(2);
            if (tl.length() > 0) {
                this.loadTLocation(tl);
            }
        }
    }

    public void loadTaskOrderItem(long index) {
        this.ProgressDialog.show();

        String sql = "exec p_mm_wo_issue_get_order_item ?,?,?";
        Parameters p = new Parameters().add(1, App.Current.UserID).add(2, _order_id).add(3, index);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_wo_issue_editor.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_wo_issue_editor.this.getContext(), result.Error);
                    return;
                }

                DataRow row = result.Value;
                if (row == null) {
                    App.Current.showError(pn_wo_issue_editor.this.getContext(), "找不到发料任务。");
                    pn_wo_issue_editor.this.clearAll();
                    pn_wo_issue_editor.this.Header.setTitleText("工单发料(无发料任务)");
                    return;
                }

                _order_row = row;
                _total = row.getValue("total", Integer.class);
                _rownum = row.getValue("rownum", Long.class);
                String preprocess = row.getValue("preprocess", "");
                if (_total > 0) {
                    pn_wo_issue_editor.this.Header.setTitleText("工单发料(" + String.valueOf(_total) + "条发料任务)");
                    if (preprocess.equals("是")) {
                        pn_wo_issue_editor.this.Header.setBackgroundColor(Color.parseColor("#FFFF00"));
                    }
                } else {
                    pn_wo_issue_editor.this.Header.setTitleText("工单发料(无发料任务)");
                }

                pn_wo_issue_editor.this.txt_wo_issue_order_cell.setTag(row);

                String item_code = row.getValue("item_code", "") + ", " + App.formatNumber(row.getValue("stock_quantity", ""), "0.##") + ", 第" + String.valueOf(_rownum) + "条";
                pn_wo_issue_editor.this.txt_wo_issue_order_cell.setContentText(row.getValue("code", ""));
                pn_wo_issue_editor.this.txt_item_code_cell.setContentText(item_code);
                pn_wo_issue_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
                pn_wo_issue_editor.this.txt_warehouse_cell.setContentText(row.getValue("warehouse_code", "") + ", " + row.getValue("locations", ""));
                pn_wo_issue_editor.this.txt_vendor_name_cell.setContentText(row.getValue("vendor_name", ""));
                pn_wo_issue_editor.this.txt_date_code_cell.setContentText(row.getValue("date_code", ""));

                BigDecimal planned_qty = row.getValue("quantity", BigDecimal.ZERO);
                BigDecimal issued_qty = row.getValue("closed_quantity", BigDecimal.ZERO);
                BigDecimal open_qty = planned_qty.subtract(issued_qty);

                String qty_str = "总数:" + App.formatNumber(planned_qty, "0.##");
                qty_str += "/已发:" + App.formatNumber(issued_qty, "0.##");
                qty_str += "/未发:" + App.formatNumber(open_qty, "0.##");

                pn_wo_issue_editor.this.txt_planned_quantity_cell.setContentText(qty_str);
                pn_wo_issue_editor.this.txt_location_cell.setContentText(row.getValue("location", ""));

                pn_wo_issue_editor.this.txt_lot_number_cell.setTag(null);
                pn_wo_issue_editor.this.txt_lot_number_cell.setContentText("");
                pn_wo_issue_editor.this.txt_issue_quantity_cell.setContentText("");
                pn_wo_issue_editor.this.txt_onhand_cell.setContentText("");
                pn_wo_issue_editor.this.txt_onhand_cell.setTag(null);
                pn_wo_issue_editor.this.txt_surplus_cell.setContentText("");
            }
        });
    }

    private BigDecimal _lot_qtys;
    private String _lots;
    private DataTable _loc_lot_tb;

    public void loadTLocation(String tl) {
        String sql = "exec p_mm_wo_issue_get_lot_number_by_locations ?,?,?";
        Parameters p = new Parameters().add(1, _order_id).add(2, _order_row.getValue("line_id", 0)).add(3, tl);
        final Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (r.HasError) {
            App.Current.showError(this.getContext(), r.Error);
        }

        if (r.Value != null && r.Value.Rows.size() > 0) {
            _lot_qtys = new BigDecimal(0);
            _lots = "";
            String lot = "";
            _loc_lot_tb = r.Value;
            for (DataRow rw : r.Value.Rows) {
                _lot_qtys = _lot_qtys.add(rw.getValue("quantity", BigDecimal.class));
                _lots += "," + rw.getValue("lot_number", "");
                lot = rw.getValue("lot_number", "");
            }
            this.loadLotNumber(lot);
            this.txt_location_cell.setContentText(tl);
            this.txt_lot_number_cell.setContentText(_lots);
            this.txt_issue_quantity_cell.setContentText(String.valueOf(_lot_qtys));
            this.txt_onhand_cell.setContentText(String.valueOf(_lot_qtys));
            //_lot_row.setValue("quantity", String.valueOf(_lot_qtys));

        }
    }


    public void loadTransferLocation(String tl) {
        String sql = "exec p_mm_wo_issue_get_lot_number_by_locations ?,?,?";
        Parameters p = new Parameters().add(1, _order_id).add(2, _order_row.getValue("line_id", 0)).add(3, tl);
        final Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (r.HasError) {
            App.Current.showError(this.getContext(), r.Error);
        }

        if (r.Value != null && r.Value.Rows.size() > 0) {
            if (r.Value.Rows.size() > 1) {

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which >= 0) {
                            DataRow row = r.Value.Rows.get(which);
                            pn_wo_issue_editor.this.showLotNumber(row, true);
                        }
                        dialog.dismiss();
                    }
                };

                final TableAdapter adapter = new TableAdapter(this.getContext()) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        DataRow row = (DataRow) r.Value.Rows.get(position);
                        if (convertView == null) {
                            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_lot_number, null);
                            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
                        }

                        TextView num = (TextView) convertView.findViewById(R.id.num);
                        TextView txt_lot_number = (TextView) convertView.findViewById(R.id.txt_lot_number);
                        TextView txt_quantity = (TextView) convertView.findViewById(R.id.txt_quantity);

                        num.setText(String.valueOf(position + 1));
                        String lot_number = row.getValue("lot_number", "");
                        String qty = row.getValue("date_code", "") + ", " + App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##") + " " + row.getValue("uom_code", "");

                        txt_lot_number.setText(lot_number);
                        txt_quantity.setText(qty);

                        return convertView;
                    }
                };

                adapter.DataTable = r.Value;
                adapter.notifyDataSetChanged();

                new AlertDialog.Builder(this.getContext())
                        .setTitle("选择出库批次")
                        .setSingleChoiceItems(adapter, 0, listener)
                        .setNegativeButton("取消", null).show();

            } else {
                DataRow row = r.Value.Rows.get(0);
                this.showLotNumber(row, true);
            }
        }
    }


    public void loadLotNumber(String lotNumber) {
        this.ProgressDialog.show();

        if (_order_row == null) {
            App.Current.showError(this.getContext(), "没有指定发料任务，无法扫描条码。");
            return;
        }

        final long order_id = _order_row.getValue("id", Long.class);
        final int line_id = _order_row.getValue("line_id", Integer.class);
        String sql = "exec p_mm_wo_issue_get_lot_number ?,?,?";
        Parameters p = new Parameters().add(1, order_id).add(2, line_id).add(3, lotNumber);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {

                pn_wo_issue_editor.this.ProgressDialog.dismiss();

                Result<DataRow> r = this.Value;
                if (r.HasError) {
                    App.Current.showError(pn_wo_issue_editor.this.getContext(), r.Error);
                    return;
                }

                pn_wo_issue_editor.this.showLotNumber(r.Value, false);
            }
        });
    }

    public void showLotNumber(DataRow row, boolean showLotQuantity) {
        _lot_row = row;
        final boolean showLotQuantityv = showLotQuantity;
        String result = _lot_row.getValue("result", "");
        if (result.length() > 0) {

            if (result.indexOf("\\^批次D/C") == -1) {
                App.Current.showError(pn_wo_issue_editor.this.getContext(), result);
                return;
            } else {
                App.Current.question(pn_wo_issue_editor.this.getContext(), result, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loaditem(_lot_row, showLotQuantityv);
                    }
                });
            }

        } else {
            loaditem(row, showLotQuantity);
        }


    }

    public void loaditem(DataRow row, boolean showLotQuantity) {
        BigDecimal plan_qty = BigDecimal.ZERO;
        if (showLotQuantity) {
            plan_qty = row.getValue("quantity", BigDecimal.ZERO);
        } else {
            try {
                plan_qty = new BigDecimal(_scan_quantity);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        BigDecimal issued_qty = _order_row.getValue("issued_quantity", BigDecimal.ZERO);
        BigDecimal open_qty = plan_qty.subtract(issued_qty);
        BigDecimal onhand_quantity = _lot_row.getValue("quantity", BigDecimal.ZERO);

        String issue_qty = "";
        String surplus_qty = "";
        String onhand_qty = App.formatNumber(onhand_quantity, "0.##");
        if (open_qty.compareTo(onhand_quantity) > 0) {
            issue_qty = onhand_qty;
        } else {
            issue_qty = App.formatNumber(open_qty, "0.##");
            surplus_qty = App.formatNumber(onhand_quantity.subtract(open_qty), "0.##");
        }

        pn_wo_issue_editor.this.txt_vendor_name_cell.setContentText(_lot_row.getValue("vendor_name", ""));
        pn_wo_issue_editor.this.txt_vendor_name_cell.setTag(_lot_row.getValue("vendor_id"));
        pn_wo_issue_editor.this.txt_lot_number_cell.setContentText(_lot_row.getValue("lot_number", ""));
        pn_wo_issue_editor.this.txt_date_code_cell.setContentText(_lot_row.getValue("date_code", ""));
        pn_wo_issue_editor.this.txt_location_cell.setContentText(_lot_row.getValue("locations", ""));

        pn_wo_issue_editor.this.txt_issue_quantity_cell.setContentText(issue_qty);
        pn_wo_issue_editor.this.txt_onhand_cell.setContentText(onhand_qty);
        pn_wo_issue_editor.this.txt_surplus_cell.setContentText(surplus_qty);
    }

    public void printLabel(final String quantity) {
        if (quantity == null || quantity.length() == 0) {
            App.Current.showError(this.getContext(), "没有指定标签数量。");
            return;
        }

        if (_lot_row == null) {
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
                                        App.Current.toastError(pn_wo_issue_editor.this.getContext(), value.Error);
                                        return;
                                    }
                                    if (value.Value != null) {
                                        Intent intent = new Intent();
                                        intent.setClass(App.Current.Workbench, Demo_ad_escActivity_for_wo_issue.class);
                                        String cur_date = value.Value.getValue("cur_date", "");
                                        intent.putExtra("cur_date", cur_date);
                                        intent.putExtra("org_code", _lot_row.getValue("org_code", ""));
                                        intent.putExtra("item_code", _lot_row.getValue("item_code", ""));
                                        intent.putExtra("vendor_model", _lot_row.getValue("vendor_model", ""));
                                        intent.putExtra("lot_number", _lot_row.getValue("lot_number", ""));
                                        intent.putExtra("vendor_lot", _lot_row.getValue("vendor_lot", ""));
                                        intent.putExtra("date_code", _lot_row.getValue("date_code", ""));
                                        intent.putExtra("station_code", _lot_row.getValue("station_code", ""));
                                        intent.putExtra("task_order_code", _lot_row.getValue("task_order_code", ""));
                                        intent.putExtra("line_name", _lot_row.getValue("line_name", ""));
                                        intent.putExtra("quantity", quantity);
                                        intent.putExtra("ut", _order_row.getValue("uom_code", ""));
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
                            App.Current.Print("mm_item_lot_label", "打印物料标签", parameters);
                        }
                    }
                }).create();
        dialog1.show();


//		Intent intent = new Intent();
//        intent.setClass(this.getContext(), frm_item_lot_printer.class);
//        intent.putExtra("org_code", _lot_row.getValue("org_code", ""));
//        intent.putExtra("item_code", _lot_row.getValue("item_code", ""));
//        intent.putExtra("vendor_model", _lot_row.getValue("vendor_model", ""));
//        intent.putExtra("lot_number", _lot_row.getValue("lot_number", ""));
//        intent.putExtra("vendor_lot", _lot_row.getValue("vendor_lot", ""));
//        intent.putExtra("date_code", _lot_row.getValue("date_code", ""));
//        intent.putExtra("quantity", quantity);
//        intent.putExtra("ut", _order_row.getValue("uom_code", ""));
//
//        this.getContext().startActivity(intent);
    }

    public void showList(boolean forLine) {
        if (_order_row == null) {
            App.Current.showError(this.getContext(), "没有发料申请数据。");
            return;
        }

        int line_id = 0;
        if (forLine) {
            line_id = _order_row.getValue("line_id", 0);
        }

        String sql = "exec p_mm_wo_get_transaction_list ?,?,?,?";
        Parameters p = new Parameters()
                .add(1, _order_row.getValue("type", ""))
                .add(2, _order_row.getValue("id", 0L))
                .add(3, line_id)
                .add(4, App.Current.UserID);

        final Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (r.HasError) {
            App.Current.showError(this.getContext(), r.Error);
            return;
        }

        if (r.Value != null && r.Value.Rows.size() > 0) {
            final TableAdapter adapter = new TableAdapter(this.getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    DataRow row = (DataRow) r.Value.Rows.get(position);
                    if (convertView == null) {
                        convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_wo_transaction, null);
                        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
                        icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
                    }

                    TextView num = (TextView) convertView.findViewById(R.id.num);
                    TextView txt_item_code = (TextView) convertView.findViewById(R.id.txt_item_code);
                    TextView txt_item_name = (TextView) convertView.findViewById(R.id.txt_item_name);
                    TextView txt_lot_number = (TextView) convertView.findViewById(R.id.txt_lot_number);
                    TextView txt_quantity = (TextView) convertView.findViewById(R.id.txt_quantity);

                    num.setText(String.valueOf(position + 1));

                    txt_item_code.setText(row.getValue("item_code", "") + ", " + App.formatDateTime(row.getValue("create_time"), "MM-dd HH:mm:ss"));
                    txt_item_name.setText(row.getValue("item_name", ""));
                    txt_lot_number.setText(row.getValue("lot_number", "") + ", " + row.getValue("date_code", ""));
                    txt_quantity.setText(row.getValue("warehouse_code", "") + ", " + App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##") + " " + row.getValue("uom_code", ""));

                    return convertView;
                }
            };

            adapter.DataTable = r.Value;
            adapter.notifyDataSetChanged();

            new AlertDialog.Builder(this.getContext())
                    .setTitle("查看发料记录")
                    .setSingleChoiceItems(adapter, 0, null)
                    .setNegativeButton("取消", null).show();
        } else {
            App.Current.showError(this.getContext(), "没有数据。");
            return;
        }
    }

    @Override
    public void commit() {
        if (_order_row == null) {
            App.Current.showError(this.getContext(), "没有发料申请数据，不能提交。");
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
        if (_loc_lot_tb == null) {
            BigDecimal onhand_quantity = _lot_row.getValue("quantity", BigDecimal.ZERO);
            if (issue_quantity.compareTo(onhand_quantity) > 0) {
                App.Current.showError(this.getContext(), "实发数量超过现有量，不能提交。");
                return;
            }
        }

        App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                Map<String, String> entry = new HashMap<String, String>();
                ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
                if (_loc_lot_tb != null) {
                    for (DataRow rw : _loc_lot_tb.Rows) {
                        entry.put("code", _order_row.getValue("code", ""));
                        entry.put("create_user", App.Current.UserID);
                        entry.put("order_id", String.valueOf(_order_row.getValue("id", 0L)));
                        entry.put("order_line_id", String.valueOf(_order_row.getValue("line_id", 0)));
                        entry.put("quantity", String.valueOf(rw.getValue("quantity", BigDecimal.class)));
                        entry.put("lot_number", rw.getValue("lot_number", ""));
                        entry.put("date_code", rw.getValue("date_code", ""));
                        entry.put("vendor_id", String.valueOf(rw.getValue("vendor_id", 0)));
                        entry.put("vendor_model", rw.getValue("vendor_model", ""));
                        entry.put("vendor_lot", rw.getValue("vendor_lot", ""));
                        entry.put("warehouse_id", String.valueOf(_order_row.getValue("warehouse_id", 0)));
                        entries.add(entry);
                    }
                } else {
                    entry.put("code", _order_row.getValue("code", ""));
                    entry.put("create_user", App.Current.UserID);
                    entry.put("order_id", String.valueOf(_order_row.getValue("id", 0L)));
                    entry.put("order_line_id", String.valueOf(_order_row.getValue("line_id", 0)));
                    entry.put("quantity", String.valueOf(issue_quantity));
                    entry.put("lot_number", lot_number);
                    entry.put("date_code", _lot_row.getValue("date_code", ""));
                    entry.put("vendor_id", String.valueOf(_lot_row.getValue("vendor_id", 0)));
                    entry.put("vendor_model", _lot_row.getValue("vendor_model", ""));
                    entry.put("vendor_lot", _lot_row.getValue("vendor_lot", ""));
                    entry.put("warehouse_id", String.valueOf(_order_row.getValue("warehouse_id", 0)));
                    entries.add(entry);
                }

                //生成XML数据，并传给存储过程
                String xml = XmlHelper.createXml("wo_issues", null, null, "wo_issue", entries);
                String sql = "exec p_mm_wo_issue_create ?,?";
                Connection conn = App.Current.DbPortal.CreateConnection(pn_wo_issue_editor.this.Connector);
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
                            App.Current.showError(pn_wo_issue_editor.this.getContext(), rs.Error);
                            return;
                        }

                        App.Current.toastInfo(pn_wo_issue_editor.this.getContext(), "提交成功");
                        _order_row = null;
                        _lot_row = null;
                        _loc_lot_tb = null;
                        pn_wo_issue_editor.this.clear();
                        pn_wo_issue_editor.this.loadTaskOrderItem(_rownum);
                    }
                } catch (SQLException e) {
                    App.Current.showInfo(pn_wo_issue_editor.this.getContext(), e.getMessage());
                    e.printStackTrace();

                    pn_wo_issue_editor.this.clear();
                    pn_wo_issue_editor.this.loadTaskOrderItem(_rownum);
                }
            }
        });
    }

    public void clear() {
        this.txt_vendor_name_cell.setContentText("");
        this.txt_date_code_cell.setContentText("");
        this.txt_lot_number_cell.setContentText("");
        this.txt_location_cell.setContentText("");
        this.txt_issue_quantity_cell.setContentText("0");
    }

    public void clearAll() {
        this.txt_wo_issue_order_cell.setContentText("");
        this.txt_item_code_cell.setContentText("");
        this.txt_item_name_cell.setContentText("");
        this.txt_warehouse_cell.setContentText("");
        this.txt_planned_quantity_cell.setContentText("");
        this.txt_issue_quantity_cell.setContentText("");
        this.txt_onhand_cell.setContentText("");
        this.txt_surplus_cell.setContentText("");
        this.txt_vendor_name_cell.setContentText("");
        this.txt_date_code_cell.setContentText("");
        this.txt_lot_number_cell.setContentText("");
        this.txt_location_cell.setContentText("");
    }
}
