package dynsoft.xone.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import org.xml.sax.DTDHandler;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/9/29.
 */

public class RawMaterialKanbanActivity extends Activity {
    private static final int UPDATECUETIME = 1000;
    private static final int UPDATEDATA = 2 * 60 * 1000;
    private int start = 1;
    private int end = 10;
    private TextView textViewTime;    //实时时间
    private TextView textViewDay;    //星期几
    private ListView listView1;
    private ListView listView2;
    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initTime();
            handler.postDelayed(runnable, UPDATECUETIME);
        }
    };
    private Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            if(end <= counts - 10) {
                start += 10;
                end += 10;
            }
            initListView1();
            initListView2();
            handler.postDelayed(runnable2, UPDATEDATA);
        }
    };
    private int counts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.activity_raw_material);
        textViewTime = (TextView) findViewById(R.id.textview_time);
        textViewDay = (TextView) findViewById(R.id.textview_day);
        listView1 = (ListView) findViewById(R.id.listview_1);
        listView2 = (ListView) findViewById(R.id.listview_2);
        handler.post(runnable);
        handler.post(runnable2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler != null) {
            handler.removeCallbacks(runnable);
            handler.removeCallbacks(runnable2);
        }
    }

    private void initListView1() {
        String sql = "exec select_wo_task_order_ycl_info";
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(RawMaterialKanbanActivity.this, value.Error);
                } else {
                    if (value.Value != null) {
                        ListViewAdapter1 listViewAdapter1 = new ListViewAdapter1(value.Value);
                        listView1.setAdapter(listViewAdapter1);
                    } else {

                    }
                }
            }
        });
    }

    private void initListView2() {
        String sql = "exec fm_get_raw_material_and ?,?";
        Parameters p = new Parameters().add(1, start).add(2, end);
        Log.e("len", "start:" + start + "end:" + end);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {

            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if(value.HasError) {
                    App.Current.toastError(RawMaterialKanbanActivity.this, value.Error);
                } else if(value.Value != null && value.Value.Rows.size() > 0) {
                    counts = value.Value.Rows.get(0).getValue("counts", 0);
                    ListViewAdapter2 listViewAdapter2 = new ListViewAdapter2(value.Value);
                    listView2.setAdapter(listViewAdapter2);
                }
            }
        });
    }

    private void initTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        textViewTime.setText(format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        String engWeek = getEngNameByNumber(i);
        String chinaWeek = getChinaNameByNimber(i);
        textViewDay.setText(engWeek + " " + chinaWeek);
    }

    private String getEngNameByNumber(int i) {
        String week = "Monday";
        switch (i) {
            case 1:
                week = "Sunday";
                break;
            case 2:
                week = "Monday";
                break;
            case 3:
                week = "Tuesday";
                break;
            case 4:
                week = "Wednesday";
                break;
            case 5:
                week = "Thursday";
                break;
            case 6:
                week = "Friday";
                break;
            case 7:
                week = "Saturday";
                break;
        }
        return week;
    }

    private String getChinaNameByNimber(int i) {
        String week = "星期一";
        switch (i) {
            case 1:
                week = "星期天";
                break;
            case 2:
                week = "星期一";
                break;
            case 3:
                week = "星期二";
                break;
            case 4:
                week = "星期三";
                break;
            case 5:
                week = "星期四";
                break;
            case 6:
                week = "星期五";
                break;
            case 7:
                week = "星期六";
                break;
        }
        return week;
    }

    class ListViewAdapter1 extends BaseAdapter {
        private DataTable dataTable;

        public ListViewAdapter1(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        @Override
        public int getCount() {
            return dataTable.Rows == null ? 0 : dataTable.Rows.size();
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
            if(view == null) {
                view = View.inflate(RawMaterialKanbanActivity.this, R.layout.item_listview_raw_material_1, null);
                viewHolder1 = new ViewHolder1();
                viewHolder1.textView1 = (TextView) view.findViewById(R.id.txt_number);
                viewHolder1.textView2 = (TextView) view.findViewById(R.id.txt_plan_today);
                viewHolder1.textView3 = (TextView) view.findViewById(R.id.txt_finished_today);
                viewHolder1.textView4 = (TextView) view.findViewById(R.id.txt_un_finish_today);
                viewHolder1.textView5 = (TextView) view.findViewById(R.id.txt_un_finish_last);
                viewHolder1.textView6 = (TextView) view.findViewById(R.id.txt_un_warehouse);
                viewHolder1.textView7 = (TextView) view.findViewById(R.id.txt_un_inventory);
                viewHolder1.textView8 = (TextView) view.findViewById(R.id.txt_all_unfinish);
                viewHolder1.textView9 = (TextView) view.findViewById(R.id.txt_status);
                viewHolder1.textView10 = (TextView) view.findViewById(R.id.txt_user);
                view.setTag(viewHolder1);
            } else {
                viewHolder1 = (ViewHolder1) view.getTag();
            }
            String todayPlan = dataTable.Rows.get(i).getValue("今日计划", 0).toString();
            String todayFinished = dataTable.Rows.get(i).getValue("今日已完成", 0).toString();
            String todayUnfinish = dataTable.Rows.get(i).getValue("今日未完成", 0).toString();
            String lastUnfinish = dataTable.Rows.get(i).getValue("遗留未完成", 0).toString();
            String unHouse = dataTable.Rows.get(i).getValue("待入库", 0).toString();
            String unCheck = dataTable.Rows.get(i).getValue("待盘点", 0).toString();
            String allUnfinish = dataTable.Rows.get(i).getValue("总未完成", 0).toString();
            String status = dataTable.Rows.get(i).getValue("状态", "");
            String manager = dataTable.Rows.get(i).getValue("仓管员", "");
            viewHolder1.textView1.setText(String.valueOf(i + 1));
            viewHolder1.textView2.setText(todayPlan);
            viewHolder1.textView3.setText(todayFinished);
            viewHolder1.textView4.setText(todayUnfinish);
            viewHolder1.textView5.setText(lastUnfinish);
            viewHolder1.textView6.setText(unHouse);
            viewHolder1.textView7.setText(unCheck);
            viewHolder1.textView8.setText(allUnfinish);
            viewHolder1.textView9.setText(status);
            viewHolder1.textView10.setText(manager);
            return view;
        }
    }

    class ViewHolder1 {
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private TextView textView4;
        private TextView textView5;
        private TextView textView6;
        private TextView textView7;
        private TextView textView8;
        private TextView textView9;
        private TextView textView10;
    }


    class ListViewAdapter2 extends BaseAdapter {
        private DataTable dataTable;

        public ListViewAdapter2(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        @Override
        public int getCount() {
            return dataTable.Rows == null ? 0 : dataTable.Rows.size();
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
            ViewHolder2 viewHolder2;
            if(view == null) {
                view = View.inflate(RawMaterialKanbanActivity.this, R.layout.item_listview_raw_material_2, null);
                viewHolder2 = new ViewHolder2();
                viewHolder2.textView1 = (TextView) view.findViewById(R.id.txt_number);
                viewHolder2.textView2 = (TextView) view.findViewById(R.id.txt_item_code);
                viewHolder2.textView3 = (TextView) view.findViewById(R.id.txt_item_name);
                viewHolder2.textView4 = (TextView) view.findViewById(R.id.txt_count);
                viewHolder2.textView5 = (TextView) view.findViewById(R.id.txt_safe_own);
                viewHolder2.textView6 = (TextView) view.findViewById(R.id.txt_library);
                viewHolder2.textView7 = (TextView) view.findViewById(R.id.txt_storage);
                viewHolder2.textView8 = (TextView) view.findViewById(R.id.txt_user);
                viewHolder2.textView9 = (TextView) view.findViewById(R.id.txt_status);
                view.setTag(viewHolder2);
            } else {
                viewHolder2 = (ViewHolder2) view.getTag();
            }
            String number = dataTable.Rows.get(i).getValue("行", 0L).toString();
            String itemCode = dataTable.Rows.get(i).getValue("物料编码", "");
            String itemName = dataTable.Rows.get(i).getValue("物料名称", "");
            String nowCount = dataTable.Rows.get(i).getValue("现有量", "");
            String safeSave = dataTable.Rows.get(i).getValue("安全库存", "");
            String house = dataTable.Rows.get(i).getValue("库位", "");
            String storage = dataTable.Rows.get(i).getValue("储位", "");
            String manager = dataTable.Rows.get(i).getValue("仓管员", "");
            String status = dataTable.Rows.get(i).getValue("状态", "");
            viewHolder2.textView1.setText(String.valueOf(number));
            viewHolder2.textView2.setText(itemCode);
            viewHolder2.textView3.setText(itemName);
            viewHolder2.textView4.setText(nowCount);
            viewHolder2.textView5.setText(safeSave);
            viewHolder2.textView6.setText(house);
            viewHolder2.textView7.setText(storage);
            viewHolder2.textView8.setText(manager);
            viewHolder2.textView9.setText(status);
            return view;
        }
    }

    class ViewHolder2 {
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private TextView textView4;
        private TextView textView5;
        private TextView textView6;
        private TextView textView7;
        private TextView textView8;
        private TextView textView9;
    }
}
