package dynsoft.xone.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import dynsoft.xone.android.adapter.GridviewAdapter;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import oracle.jdbc.driver.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;


/**
 * Created by Administrator on 2018/5/8.
 */

public class LocationCheckSMTActivity extends Activity implements View.OnClickListener {
    private String[] titles = {"生产线:", "班次:", "工序名称:", "产品型号:", "定单号:", "批量:"};
    private String[] headerStrings = {"不良现象\\时间", "8:00-9:00", "9:00-10:00", "10:00-11:00", "11:00-11:50"
            , "12:20-13:20", "13:20-14:20", "14:20-15:20", "15:20-16:50", "17:20-18:20","18:20-19:20",  "19:20-20:00", "合计"};
    //    private String[] leftStrings = {"虚焊", "短路", "少件", "多件", "插错", "插反", "翘铜皮",
//            "浮高", "移位", "脚短", "破件", "漏打胶", "溢胶", "不出脚", "针孔", "包焊", "拉尖",
//            "剪脚未加锡", "锡裂", "板脏"};
    private String[] leftStrings2 = {"检验数量", "不良数量", "不良率", "组长确认", "IPQC确认"};
    private GridView gridView;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private TextView textView6;
    private TextView textView7;
    private TextView textView8;
    private TextView textView9;
    private TextView textView10;
    private TextView textView11;
    private TextView textView12;
    private TextView textView13;
    private ListView listView1;
    private ListView listView2;
    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private SharedPreferences sharedPreferences;
    private ArrayList<String> content;
    private String segment;
    private String production;
    private String task_order_code;
    private ArrayList<String> whs;
    private boolean radioButtonChecked;   //单层还是多层，false 是单层，true 是多层
    private ArrayList<Integer> checkedItem;
    private int item_id;                    // 物料编码
    private int org_id;                     //组织编码
    private ItemListViewAdapter itemListViewAdapter;
    private double time11;
    private double time12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_smt_check);
        sharedPreferences = getSharedPreferences("sop", MODE_PRIVATE);
        content = new ArrayList<String>();
        segment = sharedPreferences.getString("segment", "");
        item_id = sharedPreferences.getInt("item_id", 0);
        org_id = sharedPreferences.getInt("org_id", 0);
        content.add(segment);
        Calendar calendar = Calendar.getInstance();
        int i = calendar.get(Calendar.HOUR_OF_DAY);
        if (i < 18) {
            content.add("白");
        } else {
            content.add("夜");
        }
        production = sharedPreferences.getString("production", "");
        String item_name = sharedPreferences.getString("item_name", "");
        task_order_code = sharedPreferences.getString("task_order", "");
        String plan_quantity = sharedPreferences.getString("plan_quantity", "");
        content.add(production);
        content.add(item_name);
        content.add(task_order_code);
        content.add(removeSmallNumber(plan_quantity));

        textView1 = (TextView) findViewById(R.id.textview_1);
        textView2 = (TextView) findViewById(R.id.textview_2);
        textView3 = (TextView) findViewById(R.id.textview_3);
        textView4 = (TextView) findViewById(R.id.textview_4);
        textView5 = (TextView) findViewById(R.id.textview_5);
        textView6 = (TextView) findViewById(R.id.textview_6);
        textView7 = (TextView) findViewById(R.id.textview_7);
        textView8 = (TextView) findViewById(R.id.textview_8);
        textView9 = (TextView) findViewById(R.id.textview_9);
        textView10 = (TextView) findViewById(R.id.textview_10);
        textView11 = (TextView) findViewById(R.id.textview_11);
        textView12 = (TextView) findViewById(R.id.textview_12);
        textView13 = (TextView) findViewById(R.id.textview_13);
        gridView = (GridView) findViewById(R.id.gridview_location);
        listView1 = (ListView) findViewById(R.id.listview_location_1);
        listView2 = (ListView) findViewById(R.id.listview_location_2);
        radioGroup = (RadioGroup) findViewById(R.id.location_radiogroup);
        radioButton1 = (RadioButton) findViewById(R.id.radiobutton_1);
        radioButton2 = (RadioButton) findViewById(R.id.radiobutton_2);
        radioButton2.setChecked(true);
        textView1.setText(headerStrings[0]);
        textView2.setText(headerStrings[1]);
        textView3.setText(headerStrings[2]);
        textView4.setText(headerStrings[3]);
        textView5.setText(headerStrings[4]);
        textView6.setText(headerStrings[5]);
        textView7.setText(headerStrings[6]);
        textView8.setText(headerStrings[7]);
        textView9.setText(headerStrings[8]);
        textView10.setText(headerStrings[9]);
        textView11.setText(headerStrings[10]);
        textView12.setText(headerStrings[11]);
        textView13.setText(headerStrings[12]);
        initGridView();
        initListView1();
        initListView2();

        radioButton1.setOnClickListener(this);
        radioButton2.setOnClickListener(this);

    }

    private void initGridView() {
        GridviewAdapter gridviewAdapter = new GridviewAdapter(titles, content, LocationCheckSMTActivity.this);
        gridView.setAdapter(gridviewAdapter);
    }

    //    private void initListView1() {
