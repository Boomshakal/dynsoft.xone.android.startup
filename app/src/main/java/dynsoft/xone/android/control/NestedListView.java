package dynsoft.xone.android.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.view.View.OnTouchListener;
import android.widget.AbsListView.OnScrollListener;

public class NestedListView extends ListView implements OnTouchListener {
    
    public ScrollView ScrollView;

    public NestedListView(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    public NestedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }
    
    public NestedListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            // Disallow ScrollView to intercept touch events.
            this.ScrollView.requestDisallowInterceptTouchEvent(true);
            break;

        case MotionEvent.ACTION_UP:
            // Allow ScrollView to intercept touch events.
            this.ScrollView.requestDisallowInterceptTouchEvent(false);
            break;
        }

        // Handle LinesListView touch events.
        v.onTouchEvent(event);
        return true;
    }
}
