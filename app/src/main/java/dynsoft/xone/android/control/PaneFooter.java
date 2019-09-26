package dynsoft.xone.android.control;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import dynsoft.xone.android.core.App;

public class PaneFooter extends BorderLayout {
    
    public static int ButtonHeight = App.dpToPx(42);
    public static int ButtonWidth = App.dpToPx(42);

    public PaneFooter(Context context) {
        super(context);
        this.initLayout();
    }
    
    public PaneFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initLayout();
    }
    
    public PaneFooter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initLayout();
    }
    
    public static int FooterHeight = App.dpToPx(48);
    public static int FooterBackgroundColor = Color.parseColor("#4A87EE");
    public static int FooterBorderColor = Color.parseColor("#2F63B8");
    
    //public static int FooterBackgroundColor = Color.parseColor("#3A3A3A");
    //public static int FooterBorderColor = Color.parseColor("#383838");
    
    public void initLayout()
    {
        this.setBackgroundColor(FooterBackgroundColor);
        this.setBorderColor(FooterBorderColor);
        this.setBorderThickness(0, 1, 0, 0);
    }
}
