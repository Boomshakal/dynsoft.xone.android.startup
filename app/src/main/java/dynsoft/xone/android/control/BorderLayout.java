package dynsoft.xone.android.control;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class BorderLayout extends LinearLayout {

	private int _borderColor;
	private int _leftBorder;
	private int _topBorder;
	private int _rightBorder;
	private int _bottomBorder;
	
	public BorderLayout(Context context) {
        super(context);
        setWillNotDraw(false);
    }
    public BorderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }
    
	public BorderLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setWillNotDraw(false);
	}
	
//	private Activity getActivity() {
//	    Context context = getContext();
//	    while (context instanceof ContextWrapper) {
//	        if (context instanceof Activity) {
//	            return (Activity)context;
//	        }
//	        context = ((ContextWrapper)context).getBaseContext();
//	    }
//	    return null;
//	}
    
    @Override
    protected void onDraw(Canvas canvas) {

    	if (_leftBorder>0 || _topBorder > 0 || _rightBorder > 0 || _bottomBorder > 0) {
    		
    		Rect r = canvas.getClipBounds() ;
            Paint strokePaint = new Paint();
            strokePaint.setColor(_borderColor);
            strokePaint.setStyle(Paint.Style.STROKE);
            
            if (_leftBorder > 0) {
            	strokePaint.setStrokeWidth(_leftBorder);  
                canvas.drawLine(0, r.bottom, 0, 0, strokePaint);
            }
            
            if (_topBorder > 0) {
            	strokePaint.setStrokeWidth(_topBorder);  
                canvas.drawLine(0, 0, r.right, 0, strokePaint);
            }
            
            if (_rightBorder > 0) {
            	strokePaint.setStrokeWidth(_rightBorder);  
                canvas.drawLine(r.right-1, 0, r.right-1, r.bottom, strokePaint);
            }
            
            if (_bottomBorder > 0) {
            	strokePaint.setStrokeWidth(_bottomBorder);  
                canvas.drawLine(r.right, r.bottom-1, 0, r.bottom-1, strokePaint);
            }
    	}
    }

    
    public void setBorderColor(int color)
    {
        _borderColor = color;
        this.invalidate();
    }
    
    public void setBorderThickness(int left, int top, int right, int bottom)
    {
    	_leftBorder = left;
    	_topBorder = top;
    	_rightBorder = right;
    	_bottomBorder = bottom;
    	this.invalidate();
    }
}
