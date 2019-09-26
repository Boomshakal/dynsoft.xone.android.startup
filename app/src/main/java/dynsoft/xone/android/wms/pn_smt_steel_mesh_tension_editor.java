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
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml.Encoding;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;

public class pn_smt_steel_mesh_tension_editor extends pn_editor {

	public pn_smt_steel_mesh_tension_editor(Context context) {
		super(context);
	}


	public ButtonTextCell txt_work_order_code_cell; // ��������
	public TextCell txt_item_code_cell; //���ϱ���
	public TextCell txt_item_name_cell; //��������
	public TextCell txt_plan_quantity_cell;//��������
	public TextCell txt_steel_mesh_code_cell; //��������
	public TextCell txt_usernumber_code_cell; //�û�����
	public TextCell txt_A_cell; //���ĵ�
	public TextCell txt_B_cell; //1��
	public TextCell txt_C_cell; //2��
	public TextCell txt_D_cell; //3��
	public TextCell txt_E_cell; //4��
	public  String work_orer_code;
	public CheckBox chk_is_scrap; //�Ƿ񱨷�
	public  int is_two_sides = 1;
	private int scan_count ;
	private boolean checkbool ;	
	private DataRow _order_row;
	private DataRow _lot_row;
	private Integer _total = 0;
	private Long _rownum;
	private String item_code1;
	private String task_order_code;
	private String item_code;
	private String item_name;
	

