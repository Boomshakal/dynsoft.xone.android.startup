package dynsoft.xone.android.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.LoginFilter;
import android.util.Log;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
 * Created by Administrator on 2018/1/26.
 */

public class IKAHEKanbanActivity extends Activity implements SpeechSynthesizerListener {
    private static final int LISTVIEWDELAYTIME = 10 * 60 * 1000;    //ListView�ͱ�״ͼˢ�µ�ʱ��
    private String[] titleString = {"�߱�:", "�����鳤:", "���:", "IPQC:", "IE:", "PQE:", "TE:",
            "ME:", "RE:"};               //GridView�����ݼ�
    private GridView gridview;

    private ListView listview;            //��ʾÿ������
    private PieChart pieChart;            //��״ͼ
    private TextView textViewFresh;      //ˢ��
    private ImageView imageViewFresh;      //ˢ��
    private TextView titleTextview;

    private TextView textViewDate;

    private TextView textview;
    private BarChart barChart3;

    private LinearLayout linearLayout;       //�����쳣
    private ImageView imageView;             //�а����쳣
    private TextView textView;               //�а����쳣
    private SpeechSynthesizer mSpeechSynthesizer;
    private BaiduTTSUtil baiduTTSUtil;
    private StringBuilder stringBuilder;  //�ַ���ƴ��
    private String curDate;                //��ʾ��ǰ����

    private int id;
    private ArrayList<Integer> parChartColors;
    private String segment;
    private ArrayList<String> titleContentString;
    private ArrayList<Integer> barChartColors;

    private Handler handler;
    private Runnable runnable3 = new Runnable() {
        @Override
        public void run() {
            initBarChartData();     //������״ͼ����
            initListViewData();     //����ListView������
            initLightView();        //�쳣����
            if (textViewFresh != null) {
                textViewFresh.setText("�ϴθ���ʱ��:" + new SimpleDateFormat("HH:mm:ss").format(new Date()));
            }
            if (textViewDate != null) {
                textViewDate.setText(curDate);
            }
            handler.postDelayed(runnable3, LISTVIEWDELAYTIME);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ikahekanban);
        Intent intent = getIntent();
        id = intent.getIntExtra("work_line_id", 0);
        segment = intent.getStringExtra("work_line");
        handler = new Handler();
        gridview = (GridView) findViewById(R.id.gridview);
        textViewFresh = (TextView) findViewById(R.id.textview_fresh);
        textViewDate = (TextView) findViewById(R.id.textview_date);
        titleTextview = (TextView) findViewById(R.id.textview_changetime);
        imageViewFresh = (ImageView) findViewById(R.id.imageview_fresh);
        titleContentString = new ArrayList<String>();

        linearLayout = (LinearLayout) findViewById(R.id.light_linearlayout);
        imageView = (ImageView) findViewById(R.id.image_view);
        textView = (TextView) findViewById(R.id.text_view);
        textView.setSelected(true);
        linearLayout.setVisibility(View.GONE);
        imageView.setImageResource(R.drawable.light_selected);
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        baiduTTSUtil = new BaiduTTSUtil(mSpeechSynthesizer, this);
        baiduTTSUtil.initSpeech();

        textview = (TextView) findViewById(R.id.textview);
        barChart3 = (BarChart) findViewById(R.id.barchart_3);

        initTitleData();           //��ȡ���������
        initView();  //��ʼ��textview��LEDʱ�䡢ͼ�����
    }

