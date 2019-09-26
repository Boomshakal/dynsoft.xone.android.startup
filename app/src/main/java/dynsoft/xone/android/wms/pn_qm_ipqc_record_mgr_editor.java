package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataRowCollection;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;
import oracle.jdbc.driver.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;
import oracle.sql.DATE;

/**
 * Created by Administrator on 2017/11/22.
 */

public class pn_qm_ipqc_record_mgr_editor extends pn_editor {
    private TextCell txt_line1;
    private TextCell txt_line2;
    private ButtonTextCell txt_line3;
    private ButtonTextCell txt_line4;
    private TextCell txt_line5;
    private ButtonTextCell txt_line6;
    private TextCell txt_line7;
    private TextCell txt_line8;
    private ButtonTextCell txt_line9;
    private ButtonTextCell txt_line10;

    private ImageButton btn_load_item;
    private ImageButton btn_refrash;

    private ListView matrix;
    private Long _order_id;
    private String status;
    private int item_id;
    private String item_name;
    private DataTable dataTable;
    private boolean isLoaded;
    private long task_id;
    private String order_code;
    private String task_order_code;
    private String work_line;

    private String user_code;
    private pn_qm_ipqc_record_mgr_editor.ipqc_check_adapter ipqc_check_adapter;
    private String plan_quantity;

    public pn_qm_ipqc_record_mgr_editor(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_ipqc_record_mgr_editor, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();

        task_id = this.Parameters.get("task_id", 0L);
        order_code = this.Parameters.get("order_code", "");
        task_order_code = this.Parameters.get("task_order_code", "");
        item_name = this.Parameters.get("item_name", "");
        work_line = this.Parameters.get("work_line", "");
        plan_quantity = this.Parameters.get("plan_quantity", new BigDecimal(0)).toString();

        item_id = this.Parameters.get("item_id", 0);

        txt_line1 = (TextCell) findViewById(R.id.txt_line1);
        txt_line2 = (TextCell) findViewById(R.id.txt_line2);
        txt_line3 = (ButtonTextCell) findViewById(R.id.txt_line3);
        txt_line4 = (ButtonTextCell) findViewById(R.id.txt_line4);
        txt_line5 = (TextCell) findViewById(R.id.txt_line5);
        txt_line6 = (ButtonTextCell) findViewById(R.id.txt_line6);
        txt_line7 = (TextCell) findViewById(R.id.txt_line7);
        txt_line8 = (TextCell) findViewById(R.id.txt_line8);
        txt_line9 = (ButtonTextCell) findViewById(R.id.txt_line9);
        txt_line10 = (ButtonTextCell) findViewById(R.id.txt_line10);

        matrix = (ListView) findViewById(R.id.matrix);

        if (txt_line1 != null) {
            txt_line1.setLabelText("计划编码");
            txt_line1.setReadOnly();
            txt_line1.setContentText(order_code);
        }

        if (txt_line2 != null) {
            txt_line2.setLabelText("任务编码");
            txt_line2.setReadOnly();
            txt_line2.setContentText(task_order_code);
        }

        if (txt_line3 != null) {
            txt_line3.setLabelText("巡检模板");
            txt_line3.setReadOnly();
            txt_line3.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    pn_qm_ipqc_record_mgr_editor.this.chooseTemplet(txt_line3);
                }
            });
        }

        if (txt_line4 != null) {
            txt_line4.setLabelText("线别");
            txt_line4.setReadOnly();
            if (item_id == 0) {
                txt_line4.setContentText(work_line);
            } else {
                txt_line4.Button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chooseWorkLine(txt_line4);
                    }
                });
            }
        }

        if (txt_line5 != null) {
            txt_line5.setLabelText("机型");
            txt_line5.setReadOnly();
            txt_line5.setContentText(item_name);
        }

        if(txt_line6 != null) {
            txt_line6.setLabelText("班别");
            txt_line6.setReadOnly();
            txt_line6.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseWorkType(txt_line6);
                }
            });
        }

        if (txt_line7 != null) {
            txt_line7.setLabelText("备注");
        }

        if(txt_line8 != null) {
            txt_line8.setLabelText("批量");
            txt_line8.setReadOnly();
            txt_line8.setContentText(removeSmallNumber(plan_quantity));
        }

        if (txt_line9 != null) {
            txt_line9.setLabelText("巡检时间段");
            txt_line9.setReadOnly();
            txt_line9.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseTime(txt_line9);
                }
            });
        }

        if (txt_line10 != null) {
            txt_line10.setLabelText("巡检员");
            txt_line10.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadComfirmName("SMT首件确认人", txt_line10);
                }
            });
        }

