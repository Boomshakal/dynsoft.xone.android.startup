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

 
	public TextCell txt_mobile_cell;//周转车
	public TextCell txt_storage_cell; //储位
	public TextCell txt_item_code_cell; //物料编码
	public TextCell txt_item_name_cell; //物料编码
	public TextCell txt_usernumber_code_cell; //物料编码
	public CheckBox isbinding; //解除绑定
	private int scan_count ;
	private boolean checkbool ;	
	private DataRow _order_row;
	private DataRow _lot_row;
	private Integer _total = 0;
	private Long _rownum;
	private String work_order_code;
	

	//设置对于的XML文件
	@Override
	public void setContentView() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.pn_smt_mobilestorage_editor, this, true);
		view.setLayoutParams(lp);
	}
	
	//  ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>(); 
	//  final String [] nItems = {"卷带","吸取中心偏移","送料不良","少配件","保养","感应灯不亮","抛料"};

	//设置显示控件
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
			this.txt_mobile_cell.setLabelText("周转车编码");
			this.txt_mobile_cell.setReadOnly();
		}
		if (this.txt_storage_cell != null) {
			this.txt_storage_cell.setLabelText("储位编码");
			this.txt_storage_cell.setReadOnly();
		}			
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("物料编码");
			this.txt_item_code_cell.setReadOnly();
		}
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("物料名称");
			this.txt_item_name_cell.setReadOnly();
		}	
		if (this.txt_usernumber_code_cell != null) {
			this.txt_usernumber_code_cell.setLabelText("操作用户");
			this.txt_usernumber_code_cell.setReadOnly();
			this.txt_usernumber_code_cell.setContentText(App.Current.UserCode);			
		}		   
	}
	
	//扫条码
	@Override
	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();
	      
        if (bar_code.startsWith("TL:"))
        {
        	
             //扫描周转车
        	 this.txt_mobile_cell.setContentText(bar_code.toString().split(":")[1]);
        	// work_order_code=bar_code.toString().split(":")[1];
        	// this. loadsteel_mesh(bar_code.toString());
        }
        else if (bar_code.startsWith("SMT-"))
        {
             //扫描储位
        	 this.txt_storage_cell.setContentText(bar_code.toString());
        	  
        	 //loadItem(bar_code.toString());
        }
        
        
        else if (bar_code.startsWith("R"))
        {
             //扫描物料
        	 this.txt_item_code_cell.setContentText(bar_code.toString());
        	  
        	 loadItem(bar_code.toString());
        }
        else if (bar_code.startsWith("MZ")||bar_code.startsWith("M")||bar_code.startsWith("MA"))
        {
        	//扫描工号
           	 this.txt_usernumber_code_cell.setContentText(bar_code.toString());
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
					//App.Current.showError(pn_smt_steel_mesh_storage_editorr.this.getContext(), "找不到对应的信息");
					
					//pn_smt_steel_mesh_storage_editorr.this.Header.setTitleText("找不到单对应的信息");
					return;
				}			
			 
				fm_smt_mobilestorage_editor.this.txt_item_name_cell.setContentText(row.getValue("name", ""));
			 

			}
		}); 
	}
	
	
	 

    /////
	//提交操作
	////
	@Override
	public void commit() {
		//将feeder标记成送修状态，并在待维修记录中增加记录
	
		
	 
		
		String mobile = this.txt_mobile_cell.getContentText().trim();
		if (mobile == null || mobile.length() == 0) {
			App.Current.showError(this.getContext(), "周装车信息不能为空！");
			return;
		}	
		String storage = this.txt_storage_cell.getContentText().trim();
		if (storage == null || storage.length() == 0) {
			App.Current.showError(this.getContext(), "储位信息不能为空！");
			return;
		}	
		String item_code = this.txt_item_code_cell.getContentText().trim();	 
		if (item_code == null || item_code.length() == 0) {
			App.Current.showError(this.getContext(), "物料信息不能为空！");
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

			
				App.Current.toastInfo(fm_smt_mobilestorage_editor.this.getContext(), "周装车储位绑定成功");				
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
		
		 	
		this.txt_mobile_cell.setContentText("");
		this.txt_storage_cell.setContentText("");
		this.txt_item_code_cell.setContentText("");
		this.txt_item_name_cell.setContentText("");
		 
	 
		
	}
}
