package dynsoft.xone.android.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import dynsoft.xone.android.data.Book;
import dynsoft.xone.android.data.PrintRequest;
import dynsoft.xone.android.data.PrintSetting;
import dynsoft.xone.android.data.Result;

public class PrintHelper {

	public static Result<String> print(PrintRequest request) {
		
		Result<String> result = new Result<String>();
		if (request.Server == null || request.Server.length() == 0) {
			result.HasError = true;
			result.Error = "没有指定打印服务器。";
			return result;
		}

		try {

			Gson gson = new Gson();
			String data = gson.toJson(request);
			data = Base64.encodeToString(data.getBytes("UTF-8"), Base64.DEFAULT);

			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			HttpConnectionParams.setConnectionTimeout(params, 30000);
			HttpConnectionParams.setSoTimeout(params, 30000);

			HttpClient client = new DefaultHttpClient(params);
			HttpPost post = new HttpPost(request.Server + "/Print");
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
		
				Result<String> rss = gson.fromJson(str, new TypeToken<Result<String>>(){}.getType());
				result.FromErrorResult(rss);
				result.Value = rss.Value;
			} else {
				result.HasError = true;
				result.Error = httpResponse.getStatusLine().getReasonPhrase();
			}
		} catch (IllegalArgumentException e) {
			result.HasError = true;
			result.Error = e.getMessage();
		} catch (IllegalStateException e) {
			result.HasError = true;
			result.Error = e.getMessage();
		} catch (UnsupportedEncodingException e) {
			result.HasError = true;
			result.Error = e.getMessage();
		} catch (IOException e) {
			result.HasError = true;
			result.Error = "网络异常。";
		}
		
		return result;
	}

	public static PrintSetting loadPrintServer(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences("X1_PRINT_SERVER_SETTING", 0);
		PrintSetting printSetting = new PrintSetting();
		printSetting.Server = sp.getString("server", "");
		printSetting.Url = sp.getString("url", "");
		printSetting.Printer = sp.getString("printer", "");
		return printSetting;
	}
	
	public static void savePrintServer(Context context, PrintSetting printSetting)
	{
		if (printSetting == null) return;
		
		SharedPreferences sp = context.getSharedPreferences("X1_PRINT_SERVER_SETTING", 0);
    	SharedPreferences.Editor editor = sp.edit();
    	editor.putString("server", printSetting.Server);
    	editor.putString("url", printSetting.Url);
	    editor.putString("printer", printSetting.Printer);
	    editor.commit();
	}
}
