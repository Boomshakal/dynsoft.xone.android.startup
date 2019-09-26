package dynsoft.xone.android.control;

import android.content.Context;
import android.util.AttributeSet;

public class LinearLayout extends android.widget.LinearLayout implements IDataContext {

    public LinearLayout(Context context) {
        super(context);
        this.DataContextWorker = new DataContextWorker();
    }

    public LinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.DataContextWorker = new DataContextWorker();
    }

    public LinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.DataContextWorker = new DataContextWorker();
    }

    public DataContextWorker DataContextWorker;
    
    @Override
    public void setDataContext(Object data) {
        this.DataContextWorker.setDataContext(data);
    }
    
    
    @Override
    public Object getDataContext() {
        return this.DataContextWorker.getDataContext();
    }

    @Override
    public void registerBinding(Binding binding) {
        this.DataContextWorker.registerBinding(binding);
    }

    @Override
    public Binding[] getRegisterBindings() {
        return this.DataContextWorker.getRegisterBindings();
    }

}
