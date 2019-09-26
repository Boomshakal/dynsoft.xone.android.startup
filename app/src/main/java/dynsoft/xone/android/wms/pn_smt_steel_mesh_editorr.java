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

public class pn_smt_steel_mesh_editorr extends pn_editor {

	public pn_smt_steel_mesh_editorr(Context context) {
		super(context);
	}


	public ButtonTextCell txt_work_order_code_cell; // ��������
	public TextCell txt_item_code_cell; //���ϱ���
	public TextCell txt_item_name_cell; //��������
	public TextCell txt_plan_quantity_cell;//��������
	public TextCell txt_steel_mesh_code_cell; //��������
	public TextCell txt_usernumber_code_cell; //�û�����
	public TextCell txt_quantity_cell; //ʹ�ô���
	public TextCell txt_accumulative_use_count_cell; //�ۼ�ʹ�ô���
	public CheckBox chk_is_scrap; //�Ƿ񱨷�
	public String steel_mesh_code;
	public TextCell txt_A_cell; //���ĵ�
	public TextCell txt_B_cell; //1��
	public TextCell txt_C_cell; //2��
	public TextCell txt_D_cell; //3��
	public TextCell txt_E_cell; //4��
	public  int is_two_sides=1;
	private int scan_count ;
	private boolean checkbool ;	
	private DataRow _order_row;
	private DataRow _lot_row;
	private Integer _total = 0;
	private Long _rownum;
	private String work_order_code;
	private String task_order_code;
	private String item_code;
	private String item_name;
	private BigDecimal plan_quantity;
	public int flage = 0;

