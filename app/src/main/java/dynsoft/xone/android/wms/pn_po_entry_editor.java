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

import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class pn_po_entry_editor extends pn_editor {

	public pn_po_entry_editor(Context context) {
		super(context);
	}

	public TextCell txt_iqc_code_cell;
	public TextCell txt_shipment_code_cell;
	public TextCell txt_item_code_cell;
	public TextCell txt_item_name_cell;
	public TextCell txt_vendor_name_cell;
	public TextCell txt_vendor_model_cell;
	public TextCell txt_lot_number_cell;
	public DecimalCell txt_quantity_cell;
	public TextCell txt_warehouse_cell;
	public ButtonTextCell txt_location_cell;
	public TextCell txt_receive_time_cell;

	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_po_entry_editor, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_iqc_code_cell = (TextCell)this.findViewById(R.id.txt_iqc_code_cell);
		this.txt_shipment_code_cell = (TextCell)this.findViewById(R.id.txt_shipment_code_cell);
		this.txt_item_code_cell = (TextCell)this.findViewById(R.id.txt_item_code_cell);
		this.txt_item_name_cell = (TextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_vendor_name_cell = (TextCell)this.findViewById(R.id.txt_vendor_name_cell);
		this.txt_vendor_model_cell = (TextCell)this.findViewById(R.id.txt_vendor_model_cell);
		this.txt_lot_number_cell = (TextCell)this.findViewById(R.id.txt_lot_number_cell);
		this.txt_quantity_cell = (DecimalCell)this.findViewById(R.id.txt_quantity_cell);
		this.txt_warehouse_cell = (TextCell)this.findViewById(R.id.txt_warehouse_cell);
		this.txt_location_cell = (ButtonTextCell)this.findViewById(R.id.txt_location_cell);
		this.txt_receive_time_cell = (TextCell)this.findViewById(R.id.txt_receive_time_cell);
		
		if (this.txt_iqc_code_cell != null) {
			this.txt_iqc_code_cell.setLabelText("检验单号");
			this.txt_iqc_code_cell.setReadOnly();
		}
		
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
		}
		
		if (this.txt_warehouse_cell != null) {
			this.txt_warehouse_cell.setLabelText("库位");
		}
		
		if (this.txt_location_cell != null) {
			this.txt_location_cell.setLabelText("储位");
			this.txt_location_cell.setReadOnly();
			this.txt_location_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_light"));
			this.txt_location_cell.Button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_po_entry_editor.this.txt_location_cell.setContentText("");
				}
			});
		}
		
		if (this.txt_receive_time_cell != null) {
			this.txt_receive_time_cell.setLabelText("接收时间");
			this.txt_receive_time_cell.setReadOnly();
		}

		this.showAcceptCount();
		String lot_number = this.Parameters.get("lot_number", "");
		if (lot_number != null && lot_number.length() > 0) {
			this.loadLotNumber(lot_number);
		}
	}
	
	private String _scan_quantity;
	
	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		
		if (bar_code.startsWith("RCRQ:")) {
			App.Current.showError(pn_po_entry_editor.this.getContext(), "这是已标记为拒绝的批次，不能入库。");
			return;
		}
		
		//扫描批次条码
		if (bar_code.startsWith("CRQ:")) {
			String cc = bar_code.substring(4, bar_code.length());
			String[] arr = cc.split("-");
			String lot = arr[0];
			_scan_quantity = arr[2];
			
			this.txt_lot_number_cell.setContentText(lot);
			this.loadLotNumber(lot);
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
	
	public void showAcceptCount()
	{
		this.ProgressDialog.show();
		
		final String sql = "exec p_mm_po_entry_get_item_count ?";
		Parameters p = new Parameters().add(1, App.Current.UserID);
		App.Current.DbPortal.ExecuteScalarAsync(this.Connector, sql, p, Integer.class, new ResultHandler<Integer>(){
			@Override
			public void handleMessage(Message msg){
				pn_po_entry_editor.this.ProgressDialog.dismiss();
				
				Result<Integer> result = this.Value;
				if (this.Value.HasError) {
					App.Current.showError(pn_po_entry_editor.this.getContext(), result.Error);
					pn_po_entry_editor.this.clear();
					return;
				}
				
				if (result.Value > 0) {
					String info = "采购入库("+ String.valueOf(result.Value) +"条待入库)";
					pn_po_entry_editor.this.Header.setTitleText(info);
				} else {
					pn_po_entry_editor.this.Header.setTitleText("采购入库(无待入库)");
				}
			}
		});
	}
	
	public void loadLotNumber(String lotNumber)
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_mm_po_entry_get_item ?,?";
		Parameters p  =new Parameters().add(1, App.Current.UserID).add(2, lotNumber);
		App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_po_entry_editor.this.ProgressDialog.dismiss();
				
				final Result<DataTable> r = this.Value;
				if (r.HasError){
					App.Current.showError(pn_po_entry_editor.this.getContext(), r.Error);
					return;
				}
				
				if (r.Value == null || r.Value.Rows.size() == 0) {
					App.Current.showError(pn_po_entry_editor.this.getContext(), "该批次号查询不到数据。");
					return;
				}
				
				if (r.Value.Rows.size() > 1) {
					
	                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        if (which >= 0) {
	                        	DataRow row = r.Value.Rows.get(which);
	                        	pn_po_entry_editor.this.showLotNumber(row);
	                        }
	                        dialog.dismiss();
	                    }
	                };

	                final TableAdapter adapter = new TableAdapter(pn_po_entry_editor.this.getContext()) {
	                    @Override
	                    public View getView(int position, View convertView, ViewGroup parent) {
	                    	DataRow row = (DataRow)r.Value.Rows.get(position);
	                    	if (convertView == null) {
	                    		convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_lot_number, null);
	                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
	                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
	                    	}
	                        
	                        TextView num = (TextView)convertView.findViewById(R.id.num);
	                        TextView txt_lot_number = (TextView)convertView.findViewById(R.id.txt_lot_number);
	                        TextView txt_quantity = (TextView)convertView.findViewById(R.id.txt_quantity);
	                        
	                        num.setText(String.valueOf(position + 1));
	                        String lot_number = row.getValue("lot_number", "");
	                        String qty = row.getValue("date_code", "") + ", " + App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##") + " " + row.getValue("uom_code", "");
	                        
	                        txt_lot_number.setText(lot_number);
	                        txt_quantity.setText(qty);

	                        return convertView;
	                    }
	                };
	                
	                adapter.DataTable = r.Value;
	                adapter.notifyDataSetChanged();
	                
	                new AlertDialog.Builder(pn_po_entry_editor.this.getContext())
	                .setTitle("选择合格数量")
	                .setSingleChoiceItems(adapter, 0, listener)
	                .setNegativeButton("取消", null).show();
					
				} else {
					DataRow row = r.Value.Rows.get(0);
					pn_po_entry_editor.this.showLotNumber(row);
				}
			}
		});
	}
	
	public void showLotNumber(DataRow row)
	{
		int total = row.getValue("total", 0);
		if (total > 0) {
			String info = "采购入库(共"+ String.valueOf(total) +"条)";
			pn_po_entry_editor.this.Header.setTitleText(info);
		} else {
			pn_po_entry_editor.this.Header.setTitleText("采购入库");
		}
		
		pn_po_entry_editor.this.txt_iqc_code_cell.setTag(row.getValue("id", Long.class));
		pn_po_entry_editor.this.txt_iqc_code_cell.setContentText(row.getValue("iqc_code", ""));
		pn_po_entry_editor.this.txt_shipment_code_cell.setContentText(row.getValue("shipment_code", ""));
		pn_po_entry_editor.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
		pn_po_entry_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
		pn_po_entry_editor.this.txt_vendor_name_cell.setContentText(row.getValue("vendor_name", ""));
		pn_po_entry_editor.this.txt_vendor_model_cell.setContentText(row.getValue("vendor_model", ""));
		pn_po_entry_editor.this.txt_lot_number_cell.setContentText(row.getValue("lot_number", ""));
		pn_po_entry_editor.this.txt_warehouse_cell.setContentText(row.getValue("warehouse_code", ""));
		pn_po_entry_editor.this.txt_receive_time_cell.setContentText(row.getValue("receive_time", ""));
		
		if (_scan_quantity == null || _scan_quantity.length() == 0) {
			String qty = App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##");
			pn_po_entry_editor.this.txt_quantity_cell.setContentText(qty);
		} else {
			pn_po_entry_editor.this.txt_quantity_cell.setContentText(_scan_quantity);
		}
	}
	
	@Override
	public void commit()
	{
		final Long accept_id = (Long)this.txt_iqc_code_cell.getTag();
		if (accept_id == null || accept_id == 0) {
			App.Current.showError(this.getContext(), "没有指定待入库项，不能提交。");
			return;
		}
		
		final String quantity = this.txt_quantity_cell.getContentText();
		if (quantity == null || quantity.length() == 0) {
			App.Current.showError(this.getContext(), "没有输入数量，不能提交。");
			return;
		}
		
		final String warehouse_code = this.txt_warehouse_cell.getContentText().trim();
		if (warehouse_code == null || warehouse_code.length() == 0) {
			App.Current.showError(this.getContext(), "没有指定入库库位，不能提交。");
			return;
		}
		
		final String locations = this.txt_location_cell.getContentText().trim();
		/*if (locations == null || locations.length() == 0) {
			App.Current.showError(this.getContext(), "没有指定入库储位，不能提交。");
			return;
		}*/
		
		Map<String, String> entry = new HashMap<String, String>();
		entry.put("code", "PDA-"+UUID.randomUUID().toString());
		entry.put("accept_id", String.valueOf(accept_id));
		entry.put("user_id", App.Current.UserID);
		entry.put("quantity", quantity);
		entry.put("warehouse_code", warehouse_code);
		entry.put("locations", locations);
		
		ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
		entries.add(entry);
		
		//生成XML数据，并传给存储过程
		String xml = XmlHelper.createXml("po_entries", null, null, "po_entry", entries);
		String sql = "exec p_mm_po_entry_create_from_accept ?,?";
		Connection conn = App.Current.DbPortal.CreateConnection(pn_po_entry_editor.this.Connector);
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
					App.Current.showError(pn_po_entry_editor.this.getContext(), rs.Error);
					return;
				}
				App.Current.toastInfo(pn_po_entry_editor.this.getContext(), "提交成功");
				
				pn_po_entry_editor.this.clear();
				this.showAcceptCount();
			}
		} catch (SQLException e) {
			App.Current.showInfo(pn_po_entry_editor.this.getContext(), e.getMessage());
			e.printStackTrace();
			pn_po_entry_editor.this.clear();
		}
	}
	
	public void clear()
	{
		pn_po_entry_editor.this.txt_iqc_code_cell.setTag(null);
		pn_po_entry_editor.this.txt_iqc_code_cell.setContentText("");
		pn_po_entry_editor.this.txt_item_code_cell.setContentText("");
		pn_po_entry_editor.this.txt_item_name_cell.setContentText("");
		pn_po_entry_editor.this.txt_vendor_name_cell.setContentText("");
		pn_po_entry_editor.this.txt_vendor_model_cell.setContentText("");
		pn_po_entry_editor.this.txt_lot_number_cell.setContentText("");
		pn_po_entry_editor.this.txt_quantity_cell.setContentText("");
	}
}
