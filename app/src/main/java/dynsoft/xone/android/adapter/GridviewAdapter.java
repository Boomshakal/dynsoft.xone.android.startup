package dynsoft.xone.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import dynsoft.xone.android.core.R;
import dynsoft.xone.android.sopactivity.LocationCheckActivity;

/**
 * Created by Administrator on 2018/7/2.
 */

public class GridviewAdapter extends BaseAdapter {
    private String[] titles;
    private ArrayList<String> content;
    private Context context;

    public GridviewAdapter(String[] titles, ArrayList<String> content, Context context) {
        this.titles = titles;
        this.content = content;
        this.context = context;
    }

    @Override
    public int getCount() {
        return titles.length;
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
        LocationGridviewViewHolder locationGridviewViewHolder;
        if (view == null) {
            view = View.inflate(context, R.layout.gridview_location, null);
            locationGridviewViewHolder = new LocationGridviewViewHolder();
            locationGridviewViewHolder.textView1 = (TextView) view.findViewById(R.id.textview_gridview_location1);
            locationGridviewViewHolder.textView2 = (TextView) view.findViewById(R.id.textview_gridview_location2);
            view.setTag(locationGridviewViewHolder);
        } else {
            locationGridviewViewHolder = (LocationGridviewViewHolder) view.getTag();
        }
        locationGridviewViewHolder.textView1.setText(titles[i]);
        locationGridviewViewHolder.textView2.setText(content.get(i));
        return view;
    }

    class LocationGridviewViewHolder {
        private TextView textView1;
        private TextView textView2;
    }
}

