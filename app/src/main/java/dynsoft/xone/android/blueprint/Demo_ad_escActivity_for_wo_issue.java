package dynsoft.xone.android.blueprint;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import zicox.cpcl.PrinterInterface;
import zicox.cpcl.zkBluetoothPrinter;

public class Demo_ad_escActivity_for_wo_issue extends Activity {
    /**
     * Called when the activity is first created.
     */
    public static BluetoothAdapter myBluetoothAdapter;
    public String SelectedBDAddress;
    public PrinterInterface zkPrinter;
    StatusBox statusBox;
    public static String ErrorMessage;
    private Intent intent;
    private String org_code;
    private String item_code;
    private String vendor_model;
    private String lot_number;
    private String vendor_lot;
    private String date_code;
    private String station_code;
    private String task_order_code;
    private String quantity;
    private String ut;
    private String cur_date;
    private int number;
    private List<Map<String, String>> list;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        intent = getIntent();
        intent = getIntent();
        org_code = intent.getStringExtra("org_code");
        item_code = intent.getStringExtra("item_code");
        vendor_model = intent.getStringExtra("vendor_model");
        lot_number = intent.getStringExtra("lot_number");
        vendor_lot = intent.getStringExtra("vendor_lot");
        date_code = intent.getStringExtra("date_code");
        station_code = intent.getStringExtra("station_code");
        task_order_code = intent.getStringExtra("task_order_code");
        quantity = intent.getStringExtra("quantity");
        cur_date = intent.getStringExtra("cur_date");
        ut = intent.getStringExtra("ut");

//        Map<String, String> parameters = (Map<String, String>) intent.getSerializableExtra("parameters");
//        org_code = parameters.get("org_code");
//        item_code = parameters.get("item_code");
//        vendor_model = parameters.get("vendor_model");
//        lot_number = parameters.get("lot_number");
//        vendor_lot = parameters.get("vendor_lot");
//        date_code = parameters.get("date_code");
//        quantity = parameters.get("quantity");
//        ut = parameters.get("ut");
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
        } catch (UnsupportedEncodingException e) {
        }
        zkPrinter.disconnect();
    }

    public void Print2(final String BDAddress) {
        final EditText txt_split_qty = new EditText(Demo_ad_escActivity_for_wo_issue.this);
        new AlertDialog.Builder(Demo_ad_escActivity_for_wo_issue.this)
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
                                } catch (UnsupportedEncodingException e) {
                                }
                            }
                        } else {
                            App.Current.toastError(Demo_ad_escActivity_for_wo_issue.this, "输入有误！");
                            App.Current.playSound(R.raw.error);
                        }
                        statusBox.Close();
                        zkPrinter.disconnect();
                    }
                }).setNegativeButton("取消", null)
                .show();

    }


    public void Content() throws UnsupportedEncodingException {
        zkPrinter.setPage(600, 335);//1650
//        print_1stLable(zkPrinter, 16, 80 - 16, 544, 688);  //打印第一联
//        print_2stLable(zkPrinter, 16, 864-16, 544, 320);//打印第二联
//        print_3stLable(zkPrinter, 16, 1240-16,544, 320);//打印第三联
//        print_stLable(zkPrinter, 16, 80-16, 544, 280);
        int lineW = 1;
        zkPrinter.drawLine(10, 10, 10, 290, lineW);
        zkPrinter.drawLine(590, 10, 590, 290, lineW);
        zkPrinter.drawLine(10, 10, 590, 10, lineW);
        zkPrinter.drawLine(10, 290, 590, 290, lineW);

        zkPrinter.drawLine(130, 10, 130, 250, lineW);
        zkPrinter.drawLine(130, 60, 590, 60, lineW);
        zkPrinter.drawLine(130, 100, 590, 100, lineW);
        zkPrinter.drawLine(130, 150, 590, 150, lineW);
        zkPrinter.drawLine(130, 200, 590, 200, lineW);
        zkPrinter.drawLine(10, 250, 590, 250, lineW);
        zkPrinter.drawLine(320, 10, 320, 250, lineW);

//        String d2Code = "CRQ:" + lot_number + "-" + item_code + "-" + quantity + "-" + date_code;
//        String d2Code = "M3522";
//        zkPrinter.DrawBarcodeQRcode(25, 30, d2Code, 6, "128", 6);

//        String d2aCode = "（21)USLH-0006(93）26074441-02";
//        zkPrinter.DrawBarcodeQRcode(200, 30, d2aCode, 4, "128", 4);
//
//        String d2aaCode = "（21）USLH-0006（93）26074441-02";
//        zkPrinter.DrawBarcodeQRcode(400, 30, d2aaCode, 4, "128", 4);

        String d2Code = "CRQ:" + lot_number + "-" + item_code + "-" + quantity + "-" + date_code;
        zkPrinter.DrawBarcodeQRcode(25, 30, d2Code, 3, "128", 3);
        zkPrinter.drawText(30, 200, quantity + " " + ut, 2, 0, true, false, false);
        zkPrinter.drawText(140, 20, "MEG.PN", 2, 0, true, false, false);
        zkPrinter.drawText(140, 70, "VERDOR.PN", 2, 0, true, false, false);
        zkPrinter.drawText(140, 110, "MEG.LOT", 2, 0, true, false, false);
        zkPrinter.drawText(140, 160, "VERDOR.LOT", 2, 0, true, false, false);
        zkPrinter.drawText(140, 210, "DC", 2, 0, true, false, false);
//        zkPrinter.DrawBarcode1D(80, 260, App.Current.UserCode + "," + format, "128", 1, 25, 0);
        zkPrinter.drawText(190, 260, App.Current.UserCode + "," + cur_date, 2, 0, true, false, false);
        zkPrinter.DrawBarcodeQRcode(330, 15, "R:" + item_code, 2, "128", 2);
        zkPrinter.drawLine(540, 10, 540, 60, lineW);
        zkPrinter.drawText(380, 20, item_code, 2, 0, true, false, false);
        zkPrinter.drawText(543, 20, org_code, 2, 0, true, false, false);
        zkPrinter.drawText(330, 70, vendor_model, 2, 0, true, false, false);
        zkPrinter.drawText(330, 110, lot_number, 2, 0, true, false, false);
        zkPrinter.drawText(330, 160, vendor_lot, 2, 0, true, false, false);
        zkPrinter.drawText(330, 210, date_code, 2, 0, true, false, false);
        zkPrinter.drawText(30, 160, station_code, 2, 0, true, false, false);
        zkPrinter.drawText(30, 260, task_order_code, 2, 0, true, false, false);

        //        zkPrinter.DrawBarcode1D(520, 10, "1081606261422", "128", 1, 45, 0);
//        zkPrinter.DrawBarcodeQRcode(101 - 16, 875 - 864, "1081606261422", 1, "128", 45);
        zkPrinter.print(0, 0);
    }


    private void print_1stLable(PrinterInterface printer, int x, int y, int width, int height) {
        int lineW = 2;
        int moveH = 0;
        printer.drawBox(x, y, x + width, y + height, lineW);
        //所有横线
        moveH = 120;
        printer.drawLine(x, y + moveH, x + width, y + moveH, lineW);
        moveH = 160;
        printer.drawLine(x + width - 200, y + moveH, x + width, y + moveH, lineW);
        moveH = 280;
        printer.drawLine(x, y + moveH, x + width - 200, y + moveH, lineW);
        moveH = 360;
        printer.drawLine(x, y + moveH, x + width, y + moveH, lineW);
        moveH = 520;
        printer.drawLine(x, y + moveH, x + width, y + moveH, lineW);
        moveH = 600;
        printer.drawLine(x, y + moveH, x + width - 200, y + moveH, lineW);
        //所有竖线
        printer.drawLine(x + 344, y, x + 344, y + 360, lineW);
        printer.drawLine(x + 344, y + height - 168, x + 344, y + height, lineW);

        printer.drawText(x + 350, y + 25, "标准快递", 2, 0, true, false, false);
        printer.drawText(90 + x - 16, 175 + y - 80, "1 0 8 1 6 0 6 2 6 1 4 2 2", 2, 0, false, false, false);//20
        printer.drawText(426 + x - 16, 205 + y - 80, "寄付", 4, 0, true, false, false);
        printer.drawText(56 + x - 6, 210 + y - 80, "李木子", 4, 0, false, false, false);
        printer.drawText(176 + x - 16, 200 + y - 80, "13132616784", 4, 0, false, false, false);
        printer.drawText(176 + x - 16, 226 + y - 80, "010-1234567", 4, 0, false, false, false);
        printer.drawText(24 + x - 16, 256 + y - 80, "北京海淀区圆明园东路", 2, 0, false, false, false);
        printer.drawText(24 + x - 16, 290 + y - 80, "北大燕北园家属院8号楼", 2, 0, false, false, false);
        printer.drawText(24 + x - 16, 324 + y - 80, "一单元1001", 2, 0, false, false, false);
        printer.drawText(370 + x - 16, 252 + y - 80, "实际重量:", 2, 0, false, false, false);
        printer.drawText(370 + x - 16, 282 + y - 80, "体积(CM):", 2, 0, false, false, false);
        printer.drawText(370 + x - 16, 312 + y - 80, "邮费:", 2, 0, false, false, false);
        printer.drawText(370 + x - 16, 342 + y - 80, "保价金额:", 2, 0, false, false, false);
        printer.drawText(370 + x - 16, 372 + y - 80, "其他费用:", 2, 0, false, false, false);
        printer.drawText(370 + x - 16, 402 + y - 80, "费用合计:", 2, 0, false, false, false);
        printer.drawText(470 + x - 16, 252 + y - 80, "0.9kg", 2, 0, false, false, false);
        printer.drawText(470 + x - 16, 282 + y - 80, "60*70*80", 2, 0, false, false, false);
        printer.drawText(470 + x - 16, 312 + y - 80, "20", 2, 0, false, false, false);
        printer.drawText(470 + x - 16, 342 + y - 80, "20", 2, 0, false, false, false);
        printer.drawText(470 + x - 16, 372 + y - 80, "0", 2, 0, false, false, false);
        printer.drawText(470 + x - 16, 402 + y - 80, "40", 2, 0, false, false, false);
        printer.drawText(56 + x - 16, 376 + y - 80, "杨尔", 2, 0, false, false, false);
        printer.drawText(106 + x - 16, 372 + y - 80, "13833456784 010-123456789", 2, 0, false, false, false);
        printer.drawText(24 + x - 16, 398 + y - 80, "北京市石景区西山汇中关村科技园6号", 2, 0, false, false, false);
        printer.drawText(24 + x - 16, 418 + y - 80, "楼5单元502", 2, 0, false, false, false);
        printer.drawText(28 + x - 16, 458 + y - 80, "1航站", 2, 0, false, false, false);
        printer.drawText(28 + x - 16, 508 + y - 80, "京A1北京航站-粤A2广州航站", 2, 0, false, false, false);
        printer.drawText(28 + x - 16, 548 + y - 80, "建设揽投.投递一段", 2, 0, false, false, false);
        printer.drawText(28 + x - 16, 616 + y - 80, "备注:", 2, 0, false, false, false);
        printer.drawText(378 + x - 16, 610 + y - 80, "签收人:", 2, 0, false, false, false);
        printer.drawText(28 + x - 16, 690 + y - 80, "备注: 1.收货前请确认外包装是否完好,有无破", 0, 0, false, false, false);
        printer.drawText(28 + x - 16, 710 + y - 80, "损.如有问提请拒绝签收.如已签收,视同认可外", 0, 0, false, false, false);
        printer.drawText(28 + x - 16, 730 + y - 80, "包装完好.2快件送达收货人地址,经收件人/寄", 0, 0, false, false, false);
        printer.drawText(28 + x - 16, 750 + y - 80, "件人允许的代收人签名,视为送达.", 0, 0, false, false, false);
        printer.drawText(20 + x - 16, 774 + y - 80, "验视人:张三   ", 2, 0, false, false, false);
        printer.drawText(220 + x - 16, 774 + y - 80, "验视机构: 北京永安路", 2, 0, false, false, false);
    }

    private void print_2stLable(PrinterInterface printer, int x, int y, int width, int height) {
        int lineW = 2;
        int moveH = 0;
        printer.drawBox(x, y, x + width, y + height, lineW);
        //所有横线
        moveH = 80;
        printer.drawLine(x, y + moveH, x + width, y + moveH, lineW);
        moveH = 28 * 8;
        printer.drawLine(x, y + moveH, x + width - 200, y + moveH, lineW);
        moveH = 30 * 8;
        printer.drawLine(x + width - 200, y + moveH, x + width, y + moveH, lineW);
        moveH = 35 * 8;
        printer.drawLine(x + width - 200, y + moveH, x + width, y + moveH, lineW);

        //所有竖线
        printer.drawLine(x + width - 200, y, x + width - 200, y + height, lineW);
        printer.DrawBarcode1D(101 + x - 16, 875 + y - 864, "1081606261422", "128", 1, 45, 0);
        printer.drawText(90 + x - 16, 920 + y - 864, "1 0 8 1 6 0 6 2 6 1 4 2 2", 2, 0, false, false, false);
        printer.drawText(426 + x - 16, 895 + y - 864, "寄付", 2, 0, true, false, false);
        //	printer.image(x+9, y+86,  bitmap);
        printer.drawText(56 + x - 6, 958 + y - 864, "李木子", 4, 0, false, false, false);
        printer.drawText(176 + x - 16, 948 + y - 864, "13132616784", 4, 0, false, false, false);
        printer.drawText(176 + x - 16, 974 + y - 864, "010-1234567", 4, 0, false, false, false);
        printer.drawText(24 + x - 16, 1002 + y - 864, "北京海淀区圆明园东路", 4, 0, false, false, false);
        printer.drawText(24 + x - 16, 1034 + y - 864, "北大燕北园家属院8号楼", 4, 0, false, false, false);
        printer.drawText(24 + x - 16, 1062 + y - 864, "一单元1001", 4, 0, false, false, false);
        printer.drawText(368 + x - 16, 958 + y - 864, "备注:", 4, 0, false, false, false);
        printer.drawText(56 + x - 16, 1106 + y - 864, "杨尔", 2, 0, false, false, false);
        printer.drawText(106 + x - 16, 1104 + y - 864, "13833456784 010-123456789", 2, 0, false, false, false);
        printer.drawText(24 + x - 16, 1128 + y - 864, "北京市石景区西山汇中关村科技园6号", 2, 0, false, false, false);
        printer.drawText(24 + x - 16, 1148 + y - 864, "楼5单元502", 2, 0, false, false, false);
        printer.drawText(386 + x - 16, 1110 + y - 864, "来源:微信", 4, 0, false, false, false);
        printer.drawText(386 + x - 16, 1150 + y - 864, "客服电话: 11183", 2, 0, false, false, false);
    }

    private void print_3stLable(PrinterInterface printer, int x, int y, int width, int height) {
        int lineW = 2;
        int moveH = 0;
        printer.drawBox(x, y, x + width, y + height, lineW);
        //所有横线
        moveH = 80;
        printer.drawLine(x, y + moveH, x + width, y + moveH, lineW);
        moveH = 28 * 8;
        printer.drawLine(x, y + moveH, x + width - 200, y + moveH, lineW);
        moveH = 30 * 8;
        printer.drawLine(x + width - 200, y + moveH, x + width, y + moveH, lineW);
        moveH = 35 * 8;
        printer.drawLine(x + width - 200, y + moveH, x + width, y + moveH, lineW);

        //所有竖线
        printer.drawLine(x + width - 200, y, x + width - 200, y + height, lineW);
        printer.drawBoxText(x, y, x + width - 200, y + 120, "北京市石景区西山汇中关村科ui技园6号12938888hod不会封号好的哈德", 2, false, false, 10, 10);

    }
}