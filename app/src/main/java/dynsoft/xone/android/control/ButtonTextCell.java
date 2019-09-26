package dynsoft.xone.android.control;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

public class ButtonTextCell extends TextCell {
	
	public static int ButtonWidth = App.dpToPx(32);
	public static int ButtonHeight = App.dpToPx(32);

	public ButtonTextCell(Context context) {
		super(context);
	}
	
	public ButtonTextCell(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ButtonTextCell(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);
	}

	public ImageButton Button;
	
	public void setButtonImage(Bitmap bitmap)
	{
		this.Button.setImageBitmap(bitmap);
	}

	@Override
	public void initLayout()
	{
		super.initLayout();
		
		this.Button = new ImageButton(Context);
		this.Button.setBackgroundColor(Color.TRANSPARENT);
		this.Button.setPadding(0, 0, 0, 0);
		
		if (App.Current.ResourceManager != null) {
		    this.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_downward_light"));
		}

		LinearLayout.LayoutParams lp_button = new LinearLayout.LayoutParams(-2,-2);
		lp_button.gravity = Gravity.CENTER_VERTICAL;
		lp_button.width = ButtonWidth;
		lp_button.height = ButtonHeight;
		lp_button.leftMargin = 10;
		this.Button.setLayoutParams(lp_button);
		this.Button.setScaleType(ScaleType.FIT_CENTER);
		this.Button.setFocusable(false);

		this.addView(this.Button);
	}
}
