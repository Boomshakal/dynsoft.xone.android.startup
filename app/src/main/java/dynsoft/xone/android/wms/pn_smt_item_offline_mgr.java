package dynsoft.xone.android.wms;

import java.io.Serializable;
import android.content.DialogInterface;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import dynsoft.xone.android.activity.LightKanbanActivity;
import dynsoft.xone.android.adapter.TableAdapter;
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
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml.Encoding;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class pn_smt_item_offline_mgr extends pn_editor {

	public pn_smt_item_offline_mgr(Context context) {
		super(context);
	}


	//public ButtonTextCell txt_work_order_code_cell; // 工单编码
	private ArrayList<String> selectedException;
	private ArrayList<String> selectedException2;
	public ButtonTextCell txt_work_code;
	public TextCell txt_item_code1;
	public TextCell txt_item_code2;
	public TextCell txt_item_name;
	public TextCell txt_number;
	public TextCell txt_user_code;
	public TextCell txt_date_time;
	public ListView Matrix;
	String currentTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString();
	public int flage = 1;
	public String item_code1;
	public String item_code2;
	public String work_code;
	public String item_code12;
	public  float item_number;
	public String work_code1;
	public  Long work_id;
	public TableAdapter Adapter;
	public DataTable dataTable;
	public DataRow datarow_head;

	//设置对于的XML文件
	@Override
	public void setContentView() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.pn_smt_item_offline_mgr, this, true);
		view.setLayoutParams(lp);
		selectedException = new ArrayList<String>();
		selectedException2 = new ArrayList<String>();
	}
	
	//  ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>(); 
	//  final String [] nItems = {"卷带","吸取中心偏移","送料不良","少配件","保养","感应灯不亮","抛料"};

	//设置显示控件
	@Override
	public void onPrepared() {


		dataTable = new DataTable();
		work_code = this.Parameters.get("task_order", "");
		item_code1 = this.Parameters.get("item_code", "");
		//plan_quantity = this.Parameters.get("plan_quantity", new BigDecimal(0));
		super.onPrepared();
		this.Matrix = (ListView)this.findViewById(R.id.matrix);
		this.Matrix.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
				putDialog2(i);
				return false;
			}
		});

		if (this.Matrix != null) {
			this.Matrix.setCacheColorHint(Color.TRANSPARENT);

			this.Adapter = new TableAdapter(this.getContext()) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					if (Adapter.DataTable != null) {
						DataRow row = (DataRow) Adapter.getItem(position);

						if (convertView == null) {
							convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_smt_check, null);
							ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
							icon.setImageBitmap(App.Current.ResourceManager.getImage("@/fm_smt_index_16"));
						}

						TextView txt_content = (TextView) convertView.findViewById(R.id.txt_content);
						TextView txt_subject = (TextView) convertView.findViewById(R.id.txt_subject);
						String feedername = "" + row.getValue("item_code2", "") + ", " + String.valueOf(row.getValue("item_name", "0"));
						txt_subject.setText(feedername);
						String back_number = "退：" + String.valueOf(row.getValue("item_number", 0.0));
						txt_content.setText(back_number);

						return convertView;
					}
					return null;
				}
			};
			this.Adapter.DataTable = dataTable;
			this.Matrix.setAdapter(Adapter);
		}
		this.txt_item_code1 = (TextCell) this
				.findViewById(R.id.txt_item_code1_cell);
		
//		this.txt_item_code2 = (TextCell) this
//				.findViewById(R.id.txt_item_code2_cell);
//		this.txt_item_name = (TextCell) this
//				.findViewById(R.id.txt_item_name_cell);
	
		this.txt_date_time = (TextCell) this
				.findViewById(R.id.txt_date_time_cell);
		
		this.txt_user_code = (TextCell) this
				.findViewById(R.id.txt_user_code_cell);
		this.txt_work_code = (ButtonTextCell) this
				.findViewById(R.id.txt_work_code_cell);


		if (this.txt_work_code != null) {
			this.txt_work_code.setLabelText("工单编码");
			this.txt_work_code.setReadOnly();
			this.txt_work_code.Button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					loadComfirmName(txt_work_code);
				}
			});
			if(!TextUtils.isEmpty(work_code)) {
				txt_work_code.setContentText(work_code);
			}

		}
		if (this.txt_item_code1 != null) {
			this.txt_item_code1.setLabelText("成品机型编码");
			if(!TextUtils.isEmpty(item_code1)) {
				txt_item_code1.setContentText(item_code1);
			}

		}

