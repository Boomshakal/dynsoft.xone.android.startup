package dynsoft.xone.android.util;

import android.util.Log;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;


import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import dynsoft.xone.android.core.App;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;




public class SmsUtilV {

//	// ��Ʒ����:��ͨ�Ŷ���API��Ʒ,�����������滻
//	private static final String product = "Dysmsapi";
//	// ��Ʒ����,�����������滻
//	private static final String domain = "dysmsapi.aliyuncs.com";
//
//	// TODO �˴���Ҫ�滻�ɿ������Լ���AK(�ڰ����Ʒ��ʿ���̨Ѱ��)
//	private static final String accessKeyId = "LTAIISTXuiFekAAi";
//	private static final String accessKeySecret = "0NCn7mkIXMeU7yPYCPaKf7uWhrToYX";
	public static SendSmsResponse sendSms(String SignName,String TemplateCode,String PhoneNumbers,String TemplateParam) throws Exception {
   
		// ������������ʱʱ��
        //���ó�ʱʱ��-�����е���
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");

		//��ʼ��ascClient��Ҫ�ļ�������
		final String product = "Dysmsapi";//����API��Ʒ���ƣ����Ų�Ʒ���̶��������޸ģ�
		final String domain = "dysmsapi.aliyuncs.com";//����API��Ʒ�������ӿڵ�ַ�̶��������޸ģ�
		final String accessKeyId = "LTAIISTXuiFekAAi";//���accessKeyId,�ο����ĵ�����2
		final String accessKeySecret = "0NCn7mkIXMeU7yPYCPaKf7uWhrToYX";//���accessKeySecret���ο����ĵ�����2
        //��ʼ��ascClient,��ʱ��֧�ֶ�region�������޸ģ�
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
				accessKeySecret);
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		IAcsClient acsClient = new DefaultAcsClient(profile);
		//��װ�������
		SendSmsRequest request = new SendSmsRequest();
		//ʹ��POST�ύ
		request.setMethod(MethodType.POST);
		// ����:�������ֻ���
		request.setPhoneNumbers(PhoneNumbers);
		// ����:����ǩ��-���ڶ��ſ���̨���ҵ�
		request.setSignName(SignName);
		// ����:����ģ��-���ڶ��ſ���̨���ҵ�
		request.setTemplateCode(TemplateCode);
		// ��ѡ:ģ���еı����滻JSON��,��ģ������Ϊ"�װ���${name},������֤��Ϊ${code}"ʱ,�˴���ֵΪ
		request.setTemplateParam(TemplateParam);
/*		"{\"Date\":\"" + date1
		+ "\",\"ID\":\""+ID+"\", \"ImeetingName\":\""+ImeetingName+"\",\"name\":\""+name+"\",\"phone\":\""+call+"\",\"Date1\":\"" +  date
		+ "\",\"ID1\":\""+ID+"\", \"ImeetingName1\":\""+ImeetingName+"\",\"name1\":\""+name+"\",\"Phone1\":\""+call+"\"}"*/
		
		// ѡ��-���ж�����չ��(�����������û�����Դ��ֶ�)
		// request.setSmsUpExtendCode("90997");

		// ��ѡ:outIdΪ�ṩ��ҵ����չ�ֶ�,�����ڶ��Ż�ִ��Ϣ�н���ֵ���ظ�������
		request.setOutId("yourOutId");

		// hint �˴����ܻ��׳��쳣��ע��catch
		SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
		return sendSmsResponse;
    }
	
	
public static String  calculate(String SignName,String TemplateCode,String PhoneNumbers,String TemplateParam)  throws Exception{
//      ���������ռ䡢���ʵ�ַ��������
       String SOAP_ACTION = "http://tempuri.org/sendmessage";
       String METHOD_NAME = "sendmessage";
       String NAMESPACE = "http://tempuri.org/";
       
       
       
       String server =App.Current.Server.Address;
       
       String URL ="http://"+server+":8028/MesWebService.asmx";
       
       Log.e("wywTag", URL);
       
       Element[] header = new Element[1];
       header[0] = new Element().createElement(NAMESPACE, "MySoapHeader");
       Element username = new Element().createElement(NAMESPACE,"UserName");
       username.addChild(Node.TEXT, "admin");
       header[0].addChild(Node.ELEMENT, username);
       Element pass = new Element().createElement(NAMESPACE, "PassWord");
       pass.addChild(Node.TEXT, "megmeet@p!@#$%8awdsz");
       header[0].addChild(Node.ELEMENT, pass);
       
    // ��һ��ʵ����SoapObject
       // ����ָ��webService�������ռ䣨�����WSDL�ĵ��п��Բ鿴�����ռ䣩���Լ����÷�������
       SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
       // �ڶ��������跽���в����Ļ�,���õ��÷�������

  
           rpc.addProperty("SignName", SignName);
           rpc.addProperty("TemplateCode", TemplateCode);
           rpc.addProperty("PhoneNumbers", PhoneNumbers);
           rpc.addProperty("TemplateParam", TemplateParam);
       // ������������SOAP������Ϣ(��������ΪSOAPЭ��汾�ţ�����Ҫ���õ�webService�а汾��һ��)
       SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
       // ���Ĳ���ע��Envelope
       envelope.enc="http://schemas.xmlsoap.org/soap/encoding/";
       envelope.env="http://schemas.xmlsoap.org/soap/envelope/"; 
       envelope.xsi="http://www.w3.org/2001/XMLSchema-instance";
       envelope.xsd="http://www.w3.org/2001/XMLSchema";
       envelope.bodyOut = rpc;
       envelope.headerOut=header;
       //envelope.headerIn
       envelope.dotNet = true;//ע�����������Ӧ�������������.net��������Ϊtrue
       envelope.setOutputSoapObject(rpc);
       
       new MarshalBase64().register(envelope);
       
       // ���岽������������󣬲�ָ��WSDL�ĵ�URL
       HttpTransportSE ht = new HttpTransportSE(URL);
       ht.debug = true;
       // ������:����WebService(���в���Ϊ1��nameSpace+�������ƣ�2��Envelope����)
       ht.call(NAMESPACE+METHOD_NAME, envelope);
        SoapPrimitive res = (SoapPrimitive) envelope.getResponse();

       // SoapObject detal = (SoapObject) envelope.bodyIn;
       //SoapObject detal = (SoapObject) envelope.bodyIn;
       Log.e("wywTag", res.toString());
       String rtnstr =res.toString();
       

       return rtnstr;
       // ���߲���������������

       }
}
