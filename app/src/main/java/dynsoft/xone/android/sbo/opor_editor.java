package dynsoft.xone.android.sbo;

import android.content.Context;

public class opor_editor extends odoc_editor {

    public opor_editor(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
    public DocType getDocType()
    {
        return DocType.OPOR;
    }

    @Override
    public void onScan(String barcode)
    {
    
    }
}
