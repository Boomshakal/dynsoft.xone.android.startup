package dynsoft.xone.android.control;

public interface IDataContext {

    public void setDataContext(Object data);
    
    public Object getDataContext();
    
    public void registerBinding(Binding binding);
    
    public Binding[] getRegisterBindings();
}
