package dynsoft.xone.android.wms;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;

import java.io.Serializable;
import java.util.ArrayList;

import dynsoft.xone.android.activity.LightKanbanActivity;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/5/19.
 */

public class pn_qm_light_kanban_mgr extends pn_mgr {
    private ButtonTextCell buttonTextCell;              //异常类型
    private ButtonTextCell buttonTextCellDepartment;   //部门
    private CheckBox checkBoxAsk;                        //选择，获取未处理的数据， 没选择，获取所有的数据
    private ImageButton imageButtonCommit;
    private ArrayList<String> selectedException;

    public pn_qm_light_kanban_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_light_kanban_mgr, this, true);
        view.setLayoutParams(lp);
        selectedException = new ArrayList<String>();
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        buttonTextCell = (ButtonTextCell) findViewById(R.id.button_text_cell);
        buttonTextCellDepartment = (ButtonTextCell) findViewById(R.id.button_text_cell_department);
        checkBoxAsk = (CheckBox) findViewById(R.id.check_ask);
        if (buttonTextCell != null) {
            buttonTextCell.setLabelText("安灯类型");
            buttonTextCell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_gray"));
            buttonTextCell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseLightType();
                }
            });
        }
        if (buttonTextCellDepartment != null) {
            buttonTextCellDepartment.setLabelText("部门");
            buttonTextCellDepartment.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_gray"));
            buttonTextCellDepartment.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseDepartment();
                }
            });
        }
        imageButtonCommit = (ImageButton) findViewById(R.id.btn_commit);
        imageButtonCommit.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_white"));
        imageButtonCommit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                GenerateValueFiles generateValueFiles = new GenerateValueFiles(50, 60, "");
//                generateValueFiles.generate();
                Intent intent = new Intent(pn_qm_light_kanban_mgr.this.getContext(), LightKanbanActivity.class);
                intent.putExtra("value", (Serializable) selectedException);
                intent.putExtra("org_name", buttonTextCellDepartment.getContentText());
                intent.putExtra("respond", checkBoxAsk.isChecked());
                App.Current.Workbench.startActivity(intent);
                selectedException.removeAll(selectedException);
                buttonTextCell.setContentText("");
                buttonTextCellDepartment.setContentText("");
            }
        });
    }

    private void chooseDepartment() {
        String sql = "SELECT DISTINCT(org_name) FROM fm_work_line WHERE org_name IS NOT NULL";
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {

            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(pn_qm_light_kanban_mgr.this.getContext(), value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
//                    toastChooseDialog(value.Value);
                    toastChooseDialog(value.Value);


                } else {
                    App.Current.toastError(pn_qm_light_kanban_mgr.this.getContext(), "安灯类型为空，请联系管理员！");
                }
            }
        });
    }

    private void chooseLightType() {
        String sql = "exec get_exception_kanban_type";
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {

            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(pn_qm_light_kanban_mgr.this.getContext(), value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
//                    toastChooseDialog(value.Value);

                    ArrayList<String> names = new ArrayList<String>();
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        names.add(value.Value.Rows.get(i).getValue("exception_type", ""));
                    }
                    final StringBuffer nameMessage = new StringBuffer();
                    final boolean[] selected = new boolean[names.size()];
                    multiChoiceDialog(nameMessage, selected, names, buttonTextCell);


                } else {
                    App.Current.toastError(pn_qm_light_kanban_mgr.this.getContext(), "安灯类型为空，请联系管理员！");
                }
            }
        });
    }


    private void multiChoiceDialog(final StringBuffer nameMessage, final boolean[] selected, final ArrayList<String> names, final ButtonTextCell buttonTextCell) {
        new AlertDialog.Builder(pn_qm_light_kanban_mgr.this.getContext()).setTitle("请选择")
                .setNegativeButton("取消", null).setPositiveButton("确定", null)
                .setMultiChoiceItems(names.toArray(new String[0]), selected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        selected[i] = b;
                        if (selected[i] == true) {
                            nameMessage.append(names.get(i));
                            nameMessage.append(",");
                            if (!selectedException.contains(names.get(i))) {
                                selectedException.add(names.get(i));
                            }
                        }
                        buttonTextCell.setContentText(nameMessage.toString());
                    }
                }).show();
    }

    private void toastChooseDialog(final DataTable value) {
        ArrayList<String> names = new ArrayList<String>();
        for (DataRow row : value.Rows) {
            String name = row.getValue("org_name", "");
            names.add(name);
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    DataRow row = value.Rows.get(which);
                    buttonTextCellDepartment.setContentText(row.getValue(
                            "org_name", ""));
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(pn_qm_light_kanban_mgr.this.getContext()).setTitle("请选择")
                .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(buttonTextCellDepartment.getContentText().toString()), listener)
                .setNegativeButton("取消", null).show();
    }
}
