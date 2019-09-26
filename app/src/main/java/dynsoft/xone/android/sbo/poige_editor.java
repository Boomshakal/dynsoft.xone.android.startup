package dynsoft.xone.android.sbo;

import android.content.Context;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

public class poige_editor extends poig_editor {

    public poige_editor(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
    public DocType getDocType()
    {
        return DocType.OIGE;
    }
    
    @Override
    public void refreshItemCode()
    {
        if (this.BaseDocCell.TextBox.getTag() == null) {
            App.Current.showWarning(getContext(), "请选择生产订单。");
            return;
        }
        
        int baseEntry = (Integer)this.BaseDocCell.TextBox.getTag();
        String itemCode = this.ItemCodeCell.getContentText();
        int top = this.ItemCodeAdapter.PageSize*(this.ItemCodeAdapter.PageIndex+1);
        int start = (this.ItemCodeAdapter.PageSize*this.ItemCodeAdapter.PageIndex)+1;
        int end = this.ItemCodeAdapter.PageSize*(this.ItemCodeAdapter.PageIndex+1);
        
        String sql = "with temp as ( select top (?) ROW_NUMBER() over (order by LineNum) N,"
                   + "LineNum,WOR1.ItemCode,ItemName,(PlannedQty-IssuedQty) Quantity,wareHouse WhsCode,WhsName from WOR1 "
                   + "inner join OITM on OITM.ItemCode=WOR1.ItemCode "
                   + "left join OWHS on OWHS.WhsCode=WOR1.wareHouse "
                   + "where WOR1.DocEntry=? and WOR1.IssueType='M' and (WOR1.PlannedQty-IssuedQty)>0 and WOR1.ItemCode like ?+'%') select * from temp where N>=? and N<=?";
        Parameters p = new Parameters().add(1, top).add(2, baseEntry).add(3, itemCode).add(4,start).add(5,end);
        Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (r.Value != null && r.Value.Rows.size() > 0) {
            if (this.ItemCodeAdapter.DataTable != null) {
                for (DataRow row : r.Value.Rows) {
                    this.ItemCodeAdapter.DataTable.Rows.add(row);
                }
            } else {
                this.ItemCodeAdapter.DataTable = r.Value;
            }
            
            this.ItemCodeAdapter.PageIndex += 1;
        } else if (r.HasError) {
            App.Current.showError(getContext(), r.Error);
        }
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
            }
        } else {
            itemCode = barcode;
        }
        
        this.ItemCodeCell.setContentText(itemCode);
        this.chooseItemCode();
        this.BatchNumCell.setContentText(barcode);
    }
    
    public void addDocLine(Document doc, String itemCode, String baseType, String baseEntry, String baseLine, String quantity, String whsCode, String freetxt)
    {
        doc.addLine("", baseType, baseEntry, baseLine, quantity, whsCode, freetxt);
    }
}
