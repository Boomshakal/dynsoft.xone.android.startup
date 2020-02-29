package dynsoft.xone.android.wms;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.PromptCallback;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

public class pn_qm_ipqc_first_sure_mgr extends pn_mgr {
    public pn_qm_ipqc_first_sure_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        super.setContentView();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_ipqc_first_sure_mgr, this, true);
        view.setLayoutParams(layoutParams);
    }

    @Override
    public void onRefrsh() {
        super.onRefrsh();
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        this.Matrix.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (Adapter.DataTable != null && Adapter.DataTable.Rows.size() > id) {
                    final DataRow row = (DataRow) Adapter.getItem((int) id);
                    App.Current.prompt(pn_qm_ipqc_first_sure_mgr.this.getContext(), "��ʾ", "�����벵��ԭ��", new PromptCallback() {
                        @Override
                        public void onReturn(String result) {
                            String sql = "exec fm_ipqc_first_item_back_and ?,?,?";
                            Parameters p = new Parameters().add(1, result).add(2, row.getValue("code", "")).add(3, App.Current.UserCode);
                            App.Current.DbPortal.ExecuteNonQueryAsync(pn_qm_ipqc_first_sure_mgr.this.Connector, sql, p, new ResultHandler<Integer>() {
                                @Override
                                public void handleMessage(Message msg) {
                                    Result<Integer> value = Value;
                                    if(value.HasError) {
                                        App.Current.toastError(getContext(), value.Error);
                                    } else {
                                        if(value.Value > 0) {
                                            App.Current.toastInfo(getContext(), "���سɹ���");
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
                return false;
            }
        });
    }

    //    @Override
//    public void create() {
//        super.create();
//        Link link = new Link("pane://x:code=pn_qm_ipqc_first_sure_editor");
//        link.Open(this, this.getContext(), null);
//    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        DataRow row = (DataRow) Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ipqc_first_chec_mgr_item, null);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView) convertView.findViewById(R.id.num);
        TextView txt_line1 = (TextView) convertView.findViewById(R.id.txt_line1);
        TextView txt_line2 = (TextView) convertView.findViewById(R.id.txt_line2);
        TextView txt_line3 = (TextView) convertView.findViewById(R.id.txt_line3);
        TextView txt_line4 = (TextView) convertView.findViewById(R.id.txt_line4);

        num.setText(String.valueOf(position + 1));
        String code = row.getValue("code", "");    //�׼�ȷ�ϵ�����
        String task_code = row.getValue("task_code", "");    //��������
        String segment = row.getValue("segment", "");    //�α�
        String item_code = row.getValue("item_code", "");    //���ϱ���
        String item_name = row.getValue("item_name", "");    //��������
        String creator = row.getValue("creator", "");      //������
        String status = row.getValue("status", "");         //״̬
        String work_line = row.getValue("work_line", "");    //״̬
        String seq_code = row.getValue("seq_code", "");       //����
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date esd_date = row.getValue("create_time", new Date(0));
        String create_time = simpleDateFormat.format(esd_date);

        txt_line1.setText("ȷ�ϱ��룺" + code + "����������" + task_code);
        txt_line2.setText("�α�" + segment + "�� ���ͣ�" + item_code + "-" + item_name);
        txt_line3.setText("���壺" + work_line + "������" + seq_code);
        txt_line4.setText("�����ߣ�" + creator + "��״̬��" + status + "������ʱ�䣺" + create_time);
        return convertView;
    }

    @Override
    public void openItem(DataRow row) {
        String seq_code = row.getValue("seq_code", "");
        if (seq_code.toUpperCase().contains("M119")) {                        //����ǹེ���� ����ͬ�Ľ���
            Link link = new Link("pane://x:code=pn_qm_ipqc_first_sure_sequence_editor");
            link.Parameters.add("id", row.getValue("id", 0));
            link.Parameters.add("task_code", row.getValue("task_code", ""));
            link.Parameters.add("code", row.getValue("code", ""));
            link.Parameters.add("sn", row.getValue("sn", ""));
            link.Open(this, this.getContext(), null);
        } else {
//            Link link = new Link("pane://x:code=pn_qm_ipqc_first_sure_sequence_editor");
            Link link = new Link("pane://x:code=pn_qm_ipqc_first_sure_editor");
            link.Parameters.add("id", row.getValue("id", 0));
            link.Parameters.add("counts", row.getValue("counts", 0));
            link.Parameters.add("code", row.getValue("code", ""));
            link.Parameters.add("task_code", row.getValue("task_code", ""));
            link.Parameters.add("sn", row.getValue("sn", ""));
            link.Parameters.add("item_code", row.getValue("item_code", ""));
            link.Parameters.add("item_name", row.getValue("item_name", ""));
            link.Open(this, this.getContext(), null);
        }
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_qm_ipqc_first_sure_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3, end).add(4, search);
        return expr;
    }
}
