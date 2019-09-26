package dynsoft.xone.android.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/6/6.
 * 采购审核看板
 */

public class PurchaseExamKanbanActivity extends Activity {
    private static int FRESHTIME = 30 * 60 * 1000;
    private static int FRESHLISTTIME = 30 * 1000;
    private ArrayList<Integer> parChartColors;
    private ListView listView;
    private ProgressBar progressBar;
    private PieChart pieChart;
    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initBarchart();
            initListView();
            handler.postDelayed(runnable, FRESHTIME);
        }
    };

    private Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            scrollListView();
            handler.postDelayed(runnable2, FRESHLISTTIME);
        }
    };

    private void scrollListView() {
        int childCount = listView.getChildCount();
        int count = listView.getCount();
        int lastVisiblePosition = listView.getLastVisiblePosition();
        Log.e("len", lastVisiblePosition + "LAST:" + childCount + "COUMT:" + count);
        if (listView.getLastVisiblePosition() >= count - 1) {
            Log.e("len", "SCROLL");
            listView.smoothScrollToPosition(0);
        } else {
            listView.smoothScrollToPosition(lastVisiblePosition + childCount - 2);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchaseexamkanban);
        if (handler == null) {
            handler = new Handler();
        }
        listView = (ListView) findViewById(R.id.listview);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        pieChart = (PieChart) findViewById(R.id.piechart);
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler.removeCallbacks(runnable2);
        }
    }

    private void initBarchart() {
        String sql = "exec pm_get_purchase_exam_per_data_and";
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PurchaseExamKanbanActivity.this, value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    setPieChart(value.Value);
                }
            }
        });
    }


    private void setPieChart(DataTable dataTable) {
        ArrayList<String> xValue = new ArrayList<String>();
        ArrayList<Entry> yValue1 = new ArrayList<Entry>();

        parChartColors = new ArrayList<Integer>();

        for (int i = 0; i < dataTable.Rows.size(); i++) {
            String flow_actors = dataTable.Rows.get(i).getValue("flow_actors", "");
            String name = null;
            if (flow_actors.contains("(")) {
                String[] split = flow_actors.split("\\(");
                name = split[0];
            } else {
                name = flow_actors;
            }
            xValue.add(name);
            yValue1.add(new Entry(dataTable.Rows.get(i).getValue("count", 0), i));
        }

        parChartColors.add(getResources().getColor(R.color.red));
        parChartColors.add(getResources().getColor(R.color.darkmagenta));
        parChartColors.add(getResources().getColor(R.color.fuchsia));
        parChartColors.add(getResources().getColor(R.color.deeppink));
        parChartColors.add(getResources().getColor(R.color.firebrick));
        parChartColors.add(getResources().getColor(R.color.darkorange));
        parChartColors.add(getResources().getColor(R.color.orange));
        parChartColors.add(getResources().getColor(R.color.pink));
        parChartColors.add(getResources().getColor(R.color.gray));
        parChartColors.add(getResources().getColor(R.color.blue));
        parChartColors.add(getResources().getColor(R.color.skyblue));
        parChartColors.add(getResources().getColor(R.color.aquamarine));
        parChartColors.add(getResources().getColor(R.color.lightgreen));
        parChartColors.add(getResources().getColor(R.color.lawngreen));
        parChartColors.add(getResources().getColor(R.color.megmeet_green));
        parChartColors.add(getResources().getColor(R.color.forestgreen));

        PieDataSet pieDataSet = null;
        pieDataSet = new PieDataSet(yValue1, "");
        pieDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return String.valueOf(Math.round(v));
            }
        });

        pieDataSet.setColors(parChartColors);
        PieData pieData = new PieData(xValue, pieDataSet);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
        legend.setTextSize(getResources().getDimension(R.dimen.ikahe_qty_text_size));

        pieChart.setData(pieData);
        pieChart.setActivated(true);
        pieChart.setDescription("");
        pieChart.invalidate();
//        parChartColors.removeAll(parChartColors);
    }


    private void initListView() {
        progressBar.setVisibility(View.VISIBLE);
        String sql = "exec pm_get_purchase_exam_data";
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    progressBar.setVisibility(View.GONE);
                    App.Current.showError(PurchaseExamKanbanActivity.this, value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    progressBar.setVisibility(View.GONE);
                    PurchaseAdapter purchaseAdapter = new PurchaseAdapter(value.Value);
                    listView.setAdapter(purchaseAdapter);
                    handler.postDelayed(runnable2, FRESHLISTTIME);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    class PurchaseAdapter extends BaseAdapter {
        private DataTable mDataTable;

        public PurchaseAdapter(DataTable mDataTable) {
            this.mDataTable = mDataTable;
        }

        @Override
        public int getCount() {
            return mDataTable.Rows.size();
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
            PurchaseViewHolder purchaseViewHolder;
            if (view == null) {
                view = View.inflate(PurchaseExamKanbanActivity.this, R.layout.purchase_exam_item, null);
                purchaseViewHolder = new PurchaseViewHolder();
                purchaseViewHolder.textView1 = (TextView) view.findViewById(R.id.textview_1);
                purchaseViewHolder.textView2 = (TextView) view.findViewById(R.id.textview_2);
                purchaseViewHolder.textView3 = (TextView) view.findViewById(R.id.textview_3);
                purchaseViewHolder.textView5 = (TextView) view.findViewById(R.id.textview_5);
                purchaseViewHolder.textView6 = (TextView) view.findViewById(R.id.textview_6);
                purchaseViewHolder.textView7 = (TextView) view.findViewById(R.id.textview_7);
                view.setTag(purchaseViewHolder);
            } else {
                purchaseViewHolder = (PurchaseViewHolder) view.getTag();
            }
            String item_code = mDataTable.Rows.get(i).getValue("item_code", "");
            String flow_actors = mDataTable.Rows.get(i).getValue("flow_actors", "");
            String vendor_name = mDataTable.Rows.get(i).getValue("vendor_name", "");
            int qty = mDataTable.Rows.get(i).getValue("qty", 0);
            String start_username = mDataTable.Rows.get(i).getValue("start_username", "");
            Date start_time = mDataTable.Rows.get(i).getValue("start_time", new Date(0));
            String plan_date = mDataTable.Rows.get(i).getValue("plan_date", "");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(start_time);

            purchaseViewHolder.textView1.setText(item_code);
            purchaseViewHolder.textView2.setText(flow_actors);
            purchaseViewHolder.textView3.setText(vendor_name);

            purchaseViewHolder.textView5.setText(start_username);
            purchaseViewHolder.textView6.setText(format);
            purchaseViewHolder.textView7.setText(plan_date);
//            if (qty == 0) {   //没有其他送料不良情况
//                purchaseViewHolder.textView4.setText("否");
//                purchaseViewHolder.textView5.setText("");
//                purchaseViewHolder.textView6.setText("");
//                purchaseViewHolder.textView7.setText("");
//            } else {        //有其他送料不良情况
//                purchaseViewHolder.textView4.setText("是");
//                purchaseViewHolder.textView5.setText(start_username);
//                purchaseViewHolder.textView6.setText(format);
//                purchaseViewHolder.textView7.setText(plan_date);
//            }
            return view;
        }
    }

    class PurchaseViewHolder {
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private TextView textView5;
        private TextView textView6;
        private TextView textView7;
    }
}
