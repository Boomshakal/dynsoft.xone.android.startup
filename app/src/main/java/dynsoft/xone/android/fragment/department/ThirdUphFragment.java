package dynsoft.xone.android.fragment.department;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.BigDecimal;
import java.util.ArrayList;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataRowCollection;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.util.BaiduTTSUtil;

/**
 * Created by Administrator on 2018/4/10.
 * 一部车间看板产能达成率fragment
 * String sql = "exec p_qm_first_department_datas1 ?";
 */

public class ThirdUphFragment extends Fragment {
    private static final int CHANGETIME = 10 * 60 * 1000;
    private String[] titleString = {"线别:", "精益组长:", "领班:", "IPQC:", "IE:", "PQE:", "TE:",
            "ME:", "RE:"};               //GridView的数据集
    private GridView gridview;

    private ListView listview;            //显示每天批量
    private PieChart pieChart;            //饼状图
    private TextView textViewFresh;      //刷新
    private ImageView imageViewFresh;      //刷新
    private ImageView changeImageView;

    private TextView textViewDate;

    private BarChart barChart1;
    private BarChart barChart2;
    private BarChart barChart3;

    private LinearLayout linearLayout;       //安灯异常
    private ImageView imageView;             //有安灯异常
    private TextView textView;               //有安灯异常
    private SpeechSynthesizer mSpeechSynthesizer;
    private BaiduTTSUtil baiduTTSUtil;
    private StringBuilder stringBuilder;
    private String curDate;                //显示当前日期

    private ArrayList<Integer> parChartColors;
    private ArrayList<String> titleContentString;
    private int isWeekday = 0;
    private ArrayList<Integer> barChartColors;
    private String segment;

    private View view;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    private String updateTime;

