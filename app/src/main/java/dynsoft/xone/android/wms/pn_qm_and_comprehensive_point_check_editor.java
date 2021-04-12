package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.link.Link;

/**
 * Created by Administrator on 2018/6/12.
 */

public class pn_qm_and_comprehensive_point_check_editor extends pn_editor {
    private View view;
    private TextCell text_cell_1;
    private ButtonTextCell button_text_cell_2;
    private TextCell text_cell_3;
    private ButtonTextCell button_text_cell_4;
    private ButtonTextCell button_text_cell_5;
    private ButtonTextCell text_cell_6;
    private TextCell text_cell_7;
    private TextCell text_cell_8;
    private ButtonTextCell button_text_cell_9;


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;

    private Integer id;

    public ImageButton btn_prev;
    public ImageButton btn_next;

    public pn_qm_and_comprehensive_point_check_editor(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        sharedPreferences = App.Current.Workbench.getSharedPreferences("ipqc_point_check", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();

        LayoutParams lp = new LayoutParams(-1, -1);
        view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_win_ipqc_point_check_editor_comprehensive, this, true);
        Log.e(getContext().getPackageName(), "setContentView()");
        view.setLayoutParams(lp);
        //noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();

        text_cell_1 = (TextCell) findViewById(R.id.text_cell_1);
        button_text_cell_2 = (ButtonTextCell) findViewById(R.id.button_text_cell_2);
        text_cell_3 = (TextCell) findViewById(R.id.text_cell_3);
        button_text_cell_4 = (ButtonTextCell) findViewById(R.id.button_text_cell_4);
        button_text_cell_5 = (ButtonTextCell) findViewById(R.id.button_text_cell_5);
        text_cell_6 = (ButtonTextCell) findViewById(R.id.text_cell_6);
        text_cell_7 = (TextCell) findViewById(R.id.text_cell_7);
        text_cell_8 = (TextCell) findViewById(R.id.text_cell_8);
        button_text_cell_9 = (ButtonTextCell) findViewById(R.id.button_text_cell_9);
        id = this.Parameters.get("id", 0);
        //任务单号
        String code = this.Parameters.get("code", "");


        if (text_cell_1 != null) {
            text_cell_1.setLabelText("点检编号");
            text_cell_1.setReadOnly();
        }

        if (button_text_cell_2 != null) {
            button_text_cell_2.setLabelText("点检时间");
            button_text_cell_2.setReadOnly();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
            String chooseDate = simpleDateFormat.format(new Date());
            button_text_cell_2.setContentText(chooseDate);
//            button_text_cell_2.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_gray"));
//            button_text_cell_2.Button.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    TimePickerView timePickerView = new TimePickerView(getContext(), TimePickerView.Type.YEAR_MONTH_DAY);
//                    timePickerView.setCyclic(true);
//                    timePickerView.setTime(new Date());//获取当前时间
////                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
////                    Date format = null;
////                    try {
////                        format = simpleDateFormat.parse(button_text_cell_2.getContentText());
////                    } catch (ParseException e) {
////                        e.printStackTrace();
////                    }
////                    timePickerView.setTime(format);
//
//                    timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
//                        @Override
//                        public void onTimeSelect(Date date) {
//
//                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
//                            String chooseDate = simpleDateFormat.format(date);
//                            button_text_cell_2.setContentText(chooseDate);
//                        }
//                    });
//                    timePickerView.show();
//                }
//            });
        }


        if (text_cell_3 != null) {
            text_cell_3.setLabelText("点检员");
            text_cell_3.setContentText(App.Current.UserCode);
            text_cell_3.setReadOnly();
        }


        if (button_text_cell_4 != null) {
            button_text_cell_4.setLabelText("生产任务");
            button_text_cell_4.setReadOnly();
            button_text_cell_4.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_gray"));
            button_text_cell_4.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadComfirmName(button_text_cell_4);
                }
            });
            if (!TextUtils.isEmpty(code)) {
                button_text_cell_4.setContentText(code);
            } else {
                code = sharedPreferences.getString("code", "");
                button_text_cell_4.setContentText(code);
            }
        }

        if (button_text_cell_5 != null) {
            button_text_cell_5.setLabelText("线体");
            button_text_cell_5.setReadOnly();
            button_text_cell_5.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_gray"));
            button_text_cell_5.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadWorkLineName(button_text_cell_5);
                }
            });
        }

        if (text_cell_6 != null) {
            text_cell_6.setLabelText("编号");
            this.text_cell_6.Button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    App.Current.Workbench.scanByCamera();
                }

            });
        }

        if (text_cell_7 != null) {
            text_cell_7.setLabelText("标准值");
        }
        if (text_cell_8 != null) {
            text_cell_8.setLabelText("测试值");
        }

        if (button_text_cell_9 != null) {
            button_text_cell_9.setLabelText("点检类型");
            button_text_cell_9.setReadOnly();
            button_text_cell_9.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_gray"));
            button_text_cell_9.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadCheck_type(button_text_cell_9);
                }
            });
        }

        Log.e("len", "ID: " + id);
        loadItem(id);
    }


