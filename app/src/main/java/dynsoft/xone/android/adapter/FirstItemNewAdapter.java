package dynsoft.xone.android.adapter;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

public class FirstItemNewAdapter extends BaseAdapter {
    private DataTable mDataTable;
    private Context mContext;

    public FirstItemNewAdapter(DataTable mDataTable, Context mContext) {
        this.mDataTable = mDataTable;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mDataTable.Rows.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_first_bom, null);
            viewHolder = new ViewHolder();
            viewHolder.textViewNum = (TextView) convertView.findViewById(R.id.text_num);
            viewHolder.textViewUpdateTime = (TextView) convertView.findViewById(R.id.text_updatetime);
            viewHolder.textViewECN = (TextView) convertView.findViewById(R.id.text_ecn);
            viewHolder.textViewUpdateCode = (TextView) convertView.findViewById(R.id.text_updatecode);
            viewHolder.textViewVersion = (TextView) convertView.findViewById(R.id.text_version);
            viewHolder.textViewLevel = (TextView) convertView.findViewById(R.id.text_level);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearlayout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String id = mDataTable.Rows.get(position).getValue("id", "");
        String change_notice = mDataTable.Rows.get(position).getValue("change_notice", "");
        String implementation_date = mDataTable.Rows.get(position).getValue("implementation_date", "");
        String new_item_revision = mDataTable.Rows.get(position).getValue("new_item_revision", "");
        String plan_level = mDataTable.Rows.get(position).getValue("plan_level", "");
        String item_num = mDataTable.Rows.get(position).getValue("item_num", "");
        viewHolder.textViewNum.setText(id);
        viewHolder.textViewUpdateTime.setText(implementation_date);
        viewHolder.textViewECN.setText(change_notice);
        viewHolder.textViewUpdateCode.setText(item_num);
        viewHolder.textViewVersion.setText(new_item_revision);
        viewHolder.textViewLevel.setText(plan_level);
        return convertView;
    }

    class ViewHolder {
        private TextView textViewNum;
        private TextView textViewUpdateTime;
        private TextView textViewECN;
        private TextView textViewUpdateCode;
        private TextView textViewVersion;
        private TextView textViewLevel;
        private LinearLayout linearLayout;
    }
}
