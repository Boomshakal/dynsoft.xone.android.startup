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

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.SwitchCell;
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
import android.graphics.Color;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

public class pn_iqc_escape_editor extends pn_editor {

	public pn_iqc_escape_editor(Context context) {
		super(context);
	}

	public TextCell txt_shipment_code_cell;
	public TextCell txt_item_code_cell;
	public TextCell txt_item_name_cell;
	public TextCell txt_vendor_name_cell;
	public TextCell txt_vendor_model_cell;
	public TextCell txt_lot_number_cell;
	public DecimalCell txt_quantity_cell;
	public TextCell txt_date_code_cell;
	public TextCell txt_location_cell;

	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_iqc_escape_editor, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_shipment_code_cell = (TextCell)this.findViewById(R.id.txt_shipment_code_cell);
		this.txt_item_code_cell = (TextCell)this.findViewById(R.id.txt_item_code_cell);
		this.txt_item_name_cell = (TextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_vendor_name_cell = (TextCell)this.findViewById(R.id.txt_vendor_name_cell);
		this.txt_vendor_model_cell = (TextCell)this.findViewById(R.id.txt_vendor_model_cell);
		this.txt_lot_number_cell = (TextCell)this.findViewById(R.id.txt_lot_number_cell);
		this.txt_quantity_cell = (DecimalCell)this.findViewById(R.id.txt_quantity_cell);
		this.txt_date_code_cell = (TextCell)this.findViewById(R.id.txt_date_code_cell);
		this.txt_location_cell = (TextCell)this.findViewById(R.id.txt_location_cell);
		
		if (this.txt_shipment_code_cell != null) {
			this.txt_shipment_code_cell.setLabelText("发运单号");
			this.txt_shipment_code_cell.setReadOnly();
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
		
		if (this.txt_lot_number_cell != null) {
			this.txt_lot_number_cell.setLabelText("批次");
			this.txt_lot_number_cell.setReadOnly();
		}
		
		if (this.txt_quantity_cell != null) {
			this.txt_quantity_cell.setLabelText("数量");
			this.txt_quantity_cell.setReadOnly();
		}
		
		if (this.txt_date_code_cell != null) {
			this.txt_date_code_cell.setLabelText("D/C");
			this.txt_date_code_cell.setReadOnly();
		}
		
		if (this.txt_location_cell != null) {
			this.txt_location_cell.setLabelText("接收储位");
			this.txt_location_cell.setReadOnly();
		}
		
		this.loadReceiveInfo();
		
		String lot_number = this.Parameters.get("lot_number", "");
		if (lot_number != null && lot_number.length() > 0) {
			this.loadLotNumber(lot_number);
		}
	}
	
	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		
		//扫描批次条码
		if (bar_code.startsWith("CR:")) {
			int pos = bar_code.indexOf("-");
			String lot = bar_code.substring(3, pos);
			this.txt_lot_number_cell.setContentText(lot);
			this.loadLotNumber(lot);
		}

		if (bar_code.startsWith("C:")){
			String lot = bar_code.substring(2, bar_code.length());
			this.txt_lot_number_cell.setContentText(lot);
			this.loadLotNumber(lot);
		}
		
	}
	
	public void loadReceiveInfo()
	{
		this.ProgressDialog.show();
		
		String sql = "SELECT COUNT(*) FROM dbo.mm_po_transaction WHERE type='PO_RECEIVE' and ISNULL(status, '')=N'免检待确认'";
		App.Current.DbPortal.ExecuteScalarAsync(this.Connector, sql, null, new ResultHandler<Integer>(){
			@Override
			public void handleMessage(Message msg){
				pn_iqc_escape_editor.this.ProgressDialog.dismiss();
				
				Result<Integer> result = this.Value;
				if (this.Value.HasError) {
					App.Current.showError(pn_iqc_escape_editor.this.getContext(), result.Error);
					return;
				}
				
				if (result.Value > 0) {
					String info = "免检确认("+ String.valueOf(result.Value) +"条待确认)";
					pn_iqc_escape_editor.this.Header.setTitleText(info);
				} else {
					pn_iqc_escape_editor.this.Header.setTitleText("免检确认(无待确认物料)");
				}
			}
		});
	}
	
