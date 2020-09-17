package dynsoft.xone.android.wms;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class pn_qm_and_blind_point_check_mgr extends pn_mgr {

    public pn_qm_and_blind_point_check_mgr(Context context) {
        super(context);
    }

    private EditText txt_split_qty;

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_and_ipqc_point_check_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
    }


    @Override
    public void openItem(DataRow row) {
        Link link = new Link("pane://x:code=pn_qm_and_blind_point_check_editor");
        link.Parameters.add("id", row.getValue("head_id", Integer.class));
        link.Parameters.add("item_code", row.getValue("item_code", Integer.class));
        link.Open(this, this.getContext(), null);
    }

    @Override
    public void create() {
        Link link = new Link("pane://x:code=pn_qm_and_blind_point_check_editor");

        link.Open(this, this.getContext(), null);
    }

    @Override
    public void onScan(final String barcode) {
//        String barcode_text=barcode.trim();
        this.SearchBox.setText(barcode);
        this.Adapter.DataTable = null;
        this.Adapter.notifyDataSetChanged();
        this.refresh();
        this.SearchBox.setText("");
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        DataRow row = (DataRow) Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_ipqc_point_check, null);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView line0 = (TextView) convertView.findViewById(R.id.txt_line0);
        TextView line1 = (TextView) convertView.findViewById(R.id.txt_line1);
        TextView line2 = (TextView) convertView.findViewById(R.id.txt_line2);
        TextView line3 = (TextView) convertView.findViewById(R.id.txt_line3);
        TextView line4 = (TextView) convertView.findViewById(R.id.txt_line4);
        TextView line5 = (TextView) convertView.findViewById(R.id.txt_line5);
        TextView line6 = (TextView) convertView.findViewById(R.id.txt_line6);

        line0.setText(String.valueOf(position + 1));
        line1.setText(row.getValue("code", ""));
        line2.setText(row.getValue("line_name", ""));
        line3.setText(row.getValue("task_code", ""));
        line4.setText(row.getValue("org_name", ""));
        String custodian = "点检人:" + row.getValue("checker", "");
        line5.setText(custodian);
        Date check_date = row.getValue("check_date", new Date());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format_date = simpleDateFormat.format(check_date);
        line6.setText("点检时间：" + format_date);
        return convertView;
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "exec get_qm_blind_point_check_mgr ?,?,?";
        expr.Parameters = new Parameters().add(1, search).add(2, start).add(3, end);
        return expr;
    }

    public void commit(DataRow row) {
    }
}
