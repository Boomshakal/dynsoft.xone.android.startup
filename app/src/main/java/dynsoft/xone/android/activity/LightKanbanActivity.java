package dynsoft.xone.android.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.util.BaiduTTSUtil;

public class LightKanbanActivity extends Activity implements SpeechSynthesizerListener {
    private String[] titleString = {"����", "�Ϻ�", "�߱�/��λ", "����ʱ��", "��Ӧʱ��", "��Ӧ��ʱ", "������", "������", "״̬", "Ӱ������", "��ʱ��", "��ʧ����"};
    private static final int LISTVIEWDELAYTIME1 = 20 * 60 * 1000;
    private static final int SCROLLTIME = 30 * 1000;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView Title;
    private ListView listView1;

    private TextView itemTextView1;
    private TextView itemTextView2;
    private TextView itemTextView3;
    private TextView itemTextView4;
    private TextView itemTextView5;
    private TextView itemTextView6;
    private TextView itemTextView7;
    private TextView itemTextView8;
    private TextView itemTextView9;
    private TextView itemTextView10;
    private TextView itemTextView11;
    private TextView itemTextViewShort;  //ȱ��ԭ��
    private TextView itemTextViewAsk;    //������
    private BaiduTTSUtil baiduTTSUtil;

    private ArrayList<String> askSize;
    private ArrayList<String> respondSize;

    private int widthPix;
    private int heightPix;
    private ArrayList<String> values;

    private Handler handler;
    private Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            scrollListView();
            handler.postDelayed(runnable1, SCROLLTIME);
        }
    };

    private Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            initData();
            initLightView();          //��ʼ�������쳣
            handler.postDelayed(runnable2, LISTVIEWDELAYTIME1);
        }
    };
    private String org_name;
    private boolean respond;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_kanban);
        Intent intent = getIntent();
        values = (ArrayList<String>) intent.getSerializableExtra("value");
        org_name = intent.getStringExtra("org_name");
        respond = intent.getBooleanExtra("respond", false);
        Title = (TextView) findViewById(R.id.title);
        textView1 = (TextView) findViewById(R.id.textview1);
        textView2 = (TextView) findViewById(R.id.textview2);
        textView3 = (TextView) findViewById(R.id.textview3);
        listView1 = (ListView) findViewById(R.id.listview_1);

        itemTextView1 = (TextView) findViewById(R.id.textview_1);
        itemTextView2 = (TextView) findViewById(R.id.textview_2);
        itemTextView3 = (TextView) findViewById(R.id.textview_3);
        itemTextView4 = (TextView) findViewById(R.id.textview_4);
        itemTextView5 = (TextView) findViewById(R.id.textview_5);
        itemTextView6 = (TextView) findViewById(R.id.textview_6);
        itemTextView7 = (TextView) findViewById(R.id.textview_7);
        itemTextView8 = (TextView) findViewById(R.id.textview_8);
        itemTextView9 = (TextView) findViewById(R.id.textview_9);
        itemTextView10 = (TextView) findViewById(R.id.textview_10);
        itemTextView11 = (TextView) findViewById(R.id.textview_11);
        itemTextViewShort = (TextView) findViewById(R.id.textview_short);
        itemTextViewAsk = (TextView) findViewById(R.id.textview_ask);

//        if("ȱ��".equals(value)) {
//            itemTextViewShort.setText("ȱ���Ϻ�");
//        } else if("�����쳣".equals(value)) {
//            itemTextViewShort.setText("�쳣����");
//        } else {
//            itemTextViewShort.setText("�쳣˵��");
//        }
        itemTextViewShort.setText("�쳣˵��");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthPix = displayMetrics.widthPixels;
        heightPix = displayMetrics.heightPixels;

        speechSynthesizer = SpeechSynthesizer.getInstance();
        baiduTTSUtil = new BaiduTTSUtil(speechSynthesizer, this);
        baiduTTSUtil.initSpeech();
        handler = new Handler();
        initTitle();

        if (Title != null) {
//            Title.setText(value + "���ƿ���");
            Title.setText("���ƿ���");
        }

