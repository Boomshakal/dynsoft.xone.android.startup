package dynsoft.xone.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import dynsoft.xone.android.adapter.SMTOnPartAdapter;
import dynsoft.xone.android.adapter.SMTOnPartPopupAdapter;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/12/13.
 * SMT 上料看板
 */

public class SMTOnPartActivity extends Activity {
    private static int UPDATETIME = 4 * 60 * 1000;
    private static int UPDATETIMEPOPUPWINDOW = 30 * 1000;
    private static final String FONT_DIGITAL_7 = "fonts" + File.separator + "Digital-7Mono.TTF";
    private static final int lastPoliceNumber = 300;
    private TextView textViewDate;   //日期
    private ListView listView;
    private DataTable dataTable;      //获取上料数-消耗数小于等于300的数据
    private String value;
    private PopupWindow popupWindow;
    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initListViewDatas();                                    //设置ListView数据
            if(popupWindow != null && popupWindow.isShowing()) {       //如果弹出了小于300的物料，30秒更新，否则4分钟
                handler.postDelayed(runnable, UPDATETIMEPOPUPWINDOW);
            } else {
                handler.postDelayed(runnable, UPDATETIME);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smt_onpart);
        textViewDate = (TextView) findViewById(R.id.textview_date);
        listView = (ListView) findViewById(R.id.listview);
        handler = new Handler();
        getIntentValue();
        setTextViewDate();                                      //设置时间的文本数据
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private void getIntentValue() {
        Intent intent = getIntent();
        value = intent.getStringExtra("value");
    }

    private void setTextViewDate() {
        AssetManager assets = SMTOnPartActivity.this.getAssets();
        Typeface fromAsset = Typeface.createFromAsset(assets, FONT_DIGITAL_7);
        textViewDate.setTypeface(fromAsset);
        String curDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        textViewDate.setText(curDate);
    }

    private void initListViewDatas() {
        dataTable = new DataTable();
        String sql = "exec p_fm_smt_on_part_report ?";
        Parameters p = new Parameters().add(1, value);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {

                } else {
                    if (value.Value != null && value.Value.Rows.size() > 0) {    //有数据，加载ListView
                        for (int i = 0; i < value.Value.Rows.size(); i++) {
                            float quantity1 = value.Value.Rows.get(i).getValue("quantity1", new BigDecimal(0)).floatValue();
                            int quantity2 = value.Value.Rows.get(i).getValue("quantity2", 0);
                            if ((quantity1 - quantity2) > 0 && (quantity1 - quantity2) < lastPoliceNumber) {     //报警数据
                                dataTable.Rows.add(value.Value.Rows.get(i));
                            }
                        }

                        //加载ListView的数据
                        SMTOnPartAdapter smtOnPartAdapter = new SMTOnPartAdapter(value.Value, SMTOnPartActivity.this);
                        listView.setAdapter(smtOnPartAdapter);
                        //如果dataTable里面有数据，弹出报警popuowindow,发出语音
                        if (dataTable.Rows.size() > 0) {
                            toastPopupwindow(dataTable);
                            spackLessThenThree(dataTable);
                        }

                    }
                }
            }
        });
    }

    private void toastPopupwindow(DataTable dataTable) {
        popupWindow = new PopupWindow();
        View view = View.inflate(SMTOnPartActivity.this, R.layout.popup_smt_warn, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        ListView listView = (ListView) view.findViewById(R.id.listview);
        setAnimation(imageView);
        listView.setAdapter(new SMTOnPartPopupAdapter(dataTable, SMTOnPartActivity.this));
        popupWindow.setContentView(view);
        popupWindow.setTouchable(true);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        //点击Popupwindow 外面 不让PopupWindow消失
        popupWindow.setOutsideTouchable(false);
        popupWindow.setWidth(width * 8 / 10);
        popupWindow.setHeight(height * 6 / 10);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(listView, Gravity.CENTER, 10, 10);
    }

    private void setAnimation(ImageView imageViewGreen) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.1f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setRepeatMode(Animation.RESTART);
        alphaAnimation.setRepeatCount(AlphaAnimation.INFINITE);
        imageViewGreen.setAnimation(alphaAnimation);

    }

    private void spackLessThenThree(DataTable dataTable) {     //语音少于300的物料

    }
}
