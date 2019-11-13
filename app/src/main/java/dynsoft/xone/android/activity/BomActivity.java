package dynsoft.xone.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2017/12/7.
 */

public class BomActivity extends Activity {
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

        setContentView(R.layout.bomactivity);
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
        this.Matrix = (ListView) this.findViewById(R.id.listview);
        Log.e("LZH", image_urls);
        handler = new Handler();
        handler.post(runnable1);
    }


    private void initData() {
        String sql = "exec p_get_bom_items_and ?";
        Parameters p = new Parameters().add(1,image_urls);
        App.Current.DbPortal.ExecuteDataTableAsync("mgmt_ts_erp_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                Log.e("LZH4", "111111111111");
                if (value.HasError) {
                    Log.e("LZH5", image_urls);
                    App.Current.toastError(BomActivity.this, value.Error);
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

                view = View.inflate(BomActivity.this, R.layout.bom_line, null);


                purchaseViewHolder = new PurchaseViewHolder();
                purchaseViewHolder.textView1 = (TextView) view.findViewById(R.id.textview_1);
                purchaseViewHolder.textView2 = (TextView) view.findViewById(R.id.textview_2);
                purchaseViewHolder.textView3 = (TextView) view.findViewById(R.id.textview_3);



                view.setTag(purchaseViewHolder);
            } else {
                purchaseViewHolder = (PurchaseViewHolder) view.getTag();
            }



            purchaseViewHolder.textView1.setText(dataTable.Rows.get(i).getValue("PRD_NO", ""));
            purchaseViewHolder.textView2.setText(String.valueOf(dataTable.Rows.get(i).getValue("NAME", "")));
            purchaseViewHolder.textView3.setText(String.valueOf(dataTable.Rows.get(i).getValue("QTY", "")));


            return view;
        }
    }

    class PurchaseViewHolder {
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;

    }
}
