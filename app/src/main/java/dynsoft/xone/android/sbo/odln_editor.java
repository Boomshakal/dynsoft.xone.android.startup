package dynsoft.xone.android.sbo;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;

public class odln_editor extends odoc_editor {

    public odln_editor(Context context) {
		super(context);
	}

	@Override
    public DocType getDocType()
    {
        return DocType.ODLN;
    }

    @Override
    public Map<Integer, DocType> getBaseTypes()
    {
        Map<Integer, DocType> map = new LinkedHashMap<Integer, DocType>();
        map.put(1, DocType.OQUT);
        map.put(2, DocType.ORDR);
        map.put(3, DocType.ORDN);
        map.put(4, DocType.OINV);
        return map;
    }
    
    @Override
    public void onScan(String barcode)
    {
        if (barcode == null) return;
        
        String itemCode = "";
        if (barcode.startsWith("CP-")) {
            String[] arr = barcode.split("-");
            if (arr.length == 4) {
                itemCode = arr[2];
            }
        } else {
            itemCode = barcode;
        }
        
        this.ItemCodeCell.setContentText(itemCode);
        this.chooseItemCode();
        
        if (this.LineRow != null) {
            this.BatchNumCell.setContentText(barcode);
        }
    }
}
