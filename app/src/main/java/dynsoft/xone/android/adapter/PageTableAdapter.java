package dynsoft.xone.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import dynsoft.xone.android.core.Element;
import dynsoft.xone.android.core.IChild;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;


public abstract class PageTableAdapter extends TableAdapter
{
	public int PageSize;
	public int PageIndex;
	public View Footer;
	public TextView FooterText;

    public PageTableAdapter(Context context)
    {
        super(context);
        this.Context = context;
        this.Footer = LayoutInflater.from(context).inflate(R.layout.load_footer, null);
        this.FooterText = (TextView)this.Footer.findViewById(R.id.txt_footer);
    }
	
	@Override
	public int getCount() {
		if (this.DataTable != null) {
			return this.DataTable.Rows.size() + 1;
		}
		
		return 0;
	}

	@Override
	public Object getItem(int index) {
		if (this.DataTable != null) {
		    if (index < this.DataTable.Rows.size()) {
		        return this.DataTable.Rows.get(index);
		    } else {
		        return this.Footer;
		    }
		}
		
		return null;
	}
}
