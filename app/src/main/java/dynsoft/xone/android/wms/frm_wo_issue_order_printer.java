package dynsoft.xone.android.wms;

import java.util.HashMap;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class frm_wo_issue_order_printer extends Activity {

	private TextCell txt_server_cell;
	private TextCell txt_printer_cell;
	private TextCell txt_code_cell;
	private TextCell txt_type_cell;
	private DecimalCell txt_copy_cell;
	private Button btn_print;
	
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
		 this.setContentView(R.layout.frm_wo_issue_order_printer);
		 
		 final Long id = this.getIntent().getLongExtra("id", 0);
		 final String code = this.getIntent().getStringExtra("code");
		 final String type = this.getIntent().getStringExtra("type");
		 
		 txt_server_cell = (TextCell)this.findViewById(R.id.txt_server_cell);
		 txt_printer_cell = (TextCell)this.findViewById(R.id.txt_printer_cell);
		 txt_code_cell = (TextCell)this.findViewById(R.id.txt_code_cell);
		 txt_type_cell = (TextCell)this.findViewById(R.id.txt_type_cell);
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
		 
		 if (txt_code_cell != null) {
			 txt_code_cell.setLabelText("发料单号");
			 txt_code_cell.setLabelWidth(140);
			 txt_code_cell.setContentText(code);
		 }
		 
		 if (txt_type_cell != null) {
			 txt_type_cell.setLabelText("打印类型");
			 txt_type_cell.setLabelWidth(140);
			 txt_type_cell.setContentText(type);
		 }
		 
		 if (txt_copy_cell != null) {
			 txt_copy_cell.setLabelText("打印份数");
			 txt_copy_cell.setLabelWidth(140);
			 txt_copy_cell.setReadOnly();
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
						App.Current.showError(frm_wo_issue_order_printer.this, "无效的打印份数。");
						return;
					}
					
					if (server == null || server.length() == 0) {
						App.Current.showError(frm_wo_issue_order_printer.this, "请指定打印机服务器地址。");
						return;
					}
					
					if (printer == null || printer.length() == 0) {
						App.Current.showError(frm_wo_issue_order_printer.this, "请指定打印机机名称。");
						return;
					}
					
					//保存打印设置
					PrintSetting printSetting = new PrintSetting();
					printSetting.Server = server;
					printSetting.Printer = printer;
					PrintHelper.savePrintServer(frm_wo_issue_order_printer.this, printSetting);
					
					PrintRequest request = new PrintRequest();
					request.Server = server;
					request.Printer = printer;
					request.Code = "mm_wo_issue_order";
					
					//准备打印参数
					request.Parameters = new HashMap<String,String>();
					request.Parameters.put("id", String.valueOf(id));
					request.Parameters.put("user_id", App.Current.UserID);
					request.Parameters.put("type", type);
					for (int i=0; i<copies; i++) {
						Result<String> result = PrintHelper.print(request);
						if (result.HasError) {
							App.Current.showError(frm_wo_issue_order_printer.this, result.Error);
						}
					}
				}
			});
		 }
	 }
}