	public void loadLotNumber(String lotNumber)
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_mm_po_iqc_escape_get_item ?,?";
		Parameters p  =new Parameters().add(1, App.Current.UserID).add(2, lotNumber);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_iqc_escape_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError){
					App.Current.showError(pn_iqc_escape_editor.this.getContext(), result.Error);
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(pn_iqc_escape_editor.this.getContext(), "待确认数据没有该批号。");
					pn_iqc_escape_editor.this.clear();
					return;
				}
				
				String status = row.getValue("status", "");
				if (status.equals("免检待确认") == false) {
					App.Current.showError(pn_iqc_escape_editor.this.getContext(), "该批次不是免检待确认状态。");
					pn_iqc_escape_editor.this.clear();
					return;
				}
				
				pn_iqc_escape_editor.this.txt_shipment_code_cell.setTag(row);
				pn_iqc_escape_editor.this.txt_shipment_code_cell.setContentText(row.getValue("shipment_code", ""));
				pn_iqc_escape_editor.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
				pn_iqc_escape_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
				pn_iqc_escape_editor.this.txt_vendor_name_cell.setContentText(row.getValue("vendor_name", ""));
				pn_iqc_escape_editor.this.txt_vendor_model_cell.setContentText(row.getValue("vendor_model", ""));
				pn_iqc_escape_editor.this.txt_lot_number_cell.setContentText(row.getValue("lot_number", ""));
				pn_iqc_escape_editor.this.txt_quantity_cell.setContentText(App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##"));
				pn_iqc_escape_editor.this.txt_date_code_cell.setContentText(row.getValue("date_code", "") + "，" + row.getValue("status", ""));
				pn_iqc_escape_editor.this.txt_location_cell.setContentText(row.getValue("locations", ""));
			}
		});
	
	}
	
	@Override
	public void commit()
	{
		final DataRow row = (DataRow)this.txt_shipment_code_cell.getTag();
		if (row == null) {
			App.Current.showError(this.getContext(), "没有数据，不能提交。");
			return;
		}
		
		this.ProgressDialog.show();
		
		App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {

				pn_iqc_escape_editor.this.ProgressDialog.dismiss();
				
				Long receive_id = row.getValue("id", Long.class);
				BigDecimal quantity = row.getValue("quantity", BigDecimal.class);
				
				Map<String, String> entry = new HashMap<String, String>();
				entry.put("code", "PDA-"+UUID.randomUUID().toString());
				entry.put("receive_id", String.valueOf(receive_id));
				entry.put("tester_id", App.Current.UserID);
				entry.put("quantity", App.formatNumber(quantity, "0.######"));
				
				ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
				entries.add(entry);
				
				//生成XML数据，并传给存储过程
				String xml = XmlHelper.createXml("po_accepts", null, null, "po_accept", entries);
				String sql = "exec p_mm_po_accept_create_from_escape ?,?";
				Connection conn = App.Current.DbPortal.CreateConnection(pn_iqc_escape_editor.this.Connector);
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
							App.Current.showError(pn_iqc_escape_editor.this.getContext(), rs.Error);
							return;
						}
						
						App.Current.toastInfo(pn_iqc_escape_editor.this.getContext(), "提交成功");
						
						pn_iqc_escape_editor.this.clear();
						pn_iqc_escape_editor.this.loadReceiveInfo();
					}
				} catch (SQLException e) {
					App.Current.showInfo(pn_iqc_escape_editor.this.getContext(), e.getMessage());
					e.printStackTrace();
					pn_iqc_escape_editor.this.clear();
				}
			}
		});
	}
	
	public void clear()
	{
		this.txt_shipment_code_cell.setTag(null);
		this.txt_shipment_code_cell.setContentText("");
		this.txt_item_code_cell.setContentText("");
		this.txt_item_name_cell.setContentText("");
		this.txt_vendor_name_cell.setContentText("");
		this.txt_vendor_model_cell.setContentText("");
		this.txt_lot_number_cell.setContentText("");
		this.txt_date_code_cell.setContentText("");
		this.txt_quantity_cell.setContentText("");
		this.txt_location_cell.setContentText("");
	}
}
