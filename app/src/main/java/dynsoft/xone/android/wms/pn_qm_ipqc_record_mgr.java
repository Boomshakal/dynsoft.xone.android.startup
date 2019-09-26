package dynsoft.xone.android.wms;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

/**
 * Created by Administrator on 2017/12/2.
 */

public class pn_qm_ipqc_record_mgr extends pn_mgr {
    private boolean isNew;

    public pn_qm_ipqc_record_mgr(Context context) {
        super(context);
    }
    @Override
    public void setContentView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_ipqc_check_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        DataRow row = (DataRow) Adapter.getItem(position);
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.ln_ipqc_check_item, null);
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
        txt_line1.setText(String.valueOf(row.getValue("code", "")));
        txt_line2.setText(String.valueOf(row.getValue("task_order_code", "")));
        txt_line3.setText(String.valueOf(row.getValue("item_code", "")));
        txt_line4.setText(String.valueOf(row.getValue("item_name", "")));
        txt_line5.setText(row.getValue("work_line", "") + "," + row.getValue("time_quantum", ""));
        return convertView;
    }

    @Override
    public void openItem(DataRow row) {           //Listview Item clickevent
        super.openItem(row);
        Link link = new Link("pane://x:code=qm_ipqc_record_mgr_editor");
        link.Parameters.add("order_id", row.getValue("id", Long.class));
        link.Parameters.add("order_code", row.getValue("code", ""));
        link.Parameters.add("task_order_code", row.getValue("task_order_code", ""));
        link.Parameters.add("item_code", row.getValue("item_code", ""));
        link.Parameters.add("item_name", row.getValue("item_name", ""));
        link.Parameters.add("work_line", row.getValue("work_line", ""));
        link.Parameters.add("task_id", row.getValue("task_id", 0));
        link.Parameters.add("plan_quantity", row.getValue("plan_quantity", new BigDecimal(0)));

        if (isNew) {
            link.Parameters.add("task_id", row.getValue("task_id", 0));
            link.Parameters.add("item_id", row.getValue("item_id", 0));
            link.Parameters.add("organization_id", row.getValue("organization_id", 0));
        }
        link.Open(this, this.getContext(), null);
    }

    @Override
    public void create() {
        super.create();
        isNew = true;
        Adapter.DataTable = null;
        Adapter.notifyDataSetChanged();
        SearchBox.setText("");
        SearchBox.isFocused();
        InputMethodManager imm = (InputMethodManager) App.Current.Workbench.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        String txt = SearchBox.getText().toString().trim();
        if (txt.length() > 0) {
            SearchBox.setText(txt);
            Adapter.PageSize = 10;
            Adapter.DataTable = null;
            Adapter.notifyDataSetChanged();
            refresh();
        }
    }

    @Override
    public void onScan(final String barcode) {
        String txt = barcode;
        this.SearchBox.setText(txt);
        this.Adapter.DataTable = null;
        this.Adapter.notifyDataSetChanged();
        this.refresh();
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        if (isNew) {
            expr.SQL = "exec p_qm_ipqc_record_get_items_new ?,?,?,?";
        } else {
            expr.SQL = "exec p_qm_ipqc_record_get_items ?,?,?,?";
        }
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3, end).add(4, search);
        return expr;
    }
}
