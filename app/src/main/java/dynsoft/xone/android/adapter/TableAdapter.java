package dynsoft.xone.android.adapter;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import dynsoft.xone.android.core.Element;
import dynsoft.xone.android.core.IChild;
import dynsoft.xone.android.data.DataTable;

public abstract class TableAdapter extends BaseAdapter implements IChild {

    public DataTable DataTable;
    public Context Context;

    public TableAdapter(Context context)
    {
        super();
        this.Context = context;
    }
    
    @Override
    public int getCount() {
        if (this.DataTable != null) {
            return this.DataTable.Rows.size();
        }
        
        return 0;
    }

    @Override
    public Object getItem(int index) {
        if (this.DataTable != null) {
            return this.DataTable.Rows.get(index);
        }
        
        return null;
    }

    @Override
    public long getItemId(int index) {
        return index;
    }
    
    @Override
    public void attachToParent(Element parent) {
        AdapterView view = (AdapterView)parent.Object;
        if (view != null) {
            view.setAdapter(this);
        }
    }
}
