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

import com.bigkoo.pickerview.TimePickerView;

import java.math.BigDecimal;
import java.text.ParseException;
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

public class pn_qm_and_ipqc_point_check_editor extends pn_editor {
    private View view;
    private TextCell text_cell_1;
    private ButtonTextCell button_text_cell_2;
    private TextCell text_cell_3;
    private ButtonTextCell button_text_cell_4;
    private ButtonTextCell button_text_cell_5;
    private ButtonTextCell text_cell_6;
    private TextCell text_cell_7;
    private TextCell text_cell_8;
    private TextCell text_cell_9;
    private TextCell text_cell_10;
    private TextCell _text_cell_7;
    private TextCell _text_cell_8;
    private TextCell _text_cell_9;
    private TextCell _text_cell_10;


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;

    private Integer id;

    public ImageButton btn_prev;
    public ImageButton btn_next;

    public pn_qm_and_ipqc_point_check_editor(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        sharedPreferences = App.Current.Workbench.getSharedPreferences("ipqc_point_check", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();

        LayoutParams lp = new LayoutParams(-1, -1);
        view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_win_ipqc_point_check_editor, this, true);
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
        text_cell_9 = (TextCell) findViewById(R.id.text_cell_9);
        text_cell_10 = (TextCell) findViewById(R.id.text_cell_10);
        _text_cell_7 = (TextCell) findViewById(R.id._text_cell_7);
        _text_cell_8 = (TextCell) findViewById(R.id._text_cell_8);
        _text_cell_9 = (TextCell) findViewById(R.id._text_cell_9);
        _text_cell_10 = (TextCell) findViewById(R.id._text_cell_10);

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
            button_text_cell_2.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_gray"));
            button_text_cell_2.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimePickerView timePickerView = new TimePickerView(getContext(), TimePickerView.Type.YEAR_MONTH_DAY);
                    timePickerView.setCyclic(true);
                    timePickerView.setTime(new Date());//获取当前时间
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
//                    Date format = null;
//                    try {
//                        format = simpleDateFormat.parse(button_text_cell_2.getContentText());
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    timePickerView.setTime(format);

                    timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date) {

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                            String chooseDate = simpleDateFormat.format(date);
                            button_text_cell_2.setContentText(chooseDate);
                        }
                    });
                    timePickerView.show();
                }
            });
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
            text_cell_6.setLabelText("设备编号");
            this.text_cell_6.Button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    App.Current.Workbench.scanByCamera();
                }

            });
        }

        if (text_cell_7 != null) {
            text_cell_7.setLabelText("泄漏电流0.45±0.045mA");
        }
        if (_text_cell_7 != null) {
            _text_cell_7.setLabelText("泄漏电流0.55±0.055mA");
        }
        if (text_cell_8 != null) {
            text_cell_8.setLabelText("绝缘电阻55±5.5MΩ");
        }
        if (_text_cell_8 != null) {
            _text_cell_8.setLabelText("绝缘电阻45±4.5MΩ");
        }
        if (text_cell_9 != null) {
            text_cell_9.setLabelText("耐压电流  0.8±0.08mA");
        }
        if (_text_cell_9 != null) {
            _text_cell_9.setLabelText("耐压电流  1.2±0.12mA");
        }
        if (text_cell_10 != null) {
            text_cell_10.setLabelText("接地电阻  60±6mΩ");
        }
        if (_text_cell_10 != null) {
            _text_cell_10.setLabelText("接地电阻  100±10mΩ");
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
        Link link = new Link("pane://x:code=pn_qm_and_ipqc_point_check_record_parameter_mgr");
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
                new AlertDialog.Builder(pn_qm_and_ipqc_point_check_editor.this.getContext()).setTitle("请选择")
                        .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                                (text.getContentText().toString()), listener)
                        .setNegativeButton("取消", null).show();
            }
        } else {
            App.Current.toastInfo(getContext(), "工单有误！");
        }
    }

    private void loadItem(Integer id) {
        this.ProgressDialog.show();

        String sql = "exec get_qm_ipqc_point_check_editor_safety ?";
        Parameters p = new Parameters().add(1, id);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {

            @Override
            public void handleMessage(Message msg) {
                pn_qm_and_ipqc_point_check_editor.this.ProgressDialog.dismiss();
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

                    String checked_equip = value.Value.getValue("checked_equip", "");
                    BigDecimal v_actual = value.Value.getValue("v_actual", BigDecimal.ZERO);
                    BigDecimal r_actual = value.Value.getValue("r_actual", BigDecimal.ZERO);
                    BigDecimal a_actual = value.Value.getValue("a_actual", BigDecimal.ZERO);
                    BigDecimal mr_actual = value.Value.getValue("mr_actual", BigDecimal.ZERO);
                    BigDecimal _v_actual = value.Value.getValue("_v_actual", BigDecimal.ZERO);
                    BigDecimal _r_actual = value.Value.getValue("_r_actual", BigDecimal.ZERO);
                    BigDecimal _a_actual = value.Value.getValue("_a_actual", BigDecimal.ZERO);
                    BigDecimal _mr_actual = value.Value.getValue("_mr_actual", BigDecimal.ZERO);


                    Date check_date = value.Value.getValue("check_date", new Date());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String format_date = simpleDateFormat.format(check_date);

                    text_cell_1.setContentText(code);
                    button_text_cell_2.setContentText(format_date);
                    text_cell_3.setContentText(checker);
                    button_text_cell_4.setContentText(task_code);
                    button_text_cell_5.setContentText(line_name);
                    text_cell_6.setContentText(checked_equip);

                    if (a_actual.compareTo(new BigDecimal(0.495)) == 1 || a_actual.compareTo(new BigDecimal(0.405)) == -1) {
                        text_cell_7.TextBox.setTextColor(android.graphics.Color.RED);
                    } else if (a_actual.compareTo(BigDecimal.ZERO) == 0) {
                        text_cell_7.TextBox.setTextColor(Color.BLACK);
                    } else {
                        text_cell_7.TextBox.setTextColor(Color.BLACK);
                    }
                    if (_a_actual.compareTo(new BigDecimal(0.605)) == 1 || _a_actual.compareTo(new BigDecimal(0.495)) == -1) {
                        _text_cell_7.TextBox.setTextColor(android.graphics.Color.RED);
                    } else if (_a_actual.compareTo(BigDecimal.ZERO) == 0) {
                        _text_cell_7.TextBox.setTextColor(Color.BLACK);
                    } else {
                        _text_cell_7.TextBox.setTextColor(Color.BLACK);
                    }
                    if (r_actual.compareTo(new BigDecimal(60.5)) == 1 || r_actual.compareTo(new BigDecimal(49.5)) == -1) {
                        text_cell_8.TextBox.setTextColor(android.graphics.Color.RED);
                    } else if (r_actual.compareTo(BigDecimal.ZERO) == 0) {
                        text_cell_8.TextBox.setTextColor(Color.BLACK);
                    } else {
                        text_cell_8.TextBox.setTextColor(Color.BLACK);
                    }
                    if (_r_actual.compareTo(new BigDecimal(49.5)) == 1 || _r_actual.compareTo(new BigDecimal(40.5)) == -1) {
                        _text_cell_8.TextBox.setTextColor(android.graphics.Color.RED);
                    } else if (_r_actual.compareTo(BigDecimal.ZERO) == 0) {
                        _text_cell_8.TextBox.setTextColor(Color.BLACK);
                    } else {
                        _text_cell_8.TextBox.setTextColor(Color.BLACK);
                    }
                    if (v_actual.compareTo(new BigDecimal(0.88)) == 1 || v_actual.compareTo(new BigDecimal(0.72)) == -1) {
                        text_cell_9.TextBox.setTextColor(android.graphics.Color.RED);
                    } else if (v_actual.compareTo(BigDecimal.ZERO) == 0) {
                        text_cell_9.TextBox.setTextColor(Color.BLACK);
                    } else {
                        text_cell_9.TextBox.setTextColor(Color.BLACK);
                    }
                    if (_v_actual.compareTo(new BigDecimal(1.32)) == 1 || _v_actual.compareTo(new BigDecimal(1.08)) == -1) {
                        _text_cell_9.TextBox.setTextColor(android.graphics.Color.RED);
                    } else if (_v_actual.compareTo(BigDecimal.ZERO) == 0) {
                        _text_cell_9.TextBox.setTextColor(Color.BLACK);
                    } else {
                        _text_cell_9.TextBox.setTextColor(Color.BLACK);
                    }
                    if (mr_actual.compareTo(new BigDecimal(66)) == 1 || mr_actual.compareTo(new BigDecimal(54)) == -1) {
                        text_cell_10.TextBox.setTextColor(android.graphics.Color.RED);
                    } else if (mr_actual.compareTo(BigDecimal.ZERO) == 0) {
                        text_cell_10.TextBox.setTextColor(Color.BLACK);
                    } else {
                        text_cell_10.TextBox.setTextColor(Color.BLACK);
                    }
                    if (_mr_actual.compareTo(new BigDecimal(110.00)) == 1 || _mr_actual.compareTo(new BigDecimal(90.00)) == -1) {
                        _text_cell_10.TextBox.setTextColor(android.graphics.Color.RED);
                    } else if (_mr_actual.compareTo(BigDecimal.ZERO) == 0) {
                        _text_cell_10.TextBox.setTextColor(Color.BLACK);
                    } else {
                        _text_cell_10.TextBox.setTextColor(Color.BLACK);
                    }


                    text_cell_7.setContentText(a_actual.toString());
                    text_cell_8.setContentText(r_actual.toString());
                    text_cell_9.setContentText(v_actual.toString());
                    text_cell_10.setContentText(mr_actual.toString());
                    _text_cell_7.setContentText(_a_actual.toString());
                    _text_cell_8.setContentText(_r_actual.toString());
                    _text_cell_9.setContentText(_v_actual.toString());
                    _text_cell_10.setContentText(_mr_actual.toString());


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
        } else {
            App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    String sql = "exec p_qm_and_ipqc_point_check_create_safety ?,?,?,?,?,?,?,?,?,?,?,?,?,?";
                    Parameters p = new Parameters().add(1, button_text_cell_2.getContentText()).add(2, text_cell_3.getContentText()).add(3, button_text_cell_4.getContentText())
                            .add(4, button_text_cell_5.getContentText()).add(5, text_cell_6.getContentText())
                            .add(6, text_cell_7.getContentText()).add(7, text_cell_8.getContentText()).add(8, text_cell_9.getContentText()).add(9, text_cell_10.getContentText())
                            .add(10, _text_cell_7.getContentText()).add(11, _text_cell_8.getContentText()).add(12, _text_cell_9.getContentText()).add(13, _text_cell_10.getContentText())
                            .add(14, id);
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
}
