package dynsoft.xone.android.wms;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.SwitchCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.PromptCallback;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.Book;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.start.FrmLogin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml.Encoding;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

public class pn_stock_batch_locations_editor extends pn_editor {

    public pn_stock_batch_locations_editor(Context context) {
        super(context);
    }


    public TextCell txt_item_code_cell;
    public TextCell txt_org_code_cell;
    public TextCell txt_quantity_cell;
    public ButtonTextCell txt_locations_cell;
    public ButtonTextCell txt_warehouse_cell;
    public TextCell txt_total_info_cell;
    public ButtonTextCell txt_lot_number_cell;
    public String lot_number;
    private ArrayList<DataRow> dataRows;
    private DataRow _rowv;
    private int scan_count;


    @Override
    public void setContentView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(
                R.layout.pn_stock_batch_locations_editor, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        this.txt_quantity_cell
                .onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onPrepared() {

        super.onPrepared();
        scan_count = 0;
        this.txt_item_code_cell = (TextCell) this
                .findViewById(R.id.txt_item_code_cell);
        this.txt_org_code_cell = (TextCell) this
                .findViewById(R.id.txt_org_code_cell);
        this.txt_quantity_cell = (TextCell) this
                .findViewById(R.id.txt_quantity_cell);
        this.txt_lot_number_cell = (ButtonTextCell) this
                .findViewById(R.id.txt_lot_number_cell);
        this.txt_total_info_cell = (TextCell) this.findViewById(R.id.txt_total_info_cell);
        this.txt_locations_cell = (ButtonTextCell) this.findViewById(R.id.txt_locations_cell);
        this.txt_warehouse_cell = (ButtonTextCell) this.findViewById(R.id.txt_warehouse_cell);

        if (this.txt_item_code_cell != null) {
            this.txt_item_code_cell.setLabelText("编码");
            this.txt_item_code_cell.setReadOnly();
        }
        if (this.txt_lot_number_cell != null) {
            this.txt_lot_number_cell.setReadOnly();
            this.txt_lot_number_cell.setBackgroundColor(Color.parseColor("#FFE4B5"));
            this.txt_lot_number_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_light"));
            this.txt_lot_number_cell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    pn_stock_batch_locations_editor.this.txt_lot_number_cell.setContentText("");

                }

            });
        }
        if (this.txt_total_info_cell != null) {
            //this.txt_warehouse_cell.setLabelText("库位");
            this.txt_total_info_cell.setReadOnly();
        }

        if (this.txt_org_code_cell != null) {
            this.txt_org_code_cell.setLabelText("组织");
            //this.txt_org_code_cell.setReadOnly();
        }
        if (this.txt_quantity_cell != null) {
            this.txt_quantity_cell.setLabelText("数量");
            this.txt_quantity_cell.setReadOnly();
        }

        if (this.txt_locations_cell != null) {

            this.txt_locations_cell.setLabelText("储位");
            this.txt_locations_cell.setReadOnly();
            this.txt_locations_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_light"));
            this.txt_locations_cell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    pn_stock_batch_locations_editor.this.txt_locations_cell.setContentText("");

                }

            });
        }

        if (this.txt_warehouse_cell != null) {

            this.txt_warehouse_cell.setLabelText("库位");
            this.txt_warehouse_cell.setReadOnly();
//            this.txt_warehouse_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_light"));
            this.txt_warehouse_cell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    chooseWarehouse(txt_warehouse_cell);

                }

            });
        }
    }

    private void chooseWarehouse(final ButtonTextCell txt_warehouse_cell) {
        String sql = "select * from mm_warehouse";
        Parameters p = new Parameters();
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(pn_stock_batch_locations_editor.this.getContext(), value.Error);
                    App.Current.playSound(R.raw.error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    ArrayList<String> names = new ArrayList<String>();
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        names.add(value.Value.Rows.get(i).getValue("name", ""));
                    }
                    dataRows = new ArrayList<DataRow>();
                    multiChoiceDialog_Warehouse(value.Value, txt_warehouse_cell);
                } else {
                    App.Current.toastError(pn_stock_batch_locations_editor.this.getContext(), "没有库位");
                    App.Current.playSound(R.raw.error);
                }
            }
        });
    }

    private void multiChoiceDialog_Warehouse(final DataTable dataTable, final ButtonTextCell editText) {
        ArrayList<String> names = new ArrayList<String>();
        for (DataRow dataRow : dataTable.Rows) {
            String name = dataRow.getValue("name", "");
            String code = dataRow.getValue("code", "");
            name = code + "-" + name ;
            Log.e("len", "name : " + name);
            names.add(name);
        }
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    String name = dataTable.Rows.get(which).getValue("name", "");
                    Integer id = dataTable.Rows.get(which).getValue("id", 0);
                    editText.TextBox.setText(name);
                    editText.TextBox.setTag(id);
                    dataRows.add(dataTable.Rows.get(which));
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(this.getContext()).setTitle("请选择")
                .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                        (editText.TextBox.getText().toString()), listener)
                .setNegativeButton("取消", null).show();
    }

    @Override
    public void onScan(final String barcode) {
        final String bar_code = barcode.trim();
        // 扫描发运单条码
        if (bar_code.startsWith("CRQ:")) {
            String tempbar_code = bar_code.substring(4, bar_code.length());
            String[] arr = tempbar_code.split("-");
            lot_number = arr[0];
            String item_code = arr[1];
            String qty = arr[2];
            BigDecimal q1 = new BigDecimal(qty);
            String entry_qty = this.txt_quantity_cell.getContentText().trim();
            String lot_numbers = this.txt_lot_number_cell.getContentText().trim();
            if (lot_numbers.contains(lot_number)) {
                App.Current.showError(this.getContext(), "请检查批次号，批次号不允许重复扫描！");
                return;
            }
            if (entry_qty.length() == 0 || entry_qty == null) {
                entry_qty = "0";
            }
            BigDecimal q2 = new BigDecimal(entry_qty);
            BigDecimal q3 = q1.add(q2);


            if (lot_numbers.length() > 0) {
                lot_numbers += ", ";
            }

            String item_no = this.txt_item_code_cell.getContentText().trim();
            if (!item_code.contains(item_no)) {
                App.Current.showError(this.getContext(), "请检查料号，两次扫描的结果不一致");
                return;
            }
            this.txt_item_code_cell.setContentText(item_code);
            loadorgcode(lot_number, item_code);

            this.txt_lot_number_cell.setContentText(lot_numbers + lot_number);
            scan_count = scan_count + 1;
            this.txt_quantity_cell.setContentText(App.formatNumber(q3, "0.##"));
            this.txt_total_info_cell.setBackgroundColor(Color.parseColor("#F5F5DC"));
            this.txt_total_info_cell.setContentText("已扫描箱数:" + String.valueOf(scan_count) + "箱");
        }

        if (bar_code.startsWith("L:")) {
            String loc = bar_code.substring(2, bar_code.length());
            String locs = this.txt_locations_cell.getContentText().trim();
            if (locs.contains(loc)) {
                return;
            }

            if (locs.length() > 0) {
                locs += ", ";
            }

            this.txt_locations_cell.setContentText(locs + loc);
        }
    }

    @Override
    public void commit() {
        String sql = "exec p_mm_stock_batch_update_loc_v2  ?,?,?,?,?,? ";
        String p_org_code = this.txt_org_code_cell.getContentText();
        String p_item_no = this.txt_item_code_cell.getContentText();
        String p_lot_number = this.txt_lot_number_cell.getContentText();
        String p_locs = this.txt_locations_cell.getContentText();
        Integer p_warehouse_id = (Integer) this.txt_warehouse_cell.TextBox.getTag();

        Log.e("len", "p_warehouse_id : " + p_warehouse_id);

        if (p_locs == null || p_locs.length() == 0) {
            App.Current.showError(this.getContext(), "没有输入储位，不能提交。");
            return;
        }

        if (p_warehouse_id == null || p_warehouse_id == 0) {
            App.Current.showError(this.getContext(), "没有输入库位，不能提交。");
            return;
        }

        if (p_org_code == null || p_org_code.length() == 0) {
            App.Current.showError(this.getContext(), "没有输入组织，不能提交。");
            return;
        }

        if (p_lot_number == null || p_lot_number.length() == 0) {
            App.Current.showError(this.getContext(), "没有输入批次，不能提交。");
            return;
        }

        if (p_item_no == null || p_item_no.length() == 0) {
            App.Current.showError(this.getContext(), "没有输入料号，不能提交。");
            return;
        }
        Parameters p = new Parameters().add(1, p_item_no).add(2, p_org_code).add(3, p_lot_number).add(4, p_locs).add(5, App.Current.UserID).add(6, p_warehouse_id);
        Result<Integer> result = App.Current.DbPortal.ExecuteNonQuery(this.Connector, sql, p);
        if (result.HasError) {
            App.Current.toastError(this.getContext(), result.Error);
        }
        if (result.Value > 0) {
            App.Current.toastInfo(pn_stock_batch_locations_editor.this.getContext(), "更新成功。");
            pn_stock_batch_locations_editor.this.clear();
        }


    }

    public void loadorgcode(String lot_number, String item_no) {
        String sql = "SELECT  d.code  FROM dbo.mm_stock_lot a,dbo.mm_item b ,dbo.mm_warehouse c,dbo.mm_organization d  WHERE a.item_id =b.id AND a.warehouse_id =c.id  AND c.organization_id =d.id AND b.code =? AND a.lot_number =?";
        Parameters p = new Parameters().add(1, item_no).add(2, lot_number);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_stock_batch_locations_editor.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_stock_batch_locations_editor.this.getContext(), result.Error);
                    return;
                }


                if (result.Value == null) {
                    App.Current.showError(pn_stock_batch_locations_editor.this.getContext(), "系统无此批次。");
                    pn_stock_batch_locations_editor.this.clear();
                    return;
                }
                String org_code = result.Value.getValue("code", "");
                String old_org = pn_stock_batch_locations_editor.this.txt_org_code_cell.getContentText();
                if (old_org.length() != 0 && old_org != null) {
                    if (!old_org.contains(org_code)) {
                        pn_stock_batch_locations_editor.this.txt_org_code_cell.setContentText("");
                    }
                } else {

                    pn_stock_batch_locations_editor.this.txt_org_code_cell.setContentText(org_code);
                }
            }


        });
    }

    public void clear() {
        this.txt_item_code_cell.setContentText("");
        this.txt_locations_cell.setContentText("");
        this.txt_org_code_cell.setContentText("");
        this.txt_quantity_cell.setContentText("");
        this.txt_lot_number_cell.setContentText("");
        this.txt_total_info_cell.setContentText("");
        scan_count = 0;

    }
}
