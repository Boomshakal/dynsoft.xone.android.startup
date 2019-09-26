package dynsoft.xone.android.fragment.secondkanban;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.sopactivity.ProductionKanbanActivity;
import dynsoft.xone.android.ui.LEDView;
import dynsoft.xone.android.util.BaiduTTSUtil;

/**
 * Created by Administrator on 2018/5/27.
 */

public class SecondKanbanFragment extends Fragment implements SpeechSynthesizerListener {
    private static final int DELAYTIME = 1000;
    private static final int LISTVIEWDELAYTIME1 = 20 * 60 * 1000;
    private String[] titleString = {"部门:", "线别:", "领班:", "IPQC:", "IE:", "PQE:", "TE:",
            "ME:", "PE:"};               //GridView的数据集

    private LEDView ledview_year;
    private LEDView ledview_month;
    private LEDView ledview_day;
    private LEDView ledview_hour;
    private LEDView ledview_min;
    private LEDView ledview_seconds;

    private EditText editText;
    private EditText editText_1;
    private EditText editText_2;
    private EditText editText_3;
    private EditText editText_4;
    private EditText editText_5;
    private EditText editText_6;
    private EditText editText_7;
    private EditText editText_8;
    private TextView textViewCode;             //工单
    private TextView textviewMen;             //工单
    private TextView textviewSign;             //工单
    private TextView textViewItem;             //机型
    private TextView textViewFresh;           //刷新时间显示
    private ImageView imageViewFresh;         //刷新按钮

    private LinearLayout linearLayout;       //安灯异常
    private ImageView imageView;             //有安灯异常
    private TextView textView;               //有安灯异常
    private SpeechSynthesizer mSpeechSynthesizer;
    private BaiduTTSUtil baiduTTSUtil;
    private StringBuilder stringBuilder;

    private ArrayList<String> codeString;     //工单的数据集

    private GridView gridview;
    private ListView listview1;   //显示每天批量1
    private ListView listview2;   //显示每天批量2
    private BarChart barChart;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private int second;

    private String segment;
    private int id;
    private ArrayList<Integer> barChartColors;

    private int existing_number;
    private int sign_counts;

    private ArrayList<String> titleContentString;
    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setLEDTime();
            handler.postDelayed(runnable, DELAYTIME);
        }
    };
    private Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            initBarChartDatas();   //获取柱状图的数据
            setQtyData(id);        //设置产能的数据
//            if(textCell_1 != null && TextUtils.isEmpty(textCell_1.getContentText())) {
//                initListViewData();
//            }
            initLightView();
            if (textViewFresh != null) {
                textViewFresh.setText("上次更新时间:" + new SimpleDateFormat().format(new Date()));
            }
            handler.postDelayed(runnable2, LISTVIEWDELAYTIME1);
        }
    };
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.fragment_secondkanban, null);

        Intent intent = getActivity().getIntent();
        id = intent.getIntExtra("work_line_id", 0);
        segment = intent.getStringExtra("work_line");
        linearLayout = (LinearLayout) view.findViewById(R.id.light_linearlayout);
        imageView = (ImageView) view.findViewById(R.id.image_view);
        textView = (TextView) view.findViewById(R.id.text_view);
        textView.setSelected(true);
        linearLayout.setVisibility(View.GONE);
        imageView.setImageResource(R.drawable.light_selected);
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        baiduTTSUtil = new BaiduTTSUtil(mSpeechSynthesizer, getActivity());
        baiduTTSUtil.initSpeech();

