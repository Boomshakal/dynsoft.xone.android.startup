package dynsoft.xone.android.wms;

import java.math.BigDecimal;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import dynsoft.xone.android.adapter.PageTableAdapter;
import dynsoft.xone.android.control.PaneFooter;
import dynsoft.xone.android.control.PaneHeader;
import dynsoft.xone.android.control.TreeViewItem;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.link.PaneLinker;
import dynsoft.xone.android.sbo.obj_mgr;

public class pn_po_shipment_mgr extends pn_mgr {

	public pn_po_shipment_mgr(Context context) {
		super(context);
	}

    @Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_po_shipment_mgr, this, true);
        view.setLayoutParams(lp);
	}
    
    @Override
    public void onPrepared() {
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
		Link link = new Link("pane://x:code=mm_and_po_receive_editor");
        link.Open(this, this.getContext(), null);
	}
    
    public void openItem(DataRow row)
    {
    	Link link = new Link("pane://x:code=mm_and_po_receive_editor");
    	link.Parameters.add("shipment_code", row.getValue("delivery_num", ""));
    	link.Parameters.add("lot_number", row.getValue("m_batch_num", ""));
    	link.Parameters.add("item_code", row.getValue("meg_pn", ""));
        link.Open(this, this.getContext(), null);
    }
    
    @Override
	public void onScan(final String barcode)
	{
    	if (barcode == null || barcode.length() == 0) {
    		return;
    	}
    	
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
    
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
    	DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_po_shipment, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_shipment_code = (TextView)convertView.findViewById(R.id.txt_shipment_code);
        TextView txt_vendor_name = (TextView)convertView.findViewById(R.id.txt_vendor_name);
        TextView txt_item_code = (TextView)convertView.findViewById(R.id.txt_item_code);
        TextView txt_item_name = (TextView)convertView.findViewById(R.id.txt_item_name);
        TextView txt_quantity = (TextView)convertView.findViewById(R.id.txt_quantity);
        
        num.setText(String.valueOf(position + 1));
        
        txt_shipment_code.setText(row.getValue("delivery_num", ""));
        txt_vendor_name.setText(row.getValue("vendor_name", ""));
        txt_item_code.setText(row.getValue("meg_pn", "") + "£¬" + row.getValue("m_batch_num", ""));
        txt_item_name.setText(row.getValue("item_name", ""));
        
        String qty = row.getValue("org_code", "") + ", " 
        	+ row.getValue("warehouse_code", "") + ", " 
        	+ row.getValue("date_code", "") + ", " 
        	+ App.formatNumber(row.getValue("open_quantity", BigDecimal.ZERO), "0.##") + "/"
        	+ App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##") + " "
        	+ row.getValue("ut", "")+ ", "+ row.getValue("status", "");
        txt_quantity.setText(qty);
        
        return convertView;
    }
    
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
    	SqlExpression expr = new SqlExpression();
    	expr.SQL = "exec p_mm_po_shipment_get_items ?,?,?,?";
		expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
}
