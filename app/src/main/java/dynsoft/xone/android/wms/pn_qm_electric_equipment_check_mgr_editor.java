package dynsoft.xone.android.wms;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.TreeMap;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.core.Workbench;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.PrintRequest;
import dynsoft.xone.android.data.PrintSetting;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.PrintHelper;
import dynsoft.xone.android.link.Link;

/**
 * Created by Administrator on 2018/7/6.
 */

public class pn_qm_electric_equipment_check_mgr_editor extends pn_editor {

    private TextCell text_cell_task_order;
    private TextCell text_cell_work_line;
    private TextCell text_cell_check_date;
    private ButtonTextCell text_cell_check_user;
    private TextCell text_cell_comment;
    private ImageButton btn_refrash;
    private ListView listView;
    private int id;
    private String create_time;
    private String work_line;
    private String task_order_code;
    private String checker;
    private String comment;
    private String status;
    private boolean isNew;

    public pn_qm_electric_equipment_check_mgr_editor(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_electric_equipment_check_mgr_editor, this, true);
        view.setLayoutParams(lp);
        //noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        id = this.Parameters.get("id", -1);
        create_time = this.Parameters.get("create_time", "");
        work_line = this.Parameters.get("work_line", "");
        task_order_code = this.Parameters.get("task_order_code", "");
        checker = this.Parameters.get("checker", "");
        comment = this.Parameters.get("comment", "");
        isNew = this.Parameters.get("isNew", false);

        text_cell_task_order = (TextCell) findViewById(R.id.text_cell_task_order);
        text_cell_work_line = (TextCell) findViewById(R.id.text_cell_work_line);
        text_cell_check_date = (TextCell) findViewById(R.id.text_cell_check_date);
        text_cell_check_user = (ButtonTextCell) findViewById(R.id.text_cell_check_user);
        text_cell_comment = (TextCell) findViewById(R.id.text_cell_comment);
        listView = (ListView) findViewById(R.id.listview_electric);

        if (text_cell_task_order != null) {
            text_cell_task_order.setLabelText("生产任务");
            text_cell_task_order.setContentText(task_order_code);
        }
        if (text_cell_work_line != null) {
            text_cell_work_line.setLabelText("线别");
            text_cell_work_line.setContentText(work_line);
        }
        if (text_cell_check_user != null) {
            text_cell_check_user.setLabelText("点检员");
            text_cell_check_user.setContentText(checker);
            text_cell_check_user.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.Current.Workbench.scanByCamera();
                }
            });
        }
        if (text_cell_check_date != null) {
            text_cell_check_date.setLabelText("日期");
            text_cell_check_date.setContentText(create_time);
        }
        if (text_cell_comment != null) {
            text_cell_comment.setLabelText("备注");
            text_cell_comment.setContentText(comment);
        }

        if(!isNew) {
            text_cell_task_order.setReadOnly();
            text_cell_work_line.setReadOnly();
            text_cell_check_user.setReadOnly();
            text_cell_check_date.setReadOnly();
            text_cell_comment.setReadOnly();
        }

        btn_refrash = (ImageButton) findViewById(R.id.btn_refresh);
        btn_refrash.setImageBitmap(App.Current.ResourceManager.getImage("@/core_refresh_white"));
        btn_refrash.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(text_cell_task_order.getContentText())) {
                    App.Current.toastError(getContext(),"请输入生产任务");
                    return;
                }
                if (TextUtils.isEmpty(text_cell_work_line.getContentText())) {
                    App.Current.toastError(getContext(),"请输入线别");
                    return;
                }
                if (TextUtils.isEmpty(text_cell_check_user.getContentText())) {
                    App.Current.toastError(getContext(),"请输入点检员工号");
                    return;
                }
                if(listView.getCount() < 1) {
                    String sql = "exec fm_auto_equipment_work_line_create ?,?,?";
                    Parameters p = new Parameters().add(1, text_cell_task_order.getContentText()).add(2, text_cell_work_line.getContentText()).add(3, text_cell_check_user.getContentText());
                    App.Current.DbPortal.ExecuteRecordAsync(pn_qm_electric_equipment_check_mgr_editor.this.Connector, sql, p, new ResultHandler<DataRow>() {
                        @Override
                        public void handleMessage(Message msg) {
                            Result<DataRow> value = Value;
                            if(value.HasError) {
                                App.Current.toastError(getContext(), value.Error);
                                return;
                            }
                            if(value.Value != null) {
                                Integer head_id = value.Value.getValue("head_id", 0);
                                String cur_date = value.Value.getValue("cur_date", "");
                                if (!TextUtils.isEmpty(cur_date)) {
                                    text_cell_check_date.setContentText(cur_date);
                                }
                                if(head_id > 0) {
                                    id = head_id;
                                    initListView();
                                } else {
                                    App.Current.toastError(getContext(), "生成错误");
                                }
                            } else {
                                App.Current.toastError(getContext(), "创建错误！");
                            }
                        }
                    });
                } else {
                    initListView();
                }
            }
        });

        initListView();
    }

    private void initListView() {
        String sql = "exec fm_get_equipment_items_data ?";
        Parameters p = new Parameters().add(1, id);
        Log.e("len", "VALUE:" + id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    EquipmentAdapter esdDailyAdapter = new EquipmentAdapter(value.Value);
                    listView.setAdapter(esdDailyAdapter);
                }
            }
        });
    }

    @Override
    public void onScan(String barcode) {
        text_cell_check_user.setContentText(barcode.replace("\n", ""));
    }

    @Override
    public void commit() {
        super.commit();
        String sql = "exec fm_get_equipment_all_item_status_and ?";
        dynsoft.xone.android.data.Parameters p = new Parameters().add(1, id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                    App.Current.playSound(R.raw.error);
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
                PrintHelper.savePrintServer(pn_qm_electric_equipment_check_mgr_editor.this.getContext(), printSetting);

                PrintRequest request = new PrintRequest();
                request.Server = "http://192.168.0.102:6683";
                request.Printer = "#15";
                request.Code = "start_eip_flow_comm";

                //准备打印参数
                request.Parameters = new HashMap<String, String>();
//fd_id
                //{"fd_work_line":"'+work_line+'","fd_esd_date":"'+esd_date+'","fd_line_man":"'+line_man+'","fd_department":"'+department+'","fd_ipqc":"'+ipqc+'"}

//                String swq = "{\"fd_work_line\":\"SP-PA1\"}";

                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("{\"fd_work_line\":\"");
                stringBuffer.append(work_line);
                stringBuffer.append("\",\"fd_check_date\":\"");
                stringBuffer.append(text_cell_check_date.getContentText().toString());
                stringBuffer.append("\",\"fd_id\":\"");
                stringBuffer.append(id);
                stringBuffer.append("\",\"fd_task_order_code\":\"");
                stringBuffer.append(text_cell_task_order.getContentText().toString());
                stringBuffer.append("\",\"fd_check_user\":\"");
                stringBuffer.append(text_cell_check_user.getContentText().toString());
                stringBuffer.append("\",\"fd_comment\":\"");
                stringBuffer.append(text_cell_comment.getContentText().length() == 0 ? " " : text_cell_comment.getContentText());
                stringBuffer.append("\"}");

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
                request.Parameters.put("work_line", work_line);
                request.Parameters.put("fdTemplateId", "166e265bccb9e18b7fa268947ff999b6");
                request.Parameters.put("docSubject", work_line + "," + text_cell_task_order.getContentText() + "电批仪器设备漏电点检");
                request.Parameters.put("formValues", stringBuffer.toString().replace("\\", ""));
//                request.Parameters.put("formValues:", jsonDatas);
                request.Parameters.put("TaskUser", text_cell_check_user.getContentText());
//                request.Parameters.put("esd_date", esd_date);
//                request.Parameters.put("ipqc", ipqc_code);
//                request.Parameters.put("line_man", line_man);
//                request.Parameters.put("line_man_code", line_man_code);
//                request.Parameters.put("department", department);
//                Log.e("len", id + "**" + request.toString());
//                Log.e("len", "JSON:" + stringBuffer.toString());
                Result<String> result = PrintHelper.print(request);
                if (result.HasError) {
                    App.Current.showError(pn_qm_electric_equipment_check_mgr_editor.this.getContext(), result.Error + "1111");
                    App.Current.playSound(R.raw.error);
                } else {
                    String sql = "exec update_equipment_head_status ?";
                    Parameters p = new Parameters().add(1, id);
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
                                App.Current.toastInfo(getContext(), "提交失败！！！");
                                App.Current.playSound(R.raw.error);
                            }
                        }
                    });
                }