//        String sql = "select * from fm_work_location_check_type";
//        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {
//            @Override
//            public void handleMessage(Message msg) {
//                Result<DataTable> value = Value;
//                if (value.HasError) {
//                    App.Current.toastError(LocationCheckActivity.this, value.Error);
//                    return;
//                }
//                if (value.Value != null && value.Value.Rows.size() > 0) {
//                    LocationListViewAdapter1 locationListViewAdapter1 = new LocationListViewAdapter1(value.Value);
//                    listView1.setAdapter(locationListViewAdapter1);
//                }
//            }
//        });
//
//    }

    private void initListView1() {
        if (production.contains(",")) {
            String[] substring = production.split(",");
            String seqName = substring[1];
            String sql = "exec fm_get_smt_location_each_counts_and ?,?";
            Parameters p = new Parameters().add(1, task_order_code).add(2, seqName);
            Log.e("len", segment + "&&&&&&&" + seqName);
            App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
                @Override
                public void handleMessage(Message msg) {
                    Result<DataTable> value = Value;
                    if (value.HasError) {
                        App.Current.toastError(LocationCheckSMTActivity.this, value.Error);
                        return;
                    }
                    if (value.Value != null && value.Value.Rows.size() > 0) {
                        LocationListViewAdapter1 locationListViewAdapter1 = new LocationListViewAdapter1(value.Value);
                        listView1.setAdapter(locationListViewAdapter1);
                    }
                }
            });
        } else {
            App.Current.toastError(LocationCheckSMTActivity.this, "工序错误！");
            App.Current.playSound(R.raw.error);
        }
    }

    private void initListView2() {
        if (production.contains(",")) {
            String[] substring = production.split(",");
            String seqName = substring[1];
            String sql = "exec fm_smt_location_kanban ?,?,?";
            Parameters p = new Parameters().add(1, segment).add(2, task_order_code).add(3, seqName);
            App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
                @Override
                public void handleMessage(Message msg) {
                    Result<DataTable> value = Value;
                    if (value.HasError) {
                        App.Current.toastError(LocationCheckSMTActivity.this, value.Error);
                        return;
                    }
                    if (value.Value != null && value.Value.Rows.size() > 0) {
                        Log.e("len", "LIST2:" + value.Value.Rows.size());
                        LocationListViewAdapter2 locationListViewAdapter2 = new LocationListViewAdapter2(value.Value);
                        listView2.setAdapter(locationListViewAdapter2);
                    }
                }
            });
        } else {

        }

    }

    private String removeSmallNumber(String plan_quantity) {
        if (plan_quantity.indexOf(".") > 0) {
            plan_quantity = plan_quantity.replaceAll("0+?$", "");//去掉多余的0
            plan_quantity = plan_quantity.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return plan_quantity;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.radiobutton_1:
                radioButtonChecked = false;
                break;
            case R.id.radiobutton_2:
                radioButtonChecked = true;
                break;
        }
    }


    class LocationListViewAdapter1 extends BaseAdapter {
        private DataTable dataTable;

        public LocationListViewAdapter1(DataTable dataTable) {
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
            final LocationListViewViewHolder locationListViewViewHolder;
            if (view == null) {
                view = View.inflate(LocationCheckSMTActivity.this, R.layout.location_smt_listview_item, null);
                locationListViewViewHolder = new LocationListViewViewHolder();
                locationListViewViewHolder.textView1 = (TextView) view.findViewById(R.id.textview_1);
                locationListViewViewHolder.textView2 = (TextView) view.findViewById(R.id.textview_2);
                locationListViewViewHolder.textView3 = (TextView) view.findViewById(R.id.textview_3);
                locationListViewViewHolder.textView4 = (TextView) view.findViewById(R.id.textview_4);
                locationListViewViewHolder.textView5 = (TextView) view.findViewById(R.id.textview_5);
                locationListViewViewHolder.textView6 = (TextView) view.findViewById(R.id.textview_6);
                locationListViewViewHolder.textView7 = (TextView) view.findViewById(R.id.textview_7);
                locationListViewViewHolder.textView8 = (TextView) view.findViewById(R.id.textview_8);
                locationListViewViewHolder.textView9 = (TextView) view.findViewById(R.id.textview_9);
                locationListViewViewHolder.textView10 = (TextView) view.findViewById(R.id.textview_10);
                locationListViewViewHolder.textView11 = (TextView) view.findViewById(R.id.textview_11);
                locationListViewViewHolder.textView12 = (TextView) view.findViewById(R.id.textview_12);
                locationListViewViewHolder.textView13 = (TextView) view.findViewById(R.id.textview_13);
                view.setTag(locationListViewViewHolder);
            } else {
                locationListViewViewHolder = (LocationListViewViewHolder) view.getTag();
            }
            final String type = dataTable.Rows.get(i).getValue("bad_type", "");
            int time1 = dataTable.Rows.get(i).getValue(headerStrings[1], 0);
            int time2 = dataTable.Rows.get(i).getValue(headerStrings[2], 0);
            int time3 = dataTable.Rows.get(i).getValue(headerStrings[3], 0);
            int time4 = dataTable.Rows.get(i).getValue(headerStrings[4], 0);
            int time5 = dataTable.Rows.get(i).getValue(headerStrings[5], 0);
            int time6 = dataTable.Rows.get(i).getValue(headerStrings[6], 0);
            int time7 = dataTable.Rows.get(i).getValue(headerStrings[7], 0);
            int time8 = dataTable.Rows.get(i).getValue(headerStrings[8], 0);
            int time9 = dataTable.Rows.get(i).getValue(headerStrings[9], 0);
            int time10 = dataTable.Rows.get(i).getValue(headerStrings[10], 0);
            int time11 = dataTable.Rows.get(i).getValue("plug_in_count", 0);

            locationListViewViewHolder.textView1.setText(type);
            locationListViewViewHolder.textView2.setText(String.valueOf(time1));
            locationListViewViewHolder.textView3.setText(String.valueOf(time2));
            locationListViewViewHolder.textView4.setText(String.valueOf(time3));
            locationListViewViewHolder.textView5.setText(String.valueOf(time4));
            locationListViewViewHolder.textView6.setText(String.valueOf(time5));
            locationListViewViewHolder.textView7.setText(String.valueOf(time6));
            locationListViewViewHolder.textView8.setText(String.valueOf(time7));
            locationListViewViewHolder.textView9.setText(String.valueOf(time8));
            locationListViewViewHolder.textView10.setText(String.valueOf(time9));
            locationListViewViewHolder.textView11.setText(String.valueOf(time10));
            locationListViewViewHolder.textView12.setText(String.valueOf(time11));
            locationListViewViewHolder.textView13.setText(String.valueOf(time1 + time2 + time3 + time4 + time5 + time6 + time7 + time8 + time9 + time10 + time11));

            locationListViewViewHolder.textView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastPopupWindow(type, headerStrings[1], locationListViewViewHolder.textView2);
                }
            });
            locationListViewViewHolder.textView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastPopupWindow(type, headerStrings[2], locationListViewViewHolder.textView3);
                }
            });
            locationListViewViewHolder.textView4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastPopupWindow(type, headerStrings[3], locationListViewViewHolder.textView4);
                }
            });
            locationListViewViewHolder.textView5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastPopupWindow(type, headerStrings[4], locationListViewViewHolder.textView5);
                }
            });
            locationListViewViewHolder.textView6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastPopupWindow(type, headerStrings[5], locationListViewViewHolder.textView6);
                }
            });
            locationListViewViewHolder.textView7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastPopupWindow(type, headerStrings[6], locationListViewViewHolder.textView7);
                }
            });
            locationListViewViewHolder.textView8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastPopupWindow(type, headerStrings[7], locationListViewViewHolder.textView8);
                }
            });
            locationListViewViewHolder.textView9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastPopupWindow(type, headerStrings[8], locationListViewViewHolder.textView9);
                }
            });
            locationListViewViewHolder.textView10.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastPopupWindow(type, headerStrings[9], locationListViewViewHolder.textView10);
                }
            });
            return view;
        }
    }

    private void ToastPopupWindow(final String leftString, final String headerString, final TextView textView) {
        final PopupWindow popupWindow = new PopupWindow();
        View view = View.inflate(LocationCheckSMTActivity.this, R.layout.item_location_check_popupwindow, null);
        final ButtonTextCell buttonTextCell1 = (ButtonTextCell) view.findViewById(R.id.button_text_cell_1);
        final ButtonTextCell buttonTextCell2 = (ButtonTextCell) view.findViewById(R.id.button_text_cell_2);
        final ButtonTextCell buttonTextCell3 = (ButtonTextCell) view.findViewById(R.id.button_text_cell_3);

        if (buttonTextCell1 != null) {
            buttonTextCell1.setLabelText("位号");
            buttonTextCell1.setContentText("");
            buttonTextCell1.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_downward_light"));
            buttonTextCell1.Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whs = new ArrayList<String>();
                    dialogChooceWh(buttonTextCell1, buttonTextCell2);
                }
            });
        }

        if (buttonTextCell2 != null) {
            buttonTextCell2.setLabelText("数量");
            buttonTextCell2.setContentText("");
            buttonTextCell2.Button.setVisibility(View.GONE);
        }

        if (buttonTextCell3 != null) {
            buttonTextCell3.setLabelText("产品条码");
            buttonTextCell3.setContentText("");
            buttonTextCell3.Button.setVisibility(View.GONE);
        }
        TextView buttonConfirm = (TextView) view.findViewById(R.id.confirm);
        final TextView buttonCancel = (TextView) view.findViewById(R.id.cancel);

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(buttonTextCell1.getContentText())) {
                    App.Current.toastError(LocationCheckSMTActivity.this, "请选择位号！");
                } else if (TextUtils.isEmpty(buttonTextCell2.getContentText())) {
                    App.Current.toastError(LocationCheckSMTActivity.this, "请输入数量！");
                } else if (production.contains(",")) {
                    String[] substring = production.split(",");
                    String seqName = substring[0];
                    String sql = "exec p_fm_location_check_commit ?,?,?,?,?,?,?,?,?";
                    Parameters p = new Parameters().add(1, task_order_code).add(2, seqName).add(3, segment)
                            .add(4, leftString).add(5, buttonTextCell2.getContentText()).add(6, buttonTextCell1.getContentText())
                            .add(7, App.Current.UserID).add(8, headerString).add(9, buttonTextCell3.getContentText());
                    Log.e("len", task_order_code + "**" + seqName + "**" + segment + "**" + leftString + headerString);
                    App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                        @Override
                        public void handleMessage(Message msg) {
                            Result<Integer> value = Value;
                            if (value.HasError) {
                                App.Current.toastError(LocationCheckSMTActivity.this, value.Error);
                                popupWindow.dismiss();
                                return;
                            }
                            if (value.Value != null) {
                                App.Current.toastInfo(LocationCheckSMTActivity.this, "提交成功");
                                App.Current.playSound(R.raw.pass);
                                initListView1();
                                initListView2();
                                popupWindow.dismiss();
                            }
                        }
                    });
                } else {
                    App.Current.toastInfo(LocationCheckSMTActivity.this, "提交失败，请查看工序！");
                    App.Current.playSound(R.raw.error);
                    popupWindow.dismiss();
                }
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setContentView(view);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
//                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.style_divider_color));
        popupWindow.showAtLocation(gridView, Gravity.CENTER, 20, 30);
    }

    private void dialogChooceWh(final ButtonTextCell buttonTextCell1, final ButtonTextCell buttonTextCell2) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LocationCheckSMTActivity.this);
        View view = View.inflate(LocationCheckSMTActivity.this, R.layout.location_choose_wh_view, null);
        final ButtonTextCell buttonTextCell = (ButtonTextCell) view.findViewById(R.id.item_buttontextcell);
        final ListView listView = (ListView) view.findViewById(R.id.item_listview);
        initItemListView(buttonTextCell.getContentText(), listView);
        TextView buttonConfirm = (TextView) view.findViewById(R.id.confirm);
        TextView buttonCancel = (TextView) view.findViewById(R.id.cancel);
        builder.setView(view);
        final AlertDialog show = builder.show();

        if (buttonTextCell != null) {
            buttonTextCell.setLabelText("");
            buttonTextCell.TextBox.setHint("请输入位号查询");
            buttonTextCell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_gray"));
            buttonTextCell.Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //根据输入搜索 buttonTextCell.getContentText()
                    //搜索到位号 把数据给ListView
                    initItemListView(buttonTextCell.getContentText(), listView);

                }
            });
        }

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("len", "WHS:" + whs.toString());
                String whsContent = whs.toString().substring(1, whs.toString().length() - 1);
                buttonTextCell1.setContentText(whsContent);
                buttonTextCell2.setContentText(String.valueOf(whs.size()));
                whs.removeAll(whs);
                show.dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whs.removeAll(whs);
                show.dismiss();
            }
        });
    }

    private void initItemListView(String search, final ListView listView) {
        ResultSet rs = null;
        Connection conn = null;
        CallableStatement stmt;
        final DataTable tb = new DataTable();
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.21:1609:PROD1", "apps", "apps");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String sql = "{call pkg_getrecord.p_rtn_bomdtl_ref_wh(?,?,?,?)}";
            stmt = conn.prepareCall(sql);
            stmt.setObject(1, item_id);
            stmt.setObject(2, org_id);
            //多层 用10   单层 用1
            if (radioButtonChecked) {      //多层
                stmt.setObject(3, 10);
            } else {
                stmt.setObject(3, 1);
            }
//            stmt.setObject(3, 1);
//            stmt.setObject(3, 10);
            stmt.registerOutParameter(4, OracleTypes.CURSOR);
            stmt.execute();
            rs = ((OracleCallableStatement) stmt).getCursor(4);
            DataRow rw;
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                rw = new DataRow();
                rw.setValue("REF_WH", rs.getString("REF_WH"));
                if (rs.getString("REF_WH").contains(search)) {
                     tb.Rows.add(rw);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            App.Current.showInfo(LocationCheckSMTActivity.this, e.getMessage());
        }

        itemListViewAdapter = new ItemListViewAdapter(tb);
        listView.setAdapter(itemListViewAdapter);
        checkedItem = new ArrayList<Integer>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (checkedItem.contains(i)) {
                    checkedItem.remove((Integer) i);
                } else {
                    checkedItem.add(i);
                }
                String refWh = tb.Rows.get(i).getValue("REF_WH", "");
                if (whs.contains(refWh)) {
                    whs.remove(refWh);
                } else {
                    whs.add(refWh);
                }
                itemListViewAdapter.notifyDataSetChanged();
            }
        });
    }

