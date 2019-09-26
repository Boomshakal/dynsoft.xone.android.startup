package dynsoft.xone.android.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.xml.sax.DTDHandler;

import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;

/**
 * Created by Administrator on 2018/12/15.
 */

public class WIPAdapter extends BaseAdapter {
    private DataTable mDataTable;
    private Context mContext;

    public WIPAdapter(DataTable mDataTable, Context mContext) {
        this.mDataTable = mDataTable;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mDataTable.Rows.size();
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
        WIPViewHolder wipViewHolder;
        if (view == null) {
            wipViewHolder = new WIPViewHolder();
            view = View.inflate(mContext, R.layout.item_wip, null);
            wipViewHolder.textViewPMC = (TextView) view.findViewById(R.id.textview_pmc);
            wipViewHolder.textViewFirstTime = (TextView) view.findViewById(R.id.textview_first);
            wipViewHolder.textViewSecondTime = (TextView) view.findViewById(R.id.textview_second);
            wipViewHolder.textViewThirdTime = (TextView) view.findViewById(R.id.textview_third);
            wipViewHolder.textViewFourthTime = (TextView) view.findViewById(R.id.textview_fourth);
            wipViewHolder.textViewFifthTime = (TextView) view.findViewById(R.id.textview_fifth);
            wipViewHolder.textViewSixthTime = (TextView) view.findViewById(R.id.textview_sixth);
            wipViewHolder.textViewCount = (TextView) view.findViewById(R.id.textview_count);
            view.setTag(wipViewHolder);
        } else {
            wipViewHolder = (WIPViewHolder) view.getTag();
        }
        String name = mDataTable.Rows.get(i).getValue("name", "");
        Integer count1 = mDataTable.Rows.get(i).getValue("count1", 0);
        Integer count2 = mDataTable.Rows.get(i).getValue("count2", 0);
        Integer count3 = mDataTable.Rows.get(i).getValue("count3", 0);
        Integer count4 = mDataTable.Rows.get(i).getValue("count4", 0);
        Integer count5 = mDataTable.Rows.get(i).getValue("count5", 0);
        Integer count6 = mDataTable.Rows.get(i).getValue("count6", 0);
        Integer sumCount = mDataTable.Rows.get(i).getValue("sum_count", 0);
        wipViewHolder.textViewPMC.setText(name);
        wipViewHolder.textViewFirstTime.setText(String.valueOf(count1));
        wipViewHolder.textViewSecondTime.setText(String.valueOf(count2));
        wipViewHolder.textViewThirdTime.setText(String.valueOf(count3));
        wipViewHolder.textViewFourthTime.setText(String.valueOf(count4));
        wipViewHolder.textViewFifthTime.setText(String.valueOf(count5));
        wipViewHolder.textViewSixthTime.setText(String.valueOf(count6));
        wipViewHolder.textViewCount.setText(String.valueOf(sumCount));
        return view;
    }

    class WIPViewHolder {
        private TextView textViewPMC;
        private TextView textViewFirstTime;
        private TextView textViewSecondTime;
        private TextView textViewThirdTime;
        private TextView textViewFourthTime;
        private TextView textViewFifthTime;
        private TextView textViewSixthTime;
        private TextView textViewCount;
    }
}
