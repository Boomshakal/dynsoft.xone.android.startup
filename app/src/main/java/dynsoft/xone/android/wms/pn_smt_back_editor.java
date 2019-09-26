package dynsoft.xone.android.wms;

import dynsoft.xone.android.blueprint.Demo_ad_escActivity;
import dynsoft.xone.android.blueprint.Demo_ad_escActivity2;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.helper.XmlHelper;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import  java.lang.String;
import dynsoft.xone.android.activity.ShortReportKanbanActivity;
import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataSet;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.link.Link;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.*;
import java.text.*;


public class pn_smt_back_editor extends pn_editor {

	public pn_smt_back_editor(Context context) {
		super(context);
	}

	public ButtonTextCell txt_task_order_cell;
	public TextCell txt_prod_name_cell;
	public TextCell txt_prod_cell;
	public TextCell txt_retreating_type;
	public TextCell txt_date;
	public TextCell txt_user_cell;
	public ListView Matrix;
	public TableAdapter Adapter;
	public ImageButton btn_clear;
	public ImageButton btn_list;
	public DataTable dataTable;
	public DataRow datarow_head;
	String currentTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString();
	public String task_order_code;
	public String head_cell;
	//public TextCell txt_store_keeper_name;
	public String plan_date;
	public long task_order_id;
	public  long organization_id;
	public  String org_code;
	public  long branch_id;
	public  String store_keeper_name;
	public long store_keeper;
	public float open_quantity;
	public String item_code;
	public int item_count;
	public  int flaga=0;
	public  int flag1_commit=1;
	@Override

	public void onPrepared() {
	 
		super.onPrepared();
		dataTable = new DataTable();
		Pane[] arr = App.Current.Workbench.GetPanes(this.Code);
		if (arr != null){
			this.Header.setTitleText("余料回退"+String.valueOf(arr.length + 1));
		}

		this.txt_task_order_cell = (ButtonTextCell)this.findViewById(R.id.txt_task_order_cell);
		this.txt_prod_name_cell = (TextCell)this.findViewById(R.id.txt_prod_name_cell);
		this.txt_prod_cell = (TextCell)this.findViewById(R.id.txt_prod_cell);
		this.txt_retreating_type = (TextCell)this.findViewById(R.id.txt_retreating_type);
		this.txt_date = (TextCell)this.findViewById(R.id.txt_date);
		this.txt_user_cell = (TextCell)this.findViewById(R.id.txt_user_cell);
		//this.txt_store_keeper_name = (TextCell)this.findViewById(R.id.txt_store_keeper);
		this.txt_task_order_cell.setContentText(this.Parameters.get("code",""));
		Log.e("LZH111",this.Parameters.get("code",""));


		this.Matrix = (ListView)this.findViewById(R.id.matrix);
		this.btn_clear = (ImageButton)this.findViewById(R.id.btn_clear);
		this.btn_list = (ImageButton)this.findViewById(R.id.btn_list);
		this.Matrix.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
				putDialog2(i);
				return false;
			}
		});






		if (!TextUtils.isEmpty(txt_task_order_cell.getContentText())) {
			String a = txt_task_order_cell.getContentText();
			//txt_task_order_cell.setContentText(task_order_code);
			task_order_code = txt_task_order_cell.getContentText();
			Log.e("LZH99",String.valueOf(a));
			String sql1 = "select id from mm_wo_task_order_head where code = ?";
			Parameters p1 = new Parameters().add(1,a);
			App.Current.DbPortal.ExecuteRecordAsync("core_and", sql1, p1, new ResultHandler<DataRow>() {
				@Override
				public void handleMessage(Message msg) {
					final Result<DataRow> value = Value;
					if (value.HasError) {
						App.Current.toastError(getContext(), value.Error);
						return;
					}

					if (value.Value == null) {
						App.Current.showError(pn_smt_back_editor.this.getContext(), "你扫描的条码有误,获取不到工单ID");
					}
					if (value.Value != null) {
						task_order_id = value.Value.getValue("id", 0L);
						Log.e("luzhihao1",String.valueOf(task_order_id));
						Log.e("luzhihao2",String.valueOf(task_order_id));
						Log.e("LZZZZ","SDSD");
						getcount_item();
						onloaditem(task_order_id);
					}

				}
			});
		}


		if (this.txt_task_order_cell != null) {
			this.txt_task_order_cell.setLabelText("生产任务");
			txt_task_order_cell.Button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					loadComfirmName(txt_task_order_cell);


				}
			});
		}

		if (this.txt_prod_name_cell != null) {
			this.txt_prod_name_cell.setLabelText("产品描述");
			this.txt_prod_name_cell.setReadOnly();
		}
