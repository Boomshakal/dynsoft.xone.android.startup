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

public class pn_stock_item_mgr extends pn_mgr {

	public pn_stock_item_mgr(Context context) {
		super(context);
	}

	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_stock_item_mgr, this, true);
        view.setLayoutParams(lp);
	}

	@Override
    public void onPrepared() {
        super.onPrepared();
        
	}

	@Override
    public void openItem(DataRow row)
    {
	    Link link = new Link("pane://x:code=mm_and_stock_lot_for_item");
	    int item_id =row.getValue("item_id",0);
    	link.Parameters.add("item_id", item_id);
    	link.Parameters.add("warehouse_id", row.getValue("warehouse_id",0));
        link.Open(pn_stock_item_mgr.this, pn_stock_item_mgr.this.getContext(), null);
    }
    
	@Override
	public void onScan(final String barcode)
	{
		String txt = barcode;
    	if (barcode.startsWith("CRQ:")) {
			String str = barcode.substring(4, barcode.length());
			String[] arr = str.split("-");
			if (arr.length > 0) {
				txt = arr[1];
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
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_mm_stock_item_mgr, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_line1 = (TextView)convertView.findViewById(R.id.txt_line1);
        TextView txt_line2 = (TextView)convertView.findViewById(R.id.txt_line2);
        TextView txt_line3 = (TextView)convertView.findViewById(R.id.txt_line3);
        TextView txt_line4 = (TextView)convertView.findViewById(R.id.txt_line4);

        num.setText(String.valueOf(position + 1));
        
        txt_line1.setText("储位："+row.getValue("locations",""));
        txt_line2.setText("料号："+row.getValue("item_code", "")+";库位："+row.getValue("war_code", "")+","+row.getValue("org_code",""));
        txt_line3.setText(row.getValue("item_name",""));
        txt_line4.setText("库存数量："+App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##"));
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
    	SqlExpression expr = new SqlExpression();
    	expr.SQL = "exec p_mm_get_stock_item ?,?,?";
		expr.Parameters = new Parameters().add(1,start).add(2, end).add(3, search);
		return expr;

    }
}
