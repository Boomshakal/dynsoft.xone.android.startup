package dynsoft.xone.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.dom4j.util.XMLErrorHandler;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.adapter.MyListTaskOrderCodeAdapter;
import dynsoft.xone.android.bean.PreItemBean;
import dynsoft.xone.android.blueprint.PreItemActivity;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.PromptCallback;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;

public class PreOnline2Activity extends Activity implements View.OnKeyListener, View.OnClickListener {
    private Button buttonStart;
    private Button buttonRefresh;
    private Button buttonPause;
    private Button buttonResume;
    private ListView listView;
    private EditText editText;
    private ImageView imageSop;
    private String selectedItem = "";   // ListView选中的列
    private String selectedTaskOrder = "";   // ListView选中的工单
    private String edittextdata;
    private StringBuffer stringSelectedItems;     //选中的物料
    private StringBuffer stringSelectedWhs;     //选中的位号
    private String stringSelectedTaskOrderCode;     //选中的工单
    private ArrayList<String> selectedItems;        //选中的物料ID
    private ArrayList<String> selectedWhs;        //选中的位号
    private ArrayList<String> selectedListItems;   //选中的list的顺序
    private ArrayList<String> selectedProcess;     //选中的加工方式
    private StringBuffer stringSelectedProcess;     //选中的方式
    private DecimalFormat df;
    private TextView textview_online_count;
    private TextView textview_time_count;
    private PreOnlineAdater adapter;
    private DataTable dataTable;
    private long worker_sign_id;        //Sign的ID
    private String taskOrderCode = "";       //工单名称，用来检索
    private String itemCode = "";       //物料名称，用来检索
    private String itemWh = "";       //位号名称，用来检索
    private String processStation = "";       //站位信息，用来检索
    private MyListAdapter myListAdapter;
    private MyListStationAdapter myListStationAdapter;
    private MyWhListAdapter myWhListAdapter;
    private MyListTaskOrderCodeAdapter myListTaskOrderCodeAdapter;
    private DataTable items;
    private ArrayList<PreItemBean> itemIDs;
    private int item_count;
    private String preLotNumber;
    private String qjgIdent;
    private String lot_ids;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preonline);
        buttonStart = (Button) findViewById(R.id.button_start);
        buttonRefresh = (Button) findViewById(R.id.button_refresh);
        buttonPause = (Button) findViewById(R.id.button_pause);
        buttonResume = (Button) findViewById(R.id.button_resume);
        listView = (ListView) findViewById(R.id.listview);
        editText = (EditText) findViewById(R.id.edittext);
        imageSop = (ImageView) findViewById(R.id.image_sop);
        textview_online_count = (TextView) findViewById(R.id.textview_online_count);
        textview_time_count = (TextView) findViewById(R.id.textview_time_count);
        df = new DecimalFormat("######0.00");
        buttonStart.setOnClickListener(this);
        buttonRefresh.setOnClickListener(this);
        buttonPause.setOnClickListener(this);
        buttonResume.setOnClickListener(this);
        editText.setOnKeyListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view.findViewById(R.id.textview_worker);
                TextView textViewTaskOrder = (TextView) view.findViewById(R.id.textview_message);
                selectedItem = textView.getText().toString().split("-")[0].trim();
                selectedTaskOrder = textViewTaskOrder.getText().toString().split(";")[0].trim();
                worker_sign_id = dataTable.Rows.get(i).getValue("id", 0L);
                adapter.changeChoose(i);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                App.Current.prompt(PreOnline2Activity.this, "请输入", "请输入数量", new PromptCallback() {
                    @Override
                    public void onReturn(String result) {
                        worker_sign_id = dataTable.Rows.get(i).getValue("id", 0L);
                        String task_order_code = dataTable.Rows.get(i).getValue("task_order_code", "");
                        int plan_quantity = dataTable.Rows.get(i).getValue("plan_quantity", new BigDecimal(0)).intValue();

                        String sql = "exec get_label_message ?";
                        Parameters p = new Parameters().add(1, worker_sign_id);
                        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
                            @Override
                            public void handleMessage(Message msg) {
                                Result<DataTable> value = Value;
                                if (value.HasError) {
                                    App.Current.toastError(PreOnline2Activity.this, value.Error);
                                } else {
                                    if (value.Value.Rows != null && value.Value.Rows.size() > 0) {
                                        ArrayList<String> itemMessages = new ArrayList<>();
                                        for (int j = 0; j < value.Value.Rows.size(); j++) {
                                            String item_code = value.Value.Rows.get(j).getValue("item_code", "");
                                            String date_code = value.Value.Rows.get(j).getValue("date_code", "");
                                            int quantity = value.Value.Rows.get(j).getValue("quantity", new BigDecimal(0)).intValue();
                                            BigDecimal quantity_per_assembly = value.Value.Rows.get(j).getValue("quantity_per_assembly", new BigDecimal(0));
                                            itemMessages.add(item_code + " " + date_code + "    " + (Integer.valueOf(result) * quantity_per_assembly.intValue()));
                                        }
                                        Calendar calendar = Calendar.getInstance();
                                        int year = calendar.get(Calendar.YEAR);
                                        int weekYear = calendar.get(Calendar.WEEK_OF_YEAR);
                                        String date_code = "";
                                        String type = value.Value.Rows.get(0).getValue("type", "");
                                        if ("SINGLE".equals(type)) {
                                            date_code = value.Value.Rows.get(0).getValue("date_code", "");
                                        } else {
                                            date_code = String.valueOf(year).substring(2) + String.valueOf(weekYear);
                                        }
                                        String pre_code = value.Value.Rows.get(0).getValue("pre_code", "");
                                        String lot_number = value.Value.Rows.get(0).getValue("lot_number", "");
                                        Intent intent = new Intent(PreOnline2Activity.this, PreItemActivity.class);
                                        intent.putExtra("task_order_code", task_order_code);
                                        intent.putExtra("plan_quantity", plan_quantity + "");
                                        intent.putExtra("cur_quantity", result);
                                        intent.putExtra("lot_number", lot_number);
                                        intent.putExtra("date_code", date_code);
                                        intent.putExtra("pre_item_code", pre_code);
                                        Log.e("len", pre_code);
                                        intent.putStringArrayListExtra("itemMessages", itemMessages);
                                        startActivity(intent);
                                    } else {
                                        App.Current.toastError(PreOnline2Activity.this, "没有数据");
                                    }
                                }
                            }
                        });
                    }
                });

                return false;
            }
        });
        imageSop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(selectedTaskOrder)) {
                    App.Current.toastError(PreOnline2Activity.this, "请先选择需要查看的SOP的列");
                } else {
                    Intent intent = new Intent(PreOnline2Activity.this, PreSopActivity.class);
                    intent.putExtra("task_order_code", selectedTaskOrder);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Edittext的key事件，通过判断回车键获取打卡人
     *
     * @param view     edittext的对象
     * @param keyCode  键入的按键编码
     * @param keyEvent 按键事件
     * @return
     */
    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            itemCode = "";     //每次刷卡的时候，把物料索引删除
            itemWh = "";     //每次刷卡的时候，把物料索引删除
            processStation = "";     //每次刷卡的时候，把加工站别索引删除
            edittextdata = editText.getText().toString().replace("\n", "").toUpperCase();
            editText.setText("");
//                    edittextdata = edittext.getText().toString();
            if (edittextdata.startsWith("M") && edittextdata.length() < 10) {
                chooseTaskOrderCode(edittextdata.replace("\n", ""));
//                chooseItemCode(edittextdata.replace("\n", ""));
            }
            return true;
        }
        return false;
    }

