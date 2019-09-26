package dynsoft.xone.android.wms;

import java.io.Serializable;
import android.content.DialogInterface;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import dynsoft.xone.android.activity.LightKanbanActivity;
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

public class pn_smt_steel_mesh_check_editorr extends pn_editor {

	public pn_smt_steel_mesh_check_editorr(Context context) {
		super(context);
	}


	//public ButtonTextCell txt_work_order_code_cell; // ��������
	private ArrayList<String> selectedException;
	public TextCell txt_create_date_cell; //��������
	public ButtonTextCell txt_vendor_name_cell; //��Ӧ��
	public TextCell txt_mesh_code_cell;//��������
	public TextCell txt_A_cell; //���ĵ�
	public TextCell txt_B_cell; //1��
	public TextCell txt_C_cell; //2��
	public TextCell txt_D_cell; //3��
	public TextCell txt_E_cell; //4��
	public TextCell txt_content_cell;//��ۼ����
	public TextCell txt_check_name_cell;//ȷ����
	public ButtonTextCell txt_result_cell;//���
	String currentTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString();
	public int flage = 1;


	//���ö��ڵ�XML�ļ�
	@Override
	public void setContentView() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.pn_smt_steel_mesh_check_editor, this, true);
		view.setLayoutParams(lp);
		selectedException = new ArrayList<String>();
	}
	
	//  ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>(); 
	//  final String [] nItems = {"���","��ȡ����ƫ��","���ϲ���","�����","����","��Ӧ�Ʋ���","����"};

	//������ʾ�ؼ�
	@Override
	public void onPrepared() {

		super.onPrepared();

		 


		this.txt_create_date_cell = (TextCell) this
				.findViewById(R.id.txt_create_date_cell);
		
		this.txt_vendor_name_cell = (ButtonTextCell) this
				.findViewById(R.id.txt_vendor_name_cell);
		
		this.txt_mesh_code_cell = (TextCell) this
				.findViewById(R.id.txt_mesh_code_cell);
	
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
		this.txt_content_cell = (TextCell) this
				.findViewById(R.id.txt_content_cell);
		this.txt_check_name_cell = (TextCell) this
				.findViewById(R.id.txt_check_name_cell);
		this.txt_result_cell = (ButtonTextCell) this
				.findViewById(R.id.txt_result_cell);


		if (this.txt_create_date_cell != null) {
			this.txt_create_date_cell.setLabelText("�������");
			this.txt_create_date_cell.setContentText(currentTime);
		}
		
		if (this.txt_vendor_name_cell != null) {
			this.txt_vendor_name_cell.setLabelText("��Ӧ��");
			this.txt_vendor_name_cell.Button.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					//chooseType();
					toastChooseDialog1();
				}
			});

		}

		if (this.txt_mesh_code_cell != null) {
			this.txt_mesh_code_cell.setLabelText("��������");
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
		if (this.txt_content_cell != null) {
			this.txt_content_cell.setLabelText("��۽��");

		}
		
		if (this.txt_check_name_cell != null) {
			this.txt_check_name_cell.setLabelText("�����");
			this.txt_check_name_cell.setContentText("MZ5493");

		}
		if (this.txt_result_cell != null) {
			this.txt_result_cell.setLabelText("���");
			this.txt_result_cell.Button.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					//chooseType();
					toastChooseDialog();
				}
			});
			//this.txt_quantity_cell.setReadOnly();
		}
		

	}


	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();
		if (bar_code.startsWith("R")) {
			txt_mesh_code_cell.setContentText(bar_code);
		}
		else if(bar_code.startsWith("M")||bar_code.startsWith("MZ")){
			txt_check_name_cell.setContentText(bar_code);
		}
		else {
			App.Current.showError(this.getContext(), "�Ƿ����룬��ɨ����ȷ�Ķ�ά�룡"+bar_code.toString());
		}
	}


	private void toastChooseDialog() {

		final ArrayList<String> names = new ArrayList<String>();
		names.add("�ϸ�");
		names.add("���ϸ�");


		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which >= 0) {
					names.get(which);
					txt_result_cell.setContentText(names.get(which));
				}
				dialog.dismiss();
			}
		};
		new AlertDialog.Builder(pn_smt_steel_mesh_check_editorr.this.getContext()).setTitle("��ѡ��")
				.setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(txt_result_cell.getContentText().toString()), listener)
				.setNegativeButton("ȡ��", null).show();
	}


	private void toastChooseDialog1() {

		final ArrayList<String> names = new ArrayList<String>();
		names.add("��̩��");
		names.add("����");


		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which >= 0) {
					names.get(which);
					txt_vendor_name_cell.setContentText(names.get(which));
				}
				dialog.dismiss();
			}
		};
		new AlertDialog.Builder(pn_smt_steel_mesh_check_editorr.this.getContext()).setTitle("��ѡ��")
				.setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(txt_result_cell.getContentText().toString()), listener)
				.setNegativeButton("ȡ��", null).show();
	}
