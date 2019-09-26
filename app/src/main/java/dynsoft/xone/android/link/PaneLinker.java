package dynsoft.xone.android.link;

import android.content.Context;
import android.view.View;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.core.Workbench;
import dynsoft.xone.android.start.*;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

public class PaneLinker extends Linker {

    public final static String KEY_XCODE = "x:code";
    public final static String KEY_XFLAG = "x:flag";
    public final static String KEY_XNEW = "x:new";
    
    @Override
    public Object CreateObject(View source, Context context, Link link, Parameters parameters) {

        Pane pane = null;
        
        String code = (String)link.Parameters.get(KEY_XCODE);
        if (code == null || code.length() == 0) return null;
        
        boolean isnew = false;
        String xnew = (String)link.Parameters.get(KEY_XNEW);
        if (xnew != null && xnew.length() > 0) {
            isnew = Boolean.parseBoolean(xnew);
        }
        
        if (isnew == false) {
            Object flag = link.Parameters.get(KEY_XFLAG);
            pane = ((Workbench)App.Current.Workbench).getPane(code, flag);
        }
        
        if (pane == null) {
            String sql = "select class from core_pane where code=?";
            Parameters pc = new Parameters();
            pc.put(1, code);

            Result<DataRow> r = App.Current.DbPortal.ExecuteRecord(App.Current.BookConnector, sql, pc);
            if (r != null && r.Value != null) {
                
                String className = r.Value.getValue("class", String.class);
                if (className != null && className.length() > 0) {
                    pane = (Pane)App.Current.ClassManager.createObject(className, Context.class, context);
                } else {
                    pane = new Pane(context);
                }

                if (pane != null) {
                    pane.Code = code;
                    
                    link.ProcessExpressions(source, parameters);
                    pane.Parameters.putAll(link.Parameters);
                    pane.Initialize();
                }
            }
        }

        link.Object = pane;
        return pane;
    }

    @Override
    public Object Open(View source, Context context, Link link, Parameters parameters) {
        
        if (link.Object == null) {
            link.Object = this.CreateObject(source, context, link, parameters);
        }
        
        Pane pane = (Pane)link.Object;
        if (pane != null) {
        	((Workbench)App.Current.Workbench).showPane(pane);
        }
        
        return link.Object;
    }

}
