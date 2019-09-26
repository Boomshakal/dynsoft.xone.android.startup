package dynsoft.xone.android.control;

import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class SwitchCell extends LabelCell {

    public SwitchCell(android.content.Context context) {
        super(context);
    }

    public SwitchCell(android.content.Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchCell(android.content.Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public CheckBox CheckBox;

    @Override
    public void initLayout()
    {
        super.initLayout();
        
        this.CheckBox = new CheckBox(this.getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2,-2);
        lp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        this.CheckBox.setLayoutParams(lp);
        
        this.addView(this.CheckBox);
    }
    
    public boolean isChecked()
    {
    	return this.CheckBox.isChecked();
    }
    
    public void setChecked(boolean checked)
    {
    	this.CheckBox.setChecked(checked);
    }
}
