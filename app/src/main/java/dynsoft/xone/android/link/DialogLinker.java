package dynsoft.xone.android.link;

import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.start.*;
import dynsoft.xone.android.data.Parameters;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;

public class DialogLinker extends Linker {

    @Override
    public Object CreateObject(View source, Context context, Link link, Parameters parameters) {
        Object vl = source.getTag(R.id.element);
        Pane pane = new Pane(source.getContext());

        return new AlertDialog.Builder(source.getContext())
        .setView(pane)
        .setOnCancelListener(null)
        .setPositiveButton("", new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                
            }
            
        }).create();
    }

    @Override
    public Object Open(View source, Context context, Link link, Parameters parameters) {
        Object vl = source.getTag(R.id.element);
        Pane pane = new Pane(source.getContext());
        
        AlertDialog dialog = new AlertDialog.Builder(source.getContext())
        .setView(pane)
        .setOnCancelListener(null)
        .setPositiveButton("", new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                
            }
            
        }).create();
        dialog.show();
        return dialog;
    }
}
