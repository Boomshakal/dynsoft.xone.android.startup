package dynsoft.xone.android.sbo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringEscapeUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.data.Request;
import dynsoft.xone.android.data.Response;
import dynsoft.xone.android.data.Result;

public class Document {

    private String _header;
    private String _body;
    private ArrayList<String> _lines;
    private ArrayList<String> _serials;
    private ArrayList<String> _batches;
    
    public Document(DocType type)
    {
        _lines = new ArrayList<String>();
        _serials = new ArrayList<String>();
        _batches = new ArrayList<String>();
        
        _header = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>"
                + "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                    + "<env:Header><SessionID>{__session__}</SessionID></env:Header>"
                    + "<env:Body>"
                        + "<dis:AddObject xmlns:dis=\"http://www.sap.com/SBO/DIS\" CommandID=\"Add Document\">"
                            + "<BOM><BO>"
                                + "<AdmInfo><Object>" + type.ObjectName + "</Object></AdmInfo>"
                                + "<" + type.BodyName + ">{__body__}</" + type.BodyName + ">"
                                + "<" + type.LineName1 + ">{__lines__}</" + type.LineName1 + ">"
                                + "<SerialNumbers>{__serials__}</SerialNumbers>"
                                + "<BatchNumbers>{__batches__}</BatchNumbers>"
                            + "</BO></BOM>"
                        + "</dis:AddObject>"
                    + "</env:Body>"
                + "</env:Envelope>";
    }
    
    public void setBody(String cardCode, String docDate, String docDueDate, String taxDate, String comments)
    {
        _body = "<row>" 
              + createXmlNode("CardCode", cardCode)
              + createXmlNode("DocDate", docDate)
              + createXmlNode("DocDueDate", docDueDate)
              + createXmlNode("TaxDate", taxDate)
              + createXmlNode("Comments", comments)
              + "</row>";
    }
    
    public void setBody(Map<String, String> body)
    {
        _body = "<row>";
        for (Entry<String,String> entry : body.entrySet()) {
            _body += createXmlNode(entry.getKey(), entry.getValue());
        }
        _body += "</row>";
    }
    
    public void addLine(String itemCode, String baseType, String baseEntry, String baseLine, String quantity, String whsCode, String freeTxt)
    {
        String line = "<row>"
                    + createXmlNode("ItemCode", itemCode)
                    + createXmlNode("BaseType", baseType)
                    + createXmlNode("BaseEntry", baseEntry)
                    + createXmlNode("BaseLine", baseLine)
                    + createXmlNode("Quantity", quantity)
                    + createXmlNode("WarehouseCode", whsCode)
                    + createXmlNode("FreeText", freeTxt)
                    + "</row>";
        _lines.add(line);
    }
    
    public void addLine(Map<String,String> line)
    {
        String str = "<row>";
        for (Entry<String,String> entry : line.entrySet()) {
            str += createXmlNode(entry.getKey(), entry.getValue());
        }
        str += "</row>";
        _lines.add(str);
    }
    
    public void addSerial(String lineNum, String number, boolean addSysNumber)
    {
        String line = "<row>"
                    + createXmlNode("BaseLineNumber", lineNum)
                    + (addSysNumber ? createXmlNode("SystemSerialNumber", String.valueOf(_serials.size() + 1)) : "")
                    + createXmlNode("ManufacturerSerialNumber", number)
                    + "</row>";
        _serials.add(line);
    }
    
    public void addBatch(String lineNum, String number, String quantity)
    {
        String line = "<row>"
                + createXmlNode("BaseLineNumber", lineNum)
                + createXmlNode("BatchNumber", number)
                + createXmlNode("Quantity", quantity)
                + "</row>";
        _batches.add(line);
    }
    
    public void addBatch(Map<String,String> line)
    {
        String str = "<row>";
        for (Entry<String,String> entry : line.entrySet()) {
            str += createXmlNode(entry.getKey(), entry.getValue());
        }
        str += "</row>";
        _batches.add(str);
    }
    
    public String toXmlString()
    {
        SboService service = (SboService)App.Current.ServiceManager.getService("svc_sbo_and");
        if (service == null) return null;
        
        if (service.SessionID == null) return null;
        String xml = _header.replace("{__session__}", service.SessionID);
        
        if (_body == null || _body.length() == 0) return null;
        xml = xml.replace("{__body__}", _body);
        
        StringBuilder sb = new StringBuilder();
        for (String str : _lines) {
            sb.append(str);
        }
        xml = xml.replace("{__lines__}", sb.toString());
        
        sb.delete(0, sb.length());
        for (String str : _serials) {
            sb.append(str);
        }
        xml = xml.replace("{__serials__}", sb.toString());
        
        sb.delete(0, sb.length());
        for (String str : _batches) {
            sb.append(str);
        }
        xml = xml.replace("{__batches__}", sb.toString());
        
        return xml;
    }
    
    private String createXmlNode(String name, String text)
    {
        if (text == null || text.length() == 0) return "";
        return "<" + name + ">" + StringEscapeUtils.escapeXml10(text) + "</" + name + ">";
    }
    
    public Result<String> save()
    {
        Result<String> result = new Result<String>();
        
        Request request = App.Current.createRequest();
        request.Code = "sbo_dis_interact";
        request.Data = this.toXmlString();
        Result<Response> r = App.Current.Server.Handle(request);
        if (r.Value != null) {
            String xml = r.Value.Data;
            if (xml != null) {
                if (XmlHelper.containsError(xml)) {
                    result.HasError = true;
                    result.Error = XmlHelper.getEnvelopeError(xml);
                } else if (XmlHelper.containsNode(xml, "RetKey")) {
                    result.Value = XmlHelper.getNodeText(xml, "RetKey");
                }
            } else {
                result.HasError = true;
                result.Error = "Has no reponse data.";
            }
        } else if (r.HasError) {
            App.Current.showError(App.Current.Workbench, r.Error);
        }
        
        return result;
    }
}
