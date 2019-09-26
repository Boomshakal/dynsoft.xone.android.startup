package dynsoft.xone.android.wms;
import java.io.UnsupportedEncodingException;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ImageButton;
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
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class fm_smt_materials_mgr extends pn_mgr {

	public fm_smt_materials_mgr(Context context) {
		super(context);
	}

	public TextCell txt_work_order_code_cell; // 工单编码
	public TextCell txt_item_code_cell; //物料编码
	
	public EditText SearchBox;
	
	
	@Override
    public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.fm_smt_materials_mgr, this, true);
        view.setLayoutParams(lp);
	}
	
	//设置显示控件
	@Override
	public void onPrepared() {

		super.onPrepared();

		this.SearchBox = (EditText)this.findViewById(R.id.txt_search);
		this.txt_work_order_code_cell = (TextCell) this
				.findViewById(R.id.txt_work_order_code_cell);	
		this.txt_item_code_cell = (TextCell) this
				.findViewById(R.id.txt_item_code_cell);		
		
		this.SearchBox.setVisibility(View.GONE);
		
	//	this.CommitButton = (ImageButton)this.findViewById(R.id.btn_commit);
		
		

		if (this.txt_work_order_code_cell != null) {
			this.txt_work_order_code_cell.setLabelText("工单编码");
			this.txt_work_order_code_cell.setReadOnly();
		}					
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("物料编码");
			this.txt_item_code_cell.setReadOnly();
		}		
		 
	}
	
	
	@Override
	public void onScan(final String barcode)
	{
		//String txt = barcode;
		//if (barcode.startsWith("CRQ:")) {
		//	String str = barcode.substring(4, barcode.length());
		//	String[] arr = str.split("-");
		//	if (arr.length > 0) {
		//		txt = arr[0];
		//	}
			
		//	this.SearchBox.setText(txt);
	    //	this.Adapter.DataTable = null;
	    //	this.Adapter.notifyDataSetChanged();
	    //	this.refresh();
	    	
		//}
		final String bar_code = barcode.trim();
		  if (bar_code.startsWith("WO:"))
	        {
	             //扫描工单条码
	        	 this.txt_work_order_code_cell.setContentText(bar_code.toString().split(":")[1]);
	        	 String a =bar_code.toString().split(":")[1];
	        	 //task_order=bar_code.toString().split(":")[1];
	        	// this.loadGetprogram(bar_code.toString().split(":")[1]);
	        	 this.Adapter.DataTable = null;
	     	     this.Adapter.notifyDataSetChanged();
	     	     this.refresh();
	        	 
	        }
	        else if (bar_code.startsWith("CRQ:"))
	        {
	        	 
	    		int pos = bar_code.indexOf("-");
	    		String lot = bar_code.toString().split("-")[1];//bar_code.substring(2, pos);
	        	//if (this.txt_work_order_code_cell.getContentText()==null ||this.txt_work_order_code_cell.getContentText().length()==0 )
	        	this.txt_item_code_cell.setContentText(lot);
	        	 //扫描程序名条码
	             this.Adapter.DataTable = null;
	     	     this.Adapter.notifyDataSetChanged();
	     	     this.refresh();
	     	     fm_smt_materials_mgr.this.txt_item_code_cell.setContentText("");
	        	 
	        }
    }
	
	
	@Override
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
    	DataRow row = (DataRow)Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.fm_smt_materials, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView txt_wo_issue_order_code = (TextView)convertView.findViewById(R.id.txt_machine);
        TextView txt_create_time = (TextView)convertView.findViewById(R.id.txt_program);
        TextView txt_status = (TextView)convertView.findViewById(R.id.txt_item_code);
        TextView txt_items = (TextView)convertView.findViewById(R.id.txt_position);
        
        num.setText(String.valueOf(position + 1));
        txt_wo_issue_order_code.setText(row.getValue("machine", ""));
        txt_create_time.setText(row.getValue("program", ""));
        txt_status.setText(row.getValue("item_code", ""));
        txt_items.setText(row.getValue("position",""));
        
        return convertView;
    }
    
	
 
	@Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
		SqlExpression expr = new SqlExpression();
		if (this.txt_work_order_code_cell!=null && this.txt_item_code_cell!=null)
		{
	 		String task_order =this.txt_work_order_code_cell.getContentText();
	 		String item_code= this.txt_item_code_cell.getContentText();
	 		if (item_code.length() ==0 || item_code ==null )
	 		{
	 			 expr.SQL = "exec p_position_get_items ?,?,?,?";
		         expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, task_order);
	 		} else 
	 		{
	 			 expr.SQL = "exec p_position_get_item ?,?,?,?,?";
		         expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, task_order).add(5, item_code);
	 		}
	 		// expr.SQL = "exec p_qm_oqc_get_items ?,?,?,?,?";
	        // expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, task_order).add(5, item_code);
	 		 return expr;	
		}
        //expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3,end).add(4, search);
        return null;
	}
}