//        source = "bd_etts_common_speech_yyjw_mand_eng_high_am-mix_v3.0.0_20170512.dat";
        handler = new Handler();
        codeString = new ArrayList<String>();
        gridview = (GridView) view.findViewById(R.id.gridview);
        titleContentString = new ArrayList<String>();
        initTitleData();           //获取标题的数据
        initView();  //初始化textview、LED时间、图表相关
        addListViewHead();  //增加头
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("len", getActivity().getPackageName() + "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("len", getActivity().getPackageName() + "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("len", getActivity().getPackageName() + "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("len", getActivity().getPackageName() + "onDestory");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("len", getActivity().getPackageName() + "onStop");
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        if (handler != null) {
            handler.removeCallbacks(runnable2);
        }
        mSpeechSynthesizer.stop();
        mSpeechSynthesizer.release();
    }

    private void addListViewHead() {
        View view = View.inflate(getActivity(), R.layout.item_production_listview, null);
        if (listview1.getHeaderViewsCount() < 1) {
            listview1.addHeaderView(view);
        }
        if (listview2.getHeaderViewsCount() < 1) {
            listview2.addHeaderView(view);
        }
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
                                mSpeechSynthesizer.setSpeechSynthesizerListener(SecondKanbanFragment.this);
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
                    Log.e("len", stringBuilder.toString());
                    textView.setText(stringBuilder.toString());
                    textView.setTextColor(getResources().getColor(R.color.red));
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, "alpha", 1, 0, 1, 0, 1, 0, 1, 0, 1);
                    objectAnimator.setRepeatMode(ValueAnimator.RESTART);
                    objectAnimator.setDuration(500);
                    objectAnimator.setRepeatCount(AlphaAnimation.INFINITE);
                    objectAnimator.start();
                } else {
                    Log.e("len", "NULL");
                    linearLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initView() {
        ledview_year = (LEDView) view.findViewById(R.id.ledview_year);
        ledview_month = (LEDView) view.findViewById(R.id.ledview_month);
        ledview_day = (LEDView) view.findViewById(R.id.ledview_day);
        ledview_hour = (LEDView) view.findViewById(R.id.ledview_hour);
        ledview_min = (LEDView) view.findViewById(R.id.ledview_min);
        ledview_seconds = (LEDView) view.findViewById(R.id.ledview_seconds);

        editText = (EditText) view.findViewById(R.id.edittext);
        editText_1 = (EditText) view.findViewById(R.id.edittext_1);
        editText_2 = (EditText) view.findViewById(R.id.edittext_2);
        editText_3 = (EditText) view.findViewById(R.id.edittext_3);
        editText_4 = (EditText) view.findViewById(R.id.edittext_4);
        editText_5 = (EditText) view.findViewById(R.id.edittext_5);
        editText_6 = (EditText) view.findViewById(R.id.edittext_6);
        editText_7 = (EditText) view.findViewById(R.id.edittext_7);
        editText_8 = (EditText) view.findViewById(R.id.edittext_8);

        textViewCode = (TextView) view.findViewById(R.id.textview_code);
        textviewMen = (TextView) view.findViewById(R.id.textview_men);
        textviewSign = (TextView) view.findViewById(R.id.textview_sign);
        textViewItem = (TextView) view.findViewById(R.id.textview_item);
        textViewFresh = (TextView) view.findViewById(R.id.textview_fresh);
        imageViewFresh = (ImageView) view.findViewById(R.id.imageview_fresh);

        listview1 = (ListView) view.findViewById(R.id.listview_1);
        listview2 = (ListView) view.findViewById(R.id.listview_2);

        if (imageViewFresh != null) {
            imageViewFresh.setImageDrawable(getResources().getDrawable(R.drawable.fresh_blue));
            imageViewFresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAnimation(imageViewFresh);
                    handler.post(runnable2);
                }
            });
        }

        setReadOnly(editText);
        setReadOnly(editText_2);
        setReadOnly(editText_3);
        setReadOnly(editText_4);
        setReadOnly(editText_5);
        setReadOnly(editText_6);
        setReadOnly(editText_7);
        setReadOnly(editText_8);

        editText.setTextColor(getResources().getColor(R.color.red));
        editText_1.setTextColor(getResources().getColor(R.color.red));
        editText_2.setTextColor(getResources().getColor(R.color.red));
        editText_3.setTextColor(getResources().getColor(R.color.red));
        editText_4.setTextColor(getResources().getColor(R.color.red));
        editText_5.setTextColor(getResources().getColor(R.color.red));
        editText_6.setTextColor(getResources().getColor(R.color.red));
        editText_7.setTextColor(getResources().getColor(R.color.red));
        editText_8.setTextColor(getResources().getColor(R.color.red));

