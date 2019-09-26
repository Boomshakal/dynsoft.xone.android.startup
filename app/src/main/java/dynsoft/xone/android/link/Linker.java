package dynsoft.xone.android.link;

import android.content.Context;
import android.view.View;
import dynsoft.xone.android.data.Parameters;

public abstract class Linker {

    public String Header;

    public abstract Object CreateObject(View source, Context context, Link link, Parameters parameters);

    public abstract Object Open(View source, Context context, Link link, Parameters parameters);
}
