package dynsoft.xone.android.retrofit;

import android.app.PictureInPictureParams;

public class MarkDownBean {
    private TextBean markdown;
    private String msgtype;

    public void setMarkdown(TextBean markdown) {
        this.markdown = markdown;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }
}
