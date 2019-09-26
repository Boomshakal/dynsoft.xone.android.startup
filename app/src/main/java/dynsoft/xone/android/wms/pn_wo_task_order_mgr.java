package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class pn_wo_task_order_mgr extends pn_mgr {

	public pn_wo_task_order_mgr(Context context) {
		super(context);
	}

	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_wo_task_order_mgr, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
    public void openItem(DataRow row)
    {
		Link link = new Link("pane://x:code=mm_and_wo_task_order_editor");
		link.Parameters.add("order_id", row.getValue("id", Long.class));
        link.Parameters.add("order_code", row.getValue("code", ""));
        link.Open(this, this.getContext(), null);
    }
	
	@Override
	public void onScan(final String barcode)
	{
		String txt = barcode;
		if (barcode.startsWith("CRQ:")) {
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
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_wo_task_order, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_wo_issue_order_code = (TextView)convertView.findViewById(R.id.txt_wo_issue_order_code);
        TextView txt_create_time = (TextView)convertView.findViewById(R.id.txt_create_time);
        TextView txt_status = (TextView)convertView.findViewById(R.id.txt_status);
        TextView txt_items = (TextView)convertView.findViewById(R.id.txt_items);
        
        num.setText(String.valueOf(position + 1));
        txt_wo_issue_order_code.setText(row.getValue("code", ""));
        txt_create_time.setText(row.getValue("create_time", ""));
        txt_status.setText("”≈œ»º∂: " + row.getValue("priority", 0).toString() + ", ◊¥Ã¨: " + row.getValue("status", ""));
        txt_items.setText(row.getValue("items", ""));
        
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
		SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_mm_wo_task_order_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
}
