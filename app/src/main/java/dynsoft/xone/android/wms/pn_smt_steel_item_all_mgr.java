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

public class pn_smt_steel_item_all_mgr extends pn_editor {

	public pn_smt_steel_item_all_mgr(Context context) {
		super(context);
	}


	//public ButtonTextCell txt_work_order_code_cell; // 工单编码
	private ArrayList<String> selectedException;
	private ArrayList<String> selectedException2;
	public ButtonTextCell txt_chose_case;
	public TextCell txt_item_code;
	public TextCell txt_item_name;
	public TextCell txt_date_time;
	public TextCell txt_number;
	public ButtonTextCell txt_is_record;
	public TextCell txt_keep_user;
	String currentTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString();
	public int flage = 1;
	public String item_code;

	//设置对于的XML文件
	@Override
	public void setContentView() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.pn_smt_steel_item_all_mgr, this, true);
		view.setLayoutParams(lp);
		selectedException = new ArrayList<String>();
		selectedException2 = new ArrayList<String>();
	}

	//  ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>();
	//  final String [] nItems = {"卷带","吸取中心偏移","送料不良","少配件","保养","感应灯不亮","抛料"};

	//设置显示控件
	@Override
	public void onPrepared() {

		super.onPrepared();



		this.txt_number = (TextCell) this
				.findViewById(R.id.txt_number_cell);
		this.txt_chose_case = (ButtonTextCell) this
				.findViewById(R.id.txt_chose_case_cell);

		this.txt_item_code = (TextCell) this
				.findViewById(R.id.txt_item_code_cell);

		this.txt_item_name = (TextCell) this
				.findViewById(R.id.txt_item_name_cell);

		this.txt_date_time = (TextCell) this
				.findViewById(R.id.txt_date_time_cell);

		this.txt_is_record = (ButtonTextCell) this
				.findViewById(R.id.txt_is_record_cell);

		this.txt_keep_user = (TextCell) this
				.findViewById(R.id.txt_keep_user_cell);



		if (this.txt_chose_case != null) {
			this.txt_chose_case.setLabelText("操作类型");
			//this.txt_chose_case.setContentText(currentTime);
			this.txt_chose_case.Button.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					//chooseType();
					toastChooseDialog1();
				}
			});
		}

		if (this.txt_item_code != null) {
			this.txt_item_code.setLabelText("物料编码");


		}

		if (this.txt_item_name != null) {
			this.txt_item_name.setLabelText("物料信息");
		}
		if (this.txt_date_time != null) {
			this.txt_date_time.setLabelText("日期");
			this.txt_date_time.setContentText(currentTime);
		}

		if (this.txt_is_record != null) {
			this.txt_is_record.setLabelText("烧录");
			this.txt_is_record.Button.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					//chooseType();
					toastChooseDialog();
				}
			});
		}

		if (this.txt_keep_user != null) {
			this.txt_keep_user.setLabelText("操作人(负责人)");
		}

		if (this.txt_number != null) {
			this.txt_number.setLabelText("数量");
		}
	}


	public void onScan(final String barcode) {
		final String bar_code = barcode.trim();
		Log.e("LZH2011",bar_code.substring(0));
		boolean result = bar_code.substring(0).matches("[0-9]+");
//		if (bar_code.startsWith(\"R\")||result ==true ||bar_code.startsWith(\"W")) {
//			txt_item_code.setContentText(bar_code);
//			load_item_name(bar_code);
//		}
		if(bar_code.startsWith("CRQ:") || bar_code.startsWith("CQR:")){
			String str = bar_code.substring(4, bar_code.length());
			final String[] arr = str.split("-");
			//String task_order_cell = txt_task_order_cell.getContentText();
			System.out.print(arr[1]);
			if (arr.length >=3) {
				final String arr0 = arr[0];
				final String arr1 = arr[1];
				final String arr2 = arr[2];
				item_code = arr1;
				txt_item_code.setContentText(arr1);
				load_item_name(item_code);
			}
			else{
				App.Current.showError(this.getContext(), "非法条码，请扫描正确的二维码！"+bar_code.toString());
			}
		}
		else if (bar_code.startsWith("MZ")||bar_code.startsWith("M")||bar_code.startsWith("MA"))
		{
			//扫描工号txt_usernumber_code_cell
			this.txt_keep_user.setContentText(bar_code.toString());
		}
		else {
			App.Current.showError(this.getContext(), "非法条码，请扫描正确的二维码！"+bar_code.toString());
		}
	}
	public void load_item_name(String item_code){
		String sql = "SELECT * FROM dbo.mm_item WHERE code = ?";
		Parameters p = new Parameters();
		p.add(1,item_code);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
			public void handleMessage(Message msg) {
				final Result<DataRow> value = Value;
				if (value.HasError) {
					App.Current.toastError(getContext(), value.Error);
					return;
				}
				else if(value.Value == null){
					App.Current.showError(pn_smt_steel_item_all_mgr.this.getContext(), "物料名称查询出错");
				}
				else{
					txt_item_name.setContentText(value.Value.getValue("name",""));
				}
			}
		});

	}

	private void toastChooseDialog() {

		final ArrayList<String> names = new ArrayList<String>();
		names.add("是");
		names.add("否");


		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which >= 0) {
					names.get(which);
					txt_is_record.setContentText(names.get(which));
				}
				dialog.dismiss();
			}
		};
		new AlertDialog.Builder(pn_smt_steel_item_all_mgr.this.getContext()).setTitle("请选择")
				.setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(txt_is_record.getContentText().toString()), listener)
				.setNegativeButton("取消", null).show();
	}
	private void toastChooseDialog1() {

		final ArrayList<String> names = new ArrayList<String>();
		names.add("入柜");
		names.add("出柜");


		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which >= 0) {
					names.get(which);
					txt_chose_case.setContentText(names.get(which));
				}
				dialog.dismiss();
			}
		};
		new AlertDialog.Builder(pn_smt_steel_item_all_mgr.this.getContext()).setTitle("请选择")
				.setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(txt_chose_case.getContentText().toString()), listener)
				.setNegativeButton("取消", null).show();
	}

