package dynsoft.xone.android.data;

import java.sql.Types;
import java.util.ArrayList;

public class DataColumnCollection extends ArrayList<DataColumn>{
	
	private DataTable _table; 
	
	DataColumnCollection(DataTable table) {
        _table = table;
    }

    public DataTable getTable()
    {
    	return _table;
    }
    
    public void Add(DataColumn column) {
    	column.setTable(_table);
    	this.add(column);
    }
    
    public DataColumn Add(String columnName) {
    	DataColumn column = new DataColumn(columnName, Types.NVARCHAR);
    	column.setTable(_table);
    	this.add(column);
    	return column;
    }
    
    public DataColumn Add(String columnName, int dataType) {
    	DataColumn column = new DataColumn(columnName, dataType);
    	column.setTable(_table);
    	this.add(column);
    	return column;
    }
    
    public DataColumn GetColumn(int columnIndex) {
    	return this.get(columnIndex);
    }
    
    public DataColumn GetColumn(String columnName) {
    	DataColumn column = null;
        for(DataColumn dataColumn :this) {
            if (dataColumn.ColumnName.toLowerCase().equals(columnName.toLowerCase())) {
                return dataColumn;
            }
        }
        return column;
    }
    
    public int IndexOf(DataColumn column) {
    	return this.indexOf(column);
    }
    
    public int IndexOf(String columnName) {
    	return this.IndexOf(GetColumn(columnName));
    }
}
