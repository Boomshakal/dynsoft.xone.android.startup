package dynsoft.xone.android.sopactivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TimeUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import dynsoft.xone.android.activity.SopActivity;
import dynsoft.xone.android.base.BaseActivity;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.wms.pn_qm_ipqc_record_mgr_editor;

/**
 * Created by Administrator on 2017/12/9.
 */

public class SetParameterActivity extends BaseActivity {
    private TextView textview_1;
    private View view;
    private SharedPreferences sharedPreferences;
    private TextCell textcell_1;
    private TextCell textcell_2;
    private ButtonTextCell textcell_3;
    private TextCell textcell_4;
    private TextView cancel;
    private TextView confirm;
    private int seq_id;

    private SharedPreferences.Editor edit;
    private AlertDialog alertDialog;

    @Override
    public View setContentView() {
        view = View.inflate(getBaseContext(), R.layout.activity_parameter_set, null);
        sharedPreferences = getApplication().getSharedPreferences("sop", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();
        WindowManager windowManager = getWindowManager();
        int width = windowManager.getDefaultDisplay().getWidth();
        textcell_1 = (TextCell) view.findViewById(R.id.textcell_1);
        textcell_2 = (TextCell) view.findViewById(R.id.textcell_2);
        textcell_3 = (ButtonTextCell) view.findViewById(R.id.textcell_3);
        textcell_4 = (TextCell) view.findViewById(R.id.textcell_4);
        cancel = (TextView) view.findViewById(R.id.cancel);
        confirm = (TextView) view.findViewById(R.id.confirm);

        if (textcell_1 != null) {
            textcell_1.setLabelText("生产任务");
            textcell_1.setReadOnly();
            textcell_1.setContentText(sharedPreferences.getString("task_order", ""));
        }

        if (textcell_2 != null) {
            textcell_2.setLabelText("作业人员");
            textcell_2.setContentText(sharedPreferences.getString("work_code", ""));
        }

        if (textcell_3 != null) {
            textcell_3.setLabelText("工序");
            textcell_3.setReadOnly();
            textcell_3.setContentText(sharedPreferences.getString("production", ""));
//            textcell_3.Button.setBackgroundResource(R.drawable.bg_button);
            textcell_3.Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadProduction(textcell_3);
                }
            });
        }

        if (textcell_4 != null) {
            textcell_4.setLabelText("切换频率(秒)");
            textcell_4.setContentText(String.valueOf(sharedPreferences.getInt("seconds", 15)));
        }
