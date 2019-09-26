package dynsoft.xone.android.helper;

import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.core.R;
import android.view.View;

public class PaneHelper {

    public static Pane GetPane(View view)
    {
        return (Pane)view.getTag(R.id.pane);
    }
}
