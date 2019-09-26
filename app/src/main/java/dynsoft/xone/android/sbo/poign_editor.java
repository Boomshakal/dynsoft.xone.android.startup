package dynsoft.xone.android.sbo;

import android.content.Context;
import android.view.View;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

public class poign_editor extends poig_editor {
    
    public poign_editor(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
    public void onPrepared()
    {
        super.onPrepared();
        
        if (this.ItemCodeCell != null) {
            this.ItemCodeCell.Button.setVisibility(View.GONE);
        }
    }
    
    @Override
    public DocType getDocType()
    {
        return DocType.OIGN;
    }

    @Override
    public void showBaseDoc(DataRow row)
    {
        super.showBaseDoc(row);
        
        this.ItemCodeCell.setContentText(row.getValue("ItemCode", ""));
        this.ItemNameCell.setContentText(row.getValue("ItemName", ""));
        this.WhsCodeCell.setContentText(row.getValue("WhsName", ""));
        this.WhsCodeCell.setTag(row.getValue("WhsCode"));
        this.QuantityCell.setContentText(App.formatNumber(row.getValue("Quantity"), "0.##"));
        this.QuantityCell.TextBox.requestFocus();
        
        this.LineRow = row;
    }
    
    @Override
    public void onScan(String barcode)
    {
        if (barcode == null) return;
        
        if (barcode.startsWith("CP-")) {
            String[] arr = barcode.split("-");
            if (arr.length == 3) {
                String docNum = arr[1];
                //String itemCode = arr[2];

                String sql = "select OWOR.DocEntry,DocNum,OWOR.ItemCode,ItemName,Warehouse WhsCode,OWHS.WhsName,(PlannedQty-CmpltQty) Quantity,Status,ManSerNum,ManBtchNum,ManOutOnly from OWOR "
                           + "inner join OITM on OITM.ItemCode=OWOR.ItemCode inner join OWHS on OWHS.WhsCode=OWOR.Warehouse where DocNum=?";
                Parameters p = new Parameters().add(1, docNum);
                Result<DataRow> r = App.Current.DbPortal.ExecuteRecord(this.Connector, sql, p);
                if (r.Value != null) {
                    DataRow row = r.Value;
                    String docStatus = row.getValue("Status", String.class);
                    if (docStatus.equals("L") == true) {
                        App.Current.showInfo(getContext(), "本批号所属生产订单已经结清，不能再对其进行收货。");
                        return;
                    } else if (docStatus.equals("P") == true) {
                        App.Current.showInfo(getContext(), "本批号所属生产订单尚未批准，不能对其进行收货。");
                        return;
                    }
                    
                      int docEntry = row.getValue("DocEntry", Integer.class);
//                    if (this.BaseDocCell.TextBox.getTag() != null) {
//                        int baseEntry = (Integer)this.BaseDocCell.TextBox.getTag();
//                        if (baseEntry != docEntry) {
//                            App.Current.showInfo(getContext(), "该单据不是当前正在收货的生产订单，请另开单据进行收货。");
//                            return;
//                        }
//                    }
                    
                    String baseDoc = DocType.OWOR.TypeName + row.getValue("DocNum").toString();
                    this.BaseDocCell.setContentText(baseDoc);
                    this.BaseDocCell.setTag(DocType.OWOR);
                    this.BaseDocCell.TextBox.setTag(docEntry);
                    
                    this.LineRow = new DataRow();
                    this.LineRow.setValue("ManSerNum", row.getValue("ManSerNum", ""));
                    this.LineRow.setValue("ManBtchNum", row.getValue("ManBtchNum", ""));
                    this.LineRow.setValue("ManOutOnly", row.getValue("ManOutOnly", ""));
                    this.LineRow.setValue("ItemCode", row.getValue("ItemCode", ""));
                    this.LineRow.setValue("ItemName", row.getValue("ItemName", ""));
                    this.LineRow.setValue("WhsCode", row.getValue("WhsCode", ""));
                    this.LineRow.setValue("WhsName", row.getValue("WhsName", ""));
                    
                    this.ItemCodeCell.setContentText(row.getValue("ItemCode", ""));
                    this.ItemNameCell.setContentText(row.getValue("ItemName", ""));
                    this.WhsCodeCell.setContentText(row.getValue("WhsName",""));
                    this.WhsCodeCell.setTag(row.getValue("WhsCode"));
                    
                    String manSerial = row.getValue("ManSerNum", "N");
                    if (manSerial.equals("Y")) {
                        this.QuantityCell.setContentText("1");
                    } else {
                        this.QuantityCell.setContentText(App.formatNumber(row.getValue("Quantity"), "0.##"));
                    }
                    this.QuantityCell.TextBox.requestFocus();
                    this.QuantityCell.TextBox.setSelection(this.QuantityCell.TextBox.getText().length());
                    this.BatchNumCell.setContentText(barcode);
                    
                    if (this.MultiLayout.getVisibility() == View.VISIBLE) {
                        this.switchLines();
                    }
                }
            }
        }
    }
    
    @Override
    public void addDocLine(Document doc, String itemCode, String baseType, String baseEntry, String baseLine, String quantity, String whsCode, String freetxt)
    {
        doc.addLine("", baseType, baseEntry, baseLine, quantity, whsCode, freetxt);
    }
    
    public void addDocSerial(Document doc, String lineNum, String number)
    {
        doc.addSerial(lineNum, number, false);
    }
}