//    private void chooseResult(final ButtonTextCell button_text_cell_4) {
//        final ArrayList<String> result = new ArrayList<String>();
//        result.add("A");
//        result.add("B");
//        result.add("C");
//
//        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (which >= 0) {
//                    String time = result.get(which);
//                    button_text_cell_4.setContentText(time);
//                }
//                dialog.dismiss();
//            }
//        };
//        new AlertDialog.Builder(pn_qm_win_ipqc_point_check_editor.this.getContext()).setTitle("请选择")
//                .setSingleChoiceItems(result.toArray(new String[0]), result.indexOf(button_text_cell_4.getContentText()), listener)
//                .setNegativeButton("取消", null).show();
//    }

    private void loadComfirmName(final ButtonTextCell button_text_cell_4) {
        Link link = new Link("pane://x:code=pn_qm_and_comprehensive_point_check_record_parameter_mgr");
//        link.Parameters.add("textcell", textcell_1);
        link.Open(null, getContext(), null);
        this.close();
    }

    private void loadWorkLineName(final ButtonTextCell text) {
        String sql = "exec p_qm_sop_work_line_items ?,?";
        Parameters p = new Parameters().add(1, App.Current.UserID).add(2, button_text_cell_4.getContentText().replace("\n", ""));
        final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (result.HasError) {
            App.Current.showError(this.getContext(), result.Error);
            return;
        }
        if (result.Value != null) {
            if (result.Value.Rows.size() == 1) {
                DataRow row = result.Value.Rows.get(0);
                button_text_cell_5.setContentText(row.getValue("name", ""));
                edit.putInt("work_line_id", row.getValue("id", 0));
                edit.commit();
            } else {
                ArrayList<String> names = new ArrayList<String>();
                for (DataRow row : result.Value.Rows) {
                    StringBuffer name = new StringBuffer();
                    name.append(row.getValue("name", ""));
                    names.add(name.toString());
                }

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which >= 0) {
                            DataRow row = result.Value.Rows.get(which);
                            StringBuffer name = new StringBuffer();
                            name.append(row.getValue("name", ""));
                            text.setContentText(name.toString());
                            button_text_cell_5.setContentText(row.getValue("name", ""));
                            edit.putInt("work_line_id", row.getValue("id", 0));
                            edit.commit();
                        }
                        dialog.dismiss();
                    }
                };
                new AlertDialog.Builder(pn_qm_and_comprehensive_point_check_editor.this.getContext()).setTitle("请选择")
                        .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                                (text.getContentText().toString()), listener)
                        .setNegativeButton("取消", null).show();
            }
        } else {
            App.Current.toastInfo(getContext(), "工单有误！");
        }
    }

    private void loadCheck_type(final ButtonTextCell text) {
        String sql = "exec p_wo_select_piont_check_type ";
        Parameters p = new Parameters();
        final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (result.HasError) {
            App.Current.showError(this.getContext(), result.Error);
            return;
        }
        if (result.Value != null) {
            if (result.Value.Rows.size() == 1) {
                DataRow row = result.Value.Rows.get(0);
                button_text_cell_9.setContentText(row.getValue("name", ""));
                edit.putInt("check_type_id", row.getValue("id", 0));
                edit.commit();
            } else {
                ArrayList<String> names = new ArrayList<String>();
                for (DataRow row : result.Value.Rows) {
                    StringBuffer name = new StringBuffer();
                    name.append(row.getValue("name", ""));
                    names.add(name.toString());
                }

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which >= 0) {
                            DataRow row = result.Value.Rows.get(which);
                            StringBuffer name = new StringBuffer();
                            name.append(row.getValue("name", ""));
                            text.setContentText(name.toString());
                            button_text_cell_9.setContentText(row.getValue("name", ""));
                            edit.putInt("work_line_id", row.getValue("id", 0));
                            edit.commit();
                        }
                        dialog.dismiss();
                    }
                };
                new AlertDialog.Builder(pn_qm_and_comprehensive_point_check_editor.this.getContext()).setTitle("请选择")
                        .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                                (text.getContentText().toString()), listener)
                        .setNegativeButton("取消", null).show();
            }
        } else {
            App.Current.toastInfo(getContext(), "选择有误！");
        }
    }

    private void loadItem(Integer id) {
        this.ProgressDialog.show();

        String sql = "exec get_qm_comprehensive_point_check_editor ?";
        Parameters p = new Parameters().add(1, id);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {

            @Override
            public void handleMessage(Message msg) {
                pn_qm_and_comprehensive_point_check_editor.this.ProgressDialog.dismiss();
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    return;
                }
                if (value.Value != null) {
                    String code = value.Value.getValue("code", "");
                    String checker = value.Value.getValue("checker", "");
                    String line_name = value.Value.getValue("line_name", "");
                    String task_code = value.Value.getValue("task_code", "");
                    String check_type = value.Value.getValue("check_type", "");

                    String checked_equip = value.Value.getValue("checked_equip", "");
                    String n_standard = value.Value.getValue("n_standard", "");
                    String n_actual = value.Value.getValue("n_actual", "");


                    Date check_date = value.Value.getValue("check_date", new Date());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String format_date = simpleDateFormat.format(check_date);

                    text_cell_1.setContentText(code);
                    button_text_cell_2.setContentText(format_date);
                    text_cell_3.setContentText(checker);
                    button_text_cell_4.setContentText(task_code);
                    button_text_cell_5.setContentText(line_name);
                    text_cell_6.setContentText(checked_equip);
                    text_cell_7.setContentText(n_standard);
                    text_cell_8.setContentText(n_actual);
                    button_text_cell_9.setContentText(check_type);

                    if (n_actual != "" && n_standard != "") {
                        if (Math.abs(Float.parseFloat(n_actual.trim()) - Float.parseFloat(n_standard.trim())) > 20.0) {
                            text_cell_8.TextBox.setTextColor(Color.RED);
                        } else text_cell_8.TextBox.setTextColor(Color.BLACK);
                    }


                }
//                else {
//                    close();
//                }
            }
        });
    }

    @Override
    public void commit() {
        super.commit();


        if (TextUtils.isEmpty(button_text_cell_5.getContentText())) {
            App.Current.toastInfo(getContext(), "请选择线体！");
            App.Current.playSound(R.raw.error);
        } else if (TextUtils.isEmpty(button_text_cell_2.getContentText())) {
            App.Current.toastInfo(getContext(), "请选择检验日期！");
            App.Current.playSound(R.raw.error);
        } else if (TextUtils.isEmpty(button_text_cell_4.getContentText())) {
            App.Current.toastInfo(getContext(), "请选择生产任务！");
            App.Current.playSound(R.raw.error);
        } else if (TextUtils.isEmpty(text_cell_6.getContentText())) {
            App.Current.toastInfo(getContext(), "请输入设备编号！");
            App.Current.playSound(R.raw.error);
        } else if (!isNumeric(text_cell_7.getContentText())) {
            App.Current.toastInfo(getContext(), "请输入标准数值");
            App.Current.playSound(R.raw.error);
        } else if (!isNumeric(text_cell_8.getContentText())) {
            App.Current.toastInfo(getContext(), "请输入测试数值");
            App.Current.playSound(R.raw.error);
        }else if (TextUtils.isEmpty(button_text_cell_9.getContentText())) {
            App.Current.toastInfo(getContext(), "请选择点检类型");
            App.Current.playSound(R.raw.error);
        } else {
            App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    String sql = "exec p_qm_and_comprehensive_point_check_create ?,?,?,?,?,?,?,?,?";
                    Parameters p = new Parameters().add(1, button_text_cell_2.getContentText()).add(2, text_cell_3.getContentText()).add(3, button_text_cell_4.getContentText())
                            .add(4, button_text_cell_5.getContentText()).add(5, text_cell_6.getContentText())
                            .add(6, text_cell_7.getContentText()).add(7, text_cell_8.getContentText())
                            .add(8, id).add(9, button_text_cell_9.getContentText());
                    App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                        @Override
                        public void handleMessage(Message msg) {
                            Result<Integer> value = Value;
                            if (value.HasError) {
                                App.Current.showError(getContext(), value.Error);
                                return;
                            }
                            if (value.Value != null) {
                                int value1 = value.Value;
                                if (value1 > 0) {
                                    App.Current.toastInfo(getContext(), "提交成功");
                                    App.Current.playSound(R.raw.pass);
                                    Log.e("len", "ID: " + id);
                                    if (id != 0) {
                                        loadItem(id);
                                    } else {
                                        close();
                                    }

                                } else {
                                    App.Current.toastInfo(getContext(), "提交失败1");
                                    App.Current.playSound(R.raw.error);
                                }
                            } else {
                                App.Current.toastInfo(getContext(), "提交失败2");
                                App.Current.playSound(R.raw.error);
                            }
                        }
                    });
                }
            });
        }
    }

    public boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
