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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

public class mm_item_identifying_editor extends pn_editor {

	public mm_item_identifying_editor(Context context) {
		super(context);
	}


	public TextCell txt_item_code_cell; // ��Ʒ����
	public TextCell txt_item_name_cell; //����
	public TextCell txt_vendor_model_cell; //����
	public TextCell txt_org_code_cell;//��֯
	public TextCell txt_date_code_cell; //����		
	public TextCell txt_snno_cell;//��Ʒ���к�
	

	
	public SwitchCell chk_commit_print;//��ӡ���κ�
	public TextCell txt_warehouse_cell;//�ɿ��λ


	private int scan_count ;
	private boolean checkbool ;
	
	private DataRow _order_row;
	private DataRow _lot_row;
	private Integer _total = 0;
	private Long _rownum;
	private String Item_code;
	

	//���ö��ڵ�XML�ļ�
	@Override
	public void setContentView() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.mm_item_identifying_editor, this, true);
		view.setLayoutParams(lp);
	}

	//������ʾ�ؼ�
	@Override
	public void onPrepared() {

		super.onPrepared();
		
		scan_count=0;
		checkbool=true;
		this.txt_item_code_cell = (TextCell) this
				.findViewById(R.id.txt_item_code_cell);
		
		this.txt_item_name_cell = (TextCell) this
				.findViewById(R.id.txt_item_name_cell);
		
		this.txt_vendor_model_cell = (TextCell) this
				.findViewById(R.id.txt_vendor_model_cell);
	
		this.txt_date_code_cell = (TextCell) this
				.findViewById(R.id.txt_date_code_cell);
		
		this.txt_org_code_cell = (TextCell) this
				.findViewById(R.id.txt_org_code_cell);
		
		this.txt_warehouse_cell = (TextCell) this
				.findViewById(R.id.txt_warehouse_cell);
		
		this.txt_snno_cell = (TextCell) this
				.findViewById(R.id.txt_snno_cell);

		this.chk_commit_print=(SwitchCell)this.findViewById(R.id.chk_commit_print);
		this.CommitButton = (ImageButton)this.findViewById(R.id.btn_commit);
		if (this.CommitButton != null){
			this.CommitButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_white"));
			this.CommitButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					mm_item_identifying_editor.this.printLabel();
				}
			});
		}

		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("��Ʒ����");
			this.txt_item_code_cell.setReadOnly();
		}
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("��Ʒ����");
			this.txt_item_name_cell.setReadOnly();
		}
		
		if (this.txt_vendor_model_cell != null) {
			this.txt_vendor_model_cell.setLabelText("��Ʒ�ͺ�");
			this.txt_vendor_model_cell.setReadOnly();
		}


		if (this.txt_date_code_cell != null) {
			this.txt_date_code_cell.setLabelText("��������");
			this.txt_date_code_cell.setReadOnly();
		}
		
		if (this.txt_org_code_cell != null) {
			this.txt_org_code_cell.setLabelText("��֯");
			this.txt_org_code_cell.setReadOnly();
		}
		if (this.txt_warehouse_cell != null) {
			this.txt_warehouse_cell.setLabelText("��λ");
			this.txt_warehouse_cell.setReadOnly();
		}	
		//if (this.txt_snno_cell != null) {
		//	this.txt_snno_cell.setLabelText("˿ӡ");
		//	this.txt_snno_cell.setReadOnly();
		//}

	
	}
	
	//ɨ����
	@Override
	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();
	      ///ɨ�����۳��ⵥ 
        if (bar_code.startsWith("R:"))
        {
       	 //ɨ�����۳��ⵥ�š��Զ��������۵��š��ͻ����ơ���Ʒ���롢��Ʒ���ơ�������������֯
        	
        	//App.Current.showError(this.getContext(), bar_code.toString());
        	 this.txt_item_code_cell.setContentText(bar_code.toString().split(":")[1]);
        	 Item_code=bar_code.toString().split(":")[1];
        	 this.loadItem();
        }
        else if (bar_code.startsWith("CRQ:"))
        {
          	 //ɨ�����۳��ⵥ�š��Զ��������۵��š��ͻ����ơ���Ʒ���롢��Ʒ���ơ�������������֯
           	
        	String tempbar_code =bar_code.substring(4, bar_code.length());
           	 this.txt_item_code_cell.setContentText(tempbar_code.split("-")[1]);
           	 Item_code=tempbar_code.split("-")[1];
           	 this.loadItem();
           }
      else
	  {
		  App.Current.showError(this.getContext(), "�Ƿ����룬��ɨ����ȷ�Ķ�ά�룡"+bar_code.toString());
	  }
	}
	

	public void loadItem()
	{
		this.ProgressDialog.show();
		
		String sql ="exec  p_mm_item_identifying_get_item ?";
		Parameters p  =new Parameters().add(1, Item_code);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				mm_item_identifying_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(mm_item_identifying_editor.this.getContext(), result.Error);
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(mm_item_identifying_editor.this.getContext(), "�Ҳ���������Ϣ");
					
					mm_item_identifying_editor.this.Header.setTitleText("�Ҳ���������Ϣ");
					return;
				}
			
				mm_item_identifying_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
				mm_item_identifying_editor.this.txt_vendor_model_cell.setContentText(row.getValue("vendor_model", ""));
				mm_item_identifying_editor.this.txt_warehouse_cell.setContentText(row.getValue("warehouse_code", ""));
				mm_item_identifying_editor.this.txt_org_code_cell.setContentText(row.getValue("org_code", ""));
			

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
		parameters.put("item_code", this.Item_code);
		App.Current.Print("mm_item_identifying_label", "��ӡ���ϱ�ʶ��", parameters);
	}
	
	//�������
	public void clear() {
		
		this.txt_item_name_cell.setContentText("");
		this.txt_item_code_cell.setContentText("");
		this.txt_warehouse_cell.setContentText("");
		this.txt_org_code_cell.setContentText("");	
		this.txt_snno_cell.setContentText("");
		
	}
}
