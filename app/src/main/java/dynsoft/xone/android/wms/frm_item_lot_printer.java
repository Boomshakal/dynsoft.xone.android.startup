package dynsoft.xone.android.wms;

import java.util.HashMap;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import dynsoft.xone.android.control.Calculator;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.PrintRequest;
import dynsoft.xone.android.data.PrintSetting;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.helper.PrintHelper;

public class frm_item_lot_printer extends Activity {

	private TextCell txt_server_cell;
	private TextCell txt_printer_cell;
	private TextCell txt_org_code_cell;
	private TextCell txt_item_code_cell;
	private TextCell txt_vendor_model_cell;
	private TextCell txt_lot_number_cell;
	private TextCell txt_vendor_lot_cell;
	private TextCell txt_quantity_cell;
	private DecimalCell txt_copy_cell;
	private Button btn_print;
	
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
		 this.setContentView(R.layout.frm_item_lot_printer);
		 
		 final String org_code = this.getIntent().getStringExtra("org_code");
		 final String item_code = this.getIntent().getStringExtra("item_code");
		 final String vendor_model = this.getIntent().getStringExtra("vendor_model");
		 final String lot_number = this.getIntent().getStringExtra("lot_number");
		 final String vendor_lot = this.getIntent().getStringExtra("vendor_lot");
		 final String date_code = this.getIntent().getStringExtra("date_code");
		 final String quantity = this.getIntent().getStringExtra("quantity");
		 final String ut = this.getIntent().getStringExtra("ut");
		 final String usercode =this.getIntent().getStringExtra("usercode");
		 
		 txt_server_cell = (TextCell)this.findViewById(R.id.txt_server_cell);
		 txt_printer_cell = (TextCell)this.findViewById(R.id.txt_printer_cell);
		 txt_org_code_cell = (TextCell)this.findViewById(R.id.txt_org_code_cell);
		 txt_item_code_cell = (TextCell)this.findViewById(R.id.txt_item_code_cell);
		 txt_vendor_model_cell = (TextCell)this.findViewById(R.id.txt_vendor_model_cell);
		 txt_lot_number_cell = (TextCell)this.findViewById(R.id.txt_lot_number_cell);
		 txt_vendor_lot_cell = (TextCell)this.findViewById(R.id.txt_vendor_lot_cell);
		 txt_quantity_cell = (TextCell)this.findViewById(R.id.txt_quantity_cell);
		 txt_copy_cell = (DecimalCell)this.findViewById(R.id.txt_copy_cell);
		 btn_print = (Button)this.findViewById(R.id.btn_print);
		 
		 if (txt_server_cell != null) {
			 txt_server_cell.setLabelText("服务器");
			 txt_server_cell.setLabelWidth(140);
		 }
		 
		 if (txt_printer_cell != null) {
			 txt_printer_cell.setLabelText("打印机");
			 txt_printer_cell.setLabelWidth(140);
		 }
		 
		 if (txt_org_code_cell != null) {
			 txt_org_code_cell.setLabelText("组织");
			 txt_org_code_cell.setLabelWidth(140);
			 txt_org_code_cell.setContentText(org_code);
		 }
		 
		 if (txt_item_code_cell != null) {
			 txt_item_code_cell.setLabelText("物料编号");
			 txt_item_code_cell.setLabelWidth(140);
			 txt_item_code_cell.setReadOnly();
			 txt_item_code_cell.setContentText(item_code);
		 }
		 
		 if (txt_vendor_model_cell != null) {
			 txt_vendor_model_cell.setLabelText("厂家型号");
			 txt_vendor_model_cell.setLabelWidth(140);
			 txt_vendor_model_cell.setReadOnly();
			 txt_vendor_model_cell.setContentText(vendor_model);
		 }
		 
		 if (txt_vendor_lot_cell != null) {
			 txt_vendor_lot_cell.setLabelText("厂家批号");
			 txt_vendor_lot_cell.setLabelWidth(140);
			 txt_vendor_lot_cell.setReadOnly();
			 txt_vendor_lot_cell.setContentText(vendor_lot);
		 }
		 
		 if (txt_lot_number_cell != null) {
			 txt_lot_number_cell.setLabelText("批号");
			 txt_lot_number_cell.setLabelWidth(140);
			 txt_lot_number_cell.setContentText(lot_number + " " + date_code);
		 }
		 
		 if (txt_quantity_cell != null) {
			 txt_quantity_cell.setLabelText("数量");
			 txt_quantity_cell.setLabelWidth(140);
			 txt_quantity_cell.setReadOnly();
			 txt_quantity_cell.setContentText(quantity + " " + ut);
		 }
		 
		 if (txt_copy_cell != null) {
			 txt_copy_cell.setLabelText("打印份数");
			 txt_copy_cell.setLabelWidth(140);
			 txt_item_code_cell.setReadOnly();
			 txt_copy_cell.setContentText("1");
		 }
		 
		 PrintSetting printSetting = PrintHelper.loadPrintServer(this);
		 if (printSetting != null) {
			 txt_server_cell.setContentText(printSetting.Url);
			 txt_printer_cell.setContentText(printSetting.Printer);
		 }

		 if (btn_print != null) {
			 btn_print.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					String server = txt_server_cell.getContentText();
					String printer = txt_printer_cell.getContentText();
					String copy = txt_copy_cell.getContentText();
					Integer copies = Integer.parseInt(copy);
					if (copies == 0) {
						App.Current.showError(frm_item_lot_printer.this, "无效的打印份数。");
						return;
					}
					
					if (server == null || server.length() == 0) {
						App.Current.showError(frm_item_lot_printer.this, "请指定打印机服务器地址。");
						return;
					}
					
					if (printer == null || printer.length() == 0) {
						App.Current.showError(frm_item_lot_printer.this, "请指定打印机机名称。");
						return;
					}
					
					//保存打印设置
					PrintSetting printSetting = new PrintSetting();
					printSetting.Server = server;
					printSetting.Url = server;
					printSetting.Printer = printer;
					PrintHelper.savePrintServer(frm_item_lot_printer.this, printSetting);
					
					PrintRequest request = new PrintRequest();
					request.Server = server;
					request.Printer = printer;
					request.Code = "mm_item_lot_label";
					
					//准备打印参数
					request.Parameters = new HashMap<String,String>();
					request.Parameters.put("org_code", org_code);
					request.Parameters.put("item_code", item_code);
					request.Parameters.put("vendor_model", vendor_model);
					request.Parameters.put("lot_number", lot_number);
					request.Parameters.put("vendor_lot", vendor_lot);
					request.Parameters.put("date_code", date_code);
					request.Parameters.put("quantity", quantity);
					request.Parameters.put("ut", ut);
					if(TextUtils.isEmpty(usercode)) {
						request.Parameters.put("user_code", App.Current.UserCode);
					} else {
						request.Parameters.put("user_code", usercode);
					}
					
					for (int i=0; i<copies; i++) {
						Result<String> result = PrintHelper.print(request);
						if (result.HasError) {
							App.Current.showError(frm_item_lot_printer.this, result.Error);
						}
					}
				}
			});
		 }
	 }

	 @Override
	 public void onActivityResult(int requestCode, int resultCode, Intent intent)
	 {
		 if (resultCode == Calculator.CalculatorResult) {
			 String result = intent.getStringExtra("result");
			 this.txt_quantity_cell.setContentText(result);
		 }
	 }
}
