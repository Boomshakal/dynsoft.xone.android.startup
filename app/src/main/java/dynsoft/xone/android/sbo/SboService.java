package dynsoft.xone.android.sbo;

import java.util.Map;

import android.util.Log;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Service;
import dynsoft.xone.android.data.Request;
import dynsoft.xone.android.data.Response;
import dynsoft.xone.android.data.Result;

public class SboService extends Service {

    public String SessionID;
    
    public Request createRequest(String xml)
    {
        Request request = App.Current.createRequest();
        request.Code = "sbo_dis_interact";
        request.Data = xml;
        return request;
    }
    
    private String getConfigItem(Map<String, String> configs, String key)
    {
        if (configs.containsKey(key)) {
            return configs.get(key);
        }
        return "";
    }
    
    @Override
    public void onStart(Map<String, String> configs) {
        
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>"
                    + "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                    + "<env:Body>"
                    + "<dis:Login xmlns:dis=\"http://www.sap.com/SBO/DIS\">"
                        + "<DatabaseServer>" + this.getConfigItem(configs, "Server") + "</DatabaseServer>"
                          + "<DatabaseName>" + this.getConfigItem(configs, "CompanyDB") + "</DatabaseName>"
                          + "<DatabaseType>" + this.getConfigItem(configs, "DbServerType") + "</DatabaseType>"
                          + "<DatabaseUsername>" + this.getConfigItem(configs, "DbUserName") + "</DatabaseUsername>"
                          + "<DatabasePassword>" + this.getConfigItem(configs, "DbPassword") + "</DatabasePassword>"
                          + "<CompanyUsername>" + this.getConfigItem(configs, "UserName") + "</CompanyUsername>"
                          + "<CompanyPassword>" + this.getConfigItem(configs, "Password") + "</CompanyPassword>"
                          + "<LicenseServer>" + this.getConfigItem(configs, "LicenseServer") + "</LicenseServer>"
                          + "<Language>" + this.getConfigItem(configs, "Language") + "</Language>"
                        + "</dis:Login>"
                      + "</env:Body>"
                    + "</env:Envelope>";
        
        Log.i(SboService.class.getName(), "SboService login xml: " + xml);
        
        Request request = this.createRequest(xml);
        Result<Response> r = App.Current.Server.Handle(request);
        if (r.Value != null) {
            
            xml = r.Value.Data;
            Log.i(SboService.class.getName(), "SboService login response: " + xml);
            
            if (xml != null) {
                if (XmlHelper.containsError(xml)) {
                    String error = XmlHelper.getEnvelopeError(xml);
                    Log.e(SboService.class.getName(), error);
                } else {
                    this.SessionID = XmlHelper.getNodeText(xml, "SessionID");
                }
            }

        } else if (r.HasError) {
            Log.e(SboService.class.getName(), "SboService login failed. " + r.HasError);
        }
    }

    @Override
    public void onStop() {
        if (this.SessionID == null) return;
        
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>"
                    + "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                      + "<env:Header>"
                        + "<SessionID>" + this.SessionID + "</SessionID>"
                      + "</env:Header>"
                      + "<env:Body>"
                        + "<dis:Logout xmlns:dis=\"http://www.sap.com/SBO/DIS\">"
                        + "</dis:Logout>"
                      + "</env:Body>"
                    + "</env:Envelope>";
    
        Log.i(SboService.class.getName(), "SboService logout xml: " + xml);
        
        Request request = this.createRequest(xml);
        Result<Response> r = App.Current.Server.Handle(request);
        if (r.Value != null) {
            Log.i(SboService.class.getName(), "SboService logout response: " + r.Value.Data);
        } else if (r.HasError) {
            Log.e(SboService.class.getName(), "SboService load failed. " + r.HasError);
        }
    }

}
