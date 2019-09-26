package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.DESedeKeySpec;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataSet;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.PrintRequest;
import dynsoft.xone.android.data.PrintSetting;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.PrintHelper;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;

/**
 * Created by Administrator on 2018/7/6.
 */

public class pn_qm_device_daily_check_mgr_editor extends pn_editor {
    private TextCell textCell1;                  //设备编码
    private TextCell textCell2;                  //设备名称
    private ButtonTextCell textCell3;                  //线别
    private ButtonTextCell textCell4;                  //设备名称
    private TextCell textCell5;                  //责任人
    private TextCell textCell6;                  //日期
    private ListView listView;
    private ImageButton loadItem;
    private ImageButton btnRefresh;
    private Long id;
    private String create_date;
    private String work_line;
    private String device_name;
    private String responsible;
    private String device_code;
    private Boolean isNew;
    private DataTable dataTableItem;
    private String ipqc_code;
    private ArrayList<String> selectedException;
    private ArrayList<String> stringType;

    public pn_qm_device_daily_check_mgr_editor(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_device_daily_check_mgr_editor, this, true);
        view.setLayoutParams(lp);
        //noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();

        id = this.Parameters.get("id", 0L);
        final Date date = new Date(0);
        Date date1 = this.Parameters.get("create_date", date);
        create_date = new SimpleDateFormat("yyyy-MM-dd").format(date1);
        work_line = this.Parameters.get("work_line", "");
        ipqc_code = this.Parameters.get("ipqc_code", "");
        device_code = this.Parameters.get("device_code", "");
        device_name = this.Parameters.get("device_name", "");
        responsible = this.Parameters.get("responsible", "");
        isNew = this.Parameters.get("isNew", false);

