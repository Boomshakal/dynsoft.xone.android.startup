package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import dynsoft.xone.android.blueprint.ZhuankuStatusActivity;
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

public class pn_tr_issue_mgr extends pn_mgr {

    public pn_tr_issue_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView()
    {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_tr_issue_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void create()
    {
        DataRow row = null;
        if (this.Adapter.DataTable != null && this.Adapter.DataTable.Rows.size() > 0) {
            row = this.Adapter.DataTable.Rows.get(0);
            Link link = new Link("pane://x:code=mm_and_tr_issue_editor");
            link.Parameters.add("order_id", row.getValue("id", ""));
            link.Parameters.add("order_code", row.getValue("code", ""));
            link.Parameters.add("line_id", row.getValue("line_id", ""));
            link.Open(this, this.getContext(), null);
        } else {
            App.Current.showInfo(this.getContext(), "û��ת�����롣");
        }
    }

    @Override
    public void openItem(DataRow row)
    {
        if (row == null) return;

        Link link = new Link("pane://x:code=mm_and_tr_issue_editor");
        link.Parameters.add("order_id", row.getValue("id", ""));
        link.Parameters.add("order_code", row.getValue("code", ""));
        link.Parameters.add("line_id", row.getValue("line_id", ""));
        link.Open(this, this.getContext(), null);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();

        this.Matrix.setOnCreateContextMenuListener(new OnCreateContextMenuListener(){
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                menu.add("��ӡת��״̬��...");
                menu.add("��ӡת�ⵥ...");
                menu.add("�ύת�ⵥ...");
            }
        });
    }

    private EditText txt_locations;

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuItem.getMenuInfo();
        final DataRow row = this.Adapter.DataTable.Rows.get(info.position);

