package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class pn_so_return_mgr extends pn_mgr {

	public pn_so_return_mgr(Context context) {
		super(context);
	}

	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_so_return_mgr, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
    public void openItem(DataRow row)
    {
		Link link = new Link("pane://x:code=mm_and_so_return_editor");
		link.Parameters.add("order_id", row.getValue("id", 0L));
		link.Parameters.add("order_code", row.getValue("code", ""));
        link.Open(this, this.getContext(), null);
    }
	
	@Override
	public void onScan(final String barcode)
	{
    	this.SearchBox.setText(barcode);
    	this.Adapter.DataTable = null;
    	this.Adapter.notifyDataSetChanged();
    	this.refresh();
    }
	
	@Override
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
    	DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_so_issue, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_order_code = (TextView)convertView.findViewById(R.id.txt_order_code);
        TextView txt_customer_name = (TextView)convertView.findViewById(R.id.txt_customer_name);
        TextView txt_items = (TextView)convertView.findViewById(R.id.txt_items);
        TextView txt_comment = (TextView)convertView.findViewById(R.id.txt_comment);

        num.setText(String.valueOf(position + 1));
        txt_order_code.setText(row.getValue("org_code", "") + ", " + row.getValue("code", "") + ", " + App.formatDateTime(row.getValue("create_time"), "yyyy-MM-dd"));
        txt_customer_name.setText(row.getValue("customer_name", ""));
        txt_items.setText(row.getValue("status", "") + ", " + row.getValue("items", ""));
        txt_comment.setText(row.getValue("comment", ""));
        
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
		SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_mm_so_return_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
}
