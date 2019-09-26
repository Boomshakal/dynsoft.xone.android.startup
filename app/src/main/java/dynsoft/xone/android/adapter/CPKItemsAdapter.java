package dynsoft.xone.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.PriorityQueue;

import dynsoft.xone.android.bean.CPKBean;
import dynsoft.xone.android.core.R;

/**
 * Created by Administrator on 2018/8/15.
 */

public class CPKItemsAdapter extends BaseAdapter {
    private String[] secondTitles = {"", "∑X = ", "∑R = ", " ", " ", " "
            , "群组数大小", " ", "X = ", "R = ", " ", " ", " ", "Std.DEV.=", "Sigma ="
            , "P P K =", "PP =", "Ca =", "C P K =", "CP =", "Grade ="};
    private ArrayList<CPKBean> firstType;
    private Context context;

    public CPKItemsAdapter(ArrayList<CPKBean> firstType, Context context) {
        this.firstType = firstType;
        this.context = context;
    }

    @Override
    public int getCount() {
        return 21;
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
        CPKBean cpkBean = firstType.get(i);
        int cpktype = cpkBean.getCPKTYPE();
        switch (cpktype) {
            case 0:
                FirstViewHolder firstViewHolder;
                if (view == null) {
                    view = View.inflate(context, R.layout.item_cpk_first, null);
                    firstViewHolder = new FirstViewHolder();
                    firstViewHolder.textView = (TextView) view.findViewById(R.id.textview);
                    view.setTag(firstViewHolder);
                } else {
                    firstViewHolder = (FirstViewHolder) view.getTag();
                }
                firstViewHolder.textView.setText(firstType.get(i).getContent());
                if (firstType.get(i).getContent().contains("红色")) {
                    firstViewHolder.textView.setTextColor(context.getResources().getColor(R.color.red));
                } else {
                    firstViewHolder.textView.setTextColor(context.getResources().getColor(R.color.black));
                }
                break;
            case 1:
                SecondViewHolder secondViewHolder;
                if (view == null) {
                    view = View.inflate(context, R.layout.item_cpk_second, null);
                    secondViewHolder = new SecondViewHolder();
                    secondViewHolder.textView = (TextView) view.findViewById(R.id.textview_title);
                    secondViewHolder.textViewCOntent = (TextView) view.findViewById(R.id.textview_content);
                    view.setTag(secondViewHolder);
                } else {
                    secondViewHolder = (SecondViewHolder) view.getTag();
                }
                secondViewHolder.textView.setText(secondTitles[i]);
                secondViewHolder.textViewCOntent.setText(firstType.get(i).getContent());
                secondViewHolder.textView.setTextColor( context.getResources().getColor(R.color.black));
                secondViewHolder.textViewCOntent.setTextColor( context.getResources().getColor(R.color.black));
                break;
        }
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return firstType.get(position).getCPKTYPE();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    class FirstViewHolder {
        private TextView textView;
    }

    class SecondViewHolder {
        private TextView textView;
        private TextView textViewCOntent;
    }
}
