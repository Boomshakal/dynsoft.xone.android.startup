package dynsoft.xone.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.math.BigDecimal;

import dynsoft.xone.android.activity.SMTOnPartActivity;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;

/**
 * Created by Administrator on 2018/12/13.
 */

public class SMTOnPartPopupAdapter extends BaseAdapter {
    private DataTable dataTable;
    private Context context;
    public SMTOnPartPopupAdapter(DataTable dataTable, Context context) {
        this.dataTable = dataTable;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataTable.Rows.size();
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
        ViewHolder viewHolder;
        if(view == null) {
            viewHolder = new ViewHolder();
            view = View.inflate(context, R.layout.item_listview_popup_smtonpart, null);
            viewHolder.textViewPosition = (TextView) view.findViewById(R.id.textview_position);
            viewHolder.textViewMachine = (TextView) view.findViewById(R.id.textview_machine);
            viewHolder.textViewItemCode = (TextView) view.findViewById(R.id.textview_itemcode);
            viewHolder.textViewLast = (TextView) view.findViewById(R.id.textview_last);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        String position = dataTable.Rows.get(i).getValue("position", ""); //工位
        String machine = dataTable.Rows.get(i).getValue("machine", ""); //工位
        String itemcode = dataTable.Rows.get(i).getValue("item_code", ""); //工位
        float quantity1 = dataTable.Rows.get(i).getValue("quantity1", new BigDecimal(0)).floatValue();
        int quantity2 = dataTable.Rows.get(i).getValue("quantity2", 0);
        viewHolder.textViewPosition.setText(position);
        viewHolder.textViewMachine.setText(machine);
        viewHolder.textViewItemCode.setText(itemcode);
        viewHolder.textViewLast.setText(String.valueOf(quantity1 - quantity2));
        return view;
    }

    class ViewHolder {
        private TextView textViewPosition;
        private TextView textViewMachine;
        private TextView textViewItemCode;
        private TextView textViewLast;
    }
}
