package dynsoft.xone.android.base;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map.Entry;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.data.*;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class PnWebReport extends BasePane {

    public PnWebReport(Context context) {
		super(context);
	}

	public final static String PARAM_CODE = "RPT_Code";
    public final static String PARAM_TITLE = "RPT_Title";
    public final static String CONFIG_URL_CODE = "core_webreport_url";
    
    public WebView WebView;
    public ImageButton RefreshButton;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onPrepared() {
        super.onPrepared();
        
        this.WebView = (WebView)this.Elements.get("webview").Object;
        this.RefreshButton = (ImageButton)this.Elements.get("refresh").Object;
        
        if (this.WebView != null) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
            lp.weight = 1;
            this.WebView.setLayoutParams(lp);
            this.WebView.getSettings().setJavaScriptEnabled(true);
            this.WebView.getSettings().setSupportZoom(true);
            this.WebView.getSettings().setBuiltInZoomControls(true);
        }
        
        if (this.RefreshButton != null) {
            this.RefreshButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_refresh_white"));
            this.RefreshButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    PnWebReport.this.refresh();
                }
            });
        }
        
        this.refresh();
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    public void refresh()
    {
        if (this.WebView != null) {
            String code = this.Parameters.get(PARAM_CODE, "");
            String title = this.Parameters.get(PARAM_TITLE, "");
            if (code.length() > 0) {
                String sql = "select (select name from report where code=?) Title,(select value from config where code=? and [key]=?) Url";
                Parameters p = new Parameters().add(1, code).add(2, CONFIG_URL_CODE).add(3, App.Current.Network.Code);
                Result<DataRow> r = App.Current.BookConnector.ExecuteRecord(sql, p);
                if (r.Value != null) {
                    
                    if (title == null || title.length() == 0) {
                        title = r.Value.getValue("Title", "");
                    }

                    this.Header.setTitleText(title);
                    
                    String url = r.Value.getValue("Url", "");
                    if (url.length() > 0) {
                        try {
                            url = url + "?Session=" + URLEncoder.encode(App.Current.Session, "UTF-8")
                                + "&BookCode=" + URLEncoder.encode(App.Current.BookCode, "UTF-8")
                                + "&ReportCode=" + URLEncoder.encode(code, "UTF-8");
                            Collection<Entry<Object, Object>> params = this.Parameters.entrySet();
                            for (Entry<Object, Object> kv : params) {
                                String key = kv.getKey().toString();
                                if (key.equals(PARAM_CODE) == false && key.startsWith("RPT_")) {
                                    
                                    String val = (kv.getValue() == null) ? "" : kv.getValue().toString();
                                    url += "&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(val, "UTF-8");
                                }
                            }
                            
                            this.WebView.loadUrl(url);
                            
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (r.HasError) {
                    Log.e(PnWebReport.class.getName(), r.Error);
                    App.Current.showError(this.getContext(), r.Error);
                }
            } else {
                Log.e(PnWebReport.class.getName(), "Report Code is empty.");
            }
        } else {
            Log.e(PnWebReport.class.getName(), "WebView is null.");
        }
    }
}
