package dynsoft.xone.android.sbo;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import dynsoft.xone.android.adapter.PageTableAdapter;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

public class poig_editor extends odoc_editor {
    
    public poig_editor(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
    public Map<Integer, DocType> getBaseTypes()
    {
        Map<Integer, DocType> types = new LinkedHashMap<Integer, DocType>();
        types.put(DocType.OWOR.TypeID, DocType.OWOR);
        return types;
    }
    
    @Override
    public void chooseBaseDoc()
    {
        if (this.BaseDocAdapter == null) {
            this.BaseDocAdapter = new PageTableAdapter(getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (position < poig_editor.this.BaseDocAdapter.DataTable.Rows.size()) {
                        if (convertView == poig_editor.this.BaseDocAdapter.Footer) 
                            convertView = null;
                        
                        DataRow row = (DataRow)poig_editor.this.BaseDocAdapter.getItem(position);
                        if (convertView == null) {
                            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ostk, null);
                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
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
                        docnum.setText(row.getValue("DocNum").toString());
                        date.setText(App.formatDateTime(row.getValue("DueDate"), "yyyy-MM-dd"));
                        status.setText(row.getValue("Status", ""));
                        itemcode.setText(row.getValue("ItemCode", ""));
                        itemname.setText(row.getValue("ItemName", ""));
                        //user.setText(row.getValue("U_NAME", ""));
                        quantity.setText("QTY: " + App.formatNumber(row.getValue("Quantity"), "0.##"));

                        return convertView;
                    } else {
                        return poig_editor.this.BaseDocAdapter.Footer;
                    }
                }
            };
        }
        
        this.BaseDocAdapter.DataTable = null;
        this.BaseDocAdapter.PageIndex = 0;
        this.BaseDocAdapter.PageSize = 10;
        this.refreshBaseDoc();
        
        if (this.BaseDocAdapter.DataTable != null && this.BaseDocAdapter.DataTable.Rows.size() > 0) {
            if (this.BaseDocAdapter.DataTable.Rows.size() > 1) {
                if (this.BaseDocDialog == null) {
                    this.BaseDocDialog = new AlertDialog.Builder(getContext())
                    .setTitle("选择生产订单")
                    .setSingleChoiceItems(this.BaseDocAdapter, 0, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which < poig_editor.this.BaseDocAdapter.DataTable.Rows.size()) {
                                DataRow row = (DataRow)((AlertDialog)dialog).getListView().getAdapter().getItem(which);
                                if (row != null) {
                                    poig_editor.this.showBaseDoc(row);
                                }
                                dialog.dismiss();
                            } else {
                                poig_editor.this.refreshBaseDoc();
                                poig_editor.this.BaseDocAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .setNegativeButton("取消", null).create();
                }

                this.BaseDocAdapter.notifyDataSetChanged();
                this.BaseDocDialog.show();
            } else {
                this.showBaseDoc(this.BaseDocAdapter.DataTable.Rows.get(0));
            }
        }
    }
    
    @Override
    public void showBaseDoc(final DataRow row)
    {
        if (this.BaseDocCell != null) {
            final Integer baseEntry = (Integer)row.getValue("DocEntry");
            
            if (this.BaseDocCell.TextBox.getTag() != null) {
                int entry = (Integer)this.BaseDocCell.TextBox.getTag();
                
                if (entry != baseEntry) {
                    if (poig_editor.this.LinesAdapter != null) {
                        poig_editor.this.LinesAdapter.DataTable = null;
                        poig_editor.this.LinesAdapter.notifyDataSetChanged();
                    }
                    poig_editor.this.resetLine();
                    
                    if (baseEntry != null) {
                        poig_editor.this.BaseDocCell.setTag(DocType.OWOR);
                        poig_editor.this.BaseDocCell.setContentText("生产订单" + row.getValue("DocNum").toString());
                        poig_editor.this.BaseDocCell.TextBox.setTag(baseEntry);
                    } else {
                        poig_editor.this.BaseDocCell.setContentText("");
                        poig_editor.this.BaseDocCell.setTag(null);
                        poig_editor.this.BaseDocCell.TextBox.setTag(null);
                    }
                    
//                    App.Current.question(getContext(), "选择不同的来源单据，将会清空已输入的行，确定要继续吗？", new DialogInterface.OnClickListener(){
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            
//                        }
//                    });
                }
            } else {
                if (baseEntry != null) {
                    this.BaseDocCell.setTag(DocType.OWOR);
                    this.BaseDocCell.setContentText("生产订单" + row.getValue("DocNum").toString());
                    this.BaseDocCell.TextBox.setTag(baseEntry);
                } else {
                    this.BaseDocCell.setContentText("");
                    this.BaseDocCell.setTag(null);
                    this.BaseDocCell.TextBox.setTag(null);
                }
            }
        }
    }
    
    @Override
    public void refreshBaseDoc()
    {
        int top = this.BaseDocAdapter.PageSize*(this.BaseDocAdapter.PageIndex+1);
        int start = (this.BaseDocAdapter.PageSize*this.BaseDocAdapter.PageIndex)+1;
        int end = this.BaseDocAdapter.PageSize*(this.BaseDocAdapter.PageIndex+1);
        
        String sql = "with temp as(select top (?) ROW_NUMBER() over (order by OWOR.DocEntry desc) N,"
                   + "OWOR.DocEntry,DocNum,DueDate,Status,OITM.ItemCode,OITM.ItemName,"
                   + "(PlannedQty-CmpltQty) Quantity,Warehouse WhsCode,OWHS.WhsName from OWOR "
                   + "inner join OITM on OITM.ItemCode=OWOR.ItemCode "
                   + "left join OWHS on OWHS.WhsCode=OWOR.Warehouse where Status='R') select * from temp where N>=? and N<=?";
        Parameters p = new Parameters().add(1, top).add(2, start).add(3, end);
        Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (r.Value != null && r.Value.Rows.size() > 0) {
            if (this.BaseDocAdapter.DataTable != null) {
                for (DataRow row : r.Value.Rows) {
                    this.BaseDocAdapter.DataTable.Rows.add(row);
                }
            } else {
                this.BaseDocAdapter.DataTable = r.Value;
            }
            
            this.BaseDocAdapter.PageIndex += 1;
        } else if (r.HasError) {
            App.Current.showError(getContext(), r.Error);
        }
    }
}