    private void initView() {
        listview = (ListView) findViewById(R.id.listview);
        pieChart = (PieChart) findViewById(R.id.piechart);
        handler.post(runnable3);               //listview������

        if (imageViewFresh != null) {
            imageViewFresh.setImageDrawable(getResources().getDrawable(R.drawable.fresh_blue));
            imageViewFresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAnimation(imageViewFresh);
                    handler.post(runnable3);
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
//        animation.setFillAfter(true);//����Ϊtrue������ת��������Ӧ��
        imageViewFresh.startAnimation(animation);//��ʼ����
    }

    private void initTitleData() {
        String sql = "exec workline_kanban_ipqc_and ?";
        Parameters p = new Parameters().add(1, segment);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(IKAHEKanbanActivity.this, value.Error);
                    return;
                }
                if (value.Value != null) {
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

    private void initLightView() {        //��ȡ��ǰ���Ƿ���δ������쳣����
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
                                mSpeechSynthesizer.speak(segment + dev_name + "��" + exception_type + "���С�" + "��" + rep_user + "�ٵ��ֳ�");
                                mSpeechSynthesizer.setSpeechSynthesizerListener(IKAHEKanbanActivity.this);
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
                            stringBuilder.append(segment + dev_name + "��" + exception_type + "���С�" + "��" + rep_user + "�ٵ��ֳ���");
                        } else {
                            stringBuilder.append(segment + dev_name + "��" + exception_type + "����," + "��" + rep_user + "�ٵ��ֳ���");
                        }
                    }
                    textView.setText(stringBuilder.toString());
                    textView.setTextColor(getResources().getColor(R.color.red));
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
        //��ȡ����
        String sql = "exec p_fm_get_work_board_and ?";
        Parameters p = new Parameters().add(1, segment);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastInfo(IKAHEKanbanActivity.this, segment + value.Error + "11111111");
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    int planQty = 0;                    //�ƻ�����
                    int actualQty = 0;                    //ʵ�ʲ���
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
//        final ArrayList<DataRow> hiData = new ArrayList<DataRow>();
//        final ArrayList<DataRow> asData = new ArrayList<DataRow>();
//        final ArrayList<DataRow> pkData = new ArrayList<DataRow>();
        final ArrayList<DataRow> loadData = new ArrayList<DataRow>();
        String sql = "exec p_fm_get_ikahe_kanban_piechart_and ?";
        Parameters p = new Parameters().add(1, segment);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastInfo(IKAHEKanbanActivity.this, segment + value.Error + "22222222");
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    String textViewStage = null;
                    curDate = value.Value.Rows.get(0).getValue("cur_date", "");
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        String work_stage_code = value.Value.Rows.get(i).getValue("work_stage_code", "");
                        if (segment.contains("��װ")) {
                            if (work_stage_code.toUpperCase().equals("AS")) {
                                DataRow dataRow = value.Value.Rows.get(i);
                                loadData.add(dataRow);
                                textViewStage = work_stage_code;
                            }
                        } else if (segment.contains("��װ")) {
                            if (work_stage_code.toUpperCase().equals("PK")) {
                                DataRow dataRow = value.Value.Rows.get(i);
                                loadData.add(dataRow);
                                textViewStage = work_stage_code;
                            }
                        }
//                        if (work_stage_code.toUpperCase().equals("HI")) {
//                            DataRow dataRow = value.Value.Rows.get(i);
//                            hiData.add(dataRow);
//                        }
//
//                        if (work_stage_code.toUpperCase().equals("AS")) {
//                            DataRow dataRow = value.Value.Rows.get(i);
//                            asData.add(dataRow);
//                        }
//                        if (work_stage_code.toUpperCase().equals("PK")) {
//                            DataRow dataRow = value.Value.Rows.get(i);
//                            pkData.add(dataRow);
//                        }
                    }
//                    addBarChart(asData, barChart2, false, "AS");
                    addBarChart(loadData, barChart3, true, textViewStage);
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
                barChartColors.add(getResources().getColor(R.color.lime));
                yValue2.add(new BarEntry((float) item_upph, i));
            } else {
                float qtyPer = (float) qty / item_upph;
                yValue1.add(new BarEntry(qtyPer, i));
                yValue2.add(new BarEntry((float) 1, i));
                if (qtyPer >= 0.8) {
                    barChartColors.add(getResources().getColor(R.color.lime));
                } else if (qtyPer >= 0.6) {
                    barChartColors.add(getResources().getColor(R.color.darkorange));
                } else {
                    barChartColors.add(getResources().getColor(R.color.red));
                }
            }
        }

        BarDataSet barDataSet1 = new BarDataSet(yValue1, "UPH�����");
//        barDataSet1.setColors(colorId);//���õ�һ��������ɫ
        barDataSet1.setColors(barChartColors);//���õ�һ��������ɫ

        BarDataSet barDataSet2 = new BarDataSet(yValue2, "��׼ֵ");
        barDataSet2.setColor(getResources().getColor(R.color.dodgerblue));//���õ�һ��������ɫ

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
        barChart.setData(bardata);      //����ͼ������

        Legend legend = barChart.getLegend();
        legend.setEnabled(legShow);
        legend.setTextSize(getResources().getDimension(R.dimen.ikahe_qty_text_size));
        legend.setXEntrySpace(80);
        int[] colors = {getResources().getColor(R.color.lime), getResources().getColor(R.color.blue)};
        String[] titles = {"UPH�����", "��׼ֵ"};
        legend.setCustom(colors, titles);
        legend.setStackSpace(20);
