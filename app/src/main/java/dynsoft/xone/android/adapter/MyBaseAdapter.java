package dynsoft.xone.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import dynsoft.xone.android.data.DataTable;

public abstract class MyBaseAdapter extends BaseAdapter {
    private Context context;
    private DataTable dataTable;

    public MyBaseAdapter(Context context, DataTable dataTable) {
        this.context = context;
        this.dataTable = dataTable;
    }

    @Override
    public int getCount() {
        if (dataTable != null) {
            if (dataTable.Rows != null && dataTable.Rows.size() > 0) {
                return dataTable.Rows.size();
            }
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return setAdapterView(i, view, viewGroup, context, dataTable);
    }

    public abstract View setAdapterView(int position, View view, ViewGroup viewGroup, Context context, DataTable dataTable);
}
