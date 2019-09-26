package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import dynsoft.xone.android.activity.FirstDepartmentKanbanActivity;
import dynsoft.xone.android.activity.ThirdDepartmentKanbanActivity;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Result;

/**
 * Created by Administrator on 2018/3/21.
 */

public class pn_qm_department_kanban_mgr extends pn_editor {
    private View view;
    private ButtonTextCell buttonTextCell;

    public pn_qm_department_kanban_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_department_kanban_editor, this, true);
        view.setLayoutParams(lp);
        buttonTextCell = (ButtonTextCell) view.findViewById(R.id.button_text_cell_1);
        //noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        if (buttonTextCell != null) {
            buttonTextCell.setLabelText("部门");
            buttonTextCell.setReadOnly();
            buttonTextCell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseWorkLine(buttonTextCell);
                }
            });
        }
    }

    private void chooseWorkLine(final ButtonTextCell txt) {
        String sql = "select distinct(org_name) from fm_work_line where org_name is not null";
        final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(Connector, sql);

        if (result.HasError) {
            App.Current.showError(this.getContext(), result.Error);
            return;
        }
        if (result.Value != null) {

            ArrayList<String> names = new ArrayList<String>();
            for (DataRow row : result.Value.Rows) {
                String name = row.getValue("org_name", "");
                names.add(name);
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which >= 0) {
                        DataRow row = result.Value.Rows.get(which);
                        txt.setContentText(row.getValue(
                                "org_name", ""));
                    }
                    dialog.dismiss();
                }
            };
            new AlertDialog.Builder(pn_qm_department_kanban_mgr.this.getContext()).setTitle("请选择")
                    .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(txt.getContentText()), listener)
                    .setNegativeButton("取消", null).show();
        }
    }

    @Override
    public void commit() {
        super.commit();
        if ("生产一部".equals(buttonTextCell.getContentText())) {
            startIntent(FirstDepartmentKanbanActivity.class);
        } else if ("生产二部".equals(buttonTextCell.getContentText())) {
            Toast.makeText(pn_qm_department_kanban_mgr.this.getContext(), "敬请期待...", Toast.LENGTH_SHORT).show();
        } else if ("生产三部".equals(buttonTextCell.getContentText())) {
            startIntent(ThirdDepartmentKanbanActivity.class);
        } else {
            Toast.makeText(pn_qm_department_kanban_mgr.this.getContext(), "敬请期待...", Toast.LENGTH_SHORT).show();
        }
    }

    public void startIntent(Class clazz) {
        Intent intent = new Intent(getContext(), clazz);
        App.Current.Workbench.startActivity(intent);
//        int currentItem = App.Current.Workbench.ViewPager.getCurrentItem();
//        App.Current.Workbench.ViewPager.removeViewAt(currentItem);
//        close();
    }

    //    Intent intent = new Intent(getContext(), FirstDepartmentKanbanActivity.class);
//        App.Current.Workbench.startActivity(intent);
//    close();
//    int currentItem = App.Current.Workbench.ViewPager.getCurrentItem();
//        App.Current.Workbench.ViewPager.removeViewAt(currentItem);
}
