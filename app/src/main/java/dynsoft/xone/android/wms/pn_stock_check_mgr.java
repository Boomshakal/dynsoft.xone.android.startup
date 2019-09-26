package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class pn_stock_check_mgr extends pn_mgr {

	public pn_stock_check_mgr(Context context) {
		super(context);
	}

	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_stock_check_mgr, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
    public void onPrepared() {
        super.onPrepared();
        
//        this.Matrix.setOnItemLongClickListener(new OnItemLongClickListener(){
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long index) {
//				final DataRow row = (DataRow)Adapter.getItem((int)index);
//				new AlertDialog.Builder(pn_stock_check_mgr.this.getContext())
//				.setMessage("确定已完成该物料和库位的盘点并提交吗？")
//				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						commitStockCheckItem(row);
//					}
//				}).setNegativeButton("取消", null)
//				.show();
//				
//				return false;
//			}
//		});
        
        
        this.Matrix.setOnCreateContextMenuListener(new OnCreateContextMenuListener(){
  			@Override
  			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
  				menu.add("批次账差异...");
  				menu.add("清零重新盘...");
  				menu.add("刷新现库存...");
  				menu.add("提交盘点单...");
  			}
  		});
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuItem.getMenuInfo();
		final DataRow row = this.Adapter.DataTable.Rows.get(info.position);
		final Long order_id = row.getValue("id", 0L);
		final String check_type =row.getValue("check_type","");
		final int line_id = row.getValue("line_id", 0);
	    final int check_flag=row.getValue("check_flag",0);
		if (menuItem.getTitle() == "清零重新盘...") {
			
			new AlertDialog.Builder(pn_stock_check_mgr.this.getContext())
			.setMessage("确定要清零重盘吗？")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				     String check_f="初盘";
					    if (check_flag==1)
					    {
					    	check_f="复盘";
					    }
					String sql="exec p_mm_stock_check_reset_item ?,?,?,? "; 
					Parameters p = new Parameters().add(1, order_id).add(2, line_id).add(3, App.Current.UserID).add(4, check_f);
					Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(pn_stock_check_mgr.this.Connector, sql, p);
					if (r.HasError) {
						App.Current.toastInfo(pn_stock_check_mgr.this.getContext(), r.Error);
						return ;
					}
					if (r.Value > 0) {
						pn_stock_check_mgr.this.refresh();
					}
				}
			}).setNegativeButton("取消", null)
			.show();
			
			return false;
			
		
			
		} else if (menuItem.getTitle() == "刷新现库存...") {
			
			new AlertDialog.Builder(pn_stock_check_mgr.this.getContext())
			.setMessage("确定要刷新库存吗？")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String sql="exec p_refresh_mm_stock_check_item_store_qty ?,?"; 
					Parameters p = new Parameters().add(1, order_id).add(2, line_id);
					Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(pn_stock_check_mgr.this.Connector, sql, p);
					if (r.HasError) {
						App.Current.toastInfo(pn_stock_check_mgr.this.getContext(), r.Error);
					}
					if (r.Value > 0) {
						pn_stock_check_mgr.this.refresh();
					}
				}
			}).setNegativeButton("取消", null)
			.show();
			
			return true;
			
		
			
			
		} else if (menuItem.getTitle() == "提交盘点单...") {
			if (check_type.equals("年度盘点"))
			{
			String sql="";
			if (check_flag==1)
			{
				sql= "  select  count(1) rtncont,ISNULL(min(check_quantity),0.0) check_quantity,ISNULL(min(recheck_quantity),0.0) recheck_quantity ,ISNULL(min(onhand_quantity),0.0)  onhand_quantity from mm_stock_check_item where isnull(status,'')!='' and head_id=? and line_id=? and isnull(check_quantity,0) <>isnull(recheck_quantity,0) ";
			}else
			{
				sql= "  select  count(1) rtncont,ISNULL(min(check_quantity),0.0) check_quantity,ISNULL(min(recheck_quantity),0.0) recheck_quantity ,ISNULL(min(onhand_quantity),0.0)  onhand_quantity   from mm_stock_check_item where isnull(restatus,'')!='' and head_id=? and line_id=?  and isnull(check_quantity,0) <>isnull(recheck_quantity,0) ";
			}
			Parameters p = new Parameters().add(1,order_id).add(2, line_id);
			Result<DataRow> ri = App.Current.DbPortal.ExecuteRecord(this.Connector,
					sql, p);
			if (ri.HasError) {
				App.Current.toastInfo(this.getContext(), ri.Error);
			}else
			{
			     if (ri.Value.getValue("rtncont",0)==1)
			 {
              
			    	 BigDecimal v =ri.Value.getValue("check_quantity", BigDecimal.ZERO);
			    	 String check_quantity =App.formatNumber(v, "0.####");
			    	 String recheck_quantity =App.formatNumber(ri.Value.getValue("recheck_quantity", BigDecimal.ZERO), "0.####");
			    	 String onhand_quantity =App.formatNumber(ri.Value.getValue("onhand_quantity", BigDecimal.ZERO), "0.####");
			    	 new AlertDialog.Builder(this.getContext())
					.setMessage("此条盘点复盘数量:["+recheck_quantity+"]和初盘数量:["+check_quantity+"]有差异,账载数量："+onhand_quantity+"，请查看核对盘点数据！")
					.setNeutralButton("查看", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Link link = new Link("pane://x:code=mm_and_stock_check_offset_item");
					    	link.Parameters.add("head_id", row.getValue("id",0L));
					    	link.Parameters.add("line_id", row.getValue("line_id",0));
					        link.Open(pn_stock_check_mgr.this, pn_stock_check_mgr.this.getContext(), null);
						}
					}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							pn_stock_check_mgr.this.commitStockCheckItem(row);
						}
					}).setNegativeButton("取消", null)
					.show();
			 
			 }else
			 {
					new AlertDialog.Builder(pn_stock_check_mgr.this.getContext())
					.setMessage("确定已完成该物料和库位的盘点并提交吗？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							commitStockCheckItem(row);
						}
					}).setNegativeButton("取消", null)
					.show();
					
					return true;
				} 
			 }
			
			} else
			{
				new AlertDialog.Builder(pn_stock_check_mgr.this.getContext())
				.setMessage("确定已完成该物料和库位的盘点并提交吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						commitStockCheckItem(row);
					}
				}).setNegativeButton("取消", null)
				.show();
				
				return true;
			}
		  }else if (menuItem.getTitle() == "批次账差异...") {
			  
			    String check_f="初盘";
			    if (check_flag==1)
			    {
			    	check_f="复盘";
			    }
			    Link link = new Link("pane://x:code=mm_and_stock_check_offset_lot");
		    	link.Parameters.add("head_id", row.getValue("id",0L));
		    	link.Parameters.add("line_id", row.getValue("line_id",0));
		    	link.Parameters.add("check_flag", check_f);
		        link.Open(pn_stock_check_mgr.this, pn_stock_check_mgr.this.getContext(), null);
			  
		  }
		
		
		return true;
	}
	
	
	public void commitStockCheckItem(DataRow row)
	{
		Long order_id = row.getValue("id", 0L);
		int line_id = row.getValue("line_id", 0);
		
		String sql = "exec p_mm_stock_check_commit_item ?,?,?";
		Parameters p = new Parameters().add(1, order_id).add(2, line_id).add(3, App.Current.UserID);
		Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(pn_stock_check_mgr.this.Connector, sql, p);
		if (r.HasError) {
			App.Current.toastInfo(pn_stock_check_mgr.this.getContext(), r.Error);
			return;
		}
		
		if (r.Value > 0) {
			this.refresh();
		}
	}
	
	@Override
	public void onScan(final String barcode)
	{
		if (barcode.startsWith("CRQ:")) {
			String cc = barcode.substring(4, barcode.length());
			String[] arr = cc.split("-");
			if (arr.length > 1) {
				String item_code = arr[1];
				this.SearchBox.setText(item_code);
		    	this.Adapter.DataTable = null;
		    	this.Adapter.notifyDataSetChanged();
		    	this.refresh();
			}
		}
	}
	
	@Override
	public void openItem(DataRow row)
    {
		Link link = new Link("pane://x:code=mm_and_stock_check_editor");
		link.Parameters.add("order_id", row.getValue("id", 0L));
		link.Parameters.add("order_code", row.getValue("code", ""));
    	link.Parameters.add("line_id", row.getValue("line_id", 0));
        link.Open(this, this.getContext(), null);
    }
	
	@Override
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
    	DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_stock_check_item, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_stock_check_order = (TextView)convertView.findViewById(R.id.txt_stock_check_order);
        TextView txt_item_code = (TextView)convertView.findViewById(R.id.txt_item_code);
        TextView txt_item_name = (TextView)convertView.findViewById(R.id.txt_item_name);
        TextView txt_quantity = (TextView)convertView.findViewById(R.id.txt_quantity);
        
        num.setText(String.valueOf(position + 1));
        txt_stock_check_order.setText(row.getValue("code", "") + ", " + row.getValue("plan_date", ""));
        txt_item_code.setText(row.getValue("warehouse_code", "") + ", " + row.getValue("item_code", "") + ", " + row.getValue("onhand_quantity", "") + row.getValue("uom_code", ""));
        txt_item_name.setText(row.getValue("item_name", ""));
        txt_quantity.setText("已盘" + row.getValue("lot_count", "0") + "批次, 共" + row.getValue("check_quantity", "0") + ", 差异"+row.getValue("offset_quantity", "0"));
        
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
    	SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_mm_stock_check_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
}
