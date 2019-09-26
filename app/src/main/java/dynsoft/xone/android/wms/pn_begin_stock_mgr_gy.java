package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import dynsoft.xone.android.blueprint.Demo_ad_escActivity;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.link.PaneLinker;

public class pn_begin_stock_mgr_gy extends pn_mgr {

	public pn_begin_stock_mgr_gy(Context context) {
		super(context);
	}

	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_begin_stock_mgr, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
    public void onPrepared() {
        super.onPrepared();
        this.Matrix.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
                if (arg2 < Adapter.DataTable.Rows.size()) {
                	final DataRow row = (DataRow)Adapter.getItem((int)arg2);
    				pn_begin_stock_mgr_gy.this.printLabel(row);
                } else {
                	pn_begin_stock_mgr_gy.this.refreshData(false);
                }
			
			}
        	
        });
       /* this.Matrix.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long index) {
				final DataRow row = (DataRow)Adapter.getItem((int)index);
				App.Current.question(pn_begin_stock_mgr.this.getContext(), "你确定要打印此批次条码吗？", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						pn_begin_stock_mgr.this.printLabel(row);
					}
				});
				return false;
			}
		});*/
	}
	 public void printLabel(final DataRow row)
	    {
	        final String items[] = {"霍尼韦尔", "芝柯"};

	        AlertDialog dialog1 = new AlertDialog.Builder(App.Current.Workbench).setTitle("请选择打印机")
	                .setItems(items, new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialogInterface, int i) {
	                       
	                        if(i == 1) {
	                            String sql = "exec get_pint_date";
								App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, new ResultHandler<DataRow>() {
									@Override
									public void handleMessage(Message msg) {
										Result<DataRow> value = Value;
										if (value.HasError) {
											App.Current.toastError(pn_begin_stock_mgr_gy.this.getContext(), value.Error);
											return;
										}
										if (value.Value != null) {
											Intent intent = new Intent();
											intent.setClass(App.Current.Workbench, Demo_ad_escActivity.class);
											String cur_date = value.Value.getValue("cur_date", "");
											intent.putExtra("cur_date", cur_date);
										intent.putExtra("org_code", row.getValue("org_code", ""));
	                        intent.putExtra("item_code", row.getValue("item_num", ""));
	                        intent.putExtra("vendor_model", row.getValue("vendor_model", ""));
	                        intent.putExtra("lot_number", row.getValue("lot_number", ""));
	                        intent.putExtra("vendor_lot", row.getValue("vendor_lot", ""));
	                        intent.putExtra("date_code", row.getValue("date_code", ""));
	                        intent.putExtra("quantity", App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.####"));
	                        intent.putExtra("ut", row.getValue("uom_code", ""));
											App.Current.Workbench.startActivity(intent);
										}
									}
								});
	                        } else {
								 Intent intent = new Intent();
	                            intent.setClass(App.Current.Workbench, frm_item_lot_printer.class);
	                        intent.putExtra("org_code", row.getValue("org_code", ""));
	                        intent.putExtra("item_code", row.getValue("item_num", ""));
	                        intent.putExtra("vendor_model", row.getValue("vendor_model", ""));
	                        intent.putExtra("lot_number", row.getValue("lot_number", ""));
	                        intent.putExtra("vendor_lot", row.getValue("vendor_lot", ""));
	                        intent.putExtra("date_code", row.getValue("date_code", ""));
	                        intent.putExtra("quantity", App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.####"));
	                        intent.putExtra("ut", row.getValue("uom_code", ""));

	                        App.Current.Workbench.startActivity(intent);
	                        }
	                    }
	                }).create();
	        dialog1.show();


	        //Intent intent = new Intent();
	        //intent.setClass(this.getContext(), frm_item_lot_printer_init.class);
	        //intent.putExtra("org_code", row.getValue("org_code", ""));
	        //intent.putExtra("item_code", row.getValue("item_num", ""));
	        //intent.putExtra("vendor_model", row.getValue("vendor_model", ""));
	        //intent.putExtra("lot_number", row.getValue("lot_number", ""));
	        //intent.putExtra("vendor_lot", row.getValue("vendor_lot", ""));
	        //intent.putExtra("date_code", row.getValue("date_code", ""));
	        //intent.putExtra("quantity", App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.####"));
	        //intent.putExtra("ut", row.getValue("uom_code", ""));
	        //intent.putExtra("usercode", App.Current.UserCode);

	        //this.getContext().startActivity(intent);
	    }
	@Override
	public void create()
	{
		Link link = new Link("pane://x:code=mm_begin_stock_editor_yh");
        link.Open(this, this.getContext(), null);
	}
    
	@Override
	public void onScan(final String barcode)
	{
    	this.SearchBox.setText(barcode);
    	this.Adapter.DataTable = null;
    	this.Adapter.notifyDataSetChanged();
    	this.refresh();
    	this.SearchBox.setText("");
    }
	
	@Override
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
    	DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_begin_stock, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView line0 = (TextView)convertView.findViewById(R.id.txt_line0);
        TextView line1 = (TextView)convertView.findViewById(R.id.txt_line1);
        TextView line2 = (TextView)convertView.findViewById(R.id.txt_line2);
        TextView line3 = (TextView)convertView.findViewById(R.id.txt_line3);
        TextView line4 = (TextView)convertView.findViewById(R.id.txt_line4);
        TextView line5 = (TextView)convertView.findViewById(R.id.txt_line5);
        TextView line6 = (TextView)convertView.findViewById(R.id.txt_line6);

 
        
        line0.setText(String.valueOf(position + 1));
        line1.setText(row.getValue("item_num", "")+","+row.getValue("org_code", "")+","+row.getValue("vendor_model", ""));
        line2.setText(row.getValue("vendor_name", ""));
        line3.setText(row.getValue("lot_number", ""));
        line4.setText(row.getValue("item_desc", ""));
        String qty = "D/C:"+row.getValue("date_code", "") + "," + App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##")+ row.getValue("uom_code", "");
        line5.setText(qty);
        line6.setText(row.getValue("location_code", ""));
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
    	SqlExpression expr = new SqlExpression();
        expr.SQL = "with temp as (SELECT row_number() over(order by create_time desc) rownum,";
        expr.SQL += "org_code,item_num,vendor_lot,vendor_name,item_desc,lot_number,quantity,uom_code,date_code,vendor_model,location_code ";
        expr.SQL += "FROM dbo.v_mm_stock_lot_init where (item_num like '%'+isnull(?,'')+'%' or date_code like '%'+isnull(?,'')+'%'  or lot_number like '%'+isnull(?,'')+'%' or isnull(?,'') like '%'+lot_number+'%')) ";
        expr.SQL += "select (select count(*) from temp) lines_count,* from temp where rownum>=? and rownum<=?";
        expr.Parameters = new Parameters().add(1, search).add(2, search).add(3, search).add(4, search).add(5, start).add(6,end);
        return expr;
    }
	
	public void commit(DataRow row)
	{
	}
}
