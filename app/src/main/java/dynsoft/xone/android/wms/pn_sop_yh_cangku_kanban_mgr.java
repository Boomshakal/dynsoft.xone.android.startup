package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;

import dynsoft.xone.android.activity.MPSActivity;
import dynsoft.xone.android.activity.SopActivity;
import dynsoft.xone.android.activity.YhActivity;
import dynsoft.xone.android.activity.YhcangkuActivity;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.link.Link;

/**
 * Created by Administrator on 2017/12/18.
 */

public class pn_sop_yh_cangku_kanban_mgr extends pn_mgr {
    private DataTable value;
    private ButtonTextCell textcell_2;

    private ImageButton btn_commit;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    private View view;
    private String code;
    private int seq_id = 0;
    private int item_id;
    private long task_id;
    private boolean isNew;
    private String org_code;
    private int org_id;
    private String segment;
    private ArrayList<String> selectedException;

    public pn_sop_yh_cangku_kanban_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_sop_yh_mps_mgr, this, true);
        view.setLayoutParams(lp);
        selectedException = new ArrayList<String>();
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        //sharedPreferences = getContext().getSharedPreferences("sop", Context.MODE_PRIVATE);
        //edit = sharedPreferences.edit();
        textcell_2 = (ButtonTextCell) view.findViewById(R.id.textcell_2);
//        segment = sharedPreferences.getString("segment", "");
        code = this.Parameters.get("code", "");
        //        item_id = this.Parameters.get("item_id", 0);
        isNew = this.Parameters.get("isNew", false);
        if (isNew) {
            task_id = this.Parameters.get("task_id", 0L);
            //edit.putInt("order_task_id", (int) task_id);
            // edit.commit();
            segment = "";
            loadWorkLineName(textcell_2);
        }

        if (textcell_2 != null) {
            textcell_2.setLabelText("线别");
            textcell_2.setReadOnly();
            textcell_2.setContentText(segment);
            textcell_2.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadWorkLineName(textcell_2);
                }
            });
        }


        btn_commit = (ImageButton) findViewById(R.id.btn_commit);
        btn_commit.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_white"));
        btn_commit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


                //  edit.putString("segment", textcell_2.getContentText());
                if (isNew) {
                    //       edit.putString("org_code", org_code);
                } else {
                    //           edit.putString("org_code", sharedPreferences.getString("org_code", ""));
                }
                // edit.commit();

                Intent intent = new Intent(getContext(), YhcangkuActivity.class);
                intent.putExtra("image_urls", textcell_2.getContentText());
                Log.e("LZH11", textcell_2.getContentText());
                App.Current.Workbench.startActivity(intent);

            }
        });
    }


    private void loadWorkLineName(final ButtonTextCell text) {

        ArrayList<String> names = new ArrayList<String>();

        names.add("仓库看板");

        final StringBuffer name = new StringBuffer();
        final boolean[] selected = new boolean[names.size()];
        multiChoiceDialog(name, selected, names, textcell_2);

    }


    private void multiChoiceDialog(final StringBuffer nameMessage, final boolean[] selected, final ArrayList<String> names, final ButtonTextCell buttonTextCell) {
        new AlertDialog.Builder(pn_sop_yh_cangku_kanban_mgr.this.getContext()).setTitle("请选择")
                .setNegativeButton("取消", null).setPositiveButton("确定", null)
                .setMultiChoiceItems(names.toArray(new String[0]), selected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        selected[i] = b;
                        if (selected[i] == true) {
                            nameMessage.append(names.get(i));
                            nameMessage.append(";");
                            if (!selectedException.contains(names.get(i))) {
                                selectedException.add(names.get(i));
                            }
                        }
                        buttonTextCell.setContentText(nameMessage.toString());
                    }
                }).show();
    }


}
