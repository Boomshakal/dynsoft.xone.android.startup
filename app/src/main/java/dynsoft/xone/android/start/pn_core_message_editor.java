package dynsoft.xone.android.start;

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
import dynsoft.xone.android.wms.pn_editor;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class pn_core_message_editor extends pn_editor {

	public pn_core_message_editor(Context context) {
		super(context);
	}

	public TextCell txt_sender;
	public TextCell txt_send_time;
	public TextCell txt_type;
	public TextCell txt_subject;
	public TextCell txt_content;
	public TextCell txt_receiver;
	public ImageButton btn_delete;
	public ImageButton btn_prev;
	public ImageButton btn_next;

	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_core_message_editor, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_sender = (TextCell)this.findViewById(R.id.txt_sender);
		this.txt_send_time = (TextCell)this.findViewById(R.id.txt_send_time);
		this.txt_type = (TextCell)this.findViewById(R.id.txt_type);
		this.txt_subject = (TextCell)this.findViewById(R.id.txt_subject);
		this.txt_content = (TextCell)this.findViewById(R.id.txt_content);
		this.txt_receiver = (TextCell)this.findViewById(R.id.txt_receiver);
		
		this.btn_delete = (ImageButton)this.findViewById(R.id.btn_delete);
		//this.btn_prev = (ImageButton)this.findViewById(R.id.btn_prev);
		//this.btn_next = (ImageButton)this.findViewById(R.id.btn_next);
		
		if (this.txt_sender != null) {
			this.txt_sender.setLabelText("发送者");
			this.txt_sender.setReadOnly();
		}
		
		if (this.txt_send_time != null) {
			this.txt_send_time.setLabelText("发送时间");
			this.txt_send_time.setReadOnly();
		}
		
		if (this.txt_type != null) {
			this.txt_type.setLabelText("消息类型");
			this.txt_type.setReadOnly();
			this.txt_type.TextBox.setSingleLine(true);
		}
		
		if (this.txt_subject != null) {
			this.txt_subject.setLabelText("标题");
			this.txt_subject.setReadOnly();
			this.txt_subject.TextBox.setSingleLine(true);
		}
		
		if (this.txt_content != null) {
			this.txt_content.setLabelText("内容");
			this.txt_content.setReadOnly();
			this.txt_content.TextBox.setSingleLine(true);
		}
		
		if (this.txt_receiver != null) {
			this.txt_receiver.setLabelText("接收者");
			this.txt_receiver.setReadOnly();
		}
		
		if (this.btn_delete != null) {
			this.btn_delete.setImageBitmap(App.Current.ResourceManager.getImage("@/core_delete_white"));
			this.btn_delete.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_core_message_editor.this.delete();
				}
			});
		}
		
		//if (this.btn_prev != null) {
		//	this.btn_prev.setImageBitmap(App.Current.ResourceManager.getImage("@/core_prev_white"));
		//	this.btn_prev.setOnClickListener(new OnClickListener(){
		//		@Override
		//		public void onClick(View v) {
		//			pn_core_message_editor.this.prev();
		//		}
		//	});
		//}
		
		//if (this.btn_next != null) {
		//	this.btn_next.setImageBitmap(App.Current.ResourceManager.getImage("@/core_next_white"));
		//	this.btn_next.setOnClickListener(new OnClickListener(){
		//		@Override
		//		public void onClick(View v) {
		//			pn_core_message_editor.this.next();
		//		}
		//	});
		//}
		
		_message_id = this.Parameters.get("message_id", 0L);
		_rownum = 1L;
		
		this.loadMessageItem(_rownum);
	}
	
	private Long _rownum;
	private Long _message_id;
	private Integer _total = 0;
	private DataRow _message_row;
	
	public void prev()
	{
		if (_rownum > 1) {
			this.loadMessageItem(_rownum - 1);
		} else {
			App.Current.showError(pn_core_message_editor.this.getContext(), "已经是第一条。");
		}
	}
	
	public void next()
	{
		if (_rownum < _total) {
			this.loadMessageItem(_rownum + 1);
		} else {
			App.Current.showError(pn_core_message_editor.this.getContext(), "已经是最后一条。");
		}
	}
	
	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
	}
	
	public void loadMessageItem(long index)
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_core_message_get_item ?,?,?";
		Parameters p  =new Parameters().add(1, App.Current.UserID).add(2, _message_id).add(3, index);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_core_message_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_core_message_editor.this.getContext(), result.Error);
					return;
				}
				
				DataRow row = result.Value;
				if (row != null) {
					_message_row = row;
					_total = row.getValue("total", Integer.class);
					_rownum = row.getValue("rownum", Long.class);
					//if (_total > 0 ){
					//	pn_core_message_editor.this.Header.setTitleText("我的消息(共" + String.valueOf(_total) + "未读)");
					//} else {
					//	pn_core_message_editor.this.Header.setTitleText("我的消息");
					//}
		
					pn_core_message_editor.this.txt_sender.setContentText(row.getValue("create_user_name", ""));
					pn_core_message_editor.this.txt_send_time.setContentText(App.formatDateTime(row.getValue("create_time"),"yyyy-MM-dd HH:mm"));
					pn_core_message_editor.this.txt_type.setContentText(row.getValue("type", ""));
					pn_core_message_editor.this.txt_subject.setContentText(row.getValue("subject", ""));
					pn_core_message_editor.this.txt_content.setContentText(row.getValue("content", ""));
					pn_core_message_editor.this.txt_receiver.setContentText(row.getValue("receiver", ""));
				}
			}
		});
	}
	
	public void delete()
	{
		App.Current.question(this.getContext(), "确定要删除吗？", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
	}
}
