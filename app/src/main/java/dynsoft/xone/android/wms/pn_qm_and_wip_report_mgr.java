package dynsoft.xone.android.wms;

import android.content.Context;
import android.content.Intent;

import dynsoft.xone.android.activity.PatrolCardActivity;
import dynsoft.xone.android.activity.WIPActivity;
import dynsoft.xone.android.core.App;

/**
 * Created by Administrator on 2018/12/17.
 */

public class pn_qm_and_wip_report_mgr extends pn_mgr {
    public pn_qm_and_wip_report_mgr(Context context) {
        super(context);
        Intent intent = new Intent(getContext(), WIPActivity.class);
        App.Current.Workbench.startActivity(intent);
        close();
        int currentItem = App.Current.Workbench.ViewPager.getCurrentItem();
        App.Current.Workbench.ViewPager.removeViewAt(currentItem);
    }
}
