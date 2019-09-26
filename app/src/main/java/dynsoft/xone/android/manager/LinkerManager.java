package dynsoft.xone.android.manager;

import java.util.LinkedHashMap;
import java.util.Map;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.link.Linker;

public class LinkerManager {

    private Map<String, Linker> _linkers;
    
    public LinkerManager()
    {
        _linkers = new LinkedHashMap<String, Linker>();
    }

    public void RegisterLinker(Linker linker)
    {
        if (linker == null) return;
        if (_linkers.containsKey(linker.Header) == false) {
            _linkers.put(linker.Header, linker);
        }
    }

    public Linker GetLinker(String header)
    {
        if (header.length() > 0) {
            Linker linker = _linkers.get(header);
            if (linker == null) {
                String sql = "select header,class from core_linker where header=? and pltfrm=?";
                Parameters p = new Parameters().add(1, header).add(2, App.Current.Platform);
                Result<DataRow> r = App.Current.DbPortal.ExecuteRecord(App.Current.BookConnector, sql, p);
                if (r.Value != null) {
                    String className = r.Value.getValue("class", String.class);
                    if (className != null && className.length() > 0) {
                        linker = (Linker)App.Current.ClassManager.createObject(className);
                        if (linker != null) {
                            linker.Header = header;
                            _linkers.put(header, linker);
                        }
                    }
                }
            }
            return linker;
        }
        return null;
    }
}
