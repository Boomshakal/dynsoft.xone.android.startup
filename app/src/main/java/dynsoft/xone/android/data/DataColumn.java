package dynsoft.xone.android.data;

public class DataColumn {
	
	private DataTable _table;
	
	public String ColumnName;
	public int DataType;
	
	public DataColumn() {
    }
	
	public DataColumn(String columnName) {
		ColumnName = columnName;
	}
	
	public DataColumn(String columnName, int dataType) {
		ColumnName = columnName;
		DataType = dataType;
	}
	
    public DataTable getTable()
    {
      return _table;
    }
    
    void setTable(DataTable table)
    {
    	_table = table;
    }
    
    @Override
    public String toString() {
    	return ColumnName;
    }
}
