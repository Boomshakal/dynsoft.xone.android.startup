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
import dynsoft.xone.android.control.DateCell;
import dynsoft.xone.android.control.DateTimeCell;
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
import android.widget.Toast;

public class feeder_maintaininfo_mgr extends pn_editor {

	public feeder_maintaininfo_mgr(Context context) {
		super(context);
	}


	public TextCell txt_feeder_code_cell; // ����������
	public TextCell txt_present_date_cell;//��������
	public TextCell txt_present_type_cell;//��������
	public TextCell txt_present_user_cell;//��������	
	public DateCell txt_maintain_date_cell;//��������
	public ButtonTextCell txt_maintain_type_cell;//��������
	public TextCell txt_maintain_user_cell;//������Ա
	public TextCell txt_valid_date_cell; //��Ч��



	private int scan_count ;
	private boolean checkbool ;	
	private DataRow _order_row;
	private DataRow _lot_row;
	private Integer _total = 0;
	private Long _rownum;
	private String feeder_code;
	

	//���ö��ڵ�XML�ļ�
	@Override
	public void setContentView() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.feeder_maintaininfo_editor, this, true);
		view.setLayoutParams(lp);
	}
	
	  ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>(); 
	  final String [] nItems = {"������š����˿�������͡�У�����ģ�" };

	//������ʾ�ؼ�
	@Override
	public void onPrepared() {

		super.onPrepared();
		
		scan_count=0;
		checkbool=true;
		this.txt_feeder_code_cell = (TextCell) this
				.findViewById(R.id.txt_feeder_code_cell);
		
		
		this.txt_maintain_date_cell = (DateCell) this
				.findViewById(R.id.txt_maintain_date_cell);
		
		this.txt_maintain_type_cell = (ButtonTextCell) this
				.findViewById(R.id.txt_maintain_type_cell);
		
		
		this.txt_maintain_user_cell = (TextCell) this
				.findViewById(R.id.txt_maintain_user_cell);
		
		this.txt_valid_date_cell = (DateCell) this
				.findViewById(R.id.txt_valid_date_cell);
		
		
		
		


		if (this.txt_feeder_code_cell != null) {
			this.txt_feeder_code_cell.setLabelText("��         ��");
			this.txt_feeder_code_cell.setReadOnly();
		}
		
	
		
		if (this.txt_maintain_date_cell != null) {
			this.txt_maintain_date_cell.setLabelText("��������");
			this.txt_maintain_date_cell.setReadOnly();
		}
		 
		
		if (this.txt_maintain_user_cell != null) {
			this.txt_maintain_user_cell.setLabelText("������Ա");
			this.txt_maintain_user_cell.setReadOnly();
		}
		if (this.txt_valid_date_cell != null) {
			this.txt_valid_date_cell.setLabelText("��Ч����");
			this.txt_valid_date_cell.setReadOnly();
		}
		
		
		
		
		 
  
		if (this.txt_maintain_type_cell != null) {
			this.txt_maintain_type_cell.setLabelText("��������");
			this.txt_maintain_type_cell.setContentText("��������鰲װ�̶��˲����Ƿ���������ɶ����������������˿�Ƿ���̣��ɴﵼ�ϲۡ����ּ����ϵȻ�����Ƿ���");
			this.txt_maintain_type_cell.Button.setVisibility(GONE);
			this.txt_maintain_type_cell.Button.setOnClickListener(new OnClickListener() {  
	              
		            @Override  
		            public void onClick(View v) {
		            	//load_repair_type();
		                AlertDialog.Builder builder = new AlertDialog.Builder(feeder_maintaininfo_mgr.this.getContext());
		                MultiChoiceID.clear();
		                builder.setTitle("����ѡ��"); 
		
		                
          
		                //  ���ö�ѡ��  
		                builder.setMultiChoiceItems(nItems,   
		                        new boolean[]{false },  
		                        new DialogInterface.OnMultiChoiceClickListener() {  
		                      
		                            @Override  
		                            public void onClick(DialogInterface arg0, int arg1, boolean arg2) {  
		                                // TODO Auto-generated method stub  
		                                if (arg2) {  
		                                    MultiChoiceID.add(arg1);  		                         
		                                }  
		                                else {  
		                                    MultiChoiceID.remove(arg1);  
		                                }  
		                            }  
		                });  
		                System.out.println("xxxx4");
		                System.out.println("size:11"+String.valueOf(MultiChoiceID.size()));
		                //  ����ȷ����ť  
		                builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {  
		                      
		                    @Override  
		                    public void onClick(DialogInterface arg0, int arg1) {  
		                        // TODO Auto-generated method stub  
		                        String str = "";  
		                        int size = MultiChoiceID.size();
		                        for(int i = 0; i < size; i++) {  
		                        	if (str=="")
		                        	{
		                            str = (nItems[MultiChoiceID.get(i)]); 
		                        	}else {
		                        	str += (","+nItems[MultiChoiceID.get(i)]); 	
		                        	}
		                        }  
		                     
		                        feeder_maintaininfo_mgr.this.txt_maintain_type_cell.setContentText(str) ;
		                    }  
		                });  
		                //  ����ȡ����ť  
		                builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {  
		                      
		                    @Override  
		                    public void onClick(DialogInterface arg0, int arg1) {  
		                       
		                          
		                    }  
		                });  
		                  
		                builder.create().show();  
		            }             
		        });  
		    }  
		
	 
	}
	
	//ɨ����
	@Override
	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();
	      
        if (bar_code.startsWith("FD:"))
        {
             //ɨ��feeder
        	 this.txt_feeder_code_cell.setContentText(bar_code.toString().split(":")[1]);
        	 feeder_code=bar_code.toString().split(":")[1];
        	
        	 this.loadItem(feeder_code);
        }
        else if (bar_code.startsWith("SSY")||bar_code.startsWith("ZSY"))
        {       
          	 feeder_code=bar_code.toString();   			 
        	 this.loadItem(feeder_code);
        }
        	
        else if (bar_code.startsWith("MZ")||bar_code.startsWith("M")||bar_code.startsWith("MA"))
        {
        	//ɨ�蹤��
           	 this.txt_maintain_user_cell.setContentText(bar_code);
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
		
		String sql ="SELECT code,ng_type,repair_userinfo,present_datetime,valid_date FROM fm_smt_feeder where code = ?";
		Parameters p  =new Parameters().add(1, code);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				feeder_maintaininfo_mgr.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(feeder_maintaininfo_mgr.this.getContext(), result.Error);
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(feeder_maintaininfo_mgr.this.getContext(), "�Ҳ����˹�������Ϣ");
					
					feeder_maintaininfo_mgr.this.Header.setTitleText("�Ҳ����˹�������Ϣ");
					return;
				}			
				feeder_maintaininfo_mgr.this.txt_feeder_code_cell.setContentText(row.getValue("code", ""));
				//feeder_maintaininfo_mgr.this.txt_present_type_cell.setContentText(row.getValue("ng_type", ""));
				//feeder_maintaininfo_mgr.this.txt_present_user_cell.setContentText(row.getValue("repair_userinfo", ""));
				//feeder_maintaininfo_mgr.this.txt_present_date_cell.setContentText(row.getValue("present_datetime", new Date()).toString());
				
				Date today1=new Date();
				 Calendar c1 = Calendar.getInstance();
			        c1.setTime(today1);
			       // c.add(Calendar.DAY_OF_MONTH, 30);// ����+1��
			 
			        Date tomorrow1 = c1.getTime();
			        SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			        String date1 = s1.format(tomorrow1);
				
				//feeder_maintaininfo_mgr.this.txt_maintain_date_cell.setContentText(row.getValue("present_datetime", new Date()).toString());
			        feeder_maintaininfo_mgr.this.txt_maintain_date_cell.setContentText(date1);
			        
			        
				Date today=new Date();
				 Calendar c = Calendar.getInstance();
			        c.setTime(today);
			        c.add(Calendar.DAY_OF_MONTH, 30);// ����+1��
			 
			        Date tomorrow = c.getTime();
			        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			        String date = s.format(tomorrow);
				
				feeder_maintaininfo_mgr.this.txt_valid_date_cell.setContentText(date);

			}
		});
	}
	
	
	
    /////
	//�ύ����
	////
	@Override
	public void commit() {
		//��feeder��ǳ�����״̬�����ڴ�ά�޼�¼�����Ӽ�¼
		String sql = " exec fm_smt_feeder_maintaininfo_create ?,?,?,?,?,?,?,? ";		
		String feeder_code = this.txt_feeder_code_cell.getContentText().trim();
		if (feeder_code == null || feeder_code.length() == 0) {
			App.Current.showError(this.getContext(), "���벻��Ϊ�գ�");
			return;
		}		
		
		//String present_date = this.txt_present_date_cell.getContentText().trim(); //��������
		//String present_type = this.txt_present_type_cell.getContentText().trim(); //��������
		//String present_user = this.txt_present_user_cell.getContentText().trim(); //������Ա
		
		
		
		String maintain_type = this.txt_maintain_type_cell.getContentText().trim();
		if (maintain_type == null || maintain_type.length() == 0) {
			App.Current.showError(this.getContext(), "�������Ͳ���Ϊ�գ�");
			return;
		}		
		String maintain_user = this.txt_maintain_user_cell.getContentText().trim();
		if (maintain_user == null || maintain_user.length() == 0) {
			App.Current.showError(this.getContext(), "������Ա����Ϊ�գ�");
			return;
		}
		
		String maintain_date = this.txt_maintain_date_cell.getContentText().trim(); //ά������
		String valid_date = this.txt_valid_date_cell.getContentText().trim(); //��Щ��
		

		Parameters p = new Parameters();
		 p.add(1, feeder_code).add(2, maintain_date).add(3, valid_date).add(4, maintain_type).add(5, maintain_user);
		 p.add(6,null ).add(7, null).add(8, null);
		App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
			@Override
			public void handleMessage(Message msg) {
				feeder_maintaininfo_mgr.this.ProgressDialog.dismiss();

				Result<DataTable> result = this.Value;
				if (result.HasError) {
					App.Current.showError(feeder_maintaininfo_mgr.this.getContext(),result.Error);
					return;
				}
				
				
				App.Current.toastInfo(feeder_maintaininfo_mgr.this.getContext(), "ά�޳ɹ�");
				
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
		
		this.txt_feeder_code_cell.setContentText("");
		//this.txt_present_date_cell.setContentText("");
		//this.txt_present_type_cell.setContentText("");
		//this.txt_present_user_cell.setContentText("");
		//this.txt_maintain_type_cell.setContentText("");
		//this.txt_maintain_user_cell.setContentText("");
		this.txt_maintain_date_cell.setContentText("");
		this.txt_valid_date_cell.setContentText("");
		 
		
	}
}
