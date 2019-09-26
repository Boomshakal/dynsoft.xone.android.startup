package dynsoft.xone.android.retrofit;

import android.os.Environment;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public Retrofit getRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(downUrl)
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
     * ��ʼ��OkHttpClient
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
     * ͨ��IO��д���ļ�
     */
    public File saveFile(Response response, String version) throws Exception {
        String filePath = Environment.getExternalStorageDirectory() + File.separator;
        String fileName = "megmeet.weld" + version + ".apk";
        InputStream in = null;
        FileOutputStream out = null;
        byte[] buf = new byte[2048];
        int len;
        try {
            File dir = new File(filePath);
            if (!dir.exists()) {// ����ļ��������½�һ��
                dir.mkdirs();
            }
            in = response.body().byteStream();
            File file = new File(dir, fileName);
            out = new FileOutputStream(file);
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            return file;
        } finally {
            in.close();
            out.close();
        }
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


}
