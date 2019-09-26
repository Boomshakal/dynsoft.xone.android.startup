package dynsoft.xone.android.core;

import java.util.HashMap;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.Calculator;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.PrintRequest;
import dynsoft.xone.android.data.PrintSetting;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.PrintHelper;
import dynsoft.xone.android.wms.pn_smt_feed_editor;
import dynsoft.xone.android.wms.pn_wo_issue_editor;

public class frm_printer extends Activity {

	private TextView txt_title;
	private ButtonTextCell txt_server_cell;
	private TextCell txt_url_cell;
	private ButtonTextCell txt_printer_cell;
	private DecimalCell txt_copy_cell;
	private Button btn_print;
	private ProgressDialog ProgressDialog;
	
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
		 this.setContentView(R.layout.frm_printer);
		 
		 this.ProgressDialog = new ProgressDialog(this);
	     this.ProgressDialog.setMessage("正在加载...");
	     this.ProgressDialog.setCancelable(false);
	        
	     txt_title = (TextView)this.findViewById(R.id.txt_title);
		 txt_server_cell = (ButtonTextCell)this.findViewById(R.id.txt_server_cell);
		 txt_url_cell = (TextCell)this.findViewById(R.id.txt_url_cell);
		 txt_printer_cell = (ButtonTextCell)this.findViewById(R.id.txt_printer_cell);
		 txt_copy_cell = (DecimalCell)this.findViewById(R.id.txt_copy_cell);
		 btn_print = (Button)this.findViewById(R.id.btn_print);
		 
		 if (txt_title != null){
			 String title = this.getIntent().getStringExtra("__print_title__");
			 if (title == null || title.length() == 0) {
				 title = "PRINTING";
			 }
			 txt_title.setText(title);
		 }
		 
		 if (txt_server_cell != null) {
			 txt_server_cell.setLabelText("服务器");
			 txt_server_cell.setLabelWidth(140);
			 txt_server_cell.setReadOnly();
			 this.txt_server_cell.Button.setOnClickListener(new OnClickListener(){
				 @Override
				 public void onClick(View v) {
					 frm_printer.this.chooseServer();
				 }
			 });
		 }
		 
		 if (txt_url_cell != null) {
			 txt_url_cell.setLabelText("地址");
			 txt_url_cell.setLabelWidth(140);
			 txt_url_cell.setReadOnly();
		 }
		 
		 if (txt_printer_cell != null) {
			 txt_printer_cell.setLabelText("打印机");
			 txt_printer_cell.setLabelWidth(140);
			 txt_printer_cell.setReadOnly();
			 this.txt_printer_cell.Button.setOnClickListener(new OnClickListener(){
				 @Override
				 public void onClick(View v) {
					 frm_printer.this.choosePrinter();
				 }
			 });
		 }
	
		 if (txt_copy_cell != null) {
			 txt_copy_cell.setLabelText("份数");
			 txt_copy_cell.setLabelWidth(140);
			 txt_copy_cell.setContentText("1");
			 txt_copy_cell.setReadOnly();
		 }
		 
		 PrintSetting printSetting = PrintHelper.loadPrintServer(this);
		 if (printSetting != null) {
			 txt_server_cell.setContentText(printSetting.Server);
			 txt_url_cell.setContentText(printSetting.Url);
			 txt_printer_cell.setContentText(printSetting.Printer);
		 }

