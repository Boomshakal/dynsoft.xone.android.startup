package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;

public class pn_wo_entry_order_commit_mgr extends pn_mgr {

	public pn_wo_entry_order_commit_mgr(Context context) {
		super(context);
	}
	public void onPrepared() {
        super.onPrepared();
        
        this.Matrix.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long index) {
				final DataRow row = (DataRow)Adapter.getItem((int)index);
				
				Long id = row.getValue("id", 0L);
				String code =row.getValue("code","");
				String sql = "exec p_mm_wo_entry_get_unentry_items ?,?";
				Parameters p = new Parameters().add(1, App.Current.UserID).add(2,id);
				Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(pn_wo_entry_order_commit_mgr.this.Connector, sql, p);
				if (r.HasError) {
					App.Current.toastInfo(pn_wo_entry_order_commit_mgr.this.getContext(), r.Error);
				}
				if (r.Value != null && r.Value.Rows.size() > 0) {
					new AlertDialog.Builder(pn_wo_entry_order_commit_mgr.this.getContext())
					.setMessage("此缴库单【" + code + "】还有" + String.valueOf(r.Value.Rows.size()) + "个批次没有入库。")
					.setNeutralButton("查看", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Link link = new Link("pane://x:code=mm_and_wo_entry_order_uncommit_mgr");
					    	link.Parameters.add("code", row.getValue("code",""));
					        link.Open(pn_wo_entry_order_commit_mgr.this, pn_wo_entry_order_commit_mgr.this.getContext(), null);
						}
					}).setNegativeButton("取消", null)
					.show();
				} 
				return false;
			}
		});
	}
	
	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_wo_entry_order_mgr, this, true);
        view.setLayoutParams(lp);
	}
	@Override
	public void create()
	{
		Link link = new Link("pane://x:code=mm_and_wo_entry_order_commit_editor");
        link.Open(this, this.getContext(), null);
	}
    
	@Override
	public void onScan(final String barcode)
	{
		String txt = barcode;
        if (barcode.startsWith("CRQ:")){
		String tempbar_code =barcode.substring(4, barcode.length());
	    String lot_number=tempbar_code.split("-")[0];
		this.SearchBox.setText(lot_number);
        }else
        {
    	this.SearchBox.setText(txt);
        }
    	this.Adapter.DataTable = null;
    	this.Adapter.notifyDataSetChanged();
    	this.refresh();
    	//this.SearchBox.setText("");
    }
	
	@Override
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
    	DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_wo_entry_order, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_work_code = (TextView)convertView.findViewById(R.id.txt_work_code);
        TextView txt_factory_name = (TextView)convertView.findViewById(R.id.txt_factory_name);
        TextView txt_item_code = (TextView)convertView.findViewById(R.id.txt_item_code);
        TextView txt_item_name = (TextView)convertView.findViewById(R.id.txt_item_name);
        TextView txt_quantity = (TextView)convertView.findViewById(R.id.txt_quantity);
        TextView txt_location = (TextView)convertView.findViewById(R.id.txt_location);
        
        num.setText(String.valueOf(position + 1));
        txt_work_code.setText(row.getValue("code", "") + ", " + row.getValue("create_time", ""));
        txt_factory_name.setText(row.getValue("factory_name", ""));
        txt_item_code.setText(row.getValue("item_code", ""));
        txt_item_name.setText(row.getValue("item_name", ""));
        String qty = row.getValue("org_code", "") + ", " + row.getValue("warehouse_code", "") + ", " + row.getValue("date_code", "") + ", " + App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##") + " " + row.getValue("uom_code", "") ;
        txt_quantity.setText(qty);
        txt_location.setText(row.getValue("items", ""));
        return convertView;
    }
	@Override
    public void openItem(DataRow row)
    {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("id", row.getValue("id", 0L).toString());
		App.Current.Print("mm_wo_entry_order", "打印工单缴库单", parameters);
    }

	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
    	SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_mm_wo_entry_order_get_items_a ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
}
