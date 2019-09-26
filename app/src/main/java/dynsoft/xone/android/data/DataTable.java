package dynsoft.xone.android.data;

//DataTable µœ÷£¨«Î≤Œøº£∫
//http://www.dotblogs.com.tw/shadow/archive/2011/06/08/27314.aspx
//

public class DataTable {

	public String TableName;
	public DataRowCollection Rows;
	public DataColumnCollection Columns;
	
	public DataTable() {
		Rows = new DataRowCollection(this);
		Columns = new DataColumnCollection(this);
	}
	
	public DataTable(String tableName) {
		TableName = tableName;
	}

	public DataRow NewRow() {
		return new DataRow(this);
	}
	
	public void setValue(int rowIndex, int columnIndex,Object value) {
        this.Rows.get(rowIndex).setValue(columnIndex, value);
    }
 
    public void setValue(int rowIndex,String columnName,Object value) {
        this.Rows.get(rowIndex).setValue(columnName.toLowerCase(), value);
    }
     
    public Object getValue(int rowIndex,int columnIndex) {
        return this.Rows.get(rowIndex).getValue(columnIndex);
    }
    
     public Object getValue(int rowindex,String columnName) {
        return this.Rows.get(rowindex).getValue(columnName.toLowerCase());
    }
}
