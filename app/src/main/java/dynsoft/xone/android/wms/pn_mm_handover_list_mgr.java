package dynsoft.xone.android.wms;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class pn_mm_handover_list_mgr extends pn_mgr {

	public pn_mm_handover_list_mgr(Context context) {
		super(context);
	}
    private String  task_code;
	public  String task_code1;
   // public EditText txt_cell;
	@Override
    public void setContentView()
	{
		LayoutParams lp = new LayoutParams(-1,-1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_mm_handover_list_mgr, this, true);
        view.setLayoutParams(lp);
	}
	@Override
    public void onPrepared() {
        super.onPrepared();
       // this.txt_cell = (EditText) this
               // .findViewById(R.id.txt_search);

//        this.Matrix.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                putDialog2(i);
//
//            }
//        });
	}
	
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
        // TODO Auto-generated method stub  
        super.onActivityResult(requestCode, resultCode, data);  
    	this.refresh();
    }

    public void  putDialog2(int i){
	    final int a = i;
        final String[] arr10 = this.Adapter.DataTable.getValue(i,"code").toString().split(",");
        task_code1 = arr10[0];
        Log.e("LZH2022",task_code1);
    }
	@Override
    public void openItem(DataRow row)
    {
		Link link = new Link("pane://x:code=mm_and_handover_enter_list_mgr");
		link.Parameters.add("order_id", row.getValue("head_id", Long.class));
       // link.Parameters.add("user_operation", row.getValue("user_operation", Long.class));
       // link.Parameters.add("user_name", row.getValue("user_operation_name", ""));
        link.Parameters.add("code", row.getValue("code", ""));
        link.Parameters.add("id", row.getValue("id",0));
        link.Parameters.add("items", row.getValue("items",""));
        link.Parameters.add("task_code",task_code1 );
       // Log.e("len", row.getValue("head_id", Long.class) + "**" + row.getValue("user_operation", Long.class)
       //  + "**" + row.getValue("user_operation_name", "") + "**" + row.getValue("code", ""));
        link.Open(this, this.getContext(), null);
        //pn_mm_handover_list_mgr.this.close();
    }
	
	@Override
	public void onScan(final String barcode)
	{
		String txt = barcode;
		if (barcode.startsWith("CRQ:")) {
			String str = barcode.substring(4, barcode.length());
			String[] arr = str.split("-");
			if (arr.length > 1) {
				txt = arr[1];
			}
			
			this.SearchBox.setText(txt);
	    	this.Adapter.DataTable = null;
	    	this.Adapter.notifyDataSetChanged();
	    	this.refresh();
	    	
		} else if (barcode.startsWith("L:")){
			String loc = barcode.substring(2, barcode.length());
			this.SearchBox.setText(loc);
	    	this.Adapter.DataTable = null;
	    	this.Adapter.notifyDataSetChanged();
	    	this.refresh();
		}
        else if (barcode.startsWith("MO")){
		    task_code = barcode;
            this.SearchBox.setText(txt.trim());
            Log.e("lzh",String.valueOf(Adapter.DataTable.Rows.size()));
            this.Adapter.DataTable = null;
            this.Adapter.notifyDataSetChanged();
            this.refresh();

        }
    }
	
	@Override
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
    	DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_mm_handover_list, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView line1 = (TextView)convertView.findViewById(R.id.txt_line1);
        TextView line2 = (TextView)convertView.findViewById(R.id.txt_line2);
        TextView line3 = (TextView)convertView.findViewById(R.id.txt_line3);
        TextView line4 = (TextView)convertView.findViewById(R.id.txt_line4);
        
        num.setText(String.valueOf(position + 1));
        line1.setText(row.getValue("code", "")+','+row.getValue("user_operation_name", "")+"," + row.getValue("org_code", "") + ", " + row.getValue("status", ""));
        line2.setText("发料日期："+row.getValue("commit_time", "")+",点料位："+row.getValue("locations", ""));
        line3.setText(row.getValue("item_code", "")+","+row.getValue("item_name", ""));
        line4.setText(row.getValue("items", "")+",-请确认");
        final String[] arr4 = row.getValue("code", "").split(",");
        task_code = arr4[0];
        Log.e("LZH2011",task_code);
        return convertView;
    }
    
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
		SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_mm_handover_list_get_items_test ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return expr;
    }
}
