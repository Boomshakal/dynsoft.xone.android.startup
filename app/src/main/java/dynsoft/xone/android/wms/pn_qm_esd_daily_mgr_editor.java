package dynsoft.xone.android.wms;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.PrintRequest;
import dynsoft.xone.android.data.PrintSetting;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.PrintHelper;
import dynsoft.xone.android.link.Link;

/**
 * Created by Administrator on 2018/6/7.
 */

public class pn_qm_esd_daily_mgr_editor extends pn_editor {
    private long id;
    private String esd_date;
    private String work_line;

    private TextCell textCell1;
    private TextCell textCell2;
    private ListView listView;
    private String ipqc_code;
    private ImageButton btn_refrash;
    private String line_man;
    private String department;
    private String line_man_code;

    public pn_qm_esd_daily_mgr_editor(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_esd_daily_mgr_editor, this, true);
        view.setLayoutParams(lp);
        //noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        id = this.Parameters.get("head_id", 0L);
        esd_date = this.Parameters.get("esd_date", "");
        work_line = this.Parameters.get("work_line", "");
        ipqc_code = this.Parameters.get("ipqc_code", "");
        line_man = this.Parameters.get("line_man", "");
        line_man_code = this.Parameters.get("line_man_code", "");
        department = this.Parameters.get("department", "");

        textCell1 = (TextCell) findViewById(R.id.text_cell_1);
        textCell2 = (TextCell) findViewById(R.id.text_cell_2);
        listView = (ListView) findViewById(R.id.listview_1);

        if (textCell1 != null) {
            textCell1.setLabelText("线别");
            textCell1.setContentText(work_line);
        }
        if (textCell2 != null) {
            textCell2.setLabelText("日期");
            textCell2.setContentText(esd_date);
        }

        btn_refrash = (ImageButton) findViewById(R.id.btn_refrash);
        btn_refrash.setImageBitmap(App.Current.ResourceManager.getImage("@/core_refresh_white"));
        btn_refrash.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                initListView();
            }
        });

        initListView();
    }

    private void initListView() {
        String sql = "exec fm_get_esd_items_data ?";
        Parameters p = new Parameters().add(1, id);
        Log.e("len", "IDD" + id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    EsdDailyAdapter esdDailyAdapter = new EsdDailyAdapter(value.Value);
                    listView.setAdapter(esdDailyAdapter);
                }
            }
        });
    }

    @Override
    public void commit() {
        super.commit();
        String sql = "exec fm_get_esd_all_item_status_and ?";
        dynsoft.xone.android.data.Parameters p = new Parameters().add(1, id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                    return;
                }
                if (value.Value != null ) {    //发起流程
                    if(value.Value.Rows.size() == 0) {
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
                Log.e("len", "IPQC:" + ipqc_code);

                //App.Current.Print("start_eip_flow", "发起流程", parameters);

                //保存打印设置
                PrintSetting printSetting = new PrintSetting();
                printSetting.Server = "株洲条码打印服务器102";
                printSetting.Url = "http://192.168.0.102:6683";
                printSetting.Printer = "#15";
                PrintHelper.savePrintServer(pn_qm_esd_daily_mgr_editor.this.getContext(), printSetting);

                PrintRequest request = new PrintRequest();
                request.Server = "http://192.168.0.102:6683";
                request.Printer = "#15";
                request.Code = "start_eip_flow";

                //准备打印参数
                request.Parameters = new HashMap<String, String>();
                request.Parameters.put("user_id", App.Current.UserID);

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
                request.Parameters.put("esd_date", esd_date);
                request.Parameters.put("ipqc", ipqc_code);
                request.Parameters.put("line_man", line_man);
                request.Parameters.put("line_man_code", line_man_code);
                request.Parameters.put("department", department);

                Result<String> result = PrintHelper.print(request);
                if (result.HasError) {
                    App.Current.showError(pn_qm_esd_daily_mgr_editor.this.getContext(), result.Error);
                    App.Current.playSound(R.raw.error);
                } else {
                    String sql = "exec update_esd_head_status ?";
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

    class EsdDailyAdapter extends BaseAdapter {
        private DataTable dataTable;

        public EsdDailyAdapter(DataTable dataTable) {
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
                view = View.inflate(getContext(), R.layout.esd_daily_editor_item, null);
                esdDailyViewHolder = new EsdDailyViewHolder();
                esdDailyViewHolder.textView1 = (TextView) view.findViewById(R.id.txt_line1);
                esdDailyViewHolder.textView2 = (TextView) view.findViewById(R.id.txt_line2);
                esdDailyViewHolder.textView3 = (TextView) view.findViewById(R.id.txt_line3);
                esdDailyViewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout);
                view.setTag(esdDailyViewHolder);
            } else {
                esdDailyViewHolder = (EsdDailyViewHolder) view.getTag();
            }
            final long line_id = dataTable.Rows.get(i).getValue("line_id", 0L);
            final String work_line = dataTable.Rows.get(i).getValue("work_line", "");
            String check_type = dataTable.Rows.get(i).getValue("check_type", "");
            String check_content = dataTable.Rows.get(i).getValue("check_content", "");
            String status = dataTable.Rows.get(i).getValue("status", "");
            final int counts = dataTable.Rows.get(i).getValue("counts", 0);
            esdDailyViewHolder.textView1.setText(check_type);
            esdDailyViewHolder.textView2.setText(check_content);
            esdDailyViewHolder.textView3.setText(work_line);
            if (TextUtils.isEmpty(status)) {
                esdDailyViewHolder.linearLayout.setBackgroundColor(Color.WHITE);
            } else {
                if ("OK".equals(status)) {
                    esdDailyViewHolder.linearLayout.setBackgroundColor(Color.GREEN);
                } else {
                    esdDailyViewHolder.linearLayout.setBackgroundColor(Color.RED);
                }
            }
            esdDailyViewHolder.linearLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Link link = new Link("pane://x:code=qm_esd_daily_editor");
                    link.Parameters.add("line_id", line_id);
                    link.Parameters.add("work_line", work_line);
                    link.Parameters.add("head_id", id);
                    link.Parameters.add("counts", counts);
                    link.Open(pn_qm_esd_daily_mgr_editor.this, pn_qm_esd_daily_mgr_editor.this.getContext(), null);
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
