package dynsoft.xone.android.wms;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.SwitchCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.PromptCallback;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.Book;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.start.FrmLogin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml.Encoding;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;

public class pn_yh_device_care_editorr extends pn_editor {

    public pn_yh_device_care_editorr(Context context) {
        super(context);
    }


    public TextCell txt_item_code_cell; //�豸��
    public TextCell txt_device_descrip;//�豸����
    public TextCell txt_sate;//�豸����
    public TextCell txt_care_date;//�豸��;
    public TextCell txt_device_guid;//���
    public TextCell txt_A_cell; //ά�ޱ�ע
    public int flage = 0;
    String code1;
    public TextCell txt_user;

    //���ö��ڵ�XML�ļ�
    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(
                R.layout.pn_yh_device_care_editorr, this, true);
        view.setLayoutParams(lp);
    }

    //  ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>();
    //  final String [] nItems = {"���","��ȡ����ƫ��","���ϲ���","�����","����","��Ӧ�Ʋ���","����"};

    //������ʾ�ؼ�
    @Override
    public void onPrepared() {

        super.onPrepared();


        this.txt_item_code_cell = (TextCell) this
                .findViewById(R.id.txt_item_code_cell);
        this.txt_device_descrip = (TextCell) this
                .findViewById(R.id.txt_device_descrip);
        this.txt_sate = (TextCell) this
                .findViewById(R.id.txt_device_kind);
        this.txt_care_date = (TextCell) this
                .findViewById(R.id.txt_device_use);
        this.txt_device_guid = (TextCell) this
                .findViewById(R.id.txt_device_guid);
        this.txt_A_cell = (TextCell) this
                .findViewById(R.id.txt_A_cell);

        this.txt_user = (TextCell) this
                .findViewById(R.id.txt_usernumber_code_cell);
        if (this.txt_item_code_cell != null) {
            this.txt_item_code_cell.setLabelText("�豸����");
            txt_item_code_cell.setReadOnly();
        }
        if (this.txt_device_descrip != null) {
            this.txt_device_descrip.setLabelText("�豸����");
            this.txt_device_descrip.setReadOnly();
        }
        if (this.txt_sate != null) {
            this.txt_sate.setLabelText("�豸λ��");
            this.txt_sate.setReadOnly();
        }
        if (this.txt_care_date != null) {
            this.txt_care_date.setLabelText("�ϴ�ά��ʱ��");
        }
        if (this.txt_device_guid != null) {
            this.txt_device_guid.setLabelText("���");
            this.txt_device_guid.setReadOnly();
        }
        if (this.txt_A_cell != null) {
            this.txt_A_cell.setLabelText("ά�ޱ�ע(OK����д�ϳ�������)");
        }
        if (this.txt_user != null) {
            this.txt_user.setLabelText("ά����");
            txt_user.setContentText(App.Current.UserCode);
        }

    }


    //ɨ����
    @Override
    public void onScan(final String barcode) {
        final String bar_code = barcode.trim();
        String str = bar_code.substring(0, 1);
        if (str.matches("[0-9]") || bar_code.startsWith("B")) {
            //ɨ��feeder
            this.txt_item_code_cell.setContentText(bar_code.toString());
            this.loadItem(bar_code);
        } else {
            App.Current.showError(this.getContext(), "�Ƿ����룬��ɨ����ȷ�Ķ�ά�룡" + bar_code.toString());
        }
    }


    //���������ж��Ƿ���Ҫά����
    public void load_ifmesh(String mesh_code) {

        //String sql1 ="SELECT count(*) as count FROM  fm_smt_position WHERE task_order=? AND item_code=?";
        String sql1 = "";
        Parameters p1 = new Parameters().add(1, mesh_code);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql1, p1, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_yh_device_care_editorr.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                DataRow row = result.Value;
                if (result.HasError) {
                    App.Current.showError(pn_yh_device_care_editorr.this.getContext(), result.Error);

                }


            }
        });
        //return count.toString();
    }


    ///��������������Ӧ������Ϣ
    public void loadItem(String code) {
        this.ProgressDialog.show();

        String sql = "select * from fm_work_dev where code = ?";
        Parameters p = new Parameters().add(1, code);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_yh_device_care_editorr.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                DataRow row = result.Value;
                if (result.HasError) {
                    App.Current.showError(pn_yh_device_care_editorr.this.getContext(), result.Error);
                    return;
                } else if (row == null) {
                    App.Current.showError(pn_yh_device_care_editorr.this.getContext(), "�Ҳ����豸��Ӧ����Ϣ");
                    return;
                } else {
                    //pn_yh_device_care_editorr.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
                    pn_yh_device_care_editorr.this.txt_device_descrip.setContentText(row.getValue("name", ""));
                    pn_yh_device_care_editorr.this.txt_sate.setContentText(row.getValue("location", ""));
                    pn_yh_device_care_editorr.this.txt_care_date.setContentText(String.valueOf(row.getValue("care_date", new Date())));
                    pn_yh_device_care_editorr.this.txt_device_guid.setContentText(row.getValue("owner", ""));
                }

            }
        });
    }


    /////
    //�ύ����
    ////
    @Override
    public void commit() {
        //��feeder��ǳ�����״̬�����ڴ�ά�޼�¼�����Ӽ�¼
        if (flage == 0) {
            Log.e("LZH2011", String.valueOf(flage));
            flage = 1;
            String device_code = this.txt_item_code_cell.getContentText().trim();
            if (device_code == null || device_code.length() == 0) {
                App.Current.showError(this.getContext(), "�豸���벻��Ϊ�գ�");
                return;
            }

            String device_kind = this.txt_sate.getContentText().trim();
            if (device_kind == null || device_kind.length() == 0) {
                App.Current.showError(this.getContext(), "�豸���Ͳ���Ϊ�գ�");
                return;
            }
            String a = this.txt_A_cell.getContentText().trim();
            if (a == null || a.length() == 0) {
                App.Current.showError(this.getContext(), "��ע����Ϊ�գ�����豸����������дOK");
                return;
            }

            String sql = "UPDATE [fm_work_dev] SET care_date = GETDATE() WHERE code = ? \n" +
                    "INSERT INTO fm_work_dev_care_log VALUES (?,?,?,GETDATE() ) ";
            Parameters p = new Parameters();
            p.add(1, txt_item_code_cell.getContentText()).add(2, txt_item_code_cell.getContentText()).add(3, txt_A_cell.getContentText()).add(4, App.Current.UserCode);
            App.Current.DbPortal.ExecuteNonQueryAsync(this.Connector, sql, p, new ResultHandler<Integer>() {
                @Override
                public void handleMessage(Message msg) {
                    pn_yh_device_care_editorr.this.ProgressDialog.dismiss();


                    Result<Integer> result = this.Value;
                    if (result.HasError) {
                        App.Current.showError(pn_yh_device_care_editorr.this.getContext(), result.Error);
                        return;
                    }
                    App.Current.toastInfo(pn_yh_device_care_editorr.this.getContext(), "�ύ�ɹ�");
                    clear();
                }
            });
        } else {
            App.Current.toastInfo(pn_yh_device_care_editorr.this.getContext(), "�벻Ҫ�����ύ");
        }
    }

    //��ӡ����
    public void printLabel() {
        //Map<String, String> parameters = new HashMap<String, String>();
        //parameters.put("item_code", this.Item_code);
        //App.Current.Print("mm_item_identifying_label", "��ӡ���ϱ�ʶ��", parameters);
    }

    //�������
    public void clear() {
        this.txt_item_code_cell.setContentText("");


        this.txt_A_cell.setContentText("");
        this.txt_care_date.setContentText("");
        this.txt_sate.setContentText("");
        this.txt_device_guid.setContentText("");
        this.txt_device_descrip.setContentText("");
        Link link = new Link("pane://x:code=pn_device_pro_mgr");
        link.Open(this, this.getContext(), null);
        flage = 0;
    }
}
