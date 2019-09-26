package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.link.Link;

/**
 * Created by Administrator on 2018/7/6.
 */

public class pn_qm_electric_equipment_check_mgr extends pn_mgr {
    public pn_qm_electric_equipment_check_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_device_daily_check_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        DataRow row = (DataRow) Adapter.getItem(position);
        if (convertView == null) {
            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.device_daily_check_mgr_item, null);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }

        TextView num = (TextView) convertView.findViewById(R.id.num);
        TextView txt_line1 = (TextView) convertView.findViewById(R.id.txt_line1);
        TextView txt_line2 = (TextView) convertView.findViewById(R.id.txt_line2);
        TextView txt_line3 = (TextView) convertView.findViewById(R.id.txt_line3);
        TextView txt_line4 = (TextView) convertView.findViewById(R.id.txt_line4);

        num.setText(String.valueOf(position + 1));
        String lot_code = row.getValue("code", "");
        String checker = row.getValue("checker", "");
        String status = row.getValue("status", "");
        String work_line = row.getValue("work_line", "");
        String org_name = row.getValue("org_name", "");
        Date date = new Date(0);
        Date check_date = row.getValue("check_date", date);
        String create_time = row.getValue("create_time", "");

        txt_line1.setText("检验单号：" + lot_code);
        txt_line2.setText("线体：" + work_line);
        txt_line3.setText("状态：" + status);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format1 = simpleDateFormat.format(check_date);
        String format2 = simpleDateFormat.format(date);
        if (format1 != format2) {
            txt_line4.setText(checker + "," + create_time);
        } else {
            txt_line4.setText(checker + "," + format1);
        }

        return convertView;
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        this.Matrix.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final DataRow row = (DataRow) Adapter.getItem((int) l);
                new AlertDialog.Builder(pn_qm_electric_equipment_check_mgr.this.getContext())
                        .setMessage("确定要提交吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                commitIssueOrder(row);
                            }
                        }).setNegativeButton("取消", null)
                        .show();
                return true;
            }
        });

    }

    private void commitIssueOrder(DataRow row) {
        int id = row.getValue("id", 0);
        String sql = "exec get_ipqc_point_commit_and ?,?";
        Parameters p = new Parameters().add(1, id).add(2, App.Current.UserID);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if(value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    return;
                }
                if(value.Value != null) {
                    String result = value.Value.getValue("result", "");
                    if("OK".equals(result)) {
                        App.Current.toastInfo(getContext(), "提交成功！");
                        Adapter.notifyDataSetChanged();
                    } else {
                        App.Current.toastError(getContext(), result);
                    }
                }
            }
        });
    }

    @Override
    public void create() {
        super.create();
        Link link = new Link("pane://x:code=qm_electric_equipment_check_mgr_editor");
        link.Parameters.add("isNew", true);
        link.Open(this, this.getContext(), null);
    }

    @Override
    public void openItem(DataRow row) {
        Link link = new Link("pane://x:code=qm_electric_equipment_check_mgr_editor");
        link.Parameters.add("id", row.getValue("id", 0));   //head_id
        link.Parameters.add("code", row.getValue("code"));                //点检号
        link.Parameters.add("checker", row.getValue("checker"));          //点检员
        link.Parameters.add("create_time", row.getValue("create_time")); //点检时间
        link.Parameters.add("work_line", row.getValue("work_line"));      //线别
        link.Parameters.add("task_order_code", row.getValue("task_order_code")); //生产工单
        link.Parameters.add("comment", row.getValue("comment"));           //备注
        link.Open(this, this.getContext(), null);
    }

    @Override
    public SqlExpression getSqlExpression(int top, int start, int end, String search) {
        SqlExpression expr = new SqlExpression();
        expr.SQL = "exec p_pn_qm_electric_equipment_check_get_items ?,?,?,?";
        expr.Parameters = new Parameters().add(1, App.Current.UserID).add(2, start).add(3, end).add(4, search);
        return expr;
    }
}
