package dynsoft.xone.android.core;

import java.util.Map;

public abstract class Service {
    
    public String Code;
    
    public String Pack;

    public abstract void onStart(Map<String, String> configs);
    
    public abstract void onStop();
}
