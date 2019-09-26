package dynsoft.xone.android.retrofit;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

/**
 * Created by Administrator on 2018/8/10.
 */

public class FileResponseBody extends ResponseBody {

    Response originalResponse;

    public FileResponseBody(Response originalResponse) {
        this.originalResponse = originalResponse;
    }

    @Override
    public MediaType contentType() {
        return originalResponse.body().contentType();
    }

    @Override
    public long contentLength() throws IOException {// �����ļ����ܳ��ȣ�Ҳ���ǽ�������max
        return originalResponse.body().contentLength();
    }

    @Override
    public BufferedSource source() throws IOException {
        return Okio.buffer(new ForwardingSource(originalResponse.body().source()) {
            long bytesReaded = 0;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                bytesReaded += bytesRead == -1 ? 0 : bytesRead;
                // ͨ��RxBus����������Ϣ
//                RxBus.getDefault().post(new FileLoadingBean(contentLength(), bytesReaded));
                return bytesRead;
            }
        });
    }
}