//	private void multiChoiceDialog(final StringBuffer nameMessage, final boolean[] selected, final ArrayList<String> names, final ButtonTextCell buttonTextCell) {
//		new AlertDialog.Builder(pn_smt_steel_item_all_mgr.this.getContext()).setTitle("请选择")
//				.setNegativeButton("取消", null).setPositiveButton("确定", null)
//				.setSingleChoiceItems(names.toArray(new String[0]),
//						names.indexOf(pn_smt_steel_item_all_mgr.this.txt_result_cell.getContentText().toString()), listener)
////				.setMultiChoiceItems(names.toArray(new String[0]), selected, new DialogInterface.OnMultiChoiceClickListener() {
////
////					@Override
////					public void onClick(DialogInterface dialogInterface, int i, boolean b) {
////						selected[i] = b;
////						Log.e("lzh0",String.valueOf(i));
////						if(selected[i] == true&&txt_result_cell.getContentText().trim()==""){
////							Log.e("LZH11","222222");
////							txt_result_cell.setContentText(names.get(i));
////						}
////						else if (selected[i] == true&&names.get(i)!=txt_result_cell.getContentText()) {
////							nameMessage.append(names.get(i));
////							//nameMessage.append(",");
////							if(i==0){
////								selected[1]==false;
////							}
////							else {
////								selected[0]==false;
////							}
////							Log.e("lzh11",txt_result_cell.getContentText());
////							Log.e("lzh12",nameMessage.toString());
////
////							txt_result_cell.setContentText(names.get(i));
////
////
////						}
////					}
//				}).show();
//	}