//        setLEDTime();

        barChart = (BarChart) view.findViewById(R.id.barchart);

//        initListViewData();
//        initBarChartDatas();                    //获取柱状图的数据
//        setQtyData(id);        //设置产能的数据
        handler.post(runnable);
        handler.post(runnable2);
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

    private void setReadOnly(EditText editText) {
        editText.setKeyListener(null);
    }

    private void initBarChartDatas() {
        String sql = "exec p_fm_work_get_board_rate ?";
        Parameters p = new Parameters().add(1, id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastInfo(getActivity(), value.Error + 2222222);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    setChartData(value.Value);
                } else {
                    setNullChart();
                }
                initListViewData();     //设置ListView的数据
            }
        });
    }

    private void setChartData(DataTable dataTable) {
        ArrayList<BarDataSet> threebardata = new ArrayList<BarDataSet>();//IBarDataSet 接口很关键，是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        ArrayList<String> xValue = new ArrayList<String>();
        ArrayList<BarEntry> yValue1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yValue2 = new ArrayList<BarEntry>();
        barChartColors = new ArrayList<Integer>();    //绘制柱状图的颜色

        for (int i = 0; i < dataTable.Rows.size(); i++) {
            String tagName = dataTable.Rows.get(i).getValue("tag_name", "");
            float rate2 = dataTable.Rows.get(i).getValue("rate2", new BigDecimal(0)).floatValue();
            if (rate2 >= 0.8) {
                barChartColors.add(getResources().getColor(R.color.megmeet_green));
            } else if (rate2 >= 0.6) {
                barChartColors.add(getResources().getColor(R.color.darkorange));
            } else {
                barChartColors.add(getResources().getColor(R.color.red));
            }
            xValue.add(tagName);
            yValue1.add(new BarEntry(rate2, i));
            yValue2.add(new BarEntry((float) 1, i));
        }
        BarDataSet barDataSet1 = new BarDataSet(yValue1, "UPH达成率");
//        barDataSet1.setColors(colorId);//设置第一组数据颜色
        String[] stack = {""};           //设置stack为没有
        barDataSet1.setStackLabels(stack);
        barDataSet1.setColors(barChartColors);
        barDataSet1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return Math.round(v * 100) + "%";
            }
        });
//        barDataSet1.setColor(getResources().getColor(R.color.megmeet_green));//设置第一组数据颜色

        BarDataSet barDataSet2 = new BarDataSet(yValue2, "标准值");
        barDataSet2.setColor(getResources().getColor(R.color.blue));//设置第一组数据颜色
        barDataSet2.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return "";
            }
        });

        threebardata.add(barDataSet1);
        threebardata.add(barDataSet2);
        BarData bardata = new BarData(xValue, threebardata);
//        bardata.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
//                return Math.round(v * 100) + "%";
//            }
//        });

        bardata.setValueTextSize(10);
        barChart.setData(bardata);      //设置图表数据

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);
//        ArrayList<Integer> colors = new ArrayList<Integer>();
//        colors.add(getResources().getColor(R.color.megmeet_green));
//        colors.add(getResources().getColor(R.color.black));
//        colors.add(getResources().getColor(R.color.red));

        int[] colors = {getResources().getColor(R.color.megmeet_green), getResources().getColor(R.color.blue)};
        String[] titles = {"UPH达成率", "标准值"};
        legend.setCustom(colors, titles);
        legend.setStackSpace(20);

        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(600);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);  //设置X轴的位置
        barChart.getXAxis().setDrawGridLines(false);    //不显示网格
//        barChart.getXAxis().setTextSize(getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin));
        barChart.getXAxis().setTextColor(getResources().getColor(R.color.blue));
        YAxis axisLeft = barChart.getAxisLeft();
        YAxis axisRight = barChart.getAxisRight();
        axisLeft.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                return String.valueOf(Math.round(v * 100));
            }
        });
        axisRight.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                return String.valueOf(Math.round(v * 100));
            }
        });
        barChart.setDescription("");
