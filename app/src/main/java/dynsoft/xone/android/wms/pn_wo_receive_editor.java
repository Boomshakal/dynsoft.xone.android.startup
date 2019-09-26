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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class pn_wo_receive_editor extends pn_editor {

	public pn_wo_receive_editor(Context context) {
		super(context);
	}

	public TextCell txt_issue_order_cell;
	public TextCell txt_task_order_cell;
	public TextCell txt_work_order_cell;
	public TextCell txt_item_code_cell;
	public TextCell txt_item_name_cell;
	public TextCell txt_vendor_name_cell;
	public TextCell txt_vendor_model_cell;
	public TextCell txt_vendor_lot_cell;
	public TextCell txt_date_code_cell;
	public TextCell txt_lot_number_cell;
	public DecimalCell txt_quantity_cell;
	public TextCell txt_location_cell;
	

	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_wo_receive_editor, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_issue_order_cell = (TextCell)this.findViewById(R.id.txt_issue_order_cell);
		this.txt_task_order_cell = (TextCell)this.findViewById(R.id.txt_task_order_cell);
		this.txt_work_order_cell = (TextCell)this.findViewById(R.id.txt_work_order_cell);
		this.txt_item_code_cell = (TextCell)this.findViewById(R.id.txt_item_code_cell);
		this.txt_item_name_cell = (TextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_vendor_name_cell = (TextCell)this.findViewById(R.id.txt_vendor_name_cell);
		this.txt_vendor_model_cell = (TextCell)this.findViewById(R.id.txt_vendor_model_cell);
		this.txt_vendor_lot_cell = (TextCell)this.findViewById(R.id.txt_vendor_lot_cell);
		this.txt_date_code_cell = (TextCell)this.findViewById(R.id.txt_date_code_cell);
		this.txt_lot_number_cell = (TextCell)this.findViewById(R.id.txt_lot_number_cell);
		this.txt_quantity_cell = (DecimalCell)this.findViewById(R.id.txt_quantity_cell);
		this.txt_location_cell = (TextCell)this.findViewById(R.id.txt_location_cell);
		
		if (this.txt_issue_order_cell != null) {
			this.txt_issue_order_cell.setLabelText("发料申请");
			this.txt_issue_order_cell.setReadOnly();
		}
		
		if (this.txt_task_order_cell != null) {
			this.txt_task_order_cell.setLabelText("生产任务");
			this.txt_task_order_cell.setReadOnly();
		}
		
		if (this.txt_work_order_cell != null) {
			this.txt_work_order_cell.setLabelText("工单编号");
			this.txt_work_order_cell.setReadOnly();
		}
		
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("物料编码");
			this.txt_item_code_cell.setReadOnly();
		}
		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("物料名称");
			this.txt_item_name_cell.setReadOnly();
			this.txt_item_name_cell.TextBox.setSingleLine(true);
		}
		
		if (this.txt_vendor_name_cell != null) {
			this.txt_vendor_name_cell.setLabelText("供应商");
			this.txt_vendor_name_cell.setReadOnly();
			this.txt_vendor_name_cell.TextBox.setSingleLine(true);
		}
		
		if (this.txt_vendor_lot_cell != null) {
			this.txt_vendor_lot_cell.setLabelText("厂家批号");
			this.txt_vendor_lot_cell.setReadOnly();
		}
		
		if (this.txt_vendor_model_cell != null) {
			this.txt_vendor_model_cell.setLabelText("厂家型号");
			this.txt_vendor_model_cell.setReadOnly();
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
			this.txt_quantity_cell.setReadOnly();
		}

		if (this.txt_location_cell != null) {
			this.txt_location_cell.setLabelText("储位");
			this.txt_location_cell.setReadOnly();
		}
		
		_issue_order_id = this.Parameters.get("issue_order_id", 0L);
		_issue_order_code = this.Parameters.get("issue_order_code", "");
		_task_order_code = this.Parameters.get("task_order_code", "");
		_work_order_code = this.Parameters.get("work_order_code", "");
		
		this.txt_issue_order_cell.setContentText(_issue_order_code);
		this.txt_task_order_cell.setContentText(_task_order_code);
		this.txt_work_order_cell.setContentText(_work_order_code);
		
		this.loadOrderCount();
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
			this.loadOrderItem(lot);
		}

		if (bar_code.startsWith("C:")){
			String lot = bar_code.substring(2, bar_code.length());
			this.loadOrderItem(lot);
		}
		
		if (bar_code.startsWith("L:")){
			String loc = bar_code.substring(2, bar_code.length());
			this.loadLocation(loc);
		}
	}
	
	private Integer _total;
	private Long _issue_order_id;
	private String _issue_order_code;
	private String _task_order_code;
	private String _work_order_code;
	private DataRow _order_row;
	private DataRow _location_row;
	
	public void loadOrderCount()
	{
		this.ProgressDialog.show();
		String sql = "exec p_mm_wo_receive_get_item_count ?,?";
		Parameters p = new Parameters().add(1, App.Current.UserID).add(2, _issue_order_id);
		App.Current.DbPortal.ExecuteScalarAsync(this.Connector, sql, p, Integer.class, new ResultHandler<Integer>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_wo_receive_editor.this.ProgressDialog.dismiss();
				
				Result<Integer> result = this.Value;
				if (this.Value.HasError) {
					App.Current.showError(pn_wo_receive_editor.this.getContext(), result.Error);
					return;
				}
				
				if (result.Value > 0) {
					String info = "发料交接("+ String.valueOf(result.Value) +"交接任务)";
					pn_wo_receive_editor.this.Header.setTitleText(info);
				} else {
					pn_wo_receive_editor.this.Header.setTitleText("发料交接(无交接任务)");
				}
			}
		});
	}
	
	public void loadOrderItem(String lot_number)
	{
		this.ProgressDialog.show();
		String sql = "exec p_mm_wo_receive_get_item ?,?,?";
		Parameters p = new Parameters().add(1, App.Current.UserID).add(2, _issue_order_id).add(3, lot_number);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_wo_receive_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_wo_receive_editor.this.getContext(), result.Error);
					return;
				}
				
				_order_row = result.Value;
				if (_order_row == null) {
					App.Current.showError(pn_wo_receive_editor.this.getContext(), "没有数据。");
					pn_wo_receive_editor.this.Header.setTitleText("发料交接(无交接任务)");
					pn_wo_receive_editor.this.clearAll();
					return;
				}
				
				_total = _order_row.getValue("lines_count", Integer.class);
				if (_total > 0 ){
					pn_wo_receive_editor.this.Header.setTitleText("发料交接(" + String.valueOf(_total) + "条交接任务)");
				} else {
					pn_wo_receive_editor.this.Header.setTitleText("发料交接(无交接任务)");
					pn_wo_receive_editor.this.clearAll();
				}

				//pn_wo_receive_editor.this.txt_issue_order_cell.setContentText(_order_row.getValue("code", ""));
				//pn_wo_receive_editor.this.txt_task_order_cell.setContentText(_order_row.getValue("task_order_code", ""));
				//pn_wo_receive_editor.this.txt_work_order_cell.setContentText(_order_row.getValue("work_order_code", ""));
				pn_wo_receive_editor.this.txt_item_code_cell.setContentText(_order_row.getValue("item_code", ""));
				pn_wo_receive_editor.this.txt_item_name_cell.setContentText(_order_row.getValue("item_name", ""));
				pn_wo_receive_editor.this.txt_vendor_name_cell.setContentText(_order_row.getValue("vendor_name", ""));
				pn_wo_receive_editor.this.txt_date_code_cell.setContentText(_order_row.getValue("date_code", ""));
				pn_wo_receive_editor.this.txt_vendor_lot_cell.setContentText(_order_row.getValue("vendor_lot", ""));
				pn_wo_receive_editor.this.txt_vendor_model_cell.setContentText(_order_row.getValue("vendor_model", ""));
				pn_wo_receive_editor.this.txt_lot_number_cell.setContentText(_order_row.getValue("lot_number", ""));
				String quantity = App.formatNumber(_order_row.getValue("quantity", BigDecimal.ZERO), "0.##");
				pn_wo_receive_editor.this.txt_quantity_cell.setContentText(quantity);
				
			}
		});
	}
	
	public void loadLocation(String location)
	{
		if (_order_row == null) {
			App.Current.showError(this.getContext(), "没有交接任务。");
			return;
		}
		
		String sql = "select id,code,name,warehouse_id from mm_location where code=?";
		Parameters p = new Parameters().add(1, location);
		Result<DataRow> ri = App.Current.DbPortal.ExecuteRecord(this.Connector, sql, p);
		if (ri.HasError) {
			App.Current.showError(this.getContext(), ri.Error);
			this.txt_location_cell.setContentText("");
			return;
		}

		_location_row = ri.Value;
		if (_location_row == null) {
			App.Current.showError(this.getContext(), "不存在编号为【" + location + "】的储位。");
			return;
		}
		
		String loc_code = ri.Value.getValue("code", String.class);
		Integer warehouse_id = ri.Value.getValue("warehouse_id", 0);
		if (warehouse_id.equals(_order_row.getValue("warehouse_id", 0)) == false){
			App.Current.showError(this.getContext(), "储位不属于交接任务指定的库位。");
			return;
		}
		
		
		this.txt_location_cell.setContentText(loc_code);
	}
	
	@Override
	public void commit()
	{
		if (_order_row == null) {
			App.Current.showError(this.getContext(), "没有交接任务数据，不能提交。");
			return;
		}
		
		final String locations = this.txt_location_cell.getContentText();
		
		final String quantity = txt_quantity_cell.getContentText();
		if (quantity == null || quantity.length() == 0) {
			App.Current.showError(this.getContext(), "没有输入数量，不能提交。");
			return;
		}
		
		final Long order_id = _order_row.getValue("id", 0L);
		final Integer line_id = _order_row.getValue("line_id", 0);
		final Long tran_id = _order_row.getValue("tran_id", 0L);
		final String lot_number = _order_row.getValue("lot_number", "");

		App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				
				Map<String, String> entry = new HashMap<String, String>();
				entry.put("user_id", App.Current.UserID);
				entry.put("order_id", String.valueOf(order_id));
				entry.put("order_line_id", String.valueOf(line_id));
				entry.put("wo_tran_id", String.valueOf(tran_id));
				entry.put("lot_number", lot_number);
				entry.put("quantity", quantity);
				entry.put("locations", String.valueOf(locations));
				
				ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
				entries.add(entry);
				
				//生成XML数据，并传给存储过程
				String xml = XmlHelper.createXml("wo_receives", null, null, "wo_receive", entries);
				String sql = "exec p_mm_wo_receive_execute ?,?";
				Connection conn = App.Current.DbPortal.CreateConnection(pn_wo_receive_editor.this.Connector);
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
							App.Current.showError(pn_wo_receive_editor.this.getContext(), rs.Error);
							pn_wo_receive_editor.this.clear();
							return;
						}
						
						App.Current.toastInfo(pn_wo_receive_editor.this.getContext(), "提交成功");
						pn_wo_receive_editor.this.clear();
						pn_wo_receive_editor.this.loadOrderCount();
					}
				} catch (SQLException e) {
					App.Current.showInfo(pn_wo_receive_editor.this.getContext(), e.getMessage());
					e.printStackTrace();
					pn_wo_receive_editor.this.clear();
				}
				
			}
		});
	}
	
	public void clear()
	{
		this.txt_item_code_cell.setContentText("");
		this.txt_item_name_cell.setContentText("");
		this.txt_vendor_name_cell.setContentText("");
		this.txt_date_code_cell.setContentText("");
		this.txt_lot_number_cell.setContentText("");
		this.txt_location_cell.setContentText("");
		this.txt_quantity_cell.setContentText("");
		this.txt_vendor_lot_cell.setContentText("");
		this.txt_vendor_model_cell.setContentText("");
	}
	
	public void clearAll()
	{
		this.txt_issue_order_cell.setContentText("");
		this.txt_task_order_cell.setContentText("");
		this.txt_work_order_cell.setContentText("");
		this.txt_item_code_cell.setContentText("");
		this.txt_item_name_cell.setContentText("");
		this.txt_vendor_name_cell.setContentText("");
		this.txt_date_code_cell.setContentText("");
		this.txt_lot_number_cell.setContentText("");
		this.txt_location_cell.setContentText("");
		this.txt_quantity_cell.setContentText("");
		this.txt_vendor_lot_cell.setContentText("");
		this.txt_vendor_model_cell.setContentText("");
	}
}