//    @Override
//    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//    }
//
//    @Override
//    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//    }
//
//    @Override
//    public void afterTextChanged(Editable editable) {
//        if (editable.length() == 10) {
//            String s = editable.toString();
////                    String replace = s.startsWith("0") ? s.substring(1) : s;
//            checkCard(s);
//        }
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start:
                new AlertDialog.Builder(PreOnline2Activity.this)
                        .setTitle("询问")
                        .setMessage("确定要将所有人员启动吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startAllUser();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.button_refresh:
                refreshWorker();
                break;
            case R.id.button_pause:
                if (TextUtils.isEmpty(selectedItem)) {        //选择了人
                    new AlertDialog.Builder(PreOnline2Activity.this)
                            .setTitle("询问")
                            .setMessage("确定要将所有人员暂停吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    pauseAllUser();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                } else {
                    new AlertDialog.Builder(PreOnline2Activity.this)
                            .setTitle("询问")
                            .setMessage("确定要将" + selectedItem + "暂停吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    pauseSelectedUser();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
                break;
            case R.id.button_resume:
                if (TextUtils.isEmpty(selectedItem)) {        //选择了人
                    new AlertDialog.Builder(PreOnline2Activity.this)
                            .setTitle("询问")
                            .setMessage("确定要将所有人员恢复吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    resumeAllUser();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                } else {
                    new AlertDialog.Builder(PreOnline2Activity.this)
                            .setTitle("询问")
                            .setMessage("确定要将" + selectedItem + "恢复吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    resumeSelectedUser();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
                break;
        }
    }

    /**
     * 启动所有人
     * 通过遍历当前页面打卡人员，进行启动
     */
    private void startAllUser() {
//        ArrayList<Map<String, String>> messages = new ArrayList<>();
//        for (int i = 0; i < listView.getCount(); i++) {
//            Map<String, String> message = new HashMap<>();
//            TextView textViewMessage = (TextView) listView.getChildAt(i).findViewById(R.id.textview_message);
//            TextView textViewUser = (TextView) listView.getChildAt(i).findViewById(R.id.textview_worker);
//            String taskOrderCode = textViewMessage.getText().toString().trim();
//            String userName = textViewUser.getText().toString().trim();
//            message.put("task_order_code", taskOrderCode);
//            message.put("user_name", userName);
//            messages.add(message);
//        }
//        String xml = XmlHelper.createXml("pre_online_head", null, "pre_online_items", "pre_online_item", messages);
//        Log.e("len", "XML:" + xml);
        String sql = "exec fm_sign_start_all_pre_max_work_time ?,?,?";
        Parameters p = new Parameters().add(1, App.Current.getMacAddress()).add(2, "前加工").add(3, 15);
        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                Result<Integer> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PreOnline2Activity.this, "Start" + value.Error);
                } else {
                    refreshWorker();
                }
            }
        });
    }

    private void pauseSelectedUser() {
        String sql = "exec p_fm_work_sign_pre_stop_and ?";
        Parameters p = new Parameters().add(1, worker_sign_id);
        refreshAfterSql(sql, p);
    }

    private void pauseAllUser() {
        String sql = "exec fm_sign_pause_all_pre_mac_work_time ?,?,?";
        Parameters p = new Parameters().add(1, App.Current.getMacAddress()).add(2, "前加工").add(3, App.Current.UserID);
        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                Result<Integer> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PreOnline2Activity.this, "Pause" + value.Error);
                } else {
                    refreshWorker();
                }
            }
        });
    }

    private void resumeSelectedUser() {
        String sql = "exec p_fm_work_sign_resume_and ?";
        Parameters p = new Parameters().add(1, worker_sign_id);
        refreshAfterSql(sql, p);
    }

    /**
     * 恢复所有人
     */
    private void resumeAllUser() {
        String sql = "exec fm_sign_resume_all_pre_mac_work_time ?,?,?";
        Parameters p = new Parameters().add(1, App.Current.getMacAddress()).add(2, "前加工").add(3, 15);
        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                Result<Integer> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PreOnline2Activity.this, "Resume" + value.Error);
                } else {
                    refreshWorker();
                }
            }
        });
    }

    private void refreshAfterSql(String sql, Parameters p) {
        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                Result<Integer> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PreOnline2Activity.this, value.Error);
                    return;
                }
                if (value.Value != null) {
                    refreshWorker();
                }
            }
        });
    }

    /**
     * 选择工单
     *
     * @param cardNumber 打卡的工号
     */
    private void chooseTaskOrderCode(final String cardNumber) {
        taskOrderCode = "";     //每次刷卡的时候，把工单索引删除
        itemCode = "";     //每次刷卡的时候，把物料索引删除
        itemWh = "";     //每次刷卡的时候，把位号索引删除
        processStation = "";     //每次刷卡的时候，把加工站别索引删除
        final PopupWindow popupWindow = new PopupWindow();
        View view = View.inflate(PreOnline2Activity.this, R.layout.item_preonline_popup, null);
        final EditText editText = (EditText) view.findViewById(R.id.edittext);
        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        final ListView listView = (ListView) view.findViewById(R.id.listview);
        editText.setHint("请输入工单或者料号查询");
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    String trim = editText.getText().toString().trim();
                    if (trim.contains("-") && trim.length() > 1) {     //获取扫描后的生产任务
                        taskOrderCode = trim.split("-")[0].trim() + "-" + trim.split("-")[1].trim();
                    } else {
                        taskOrderCode = trim;
                    }
                    afterTaskOrderCodeKeyListener(listView);
                    editText.setText(taskOrderCode);
                    return true;
                }
                return false;
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取已经选中的工单，隐藏popup，清空selecteditems
                if (TextUtils.isEmpty(stringSelectedTaskOrderCode)) {
                    App.Current.toastError(PreOnline2Activity.this, "请先选择生产任务！");
                } else {
                    popupWindow.dismiss();
                    //选择物料
                    chooseItemCode(cardNumber);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏popup
                popupWindow.dismiss();
            }
        });
        afterTaskOrderCodeKeyListener(listView);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        popupWindow.setWidth(width / 2);
        popupWindow.setHeight(height - (height / 5));
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(listView, Gravity.CENTER, 0, 0);
        //listview点击事件，选中与非选中
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view.findViewById(R.id.textview_1);
                String trim = textView.getText().toString().trim();
                stringSelectedTaskOrderCode = trim;
                myListTaskOrderCodeAdapter.frash(i);
            }
        });
    }

    /**
     * 选择物料
     *
     * @param cardNumber 打卡的工号
     *                   物料都是通过扫描获取，扫描了多个物料生成记录保存。下次再有这个工单，把记录也带出来。
     */
    private void chooseItemCode(final String cardNumber) {
        final PopupWindow popupWindow = new PopupWindow();
        selectedItems = new ArrayList<>();
        selectedListItems = new ArrayList<>();
        View view = View.inflate(PreOnline2Activity.this, R.layout.item_preonline_popup_item, null);
        final EditText editText = (EditText) view.findViewById(R.id.edittext);
        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        final ListView listView = (ListView) view.findViewById(R.id.listview);
        //之前加工的数据
        final ListView listViewLast = (ListView) view.findViewById(R.id.listview_last);
        editText.setHint("请输入物料编码查询");
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    String trim = editText.getText().toString().trim();
                    editText.setText("");
                    if (trim.contains("-")) {
                        qjgIdent = trim.split("-")[1];
                    }
                    if (!TextUtils.isEmpty(qjgIdent) && qjgIdent.startsWith("QJG")) {   //前加工标签
                        preLotNumber = trim.substring(4).split("-")[0];
                        popupwindowShowItems(qjgIdent, popupWindow, listView);
                    } else {
                        if (trim.startsWith("CRQ:") && trim.contains("-")) {     //获取扫描后的物料编码
                            lot_ids = trim.substring(4);
                            itemCode = trim.split("-")[1].trim();
                        } else if (trim.startsWith("R:")) {
                            itemCode = trim.substring(2);
                        } else {
                            itemCode = trim;
                        }
//                    editText.setText(itemCode);
                        editText.setText("");
                        /*扫描物料之后的处理*/
                        afterKeyListener(listView);
                    }
                    return true;
                }
                return false;
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断是否选择了物料
                if (selectedItems != null && selectedItems.size() < 1) {
                    App.Current.toastError(PreOnline2Activity.this, "请先选择加工物料！");
                } else {
                    //获取选择的ITEMS 插入表，把获取的ITEMS保存到打卡
                    ArrayList<Map<String, String>> itemsArray = new ArrayList<>();
                    for (int i = 0; i < items.Rows.size(); i++) {
                        Map<String, String> item = new HashMap<>();
                        item.put("item_code", items.Rows.get(i).getValue("item_code", ""));
                        item.put("item_id", items.Rows.get(i).getValue("item_id", 0).toString());
                        itemsArray.add(item);
                    }
                    String xml = XmlHelper.createXml("head", null, "items", "item", itemsArray);
                    String sql = "exec fm_pre_insert_table_and ?,?";
                    Parameters p = new Parameters().add(1, stringSelectedTaskOrderCode).add(2, xml);
                    App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                        @Override
                        public void handleMessage(Message msg) {
                            Result<Integer> value = Value;
                            if (value.HasError) {
                                App.Current.toastError(PreOnline2Activity.this, value.Error);
                            } else {
                                //选择位号
                                chooseWf(cardNumber);
                            }
                        }
                    });

                    //获取已经选中的物料，隐藏popup，清空selecteditems
