package dynsoft.xone.android.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.util.BaiduTTSUtil;
import oracle.jdbc.driver.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;

/**
 * Created by Administrator on 2018/6/11.
 */

public class ShortReportKanbanActivity extends Activity implements SpeechSynthesizerListener {
    private static int FRESHTIME = 30 * 60 * 1000;
    private static int FRESHLISTTIME = 30 * 1000;

    private String planName;
    private String startTime;
    private String sumShort;

    //是否有缺料安灯呼叫
    private LinearLayout linearLayout;
    private ImageView imageView;
    private TextView textView;

    private SpeechSynthesizer mSpeechSynthesizer;
    private BaiduTTSUtil baiduTTSUtil;
    private StringBuilder stringBuilder;

    private ListView listView;
    private ProgressBar progressBar;
    private TextView textView7;
    private TextView textView8;
    private TextView textView9;
    private TextView textView10;
    private TextView textView11;

    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
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
    private PurchaseAdapter shortAdapter;

    private void scrollListView() {
        int childCount = listView.getChildCount();
        int count = listView.getCount();
        int lastVisiblePosition = listView.getLastVisiblePosition();
        Log.e("len", lastVisiblePosition + "LAST:" + childCount + "COUMT:" + count);
        if (listView.getLastVisiblePosition() >= count - 1) {
            Log.e("len", "SCROLL");
            listView.smoothScrollToPosition(0);
        } else {
//            listView.smoothScrollToPosition(lastVisiblePosition + childCount - 1);
            listView.smoothScrollByOffset(childCount - 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortreport_kanban);
        Intent intent = getIntent();
        planName = intent.getStringExtra("planName");
        startTime = intent.getStringExtra("startTime");
        sumShort = intent.getStringExtra("sumshort");

        Log.e("len", "PLANNAME:" + planName);

        textView7 = (TextView) findViewById(R.id.textview_7);
        textView8 = (TextView) findViewById(R.id.textview_8);
        textView9 = (TextView) findViewById(R.id.textview_9);
        textView10 = (TextView) findViewById(R.id.textview_10);
        textView11 = (TextView) findViewById(R.id.textview_11);

        listView = (ListView) findViewById(R.id.listview);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        linearLayout = (LinearLayout) findViewById(R.id.light_linearlayout);
        imageView = (ImageView) findViewById(R.id.image_view);
        textView = (TextView) findViewById(R.id.text_view);
        textView.setSelected(true);
        linearLayout.setVisibility(View.GONE);
        imageView.setImageResource(R.drawable.light_selected);
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        baiduTTSUtil = new BaiduTTSUtil(mSpeechSynthesizer, this);
        baiduTTSUtil.initSpeech();

        initDateTitle();
        if (handler == null) {
            handler = new Handler();
        }
        handler.post(runnable);
        handler.post(runnable2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler.removeCallbacks(runnable2);
            handler = null;
        }
        mSpeechSynthesizer.stop();
        mSpeechSynthesizer.release();
    }

    private void initDateTitle() {
        SimpleDateFormat sf = new SimpleDateFormat("MM-dd");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date parse = null;
        try {
            parse = simpleDateFormat.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(parse);
        c.add(Calendar.DAY_OF_MONTH, 0);
        String yestoday = sf.format(c.getTime());
        textView7.setText(yestoday);
        c.add(Calendar.DAY_OF_MONTH, 1);
        String today = sf.format(c.getTime());
        textView8.setText(today);
        c.add(Calendar.DAY_OF_MONTH, 1);
        String tomorrow = sf.format(c.getTime());
        textView9.setText(tomorrow);
        c.add(Calendar.DAY_OF_MONTH, 1);
        String tomorrow1 = sf.format(c.getTime());
        textView10.setText(tomorrow1);
        c.add(Calendar.DAY_OF_MONTH, 1);
        String tomorrow2 = sf.format(c.getTime());
        textView11.setText(tomorrow2);
    }

    private void initListView() {
        progressBar.setVisibility(View.VISIBLE);
        Connection conn = null;
        CallableStatement stmt;
        final DataTable tb = new DataTable();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = null;
        java.util.Date parse = null;
        try {
            parse = simpleDateFormat.parse(startTime);
            format = new SimpleDateFormat("yyyy-MM-dd HH;MM;ss").format(parse);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        java.sql.Date date1 = new java.sql.Date(parse.getTime());
        DataRow rw;

//        Date date = Date.valueOf(format);
        Log.e("len", startTime + "FORMAT:" + format);
        //pkg_getrecord.p_re_outplan_cur(p_plan_name => ,p_start_date => ,p_days => ,p_flag => ,rtncur => )
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.21:1609:PROD1", "apps", "apps");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String sql = "{call pkg_getrecord.p_re_outplan_cur(?,?,?,?,?)}";
            stmt = conn.prepareCall(sql);
            stmt.setObject(1, planName);
//            stmt.setObject(2, parse);
//            stmt.setObject(2, "to_date(" + "'" + format + "'" + ", 'yyyy-mm-dd hh24:mi:ss')");
            stmt.setDate(2, date1);
            stmt.setObject(3, 30);
            if ("是".equals(sumShort)) {
                stmt.setObject(4, 0);
            } else {
                stmt.setObject(4, 1);
            }
            stmt.registerOutParameter(5, OracleTypes.CURSOR);
            stmt.execute();
            ResultSet cursor = ((OracleCallableStatement) stmt).getCursor(5);
            ResultSetMetaData metaData = cursor.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (cursor.next()) {
                rw = new DataRow();
//                for (int i = 0; i < columnCount; i++) {
//                    String string = cursor.getString(i);
//                    Log.e("len", "STRING" + string);
//                }

                rw.setValue("work_line", cursor.getString("WORK_LINE"));
                rw.setValue("agent_name", cursor.getString("AGENT_NAME"));
                rw.setValue("quantity_recv", cursor.getString("QUANTITY_RECV"));
                rw.setValue("wip_code", cursor.getString("WIP_CODE"));
                rw.setValue("quantity_stock", cursor.getString("QUANTITY_STOCK"));
                rw.setValue("quantity_on_order", cursor.getString("QUANTITY_ON_ORDER"));
                rw.setValue("quantity_ship", cursor.getString("QUANTITY_SHIP"));
                rw.setValue("organization_code", cursor.getString("ORGANIZATION_CODE"));
                rw.setValue("plan_name", cursor.getString("PLAN_NAME"));
                rw.setValue("quantity_wip", cursor.getString("QUANTITY_WIP"));
                rw.setValue("quantity_wip_b", cursor.getString("QUANTITY_WIP_B"));
                rw.setValue("quantity_stock_b", cursor.getString("QUANTITY_STOCK_B"));
                rw.setValue("item_code", cursor.getString("ITEM_CODE"));
                rw.setValue("item_name", cursor.getString("ITEM_NAME"));
                rw.setValue("quantity_short", cursor.getString("QUANTITY_SHORT"));
                rw.setValue("vendor_name", cursor.getString("VENDOR_NAME"));
                rw.setValue("plan_date", cursor.getString("PLAN_DATE"));
                rw.setValue("date1", cursor.getString(startTime.substring(0, 5) + textView7.getText().toString()));
                rw.setValue("date2", cursor.getString(startTime.substring(0, 5) + textView8.getText().toString()));
                rw.setValue("date3", cursor.getString(startTime.substring(0, 5) + textView9.getText().toString()));
                rw.setValue("date4", cursor.getString(startTime.substring(0, 5) + textView10.getText().toString()));
                rw.setValue("date5", cursor.getString(startTime.substring(0, 5) + textView11.getText().toString()));
//                rw.setValue("date4", cursor.getString(startTime.substring(0, 5) + textviewDate4.getText().toString()));
//                rw.setValue("date5", cursor.getString(startTime.substring(0, 5) + textviewDate5.getText().toString()));
//                rw.setValue("date6", cursor.getString(startTime.substring(0, 5) + textviewDate6.getText().toString()));
//                rw.setValue("date7", cursor.getString(startTime.substring(0, 5) + textviewDate7.getText().toString()));
//                rw.setValue("date8", cursor.getString(startTime.substring(0, 5) + textviewDate8.getText().toString()));
//                rw.setValue("date9", cursor.getString(startTime.substring(0, 5) + textviewDate9.getText().toString()));
//                rw.setValue("date10", cursor.getString(startTime.substring(0, 5) + textviewDate10.getText().toString()));

                if (rw.getValue("agent_name") != null) {
                    tb.Rows.add(rw);
                }
            }
            progressBar.setVisibility(View.GONE);
            shortAdapter = new PurchaseAdapter(tb);
            listView.setAdapter(shortAdapter);
//            rs = ((OracleCallableStatement) stmt).getCursor(5);
//            Log.e("len")
//            DataRow rw;
//            ResultSetMetaData metaData = rs.getMetaData();
//            int columnCount = metaData.getColumnCount();
//            Log.e("len", "COLUMNCOUNT:" + columnCount);
//            while (rs.next()) {
//                rw = new DataRow();
//                for (int i = 0; i < columnCount; i++) {
//                    Object value = rw.getValue(i);
//                    Log.e("len", "VALUE:" + value);
//                }
//            }
        } catch (SQLException e) {
            e.printStackTrace();
            App.Current.showInfo(ShortReportKanbanActivity.this, e.getMessage() + "111111");
        }

        initException();
    }

    private void initException() {
        String sql = "exec fm_get_short_materiel_items_and";
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataTable> value = Value;
                if(value.HasError) {
                    App.Current.showError(ShortReportKanbanActivity.this, value.Error);
                    linearLayout.setVisibility(View.GONE);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    stringBuilder = new StringBuilder();
                    linearLayout.setVisibility(View.VISIBLE);
                    int po = 0;
                    while (po < 3) {
                        new Thread() {
                            @Override
                            public void run() {
                                int i = 0;
                                while (i < value.Value.Rows.size()) {
                                    DataRow dataRow = value.Value.Rows.get(i);
                                    String workLine = dataRow.getValue("name", "");
                                    String orgName = dataRow.getValue("org_name", "");
                                    String exception_type = dataRow.getValue("exception_type", "");
                                    String exception_comment = dataRow.getValue("exception_comment", "");
                                    Date date = dataRow.getValue("create_time", new Date(0));
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                                    String createDate = simpleDateFormat.format(date);
                                    mSpeechSynthesizer.speak(orgName + workLine + "在" + createDate + "有" + exception_type + "呼叫," + exception_comment);
                                    mSpeechSynthesizer.setSpeechSynthesizerListener(ShortReportKanbanActivity.this);
                                    i++;
                                }
                            }
                        }.start();
                        po ++;
                    }

                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        DataRow dataRow = value.Value.Rows.get(i);
                        String workLine = dataRow.getValue("name", "");
                        String orgName = dataRow.getValue("org_name", "");
                        String exception_type = dataRow.getValue("exception_type", "");
                        String exception_comment = dataRow.getValue("exception_comment", "");
                        Date date = dataRow.getValue("create_time", new Date(0));
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                        String createDate = simpleDateFormat.format(date);
                        if (i == value.Value.Rows.size() - 1) {
                            stringBuilder.append(orgName + workLine + "在" + createDate + "有" + exception_type + "呼叫,料号：" + exception_comment + "。");
                        } else {
                            stringBuilder.append(orgName + workLine + "在" + createDate + "有" + exception_type + "呼叫,料号：" + exception_comment + "，");
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
    }

    @Override
    public void onSynthesizeStart(String s) {
        //监听到合成开始
    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
        //监听到有合成数据到达
    }

    @Override
    public void onSynthesizeFinish(String s) {
        //监听到合成结束
    }

    @Override
    public void onSpeechStart(String s) {
        //监听到合成并开始播放
    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {
        //监听到播放进度有变化
    }

    @Override
    public void onSpeechFinish(String s) {
        //监听到播放结束
        SystemClock.sleep(2000);
    }

    @Override
    public void onError(String s, SpeechError speechError) {
        //监听到出错
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
            PurchaseViewHolder holder;
            if (view == null) {
                view = View.inflate(ShortReportKanbanActivity.this, R.layout.short_report_item, null);
                holder = new PurchaseViewHolder();
                holder.textView2 = (TextView) view.findViewById(R.id.textview_2);
                holder.textView3 = (TextView) view.findViewById(R.id.textview_3);
                holder.textView4 = (TextView) view.findViewById(R.id.textview_4);
                holder.textView5 = (TextView) view.findViewById(R.id.textview_5);
                holder.textView6 = (TextView) view.findViewById(R.id.textview_6);
                holder.textView7 = (TextView) view.findViewById(R.id.textview_7);
                holder.textView8 = (TextView) view.findViewById(R.id.textview_8);
                holder.textView9 = (TextView) view.findViewById(R.id.textview_9);
                holder.textView10 = (TextView) view.findViewById(R.id.textview_10);
                holder.textView11 = (TextView) view.findViewById(R.id.textview_11);
                holder.textView12 = (TextView) view.findViewById(R.id.textview_12);
                view.setTag(holder);
            } else {
                holder = (PurchaseViewHolder) view.getTag();
            }
            String work_line = mDataTable.Rows.get(i).getValue("work_line", "");
            String agent_name = mDataTable.Rows.get(i).getValue("agent_name", "");
            String quantity_recv = mDataTable.Rows.get(i).getValue("quantity_recv", "");
            String wip_code = mDataTable.Rows.get(i).getValue("wip_code", "");
            String quantity_stock = mDataTable.Rows.get(i).getValue("quantity_stock", "");
            String quantity_on_order = mDataTable.Rows.get(i).getValue("quantity_on_order", "");
            String quantity_ship = mDataTable.Rows.get(i).getValue("quantity_ship", "");
            String organization_code = mDataTable.Rows.get(i).getValue("organization_code", "");
            String plan_name = mDataTable.Rows.get(i).getValue("plan_name", "");
            String quantity_wip = mDataTable.Rows.get(i).getValue("quantity_wip", "");
            String quantity_wip_b = mDataTable.Rows.get(i).getValue("quantity_wip_b", "");
            String quantity_stock_b = mDataTable.Rows.get(i).getValue("quantity_stock_b", "");
            String item_code = mDataTable.Rows.get(i).getValue("item_code", "");
            String item_name = mDataTable.Rows.get(i).getValue("item_name", "");
            String quantity_short = mDataTable.Rows.get(i).getValue("quantity_short", "");
            String vendor_name = mDataTable.Rows.get(i).getValue("vendor_name", "");
            String plan_date = mDataTable.Rows.get(i).getValue("plan_date", "");
            String date1 = mDataTable.Rows.get(i).getValue("date1", "");
            String date2 = mDataTable.Rows.get(i).getValue("date2", "");
            String date3 = mDataTable.Rows.get(i).getValue("date3", "");
            String date4 = mDataTable.Rows.get(i).getValue("date4", "");
            String date5 = mDataTable.Rows.get(i).getValue("date5", "");
//        String date4 = mList.Rows.get(i).getValue("date4", "");
//        String date5 = mList.Rows.get(i).getValue("date5", "");
//        String date6 = mList.Rows.get(i).getValue("date6", "");
//        String date7 = mList.Rows.get(i).getValue("date7", "");
//        String date8 = mList.Rows.get(i).getValue("date8", "");
//        String date9 = mList.Rows.get(i).getValue("date9", "");
//        String date10 = mList.Rows.get(i).getValue("date10", "");

            holder.textView2.setText(agent_name);
            holder.textView3.setText(item_name);
            holder.textView4.setText(vendor_name);
            holder.textView5.setText(plan_date.length() > 9 ? plan_date.substring(0, 10) : plan_date);
            holder.textView6.setText(quantity_short);
            holder.textView7.setText(date1);
            holder.textView8.setText(date2);
            holder.textView9.setText(date3);
            holder.textView10.setText(date4);
            holder.textView11.setText(date5);
            holder.textView12.setText(item_code);
            return view;
        }
    }

    class PurchaseViewHolder {
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
    }
}
