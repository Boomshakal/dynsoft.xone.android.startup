package dynsoft.xone.android.wms;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class pn_mm_device_pro_list extends pn_mgr {

	public pn_mm_device_pro_list(Context context) {
		super(context);
	}
    private String  task_code;
   // public EditText txt_cell;
	@Override
    public void setContentView()
	{
		LayoutParams lp = new LayoutParams(-1,-1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_mm_device_pro_list, this, true);
        view.setLayoutParams(lp);
	}
	@Override
    public void onPrepared() {
        super.onPrepared();
       // this.txt_cell = (EditText) this
               // .findViewById(R.id.txt_search);
	}
	
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
        // TODO Auto-generated method stub  
        super.onActivityResult(requestCode, resultCode, data);  
    	this.refresh();
    }
    
	@Override
    public void openItem(DataRow row)
    {
        Link link = new Link("pane://x:code=pn_device_pro_edict");

        link.Parameters.add("code", row.getValue("code", ""));
       
        this.Adapter.DataTable = null;
        this.Adapter.notifyDataSetChanged();
        this.refresh();
        link.Open(this, this.getContext(), null);

        //pn_mm_handover_list_mgr.this.close();
    }
	
	@Override
    public void onScan(final String barcode)
    {
        String txt = barcode;
        final String bar_code = barcode.trim();
        String str = bar_code.substring(0,1);
        if (str.matches("[0-9]")||bar_code.startsWith("B")){

            this.SearchBox.setText(txt.trim());
            this.Adapter.DataTable = null;
            this.Adapter.notifyDataSetChanged();
            this.refresh();

        }
        else
        {
            App.Current.showError(this.getContext(), "非法条码，请扫描正确的二维码！"+bar_code.toString());
        }
    }
	
	@Override
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
    	DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_mm_device_pro_item, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView line1 = (TextView)convertView.findViewById(R.id.txt_line1);
        TextView line2 = (TextView)convertView.findViewById(R.id.txt_line2);
        TextView line3 = (TextView)convertView.findViewById(R.id.txt_line3);
        TextView line4 = (TextView)convertView.findViewById(R.id.txt_line4);
        
        num.setText(String.valueOf(position + 1));
        line1.setText(row.getValue("code", "")+','+row.getValue("care_user", "")+"," + row.getValue("care_kind", "") );
        line2.setText("上次维护时间："+String.valueOf(row.getValue("care_date", ""))+",维护周期："+String.valueOf(row.getValue("care_rate", 0)+"天"));
        line3.setText("位置:"+row.getValue("location", ""));
        line4.setText(row.getValue("use_depart_name", ""));
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
		SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_mm_device_pro_itemt ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
}
