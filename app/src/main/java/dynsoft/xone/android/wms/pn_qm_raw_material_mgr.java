package dynsoft.xone.android.wms;

import android.content.Context;
import android.content.Intent;

import dynsoft.xone.android.activity.PurchaseExamKanbanActivity;
import dynsoft.xone.android.activity.RawMaterialKanbanActivity;
import dynsoft.xone.android.core.App;

/**
 * Created by Administrator on 2018/6/6.
 */

public class pn_qm_raw_material_mgr extends pn_mgr {
    public pn_qm_raw_material_mgr(Context context) {
        super(context);
        Intent intent = new Intent(getContext(), RawMaterialKanbanActivity.class);
        App.Current.Workbench.startActivity(intent);
        close();
        int currentItem = App.Current.Workbench.ViewPager.getCurrentItem();
        App.Current.Workbench.ViewPager.removeViewAt(currentItem);
    }
}
