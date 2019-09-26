package dynsoft.xone.android.sbo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.SqlExpression;

public class odoc_mgr extends obj_mgr {

    public odoc_mgr(Context context) {
		super(context);
	}

	@Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.odoc, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            ImageView arrow = (ImageView)convertView.findViewById(R.id.imgArrow);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_odoc_gray"));
            arrow.setImageBitmap(App.Current.ResourceManager.getImage("@/core_forward_light"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView docnum = (TextView)convertView.findViewById(R.id.txtDocNum);
        TextView date = (TextView)convertView.findViewById(R.id.txtDocDate);
        TextView status = (TextView)convertView.findViewById(R.id.txtDocStatus);
        TextView card = (TextView)convertView.findViewById(R.id.txtCardCode);
        TextView name = (TextView)convertView.findViewById(R.id.txtCardName);
        TextView user = (TextView)convertView.findViewById(R.id.txtUserName);
        TextView amount = (TextView)convertView.findViewById(R.id.txtDocTotal);
        
        num.setText(String.valueOf(position + 1));
        docnum.setText(row.getValue("DocNum").toString());
        date.setText(row.getValue("DocDate",""));
        status.setText(row.getValue("DocStatus", ""));
        card.setText(row.getValue("CardCode", ""));
        name.setText(row.getValue("CardName", ""));
        user.setText(row.getValue("U_NAME", ""));
        amount.setText(App.formatNumber(row.getValue("DocTotal"), "0.##"));

        return convertView;
    }
    
    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        String table = this.getDocType().MainTable;
        SqlExpression expr = new SqlExpression();
        expr.SQL = "with temp as (select top (?) ROW_NUMBER() over (order by DocEntry desc) Number,DocEntry,DocNum,CONVERT(varchar(10),DocDate,120) DocDate,DocStatus,CardCode,CardName,DocTotal,OUSR.U_NAME from "
                 + table + " left join OUSR on OUSR.USERID=" + table + ".UserSign where (ISNULL(CardCode,'') like '%'+?+'%') or (ISNULL(CardName,'') like '%'+?+'%')) select * from temp where Number>=? and Number<=?";
        expr.Parameters = new Parameters().add(1,top).add(2, search).add(3, search).add(4,start).add(5,end);
        return expr;
    }
    
    public DocType getDocType()
    {
        return null;
    }
}
