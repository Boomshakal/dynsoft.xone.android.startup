package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.blueprint.Demo_ad_escActivity;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class pn_so_after_delivery_and_main extends pn_mgr {

    public pn_so_after_delivery_and_main(Context context) {
        super(context);
    }

    private EditText txt_split_qty;

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_so_after_sales_batch_show1, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void openItem(DataRow row) {
        if (row.getValue("sta","").equals("正常发货")){
            Link link = new Link("pane://x:code=pn_so_after_sales_delivery_and");
            link.Parameters.add("code", row.getValue("code", "")).add("customer_name",row.getValue("customer_name", ""));
            link.Open(this, this.getContext(), null);
        }
        else if (row.getValue("sta","").equals("销货")){
            Link link = new Link("pane://x:code=pn_so_after_sales_delivery_and_sales");
            link.Parameters.add("code", row.getValue("code", "")).add("customer_name",row.getValue("customer_name", ""));
            link.Open(this, this.getContext(), null);

        }

    }

    @Override
    public void onScan(final String barcode) {
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
            convertView = View.inflate(getContext(), R.layout.so_after_batch, null);
            //App.Current.Workbench.getLayoutInflater().inflate(R.layout.so_after_batch, null);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }
        /*
        TextView num = (TextView) convertView.findViewById(R.id.num);
        TextView customer_name = (TextView) convertView.findViewById(R.id.txt_customer_name_cell);
        TextView code = (TextView) convertView.findViewById(R.id.txt_code_cell);
        TextView quantity = (TextView) convertView.findViewById(R.id.txt_quantity_cell);
        customer_name.setText(row.getValue("customer_name", ""));
        code.setText(row.getValue("code", ""));
        quantity.setText(row.getValue("quantity", ""));
        return convertView;*/
        TextView num = (TextView) convertView.findViewById(R.id.num);
        TextView txt_line1 = (TextView) convertView.findViewById(R.id.txt_line1);
        TextView txt_line2 = (TextView) convertView.findViewById(R.id.txt_line2);
        TextView txt_line3 = (TextView) convertView.findViewById(R.id.txt_line3);
        num.setText(String.valueOf(position + 1));
        txt_line1.setText(String.valueOf("客户名称："+row.getValue("customer_name", "")+"状态："+row.getValue("sta", "")));
        txt_line2.setText(String.valueOf("单号："+row.getValue("code", "")));
        txt_line3.setText(String.valueOf("共"+row.getValue("quantity", 0)+"条/已发"+row.getValue("close_quantity", 0)+"条"));
        return convertView;
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "exec mm_so_after_batch_search ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3, end).add(4, search);
        return expr;
    }

}