//        barChart.animateXY(1000, 2000);   //设置动画
        barChart.animateXY(0, 0);   //设置动画
    }

    private void setNullChart() {
        ArrayList<String> xValue = new ArrayList<String>();
        ArrayList<BarEntry> yValue1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yValue2 = new ArrayList<BarEntry>();
        yValue1.add(new BarEntry((float) 0, 0));
        yValue2.add(new BarEntry((float) 1, 0));
        xValue.add("8:00-9:00");

        BarDataSet barDataSet1 = new BarDataSet(yValue1, "UPH达成率");
//        barDataSet1.setColors(colorId);//设置第一组数据颜色
        barDataSet1.setColor(getResources().getColor(R.color.megmeet_green));//设置第一组数据颜色

        BarDataSet barDataSet2 = new BarDataSet(yValue2, "标准值");
        barDataSet2.setColor(getResources().getColor(R.color.blue));//设置第一组数据颜色

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

        bardata.setValueTextSize(20);
        barChart.setData(bardata);      //设置图表数据

//        Legend legend = barChart.getLegend();
//        legend.setTextColor(Color.RED);
//        legend.setTextSize(28);

        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(600);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);  //设置X轴的位置
        barChart.getXAxis().setDrawGridLines(false);    //不显示网格
//        barChart.getXAxis().setTextSize(getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin));
        YAxis axisLeft = barChart.getAxisLeft();
        YAxis axisRight = barChart.getAxisRight();
        axisLeft.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                return String.valueOf(Math.round(v * 100));
            }
        });
        axisRight.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                return String.valueOf(Math.round(v * 100));
            }
        });
        barChart.setDescription("");
