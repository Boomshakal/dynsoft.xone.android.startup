package dynsoft.xone.android.data;

public class Network {

    public String Code;
    
    public String Name;
    
    public Network(String code, String name)
    {
        this.Code = code;
        this.Name = name;
    }
    
    public final static Network Network1 = new Network("NET1", "��·1");
    public final static Network Network2 = new Network("NET2", "��·2");
    public final static Network Network3 = new Network("NET3", "��·3");
    public final static Network Network4 = new Network("NET4", "��·4");
    public final static Network Network5 = new Network("NET5", "��·5");
    public final static Network Network6 = new Network("NET6", "��·6");
    public final static Network Network7 = new Network("NET7", "��·7");
}
