package dynsoft.xone.android.data;

import java.util.ArrayList;



public class DataRowCollection extends ArrayList<DataRow> {
	
	private DataTable _table; 
	
	public DataRowCollection(DataTable table) {
        _table = table;
    }

    public DataTable getTable()
    {
        return _table;
    }
    
    @Override
    public boolean add(DataRow row)
    {
        boolean r = super.add(row);
        if (r == true) {
            row.setTable(_table);
        }
        return r;
    }
}
