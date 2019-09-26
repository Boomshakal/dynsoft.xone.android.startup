package dynsoft.xone.android.sbo;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;

public class orpd_editor extends odoc_editor {

    public orpd_editor(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
    public DocType getDocType()
    {
        return DocType.ORPD;
    }

    @Override
    public Map<Integer, DocType> getBaseTypes()
    {
        Map<Integer, DocType> map = new LinkedHashMap<Integer, DocType>();
        map.put(1, DocType.OPDN);
        return map;
    }
    
    @Override
    public void onScan(String barcode)
    {
    	
    }
}
