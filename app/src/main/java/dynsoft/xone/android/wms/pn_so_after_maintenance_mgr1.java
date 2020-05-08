package dynsoft.xone.android.wms;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class pn_so_after_maintenance_mgr1 extends pn_mgr {

	public pn_so_after_maintenance_mgr1(Context context) {
		super(context);
	}

	@Override
    public void setContentView()
	{
		LayoutParams lp = new LayoutParams(-1,-1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_so_after_maintenance_mgr, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
    public void openItem(DataRow row)
    {
		Link link = new Link("pane://x:code=pn_so_after_maintenance_editor");
		//link.Parameters.add("order_id", row.getValue("id", Long.class));
        link.Parameters.add("code", row.getValue("code", ""));
        link.Parameters.add("logistics_code", row.getValue("logistics_code", ""));
        link.Parameters.add("sh_date", row.getValue("sh_date", ""));
        link.Parameters.add("customer_name", row.getValue("customer_name", ""));
        link.Parameters.add("qty", row.getValue("qty", ""));
        link.Open(this, this.getContext(), null);
    }
	
	@Override
	public void onScan(final String barcode)
	{
		String txt = barcode;
		if (barcode.startsWith("A")) {
			String str = barcode.substring(4, barcode.length());
			String[] arr = str.split("-");
			if (arr.length > 0) {
				txt = arr[0];
			}
			
			this.SearchBox.setText(txt);
	    	this.Adapter.DataTable = null;
	    	this.Adapter.notifyDataSetChanged();
	    	this.refresh();
	    	
		}
    }
	
	@Override
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
    	DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_maintenance, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_head_code = (TextView)convertView.findViewById(R.id.txt_head_code);
        TextView txt_logistics_code = (TextView)convertView.findViewById(R.id.txt_logistics_code);
        TextView txt_sh_date = (TextView)convertView.findViewById(R.id.txt_sh_date);
        TextView txt_customer_name = (TextView)convertView.findViewById(R.id.txt_customer_name);
        TextView txt_qty = (TextView)convertView.findViewById(R.id.txt_qty);
        
        num.setText(String.valueOf(position + 1));
        txt_head_code.setText(row.getValue("code", ""));
        txt_logistics_code.setText(row.getValue("logistics_code", ""));
        txt_sh_date.setText(row.getValue("sh_date", ""));
        txt_customer_name.setText(row.getValue("customer_name", ""));
        txt_qty.setText(row.getValue("qty", 0).toString());
        
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
		SqlExpression expr = new SqlExpression();
        expr.SQL = "exec pn_so_after_maintenance_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
}
