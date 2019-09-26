package dynsoft.xone.android.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import android.util.Log;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Service;
import dynsoft.xone.android.data.Connector;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

public class ServiceManager {

    private Map<String, Service> _services;
    
    public ServiceManager()
    {
        _services = new LinkedHashMap<String, Service>();
    }
    
    public void loadServices()
    {
        String sql = "select * from core_service where pltfrm=?";
        Parameters p = new Parameters().add(1, App.Current.Platform);
        Result<DataRow> r = App.Current.DbPortal.ExecuteRecord(App.Current.BookConnector, sql, p);
        if (r.Value != null) {
            DataRow row = r.Value;
            String clss = row.getValue("class", String.class);
            if (clss != null && clss.length() > 0) {
                String code = row.getValue("code", "");
                Service service = (Service)App.Current.ClassManager.createObject(clss);
                if (service != null) {
                    Map<String, String> map = null;
                    String config = row.getValue("config", "");
                    if (config.length() > 0) {
                        sql = "select * from core_config where code=?";
                        p.clearAll().add(1, config);
                        Result<DataTable> rs = App.Current.DbPortal.ExecuteDataTable(App.Current.BookConnector, sql, p);
                        if (rs.Value != null && rs.Value.Rows.size() > 0) {
                            map = new HashMap<String,String>();
                            
                            for (DataRow rw : rs.Value.Rows) {
                                map.put(rw.getValue("key", ""), rw.getValue("value", ""));
                            }
                        }
                    }
                    
                    _services.put(code, service);
                    service.Code = code;
                    service.Pack = row.getValue("pack", "");
                    service.onStart(map);
                } else {
                    Log.e(ServiceManager.class.getName(), "Create instance of \"" + clss + "\" return null." );
                }
            }
        } else if (r.HasError) {
            Log.e(ServiceManager.class.getName(), "Load Services failed. " + r.Error);
        }
    }
    
    public void stopServices()
    {
        Collection<Service> services = _services.values();
        for (Service service : services) {
            service.onStop();
        }
    }
    
    public Service getService(String code)
    {
        if (_services.containsKey(code)) {
            return _services.get(code);
        }
        return null;
    }
    
}