        textCell1 = (TextCell) findViewById(R.id.text_cell_1);
        textCell2 = (TextCell) findViewById(R.id.text_cell_2);
        textCell3 = (ButtonTextCell) findViewById(R.id.text_cell_3);
        textCell4 = (ButtonTextCell) findViewById(R.id.text_cell_4);
        textCell5 = (TextCell) findViewById(R.id.text_cell_5);
        textCell6 = (TextCell) findViewById(R.id.text_cell_6);
        listView = (ListView) findViewById(R.id.listview_1);
        loadItem = (ImageButton) findViewById(R.id.btn_commit);
        btnRefresh = (ImageButton) findViewById(R.id.btn_refresh);
        btnRefresh.setImageBitmap(App.Current.ResourceManager.getImage("@/core_refresh_white"));
        btnRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loadOldListItem();
            }
        });

        if (textCell1 != null) {
            textCell1.setLabelText("设备编码");
        }
        if (textCell2 != null) {
            textCell2.setLabelText("设备名称");
        }
        if (textCell3 != null) {
            textCell3.setLabelText("线别");
            textCell3.setReadOnly();
            textCell3.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoadWorkLine(textCell3);
                }
            });
        }
        if (textCell4 != null) {
            textCell4.setLabelText("项目");
            textCell4.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseType(textCell4);
                }
            });
        }

        if (textCell5 != null) {
            textCell5.setLabelText("责任人");
        }

        if (textCell6 != null) {
            textCell6.setLabelText("创建时间");
        }

        Log.e("len", "ISNEW:" + isNew);
        if (!isNew) {
            textCell1.setReadOnly();
            textCell1.setContentText(device_code);
            textCell2.setReadOnly();
            textCell2.setContentText(device_name);
            textCell3.setReadOnly();
            textCell3.setContentText(work_line);
            textCell5.setReadOnly();
            textCell5.setContentText(responsible);
            textCell6.setReadOnly();
            textCell6.setContentText(create_date);
            loadOldListItem();
        } else {
            if (listView.getCount() > 0) {
                loadItem.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_white"));
            } else {
                loadItem.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_white"));
            }
            loadItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("len", "LISSS:" + listView.getCount());
                    if (listView.getCount() > 0) {   //已经加载过了，提交流程
                        commit();
                    } else {                        //加载Item
                        if (TextUtils.isEmpty(textCell1.getContentText())) {
                            App.Current.toastError(getContext(), "请输入机器编码。");
                        } else if (TextUtils.isEmpty(textCell2.getContentText())) {
                            App.Current.toastError(getContext(), "请输入机器名称。");
                        } else if (TextUtils.isEmpty(textCell3.getContentText())) {
                            App.Current.toastError(getContext(), "请选择线体。");
                        } else if (TextUtils.isEmpty(textCell4.getContentText())) {
                            App.Current.toastError(getContext(), "请选择项目。");
                        } else if (TextUtils.isEmpty(textCell5.getContentText())) {
                            App.Current.toastError(getContext(), "请输入责任人。");
                        } else {
                            //新建head表记录，再加入Item记录
                            String xml = createXML(stringType);
                            Log.e("len", "XML:" + xml);
                            pn_qm_device_daily_check_mgr_editor.this.ProgressDialog.show();
                            String sql = "exec fm_load_device_item_by_head_v01 ?,?,?,?,?,?";
                            Parameters p = new Parameters().add(1, textCell1.getContentText()).add(2, textCell2.getContentText())
                                    .add(3, textCell3.getContentText()).add(4, textCell5.getContentText()).add(5, App.Current.UserCode).add(6, xml);
                            Log.e("len", "PP:" + xml);
                            App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
                                @Override
                                public void handleMessage(Message msg) {
                                    final Result<DataTable> value = Value;
                                    if (value.HasError) {
                                        App.Current.toastError(getContext(), value.Error);
                                        pn_qm_device_daily_check_mgr_editor.this.ProgressDialog.dismiss();
                                        return;
                                    }
                                    if (value.Value != null && value.Value.Rows.size() > 0) {
                                        String device_code = value.Value.Rows.get(0).getValue("device_code", "");
                                        String device_name = value.Value.Rows.get(0).getValue("device_name", "");
                                        String work_line = value.Value.Rows.get(0).getValue("work_line", "");
                                        String respond_by = value.Value.Rows.get(0).getValue("respond_by", "");
                                        String create_date = value.Value.Rows.get(0).getValue("create_date", "");
                                        String ipqc_code = value.Value.Rows.get(0).getValue("ipqc_code", "");
                                        final int line_count = value.Value.Rows.get(0).getValue("line_count", 0);
                                        long id = value.Value.Rows.get(0).getValue("id", 0L);
                                        pn_qm_device_daily_check_mgr_editor.this.work_line = work_line;
                                        pn_qm_device_daily_check_mgr_editor.this.ipqc_code = ipqc_code;
                                        pn_qm_device_daily_check_mgr_editor.this.id = id;
                                        textCell1.setContentText(device_code);
                                        textCell2.setContentText(device_name);
                                        textCell3.setContentText(work_line);
                                        textCell5.setContentText(respond_by);
                                        textCell6.setContentText(create_date);
                                        DeviceListviewItemAdapter deviceCheckAdapter = new DeviceListviewItemAdapter(value.Value);
                                        listView.setAdapter(deviceCheckAdapter);
                                        loadItem.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_white"));
                                    }
                                    pn_qm_device_daily_check_mgr_editor.this.ProgressDialog.dismiss();
                                }
                            });
                        }
                    }
                }
            });
        }

        if (listView != null) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    long line_id = i + 1;
                    Link link = new Link("pane://x:code=qm_device_daily_check_editor");
                    link.Parameters.add("line_id", line_id);
                    link.Parameters.add("work_line", pn_qm_device_daily_check_mgr_editor.this.work_line);
                    link.Parameters.add("head_id", id);
                    link.Parameters.add("line_count", listView.getCount());
                    link.Open(pn_qm_device_daily_check_mgr_editor.this, pn_qm_device_daily_check_mgr_editor.this.getContext(), null);
                }
            });
        }

