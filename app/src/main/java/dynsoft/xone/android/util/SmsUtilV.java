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

//	// 产品名称:云通信短信API产品,开发者无需替换
//	private static final String product = "Dysmsapi";
//	// 产品域名,开发者无需替换
//	private static final String domain = "dysmsapi.aliyuncs.com";
//
//	// TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
//	private static final String accessKeyId = "LTAIISTXuiFekAAi";
//	private static final String accessKeySecret = "0NCn7mkIXMeU7yPYCPaKf7uWhrToYX";
	public static SendSmsResponse sendSms(String SignName,String TemplateCode,String PhoneNumbers,String TemplateParam) throws Exception {
   
		// 可自助调整超时时间
        //设置超时时间-可自行调整
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");

		//初始化ascClient需要的几个参数
		final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
		final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
		final String accessKeyId = "LTAIISTXuiFekAAi";//你的accessKeyId,参考本文档步骤2
		final String accessKeySecret = "0NCn7mkIXMeU7yPYCPaKf7uWhrToYX";//你的accessKeySecret，参考本文档步骤2
        //初始化ascClient,暂时不支持多region（请勿修改）
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
				accessKeySecret);
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		IAcsClient acsClient = new DefaultAcsClient(profile);
		//组装请求对象
		SendSmsRequest request = new SendSmsRequest();
		//使用POST提交
		request.setMethod(MethodType.POST);
		// 必填:待发送手机号
		request.setPhoneNumbers(PhoneNumbers);
		// 必填:短信签名-可在短信控制台中找到
		request.setSignName(SignName);
		// 必填:短信模板-可在短信控制台中找到
		request.setTemplateCode(TemplateCode);
		// 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
		request.setTemplateParam(TemplateParam);
/*		"{\"Date\":\"" + date1
		+ "\",\"ID\":\""+ID+"\", \"ImeetingName\":\""+ImeetingName+"\",\"name\":\""+name+"\",\"phone\":\""+call+"\",\"Date1\":\"" +  date
		+ "\",\"ID1\":\""+ID+"\", \"ImeetingName1\":\""+ImeetingName+"\",\"name1\":\""+name+"\",\"Phone1\":\""+call+"\"}"*/
		
		// 选填-上行短信扩展码(无特殊需求用户请忽略此字段)
		// request.setSmsUpExtendCode("90997");

		// 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
		request.setOutId("yourOutId");

		// hint 此处可能会抛出异常，注意catch
		SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
		return sendSmsResponse;
    }
	
	
public static String  calculate(String SignName,String TemplateCode,String PhoneNumbers,String TemplateParam)  throws Exception{
//      设置命名空间、访问地址、方法名
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
       
    // 第一：实例化SoapObject
       // 对象，指定webService的命名空间（从相关WSDL文档中可以查看命名空间），以及调用方法名称
       SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
       // 第二步：假设方法有参数的话,设置调用方法参数

  
           rpc.addProperty("SignName", SignName);
           rpc.addProperty("TemplateCode", TemplateCode);
           rpc.addProperty("PhoneNumbers", PhoneNumbers);
           rpc.addProperty("TemplateParam", TemplateParam);
       // 第三步：设置SOAP请求信息(参数部分为SOAP协议版本号，与你要调用的webService中版本号一致)
       SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
       // 第四步：注册Envelope
       envelope.enc="http://schemas.xmlsoap.org/soap/encoding/";
       envelope.env="http://schemas.xmlsoap.org/soap/envelope/"; 
       envelope.xsi="http://www.w3.org/2001/XMLSchema-instance";
       envelope.xsd="http://www.w3.org/2001/XMLSchema";
       envelope.bodyOut = rpc;
       envelope.headerOut=header;
       //envelope.headerIn
       envelope.dotNet = true;//注意跟服务器对应，如果服务器用.net开发，就为true
       envelope.setOutputSoapObject(rpc);
       
       new MarshalBase64().register(envelope);
       
       // 第五步：构建传输对象，并指明WSDL文档URL
       HttpTransportSE ht = new HttpTransportSE(URL);
       ht.debug = true;
       // 第六步:调用WebService(其中参数为1：nameSpace+方法名称，2：Envelope对象)
       ht.call(NAMESPACE+METHOD_NAME, envelope);
        SoapPrimitive res = (SoapPrimitive) envelope.getResponse();

       // SoapObject detal = (SoapObject) envelope.bodyIn;
       //SoapObject detal = (SoapObject) envelope.bodyIn;
       Log.e("wywTag", res.toString());
       String rtnstr =res.toString();
       

       return rtnstr;
       // 第七步：解析返回数据

       }
}
