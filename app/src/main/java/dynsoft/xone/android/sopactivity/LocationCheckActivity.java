package dynsoft.xone.android.sopactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dynsoft.xone.android.activity.FirstKanbanActivity;
import dynsoft.xone.android.adapter.GridviewAdapter;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.fragment.department.FragmentFactory;
import oracle.jdbc.driver.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;


/**
 * Created by Administrator on 2018/5/8.
 */

public class LocationCheckActivity extends FragmentActivity {
    private ViewPager viewPager;
    private ProductionAdapter adapter;
    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            int currentItem = viewPager.getCurrentItem();
//            if(currentItem == 2) {
//                viewPager.setCurrentItem(0);
//            } else {
//                viewPager.setCurrentItem(currentItem + 1);
//            }
            adapter.notifyDataSetChanged();
            handler.postDelayed(runnable, 10 * 60 * 1000);
        }
    };
//    private Runnable runnable2 = new Runnable() {
//        @Override
//        public void run() {
//            int currentItem = viewPager.getCurrentItem();
//            if(currentItem == 1) {
//                viewPager.setCurrentItem(0);
//            } else {
//                viewPager.setCurrentItem(currentItem + 1);
//            }
//            handler.postDelayed(runnable2, 20 * 60 * 1000);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productionkanban_fragment);
        handler = new Handler();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ProductionAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        handler.post(runnable);
//        toastChooseCPK();
    }

//    private void toastChooseCPK() {
//        PopupWindow popupWindow = new PopupWindow();
//        View view = View.inflate(ThirdKanbanActivity.this, R.layout.item_toast_choose_cpk, null);
//        ButtonTextCell buttonTextCell1 = (ButtonTextCell) view.findViewById(R.id.button_text_cell_1);
//        ButtonTextCell buttonTextCell2 = (ButtonTextCell) view.findViewById(R.id.button_text_cell_2);
//        ButtonTextCell buttonTextCell3 = (ButtonTextCell) view.findViewById(R.id.button_text_cell_3);
//        TextView confirm = (TextView) view.findViewById(R.id.confirm);
//        TextView cancel = (TextView) view.findViewById(R.id.cancel);
//        buttonTextCell1.Button.setOnClickListener(this);
//        buttonTextCell2.Button.setOnClickListener(this);
//        buttonTextCell3.Button.setOnClickListener(this);
//        confirm.setOnClickListener(this);
//        cancel.setOnClickListener(this);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }


    class ProductionAdapter extends FragmentStatePagerAdapter {
        private Fragment fragment;

        public ProductionAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            fragment = FragmentFactory.getLocationFragment(position);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

}