//                    Log.e("len", result.Value);
            }
        });
    }

    class EquipmentAdapter extends BaseAdapter {
        private DataTable dataTable;

        public EquipmentAdapter(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        @Override
        public int getCount() {
            return dataTable.Rows.size();
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
            EsdDailyViewHolder esdDailyViewHolder;
            if (view == null) {
                view = View.inflate(getContext(), R.layout.equipment_check_editor_item, null);
                esdDailyViewHolder = new EsdDailyViewHolder();
                esdDailyViewHolder.textView1 = (TextView) view.findViewById(R.id.txt_line1);
                esdDailyViewHolder.textView2 = (TextView) view.findViewById(R.id.txt_line2);
                esdDailyViewHolder.textView3 = (TextView) view.findViewById(R.id.txt_line3);
                esdDailyViewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout);
                view.setTag(esdDailyViewHolder);
            } else {
                esdDailyViewHolder = (EsdDailyViewHolder) view.getTag();
            }
            final int line_id = dataTable.Rows.get(i).getValue("id", 0);
            final String checked_equip = dataTable.Rows.get(i).getValue("checked_equip", "");
            String product_name = dataTable.Rows.get(i).getValue("product_name", "");
            String time = dataTable.Rows.get(i).getValue("time", "");
            final int counts = dataTable.Rows.get(i).getValue("counts", 0);
            final String status = dataTable.Rows.get(i).getValue("status", "");
            esdDailyViewHolder.textView1.setText(checked_equip);
            esdDailyViewHolder.textView2.setText(product_name);
            esdDailyViewHolder.textView3.setText(time);
            Log.e("len", "Status:" + status);
            if (TextUtils.isEmpty(status)) {
                esdDailyViewHolder.linearLayout.setBackgroundColor(Color.WHITE);
            } else {
                if (status.contains("提交")) {
                    esdDailyViewHolder.linearLayout.setBackgroundColor(Color.GREEN);
                } else {
                    esdDailyViewHolder.linearLayout.setBackgroundColor(Color.RED);
                }
            }
            esdDailyViewHolder.linearLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Link link = new Link("pane://x:code=qm_electric_equipment_check_editor");
                    link.Parameters.add("line_id", line_id);
                    link.Parameters.add("head_id", id);
                    link.Parameters.add("counts", counts);
                    link.Open(pn_qm_electric_equipment_check_mgr_editor.this, pn_qm_electric_equipment_check_mgr_editor.this.getContext(), null);
                }
            });
            return view;
        }
    }

    class EsdDailyViewHolder {
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private LinearLayout linearLayout;
    }
}
