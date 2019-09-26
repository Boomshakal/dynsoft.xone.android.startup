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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import dynsoft.xone.android.blueprint.Demo_ad_escActivity2;
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

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;

public class pn_smt_steel_mesh_storage_editorr extends pn_editor {

    public pn_smt_steel_mesh_storage_editorr(Context context) {
        super(context);
    }


    public TextCell txt_steel_mesh_code_cell; //��������
    public TextCell txt_usernumber_code_cell; //�û�����
    public TextCell txt_plan_count_cell; //Ԥ��ʹ�ô���
    public TextCell txt_accumulative_use_count_cell; //�ۼ�ʹ�ô���
    public TextCell txt_is_scrap; //�Ƿ񱨷�
    public TextCell txt_storage_code_cell;
    public TextCell txt_user_cell;
    public CheckBox chk_is_scrap;
    public CheckBox chk_is_scrap2;
    public TextCell txt_B_cell; //1��
    public TextCell txt_C_cell; //2��
    public TextCell txt_D_cell; //3��
    public TextCell txt_E_cell; //4��

    private int scan_count;
    private boolean checkbool;
    private DataRow _order_row;
    private DataRow _lot_row;
    private Integer _total = 0;
    private Long _rownum;
    private String work_order_code;
    private String task_order_code;
    private String item_code;
    private String item_name;
    private BigDecimal plan_count;


    //boolean is_scrap = this.chk_is_scrap.isChecked();
    //���ö��ڵ�XML�ļ�
    @Override
    public void setContentView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(
                R.layout.pn_smt_steel_mesh_storage_editorr, this, true);
        view.setLayoutParams(lp);
    }

    //  ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>();
    //  final String [] nItems = {"���","��ȡ����ƫ��","���ϲ���","�����","����","��Ӧ�Ʋ���","����"};

    //������ʾ�ؼ�
    @Override
    public void onPrepared() {

        super.onPrepared();

        task_order_code = this.Parameters.get("task_order", "");
        item_code = this.Parameters.get("item_code", "");
        item_name = this.Parameters.get("item_name", "");
        plan_count = this.Parameters.get("plan_count", new BigDecimal(0));
        plan_count = this.Parameters.get("plan_count", new BigDecimal(0));

        scan_count = 0;
        checkbool = true;


        //button.setOnClickListener(this);

        this.txt_steel_mesh_code_cell = (TextCell) this
                .findViewById(R.id.txt_steel_mesh_code_cell);

        this.txt_usernumber_code_cell = (TextCell) this
                .findViewById(R.id.txt_usernumber_code_cell);

        this.txt_storage_code_cell = (TextCell) this
                .findViewById(R.id.txt_storage_code_cell);

        this.txt_user_cell = (TextCell) this.findViewById(R.id.txt_user_cell);

        this.chk_is_scrap = (CheckBox) this.findViewById(R.id.chk_is_scrap);
        //this.chk_is_scrap2 = (CheckBox) this.findViewById(R.id.chk_is_scrap2);

        if (this.txt_steel_mesh_code_cell != null) {
            this.txt_steel_mesh_code_cell.setLabelText("��������");
            //this.txt_steel_mesh_code_cell.setReadOnly();
        }

        if (this.txt_storage_code_cell != null) {
            this.txt_storage_code_cell.setLabelText("�洢����");

        }
        if (this.txt_user_cell != null) {
            this.txt_user_cell.setLabelText("������");
           // txt_user_cell
          this.txt_user_cell.setContentText("MZ5493");
        }

    }


    //ɨ����
    @Override
    public void onScan(final String barcode) {
        final String bar_code = barcode.trim();
        if (bar_code.startsWith("GWS")) {
            //ɨ��feeder
            //this.txt_storage_code_cell.setContentText(bar_code.toString().split(":")[1]);
            //storage_code=bar_code.toString().split(":")[1];
            this.txt_storage_code_cell.setContentText(bar_code.toString());
            String storage = bar_code.toString();
            load_ifmesh(storage);

        } else if (bar_code.startsWith("R")) {
            this.txt_steel_mesh_code_cell.setContentText(bar_code.toString());
            loadsteel_mesh(bar_code.toString());
            //load_ifmesh(task_order_code.toString().split("-")[0], bar_code);
            //loadsteel_mesh(bar_code);
            //this.txt_steel_mesh_code_cell
        }
        else if(bar_code.startsWith("MZ")){
            this.txt_user_cell.setContentText(bar_code.toString());

        }
            else
        {
            App.Current.showError(this.getContext(), "�Ƿ����룬��ɨ����ȷ�Ķ�ά�룡" + bar_code.toString());
        }
    }
    public void load_ifmesh(String storage) {
        this.ProgressDialog.show();
        String sql = "SELECT   item_code from mm_smt_steel_mesh_storage_bind WHERE storage_code =? ";
        Parameters p = new Parameters().add(1, storage);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_smt_steel_mesh_storage_editorr.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_smt_steel_mesh_storage_editorr.this.getContext(), result.Error);
                    return;
                }
                if (result.Value == null) {
                    App.Current.showError(pn_smt_steel_mesh_storage_editorr.this.getContext(), "�òֿ�û�б��󶨸���������а󶨲���");

                    return;
                }
                DataRow row = result.Value;


                pn_smt_steel_mesh_storage_editorr.this.txt_steel_mesh_code_cell.setContentText(row.getValue("item_code", ""));
                pn_smt_steel_mesh_storage_editorr.this.txt_user_cell.setContentText(row.getValue("user_info", ""));

            }
        });

    }




