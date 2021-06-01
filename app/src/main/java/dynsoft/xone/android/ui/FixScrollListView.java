package dynsoft.xone.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class FixScrollListView extends ListView {
    public FixScrollListView(Context context) {
        this(context, null);
    }

    public FixScrollListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FixScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // ���һ���ϴ��ֵ��AT_MOSTģʽ
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        // ����ԭ��������
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int i = MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.UNSPECIFIED);
//        setMeasuredDimension(widthMeasureSpec, i + 200);
    }

}
