package dynsoft.xone.android.fragment.department;

import android.support.v4.app.Fragment;

import java.util.HashMap;

import dynsoft.xone.android.fragment.firstkanban.FirstCPKFragment;
import dynsoft.xone.android.fragment.firstkanban.FirstKanbanFragment;
import dynsoft.xone.android.fragment.locationfragment.LocationChartFragment;
import dynsoft.xone.android.fragment.locationfragment.LocationFragment;
import dynsoft.xone.android.fragment.secondkanban.EachNumberFragment;
import dynsoft.xone.android.fragment.secondkanban.SecondKanbanFragment;
import dynsoft.xone.android.fragment.thirdkanban.ThirdCPKFragment;
import dynsoft.xone.android.fragment.thirdkanban.ThirdKanbanFragment;

/**
 * Created by Administrator on 2018/4/10.
 */

public class FragmentFactory {
    public static HashMap<Integer, Fragment> mFragments = new HashMap<Integer, Fragment>();
    public static HashMap<Integer, Fragment> mSecondFragments = new HashMap<Integer, Fragment>();
    public static HashMap<Integer, Fragment> mThirdFragments = new HashMap<Integer, Fragment>();
    public static HashMap<Integer, Fragment> mThirdDepartmentFragments = new HashMap<Integer, Fragment>();

    public static Fragment getFragment(int position) {
        Fragment fragment = null;
        fragment = mFragments.get(position);
        if (fragment == null) {
            if (position == 0) {
                fragment = new WipFragment();
            } else {
                fragment = new OqcFragment();
            }
            mFragments.put(position, fragment);
        }
        return fragment;
    }

    public static Fragment getFirstFragment(int position) {
        Fragment fragment = null;
//        fragment = mSecondFragments.get(position);
//        if (fragment == null) {
        if (position == 0) {
            fragment = new FirstKanbanFragment();
        } else {
            fragment = new FirstCPKFragment();
        }
//        }
        return fragment;
    }

    public static Fragment getSecondFragment(int position) {
        Fragment fragment = null;
//        fragment = mSecondFragments.get(position);
//        if (fragment == null) {
        if (position == 0) {
            fragment = new SecondKanbanFragment();
        } else {
            fragment = new EachNumberFragment();
        }
//        }
        return fragment;
    }

    public static Fragment getThirdFragment(int position) {
        Fragment fragment = null;
//        fragment = mSecondFragments.get(position);
//        if (fragment == null) {
        if (position == 0) {
            fragment = new ThirdKanbanFragment();
        } else {
            fragment = new ThirdCPKFragment();
        }
//        }
        mThirdFragments.put(position, fragment);
        return fragment;
    }

    public static Fragment getThirdDepartmentFragment(int position) {
        Fragment fragment = null;
//        fragment = mSecondFragments.get(position);
//        if (fragment == null) {
        if (position == 0) {
            fragment = new ThirdUphFragment();
        } else if (position == 1) {
            fragment = new ThirdWipFragment();
        } else {
            fragment = new ThirdOqcFragment();
        }
//        }
        mThirdDepartmentFragments.put(position, fragment);
        return fragment;
    }

    public static Fragment getLocationFragment(int position) {
        Fragment fragment = null;
//        fragment = mSecondFragments.get(position);
//        if (fragment == null) {
        if (position == 0) {
            fragment = new LocationFragment();
        } else {
            fragment = new LocationChartFragment();
        }
//        }
        return fragment;
    }
}