//        if (status.equals("已提交") && item_id == 0) {
//            findViewById(R.id.textview).setVisibility(
//                    View.VISIBLE);
//        }

        if (item_id == 0) {
            isLoaded = true;
            long order_id = this.Parameters.get("order_id", 0L);
            loadHeadDate(order_id);
            loadDatas();
        }

        btn_refrash = (ImageButton) findViewById(R.id.btn_refrash);
        btn_refrash.setImageBitmap(App.Current.ResourceManager.getImage("@/core_refresh_white"));
        btn_refrash.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ipqc_check_adapter != null) {
                    ipqc_check_adapter.notifyDataSetChanged();
                    if(item_id == 0) {
                        loadListViewData();
                    } else {
                        loadNewListViewData();
                    }
                }
            }
        });

        btn_load_item = (ImageButton) findViewById(R.id.btn_load_item);
        btn_load_item.setImageBitmap(App.Current.ResourceManager.getImage("@/core_save_white"));
        btn_load_item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item_id == 0) {
                    save();
                } else {
                    if (txt_line3.getContentText().equals("") || txt_line4.getContentText().equals("") || txt_line8.getContentText().equals("")) {
                        App.Current.toastInfo(pn_qm_ipqc_record_mgr_editor.this.getContext(), "请先选择巡检模板和线别和巡检时间段");
                    } else {
                        if(ipqc_check_adapter != null) {
                            App.Current.toastInfo(getContext(), "数据已经加载");
                        } else {
                            isLoaded = true;
                            pn_qm_ipqc_record_mgr_editor.this.ProgressDialog.show();
                            loadDatas();
                        }
                    }
                }
            }
        });
    }

    private void loadHeadDate(long order_id) {
        String sql = "exec p_qm_ipqc_record_load_items ?";
        Parameters p = new Parameters().add(1, order_id);
        Result<DataRow> dataRowResult = App.Current.DbPortal.ExecuteRecord(Connector, sql, p);
        if (dataRowResult.HasError) {
            App.Current.showError(getContext(), dataRowResult.Error);
            return;
        }
        if (dataRowResult.Value != null) {
            txt_line1.setContentText(dataRowResult.Value.getValue("plan_code", ""));
            txt_line2.setContentText(dataRowResult.Value.getValue("work_order_code", ""));
            txt_line3.setContentText(dataRowResult.Value.getValue("template_inspection", ""));
            txt_line4.setContentText(dataRowResult.Value.getValue("work_line", ""));
            txt_line5.setContentText(dataRowResult.Value.getValue("item_name", ""));
            txt_line6.setContentText(dataRowResult.Value.getValue("work_type", ""));
            txt_line7.setContentText(dataRowResult.Value.getValue("comment", ""));
            txt_line8.setContentText(removeSmallNumber(dataRowResult.Value.getValue("plan_quantity", new BigDecimal(0)).toString()));
            txt_line9.setContentText(dataRowResult.Value.getValue("time_quantum", ""));
            txt_line10.setContentText(getNameFromCode(dataRowResult.Value.getValue("user_code", "")));
        }
    }

    private String removeSmallNumber(String plan_quantity) {
        if (plan_quantity.indexOf(".") > 0) {
            plan_quantity = plan_quantity.replaceAll("0+?$", "");//去掉多余的0
            plan_quantity = plan_quantity.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return plan_quantity;
    }

    private String getNameFromCode(String user_code) {
        String sql = "select * from core_user where code = ?";
        Parameters p = new Parameters().add(1, user_code);
        Result<DataRow> dataRowResult = App.Current.DbPortal.ExecuteRecord(Connector, sql, p);
        if (dataRowResult.HasError) {
            App.Current.showError(getContext(), dataRowResult.Error);
            return "";
        } else {
            if (dataRowResult.Value != null) {
                return dataRowResult.Value.getValue("name", "");
            } else {
                return "";
            }
        }
    }

    private void chooseWorkType(final ButtonTextCell txt) {
        final ArrayList<String> times = new ArrayList<String>();
        times.add("A班");
        times.add("B班");

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    String time = times.get(which);
                    txt.setContentText(time);
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(pn_qm_ipqc_record_mgr_editor.this.getContext()).setTitle("请选择")
                .setSingleChoiceItems(times.toArray(new String[0]), times.indexOf(txt.getContentText()), listener)
                .setNegativeButton("取消", null).show();
    }

    private void chooseTime(final ButtonTextCell txt) {
        final ArrayList<String> times = new ArrayList<String>();
        times.add("8:00-10:00");
        times.add("10:00-12:00");
        times.add("12:00-14:00");
        times.add("14:00-16:00");
        times.add("16:00-18:00");
        times.add("18:00-20:00");
        times.add("20:00-22:00");
        times.add("22:00-0:00");
        times.add("0:00-2:00");
        times.add("2:00-4:00");
        times.add("4:00-6:00");
        times.add("6:00-8:00");

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    String time = times.get(which);
                    txt.setContentText(time);
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(pn_qm_ipqc_record_mgr_editor.this.getContext()).setTitle("请选择")
                .setSingleChoiceItems(times.toArray(new String[0]), times.indexOf(txt.getContentText()), listener)
                .setNegativeButton("取消", null).show();
    }

    private void chooseTemplet(final ButtonTextCell txt) {
        String sql = "select DISTINCT(plan_type) from qm_ipqc_inspection_item where plan_type not in (select plan_type from qm_ipqc_inspection_item where plan_type like '%IPQC%')";
        final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(Connector, sql);

        if (result.HasError) {
            App.Current.showError(this.getContext(), result.Error);
            return;
        }
        if (result.Value != null) {

            ArrayList<String> names = new ArrayList<String>();
            for (DataRow row : result.Value.Rows) {
                String name = row.getValue("plan_type", "");
                names.add(name);
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which >= 0) {
                        DataRow row = result.Value.Rows.get(which);
                        txt.setContentText(row.getValue(
                                "plan_type", ""));
                    }
                    dialog.dismiss();
                }
            };
            new AlertDialog.Builder(pn_qm_ipqc_record_mgr_editor.this.getContext()).setTitle("请选择")
                    .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(txt.getContentText()), listener)
                    .setNegativeButton("取消", null).show();
        }
    }

    private void chooseWorkLine(final ButtonTextCell txt) {
        String sql = "select name from fm_work_line where name like 'AI%' or name like 'SMT%'";
        final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(Connector, sql);

        if (result.HasError) {
            App.Current.showError(this.getContext(), result.Error);
            return;
        }
        if (result.Value != null) {

            ArrayList<String> names = new ArrayList<String>();
            for (DataRow row : result.Value.Rows) {
                String name = row.getValue("name", "");
                names.add(name);
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which >= 0) {
                        DataRow row = result.Value.Rows.get(which);
                        txt.setContentText(row.getValue(
                                "name", ""));
                    }
                    dialog.dismiss();
                }
            };
            new AlertDialog.Builder(pn_qm_ipqc_record_mgr_editor.this.getContext()).setTitle("请选择")
                    .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(txt.getContentText()), listener)
                    .setNegativeButton("取消", null).show();
        }
    }

    public void loadComfirmName(String lookup_type, final ButtonTextCell txt) {

        String sql = "SELECT lookup_code,meaning from dbo.core_data_keyword WHERE lookup_type =?";
        Parameters p = new Parameters().add(1, lookup_type);
        final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (result.HasError) {
            App.Current.showError(this.getContext(), result.Error);
            return;
        }
        if (result.Value != null) {

            ArrayList<String> names = new ArrayList<String>();
            for (DataRow row : result.Value.Rows) {
                String name = row.getValue("lookup_code", "");
                names.add(name);
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which >= 0) {
                        DataRow row = result.Value.Rows.get(which);
                        txt.setContentText(row.getValue(
                                "lookup_code", ""));
                    }
                    dialog.dismiss();
                }
            };
            new AlertDialog.Builder(pn_qm_ipqc_record_mgr_editor.this.getContext()).setTitle("请选择")
                    .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(txt.getContentText().toString()), listener)
                    .setNegativeButton("取消", null).show();
        }
    }


    @Override
    public void onScan(String barcode) {

        if (this.txt_line7.getContentText() != null) {
            String loc = barcode.substring(2, barcode.length());
            String locs = this.txt_line7.TextBox.getText().toString().trim();
            if (locs.contains(loc)) {
                return;
            }

            if (locs.length() > 0) {
                locs += ", ";
            }
            this.txt_line7.setContentText(locs + loc);
        }
    }

    private void loadDatas() {
        if (item_id == 0) {
            _order_id = this.Parameters.get("order_id", 0L);
            loadListViewData();
        } else {
            _order_id = 0L;
            new Thread() {
                @Override
                public void run() {
                    loadNewListViewData();
                }
            }.start();
        }
    }

    private void loadListViewData() {
        this.ProgressDialog.show();

        String sql = "exec p_qm_ipqc_record_get_item ?,?";
        Parameters p = new Parameters().add(1, App.Current.UserID).add(2, _order_id);
        App.Current.DbPortal.ExecuteDataTableAsync(Connector, sql, p, new ResultHandler<DataTable>() {

            @Override
            public void handleMessage(Message msg) {
                pn_qm_ipqc_record_mgr_editor.this.ProgressDialog.dismiss();
                    ipqc_check_adapter = new ipqc_check_adapter(Value.Value.Rows);
                    matrix.setAdapter(ipqc_check_adapter);
                    pn_qm_ipqc_record_mgr_editor.this.ProgressDialog.dismiss();
                if (Value.Value.Rows.size() > 0) {
                    status = Value.Value.Rows.get(0).getValue("status", "");
                }
                if (status.equals("已提交")) {
                    txt_line3.setReadOnly();
                    txt_line4.setReadOnly();
                    txt_line7.setReadOnly();
                    txt_line9.setReadOnly();
                    txt_line10.setReadOnly();
                }
                loadItems(Value.Value);
            }
        });
    }

    class ipqc_check_adapter extends BaseAdapter {
        private DataRowCollection mRows;

        public ipqc_check_adapter(DataRowCollection rows) {
            mRows = rows;
        }

        @Override
        public int getCount() {
            return mRows == null ? 0 : mRows.size();
        }

        @Override
        public Object getItem(int i) {
            return mRows.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = View.inflate(getContext(), R.layout.ln_ipqc_record_mgr_listview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.txt_line1 = (TextView) view.findViewById(R.id.txt_line1);
                viewHolder.txt_line2 = (TextView) view.findViewById(R.id.txt_line2);
                viewHolder.txt_line3 = (TextView) view.findViewById(R.id.txt_line3);
                viewHolder.txt_line4 = (TextView) view.findViewById(R.id.txt_line4);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            if (item_id == 0) {
                viewHolder.txt_line1.setText(mRows.get(position).getValue("project_name", ""));
                viewHolder.txt_line2.setText(mRows.get(position).getValue("standards", ""));
                viewHolder.txt_line3.setText(mRows.get(position).getValue("dic_name", ""));
                viewHolder.txt_line4.setText(mRows.get(position).getValue("result_value", ""));
            } else {
                viewHolder.txt_line1.setText(mRows.get(position).getValue("project_name", ""));
                viewHolder.txt_line2.setText(mRows.get(position).getValue("standards", ""));
                viewHolder.txt_line3.setText(mRows.get(position).getValue("dic_name", ""));
                viewHolder.txt_line4.setText(mRows.get(position).getValue("result_value", ""));
            }
            String result_value = mRows.get(position).getValue("result_value", "");
            if (result_value.equals("OK")) {
                view.setBackgroundColor(getResources().getColor(R.color.limegreen));
            } else if (result_value.equals("NG")) {
                view.setBackgroundColor(getResources().getColor(R.color.crimson));
            } else if (result_value.equals("NA")) {
                view.setBackgroundColor(getResources().getColor(R.color.gold));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.white));
            }
            return view;
        }
    }

    class ViewHolder {
        private TextView txt_line1;
        private TextView txt_line2;
        private TextView txt_line3;
        private TextView txt_line4;
    }

    private void loadNewListViewData() {
        dataTable = loadItem();
        App.Current.Workbench.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean s = save();
                if (s) {
                    pn_qm_ipqc_record_mgr_editor.this.ProgressDialog.dismiss();
                    loadListViewData();
                } else {
                    pn_qm_ipqc_record_mgr_editor.this.ProgressDialog.dismiss();
                    return;
                }
            }
        });
    }

    private void loadItems(final DataTable dataTable) {
        App.Current.Workbench.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                matrix.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        long x = getCurIndex(dataTable, position);
                        if (status.equals("已提交")) {
                            return;
                        }
                        if (x == -1) {
                            App.Current.toastError(getContext(), "已检查");
                        } else {
                            Link link = new Link("pane://x:code=qm_and_ipqc_patrol_record_editor");
                            link.Parameters.add("order_id", dataTable.Rows.get(position).getValue("id", Long.class));
                            link.Parameters.add("order_code", dataTable.Rows.get(position).getValue("code", ""));
                            link.Parameters.add("index", x);
                            link.Open(pn_qm_ipqc_record_mgr_editor.this, pn_qm_ipqc_record_mgr_editor.this.getContext(), null);
                        }
                    }
                });
            }
        });
    }

    private long getCurIndex(DataTable dataTable, int position) {
        String sql = "exec p_qm_ipqc_patrol_record_get_index ?,?,?";
        Parameters p = new Parameters().add(1, App.Current.UserID).add(2, dataTable.Rows.get(position).getValue("head_id", Long.class)).add(3, dataTable.Rows.get(position).getValue("line_id", Long.class));
        Result<DataRow> dataRowResult = App.Current.DbPortal.ExecuteRecord(Connector, sql, p);
        if (dataRowResult.HasError) {
            App.Current.showError(getContext(), dataRowResult.Error);
        }
        if (dataRowResult.Value != null) {
            return dataRowResult.Value.getValue("rownum", 0L);
        } else {
            return -1L;
        }
    }

    public DataTable loadItem() {
        String sql = "exec p_qm_ipqc_record_get_item_by_plan_type ?";
        Parameters p = new Parameters().add(1, txt_line3.getContentText());
        Result<DataTable> dataTableResult = App.Current.DbPortal.ExecuteDataTable(Connector, sql, p);
        return dataTableResult.Value;
    }

    public boolean save() {
        boolean codeFromName = getCodeFromName(txt_line10.getContentText());
        if (!codeFromName) {
            return false;
        } else {
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(currentTime);
            Map<String, String> head_entry = new HashMap<String, String>();

            head_entry.put("create_date", dateString);
            head_entry.put("code", txt_line1.getContentText());
            head_entry.put("create_by", App.Current.UserID);
            head_entry.put("work_line", txt_line4.getContentText());
            head_entry.put("comment", txt_line7.getContentText());
            head_entry.put("time_quantum", txt_line9.getContentText());
            head_entry.put("user_code", user_code);
            head_entry.put("task_order_id", task_id + "");
            head_entry.put("id", String.valueOf(_order_id));
            head_entry.put("template_inspection", txt_line3.getContentText());
            head_entry.put("work_type", txt_line6.getContentText());

            ArrayList<Map<String, String>> item_entries = new ArrayList<Map<String, String>>();
            if (dataTable != null) {
                Map<String, String> item_entry = null;
                for (int i = 0; i < dataTable.Rows.size(); i++) {
                    item_entry = new HashMap<String, String>();
                    item_entry.put("work_line", txt_line4.getContentText());
                    item_entry.put("project_name", dataTable.Rows.get(i).getValue("project_name", ""));
                    item_entry.put("standards", dataTable.Rows.get(i).getValue("standards", ""));
                    item_entry.put("create_date", dateString);
                    item_entry.put("task_order_id", task_id + "");
                    item_entry.put("create_by", App.Current.UserID);
                    item_entry.put("operator", App.Current.UserID);
                    item_entry.put("status", "已下达");
                    item_entries.add(item_entry);
                }
            }

            String head_xml = XmlHelper.createXml("document", head_entry, "items", "item", item_entries);

            String sql = "exec qm_ipqc_record_create ?,?";
            Connection conn = App.Current.DbPortal.CreateConnection(pn_qm_ipqc_record_mgr_editor.this.Connector);
            CallableStatement stmt;
            try {
                stmt = conn.prepareCall(sql);
                stmt.setObject(1, head_xml);
                stmt.registerOutParameter(2, Types.VARCHAR);
                stmt.execute();
                boolean moreResults = stmt.getMoreResults();
                String string = stmt.getString(2);
                if (string != null) {
                    Result<String> stringResult = XmlHelper.parseResult(string);
                    if (stringResult.HasError) {
                        App.Current.showError(pn_qm_ipqc_record_mgr_editor.this.getContext(), stringResult.Error);
                        return false;
                    }
                    String v = stringResult.Value;
                    _order_id = Long.parseLong(v);
                    loadHeadDate(_order_id);
                    App.Current.toastInfo(pn_qm_ipqc_record_mgr_editor.this.getContext(), "加载完成");
                }
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                App.Current.showError(pn_qm_ipqc_record_mgr_editor.this.getContext(), e.getMessage());
                return false;
            }
        }
    }

    private boolean getCodeFromName(String contentText) {
        String sql = "select * from core_user where name = ?";
        Parameters p = new Parameters().add(1, contentText);
        Result<DataRow> dataRowResult = App.Current.DbPortal.ExecuteRecord(Connector, sql, p);
        if (dataRowResult.HasError) {
            App.Current.showError(getContext(), dataRowResult.Error);
            return false;
        }

        if (dataRowResult.Value != null) {
            user_code = dataRowResult.Value.getValue("code", "");
            return true;
        } else {
            App.Current.toastError(getContext(), "确认人不存在，请重新选择");
            return false;
        }
    }

    @Override
    public void commit() {
        super.commit();
        if (isLoaded) {
            if (status.equals("已提交")) {
                App.Current.toastInfo(pn_qm_ipqc_record_mgr_editor.this.getContext(), "已经提交过了" + txt_line8.TextBox.getText().toString().replace("\n", ","));
            } else {
                pn_qm_ipqc_record_mgr_editor.this.ProgressDialog.show();

                String sql = "exec p_mm_ipqc_patrol_record_commit ?,?";
                Parameters p = new Parameters().add(1, App.Current.UserID).add(2, _order_id);
                Result<Integer> integerResult = App.Current.DbPortal.ExecuteNonQuery(Connector, sql, p);
                if (integerResult.HasError) {
                    App.Current.showError(getContext(), integerResult.Error);
                    pn_qm_ipqc_record_mgr_editor.this.ProgressDialog.dismiss();
                    return;
                }
                App.Current.toastInfo(getContext(), "提交成功");
                pn_qm_ipqc_record_mgr_editor.this.ProgressDialog.dismiss();
            }
        } else {
            App.Current.toastInfo(pn_qm_ipqc_record_mgr_editor.this.getContext(), "请先加载物料列表");
        }
    }
}