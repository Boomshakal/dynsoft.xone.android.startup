package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
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

public class pn_mm_handover_exception_close_mgr extends pn_mgr {

	public pn_mm_handover_exception_close_mgr(Context context) {
		super(context);
	}
	
	@Override
    public void setContentView()
	{
		LayoutParams lp = new LayoutParams(-1,-1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_mm_handover_list_mgr, this, true);
        view.setLayoutParams(lp);
	}
	@Override
    public void onPrepared() {
        super.onPrepared();
        
	}
	
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
        // TODO Auto-generated method stub  
        super.onActivityResult(requestCode, resultCode, data);  
    	this.refresh();
    }
    
	@Override
    public void openItem(DataRow row)
    {
		Link link = new Link("pane://x:code=mm_and_handover_exception_close_editor");
        link.Parameters.add("id", row.getValue("id",0L));
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
	    	
		} else if (barcode.startsWith("L:")){
			String loc = barcode.substring(2, barcode.length());
			this.SearchBox.setText(loc);
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
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_mm_handover_list, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView line1 = (TextView)convertView.findViewById(R.id.txt_line1);
        TextView line2 = (TextView)convertView.findViewById(R.id.txt_line2);
        TextView line3 = (TextView)convertView.findViewById(R.id.txt_line3);
        TextView line4 = (TextView)convertView.findViewById(R.id.txt_line4);
        
        num.setText(String.valueOf(position + 1));
        line1.setText(row.getValue("code", "")+",发起人:"+row.getValue("except_user", "")+"," + row.getValue("org_code", "") +",确认日期:"+row.getValue("reply_time",""));
        line2.setText("责任人："+row.getValue("user_operation_name", "")+","+row.getValue("exception_type", "")+","+row.getValue("exception_comment",""));
        line3.setText(row.getValue("item_code", "")+","+row.getValue("item_name", ""));
        line4.setText("实发数量："+App.formatNumber(row.getValue("closed_quantity",BigDecimal.ZERO), "0.##")+"实点数量:"+App.formatNumber(row.getValue("oepn_qty",BigDecimal.ZERO), "0.##")+","+row.getValue("reply_comment",""));
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
		SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_mm_exception_close_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
}
