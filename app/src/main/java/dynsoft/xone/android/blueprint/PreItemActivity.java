package dynsoft.xone.android.blueprint;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import zicox.cpcl.PrinterInterface;
import zicox.cpcl.zkBluetoothPrinter;

public class PreItemActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    public static BluetoothAdapter myBluetoothAdapter;
    public String SelectedBDAddress;
    public PrinterInterface zkPrinter;
    StatusBox statusBox;
    public static String ErrorMessage;
    private Intent intent;
    private String task_order_code;
    private String plan_quantity;
    private String pre_item_code;
    private String lot_number;
    private String date_code;
    private String cur_quantity;
    private String top_item_name;
    private String user_message;
    private String numberIndex;
    private String radiatorWh;
    private boolean check_box;
    private ArrayList<String> itemMessages;
    private List<Map<String, String>> list;
    private ListView listView;
    private int number;
    private String create_time;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        intent = getIntent();
        task_order_code = intent.getStringExtra("task_order_code");
        plan_quantity = intent.getStringExtra("plan_quantity");
        pre_item_code = intent.getStringExtra("pre_item_code");
        lot_number = intent.getStringExtra("lot_number");
        date_code = intent.getStringExtra("date_code");
        cur_quantity = intent.getStringExtra("cur_quantity");
        top_item_name = intent.getStringExtra("top_item_name");
        user_message = intent.getStringExtra("user_message");
        radiatorWh = intent.getStringExtra("radiatorWh");
        check_box = intent.getBooleanExtra("check_box", false);
        numberIndex = intent.getStringExtra("number");
        itemMessages = intent.getStringArrayListExtra("itemMessages");
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        create_time=format.format(new Date());

        Log.e("len", "user_message :" + user_message);
        if (!ListBluetoothDevice()) finish();

        zkPrinter = new zkBluetoothPrinter();
        Button Button1 = (Button) findViewById(R.id.button1);
        statusBox = new StatusBox(this, Button1);
        ErrorMessage = "";
        Button1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                if (SelectedBDAddress == null) {
                    SelectedBDAddress = list.get(0).get("BDAddress");
                }
                Print1(SelectedBDAddress);


            }
        });
        Button Button2 = (Button) findViewById(R.id.button2);
        Button2.setVisibility(View.INVISIBLE);
        Button2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                if (SelectedBDAddress == null) {
                    SelectedBDAddress = list.get(0).get("BDAddress");
                }
                Print2(SelectedBDAddress);
            }
        });
    }

    public boolean ListBluetoothDevice() {
        list = new ArrayList<Map<String, String>>();
        listView = (ListView) findViewById(R.id.listView1);
        SimpleAdapter m_adapter = new SimpleAdapter(this, list,
                android.R.layout.simple_list_item_2,
                new String[]{"DeviceName", "BDAddress"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        listView.setAdapter(m_adapter);
        if ((myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()) == null) {
            Toast.makeText(this, "没有找到蓝牙适配器", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!myBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 2);
        }

        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() <= 0) return false;
        for (BluetoothDevice device : pairedDevices) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("DeviceName", device.getName());
            map.put("BDAddress", device.getAddress());
            list.add(map);
        }
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedBDAddress = list.get(position).get("BDAddress");
                if (((ListView) parent).getTag() != null) {
                    ((View) ((ListView) parent).getTag()).setBackgroundDrawable(null);
                }
                ((ListView) parent).setTag(view);
                view.setBackgroundColor(Color.BLUE);
            }
        });
        return true;
    }

    public void showMessage(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    public void Print1(String BDAddress) {
        statusBox.Show("正在连接...");
        if (zkPrinter.connect(BDAddress)) {
            statusBox.Show("打印机已连接");
        } else {
            statusBox.Close();
            return;
        }

        try {
            int status = zkPrinter.getStatus();
            if (status == 2) ErrorMessage = "打印机纸仓盖开";
            if (status == 1) ErrorMessage = "打印机缺纸";
            if (status == 3) ErrorMessage = "打印头过热";
            if (status == -1) ErrorMessage = "打印机没反应";
            if (status == 0)
                Content();
            else
                showMessage(ErrorMessage);
            statusBox.Close();
        } catch (Exception e) {
        }
        zkPrinter.disconnect();
    }

    public void Print2(final String BDAddress) {
        final EditText txt_split_qty = new EditText(PreItemActivity.this);
        new AlertDialog.Builder(PreItemActivity.this)
                .setTitle("请输入数量")
                .setView(txt_split_qty)
                //.setText()
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        number = Integer.parseInt(txt_split_qty.getText().toString());
                        statusBox.Show("正在连接...");
                        if (zkPrinter.connect(BDAddress)) {
                            statusBox.Show("打印机已连接");
                        } else {
                            statusBox.Close();
                            return;
                        }
                        if (number > 0) {
                            for (int i = 0; i < number; i++) {
                                try {
                                    int status = zkPrinter.getStatus();
                                    if (status == 2) ErrorMessage = "打印机纸仓盖开";
                                    if (status == 1) ErrorMessage = "打印机缺纸";
                                    if (status == 3) ErrorMessage = "打印头过热";
                                    if (status == -1) ErrorMessage = "打印机没反应";
                                    if (status == 0) Content();
                                    else
                                        showMessage(ErrorMessage);
                                    if (status != 0) {
                                        statusBox.Close();
                                        zkPrinter.disconnect();
                                        return;
                                    }
                                } catch (Exception e) {
                                }
                            }
                        } else {
                            App.Current.toastError(PreItemActivity.this, "输入有误！");
                            App.Current.playSound(R.raw.error);
                        }
                        statusBox.Close();
                        zkPrinter.disconnect();
                    }
                }).setNegativeButton("取消", null)
                .show();
    }

    public void ContentBefore() {
        zkPrinter.setPage(600, 335);//1650
        int lineW = 1;
        zkPrinter.drawLine(10, 10, 10, 290, lineW);
        zkPrinter.drawLine(590, 10, 590, 290, lineW);
        zkPrinter.drawLine(10, 10, 590, 10, lineW);
        zkPrinter.drawLine(10, 290, 590, 290, lineW);
        String d2Code = "";
        if (check_box) {
            d2Code = "CRQ:" + lot_number + "-" + pre_item_code + "-" + cur_quantity + "-" + date_code + "-LH";
        } else {
            d2Code = "CRQ:" + lot_number + "-" + pre_item_code + "-" + cur_quantity + "-" + date_code;
        }
        zkPrinter.drawText(30, 30, task_order_code, 2, 0, true, false, false);
        zkPrinter.drawText(500, 30, plan_quantity, 2, 0, true, false, false);
        zkPrinter.drawText(40, 60, "组件虚拟料号：", 2, 0, true, false, false);
        zkPrinter.drawText(240, 60, lot_number, 2, 0, true, false, false);
        zkPrinter.DrawBarcodeQRcode(40, 110, d2Code, 4, "128", 4);
        zkPrinter.drawText(60, 180, cur_quantity + "PCS", 2, 0, true, false, false);
        zkPrinter.drawText(60, 220, user_message, 2, 0, true, false, false);
        int x = 280;
        int y = 110;
        if (itemMessages.size() == 1) {
            String itemMessage = itemMessages.get(0);
            zkPrinter.drawText(x, 150, itemMessage, 2, 0, true, false, false);
        } else {
            for (int i = 0; i < itemMessages.size(); i++) {
                String itemMessage = itemMessages.get(i);
                zkPrinter.drawText(x, y, itemMessage, 2, 0, true, false, false);
                y += 30;
            }
        }
        zkPrinter.print(0, 0);
    }

    public void Content() {
        zkPrinter.setPage(550, 420);//1650
        int lineW = 1;

        String d2Code = "";
        String showLotNumber = lot_number;
        if (check_box) {
            d2Code = "CRQ:" + lot_number + "-" + pre_item_code + "-" + cur_quantity + "-" + date_code + "-LH";
        } else {
            d2Code = "CRQ:" + lot_number + "-" + pre_item_code + "-" + cur_quantity + "-" + date_code;
        }
        if (!TextUtils.isEmpty(numberIndex)) {
            d2Code = "CRQ:" + lot_number + "-" + pre_item_code + "-" + cur_quantity + "-" + date_code + "-" + numberIndex;
            showLotNumber = lot_number + "-" + numberIndex;
        }


        zkPrinter.drawText(10, 15, task_order_code, 2, 0, true, false, false);
        zkPrinter.drawText(240, 15, plan_quantity, 2, 0
                , true, false, false);
        zkPrinter.drawText(400, 15, radiatorWh, 2, 0, true, false, false);
        zkPrinter.drawText(10, 40, top_item_name, 2, 0, true, false, false);
        zkPrinter.drawText(10, 70, "组件虚拟料号：", 2, 0, true, false, false);
        zkPrinter.drawText(230, 70, showLotNumber, 2, 0, true, false, false);
        zkPrinter.DrawBarcodeQRcode(20, 140, d2Code, 4, "128", 4);
        zkPrinter.drawText(50, 290, cur_quantity + "PCS", 2, 0, true, false, false);
        zkPrinter.drawText(10, 330, TextUtils.isEmpty(user_message) ? "" : user_message, 2, 0, true, false, false);
        zkPrinter.drawText(230, 330, create_time, 2, 0, true, false, false);
        int x = 160;
        int y = 105;
        if (itemMessages.size() == 1) {
            String itemMessage = itemMessages.get(0);
            zkPrinter.drawText(x, 180, itemMessage, 2, 0, true, false, false);
        } else {
            for (int i = 0; i < itemMessages.size(); i++) {
                String itemMessage = itemMessages.get(i);
                zkPrinter.drawText(x, y, itemMessage, 2, 0, true, false, false);
                y += 30;
            }
        }
        zkPrinter.print(0, 0);
    }
}