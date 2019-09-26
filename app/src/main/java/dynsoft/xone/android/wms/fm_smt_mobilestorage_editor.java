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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;

public class fm_smt_mobilestorage_editor extends pn_editor {

	public fm_smt_mobilestorage_editor(Context context) {
		super(context);
	}

 
	public TextCell txt_mobile_cell;//��ת��
	public TextCell txt_storage_cell; //��λ
	public TextCell txt_item_code_cell; //���ϱ���
	public TextCell txt_item_name_cell; //���ϱ���
	public TextCell txt_usernumber_code_cell; //���ϱ���
	public CheckBox isbinding; //�����
	private int scan_count ;
	private boolean checkbool ;	
	private DataRow _order_row;
	private DataRow _lot_row;
	private Integer _total = 0;
	private Long _rownum;
	private String work_order_code;
	

	//���ö��ڵ�XML�ļ�
	@Override
	public void setContentView() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.pn_smt_mobilestorage_editor, this, true);
		view.setLayoutParams(lp);
	}
	
	//  ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>(); 
	//  final String [] nItems = {"���","��ȡ����ƫ��","���ϲ���","�����","����","��Ӧ�Ʋ���","����"};

	//������ʾ�ؼ�
	@Override
	public void onPrepared() {

		super.onPrepared();
		
		scan_count=0;
		checkbool=true;
 
		this.txt_mobile_cell = (TextCell) this
				.findViewById(R.id.txt_mobile_cell);
		
		this.txt_storage_cell = (TextCell) this
				.findViewById(R.id.txt_storage_cell);
		
		this.txt_item_code_cell = (TextCell) this
				.findViewById(R.id.txt_item_code_cell);
		
		this.txt_item_name_cell = (TextCell) this
				.findViewById(R.id.txt_item_name_cell);
		
		this.txt_usernumber_code_cell = (TextCell) this
				.findViewById(R.id.txt_usernumber_code_cell);
		
		this.isbinding = (CheckBox) this
				.findViewById(R.id.isbinding);
		this.isbinding.isChecked();
		//this.isbinding.setReadOnly();
		
		
		if (this.txt_mobile_cell != null) {
			this.txt_mobile_cell.setLabelText("��ת������");
			this.txt_mobile_cell.setReadOnly();
		}
		if (this.txt_storage_cell != null) {
			this.txt_storage_cell.setLabelText("��λ����");
			this.txt_storage_cell.setReadOnly();
		}			
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("���ϱ���");
			this.txt_item_code_cell.setReadOnly();
		}
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("��������");
			this.txt_item_name_cell.setReadOnly();
		}	
		if (this.txt_usernumber_code_cell != null) {
			this.txt_usernumber_code_cell.setLabelText("�����û�");
			this.txt_usernumber_code_cell.setReadOnly();
			this.txt_usernumber_code_cell.setContentText(App.Current.UserCode);			
		}		   
	}
	
	//ɨ����
	@Override
	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();
	      
        if (bar_code.startsWith("TL:"))
        {
        	
             //ɨ����ת��
        	 this.txt_mobile_cell.setContentText(bar_code.toString().split(":")[1]);
        	// work_order_code=bar_code.toString().split(":")[1];
        	// this. loadsteel_mesh(bar_code.toString());
        }
        else if (bar_code.startsWith("SMT-"))
        {
             //ɨ�财λ
        	 this.txt_storage_cell.setContentText(bar_code.toString());
        	  
        	 //loadItem(bar_code.toString());
        }
        
        
        else if (bar_code.startsWith("R"))
        {
             //ɨ������
        	 this.txt_item_code_cell.setContentText(bar_code.toString());
        	  
        	 loadItem(bar_code.toString());
        }
        else if (bar_code.startsWith("MZ")||bar_code.startsWith("M")||bar_code.startsWith("MA"))
        {
        	//ɨ�蹤��
           	 this.txt_usernumber_code_cell.setContentText(bar_code.toString());
        }
	      else
		  {
			  App.Current.showError(this.getContext(), "�Ƿ����룬��ɨ����ȷ�Ķ�ά�룡"+bar_code.toString());
		  }
	}
	
	 
	
	
	///��������������Ӧ������Ϣ
	public void loadItem(String code)
	{
		this.ProgressDialog.show();
		
		String sql ="SELECT code,name FROM dbo.mm_item WHERE code=?";
		Parameters p  =new Parameters().add(1, code);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				fm_smt_mobilestorage_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(fm_smt_mobilestorage_editor.this.getContext(), result.Error);
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					//App.Current.showError(pn_smt_steel_mesh_storage_editorr.this.getContext(), "�Ҳ�����Ӧ����Ϣ");
					
					//pn_smt_steel_mesh_storage_editorr.this.Header.setTitleText("�Ҳ�������Ӧ����Ϣ");
					return;
				}			
			 
				fm_smt_mobilestorage_editor.this.txt_item_name_cell.setContentText(row.getValue("name", ""));
			 

			}
		}); 
	}
	
	
	 

    /////
	//�ύ����
	////
	@Override
	public void commit() {
		//��feeder��ǳ�����״̬�����ڴ�ά�޼�¼�����Ӽ�¼
	
		
	 
		
		String mobile = this.txt_mobile_cell.getContentText().trim();
		if (mobile == null || mobile.length() == 0) {
			App.Current.showError(this.getContext(), "��װ����Ϣ����Ϊ�գ�");
			return;
		}	
		String storage = this.txt_storage_cell.getContentText().trim();
		if (storage == null || storage.length() == 0) {
			App.Current.showError(this.getContext(), "��λ��Ϣ����Ϊ�գ�");
			return;
		}	
		String item_code = this.txt_item_code_cell.getContentText().trim();	 
		if (item_code == null || item_code.length() == 0) {
			App.Current.showError(this.getContext(), "������Ϣ����Ϊ�գ�");
			return;
		}	
		String item_name = this.txt_item_name_cell.getContentText().trim();	 		
		String usernumber = this.txt_usernumber_code_cell.getContentText().trim();	 		
 
		boolean is_scrap = this.isbinding.isChecked();
		
		String sql =" exec mm_smt_mobilestorage_isnert ?,?,?,?,?,?";
		 
	 
		 
		Parameters p = new Parameters();
		p.add(1, mobile).add(2, storage).add(3, item_code).add(4, item_name).add(5, usernumber).add(6, is_scrap);
		App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
			@Override
			public void handleMessage(Message msg) {
				fm_smt_mobilestorage_editor.this.ProgressDialog.dismiss();

			
				App.Current.toastInfo(fm_smt_mobilestorage_editor.this.getContext(), "��װ����λ�󶨳ɹ�");				
				clear();
				
			}
		});
	}
	
	//��ӡ����
	public void printLabel() {
		//Map<String, String> parameters = new HashMap<String, String>();
		//parameters.put("item_code", this.Item_code);
		//App.Current.Print("mm_item_identifying_label", "��ӡ���ϱ�ʶ��", parameters);
	}
	
	//�������
	public void clear() {
		
		 	
		this.txt_mobile_cell.setContentText("");
		this.txt_storage_cell.setContentText("");
		this.txt_item_code_cell.setContentText("");
		this.txt_item_name_cell.setContentText("");
		 
	 
		
	}
}
