package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

import dynsoft.xone.android.activity.SMTOnPartActivity;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/8/28.
 */

public class pn_qm_oqc_check_kanban_mgr extends pn_mgr {
    private ButtonTextCell buttonTextCell;
    private ImageButton imageButtonCommit;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;

    public pn_qm_oqc_check_kanban_mgr(Context context) {
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

        sharedPreferences = this.getContext().getSharedPreferences("oqc_work_line", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();
        String work_line = sharedPreferences.getString("work_line", "");

        buttonTextCell = (ButtonTextCell) findViewById(R.id.button_text_cell);

        if (buttonTextCell != null) {
            buttonTextCell.setLabelText("线体");
            if (!TextUtils.isEmpty(work_line)) {
                buttonTextCell.setContentText(work_line);
            }
            buttonTextCell.Button.setFocusable(true);
            buttonTextCell.setReadOnly();
            buttonTextCell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseWorkLine();
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
                if (TextUtils.isEmpty(buttonTextCell.getContentText())) {
                    App.Current.toastError(pn_qm_oqc_check_kanban_mgr.this.getContext(), "请先选择线体!");
                } else {
                    edit.putString("work_line", buttonTextCell.getContentText());
                    edit.commit();
                    Intent intent = new Intent(pn_qm_oqc_check_kanban_mgr.this.getContext(), SMTOnPartActivity.class);
//                    Intent intent = new Intent(pn_qm_smt_on_part_report_mgr.this.getContext(), WIPActivity.class);
                    intent.putExtra("value", buttonTextCell.getContentText() == "" ? "all" : buttonTextCell.getContentText());
                    App.Current.Workbench.startActivity(intent);
                }

            }
        });
    }

    private void chooseWorkLine() {
        String sql = "exec fm_get_work_line_by_department ?";
        Parameters parameters = new Parameters().add(1, "生产二部");
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, parameters, new ResultHandler<DataTable>() {

            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(pn_qm_oqc_check_kanban_mgr.this.getContext(), value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
//                    toastChooseDialog(value.Value);

                    ArrayList<String> names = new ArrayList<String>();
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        names.add(value.Value.Rows.get(i).getValue("name", ""));
                    }
                    toastChooseDialog(value.Value);


                } else {
                    App.Current.toastError(pn_qm_oqc_check_kanban_mgr.this.getContext(), "线体为空，请联系管理员！");
                }
            }
        });
    }

    private void toastChooseDialog(final DataTable value) {
        ArrayList<String> names = new ArrayList<String>();
        for (DataRow row : value.Rows) {
            String name = row.getValue("name", "");
            names.add(name);
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    DataRow row = value.Rows.get(which);
                    buttonTextCell.setContentText(row.getValue(
                            "name", ""));
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(pn_qm_oqc_check_kanban_mgr.this.getContext()).setTitle("请选择")
                .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(buttonTextCell.getContentText().toString()), listener)
                .setNegativeButton("取消", null).show();
    }
}