//	private void multiChoiceDialog(final StringBuffer nameMessage, final boolean[] selected, final ArrayList<String> names, final ButtonTextCell buttonTextCell) {
//		new AlertDialog.Builder(pn_smt_steel_item_all_mgr.this.getContext()).setTitle("请选择")
//				.setNegativeButton("取消", null).setPositiveButton("确定",new DialogInterface.OnClickListener(){
//					public void onClick(DialogInterface dialogInterface, int i) {
//
//					}
//		})
//				.setMultiChoiceItems(names.toArray(new String[0]), selected, new DialogInterface.OnMultiChoiceClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialogInterface, int i, boolean b) {
//
//						selected[i] = b;
//						if (selected[i] == true) {
//							nameMessage.append(names.get(i));
//							//nameMessage.append(",");
//
//							if (!selectedException.contains(names.get(i))) {
//								selectedException.add(names.get(i));
//
//							}
//							Log.e("lzh11",txt_result_cell.getContentText());
//							Log.e("lzh12",nameMessage.toString());
//							if(txt_result_cell.getContentText()!=nameMessage.toString()) {
//								txt_result_cell.setContentText(names.get(i));
//								return;
//							}
//						}
//					}
//				}).show();
//	}














	/////
	//提交操作
	////
	@Override
	public void commit() {
		//将feeder标记成送修状态，并在待维修记录中增加记录



		String chose_case = this.txt_chose_case.getContentText().trim();
		if (chose_case == null || chose_case.length() == 0) {
			App.Current.showError(this.getContext(), "请选择类型");
			return;
		}

		String item_code = this.txt_item_code.getContentText().trim();
		if (item_code == null || item_code.length() == 0) {
			App.Current.showError(this.getContext(), "请扫描物料条码");
			return;
		}
		String item_name = this.txt_item_name.getContentText().trim();
//		if (item_name == null || item_name.length() == 0) {
//			App.Current.showError(this.getContext(), "测试中心点不能为空！");
//			return;
//		}
		String date_time = this.txt_date_time.getContentText().trim();
//		if (date_time == null || date_time.length() == 0) {
//			App.Current.showError(this.getContext(), "测试A点不能为空！");
//			return;
//		}
		String number = this.txt_number.getContentText().trim();
		if (number == null || number.length() == 0) {
			App.Current.showError(this.getContext(), "数量不能为空");
			return;
		}
		String is_record = this.txt_is_record.getContentText().trim();
		if (is_record == null || is_record.length() == 0) {
			App.Current.showError(this.getContext(), "是否烧录不能为空");
			return;
		}
		String keep_user = this.txt_keep_user.getContentText().trim();
		if (keep_user == null || keep_user.length() == 0) {
			App.Current.showError(this.getContext(), "操作人不能为空！");
			return;
		}
		int flag;
		Log.e("LZH111",txt_chose_case.getContentText().trim());
		if(txt_chose_case.getContentText().trim().equals("入柜")) {
			if(txt_is_record.getContentText().trim().equals("是")){
				flag = 1;
			}
			else {
				flag = 0;
			}
			String sql = "INSERT INTO dbo.mm_item_all_mgr (item_code,item_name,date_in,in_num,keep_user,is_burning_record\n" +
					")    VALUES(?,?,?,?,?,?)";

			Parameters p = new Parameters();
			Log.e("LZH2011",sql);
			Log.e("LZH2011",item_code+item_name+date_time+number+keep_user+flag);
			p.add(1, item_code).add(2,item_name).add(3,date_time).add(4,number).add(5,keep_user).add(6,flag);

			App.Current.DbPortal.ExecuteNonQueryAsync(this.Connector, sql, p, new ResultHandler<Integer>() {
				@Override
				public void handleMessage(Message msg) {
					Result<Integer> result = this.Value;
					if (result.HasError) {
						App.Current.showError(pn_smt_steel_item_all_mgr.this.getContext(), result.Error);

					} else {
						App.Current.toastInfo(pn_smt_steel_item_all_mgr.this.getContext(), "入柜提交成功");
						clear();
					}
				}
			});
		}

		else if(txt_chose_case.getContentText().trim().equals("出柜")) {
			if(txt_is_record.getContentText().trim().equals("是")){
				flag = 1;
			}
			else {
				flag = 0;
			}
			String sql = "INSERT INTO dbo.mm_item_all_mgr (item_code,item_name,date_out,out_num,keep_user,is_burning_record\n" +
					")    VALUES(?,?,?,?,?,?)";

			Parameters p = new Parameters();

			p.add(1, item_code).add(2,item_name).add(3,date_time).add(4,number).add(5,keep_user).add(6,flag);

			App.Current.DbPortal.ExecuteNonQueryAsync(this.Connector, sql, p, new ResultHandler<Integer>() {
				@Override
				public void handleMessage(Message msg) {
					Result<Integer> result = this.Value;
					if (result.HasError) {
						App.Current.showError(pn_smt_steel_item_all_mgr.this.getContext(), result.Error);

					} else {
						App.Current.toastInfo(pn_smt_steel_item_all_mgr.this.getContext(), "出柜提交成功");
						clear();
					}
				}
			});
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

		//this.txt_create_date_cell.setContentText("");
		this.txt_chose_case.setContentText("");
		this.txt_keep_user.setContentText("");
		this.txt_is_record.setContentText("");
		this.txt_number.setContentText("");
		//this.txt_date_time.setContentText("");
		this.txt_item_name.setContentText("");
		this.txt_item_code.setContentText("");
	}

//	private void chooseType() {
//
//
//					final StringBuffer nameMessage = new StringBuffer();
//					final boolean[] selected = new boolean[names.size()];
//					toastChooseDialog(nameMessage, selected, names, txt_result_cell);
//	}







}
