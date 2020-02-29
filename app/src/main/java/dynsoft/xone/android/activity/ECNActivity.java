package dynsoft.xone.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebSettings;
import android.webkit.WebView;

import dynsoft.xone.android.core.R;
public class ECNActivity extends Activity {
    private String baseString = "http://192.168.0.111:8075/WebReport/ReportServer?reportlet=MEGMEET/plm/twoecn_rep.cpt&UNID=";
    private WebView webView;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecn);
        webView = (WebView) findViewById(R.id.webview);
        Intent intent = getIntent();
        String unid = intent.getStringExtra("unid");
        baseString = baseString + unid;
        webView.loadUrl(baseString);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "android");
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                webView.loadUrl(url);
//                return true;
//            }
//        });
        //适配手机大小
//        settings.setUseWideViewPort(true);
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//        settings.setLoadWithOverviewMode(true);
//        settings.setSupportZoom(true);
//        settings.setBuiltInZoomControls(true);
//        settings.setDisplayZoomControls(false);

    }
}