//		if (this.txt_store_keeper_name != null) {
//			this.txt_store_keeper_name.setLabelText("仓管员");
//			this.txt_store_keeper_name.setReadOnly();
//		}
		if (this.txt_prod_cell != null) {
			this.txt_prod_cell.setLabelText("产品编码");
			this.txt_prod_cell.setReadOnly();
		}
		
		if (this.txt_retreating_type != null) {
			this.txt_retreating_type.setLabelText("退料类型");
			this.txt_retreating_type.setContentText("余料回库");
			this.txt_retreating_type.setReadOnly();
		}
		
		if (this.txt_date != null) {
			this.txt_date.setLabelText("操作日期");
			this.txt_date.setContentText(currentTime);
			this.txt_date.setReadOnly();
		}
		if(this.txt_user_cell!=null){
			this.txt_user_cell.setLabelText("操作人员");
			this.txt_user_cell.setContentText(App.Current.UserCode);

		}
		if (this.Matrix != null) {
        	this.Matrix.setCacheColorHint(Color.TRANSPARENT);
			    this.Adapter = new TableAdapter(this.getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (Adapter.DataTable != null) {
                    	DataRow row = (DataRow)Adapter.getItem(position);
                    	
                    	if (convertView == null) {
                    		convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_smt_check, null);
                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/fm_smt_index_16"));
                    	}
                        
                        TextView num = (TextView)convertView.findViewById(R.id.num);
                        TextView txt_feeder = (TextView)convertView.findViewById(R.id.txt_feeder);
                        TextView txt_subject = (TextView) convertView.findViewById(R.id.txt_subject);
                        TextView txt_content = (TextView)convertView.findViewById(R.id.txt_content) ;
                        num.setText(String.valueOf(position + 1));
                        String feeder = ""+String.valueOf(row.getValue("lot_number", 0));
                        txt_feeder.setText(feeder);
						String feedername = ""+row.getValue("item_code", "")+ ", " + String.valueOf(row.getValue("item_name", "0"));
						txt_subject.setText(feedername);
						String back_number = "退："+String.valueOf(row.getValue("back_num", 0))+"              date_code:"+String.valueOf(row.getValue("date_code", 0)) ;
						txt_content.setText(back_number);

                        return convertView;
                    }
                    return null;
                }
            };
            
            this.Adapter.DataTable = dataTable;
            this.Matrix.setAdapter(Adapter);
        }
		

		
		this.btn_clear = (ImageButton)this.findViewById(R.id.btn_clear);
		if (this.btn_clear != null){
			this.btn_clear.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_white"));
			this.btn_clear.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_smt_back_editor.this.Adapter.DataTable.Rows.clear();
					pn_smt_back_editor.this.Adapter.notifyDataSetChanged();
				}
			});
		}
		
		_txt_item_code = new EditText(pn_smt_back_editor.this.getContext());
		_item_dialog = new AlertDialog.Builder(pn_smt_back_editor.this.getContext())
		.setMessage("请扫描物料批次条码")
		.setView(_txt_item_code)

		.setCancelable(false).create();
		
	}


	private void loadComfirmName(ButtonTextCell txt_task_order_cell) {
		Link link = new Link("pane://x:code=qm_back_parameter_mgr");
		link.Parameters.add("txt_task_order_cell", txt_task_order_cell);

		this.txt_task_order_cell.setContentText(this.Parameters.get("code",""));
		//txt_task_order_cell.setContentText(task_order);
		task_order_code = this.Parameters.get("code","");
		Log.e("LZH220",task_order_code);
		link.Open(null, getContext(), null);
		this.close();
	}
	
	private DataRow _program_row;
	private DataRow _feeder_row;
	private AlertDialog _item_dialog;
	private EditText _txt_item_code;
	private  ArrayList<DataRow> _Date_group;
	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_smt_back_editor, this, true);
        view.setLayoutParams(lp);
	}

	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		if(txt_task_order_cell.getContentText()==null){
			App.Current.showError(pn_smt_back_editor.this.getContext(), "请填写生产任务");
		}

		if (bar_code.startsWith("WO:")){
			String task_order = bar_code.substring(3, bar_code.length());

			txt_task_order_cell.setContentText(task_order);
			task_order_code = task_order;
			Log.e("luzhihao0",String.valueOf(task_order_code));
			String sql1 = "select id from mm_wo_task_order_head where code = ?";
			Parameters p1 = new Parameters().add(1,task_order_code);
			App.Current.DbPortal.ExecuteRecordAsync("core_and", sql1, p1, new ResultHandler<DataRow>() {
				@Override
				public void handleMessage(Message msg) {
					final Result<DataRow> value = Value;
					if (value.HasError) {
						App.Current.toastError(getContext(), value.Error);
						return;
					}
//									if(value.Value!=null&&value.Value.Tables.size()>2){
//
//										App.Current.toastError(getContext(), );
//									}
					if (value.Value == null) {
						App.Current.showError(pn_smt_back_editor.this.getContext(), "你扫描的条码有误,获取不到工单ID");
					}
					if (value.Value != null) {
						task_order_id = value.Value.getValue("id", 0L);
						Log.e("luzhihao1",String.valueOf(task_order_id));
						Log.e("luzhihao2",String.valueOf(task_order_id));
						onloaditem(task_order_id);
					}

				}
			});


		} else if (bar_code.startsWith("B") && bar_code.length() > 20) {
			String device = bar_code.substring(20, bar_code.length());
			if (device.length() > 4 && device.startsWith("DEV:")) {
				device = device.substring(4, device.length());

			}
		} else if (bar_code.startsWith("CRQ:") || bar_code.startsWith("CQR:")) {

			String sql = "WITH temp AS (SELECT t0.*,t1.code item_code,t1.name item_name,t1.segment2,t2.code warehouse_code,t3.code vendor_code,t3.name vendor_name,\n" +
					"(t0.quantity_per_assembly*t5.plan_quantity+t0.lost_quantity) required_quantity,t4.quantity_issued wo_issued_quantity,\n" +
					"(SELECT ISNULL(SUM(quantity),0) FROM dbo.mm_stock_item  WHERE item_id=t0.item_id AND warehouse_id=t0.warehouse_id) stock_quantity\n" +
					"\n" +
					"FROM \n" +
					"dbo.mm_wo_task_order_item t0 \n" +
					"INNER JOIN mm_item t1 ON t1.id=t0.item_id\n" +
					"LEFT JOIN dbo.mm_warehouse t2 ON t2.id=t0.warehouse_id\n" +
					"LEFT JOIN dbo.mm_partner t3 ON t3.id=t0.vendor_id\n" +
					"left join dbo.mm_wo_work_order_item t4 on t4.head_id=t0.work_order_id and t4.item_id=t0.item_id and t4.seq_num =t0.seq_num\n" +
					"inner join mm_wo_task_order_head t5 on t5.id=t0.head_id\n" +
					"WHERE t0.head_id=?)\n" +
					"SELECT COUNT(*)  count FROM temp WHERE  wo_issued_quantity>required_quantity";
			Parameters p = new Parameters().add(1,task_order_id);
			App.Current.DbPortal.ExecuteRecordAsync("core_and", sql,p,new ResultHandler<DataRow>() {

				public void handleMessage(Message msg) {
					final Result<DataRow> value = Value;
					if (value.Value==null) {
						App.Current.showInfo(pn_smt_back_editor.this.getContext(), "没有需要回库的物料");
						Log.e("LZH922",String .valueOf(task_order_id));
					}else if(value.HasError){
						App.Current.showError(pn_smt_back_editor.this.getContext(), String.valueOf(value.Error));
					}else {
						//App.Current.showInfo(pn_smt_back_editor.this.getContext(), "错误914行");
						//App.Current.showInfo(pn_smt_back_editor.this.getContext(), "错误914行");
						Log.e("LZH922",String .valueOf(task_order_id));
						Log.e("LZH922",String .valueOf(value.Value.getValue("count",1)));
						item_count = value.Value.getValue("count",0);
						//pn_smt_back_editor.this.txt_store_keeper_name.setContentText(store_keeper_name);
	Log.e("LZH374",String.valueOf(item_count));
		if (item_count == 0) {
				App.Current.showError(pn_smt_back_editor.this.getContext(), "没有要回库的物料");
			} else {


				String str = bar_code.substring(4, bar_code.length());
				final String[] arr = str.split("-");
				//String task_order_cell = txt_task_order_cell.getContentText();
				System.out.print(arr[1]);
				if (arr.length >= 3) {
					final String arr0 = arr[0];
					final String arr1 = arr[1];
					final String arr3 = arr[3];
					item_code = arr1;
					Log.e("lZH", arr0);
					Log.e("lZH", arr1);
					Log.e("lZH", arr3);
					if (task_order_code == null) {
						App.Current.showError(pn_smt_back_editor.this.getContext(), "请先输入工单号");
					} else {
						//String item_code = _feeder_row.getValue("item_code", "")SQL查生产任务中有没这个产品
						String sql1 = "exec p_mm_wo_return_order_get_item ?,?,?";
						Parameters p1 = new Parameters().add(1, task_order_id).add(2, arr[1]).add(3, arr[0]);

						App.Current.DbPortal.ExecuteRecordAsync(pn_smt_back_editor.this.Connector, sql1, p1, new ResultHandler<DataRow>() {

							@Override
							public void handleMessage(Message msg) {
								pn_smt_back_editor.this.ProgressDialog.dismiss();
								int flage = 1;

								Result<DataRow> result = this.Value;
								if (result.HasError) {
									Log.e("luzhihao", arr1);
									App.Current.showError(pn_smt_back_editor.this.getContext(), result.Error);
									App.Current.playSound(R.raw.hook);
									pn_smt_back_editor.this.Adapter.DataTable.Rows.remove(_feeder_row);

									flage = 0;
								}
								else{
									Log.e("LZH8080", String.valueOf(result.Value.getValue("qty_not_issue", new BigDecimal(0)).floatValue()));
									open_quantity = result.Value.getValue("qty_not_issue", new BigDecimal(0)).floatValue();
								}
								String b = "111";
								String a = result.Value.getValue("error", "");
								Log.e("LZH376", result.Value.getValue("error", ""));
								Log.e("LZH376", result.Value.getValue("error", "1111"));
								Log.e("LZH376", b);

								if (a.trim().equals("") && flage == 1) {
									String[] task_order_code1 = task_order_code.split("-");

									String sql = "exec mm_get_del_quantity ?,?";
									Parameters p = new Parameters().add(1, task_order_code).add(2, item_code);
									App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
										public void handleMessage(Message msg) {
											final Result<DataRow> value = Value;
											if (value.Value == null) {
												App.Current.showInfo(pn_smt_back_editor.this.getContext(), "查询退料数量最大值出错");
											} else if (value.HasError) {
												App.Current.showError(pn_smt_back_editor.this.getContext(), String.valueOf(value.Error));
											} else {
												float request;
												float issue;
												request = value.Value.getValue("required_quantity", new BigDecimal(0)).floatValue();
												issue = value.Value.getValue("wo_issued_quantity", new BigDecimal(0)).floatValue();

												Log.e("LZH751", String.valueOf(request));
												Log.e("LZH752", String.valueOf(issue));
												//open_quantity = issue - request;
												for (int i = 0; i < dataTable.Rows.size(); i++) {
													String item_codeofquantity = dataTable.Rows.get(i).getValue("item_code","");
													if(item_codeofquantity.equals(item_code) )
													{
														open_quantity = open_quantity - Float.valueOf(dataTable.Rows.get(i).getValue("back_num","0"));
													}
												}






												if (open_quantity <= 0) {
													open_quantity = 0;
												}
//												if(open_quantity>0) {
//													Log.e("lzh757", String.valueOf(open_quantity));
//													String sql = "exec mm_get_del_quantity_final ?,?";
//													Parameters p = new Parameters().add(1, task_order_id).add(2, item_code);
//													App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
//														public void handleMessage(Message msg) {
//															final Result<DataRow> value = Value;
//															if (value.Value == null) {
//																App.Current.showInfo(pn_smt_back_editor.this.getContext(), "查询退料数量最大值出错");
//															} else if (value.HasError) {
//																App.Current.showError(pn_smt_back_editor.this.getContext(), String.valueOf(value.Error));
//															} else {
//																float request;
//																float issue;
//																request = value.Value.getValue("quantity_issued", new BigDecimal(0)).floatValue();
//																issue = value.Value.getValue("open_quantity", new BigDecimal(0)).floatValue();
//																open_quantity = request -issue;
//
																putDialog(arr1, arr0, arr3);
//															}
//														}
//													});
//												}

											}
										}
									});


									return;
								} else
									App.Current.showError(pn_smt_back_editor.this.getContext(), result.Value.getValue("error", ""));
								return;

							}
						});


					}
				} else
					App.Current.showError(pn_smt_back_editor.this.getContext(), "条码格式为空。");
			}		}

	}
});

		}
		else if (bar_code.startsWith("DEV:")) {
			String device = bar_code.substring(4, bar_code.length());

		} else if (bar_code.startsWith("FD:")||bar_code.startsWith("SSY")||bar_code.startsWith("ZSY") ) {
			if (_feeder_row==null)
			{
				_item_dialog.dismiss();
			}
			if (_item_dialog.isShowing() == false) {
				String feeder = bar_code.substring(3, bar_code.length());
				if (bar_code.startsWith("SSY")||bar_code.startsWith("ZSY"))
				{
					feeder=bar_code;
				}

			}
		} else if (bar_code.startsWith("M")) {
			if (_item_dialog.isShowing() == false) {
				this.txt_user_cell.setContentText(bar_code);
			}
		}

		else
		   {
			App.Current.showError(pn_smt_back_editor.this.getContext(), "无效的条码格式。");
		  }

	}


	public void  putDialog(String a, final String b, String c){

		final EditText editText = new EditText(pn_smt_back_editor.this.getContext());
		final String item_code1 = a;//arr1
		final String lot_numeber = b;//arr0
		final String date_code = c;//arr2
		new AlertDialog.Builder(pn_smt_back_editor.this.getContext()).setTitle("请输入数量("+open_quantity+")").
				setIcon(android.R.drawable.ic_dialog_info).setView(editText).setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						final String i1 = editText.getText().toString();

						if (i1 == "") {
							App.Current.showError(pn_smt_back_editor.this.getContext(), "请输入数量。");
						}
						boolean result = i1.matches("[0-9]+");
						if (result == false) {   //判断是否是数字
							App.Current.showError(pn_smt_back_editor.this.getContext(), "请输入正确的数量。");
						}else if(Float.valueOf(i1)>open_quantity) {
							App.Current.showError(pn_smt_back_editor.this.getContext(), "不能输入数比最大退回数大");

						}
							else
						{
							String sql2 = "exec p_mm_wo_return_order_get_item ?,?,?";
							Parameters p2 = new Parameters().add(1, task_order_id).add(2, item_code1).add(3, lot_numeber);
							Log.e("luzhihao", sql2);
							Log.e("luzhihao", task_order_code);
							Log.e("luzhihao", item_code1);
							Log.e("luzhihao", i1);
							//.ExecuteDataSetAsync
							App.Current.DbPortal.ExecuteRecordAsync("core_and", sql2, p2, new ResultHandler<DataRow>() {
								@Override
								public void handleMessage(Message msg) {
									final Result<DataRow> value = Value;
									if (value.HasError) {
										App.Current.toastError(getContext(), value.Error);
										return;
									}
//									if(value.Value!=null&&value.Value.Tables.size()>2){
//
//										App.Current.toastError(getContext(), );
//									}
									if (value.Value == null) {
										App.Current.showError(pn_smt_back_editor.this.getContext(), "你扫描的条码有误，与工单不对应。");
									}
									if (value.Value != null) {
										head_cell = String.valueOf(value.Value.getValue("task_order_id",0));


										String sql4 = "SELECT t0.*,t1.code item_code,t1.name item_name,t2.code warehouse_code,t3.code vendor_code,\n" +
												"t3.code+' - '+t3.name vendor_name,t4.code+' - '+t4.name store_keeper_name,\n" +
												"(select isnull(quantity,0) from mm_stock_item where item_id=t0.item_id and warehouse_id=t0.warehouse_id) stock_quantity\n" +
												"FROM dbo.mm_wo_return_order_item t0\n" +
												"INNER JOIN mm_item t1 ON t1.id=t0.item_id\n" +
												"INNER JOIN dbo.mm_warehouse t2 ON t2.id=t0.warehouse_id\n" +
												"LEFT JOIN dbo.mm_partner t3 ON t3.id=t0.vendor_id\n" +
												"LEFT JOIN dbo.core_user t4 ON t4.id=t0.store_keeper\n" +
												"where lot_number = ? and task_order_id = ?";
										Parameters p4 = new Parameters().add(1,lot_numeber).add(2,head_cell);
										App.Current.DbPortal.ExecuteRecordAsync("core_and", sql4,p4,new ResultHandler<DataRow>() {
											@Override

											public void handleMessage(Message msg) {
												Result<DataRow> dataRow1 = Value;

												if (dataRow1.Value == null) {
													//App.Current.showError(pn_smt_back_editor.this.getContext(), "数据为空");
												} else if (dataRow1.HasError) {
													App.Current.showError(pn_smt_back_editor.this.getContext(), dataRow1.Error);
												} else {
													store_keeper = dataRow1.Value.getValue("store_keeper", 0);
													Log.e("luzhihao507", String.valueOf(store_keeper));
												}
											}
											});
//										Log.e("luzhihao390",String.valueOf(task_order_id));
//										Log.e("luzhihao391",String.valueOf(lot_numeber));
//
//										App.Current.DbPortal.ExecuteRecordAsync("core_and", sql4,p4,new ResultHandler<DataRow>() {
//											@Override
//
//											public void handleMessage(Message msg) {
//												Result<DataRow> dataRow1 = Value;
//
//												 if(dataRow1.Value==null){
//													App.Current.showError(pn_smt_back_editor.this.getContext(), "数据为空");
//												}
//												else if(dataRow1.HasError){
//													App.Current.showError(pn_smt_back_editor.this.getContext(), dataRow1.Error);
//												}
//
//												else {
													 plan_date = String.valueOf(value.Value.getValue("plan_date",""));
													//pn_smt_back_editor.this.txt_store_keeper_name.setContentText(String.valueOf(value.Value.getValue("store_keeper_name", "")));
													DataRow dataRow = value.Value;
													//String cur_date = value.Value.getValue("cur_date", "");
													dataRow.setValue("lot_number", b);
													dataRow.setValue("date_code", date_code);
													dataRow.setValue("back_num", i1);
													//dataRow.setValue("store_keeper_name", txt_store_keeper_name.getContentText());
													dataRow.setValue("plan_date", "");
													Log.e("luzhihao390",String.valueOf(dataRow.getValue("quantity",0)));

										Intent intent = new Intent();
										intent.setClass(App.Current.Workbench, Demo_ad_escActivity2.class);


										intent.putExtra("org_code", String.valueOf(datarow_head.getValue("org_code","")));
										intent.putExtra("item_code", String.valueOf(dataRow.getValue("item_code","")));
										intent.putExtra("vendor_model", String.valueOf(dataRow.getValue("vendor_model","")));
										intent.putExtra("date_code", String.valueOf(dataRow.getValue("date_code","")));
										intent.putExtra("lot_number",String.valueOf(dataRow.getValue("lot_number","")) );
										intent.putExtra("vendor_lot",String.valueOf(dataRow.getValue("vendor_lot","")) );
										intent.putExtra("quantity",String.valueOf(dataRow.getValue("back_num",0)) );
										intent.putExtra("ut", String.valueOf(dataRow.getValue("uom_code","")));
										intent.putExtra("user",pn_smt_back_editor.this.txt_user_cell.getContentText());
										Log.e("lzh",pn_smt_back_editor.this.txt_user_cell.getContentText());
										intent.putExtra("time",currentTime);
										App.Current.Workbench.startActivity(intent);
										dataTable.Rows.add(dataRow);
													Adapter.notifyDataSetChanged();
									}
								}
							});

						}
					}
				}).setNegativeButton("取消",null).show();

	}

	public void  putDialog2(int i){
		final int a = i;
		final EditText editText = new EditText(pn_smt_back_editor.this.getContext());
		new AlertDialog.Builder(pn_smt_back_editor.this.getContext()).setTitle("是否删除").setIcon(android.R.drawable.ic_dialog_info).setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int e) {
						if(dataTable==null){

						}else {
							dataTable.Rows.remove(a);
							Adapter.notifyDataSetChanged();
						}
					}
				}).setNegativeButton("取消",null).show();

	}
	public void onloaditem(long task_order_id){
		String sql = "     SELECT t0.id,t0.code,t0.priority,t0.organization_id,t0.work_order_id,t6.code work_order_code,t4.code org_code,t0.item_id,t1.code item_code,t1.name item_name,\n" +
				"        t0.plan_issue_date,t0.warehouse_id,t5.code warehouse_code,t0.factory_id,t2.name factory_name,t0.comment,t0.status FROM dbo.mm_wo_task_order_head t0 \n" +
				"       INNER JOIN dbo.mm_item t1 ON t1.id=t0.item_id \n" +
				"        INNER JOIN dbo.mm_partner t2 ON t2.id=t0.factory_id \n" +
				"        INNER JOIN dbo.core_user t3 ON t3.id=t0.create_user \n" +
				"        INNER JOIN dbo.mm_organization t4 ON t4.id=t0.organization_id \n" +
				"       INNER JOIN dbo.mm_warehouse t5 ON t5.id=t0.warehouse_id \n" +
				"        INNER jOIN dbo.mm_wo_work_order_head t6 on t6.id=t0.work_order_id \n" +
				"        where t0.id=?";
		Parameters p = new Parameters().add(1,task_order_id);
		App.Current.DbPortal.ExecuteRecordAsync("core_and", sql,p,new ResultHandler<DataRow>() {
			@Override
			public void handleMessage(Message msg) {
				Result<DataRow> value = Value;
				if (value.HasError) {
					App.Current.toastError(getContext(), value.Error);
					return;
				}
				if(value.Value ==null){
					App.Current.showError(pn_smt_back_editor.this.getContext(), "数据为空");
				}
				if (value.Value != null) {

					datarow_head = value.Value;
					datarow_head.setValue("plan_return_date",txt_date.getContentText());
					String txt_prod = String.valueOf(value.Value.getValue("item_code"));

					String txt_prod_name = String.valueOf(value.Value.getValue("item_name"));
					pn_smt_back_editor.this.txt_prod_name_cell.setContentText(txt_prod_name);

					pn_smt_back_editor.this.txt_prod_cell.setContentText(txt_prod+"("+item_count+")");
					Log.e("LZ584",txt_prod);
					get_branch_id();
				}
			}
		});
	}

	public void  get_branch_id(){
		String sql = "   select  ROW_NUMBER() over(order by isnull(1,1) desc) number,\n" +
				"    t0.id,t0.type,t0.code,t0.work_order_id,t7.code work_order_code,t0.create_time,t5.code+' - '+t5.name create_user,\n" +
				"    t0.update_time,t6.code+' - '+t6.name update_user,t0.organization_id,t1.code org_code,t0.factory_id,t2.code factory_code,t2.code+' - '+t2.name factory_name,\n" +
				"    t0.warehouse_id,t3.code warehouse_code,t0.item_id,t4.code item_code,t4.name item_name,t4.uom_code,t0.plan_quantity,\n" +
				"    t0.completed_quantity,t0.scrapped_quantity,t0.release_time,t0.plan_issue_date,plan_start_date,t0.plan_complete_date,t7.status wo_status,\n" +
				"    t0.actual_start_date,t0.actual_complete_date,t0.issue_order_count,t0.status,t0.priority,t0.comment,t8.code+' - '+t8.name process_name\n" +
				"    FROM dbo.mm_wo_task_order_head t0\n" +
				"    INNER JOIN dbo.mm_organization t1 ON t1.id=t0.organization_id\n" +
				"    LEFT JOIN dbo.mm_partner t2 ON t2.id=t0.factory_id\n" +
				"    LEFT JOIN dbo.mm_warehouse t3 ON t3.id=t0.warehouse_id\n" +
				"    INNER JOIN mm_item t4 ON t4.id=t0.item_id\n" +
				"    INNER JOIN dbo.core_user t5 ON t5.id=t0.create_user\n" +
				"    LEFT JOIN dbo.core_user t6 ON t6.id=t0.update_user\n" +
				"    INNER JOIN mm_wo_work_order_head t7 ON t7.id=t0.work_order_id\n" +
				"    left join fm_process t8 on t8.id=t0.process_id\n" +
				"    where t0.status='已下达' and  (t7.status ='已发放' or t7.status='完成' or t7.status='已关闭')\n" +
				"    AND   t0.organization_id<>'157'   and t0.code = ?";
		Parameters p = new Parameters().add(1,task_order_code);
		App.Current.DbPortal.ExecuteRecordAsync("core_and", sql,p,new ResultHandler<DataRow>() {
			public void handleMessage(Message msg) {
				final Result<DataRow> value = Value;
				if (value.Value==null) {
					App.Current.showError(pn_smt_back_editor.this.getContext(), "查询出的数据为空，你的工单有误");
				}
				else if(value.HasError){
					App.Current.showError(pn_smt_back_editor.this.getContext(), value.Error);
				}
				else {
					 organization_id = value.Value.getValue("organization_id",0);
					 org_code = value.Value.getValue("org_code","");

					 String sql1 = "select * from dbo.mm_organization where id = ?";
					Parameters p1 = new Parameters().add(1,organization_id);App.Current.DbPortal.ExecuteRecordAsync("core_and", sql1,p1,new ResultHandler<DataRow>() {
						public void handleMessage(Message msg) {
							Result<DataRow> value1 = Value;
							if (value1.Value ==null){
								App.Current.showError(pn_smt_back_editor.this.getContext(), "查询不出工单的组织");
							} else if (value1.HasError) {
								App.Current.showError(pn_smt_back_editor.this.getContext(), value1.Error);

							}
							else{
								branch_id = value1.Value.getValue("branch_id",0);

							}
						}
					});


				}
			}
		});
	}