	//���ö��ڵ�XML�ļ�
	@Override
	public void setContentView() {
		LayoutParams lp = new LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.pn_smt_steel_mesh_tension_editor, this, true);
		view.setLayoutParams(lp);
	}
	

	//������ʾ�ؼ�
	@Override
	public void onPrepared() {

		super.onPrepared();
		
		 task_order_code = this.Parameters.get("task_order", "");		 
		 item_code = this.Parameters.get("item_code", "");		 
		 item_name = this.Parameters.get("item_name", "");
		
		scan_count=0;
		checkbool=true;
		this.txt_work_order_code_cell = (ButtonTextCell) this
				.findViewById(R.id.txt_work_order_code_cell);
		
		this.txt_item_code_cell = (TextCell) this
				.findViewById(R.id.txt_item_code_cell);
		
		this.txt_item_name_cell = (TextCell) this
				.findViewById(R.id.txt_item_name_cell);
	
		this.txt_plan_quantity_cell = (TextCell) this
				.findViewById(R.id.txt_plan_quantity_cell);
		
		this.txt_steel_mesh_code_cell = (TextCell) this
				.findViewById(R.id.txt_steel_mesh_code_cell);
		
		this.txt_usernumber_code_cell = (TextCell) this
				.findViewById(R.id.txt_user_code_cell);
		
		this.txt_A_cell = (TextCell) this
				.findViewById(R.id.txt_A_cell);
		this.txt_B_cell = (TextCell) this
				.findViewById(R.id.txt_B_cell);
		this.txt_C_cell = (TextCell) this
				.findViewById(R.id.txt_C_cell);
		this.txt_D_cell = (TextCell) this
				.findViewById(R.id.txt_D_cell);
		this.txt_E_cell = (TextCell) this
				.findViewById(R.id.txt_E_cell);

		 
	 

		if (this.txt_work_order_code_cell != null) {
			this.txt_work_order_code_cell.setLabelText("��������");
			//this.txt_work_order_code_cell.setReadOnly();
			this.txt_work_order_code_cell.Button.setOnClickListener(new OnClickListener() {
	                @Override
	                public void onClick(View view) {
	                    loadComfirmName(txt_work_order_code_cell);
	                }
	            });
			if(!TextUtils.isEmpty(task_order_code)) {
				this.txt_work_order_code_cell.setContentText(task_order_code);
			}
		}
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("���ϱ���");
			this.txt_item_code_cell.setReadOnly();
			if(!TextUtils.isEmpty(item_code)) {
				txt_item_code_cell.setContentText(item_code);
			}
		}
		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("��������");
			this.txt_item_name_cell.setReadOnly();
			if(!TextUtils.isEmpty(item_name)) {
				txt_item_name_cell.setContentText(item_name);
			}
		}

		if (this.txt_steel_mesh_code_cell != null) {
			this.txt_steel_mesh_code_cell.setLabelText("��������");
			this.txt_steel_mesh_code_cell.setReadOnly();
		}
		if (this.txt_usernumber_code_cell != null) {
			this.txt_usernumber_code_cell.setLabelText("������");
			txt_usernumber_code_cell.setContentText("MZ5493");
			this.txt_usernumber_code_cell.setReadOnly();
		}
		if (this.txt_A_cell != null) {
			this.txt_A_cell.setLabelText("���ĵ�");		 
		}
		
		if (this.txt_B_cell != null) {
			this.txt_B_cell.setLabelText("����1��");		 
		}
		
		if (this.txt_C_cell != null) {
			this.txt_C_cell.setLabelText("����2��");		 
		}
		
		if (this.txt_D_cell != null) {
			this.txt_D_cell.setLabelText("����3��");		 
		}
		
		if (this.txt_E_cell != null) {
			this.txt_E_cell.setLabelText("����4��");		 
		}
		
		 
		    
		
	}
	
	private void loadComfirmName(ButtonTextCell textcell_1) {
        Link link = new Link("pane://x:code=pn_smt_steel_mesh_parameter_mgr");
        link.Parameters.add("textcell", textcell_1);
        link.Open(null, getContext(), null);
        this.close();
    }
	
	//ɨ����
	@Override
	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();
	      
        if (bar_code.startsWith("R"))
        {
             //ɨ��feeder
        	 this.txt_steel_mesh_code_cell.setContentText(bar_code.toString());
        	 item_code1 = bar_code;
//        	 this.loadItem(work_order_code);
			load_work_code();
			//is_two_sides();
        }
       
        
	      else
		  {
			  App.Current.showError(this.getContext(), "�Ƿ����룬��ɨ����ȷ�Ķ�ά�룡"+bar_code.toString());
		  }
	}
	
  
		

    /////
	//�ύ����
	////
	@Override
	public void commit() {
		//��feeder��ǳ�����״̬�����ڴ�ά�޼�¼�����Ӽ�¼
	
		//App.Current.showError(this.getContext(), "������Ϣ����Ϊ�գ�");
		
		
		String work_order_code = this.txt_work_order_code_cell.getContentText().trim();
		if (work_order_code == null || work_order_code.length() == 0) {
			App.Current.showError(this.getContext(), "������Ϣ����Ϊ�գ�");
			return;
		}
		
		String steel_mesh_code = this.txt_steel_mesh_code_cell.getContentText().trim();
		if (steel_mesh_code == null || steel_mesh_code.length() == 0) {
			App.Current.showError(this.getContext(), "������Ϣ����Ϊ�գ�");
			return;
		}
		
		String item_code = this.txt_item_code_cell.getContentText().trim();
		if (item_code == null || item_code.length() == 0) {
			App.Current.showError(this.getContext(), "����������Ϣ����Ϊ�գ�");
			return;
		}
		 
		String a = this.txt_A_cell.getContentText().trim();
		if (a == null || a.length() == 0) {
			App.Current.showError(this.getContext(), "�������ĵ㲻��Ϊ�գ�");
			return;
		}else if(is_two_sides>1){
			boolean result = a.matches("([0-9]+)[,]([0-9]+)");
			if(result==false){
				App.Current.showError(this.getContext(), "����˫�������������,�ֿ�д");
			}
		}
		
		String b = this.txt_B_cell.getContentText().trim();
		if (b == null || b.length() == 0) {
			App.Current.showError(this.getContext(), "����A�㲻��Ϊ�գ�");
			return;
		}else if(is_two_sides>1){
			boolean result = b.matches("([0-9]+)[,]([0-9]+)");
			if(result==false){
				App.Current.showError(this.getContext(), "����˫�������������,�ֿ�д");
			}
		}
		String c = this.txt_B_cell.getContentText().trim();
		if (c == null || c.length() == 0) {
			App.Current.showError(this.getContext(), "����B�㲻��Ϊ�գ�");
			return;
		}else if(is_two_sides>1){
			boolean result = c.matches("([0-9]+)[,]([0-9]+)");
			if(result==false){
				App.Current.showError(this.getContext(), "����˫�������������,�ֿ�д");
			}
		}
		String d = this.txt_C_cell.getContentText().trim();
		if (d == null || d.length() == 0) {
			App.Current.showError(this.getContext(), "����C�㲻��Ϊ�գ�");
			return;
		}else if(is_two_sides>1){
			boolean result = d.matches("([0-9]+)[,]([0-9]+)");
			if(result==false){
				App.Current.showError(this.getContext(), "����˫�������������,�ֿ�д");
			}
		}
		String e = this.txt_D_cell.getContentText().trim();
		if (e == null || e.length() == 0) {
			App.Current.showError(this.getContext(), "����D�㲻��Ϊ�գ�");
			return;
		}else if(is_two_sides>1){
			boolean result = e.matches("([0-9]+)[,]([0-9]+)");
			if(result==false){
				App.Current.showError(this.getContext(), "����˫�������������,�ֿ�д");
			}
		}
		boolean is_scrap = false;
		if(a.matches("[0-9]+")) {
			if (Float.valueOf(d) < 34) {
				App.Current.showError(this.getContext(), "�����ѱ��ϵ���ύ���ɱ���");
				is_scrap = true;
			}
			if (Float.valueOf(e) < 34) {
				App.Current.showError(this.getContext(), "�����ѱ��ϵ���ύ���ɱ���");
				is_scrap = true;
			}
			if (Float.valueOf(c) < 34) {
				App.Current.showError(this.getContext(), "�����ѱ��ϵ���ύ���ɱ���");
				is_scrap = true;
			}
			if (Float.valueOf(a) < 34) {
				App.Current.showError(this.getContext(), "�����ѱ��ϵ���ύ���ɱ���");
				is_scrap = true;
			}
			if (Float.valueOf(b) < 34) {
				App.Current.showError(this.getContext(), "�����ѱ��ϵ���ύ���ɱ���");
				is_scrap = true;
			}
		}
//		SharedPreferences sharedPreferences = g
		
		String sql =" exec mm_smt_steel_mesh_insert_usage_log ?,?,?,?,?,?,?,?,?,?,?";
		 
		Parameters p = new Parameters();
		p.add(1,work_order_code ).add(2, item_code).add(3, item_name).add(4, steel_mesh_code)
		.add(5, a).add(6, b).add(7, c).add(8, d).add(9, e).add(10, "MA1448").add(11,is_scrap);

		App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
			@Override
			public void handleMessage(Message msg) {
				pn_smt_steel_mesh_tension_editor.this.ProgressDialog.dismiss();

				//Result<DataTable> result = this.Value;
				//if (result.HasError) {
				//	App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(),result.Error);
				//	return;
				//}
				App.Current.toastInfo(pn_smt_steel_mesh_tension_editor.this.getContext(), "����ʹ�õǼǼ�¼�ɹ�");				
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

	public void  load_work_code(){
		String sql = "\twith  #temp as (select top(1) * from [dbo].[mm_smt_steel_mesh_usage_log] where head_id =(select \n" +
				"\t\t\t\ta.id\n" +
				"\t\t\t\tFROM mm_smt_steel_mesh a\n" +
				"\t\t\t\t\n" +
				"\t\t\t\twhere  a.is_scrap = 0 and a.code =?)\n" +
				"\t\t\t\tORDER BY create_time DESC) \n" +
				"\n" +
				"\t\t\t\tselect b.item_id,c.name item_name ,b.code as work_code,c.code FROM  #temp a left join  \n" +
				"\t\t\t\tmm_wo_work_order_head b on  b.id = a.work_order_id\n" +
				"\t\t\t\tleft join mm_item c  on a.item_id = c.id   ";
		Parameters p = new Parameters();
		Log.e("LZH331",txt_steel_mesh_code_cell.getContentText());
		p.add(1, item_code1);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
			@Override
			public void handleMessage(Message msg) {
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_smt_steel_mesh_tension_editor.this.getContext(), result.Error);
					return;
				}
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(pn_smt_steel_mesh_tension_editor.this.getContext(),"���ݿ�  �д�");
					return;
				}
				else {
					Log.e("LZH374",row.getValue("work_code",""));
					pn_smt_steel_mesh_tension_editor.this.txt_work_order_code_cell.setContentText(row.getValue("work_code", ""));
					pn_smt_steel_mesh_tension_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
					pn_smt_steel_mesh_tension_editor.this.txt_item_code_cell.setContentText(row.getValue("code", ""));
				}
			}
		});
	}







	//�������
	public void clear() {
		
		this.txt_work_order_code_cell.setContentText("");
		this.txt_item_code_cell.setContentText("");		
		this.txt_item_name_cell.setContentText("");			
		this.txt_steel_mesh_code_cell.setContentText("");
		
		this.txt_A_cell.setContentText("");
		this.txt_B_cell.setContentText("");
		this.txt_C_cell.setContentText("");
		this.txt_D_cell.setContentText("");
		this.txt_E_cell.setContentText("");
		 
		
	}
//	public void is_two_sides() {
//		String sql = "select count(*) count from mm_smt_steel_mesh_storage_bind where item_code = ?";
//		Parameters p  =new Parameters().add(1,this.txt_steel_mesh_code_cell.getContentText().trim());
//		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
//			public void handleMessage(Message msg) {
//				Result<DataRow> result = this.Value;
//				if (result.HasError) {
//					App.Current.showError(pn_smt_steel_mesh_tension_editor.this.getContext(), result.Error);
//					return;
//				}
//				DataRow row = result.Value;
//				if (row == null||row.getValue("count",0)==0) {
//					App.Current.showError(pn_smt_steel_mesh_tension_editor.this.getContext(),"�˸���û�󶨿�λ����");
//				}
//				Log.e("LZH579",String.valueOf(row.getValue("count",0)));
//				is_two_sides = row.getValue("count", 0);
//				Log.e("LZH581",String.valueOf(is_two_sides));
//
//
//			}
//		});
//
//	}
}
