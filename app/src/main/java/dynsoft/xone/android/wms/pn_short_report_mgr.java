package dynsoft.xone.android.wms;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.DialerKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.intermec.aidc.Symbology;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.activity.FirstDepartmentKanbanActivity;
import dynsoft.xone.android.activity.ShortReportKanbanActivity;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/3/21.
 */

public class pn_short_report_mgr extends pn_mgr {
    private ButtonTextCell buttonTextCell1;
    private ButtonTextCell buttonTextCell2;
    private ButtonTextCell buttonTextCell3;

    private ImageButton btnCommit;
    private String monthString;

    public pn_short_report_mgr(Context context) {
        super(context);
//        Intent intent = new Intent(getContext(), ShortReportActivity.class);
//        App.Current.Workbench.startActivity(intent);
//        Toast.makeText(context, "意图Intent", Toast.LENGTH_SHORT).show();
//        close();
//        int currentItem = App.Current.Workbench.ViewPager.getCurrentItem();
//        App.Current.Workbench.ViewPager.removeViewAt(currentItem);
        String sql = "exec get_short_item_date_and";
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if(value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                    return;
                }
                if(value.Value != null) {
                    String yesterday = value.Value.getValue("yesterday", "");
                    String today = value.Value.getValue("today", "");

                    Intent intent = new Intent(getContext(), ShortReportKanbanActivity.class);
                    String planName;
                    planName = "AUTO-E41-" + yesterday;
                    intent.putExtra("planName", planName);
                    intent.putExtra("startTime", today);
                    intent.putExtra("sumshort", "是");
                    Log.e("len", "Plan_Name:" + planName);
                    App.Current.Workbench.startActivity(intent);
                }
            }
        });

        close();
        int currentItem = App.Current.Workbench.ViewPager.getCurrentItem();
        App.Current.Workbench.ViewPager.removeViewAt(currentItem);
    }

//    @Override
//    public void setContentView() {
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
//        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_short_report_mgr, this, true);
//        view.setLayoutParams(lp);
//    }
//
//    @Override
//    public void onPrepared() {
//        super.onPrepared();
//        buttonTextCell1 = (ButtonTextCell) findViewById(R.id.button_text_cell1);
//        buttonTextCell2 = (ButtonTextCell) findViewById(R.id.button_text_cell2);
//        buttonTextCell3 = (ButtonTextCell) findViewById(R.id.button_text_cell3);
//        btnCommit = (ImageButton) findViewById(R.id.btn_commit);
//
//        if (buttonTextCell1 != null) {
//            buttonTextCell1.setLabelText("计划名称");
//            buttonTextCell1.Button.setVisibility(GONE);
//        }
//
//        if (buttonTextCell2 != null) {
//            buttonTextCell2.setLabelText("开始日期");
//            buttonTextCell2.Button.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    chooseDate(buttonTextCell2);
//                }
//            });
//        }
//
//        if (buttonTextCell3 != null) {
//            buttonTextCell3.setLabelText("合计短缺");
//            buttonTextCell3.setContentText("否");
//            buttonTextCell3.Button.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    chooseYesOrNo(buttonTextCell3);
//                }
//            });
//        }
//
//        if (btnCommit != null) {
//            btnCommit.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_white"));
//            btnCommit.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    if (TextUtils.isEmpty(buttonTextCell2.getContentText())) {
//                        App.Current.toastError(pn_short_report_mgr.this.getContext(), "请先选择开始时间");
//                    } else if(TextUtils.isEmpty(buttonTextCell1.getContentText())) {
//                        App.Current.showError(pn_short_report_mgr.this.getContext(), "请输入计划名称");
//                    } else{
//                        Intent intent = new Intent(getContext(), ShortReportKanbanActivity.class);
//                        intent.putExtra("planName", buttonTextCell1.getContentText());
//                        intent.putExtra("startTime", buttonTextCell2.getContentText());
//                        intent.putExtra("sumshort", buttonTextCell3.getContentText());
//                        App.Current.Workbench.startActivity(intent);
//                    }
//                }
//            });
//        }
//    }
//
//    private void chooseYesOrNo(final ButtonTextCell buttonTextCell3) {
//        final ArrayList<String> times = new ArrayList<String>();
//        times.add("是");
//        times.add("否");
//
//        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (which >= 0) {
//                    String time = times.get(which);
//                    buttonTextCell3.setContentText(time);
//                }
//                dialog.dismiss();
//            }
//        };
//        new AlertDialog.Builder(pn_short_report_mgr.this.getContext()).setTitle("请选择")
//                .setSingleChoiceItems(times.toArray(new String[0]), times.indexOf(buttonTextCell3.getContentText()), listener)
//                .setNegativeButton("取消", null).show();
//    }
//
//    private void chooseDate(final ButtonTextCell buttonTextCell4) {
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        new DatePickerDialog(pn_short_report_mgr.this.getContext(), DatePickerDialog.THEME_DEVICE_DEFAULT_DARK, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                monthString = String.valueOf((month + 1)).length() == 1 ? "0" + (month + 1) : String.valueOf(month + 1);
//                buttonTextCell4.setContentText(year + "-" + monthString + "-" + day);
//            }
//        }, year, month, day).show();
//    }
}
