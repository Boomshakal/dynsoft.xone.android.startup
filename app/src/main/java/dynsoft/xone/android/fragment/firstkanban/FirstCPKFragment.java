package dynsoft.xone.android.fragment.firstkanban;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.BigDecimal;
import java.util.ArrayList;

import dynsoft.xone.android.adapter.CPKItemsAdapter;
import dynsoft.xone.android.bean.CPKBean;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataSet;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.fragment.thirdkanban.ThirdCPKFragment;

/**
 * Created by Administrator on 2018/10/11.
 */

public class FirstCPKFragment extends Fragment {
    private static final String[] cpkTitles = {"制品名称", "管控工序", "管控项目", "群组数大小"
            , "下限LSL", "中心限CL", "上限USL", "测量单位", "下限UCL", "中心限CL", "上限LCL"
            , "线别", "上限R", "中限R", "下限R", "采集时间"};
    private ArrayList<String> cpkContent;
    private ProgressBar progressBar;
    private GridView gridView;
    private ListView listView1;
    private ListView listViewRight;
    private LineChart lineChart1;
    private LineChart lineChart2;

    private String textString;
    private DataTable mDataTable;
    private ListView itemListView;
    private MyItemListAdapter myItemListAdapter;
    private MyAdapter myAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    private PopupWindow popupWindow;
    private int item_id;
    private int seq_id;
    private String project_name;
    private String max_v;
    private String min_v;
    private String unit;
    private String item_name;
    private String sequence_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_third_cpk, null);
        cpkContent = new ArrayList<String>();
        progressBar = (ProgressBar) view.findViewById(R.id.progrssbar);
        gridView = (GridView) view.findViewById(R.id.cpk_gridview);
        listView1 = (ListView) view.findViewById(R.id.cpk_listview);
        listViewRight = (ListView) view.findViewById(R.id.cpk_listview_right);
        lineChart1 = (LineChart) view.findViewById(R.id.cpk_linechart_1);
        lineChart2 = (LineChart) view.findViewById(R.id.cpk_linechart_2);
        sharedPreferences = getActivity().getSharedPreferences("cpk", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();

        item_name = sharedPreferences.getString("item_name", "");
        sequence_name = sharedPreferences.getString("sequence_name", "");
        project_name = sharedPreferences.getString("project_name", "");
        cpkContent.add(item_name);
        cpkContent.add(sequence_name);
        cpkContent.add(project_name);
        initView();
        return view;
    }

    private void initView() {
        //GridView 适配器
        myAdapter = new MyAdapter(cpkContent);
        gridView.setAdapter(myAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view.findViewById(R.id.textview_title);
                textString = textView.getText().toString();
                if ("制品名称".equals(textString)) {
                    //选择制品名称
                    toastPopup("fm_cpk_get_item_code_and", 1);
                } else if ("管控工序".equals(textString)) {
                    //选择管控工序
                    toastPopup("fm_cpk_get_sequence_code_and", 1);
                } else if ("管控项目".equals(textString)) {
                    //选择管控项目
                    toastPopup("p_qm_get_cpk_project_name_v1", 2);
                }
            }
        });

        if (!TextUtils.isEmpty(item_name) && !TextUtils.isEmpty(sequence_name) && !TextUtils.isEmpty(project_name)) {
            loadAllDatas();
        }
    }


    private void loadAllDatas() {
        //初始化数据
        item_id = sharedPreferences.getInt("item_id", 0);
        seq_id = sharedPreferences.getInt("sequence_id", 0);
        max_v = sharedPreferences.getString("max_v", "");
        min_v = sharedPreferences.getString("min_v", "");
//        /***
//         * 测试数据库
//         */
//        String sqlTest = "exec p_qm_get_cpk_report_v1 ?,?,?,?,?,?,?,?";
//        Parameters pTest = new Parameters().add(1, item_id).add(2, project_name).add(3,5)
//                .add(4, min_v).add(5, max_v).add(6, String.valueOf(min_v)).add(7, seq_id).add(8, "");
//        App.Current.DbPortal.ExecuteDataSetAsync("test_data_and", sqlTest, pTest, new ResultHandler<DataSet>() {
//            @Override
//            public void handleMessage(Message msg) {
//                Result<DataSet> value = Value;
//                if(value.HasError) {
////                    App.Current.toastError(getActivity(), value.Error);
//                } else {
//                    if(value.Value != null) {
//                        App.Current.toastError(getActivity(), value.Value.Name + "VALUE:" + value.Value.Tables.size());
//                    } else {
////                        App.Current.toastError(getActivity(), "ERROR-NULL");
//                    }
//                }
//            }
//        });

        String sql = "exec p_qm_get_cpk_report_and_p1 ?,?,?,?,?,?,?,?";
        Parameters parameters = new Parameters().add(1, item_id).add(2, project_name).add(3, 5)
                .add(4, min_v).add(5, max_v).add(6, String.valueOf(min_v)).add(7, seq_id).add(8, "");
        Log.e("len", item_id + "**" + project_name + "**" + min_v + "**" + max_v + "**" + seq_id);
        progressBar.setVisibility(View.VISIBLE);
        App.Current.DbPortal.ExecuteRecordAsync("test_data_and", sql, parameters, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
//                    App.Current.toastError(getActivity(), value.Error);
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (value.Value != null) {
                    //第一部分的数据
                    setPartOneData(value.Value);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
//        String sql = "exec p_qm_get_cpk_report_and ?,?,?,?,?,?,?,?";
//        Parameters p = new Parameters().add(1, item_id).add(2, project_name).add(3, 5)
//                .add(4, min_v).add(5, max_v).add(6, String.valueOf(min_v)).add(7, seq_id).add(8, "");
//        Log.e("len", item_id + "**" + project_name + "**" + min_v + "**" + max_v + "**" + seq_id);
//        App.Current.DbPortal.ExecuteDataSetAsync("test_data_and", sql, p, new ResultHandler<DataSet>() {
//            @Override
//            public void handleMessage(Message msg) {
//                Result<DataSet> value = Value;
//                if (value.HasError) {
//                    App.Current.toastError(getActivity(), value.Error);
//                    return;
//                }
//                if (value.Value != null && value.Value.Tables.size() > 0) {
//                    //获取数据，处理
//                    Toast.makeText(getActivity(), "TABLESET:" + value.Value.Tables.size(), Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getActivity(), "AAAAAAAAA", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private void setPartOneData(DataRow row) {
        float x_value = row.getValue("x_value", new BigDecimal(0)).floatValue();
        float r_value = row.getValue("r_value", new BigDecimal(0)).floatValue();
        float x_avg = row.getValue("x_avg", new BigDecimal(0)).floatValue();
        float r_avg = row.getValue("r_avg", new BigDecimal(0)).floatValue();
        float std_dev = row.getValue("std_dev", new BigDecimal(0)).floatValue();
        float sigma = row.getValue("sigma", new BigDecimal(0)).floatValue();
        float ppk = row.getValue("ppk", new BigDecimal(0)).floatValue();
        float pp = row.getValue("pp", new BigDecimal(0)).floatValue();
        String ca = row.getValue("ca", "");
        float cpk = row.getValue("cpk", new BigDecimal(0)).floatValue();
        float cp = row.getValue("cp", new BigDecimal(0)).floatValue();
        String grade = row.getValue("grade", "");
        ArrayList<CPKBean> CPKBeans = new ArrayList<CPKBean>();
        CPKBean cpkBean = new CPKBean("合计", 0);
        CPKBeans.add(cpkBean);
        CPKBean cpkBean1 = new CPKBean(String.format("%.2f", x_value), 1);
        CPKBeans.add(cpkBean1);
        CPKBean cpkBean2 = new CPKBean(String.format("%.2f", r_value), 1);
        CPKBeans.add(cpkBean2);
        CPKBean cpkBean3 = new CPKBean("量测数值的判定条件", 0);
        CPKBeans.add(cpkBean3);
        CPKBean cpkBean4 = new CPKBean("> USL 红色", 0);
        CPKBeans.add(cpkBean4);
        CPKBean cpkBean5 = new CPKBean("< LSL 黑色", 0);
        CPKBeans.add(cpkBean5);
        CPKBean cpkBean6 = new CPKBean("5", 0);
        CPKBeans.add(cpkBean6);
        CPKBean cpkBean7 = new CPKBean("平均", 0);
        CPKBeans.add(cpkBean7);
        CPKBean cpkBean8 = new CPKBean(String.format("%.2f", x_avg), 1);
        CPKBeans.add(cpkBean8);
        CPKBean cpkBean9 = new CPKBean(String.format("%.2f", r_avg), 1);
        CPKBeans.add(cpkBean9);
        CPKBean cpkBean10 = new CPKBean("预估不良率(PPM)", 0);
        CPKBeans.add(cpkBean10);
        CPKBean cpkBean11 = new CPKBean("0", 0);
        CPKBeans.add(cpkBean11);
        CPKBean cpkBean12 = new CPKBean("制程能力分析", 0);
        CPKBeans.add(cpkBean12);
        CPKBean cpkBean13 = new CPKBean(String.format("%.2f", std_dev), 1);
        CPKBeans.add(cpkBean13);
        CPKBean cpkBean14 = new CPKBean(String.format("%.2f", sigma), 1);
        CPKBeans.add(cpkBean14);
        CPKBean cpkBean15 = new CPKBean(String.format("%.2f", ppk), 1);
        CPKBeans.add(cpkBean15);
        CPKBean cpkBean16 = new CPKBean(String.format("%.2f", pp), 1);
        CPKBeans.add(cpkBean16);
        CPKBean cpkBean17 = new CPKBean(ca, 1);
        CPKBeans.add(cpkBean17);
        CPKBean cpkBean18 = new CPKBean(String.format("%.2f", cpk), 1);
        CPKBeans.add(cpkBean18);
        CPKBean cpkBean19 = new CPKBean(String.format("%.2f", cp), 1);
        CPKBeans.add(cpkBean19);
        CPKBean cpkBean20 = new CPKBean(grade, 1);
        CPKBeans.add(cpkBean20);

        CPKItemsAdapter cpkItemsAdapter = new CPKItemsAdapter(CPKBeans, getActivity());
        listViewRight.setAdapter(cpkItemsAdapter);

        cpkContent.add(3, "5");
        int min_usl = row.getValue("min_usl", new BigDecimal(0)).intValue();
        int mid_usl = row.getValue("mid_usl", new BigDecimal(0)).intValue();
        int max_usl = row.getValue("max_usl", new BigDecimal(0)).intValue();
        float min_ucl = row.getValue("min_ucl", new BigDecimal(0)).floatValue();
        float mid_ucl = row.getValue("mid_ucl", new BigDecimal(0)).floatValue();
        float max_ucl = row.getValue("max_ucl", new BigDecimal(0)).floatValue();
        float min_r = row.getValue("min_r", new BigDecimal(0)).floatValue();
        float mid_r = row.getValue("mid_r", new BigDecimal(0)).floatValue();
        float max_r = row.getValue("max_r", new BigDecimal(0)).floatValue();
        String date = row.getValue("date", "");
        Log.e("len", min_usl + "*" + mid_usl + "*" + max_usl + "*" + max_ucl + "*" + mid_ucl
                + "*" + min_ucl + "*" + max_r + "*" + mid_r + "*" + min_r);
        cpkContent.add(4, String.valueOf(min_usl));
        cpkContent.add(5, String.valueOf(mid_usl));
        cpkContent.add(6, String.valueOf(max_usl));
        cpkContent.add(7, unit);

        cpkContent.add(8, String.format("%.2f", min_ucl));
        cpkContent.add(9, String.format("%.2f", mid_ucl));
        cpkContent.add(10, String.format("%.2f", max_ucl));

        cpkContent.add(12, String.format("%.2f", max_r));
        cpkContent.add(13, String.format("%.2f", mid_r));
        cpkContent.add(14, String.format("%.2f", min_r));
        cpkContent.add(15, date);
        myAdapter.notifyDataSetChanged();

        String sql = "exec p_qm_get_cpk_report_and_p2 ?,?,?,?,?,?,?,?";
        Parameters parameters = new Parameters().add(1, item_id).add(2, project_name).add(3, 5)
                .add(4, min_v).add(5, max_v).add(6, String.valueOf(min_v)).add(7, seq_id).add(8, "");
        Log.e("len", item_id + "**" + project_name + "**" + min_v + "**" + max_v + "**" + seq_id);
        App.Current.DbPortal.ExecuteDataTableAsync("test_data_and", sql, parameters, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
//                    App.Current.toastError(getActivity(), value.Error);
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    //第二部分的数据
                    setPartTwoData(value.Value);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setPartTwoData(DataTable value) {

        MyListView1Adapter myListView1Adapter = new MyListView1Adapter(value);
        listView1.setAdapter(myListView1Adapter);


        String sql = "exec p_qm_get_cpk_report_and_p3 ?,?,?,?,?,?,?,?";
        Parameters parameters = new Parameters().add(1, item_id).add(2, project_name).add(3, 5)
                .add(4, min_v).add(5, max_v).add(6, String.valueOf(min_v)).add(7, seq_id).add(8, "");
        Log.e("len", item_id + "**" + project_name + "**" + min_v + "**" + max_v + "**" + seq_id);
        App.Current.DbPortal.ExecuteDataTableAsync("test_data_and", sql, parameters, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
//                    App.Current.toastError(getActivity(), value.Error);
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    //第三部分的数据
                    setPartThreeData(value.Value);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setPartThreeData(DataTable value) {
        float mid_ucl = value.Rows.get(0).getValue("mid_ucl", new BigDecimal(0)).floatValue();
        float max_ucl = value.Rows.get(0).getValue("max_ucl", new BigDecimal(0)).floatValue();
        final float min_ucl = value.Rows.get(0).getValue("min_ucl", new BigDecimal(0)).floatValue();

        ArrayList<Entry> yDatas = new ArrayList<Entry>();
        ArrayList<Entry> yDatas1 = new ArrayList<Entry>();
        for (int i = 0; i < value.Rows.size(); i++) {
            yDatas.add(new Entry(value.Rows.get(i).getValue("value", new BigDecimal(0)).floatValue() - min_ucl, i));
        }
        yDatas1.add(new Entry((max_ucl - min_ucl), 0));
        LineDataSet lineDataSet1 = new LineDataSet(yDatas1, "X图");
        lineDataSet1.setColor(Color.TRANSPARENT);
        lineDataSet1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return "";
            }
        });
        lineDataSet1.setCircleColor(Color.TRANSPARENT);


        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 0; i < value.Rows.size(); i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            xValues.add(String.valueOf(i));
        }

        LineDataSet lineDataSet = new LineDataSet(yDatas, "X图");
        lineDataSet.setColor(Color.BLACK);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return String.format("%.2f", (v + min_ucl));
            }
        });

        Legend legend = lineChart1.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);

        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(lineDataSet);
        lineDataSets.add(lineDataSet1);

        LineData lineData = new LineData(xValues, lineDataSets);
        lineChart1.setDrawGridBackground(false); //表格颜色
        lineChart1.setGridBackgroundColor(Color.GRAY & 0x70FFFFFF); //表格的颜色，设置一个透明度
        lineChart1.setTouchEnabled(true);  //可点击
        lineChart1.setDragEnabled(true);   //可拖拽
        lineChart1.setScaleEnabled(true);  //可缩放
        lineChart1.setPinchZoom(false);
        lineChart1.setBackgroundColor(Color.WHITE); //设置背景颜色

        lineChart1.setData(lineData);
        lineChart1.setDescription("");

        XAxis xAxis = lineChart1.getXAxis();
        //X轴显示的位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

        YAxis axisLeft = lineChart1.getAxisLeft();
