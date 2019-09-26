package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.math.BigDecimal;
import java.util.ArrayList;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/7/6.
 */

public class pn_qm_electric_equipment_check_editor extends pn_editor {
    private View view;
    private TextCell text_cell_1;
    private TextCell text_cell_2;
    private TextCell text_cell_3;
    private TextCell text_cell_comment;
    private TextCell text_cell_4;
    private TextCell text_cell_5;
    private TextCell text_cell_6;
    private TextCell text_cell_7;
    private TextCell text_cell_8;
    private TextCell text_cell_9;
    private TextCell text_cell_10;
    private TextCell text_cell_11;
    private TextCell text_cell_12;
    private TextCell text_cell_13;
    private TextCell text_cell_14;
    private TextCell text_cell_15;
    private Integer line_id;
    private int head_id;

    public ImageButton btn_prev;
    public ImageButton btn_next;
    private int counts;
    private int all_data;

    public pn_qm_electric_equipment_check_editor(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_electric_equipment_check_editor, this, true);
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
        text_cell_comment = (TextCell) findViewById(R.id.text_cell_comment);
        text_cell_4 = (TextCell) findViewById(R.id.text_cell_4);
        text_cell_5 = (TextCell) findViewById(R.id.text_cell_5);
        text_cell_6 = (TextCell) findViewById(R.id.text_cell_6);
        text_cell_7 = (TextCell) findViewById(R.id.text_cell_7);
        text_cell_8 = (TextCell) findViewById(R.id.text_cell_8);
        text_cell_9 = (TextCell) findViewById(R.id.text_cell_9);
        text_cell_10 = (TextCell) findViewById(R.id.text_cell_10);
        text_cell_11 = (TextCell) findViewById(R.id.text_cell_11);
        text_cell_12 = (TextCell) findViewById(R.id.text_cell_12);
        text_cell_13 = (TextCell) findViewById(R.id.text_cell_13);
        text_cell_14 = (TextCell) findViewById(R.id.text_cell_14);
        text_cell_15 = (TextCell) findViewById(R.id.text_cell_15);

        this.btn_prev = (ImageButton) this.findViewById(R.id.btn_prev);
        this.btn_next = (ImageButton) this.findViewById(R.id.btn_next);

        line_id = this.Parameters.get("line_id", 0);
        head_id = this.Parameters.get("head_id", 0);
        counts = this.Parameters.get("counts", 0);


        if (text_cell_1 != null) {
            text_cell_1.setLabelText("设备名称");
            text_cell_1.setReadOnly();
        }

        if (text_cell_2 != null) {
            text_cell_2.setLabelText("产品名称");
            text_cell_2.setReadOnly();
        }
        if (text_cell_3 != null) {
            text_cell_3.setLabelText("时间");
            text_cell_3.setReadOnly();
        }
        if (text_cell_comment != null) {
            text_cell_comment.setLabelText("备注");
        }
        if (text_cell_4 != null) {
            text_cell_4.setLabelText("漏电电压标准值");
        }
        if (text_cell_5 != null) {
            text_cell_5.setLabelText("漏电电压实测值");
        }
        if (text_cell_6 != null) {
            text_cell_6.setLabelText("接地电阻标准值");
            text_cell_6.setReadOnly();
        }
        if (text_cell_7 != null) {
            text_cell_7.setLabelText("接地电阻实测值");
        }
        if (text_cell_8 != null) {
            text_cell_8.setLabelText("最小值");
        }
        if (text_cell_9 != null) {
            text_cell_9.setLabelText("最大值");
        }
        if (text_cell_10 != null) {
            text_cell_10.setLabelText("实测值");
        }
        if (text_cell_11 != null) {
            text_cell_11.setLabelText("校正值");
        }
        if (text_cell_12 != null) {
            text_cell_12.setLabelText("最小值");
        }
        if (text_cell_13 != null) {
            text_cell_13.setLabelText("最大值");
        }
        if (text_cell_14 != null) {
            text_cell_14.setLabelText("实测值");
        }
        if (text_cell_15 != null) {
            text_cell_15.setLabelText("校正值");
        }

