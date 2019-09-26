package dynsoft.xone.android.core;

import dynsoft.xone.android.data.Connector;

public class ConnectorInfo {

	public String Code;

    public String Pack;

    public String Platform;

    public String Network;

    public int ViaMode;
    
    public String DefPort;
    
    public String DbServer;

    public String DbPort;

    public String DbName;

    public String DbUser;

    public String DbPassword;

    public String ConnString;

    public String ClassName;
    
    public void AssignConnector(Connector connector)
    {
    	if (connector != null){
    		connector.Code = this.Code;
    		connector.Pack = this.Pack;
    		connector.Platform = this.Platform;
    		connector.ViaMode = this.ViaMode;
    		connector.DefPort = this.DefPort;
    		connector.DbServer = this.DbServer;
    		connector.DbPort = this.DbPort;
    		connector.DbName = this.DbName;
    		connector.DbUser = this.DbUser;
    		connector.DbPassword = this.DbPassword;
    		connector.ConnString = this.ConnString;
    		connector.ClassName = this.ClassName;
    	}
    }
}
