package dynsoft.xone.android.data;

import java.util.Map;
import java.util.LinkedHashMap;



public class DataRow {
	
	private DataTable _table;
	private Map<String,Object> _fields;
	
	public DataRow()
	{
	    _fields = new LinkedHashMap<String, Object>();
	}
	
	public DataRow(DataTable table) {
		_table = table;
		_fields = new LinkedHashMap<String, Object>();
    }
	
	public void setTable(DataTable table)
	{
	    _table = table;
	}

    public DataTable getTable()
    {
      return _table;
    }
    
    public Object getValue(int columnIndex) {
    	String columnName = _table.Columns.get(columnIndex).ColumnName.toLowerCase();
        return _fields.get(columnName);
    }
    
    @SuppressWarnings("unchecked")
	public <T> T getValue(int columnIndex, Class<T> classOfT)
    {
    	String columnName = _table.Columns.get(columnIndex).ColumnName.toLowerCase();
        return (T)_fields.get(columnName);
    }
    
    public Object getValue(String columnName) 
    {
    	return _fields.get(columnName.toLowerCase());
    }

    @SuppressWarnings("unchecked")
	public <T> T getValue(String columnName, Class<T> classOfT)
    {
    	return (T)_fields.get(columnName.toLowerCase());
    }
    
    public <T> T getValue(String columnName, T defaultValue)
    {
        @SuppressWarnings("unchecked")
        T val = (T)_fields.get(columnName.toLowerCase());
        if (val == null) {
            val = defaultValue;
        }
        return val;
    }
    
    public Object getValue(DataColumn column) {
      return _fields.get(column.ColumnName.toLowerCase());
    }
    
    @SuppressWarnings("unchecked")
	public <T> T getValue(DataColumn column, Class<T> classOfT)
    {
    	return (T)_fields.get(column.ColumnName.toLowerCase());
    }
    
    public <T> T getValue(DataColumn column, T defaultValue)
    {
        @SuppressWarnings("unchecked")
        T val = (T)_fields.get(column.ColumnName.toLowerCase());
        if (val == null) {
            val = defaultValue;
        }
        return val;
    }
    
    public void setValue(int columnindex, Object value) {
        setValue(_table.Columns.get(columnindex), value);
    }

    public void setValue(String columnName, Object value) {
    	String lowerColumnName = columnName.toLowerCase();
        if (_fields.containsKey(lowerColumnName)) {
        	_fields.remove(lowerColumnName);
        }
        _fields.put(lowerColumnName, value);
    }
    
    public Object[] getValues()
    {
    	return _fields.values().toArray();
    }

    private void setValue(DataColumn column, Object value) {
        if (column != null) {
            String lowerColumnName = column.ColumnName.toLowerCase();
            if (_fields.containsKey(lowerColumnName)) {
            	_fields.remove(lowerColumnName);
            }
            _fields.put(lowerColumnName, value);
        }
    }
}
