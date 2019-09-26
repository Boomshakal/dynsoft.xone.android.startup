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

/**
 * Created by Administrator on 2017/12/15.
 */

public class pn_back_parameter extends pn_mgr {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    public pn_back_parameter(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        super.setContentView();
        LayoutParams lp = new LayoutParams(-1, -1);
        sharedPreferences = App.Current.Workbench.getSharedPreferences("sop", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();
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
        txt_line1.setText(row.getValue("code", ""));
        txt_line2.setText(row.getValue("item_code", ""));
        txt_line3.setText(row.getValue("item_name", ""));
        edit.putString("item_name", row.getValue("item_name", ""));
        edit.putString("item_code", row.getValue("item_code", ""));
        edit.commit();
//        txt_line4.setText(String.valueOf(row.getValue("creator_time", "")));
//        txt_line5.setText(row.getValue("items", ""));
        return convertView;
    }

    @Override
    public void openItem(DataRow row) {
        super.openItem(row);
        Link link = new Link("pane://x:code=fm_and_smt_back_editor");
        link.Parameters.add("code", row.getValue("code", ""));
        Log.e("LZH",row.getValue("code", ""));


        link.Parameters.add("process_id", row.getValue("process_id", 0));
        link.Parameters.add("item_id", row.getValue("item_id", 0));
        link.Parameters.add("task_id", row.getValue("task_id", 0));
        link.Parameters.add("isNew", true);
        link.Open(null, getContext(), null);
        this.close();
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_smt_back_task_order ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3, end).add(4, search);
        return expr;
    }
}

