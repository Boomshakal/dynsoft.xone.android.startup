package dynsoft.xone.android.wms;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

/**
 * Created by Administrator on 2018/7/6.
 */

public class pn_qm_device_daily_check_mgr extends pn_mgr {
    public pn_qm_device_daily_check_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_device_daily_check_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        DataRow row = (DataRow) Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.device_daily_check_mgr_item, null);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView) convertView.findViewById(R.id.num);
        TextView txt_line1 = (TextView) convertView.findViewById(R.id.txt_line1);
        TextView txt_line2 = (TextView) convertView.findViewById(R.id.txt_line2);
        TextView txt_line3 = (TextView) convertView.findViewById(R.id.txt_line3);
        TextView txt_line4 = (TextView) convertView.findViewById(R.id.txt_line4);

        num.setText(String.valueOf(position + 1));
        String device_code = row.getValue("device_code", "");
        String device_name = row.getValue("device_name", "");
        String work_line = row.getValue("work_line", "");
        String respond_by = row.getValue("respond_by", "");
        Date date = new Date(0);
        Date create_date = row.getValue("create_date", date);
        txt_line1.setText("设备编码：" + device_code);
        txt_line2.setText("设备名称：" + device_name);
        txt_line3.setText("线别：" + work_line);
        Log.e("len", create_date + "**" + (create_date == date) + "**" + date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format1 = simpleDateFormat.format(create_date);
        String format2 = simpleDateFormat.format(date);
        if (format1 != format2) {
            String format = simpleDateFormat.format(create_date);
            txt_line4.setText(respond_by + "," + format);
        } else {
            txt_line4.setText(respond_by);
        }

        return convertView;
    }

    @Override
    public void create() {
        super.create();
        Link link = new Link("pane://x:code=qm_device_daily_check_mgr_editor");
        link.Parameters.add("isNew", true);
        link.Open(this, this.getContext(), null);
    }

    @Override
    public void openItem(DataRow row) {
        Link link = new Link("pane://x:code=qm_device_daily_check_mgr_editor");
        link.Parameters.add("id", row.getValue("id", 0L));
        link.Parameters.add("device_code", row.getValue("device_code"));
        link.Parameters.add("device_name", row.getValue("device_name"));
        link.Parameters.add("work_line", row.getValue("work_line"));
        link.Parameters.add("ipqc_code", row.getValue("ipqc_code"));
        link.Parameters.add("create_date", row.getValue("create_date"));
        link.Parameters.add("responsible", row.getValue("respond_by"));
        link.Parameters.add("isNew", false);
        link.Open(this, this.getContext(), null);
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_qm_device_daily_check_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3, end).add(4, search);
        return expr;
    }
}