//        axisLeft.setSpaceTop(max_ucl);
//        axisLeft.setSpaceBottom(min_ucl);
//        axisLeft.setAxisMaxValue(max_ucl);
//        axisLeft.setAxisMinValue(min_ucl);
        axisLeft.setTextColor(Color.GRAY);    //字体的颜色
        axisLeft.setTextSize(10f); //字体大小
        axisLeft.setGridColor(Color.GRAY);//网格线颜色
        axisLeft.setDrawGridLines(false); //不显示网格线
//        axisLeft.setSpaceTop(20f);
        axisLeft.setSpaceTop(10f);
        axisLeft.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                return String.format("%.2f", (v + min_ucl));
            }
        });


        LimitLine limitLine = new LimitLine((mid_ucl - min_ucl));
        //设置限制线的宽
        limitLine.setLineWidth(1f);
        //设置限制线的颜色
        limitLine.setLineColor(Color.RED);
        //设置基线的位置
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        limitLine.setLabel("中心线");
        limitLine.setTextSize(15);
        //设置限制线为虚线
//        limitLine.enableDashedLine(10f, 10f, 0f);


        LimitLine limitLinetop = new LimitLine((max_ucl - min_ucl));
        //设置限制线的宽
        limitLinetop.setLineWidth(1f);
        //设置限制线的颜色
        limitLinetop.setLineColor(Color.BLUE);
        //设置基线的位置
        limitLinetop.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        limitLinetop.setLabel("最大值");
        limitLinetop.setTextSize(15);
        //设置限制线为虚线
