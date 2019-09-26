package dynsoft.xone.android.wms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.control.ButtonTextCell;
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
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class pn_trial_produce_record_editor extends pn_editor {

    public pn_trial_produce_record_editor(Context context) {
        super(context);
    }

    public ButtonTextCell txt_task_order_cell;
    public TextCell txt_prod_name_cell;
    public ButtonTextCell txt_man_cell;
    public TextCell txt_product_lines_cell;
    public ListView Matrix;
    public TableAdapter Adapter;
    public ImageButton btn_clear;
    public ImageButton btn_list;

    @Override
    public void setContentView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_trial_produce_record_editor, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();

        Pane[] arr = App.Current.Workbench.GetPanes(this.Code);
        if (arr != null) {
            this.Header.setTitleText("试产巡线记录" + String.valueOf(arr.length + 1));
        }

        this.txt_task_order_cell = (ButtonTextCell) this.findViewById(R.id.txt_task_order_cell);
        this.txt_prod_name_cell = (TextCell) this.findViewById(R.id.txt_prod_name_cell);
        this.txt_man_cell = (ButtonTextCell) this.findViewById(R.id.txt_man_cell);
        this.txt_product_lines_cell = (TextCell) this.findViewById(R.id.txt_product_lines_cell);
        this.Matrix = (ListView) this.findViewById(R.id.matrix);
        this.btn_clear = (ImageButton) this.findViewById(R.id.btn_clear);
        this.btn_list = (ImageButton) this.findViewById(R.id.btn_list);

        if (this.txt_task_order_cell != null) {
            this.txt_task_order_cell.setLabelText("工单号");
            this.txt_task_order_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_ok_user"));
            this.txt_task_order_cell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(txt_task_order_cell.getContentText())) {
                        App.Current.toastInfo(getContext(), "请输入工单号");
                    } else {
                        loadTaskOrder(txt_task_order_cell.getContentText().replace("\n", ""));
                    }
                }
            });
            this.txt_task_order_cell.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    App.Current.Workbench.scanByCamera();
                    return false;
                }
            });
