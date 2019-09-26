package dynsoft.xone.android.retrofit.downloadapp;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by Administrator on 2018/8/10.
 */

public interface DownFileService {
    @GET("mes/mes.apk")
    Call<ResponseBody> loadFile();
}
