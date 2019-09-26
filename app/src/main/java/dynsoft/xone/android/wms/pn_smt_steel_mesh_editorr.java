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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml.Encoding;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;

public class pn_smt_steel_mesh_editorr extends pn_editor {

	public pn_smt_steel_mesh_editorr(Context context) {
		super(context);
	}


	public ButtonTextCell txt_work_order_code_cell; // 工单编码
	public TextCell txt_item_code_cell; //物料编码
	public TextCell txt_item_name_cell; //物料名称
	public TextCell txt_plan_quantity_cell;//工单数量
	public TextCell txt_steel_mesh_code_cell; //钢网编码
	public TextCell txt_usernumber_code_cell; //用户编码
	public TextCell txt_quantity_cell; //使用次数
	public TextCell txt_accumulative_use_count_cell; //累计使用次数
	public CheckBox chk_is_scrap; //是否报废
	public String steel_mesh_code;
	public TextCell txt_A_cell; //中心点
	public TextCell txt_B_cell; //1点
	public TextCell txt_C_cell; //2点
	public TextCell txt_D_cell; //3点
	public TextCell txt_E_cell; //4点
	public  int is_two_sides=1;
	private int scan_count ;
	private boolean checkbool ;	
	private DataRow _order_row;
	private DataRow _lot_row;
	private Integer _total = 0;
	private Long _rownum;
	private String work_order_code;
	private String task_order_code;
	private String item_code;
	private String item_name;
	private BigDecimal plan_quantity;
	public int flage = 0;

