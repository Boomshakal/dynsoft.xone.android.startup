package dynsoft.xone.android.retrofit;

import com.squareup.okhttp.ResponseBody;

import dynsoft.xone.android.bean.RequestBean;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

public interface DingdingService {
    @POST("topapi/message/corpconversation/asyncsend_v2")
    @Headers("Content-Type:application/json")
    Call<ResponseBody> sendMessage(@Query("access_token") String access_token, @Body RequestBean requestBean);

    @GET("gettoken")
    Call<ResponseBody> getToken(@Query("appkey") String appkey, @Query("appsecret") String appsecret);

    @GET("user/get_by_mobile")
    Call<ResponseBody> getUserIdByMobile(@Query("access_token") String access_token, @Query("mobile") String mobile);
}
