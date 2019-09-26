package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class pn_wo_part_online_mgr extends pn_mgr {

    public pn_wo_part_online_mgr(Context context) {
        super(context);
    }
    @Override
    public void setContentView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_wo_part_online_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onScan(final String barcode) {
        this.SearchBox.setText(barcode);
        this.Adapter.DataTable = null;
        this.Adapter.notifyDataSetChanged();
        this.refresh();
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        this.Matrix.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, final long index) {
                Log.e("len", "123454");
                if (Adapter.DataTable != null && Adapter.DataTable.Rows.size() > index) {
                    new AlertDialog.Builder(getContext()).setTitle("提示")
                            .setMessage("提交以后不能再继续上料，是否确定提交？")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    final DataRow row = (DataRow)Adapter.getItem((int)index);
                                    long id = row.getValue("id", Long.class);
                                    String sql = "update fm_wo_item_diet_arrangemen_head set feed_status = '已上料' where id = ?";
                                    Parameters p = new Parameters().add(1, id);
                                    App.Current.DbPortal.ExecuteNonQueryAsync(pn_wo_part_online_mgr.this.Connector, sql, p, new ResultHandler<Integer>(){
                                        @Override
                                        public void handleMessage(Message msg) {
                                            super.handleMessage(msg);
                                        }
                                    });
                                }
                            }).show();
                }
                return true;
            }
        });
    }


    @Override
    public void openItem(DataRow row) {
        Link link = new Link("pane://x:code=mm_and_wo_part_online_editor");
        link.Parameters.add("id", row.getValue("id", Long.class));
        link.Parameters.add("task_order_id", row.getValue("task_order_id", Long.class));
        link.Parameters.add("task_order_code", row.getValue("task_order_code", ""));
        link.Open(this, this.getContext(), null);
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        DataRow row = (DataRow) Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_wo_part_online, null);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView) convertView.findViewById(R.id.num);
        TextView txt_code = (TextView) convertView.findViewById(R.id.txt_code);
        TextView txt_status = (TextView) convertView.findViewById(R.id.txt_status);
        TextView txt_time = (TextView) convertView.findViewById(R.id.txt_time);

        num.setText(String.valueOf(position + 1));
        txt_code.setText(row.getValue("code", ""));
        txt_status.setText(row.getValue("status", ""));
        txt_time.setText(row.getValue("release_time", ""));
        return convertView;
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "exec fm_diet_arrangement_items_and ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3, end).add(4, search);
        return expr;
    }
}
