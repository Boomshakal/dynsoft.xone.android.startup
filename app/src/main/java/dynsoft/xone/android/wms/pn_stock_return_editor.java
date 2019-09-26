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
import dynsoft.xone.android.control.Calculator;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.Book;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.start.FrmLogin;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class pn_stock_return_editor extends pn_editor {

	public pn_stock_return_editor(Context context) {
		super(context);
	}

	public TextCell txt_item_code_cell;
	public TextCell txt_item_name_cell;
	public TextCell txt_vendor_name_cell;
	public TextCell txt_vendor_model_cell;
	public TextCell txt_vendor_lot_cell;
	public TextCell txt_lot_number_cell;
	public TextCell txt_quantity_cell;
	public TextCell txt_location_cell;
	public TextCell txt_warehouse_cell;
	public DecimalCell txt_return_quantity_cell;
	

	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_item_code_cell = (TextCell)this.findViewById(R.id.txt_item_code_cell);
		this.txt_item_name_cell = (TextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_vendor_name_cell = (TextCell)this.findViewById(R.id.txt_vendor_name_cell);
		this.txt_vendor_model_cell = (TextCell)this.findViewById(R.id.txt_vendor_model_cell);
		this.txt_vendor_lot_cell = (TextCell)this.findViewById(R.id.txt_vendor_lot_cell);
		this.txt_lot_number_cell = (TextCell)this.findViewById(R.id.txt_lot_number_cell);
		this.txt_quantity_cell = (TextCell)this.findViewById(R.id.txt_quantity_cell);
		this.txt_location_cell = (TextCell)this.findViewById(R.id.txt_location_cell);
		this.txt_warehouse_cell = (TextCell)this.findViewById(R.id.txt_warehouse_cell);
		this.txt_return_quantity_cell = (DecimalCell)this.findViewById(R.id.txt_return_quantity_cell);
		
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
		
		if (this.txt_lot_number_cell != null) {
			this.txt_lot_number_cell.setLabelText("批次");
			this.txt_lot_number_cell.setReadOnly();
		}
		
		if (this.txt_quantity_cell != null) {
			this.txt_quantity_cell.setLabelText("现有数量");
			this.txt_quantity_cell.setReadOnly();
		}
		
		if (this.txt_location_cell != null) {
			this.txt_location_cell.setLabelText("储位");
			this.txt_location_cell.setReadOnly();
		}
		
		if (this.txt_warehouse_cell != null) {
			this.txt_warehouse_cell.setLabelText("库位");
			this.txt_warehouse_cell.setReadOnly();
		}
		
		if (this.txt_return_quantity_cell != null) {
			this.txt_return_quantity_cell.setLabelText("退回数量");
			this.txt_return_quantity_cell.setReadOnly();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == Calculator.CalculatorResult) {
			String qty = intent.getStringExtra("result");
			this.txt_return_quantity_cell.setContentText(qty);
		}
	}
	
	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_stock_return_editor, this, true);
        view.setLayoutParams(lp);
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
			this.loadLotNumber(lot);
		}

		if (bar_code.startsWith("C:")){
			String lot = bar_code.substring(2, bar_code.length());
			this.txt_lot_number_cell.setContentText(lot);
			this.loadLotNumber(lot);
		}
		
	}
	
	private DataRow _lot_number_row;
	
	public void loadLotNumber(String lotNumber)
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_mm_stock_return_get_lot_number ?,?";
		Parameters p  =new Parameters().add(1, App.Current.UserID).add(2, lotNumber);
		App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_stock_return_editor.this.ProgressDialog.dismiss();
				
				final Result<DataTable> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_stock_return_editor.this.getContext(), result.Error);
					return;
				}
				
				if (result.Value.Rows.size() > 0) {
					if (result.Value.Rows.size() > 1) {
		                ArrayList<String> names = new ArrayList<String>();
		                for (DataRow row : result.Value.Rows) {
		                    names.add(row.getValue("warehouse_code", ""));
		                }
		                
		                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		                        if (which >= 0) {
		                        	pn_stock_return_editor.this.loadLotRow(result.Value.Rows.get(which));
		                        }
		                        dialog.dismiss();
		                    }
		                };

		                new AlertDialog.Builder(pn_stock_return_editor.this.getContext())
		                .setTitle("选择库位")
		                .setSingleChoiceItems(names.toArray(new String[0]), 0, listener)
		                .setNegativeButton("取消", null).show();
					} else {
						pn_stock_return_editor.this.loadLotRow(result.Value.Rows.get(0));
					}
				} else {
					App.Current.showError(pn_stock_return_editor.this.getContext(), "该批次不存在。");
					return;	
				}
				
			}
		});
	}
	
	public void loadLotRow(DataRow row)
	{
		_lot_number_row = row;
		
		this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
		this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
		this.txt_vendor_name_cell.setContentText(row.getValue("vendor_name", ""));
		this.txt_vendor_model_cell.setContentText(row.getValue("vendor_model", ""));
		this.txt_vendor_lot_cell.setContentText(row.getValue("vendor_lot", ""));
		this.txt_lot_number_cell.setContentText(row.getValue("lot_number", ""));
		this.txt_quantity_cell.setContentText(App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##"));
		this.txt_location_cell.setContentText(row.getValue("locations", ""));
		this.txt_warehouse_cell.setContentText(row.getValue("warehouse_code", ""));
		this.txt_return_quantity_cell.setContentText(App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##"));
	}
	
	@Override
	public void commit()
	{
		if (_lot_number_row == null) {
			App.Current.showError(this.getContext(), "没有指定批次数据。");
			return;
		}
		
		final Integer item_id = _lot_number_row.getValue("item_id", Integer.class);
		final Integer warehouse_id = _lot_number_row.getValue("warehouse_id", Integer.class);
		final String lot_number = _lot_number_row.getValue("lot_number", String.class);
		final String quantity = txt_return_quantity_cell.getContentText().trim();

		App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				
				Map<String, String> entry = new HashMap<String, String>();
				entry.put("code", "PDA-"+UUID.randomUUID().toString());
				entry.put("create_user", App.Current.UserID);
				entry.put("item_id", String.valueOf(item_id));
				entry.put("warehouse_id", String.valueOf(warehouse_id));
				entry.put("lot_number", lot_number);
				entry.put("quantity", quantity);
				
				ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
				entries.add(entry);
				
				//生成XML数据，并传给存储过程
				String xml = XmlHelper.createXml("stock_returns", null, null, "stock_return", entries);
				String sql = "exec p_mm_stock_return_create ?,?";
				Connection conn = App.Current.DbPortal.CreateConnection(pn_stock_return_editor.this.Connector);
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
							App.Current.showError(pn_stock_return_editor.this.getContext(), rs.Error);
							return;
						}
						
						App.Current.toastInfo(pn_stock_return_editor.this.getContext(), "提交成功");
						
						pn_stock_return_editor.this.clear();
					}
				} catch (SQLException e) {
					App.Current.showInfo(pn_stock_return_editor.this.getContext(), e.getMessage());
					e.printStackTrace();
					pn_stock_return_editor.this.clear();
				}
			}
		});
	}
	
	public void clear()
	{
		pn_stock_return_editor.this.txt_item_code_cell.setContentText("");
		pn_stock_return_editor.this.txt_item_name_cell.setContentText("");
		pn_stock_return_editor.this.txt_vendor_name_cell.setContentText("");
		pn_stock_return_editor.this.txt_vendor_lot_cell.setContentText("");
		pn_stock_return_editor.this.txt_vendor_model_cell.setContentText("");
		pn_stock_return_editor.this.txt_lot_number_cell.setContentText("");
		pn_stock_return_editor.this.txt_quantity_cell.setContentText("");
		pn_stock_return_editor.this.txt_location_cell.setContentText("");
		pn_stock_return_editor.this.txt_warehouse_cell.setContentText("");
		pn_stock_return_editor.this.txt_return_quantity_cell.setContentText("");
	}
}
