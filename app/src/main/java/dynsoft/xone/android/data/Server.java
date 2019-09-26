package dynsoft.xone.android.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Base64;

import com.google.gson.Gson;

public class Server {
	
	public String Server;
	
	public String Port;
	
	public Result<Response> SendMessage(Request request)
	{
		Result<Response> result = new Result<Response>();

		try {
			
			Gson gson = new Gson();
			String data = gson.toJson(request);
			data = Base64.encodeToString(data.getBytes("UTF-8"), Base64.DEFAULT);
			
			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			HttpConnectionParams.setConnectionTimeout(params, 30000);
			HttpConnectionParams.setSoTimeout(params, 30000);
			
			HttpClient client = new DefaultHttpClient(params);
			HttpPost post = new HttpPost("http://" + Server + ":" + Port.toString() + "/Send");
			post.setHeader("Content-Type", "text/plain");
			StringEntity se = new StringEntity(data);
			post.setEntity(se);
			
	        HttpResponse httpResponse = client.execute(post);
	        int statusCode = httpResponse.getStatusLine().getStatusCode();
	        if (statusCode == 200) {
	        	HttpEntity responseEntity = httpResponse.getEntity();
	        	
	        	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        	byte[] buffer = new byte[1024];
	        	int len = 0;
	        	InputStream stream = responseEntity.getContent();
	        	while ((len = stream.read(buffer)) != -1) {
	        		outputStream.write(buffer, 0, len);
	        	}
	        	
	        	String str = new String(outputStream.toByteArray(), "UTF-8");
	        	buffer = Base64.decode(str, Base64.DEFAULT);
        		str = new String(buffer, "UTF-8");
        		result.Value = gson.fromJson(str, Response.class);
        		if (result.Value != null) {
        			result.HasError = result.Value.HasError;
        			result.Error = result.Value.Error;
        		}
	        } else {
	        	result.HasError = true;
	        	result.Error = httpResponse.getStatusLine().getReasonPhrase();
	        }
		} catch (IllegalArgumentException e) {
			result.HasError = true;
			result.Error = e.getMessage();
		} catch (UnsupportedEncodingException e) {
			result.HasError = true;
			result.Error = e.getMessage();
		} catch (IOException e) {
			result.HasError = true;
			result.Error = "ÍøÂç´íÎó¡£";
		}
        
        return result;
	}

	public void SendMessageAsync(final Request request, final ResultHandler<Response> handler)
	{
		new Thread(new Runnable(){
			@Override
			public void run() {
				Result<Response> result = SendMessage(request);
				if (handler != null) {
					handler.Value = result;
					handler.sendEmptyMessage(0);
				}
			}}).start();
	}
}