//                    stringSelectedItems = new StringBuffer();
//                    for (int i = 0; i < selectedItems.size(); i++) {
//                        if (i == selectedItems.size() - 1) {
//                            stringSelectedItems.append(selectedItems.get(i));
//                        } else {
//                            stringSelectedItems.append(selectedItems.get(i) + ",");
//                        }
//                    }
//                    selectedItems.removeAll(selectedItems);
//                    selectedListItems.removeAll(selectedListItems);
                    popupWindow.dismiss();
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏popup，清空selecteditems
                popupWindow.dismiss();
                selectedItems.removeAll(selectedItems);
                selectedListItems.removeAll(selectedListItems);
            }
        });
//        afterKeyListener(listView);
        showTaskSelectedMessage(stringSelectedTaskOrderCode, listViewLast);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        popupWindow.setWidth(width / 2);
        popupWindow.setHeight(height - (height / 5));
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(listView, Gravity.CENTER, 0, 0);
        //listview点击事件，选中与非选中
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view.findViewById(R.id.textview_1);
                String trim = textView.getText().toString().trim();
//                if (selectedItems.contains(trim)) {
//                    selectedItems.remove(trim);
//                } else {
//                    selectedItems.add(trim);
//                }
                if (selectedListItems.contains(String.valueOf(i))) {
                    selectedListItems.remove(String.valueOf(i));
                } else {
                    selectedListItems.add(String.valueOf(i));
                }
                myListAdapter.frash(selectedListItems);
            }
        });
    }

    private void popupwindowShowItems(String qjgIdent, PopupWindow headPopupWindow, ListView itemListView) {
        itemIDs = new ArrayList<>();
        PopupWindow popupWindow = new PopupWindow();
        View view = View.inflate(PreOnline2Activity.this, R.layout.item_preonline_popup_2, null);
        EditText editText = view.findViewById(R.id.edittext);
        ListView listView = view.findViewById(R.id.listview);
        MyItemListAdapter myItemListAdapter = new MyItemListAdapter(itemIDs);
        listView.setAdapter(myItemListAdapter);
        WindowManager windowManager = getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        popupWindow.setContentView(view);
        popupWindow.setWidth(width / 2);
        popupWindow.setHeight(height / 2);
        popupWindow.setBackgroundDrawable(new ShapeDrawable());
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(textview_time_count, Gravity.CENTER, 0, 0);
        editText.setOnKeyListener((view1, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String itemCode = editText.getText().toString().replace("\n", "");
                editText.setText("");
                if (itemCode.startsWith("CRQ:")) {
                    //检查数据是否正确
                    String[] split = itemCode.substring(4).split("-");
                    if (split.length > 3) {
                        String lot_number = split[0];
                        String item_code = split[1];
                        String date_code = split[3];
                        PreItemBean preItemBean = new PreItemBean(item_code, date_code);
                        if (itemIDs != null && itemIDs.size() == 0) {
                            checkItemMessage(item_code, date_code, qjgIdent, myItemListAdapter, popupWindow, headPopupWindow);
                            itemIDs.add(preItemBean);
                        } else {
                            Log.e("len", itemIDs.size() + "***" + item_count + "*" + itemIDs.toString());
//                            if (itemIDs.size() < item_count) {  //数据加入集合
//                                itemIDs.add(preItemBean);
//                                myItemListAdapter.frash(itemIDs);
//                            } else { //提交数据
//                                itemIDs.add(preItemBean);
//                                commitDatas(qjgIdent, popupWindow, myItemListAdapter, itemListView);
//                            }
                            itemIDs.add(preItemBean);
                            myItemListAdapter.frash(itemIDs);
                        }
                        Log.e("len", itemIDs.size() + "&&" + item_count);
                        if (itemIDs.size() == item_count) {
                            commitDatas(qjgIdent, popupWindow, myItemListAdapter, itemListView);
                        }
                    } else {
                        App.Current.showError(PreOnline2Activity.this, "扫描有误，请重新扫描！");
                    }

                } else {
                    App.Current.showError(PreOnline2Activity.this, "扫描有误，请重新扫描！当前扫描的并非物料条码");
                }
            }
            return false;
        });
    }

    private void commitDatas(String qjgIdent, PopupWindow popupWindow, MyItemListAdapter myItemListAdapter, ListView itemListView) {
        ArrayList<Map<String, String>> itemMaps = new ArrayList<>();
        for (int i = 0; i < itemIDs.size(); i++) {
            Map<String, String> itemMap = new HashMap<>();
            itemMap.put("item_code", itemIDs.get(i).getItem_code());
            itemMap.put("date_code", itemIDs.get(i).getDate_code());
            itemMaps.add(itemMap);
        }
        String xml = XmlHelper.createXml("head", null, "items", "item", itemMaps);
        Log.e("len", xml);
        String sql = "exec fm_pre_datas_commit ?,?";
        Parameters p = new Parameters().add(1, qjgIdent).add(2, xml);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(PreOnline2Activity.this, value.Error);
                    itemIDs.removeAll(itemIDs);
                    myItemListAdapter.frash(itemIDs);
                } else {
                    if (value.Value.Rows != null && value.Value.Rows.size() > 0) {
                        items = new DataTable();
                        for (int i = 0; i < value.Value.Rows.size(); i++) {
                            items.Rows.add(value.Value.Rows.get(i));
                            selectedItems.add(value.Value.Rows.get(i).getValue("item_code", ""));
                        }
                        MyListAdapter myListAdapter = new MyListAdapter(items);
                        itemListView.setAdapter(myListAdapter);
//                        } else {
//                            myListAdapter.frash(selectedItems);
//                        }
                        selectedListItems.add(String.valueOf(items.Rows.size() - 1));
                        popupWindow.dismiss();
                    } else {
                        App.Current.showError(PreOnline2Activity.this, "没有返回数据");
                        itemIDs.removeAll(itemIDs);
                        myItemListAdapter.frash(itemIDs);
                    }
                }
            }
        });

    }

    private void checkItemMessage(String item_code, String date_code, String qjgIdent, MyItemListAdapter myItemListAdapter, PopupWindow popupWindow, PopupWindow headPopupWindow) {
        String sql = "exec fm_get_qjg_item_count ?,?,?,?";
        Parameters p = new Parameters().add(1, item_code).add(2, date_code).add(3, qjgIdent).add(4, stringSelectedTaskOrderCode);
        Log.e("len", item_code + "*" + date_code + "*" + qjgIdent + "*" + stringSelectedTaskOrderCode);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(PreOnline2Activity.this, value.Error);
                    itemIDs.removeAll(itemIDs);
                    popupWindow.dismiss();
                    headPopupWindow.dismiss();
                } else {
                    if (value.Value != null) {
                        item_count = value.Value.getValue("item_count", 0);
                        PreItemBean preItemBean = new PreItemBean(item_code, date_code);
                        myItemListAdapter.frash(itemIDs);
                    } else {
                        App.Current.showError(PreOnline2Activity.this, "没有查询到当前工单当前物料的信息，请检查是否扫描有误");
                    }
                }
            }
        });
    }

    /**
     * 把工单里面之前保存的数据展示
     *
     * @param stringSelectedTaskOrderCode 选择的生产任务
     */
    private void showTaskSelectedMessage(String stringSelectedTaskOrderCode, final ListView listView) {
        String sql = "exec fm_pre_get_taskorder_create_datas ?";
        Parameters p = new Parameters().add(1, stringSelectedTaskOrderCode);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PreOnline2Activity.this, value.Error);
                } else {
                    if (value.Value.Rows.size() > 0) {
                        listView.setAdapter(new PreOnlineAdater(value.Value));
                    }
                }
            }
        });
    }

    /**
     * 选择位号
     *
     * @param cardNumber 打卡的工号
     */
    private void chooseWf(final String cardNumber) {
        final PopupWindow popupWindow = new PopupWindow();
//        selectedItems = new ArrayList<>();
        selectedWhs = new ArrayList<>();
        View view = View.inflate(PreOnline2Activity.this, R.layout.item_preonline_popup, null);
        final EditText editText = (EditText) view.findViewById(R.id.edittext);
        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        final ListView listView = (ListView) view.findViewById(R.id.listview);
        editText.setHint("请输入位号查询");
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    String trim = editText.getText().toString().trim();
                    if (trim.startsWith("CRQ:") && trim.contains("-")) {     //获取扫描后的物料编码
                        itemWh = trim.split("-")[1].trim();
                    } else if (trim.startsWith("R:")) {
                        itemWh = trim.substring(2);
                    } else {
                        itemWh = trim;
                    }
                    editText.setText(itemWh);
                    afterWhKeyListener(listView);
                    return true;
                }
                return false;
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取已经选中的物料，隐藏popup，清空selecteditems
                stringSelectedWhs = new StringBuffer();
                for (int i = 0; i < selectedWhs.size(); i++) {
                    if (i == selectedWhs.size() - 1) {
                        stringSelectedWhs.append(selectedWhs.get(i));
                    } else {
                        stringSelectedWhs.append(selectedWhs.get(i) + ",");
                    }
                }
                selectedWhs.removeAll(selectedWhs);
                popupWindow.dismiss();
                //选择加工方式
                chooseProcessingMethod(cardNumber);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏popup，清空selecteditems
                popupWindow.dismiss();
                selectedWhs.removeAll(selectedWhs);
            }
        });
        afterWhKeyListener(listView);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        popupWindow.setWidth(width / 2);
        popupWindow.setHeight(height - (height / 5));
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(listView, Gravity.CENTER, 0, 0);
        //listview点击事件，选中与非选中
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view.findViewById(R.id.textview_1);
                String trim = textView.getText().toString().trim();
                if (selectedWhs.contains(trim)) {
                    selectedWhs.remove(trim);
                } else {
                    selectedWhs.add(trim);
                }
                myWhListAdapter.frash(selectedWhs);
            }
        });
    }

    /**
     * 选择加工站位和方式
     *
     * @param cardNumber
     */
    private void chooseProcessingMethod(final String cardNumber) {
        final PopupWindow popupWindow = new PopupWindow();
        stringSelectedProcess = new StringBuffer();
        selectedProcess = new ArrayList<>();
        selectedListItems = new ArrayList<>();
        View view = View.inflate(PreOnline2Activity.this, R.layout.item_preonline_station_popup, null);
        final ButtonTextCell buttonTextCell = (ButtonTextCell) view.findViewById(R.id.button_text_cell);
        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        TextView watch_sop = (TextView) view.findViewById(R.id.watch_sop);
        watch_sop.setVisibility(View.VISIBLE);
        final ListView listView = (ListView) view.findViewById(R.id.listview);
        buttonTextCell.setLabelText("加工站别");
        buttonTextCell.Button.setOnClickListener(new View.OnClickListener() {    //点击，选择站别
            @Override
            public void onClick(View view) {
                chooseStation(buttonTextCell, listView);
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果没有选，不让进行下一步
                if (selectedProcess != null && selectedProcess.size() < 1) {
                    App.Current.toastError(PreOnline2Activity.this, "请先选择加工方式！");
                } else {
                    //获取已经选中的站位，隐藏popup，清空selecteditems
                    stringSelectedProcess = new StringBuffer();
                    for (int i = 0; i < selectedProcess.size(); i++) {
                        if (i == selectedProcess.size() - 1) {
                            stringSelectedProcess.append(selectedProcess.get(i));
                        } else {
                            stringSelectedProcess.append(selectedProcess.get(i) + ",");
                        }
                    }
                    selectedProcess.removeAll(selectedProcess);
                    popupWindow.dismiss();
                    //选择加工方式
//                chooseProcessingMethods(cardNumber);
                    //上线
                    onlineWorker(cardNumber, stringSelectedItems.toString(), stringSelectedProcess.toString());
                    //触发首件
                    startFirstItem(stringSelectedProcess.toString());
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏popup，清空selecteditems
                popupWindow.dismiss();
                selectedProcess.removeAll(selectedProcess);
            }
        });
        watch_sop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreOnline2Activity.this, PreSopActivity.class);
                intent.putExtra("task_order_code", stringSelectedTaskOrderCode);
                startActivity(intent);
            }
        });
        afterStationKeyListener(listView);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        popupWindow.setWidth(width / 2);
        popupWindow.setHeight(height - (height / 5));
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(listView, Gravity.CENTER, 0, 0);
        //listview点击事件，选中与非选中
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView1 = (TextView) view.findViewById(R.id.textview_1);
                TextView textView3 = (TextView) view.findViewById(R.id.textview_3);
                String trim1 = textView1.getText().toString().trim();
                String trim3 = textView3.getText().toString().trim();
                String trim = trim1 + "-" + trim3;
                if (selectedProcess.contains(trim)) {
                    selectedProcess.remove(trim);
                } else {
                    selectedProcess.add(trim);
                }
                if (selectedListItems.contains(String.valueOf(i))) {
                    selectedListItems.remove(String.valueOf(i));
                } else {
                    selectedListItems.add(String.valueOf(i));
                }
                myListStationAdapter.frash(selectedListItems);
            }
        });
    }

    private void startFirstItem(String processMethod) {
        //发起首件
        Map<String, String> headMap = new HashMap<>();
        headMap.put("task_order_code", stringSelectedTaskOrderCode);
        headMap.put("user_id", App.Current.UserID);
        headMap.put("process_method", processMethod);

        ArrayList<Map<String, String>> itemLists = new ArrayList<>();
        if (itemIDs != null && itemIDs.size() > 0) {
            for (int i = 0; i < itemIDs.size(); i++) {
                Map<String, String> itemMap = new HashMap<>();
                String item_code = itemIDs.get(i).getItem_code();
                itemMap.put("item_code", item_code);
                itemLists.add(itemMap);
            }
        }
        if (!TextUtils.isEmpty(itemCode)) {
            Map<String, String> itemMap = new HashMap<>();
            itemMap.put("item_code", itemCode);
            itemLists.add(itemMap);
        }
        Log.e("len", itemCode + "*" + itemLists.size());
        String xml = XmlHelper.createXml("head", headMap, "items", "item", itemLists);
        Log.e("len", "XML : " + xml);
        String sql = "exec fm_pre_first_item_start ?";
        Parameters p = new Parameters().add(1, xml);
        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                Result<Integer> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PreOnline2Activity.this, value.Error);
                } else {
                    if (value.Value > 0) {
                        App.Current.toastError(PreOnline2Activity.this, "触发首件成功");
                    }
                }
            }
        });

    }

    private void chooseStation(final ButtonTextCell buttonTextCell, final ListView listView) {
        String sql = "exec fm_pre_get_process_station_all";
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PreOnline2Activity.this, value.Error);
                } else {
                    if (value.Value != null && value.Value.Rows.size() > 0) {
                        ArrayList<String> stations = new ArrayList<>();
                        for (int i = 0; i < value.Value.Rows.size(); i++) {
                            String type_station = value.Value.Rows.get(i).getValue("type_station", "");
                            stations.add(type_station);
                        }
                        //弹出对话框，选择对应站别
                        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String type_station = value.Value.Rows.get(i).getValue("type_station", "");
                                buttonTextCell.setContentText(type_station);
                                processStation = type_station;
                                afterStationKeyListener(listView);
                                dialogInterface.dismiss();
                            }
                        };

                        new AlertDialog.Builder(PreOnline2Activity.this)
                                .setTitle("请选择")
                                .setSingleChoiceItems(stations.toArray(new String[0]), stations.indexOf(buttonTextCell.getContentText().trim()), listener).show();
                    }
                }
            }
        });
    }

    private void chooseProcessingMethods(final String cardNumber) {
        String sql = "exec fm_pre_choose_process_method ?";
        Parameters p = new Parameters().add(1, "");
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PreOnline2Activity.this, value.Error);
                } else {
                    if (value.Value != null && value.Value.Rows.size() > 0) {
//                    toastChooseDialog(value.Value);

                        ArrayList<String> names = new ArrayList<String>();
                        for (int i = 0; i < value.Value.Rows.size(); i++) {
                            names.add(value.Value.Rows.get(i).getValue("type_station", "") + "-" + value.Value.Rows.get(i).getValue("processing_methods", ""));
                        }
                        stringSelectedProcess = new StringBuffer();
                        selectedProcess = new ArrayList<>();
                        final boolean[] selected = new boolean[value.Value.Rows.size()];
                        multiChoiceDialog(names, value.Value, selected, cardNumber);
                    } else {
                        App.Current.toastError(PreOnline2Activity.this, "加工类型为空，请联系管理员！");
                    }
                }
            }
        });
    }

    private void afterTaskOrderCodeKeyListener(final ListView listView) {    //工单索引后监听
        String sql = "exec fm_get_item_task_order_by_code ?";
        Parameters p = new Parameters().add(1, taskOrderCode);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PreOnline2Activity.this, value.Error);
                } else {
                    if (value.Value != null) {
                        myListTaskOrderCodeAdapter = new MyListTaskOrderCodeAdapter(value.Value, PreOnline2Activity.this);
                        listView.setAdapter(myListTaskOrderCodeAdapter);
                    }
                }
            }
        });
    }

    private void afterKeyListener(final ListView listView) {
        String sql = "exec fm_pre_get_item_message_by_code ?,?";
        Parameters p = new Parameters().add(1, stringSelectedTaskOrderCode).add(2, itemCode);
        Log.e("len", stringSelectedTaskOrderCode + "*" + itemCode);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PreOnline2Activity.this, value.Error);
                } else {
                    if (value.Value != null) {
                        items = new DataTable();
                        items.Rows.add(value.Value);
//                        if (myListAdapter == null) {
                        myListAdapter = new MyListAdapter(items);
                        listView.setAdapter(myListAdapter);
//                        } else {
//                            myListAdapter.notifyDataSetChanged();
//                        }
                        selectedItems.add(value.Value.getValue("item_code", ""));
                        selectedListItems.add(String.valueOf(items.Rows.size() - 1));
                    } else {
                        App.Current.toastError(PreOnline2Activity.this, "您扫描的物料有问题：" + itemCode);
                    }
                }
            }
        });
    }

    private void afterWhKeyListener(final ListView listView) {
        stringSelectedItems = new StringBuffer();
        String sql = "exec fm_get_wh_by_item_code ?,?,?";
        for (int i = 0; i < selectedItems.size(); i++) {
            stringSelectedItems.append(selectedItems.get(i));
            stringSelectedItems.append(",");
        }
        Log.e("len", stringSelectedTaskOrderCode + "," + stringSelectedItems.toString() + "," + itemWh);
        Parameters p = new Parameters().add(1, stringSelectedTaskOrderCode).add(2, stringSelectedItems.toString()).add(3, itemWh);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PreOnline2Activity.this, value.Error);
                } else {
                    if (value.Value != null) {
                        ArrayList<String> refWhs = new ArrayList<>();
                        String ref_wh = value.Value.getValue("ref_wh", "");
                        if (ref_wh.contains(",")) {
                            String[] split = ref_wh.split(",");
                            for (int i = 0; i < split.length; i++) {
                                refWhs.add(split[i]);
                            }
                        } else {
                            refWhs.add(ref_wh);
                        }
                        myWhListAdapter = new MyWhListAdapter(refWhs);
                        listView.setAdapter(myWhListAdapter);
                    }
                }
            }
        });
    }

    private void afterStationKeyListener(final ListView listView) {     //选择站位时候的结果
        String sql = "exec fm_pre_get_process_methods_by_station ?";
        Parameters p = new Parameters().add(1, processStation);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PreOnline2Activity.this, value.Error);
                } else {
                    if (value.Value != null) {
                        myListStationAdapter = new MyListStationAdapter(value.Value);
                        listView.setAdapter(myListStationAdapter);
                    }
                }
            }
        });
    }

    private void multiChoiceDialog(ArrayList<String> names, final DataTable dataTable,
                                   final boolean[] selected, final String cardNumber) {

        new AlertDialog.Builder(PreOnline2Activity.this).setTitle("请选择")
                .setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onlineWorker(cardNumber, stringSelectedItems.toString(), stringSelectedProcess.toString());
            }
        })
                .setMultiChoiceItems(names.toArray(new String[0]), selected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        selected[i] = b;
                        if (selected[i] == true) {
                            String processing_methods = dataTable.Rows.get(i).getValue("processing_methods", "");
                            String type_station = dataTable.Rows.get(i).getValue("type_station", "");
                            if (!selectedProcess.contains(type_station + "-" + processing_methods)) {
                                selectedProcess.add(type_station + "-" + processing_methods);
                                stringSelectedProcess.append(type_station + "-" + processing_methods);
                                stringSelectedProcess.append(",");
                            }
                        }
                    }
                }).show();
    }

    /**
     * @param cardNumber     员工打卡工号
     * @param item_ids       加工物料ID集
     * @param processMethods 加工方式集
     */
    private void onlineWorker(String cardNumber, String item_ids, String processMethods) {
        Time time = new Time();
        time.setToNow();
        int hour = time.hour;

        Map<String, String> headMap = new HashMap<>();

        headMap.put("task_order_code", stringSelectedTaskOrderCode);
        headMap.put("work_line", "前加工");
        headMap.put("sequence_id", "15");
        headMap.put("worker_code", cardNumber);
        headMap.put("work_shift", hour > 18 ? "夜班" : "白班");
        headMap.put("work_station", " ");
        headMap.put("work_device", "一体机");
        headMap.put("department", "4401");
        headMap.put("work_leader", "3446");
        headMap.put("user_id", App.Current.UserID);
        headMap.put("item_ids", item_ids);
        headMap.put("process_methods", processMethods);
        headMap.put("mac_address", App.Current.getMacAddress());
        headMap.put("ref_wh", stringSelectedWhs.toString().trim());
        headMap.put("pre_code", qjgIdent);
        headMap.put("pre_lot_number", preLotNumber);
        headMap.put("lot_ids", lot_ids);
        String xml = XmlHelper.createXml("head", headMap, "items", "item", null);
        Log.e("len", xml);

        String sql = "exec fm_sign_on_pre_mac_wh_work_time_v1 ?";
        Parameters p = new Parameters().add(1, xml);
        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                Result<Integer> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PreOnline2Activity.this, "Online" + value.Error);
                    editText.setText("");
                    return;
                }
                if (value.Value != null) {
//                    Integer id = value.Value.getValue("id", 0);
                    App.Current.toastInfo(PreOnline2Activity.this, "上线打卡成功");
                    lot_ids = "";
                    refreshWorker();   //刷新，获取当前打卡集合
                }
                editText.setText("");
            }
        });
    }

    private void refreshWorker() {
        String sql = "exec p_fm_pre_work_sign_get_items_by_mac_and ?,?,?";
        Parameters p = new Parameters().add(1, App.Current.getMacAddress()).add(2, "前加工").add(3, 15);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PreOnline2Activity.this, "Refresh" + value.Error);
                    editText.setText("");
                    return;
                }
                if (value.Value != null) {
                    dataTable = value.Value;
                    adapter = new PreOnlineAdater(value.Value);
                    listView.setAdapter(adapter);
//                    adapter.frash(dataTable);
                }
                editText.setText("");
                selectedItem = "";
