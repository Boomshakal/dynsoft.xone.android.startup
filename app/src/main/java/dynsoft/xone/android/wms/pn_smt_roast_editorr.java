package dynsoft.xone.android.wms;

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

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.SwitchCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.PromptCallback;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.Book;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataSet;
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

public class pn_smt_roast_editorr extends pn_editor {

	public pn_smt_roast_editorr(Context context) {
		super(context);
	}

	public ButtonTextCell txt_kind;//操作类型
	public ButtonTextCell txt_task_code;//工单
	public TextCell txt_item_code;
	public TextCell txt_time; //时长

	public TextCell txt_temperature;//温度
	public TextCell txt_back_store;//接收储位
	public TextCell txt_usernumber_code_cell; //用户编码
	public TextCell txt_store;//储位
	public TextCell txt_date_time;
	String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString();
	public int flag;
	public String item_code;
	public String task_order_code;
	public long task_order_id;
	public String item_code1;//arr1
	public String lot_numeber;//arr0
	public int flag1;
	//设置对于的XML文件
	@Override
	public void setContentView() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.pn_smt_roast_editorr, this, true);
		view.setLayoutParams(lp);
	}

	@Override
	public void onPrepared() {

		super.onPrepared();
		task_order_code = this.Parameters.get("task_order", "");
		this.txt_kind = (ButtonTextCell) this
				.findViewById(R.id.txt_kind);
		this.txt_task_code = (ButtonTextCell) this
				.findViewById(R.id.txt_task_code);
		this.txt_item_code = (TextCell) this
				.findViewById(R.id.txt_item_code);
		this.txt_temperature = (TextCell) this
				.findViewById(R.id.txt_temperature);
		this.txt_store = (TextCell) this
				.findViewById(R.id.txt_store);
		this.txt_back_store = (TextCell) this
				.findViewById(R.id.txt_back_store);
		this.txt_store = (TextCell) this
				.findViewById(R.id.txt_store);
		this.txt_date_time = (TextCell) this
				.findViewById(R.id.txt_date_time);
		this.txt_usernumber_code_cell = (TextCell) this
				.findViewById(R.id.txt_usernumber_code_cell);
		this.txt_time = (TextCell) this
				.findViewById(R.id.txt_time);
		if (this.txt_kind != null) {
			this.txt_kind.setLabelText("操作类型");
			this.txt_kind.Button.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					//chooseType();
					toastChooseDialog();
				}
			});
		}
		if (this.txt_task_code != null) {
			this.txt_task_code.setLabelText("工单");
			this.txt_task_code.Button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					loadComfirmName(txt_task_code);
				}
			});
			if (!TextUtils.isEmpty(task_order_code)) {
				this.txt_task_code.setContentText(task_order_code);
				String a = txt_task_code.getContentText();
				String sql1 = "select id from mm_wo_task_order_head where code = ?";
				Parameters p1 = new Parameters().add(1, a);
				App.Current.DbPortal.ExecuteRecordAsync("core_and", sql1, p1, new ResultHandler<DataRow>() {
					@Override
					public void handleMessage(Message msg) {
						final Result<DataRow> value = Value;
						if (value.HasError) {
							App.Current.toastError(getContext(), value.Error);
							return;
						}

						if (value.Value == null) {
							App.Current.showError(pn_smt_roast_editorr.this.getContext(), "你扫描的条码有误,获取不到工单ID");
						}
						if (value.Value != null) {
							task_order_id = value.Value.getValue("id", 0L);
							Log.e("luzhihao1", String.valueOf(task_order_id));
							Log.e("luzhihao2", String.valueOf(task_order_id));
							Log.e("LZZZZ", "SDSD");
							//getcount_item();
							//onloaditem(task_order_id);
						}

					}
				});
			}

		}

		if (this.txt_item_code != null) {
			this.txt_item_code.setLabelText("物料编码");
			this.txt_item_code.setReadOnly();
			if (!TextUtils.isEmpty(txt_item_code.getContentText())) {
				bool_item();
			}
		}
		if (this.txt_time != null) {
			this.txt_time.setLabelText("烘烤时长");
			//this.txt_time.setReadOnly();
//			if (!TextUtils.isEmpty(txt_time.getContentText())) {
//				bool_item();
//			}
		}

		if (this.txt_temperature != null) {
			this.txt_temperature.setLabelText("烤板温度");
			//this.txt_temperature.setReadOnly();
			//this.txt_temperature.setContentText("SMT");
		}
		if (this.txt_store != null) {
			this.txt_store.setLabelText("储位");
			this.txt_store.setReadOnly();
		}
		if (this.txt_back_store != null) {
			this.txt_back_store.setLabelText("接收储位");
			this.txt_back_store.setReadOnly();
			this.txt_back_store.setContentText("SMT");
		}

		if (this.txt_usernumber_code_cell != null) {
			this.txt_usernumber_code_cell.setLabelText("操作人");

		}
		if (this.txt_date_time != null) {
			this.txt_date_time.setLabelText("操作时间");
			txt_date_time.setContentText(currentTime);

		}

	}

	//扫条码
	@Override
	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();

		if (bar_code.startsWith("WO:")) {
			//扫描feeder
			txt_task_code.setContentText(bar_code);
		} else if (bar_code.startsWith("MZ") || bar_code.startsWith("M") || bar_code.startsWith("MA")) {
			//扫描工号txt_usernumber_code_cell
			this.txt_usernumber_code_cell.setContentText(bar_code.toString());
		} else if (bar_code.startsWith("CRQ:") || bar_code.startsWith("CQR:")) {
			//扫描工号txt_usernumber_code_cell

			String str = bar_code.substring(4, bar_code.length());
			final String[] arr = str.split("-");
			//String task_order_cell = txt_task_order_cell.getContentText();
			System.out.print(arr[1]);
			if (arr.length >= 3) {

				lot_numeber = arr[0];
				final String arr0 = arr[0];
				final String arr1 = arr[1];
				final String arr3 = arr[3];
				item_code = arr1;
				Log.e("lZH", arr0);
				Log.e("lZH", arr1);
				Log.e("lZH", arr3);
				bool_item1(item_code);
				//get_item_code(arr1);
			}
		}else if (bar_code.startsWith("SMT:")) {
				txt_store.setContentText(bar_code);
			} else {
				App.Current.showError(this.getContext(), "非法条码，请扫描正确的二维码！" + bar_code.toString());
			}


		/////
		//提交操作
		////


	}

	private void toastChooseDialog() {

		final ArrayList<String> names = new ArrayList<String>();
		names.add("进烤箱");
		names.add("出烤箱");

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which >= 0) {
					names.get(which);
					txt_kind.setContentText(names.get(which));
				}
				dialog.dismiss();
			}
		};
		new AlertDialog.Builder(pn_smt_roast_editorr.this.getContext()).setTitle("请选择")
				.setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(txt_kind.getContentText().toString()), listener)
				.setNegativeButton("取消", null).show();
	}

	@Override
	public void commit() {
		//将feeder标记成送修状态，并在待维修记录中增加记录

		String kind = this.txt_kind.getContentText().trim();
		if (kind == null || kind.length() == 0) {
			App.Current.showError(this.getContext(), "操作类型不能为空");
			return;
		}

		String task_code = this.txt_task_code.getContentText().trim();
		if (task_code == null || task_code.length() == 0) {
			App.Current.showError(this.getContext(), "工单不能为空！");
			return;
		}

		String item_code = this.txt_item_code.getContentText().trim();
		if (item_code == null || item_code.length() == 0) {
			App.Current.showError(this.getContext(), "物料编码不能为空！");
			return;
		}
		String store = this.txt_store.getContentText().trim();
		if (store == null || store.length() == 0) {
			App.Current.showError(this.getContext(), "储位不能为空！");
			return;
		}
		String time = this.txt_time.getContentText().trim();
		if (time == null || time.length() == 0) {
			App.Current.showError(this.getContext(), "时长不能为空！");
			return;
		}
		String temperature = this.txt_temperature.getContentText().trim();
		if (temperature == null || temperature.length() == 0) {
			App.Current.showError(this.getContext(), "温度不能为空！");
			return;
		}
		String usernumber_code_cell = this.txt_usernumber_code_cell.getContentText().trim();
		if (usernumber_code_cell == null || usernumber_code_cell.length() == 0) {
			App.Current.showError(this.getContext(), "操作人不能为空！");
			return;
		}
		if(flag1 == 0){
			App.Current.showError(pn_smt_roast_editorr.this.getContext(), "你扫描的条码有误，与工单不对应。");

		}
		String sql = "exec [mm_smt_roast_mgr1] ?,?,?,?,?,?,?,?";
		Parameters p = new Parameters();
//		Log.e("LZH","495");
//		Log.e("LZH",work_order_code_final);
		p.add(1, kind).add(2, task_code).add(3, item_code).add(4, store).add(5, time).add(6, temperature).add(7, usernumber_code_cell).add(8, currentTime);
		App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
			@Override
			public void handleMessage(Message msg) {
				pn_smt_roast_editorr.this.ProgressDialog.dismiss();
				App.Current.toastInfo(pn_smt_roast_editorr.this.getContext(), "烤板记录成功");
				clear();

			}
		});

	}

	//清空数据
	public void clear() {
		this.txt_usernumber_code_cell.setContentText("");
		this.txt_temperature.setContentText("");
		this.txt_time.setContentText("");
		//this.txt_back_store.setContentText("");
		this.txt_item_code.setContentText("");
		this.txt_task_code.setContentText("");
		this.txt_kind.setContentText("");
		this.txt_date_time.setContentText("");
		this.txt_store.setContentText("");
		this.txt_date_time.setContentText("");

		//this.txt_sate.setContentText("");
		//this.txt_depart.setContentText("");
		//this.txt_item_code_cell.setContentText("");
		//this.txt_usernumber_code_cell.setContentText("");
	}



	private void loadComfirmName(ButtonTextCell textcell_1) {
		Link link = new Link("pane://x:code=pn_smt_steel_car_parameter_out_mgr");
		link.Parameters.add("textcell", textcell_1);
		link.Open(null, getContext(), null);
		this.close();
	}

