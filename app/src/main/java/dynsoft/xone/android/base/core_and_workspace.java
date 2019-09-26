package dynsoft.xone.android.base;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Workbench;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class core_and_workspace extends BasePane {

    public core_and_workspace(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	protected ScrollView _scroll;
    protected LinearLayout _body;
    protected ImageButton _exit;
    protected ImageButton _info;
    
    @Override
    public void onPrepared() {
        
        super.onPrepared();
        
        _scroll = (ScrollView)this.Elements.get("scroll").Object;
        _body = (LinearLayout)this.Elements.get("body").Object;
        _exit = (ImageButton)this.Elements.get("exit").Object;
        _info = (ImageButton)this.Elements.get("info").Object;
        
        if (_scroll != null) {
            LinearLayout.LayoutParams lp_scroll = new LinearLayout.LayoutParams(-1,-2);
            lp_scroll.weight = 1;
            _scroll.setLayoutParams(lp_scroll);
            _scroll.setBackgroundColor(Color.WHITE);
        }
        
        if (_body != null) {
            _body.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp_module = new LinearLayout.LayoutParams(-1,-2);
            int count = _body.getChildCount();
            for (int i=0; i<count; i++) {
                View child = _body.getChildAt(i);
                child.setLayoutParams(lp_module);
            }
        }
        
        if (_exit != null) {
            _exit.setImageBitmap(App.Current.ResourceManager.getImage("core_exit_white"));
            _exit.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                	((Workbench)App.Current.Workbench).exit();
                }
            });
        }
        
        if (_info != null) {
            _info.setImageBitmap(App.Current.ResourceManager.getImage("core_info_white"));
            _info.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    
                }
            });
        }
    }
}
