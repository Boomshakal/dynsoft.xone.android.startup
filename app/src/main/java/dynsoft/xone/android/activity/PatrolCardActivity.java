package dynsoft.xone.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import dynsoft.xone.android.adapter.PageTableAdapter;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.ui.ImageShower;

/**
 * Created by Administrator on 2018/11/16.
 */

public class PatrolCardActivity extends Activity implements View.OnClickListener {
    private static final int TACKPHOTOBACK = 0;
    private static final int RQ_SCAN_BY_CAMERA = 1;
    private ButtonTextCell textCellWorkLine;
    private ButtonTextCell textCellCardType;
    private ButtonTextCell textCellCardPerson;
    private ButtonTextCell textCellResult;
    private ButtonTextCell textCellComment;
    private ImageView imageViewPhoto;
    private TextView textViewTackPgoto;
    private TextView textViewbuttonUpload;
    private File file;
    private String imageName;
    private boolean isKeySHIFT;
    private StringBuilder _sb;
    private String edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrolcard);
        _sb = new StringBuilder();
        initView();
        setLable();
        setListener();
    }

    private void initView() {
        textCellWorkLine = (ButtonTextCell) findViewById(R.id.tc_workline);
        textCellCardType = (ButtonTextCell) findViewById(R.id.tc_cardtype);
        textCellCardPerson = (ButtonTextCell) findViewById(R.id.tc_cardperson);
        textCellResult = (ButtonTextCell) findViewById(R.id.tc_result);
        textCellComment = (ButtonTextCell) findViewById(R.id.tc_comment);
        imageViewPhoto = (ImageView) findViewById(R.id.imageview_photo);
        textViewTackPgoto = (TextView) findViewById(R.id.tackpgoto_again);
        textViewbuttonUpload = (TextView) findViewById(R.id.upload);
    }

    private void setLable() {
        if (textCellWorkLine != null) {
            textCellWorkLine.setLabelText("�߱�");
            textCellWorkLine.setReadOnly();
            initWorkLine();
            textCellWorkLine.Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(textCellCardType.getContentText())) {
                        App.Current.toastError(PatrolCardActivity.this, "����ѡ�������");
                    } else {
                        chooseWorkLine(textCellWorkLine);
                    }
                }
            });
        }

        if (textCellCardType != null) {
            textCellCardType.setLabelText("������");
            textCellCardType.setReadOnly();
        }

        if (textCellCardPerson != null) {
            textCellCardPerson.setLabelText("����Ա");
            textCellCardPerson.setReadOnly();
            textCellCardPerson.Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(textCellWorkLine.getContentText())) {    //��ѡ����
                        App.Current.toastError(PatrolCardActivity.this, "����ѡ������");
                    } else {
                        scanByCamera();
                    }
                }
            });
        }

        if (textCellResult != null) {
            textCellResult.setLabelText("���");
            textCellResult.setReadOnly();
        }

        if (textCellComment != null) {
            textCellComment.setLabelText("��ע");
            textCellComment.Button.setVisibility(View.INVISIBLE);
        }
    }

    public void scanByCamera() {
        PackageInfo packageInfo;
        try {
            packageInfo = this.getPackageManager().getPackageInfo("com.google.zxing.client.android", 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }

        if (packageInfo == null) {
            Dialog dialog = new AlertDialog.Builder(this)
                    .setTitle("����")
                    .setMessage("û�а�װ Barcode Scanner")
                    .setPositiveButton("ȷ��", null).create();
            dialog.show();

        } else {
            App.Current.playSound(R.raw.shake_beep);
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.setPackage("com.google.zxing.client.android");
            startActivityForResult(intent, RQ_SCAN_BY_CAMERA);
        }
    }

    private void chooseWorkLine(final ButtonTextCell textCellWorkLine) {
        String sql = "exec fm_patrol_card_record ?";
        Parameters p = new Parameters().add(1, textCellCardType.getContentText());
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PatrolCardActivity.this, value.Error);
                } else {
                    if (value.Value != null && value.Value.Rows.size() > 0) {
                        ArrayList<String> itemNumbers = new ArrayList<String>();
                        for (DataRow row : value.Value.Rows) {
//                            StringBuffer itemNumber = new StringBuffer();
//                            itemNumber.append(row.getValue("name", ""));
                            itemNumbers.add(row.getValue("name", ""));
                        }

                        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which >= 0) {
                                    DataRow row = value.Value.Rows.get(which);
                                    textCellWorkLine.setContentText(row.getValue("name", ""));
                                }
                                dialog.dismiss();
                            }
                        };
                        new AlertDialog.Builder(PatrolCardActivity.this).setTitle("��ѡ��")
                                .setSingleChoiceItems(itemNumbers.toArray(new String[0]), itemNumbers.indexOf
                                        (textCellWorkLine.getContentText().toString()), listener)
                                .setNegativeButton("ȡ��", null).show();
                    }
                }
            }
        });
    }

    private void setListener() {
        imageViewPhoto.setOnClickListener(this);
        textViewTackPgoto.setOnClickListener(this);
        textViewbuttonUpload.setOnClickListener(this);
        textCellResult.Button.setOnClickListener(new View.OnClickListener() {     //���
            @Override
            public void onClick(View view) {
                chooseResult(textCellResult);
            }
        });
        textCellCardType.Button.setOnClickListener(new View.OnClickListener() {   //������
            @Override
            public void onClick(View view) {
                chooseCardtype(textCellCardType);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageview_photo:
                if (file != null && file.exists()) {       //����Ѿ����գ� ��� ���ǷŴ�ͼƬ
                    Intent intent = new Intent(PatrolCardActivity.this, ImageShower.class);
                    intent.putExtra("photo_path", imageName);
                    startActivity(intent);
                } else {
                    intentCamera();         //û�����գ�
                }
                break;
            case R.id.tackpgoto_again:   //��������
                if (file != null && file.exists()) {    //�Ѿ�����Ƭ
                    file.delete();
                    intentCamera();
                } else {               //û����Ƭ
                    App.Current.toastError(PatrolCardActivity.this, "��ǰû����Ƭ����������");
                }
                break;
            case R.id.upload:      //�ϴ�
                if (TextUtils.isEmpty(textCellWorkLine.getContentText())) {
                    App.Current.toastError(PatrolCardActivity.this, "���豸MAC��ַû��ά��������ά����" + getMacAddress());
                    App.Current.playSound(R.raw.hook);
                } else if (TextUtils.isEmpty(textCellCardPerson.getContentText())) {
                    App.Current.toastError(PatrolCardActivity.this, "����ˢ��");
                    App.Current.playSound(R.raw.hook);
                } else if (TextUtils.isEmpty(textCellResult.getContentText())) {
                    App.Current.toastError(PatrolCardActivity.this, "��ѡ������");
                    App.Current.playSound(R.raw.hook);
                } else {      //�ύ����
                    try {
                        byte[] stringFromFile = getStringFromFile(file);           //��ȡ�ļ�������
//                        Map<String, String> entry = new HashMap<String, String>();
//                        entry.put("work_line", textCellWorkLine.getContentText().trim());
//                        entry.put("card_type", textCellCardType.getContentText().trim());
//                        entry.put("card_person", textCellCardPerson.getContentText().trim());
//                        entry.put("result", textCellResult.getContentText().trim());
//                        entry.put("comment", textCellComment.getContentText().trim());
//                        entry.put("img", String.valueOf(stringFromFile));
//                        String xml = XmlHelper.createXml("document", entry, "items", "item", null);
                        //�ύ����
                        String sql = "exec fm_patrol_card_create ?,?,?,?,?,?,?";
                        Parameters p = new Parameters().add(1, textCellWorkLine.getContentText().trim()).add(2, textCellCardType.getContentText().trim())
                                .add(3, textCellCardPerson.getContentText().trim().split("-")[0]).add(4, textCellResult.getContentText().trim())
                                .add(5, textCellComment.getContentText().trim()).add(6, getMacAddress()).add(7, stringFromFile);
                        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                            @Override
                            public void handleMessage(Message msg) {
                                Result<Integer> value = Value;
                                if (value.HasError) {
                                    App.Current.toastError(PatrolCardActivity.this, value.Error);
                                } else {
                                    if (value.Value > 0) {
                                        App.Current.toastInfo(PatrolCardActivity.this, "�ύ�ɹ���");
                                        clearAll();
                                    } else {
                                        App.Current.toastError(PatrolCardActivity.this, "�ύʧ�ܣ�");
                                    }
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
                break;
        }
    }

    private void clearAll() {
        textCellCardType.setContentText("");
        textCellCardPerson.setContentText("");
        textCellResult.setContentText("");
        textCellComment.setContentText("");
        if (file != null && file.exists()) {
            file.delete();
        }
        imageViewPhoto.setImageResource(R.drawable.icon_addpic_focused);
    }

    private void initWorkLine() {
        String sql = "select * from fm_work_dev where mac_address = ?";
        Parameters p = new Parameters().add(1, getMacAddress());
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PatrolCardActivity.this, value.Error);
                } else {
                    if (value.Value != null) {
                        String work_line_name = value.Value.getValue("work_line_name", "");
                        textCellWorkLine.setContentText(work_line_name);
                    } else {
                        App.Current.toastError(PatrolCardActivity.this, "���豸��MAC��ַû��ά��������ϵ�����Աά����");
                    }
                }
            }
        });
    }

    private byte[] getStringFromFile(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int len = 0;
        while (-1 != (len = fileInputStream.read(bytes))) {
            byteArrayOutputStream.write(bytes, 0, len);
        }
        byteArrayOutputStream.close();
        fileInputStream.close();
        return byteArrayOutputStream.toByteArray();


    }

    private void intentCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageName = Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".jpg";
        file = new File(imageName);
        Uri uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, TACKPHOTOBACK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case TACKPHOTOBACK:
                if (resultCode == RESULT_OK) {
                    if (file != null && file.exists()) {
                        Picasso.with(PatrolCardActivity.this).load(file).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageViewPhoto);
                    } else {
                        Picasso.with(PatrolCardActivity.this).load(R.drawable.icon_addpic_unfocused).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageViewPhoto);
                    }
                } else {
                    Picasso.with(PatrolCardActivity.this).load(R.drawable.icon_addpic_unfocused).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageViewPhoto);
                }
                break;
            case RQ_SCAN_BY_CAMERA:
                String contents = intent.getStringExtra("SCAN_RESULT");
                textCellCardPerson.setContentText(contents);
                break;
        }
    }

    private void chooseResult(final ButtonTextCell button_text_cell_4) {
        final ArrayList<String> result = new ArrayList<String>();
        result.add("OK");
        result.add("NG");

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    String time = result.get(which);
                    button_text_cell_4.setContentText(time);
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(PatrolCardActivity.this).setTitle("��ѡ��")
                .setSingleChoiceItems(result.toArray(new String[0]), result.indexOf(button_text_cell_4.getContentText()), listener)
                .setNegativeButton("ȡ��", null).show();
    }

    private void chooseCardtype(final ButtonTextCell textCellCardType) {
        String sql = "exec fm_get_patrol_record_card_type ?";
        Parameters p = new Parameters().add(1, textCellWorkLine.getContentText());
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PatrolCardActivity.this, value.Error);
                } else {
                    if (value.Value != null && value.Value.Rows.size() > 0) {
                        ArrayList<String> itemNumbers = new ArrayList<String>();
                        for (DataRow row : value.Value.Rows) {
                            StringBuffer itemNumber = new StringBuffer();
                            itemNumber.append(row.getValue("card_type", ""));
                            itemNumbers.add(row.getValue("card_type", ""));
                        }

                        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which >= 0) {
                                    DataRow row = value.Value.Rows.get(which);
                                    textCellCardType.setContentText(row.getValue("card_type", ""));
                                }
                                dialog.dismiss();
                            }
                        };
                        new AlertDialog.Builder(PatrolCardActivity.this).setTitle("��ѡ��")
                                .setSingleChoiceItems(itemNumbers.toArray(new String[0]), itemNumbers.indexOf
                                        (textCellCardType.getContentText().toString()), listener)
                                .setNegativeButton("ȡ��", null).show();
                    }
                }
            }
        });
    }

    private void checkShift(char ascallNoShift, char ascallOnShift) {
        if (this.isKeySHIFT) {
            _sb.append(ascallOnShift);
            this.isKeySHIFT = false;
        } else {
            _sb.append(ascallNoShift);
        }
    }

    private void handleNumPadKeys(int keyCode) {
        if (keyCode <= 153) {
            _sb.append((char) (keyCode - 96));
        } else if (keyCode == 154) {
            _sb.append('/');
        } else if (keyCode == 155) {
            _sb.append('*');
        } else if (keyCode == 156) {
            _sb.append('-');
        } else if (keyCode == 157) {
            _sb.append('+');
        } else if (keyCode == 158) {
            _sb.append('.');
        }
    }

    private void handleTopNumKeys(int keyCode) {
        if (keyCode >= 7 && keyCode <= 16) {
            switch (keyCode) {
                case 7:
                    this.checkShift('0', ')');
                    break;
                case 8:
                    this.checkShift('1', '!');
                    break;
                case 9:
                    this.checkShift('2', '@');
                    break;
                case 10:
                    this.checkShift('3', '#');
                    break;
                case 11:
                    this.checkShift('4', '$');
                    break;
                case 12:
                    this.checkShift('5', '%');
                    break;
                case 13:
                    this.checkShift('6', '^');
                    break;
                case 14:
                    this.checkShift('7', '&');
                    break;
                case 15:
                    this.checkShift('8', '*');
                    break;
                case 16:
                    this.checkShift('9', '(');
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode <= 6 && keyCode != 3 && keyCode != 4) {
            return super.dispatchKeyEvent(event);
        } else if (keyCode == 3 || keyCode == 4) {
            return super.onKeyDown(keyCode, event);
        } else {
            if (keyCode == 59) {
                this.isKeySHIFT = true;
            }

            if (keyCode == 66) {
                this.isKeySHIFT = false;
                edittext = _sb.toString().toUpperCase().replace("\n", "");
                if (edittext.length() == 10) {
                    String sql = "exec p_fm_card_login ?";
                    Parameters p = new Parameters().add(1, edittext);
                    App.Current.DbPortal.ExecuteRecordAsync("core_hr_and", sql, p, new ResultHandler<DataRow>() {
                        @Override
                        public void handleMessage(Message msg) {
                            Result<DataRow> value = Value;
                            if (value.HasError) {
                                App.Current.toastError(PatrolCardActivity.this, value.Error);
                            } else {
                                if (value.Value != null) {
                                    String user_code = value.Value.getValue("user_code", "");
                                    String user_name = value.Value.getValue("username", "");
                                    textCellCardPerson.setContentText(user_code);
                                } else {
                                    App.Current.toastError(PatrolCardActivity.this, "��Ƭ�����ڱ�ϵͳ��");
                                }
                            }
                        }
                    });
                }
                _sb.delete(0, _sb.length());
                return true;
            } else {
                if (keyCode >= 7 && keyCode <= 16) {
                    this.handleTopNumKeys(keyCode);
                } else if (keyCode >= 29 && keyCode <= 54) {
                    this.checkShift((char) (keyCode + 68), (char) (keyCode + 36));
                } else {
                    if (keyCode < 144 || keyCode > 158) {
                        switch (keyCode) {
                            case 55:
                                this.checkShift(',', '<');
                                break;
                            case 56:
                                this.checkShift('.', '>');
                                break;
                            case 57:
                            case 58:
                            case 59:
                            case 60:
                            case 61:
                            case 63:
                            case 64:
                            case 65:
                            case 66:
                            case 67:
                            default:
                                return false;
                            case 62:
                                _sb.append(' ');
                                break;
                            case 68:
                                this.checkShift('`', '~');
                                break;
                            case 69:
                                this.checkShift('-', '_');
                                break;
                            case 70:
                                this.checkShift('=', '+');
                                break;
                            case 71:
                                this.checkShift('[', '{');
                                break;
                            case 72:
                                this.checkShift(']', '}');
                                break;
                            case 73:
                                this.checkShift('\\', '|');
                                break;
                            case 74:
                                this.checkShift(';', ':');
                                break;
                            case 75:
                                this.checkShift('\'', '"');
                                break;
                            case 76:
                                this.checkShift('/', '?');
                        }
                        return true;
                    }

                    this.handleNumPadKeys(keyCode);
                }
            }
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 拦截物理键盘事件
        if (event.getKeyCode() > 6) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                this.onKeyDown(event.getKeyCode(), event);
                return true;

            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                this.onKeyUp(event.getKeyCode(), event);
                return true;
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private String getMacAddress() {
        String strMacAddr = null;
        try {
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip)
                    .getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return strMacAddr;
    }

    /**
     * ��ȡ�ƶ��豸����IP
     *
     * @return
     */
    protected InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //�о�
            Enumeration en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//�Ƿ���Ԫ��
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//�õ���һ��Ԫ��
                Enumeration en_ip = ni.getInetAddresses();//�õ�һ��ip��ַ���о�
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