    private int updatePosition = 1;   //自动刷新

    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (updatePosition < 10) {
                segment = "SP-PA" + updatePosition;
            } else {
                segment = "SP-PA1";
            }
            initAll();
            handler.postDelayed(runnable, CHANGETIME);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.third_fragment_uph_kanban, null);
        handler = new Handler();
        gridview = (GridView) view.findViewById(R.id.gridview);
        textViewFresh = (TextView) view.findViewById(R.id.textview_fresh);
        textViewDate = (TextView) view.findViewById(R.id.textview_date);
        imageViewFresh = (ImageView) view.findViewById(R.id.imageview_fresh);
        sharedPreferences = getActivity().getSharedPreferences("third_uph", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();

        linearLayout = (LinearLayout) view.findViewById(R.id.light_linearlayout);
        imageView = (ImageView) view.findViewById(R.id.image_view);
        changeImageView = (ImageView) view.findViewById(R.id.imageview_logo);
        textView = (TextView) view.findViewById(R.id.text_view);
        textView.setSelected(true);
        linearLayout.setVisibility(View.GONE);
        imageView.setImageResource(R.drawable.light_selected);
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        baiduTTSUtil = new BaiduTTSUtil(mSpeechSynthesizer, getActivity());
        baiduTTSUtil.initSpeech();

        barChart1 = (BarChart) view.findViewById(R.id.barchart_1);
        barChart2 = (BarChart) view.findViewById(R.id.barchart_2);
        barChart3 = (BarChart) view.findViewById(R.id.barchart_3);
        String segment = sharedPreferences.getString("segment", "");
        if (TextUtils.isEmpty(segment)) {
            this.segment = "SP-PA1";
        } else {
            this.segment = segment;
        }

        handler.post(runnable);

        textViewFresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sql = "exec fm_getline_by_org_and ?,?";
                Parameters p = new Parameters().add(1, "生产三部").add(2, "SP-PA");
                App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
                    @Override
                    public void handleMessage(Message msg) {
                        final Result<DataTable> value = Value;
                        if (value.HasError) {
                            App.Current.toastError(getActivity(), value.Error);
                            return;
                        }
                        if (value.Value != null && value.Value.Rows.size() > 0) {
                            ArrayList<String> names = new ArrayList<String>();
                            final StringBuilder stringBuilder = new StringBuilder();
                            for (DataRow row : value.Value.Rows) {
                                StringBuffer name = new StringBuffer();
                                name.append(row.getValue("name", ""));
                                names.add(name.toString());
                            }

                            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which >= 0) {
                                        DataRow row = value.Value.Rows.get(which);
                                        StringBuffer name = new StringBuffer();
                                        name.append(row.getValue("name", ""));
                                        stringBuilder.append(name);
                                        ThirdUphFragment.this.segment = name.toString();
                                        edit.putString("segment", name.toString());
                                        edit.commit();
                                        initAll();
                                    }
                                    dialog.dismiss();
                                }
                            };
                            new AlertDialog.Builder(getActivity()).setTitle("请选择")
                                    .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                                            (stringBuilder.toString()), listener)
                                    .setNegativeButton("取消", null).show();
                        }
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initAll();
    }

    private void initAll() {
        initTitleData();           //获取标题的数据
        initView();  //初始化textview、LED时间、图表相关
        initBarChartData();     //设置柱状图数据
        initListViewData();     //设置ListView的数据
        initLightView();        //异常呼叫
    }

    private void initView() {
        listview = (ListView) view.findViewById(R.id.listview);
        pieChart = (PieChart) view.findViewById(R.id.piechart);

        if (imageViewFresh != null) {
            if (isAdded()) {
                imageViewFresh.setImageDrawable(getResources().getDrawable(R.drawable.fresh_blue));
            }
            imageViewFresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAnimation(imageViewFresh);
                    initBarChartData();     //设置柱状图数据
                    initListViewData();     //设置ListView的数据
                    initLightView();        //异常呼叫
                }
            });
        }
    }

    private void setAnimation(ImageView imageViewFresh) {
        Animation animation = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
//        animation.setFillAfter(true);//设置为true，动画转化结束后被应用
        imageViewFresh.startAnimation(animation);//开始动画
    }

    private void initTitleData() {
        String sql = "exec workline_kanban_ipqc_and ?";
        Parameters p = new Parameters().add(1, segment);
        Log.e("len", segment);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(getActivity(), value.Error);
                    return;
                }
                if (value.Value != null) {
                    titleContentString = new ArrayList<String>();
                    titleContentString.add(segment);
                    titleContentString.add(value.Value.getValue("lean_team_leader", ""));
                    titleContentString.add(value.Value.getValue("responsible_man_name", ""));
                    titleContentString.add(value.Value.getValue("ipqc_name", ""));
                    titleContentString.add(value.Value.getValue("ie_name", ""));
                    titleContentString.add(value.Value.getValue("pqe_name", ""));
                    titleContentString.add(value.Value.getValue("te_name", ""));
                    titleContentString.add(value.Value.getValue("me_name", ""));
                    titleContentString.add(value.Value.getValue("pe_name", ""));
                    MyAdapter myAdapter = new MyAdapter(titleContentString);
                    gridview.setNumColumns(3);
                    gridview.setAdapter(myAdapter);
                }
            }
        });
    }

    private void initLightView() {        //获取当前线是否有未处理的异常呼叫
        String sql = "exec get_fm_kanban_light_notice ?";
        Parameters p = new Parameters().add(1, segment);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataTable> value = Value;
                if (value.HasError) {
                    linearLayout.setVisibility(View.GONE);
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    stringBuilder = new StringBuilder();
                    linearLayout.setVisibility(View.VISIBLE);
                    new Thread() {
                        @Override
                        public void run() {
                            int i = 0;
                            while (i < value.Value.Rows.size()) {
                                DataRow dataRow = value.Value.Rows.get(i);
                                String dev_name = dataRow.getValue("dev_name", "");
                                String exception_type = dataRow.getValue("exception_type", "");
                                String rep_user = dataRow.getValue("rep_user", "");
                                mSpeechSynthesizer.speak(segment + dev_name + "有" + exception_type + "呼叫。" + "请" + rep_user + "速到现场");
                                mSpeechSynthesizer.setSpeechSynthesizerListener(new SpeechSynthesizerListener() {
                                    @Override
                                    public void onSynthesizeStart(String s) {

                                    }

                                    @Override
                                    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {

                                    }

                                    @Override
                                    public void onSynthesizeFinish(String s) {

                                    }

                                    @Override
                                    public void onSpeechStart(String s) {

                                    }

                                    @Override
                                    public void onSpeechProgressChanged(String s, int i) {

                                    }

                                    @Override
                                    public void onSpeechFinish(String s) {
                                        //监听到播放结束
                                        SystemClock.sleep(2000);
                                    }

                                    @Override
                                    public void onError(String s, SpeechError speechError) {

                                    }
                                });
                                i++;
                            }
                        }
                    }.start();
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        DataRow dataRow = value.Value.Rows.get(i);
                        String dev_name = dataRow.getValue("dev_name", "");
                        String exception_type = dataRow.getValue("exception_type", "");
                        String rep_user = dataRow.getValue("rep_user", "");
                        if (i == value.Value.Rows.size() - 1) {
                            stringBuilder.append(segment + dev_name + "有" + exception_type + "呼叫。" + "请" + rep_user + "速到现场。");
                        } else {
                            stringBuilder.append(segment + dev_name + "有" + exception_type + "呼叫," + "请" + rep_user + "速到现场，");
                        }
                    }
                    textView.setText(stringBuilder.toString());
                    if (isAdded()) {
                        textView.setTextColor(App.Current.Workbench.getResources().getColor(R.color.red));
                    }
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, "alpha", 1, 0, 1, 0, 1, 0, 1, 0, 1);
                    objectAnimator.setRepeatMode(ValueAnimator.RESTART);
                    objectAnimator.setDuration(500);
                    objectAnimator.setRepeatCount(AlphaAnimation.INFINITE);
                    objectAnimator.start();
                } else {
                    linearLayout.setVisibility(View.GONE);
                }
            }
        });
