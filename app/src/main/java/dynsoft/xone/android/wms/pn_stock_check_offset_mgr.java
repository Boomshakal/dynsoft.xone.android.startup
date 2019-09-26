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

public class pn_stock_check_offset_mgr extends pn_mgr {

	public pn_stock_check_offset_mgr(Context context) {
		super(context);
	}

	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_stock_check_offset_mgr, this, true);
        view.setLayoutParams(lp);
	}
	public long _order_id ;
	public int _line_id ;
	@Override
    public void onPrepared() {
		
		//如果传递了发运单，则加载页面时按照发运单刷新数据，不要自动刷新
		_order_id = this.Parameters.get("head_id", 0L);
		_line_id =this.Parameters.get("line_id",0);
    	
        super.onPrepared();
        
        //this.SearchBox.setText(shipment_code);
  
    }
	
	@Override
	public void create()
	{

	}
	
	@Override
    public void openItem(DataRow row)
    {

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
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_mm_stock_check_offset_item, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_line1 = (TextView)convertView.findViewById(R.id.txt_line1);
        TextView txt_line2 = (TextView)convertView.findViewById(R.id.txt_line2);
        TextView txt_line3 = (TextView)convertView.findViewById(R.id.txt_line3);
        TextView txt_line4 = (TextView)convertView.findViewById(R.id.txt_line4);

        num.setText(String.valueOf(position + 1));
        
        txt_line1.setText(row.getValue("lot_number", "") + ", " + row.getValue("iqc_code", ""));
        txt_line2.setText("初盘储位："+row.getValue("locations1", "")+";复盘储位："+row.getValue("locations2", ""));
        txt_line3.setText("初盘数量："+App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##")  + ";复盘数量：" + App.formatNumber(row.getValue("requantity", BigDecimal.ZERO), "0.##") );
        txt_line4.setText("差异数量："+App.formatNumber(row.getValue("offset_quantity", BigDecimal.ZERO), "0.##") );
        
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
    	SqlExpression expr = new SqlExpression();
    	expr.SQL = "exec p_mm_stock_check_get_offset_items ?,?,?,?,?";
		expr.Parameters = new Parameters().add(1, _order_id).add(2, _line_id).add(3,start).add(4, end).add(5, search);
        return expr;
    }
}
