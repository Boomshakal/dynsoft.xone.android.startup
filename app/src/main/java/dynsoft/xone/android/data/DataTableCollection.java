package dynsoft.xone.android.data;

import java.util.ArrayList;


public class DataTableCollection extends ArrayList<DataTable> {

	DataTableCollection() {
	}
	
	public DataTable get(String tableName) {
		DataTable table = null;
        for(DataTable dataTable :this) {
            if (dataTable.TableName.toLowerCase().equals(tableName.toLowerCase())) {
                return dataTable;
            }
        }
        return table;
	}
}
