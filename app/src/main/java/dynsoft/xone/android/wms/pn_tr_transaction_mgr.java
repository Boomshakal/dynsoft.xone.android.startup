package dynsoft.xone.android.wms;

import java.math.BigDecimal;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class pn_tr_transaction_mgr extends pn_mgr {

	public pn_tr_transaction_mgr(Context context) {
		super(context);
	}
	
	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_tr_issue_mgr, this, true);
        view.setLayoutParams(lp);
	}

	@Override
	public void create()
	{
		DataRow row = null;
		if (this.Adapter.DataTable != null && this.Adapter.DataTable.Rows.size() > 0) {
			row = this.Adapter.DataTable.Rows.get(0);
			Link link = new Link("pane://x:code=mm_and_tr_issue_editor");
			link.Parameters.add("order_id", row.getValue("id", ""));
			link.Parameters.add("order_code", row.getValue("code", ""));
	    	link.Parameters.add("line_id", row.getValue("line_id", ""));
	        link.Open(this, this.getContext(), null);
		} else {
			App.Current.showInfo(this.getContext(), "没有转出申请。");
		}
	}

	@Override
	public void openItem(DataRow row)
	{
		if (row == null) return;

		Link link = new Link("pane://x:code=mm_and_tr_issue_editor");
		link.Parameters.add("order_id", row.getValue("id", ""));
		link.Parameters.add("order_code", row.getValue("code", ""));
    	link.Parameters.add("line_id", row.getValue("line_id", ""));
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
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_tr_transaction, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_tr_order_code = (TextView)convertView.findViewById(R.id.txt_tr_order_code);
        TextView txt_item_code = (TextView)convertView.findViewById(R.id.txt_item_code);
        TextView txt_item_name = (TextView)convertView.findViewById(R.id.txt_item_name);
        TextView txt_vendor_name = (TextView)convertView.findViewById(R.id.txt_vendor_name);
        TextView txt_quantity = (TextView)convertView.findViewById(R.id.txt_quantity);
        TextView txt_warehouse = (TextView)convertView.findViewById(R.id.txt_warehouse);
        TextView txt_comment = (TextView)convertView.findViewById(R.id.txt_comment);
        
        num.setText(String.valueOf(position + 1));
        txt_tr_order_code.setText(row.getValue("code", ""));
        
        String item_code = row.getValue("from_item_code", "");
        String date_code = row.getValue("date_code", "");
        if (date_code != null && date_code.length() > 0) {
        	item_code = date_code + "，" + item_code;
        }
        
        txt_item_code.setText(item_code + "，" + row.getValue("status", ""));
        txt_item_name.setText(row.getValue("from_item_name", ""));
        
        String vendor_name = row.getValue("vendor_name", "");
        if (vendor_name == null || vendor_name.length() == 0) {
        	txt_vendor_name.setVisibility(View.GONE);
        	txt_vendor_name.setText("");
        } else {
        	txt_vendor_name.setText(vendor_name);
        	txt_comment.setVisibility(View.VISIBLE);
        }
        
        String qty = String.format("待转出: %s %s，待转入: %s %s", 
        		App.formatNumber(row.getValue("open_issue_quantity", BigDecimal.ZERO), "0.##"),
        		row.getValue("uom_code", ""),
        		App.formatNumber(row.getValue("open_entry_quantity", BigDecimal.ZERO), "0.##"),
        		row.getValue("uom_code", ""));
        
        txt_quantity.setText(qty);
        
        String warehouse = String.format("从 %s.%s 到  %s.%s", row.getValue("from_organization_code", ""), row.getValue("from_warehouse_code", ""), row.getValue("to_organization_code", ""), row.getValue("to_warehouse_code", ""));
        txt_warehouse.setText(warehouse);
        
        String comment = row.getValue("comment", "");
        if (comment == null || comment.length() == 0) {
        	txt_comment.setVisibility(View.GONE);
        	txt_comment.setText("");
        } else {
        	txt_comment.setVisibility(View.VISIBLE);
        	txt_comment.setText(comment);
        }
        
//        String status = row.getValue("status","");
//        if (status.equals("待转出")) {
//        	txt_tr_order_code.setTextColor(Color.BLUE);
//        	txt_item_code.setTextColor(Color.BLUE);
//        	txt_item_name.setTextColor(Color.BLUE);
//        	txt_vendor_name.setTextColor(Color.BLUE);
//        	txt_quantity.setTextColor(Color.BLUE);
//        	txt_warehouse.setTextColor(Color.BLUE);
//        	txt_comment.setTextColor(Color.BLUE);
//        } else if (status.equals("待转入")){
//        	txt_tr_order_code.setTextColor(Color.RED);
//        	txt_item_code.setTextColor(Color.RED);
//        	txt_item_name.setTextColor(Color.RED);
//        	txt_vendor_name.setTextColor(Color.RED);
//        	txt_quantity.setTextColor(Color.RED);
//        	txt_warehouse.setTextColor(Color.RED);
//        	txt_comment.setTextColor(Color.RED);
//        }

        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
    	SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_mm_tr_issue_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
	
}
