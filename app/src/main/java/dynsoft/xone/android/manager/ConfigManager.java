package dynsoft.xone.android.manager;

import android.util.Log;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

public class ConfigManager {

	public Parameters getParameters(String code)
    {
		Parameters params = new Parameters();
		
        String sql = "select key,value from core_config where code=?";
        Parameters p = new Parameters().add(1, code);
        Result<DataTable> r = App.Current.BookConnector.ExecuteDataTable(sql, p);
        if (r.HasError) {
            Log.i("dynsoft.xone.android.core.manager.ConfigManager.getValue", r.Error);
        }
        
        if (r.Value != null && r.Value.Rows.size() > 0) {
        	for (DataRow row : r.Value.Rows) {
        		String key = row.getValue("key", "");
        		String val = row.getValue("value", "");
        		if (key != null && key.length() > 0) {
        			params.add(key, val);
        		}
        	}
        }
        
        return params;
    }
	
    public String getValue(String code, String key)
    {
        String sql = "select value from core_config where code=? and [key]=?";
        Parameters p = new Parameters().add(1, code).add(2, key);
        Result<String> r = App.Current.BookConnector.ExecuteScalar(sql, p, String.class);
        if (r.HasError) {
            Log.i("dynsoft.xone.android.core.manager.ConfigManager.getValue", r.Error);
        }
        return r.Value;
    }
}
