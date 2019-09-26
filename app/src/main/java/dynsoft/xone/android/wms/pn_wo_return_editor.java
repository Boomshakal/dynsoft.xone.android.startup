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
import dynsoft.xone.android.link.Link;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class pn_wo_return_editor extends pn_editor {

	public pn_wo_return_editor(Context context) {
		super(context);
	}

	public ButtonTextCell txt_wo_return_order_cell;
	public TextCell txt_item_code_cell;
	public TextCell txt_item_name_cell;
	public TextCell txt_vendor_name_cell;
	public TextCell txt_date_code_cell;
	public TextCell txt_lot_number_cell;
	public DecimalCell txt_quantity_cell;
	public ButtonTextCell  txt_location_cell;
	public TextCell txt_warehouse_cell;
	public TextCell txt_vendor_model_cell;
	public TextCell txt_vendor_lot_cell;

	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_wo_return_editor, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_wo_return_order_cell = (ButtonTextCell)this.findViewById(R.id.txt_wo_return_order_cell);
		this.txt_item_code_cell = (TextCell)this.findViewById(R.id.txt_item_code_cell);
		this.txt_item_name_cell = (TextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_vendor_name_cell = (TextCell)this.findViewById(R.id.txt_vendor_name_cell);
		this.txt_date_code_cell = (TextCell)this.findViewById(R.id.txt_date_code_cell);
		this.txt_lot_number_cell = (TextCell)this.findViewById(R.id.txt_lot_number_cell);
		this.txt_quantity_cell = (DecimalCell)this.findViewById(R.id.txt_quantity_cell);
		this.txt_location_cell = (ButtonTextCell)this.findViewById(R.id.txt_location_cell);
		this.txt_warehouse_cell = (TextCell)this.findViewById(R.id.txt_warehouse_cell);
		this.txt_vendor_model_cell = (TextCell)this.findViewById(R.id.txt_vendor_model_cell);
		this.txt_vendor_lot_cell = (TextCell)this.findViewById(R.id.txt_vendor_lot_cell);
		
		if (this.txt_wo_return_order_cell != null) {
			this.txt_wo_return_order_cell.setLabelText("退料申请");
			this.txt_wo_return_order_cell.setReadOnly();
			this.txt_wo_return_order_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_right_light"));
			this.txt_wo_return_order_cell.Button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					String code = txt_wo_return_order_cell.getContentText().trim();
					if(code!=null && ("").equals(code) == false) {
				        Link link = new Link("pane://x:code=mm_and_wo_return_dtl_mgr");
					    link.Parameters.add("code", code);
					    link.Open(pn_wo_return_editor.this, pn_wo_return_editor.this.getContext(), null);
				    }
				}
			});
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
			this.txt_location_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_light"));
			this.txt_location_cell.Button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					
					pn_wo_return_editor.this.txt_location_cell.setContentText("");
				    
				}
				
			});
		}
		
		if (this.txt_warehouse_cell != null) {
			this.txt_warehouse_cell.setLabelText("库位");
			this.txt_warehouse_cell.setReadOnly();
		}
		
		if (this.txt_vendor_lot_cell != null) {
			this.txt_vendor_lot_cell.setLabelText("厂家批号");
			this.txt_vendor_lot_cell.setReadOnly();
		}
		
		if (this.txt_vendor_model_cell != null) {
			this.txt_vendor_model_cell.setLabelText("厂家型号");
			this.txt_vendor_model_cell.setReadOnly();
		}
		
		_order_id = this.Parameters.get("order_id", 0L);
		_order_code = this.Parameters.get("order_code", "");
		this.txt_wo_return_order_cell.setContentText(_order_code);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		this.txt_quantity_cell.onActivityResult(requestCode, resultCode, intent);
	}
	
	private String _scan_quantity;
	
	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		
		//扫描批次条码
		if (bar_code.startsWith("CRQ:")) {
			String str = bar_code.substring(4, bar_code.length());
			String[] arr = str.split("-");
			if (arr.length > 2) {
				String lot = arr[0];
				_scan_quantity = arr[2];
				this.loadReturnOrderItem(lot);
			}
		}

		if (bar_code.startsWith("C:")) {
			String lot = bar_code.substring(2, bar_code.length());
			this.loadReturnOrderItem(lot);
		}
		
		if (bar_code.startsWith("L:")) {
			String loc = bar_code.substring(2, bar_code.length());
			String locs = this.txt_location_cell.getContentText().trim();
			if (locs.contains(loc)) {
				return;
			}
			
			if (locs.length() > 0) {
				locs += ", ";
			}
			
			this.txt_location_cell.setContentText(locs+loc);
		}
	}
	
	private Long _order_id;
	private String _order_code;
	private DataRow _order_row;
	private Integer _total;
	
	public void loadReturnOrderItem(String lot_number)
	{
		this.ProgressDialog.show();
		String sql = "exec p_mm_wo_return_get_order_item ?,?,?";
		Parameters p = new Parameters().add(1, _order_id).add(2, lot_number).add(3, App.Current.UserID);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_wo_return_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_wo_return_editor.this.getContext(), result.Error);
					return;
				}
				
				_order_row = result.Value;
				if (_order_row == null) {
					App.Current.showError(pn_wo_return_editor.this.getContext(), "没有数据。");
					pn_wo_return_editor.this.clear();
					pn_wo_return_editor.this.Header.setTitleText("工单退料");
					return;
				}
				
				_total = _order_row.getValue("total", Integer.class);
				if (_total > 0 ){
					pn_wo_return_editor.this.Header.setTitleText("工单退料(共" + String.valueOf(_total) + "条)");
				} else {
					pn_wo_return_editor.this.Header.setTitleText("工单退料");
				}

				pn_wo_return_editor.this.txt_wo_return_order_cell.setContentText(_order_row.getValue("code", ""));
				pn_wo_return_editor.this.txt_item_code_cell.setContentText(_order_row.getValue("item_code", ""));
				pn_wo_return_editor.this.txt_item_name_cell.setContentText(_order_row.getValue("item_name", ""));
				pn_wo_return_editor.this.txt_vendor_name_cell.setContentText(_order_row.getValue("vendor_name", ""));
				pn_wo_return_editor.this.txt_date_code_cell.setContentText(_order_row.getValue("date_code", ""));
				pn_wo_return_editor.this.txt_vendor_lot_cell.setContentText(_order_row.getValue("vendor_lot", ""));
				pn_wo_return_editor.this.txt_vendor_model_cell.setContentText(_order_row.getValue("vendor_model", ""));
				pn_wo_return_editor.this.txt_lot_number_cell.setContentText(_order_row.getValue("lot_number", ""));
				pn_wo_return_editor.this.txt_warehouse_cell.setContentText(_order_row.getValue("warehouse_code", ""));
				pn_wo_return_editor.this.txt_quantity_cell.setContentText(_scan_quantity);
				pn_wo_return_editor.this.txt_location_cell.setContentText(_order_row.getValue("locations", ""));
			}
		});
	}
	
	@Override
	public void commit()
	{
		if (_order_row == null) {
			App.Current.showError(this.getContext(), "没有退料指令数据，不能提交。");
			return;
		}
		
		final String quantity = txt_quantity_cell.getContentText();
		if (quantity == null || quantity.length() == 0) {
			App.Current.showError(this.getContext(), "没有输入数量，不能提交。");
			return;
		}
		
		final String locations = txt_location_cell.getContentText().trim();
		if (locations == null || locations.length() == 0) {
			App.Current.showError(this.getContext(), "没有输入储位，不能提交。");
			return;
		}
		
		final String order_code = _order_row.getValue("code", "");
		final Long order_id = _order_row.getValue("head_id", Long.class);
		final Integer line_id = _order_row.getValue("line_id", Integer.class);
		
		App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				
				Map<String, String> entry = new HashMap<String, String>();
				entry.put("code", order_code);
				entry.put("branch_id", App.Current.BranchID);
				entry.put("create_user", App.Current.UserID);
				entry.put("order_id", String.valueOf(order_id));
				entry.put("order_line_id", String.valueOf(line_id));
				entry.put("quantity", quantity);
				entry.put("locations", String.valueOf(locations));
				
				ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
				entries.add(entry);
				
				//生成XML数据，并传给存储过程
				String xml = XmlHelper.createXml("wo_returns", null, null, "wo_return", entries);
				String sql = "exec p_mm_wo_return_create ?,?";
				Connection conn = App.Current.DbPortal.CreateConnection(pn_wo_return_editor.this.Connector);
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
							App.Current.showError(pn_wo_return_editor.this.getContext(), rs.Error);
							return;
						}
						
						App.Current.toastInfo(pn_wo_return_editor.this.getContext(), "提交成功");
						_order_row = null;
						pn_wo_return_editor.this.clear();
					}
				} catch (SQLException e) {
					App.Current.showInfo(pn_wo_return_editor.this.getContext(), e.getMessage());
					e.printStackTrace();
					
					pn_wo_return_editor.this.clear();
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
		this.txt_warehouse_cell.setContentText("");
		this.txt_location_cell.setContentText("");
		this.txt_quantity_cell.setContentText("");
		this.txt_vendor_lot_cell.setContentText("");
		this.txt_vendor_model_cell.setContentText("");
	}
}
