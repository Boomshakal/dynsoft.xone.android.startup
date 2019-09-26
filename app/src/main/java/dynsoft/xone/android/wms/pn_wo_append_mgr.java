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
import dynsoft.xone.android.link.PaneLinker;

public class pn_wo_append_mgr extends pn_mgr {

	public pn_wo_append_mgr(Context context) {
		super(context);
	}

	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_wo_append_mgr, this, true);
        view.setLayoutParams(lp);
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
	public void create()
	{
		if (this.Adapter.DataTable != null && this.Adapter.DataTable.Rows.size() > 0) {
			DataRow row = this.Adapter.DataTable.Rows.get(0);
			Link link = new Link("pane://x:code=mm_and_wo_append_editor");
			link.Parameters.add("line_id", row.getValue("line_id", Long.class));
	        link.Parameters.add("code", row.getValue("code", ""));
	        link.Open(this, this.getContext(), null);
		} else {
			App.Current.showInfo(this.getContext(), "没有补料指令。");
		}
	}
	
	@Override
    public void openItem(DataRow row)
    {
		Link link = new Link("pane://x:code=mm_and_wo_append_editor");
		link.Parameters.add("line_id", row.getValue("line_id", Integer.class));
        link.Parameters.add("code", row.getValue("code", ""));
        link.Open(this, this.getContext(), null);
    }
	
	@Override
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
        DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_wo_append, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_append_order_code = (TextView)convertView.findViewById(R.id.txt_append_order_code);
        TextView txt_task_order_code = (TextView)convertView.findViewById(R.id.txt_task_order_code);
        TextView txt_item_code = (TextView)convertView.findViewById(R.id.txt_item_code);
        TextView txt_item_name = (TextView)convertView.findViewById(R.id.txt_item_name);
        TextView txt_vendor_name = (TextView)convertView.findViewById(R.id.txt_vendor_name);
        TextView txt_quantity = (TextView)convertView.findViewById(R.id.txt_quantity);
        TextView txt_location = (TextView)convertView.findViewById(R.id.txt_location);
        
        num.setText(String.valueOf(position + 1));
        txt_append_order_code.setText(row.getValue("code", ""));
        txt_task_order_code.setText(row.getValue("task_order_code", ""));
        txt_item_code.setText(row.getValue("item_code", ""));
        txt_item_name.setText(row.getValue("item_name", ""));
        
        String vendor_name = row.getValue("vendor_name", "");
        if (vendor_name == null || vendor_name.length() == 0) {
        	txt_vendor_name.setVisibility(View.GONE);
        	txt_vendor_name.setText(vendor_name);
        }
        
        String date_code = row.getValue("date_code", "");
        String qty = App.formatNumber(row.getValue("open_quantity", BigDecimal.ZERO), "0.##") + " " + row.getValue("uom_code", "") + "，" + row.getValue("status", "");
        if (date_code != null && date_code.length() > 0) {
        	qty = date_code + "，" + qty;
        }
        
        txt_quantity.setText(qty);
        txt_location.setText("默认储位：" + row.getValue("location_code", ""));
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
    	SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_mm_wo_append_get_order_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, search).add(2, search).add(3, start).add(4,end);
        return expr;
    }
}
