package dynsoft.xone.android.bean;

import java.util.ArrayList;

public class IotBean {
    /**
     *  "mac":"mac",
     *  "sn":"sn",
     *  "name":"name"
     */
    private String mac;
    private String sn;
    private String name;
    private String domain;
    private ArrayList<String> tags;

    public IotBean(String mac, String sn, String name, String domain, ArrayList<String> tags) {
        this.mac = mac;
        this.sn = sn;
        this.name = name;
        this.domain = domain;
        this.tags = tags;
    }
}