//        initData();
//        initLightView();
        handler.post(runnable2);
        handler.post(runnable1);
    }

    private SpeechSynthesizer speechSynthesizer;

    private void scrollListView() {
        int childCount = listView1.getChildCount();
        int count = listView1.getCount();
        int lastVisiblePosition = listView1.getLastVisiblePosition();
        Log.e("len", lastVisiblePosition + "LAST:" + childCount + "COUMT:" + count);
        if (listView1.getLastVisiblePosition() >= count - 1) {
            Log.e("len", "SCROLL");
            listView1.smoothScrollToPosition(0);
        } else {
//            listView.smoothScrollToPosition(lastVisiblePosition + childCount - 1);
            listView1.smoothScrollByOffset(childCount - 1);
        }
    }

    private void initLightView() {        //��ȡ��ǰ�Ƿ���δ������쳣����pn_qm_raw_material_mgr
        String xml = createXML(values);
        Log.e("len", xml);
        String sql = " exec get_fm_kanban_light_notice_form_exception_class_and_v01 ?,?";
        Parameters p = new Parameters().add(1, xml).add(2, org_name);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataTable> value = Value;
                if (value.HasError) {
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    new Thread() {
                        @Override
                        public void run() {
                            int i = 0;
                            while (i < value.Value.Rows.size()) {
                                DataRow dataRow = value.Value.Rows.get(i);
                                String dev_name = dataRow.getValue("dev_name", "");
                                String exception_type = dataRow.getValue("exception_type", "");
                                String rep_user = dataRow.getValue("rep_user", "");
                                String segment = dataRow.getValue("work_line", "");
                                speechSynthesizer.speak(segment + dev_name + "��" + exception_type + "���С�" + "��" + rep_user + "���ٴ���");
                                speechSynthesizer.setSpeechSynthesizerListener(LightKanbanActivity.this);
                                i++;
                            }
                        }
                    }.start();
                } else {
                    Log.e("len", "NULL");
                }
            }
        });
    }

    private String createXML(ArrayList<String> values) {
        ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
        for (int i = 0; i < values.size(); i++) {
            Map<String, String> entry = new HashMap<String, String>();
            entry.put("exception", values.get(i));
            entries.add(entry);
        }

        //����XML���ݣ��������洢����
        String xml = XmlHelper.createXml("exception_head", null, null, "exception_item", entries);
        return xml;
    }

    @Override
    public void onSynthesizeStart(String s) {
        //�������ϳɿ�ʼ
    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
        //�������кϳ����ݵ���
    }

    @Override
    public void onSynthesizeFinish(String s) {
        //�������ϳɽ���
    }

    @Override
    public void onSpeechStart(String s) {
        //�������ϳɲ���ʼ����
    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {
        //���������Ž����б仯
    }

    @Override
    public void onSpeechFinish(String s) {
        //���������Ž���
        SystemClock.sleep(2000);
    }

    @Override
    public void onError(String s, SpeechError speechError) {
        //����������
    }

    private void initTitle() {
        if (textView3 != null) {
            textView3.setText("���ڣ�" + new SimpleDateFormat("yyyy��MM��dd��").format(new Date()));
        }
        if (itemTextView1 != null) {
            itemTextView1.setText(titleString[0]);
        }
        if (itemTextView2 != null) {
            itemTextView2.setText(titleString[1]);
        }
        if (itemTextView3 != null) {
            itemTextView3.setText(titleString[2]);
        }
        if (itemTextView4 != null) {
            itemTextView4.setText(titleString[3]);
        }
        if (itemTextView5 != null) {
            itemTextView5.setText(titleString[4]);
        }
        if (itemTextView6 != null) {
            itemTextView6.setText(titleString[5]);
        }
        if (itemTextViewAsk != null) {
            itemTextViewAsk.setText(titleString[6]);
        }
        if (itemTextView7 != null) {
            itemTextView7.setText(titleString[7]);
        }
        if (itemTextView8 != null) {
            itemTextView8.setText(titleString[8]);
        }
        if (itemTextView9 != null) {
            itemTextView9.setText(titleString[9]);
        }
        if (itemTextView10 != null) {
            itemTextView10.setText(titleString[10]);
        }
        if (itemTextView11 != null) {
            itemTextView11.setText(titleString[11]);
        }
    }

    private void initData() {                   //��ȡ���е��쳣����
        final String sql = "exec p_fm_get_xml_light_exception_and ?,?,?";
        String xml = createXML(values);
        Log.e("len", xml);
        Parameters p = new Parameters().add(1, xml).add(2, org_name).add(3, respond);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(LightKanbanActivity.this, value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    askSize = new ArrayList<String>();
                    respondSize = new ArrayList<String>();
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        String status = value.Value.Rows.get(i).getValue("status", "");
                        if ("����".equals(status)) {
                            askSize.add(status);
                        } else if ("�Ѵ���".equals(status)) {
                            respondSize.add(status);
                        }
                    }
                    if (textView1 != null) {
                        textView1.setText("�Ѵ���" + respondSize.size() + "��");
                    }
                    if (textView2 != null) {
                        textView2.setText("δ����" + askSize.size() + "��");
                    }
                    Log.e("len", "�Ѵ���" + respondSize.size() + "δ����" + askSize.size());
                    LightKanbanAdapter lightKanbanAdapter = new LightKanbanAdapter(value.Value);
                    listView1.setAdapter(lightKanbanAdapter);
                }
            }
        });
    }

    public float getTimeDifference(Date starTime, Date endTime) {   //��ȡʱ����
        long diff = endTime.getTime() - starTime.getTime();

        long day = diff / (24 * 60 * 60 * 1000);
        long hour = (diff / (60 * 60 * 1000) - day * 24);
        long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long ms = (diff - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
                - min * 60 * 1000 - s * 1000);

        long min1 = diff / (60 * 1000);

        long needHour = (diff / (60 * 60 * 1000));
        long needMin = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        float diffHour = (float) (Math.round((needMin / 60.0) * 100)) / 100;
        return (needHour + diffHour);
    }


    public String getTimeDifferenceString(Date starTime, Date endTime) {   //��ȡʱ����
        long diff = endTime.getTime() - starTime.getTime();

        long day = diff / (24 * 60 * 60 * 1000);
        long hour = (diff / (60 * 60 * 1000) - day * 24);
        long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long ms = (diff - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
                - min * 60 * 1000 - s * 1000);


        long needHour = (diff / (60 * 60 * 1000));
        long needMin = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);

        float diffHour = (float) (Math.round((needMin / 60.0) * 100)) / 100;

        StringBuffer stringBuffer = new StringBuffer();
        if (diff > 0) {
            stringBuffer.append((needHour + diffHour) + "ʱ");
        } else {
            stringBuffer.append(needHour + "ʱ");
        }

        return stringBuffer.toString();
    }

    class LightKanbanAdapter extends BaseAdapter {
        private DataTable dataTable;

        public LightKanbanAdapter(DataTable dataTable) {
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
            LightKanbanViewHolder lightKanbanViewHolder;
            if (view == null) {
                view = View.inflate(LightKanbanActivity.this, R.layout.item_light_kanban, null);
                lightKanbanViewHolder = new LightKanbanViewHolder();
                lightKanbanViewHolder.itemTextView1 = (TextView) view.findViewById(R.id.textview_1);
                lightKanbanViewHolder.itemTextView2 = (TextView) view.findViewById(R.id.textview_2);
                lightKanbanViewHolder.itemTextView3 = (TextView) view.findViewById(R.id.textview_3);
                lightKanbanViewHolder.itemTextView4 = (TextView) view.findViewById(R.id.textview_4);
                lightKanbanViewHolder.itemTextView5 = (TextView) view.findViewById(R.id.textview_5);
                lightKanbanViewHolder.itemTextView6 = (TextView) view.findViewById(R.id.textview_6);
                lightKanbanViewHolder.itemTextView7 = (TextView) view.findViewById(R.id.textview_7);
                lightKanbanViewHolder.itemTextView8 = (TextView) view.findViewById(R.id.textview_8);
                lightKanbanViewHolder.itemTextView9 = (TextView) view.findViewById(R.id.textview_9);
                lightKanbanViewHolder.itemTextView10 = (TextView) view.findViewById(R.id.textview_10);
                lightKanbanViewHolder.itemTextView11 = (TextView) view.findViewById(R.id.textview_11);
                lightKanbanViewHolder.itemTextViewShort = (TextView) view.findViewById(R.id.textview_short);
                lightKanbanViewHolder.itemTextViewAsk = (TextView) view.findViewById(R.id.textview_ask);
                lightKanbanViewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.light_linearlayout);
                view.setTag(lightKanbanViewHolder);
            } else {
                lightKanbanViewHolder = (LightKanbanViewHolder) view.getTag();
            }
            String task_order_code = dataTable.Rows.get(i).getValue("task_order_code", "");
            String item_code = dataTable.Rows.get(i).getValue("item_code", "");
            String work_line = dataTable.Rows.get(i).getValue("work_line", "");
            String station = dataTable.Rows.get(i).getValue("station", "");
            String ask_name = dataTable.Rows.get(i).getValue("ask_name", "");
            String respond_name = dataTable.Rows.get(i).getValue("respond_name", "");
            String status = dataTable.Rows.get(i).getValue("status", "");
            String exceptionComment = dataTable.Rows.get(i).getValue("exception_comment", "");
            Date create_time = dataTable.Rows.get(i).getValue("create_time", new Date());
            Date respond_time = dataTable.Rows.get(i).getValue("respond_time", new Date());
            int influences_counts = dataTable.Rows.get(i).getValue("influences_counts", 0);
            String timeDifferenceString;
            float timeDifference;
            if (status.contains("�Ѵ���")) {
                timeDifferenceString = getTimeDifferenceString(create_time, respond_time);
                timeDifference = getTimeDifference(create_time, respond_time);
            } else {
                timeDifferenceString = getTimeDifferenceString(create_time, new Date());
                timeDifference = getTimeDifference(create_time, new Date());
            }
            lightKanbanViewHolder.itemTextView1.setText(task_order_code);
            lightKanbanViewHolder.itemTextView2.setText(item_code);
            String workLineStation = work_line + "/" + station;
            lightKanbanViewHolder.itemTextView3.setText(workLineStation.trim());
            lightKanbanViewHolder.itemTextView4.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(create_time));
            lightKanbanViewHolder.itemTextView9.setText(String.valueOf(influences_counts));
            if (status.contains("�Ѵ���")) {
                lightKanbanViewHolder.itemTextView5.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(respond_time));
                lightKanbanViewHolder.itemTextView6.setText(timeDifferenceString);
