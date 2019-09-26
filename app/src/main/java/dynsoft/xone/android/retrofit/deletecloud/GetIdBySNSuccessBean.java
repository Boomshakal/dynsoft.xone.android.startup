package dynsoft.xone.android.retrofit.deletecloud;

import java.util.ArrayList;

import dynsoft.xone.android.retrofit.uploadcloud.BackSuccessBean;

public class GetIdBySNSuccessBean {
    private int count;
    private ArrayList<BackSuccessBean> list;

    public GetIdBySNSuccessBean(int count, ArrayList<BackSuccessBean> list) {
        this.count = count;
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<BackSuccessBean> getList() {
        return list;
    }

    public void setList(ArrayList<BackSuccessBean> list) {
        this.list = list;
    }
}
