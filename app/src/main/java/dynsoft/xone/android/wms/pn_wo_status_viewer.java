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
import dynsoft.xone.android.control.SwitchCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataSet;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class pn_wo_status_viewer extends pn_editor {

	public pn_wo_status_viewer(Context context) {
		super(context);
	}

	public TextCell txt_issue_order_cell;
	public TextCell txt_task_order_cell;
	public TextCell txt_work_order_cell;
	public TextCell txt_create_time_cell;
	public TextCell txt_status_cell;
	public ListView Matrix;
	public TableAdapter Adapter;
	public ImageButton btn_convert;

	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_wo_status_viewer, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_issue_order_cell = (TextCell)this.findViewById(R.id.txt_issue_order_cell);
		this.txt_task_order_cell = (TextCell)this.findViewById(R.id.txt_task_order_cell);
		this.txt_work_order_cell = (TextCell)this.findViewById(R.id.txt_work_order_cell);
		this.txt_create_time_cell = (TextCell)this.findViewById(R.id.txt_create_time_cell);
		this.txt_status_cell = (TextCell)this.findViewById(R.id.txt_status_cell);
		this.btn_convert = (ImageButton)this.findViewById(R.id.btn_convert);
		this.Matrix = (ListView)this.findViewById(R.id.matrix);
		
		if (this.txt_issue_order_cell != null) {
			this.txt_issue_order_cell.setLabelText("发料申请");
			this.txt_issue_order_cell.setReadOnly();
		}
		
		if (this.txt_task_order_cell != null) {
			this.txt_task_order_cell.setLabelText("任务编号");
			this.txt_task_order_cell.setReadOnly();
		}
		
		if (this.txt_work_order_cell != null) {
			this.txt_work_order_cell.setLabelText("工单编号");
			this.txt_work_order_cell.setReadOnly();
		}
		
		if (this.txt_create_time_cell != null) {
			this.txt_create_time_cell.setLabelText("创建日期");
			this.txt_create_time_cell.setReadOnly();
		}
		
		if (this.txt_status_cell != null) {
			this.txt_status_cell.setLabelText("状态");
			this.txt_status_cell.setReadOnly();
		}
		
		if (this.btn_convert != null) {
			this.btn_convert.setImageBitmap(App.Current.ResourceManager.getImage("@/core_convert_white"));
			this.btn_convert.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_wo_status_viewer.this.convert();
				}
			});
		}
		
		if (this.Matrix != null) {
			this.Matrix.setCacheColorHint(Color.TRANSPARENT);
            this.Adapter = new TableAdapter(this.getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (Adapter.DataTable != null) {
                    	DataRow row = (DataRow)Adapter.getItem(position);
                        if (convertView == null) {
                            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_wo_issue_confirm_user, null);
                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_ocpr_gray"));
                        }

                        TextView txt_user = (TextView)convertView.findViewById(R.id.txt_user);
                        String user_code = row.getValue("code", "");
                        String user_name = row.getValue("name", "");
                        String status = row.getValue("status", "");
                        String count = String.valueOf(row.getValue("count", 0));
                        
                        txt_user.setText(user_code + "(" + user_name + ")    " + status + ": " + count + "条");
                        
                        return convertView;
                    }
                    return null;
                }
            };
            
            this.Matrix.setAdapter(this.Adapter);
		}
		
		_issue_order_id = this.Parameters.get("order_id", 0L);
		if (_issue_order_id != null && _issue_order_id > 0) {
			this.loadTaskOrder();
		}
	}
	
	private Long _issue_order_id;
	private DataRow _issue_order_row;
	
	public void loadTaskOrder()
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_mm_wo_receive_get_status ?,?";

		Parameters p  =new Parameters().add(1, App.Current.UserID).add(2, _issue_order_id);
		App.Current.DbPortal.ExecuteDataSetAsync(this.Connector, sql, p, new ResultHandler<DataSet>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_wo_status_viewer.this.ProgressDialog.dismiss();
				
				Result<DataSet> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_wo_status_viewer.this.getContext(), result.Error);
					return;
				}
				
				DataTable t_order = result.Value.Tables.get(0);
				DataTable t_user = result.Value.Tables.get(1);
				
				if (t_order.Rows.size() == 0) {
					App.Current.showError(pn_wo_status_viewer.this.getContext(), "指定发料申请不存在。");
					return;
				}
				
				DataRow row = t_order.Rows.get(0);
				pn_wo_status_viewer.this._issue_order_row = row;
				pn_wo_status_viewer.this.Adapter.DataTable = t_user;
				
				pn_wo_status_viewer.this.txt_task_order_cell.setTag(row);
				pn_wo_status_viewer.this.txt_issue_order_cell.setContentText(row.getValue("code", ""));
				pn_wo_status_viewer.this.txt_task_order_cell.setContentText(row.getValue("task_order_code", ""));
				pn_wo_status_viewer.this.txt_work_order_cell.setContentText(row.getValue("work_order_code", ""));
				pn_wo_status_viewer.this.txt_create_time_cell.setContentText(row.getValue("create_time", ""));
				pn_wo_status_viewer.this.txt_status_cell.setContentText(row.getValue("status", ""));
				
				pn_wo_status_viewer.this.Adapter.DataTable = t_user;
				pn_wo_status_viewer.this.Adapter.notifyDataSetChanged();
				
			}
		});
	}
	
	public void convert()
	{
		Link link = new Link("pane://x:code=mm_and_wo_receive_editor");
		link.Parameters.add("issue_order_id", _issue_order_id);
		link.Parameters.add("issue_order_code", _issue_order_row.getValue("code", ""));
		link.Parameters.add("task_order_code", _issue_order_row.getValue("task_order_code", ""));
		link.Parameters.add("work_order_code", _issue_order_row.getValue("work_order_code", ""));
		
        link.Open(this, this.getContext(), null);
	}
}
