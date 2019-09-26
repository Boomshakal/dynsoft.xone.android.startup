package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.RatingBarCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

public class pn_mm_handover_exception_editor extends pn_editor {

	public pn_mm_handover_exception_editor(Context context) {
		super(context);
	}

	public TextCell txt_wo_issue_order_cell;
	public TextCell txt_user_name;
	public TextCell txt_excpt_user_name;
	public TextCell txt_item_name_cell;
	public TextCell txt_item_code_cell;
	public TextCell txt_qty1;
	public DecimalCell txt_qty2;
	public ButtonTextCell txt_exception_type;
	public TextCell txt_remart;

	@Override
	public void setContentView()
	{
		LayoutParams lp = new LayoutParams(-1,-1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_mm_handover_exception_editor, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
	public void onScan(final String barcode)
	{
		String str="";
		if (barcode.startsWith("M")) {
			if (barcode.startsWith("M:"))
			{
			 str = barcode.substring(2, barcode.length());	
			}else
			{
			  str=barcode;
			}
		 this.txt_excpt_user_name.setContentText(str);
		}
	}
	
	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		_order_id = this.Parameters.get("order_id", 0L);
		_line_id  = this.Parameters.get("line_id", 0);
		_items    =this.Parameters.get("items","");
		//_id=this.Parameters.get("id", 0L);
		_order_code=this.Parameters.get("code","");
		_user_id=this.Parameters.get("user_id",0L);

		this.txt_wo_issue_order_cell = (TextCell)this.findViewById(R.id.txt_wo_issue_order_cell);
		this.txt_user_name = (TextCell)this.findViewById(R.id.txt_user_name);
		this.txt_excpt_user_name = (TextCell)this.findViewById(R.id.txt_except_user_name_cell);
		this.txt_item_name_cell = (TextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_item_code_cell =(TextCell)this.findViewById(R.id.txt_item_code_cell);
		this.txt_qty1 = (TextCell)this.findViewById(R.id.txt_qty1);
		this.txt_qty2 = (DecimalCell)this.findViewById(R.id.txt_qty2);
		this.txt_exception_type=(ButtonTextCell)this.findViewById(R.id.txt_exception_type);
		this.txt_remart = (TextCell)this.findViewById(R.id.txt_remart);
	    if (this.txt_item_code_cell!=null){
	    	this.txt_item_code_cell.setLabelText("物料编码");
	    	this.txt_item_code_cell.setReadOnly();
	    }
		if (this.txt_wo_issue_order_cell != null) {
			this.txt_wo_issue_order_cell.setLabelText("单号");
			this.txt_wo_issue_order_cell.setReadOnly();
		}
		
		if (this.txt_user_name != null) {
			this.txt_user_name.setLabelText("仓管员");
			this.txt_user_name.setReadOnly();
			
		}
		if (this.txt_excpt_user_name != null) {
			this.txt_excpt_user_name.setLabelText("用户名");
			//this.txt_user_name.setReadOnly();
			
		}
		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("物料名称");
			this.txt_item_name_cell.setReadOnly();
		}
		
	    if(this.txt_qty1!=null){
	    	this.txt_qty1.setLabelText("实发数量");
	    	this.txt_qty1.setReadOnly();
	    }
	    if(this.txt_qty2!=null){
	    	this.txt_qty2.setLabelText("实点数量");
	    }
	    if(this.txt_exception_type!=null){
	    	this.txt_exception_type.setLabelText("退单原因");
	    	this.txt_exception_type.Button
			.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					pn_mm_handover_exception_editor.this.loadexceptiontype();

				}
			});
	    }
		if (this.txt_remart != null) {
			this.txt_remart.setLabelText("详细说明");
		}
		 if (this.BackButton != null){
				this.BackButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_exit_white"));
				this.BackButton.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						pn_mm_handover_exception_editor.this.backenvent();
					}
				});
			}
		loadRecord(_order_id,_line_id);
	}
	
	private Long _order_id;
	private int _line_id;
	private String _items;
	private String _order_code;
	//private long _id;
	private DataRow _row;
	private long _user_id;
	public void  backenvent()
	{
		Link link = new Link("pane://x:code=mm_and_handover_enter_list_mgr");
		link.Parameters.add("order_id", _order_id);
        link.Parameters.add("user_operation",_user_id);
        link.Parameters.add("user_name", _row.getValue("user_name", ""));
        link.Parameters.add("code", _order_code);
        //link.Parameters.add("id", _id);
        link.Parameters.add("items", _items);
        link.Open(this, this.getContext(), null);
        pn_mm_handover_exception_editor.this.close();
	}
	public void loadexceptiontype()
	{
		String sql = "SELECT lookup_code FROM core_data_keyword where lookup_type ='事务退单类型'";
		final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql);
		if (result.HasError) {
			App.Current.showError(this.getContext(), result.Error);
			return;
		}
		if (result.Value != null) {
	
				ArrayList<String> names = new ArrayList<String>();
				for (DataRow row : result.Value.Rows) {
					String name = row.getValue("lookup_code", "");
					names.add(name);
				}

				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which >= 0) {
							DataRow row = result.Value.Rows.get(which);
							pn_mm_handover_exception_editor.this.txt_exception_type.setContentText(row.getValue(
									"lookup_code", ""));
						}
						dialog.dismiss();
					}
				};
				new AlertDialog.Builder(pn_mm_handover_exception_editor.this.getContext()).setTitle("选择类型")
				.setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(pn_mm_handover_exception_editor.this.txt_exception_type.getContentText().toString()), listener)
				.setNegativeButton("取消", null).show();
		}	
	}
	
	public void loadRecord(long head_id,int line_id)
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_mm_handover_exception_get_item ?,?";
		Parameters p  =new Parameters().add(1, head_id).add(2, line_id);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_mm_handover_exception_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_mm_handover_exception_editor.this.getContext(), result.Error);
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(pn_mm_handover_exception_editor.this.getContext(), "该事务已经发起异常处理！。");
					return;
				}
				
				_row = row;
				
				pn_mm_handover_exception_editor.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
				pn_mm_handover_exception_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
				pn_mm_handover_exception_editor.this.txt_wo_issue_order_cell.setContentText(row.getValue("code", ""));
				pn_mm_handover_exception_editor.this.txt_user_name.setContentText(row.getValue("user_name", ""));
				pn_mm_handover_exception_editor.this.txt_qty1.setContentText(App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##"));
				pn_mm_handover_exception_editor.this.txt_qty2.setContentText(App.formatNumber(row.getValue("receive_quantity", BigDecimal.ZERO), "0.##"));
			}
		});
	}
	
	@Override
	public void commit()
	{
	    String  txt_remark_val =this.txt_remart.getContentText();
	    String  txt_exception_type_val =this.txt_exception_type.getContentText();
	    String  txt_qty1_val =this.txt_qty2.getContentText();
	    String  user_name =this.txt_excpt_user_name.getContentText().trim();
	    
	    if (txt_remark_val==null || txt_remark_val.length()==0)
	     {
	    	App.Current.showError(pn_mm_handover_exception_editor.this.getContext(), "请输入退单说明");
            return;
         }
	    if (txt_exception_type_val==null || txt_exception_type_val.length()==0)
	    {
	    	App.Current.showError(pn_mm_handover_exception_editor.this.getContext(), "请输入退单类型");
            return;
        }
	    if (txt_qty1_val==null || txt_qty1_val.length()==0)
	    {
	    	App.Current.showError(pn_mm_handover_exception_editor.this.getContext(), "请输入实点数量");
            return;
        }
	    
	    if (user_name==null || user_name.length()==0)
        {
         App.Current.showError(this.getContext(), "请输入或者扫描用户名！");
         return;
        }
	   
	    String sql ="insert into mm_exception_handling_list(head_id,line_id,bill_type,responsible,create_user,create_time,open_qty,exception_type,exception_comment,exception_user_name) values (?,?,?,?,?,getdate(),?,?,?,?) ";
		Parameters p = new Parameters().add(1, _order_id).add(2, _line_id).add(3, _row.getValue("bill_type","")).add(4, _row.getValue("store_keeper",0)).add(5, App.Current.UserID).add(6, txt_qty1_val).add(7, txt_exception_type_val).add(8,txt_remark_val).add(9, user_name);
		Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(pn_mm_handover_exception_editor.this.Connector, sql, p);
		if (r.HasError) {
		 App.Current.showError(pn_mm_handover_exception_editor.this.getContext(), r.Error);
		}
		if (r.Value > 0) {
			App.Current.toastInfo(pn_mm_handover_exception_editor.this.getContext(), "提交成功!");
			//pn_mm_handover_enter_list_editor.this.refresh();
			clear();
			//Link link = new Link("pane://x:code=mm_and_handover_enter_list_mgr");
			//link.Open(this, this.getContext(), null);
		}
	 
	}
	
	public void clear()
	{
		pn_mm_handover_exception_editor.this.close();
		Link link = new Link("pane://x:code=mm_and_handover_enter_list_mgr");
		link.Parameters.add("order_id", _order_id);
        link.Parameters.add("user_operation", _user_id);
        link.Parameters.add("user_name", _row.getValue("user_name",""));
        link.Parameters.add("code", _order_code);
        //link.Parameters.add("id", _id);
        link.Parameters.add("items",_items);
        link.Open(this, this.getContext(), null);
    	pn_mm_handover_exception_editor.this.close();
	}
	

}
