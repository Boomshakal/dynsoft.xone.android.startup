package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.io.Serializable;
import java.util.ArrayList;

import dynsoft.xone.android.activity.LightKanbanActivity;
import dynsoft.xone.android.activity.PostSkillActivity;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/8/28.
 */

public class pn_qm_post_skill_mgr extends pn_mgr {
    private ButtonTextCell buttonTextCell;
    private ImageButton imageButtonCommit;

    public pn_qm_post_skill_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_post_skill_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();

        buttonTextCell = (ButtonTextCell) findViewById(R.id.button_text_cell);

        if (buttonTextCell != null) {
            buttonTextCell.setLabelText("部门");
            buttonTextCell.Button.setFocusable(true);
            buttonTextCell.setReadOnly();
            buttonTextCell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseOrg();
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
                Intent intent = new Intent(pn_qm_post_skill_mgr.this.getContext(), PostSkillActivity.class);
                intent.putExtra("value", buttonTextCell.getContentText());
                App.Current.Workbench.startActivity(intent);
            }
        });
    }

    private void chooseOrg() {
        String sql = "exec fm_get_post_skill_org_name";
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {

            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(pn_qm_post_skill_mgr.this.getContext(), value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
//                    toastChooseDialog(value.Value);

                    ArrayList<String> names = new ArrayList<String>();
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        names.add(value.Value.Rows.get(i).getValue("org_name", ""));
                    }
                    toastChooseDialog(value.Value);


                } else {
                    App.Current.toastError(pn_qm_post_skill_mgr.this.getContext(), "部门为空，请联系管理员！");
                }
            }
        });
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
                    buttonTextCell.setContentText(row.getValue(
                            "org_name", ""));
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(pn_qm_post_skill_mgr.this.getContext()).setTitle("请选择")
                .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(buttonTextCell.getContentText().toString()), listener)
                .setNegativeButton("取消", null).show();
    }
}