//	private void multiChoiceDialog(final StringBuffer nameMessage, final boolean[] selected, final ArrayList<String> names, final ButtonTextCell buttonTextCell) {
//		new AlertDialog.Builder(pn_smt_steel_mesh_check_editorr.this.getContext()).setTitle("��ѡ��")
//				.setNegativeButton("ȡ��", null).setPositiveButton("ȷ��", null)
//				.setSingleChoiceItems(names.toArray(new String[0]),
//						names.indexOf(pn_smt_steel_mesh_check_editorr.this.txt_result_cell.getContentText().toString()), listener)
////				.setMultiChoiceItems(names.toArray(new String[0]), selected, new DialogInterface.OnMultiChoiceClickListener() {
////
////					@Override
////					public void onClick(DialogInterface dialogInterface, int i, boolean b) {
////						selected[i] = b;
////						Log.e("lzh0",String.valueOf(i));
////						if(selected[i] == true&&txt_result_cell.getContentText().trim()==""){
////							Log.e("LZH11","222222");
////							txt_result_cell.setContentText(names.get(i));
////						}
////						else if (selected[i] == true&&names.get(i)!=txt_result_cell.getContentText()) {
////							nameMessage.append(names.get(i));
////							//nameMessage.append(",");
////							if(i==0){
////								selected[1]==false;
////							}
////							else {
////								selected[0]==false;
////							}
////							Log.e("lzh11",txt_result_cell.getContentText());
////							Log.e("lzh12",nameMessage.toString());
////
////							txt_result_cell.setContentText(names.get(i));
////
////
////						}
////					}
//				}).show();
//	}









