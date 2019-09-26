package dynsoft.xone.android.start;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.wms.pn_mgr;

public class pn_core_message_mgr extends pn_mgr {

	public pn_core_message_mgr(Context context) {
		super(context);
	}

	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_core_message_mgr, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
    public void openItem(DataRow row)
    {
		Link link = new Link("pane://x:code=mm_and_core_message_editor");
		link.Parameters.add("message_id", row.getValue("id", 0L));
        link.Open(this, this.getContext(), null);
    }
	
	@Override
	public void onScan(final String barcode)
	{
    	this.SearchBox.setText(barcode);
    	this.Adapter.DataTable = null;
    	this.Adapter.notifyDataSetChanged();
    	this.refresh();
    }
	
	@Override
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
    	DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_core_message, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_subject = (TextView)convertView.findViewById(R.id.txt_subject);
        TextView txt_content = (TextView)convertView.findViewById(R.id.txt_content);
        TextView txt_sender = (TextView)convertView.findViewById(R.id.txt_sender);

        num.setText(String.valueOf(position + 1));
        txt_subject.setText(row.getValue("subject", ""));
        txt_sender.setText(row.getValue("create_user_name", "") + ", " + App.formatDateTime(row.getValue("create_time"), "yyyy-MM-dd HH:mm"));
        
        String content = row.getValue("content", "");
        if (content != null && content.length() > 0) {
        	txt_content.setText(content);
        	txt_content.setVisibility(View.VISIBLE);
        } else {
        	txt_content.setText("");
        	txt_content.setVisibility(View.GONE);
        }
        
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
		SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_core_message_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
}
