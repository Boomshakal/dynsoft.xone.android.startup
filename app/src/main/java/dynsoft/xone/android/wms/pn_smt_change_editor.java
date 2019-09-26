package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DecimalCell;
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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class pn_smt_change_editor extends pn_editor {

	public pn_smt_change_editor(Context context) {
		super(context);
	}

	public TextCell txt_task_order_cell;
	public TextCell txt_prod_code_cell;
	public TextCell txt_prod_name_cell;
	public TextCell txt_machine_cell;
	public TextCell txt_program_cell;
	public TextCell txt_count_cell;
	public TextCell txt_item_code_cell;
	public TextCell txt_item_name_cell;
	public TextCell txt_quantity_cell;
	public TextCell txt_position_cell;
	public TextCell txt_feeder_cell;
	public TextCell txt_worker_cell;
	public ImageButton btn_list;

	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		Pane[] arr = App.Current.Workbench.GetPanes(this.Code);
		if (arr != null){
			this.Header.setTitleText("SMT备料"+String.valueOf(arr.length + 1));
		}
		
		this.txt_task_order_cell = (TextCell)this.findViewById(R.id.txt_task_order_cell);
		this.txt_prod_code_cell = (TextCell)this.findViewById(R.id.txt_prod_code_cell);
		this.txt_prod_name_cell = (TextCell)this.findViewById(R.id.txt_prod_name_cell);
		this.txt_machine_cell = (TextCell)this.findViewById(R.id.txt_machine_cell);
		this.txt_program_cell = (TextCell)this.findViewById(R.id.txt_program_cell);
		this.txt_count_cell = (TextCell)this.findViewById(R.id.txt_count_cell);
		this.txt_item_code_cell = (TextCell)this.findViewById(R.id.txt_item_code_cell);
		this.txt_item_name_cell = (TextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_quantity_cell = (TextCell)this.findViewById(R.id.txt_quantity_cell);
		this.txt_position_cell = (TextCell)this.findViewById(R.id.txt_position_cell);
		this.txt_feeder_cell = (TextCell)this.findViewById(R.id.txt_feeder_cell);
		this.txt_worker_cell = (TextCell)this.findViewById(R.id.txt_worker_cell);
		this.btn_list = (ImageButton)this.findViewById(R.id.btn_list);
		
		if (this.txt_task_order_cell != null) {
			this.txt_task_order_cell.setLabelText("生产任务");
			this.txt_task_order_cell.setReadOnly();
		}
		
		if (this.txt_prod_code_cell != null) {
			this.txt_prod_code_cell.setLabelText("产品编号");
			this.txt_prod_code_cell.setReadOnly();
		}
		
		if (this.txt_prod_name_cell != null) {
			this.txt_prod_name_cell.setLabelText("产品描述");
			this.txt_prod_name_cell.setReadOnly();
		}
		
		if (this.txt_machine_cell != null) {
			this.txt_machine_cell.setLabelText("机台编号");
			this.txt_machine_cell.setReadOnly();
		}
		
		if (this.txt_program_cell != null) {
			this.txt_program_cell.setLabelText("程序名称");
			this.txt_program_cell.setReadOnly();
		}
		
		if (this.txt_count_cell != null) {
			this.txt_count_cell.setLabelText("备料条数");
			this.txt_count_cell.setReadOnly();
		}
		
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("物料编号");
			this.txt_item_code_cell.setReadOnly();
		}
		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("物料描述");
			this.txt_item_name_cell.setReadOnly();
		}
		
		if (this.txt_quantity_cell != null) {
			this.txt_quantity_cell.setLabelText("配量");
			this.txt_quantity_cell.setReadOnly();
		}
		
		if (this.txt_position_cell != null) {
			this.txt_position_cell.setLabelText("Z位");
			this.txt_position_cell.setReadOnly();
		}
		
		if (this.txt_feeder_cell != null) {
			this.txt_feeder_cell.setLabelText("供料器");
			this.txt_feeder_cell.setReadOnly();
		}

		if (this.txt_worker_cell != null) {
			this.txt_worker_cell.setLabelText("作业人员");
			this.txt_worker_cell.setReadOnly();
		}
		
		this.btn_list = (ImageButton)this.findViewById(R.id.btn_list);
		if (this.btn_list != null){
			this.btn_list.setImageBitmap(App.Current.ResourceManager.getImage("@/core_list_white"));
			this.btn_list.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_smt_change_editor.this.showList();
				}
			});
		}
	}
	
	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_smt_prepare_editor, this, true);
        view.setLayoutParams(lp);
	}

	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		
		if (bar_code.startsWith("WO:")) {
			String task_order = bar_code.substring(3, bar_code.length());
			this.loadTaskOrder(task_order);
		} else if (bar_code.startsWith("B") && bar_code.length() > 20) {
			String device = bar_code.substring(20, bar_code.length());
			if (device.length() > 4 && device.startsWith("DEV:")) {
				device = device.substring(4, device.length());
				this.loadProgram(device);
			}
		} else if (bar_code.startsWith("DEV:")) {
			String device = bar_code.substring(4, bar_code.length());
			this.loadProgram(device);
		} else if (bar_code.startsWith("CRQ:") || bar_code.startsWith("CQR:")) {
			String lot = bar_code.substring(4, bar_code.length());
			String[] arr = lot.split("-");
			if (arr.length > 1){
				String item_code = arr[1];
				this.loadPositions(item_code);
			}
		} else if (bar_code.startsWith("FD:")||bar_code.startsWith("SSY")||bar_code.startsWith("ZSY")  ) {
			
			String feeder = bar_code.substring(3, bar_code.length());
			if (bar_code.startsWith("SSY")||bar_code.startsWith("ZSY"))
			{
				feeder=bar_code;
			}
			this.loadFeeder(feeder);
		} else if (bar_code.startsWith("M")) {
			this.txt_worker_cell.setContentText(bar_code);
		} else {
			App.Current.toastError(pn_smt_change_editor.this.getContext(), "无效的条码格式。");
		}
	}
	
	private DataRow _task_order_row;
	
	public void loadTaskOrder(String code)
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_fm_smt_prepare_get_task_order ?";
		
		Parameters p = new Parameters().add(1, code);
		App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_smt_change_editor.this.ProgressDialog.dismiss();
				
				final Result<DataTable> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_smt_change_editor.this.getContext(), result.Error);
					return;
				}
				
				if (result.Value == null || result.Value.Rows.size() == 0) {
					App.Current.showError(pn_smt_change_editor.this.getContext(), "找不到生产任务。");
					return;
				}
				
				if (result.Value != null && result.Value.Rows.size() > 0) {
					if (result.Value.Rows.size() > 1) {
						DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		                        if (which >= 0) {
		                        	DataRow row = result.Value.Rows.get(which);
		                        	pn_smt_change_editor.this.showTaskOrder(row);
		                        }
		                        dialog.dismiss();
		                    }
		                };

		                final TableAdapter adapter = new TableAdapter(pn_smt_change_editor.this.getContext()) {
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
		                        txt_task_order.setText(row.getValue("item_code", "") + ", " + App.formatNumber(row.getValue("plan_quantity"), "0.##"));
		                        txt_task_order.setText(row.getValue("item_name", ""));

		                        return convertView;
		                    }
		                };
		                
		                adapter.DataTable = result.Value;
		                adapter.notifyDataSetChanged();
		                
		                new AlertDialog.Builder(pn_smt_change_editor.this.getContext())
		                .setTitle("选择生产任务")
		                .setSingleChoiceItems(adapter, 0, listener)
		                .setNegativeButton("取消", null).show();
					} else {
						DataRow row = result.Value.Rows.get(0);
						pn_smt_change_editor.this.showTaskOrder(row);
					}
				}
			}
		});
	}
	
	private void showTaskOrder(DataRow row)
	{
		_task_order_row = row;
		
		this.txt_task_order_cell.setContentText(row.getValue("code", ""));
		this.txt_prod_code_cell.setContentText(row.getValue("item_code", ""));
		this.txt_prod_name_cell.setContentText(row.getValue("item_name", ""));
		this.txt_machine_cell.setContentText("");
		this.txt_program_cell.setContentText("");
		this.txt_count_cell.setContentText("");
		this.txt_item_code_cell.setContentText("");
		this.txt_item_name_cell.setContentText("");
		this.txt_quantity_cell.setContentText("");
		this.txt_position_cell.setContentText("");
		this.txt_feeder_cell.setContentText("");
	}
	
	private void loadProgram(String device)
	{
		if (_task_order_row == null) {
			App.Current.toastWarning(this.getContext(), "未指定生产任务。");
			return;
		}
		
		this.txt_machine_cell.setContentText(device);
		
		String sql = "exec p_fm_smt_prepare_get_program ?,?";
		Parameters p = new Parameters().add(1, _task_order_row.getValue("code", "")).add(2, device);
		App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_smt_change_editor.this.ProgressDialog.dismiss();
				
				final Result<DataTable> r = this.Value;
				if (r.HasError) {
					App.Current.showError(pn_smt_change_editor.this.getContext(), r.Error);
					return;
				}
				
				if (r.Value == null || r.Value.Rows.size() == 0) {
					App.Current.showError(pn_smt_change_editor.this.getContext(), "找不到程序名。");
					return;
				}
				
				if (r.Value != null && r.Value.Rows.size() > 0) {
					if (r.Value.Rows.size() > 1) {
		                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		                        if (which >= 0) {
		                        	DataRow row = r.Value.Rows.get(which);
		                        	pn_smt_change_editor.this.showProgram(row);
		                        }
		                        dialog.dismiss();
		                    }
		                };

		                final TableAdapter adapter = new TableAdapter(pn_smt_change_editor.this.getContext()) {
		                    @Override
		                    public View getView(int position, View convertView, ViewGroup parent) {
		                    	DataRow row = (DataRow)r.Value.Rows.get(position);
		                    	if (convertView == null) {
		                    		convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_smt_program, null);
		                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
		                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
		                    	}
		                        
		                    	TextView num = (TextView)convertView.findViewById(R.id.num);
		                        TextView txt_program = (TextView)convertView.findViewById(R.id.txt_program);
		                        
		                        num.setText(String.valueOf(position + 1));
		                        txt_program.setText(row.getValue("program", ""));

		                        return convertView;
		                    }
		                };
		                
		                adapter.DataTable = r.Value;
		                adapter.notifyDataSetChanged();
		                
		                new AlertDialog.Builder(pn_smt_change_editor.this.getContext())
		                .setTitle("选择程序名称")
		                .setSingleChoiceItems(adapter, 0, listener)
		                .setNegativeButton("取消", null).show();
					} else {
						DataRow row = r.Value.Rows.get(0);
						pn_smt_change_editor.this.showProgram(row);
					}
				}
				
			}
		});
	}

	private DataRow _program_row;
	
	private void showProgram(DataRow row)
	{
		_program_row = row;
		
		this.txt_program_cell.setContentText(row.getValue("program", ""));
		this.txt_count_cell.setContentText("");
		this.txt_item_code_cell.setContentText("");
		this.txt_item_name_cell.setContentText("");
		this.txt_quantity_cell.setContentText("");
		this.txt_position_cell.setContentText("");
		this.txt_feeder_cell.setContentText("");
		
		this.loadCount();
	}
	
	public void loadCount()
	{
		if (_task_order_row == null) {
			App.Current.showError(this.getContext(), "未指定生产任务。");
			return;
		}
		
		final long task_order_id = _task_order_row.getValue("id", 0L);
		String machine = this.txt_machine_cell.getContentText().trim();
		if (machine == null || machine.length() == 0) {
			App.Current.showError(this.getContext(), "未指定机台编号。");
			return;
		}
		
		String program = this.txt_program_cell.getContentText().trim();
		if (program == null || program.length() == 0) {
			App.Current.showError(this.getContext(), "未指定程序名称。");
			return;
		}
		
		if (task_order_id > 0 && machine != null && machine.length() > 0) {
			String sql = "exec p_fm_smt_prepare_get_count ?,?,?";
			Parameters p = new Parameters().add(1, task_order_id).add(2, machine).add(3, program);
			App.Current.DbPortal.ExecuteScalarAsync(this.Connector, sql, p, String.class, new ResultHandler<String>(){
				@Override
	    	    public void handleMessage(Message msg) {
					pn_smt_change_editor.this.ProgressDialog.dismiss();
					pn_smt_change_editor.this.txt_count_cell.setContentText("");
					
					Result<String> result = this.Value;
					if (result.HasError) {
						App.Current.showError(pn_smt_change_editor.this.getContext(), result.Error);
						return;
					}
					
					pn_smt_change_editor.this.txt_count_cell.setContentText(result.Value);
				}
			});
		}
	}
	
	public void loadPositions(String item_code)
	{
		if (_task_order_row == null) {
			App.Current.toastWarning(this.getContext(), "未指定生产任务。");
			return;
		}
		
		Long task_order_id = _task_order_row.getValue("id", 0L);
		
		String machine = this.txt_machine_cell.getContentText().trim();
		if (machine == null || machine.length() == 0) {
			App.Current.toastWarning(this.getContext(), "未指定设备编号。");
			return;
		}
		
		String program = this.txt_program_cell.getContentText().trim();
		if (program == null || program.length() == 0) {
			App.Current.toastWarning(this.getContext(), "未指定程序名称。");
			return;
		}
		
		String sql = "exec p_fm_smt_prepare_get_position ?,?,?,?";
		Parameters p = new Parameters().add(1, task_order_id).add(2, machine).add(3, program).add(4, item_code);
		App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_smt_change_editor.this.ProgressDialog.dismiss();
				
				final Result<DataTable> r = this.Value;
				if (r.HasError) {
					App.Current.toastError(pn_smt_change_editor.this.getContext(), r.Error);
					return;
				}
				
				if (r.Value != null && r.Value.Rows.size() > 0) {
					if (r.Value.Rows.size() > 1) {
		                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		                        if (which >= 0) {
		                        	DataRow row = r.Value.Rows.get(which);
		                        	pn_smt_change_editor.this.showPosition(row);
		                        }
		                        dialog.dismiss();
		                    }
		                };

		                final TableAdapter adapter = new TableAdapter(pn_smt_change_editor.this.getContext()) {
		                    @Override
		                    public View getView(int position, View convertView, ViewGroup parent) {
		                    	DataRow row = (DataRow)r.Value.Rows.get(position);
		                    	if (convertView == null) {
		                    		convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_smt_position, null);
		                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
		                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
		                    	}
		                        
		                    	TextView num = (TextView)convertView.findViewById(R.id.num);
		                        TextView txt_position = (TextView)convertView.findViewById(R.id.txt_position);
		                        TextView txt_quantity = (TextView)convertView.findViewById(R.id.txt_quantity);
		                        
		                        num.setText(String.valueOf(position + 1));
		                        
		                        String feeder = row.getValue("feeder_code",  "");
		                        if (feeder == null || feeder.length() == 0) {
		                        	feeder = "未备";
		                        	txt_position.setTextColor(Color.RED);
		                        	txt_quantity.setTextColor(Color.RED);
		                        } else {
		                        	txt_position.setTextColor(Color.rgb(0, 127, 0));
		                        	txt_quantity.setTextColor(Color.rgb(0, 127, 0));
		                        }
		                        
		                        txt_position.setText(row.getValue("position").toString() + ", " + row.getValue("item_code", ""));
		                        txt_quantity.setText(feeder + ", " + row.getValue("feeder_size", "") + ", " +App.formatNumber(row.getValue("quantity"), "0.##"));

		                        return convertView;
		                    }
		                };
		                
		                adapter.DataTable = r.Value;
		                adapter.notifyDataSetChanged();
		                
		                new AlertDialog.Builder(pn_smt_change_editor.this.getContext())
		                .setTitle("选择站位")
		                .setSingleChoiceItems(adapter, 0, listener)
		                .setNegativeButton("取消", null).show();
					} else {
						DataRow row = r.Value.Rows.get(0);
						pn_smt_change_editor.this.showPosition(row);
					}
				}
				
			}
		});
	}
	
	private DataRow _position_row;
	
	private void showPosition(DataRow row)
	{
		_position_row = row;
		if (_position_row == null) {
			App.Current.toastInfo(pn_smt_change_editor.this.getContext(), "物料编号不存在。");
			return;
		}
		
		String position = _position_row.getValue("position").toString();
		if (_position_row.getValue("feeder_code", "").length() > 0) {
			App.Current.toastInfo(pn_smt_change_editor.this.getContext(), "第"+position+"站位已经备过料。");
		}
		
		pn_smt_change_editor.this.txt_item_code_cell.setContentText(_position_row.getValue("item_code", ""));
		pn_smt_change_editor.this.txt_item_name_cell.setContentText(_position_row.getValue("item_name", ""));
		pn_smt_change_editor.this.txt_quantity_cell.setContentText(App.formatNumber(_position_row.getValue("quantity"), ""));
		pn_smt_change_editor.this.txt_position_cell.setContentText(_position_row.getValue("position").toString());
		pn_smt_change_editor.this.txt_feeder_cell.setContentText(_position_row.getValue("feeder_code", ""));
	}
	
	private DataRow _feeder_row;
	
	public void loadFeeder(final String code)
	{
		if (_task_order_row == null) {
			App.Current.toastInfo(this.getContext(), "未指定生产任务。");
			return;
		}

		Long task_order_id = _task_order_row.getValue("id", 0L);
		String machine = this.txt_machine_cell.getContentText().trim();
		if (machine == null || machine.length() == 0) {
			App.Current.toastInfo(this.getContext(), "未指定设备编号。");
			return;
		}
		
		String program = this.txt_program_cell.getContentText().trim();
		if (program == null || program.length() == 0) {
			App.Current.toastInfo(this.getContext(), "未指定程序名称。");
			return;
		}
		
		if (_position_row == null) {
			App.Current.toastInfo(this.getContext(), "未指定站位。");
			return;
		}
		
		String sql = "exec p_fm_smt_prepare_get_feeder ?,?,?,?";
		Parameters p = new Parameters().add(1, task_order_id).add(2, machine).add(3, program).add(4, code);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_smt_change_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_smt_change_editor.this.getContext(), result.Error);
					return;
				}
				
				_feeder_row = result.Value;
				if (_feeder_row == null) {
					App.Current.playSound(R.raw.hook);
					pn_smt_change_editor.this.txt_feeder_cell.setContentText("");
					App.Current.toastInfo(pn_smt_change_editor.this.getContext(), "供料器编号不存在。");
					return;
				}
				
				if (_feeder_row.getValue("expired", "").equals("Y")) {
					App.Current.playSound(R.raw.hook);
					pn_smt_change_editor.this.txt_feeder_cell.setContentText("");
					App.Current.toastError(pn_smt_change_editor.this.getContext(), "供料器编号已过有效期。");
					return;
				}
				
				if (_feeder_row.getValue("size", "").equals(_position_row.getValue("feeder_size", "")) == false) {
					App.Current.playSound(R.raw.hook);
					pn_smt_change_editor.this.txt_feeder_cell.setContentText("");
					App.Current.toastError(pn_smt_change_editor.this.getContext(), "供料器尺寸"+_feeder_row.getValue("size", "")+"与站位表指定尺寸"+_position_row.getValue("feeder_size", "")+"不匹配。");
					return;
				}
				
				int cur_pos = _position_row.getValue("position", 0);
				int old_pos = _feeder_row.getValue("position", 0);
				if (old_pos > 0 && cur_pos != old_pos) {
					final String str = "供料器"+ code + "已在第" + String.valueOf(old_pos) + "站位使用，确定要更改到" + String.valueOf(cur_pos) + "站位吗？";
					App.Current.question(pn_smt_change_editor.this.getContext(), str, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							pn_smt_change_editor.this.txt_feeder_cell.setContentText(_feeder_row.getValue("code", ""));
						}
					});
				} else {
					pn_smt_change_editor.this.txt_feeder_cell.setContentText(_feeder_row.getValue("code", ""));
				}
			}
		});
	}
	
	///显示所有站位表及其扫描记录
	public void showList()
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
		
		String program = this.txt_program_cell.getContentText().trim();
		if (program == null || program.length() == 0) {
			App.Current.toastInfo(this.getContext(), "未指定程序名称。");
			return;
		}
		
		Long task_order_id = _task_order_row.getValue("id", 0L);
		String sql = "exec p_fm_smt_prepare_get_list ?,?,?";
		Parameters p = new Parameters().add(1, task_order_id).add(2, machine).add(3, program);
		App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_smt_change_editor.this.ProgressDialog.dismiss();
				
				final Result<DataTable> r = this.Value;
				if (r.HasError) {
					App.Current.showError(pn_smt_change_editor.this.getContext(), r.Error);
					return;
				}
				
				if (r.Value != null && r.Value.Rows.size() > 0) {
	                final TableAdapter adapter = new TableAdapter(pn_smt_change_editor.this.getContext()) {
	                    @Override
	                    public View getView(int position, View convertView, ViewGroup parent) {
	                    	DataRow row = (DataRow)r.Value.Rows.get(position);
	                    	if (convertView == null) {
	                    		convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_smt_position, null);
	                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
	                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/fm_smt_index_16"));
	                    	}
	                        
	                    	TextView num = (TextView)convertView.findViewById(R.id.num);
	                        TextView txt_position = (TextView)convertView.findViewById(R.id.txt_position);
	                        TextView txt_quantity = (TextView)convertView.findViewById(R.id.txt_quantity);
	                        
	                        num.setText(String.valueOf(position + 1));
	                        
	                        String prepared = "";
	                        String feeder = row.getValue("feeder_code", "");
	                        int prepare_count = row.getValue("prepare_count",  0);
	                        if (prepare_count == 0) {
	                        	prepared = "未备";
	                        	txt_position.setTextColor(Color.RED);
	                        	txt_quantity.setTextColor(Color.RED);
	                        } else {
	                        	prepared = "已备" + String.valueOf(prepare_count) + "次";
	                        	txt_position.setTextColor(Color.rgb(0, 127, 0));
	                        	txt_quantity.setTextColor(Color.rgb(0, 127, 0));
	                        }
	                        
	                        if (feeder == null || feeder.length() == 0) {
	                        	feeder = "未绑定";
	                        }
	                        
	                        txt_position.setText(row.getValue("position").toString() + ", " + row.getValue("item_code", "") + ", " + prepared);
	                        txt_quantity.setText(feeder + ", " + row.getValue("feeder_size", "") + ", " +App.formatNumber(row.getValue("quantity"), "0.##"));
	                        return convertView;
	                    }
	                };
	                
	                adapter.DataTable = r.Value;
	                adapter.notifyDataSetChanged();
	                new AlertDialog.Builder(pn_smt_change_editor.this.getContext())
	                .setTitle("所有站位")
	                .setSingleChoiceItems(adapter, 0, null)
	                .setNegativeButton("取消", null).show();
				}
			}
		});
	}
	
	@Override
	public void commit()
	{
		if (_task_order_row == null) {
			App.Current.toastError(this.getContext(), "未指定生产任务。");
			return;
		}
		
		if (_position_row == null) {
			App.Current.toastError(this.getContext(), "未指定站位。");
			return;
		}
		
		String operator = this.txt_worker_cell.getContentText().trim();
		if (operator == null || operator.length() == 0) {
			App.Current.toastInfo(this.getContext(), "未指定操作人员。");
			return;
		}
		
		final Long task_order_id = _task_order_row.getValue("id", 0L);
		final String machine = txt_machine_cell.getContentText().trim();
		final String program = txt_program_cell.getContentText().trim();
		final Integer position = _position_row.getValue("position", 0);
		
		Integer feeder_id = 0;
		
		String sql = "exec p_fm_smt_prepare_create ?,?,?,?,?,?";
		Parameters p = new Parameters().add(1, task_order_id).add(2, machine).add(3, program).add(4, position).add(5, feeder_id).add(6, App.Current.UserID);
		Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(pn_smt_change_editor.this.Connector, sql, p);
		if (r.HasError) {
			App.Current.toastError(pn_smt_change_editor.this.getContext(), r.Error);
			pn_smt_change_editor.this.clear();
			return;
		}
		
		if (r.Value > 0) {
			App.Current.toastInfo(pn_smt_change_editor.this.getContext(), "备料成功。");
			pn_smt_change_editor.this.clear();
			pn_smt_change_editor.this.loadCount();
		}
	}
	
	public void clear()
	{
		_position_row = null;
		_feeder_row = null;
		this.txt_item_code_cell.setContentText("");
		this.txt_item_name_cell.setContentText("");
		this.txt_quantity_cell.setContentText("");
		this.txt_position_cell.setContentText("");
		this.txt_feeder_cell.setContentText("");
	}
}
