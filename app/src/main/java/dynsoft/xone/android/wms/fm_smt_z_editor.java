package dynsoft.xone.android.wms;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.SwitchCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.PromptCallback;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.Book;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.start.FrmLogin;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Xml.Encoding;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;

public class fm_smt_z_editor extends pn_editor {

	public fm_smt_z_editor(Context context) {
		super(context);
	}

	public TextCell txt_work_order_code_cell; // 工单编码
	public TextCell txt_item_code_cell; //物料编码
	public TextCell txt_item_name_cell; //物料名称
	public TextCell txt_program_cell;//程序名称
	public TextCell txt_machine_cell; //机台
	public TextCell txt_position_cell; //Z位
	
	String task_order="";
	String machine="";
	String item_code="";
	//设置对于的XML文件
	@Override
	public void setContentView() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.pn_smt_z_editor, this, true);
		view.setLayoutParams(lp);
	}
	//设置显示控件
	@Override
	public void onPrepared() {

		super.onPrepared();

		 
		this.txt_work_order_code_cell = (TextCell) this
				.findViewById(R.id.txt_work_order_code_cell);
		this.txt_machine_cell = (TextCell) this
				.findViewById(R.id.txt_machine_cell);
		this.txt_program_cell = (TextCell) this
				.findViewById(R.id.txt_program_cell);
		
		this.txt_item_code_cell = (TextCell) this
				.findViewById(R.id.txt_item_code_cell);
		
		this.txt_item_name_cell = (TextCell) this
				.findViewById(R.id.txt_item_name_cell);
		this.txt_position_cell = (TextCell) this
				.findViewById(R.id.txt_position_cell);
		 

		
		this.CommitButton = (ImageButton)this.findViewById(R.id.btn_commit);
		if (this.CommitButton != null){
			this.CommitButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_white"));
			this.CommitButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					fm_smt_z_editor.this.printLabel();
				}
			});
		}
		

		if (this.txt_work_order_code_cell != null) {
			this.txt_work_order_code_cell.setLabelText("工单编码");
			this.txt_work_order_code_cell.setReadOnly();
		}		
		if (this.txt_machine_cell != null) {
			this.txt_machine_cell.setLabelText("机台");
			this.txt_machine_cell.setReadOnly();
		}		
		if (this.txt_program_cell != null) {
			this.txt_program_cell.setLabelText("程序名称");
			this.txt_program_cell.setReadOnly();
		}				
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("物料编码");
			this.txt_item_code_cell.setReadOnly();
		}		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("物料名称");
			this.txt_item_name_cell.setReadOnly();
		}
		
		if (this.txt_position_cell != null) {
			this.txt_position_cell.setLabelText("Z位");
			this.txt_position_cell.setReadOnly();
		}	
	}
	
	//扫条码
	@Override
	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();
	      
		
        if (bar_code.startsWith("WO:"))
        {
             //扫描工单条码
        	 this.txt_work_order_code_cell.setContentText(bar_code.toString().split(":")[1]);
        	 task_order=bar_code.toString().split(":")[1];
        }
        else if (bar_code.startsWith("DEV:"))
        {
        	 //扫描程序名条码
        	this.txt_machine_cell.setContentText(bar_code.toString().split(":")[1]);
        	machine=bar_code.toString().split(":")[1];
        	
        	//根据机台自动带出程序名称
        	this.loadGetprogram(task_order,machine);
        }
		//else if (bar_code.startsWith("R:"))
		//{
		//物料标签
		//   	 this.txt_item_code_cell.setContentText(bar_code.toString().split(":")[1]);
        //    	 item_code=bar_code.toString().split(":")[1];
        //  	 this.loadGetItemname(item_code);
        //  	 this.loadGetposition(task_order, machine, item_code);
		//}
        else if (bar_code.startsWith("CRQ:")) {
			String str = bar_code.substring(4, barcode.length());
			String[] arr = str.split("-");
			item_code = arr[1];
			this.txt_item_code_cell.setContentText(item_code);
			this.loadGetItemname(item_code);
			this.loadGetposition(task_order, machine, item_code);
			
		}
	      else
		  {
			  App.Current.showError(this.getContext(), "非法条码，请扫描正确的二维码！"+bar_code.toString());
		  }
	}
	
	///根据工单、机台带出程序名
	public void loadGetprogram(String task_order,String machine)
	{
		this.ProgressDialog.show();
		if(task_order=="")
		{
			App.Current.showError(fm_smt_z_editor.this.getContext(), "请先扫描工单二维码");
			return;
		}
		String sql ="SELECT TOP 1 program   FROM  fm_smt_position WHERE task_order=? AND machine=?";
		Parameters p  =new Parameters().add(1, task_order).add(2, machine);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				fm_smt_z_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(fm_smt_z_editor.this.getContext(), result.Error);
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(fm_smt_z_editor.this.getContext(), "找不到对应的程序名信息");
					
					fm_smt_z_editor.this.Header.setTitleText("找不到对应的程序名信息");
					return;
				}			
				  fm_smt_z_editor.this.txt_program_cell.setContentText(row.getValue("program", ""));
			}
		});			
	}
	
	
	///根据物料编码带出物料说明
	public void loadGetItemname(String item_code)
	{
		this.ProgressDialog.show();
		
		String sql ="SELECT top 1 name FROM dbo.mm_item WHERE code=?";
		Parameters p  =new Parameters().add(1, item_code);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				fm_smt_z_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(fm_smt_z_editor.this.getContext(), result.Error);
					return;
				}					
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(fm_smt_z_editor.this.getContext(), "找不到对应的物料信息");
					return;
				}			
				fm_smt_z_editor.this.txt_item_name_cell.setContentText(row.getValue("name", ""));
			}
		});
	}
	
	///根据物料编码带出物料Z位
	public void loadGetposition(String task_order,String machine,String item_code)
	{
		this.ProgressDialog.show();
		
		String sql ="SELECT TOP 1 position FROM fm_smt_position WHERE task_order=? AND machine=? AND item_code=?";
		Parameters p  =new Parameters().add(1, task_order).add(2, machine).add(3, item_code);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				fm_smt_z_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(fm_smt_z_editor.this.getContext(), result.Error);
					return;
				}					
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(fm_smt_z_editor.this.getContext(), "找不到对应的物料Z位信息");
					return;
				}			
				fm_smt_z_editor.this.txt_position_cell.setContentText(row.getValue("position", 0).toString());
			}
		});
	}
		

    /////
	//提交操作
	////
	@Override
	public void commit() {

	}
	
	//打印操作
	public void printLabel() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("task_order", this.task_order);
		parameters.put("machine", this.machine);
		parameters.put("item_code", this.item_code);
		App.Current.Print("fm_smt_z_label", "打印Z位标签", parameters);
	}
	
	//清空数据
	public void clear() {
		
		
		this.txt_item_code_cell.setContentText("");		
		this.txt_item_name_cell.setContentText("");	
		this.txt_program_cell.setContentText("");		
		
		
	}
}
