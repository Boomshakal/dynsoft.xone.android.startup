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

	public TextCell txt_work_order_code_cell; // ��������
	public TextCell txt_item_code_cell; //���ϱ���
	public TextCell txt_item_name_cell; //��������
	public TextCell txt_program_cell;//��������
	public TextCell txt_machine_cell; //��̨
	public TextCell txt_position_cell; //Zλ
	
	String task_order="";
	String machine="";
	String item_code="";
	//���ö��ڵ�XML�ļ�
	@Override
	public void setContentView() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.pn_smt_z_editor, this, true);
		view.setLayoutParams(lp);
	}
	//������ʾ�ؼ�
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
			this.txt_work_order_code_cell.setLabelText("��������");
			this.txt_work_order_code_cell.setReadOnly();
		}		
		if (this.txt_machine_cell != null) {
			this.txt_machine_cell.setLabelText("��̨");
			this.txt_machine_cell.setReadOnly();
		}		
		if (this.txt_program_cell != null) {
			this.txt_program_cell.setLabelText("��������");
			this.txt_program_cell.setReadOnly();
		}				
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("���ϱ���");
			this.txt_item_code_cell.setReadOnly();
		}		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("��������");
			this.txt_item_name_cell.setReadOnly();
		}
		
		if (this.txt_position_cell != null) {
			this.txt_position_cell.setLabelText("Zλ");
			this.txt_position_cell.setReadOnly();
		}	
	}
	
	//ɨ����
	@Override
	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();
	      
		
        if (bar_code.startsWith("WO:"))
        {
             //ɨ�蹤������
        	 this.txt_work_order_code_cell.setContentText(bar_code.toString().split(":")[1]);
        	 task_order=bar_code.toString().split(":")[1];
        }
        else if (bar_code.startsWith("DEV:"))
        {
        	 //ɨ�����������
        	this.txt_machine_cell.setContentText(bar_code.toString().split(":")[1]);
        	machine=bar_code.toString().split(":")[1];
        	
        	//���ݻ�̨�Զ�������������
        	this.loadGetprogram(task_order,machine);
        }
		//else if (bar_code.startsWith("R:"))
		//{
		//���ϱ�ǩ
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
			  App.Current.showError(this.getContext(), "�Ƿ����룬��ɨ����ȷ�Ķ�ά�룡"+bar_code.toString());
		  }
	}
	
	///���ݹ�������̨����������
	public void loadGetprogram(String task_order,String machine)
	{
		this.ProgressDialog.show();
		if(task_order=="")
		{
			App.Current.showError(fm_smt_z_editor.this.getContext(), "����ɨ�蹤����ά��");
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
					App.Current.showError(fm_smt_z_editor.this.getContext(), "�Ҳ�����Ӧ�ĳ�������Ϣ");
					
					fm_smt_z_editor.this.Header.setTitleText("�Ҳ�����Ӧ�ĳ�������Ϣ");
					return;
				}			
				  fm_smt_z_editor.this.txt_program_cell.setContentText(row.getValue("program", ""));
			}
		});			
	}
	
	
	///�������ϱ����������˵��
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
					App.Current.showError(fm_smt_z_editor.this.getContext(), "�Ҳ�����Ӧ��������Ϣ");
					return;
				}			
				fm_smt_z_editor.this.txt_item_name_cell.setContentText(row.getValue("name", ""));
			}
		});
	}
	
	///�������ϱ����������Zλ
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
					App.Current.showError(fm_smt_z_editor.this.getContext(), "�Ҳ�����Ӧ������Zλ��Ϣ");
					return;
				}			
				fm_smt_z_editor.this.txt_position_cell.setContentText(row.getValue("position", 0).toString());
			}
		});
	}
		

    /////
	//�ύ����
	////
	@Override
	public void commit() {

	}
	
	//��ӡ����
	public void printLabel() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("task_order", this.task_order);
		parameters.put("machine", this.machine);
		parameters.put("item_code", this.item_code);
		App.Current.Print("fm_smt_z_label", "��ӡZλ��ǩ", parameters);
	}
	
	//�������
	public void clear() {
		
		
		this.txt_item_code_cell.setContentText("");		
		this.txt_item_name_cell.setContentText("");	
		this.txt_program_cell.setContentText("");		
		
		
	}
}
