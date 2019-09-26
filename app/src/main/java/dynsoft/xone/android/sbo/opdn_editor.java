package dynsoft.xone.android.sbo;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

public class opdn_editor extends odoc_editor {

    public opdn_editor(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
    public DocType getDocType()
    {
        return DocType.OPDN;
    }
    
    @Override
    public Map<Integer, DocType> getBaseTypes()
    {
        Map<Integer, DocType> map = new LinkedHashMap<Integer, DocType>();
        map.put(1, DocType.OPOR);
        map.put(2, DocType.OPCH);
        map.put(3, DocType.ORPD);
        return map;
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
                String poNumber = arr[2];
                String lineNum = arr[3];

                String sql = "select OPOR.DocEntry,DocNum,CardCode,CardName,DocStatus,LineNum,ItemCode,Dscription ItemName,OpenQty,OWHS.WhsCode,OWHS.WhsName,LineStatus from POR1 inner join OPOR on OPOR.DocEntry=POR1.DocEntry left join OWHS on OWHS.WhsCode=POR1.WhsCode where DocNum=? and LineNum=?";
                Parameters p = new Parameters().add(1, poNumber).add(2, lineNum);
                Result<DataRow> r = App.Current.DbPortal.ExecuteRecord(this.Connector, sql, p);
                if (r.Value != null) {
                    DataRow row = r.Value;
                    String docStatus = row.getValue("DocStatus", String.class);
                    if (docStatus.equals("O") == false) {
                        App.Current.showInfo(getContext(), "本批号所属采购订单已经结清，不能再对其进行收货。");
                        return;
                    }
                    
                    String lineStatus = row.getValue("LineStatus", String.class);
                    if (lineStatus.equals("O") == false) {
                        App.Current.showInfo(getContext(), "该批次物料对应的采购订单行已经结清，不能再对其进行收货。");
                        return;
                    }
                    
                    int docEntry = row.getValue("DocEntry", Integer.class);
                    if (this.BaseDocCell.TextBox.getTag() != null) {
                        int baseEntry = (Integer)this.BaseDocCell.TextBox.getTag();
                        if (baseEntry != docEntry) {
                            App.Current.showInfo(getContext(), "该单据不是当前正在收货的采购订单，请另开单据进行收货。");
                            return;
                        }
                    }
                    
                    String baseDoc = DocType.OPOR.TypeName + row.getValue("DocNum").toString();
                    this.BaseDocCell.setContentText(baseDoc);
                    this.BaseDocCell.setTag(DocType.OPOR);
                    this.BaseDocCell.TextBox.setTag(docEntry);
                    this.CardCodeCell.setContentText(row.getValue("CardCode", ""));
                    this.CardNameCell.setContentText(row.getValue("CardName", ""));
                    
//                    Object baseLine = row.getValue("LineNum");
//                    if (baseLine != null) {
//                        this.BaseLineCell.setContentText(baseLine.toString());
//                        this.BaseLineCell.setTag(baseLine);
//                    } else {
//                        this.BaseLineCell.setContentText("");
//                        this.BaseLineCell.setTag(null);
//                    }
//                    
//                    this.ItemCodeCell.setContentText(row.getValue("ItemCode", ""));
//                    this.ItemNameCell.setContentText(row.getValue("ItemName", ""));
//                    this.WhsCodeCell.setContentText(row.getValue("WhsName",""));
//                    this.WhsCodeCell.setTag(row.getValue("WhsCode"));
//                    this.QuantityCell.setContentText(App.formatNumber(row.getValue("OpenQty"), "0.##"));
//                    this.QuantityCell.TextBox.requestFocus();
//                    this.QuantityCell.TextBox.setSelection(this.QuantityCell.TextBox.getText().length());
//                    this.BatchNumCell.setContentText(barcode);
//                    
//                    if (this.MultiLayout.getVisibility() == View.VISIBLE) {
//                        this.switchLines();
//                    }
                }
            }
        } else {
            itemCode = barcode;
        }
        
        this.ItemCodeCell.setContentText(itemCode);
        this.chooseItemCode();
        this.BatchNumCell.setContentText(barcode);
    }
}
