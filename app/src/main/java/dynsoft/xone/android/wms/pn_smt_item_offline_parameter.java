package dynsoft.xone.android.wms;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import dynsoft.xone.android.control.PaneHeader;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class pn_smt_item_offline_parameter extends pn_mgr {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    public pn_smt_item_offline_parameter(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        super.setContentView();
        LayoutParams lp = new LayoutParams(-1, -1);
        sharedPreferences = App.Current.Workbench.getSharedPreferences("sop", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_sop_parameter_mgr, this, true);

        view.setLayoutParams(lp);
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        final DataRow row = (DataRow) Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_sop_parameter_item, null);

        }


        TextView txt_line1 = (TextView) convertView.findViewById(R.id.txt_line1);
        TextView txt_line2 = (TextView) convertView.findViewById(R.id.txt_line2);
        TextView txt_line3 = (TextView) convertView.findViewById(R.id.txt_line3);


        txt_line1.setText(row.getValue("task_order_code", ""));
        txt_line2.setText(row.getValue("item_code", ""));
        txt_line3.setText(row.getValue("item_name", ""));
        edit.putString("item_name", row.getValue("item_name", ""));
        edit.putString("item_code", row.getValue("item_code", ""));
        edit.commit();

        return convertView;
    }

    @Override
    public void openItem(DataRow row) {
        super.openItem(row);
        Link link = new Link("pane://x:code=fm_smt_item_offline_mgr");
        link.Parameters.add("task_order", row.getValue("task_order_code", ""));
        link.Parameters.add("item_code", row.getValue("item_code", ""));
        link.Parameters.add("item_name", row.getValue("item_name", ""));
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