//        btn_refrash = (ImageButton) findViewById(R.id.btn_refrash);
//        btn_refrash.setImageBitmap(App.Current.ResourceManager.getImage("@/core_refresh_white"));
//        btn_refrash.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                initListView();
//            }
//        });

    }

    private void chooseType(final ButtonTextCell textCell4) {
        String sql = "SELECT DISTINCT(check_type) type FROM fm_device_check_type";
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(pn_qm_device_daily_check_mgr_editor.this.getContext(), value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    ArrayList<String> names = new ArrayList<String>();
                    selectedException = new ArrayList<String>();
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        names.add(value.Value.Rows.get(i).getValue("type", ""));
                    }
                    final StringBuffer nameMessage = new StringBuffer();
                    stringType = new ArrayList<String>();
                    final boolean[] selected = new boolean[names.size()];
                    multiChoiceDialog(nameMessage, selected, names, textCell4);

                } else {

                }
            }
        });
    }

    private void LoadWorkLine(final ButtonTextCell textCell3) {
        String sql = "select name from fm_work_line";
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(pn_qm_device_daily_check_mgr_editor.this.getContext(), value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    ArrayList<String> names = new ArrayList<String>();
                    for (DataRow row : value.Value.Rows) {
                        String name = row.getValue("name", "");
                        names.add(name);
                    }

                    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which >= 0) {
                                DataRow row = value.Value.Rows.get(which);
                                textCell3.setContentText(row.getValue(
                                        "name", ""));
                            }
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(pn_qm_device_daily_check_mgr_editor.this.getContext()).setTitle("请选择")
                            .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(textCell3.getContentText()), listener)
                            .setNegativeButton("取消", null).show();
                }
            }
        });
    }

    private void loadOldListItem() {
        String sql = "select * from fm_device_check_item where head_id = ?";
        Parameters p = new Parameters().add(1, id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                    return;
                }
                if (value.Value != null) {
                    dataTableItem = value.Value;
                    DeviceListviewItemAdapter deviceListviewItemAdapter = new DeviceListviewItemAdapter(dataTableItem);
                    listView.setAdapter(deviceListviewItemAdapter);
                }
            }
        });
    }

    private void multiChoiceDialog(final StringBuffer nameMessage, final boolean[] selected, final ArrayList<String> names, final ButtonTextCell buttonTextCell) {
        new AlertDialog.Builder(pn_qm_device_daily_check_mgr_editor.this.getContext()).setTitle("请选择")
                .setNegativeButton("取消", null).setPositiveButton("确定", null)
                .setMultiChoiceItems(names.toArray(new String[0]), selected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        selected[i] = b;
                        if (selected[i] == true) {
                            nameMessage.append(names.get(i).trim());
                            nameMessage.append(",");
                            stringType.add(names.get(i).trim());
                            if (!selectedException.contains(names.get(i))) {
                                selectedException.add(names.get(i));
                            }
                        }
                        buttonTextCell.setContentText(nameMessage.toString());
                    }
                }).show();
    }

    private String createXML(ArrayList<String> values) {
        Log.e("len", "Array:" + values);
        ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
        for (int i = 0; i < values.size(); i++) {
            Map<String, String> entry = new HashMap<String, String>();
            entry.put("device_type", values.get(i));
            entries.add(entry);
        }

        //生成XML数据，并传给存储过程
        String xml = XmlHelper.createXml("device_head", null, null, "device_item", entries);
        Log.e("len", "XML:" + xml);
        return xml;
    }

    @Override
    public void onRefrsh() {
        super.onRefrsh();
        Toast.makeText(pn_qm_device_daily_check_mgr_editor.this.getContext(), "刷新", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void commit() {
        super.commit();
        String sql = "exec fm_get_device_all_item_status_and ?";
        dynsoft.xone.android.data.Parameters p = new Parameters().add(1, id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                    return;
                }
                if (value.Value != null) {    //发起流程
                    if (value.Value.Rows.size() == 0) {
                        startFlow();
                    } else {
                        App.Current.toastError(getContext(), "当前项目还没有检查完，请检查完再提交。");
                        App.Current.playSound(R.raw.error);
                    }
                } else {
                    App.Current.toastError(getContext(), "出错了！");
                    App.Current.playSound(R.raw.error);
                }
            }
        });
    }

    private void startFlow() {
        App.Current.question(this.getContext(), "确定要发起流程吗？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

//                Map<String, String> parameters = new HashMap<String, String>();
//                parameters.put("id", String.valueOf(id));
//                parameters.put("work_line", work_line);
//                parameters.put("esd_date", esd_date);
//                parameters.put("ipqc", "M3933");

                //App.Current.Print("start_eip_flow", "发起流程", parameters);

                //保存打印设置
                PrintSetting printSetting = new PrintSetting();
                printSetting.Server = "株洲条码打印服务器102";
                printSetting.Url = "http://192.168.0.102:6683";
                printSetting.Printer = "#15";
                PrintHelper.savePrintServer(pn_qm_device_daily_check_mgr_editor.this.getContext(), printSetting);

                PrintRequest request = new PrintRequest();
                request.Server = "http://192.168.0.102:6683";
                request.Printer = "#15";
                request.Code = "start_eip_flow_comm";

                //准备打印参数
                request.Parameters = new HashMap<String, String>();

                //{"fd_work_line":"'+work_line+'","fd_esd_date":"'+esd_date+'","fd_line_man":"'+line_man+'","fd_department":"'+department+'","fd_ipqc":"'+ipqc+'"}'
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("{\"fd_shebeibianma\":\"");
                stringBuilder.append(textCell1.getContentText());
                stringBuilder.append("\",\"fd_shebeimingcheng\":\"");
                stringBuilder.append(textCell2.getContentText());
                stringBuilder.append("\",\"fd_xianbie\":\"");
                stringBuilder.append(textCell3.getContentText());
                stringBuilder.append("\",\"fd_zenrenren\":\"");
                stringBuilder.append(textCell4.getContentText());
                stringBuilder.append("\",\"fd_create_time\":\"");
                stringBuilder.append(textCell5.getContentText());
                stringBuilder.append("\",\"fd_ipqc\":\"");
                stringBuilder.append(ipqc_code);
                stringBuilder.append("\"}");

                request.Parameters.put("formValues", stringBuilder.toString());
                request.Parameters.put("fdTemplateId", "164dab44a4074feecc441c8413e84408");
                request.Parameters.put("docSubject", "设备点检流程");
                request.Parameters.put("TaskUser", App.Current.UserCode);
                Log.e("len", "STR:" + stringBuilder.toString());


//                SerializableMap myMap = new SerializableMap();
//                myMap.setMap(parameters);//将map数据添加到封装的myMap<span></span>中
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("map", myMap);

//                    if (bundle != null && bundle.size() > 0) {
//                        Set<String> keys = bundle.keySet();
//                        for (String key : keys) {
//                            request.Parameters.put(key, bundle.getString(key));
//                        }
//                    }

                request.Parameters.put("id", String.valueOf(id));

                Result<String> result = PrintHelper.print(request);
                if (result.HasError) {
                    App.Current.showError(pn_qm_device_daily_check_mgr_editor.this.getContext(), result.Error);
                } else {
                    String sql = "exec update_device_head_status ?,?";
                    Parameters p = new Parameters().add(1, id).add(2, result.Value);
                    App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                        @Override
                        public void handleMessage(Message msg) {
                            Result<Integer> value = Value;
                            if (value.HasError) {
                                App.Current.showError(getContext(), value.Error);
                                return;
                            }
                            if (value.Value != null) {
                                if (value.Value > 0) {
                                    App.Current.toastInfo(getContext(), "提交成功！");
                                    App.Current.playSound(R.raw.pass);
                                    close();
                                } else {
                                    App.Current.toastInfo(getContext(), "提交失败！");
                                    App.Current.playSound(R.raw.error);
                                }
                            } else {
                                App.Current.toastInfo(getContext(), "提交失败！");
                                App.Current.playSound(R.raw.error);
                            }
                        }
                    });
                }