	//设置对于的XML文件
	@Override
	public void setContentView() {
		LayoutParams lp = new LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.pn_smt_steel_mesh_editor, this, true);
		view.setLayoutParams(lp);
	}
	
	//  ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>(); 
	//  final String [] nItems = {"卷带","吸取中心偏移","送料不良","少配件","保养","感应灯不亮","抛料"};

	//设置显示控件
	@Override
	public void onPrepared() {

		super.onPrepared();
		
		 task_order_code = this.Parameters.get("task_order", "");		 
		 item_code = this.Parameters.get("item_code", "");		 
		 item_name = this.Parameters.get("item_name", "");
		 plan_quantity = this.Parameters.get("plan_quantity", new BigDecimal(0));
		 
		 
		scan_count=0;
		checkbool=true;
		this.txt_work_order_code_cell = (ButtonTextCell) this
				.findViewById(R.id.txt_work_order_code_cell);
		
		this.txt_item_code_cell = (TextCell) this
				.findViewById(R.id.txt_item_code_cell);
		
		this.txt_item_name_cell = (TextCell) this
				.findViewById(R.id.txt_item_name_cell);
	
		this.txt_plan_quantity_cell = (TextCell) this
				.findViewById(R.id.txt_plan_quantity_cell);
		
		this.txt_steel_mesh_code_cell = (TextCell) this
				.findViewById(R.id.txt_steel_mesh_code_cell);
		
		this.txt_usernumber_code_cell = (TextCell) this
				.findViewById(R.id.txt_usernumber_code_cell);
		
		this.txt_quantity_cell = (TextCell) this
				.findViewById(R.id.txt_quantity_cell);

		this.txt_accumulative_use_count_cell = (TextCell) this
				.findViewById(R.id.txt_accumulative_use_count_cell);
		
		
		this.txt_A_cell = (TextCell) this
				.findViewById(R.id.txt_A_cell);
		this.txt_B_cell = (TextCell) this
				.findViewById(R.id.txt_B_cell);
		this.txt_C_cell = (TextCell) this
				.findViewById(R.id.txt_C_cell);
		this.txt_D_cell = (TextCell) this
				.findViewById(R.id.txt_D_cell);
		this.txt_E_cell = (TextCell) this
				.findViewById(R.id.txt_E_cell);

		
		this.chk_is_scrap = (CheckBox) this
				.findViewById(R.id.chk_is_scrap);

		if (this.txt_work_order_code_cell != null) {
			this.txt_work_order_code_cell.setLabelText("工单编码");
			this.txt_work_order_code_cell.setReadOnly();
			this.txt_work_order_code_cell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadComfirmName(txt_work_order_code_cell);
                }
            });
			if(!TextUtils.isEmpty(task_order_code)) {
				this.txt_work_order_code_cell.setContentText(task_order_code);
			}
		}
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("物料编码");
			this.txt_item_code_cell.setReadOnly();
			if(!TextUtils.isEmpty(item_code)) {
				txt_item_code_cell.setContentText(item_code);
			}
		}
		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("物料名称");
			this.txt_item_name_cell.setReadOnly();
			if(!TextUtils.isEmpty(item_name)) {
				txt_item_name_cell.setContentText(item_name);
			}
		}

		if (this.txt_plan_quantity_cell != null) {
			this.txt_plan_quantity_cell.setLabelText("工单数量");
			this.txt_plan_quantity_cell.setReadOnly();
			 
			txt_plan_quantity_cell.setContentText(String.valueOf(plan_quantity.floatValue()));
			 
		}

		if (this.txt_steel_mesh_code_cell != null) {
			this.txt_steel_mesh_code_cell.setLabelText("钢网编码");
			//this.txt_steel_mesh_code_cell.setReadOnly();
		}
		
		if (this.txt_accumulative_use_count_cell != null) {
			this.txt_accumulative_use_count_cell.setLabelText("累计次数");
			this.txt_accumulative_use_count_cell.setReadOnly();
		}
		
		if (this.txt_usernumber_code_cell != null) {
			this.txt_usernumber_code_cell.setLabelText("操作人");
			txt_usernumber_code_cell.setContentText("MZ5493");

		}
		    
		if (this.txt_quantity_cell != null) {
			this.txt_quantity_cell.setLabelText("本次次数");
			//this.txt_quantity_cell.setReadOnly();
		}
		
		if (this.txt_A_cell != null) {
			this.txt_A_cell.setLabelText("中心点");		 
		}
		
		if (this.txt_B_cell != null) {
			this.txt_B_cell.setLabelText("测试1点");		 
		}
		
		if (this.txt_C_cell != null) {
			this.txt_C_cell.setLabelText("测试2点");		 
		}
		
		if (this.txt_D_cell != null) {
			this.txt_D_cell.setLabelText("测试3点");		 
		}
		
		if (this.txt_E_cell != null) {
			this.txt_E_cell.setLabelText("测试4点");		 
		}
	}
	private void loadComfirmName(ButtonTextCell textcell_1) {
        Link link = new Link("pane://x:code=pn_smt_steel_mesh_parameter_out_mgr");
        link.Parameters.add("textcell", textcell_1);
        link.Open(null, getContext(), null);
        this.close();
    }
	//扫条码
	@Override
	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();
	      
        if (bar_code.startsWith("WO:"))
        {
             //扫描feeder
        	 this.txt_work_order_code_cell.setContentText(bar_code.toString().split(":")[1]);
        	 work_order_code=bar_code.toString().split(":")[1];
        	 this.loadItem(work_order_code);
        }
        else if (bar_code.startsWith("R"))
        {
             steel_mesh_code = bar_code;
        	 this.txt_steel_mesh_code_cell.setContentText(bar_code.toString());
        	 load_ifmesh(bar_code);
        	loadsteel_mesh(bar_code);
			//is_two_sides();
        }
        else if (bar_code.startsWith("MZ")||bar_code.startsWith("M")||bar_code.startsWith("MA"))
        {
        	//扫描工号txt_usernumber_code_cell
           	 this.txt_usernumber_code_cell.setContentText(bar_code.toString());
        }
	      else
		  {
			  App.Current.showError(this.getContext(), "非法条码，请扫描正确的二维码！"+bar_code.toString());
		  }
	}










