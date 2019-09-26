package dynsoft.xone.android.control;

import dynsoft.xone.android.core.R;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class TextCell extends LabelCell {

	public EditText TextBox;
	public boolean EnterToNext;
	
	public TextCell(Context context) {
		super(context);
	}

	public TextCell(android.content.Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TextCell(android.content.Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setContentText(String text)
	{
		this.TextBox.setText(text);
	}
	
	public String getContentText()
	{
		return this.TextBox.getText().toString();
	}
	
	public void setReadOnly()
	{
		this.TextBox.setKeyListener(null);
	}
	
	@Override
	protected void initLayout()
	{
		super.initLayout();
		
		this.EnterToNext = true;
		this.TextBox = new EditText(Context);
		this.TextBox.setBackgroundResource(R.drawable.bg_textbox_editable);
		this.TextBox.setPadding(0, 0, 0, 0);

		LinearLayout.LayoutParams lp_text = new LinearLayout.LayoutParams(-2,-2);
		lp_text.weight = 1;
		lp_text.gravity = Gravity.CENTER_VERTICAL;
		this.TextBox.setLayoutParams(lp_text);
		this.TextBox.setTextSize(ContentTextSize);
		this.TextBox.setTextColor(ContentTextColor);
//		this.TextBox.setOnKeyListener(new OnKeyListener(){
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                
//                if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                    if (TextCell.this.EnterToNext) {
//                        View view = TextCell.this.TextBox.focusSearch(View.FOCUS_FORWARD);
//                        if (view != null) {
//                            view.requestFocus();
//                            return true;
//                        }
//                    }
//                }
//                
//                return false;
//            }
//        });

		this.addView(TextBox);
	}

}