//        if (imageViewFresh != null) {
//            imageViewFresh.clearAnimation();
//        }
    }

    private void initListViewData() {
        //获取数据
        String sql = "exec p_fm_get_third_work_board_and ?,?";
        Parameters p = new Parameters().add(1, segment).add(2, isWeekday);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastInfo(getActivity(), segment + value.Error + "11111111");
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    int planQty = 0;                    //计划产能
                    int actualQty = 0;                    //实际产能
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        int plan = Math.round(value.Value.Rows.get(i).getValue("item_upph", new BigDecimal(0)).floatValue());
                        int actual = value.Value.Rows.get(i).getValue("qty", 0);
                        long id = value.Value.Rows.get(i).getValue("id", 0L);
                        if (id < 100) {
                            planQty += plan;
                            actualQty += actual;
                        }
                    }
                    setPieChart(planQty, actualQty);
                    MyListAdapter listAdapter = new MyListAdapter(value.Value.Rows);
                    listview.setAdapter(listAdapter);
                } else {
                    setPieChart(0, 0);
                }
            }
        });
    }

    private void initBarChartData() {
        final ArrayList<DataRow> hiData = new ArrayList<DataRow>();
        final ArrayList<DataRow> asData = new ArrayList<DataRow>();
        final ArrayList<DataRow> pkData = new ArrayList<DataRow>();
        String sql = "exec p_fm_get_third_weekday_piechart_and ?,?";
        Parameters p = new Parameters().add(1, segment).add(2, isWeekday);
        Log.e("len", segment + "**" + isWeekday);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {

            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastInfo(getActivity(), segment + value.Error + "22222222");
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    curDate = value.Value.Rows.get(0).getValue("cur_date", "");
                    updateTime = value.Value.Rows.get(0).getValue("update_time", "");
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        String work_stage_code = value.Value.Rows.get(i).getValue("work_stage_code", "");
                        if (work_stage_code.toUpperCase().equals("HI")) {
                            DataRow dataRow = value.Value.Rows.get(i);
                            hiData.add(dataRow);
                        }

                        if (work_stage_code.toUpperCase().equals("AS")) {
                            DataRow dataRow = value.Value.Rows.get(i);
                            asData.add(dataRow);
                        }
                        if (work_stage_code.toUpperCase().equals("PK")) {
                            DataRow dataRow = value.Value.Rows.get(i);
                            pkData.add(dataRow);
                        }
                    }
                    addBarChart(hiData, barChart1, false, "HI");
                    addBarChart(asData, barChart2, false, "AS");
                    addBarChart(pkData, barChart3, true, "PK");


                    if (textViewFresh != null) {
                        textViewFresh.setText("上次更新时间:" + updateTime);
                    }
                    if (textViewDate != null) {
                        textViewDate.setText(curDate);
                    }

                } else {

                }
            }
        });
    }

    private void addBarChart(ArrayList<DataRow> dataRows, BarChart barChart, boolean legShow, String description) {
        ArrayList<String> xValue = new ArrayList<String>();
        ArrayList<BarEntry> yValue1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yValue2 = new ArrayList<BarEntry>();
        barChartColors = new ArrayList<Integer>();
        for (int i = 0; i < dataRows.size(); i++) {
            int item_upph = Math.round(dataRows.get(i).getValue("item_upph", new BigDecimal(0)).floatValue());
            int qty = Math.round(dataRows.get(i).getValue("qty", 0));
            String begin_time = removeSmallNumber(dataRows.get(i).getValue("begin_time", ""));
            String end_time = removeSmallNumber(dataRows.get(i).getValue("end_time", ""));
            String beginTime = begin_time.startsWith("0") ? begin_time.substring(1) : begin_time;
            String endTime = end_time.startsWith("0") ? end_time.substring(1) : end_time;
//            xValue.add(beginTime + "-" + endTime);
            xValue.add(endTime);
            if (item_upph == 0) {
                if (qty > 100) {
                    yValue1.add(new BarEntry(1, i));
                } else {
                    yValue1.add(new BarEntry((float) qty / 100, i));
                }
                if (isAdded()) {
                    barChartColors.add(getResources().getColor(R.color.lime));
                }
                yValue2.add(new BarEntry((float) item_upph, i));
            } else {
                float qtyPer = (float) qty / item_upph;
                yValue1.add(new BarEntry(qtyPer, i));
                yValue2.add(new BarEntry((float) 1, i));
                if (qtyPer >= 0.8) {
                    if (isAdded()) {
                        barChartColors.add(getResources().getColor(R.color.lime));
                    }
                } else if (qtyPer >= 0.6) {
                    if (isAdded()) {
                        barChartColors.add(getResources().getColor(R.color.darkorange));
                    }
                } else {
                    if (isAdded()) {
                        barChartColors.add(getResources().getColor(R.color.red));
                    }
                }
            }
        }

        BarDataSet barDataSet1 = new BarDataSet(yValue1, "UPH达成率");
//        barDataSet1.setColors(colorId);//设置第一组数据颜色
        barDataSet1.setColors(barChartColors);//设置第一组数据颜色

        BarDataSet barDataSet2 = new BarDataSet(yValue2, "标准值");
        if (isAdded()) {
            barDataSet2.setColor(getResources().getColor(R.color.dodgerblue));//设置第一组数据颜色
        }

        ArrayList<BarDataSet> threebardata = new ArrayList<BarDataSet>();
        threebardata.add(barDataSet1);
        threebardata.add(barDataSet2);
        BarData bardata = new BarData(xValue, threebardata);
        bardata.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return Math.round(v * 100) + "%";
            }
        });

        bardata.setValueTextSize(10);
        barChart.setData(bardata);      //设置图表数据

        Legend legend = barChart.getLegend();
        legend.setEnabled(legShow);
        if (isAdded()) {
            legend.setTextSize(getResources().getDimension(R.dimen.ikahe_qty_text_size));
        }
        legend.setXEntrySpace(80);
        int[] colors = {Color.GREEN, Color.BLUE};
        String[] titles = {"UPH达成率", "标准值"};
        legend.setCustom(colors, titles);
        legend.setStackSpace(20);
