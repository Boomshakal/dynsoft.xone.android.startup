package dynsoft.xone.android.retrofit.downloadapp;


import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by Administrator on 2018/8/10.
 */

public interface DownService {
    @GET("mes/mes.json")
    Call<DownBean> getDownInfo();
}
