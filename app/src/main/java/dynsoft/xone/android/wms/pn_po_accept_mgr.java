package dynsoft.xone.android.wms;

import java.math.BigDecimal;

import android.content.Context;
import android.graphics.Color;
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

public class pn_po_accept_mgr extends pn_mgr {

	public pn_po_accept_mgr(Context context) {
		super(context);
	}

	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_po_accept_mgr, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
    public void onPrepared() {
		
		//如果传递了发运单，则加载页面时按照发运单刷新数据，不要自动刷新
    	String shipment_code = this.Parameters.get("shipment_code", "");
    	if (shipment_code != null && shipment_code.length() > 0){
    		this.RefreshOnLoad = false;
    	}
    	
        super.onPrepared();
        
        if (shipment_code != null && shipment_code.length() > 0) {
        	this.SearchBox.setText(shipment_code);
        	this.refresh();
        }
    }
	
	@Override
	public void create()
	{
		Link link = new Link("pane://x:code=mm_and_po_entry_editor");
        link.Open(this, this.getContext(), null);
	}
	
	@Override
    public void openItem(DataRow row)
    {
		Link link = new Link("pane://x:code=mm_and_po_entry_editor");
		//link.Parameters.add("shipment_code", row.getValue("shipment_code", ""));
		//link.Parameters.add("item_code", row.getValue("item_code", ""));
        //link.Parameters.add("lot_number", row.getValue("lot_number", ""));
        link.Open(this, this.getContext(), null);
    }
    
	@Override
	public void onScan(final String barcode)
	{
		String txt = barcode;
    	if (barcode.startsWith("M:")) {
    		txt = barcode.substring(2, barcode.length());
    	}

    	if (barcode.startsWith("CRQ:")) {
			String str = barcode.substring(4, barcode.length());
			String[] arr = str.split("-");
			if (arr.length > 0) {
				txt = arr[0];
			}
		}
    	
    	this.SearchBox.setText(txt);
    	this.Adapter.DataTable = null;
    	this.Adapter.notifyDataSetChanged();
    	this.refresh();
    }
	
	@Override
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
    	DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_po_accept, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_iqc_code = (TextView)convertView.findViewById(R.id.txt_iqc_code);
        TextView txt_vendor_name = (TextView)convertView.findViewById(R.id.txt_vendor_name);
        TextView txt_item_code = (TextView)convertView.findViewById(R.id.txt_item_code);
        TextView txt_item_name = (TextView)convertView.findViewById(R.id.txt_item_name);
        TextView txt_quantity = (TextView)convertView.findViewById(R.id.txt_quantity);
        TextView txt_receive_location = (TextView)convertView.findViewById(R.id.txt_receive_location);
        TextView txt_receive_time = (TextView)convertView.findViewById(R.id.txt_receive_time);
        
        num.setText(String.valueOf(position + 1));
        
        int  priorityv=row.getValue("flag", 0);
 
        
        if(priorityv==0)
        {
        	txt_iqc_code.setBackgroundColor(Color.parseColor("#EE2C2C")); 
        	txt_vendor_name.setBackgroundColor(Color.parseColor("#EE2C2C")); 
        	txt_item_code.setBackgroundColor(Color.parseColor("#EE2C2C")); 
        	txt_item_name.setBackgroundColor(Color.parseColor("#EE2C2C")); 
        	txt_quantity.setBackgroundColor(Color.parseColor("#EE2C2C")); 
        	txt_receive_location.setBackgroundColor(Color.parseColor("#EE2C2C")); 
        	txt_receive_time.setBackgroundColor(Color.parseColor("#EE2C2C")); 
        }
         else
         {
        	txt_iqc_code.setBackgroundColor(Color.parseColor("#FFFFF0")); 
        	txt_vendor_name.setBackgroundColor(Color.parseColor("#FFFFF0")); 
        	txt_item_code.setBackgroundColor(Color.parseColor("#FFFFF0")); 
        	txt_item_name.setBackgroundColor(Color.parseColor("#FFFFF0")); 
        	txt_quantity.setBackgroundColor(Color.parseColor("#FFFFF0")); 
        	txt_receive_location.setBackgroundColor(Color.parseColor("#FFFFF0")); 
        	txt_receive_time.setBackgroundColor(Color.parseColor("#FFFFF0")); 
          }
        
        txt_iqc_code.setText(row.getValue("shipment_code", "") + ", " + row.getValue("iqc_code", ""));
        txt_vendor_name.setText(row.getValue("vendor_name", ""));
        txt_item_code.setText(row.getValue("item_code", "") + "，" + row.getValue("lot_number", ""));
        txt_item_name.setText(row.getValue("item_name", ""));
        
        String qty = row.getValue("warehouse_code", "") + ", " + row.getValue("date_code", "") + ", " + App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##") + " " + row.getValue("uom_code", "")+ ", " + row.getValue("status", "");
        txt_quantity.setText(qty);
        txt_receive_location.setText("接收储位: " + row.getValue("locations", ""));
        txt_receive_time.setText("接受时间: " + row.getValue("create_time", ""));
        
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
    	SqlExpression expr = new SqlExpression();
    	expr.SQL = "exec p_mm_po_accept_get_items ?,?,?,?";
		expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
}
