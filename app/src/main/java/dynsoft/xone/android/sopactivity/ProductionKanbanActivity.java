package dynsoft.xone.android.sopactivity;

import java.util.ArrayList;

import dynsoft.xone.android.core.R;
import dynsoft.xone.android.fragment.secondkanban.EachNumberFragment;
import dynsoft.xone.android.fragment.secondkanban.SecondKanbanFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;


/**
 * Created by Administrator on 2018/1/26.
 */

public class ProductionKanbanActivity extends FragmentActivity {
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productionkanban_fragment);
        fragments = new ArrayList<Fragment>();
        if(fragments.size() < 1) {
        	SecondKanbanFragment second = new SecondKanbanFragment();
        	EachNumberFragment each = new EachNumberFragment();
        	fragments.add(second);
        	fragments.add(each);
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ProductionAdapter adapter = new ProductionAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    class ProductionAdapter extends FragmentStatePagerAdapter {
        private Fragment fragment;

        public ProductionAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            fragment = fragments.get(position);
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
