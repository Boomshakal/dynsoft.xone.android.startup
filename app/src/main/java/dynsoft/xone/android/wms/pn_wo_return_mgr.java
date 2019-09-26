package dynsoft.xone.android.wms;

import java.math.BigDecimal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import dynsoft.xone.android.link.PaneLinker;

public class pn_wo_return_mgr extends pn_mgr {

	public pn_wo_return_mgr(Context context) {
		super(context);
	}

	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_wo_return_mgr, this, true);
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
    public void onPrepared() {
        super.onPrepared();
        this.Matrix.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long index) {
				if (Adapter.DataTable != null && Adapter.DataTable.Rows.size() > index) {
					final DataRow row = (DataRow)Adapter.getItem((int)index);
					String code = row.getValue("code", "");
			        Link link = new Link("pane://x:code=mm_and_wo_return_dtl_mgr");
				    link.Parameters.add("code", code);
				    link.Open(pn_wo_return_mgr.this, pn_wo_return_mgr.this.getContext(), null);
				}
				
				return false;
			}
		});
	}
	
	
	@Override
	public void create()
	{
		if (this.Adapter.DataTable != null && this.Adapter.DataTable.Rows.size() > 0) {
			DataRow row = this.Adapter.DataTable.Rows.get(0);
			Link link = new Link("pane://x:code=mm_and_wo_return_editor");
			link.Parameters.add("order_id", row.getValue("id", Long.class));
			link.Parameters.add("order_code", row.getValue("code", Integer.class));
	        link.Open(this, this.getContext(), null);
		} else {
			App.Current.showInfo(this.getContext(), "没有退料指令。");
		}
	}
	
	@Override
    public void openItem(DataRow row)
    {
		Link link = new Link("pane://x:code=mm_and_wo_return_editor");
		link.Parameters.add("order_id", row.getValue("id", Long.class));
		link.Parameters.add("order_code", row.getValue("code", Integer.class));
        link.Open(this, this.getContext(), null);
    }
	
	@Override
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
    	DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_wo_return, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_code = (TextView)convertView.findViewById(R.id.txt_code);
        TextView txt_status = (TextView)convertView.findViewById(R.id.txt_status);
        
        num.setText(String.valueOf(position + 1));
        txt_code.setText(row.getValue("org_code", "") + ", " + row.getValue("code", "") + ", " + row.getValue("create_time", ""));
        txt_status.setText(row.getValue("status", "") + ", " + row.getValue("items", ""));
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
    	SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_mm_wo_return_get_order_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
	
}