//                    Log.e("len", result.Value);
            }
        });
    }

    private class DeviceListviewItemAdapter extends BaseAdapter {
        private DataTable dataTable;

        public DeviceListviewItemAdapter(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        @Override
        public int getCount() {
            return dataTable == null ? 0 : dataTable.Rows.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            DeviceItemViewHolder deviceItemViewHolder;
            if (view == null) {
                deviceItemViewHolder = new DeviceItemViewHolder();
                view = View.inflate(getContext(), R.layout.device_check_mgr_editor_item, null);
                deviceItemViewHolder.textView1 = (TextView) view.findViewById(R.id.txt_line1);
                deviceItemViewHolder.textView2 = (TextView) view.findViewById(R.id.txt_line2);
                deviceItemViewHolder.textView3 = (TextView) view.findViewById(R.id.txt_line3);
                deviceItemViewHolder.textView4 = (TextView) view.findViewById(R.id.txt_line4);
                deviceItemViewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout);
                view.setTag(deviceItemViewHolder);
            } else {
                deviceItemViewHolder = (DeviceItemViewHolder) view.getTag();
            }
            String check_type = dataTable.Rows.get(i).getValue("check_type", "");
            String check_item = dataTable.Rows.get(i).getValue("check_item", "");
            String check_way = dataTable.Rows.get(i).getValue("check_way", "");
            String check_result = dataTable.Rows.get(i).getValue("check_result", "");
            String standard = dataTable.Rows.get(i).getValue("standard", "");
            deviceItemViewHolder.textView1.setText(check_item);
            deviceItemViewHolder.textView2.setText(check_way);
            deviceItemViewHolder.textView3.setText(standard);
            deviceItemViewHolder.textView4.setText(check_type);
            if ("OK".equals(check_result)) {
                deviceItemViewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.green));
            } else if ("NG".equals(check_result)) {
                deviceItemViewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.red));
            } else {
                deviceItemViewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.white));
            }
            return view;
        }
    }

    class DeviceItemViewHolder {
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private TextView textView4;
        private LinearLayout linearLayout;
    }
}
