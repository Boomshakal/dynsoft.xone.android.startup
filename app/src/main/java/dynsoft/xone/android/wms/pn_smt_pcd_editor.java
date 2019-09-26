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

public class pn_smt_pcd_editor extends pn_editor {

	public pn_smt_pcd_editor(Context context) {
		super(context);
	}


	//public ButtonTextCell txt_work_order_code_cell; // 工单编码
	private ArrayList<String> selectedException;
	private ArrayList<String> selectedException2;
	//public ButtonTextCell txt_chose_case;
	public TextCell txt_item_code;
	public TextCell txt_item_line;   //位号
	public TextCell txt_date_time;
	public TextCell txt_result;
	public TextCell txt_user_code;
	String currentTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString();
	public int flage = 1;
	public String item_code;

	//设置对于的XML文件
	@Override
	public void setContentView() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
		View view = App.Current.Workbench.getLayoutInflater().inflate(
				R.layout.pn_smt_pcd_editor, this, true);
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



		this.txt_item_code = (TextCell) this
				.findViewById(R.id.txt_item_code_cell);
		this.txt_item_line = (TextCell) this
				.findViewById(R.id.txt_item_line_cell);

		this.txt_date_time = (TextCell) this
				.findViewById(R.id.txt_date_time_cell);

		this.txt_result = (TextCell) this
				.findViewById(R.id.txt_result_cell);

		this.txt_user_code = (TextCell) this
				.findViewById(R.id.txt_user_code_cell);

		if (this.txt_item_code != null) {
			this.txt_item_code.setLabelText("PCB编码");
	}

		if (this.txt_item_line != null) {
			this.txt_item_line.setLabelText("位号");
		}
		if (this.txt_date_time != null) {
			this.txt_date_time.setLabelText("日期");
			this.txt_date_time.setContentText(currentTime);
		}
		if (this.txt_user_code != null) {
			this.txt_user_code.setLabelText("操作人(负责人)");
		}

		if (this.txt_result != null) {
			this.txt_result.setLabelText("结果");
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
		if(bar_code.startsWith("C") || bar_code.startsWith("P")){
			txt_item_code.setContentText(bar_code.toString());

		}
		else if (bar_code.startsWith("MZ")||bar_code.startsWith("M")||bar_code.startsWith("MA"))
		{
			//扫描工号txt_usernumber_code_cell
			this.txt_user_code.setContentText(bar_code.toString());
		}
		else {
			App.Current.showError(this.getContext(), "非法条码，请扫描正确的二维码！"+bar_code.toString());
		}
	}

	/////
	//提交操作
	////
	@Override
	public void commit() {
		String item_code = this.txt_item_code.getContentText().trim();
		if (item_code == null || item_code.length() == 0) {
			App.Current.showError(this.getContext(), "请扫PCB条码");
			return;
		}
		String result = this.txt_result.getContentText().trim();
		if (result == null || result.length() == 0) {
			App.Current.showError(this.getContext(), "请输入结果");
			return;
		}
//		if (item_name == null || item_name.length() == 0) {
//			App.Current.showError(this.getContext(), "测试中心点不能为空！");
//			return;
//		}
		String date_time = this.txt_date_time.getContentText().trim();
//		if (date_time == null || date_time.length() == 0) {
//			App.Current.showError(this.getContext(), "测试A点不能为空！");
//			return;
//		}
		String item_line = this.txt_item_line.getContentText().trim();
		if (item_line == null || item_line.length() == 0) {
			App.Current.showError(this.getContext(), "位号不能为空");
			return;
		}
		String user_code = this.txt_user_code.getContentText().trim();
		if (user_code == null || user_code.length() == 0) {
			App.Current.showError(this.getContext(), "操作人不能为空！");
			return;
		}
			String sql = "INSERT INTO dbo.mm_smt_pcb_log (pcb_code,pcb_line,create_date,result,user_code\n" +
					")    VALUES(?,?,?,?,?)";

			Parameters p = new Parameters();
			Log.e("LZH2011",sql);
			p.add(1, item_code).add(2,item_line).add(3,date_time).add(4,result).add(5,user_code);

			App.Current.DbPortal.ExecuteNonQueryAsync(this.Connector, sql, p, new ResultHandler<Integer>() {
				@Override
				public void handleMessage(Message msg) {
					Result<Integer> result = this.Value;
					if (result.HasError) {
						App.Current.showError(pn_smt_pcd_editor.this.getContext(), result.Error);

					} else {
						App.Current.toastInfo(pn_smt_pcd_editor.this.getContext(), "记录成功");
						clear();
					}
				}
			});
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
		this.txt_result.setContentText("");
		this.txt_user_code.setContentText("");
		this.txt_item_code.setContentText("");
		this.txt_item_line.setContentText("");
		//this.txt_date_time.setContentText("");
		this.txt_date_time.setContentText("");
	}

//	private void chooseType() {
//
//
//					final StringBuffer nameMessage = new StringBuffer();
//					final boolean[] selected = new boolean[names.size()];
//					toastChooseDialog(nameMessage, selected, names, txt_result_cell);
//	}







}
