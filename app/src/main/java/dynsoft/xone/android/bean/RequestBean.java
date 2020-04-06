package dynsoft.xone.android.bean;
import dynsoft.xone.android.retrofit.MarkDownBean;
import dynsoft.xone.android.retrofit.MsgBean;

public class RequestBean {
    private String agent_id;
    private String userid_list;
    private String msgtype;
//    private MsgBean msg;
    private MarkDownBean msg;

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getUserid_list() {
        return userid_list;
    }

    public void setUserid_list(String userid_list) {
        this.userid_list = userid_list;
    }

    public MarkDownBean getMsg() {
        return msg;
    }

    public void setMsg(MarkDownBean msg) {
        this.msg = msg;
    }
}


