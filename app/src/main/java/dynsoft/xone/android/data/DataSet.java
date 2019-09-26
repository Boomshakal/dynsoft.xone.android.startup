package dynsoft.xone.android.data;

public class DataSet {
	
	public String Name;
	public DataTableCollection Tables;
	
	public DataSet() {
		Tables = new DataTableCollection();
	}
	
	public DataSet(String name) {
		this.Name = name;
		Tables = new DataTableCollection();
	}
	
	public void LoadXml(String file) {
		
	}
	
	public void WriteXml(String file) {
		
	}
}
