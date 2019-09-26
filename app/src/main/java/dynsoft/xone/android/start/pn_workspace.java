package dynsoft.xone.android.start;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.ImageView.ScaleType;
import dynsoft.xone.android.base.BasePane;
import dynsoft.xone.android.control.PaneFooter;
import dynsoft.xone.android.control.PaneHeader;
import dynsoft.xone.android.control.TreeViewItem;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.core.Workbench;

public class pn_workspace extends pn_base {

	public pn_workspace(Context context) {
		super(context);
	}

	public LinearLayout Root;
    public ImageButton CloseButton;
    public ImageButton RefreshButton;
    public ImageButton InfoButton;
    public ScrollView Scroll;
    public LinearLayout Body;
    
	@Override
    public void onPrepared() {
        super.onPrepared();
        
        this.Root = (LinearLayout)this.Elements.get("root").Object;
        this.Scroll = (ScrollView)this.Elements.get("scroll").Object;
        this.Header = (PaneHeader)this.Elements.get("header").Object;
        this.Footer = (PaneFooter)this.Elements.get("footer").Object;
        this.Body = (LinearLayout)this.Elements.get("body").Object;
        this.CloseButton = (ImageButton)this.Elements.get("close").Object;
        this.RefreshButton = (ImageButton)this.Elements.get("refresh").Object;
        this.InfoButton = (ImageButton)this.Elements.get("info").Object;
        
        if (this.Root != null) {
            LinearLayout.LayoutParams lp_root = new LinearLayout.LayoutParams(-1,-1);
            this.Root.setLayoutParams(lp_root);
            this.Root.setOrientation(LinearLayout.VERTICAL);
            this.Root.setBackgroundResource(R.drawable.bg_pane);
        }
        
        if (this.Header != null) {
            this.Header.Pane = this;
            
            LinearLayout.LayoutParams lp_header = new LinearLayout.LayoutParams(-1,-2);
            lp_header.height = PaneHeader.HeaderHeight;
            this.Header.setLayoutParams(lp_header);
            this.Header.setIconImage(App.Current.ResourceManager.getImage(this.Icon));
            this.Header.setMenuImage(App.Current.ResourceManager.getImage("@/core_menu_list_white"));
            this.Header.setTitleText(App.Current.ResourceManager.getString(App.Current.BookName + " - " + App.Current.UserName));
            this.Header.Icon.setClickable(true);
            this.Header.Icon.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_workspace.this.info();
				}
            });
        }

        if (Footer != null) {
            LinearLayout.LayoutParams lp_footer = new LinearLayout.LayoutParams(-1,-2);
            lp_footer.height = PaneFooter.FooterHeight;
            Footer.setLayoutParams(lp_footer);
            //Footer.setBackgroundResource(R.drawable.bg_footer);
            
            int childCount = Footer.getChildCount();
            if (childCount > 0) {
                LinearLayout.LayoutParams lp_cmd = new LinearLayout.LayoutParams(-2,-2);
                lp_cmd.gravity = Gravity.CENTER_VERTICAL;
                lp_cmd.weight = 1;
                lp_cmd.height = PaneFooter.ButtonHeight;
                lp_cmd.width = PaneFooter.ButtonWidth;
                
                for (int i=0; i<childCount;i++) {
                    ImageButton cmd = (ImageButton)Footer.getChildAt(i);
                    if (cmd != null) {
                        cmd.setLayoutParams(lp_cmd);
                        cmd.setBackgroundDrawable(null);
                        cmd.setScaleType(ScaleType.FIT_CENTER);
                        cmd.setPadding(0, 0, 0, 0);
                    }
                }
            }
        }
        
        if (this.CloseButton != null) {
            this.CloseButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_exit_white"));
            this.CloseButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                	pn_workspace.this.close();
                }
            });
        }
        
        if (this.RefreshButton != null) {
            this.RefreshButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_refresh_white"));
            this.RefreshButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    pn_workspace.this.refresh();
                }
            });
        }
        
        if (this.InfoButton != null) {
            this.InfoButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_info_white"));
            this.InfoButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    pn_workspace.this.info();
                }
            });
        }
        
        if (this.Scroll != null){
        	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1);
        	lp.weight = 1;
        	this.Scroll.setLayoutParams(lp);
        	this.Scroll.setBackgroundColor(Color.WHITE);
        }
        
        if (this.Body != null){
        	this.Body.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp_module = new LinearLayout.LayoutParams(-1,-2);
            int count = this.Body.getChildCount();
            for (int i=0; i<count; i++) {
                View child = this.Body.getChildAt(i);
                child.setLayoutParams(lp_module);
            }
        }
        
        this.refresh();
	}
	
	public void close()
	{
		((Workbench)App.Current.Workbench).exit();
	}
	
	public void refresh()
	{
		if (this.Body != null){
            int count = this.Body.getChildCount();
            for (int i=0; i<count; i++) {
            	TreeViewItem item = (TreeViewItem)this.Body.getChildAt(i);
            	if (item != null) {
            		item.refresh();
            	}
            }
        }
	}
	
	public void info()
	{
		try {
			PackageInfo packInfo = this.getContext().getPackageManager().getPackageInfo(this.getContext().getPackageName(), 0);
			String info = "MEGMEET移动企业管理平台\n";
			info += "版本号: v" + packInfo.versionName + "\n";
			info += "环境: " + App.Current.BookName + "\n";
			info += "用户: " + App.Current.UserCode + "(" + App.Current.UserName + ")\n";
			info += "服务器: " + App.Current.Server.Address + "\n";
			
			App.Current.showInfo(this.getContext(), info);
		} catch (NameNotFoundException e) {
		}
	}
}
