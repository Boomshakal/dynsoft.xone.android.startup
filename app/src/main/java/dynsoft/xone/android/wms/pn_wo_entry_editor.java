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

import dynsoft.xone.android.adapter.PageTableAdapter;
import dynsoft.xone.android.adapter.TableAdapter;
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
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class pn_wo_entry_editor extends pn_editor {

	public pn_wo_entry_editor(Context context) {
		super(context);
	}

	public TextCell txt_wo_entry_order_cell;
	public TextCell txt_item_code_cell;
	public TextCell txt_item_name_cell;
	public TextCell txt_lot_number_cell;
	public DecimalCell txt_quantity_cell;
	public TextCell txt_date_code_cell;
	public TextCell txt_location_cell;
	public TextCell txt_organization_cell;
	public TextCell txt_warehouse_cell;
	public ListView Matrix;
	public TableAdapter Adapter;

	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_wo_entry_order_cell = (TextCell)this.findViewById(R.id.txt_wo_entry_order_cell);
		this.txt_item_code_cell = (TextCell)this.findViewById(R.id.txt_item_code_cell);
		this.txt_item_name_cell = (TextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_lot_number_cell = (TextCell)this.findViewById(R.id.txt_lot_number_cell);
		this.txt_date_code_cell = (TextCell)this.findViewById(R.id.txt_date_code_cell);
		this.txt_quantity_cell = (DecimalCell)this.findViewById(R.id.txt_quantity_cell);
		this.txt_location_cell = (TextCell)this.findViewById(R.id.txt_location_cell);
		this.txt_organization_cell = (TextCell)this.findViewById(R.id.txt_organization_cell);
		this.txt_warehouse_cell = (TextCell)this.findViewById(R.id.txt_warehouse_cell);
		this.Matrix = (ListView)this.findViewById(R.id.matrix);
		
		if (this.txt_wo_entry_order_cell != null) {
			this.txt_wo_entry_order_cell.setLabelText("申请编号");
			this.txt_wo_entry_order_cell.setReadOnly();
		}
		
		if (this.txt_organization_cell != null) {
			this.txt_organization_cell.setLabelText("组织");
			this.txt_organization_cell.setReadOnly();
		}
		
		if (this.txt_warehouse_cell != null) {
			this.txt_warehouse_cell.setLabelText("库位");
			this.txt_warehouse_cell.setReadOnly();
		}
		
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("物料编码");
			this.txt_item_code_cell.setReadOnly();
		}
		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("物料名称");
			this.txt_item_name_cell.setReadOnly();
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
		
		if (this.txt_location_cell != null) {
			this.txt_location_cell.setLabelText("储位");
		}
		
		if (this.Matrix != null) {
        	this.Matrix.setCacheColorHint(Color.TRANSPARENT);
            LinearLayout.LayoutParams lp_listview = new LinearLayout.LayoutParams(-1,10);
            this.Matrix.setLayoutParams(lp_listview);
            this.Adapter = new TableAdapter(this.getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (Adapter.DataTable != null) {
                    	DataRow row = (DataRow)Adapter.getItem(position);
                    	
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
                    return null;
                }
            };
            
            this.Matrix.setAdapter(Adapter);
        }

		_order_id = this.Parameters.get("order_id", 0L);
		_order_code = this.Parameters.get("order_code", "");
		this.loadOrderCount();
	}
	
	private Long _order_id;
	private String _order_code;
	private DataRow _order_row;
	
	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_wo_entry_editor, this, true);
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
		else if (bar_code.startsWith("TO2:")) {
			String lot = bar_code.split("\\^")[1];
			this.txt_lot_number_cell.setContentText(lot);
			this.loadOrderItem(lot);
		}

		else if (bar_code.startsWith("C:")){
			String lot = bar_code.substring(2, bar_code.length());
			this.txt_lot_number_cell.setContentText(lot);
			this.loadOrderItem(lot);
		}
		
		else if (bar_code.startsWith("L:")){
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
		
		else if (bar_code.startsWith("TL:")) {
			String tl = bar_code.substring(3);
			if (tl.length() > 0) {
				this.loadTransferLocation(tl);
				this.txt_location_cell.setContentText(tl);
			}
		}
		
		else if (bar_code.startsWith("LC:")) {
			String tl = bar_code.substring(3);
			if (tl.length() > 0) {
				this.loadTransferLocation(tl);
			}
		}else
		{
			loadTaskLot(bar_code);
		}
	}
	
	
	private String lot_number;
	
	public void loadTaskLot(String sn_no)
	{
		String sql = "exec p_mm_wo_order_entry_get_item_from_sn ?";
		Parameters p = new Parameters().add(1, sn_no);
		final Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql,p);
		if (r.HasError) {
			App.Current.showError(this.getContext(), r.Error);
		}
		
		if (r.Value != null && r.Value.Rows.size() > 0) {
			if (r.Value.Rows.size() > 1) {
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which >= 0) {
                        	DataRow row = r.Value.Rows.get(which);
                        	lot_number =row.getValue("lot_number","");
            				pn_wo_entry_editor.this.loadOrderItem(lot_number);
                        }
                        dialog.dismiss();
                    }
                };

                final TableAdapter adapter = new TableAdapter(this.getContext()) {
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
                        String qty = row.getValue("task_order_code", "")+","+row.getValue("item_code", "") + ", " + App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##") ;
                        
                        txt_lot_number.setText(lot_number);
                        txt_quantity.setText(qty);

                        return convertView;
                    }
                };
                
                adapter.DataTable = r.Value;
                adapter.notifyDataSetChanged();
                
                new AlertDialog.Builder(this.getContext())
                .setTitle("选择入库批次")
                .setSingleChoiceItems(adapter, 0, listener)
                .setNegativeButton("取消", null).show();
			} else {
				DataRow row = r.Value.Rows.get(0);
				lot_number =row.getValue("lot_number","");
				this.loadOrderItem(lot_number);
			}
		} else 
		{
			 App.Current.showError(this.getContext(), "非法条码，请扫描正确的条码！");
		}
	}
	
	public void loadOrderCount()
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_mm_wo_entry_get_item_count ?,?";
		Parameters p = new Parameters().add(1, App.Current.UserID).add(2, _order_id);
		App.Current.DbPortal.ExecuteScalarAsync(this.Connector, sql, p, Integer.class, new ResultHandler<Integer>(){
			@Override
			public void handleMessage(Message msg){
				pn_wo_entry_editor.this.ProgressDialog.dismiss();
				
				Result<Integer> result = this.Value;
				if (this.Value.HasError) {
					App.Current.showError(pn_wo_entry_editor.this.getContext(), result.Error);
					return;
				}
				
				if (result.Value > 0) {
					String info = "工单缴库("+ String.valueOf("共" + result.Value) +"条)";
					pn_wo_entry_editor.this.Header.setTitleText(info);
				} else {
					pn_wo_entry_editor.this.Header.setTitleText("工单缴库");
					pn_wo_entry_editor.this.clearAll();
				}
			}
		});
	}
	
	public void loadOrderItem(String lot_number)
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_mm_wo_entry_get_item ?,?,?";
		Parameters p  =new Parameters().add(1, App.Current.UserID).add(2, _order_id).add(3, lot_number);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_wo_entry_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError){
					App.Current.showError(pn_wo_entry_editor.this.getContext(), result.Error);
					return;
				}
				
				pn_wo_entry_editor.this.showOrderItem(result.Value);
			}
		});
	}
	
	public void loadTransferLocation(String tl)
	{
		String sql = "exec p_mm_wo_entry_get_item_by_transfer_location ?,?,?";
		Parameters p = new Parameters().add(1, App.Current.UserID).add(2, _order_id).add(3, tl);
		final Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql,p);
		if (r.HasError) {
			App.Current.showError(this.getContext(), r.Error);
		}
		
		if (r.Value != null && r.Value.Rows.size() > 0) {
			if (r.Value.Rows.size() > 1) {
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which >= 0) {
                        	DataRow row = r.Value.Rows.get(which);
                        	pn_wo_entry_editor.this.showOrderItem(row);
                        }
                        dialog.dismiss();
                    }
                };

                final TableAdapter adapter = new TableAdapter(this.getContext()) {
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
                        String qty = row.getValue("code", "")+","+row.getValue("date_code", "") + ", " + App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##") + " " + row.getValue("uom_code", "");
                        
                        txt_lot_number.setText(lot_number);
                        txt_quantity.setText(qty);

                        return convertView;
                    }
                };
                
                adapter.DataTable = r.Value;
                adapter.notifyDataSetChanged();
                
                new AlertDialog.Builder(this.getContext())
                .setTitle("选择入库批次")
                .setSingleChoiceItems(adapter, 0, listener)
                .setNegativeButton("取消", null).show();
			} else {
				DataRow row = r.Value.Rows.get(0);
				this.showOrderItem(row);
			}
		}
		
