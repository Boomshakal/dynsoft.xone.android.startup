package dynsoft.xone.android.retrofit;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import dynsoft.xone.android.core.App;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by Administrator on 2018/8/10.
 */

public class RetrofitDownUtil {
    private static final String downUrl = "http://" + App.Current.Server.Address + ":8018/";
    private static final String uploadCloudUrl = "http://api-iot.megmeet.com";
    private static final String dingdingUrl = "https://oapi.dingtalk.com/";
    private static RetrofitDownUtil retrofitDown;
    private Retrofit retrofit;

    private RetrofitDownUtil() {

    }

    public static RetrofitDownUtil getInstence() {
        if (retrofitDown == null) {
            retrofitDown = new RetrofitDownUtil();
        }
        return retrofitDown;
    }

    public Retrofit getDingdingRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(dingdingUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(initOkHttpClient())
                .build();
    }



    public Retrofit getRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(downUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(initOkHttpClient())
                .build();
        return retrofit;
    }

    public Retrofit getUploadCloudRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(uploadCloudUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(initOkHttpClient())
                .build();
        return retrofit;
    }

    public Retrofit getPrintRetrofit(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(initOkHttpClient())
                .build();
        return retrofit;
    }

//    public Retrofit getRetrofit () {
//        Retrofit build = new Retrofit.Builder()
//                .baseUrl(downUrl)
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        return build;
//    }

    /**
     * 初始化OkHttpClient
     *
     * @return
     */
    public OkHttpClient initOkHttpClient() {
        OkHttpClient builder = new OkHttpClient();
        builder.setConnectTimeout(10, TimeUnit.SECONDS);
        builder.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response proceed = chain.proceed(chain.request());
                return proceed.newBuilder().body(new FileResponseBody(proceed)).build();
            }
        });
        return builder;
    }


    /**
     * 通过IO流写入文件
     */
//    public File saveFile(Response response, String version) throws Exception {
//        String filePath = Environment.getExternalStorageDirectory() + File.separator;
//        String fileName = "megmeet.weld" + version + ".apk";
//        InputStream in = null;
//        FileOutputStream out = null;
//        byte[] buf = new byte[2048];
//        int len;
//        try {
//            File dir = new File(filePath);
//            if (!dir.exists()) {// 如果文件不存在新建一个
//                dir.mkdirs();
//            }
//            in = response.body().byteStream();
//            File file = new File(dir, fileName);
//            out = new FileOutputStream(file);
//            while ((len = in.read(buf)) != -1) {
//                out.write(buf, 0, len);
//            }
//            return file;
//        } finally {
//            in.close();
//            out.close();
//        }
//    }

}
