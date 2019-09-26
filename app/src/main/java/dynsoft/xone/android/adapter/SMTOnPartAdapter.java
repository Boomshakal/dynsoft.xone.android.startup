package dynsoft.xone.android.adapter;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.BigDecimal;
import java.util.ArrayList;

import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;

/**
 * Created by Administrator on 2018/12/13.
 */

public class SMTOnPartAdapter extends BaseAdapter {
    private DataTable mDataTable;
    private Context mContext;

    public SMTOnPartAdapter(DataTable dataTable, Context context) {
        this.mDataTable = dataTable;
        this.mContext = context;
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
        SMTOnPartViewHolder smtOnPartViewHolder;
        if (view == null) {
            view = View.inflate(mContext, R.layout.item_smt_onpart, null);
            smtOnPartViewHolder = new SMTOnPartViewHolder();
            smtOnPartViewHolder.textViewNum = (TextView) view.findViewById(R.id.textview_num);
            smtOnPartViewHolder.textViewItemname = (TextView) view.findViewById(R.id.textview_itemname);
            smtOnPartViewHolder.textViewMachine = (TextView) view.findViewById(R.id.textview_machine);
            smtOnPartViewHolder.textViewItemcode = (TextView) view.findViewById(R.id.textview_itemcode);
            smtOnPartViewHolder.textViewUseeach = (TextView) view.findViewById(R.id.textview_useeach);
            smtOnPartViewHolder.textViewOnlinecount = (TextView) view.findViewById(R.id.textview_online_count);
            smtOnPartViewHolder.textViewUsecount = (TextView) view.findViewById(R.id.textview_usecount);
            smtOnPartViewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout);
            smtOnPartViewHolder.imageView1 = (ImageView) view.findViewById(R.id.imageview_1);
            smtOnPartViewHolder.imageView2 = (ImageView) view.findViewById(R.id.imageview_2);
            smtOnPartViewHolder.textViewPer = (TextView) view.findViewById(R.id.textview_per);
//            smtOnPartViewHolder.piechart = (PieChart) view.findViewById(R.id.piechart);
            view.setTag(smtOnPartViewHolder);
        } else {
            smtOnPartViewHolder = (SMTOnPartViewHolder) view.getTag();
        }
        String item_name = mDataTable.Rows.get(i).getValue("item_name", "");     //机型
        String machine = mDataTable.Rows.get(i).getValue("machine", "");         //机台
        String item_code = mDataTable.Rows.get(i).getValue("item_code", "");      //料号
        float quantity = mDataTable.Rows.get(i).getValue("quantity", new BigDecimal(0)).floatValue();         //用量
        float quantity1 = mDataTable.Rows.get(i).getValue("quantity1", new BigDecimal(0)).floatValue();         //上料数
        int quantity2 = mDataTable.Rows.get(i).getValue("quantity2", 0);         //消耗数
        smtOnPartViewHolder.textViewNum.setText(String.valueOf(i + 1));
        smtOnPartViewHolder.textViewItemname.setText(item_name);
        smtOnPartViewHolder.textViewMachine.setText(machine);
        smtOnPartViewHolder.textViewItemcode.setText(item_code);
        smtOnPartViewHolder.textViewUseeach.setText(String.valueOf(quantity));
        smtOnPartViewHolder.textViewOnlinecount.setText(String.valueOf(quantity1));
        smtOnPartViewHolder.textViewUsecount.setText(String.valueOf(quantity2));
//        setBarchart(smtOnPartViewHolder.piechart, quantity1, quantity2);    //设置饼状图， 上料数和消耗数
//        ImageView imageView1 = new ImageView(mContext);
//        imageView1.setImageResource(R.color.red);
        if(quantity2 > quantity1) {            //消耗数大于上料数
            smtOnPartViewHolder.textViewPer.setText(String.valueOf(Math.round((quantity2 * 100f) / (quantity1 * 1f))) + "%");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.weight = quantity2;
            smtOnPartViewHolder.imageView1.setLayoutParams(layoutParams);
        } else {
            smtOnPartViewHolder.textViewPer.setText(String.valueOf(Math.round((quantity2 * 100f) / (quantity1 * 1f))) + "%");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.weight = quantity2;
            smtOnPartViewHolder.imageView1.setLayoutParams(layoutParams);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams1.weight = quantity1 - quantity2;
            smtOnPartViewHolder.imageView2.setLayoutParams(layoutParams1);
        }

////        imageView1.setLayoutParams(layoutParams);
//
//        ImageView imageView2 = new ImageView(mContext);
//        imageView2.setImageResource(R.color.megmeet_green);
//        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(-2, -1);
//        layoutParams.weight = quantity1;
////        imageView2.setLayoutParams(layoutParams1);
//        smtOnPartViewHolder.linearLayout.addView(imageView1, layoutParams);
//        smtOnPartViewHolder.linearLayout.addView(imageView2, layoutParams1);
        return view;
    }

    class SMTOnPartViewHolder {
        private TextView textViewNum;
        private TextView textViewItemname;
        private TextView textViewMachine;
        private TextView textViewItemcode;
        private TextView textViewUseeach;
        private TextView textViewOnlinecount;
        private TextView textViewUsecount;
        private LinearLayout linearLayout;
        private ImageView imageView1;
        private ImageView imageView2;
        private TextView textViewPer;
    }
}
