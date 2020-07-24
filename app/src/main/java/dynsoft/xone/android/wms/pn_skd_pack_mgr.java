package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

public class pn_skd_pack_mgr extends pn_mgr {

    public pn_skd_pack_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_skd_pack_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();

        this.Matrix.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                menu.add("��ӡ����״̬��...");
                menu.add("��ӡ�ӳ����ϵ�...");
                menu.add("�ύ�ӳ����ϵ�...");
            }
        });
    }

    private EditText txt_locations;

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuItem.getMenuInfo();
        final DataRow row = this.Adapter.DataTable.Rows.get(info.position);

        if (menuItem.getTitle() == "��ӡ����״̬��...") {

            final String items[] = {"����Τ��", "֥��"};

            AlertDialog dialog1 = new AlertDialog.Builder(App.Current.Workbench).setTitle("��ѡ���ӡ��")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 1) {
                                long id = row.getValue("id", Long.class);
                                String sql = "exec p_mm_up_issue_order_get_report_data ?,?";
                                Parameters p = new Parameters().add(1, id).add(2, App.Current.UserID);
                                App.Current.DbPortal.ExecuteDataSetAsync("core_and", sql, p, new ResultHandler<DataSet>() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        Result<DataSet> value = Value;
                                        if (value.HasError) {
                                            App.Current.toastError(pn_skd_pack_mgr.this.getContext(), value.Error);
                                            return;
                                        }
                                        if (value.Value != null && value.Value.Tables.size() > 0) {
                                            Intent intent = new Intent(App.Current.Workbench, ZaxiangStatusActivity.class);
                                            intent.putExtra("type", "����״̬��ǩ");

                                            DataTable dataTableHead = value.Value.Tables.get(0);  //head��

                                            if (dataTableHead.Rows.size() > 0) {
                                                String cur_date = dataTableHead.Rows.get(0).getValue("cur_date", "");
                                                String code = dataTableHead.Rows.get(0).getValue("code", "");
                                                String comment = dataTableHead.Rows.get(0).getValue("comment", "");
                                                String organization_code = dataTableHead.Rows.get(0).getValue("organization_code", "");
                                                intent.putExtra("cur_date", cur_date);
                                                intent.putExtra("code", code);
                                                intent.putExtra("comment", comment);
                                                intent.putExtra("organization_code", organization_code);

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
                                parameters.put("type", "����״̬��");

                                App.Current.Print("mm_up_issue_order", "��ӡ����״̬��", parameters);
                            }
                        }
                    }).create();
            dialog1.show();


        } else if (menuItem.getTitle() == "��ӡ�ӳ����ϵ�...") {

            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("id", String.valueOf(row.getValue("id", Long.class)));
            parameters.put("code", row.getValue("code", ""));
            parameters.put("type", "�������ϵ�");

            App.Current.Print("mm_up_issue_order", "��ӡ�ӳ����ϵ�", parameters);


        } else if (menuItem.getTitle() == "�ύ�ӳ����ϵ�...") {

            final long id = row.getValue("id", Long.class);
            final long war_id = row.getValue("warehouse_id", 0);
            final String code = row.getValue("code", "");

            String sql = "exec p_mm_up_issue_get_uncommited_items ?,?,?";
            Parameters p = new Parameters().add(1, App.Current.UserID).add(2, id).add(3, war_id);
            Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(pn_skd_pack_mgr.this.Connector, sql, p);
            if (r.HasError) {
                App.Current.toastInfo(pn_skd_pack_mgr.this.getContext(), r.Error);
            }

            if (r.Value != null && r.Value.Rows.size() > 0) {
                App.Current.toastInfo(pn_skd_pack_mgr.this.getContext(), "�������롾" + code + "������" + String.valueOf(r.Value.Rows.size()) + "����¼û�з��꣬�����ύ��");
            } else {
                this.txt_locations = new EditText(pn_skd_pack_mgr.this.getContext());
                new AlertDialog.Builder(pn_skd_pack_mgr.this.getContext())
                        .setTitle("��ɨ�财λ")
                        .setView(this.txt_locations)

                        .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String locations = txt_locations.getText().toString().trim();
                                if (locations.length() > 0) {
                                    String sql = "exec p_mm_up_issue_commit ?,?,?,?";
                                    Parameters p = new Parameters().add(1, App.Current.UserID).add(2, id).add(3, war_id).add(4, locations);
                                    App.Current.DbPortal.ExecuteNonQueryAsync(pn_skd_pack_mgr.this.Connector, sql, p, new ResultHandler<Integer>() {
                                        @Override
                                        public void handleMessage(Message msg) {
                                            Result<Integer> value = Value;
                                            if (value.HasError) {
                                                App.Current.toastInfo(pn_skd_pack_mgr.this.getContext(), value.Error);
                                            } else {
                                                if (value.Value != null && value.Value > 0) {
                                                    App.Current.toastInfo(pn_skd_pack_mgr.this.getContext(), "�ύ�ɹ�!");
                                                    pn_skd_pack_mgr.this.refresh();
                                                } else {
                                                    App.Current.toastInfo(pn_skd_pack_mgr.this.getContext(), "�ύʧ��!");
                                                }
                                                txt_locations.setText("");
                                            }
                                        }
                                    });
//							Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(pn_up_issue_mgr.this.Connector, sql, p);
//							if (r.HasError) {
//								App.Current.toastInfo(pn_up_issue_mgr.this.getContext(), r.Error);
//							}
//
//							if (r.Value != null && r.Value > 0) {
//								App.Current.toastInfo(pn_up_issue_mgr.this.getContext(), "�ύ�ɹ�!");
//								pn_up_issue_mgr.this.refresh();
//							}
//							txt_locations.setText("");
                                } else {
                                    App.Current.showInfo(pn_skd_pack_mgr.this.getContext(), "����ɨ������봢λ��");
                                }
                            }
                        }).setNegativeButton("ȡ��", null)
                        .show();
            }
        }

        return true;
    }

    @Override
    public void create() {
        if (this.Adapter.DataTable != null && this.Adapter.DataTable.Rows.size() > 0) {
            DataRow row = this.Adapter.DataTable.Rows.get(0);
            Link link = new Link("pane://x:code=mm_and_up_issue_editor");
            link.Parameters.add("order_id", row.getValue("id", Long.class));
            link.Parameters.add("war_id", row.getValue("warehouse_id", 0));
            link.Parameters.add("order_code", row.getValue("code", Long.class));
            link.Open(this, this.getContext(), null);
        } else {
            App.Current.showInfo(this.getContext(), "û������������롣");
        }
    }

    @Override
    public void openItem(DataRow row) {
        Link link = new Link("pane://x:code=mm_and_up_issue_editor");
        link.Parameters.add("order_id", row.getValue("id", Long.class));
        link.Parameters.add("war_id", row.getValue("warehouse_id", 0));
        link.Parameters.add("order_code", row.getValue("code", Long.class));
        link.Open(this, this.getContext(), null);
    }

    @Override
    public void onScan(final String barcode) {
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

        } else if (barcode.startsWith("L:")) {
            if (this.txt_locations != null) {
                String loc = barcode.substring(2, barcode.length());
                String locs = this.txt_locations.getText().toString().trim();
                if (locs.contains(loc)) {
                    return;
                }

                if (locs.length() > 0) {
                    locs += ", ";
                }

                this.txt_locations.setText(locs + loc);
            }
        }
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        DataRow row = (DataRow) Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_up_issue, null);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView) convertView.findViewById(R.id.num);
        TextView txt_code = (TextView) convertView.findViewById(R.id.txt_code);
        TextView txt_status = (TextView) convertView.findViewById(R.id.txt_status);
        TextView txt_sub_type = (TextView) convertView.findViewById(R.id.txt_sub_type);

        int issue_count = row.getValue("count_x", 0);
        if (issue_count == 0) {
            //num.setBackgroundColor(Color.parseColor("#76EE00"));
            txt_code.setBackgroundColor(Color.parseColor("#76EE00"));
            txt_status.setBackgroundColor(Color.parseColor("#76EE00"));
            txt_sub_type.setBackgroundColor(Color.parseColor("#76EE00"));
        } else {
            //num.setBackgroundColor(Color.parseColor("#FFFFF0"));
            txt_code.setBackgroundColor(Color.parseColor("#FFFFF0"));
            txt_status.setBackgroundColor(Color.parseColor("#FFFFF0"));
            txt_sub_type.setBackgroundColor(Color.parseColor("#FFFFF0"));
        }

        num.setText(String.valueOf(position + 1));
        txt_code.setText(row.getValue("org_code", "") + ", " + row.getValue("code", "") + ", " + row.getValue("create_time", ""));
        txt_status.setText(row.getValue("status", "") + ", " + row.getValue("items", ""));
        txt_sub_type.setText(row.getValue("sub_type_name", ""));
        return convertView;
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "p_mm_up_issue_get_order_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3, end).add(4, search);
        return expr;
    }
}
