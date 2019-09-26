package dynsoft.xone.android.util;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
public class SmsUtil {

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
}