//            this.txt_task_order_cell.TextBox.setOnKeyListener(new OnKeyListener() {
//                @Override
//                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
//                    if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
//                        loadTaskOrder(txt_task_order_cell.getContentText().replace("\n", ""));
//                    }
//                    return false;
//                }
//            });
        }

        if (this.txt_prod_name_cell != null) {
            this.txt_prod_name_cell.setLabelText("机型");
            this.txt_prod_name_cell.setReadOnly();
        }

        if (this.txt_product_lines_cell != null) {
            this.txt_product_lines_cell.setLabelText("产线");
            this.txt_product_lines_cell.setReadOnly();
            //this.txt_reviewer_cell.setContentText(App.Current.UserName);
        }

        if (this.txt_man_cell != null) {
            this.txt_man_cell.setLabelText("人员");
//            this.txt_man_cell.TextBox.setSingleLine(true);
            this.txt_man_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_ok_user"));


            this.txt_man_cell.TextBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String name = editable.toString();
                    if(name.length() > 4) {
                        String substring = name.substring(1, name.length() - 1);
                        if (name.toUpperCase().startsWith("MZ") && name.length() == 6) {
                            loadMan(name);
                        } else if (name.toUpperCase().startsWith("M") && isInteger(substring) && name.length() == 5) {
                            loadMan(name);
                        } else {

                        }
                    }
                }
            });


            this.txt_man_cell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String userCode = pn_trial_produce_record_editor.this.txt_man_cell.TextBox.getText().toString();
                    if (userCode.equals("")) {
                        Toast.makeText(getContext(), "请输入人员编号", Toast.LENGTH_SHORT).show();
                    } else {
                        if (_task_order_row != null) {
                            loadMan(userCode);
                        } else {
                            App.Current.toastError(pn_trial_produce_record_editor.this.getContext(), "无效的条码格式。请先扫描工单");
                            App.Current.playSound(R.raw.hook);
                        }
                    }
                }
            });
        }

        if (this.Matrix != null) {
            this.Matrix.setCacheColorHint(Color.TRANSPARENT);
            //LinearLayout.LayoutParams lp_listview = new LinearLayout.LayoutParams(-1,10);
            //this.Matrix.setLayoutParams(lp_listview);
            this.Adapter = new TableAdapter(this.getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (Adapter.DataTable != null) {
                        DataRow row = (DataRow) Adapter.getItem(position);

                        if (convertView == null) {
                            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_trial_produce_record_editor_item, null);
                            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/fm_smt_index_16"));
                        }

                        TextView num = (TextView) convertView.findViewById(R.id.num);
                        TextView txt_feeder = (TextView) convertView.findViewById(R.id.txt_feeder);

                        num.setText(String.valueOf(position + 1));
//                        String feeder = row.getValue("bi_no", "") + ", " + String.valueOf(row.getValue("lot_number", 0));
                        String feeder = row.getValue("code", "") + ", " + String.valueOf(row.getValue("name", ""));
                        txt_feeder.setText(feeder);

                        return convertView;
                    }
                    return null;
                }
            };

            this.Adapter.DataTable = new DataTable();
            this.Matrix.setAdapter(Adapter);
        }

        this.btn_clear = (ImageButton) this.findViewById(R.id.btn_clear);
        if (this.btn_clear != null) {
            this.btn_clear.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_white"));
            this.btn_clear.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_trial_produce_record_editor.this.Adapter.DataTable.Rows.clear();
                    pn_trial_produce_record_editor.this.Adapter.notifyDataSetChanged();
                }
            });
        }

        _txt_item_code = new EditText(pn_trial_produce_record_editor.this.getContext());
        _item_dialog = new AlertDialog.Builder(pn_trial_produce_record_editor.this.getContext())
                .setMessage("请扫描条码")
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

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    @Override
    public void onScan(final String barcode) {
//        App.Current.Workbench.onShake();
        final String bar_code = barcode.trim();

        if (bar_code.startsWith("SX:")) {
            String task_order = bar_code.substring(3, bar_code.length());
            this.loadTaskOrder(task_order);
        } else {
            if (_task_order_row != null) {
                this.loadMan(bar_code);
            } else {
                App.Current.toastError(pn_trial_produce_record_editor.this.getContext(), "无效的条码格式。请先扫描工单");
                App.Current.playSound(R.raw.hook);
            }
        }
    }

    private DataRow _task_order_row;

    public void loadMan(String user_code1) {
        final String user_code = user_code1.trim();
        txt_man_cell.setContentText("");
        if (pn_trial_produce_record_editor.this.Adapter.DataTable != null) {
            boolean exists = false;
            for (DataRow row : pn_trial_produce_record_editor.this.Adapter.DataTable.Rows) {
                if (row.getValue("user_code", "").contains(user_code)) {
//                if (row.getValue("user_code", "").equals(user_code)) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                App.Current.toastError(pn_trial_produce_record_editor.this.getContext(), "该用户已扫描");
                App.Current.playSound(R.raw.hook);
                return;
            }
        }

        String sql = "exec p_fm_work_pilot_get_man ?";
        Parameters p = new Parameters().add(1, user_code);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            public void handleMessage(Message msg) {
                pn_trial_produce_record_editor.this.ProgressDialog.dismiss();

                final Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_trial_produce_record_editor.this.getContext(), result.Error);
                    App.Current.playSound(R.raw.hook);
                    return;
                }
                if (result.Value == null || result.Value.getValue("code", "") == null || result.Value.getValue("code", "").length() == 0) {
                    App.Current.showError(pn_trial_produce_record_editor.this.getContext(), "用户名错误或者条码不合法");
                    App.Current.playSound(R.raw.hook);
                    //_item_dialog.dismiss();
                    //_bi_wh_row =null;
                    return;
                } else if (result.Value != null && result.Value.getTable().Rows.size() > 0) {
                    if (result.Value.getTable().Rows.size() > 1) {
                        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which >= 0) {
                                    DataRow row = result.Value.getTable().Rows.get(which);
//                                    pn_trial_produce_record_editor.this.showTaskOrder(row);
                                    pn_trial_produce_record_editor.this.loadMan(row.getValue("code", ""));
                                }
                                dialog.dismiss();
                            }
                        };
                        final TableAdapter adapter = new TableAdapter(pn_trial_produce_record_editor.this.getContext()) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                DataRow row = (DataRow) result.Value.getTable().Rows.get(position);
                                if (convertView == null) {
                                    convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_trial_produce_editor_chooce_man, null);
                                    ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
                                    icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
                                }

                                TextView num = (TextView) convertView.findViewById(R.id.num);
//                                TextView txt_task_order = (TextView) convertView.findViewById(R.id.txt_task_order);
                                TextView txt_item_code = (TextView) convertView.findViewById(R.id.txt_item_code);
//                                TextView txt_item_name = (TextView) convertView.findViewById(R.id.txt_item_name);

                                num.setText(String.valueOf(position + 1));
//                                txt_task_order.setText(row.getValue("task_order_code", ""));
                                txt_item_code.setText(row.getValue("code", ""));