//        String titles[] = {"UPH达成率", "标准值"};
//        int color[] = {App.Current.Workbench.getResources().getColor(R.color.darkorange), App.Current.Workbench.getResources().getColor(R.color.dodgerblue)};
//        legend.setExtra(color, titles);
//        legend.setStackSpace(100);
//        Legend legend = barChart.getLegend();
//        legend.setEnabled(legShow);
//        ArrayList<Integer> colors = new ArrayList<Integer>();
//        colors.add(App.Current.Workbench.getResources().getColor(R.color.megmeet_green));
//        colors.add(App.Current.Workbench.getResources().getColor(R.color.black));
//        colors.add(App.Current.Workbench.getResources().getColor(R.color.red));

        barChart.setDescription("");
        if (isAdded()) {
            barChart.setDescriptionTextSize(getResources().getDimension(R.dimen.activity_horizontal_margin));
            barChart.setDescriptionColor(getResources().getColor(R.color.red));
        }

        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(600);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);  //设置X轴的位置
        barChart.getXAxis().setDrawGridLines(false);    //不显示网格
//        barChart.getXAxis().setLabelRotationAngle(315);
        barChart.getXAxis().setSpaceBetweenLabels(1);
        barChart.getXAxis().setTextSize(10);
//        barChart.getXAxis().setTextSize(getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin));
        YAxis axisLeft = barChart.getAxisLeft();
        YAxis axisRight = barChart.getAxisRight();
        axisLeft.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                return String.valueOf(Math.round(v * 100));
            }
        });
        axisRight.setEnabled(false);
        barChart.animateXY(0, 0);   //设置动画
    }

    private void setPieChart(final int planQty, final int actualQty) {
        parChartColors = new ArrayList<Integer>();
        ArrayList<String> xValue = new ArrayList<String>();
        ArrayList<Entry> yValue1 = new ArrayList<Entry>();

        xValue.add("实际产能");
        xValue.add("计划产能");

        PieDataSet pieDataSet = null;
        if (planQty <= 0 && actualQty <= 0) {
            yValue1.add(new Entry(1, 0));
            yValue1.add(new Entry(1, 1));

            if (isAdded()) {
                parChartColors.add(getResources().getColor(R.color.lime));
                parChartColors.add(getResources().getColor(R.color.dodgerblue));
            }

            pieDataSet = new PieDataSet(yValue1, "");
            pieDataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                    return "";
                }
            });
        } else {
            if (actualQty < planQty && planQty != 0) {
                float last = (float) (planQty - actualQty) / planQty;      //剩下的比重
                final float actual = (float) actualQty / planQty;    //实际占计划的比重
                yValue1.add(new Entry(actual, 0));
                yValue1.add(new Entry(last, 1));

                if (actual >= 0.8) {
                    if (isAdded()) {
                        parChartColors.add(getResources().getColor(R.color.lime));
                    }
                } else if (actual >= 0.6) {
                    if (isAdded()) {
                        parChartColors.add(getResources().getColor(R.color.darkorange));
                    }
                } else {
                    parChartColors.add(Color.RED);
                }
                if (isAdded()) {
                    parChartColors.add(getResources().getColor(R.color.dodgerblue));
                }

                pieDataSet = new PieDataSet(yValue1, "");
                pieDataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                        if (v == actual) {
                            return removeSmallNumber(String.valueOf(actualQty));
                        } else {
                            return removeSmallNumber(String.valueOf(planQty));
                        }
                    }
                });
            } else {
                yValue1.add(new Entry(actualQty, 0));
                yValue1.add(new Entry(0, 1));
                if (isAdded()) {
                    parChartColors.add(App.Current.Workbench.getResources().getColor(R.color.lime));
                    parChartColors.add(App.Current.Workbench.getResources().getColor(R.color.dodgerblue));
                }

                pieDataSet = new PieDataSet(yValue1, "");
                pieDataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                        if (v == 0) {
                            return removeSmallNumber(String.valueOf(planQty));
                        } else {
                            return removeSmallNumber(String.valueOf(actualQty));
                        }
                    }
                });
            }
        }

        pieDataSet.setColors(parChartColors);
        PieData pieData = new PieData(xValue, pieDataSet);

        Legend legend = pieChart.getLegend();
        if (isAdded()) {
            legend.setTextSize(getResources().getDimension(R.dimen.ikahe_qty_text_size));
        }

        pieChart.setData(pieData);
        pieChart.setActivated(true);
        pieChart.setDescription("");
        pieChart.invalidate();
