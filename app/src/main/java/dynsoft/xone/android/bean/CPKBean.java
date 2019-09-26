package dynsoft.xone.android.bean;

/**
 * Created by Administrator on 2018/8/15.
 */

public class CPKBean {
    private String content;
    private int CPKTYPE;

    public CPKBean(String content, int CPKTYPE) {
        this.content = content;
        this.CPKTYPE = CPKTYPE;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCPKTYPE() {
        return CPKTYPE;
    }

    public void setCPKTYPE(int CPKTYPE) {
        this.CPKTYPE = CPKTYPE;
    }
}
