package dynsoft.xone.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import dynsoft.xone.android.core.R;
import dynsoft.xone.android.retrofit.deletecloud.GetIdBySNSuccessBean;

public class CloudAdapter extends BaseAdapter {
    private GetIdBySNSuccessBean getIdBySNSuccessBeans;
    private Context mContext;

    public CloudAdapter(GetIdBySNSuccessBean getIdBySNSuccessBeans, Context mContext) {
        this.getIdBySNSuccessBeans = getIdBySNSuccessBeans;
        this.mContext = mContext;
    }

    public void fresh(GetIdBySNSuccessBean getIdBySNSuccessBeans) {
        this.getIdBySNSuccessBeans =getIdBySNSuccessBeans;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return getIdBySNSuccessBeans == null ? 0 : getIdBySNSuccessBeans.getList().size();
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
        if (view == null) {
            viewHolder = new ViewHolder();
            view = View.inflate(mContext, R.layout.item_withdraw_cloud_listview, null);
            viewHolder.textViewSn = (TextView) view.findViewById(R.id.txt_sn);
            viewHolder.textViewMac = (TextView) view.findViewById(R.id.txt_mac);
            viewHolder.textViewId = (TextView) view.findViewById(R.id.txt_id);
            viewHolder.textViewName = (TextView) view.findViewById(R.id.txt_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        String sn_no = getIdBySNSuccessBeans.getList().get(i).getSn();
        String mac = getIdBySNSuccessBeans.getList().get(i).getMac();
        int id = getIdBySNSuccessBeans.getList().get(i).getId();
        String name = getIdBySNSuccessBeans.getList().get(i).getName();
        viewHolder.textViewSn.setText(sn_no.trim());
        viewHolder.textViewMac.setText(mac.trim());
        viewHolder.textViewId.setText(String.valueOf(id));
        viewHolder.textViewName.setText(name.trim());
        return view;
    }

    class ViewHolder {
        private TextView textViewSn;
        private TextView textViewMac;
        private TextView textViewId;
        private TextView textViewName;
    }
}
