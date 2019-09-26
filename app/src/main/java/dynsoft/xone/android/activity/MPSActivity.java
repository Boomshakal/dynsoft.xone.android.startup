package dynsoft.xone.android.activity;
import java.io.Serializable;
import android.content.DialogInterface;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
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

public class MPSActivity extends Activity {
    String currentTime=new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
    //String currentTime1=new SimpleDateFormat("HH:mm:ss").format(new Date()).toString();
    public TableAdapter Adapter;
    public DataTable dataTable;
    private ProgressBar progressBar;
    private TextView textview_1;
    public String image_urls;
    public ListView Matrix;
    private PostKillAdapter postKillAdapter;
//    private ArrayList<String> exception_types;
//    private String exception_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Time time = new Time();
        time.setToNow();
        int hour = time.hour;
 if(hour>8&&hour<20){
        setContentView(R.layout.mpsactivity);
 }
        else {
     setContentView(R.layout.mpsactivity_night);
 }
        textview_1 = (TextView) findViewById(R.id.textview_1);
        if (textview_1 != null) {
            textview_1.setTextColor(getResources().getColor(R.color.black));
            textview_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //¸Ä±äimage_urls
                }
            });
        }
        dataTable = new DataTable();
//        progressBar = (ProgressBar) findViewById(R.id.progressbar);
//        progressBar.setVisibility(View.VISIBLE);
        final Intent intent = getIntent();
        image_urls = intent.getStringExtra("image_urls");
        this.Matrix = (ListView)this.findViewById(R.id.listview);
        Log.e("LZH",image_urls);
        String sql = " EXEC p_mm_wo_smt_prod_plan_get_items_v2 ?";
        Parameters p = new Parameters();
        Log.e("LZH2",image_urls);
        p.add(1,image_urls);
        Log.e("LZH3",image_urls);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql,p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                Log.e("LZH4", "111111111111");
                if (value.HasError) {
                    Log.e("LZH5", image_urls);
                    App.Current.toastError(MPSActivity.this, value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 5) {
                    postKillAdapter =   new PostKillAdapter(value.Value);
                    Matrix.setAdapter(postKillAdapter);
                }
            }
        });
        Log.e("LZH6", image_urls);

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
                if(hour>8&&hour<20){
                    view = View.inflate(MPSActivity.this, R.layout.mps_line, null);
                }else {
                    view = View.inflate(MPSActivity.this, R.layout.mps_line_night, null);
                }

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
                purchaseViewHolder.textView12 = (TextView) view.findViewById(R.id.textview_12);
                purchaseViewHolder.textView13 = (TextView) view.findViewById(R.id.textview_13);
                purchaseViewHolder.textView14 = (TextView) view.findViewById(R.id.textview_14);



                view.setTag(purchaseViewHolder);
            } else {
                purchaseViewHolder = (PurchaseViewHolder) view.getTag();
            }
            String date1 = currentTime+" 08:00:00";
            String date2 = currentTime+" 09:00:00";
            String date3 = currentTime+" 10:00:00";
            String date4 = currentTime+" 11:00:00";
            String date5 = currentTime+" 12:00:00";
            String date6 = currentTime+" 13:00:00";
            String date7 = currentTime+" 14:00:00";
            String date8 = currentTime+" 15:00:00";
            String date9 = currentTime+" 16:00:00";
            String date10 = currentTime+" 17:00:00";
            String date11 = currentTime+" 18:00:00";
            String date12 = currentTime+" 19:00:00";

                purchaseViewHolder.textView1.setText(dataTable.Rows.get(i).getValue("line_name2", ""));
                purchaseViewHolder.textView2.setText(dataTable.Rows.get(i).getValue("project_name", ""));
                purchaseViewHolder.textView3.setText(dataTable.Rows.get(i).getValue(date1, ""));
                purchaseViewHolder.textView4.setText(dataTable.Rows.get(i).getValue(date2, ""));
                purchaseViewHolder.textView5.setText(dataTable.Rows.get(i).getValue(date3, ""));
                purchaseViewHolder.textView6.setText(dataTable.Rows.get(i).getValue(date4, ""));
                purchaseViewHolder.textView7.setText(dataTable.Rows.get(i).getValue(date5, ""));
                purchaseViewHolder.textView8.setText(dataTable.Rows.get(i).getValue(date6, ""));
                purchaseViewHolder.textView9.setText(dataTable.Rows.get(i).getValue(date7, ""));
                purchaseViewHolder.textView10.setText(dataTable.Rows.get(i).getValue(date8, ""));
                purchaseViewHolder.textView11.setText(dataTable.Rows.get(i).getValue(date9, ""));
                purchaseViewHolder.textView12.setText(dataTable.Rows.get(i).getValue(date10, ""));
                purchaseViewHolder.textView13.setText(dataTable.Rows.get(i).getValue(date11, ""));
                purchaseViewHolder.textView14.setText(dataTable.Rows.get(i).getValue(date12, ""));


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

        }
}
