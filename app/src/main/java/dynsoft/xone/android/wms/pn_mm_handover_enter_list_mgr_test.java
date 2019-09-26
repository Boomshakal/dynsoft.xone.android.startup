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

public class pn_mm_handover_enter_list_mgr_test extends pn_mgr {

	public pn_mm_handover_enter_list_mgr_test(Context context) {
		super(context);
	}
	private int id;
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
	public  String lot_number;
	public String[] arr;

	@Override
	public void onPrepared() {
		super.onPrepared();
		this.CommitButton = (ImageButton)this.findViewById(R.id.btn_commit);
		if (this.CommitButton != null){
			this.CommitButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_white"));
			this.CommitButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_mm_handover_enter_list_mgr_test.this.commit();
				}
			});
		}
	    if (this.BackButton != null){
				this.BackButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_exit_white"));
				this.BackButton.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						pn_mm_handover_enter_list_mgr_test.this.backenvent();
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
		id = this.Parameters.get("id", 0);
	}
	
	public void backenvent()
	{
		pn_mm_handover_enter_list_mgr_test.this.close();
		//Link link = new Link("pane://x:code=mm_and_handover_list_mgr");
        //link.Open(this, this.getContext(), null);
    
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
//				App.Current.showError(pn_mm_handover_enter_list_mgr_test.this.getContext(),"还有未扫描的条码");
//				return;
//
//
//
//			}
//
//		}

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
			 arr = str.split("-");
			lot_number=arr[0];
			String sql=" SELECT top 1 t1.name vendor_name, t0.task_order_id , t0.work_order_id , t0.date_code FROM  mm_wo_transaction t0 LEFT JOIN   dbo.mm_partner t1 ON t0.vendor_id = t1.id WHERE t0.lot_number = ?";
			Parameters p = new Parameters().add(1, lot_number);
			App.Current.DbPortal.ExecuteRecordAsync(pn_mm_handover_enter_list_mgr_test.this.Connector, sql, p, new ResultHandler<DataRow>() {
				public void handleMessage(Message msg) {
					Result<DataRow> result = this.Value;
					if (result.HasError) {
						App.Current.showError(pn_mm_handover_enter_list_mgr_test.this.getContext(), result.Error);
						return;
					} else if (result == null) {
						App.Current.showError(pn_mm_handover_enter_list_mgr_test.this.getContext(), "此Lot_number不属于这个审料");
					} else {
						String cjname = result.Value.getValue("vendor_name", "");
						String dc = result.Value.getValue("date_code", "");
						int flag = 0;
						final String sql = "UPDATE qm_ipqc_item SET CJ_NAME=?,DC=?,IS_COMPLETE=1\n" +
								"                                where item_id=(select id from mm_item where code=?) and head_id=?\n" +
								"                                update qm_ipqc_head set update_time=GETDATE() , updator_id=? WHERE id=?";
						Parameters p = new Parameters().add(1, cjname).add(2, dc).add(3, arr[1]).add(4, id).add(5, App.Current.UserID).add(6, id);
						App.Current.DbPortal.ExecuteNonQueryAsync(pn_mm_handover_enter_list_mgr_test.this.Connector, sql, p, new ResultHandler<Integer>() {
							@Override
							public void handleMessage(Message msg) {
								Result<Integer> result = this.Value;
								if (result.HasError) {
									App.Current.showError(pn_mm_handover_enter_list_mgr_test.this.getContext(), result.Error);
									return;
								} else if (result.Value != 0) {
									pn_mm_handover_enter_list_mgr_test.this.Adapter.DataTable = null;
									pn_mm_handover_enter_list_mgr_test.this.Adapter.notifyDataSetChanged();
									pn_mm_handover_enter_list_mgr_test.this.refresh();
									App.Current.showError(pn_mm_handover_enter_list_mgr_test.this.getContext(), "保存成功"+sql);
								} else {
									App.Current.showError(pn_mm_handover_enter_list_mgr_test.this.getContext(), "保存出错，请检查你的料号是否属于这个");
								}

							}
						});
					}
				}


			});


		} else {
			App.Current.showError(pn_mm_handover_enter_list_mgr_test.this.getContext(),"你扫描的条码有误");
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
		int exception_status;
        Boolean bool =row.getValue("IS_COMPLETE",false);
        if(bool==true){
			 exception_status = 1;
		}else{
			exception_status= 0;
		}




        BigDecimal qty =row.getValue("closed_quantity",BigDecimal.ZERO);
        BigDecimal rec_qty =row.getValue("receive_quantity",BigDecimal.ZERO);
        
        TextView num = (TextView)convertView.findViewById(R.id.num);
        CheckBox checked=(CheckBox)convertView.findViewById(R.id.select_all);

        TextView line1 = (TextView)convertView.findViewById(R.id.txt_line1);
        TextView line2 = (TextView)convertView.findViewById(R.id.txt_line2);
        TextView line3 = (TextView)convertView.findViewById(R.id.txt_line3);
        TextView line4 = (TextView)convertView.findViewById(R.id.txt_line4);
        //btn1.setHeight(1);
        //btn1.setWidth(1);



  
        if(exception_status==1)
        {
        	line1.setBackgroundColor(Color.parseColor("#DAA520"));
        	line2.setBackgroundColor(Color.parseColor("#DAA520"));
        	line3.setBackgroundColor(Color.parseColor("#DAA520"));
        	line4.setBackgroundColor(Color.parseColor("#DAA520"));
        }
        else
        {
            if (rec_qty.compareTo(qty)>=0)
            {
            	checked.setChecked(true); 
               	line1.setBackgroundColor(Color.parseColor("#FFFFF0"));
            	line2.setBackgroundColor(Color.parseColor("#FFFFF0"));
            	line3.setBackgroundColor(Color.parseColor("#FFFFF0"));
            	line4.setBackgroundColor(Color.parseColor("#FFFFF0"));
            }else
            {
        	line1.setBackgroundColor(Color.parseColor("#FFFFF0")); 
        	line2.setBackgroundColor(Color.parseColor("#FFFFF0")); 
        	line3.setBackgroundColor(Color.parseColor("#FFFFF0")); 
        	line4.setBackgroundColor(Color.parseColor("#FFFFF0")); 
            }
        }
        num.setText(String.valueOf(position + 1));
        line1.setText(row.getValue("item_code", "")+row.getValue("store_keeper_name", ""));
        line2.setText(String.valueOf(row.getValue("dc", ""))+","+row.getValue("item_code", "")+"," + "用量 "+String.valueOf(row.getValue("COMPONENT_QUANTITY", "") ));
        line3.setText(row.getValue("item_name", ""));
        line4.setText("");
        return convertView;
    }
 
	@Override





    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
		id = this.Parameters.get("id", 0);
		Log.e("lzh111",String.valueOf(id));
    	//Log.e("lzh111",String.valueOf(id));
		//id = this.Parameters.get("id", 0);
		//_id=this.Parameters.get("id", 0L);
		SqlExpression expr = new SqlExpression();
        expr.SQL = "exec get_check_item_ipqc_2 ?,?,?,?";
        expr.Parameters = new Parameters().add(1, id).add(2, start).add(3,end).add(4, search);

        return expr;

    }
}
