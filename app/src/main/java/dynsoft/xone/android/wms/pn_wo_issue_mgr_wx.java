package dynsoft.xone.android.wms;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
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
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class pn_wo_issue_mgr_wx extends pn_mgr {

	public pn_wo_issue_mgr_wx(Context context) {
		super(context);
	}

	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_wo_issue_mgr, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
    public void onPrepared() {
        super.onPrepared();
        
        this.Matrix.setOnCreateContextMenuListener(new OnCreateContextMenuListener(){
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.add("打印发料状态牌...");
				menu.add("打印生产发料单...");
				menu.add("提交生产发料单...");
			}
		});
	}
	
	private EditText txt_locations;
	
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuItem.getMenuInfo();
		DataRow row = this.Adapter.DataTable.Rows.get(info.position);
		
		if (menuItem.getTitle() == "打印发料状态牌...") {
			
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("id", String.valueOf(row.getValue("id", Long.class)));
			parameters.put("code", row.getValue("code", ""));
			parameters.put("type", "发料状态牌");
			
			App.Current.Print("mm_wo_issue_order", "打印发料状态牌", parameters);
			
//			Intent intent = new Intent();
//	        intent.setClass(this.getContext(), frm_wo_issue_order_printer.class);
//	        intent.putExtra("id", row.getValue("id", Long.class));
//	        intent.putExtra("code", row.getValue("code", ""));
//	        intent.putExtra("type", "发料状态牌");
//	        this.getContext().startActivity(intent);
			
		} else if (menuItem.getTitle() == "打印生产发料单...") {
			
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("id", String.valueOf(row.getValue("id", Long.class)));
			parameters.put("code", row.getValue("code", ""));
			parameters.put("type", "生产发料单");
			
			App.Current.Print("mm_wo_issue_order", "打印生产发料单", parameters);
			
//			Intent intent = new Intent();
//	        intent.setClass(this.getContext(), frm_wo_issue_order_printer.class);
//	        intent.putExtra("id", row.getValue("id", Long.class));
//	        intent.putExtra("code", row.getValue("code", ""));
//	        intent.putExtra("type", "生产发料单");
//	        this.getContext().startActivity(intent);
			
		} else if (menuItem.getTitle() == "提交生产发料单...") {
			
			final long id = row.getValue("id", Long.class);
			final String code = row.getValue("code", "");
			
			String sql = "exec p_mm_wo_issue_get_uncommited_items ?,?";
			Parameters p = new Parameters().add(1, App.Current.UserID).add(2, id);
			Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(pn_wo_issue_mgr_wx.this.Connector, sql, p);
			if (r.HasError) {
				App.Current.toastInfo(pn_wo_issue_mgr_wx.this.getContext(), r.Error);
			}
			
			if (r.Value != null && r.Value.Rows.size() > 0) {
				App.Current.toastInfo(pn_wo_issue_mgr_wx.this.getContext(), "发料申请【" + code + "】还有" + String.valueOf(r.Value.Rows.size()) + "条记录没有发完，不能提交。");
			} else {
				this.txt_locations = new EditText(pn_wo_issue_mgr_wx.this.getContext());
				new AlertDialog.Builder(pn_wo_issue_mgr_wx.this.getContext())
				.setTitle("请扫描储位")
				.setView(this.txt_locations)
				
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String locations = txt_locations.getText().toString().trim();
						if (locations.length() > 0) {
							String sql = "exec p_mm_wo_issue_commit ?,?,?";
							Parameters p = new Parameters().add(1, App.Current.UserID).add(2, id).add(3, locations);
							Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(pn_wo_issue_mgr_wx.this.Connector, sql, p);
							if (r.HasError) {
								App.Current.toastInfo(pn_wo_issue_mgr_wx.this.getContext(), r.Error);
							}
							
							if (r.Value > 0) {
								App.Current.toastInfo(pn_wo_issue_mgr_wx.this.getContext(), "提交成功!");
								pn_wo_issue_mgr_wx.this.refresh();
							}
							txt_locations.setText("");
						} else {
							App.Current.showInfo(pn_wo_issue_mgr_wx.this.getContext(), "请先扫描或输入储位。");
						}
					}
				}).setNegativeButton("取消", null)
				.show();
			}
		}
		
		return true;
	}
	
	@Override
    public void openItem(DataRow row)
    {
		Link link = new Link("pane://x:code=mm_and_wo_issue_editor_wx");
		link.Parameters.add("order_id", row.getValue("id", Long.class));
        link.Parameters.add("order_code", row.getValue("code", ""));
        link.Open(this, this.getContext(), null);
    }
	
	@Override
	public void onScan(final String barcode)
	{
		String txt = barcode;
		if (barcode.startsWith("CRQ:")) {
			String str = barcode.substring(4, barcode.length());
			String[] arr = str.split("-");
			if (arr.length > 0) {
				txt = arr[0];
			}
			
			this.SearchBox.setText(txt);
	    	this.Adapter.DataTable = null;
	    	this.Adapter.notifyDataSetChanged();
	    	this.refresh();
	    	
		} else if (barcode.startsWith("L:")){
			if (this.txt_locations != null) {
				String loc = barcode.substring(2, barcode.length());
				String locs = this.txt_locations.getText().toString().trim();
				if (locs.contains(loc)) {
					return;
				}
				
				if (locs.length() > 0){
					locs += ", ";
				}
				
				this.txt_locations.setText(locs+loc);
			}
		}
    }
	
	@Override
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
    	DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_wo_issue, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_wo_issue_order_code = (TextView)convertView.findViewById(R.id.txt_wo_issue_order_code);
        TextView txt_create_time = (TextView)convertView.findViewById(R.id.txt_create_time);
        TextView txt_status = (TextView)convertView.findViewById(R.id.txt_status);
        TextView txt_items = (TextView)convertView.findViewById(R.id.txt_items);
        
        num.setText(String.valueOf(position + 1));
        txt_wo_issue_order_code.setText(row.getValue("code", ""));
        txt_create_time.setText(row.getValue("create_time", ""));
        txt_status.setText("优先级: " + row.getValue("priority", 0).toString() + ", 状态: " + row.getValue("status", ""));
        txt_items.setText(row.getValue("items", ""));
        
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
		SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_mm_wo_issue_get_order_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
}
