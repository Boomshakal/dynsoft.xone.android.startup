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

public class frm_smt_accessories_editor extends pn_editor {

	public frm_smt_accessories_editor(Context context) {
		super(context);
	}

	public TextCell txt_accessories_code_cell; // ��ű���
	public TextCell txt_accessories_name_cell; //�������
	public TextCell txt_item_name_cell; //��������
	public TextCell txt_inventory_quantity_cell;//�������
	public TextCell txt_quantity_cell; //��������
	public CheckBox chk_is_scrap; //������
	public TextCell txt_user_code_cell; //�����û�
	public TextCell txt_note_cell; //����˵��
	
	

	String accessories_code="";
	String user_code="";
	 
	//���ö��ڵ�XML�ļ�
	@Override
	public void setContentView() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.pn_smt_accessories_editor, this, true);
		view.setLayoutParams(lp);
	}
	//������ʾ�ؼ�
	@Override
	public void onPrepared() {

		super.onPrepared();

		 
		this.txt_accessories_code_cell = (TextCell) this
				.findViewById(R.id.txt_accessories_code_cell);
		this.txt_accessories_name_cell = (TextCell) this
				.findViewById(R.id.txt_accessories_name_cell);
		this.txt_item_name_cell = (TextCell) this
				.findViewById(R.id.txt_item_name_cell);	
		this.txt_inventory_quantity_cell = (TextCell) this
				.findViewById(R.id.txt_inventory_quantity_cell);
		this.txt_quantity_cell = (TextCell) this
				.findViewById(R.id.txt_quantity_cell);
		this.chk_is_scrap = (CheckBox) this
				.findViewById(R.id.chk_is_scrap);
		this.txt_user_code_cell = (TextCell) this
				.findViewById(R.id.txt_user_code_cell);
		this.txt_note_cell = (TextCell) this
				.findViewById(R.id.txt_note_cell);
		
		this.CommitButton = (ImageButton)this.findViewById(R.id.btn_commit);
		
		

		if (this.txt_accessories_code_cell != null) {
			this.txt_accessories_code_cell.setLabelText("��ά����");
			this.txt_accessories_code_cell.setReadOnly();
		}		
		if (this.txt_accessories_name_cell != null) {
			this.txt_accessories_name_cell.setLabelText("��������");
			this.txt_accessories_name_cell.setReadOnly();
		}		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("������");
			this.txt_item_name_cell.setReadOnly();
		}				
		if (this.txt_inventory_quantity_cell != null) {
			this.txt_inventory_quantity_cell.setLabelText("��ǰ���");
			this.txt_inventory_quantity_cell.setReadOnly();
		}		
		if (this.txt_quantity_cell != null) {
			this.txt_quantity_cell.setLabelText("��������");
			//this.txt_quantity_cell.setReadOnly();
		}
		if (this.txt_user_code_cell != null) {
			this.txt_user_code_cell.setLabelText("�����û�");
			this.txt_user_code_cell.setReadOnly();
		}
		
		if (this.txt_note_cell != null) {
			this.txt_note_cell.setLabelText("˵��");
			//this.txt_note_cell.setReadOnly();
		}
	
	}
	
	//ɨ����
	@Override
	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();
	      
		
        if (bar_code.startsWith("M-"))
        {
             //ɨ���ά����
        	 this.txt_accessories_code_cell.setContentText(bar_code.toString());//.split(":")[1]);
        	 accessories_code=bar_code.toString();//.split(":")[1];
        	//���ݶ�ά���������Ϣ
         	this.loadGetprogram(accessories_code);
        }
        else if (bar_code.startsWith("MZ")||bar_code.startsWith("MA"))
        {
        	 //ɨ������û�
        	this.txt_user_code_cell.setContentText(bar_code.toString());//.split(":")[1]);
        	user_code=bar_code.toString();///.split(":")[1];	        	
        }
	      else
		  {
			  App.Current.showError(this.getContext(), "�Ƿ����룬��ɨ����ȷ�Ķ�ά�룡"+bar_code.toString());
		  }
	}
	
	///���ݶ�ά�������Ϣ
	public void loadGetprogram(String accessories_code)
	{
		this.ProgressDialog.show();
		if(accessories_code=="")
		{
			App.Current.showError(frm_smt_accessories_editor.this.getContext(), "����ɨ���ά��");
			return;
		}
		String sql ="SELECT level_code,accessories_name,code,item_code,inventory_quantity  FROM  fm_smt_accessories_head WHERE code=? ";
		Parameters p  =new Parameters().add(1, accessories_code);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				frm_smt_accessories_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(frm_smt_accessories_editor.this.getContext(), result.Error);
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(frm_smt_accessories_editor.this.getContext(), "�Ҳ�����Ӧ�Ķ�ά����Ϣ");
					
					frm_smt_accessories_editor.this.Header.setTitleText("�Ҳ�����Ӧ�Ķ�ά����Ϣ");
					return;
				}			
				  frm_smt_accessories_editor.this.txt_item_name_cell.setContentText(row.getValue("item_code", ""));
				  frm_smt_accessories_editor.this.txt_accessories_code_cell.setContentText(row.getValue("code", ""));
				  frm_smt_accessories_editor.this.txt_accessories_name_cell.setContentText(row.getValue("accessories_name", ""));
				  frm_smt_accessories_editor.this.txt_inventory_quantity_cell.setContentText(row.getValue("inventory_quantity",0).toString());
			}
		});			
	}
	
	
 
	

		

    /////
	//�ύ����
	////
	@Override
	public void commit() {

		if (accessories_code == null) {
			App.Current.toastInfo(this.getContext(), "��ɨ���ά�롣");
			return;
		}
		
		if (user_code == null) {
			App.Current.toastInfo(this.getContext(), "��ɨ������û���");
			return;
		}
    	final String accessories_code = txt_accessories_code_cell.getContentText().trim();
		final String user_code = txt_user_code_cell.getContentText().trim();
		final String quantity =txt_quantity_cell.getContentText().trim();
		final String note = txt_note_cell.getContentText().trim();
		boolean is_scrap = this.chk_is_scrap.isChecked();
		
		String sql = "exec p_fm_smt_accessories_create ?,?,?,?,?";
		Parameters p = new Parameters().add(1,user_code).add(2, accessories_code).add(3, quantity).add(4, note).add(5, is_scrap);
		Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(this.Connector, sql, p);
		if (r.HasError) {
			App.Current.toastError( this.getContext(), r.Error);
			this.clear();
			return;
		}		
		if (r.Value > 0) {
			App.Current.toastInfo(this.getContext(), "��������Ǽǳɹ���");
			this.clear();
		}
		
	}

	
	//�������
	public void clear() {	
		this.txt_user_code_cell.setContentText("");		
		this.txt_item_name_cell.setContentText("");	
		this.txt_user_code_cell.setContentText("");	
		this.txt_accessories_code_cell.setContentText("");		
		this.txt_quantity_cell.setContentText("");	
		this.txt_inventory_quantity_cell.setContentText("");	
		this.txt_note_cell.setContentText("");	
	}
}
