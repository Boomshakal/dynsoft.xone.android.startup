package dynsoft.xone.android.wms;

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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class pn_smt_feed_editor extends pn_editor {

    public pn_smt_feed_editor(Context context) {
        super(context);
    }

    public TextCell txt_task_order_cell;
    public TextCell txt_prod_code_cell;
    public TextCell txt_prod_name_cell;
    public TextCell txt_machine_cell;
    public TextCell txt_program_cell;
    public TextCell txt_item_code_cell;
    public TextCell txt_item_name_cell;
    public TextCell txt_lot_number_cell;
    public TextCell txt_quantity_cell;
    public TextCell txt_feeder_cell;
    public TextCell txt_position_cell;
    public TextCell txt_out_quantity_cell;
    public TextCell txt_operator_cell;
    public ImageButton btn_list;

    @Override
    public void onPrepared() {

        super.onPrepared();

        Pane[] arr = App.Current.Workbench.GetPanes(this.Code);
        if (arr != null) {
            this.Header.setTitleText("SMT上料" + String.valueOf(arr.length + 1));
        }

        this.txt_task_order_cell = (TextCell) this.findViewById(R.id.txt_task_order_cell);
        this.txt_prod_code_cell = (TextCell) this.findViewById(R.id.txt_prod_code_cell);
        this.txt_prod_name_cell = (TextCell) this.findViewById(R.id.txt_prod_name_cell);
        this.txt_machine_cell = (TextCell) this.findViewById(R.id.txt_machine_cell);
        this.txt_program_cell = (TextCell) this.findViewById(R.id.txt_program_cell);
        this.txt_item_code_cell = (TextCell) this.findViewById(R.id.txt_item_code_cell);
        this.txt_item_name_cell = (TextCell) this.findViewById(R.id.txt_item_name_cell);
        this.txt_lot_number_cell = (TextCell) this.findViewById(R.id.txt_lot_number_cell);
        this.txt_quantity_cell = (TextCell) this.findViewById(R.id.txt_quantity_cell);
        this.txt_feeder_cell = (TextCell) this.findViewById(R.id.txt_feeder_cell);
        this.txt_operator_cell = (TextCell) this.findViewById(R.id.txt_operator_cell);
        this.txt_out_quantity_cell = (TextCell) this.findViewById(R.id.txt_out_quantity_cell);
        this.txt_position_cell = (TextCell) this.findViewById(R.id.txt_position_cell);
        this.btn_list = (ImageButton) this.findViewById(R.id.btn_list);

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

        if (this.txt_item_code_cell != null) {
            this.txt_item_code_cell.setLabelText("物料编号");
            this.txt_item_code_cell.setReadOnly();
        }

        if (this.txt_item_name_cell != null) {
            this.txt_item_name_cell.setLabelText("物料描述");
            this.txt_item_name_cell.setReadOnly();
        }

        if (this.txt_lot_number_cell != null) {
            this.txt_lot_number_cell.setLabelText("批次号");
            this.txt_lot_number_cell.setReadOnly();
        }

        if (this.txt_quantity_cell != null) {
            this.txt_quantity_cell.setLabelText("上料数量");
            this.txt_quantity_cell.setReadOnly();
        }

        if (this.txt_out_quantity_cell != null) {
            this.txt_out_quantity_cell.setLabelText("机抛数量");
        }

        if (this.txt_feeder_cell != null) {
            this.txt_feeder_cell.setLabelText("供料器");
            this.txt_feeder_cell.setReadOnly();
        }

        if (this.txt_position_cell != null) {
            this.txt_position_cell.setLabelText("站位");
            this.txt_position_cell.setReadOnly();
        }

        if (this.txt_operator_cell != null) {
            this.txt_operator_cell.setLabelText("操作员");
            this.txt_operator_cell.setReadOnly();
        }

        this.btn_list = (ImageButton) this.findViewById(R.id.btn_list);
        if (this.btn_list != null) {
            this.btn_list.setImageBitmap(App.Current.ResourceManager.getImage("@/core_list_white"));
            this.btn_list.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_smt_feed_editor.this.showList();
                }
            });
        }
    }

    @Override
    public void setContentView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_smt_feed_editor, this, true);
        view.setLayoutParams(lp);
    }

    private String _scan_quantity;

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
            if (arr.length > 0){
                String lot_number = arr[0];
                String item_code = arr[1];
                _scan_quantity = arr[2];
                if (_feeder_row ==null)
                {
                    this.loadFeeder("FD"+item_code);
                }else
                {
                    this.loadItemCode(lot_number, item_code);
                }
            }
        } else if (bar_code.startsWith("FD:")||bar_code.startsWith("SSY")||bar_code.startsWith("ZSY")) {
            String feeder = bar_code.substring(3, bar_code.length());
            if (bar_code.startsWith("SSY")||bar_code.startsWith("ZSY"))
            {
                feeder=bar_code;
            }
            this.loadFeeder(feeder);
        } else if (bar_code.startsWith("M")) {
            this.txt_operator_cell.setContentText(bar_code);
        } else {
            App.Current.showError(pn_smt_feed_editor.this.getContext(), "无效的条码格式。");
        }

    }

    private DataRow _task_order_row;

    public void loadTaskOrder(String code) {
        this.ProgressDialog.show();

        String sql = "exec p_fm_smt_prepare_get_task_order ?";

        Parameters p = new Parameters().add(1, code);
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_smt_feed_editor.this.ProgressDialog.dismiss();

                final Result<DataTable> result = this.Value;
                if (result.HasError) {
                    App.Current.toastInfo(pn_smt_feed_editor.this.getContext(), result.Error);
                    return;
                }

                if (result.Value == null || result.Value.Rows.size() == 0) {
                    App.Current.toastInfo(pn_smt_feed_editor.this.getContext(), "找不到生产任务。");
                    return;
                }

                if (result.Value != null && result.Value.Rows.size() > 0) {
                    if (result.Value.Rows.size() > 1) {
                        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which >= 0) {
                                    DataRow row = result.Value.Rows.get(which);
                                    pn_smt_feed_editor.this.showTaskOrder(row);
                                }
                                dialog.dismiss();
                            }
                        };

                        final TableAdapter adapter = new TableAdapter(pn_smt_feed_editor.this.getContext()) {
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
                                txt_task_order.setText(row.getValue("code", ""));
                                txt_task_order.setText(row.getValue("item_code", "") + ", " + App.formatNumber(row.getValue("plan_quantity"), "0.##"));
                                txt_task_order.setText(row.getValue("item_name", ""));

                                return convertView;
                            }
                        };

                        adapter.DataTable = result.Value;
                        adapter.notifyDataSetChanged();

                        new AlertDialog.Builder(pn_smt_feed_editor.this.getContext())
                                .setTitle("选择生产任务")
                                .setSingleChoiceItems(adapter, 0, listener)
                                .setNegativeButton("取消", null).show();
                    } else {
                        DataRow row = result.Value.Rows.get(0);
                        pn_smt_feed_editor.this.showTaskOrder(row);
                    }
                }
            }
        });
    }

    private void showTaskOrder(DataRow row) {
        _task_order_row = row;

        this.txt_task_order_cell.setContentText(row.getValue("code", ""));
        this.txt_prod_code_cell.setContentText(row.getValue("item_code", ""));
        this.txt_prod_name_cell.setContentText(row.getValue("item_name", ""));
        this.txt_machine_cell.setContentText("");
        this.txt_program_cell.setContentText("");
        this.txt_item_code_cell.setContentText("");
        this.txt_item_name_cell.setContentText("");
        this.txt_quantity_cell.setContentText("");
        this.txt_feeder_cell.setContentText("");
    }

    private void loadProgram(String device) {
        if (_task_order_row == null) {
            App.Current.toastInfo(this.getContext(), "未指定生产任务。");
            return;
        }

        this.txt_machine_cell.setContentText(device);

        String sql = "exec p_fm_smt_prepare_get_program ?,?";
        Parameters p = new Parameters().add(1, _task_order_row.getValue("code", "")).add(2, device);
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_smt_feed_editor.this.ProgressDialog.dismiss();

                final Result<DataTable> r = this.Value;
                if (r.HasError) {
                    App.Current.toastInfo(pn_smt_feed_editor.this.getContext(), r.Error);
                    return;
                }

                if (r.Value == null || r.Value.Rows.size() == 0) {
                    App.Current.toastInfo(pn_smt_feed_editor.this.getContext(), "找不到程序名。");
                    return;
                }

                if (r.Value != null && r.Value.Rows.size() > 0) {
                    if (r.Value.Rows.size() > 1) {
                        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which >= 0) {
                                    DataRow row = r.Value.Rows.get(which);
                                    pn_smt_feed_editor.this.showProgram(row);
                                }
                                dialog.dismiss();
                            }
                        };

                        final TableAdapter adapter = new TableAdapter(pn_smt_feed_editor.this.getContext()) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                DataRow row = (DataRow) r.Value.Rows.get(position);
                                if (convertView == null) {
                                    convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_smt_program, null);
                                    ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
                                    icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
                                }

                                TextView num = (TextView) convertView.findViewById(R.id.num);
                                TextView txt_program = (TextView) convertView.findViewById(R.id.txt_program);

                                num.setText(String.valueOf(position + 1));
                                txt_program.setText(row.getValue("program", ""));

                                return convertView;
                            }
                        };

                        adapter.DataTable = r.Value;
                        adapter.notifyDataSetChanged();

                        new AlertDialog.Builder(pn_smt_feed_editor.this.getContext())
                                .setTitle("选择程序名称")
                                .setSingleChoiceItems(adapter, 0, listener)
                                .setNegativeButton("取消", null).show();
                    } else {
                        DataRow row = r.Value.Rows.get(0);
                        pn_smt_feed_editor.this.showProgram(row);
                    }
                }

            }
        });
    }

    private DataRow _program_row;

    private void showProgram(DataRow row) {
        _program_row = row;

        this.txt_program_cell.setContentText(row.getValue("program", ""));
        this.txt_item_code_cell.setContentText("");
        this.txt_item_name_cell.setContentText("");
        this.txt_quantity_cell.setContentText("");
        this.txt_feeder_cell.setContentText("");

    }

    private DataRow _feeder_row;

    public void loadFeeder(String code) {
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
        String sql = "exec p_fm_smt_feed_get_feeder_v1 ?,?,?,?";
        Parameters p = new Parameters().add(1, task_order_id).add(2, machine).add(3, program).add(4, code);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_smt_feed_editor.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.playSound(R.raw.hook);
                    App.Current.toastError(pn_smt_feed_editor.this.getContext(), result.Error);
                    return;
                }

                _feeder_row = result.Value;
                if (_feeder_row == null) {
                    App.Current.playSound(R.raw.hook);
                    pn_smt_feed_editor.this.txt_feeder_cell.setContentText("");
                    App.Current.toastInfo(pn_smt_feed_editor.this.getContext(), "供料器编号不存在。");
                    return;
                }

                pn_smt_feed_editor.this.txt_feeder_cell.setContentText(_feeder_row.getValue("feeder_code", ""));
                String sub_position = _feeder_row.getValue("sub_position", "");
                if (sub_position.equals("Z")) {
                    pn_smt_feed_editor.this.txt_position_cell.setContentText(String.valueOf(_feeder_row.getValue("position", 0)) + ", " + _feeder_row.getValue("item_code", ""));
                } else {
                    pn_smt_feed_editor.this.txt_position_cell.setContentText(String.valueOf(_feeder_row.getValue("position", 0)) + "-" + sub_position + ", " + _feeder_row.getValue("item_code", ""));
                }

            }
        });
    }

    private DataRow _item_row;

    public void loadItemCode(String lot_number, String item_code) {
        if (_task_order_row == null) {
            App.Current.toastInfo(this.getContext(), "未指定生产任务。");
            return;
        }

        if (_feeder_row == null) {

            App.Current.toastInfo(this.getContext(), "请先扫描供料器条码。");
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
        int feeder_id = _feeder_row.getValue("feeder_id", 0);

        String sql = "exec p_fm_smt_feed_get_item_code ?,?,?,?,?,?";
        Parameters p = new Parameters().add(1, task_order_id).add(2, machine).add(3, program).add(4, feeder_id).add(5, item_code).add(6, lot_number);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_smt_feed_editor.this.ProgressDialog.dismiss();

                final Result<DataRow> r = this.Value;
                if (r.HasError) {
                    App.Current.toastError(pn_smt_feed_editor.this.getContext(), r.Error);
                    pn_smt_feed_editor.this.txt_item_code_cell.setContentText("");
                    pn_smt_feed_editor.this.txt_item_name_cell.setContentText("");
                    pn_smt_feed_editor.this.txt_lot_number_cell.setContentText("");
                    pn_smt_feed_editor.this.txt_quantity_cell.setContentText("");
                    _item_row = null;
                    return;
                }

                _item_row = r.Value;
                if (_item_row == null) {
                    App.Current.toastInfo(pn_smt_feed_editor.this.getContext(), "未找到任何数据。");
                    return;
                }

                int feed_count = _item_row.getValue("feed_count", 0);
                if (feed_count > 0) {
                    App.Current.toastError(pn_smt_feed_editor.this.getContext(), "该批次已有" + String.valueOf(feed_count) + "次上料扫描记录。");
                }

                pn_smt_feed_editor.this.txt_item_code_cell.setContentText(_item_row.getValue("item_code", ""));
                pn_smt_feed_editor.this.txt_item_name_cell.setContentText(_item_row.getValue("item_name", ""));
                pn_smt_feed_editor.this.txt_lot_number_cell.setContentText(_item_row.getValue("lot_number", ""));
                pn_smt_feed_editor.this.txt_quantity_cell.setContentText(_scan_quantity);
            }
        });
    }

    ///显示所有站位表及其扫描记录
    public void showList() {
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
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_smt_feed_editor.this.ProgressDialog.dismiss();

                final Result<DataTable> r = this.Value;
                if (r.HasError) {
                    App.Current.showError(pn_smt_feed_editor.this.getContext(), r.Error);
                    return;
                }

                if (r.Value != null && r.Value.Rows.size() > 0) {
                    final TableAdapter adapter = new TableAdapter(pn_smt_feed_editor.this.getContext()) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            DataRow row = (DataRow) r.Value.Rows.get(position);
                            if (convertView == null) {
                                convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_smt_position, null);
                                ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
                                icon.setImageBitmap(App.Current.ResourceManager.getImage("@/fm_smt_index_16"));
                            }

                            TextView num = (TextView) convertView.findViewById(R.id.num);
                            TextView txt_position = (TextView) convertView.findViewById(R.id.txt_position);
                            TextView txt_quantity = (TextView) convertView.findViewById(R.id.txt_quantity);

                            num.setText(String.valueOf(position + 1));

                            String prepared = "";
                            String feeder = row.getValue("feeder_code", "");
                            int prepare_count = row.getValue("prepare_count", 0);
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
                            txt_quantity.setText(feeder + ", " + row.getValue("feeder_size", "") + ", " + App.formatNumber(row.getValue("quantity"), "0.##"));
                            return convertView;
                        }
                    };

                    adapter.DataTable = r.Value;
                    adapter.notifyDataSetChanged();
                    new AlertDialog.Builder(pn_smt_feed_editor.this.getContext())
                            .setTitle("所有站位")
                            .setSingleChoiceItems(adapter, 0, null)
                            .setNegativeButton("取消", null).show();
                }
            }
        });
    }

    @Override
    public void commit() {
        if (_task_order_row == null) {
            App.Current.toastInfo(this.getContext(), "未指定生产任务。");
            return;
        }

        if (_program_row == null) {
            App.Current.toastInfo(this.getContext(), "未指定程序名。");
            return;
        }

        if (_feeder_row == null) {
            App.Current.toastInfo(this.getContext(), "未指定供料器。");
            return;
        }

        if (_item_row == null) {
            App.Current.toastInfo(this.getContext(), "未指定物料。");
            return;
        }

        final String operator = this.txt_operator_cell.getContentText().trim();
        if (operator == null || operator.length() == 0) {
            App.Current.toastInfo(this.getContext(), "未指定操作人员。");
            return;
        }

        final Long task_order_id = _task_order_row.getValue("id", 0L);
        final String machine = txt_machine_cell.getContentText().trim();
        final String program = txt_program_cell.getContentText().trim();
        final Integer feeder_id = _feeder_row.getValue("feeder_id", 0);
        final String item_code = _item_row.getValue("item_code", "");
        final String lot_number = _item_row.getValue("lot_number", "");
        final Integer position = _feeder_row.getValue("position", 0);
        String sub_position = _feeder_row.getValue("sub_position", "");

        String csql = "exec p_fm_smt_check_feed_direction_v1 ?,?,?,?,?";
        Parameters p1 = new Parameters().add(1, task_order_id).add(2, machine).add(3, program).add(4, position).add(5, sub_position);
        Result<DataRow> result = App.Current.DbPortal.ExecuteRecord(this.Connector, csql, p1);
        if (result.HasError) {
            App.Current.showError(pn_smt_feed_editor.this.getContext(), result.Error);
            return;
        }
        if (result.Value != null) {
            String feeder_size = result.Value.getValue("feeder_size", "");
            if (feeder_size != "" && feeder_size != null) {
                App.Current.question(pn_smt_feed_editor.this.getContext(), "请确认备料方向：" + feeder_size, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String sql = "exec p_fm_smt_feed_create ?,?,?,?,?,?,?,?,?,?";
                        Parameters p = new Parameters().add(1, App.Current.UserID).add(2, task_order_id).add(3, machine).add(4, program).add(5, feeder_id)
                                .add(6, item_code).add(7, lot_number).add(8, _scan_quantity).add(9, operator).add(10, txt_out_quantity_cell.getContentText());
                        Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(pn_smt_feed_editor.this.Connector, sql, p);
                        if (r.HasError) {
                            App.Current.toastError(pn_smt_feed_editor.this.getContext(), r.Error);
                            pn_smt_feed_editor.this.clear();
                            return;
                        }

                        if (r.Value > 0) {
                            App.Current.toastInfo(pn_smt_feed_editor.this.getContext(), "上料成功。");
                            pn_smt_feed_editor.this.clear();
                        }
                    }
                });
            }
        } else {
            String sql = "exec p_fm_smt_feed_create ?,?,?,?,?,?,?,?,?,?";
            Parameters p = new Parameters().add(1, App.Current.UserID).add(2, task_order_id).add(3, machine).add(4, program).add(5, feeder_id)
                    .add(6, item_code).add(7, lot_number).add(8, _scan_quantity).add(9, operator).add(10, txt_out_quantity_cell.getContentText());
            Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(pn_smt_feed_editor.this.Connector, sql, p);
            if (r.HasError) {
                App.Current.toastError(pn_smt_feed_editor.this.getContext(), r.Error);
                pn_smt_feed_editor.this.clear();
                return;
            }

            if (r.Value > 0) {
                App.Current.toastInfo(pn_smt_feed_editor.this.getContext(), "上料成功。");
                pn_smt_feed_editor.this.clear();
            }
        }


    }

    public void clear() {
        _item_row = null;
        _feeder_row = null;
        pn_smt_feed_editor.this.txt_feeder_cell.setContentText("");
        pn_smt_feed_editor.this.txt_item_code_cell.setContentText("");
        pn_smt_feed_editor.this.txt_item_name_cell.setContentText("");
        pn_smt_feed_editor.this.txt_quantity_cell.setContentText("");
        pn_smt_feed_editor.this.txt_operator_cell.setContentText("");
    }
}