//        barChart.animateXY(1000, 2000);   //设置动画
        barChart.animateXY(0, 0);   //设置动画
    }

    private void setQtyData(int work_line_id) {
        final String sql = "exec p_fm_work_get_board_view ?";
        Parameters p = new Parameters().add(1, work_line_id);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.toastInfo(getActivity(), value.Error + 3333333);
                    return;
                }
                if (value.Value != null) {
                    existing_number = value.Value.getValue("existing_number", 0);
                    sign_counts = value.Value.getValue("sign_counts", 0);
                    BigDecimal real_qty_for_h2 = new BigDecimal(value.Value.getValue("real_qty_for_h2", 0));
                    BigDecimal uph = value.Value.getValue("uph", new BigDecimal(0));
                    String real_qty_for_h = value.Value.getValue("real_qty_for_h", 0).toString();
                    String upph = value.Value.getValue("upph", new BigDecimal(0)).toString();
                    int real_qty = value.Value.getValue("real_qty", 0);
                    editText.setText(removeSmallNumber(upph));
                    if (TextUtils.isEmpty(editText_1.getText()) || editText_1.getText().toString().equals("0")) {
                        editText_1.setText(removeSmallNumber(value.Value.getValue("plan_quantity", new BigDecimal(0)).toString()));
                    }
                    editText_2.setText(removeSmallNumber(String.valueOf(real_qty)));
                    editText_3.setText(removeSmallNumber(String.valueOf(Math.round(uph.floatValue()))));

                    editText_4.setText(removeSmallNumber(String.valueOf(real_qty_for_h2)));
                    editText_5.setText(removeSmallNumber(real_qty_for_h));
                    editText_6.setText(removeSmallNumber(String.valueOf(Math.round(real_qty_for_h2.subtract(uph).floatValue()))));
                    editText_7.setText(removeSmallNumber(value.Value.getValue("fail_qty", 0).toString()));
                    float a_pass = (value.Value.getValue("a_pass", new BigDecimal(0)).floatValue() * 100);
                    editText_8.setText(String.valueOf(Math.round(a_pass)));

                    String task_order_code = value.Value.getValue("task_order_code", "");
                    String item_name = value.Value.getValue("item_name", "");

                    if (textViewCode != null) {
                        textViewCode.setText(task_order_code);
                    }
                    if (textViewItem != null) {
                        textViewItem.setText(item_name);
                    }

                    if (textviewMen != null) {
                        textviewMen.setText(String.valueOf(existing_number));
                    }

                    if (textviewSign != null) {
                        textviewSign.setText(String.valueOf(sign_counts));
                    }
                }
            }
        });
    }

    private void setLEDTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
        ledview_year.setLedView(50, "8888", year + "");
        ledview_month.setLedView(50, "88", month + "");
        ledview_day.setLedView(50, "88", day + "");
        ledview_hour.setLedView(50, "88", hour + "");
        ledview_min.setLedView(50, "88", min + "");
        ledview_seconds.setLedView(50, "88", second + "");
    }

    private void initTitleData() {
        String sql = "exec workline_kanban_ipqc_and ?";
        Parameters p = new Parameters().add(1, segment);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(getActivity(), value.Error);
                    return;
                }
                if (value.Value != null) {
                    titleContentString.add(value.Value.getValue("org_name", ""));
                    titleContentString.add(segment);
                    titleContentString.add(value.Value.getValue("responsible_man_name", ""));
                    titleContentString.add(value.Value.getValue("ipqc_name", ""));
                    titleContentString.add(value.Value.getValue("ie_name", ""));
                    titleContentString.add(value.Value.getValue("pqe_name", ""));
                    titleContentString.add(value.Value.getValue("te_name", ""));
                    titleContentString.add(value.Value.getValue("me_name", ""));
                    titleContentString.add(value.Value.getValue("pe_name", ""));
                    MyAdapter myAdapter = new MyAdapter(titleContentString);
                    gridview.setAdapter(myAdapter);
                }
            }
        });
    }

    private void initListViewData() {
        //获取数据
        String sql = "exec p_fm_work_get_broad_eb ?";
        Parameters p = new Parameters().add(1, id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    Log.e("len", "valueError" + value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    ArrayList<DataRow> dataRows1 = new ArrayList<DataRow>();     //每天批量1的数据集
                    ArrayList<DataRow> dataRows2 = new ArrayList<DataRow>();     //每天批量2的数据集
                    for (int i = 0; i <= value.Value.Rows.size() / 2; i++) {
                        dataRows1.add(value.Value.Rows.get(i));
                    }
                    for (int i = (value.Value.Rows.size() / 2) + 1; i < value.Value.Rows.size(); i++) {
                        dataRows2.add(value.Value.Rows.get(i));
                    }
//                    Collections.reverse(value.Value.Rows);
                    MyListAdapter listAdapter1 = new MyListAdapter(dataRows1);
                    listview1.setAdapter(listAdapter1);
                    MyListAdapter listAdapter2 = new MyListAdapter(dataRows2);
                    listview2.setAdapter(listAdapter2);

                    if (imageViewFresh != null) {
                        imageViewFresh.clearAnimation();
                    }
                } else {
                    Log.e("len", "listNULL");
                }
            }
        });
    }

    private void initListViewData2() {
        //获取数据
        String sql = "exec p_fm_work_get_broad_eb_2 ?";
        Parameters p = new Parameters().add(1, id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    Log.e("len", "valueError" + value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
//                    Collections.reverse(value.Value.Rows);
                    MyListAdapter listAdapter = new MyListAdapter(value.Value.Rows);
                    listview2.setAdapter(listAdapter);
                } else {
                    Log.e("len", "listNULL");
                }
            }
        });
    }

    private String removeSmallNumber(String plan_quantity) {
        if (plan_quantity.indexOf(".") > 0) {
            plan_quantity = plan_quantity.replaceAll("0+?$", "");//去掉多余的0
            plan_quantity = plan_quantity.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return plan_quantity;
    }

    @Override
    public void onSynthesizeStart(String s) {
        //监听到合成开始
        Log.e("len", "onSynthesizeStart;" + s);
    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
        //监听到有合成数据到达
        Log.e("len", "onSynthesizeDataArrived;" + s);
    }

    @Override
    public void onSynthesizeFinish(String s) {
        //监听到合成结束
        Log.e("len", "onSynthesizeFinish;" + s);
    }

    @Override
    public void onSpeechStart(String s) {
        //监听到合成并开始播放
        Log.e("len", "onSpeechStart;" + s);
    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {
        //监听到播放进度有变化
        Log.e("len", s + "SpeechProgressChanged;" + i);
    }

    @Override
    public void onSpeechFinish(String s) {
        //监听到播放结束
        SystemClock.sleep(2000);
        Log.e("len", "onSpeechFinish;" + s);
    }

    @Override
    public void onError(String s, SpeechError speechError) {
        //监听到出错
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
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = View.inflate(getActivity(), R.layout.production_griditem, null);
                viewHolder.textView_title = (TextView) view.findViewById(R.id.textview_title);
                viewHolder.textView_content = (TextView) view.findViewById(R.id.textview_content);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.textView_title.setText(titleString[i]);
            if (titleContentString.get(i) != null) {
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

    class MyListAdapter extends BaseAdapter {
        ArrayList<DataRow> dataTables;

        public MyListAdapter(ArrayList<DataRow> dataTables) {
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
                view = View.inflate(getActivity(), R.layout.item_production_kanban, null);
                listViewHolder = new ListViewHolder();
                listViewHolder.linearLayout = (android.widget.LinearLayout) view.findViewById(R.id.linearlayout);
                listViewHolder.textView_1 = (TextView) view.findViewById(R.id.textview_1);
                listViewHolder.textView_2 = (TextView) view.findViewById(R.id.textview_2);
                listViewHolder.textView_3 = (TextView) view.findViewById(R.id.textview_3);
                listViewHolder.textView_4 = (TextView) view.findViewById(R.id.textview_4);
                listViewHolder.textView_5 = (TextView) view.findViewById(R.id.textview_5);
                listViewHolder.textView_6 = (TextView) view.findViewById(R.id.textview_6);
                view.setTag(listViewHolder);
            } else {
                listViewHolder = (ListViewHolder) view.getTag();
            }
            String begin_time = dataTables.get(i).getValue("begin_time", "");
            String end_time = dataTables.get(i).getValue("end_time", "");
//            String[] split = begin_time.split(" ");
//            String[] split1 = end_time.split(" ");

            //投入数量
            float tr_qty = dataTables.get(i).getValue("tr_qty", new BigDecimal(0)).floatValue();
            //产出数量
            float bz_qty = dataTables.get(i).getValue("bz_qty", new BigDecimal(0)).floatValue();
            //目标
            float tr_uph = dataTables.get(i).getValue("tr_uph", new BigDecimal(0)).floatValue();
            float bz_uph = dataTables.get(i).getValue("bz_uph", new BigDecimal(0)).floatValue();
            String code = dataTables.get(i).getValue("code", "");
            String comment = dataTables.get(i).getValue("comment", "");
            listViewHolder.textView_1.setText(begin_time.substring(0, 5) + "-" + end_time.substring(0, 5));
            listViewHolder.textView_2.setText(removeSmallNumber(String.valueOf(Math.ceil(tr_qty))));
            listViewHolder.textView_3.setText(removeSmallNumber(String.valueOf(Math.ceil(bz_qty))));
            listViewHolder.textView_4.setText(removeSmallNumber(String.valueOf(Math.ceil(tr_uph))));
            listViewHolder.textView_5.setText(removeSmallNumber(String.valueOf(Math.ceil(bz_qty - bz_uph))));     // 包装shuliang
            listViewHolder.textView_6.setText(code);
            listViewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.skyblue));
            return view;
        }
    }

    class ListViewHolder {
        private android.widget.LinearLayout linearLayout;
        private TextView textView_1;
        private TextView textView_2;
        private TextView textView_3;
        private TextView textView_4;
        private TextView textView_5;
        private TextView textView_6;
    }
}
