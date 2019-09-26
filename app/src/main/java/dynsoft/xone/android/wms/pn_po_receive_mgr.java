package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
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
import dynsoft.xone.android.link.PaneLinker;

public class pn_po_receive_mgr extends pn_mgr {

	public pn_po_receive_mgr(Context context) {
		super(context);
	}

	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_po_receive_mgr, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
    public void onPrepared() {
        super.onPrepared();
        
        this.Matrix.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long index) {
				final DataRow row = (DataRow)Adapter.getItem((int)index);
				
				String shipment_code = row.getValue("shipment_code", "");
				String sql = "exec p_mm_po_receive_get_unreceived_items ?,?";
				Parameters p = new Parameters().add(1, App.Current.UserID).add(2, shipment_code);
				Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(pn_po_receive_mgr.this.Connector, sql, p);
				if (r.HasError) {
					App.Current.toastInfo(pn_po_receive_mgr.this.getContext(), r.Error);
				}
				
				if (r.Value != null && r.Value.Rows.size() > 0) {
					new AlertDialog.Builder(pn_po_receive_mgr.this.getContext())
					.setMessage("发运单【" + shipment_code + "】还有" + String.valueOf(r.Value.Rows.size()) + "个批次没有全部接收，确定要提交送检吗？")
					.setNeutralButton("查看", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Link link = new Link("pane://x:code=mm_and_po_shipment_mgr");
					    	link.Parameters.add("shipment_code", row.getValue("shipment_code", ""));
					        link.Open(pn_po_receive_mgr.this, pn_po_receive_mgr.this.getContext(), null);
						}
					}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							pn_po_receive_mgr.this.commit(row);
						}
					}).setNegativeButton("取消", null)
					.show();
				} else {
					App.Current.question(pn_po_receive_mgr.this.getContext(), "确定要将该发运单所有扫描批次提交送检吗？", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							pn_po_receive_mgr.this.commit(row);
						}
					});
				}
				
				return false;
			}
		});
	}
	
	@Override
	public void create()
	{
		Link link = new Link("pane://x:code=mm_and_po_receive_editor");
        link.Open(this, this.getContext(), null);
	}
    
	@Override
	public void onScan(final String barcode)
	{
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
	
	@Override
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
    	DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_po_receive, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_shipment_code = (TextView)convertView.findViewById(R.id.txt_shipment_code);
        TextView txt_vendor_name = (TextView)convertView.findViewById(R.id.txt_vendor_name);
        TextView txt_item_code = (TextView)convertView.findViewById(R.id.txt_item_code);
        TextView txt_item_name = (TextView)convertView.findViewById(R.id.txt_item_name);
        TextView txt_quantity = (TextView)convertView.findViewById(R.id.txt_quantity);
        TextView txt_locations = (TextView)convertView.findViewById(R.id.txt_locations);
        
        num.setText(String.valueOf(position + 1));
        txt_shipment_code.setText(row.getValue("shipment_code", ""));
        txt_vendor_name.setText(row.getValue("vendor_name", ""));
        txt_item_code.setText(row.getValue("item_code", "") + "，" + row.getValue("lot_number", ""));
        txt_item_name.setText(row.getValue("item_name", ""));
        String qty = row.getValue("warehouse_code", "") + ", "+ row.getValue("date_code", "") + ", " + App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##") + " " + row.getValue("uom_code", "") + ", " + row.getValue("status", "");
        txt_quantity.setText(qty);
        txt_locations.setText("接收储位：" + row.getValue("locations", ""));
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
    	SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_mm_po_receive_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
	
	public void commit(DataRow row)
	{
		if (row == null) return;
		
		Map<String, String> entry = new HashMap<String, String>();
		entry.put("shipment_code", row.getValue("shipment_code", ""));
		entry.put("receive_user", App.Current.UserID);
		entry.put("post_user", App.Current.UserID);
		
		ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
		entries.add(entry);
		
		//生成XML数据，并传给存储过程
		String xml = XmlHelper.createXml("po_receives", entry, null, null, null);
		String sql = "exec p_mm_po_receive_post ?,?";
		Connection conn = App.Current.DbPortal.CreateConnection(pn_po_receive_mgr.this.Connector);
		CallableStatement stmt;
		try {
			stmt = conn.prepareCall(sql);
			stmt.setObject(1, xml);
			stmt.registerOutParameter(2, Types.VARCHAR);
			stmt.execute();
			
			String val = stmt.getString(2);
			if (val != null) {
				Result<String> rs = XmlHelper.parseResult(val);
				if (rs.HasError) {
					App.Current.showError(pn_po_receive_mgr.this.getContext(), rs.Error);
					return;
				}
				
				App.Current.toastInfo(pn_po_receive_mgr.this.getContext(), "提交成功");
				
				pn_po_receive_mgr.this.refresh();
			}
		} catch (SQLException e) {
			App.Current.showInfo(pn_po_receive_mgr.this.getContext(), e.getMessage());
			e.printStackTrace();
			pn_po_receive_mgr.this.refresh();
		}
	}

}
