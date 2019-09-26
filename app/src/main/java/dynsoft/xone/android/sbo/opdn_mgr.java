package dynsoft.xone.android.sbo;

import android.content.Context;
import dynsoft.xone.android.link.PaneLinker;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.link.Link;

public class opdn_mgr extends odoc_mgr {

    public opdn_mgr(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
    public DocType getDocType()
    {
        return DocType.OPDN;
    }
    
    @Override
    public void openItem(DataRow row)
    {
        Link link = new Link("pane://x:code=sbo_and_opdn_editor");
        link.Parameters.add(PaneLinker.KEY_XFLAG, row.getValue("DocEntry"));
        link.Parameters.add("DocEntry", row.getValue("DocEntry"));
        link.Open(this, this.getContext(), null);
    }
    
    @Override
    public void create()
    {
        Link link = new Link("pane://x:code=sbo_and_opdn_editor");
        link.Parameters.add(PaneLinker.KEY_XFLAG, -1);
        link.Parameters.add("DocEntry", -1);
        link.Open(this, this.getContext(), null);
    }
    
}
