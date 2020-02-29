package dynsoft.xone.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.ListView;

import dynsoft.xone.android.adapter.FirstItemAdapter;
import dynsoft.xone.android.adapter.FirstItemNewAdapter;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

public class FirstItemNewActivity extends Activity {
    private ListView listViewTop;
    private ListView listViewSecond;
    private String check_code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_item);
        listViewTop = (ListView) findViewById(R.id.listview_1);
        listViewSecond = (ListView) findViewById(R.id.listview_2);
        Intent intent = getIntent();
        check_code = intent.getStringExtra("check_code");
        loadItems();
    }

    private void loadItems() {
        String sql = "exec fm_load_first_item_by_check_code ?";
        Parameters p = new Parameters().add(1, check_code);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(FirstItemNewActivity.this, value.Error);
                } else if (value.Value != null && value.Value.Rows.size() > 0) {
                    FirstItemNewAdapter firstItemAdapter = new FirstItemNewAdapter(value.Value, FirstItemNewActivity.this);
                    listViewSecond.setAdapter(firstItemAdapter);
                }
            }
        });
    }
}
