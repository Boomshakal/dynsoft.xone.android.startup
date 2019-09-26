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
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class pn_tr_iqc_move_editor extends pn_editor {

	public pn_tr_iqc_move_editor(Context context) {
		super(context);
	}

	public TextCell txt_item_code_cell;
	public TextCell txt_item_name_cell;
	public TextCell txt_vendor_name_cell;
	public TextCell txt_vendor_model_cell;
	public TextCell txt_vendor_lot_cell;
	public TextCell txt_lot_number_cell;
	public TextCell txt_quantity_cell;
	public TextCell txt_old_location_cell;
	public ButtonTextCell txt_new_location_cell;

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
			this.txt_quantity_cell.setLabelText("数量");
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
					pn_tr_iqc_move_editor.this.txt_new_location_cell.setContentText("");
				}
			});
		}

		String lot_number = this.Parameters.get("lot_number", "");
		if (lot_number != null && lot_number.length() > 0) {
			this.loadLotNumber(lot_number);
		}
	}
	
	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_tr_iqc_move_editor, this, true);
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
	
	public void loadLotNumber(String lotNumber)
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_mm_stock_lot_get_lot_number_c  ?";
		Parameters p  =new Parameters().add(1, lotNumber);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_tr_iqc_move_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_tr_iqc_move_editor.this.getContext(), result.Error);
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(pn_tr_iqc_move_editor.this.getContext(), "该批次不存在。");
					return;
				}
				
				_lot_number_row = row;
				
				pn_tr_iqc_move_editor.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
				pn_tr_iqc_move_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
				pn_tr_iqc_move_editor.this.txt_vendor_name_cell.setContentText(row.getValue("vendor_name", ""));
				pn_tr_iqc_move_editor.this.txt_vendor_model_cell.setContentText(row.getValue("vendor_model", ""));
				pn_tr_iqc_move_editor.this.txt_vendor_lot_cell.setContentText(row.getValue("vendor_lot", ""));
				pn_tr_iqc_move_editor.this.txt_lot_number_cell.setContentText(row.getValue("lot_number", ""));
				pn_tr_iqc_move_editor.this.txt_quantity_cell.setContentText(App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##"));
				pn_tr_iqc_move_editor.this.txt_old_location_cell.setContentText(row.getValue("locations", ""));
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
		final String lot_number = _lot_number_row.getValue("lot_number", String.class);

		final String locations = this.txt_new_location_cell.getContentText().trim();
		if (locations == null || locations.length() == 0) {
			App.Current.showError(this.getContext(), "没有指定新储位，不能提交。");
			return;
		}
		
		App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				
				String sql = "exec p_mm_tr_move_iqc_loc ?,?,?";
				Parameters p = new Parameters().add(1, lot_number).add(2, locations).add(3, item_id);
				Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(pn_tr_iqc_move_editor.this.Connector, sql, p);
				if (r.HasError) {
					App.Current.showError(pn_tr_iqc_move_editor.this.getContext(), r.Error);
					return;
				}
				
				if (r.Value > 0) {
					App.Current.showInfo(pn_tr_iqc_move_editor.this.getContext(), "修改成功。");
					pn_tr_iqc_move_editor.this.clear();
				}
			}
		});
	}
	
	public void clear()
	{
		pn_tr_iqc_move_editor.this.txt_item_code_cell.setContentText("");
		pn_tr_iqc_move_editor.this.txt_item_name_cell.setContentText("");
		pn_tr_iqc_move_editor.this.txt_vendor_name_cell.setContentText("");
		pn_tr_iqc_move_editor.this.txt_vendor_lot_cell.setContentText("");
		pn_tr_iqc_move_editor.this.txt_vendor_model_cell.setContentText("");
		pn_tr_iqc_move_editor.this.txt_lot_number_cell.setContentText("");
		pn_tr_iqc_move_editor.this.txt_quantity_cell.setContentText("");
		pn_tr_iqc_move_editor.this.txt_old_location_cell.setContentText("");
		pn_tr_iqc_move_editor.this.txt_new_location_cell.setContentText("");
	}
}
