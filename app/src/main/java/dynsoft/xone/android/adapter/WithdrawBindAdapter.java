package dynsoft.xone.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;

public class WithdrawBindAdapter extends BaseAdapter {
    private DataTable mDataTable;
    private Context mContext;

    public WithdrawBindAdapter(DataTable mDataTable, Context mContext) {
        this.mDataTable = mDataTable;
        this.mContext = mContext;
    }

    public void fresh(DataTable dataTable) {
        mDataTable = dataTable;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDataTable == null ? 0 : mDataTable.Rows.size();
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
            view = View.inflate(mContext, R.layout.item_withdraw_bind_listview, null);
            viewHolder.textViewLotnumber = (TextView) view.findViewById(R.id.txt_lot_number);
            viewHolder.textViewBindLotnumber = (TextView) view.findViewById(R.id.txt_bind_lot_number);
            viewHolder.textViewWorktime = (TextView) view.findViewById(R.id.txt_work_time);
            viewHolder.textViewWorker = (TextView) view.findViewById(R.id.txt_worker);
            viewHolder.textViewCurSequence = (TextView) view.findViewById(R.id.txt_cur_sequence);
            viewHolder.textViewTaskOrderCode = (TextView) view.findViewById(R.id.txt_task_order_code);
            viewHolder.textViewItemcode = (TextView) view.findViewById(R.id.txt_item_code);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        String sn_no = mDataTable.Rows.get(i).getValue("sn_no", "");
        String binding_sn_no = mDataTable.Rows.get(i).getValue("binding_sn_no", "");
        String create_time = mDataTable.Rows.get(i).getValue("create_time", "");
        String worker_name = mDataTable.Rows.get(i).getValue("worker_name", "");
        String sequence_name = mDataTable.Rows.get(i).getValue("sequence_name", "");
        String task_order_code = mDataTable.Rows.get(i).getValue("task_order_code", "");
        String item_name = mDataTable.Rows.get(i).getValue("item_name", "");
        viewHolder.textViewLotnumber.setText(sn_no.trim());
        viewHolder.textViewBindLotnumber.setText(binding_sn_no.trim());
        viewHolder.textViewWorktime.setText(create_time.trim());
        viewHolder.textViewWorker.setText(worker_name.trim());
        viewHolder.textViewCurSequence.setText(sequence_name.trim());
        viewHolder.textViewTaskOrderCode.setText(task_order_code.trim());
        viewHolder.textViewItemcode.setText(item_name);
        return view;
    }

    class ViewHolder {
        private TextView textViewLotnumber;
        private TextView textViewBindLotnumber;
        private TextView textViewWorktime;
        private TextView textViewWorker;
        private TextView textViewCurSequence;
        private TextView textViewTaskOrderCode;
        private TextView textViewItemcode;
    }
}
