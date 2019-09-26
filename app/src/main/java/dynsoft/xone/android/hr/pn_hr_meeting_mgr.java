package dynsoft.xone.android.hr;

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

import dynsoft.xone.android.activity.MeetingActivity;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/3/7.
 */

public class pn_hr_meeting_mgr extends pn_mgr {
    private View view;
    private ButtonTextCell buttonTextCell;
    private ImageButton btn_commit;

    private String fd_place_id;                //会议室ID
    private String fd_name;                    //会议室名称
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;

    public pn_hr_meeting_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_hr_meeting_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        buttonTextCell = (ButtonTextCell) findViewById(R.id.buttontextcell_1);
        btn_commit = (ImageButton) findViewById(R.id.btn_commit);

        sharedPreferences = App.Current.Workbench.getSharedPreferences("hr", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();

        fd_name = sharedPreferences.getString("fd_name", "");
        fd_place_id = sharedPreferences.getString("fd_place_id", "");
        if(!TextUtils.isEmpty(fd_name)) {
            buttonTextCell.setContentText(fd_name);
        }

        if(buttonTextCell != null) {
            buttonTextCell.setLabelText("会议室");
            buttonTextCell.setReadOnly();
            buttonTextCell.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseMeetingRoom();               //选择会议室
                }
            });
        }

        if(btn_commit != null) {
            btn_commit.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_white"));
            btn_commit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(TextUtils.isEmpty(buttonTextCell.getContentText())) {
                        App.Current.toastInfo(getContext(), "请先选择会议室");
                    } else {
                        IntentToMeetingActivity();      //跳转到会议记录界面
                    }
                }
            });
        }
    }

    private void IntentToMeetingActivity() {
        Intent intent = new Intent(getContext(), MeetingActivity.class);
        intent.putExtra("fd_place_id", fd_place_id);
        intent.putExtra("fd_name", fd_name);
        App.Current.Workbench.startActivity(intent);
    }

    private void chooseMeetingRoom() {
        String sql = "SELECT fd_id fd_place_id ,fd_name FROM km_imeeting_res";
        App.Current.DbPortal.ExecuteDataTableAsync("mgmt_eip_and", sql, new ResultHandler<DataTable>(){
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if(value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    return;
                }
                if(value.Value != null) {
                    toastDialog(value.Value, buttonTextCell);
                } else {
                    App.Current.showError(getContext(), "当前没有会议室！");
                }
            }
        });
    }

    private void toastDialog(final DataTable dataTable, final ButtonTextCell buttonTextCell3) {
        ArrayList<String> names = new ArrayList<String>();
        for (DataRow row : dataTable.Rows) {
            StringBuffer name = new StringBuffer();
            name.append(row.getValue("fd_name", ""));
            names.add(name.toString());
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    DataRow row = dataTable.Rows.get(which);
                    fd_name = row.getValue("fd_name", "");
                    buttonTextCell3.setContentText(fd_name);
                    fd_place_id = row.getValue("fd_place_id", "");
                    edit.putString("fd_name", fd_name);
                    edit.putString("fd_place_id", fd_place_id);
                    edit.commit();
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(getContext()).setTitle("请选择")
                .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                        (buttonTextCell3.getContentText().toString()), listener)
                .setNegativeButton("取消", null).show();
    }

}
