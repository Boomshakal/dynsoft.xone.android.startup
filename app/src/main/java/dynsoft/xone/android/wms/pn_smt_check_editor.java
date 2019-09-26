package dynsoft.xone.android.wms;


import java.util.ArrayList;
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

public class pn_smt_check_editor extends pn_editor {

	public pn_smt_check_editor(Context context) {
		super(context);
	}

	public TextCell txt_task_order_cell;
	public TextCell txt_prod_name_cell;
	public TextCell txt_machine_cell;
	public TextCell txt_program_cell;
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
			this.Header.setTitleText("SMT复核"+String.valueOf(arr.length + 1));
		}
		
		this.txt_task_order_cell = (TextCell)this.findViewById(R.id.txt_task_order_cell);
		this.txt_prod_name_cell = (TextCell)this.findViewById(R.id.txt_prod_name_cell);
		this.txt_machine_cell = (TextCell)this.findViewById(R.id.txt_machine_cell);
		this.txt_program_cell = (TextCell)this.findViewById(R.id.txt_program_cell);
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
		
		if (this.txt_program_cell != null) {
			this.txt_program_cell.setLabelText("程序名称");
			this.txt_program_cell.setReadOnly();
		}
		
		if (this.txt_reviewer_cell != null) {
			this.txt_reviewer_cell.setLabelText("复核人");
			this.txt_reviewer_cell.setReadOnly();
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
                    		convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_smt_check, null);
                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/fm_smt_index_16"));
                    	}
                        
                        TextView num = (TextView)convertView.findViewById(R.id.num);
                        TextView txt_feeder = (TextView)convertView.findViewById(R.id.txt_feeder);
                        
                        num.setText(String.valueOf(position + 1));
                        String feeder = row.getValue("code", "") + ", " + String.valueOf(row.getValue("position", 0)) + ", " + row.getValue("item_code", "");
                        txt_feeder.setText(feeder);

                        return convertView;
                    }
                    return null;
                }
            };
            
            this.Adapter.DataTable = new DataTable();
            this.Matrix.setAdapter(Adapter);
        }
		
		this.btn_list = (ImageButton)this.findViewById(R.id.btn_list);
		if (this.btn_list != null){
			this.btn_list.setImageBitmap(App.Current.ResourceManager.getImage("@/core_list_white"));
			this.btn_list.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_smt_check_editor.this.showList();
				}
			});
		}
		
		this.btn_clear = (ImageButton)this.findViewById(R.id.btn_clear);
		if (this.btn_clear != null){
			this.btn_clear.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_white"));
			this.btn_clear.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_smt_check_editor.this.Adapter.DataTable.Rows.clear();
					pn_smt_check_editor.this.Adapter.notifyDataSetChanged();
				}
			});
		}
		
		_txt_item_code = new EditText(pn_smt_check_editor.this.getContext());
		_item_dialog = new AlertDialog.Builder(pn_smt_check_editor.this.getContext())
		.setMessage("请扫描物料批次条码")
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
	
	private DataRow _program_row;
	private DataRow _feeder_row;
	private AlertDialog _item_dialog;
	private EditText _txt_item_code;
	
	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_smt_check_editor, this, true);
        view.setLayoutParams(lp);
	}

	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		
		if (bar_code.startsWith("WO:")){
			String task_order = bar_code.substring(3, bar_code.length());
			this.loadTaskOrder(task_order);
		} else if (bar_code.startsWith("B") && bar_code.length() > 20) {
			String device = bar_code.substring(20, bar_code.length());
			if (device.length() > 4 && device.startsWith("DEV:")) {
				device = device.substring(4, device.length());
				this.loadProgram(device);
			}
		} else if (bar_code.startsWith("CRQ:") || bar_code.startsWith("CQR:")) {
			String str = bar_code.substring(4, bar_code.length());
			String[] arr = str.split("-");
			String _scan_item_code = arr[1];
			if (_feeder_row != null) {
				if (arr.length > 1) {
					String item_code = _feeder_row.getValue("item_code", "");
					if (_scan_item_code.equals(item_code) == false) {
						App.Current.playSound(R.raw.hook);
						String error = "扫描的物料为<" + _scan_item_code + ">，与备料指定物料的不一致。";
						App.Current.toastError(pn_smt_check_editor.this.getContext(), error);
						
						pn_smt_check_editor.this.Adapter.DataTable.Rows.remove(_feeder_row);
						pn_smt_check_editor.this.Adapter.notifyDataSetChanged();
						_item_dialog.dismiss();
						_feeder_row =null;
					}else
					{
						String csql="exec p_fm_smt_check_feed_direction_v1 ?,?,?,?,?";
						final Long task_order_id = _task_order_row.getValue("id", 0L);
						final String program =_program_row.getValue("program","");
						final String machine =this.txt_machine_cell.getContentText();
						final Integer position =_feeder_row.getValue("position",0);
						String sub_position =_feeder_row.getValue("sub_position","");
						Parameters p1 =new Parameters().add(1, task_order_id).add(2, machine).add(3, program).add(4, position).add(5, sub_position);
						Result<DataRow> result=App.Current.DbPortal.ExecuteRecord(this.Connector, csql, p1);
						if (result.HasError) {
							App.Current.showError(pn_smt_check_editor.this.getContext(), result.Error);
							return;
						}
						if (result.Value!=null)
						{
						   String feeder_size =result.Value.getValue("feeder_size","");
							if (feeder_size!="" && feeder_size!=null)
						   {
								App.Current.question(pn_smt_check_editor.this.getContext(),"请确认备料方向："+feeder_size , new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface dialog, int which) {
										_item_dialog.dismiss();
										_feeder_row =null;
									}
						      });
						    }
					  
					      } else
					       {
						    _item_dialog.dismiss();
						    _feeder_row =null;
					       }
				 }
			}
			}
				else 
			{
				this.loadFeeder("FD"+_scan_item_code);
			}
		
		}else if (bar_code.startsWith("DEV:")) {
			String device = bar_code.substring(4, bar_code.length());
			this.loadProgram(device);
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
				this.loadFeeder(feeder);
			}
		} else if (bar_code.startsWith("M")) {
			if (_item_dialog.isShowing() == false) {
				this.txt_reviewer_cell.setContentText(bar_code);
			}
		} 
		
		else
		   {
			App.Current.showError(pn_smt_check_editor.this.getContext(), "无效的条码格式。");
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
				pn_smt_check_editor.this.ProgressDialog.dismiss();
				
				final Result<DataTable> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_smt_check_editor.this.getContext(), result.Error);
					return;
				}
				
				if (result.Value == null || result.Value.Rows.size() == 0) {
					App.Current.showError(pn_smt_check_editor.this.getContext(), "找不到生产任务。");
					return;
				}
				
				if (result.Value != null && result.Value.Rows.size() > 0) {
					if (result.Value.Rows.size() > 1) {
						DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		                        if (which >= 0) {
		                        	DataRow row = result.Value.Rows.get(which);
		                        	pn_smt_check_editor.this.showTaskOrder(row);
		                        }
		                        dialog.dismiss();
		                    }
		                };

		                final TableAdapter adapter = new TableAdapter(pn_smt_check_editor.this.getContext()) {
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
		                
		                new AlertDialog.Builder(pn_smt_check_editor.this.getContext())
		                .setTitle("选择生产任务")
		                .setSingleChoiceItems(adapter, 0, listener)
		                .setNegativeButton("取消", null).show();
					} else {
						DataRow row = result.Value.Rows.get(0);
						pn_smt_check_editor.this.showTaskOrder(row);
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
		this.txt_machine_cell.setContentText("");
		this.txt_program_cell.setContentText("");
	}
	
	private void loadProgram(String device)
	{
		if (_task_order_row == null) {
			App.Current.toastInfo(this.getContext(), "未指定生产任务。");
			return;
		}
		
		this.txt_machine_cell.setContentText(device);
		
		String sql = "exec p_fm_smt_prepare_get_program ?,?";
		Parameters p = new Parameters().add(1, _task_order_row.getValue("code", "")).add(2, device);
		App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_smt_check_editor.this.ProgressDialog.dismiss();
				
				final Result<DataTable> r = this.Value;
				if (r.HasError) {
					App.Current.showError(pn_smt_check_editor.this.getContext(), r.Error);
					return;
				}
				
				if (r.Value == null || r.Value.Rows.size() == 0) {
					App.Current.showError(pn_smt_check_editor.this.getContext(), "找不到程序名。");
					return;
				}
				
				if (r.Value != null && r.Value.Rows.size() > 0) {
					if (r.Value.Rows.size() > 1) {
		                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		                        if (which >= 0) {
		                        	DataRow row = r.Value.Rows.get(which);
		                        	pn_smt_check_editor.this.showProgram(row);
		                        }
		                        dialog.dismiss();
		                    }
		                };

		                final TableAdapter adapter = new TableAdapter(pn_smt_check_editor.this.getContext()) {
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
		                
		                new AlertDialog.Builder(pn_smt_check_editor.this.getContext())
		                .setTitle("选择程序名称")
		                .setSingleChoiceItems(adapter, 0, listener)
		                .setNegativeButton("取消", null).show();
					} else {
						DataRow row = r.Value.Rows.get(0);
						pn_smt_check_editor.this.showProgram(row);
					}
				}
				
			}
		});
	}

	private void showProgram(DataRow row)
	{
		_program_row = row;
		this.txt_program_cell.setContentText(_program_row.getValue("program", ""));
	}
	
	public void loadFeeder(String code)
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
		String sql = "exec p_fm_smt_check_get_feeder_v1 ?,?,?,?";
		Parameters p = new Parameters().add(1, task_order_id).add(2, machine).add(3, program).add(4, code);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_smt_check_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.toastInfo(pn_smt_check_editor.this.getContext(), result.Error);
					return;
				}
				
				_feeder_row = result.Value;
				if (_feeder_row == null) {
					App.Current.playSound(R.raw.hook);
					App.Current.toastError(pn_smt_check_editor.this.getContext(), "供料器编号不存在。");
					if (_item_dialog.isShowing() != false) {
						_item_dialog.dismiss();
					}
					return;
				}
				
				if (_feeder_row.getValue("expired", "").equals("Y")) {
					App.Current.playSound(R.raw.hook);
					App.Current.toastError(pn_smt_check_editor.this.getContext(), "供料器编号已过有效期。");
					return;
				}
				
				int position = _feeder_row.getValue("position", 0);
				if (position == 0) {
					App.Current.playSound(R.raw.hook);
					App.Current.toastError(pn_smt_check_editor.this.getContext(), "供料器没有备料。");
					return;
				}
				
				if (pn_smt_check_editor.this.Adapter.DataTable != null) {
					boolean exists = false;
					for (DataRow row : pn_smt_check_editor.this.Adapter.DataTable.Rows) {
						if (row.getValue("code", "").equals(_feeder_row.getValue("code", ""))) {
							exists = true;
							break;
						}
					 }
					
					if (exists) {
						App.Current.toastInfo(pn_smt_check_editor.this.getContext(), "该供料器已经扫描。");
						return;
					}
					
					pn_smt_check_editor.this.Adapter.DataTable.Rows.add(_feeder_row);
					pn_smt_check_editor.this.Adapter.notifyDataSetChanged();
					
					if (check(false) == false) {
						pn_smt_check_editor.this.Adapter.DataTable.Rows.remove(_feeder_row);
						pn_smt_check_editor.this.Adapter.notifyDataSetChanged();
						_feeder_row=null;
						return;
					}
					
					_item_dialog.show();
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
				pn_smt_check_editor.this.ProgressDialog.dismiss();
				
				final Result<DataTable> r = this.Value;
				if (r.HasError) {
					App.Current.showError(pn_smt_check_editor.this.getContext(), r.Error);
					return;
				}
				
				if (r.Value != null && r.Value.Rows.size() > 0) {
	                final TableAdapter adapter = new TableAdapter(pn_smt_check_editor.this.getContext()) {
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
	                        String sub_position =row.getValue("sub_position","");
	                        if (sub_position.equals("Z"))
	                        {
	                        txt_position.setText(row.getValue("position").toString() + ", " + row.getValue("item_code", "") + ", " + prepared);
	                        }else
	                        {
	                        	txt_position.setText(row.getValue("position").toString() +"-"+sub_position+ ", " + row.getValue("item_code", "") + ", " + prepared);	
	                        }
	                        txt_quantity.setText(feeder + ", " + row.getValue("feeder_size", "") + ", " +App.formatNumber(row.getValue("quantity"), "0.##"));
	                        return convertView;
	                    }
	                };
	                
	                adapter.DataTable = r.Value;
	                adapter.notifyDataSetChanged();
	                new AlertDialog.Builder(pn_smt_check_editor.this.getContext())
	                .setTitle("所有站位")
	                .setSingleChoiceItems(adapter, 0, null)
	                .setNegativeButton("取消", null).show();
				}
			}
		});
	}
	
	public boolean check(boolean checkAll)
	{
		if (_task_order_row == null) {
			App.Current.toastInfo(this.getContext(), "未指定生产任务。");
			return false;
		}
		
		String reviewer = this.txt_reviewer_cell.getContentText().trim();
		if (reviewer == null || reviewer.length() == 0) {
			App.Current.toastInfo(this.getContext(), "未指定复核人员。");
			return false;
		}
		
		final Long task_order_id = _task_order_row.getValue("id", 0L);
		final String machine = txt_machine_cell.getContentText().trim();
		final String program = txt_program_cell.getContentText().trim();
		
		String sql = "exec p_fm_smt_check_get_prepared_feeders_v1 ?,?,?";
		Parameters p = new Parameters().add(1, task_order_id).add(2, machine).add(3, program);
		Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(pn_smt_check_editor.this.Connector, sql, p);
		if (r.HasError) {
			App.Current.showError(pn_smt_check_editor.this.getContext(), r.Error);
			return false;
		}
		
		String error = "";
		
		final ArrayList<String> prepared_feeders = new ArrayList<String>();
		final ArrayList<String> prepared_items = new ArrayList<String>();
		for (DataRow row : r.Value.Rows) {
			String feeder_code = row.getValue("code", "");
			if (feeder_code != null && feeder_code.length() > 0) {
				prepared_feeders.add(feeder_code);
				prepared_items.add(row.getValue("item_code", ""));
			}
		}
		
		final ArrayList<String> scan_feeders = new ArrayList<String>();
		final ArrayList<String> scan_items = new ArrayList<String>();
		for (DataRow row : this.Adapter.DataTable.Rows) {
			scan_feeders.add(row.getValue("code", ""));
			scan_items.add(row.getValue("item_code", ""));
		}
		
		for (int i = 0; i< scan_feeders.size(); i++) {
			if (scan_feeders.get(i).equals(prepared_feeders.get(i)) == false) {
				App.Current.playSound(R.raw.hook);
				error = "扫描的第" + String.valueOf(i+1) + "个飞达" + scan_feeders.get(i) + "与备料扫描的不一致。"+prepared_feeders.get(i);
				App.Current.toastError(pn_smt_check_editor.this.getContext(), error);
				//_feeder_row =null;
				return false;
			}
		}
		
		if (checkAll) {
			if (scan_feeders.size() != prepared_feeders.size()) {
				App.Current.playSound(R.raw.hook);
				error = "复核扫描的飞达个数("+String.valueOf(scan_feeders.size())+")与备料扫描个数("+String.valueOf(prepared_feeders.size())+")不一致。";
				App.Current.toastError(pn_smt_check_editor.this.getContext(), error);
				return false;
			}
		}
		
//		for (int i = 0; i< scan_items.size(); i++) {
//			if (scan_items.get(i).equals(prepared_items.get(i)) == false) {
//				App.Current.playSound(R.raw.hook);
//				error = "扫描的第" + String.valueOf(i+1) + "个物料" + prepared_items.get(i) + "与站位表的物料不一致。";
//				App.Current.showError(pn_smt_check_editor.this.getContext(), error);
//				return false;
//			}
//		}
		
		return true;
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
			App.Current.toastInfo(this.getContext(), "没有扫描供料器。");
			return;
		}
		
		final Long task_order_id = _task_order_row.getValue("id", 0L);
		final String machine = txt_machine_cell.getContentText().trim();
		final String program = txt_program_cell.getContentText().trim();
		
		if (check(true)) {
			String sql = "exec p_fm_smt_review_create ?,?,?,?,?,?";
			Parameters p = new Parameters().add(1, task_order_id).add(2, machine).add(3, program).add(4, App.Current.UserID).add(5, "OK").add(6, reviewer);
			Result<Integer> rn = App.Current.DbPortal.ExecuteNonQuery(pn_smt_check_editor.this.Connector, sql, p);
			if (rn.HasError) {
				App.Current.showError(pn_smt_check_editor.this.getContext(), rn.Error);
				return;
			}
			
			App.Current.toastInfo(pn_smt_check_editor.this.getContext(), "检查OK！");
			pn_smt_check_editor.this.Adapter.DataTable.Rows.clear();
			pn_smt_check_editor.this.Adapter.notifyDataSetChanged();
			pn_smt_check_editor.this.txt_reviewer_cell.setContentText("");
			
		}
	}
}