//就是用来判断在不在库的
	public void load_ifmesh(String mesh_code )
	{

		//String sql1 ="SELECT count(*) as count FROM  fm_smt_position WHERE task_order=? AND item_code=?";
		String sql1 = "DECLARE @out_time DATETIME\n" +
				"DECLARE @back_time DATETIME\n" +
				"\n" +
				"DECLARE @headid BIGINT\n" +

				"SET @headid = (SELECT id FROM mm_smt_steel_mesh WHERE code = ? AND is_scrap = 0)"+
				"SET @out_time = (SELECT TOP(1) create_time FROM dbo.mm_smt_steel_mesh_usage_log WHERE head_id = @headid AND state = 0 ORDER BY create_time DESC)\n" +
				"SET @back_time = (SELECT TOP(1) create_time FROM dbo.mm_smt_steel_mesh_usage_log WHERE head_id = @headid AND state = 1 ORDER BY create_time DESC)\n" +
				"IF(@out_time is NULL)\n" +
				"BEGIN\n" +
				"RAISERROR ('数据没更新，让管理员进行维护(如果是新钢网可忽视次错误)',16,1)\n" +
				"SELECT flag = 0\n" +

				"END\n" +
				"ELSE IF(@back_time is NULL)\n" +
				"BEGIN\n" +
				"RAISERROR ('数据没更新，让管理员进行维护(如果是新钢网可忽视次错误)',16,1)\n" +
				"SELECT flag = 0\n" +

				"END\n" +
				"\n" +
				"ELSE IF(@back_time >@out_time)\n" +
				"BEGIN\n" +
				"SELECT flag = 1\n" +
				"END\n" +
				"ELSE\n" +
				"BEGIN\n" +
				"SELECT flag = 0\n" +
				"END";
		Parameters p1  =new Parameters().add(1, mesh_code );
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql1, p1, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_smt_steel_mesh_editorr.this.ProgressDialog.dismiss();

				Result<DataRow> result = this.Value;
				DataRow row = result.Value;
				if (result.HasError) {
					App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(), result.Error);

				}

				else if (row == null) {
					App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(), "有毒，肯定不可能报这个错的");
					return;
				}
			    else if (row.getValue("flag", 0)==0)
			    {
					App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(), result.Error);
			    	App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(), "钢网不在库！");
			    	 pn_smt_steel_mesh_editorr.this.txt_steel_mesh_code_cell.setContentText("");
			    }
				else if(row.getValue("flag", 0)==1){
					pn_smt_steel_mesh_editorr.this.txt_steel_mesh_code_cell.setContentText(steel_mesh_code);
				}
			}
		});
		//return count.toString();
	}
	
	
	
	///根据条件带出相应数据信息
	public void loadItem(String code)
	{
		this.ProgressDialog.show();
		
		String sql ="select a.code as  work_order_code, m.code as item_code,m.name as  item_name,CONVERT(VARCHAR(50),CONVERT(decimal(18,0),a.plan_quantity)) as plan_quantity  from mm_wo_work_order_head  a left join mm_item m on a.item_id=m.id where a.code=?";
		Parameters p  =new Parameters().add(1, code);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_smt_steel_mesh_editorr.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				DataRow row = result.Value;
				if (result.HasError) {
					App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(), result.Error);
					return;
				}
				

				else if (row == null) {
					App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(), "找不到该工单对应的信息");
					
					pn_smt_steel_mesh_editorr.this.Header.setTitleText("找不到该工单对应的信息");
					return;
				}
				else {
					pn_smt_steel_mesh_editorr.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
					pn_smt_steel_mesh_editorr.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
					pn_smt_steel_mesh_editorr.this.txt_plan_quantity_cell.setContentText(row.getValue("plan_quantity", ""));
					pn_smt_steel_mesh_editorr.this.txt_quantity_cell.setContentText(row.getValue("plan_quantity", ""));
				}
			}
		});
	}

	///根据条件带出相应数据信息
		public void loadsteel_mesh(String steel_mesh_code)
		{
			this.ProgressDialog.show();
	
			
			
			
			
			
			String sql ="SELECT  TOP 1  CONVERT(varchar(20),CASE WHEN accumulative_use_count IS NULL THEN '0' ELSE accumulative_use_count END) AS accumulative_use_count FROM mm_smt_steel_mesh WHERE code=? AND is_scrap = 0 ";
			Parameters p  =new Parameters().add(1, steel_mesh_code);
			App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
				@Override
	    	    public void handleMessage(Message msg) {
					pn_smt_steel_mesh_editorr.this.ProgressDialog.dismiss();
					
					Result<DataRow> result = this.Value;
					if (result.HasError) {
						App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(), result.Error);
						return;
					}					
					DataRow row = result.Value;
					if (row == null) {
						return;
					}			
					pn_smt_steel_mesh_editorr.this.txt_accumulative_use_count_cell.setContentText(row.getValue("accumulative_use_count", ""));



				}
			});
		}
		

    /////
	//提交操作
	////
	@Override
	public void commit() {
		//将feeder标记成送修状态，并在待维修记录中增加记录
		if(flage ==0) {
			flage = 1;
			Log.e("LZH428", String.valueOf(is_two_sides));
			String work_order_code = this.txt_work_order_code_cell.getContentText().trim();
			if (work_order_code == null || work_order_code.length() == 0) {
				App.Current.showError(this.getContext(), "工单信息不能为空！");
				return;
			}

			String steel_mesh_code1 = this.txt_steel_mesh_code_cell.getContentText().trim();
			if (steel_mesh_code1 == null || steel_mesh_code1.length() == 0) {
				App.Current.showError(this.getContext(), "钢网信息不能为空！");
				return;
			}

			String quantity = this.txt_quantity_cell.getContentText().trim();
			if (quantity == null || quantity.length() == 0) {
				App.Current.showError(this.getContext(), "使用次数信息不能为空！");
				return;
			}

			String item_code = this.txt_item_code_cell.getContentText().trim();
			if (item_code == null || item_code.length() == 0) {
				App.Current.showError(this.getContext(), "工单物料信息不能为空！");
				return;
			}
			String usernumber = this.txt_usernumber_code_cell.getContentText().trim();
			if (usernumber == null || usernumber.length() == 0) {
				App.Current.showError(this.getContext(), "操作员信息不能为空！");
				return;
			}


			String a = this.txt_A_cell.getContentText().trim();
			if (a == null || a.length() == 0) {
				App.Current.showError(this.getContext(), "测试中心点不能为空！");
				return;
			} else if (is_two_sides > 1) {
				boolean result = a.matches("([0-9]+)[,]([0-9]+)");
				if (result == false) {
					App.Current.showError(this.getContext(), "这是双面钢网！，请用,分开写");
				}
			}

			String b = this.txt_B_cell.getContentText().trim();
			if (b == null || b.length() == 0) {
				App.Current.showError(this.getContext(), "测试A点不能为空！");
				return;
			} else if (is_two_sides > 1) {
				boolean result = b.matches("([0-9]+)[,]([0-9]+)");
				if (result == false) {
					App.Current.showError(this.getContext(), "这是双面钢网！，请用,分开写");
				}
			}
			String c = this.txt_C_cell.getContentText().trim();
			if (a == null || c.length() == 0) {
				App.Current.showError(this.getContext(), "测试B点不能为空！");

				return;
			} else if (is_two_sides > 1) {
				boolean result = c.matches("([0-9]+)[,]([0-9]+)");
				if (result == false) {
					App.Current.showError(this.getContext(), "这是双面钢网！，请用,分开写");
				}
			}
			String d = this.txt_D_cell.getContentText().trim();
			if (d == null || d.length() == 0) {
				App.Current.showError(this.getContext(), "测试C点不能为空！");
				return;
			} else if (is_two_sides > 1) {
				boolean result = d.matches("([0-9]+)[,]([0-9]+)");
				if (result == false) {
					App.Current.showError(this.getContext(), "这是双面钢网！，请用,分开写");
				}
			}
			String e = this.txt_E_cell.getContentText().trim();
			if (e == null || e.length() == 0) {
				App.Current.showError(this.getContext(), "测试D点不能为空！");
				return;
			} else if (is_two_sides > 1) {
				boolean result = e.matches("([0-9]+)[,]([0-9]+)");
				if (result == false) {
					App.Current.showError(this.getContext(), "这是双面钢网！，请用,分开写");
				}
			}
			if (chk_is_scrap.isChecked()) {
				App.Current.toastInfo(pn_smt_steel_mesh_editorr.this.getContext(), "钢网报废记录成功");
			} else {
				if (a.matches("[0-9]+")) {
					if (Float.valueOf(d) < 34) {
						App.Current.showError(this.getContext(), "钢网已报废，请先打勾");
						return;
					}
					if (Float.valueOf(e) < 34) {
						App.Current.showError(this.getContext(), "钢网已报废，请先打勾");
						return;
					}
					if (Float.valueOf(c) < 34) {
						App.Current.showError(this.getContext(), "钢网已报废，请先打勾");
						return;
					}
					if (Float.valueOf(a) < 34) {
						App.Current.showError(this.getContext(), "钢网已报废，请先打勾");
						return;
					}
					if (Float.valueOf(b) < 34) {
						App.Current.showError(this.getContext(), "钢网已报废，请先打勾");
						return;
					}
				}

			}
			chk_is_scrap.isChecked();

			String sql = " exec mm_smt_steel_mesh_update ?,?,?,?,?,?,?,?,?,?,?";
			System.out.print(steel_mesh_code1);
			System.out.print(work_order_code);
			final String[] work_order_code1 = work_order_code.split("-");
			final String work_order_code_final = work_order_code1[0];
			System.out.print(item_code);
			Log.e("LZH999", steel_mesh_code1);
			Log.e("LZH999", work_order_code_final);
			Log.e("LZH999", item_code);
			Log.e("LZH999", usernumber);
			Log.e("LZH999", quantity);
			Log.e("LZH999", String.valueOf(chk_is_scrap.isChecked()));
			Log.e("LZH999", a);
			Log.e("LZH999", b);
			Log.e("LZH999", c);
			Log.e("LZH999", d);
			Log.e("LZH999", e);
			System.out.print(quantity);
			System.out.print(sql);
			Parameters p = new Parameters();
			Log.e("LZH", "495");
			Log.e("LZH", work_order_code_final);
			p.add(1, steel_mesh_code1).add(2, work_order_code_final).add(3, item_code).add(4, usernumber).add(5, quantity).add(6, chk_is_scrap.isChecked())
					.add(7, a).add(8, b).add(9, c).add(10, d).add(11, e);
			App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
				@Override
				public void handleMessage(Message msg) {
					pn_smt_steel_mesh_editorr.this.ProgressDialog.dismiss();

					//Result<DataTable> result = this.Value;
					//if (result.HasError) {
					//	App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(),result.Error);
					//	return;
					//}


					App.Current.toastInfo(pn_smt_steel_mesh_editorr.this.getContext(), "钢网使用登记记录成功");
					clear();

				}
			});
		}
		else {
			App.Current.toastInfo(pn_smt_steel_mesh_editorr.this.getContext(), "请不要反复提交");
		}
	}
	
	//打印操作
	public void printLabel() {
		//Map<String, String> parameters = new HashMap<String, String>();
		//parameters.put("item_code", this.Item_code);
		//App.Current.Print("mm_item_identifying_label", "打印物料标识牌", parameters);
	}
	
	//清空数据
	public void clear() {
		
		this.txt_work_order_code_cell.setContentText("");
		this.txt_item_code_cell.setContentText("");		
		this.txt_item_name_cell.setContentText("");			
		this.txt_steel_mesh_code_cell.setContentText("");
		this.txt_plan_quantity_cell.setContentText("");
		this.txt_usernumber_code_cell.setContentText("");
		this.txt_quantity_cell.setContentText("");
		this.txt_accumulative_use_count_cell.setContentText("");
		
		this.txt_A_cell.setContentText("");
		this.txt_B_cell.setContentText("");
		this.txt_C_cell.setContentText("");
		this.txt_D_cell.setContentText("");
		this.txt_E_cell.setContentText("");
		
	}
	//判断是否为双面钢网
//	public void is_two_sides() {
//		String sql = "select count(*) count from mm_smt_steel_mesh_storage_bind where item_code = ?";
//		Parameters p  =new Parameters().add(1,this.txt_steel_mesh_code_cell.getContentText().trim());
//		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
//			public void handleMessage(Message msg) {
//				Result<DataRow> result = this.Value;
//				if (result.HasError) {
//					App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(), result.Error);
//					return;
//				}
//				DataRow row = result.Value;
//				if (row == null||row.getValue("count",0)==0) {
//					App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(),"此钢网没绑定库位？？");
//				}
//				Log.e("LZH579",String.valueOf(row.getValue("count",0)));
//				is_two_sides = row.getValue("count", 0);
//				Log.e("LZH581",String.valueOf(is_two_sides));
//
//
//			}
//		});
//
//}

}
