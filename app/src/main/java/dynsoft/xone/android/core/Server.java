package dynsoft.xone.android.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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

import android.os.Handler;
import android.os.Message;
import android.util.Base64;

import com.google.gson.Gson;

import dynsoft.xone.android.data.Book;
import dynsoft.xone.android.data.Request;
import dynsoft.xone.android.data.Response;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

public class Server {

	public String Address;
	
	public String Port;
	
	public Result<Book[]> GetBooks()
	{
		 Request request = new Request();
		 Result<Book[]> result = new Result<Book[]>();
		 String url = "http://" + this.Address + ":" + this.Port + "/GetBooks";
		 Result<Response> rs = this.SendMessage(request, url);
		 result.FromErrorResult(rs);
		 if (rs.Value != null) {
    	     if (rs.Value.Data != null && rs.Value.Data.length() > 0) {
    	         Gson gson = new Gson();
                 result.Value = gson.fromJson(rs.Value.Data, Book[].class);
             } else {
                 result.HasError = true;
                 result.Error = "@/win_core_server_get_books_response_data_empty";
             }
	     }
	     return result;
	 }
	
	public void GetBooksAsync(final ResultHandler<Book[]> handler)
	{
		new Thread(new Runnable(){
			@Override
			public void run() {
				Result<Book[]> result = GetBooks();
				if (handler != null) {
					handler.Value = result;
					handler.sendEmptyMessage(0);
				}
			}}).start();
	}
	
	public Result<LoginInfo> Login(Request request)
	{
		 Result<LoginInfo> result = new Result<LoginInfo>();
         String url = "http://" + this.Address + ":" + this.Port + "/Login";
         Result<Response> rs = this.SendMessage(request, url);
         result.FromErrorResult(rs);
         if (rs.Value != null) {
             Response response = rs.Value;
             if (response.HasError == false) {
            	 Gson gson = new Gson();
                 result.Value = gson.fromJson(response.Data, LoginInfo.class);
             } else {
                 result.HasError = true;
                 result.Error = response.Error;
             }
         }
         return result;
	}
	
	public void LoginAsync(final Request request, final ResultHandler<LoginInfo> handler)
	{
		new Thread(new Runnable(){
			@Override
			public void run() {
				Result<LoginInfo> result = Login(request);
				if (handler != null) {
					handler.Value = result;
					handler.sendEmptyMessage(0);
				}
			}}).start();
	}
	
	public void StartSessionTimer()
	{
		final Handler handler = new Handler(){  
	        public void handleMessage(Message msg) {
	        	Server.this.Hello();
	        	super.handleMessage(msg);  
	        }    
	    }; 
		     
		TimerTask task = new TimerTask(){  
			public void run() {  
		        Message message = new Message();      
		        message.what = 1;      
		        handler.sendMessage(message);    
		    }  
		};
		   
		Timer timer = new Timer(true);
		timer.schedule(task,1000, 60000);
	}
	
	public void Hello()
	{
	    Request request = new Request();
        request.Session = App.Current.Session;
        request.Feature = App.Current.FeatureCode;
        String url = "http://" + this.Address + ":" + this.Port + "/Hello";
        this.SendMessage(request, url);
	}
	
	public void Logout()
    {
        Request request = App.Current.createRequest();
        String url = "http://" + this.Address + ":" + this.Port + "/Logout";
        this.SendMessage(request, url);
    }
	
	public Result<Response> Handle(Request request)
    {
         String url = "http://" + this.Address + ":" + this.Port + "/Handle";
         return this.SendMessage(request, url);
    }
	
	public Result<Response> SendMessage(Request request, String url)
	{
		Result<Response> result = new Result<Response>();

		try {
			
			Gson gson = new Gson();
			String data = gson.toJson(request);
			data = Base64.encodeToString(data.getBytes("UTF-8"), Base64.NO_WRAP);
			
			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			HttpConnectionParams.setConnectionTimeout(params, 30000);
			HttpConnectionParams.setSoTimeout(params, 30000);
			
			HttpClient client = new DefaultHttpClient(params);
			HttpPost post = new HttpPost(url);
			post.setHeader("Content-Type", "text/plain");
			StringEntity se = new StringEntity(data);
			post.setEntity(se);
			
	        HttpResponse httpResponse = client.execute(post);
	        int statusCode = httpResponse.getStatusLine().getStatusCode();
	        if (statusCode == 200) {
	        	HttpEntity responseEntity = httpResponse.getEntity();
	        	
	        	InputStream stream = responseEntity.getContent();
                InputStreamReader input = new InputStreamReader(stream);
                BufferedReader reader = new BufferedReader(input);
                String str = reader.readLine();
                reader.close();
                input.close();
                stream.close();
                
//	        	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//	        	byte[] buffer = new byte[1024];
//	        	int len = 0;
//	        	while ((len = stream.read(buffer)) != -1) {
//	        		outputStream.write(buffer, 0, len);
//	        	}
//	        	String str = new String(outputStream.toByteArray(), "UTF-8");
                
	        	byte[] buffer = Base64.decode(str, Base64.NO_WRAP);
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
			result.Error = "core_and_server_network_err";
		} catch (Exception e) {
            result.HasError = true;
            result.Error = e.getMessage();
        }
        
        return result;
	}

}