//		String sql = "exec p_mm_wo_entry_get_item_by_transfer_location ?,?,?";
//		Parameters p = new Parameters().add(1, App.Current.UserID).add(2, _order_id).add(3, tl);
//		final Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql,p);
//		if (r.HasError) {
//			App.Current.showError(this.getContext(), r.Error);
//		}
//		
//		this.Adapter.DataTable = r.Value;
//		this.Adapter.notifyDataSetChanged();
		
	}
	
	private void showOrderItem(DataRow row)
	{
		_order_row = row;
		if (_order_row == null) {
			App.Current.showError(pn_wo_entry_editor.this.getContext(), "待入库数据没有该批号。");
			return;
		}
		
		int total = _order_row.getValue("lines_count", 0);
		if (total > 0) {
			String info = "工单入库("+ String.valueOf(total) +"条待入库)";
			pn_wo_entry_editor.this.Header.setTitleText(info);
		} else {
			pn_wo_entry_editor.this.Header.setTitleText("工单缴库");
		}
		
		pn_wo_entry_editor.this.txt_wo_entry_order_cell.setTag(_order_row);
		pn_wo_entry_editor.this.txt_wo_entry_order_cell.setContentText(_order_row.getValue("code", ""));
		pn_wo_entry_editor.this.txt_item_code_cell.setContentText(_order_row.getValue("item_code", ""));
		pn_wo_entry_editor.this.txt_item_name_cell.setContentText(_order_row.getValue("item_name", ""));
		pn_wo_entry_editor.this.txt_date_code_cell.setContentText(_order_row.getValue("date_code", ""));
		pn_wo_entry_editor.this.txt_lot_number_cell.setContentText(_order_row.getValue("lot_number", ""));
		pn_wo_entry_editor.this.txt_organization_cell.setContentText(_order_row.getValue("organization_code", ""));
		pn_wo_entry_editor.this.txt_warehouse_cell.setContentText(_order_row.getValue("warehouse_code", ""));
		pn_wo_entry_editor.this.txt_quantity_cell.setContentText(App.formatNumber(_order_row.getValue("open_quantity", BigDecimal.ZERO), "0.##"));
	}
	
	@Override
	public void commit()
	{
		if (_order_row == null) {
			App.Current.showError(this.getContext(), "没有工单缴库申请，不能提交。");
			return;
		}
		
		final Long order_id = _order_row.getValue("id", Long.class);
		final String order_code = _order_row.getValue("code", "");
		final Integer order_line_id = _order_row.getValue("line_id", Integer.class);
		
		final String quantity = this.txt_quantity_cell.getContentText();
		if (quantity == null || quantity.length() == 0) {
			App.Current.showError(this.getContext(), "没有输入数量，不能提交。");
			return;
		}
		
		final String locations = (String)this.txt_location_cell.getContentText().trim();
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
				entry.put("quantity", quantity);
				entry.put("locations", locations);
				
				ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
				entries.add(entry);
				
				//生成XML数据，并传给存储过程
				String xml = XmlHelper.createXml("wo_entries", null, null, "wo_entry", entries);
				String sql = "exec p_mm_wo_entry_create ?,?";
				Connection conn = App.Current.DbPortal.CreateConnection(pn_wo_entry_editor.this.Connector);
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
							App.Current.showError(pn_wo_entry_editor.this.getContext(), rs.Error);
							return;
						}
						
						App.Current.toastInfo(pn_wo_entry_editor.this.getContext(), "提交成功");
						
						pn_wo_entry_editor.this.clearAll();
						pn_wo_entry_editor.this.loadOrderCount();
					}
				} catch (SQLException e) {
					App.Current.showInfo(pn_wo_entry_editor.this.getContext(), e.getMessage());
					e.printStackTrace();
					pn_wo_entry_editor.this.clearAll();
				}
			}
		});
	}
	
	public void clearAll()
	{
		pn_wo_entry_editor.this.txt_wo_entry_order_cell.setContentText("");
		pn_wo_entry_editor.this.txt_organization_cell.setContentText("");
		pn_wo_entry_editor.this.txt_warehouse_cell.setContentText("");
		pn_wo_entry_editor.this.txt_item_code_cell.setContentText("");
		pn_wo_entry_editor.this.txt_item_name_cell.setContentText("");
		pn_wo_entry_editor.this.txt_lot_number_cell.setContentText("");
		pn_wo_entry_editor.this.txt_date_code_cell.setContentText("");
		pn_wo_entry_editor.this.txt_quantity_cell.setContentText("");
		//pn_wo_entry_editor.this.txt_location_cell.setContentText("");
	}
}
