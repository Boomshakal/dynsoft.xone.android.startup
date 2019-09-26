package dynsoft.xone.android.sbo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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

public class ocrd_mgr extends obj_mgr {

    public ocrd_mgr(Context context) {
		super(context);
	}

	@Override
    public void openItem(DataRow row) {
        Link link = new Link("pane://x:code=sbo_and_ocrd_editor");
        link.Parameters.add(PaneLinker.KEY_XFLAG, row.getValue("CardCode", String.class));
        link.Parameters.add("CardCode", row.getValue("CardCode", String.class));
        link.Open(this, this.getContext(), null);
    }
    
    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ocrd, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            ImageView arrow = (ImageView)convertView.findViewById(R.id.imgArrow);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_ocrd_gray"));
            arrow.setImageBitmap(App.Current.ResourceManager.getImage("@/core_forward_light"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView code = (TextView)convertView.findViewById(R.id.txtCode);
        TextView qty = (TextView)convertView.findViewById(R.id.txtCntct);
        TextView name = (TextView)convertView.findViewById(R.id.txtName);
        TextView addr = (TextView)convertView.findViewById(R.id.txtAddress);
        
        num.setText(String.valueOf(position + 1));
        code.setText(row.getValue("CardCode", ""));
        qty.setText(row.getValue("CntctPrsn", ""));
        name.setText(row.getValue("CardName", ""));
        addr.setText(row.getValue("Address", ""));
        
        return convertView;
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "with temp as (select top (?) ROW_NUMBER() over (order by CardCode) Number,CardCode,CardName,CntctPrsn,Address from OCRD where (CardCode like '%'+?+'%') or (CardName like '%'+?+'%')) select CardCode,CardName,CntctPrsn,Address from temp where Number>=? and Number<=?";
        expr.Parameters = new Parameters().add(1,top).add(2, search).add(3, search).add(4,start).add(5,end);
        return expr;
    }
}
