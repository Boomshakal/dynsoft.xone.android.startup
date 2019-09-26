package dynsoft.xone.android.sbo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class XmlHelper {
    
    public static String getNodeText(String xml, String node)
    {
        if (node == null) return null;
        
        try {
            InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF_8"));
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(stream , "UTF_8");
            int eventType = parser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {  
                switch(eventType) {  
                    case XmlPullParser.START_TAG:
                        if(node.equals(parser.getName())){
                            return parser.nextText();
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static boolean containsNode(String xml, String node)
    {
        if (node == null) return false;
        
        try {
            InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF_8"));
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(stream , "UTF_8");
            int eventType = parser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {  
                switch(eventType) {  
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if(node.equals(name)){
                            return true;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public static boolean containsError(String xml)
    {
        return containsNode(xml, "Fault");
    }

    public static String getEnvelopeError(String xml)
    {
        String code = getNodeText(xml, "Value");
        String msg = getNodeText(xml, "Text");
        return code + ": " + msg;
    }
}
