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
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class pn_mm_changeloc_byitem_editor extends pn_editor {

	public pn_mm_changeloc_byitem_editor(Context context) {
		super(context);
	}

	public TextCell txt_item_code_cell;
	public TextCell txt_item_name_cell;
	public TextCell txt_org_code_cell;
	public TextCell txt_war_code_cell;
	public TextCell txt_quantity_cell;
	public TextCell txt_old_location_cell;
	public ButtonTextCell txt_new_location_cell;
	private String x_item_code;

	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_item_code_cell = (TextCell)this.findViewById(R.id.txt_item_code_cell);
		this.txt_item_name_cell = (TextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_org_code_cell = (TextCell)this.findViewById(R.id.txt_org_code_cell);
		this.txt_war_code_cell = (TextCell)this.findViewById(R.id.txt_war_code_cell);
		this.txt_quantity_cell = (TextCell)this.findViewById(R.id.txt_quantity_cell);
		this.txt_old_location_cell = (TextCell)this.findViewById(R.id.txt_old_location_cell);
		this.txt_new_location_cell = (ButtonTextCell)this.findViewById(R.id.txt_new_location_cell);
		
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("物料编码");
			this.txt_item_code_cell.setReadOnly();
		}
		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("物料名称");
			this.txt_item_name_cell.setReadOnly();
		}
		
		if (this.txt_org_code_cell != null) {
			this.txt_org_code_cell.setLabelText("组织");
			this.txt_org_code_cell.setReadOnly();
		}
		
		if (this.txt_war_code_cell != null) {
			this.txt_war_code_cell.setLabelText("库位");
			this.txt_war_code_cell.setReadOnly();
		}
		
		if (this.txt_quantity_cell != null) {
			this.txt_quantity_cell.setLabelText("库存数量");
			this.txt_quantity_cell.setReadOnly();
		}
		
		if (this.txt_old_location_cell != null) {
			this.txt_old_location_cell.setLabelText("原储位");
			this.txt_old_location_cell.setReadOnly();
		}
		if (this.txt_new_location_cell != null) {
			this.txt_new_location_cell.setLabelText("储位");
			this.txt_new_location_cell.setReadOnly();
			this.txt_new_location_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_light"));
			this.txt_new_location_cell.Button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_mm_changeloc_byitem_editor.this.txt_new_location_cell.setContentText("");
				}
			});
		}
	}
	
	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_mm_changeloc_byitem_editor, this, true);
        view.setLayoutParams(lp);
	}
	public void loadwarcode(String v_lot_number){

		String sql = "exec p_mm_stock_lot_get_lot_number_b ?";
		Parameters p  =new Parameters().add(1, v_lot_number);
		
		final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql,p);
		if (result.HasError) {
			App.Current.showError(this.getContext(), result.Error);
			return;
		}
		if (result.Value != null) {
			if (result.Value.Rows.size() > 1){
				ArrayList<String> names = new ArrayList<String>();
				for (DataRow row : result.Value.Rows) {
					String name = row.getValue("code", "");
					names.add(name);
				}

				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which >= 0) {
							DataRow row = result.Value.Rows.get(which);
							pn_mm_changeloc_byitem_editor.this.txt_war_code_cell.setContentText(row.getValue(
									"code", ""));
							pn_mm_changeloc_byitem_editor.this.loadLotNumber(x_item_code, row.getValue("code", ""));
						}
						
						dialog.dismiss();

					}
				};
				new AlertDialog.Builder(pn_mm_changeloc_byitem_editor.this.getContext()).setTitle("选择库位")
				.setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(pn_mm_changeloc_byitem_editor.this.txt_war_code_cell.getContentText().toString()), listener)
				.setNegativeButton("取消", null).show();
		} 
			else
			{
				DataRow rowv = result.Value.Rows.get(0);
				pn_mm_changeloc_byitem_editor.this.txt_war_code_cell.setContentText(rowv.getValue(
						"code", ""));
				pn_mm_changeloc_byitem_editor.this.loadLotNumber(x_item_code, rowv.getValue("code", ""));
			}
		} 
	}
	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		
		//扫描批次条码
		if (bar_code.startsWith("CRQ:")) {
			int pos = bar_code.indexOf("-");
			String tempbar_code =barcode.substring(4, barcode.length());
			String lot = bar_code.substring(4, pos);
			x_item_code =tempbar_code.split("-")[1];
			this.loadwarcode(lot);
		}

		if (bar_code.startsWith("C:")){
			String lot = bar_code.substring(2, bar_code.length());
			this.loadwarcode(lot);
		
		}
		
		if (bar_code.startsWith("L:")){
			String loc = bar_code.substring(2, bar_code.length());
			String locs = this.txt_new_location_cell.getContentText().trim();
			if (locs.contains(loc)){
				return;
			}
			
			if (locs.length() > 0){
				locs += ", ";
			}
			
			this.txt_new_location_cell.setContentText(locs+loc);
		}
	}
	
	private DataRow _lot_number_row;
	
	public void loadLotNumber(String lotNumber,String warcode)
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_mm_stock_lot_get_lot_number_a ?,?";
		Parameters p  =new Parameters().add(1, lotNumber).add(2, warcode);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_mm_changeloc_byitem_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_mm_changeloc_byitem_editor.this.getContext(), result.Error);
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(pn_mm_changeloc_byitem_editor.this.getContext(), "该批次不存在。");
					return;
				}
				
				_lot_number_row = row;
				
				pn_mm_changeloc_byitem_editor.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
				pn_mm_changeloc_byitem_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
				pn_mm_changeloc_byitem_editor.this.txt_war_code_cell.setContentText(row.getValue("warehouse_code", ""));
				pn_mm_changeloc_byitem_editor.this.txt_org_code_cell.setContentText(row.getValue("org_code", ""));
				pn_mm_changeloc_byitem_editor.this.txt_quantity_cell.setContentText(App.formatNumber(row.getValue("onhand_qty", BigDecimal.ZERO), "0.##"));
				pn_mm_changeloc_byitem_editor.this.txt_old_location_cell.setContentText(row.getValue("locations", ""));
			}
		});
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

		final String locations = this.txt_new_location_cell.getContentText().trim();
		if (locations == null || locations.length() == 0) {
			App.Current.showError(this.getContext(), "没有指定新储位，不能提交。");
			return;
		}
		
		App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				String sql=" exec p_mm_change_location ?,?,? ";
				//String sql = "update mm_stock_lot set locations=? where item_id=? and warehouse_id=?  ";
				String wherestr =" item_id ="+item_id.toString()+" and warehouse_id="+warehouse_id.toString();
				Parameters p = new Parameters().add(1, locations).add(2, App.Current.UserID).add(3, wherestr);
				Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(pn_mm_changeloc_byitem_editor.this.Connector, sql, p);
				if (r.HasError) {
					App.Current.showError(pn_mm_changeloc_byitem_editor.this.getContext(), r.Error);
					return;
				}
				
				if (r.Value > 0) {
					App.Current.showInfo(pn_mm_changeloc_byitem_editor.this.getContext(), "修改成功。");
					pn_mm_changeloc_byitem_editor.this.clear();
				}
			}
		});
	}
	
	public void clear()
	{
		pn_mm_changeloc_byitem_editor.this.txt_item_code_cell.setContentText("");
		pn_mm_changeloc_byitem_editor.this.txt_item_name_cell.setContentText("");
		pn_mm_changeloc_byitem_editor.this.txt_war_code_cell.setContentText("");
		pn_mm_changeloc_byitem_editor.this.txt_org_code_cell.setContentText("");
		//pn_mm_changeloc_byitem_editor.this.txt_vendor_model_cell.setContentText("");
		//pn_mm_changeloc_byitem_editor.this.txt_lot_number_cell.setContentText("");
		pn_mm_changeloc_byitem_editor.this.txt_quantity_cell.setContentText("");
		pn_mm_changeloc_byitem_editor.this.txt_old_location_cell.setContentText("");
		pn_mm_changeloc_byitem_editor.this.txt_new_location_cell.setContentText("");
	}
}
