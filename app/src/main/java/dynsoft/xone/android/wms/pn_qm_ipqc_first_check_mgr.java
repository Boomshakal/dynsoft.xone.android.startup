package dynsoft.xone.android.wms;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
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
 * Created by Administrator on 2018/9/19.
 */

public class pn_qm_ipqc_first_check_mgr extends pn_mgr {
    public pn_qm_ipqc_first_check_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_ipqc_patrol_record_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        DataRow row = (DataRow) Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ipqc_first_chec_mgr_item, null);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView) convertView.findViewById(R.id.num);
        TextView txt_line1 = (TextView) convertView.findViewById(R.id.txt_line1);
        TextView txt_line2 = (TextView) convertView.findViewById(R.id.txt_line2);
        TextView txt_line3 = (TextView) convertView.findViewById(R.id.txt_line3);
        TextView txt_line4 = (TextView) convertView.findViewById(R.id.txt_line4);

        num.setText(String.valueOf(position + 1));
        String code = row.getValue("code", "");    //确认编码
        String task_code = row.getValue("task_code", "");    //确认编码
        String segment = row.getValue("segment", "");    //确认编码
        String item_code = row.getValue("item_code", "");    //确认编码
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date esd_date = row.getValue("create_time", new Date(0));
        String format = simpleDateFormat.format(esd_date);
        txt_line1.setText("确认编码：" + code);
        txt_line2.setText("生产任务：" + task_code);
        txt_line3.setText("物料编码：" + item_code);
        if (TextUtils.isEmpty(segment)) {
            txt_line4.setText("创建时间：" + format);
        } else {
            txt_line4.setText("段别：" + segment + ",创建时间：" + format);
        }

        return convertView;
    }

    @Override
    public void openItem(DataRow row) {
        Link link = new Link("pane://x:code=pn_qm_ipqc_first_check_editor");
        link.Parameters.add("id", row.getValue("id", 0));
        link.Open(this, this.getContext(), null);
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_qm_ipqc_first_check_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3, end).add(4, search);
        return expr;
    }
}
