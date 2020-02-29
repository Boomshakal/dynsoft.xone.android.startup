package dynsoft.xone.android.control;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import dynsoft.xone.android.core.App;

public class ItemLabelCell extends PaneCell {

	public static int LabelWidth = App.dpToPx(70);
	public static int LabelTextSize = 16;
	public static int LabelTextColor = Color.GRAY;
	public static int LabelContentSpace = App.dpToPx(10);
	public static int ContentTextSize = 16;
	public static int ContentTextColor = Color.BLACK;

	public TextView Label;

	public ItemLabelCell(Context context) {
		super(context);
	}

	public ItemLabelCell(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ItemLabelCell(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setLabelText(String text)
	{
		this.Label.setText(text);
	}

	public String getLabelText()
	{
		return this.Label.getText().toString();
	}

	public void setLabelWidth(int width)
	{
		this.Label.setWidth(width);
	}

	protected void initLayout()
	{
	    super.initLayout();

		this.Label = new TextView(Context);
		LayoutParams lp_label = new LayoutParams(-2,-2);
		lp_label.weight = 1;
		lp_label.gravity = Gravity.CENTER_VERTICAL;
		lp_label.rightMargin = LabelContentSpace;
		this.Label.setLayoutParams(lp_label);
		this.Label.setGravity(Gravity.RIGHT);
		this.Label.setTextSize(LabelTextSize);
		this.Label.setTextColor(LabelTextColor);
		this.addView(this.Label);
	}
}
