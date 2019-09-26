package dynsoft.xone.android.wms;

import java.util.List;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

/**
 * Created by Administrator on 2017/11/16.
 */

public class pn_trial_produce_record_mgr extends pn_mgr {
    public pn_trial_produce_record_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_trial_produce_record_mgr, this, true);
        view.setLayoutParams(lp);
    }

//    @Override
//    public void onScan(final String barcode) {
//        String txt = barcode;
//        if (barcode.startsWith("M:")) {
//            txt = barcode.substring(2, barcode.length());
//        }
//
//        if (barcode.startsWith("CRQ:")) {
//            String str = barcode.substring(4, barcode.length());
//            String[] arr = str.split("-");
//            if (arr.length > 0) {
//                txt = arr[0];
//            }
//        }
//
//        this.SearchBox.setText(txt);
//        this.Adapter.DataTable = null;
//        this.Adapter.notifyDataSetChanged();
//        this.refresh();
//    }
//    @Override
//    public void create() {                  //new create event
//        Link link = new Link("pane://x:code=atext_ipqc_mm_item_editor");           //link context
//        link.Open(this, getContext(), null);
//    }

    @Override
    public void openItem(DataRow row) {           //Listview Item clickevent
        super.openItem(row);
        Link link = new Link("pane://x:code=qm_trial_produce_record_editor");
        Long order_id = row.getValue("id", Long.class);
        link.Parameters.add("order_id", order_id);
        link.Parameters.add("order_code", row.getValue("code", ""));
        link.Open(this, this.getContext(), null);
    }

    @Override
    public void create() {
            Link link = new Link("pane://x:code=qm_trial_produce_record_editor");
            link.Parameters.add("order_id", -1);
            link.Parameters.add("order_code", -1);
            link.Parameters.add("code", -1);
            link.Open(this, this.getContext(), null);
//        if (this.Adapter.DataTable != null && this.Adapter.DataTable.Rows.size() > 0) {
//            DataRow row = this.Adapter.DataTable.Rows.get(0);
//            Link link = new Link("pane://x:code=qm_trial_produce_record_editor");
//            link.Parameters.add("order_id", -1);
//            link.Parameters.add("order_code", row.getValue("code", Integer.class));
//            link.Parameters.add("code", -1);
//            link.Open(this, this.getContext(), null);
//        } else {
//            App.Current.showInfo(this.getContext(), "没有上料指令。");
//        }
    }
    
    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        final DataRow row = (DataRow) Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_trial_produce_record_mgr_item, null);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView) convertView.findViewById(R.id.num);
        TextView txt_line1 = (TextView) convertView.findViewById(R.id.txt_line1);
        TextView txt_line2 = (TextView) convertView.findViewById(R.id.txt_line2);
        TextView txt_line3 = (TextView) convertView.findViewById(R.id.txt_line3);
        TextView txt_line4 = (TextView) convertView.findViewById(R.id.txt_line4);
        TextView txt_line5 = (TextView) convertView.findViewById(R.id.txt_line5);

        num.setText(String.valueOf(position + 1));
        txt_line1.setText(String.valueOf(row.getValue("task_oreder_code", "")));
        txt_line2.setText(String.valueOf(row.getValue("item_name", "")));
        txt_line3.setText(row.getValue("work_line", ""));
        txt_line4.setText(String.valueOf(row.getValue("patrol_date", "")));
//        txt_line5.setText(row.getValue("items", ""));
        return convertView;
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
    }
    
    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_fm_work_pilot_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3, end).add(4, search);
        return expr;
    }
}
