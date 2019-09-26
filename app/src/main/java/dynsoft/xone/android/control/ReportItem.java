package dynsoft.xone.android.control;

import android.content.Context;

public class ReportItem {
    
    public Context Context;
    
    public String Code;
    
    public String Title;
    
    public String Url;
    
    public ReportItem(Context context)
    {
        this.Context = context;
    }

    public void setCode(String code)
    {
        this.Code = code;
    }
    
    public void setTitle(String title)
    {
        this.Title = title;
    }
    
    public void setUrl(String url)
    {
        this.Url = url;
    }
}
