package dynsoft.xone.android.scanner.lianxin;

import android.os.Handler;
import android.os.Message;
import com.iData.barcodecontroll.BarcodeControll;

public class IDataBarcodeScanner {
    
    private static BarcodeControll _controller;

    private static boolean _stop = false;
    private static boolean _startScan = false;
    
    public static Handler ScanHandler = null;
    public static String ScanEncoding = "GBK";
    public static final int IDATA_BARCODE = 10;
    
    static {
        try {
            _controller = new BarcodeControll();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void Open() {
        _controller.Barcode_open();
        _stop = false;
        _startScan=false;
        new BarcodeReadThread().start();
    }

    public static void Close() {
        _stop = true;
        _startScan = false;
        _controller.Barcode_Close();
    }

    public static void StartScan() {   
        _controller.Barcode_StartScan();
        _startScan = true;
    }

    public static void StopScan() {
        _controller.Barcode_StopScan();
    }

    static class BarcodeReadThread extends Thread {
        public void run() {
            while (!_stop) {
                try {
                    Thread.sleep(50);
                    ReadBarcodeID();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static void ReadBarcodeID() {
        String barcode = null;
        byte[] buffer = _controller.Barcode_Read();
        try {
            barcode = new String(buffer, ScanEncoding);
        }catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        if (barcode != null && barcode.length() > 0 && ScanHandler != null && _startScan) {
            Message msg = new Message();
            msg.what = IDATA_BARCODE;
            msg.obj = barcode;
            System.out.println("msg.obj=" + msg.obj);
            ScanHandler.sendMessage(msg);
        }
    }
}