//    public void load_ifmesh(String work_order_code, String mesh_code) {
//
//        String sql1 = "SELECT count(*) as count FROM  fm_smt_position WHERE task_order=? AND item_code=?";
//        Parameters p1 = new Parameters().add(1, work_order_code).add(2, mesh_code);
//        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql1, p1, new ResultHandler<DataRow>() {
//            @Override
//            public void handleMessage(Message msg) {
//                pn_smt_steel_mesh_storage_editorr.this.ProgressDialog.dismiss();
//
//                Result<DataRow> result = this.Value;
//                if (result.HasError) {
//                    App.Current.showError(pn_smt_steel_mesh_storage_editorr.this.getContext(), result.Error);
//                    return;
//                }
//                DataRow row = result.Value;
//                if (row == null) {
//                    return;
//                }
//                if (row.getValue("count", 0) == 0) {
//                    App.Current.showError(pn_smt_steel_mesh_storage_editorr.this.getContext(), "�ø����뵱ǰ������ƥ�䣬��˶Ը���������");
//                    pn_smt_steel_mesh_storage_editorr.this.txt_steel_mesh_code_cell.setContentText("");
//                    return;
//                }
//
//            }
//        });
//        //return count.toString();
//    }


    ///��������������Ӧ������Ϣ
    @SuppressLint("HandlerLeak")
    public void loadsteel_mesh(final String steel_mesh_code) {
        //this.ProgressDialog.show();
        final String steel_mesh_code1 = steel_mesh_code;

        //steel_mesh_code = "R28030698";
        String sql2 = "SELECT   count(*) a from mm_smt_steel_mesh WHERE code =? ";
        Parameters p2 = new Parameters().add(1, steel_mesh_code);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql2, p2,new ResultHandler<DataRow>(){
            public  void handleMessage(Message msg){
                Result<DataRow> result = this.Value;
                Log.e("LZH253",String.valueOf(result.Value));
                if (result.HasError) {
                    App.Current.showError(pn_smt_steel_mesh_storage_editorr.this.getContext(), result.Error);

                }
                else if(result.Value.getValue("a",0)==0){
                    putDialog(steel_mesh_code);
                    App.Current.showError(pn_smt_steel_mesh_storage_editorr.this.getContext(), "��ĸ�����ûע�ᡣ����ע���ٰ�");


                }
                else {
                    String sql = "SELECT   * from mm_smt_steel_mesh_storage_bind WHERE item_code =? ";
                    Parameters p = new Parameters().add(1, steel_mesh_code1);
                    App.Current.DbPortal.ExecuteRecordAsync(pn_smt_steel_mesh_storage_editorr.this.Connector, sql, p, new ResultHandler<DataRow>() {
                        @Override
                        public void handleMessage(Message msg) {
                            //pn_smt_steel_mesh_storage_editorr.this.ProgressDialog.dismiss();

                            Result<DataRow> result = this.Value;
                            if (result.HasError) {
                                App.Current.showError(pn_smt_steel_mesh_storage_editorr.this.getContext(), result.Error);

                            } else if (result.Value == null) {
                                App.Current.toastInfo(pn_smt_steel_mesh_storage_editorr.this.getContext(), "�ø���û�б��󶨣�����а󶨲���");

                            } else {
                                DataRow row = result.Value;
                                Log.e("LZH289", row.getValue("user_info", "111"));

                                pn_smt_steel_mesh_storage_editorr.this.txt_storage_code_cell.setContentText(row.getValue("storage_code", ""));
                                pn_smt_steel_mesh_storage_editorr.this.txt_user_cell.setContentText(row.getValue("user_info", ""));

                            }
                        }
                    });
                }


            }

                }


        );



    }


