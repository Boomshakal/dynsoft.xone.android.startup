package dynsoft.xone.android.sbo;

import android.content.Context;

public class oign_editor extends odoc_editor {

    public oign_editor(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
    public DocType getDocType()
    {
        return DocType.OIGN;
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
}