@SuppressLint("HandlerLeak")

public  void load_store_keeper_name(long branch_id){
	String sql = "SELECT t0.*,t1.code org_code,t4.code item_code,t4.name item_name,t2.code task_order_code,t6.code work_order_code ,t7.name store_keeper_name\n" +
			"FROM dbo.mm_wo_return_order_head t0\n" +
			"INNER JOIN dbo.mm_organization t1 ON t1.id=t0.organization_id\n" +
			"INNER JOIN dbo.mm_wo_task_order_head t2 ON t2.id=t0.task_order_id\n" +
			"INNER JOIN dbo.mm_item t4 ON t4.id=t2.item_id\n" +
			"INNER JOIN dbo.mm_wo_work_order_head t6 ON t6.id=t0.work_order_id\n" +
			"left JOIN dbo.core_user t7 ON t7.id=t0.store_keeper\n" +
			"where t0.id=4101";
	Parameters p = new Parameters().add(1,task_order_code);
	App.Current.DbPortal.ExecuteRecordAsync("core_and", sql,p,new ResultHandler<DataRow>() {
		public void handleMessage(Message msg) {
			final Result<DataRow> value = Value;
			if (value.Value==null) {
				//App.Current.showInfo(pn_smt_back_editor.this.getContext(), "你的仓管员为空");
			}else if(value.HasError){
				App.Current.showError(pn_smt_back_editor.this.getContext(), String.valueOf(value.Error));
			}else {
				store_keeper_name = value.Value.getValue("store_keeper_name","无");
				//pn_smt_back_editor.this.txt_store_keeper_name.setContentText(store_keeper_name);
			}
		}
	});

}




	@Override
	public void commit()
	{	if(flag1_commit==0){
		App.Current.toastInfo(this.getContext(), "不要重复提交。");
	}else {
		flag1_commit = 0;
		if(flaga ==0){
		String user= String.valueOf(this.txt_user_cell.getContentText().trim());
		if (txt_task_order_cell.getContentText() == null) {
			App.Current.toastInfo(this.getContext(), "未指定生产任务。");
			return;
		}
		

		else if (user == null || user.length() == 0) {
			App.Current.toastInfo(this.getContext(), "未指定操作人员。");
			return;
		}
		
		else if (this.Adapter.DataTable == null || this.Adapter.DataTable.Rows.size() == 0) {
			App.Current.toastInfo(this.getContext(), "没有扫描物料条码。");


			return;}



		for (int i = 0; i < dataTable.Rows.size(); i++) {


		}

		Map<String, String> entry = new HashMap<String, String>();

		Map<String, String> head_entry =new HashMap<String, String>();
		head_entry.put("id","");
		head_entry.put("create_user",App.Current.UserID);
		head_entry.put("priority","0");
		head_entry.put("comment","SMT PDT 余料回库");
		head_entry.put("branch_id",String.valueOf(branch_id));
		head_entry.put("task_order_id",String.valueOf(dataTable.Rows.get(0).getValue("task_order_id",0)));
		head_entry.put("return_type",txt_retreating_type.getContentText());
		head_entry.put("Operation_user",App.Current.UserCode);

		ArrayList<Map<String, String>> item_entries = new ArrayList<Map<String, String>>();
		Map<String, String> item_entry = null;
		for (int i = 0; i < dataTable.Rows.size(); i++) {
			item_entry = new HashMap<String, String>();
			item_entry.put("line_id", String.valueOf(dataTable.Rows.get(i).getValue("line_id",0)));
			item_entry.put("task_order_line_id", String.valueOf(dataTable.Rows.get(i).getValue("task_order_line_id",0)));
			item_entry.put("quantity", String.valueOf(dataTable.Rows.get(i).getValue("back_num",0)));
			item_entry.put("warehouse_code", String.valueOf(dataTable.Rows.get(i).getValue("warehouse_code","")));
			item_entry.put("store_keeper", "2751");
			item_entry.put("lot_number",String.valueOf(dataTable.Rows.get(i).getValue("lot_number","")));
			item_entry.put("vendor_id", String.valueOf(dataTable.Rows.get(i).getValue("vendor_id","")));
			item_entry.put("vendor_model", String.valueOf(dataTable.Rows.get(i).getValue("vendor_model","")));
			item_entry.put("vendor_lot", String.valueOf(dataTable.Rows.get(i).getValue("vendor_lot","")));
			item_entry.put("date_code", String.valueOf(dataTable.Rows.get(i).getValue("date_code","")));
			item_entry.put("comment", String.valueOf(dataTable.Rows.get(i).getValue("comment","")));
			item_entry.put("plan_date", String.valueOf(datarow_head.getValue("plan_date","")));
			//item_entry.put("store_keeper", String.valueof(store_keeper);
			item_entries.add(item_entry);
		}
		String xml = XmlHelper.createXml("document", head_entry, "items", "item", item_entries);
		Log.e("len", "XML:" + xml);
		String id="" ;
		//return xml;
		String sql="exec p_mm_wo_return_order_create ?,? output";
		Connection conn = App.Current.DbPortal.CreateConnection(pn_smt_back_editor.this.Connector);
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
					App.Current.showError(pn_smt_back_editor.this.getContext(), rs.Error);
					return;
				}

				//_order_row = null;
				App.Current.toastInfo(pn_smt_back_editor.this.getContext(), "提交成功");
				id = rs.Value;

				//pn_smt_back_editor.this.clear();
				//pn_smt_back_editor.this.loadTaskOrderItem(_rownum);
			} else {

				//_order_row = null;
				return;
			}
		} catch (SQLException e) {
			//_order_row = null;
			App.Current.showInfo(pn_smt_back_editor.this.getContext(), e.getMessage());
			e.printStackTrace();

			//pn_tr_issue_editor.this.clear();
			//pn_tr_issue_editor.this.loadTaskOrderItem(_rownum);
		}
		int flage = 0;
			if(flage == 0){
		String sql6 = "update  a   set store_keeper = 2751 from mm_wo_return_order_item a  where a.head_id =? exec p_mm_wo_return_order_release ?,?";
		int a =0;
		Parameters p6 = new Parameters().add(1,id).add(2,id).add(3,App.Current.UserID);
		App.Current.DbPortal.ExecuteNonQueryAsync(Connector, sql6, p6, new ResultHandler<Integer>() {

		@Override
			public void handleMessage(Message msg) {
				pn_smt_back_editor.this.ProgressDialog.dismiss();


				Result<Integer> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_smt_back_editor.this.getContext(), result.Error);
					return;
				}else {
					App.Current.showInfo(pn_smt_back_editor.this.getContext(), "下达成功");

				}


			}
		});}
		else {
				App.Current.showInfo(pn_smt_back_editor.this.getContext(), "下达成功");
		}}
		else {
		App.Current.showInfo(pn_smt_back_editor.this.getContext(), "轻忽重复提交");
	}
		clear();


	}}



	public void getcount_item(){

	String sql = "WITH temp AS (SELECT t0.*,t1.code item_code,t1.name item_name,t1.segment2,t2.code warehouse_code,t3.code vendor_code,t3.name vendor_name,\n" +
			"(t0.quantity_per_assembly*t5.plan_quantity+t0.lost_quantity) required_quantity,t4.quantity_issued wo_issued_quantity,\n" +
			"(SELECT ISNULL(SUM(quantity),0) FROM dbo.mm_stock_item  WHERE item_id=t0.item_id AND warehouse_id=t0.warehouse_id) stock_quantity\n" +
			"\n" +
			"FROM \n" +
			"dbo.mm_wo_task_order_item t0 \n" +
			"INNER JOIN mm_item t1 ON t1.id=t0.item_id\n" +
			"LEFT JOIN dbo.mm_warehouse t2 ON t2.id=t0.warehouse_id\n" +
			"LEFT JOIN dbo.mm_partner t3 ON t3.id=t0.vendor_id\n" +
			"left join dbo.mm_wo_work_order_item t4 on t4.head_id=t0.work_order_id and t4.item_id=t0.item_id and t4.seq_num =t0.seq_num\n" +
			"inner join mm_wo_task_order_head t5 on t5.id=t0.head_id\n" +
			"WHERE t0.head_id=?)\n" +
			"SELECT COUNT(*)  count FROM temp WHERE  wo_issued_quantity>required_quantity";
	Parameters p = new Parameters().add(1,task_order_id);
		App.Current.DbPortal.ExecuteRecordAsync("core_and", sql,p,new ResultHandler<DataRow>() {

			public void handleMessage(Message msg) {
				final Result<DataRow> value = Value;
				if (value.Value==null) {
					App.Current.showInfo(pn_smt_back_editor.this.getContext(), "没有需要回库的物料");
					Log.e("LZH922",String .valueOf(task_order_id));
				}else if(value.HasError){
					App.Current.showError(pn_smt_back_editor.this.getContext(), String.valueOf(value.Error));
				}else {
					//App.Current.showInfo(pn_smt_back_editor.this.getContext(), "错误914行");
					//App.Current.showInfo(pn_smt_back_editor.this.getContext(), "错误914行");
					Log.e("LZH922",String .valueOf(task_order_id));
					Log.e("LZH922",String .valueOf(value.Value.getValue("count",1)));
					item_count = value.Value.getValue("count",0);
					//pn_smt_back_editor.this.txt_store_keeper_name.setContentText(store_keeper_name);
					Log.e("LZH971",String.valueOf(item_count));
				}

			}
		});



}
	public void clear() {

		//this.txt_create_date_cell.setContentText("");
		this.txt_task_order_cell.setContentText("");
		this.txt_user_cell.setContentText("");
		this.txt_date.setContentText("");
		this.txt_prod_cell.setContentText("");
		//this.txt_date_time.setContentText("");
		this.txt_prod_name_cell.setContentText("");
		this.txt_retreating_type.setContentText("");
		dataTable = null;
		Adapter.notifyDataSetChanged();
	}


}












