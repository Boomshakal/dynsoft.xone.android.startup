package dynsoft.xone.android.util;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
public class SmsUtil {

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
}
