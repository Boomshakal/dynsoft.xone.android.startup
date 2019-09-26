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
import dynsoft.xone.android.link.Link;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class pn_stock_check_editor extends pn_editor {

	public pn_stock_check_editor(Context context) {
		super(context);
	}

	public TextCell txt_stock_check_order_cell;
	public ButtonTextCell txt_item_code_cell;
	public ButtonTextCell txt_item_name_cell;
	public ButtonTextCell txt_summary_cell;
	public TextCell txt_vendor_name_cell;
	public TextCell txt_vendor_model_cell;
	public TextCell txt_vendor_lot_cell;
	public TextCell txt_lot_number_cell;
	public DecimalCell txt_quantity_cell;
	public TextCell txt_date_code_cell;
	public ButtonTextCell txt_location_cell;
	public TextCell txt_total_quantity_cell;

	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_stock_check_order_cell = (TextCell)this.findViewById(R.id.txt_stock_check_order_cell);
		this.txt_item_code_cell = (ButtonTextCell)this.findViewById(R.id.txt_item_code_cell);
		this.txt_item_name_cell = (ButtonTextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_summary_cell = (ButtonTextCell)this.findViewById(R.id.txt_summary_cell);
		this.txt_vendor_name_cell = (TextCell)this.findViewById(R.id.txt_vendor_name_cell);
		this.txt_vendor_model_cell = (TextCell)this.findViewById(R.id.txt_vendor_model_cell);
		this.txt_vendor_lot_cell = (TextCell)this.findViewById(R.id.txt_vendor_lot_cell);
		this.txt_lot_number_cell = (TextCell)this.findViewById(R.id.txt_lot_number_cell);
		this.txt_date_code_cell = (TextCell)this.findViewById(R.id.txt_date_code_cell);
		this.txt_quantity_cell = (DecimalCell)this.findViewById(R.id.txt_quantity_cell);
		this.txt_location_cell = (ButtonTextCell)this.findViewById(R.id.txt_location_cell);
		this.txt_total_quantity_cell = (TextCell)this.findViewById(R.id.txt_total_quantity_cell);
		
		if (this.txt_stock_check_order_cell != null) {
			this.txt_stock_check_order_cell.setLabelText("盘点单号");
			this.txt_stock_check_order_cell.setReadOnly();
		}
		
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("物料编码");
			this.txt_item_code_cell.setReadOnly();
			this.txt_item_code_cell.setButtonImage(App.Current.ResourceManager.getImage("core_multis_light"));
			this.txt_item_code_cell.Button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					showItemStock();
				}
			});
		}
		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("物料名称");
			this.txt_item_name_cell.setReadOnly();
			this.txt_item_name_cell.setButtonImage(App.Current.ResourceManager.getImage("core_right_light"));
			this.txt_item_name_cell.Button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					//openInitLotNumber();
				}
			});
		}
		
		if (this.txt_summary_cell != null) {
			this.txt_summary_cell.setLabelText("累计盘点");
			this.txt_summary_cell.setReadOnly();
			this.txt_summary_cell.setButtonImage(App.Current.ResourceManager.getImage("core_multis_light"));
			this.txt_summary_cell.Button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					openCheckedLotNumbers();
				}
			});
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
		
		if (this.txt_location_cell != null) {
			this.txt_location_cell.setLabelText("储位");
			this.txt_location_cell.setReadOnly();
			this.txt_location_cell.setButtonImage(App.Current.ResourceManager.getImage("core_close_light"));
			this.txt_location_cell.Button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					txt_location_cell.setContentText("");
				}
			});
		}
		
		if (this.txt_quantity_cell != null) {
			this.txt_quantity_cell.setLabelText("数量");
		}
		
		if (this.txt_total_quantity_cell != null) {
			this.txt_total_quantity_cell.setLabelText("累计数量");
			this.txt_total_quantity_cell.setReadOnly();
		}

		_order_id = this.Parameters.get("order_id", 0L);
		_order_code = this.Parameters.get("order_code", "");
		_line_id = this.Parameters.get("line_id", 0);
		this.loadOrderItem();
	}
	
	private Long _order_id;
	private String _order_code;
	private int _line_id;
	
	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_stock_check_editor, this, true);
        view.setLayoutParams(lp);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		this.txt_quantity_cell.onActivityResult(requestCode, resultCode, intent);
	}
	
	public void showItemStock()
	{
		if (_order_row != null) {
			int item_id = _order_row.getValue("item_id", 0);
			String sql = "exec p_mm_stock_check_get_stock_list ?";
			Parameters p = new Parameters().add(1, item_id);
			Result<String> r = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, String.class);
			if (r.HasError){
				App.Current.showError(this.getContext(), r.Error);
				return;
			}
			
			new AlertDialog.Builder(this.getContext())
	        .setTitle("实时库存")
	        .setMessage(r.Value)
	        .setPositiveButton("确定", null)
	        .show();
		}
	}
	
	public void openInitLotNumber()
	{
		if (_order_row != null) {
			String item_code = _order_row.getValue("item_code", "");
			Link link = new Link("pane://x:code=mm_begin_stock_editor");
			link.Parameters.add("item_code", item_code);
	        link.Open(this, this.getContext(), null);
		}
	}
	
	public void openCheckedLotNumbers()
	{
		if (_order_row == null) {
			App.Current.showError(this.getContext(), "没有指定盘点单。");
			return;
		}
		
		String sql = "exec p_mm_stock_check_get_lot_list ?,?,?";
		Parameters p = new Parameters()
		.add(1, _order_row.getValue("id",0L))
		.add(2, _order_row.getValue("line_id", 0))
		.add(3,App.Current.UserID);
		
		final Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql,p);
		if (r.HasError) {
			App.Current.showError(this.getContext(), r.Error);
			return;
		}
		
		if (r.Value != null && r.Value.Rows.size() > 0) {
			final TableAdapter adapter = new TableAdapter(this.getContext()) {
	            @Override
	            public View getView(int position, View convertView, ViewGroup parent) {
	            	DataRow row = (DataRow)r.Value.Rows.get(position);
	            	if (convertView == null) {
	            		convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_stock_check_lot, null);
	                    ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
	                    icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
	            	}
	                
	                TextView num = (TextView)convertView.findViewById(R.id.num);
	                
	                TextView lot_number = (TextView)convertView.findViewById(R.id.txt_lot_number);
	                TextView locations = (TextView)convertView.findViewById(R.id.txt_locations);
	                TextView vendor_name = (TextView)convertView.findViewById(R.id.txt_vendor_name);
	                TextView vendor_lot = (TextView)convertView.findViewById(R.id.txt_vendor_lot);
	                TextView vendor_model = (TextView)convertView.findViewById(R.id.txt_vendor_model);
	                TextView quantity = (TextView)convertView.findViewById(R.id.txt_quantity);
	                
	                num.setText(String.valueOf(position + 1));

	                lot_number.setText(row.getValue("lot_number", "") + ", " + row.getValue("date_code", ""));
	                locations.setText(row.getValue("locations", ""));
	                vendor_name.setText(row.getValue("vendor_name", ""));
	                
	                String v_lot = row.getValue("vendor_lot", "");
	                if (v_lot.length() > 0) {
	                	vendor_lot.setText("厂家批次: "+ v_lot);
	                	vendor_lot.setVisibility(View.VISIBLE);
	                } else {
	                	vendor_lot.setText("");
	                	vendor_lot.setVisibility(View.GONE);
	                }
	                
	                String v_model = row.getValue("vendor_model", "");
	                if (v_model.length() > 0) {
	                	vendor_model.setText("厂家型号: "+ v_model);
	                	vendor_model.setVisibility(View.VISIBLE);
	                } else {
	                	vendor_model.setText("");
	                	vendor_model.setVisibility(View.VISIBLE);
	                }
	                
	                quantity.setText(App.formatNumber(row.getValue("quantity"), "0.##") + row.getValue("uom_code", ""));

	                return convertView;
	            }
	        };
	        
	        adapter.DataTable = r.Value;
	        adapter.notifyDataSetChanged();
	        
	        new AlertDialog.Builder(this.getContext())
	        .setTitle("查看盘点批次")
	        .setSingleChoiceItems(adapter, 0, null)
	        .setNegativeButton("取消", null).show();
		} else {
			App.Current.showError(this.getContext(), "没有数据。");
			return;
		}
	}
	
	private String _scan_lot_number;
	private String _scan_quantity;
	
	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		
		//扫描批次条码
		if (bar_code.startsWith("CRQ:")) {
			String cc = bar_code.substring(4, bar_code.length());
			String[] arr = cc.split("-");
			_scan_lot_number = arr[0];
			_scan_quantity = arr[2];
			this.loadLotNumber(_scan_lot_number);
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
	
	private DataRow _order_row;
	private DataRow _lot_row;
	
	public void loadOrderItem()
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_mm_stock_check_get_item ?,?";
		Parameters p  =new Parameters().add(1, _order_id).add(2, _line_id);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_stock_check_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				_order_row = result.Value;
				
				if (result.HasError){
					App.Current.showError(pn_stock_check_editor.this.getContext(), result.Error);
					return;
				}
				
				if (_order_row == null) {
					App.Current.showError(pn_stock_check_editor.this.getContext(), "盘点明细已不存在。");
					return;
				}
				
				String item_code = _order_row.getValue("warehouse_code", "") + ", " + _order_row.getValue("item_code", "") + ", " + _order_row.getValue("onhand_quantity", "") + " " + _order_row.getValue("uom_code", "");
				pn_stock_check_editor.this.txt_stock_check_order_cell.setContentText(_order_row.getValue("code", ""));
				pn_stock_check_editor.this.txt_item_code_cell.setContentText(item_code);
				pn_stock_check_editor.this.txt_item_name_cell.setContentText(_order_row.getValue("item_name", ""));
				pn_stock_check_editor.this.txt_summary_cell.setContentText(_order_row.getValue("summary", ""));
			}
		});
	}
	
	public void loadLotNumber(String lot)
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_mm_stock_check_get_lot_v1 ?,?,?,?";
		Parameters p  =new Parameters().add(1, _order_id).add(2, _line_id).add(3, lot).add(4, App.Current.UserID);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_stock_check_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_stock_check_editor.this.getContext(), result.Error);
					return;
				}
				
				_lot_row = result.Value;
				if (_lot_row == null) {
					App.Current.showError(pn_stock_check_editor.this.getContext(), "该批次不存在。");
					return;
				}
				String alert_flag =_lot_row.getValue("alert_flag","");
				if(alert_flag.equals("T"))
				{
					BigDecimal total_quantity =new BigDecimal(_lot_row.getValue("total_quantity",""));;
					BigDecimal onhand_lot_qty ;
					BigDecimal sum_qty ;
					BigDecimal scan_quantity=new BigDecimal(_scan_quantity) ;
			
					onhand_lot_qty= _lot_row.getValue("onhand_lot_qty",BigDecimal.ZERO);
					sum_qty =total_quantity.add(scan_quantity);
					if (sum_qty.compareTo(onhand_lot_qty)>0)
					{
						App.Current.question(pn_stock_check_editor.this.getContext(),"批次库存:["+onhand_lot_qty.toString()+"]小于当前盘点数量["+sum_qty.toString()+"]:{已盘"+total_quantity.toString()+"+"+_scan_quantity+"}！请确认是否重复扫描！" , new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int which) {
								pn_stock_check_editor.this.txt_vendor_name_cell.setContentText(_lot_row.getValue("vendor_name", ""));
								pn_stock_check_editor.this.txt_vendor_model_cell.setContentText(_lot_row.getValue("vendor_model", ""));
								pn_stock_check_editor.this.txt_vendor_lot_cell.setContentText(_lot_row.getValue("vendor_lot", ""));
								pn_stock_check_editor.this.txt_date_code_cell.setContentText(_lot_row.getValue("date_code", ""));
								pn_stock_check_editor.this.txt_lot_number_cell.setContentText(_lot_row.getValue("lot_number", ""));
								pn_stock_check_editor.this.txt_location_cell.setContentText(_lot_row.getValue("locations", ""));
								pn_stock_check_editor.this.txt_total_quantity_cell.setContentText(_lot_row.getValue("total_quantity", ""));
								pn_stock_check_editor.this.txt_quantity_cell.setContentText(_scan_quantity);
							}
						});
					}else
					{
						pn_stock_check_editor.this.txt_vendor_name_cell.setContentText(_lot_row.getValue("vendor_name", ""));
						pn_stock_check_editor.this.txt_vendor_model_cell.setContentText(_lot_row.getValue("vendor_model", ""));
						pn_stock_check_editor.this.txt_vendor_lot_cell.setContentText(_lot_row.getValue("vendor_lot", ""));
						pn_stock_check_editor.this.txt_date_code_cell.setContentText(_lot_row.getValue("date_code", ""));
						pn_stock_check_editor.this.txt_lot_number_cell.setContentText(_lot_row.getValue("lot_number", ""));
						pn_stock_check_editor.this.txt_location_cell.setContentText(_lot_row.getValue("locations", ""));
						pn_stock_check_editor.this.txt_total_quantity_cell.setContentText(_lot_row.getValue("total_quantity", ""));
						pn_stock_check_editor.this.txt_quantity_cell.setContentText(_scan_quantity);	
					
					}
				}
				else
				{
				
				pn_stock_check_editor.this.txt_vendor_name_cell.setContentText(_lot_row.getValue("vendor_name", ""));
				pn_stock_check_editor.this.txt_vendor_model_cell.setContentText(_lot_row.getValue("vendor_model", ""));
				pn_stock_check_editor.this.txt_vendor_lot_cell.setContentText(_lot_row.getValue("vendor_lot", ""));
				pn_stock_check_editor.this.txt_date_code_cell.setContentText(_lot_row.getValue("date_code", ""));
				pn_stock_check_editor.this.txt_lot_number_cell.setContentText(_lot_row.getValue("lot_number", ""));
				pn_stock_check_editor.this.txt_location_cell.setContentText(_lot_row.getValue("locations", ""));
				pn_stock_check_editor.this.txt_total_quantity_cell.setContentText(_lot_row.getValue("total_quantity", ""));
				pn_stock_check_editor.this.txt_quantity_cell.setContentText(_scan_quantity);
				}
			}
		});
	}
	
	@Override
	public void commit()
	{
		if (_order_row == null) {
			App.Current.showError(this.getContext(), "没有盘点单数据，不能提交。");
			return;
		}
		
		if (_lot_row == null) {
			App.Current.showError(this.getContext(), "没有批次数据，不能提交。");
			return;
		}
		
		final Long order_id = _order_row.getValue("id", Long.class);
		final String order_code = _order_row.getValue("code", "");
		final Integer order_line_id = _order_row.getValue("line_id", Integer.class);
		final String quantity = this.txt_quantity_cell.getContentText();
		final String locations =this.txt_location_cell.getContentText();
		if (quantity == null || quantity.length() == 0) {
			App.Current.showError(this.getContext(), "没有输入数量，不能提交。");
			return;
		}
		if (locations==null ||locations.length()==0)
		{
			App.Current.showError(this.getContext(), "没有扫描储位，不能提交。");
			return;
		}
		
		Map<String, String> entry = new HashMap<String, String>();
		entry.put("user_id", App.Current.UserID);
		entry.put("order_id", String.valueOf(order_id));
		entry.put("order_line_id", String.valueOf(order_line_id));
		entry.put("lot_number", _lot_row.getValue("lot_number",""));
		entry.put("vendor_id", String.valueOf(_lot_row.getValue("vendor_id", 0)));
		entry.put("vendor_lot", _lot_row.getValue("vendor_lot",""));
		entry.put("vendor_model", _lot_row.getValue("vendor_model",""));
		entry.put("date_code", _lot_row.getValue("date_code",""));
		entry.put("locations", locations);
		entry.put("quantity", quantity);

		//生成XML数据，并传给存储过程
		String xml = XmlHelper.createXml("stock_check_lot", entry, null, null, null);
		String sql = "exec p_mm_stock_check_commit_lot ?,?";
		Connection conn = App.Current.DbPortal.CreateConnection(pn_stock_check_editor.this.Connector);
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
					App.Current.showError(pn_stock_check_editor.this.getContext(), rs.Error);
					return;
				}
				
				App.Current.toastInfo(pn_stock_check_editor.this.getContext(), "提交成功");
				
				pn_stock_check_editor.this.clear();
				pn_stock_check_editor.this.loadOrderItem();
			}
		} catch (SQLException e) {
			App.Current.showInfo(pn_stock_check_editor.this.getContext(), e.getMessage());
			e.printStackTrace();
			pn_stock_check_editor.this.clear();
		}
	}
	
	public void clear()
	{
		_lot_row = null;
		pn_stock_check_editor.this.txt_vendor_name_cell.setContentText("");
		pn_stock_check_editor.this.txt_vendor_lot_cell.setContentText("");
		pn_stock_check_editor.this.txt_vendor_model_cell.setContentText("");
		pn_stock_check_editor.this.txt_lot_number_cell.setContentText("");
		pn_stock_check_editor.this.txt_date_code_cell.setContentText("");
		pn_stock_check_editor.this.txt_location_cell.setContentText("");
		pn_stock_check_editor.this.txt_quantity_cell.setContentText("");
		pn_stock_check_editor.this.txt_total_quantity_cell.setContentText("");
	}
}
