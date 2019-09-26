package dynsoft.xone.android.sopactivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dynsoft.xone.android.base.BaseActivity;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/1/3.
 */

public class CardRegistrationActivity extends BaseActivity implements View.OnClickListener {
    private static final int ONLINE = 1;
    private static final int OFFLINE = 2;
    private View view;
    private Button button_stop;
    private Button button_resume;
    private Button button_refresh;
    private Button button_offline;
    private Button button_changeline;
    private Button button_offline_create;
    private TextView text_1;
    private TextView text_2;
    private TextView text_3;
    private TextView text_4;
    private TextView text_5;
    private TextView text_6;
    private TextView text_7;
    private RadioGroup rediogroup;
    private EditText edittext;
    private ListView listview;
    private TextView textview_online_count;
    private TextView textview_time_count;
    private MyListAdapter adapter;
    private SharedPreferences sharedPreferences;
    private String edittextdata;
    private int status = ONLINE;
    private String production;
    private String[] split;
    private String work_stage;
    private int order_task_id;
    private int seq_id;
    private int foreman_id;
    private String task_order;
    private String device;
    private String station;
    private String org_code;
    private TextView selectWorker;
    private String worker = "";
    private DataTable dataTable;
    private long worker_sign_id;
    private String segment;
    private DecimalFormat df;

    @Override
    public View setContentView() {
        view = View.inflate(this, R.layout.activity_cardregistration, null);
        sharedPreferences = getSharedPreferences("sop", MODE_PRIVATE);
        df = new DecimalFormat("######0.00");
        initView();
        return view;
    }

