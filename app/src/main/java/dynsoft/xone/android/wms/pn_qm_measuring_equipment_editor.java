package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/6/12.
 */

public class pn_qm_measuring_equipment_editor extends pn_editor {
    private View view;
    private TextCell text_cell_1;
    private TextCell text_cell_2;
    private TextCell text_cell_3;
    private ButtonTextCell button_text_cell_4;
    private TextCell text_cell_5;
    private ButtonTextCell button_text_cell_6;

    private Integer id;
    private Integer validity;

    public ImageButton btn_prev;
    public ImageButton btn_next;

    public pn_qm_measuring_equipment_editor(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_measuring_equipment_editor, this, true);
        Log.e(getContext().getPackageName(), "setContentView()");
        view.setLayoutParams(lp);
        //noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();

        text_cell_1 = (TextCell) findViewById(R.id.text_cell_1);
        text_cell_2 = (TextCell) findViewById(R.id.text_cell_2);
        text_cell_3 = (TextCell) findViewById(R.id.text_cell_3);
        button_text_cell_4 = (ButtonTextCell) findViewById(R.id.button_text_cell_4);
        text_cell_5 = (TextCell) findViewById(R.id.text_cell_5);
        button_text_cell_6 = (ButtonTextCell) findViewById(R.id.button_text_cell_6);

//        this.btn_prev = (ImageButton) this.findViewById(R.id.btn_prev);
//        this.btn_next = (ImageButton) this.findViewById(R.id.btn_next);

        id = this.Parameters.get("id", 0);


        if (text_cell_1 != null) {
            text_cell_1.setLabelText("仪器编号");
        }

        if (text_cell_2 != null) {
            text_cell_2.setLabelText("型号规格");
        }

        if (text_cell_3 != null) {
            text_cell_3.setLabelText("仪器名称");
        }

        if (button_text_cell_4 != null) {
            button_text_cell_4.setLabelText("分类");
            button_text_cell_4.setReadOnly();
            button_text_cell_4.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_gray"));
            button_text_cell_4.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseResult(button_text_cell_4);
                }
            });
        }
        if (text_cell_5 != null) {
            text_cell_5.setLabelText("有效期");
        }

        if (button_text_cell_6 != null) {
            button_text_cell_6.setLabelText("最近校验时间");
            button_text_cell_6.setReadOnly();
            button_text_cell_6.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_gray"));
            button_text_cell_6.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimePickerView timePickerView = new TimePickerView(getContext(), TimePickerView.Type.YEAR_MONTH_DAY);
                    timePickerView.setCyclic(true);
//                    timePickerView.setTime(new Date());//获取当前时间
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd");
                    Date format = null;
                    try {
                        format = simpleDateFormat.parse(button_text_cell_6.getContentText());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    timePickerView.setTime(format);

                    timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date) {

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd");
                            String chooseDate = simpleDateFormat.format(date);
                            button_text_cell_6.setContentText(chooseDate);
                        }
                    });
                    timePickerView.show();
                }
            });
        }


        if (this.btn_prev != null) {
            this.btn_prev.setImageBitmap(App.Current.ResourceManager.getImage("@/core_prev_white"));
            this.btn_prev.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_qm_measuring_equipment_editor.this.prev();
                }
            });
        }
        if (this.btn_next != null) {
            this.btn_next.setImageBitmap(App.Current.ResourceManager.getImage("@/core_next_white"));
            this.btn_next.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_qm_measuring_equipment_editor.this.next();
                }
            });
        }


        Log.e("len", "ID: " + id);
        loadItem(id);
    }

    private void loadItem(Integer id) {
        this.ProgressDialog.show();

        String sql = "select ROW_NUMBER() over(order by isnull(1,1) desc) number,* from qm_measuring_equipment_mgr where id=?";
        Parameters p = new Parameters().add(1, id);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {

            @Override
            public void handleMessage(Message msg) {
                pn_qm_measuring_equipment_editor.this.ProgressDialog.dismiss();
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    return;
                }
                if (value.Value != null) {
                    String code = value.Value.getValue("code", "");
                    String model = value.Value.getValue("model", "");
                    String name = value.Value.getValue("name", "");
                    String type = value.Value.getValue("type", "");

                    validity = value.Value.getValue("period_of_validity", 0);

                    Date last_date = value.Value.getValue("last_date", new Date());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String format_date = simpleDateFormat.format(last_date);

                    text_cell_1.setContentText(code);
                    text_cell_2.setContentText(model);
                    text_cell_3.setContentText(name);
                    button_text_cell_4.setContentText(type);
                    text_cell_5.setContentText(String.valueOf(validity));
                    button_text_cell_6.setContentText(format_date);

                }
//                else {
//                    close();
//                }
            }
        });
    }

    private void chooseResult(final ButtonTextCell button_text_cell_4) {
        final ArrayList<String> result = new ArrayList<String>();
        result.add("A");
        result.add("B");
        result.add("C");

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    String time = result.get(which);
                    button_text_cell_4.setContentText(time);
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(pn_qm_measuring_equipment_editor.this.getContext()).setTitle("请选择")
                .setSingleChoiceItems(result.toArray(new String[0]), result.indexOf(button_text_cell_4.getContentText()), listener)
                .setNegativeButton("取消", null).show();
    }

    public void prev() {
        if (id > 2) {
            this.loadItem(id - 1);
        } else {
            App.Current.showError(pn_qm_measuring_equipment_editor.this.getContext(), "已经是第一条。");
        }
    }

    public void next() {
        if (id < 280) {
            this.loadItem(id + 1);
        } else {
            App.Current.showError(pn_qm_measuring_equipment_editor.this.getContext(), "已经是最后一条。");
        }
    }

    @Override
    public void commit() {
        super.commit();

        if (id > 0) {   //提交本次的数据
            if (TextUtils.isEmpty(button_text_cell_4.getContentText())) {
                App.Current.toastInfo(getContext(), "请选择分类！");
                App.Current.playSound(R.raw.error);
            } else if (TextUtils.isEmpty(button_text_cell_6.getContentText())) {
                App.Current.toastInfo(getContext(), "请选择检验日期！");
                App.Current.playSound(R.raw.error);
            } else {
                App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        String sql = "UPDATE qm_measuring_equipment_mgr\n" +
                                "SET code=?,\n" +
                                "    model=?,\n" +
                                "    name=?,\n" +
                                "    type=?," +
                                "    period_of_validity=?,\n" +
                                "    last_date=?\n" +
                                "WHERE id=? ";
                        Parameters p = new Parameters().add(1, text_cell_1.getContentText()).add(2, text_cell_2.getContentText()).add(3, text_cell_3.getContentText())
                                .add(4, button_text_cell_4.getContentText()).add(5, text_cell_5.getContentText()).add(6, button_text_cell_6.getContentText()).add(7, id);
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
//
                                        loadItem(id);
                                    } else {
                                        App.Current.toastInfo(getContext(), "提交失败");
                                        App.Current.playSound(R.raw.error);
                                    }
                                } else {
                                    App.Current.toastInfo(getContext(), "提交失败");
                                    App.Current.playSound(R.raw.error);
                                }
                            }
                        });
                    }
                });
            }
        } else {
            close();
        }
    }
}
