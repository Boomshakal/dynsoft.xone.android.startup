package dynsoft.xone.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;

public class MyListTaskOrderCodeAdapter extends BaseAdapter {
    private DataTable dataTable;
    private Context context;
    private int selectedItems = -1;

    public MyListTaskOrderCodeAdapter(DataTable dataTable, Context context) {
        this.dataTable = dataTable;
        this.context = context;
    }

    public void frash(int selectedItems) {
        this.selectedItems = selectedItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataTable == null ? 0 : dataTable.Rows.size();
    }

    @Override
    public Object getItem(int position) {
        return dataTable.Rows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View contextView, ViewGroup viewGroup) {
        ItemViewHolder viewHolder;
        if (contextView == null) {
            contextView = View.inflate(context, R.layout.pre_card_choose_taskorder_item, null);
            viewHolder = new ItemViewHolder();
            viewHolder.textview_1 = (TextView) contextView.findViewById(R.id.textview_1);
            viewHolder.textview_2 = (TextView) contextView.findViewById(R.id.textview_2);
            viewHolder.textview_3 = (TextView) contextView.findViewById(R.id.textview_3);
            viewHolder.linearLayout = (LinearLayout) contextView.findViewById(R.id.linearlayout);
            contextView.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder) contextView.getTag();
        }

        DataRow dataRow = dataTable.Rows.get(position);
        String task_order_code = dataRow.getValue("task_order_code", "");
        String item_code = dataRow.getValue("item_code", "");
        String item_name = dataRow.getValue("item_name", "");
        viewHolder.textview_1.setText(task_order_code);
        viewHolder.textview_2.setText(item_code);
        viewHolder.textview_3.setText(item_name);

        if (selectedItems == position) {
            viewHolder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.parameter_color));
        } else {
            viewHolder.linearLayout.setBackgroundColor(Color.WHITE);
        }
        return contextView;
    }

    public void refresh(DataTable value) {
        dataTable = value;
        notifyDataSetChanged();
    }

    class ItemViewHolder {
        private TextView textview_1;
        private TextView textview_2;
        private TextView textview_3;
        private LinearLayout linearLayout;
    }
}