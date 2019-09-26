package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;

public class pn_wo_entry_order_mgr extends pn_mgr {

	public pn_wo_entry_order_mgr(Context context) {
		super(context);
	}
     
	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_wo_entry_order_mgr, this, true);
        view.setLayoutParams(lp);
	}
	private EditText txt_lot_qty;
	@Override
    public void onPrepared() {
        super.onPrepared();
        this.Matrix.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long index) {
				if (Adapter.DataTable != null && Adapter.DataTable.Rows.size() > index) {
					final DataRow row = (DataRow)Adapter.getItem((int)index);
			        App.Current.question(pn_wo_entry_order_mgr.this.getContext(), "确定要将该生产任务单所有扫描批次提交送检吗？", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//pn_wo_entry_order_mgr.this.commit(row);
							pn_wo_entry_order_mgr.this.txt_lot_qty = new EditText(pn_wo_entry_order_mgr.this.getContext());
							new AlertDialog.Builder(pn_wo_entry_order_mgr.this.getContext())
							.setTitle("请输入提交批数（箱数）")
							.setView(pn_wo_entry_order_mgr.this.txt_lot_qty)
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									pn_wo_entry_order_mgr.this.commit(row);
								 }
							}).setNegativeButton("取消", null)
							.show();
						
						}
      				});
				}
				return false;
			}
        });
	}

	@Override
	public void create()
	{
		Link link = new Link("pane://x:code=mm_and_wo_entry_order_editor");
        link.Open(this, this.getContext(), null);
	}
    
	@Override
	public void onScan(final String barcode)
	{
		String txt = barcode;
    	this.SearchBox.setText(txt);
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
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_wo_entry_order, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_work_code = (TextView)convertView.findViewById(R.id.txt_work_code);
        TextView txt_factory_name = (TextView)convertView.findViewById(R.id.txt_factory_name);
        TextView txt_item_code = (TextView)convertView.findViewById(R.id.txt_item_code);
        TextView txt_item_name = (TextView)convertView.findViewById(R.id.txt_item_name);
        TextView txt_quantity = (TextView)convertView.findViewById(R.id.txt_quantity);
        TextView txt_location = (TextView)convertView.findViewById(R.id.txt_location);

        
        num.setText(String.valueOf(position + 1));
        txt_work_code.setText(row.getValue("work_code", "") + ", " + row.getValue("iqc_code", ""));
        txt_factory_name.setText(row.getValue("factory_name", ""));
        txt_item_code.setText(row.getValue("item_code", "") + "，" + row.getValue("lot_number", ""));
        txt_item_name.setText(row.getValue("item_name", ""));
        String qty = row.getValue("org_code", "") + ", " + row.getValue("warehouse_code", "") + ", " + row.getValue("date_code", "") + ", " + App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##") + " " + row.getValue("uom_code", "") + ",草稿 ";
        txt_quantity.setText(qty);
        txt_location.setText(row.getValue("locations", ""));
        return convertView;
    }
	
	
	private EditText txt_split_qty;
	
	private String _split_qty;
	
	public void ShowInputQty(final DataRow row)
	{

    	this.txt_split_qty = new EditText(this.getContext());
    	final BigDecimal totalqty =row.getValue("quantity",BigDecimal.ZERO);
    	txt_split_qty.setText(App.formatNumber(row.getValue("quantity",BigDecimal.ZERO),"0.###"));
		new AlertDialog.Builder(this.getContext())
		.setTitle("请输入数量")
		.setView(this.txt_split_qty)
		//.setText()
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//txt_split_qty.setTe
				String split_qty = txt_split_qty.getText().toString().trim();
				if (split_qty.length() > 0) {
				BigDecimal put_qty =new BigDecimal(split_qty);
				if (put_qty.compareTo(totalqty)<=0)
				  {
					_split_qty =split_qty;
					pn_wo_entry_order_mgr.this.printLotLable(row);
				  } else
				  {
					  App.Current.toastError(pn_wo_entry_order_mgr.this.getContext(), "请输入小于批次数量的数量！");
				  }
	
				}
				else
				{
					App.Current.showInfo(pn_wo_entry_order_mgr.this.getContext(), "请先输入打印的数量。");
				}
			}
		}).setNegativeButton("取消", null)
		.show();
		 

	}
	
	@Override
    public void openItem(DataRow row)
    {
		
		ShowInputQty(row);
		
    }
	
	public  void printLotLable(DataRow row)
	{
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("org_code", row.getValue("org_code", ""));
		parameters.put("item_code", row.getValue("item_code", ""));
		parameters.put("vendor_model", row.getValue("vendor_model", ""));
		parameters.put("lot_number", row.getValue("lot_number", ""));
		parameters.put("vendor_lot", row.getValue("work_code", ""));
		parameters.put("date_code", row.getValue("date_code", ""));
		parameters.put("quantity", _split_qty);
		parameters.put("ut", row.getValue("ut", ""));
		parameters.put("pack_sn_no", row.getValue("pack_sn_no", ""));
		App.Current.Print("mm_prod_lot_label", "打印产品包装标签", parameters);
		;
	}

	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
    	SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_mm_wo_entry_order_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
	public void commit(DataRow row)
	{
		if (row == null) return;
		String sqlstr = "exec p_mm_wo_entry_order_pda_create ?,?,?,?,?,?,?";
		Connection conn = App.Current.DbPortal.CreateConnection(pn_wo_entry_order_mgr.this.Connector);
		CallableStatement stmt;
		try {
			stmt = conn.prepareCall(sqlstr);
			stmt.setObject(1, row.getValue("task_order_id", 0L));
			stmt.setObject(2, App.Current.UserID);
			stmt.setObject(3, App.Current.BranchID);
			stmt.setObject(4, 1);
			stmt.setObject(5, "");
			stmt.setObject(6, this.txt_lot_qty.getText().toString().trim());
			stmt.registerOutParameter(7, Types.VARCHAR);
			stmt.execute();
			String val = stmt.getString(7);
			if (val != null) {
				Result<String> rs = XmlHelper.parseResult(val);
				if (rs.HasError) {
					App.Current.showError(pn_wo_entry_order_mgr.this.getContext(), rs.Error);
					return;
				}
				App.Current.toastInfo(pn_wo_entry_order_mgr.this.getContext(), "提交成功");
				pn_wo_entry_order_mgr.this.refresh();
			}
		} catch (SQLException e) {
			App.Current.showInfo(pn_wo_entry_order_mgr.this.getContext(), e.getMessage());
			e.printStackTrace();
			pn_wo_entry_order_mgr.this.refresh();
		}
	}
}
