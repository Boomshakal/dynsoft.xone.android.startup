package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.blueprint.ZaxiangStatusActivity;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataSet;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class pn_so_issue_mgr extends pn_mgr {

    public pn_so_issue_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_so_issue_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();

        this.Matrix.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add("打印发料状态牌...");
                menu.add("打印销出发料单...");
                menu.add("提交销出发料单...");
            }
        });
    }

    private EditText txt_locations;

    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        final DataRow row = this.Adapter.DataTable.Rows.get(info.position);

        if (menuItem.getTitle() == "打印发料状态牌...") {
            long id = row.getValue("id", Long.class);
            Log.e("lhz2011", String.valueOf(id));
            final String items[] = {"霍尼韦尔", "芝柯"};

            AlertDialog dialog1 = new AlertDialog.Builder(App.Current.Workbench).setTitle("请选择打印机")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 1) {
                                long id = row.getValue("id", Long.class);
                                String sql = "exec p_mm_so_issue_order_get_report_data_new ?,?";
                                Log.e("lhz2011", String.valueOf(id));
                                Log.e("lhz2012", String.valueOf(App.Current.UserID));
                                Parameters p = new Parameters().add(1, id).add(2, App.Current.UserID);
                                App.Current.DbPortal.ExecuteDataSetAsync("core_and", sql, p, new ResultHandler<DataSet>() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        Result<DataSet> value = Value;
                                        if (value.HasError) {
                                            App.Current.toastError(pn_so_issue_mgr.this.getContext(), value.Error);
                                            return;
                                        }
                                        if (value.Value != null && value.Value.Tables.size() > 0) {
                                            Intent intent = new Intent(App.Current.Workbench, ZaxiangStatusActivity.class);
                                            intent.putExtra("type", "发料状态标签");

                                            DataTable dataTableHead = value.Value.Tables.get(0);  //head表

                                            if (dataTableHead.Rows.size() > 0) {
                                                String cur_date = dataTableHead.Rows.get(0).getValue("cur_date", "");
                                                String code = dataTableHead.Rows.get(0).getValue("code", "");
                                                String comment = dataTableHead.Rows.get(0).getValue("comment", "");
                                                String organization_code = dataTableHead.Rows.get(0).getValue("organization_code", "");
                                                Log.e("lhz1", String.valueOf(cur_date));
                                                Log.e("lhz2", String.valueOf(code));
                                                Log.e("lhz3", String.valueOf(comment));
                                                Log.e("lhz3", String.valueOf(organization_code));
                                                intent.putExtra("cur_date", cur_date);
                                                intent.putExtra("code", code);
                                                intent.putExtra("comment", comment);
                                                intent.putExtra("organization_code", "销售部");

                                                if (value.Value.Tables.size() > 1) {
                                                    DataTable dataTableItem = value.Value.Tables.get(1);  //item表
                                                    if (dataTableItem.Rows.size() > 0) {
                                                        String store_keeper_name = dataTableItem.Rows.get(0).getValue("store_keeper_name", "");
                                                        intent.putExtra("store_keeper_name", store_keeper_name);
                                                    }
                                                }

                                                App.Current.Workbench.startActivity(intent);
                                            }
                                        }
                                    }
                                });
