package dynsoft.xone.android.sbo;

import android.content.Context;

public class oqut_editor extends odoc_editor {

    public oqut_editor(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
    public DocType getDocType()
    {
        return DocType.OQUT;
    }

    @Override
    public void onScan(String barcode)
    {
        this.ItemCodeCell.setContentText(barcode);
        this.chooseItemCode();
    }
}
