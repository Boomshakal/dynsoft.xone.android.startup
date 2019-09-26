package dynsoft.xone.android.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import dynsoft.xone.android.core.R;

/**
 * Created by Administrator on 2018/12/13.
 */

public class PercImageView extends View {
    private int allLong;

    public PercImageView(Context context) {
        this(context, null, 0);
    }

    public PercImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PercImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