    private void initView() {
        button_stop = (Button) view.findViewById(R.id.button_stop);
        button_resume = (Button) view.findViewById(R.id.button_resume);
        button_refresh = (Button) view.findViewById(R.id.button_refresh);
        button_offline = (Button) view.findViewById(R.id.button_offline);
        button_changeline = (Button) view.findViewById(R.id.button_changeline);  //换线
        button_offline_create = (Button) view.findViewById(R.id.button_offline_create);
        text_1 = (TextView) view.findViewById(R.id.text_1);
        text_2 = (TextView) view.findViewById(R.id.text_2);
        text_3 = (TextView) view.findViewById(R.id.text_3);
        text_4 = (TextView) view.findViewById(R.id.text_4);
        text_5 = (TextView) view.findViewById(R.id.text_5);
        text_6 = (TextView) view.findViewById(R.id.text_6);
        text_7 = (TextView) view.findViewById(R.id.text_7);
        rediogroup = (RadioGroup) view.findViewById(R.id.rediogroup);
        edittext = (EditText) view.findViewById(R.id.edittext);
        listview = (ListView) view.findViewById(R.id.listview);
        textview_online_count = (TextView) view.findViewById(R.id.textview_online_count);
        textview_time_count = (TextView) view.findViewById(R.id.textview_time_count);

        order_task_id = sharedPreferences.getInt("order_task_id", 0);
        seq_id = sharedPreferences.getInt("seq_id", 0);
        foreman_id = sharedPreferences.getInt("foreman_id", 0);
        task_order = sharedPreferences.getString("task_order", "");
        device = sharedPreferences.getString("device", "");
        org_code = sharedPreferences.getString("org_code", "");
        station = sharedPreferences.getString("station", "");
        production = sharedPreferences.getString("production", "");
        segment = sharedPreferences.getString("segment", "");
        split = production.split(",");

        button_stop.setOnClickListener(this);
        button_resume.setOnClickListener(this);
        button_refresh.setOnClickListener(this);
        button_offline.setOnClickListener(this);
        button_changeline.setOnClickListener(this);
        button_offline_create.setOnClickListener(this);

        if (text_1 != null) {
            text_1.setText(sharedPreferences.getString("task_order", ""));
        }


        //select work_stage, comment  from fm_sequence where name

        if (text_4 != null) {
            text_4.setText(sharedPreferences.getString("station", ""));
        }

        if (text_5 != null) {
            text_5.setText(segment);
        }

        if (text_6 != null) {
            text_6.setText(production);
        }

        rediogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.radiobutton_1:
                        status = ONLINE;
                        break;
                    case R.id.radiobutton_2:
                        status = OFFLINE;
                        break;
                }
            }
        });

        Log.e("len", "APP:" + App.Current.Server.Address);
        if(App.Current.Server.Address.contains("192.168.0.126")) {
            edittext.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        edittextdata = edittext.getText().toString().replace("\n", "").toUpperCase();
//                    edittextdata = edittext.getText().toString();
                        if ((edittextdata.toUpperCase().startsWith("M") || edittextdata.toUpperCase().startsWith("YH"))&& edittextdata.length() < 8) {
                            if (status == ONLINE) {
                                workerOnLine(edittextdata.replace("\n", ""));
                            } else {
                                workerOffLine(edittextdata.replace("\n", ""));
                            }
                        }
                    }
                    return false;
                }
            });

            edittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() == 10) {
                        String s = editable.toString();
//                    String replace = s.startsWith("0") ? s.substring(1) : s;
                        checkIkaheCard(s, status);
                    }
                }
            });
        } else if (App.Current.Server.Address.contains("39.108.49.47")){
            edittext.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        edittextdata = edittext.getText().toString().replace("\n", "").toUpperCase();
//                    edittextdata = edittext.getText().toString();
                        if (edittextdata.length() < 9) {
                            if (status == ONLINE) {
                                workerOnLine(edittextdata.replace("\n", ""));
                            } else {
                                workerOffLine(edittextdata.replace("\n", ""));
                            }
                        }
                    }
                    return false;
                }
            });

            edittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() == 10) {
                        String s = editable.toString();
//                    String replace = s.startsWith("0") ? s.substring(1) : s;
                        checkIkaheCard(s, status);
                    }
                }
            });
        }else {
            edittext.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        edittextdata = edittext.getText().toString().replace("\n", "").toUpperCase();
//                    edittextdata = edittext.getText().toString();
                        if (edittextdata.startsWith("M") && edittextdata.length() < 8) {
                            if (status == ONLINE) {
                                workerOnLine(edittextdata.replace("\n", ""));
                            } else {
                                workerOffLine(edittextdata.replace("\n", ""));
                            }
                        }
                    }
                    return false;
                }
            });

            edittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() == 10) {
                        String s = editable.toString();
//                    String replace = s.startsWith("0") ? s.substring(1) : s;
                        checkCard(s, status);
                    }
                }
            });
        }


        getSection(split[0]);
        getItemMessage(order_task_id);

        adapter = new MyListAdapter(dataTable);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectWorker = (TextView) view.findViewById(R.id.textview_5);
                worker_sign_id = dataTable.Rows.get(position).getValue("id", 0L);
                adapter.changSelected(position);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Toast.makeText(this, "keyCode:" + keyCode, Toast.LENGTH_SHORT).show();
        return super.onKeyDown(keyCode, event);
    }

    private void checkCard(String edittextdata, final int status) {   //
        // workerOnLine(edittextdata);
        String sql = "exec p_fm_card_login ?";
        Parameters p = new Parameters().add(1, edittextdata.replace("\n", ""));
        edittext.setText("");
        App.Current.DbPortal.ExecuteRecordAsync("core_hr_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(CardRegistrationActivity.this, value.Error);
                    return;
                }
                if (value.Value != null) {
                    String user_code = value.Value.getValue("user_code", "");
                    if (status == ONLINE) {
                        workerOnLine(user_code);
                    } else {
                        workerOffLine(user_code);
                    }

                } else {
                    App.Current.toastError(CardRegistrationActivity.this, "本卡不属于此系统");
                    App.Current.playSound(R.raw.error);
                    edittext.setText("");
                }
            }
        });
    }

    private void checkIkaheCard(String edittextdata, final int status) {   //
        // workerOnLine(edittextdata);
        String sql = "select * from fm_worker where id_card_number = ?";
        Parameters p = new Parameters().add(1, edittextdata.replace("\n", ""));
        edittext.setText("");
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(CardRegistrationActivity.this, value.Error);
                    return;
                }
                if (value.Value != null) {
                    String user_code = value.Value.getValue("code", "");
                    if (status == ONLINE) {
                        workerOnLine(user_code);
                    } else {
                        workerOffLine(user_code);
                    }

                } else {
                    App.Current.toastError(CardRegistrationActivity.this, "本卡不属于此系统");
                    App.Current.playSound(R.raw.error);
                    edittext.setText("");
                }
            }
        });
    }

    private void getItemMessage(int order_task_id) {
        String sql = "SELECT t2.code item_code, t2.name item_name " +
                "FROM dbo.mm_wo_task_order_head t0 INNER JOIN dbo.fm_process t1 ON t1.id=t0.process_id " +
                "INNER JOIN dbo.mm_item t2 ON t2.id=t0.item_id WHERE t0.id = ?";
        Parameters p = new Parameters().add(1, order_task_id);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(CardRegistrationActivity.this, value.Error);
                    return;
                }
                if (value.Value != null) {
                    String item_code = value.Value.getValue("item_code", "");
                    String item_name = value.Value.getValue("item_name", "");
                    text_2.setText(item_code);
                    text_3.setText(item_name);
                } else {
                    Toast.makeText(CardRegistrationActivity.this, "没查询到数据", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void workerOffLine(String edittextdata) {
        String sql = "exec p_fm_work_sign_off_and ?,?,?,?";
        Parameters p = new Parameters().add(1, order_task_id).add(2, segment).add(3, seq_id).add(4, edittextdata);
        Result<Integer> result = App.Current.DbPortal.ExecuteNonQuery("core_and", sql, p);
        if (result.HasError) {
            App.Current.showError(CardRegistrationActivity.this, result.Error);
            edittext.setText("");
            return;
        }
        if (result.Value != null) {
            App.Current.toastInfo(CardRegistrationActivity.this, "离线打卡成功");
            refreshWorker();
        }
        edittext.setText("");
    }

    private void workerOnLine(String edittextdata) {
        Time time = new Time();
        time.setToNow();
        int hour = time.hour;
        String sql = "exec p_fm_work_sign_on_and ?,?,?,?,?,?,?,?,?,?";
        Parameters p = new Parameters().add(1, order_task_id).add(2, segment).add(3, seq_id)
                .add(4, edittextdata).add(5, hour > 18 ? "夜班" : "白班").add(6, station).add(7, device)
                .add(8, org_code).add(9, foreman_id).add(10, App.Current.UserID);
        Log.e("len", order_task_id + "," + segment + "," + seq_id + "," + edittextdata + "," + "白班"+ ","
                + station + "," + device + "," + org_code + "," + foreman_id + "," + App.Current.UserID);
        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                Result<Integer> value = Value;
                if (value.HasError) {
                    App.Current.showError(CardRegistrationActivity.this, value.Error);
                    InputMethodManager systemService = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    systemService.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    edittext.setText("");
                    return;
                }
                if (value.Value != null) {
//                    Integer id = value.Value.getValue("id", 0);
                    App.Current.toastInfo(CardRegistrationActivity.this, "上线打卡成功");
                    refreshWorker();   //刷新，获取当前打卡集合
                }
                edittext.setText("");
            }
        });
    }

    private void refreshWorker() {
        String sql = "exec p_fm_work_sign_get_items_and ?,?,?";
        Parameters p = new Parameters().add(1, order_task_id).add(2, segment).add(3, seq_id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(CardRegistrationActivity.this, value.Error);
                    InputMethodManager systemService = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    systemService.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    edittext.setText("");
                    return;
                }
                if (value.Value != null) {
                    dataTable = value.Value;
                    adapter.frash(dataTable);
                    InputMethodManager systemService = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    systemService.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                edittext.setText("");
                adapter.changSelected(-1);
                selectWorker = null;
                worker = "";
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

    //button的点击事件
    @Override
    public void onClick(View view) {
        if (selectWorker != null) {
            worker = selectWorker.getText().toString();
        }
        switch (view.getId()) {
            case R.id.button_stop:
                if (dataTable != null && dataTable.Rows.size() > 0) {
                    if (!TextUtils.isEmpty(worker)) {    //有选中工作人员
                        new AlertDialog.Builder(CardRegistrationActivity.this)
                                .setTitle("询问")
                                .setMessage("确定要暂停选中人员的作业计时吗？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        stopTheWorker();
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    } else {                 //没有选中工作人员,全部暂停
                        new AlertDialog.Builder(CardRegistrationActivity.this)
                                .setTitle("询问")
                                .setMessage("确定要暂停所有人员的作业计时吗？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        stopAllWorkers();
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    }
                }
                break;
            case R.id.button_resume:
                if (dataTable != null && dataTable.Rows.size() > 0) {
                    if (!TextUtils.isEmpty(worker)) {    //有选中工作人员
                        new AlertDialog.Builder(CardRegistrationActivity.this)
                                .setTitle("询问")
                                .setMessage("确定要恢复选中人员的作业计时吗？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        resumeTheWorker();
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    } else {                 //没有选中工作人员,全部暂停
                        new AlertDialog.Builder(CardRegistrationActivity.this)
                                .setTitle("询问")
                                .setMessage("确定要恢复所有人员的作业计时吗？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        resumeAllWorkers();
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    }
                }
                break;
            case R.id.button_refresh:
                refreshWorker();
                break;
            case R.id.button_offline:
                if (dataTable != null) {
                    if (dataTable.Rows.size() > 0) {
                        if (!TextUtils.isEmpty(worker)) { //选中人员离线
                            new AlertDialog.Builder(CardRegistrationActivity.this)
                                    .setTitle("询问")
                                    .setMessage("确定要将选中人员离线吗？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            offLineTheWorker();
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .show();
                        } else {
                            new AlertDialog.Builder(CardRegistrationActivity.this)
                                    .setTitle("询问")
                                    .setMessage("确定要将所有人员离线吗？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            offLineAllWorkers();
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .show();
                        }
                    } else {

                    }
                }
                break;
            case R.id.button_offline_create:
                if (dataTable != null && dataTable.Rows.size() > 0) {
                    new AlertDialog.Builder(CardRegistrationActivity.this)
                            .setTitle("询问")
                            .setMessage("确定要将所有人员离线吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    offLineAllCreateWorkers();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
                break;
            case R.id.button_changeline:      //换线
                if (dataTable != null && dataTable.Rows.size() > 0) {
                    changePopupWindow();
                } else {
                   App.Current.toastError(CardRegistrationActivity.this, "当前没有打卡人员！");
                   App.Current.playSound(R.raw.error);
                }
                break;
        }
    }

    private void changePopupWindow() {
        final EditText et = new EditText(this);
        et.setHint("请输入需要复制的工单");
        new AlertDialog.Builder(this).setTitle("输入内容")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件
                        isInputTaskCodeRight(et.getText().toString());
                    }
                }).setNegativeButton("取消",null).show();
    }

    private void isInputTaskCodeRight(String taskCode) {
        String sql = "select id from mm_wo_task_order_head where code = ?";
        Parameters p = new Parameters().add(1, taskCode);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataRow> value = Value;
                if(value.HasError) {
                    App.Current.toastError(CardRegistrationActivity.this, value.Error);
                    App.Current.playSound(R.raw.error);
                    return;
                }
                if(value.Value != null) {
                    //输入正确，转线
                    long id = value.Value.getValue("id", 0L);
                    String sql = "exec fm_work_sign_start_by_seq ?,?,?,?,?";
                    Parameters p = new Parameters().add(1, text_5.getText()).add(2, id)
                            .add(3, seq_id).add(4, App.Current.UserID).add(5, text_1.getText());
                    App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                        @Override
                        public void handleMessage(Message msg) {
                            Result<DataRow> value1 = value;
                            if(value1.HasError) {
                                App.Current.toastError(CardRegistrationActivity.this, value1.Error);
                                App.Current.playSound(R.raw.error);
                                return;
                            }
                            if(value1.Value != null) {
                                App.Current.toastInfo(CardRegistrationActivity.this, "启动成功！");
                                App.Current.playSound(R.raw.pass);
                                refreshWorker();
                            }
                        }
                    });
                } else {
                    App.Current.toastError(CardRegistrationActivity.this, "输入的工单有误！");
                    App.Current.playSound(R.raw.error);
                }
            }
        });
    }

    private void resumeAllWorkers() {
        String sql = "exec p_fm_work_sign_resume_all_and ?,?,? ";
        Parameters p = new Parameters().add(1, order_task_id).add(2, segment).add(3, seq_id);
        refreshAfterSql(sql, p);
    }

    private void resumeTheWorker() {
        String sql = "exec p_fm_work_sign_resume_and ?";
        Parameters p = new Parameters().add(1, worker_sign_id);
        refreshAfterSql(sql, p);
    }

    private void stopAllWorkers() {
        String sql = "exec p_fm_work_sign_stop_all_and ?,?,?";
        Parameters p = new Parameters().add(1, order_task_id).add(2, segment).add(3, seq_id);
        refreshAfterSql(sql, p);
    }

    private void stopTheWorker() {
        String sql = "exec p_fm_work_sign_stop_and ?";
        Parameters p = new Parameters().add(1, worker_sign_id);
        //refreshAfterSql(sql, p);
        Result<DataRow> result = App.Current.DbPortal.ExecuteRecord("core_and", sql, p);
        if (result.HasError) {
            App.Current.showError(CardRegistrationActivity.this, result.Error + "****");
            return;
        }
        refreshWorker();
    }

    private void offLineAllWorkers() {
        String sql = "exec p_fm_work_sign_off_all_and ?,?,?,?";
        Parameters p = new Parameters().add(1, order_task_id).add(2, segment).add(3, seq_id).add(4, App.Current.UserID);
        refreshAfterSql(sql, p);
    }

    private void refreshAfterSql(String sql, Parameters p) {
        Result<Integer> result = App.Current.DbPortal.ExecuteNonQuery("core_and", sql, p);
        if (result.HasError) {
            App.Current.showError(CardRegistrationActivity.this, result.Error);
            return;
        }
        if (result.Value != null) {
            refreshWorker();
        }
    }

    private void offLineTheWorker() {   //设置指定人员离线
        String sql = "exec p_fm_work_sign_off_one_and ?";
        Parameters p = new Parameters().add(1, worker_sign_id);
        refreshAfterSql(sql, p);
    }

    private void offLineAllCreateWorkers() {
        String sql = "exec p_fm_work_sign_off_line_and ?,?,?";
        Parameters p = new Parameters().add(1, order_task_id).add(2, segment).add(3, App.Current.UserID);
        refreshAfterSql(sql, p);
    }

    private void getSection(String procedure) {
        String sql = "exec p_fm_get_section_and ?,?";
        Parameters p = new Parameters().add(1, task_order).add(2, procedure);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(CardRegistrationActivity.this, value.Error);
                    return;
                }
                if (value.Value != null) {
                    work_stage = value.Value.getValue("code", "");
                    String comment = value.Value.getValue("name", "");
                    text_7.setText(work_stage + " - " + comment);
                } else {
                    Toast.makeText(CardRegistrationActivity.this, "没查询到数据", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class MyListAdapter extends BaseAdapter {
        private DataTable dataTable;
        private int selectedItem = -1;

        public MyListAdapter(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        public void changSelected(int position) {
            if (selectedItem != position) {
                selectedItem = position;
                notifyDataSetChanged();
            }
        }

        public void frash(DataTable dataTable) {
            this.dataTable = dataTable;
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
            ViewHolder viewHolder;
            if (contextView == null) {
                contextView = View.inflate(CardRegistrationActivity.this, R.layout.cardregistration_item, null);
                viewHolder = new ViewHolder();
                viewHolder.textview_1 = (TextView) contextView.findViewById(R.id.textview_1);
                viewHolder.textview_2 = (TextView) contextView.findViewById(R.id.textview_2);
                viewHolder.textview_3 = (TextView) contextView.findViewById(R.id.textview_3);
                viewHolder.textview_4 = (TextView) contextView.findViewById(R.id.textview_4);
                viewHolder.textview_5 = (TextView) contextView.findViewById(R.id.textview_5);
                viewHolder.textview_6 = (TextView) contextView.findViewById(R.id.textview_6);
                viewHolder.textview_7 = (TextView) contextView.findViewById(R.id.textview_7);
                viewHolder.textview_8 = (TextView) contextView.findViewById(R.id.textview_8);
                viewHolder.textview_9 = (TextView) contextView.findViewById(R.id.textview_9);
                viewHolder.textview_10 = (TextView) contextView.findViewById(R.id.textview_10);
                viewHolder.linearLayout = (LinearLayout) contextView.findViewById(R.id.linearlayout);
                contextView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) contextView.getTag();
            }
            DataRow dataRow = dataTable.Rows.get(position);
            viewHolder.textview_1.setText(dataRow.getValue("task_order_code", ""));
            viewHolder.textview_2.setText(dataRow.getValue("work_line", ""));
            viewHolder.textview_3.setText(dataRow.getValue("work_stage", ""));
            viewHolder.textview_4.setText(dataRow.getValue("sequence_name", ""));
            viewHolder.textview_5.setText(dataRow.getValue("worker_name", ""));
            SimpleDateFormat decimalFormat = new SimpleDateFormat("HH:mm:ss");
            viewHolder.textview_6.setText(decimalFormat.format(dataRow.getValue("on_time", new Date())));
//            viewHolder.textview_7.setText(decimalFormat.format(dataRow.getValue("off_time", new Date())));
            viewHolder.textview_8.setText(df.format(Double.parseDouble(dataRow.getValue("work_hours", new BigDecimal(0)).toString())));
            String status = dataRow.getValue("status", "");
            viewHolder.textview_9.setText(status);
            viewHolder.textview_10.setText(dataRow.getValue("remark", ""));

            if (position == selectedItem) {
                viewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.parameter_color));
            } else {
                viewHolder.linearLayout.setBackgroundColor(Color.WHITE);
            }

            if (status.equals("暂停")) {
                viewHolder.textview_1.setTextColor(getResources().getColor(R.color.orangered));
                viewHolder.textview_2.setTextColor(getResources().getColor(R.color.orangered));
                viewHolder.textview_3.setTextColor(getResources().getColor(R.color.orangered));
                viewHolder.textview_4.setTextColor(getResources().getColor(R.color.orangered));
                viewHolder.textview_5.setTextColor(getResources().getColor(R.color.orangered));
                viewHolder.textview_6.setTextColor(getResources().getColor(R.color.orangered));
                viewHolder.textview_7.setTextColor(getResources().getColor(R.color.orangered));
                viewHolder.textview_8.setTextColor(getResources().getColor(R.color.orangered));
                viewHolder.textview_9.setTextColor(getResources().getColor(R.color.orangered));
                viewHolder.textview_10.setTextColor(getResources().getColor(R.color.orangered));
            } else {
                viewHolder.textview_1.setTextColor(getResources().getColor(R.color.blue));
                viewHolder.textview_2.setTextColor(getResources().getColor(R.color.blue));
                viewHolder.textview_3.setTextColor(getResources().getColor(R.color.blue));
                viewHolder.textview_4.setTextColor(getResources().getColor(R.color.blue));
                viewHolder.textview_5.setTextColor(getResources().getColor(R.color.blue));
                viewHolder.textview_6.setTextColor(getResources().getColor(R.color.blue));
                viewHolder.textview_7.setTextColor(getResources().getColor(R.color.blue));
                viewHolder.textview_8.setTextColor(getResources().getColor(R.color.blue));
                viewHolder.textview_9.setTextColor(getResources().getColor(R.color.blue));
                viewHolder.textview_10.setTextColor(getResources().getColor(R.color.blue));
            }
            return contextView;
        }
    }

    class ViewHolder {
        private TextView textview_1;
        private TextView textview_2;
        private TextView textview_3;
        private TextView textview_4;
        private TextView textview_5;
        private TextView textview_6;
        private TextView textview_7;
        private TextView textview_8;
        private TextView textview_9;
        private TextView textview_10;
        private LinearLayout linearLayout;
    }
}
