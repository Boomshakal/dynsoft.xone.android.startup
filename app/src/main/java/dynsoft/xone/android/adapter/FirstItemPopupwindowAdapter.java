package dynsoft.xone.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;

public class FirstItemPopupwindowAdapter extends BaseAdapter {
    private DataTable mDataTable;
    private Context mContext;

    public FirstItemPopupwindowAdapter(DataTable mDataTable, Context mContext) {
        this.mDataTable = mDataTable;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mDataTable == null ? 0 : mDataTable.Rows.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_first_item_popupwindow, null);
            viewHolder = new ViewHolder();
            viewHolder.textViewPicNum = (TextView) convertView.findViewById(R.id.textview_1);
            viewHolder.textViewFileName = (TextView) convertView.findViewById(R.id.textview_2);
            viewHolder.textViewStatus = (TextView) convertView.findViewById(R.id.textview_3);
            viewHolder.textViewVersion = (TextView) convertView.findViewById(R.id.textview_4);
            viewHolder.textViewFileType = (TextView) convertView.findViewById(R.id.textview_5);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String drawNo = mDataTable.Rows.get(position).getValue("drawNo", "");
        String attachments = mDataTable.Rows.get(position).getValue("Attachments", "");
        String docstatus = mDataTable.Rows.get(position).getValue("docstatus", "");
        String version = mDataTable.Rows.get(position).getValue("version", "");
        String docname = mDataTable.Rows.get(position).getValue("docname", "");
        viewHolder.textViewPicNum.setText(drawNo);
        viewHolder.textViewFileName.setText(attachments);
        viewHolder.textViewStatus.setText(docstatus);
        viewHolder.textViewVersion.setText(version);
        viewHolder.textViewFileType.setText(docname);
        return convertView;
    }

    class ViewHolder {
        private TextView textViewPicNum;
        private TextView textViewFileName;
        private TextView textViewStatus;
        private TextView textViewVersion;
        private TextView textViewFileType;
    }
}
