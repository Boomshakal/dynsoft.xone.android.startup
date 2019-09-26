package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.start.pn_base;

public class pn_mm_handover_enter_list_mgr extends pn_mgr {

	public pn_mm_handover_enter_list_mgr(Context context) {
		super(context);
	}
	private Long _order_id;
	private Long _user_id;
	private Long _id;
	private String order_code;
	private String user_name;
	private String items;
	public ImageButton CommitButton;
	public ImageButton CommitReturn;
	public String task_code;
	public int item_id;
	public int flage1;
	@Override
	public void onPrepared() {
		super.onPrepared();
		this.CommitButton = (ImageButton)this.findViewById(R.id.btn_commit);
		if (this.CommitButton != null){
			this.CommitButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_white"));
			this.CommitButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_mm_handover_enter_list_mgr.this.commit();
				}
			});
		}
	    if (this.BackButton != null){
				this.BackButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_exit_white"));
				this.BackButton.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						pn_mm_handover_enter_list_mgr.this.backenvent();
					}
				});
			}
		task_code = this.Parameters.get("task_code","");
	   // Log.e("LZH85",task_code);
		//App.Current.showError(this.getContext(), task_code);
	    this.Adapter.PageSize =99999999;
		this.Adapter.FooterText.setText("");
		this.Adapter.FooterText.clearAnimation();
		this.Adapter.FooterText.setVisibility(GONE);
		this.Adapter.Footer.setVisibility(GONE);
	}
	
	public void backenvent()
	{
	    pn_mm_handover_enter_list_mgr.this.close();
		//Link link = new Link("pane://x:code=mm_and_handover_list_mgr");
        //link.Open(this, this.getContext(), null);
    
	}
	@Override
    public  void openItem(DataRow row)
    {
		if (row.getValue("exception_status",0)==1)
		{
		 App.Current.showError(this.getContext(), "已经发起异常处理单据");
		 return;
		}
		Link link = new Link("pane://x:code=mm_and_handover_exception_editor");
		link.Parameters.add("order_id", row.getValue("head_id", Long.class));
        link.Parameters.add("line_id", row.getValue("line_id", 0));
        link.Parameters.add("user_id", _user_id);
        link.Parameters.add("id", _id);
        link.Parameters.add("items", items);
        link.Parameters.add("user_name", user_name);
        link.Parameters.add("code", order_code);
        link.Open(this, this.getContext(), null);
        pn_mm_handover_enter_list_mgr.this.close();

    }
    
	public void commit()
	{
//		Log.e("lzh111",String.valueOf(Adapter.DataTable.Rows.size()));
//
//		int flage = Adapter.DataTable.Rows.size();
//
//		for(int i=0 ;i<flage;i++){
//
//			BigDecimal rec_qty = Adapter.DataTable.Rows.get(i).getValue("closed_quantity",BigDecimal.ZERO);
//			BigDecimal qty = Adapter.DataTable.Rows.get(i).getValue("receive_quantity",BigDecimal.ZERO);
//			Log.e("lzh111",String.valueOf(rec_qty));
//			Log.e("lzh111",String.valueOf(qty));
//			if(rec_qty.compareTo(qty)>0){
//
//				App.Current.showError(pn_mm_handover_enter_list_mgr.this.getContext(),"还有未扫描的条码");
//				return;
//
//
//
//			}
//
//		}
		String sql = "exec p_mm_handover_list_check_item ?";
		Parameters p = new Parameters().add(1, _order_id);
		App.Current.DbPortal.ExecuteRecordAsync(pn_mm_handover_enter_list_mgr.this.Connector, sql, p, new ResultHandler<DataRow>(){
			public void handleMessage(Message msg) {
				Result<DataRow> result = this.Value;
				if (result.HasError) {
							App.Current.showError(pn_mm_handover_enter_list_mgr.this.getContext(), result.Error);
								return;
						}
						DataRow row = result.Value;
						if (row == null) {
							App.Current.showError(pn_mm_handover_enter_list_mgr.this.getContext(),"数据为空");
							}
							else {
							flage1 = row.getValue("rtn", 0);
							if(flage1==0){
								Link link = new Link("pane://x:code=mm_and_handover_enter_list_editor");
								link.Parameters.add("id", _id);
								link.Parameters.add("order_id", _order_id);
								link.Parameters.add("items", items);
								link.Parameters.add("user_name", user_name);
								link.Parameters.add("code", order_code);
								link.Parameters.add("user_id", _user_id);
								link.Parameters.add("task_code", task_code);
								link.Open(pn_mm_handover_enter_list_mgr.this, pn_mm_handover_enter_list_mgr.this.getContext(), null);
								pn_mm_handover_enter_list_mgr.this.close();
							}
							else {
								App.Current.showError(pn_mm_handover_enter_list_mgr.this.getContext(),"你还有没有扫描完的条码");
							}
						}
			}
				});
	}
	@Override
    public void setContentView()
	{
		LayoutParams lp = new LayoutParams(-1,-1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_mm_handover_enter_list_mgr, this, true);
        view.setLayoutParams(lp);
	}

	
	@Override
	public void onScan(final String barcode)
	{
		 String txt = barcode;
		if (barcode.startsWith("CRQ:")) {
			String str = barcode.substring(4, barcode.length());
			String[] arr = str.split("-");
			if (arr.length > 0) {
				txt = "SCAN:"+arr[1]+":"+arr[2];
			}
			String sql = "exec p_mm_handover_list_recv_scan  ?,?,?,?,? ";
			Parameters p = new Parameters().add(1, App.Current.UserID).add(2, _order_id).add(3, arr[0]);
			p.add(4, arr[1]).add(5, arr[2]);
			App.Current.DbPortal.ExecuteNonQueryAsync(this.Connector, sql, p, new ResultHandler<Integer>() {
				@Override
				public void handleMessage(Message msg) {
					pn_mm_handover_enter_list_mgr.this.ProgressDialog.dismiss();

					Result<Integer> result = this.Value;
					if (result.HasError) {
						App.Current.showError(pn_mm_handover_enter_list_mgr.this.getContext(),result.Error);
						return;
					}
					String str = barcode.substring(4, barcode.length());
					String txt2="";
					String[] arr = str.split("-");
					if (arr.length > 0) {
						txt2 = "SCAN:"+arr[1]+":"+arr[2];
					}
					pn_mm_handover_enter_list_mgr.this.SearchBox.setText(txt2);
//					String txt3 = txt2.trim();
//					String sql1 = "select * from dbo.mm_item WHERE code =? ";
//					Parameters p1 = new Parameters().add(1,txt3);
//					App.Current.DbPortal.ExecuteRecordAsync(pn_mm_handover_enter_list_mgr.this.Connector, sql1, p1, new ResultHandler<DataRow>(){
//						@Override
//						public void handleMessage(Message msg) {
//							Result<DataRow> result = this.Value;
//							if (result.HasError) {
//								App.Current.showError(pn_mm_handover_enter_list_mgr.this.getContext(), result.Error);
//								return;
//							}
//							DataRow row = result.Value;
//							if (row == null) {
//								App.Current.showError(pn_mm_handover_enter_list_mgr.this.getContext(),"数据为空");
//							}
//							else {
//								item_id = row.getValue("id", 0);
//								int flag = Adapter.DataTable.Rows.size();
//								for(int i = 0;i<flag;i++){
//
//								}
//
//							}
//
//
//
//						}
//
//							});




					pn_mm_handover_enter_list_mgr.this.Adapter.DataTable = null;
					pn_mm_handover_enter_list_mgr.this.Adapter.notifyDataSetChanged();
					pn_mm_handover_enter_list_mgr.this.refresh();
				}
			});
		
	    	
		} else if (barcode.startsWith("L:")){
			String loc = barcode.substring(2, barcode.length());
			this.SearchBox.setText(loc);
	    	this.Adapter.DataTable = null;
	    	this.Adapter.notifyDataSetChanged();
	    	this.refresh();
		}
    }
	
	@Override
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
    	final DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_mm_handover_enter_list, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        	
        }
        int exception_status =row.getValue("exception_status",0);
        
        BigDecimal qty =row.getValue("closed_quantity",BigDecimal.ZERO);
        BigDecimal rec_qty =row.getValue("receive_quantity",BigDecimal.ZERO);
        
        TextView num = (TextView)convertView.findViewById(R.id.num);
        CheckBox checked=(CheckBox)convertView.findViewById(R.id.select_all);
        Button  btn1 =(Button)convertView.findViewById(R.id.Button1);
        TextView line1 = (TextView)convertView.findViewById(R.id.txt_line1);
        TextView line2 = (TextView)convertView.findViewById(R.id.txt_line2);
        TextView line3 = (TextView)convertView.findViewById(R.id.txt_line3);
        TextView line4 = (TextView)convertView.findViewById(R.id.txt_line4);
        //btn1.setHeight(1);
        //btn1.setWidth(1);
       
        
       
        btn1.setText("发起异常");
        btn1.setTextColor(Color.RED);
        btn1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				pn_mm_handover_enter_list_mgr.this.openItem(row);
			}
		});
  
        if(exception_status==1)
        {
        	line1.setBackgroundColor(Color.parseColor("#EE2C2C")); 
        	line2.setBackgroundColor(Color.parseColor("#EE2C2C")); 
        	line3.setBackgroundColor(Color.parseColor("#EE2C2C")); 
        	line4.setBackgroundColor(Color.parseColor("#EE2C2C")); 
        }
        else
        {
            if (rec_qty.compareTo(qty)>=0)
            {
            	checked.setChecked(true); 
               	line1.setBackgroundColor(Color.parseColor("#DAA520")); 
            	line2.setBackgroundColor(Color.parseColor("#DAA520")); 
            	line3.setBackgroundColor(Color.parseColor("#DAA520")); 
            	line4.setBackgroundColor(Color.parseColor("#DAA520"));
            }else
            {
        	line1.setBackgroundColor(Color.parseColor("#FFFFF0")); 
        	line2.setBackgroundColor(Color.parseColor("#FFFFF0")); 
        	line3.setBackgroundColor(Color.parseColor("#FFFFF0")); 
        	line4.setBackgroundColor(Color.parseColor("#FFFFF0")); 
            }
        }
        num.setText(String.valueOf(position + 1));
        line1.setText(row.getValue("code", "")+row.getValue("store_keeper_name", ""));
        line2.setText(row.getValue("locations", "")+","+row.getValue("item_code", "")+"," + row.getValue("org_code", "") + ", 已出库");
        line3.setText(row.getValue("item_name", ""));
        line4.setText("发料数量："+App.formatNumber(row.getValue("closed_quantity",BigDecimal.ZERO),"0.##")+" /已扫描数量:"+App.formatNumber(rec_qty,"0.##"));
        return convertView;
    }
 
	@Override





    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
		_order_id = this.Parameters.get("order_id", 0L);
		_user_id = this.Parameters.get("user_operation", 0L);
		order_code=this.Parameters.get("code", "");
		user_name=this.Parameters.get("user_name", "");
		items=this.Parameters.get("items","");
		//_id=this.Parameters.get("id", 0L);
		SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_mm_handover_list_get_item_test ?,?,?,?";
        expr.Parameters = new Parameters().add(1, _order_id).add(2, start).add(3,999999999).add(4, search);
        return expr;
    }
}