//        parChartColors.removeAll(parChartColors);
    }

    private String removeSmallNumber(String plan_quantity) {
        if (plan_quantity.indexOf(".") > 0) {
            plan_quantity = plan_quantity.replaceAll("0+?$", "");//去掉多余的0
            plan_quantity = plan_quantity.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return plan_quantity;
    }


    @Override
    public void onStop() {
        super.onStop();
        mSpeechSynthesizer.stop();
        mSpeechSynthesizer.release();
    }

    public double SplitAndRound(double a, int n) {
        a = a * Math.pow(10, n);
        return (Math.round(a)) / (Math.pow(10, n));
    }

    class MyAdapter extends BaseAdapter {                //gridview的适配器
        private ArrayList<String> titleContentString;

        public MyAdapter(ArrayList<String> titleContentString) {
            this.titleContentString = titleContentString;
        }

        @Override
        public int getCount() {
            return titleString.length;
        }

        @Override
        public Object getItem(int i) {
            return titleString[i];
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
                view = View.inflate(getActivity(), R.layout.production_griditem, null);
                viewHolder.textView_title = (TextView) view.findViewById(R.id.textview_title);
                viewHolder.textView_content = (TextView) view.findViewById(R.id.textview_content);
                view.setTag(viewHolder);
            } else {
                viewHolder = (MyAdapter.ViewHolder) view.getTag();
            }
            viewHolder.textView_title.setText(titleString[i]);
            if (titleContentString.get(i) != null) {
                viewHolder.textView_content.setText(titleContentString.get(i));
                if (isAdded()) {
                    viewHolder.textView_content.setTextColor(getResources().getColor(R.color.deepskyblue));
                }
            }
            return view;
        }

        class ViewHolder {                          //GridView的viewholder
            TextView textView_title;
            TextView textView_content;
        }
    }

    class MyListAdapter extends BaseAdapter {
        DataRowCollection dataTables;

        public MyListAdapter(DataRowCollection dataTables) {
            this.dataTables = dataTables;
        }

        @Override
        public int getCount() {
            return dataTables == null ? 0 : dataTables.size();
        }

        @Override
        public Object getItem(int i) {
            return dataTables.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ListViewHolder listViewHolder;
            if (view == null) {
                view = View.inflate(getActivity(), R.layout.item_kanban, null);
                listViewHolder = new ListViewHolder();
                listViewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout);
                listViewHolder.textView_1 = (TextView) view.findViewById(R.id.textview_1);
                listViewHolder.textView_2 = (TextView) view.findViewById(R.id.textview_2);
                listViewHolder.textView_3 = (TextView) view.findViewById(R.id.textview_3);
//                listViewHolder.textView_4 = (TextView) view.view.findViewById(R.id.textview_4);
                listViewHolder.textView_5 = (TextView) view.findViewById(R.id.textview_5);
                listViewHolder.textView_6 = (TextView) view.findViewById(R.id.textview_6);
                listViewHolder.textView_7 = (TextView) view.findViewById(R.id.textview_7);
                listViewHolder.textView_8 = (TextView) view.findViewById(R.id.textview_8);
                listViewHolder.textView_9 = (TextView) view.findViewById(R.id.textview_9);
                listViewHolder.textView_10 = (TextView) view.findViewById(R.id.textview_10);
                listViewHolder.textView_11 = (TextView) view.findViewById(R.id.textview_11);
                listViewHolder.textView_12 = (TextView) view.findViewById(R.id.textview_12);
//                listViewHolder.textView_13 = (TextView) view.view.findViewById(R.id.textview_13);
                view.setTag(listViewHolder);
            } else {
                listViewHolder = (ListViewHolder) view.getTag();
            }
            String begin_time = dataTables.get(i).getValue("beg_time", "");
            String end_time = dataTables.get(i).getValue("end_time", "");
            String[] split = begin_time.split(" ");
            String[] split1 = end_time.split(" ");
            int item_upph = Math.round(dataTables.get(i).getValue("item_upph", new BigDecimal(0)).floatValue());
            int item_uph = Math.round(dataTables.get(i).getValue("item_uph", new BigDecimal(0)).floatValue());
            int qty = Math.round(dataTables.get(i).getValue("qty", 0));
            long id = dataTables.get(i).getValue("id", 0L);
            int fail_count = dataTables.get(i).getValue("fail_count", 0);  //不良数
            float item_ct = dataTables.get(i).getValue("item_ct", new BigDecimal(0)).floatValue();
            //            int average_qty = Math.round(dataTables.get(i).getValue("average_qty", 0).floatValue());
            listViewHolder.textView_1.setText(removeSmallNumber(dataTables.get(i).getValue("beg_time", "")));
            listViewHolder.textView_2.setText(removeSmallNumber(dataTables.get(i).getValue("end_time", "")));
            listViewHolder.textView_3.setText(dataTables.get(i).getValue("task_order_code", ""));
//            listViewHolder.textView_4.setText(dataTables.get(i).getValue("item_code", ""));
            listViewHolder.textView_5.setText(dataTables.get(i).getValue("item_name", ""));
            String work_stage_code = dataTables.get(i).getValue("work_stage_code", "");
            if (work_stage_code.equals("HI")) {
                listViewHolder.textView_6.setText("HI-TU");
            } else {
                listViewHolder.textView_6.setText(work_stage_code);
            }
            listViewHolder.textView_7.setText(String.valueOf(item_upph));   //计划产能
            listViewHolder.textView_8.setText(String.valueOf(qty));         //实际产能
            listViewHolder.textView_9.setText(removeSmallNumber(String.valueOf(item_ct)));                      //节拍
            listViewHolder.textView_10.setText(String.valueOf(fail_count));
            if (isAdded()) {
                listViewHolder.textView_10.setTextColor(getResources().getColor(R.color.red));
            }
            if (qty > 0) {
                String failPercent = String.valueOf(((float) fail_count / (float) qty) * 100);   //百分比
                if (failPercent.length() > 4) {
                    listViewHolder.textView_11.setText(failPercent.substring(0, 4) + "%");
                } else {
                    listViewHolder.textView_11.setText(failPercent + "%");
                }
            }
            if (isAdded()) {
                listViewHolder.textView_11.setTextColor(getResources().getColor(R.color.red));
            }
//            listViewHolder.textView_6.setText(String.valueOf(average_qty));
//            listViewHolder.textView_7.setText(dataTables.get(i).getValue("quantity", 0).toString());
//            listViewHolder.textView_8.setText(String.valueOf(Math.round(dataTables.get(i).getValue("投入差异", new BigDecimal(0)).floatValue())));
//            listViewHolder.textView_9.setText(dataTables.get(i).getValue("sequence_name", ""));
//            listViewHolder.textView_10.setText(dataTables.get(i).getValue("quantity_cc", 0).toString());
//            listViewHolder.textView_11.setText(String.valueOf(Math.round(dataTables.get(i).getValue("average_qty_cc", new BigDecimal(0)).floatValue())));
            listViewHolder.textView_12.setText(String.valueOf(qty - item_upph));
//            listViewHolder.textView_13.setText(dataTables.get(i).getValue("sequence_name_cc", ""));

            if (id > 100) {
                if (isAdded()) {
                    listViewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.dodgerblue));
                }
            } else {
                if (isAdded()) {
                    listViewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.skyblue));
                }
            }
            return view;
        }
    }

    class ListViewHolder {
        private LinearLayout linearLayout;
        private TextView textView_1;
        private TextView textView_2;
        private TextView textView_3;
        //        private TextView textView_4;
        private TextView textView_5;
        private TextView textView_6;
        private TextView textView_7;
        private TextView textView_8;
        private TextView textView_9;
        private TextView textView_10;
        private TextView textView_11;
        private TextView textView_12;
//        private TextView textView_13;
    }
}