		 if (btn_print != null) {
			 btn_print.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					frm_printer.this.print();
				}
			});
		 }
	 }

	 @Override
	 public void onActivityResult(int requestCode, int resultCode, Intent intent)
	 {
		 if (resultCode == Calculator.CalculatorResult) {
			 String result = intent.getStringExtra("result");
			 this.txt_copy_cell.setContentText(result);
		 }
	 }
	 
	 private DataRow _server_row;
	 
	 private void chooseServer()
	 {
		 String sql = "select * from core_print_server";
		 App.Current.DbPortal.ExecuteDataTableAsync(App.BookConnectorCode, sql, new ResultHandler<DataTable>(){
			@Override
    	    public void handleMessage(Message msg) {
				frm_printer.this.ProgressDialog.dismiss();
				
				final Result<DataTable> r = this.Value;
				if (r.HasError) {
					App.Current.showError(frm_printer.this, r.Error);
					return;
				}
				
				if (r.Value != null && r.Value.Rows.size() > 0) {
					DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        if (which >= 0) {
	                        	_server_row = r.Value.Rows.get(which);
	                        	txt_server_cell.setContentText(_server_row.getValue("name", ""));
	                        	txt_url_cell.setContentText(_server_row.getValue("url", ""));
	                        	txt_printer_cell.setContentText("");
	                        }
	                        dialog.dismiss();
	                    }
	                };
	                
	                final TableAdapter adapter = new TableAdapter(frm_printer.this) {
	                    @Override
	                    public View getView(int position, View convertView, ViewGroup parent) {
	                    	DataRow row = (DataRow)r.Value.Rows.get(position);
	                    	if (convertView == null) {
	                    		convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_core_server, null);
	                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
	                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/core_folder_green_16"));
	                    	}
	                        
	                    	TextView num = (TextView)convertView.findViewById(R.id.num);
	                        TextView txt_server = (TextView)convertView.findViewById(R.id.txt_server);
	                        
	                        num.setText(String.valueOf(position + 1));
	                        txt_server.setText(row.getValue("name", ""));

	                        return convertView;
	                    }
	                };
	                
	                adapter.DataTable = r.Value;
	                adapter.notifyDataSetChanged();
	                
	                new AlertDialog.Builder(frm_printer.this)
	                .setTitle("选择打印服务器")
	                .setSingleChoiceItems(adapter, 0, listener)
	                .setNegativeButton("取消", null).show();
				}
			}
		});
	 }
	 
	 private DataRow _printer_row;
	 
	 private void choosePrinter()
	 {
		 if (_server_row == null) {
			App.Current.showError(this, "请选择打印服务器。");
			return;
		 }
		 
		 String sql = "select * from core_print_server_printer where server_id=?";
		 Parameters p = new Parameters().add(1, _server_row.getValue("id", 0));
		 App.Current.DbPortal.ExecuteDataTableAsync(App.BookConnectorCode, sql, p, new ResultHandler<DataTable>(){
			@Override
    	    public void handleMessage(Message msg) {
				frm_printer.this.ProgressDialog.dismiss();
				
				final Result<DataTable> r = this.Value;
				if (r.HasError) {
					App.Current.showError(frm_printer.this, r.Error);
					return;
				}
				
				if (r.Value != null && r.Value.Rows.size() > 0) {
					DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        if (which >= 0) {
	                        	_printer_row = r.Value.Rows.get(which);
	                        	txt_printer_cell.setContentText(_printer_row.getValue("printer", ""));
	                        }
	                        dialog.dismiss();
	                    }
	                };
	                
	                final TableAdapter adapter = new TableAdapter(frm_printer.this) {
	                    @Override
	                    public View getView(int position, View convertView, ViewGroup parent) {
	                    	DataRow row = (DataRow)r.Value.Rows.get(position);
	                    	if (convertView == null) {
	                    		convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_core_printer, null);
	                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
	                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/core_printer_gray_16"));
	                    	}
	                        
	                    	TextView num = (TextView)convertView.findViewById(R.id.num);
	                        TextView txt_printer = (TextView)convertView.findViewById(R.id.txt_printer);
	                        
	                        num.setText(String.valueOf(position + 1));
	                        txt_printer.setText(row.getValue("printer", ""));

	                        return convertView;
	                    }
	                };
	                
	                adapter.DataTable = r.Value;
	                adapter.notifyDataSetChanged();
	                
	                new AlertDialog.Builder(frm_printer.this)
	                .setTitle("选择打印机")
	                .setSingleChoiceItems(adapter, 0, listener)
	                .setNegativeButton("取消", null).show();
				}
			}
		});
	 }

	 private void print()
	 {
		String server = txt_server_cell.getContentText();
		String url = txt_url_cell.getContentText();
		String printer = txt_printer_cell.getContentText();
		String copy = txt_copy_cell.getContentText();
		
		if (url == null || url.length() == 0) {
			App.Current.showError(this, "请指定打印服务器。");
			return;
		}
		
		if (printer == null || printer.length() == 0) {
			App.Current.showError(this, "请指定打印机。");
			return;
		}
		
		Integer copies = Integer.parseInt(copy);
		if (copies == 0) {
			App.Current.showError(frm_printer.this, "无效的打印份数。");
			return;
		}
		
		//保存打印设置
		PrintSetting printSetting = new PrintSetting();
		printSetting.Server = server;
		printSetting.Url = url;
		printSetting.Printer = printer;
		PrintHelper.savePrintServer(frm_printer.this, printSetting);
		
		PrintRequest request = new PrintRequest();
		request.Server = url;
		request.Printer = printer;
		request.Code = this.getIntent().getStringExtra("__print_code__");
		
		//准备打印参数
		request.Parameters = new HashMap<String,String>();
		request.Parameters.put("user_id", App.Current.UserID);
		request.Parameters.put("user_code", App.Current.UserCode);
		
		Bundle bundle = frm_printer.this.getIntent().getExtras();
		if (bundle != null && bundle.size() > 0) {
			Set<String> keys = bundle.keySet();
			for	(String key : keys) {
				request.Parameters.put(key, bundle.getString(key));
			}
		}
		
		for (int i=0; i<copies; i++) {
			Result<String> result = PrintHelper.print(request);
			if (result.HasError) {
				App.Current.showError(frm_printer.this, result.Error);
			}
		}
	 }
}
