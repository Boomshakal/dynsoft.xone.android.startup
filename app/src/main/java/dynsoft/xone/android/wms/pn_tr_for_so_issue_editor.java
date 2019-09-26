package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class pn_tr_for_so_issue_editor extends pn_editor {

	public pn_tr_for_so_issue_editor(Context context) {
		super(context);
	}

	public TextCell txt_tr_order_cell;
	public ButtonTextCell txt_item_code_cell;
	public TextCell txt_item_name_cell;
	public TextCell txt_vendor_name_cell;
	public TextCell txt_date_code_cell;
	public TextCell txt_lot_number_cell;
	public TextCell txt_quantity_cell;
	public ButtonTextCell txt_issue_quantity_cell;
	public TextCell txt_location_cell;
	public TextCell txt_onhand_cell;
	public ButtonTextCell txt_surplus_cell;
	public ImageButton btn_prev;
	public ImageButton btn_next;
	public ImageButton btn_print;
	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_tr_for_so_issue_editor, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_tr_order_cell = (TextCell)this.findViewById(R.id.txt_tr_order_cell);
		this.txt_item_code_cell = (ButtonTextCell)this.findViewById(R.id.txt_item_code_cell);
		this.txt_item_name_cell = (TextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_vendor_name_cell = (TextCell)this.findViewById(R.id.txt_vendor_name_cell);
		this.txt_date_code_cell = (TextCell)this.findViewById(R.id.txt_date_code_cell);
		this.txt_lot_number_cell = (TextCell)this.findViewById(R.id.txt_lot_number_cell);
		this.txt_quantity_cell = (TextCell)this.findViewById(R.id.txt_quantity_cell);
		this.txt_issue_quantity_cell = (ButtonTextCell)this.findViewById(R.id.txt_issue_quantity_cell);
		this.txt_location_cell = (TextCell)this.findViewById(R.id.txt_location_cell);
		this.txt_onhand_cell = (TextCell)this.findViewById(R.id.txt_onhand_cell);
		this.txt_surplus_cell = (ButtonTextCell)this.findViewById(R.id.txt_surplus_cell);
		this.btn_prev = (ImageButton)this.findViewById(R.id.btn_prev);
		this.btn_next = (ImageButton)this.findViewById(R.id.btn_next);
		this.btn_print = (ImageButton)this.findViewById(R.id.btn_print);
		
		if (this.txt_tr_order_cell != null) {
			this.txt_tr_order_cell.setLabelText("申请单号");
			this.txt_tr_order_cell.setReadOnly();
		}
		
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("物料编码");
			this.txt_item_code_cell.setReadOnly();
			this.txt_item_code_cell.Button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_tr_for_so_issue_editor.this.showStockQuantity();
				}
			});
		}
		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("物料名称");
			this.txt_item_name_cell.setReadOnly();
			this.txt_item_name_cell.TextBox.setSingleLine(true);
		}
		
		if (this.txt_vendor_name_cell != null) {
			this.txt_vendor_name_cell.setLabelText("转出SN");
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
		
		if (this.txt_issue_quantity_cell != null) {
			this.txt_issue_quantity_cell.setLabelText("转移数量");
			this.txt_issue_quantity_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_label_light"));
			this.txt_issue_quantity_cell.TextBox.addTextChangedListener(new TextWatcher(){
				@Override
				public void afterTextChanged(Editable s) {
					DataRow row = (DataRow)pn_tr_for_so_issue_editor.this.txt_onhand_cell.getTag();
					if (row != null) {
						String str = s.toString();
						if (str == null || str.length() == 0) {
							str = "0";
						}
						BigDecimal onhand_quantity = row.getValue("quantity", BigDecimal.ZERO);
						BigDecimal issue_quantity = new BigDecimal(str);
						BigDecimal surplus = onhand_quantity.subtract(issue_quantity);
						pn_tr_for_so_issue_editor.this.txt_surplus_cell.setContentText(App.formatNumber(surplus, "0.##"));
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,int count, int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start,int before, int count) {
				}
			});
			
			this.txt_issue_quantity_cell.Button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					String qty = pn_tr_for_so_issue_editor.this.txt_issue_quantity_cell.getContentText();
					pn_tr_for_so_issue_editor.this.printLabel(qty);
				}
			});
		}
		
		if (this.txt_location_cell != null) {
			this.txt_location_cell.setLabelText("转入储位");
			this.txt_location_cell.setReadOnly();
		}
		
		if (this.txt_onhand_cell != null) {
			this.txt_onhand_cell.setLabelText("现有量");
			this.txt_onhand_cell.setReadOnly();
		}
		
		if (this.txt_surplus_cell != null) {
			this.txt_surplus_cell.setLabelText("剩余量");
			this.txt_surplus_cell.setReadOnly();
			this.txt_surplus_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_label_light"));
			this.txt_surplus_cell.Button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					String qty = pn_tr_for_so_issue_editor.this.txt_surplus_cell.getContentText();
					pn_tr_for_so_issue_editor.this.printLabel(qty);
				}
			});
		}
		
		if (this.btn_prev != null) {
			this.btn_prev.setImageBitmap(App.Current.ResourceManager.getImage("@/core_prev_white"));
			this.btn_prev.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_tr_for_so_issue_editor.this.prev();
				}
			});
		}
		
		if (this.btn_next != null) {
			this.btn_next.setImageBitmap(App.Current.ResourceManager.getImage("@/core_next_white"));
			this.btn_next.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_tr_for_so_issue_editor.this.next();
				}
			});
		}
		if (this.btn_print != null) {
			this.btn_print.setImageBitmap(App.Current.ResourceManager.getImage("@/core_print_white"));
			this.btn_print.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_tr_for_so_issue_editor.this.print_status();
				}
			});
		}
		_order_id = this.Parameters.get("order_id", 0L);
		_order_code = this.Parameters.get("order_code", "");
		if (_order_code == null || _order_code.length() == 0) {
			this.close();
			return;
		}
		
		this.txt_tr_order_cell.setContentText(_order_code);
		_line_id = this.Parameters.get("line_id", 0);
		if (_line_id > 0) {
			String sql = "exec p_mm_tr_issue_get_row_number ?,?,?";
			Parameters p = new Parameters().add(1, App.Current.UserID).add(2, _order_id).add(3, _line_id);
			Result<Long> r = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, Long.class);
			if (r.HasError) {
				App.Current.showError(pn_tr_for_so_issue_editor.this.getContext(), "没有指定转移申请编号。");
				this.close();
			}
			
			if (r.Value != null && r.Value > 0L) {
				_rownum = r.Value;
				this.loadTaskOrderItem(_rownum);
			}
		} else {
			this.close();
			return;
		}
	}
	
	private Long _order_id;
	private Integer _line_id;
	private String _order_code;
	private Long _rownum;
	private Integer _total;
	private String from_war_code;
	private String to_war_code;
	private String store_keeper;
	private String tr_date;
	private String comment;
	
	public void prev()
	{
		if (_rownum >1 ) {
			this.loadTaskOrderItem(_rownum - 1);
		} else {
			App.Current.showError(pn_tr_for_so_issue_editor.this.getContext(), "已经是第一条。");
		}
	}
	
	public void next()
	{
		if (_rownum < _total) {
			this.loadTaskOrderItem(_rownum + 1);
		} else {
			App.Current.showError(pn_tr_for_so_issue_editor.this.getContext(), "已经是最后一条。");
		}
	}
	
	private String _scan_quantity;
	
	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		
		//扫描批次条码
		if (bar_code.startsWith("CRQ:")) {
			String str = bar_code.substring(4, barcode.length());
			String[] arr = str.split("-");
			String lot_number = arr[0];
			_scan_quantity = arr[2];
			if (_scan_quantity != null || _scan_quantity.length() > 0) {
				this.loadLotNumber(lot_number);
			}
		}

		if (bar_code.startsWith("C:")) {
			String lot = bar_code.substring(2, bar_code.length());
			this.loadLotNumber(lot);
		}
		
		if (bar_code.startsWith("L:")){
			String loc = bar_code.substring(2, bar_code.length());
			this.txt_location_cell.setContentText(loc);
		}
	}
	
	private DataRow _order_row;
	
	public void loadTaskOrderItem(long index)
	{
		this.ProgressDialog.show();
		
		String code = this.txt_tr_order_cell.getContentText();
		if (code == null || code.length() == 0) {
			App.Current.showError(pn_tr_for_so_issue_editor.this.getContext(), "没有指定转出申请编号。");
			return;
		}
		
		String sql = "exec p_mm_tr_issue_get_item ?,?,?";
		Parameters p  =new Parameters().add(1, App.Current.UserID).add(2, _order_id).add(3, index);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_tr_for_so_issue_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_tr_for_so_issue_editor.this.getContext(), result.Error);
					return;
				}
				
				_order_row = result.Value;
				if (_order_row == null) {
					App.Current.showError(pn_tr_for_so_issue_editor.this.getContext(), "没有数据。");
					pn_tr_for_so_issue_editor.this.clearAll();
					pn_tr_for_so_issue_editor.this.Header.setTitleText("库存转移");
					return;
				}
				
				_total = _order_row.getValue("total", Integer.class);
				_rownum = _order_row.getValue("rownum", Long.class);
				if (_total > 0 ){
					pn_tr_for_so_issue_editor.this.Header.setTitleText("库存转移(共" + String.valueOf(_total) + "条)");
				} else {
					pn_tr_for_so_issue_editor.this.Header.setTitleText("库存转移");
				}

				String order_code = _order_row.getValue("code", "") + ", 第" + String.valueOf(_rownum) + "条";
				String item_code = _order_row.getValue("item_code", "") + ", 从" + _order_row.getValue("warehouse_code","") + "(" + _order_row.getValue("stock_quantity", "") + ") 到" + _order_row.getValue("to_warehouse_code", "");
				from_war_code =_order_row.getValue("warehouse_code","");
			    to_war_code=_order_row.getValue("to_warehouse_code", "");
			    tr_date="";
			    comment=_order_row.getValue("comment","");
			    store_keeper=_order_row.getValue("store_keeper_name", "");
				pn_tr_for_so_issue_editor.this.txt_tr_order_cell.setContentText(order_code);
				pn_tr_for_so_issue_editor.this.txt_item_code_cell.setContentText(item_code);
				pn_tr_for_so_issue_editor.this.txt_item_name_cell.setContentText(_order_row.getValue("item_name", ""));
				pn_tr_for_so_issue_editor.this.txt_vendor_name_cell.setContentText(_order_row.getValue("vendor_name", ""));
				pn_tr_for_so_issue_editor.this.txt_date_code_cell.setContentText(_order_row.getValue("date_code", ""));
				
				BigDecimal planned_qty = _order_row.getValue("quantity", BigDecimal.ZERO);
				BigDecimal issued_qty = _order_row.getValue("issued_quantity", BigDecimal.ZERO);
				BigDecimal open_qty = planned_qty.subtract(issued_qty);
				
				String qty_str = "总数:" + App.formatNumber(planned_qty, "0.##");
				qty_str += "/已转:" + App.formatNumber(issued_qty, "0.##");
				qty_str += "/未转:" + App.formatNumber(open_qty, "0.##");
				
				pn_tr_for_so_issue_editor.this.txt_quantity_cell.setContentText(qty_str);
				pn_tr_for_so_issue_editor.this.txt_location_cell.setContentText(_order_row.getValue("locations", ""));
				
				pn_tr_for_so_issue_editor.this.txt_lot_number_cell.setTag(null);
				pn_tr_for_so_issue_editor.this.txt_lot_number_cell.setContentText("");
				pn_tr_for_so_issue_editor.this.txt_issue_quantity_cell.setContentText("");
				pn_tr_for_so_issue_editor.this.txt_onhand_cell.setContentText("");
				pn_tr_for_so_issue_editor.this.txt_onhand_cell.setTag(null);
				pn_tr_for_so_issue_editor.this.txt_surplus_cell.setContentText("");
				pn_tr_for_so_issue_editor.this.txt_location_cell.setContentText(_order_row.getValue("locations",""));
			}
		});
	}
	
	public void showStockQuantity()
	{
		if (_order_row == null) {
			App.Current.showError(this.getContext(), "没有转移申请数据，不能提交。");
			return;
		}
		
		Integer item_id = _order_row.getValue("item_id", Integer.class);
		Integer warehouse_id = _order_row.getValue("warehouse_id", 0);
		
		String sql = "select (select top 1 locations from mm_stock_lot where item_id=? and warehouse_id=? order by date_code) locations,";
		sql += "(SELECT cast(cast(quantity as real) as varchar)+' '+uom_code FROM dbo.mm_stock_item WHERE item_id=? AND warehouse_id=?) quantity";
		Parameters p = new Parameters().add(1, item_id).add(2, warehouse_id).add(3, item_id).add(4, warehouse_id);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
			public void handleMessage(Message msg) {
				Result<DataRow> r = this.Value;
				if (r.Value != null) {
					DataRow row = r.Value;
					String str = "库存：" + row.getValue("quantity", "") + "\n";
					str += "储位：" + row.getValue("locations", "");
					App.Current.showInfo(pn_tr_for_so_issue_editor.this.getContext(), str);
				}
			}
		});
	}
	
	public void loadLotNumber(String lotNumber)
	{
		if (_order_row == null) {
			App.Current.toastError(pn_tr_for_so_issue_editor.this.getContext(), "缺少转移申请数据");
			return;
		}

		final long order_id = _order_row.getValue("id", Long.class);
		final int line_id = _order_row.getValue("line_id", Integer.class);

		this.ProgressDialog.show();
		String sql = "exec p_mm_tr_issue_get_lot_number ?,?,?";
		Parameters p = new Parameters().add(1, order_id).add(2, line_id).add(3, lotNumber);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
			public void handleMessage(Message msg) {
				
				pn_tr_for_so_issue_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> r = this.Value;
				if (r.HasError) {
					App.Current.showError(pn_tr_for_so_issue_editor.this.getContext(), r.Error);
					return;
				}
				
				String result = r.Value.getValue("result", "");
				if (result.length()>0) {
					App.Current.showError(pn_tr_for_so_issue_editor.this.getContext(), result);
					return;
				}
				
				BigDecimal issue_quantity = BigDecimal.ZERO;
				BigDecimal scan_quantity = new BigDecimal(_scan_quantity);
				BigDecimal open_quantity = _order_row.getValue("open_quantity", BigDecimal.ZERO);
				BigDecimal onhand_quantity = r.Value.getValue("quantity", BigDecimal.ZERO);
				
				issue_quantity = scan_quantity;
				if (open_quantity.compareTo(scan_quantity) < 0){
					issue_quantity = open_quantity;
				}
				
				if (onhand_quantity.compareTo(issue_quantity) < 0) {
					issue_quantity = onhand_quantity;
				}
				
				pn_tr_for_so_issue_editor.this.txt_vendor_name_cell.setContentText(r.Value.getValue("vendor_name", ""));
				pn_tr_for_so_issue_editor.this.txt_vendor_name_cell.setTag(r.Value.getValue("vendor_id"));
				pn_tr_for_so_issue_editor.this.txt_lot_number_cell.setTag(r.Value);
				pn_tr_for_so_issue_editor.this.txt_lot_number_cell.setContentText(r.Value.getValue("lot_number", ""));
				pn_tr_for_so_issue_editor.this.txt_date_code_cell.setContentText(r.Value.getValue("date_code", ""));
				pn_tr_for_so_issue_editor.this.txt_location_cell.setContentText(r.Value.getValue("locations", ""));
				pn_tr_for_so_issue_editor.this.txt_onhand_cell.setTag(r.Value);
				pn_tr_for_so_issue_editor.this.txt_onhand_cell.setContentText(App.formatNumber(onhand_quantity, "0.##"));
				pn_tr_for_so_issue_editor.this.txt_issue_quantity_cell.setContentText(App.formatNumber(issue_quantity, "0.##"));
			}
		});
		
	}
	
	public void print_status() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("code", this._order_code);
		parameters.put("wo_code", "");
		parameters.put("from_war_code", from_war_code);
		parameters.put("to_war_code", to_war_code);
		parameters.put("store_keeper", store_keeper);
		parameters.put("tr_date", tr_date);
		App.Current.Print("mm_win_allocation_states_brand", "打印调拨状态牌标签", parameters);
		;
	}
	
	public void printLabel(String quantity)
	{
		if (quantity == null || quantity.length() == 0) {
			App.Current.showError(this.getContext(), "没有指定标签数量。");
			return;
		}
		
		if (_order_row == null) {
			App.Current.showError(this.getContext(), "没有转移申请数据。");
			return;
		}
		
		final DataRow lot_row = (DataRow)this.txt_lot_number_cell.getTag();
		if (lot_row == null) {
			App.Current.showError(this.getContext(), "没有批号数据。");
			return;
		}

		Intent intent = new Intent();
        intent.setClass(this.getContext(), frm_item_lot_printer.class);
        intent.putExtra("org_code", lot_row.getValue("org_code", ""));
        intent.putExtra("item_code", lot_row.getValue("item_code", ""));
        intent.putExtra("vendor_model", lot_row.getValue("vendor_model", ""));
        intent.putExtra("lot_number", lot_row.getValue("lot_number", ""));
        intent.putExtra("vendor_lot", lot_row.getValue("vendor_lot", ""));
        intent.putExtra("date_code", lot_row.getValue("date_code", ""));
        intent.putExtra("quantity", quantity);
        intent.putExtra("ut", _order_row.getValue("uom_code", ""));
        
        this.getContext().startActivity(intent);
	}
	
	@Override
	public void commit()
	{
		if (_order_row == null) {
			App.Current.showError(this.getContext(), "没有转移申请数据，不能提交。");
			return;
		}
		
		final DataRow lot_row = (DataRow)this.txt_lot_number_cell.getTag();
		if (lot_row == null) {
			App.Current.showError(this.getContext(), "没有批号数据，不能提交。");
			return;
		}
		
		final String lot_number = this.txt_lot_number_cell.getContentText();
		if (lot_number == null || lot_number.length() == 0) {
			App.Current.showError(this.getContext(), "没有批号数据，不能提交。");
			return;
		}
		
		final String issue = this.txt_issue_quantity_cell.getContentText();
		if (issue == null || issue.length() == 0) {
			App.Current.showError(this.getContext(), "没有输入转移数量，不能提交。");
			return;
		}
		
		final BigDecimal issue_quantity = new BigDecimal(issue);
		if (issue_quantity.equals(BigDecimal.ZERO)){
			App.Current.showError(this.getContext(), "转移数量为0，不能提交。");
			return;
		}
		
		final Long order_id = _order_row.getValue("id", Long.class);
		final String order_code = _order_row.getValue("code", "");
		final int order_line_id = _order_row.getValue("line_id", 0);
		final int organization_id = _order_row.getValue("org_id", 0);
		final String locations = pn_tr_for_so_issue_editor.this.txt_location_cell.getContentText();
		final Integer vendor_id = lot_row.getValue("vendor_id", Integer.class);
		final String date_code = lot_row.getValue("date_code", "");
		

		App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				
				Map<String, String> entry = new HashMap<String, String>();
				entry.put("code", order_code);
				entry.put("create_user", App.Current.UserID);
				entry.put("order_id", String.valueOf(order_id));
				entry.put("order_line_id", String.valueOf(order_line_id));
				entry.put("organization_id", String.valueOf(organization_id));
				entry.put("item_id", String.valueOf(_order_row.getValue("item_id", Integer.class)));
				entry.put("quantity", String.valueOf(issue_quantity));
				entry.put("uom_code", _order_row.getValue("uom_code", ""));
				entry.put("lot_number", lot_number);
				entry.put("date_code", date_code);
				entry.put("vendor_id", String.valueOf(vendor_id));
				entry.put("vendor_model", lot_row.getValue("vendor_model", ""));
				entry.put("vendor_lot", _order_row.getValue("vendor_lot", ""));
				entry.put("warehouse_id", String.valueOf(_order_row.getValue("warehouse_id", 0)));
				entry.put("locations", locations);
				
				ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
				entries.add(entry);
				
				//生成XML数据，并传给存储过程
				String xml = XmlHelper.createXml("tr_issues", null, null, "tr_issue", entries);
				String sql = "exec p_mm_tr_issue_create ?,?";
				Connection conn = App.Current.DbPortal.CreateConnection(pn_tr_for_so_issue_editor.this.Connector);
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
							App.Current.showError(pn_tr_for_so_issue_editor.this.getContext(), rs.Error);
							return;
						}
						
						_order_row = null;
						App.Current.toastInfo(pn_tr_for_so_issue_editor.this.getContext(), "提交成功");
						pn_tr_for_so_issue_editor.this.clear();
						pn_tr_for_so_issue_editor.this.loadTaskOrderItem(_rownum);
					} else {
						_order_row = null;
					}
				} catch (SQLException e) {
					_order_row = null;
					App.Current.showInfo(pn_tr_for_so_issue_editor.this.getContext(), e.getMessage());
					e.printStackTrace();
					
					pn_tr_for_so_issue_editor.this.clear();
					pn_tr_for_so_issue_editor.this.loadTaskOrderItem(_rownum);
				}
				
			}
		});
	}
	
	public void clear()
	{
		this.txt_vendor_name_cell.setContentText("");
		this.txt_date_code_cell.setContentText("");
		this.txt_lot_number_cell.setContentText("");
		this.txt_location_cell.setContentText("");
		this.txt_issue_quantity_cell.setContentText("0");
	}
	
	public void clearAll()
	{
		this.txt_tr_order_cell.setContentText("");
		this.txt_item_code_cell.setContentText("");
		this.txt_item_name_cell.setContentText("");
		this.txt_quantity_cell.setContentText("");
		this.txt_vendor_name_cell.setContentText("");
		this.txt_date_code_cell.setContentText("");
		this.txt_lot_number_cell.setContentText("");
		this.txt_location_cell.setContentText("");
		this.txt_issue_quantity_cell.setContentText("0");
		this.txt_surplus_cell.setContentText("");
		this.txt_onhand_cell.setContentText("");
	}
}
