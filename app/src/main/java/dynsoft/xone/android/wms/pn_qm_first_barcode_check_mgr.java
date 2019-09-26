package dynsoft.xone.android.wms;

import android.content.Context;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class pn_qm_first_barcode_check_mgr extends pn_mgr {

	public pn_qm_first_barcode_check_mgr(Context context) {
		super(context);
	}

	@Override
    public void setContentView()
	{
		LayoutParams lp = new LayoutParams(-1,-1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_ipqc_patrol_record_mgr, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
    public void onPrepared() {
        super.onPrepared();
        
//        this.Matrix.setOnCreateContextMenuListener(new OnCreateContextMenuListener(){
//			@Override
//			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//				menu.add("打印巡检报告...");
//				menu.add("提交巡检报告...");
//			}
//		});
	}
	
	
//	@Override
//	public boolean onContextItemSelected(MenuItem menuItem) {
//		AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuItem.getMenuInfo();
//		DataRow row = this.Adapter.DataTable.Rows.get(info.position);
//
//		if (menuItem.getTitle() == "打印巡检报告...") {
//
//			Map<String, String> parameters = new HashMap<String, String>();
//			parameters.put("id", String.valueOf(row.getValue("id", Long.class)));
//			parameters.put("code", row.getValue("code", ""));
//			parameters.put("type", "发料状态牌");
//
//			App.Current.Print("mm_wo_issue_order", "打印发料状态牌", parameters);
//
//
//		}  else if (menuItem.getTitle() == "提交巡检报告...") {
//
//			final long id = row.getValue("id", Long.class);
//			final String code = row.getValue("code", "");
//
//			String sql = "exec p_qm_ipqc_patrol_record_get_uncommited_items ?,?";
//			Parameters p = new Parameters().add(1, App.Current.UserID).add(2, id);
//			Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(pn_qm_first_barcode_check_mgr.this.Connector, sql, p);
//			if (r.HasError) {
//				App.Current.toastInfo(pn_qm_first_barcode_check_mgr.this.getContext(), r.Error);
//			}
//
//			if (r.Value != null && r.Value.Rows.size() > 0) {
//				App.Current.toastInfo(pn_qm_first_barcode_check_mgr.this.getContext(), "巡检【" + code + "】还有" + String.valueOf(r.Value.Rows.size()) + "条记录没有检验完成，不能提交。");
//			} else {
//
//						String sql0 = "exec p_mm_ipqc_patrol_record_commit ?,?";
//						Parameters p0 = new Parameters().add(1, App.Current.UserID).add(2, id);
//						Result<Integer> r0 = App.Current.DbPortal.ExecuteNonQuery(pn_qm_first_barcode_check_mgr.this.Connector, sql0, p0);
//						if (r.HasError) {
//							App.Current.toastInfo(pn_qm_first_barcode_check_mgr.this.getContext(), r.Error);
//						}
//
//						if (r0.Value > 0) {
//							App.Current.toastInfo(pn_qm_first_barcode_check_mgr.this.getContext(), "提交成功!");
//							pn_qm_first_barcode_check_mgr.this.refresh();
//						}
//
//					}
//
//		}
//
//		return true;
//	}
	
	@Override
    public void openItem(DataRow row)
    {
		Link link = new Link("pane://x:code=qm_first_barcode_check_editor");
		link.Parameters.add("order_id", row.getValue("id", Long.class));
        link.Parameters.add("order_code", row.getValue("code", ""));
        link.Parameters.add("index", 1L);
        link.Parameters.add("head_id", row.getValue("head_id", Integer.class));
        link.Parameters.add("img",  row.getValue("img", new byte[0]));
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
	    	
		}
    }
	
	@Override
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
    	DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_ipqc_patrol_record, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_line1 = (TextView)convertView.findViewById(R.id.txt_line1);
        TextView txt_line2 = (TextView)convertView.findViewById(R.id.txt_line2);
        TextView txt_line3 = (TextView)convertView.findViewById(R.id.txt_line3);
        TextView txt_line4 = (TextView)convertView.findViewById(R.id.txt_line4);
        
        num.setText(String.valueOf(position + 1));
        txt_line1.setText(row.getValue("code", "")+"工单:"+row.getValue("task_order_code", ""));
        txt_line2.setText(row.getValue("create_date", ""));
        txt_line3.setText(" 线别: " + row.getValue("work_line", ""));
        txt_line4.setText(row.getValue("items", ""));
        
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
		SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_qm_first_barcode_check_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
}