//    public void loadItem(long p_item_id, int p_org_id, String search_data, ListView listView) {
//        Log.e("len", p_item_id + "***" + p_org_id + "@Q@" + search_data);
//        ResultSet rs = null;
//        Connection conn = null;
//        CallableStatement stmt;
//        DataTable tb = new DataTable();
//        try {
//            Class.forName("oracle.jdbc.driver.OracleDriver");
//            conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.21:1609:PROD1", "apps", "apps");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            String sql = "{call pkg_getrecord.p_rtn_bomdtl_ref_wh(?,?,?,?)}";
//            stmt = conn.prepareCall(sql);
//            stmt.setObject(1, p_item_id);
//            stmt.setObject(2, p_org_id);
//            //多层 用10   单层 用1
//            if (radioButtonChecked) {      //多层
//                stmt.setObject(3, 10);
//            } else {
//                stmt.setObject(3, 1);
//            }
////            stmt.setObject(3, 1);
////            stmt.setObject(3, 10);
//            stmt.registerOutParameter(4, OracleTypes.CURSOR);
//            stmt.execute();
//            rs = ((OracleCallableStatement) stmt).getCursor(4);
//            DataRow rw;
//            ResultSetMetaData metaData = rs.getMetaData();
//            int columnCount = metaData.getColumnCount();
//            Log.e("len", "COLUMNCOUNT:" + columnCount);
//            while (rs.next()) {
//                Log.e("len", "rs:" + rs.getString("REF_WH"));
//                rw = new DataRow();
//                rw.setValue("REF_WH", rs.getString("REF_WH"));
//                if (rs.getString("REF_WH").contains(search_data)) {
//                    Log.e("len", "RW:" + rw.getValue("REF_WH", ""));
//                    tb.Rows.add(rw);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            App.Current.showInfo(LocationCheckActivity.this, e.getMessage());
//        }
//
//        Log.e("len", "111111111111111");
//        for (int i = 0; i < tb.Rows.size(); i++) {
//            Log.e("len", "tbtbtbTBTB:" + tb.Rows.get(i).getValue("REF_WH", ""));
//        }
//        itemListViewAdapter = new ItemListViewAdapter(tb);
//        listView.setAdapter(itemListViewAdapter);
//    }

    class LocationListViewAdapter2 extends BaseAdapter {
        private DataTable dataTable;
        private double time12;
        private double time13;

        public LocationListViewAdapter2(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        @Override
        public int getCount() {
            return leftStrings2.length;
        }

        @Override
        public Object getItem(int i) {
            return leftStrings2[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LocationListViewViewHolder locationListViewViewHolder;
            if (view == null) {
                view = View.inflate(LocationCheckSMTActivity.this, R.layout.location_smt_listview_item, null);
                locationListViewViewHolder = new LocationListViewViewHolder();
                locationListViewViewHolder.textView1 = (TextView) view.findViewById(R.id.textview_1);
                locationListViewViewHolder.textView2 = (TextView) view.findViewById(R.id.textview_2);
                locationListViewViewHolder.textView3 = (TextView) view.findViewById(R.id.textview_3);
                locationListViewViewHolder.textView4 = (TextView) view.findViewById(R.id.textview_4);
                locationListViewViewHolder.textView5 = (TextView) view.findViewById(R.id.textview_5);
                locationListViewViewHolder.textView6 = (TextView) view.findViewById(R.id.textview_6);
                locationListViewViewHolder.textView7 = (TextView) view.findViewById(R.id.textview_7);
                locationListViewViewHolder.textView8 = (TextView) view.findViewById(R.id.textview_8);
                locationListViewViewHolder.textView9 = (TextView) view.findViewById(R.id.textview_9);
                locationListViewViewHolder.textView10 = (TextView) view.findViewById(R.id.textview_10);
                locationListViewViewHolder.textView11 = (TextView) view.findViewById(R.id.textview_11);
                locationListViewViewHolder.textView12 = (TextView) view.findViewById(R.id.textview_12);
                locationListViewViewHolder.textView13 = (TextView) view.findViewById(R.id.textview_13);
                view.setTag(locationListViewViewHolder);
            } else {
                locationListViewViewHolder = (LocationListViewViewHolder) view.getTag();
            }

            if (i < dataTable.Rows.size()) {
                double time1 = dataTable.Rows.get(i).getValue(headerStrings[1], 0);
                double time2 = dataTable.Rows.get(i).getValue(headerStrings[2], 0);
                double time3 = dataTable.Rows.get(i).getValue(headerStrings[3], 0);
                double time4 = dataTable.Rows.get(i).getValue(headerStrings[4], 0);
                double time5 = dataTable.Rows.get(i).getValue(headerStrings[5], 0);
                double time6 = dataTable.Rows.get(i).getValue(headerStrings[6], 0);
                double time7 = dataTable.Rows.get(i).getValue(headerStrings[7], 0);
                double time8 = dataTable.Rows.get(i).getValue(headerStrings[8], 0);
                double time9 = dataTable.Rows.get(i).getValue(headerStrings[9], 0);
                double time10 = dataTable.Rows.get(i).getValue(headerStrings[10], 0);
                double time11 = dataTable.Rows.get(i).getValue(headerStrings[11], 0);
                if (i == 0) {
                    time12 = time1 + time2 + time3 + time4 + time5 + time6 + time7 + time8 + time9 + time10;
                } else if (i == 1) {
                    time13 = time1 + time2 + time3 + time4 + time5 + time6 + time7 + time8 + time9 + time10;
                }
                if (i == 2) {
                    locationListViewViewHolder.textView2.setText(String.valueOf(time1) + "%");
                    locationListViewViewHolder.textView3.setText(String.valueOf(time2) + "%");
                    locationListViewViewHolder.textView4.setText(String.valueOf(time3) + "%");
                    locationListViewViewHolder.textView5.setText(String.valueOf(time4) + "%");
                    locationListViewViewHolder.textView6.setText(String.valueOf(time5) + "%");
                    locationListViewViewHolder.textView7.setText(String.valueOf(time6) + "%");
                    locationListViewViewHolder.textView8.setText(String.valueOf(time7) + "%");
                    locationListViewViewHolder.textView9.setText(String.valueOf(time8) + "%");
                    locationListViewViewHolder.textView10.setText(String.valueOf(time9) + "%");
                    locationListViewViewHolder.textView11.setText(String.valueOf(time10) + "%");
               
                    if (time11 != 0) {
                        locationListViewViewHolder.textView12.setText(String.valueOf(new DecimalFormat("0.00").format(time12 / time11)) + "%");
                    } else {
                        locationListViewViewHolder.textView12.setText("0%");
                    }
                } else {
                    locationListViewViewHolder.textView2.setText(removeSmallNumber(String.valueOf(time1)));
                    locationListViewViewHolder.textView3.setText(removeSmallNumber(String.valueOf(time2)));
                    locationListViewViewHolder.textView4.setText(removeSmallNumber(String.valueOf(time3)));
                    locationListViewViewHolder.textView5.setText(removeSmallNumber(String.valueOf(time4)));
                    locationListViewViewHolder.textView6.setText(removeSmallNumber(String.valueOf(time5)));
                    locationListViewViewHolder.textView7.setText(removeSmallNumber(String.valueOf(time6)));
                    locationListViewViewHolder.textView8.setText(removeSmallNumber(String.valueOf(time7)));
                    locationListViewViewHolder.textView9.setText(removeSmallNumber(String.valueOf(time8)));
                    locationListViewViewHolder.textView10.setText(removeSmallNumber(String.valueOf(time9)));
                    locationListViewViewHolder.textView11.setText(removeSmallNumber(String.valueOf(time10)));
                    locationListViewViewHolder.textView12.setText(removeSmallNumber(String.valueOf(time11)));
                    if (i == 0) {
                        locationListViewViewHolder.textView13.setText(removeSmallNumber(String.valueOf(time12 + "")));
                    } else if (i == 1) {
                        locationListViewViewHolder.textView13.setText(removeSmallNumber(String.valueOf(time13 + "")));
                    }
                }
            }
            locationListViewViewHolder.textView1.setText(leftStrings2[i]);
            return view;
        }
    }

    class LocationListViewViewHolder {
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private TextView textView4;
        private TextView textView5;
        private TextView textView6;
        private TextView textView7;
        private TextView textView8;
        private TextView textView9;
        private TextView textView10;
        private TextView textView11;
        private TextView textView12;
        private TextView textView13;
    }

    private class ItemListViewAdapter extends BaseAdapter {
        private DataTable mDataTable;

        public ItemListViewAdapter(DataTable value) {
            mDataTable = value;
        }

        @Override
        public int getCount() {
            return mDataTable.Rows.size();
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final ItemListViewViewHolder itemListViewViewHolder;
            ArrayList<String> textString = null;
            if (textString == null) {
                textString = new ArrayList<String>();
            }
            if (view == null) {
                view = View.inflate(LocationCheckSMTActivity.this, R.layout.item_listview_location, null);
                itemListViewViewHolder = new ItemListViewViewHolder();
                itemListViewViewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.item_linearlayout);
                itemListViewViewHolder.textView = (TextView) view.findViewById(R.id.item_textview);
                view.setTag(itemListViewViewHolder);
            } else {
                itemListViewViewHolder = (ItemListViewViewHolder) view.getTag();
            }
            itemListViewViewHolder.textView.setText(mDataTable.Rows.get(i).getValue("REF_WH", ""));
            if (checkedItem.contains(i)) {
                itemListViewViewHolder.textView.setSelected(true);
            } else {
                itemListViewViewHolder.textView.setSelected(false);
            }
            textString.add(itemListViewViewHolder.textView.getText().toString());
            return view;
        }
    }

    class ItemListViewViewHolder {
        private LinearLayout linearLayout;
        private TextView textView;
    }
}
