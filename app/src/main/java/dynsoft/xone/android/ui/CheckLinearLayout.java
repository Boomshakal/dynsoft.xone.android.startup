package dynsoft.xone.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Checkable;

/**
 * Created by Administrator on 2018/1/8.
 */

public class CheckLinearLayout extends LinearLayout implements Checkable {
    private boolean mChecked;

    public CheckLinearLayout(Context context) {
        super(context);
    }

    public CheckLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setChecked(boolean b) {
        if(b != mChecked) {
            mChecked = b;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }
}
