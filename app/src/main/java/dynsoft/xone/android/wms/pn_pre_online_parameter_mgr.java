package dynsoft.xone.android.wms;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

/**
 * Created by Administrator on 2017/12/15.
 */

public class pn_pre_online_parameter_mgr extends pn_mgr {
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor edit;
    public pn_pre_online_parameter_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        super.setContentView();
        LayoutParams lp = new LayoutParams(-1, -1);
        sharedPreferences = App.Current.Workbench.getSharedPreferences("pre", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_sop_parameter_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        final DataRow row = (DataRow) Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_sop_parameter_item, null);
//            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
//            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

//        TextView num = (TextView) convertView.findViewById(R.id.num);
        TextView txt_line1 = (TextView) convertView.findViewById(R.id.txt_line1);
        TextView txt_line2 = (TextView) convertView.findViewById(R.id.txt_line2);
        TextView txt_line3 = (TextView) convertView.findViewById(R.id.txt_line3);

//        num.setText(String.valueOf(position + 1));
        txt_line1.setText(row.getValue("task_order_code", ""));
        txt_line2.setText(row.getValue("item_code", ""));
        txt_line3.setText(row.getValue("item_name", ""));
//        txt_line4.setText(String.valueOf(row.getValue("creator_time", "")));
//        txt_line5.setText(row.getValue("items", ""));
        return convertView;
    }

    @Override
    public void openItem(DataRow row) {
        super.openItem(row);
        Link link = new Link("pane://x:code=pre_work_online_mgr");
        link.Parameters.add("code", row.getValue("task_order_code", ""));
        link.Parameters.add("isNew", true);
        edit.putString("task_order_code", row.getValue("task_order_code", ""));
        edit.putString("item_code", row.getValue("item_code", ""));
        edit.putString("item_name", row.getValue("item_name", ""));
        edit.putLong("task_order_id", row.getValue("task_id", 0L));
        edit.commit();
        link.Open(null, getContext(), null);
        this.close();
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_qm_sop_task_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3, end).add(4, search);
        return expr;
    }
}