//        textcell_1 = (ButtonTextCell) view.findViewById(R.id.textcell_1);
//        textcell_2 = (TextCell) view.findViewById(R.id.textcell_2);
//        textcell_3 = (TextCell) view.findViewById(R.id.textcell_3);
//        textcell_4 = (TextCell) view.findViewById(R.id.textcell_4);
//        textcell_5 = (TextCell) view.findViewById(R.id.textcell_5);
//        textcell_6 = (TextCell) view.findViewById(R.id.textcell_6);
//        textcell_7 = (TextCell) view.findViewById(R.id.textcell_7);
//        textcell_8 = (TextCell) view.findViewById(R.id.textcell_8);

        initView();
        return view;
    }

    private void loadProduction(final ButtonTextCell txt) {
        String sql = "exec p_qm_sop_change_production ?,?";
        Parameters p = new Parameters().add(1, App.Current.UserID).add(2, textcell_1.getContentText());
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataTable> result = Value;
                if (result.HasError) {
                    App.Current.showError(SetParameterActivity.this, result.Error);
                    return;
                }
                if (result.Value != null) {

                    ArrayList<String> names = new ArrayList<String>();
                    for (DataRow row : result.Value.Rows) {
                        String name = row.getValue("name", "");
                        names.add(name);
                    }

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SetParameterActivity.this);
                    if (alertDialog == null) {
                        AlertDialog.Builder builder = alertDialogBuilder.setTitle("请选择")
                                .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(txt.getContentText()), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        if (which >= 0) {
                                            DataRow row = result.Value.Rows.get(which);
                                            txt.setContentText(row.getValue(
                                                    "name", ""));
                                            seq_id = row.getValue("id", 0);
                                        }
                                        alertDialog.dismiss();
                                    }
                                })
                                .setNegativeButton("取消", null);
                        alertDialog = builder.create();
                    }
                    alertDialog.show();
                }
            }
        });
    }

    private void initView() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(textcell_2.getContentText())) {
                    Toast.makeText(getBaseContext(), "请输入作业人员工号", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(textcell_4.getContentText())) {
                    Toast.makeText(SetParameterActivity.this, "请输入切换频率", Toast.LENGTH_SHORT).show();
                } else {
                    checkWorkerCode();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void checkWorkerCode() {
        String sql = "select * from fm_worker where code = ?";
        Parameters p = new Parameters().add(1, textcell_2.getContentText());
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if(value.HasError) {
                    App.Current.showError(SetParameterActivity.this, value.Error);
                    return;
                }
                if(value.Value != null) {
                    if ((textcell_3.getContentText().contains(sharedPreferences.getString("production", "")))) {
//                        edit.putInt("seconds", Integer.parseInt(textcell_4.getContentText()));
//                        edit.putString("production", textcell_3.getContentText());
//                        edit.putString("work_code", textcell_2.getContentText().replace("\n", ""));
//                        edit.putInt("seq_id", seq_id);
//                        Log.e("len", "commitSeconds:" + textcell_4.getContentText());
//                        edit.commit();
//                        finish();
                        seq_id = sharedPreferences.getInt("seq_id", 0);
                    }
//                        if(!textcell_2.getContentText().contains(sharedPreferences.getString("work_code", ""))) {
//                            seq_id = sharedPreferences.getInt("seq_id", 0);
//                        }
                    int item_id = sharedPreferences.getInt("item_id", 0);
                    //String sql = "select * from fm_sop where item_id = ? and sequence_id = ?";
                    String sql = "exec fm_sop_get_url ?,?,?";
                    Parameters p = new Parameters().add(1, item_id).add(2, seq_id).add(3, sharedPreferences.getString("station", ""));

                    App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
                        @Override
                        public void handleMessage(Message msg) {
                            Result<DataTable> value = Value;
                            if (value.HasError) {
                                App.Current.showError(SetParameterActivity.this, value.Error);
                                return;
                            }
                            if (value.Value != null) {
                                ArrayList<String> image_urls = new ArrayList<String>();
                                for (DataRow rw : value.Value.Rows) {
                                    image_urls.add(rw.getValue("image_url", ""));
                                }
                                edit.putString("production", textcell_3.getContentText());
                                edit.putString("work_code", textcell_2.getContentText().replace("\n", ""));
                                edit.putInt("seq_id", seq_id);
                                edit.putInt("seconds", Integer.parseInt(textcell_4.getContentText()));
                                edit.commit();
                                Intent intent = new Intent(SetParameterActivity.this, SopActivity.class);
                                intent.putStringArrayListExtra("image_urls", image_urls);
                                App.Current.Workbench.startActivity(intent);
                                finish();
                            }
                        }
                    });

                } else {
                    App.Current.showError(SetParameterActivity.this, "输入的工号有误，请重新输入");
                }
            }
        });
    }

    private void loadTaskOrder(final EditText txt) {     //选择生产任务
        String sql = "select top 10 code from mm_wo_task_order_head";
        final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable("core_and", sql);
        if (result.HasError) {
            App.Current.showError(SetParameterActivity.this, result.Error);
            return;
        }
        if (result.Value != null) {

            ArrayList<String> names = new ArrayList<String>();
            for (DataRow row : result.Value.Rows) {
                String name = row.getValue("code", "");
                names.add(name);
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which >= 0) {
                        DataRow row = result.Value.Rows.get(which);
                        txt.setText(row.getValue("code", ""));
                    }
                    dialog.dismiss();
                }
            };
            new AlertDialog.Builder(SetParameterActivity.this).setTitle("请选择")
                    .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(txt.getText().toString()), listener)
                    .setNegativeButton("取消", null).show();
        }
    }
}