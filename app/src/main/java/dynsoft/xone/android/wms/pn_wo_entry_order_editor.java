package dynsoft.xone.android.wms;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.SwitchCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.PromptCallback;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.Book;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.start.FrmLogin;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Xml.Encoding;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

public class pn_wo_entry_order_editor extends pn_editor {

	public pn_wo_entry_order_editor(Context context) {
		super(context);
	}

	public ButtonTextCell task_work_code_cell; //工单号
	public TextCell txt_item_code_cell; // 产品编码
	public TextCell txt_item_name_cell; //机型
	public TextCell txt_factory_name_cell;//加工厂
	public TextCell txt_org_code_cell;//组织
	public TextCell txt_remark_cell ;//备注
	public DecimalCell txt_quantity_cell;//缴库数量
	public TextCell txt_date_code_cell; //周期
	public TextCell txt_location_cell; // 储位
	public SwitchCell chk_commit_print;//打印批次号
	public TextCell txt_warehouse_cell;//缴库库位
	public TextCell txt_snno_cell;//产品序列号
	public BigDecimal temp_aval_qty;
	public BigDecimal total_qty;
	public String lot_number;
	public String line_name;
	

	@Override
	public void setContentView() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.pn_wo_entry_order_editor, this, true);
		view.setLayoutParams(lp);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		this.txt_quantity_cell
				.onActivityResult(requestCode, resultCode, intent);
	}
  
	@Override
	public void onPrepared() {

		super.onPrepared();

		this.txt_item_code_cell = (TextCell) this
				.findViewById(R.id.txt_item_code_cell);
		this.task_work_code_cell = (ButtonTextCell) this
				.findViewById(R.id.task_work_code_cell);
		this.txt_item_name_cell = (TextCell) this
				.findViewById(R.id.txt_item_name_cell);
		this.txt_factory_name_cell = (TextCell) this
				.findViewById(R.id.txt_factory_name_cell);
		this.txt_org_code_cell = (TextCell) this
				.findViewById(R.id.txt_org_code_cell);
		this.txt_quantity_cell = (DecimalCell) this
				.findViewById(R.id.txt_quantity_cell);
		this.txt_date_code_cell = (TextCell) this
				.findViewById(R.id.txt_date_code_cell);
		this.txt_location_cell = (TextCell) this
				.findViewById(R.id.txt_location_cell);
		this.txt_remark_cell = (TextCell) this
				.findViewById(R.id.txt_remark_cell);
		this.txt_snno_cell = (TextCell) this
				.findViewById(R.id.txt_snno_cell);
		this.txt_warehouse_cell =(TextCell) this.findViewById(R.id.txt_warehouse_cell);
		this.chk_commit_print=(SwitchCell)this.findViewById(R.id.chk_commit_print);
		if (this.task_work_code_cell != null) {
			this.task_work_code_cell.setLabelText("任务单号");
			this.task_work_code_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_right_light"));
			this.task_work_code_cell.Button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					String code = task_work_code_cell.getContentText().trim();
					if(code!=null && ("").equals(code) == false) {
						loadworkdtl(code);
				    }
				}
			});
		}
		this.CommitButton = (ImageButton)this.findViewById(R.id.print_commit);
		if (this.CommitButton != null){
			this.CommitButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_print_white"));
			this.CommitButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_wo_entry_order_editor.this.printtaskbill();
				}
			});
		}

		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("物料编码");
			//this.txt_item_code_cell.setContentText("R");
			this.txt_item_code_cell.setReadOnly();
		}
		if (this.txt_snno_cell != null) {
			this.txt_snno_cell.setLabelText("产品SN");
			this.txt_snno_cell.setReadOnly();
		}
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("机型名称");
			this.txt_item_name_cell.setReadOnly();
		}
		if (this.txt_warehouse_cell != null) {
			this.txt_warehouse_cell.setLabelText("库位");
		}
		if (this.txt_factory_name_cell != null) {
			this.txt_factory_name_cell.setLabelText("工厂名称");
			 this.txt_factory_name_cell.setReadOnly();
		}
		if (this.txt_org_code_cell != null) {
			this.txt_org_code_cell.setLabelText("组织");
			this.txt_org_code_cell.setReadOnly();
			//this.txt_org_code_cell.setContentText("E");
	
		}
		if (this.txt_remark_cell != null) {
			this.txt_remark_cell.setLabelText("投产数量");
			this.txt_remark_cell.setReadOnly();
		}
		if (this.txt_quantity_cell != null) {
			this.txt_quantity_cell.setLabelText("缴库数量");
			this.txt_quantity_cell.TextBox.addTextChangedListener(new TextWatcher(){
				@Override
				public void afterTextChanged(Editable s) {
				String str = s.toString();
				if(str==null || str=="" || ("").equals(str))
				{
					str="0";
				}
					String rem=pn_wo_entry_order_editor.this.txt_remark_cell.getContentText();
					if (rem !=null && !("").equals(rem) && rem!="" ) {
					//String[] qt=rem.split("/");
					BigDecimal q1 =new BigDecimal(str);
					BigDecimal q2 =temp_aval_qty;
					if (q1.compareTo(q2)>0){
						App.Current.showError(pn_wo_entry_order_editor.this.getContext(), "数量已超过可缴库数量！"); 	
						pn_wo_entry_order_editor.this.txt_quantity_cell.setContentText("");
						return;
					} else
					{
				     BigDecimal q =q2.subtract(q1);
					 pn_wo_entry_order_editor.this.txt_remark_cell.setContentText("共："+App.formatNumber(total_qty,"0.##")+" PCS/未缴："+App.formatNumber(q,"0.##")+" PCS"); 
					}
					}
				 
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,int count, int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start,int before, int count) {
				}
			});
		}

		if (this.txt_date_code_cell != null) {
			this.txt_date_code_cell.setLabelText("D/C");
			TimeZone zone=TimeZone.getTimeZone("Asia/Shanghai");
			//GregorianCalendar g = new GregorianCalendar(); 
			Calendar cal = Calendar.getInstance(zone);
			int c = cal.get(Calendar.WEEK_OF_YEAR);
			int  y=cal.get(Calendar.YEAR);
			
			 DecimalFormat df=new DecimalFormat("00");
		     String week=df.format(c);
			 this.txt_date_code_cell.setContentText(String.valueOf(y).substring(2,4)+week);
			//System.out.println(c);
			// this.txt_date_code_cell.setReadOnly();
		}
		if (this.chk_commit_print != null) {
			this.chk_commit_print.CheckBox.setTextColor(Color.BLACK);
			this.chk_commit_print.CheckBox.setChecked(false);
			this.chk_commit_print.CheckBox.setText("提交并打印");
		}
		if (this.txt_location_cell != null) {
			this.txt_location_cell.setLabelText("缴库储位");
		}

	}

	@Override
	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();
		// 扫描发运单条码
		if (bar_code.startsWith("QTY:")) {
			String qty = bar_code.substring(4, bar_code.length());
			this.txt_quantity_cell.setContentText(qty);
			return;
		} else if (bar_code.startsWith("Q:")) {
			String qty = bar_code.substring(2, bar_code.length());
			this.txt_quantity_cell.setContentText(qty);
			return;
		} else if (bar_code.startsWith("TL:")) {
			String loc = bar_code.substring(3, bar_code.length());
			this.txt_location_cell.setContentText(loc);
			return;
		} else if (bar_code.startsWith("TO:")){
			this.task_work_code_cell.setContentText(bar_code.substring(3, bar_code.length()));
			loadworkdtl(bar_code.substring(3, bar_code.length()));
		}else if (bar_code.startsWith("TO2:")){
			String tempbar_code =bar_code.substring(4, bar_code.length());
			String wip_no=tempbar_code.split("-")[0];
			lot_number=tempbar_code.split("-")[1];
			String qty =tempbar_code.split("-")[2];
			this.task_work_code_cell.setContentText(wip_no);
			this.txt_quantity_cell.setContentText(qty);
			loadworkdtl(wip_no);
		  }else if (bar_code.startsWith("CRQ:")){
				String tempbar_code =bar_code.substring(4, bar_code.length());
				lot_number=tempbar_code.split("-")[0];
				String wip_no=this.getwip_no(lot_number);
				String qty =tempbar_code.split("-")[2];
				this.task_work_code_cell.setContentText(wip_no);
				this.txt_quantity_cell.setContentText(qty);
				loadworkdtl(wip_no);
			  }else
		  {
			 this.txt_snno_cell.setContentText(barcode);
			 this.txt_quantity_cell.setContentText("1");
		  }
	}
	public String getwip_no(String lot_number){
		String wip_no="";
		String sql="SELECT TOP 1 code  FROM dbo.mm_wo_pack_lot a LEFT JOIN dbo.mm_wo_task_order_head  b ON a.task_order_id =b.id  where a.lot_number=? ";
		Parameters p=new Parameters().add(1, lot_number);
		Result<DataRow> ri = App.Current.DbPortal.ExecuteRecord(this.Connector,
				sql, p);
		if (ri.HasError) {
		  wip_no="";
		}
		if(ri.Value!=null)
		{
		 wip_no =ri.Value.getValue("code",String.class);
		}else
		{
		  wip_no="";
		}
		return wip_no;
	}
	public void loadworkdtl(String txt_work_code) {			
		String sql = "exec p_mm_wo_get_work_dtl ?";
		Parameters p = new Parameters().add(1, txt_work_code);
		Result<DataRow> ri = App.Current.DbPortal.ExecuteRecord(this.Connector,
				sql, p);
		if (ri.HasError) {
			App.Current.showError(this.getContext(), ri.Error);
			this.task_work_code_cell.setContentText("");
			this.txt_location_cell.setContentText("");
			this.txt_item_name_cell.setContentText("");
			this.txt_item_code_cell.setContentText("");
			return;
		}
		
		if(ri.Value!=null)
		{
			if (ri.Value.getValue("aval_quantity",BigDecimal.ZERO).compareTo(BigDecimal.ZERO)==0 )
			{	
				App.Current.showError(this.getContext(), "工单"+txt_work_code+"已经缴库完成！");
				this.task_work_code_cell.setContentText("");
				this.txt_location_cell.setContentText("");
				this.txt_item_name_cell.setContentText("");
				this.txt_item_code_cell.setContentText("");
				this.txt_remark_cell.setContentText("");
				this.txt_factory_name_cell.setContentText("");
				this.txt_warehouse_cell.setContentText("");
				this.txt_org_code_cell.setContentText("");
				return; 
			}
			
			this.txt_org_code_cell.setContentText(ri.Value.getValue("org_code", ""));
			//this.task_work_code_cell.setContentText(ri.Value.getValue("work_order_code", ""));
			this.txt_item_name_cell.setContentText(ri.Value.getValue("item_name", ""));
			this.txt_item_code_cell.setContentText(ri.Value.getValue("item_code", ""));
			this.txt_factory_name_cell.setContentText(ri.Value.getValue("factory_name", ""));
			this.txt_warehouse_cell.setContentText(ri.Value.getValue("warehouse_code", ""));
			total_qty =ri.Value.getValue("plan_quantity", BigDecimal.ZERO);
			temp_aval_qty =ri.Value.getValue("aval_quantity", BigDecimal.ZERO);
			this.txt_remark_cell.setContentText("共："+App.formatNumber(total_qty,"0.##")+" PCS/未缴:"+App.formatNumber(temp_aval_qty,"0.##")+" PCS");	
		} else {
			App.Current.showError(this.getContext(), "找不到编号为"+txt_work_code+"的生产任务。");
		}
	}
    
	
	@Override
	public void commit() {
		
		String sql = "exec p_mm_wo_entry_create_pda  ?,?,?,?,?,?,?,? ";
		String itemnum = this.txt_item_code_cell.getContentText().trim();
		String workcode = this.task_work_code_cell.getContentText().trim();
		String orgcode = this.txt_org_code_cell.getContentText().trim();
		String datecode = this.txt_date_code_cell.getContentText().trim();
		String qty = this.txt_quantity_cell.getContentText().trim(); 
		String locationcode = this.txt_location_cell.getContentText().trim();
		String warcode=this.txt_warehouse_cell.getContentText().trim();
		String sn_no =this.txt_snno_cell.getContentText().trim();
		if (itemnum == null || itemnum.length() == 0) {
			App.Current.showError(this.getContext(), "物料编码为空不能提交！");
			return;
		}
		if (workcode == null || workcode.length() == 0) {
			App.Current.showError(this.getContext(), "生产任务单号不能为空！");
			return;
		}
		if (orgcode == null || orgcode.length() == 0) {
			App.Current.showError(this.getContext(), "组织为空不能提交！");
			return;
		}
		if (datecode == null || datecode.length() == 0) {
			App.Current.showError(this.getContext(), "D/C为空不能提交！");
			return;
		}
		if (qty == null || qty.length() == 0) {
			App.Current.showError(this.getContext(), "数量为空不能提交！");
			return;
		}
		String is_check_items_matched=App.Current.ConfigManager.getValue("mm_wo_system_paras","wo_entry_check_items_matched");
		if (is_check_items_matched=="Y")
		{
		BigDecimal allow_qty =new BigDecimal(0);
		CallableStatement  cstmt=null;
		Connection conn = App.Current.DbPortal.CreateConnection("mgmt_erp_and");
		try
		{
	      cstmt =conn.prepareCall("{?=call meg_cux_package_xq1.get_allow_move_qty(?) }");
	      cstmt.registerOutParameter(2, Types.VARCHAR);
	      cstmt.registerOutParameter(1, Types.NUMERIC);
	      cstmt.setString(2, workcode.split("-")[0]);
	      cstmt.execute();
	      allow_qty=cstmt.getBigDecimal(1);
		}catch (SQLException ex2) {
			App.Current.showError(this.getContext(), "错误消息" + ex2.getMessage());
			ex2.printStackTrace();	
			return;
		} catch (Exception ex2) {
		App.Current.showError(this.getContext(), "错误消息" + ex2.getMessage());
		ex2.printStackTrace();	
		return;
		}
		finally {
			try {

			if (cstmt != null) {
			cstmt.close();
			if (conn != null) {
			conn.close();
			}
			}
			} catch (SQLException e) {
			App.Current.showError(this.getContext(), "错误消息" + e.getMessage());
			e.printStackTrace();
			return;
			}
			}
		BigDecimal q =new BigDecimal(qty);
		if (q.compareTo(allow_qty)>0)
		{
			App.Current.showError(this.getContext(), "缴库数量:"+qty+"不能大于物料齐套数量："+String.valueOf(allow_qty));
			return;	
		}
		}
		if (locationcode == null || locationcode.length() == 0) {
			App.Current.showError(this.getContext(), "储位为空不能提交！");
			return;
		}
		if (warcode == null || warcode.length() == 0) {
			App.Current.showError(this.getContext(), "库位为空不能提交！");
			return;
		}
		Parameters p = new Parameters().add(1, workcode).add(2, qty).add(3, datecode);
		p.add(4, warcode).add(5, locationcode).add(6, App.Current.UserID).add(7, lot_number).add(8, sn_no);
		App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
			@Override
			public void handleMessage(Message msg) {
				pn_wo_entry_order_editor.this.ProgressDialog.dismiss();

				Result<DataTable> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_wo_entry_order_editor.this.getContext(),result.Error);
					return;
				}
				DataTable dt = result.Value;
				App.Current.toastInfo(pn_wo_entry_order_editor.this.getContext(), "提交成功");
				if (pn_wo_entry_order_editor.this.chk_commit_print.CheckBox.isChecked()) {
					if (dt.Rows.size() > 0) {
						//printLabel(dt.Rows.get(0));
						ShowInputQty(dt.Rows.get(0));	
					}
				}
				//pn_begin_stock_editor.this.txt_org_code_cell.setContentText(org_code);
				clear();
			}
		});
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
					pn_wo_entry_order_editor.this.printLabel(row);
				  } else
				  {
					  App.Current.toastError(pn_wo_entry_order_editor.this.getContext(), "请输入小于批次数量的数量！");
				  }
	
				}
				else
				{
					App.Current.showInfo(pn_wo_entry_order_editor.this.getContext(), "请先输入打印的数量。");
				}
			}
		}).setNegativeButton("取消", null)
		.show();
		 

	}
	

	public void printLabel(DataRow row) {
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("org_code", row.getValue("org_code", ""));
		parameters.put("item_code", row.getValue("item_code", ""));
		parameters.put("vendor_model", row.getValue("vendor_model", ""));
		parameters.put("lot_number", row.getValue("lot_number", ""));
		parameters.put("vendor_lot", row.getValue("vendor_lot", ""));
		parameters.put("date_code", row.getValue("date_code", ""));
		parameters.put("quantity", _split_qty);
		parameters.put("ut", row.getValue("ut", ""));
		parameters.put("pack_sn_no", row.getValue("pack_sn_no", ""));
		App.Current.Print("mm_prod_lot_label", "打印产品包装标签", parameters);
		;
		
	}
	public void printtaskbill() {
	
		loadexceptiontype();
	}
	
	public void loadexceptiontype()
	{
		String sql = "SELECT lookup_code FROM core_data_keyword where lookup_type ='SMT_STATUS'";
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
							Map<String, String> parameters = new HashMap<String, String>();
							parameters.put("item_name", pn_wo_entry_order_editor.this.txt_item_name_cell.getContentText());
							parameters.put("item_code", pn_wo_entry_order_editor.this.txt_item_code_cell.getContentText());
							parameters.put("wo_code", pn_wo_entry_order_editor.this.task_work_code_cell.getContentText());
							parameters.put("qty", App.formatNumber(total_qty,"0.##"));
							parameters.put("line_name", line_name);
							parameters.put("smt_status", row.getValue("lookup_code",""));
							parameters.put("smt_qty", pn_wo_entry_order_editor.this.txt_quantity_cell.getContentText().trim());
							parameters.put("zrr_name", pn_wo_entry_order_editor.this.txt_location_cell.getContentText().trim());
							App.Current.Print("mm_win_wo_states_brand", "打印状态牌标签", parameters);
							;
						}
						dialog.dismiss();
					}
				};
				new AlertDialog.Builder(pn_wo_entry_order_editor.this.getContext()).setTitle("选择状态")
				.setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(""), listener)
				.setNegativeButton("取消", null).show();
		}	
	}
	public void clear() {
		this.task_work_code_cell.setContentText("");
		this.txt_location_cell.setContentText("");
		this.txt_item_name_cell.setContentText("");
		this.txt_item_code_cell.setContentText("");
		this.txt_remark_cell.setContentText("");
		this.txt_factory_name_cell.setContentText("");
		this.txt_warehouse_cell.setContentText("");
		this.txt_org_code_cell.setContentText("");
		
	}
}