        if (menuItem.getTitle() == "��ӡת��״̬��...") {

            final String items[] = {"����Τ��", "֥��"};

            AlertDialog dialog1 = new AlertDialog.Builder(App.Current.Workbench).setTitle("��ѡ���ӡ��")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 1) {
                                long id = row.getValue("id", Long.class);
                                int line_id = row.getValue("line_id", 0);
                                String sql = "exec p_mm_win_tr_order_get_report_data ?,?,?";
                                Parameters p = new Parameters().add(1, id).add(2, App.Current.UserID).add(3, line_id);
                                App.Current.DbPortal.ExecuteDataSetAsync("core_and", sql, p, new ResultHandler<DataSet>() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        Result<DataSet> value = Value;
                                        if (value.HasError) {
                                            App.Current.toastError(pn_tr_issue_mgr.this.getContext(), value.Error);
                                            return;
                                        }
                                        if (value.Value != null && value.Value.Tables.size() > 0) {
                                            Intent intent = new Intent(App.Current.Workbench, ZhuankuStatusActivity.class);
                                            intent.putExtra("type", "ת��״̬��ǩ");
                                            DataTable dataTableHead = value.Value.Tables.get(0);  //head��
                                            if (dataTableHead.Rows.size() > 0) {
                                                String cur_date = dataTableHead.Rows.get(0).getValue("cur_date", "");
                                                String code = dataTableHead.Rows.get(0).getValue("code", "");
                                                String comment = dataTableHead.Rows.get(0).getValue("comment", "");
                                                String war_code = dataTableHead.Rows.get(0).getValue("war_code", "");
                                                String to_war_code = dataTableHead.Rows.get(0).getValue("to_war_code", "");
                                                String organization_code = dataTableHead.Rows.get(0).getValue("organization_code", "");
                                                intent.putExtra("cur_date", cur_date);
                                                intent.putExtra("code", code);
                                                intent.putExtra("comment", comment);
                                                intent.putExtra("organization_code", organization_code);
                                                intent.putExtra("war_code", war_code);
                                                intent.putExtra("to_war_code", to_war_code);

                                                if (value.Value.Tables.size() > 1) {
                                                    DataTable dataTableItem = value.Value.Tables.get(1);  //item��
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
//                                intent.putExtra("type", "����״̬��");
//
//                                App.Current.Workbench.startActivity(intent);
                            } else {
                                Map<String, String> parameters = new HashMap<String, String>();
                                parameters.put("id", String.valueOf(row.getValue("id", Long.class)));
                                parameters.put("code", row.getValue("code", ""));
                                parameters.put("line_id", String.valueOf(row.getValue("line_id", 0)));
                                parameters.put("type", "��ӡת��״̬��");
//
                                App.Current.Print("mm_tr_issue_order", "��ӡת��״̬��", parameters);
                            }
                        }
                    }).create();
            dialog1.show();
//			Intent intent = new Intent();
//	        intent.setClass(this.getContext(), frm_wo_issue_order_printer.class);
//	        intent.putExtra("id", row.getValue("id", Long.class));
//	        intent.putExtra("code", row.getValue("code", ""));
//	        intent.putExtra("type", "����״̬��");
//	        this.getContext().startActivity(intent);

        } else if (menuItem.getTitle() == "��ӡת�ⵥ...") {

            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("id", String.valueOf(row.getValue("id", Long.class)));
            parameters.put("line_id", String.valueOf(row.getValue("line_id", 0)));
            parameters.put("code", row.getValue("code", ""));
            parameters.put("type", "��ӡת�ⵥ");

            App.Current.Print("mm_tr_issue_order", "��ӡת�ⵥ", parameters);

//			Intent intent = new Intent();
//	        intent.setClass(this.getContext(), frm_wo_issue_order_printer.class);
//	        intent.putExtra("id", row.getValue("id", Long.class));
//	        intent.putExtra("code", row.getValue("code", ""));
//	        intent.putExtra("type", "�������ϵ�");
//	        this.getContext().startActivity(intent);

        } else if (menuItem.getTitle() == "�ύת�ⵥ...") {

            final long id = row.getValue("id", Long.class);
            final int line_id=row.getValue("line_id",0);
            final String code = row.getValue("code", "");

            String sql = "exec p_mm_tr_issue_get_uncommited_items ?,?,?";
            Parameters p = new Parameters().add(1, App.Current.UserID).add(2, id).add(3, line_id);
            Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(pn_tr_issue_mgr.this.Connector, sql, p);
            if (r.HasError) {
                App.Current.toastInfo(pn_tr_issue_mgr.this.getContext(), r.Error);
            }

            if (r.Value != null && r.Value.Rows.size() > 0) {
                App.Current.toastInfo(pn_tr_issue_mgr.this.getContext(), "ת�����롾" + code + "������" + String.valueOf(r.Value.Rows.size()) + "����¼û�з��꣬�����ύ��");
            } else {
                this.txt_locations = new EditText(pn_tr_issue_mgr.this.getContext());
                new AlertDialog.Builder(pn_tr_issue_mgr.this.getContext())
                        .setTitle("��ɨ�财λ")
                        .setView(this.txt_locations)

                        .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String locations = txt_locations.getText().toString().trim();
                                if (locations.length() > 0) {
                                    String sql = "exec p_mm_tr_issue_commit ?,?,?,?";
                                    Parameters p = new Parameters().add(1, App.Current.UserID).add(2, id).add(3, line_id).add(4, locations);
                                    Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(pn_tr_issue_mgr.this.Connector, sql, p);
                                    if (r.HasError) {
                                        App.Current.toastInfo(pn_tr_issue_mgr.this.getContext(), r.Error);
                                    }

                                    if (r.Value > 0) {
                                        App.Current.toastInfo(pn_tr_issue_mgr.this.getContext(), "�ύ�ɹ�!");
                                        pn_tr_issue_mgr.this.refresh();
                                    }
                                    txt_locations.setText("");
                                } else {
                                    App.Current.showInfo(pn_tr_issue_mgr.this.getContext(), "����ɨ������봢λ��");
                                }
                            }
                        }).setNegativeButton("ȡ��", null)
                        .show();
            }
        }

        return true;
    }

    @Override
    public void onScan(final String barcode)
    {
        String txt = barcode;
        if (barcode.startsWith("CRQ:")) {
            String str = barcode.substring(4, barcode.length());
            String[] arr = str.split("-");
            if (arr.length > 0) {
                txt = arr[0];
            }

            this.SearchBox.setText(txt);
            this.Adapter.DataTable = null;
            this.Adapter.notifyDataSetChanged();
            this.refresh();

        } else if (barcode.startsWith("L:")){
            if (this.txt_locations != null) {
                String loc = barcode.substring(2, barcode.length());
                String locs = this.txt_locations.getText().toString().trim();
                if (locs.contains(loc)) {
                    return;
                }

                if (locs.length() > 0){
                    locs += ", ";
                }

                this.txt_locations.setText(locs+loc);
            }
        }
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
        DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_tr_transaction, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_tr_order_code = (TextView)convertView.findViewById(R.id.txt_tr_order_code);
        TextView txt_item_code = (TextView)convertView.findViewById(R.id.txt_item_code);
        TextView txt_item_name = (TextView)convertView.findViewById(R.id.txt_item_name);
        TextView txt_vendor_name = (TextView)convertView.findViewById(R.id.txt_vendor_name);
        TextView txt_quantity = (TextView)convertView.findViewById(R.id.txt_quantity);
        TextView txt_warehouse = (TextView)convertView.findViewById(R.id.txt_warehouse);
        TextView txt_comment = (TextView)convertView.findViewById(R.id.txt_comment);

        num.setText(String.valueOf(position + 1));
        txt_tr_order_code.setText(row.getValue("code", ""));

        String item_code = row.getValue("from_item_code", "");
        String date_code = row.getValue("date_code", "");
        if (date_code != null && date_code.length() > 0) {
            item_code = date_code + "��" + item_code;
        }

        txt_item_code.setText(item_code + "��" + row.getValue("status", ""));
        txt_item_name.setText(row.getValue("from_item_name", ""));

        String vendor_name = row.getValue("vendor_name", "");
        if (vendor_name == null || vendor_name.length() == 0) {
            txt_vendor_name.setVisibility(View.GONE);
            txt_vendor_name.setText("");
        } else {
            txt_vendor_name.setText(vendor_name);
            txt_comment.setVisibility(View.VISIBLE);
        }

        String qty = String.format("��:%s, ��ת:%s, ��ת:%s %s",
                App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##"),
                App.formatNumber(row.getValue("open_quantity", BigDecimal.ZERO), "0.##"),
                App.formatNumber(row.getValue("closed_quantity", BigDecimal.ZERO), "0.##"),
                row.getValue("uom_code", ""));

        txt_quantity.setText(qty);

        String warehouse = String.format("�� %s.%s ��  %s.%s", row.getValue("from_organization_code", ""), row.getValue("from_warehouse_code", ""), row.getValue("to_organization_code", ""), row.getValue("to_warehouse_code", ""));
        txt_warehouse.setText(warehouse);

        String comment = row.getValue("comment", "");
        if (comment == null || comment.length() == 0) {
            txt_comment.setVisibility(View.GONE);
            txt_comment.setText("");
        } else {
            txt_comment.setVisibility(View.VISIBLE);
            txt_comment.setText(comment);
        }

//        String status = row.getValue("status","");
//        if (status.equals("��ת��")) {
//        	txt_tr_order_code.setTextColor(Color.BLUE);
//        	txt_item_code.setTextColor(Color.BLUE);
//        	txt_item_name.setTextColor(Color.BLUE);
//        	txt_vendor_name.setTextColor(Color.BLUE);
//        	txt_quantity.setTextColor(Color.BLUE);
//        	txt_warehouse.setTextColor(Color.BLUE);
//        	txt_comment.setTextColor(Color.BLUE);
//        } else if (status.equals("��ת��")){
//        	txt_tr_order_code.setTextColor(Color.RED);
//        	txt_item_code.setTextColor(Color.RED);
//        	txt_item_name.setTextColor(Color.RED);
//        	txt_vendor_name.setTextColor(Color.RED);
//        	txt_quantity.setTextColor(Color.RED);
//        	txt_warehouse.setTextColor(Color.RED);
//        	txt_comment.setTextColor(Color.RED);
//        }

        return convertView;
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_mm_tr_issue_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }

}
