package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.link.PaneLinker;

public class pn_qm_iqc_mgr extends pn_mgr {

	public pn_qm_iqc_mgr(Context context) {
		super(context);
	}
	@Override
    public void onPrepared() {
        super.onPrepared();
        
        this.Matrix.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long index) {
				final DataRow row = (DataRow)Adapter.getItem((int)index);
				
					App.Current.question(pn_qm_iqc_mgr.this.getContext(), "确定要将该条检验单标记吗？", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							pn_qm_iqc_mgr.this.updateflag(row);
						}
					});
				return false;
			}
		});
	}
	public void updateflag(DataRow row)
	{
		//
		String sql = " update qm_iqc_head set attribute1=?  where id=? ";
		String  attribute1 ="";
		if (row.getValue("attribute1","").equals("1"))
		{
			attribute1="0";
		}else
		{
			attribute1="1";
		}
		Parameters p=new Parameters().add(1, attribute1).add(2,row.getValue("id",0));
		App.Current.DbPortal.ExecuteNonQuery(pn_qm_iqc_mgr.this.Connector, sql,p);
	    this.Adapter.DataTable = null;
	    this.Adapter.notifyDataSetChanged();
	    this.refresh();
	}

	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_iqc_mgr, this, true);
        view.setLayoutParams(lp);
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
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_qm_iqc_tasks, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_line1 = (TextView)convertView.findViewById(R.id.txt_line1);
        TextView txt_line2 = (TextView)convertView.findViewById(R.id.txt_line2);
        TextView txt_line3 = (TextView)convertView.findViewById(R.id.txt_line3);
        TextView txt_line4 = (TextView)convertView.findViewById(R.id.txt_line4);
        num.setText(String.valueOf(position + 1));
        txt_line1.setTextSize(18);
        txt_line2.setTextSize(18);
        txt_line3.setTextSize(18);
        txt_line4.setTextSize(18);
        int  priorityv=row.getValue("priorityv", 0);
        String attribute1 =row.getValue("attribute1","");
        
        if(priorityv==0)
        {
        	txt_line1.setBackgroundColor(Color.parseColor("#EE2C2C")); 
        	txt_line2.setBackgroundColor(Color.parseColor("#EE2C2C")); 
        	txt_line3.setBackgroundColor(Color.parseColor("#EE2C2C")); 
        	txt_line4.setBackgroundColor(Color.parseColor("#EE2C2C")); 
        }else if(attribute1=="1" || attribute1.equals("1") )
        {
        	txt_line1.setBackgroundColor(Color.parseColor("#9ACD32")); 
        	txt_line2.setBackgroundColor(Color.parseColor("#9ACD32")); 
        	txt_line3.setBackgroundColor(Color.parseColor("#9ACD32")); 
        	txt_line4.setBackgroundColor(Color.parseColor("#9ACD32"));
        }
        else
        {
        	txt_line1.setBackgroundColor(Color.parseColor("#FFFFF0")); 
        	txt_line2.setBackgroundColor(Color.parseColor("#FFFFF0")); 
        	txt_line3.setBackgroundColor(Color.parseColor("#FFFFF0")); 
        	txt_line4.setBackgroundColor(Color.parseColor("#FFFFF0")); 
        }
        txt_line4.setText(row.getValue("organization_code", "") +","+ row.getValue("code", ""));
        txt_line1.setText(row.getValue("priority", "")+","+row.getValue("locations", "") + ", " + row.getValue("vendor_name", ""));
        txt_line3.setText(row.getValue("item_name", "") );
        txt_line2.setText(row.getValue("item_code", "")+","+App.formatNumber(row.getValue("test_qty", BigDecimal.ZERO), "0.##")+" PCS");
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
    	SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_qm_iqc_get_tasks ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
}
