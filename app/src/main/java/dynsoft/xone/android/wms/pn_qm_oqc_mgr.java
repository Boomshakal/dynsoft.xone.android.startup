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

public class pn_qm_oqc_mgr extends pn_mgr {

	public pn_qm_oqc_mgr(Context context) {
		super(context);
	}

	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_oqc_mgr, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
	public void create()
	{
		if (this.Adapter.DataTable != null && this.Adapter.DataTable.Rows.size() > 0) {
			//DataRow row = this.Adapter.DataTable.Rows.get(0);
			Link link = new Link("pane://x:code=qm_and_oqc_editor");
			//link.Parameters.add("id", row.getValue("head_id", ""));
	    	//link.Parameters.add("order_code", row.getValue("code", ""));
	    	//link.Parameters.add("line_id", row.getValue("line_id", ""));
	    	//link.Parameters.add('lot_number',row.getValue("line_id", ""))
	        link.Open(this, this.getContext(), null);
		} else {
			App.Current.showInfo(this.getContext(), "√ª”–Ω…ø‚…Í«Î°£");
		}
	}
	
	@Override
	public void openItem(DataRow row)
    {
    	Link link = new Link("pane://x:code=qm_and_oqc_editor");
    	link.Parameters.add("order_id", row.getValue("head_id", 0L));
    	link.Parameters.add("order_code", row.getValue("code", ""));
    	//link.Parameters.add("line_id", row.getValue("line_id", 0));
    	link.Parameters.add("lot_number", row.getValue("lot_number", ""));;
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
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_wo_entry_oqc, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_code = (TextView)convertView.findViewById(R.id.txt_code);
        TextView txt_status = (TextView)convertView.findViewById(R.id.txt_status);
        TextView txt_line1 = (TextView)convertView.findViewById(R.id.txt_line1);
        TextView txt_line2 = (TextView)convertView.findViewById(R.id.txt_line2);
        num.setText(String.valueOf(position + 1));
        txt_code.setText(row.getValue("org_code", "") + ", " + row.getValue("code", "") + ", " + row.getValue("item_code", ""));
        txt_status.setText(row.getValue("locations", "") + ", " + row.getValue("lot_number", ""));
        txt_line1.setText(row.getValue("item_name", "") );
        txt_line2.setText(App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##")+row.getValue("uom_code", ""));
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
    	SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_qm_oqc_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
}