//                if (timeDifference < 30) {       //��������Сʱ
//                    lightKanbanViewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.turquoise));
//                } else {
//                    lightKanbanViewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.orchid));
//                }

                lightKanbanViewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.darkgrey));
                lightKanbanViewHolder.itemTextView8.setText(status);
                float timeCounts = (float) (Math.round((timeDifference * influences_counts) * 100) / 100);
                float money = (timeCounts * 50);
                lightKanbanViewHolder.itemTextView10.setText(String.valueOf(timeCounts));
                lightKanbanViewHolder.itemTextView11.setText(String.valueOf(money));
            } else {
                lightKanbanViewHolder.itemTextView5.setText("");
                lightKanbanViewHolder.itemTextView6.setText("");
//                if (timeDifference < 30) {        //δ��������Сʱ
//                    lightKanbanViewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.gold));
//                } else {
//                    lightKanbanViewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.red));
//                }

                lightKanbanViewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.red));

                lightKanbanViewHolder.itemTextView8.setText("δ����");
            }
            lightKanbanViewHolder.itemTextView7.setText(respond_name);
            lightKanbanViewHolder.itemTextViewShort.setText(exceptionComment);
            lightKanbanViewHolder.itemTextViewAsk.setText(ask_name);
            return view;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (handler != null) {
            handler.removeCallbacks(runnable2);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacks(runnable2);
        }
        speechSynthesizer.stop();
        speechSynthesizer.release();
    }

    class LightKanbanViewHolder {
        private TextView itemTextView1;
        private TextView itemTextView2;
        private TextView itemTextView3;
        private TextView itemTextView4;
        private TextView itemTextView5;
        private TextView itemTextView6;
        private TextView itemTextView7;
        private TextView itemTextView8;
        private TextView itemTextView9;
        private TextView itemTextView10;
        private TextView itemTextView11;
        private TextView itemTextViewShort;
        private TextView itemTextViewAsk;
        private LinearLayout linearLayout;
    }
}
