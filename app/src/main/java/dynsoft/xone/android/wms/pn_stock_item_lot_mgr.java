package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dynsoft.xone.android.blueprint.Demo_ad_escActivity;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.link.PaneLinker;

public class pn_stock_item_lot_mgr extends pn_mgr {

    public pn_stock_item_lot_mgr(Context context) {
        super(context);
    }

    private EditText txt_split_qty;

    @Override
    public void setContentView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_stock_item_lot_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        this.Matrix.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                if (arg2 < Adapter.DataTable.Rows.size()) {
                    final DataRow row = (DataRow) Adapter.getItem((int) arg2);
                    pn_stock_item_lot_mgr.this.txt_split_qty = new EditText(pn_stock_item_lot_mgr.this.getContext());
                    txt_split_qty.setText(App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.###"));
                    new AlertDialog.Builder(pn_stock_item_lot_mgr.this.getContext())
                            .setTitle("请输入数量")
                            .setView(pn_stock_item_lot_mgr.this.txt_split_qty)
                            //.setText()
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //txt_split_qty.setTe
                                    String split_qty = txt_split_qty.getText().toString().trim();
                                    if (split_qty.length() > 0) {
                                        BigDecimal total_qty = row.getValue("quantity", BigDecimal.ZERO);
                                        BigDecimal put_qty = new BigDecimal(split_qty);
                                        if (put_qty.compareTo(total_qty) <= 0) {
                                            pn_stock_item_lot_mgr.this.printLabel(row, split_qty);
                                        } else {
                                            App.Current.toastError(pn_stock_item_lot_mgr.this.getContext(), "请输入小于批次数量的数量！");
                                        }

                                    } else {
                                        App.Current.showInfo(pn_stock_item_lot_mgr.this.getContext(), "请先输入打印的数量。");
                                    }
                                }
                            }).setNegativeButton("取消", null)
                            .show();

                } else {
                    pn_stock_item_lot_mgr.this.refreshData(false);
                }

            }

        });
    }


    public void printLabel(final DataRow row, final String split_qty) {


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
                                        App.Current.toastError(pn_stock_item_lot_mgr.this.getContext(), value.Error);
                                        return;
                                    }
                                    if (value.Value != null) {
                                        Intent intent = new Intent();
                                        intent.setClass(App.Current.Workbench, Demo_ad_escActivity.class);
                                        String cur_date = value.Value.getValue("cur_date", "");
                                        intent.putExtra("cur_date", cur_date);
                                        intent.putExtra("org_code", row.getValue("org_code", ""));
                                        intent.putExtra("item_code", row.getValue("item_num", ""));
                                        intent.putExtra("vendor_model", row.getValue("vendor_model", ""));
                                        intent.putExtra("lot_number", row.getValue("lot_number", ""));
                                        intent.putExtra("vendor_lot", row.getValue("vendor_lot", ""));
                                        intent.putExtra("date_code", row.getValue("date_code", ""));
                                        intent.putExtra("quantity", split_qty);
                                        intent.putExtra("ut", row.getValue("uom_code", ""));
                                        App.Current.Workbench.startActivity(intent);
                                    }
                                }
                            });


                        } else {
                            Map<String, String> parameters = new HashMap<String, String>();
                            parameters.put("org_code", row.getValue("org_code", ""));
                            parameters.put("item_code", row.getValue("item_num", ""));
                            parameters.put("vendor_model", row.getValue("vendor_model", ""));
                            parameters.put("lot_number", row.getValue("lot_number", ""));
                            parameters.put("vendor_lot", row.getValue("vendor_lot", ""));
                            parameters.put("date_code", row.getValue("date_code", ""));
                            parameters.put("quantity", split_qty);
                            parameters.put("ut", row.getValue("uom_code", ""));
                            App.Current.Print("mm_item_lot_label", "打印物料批次标签", parameters);
                        }

                    }
                }).create();
        dialog1.show();
    }

    @Override
    public void create() {

    }

    @Override
    public void onScan(final String barcode) {
        this.SearchBox.setText(barcode);
        this.Adapter.DataTable = null;
        this.Adapter.notifyDataSetChanged();
        this.refresh();
        this.SearchBox.setText("");
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        DataRow row = (DataRow) Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_begin_stock, null);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView line0 = (TextView) convertView.findViewById(R.id.txt_line0);
        TextView line1 = (TextView) convertView.findViewById(R.id.txt_line1);
        TextView line2 = (TextView) convertView.findViewById(R.id.txt_line2);
        TextView line3 = (TextView) convertView.findViewById(R.id.txt_line3);
        TextView line4 = (TextView) convertView.findViewById(R.id.txt_line4);
        TextView line5 = (TextView) convertView.findViewById(R.id.txt_line5);
        TextView line6 = (TextView) convertView.findViewById(R.id.txt_line6);

        line0.setText(String.valueOf(position + 1));
        line1.setText(row.getValue("item_num", "") + "," + row.getValue("org_code", "") + "," + row.getValue("vendor_model", ""));
        line2.setText(row.getValue("vendor_name", ""));
        line3.setText(row.getValue("lot_number", ""));
        line4.setText(row.getValue("item_desc", ""));
        String qty = "D/C:" + row.getValue("date_code", "") + "," + App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##") + row.getValue("uom_code", "");
        line5.setText(qty);
        line6.setText(row.getValue("location_code", ""));
        return convertView;
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "with temp as (SELECT row_number() over(order by date_code asc) rownum,";
        expr.SQL += "org_code,item_num,vendor_lot,vendor_name,item_desc,lot_number,quantity,uom_code,date_code,vendor_model,location_code ";
        expr.SQL += "FROM dbo.v_mm_stock_lot_init where (item_num like '%'+isnull(?,'')+'%' or date_code like '%'+isnull(?,'')+'%'  or lot_number like '%'+isnull(?,'')+'%' or isnull(?,'') like '%'+lot_number+'%')) ";
        expr.SQL += "select (select count(*) from temp) lines_count,* from temp where rownum>=? and rownum<=? order by date_code asc";
        expr.Parameters = new Parameters().add(1, search).add(2, search).add(3, search).add(4, search).add(5, start).add(6, end);
        return expr;
    }

    public void commit(DataRow row) {
    }
}
