package dynsoft.xone.android.sbo;

import android.content.Context;

public class ordr_editor extends odoc_editor {
    
    public ordr_editor(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
    public DocType getDocType()
    {
        return DocType.ORDR;
    }

    @Override
    public void setDocBody(Document doc, String cardCode, String docDate, String dueDate, String taxDate, String comments)
    {
        if (docDate != null) docDate = docDate.replace("-", "");
        if (dueDate != null) dueDate = dueDate.replace("-", "");
        if (taxDate != null) taxDate = taxDate.replace("-", "");
        
        doc.setBody(cardCode, docDate, dueDate, taxDate, comments);
    }
    
    @Override
    public void onScan(String barcode)
    {
        this.ItemCodeCell.setContentText(barcode);
        this.chooseItemCode();
    }
}
