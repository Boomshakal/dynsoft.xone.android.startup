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

public class pn_smt_steel_Solder_paste_editor extends pn_editor {

	public pn_smt_steel_Solder_paste_editor(Context context) {
		super(context);
	}


	public TextCell txt_work_order_code_cell; // ��������
	public TextCell txt_item_code_cell; //���ϱ���
	public TextCell txt_item_name_cell; //��������
	public TextCell txt_solder_code_cell; //�������
	public TextCell txt_user_code; //������

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
				R.layout.pn_smt_steel_solder_paste_editor, this, true);
		view.setLayoutParams(lp);
	}
	
 

	//������ʾ�ؼ�
	@Override
	public void onPrepared() {

		super.onPrepared();
		
		scan_count=0;
		checkbool=true;
		this.txt_work_order_code_cell = (TextCell) this
				.findViewById(R.id.txt_work_order_code_cell);
		
		this.txt_item_code_cell = (TextCell) this
				.findViewById(R.id.txt_item_code_cell);
		
		this.txt_item_name_cell = (TextCell) this
				.findViewById(R.id.txt_item_name_cell);
		
		this.txt_solder_code_cell = (TextCell) this
				.findViewById(R.id.txt_solder_code_cell);
		
	 	this.txt_user_code = (TextCell) this
				.findViewById(R.id.txt_user_code);

		if (this.txt_work_order_code_cell != null) {
			this.txt_work_order_code_cell.setLabelText("��������");
			this.txt_work_order_code_cell.setReadOnly();
		}
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("���ϱ���");
			this.txt_item_code_cell.setReadOnly();
		}
		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("��������");
			this.txt_item_name_cell.setReadOnly();
		}

		if (this.txt_solder_code_cell != null) {
			this.txt_solder_code_cell.setLabelText("�������");
			this.txt_solder_code_cell.setReadOnly();
		}
		if (this.txt_user_code != null) {
			this.txt_user_code.setLabelText("������");
			this.txt_user_code.setReadOnly();
		}

	}
	
	//ɨ����
	@Override
	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();
	      
        if (bar_code.startsWith("CRQ:"))
        {
             //ɨ��feeder
        	 this.txt_work_order_code_cell.setContentText(bar_code.toString().split(":")[1].split("-")[0]);
        	 work_order_code=bar_code.toString().split(":")[1].split("-")[0];
        	 this.loadItem(work_order_code);
        }  
        else if (bar_code.startsWith("H")||bar_code.startsWith("X"))
        {
        	//ɨ��������ߺ콺
           	 this.txt_solder_code_cell.setContentText(bar_code.toString());
        }
        else if(bar_code.startsWith("M")||bar_code.startsWith("MZ")){
			this.txt_user_code.setContentText(bar_code.toString());
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
		
		String sql ="select a.code as  work_order_code, m.code as item_code,m.name as  item_name,CONVERT(VARCHAR(50),CONVERT(decimal(18,0),a.plan_quantity)) as plan_quantity  from mm_wo_work_order_head  a left join mm_item m on a.item_id=m.id where a.code=?";
		Parameters p  =new Parameters().add(1, code);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_smt_steel_Solder_paste_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_smt_steel_Solder_paste_editor.this.getContext(), result.Error);
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(pn_smt_steel_Solder_paste_editor.this.getContext(), "�Ҳ����ù�����Ӧ����Ϣ");
					
					pn_smt_steel_Solder_paste_editor.this.Header.setTitleText("�Ҳ����ù�����Ӧ����Ϣ");
					return;
				}			
				  pn_smt_steel_Solder_paste_editor.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
				  pn_smt_steel_Solder_paste_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
				 

			}
		});
	}
	
	
	

    /////
	//�ύ����
	////
	@Override
	public void commit() {
		//��feeder��ǳ�����״̬�����ڴ�ά�޼�¼�����Ӽ�¼
	
		
		String work_order_code = this.txt_work_order_code_cell.getContentText().trim();
		if (work_order_code == null || work_order_code.length() == 0) {
			App.Current.showError(this.getContext(), "������Ϣ����Ϊ�գ�");
			return;
		}
		
		String solder_code = this.txt_solder_code_cell.getContentText().trim();
		if (solder_code == null || solder_code.length() == 0) {
			App.Current.showError(this.getContext(), "������Ϣ����Ϊ�գ�");
			return;
		}
		
		 
		String item_code = this.txt_item_code_cell.getContentText().trim();
		if (item_code == null || item_code.length() == 0) {
			App.Current.showError(this.getContext(), "����������Ϣ����Ϊ�գ�");
			return;
		}
		String item_name = this.txt_item_name_cell.getContentText().trim();
		if (item_name == null || item_name.length() == 0) {
			App.Current.showError(this.getContext(), "����������Ϣ����Ϊ�գ�");
			return;
		}
		
		
		String sql =" exec mm_smt_solder_paste_work_order_insert ?,?,?,?,?";
		 
		Parameters p = new Parameters();
		p.add(1, work_order_code).add(2, item_code).add(3, item_name).add(4, solder_code).add(5,txt_user_code.getContentText());
	
		App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
			@Override
			public void handleMessage(Message msg) {
				pn_smt_steel_Solder_paste_editor.this.ProgressDialog.dismiss();

			 
				App.Current.toastInfo(pn_smt_steel_Solder_paste_editor.this.getContext(), "�󶨵ǼǼ�¼�ɹ�");				
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
		
		this.txt_work_order_code_cell.setContentText("");
		this.txt_item_code_cell.setContentText("");		
		this.txt_item_name_cell.setContentText("");			 
		this.txt_solder_code_cell.setContentText("");
	 
		
	}
}