//                                Intent intent = new Intent();
//                                intent.setClass(App.Current.Workbench, Demo_ad_escActivity.class);
//
//                                intent.putExtra("id", String.valueOf(row.getValue("id", Long.class)));
//                                intent.putExtra("code", row.getValue("code", ""));
//                                intent.putExtra("type", "发料状态牌");
//
//                                App.Current.Workbench.startActivity(intent);
                            } else {
                                Map<String, String> parameters = new HashMap<String, String>();
                                parameters.put("id", String.valueOf(row.getValue("id", Long.class)));
                                parameters.put("code", row.getValue("code", ""));
                                parameters.put("type", "发料状态牌");
                                Log.e("lhz1", String.valueOf(row.getValue("id", Long.class)));
                                Log.e("lhz1", row.getValue("code", ""));
                                App.Current.Print("mm_so_issue_order", "打印发料状态牌", parameters);
                            }
                        }
                    }).create();
            dialog1.show();


        } else if (menuItem.getTitle() == "打印销出发料单...") {

            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("id", String.valueOf(row.getValue("id", Long.class)));
            parameters.put("code", row.getValue("code", ""));
            parameters.put("type", "生产发料单");

            App.Current.Print("mm_up_issue_order", "打印杂出发料单", parameters);


        } else if (menuItem.getTitle() == "提交销出发料单...") {

            final long id = row.getValue("id", Long.class);
            final long war_id = row.getValue("warehouse_id", 0);
            final String code = row.getValue("code", "");

            String sql = "SELECT COUNT(*) FROM mm_so_issue_order_item WHERE head_id=? AND open_quantity>0";
            Parameters p = new Parameters().add(1, row.getValue("id"));
            Result<Integer> r = App.Current.DbPortal.ExecuteScalar(pn_so_issue_mgr.this.Connector, sql, p, Integer.class);
            if (r.HasError) {
                App.Current.toastInfo(pn_so_issue_mgr.this.getContext(), r.Error);
                return false;
            }

            if (r.Value > 0) {
                new AlertDialog.Builder(pn_so_issue_mgr.this.getContext())
                        .setMessage("还有" + String.valueOf(r.Value) + "个物料没有完成出货扫描，确定要提交吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                commitIssueOrder(row);
                            }
                        }).setNegativeButton("取消", null)
                        .show();
            } else {
                new AlertDialog.Builder(pn_so_issue_mgr.this.getContext())
                        .setMessage("已全部完成出货扫描，确定要提交吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                commitIssueOrder(row);
                            }
                        }).setNegativeButton("取消", null)
                        .show();
            }

            return false;
        }

        return true;
    }


    public void commitIssueOrder(DataRow row) {
        Long order_id = row.getValue("id", 0L);
        String sql = "exec p_mm_so_issue_commit ?,?";
        Parameters p = new Parameters().add(1, order_id).add(2, App.Current.UserID);
        Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(pn_so_issue_mgr.this.Connector, sql, p);
        if (r.HasError) {
            App.Current.toastInfo(pn_so_issue_mgr.this.getContext(), r.Error);
            return;
        }

        if (r.Value > 0) {
            this.refresh();
        }
    }

    @Override
    public void openItem(DataRow row) {
        Link link = new Link("pane://x:code=mm_and_so_issue_editor");
        link.Parameters.add("order_id", row.getValue("id", 0L));
        link.Parameters.add("order_code", row.getValue("code", ""));
        link.Open(this, this.getContext(), null);
    }

    @Override
    public void onScan(final String barcode) {
        this.SearchBox.setText(barcode);
        this.Adapter.DataTable = null;
        this.Adapter.notifyDataSetChanged();
        this.refresh();
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        DataRow row = (DataRow) Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_so_issue, null);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView) convertView.findViewById(R.id.num);
        TextView txt_order_code = (TextView) convertView.findViewById(R.id.txt_order_code);
        TextView txt_customer_name = (TextView) convertView.findViewById(R.id.txt_customer_name);
        TextView txt_items = (TextView) convertView.findViewById(R.id.txt_items);
        TextView txt_comment = (TextView) convertView.findViewById(R.id.txt_comment);

        num.setText(String.valueOf(position + 1));
        txt_order_code.setText(row.getValue("org_code", "") + ", " + row.getValue("code", "") + ", " + App.formatDateTime(row.getValue("create_time"), "yyyy-MM-dd"));
        txt_customer_name.setText(row.getValue("customer_name", ""));
        txt_items.setText(row.getValue("status", "") + ", " + row.getValue("items", ""));

        String comment = row.getValue("comment", "");
        if (comment.length() > 0) {
            txt_comment.setText(comment);
            txt_comment.setVisibility(View.VISIBLE);
        } else {
            txt_comment.setText("");
            txt_comment.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_mm_so_issue_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3, end).add(4, search);
        return expr;
    }
}