	//���ö��ڵ�XML�ļ�
	@Override
	public void setContentView() {
		LayoutParams lp = new LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.pn_smt_steel_mesh_editor, this, true);
		view.setLayoutParams(lp);
	}
	
	//  ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>(); 
	//  final String [] nItems = {"���","��ȡ����ƫ��","���ϲ���","�����","����","��Ӧ�Ʋ���","����"};

	//������ʾ�ؼ�
	@Override
	public void onPrepared() {

		super.onPrepared();
		
		 task_order_code = this.Parameters.get("task_order", "");		 
		 item_code = this.Parameters.get("item_code", "");		 
		 item_name = this.Parameters.get("item_name", "");
		 plan_quantity = this.Parameters.get("plan_quantity", new BigDecimal(0));
		 
		 
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
				.findViewById(R.id.txt_usernumber_code_cell);
		
		this.txt_quantity_cell = (TextCell) this
				.findViewById(R.id.txt_quantity_cell);

		this.txt_accumulative_use_count_cell = (TextCell) this
				.findViewById(R.id.txt_accumulative_use_count_cell);
		
		
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

		
		this.chk_is_scrap = (CheckBox) this
				.findViewById(R.id.chk_is_scrap);

		if (this.txt_work_order_code_cell != null) {
			this.txt_work_order_code_cell.setLabelText("��������");
			this.txt_work_order_code_cell.setReadOnly();
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

		if (this.txt_plan_quantity_cell != null) {
			this.txt_plan_quantity_cell.setLabelText("��������");
			this.txt_plan_quantity_cell.setReadOnly();
			 
			txt_plan_quantity_cell.setContentText(String.valueOf(plan_quantity.floatValue()));
			 
		}

		if (this.txt_steel_mesh_code_cell != null) {
			this.txt_steel_mesh_code_cell.setLabelText("��������");
			//this.txt_steel_mesh_code_cell.setReadOnly();
		}
		
		if (this.txt_accumulative_use_count_cell != null) {
			this.txt_accumulative_use_count_cell.setLabelText("�ۼƴ���");
			this.txt_accumulative_use_count_cell.setReadOnly();
		}
		
		if (this.txt_usernumber_code_cell != null) {
			this.txt_usernumber_code_cell.setLabelText("������");
			txt_usernumber_code_cell.setContentText("MZ5493");

		}
		    
		if (this.txt_quantity_cell != null) {
			this.txt_quantity_cell.setLabelText("���δ���");
			//this.txt_quantity_cell.setReadOnly();
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
        Link link = new Link("pane://x:code=pn_smt_steel_mesh_parameter_out_mgr");
        link.Parameters.add("textcell", textcell_1);
        link.Open(null, getContext(), null);
        this.close();
    }
	//ɨ����
	@Override
	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();
	      
        if (bar_code.startsWith("WO:"))
        {
             //ɨ��feeder
        	 this.txt_work_order_code_cell.setContentText(bar_code.toString().split(":")[1]);
        	 work_order_code=bar_code.toString().split(":")[1];
        	 this.loadItem(work_order_code);
        }
        else if (bar_code.startsWith("R"))
        {
             steel_mesh_code = bar_code;
        	 this.txt_steel_mesh_code_cell.setContentText(bar_code.toString());
        	 load_ifmesh(bar_code);
        	loadsteel_mesh(bar_code);
			//is_two_sides();
        }
        else if (bar_code.startsWith("MZ")||bar_code.startsWith("M")||bar_code.startsWith("MA"))
        {
        	//ɨ�蹤��txt_usernumber_code_cell
           	 this.txt_usernumber_code_cell.setContentText(bar_code.toString());
        }
	      else
		  {
			  App.Current.showError(this.getContext(), "�Ƿ����룬��ɨ����ȷ�Ķ�ά�룡"+bar_code.toString());
		  }
	}










//���������ж��ڲ��ڿ��
	public void load_ifmesh(String mesh_code )
	{

		//String sql1 ="SELECT count(*) as count FROM  fm_smt_position WHERE task_order=? AND item_code=?";
		String sql1 = "DECLARE @out_time DATETIME\n" +
				"DECLARE @back_time DATETIME\n" +
				"\n" +
				"DECLARE @headid BIGINT\n" +

				"SET @headid = (SELECT id FROM mm_smt_steel_mesh WHERE code = ? AND is_scrap = 0)"+
				"SET @out_time = (SELECT TOP(1) create_time FROM dbo.mm_smt_steel_mesh_usage_log WHERE head_id = @headid AND state = 0 ORDER BY create_time DESC)\n" +
				"SET @back_time = (SELECT TOP(1) create_time FROM dbo.mm_smt_steel_mesh_usage_log WHERE head_id = @headid AND state = 1 ORDER BY create_time DESC)\n" +
				"IF(@out_time is NULL)\n" +
				"BEGIN\n" +
				"RAISERROR ('����û���£��ù���Ա����ά��(������¸����ɺ��Ӵδ���)',16,1)\n" +
				"SELECT flag = 0\n" +

				"END\n" +
				"ELSE IF(@back_time is NULL)\n" +
				"BEGIN\n" +
				"RAISERROR ('����û���£��ù���Ա����ά��(������¸����ɺ��Ӵδ���)',16,1)\n" +
				"SELECT flag = 0\n" +

				"END\n" +
				"\n" +
				"ELSE IF(@back_time >@out_time)\n" +
				"BEGIN\n" +
				"SELECT flag = 1\n" +
				"END\n" +
				"ELSE\n" +
				"BEGIN\n" +
				"SELECT flag = 0\n" +
				"END";
		Parameters p1  =new Parameters().add(1, mesh_code );
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql1, p1, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_smt_steel_mesh_editorr.this.ProgressDialog.dismiss();

				Result<DataRow> result = this.Value;
				DataRow row = result.Value;
				if (result.HasError) {
					App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(), result.Error);

				}

				else if (row == null) {
					App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(), "�ж����϶������ܱ�������");
					return;
				}
			    else if (row.getValue("flag", 0)==0)
			    {
					App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(), result.Error);
			    	App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(), "�������ڿ⣡");
			    	 pn_smt_steel_mesh_editorr.this.txt_steel_mesh_code_cell.setContentText("");
			    }
				else if(row.getValue("flag", 0)==1){
					pn_smt_steel_mesh_editorr.this.txt_steel_mesh_code_cell.setContentText(steel_mesh_code);
				}
			}
		});
		//return count.toString();
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
				pn_smt_steel_mesh_editorr.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				DataRow row = result.Value;
				if (result.HasError) {
					App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(), result.Error);
					return;
				}
				

				else if (row == null) {
					App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(), "�Ҳ����ù�����Ӧ����Ϣ");
					
					pn_smt_steel_mesh_editorr.this.Header.setTitleText("�Ҳ����ù�����Ӧ����Ϣ");
					return;
				}
				else {
					pn_smt_steel_mesh_editorr.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
					pn_smt_steel_mesh_editorr.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
					pn_smt_steel_mesh_editorr.this.txt_plan_quantity_cell.setContentText(row.getValue("plan_quantity", ""));
					pn_smt_steel_mesh_editorr.this.txt_quantity_cell.setContentText(row.getValue("plan_quantity", ""));
				}
			}
		});
	}

	///��������������Ӧ������Ϣ
		public void loadsteel_mesh(String steel_mesh_code)
		{
			this.ProgressDialog.show();
	
			
			
			
			
			
			String sql ="SELECT  TOP 1  CONVERT(varchar(20),CASE WHEN accumulative_use_count IS NULL THEN '0' ELSE accumulative_use_count END) AS accumulative_use_count FROM mm_smt_steel_mesh WHERE code=? AND is_scrap = 0 ";
			Parameters p  =new Parameters().add(1, steel_mesh_code);
			App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
				@Override
	    	    public void handleMessage(Message msg) {
					pn_smt_steel_mesh_editorr.this.ProgressDialog.dismiss();
					
					Result<DataRow> result = this.Value;
					if (result.HasError) {
						App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(), result.Error);
						return;
					}					
					DataRow row = result.Value;
					if (row == null) {
						return;
					}			
					pn_smt_steel_mesh_editorr.this.txt_accumulative_use_count_cell.setContentText(row.getValue("accumulative_use_count", ""));



				}
			});
		}
		

    /////
	//�ύ����
	////
	@Override
	public void commit() {
		//��feeder��ǳ�����״̬�����ڴ�ά�޼�¼�����Ӽ�¼
		if(flage ==0) {
			flage = 1;
			Log.e("LZH428", String.valueOf(is_two_sides));
			String work_order_code = this.txt_work_order_code_cell.getContentText().trim();
			if (work_order_code == null || work_order_code.length() == 0) {
				App.Current.showError(this.getContext(), "������Ϣ����Ϊ�գ�");
				return;
			}

			String steel_mesh_code1 = this.txt_steel_mesh_code_cell.getContentText().trim();
			if (steel_mesh_code1 == null || steel_mesh_code1.length() == 0) {
				App.Current.showError(this.getContext(), "������Ϣ����Ϊ�գ�");
				return;
			}

			String quantity = this.txt_quantity_cell.getContentText().trim();
			if (quantity == null || quantity.length() == 0) {
				App.Current.showError(this.getContext(), "ʹ�ô�����Ϣ����Ϊ�գ�");
				return;
			}

			String item_code = this.txt_item_code_cell.getContentText().trim();
			if (item_code == null || item_code.length() == 0) {
				App.Current.showError(this.getContext(), "����������Ϣ����Ϊ�գ�");
				return;
			}
			String usernumber = this.txt_usernumber_code_cell.getContentText().trim();
			if (usernumber == null || usernumber.length() == 0) {
				App.Current.showError(this.getContext(), "����Ա��Ϣ����Ϊ�գ�");
				return;
			}


			String a = this.txt_A_cell.getContentText().trim();
			if (a == null || a.length() == 0) {
				App.Current.showError(this.getContext(), "�������ĵ㲻��Ϊ�գ�");
				return;
			} else if (is_two_sides > 1) {
				boolean result = a.matches("([0-9]+)[,]([0-9]+)");
				if (result == false) {
					App.Current.showError(this.getContext(), "����˫�������������,�ֿ�д");
				}
			}

			String b = this.txt_B_cell.getContentText().trim();
			if (b == null || b.length() == 0) {
				App.Current.showError(this.getContext(), "����A�㲻��Ϊ�գ�");
				return;
			} else if (is_two_sides > 1) {
				boolean result = b.matches("([0-9]+)[,]([0-9]+)");
				if (result == false) {
					App.Current.showError(this.getContext(), "����˫�������������,�ֿ�д");
				}
			}
			String c = this.txt_C_cell.getContentText().trim();
			if (a == null || c.length() == 0) {
				App.Current.showError(this.getContext(), "����B�㲻��Ϊ�գ�");

				return;
			} else if (is_two_sides > 1) {
				boolean result = c.matches("([0-9]+)[,]([0-9]+)");
				if (result == false) {
					App.Current.showError(this.getContext(), "����˫�������������,�ֿ�д");
				}
			}
			String d = this.txt_D_cell.getContentText().trim();
			if (d == null || d.length() == 0) {
				App.Current.showError(this.getContext(), "����C�㲻��Ϊ�գ�");
				return;
			} else if (is_two_sides > 1) {
				boolean result = d.matches("([0-9]+)[,]([0-9]+)");
				if (result == false) {
					App.Current.showError(this.getContext(), "����˫�������������,�ֿ�д");
				}
			}
			String e = this.txt_E_cell.getContentText().trim();
			if (e == null || e.length() == 0) {
				App.Current.showError(this.getContext(), "����D�㲻��Ϊ�գ�");
				return;
			} else if (is_two_sides > 1) {
				boolean result = e.matches("([0-9]+)[,]([0-9]+)");
				if (result == false) {
					App.Current.showError(this.getContext(), "����˫�������������,�ֿ�д");
				}
			}
			if (chk_is_scrap.isChecked()) {
				App.Current.toastInfo(pn_smt_steel_mesh_editorr.this.getContext(), "�������ϼ�¼�ɹ�");
			} else {
				if (a.matches("[0-9]+")) {
					if (Float.valueOf(d) < 34) {
						App.Current.showError(this.getContext(), "�����ѱ��ϣ����ȴ�");
						return;
					}
					if (Float.valueOf(e) < 34) {
						App.Current.showError(this.getContext(), "�����ѱ��ϣ����ȴ�");
						return;
					}
					if (Float.valueOf(c) < 34) {
						App.Current.showError(this.getContext(), "�����ѱ��ϣ����ȴ�");
						return;
					}
					if (Float.valueOf(a) < 34) {
						App.Current.showError(this.getContext(), "�����ѱ��ϣ����ȴ�");
						return;
					}
					if (Float.valueOf(b) < 34) {
						App.Current.showError(this.getContext(), "�����ѱ��ϣ����ȴ�");
						return;
					}
				}

			}
			chk_is_scrap.isChecked();

			String sql = " exec mm_smt_steel_mesh_update ?,?,?,?,?,?,?,?,?,?,?";
			System.out.print(steel_mesh_code1);
			System.out.print(work_order_code);
			final String[] work_order_code1 = work_order_code.split("-");
			final String work_order_code_final = work_order_code1[0];
			System.out.print(item_code);
			Log.e("LZH999", steel_mesh_code1);
			Log.e("LZH999", work_order_code_final);
			Log.e("LZH999", item_code);
			Log.e("LZH999", usernumber);
			Log.e("LZH999", quantity);
			Log.e("LZH999", String.valueOf(chk_is_scrap.isChecked()));
			Log.e("LZH999", a);
			Log.e("LZH999", b);
			Log.e("LZH999", c);
			Log.e("LZH999", d);
			Log.e("LZH999", e);
			System.out.print(quantity);
			System.out.print(sql);
			Parameters p = new Parameters();
			Log.e("LZH", "495");
			Log.e("LZH", work_order_code_final);
			p.add(1, steel_mesh_code1).add(2, work_order_code_final).add(3, item_code).add(4, usernumber).add(5, quantity).add(6, chk_is_scrap.isChecked())
					.add(7, a).add(8, b).add(9, c).add(10, d).add(11, e);
			App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
				@Override
				public void handleMessage(Message msg) {
					pn_smt_steel_mesh_editorr.this.ProgressDialog.dismiss();

					//Result<DataTable> result = this.Value;
					//if (result.HasError) {
					//	App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(),result.Error);
					//	return;
					//}


					App.Current.toastInfo(pn_smt_steel_mesh_editorr.this.getContext(), "����ʹ�õǼǼ�¼�ɹ�");
					clear();

				}
			});
		}
		else {
			App.Current.toastInfo(pn_smt_steel_mesh_editorr.this.getContext(), "�벻Ҫ�����ύ");
		}
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
		this.txt_steel_mesh_code_cell.setContentText("");
		this.txt_plan_quantity_cell.setContentText("");
		this.txt_usernumber_code_cell.setContentText("");
		this.txt_quantity_cell.setContentText("");
		this.txt_accumulative_use_count_cell.setContentText("");
		
		this.txt_A_cell.setContentText("");
		this.txt_B_cell.setContentText("");
		this.txt_C_cell.setContentText("");
		this.txt_D_cell.setContentText("");
		this.txt_E_cell.setContentText("");
		
	}
	//�ж��Ƿ�Ϊ˫�����
//	public void is_two_sides() {
//		String sql = "select count(*) count from mm_smt_steel_mesh_storage_bind where item_code = ?";
//		Parameters p  =new Parameters().add(1,this.txt_steel_mesh_code_cell.getContentText().trim());
//		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
//			public void handleMessage(Message msg) {
//				Result<DataRow> result = this.Value;
//				if (result.HasError) {
//					App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(), result.Error);
//					return;
//				}
//				DataRow row = result.Value;
//				if (row == null||row.getValue("count",0)==0) {
//					App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(),"�˸���û�󶨿�λ����");
//				}
//				Log.e("LZH579",String.valueOf(row.getValue("count",0)));
//				is_two_sides = row.getValue("count", 0);
//				Log.e("LZH581",String.valueOf(is_two_sides));
//
//
//			}
//		});
//
//}

}