//		Parameters p=new Parameters().add(1,xml);
//		App.Current.DbPortal.ExecuteNonQueryAsync(Connector, sql, p, new ResultHandler<Integer>() {
//
//			@Override
//			public void handleMessage(Message msg) {
//				pn_smt_back_editor.this.ProgressDialog.dismiss();
//
//
//				Result<Integer> result = this.Value;
//				if (result.HasError) {
//					App.Current.showError(pn_smt_back_editor.this.getContext(), result.Error);
//					return;
//				}
//
//
//			}
//		});
//		App.Current.toastInfo(this.getContext(), "正在保存......" );
//		String sql5="exec p_mm_wo_return_order_release ?,?";
//		Parameters p5 = new Parameters().add(1,"0").add(2,App.Current.UserCode);
//		App.Current.DbPortal.ExecuteNonQueryAsync(Connector, sql, p, new ResultHandler<Integer>() {
//
//			@Override
//			public void handleMessage(Message msg) {
//				pn_smt_back_editor.this.ProgressDialog.dismiss();
//
//
//				Result<Integer> result = this.Value;
//				if (result.HasError) {
//					App.Current.showError(pn_smt_back_editor.this.getContext(), result.Error);
//					return;
//				}
//
//
//			}
//		});
//		App.Current.toastInfo(this.getContext(), "保存下达成功" );





