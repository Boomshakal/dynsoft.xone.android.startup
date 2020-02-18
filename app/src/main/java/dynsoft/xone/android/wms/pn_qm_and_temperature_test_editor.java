package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Message;
import android.service.autofill.Dataset;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

//import com.bigkoo.pickerview.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataSet;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.link.Link;

/**
 * Created by Administrator on 2018/6/12.
 */

public class pn_qm_and_temperature_test_editor extends pn_editor {
    private View view;
    private TextCell text_cell_1;
    private ButtonTextCell button_text_cell_2;
    private ButtonTextCell button_text_cell_3;
    private TextCell text_cell_4;


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;

    private Integer id;

    public ImageButton btn_prev;
    public ImageButton btn_next;

    public pn_qm_and_temperature_test_editor(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        sharedPreferences = App.Current.Workbench.getSharedPreferences("temperature_test", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();

        LayoutParams lp = new LayoutParams(-1, -1);
        view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_win_temperature_test_editor, this, true);
        Log.e(getContext().getPackageName(), "setContentView()");
        view.setLayoutParams(lp);
        //noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();

        text_cell_1 = (TextCell) findViewById(R.id.text_cell_1);
        button_text_cell_2 = (ButtonTextCell) findViewById(R.id.button_text_cell_2);
        button_text_cell_3 = (ButtonTextCell) findViewById(R.id.button_text_cell_3);
        text_cell_4 = (TextCell) findViewById(R.id.text_cell_4);

        id = this.Parameters.get("id", 0);
        //任务单号
        String code = this.Parameters.get("code", "");


        if (text_cell_1 != null) {
            text_cell_1.setLabelText("工号");
//            text_cell_1.setReadOnly();
            text_cell_1.TextBox.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                        String bar_code = text_cell_1.getContentText().trim();
                        text_cell_1.setContentText(bar_code);
                        text_cell_1.TextBox.setSelection(bar_code.length());
                        getname(bar_code);
                        return true;
                    }
                    return false;
                }
            });
        }

        if (button_text_cell_2 != null) {
            button_text_cell_2.setLabelText("测量时间");
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


        if (button_text_cell_3 != null) {
            button_text_cell_3.setLabelText("姓名");
//            button_text_cell_3.setContentText(App.Current.UserCode);
            button_text_cell_3.setReadOnly();
            button_text_cell_3.Button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    App.Current.Workbench.scanByCamera();
                }

            });
        }

        if (text_cell_4 != null) {
            text_cell_4.setLabelText("体温");
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

    @Override
    public void onScan(final String barcode) {
        String barcode_text = barcode.trim();
        text_cell_1.setContentText(barcode_text);

        getname(barcode_text);
    }

    private void getname(String code) {
        String sql = "SELECT name from dbo.user_diff WHERE code =?";
        Parameters p = new Parameters().add(1, code);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                DataRow row = result.Value;
                if (result.HasError) {
                    App.Current.showError(getContext(), result.Error);
                    return;
                } else {
                    if (row == null) {
                        App.Current.toastInfo(getContext(), "请扫描正确的员工工号！");
                        App.Current.playSound(R.raw.error);
                        return;
                    } else {
                        button_text_cell_3.setContentText(row.getValue("name", ""));
                    }
                }


            }
        });
    }

    private void loadItem(Integer id) {
        this.ProgressDialog.show();

        String sql = "exec get_qm_temperature_test_editor ?";
        Parameters p = new Parameters().add(1, id);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {

            @Override
            public void handleMessage(Message msg) {
                pn_qm_and_temperature_test_editor.this.ProgressDialog.dismiss();
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    return;
                }
                if (value.Value != null) {
                    String code = value.Value.getValue("code", "");
                    String name = value.Value.getValue("name", "");

                    String temp = value.Value.getValue("temp", "");


                    Date check_date = value.Value.getValue("createtime", new Date());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String format_date = simpleDateFormat.format(check_date);

                    text_cell_1.setContentText(code);
                    button_text_cell_2.setContentText(format_date);
                    button_text_cell_3.setContentText(name);
                    text_cell_4.setContentText(temp);


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


        if (TextUtils.isEmpty(button_text_cell_3.getContentText())) {
            App.Current.toastInfo(getContext(), "请扫描员工！");
            App.Current.playSound(R.raw.error);
        } else if (TextUtils.isEmpty(button_text_cell_2.getContentText())) {
            App.Current.toastInfo(getContext(), "请选择检验日期！");
            App.Current.playSound(R.raw.error);
        } else if (!isNumeric(text_cell_4.getContentText())) {
            App.Current.toastInfo(getContext(), "请输入测试数值");
            App.Current.playSound(R.raw.error);
        } else {
            App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    String sql = "exec p_qm_and_temperature_test_create ?,?,?,?";
                    Parameters p = new Parameters().add(1, button_text_cell_2.getContentText()).add(2, text_cell_1.getContentText()).add(3, text_cell_4.getContentText())
                            .add(4, id);
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
//                                        close();
                                        clear();
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

    public void clear() {
        this.text_cell_1.setContentText("");
        this.text_cell_4.setContentText("");
        this.button_text_cell_3.setContentText("");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        String chooseDate = simpleDateFormat.format(new Date());
        button_text_cell_2.setContentText(chooseDate);
    }
}
