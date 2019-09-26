package dynsoft.xone.android.start;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.ImageView.ScaleType;
import dynsoft.xone.android.base.BasePane;
import dynsoft.xone.android.control.PaneFooter;
import dynsoft.xone.android.control.PaneHeader;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.control.TreeViewItem;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.wms.pn_po_shipment_mgr;

public class PnWorkSpace extends Pane {

	public PnWorkSpace(Context context) {
		super(context);
	}

	public LinearLayout Root;
    public PaneHeader Header;
    public ScrollView Scroll;
    public LinearLayout Body;
    public PaneFooter Footer;
    public ImageButton CloseButton;
    public ImageButton OpenButton;
    
    
    
	@Override
    public void onPrepared() {
        super.onPrepared();
        
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-1); 
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_workspace, null);
        view.setLayoutParams(lp);
        
        this.addView(view);
        
        this.Header = (PaneHeader)this.findViewById(R.id.header);
        this.Scroll = (ScrollView)this.findViewById(R.id.scroll);
        this.Body = (LinearLayout)this.findViewById(R.id.body);
        this.Footer = (PaneFooter)this.findViewById(R.id.footer);
         
        TreeViewItem tvi_mm_po_shipment = (TreeViewItem)this.findViewById(R.id.tvi_mm_po_shipment);
        TreeViewItem tvi_mm_po_receive = (TreeViewItem)this.findViewById(R.id.tvi_mm_po_receive);
        TreeViewItem tvi_mm_po_accept = (TreeViewItem)this.findViewById(R.id.tvi_mm_po_accept);
        TreeViewItem tvi_mm_po_reject = (TreeViewItem)this.findViewById(R.id.tvi_mm_po_reject);
        TreeViewItem tvi_mm_po_entry = (TreeViewItem)this.findViewById(R.id.tvi_mm_po_entry);
        TreeViewItem tvi_mm_po_return = (TreeViewItem)this.findViewById(R.id.tvi_mm_po_return);
        
        tvi_mm_po_shipment.setTitle("采购发运");
        tvi_mm_po_shipment.setIcon(App.Current.ResourceManager.getImage("@/core_doc_gray"));
        tvi_mm_po_shipment.setLinkUrl("pane://x:code=mm_and_po_shipment_mgr");
        
        tvi_mm_po_receive.setTitle("采购接收");
        tvi_mm_po_receive.setIcon(App.Current.ResourceManager.getImage("@/core_doc_gray"));
        tvi_mm_po_receive.setLinkUrl("pane://x:code=mm_and_po_transaction_mgr;x:flag=采购接收;type=PO_RECEIVE");
        
        tvi_mm_po_accept.setTitle("采购接受");
        tvi_mm_po_accept.setIcon(App.Current.ResourceManager.getImage("@/core_doc_gray"));
        tvi_mm_po_accept.setLinkUrl("pane://x:code=mm_and_po_transaction_mgr;x:flag=采购接受;type=PO_ACCEPT");
        
        tvi_mm_po_reject.setTitle("采购拒绝");
        tvi_mm_po_reject.setIcon(App.Current.ResourceManager.getImage("@/core_doc_gray"));
        tvi_mm_po_reject.setLinkUrl("pane://x:code=mm_and_po_transaction_mgr;x:flag=采购拒绝;type=PO_REJECT");
        
        tvi_mm_po_entry.setTitle("采购入库");
        tvi_mm_po_entry.setIcon(App.Current.ResourceManager.getImage("@/core_doc_gray"));
        tvi_mm_po_entry.setLinkUrl("pane://x:code=mm_and_po_transaction_mgr;x:flag=采购入库;type=PO_ENTRY");
        
        tvi_mm_po_return.setTitle("采购退货");
        tvi_mm_po_return.setIcon(App.Current.ResourceManager.getImage("@/core_doc_gray"));
        tvi_mm_po_return.setLinkUrl("pane://x:code=mm_and_po_transaction_mgr;x:flag=采购退货;type=PO_RETURN");
        
        this.CloseButton = (ImageButton)this.findViewById(R.id.btn_exit);
        this.OpenButton = (ImageButton)this.findViewById(R.id.btn_info);
        
        if (this.Root != null) {
            LinearLayout.LayoutParams lp_root = new LinearLayout.LayoutParams(-1,-1);
            this.Root.setLayoutParams(lp_root);
            this.Root.setOrientation(LinearLayout.VERTICAL);
            this.Root.setBackgroundResource(R.drawable.bg_pane);
        }
        
        if (this.Header != null) {
            this.Header.Pane = this;
            
            LinearLayout.LayoutParams lp_header = new LinearLayout.LayoutParams(-1,-2);
            lp_header.height = PaneHeader.HeaderHeight;
            this.Header.setLayoutParams(lp_header);
            this.Header.setIconImage(App.Current.ResourceManager.getImage(this.Icon));
            this.Header.setMenuImage(App.Current.ResourceManager.getImage("@/core_menu_white"));
            this.Header.setTitleText("WMS");
        }

        if (Footer != null) {
            LinearLayout.LayoutParams lp_footer = new LinearLayout.LayoutParams(-1,-2);
            lp_footer.height = PaneFooter.FooterHeight;
            Footer.setLayoutParams(lp_footer);
            //Footer.setBackgroundResource(R.drawable.bg_footer);
            
            int childCount = Footer.getChildCount();
            if (childCount > 0) {
                LinearLayout.LayoutParams lp_cmd = new LinearLayout.LayoutParams(-2,-2);
                lp_cmd.gravity = Gravity.CENTER_VERTICAL;
                lp_cmd.weight = 1;
                lp_cmd.height = PaneFooter.ButtonHeight;
                lp_cmd.width = PaneFooter.ButtonWidth;
                
                for (int i=0; i<childCount;i++) {
                    ImageButton cmd = (ImageButton)Footer.getChildAt(i);
                    if (cmd != null) {
                        cmd.setLayoutParams(lp_cmd);
                        cmd.setBackgroundDrawable(null);
                        cmd.setScaleType(ScaleType.FIT_CENTER);
                        cmd.setPadding(0, 0, 0, 0);
                    }
                }
            }
        }
        
        if (this.CloseButton != null) {
            this.CloseButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_exit_white"));
            this.CloseButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    
                }
            });
        }
        
        if (this.OpenButton != null) {
            this.OpenButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_open_white"));
            this.OpenButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                   
                }
            });
        }
        
    }
}
