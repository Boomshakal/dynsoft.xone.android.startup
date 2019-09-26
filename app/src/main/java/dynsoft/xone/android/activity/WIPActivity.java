package dynsoft.xone.android.activity;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import dynsoft.xone.android.adapter.WIPAdapter;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/12/15.
 */

public class WIPActivity extends Activity {
    private static int UPDATETIME = 10 * 60 * 1000;
    private static final String FONT_DIGITAL_7 = "fonts" + File.separator + "Digital-7Mono.TTF";
    private ListView listView;
    private ProgressBar progressBar;
    private TextView textViewDate;
    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setListViewDatas();
            handler.postDelayed(runnable, UPDATETIME);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wip);
        listView = (ListView) findViewById(R.id.listview);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        textViewDate = (TextView) findViewById(R.id.textview_date);
        setTextViewDate();
        handler = new Handler();
        handler.post(runnable);
    }

    private void setTextViewDate() {
        AssetManager assets = WIPActivity.this.getAssets();
        Typeface fromAsset = Typeface.createFromAsset(assets, FONT_DIGITAL_7);
        textViewDate.setTypeface(fromAsset);
        String curDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        textViewDate.setText(curDate);
    }

    private void setListViewDatas() {
        progressBar.setVisibility(View.VISIBLE);
        String sql = "exec fm_get_wip_data_group_by_pmc_and";
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                progressBar.setVisibility(View.GONE);
                Result<DataTable> value = Value;
                if (value.HasError) {

                } else {
                    if (value.Value != null && value.Value.Rows.size() > 0) {
                        WIPAdapter wipAdapter = new WIPAdapter(value.Value, WIPActivity.this);
                        listView.setAdapter(wipAdapter);
                    }
                }
            }
        });
    }
}
