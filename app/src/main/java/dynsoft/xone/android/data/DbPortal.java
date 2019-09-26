package dynsoft.xone.android.data;

import java.sql.SQLException;
import java.util.ArrayList;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.ViaModes;

public class DbPortal {

	public java.sql.Connection CreateConnection(String connector)
	{
		Connector conn = App.Current.ConnectorManager.GetConnector(connector);
		if (conn != null) {
			try {
				return conn.CreateConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public Result<Integer> ExecuteNonQuery(String connector, String sql)
	{
		return this.ExecuteNonQuery(connector, sql, null);
	}
	
	public Result<Integer> ExecuteNonQuery(String connector, String sql, Parameters parameters)
	{
		Connector conn = App.Current.ConnectorManager.GetConnector(connector);
		if (conn == null) {
			Result<Integer> r = new Result<Integer>();
			r.HasError = true;
			r.Error = "core_and_dbportal_connector_null:"+connector;
			return r;
		}
		return this.ExecuteNonQuery(conn, sql, parameters);
	}
	
	public Result<Integer> ExecuteNonQuery(Connector connector, String sql)
	{
		return this.ExecuteNonQuery(connector, sql, null);
	}
	
	public Result<Integer> ExecuteNonQuery(Connector connector, String sql, Parameters parameters)
	{
		if (connector.ViaMode == ViaModes.Directly) {
			return connector.ExecuteNonQuery(sql, parameters);
		} else {
			
			//........
			return null;
		}
	}

	public void ExecuteNonQueryAsync(final String connector, final String sql, final ResultHandler<Integer> handler)
	{
		this.ExecuteNonQueryAsync(connector, sql, null, handler);
	}
	
	public void ExecuteNonQueryAsync(final String connector, final String sql, final Parameters parameters, final ResultHandler<Integer> handler)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Result<Integer> r = DbPortal.this.ExecuteNonQuery(connector, sql, parameters);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
			
		}).start();
	}
	
	public void ExecuteNonQueryAsync(final Connector connector, final String sql, final ResultHandler<Integer> handler)
	{
		this.ExecuteNonQueryAsync(connector, sql, null, handler);
	}
	