//        limitLinetop.enableDashedLine(10f, 10f, 0f);


        LimitLine limitLinebuttom = new LimitLine((min_ucl - min_ucl));
        //设置限制线的宽
        limitLinebuttom.setLineWidth(1f);
        //设置限制线的颜色
        limitLinebuttom.setLineColor(Color.BLUE);
        //设置基线的位置
        limitLinebuttom.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        limitLinebuttom.setLabel("最小值");
        limitLinebuttom.setTextSize(15);
        //设置限制线为虚线
//        limitLinebuttom.enableDashedLine(10f, 10f, 0f);

        axisLeft.addLimitLine(limitLine);
        axisLeft.addLimitLine(limitLinetop);
        axisLeft.addLimitLine(limitLinebuttom);

        YAxis axisRight = lineChart1.getAxisRight();
        axisRight.setEnabled(false);
//        axisRight.setSpaceTop(max_ucl);
//        axisRight.setSpaceBottom(min_ucl);
//        axisRight.setSpaceBottom(min_ucl);
//        axisRight.setAxisMaxValue(max_ucl);
//        axisRight.setTextColor(Color.GRAY);    //字体的颜色
//        axisRight.setTextSize(10f); //字体大小
//        axisRight.setGridColor(Color.GRAY);//网格线颜色
//        axisRight.setDrawGridLines(false); //不显示网格线

        //设置动画效果
        lineChart1.animateY(2000, Easing.EasingOption.Linear);
        lineChart1.animateX(2000, Easing.EasingOption.Linear);
        lineChart1.invalidate();


