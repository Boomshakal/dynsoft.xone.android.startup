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

import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

public class pn_tr_entry_editor extends pn_editor {

	public pn_tr_entry_editor(Context context) {
		super(context);
	}

	public TextCell txt_tr_order_cell;
	public TextCell txt_item_code_cell;
	public TextCell txt_item_name_cell;
	public TextCell txt_vendor_name_cell;
	public TextCell txt_vendor_model_cell;
	public TextCell txt_vendor_lot_cell;
	public TextCell txt_lot_number_cell;
	public DecimalCell txt_quantity_cell;
	public TextCell txt_date_code_cell;
	public TextCell txt_location_cell;
	public TextCell txt_warehouse_cell;

	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_tr_order_cell = (TextCell)this.findViewById(R.id.txt_tr_order_cell);
		this.txt_item_code_cell = (TextCell)this.findViewById(R.id.txt_item_code_cell);
		this.txt_item_name_cell = (TextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_vendor_name_cell = (TextCell)this.findViewById(R.id.txt_vendor_name_cell);
		this.txt_vendor_model_cell = (TextCell)this.findViewById(R.id.txt_vendor_model_cell);
		this.txt_vendor_lot_cell = (TextCell)this.findViewById(R.id.txt_vendor_lot_cell);
		this.txt_lot_number_cell = (TextCell)this.findViewById(R.id.txt_lot_number_cell);
		this.txt_date_code_cell = (TextCell)this.findViewById(R.id.txt_date_code_cell);
		this.txt_quantity_cell = (DecimalCell)this.findViewById(R.id.txt_quantity_cell);
		this.txt_location_cell = (TextCell)this.findViewById(R.id.txt_location_cell);
		this.txt_warehouse_cell = (TextCell)this.findViewById(R.id.txt_warehouse_cell);
		
		if (this.txt_tr_order_cell != null) {
			this.txt_tr_order_cell.setLabelText("检验单号");
			this.txt_tr_order_cell.setReadOnly();
		}
		
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("物料编码");
			this.txt_item_code_cell.setReadOnly();
		}
		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("物料名称");
			this.txt_item_name_cell.setReadOnly();
		}
		
		if (this.txt_vendor_name_cell != null) {
			this.txt_vendor_name_cell.setLabelText("供应商");
			this.txt_vendor_name_cell.setReadOnly();
		}
		
		if (this.txt_vendor_model_cell != null) {
			this.txt_vendor_model_cell.setLabelText("厂家型号");
			this.txt_vendor_model_cell.setReadOnly();
		}
		
		if (this.txt_vendor_lot_cell != null) {
			this.txt_vendor_lot_cell.setLabelText("厂家批次");
			this.txt_vendor_lot_cell.setReadOnly();
		}
		
		if (this.txt_date_code_cell != null) {
			this.txt_date_code_cell.setLabelText("D/C");
			this.txt_date_code_cell.setReadOnly();
		}
		
		if (this.txt_lot_number_cell != null) {
			this.txt_lot_number_cell.setLabelText("批次");
			this.txt_lot_number_cell.setReadOnly();
		}
		
		if (this.txt_quantity_cell != null) {
			this.txt_quantity_cell.setLabelText("数量");
		}
		
		if (this.txt_warehouse_cell != null) {
			this.txt_warehouse_cell.setLabelText("库位");
			this.txt_warehouse_cell.setReadOnly();
		}
		
		if (this.txt_location_cell != null) {
			this.txt_location_cell.setLabelText("储位");
			this.txt_location_cell.setReadOnly();
		}

		this.loadUpOrderInfo();
		
		String lot_number = this.Parameters.get("lot_number", "");
		if (lot_number != null && lot_number.length() > 0) {
			this.loadOrderItem(lot_number);
		}
	}
	
	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_tr_entry_editor, this, true);
        view.setLayoutParams(lp);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		this.txt_quantity_cell.onActivityResult(requestCode, resultCode, intent);
	}
	
	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		
		//扫描批次条码
		if (bar_code.startsWith("CRQ:")) {
			int pos = bar_code.indexOf("-");
			String lot = bar_code.substring(4, pos);
			this.txt_lot_number_cell.setContentText(lot);
			this.loadOrderItem(lot);
		}

		if (bar_code.startsWith("C:")){
			String lot = bar_code.substring(2, bar_code.length());
			this.txt_lot_number_cell.setContentText(lot);
			this.loadOrderItem(lot);
		}
		
		if (bar_code.startsWith("L:")){
			String loc = bar_code.substring(2, bar_code.length());
			String locs = this.txt_location_cell.getContentText().trim();
			if (locs.contains(loc)){
				return;
			}
			
			if (locs.length() > 0){
				locs += ", ";
			}
			this.txt_location_cell.setContentText(locs+loc);
		}
	}
	
	public void loadUpOrderInfo()
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_mm_tr_entry_get_count ?";
		Parameters p = new Parameters().add(1, App.Current.UserID);
		App.Current.DbPortal.ExecuteScalarAsync(this.Connector, sql, p, Integer.class, new ResultHandler<Integer>(){
			@Override
			public void handleMessage(Message msg){
				pn_tr_entry_editor.this.ProgressDialog.dismiss();
				
				Result<Integer> result = this.Value;
				if (this.Value.HasError) {
					App.Current.showError(pn_tr_entry_editor.this.getContext(), result.Error);
					return;
				}
				
				if (result.Value > 0) {
					String info = "库存转入("+ String.valueOf(result.Value) +"条转入申请)";
					pn_tr_entry_editor.this.Header.setTitleText(info);
				} else {
					pn_tr_entry_editor.this.Header.setTitleText("库存转入(无转入申请)");
				}
			}
		});
	}
	
	public void loadOrderItem(String lotNumber)
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_mm_tr_entry_get_item ?,?";
		Parameters p  =new Parameters().add(1, App.Current.UserID).add(2, lotNumber);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_tr_entry_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError){
					App.Current.showError(pn_tr_entry_editor.this.getContext(), result.Error);
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(pn_tr_entry_editor.this.getContext(), "待入库数据没有该批号。");
					return;
				}
				
				int total = row.getValue("lines_count", 0);
				if (total > 0) {
					String info = "库存转入(共"+ String.valueOf(total) +"条)";
					pn_tr_entry_editor.this.Header.setTitleText(info);
				} else {
					pn_tr_entry_editor.this.Header.setTitleText("库存转入(无待入库)");
				}
				
				pn_tr_entry_editor.this.txt_tr_order_cell.setTag(row);
				pn_tr_entry_editor.this.txt_tr_order_cell.setContentText(row.getValue("code", ""));
				pn_tr_entry_editor.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
				pn_tr_entry_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
				pn_tr_entry_editor.this.txt_vendor_name_cell.setContentText(row.getValue("vendor_name", ""));
				pn_tr_entry_editor.this.txt_vendor_model_cell.setContentText(row.getValue("vendor_model", ""));
				pn_tr_entry_editor.this.txt_vendor_lot_cell.setContentText(row.getValue("vendor_lot", ""));
				pn_tr_entry_editor.this.txt_date_code_cell.setContentText(row.getValue("date_code", ""));
				pn_tr_entry_editor.this.txt_lot_number_cell.setContentText(row.getValue("lot_number", ""));
				pn_tr_entry_editor.this.txt_warehouse_cell.setContentText(row.getValue("warehouse_code", ""));
				pn_tr_entry_editor.this.txt_quantity_cell.setContentText(App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##"));
			}
		});
	}
	
	@Override
	public void commit()
	{
		final DataRow row = (DataRow)this.txt_tr_order_cell.getTag();
		if (row == null) {
			App.Current.showError(this.getContext(), "没有库存转入指令，不能提交。");
			return;
		}
		
		final Long order_id = row.getValue("id", Long.class);
		final Long issue_id = row.getValue("issue_id", Long.class);
		final String order_code = row.getValue("code", "");
		final Integer order_line_id = row.getValue("line_id", Integer.class);
		final String quantity = this.txt_quantity_cell.getContentText();
		if (quantity == null || quantity.length() == 0) {
			App.Current.showError(this.getContext(), "没有输入数量，不能提交。");
			return;
		}
		
		final String locations = this.txt_location_cell.getContentText().trim();
		if (locations == null || locations.length() == 0) {
			App.Current.showError(this.getContext(), "没有指定入库储位，不能提交。");
			return;
		}
		
		App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				
				Map<String, String> entry = new HashMap<String, String>();
				entry.put("code", order_code);
				entry.put("create_user", App.Current.UserID);
				entry.put("order_id", String.valueOf(order_id));
				entry.put("order_line_id", String.valueOf(order_line_id));
				entry.put("issue_id", String.valueOf(issue_id));
				entry.put("item_id", String.valueOf(row.getValue("item_id", 0)));
				entry.put("quantity", quantity);
				entry.put("warehouse_id", String.valueOf(row.getValue("warehouse_id", Integer.class)));
				entry.put("locations", locations);
				
				ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
				entries.add(entry);
				
				//生成XML数据，并传给存储过程
				String xml = XmlHelper.createXml("tr_entries", null, null, "tr_entry", entries);
				String sql = "exec p_mm_tr_entry_create ?,?";
				Connection conn = App.Current.DbPortal.CreateConnection(pn_tr_entry_editor.this.Connector);
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
							App.Current.showError(pn_tr_entry_editor.this.getContext(), rs.Error);
							return;
						}
						
						App.Current.toastInfo(pn_tr_entry_editor.this.getContext(), "提交成功");
						
						pn_tr_entry_editor.this.clear();
						pn_tr_entry_editor.this.loadUpOrderInfo();
					}
				} catch (SQLException e) {
					App.Current.showInfo(pn_tr_entry_editor.this.getContext(), e.getMessage());
					e.printStackTrace();
					pn_tr_entry_editor.this.clear();
				}
			}
		});
	}
	
	public void clear()
	{
		pn_tr_entry_editor.this.txt_tr_order_cell.setTag(null);
		pn_tr_entry_editor.this.txt_tr_order_cell.setContentText("");
		pn_tr_entry_editor.this.txt_item_code_cell.setContentText("");
		pn_tr_entry_editor.this.txt_item_name_cell.setContentText("");
		pn_tr_entry_editor.this.txt_vendor_name_cell.setContentText("");
		pn_tr_entry_editor.this.txt_vendor_lot_cell.setContentText("");
		pn_tr_entry_editor.this.txt_vendor_model_cell.setContentText("");
		pn_tr_entry_editor.this.txt_lot_number_cell.setContentText("");
		pn_tr_entry_editor.this.txt_warehouse_cell.setContentText("");
		pn_tr_entry_editor.this.txt_date_code_cell.setContentText("");
		pn_tr_entry_editor.this.txt_quantity_cell.setContentText("");
		pn_tr_entry_editor.this.txt_location_cell.setContentText("");
	}
}
