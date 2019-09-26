package dynsoft.xone.android.start;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import dynsoft.xone.android.base.BasePane;
import dynsoft.xone.android.control.PaneFooter;
import dynsoft.xone.android.control.PaneHeader;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.core.Workbench;

public class pn_base extends Pane {

	public pn_base(Context context) {
		super(context);
	}

	public PaneHeader Header;
    public PaneFooter Footer;
    public ImageButton BackButton;
    public ProgressDialog ProgressDialog;
    
    public void setContentView()
	{
	}
    
    @Override
    public void onPrepared() {

        this.setContentView();
    	
    	this.ProgressDialog = new ProgressDialog(this.getContext());
        this.ProgressDialog.setMessage("ÕýÔÚ¼ÓÔØ...");
        this.ProgressDialog.setCancelable(false);
        
        this.Header = (PaneHeader)this.findViewById(R.id.header);
        this.Footer = (PaneFooter)this.findViewById(R.id.footer);
        this.BackButton = (ImageButton)this.findViewById(R.id.btn_back);
        
        if (this.Header != null) {
            this.Header.Pane = this;
            
            LinearLayout.LayoutParams lp_header = new LinearLayout.LayoutParams(-1,-2);
            lp_header.height = PaneHeader.HeaderHeight;
            this.Header.setLayoutParams(lp_header);
            this.Header.setIconImage(App.Current.ResourceManager.getImage("@/core_arrow_left_white"));
            this.Header.setMenuImage(App.Current.ResourceManager.getImage("@/core_menu_list_white"));
            this.Header.setTitleText(App.Current.ResourceManager.getString(this.Title));
            this.Header.Icon.setClickable(true);
            this.Header.Icon.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_base.this.close();
				}
            });
        }

        if (this.Footer != null) {
            LinearLayout.LayoutParams lp_footer = new LinearLayout.LayoutParams(-1,-2);
            lp_footer.height = PaneFooter.FooterHeight;
            this.Footer.setLayoutParams(lp_footer);
        }
        
        if (this.BackButton != null){
			this.BackButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_exit_white"));
			this.BackButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_base.this.close();
				}
			});
		}
    }
    
    public void close()
    {
    	((Workbench)App.Current.Workbench).closePane(pn_base.this);
    }
}