//        String titles[] = {"UPH�����", "��׼ֵ"};
//        int color[] = {getResources().getColor(R.color.darkorange), getResources().getColor(R.color.dodgerblue)};
//        legend.setExtra(color, titles);
//        legend.setStackSpace(100);
//        Legend legend = barChart.getLegend();
//        legend.setEnabled(legShow);
//        ArrayList<Integer> colors = new ArrayList<Integer>();
//        colors.add(getResources().getColor(R.color.megmeet_green));
//        colors.add(getResources().getColor(R.color.black));
//        colors.add(getResources().getColor(R.color.red));

        barChart.setDescription("");
        barChart.setDescriptionTextSize(getResources().getDimension(R.dimen.activity_horizontal_margin));
        barChart.setDescriptionColor(getResources().getColor(R.color.red));

        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(600);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);  //����X���λ��
        barChart.getXAxis().setDrawGridLines(false);    //����ʾ����
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
        barChart.animateXY(0, 0);   //���ö���
    }

    private void setPieChart(final int planQty, final int actualQty) {
        parChartColors = new ArrayList<Integer>();
        ArrayList<String> xValue = new ArrayList<String>();
        ArrayList<Entry> yValue1 = new ArrayList<Entry>();

        xValue.add("ʵ�ʲ���");
        xValue.add("Ŀ�����");

        PieDataSet pieDataSet = null;
        if (planQty <= 0 && actualQty <= 0) {
            yValue1.add(new Entry(1, 0));
            yValue1.add(new Entry(1, 1));

            parChartColors.add(getResources().getColor(R.color.lime));
            parChartColors.add(getResources().getColor(R.color.dodgerblue));

            pieDataSet = new PieDataSet(yValue1, "");
            pieDataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                    return "";
                }
            });
        } else {
            if (actualQty < planQty && planQty != 0) {
                float last = (float) (planQty - actualQty) / planQty;      //ʣ�µı���
                final float actual = (float) actualQty / planQty;    //ʵ��ռ�ƻ��ı���
                yValue1.add(new Entry(actual, 0));
                yValue1.add(new Entry(last, 1));


                if (actual >= 1.05) {
                    parChartColors.add(getResources().getColor(R.color.darkorange));
                } else if (actual >= 0.8) {
                    parChartColors.add(getResources().getColor(R.color.lime));
                } else if (actual >= 0.6) {
                    parChartColors.add(getResources().getColor(R.color.darkorange));
                } else {
                    parChartColors.add(getResources().getColor(R.color.red));
                }
                parChartColors.add(getResources().getColor(R.color.dodgerblue));

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

                parChartColors.add(getResources().getColor(R.color.lime));
                parChartColors.add(getResources().getColor(R.color.dodgerblue));

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
        legend.setTextSize(getResources().getDimension(R.dimen.ikahe_qty_text_size));

        pieChart.setData(pieData);
        pieChart.setActivated(true);
        pieChart.setDescription("");
        pieChart.invalidate();
//        parChartColors.removeAll(parChartColors);
    }

    private String removeSmallNumber(String plan_quantity) {
        if (plan_quantity.indexOf(".") > 0) {
            plan_quantity = plan_quantity.replaceAll("0+?$", "");//ȥ�������0
            plan_quantity = plan_quantity.replaceAll("[.]$", "");//�����һλ��.��ȥ��
        }
        return plan_quantity;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (handler == null) {
            handler = new Handler();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacks(runnable3);
            handler = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacks(runnable3);
        }
        mSpeechSynthesizer.stop();
        mSpeechSynthesizer.release();
    }

    @Override
    public void onSynthesizeStart(String s) {
        //�������ϳɿ�ʼ
        Log.e("len", "onSynthesizeStart;" + s);
    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
        //�������кϳ����ݵ���
        Log.e("len", "onSynthesizeDataArrived;" + s);
    }

    @Override
    public void onSynthesizeFinish(String s) {
        //�������ϳɽ���
        Log.e("len", "onSynthesizeFinish;" + s);
    }

    @Override
    public void onSpeechStart(String s) {
        //�������ϳɲ���ʼ����
        Log.e("len", "onSpeechStart;" + s);
    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {
        //���������Ž����б仯
        Log.e("len", s + "SpeechProgressChanged;" + i);
    }

    @Override
    public void onSpeechFinish(String s) {
        //���������Ž���
        SystemClock.sleep(2000);
        Log.e("len", "onSpeechFinish;" + s);
    }

    @Override
    public void onError(String s, SpeechError speechError) {
        //����������
    }

    public double SplitAndRound(double a, int n) {
        a = a * Math.pow(10, n);
        return (Math.round(a)) / (Math.pow(10, n));
    }

    class MyAdapter extends BaseAdapter {                //gridview��������
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
                view = View.inflate(IKAHEKanbanActivity.this, R.layout.production_griditem, null);
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

        class ViewHolder {                          //GridView��viewholder
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
                view = View.inflate(IKAHEKanbanActivity.this, R.layout.item_kanban, null);
                listViewHolder = new ListViewHolder();
                listViewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout);
                listViewHolder.textView_1 = (TextView) view.findViewById(R.id.textview_1);
                listViewHolder.textView_2 = (TextView) view.findViewById(R.id.textview_2);
                listViewHolder.textView_3 = (TextView) view.findViewById(R.id.textview_3);
//                listViewHolder.textView_4 = (TextView) view.findViewById(R.id.textview_4);
                listViewHolder.textView_5 = (TextView) view.findViewById(R.id.textview_5);
                listViewHolder.textView_6 = (TextView) view.findViewById(R.id.textview_6);
                listViewHolder.textView_7 = (TextView) view.findViewById(R.id.textview_7);
                listViewHolder.textView_8 = (TextView) view.findViewById(R.id.textview_8);
                listViewHolder.textView_9 = (TextView) view.findViewById(R.id.textview_9);
                listViewHolder.textView_10 = (TextView) view.findViewById(R.id.textview_10);
                listViewHolder.textView_11 = (TextView) view.findViewById(R.id.textview_11);
                listViewHolder.textView_12 = (TextView) view.findViewById(R.id.textview_12);
//                listViewHolder.textView_13 = (TextView) view.findViewById(R.id.textview_13);
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
            int fail_count = dataTables.get(i).getValue("fail_count", 0);  //������
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
            listViewHolder.textView_7.setText(String.valueOf(item_upph));   //�ƻ�����
            listViewHolder.textView_8.setText(String.valueOf(qty));         //ʵ�ʲ���
            listViewHolder.textView_9.setText(removeSmallNumber(String.valueOf(item_ct)));                      //����
            listViewHolder.textView_10.setText(String.valueOf(fail_count));
            listViewHolder.textView_10.setTextColor(getResources().getColor(R.color.red));
            if (qty > 0) {
                String failPercent = String.valueOf(((float) fail_count / (float) qty) * 100);   //�ٷֱ�
                if (failPercent.length() > 4) {
                    listViewHolder.textView_11.setText(failPercent.substring(0, 4) + "%");
                } else {
                    listViewHolder.textView_11.setText(failPercent + "%");
                }
            }

            listViewHolder.textView_11.setTextColor(getResources().getColor(R.color.red));
//            listViewHolder.textView_6.setText(String.valueOf(average_qty));
//            listViewHolder.textView_7.setText(dataTables.get(i).getValue("quantity", 0).toString());
//            listViewHolder.textView_8.setText(String.valueOf(Math.round(dataTables.get(i).getValue("Ͷ�����", new BigDecimal(0)).floatValue())));
//            listViewHolder.textView_9.setText(dataTables.get(i).getValue("sequence_name", ""));
//            listViewHolder.textView_10.setText(dataTables.get(i).getValue("quantity_cc", 0).toString());
//            listViewHolder.textView_11.setText(String.valueOf(Math.round(dataTables.get(i).getValue("average_qty_cc", new BigDecimal(0)).floatValue())));
            listViewHolder.textView_12.setText(String.valueOf(qty - item_upph));
//            listViewHolder.textView_13.setText(dataTables.get(i).getValue("sequence_name_cc", ""));

            if (id > 100) {
                listViewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.dodgerblue));
            } else {
                listViewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.skyblue));
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
