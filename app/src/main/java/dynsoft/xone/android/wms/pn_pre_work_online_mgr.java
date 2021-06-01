package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

import dynsoft.xone.android.activity.PostSkillActivity;
import dynsoft.xone.android.activity.PreOnline2Activity;
//import dynsoft.xone.android.activity.PreOnlineActivity;
import dynsoft.xone.android.activity.WIPActivity;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.link.Link;

/**
 * Created by Administrator on 2018/8/28.
 * 前加工打卡,直接进入打卡界面
 */

public class pn_pre_work_online_mgr extends pn_mgr {

    public pn_pre_work_online_mgr(Context context) {
        super(context);
        Intent intent = new Intent(getContext(), PreOnline2Activity.class);
        App.Current.Workbench.startActivity(intent);
        close();
        int currentItem = App.Current.Workbench.ViewPager.getCurrentItem();
        App.Current.Workbench.ViewPager.removeViewAt(currentItem);
    }

//    private ButtonTextCell buttonTextCell;
//    private ImageButton imageButtonCommit;
//    private SharedPreferences sharedPreferences;
//    private SharedPreferences.Editor edit;
//    private String taskOrderCode;
//
//    public pn_pre_work_online_mgr(Context context) {
//        super(context);
//    }
//
//    @Override
//    public void setContentView() {
//        LayoutParams lp = new LayoutParams(-1, -1);
//        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_post_skill_mgr, this, true);
//        sharedPreferences = getContext().getSharedPreferences("pre", Context.MODE_PRIVATE);
//        edit = sharedPreferences.edit();
//        view.setLayoutParams(lp);
//    }
//
//    @Override
//    public void onPrepared() {
//        super.onPrepared();
//
//        taskOrderCode = this.Parameters.get("task_order_code", sharedPreferences.getString("task_order_code", ""));
//
//        buttonTextCell = (ButtonTextCell) findViewById(R.id.button_text_cell);
//
//        if (buttonTextCell != null) {
//            buttonTextCell.setLabelText("生产任务");
//            buttonTextCell.setReadOnly();
//            buttonTextCell.setContentText(taskOrderCode);
//            buttonTextCell.Button.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    loadComfirmName();
//                }
//            });
//        }
//
//        imageButtonCommit = (ImageButton) findViewById(R.id.btn_commit);
//        imageButtonCommit.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_white"));
//        imageButtonCommit.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                GenerateValueFiles generateValueFiles = new GenerateValueFiles(50, 60, "");
////                generateValueFiles.generate();
//                Intent intent = new Intent(pn_pre_work_online_mgr.this.getContext(), PreOnlineActivity.class);
//                intent.putExtra("value", buttonTextCell.getContentText());
//                App.Current.Workbench.startActivity(intent);
//            }
//        });
//    }
//
//    private void loadComfirmName() {
//        Link link = new Link("pane://x:code=pre_online_parameter_mgr");
//        link.Parameters.add("textcell", buttonTextCell);
//        link.Open(null, getContext(), null);
//        this.close();
//    }
}
