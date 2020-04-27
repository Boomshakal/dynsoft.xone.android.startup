package dynsoft.xone.android.wms;

import android.content.Context;
import android.content.SharedPreferences;
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
 * Created by Administrator on 2018/3/1.
 */

public class pn_qm_and_seat_temperature_point_check_record_parameter_mgr extends pn_mgr {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;

    public pn_qm_and_seat_temperature_point_check_record_parameter_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        super.setContentView();
        sharedPreferences = App.Current.Workbench.getSharedPreferences("ipqc_point_check", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_sop_parameter_mgr, this, true);
//        this.Icon.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                backToSop("code", "");
//            }
//        });
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
        Link link = new Link("pane://x:code=pn_qm_and_seat_temperature_point_check_editor");

        edit.putString("code", row.getValue("task_order_code", ""));
        edit.commit();
        link.Parameters.add("code", row.getValue("task_order_code", ""));
        link.Parameters.add("process_id", row.getValue("process_id", 0));
        link.Parameters.add("item_id", row.getValue("item_id", 0));
        link.Parameters.add("item_name", row.getValue("item_name", ""));
        link.Parameters.add("task_id", row.getValue("task_id", 0));
        link.Parameters.add("isNew", true);
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
