package dynsoft.xone.android.sbo;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import dynsoft.xone.android.adapter.PageTableAdapter;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

public class owtr_editor extends odoc_editor {

	public owtr_editor(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ButtonTextCell FillerCell;
	public PageTableAdapter FillerAdapter;
	public Dialog FillerDialog;
	
	@Override
	public void onPrepared()
	{
		this.FillerCell = (ButtonTextCell)this.Elements.get("filler").Object;
		
		if (this.FillerCell != null) {
            this.FillerCell.setLabelText("转出仓库");
            this.FillerCell.TextBox.setKeyListener(null);
            this.FillerCell.TextBox.setFocusable(false);
            this.FillerCell.Button.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                	owtr_editor.this.chooseFiller();
                }
            });
        }
		
		super.onPrepared();
	}
	
	public void chooseFiller()
    {
        if (this.FillerAdapter == null) {
            this.FillerAdapter = new PageTableAdapter(getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (position < owtr_editor.this.FillerAdapter.DataTable.Rows.size()) {
                        if (convertView == owtr_editor.this.FillerAdapter.Footer) 
                            convertView = null;
                        
                        DataRow row = (DataRow)owtr_editor.this.FillerAdapter.getItem(position);
                        if (convertView == null) {
                            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.odoc_owhs, null);
                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_owhs_gray"));
                        }

                        TextView num = (TextView)convertView.findViewById(R.id.num);
                        TextView code = (TextView)convertView.findViewById(R.id.txtWhsCode);
                        TextView name = (TextView)convertView.findViewById(R.id.txtWhsName);
                        
                        num.setText(String.valueOf(position + 1));
                        code.setText(row.getValue("WhsCode", ""));
                        name.setText(row.getValue("WhsName", ""));

                        return convertView;
                    } else {
                        return owtr_editor.this.FillerAdapter.Footer;
                    }
                }
            };
        }
        
        this.FillerAdapter.DataTable = null;
        this.FillerAdapter.PageIndex = 0;
        this.FillerAdapter.PageSize = 10;
        this.refreshFiller();
        
        if (this.FillerAdapter.DataTable.Rows.size() > 0) {
            if (this.FillerAdapter.DataTable.Rows.size() > 1) {
                if (this.FillerDialog == null) {
                    this.FillerDialog = new AlertDialog.Builder(getContext())
                    .setTitle("选择转出仓库")
                    .setSingleChoiceItems(this.FillerAdapter, 0, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which < owtr_editor.this.FillerAdapter.DataTable.Rows.size()) {
                                DataRow row = (DataRow)((AlertDialog)dialog).getListView().getAdapter().getItem(which);
                                if (row != null) {
                                	owtr_editor.this.showFiller(row);
                                }
                                dialog.dismiss();
                            } else {
                            	owtr_editor.this.refreshFiller();
                            	owtr_editor.this.FillerAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .setNegativeButton("取消", null).create();
                }

                this.FillerAdapter.notifyDataSetChanged();
                this.FillerDialog.show();
            } else {
                this.showFiller(this.FillerAdapter.DataTable.Rows.get(0));
            }
        }
    }
    
    public void showFiller(DataRow row)
    {
    	owtr_editor.this.FillerCell.setContentText(row.getValue("WhsName", ""));
    	owtr_editor.this.FillerCell.setTag(row.getValue("WhsCode"));
    }
    
    public void refreshFiller()
    {
        int top = this.FillerAdapter.PageSize*(this.FillerAdapter.PageIndex+1);
        int start = (this.FillerAdapter.PageSize*this.FillerAdapter.PageIndex)+1;
        int end = this.FillerAdapter.PageSize*(this.FillerAdapter.PageIndex+1);
        
        String sql = "with temp as (select top (?) ROW_NUMBER() over (order by WhsCode) Number,WhsCode,WhsName from OWHS) select * from temp where Number>=? and Number<=?";
        Parameters p = new Parameters().add(1,top).add(2,start).add(3,end);
        Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (r.Value != null && r.Value.Rows.size() > 0) {
            if (this.FillerAdapter.DataTable != null) {
                for (DataRow row : r.Value.Rows) {
                    this.FillerAdapter.DataTable.Rows.add(row);
                }
            } else {
                this.FillerAdapter.DataTable = r.Value;
            }
            
            this.FillerAdapter.PageIndex += 1;
        } else if (r.HasError) {
            App.Current.showError(getContext(), r.Error);
        }
    }
	
	@Override
    public DocType getDocType()
    {
        return DocType.OWTR;
    }
    
    @Override
    public void onScan(String barcode)
    {
    	if (barcode == null) return;
        
        String itemCode = "";
        if (barcode.startsWith("YL-")) {
            String[] arr = barcode.split("-");
            if (arr.length == 5) {
                itemCode = arr[1];
                
                this.ItemCodeCell.setContentText(itemCode);
                this.BatchNumCell.setContentText(barcode);
            }
        }
    }
    
    @Override
    public boolean checkMain()
    {
        if (this.FillerCell != null) {
            String filler = this.FillerCell.TextBox.getText().toString();
            if (filler == null || filler.length() == 0) {
                App.Current.showWarning(getContext(), "请先选择转出仓库。");
                this.FillerCell.TextBox.requestFocus();
                return false;
            }
        }
        
        return super.checkMain();
    }
    
    @Override
    public void setDocBody(Document doc, String cardCode, String docDate, String dueDate, String taxDate, String comments)
    {
        //doc.setBody(cardCode, docDate, dueDate, taxDate, comments);

    	Map<String, String> body = new HashMap<String, String>();
    	body.put("CardCode", cardCode);
    	body.put("DocDate", docDate);
    	//body.put("DocDueDate", dueDate);
    	body.put("TaxDate", taxDate);
    	body.put("Comments", comments);
    	body.put("FromWarehouse", this.FillerCell.getTag().toString());
    	doc.setBody(body);
    }
}
