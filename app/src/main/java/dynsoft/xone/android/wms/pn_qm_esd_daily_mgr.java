package dynsoft.xone.android.wms;

import android.content.Context;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

/**
 * Created by Administrator on 2018/6/4.
 */

public class pn_qm_esd_daily_mgr extends pn_mgr {
    private ListView listView;
    private View view;

    public pn_qm_esd_daily_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
        view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_esd_daily_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        DataRow row = (DataRow) Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.esd_daily_mgr_item, null);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView) convertView.findViewById(R.id.num);
        TextView txt_line1 = (TextView) convertView.findViewById(R.id.txt_line1);
        TextView txt_line2 = (TextView) convertView.findViewById(R.id.txt_line2);
        TextView txt_line3 = (TextView) convertView.findViewById(R.id.txt_line3);

        num.setText(String.valueOf(position + 1));
        String work_line = row.getValue("work_line", "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date esd_date = row.getValue("esd_date", new Date(0));
        String format = simpleDateFormat.format(esd_date);
        String status = row.getValue("status", "");
        txt_line1.setText("线别：" + work_line);
        txt_line2.setText("状态：" + status);
        txt_line3.setText("日期：" + format);

        return convertView;
    }

    @Override
    public void openItem(DataRow row)
    {
        Link link = new Link("pane://x:code=qm_esd_daily_mgr_editor");
        link.Parameters.add("head_id", row.getValue("id", Long.class));
        Date esd_date = row.getValue("esd_date", new Date(SystemClock.currentThreadTimeMillis()));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(esd_date);
        link.Parameters.add("esd_date", format);
        link.Parameters.add("work_line", row.getValue("work_line", ""));
        link.Parameters.add("ipqc_code", row.getValue("ipqc_code", ""));
        link.Parameters.add("line_man", row.getValue("line_man", ""));
        link.Parameters.add("line_man_code", row.getValue("line_man_code", ""));
        link.Parameters.add("department", row.getValue("org_name", ""));
        link.Open(this, this.getContext(), null);
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_qm_esd_check_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3, end).add(4, search);
        return expr;
    }
}
