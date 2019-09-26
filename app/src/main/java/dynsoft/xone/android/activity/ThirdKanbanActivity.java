package dynsoft.xone.android.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.fragment.department.FragmentFactory;
import dynsoft.xone.android.fragment.thirdkanban.ThirdKanbanFragment;

/**
 * Created by Administrator on 2018/1/26.
 */

public class ThirdKanbanActivity extends FragmentActivity {
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
    private Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            int currentItem = viewPager.getCurrentItem();
            if(currentItem == 1) {
                viewPager.setCurrentItem(0);
            } else {
                viewPager.setCurrentItem(currentItem + 1);
            }
            handler.postDelayed(runnable2, 2 * 60 * 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productionkanban_fragment);
        handler = new Handler();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ProductionAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        handler.post(runnable);
        handler.post(runnable2);
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
            fragment = FragmentFactory.getThirdFragment(position);
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
