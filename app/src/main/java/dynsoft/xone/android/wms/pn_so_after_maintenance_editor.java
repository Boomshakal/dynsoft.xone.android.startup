package dynsoft.xone.android.wms;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;

import dynsoft.xone.android.control.Calculator;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

public class pn_so_after_maintenance_editor extends pn_editor {

	public pn_so_after_maintenance_editor(Context context) {
		super(context);
	}

	public TextCell txt_head_code_cell;

	public TextCell txt_item_name_cell;
	public TextCell txt_customer_name_cell;
	public TextCell txt_sh_date_cell;
	public TextCell txt_JSuser_cell;
	public TextCell txt_OPuser_cell;
	public TextCell txt_qty_cell;
	public TextCell txt_logistics_code_cell;


	public ImageButton btn_prev;
	public ImageButton btn_next;

	@Override
	public void setContentView()
	{
		LayoutParams lp = new LayoutParams(-1,-1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_so_after_maintenance_editor, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_head_code_cell = (TextCell)this.findViewById(R.id.txt_head_code_cell);
		this.txt_sh_date_cell = (TextCell)this.findViewById(R.id.txt_sh_date_cell);
		this.txt_item_name_cell = (TextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_customer_name_cell = (TextCell)this.findViewById(R.id.txt_customer_name_cell);
		this.txt_logistics_code_cell = (TextCell)this.findViewById(R.id.txt_logistics_code_cell);
		this.txt_qty_cell = (TextCell)this.findViewById(R.id.txt_qty_cell);
		this.txt_OPuser_cell = (TextCell)this.findViewById(R.id.txt_OPuser_cell);
		this.txt_JSuser_cell = (TextCell)this.findViewById(R.id.txt_JSuser_cell);




		if (this.txt_head_code_cell != null) {
			this.txt_head_code_cell.setLabelText("业务单号");
			this.txt_head_code_cell.setReadOnly();
		}

		if (this.txt_logistics_code_cell != null) {
			this.txt_logistics_code_cell.setLabelText("物流单号");
			this.txt_logistics_code_cell.setReadOnly();
		}

		if (this.txt_sh_date_cell != null) {
			this.txt_sh_date_cell.setLabelText("收货时间");
			this.txt_sh_date_cell.setReadOnly();
		}
		

		if (this.txt_customer_name_cell != null) {
			this.txt_customer_name_cell.setLabelText("客户名称");
			this.txt_customer_name_cell.setReadOnly();

		}

		if (this != null) {
			this.txt_qty_cell.setLabelText("接收数量");
			this.txt_qty_cell.setReadOnly();
			this.txt_qty_cell.TextBox.setSingleLine(true);
		}
		if (this.txt_OPuser_cell != null) {
			this.txt_OPuser_cell.setLabelText("操作人");
			this.txt_OPuser_cell.setReadOnly();
			this.txt_OPuser_cell.setContentText(App.Current.UserCode);
		}

		if (this.txt_JSuser_cell != null) {
			this.txt_JSuser_cell.setLabelText("接收人");
			this.txt_JSuser_cell.setReadOnly();
			this.txt_JSuser_cell.TextBox.setSingleLine(true);
		}
		

		_order_code = this.Parameters.get("code", "");
		_sh_date = this.Parameters.get("sh_date", "");
		_customer_name = this.Parameters.get("customer_name", "");
		_qty = this.Parameters.get("qty", 0);
		_logistics_code = this.Parameters.get("logistics_code", "");
		//_order_code = this.Parameters.get("code", "");
		_rownum = 1L;
		
		this.txt_head_code_cell.setContentText(_order_code);
		this.txt_sh_date_cell.setContentText(_sh_date);
		this.txt_logistics_code_cell.setContentText(_logistics_code);
		this.txt_customer_name_cell.setContentText(_customer_name);
		this.txt_qty_cell.setContentText(""+_qty);


	}
	
	private Long _rownum;
	private int _qty;
	private String _logistics_code;
	private String _customer_name;
	private String _sh_date;
	private String _order_code;
	private DataRow _order_row;
	private Integer _total = 0;
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if (resultCode == Calculator.CalculatorResult) {
			String qty = intent.getStringExtra("result");
			//this.txt_confirm_quantity_cell.setContentText(qty);
		}
	}
	
	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		if (bar_code.startsWith("M")||bar_code.length()==9) {

			this.txt_JSuser_cell.setContentText(bar_code);

		}else {
			App.Current.showError(pn_so_after_maintenance_editor.this.getContext(), "无效条码。");
		}

	}



	
	@Override
	public void commit()
	{

		final String head_code = this.txt_head_code_cell.getContentText().trim();
		final String L_user =this.txt_JSuser_cell.getContentText().trim();

		if (L_user == null || L_user.length() == 0) {
			App.Current.showError(this.getContext(), "接收人不能为空，不能提交。");
			return;
		}

		App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				String sql = "exec pn_so_after_maintenance_commit ?,?";
				Parameters p  =new Parameters().add(1, head_code).add(2,L_user);
				Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(pn_so_after_maintenance_editor.this.Connector, sql, p);
				if (r.HasError) {
					App.Current.showError(pn_so_after_maintenance_editor.this.getContext(), r.Error);
					return;
				}
				if (r.Value > 0) {
					App.Current.showInfo(pn_so_after_maintenance_editor.this.getContext(), "提交成功。");
					pn_so_after_maintenance_editor.this.close();
				}
			}
		});
	}
	public void clear()
	{
		//this.txt_fa_number_cell.setContentText("");
	}
}
