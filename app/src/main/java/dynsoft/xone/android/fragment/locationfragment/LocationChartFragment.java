package dynsoft.xone.android.fragment.locationfragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataSet;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/11/26.
 */

public class LocationChartFragment extends Fragment {
    private String[] stringTitles = {"工单:", "机型:", "日期:", "检验总数:", "测试不良元件总数:", "直通率:", "班次:"};
    private GridView gridView;
    private BarChart barChartWhf;
    private BarChart barChartRepairtype;
    private SharedPreferences sharedPreferences;
    private long task_order_id;
    private int allCounts;    //测试不良元件总数
    private Map<Integer, String> gridTitles;
    private int seq_id;
    private GridAdapter gridAdapter;
    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initBarChartDatas(task_order_id);
            handler.postDelayed(runnable, 10 * 60 * 1000);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_location_check, null);
        sharedPreferences = getActivity().getSharedPreferences("sop", Context.MODE_PRIVATE);
        task_order_id = sharedPreferences.getLong("task_order_id", 0L);
        String task_order_code = sharedPreferences.getString("task_order", "");
        String item_name = sharedPreferences.getString("item_name", "");
        String production = sharedPreferences.getString("production", "");
        seq_id = sharedPreferences.getInt("seq_id", 0);
        gridView = (GridView) view.findViewById(R.id.gridview);
        barChartWhf = (BarChart) view.findViewById(R.id.barchart_1);
        barChartRepairtype = (BarChart) view.findViewById(R.id.barchart_2);
        gridTitles = new HashMap<Integer, String>();
        gridTitles.put(0, task_order_code);
        gridTitles.put(1, item_name);
        handler = new Handler();
        handler.post(runnable);
        gridAdapter = new GridAdapter();
        gridView.setAdapter(gridAdapter);
        return view;
    }

    private void initBarChartDatas(long task_order_id) {
        String sql = "exec fm_get_location_chart_datas_and ?,?";
        Parameters p = new Parameters().add(1, task_order_id).add(2, seq_id);
//        Parameters p = new Parameters().add(1, 56633);
        App.Current.DbPortal.ExecuteDataSetAsync("core_and", sql, p, new ResultHandler<DataSet>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataSet> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getActivity(), value.Error);
                } else {
                    if (value.Value != null) {
                        if (value.Value.Tables.size() > 2) {
                            DataTable dataTable1 = value.Value.Tables.get(0);
                            DataTable dataTable2 = value.Value.Tables.get(1);
                            DataTable dataTable3 = value.Value.Tables.get(2);
                            ArrayList<DataRow> dataRows1 = new ArrayList<DataRow>();
                            ArrayList<DataRow> dataRows2 = new ArrayList<DataRow>();
                            float tpy = dataTable1.Rows.get(0).getValue("tpy", new BigDecimal(0)).floatValue();// 直通率
                            int all_count = dataTable1.Rows.get(0).getValue("all_count", 0);
                            String cur_date = dataTable1.Rows.get(0).getValue("cur_date", "");
                            String hour = dataTable1.Rows.get(0).getValue("hours", "");
                            for (int i = 0; i < dataTable2.Rows.size(); i++) {
                                dataRows1.add(dataTable2.Rows.get(i));
                                allCounts += dataTable2.Rows.get(i).getValue("counts", 0);
                            }
                            for (int i = 0; i < dataTable3.Rows.size(); i++) {
                                dataRows2.add(dataTable3.Rows.get(i));
                            }
                            setBarChart(barChartRepairtype, dataRows1, "异常类型");
                            setBarChart(barChartWhf, dataRows2, "位号");
                            gridTitles.put(2, cur_date);
                            gridTitles.put(3, String.valueOf(all_count));
                            gridTitles.put(4, String.valueOf(allCounts));
                            gridTitles.put(5, String.valueOf((int) (tpy * 100)) + "%");
                            if (!TextUtils.isEmpty(hour)) {
                                if (Integer.valueOf(hour) >= 20 || Integer.valueOf(hour) < 8) {
                                    gridTitles.put(6, "B班");
                                } else {
                                    gridTitles.put(6, "A班");
                                }
                            }
                            gridAdapter.notifyDataSetChanged();
                        } else if (value.Value.Tables.size() > 1) {
                            DataTable dataTable1 = value.Value.Tables.get(0);
                            DataTable dataTable2 = value.Value.Tables.get(1);
                            ArrayList<DataRow> dataRows1 = new ArrayList<DataRow>();
                            ArrayList<DataRow> dataRows2 = new ArrayList<DataRow>();
                            float tpy = dataTable1.Rows.get(0).getValue("tpy", new BigDecimal(0)).floatValue();// 直通率
                            for (int i = 0; i < dataTable1.Rows.size(); i++) {
                                dataRows1.add(dataTable1.Rows.get(i));
                                allCounts += dataTable1.Rows.get(i).getValue("counts", 0);
                            }
                            for (int i = 0; i < dataTable2.Rows.size(); i++) {
                                dataRows2.add(dataTable2.Rows.get(i));
                            }
                            setBarChart(barChartRepairtype, dataRows1, "异常类型");
                            setBarChart(barChartWhf, dataRows2, "位号");
                            String formatDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
                            String formatHour = new SimpleDateFormat("MM").format(new Date(System.currentTimeMillis()));
                            gridTitles.put(2, formatDate);
                            gridTitles.put(4, String.valueOf(allCounts));
                            gridTitles.put(5, String.valueOf((int) (tpy * 100)) + "%");
                            if (!TextUtils.isEmpty(formatHour)) {
                                if (Integer.valueOf(formatHour) >= 20 || Integer.valueOf(formatHour) < 8) {
                                    gridTitles.put(6, "B班");
                                } else {
                                    gridTitles.put(6, "A班");
                                }
                            }
                            gridAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    private void setBarChart(BarChart barChart, ArrayList<DataRow> dataRows, String title) {
        ArrayList<String> xValue = new ArrayList<String>();
        ArrayList<BarEntry> yValue = new ArrayList<BarEntry>();
        for (int i = 0; i < dataRows.size(); i++) {
            int counts = dataRows.get(i).getValue("counts", 0);
            String comment = dataRows.get(i).getValue("comment", "");
            xValue.add(comment);
            yValue.add(new BarEntry((float) counts, i));
        }

        BarDataSet barDataSet = new BarDataSet(yValue, title);
        barDataSet.setColor(Color.BLUE);//设置第一组数据颜色
        BarData bardata = new BarData(xValue, barDataSet);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return String.valueOf((int) v);
            }
        });

        bardata.setValueTextSize(20);
        barChart.setData(bardata);      //设置图表数据

        Legend legend = barChart.getLegend();
        legend.setTextSize(20);

        barChart.setDescription("");
        barChart.setDescriptionColor(Color.RED);

        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(600);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);  //设置X轴的位置
        barChart.getXAxis().setDrawGridLines(false);    //不显示网格
        barChart.getXAxis().setSpaceBetweenLabels(1);
        barChart.getXAxis().setTextSize(20);
        YAxis axisRight = barChart.getAxisRight();
        axisRight.setEnabled(false);
        barChart.animateXY(0, 0);   //设置动画
    }

    private class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return gridTitles.size();
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
            ViewHolder viewHolder;
            if (view == null) {
                view = View.inflate(getActivity(), R.layout.item_grid_locationchart, null);
                viewHolder = new ViewHolder();
                viewHolder.textViewTitle = (TextView) view.findViewById(R.id.textview_title);
                viewHolder.textViewContent = (TextView) view.findViewById(R.id.textview_content);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.textViewTitle.setText(stringTitles[i]);
            viewHolder.textViewContent.setText(gridTitles.get(i));
            return view;
        }
    }

    class ViewHolder {
        private TextView textViewTitle;
        private TextView textViewContent;
    }

}
