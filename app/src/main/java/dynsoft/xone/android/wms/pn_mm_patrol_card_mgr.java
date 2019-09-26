package dynsoft.xone.android.wms;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import dynsoft.xone.android.activity.PatrolCardActivity;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.data.DataSet;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/11/16.
 */

public class pn_mm_patrol_card_mgr extends pn_mgr{
    public pn_mm_patrol_card_mgr(Context context) {
        super(context);
        Intent intent = new Intent(getContext(), PatrolCardActivity.class);
        App.Current.Workbench.startActivity(intent);
        close();
        int currentItem = App.Current.Workbench.ViewPager.getCurrentItem();
        App.Current.Workbench.ViewPager.removeViewAt(currentItem);
    }
}
