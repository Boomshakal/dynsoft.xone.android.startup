package dynsoft.xone.android.sbo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.link.PaneLinker;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class owdd_mgr extends obj_mgr {

    public owdd_mgr(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
    public void openItem(DataRow row)
    {
        Link link = new Link("pane://x:code=sbo_and_owdd_editor");
        link.Parameters.add(PaneLinker.KEY_XFLAG, row.getValue("DocEntry"));
        link.Parameters.add("DocEntry", row.getValue("DocEntry"));
        link.Open(this, this.getContext(), null);
    }
    
    @Override
    public void create()
    {
        Link link = new Link("pane://x:code=sbo_and_owdd_editor");
        link.Parameters.add(PaneLinker.KEY_XFLAG, -1);
        link.Parameters.add("DocEntry", -1);
        link.Open(this, this.getContext(), null);
    }
    
    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.owdd, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            ImageView arrow = (ImageView)convertView.findViewById(R.id.imgArrow);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_owdd_gray"));
            arrow.setImageBitmap(App.Current.ResourceManager.getImage("@/core_forward_light"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView wddcode = (TextView)convertView.findViewById(R.id.txtWddCode);
        TextView date = (TextView)convertView.findViewById(R.id.txtDocDate);
        TextView status = (TextView)convertView.findViewById(R.id.txtStatus);
        TextView wddname = (TextView)convertView.findViewById(R.id.txtWddName);
        TextView user = (TextView)convertView.findViewById(R.id.txtUserName);
        TextView remark = (TextView)convertView.findViewById(R.id.txtRemarks);
        
        num.setText(String.valueOf(position + 1));
        wddcode.setText(row.getValue("WddCode").toString());
        date.setText(App.formatDateTime(row.getValue("DocDate"), "yyyy-MM-dd"));
        status.setText(row.getValue("Status", ""));
        wddname.setText(row.getValue("Name", ""));
        user.setText(row.getValue("U_NAME", ""));
        remark.setText(row.getValue("Remarks", ""));

        return convertView;
    }
    
    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {

        SqlExpression expr = new SqlExpression();
        expr.SQL = "with temp as (select top (?) ROW_NUMBER() over (order by WddCode desc) N,WddCode,OWTM.Name,OWDD.DocDate,OUSR.U_NAME,"
                 + "(CASE Status when 'W' then N'未决' when 'Y' then N'已批' when 'N' then N'否决' end) Status,"
                 + "(select count(*) from WDD1 where WddCode=OWDD.WddCode and UserID=? and Status='W') Count from OWDD "
                 + "left join OWTM on OWTM.WtmCode=OWDD.WtmCode left join OUSR on OUSR.USERID=OWDD.OwnerID "
                 + "where ISNULL(OWTM.Name,'') like '%'+?+'%' and "
                 + "(select count(*) from WST1 inner join WTM2 on WTM2.WstCode=WST1.WstCode where WTM2.WtmCode=OWDD.WtmCode and WST1.UserID=?)>0) "
                 + "select * from temp where N>=? and N<=?";
        expr.Parameters = new Parameters().add(1,top).add(2, App.Current.UserID).add(3, search).add(4, App.Current.UserID).add(5,start).add(6,end);
        return expr;
    }
}