//	public void get_item_code(String a) {
//		final String b = a;
//
//		String sql = "exec [mm_smt_steel_hongkao] ?,?";
//		Parameters p = new Parameters();
//		p.add(1, a).add(2, txt_task_code.getContentText());
//		App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
//
//			public void handleMessage(Message msg) {
//				Result<DataRow> value = Value;
//				if (value.HasError) {
//					App.Current.showError(pn_smt_roast_editorr.this.getContext(), String.valueOf(value.Error));
//				} else if (value.Value == null) {
//					App.Current.showError(pn_smt_roast_editorr.this.getContext(), "工单与物料不匹配");
//				} else {
//					txt_item_code.setContentText(b);
//				}
//			}
//		});
//
//	}


	public void bool_item1(final String item_code) {

		String sql2 = "exec p_mm_wo_return_order_get_item ?,?,?";
		Parameters p2 = new Parameters().add(1, task_order_id).add(2, item_code).add(3, lot_numeber);
		Log.e("luzhihao9", sql2);
		Log.e("luzhihao9", String.valueOf(task_order_id));
		Log.e("luzhihao9", item_code);
		Log.e("luzhihao9", task_order_code);
		Log.e("luzhihao9", lot_numeber);

		//Log.e("luzhihao", item_code1);
		//Log.e("luzhihao", i1);
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
				String a = value.Value.getValue("error", "");
				if (value.Value == null) {
					flag1 = 0;
					App.Current.showError(pn_smt_roast_editorr.this.getContext(), "你扫描的条码有误，与工单不对应。");
				} else if(a.trim().equals("")) {
					flag1 = 1;
					txt_item_code.setContentText(item_code);
				}
				else {
					App.Current.showError(pn_smt_roast_editorr.this.getContext(), value.Value.getValue("error", ""));

				}
			}

		});
	}
	public void bool_item() {

		String sql2 = "exec p_mm_wo_return_order_get_item ?,?,?";
		Parameters p2 = new Parameters().add(1, task_order_id).add(2, item_code).add(3, lot_numeber);
		Log.e("luzhihao", sql2);
		Log.e("luzhihao", task_order_code);
	//	Log.e("luzhihao", item_code1);
		//Log.e("luzhihao", i1);
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
					flag1 = 0;
					App.Current.showError(pn_smt_roast_editorr.this.getContext(), "你扫描的条码有误，与工单不对应。");
				} else {
					flag1 = 1;
				}
			}

		});
	}
}
