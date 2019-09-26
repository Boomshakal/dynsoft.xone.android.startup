package dynsoft.xone.android.wms;

import java.util.HashMap;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.PrintRequest;
import dynsoft.xone.android.data.PrintSetting;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.helper.PrintHelper;

public class frm_item_lot_printer_init extends Activity {

	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
		 this.setContentView(R.layout.frm_item_lot_printer_init);
		 
		 final String org_code = this.getIntent().getStringExtra("org_code");
		 final String item_code = this.getIntent().getStringExtra("item_code");
		 final String vendor_model = this.getIntent().getStringExtra("vendor_model");
		 final String lot_number = this.getIntent().getStringExtra("lot_number");
		 final String vendor_lot = this.getIntent().getStringExtra("vendor_lot");
		 final String date_code = this.getIntent().getStringExtra("date_code");
		 final String quantity = this.getIntent().getStringExtra("quantity");
		 final String ut = this.getIntent().getStringExtra("ut");
		 final String usercode =this.getIntent().getStringExtra("usercode");
		 
		 final TextCell txt_server_cell = (TextCell)this.findViewById(R.id.txt_server_cell);
		 final TextCell txt_printer_cell = (TextCell)this.findViewById(R.id.txt_printer_cell);
		 //final TextCell txt_org_code_cell = (TextCell)this.findViewById(R.id.txt_org_code_cell);
		 final TextCell txt_item_code_cell = (TextCell)this.findViewById(R.id.txt_item_code_cell);
		 final TextCell txt_vendor_model_cell = (TextCell)this.findViewById(R.id.txt_vendor_model_cell);
		 final TextCell txt_lot_number_cell = (TextCell)this.findViewById(R.id.txt_lot_number_cell);
		 final TextCell txt_vendor_lot_cell = (TextCell)this.findViewById(R.id.txt_vendor_lot_cell);
		 final TextCell txt_date_code_cell = (TextCell)this.findViewById(R.id.txt_date_code_cell);
		 final DecimalCell txt_quantity_cell = (DecimalCell)this.findViewById(R.id.txt_quantity_cell);
		 //final TextCell txt_uom_cell = (TextCell)this.findViewById(R.id.txt_uom_cell);
		 final DecimalCell txt_copy_cell = (DecimalCell)this.findViewById(R.id.txt_copy_cell);
		 final Button btn_print = (Button)this.findViewById(R.id.btn_print);
		 final TextCell txt_uom_code_cell=(TextCell)this.findViewById(R.id.txt_uom_code_cell);
		 
		 if (txt_server_cell != null) {
			 txt_server_cell.setLabelText("������");
			 txt_server_cell.setLabelWidth(140);
			 txt_server_cell.setContentText("http://");
		 }
		 
		 if (txt_printer_cell != null) {
			 txt_printer_cell.setLabelText("��ӡ��");
			 txt_printer_cell.setLabelWidth(140);
		 }
		 
		/* if (txt_org_code_cell != null) {
			 txt_org_code_cell.setLabelText("��֯");
			 txt_org_code_cell.setLabelWidth(140);
			 txt_org_code_cell.setContentText(org_code);
		 }*/
		 
		 if (txt_item_code_cell != null) {
			 txt_item_code_cell.setLabelText("���ϱ��");
			 txt_item_code_cell.setLabelWidth(140);
			 txt_item_code_cell.setReadOnly();
			 txt_item_code_cell.setContentText(item_code+","+org_code);
		 }
		 
		 if (txt_vendor_model_cell != null) {
			 txt_vendor_model_cell.setLabelText("�����ͺ�");
			 txt_vendor_model_cell.setLabelWidth(140);
			 txt_vendor_model_cell.setReadOnly();
			 txt_vendor_model_cell.setContentText(vendor_model);
		 }
		 
		 if (txt_lot_number_cell != null) {
			 txt_lot_number_cell.setLabelText("����");
			 txt_lot_number_cell.setLabelWidth(140);
			 txt_item_code_cell.setReadOnly();
			 txt_lot_number_cell.setContentText(lot_number);
		 }
		 
		 if (txt_vendor_lot_cell != null) {
			 txt_vendor_lot_cell.setLabelText("��������");
			 txt_vendor_lot_cell.setLabelWidth(140);
			 txt_vendor_lot_cell.setReadOnly();
			 txt_vendor_lot_cell.setContentText(vendor_lot);
		 }
		 
		 if (txt_date_code_cell != null) {
			 txt_date_code_cell.setLabelText("D/C");
			 txt_date_code_cell.setLabelWidth(140);
			 txt_item_code_cell.setReadOnly();
			 txt_date_code_cell.setContentText(date_code);
		 }
		 
		 if (txt_quantity_cell != null) {
			 txt_quantity_cell.setLabelText("����");
			 txt_quantity_cell.setLabelWidth(140);
			 txt_quantity_cell.setContentText(quantity);
		 }
		 if (txt_uom_code_cell != null) {
			 txt_uom_code_cell.setLabelText("��λ");
			 txt_uom_code_cell.setLabelWidth(140);
			 txt_uom_code_cell.setReadOnly();
			 txt_uom_code_cell.setContentText(ut);
		 }
		 
		 if (txt_copy_cell != null) {
			 txt_copy_cell.setLabelText("��ӡ����");
			 txt_copy_cell.setLabelWidth(140);
			 txt_copy_cell.setContentText("1");
		 }
		 
		 PrintSetting printSetting = PrintHelper.loadPrintServer(this);
		 if (printSetting != null) {
			 if (printSetting.Url!=null && printSetting.Url.length()!=0)
			 {
				 txt_server_cell.setContentText(printSetting.Url);
			 }
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
						App.Current.showError(frm_item_lot_printer_init.this, "��Ч�Ĵ�ӡ������");
						return;
					}
					 
					if (server == null || server.length() == 0) {
						App.Current.showError(frm_item_lot_printer_init.this, "��ָ����ӡ����������ַ��");
						return;
					}
					
					if (printer == null || printer.length() == 0) {
						App.Current.showError(frm_item_lot_printer_init.this, "��ָ����ӡ�������ơ�");
						return;
					}

					final String qty=txt_quantity_cell.getContentText();

					//�����ӡ����
					PrintSetting printSetting = new PrintSetting();
					printSetting.Server = server;
					printSetting.Url = server;
					printSetting.Printer = printer;
					PrintHelper.savePrintServer(frm_item_lot_printer_init.this, printSetting);
					
					PrintRequest request = new PrintRequest();
					request.Server = server;
					request.Printer = printer;
					request.Code = "mm_item_lot_label";
					
					//׼����ӡ����
					request.Parameters = new HashMap<String,String>();
					request.Parameters.put("org_code", org_code);
					request.Parameters.put("item_code", item_code);
					request.Parameters.put("vendor_model", vendor_model);
					request.Parameters.put("lot_number", lot_number);
					request.Parameters.put("vendor_lot", vendor_lot);
					request.Parameters.put("date_code", date_code);
					request.Parameters.put("quantity", qty);
					request.Parameters.put("ut", ut);
					request.Parameters.put("user_code", usercode);
					
					for (int i=0; i<copies; i++) {
						Result<String> result = PrintHelper.print(request);
						if (result.HasError) {
							App.Current.showError(frm_item_lot_printer_init.this, result.Error);
						}
					}
				}
			});
		 }
	 }
}
