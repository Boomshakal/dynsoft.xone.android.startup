package dynsoft.xone.android.sbo;

import android.content.Context;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.link.Link;

public class ordr_mgr extends odoc_mgr {

    public ordr_mgr(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
    public DocType getDocType()
    {
        return DocType.ORDR;
    }
    
    @Override
    public void openItem(DataRow row)
    {
        Link link = new Link("pane://x:code=sbo_and_ordr_editor");
        link.Parameters.add("x:flag", row.getValue("DocEntry"));
        link.Parameters.add("DocEntry", row.getValue("DocEntry"));
        link.Open(this, this.getContext(), null);
    }
    
    @Override
    public void create()
    {
        Link link = new Link("pane://x:code=sbo_and_ordr_editor");
        link.Parameters.add("x:flag", -1);
        link.Parameters.add("DocEntry", -1);
        link.Open(this, this.getContext(), null);
    }
}
