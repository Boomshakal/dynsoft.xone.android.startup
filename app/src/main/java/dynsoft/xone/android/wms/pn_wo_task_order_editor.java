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
import dynsoft.xone.android.control.Calculator;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class pn_wo_task_order_editor extends pn_editor {

	public pn_wo_task_order_editor(Context context) {
		super(context);
	}

	public TextCell txt_task_order_cell;
	public TextCell txt_item_code_cell;
	public TextCell txt_item_name_cell;
	public TextCell txt_warehouse_cell;
	public TextCell txt_vendor_name_cell;
	public TextCell txt_date_code_cell;
	public TextCell txt_plan_quantity_cell;
	public TextCell txt_issue_quantity_cell;
	public DecimalCell txt_confirm_quantity_cell;
	public ImageButton btn_prev;
	public ImageButton btn_next;

	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_wo_task_order_editor, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_task_order_cell = (TextCell)this.findViewById(R.id.txt_task_order_cell);
		this.txt_item_code_cell = (TextCell)this.findViewById(R.id.txt_item_code_cell);
		this.txt_item_name_cell = (TextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_warehouse_cell = (TextCell)this.findViewById(R.id.txt_warehouse_cell);
		this.txt_vendor_name_cell = (TextCell)this.findViewById(R.id.txt_vendor_name_cell);
		this.txt_date_code_cell = (TextCell)this.findViewById(R.id.txt_date_code_cell);
		this.txt_plan_quantity_cell = (TextCell)this.findViewById(R.id.txt_plan_quantity_cell);
		this.txt_issue_quantity_cell = (TextCell)this.findViewById(R.id.txt_issue_quantity_cell);
		this.txt_confirm_quantity_cell = (DecimalCell)this.findViewById(R.id.txt_confirm_quantity_cell);
		this.btn_prev = (ImageButton)this.findViewById(R.id.btn_prev);
		this.btn_next = (ImageButton)this.findViewById(R.id.btn_next);
		
		if (this.txt_task_order_cell != null) {
			this.txt_task_order_cell.setLabelText("生产任务");
			this.txt_task_order_cell.setReadOnly();
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
		
		if (this.txt_warehouse_cell != null) {
			this.txt_warehouse_cell.setLabelText("发出库位");
			this.txt_warehouse_cell.setReadOnly();
			this.txt_warehouse_cell.TextBox.setSingleLine(true);
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
		

		if (this.txt_plan_quantity_cell != null) {
			this.txt_plan_quantity_cell.setLabelText("计划数量");
			this.txt_plan_quantity_cell.setReadOnly();
		}
		
		if (this.txt_issue_quantity_cell != null) {
			this.txt_issue_quantity_cell.setLabelText("已发数量");
			this.txt_issue_quantity_cell.setReadOnly();
		}
		
		if (this.txt_confirm_quantity_cell != null) {
			this.txt_confirm_quantity_cell.setLabelText("接收数量");
		}
		
		if (this.btn_prev != null) {
			this.btn_prev.setImageBitmap(App.Current.ResourceManager.getImage("@/core_prev_white"));
			this.btn_prev.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_wo_task_order_editor.this.prev();
				}
			});
		}
		
		if (this.btn_next != null) {
			this.btn_next.setImageBitmap(App.Current.ResourceManager.getImage("@/core_next_white"));
			this.btn_next.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_wo_task_order_editor.this.next();
				}
			});
		}
		
		_order_id = this.Parameters.get("order_id", 0L);
		_order_code = this.Parameters.get("order_code", "");
		_rownum = 1L;
		
		this.txt_task_order_cell.setContentText(_order_code);
		this.loadTaskOrderItem(_rownum);
	}
	
	private Long _rownum;
	private Long _order_id;
	private String _order_code;
	private DataRow _order_row;
	private Integer _total = 0;
	
	public void prev()
	{
		if (_rownum > 1) {
			this.loadTaskOrderItem(_rownum - 1);
		} else {
			App.Current.showError(pn_wo_task_order_editor.this.getContext(), "已经是第一条。");
		}
	}
	
	public void next()
	{
		if (_rownum < _total) {
			this.loadTaskOrderItem(_rownum + 1);
		} else {
			App.Current.showError(pn_wo_task_order_editor.this.getContext(), "已经是最后一条。");
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if (resultCode == Calculator.CalculatorResult) {
			String qty = intent.getStringExtra("result");
			this.txt_confirm_quantity_cell.setContentText(qty);
		}
	}
	
	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		
		//扫描批次条码
		if (bar_code.startsWith("CRQ:")) {
			String str = bar_code.substring(4, bar_code.length());
			String[] arr = str.split("-");
			if (arr.length > 2) {
				//String item_code = arr[2];
				//this.loadLotNumber(item_code);
			}
			return;
		}
	}
	
	public void loadTaskOrderItem(long index)
	{
		this.ProgressDialog.show();
		
		String code = this.txt_task_order_cell.getContentText();
		if (code == null || code.length() == 0) {
			App.Current.showError(pn_wo_task_order_editor.this.getContext(), "没有指定发料申请。");
			return;
		}
		
		String sql = "exec p_mm_wo_task_order_get_item ?,?,?";
		Parameters p  =new Parameters().add(1, App.Current.UserID).add(2, _order_id).add(3, index);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_wo_task_order_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_wo_task_order_editor.this.getContext(), result.Error);
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(pn_wo_task_order_editor.this.getContext(), "找不到生产任务。");
					pn_wo_task_order_editor.this.clear();
					pn_wo_task_order_editor.this.Header.setTitleText("生产任务(无生产任务)");
					return;
				}
				
				_order_row = row;
				_total = row.getValue("total", Integer.class);
				_rownum = row.getValue("rownum", Long.class);
				if (_total > 0 ) {
					pn_wo_task_order_editor.this.Header.setTitleText("生产任务(" + String.valueOf(_total) + "条生产任务)");
				} else {
					pn_wo_task_order_editor.this.Header.setTitleText("生产任务(无生产任务)");
				}
				
				pn_wo_task_order_editor.this.txt_task_order_cell.setTag(row);
				
				String item_code = row.getValue("item_code", "") + ", 第" + String.valueOf(_rownum) + "条";
				pn_wo_task_order_editor.this.txt_task_order_cell.setContentText(row.getValue("code", ""));
				pn_wo_task_order_editor.this.txt_item_code_cell.setContentText(item_code);
				pn_wo_task_order_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
				pn_wo_task_order_editor.this.txt_warehouse_cell.setContentText(row.getValue("warehouse_code", "") + ", " + row.getValue("locations", ""));
				pn_wo_task_order_editor.this.txt_vendor_name_cell.setContentText(row.getValue("vendor_name", ""));
				pn_wo_task_order_editor.this.txt_date_code_cell.setContentText(row.getValue("date_code", ""));
				
				BigDecimal planned_qty = row.getValue("plan_quantity", BigDecimal.ZERO);
				BigDecimal issued_qty = row.getValue("issued_quantity", BigDecimal.ZERO);
				//BigDecimal closed_qty = row.getValue("closed_quantity", BigDecimal.ZERO);
				
				pn_wo_task_order_editor.this.txt_plan_quantity_cell.setContentText(App.formatNumber(planned_qty, "0.##"));
				pn_wo_task_order_editor.this.txt_issue_quantity_cell.setContentText(App.formatNumber(issued_qty, "0.##"));
				pn_wo_task_order_editor.this.txt_confirm_quantity_cell.setContentText("");
			}
		});
	}
	
	public void loadLotNumber(String lotNumber)
	{
		this.ProgressDialog.show();
		
		final DataRow row = (DataRow)this.txt_task_order_cell.getTag();
		if (row == null) {
			App.Current.showError(this.getContext(), "没有指定生产任务，无法扫描条码。");
			return;
		}

		final long order_id = row.getValue("id", Long.class);
		final int line_id = row.getValue("line_id", Integer.class);
		String sql = "exec p_mm_wo_issue_get_lot_number ?,?,?";
		Parameters p = new Parameters().add(1, order_id).add(2, line_id).add(3, lotNumber);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
			public void handleMessage(Message msg) {
				
			}
		});
		
	}
	
	@Override
	public void commit()
	{
		if (_order_row == null) {
			App.Current.showError(this.getContext(), "没有输入实发数量，不能提交。");
			return;
		}
		
		final Long order_id = _order_row.getValue("id", 0L);
		final Integer line_id = _order_row.getValue("line_id", 0);
		final String confirm_qty = this.txt_confirm_quantity_cell.getContentText().trim();
		
		if (confirm_qty == null || confirm_qty.length() == 0) {
			App.Current.showError(this.getContext(), "没有输入确认数量，不能提交。");
			return;
		}
		
		App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				
				String sql = "exec p_mm_wo_task_order_confirm ?,?,?,?";
				Parameters p  =new Parameters().add(1, App.Current.UserID).add(2, order_id).add(3, line_id).add(4, confirm_qty);
				Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(pn_wo_task_order_editor.this.Connector, sql, p);
				if (r.HasError) {
					App.Current.showError(pn_wo_task_order_editor.this.getContext(), r.Error);
				}
				
				if (r.Value > 0) {
					App.Current.showError(pn_wo_task_order_editor.this.getContext(), "确认成功。");
					pn_wo_task_order_editor.this.clear();
				}
			}
		});
	}
	
	public void clear()
	{
		this.txt_task_order_cell.setContentText("");
		this.txt_item_code_cell.setContentText("");
		this.txt_item_name_cell.setContentText("");
		this.txt_warehouse_cell.setContentText("");
		this.txt_plan_quantity_cell.setContentText("");
		this.txt_issue_quantity_cell.setContentText("");
		this.txt_vendor_name_cell.setContentText("");
		this.txt_date_code_cell.setContentText("");
		this.txt_warehouse_cell.setContentText("");
		
	}
}