//    public void commit() {
//        String a =  this.txt_steel_mesh_code_cell.getContentText().trim(); ;
//
//        loadsteel_mesh(a);
//    }
//}


    @Override

///
//�ύ����
//
    public void commit() {

        final String user_info = App.Current.UserCode;
        final String steel_mesh_code = this.txt_steel_mesh_code_cell.getContentText().trim();
        final String storage_code = this.txt_storage_code_cell.getContentText().trim();
        //��ѡ�н���󶨲���ʱ���������²���
        if (chk_is_scrap.isChecked()) {
            if (steel_mesh_code == null || steel_mesh_code.length() == 0) {
                App.Current.showError(this.getContext(), "��Ҫ����󶨵ĸ�����Ϣ����Ϊ�գ�");
                return;
            }
            if (storage_code == null || storage_code.length() == 0) {
                App.Current.showError(this.getContext(), "��Ҫ����İ󶨵Ĵ�λ����Ϊ�գ�");
                return;
            }
            String sql2 = "delete from mm_smt_steel_mesh_storage_bind where item_code = ? and storage_code = ?";
            Parameters p2 = new Parameters();
            p2.add(1, steel_mesh_code).add(2, storage_code);
            //App.Current.DbPortal.ExecuteDataSet()ExecuteDataTableAsync(this.Connector,sql,p,new ResultHandler<DataTable>()App.Current.DbPortal.ExecuteDataTableAsync
            App.Current.DbPortal.ExecuteNonQueryAsync(this.Connector, sql2, p2, new ResultHandler<Integer>() {

                @Override
                public void handleMessage(Message msg) {
                    pn_smt_steel_mesh_storage_editorr.this.ProgressDialog.dismiss();


                    Result<Integer> result = this.Value;
                    if (result.HasError) {
                        App.Current.showError(pn_smt_steel_mesh_storage_editorr.this.getContext(), result.Error);
                        return;
                    }


                }
            });
            App.Current.toastInfo(pn_smt_steel_mesh_storage_editorr.this.getContext(), "������λ�ѽ����");
            clear();
            return;
        }

        //��ѡ�а󶨲���ʱ�������²���
//        boolean is_scrap2 = this.chk_is_scrap.isChecked();
//        if (is_scrap2 = true) {
//            if (steel_mesh_code == null || steel_mesh_code.length() == 0) {
//                App.Current.showError(this.getContext(), "��Ҫ����󶨵ĸ�����Ϣ����Ϊ�գ�");
//                return;
//            }
//            if (storage_code == null || storage_code.length() == 0) {
//                App.Current.showError(this.getContext(), "��Ҫ����İ󶨵Ĵ�λ����Ϊ�գ�");
//                return;
//            }
//            String sql2 = "INSERT INTO mm_smt_steel_mesh_storage_bind (item_code,storage_code,enable_date,user_info) VALUES (?,?,getdate(),?)";
//            Parameters p2 = new Parameters();
//            p2.add(1, steel_mesh_code).add(2, storage_code).add(3, user_info);
//            App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql2, p2, new ResultHandler<DataRow>() {
//                @Override
//                public void handleMessage(Message msg) {
//                    pn_smt_steel_mesh_storage_editorr.this.ProgressDialog.dismiss();
//
//
//                    Result<DataRow> result = this.Value;
//                    if (result.HasError) {
//                        App.Current.showError(pn_smt_steel_mesh_storage_editorr.this.getContext(), result.Error);
//                        return;
//                    }
//
//
//                }
//            });
//            App.Current.toastInfo(pn_smt_steel_mesh_storage_editorr.this.getContext(), "������λ�󶨳ɹ�");
//            clear();
//            return;
//        }


//        if (steel_mesh_code == null || steel_mesh_code.length() == 0) {
//            App.Current.showError(this.getContext(), "������Ϣ����Ϊ�գ�");
//            return;
//        }
//
//
//
//
//
//        if (storage_code == null || storage_code.length() == 0) {
//            App.Current.showError(this.getContext(), "��λ����Ϊ�գ�");
//            return;
//        }
//        //steel_mesh_code="R123445";
//        //storage_code="G1234";
//        String sql1 ="select * from mm_smt_steel_mesh_storage_bind where item_code = ? ";
//        Parameters p1 = new Parameters();
//        p1.add(1, steel_mesh_code);
//        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql1, p1, new ResultHandler<DataRow>() {
//            @Override
//            public void handleMessage(Message msg) {
//                pn_smt_steel_mesh_storage_editorr.this.ProgressDialog.dismiss();
//
//
//                Result<DataRow> result = this.Value;
//                if (result.HasError) {
//                    App.Current.showError(pn_smt_steel_mesh_storage_editorr.this.getContext(), result.Error);
//                    return;
//                }
//                if (result.Value != null) {
//                    App.Current.showError(pn_smt_steel_mesh_storage_editorr.this.getContext(), "�ø������д�λ");
//                }else{
//                    String sql ="INSERT INTO mm_smt_steel_mesh_storage_bind (item_code,storage_code,enable_date,user_info) VALUES (?,?,getdate(),?)";
//                    System.out.print(steel_mesh_code);
//                    System.out.print(work_order_code);
//                    System.out.print(item_code);
//
//                    System.out.print(sql);
//                    Parameters p = new Parameters();
//                    p.add(1, steel_mesh_code).add(2, storage_code).add(3,user_info);
//                    App.Current.DbPortal.ExecuteDataTableAsync(pn_smt_steel_mesh_storage_editorr.this.Connector, sql, p, new ResultHandler<DataTable>() {
//                        @Override
//                        public void handleMessage(Message msg) {
//                            pn_smt_steel_mesh_storage_editorr.this.ProgressDialog.dismiss();
//
//                            //Result<DataTable> result = this.Value;
//                            //if (result.HasError) {
//                            //	App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(),result.Error);
//                            //	return;
//                            //}
//                            //App.Current.toastInfo(pn_smt_steel_mesh_storage_editorr.this.getContext(), "������λ�󶨳ɹ�");
//                            clear();
//
//                        }
//                    });
//                }



    // DataRow row = pn_smt_steel_mesh_storage_editorr.result.Value;

    //if (result.HasError) {
    //	App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(),result.Error);
    //	return;
    //}

    //clear();


    //});
//}


    String sql = "INSERT INTO mm_smt_steel_mesh_storage_bind (item_code,storage_code,enable_date,user_info) VALUES (?,?,getdate(),?)";
        System.out.print(steel_mesh_code);
        System.out.print(work_order_code);
        System.out.print(item_code);

        System.out.print(sql);
    Parameters p = new Parameters();
        p.add(1,steel_mesh_code).

    add(2,storage_code).

    add(3,user_info);

        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector,sql,p,new ResultHandler<DataTable>()

    {
        @Override
        public void handleMessage (Message msg){
        pn_smt_steel_mesh_storage_editorr.this.ProgressDialog.dismiss();

        //Result<DataTable> result = this.Value;
        //if (result.HasError) {
        //	App.Current.showError(pn_smt_steel_mesh_editorr.this.getContext(),result.Error);
        //	return;
        //}
        App.Current.toastInfo(pn_smt_steel_mesh_storage_editorr.this.getContext(), "������λ�󶨳ɹ�");
        clear();

    }
    });
}

    //��ӡ����
    public void printLabel() {
        //Map<String, String> parameters = new HashMap<String, String>();
        //parameters.put("item_code", this.Item_code);
        //App.Current.Print("mm_item_identifying_label", "��ӡ���ϱ�ʶ��", parameters);
    }

    //�������
    public void clear() {
        this.txt_steel_mesh_code_cell.setContentText("");
        this.txt_storage_code_cell.setContentText("");
        //this.txt_steel_mesh_code_cell.setContentText("");

        //this.txt_usernumber_code_cell.setContentText("");
        //this.txt_plan_count_cell.setContentText("");
        //this.txt_accumulative_use_count_cell.setContentText("");

        //this.txt_A_cell.setContentText("");
        //this.txt_B_cell.setContentText("");
        //this.txt_C_cell.setContentText("");
        //this.txt_D_cell.setContentText("");
        //this.txt_E_cell.setContentText("");

    }


    public void  putDialog(String a){

        final EditText editText = new EditText(pn_smt_steel_mesh_storage_editorr.this.getContext());
        final String steel_mesh_code = a;//arr1
        new AlertDialog.Builder(pn_smt_steel_mesh_storage_editorr.this.getContext()).setTitle("������Ԥ��ʹ�ô���(100000)").setIcon(android.R.drawable.ic_dialog_info).setView(editText).setPositiveButton("ȷ��",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final String i1 = editText.getText().toString();
                        boolean result = i1.matches("[0-9]+");
                        if (i1 == "") {
                            App.Current.showError(pn_smt_steel_mesh_storage_editorr.this.getContext(), "������Ԥ��ʹ�ô�����");
                        }

                        else if (result == false) {   //�ж��Ƿ�������
                            App.Current.showError(pn_smt_steel_mesh_storage_editorr.this.getContext(), "��������ȷ�����֡�");
                        }
                        else{
                            String sql = "INSERT INTO mm_smt_steel_mesh (code,plan_count,accumulative_use_count,is_scrap,create_time) VALUES (?,?,0,0,getdate())";
                            Parameters p = new Parameters();
                            p.add(1,steel_mesh_code).add(2,(Integer.valueOf(i1).intValue()));
                            App.Current.DbPortal.ExecuteNonQueryAsync(pn_smt_steel_mesh_storage_editorr.this.Connector, sql, p, new ResultHandler<Integer>() {

                                @Override
                                public void handleMessage(Message msg) {
                                    pn_smt_steel_mesh_storage_editorr.this.ProgressDialog.dismiss();


                                    Result<Integer> result = this.Value;
                                    if (result.HasError) {
                                        App.Current.showError(pn_smt_steel_mesh_storage_editorr.this.getContext(), result.Error);
                                        return;
                                    }


                                }
                            });
                            App.Current.toastInfo(pn_smt_steel_mesh_storage_editorr.this.getContext(), "����ע��ɹ�");

                            return;
                        }







                    }
                }).setNegativeButton("ȡ��",null).show();

    }




}

