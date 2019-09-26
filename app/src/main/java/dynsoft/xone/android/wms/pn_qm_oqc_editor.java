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

import dynsoft.xone.android.adapter.PageTableAdapter;
import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.Book;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.start.FrmLogin;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class pn_qm_oqc_editor extends pn_editor {

	public pn_qm_oqc_editor(Context context) {
		super(context);
	}

	public TextCell txt_wo_entry_order_cell;
	public TextCell txt_item_code_cell;
	public TextCell txt_item_name_cell;
	public TextCell txt_lot_number_cell;
	public TextCell txt_location_cell;
    public TextCell txt_checker_cell;
	public ButtonTextCell txt_dept_cell;
	public ButtonTextCell txt_prdline_cell;
	public ButtonTextCell txt_shiftday_cell;
	public DecimalCell txt_fqc_qty_cell;
	public DecimalCell txt_reject_qty_cell;
	public ListView Matrix;
	public TableAdapter Adapter;

	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_wo_entry_order_cell = (TextCell)this.findViewById(R.id.txt_wo_entry_order_cell);
		this.txt_item_code_cell = (TextCell)this.findViewById(R.id.txt_item_code_cell);
		this.txt_item_name_cell = (TextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_lot_number_cell = (TextCell)this.findViewById(R.id.txt_lot_number_cell);
		this.txt_dept_cell = (ButtonTextCell)this.findViewById(R.id.txt_dept_cell);
		this.txt_prdline_cell = (ButtonTextCell)this.findViewById(R.id.txt_prdline_cell);
		this.txt_shiftday_cell = (ButtonTextCell)this.findViewById(R.id.txt_shiftday_cell);
		this.txt_location_cell = (TextCell)this.findViewById(R.id.txt_location_cell);
		this.txt_fqc_qty_cell = (DecimalCell)this.findViewById(R.id.txt_fqc_qty_cell);
		this.txt_reject_qty_cell = (DecimalCell)this.findViewById(R.id.txt_reject_qty_cell);
		this.txt_checker_cell=(TextCell)this.findViewById(R.id.txt_checker_cell);
		this.Matrix = (ListView)this.findViewById(R.id.matrix);
		this.CommitButton = (ImageButton)this.findViewById(R.id.pass_commit);
		if (this.CommitButton != null){
			this.CommitButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_white"));
			this.CommitButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_qm_oqc_editor.this.t_commit("PASS");
				}
			});
		}
		this.CommitButton = (ImageButton)this.findViewById(R.id.ng_commit);
		if (this.CommitButton != null){
			this.CommitButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_white"));
			this.CommitButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_qm_oqc_editor.this.t_commit("NG");
				}
			});
		}
		if (this.txt_wo_entry_order_cell != null) {
			this.txt_wo_entry_order_cell.setLabelText("申请编号");
			this.txt_wo_entry_order_cell.setReadOnly();
		}
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("物料编码");
			this.txt_item_code_cell.setReadOnly();
		}
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("物料名称");
			this.txt_item_name_cell.setReadOnly();
		}
		if (this.txt_location_cell != null) {
			this.txt_location_cell.setLabelText("储位");
			this.txt_location_cell.setReadOnly();
		}
		
		if (this.txt_lot_number_cell != null) {
			this.txt_lot_number_cell.setLabelText("批次");
			this.txt_lot_number_cell.setReadOnly();
		}
		
		if (this.txt_dept_cell != null) {
			this.txt_dept_cell.setLabelText("事业部");
			this.txt_dept_cell.Button
			.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					pn_qm_oqc_editor.this.loadbu();
					
				}
			});
		 
		}
		
		if (this.txt_prdline_cell != null) {
			this.txt_prdline_cell.setLabelText("产线");
			this.txt_prdline_cell.Button
			.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					pn_qm_oqc_editor.this.loadprdline();
					
				}
			});
		 
		}
		
		if (this.txt_checker_cell != null) {
			this.txt_checker_cell.setLabelText("检验员");
		}
		
		if (this.txt_shiftday_cell != null) {
			this.txt_shiftday_cell.setLabelText("班次");
			this.txt_shiftday_cell.Button
			.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					pn_qm_oqc_editor.this.loadshiftday();
					
				}
			});
		 
		}
		if (this.txt_fqc_qty_cell != null) {
			this.txt_fqc_qty_cell.setLabelText("抽样数量");
			//this.txt_location_cell.setReadOnly();
		}
		
		if (this.txt_reject_qty_cell != null) {
			this.txt_reject_qty_cell.setLabelText("不合格数");
			//this.txt_lot_number_cell.setReadOnly();
		}

		_order_id = this.Parameters.get("order_id", 0L);
		_order_code = this.Parameters.get("order_code", "");
		_lot_number=this.Parameters.get("lot_number","");
		if (_lot_number!="" && !("").equals(_lot_number) && _lot_number !=null)
		{
		 this.loadOrderItem(_lot_number);  
		}
		
		 
	}
	public void loadprdline() {

		String sql = "SELECT lookup_code FROM core_data_keyword where lookup_type ='SMT_LINE'";
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
							pn_qm_oqc_editor.this.txt_prdline_cell.setContentText(row.getValue(
									"lookup_code", ""));
						}
						dialog.dismiss();
					}
				};
				new AlertDialog.Builder(pn_qm_oqc_editor.this.getContext()).setTitle("选择组织")
				.setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(pn_qm_oqc_editor.this.txt_prdline_cell.getContentText().toString()), listener)
				.setNegativeButton("取消", null).show();
		}
	}
	
	public void loadbu() {

		String sql = "SELECT meaning from dbo.core_data_keyword w WHERE lookup_type='BUSINESS_UNIT'";
		final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql);
		if (result.HasError) {
			App.Current.showError(this.getContext(), result.Error);
			return;
		}
		if (result.Value != null) {
	
				ArrayList<String> names = new ArrayList<String>();
				for (DataRow row : result.Value.Rows) {
					String name = row.getValue("meaning", "");
					names.add(name);
				}

				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which >= 0) {
							DataRow row = result.Value.Rows.get(which);
							pn_qm_oqc_editor.this.txt_dept_cell.setContentText(row.getValue(
									"meaning", ""));
						}
						dialog.dismiss();
					}
				};
				new AlertDialog.Builder(pn_qm_oqc_editor.this.getContext()).setTitle("选择组织")
				.setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(pn_qm_oqc_editor.this.txt_dept_cell.getContentText().toString()), listener)
				.setNegativeButton("取消", null).show();
		}
	}
	public void loadshiftday() {

		String sql = "SELECT meaning from dbo.core_data_keyword w WHERE lookup_type='SHIFT_DAY'";
		final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql);
		if (result.HasError) {
			App.Current.showError(this.getContext(), result.Error);
			return;
		}
		if (result.Value != null) {
	
				ArrayList<String> names = new ArrayList<String>();
				for (DataRow row : result.Value.Rows) {
					String name = row.getValue("meaning", "");
					names.add(name);
				}

				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which >= 0) {
							DataRow row = result.Value.Rows.get(which);
							pn_qm_oqc_editor.this.txt_shiftday_cell.setContentText(row.getValue(
									"meaning", ""));
						}
						dialog.dismiss();
					}
				};
				new AlertDialog.Builder(pn_qm_oqc_editor.this.getContext()).setTitle("选择组织")
				.setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(pn_qm_oqc_editor.this.txt_dept_cell.getContentText().toString()), listener)
				.setNegativeButton("取消", null).show();
		}
	}
	private Long _order_id;
	private String _order_code;
	private DataRow _order_row;
	private String _lot_number;
	
	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_oqc_editor, this, true);
        view.setLayoutParams(lp);
	}

	
	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		
		//扫描批次条码
		if (bar_code.startsWith("CRQ:")) {
			int pos = bar_code.indexOf("-");
			String lot = bar_code.substring(4, pos);
			this.txt_lot_number_cell.setContentText(lot);
			this.loadOrderItem(lot);
		}

		if (bar_code.startsWith("C:")){
			String lot = bar_code.substring(2, bar_code.length());
			this.txt_lot_number_cell.setContentText(lot);
			this.loadOrderItem(lot);
		}
		
		if (bar_code.startsWith("L:")){
			String loc = bar_code.substring(2, bar_code.length());
			String locs = this.txt_location_cell.getContentText().trim();
			if (locs.contains(loc)){
				return;
			}
			
			if (locs.length() > 0){
				locs += ", ";
			}
			this.txt_location_cell.setContentText(locs+loc);
		}
		
		if (bar_code.startsWith("TL:")) {
			String tl = bar_code.substring(3);
			if (tl.length() > 0) {
				this.loadTransferLocation(tl);
				this.txt_location_cell.setContentText(tl);
			}
		}
		
		if (bar_code.startsWith("LC:")) {
			String tl = bar_code.substring(3);
			if (tl.length() > 0) {
				this.loadTransferLocation(tl);
			}
		}
		if (bar_code.startsWith("M"))
		{
			//String man_no = bar_code.substring(2, bar_code.length());
			this.txt_checker_cell.setContentText(bar_code);
		}
	}

	public void loadOrderItem(String lot_number)
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_qm_oqc_get_item ?,?,?";
		Parameters p  =new Parameters().add(1, App.Current.UserID).add(2, _order_id).add(3, lot_number);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_qm_oqc_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError){
					App.Current.showError(pn_qm_oqc_editor.this.getContext(), result.Error);
					return;
				}
				
				pn_qm_oqc_editor.this.showOrderItem(result.Value);
			}
		});
	}
	
	public void loadTransferLocation(String tl)
	{
		String sql = "exec p_qm_oqc_get_item_by_transfer_location ?,?,?";
		Parameters p = new Parameters().add(1, App.Current.UserID).add(2, _order_id).add(3, tl);
		final Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql,p);
		if (r.HasError) {
			App.Current.showError(this.getContext(), r.Error);
		}
		
		if (r.Value != null && r.Value.Rows.size() > 0) {
			if (r.Value.Rows.size() > 1) {
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which >= 0) {
                        	DataRow row = r.Value.Rows.get(which);
                        	pn_qm_oqc_editor.this.showOrderItem(row);
                        }
                        dialog.dismiss();
                    }
                };

                final TableAdapter adapter = new TableAdapter(this.getContext()) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                    	DataRow row = (DataRow)r.Value.Rows.get(position);
                    	if (convertView == null) {
                    		convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_lot_number, null);
                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
                    	}
                        
                        TextView num = (TextView)convertView.findViewById(R.id.num);
                        TextView txt_lot_number = (TextView)convertView.findViewById(R.id.txt_lot_number);
                        TextView txt_quantity = (TextView)convertView.findViewById(R.id.txt_quantity);
                        
                        num.setText(String.valueOf(position + 1));
                        String lot_number = row.getValue("lot_number", "");
                        String qty =row.getValue("code", "")+","+ row.getValue("date_code", "") + ", " + App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##") + " " + row.getValue("uom_code", "");
                        
                        txt_lot_number.setText(lot_number);
                        txt_quantity.setText(qty);

                        return convertView;
                    }
                };
                
                adapter.DataTable = r.Value;
                adapter.notifyDataSetChanged();
                
                new AlertDialog.Builder(this.getContext())
                .setTitle("选择入库批次")
                .setSingleChoiceItems(adapter, 0, listener)
                .setNegativeButton("取消", null).show();
			} else {
				DataRow row = r.Value.Rows.get(0);
				this.showOrderItem(row);
			}
		}
	}
	
	private void showOrderItem(DataRow row)
	{
		_order_row = row;
		if (_order_row == null) {
			App.Current.showError(pn_qm_oqc_editor.this.getContext(), "待入库数据没有该批号。");
			return;
		}
		
		int total = _order_row.getValue("lines_count", 0);
		if (total > 0) {
			String info = "("+ String.valueOf(total) +"条待检验)";
			pn_qm_oqc_editor.this.Header.setTitleText(info);
		} else {
			pn_qm_oqc_editor.this.Header.setTitleText("成品检验");
		}
		_lot_number=_order_row.getValue("lot_number", "");
		pn_qm_oqc_editor.this.txt_wo_entry_order_cell.setTag(_order_row);
		pn_qm_oqc_editor.this.txt_wo_entry_order_cell.setContentText(_order_row.getValue("code", "")+","+_order_row.getValue("organization_code", ""));
		pn_qm_oqc_editor.this.txt_item_code_cell.setContentText(_order_row.getValue("item_code", "")+"送检数:"+App.formatNumber(_order_row.getValue("quantity", BigDecimal.ZERO), "0.##"));
		pn_qm_oqc_editor.this.txt_item_name_cell.setContentText(_order_row.getValue("item_name", ""));
		pn_qm_oqc_editor.this.txt_lot_number_cell.setContentText(_order_row.getValue("lot_number", "")+","+_order_row.getValue("date_code", ""));
		pn_qm_oqc_editor.this.txt_prdline_cell.setContentText(_order_row.getValue("prdline", ""));
		pn_qm_oqc_editor.this.txt_location_cell.setContentText(_order_row.getValue("locations", ""));
		pn_qm_oqc_editor.this.txt_fqc_qty_cell.setContentText(App.formatNumber(_order_row.getValue("sampling_quantity", BigDecimal.ZERO), "0.##"));
	}
	
	//@Override
	public void t_commit(String flag)
	{
		if (_order_row == null) {
			App.Current.showError(this.getContext(), "没有工单缴库申请，不能提交。");
			return;
		}
		
		final Long order_id = _order_row.getValue("id", Long.class);
		//final String order_code = _order_row.getValue("code", "");
		final Integer order_line_id = _order_row.getValue("line_id", Integer.class);
		final String fqc_qty =this.txt_fqc_qty_cell.getContentText();
		final String reject_qty=this.txt_reject_qty_cell.getContentText();
		final String prdline =this.txt_prdline_cell.getContentText();
		final String dept =this.txt_dept_cell.getContentText();
		final String shiftday =this.txt_shiftday_cell.getContentText();
		final String checker=this.txt_checker_cell.getContentText();
		BigDecimal q1=_order_row.getValue("quantity", BigDecimal.ZERO);
		final String flg =flag;
		if (prdline==null || prdline.length()==0)
		{
			App.Current.showError(this.getContext(), "未输入线别，不能提交。");
			return;	
		}
		if (dept==null || dept.length()==0)
		{
			App.Current.showError(this.getContext(), "未输入事业部，不能提交。");
			return;	
		}
		if  (fqc_qty==null || fqc_qty.length()==0)
		{
			App.Current.showError(this.getContext(), "没有输入抽样数量，不能提交。");
			return;
		}
		if (checker==null ||checker.length()==0){
			App.Current.showError(this.getContext(), "没有扫描检验员工卡，不能提交。");
			return;
		}
		BigDecimal q2=new BigDecimal(fqc_qty);
		if (q2.compareTo(q1)>0)
		{
			App.Current.showError(this.getContext(), "抽样数量大于送检数量，不能提交。");
			return; 
		}
		if (flag=="NG" &&  (reject_qty==null || reject_qty.length()==0))
		{
			App.Current.showError(this.getContext(), "您点击的是不合格按钮，未填写不合格数量，不能提交。");
			return;
		}
		if (flag=="PASS" &&  (reject_qty!=null && reject_qty.length()!=0))
		{
			App.Current.showError(this.getContext(), "您点击的是合格按钮，填写不合格数量，不能提交。");
			return;
		}
		String Confirmmessage="";
		if (flag=="PASS")
		{
			Confirmmessage="点击的是合格按钮，您确定此批次检验合格吗?";
		}else
		{
			Confirmmessage="点击的是不合格按钮，您确定此批次检验不合格吗?";
		}
		App.Current.question(this.getContext(), Confirmmessage, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				String qty =String.valueOf(_order_row.getValue("quantity",BigDecimal.ZERO));
				Map<String, String> entry = new HashMap<String, String>();
				entry.put("order_id", "");
				entry.put("task_order_id", String.valueOf(_order_row.getValue("task_order_id",0)));
				entry.put("create_user", App.Current.UserID);
				entry.put("tester_id", App.Current.UserID);
				entry.put("test_quantity", qty);
				entry.put("sample_quantity", fqc_qty);
				entry.put("bad_quantity", reject_qty);
				entry.put("comment", "PDA检验");
				entry.put("line_name", prdline);
				entry.put("department", dept);
				entry.put("reason", "");
				entry.put("improve", "");
				entry.put("checker", checker);
				entry.put("shift_name", shiftday);
				
			
				ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
		
				Map<String, String> line = new HashMap<String, String>();
				line.put("entry_order_id", String.valueOf(order_id));
				line.put("entry_order_line_id", String.valueOf(order_line_id));
				line.put("lot_number", _lot_number);
				line.put("quantity", qty);
				line.put("date_code", _order_row.getValue("date_code",""));
				line.put("locations", _order_row.getValue("locations",""));
				line.put("ok_quantity", qty);
				line.put("ng_quantity", reject_qty);
				line.put("store_keeper", String.valueOf(_order_row.getValue("store_keeper",0)));
				line.put("comment", "");
				entries.add(line);
				
				//生成XML数据，并传给存储过程
				String xml = XmlHelper.createXml("document", entry, "items", "item", entries);
				String sql = "exec p_qm_fqc_create ?,?";
				Connection conn = App.Current.DbPortal.CreateConnection(pn_qm_oqc_editor.this.Connector);
				CallableStatement stmt;
				try {
					stmt = conn.prepareCall(sql);
					stmt.setObject(1, xml);
					stmt.registerOutParameter(2, Types.VARCHAR);
					stmt.execute();
					
					String val = stmt.getString(2);
					if (val != null) {
						Result<String> rs = XmlHelper.parseResult(val);
						if (rs.HasError) {
							App.Current.showError(pn_qm_oqc_editor.this.getContext(), rs.Error);
							return;
						}
						if (flg=="PASS")
						{
//						String rid=rs.Value;
//						String upsql="exec p_qm_fqc_commit ?,?";
//						Parameters p1 = new Parameters().add(1,rid).add(2, App.Current.UserID);
//						App.Current.DbPortal.ExecuteNonQuery(pn_qm_oqc_editor.this.Connector, upsql,p1);
						App.Current.toastInfo(pn_qm_oqc_editor.this.getContext(), "操作完成，请在PC端输入检验结果");
						}else
						{
							App.Current.toastInfo(pn_qm_oqc_editor.this.getContext(), "检验完成，你选择的是NG,请到PC端输入不良报告");	
						}
						
						pn_qm_oqc_editor.this.clearAll();
					}
				} catch (SQLException e) {
					App.Current.showInfo(pn_qm_oqc_editor.this.getContext(), e.getMessage());
					e.printStackTrace();
					pn_qm_oqc_editor.this.clearAll();
				}
			}
		});
	}
	
	public void clearAll()
	{
		pn_qm_oqc_editor.this.txt_wo_entry_order_cell.setContentText("");
		pn_qm_oqc_editor.this.txt_prdline_cell.setContentText("");
		pn_qm_oqc_editor.this.txt_dept_cell.setContentText("");
		pn_qm_oqc_editor.this.txt_item_code_cell.setContentText("");
		pn_qm_oqc_editor.this.txt_item_name_cell.setContentText("");
		//pn_wo_entryoqc_editor.this.txt_lot_number_cell.setContentText("");
		pn_qm_oqc_editor.this.txt_location_cell.setContentText("");
	}
}
