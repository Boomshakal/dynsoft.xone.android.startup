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
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

public class pn_po_receive_editor extends pn_editor {

	public pn_po_receive_editor(Context context) {
		super(context);
	}

	public TextCell txt_shipment_code_cell;
	public TextCell txt_org_code_cell;
	public TextCell txt_item_code_cell;
	public TextCell txt_item_name_cell;
	public TextCell txt_vendor_name_cell;
	public TextCell txt_vendor_model_cell;
	public TextCell txt_lot_number_cell;
	public DecimalCell txt_quantity_cell;
	public TextCell txt_date_code_cell;
	public TextCell txt_warehouse_cell;
	public ButtonTextCell txt_location_cell;
	public SwitchCell chk_remember_location_cell;

	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_po_receive_editor, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		this.txt_quantity_cell.onActivityResult(requestCode, resultCode, intent);
	}
	
	@Override
	public void onPrepared() {
		super.onPrepared();
		
		this.txt_shipment_code_cell = (TextCell)this.findViewById(R.id.txt_shipment_code_cell);
		this.txt_org_code_cell = (TextCell)this.findViewById(R.id.txt_org_code_cell);
		this.txt_item_code_cell = (TextCell)this.findViewById(R.id.txt_item_code_cell);
		this.txt_item_name_cell = (TextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_vendor_name_cell = (TextCell)this.findViewById(R.id.txt_vendor_name_cell);
		this.txt_vendor_model_cell = (TextCell)this.findViewById(R.id.txt_vendor_model_cell);
		this.txt_lot_number_cell = (TextCell)this.findViewById(R.id.txt_lot_number_cell);
		this.txt_quantity_cell = (DecimalCell)this.findViewById(R.id.txt_quantity_cell);
		this.txt_date_code_cell = (TextCell)this.findViewById(R.id.txt_date_code_cell);
		this.txt_warehouse_cell = (TextCell)this.findViewById(R.id.txt_warehouse_cell);
		this.txt_location_cell = (ButtonTextCell)this.findViewById(R.id.txt_location_cell);
		this.chk_remember_location_cell = (SwitchCell)this.findViewById(R.id.chk_remember_location_cell);
		
		if (this.txt_shipment_code_cell != null) {
			this.txt_shipment_code_cell.setLabelText("发运单号");
			this.txt_shipment_code_cell.setReadOnly();
		}
		
		if (this.txt_org_code_cell != null) {
			this.txt_org_code_cell.setLabelText("组织");
			this.txt_org_code_cell.setReadOnly();
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
		}
		
		if (this.txt_date_code_cell != null) {
			this.txt_date_code_cell.setLabelText("D/C");
			this.txt_date_code_cell.setReadOnly();
		}
		
		if (this.txt_warehouse_cell != null) {
			this.txt_warehouse_cell.setLabelText("库位");
			this.txt_warehouse_cell.setReadOnly();
		}
		
		if (this.txt_location_cell != null) {
			this.txt_location_cell.setLabelText("储位");
			this.txt_location_cell.setReadOnly();
			this.txt_location_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_light"));
			this.txt_location_cell.Button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_po_receive_editor.this.txt_location_cell.setContentText("");
				}
			});
		}
		
		if (this.chk_remember_location_cell != null) {
			this.chk_remember_location_cell.CheckBox.setTextColor(Color.BLACK);
			this.chk_remember_location_cell.CheckBox.setText("提交后清空待检储位");
		}
		
		_shipment_code = this.Parameters.get("shipment_code", "");
		
		if (_shipment_code != null && _shipment_code.length() > 0) {
			this.txt_shipment_code_cell.setContentText(_shipment_code);
		}
		
		this.showShipmentCount();
		
		_scan_lot_number = this.Parameters.get("lot_number", "");
		_scan_item_code = this.Parameters.get("item_code", "");
		
		if (_scan_lot_number != null && _scan_lot_number.length() > 0) {
			this.loadLotNumber(_scan_lot_number, true);
		}
	}
	
	private String _shipment_code;
	private String _scan_lot_number;
	private String _scan_item_code;
	private String _scan_quantity;
	
	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		
		//扫描发运单条码
		if (bar_code.startsWith("M:") && bar_code.length() == 16){
			String shipment_code = bar_code.substring(2, bar_code.length());
			this.txt_shipment_code_cell.setContentText(shipment_code);
			showShipmentCount();
			return;
		}
		
		if (bar_code.startsWith("M") && bar_code.length() == 14){
			this.txt_shipment_code_cell.setContentText(bar_code);
			showShipmentCount();
			return;
		}
		
		//扫描批次条码
		if (bar_code.startsWith("CRQ:")) {
			String str = bar_code.substring(4, bar_code.length());
			String[] arr = str.split("-");
			if (arr.length > 2) {
				_scan_lot_number = arr[0];
				_scan_item_code = arr[1];
				_scan_quantity = arr[2];
				
				this.txt_lot_number_cell.setContentText(_scan_lot_number);
				this.txt_quantity_cell.setContentText(_scan_quantity);
				this.loadLotNumber(_scan_lot_number, false);
			}
			return;
		}

		if (bar_code.startsWith("C:")){
			String lot = bar_code.substring(2, bar_code.length());
			this.txt_lot_number_cell.setContentText(lot);
			this.loadLotNumber(lot, false);
			return;
		}
		
		if (bar_code.startsWith("QTY:")){
			String qty = bar_code.substring(4, bar_code.length());
			this.txt_quantity_cell.setContentText(qty);
			return;
		}
		
		if (bar_code.startsWith("Q:")){
			String qty = bar_code.substring(2, bar_code.length());
			this.txt_quantity_cell.setContentText(qty);
			return;
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
	
	public void showShipmentCount()
	{
		this.ProgressDialog.show();
		
		final String shipment = this.txt_shipment_code_cell.getContentText().trim();
		final String sql = "exec p_mm_po_receive_get_item_count ?,?";
		Parameters p = new Parameters().add(1, App.Current.UserID).add(2, shipment);
		App.Current.DbPortal.ExecuteScalarAsync(this.Connector, sql, p, Integer.class, new ResultHandler<Integer>(){
			@Override
			public void handleMessage(Message msg){
				pn_po_receive_editor.this.ProgressDialog.dismiss();
				
				Result<Integer> result = this.Value;
				if (this.Value.HasError) {
					App.Current.showError(pn_po_receive_editor.this.getContext(), result.Error);
					pn_po_receive_editor.this.clear();
					return;
				}
				
				if (result.Value > 0) {
					String info = "采购接收("+ String.valueOf(result.Value) +"条待接收)";
					pn_po_receive_editor.this.Header.setTitleText(info);
				} else {
					pn_po_receive_editor.this.Header.setTitleText("采购接收(无待接收)");
				}
			}
		});
	}
	
	public void loadLotNumber(String lotNumber, final boolean withQuantity)
	{
		this.ProgressDialog.show();
		
    	String sql = "exec p_mm_po_receive_get_item ?,?";
		Parameters p  =new Parameters().add(1, App.Current.UserID).add(2, lotNumber);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_po_receive_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError){
					App.Current.showError(pn_po_receive_editor.this.getContext(), result.Error);
					pn_po_receive_editor.this.clear();
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(pn_po_receive_editor.this.getContext(), "查询不到该批次，请检查批次是否存在，或登录用户与发运地点是否匹配。");
					pn_po_receive_editor.this.clear();
					return;
				}
				
				String item_code = row.getValue("meg_pn", "");
				if (item_code.equals(_scan_item_code) == false) {
					App.Current.showError(pn_po_receive_editor.this.getContext(), "从条码读取的料号为【" + _scan_item_code + "】，与发运料号【" + item_code + "】不一致，不能接收。");
					pn_po_receive_editor.this.clear();
					return;
				}
				
				String status = row.getValue("status", "");
				if (status.equals("待接收") == false) {
					App.Current.showError(pn_po_receive_editor.this.getContext(), "该批次为"+status+"状态，不能接收。");
					pn_po_receive_editor.this.clear();
					return;
				}
				
				String scan_shipment_code = row.getValue("delivery_num", "");
				String current_shipment_code = pn_po_receive_editor.this.txt_shipment_code_cell.getContentText();
				if (current_shipment_code.length() > 0) {
					if (scan_shipment_code.equals(current_shipment_code) == false) {
						App.Current.showError(pn_po_receive_editor.this.getContext(), "所扫描批次不属于当前发运单。");
						pn_po_receive_editor.this.clear();
						return;
					}
				}
				String iqc_flag=row.getValue("iqc_flag","");
				if (iqc_flag.equals("免检"))
				{
				  App.Current.showWarning(pn_po_receive_editor.this.getContext(), "该批次物料免检，请注意D/C");
				}
				pn_po_receive_editor.this.txt_shipment_code_cell.setTag(row);
				pn_po_receive_editor.this.txt_shipment_code_cell.setContentText(row.getValue("delivery_num", ""));
				pn_po_receive_editor.this.txt_org_code_cell.setContentText(row.getValue("org_code", ""));
				pn_po_receive_editor.this.txt_warehouse_cell.setContentText(row.getValue("warehouse_code", ""));
				pn_po_receive_editor.this.txt_item_code_cell.setContentText(row.getValue("meg_pn", ""));
				pn_po_receive_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
				pn_po_receive_editor.this.txt_vendor_name_cell.setContentText(row.getValue("vendor_name", ""));
				pn_po_receive_editor.this.txt_vendor_model_cell.setContentText(row.getValue("vendor_pn", ""));
				pn_po_receive_editor.this.txt_lot_number_cell.setContentText(row.getValue("m_batch_num", ""));
				if (withQuantity) {
					pn_po_receive_editor.this.txt_quantity_cell.setContentText(App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##"));
				}
				pn_po_receive_editor.this.txt_date_code_cell.setContentText(row.getValue("date_code", ""));
				
				if (current_shipment_code.length() == 0){
					pn_po_receive_editor.this.showShipmentCount();
				}
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
		
		final String shipment_code = this.txt_shipment_code_cell.getContentText();
		if (shipment_code == null || shipment_code.length() == 0) {
			App.Current.showError(this.getContext(), "发运单为空，不能提交。");
			return;
		}
		
		final String warehouse = this.txt_warehouse_cell.getContentText().trim();
		if (warehouse == null || warehouse.length() == 0) {
			App.Current.showError(this.getContext(), "没有输入库位，不能提交。");
			return;
		}
		
		final String quantity = this.txt_quantity_cell.getContentText();
		if (quantity == null || quantity.length() == 0) {
			App.Current.showError(this.getContext(), "没有输入数量，不能提交。");
			return;
		}
		
		final String locations = this.txt_location_cell.getContentText();
		if (locations == null || locations.length() == 0) {
			App.Current.showError(this.getContext(), "没有指定待检储位，不能提交。");
			return;
		}
		
		Map<String, String> entry = new HashMap<String, String>();
		entry.put("code", row.getValue("delivery_num", ""));
		entry.put("package_id", row.getValue("package_id").toString());
		entry.put("shipment_code", row.getValue("delivery_num", ""));
		entry.put("organization_id", row.getValue("organization_id").toString());
		entry.put("create_user", App.Current.UserID);
		entry.put("vendor_id", row.getValue("vendor_id").toString());
		entry.put("item_id", row.getValue("item_id").toString());
		entry.put("lot_number", row.getValue("m_batch_num").toString());
		entry.put("vendor_lot", row.getValue("v_batch_num").toString());
		entry.put("vendor_model", row.getValue("vendor_pn").toString());
		entry.put("date_code", row.getValue("date_code").toString());
		entry.put("uom_code", row.getValue("ut").toString());
		entry.put("quantity", quantity);
		entry.put("locations", locations);
		
		ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
		entries.add(entry);
		
		//生成XML数据，并传给存储过程
		String xml = XmlHelper.createXml("po_receives", null, null, "po_receive", entries);
		String sql = "exec p_mm_po_receive_create ?,?";
		Connection conn = App.Current.DbPortal.CreateConnection(pn_po_receive_editor.this.Connector);
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
					App.Current.showError(pn_po_receive_editor.this.getContext(), rs.Error);
					return;
				}
				
				App.Current.toastInfo(pn_po_receive_editor.this.getContext(), "提交成功");
				pn_po_receive_editor.this.clearLotNumber();
				showShipmentCount();
			}
		} catch (SQLException e) {
			App.Current.showInfo(pn_po_receive_editor.this.getContext(), e.getMessage());
			e.printStackTrace();
			
			pn_po_receive_editor.this.clear();
			showShipmentCount();
		}
	}
	
	public void clearLotNumber()
	{
		this.txt_item_code_cell.setContentText("");
		this.txt_item_name_cell.setContentText("");
		this.txt_vendor_name_cell.setContentText("");
		this.txt_vendor_model_cell.setContentText("");
		this.txt_lot_number_cell.setContentText("");
		this.txt_date_code_cell.setContentText("");
		this.txt_quantity_cell.setContentText("");
		if (this.chk_remember_location_cell.CheckBox.isChecked()) {
			this.txt_location_cell.setContentText("");
		}
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
		if (this.chk_remember_location_cell.CheckBox.isChecked()) {
			this.txt_location_cell.setContentText("");
		}
	}
}