//		if (this.txt_item_code2 != null) {
//			this.txt_item_code2.setLabelText("下线物料编码");
//		}
		if (this.txt_date_time != null) {
			this.txt_date_time.setLabelText("日期");
			this.txt_date_time.setContentText(currentTime);
		}

//		if (this.txt_item_name != null) {
//			this.txt_item_name.setLabelText("物料名称");
//		}

		if (this.txt_user_code != null) {
			this.txt_user_code.setLabelText("操作人");
		}

//		if (this.txt_number != null) {
//			this.txt_number.setLabelText("数量");
//
//		}
	}


	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();
		Log.e("LZH2011",bar_code.substring(0));
		boolean result = bar_code.substring(0).matches("[0-9]+");
//		if (bar_code.startsWith(\"R\")||result ==true ||bar_code.startsWith(\"W")) {
//			txt_item_code.setContentText(bar_code);
//			load_item_name(bar_code);
//		}
	if (bar_code.startsWith("MZ")||bar_code.startsWith("M")||bar_code.startsWith("MA"))
		{
			//扫描工号txt_usernumber_code_cell
			this.txt_user_code.setContentText(bar_code.toString());
		}
		else if(bar_code.startsWith("CRQ:") || bar_code.startsWith("CQR:")) {
		String str = bar_code.substring(4, bar_code.length());
		final String[] arr4 = str.split("-");
		//String task_order_cell = txt_task_order_cell.getContentText();
		System.out.print(arr4[1]);
		if (arr4.length >= 3) {
			final String arr0 = arr4[0];
			final String arr1 = arr4[1];
			final String arr2 = arr4[2];
			item_code2 = arr1;
			//txt_item_code2.setContentText(arr1);
			load_Item_code2(item_code2);
			Log.e("LZH206",item_code2);

			String work_code2 = txt_work_code.getContentText().trim();
			if (work_code2 =="") {
				App.Current.showError(pn_smt_item_offline_mgr.this.getContext(), "请先输入工单");
			}
			else {
				final String[] arr = work_code2.split("-");
				if (arr.length >= 1) {
					final String array = arr[0];
					Log.e("LZH283", array);
					get_work_id(array);
					Log.e("LZH285", String.valueOf(work_id));
				}
				}
				load_item_number2();
			//load_item_number(item_code2);
		} else {
			final String[] arr = bar_code.split("-");
			if (arr.length >= 3) {
				final String arr0 = arr[0];
				final String arr1 = arr[1];
				final String arr3 = arr[2];
				work_code = arr0 + "-" + arr1;
				work_code1 = arr0;
				txt_work_code.setContentText(work_code);
				load_item_code(work_code);
				get_work_id(work_code1);

			}

			App.Current.showError(this.getContext(), "非法条码，请扫描正确的二维码！" + bar_code.toString());
		}
	}
	}
	private void loadComfirmName(ButtonTextCell textcell_1) {
		Link link = new Link("pane://x:code=pn_smt_item_offline_parameter");
		link.Parameters.add("textcell", textcell_1);
		link.Open(null, getContext(), null);
		this.close();
	}
	public void load_item_code(String work_code1){
		String sql = "SELECT * FROM dbo.mm_item WHERE id = (SELECT * FROM dbo.mm_wo_task_order_head WHERE code = ?)";
		Parameters p = new Parameters();
		p.add(1,work_code1);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
			public void handleMessage(Message msg) {
				final Result<DataRow> value = Value;
				if (value.HasError) {
					App.Current.toastError(getContext(), value.Error);
					return;
				}
				else if(value.Value == null){
					App.Current.showError(pn_smt_item_offline_mgr.this.getContext(), "机型编号查询出错");
				}
				else{
					txt_item_code1.setContentText(value.Value.getValue("code",""));
				}
			}
		});

	}
	public  void  get_work_id(String work_code){
		Log.e("LZH253",work_code);
		String sql = "select * from dbo.mm_wo_work_order_head where code = ?";
		Parameters p  =new Parameters().add(1,work_code);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
			@Override
			public void handleMessage(Message msg) {
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_smt_item_offline_mgr.this.getContext(), result.Error);
					return;
				}
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(pn_smt_item_offline_mgr.this.getContext(),"数据空有错1");
					return;
				}
				else {
					Log.e("LZH374",String.valueOf(row.getValue("id",Long.valueOf(0))));
					work_id = row.getValue("id",Long.valueOf(0));
					Log.e("LZH374",String.valueOf(work_id));
				}
			}
		});
	}

	//
	public void load_item_number2() {
		String work_code2 = txt_work_code.getContentText().trim();
		if (work_code2 =="") {
			App.Current.showError(pn_smt_item_offline_mgr.this.getContext(), "请先输入工单");
		}
		else {
			final String[] arr = work_code2.split("-");
			if (arr.length >= 1) {
				final String arr0 = arr[0];
				Log.e("LZH999", arr0);
				String sql = "select * from dbo.mm_wo_work_order_head where code = ?";
				Parameters p = new Parameters().add(1, arr0);
				App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
					@Override
					public void handleMessage(Message msg) {
						Result<DataRow> result = this.Value;
						if (result.HasError) {
							App.Current.showError(pn_smt_item_offline_mgr.this.getContext(), result.Error);
							return;
						}
						DataRow row = result.Value;
						if (row == null) {
							App.Current.showError(pn_smt_item_offline_mgr.this.getContext(), "数据空work_id有错");
							return;
						} else {
							Log.e("LZH374", String.valueOf(row.getValue("id", Long.valueOf(0))));
							work_id = row.getValue("id", Long.valueOf(0));
							Log.e("LZH999", String.valueOf(work_id));
							String sql = "SELECT a.line_id,\n" +
									"\t   a.seq_num,\n" +
									"\t   a.item_id,\n" +
									"\t   b.code AS item_code,\n" +
									"\t   b.name AS item_name,\n" +
									"\t   a.quantity_required,\n" +
									"\t   a.date_required,\n" +
									"\t   a.quantity_per_assembly,\n" +
									"\t   a.quantity_issued,\n" +
									"\t   a.comment,\n" +
									"\t   ISNULL((select t0.quantity \n" +
									"\t       from mm_stock_item t0,mm_warehouse t1 \n" +
									"\t    where t0.warehouse_id=t1.id and t1.organization_id=c.organization_id and t0.item_id=a.item_id AND right(t1.code,2) ='01'),0)\n" +
									"\t    stock_qty,\n" +
									"\t   isnull(a.quantity_required,0)-isnull(a.quantity_issued,0) open_quantity,\n" +
									"\t   isnull(( SELECT SUM(open_quantity) FROM mm_po_transaction WHERE type='PO_RECEIVE' and item_id=a.item_id and organization_id=c.organization_id),0)\n" +
									"\t    rec_op_qty,\n" +
									"\t   isnull(( SELECT SUM(open_quantity) FROM mm_po_transaction WHERE type='PO_ACCEPT' and item_id=a.item_id and organization_id=c.organization_id),0)\n" +
									"\t    acc_op_qty,\n" +
									"\t    d.open_quantity as 未申请数量,\n" +
									"\t    d.closed_quantity as 已申请数量,\n" +
									"\t    case  ISNULL(attribute1,'0')  when 1 then '是' else '否' end  attribute1\n" +
									"FROM dbo.mm_wo_work_order_item a\n" +
									"LEFT JOIN dbo.mm_item b ON b.id=a.item_id\n" +
									"inner join mm_wo_work_order_head c on c.id=a.head_id\n" +
									"left join mm_wo_task_order_item d on d.work_order_id=a.head_id and a.line_id=d.work_order_line_id \n" +
									"left join view_fm_work_bom e on c.item_id =e.assembly_item_id and a.item_id =e.component_item_id\n" +
									"WHERE a.head_id=? AND b.code = ?";
							Parameters p = new Parameters().add(1, work_id).add(2, item_code2);
							Log.e("LZH888", String.valueOf(work_id));
							Log.e("LZH888", String.valueOf(item_code2));
							App.Current.DbPortal.ExecuteRecordAsync(pn_smt_item_offline_mgr.this.Connector, sql, p, new ResultHandler<DataRow>() {
								@Override
								public void handleMessage(Message msg) {
									Result<DataRow> result = this.Value;
									if (result.HasError) {
										App.Current.showError(pn_smt_item_offline_mgr.this.getContext(), result.Error);
										return;
									}
									DataRow row = result.Value;
									if (row == null) {
										App.Current.showError(pn_smt_item_offline_mgr.this.getContext(), "数据空item_number有错");
										return;
									} else {

										float item_number1 = row.getValue("open_quantity", new BigDecimal(0)).floatValue();
										Log.e("LZH999",String.valueOf(item_number1));
										if (item_number1 > 0) {
											item_number1 = 0;
										} else {
											item_number1 = Math.abs(item_number1);
										}
										item_number = item_number1;
										//txt_number.TextBox.setHint(String.valueOf(item_number));
										 DataRow dataRow = new  DataRow();
										//txt_number.setContentText(String.valueOf(item_number));
										dataRow.setValue("item_code2", item_code2);
										dataRow.setValue("item_number", item_number);
										Log.e("LZH430",dataRow.getValue("item_code2",""));
										Log.e("LZH431",String.valueOf(dataRow.getValue("item_number",0)));
										dataTable.Rows.add(dataRow);
										Adapter.notifyDataSetChanged();
									}
								}
							});




						}


					}
				});
			}
		}
	}











	//
	public void load_item_number(String item_code) {

		String work_code2 = txt_work_code.getContentText().trim();
		if (work_code2 =="") {
			App.Current.showError(pn_smt_item_offline_mgr.this.getContext(), "请先输入工单");
		}
		else {
			final String[] arr = work_code2.split("-");
			if (arr.length >= 1) {
				final String arr0 = arr[0];
				Log.e("LZH283", arr0);
				get_work_id(arr0);
				Log.e("LZH285", String.valueOf(work_id));

				String sql = "SELECT a.line_id,\n" +
						"\t   a.seq_num,\n" +
						"\t   a.item_id,\n" +
						"\t   b.code AS item_code,\n" +
						"\t   b.name AS item_name,\n" +
						"\t   a.quantity_required,\n" +
						"\t   a.date_required,\n" +
						"\t   a.quantity_per_assembly,\n" +
						"\t   a.quantity_issued,\n" +
						"\t   a.comment,\n" +
						"\t   ISNULL((select t0.quantity \n" +
						"\t       from mm_stock_item t0,mm_warehouse t1 \n" +
						"\t    where t0.warehouse_id=t1.id and t1.organization_id=c.organization_id and t0.item_id=a.item_id AND right(t1.code,2) ='01'),0)\n" +
						"\t    stock_qty,\n" +
						"\t   isnull(a.quantity_required,0)-isnull(a.quantity_issued,0) open_quantity,\n" +
						"\t   isnull(( SELECT SUM(open_quantity) FROM mm_po_transaction WHERE type='PO_RECEIVE' and item_id=a.item_id and organization_id=c.organization_id),0)\n" +
						"\t    rec_op_qty,\n" +
						"\t   isnull(( SELECT SUM(open_quantity) FROM mm_po_transaction WHERE type='PO_ACCEPT' and item_id=a.item_id and organization_id=c.organization_id),0)\n" +
						"\t    acc_op_qty,\n" +
						"\t    d.open_quantity as 未申请数量,\n" +
						"\t    d.closed_quantity as 已申请数量,\n" +
						"\t    case  ISNULL(attribute1,'0')  when 1 then '是' else '否' end  attribute1\n" +
						"FROM dbo.mm_wo_work_order_item a\n" +
						"LEFT JOIN dbo.mm_item b ON b.id=a.item_id\n" +
						"inner join mm_wo_work_order_head c on c.id=a.head_id\n" +
						"left join mm_wo_task_order_item d on d.work_order_id=a.head_id and a.line_id=d.work_order_line_id \n" +
						"left join view_fm_work_bom e on c.item_id =e.assembly_item_id and a.item_id =e.component_item_id\n" +
						"WHERE a.head_id=? AND b.code = ?";
				Parameters p = new Parameters().add(1, work_id).add(2, item_code);
				Log.e("LZH318", String.valueOf(work_id));
				App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
					@Override
					public void handleMessage(Message msg) {
						Result<DataRow> result = this.Value;
						if (result.HasError) {
							App.Current.showError(pn_smt_item_offline_mgr.this.getContext(), result.Error);
							return;
						}
						DataRow row = result.Value;
						if (row == null) {
							App.Current.showError(pn_smt_item_offline_mgr.this.getContext(), "数据空  有错");
							return;
						} else {

							float item_number1 = row.getValue("open_quantity", new BigDecimal(0)).floatValue();
							if (item_number1 > 0) {
								item_number1 = 0;
							} else {
								item_number1 = Math.abs(item_number1);
							}
							item_number = item_number1;
						}
					}
				});
			}
		}
	}


	public void load_Item_code2(String code)
	{
		this.ProgressDialog.show();

		String sql ="select * from dbo.mm_item WHERE code = ?";
		Parameters p  =new Parameters().add(1, code);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
			public void handleMessage(Message msg) {
				pn_smt_item_offline_mgr.this.ProgressDialog.dismiss();

				Result<DataRow> result = this.Value;
				DataRow row = result.Value;
				if (result.HasError) {
					App.Current.showError(pn_smt_item_offline_mgr.this.getContext(), result.Error);
					return;
				}


				else if (row == null) {
					App.Current.showError(pn_smt_item_offline_mgr.this.getContext(), "找不到该物料名称,请核对此物料编码是否正确");

					//pn_smt_item_offline_mgr.this.Header.setTitleText("找不到该工单对应的信息");
					return;
				}
				else {
//					pn_smt_item_offline_mgr.this.txt_item_name.setContentText(row.getValue("name", ""));

				}

			}
		});
	}
	/////
	//提交操作
	////
	@Override
	public void commit() {
		//将feeder标记成送修状态，并在待维修记录中增加记录



		String chose_case = this.txt_work_code.getContentText().trim();
		if (chose_case == null || chose_case.length() == 0) {
			App.Current.showError(this.getContext(), "工单不能为空");
			return;
		}
		
		String user_code1 = this.txt_user_code.getContentText().trim();
		if (user_code1 == null || user_code1.length() == 0) {
			App.Current.showError(this.getContext(), "操作人不能为空");
			return;
		}
		//String item_name = this.txt_item_name.getContentText().trim();
//		if (item_name == null || item_name.length() == 0) {
//			App.Current.showError(this.getContext(), "测试中心点不能为空！");
//			return;
//		}
		String date_time = this.txt_date_time.getContentText().trim();
//		if (date_time == null || date_time.length() == 0) {
//			App.Current.showError(this.getContext(), "测试A点不能为空！");
//			return;
//		}
		//String number = this.txt_number.getContentText().trim();
		//if (number == null || number.length() == 0) {
			//App.Current.showError(this.getContext(), "数量不能为空");
			//if(Float.valueOf(number)>item_number){
			//	App.Current.showError(this.getContext(), "你输入的数量不对");
			//}
			//return;
		//}
		String is_record = this.txt_item_code1.getContentText().trim();
		if (is_record == null || is_record.length() == 0) {
			App.Current.showError(this.getContext(), "成品机型编码不能为空");
			return;
		}
//		String keep_user = this.txt_item_code2.getContentText().trim();
//		if (keep_user == null || keep_user.length() == 0) {
//			App.Current.showError(this.getContext(), "物料编码不能为空！");
//			return;
//		}
		for (int i = 0; i < dataTable.Rows.size(); i++) {


			String sql = "INSERT INTO mm_item_offline (work_order_id,item_code1,item_code2,quantity,user_code,create_date) VALUES (?,?,?,?,?,?)";
			Parameters p = new Parameters().add(1, work_id).add(2, item_code1).add(3, dataTable.Rows.get(i).getValue("item_code2","")).add(4, dataTable.Rows.get(i).getValue("item_number",0)).add(5, txt_user_code.getContentText()).add(6, currentTime);
			App.Current.DbPortal.ExecuteNonQueryAsync(this.Connector, sql, p, new ResultHandler<Integer>() {
				@Override
				public void handleMessage(Message msg) {
					Result<Integer> result = this.Value;
					if (result.HasError) {
						App.Current.showError(pn_smt_item_offline_mgr.this.getContext(), result.Error);

					} else {
						App.Current.toastInfo(pn_smt_item_offline_mgr.this.getContext(), "物料下线成功");
						clear();
					}
				}
			});


		}

	}




	public void  putDialog2(int i){
		final int a = i;
		final EditText editText = new EditText(pn_smt_item_offline_mgr.this.getContext());
		new AlertDialog.Builder(pn_smt_item_offline_mgr.this.getContext()).setTitle("是否删除").setIcon(android.R.drawable.ic_dialog_info).setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int e) {
						dataTable.Rows.remove(a);
						Adapter.notifyDataSetChanged();
					}
				}).setNegativeButton("取消",null).show();

	}














	
	//打印操作
	public void printLabel() {
		//Map<String, String> parameters = new HashMap<String, String>();
		//parameters.put("item_code", this.Item_code);
		//App.Current.Print("mm_item_identifying_label", "打印物料标识牌", parameters);
	}
	
	//清空数据
	public void clear() {
		
		//this.txt_create_date_cell.setContentText("");
		this.txt_user_code.setContentText("");
		this.txt_work_code.setContentText("");
		this.txt_item_code1.setContentText("");
		//this.txt_number.setContentText("");
		//this.txt_date_time.setContentText("");
		//this.txt_item_name.setContentText("");
		//this.txt_item_code2.setContentText("");
		dataTable = null;
		Adapter.notifyDataSetChanged();
	}

//	private void chooseType() {
//
//
//					final StringBuffer nameMessage = new StringBuffer();
//					final boolean[] selected = new boolean[names.size()];
//					toastChooseDialog(nameMessage, selected, names, txt_result_cell);
//	}







}