//        LineData lineData = LineChartManager.initSingleLineChart(value.Rows.size(), yDatas);
//        LineChartManager.setLineName("实际值");
//        //创建两条折线的图表
//        Log.e("len", "LINE" + lineChart1);
//        LineChartManager.initDataStyle(lineChart1, lineData, getActivity());
//
//        //设置限制线 70代表某个该轴某个值，也就是要画到该轴某个值上
//        float mid_ucl = value.Rows.get(0).getValue("mid_ucl", new BigDecimal(0)).floatValue();
//        float max_ucl = value.Rows.get(0).getValue("max_ucl", new BigDecimal(0)).floatValue();
//        float min_ucl = value.Rows.get(0).getValue("min_ucl", new BigDecimal(0)).floatValue();
//
//        Log.e("len", max_ucl + "**" + min_ucl);
//        LimitLine limitLine = new LimitLine(mid_ucl);
//        //设置限制线的宽
//        limitLine.setLineWidth(1f);
//        //设置限制线的颜色
//        limitLine.setLineColor(Color.RED);
//        //设置基线的位置
//        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
//        limitLine.setLabel("我是基线，也可以叫我水位线");
//        //设置限制线为虚线
//        limitLine.enableDashedLine(10f, 10f, 0f);
//
//
//        YAxis leftAxis = lineChart1.getAxisLeft();
//        //Y轴数据的字体颜色
//        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
//        //左侧Y轴最大值
//        leftAxis.setAxisMaxValue(max_ucl);
//        //左侧Y轴最小值
//        leftAxis.setAxisMinValue(min_ucl);
//        //是否绘制Y轴网格线
//        leftAxis.setDrawGridLines(false);
//        //TODO 什么属性?
//        leftAxis.setDrawGridLines(true);
//        //左边Y轴添加限制线
//        leftAxis.addLimitLine(limitLine);
//        //右侧Y轴坐标
//        YAxis rightAxis = lineChart1.getAxisRight();
//        rightAxis.setTextColor(Color.WHITE);
//        rightAxis.setAxisMinValue(max_ucl);
//        rightAxis.setAxisMinValue(min_ucl);
//        rightAxis.setDrawGridLines(false);
////        //是否绘制等0线
////        rightAxis.setDrawZeroLine(true);


        String sql = "exec p_qm_get_cpk_report_and_p4 ?,?,?,?,?,?,?,?";
        Parameters parameters = new Parameters().add(1, item_id).add(2, project_name).add(3, 5)
                .add(4, min_v).add(5, max_v).add(6, String.valueOf(min_v)).add(7, seq_id).add(8, "");
        Log.e("len", item_id + "**" + project_name + "**" + min_v + "**" + max_v + "**" + seq_id);
        App.Current.DbPortal.ExecuteDataTableAsync("test_data_and", sql, parameters, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
//                    App.Current.toastError(getActivity(), value.Error);
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    //第四部分的数据
                    setPartFourData(value.Value);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setPartFourData(DataTable value) {
        progressBar.setVisibility(View.GONE);
        float mid_ucl = value.Rows.get(0).getValue("mid_r", new BigDecimal(0)).floatValue();
        float max_ucl = value.Rows.get(0).getValue("max_r", new BigDecimal(0)).floatValue();
        float min_ucl = value.Rows.get(0).getValue("min_r", new BigDecimal(0)).floatValue();

        ArrayList<Entry> yDatas = new ArrayList<Entry>();
        ArrayList<Entry> yDatas1 = new ArrayList<Entry>();
        for (int i = 0; i < value.Rows.size(); i++) {
            float value1 = value.Rows.get(i).getValue("value", new BigDecimal(0)).floatValue();
            BigDecimal bd = new BigDecimal((double) value1);
            bd = bd.setScale(2, 4);
            yDatas.add(new Entry(bd.floatValue(), i));
        }

        yDatas1.add(new Entry(max_ucl, 0));

        LineDataSet lineDataSet1 = new LineDataSet(yDatas1, "R图");
        lineDataSet1.setColor(Color.TRANSPARENT);
        lineDataSet1.setCircleColor(Color.TRANSPARENT);

        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 0; i < value.Rows.size(); i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            xValues.add(String.valueOf(i));
        }

        LineDataSet lineDataSet = new LineDataSet(yDatas, "R图");
        lineDataSet.setColor(Color.BLACK);
        lineDataSet.setCircleColor(Color.BLACK);

        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(lineDataSet);
        lineDataSets.add(lineDataSet1);

        Legend legend = lineChart2.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);
