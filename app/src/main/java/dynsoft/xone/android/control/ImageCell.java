package dynsoft.xone.android.control;

import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageCell extends LabelCell {
    
    public ImageView ImageView;

    public ImageCell(android.content.Context context) {
        super(context);
    }

    public ImageCell(android.content.Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageCell(android.content.Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void initLayout()
    {
        super.initLayout();
        
        this.ImageView = new ImageView(this.getContext());
        
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
        lp.gravity = Gravity.CENTER_VERTICAL;
        this.ImageView.setLayoutParams(lp);
        
        this.addView(this.ImageView);
    }
}