//                                txt_item_name.setText(row.getValue("item_name", ""));

                                return convertView;
                            }
                        };

                        adapter.DataTable = result.Value.getTable();
                        adapter.notifyDataSetChanged();

                        new AlertDialog.Builder(pn_trial_produce_record_editor.this.getContext())
                                .setTitle("选择人员编号")
                                .setSingleChoiceItems(adapter, 0, listener)
                                .setNegativeButton("取消", null).show();
                    } else {
//                        DataRow row = result.Value.getTable().Rows.get(0);
//                        pn_trial_produce_record_editor.this.showTaskOrder(row);
                        _bi_wh_row = result.Value.getTable().Rows.get(0);
                        _bi_wh_row.setValue("user_code", user_code);
                        _bi_wh_row.setValue("user_name", result.Value.getValue("name", ""));
                        _bi_wh_row.setValue("man_id", result.Value.getValue("man_id", ""));
                        pn_trial_produce_record_editor.this.Adapter.DataTable.Rows.add(_bi_wh_row);
                        pn_trial_produce_record_editor.this.Adapter.notifyDataSetChanged();
                        pn_trial_produce_record_editor.this.txt_man_cell.TextBox.setText("");
                        _bi_wh_row = null;
                    }
                } else {
                }
            }
        });
    }

    public void loadTaskOrder(String code) {
        this.ProgressDialog.show();
        String sql = "exec p_fm_work_pilot_get_task ?";
        Parameters p = new Parameters().add(1, code);
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_trial_produce_record_editor.this.ProgressDialog.dismiss();

                final Result<DataTable> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_trial_produce_record_editor.this.getContext(), result.Error);
                    return;
                }

                if (result.Value == null || result.Value.Rows.size() == 0) {
                    App.Current.showError(pn_trial_produce_record_editor.this.getContext(), "找不到生产任务或二维码无效。");
                    App.Current.playSound(R.raw.hook);
                    return;
                }

                if (result.Value != null && result.Value.Rows.size() > 0) {
                    if (result.Value.Rows.size() > 1) {
                        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which >= 0) {
                                    DataRow row = result.Value.Rows.get(which);
                                    pn_trial_produce_record_editor.this.showTaskOrder(row);
                                }
                                dialog.dismiss();
                            }
                        };

                        final TableAdapter adapter = new TableAdapter(pn_trial_produce_record_editor.this.getContext()) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                DataRow row = (DataRow) result.Value.Rows.get(position);
                                if (convertView == null) {
                                    convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_smt_task_order, null);
                                    ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
                                    icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
                                }

                                TextView num = (TextView) convertView.findViewById(R.id.num);
                                TextView txt_task_order = (TextView) convertView.findViewById(R.id.txt_task_order);
                                TextView txt_item_code = (TextView) convertView.findViewById(R.id.txt_item_code);
                                TextView txt_item_name = (TextView) convertView.findViewById(R.id.txt_item_name);

                                num.setText(String.valueOf(position + 1));
                                txt_task_order.setText(row.getValue("task_order_code", ""));
                                txt_item_code.setText(row.getValue("item_code", ""));
                                txt_item_name.setText(row.getValue("work_line", ""));

                                return convertView;
                            }
                        };

                        adapter.DataTable = result.Value;
                        adapter.notifyDataSetChanged();

                        new AlertDialog.Builder(pn_trial_produce_record_editor.this.getContext())
                                .setTitle("选择生产任务")
                                .setSingleChoiceItems(adapter, 0, listener)
                                .setNegativeButton("取消", null).show();
                    } else {
                        DataRow row = result.Value.Rows.get(0);
                        pn_trial_produce_record_editor.this.showTaskOrder(row);
                    }
                }
            }
        });
    }

    private void showTaskOrder(DataRow row) {
        _task_order_row = row;

        this.txt_task_order_cell.setContentText(row.getValue("task_order_code", ""));
        this.txt_prod_name_cell.setContentText(row.getValue("item_name", ""));
        //this.txt_man_cell.setContentText(row.getValue("user_code", ""));
        this.txt_product_lines_cell.setContentText(row.getValue("work_line", ""));
    }

    @Override
    public void commit() {
        if (_task_order_row == null) {
            App.Current.toastInfo(this.getContext(), "未指定生产任务。");
            return;
        }

        String reviewer = this.txt_product_lines_cell.getContentText().trim();
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
        for (DataRow rowx : this.Adapter.DataTable.Rows) {

            Map<String, String> entry = new HashMap<String, String>();
            entry.put("randnum", _task_order_row.getValue("randnum", ""));
            entry.put("task_order_id", String.valueOf(_task_order_row.getValue("task_order_id", 0L)));
            entry.put("create_user", App.Current.UserID);
            entry.put("user_code", rowx.getValue("user_code", ""));
            entry.put("user_name", rowx.getValue("user_name", ""));
            entry.put("man_id", String.valueOf(rowx.getValue("man_id", 0)));
            entry.put("work_line", _task_order_row.getValue("work_line", ""));
            entries.add(entry);
        }
        //生成XML数据，并传给存储过程
        String xml = XmlHelper.createXml("userdtl", null, null, "user", entries);
        String sql = "exec p_fm_work_pilot_create ?";
        Parameters p = new Parameters().add(1, xml);
        Result<Integer> rn = App.Current.DbPortal.ExecuteNonQuery(pn_trial_produce_record_editor.this.Connector, sql, p);
        if (rn.HasError) {
            App.Current.showError(pn_trial_produce_record_editor.this.getContext(), rn.Error);
            return;
        }

        App.Current.toastInfo(pn_trial_produce_record_editor.this.getContext(), "保存成功！");
        pn_trial_produce_record_editor.this.Adapter.DataTable.Rows.clear();
        pn_trial_produce_record_editor.this.Adapter.notifyDataSetChanged();
        pn_trial_produce_record_editor.this.txt_prod_name_cell.setContentText("");
        pn_trial_produce_record_editor.this.txt_task_order_cell.setContentText("");
        pn_trial_produce_record_editor.this.txt_product_lines_cell.setContentText("");
        _task_order_row = null;
        pn_trial_produce_record_editor.this.txt_man_cell.setContentText("");
    }
}
