package dynsoft.xone.android.control;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;

public class TextChexkbox extends ItemLabelCell {

	public CheckBox checkBox;
	public boolean checked;

	public TextChexkbox(Context context) {
		super(context);
	}

	public TextChexkbox(android.content.Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TextChexkbox(android.content.Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);
	}

//	public void setContentText(String text)
//	{
//		this.checkBox.setText(text);
//	}

	public String getContentText()
	{
		return this.Label.getText().toString();
	}

	public void setReadOnly()
	{
		this.checkBox.setKeyListener(null);
	}

	public void setChecked(boolean checked) {
		this.checkBox.setChecked(checked);
	}

	@Override
	protected void initLayout()
	{
		super.initLayout();

		this.checked = true;
		this.checkBox = new CheckBox(Context);
		this.checkBox.setBackgroundResource(R.drawable.bg_textbox_editable);
		this.checkBox.setPadding(0, 0, 0, 0);

		android.widget.LinearLayout.LayoutParams lp_text = new LinearLayout.LayoutParams(-2,-2);
		lp_text.weight = 1;
		lp_text.gravity = Gravity.CENTER_VERTICAL;
		this.checkBox.setLayoutParams(lp_text);
		this.checkBox.setTextSize(ContentTextSize);
		this.checkBox.setTextColor(ContentTextColor);
		this.Label.setTextColor(ContentTextColor);
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

		this.addView(checkBox);
	}

}
