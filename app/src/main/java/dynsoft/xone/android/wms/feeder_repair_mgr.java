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
import android.widget.Toast;

public class feeder_repair_mgr extends pn_editor {

	public feeder_repair_mgr(Context context) {
		super(context);
	}


	public TextCell txt_feeder_code_cell; // 供料器编码
	public TextCell txt_size_cell; //尺寸
	public TextCell txt_valid_date_cell; //有效期至
	public ButtonTextCell txt_repair_type_cell;//送修类型
	public TextCell txt_repair_user_cell; //送修人	



	private int scan_count ;
	private boolean checkbool ;	
	private DataRow _order_row;
	private DataRow _lot_row;
	private Integer _total = 0;
	private Long _rownum;
	private String feeder_code;
	

	//设置对于的XML文件
	@Override
	public void setContentView() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.feeder_repair_editor, this, true);
		view.setLayoutParams(lp);
	}
	
	  ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>(); 
	  final String [] nItems = {"感应灯不亮","少配件/配件损坏（变形）","翻贴","不送料","中心偏","抛料","不卷带","双闪"};

	//设置显示控件
	@Override
	public void onPrepared() {

		super.onPrepared();
		
		scan_count=0;
		checkbool=true;
		this.txt_feeder_code_cell = (TextCell) this
				.findViewById(R.id.txt_feeder_code_cell);
		
		this.txt_size_cell = (TextCell) this
				.findViewById(R.id.txt_size_cell);
		
		this.txt_valid_date_cell = (TextCell) this
				.findViewById(R.id.txt_valid_date_cell);
	
		this.txt_repair_type_cell = (ButtonTextCell) this
				.findViewById(R.id.txt_repair_type_cell);
		
		this.txt_repair_user_cell = (TextCell) this
				.findViewById(R.id.txt_repair_user_cell);
		
		

		//this.chk_commit_print=(SwitchCell)this.findViewById(R.id.chk_commit_print);
		//this.CommitButton = (ImageButton)this.findViewById(R.id.btn_commit);
		//if (this.CommitButton != null){
		//	this.CommitButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_white"));
		//	this.CommitButton.setOnClickListener(new OnClickListener(){
		//		@Override
		//		public void onClick(View v) {
		//			feeder_repair_mgr.this.printLabel();
		//		}
		//	});
		//}

		if (this.txt_feeder_code_cell != null) {
			this.txt_feeder_code_cell.setLabelText("编    码");
			this.txt_feeder_code_cell.setReadOnly();
		}
		if (this.txt_size_cell != null) {
			this.txt_size_cell.setLabelText("尺   寸");
			this.txt_size_cell.setReadOnly();
		}
		
		if (this.txt_valid_date_cell != null) {
			this.txt_valid_date_cell.setLabelText("有效期至");
			this.txt_valid_date_cell.setReadOnly();
		}
  
		if (this.txt_repair_type_cell != null) {
			this.txt_repair_type_cell.setLabelText("送修类型");
			this.txt_repair_type_cell.Button.setOnClickListener(new OnClickListener() {  
	              
		            @Override  
		            public void onClick(View v) {
		            	//load_repair_type();
		                AlertDialog.Builder builder = new AlertDialog.Builder(feeder_repair_mgr.this.getContext());
		                MultiChoiceID.clear();
		                builder.setTitle("多项选择"); 
		
		                
          
		                //  设置多选项  
		                builder.setMultiChoiceItems(nItems,   
		                        new boolean[]{false,false,false,false,false,false,false,false},  
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
		                //  设置确定按钮  
		                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
		                      
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
		                     
		                        feeder_repair_mgr.this.txt_repair_type_cell.setContentText(str) ;
		                    }  
		                });  
		                //  设置取消按钮  
		                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
		                      
		                    @Override  
		                    public void onClick(DialogInterface arg0, int arg1) {  
		                       
		                          
		                    }  
		                });  
		                  
		                builder.create().show();  
		            }             
		        });  
		    }  
		
		if (this.txt_repair_user_cell != null) {
			this.txt_repair_user_cell.setLabelText("送修人员");
			this.txt_repair_user_cell.setReadOnly();
		}

	}
	
	//扫条码
	@Override
	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();
	      
        if (bar_code.startsWith("FD:"))
        {
             //扫描feeder
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
        	//扫描工号
           	 this.txt_repair_user_cell.setContentText(bar_code);
           }
      else
	  {
		  App.Current.showError(this.getContext(), "非法条码，请扫描正确的二维码！"+bar_code.toString());
	  }
	}
	
	///根据条件带出相应数据信息
	public void loadItem(String code)
	{
		this.ProgressDialog.show();
		
		String sql ="select  code,size,convert(nvarchar(10),valid_date,120) valid_date  from fm_smt_feeder where code = ?";
		Parameters p  =new Parameters().add(1, code);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				feeder_repair_mgr.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(feeder_repair_mgr.this.getContext(), result.Error);
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(feeder_repair_mgr.this.getContext(), "找不到此供料器信息");
					
					feeder_repair_mgr.this.Header.setTitleText("找不到此供料器信息");
					return;
				}			
				feeder_repair_mgr.this.txt_feeder_code_cell.setContentText(row.getValue("code", ""));
				feeder_repair_mgr.this.txt_size_cell.setContentText(row.getValue("size", ""));
				feeder_repair_mgr.this.txt_valid_date_cell.setContentText(row.getValue("valid_date", ""));

			}
		});
	}
	
	public void load_repair_type() {

		String sql = "select meaning FROM dbo.core_data_keyword where lookup_type='SMT_FEEDER'";
		final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql);
		if (result.HasError) {
			App.Current.showError(this.getContext(), result.Error);
			return;
		}
		if (result.Value != null) {
	
				ArrayList<String> names = new ArrayList<String>();	
				for (DataRow row : result.Value.Rows) {
					String name = row.getValue("meaning", "");
					names.add(name);
				}

				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which >= 0) {
							DataRow row = result.Value.Rows.get(which);
						feeder_repair_mgr.this.txt_repair_type_cell.setContentText(row.getValue(
									"meaning", ""));
						}
						dialog.dismiss();
					}
				};
				new AlertDialog.Builder(feeder_repair_mgr.this.getContext()).setTitle("选择送修类型")
				.setSingleChoiceItems(names.toArray(new String[0]), 
						names.indexOf(feeder_repair_mgr.this.txt_repair_type_cell.getContentText().toString()), listener)
				.setNegativeButton("取消", null).show();
		}
	}
	
    /////
	//提交操作
	////
	@Override
	public void commit() {
		//将feeder标记成送修状态，并在待维修记录中增加记录
		String sql = " update fm_smt_feeder set is_repair=1,ng_type=?,repair_userinfo=?,present_datetime=getdate() where code=? select '' as  messagestr ";
		
		String feeder_code = this.txt_feeder_code_cell.getContentText().trim();
		if (feeder_code == null || feeder_code.length() == 0) {
			App.Current.showError(this.getContext(), "编码不能为空！");
			return;
		}
		
		String repair_type = this.txt_repair_type_cell.getContentText().trim();
		if (repair_type == null || repair_type.length() == 0) {
			App.Current.showError(this.getContext(), "送修类型不能为空！");
			return;
		}
		
		String repair_user = this.txt_repair_user_cell.getContentText().trim();
		if (repair_user == null || repair_user.length() == 0) {
			App.Current.showError(this.getContext(), "送修人员不能为空！");
			return;
		}
		
		Parameters p = new Parameters();
		p.add(1, repair_type).add(2, repair_user).add(3, feeder_code);
		App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
			@Override
			public void handleMessage(Message msg) {
				feeder_repair_mgr.this.ProgressDialog.dismiss();

				Result<DataTable> result = this.Value;
				if (result.HasError) {
					App.Current.showError(feeder_repair_mgr.this.getContext(),result.Error);
					return;
				}
				
				
				App.Current.toastInfo(feeder_repair_mgr.this.getContext(), "送修成功");
				//打印操作
				//DataTable dt = result.Value;
				//if (feeder_repair_mgr.this.chk_commit_print.CheckBox.isChecked()) {
				//	if (dt.Rows.size() > 0) {
						//printLabel(dt.Rows.get(0));
				//	}
				//}
				//pn_begin_stock_editor.this.txt_org_code_cell.setContentText(org_code);
				clear();
			}
		});
	}
	
	//打印操作
	public void printLabel() {
		//Map<String, String> parameters = new HashMap<String, String>();
		//parameters.put("item_code", this.Item_code);
		//App.Current.Print("mm_item_identifying_label", "打印物料标识牌", parameters);
	}
	
	//清空数据
	public void clear() {
		
		this.txt_feeder_code_cell.setContentText("");
		this.txt_size_cell.setContentText("");
		this.txt_valid_date_cell.setContentText("");
		this.txt_repair_type_cell.setContentText("");	
		this.txt_repair_user_cell.setContentText("");
		
	}
}