//-------------------------------------------------------

//		String sql6="exec p_mm_wo_return_order_release ?,?";
//		Parameters p6 = new Parameters().add(1,"0").add(2,App.Current.UserCode);
//		App.Current.DbPortal.ExecuteNonQueryAsync(Connector, sql, p, new ResultHandler<Integer>() {
//
//			@Override
//			public void handleMessage(Message msg) {
//				pn_smt_back_editor.this.ProgressDialog.dismiss();
//
//
//				Result<Integer> result = this.Value;
//				if (result.HasError) {
//					App.Current.showError(pn_smt_back_editor.this.getContext(), result.Error);
//					return;
//				}
//
//
//			}
//		});








//		String group1 =String.valueOf( dataTable.getValue(1,"back_num"));
//		String group2 =String.valueOf( dataTable.getValue(1,"seq_num"));
//		String group3 =String.valueOf( dataTable.getValue(1,"task_order_id"));
//		String group4 =String.valueOf( dataTable.getValue(1,"vendor_id"));
//		Log.e("luzhihao",group1 );
//		Log.e("luzhihao",group2 );
//		Log.e("luzhihao",group3 );
//		Log.e("luzhihao",group4 );











//final Long task_order_id = _task_order_row.getValue("id", 0L);
//final String machine = txt_machine_cell.getContentText().trim();
//final String program = txt_program_cell.getContentText().trim();
//
//		if (check(true)) {
//			String sql = "exec p_fm_smt_review_create ?,?,?,?,?,?";
//			Parameters p = new Parameters().add(1, task_order_id).add(2, machine).add(3, program).add(4, App.Current.UserID).add(5, "OK").add(6, reviewer);
//			Result<Integer> rn = App.Current.DbPortal.ExecuteNonQuery(pn_smt_back_editor.this.Connector, sql, p);
//			if (rn.HasError) {
//				App.Current.showError(pn_smt_back_editor.this.getContext(), rn.Error);
//				return;
//			}
//
//			App.Current.toastInfo(pn_smt_back_editor.this.getContext(), "检查OK！");
//			pn_smt_back_editor.this.Adapter.DataTable.Rows.clear();
//			pn_smt_back_editor.this.Adapter.notifyDataSetChanged();
//			pn_smt_back_editor.this.txt_reviewer_cell.setContentText("");
//
//		}