//	private void multiChoiceDialog(final StringBuffer nameMessage, final boolean[] selected, final ArrayList<String> names, final ButtonTextCell buttonTextCell) {
//		new AlertDialog.Builder(pn_smt_steel_mesh_check_editorr.this.getContext()).setTitle("��ѡ��")
//				.setNegativeButton("ȡ��", null).setPositiveButton("ȷ��",new DialogInterface.OnClickListener(){
//					public void onClick(DialogInterface dialogInterface, int i) {
//
//					}
//		})
//				.setMultiChoiceItems(names.toArray(new String[0]), selected, new DialogInterface.OnMultiChoiceClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialogInterface, int i, boolean b) {
//
//						selected[i] = b;
//						if (selected[i] == true) {
//							nameMessage.append(names.get(i));
//							//nameMessage.append(",");
//
//							if (!selectedException.contains(names.get(i))) {
//								selectedException.add(names.get(i));
//
//							}
//							Log.e("lzh11",txt_result_cell.getContentText());
//							Log.e("lzh12",nameMessage.toString());
//							if(txt_result_cell.getContentText()!=nameMessage.toString()) {
//								txt_result_cell.setContentText(names.get(i));
//								return;
//							}
//						}
//					}
//				}).show();
//	}














    /////
	//�ύ����
	////
	@Override
	public void commit() {
		//��feeder��ǳ�����״̬�����ڴ�ά�޼�¼�����Ӽ�¼
	
		

		String vendor_name = this.txt_vendor_name_cell.getContentText().trim();
		if (vendor_name == null || vendor_name.length() == 0) {
			App.Current.showError(this.getContext(), "��Ӧ�̲���Ϊ�գ�");
			return;
		}
		
		String mesh_code = this.txt_mesh_code_cell.getContentText().trim();
		if (mesh_code == null || mesh_code.length() == 0) {
			App.Current.showError(this.getContext(), "�������벻��Ϊ�գ�");
			return;
		}
		String a = this.txt_A_cell.getContentText().trim();
		if (a == null || a.length() == 0) {
			App.Current.showError(this.getContext(), "�������ĵ㲻��Ϊ�գ�");
			return;
		}
		String b = this.txt_B_cell.getContentText().trim();
		if (b == null || b.length() == 0) {
			App.Current.showError(this.getContext(), "����A�㲻��Ϊ�գ�");
			return;
		}
		String c = this.txt_C_cell.getContentText().trim();
		if (c == null || c.length() == 0) {
			App.Current.showError(this.getContext(), "����B�㲻��Ϊ�գ�");
			return;
		}
		String d = this.txt_D_cell.getContentText().trim();
		if (d == null || d.length() == 0) {
			App.Current.showError(this.getContext(), "����C�㲻��Ϊ�գ�");
			return;
		}
		String e = this.txt_E_cell.getContentText().trim();
		if (e == null || e.length() == 0) {
			App.Current.showError(this.getContext(), "����D�㲻��Ϊ�գ�");
			return;
		}
		String content = this.txt_content_cell.getContentText().trim();
		if (content == null || content.length() == 0) {
			App.Current.showError(this.getContext(), "��۽������Ϊ�գ�");
			return;
		}
		String txt_check_name_cell1 = this.txt_check_name_cell.getContentText().trim();
		if (txt_check_name_cell1 == null || txt_check_name_cell1.length() == 0) {
			App.Current.showError(this.getContext(), "����Ա��Ϣ����Ϊ�գ�");
			return;
		}
		String result = this.txt_result_cell.getContentText().trim();
		if (result == null || result.length() == 0) {
			App.Current.showError(this.getContext(), "�������Ϊ�գ�");
			return;
		}
		
		String sql ="INSERT INTO [dbo].[mm_smt_steel_check](create_date,vendor_name,mesh_code,strain1,strain2,strain3,strain4,strain5,content,check_name,result\n" +
				")    VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		//System.out.print(steel_mesh_code);
		//System.out.print(work_order_code);
//		//System.out.print(item_code);
//		//System.out.print(quantity);
//		//System.out.print(sql);
		Parameters p = new Parameters();
//		Log.e("LZH","495");
//		Log.e("LZH",work_order_code_final);
		p.add(1, txt_create_date_cell.getContentText()).add(2, txt_vendor_name_cell.getContentText()).add(3, txt_mesh_code_cell.getContentText()).add(4,txt_A_cell.getContentText()).add(5,txt_B_cell.getContentText()).add(6, txt_C_cell.getContentText())
		.add(7, txt_D_cell.getContentText()).add(8, txt_E_cell.getContentText()).add(9, txt_content_cell.getContentText()).add(10, txt_check_name_cell.getContentText()).add(11, txt_result_cell.getContentText());
		App.Current.DbPortal.ExecuteNonQueryAsync(this.Connector, sql, p, new ResultHandler<Integer>() {
			@Override
			public void handleMessage(Message msg) {
				Result<Integer> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_smt_steel_mesh_check_editorr.this.getContext(), result.Error);

				}
				else {
					App.Current.toastInfo(pn_smt_steel_mesh_check_editorr.this.getContext(), "����ɹ�");
					clear();
				}
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
		
		//this.txt_create_date_cell.setContentText("");
		this.txt_result_cell.setContentText("");
		this.txt_check_name_cell.setContentText("");
		this.txt_content_cell.setContentText("");
		this.txt_mesh_code_cell.setContentText("");
		this.txt_vendor_name_cell.setContentText("");
		this.txt_A_cell.setContentText("");
		this.txt_B_cell.setContentText("");
		this.txt_C_cell.setContentText("");
		this.txt_D_cell.setContentText("");
		this.txt_E_cell.setContentText("");
		
	}

//	private void chooseType() {
//
//
//					final StringBuffer nameMessage = new StringBuffer();
//					final boolean[] selected = new boolean[names.size()];
//					toastChooseDialog(nameMessage, selected, names, txt_result_cell);
//	}







}
