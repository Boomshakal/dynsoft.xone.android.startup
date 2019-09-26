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
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class pn_up_entry_editor extends pn_editor {

	public pn_up_entry_editor(Context context) {
		super(context);
	}

	public TextCell txt_up_order_cell;
	public TextCell txt_item_code_cell;
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

	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_up_entry_editor, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_up_order_cell = (TextCell)this.findViewById(R.id.txt_up_order_cell);
		this.txt_item_code_cell = (TextCell)this.findViewById(R.id.txt_item_code_cell);
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
		
		if (this.txt_up_order_cell != null) {
			this.txt_up_order_cell.setLabelText("������");
			this.txt_up_order_cell.setReadOnly();
		}
		
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("���ϱ���");
			this.txt_item_code_cell.setReadOnly();
		}
		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("��������");
			this.txt_item_name_cell.setReadOnly();
			this.txt_item_name_cell.TextBox.setSingleLine(true);
		}
		
		if (this.txt_vendor_name_cell != null) {
			this.txt_vendor_name_cell.setLabelText("��Ӧ��");
			this.txt_vendor_name_cell.setReadOnly();
			this.txt_vendor_name_cell.TextBox.setSingleLine(true);
		}
		
		if (this.txt_date_code_cell != null) {
			this.txt_date_code_cell.setLabelText("D/C");
			this.txt_date_code_cell.setReadOnly();
		}
		
		if (this.txt_lot_number_cell != null) {
			this.txt_lot_number_cell.setLabelText("����");
			this.txt_lot_number_cell.setReadOnly();
		}
		
		if (this.txt_quantity_cell != null) {
			this.txt_quantity_cell.setLabelText("����");
			this.txt_quantity_cell.setReadOnly();
		}
		
		if (this.txt_issue_quantity_cell != null) {
			this.txt_issue_quantity_cell.setLabelText("ʵ������");
			this.txt_issue_quantity_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_label_light"));
			this.txt_issue_quantity_cell.TextBox.addTextChangedListener(new TextWatcher(){
				@Override
				public void afterTextChanged(Editable s) {
					if (_lot_row != null) {
						String str = s.toString();
						if (str == null || str.length() == 0) {
							str = "0";
						}
						
						BigDecimal issue_quantity = new BigDecimal(str);
						BigDecimal onhand_quantity = _lot_row.getValue("quantity", BigDecimal.ZERO);
						BigDecimal surplus = onhand_quantity.subtract(issue_quantity);
						pn_up_entry_editor.this.txt_surplus_cell.setContentText(App.formatNumber(surplus, "0.##"));
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
					String qty = pn_up_entry_editor.this.txt_issue_quantity_cell.getContentText();
					pn_up_entry_editor.this.printLabel(qty, true);
				}
			});
		}
		
		if (this.txt_location_cell != null) {
			this.txt_location_cell.setLabelText("��λ");
			this.txt_location_cell.setReadOnly();
		}
		
		if (this.txt_onhand_cell != null) {
			this.txt_onhand_cell.setLabelText("������");
			this.txt_onhand_cell.setReadOnly();
		}
		
		if (this.txt_surplus_cell != null) {
			this.txt_surplus_cell.setLabelText("ʣ����");
			this.txt_surplus_cell.setReadOnly();
			this.txt_surplus_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_label_light"));
			this.txt_surplus_cell.Button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					String qty = pn_up_entry_editor.this.txt_surplus_cell.getContentText();
					pn_up_entry_editor.this.printLabel(qty, false);
				}
			});
		}
		
		if (this.btn_prev != null) {
			this.btn_prev.setImageBitmap(App.Current.ResourceManager.getImage("@/core_prev_white"));
			this.btn_prev.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_up_entry_editor.this.prev();
				}
			});
		}
		
		if (this.btn_next != null) {
			this.btn_next.setImageBitmap(App.Current.ResourceManager.getImage("@/core_next_white"));
			this.btn_next.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_up_entry_editor.this.next();
				}
			});
		}
		
		_order_id = this.Parameters.get("order_id", 0L);
		_order_code = this.Parameters.get("order_code", "");
		
		this.txt_up_order_cell.setContentText(_order_code);
		this.loadTaskOrderItem(1L);
	}
	
	private Long _rownum;
	private Integer _total = 0;
	private Long _order_id;
	private DataRow _order_row;
	private DataRow _lot_row;
	private String _order_code;

	public void prev()
	{
		if (_rownum >1 ) {
			this.loadTaskOrderItem(_rownum - 1);
		} else {
			App.Current.showError(pn_up_entry_editor.this.getContext(), "�Ѿ��ǵ�һ����");
		}
	}
	
	public void next()
	{
		if (_rownum < _total) {
			this.loadTaskOrderItem(_rownum + 1);
		} else {
			App.Current.showError(pn_up_entry_editor.this.getContext(), "�Ѿ������һ����");
		}
	}
	
	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		
		//ɨ����������
		if (bar_code.startsWith("CRQ:")) {
			int pos = bar_code.indexOf("-");
			String lot = bar_code.substring(4, pos);
			this.loadLotNumber(lot);
		}

		if (bar_code.startsWith("C:")) {
			String lot = bar_code.substring(2, bar_code.length());
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
	
	public void loadTaskOrderItem(long index)
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_mm_up_entry_get_order_item ?,?,?";
		Parameters p  =new Parameters().add(1, App.Current.UserID).add(2, _order_id).add(3, index);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_up_entry_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_up_entry_editor.this.getContext(), result.Error);
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(pn_up_entry_editor.this.getContext(), "û�����ݡ�");
					pn_up_entry_editor.this.clearAll();
					pn_up_entry_editor.this.Header.setTitleText("�������");
					return;
				}
				
				_order_row = row;
				_total = row.getValue("total", Integer.class);
				_rownum = row.getValue("rownum", Long.class);
				if (_total > 0 ){
					pn_up_entry_editor.this.Header.setTitleText("�������(��" + String.valueOf(_total) + "��)");
				} else {
					pn_up_entry_editor.this.Header.setTitleText("�������");
				}
				
				String item_code = row.getValue("item_code", "") + ", ��" + String.valueOf(_rownum) + "��";
				pn_up_entry_editor.this.txt_up_order_cell.setContentText(row.getValue("code", ""));
				pn_up_entry_editor.this.txt_item_code_cell.setContentText(item_code);
				pn_up_entry_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
				
				BigDecimal planned_qty = row.getValue("quantity", BigDecimal.ZERO);
				BigDecimal issued_qty = row.getValue("closed_quantity", BigDecimal.ZERO);
				BigDecimal open_qty = row.getValue("open_quantity", BigDecimal.ZERO);
				
				String qty_str = "����:" + App.formatNumber(planned_qty, "0.##");
				qty_str += "/����:" + App.formatNumber(issued_qty, "0.##");
				qty_str += "/δ��:" + App.formatNumber(open_qty, "0.##");
				
				pn_up_entry_editor.this.txt_quantity_cell.setContentText(qty_str);
				//pn_up_entry_editor.this.txt_location_cell.setContentText(row.getValue("locations", ""));
				
				pn_up_entry_editor.this.txt_lot_number_cell.setTag(null);
				pn_up_entry_editor.this.txt_lot_number_cell.setContentText("");
				pn_up_entry_editor.this.txt_issue_quantity_cell.setContentText("");
				pn_up_entry_editor.this.txt_onhand_cell.setContentText("");
				pn_up_entry_editor.this.txt_onhand_cell.setTag(null);
				pn_up_entry_editor.this.txt_surplus_cell.setContentText("");
			}
		});
	}
	
	public void loadLotNumber(String lotNumber)
	{
		this.ProgressDialog.show();
		
		if (_order_row == null) {
			App.Current.showError(this.getContext(), "û��ָ��������⣬�޷�ɨ�����롣");
			return;
		}

		final long order_id = _order_row.getValue("id", Long.class);
		final int line_id = _order_row.getValue("line_id", Integer.class);

		String sql = "exec p_mm_up_entry_get_lot_number ?,?,?";
		Parameters p = new Parameters().add(1, order_id).add(2, line_id).add(3, lotNumber);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
			public void handleMessage(Message msg) {
				
				pn_up_entry_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> r = this.Value;
				if (r.HasError) {
					App.Current.showError(pn_up_entry_editor.this.getContext(), r.Error);
					return;
				}
				
				_lot_row = r.Value;
				String result = _lot_row.getValue("result", "");
				if (result.length()>0) {
					App.Current.showError(pn_up_entry_editor.this.getContext(), result);
					return;
				}
				
				BigDecimal planned_qty = _order_row.getValue("quantity", BigDecimal.ZERO);
				BigDecimal issued_qty = _order_row.getValue("closed_quantity", BigDecimal.ZERO);
				BigDecimal open_qty = planned_qty.subtract(issued_qty);
				BigDecimal onhand_quantity = _lot_row.getValue("quantity", BigDecimal.ZERO);
				
				String issue_qty = "";
				String surplus_qty = "";
				String onhand_qty = App.formatNumber(onhand_quantity, "0.##");
				if (open_qty.compareTo(onhand_quantity) > 0) {
					issue_qty = onhand_qty;
				} else {
					issue_qty = App.formatNumber(open_qty, "0.##");
					surplus_qty = App.formatNumber(onhand_quantity.subtract(open_qty), "0.##");
				}
				
				pn_up_entry_editor.this.txt_vendor_name_cell.setContentText(_lot_row.getValue("vendor_name", ""));
				pn_up_entry_editor.this.txt_vendor_name_cell.setTag(_lot_row.getValue("vendor_id"));
				pn_up_entry_editor.this.txt_lot_number_cell.setContentText(_lot_row.getValue("lot_number", ""));
				pn_up_entry_editor.this.txt_date_code_cell.setContentText(_lot_row.getValue("date_code", ""));
				pn_up_entry_editor.this.txt_location_cell.setContentText(_lot_row.getValue("locations", ""));
				
				pn_up_entry_editor.this.txt_issue_quantity_cell.setContentText(issue_qty);
				pn_up_entry_editor.this.txt_onhand_cell.setContentText(onhand_qty);
				pn_up_entry_editor.this.txt_surplus_cell.setContentText(surplus_qty);
			}
		});
	}
	
	public void printLabel(String quantity, boolean withIssueOrderCode)
	{
		if (quantity == null || quantity.length() == 0) {
			App.Current.showError(this.getContext(), "û��ָ����ǩ������");
			return;
		}
		
		if (_order_row == null) {
			App.Current.showError(this.getContext(), "û������������ݡ�");
			return;
		}
		
		if (_lot_row == null) {
			App.Current.showError(this.getContext(), "û���������ݡ�");
			return;
		}

		Intent intent = new Intent();
        intent.setClass(this.getContext(), frm_item_lot_printer.class);
        intent.putExtra("org_code", _lot_row.getValue("org_code", ""));
        intent.putExtra("item_code", _lot_row.getValue("item_code", ""));
        intent.putExtra("vendor_model", _lot_row.getValue("vendor_model", ""));
        intent.putExtra("lot_number", _lot_row.getValue("lot_number", ""));
        intent.putExtra("vendor_lot", _lot_row.getValue("vendor_lot", ""));
        intent.putExtra("date_code", _lot_row.getValue("date_code", ""));
        
        if (withIssueOrderCode) {
        	intent.putExtra("issue_order_code", _order_row.getValue("code", ""));
        }
        
        intent.putExtra("quantity", quantity);
        intent.putExtra("ut", _order_row.getValue("uom_code", ""));
        
        this.getContext().startActivity(intent);
	}
	
	@Override
	public void commit()
	{
		if (_order_row == null) {
			App.Current.showError(this.getContext(), "û������������ݣ������ύ��");
			return;
		}
		
		if (_lot_row == null) {
			App.Current.showError(this.getContext(), "û���������ݣ������ύ��");
			return;
		}
		
		final String lot_number = this.txt_lot_number_cell.getContentText();
		if (lot_number == null || lot_number.length() == 0) {
			App.Current.showError(this.getContext(), "û���������ݣ������ύ��");
			return;
		}
		
		final String locations =this.txt_location_cell.getContentText();
		if (locations==null || locations.length()==0)
		{
			App.Current.showError(this.getContext(), "û�д�λ���ݣ������ύ��");
			return;
		}
		
		
		final String issue = this.txt_issue_quantity_cell.getContentText();
		if (issue == null || issue.length() == 0) {
			App.Current.showError(this.getContext(), "û������������������ύ��");
			return;
		}
		
		final BigDecimal issue_quantity = new BigDecimal(issue);
		if (issue_quantity.equals(BigDecimal.ZERO)){
			App.Current.showError(this.getContext(), "�������Ϊ0�������ύ��");
			return;
		}
		
		
		App.Current.question(this.getContext(), "ȷ��Ҫ�ύ��", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				
				Map<String, String> entry = new HashMap<String, String>();
				entry.put("code", _order_row.getValue("code", ""));
				entry.put("create_user", App.Current.UserID);
				entry.put("order_id", String.valueOf(_order_row.getValue("id", Long.class)));
				entry.put("order_line_id", String.valueOf(_order_row.getValue("line_id", 0)));
				entry.put("item_id", String.valueOf(_order_row.getValue("item_id", Integer.class)));
				entry.put("quantity", String.valueOf(issue_quantity));
				entry.put("uom_code", _order_row.getValue("uom_code", ""));
				entry.put("lot_number", lot_number);
				entry.put("date_code", _lot_row.getValue("date_code", ""));
				entry.put("vendor_id", String.valueOf(_lot_row.getValue("vendor_id", 0)));
				entry.put("vendor_model", _lot_row.getValue("vendor_model", ""));
				entry.put("vendor_lot", _order_row.getValue("vendor_lot", ""));
				entry.put("warehouse_id", String.valueOf(_order_row.getValue("warehouse_id", 0)));
				entry.put("locations", String.valueOf(locations));
				
				ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
				entries.add(entry);
				
				//����XML���ݣ��������洢����
				String xml = XmlHelper.createXml("up_entrys", null, null, "up_entry", entries);
				String sql = "exec p_mm_up_entry_create ?,?";
				Connection conn = App.Current.DbPortal.CreateConnection(pn_up_entry_editor.this.Connector);
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
							App.Current.showError(pn_up_entry_editor.this.getContext(), rs.Error);
							return;
						}
						
						App.Current.toastInfo(pn_up_entry_editor.this.getContext(), "�ύ�ɹ�");
						_order_row = null;
						_lot_row = null;
						pn_up_entry_editor.this.clear();
						pn_up_entry_editor.this.loadTaskOrderItem(_rownum);
					}
				} catch (SQLException e) {
					App.Current.showInfo(pn_up_entry_editor.this.getContext(), e.getMessage());
					e.printStackTrace();
					
					pn_up_entry_editor.this.clear();
					pn_up_entry_editor.this.loadTaskOrderItem(_rownum);
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
		this.txt_issue_quantity_cell.setContentText("");
		this.txt_surplus_cell.setContentText("");
		this.txt_onhand_cell.setContentText("");
	}
	
	public void clearAll()
	{
		this.txt_up_order_cell.setContentText("");
		this.txt_item_code_cell.setContentText("");
		this.txt_item_name_cell.setContentText("");
		this.txt_quantity_cell.setContentText("");
		
		this.txt_vendor_name_cell.setContentText("");
		this.txt_date_code_cell.setContentText("");
		this.txt_lot_number_cell.setContentText("");
		this.txt_location_cell.setContentText("");
		this.txt_issue_quantity_cell.setContentText("");
		this.txt_surplus_cell.setContentText("");
		this.txt_onhand_cell.setContentText("");
	}
}
