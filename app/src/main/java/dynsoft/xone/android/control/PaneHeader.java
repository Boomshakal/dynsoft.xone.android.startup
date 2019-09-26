package dynsoft.xone.android.control;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.core.Workbench;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class PaneHeader extends BorderLayout {
    
    public static int HeaderHeight = App.dpToPx(48);
    public static int HeaderBackgroundColor = Color.parseColor("#4A87EE");
    public static int HeaderBorderColor = Color.parseColor("#2F63B8");
    
    //public static int HeaderBackgroundColor = Color.parseColor("#3A3A3A");
    //public static int HeaderBorderColor = Color.parseColor("#383838");
    
    public static int IconWidth = App.dpToPx(32);
    public static int IconHeight = App.dpToPx(32);
    public static int IconLeftMargin = App.dpToPx(10);
    public static int TitleTextSize = 20;
    public static int TitleTextColor = Color.WHITE;
    public static int TitleMargin = App.dpToPx(10);
    public static int MenuWidth = App.dpToPx(32);
    public static int MenuHeight = App.dpToPx(32);
    public static int MenuRightMargin = App.dpToPx(10);
    
    public Pane Pane;
    public ImageView Icon;
    public TextView Title;
    public ImageButton Menu;

    public PaneHeader(Context context) {
        super(context);
        this.initLayout();
    }

    public PaneHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initLayout();
    }

    public PaneHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initLayout();
    }
    
    public void initLayout()
    {
        this.setBackgroundColor(HeaderBackgroundColor);
        this.setBorderColor(HeaderBorderColor);
        this.setBorderThickness(0, 0, 0, 1);
        
        this.Icon = new ImageView(this.getContext());
        this.Title = new TextView(this.getContext());
        this.Menu = new ImageButton(this.getContext());
        
        LinearLayout.LayoutParams lp_icon = new LinearLayout.LayoutParams(-2, -2);
        lp_icon.height = IconWidth;
        lp_icon.width = IconHeight;
        lp_icon.gravity = Gravity.CENTER_VERTICAL;
        lp_icon.leftMargin = IconLeftMargin;
        this.Icon.setLayoutParams(lp_icon);
        this.Icon.setScaleType(ScaleType.FIT_CENTER);
        
        LinearLayout.LayoutParams lp_title = new LinearLayout.LayoutParams(-2, -2);
        lp_title.height = MenuWidth;
        lp_title.width = MenuHeight;
        lp_title.weight = 1;
        lp_title.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        lp_title.leftMargin = TitleMargin;
        lp_title.rightMargin = TitleMargin;
        this.Title.setLayoutParams(lp_title);
        this.Title.setTextSize(TitleTextSize);
        this.Title.setTextColor(TitleTextColor);
        this.Title.setSingleLine(true);
        //this.Title.setGravity(Gravity.CENTER);
        this.Title.setEllipsize(TruncateAt.END);
        
        LinearLayout.LayoutParams lp_menu = new LinearLayout.LayoutParams(-2, -2);
        lp_menu.height = MenuWidth;
        lp_menu.width = MenuHeight;
        lp_menu.gravity = Gravity.CENTER_VERTICAL;
        lp_menu.rightMargin = MenuRightMargin;
        this.Menu.setLayoutParams(lp_menu);
        this.Menu.setBackgroundDrawable(null);
        this.Menu.setPadding(0, 0, 0, 0);
        this.Menu.setScaleType(ScaleType.FIT_CENTER);
        this.Menu.setBackgroundColor(Color.TRANSPARENT);
        
        this.Menu.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
            	((Workbench)App.Current.Workbench).showMenuDialog();
            }
        });
        
        this.addView(this.Icon);
        this.addView(this.Title);
        this.addView(this.Menu);
    }
    
    public void setIconImage(Bitmap bitmap)
    {
        this.Icon.setImageBitmap(bitmap);
    }

    public void setTitleText(String title)
    {
        this.Title.setText(title);
    }
    
    public void setMenuImage(Bitmap bitmap)
    {
        this.Menu.setImageBitmap(bitmap);
    }
}