//                App.Current.toastInfo(PreOnlineActivity.this, "成功");
//                selectWorker = null;
//                worker = "";
                textview_online_count.setText(dataTable.Rows.size() + "");
                double times = 0;
                for (int i = 0; i < dataTable.Rows.size(); i++) {
                    double work_hours = Double.parseDouble(df.format(Double.parseDouble(dataTable.Rows.get(i).getValue("work_hours", new BigDecimal(0)).toString())));
                    times += work_hours;
                }
                textview_time_count.setText(df.format(times));
            }
        });
    }

    private void checkCard(String edittextdata) {   //
        // workerOnLine(edittextdata);
        String sql = "exec p_fm_card_login ?";
        Parameters p = new Parameters().add(1, edittextdata.replace("\n", ""));
        editText.setText("");
        App.Current.DbPortal.ExecuteRecordAsync("core_hr_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PreOnline2Activity.this, "HR" + value.Error);
                    return;
                }
                if (value.Value != null) {
                    String user_code = value.Value.getValue("user_code", "");
//                    chooseItemCode(user_code);
                    chooseTaskOrderCode(user_code);
                } else {
                    App.Current.toastError(PreOnline2Activity.this, "本卡不属于此系统");
                    App.Current.playSound(R.raw.error);
                    editText.setText("");
                }
            }
        });
    }

    class PreOnlineAdater extends BaseAdapter {
        private DataTable dataTable;
        private int chooseItem = -1;

        public PreOnlineAdater(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        public void frash(DataTable dataTable) {
            this.dataTable = dataTable;
            notifyDataSetChanged();
        }

        public void changeChoose(int position) {
            if (chooseItem != position) {
                chooseItem = position;
            } else {
                chooseItem = -1;
            }
            notifyDataSetChanged();
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
            final ViewHolder viewHolder;
            if (view == null) {
                view = View.inflate(PreOnline2Activity.this, R.layout.item_preonline, null);
                viewHolder = new ViewHolder();
                viewHolder.textview_message = (TextView) view.findViewById(R.id.textview_message);
                viewHolder.textview_quantity = (TextView) view.findViewById(R.id.textview_quantity);
                viewHolder.textview_worker = (TextView) view.findViewById(R.id.textview_worker);
                viewHolder.textview_ontime = (TextView) view.findViewById(R.id.textview_ontime);
                viewHolder.textview_itemcode = (TextView) view.findViewById(R.id.textview_itemcode);
                viewHolder.textview_wh = (TextView) view.findViewById(R.id.textview_wh);
                viewHolder.textview_method = (TextView) view.findViewById(R.id.textview_method);
                viewHolder.textview_timecount = (TextView) view.findViewById(R.id.textview_timecount);
                viewHolder.textview_status = (TextView) view.findViewById(R.id.textview_status);
                viewHolder.textview_comment = (TextView) view.findViewById(R.id.textview_comment);
                viewHolder.textview_ipqc = (TextView) view.findViewById(R.id.text_ipqc);
                viewHolder.buttonIpqc = (Button) view.findViewById(R.id.button_ipqc);
                viewHolder.buttonStart = (Button) view.findViewById(R.id.button_start);
                viewHolder.buttonOffline = (Button) view.findViewById(R.id.button_offline);
                viewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            String worker_name = dataTable.Rows.get(i).getValue("worker_name", "");
            final String task_order_code = dataTable.Rows.get(i).getValue("task_order_code", "");
            String pre_process_wh = dataTable.Rows.get(i).getValue("pre_process_wh", "");
            String item_code = dataTable.Rows.get(i).getValue("item_code", "");
            String item_name = dataTable.Rows.get(i).getValue("item_name", "");
            Date on_time = dataTable.Rows.get(i).getValue("on_time", date);
            Date start_time = dataTable.Rows.get(i).getValue("start_time", date);
            final BigDecimal work_hours = dataTable.Rows.get(i).getValue("work_hours", new BigDecimal(0));
            BigDecimal plan_quantity = dataTable.Rows.get(i).getValue("plan_quantity", new BigDecimal(0));
            String status = dataTable.Rows.get(i).getValue("status", "");
            String process_method = dataTable.Rows.get(i).getValue("process_method", "");
            String remark = dataTable.Rows.get(i).getValue("remark", "");
            final String item_ids = dataTable.Rows.get(i).getValue("item_ids", "");
            String ipqc_code = dataTable.Rows.get(i).getValue("ipqc_code", "");
            String item_codes = dataTable.Rows.get(i).getValue("item_codes", "");
            viewHolder.textview_worker.setText(worker_name);
            String onTime = simpleDateFormat.format(on_time);
            String format = simpleDateFormat.format(date);
            String startTime = simpleDateFormat.format(start_time);
            viewHolder.textview_message.setText(task_order_code + "\n" + item_code + "\n" + item_name);
            viewHolder.textview_quantity.setText(String.valueOf(plan_quantity.intValue()));
            viewHolder.textview_itemcode.setText(item_codes);
            viewHolder.textview_wh.setText(pre_process_wh);
            viewHolder.textview_ipqc.setText(ipqc_code);
            viewHolder.textview_method.setText(process_method.trim());
            if (onTime.equals(format)) {
                viewHolder.textview_ontime.setText(" ");
            } else {
                viewHolder.textview_ontime.setText(onTime);
            }
            if (startTime.equals(format) && status.equals("上线")) {
                viewHolder.textview_status.setText("未启动");
            } else {
                viewHolder.textview_status.setText(status);
            }
            viewHolder.textview_timecount.setText(String.valueOf(df.format(work_hours)));
            viewHolder.textview_comment.setText(remark);

            if (chooseItem == i) {
                viewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.skyblue));
            } else {
                viewHolder.linearLayout.setBackgroundColor(Color.WHITE);
            }

            viewHolder.buttonIpqc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    App.Current.prompt(PreOnline2Activity.this, "提示", "请IPQC打卡", new PromptCallback() {
                        @Override
                        public void onReturn(String result) {
                            String[] worker = viewHolder.textview_worker.getText().toString().split("-");
                            final String workerCode = worker[0].trim();
                            String sql = "exec fm_pre_ipqc_sure_card ?,?,?,?,?";
                            Parameters p = new Parameters().add(1, result.replace("\n", "").trim()).add(2, viewHolder.textview_message.getText().toString().split("\n")[0].trim()).add(3, App.Current.getMacAddress().trim()).add(4, item_ids.trim()).add(5, workerCode.trim());
                            App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                                @Override
                                public void handleMessage(Message msg) {
                                    Result<Integer> value = Value;
                                    if (value.HasError) {
                                        App.Current.toastError(PreOnline2Activity.this, value.Error);
                                    } else {
                                        App.Current.toastInfo(PreOnline2Activity.this, "IPQC确认成功");
                                        refreshWorker();
                                    }
                                }
                            });
                        }
                    });
                }
            });
            viewHolder.buttonStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] worker = viewHolder.textview_worker.getText().toString().split("-");
                    final String workerCode = worker[0].trim();
                    String sql = "exec fm_sign_start_pre_work_time ?,?";
                    Parameters p = new Parameters().add(1, workerCode).add(2, viewHolder.textview_message.getText().toString().split("\n")[0]);
                    App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                        @Override
                        public void handleMessage(Message msg) {
                            Result<Integer> value = Value;
                            if (value.HasError) {
                                App.Current.toastError(PreOnline2Activity.this, value.Error);
                            } else {
                                if (value.Value > 0) {
                                    App.Current.toastInfo(PreOnline2Activity.this, "启动成功" + workerCode);
                                    refreshWorker();
                                } else {
                                    App.Current.toastError(PreOnline2Activity.this, "出错了");
                                }
                            }
                        }
                    });
                }
            });
            viewHolder.buttonOffline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    App.Current.prompt(PreOnline2Activity.this, "提示", "请输入加工数量", new PromptCallback() {
                        @Override
                        public void onReturn(String result) {
                            String[] worker = viewHolder.textview_worker.getText().toString().split("-");
                            String taskOrderCode = task_order_code;
                            final String workerCode = worker[0].trim();
                            if (TextUtils.isEmpty(result)) {
                                App.Current.toastError(PreOnline2Activity.this, "请先输入加工数量");
                            } else if (result.matches("^[0-9]*$")) {
                                String sql = "exec fm_sign_off_pre_work_time ?,?,?";
                                Parameters p = new Parameters().add(1, result).add(2, workerCode).add(3, taskOrderCode);
                                App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        Result<Integer> value = Value;
                                        if (value.HasError) {
                                            App.Current.toastError(PreOnline2Activity.this, "OffItem" + value.Error);
                                        } else {
                                            App.Current.toastInfo(PreOnline2Activity.this, "离线成功");
                                            refreshWorker();
                                        }
                                    }
                                });
                            } else {
                                App.Current.toastError(PreOnline2Activity.this, "请输入正确的数值");
                            }
                        }
                    });
                }
            });

