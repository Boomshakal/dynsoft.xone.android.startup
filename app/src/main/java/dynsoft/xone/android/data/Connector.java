package dynsoft.xone.android.data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

public class Connector {

	public String Code;

    public String Pack;

    public String Platform;

    public String Network;

    public int ViaMode;
    
    public String DefPort;

    public String LoginServer;

    public String DbServer;

    public String DbPort;

    public String DbName;

    public String DbUser;

    public String DbPassword;

    public String ConnString;

    public String ClassName;
    
    public Connector Connector;
    
    public String GetConnectionString()
    {
    	return this.ConnString;
    }
    
    public Connection CreateConnection() throws SQLException
    {
    	return null;
    }
    
    public DbSession CreateDbSession() throws SQLException
    {
    	Connection conn = this.CreateConnection();
    	if (conn != null) {
    		return new DbSession(conn);
    	}
    	return null;
    }
    
    public String ProcessParameters(String sql, Parameters parameters)
    {
    	return sql;
    }
    
    private void CloseConnection(Connection connection)
	{
		if (connection != null) {
    		try {
    			connection.close();
			} catch (SQLException e) {
			}
    	}
	}

	private void PrepareParameters(PreparedStatement statement, Parameters parameters) throws SQLException {
		Iterator<Entry<Object, Object>> iterator = parameters.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<Object, Object> entry = iterator.next();
			statement.setObject((Integer)entry.getKey(), entry.getValue());
		}
	}
	
	public Result<Integer> ExecuteNonQuery(String sql)
	{
		return this.ExecuteNonQuery(sql, null);
	}
	
    public Result<Integer> ExecuteNonQuery(String sql, Parameters parameters)
	{
    	sql = this.ProcessParameters(sql, parameters);
		Result<Integer> result = new Result<Integer>();
		java.sql.Connection conn = null;
		int rows = 0;
        try {
        	conn = this.CreateConnection();
        	if (conn != null) {
        		if (parameters != null) {
        			PreparedStatement stmt = conn.prepareStatement(sql);
        			this.PrepareParameters(stmt, parameters);
        			rows = stmt.executeUpdate();
        		} else {
        			Statement stmt = conn.createStatement();
        			
        			rows = stmt.executeUpdate(sql);
        		}
        		
        		result.Value = rows;
        	}
        } catch (SQLException e) {
        	result.Error = e.getMessage();
        	result.HasError = true;
        } finally {
        	this.CloseConnection(conn);
        }
        
		return result;
	}


	public void ExecuteNonQueryAsync(final String sql, final ResultHandler<Integer> handler)
	{
		this.ExecuteNonQueryAsync(sql, null, handler);
	}
	
	public void ExecuteNonQueryAsync(final String sql, final Parameters parameters, final ResultHandler<Integer> handler)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Result<Integer> r = Connector.this.ExecuteNonQuery(sql, parameters);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
			
		}).start();
	}
	
	public <T> Result<T> ExecuteScalar(String sql, Class<T> clazz)
	{
		return this.ExecuteScalar(sql, null, clazz);
	}
	
	@SuppressWarnings("unchecked")
    public <T> Result<T> ExecuteScalar(String sql, Parameters parameters, Class<T> clazz)
	{
		sql = this.ProcessParameters(sql, parameters);
		Result<T> result = new Result<T>();
		Connection conn = null;
        try {
        	conn = this.CreateConnection();
        	if (conn != null) {
        		ResultSet rs = null;
        		if (parameters != null) {
        			//CallableStatement stmt = conn.prepareCall(sql);
        			PreparedStatement stmt = conn.prepareStatement(sql);
        			this.PrepareParameters(stmt, parameters);
        			rs = stmt.executeQuery();
        		} else {
        			Statement stmt = conn.createStatement();
        			rs = stmt.executeQuery(sql);
        		}
        		
        		if (rs != null) {
        			if (rs.next()) {
        				result.Value = (T)rs.getObject(1);
        			}
        		}
        	}
        } catch (Exception e) {
        	result.Error = e.getMessage();
        	result.HasError = true;
        } finally {
        	this.CloseConnection(conn);
        }
        
		return result;
	}

	public Result<Object> ExecuteScalar(String sql)
	{
		return ExecuteScalar(sql, (Parameters)null);
	}
	
	public Result<Object> ExecuteScalar(String sql, Parameters parameters)
	{
		sql = this.ProcessParameters(sql, parameters);
		Result<Object> result = new Result<Object>();
		Connection conn = null;
        try {
        	conn = this.CreateConnection();
        	if (conn != null) {
        		ResultSet rs = null;
        		if (parameters != null) {
        			//CallableStatement stmt = conn.prepareCall(sql);
        			PreparedStatement stmt = conn.prepareStatement(sql);
        			this.PrepareParameters(stmt, parameters);
        			rs = stmt.executeQuery();
        		} else {
        			Statement stmt = conn.createStatement();
        			rs = stmt.executeQuery(sql);
        		}
        		
        		if (rs != null) {
        			if (rs.next()) {
        				result.Value = rs.getObject(1);
        			}
        		}
        	}
        } catch (Exception e) {
        	result.Error = e.getMessage();
        	result.HasError = true;
        } finally {
        	this.CloseConnection(conn);
        }
        
		return result;
	}
	
	public <T> void ExecuteScalarAsync(final String sql, final Class<T> clazz, final ResultHandler<T> handler)
	{
		ExecuteScalarAsync(sql, null, clazz, handler);
	}
	
	public <T> void ExecuteScalarAsync(final String sql, final Parameters parameters, final Class<T> clazz, final ResultHandler<T> handler)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Result<T> r = Connector.this.ExecuteScalar(sql, parameters, clazz);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
			
		}).start();
	}
	
	public void ExecuteScalarAsync(final String sql, final ResultHandler<Object> handler)
	{
		ExecuteScalarAsync(sql, (Parameters)null, handler);
	}
	
	public void ExecuteScalarAsync(final String sql, final Parameters parameters, final ResultHandler<Object> handler)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Result<Object> r = Connector.this.ExecuteScalar(sql, parameters);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
			
		}).start();
	}
	
	public <T> Result<ArrayList<T>> ExecuteScalarArray(final String sql, final Parameters parameters, Class<T> clss)
	{
		Result<ArrayList<T>> result = new Result<ArrayList<T>>();
		Result<DataTable> rt = this.ExecuteDataTable(sql, parameters);
		result.FromErrorResult(rt);
		if (rt.Value != null && rt.Value.Columns.size() > 0 && rt.Value.Rows.size() > 0) {
			ArrayList<T> arr = new ArrayList<T>();
			for (DataRow row : rt.Value.Rows) {
				T v = row.getValue(0, clss);
				arr.add(v);
			}
			result.Value = arr;
		}
		
		return result;
	}
	
	public <T> void ExecuteScalarArrayAsync(final String sql, final Parameters parameters, final Class<T> clazz, final ResultHandler<ArrayList<T>> handler)
	{
		new Thread(new Runnable() {

			@Override
			public void run() {
				Result<ArrayList<T>> r = Connector.this.ExecuteScalarArray(sql, parameters, clazz);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
			
		}).start();
	}
	
	public Result<DataTable> ExecuteDataTable(String sql)
	{
		return ExecuteDataTable(sql, null);
	}
	
	public Result<DataTable> ExecuteDataTable(String sql, Parameters parameters)
	{
		sql = this.ProcessParameters(sql, parameters);
		Result<DataTable> result = new Result<DataTable>();

		Connection conn = null;
        try {
        	conn = this.CreateConnection();
        	if (conn != null) {
        		ResultSet rs = null;
        		if (parameters != null) {
        			//CallableStatement stmt = conn.prepareCall(sql);
        			PreparedStatement stmt = conn.prepareStatement(sql);
        			this.PrepareParameters(stmt, parameters);
        			rs = stmt.executeQuery();
        		} else {
        			Statement stmt = conn.createStatement();
        			rs = stmt.executeQuery(sql);
        		}
        		
        		if (rs != null) {
        			DataTable table = new DataTable();
        			ResultSetMetaData rsm = rs.getMetaData();
        			int columnCount = rsm.getColumnCount();
        			for (int i = 0; i < columnCount; i++) {
        				table.Columns.Add(rsm.getColumnName(i+1), rsm.getColumnType(i+1));
        			}
        			
        			while(rs.next()) {
        				DataRow row = table.NewRow();
        				for (int i = 0; i < table.Columns.size(); i++) {
        					row.setValue(i, rs.getObject(i+1));
        	            }
        				table.Rows.add(row);
        			}
        			
        			result.Value = table;
        		}
        	}
        } catch (SQLException e) {
        	result.Error = e.getMessage();
        	result.HasError = true;
        } finally {
        	this.CloseConnection(conn);
        }
        
		return result;
	}
	
	public void ExecuteDataTableAsync(final String sql,final ResultHandler<DataTable> handler)
	{
		this.ExecuteDataTableAsync(sql, null, handler);
	}
	
	public void ExecuteDataTableAsync(final String sql, final Parameters parameters, final ResultHandler<DataTable> handler)
	{
		new Thread(new Runnable(){
			@Override
			public void run() {
				Result<DataTable> r = ExecuteDataTable(sql, parameters);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
			
		}).start();
	}
	
	public Result<DataRow> ExecuteRecord(String sql)
	{
		return this.ExecuteRecord(sql, null);
	}
	
	public Result<DataRow> ExecuteRecord(String sql, Parameters parameters)
	{
		Result<DataRow> result = new Result<DataRow>();
		Result<DataTable> r = this.ExecuteDataTable(sql, parameters);
		result.HasError = r.HasError;
		result.Error = r.Error;
		if (r.Value != null && r.Value.Rows.size() > 0) {
			result.Value = r.Value.Rows.get(0);
		}
		return result;
	}
	
	public void ExecuteRecordAsync(final String sql, final ResultHandler<DataRow> handler)
	{
		ExecuteRecordAsync(sql, null, handler);
	}
	
	public void ExecuteRecordAsync(final String sql, final Parameters parameters, final ResultHandler<DataRow> handler)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Result<DataRow> r = Connector.this.ExecuteRecord(sql, parameters);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
			
		}).start();
	}
	
	public Result<DataSet> ExecuteDataSet(String sql)
	{
		return ExecuteDataSet(sql, null);
	}
	
	public Result<DataSet> ExecuteDataSet(String sql, Parameters parameters)
	{
		sql = this.ProcessParameters(sql, parameters);
		Result<DataSet> result = new Result<DataSet>();
		Connection conn = null;
		try {
        	conn = this.CreateConnection();
        	if (conn != null) {
        		Boolean ok = false;
        		Statement stmt = null;
        		if (parameters != null) {
        			PreparedStatement st = conn.prepareStatement(sql);
        			this.PrepareParameters(st, parameters);
        			stmt = st;
        			ok = st.execute();
        		} else {
        			stmt = conn.createStatement();
        			ok = stmt.execute(sql);
        		}

        		if (ok) {
            		DataSet ds = new DataSet();
            		do {
            			ResultSet rs = stmt.getResultSet();
            			if (rs != null) {
            				DataTable table = new DataTable();
                			ResultSetMetaData rsm = rs.getMetaData();
                			int columnCount = rsm.getColumnCount();
                			for (int j = 0; j < columnCount; j++) {
                				table.Columns.Add(rsm.getColumnName(j+1));
                			}
                			
                			while(rs.next()) {
                				DataRow row = table.NewRow();
                				for (int j = 0; j < table.Columns.size(); j++) {
                					row.setValue(j, rs.getObject(j+1));
                	            }
                				table.Rows.add(row);
                			}
                			
                			ds.Tables.add(table);
            			}
            		} while(stmt.getMoreResults());
            		
            		result.Value = ds;
        		}
        		
        		if (stmt != null) {
        			stmt.close();
        		}
        	}
        } catch (SQLException e) {
        	result.Error = e.getMessage();
        	result.HasError = true;
        } finally {
        	this.CloseConnection(conn);
        }

		return result;
	}

	public void ExecuteDataSetAsync(final String sql, final ResultHandler<DataSet> handler)
	{
		ExecuteDataSetAsync(sql, null, handler);
	}
	
	public void ExecuteDataSetAsync(final String sql, final Parameters parameters, final ResultHandler<DataSet> handler)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Result<DataSet> r = Connector.this.ExecuteDataSet(sql, parameters);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
		}).start();
	}
}
