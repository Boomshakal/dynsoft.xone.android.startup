package dynsoft.xone.android.activity;
import java.io.Serializable;
import android.content.DialogInterface;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import android.os.Handler;
import android.os.SystemClock;
import android.text.format.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import dynsoft.xone.android.activity.LightKanbanActivity;
import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.start.FrmLogin;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml.Encoding;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.baidu.tts.client.SpeechError;

/**
 * Created by Administrator on 2017/12/7.
 */

public class YhActivity extends Activity {
    String currentTime=new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
    String currentTime1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString();
    Date today = new Date();
    Calendar c = Calendar.getInstance();
    //String currentTime1=new SimpleDateFormat("HH:mm:ss").format(new Date()).toString();
    public TableAdapter Adapter;
    public DataTable dataTable;
    public  TextView textview_time;
    private ProgressBar progressBar;
    private TextView textview_1;
    public String image_urls;
    public ListView Matrix;
    public  String flag1;
    private PostKillAdapter postKillAdapter;
    private Handler handler;
    public int flag = 1;
    private static final int LISTVIEWDELAYTIME1 = 30*60 * 1000;
    private static final int LISTVIEWDELAYTIME2 = 10* 1000;
    private static final int SCROLLTIME = 30*60 * 1000;
    public Runnable runnable1 = new Runnable() {
        @Override
        public void run() {

                initData1();

            handler.postDelayed(runnable1, SCROLLTIME);

        }
    };
   private Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            initData2();
           handler.postDelayed(runnable2, LISTVIEWDELAYTIME1);
        }
    };
    private Runnable runnable4 = new Runnable() {
        @Override
        public void run() {
            initData3();
            handler.postDelayed(runnable2, LISTVIEWDELAYTIME1);
        }
    };
    private Runnable runnable5 = new Runnable() {
        @Override
        public void run() {
            initData4();
            handler.postDelayed(runnable2, LISTVIEWDELAYTIME1);
        }
    };
    private Runnable runnable3 = new Runnable() {
        @Override
        public void run() {

            initTitle();
            if(flag1.equals("1")||flag1.equals("2")){
                initData1();
            }
            if(flag1.equals("3")||flag1.equals("4")||flag1.equals("5")){
                initData2();
            }
            if(flag1.equals("6")||flag1.equals("7")||flag1.equals("8")){
                initData3();
            }
            if(flag1.equals("9")||flag1.equals("0")){
                initData4();
            }
            handler.postDelayed(runnable3, LISTVIEWDELAYTIME2);
        }
    };
//    private ArrayList<String> exception_types;
//    private String exception_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Time time = new Time();
        time.setToNow();
        int hour = time.hour;
        setContentView(R.layout.yhactivity);
        textview_1 = (TextView) findViewById(R.id.textview_1);
        textview_time = (TextView) findViewById(R.id.textview_time);
        if (textview_1 != null) {
            textview_1.setTextColor(getResources().getColor(R.color.black));
            textview_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //改变image_urls
                }
            });
        }
        handler = new Handler();

