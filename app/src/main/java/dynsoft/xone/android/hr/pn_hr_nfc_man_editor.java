package dynsoft.xone.android.hr;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;



import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.ImageCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.wms.fm_smt_z_editor;
import dynsoft.xone.android.wms.frm_item_lot_printer_init;
import dynsoft.xone.android.wms.pn_wo_issue_editor;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class pn_hr_nfc_man_editor extends pn_editor {

	public pn_hr_nfc_man_editor(Context context) {
		super(context);
	}
	
	
	public TextCell txt_man_no_textcell;
	public TextCell txt_fa_textcell;
	public ImageCell txt_image_textcell;
	public TextCell txt_man_name_textcell;
	public TextCell txt_dept_name;
	public TextCell txt_sex_textcell;
	public TextCell txt_rz_datecell;
	
	private DataRow _order_row;

  
	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_man_no_textcell = (TextCell)this.findViewById(R.id.txt_man_no_textcell);
		this.txt_man_name_textcell = (TextCell)this.findViewById(R.id.txt_man_name_textcell);
		this.txt_fa_textcell = (TextCell)this.findViewById(R.id.txt_fa_textcell);
		this.txt_dept_name = (TextCell)this.findViewById(R.id.txt_dept_name);
		this.txt_sex_textcell = (TextCell)this.findViewById(R.id.txt_sex_textcell);
		this.txt_rz_datecell = (TextCell)this.findViewById(R.id.txt_rz_datecell);
		this.txt_image_textcell = (ImageCell)this.findViewById(R.id.txt_image_textcell);
	
		if (this.txt_man_no_textcell != null) {
			this.txt_man_no_textcell.setLabelText("工号：");
			this.txt_man_no_textcell.setReadOnly();
		}
		
		if (this.txt_man_name_textcell != null) {
			this.txt_man_name_textcell.setLabelText("姓名：");
			this.txt_man_name_textcell.setReadOnly();
		}
		if (this.txt_fa_textcell != null) {
			this.txt_fa_textcell.setLabelText("条码：");
			this.txt_fa_textcell.setReadOnly();
		}
		if (this.txt_dept_name != null) {
			this.txt_dept_name.setLabelText("部门：");
			this.txt_dept_name.setReadOnly();
		}
		
		if (this.txt_sex_textcell != null) {
			this.txt_sex_textcell.setLabelText("性别：");
			this.txt_sex_textcell.setReadOnly();
		}
		
		if (this.txt_rz_datecell != null) {
			this.txt_rz_datecell.setLabelText("入职日期：");
			this.txt_rz_datecell.setReadOnly();
		}
		if (this.txt_image_textcell!=null)
		{
			LayoutParams params =(LayoutParams) this.txt_image_textcell.getLayoutParams();  
		    params.height=280;  
		    params.width =300;
		    this.txt_image_textcell.setLayoutParams(params); 
		}
		
	   
	}
	
	
	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_hr_nfc_man_editor, this, true);
        view.setLayoutParams(lp);
	}

	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		   if (bar_code.startsWith("FA:"))
	        {
			   
			   String e = this.txt_man_no_textcell.getContentText().trim();
				if (e == null || e.length() == 0) {
					App.Current.showError(this.getContext(), "请先扫描员工编码!!!");
					return;
				}
			  
			   loadmandtl1(bar_code.toString().split(":")[1],e);
	        }
	        else if (bar_code.startsWith("M"))
	        {
 
	        	loadmandtl(bar_code);
	        }
	}
	
	public void loadmandtl1(String fa_code,String hr_code)
	{
		this.ProgressDialog.show();
		pn_hr_nfc_man_editor.this.clearAll();
		String sql ="exec p_hr_get_fa_dtl ?,?";
		Parameters p  =new Parameters();
		p.add(1, hr_code).add(2, fa_code);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_hr_nfc_man_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_hr_nfc_man_editor.this.getContext(), result.Error);
					App.Current.playSound(R.raw.hook);
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					App.Current.playSound(R.raw.hook);
					App.Current.showError(pn_hr_nfc_man_editor.this.getContext(), "资产与人员信息不匹配，请注意!!!");
					pn_hr_nfc_man_editor.this.txt_image_textcell.setBackgroundDrawable(null);
					pn_hr_nfc_man_editor.this.clearAll();
				 
			 
					return;
				} 
					
				App.Current.showInfo(pn_hr_nfc_man_editor.this.getContext(), "登记出入成功!!!");
				pn_hr_nfc_man_editor.this.txt_image_textcell.setBackgroundDrawable(null);
				pn_hr_nfc_man_editor.this.clearAll();
	 
			 }
		});
	  
	}
	
	
	public void loadmandtl(String barcode)
	{
		this.ProgressDialog.show();
		pn_hr_nfc_man_editor.this.clearAll();
		String sql ="exec p_hr_get_man_dtl ?";
		Parameters p  =new Parameters();
		p.add(1, barcode);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_hr_nfc_man_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError) {
					App.Current.showError(pn_hr_nfc_man_editor.this.getContext(), result.Error);
					App.Current.playSound(R.raw.hook);
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					App.Current.playSound(R.raw.hook);
					App.Current.showError(pn_hr_nfc_man_editor.this.getContext(), "找不到此人。");
					pn_hr_nfc_man_editor.this.clearAll();
					//Bitmap bmpout1 =App.Current.ResourceManager.getImage("@/core_user_yellow");
					//BitmapDrawable bd1= new BitmapDrawable(bmpout1);
					pn_hr_nfc_man_editor.this.txt_image_textcell.setBackgroundDrawable(null);
					//pn_wo_issue_editor.this.Header.setTitleText("工单发料(无发料任务)");
					return;
				}
				_order_row = row;
				Bitmap bmpout =null;
				byte[] in =_order_row.getValue("img",byte[].class);
				if (in != null && in.length> 0)
				{
				  bmpout = BitmapFactory.decodeByteArray(in, 0, in.length);
				
				if (bmpout == null)
				{
					bmpout =App.Current.ResourceManager.getImage("@/core_user_yellow");	
				}
				} else
				{
					bmpout =App.Current.ResourceManager.getImage("@/core_user_yellow");
				}
				
				BitmapDrawable bd= new BitmapDrawable(bmpout);
				App.Current.playSound(R.raw.scan_beep);
				pn_hr_nfc_man_editor.this.txt_image_textcell.setBackgroundDrawable(bd);
				pn_hr_nfc_man_editor.this.txt_man_name_textcell.setContentText(_order_row.getValue("s_employname",""));
				pn_hr_nfc_man_editor.this.txt_man_no_textcell.setContentText(_order_row.getValue("employee_number",""));
				pn_hr_nfc_man_editor.this.txt_sex_textcell.setContentText(_order_row.getValue("s_sex",""));
				pn_hr_nfc_man_editor.this.txt_rz_datecell.setContentText(_order_row.getValue("c_meg_in_time",""));
				pn_hr_nfc_man_editor.this.txt_dept_name.setContentText(_order_row.getValue("dept_name",""));
				String sqlstr ="insert into  dbo.UserClock (userid,clock_date,clock_code,clock_add) select ?,getdate(),?,?";
				String ipstr=getLocalIpAddress();
				Parameters p1  =new Parameters();
				p1.add(1, _order_row.getValue("userid",0L)).add(2, App.Current.UserCode).add(3, ipstr);
				App.Current.DbPortal.ExecuteNonQuery(pn_hr_nfc_man_editor.this.Connector, sqlstr,p1);
				
			 }
		});
	  
	}
	
	
	public String getLocalIpAddress()  
    {  
        try  
        {  
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)  
            {  
               NetworkInterface intf = en.nextElement();  
               for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)  
               {  
                   InetAddress inetAddress = enumIpAddr.nextElement();  
                   if (!inetAddress.isLoopbackAddress())  
                   {  
                       return inetAddress.getHostAddress().toString();  
                   }  
               }  
           }  
        }  
        catch (SocketException ex)  
        {  
            Log.e("WifiPreference IpAddress", ex.toString());  
        }  
        return null;  
    } 
	
	
	
	@Override
	public void commit()
	{

	}
	
	public void clearAll()
	{
		pn_hr_nfc_man_editor.this.txt_man_name_textcell.setContentText("");
		pn_hr_nfc_man_editor.this.txt_man_no_textcell.setContentText("");
		pn_hr_nfc_man_editor.this.txt_sex_textcell.setContentText("");
		pn_hr_nfc_man_editor.this.txt_rz_datecell.setContentText("");	
		pn_hr_nfc_man_editor.this.txt_dept_name.setContentText("");
	}
	

}
