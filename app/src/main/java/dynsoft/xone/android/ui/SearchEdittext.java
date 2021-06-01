package dynsoft.xone.android.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import dynsoft.xone.android.core.R;

public class SearchEdittext extends android.support.v7.widget.AppCompatEditText {
    Drawable searchDrawable;
    Drawable scanDrawable;

    private EdittextDrawableClicklistener edittextDrawableClicklistener;

    public SearchEdittext(Context context) {
        super(context);
        init();
    }

    public SearchEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchEdittext(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        searchDrawable = getResources().getDrawable(R.drawable.search);
        scanDrawable = getResources().getDrawable(R.drawable.scanner);
        setCompoundDrawablesWithIntrinsicBounds(scanDrawable, null, searchDrawable, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
                float x = event.getX();
                int right = scanDrawable.getBounds().right;
                int left = searchDrawable.getBounds().left;
                if (x < right) {
                    edittextDrawableClicklistener.leftClicklistener(this, right);
                    return true;
                }
            Log.e("len", "Width : " + getWidth());
                if (x > getWidth() - left) {
                    edittextDrawableClicklistener.rightClicklistener(this, left);
                    return true;
                }
        }
        return super.onTouchEvent(event);
    }

    public void setEdittextDrawableClicklistener(EdittextDrawableClicklistener edittextDrawableClicklistener) {
        this.edittextDrawableClicklistener = edittextDrawableClicklistener;
    }
}
