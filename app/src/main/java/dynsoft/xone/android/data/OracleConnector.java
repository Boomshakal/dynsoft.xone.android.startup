package dynsoft.xone.android.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleConnector extends Connector {
	
	static {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class not found for Oracle Driver.", e);
		}
	}

	@Override
	public String GetConnectionString() {
	    String server = this.DbServer;
	    String port = this.DbPort;
	    if (port == null || port.length() == 0) {
	        port = this.DefPort;
	    }
	    
		if (this.ConnString != null && this.ConnString.length() > 0) {
			this.ConnString = this.ConnString.replace("{dbserver}", server);
			this.ConnString = this.ConnString.replace("{dbport}", port);
			this.ConnString = this.ConnString.replace("{dbname}", this.DbName);
			this.ConnString = this.ConnString.replace("{dbuser}", this.DbUser);
            this.ConnString = this.ConnString.replace("{dbpassword}", this.DbPassword);
		} else {
			return "jdbc:oracle:thin:@"+server+":"+port+":"+this.DbName;
		}
		return this.ConnString;
	}

	@Override
	public Connection CreateConnection() throws SQLException {
		String connStr = this.GetConnectionString();
		Connection connection = DriverManager.getConnection(connStr, DbUser, DbPassword);
		return connection;
	}

	@Override
	public String ProcessParameters(String sql, Parameters parameters) {
		return super.ProcessParameters(sql, parameters);
	}
}
