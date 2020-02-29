package dynsoft.xone.android.util;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class DrawableUtil {

    /**
     * TextViewËÄÖÜdrawableµÄÐòºÅ¡£
     * 0 left,  1 top, 2 right, 3 bottom
     */
    private final int LEFT = 0;
    private final int RIGHT = 2;

    private OnDrawableListener listener;
    private EditText mEditText;

    public DrawableUtil(EditText textView, OnDrawableListener l) {
        mEditText = textView;
        mEditText.setOnTouchListener(mOnTouchListener);
        listener = l;
    }

    public interface OnDrawableListener {
        public void onLeft(View v, Drawable left);

        public void onRight(View v, Drawable right);
    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (listener != null) {
                        Drawable drawableLeft = mEditText.getCompoundDrawables()[LEFT];
                        if (drawableLeft != null && event.getRawX() <= (mEditText.getLeft() + drawableLeft.getBounds().width())) {
                            listener.onLeft(v, drawableLeft);
                            return true;
                        }

                        Drawable drawableRight = mEditText.getCompoundDrawables()[RIGHT];
                        if (drawableRight != null && event.getRawX() >= (mEditText.getRight() - drawableRight.getBounds().width())) {
                            listener.onRight(v, drawableRight);
                            return true;
                        }
                    }
                    break;
            }
            return false;
        }

    };
}
