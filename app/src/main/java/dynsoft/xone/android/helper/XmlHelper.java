package dynsoft.xone.android.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import dynsoft.xone.android.data.Result;

public class XmlHelper {

	public static Result<String> parseResult(String xml)
	{
		Result<String> r = new Result<String>();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			Document document = builder.parse(stream);
			Element root = document.getDocumentElement();
			if (root != null && "Result".equals(root.getNodeName())) {
				
				NodeList list = root.getElementsByTagName("HasError");
				if (list != null && list.getLength() > 0) {
					String v = list.item(0).getTextContent();
					if (v == null || v.length() == 0) {
						v = "false";
					}
					r.HasError = Boolean.valueOf(v);
				}
				
				list = root.getElementsByTagName("Error");
				if (list != null && list.getLength() > 0) {
					r.Error = list.item(0).getTextContent();
				}
				
				list = root.getElementsByTagName("Value");
				if (list != null && list.getLength() > 0) {
					Node n = list.item(0);
					r.Value = list.item(0).getTextContent();
				}
			}

			return r;
			
		} catch (ParserConfigurationException e) {
			r.HasError = true;
			r.Error = e.getMessage();
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			r.HasError = true;
			r.Error = e.getMessage();
			e.printStackTrace();
		} catch (SAXException e) {
			r.HasError = true;
			r.Error = e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			r.HasError = true;
			r.Error = e.getMessage();
			e.printStackTrace();
		}
		
		return r;
	}
	
	public static String createXmlNode(String name, String text)
    {
        if (text == null || text.length() == 0) return "";
        return "<" + name + ">" + StringEscapeUtils.escapeXml10(text) + "</" + name + ">";
    }
	
	public static String createXmlAttribute(String name, String val)
	{
		if (val == null || val.length() == 0) return "";
        return name + "=\"" + StringEscapeUtils.escapeXml10(val) + "\" ";
	}
	
	public static String createXml(String root, Map<String, String> head, String itemsRoot, String itemName, ArrayList<Map<String, String>> items)
	{
		String xml = "";
		if (root != null && root.length() > 0) {
			xml = "<"+root+">";
			if (head != null && head.size() > 0) {
				for (Entry<String,String> entry : head.entrySet()) {
					String key = entry.getKey();
					String val = entry.getValue();
					if (key != null && key.length() > 0) {
						xml += createXmlNode(key, val);
					}
				}
			}
		}
		
		if (itemsRoot != null && itemsRoot.length() > 0) {
			xml+="<"+itemsRoot+">";
		}
		
		if (itemName != null && itemName.length() > 0 && items != null && items.size() > 0) {
			for (Map<String,String> item : items) {
				xml += "<"+itemName+" ";
				for (Entry<String,String> entry : item.entrySet()) {
					String key = entry.getKey();
					String val = entry.getValue();
					if (key != null && key.length() > 0) {
						xml += createXmlAttribute(key, val);
					}
				}
				xml += "/>";
			}
		}
		
		if (itemsRoot != null && itemsRoot.length() > 0) {
			xml += "</"+itemsRoot+">";
		}
		
		if (root != null && root.length() > 0) {
			xml += "</"+root+">";
		}
		return xml;
	}
}
