package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class pn_mm_handover_exception_replay_editor extends pn_editor {

	public pn_mm_handover_exception_replay_editor(Context context) {
		super(context);
	}

	public TextCell txt_order_code_cell;
	public TextCell txt_item_code_cell;
	public TextCell txt_item_name_cell;
	public TextCell txt_exception_remark_cell;
	public TextCell txt_relay_comment_cell;
	public TextCell txt_user_name;
	public ImageButton btn_prev;
	public ImageButton btn_next;
	@Override
	public void setContentView()
	{
		LayoutParams lp = new LayoutParams(-1,-1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_mm_handover_exception_replay_editor, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_order_code_cell = (TextCell)this.findViewById(R.id.txt_order_code_cell);
		this.txt_item_code_cell = (TextCell)this.findViewById(R.id.txt_item_code_cell);
		this.txt_item_name_cell = (TextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_exception_remark_cell = (TextCell)this.findViewById(R.id.txt_exception_remark_cell);
		this.txt_relay_comment_cell = (TextCell)this.findViewById(R.id.txt_relay_comment_cell);
		this.txt_user_name =(TextCell)this.findViewById(R.id.txt_user_name_cell);

		this.btn_prev = (ImageButton)this.findViewById(R.id.btn_prev);
		this.btn_next = (ImageButton)this.findViewById(R.id.btn_next);
		
		if (this.txt_order_code_cell != null) {
			this.txt_order_code_cell.setLabelText("单号");
			this.txt_order_code_cell.setReadOnly();
		}
		if (this.txt_user_name != null) {
			this.txt_user_name.setLabelText("用户");
		}
		
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("物料编码");
			this.txt_item_code_cell.setReadOnly();
		}
		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("物料名称");
			this.txt_item_name_cell.setReadOnly();
			this.txt_item_name_cell.TextBox.setSingleLine(true);
		}
		
		if (this.txt_exception_remark_cell != null) {
			this.txt_exception_remark_cell.setLabelText("异常说明");
			this.txt_exception_remark_cell.setReadOnly();
			//this.txt_exception_remark_cell.TextBox.setSingleLine(true);
		}
		
		if (this.txt_relay_comment_cell != null) {
			this.txt_relay_comment_cell.setLabelText("异常回复");
		}
		

		if (this.btn_prev != null) {
			this.btn_prev.setImageBitmap(App.Current.ResourceManager.getImage("@/core_prev_white"));
			this.btn_prev.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_mm_handover_exception_replay_editor.this.prev();
				}
			});
		}
		
		if (this.btn_next != null) {
			this.btn_next.setImageBitmap(App.Current.ResourceManager.getImage("@/core_next_white"));
			this.btn_next.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_mm_handover_exception_replay_editor.this.next();
				}
			});
		}
		 _id = this.Parameters.get("id", 0L);
		 
		if (_id > 0) {
			String sql = "exec p_mm_exception_relay_get_row_number ?,?";
			Parameters p = new Parameters().add(1, App.Current.UserID).add(2, _id);
			Result<Long> r = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, Long.class);
			if (r.HasError) {
				App.Current.showError(pn_mm_handover_exception_replay_editor.this.getContext(), "没有指定异常单据。");
				this.close();
			}
			
			if (r.Value != null && r.Value > 0L) {
				_rownum = r.Value;
				this.loadExceptionItem(_rownum);
			}
		} else {
			this.close();
			return;
		}
	}
	
	private Long _id;
	private Long _rownum;
	private Integer _total;

	
	public void prev()
	{
		if (_rownum >1 ) {
			this.loadExceptionItem(_rownum - 1);
		} else {
			App.Current.showError(pn_mm_handover_exception_replay_editor.this.getContext(), "已经是第一条。");
		}
	}
	
	public void next()
	{
		if (_rownum < _total) {
			this.loadExceptionItem(_rownum + 1);
		} else {
			App.Current.showError(pn_mm_handover_exception_replay_editor.this.getContext(), "已经是最后一条。");
		}
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
		 this.txt_user_name.setContentText(str);
		}
	}
	
	private DataRow _order_row;
	
	public void loadExceptionItem(long index)
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_mm_exception_relay_get_item ?,?";
		Parameters p  =new Parameters().add(1, App.Current.UserID).add(2, index);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_mm_handover_exception_replay_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_mm_handover_exception_replay_editor.this.getContext(), result.Error);
					return;
				}
				
				_order_row = result.Value;
				if (_order_row == null) {
					App.Current.showError(pn_mm_handover_exception_replay_editor.this.getContext(), "没有数据。");
					pn_mm_handover_exception_replay_editor.this.clearAll();
					pn_mm_handover_exception_replay_editor.this.Header.setTitleText("事务异常回复");
					return;
				}
				
				_total = _order_row.getValue("total", Integer.class);
				_rownum = _order_row.getValue("rownum", Long.class);
				if (_total > 0 ){
					pn_mm_handover_exception_replay_editor.this.Header.setTitleText("事务异常(共" + String.valueOf(_total) + "条)");
				} else {
					pn_mm_handover_exception_replay_editor.this.Header.setTitleText("事务异常");
				}

				String order_code = _order_row.getValue("code", "") +","+_order_row.getValue("org_code","")+ ", 第" + String.valueOf(_rownum) + "条,发起人："+_order_row.getValue("except_user","");
				String item_code = _order_row.getValue("item_code", "");
				String item_name =_order_row.getValue("item_name","");
			    String exception_remark=_order_row.getValue("exception_type", "")+","+_order_row.getValue("exception_comment", "")+",日期："+_order_row.getValue("create_time","")+"已发数："+App.formatNumber(_order_row.getValue("closed_quantity",BigDecimal.ZERO), "0.##")+"实点数："+App.formatNumber(_order_row.getValue("open_qty",BigDecimal.ZERO), "0.##");

	             
			    pn_mm_handover_exception_replay_editor.this.txt_order_code_cell.setContentText(order_code);  
				pn_mm_handover_exception_replay_editor.this.txt_item_code_cell.setContentText(item_code);
				pn_mm_handover_exception_replay_editor.this.txt_item_name_cell.setContentText(item_name);
				pn_mm_handover_exception_replay_editor.this.txt_exception_remark_cell.setContentText(exception_remark);
				
				
				
			}
		});
	}
	

	@Override
	public void commit()
	{
	
		
		final Long id = _order_row.getValue("id", Long.class);
		final  String relay_remark=this.txt_relay_comment_cell.getContentText();
		final String user_name =this.txt_user_name.getContentText().trim();
        if (relay_remark==null || relay_remark.length()==0)
        {
         App.Current.showError(this.getContext(), "请填写回复后提交！");
         return;
        }
        if (user_name==null || user_name.length()==0)
        {
         App.Current.showError(this.getContext(), "请输入或者扫描用户名！");
         return;
        }
		App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
		
				String sql = "update  mm_exception_handling_list set  reply_time=getdate(),reply_comment =?,replay_user_name=?  where   id=? ";
				Parameters p = new Parameters().add(1, relay_remark).add(2, user_name).add(3, id);
				Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(pn_mm_handover_exception_replay_editor.this.Connector, sql, p);
				if (r.HasError) {
					_order_row = null;
					 App.Current.showError(pn_mm_handover_exception_replay_editor.this.getContext(), r.Error);
					 pn_mm_handover_exception_replay_editor.this.clearAll();
					 pn_mm_handover_exception_replay_editor.this.loadExceptionItem(_rownum);
					}
					if (r.Value > 0) {
						_order_row = null;
						App.Current.toastInfo(pn_mm_handover_exception_replay_editor.this.getContext(), "提交成功!");
						pn_mm_handover_exception_replay_editor.this.clearAll();
					    pn_mm_handover_exception_replay_editor.this.loadExceptionItem(_rownum);
					}
				
			}
		});
	}
	

	
	public void clearAll()
	{
		this.txt_order_code_cell.setContentText("");
		this.txt_item_code_cell.setContentText("");
		this.txt_item_name_cell.setContentText("");
		this.txt_relay_comment_cell.setContentText("");
		this.txt_exception_remark_cell.setContentText("");
	}
}
