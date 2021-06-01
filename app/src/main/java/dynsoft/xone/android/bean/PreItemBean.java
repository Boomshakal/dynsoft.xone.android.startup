package dynsoft.xone.android.bean;

public class PreItemBean {
    private String item_code;
    private String date_code;

    public PreItemBean(String item_code, String date_code) {
        this.item_code = item_code;
        this.date_code = date_code;
    }

    public String getItem_code() {
        return item_code;
    }

    public String getDate_code() {
        return date_code;
    }

}
