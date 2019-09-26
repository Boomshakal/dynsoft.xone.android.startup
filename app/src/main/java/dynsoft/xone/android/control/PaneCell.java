package dynsoft.xone.android.control;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import dynsoft.xone.android.core.App;

public class PaneCell extends BorderLayout {

    public static int BorderColor = Color.rgb(0xC0, 0xC0, 0xC0);
    public static int LeftPadding = App.dpToPx(0);
    public static int TopPadding = App.dpToPx(8);
    public static int RightPadding = App.dpToPx(10);
    public static int BottomPadding = App.dpToPx(8);
    
    public Context Context;
    
    public PaneCell(Context context) {
        super(context);
        //this.setId(App.generateViewId());
        Context = context;
        this.initLayout();
    }

    public PaneCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        //this.setId(App.generateViewId());
        Context = context;
        this.initLayout();
    }
    
    public PaneCell(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //this.setId(App.generateViewId());
        Context = context;
        this.initLayout();
    }
    
    protected void initLayout()
    {
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setBorderColor(BorderColor);
        this.setBorderThickness(0, 0, 0, 1);
        this.setPadding(LeftPadding, TopPadding, RightPadding, BottomPadding);
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        
    }
}
