package dynsoft.xone.android.start;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.data.*;
import dynsoft.xone.android.link.Link;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;

public class FrmForm extends Activity {

    @Override
    protected void onCreate(Bundle bundle) {
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
            StrictMode.setThreadPolicy(policy);
        }
        
        super.onCreate(bundle);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Link link = null;
        String link_uuid = this.getIntent().getStringExtra("link_uuid");
        if (link_uuid != null) {
            link = (Link)App.Current.FormParameters.get(link_uuid);
            App.Current.FormParameters.remove(link_uuid);
        }
        
        View source = null;
        String source_uuid = this.getIntent().getStringExtra("source_uuid");
        if (source_uuid != null) {
            source = (View)App.Current.FormParameters.get(source_uuid);
            App.Current.FormParameters.remove(source_uuid);
        }
        
        Parameters parameters = null;
        String param_uuid = this.getIntent().getStringExtra("param_uuid");
        if (param_uuid != null) {
            parameters = (Parameters)App.Current.FormParameters.get(param_uuid);
            App.Current.FormParameters.remove(param_uuid);
        }
        
        link.Object = link.CreateObject(source, this, parameters);
        Pane pane = (Pane)link.Object;
        if (pane != null) {
            this.setContentView(pane);
        }
    }
}
