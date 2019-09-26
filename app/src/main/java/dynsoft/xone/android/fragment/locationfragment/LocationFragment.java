package dynsoft.xone.android.fragment.locationfragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dynsoft.xone.android.adapter.GridviewAdapter;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.sopactivity.LocationCheckActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2018/11/26.
 */

public class LocationFragment extends Fragment {
    private String[] titles = {"生产线:", "工序名称:", "产品型号:", "定单号:",};
    private String[] headerStrings = {"不良现象\\时间", "8:00-9:00", "9:00-10:00", "10:00-12:00", "12:30-14:00"
            , "14:00-15:00", "15:00-16:00", "16:00-17:20", "18:00-19:00", "19:00-20:00", "20:00-", "合计"};
    //    private String[] leftStrings = {"虚焊", "短路", "少件", "多件", "插错", "插反", "翘铜皮",
//            "浮高", "移位", "脚短", "破件", "漏打胶", "溢胶", "不出脚", "针孔", "包焊", "拉尖",
//            "剪脚未加锡", "锡裂", "板脏"};
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
    private ListView listView1;
    private SharedPreferences sharedPreferences;
    private ArrayList<String> content;
    private String segment;
    private String production;
    private String task_order_code;
    private BarChart barChart;
    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initListView1();
            handler.postDelayed(runnable, 10 * 60 * 1000);
        }
    };//柱状图的sql
    private String sql_barchart;
    private Parameters p;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.activity_location_check, null);
        sharedPreferences = getActivity().getSharedPreferences("sop", MODE_PRIVATE);
        content = new ArrayList<String>();
        segment = sharedPreferences.getString("segment", "");
        content.add(segment);
        production = sharedPreferences.getString("production", "");
        String item_name = sharedPreferences.getString("item_name", "");
        task_order_code = sharedPreferences.getString("task_order", "");
        String plan_quantity = sharedPreferences.getString("plan_quantity", "");
        content.add(production);
        content.add(item_name);
        content.add(task_order_code);

        textView1 = (TextView) view.findViewById(R.id.textview_1);
        textView2 = (TextView) view.findViewById(R.id.textview_2);
        textView3 = (TextView) view.findViewById(R.id.textview_3);
        textView4 = (TextView) view.findViewById(R.id.textview_4);
        textView5 = (TextView) view.findViewById(R.id.textview_5);
        textView6 = (TextView) view.findViewById(R.id.textview_6);
        textView7 = (TextView) view.findViewById(R.id.textview_7);
        textView8 = (TextView) view.findViewById(R.id.textview_8);
        textView9 = (TextView) view.findViewById(R.id.textview_9);
        textView10 = (TextView) view.findViewById(R.id.textview_10);
        textView11 = (TextView) view.findViewById(R.id.textview_11);
        textView12 = (TextView) view.findViewById(R.id.textview_12);
        gridView = (GridView) view.findViewById(R.id.gridview_location);
        barChart = (BarChart) view.findViewById(R.id.barchart);
        listView1 = (ListView) view.findViewById(R.id.listview_location_1);
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
        initGridView();
        handler = new Handler();
        handler.post(runnable);
        return view;
    }

    private void initGridView() {
        GridviewAdapter gridviewAdapter = new GridviewAdapter(titles, content, getActivity());
        gridView.setAdapter(gridviewAdapter);
    }

    private void initListView1() {
        final long task_order_id = sharedPreferences.getLong("task_order_id", 0L);
        final int seq_id = sharedPreferences.getInt("seq_id", 0);
        final String cur_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));

        final String sql_worktype = "exec p_qm_sop_get_scan_number ?,?";
        String[] split = production.split(",");
        Parameters p_worktype = new Parameters().add(1, task_order_code).add(2, split[0]);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql_worktype, p_worktype, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getActivity(), value.Error);
                } else {
                    if (value.Value != null) {
                        String work_type = value.Value.getValue("work_type", "");
                        String sql_datas;
                        Log.e("len", "WORK_TYPE:" + work_type);
                        if (work_type.equals("inspect")) {           //测试
                            sql_datas = "exec p_fm_get_fm_work_electrical_items ?,?,?";
                            sql_barchart = "exec p_fm_get_fm_work_electrical_items_barchart ?,?,?";
                        } else {
                            sql_datas = "exec p_fm_get_fm_work_repair_items_report ?,?,?";
                            sql_barchart = "exec p_fm_get_fm_work_repair_items_barchart ?,?,?";
                        }
                        p = new Parameters().add(1, task_order_id).add(2, seq_id).add(3, cur_date);
                        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql_datas, p, new ResultHandler<DataTable>() {
                            @Override
                            public void handleMessage(Message msg) {
                                final Result<DataTable> value = Value;
                                if (value.HasError) {
                                    App.Current.toastError(getActivity(), value.Error);
                                    return;
                                }
                                if (value.Value != null && value.Value.Rows.size() > 0) {
                                    LocationListViewAdapter1 locationListViewAdapter1 = new LocationListViewAdapter1(value.Value);
                                    listView1.setAdapter(locationListViewAdapter1);
                                    //加载柱状图
                                    App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql_barchart, p, new ResultHandler<DataTable>() {
                                        @Override
                                        public void handleMessage(Message msg) {
                                            Result<DataTable> chart_value = Value;
                                            if(chart_value.HasError) {

                                            } else {
                                                if(chart_value.Value != null && chart_value.Value.Rows.size() > 0) {
                                                    setChartDatas(chart_value.Value);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });

//        String sql = "exec p_fm_get_fm_work_repair_items_report ?,?,?";
////            Parameters p = new Parameters().add(1, task_order_code).add(2, seqName);55667,224,'2018-11-16'
//        Parameters p = new Parameters().add(1, task_order_id).add(2, seq_id).add(3, cur_date);
//        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
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
    }

    private void setChartDatas(DataTable value) {
        ArrayList<String> xValue = new ArrayList<String>();
        ArrayList<BarEntry> yValue = new ArrayList<BarEntry>();
        for (int i = 0; i < value.Rows.size(); i++) {
            int counts = value.Rows.get(i).getValue("counts", 0);
            String fail_type = value.Rows.get(i).getValue("fail_type", "");
            xValue.add(fail_type);
            yValue.add(new BarEntry((float) counts, i));
        }

        BarDataSet barDataSet = new BarDataSet(yValue, "定位报表");
        barDataSet.setColor(Color.BLUE);//设置第一组数据颜色
        BarData bardata = new BarData(xValue, barDataSet);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return String.valueOf((int) v);
            }
        });

        bardata.setValueTextSize(20);
        barChart.setData(bardata);      //设置图表数据

        Legend legend = barChart.getLegend();
        legend.setTextSize(20);

        barChart.setDescription("");
        barChart.setDescriptionColor(Color.RED);

        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(600);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);  //设置X轴的位置
        barChart.getXAxis().setDrawGridLines(false);    //不显示网格
        barChart.getXAxis().setSpaceBetweenLabels(1);
        barChart.getXAxis().setTextSize(20);
        YAxis axisRight = barChart.getAxisRight();
        axisRight.setEnabled(false);
        barChart.animateXY(0, 0);   //设置动画
    }

//    private void initListView2() {
//        if (production.contains(",")) {
//            String[] substring = production.split(",");
//            String seqName = substring[0];
//            String sql = "exec p_fm_get_location_check_counts_v01 ?,?,?";
//            Parameters p = new Parameters().add(1, segment).add(2, seqName).add(3, task_order_code);
//            App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
//                @Override
//                public void handleMessage(Message msg) {
//                    Result<DataTable> value = Value;
//                    if (value.HasError) {
//                        App.Current.toastError(LocationCheckActivity.this, value.Error);
//                        return;
//                    }
//                    if (value.Value != null && value.Value.Rows.size() > 0) {
//                        Log.e("len", "LIST2:" + value.Value.Rows.size());
//                        LocationListViewAdapter2 locationListViewAdapter2 = new LocationListViewAdapter2(value.Value);
//                        listView2.setAdapter(locationListViewAdapter2);
//                    }
//                }
//            });
//        } else {
//
//        }
//
//    }

    private String removeSmallNumber(String plan_quantity) {
        if (plan_quantity.indexOf(".") > 0) {
            plan_quantity = plan_quantity.replaceAll("0+?$", "");//去掉多余的0
            plan_quantity = plan_quantity.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return plan_quantity;
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
                view = View.inflate(getActivity(), R.layout.location_listview_item, null);
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
                view.setTag(locationListViewViewHolder);
            } else {
                locationListViewViewHolder = (LocationListViewViewHolder) view.getTag();
            }
            final String type = dataTable.Rows.get(i).getValue("fail_type", "");
            String time1 = dataTable.Rows.get(i).getValue(headerStrings[1], "");
            String time2 = dataTable.Rows.get(i).getValue(headerStrings[2], "");
            String time3 = dataTable.Rows.get(i).getValue(headerStrings[3], "");
            String time4 = dataTable.Rows.get(i).getValue(headerStrings[4], "");
            String time5 = dataTable.Rows.get(i).getValue(headerStrings[5], "");
            String time6 = dataTable.Rows.get(i).getValue(headerStrings[6], "");
            String time7 = dataTable.Rows.get(i).getValue(headerStrings[7], "");
            String time8 = dataTable.Rows.get(i).getValue(headerStrings[8], "");
            String time9 = dataTable.Rows.get(i).getValue(headerStrings[9], "");
            String time10 = dataTable.Rows.get(i).getValue(headerStrings[10], "");
            String time11 = dataTable.Rows.get(i).getValue("count", "");

            locationListViewViewHolder.textView1.setText(type);
            locationListViewViewHolder.textView2.setText(time1);
            locationListViewViewHolder.textView3.setText(time2);
            locationListViewViewHolder.textView4.setText(time3);
            locationListViewViewHolder.textView5.setText(time4);
            locationListViewViewHolder.textView6.setText(time5);
            locationListViewViewHolder.textView7.setText(time6);
            locationListViewViewHolder.textView8.setText(time7);
            locationListViewViewHolder.textView9.setText(time8);
            locationListViewViewHolder.textView10.setText(time9);
            locationListViewViewHolder.textView11.setText(time10);
            locationListViewViewHolder.textView12.setText(String.valueOf(time11));
            return view;
        }
    }

//    private void ToastPopupWindow(final String leftString, final String headerString, final TextView textView) {
//        final PopupWindow popupWindow = new PopupWindow();
//        View view = View.inflate(LocationCheckActivity.this, R.layout.item_location_check_popupwindow, null);
//        final ButtonTextCell buttonTextCell1 = (ButtonTextCell) view.findViewById(R.id.button_text_cell_1);
//        final ButtonTextCell buttonTextCell2 = (ButtonTextCell) view.findViewById(R.id.button_text_cell_2);
//        final ButtonTextCell buttonTextCell3 = (ButtonTextCell) view.findViewById(R.id.button_text_cell_3);
//
//        if (buttonTextCell1 != null) {
//            buttonTextCell1.setLabelText("位号");
//            buttonTextCell1.setContentText("");
//            buttonTextCell1.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_downward_light"));
//            buttonTextCell1.Button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    whs = new ArrayList<String>();
//                    dialogChooceWh(buttonTextCell1, buttonTextCell2);
//                }
//            });
//        }
//
//        if (buttonTextCell2 != null) {
//            buttonTextCell2.setLabelText("数量");
//            buttonTextCell2.setContentText("");
//            buttonTextCell2.Button.setVisibility(View.GONE);
//        }
//
//        if (buttonTextCell3 != null) {
//            buttonTextCell3.setLabelText("产品条码");
//            buttonTextCell3.setContentText("");
//            buttonTextCell3.Button.setVisibility(View.GONE);
//        }
//        TextView buttonConfirm = (TextView) view.findViewById(R.id.confirm);
//        final TextView buttonCancel = (TextView) view.findViewById(R.id.cancel);
//
//        buttonConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (TextUtils.isEmpty(buttonTextCell1.getContentText())) {
//                    App.Current.toastError(LocationCheckActivity.this, "请选择位号！");
//                } else if (TextUtils.isEmpty(buttonTextCell2.getContentText())) {
//                    App.Current.toastError(LocationCheckActivity.this, "请输入数量！");
//                } else if (production.contains(",")) {
//                    String[] substring = production.split(",");
//                    String seqName = substring[0];
//                    String sql = "exec p_fm_location_check_commit ?,?,?,?,?,?,?,?,?";
//                    Parameters p = new Parameters().add(1, task_order_code).add(2, seqName).add(3, segment)
//                            .add(4, leftString).add(5, buttonTextCell2.getContentText()).add(6, buttonTextCell1.getContentText())
//                            .add(7, App.Current.UserID).add(8, headerString).add(9, buttonTextCell3.getContentText());
//                    Log.e("len", task_order_code + "**" + seqName + "**" + segment + "**" + leftString + headerString);
//                    App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
//                        @Override
//                        public void handleMessage(Message msg) {
//                            Result<Integer> value = Value;
//                            if (value.HasError) {
//                                App.Current.toastError(LocationCheckActivity.this, value.Error);
//                                popupWindow.dismiss();
//                                return;
//                            }
//                            if (value.Value != null) {
//                                App.Current.toastInfo(LocationCheckActivity.this, "提交成功");
//                                App.Current.playSound(R.raw.pass);
//                                initListView1();
//                                popupWindow.dismiss();
//                            }
//                        }
//                    });
//                } else {
//                    App.Current.toastInfo(LocationCheckActivity.this, "提交失败，请查看工序！");
//                    App.Current.playSound(R.raw.error);
//                    popupWindow.dismiss();
//                }
//            }
//        });
//        buttonCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                popupWindow.dismiss();
//            }
//        });
//        popupWindow.setContentView(view);
//        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setFocusable(true);
////                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.style_divider_color));
//        popupWindow.showAtLocation(gridView, Gravity.CENTER, 20, 30);
//    }

//    private void dialogChooceWh(final ButtonTextCell buttonTextCell1, final ButtonTextCell buttonTextCell2) {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(LocationCheckActivity.this);
//        View view = View.inflate(LocationCheckActivity.this, R.layout.location_choose_wh_view, null);
//        final ButtonTextCell buttonTextCell = (ButtonTextCell) view.findViewById(R.id.item_buttontextcell);
//        final ListView listView = (ListView) view.findViewById(R.id.item_listview);
//        initItemListView(buttonTextCell.getContentText(), listView);
//        TextView buttonConfirm = (TextView) view.findViewById(R.id.confirm);
//        TextView buttonCancel = (TextView) view.findViewById(R.id.cancel);
//        builder.setView(view);
//        final AlertDialog show = builder.show();
//
//        if (buttonTextCell != null) {
//            buttonTextCell.setLabelText("");
//            buttonTextCell.TextBox.setHint("请输入位号查询");
//            buttonTextCell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_gray"));
//            buttonTextCell.Button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    //根据输入搜索 buttonTextCell.getContentText()
//                    //搜索到位号 把数据给ListView
//                    initItemListView(buttonTextCell.getContentText(), listView);
//
//                }
//            });
//        }
//
//        buttonConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("len", "WHS:" + whs.toString());
//                String whsContent = whs.toString().substring(1, whs.toString().length() - 1);
//                buttonTextCell1.setContentText(whsContent);
//                buttonTextCell2.setContentText(String.valueOf(whs.size()));
//                whs.removeAll(whs);
//                show.dismiss();
//            }
//        });
//        buttonCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                whs.removeAll(whs);
//                show.dismiss();
//            }
//        });
//    }

//    private void initItemListView(String search, final ListView listView) {
//        ResultSet rs = null;
//        Connection conn = null;
//        CallableStatement stmt;
//        final DataTable tb = new DataTable();
//        try {
//            Class.forName("oracle.jdbc.driver.OracleDriver");
//            conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.21:1609:PROD1", "apps", "apps");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            String sql = "{call pkg_getrecord.p_rtn_bomdtl_ref_wh(?,?,?,?)}";
//            stmt = conn.prepareCall(sql);
//            stmt.setObject(1, item_id);
//            stmt.setObject(2, org_id);
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
//            while (rs.next()) {
//                rw = new DataRow();
//                rw.setValue("REF_WH", rs.getString("REF_WH"));
//                if (rs.getString("REF_WH").contains(search)) {
//                     tb.Rows.add(rw);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            App.Current.showInfo(LocationCheckActivity.this, e.getMessage());
//        }
//
//        itemListViewAdapter = new ItemListViewAdapter(tb);
//        listView.setAdapter(itemListViewAdapter);
//        checkedItem = new ArrayList<Integer>();
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (checkedItem.contains(i)) {
//                    checkedItem.remove((Integer) i);
//                } else {
//                    checkedItem.add(i);
//                }
//                String refWh = tb.Rows.get(i).getValue("REF_WH", "");
//                if (whs.contains(refWh)) {
//                    whs.remove(refWh);
//                } else {
//                    whs.add(refWh);
//                }
//                itemListViewAdapter.notifyDataSetChanged();
//            }
//        });
//    }

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
    }
}
