package dynsoft.xone.android.control;

import dynsoft.xone.android.core.App;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

public class ToolBarCell extends PaneCell {
    
    public static int ToolBarBackground = Color.parseColor("#F8F8F8");
    public static int ToolBarButtonWidth = App.dpToPx(32);
    public static int ToolBarButtonHeight = App.dpToPx(32);

    public ToolBarCell(Context context) {
        super(context);
    }

    public ToolBarCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolBarCell(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    public void initLayout()
    {
        super.initLayout();
        
        //this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(ToolBarBackground);
    }
    
    public void initButtonLayout()
    {
        int count = this.getChildCount();
        if (count > 0) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2,-2);
            lp.weight = ToolBarButtonWidth;
            lp.height = ToolBarButtonHeight;
            lp.weight = 1;
            
            for (int i=0; i<count; i++) {
                View view = this.getChildAt(i);
                view.setLayoutParams(lp);
            }
        }
    }
}
