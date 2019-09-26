package dynsoft.xone.android.wms;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import dynsoft.xone.android.activity.FirstKanbanActivity;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.sopactivity.ProductionKanbanActivity;

/**
 * Created by Administrator on 2018/2/27.
 */

public class pn_kanban_mgr extends pn_mgr {
    private View view;
    private TextCell textCell_1;
    private TextCell textCell_2;
    private String work_line;
    private int work_line_id;
    private String org_name;

    public pn_kanban_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_kanban_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        textCell_1 = (TextCell) findViewById(R.id.textcell_1);
        textCell_2 = (TextCell) findViewById(R.id.textcell_2);

        TelephonyManager telephonyManager = (TelephonyManager) App.Current.Workbench.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();

        if (textCell_1 != null) {
            textCell_1.setLabelText("MAC地址");
            textCell_1.setContentText(getMacAddress());
        }

        Toast.makeText(getContext(), getMacAddress(), Toast.LENGTH_SHORT).show();

        if (textCell_2 != null) {
            textCell_2.setLabelText("产线");
            textCell_2.setReadOnly();
        }
//        if(textCell_2 != null) {
//            textCell_2.setLabelText("产线");
//            textCell_2.setContentText(getMac() + "**" + deviceId);
//        }
            getMessageAndIntent();                          //获取数据 跳转
//        Intent intent = new Intent(getContext(), ProductionKanbanActivity.class);
//        App.Current.Workbench.startActivity(intent);
//        close();
    }

    private void getMessageAndIntent() {
        String sql = "exec p_core_get_kanban_message ?";
        Parameters p = new Parameters().add(1, getMacAddress());
        Toast.makeText(getContext(), getMacAddress(), Toast.LENGTH_LONG).show();
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.toastInfo(getContext(), value.Error);
                    return;
                }
                if (value.Value != null) {
                    work_line = value.Value.getValue("use_work_line", "");
                    work_line_id = value.Value.getValue("work_line_id", 0);
                    org_name = value.Value.getValue("org_name", "");
                    textCell_2.setContentText(work_line);
                    if (org_name.equals("生产一部")) {
                        startActivity(FirstKanbanActivity.class);
                    } else if (org_name.equals("生产二部")) {
                        startActivity(ProductionKanbanActivity.class);
                    } else if (org_name.equals("生产三部")) {
                        startActivity(ProductionKanbanActivity.class);
                    }
                }
            }
        });
    }

    public void startActivity(Class clazz) {
        Intent intent = new Intent(getContext(), clazz);
        intent.putExtra("work_line", work_line);
        intent.putExtra("work_line_id", work_line_id);
        App.Current.Workbench.startActivity(intent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                close();
            }
        }, 1000);
    }

    public String getMac() {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);


            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }

    private String getMacAddress() {
        String strMacAddr = null;
        try {
            InetAddress ip = getLocalInetAddress();
            Log.e("len", "ip:" + ip.getHostName());
            byte[] b = NetworkInterface.getByInetAddress(ip)
                    .getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                Log.e("len", "b:" + b[i]);
                if (i != 0) {
                    buffer.append(':');
                }

                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
            Log.e("len", "strMacAddr:" + strMacAddr);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.e("yttest", "strMacAddr:" + strMacAddr);

        String mac = getMac();
        Log.e("yttest", "mac:" + mac);
        return strMacAddr;
    }

    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    protected static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Log.e("len", "ni:" + ni.getName());
                Enumeration en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                Log.e("len", "en_ip:" + en_ip.toString());
                while (en_ip.hasMoreElements()) {
                    ip = (InetAddress) en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return ip;
    }

}