        if (this.btn_prev != null) {
            this.btn_prev.setImageBitmap(App.Current.ResourceManager.getImage("@/core_prev_white"));
            this.btn_prev.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_qm_electric_equipment_check_editor.this.prev();
                }
            });
        }
        if (this.btn_next != null) {
            this.btn_next.setImageBitmap(App.Current.ResourceManager.getImage("@/core_next_white"));
            this.btn_next.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_qm_electric_equipment_check_editor.this.next();
                }
            });
        }

        if (line_id == null) {
            line_id = 1;
        }
        Log.e("len", "LINE: " + line_id);
        loadItem(line_id);
    }

    private void loadItem(int line) {
        this.ProgressDialog.show();

        String sql = "exec fm_get_equipment_check_item_and ?,?";
        Parameters p = new Parameters().add(1, head_id).add(2, line);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {

            @Override
            public void handleMessage(Message msg) {
                pn_qm_electric_equipment_check_editor.this.ProgressDialog.dismiss();
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    return;
                }
                if (value.Value != null) {
                    //检查项目
                    String checked_equip = value.Value.getValue("checked_equip", "");
                    String product_name = value.Value.getValue("product_name", "");
                    String time = value.Value.getValue("time", "");
                    String comment = value.Value.getValue("comment", "");
                    String v_standard = value.Value.getValue("v_standard", "");
                    String r_standard = value.Value.getValue("r_standard", "");
                    float v_actual = value.Value.getValue("v_actual", new BigDecimal(0)).floatValue();
                    float r_actual = value.Value.getValue("r_actual", new BigDecimal(0)).floatValue();
                    float sop_min = value.Value.getValue("sop_min", new BigDecimal(0)).floatValue();
                    float sop_max = value.Value.getValue("sop_max", new BigDecimal(0)).floatValue();
                    float sop_actual = value.Value.getValue("sop_actual", new BigDecimal(0)).floatValue();
                    float s_adjusted = value.Value.getValue("s_adjusted", new BigDecimal(0)).floatValue();
                    float t_standard_min = value.Value.getValue("t_standard_min", new BigDecimal(0)).floatValue();
                    float t_standard_max = value.Value.getValue("t_standard_max", new BigDecimal(0)).floatValue();
                    float t_actual = value.Value.getValue("t_actual", new BigDecimal(0)).floatValue();
                    float t_adjusted = value.Value.getValue("t_adjusted", new BigDecimal(0)).floatValue();

                    line_id = value.Value.getValue("id", 0);
                    all_data = value.Value.getValue("all_data", 0);
                    text_cell_1.setContentText(checked_equip);
                    text_cell_2.setContentText(product_name);
                    text_cell_3.setContentText(time);
                    text_cell_comment.setContentText(comment);
                    text_cell_4.setContentText(v_standard);
                    text_cell_5.setContentText(String.valueOf(v_actual));
                    text_cell_6.setContentText(r_standard);
                    text_cell_7.setContentText(String.valueOf(r_actual));
                    text_cell_8.setContentText(String.valueOf(sop_min));
                    text_cell_9.setContentText(String.valueOf(sop_max));
                    text_cell_10.setContentText(String.valueOf(sop_actual));
                    text_cell_11.setContentText(String.valueOf(s_adjusted));
                    text_cell_12.setContentText(String.valueOf(t_standard_min));
                    text_cell_13.setContentText(String.valueOf(t_standard_max));
                    text_cell_14.setContentText(String.valueOf(t_actual));
                    text_cell_15.setContentText(String.valueOf(t_adjusted));
                    if (all_data > 0) {
                        pn_qm_electric_equipment_check_editor.this.Header.setTitleText("电批(" + String.valueOf(all_data) + "条点检任务)");
                    } else {
                        pn_qm_electric_equipment_check_editor.this.Header.setTitleText("电批(无点检任务)");
                    }
                } else {
                    close();
                }
            }
        });
    }

    private void chooseResult(final ButtonTextCell button_text_cell_4) {
        final ArrayList<String> result = new ArrayList<String>();
        result.add("OK");
        result.add("NA");
        result.add("NG");

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
        new AlertDialog.Builder(pn_qm_electric_equipment_check_editor.this.getContext()).setTitle("请选择")
                .setSingleChoiceItems(result.toArray(new String[0]), result.indexOf(button_text_cell_4.getContentText()), listener)
                .setNegativeButton("取消", null).show();
    }

    public void prev() {
        if (line_id > 2) {
            this.loadItem(line_id - 1);
        } else {
            App.Current.showError(pn_qm_electric_equipment_check_editor.this.getContext(), "已经是第一条。");
        }
    }

    public void next() {
        if (line_id < counts) {
            this.loadItem(counts + 1);
        } else {
            App.Current.showError(pn_qm_electric_equipment_check_editor.this.getContext(), "已经是最后一条。");
        }
    }

    @Override
    public void commit() {
        super.commit();
        Log.e("len", "ALL:" + all_data);
        if (all_data > 0) {   //提交本次的数据
            App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    String sql = "exec fm_update_equipment_check_items_and ?,?,?,?,?,?,?,?,?,?,?,?,?";
                    Parameters p = new Parameters().add(1, head_id).add(2, line_id).add(3, text_cell_comment.getContentText()).add(4, text_cell_5.getContentText())
                            .add(5, text_cell_7.getContentText()).add(6, text_cell_8.getContentText()).add(7, text_cell_9.getContentText()).add(8, text_cell_10.getContentText())
                            .add(9, text_cell_11.getContentText()).add(10, text_cell_12.getContentText()).add(11, text_cell_13.getContentText())
                            .add(12, text_cell_14.getContentText()).add(13, text_cell_15.getContentText());
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
//                                        if (line_id >= total) {
//                                            line_id -= 1;
//                                        } else {
//                                            line_id += 1;
//                                        }
                                    int curIndex = (line_id % counts) + 1;
                                    loadItem(curIndex);
                                } else {
                                    App.Current.toastError(getContext(), "提交失败,没有更新到数据。");
                                    App.Current.playSound(R.raw.error);
                                }
                            } else {
                                App.Current.toastError(getContext(), "提交失败");
                                App.Current.playSound(R.raw.error);
                            }
                        }
                    });
                }
            });
        } else {
            close();
        }
    }
}
