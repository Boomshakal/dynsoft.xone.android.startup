package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import dynsoft.xone.android.activity.SopActivity;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.link.Link;

/**
 * Created by Administrator on 2017/12/18.
 */

public class pn_sop_mgr extends pn_mgr {
    private DataTable value;
    private ButtonTextCell textcell_1;
    private ButtonTextCell textcell_2;
    private TextCell textcell_3;
    private TextCell textcell_4;
    private ButtonTextCell textcell_5;
    private TextCell textcell_6;
    private TextCell textcell_7;

    private ImageButton btn_commit;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    private View view;
    private String code;
    private int seq_id = 0;
    private int item_id;
    private long task_id;
    private boolean isNew;
    private String org_code;
    private int org_id;
    private String segment;

    public pn_sop_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_sop_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        sharedPreferences = getContext().getSharedPreferences("sop", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();

        textcell_1 = (ButtonTextCell) view.findViewById(R.id.textcell_1);
        textcell_2 = (ButtonTextCell) view.findViewById(R.id.textcell_2);
        textcell_3 = (TextCell) view.findViewById(R.id.textcell_3);
        textcell_4 = (TextCell) view.findViewById(R.id.textcell_4);
        textcell_5 = (ButtonTextCell) view.findViewById(R.id.textcell_5);
        textcell_6 = (TextCell) view.findViewById(R.id.textcell_6);
        textcell_7 = (TextCell) view.findViewById(R.id.textcell_7);
        segment = sharedPreferences.getString("segment", "");
        code = this.Parameters.get("code", "");
        //        item_id = this.Parameters.get("item_id", 0);
        isNew = this.Parameters.get("isNew", false);
        if (isNew) {
            task_id = this.Parameters.get("task_id", 0L);
            edit.putInt("order_task_id", (int) task_id);
            edit.commit();
            segment = "";
            loadWorkLineName(textcell_2, code);
        }

        if (textcell_1 != null) {
            textcell_1.setLabelText("生产任务");
            textcell_1.setReadOnly();
            textcell_1.setContentText(sharedPreferences.getString("task_order", ""));
            textcell_1.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadComfirmName(textcell_1);
                }
            });
        }

        if (!TextUtils.isEmpty(code)) {
            textcell_1.setContentText(code);
        }

        if (!TextUtils.isEmpty(textcell_1.getContentText())) {
            String sql1 = "select * from mm_wo_task_order_head where code = ?";
            Parameters p1 = new Parameters().add(1, textcell_1.getContentText().trim());
            App.Current.DbPortal.ExecuteRecordAsync("core_and", sql1, p1, new ResultHandler<DataRow>() {
                @Override
                public void handleMessage(Message msg) {
                    Result<DataRow> value = Value;
                    if (value.HasError) {
                        App.Current.showError(getContext(), value.Error);
                        return;
                    }
                    if (value.Value != null) {
                        String plan_quantity = value.Value.getValue("plan_quantity", new BigDecimal(0)).toString();
                        edit.putString("plan_quantity", plan_quantity);
                        item_id = value.Value.getValue("item_id", 0);
                        org_id = value.Value.getValue("organization_id", 0);
                        if (item_id != 0) {
                            edit.putInt("item_id", item_id);
                        }
                        if (org_id != 0) {
                            edit.putInt("org_id", org_id);
                        }
                        edit.commit();
                    }
                }
            });
        }

        if (textcell_2 != null) {
            textcell_2.setLabelText("线别");
            textcell_2.setReadOnly();
            textcell_2.setContentText(segment);
            textcell_2.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(textcell_1.getContentText())) {
                        App.Current.showError(getContext(), "请先选择生产任务");
                    } else {
                        loadWorkLineName(textcell_2, textcell_1.getContentText());
                    }
                }
            });
        }

        if (textcell_3 != null) {
            textcell_3.setLabelText("车间");
            textcell_3.setReadOnly();
            textcell_3.setContentText(sharedPreferences.getString("work_line", ""));
        }

        if (textcell_4 != null) {
            textcell_4.setLabelText("工位");
            textcell_4.setContentText(sharedPreferences.getString("station", ""));
        }

        if (textcell_5 != null) {
            textcell_5.setLabelText("工序");
            textcell_5.setReadOnly();
            textcell_5.setContentText(sharedPreferences.getString("production", ""));

            if (!TextUtils.isEmpty(textcell_5.getContentText())) {
                String contentText = textcell_5.getContentText();
                String[] split = contentText.split(",");
                String sql1 = "select * from fm_eng_sequence where code = ?";
                Parameters p1 = new Parameters().add(1, split[0]);
                App.Current.DbPortal.ExecuteRecordAsync("core_and", sql1, p1, new ResultHandler<DataRow>() {
                    @Override
                    public void handleMessage(Message msg) {
                        Result<DataRow> value = Value;
                        if (value.HasError) {
                            App.Current.showError(getContext(), value.Error);
                            return;
                        }
                        if (value.Value != null) {
                            seq_id = value.Value.getValue("sequence_id", 0);
                            if (seq_id != 0) {
                                edit.putInt("seq_id", seq_id);
                                edit.commit();
                            }
                        }
                    }
                });
            }

            textcell_5.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(textcell_1.getContentText())) {
                        App.Current.showError(getContext(), "请先选择生产任务");
                    } else {
                        loadProductionName(textcell_5);
                    }
                }
            });
        }

        if (textcell_6 != null) {
            textcell_6.setLabelText("设备编号");
            textcell_6.setReadOnly();
            setDeviceNumByMac(textcell_6);
        }

        if (textcell_7 != null) {
            textcell_7.setLabelText("领班");
            textcell_7.setContentText(sharedPreferences.getString("foreman", ""));
        }

        btn_commit = (ImageButton) findViewById(R.id.btn_commit);
        btn_commit.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_white"));
        btn_commit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                getJson();
                //写数据库，然后做判断，如果对的就跳转到SopActivity，否则报错
                if (TextUtils.isEmpty(textcell_1.getContentText())) {
                    App.Current.showInfo(getContext(), "请选择生产任务");
                } else if (TextUtils.isEmpty(textcell_2.getContentText())) {
                    App.Current.showInfo(getContext(), "请选择线别");
                } else if (TextUtils.isEmpty(textcell_4.getContentText())) {
                    App.Current.showInfo(getContext(), "请输入工位");
                } else if (TextUtils.isEmpty(textcell_5.getContentText())) {
                    App.Current.showInfo(getContext(), "请选择工序");
                } else if (TextUtils.isEmpty(textcell_6.getContentText())) {
                    App.Current.showInfo(getContext(), "该设备的MAC地址没有维护");
                } else if (TextUtils.isEmpty(textcell_7.getContentText())) {
                    App.Current.showInfo(getContext(), "请输入领班工号");
                } else if (!checkForeMan()) {
                    App.Current.showError(getContext(), "输入的领班工号有误");
                } else {
//                    edit.putString("task_order", textcell_1.getContentText());
//                    edit.putString("segment", textcell_2.getContentText());
//                    edit.putString("work_line", textcell_3.getContentText());
//                    edit.putString("station", textcell_4.getContentText());
//                    edit.putString("production", textcell_5.getContentText());
//                    edit.putString("device", textcell_6.getContentText());
//                    edit.putString("foreman", textcell_7.getContentText());
//                    edit.putInt("process_id", process_id);
////                        getPic(1L,2L);
//                    edit.commit();

                    edit.putString("task_order", textcell_1.getContentText());
                    edit.putString("segment", textcell_2.getContentText());
                    edit.putString("work_line", textcell_3.getContentText());
                    edit.putString("station", textcell_4.getContentText());
                    edit.putString("production", textcell_5.getContentText());
                    edit.putString("device", textcell_6.getContentText());
                    edit.putString("foreman", textcell_7.getContentText());
                    if (isNew) {
                        edit.putString("org_code", org_code);
                    } else {
                        edit.putString("org_code", sharedPreferences.getString("org_code", ""));
                    }
//                    edit.putInt("process_id", process_id);
//                    edit.putInt("seq_id", seq_id);
                    edit.commit();

                    String sql = "exec fm_sop_get_url ?,?,?";
                    Parameters p = new Parameters().add(1, item_id).add(2, seq_id).add(3, textcell_4.getContentText());
                    Log.e("len", item_id + "**" + seq_id + "**" + textcell_4.getContentText());
                    App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
                        @Override
                        public void handleMessage(Message msg) {
                            Result<DataTable> value = Value;
                            if (value.HasError) {
                                App.Current.showError(getContext(), value.Error);
                                return;
                            }
                            if (value.Value != null && value.Value.Rows.size() > 0) {
                                ArrayList<String> image_urls = new ArrayList<String>();
                                long head_id = value.Value.Rows.get(0).getValue("head_id", 0L);
                                edit.putLong("head_id", head_id);
                                edit.commit();
                                for (DataRow rw : value.Value.Rows) {
                                    image_urls.add(rw.getValue("image_url", ""));
                                }
                                Intent intent = new Intent(getContext(), SopActivity.class);
                                intent.putStringArrayListExtra("image_urls", image_urls);
                                App.Current.Workbench.startActivity(intent);
                            } else {
                                App.Current.showError(getContext(), "请上传SOP");
                                ArrayList<String> image_urls = new ArrayList<String>();
                                Intent intent = new Intent(getContext(), SopActivity.class);
                                intent.putStringArrayListExtra("image_urls", image_urls);
                                App.Current.Workbench.startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }

    private void setDeviceNumByMac(final TextCell textcell_6) {
        String sql = "exec get_devnum_by_macaddress_and ?";
        Parameters p = new Parameters().add(1, getMacAddress());
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    if (value.Value.Rows.size() > 1) {
                        textcell_6.setContentText(getMacAddress());
                    } else {
                        String code = value.Value.Rows.get(0).getValue("code", "");
                        textcell_6.setContentText(code);
                    }
                } else {
                    textcell_6.setContentText(getMacAddress());
                }
            }
        });
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
                Enumeration en_ip = ni.getInetAddresses();//得到一个ip地址的列举
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

    private boolean checkForeMan() {
        String sql = "select t0.code, t0.name from fm_worker t0 inner join core_user t1 on t1.id=t0.create_user where t0.code = ?";
        String parameter;
        String contentText = textcell_7.getContentText();
        if (contentText.contains("\n")) {
            parameter = contentText.replace("\n", "");
        } else {
            parameter = contentText;
        }
        Parameters p = new Parameters().add(1, parameter);
        Result<DataRow> dataRowResult = App.Current.DbPortal.ExecuteRecord(Connector, sql, p);
        if (dataRowResult.HasError) {
            App.Current.showError(getContext(), dataRowResult.Error);
            return false;
        }
        if (dataRowResult.Value != null) {
            return true;
        } else {
            return false;
        }
    }


    private void loadComfirmName(ButtonTextCell textcell_1) {
        Link link = new Link("pane://x:code=qm_sop_parameter_mgr");
        link.Parameters.add("textcell", textcell_1);
        link.Open(null, getContext(), null);
        this.close();
    }

    private void loadWorkLineName(final ButtonTextCell text, String taskOrder) {
        String sql = "exec p_qm_sop_work_line_items ?,?";
        Parameters p = new Parameters().add(1, App.Current.UserID).add(2, taskOrder);
        final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (result.HasError) {
            App.Current.showError(this.getContext(), result.Error);
            return;
        }
        if (result.Value != null) {

            ArrayList<String> names = new ArrayList<String>();
            for (DataRow row : result.Value.Rows) {
                StringBuffer name = new StringBuffer();
                name.append(row.getValue("name", ""));
                names.add(name.toString());
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which >= 0) {
                        DataRow row = result.Value.Rows.get(which);
                        StringBuffer name = new StringBuffer();
                        name.append(row.getValue("name", ""));
                        text.setContentText(name.toString());
                        textcell_3.setContentText(row.getValue("work_name", ""));
                        org_code = row.getValue("org_code", "");
                        String foreman_code = row.getValue("foreman_code", "");
                        textcell_7.setContentText(foreman_code);
                        edit.putInt("work_line_id", row.getValue("id", 0));
                        edit.putString("org_name", row.getValue("org_name", ""));
                        edit.commit();
                    }
                    dialog.dismiss();
                }
            };
            new AlertDialog.Builder(pn_sop_mgr.this.getContext()).setTitle("请选择")
                    .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                            (text.getContentText().toString()), listener)
                    .setNegativeButton("取消", null).show();
        }
    }

    private void loadProductionName(final ButtonTextCell text) {
//        String sql = "exec p_qm_sop_production_items ?,?";
        String sql = "exec p_qm_sop_change_production ?,?";
        Parameters p = new Parameters().add(1, App.Current.UserID).add(2, textcell_1.getContentText());
        final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (result.HasError) {
            App.Current.showError(this.getContext(), result.Error);
            return;
        }
        if (result.Value != null) {

            ArrayList<String> names = new ArrayList<String>();
            for (DataRow row : result.Value.Rows) {
                StringBuffer name = new StringBuffer();
                name.append(row.getValue("name", ""));
                names.add(name.toString());
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which >= 0) {
                        DataRow row = result.Value.Rows.get(which);
                        StringBuffer name = new StringBuffer();
                        name.append(row.getValue("name", ""));
                        text.setContentText(name.toString());
                        seq_id = row.getValue("id", 0);
                        edit.putInt("seq_id", seq_id);
                    }
                    dialog.dismiss();
                }
            };
            new AlertDialog.Builder(pn_sop_mgr.this.getContext()).setTitle("请选择")
                    .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                            (text.getContentText().toString()), listener)
                    .setNegativeButton("取消", null).show();
        }
    }
}
