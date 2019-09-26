package dynsoft.xone.android.manager;

import java.util.LinkedHashMap;
import java.util.Map;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.data.Connector;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataSet;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

public class ConnectorManager {

	private Map<String,Connector> _connectors;
	
	public ConnectorManager()
	{
		_connectors = new LinkedHashMap<String,Connector>();
	}
	
	public Connector GetConnector(String code)
    {
		Connector connector = _connectors.get(code);
		if (connector == null) {
            String sql = "select * from core_connector where code=?;select * from core_connection where connector=? and network=?";
            Parameters p = new Parameters().add(1, code).add(2, code).add(3, App.Current.Network.Code);
            Result<DataSet> r = App.Current.DbPortal.ExecuteDataSet(App.Current.BookConnector, sql, p);
            if (r.Value != null) {
            	DataTable tConnector = r.Value.Tables.get(0);
            	DataTable tConnection = r.Value.Tables.get(1);
                DataRow row = tConnector.Rows.get(0);
                String clss = row.getValue("clss", String.class);
                if (clss != null && clss.length() > 0) {
                    connector = (Connector)App.Current.ClassManager.createObject(clss);
                    if (connector != null) {
                        connector.Code = row.getValue("code", String.class);
                        connector.Pack = row.getValue("pack", String.class);
                        connector.LoginServer = App.Current.Server.Address;
                        connector.ConnString = row.getValue("connstr", String.class);
                        connector.ClassName = row.getValue("clss", String.class);
                        
                        DataRow rc = tConnection.Rows.get(0);
                        if (rc != null) {
                        	connector.Network = App.Current.Network.Code;
                            connector.ViaMode = rc.getValue("via", Integer.class);
                            connector.DbServer = rc.getValue("dbserver", String.class);
                            connector.DbPort = rc.getValue("dbport", String.class);
                            connector.DbName = rc.getValue("dbname", String.class);
                            connector.DbUser = rc.getValue("dbuser", String.class);
                            connector.DbPassword = rc.getValue("dbpassword", String.class);
                        } 
                        
                        _connectors.put(connector.Code, connector);
                    }
                }
            }
        }
		return connector;
    }
}
