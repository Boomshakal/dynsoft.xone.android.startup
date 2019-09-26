package dynsoft.xone.android.link;


import java.util.UUID;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.start.*;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

public class FormLinker extends Linker {

    public final static String CORE_KEY_XCODE = "x:code";
    public final static String CORE_KEY_XFLAG = "x:flag";
    
    @Override
    public Object CreateObject(View source, Context context, Link link, Parameters parameters) {

        String code = (String)link.Parameters.get(CORE_KEY_XCODE);
        if (code == null || code.length() == 0) return null;
        
        String sql = "select * from core_pane where code=?";
        Parameters pc = new Parameters();
        pc.put(1, code);

        Result<DataRow> r = App.Current.DbPortal.ExecuteRecord(App.Current.BookConnector, sql, pc);
        if (r != null && r.Value != null) {
            Pane pane = null;
            String className = r.Value.getValue("class", String.class);
            if (className != null && className.length() > 0) {
                pane = (Pane)App.Current.ClassManager.createObject(className, Context.class, context);
            } else {
                pane = new Pane(context);
            }

            if (pane != null) {
                pane.Code = code;
                pane.Initialize();
                
                //pane.Title = row.getValue("Title", String.class);
                //pane.Context = owner;
                //pane.FrameDefinition = result;
                
//                String imageName = row.getValue("ImageName", String.class);
//                if (imageName != null && imageName.length() > 0) {
//                    frame.Icon = Core.ResourceManager.GetImage(imageName);
//                }
                
//                frame.Flag = parameters[CORE_KEY_XFLAG];
//                frame.Margin = (parameters["x:margin"] as string).ToThickness(new Thickness(0));
//                frame.Padding = (parameters["x:padding"] as string).ToThickness(new Thickness(0));
//                frame.BorderThickness = (parameters["x:border"] as string).ToThickness(new Thickness(0));
//                frame.BorderBrush = (parameters["x:bordercolor"] as string).ToBrush(Brushes.Transparent);
//                frame.Background = (parameters["x:background"] as string).ToBrush(frame.Background);
//                frame.ToolBarTrayAlignment = (parameters["x:toolbarposition"] as string).ToEnumValue<VerticalAlignment>(VerticalAlignment.Top);
//                frame.FontFamily = (parameters["x:font"] as string).ToFontFamily(Core.Workbench.FontFamily);
//                frame.FontSize = (parameters["x:fontsize"] as string).ToDouble(Core.Workbench.FontSize);

//                foreach (var item in parameters) {
//                    frame.Parameters[item.Key] = item.Value;
//                }
            }
            
            return pane;
        }
        
        return null;
    }

    @Override
    public Object Open(View source, Context context, Link link, Parameters parameters) {
        
        if (source != null) {
            Intent intent = new Intent();

            UUID link_uuid = UUID.randomUUID();
            App.Current.FormParameters.put(link_uuid.toString(), link);
            intent.putExtra("link_uuid", link_uuid.toString());
            
            UUID source_uuid = UUID.randomUUID();
            App.Current.FormParameters.put(source_uuid.toString(), source);
            intent.putExtra("source_uuid", source_uuid.toString());
            
            if (parameters != null) {
                UUID param_uuid = UUID.randomUUID();
                App.Current.FormParameters.put(param_uuid.toString(), parameters);
                intent.putExtra("param_uuid", param_uuid.toString());
            }
            
            intent.setClass(context, FrmForm.class);
            context.startActivity(intent);
        }
        
        return null;
    }
}
