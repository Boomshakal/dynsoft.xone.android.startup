package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

/**
 * 售后入库
 */

public class pn_so_after_storage_mgr extends pn_mgr {

    public pn_so_after_storage_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_so_after_storage_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();

        this.Matrix.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, final long l) {
                new AlertDialog.Builder(getContext())
                        .setTitle("提示")
                        .setMessage("是否确认提交？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                DataRow row = Adapter.DataTable.Rows.get((int) l);
                                String code = row.getValue("code", "");
                                //判断是否已经都检验完成了
                                String sql = "exec fm_check_after_oqc_update_status ?";
                                Parameters p = new Parameters().add(1, code);
                                App.Current.DbPortal.ExecuteNonQueryAsync(Connector, sql, p, new ResultHandler<Integer>() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        Result<Integer> value = Value;
                                        if (value.HasError) {
                                            App.Current.showError(getContext(), value.Error);
                                            App.Current.playSound(R.raw.hook);
                                        } else {
                                            App.Current.toastInfo(getContext(), "提交成功");
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

                return true;
            }
        });
    }

    @Override
    public void openItem(DataRow row) {
        Link link = new Link("pane://x:code=pn_so_after_storage_editor");
        link.Parameters.add("code", row.getValue("code", ""));
        link.Open(this, this.getContext(), null);
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        DataRow row = (DataRow) Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_ipqc_patrol_record, null);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView) convertView.findViewById(R.id.num);
        TextView txt_line1 = (TextView) convertView.findViewById(R.id.txt_line1);
        TextView txt_line2 = (TextView) convertView.findViewById(R.id.txt_line2);
        TextView txt_line3 = (TextView) convertView.findViewById(R.id.txt_line3);
        TextView txt_line4 = (TextView) convertView.findViewById(R.id.txt_line4);

        num.setText(String.valueOf(position + 1));
        txt_line1.setText("检验单号：" + row.getValue("code", ""));
        txt_line2.setText("厂家：" + row.getValue("customer_name", ""));
        txt_line3.setText(row.getValue("count_comment", ""));
        txt_line4.setVisibility(GONE);
        return convertView;
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_qm_so_after_storage_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3, end).add(4, search);
        return expr;
    }
}
