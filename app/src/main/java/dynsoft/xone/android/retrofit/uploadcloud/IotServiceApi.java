package dynsoft.xone.android.retrofit.uploadcloud;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

public interface IotServiceApi {
    //Content-Type:"application/json"
    //Access-Token:"调用凭证"
    @Headers({"Content-Type: application/json","Access-Token: QUFGOUUwMkRBNUM4Rjk4MTZENDYwNkQ0NDBCNDYzOEFEMENDNUZFMzE0MzM3MTlCOUJBMDRERjc0Q0JDMEVFMw=="})
    @POST("/v2/product/{product_id}/device")
    /**
     * @POST ("/v2/product/1607d2b5e2351f411607d2b5e2351801/device_batch")
     * 添加设备到云平台
     */
//    Call<ResponseBody> addDeviceToCloud(@Query("product_id") String product_id );   // 请求体RequestBody类型
    Call<ResponseBody> addDeviceToCloud(@Query("product_id") String product_id, @Body RequestBody info);   // 请求体RequestBody类型

    /**
     * 获取操作的token
     * @param info
     * @return NTQyNUEyODA0RTlGMzlCRUM0Rjk3RjVBOTM0QkE0NTNBMURFNDRCQkMyNkZDNTcyRTU0MjJDRTYwOUIxRjlFMw==
     *          RDFBOUUwNUQzRTA4MjYwMkUxNERCQjlCMTkyMDQ0OEI1MDI0RjdBMzc5RTcyQTM3NDNCQjlGQjgyM0Y0NEE0Mg==
     */
    @Headers({"Content-Type:application/json"})
    @POST("/v2/accesskey_auth")
    Call<ResponseBody> getToken(@Body RequestBody info);

    /**
     * 通过设备SN号获取设备ID
     * @param product_id
     * @return
     */
    @Headers({"Content-Type:application/json", "Access-Token:QUFGOUUwMkRBNUM4Rjk4MTZENDYwNkQ0NDBCNDYzOEFEMENDNUZFMzE0MzM3MTlCOUJBMDRERjc0Q0JDMEVFMw=="})
    @POST("/v2/product/{product_id}/devices")
    Call<ResponseBody> getDeviceMessageBySN(@Query("product_id") String product_id, @Body RequestBody info);
//    Call<ResponseBody> getDeviceMessageBySN(@Query("product_id") String product_id);

    /**
     * 通过设备ID删除设备
     * @param product_id
     * @return
     */
    @Headers({"Content-Type:application/json", "Access-Token:QUFGOUUwMkRBNUM4Rjk4MTZENDYwNkQ0NDBCNDYzOEFEMENDNUZFMzE0MzM3MTlCOUJBMDRERjc0Q0JDMEVFMw=="})
    @DELETE("/v2/product/{product_id}/device/{device_id}")
    Call<ResponseBody> deleteDeviceById(@Query("product_id") String product_id, @Query("device_id") String device_id);
}
