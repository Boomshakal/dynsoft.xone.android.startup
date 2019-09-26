package dynsoft.xone.android.sbo;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import dynsoft.xone.android.base.PnWebReport;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.link.PaneLinker;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class oitm_mgr extends obj_mgr {
    
    public oitm_mgr(Context context) {
		super(context);
	}

	@Override
    public void openItem(DataRow row) {
        Link link = new Link("pane://x:code=sbo_and_oitm_editor");
        link.Parameters.add(PaneLinker.KEY_XFLAG, row.getValue("ItemCode", String.class));
        link.Parameters.add("ItemCode", row.getValue("ItemCode", String.class));
        link.Open(this, this.getContext(), null);
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.oitm, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            ImageView arrow = (ImageView)convertView.findViewById(R.id.imgArrow);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
            arrow.setImageBitmap(App.Current.ResourceManager.getImage("@/core_forward_light"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView code = (TextView)convertView.findViewById(R.id.txtCode);
        TextView qty = (TextView)convertView.findViewById(R.id.txtQty);
        TextView name = (TextView)convertView.findViewById(R.id.txtName);
        
        num.setText(String.valueOf(position + 1));
        code.setText(row.getValue("ItemCode", String.class));
        qty.setText(row.getValue("OnHand").toString());
        name.setText(row.getValue("ItemName", ""));

        return convertView;
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "with temp as (select top (?) ROW_NUMBER() over (order by ItemCode) Number,ItemCode,ItemName,CAST(OnHand as decimal(19,0)) OnHand from OITM where (ItemCode like '%'+?+'%') or (ItemName like '%'+?+'%')) select ItemCode,ItemName,OnHand from temp where Number>=? and Number<=?";
        expr.Parameters = new Parameters().add(1,top).add(2, search).add(3, search).add(4,start).add(5,end);
        return expr;
    }

//    @Override
//    public void report()
//    {
//        String url = "pane://x:code=core_and_webreport;" + WebReportPaneExtension.PARAM_CODE + "=OITM_ODLN_TOP_10";
//        Link link = new Link(url);
//        link.Open(this.ReportButton, getContext(), null);
//    }
}
