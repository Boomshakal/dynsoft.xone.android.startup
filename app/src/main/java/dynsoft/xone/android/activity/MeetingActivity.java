package dynsoft.xone.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/3/7.
 */

public class MeetingActivity extends Activity {
    private static final String FONT_DIGITAL_7 = "fonts" + File.separator
            + "font1.ttf";

    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private TextView textView6;
    private TextView textView7;
    private TextView textView8;
    private TextView textView9;
    private ListView listView1;
    private String fd_place_id;
    private String fd_name;
    private String mWay;
    private int mYear;
    private int mMonth;
    private int mDay;
    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getCurrentTime();
            handler.postDelayed(runnable, 1000);
        }
    };
    private Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            getCurrentMetting();
            getMettingRoomDate();
            handler.postDelayed(runnable2, 30 * 1000);
        }
    };
    private View view;
    private Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        AssetManager assets = this.getAssets();
        font = Typeface.createFromAsset(assets, FONT_DIGITAL_7);
        Intent intent = getIntent();
        fd_place_id = intent.getStringExtra("fd_place_id");
        fd_name = intent.getStringExtra("fd_name");
        handler = new Handler();
        initView();
    }

    private void initView() {
        textView1 = (TextView) findViewById(R.id.text_view_1);
        textView2 = (TextView) findViewById(R.id.text_view_2);
        textView3 = (TextView) findViewById(R.id.text_view_3);
        textView4 = (TextView) findViewById(R.id.text_view_4);
        textView5 = (TextView) findViewById(R.id.text_view_5);
        textView6 = (TextView) findViewById(R.id.text_view_6);
        textView7 = (TextView) findViewById(R.id.text_view_7);
        textView8 = (TextView) findViewById(R.id.text_view_8);
        textView9 = (TextView) findViewById(R.id.text_view_9);
        listView1 = (ListView) findViewById(R.id.list_view_1);

        if (textView4 != null) {
            textView4.setText(fd_name);
        }

        if (textView5 != null) {
            textView5.setText("当前会议");
        }

        textView1.setTypeface(font);
        textView2.setTypeface(font);
        textView3.setTypeface(font);
        textView4.setTypeface(font);
//        textView5.setTypeface(font);
        textView6.setTypeface(font);
        textView7.setTypeface(font);
        textView8.setTypeface(font);
        textView9.setTypeface(font);

        handler.post(runnable);
        handler.post(runnable2);
    }

    private void getMettingRoomDate() {
        String sql = "exec p_hr_get_imeeting_main ?";
        Parameters p = new Parameters().add(1, fd_place_id);
        App.Current.DbPortal.ExecuteDataTableAsync("mgmt_eip_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(MeetingActivity.this, value.Error);
                    return;
                }
                if (value.Value != null) {
                    MyListViewAdapter1 myListViewAdapter1 = new MyListViewAdapter1(value.Value);
                    if (view == null) {
                        view = View.inflate(MeetingActivity.this, R.layout.meeting_listview_item, null);
                        TextView textview1 = (TextView) view.findViewById(R.id.item_textview_1);
                        TextView textview2 = (TextView) view.findViewById(R.id.item_textview_2);
                        TextView textview3 = (TextView) view.findViewById(R.id.item_textview_3);
                        TextView textview4 = (TextView) view.findViewById(R.id.item_textview_4);
                        textview1.setText("会议议题");
                        textview2.setText("日期");
                        textview3.setText("时间");
                        textview4.setText("预订人");
                    }
                    if (listView1.getHeaderViewsCount() < 1) {
                        listView1.addHeaderView(view);
                    }
                    listView1.setAdapter(myListViewAdapter1);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (handler == null) {
            handler.post(runnable);
            handler.post(runnable2);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler.removeCallbacks(runnable2);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler.removeCallbacks(runnable2);
        }
    }

    public void getCurrentMetting() {
        String sql = "exec f_hr_get_imeeting_sumarry ?";
        Parameters p = new Parameters().add(1, fd_place_id);
        App.Current.DbPortal.ExecuteRecordAsync("mgmt_eip_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.toastError(MeetingActivity.this, value.Error);
                    return;
                }
                if (value.Value != null) {
                    setCurrentText(value.Value);            //设置当前会议的文字数据
                }
            }
        });
    }

    private void setCurrentText(DataRow value) {
        String imeeting_current = value.getValue("imeeting_current", "");
        String fd_detail = value.getValue("fd_detail", "");
        textView6.setText(imeeting_current);
        textView8.setText(fd_detail);
    }

    public void getCurrentTime() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = c.get(Calendar.YEAR); // 获取当前年份
        mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
        mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }

        if (textView1 != null) {
            textView1.setText(mYear + "年" + mMonth + "月" + mDay + "日");
        }

        if (textView2 != null) {
            textView2.setText("星期" + mWay);
        }

        if (textView3 != null) {
            textView3.setText(format);
        }
    }

    class MyListViewAdapter1 extends BaseAdapter {
        private DataTable dataTable;

        public MyListViewAdapter1(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        @Override
        public int getCount() {
            return dataTable.Rows.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder1 viewHolder1;
            if (view == null) {
                view = View.inflate(MeetingActivity.this, R.layout.meeting_listview_item, null);
                viewHolder1 = new ViewHolder1();
                viewHolder1.textView1 = (TextView) view.findViewById(R.id.item_textview_1);
                viewHolder1.textView2 = (TextView) view.findViewById(R.id.item_textview_2);
                viewHolder1.textView3 = (TextView) view.findViewById(R.id.item_textview_3);
                viewHolder1.textView4 = (TextView) view.findViewById(R.id.item_textview_4);
                view.setTag(viewHolder1);
            } else {
                viewHolder1 = (ViewHolder1) view.getTag();
            }
            viewHolder1.textView1.setText(dataTable.Rows.get(i).getValue("fd_name", ""));
            viewHolder1.textView2.setText(dataTable.Rows.get(i).getValue("fd_date", ""));
            String metting_duration = dataTable.Rows.get(i).getValue("fd_hold_date", "") + "-" + dataTable.Rows.get(i).getValue("fd_finish_date", "");
            viewHolder1.textView3.setText(metting_duration);
            viewHolder1.textView4.setText(dataTable.Rows.get(i).getValue("user_name", ""));
            return view;
        }
    }

    class ViewHolder1 {
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private TextView textView4;
    }
}