//            dataTable.Rows.get(i).getValue("on_time", )
            return view;
        }
    }

    class ViewHolder {
        private TextView textview_message;
        private TextView textview_quantity;
        private TextView textview_worker;
        private TextView textview_ontime;
        private TextView textview_itemcode;
        private TextView textview_wh;
        private TextView textview_method;
        private TextView textview_timecount;
        private TextView textview_status;
        private TextView textview_comment;
        private TextView textview_ipqc;
        private Button buttonIpqc;
        private Button buttonStart;
        private Button buttonOffline;
        private LinearLayout linearLayout;
    }

    class MyListAdapter extends BaseAdapter {
        private DataTable dataTable;
        private ArrayList<String> selectedItems = new ArrayList<>();

        public MyListAdapter(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        public void frash(ArrayList<String> selectedItems) {
            this.selectedItems = selectedItems;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return dataTable == null ? 0 : dataTable.Rows.size();
        }

        @Override
        public Object getItem(int position) {
            return dataTable.Rows.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View contextView, ViewGroup viewGroup) {
            ItemViewHolder viewHolder;
            if (contextView == null) {
                contextView = View.inflate(PreOnline2Activity.this, R.layout.pre_card_choose_item, null);
                viewHolder = new ItemViewHolder();
                viewHolder.textview_1 = (TextView) contextView.findViewById(R.id.textview_1);
                viewHolder.textview_2 = (TextView) contextView.findViewById(R.id.textview_2);
                viewHolder.textview_3 = (TextView) contextView.findViewById(R.id.textview_3);
                viewHolder.linearLayout = (LinearLayout) contextView.findViewById(R.id.linearlayout);
                contextView.setTag(viewHolder);
            } else {
                viewHolder = (ItemViewHolder) contextView.getTag();
            }

            DataRow dataRow = dataTable.Rows.get(position);
            int item_id = dataRow.getValue("item_id", 0);
            String item_code = dataRow.getValue("item_code", "");
            String item_name = dataRow.getValue("item_name", "");
            viewHolder.textview_1.setText(String.valueOf(item_id));
            viewHolder.textview_2.setText(item_code);
            viewHolder.textview_3.setText(item_name);

            if (selectedItems.contains(String.valueOf(position))) {
                viewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.parameter_color));
            } else {
                viewHolder.linearLayout.setBackgroundColor(Color.WHITE);
            }
            return contextView;
        }
    }

    class ItemViewHolder {
        private TextView textview_1;
        private TextView textview_2;
        private TextView textview_3;
        private LinearLayout linearLayout;
    }

    class MyListStationAdapter extends BaseAdapter {
        private DataTable dataTable;
        private ArrayList<String> selectedItems = new ArrayList<>();

        public MyListStationAdapter(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        public void frash(ArrayList<String> selectedItems) {
            this.selectedItems = selectedItems;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return dataTable == null ? 0 : dataTable.Rows.size();
        }

        @Override
        public Object getItem(int position) {
            return dataTable.Rows.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View contextView, ViewGroup viewGroup) {
            ItemViewHolder viewHolder;
            if (contextView == null) {
                contextView = View.inflate(PreOnline2Activity.this, R.layout.pre_card_choose_item, null);
                viewHolder = new ItemViewHolder();
                viewHolder.textview_1 = (TextView) contextView.findViewById(R.id.textview_1);
                viewHolder.textview_2 = (TextView) contextView.findViewById(R.id.textview_2);
                viewHolder.textview_3 = (TextView) contextView.findViewById(R.id.textview_3);
                viewHolder.linearLayout = (LinearLayout) contextView.findViewById(R.id.linearlayout);
                contextView.setTag(viewHolder);
            } else {
                viewHolder = (ItemViewHolder) contextView.getTag();
            }

            DataRow dataRow = dataTable.Rows.get(position);
            String type_station = dataRow.getValue("type_station", "");
            String number = dataRow.getValue("number", "");
            String processing_methods = dataRow.getValue("processing_methods", "");
            viewHolder.textview_1.setText(type_station);
            viewHolder.textview_2.setText(number);
            viewHolder.textview_3.setText(processing_methods);

            if (selectedItems.contains(String.valueOf(position))) {
                viewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.parameter_color));
            } else {
                viewHolder.linearLayout.setBackgroundColor(Color.WHITE);
            }
            return contextView;
        }
    }

    class MyWhListAdapter extends BaseAdapter {
        private ArrayList<String> ref_whs;
        private ArrayList<String> selectedItems = new ArrayList<>();

        public MyWhListAdapter(ArrayList<String> ref_whs) {
            this.ref_whs = ref_whs;
        }

        public void frash(ArrayList<String> selectedItems) {
            this.selectedItems = selectedItems;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return ref_whs == null ? 0 : ref_whs.size();
        }

        @Override
        public Object getItem(int position) {
            return ref_whs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View contextView, ViewGroup viewGroup) {
            ItemViewHolder viewHolder;
            if (contextView == null) {
                contextView = View.inflate(PreOnline2Activity.this, R.layout.pre_card_choose_item, null);
                viewHolder = new ItemViewHolder();
                viewHolder.textview_1 = (TextView) contextView.findViewById(R.id.textview_1);
                viewHolder.textview_2 = (TextView) contextView.findViewById(R.id.textview_2);
                viewHolder.textview_3 = (TextView) contextView.findViewById(R.id.textview_3);
                viewHolder.linearLayout = (LinearLayout) contextView.findViewById(R.id.linearlayout);
                contextView.setTag(viewHolder);
            } else {
                viewHolder = (ItemViewHolder) contextView.getTag();
            }

            String ref_wh = ref_whs.get(position);
            viewHolder.textview_1.setText(ref_wh);
            viewHolder.textview_2.setVisibility(View.GONE);
            viewHolder.textview_3.setVisibility(View.GONE);

            if (selectedItems.contains(ref_wh)) {
                viewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.parameter_color));
            } else {
                viewHolder.linearLayout.setBackgroundColor(Color.WHITE);
            }
            return contextView;
        }
    }

    class MyItemListAdapter extends BaseAdapter {
        private ArrayList<PreItemBean> mItems = new ArrayList<>();

        public MyItemListAdapter(ArrayList<PreItemBean> selectedItems) {
            this.mItems = selectedItems;
        }

        public void frash(ArrayList<PreItemBean> mItems) {
            this.mItems = mItems;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mItems == null ? 0 : mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View contextView, ViewGroup viewGroup) {
            ItemViewHolder viewHolder;
            if (contextView == null) {
                contextView = View.inflate(PreOnline2Activity.this, R.layout.pre_card_choose_item, null);
                viewHolder = new ItemViewHolder();
                viewHolder.textview_1 = (TextView) contextView.findViewById(R.id.textview_1);
                viewHolder.textview_2 = (TextView) contextView.findViewById(R.id.textview_2);
                viewHolder.textview_3 = (TextView) contextView.findViewById(R.id.textview_3);
                viewHolder.linearLayout = (LinearLayout) contextView.findViewById(R.id.linearlayout);
                contextView.setTag(viewHolder);
            } else {
                viewHolder = (ItemViewHolder) contextView.getTag();
            }

            String item_code = mItems.get(position).getItem_code();
            String date_code = mItems.get(position).getDate_code();
            viewHolder.textview_1.setText(item_code);
            viewHolder.textview_2.setText(date_code);
            viewHolder.textview_3.setText("");
            viewHolder.textview_3.setVisibility(View.GONE);

//            if (selectedItems.contains(String.valueOf(position))) {
//                viewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.parameter_color));
//            } else {
//                viewHolder.linearLayout.setBackgroundColor(Color.WHITE);
//            }
            return contextView;
        }
    }
}
