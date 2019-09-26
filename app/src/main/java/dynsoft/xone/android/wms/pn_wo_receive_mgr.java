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

public class pn_wo_receive_mgr extends pn_mgr {

	public pn_wo_receive_mgr(Context context) {
		super(context);
	}

	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_wo_receive_mgr, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
	public void create()
	{
		Link link = new Link("pane://x:code=mm_and_wo_receive_editor");
        link.Open(this, this.getContext(), null);
	}
	
	@Override
	public void openItem(DataRow row)
	{
		Link link = new Link("pane://x:code=mm_and_wo_receive_editor");
		link.Parameters.add("issue_order_id", row.getValue("id", 0L));
		link.Parameters.add("issue_order_code", row.getValue("code", ""));
		link.Parameters.add("task_order_code", row.getValue("task_order_code", ""));
		link.Parameters.add("work_order_code", row.getValue("work_order_code", ""));
		
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
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_wo_receive, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_issue_order_code = (TextView)convertView.findViewById(R.id.txt_issue_order_code);
        TextView txt_item_code = (TextView)convertView.findViewById(R.id.txt_item_code);
        TextView txt_item_name = (TextView)convertView.findViewById(R.id.txt_item_name);
        TextView txt_quantity = (TextView)convertView.findViewById(R.id.txt_quantity);
        TextView txt_lot_number = (TextView)convertView.findViewById(R.id.txt_lot_number);
        
        num.setText(String.valueOf(position + 1));
        txt_issue_order_code.setText(row.getValue("code", ""));
        txt_item_code.setText(row.getValue("item_code", ""));
        txt_item_name.setText(row.getValue("item_name", ""));
        txt_lot_number.setText(row.getValue("lot_number", ""));
        
        String status = App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##") + " " + row.getValue("uom_code","") + "£¬" + row.getValue("receive_status", "");
        txt_quantity.setText(status);
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
    	SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_mm_wo_receive_get_items ?,?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4,search).add(5, search);
        return expr;
    }
}
