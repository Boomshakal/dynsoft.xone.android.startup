package dynsoft.xone.android.sbo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.link.PaneLinker;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class ostk_mgr extends odoc_mgr {

    public ostk_mgr(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ostk, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            ImageView arrow = (ImageView)convertView.findViewById(R.id.imgArrow);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
            arrow.setImageBitmap(App.Current.ResourceManager.getImage("@/core_forward_light"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView docnum = (TextView)convertView.findViewById(R.id.txtDocNum);
        TextView date = (TextView)convertView.findViewById(R.id.txtDocDate);
        TextView status = (TextView)convertView.findViewById(R.id.txtDocStatus);
        TextView itemcode = (TextView)convertView.findViewById(R.id.txtItemCode);
        TextView itemname = (TextView)convertView.findViewById(R.id.txtItemName);
        //TextView user = (TextView)convertView.findViewById(R.id.txtUserName);
        TextView quantity = (TextView)convertView.findViewById(R.id.txtQuantity);
        
        num.setText(String.valueOf(position + 1));
        docnum.setText(row.getValue("Number").toString());
        date.setText(App.formatDateTime(row.getValue("DocDate"), "yyyy-MM-dd"));
        status.setText(row.getValue("DocStatus", ""));
        itemcode.setText(row.getValue("ItemCode", ""));
        itemname.setText(row.getValue("ItemName", ""));
        //user.setText(row.getValue("U_NAME", ""));
        quantity.setText("QTY: " + App.formatNumber(row.getValue("Quantity"), "0.##"));

        return convertView;
    }
    
    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        DocType docType = this.getDocType();
        DocType baseType = this.getBaseType();
        String main = docType.MainTable;
        String line = docType.LineTable;
        
        SqlExpression expr = new SqlExpression();
        expr.SQL = "with temp as (select top(?) ROW_NUMBER() over(order by DocNum desc,LineNum asc) N, " + main + ".DocEntry," + line + ".LineNum,(cast(DocNum as varchar)+'-'+cast(LineNum as varchar)) Number,"
                 + main + ".DocDate,DocStatus,ItemCode,Dscription ItemName,Quantity,OUSR.USERID,OUSR.U_NAME from " + line + " "
                 + "inner join " + main + " on " + main + ".DocEntry=" + line + ".DocEntry left join OUSR on OUSR.USERID=" + main + ".UserSign "
                 + "where BaseType=? and (ISNULL(ItemCode,'') like '%'+?+'%' or ISNULL(Dscription,'') like '%'+?+'%')) "
                 + "select * from temp where N>=? and N<=?";
        expr.Parameters = new Parameters().add(1,top).add(2, baseType.TypeID).add(3, search).add(4, search).add(5,start).add(6,end);
        return expr;
    }
    
    public DocType getBaseType()
    {
        return DocType.NONE;
    }
}
