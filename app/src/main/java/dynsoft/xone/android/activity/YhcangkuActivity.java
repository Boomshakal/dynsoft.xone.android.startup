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

/**
 * Created by Administrator on 2017/12/7.
 */

public class YhcangkuActivity extends Activity {
    String currentTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
    Date today = new Date();
    Calendar c = Calendar.getInstance();
    //String currentTime1=new SimpleDateFormat("HH:mm:ss").format(new Date()).toString();
    public TableAdapter Adapter;
    public DataTable dataTable;
    private ProgressBar progressBar;
    private TextView textview_1;
    public String image_urls;
    public ListView Matrix;
    private PostKillAdapter postKillAdapter;
    private static final int LISTVIEWDELAYTIME1 = 20 * 60 * 1000;
    private Handler handler;
    public Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            initData();
            handler.postDelayed(runnable1, LISTVIEWDELAYTIME1);
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

        setContentView(R.layout.yhcangkuactivity);
        textview_1 = (TextView) findViewById(R.id.textview_1);
        if (textview_1 != null) {
            textview_1.setTextColor(getResources().getColor(R.color.black));
            textview_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //改变image_urls
                }
            });
        }
        dataTable = new DataTable();
//        progressBar = (ProgressBar) findViewById(R.id.progressbar);
//        progressBar.setVisibility(View.VISIBLE);
        final Intent intent = getIntent();
        image_urls = intent.getStringExtra("image_urls");
        this.Matrix = (ListView) this.findViewById(R.id.listview);
        Log.e("LZH", image_urls);
        handler = new Handler();
        handler.post(runnable1);
    }


    private void initData() {
        String sql = "exec select_wo_task_order_ycl_info";
        Parameters p = new Parameters();
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                Log.e("LZH4", "111111111111");
                if (value.HasError) {
                    Log.e("LZH5", image_urls);
                    App.Current.toastError(YhcangkuActivity.this, value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 5) {
                    postKillAdapter = new PostKillAdapter(value.Value);
                    Matrix.setAdapter(postKillAdapter);
                }
            }
        });
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
            PurchaseViewHolder purchaseViewHolder;
            if (view == null) {
                Time time = new Time();
                time.setToNow();
                int hour = time.hour;

                view = View.inflate(YhcangkuActivity.this, R.layout.yh_cangku_mps_line, null);


                purchaseViewHolder = new PurchaseViewHolder();
                purchaseViewHolder.textView1 = (TextView) view.findViewById(R.id.textview_1);
                purchaseViewHolder.textView2 = (TextView) view.findViewById(R.id.textview_2);
                purchaseViewHolder.textView3 = (TextView) view.findViewById(R.id.textview_3);
                purchaseViewHolder.textView4 = (TextView) view.findViewById(R.id.textview_4);
                purchaseViewHolder.textView5 = (TextView) view.findViewById(R.id.textview_5);
                purchaseViewHolder.textView6 = (TextView) view.findViewById(R.id.textview_6);
                purchaseViewHolder.textView7 = (TextView) view.findViewById(R.id.textview_7);
                purchaseViewHolder.textView8 = (TextView) view.findViewById(R.id.textview_8);
                purchaseViewHolder.textView9 = (TextView) view.findViewById(R.id.textview_9);
                purchaseViewHolder.textView10 = (TextView) view.findViewById(R.id.textview_10);
                purchaseViewHolder.textView11 = (TextView) view.findViewById(R.id.textview_11);
                purchaseViewHolder.textView12 = (TextView) view.findViewById(R.id.textview_12);
                purchaseViewHolder.textView13 = (TextView) view.findViewById(R.id.textview_13);
                purchaseViewHolder.textView14 = (TextView) view.findViewById(R.id.textview_14);
                purchaseViewHolder.textView15 = (TextView) view.findViewById(R.id.textview_15);
                purchaseViewHolder.textView16 = (TextView) view.findViewById(R.id.textview_16);


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
            Date to3 = a.getTime();
            Calendar d = Calendar.getInstance();
            d.setTime(today);
            d.add(Calendar.DAY_OF_MONTH, 4);
            Date to4 = b.getTime();
            Calendar e = Calendar.getInstance();
            e.setTime(today);
            e.add(Calendar.DAY_OF_MONTH, 5);
            Date to5 = a.getTime();
            Calendar f = Calendar.getInstance();
            f.setTime(today);
            f.add(Calendar.DAY_OF_MONTH, 6);
            Date to6 = a.getTime();
            String currentTime2 = new SimpleDateFormat("yyyy-MM-dd").format(tomorrow).toString();
            String currentTime3 = new SimpleDateFormat("yyyy-MM-dd").format(to2).toString();
            String currentTime4 = new SimpleDateFormat("yyyy-MM-dd").format(to3).toString();
            String currentTime5 = new SimpleDateFormat("yyyy-MM-dd").format(to4).toString();
            String currentTime6 = new SimpleDateFormat("yyyy-MM-dd").format(to5).toString();
            String currentTime7 = new SimpleDateFormat("yyyy-MM-dd").format(to6).toString();
            Log.e("LZH127", currentTime7);
            Log.e("LZH127", currentTime);
            String date1 = currentTime;
            String date2 = currentTime2;
            String date3 = currentTime3;
            String date4 = currentTime4;
            String date5 = currentTime5;
            String date6 = currentTime6;
            String date7 = currentTime7;


            purchaseViewHolder.textView1.setText(dataTable.Rows.get(i).getValue("仓管员", ""));
            purchaseViewHolder.textView2.setText(String.valueOf(dataTable.Rows.get(i).getValue("截止今日未入", "")));
            purchaseViewHolder.textView3.setText(String.valueOf(dataTable.Rows.get(i).getValue("今日新增入库", "")));
            purchaseViewHolder.textView4.setText(String.valueOf(dataTable.Rows.get(i).getValue("今日入库", "")));
            purchaseViewHolder.textView5.setText(String.valueOf(dataTable.Rows.get(i).getValue("截止未入", "")));
            purchaseViewHolder.textView6.setText(String.valueOf(dataTable.Rows.get(i).getValue("预警未入", "")));
            purchaseViewHolder.textView7.setText(String.valueOf(dataTable.Rows.get(i).getValue("截止今日未发", "")));
            purchaseViewHolder.textView8.setText(String.valueOf(dataTable.Rows.get(i).getValue("今日新增发料", "")));
            purchaseViewHolder.textView9.setText(String.valueOf(dataTable.Rows.get(i).getValue("今日发料", "")));
            purchaseViewHolder.textView10.setText(String.valueOf(dataTable.Rows.get(i).getValue("截止未发", "")));
            purchaseViewHolder.textView11.setText(String.valueOf(dataTable.Rows.get(i).getValue("预警未发", "")));
            purchaseViewHolder.textView12.setText(String.valueOf(dataTable.Rows.get(i).getValue("截止今日未盘", "")));
            purchaseViewHolder.textView13.setText(String.valueOf(dataTable.Rows.get(i).getValue("今日盘点", "")));
            purchaseViewHolder.textView14.setText(String.valueOf(dataTable.Rows.get(i).getValue("总条数", "")));
            purchaseViewHolder.textView15.setText(String.valueOf(dataTable.Rows.get(i).getValue("总完成量", "")));
            purchaseViewHolder.textView16.setText(String.valueOf(dataTable.Rows.get(i).getValue("总未完成量", "")));

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
        private TextView textView12;
        private TextView textView13;
        private TextView textView14;
        private TextView textView15;
        private TextView textView16;

    }
}