//        legend.setTextColor(Color.GREEN);

        LineData lineData = new LineData(xValues, lineDataSets);
        lineChart2.setDrawGridBackground(false); //表格颜色
        lineChart2.setGridBackgroundColor(Color.GRAY & 0x70FFFFFF); //表格的颜色，设置一个透明度
        lineChart2.setTouchEnabled(true);  //可点击
        lineChart2.setDragEnabled(true);   //可拖拽
        lineChart2.setScaleEnabled(true);  //可缩放
        lineChart2.setPinchZoom(false);
        lineChart2.setBackgroundColor(Color.WHITE); //设置背景颜色

        lineChart2.setData(lineData);
        lineChart2.setDescription("");

        LimitLine limitLine = new LimitLine(mid_ucl);
        //设置限制线的宽
        limitLine.setLineWidth(1f);
        //设置限制线的颜色
        limitLine.setLineColor(Color.RED);
        //设置基线的位置
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        limitLine.setLabel("中心线");
        limitLine.setTextSize(15);
        //设置限制线为虚线
//        limitLine.enableDashedLine(10f, 10f, 0f);


        LimitLine limitLinetop = new LimitLine(max_ucl);
        //设置限制线的宽
        limitLinetop.setLineWidth(1f);
        //设置限制线的颜色
        limitLinetop.setLineColor(Color.BLUE);
        //设置基线的位置
        limitLinetop.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        limitLinetop.setLabel("最大值");
        limitLinetop.setTextSize(15);
        //设置限制线为虚线
//        limitLinetop.enableDashedLine(10f, 10f, 0f);


        LimitLine limitLinebuttom = new LimitLine(min_ucl);
        //设置限制线的宽
        limitLinebuttom.setLineWidth(1f);
        //设置限制线的颜色
        limitLinebuttom.setLineColor(Color.BLUE);
        //设置基线的位置
        limitLinebuttom.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        limitLinebuttom.setLabel("最小值");
        limitLinebuttom.setTextSize(15);
        //设置限制线为虚线
