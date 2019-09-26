package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

public class pn_po_return_editor extends pn_editor {

	public pn_po_return_editor(Context context) {
		super(context);
	}

	public TextCell txt_iqc_code_cell;
	public TextCell txt_shipment_code_cell;
	public TextCell txt_item_code_cell;
	public TextCell txt_item_name_cell;
	public TextCell txt_vendor_name_cell;
	public TextCell txt_vendor_model_cell;
	public TextCell txt_org_name_cell;
	public TextCell txt_lot_number_cell;
	public TextCell txt_quantity_cell;
	public TextCell txt_location_cell;

	@Override
	public void onPrepared() {
	 
		super.onPrepared();
		
		this.txt_iqc_code_cell = (TextCell)this.findViewById(R.id.txt_iqc_code_cell);
		this.txt_shipment_code_cell = (TextCell)this.findViewById(R.id.txt_shipment_code_cell);
		this.txt_item_code_cell = (TextCell)this.findViewById(R.id.txt_item_code_cell);
		this.txt_item_name_cell = (TextCell)this.findViewById(R.id.txt_item_name_cell);
		this.txt_org_name_cell = (TextCell)this.findViewById(R.id.txt_org_name_cell);
		this.txt_vendor_name_cell = (TextCell)this.findViewById(R.id.txt_vendor_name_cell);
		this.txt_vendor_model_cell = (TextCell)this.findViewById(R.id.txt_vendor_model_cell);
		this.txt_lot_number_cell = (TextCell)this.findViewById(R.id.txt_lot_number_cell);
		this.txt_quantity_cell = (TextCell)this.findViewById(R.id.txt_quantity_cell);
		this.txt_location_cell = (TextCell)this.findViewById(R.id.txt_location_cell);
		
		if (this.txt_iqc_code_cell != null) {
			this.txt_iqc_code_cell.setLabelText("���鵥��");
			this.txt_iqc_code_cell.setReadOnly();
		}
		
		if (this.txt_shipment_code_cell != null) {
			this.txt_shipment_code_cell.setLabelText("���˵���");
			this.txt_shipment_code_cell.setReadOnly();
		}
		
		if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("���ϱ���");
			this.txt_item_code_cell.setReadOnly();
		}
		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("��������");
			this.txt_item_name_cell.setReadOnly();
		}
		
		if (this.txt_org_name_cell != null) {
			this.txt_org_name_cell.setLabelText("��֯");
			this.txt_org_name_cell.setReadOnly();
		}
		
		if (this.txt_vendor_name_cell != null) {
			this.txt_vendor_name_cell.setLabelText("��Ӧ��");
			this.txt_vendor_name_cell.setReadOnly();
		}
		
		if (this.txt_vendor_model_cell != null) {
			this.txt_vendor_model_cell.setLabelText("�����ͺ�");
			this.txt_vendor_model_cell.setReadOnly();
		}
		
		if (this.txt_lot_number_cell != null) {
			this.txt_lot_number_cell.setLabelText("����");
			this.txt_lot_number_cell.setReadOnly();
		}
		
		if (this.txt_quantity_cell != null) {
			this.txt_quantity_cell.setLabelText("����");
			this.txt_quantity_cell.setReadOnly();
		}
		
		if (this.txt_location_cell != null) {
			this.txt_location_cell.setLabelText("���촢λ");
			this.txt_location_cell.setReadOnly();
		}
		
		this.loadRejectInfo();
		
		String lot_number = this.Parameters.get("lot_number", "");
		if (lot_number != null && lot_number.length() > 0) {
			this.loadLotNumber(lot_number);
		}
	}
	
	@Override
	public void setContentView()
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_po_return_editor, this, true);
        view.setLayoutParams(lp);
	}
	
	@Override
	public void onScan(final String barcode)
	{
		final String bar_code = barcode.trim();
		
		//ɨ����������
		if (bar_code.startsWith("CRQ:")) {
			
			App.Current.question(pn_po_return_editor.this.getContext(), "������û�б��Ϊ�ܾ����Σ�ȷ��Ҫ�˻���", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int pos = bar_code.indexOf("-");
					String lot = bar_code.substring(4, pos);
					pn_po_return_editor.this.txt_lot_number_cell.setContentText(lot);
					pn_po_return_editor.this.loadLotNumber(lot);
				}
			});
			
			return;
		}

		if (bar_code.startsWith("C:")){
			String lot = bar_code.substring(2, bar_code.length());
			this.txt_lot_number_cell.setContentText(lot);
			this.loadLotNumber(lot);
		}
	}
	
	public void loadRejectInfo()
	{
		this.ProgressDialog.show();
		
		String sql = "SELECT COUNT(*) FROM dbo.mm_po_transaction WHERE type='PO_REJECT' AND status=N'���˻�'";
		App.Current.DbPortal.ExecuteScalarAsync(this.Connector, sql, null, new ResultHandler<Integer>(){
			@Override
			public void handleMessage(Message msg){
				pn_po_return_editor.this.ProgressDialog.dismiss();
				
				Result<Integer> result = this.Value;
				if (this.Value.HasError) {
					App.Current.showError(pn_po_return_editor.this.getContext(), result.Error);
					return;
				}
				
				if (result.Value > 0) {
					String info = "�ɹ��˻�("+ String.valueOf(result.Value) +"�����˻�)";
					pn_po_return_editor.this.Header.setTitleText(info);
				} else {
					pn_po_return_editor.this.Header.setTitleText("�ɹ��˻�(�޴��˻�)");
				}
			}
		});
	}
	
	public void loadLotNumber(String lotNumber)
	{
		this.ProgressDialog.show();
		
		String sql = "exec p_mm_po_return_get_item ?,?";
		Parameters p  =new Parameters().add(1, App.Current.UserID).add(2, lotNumber);
		App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>(){
			@Override
    	    public void handleMessage(Message msg) {
				pn_po_return_editor.this.ProgressDialog.dismiss();
				
				Result<DataRow> result = this.Value;
				if (result.HasError){
					App.Current.showError(pn_po_return_editor.this.getContext(), result.Error);
					return;
				}
				
				DataRow row = result.Value;
				if (row == null) {
					App.Current.showError(pn_po_return_editor.this.getContext(), "���˻�����û�и����š�");
					return;
				}
				
				String status = row.getValue("status", "");
				if (status.equals("���˻�") == false) {
					App.Current.showError(pn_po_return_editor.this.getContext(), "�����β��Ǵ��˻�״̬�������˻���");
					return;
				}
				
				int total = row.getValue("total", 0);
				if (total > 0) {
					String info = "�ɹ��˻�("+ String.valueOf(total) +"�����˻�)";
					pn_po_return_editor.this.Header.setTitleText(info);
				} else {
					pn_po_return_editor.this.Header.setTitleText("�ɹ��˻�(�޴��˻�)");
				}
				
				pn_po_return_editor.this.txt_iqc_code_cell.setTag(row);
				pn_po_return_editor.this.txt_iqc_code_cell.setContentText(row.getValue("iqc_code", ""));
				pn_po_return_editor.this.txt_shipment_code_cell.setContentText(row.getValue("iqc_code", ""));
				pn_po_return_editor.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
				pn_po_return_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
				
				String org = row.getValue("organization_code", "") + ", " + row.getValue("organization_name", "");
				pn_po_return_editor.this.txt_org_name_cell.setContentText(org);
				
				pn_po_return_editor.this.txt_vendor_name_cell.setContentText(row.getValue("vendor_name", ""));
				pn_po_return_editor.this.txt_vendor_model_cell.setContentText(row.getValue("vendor_model", ""));
				pn_po_return_editor.this.txt_lot_number_cell.setContentText(row.getValue("lot_number", ""));
				pn_po_return_editor.this.txt_quantity_cell.setContentText(App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##"));
				pn_po_return_editor.this.txt_location_cell.setContentText(row.getValue("locations", ""));
			}
		});
	}

	@Override
	public void commit()
	{
		final DataRow row = (DataRow)this.txt_iqc_code_cell.getTag();
		if (row == null) {
			App.Current.showError(this.getContext(), "û�д��˻����ݣ������ύ��");
			return;
		}
		
		final String quantity = this.txt_quantity_cell.getContentText();
		if (quantity == null || quantity.length() == 0) {
			App.Current.showError(this.getContext(), "û�����������������ύ��");
			return;
		}
		
		App.Current.question(this.getContext(), "ȷ��Ҫ�ύ��", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				
				Map<String, String> entry = new HashMap<String, String>();
				entry.put("code", "PDA-"+UUID.randomUUID().toString());
				entry.put("reject_id", String.valueOf(row.getValue("id", Integer.class)));
				entry.put("user_id", App.Current.UserID);
				entry.put("quantity", quantity);
				
				ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
				entries.add(entry);
				
				//����XML���ݣ��������洢����
				String xml = XmlHelper.createXml("po_returns", null, null, "po_return", entries);
				String sql = "exec p_mm_po_return_create_from_reject ?,?";
				Connection conn = App.Current.DbPortal.CreateConnection(pn_po_return_editor.this.Connector);
				CallableStatement stmt;
				try {
					stmt = conn.prepareCall(sql);
					stmt.setObject(1, xml);
					stmt.registerOutParameter(2, Types.VARCHAR);
					stmt.execute();
					
					String val = stmt.getString(2);
					if (val != null) {
						Result<String> rs = XmlHelper.parseResult(val);
						if (rs.HasError) {
							App.Current.showError(pn_po_return_editor.this.getContext(), rs.Error);
							return;
						}
						
						App.Current.toastInfo(pn_po_return_editor.this.getContext(), "�ύ�ɹ�");
						
						pn_po_return_editor.this.clear();
						pn_po_return_editor.this.loadRejectInfo();
					}
				} catch (SQLException e) {
					App.Current.showInfo(pn_po_return_editor.this.getContext(), e.getMessage());
					e.printStackTrace();
					pn_po_return_editor.this.clear();
				}
			}
		});
	}
	
	public void clear()
	{
		pn_po_return_editor.this.txt_iqc_code_cell.setTag(null);
		pn_po_return_editor.this.txt_iqc_code_cell.setContentText("");
		pn_po_return_editor.this.txt_shipment_code_cell.setContentText("");
		pn_po_return_editor.this.txt_item_code_cell.setContentText("");
		pn_po_return_editor.this.txt_item_name_cell.setContentText("");
		pn_po_return_editor.this.txt_vendor_name_cell.setContentText("");
		pn_po_return_editor.this.txt_vendor_model_cell.setContentText("");
		pn_po_return_editor.this.txt_lot_number_cell.setContentText("");
		pn_po_return_editor.this.txt_quantity_cell.setContentText("");
		pn_po_return_editor.this.txt_location_cell.setContentText("");
	}
}
