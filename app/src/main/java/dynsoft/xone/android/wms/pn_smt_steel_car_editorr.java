package dynsoft.xone.android.wms;

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

public class pn_smt_steel_car_editorr extends pn_editor {

	public pn_smt_steel_car_editorr(Context context) {
		super(context);
	}
	//public ButtonTextCell txt_depart;//����Ĳ���
	public TextCell txt_item_code_cell; //��ת������
	public TextCell txt_store_code;//��λ����
	public ButtonTextCell txt_sate; // ��ת��״̬
	public TextCell txt_usernumber_code_cell; //�û�����
	public TextCell txt_date_time;
	String currentTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString();
	public boolean flag11;

	//���ö��ڵ�XML�ļ�
	@Override
	public void setContentView() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.pn_smt_steel_car_mgr, this, true);
		view.setLayoutParams(lp);
	}
	@Override
	public void onPrepared() {

		super.onPrepared();
//		this.txt_depart = (ButtonTextCell) this
//				.findViewById(R.id.txt_depart_cell);
		this.txt_date_time = (TextCell) this
				.findViewById(R.id.txt_date_cell);
		this.txt_item_code_cell = (TextCell) this
				.findViewById(R.id.txt_item_code_cell);
		
		this.txt_store_code = (TextCell) this
				.findViewById(R.id.txt_store_code_cell);
	
		this.txt_sate = (ButtonTextCell) this
				.findViewById(R.id.txt_sate_cell);
		
		this.txt_usernumber_code_cell = (TextCell) this
				.findViewById(R.id.txt_usernumber_code_cell);

		if (this.txt_sate != null) {
			this.txt_sate.setLabelText("��ת��״̬");
			this.txt_sate.Button.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					//chooseType();
					toastChooseDialog1();
				}
			});
		}
//		if (this.txt_depart != null) {
//			this.txt_depart.setLabelText("������");
//			this.txt_depart.Button.setOnClickListener(new OnClickListener() {
//				public void onClick(View view) {
//					//chooseType();
//					toastChooseDialog();
//				}
//			});
//		}

		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("��ת������");
			this.txt_item_code_cell.setReadOnly();
		}
		
		if (this.txt_store_code != null) {
			this.txt_store_code.setLabelText("��ת����λ");
			this.txt_store_code.setReadOnly();
			this.txt_store_code.setContentText("SMT");
		}
		
		if (this.txt_usernumber_code_cell != null) {
			this.txt_usernumber_code_cell.setLabelText("������");

		}
		if (this.txt_date_time != null) {
			this.txt_date_time.setLabelText("����ʱ��");
			txt_date_time.setContentText(currentTime);

		}

    }
	//ɨ����
	@Override
	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();
	      
        if (bar_code.startsWith("TL:")||bar_code.startsWith("SMT:"))
        {
             //ɨ��feeder
			get_depart(bar_code);
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

    /////
	//�ύ����
	////

	private void toastChooseDialog1() {

		final ArrayList<String> names = new ArrayList<String>();
		names.add("OK");
		names.add("���ӻ�");
		names.add("�ֱ���");
		names.add("���ӻ�");
		names.add("������");
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which >= 0) {
					names.get(which);
					txt_sate.setContentText(names.get(which));
				}
				dialog.dismiss();
			}
		};
		new AlertDialog.Builder(pn_smt_steel_car_editorr.this.getContext()).setTitle("��ѡ��")
				.setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(txt_sate.getContentText().toString()), listener)
				.setNegativeButton("ȡ��", null).show();
	}
//	private void toastChooseDialog() {
//
//		final ArrayList<String> names = new ArrayList<String>();
//		names.add("����");
//		names.add("����1��");
//		names.add("����2��");
//		names.add("����3��");
//		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (which >= 0) {
//					names.get(which);
//					txt_depart.setContentText(names.get(which));
//				}
//				dialog.dismiss();
//			}
//		};
//		new AlertDialog.Builder(pn_smt_steel_car_editorr.this.getContext()).setTitle("��ѡ��")
//				.setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(txt_depart.getContentText().toString()), listener)
//				.setNegativeButton("ȡ��", null).show();
//	}
	@Override
	public void commit() {
		//��feeder��ǳ�����״̬�����ڴ�ά�޼�¼�����Ӽ�¼

		String store_code = this.txt_store_code.getContentText().trim();
		if (store_code == null || store_code.length() == 0) {
			App.Current.showError(this.getContext(), "��ת����λ����Ϊ��");
			return;
		}
		
		String item_code = this.txt_item_code_cell.getContentText().trim();
		if (item_code == null || item_code.length() == 0) {
			App.Current.showError(this.getContext(), "��ת�����벻��Ϊ�գ�");
			return;
		}
		
//		String depart = this.txt_depart.getContentText().trim();
//		if (depart == null || depart.length() == 0) {
//			App.Current.showError(this.getContext(), "�����Ų���Ϊ�գ�");
//			return;
//		}
		String state = this.txt_sate.getContentText().trim();
		if (state == null || state.length() == 0) {
			App.Current.showError(this.getContext(), "��ת��״̬����Ϊ�գ�");
			return;
		}
		String sql ="exec mm_smt_steel_car_count ?,?,?,?,?,?";
		Parameters p = new Parameters();
//		Log.e("LZH","495");
//		Log.e("LZH",work_order_code_final);
		p.add(1,txt_item_code_cell.getContentText()).add(2, txt_store_code.getContentText()).add(3, txt_sate.getContentText()).add(4, "111").add(5,txt_date_time.getContentText()).add(6, txt_usernumber_code_cell.getContentText());
		App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
			@Override
			public void handleMessage(Message msg) {
				pn_smt_steel_car_editorr.this.ProgressDialog.dismiss();
				App.Current.toastInfo(pn_smt_steel_car_editorr.this.getContext(), "��ת����¼�ɹ�");
				clear();
				
			}
		});
	}

	//�������
	public void clear() {
		
		this.txt_sate.setContentText("");
		//this.txt_depart.setContentText("");
		this.txt_item_code_cell.setContentText("");
		this.txt_usernumber_code_cell.setContentText("");
	}
	public void  get_depart(String a){
		final String bar_code = a;
		String sql = "SELECT * from mm_smt_steel_car_use_count1 where item_code = ?";
		Parameters p = new Parameters();
		p.add(1,bar_code);
		App.Current.DbPortal.ExecuteRecordAsync("core_and", sql,p,new ResultHandler<DataRow>() {

			public void handleMessage(Message msg) {
				Result<DataRow> value = Value;
				if(value.HasError){
					App.Current.showError(pn_smt_steel_car_editorr.this.getContext(), String.valueOf(value.Error));
				}else if(value.Value == null) {
					//App.Current.showError(pn_smt_steel_car_editorr.this.getContext(), "��������");
					txt_item_code_cell.setContentText(bar_code);
				}else {



							App.Current.showError(pn_smt_steel_car_editorr.this.getContext(), "����ת���ѵǼǣ��벻Ҫ�ظ��Ǽǣ�");
							clear();
						}
			}

			});
	}

}