//        limitLinebuttom.enableDashedLine(10f, 10f, 0f);


        XAxis xAxis = lineChart2.getXAxis();
        //X轴显示的位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

        YAxis axisLeft = lineChart2.getAxisLeft();
        axisLeft.setSpaceTop(max_ucl);
        axisLeft.setSpaceBottom(min_ucl);
        axisLeft.setAxisMaxValue(max_ucl);
        axisLeft.setAxisMinValue(min_ucl);
        axisLeft.setTextColor(Color.GRAY);    //字体的颜色
        axisLeft.setTextSize(10f); //字体大小
        axisLeft.setGridColor(Color.GRAY);//网格线颜色
        axisLeft.setDrawGridLines(false); //不显示网格线
        axisLeft.addLimitLine(limitLine);
        axisLeft.addLimitLine(limitLinetop);
        axisLeft.addLimitLine(limitLinebuttom);

        YAxis axisRight = lineChart2.getAxisRight();
        axisRight.setSpaceTop(max_ucl);
        axisRight.setSpaceBottom(min_ucl);
        axisRight.setSpaceBottom(min_ucl);
        axisRight.setAxisMaxValue(max_ucl);
        axisRight.setTextColor(Color.GRAY);    //字体的颜色
        axisRight.setTextSize(10f); //字体大小
        axisRight.setGridColor(Color.GRAY);//网格线颜色
        axisRight.setDrawGridLines(false); //不显示网格线
        axisRight.setEnabled(false);


        //设置动画效果
        lineChart2.animateY(2000, Easing.EasingOption.Linear);
        lineChart2.animateX(2000, Easing.EasingOption.Linear);
        lineChart2.invalidate();
    }


    public void toastPopup(final String procString, final int sqlParameters) {
        popupWindow = new PopupWindow();
        View view = View.inflate(getActivity(), R.layout.item_toast_choose_cpk, null);
        final EditText editText = (EditText) view.findViewById(R.id.cpk_edittext);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.cpk_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        itemListView = (ListView) view.findViewById(R.id.cpk_listview);
        editText.setHint("请输入" + textString + "搜索");
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    String replace = editText.getText().toString().replace("\n", "");
                    String sql;
                    Parameters p;
                    String connector;
                    if (sqlParameters == 1) {
                        sql = "exec " + procString + " ?";
                        p = new Parameters().add(1, replace);
                        connector = "core_and";
                    } else {
                        sql = "exec " + procString + " ?,?,?";
                        int item_id = sharedPreferences.getInt("item_id", 0);
                        int sequence_id = sharedPreferences.getInt("sequence_id", 0);
                        p = new Parameters().add(1, item_id).add(2, sequence_id).add(3, "");
                        connector = "test_data_and";
                    }
                    App.Current.DbPortal.ExecuteDataTableAsync(connector, sql, p, new ResultHandler<DataTable>() {
                        @Override
                        public void handleMessage(Message msg) {
                            Result<DataTable> value = Value;
                            if (value.HasError) {
//                                App.Current.toastError(getActivity(), value.Error);
                                progressBar.setVisibility(View.GONE);
                                return;
                            }
                            if (value.Value != null && value.Value.Rows.size() > 0) {
                                mDataTable = value.Value;
                                myItemListAdapter.setData(mDataTable);
                                progressBar.setVisibility(View.GONE);
                            } else {
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                return false;
            }
        });

        //预加载所有的数据给ListView
        String sql;
        Parameters p;
        String connector;
        if (sqlParameters == 1) {
            sql = "exec " + procString + " ?";
            p = new Parameters().add(1, "");
            connector = "core_and";
        } else {
            sql = "exec " + procString + " ?,?,?";
            int item_id = sharedPreferences.getInt("item_id", 0);
            int sequence_id = sharedPreferences.getInt("sequence_id", 0);
            p = new Parameters().add(1, item_id).add(2, sequence_id).add(3, "");
            connector = "test_data_and";
        }
        Log.e("len", "SQL:" + sql);
        App.Current.DbPortal.ExecuteDataTableAsync(connector, sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
//                    App.Current.toastError(getActivity(), value.Error);
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    progressBar.setVisibility(View.GONE);
                    mDataTable = value.Value;
                    myItemListAdapter = new MyItemListAdapter(mDataTable);
                    itemListView.setAdapter(myItemListAdapter);
                    Log.e("len", "mData:" + mDataTable.Rows.size());
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
//                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.style_divider_color));
        popupWindow.showAtLocation(gridView, Gravity.CENTER, 20, 30);
    }

    class MyAdapter extends BaseAdapter {                //gridview的适配器
        private ArrayList<String> titleContentString;

        public MyAdapter(ArrayList<String> titleContentString) {
            this.titleContentString = titleContentString;
        }

        public void setDatas(ArrayList<String> arrayList) {
            titleContentString = arrayList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (titleContentString.size() < cpkTitles.length) {
                for (int i = 0; i <= (cpkTitles.length - titleContentString.size()); i++) {
                    titleContentString.add(" ");
                }
            }
            return cpkTitles.length;
        }

        @Override
        public Object getItem(int i) {
            return cpkTitles[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            MyAdapter.ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new MyAdapter.ViewHolder();
                view = View.inflate(getActivity(), R.layout.cpk_griditem, null);
                viewHolder.textView_title = (TextView) (view.findViewById(R.id.textview_title));
                viewHolder.textView_content = (TextView) (view.findViewById(R.id.textview_content));
                view.setTag(viewHolder);
            } else {
                viewHolder = ( MyAdapter.ViewHolder) view.getTag();
            }
            viewHolder.textView_title.setText(cpkTitles[i]);
            if (titleContentString != null) {
                viewHolder.textView_content.setText(titleContentString.get(i));
                viewHolder.textView_content.setTextColor(getResources().getColor(R.color.deepskyblue));
            }
            return view;
        }

        class ViewHolder {                          //GridView的viewholder
            TextView textView_title;
            TextView textView_content;
        }
    }

    public class MyItemListAdapter extends BaseAdapter {

        private DataTable dataTable;

        public MyItemListAdapter(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        public void setData(DataTable data) {
            this.dataTable = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.dataTable == null ? 0 : this.dataTable.Rows.size();
        }

        @Override
        public Object getItem(int i) {
            return dataTable.Rows.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ItemListViewHolder itemListViewHolder;
            if (view == null) {
                view = View.inflate(getActivity(), R.layout.item_cpk_popup, null);
                itemListViewHolder = new ItemListViewHolder();
                itemListViewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.cpk_linearlayout);
                itemListViewHolder.textViewId = (TextView) view.findViewById(R.id.cpk_item_textview_id);
                itemListViewHolder.textViewCode = (TextView) view.findViewById(R.id.cpk_item_textview_code);
                itemListViewHolder.textViewName = (TextView) view.findViewById(R.id.cpk_item_textview_name);
                itemListViewHolder.textViewUnit = (TextView) view.findViewById(R.id.cpk_item_textview_unit);
                view.setTag(itemListViewHolder);
            } else {
                itemListViewHolder = (ItemListViewHolder) view.getTag();
            }
            if ("制品名称".equals(textString)) {
                final int id = dataTable.Rows.get(i).getValue("id", 0);
                final String code = dataTable.Rows.get(i).getValue("code", "");
                final String name = dataTable.Rows.get(i).getValue("name", "");
                String uom_code = dataTable.Rows.get(i).getValue("uom_code", "");
                itemListViewHolder.textViewId.setText(String.valueOf(id));
                itemListViewHolder.textViewCode.setText(code);
                itemListViewHolder.textViewName.setText(name);
                itemListViewHolder.textViewUnit.setText(uom_code);
                itemListViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cpkContent.remove(0);
                        cpkContent.set(0, name);
                        myAdapter.setDatas(cpkContent);
                        edit.putInt("item_id", id);
                        edit.putString("item_name", name);
                        edit.commit();
                        if (popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                    }
                });
            } else if ("管控工序".equals(textString)) {
                final int sequence_id = dataTable.Rows.get(i).getValue("sequence_id", 0);
                Log.e("len", "SEQ:" + sequence_id);
                final String code = dataTable.Rows.get(i).getValue("code", "");
                final String name = dataTable.Rows.get(i).getValue("name", "");
                String comment = dataTable.Rows.get(i).getValue("comment", "");
                itemListViewHolder.textViewId.setText(String.valueOf(sequence_id));
                itemListViewHolder.textViewCode.setText(code);
                itemListViewHolder.textViewName.setText(name);
                itemListViewHolder.textViewUnit.setText(comment);
                itemListViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cpkContent.remove(1);
                        cpkContent.set(1, code + "-" + name);
                        myAdapter.setDatas(cpkContent);
                        edit.putInt("sequence_id", sequence_id);
                        edit.putString("sequence_name", name);
                        edit.commit();
                        if (popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                    }
                });
            } else if ("管控项目".equals(textString)) {
                final String projects_name = dataTable.Rows.get(i).getValue("project_name", "");
                final String max_v = dataTable.Rows.get(i).getValue("max_v", "");
                final String min_v = dataTable.Rows.get(i).getValue("min_v", "");
                unit = dataTable.Rows.get(i).getValue("unit", "");
                itemListViewHolder.textViewId.setText(projects_name);
                itemListViewHolder.textViewCode.setText(String.valueOf(max_v));
                itemListViewHolder.textViewName.setText(String.valueOf(min_v));
                itemListViewHolder.textViewUnit.setText(unit);
                itemListViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cpkContent.remove(2);
                        cpkContent.set(2, projects_name);
                        cpkContent.set(7, unit);
                        myAdapter.setDatas(cpkContent);
                        edit.putString("project_name", projects_name);
                        project_name = projects_name;
                        edit.putString("max_v", max_v);
                        edit.putString("min_v", min_v);
                        edit.commit();
                        if (popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        Log.d("len", "LOADALLDATAS");
                        loadAllDatas();
                    }
                });
            }
            return view;
        }
    }

    class ItemListViewHolder {
        LinearLayout linearLayout;
        TextView textViewId;
        TextView textViewCode;
        TextView textViewName;
        TextView textViewUnit;
    }

    class MyListView1Adapter extends BaseAdapter {
        private DataTable dataTable;

        public MyListView1Adapter(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        @Override
        public int getCount() {
            return dataTable == null ? 0 : dataTable.Rows.size() + 1;
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
            MyList1ViewHolder myList1ViewHolder;
            if (view == null) {
                view = View.inflate(getActivity(), R.layout.item_cpk_listview1, null);
                myList1ViewHolder = new MyList1ViewHolder();
                myList1ViewHolder.textViewLineNum = (TextView) view.findViewById(R.id.textview_linenum);
                myList1ViewHolder.textViewPiNum = (TextView) view.findViewById(R.id.textview_pinum);
                myList1ViewHolder.textView1 = (TextView) view.findViewById(R.id.textview_1);
                myList1ViewHolder.textView2 = (TextView) view.findViewById(R.id.textview_2);
                myList1ViewHolder.textView3 = (TextView) view.findViewById(R.id.textview_3);
                myList1ViewHolder.textView4 = (TextView) view.findViewById(R.id.textview_4);
                myList1ViewHolder.textView5 = (TextView) view.findViewById(R.id.textview_5);
                myList1ViewHolder.textView6 = (TextView) view.findViewById(R.id.textview_6);
                myList1ViewHolder.textView7 = (TextView) view.findViewById(R.id.textview_7);
                myList1ViewHolder.textView8 = (TextView) view.findViewById(R.id.textview_8);
                myList1ViewHolder.textView9 = (TextView) view.findViewById(R.id.textview_9);
                myList1ViewHolder.textView10 = (TextView) view.findViewById(R.id.textview_10);
                myList1ViewHolder.textView11 = (TextView) view.findViewById(R.id.textview_11);
                myList1ViewHolder.textView12 = (TextView) view.findViewById(R.id.textview_12);
                myList1ViewHolder.textView13 = (TextView) view.findViewById(R.id.textview_13);
                myList1ViewHolder.textView14 = (TextView) view.findViewById(R.id.textview_14);
                myList1ViewHolder.textView15 = (TextView) view.findViewById(R.id.textview_15);
                myList1ViewHolder.textView16 = (TextView) view.findViewById(R.id.textview_16);
                myList1ViewHolder.textView17 = (TextView) view.findViewById(R.id.textview_17);
                myList1ViewHolder.textView18 = (TextView) view.findViewById(R.id.textview_18);
                myList1ViewHolder.textView19 = (TextView) view.findViewById(R.id.textview_19);
                myList1ViewHolder.textView20 = (TextView) view.findViewById(R.id.textview_20);
                myList1ViewHolder.textView21 = (TextView) view.findViewById(R.id.textview_21);
                myList1ViewHolder.textView22 = (TextView) view.findViewById(R.id.textview_22);
                myList1ViewHolder.textView23 = (TextView) view.findViewById(R.id.textview_23);
                myList1ViewHolder.textView24 = (TextView) view.findViewById(R.id.textview_24);
                myList1ViewHolder.textView25 = (TextView) view.findViewById(R.id.textview_25);
                view.setTag(myList1ViewHolder);
            } else {
                myList1ViewHolder = (MyList1ViewHolder) view.getTag();
            }
            if (i == 0) {
                myList1ViewHolder.textViewLineNum.setText("行号");
                myList1ViewHolder.textViewPiNum.setText("批号");
                myList1ViewHolder.textView1.setText("0");
                myList1ViewHolder.textView2.setText("1");
                myList1ViewHolder.textView3.setText("2");
                myList1ViewHolder.textView4.setText("3");
                myList1ViewHolder.textView5.setText("4");
                myList1ViewHolder.textView6.setText("5");
                myList1ViewHolder.textView7.setText("6");
                myList1ViewHolder.textView8.setText("7");
                myList1ViewHolder.textView9.setText("8");
                myList1ViewHolder.textView10.setText("9");
                myList1ViewHolder.textView11.setText("10");
                myList1ViewHolder.textView12.setText("11");
                myList1ViewHolder.textView13.setText("12");
                myList1ViewHolder.textView14.setText("13");
                myList1ViewHolder.textView15.setText("14");
                myList1ViewHolder.textView16.setText("15");
                myList1ViewHolder.textView17.setText("16");
                myList1ViewHolder.textView18.setText("17");
                myList1ViewHolder.textView19.setText("18");
                myList1ViewHolder.textView20.setText("19");
                myList1ViewHolder.textView21.setText("20");
                myList1ViewHolder.textView22.setText("21");
                myList1ViewHolder.textView23.setText("22");
                myList1ViewHolder.textView24.setText("23");
                myList1ViewHolder.textView25.setText("24");
            } else {
                float v0 = dataTable.Rows.get(i - 1).getValue("0", new BigDecimal(0)).floatValue();
                float v1 = dataTable.Rows.get(i - 1).getValue("1", new BigDecimal(0)).floatValue();
                float v2 = dataTable.Rows.get(i - 1).getValue("2", new BigDecimal(0)).floatValue();
                float v3 = dataTable.Rows.get(i - 1).getValue("3", new BigDecimal(0)).floatValue();
                float v4 = dataTable.Rows.get(i - 1).getValue("4", new BigDecimal(0)).floatValue();
                float v5 = dataTable.Rows.get(i - 1).getValue("5", new BigDecimal(0)).floatValue();
                float v6 = dataTable.Rows.get(i - 1).getValue("6", new BigDecimal(0)).floatValue();
                float v7 = dataTable.Rows.get(i - 1).getValue("7", new BigDecimal(0)).floatValue();
                float v8 = dataTable.Rows.get(i - 1).getValue("8", new BigDecimal(0)).floatValue();
                float v9 = dataTable.Rows.get(i - 1).getValue("9", new BigDecimal(0)).floatValue();
                float v10 = dataTable.Rows.get(i - 1).getValue("10", new BigDecimal(0)).floatValue();
                float v11 = dataTable.Rows.get(i - 1).getValue("11", new BigDecimal(0)).floatValue();
                float v12 = dataTable.Rows.get(i - 1).getValue("12", new BigDecimal(0)).floatValue();
                float v13 = dataTable.Rows.get(i - 1).getValue("13", new BigDecimal(0)).floatValue();
                float v14 = dataTable.Rows.get(i - 1).getValue("14", new BigDecimal(0)).floatValue();
                float v15 = dataTable.Rows.get(i - 1).getValue("15", new BigDecimal(0)).floatValue();
                float v16 = dataTable.Rows.get(i - 1).getValue("16", new BigDecimal(0)).floatValue();
                float v17 = dataTable.Rows.get(i - 1).getValue("17", new BigDecimal(0)).floatValue();
                float v18 = dataTable.Rows.get(i - 1).getValue("18", new BigDecimal(0)).floatValue();
                float v19 = dataTable.Rows.get(i - 1).getValue("19", new BigDecimal(0)).floatValue();
                float v20 = dataTable.Rows.get(i - 1).getValue("20", new BigDecimal(0)).floatValue();
                float v21 = dataTable.Rows.get(i - 1).getValue("21", new BigDecimal(0)).floatValue();
                float v22 = dataTable.Rows.get(i - 1).getValue("22", new BigDecimal(0)).floatValue();
                float v23 = dataTable.Rows.get(i - 1).getValue("23", new BigDecimal(0)).floatValue();
                float v24 = dataTable.Rows.get(i - 1).getValue("24", new BigDecimal(0)).floatValue();
                myList1ViewHolder.textViewLineNum.setText(String.valueOf(dataTable.Rows.get(i - 1).getValue("rowline1", 0)));
                myList1ViewHolder.textViewPiNum.setText(dataTable.Rows.get(i - 1).getValue("rowline", ""));
                myList1ViewHolder.textView1.setText(String.format("%.2f", v0));
                myList1ViewHolder.textView2.setText(String.format("%.2f", v1));
                myList1ViewHolder.textView3.setText(String.format("%.2f", v2));
                myList1ViewHolder.textView4.setText(String.format("%.2f", v3));
                myList1ViewHolder.textView5.setText(String.format("%.2f", v4));
                myList1ViewHolder.textView6.setText(String.format("%.2f", v5));
                myList1ViewHolder.textView7.setText(String.format("%.2f", v6));
                myList1ViewHolder.textView8.setText(String.format("%.2f", v7));
                myList1ViewHolder.textView9.setText(String.format("%.2f", v8));
                myList1ViewHolder.textView10.setText(String.format("%.2f", v9));
                myList1ViewHolder.textView11.setText(String.format("%.2f", v10));
                myList1ViewHolder.textView12.setText(String.format("%.2f", v11));
                myList1ViewHolder.textView13.setText(String.format("%.2f", v12));
                myList1ViewHolder.textView14.setText(String.format("%.2f", v13));
                myList1ViewHolder.textView15.setText(String.format("%.2f", v14));
                myList1ViewHolder.textView16.setText(String.format("%.2f", v15));
                myList1ViewHolder.textView17.setText(String.format("%.2f", v16));
                myList1ViewHolder.textView18.setText(String.format("%.2f", v17));
                myList1ViewHolder.textView19.setText(String.format("%.2f", v18));
                myList1ViewHolder.textView20.setText(String.format("%.2f", v19));
                myList1ViewHolder.textView21.setText(String.format("%.2f", v20));
                myList1ViewHolder.textView22.setText(String.format("%.2f", v21));
                myList1ViewHolder.textView23.setText(String.format("%.2f", v22));
                myList1ViewHolder.textView24.setText(String.format("%.2f", v23));
                myList1ViewHolder.textView25.setText(String.format("%.2f", v24));
            }
            return view;
        }
    }

    class MyList1ViewHolder {
        private TextView textViewLineNum;
        private TextView textViewPiNum;
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
        private TextView textView11;
        private TextView textView12;
        private TextView textView13;
        private TextView textView14;
        private TextView textView15;
        private TextView textView16;
        private TextView textView17;
        private TextView textView18;
        private TextView textView19;
        private TextView textView20;
        private TextView textView21;
        private TextView textView22;
        private TextView textView23;
        private TextView textView24;
        private TextView textView25;
    }
}
