package dynsoft.xone.android.wms;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.barcode.Scanner;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class pn_fm_bi_check_editor extends pn_editor {

	public pn_fm_bi_check_editor(Context context) {
		super(context);
	}

	public TextCell txt_task_order_cell;
	public TextCell txt_prod_name_cell;
	public TextCell txt_machine_cell;
	public TextCell txt_reviewer_cell;
	public ListView Matrix;
	public TableAdapter Adapter;
	public ImageButton btn_clear;
	public ImageButton btn_list;

	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		Pane[] arr = App.Current.Workbench.GetPanes(this.Code);
		if (arr != null){
			this.Header.setTitleText("老化上架"+String.valueOf(arr.length + 1));
		}
		
		this.txt_task_order_cell = (TextCell)this.findViewById(R.id.txt_task_order_cell);
		this.txt_prod_name_cell = (TextCell)this.findViewById(R.id.txt_prod_name_cell);
		this.txt_machine_cell = (TextCell)this.findViewById(R.id.txt_machine_cell);
		this.txt_reviewer_cell = (TextCell)this.findViewById(R.id.txt_reviewer_cell);
		this.Matrix = (ListView)this.findViewById(R.id.matrix);
		this.btn_clear = (ImageButton)this.findViewById(R.id.btn_clear);
		this.btn_list = (ImageButton)this.findViewById(R.id.btn_list);
		
		if (this.txt_task_order_cell != null) {
			this.txt_task_order_cell.setLabelText("生产任务");
			this.txt_task_order_cell.setReadOnly();
		}
		
		if (this.txt_prod_name_cell != null) {
			this.txt_prod_name_cell.setLabelText("产品描述");
			this.txt_prod_name_cell.setReadOnly();
		}
		
		if (this.txt_machine_cell != null) {
			this.txt_machine_cell.setLabelText("设备编号");
			this.txt_machine_cell.setReadOnly();
		}
		
		if (this.txt_reviewer_cell != null) {
			this.txt_reviewer_cell.setLabelText("作业人员");
			this.txt_reviewer_cell.setReadOnly();
			this.txt_reviewer_cell.setContentText(App.Current.UserName);
		}
		
		if (this.Matrix != null) {
        	this.Matrix.setCacheColorHint(Color.TRANSPARENT);
            //LinearLayout.LayoutParams lp_listview = new LinearLayout.LayoutParams(-1,10);
            //this.Matrix.setLayoutParams(lp_listview);
            this.Adapter = new TableAdapter(this.getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (Adapter.DataTable != null) {
                    	DataRow row = (DataRow)Adapter.getItem(position);
                    	
                    	if (convertView == null) {
                    		convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_fm_bi_check, null);
                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/fm_smt_index_16"));
                    	}
                        
                        TextView num = (TextView)convertView.findViewById(R.id.num);
                        TextView txt_feeder = (TextView)convertView.findViewById(R.id.txt_feeder);
                        
                        num.setText(String.valueOf(position + 1));
                        String feeder = row.getValue("bi_no", "") + ", " + String.valueOf(row.getValue("lot_number", 0)) ;
                        txt_feeder.setText(feeder);

                        return convertView;
                    }
                    return null;
                }
            };
            
            this.Adapter.DataTable = new DataTable();
            this.Matrix.setAdapter(Adapter);
        }
		
		
		this.btn_clear = (ImageButton)this.findViewById(R.id.btn_clear);
		if (this.btn_clear != null){
			this.btn_clear.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_white"));
			this.btn_clear.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_fm_bi_check_editor.this.Adapter.DataTable.Rows.clear();
					pn_fm_bi_check_editor.this.Adapter.notifyDataSetChanged();
				}
			});
		}
		
		_txt_item_code = new EditText(pn_fm_bi_check_editor.this.getContext());
		_item_dialog = new AlertDialog.Builder(pn_fm_bi_check_editor.this.getContext())
		.setMessage("请扫描需老化的产品1")
		.setView(_txt_item_code)
		/*.setOnKeyListener(new DialogInterface.OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (event.getRepeatCount() == 0) {
	    			if (keyCode == 4){// 返回键
	    			} else if ((keyCode == 220) | (keyCode == 211) | (keyCode == 212)
	    					| (keyCode == 221)){// 扫描键
	
	    				// 扫描开始
	    				Scanner.Read();
	    				return true;
	    			}
	    		}
				return false;
			}
		})*/
		.setCancelable(false).create();
		
	}
	
	private DataRow _bi_wh_row;
	private AlertDialog _item_dialog;
	private EditText _txt_item_code;
	
	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_fm_bi_check_editor, this, true);
        view.setLayoutParams(lp);
	}

	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		
		if (bar_code.startsWith("TO:")){
			String task_order = bar_code.substring(3, bar_code.length());
			this.loadTaskOrder(task_order);
		} else if (bar_code.startsWith("DEV:")) {
			String machine_no =bar_code.substring(4, bar_code.length());
			this.loadMachine(machine_no);	
		} else if (bar_code.length() ==7) {
			this.loadBiNo(bar_code);
		}  else if (bar_code.startsWith("M")) {
			if (_item_dialog.isShowing() == false) {
				this.txt_reviewer_cell.setContentText(bar_code);
			}
		} 
		else 
		   {
		   if (_bi_wh_row!=null && _item_dialog.isShowing() )
		   {
			   this.loadProdSnNo(bar_code);
			
		   }else
		   {
			   App.Current.toastError(pn_fm_bi_check_editor.this.getContext(), "无效的条码格式。请先扫描老化架号");
			   App.Current.playSound(R.raw.hook);
		   }
		  }
		
	}
	
	private DataRow _task_order_row;
	private DataRow _macheine_row;
	
	public void loadMachine(String machine_no)
	{
	if (this.Adapter.DataTable != null && this.Adapter.DataTable.Rows.size() != 0) {
			App.Current.toastError(this.getContext(), "已扫描老化架，不允许更换机器编号");
			App.Current.playSound(R.raw.hook);
			return;
		}
	 String sql ="exec p_fm_work_bi_check_machine_no ?";
	 Parameters p = new Parameters().add(1, machine_no);
	 App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
		    public void handleMessage(Message msg) {
		    pn_fm_bi_check_editor.this.ProgressDialog.dismiss();
			final Result<DataRow> result = this.Value;
			if (result.HasError) {
				App.Current.showError(pn_fm_bi_check_editor.this.getContext(), result.Error);
				App.Current.playSound(R.raw.hook);
				return;
			}else
			{
			_macheine_row =result.Value;
			pn_fm_bi_check_editor.this.txt_machine_cell.setContentText(_macheine_row.getValue("machine_no",""));
			  
			}
		    }
					
	 });
	 
	}
	
	public void loadProdSnNo(final String lot_number)
	{
	  String  task_order_code =	_task_order_row.getValue("code","");
	  //exec p_fm_work_check_barcode @task_order_code,@work_line,@work_station,@work_shift,@sequence,@machine,@operator,@barcode,@scan_type
	  
	  if (pn_fm_bi_check_editor.this.Adapter.DataTable != null) {
			boolean exists = false;
			for (DataRow row : pn_fm_bi_check_editor.this.Adapter.DataTable.Rows) {
				if (row.getValue("lot_number", "").contains(lot_number)) {
					exists = true;
					break;
				}
			 }
			
			if (exists) {
				App.Current.toastError(pn_fm_bi_check_editor.this.getContext(), "该产品已经扫描；请勿重复上架！");
				App.Current.playSound(R.raw.hook);
				return;
			}
	   }
	  
	  String sql ="exec p_fm_check_barcode_for_pda ?,?,?";
	  Parameters p = new Parameters().add(1, lot_number).add(2, task_order_code).add(3, App.Current.UserCode);
	  App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
		    public void handleMessage(Message msg) {
					pn_fm_bi_check_editor.this.ProgressDialog.dismiss();
				
					final Result<DataRow> result = this.Value;
					if (result.HasError) {
						App.Current.showError(pn_fm_bi_check_editor.this.getContext(), result.Error);
						App.Current.playSound(R.raw.hook);
						_item_dialog.dismiss();
						_bi_wh_row =null;
						return;
					}
					if (!result.Value.getValue("rtnstr","").equals("OK"))
					{
					   App.Current.showError(pn_fm_bi_check_editor.this.getContext(), "条码错误");
					   App.Current.playSound(R.raw.hook);
					   //_item_dialog.dismiss();
						//_bi_wh_row =null;
					   return;	
					}else
					{
					  String snnos =_txt_item_code.getText().toString();
					  if (snnos.contains(lot_number)){
						   App.Current.toastError(pn_fm_bi_check_editor.this.getContext(), "该产品序列号已经扫描");
						   App.Current.playSound(R.raw.hook);
						   return;
						}
						if (snnos.length() > 0){
							snnos += "@";
						}
						snnos +=lot_number;
					   _txt_item_code.setText(snnos);
					  String[] arr =snnos.split("@");
					  int cunt =arr.length;
					  if (cunt==_macheine_row.getValue("test_qty",0))
					  {
					  _bi_wh_row.setValue("lot_number", snnos);
					  pn_fm_bi_check_editor.this.Adapter.DataTable.Rows.add(_bi_wh_row);
					  pn_fm_bi_check_editor.this.Adapter.notifyDataSetChanged();
						_item_dialog.dismiss();
						_bi_wh_row =null;
						_txt_item_code.setText("");
					  }else
					  {
						  _item_dialog.setMessage("请扫描需老化的产品["+String.valueOf(cunt)+"]");
					  }
					  
					}
					
		    }
	  });
	}
	
	public void loadTaskOrder(String code)
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_fm_bi_get_task_order_code ?";
		
		Parameters p = new Parameters().add(1, code);
		App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_fm_bi_check_editor.this.ProgressDialog.dismiss();
				
				final Result<DataTable> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_fm_bi_check_editor.this.getContext(), result.Error);
					return;
				}
				
				if (result.Value == null || result.Value.Rows.size() == 0) {
					App.Current.showError(pn_fm_bi_check_editor.this.getContext(), "找不到生产任务。");
					App.Current.playSound(R.raw.hook);
					return;
				}
				
				if (result.Value != null && result.Value.Rows.size() > 0) {
					if (result.Value.Rows.size() > 1) {
						DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		                        if (which >= 0) {
		                        	DataRow row = result.Value.Rows.get(which);
		                        	pn_fm_bi_check_editor.this.showTaskOrder(row);
		                        }
		                        dialog.dismiss();
		                    }
		             };

		                final TableAdapter adapter = new TableAdapter(pn_fm_bi_check_editor.this.getContext()) {
		                    @Override
		                    public View getView(int position, View convertView, ViewGroup parent) {
		                    	DataRow row = (DataRow)result.Value.Rows.get(position);
		                    	if (convertView == null) {
		                    		convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_smt_task_order, null);
		                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
		                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
		                    	}
		                        
		                    	TextView num = (TextView)convertView.findViewById(R.id.num);
		                        TextView txt_task_order = (TextView)convertView.findViewById(R.id.txt_task_order);
		                        TextView txt_item_code = (TextView)convertView.findViewById(R.id.txt_item_code);
		                        TextView txt_item_name = (TextView)convertView.findViewById(R.id.txt_item_name);
		                        
		                        num.setText(String.valueOf(position + 1));
		                        txt_task_order.setText(row.getValue("code", ""));
		                        txt_item_code.setText(row.getValue("item_code", "") + ", " + App.formatNumber(row.getValue("plan_quantity"), "0.##"));
		                        txt_item_name.setText(row.getValue("item_name", ""));

		                        return convertView;
		                    }
		                };
		                
		                adapter.DataTable = result.Value;
		                adapter.notifyDataSetChanged();
		                
		                new AlertDialog.Builder(pn_fm_bi_check_editor.this.getContext())
		                .setTitle("选择生产任务")
		                .setSingleChoiceItems(adapter, 0, listener)
		                .setNegativeButton("取消", null).show();
					} else {
						DataRow row = result.Value.Rows.get(0);
						pn_fm_bi_check_editor.this.showTaskOrder(row);
					}
				}
			}
		});
	}
	
	private void showTaskOrder(DataRow row)
	{
		_task_order_row = row;
		
		this.txt_task_order_cell.setContentText(row.getValue("code", ""));
		this.txt_prod_name_cell.setContentText(row.getValue("item_name", ""));
		//this.txt_machine_cell.setContentText("");
		//this.txt_program_cell.setContentText("");
	}
	
    
	public void loadBiNo(String code)
	{
		if (_task_order_row == null) {
			App.Current.toastInfo(this.getContext(), "未指定生产任务。");
			return;
		}

		String machine = this.txt_machine_cell.getContentText().trim();
		if (machine == null || machine.length() == 0) {
			App.Current.toastInfo(this.getContext(), "未指定设备编号。");
			return;
		}
		String machine_no =this.txt_machine_cell.getContentText();
	   if (!code.startsWith(machine_no))
	   {
		   App.Current.toastError(this.getContext(), "扫描的老化位置和机器码不一致。");
		   App.Current.playSound(R.raw.hook);
			return;
	   }
		String sql = "exec p_fm_bi_get_bi_no ?";
		Parameters p = new Parameters().add(1, code);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_fm_bi_check_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.toastInfo(pn_fm_bi_check_editor.this.getContext(), result.Error);
					return;
				}
				
				_bi_wh_row = result.Value;
				if (_bi_wh_row == null) {
					App.Current.playSound(R.raw.hook);
					App.Current.toastError(pn_fm_bi_check_editor.this.getContext(), "老化位置不存在");
					if (_item_dialog.isShowing() != false) {
						_item_dialog.dismiss();
					}
					return;
				}
				
				if (!_bi_wh_row.getValue("lot_number", "").equals("无绑定")) {
					App.Current.playSound(R.raw.hook);
					App.Current.toastError(pn_fm_bi_check_editor.this.getContext(), "老化位置已扫描绑定产品。");
					return;
				}
				
				if (pn_fm_bi_check_editor.this.Adapter.DataTable != null) {
					boolean exists = false;
					for (DataRow row : pn_fm_bi_check_editor.this.Adapter.DataTable.Rows) {
						if (row.getValue("bi_no", "").equals(_bi_wh_row.getValue("bi_no", ""))) {
							exists = true;
							break;
						}
					 }
					
					if (exists) {
						App.Current.toastError(pn_fm_bi_check_editor.this.getContext(), "该老化位置已经扫描。");
						App.Current.playSound(R.raw.hook);
						return;
					}
					
					//pn_fm_bi_check_editor.this.Adapter.DataTable.Rows.add(_bi_wh_row);
					//pn_fm_bi_check_editor.this.Adapter.notifyDataSetChanged();
										
					_item_dialog.show();
				}
			}
		});
		
	   	
	}

	@Override
	public void commit()
	{
		if (_task_order_row == null) {
			App.Current.toastInfo(this.getContext(), "未指定生产任务。");
			return;
		}
		
		String reviewer = this.txt_reviewer_cell.getContentText().trim();
		if (reviewer == null || reviewer.length() == 0) {
			App.Current.toastInfo(this.getContext(), "未指定复核人员。");
			return;
		}
		
		if (this.Adapter.DataTable == null || this.Adapter.DataTable.Rows.size() == 0) {
			App.Current.toastInfo(this.getContext(), "没有扫描绑定。");
			return;
		}
		ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
		//row =this.Adapter.DataTable.Rows;
		for (DataRow rowx:this.Adapter.DataTable.Rows)
		{
	    
	    Map<String, String> entry = new HashMap<String, String>();
		entry.put("bi_no", rowx.getValue("bi_no", ""));
		entry.put("lot_number", rowx.getValue("lot_number",""));
		entry.put("create_user", App.Current.UserID);
		entry.put("machine_no", this.txt_machine_cell.getContentText());
		entry.put("task_order_code",_task_order_row.getValue("code",""));
		entries.add(entry);
		
		}
		//生成XML数据，并传给存储过程
		String xml = XmlHelper.createXml("bis", null, null, "bi", entries);
		String sql = "exec p_fm_work_bi_status_create ?";
		Parameters p = new Parameters().add(1, xml);
		Result<Integer> rn = App.Current.DbPortal.ExecuteNonQuery(pn_fm_bi_check_editor.this.Connector, sql, p);
		if (rn.HasError) {
			App.Current.showError(pn_fm_bi_check_editor.this.getContext(), rn.Error);
			return;
		}
			
		App.Current.toastInfo(pn_fm_bi_check_editor.this.getContext(), "上传成功,请启动测试！");
		pn_fm_bi_check_editor.this.Adapter.DataTable.Rows.clear();
		pn_fm_bi_check_editor.this.Adapter.notifyDataSetChanged();
		pn_fm_bi_check_editor.this.txt_machine_cell.setContentText("");
		
		
	}
}
