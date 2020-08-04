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

public class Scan_Print_Activity extends Activity {
    /**
     * Called when the activity is first created.
     */
    public static BluetoothAdapter myBluetoothAdapter;
    public String SelectedBDAddress;
    public PrinterInterface zkPrinter;
    StatusBox statusBox;
    public static String ErrorMessage;
    private Intent intent;
    private String code;
    private String org_code;
    private String item_code;
    private String vendor_model;
    private String lot_number;
    private String vendor_lot;
    private String date_code;
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
        code = intent.getStringExtra("code");


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
                finish();
            }
        });
        Button Button2 = (Button) findViewById(R.id.button2);
        Button2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                if (SelectedBDAddress == null) {
                    SelectedBDAddress = list.get(0).get("BDAddress");
                }
                Print2(SelectedBDAddress);
                finish();
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
            Toast.makeText(this, "û���ҵ�����������", Toast.LENGTH_LONG).show();
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
        statusBox.Show("��������...");
        if (zkPrinter.connect(BDAddress)) {
            statusBox.Show("��ӡ��������");
        } else {
            statusBox.Close();
            return;
        }

        try {
            int status = zkPrinter.getStatus();
            if (status == 2) ErrorMessage = "��ӡ��ֽ�ָǿ�";
            if (status == 1) ErrorMessage = "��ӡ��ȱֽ";
            if (status == 3) ErrorMessage = "��ӡͷ����";
            if (status == -1) ErrorMessage = "��ӡ��û��Ӧ";
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
        final EditText txt_split_qty = new EditText(Scan_Print_Activity.this);
        new AlertDialog.Builder(Scan_Print_Activity.this)
                .setTitle("����������")
                .setView(txt_split_qty)
                //.setText()
                .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        number = Integer.parseInt(txt_split_qty.getText().toString());
                        statusBox.Show("��������...");
                        if (zkPrinter.connect(BDAddress)) {
                            statusBox.Show("��ӡ��������");
                        } else {
                            statusBox.Close();
                            return;
                        }
                        if (number > 0) {
                            for (int i = 0; i < number; i++) {
                                try {
                                    int status = zkPrinter.getStatus();
                                    if (status == 2) ErrorMessage = "��ӡ��ֽ�ָǿ�";
                                    if (status == 1) ErrorMessage = "��ӡ��ȱֽ";
                                    if (status == 3) ErrorMessage = "��ӡͷ����";
                                    if (status == -1) ErrorMessage = "��ӡ��û��Ӧ";
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
                            App.Current.toastError(Scan_Print_Activity.this, "��������");
                            App.Current.playSound(R.raw.error);
                        }
                        statusBox.Close();
                        zkPrinter.disconnect();
                    }
                }).setNegativeButton("ȡ��", null)
                .show();

    }


    public void Content() throws UnsupportedEncodingException {
        zkPrinter.setPage(600, 335);//1650
//        print_1stLable(zkPrinter, 16, 80 - 16, 544, 688);  //��ӡ��һ��
//        print_2stLable(zkPrinter, 16, 864-16, 544, 320);//��ӡ�ڶ���
//        print_3stLable(zkPrinter, 16, 1240-16,544, 320);//��ӡ������
//        print_stLable(zkPrinter, 16, 80-16, 544, 280);
        int lineW = 1;
        zkPrinter.DrawBarcode1D(10, 10, code, "128", 1, 45, 0);

        zkPrinter.print(0, 0);
    }


    private void print_1stLable(PrinterInterface printer, int x, int y, int width, int height) {
        int lineW = 2;
        int moveH = 0;
        printer.drawBox(x, y, x + width, y + height, lineW);
        //���к���
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
        //��������
        printer.drawLine(x + 344, y, x + 344, y + 360, lineW);
        printer.drawLine(x + 344, y + height - 168, x + 344, y + height, lineW);

        printer.drawText(x + 350, y + 25, "��׼���", 2, 0, true, false, false);
        printer.drawText(90 + x - 16, 175 + y - 80, "1 0 8 1 6 0 6 2 6 1 4 2 2", 2, 0, false, false, false);//20
        printer.drawText(426 + x - 16, 205 + y - 80, "�ĸ�", 4, 0, true, false, false);
        printer.drawText(56 + x - 6, 210 + y - 80, "��ľ��", 4, 0, false, false, false);
        printer.drawText(176 + x - 16, 200 + y - 80, "13132616784", 4, 0, false, false, false);
        printer.drawText(176 + x - 16, 226 + y - 80, "010-1234567", 4, 0, false, false, false);
        printer.drawText(24 + x - 16, 256 + y - 80, "����������Բ��԰��·", 2, 0, false, false, false);
        printer.drawText(24 + x - 16, 290 + y - 80, "�����౱԰����Ժ8��¥", 2, 0, false, false, false);
        printer.drawText(24 + x - 16, 324 + y - 80, "һ��Ԫ1001", 2, 0, false, false, false);
        printer.drawText(370 + x - 16, 252 + y - 80, "ʵ������:", 2, 0, false, false, false);
        printer.drawText(370 + x - 16, 282 + y - 80, "���(CM):", 2, 0, false, false, false);
        printer.drawText(370 + x - 16, 312 + y - 80, "�ʷ�:", 2, 0, false, false, false);
        printer.drawText(370 + x - 16, 342 + y - 80, "���۽��:", 2, 0, false, false, false);
        printer.drawText(370 + x - 16, 372 + y - 80, "��������:", 2, 0, false, false, false);
        printer.drawText(370 + x - 16, 402 + y - 80, "���úϼ�:", 2, 0, false, false, false);
        printer.drawText(470 + x - 16, 252 + y - 80, "0.9kg", 2, 0, false, false, false);
        printer.drawText(470 + x - 16, 282 + y - 80, "60*70*80", 2, 0, false, false, false);
        printer.drawText(470 + x - 16, 312 + y - 80, "20", 2, 0, false, false, false);
        printer.drawText(470 + x - 16, 342 + y - 80, "20", 2, 0, false, false, false);
        printer.drawText(470 + x - 16, 372 + y - 80, "0", 2, 0, false, false, false);
        printer.drawText(470 + x - 16, 402 + y - 80, "40", 2, 0, false, false, false);
        printer.drawText(56 + x - 16, 376 + y - 80, "���", 2, 0, false, false, false);
        printer.drawText(106 + x - 16, 372 + y - 80, "13833456784 010-123456789", 2, 0, false, false, false);
        printer.drawText(24 + x - 16, 398 + y - 80, "������ʯ������ɽ���йش�Ƽ�԰6��", 2, 0, false, false, false);
        printer.drawText(24 + x - 16, 418 + y - 80, "¥5��Ԫ502", 2, 0, false, false, false);
        printer.drawText(28 + x - 16, 458 + y - 80, "1��վ", 2, 0, false, false, false);
        printer.drawText(28 + x - 16, 508 + y - 80, "��A1������վ-��A2���ݺ�վ", 2, 0, false, false, false);
        printer.drawText(28 + x - 16, 548 + y - 80, "������Ͷ.Ͷ��һ��", 2, 0, false, false, false);
        printer.drawText(28 + x - 16, 616 + y - 80, "��ע:", 2, 0, false, false, false);
        printer.drawText(378 + x - 16, 610 + y - 80, "ǩ����:", 2, 0, false, false, false);
        printer.drawText(28 + x - 16, 690 + y - 80, "��ע: 1.�ջ�ǰ��ȷ�����װ�Ƿ����,������", 0, 0, false, false, false);
        printer.drawText(28 + x - 16, 710 + y - 80, "��.����������ܾ�ǩ��.����ǩ��,��ͬ�Ͽ���", 0, 0, false, false, false);
        printer.drawText(28 + x - 16, 730 + y - 80, "��װ���.2����ʹ��ջ��˵�ַ,���ռ���/��", 0, 0, false, false, false);
        printer.drawText(28 + x - 16, 750 + y - 80, "��������Ĵ�����ǩ��,��Ϊ�ʹ�.", 0, 0, false, false, false);
        printer.drawText(20 + x - 16, 774 + y - 80, "������:����   ", 2, 0, false, false, false);
        printer.drawText(220 + x - 16, 774 + y - 80, "���ӻ���: ��������·", 2, 0, false, false, false);
    }

    private void print_2stLable(PrinterInterface printer, int x, int y, int width, int height) {
        int lineW = 2;
        int moveH = 0;
        printer.drawBox(x, y, x + width, y + height, lineW);
        //���к���
        moveH = 80;
        printer.drawLine(x, y + moveH, x + width, y + moveH, lineW);
        moveH = 28 * 8;
        printer.drawLine(x, y + moveH, x + width - 200, y + moveH, lineW);
        moveH = 30 * 8;
        printer.drawLine(x + width - 200, y + moveH, x + width, y + moveH, lineW);
        moveH = 35 * 8;
        printer.drawLine(x + width - 200, y + moveH, x + width, y + moveH, lineW);

        //��������
        printer.drawLine(x + width - 200, y, x + width - 200, y + height, lineW);
        printer.DrawBarcode1D(101 + x - 16, 875 + y - 864, "1081606261422", "128", 1, 45, 0);
        printer.drawText(90 + x - 16, 920 + y - 864, "1 0 8 1 6 0 6 2 6 1 4 2 2", 2, 0, false, false, false);
        printer.drawText(426 + x - 16, 895 + y - 864, "�ĸ�", 2, 0, true, false, false);
        //	printer.image(x+9, y+86,  bitmap);
        printer.drawText(56 + x - 6, 958 + y - 864, "��ľ��", 4, 0, false, false, false);
        printer.drawText(176 + x - 16, 948 + y - 864, "13132616784", 4, 0, false, false, false);
        printer.drawText(176 + x - 16, 974 + y - 864, "010-1234567", 4, 0, false, false, false);
        printer.drawText(24 + x - 16, 1002 + y - 864, "����������Բ��԰��·", 4, 0, false, false, false);
        printer.drawText(24 + x - 16, 1034 + y - 864, "�����౱԰����Ժ8��¥", 4, 0, false, false, false);
        printer.drawText(24 + x - 16, 1062 + y - 864, "һ��Ԫ1001", 4, 0, false, false, false);
        printer.drawText(368 + x - 16, 958 + y - 864, "��ע:", 4, 0, false, false, false);
        printer.drawText(56 + x - 16, 1106 + y - 864, "���", 2, 0, false, false, false);
        printer.drawText(106 + x - 16, 1104 + y - 864, "13833456784 010-123456789", 2, 0, false, false, false);
        printer.drawText(24 + x - 16, 1128 + y - 864, "������ʯ������ɽ���йش�Ƽ�԰6��", 2, 0, false, false, false);
        printer.drawText(24 + x - 16, 1148 + y - 864, "¥5��Ԫ502", 2, 0, false, false, false);
        printer.drawText(386 + x - 16, 1110 + y - 864, "��Դ:΢��", 4, 0, false, false, false);
        printer.drawText(386 + x - 16, 1150 + y - 864, "�ͷ��绰: 11183", 2, 0, false, false, false);
    }

    private void print_3stLable(PrinterInterface printer, int x, int y, int width, int height) {
        int lineW = 2;
        int moveH = 0;
        printer.drawBox(x, y, x + width, y + height, lineW);
        //���к���
        moveH = 80;
        printer.drawLine(x, y + moveH, x + width, y + moveH, lineW);
        moveH = 28 * 8;
        printer.drawLine(x, y + moveH, x + width - 200, y + moveH, lineW);
        moveH = 30 * 8;
        printer.drawLine(x + width - 200, y + moveH, x + width, y + moveH, lineW);
        moveH = 35 * 8;
        printer.drawLine(x + width - 200, y + moveH, x + width, y + moveH, lineW);

        //��������
        printer.drawLine(x + width - 200, y, x + width - 200, y + height, lineW);
        printer.drawBoxText(x, y, x + width - 200, y + 120, "������ʯ������ɽ���йش��ui��԰6��12938888hod�����źõĹ���", 2, false, false, 10, 10);

    }
}