//            handler.post(runnable1);
//
//            handler.post(runnable2);
//
//            handler.post(runnable3);
//
//            handler.post(runnable4);
//
        final Intent intent = getIntent();
        image_urls = intent.getStringExtra("image_urls");
        if (image_urls.equals("自动轮播;")) {
            handler.post(runnable3);
        }
        else{
            initLightView();
            Log.e("LZH624", image_urls);
        }
}
    private void initData1() {
        dataTable = new DataTable();
        final Intent intent = getIntent();
        image_urls = intent.getStringExtra("image_urls");
        this.Matrix = (ListView) this.findViewById(R.id.listview);
        Log.e("LZH", image_urls);
        String sql = " EXEC p_mm_wo_workshop_plan_get_items_and ?,6,'','','Line1(包装);',''";
        Parameters p = new Parameters().add(1,currentTime1);
        Log.e("LZH2", image_urls);
       // p.add(1, image_urls);
        Log.e("LZH3", image_urls);
        App.Current.DbPortal.ExecuteDataTableAsync("mgmt_ts_erp_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                Log.e("LZH4", "111111111111");
                if (value.HasError) {
                    Log.e("LZH5", image_urls);
                    App.Current.toastError(YhActivity.this, value.Error);
                    return;
                }
                if (value.Value != null) {
                    postKillAdapter = new PostKillAdapter(value.Value);
                    Matrix.setAdapter(postKillAdapter);
                }
            }
        });
        Log.e("LZH6666", image_urls);
        Log.e("LZH99999", "11");

    }
    private void initData2() {
        dataTable = new DataTable();
        final Intent intent = getIntent();
        image_urls = intent.getStringExtra("image_urls");
        this.Matrix = (ListView) this.findViewById(R.id.listview);
        Log.e("LZH", image_urls);
        String sql = " EXEC p_mm_wo_workshop_plan_get_items_and ?,6,'','','Line2(包装);',''";
        Parameters p = new Parameters().add(1,currentTime1);
        Log.e("LZH2", image_urls);
        // p.add(1, image_urls);
        Log.e("LZH3", image_urls);
        App.Current.DbPortal.ExecuteDataTableAsync("mgmt_ts_erp_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                Log.e("LZH4", "111111111111");
                if (value.HasError) {
                    Log.e("LZH5", image_urls);
                    App.Current.toastError(YhActivity.this, value.Error);
                    return;
                }
                if (value.Value != null) {
                    postKillAdapter = new PostKillAdapter(value.Value);
                    Matrix.setAdapter(postKillAdapter);
                }
            }
        });
        Log.e("LZH6666", image_urls);
        Log.e("LZH99999", "22");

    }
    private void initData3() {
        dataTable = new DataTable();
        final Intent intent = getIntent();
        image_urls = intent.getStringExtra("image_urls");
        this.Matrix = (ListView) this.findViewById(R.id.listview);
        Log.e("LZH", image_urls);
        String sql = " EXEC p_mm_wo_workshop_plan_get_items_and ?,6,'','','Line3(包装);',''";
        Parameters p = new Parameters().add(1,currentTime1);
        Log.e("LZH2", image_urls);
        // p.add(1, image_urls);
        Log.e("LZH3", image_urls);
        App.Current.DbPortal.ExecuteDataTableAsync("mgmt_ts_erp_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    Log.e("LZH5", image_urls);
                    App.Current.toastError(YhActivity.this, value.Error);
                    return;
                }
                if (value.Value != null) {
                    postKillAdapter = new PostKillAdapter(value.Value);
                    Matrix.setAdapter(postKillAdapter);
                }
            }
        });
        Log.e("LZH6666", image_urls);
        Log.e("LZH99999", "33");

    }
    private void initData4() {
        dataTable = new DataTable();
        final Intent intent = getIntent();
        image_urls = intent.getStringExtra("image_urls");
        this.Matrix = (ListView) this.findViewById(R.id.listview);
        Log.e("LZH", image_urls);
        String sql = " EXEC p_mm_wo_workshop_plan_get_items_and ?,6,'','','Line4(包装);',''";
        Parameters p = new Parameters().add(1,currentTime1);
        Log.e("LZH2", image_urls);
        // p.add(1, image_urls);
        Log.e("LZH3", image_urls);
        App.Current.DbPortal.ExecuteDataTableAsync("mgmt_ts_erp_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                Log.e("LZH4", "111111111111");
                if (value.HasError) {
                    Log.e("LZH5", image_urls);
                    App.Current.toastError(YhActivity.this, value.Error);
                    return;
                }
                if (value.Value != null) {
                    postKillAdapter = new PostKillAdapter(value.Value);
                    Matrix.setAdapter(postKillAdapter);
                }
            }
        });
        Log.e("LZH6666", image_urls);
        Log.e("LZH99999", "44");

    }
    private void initLightView() {
        dataTable = new DataTable();
        //        progressBar = (ProgressBar) findViewById(R.id.progressbar);
//        progressBar.setVisibility(View.VISIBLE);
        final Intent intent = getIntent();
        image_urls = intent.getStringExtra("image_urls");
        this.Matrix = (ListView) this.findViewById(R.id.listview);
        Log.e("LZH", image_urls);
        String sql = " EXEC p_mm_wo_workshop_plan_get_items_and ?,6,'','',?,''";
        Parameters p = new Parameters().add(1,currentTime1).add(2,image_urls);
        Log.e("LZHmy", image_urls);
       // p.add(1, image_urls);
        //Log.e("LZH3", image_urls);
        App.Current.DbPortal.ExecuteDataTableAsync("mgmt_ts_erp_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                Log.e("LZH4", "111111111111");
                if (value.HasError) {
                    Log.e("LZH5", image_urls);
                    App.Current.toastError(YhActivity.this, value.Error);
                    return;
                }
                if(value.Value == null){
                    App.Current.toastError(YhActivity.this, "数据空");
                }
                if (value.Value != null ) {
                    postKillAdapter = new PostKillAdapter(value.Value);
                    Matrix.setAdapter(postKillAdapter);
                }
            }
        });
        Log.e("LZH6", image_urls);


    }
    private void initTitle() {
        if (textview_time != null) {
            textview_time.setText("怡和车间看板   日期：" + new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));
            currentTime1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString();
            String[] arr = currentTime1.split(":");
            if (arr.length > 1) {

                flag1 = arr[1].substring(1);
                Log.e("LZH7",flag1 );
            }
        }
    }
    private void inittime() {

        currentTime1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString();
        Log.e("LZH2011",currentTime1);
    }
    class PostKillAdapter extends BaseAdapter {
        private DataTable dataTable;

        public PostKillAdapter(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        @Override
        public int getCount() {
            return dataTable == null ? 0 : dataTable.Rows.size();
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
//            ViewHolder viewHolder;
//            if (view == null) {
//                view = View.inflate(PostSkillActivity.this, R.layout.item_post_skill, null);
//                viewHolder = new ViewHolder();
//                viewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.item_post_linearlayout);
//                view.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) view.getTag();
//            }

            PurchaseViewHolder purchaseViewHolder;
            if (view == null) {
                Time time = new Time();
                time.setToNow();
                int hour = time.hour;

                    view = View.inflate(YhActivity.this, R.layout.yh_mps_line, null);


                purchaseViewHolder = new PurchaseViewHolder();
                purchaseViewHolder.textView1 = (TextView) view.findViewById(R.id.textview_1);
                purchaseViewHolder.textView2 = (TextView) view.findViewById(R.id.textview_2);
                purchaseViewHolder.textView3 = (TextView) view.findViewById(R.id.textview_3);
                purchaseViewHolder.textView4 = (TextView) view.findViewById(R.id.textview_4 );
                purchaseViewHolder.textView5 = (TextView) view.findViewById(R.id.textview_5);
                purchaseViewHolder.textView6 = (TextView) view.findViewById(R.id.textview_6);
                purchaseViewHolder.textView7 = (TextView) view.findViewById(R.id.textview_7);
                purchaseViewHolder.textView8 = (TextView) view.findViewById(R.id.textview_8);
                purchaseViewHolder.textView9 = (TextView) view.findViewById(R.id.textview_9);
                purchaseViewHolder.textView10 = (TextView) view.findViewById(R.id.textview_10);
                purchaseViewHolder.textView11 = (TextView) view.findViewById(R.id.textview_11);




                view.setTag(purchaseViewHolder);
            } else {
                purchaseViewHolder = (PurchaseViewHolder) view.getTag();
            }
            c.setTime(today);
            c.add(Calendar.DAY_OF_MONTH, 1);
            Date tomorrow = c.getTime();
            Calendar a = Calendar.getInstance();
            a.setTime(today);
            a.add(Calendar.DAY_OF_MONTH, 2);
            Date to2 = a.getTime();

            Calendar b = Calendar.getInstance();
            b.setTime(today);
            b.add(Calendar.DAY_OF_MONTH, 3);
            Date to3 = b.getTime();
            Calendar d = Calendar.getInstance();
            d.setTime(today);
            d.add(Calendar.DAY_OF_MONTH, 4);
            Date to4 = d.getTime();
            Calendar e = Calendar.getInstance();
            e.setTime(today);
            e.add(Calendar.DAY_OF_MONTH, 5);
            Date to5 = e.getTime();
            Calendar f = Calendar.getInstance();
            f.setTime(today);
            f.add(Calendar.DAY_OF_MONTH, 6);
            Date to6 = f.getTime();
            String currentTime2=new SimpleDateFormat("yyyy-MM-dd").format(tomorrow).toString();
            String currentTime3=new SimpleDateFormat("yyyy-MM-dd").format(to2).toString();
            String currentTime4=new SimpleDateFormat("yyyy-MM-dd").format(to3).toString();
            String currentTime5=new SimpleDateFormat("yyyy-MM-dd").format(to4).toString();
            String currentTime6=new SimpleDateFormat("yyyy-MM-dd").format(to5).toString();
            String currentTime7=new SimpleDateFormat("yyyy-MM-dd").format(to6).toString();
            Log.e("LZH22",currentTime2);
            Log.e("LZH33",currentTime3);
            Log.e("LZH144",currentTime4);
            Log.e("LZH155",currentTime5);

            Log.e("LZH166",currentTime6);
           Log.e("LZH177",currentTime7);
            Log.e("LZH123",currentTime);
            String date1 = currentTime;
            String date2 = currentTime2;
            String date3 = currentTime3;
            String date4 = currentTime4;
            String date5 = currentTime5;
            String date6 = currentTime6;
            String date7 = currentTime7;


                purchaseViewHolder.textView1.setText(dataTable.Rows.get(i).getValue("line_name", ""));
                purchaseViewHolder.textView2.setText(dataTable.Rows.get(i).getValue("mo_no", ""));
                purchaseViewHolder.textView3.setText(dataTable.Rows.get(i).getValue("prd_name", ""));
                purchaseViewHolder.textView4.setText(String.valueOf(dataTable.Rows.get(i).getValue("sum_plan_qty",new BigDecimal(0)).intValue()));
                purchaseViewHolder.textView5.setText(String.valueOf(dataTable.Rows.get(i).getValue(date1, "")));
                purchaseViewHolder.textView6.setText(String.valueOf(dataTable.Rows.get(i).getValue(date2, "")));
                purchaseViewHolder.textView7.setText(String.valueOf(dataTable.Rows.get(i).getValue(date3, "")));
                purchaseViewHolder.textView8.setText(String.valueOf(dataTable.Rows.get(i).getValue(date4, "")));
                purchaseViewHolder.textView9.setText(String.valueOf(dataTable.Rows.get(i).getValue(date5, "")));
                purchaseViewHolder.textView10.setText(String.valueOf(dataTable.Rows.get(i).getValue(date6, "")));
                purchaseViewHolder.textView11.setText(String.valueOf(dataTable.Rows.get(i).getValue(date7, "")));


            return view;
        }
    }
        class PurchaseViewHolder {
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


        }
}
