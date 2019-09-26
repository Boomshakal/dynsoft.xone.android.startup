package dynsoft.xone.android.wms;

import android.content.Context;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import dynsoft.xone.android.adapter.PageTableAdapter;
import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.start.pn_base;

public class pn_editor extends pn_base {

	public pn_editor(Context context) {
		super(context);
	}
	
	public ImageButton CommitButton;
	
	@Override
    public void onPrepared() {
		super.onPrepared();
		
		this.CommitButton = (ImageButton)this.findViewById(R.id.btn_commit);
		if (this.CommitButton != null){
			this.CommitButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_white"));
			this.CommitButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pn_editor.this.commit();
				}
			});
		}
	}
	
	public void commit()
	{	
	}
}
