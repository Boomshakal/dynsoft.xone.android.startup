package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
 * Created by Administrator on 2018/6/12.
 */

public class pn_qm_esd_daily_editor extends pn_editor {
    private View view;
    private TextCell text_cell_1;
    private TextCell text_cell_2;
    private TextCell text_cell_3;
    private ButtonTextCell button_text_cell_4;
    private TextCell text_cell_5;
    private Long line_id;
    private String work_line;
    private long head_id;

    public ImageButton btn_prev;
    public ImageButton btn_next;
    private int counts;
    private int all_data;

    public pn_qm_esd_daily_editor(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_esd_daily_editor, this, true);
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

        this.btn_prev = (ImageButton) this.findViewById(R.id.btn_prev);
        this.btn_next = (ImageButton) this.findViewById(R.id.btn_next);

        line_id = this.Parameters.get("line_id", 0L);
        head_id = this.Parameters.get("head_id", 0L);
        counts = this.Parameters.get("counts", 0);
        work_line = this.Parameters.get("work_line", "");

        if (text_cell_1 != null) {
            text_cell_1.setLabelText("�߱�");
            text_cell_1.setContentText(work_line);
        }

        if (text_cell_2 != null) {
            text_cell_2.setLabelText("��Ŀ");
        }

        if (text_cell_3 != null) {
            text_cell_3.setLabelText("����");
        }

        if (button_text_cell_4 != null) {
            button_text_cell_4.setLabelText("���");
            button_text_cell_4.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_gray"));
            button_text_cell_4.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseResult(button_text_cell_4);
                }
            });
        }
        if (text_cell_5 != null) {
            text_cell_5.setLabelText("ԭ��");
        }

        if (this.btn_prev != null) {
            this.btn_prev.setImageBitmap(App.Current.ResourceManager.getImage("@/core_prev_white"));
            this.btn_prev.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_qm_esd_daily_editor.this.prev();
                }
            });
        }
        if (this.btn_next != null) {
            this.btn_next.setImageBitmap(App.Current.ResourceManager.getImage("@/core_next_white"));
            this.btn_next.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_qm_esd_daily_editor.this.next();
                }
            });
        }

        if(line_id == null) {
            line_id = 1L;
        }
        Log.e("len", "LINE: " + line_id);
        loadItem(line_id);
    }

    private void loadItem(long line) {
        this.ProgressDialog.show();

        String sql = "exec fm_get_esd_item_and ?,?";
        Parameters p = new Parameters().add(1, head_id).add(2, line);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {

            @Override
            public void handleMessage(Message msg) {
                pn_qm_esd_daily_editor.this.ProgressDialog.dismiss();
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    return;
                }
                if (value.Value != null) {
                    String check_type = value.Value.getValue("check_type", "");
                    String check_content = value.Value.getValue("check_content", "");
                    String status = value.Value.getValue("status", "");
                    String check_reason = value.Value.getValue("check_reason", "");

                    line_id = value.Value.getValue("line_id", 0L);
                    all_data = value.Value.getValue("all_data", 0);
                    text_cell_2.setContentText(check_type);
                    text_cell_3.setContentText(check_content);
                    button_text_cell_4.setContentText(status);
                    text_cell_5.setContentText(check_reason);
                    if (all_data > 0) {
                        pn_qm_esd_daily_editor.this.Header.setTitleText("ESD����(" + String.valueOf(all_data) + "���������)");
                    } else {
                        pn_qm_esd_daily_editor.this.Header.setTitleText("ESD����(�޼������)");
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
        new AlertDialog.Builder(pn_qm_esd_daily_editor.this.getContext()).setTitle("��ѡ��")
                .setSingleChoiceItems(result.toArray(new String[0]), result.indexOf(button_text_cell_4.getContentText()), listener)
                .setNegativeButton("ȡ��", null).show();
    }

    public void prev() {
        if (line_id > 2) {
            this.loadItem(line_id - 1);
        } else {
            App.Current.showError(pn_qm_esd_daily_editor.this.getContext(), "�Ѿ��ǵ�һ����");
        }
    }

    public void next() {
        if (line_id < counts) {
            this.loadItem(counts + 1);
        } else {
            App.Current.showError(pn_qm_esd_daily_editor.this.getContext(), "�Ѿ������һ����");
        }
    }

    @Override
    public void commit() {
        super.commit();
        Log.e("len", "ALL:" + all_data);
        if (all_data > 0) {   //�ύ���ε�����
            if (TextUtils.isEmpty(button_text_cell_4.getContentText())) {
                App.Current.toastInfo(getContext(), "��ѡ������");
                App.Current.playSound(R.raw.error);
            } else {
                App.Current.question(this.getContext(), "ȷ��Ҫ�ύ��", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        String sql = "exec fm_update_esd_check_items_and ?,?,?,?";
                        Parameters p = new Parameters().add(1, head_id).add(2, line_id)
                                .add(3, button_text_cell_4.getContentText()).add(4, text_cell_5.getContentText());
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
                                        App.Current.toastInfo(getContext(), "�ύ�ɹ�");
                                        App.Current.playSound(R.raw.pass);
//                                        if (line_id >= total) {
//                                            line_id -= 1;
//                                        } else {
//                                            line_id += 1;
//                                        }
                                        long curIndex = (line_id  % counts) + 1;
                                        loadItem(curIndex);
                                    } else {
                                        App.Current.toastInfo(getContext(), "�ύʧ��");
                                        App.Current.playSound(R.raw.error);
                                    }
                                } else {
                                    App.Current.toastInfo(getContext(), "�ύʧ��");
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