	public void ExecuteNonQueryAsync(final Connector connector, final String sql, final Parameters parameters, final ResultHandler<Integer> handler)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Result<Integer> r = DbPortal.this.ExecuteNonQuery(connector, sql, parameters);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
			
		}).start();
	}
	
	public <T> Result<T> ExecuteScalar(String connector, String sql, Class<T> clazz)
	{
		return this.ExecuteScalar(connector, sql, null, clazz);
	}
	
	public <T> Result<T> ExecuteScalar(String connector, String sql, Parameters parameters, Class<T> clazz)
	{
		Connector conn = App.Current.ConnectorManager.GetConnector(connector);
		if (conn == null) {
			Result<T> r = new Result<T>();
			r.HasError = true;
			r.Error = "core_and_dbportal_connector_null:"+connector;
			return r;
		}
		return this.ExecuteScalar(conn, sql, parameters, clazz);
	}

	public <T> Result<T> ExecuteScalar(Connector connector, String sql, Class<T> clazz)
	{
		return this.ExecuteScalar(connector, sql, null, clazz);
	}
	
	public <T> Result<T> ExecuteScalar(Connector connector, String sql, Parameters parameters, Class<T> clazz)
	{
		if (connector.ViaMode == ViaModes.Directly) {
			return connector.ExecuteScalar(sql, parameters, clazz);
		} else {
			
			//........
			return null;
		}
	}

	public <T> void ExecuteScalarAsync(final String connector, final String sql, final Class<T> clazz, final ResultHandler<T> handler)
	{
		ExecuteScalarAsync(connector, sql, null, clazz, handler);
	}
	
	public <T> void ExecuteScalarAsync(final String connector, final String sql, final Parameters parameters, final Class<T> clazz, final ResultHandler<T> handler)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Result<T> r = DbPortal.this.ExecuteScalar(connector, sql, parameters, clazz);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
			
		}).start();
	}
	
	public <T> void ExecuteScalarAsync(final Connector connector, final String sql, final Class<T> clazz, final ResultHandler<T> handler)
	{
		ExecuteScalarAsync(connector, sql, null, clazz, handler);
	}
	
	public <T> void ExecuteScalarAsync(final Connector connector, final String sql, final Parameters parameters, final Class<T> clazz, final ResultHandler<T> handler)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Result<T> r = DbPortal.this.ExecuteScalar(connector, sql, parameters, clazz);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
			
		}).start();
	}
	
	public Result<Object> ExecuteScalar(String connector, String sql)
	{
		return this.ExecuteScalar(connector, sql, (Parameters)null);
	}
	
	public Result<Object> ExecuteScalar(String connector, String sql, Parameters parameters)
	{
		Connector conn = App.Current.ConnectorManager.GetConnector(connector);
		if (conn == null) {
			Result<Object> r = new Result<Object>();
			r.HasError = true;
			r.Error = "core_and_dbportal_connector_null:"+connector;
			return r;
		}
		return ExecuteScalar(conn, sql, parameters);
	}
	
	public Result<Object> ExecuteScalar(Connector connector, String sql)
	{
		return this.ExecuteScalar(connector, sql, (Parameters)null);
	}
	
	public Result<Object> ExecuteScalar(Connector connector, String sql, Parameters parameters)
	{
		if (connector.ViaMode == ViaModes.Directly) {
			return connector.ExecuteScalar(sql, parameters);
		} else {
			
			//....
			return null;
		}
	}
	
	public <T> void ExecuteScalarAsync(final String connector, final String sql, final ResultHandler<Object> handler)
	{
		ExecuteScalarAsync(connector, sql, (Parameters)null, handler);
	}
	
	public void ExecuteScalarAsync(final String connector, final String sql, final Parameters parameters, final ResultHandler<Object> handler)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Result<Object> r = DbPortal.this.ExecuteScalar(connector, sql, parameters);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
			
		}).start();
	}
	
	public void ExecuteScalarAsync(final Connector connector, final String sql, final ResultHandler<Object> handler)
	{
		ExecuteScalarAsync(connector, sql, (Parameters)null, handler);
	}
	
	public void ExecuteScalarAsync(final Connector connector, final String sql, final Parameters parameters, final ResultHandler<Object> handler)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Result<Object> r = DbPortal.this.ExecuteScalar(connector, sql, parameters);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
			
		}).start();
	}
	
	public <T> Result<ArrayList<T>> ExecuteScalarArray(final String connector, final String sql, final Parameters parameters, Class<T> clss)
	{
		Connector conn = App.Current.ConnectorManager.GetConnector(connector);
		if (conn == null) {
			Result<ArrayList<T>> r = new Result<ArrayList<T>>();
			r.HasError = true;
			r.Error = "core_and_dbportal_connector_null:"+connector;
			return r;
		}
		
		return this.ExecuteScalarArray(conn, sql, parameters, clss);
	}
	
	public <T> Result<ArrayList<T>> ExecuteScalarArray(Connector connector, String sql, Parameters parameters, Class<T> clss)
	{
		if (connector.ViaMode == ViaModes.Directly) {
			return connector.ExecuteScalarArray(sql, parameters, clss);
		} else {
			
			//....
			return null;
		}
	}
	
	public <T> void ExecuteScalarArrayAsync(final String connector, final String sql, final Parameters parameters, final Class<T> clss, final ResultHandler<ArrayList<T>> handler)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Result<ArrayList<T>> r = DbPortal.this.ExecuteScalarArray(connector, sql, parameters, clss);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
			
		}).start();
	}
	
	public <T> void ExecuteScalarArrayAsync(final Connector connector, final String sql, final Parameters parameters, final Class<T> clss, final ResultHandler<ArrayList<T>> handler)
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				Result<ArrayList<T>> r = DbPortal.this.ExecuteScalarArray(connector, sql, parameters, clss);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
		}).start();
	}

	public Result<DataTable> ExecuteDataTable(String connector, String sql)
	{
		return this.ExecuteDataTable(connector, sql, null);
	}
	
	public Result<DataTable> ExecuteDataTable(String connector, String sql, Parameters parameters)
	{
		Connector conn = App.Current.ConnectorManager.GetConnector(connector);
		if (conn == null) {
			Result<DataTable> r = new Result<DataTable>();
			r.HasError = true;
			r.Error = "core_and_dbportal_connector_null:"+connector;
			return r;
		}
		
		return this.ExecuteDataTable(conn, sql, parameters);
	}

	public Result<DataTable> ExecuteDataTable(Connector connector, String sql)
	{
		return this.ExecuteDataTable(connector, sql, null);
	}
	
	public Result<DataTable> ExecuteDataTable(Connector connector, String sql, Parameters parameters)
	{
		if (connector.ViaMode == ViaModes.Directly) {
			return connector.ExecuteDataTable(sql, parameters);
		} else {
			
			//....
			return null;
		}
	}
	
	public void ExecuteDataTableAsync(final String connector, final String sql, final ResultHandler<DataTable> handler)
	{
		this.ExecuteDataTableAsync(connector, sql, null, handler);
	}
	
	public void ExecuteDataTableAsync(final String connector, final String sql, final Parameters parameters, final ResultHandler<DataTable> handler)
	{
		new Thread(new Runnable(){
			@Override
			public void run() {
				Result<DataTable> r = ExecuteDataTable(connector, sql, parameters);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
			
		}).start();
	}
	
	public void ExecuteDataTableAsync(final Connector connector, final String sql, final ResultHandler<DataTable> handler)
	{
		this.ExecuteDataTableAsync(connector, sql, null, handler);
	}
	
	public void ExecuteDataTableAsync(final Connector connector, final String sql, final Parameters parameters, final ResultHandler<DataTable> handler)
	{
		new Thread(new Runnable(){
			@Override
			public void run() {
				Result<DataTable> r = ExecuteDataTable(connector, sql, parameters);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
			
		}).start();
	}
	
	public Result<DataRow> ExecuteRecord(String connector, String sql)
	{
		return this.ExecuteRecord(connector, sql, null);
	}
	
	public Result<DataRow> ExecuteRecord(String connector, String sql, Parameters parameters)
	{
		Result<DataRow> result = new Result<DataRow>();
		Result<DataTable> r = this.ExecuteDataTable(connector, sql, parameters);
		result.HasError = r.HasError;
		result.Error = r.Error;
		if (r.Value != null && r.Value.Rows.size() > 0) {
			result.Value = r.Value.Rows.get(0);
		}
		return result;
	}
	
	public Result<DataRow> ExecuteRecord(Connector connector, String sql)
	{
		return this.ExecuteRecord(connector, sql, null);
	}
	
	public Result<DataRow> ExecuteRecord(Connector connector, String sql, Parameters parameters)
	{
		Result<DataRow> result = new Result<DataRow>();
		Result<DataTable> r = this.ExecuteDataTable(connector, sql, parameters);
		result.HasError = r.HasError;
		result.Error = r.Error;
		if (r.Value != null && r.Value.Rows.size() > 0) {
			result.Value = r.Value.Rows.get(0);
		}
		return result;
	}
	
	public void ExecuteRecordAsync(final String connector, final String sql, final ResultHandler<DataRow> handler)
	{
		ExecuteRecordAsync(connector, sql, null, handler);
	}
	
	public void ExecuteRecordAsync(final String connector, final String sql, final Parameters parameters, final ResultHandler<DataRow> handler)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Result<DataRow> r = DbPortal.this.ExecuteRecord(connector, sql, parameters);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
			
		}).start();
	}
	
	public void ExecuteRecordAsync(final Connector connector, final String sql, final ResultHandler<DataRow> handler)
	{
		ExecuteRecordAsync(connector, sql, null, handler);
	}
	
	public void ExecuteRecordAsync(final Connector connector, final String sql, final Parameters parameters, final ResultHandler<DataRow> handler)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Result<DataRow> r = DbPortal.this.ExecuteRecord(connector, sql, parameters);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
			
		}).start();
	}
	
	public Result<DataSet> ExecuteDataSet(String connector, String sql)
	{
		return ExecuteDataSet(connector, sql, null);
	}
	
	public Result<DataSet> ExecuteDataSet(String connector, String sql, Parameters parameters)
	{
		Connector conn = App.Current.ConnectorManager.GetConnector(connector);
		if (conn == null) {
			Result<DataSet> r = new Result<DataSet>();
			r.HasError = true;
			r.Error = "core_and_dbportal_connector_null:"+ connector;
			return r;
		}
		
		return this.ExecuteDataSet(conn, sql, parameters);
	}
	
	public Result<DataSet> ExecuteDataSet(Connector connector, String sql)
	{
		return ExecuteDataSet(connector, sql, null);
	}
	
	public Result<DataSet> ExecuteDataSet(Connector connector, String sql, Parameters parameters)
	{
		if (connector.ViaMode == ViaModes.Directly) {
			return connector.ExecuteDataSet(sql, parameters);
		} else {
			
			//....
			return null;
		}
	}

	public void ExecuteDataSetAsync(String connector, String sql, ResultHandler<DataSet> handler)
	{
		ExecuteDataSetAsync(connector, sql, null, handler);
	}
	
	public void ExecuteDataSetAsync(final String connector, final String sql, final Parameters parameters, final ResultHandler<DataSet> handler)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Result<DataSet> r = DbPortal.this.ExecuteDataSet(connector, sql, parameters);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
		}).start();
	}
	
	public void ExecuteDataSetAsync(Connector connector, String sql, ResultHandler<DataSet> handler)
	{
		ExecuteDataSetAsync(connector, sql, null, handler);
	}
	
	public void ExecuteDataSetAsync(final Connector connector, final String sql, final Parameters parameters, final ResultHandler<DataSet> handler)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Result<DataSet> r = DbPortal.this.ExecuteDataSet(connector, sql, parameters);
				if (handler != null) {
					handler.Value = r;
					handler.sendEmptyMessage(0);
				}
			}
		}).start();
	}
}
