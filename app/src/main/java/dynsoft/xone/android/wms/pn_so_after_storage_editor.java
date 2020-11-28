package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.SwitchCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataSet;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class pn_so_after_storage_editor extends pn_editor implements OnClickListener, AdapterView.OnItemClickListener {

    public pn_so_after_storage_editor(Context context) {
        super(context);
    }


    public TextCell txt_sn_number_cell; // sn编码
    public TextCell txt_item_code_cell; //物料编码
    public TextCell txt_item_name_cell; //物料名称
    public TextCell txt_customer_name_cell; //客户名称
    public TextCell txt_sh_date_cell; //收货时间
    public TextCell txt_user_cell; //入库用户
    public TextCell txt_comment_cell; //备注
    public CheckBox chk_is_scrap; //入库操作
    public CheckBox chk_is_batch;
    public SwitchCell chk_commit_print;//打印批次号
    public TextCell txt_is_return; // 是否返还
    public TextCell txt_not_warehouse_cell;
    private Button txt_button;
    private Button btn_batch_start;
    private Button btn_batch_end;
    private TextCell txt_pc_number_cell;
    public ListView txt_batch_cell;
    private ScrollView scrollView;
    public MyAdapter myAdapter;
    private String main_pc_number;
    public DataTable resultDatatable;
    public TextCell txt_is_batch_cell;
    public TextCell txt_date_code_cell;
    private int quantity;
    private int result1 = 0;

    private int scan_count;
    private boolean checkbool;
    private DataRow _order_row;
    private DataRow _lot_row;
    private Integer _total = 0;
    private Long _rownum;
    private String work_order_code;

    boolean is_scrap = false;
    boolean is_batch = false;

    //设置对于的XML文件
    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(
                R.layout.pn_so_after_storage_editor, this, true);
        view.setLayoutParams(lp);
    }

    //  ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>();
    //  final String [] nItems = {"卷带","吸取中心偏移","送料不良","少配件","保养","感应灯不亮","抛料"};

    //设置显示控件
    @Override
    public void onPrepared() {

        super.onPrepared();

        scan_count = 0;
        checkbool = true;
        this.txt_sn_number_cell = (TextCell) this
                .findViewById(R.id.txt_sn_number_cell);

        this.txt_item_code_cell = (TextCell) this
                .findViewById(R.id.txt_item_code_cell);

        this.txt_button = (Button) this
                .findViewById(R.id.txt_button);
        txt_button.setVisibility(GONE);
        txt_button.setOnClickListener(this);


        this.btn_batch_start = (Button) this
                .findViewById(R.id.btn_batch_start);
        btn_batch_start.setOnClickListener(this);


        this.btn_batch_end = (Button) this
                .findViewById(R.id.btn_batch_end);
        btn_batch_end.setOnClickListener(this);

        this.txt_is_batch_cell = (TextCell) this
                .findViewById(R.id.txt_is_batch_cell);

        this.txt_not_warehouse_cell = (TextCell) this
                .findViewById(R.id.txt_not_warehouse_cell);
        this.txt_not_warehouse_cell.setReadOnly();

        this.txt_item_name_cell = (TextCell) this
                .findViewById(R.id.txt_item_name_cell);

        this.txt_customer_name_cell = (TextCell) this
                .findViewById(R.id.txt_customer_name_cell);

        this.txt_sh_date_cell = (TextCell) this
                .findViewById(R.id.txt_sh_date_cell);

        this.txt_user_cell = (TextCell) this
                .findViewById(R.id.txt_user_cell);

        this.txt_comment_cell = (TextCell) this
                .findViewById(R.id.txt_comment_cell);

        this.txt_is_return = (TextCell) this
                .findViewById(R.id.txt_is_return);

        this.txt_date_code_cell = (TextCell) this
                .findViewById(R.id.txt_date_code_cell);

        this.chk_is_scrap = (CheckBox) this
                .findViewById(R.id.chk_is_scrap);

        this.chk_is_batch = (CheckBox) this
                .findViewById(R.id.chk_is_batch);

        this.txt_pc_number_cell = (TextCell) this
                .findViewById(R.id.txt_pc_number_cell);
        this.txt_batch_cell = (ListView) this
                .findViewById(R.id.txt_batch_cell);

        scrollView = (ScrollView) findViewById(R.id.scroll);

        if (this.txt_sn_number_cell != null) {
            this.txt_sn_number_cell.setLabelText("sn编码");
            this.txt_sn_number_cell.setReadOnly();
        }

        if (this.txt_is_return != null) {
            this.txt_is_return.setLabelText("是否返还");
            this.txt_is_return.setReadOnly();
        }

        if (this.txt_is_batch_cell != null) {
            this.txt_is_batch_cell.setLabelText("绑定状态");
            this.txt_is_batch_cell.setReadOnly();
        }

        if (this.txt_date_code_cell != null) {
            this.txt_date_code_cell.setLabelText("周期");
        }


        if (this.txt_item_code_cell != null) {
            this.txt_item_code_cell.setLabelText("物料编码");
            this.txt_item_code_cell.setReadOnly();
        }

        if (this.txt_item_name_cell != null) {
            this.txt_item_name_cell.setLabelText("物料名称");
            this.txt_item_name_cell.setReadOnly();
        }


        if (this.txt_customer_name_cell != null) {
            this.txt_customer_name_cell.setLabelText("客户名称");
            this.txt_customer_name_cell.setReadOnly();
        }

        if (this.txt_sh_date_cell != null) {
            this.txt_sh_date_cell.setLabelText("收货日期");
            this.txt_sh_date_cell.setReadOnly();
        }


        this.txt_user_cell = (TextCell) this
                .findViewById(R.id.txt_user_cell);

        if (this.txt_user_cell != null) {
            this.txt_user_cell.setLabelText("入库人员");
            this.txt_user_cell.setReadOnly();
            this.txt_user_cell.setContentText(App.Current.UserCode);

        }


        if (this.txt_comment_cell != null) {
            this.txt_comment_cell.setLabelText("备注");
            //this.txt_comment_cell.setReadOnly();
        }

        if (this.txt_not_warehouse_cell != null) {
            this.txt_not_warehouse_cell.setLabelText("剩余未入库数量");
            this.txt_not_warehouse_cell.setReadOnly();
        }

        if (this.txt_pc_number_cell != null) {
            this.txt_pc_number_cell.setLabelText("批次号");
            this.txt_pc_number_cell.setReadOnly();
        }

    }


    //扫条码
    @Override
    public void onScan(final String barcode) {
        this.txt_sn_number_cell.setContentText("");
        this.txt_item_code_cell.setContentText("");
        this.txt_item_name_cell.setContentText("");
        this.txt_customer_name_cell.setContentText("");
        this.txt_sh_date_cell.setContentText("");
        this.txt_not_warehouse_cell.setContentText("");

        this.txt_date_code_cell.setContentText("");
        Button txt_button;
        final String bar_code = barcode.trim();
        //扫描SN条码
        this.txt_sn_number_cell.setContentText(bar_code.toString());
        loadItem(bar_code);
    }


    ///根据SN编号带出物料编码和物料名称
    public void loadItem(String code) {
        this.ProgressDialog.show();

        String sql = "exec PDA_mm_after_storage_not_warehouse ?";
        Parameters p = new Parameters().add(1, code);
        App.Current.DbPortal.ExecuteDataSetAsync(this.Connector, sql, p, new ResultHandler<DataSet>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_storage_editor.this.ProgressDialog.dismiss();

                Result<DataSet> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_storage_editor.this.getContext(), result.Error);
                    return;
                }

                DataSet row = result.Value;
                DataTable table1 = row.Tables.get(0);
                DataTable table2 = row.Tables.get(1);
                if (table1.Rows.size() == 0) {
                    App.Current.showError(pn_so_after_storage_editor.this.getContext(), "找不到该SN的对应的信息");
                    pn_so_after_storage_editor.this.Header.setTitleText("找不到该SN的对应的信息");
                    return;
                }

                if (table2.Rows.size() > 1) {
                    App.Current.showError(pn_so_after_storage_editor.this.getContext(), "该条码收货多次扫描");
                    pn_so_after_storage_editor.this.Header.setTitleText("该条码收货多次扫描");
                    return;
                }


                for (int x = 0; x < table2.Rows.size(); x++) {
                    pn_so_after_storage_editor.this.txt_not_warehouse_cell.setContentText(table2.Rows.get(x).getValue("a", 0).toString());
                }

                for (int y = 0; y < table1.Rows.size(); y++) {
                    pn_so_after_storage_editor.this.txt_item_name_cell.setContentText(table1.Rows.get(y).getValue("item_name", "").toString());
                    pn_so_after_storage_editor.this.txt_item_code_cell.setContentText(table1.Rows.get(y).getValue("item_code", "").toString());
                    pn_so_after_storage_editor.this.txt_customer_name_cell.setContentText(table1.Rows.get(y).getValue("customer_name", "").toString());
                    pn_so_after_storage_editor.this.txt_is_return.setContentText(table1.Rows.get(y).getValue("is_return", "").toString());
                    Date date = table1.Rows.get(y).getValue("create_time", new Date());
                    pn_so_after_storage_editor.this.txt_sh_date_cell.setContentText(date.toString());
                }
            }

        });
    }

    /////
    //提交操作
    ////
    @Override
    public void commit() {
        //将feeder标记成送修状态，并在待维修记录中增加记录

        is_scrap = this.chk_is_scrap.isChecked();
        String sn_number = this.txt_sn_number_cell.getContentText().trim();
        if (sn_number == null || sn_number.length() == 0) {
            App.Current.showError(this.getContext(), "SN信息不能为空！");
            clear();
            return;
        }

        String item_code = this.txt_item_code_cell.getContentText().trim();
        String item_name = this.txt_item_name_cell.getContentText().trim();
        String customer_name = this.txt_customer_name_cell.getContentText().trim();
        String sh_date = this.txt_sh_date_cell.getContentText().trim();
        String user = this.txt_user_cell.getContentText().trim();
        final String pc_number = this.txt_pc_number_cell.getContentText().trim();
        String is_return = this.txt_is_return.getContentText().trim();
        int lcount = txt_batch_cell.getCount();
        if (user == null || user.length() == 0) {
            App.Current.showError(this.getContext(), "收货人员信息不能为空！");
            clear();
            return;
        }
        String comment = this.txt_comment_cell.getContentText().trim();
        if (txt_is_batch_cell.getContentText().equals("") && lcount == 0) {
            String datecode = pn_so_after_storage_editor.this.txt_date_code_cell.getContentText().trim();
            if (datecode == "") {
                App.Current.showError(this.getContext(), "没有填写周期！");
                return;

            }
            String code = this.Parameters.get("code", "");
            Integer oqc_head_id = this.Parameters.get("id", 0);

            String sql = " exec mm_after_storage_isnert_v1 ?,?,?,?,?,?,?,?";
            Parameters p = new Parameters();
            p.add(1, sn_number).add(2, item_code).add(3, item_name).add(4, customer_name).add(5, sh_date).add(6, user).add(7, comment).add(8, oqc_head_id);
            App.Current.DbPortal.ExecuteNonQueryAsync(this.Connector, sql, p, new ResultHandler<Integer>() {
                @Override
                public void handleMessage(Message msg) {
                    Result<Integer> result = this.Value;
                    if (result.HasError) {
                        App.Current.showError(pn_so_after_storage_editor.this.getContext(), result.Error);
                        return;
                    }
                    pn_so_after_storage_editor.this.ProgressDialog.dismiss();


                    App.Current.toastInfo(pn_so_after_storage_editor.this.getContext(), "售后入库登记记录成功");
                    main_pc_number = "";

                    if (is_scrap == true) {
                        //判断是否打印
                        pn_so_after_storage_editor.this.printLabel();
                    }

                    clear();

                }
            });

        } else if (txt_is_batch_cell.getContentText().equals("截断绑定") && lcount != 0) {
            is_batch = this.chk_is_batch.isChecked();
            String date_code = pn_so_after_storage_editor.this.txt_date_code_cell.getContentText().trim();
            pn_so_after_storage_editor.this.txt_sn_number_cell.setContentText("");
            String sql2 = "exec mm_after_storage_isnert_batch ?,?,?";
            Parameters p2 = new Parameters().add(1, main_pc_number).add(2, user).add(3, date_code);
            App.Current.DbPortal.ExecuteNonQueryAsync(this.Connector, sql2, p2, new ResultHandler<Integer>() {
                public void handleMessage(Message msg) {
                    Result<Integer> result = this.Value;
                    if (result.HasError) {
                        App.Current.showError(pn_so_after_storage_editor.this.getContext(), result.Error);
                        return;
                    }
                    App.Current.toastInfo(pn_so_after_storage_editor.this.getContext(), "所有选中物料已生成新的批次。");
                    if (is_batch == true) {
                        //判断是否打印
                        pn_so_after_storage_editor.this.printLabel1();
                    }
                    resultDatatable.Rows.clear();
                    myAdapter = new MyAdapter(resultDatatable);
                    myAdapter.notifyDataSetChanged();
                    pn_so_after_storage_editor.this.txt_batch_cell.setAdapter(myAdapter);
                    main_pc_number = "";
                    is_batch = false;
                    clear();
                }
            });
        } else {
            String sqlsel = "exec mm_after_storage_isnert_select ?";
            Parameters psel = new Parameters().add(1, pc_number);
            App.Current.DbPortal.ExecuteScalarAsync("core_and", sqlsel, psel, new ResultHandler<Object>() {
                public void handleMessage(Message msg) {
                    Result<Object> result = this.Value;
                    if (result.HasError) {
                        App.Current.showError(pn_so_after_storage_editor.this.getContext(), result.Error);
                        return;
                    }
                    result1 = (Integer) result.Value;
                }
            });
            if (result1 >= 1 && lcount == 0) {
                App.Current.showError(this.getContext(), "不能保存空批次！");
                return;
            } else {
                String sql1 = "exec mm_after_storage_isnert_start ?,?,?,?,?,?,?";
                Parameters p1 = new Parameters();
                p1.add(1, pc_number).add(2, sn_number).add(3, item_code).add(4, item_name).add(5, sh_date).add(6, customer_name).add(7, is_return);
                App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql1, p1, new ResultHandler<DataTable>() {
                    public void handleMessage(Message msg) {
                        Result<DataTable> result = this.Value;
                        if (result.HasError) {
                            App.Current.showError(pn_so_after_storage_editor.this.getContext(), result.Error);
                            return;
                        }

                        App.Current.toastInfo(pn_so_after_storage_editor.this.getContext(), "物料进入该待绑批次。");
                        main_pc_number = result.Value.Rows.get(0).getValue("pc_number", "");
                        resultDatatable = result.Value;
                        myAdapter = new MyAdapter(resultDatatable);
                        myAdapter.notifyDataSetChanged();
                        pn_so_after_storage_editor.this.txt_batch_cell.setAdapter(myAdapter);
                        pn_so_after_storage_editor.this.txt_batch_cell.setOnItemClickListener(pn_so_after_storage_editor.this);
                        txt_batch_cell.setOnTouchListener(new OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                switch (motionEvent.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        break;
                                    case MotionEvent.ACTION_MOVE:
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        break;
                                }
                                scrollView.requestDisallowInterceptTouchEvent(true);
                                return false;
                            }
                        });
                    }
                });
            }
        }
    }

    //打印操作
    public void printLabel() {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("sn_numner", this.txt_sn_number_cell.getContentText().trim());
        parameters.put("item_name", this.txt_item_name_cell.getContentText().trim());
        parameters.put("item_code", this.txt_item_code_cell.getContentText().trim());
        parameters.put("customer_name", this.txt_customer_name_cell.getContentText().trim());
        parameters.put("sh_date", this.txt_sh_date_cell.getContentText().trim());
        parameters.put("is_return", this.txt_is_return.getContentText().trim());
        App.Current.Print("mm_item_after_storage_number_label", "打印条码", parameters);

    }

    public void printLabel1() {
        quantity = pn_so_after_storage_editor.this.txt_batch_cell.getCount();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("pc_number", this.txt_pc_number_cell.getContentText().trim());
        parameters.put("customer_name", this.txt_customer_name_cell.getContentText().trim());
        parameters.put("sh_date", this.txt_sh_date_cell.getContentText().trim());
        parameters.put("is_return", this.txt_is_return.getContentText().trim());
        parameters.put("datecode", this.txt_date_code_cell.getContentText().trim());
        parameters.put("quantity", quantity + "");
        parameters.put("item_code", this.txt_item_code_cell.getContentText().trim());
        App.Current.Print("mm_after_storage_barch", "打印条码", parameters);

    }

    //清空数据
    public void clear() {
        this.txt_sn_number_cell.setContentText("");
        this.txt_item_code_cell.setContentText("");
        this.txt_item_name_cell.setContentText("");
        this.txt_customer_name_cell.setContentText("");
        this.txt_sh_date_cell.setContentText("");
        this.txt_not_warehouse_cell.setContentText("");
        this.txt_is_batch_cell.setContentText("");
        this.txt_pc_number_cell.setContentText("");
        this.txt_date_code_cell.setContentText("");
    }

    @Override
    public void onClick(View view) {
//        App.Current.Workbench.onShake();
        String sn_number = txt_sn_number_cell.getContentText().trim();
        String customer_name = txt_customer_name_cell.getContentText().trim();
        String item_code = txt_item_code_cell.getContentText().trim();
        String item_name = txt_item_name_cell.getContentText().trim();
        String sh_date = txt_sh_date_cell.getContentText().trim();
        if (sn_number.equals("")) {
            App.Current.showError(pn_so_after_storage_editor.this.getContext(), "sn编码为空");
            pn_so_after_storage_editor.this.Header.setTitleText("sn编码为空");
            return;
        }
        if (customer_name.equals("")) {
            App.Current.showError(pn_so_after_storage_editor.this.getContext(), "客户信息为空");
            pn_so_after_storage_editor.this.Header.setTitleText("客户信息为空");
            return;
        }
        if (item_code.equals("")) {
            App.Current.showError(pn_so_after_storage_editor.this.getContext(), "物料编码为空");
            pn_so_after_storage_editor.this.Header.setTitleText("物料编码为空");
            return;
        }
        if (item_name.equals("")) {
            App.Current.showError(pn_so_after_storage_editor.this.getContext(), "物料信息为空");
            pn_so_after_storage_editor.this.Header.setTitleText("物料信息为空");
            return;
        }
        switch (view.getId()) {
            case R.id.txt_button:
                App.Current.Workbench.onShake();
                break;
            case R.id.btn_batch_start:
                App.Current.toastInfo(pn_so_after_storage_editor.this.getContext(), "生成空绑定批次号");
                String sqlbbs1 = "exec pn_so_after_storage_editor_batch_start";
                Parameters p1 = new Parameters();
                App.Current.DbPortal.ExecuteRecordAsync("core_and", sqlbbs1, p1, new ResultHandler<DataRow>() {
                    @Override
                    public void handleMessage(Message msg) {
                        Result<DataRow> value = Value;
                        if (value.HasError) {
                            App.Current.showError(pn_so_after_storage_editor.this.getContext(), value.Error);
                            return;
                        } else {
                            String va = value.Value.getValue(0).toString();
                            pn_so_after_storage_editor.this.txt_pc_number_cell.setContentText(va);
                            pn_so_after_storage_editor.this.txt_is_batch_cell.setContentText("等待绑定");
                        }
                    }
                });
                break;
            case R.id.btn_batch_end:
                if (pn_so_after_storage_editor.this.txt_is_batch_cell.getContentText().trim().equals("等待绑定")) {
                    App.Current.toastInfo(pn_so_after_storage_editor.this.getContext(), "截断该绑定批次号");
                    pn_so_after_storage_editor.this.txt_is_batch_cell.setContentText("截断绑定");
                } else {
                    App.Current.showError(pn_so_after_storage_editor.this.getContext(), "不能截断空批次");
                    pn_so_after_storage_editor.this.Header.setTitleText("不能截断空批次");
                    return;
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(pn_so_after_storage_editor.this.getContext());
        TextView title1 = (TextView) view.findViewById(R.id.txt_sn_number_cell);
        String title = title1.getText().toString().trim();
        builder.setTitle("对物料" + title.toString() + ":");
        builder.setMessage("确定从待绑批次删除吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView item_code_textCell = (TextView) view.findViewById(R.id.txt_item_code_cell);
                String item_code = item_code_textCell.getText().toString().trim();
                String pc_number = main_pc_number;
                TextView sn_number_textCell = (TextView) view.findViewById(R.id.txt_sn_number_cell);
                String sn_number = sn_number_textCell.getText().toString().trim();
                String sqldel = "exec pn_so_after_storage_editor_main_delete ?,?,?";
                Parameters pdel = new Parameters().add(1, pc_number).add(2, sn_number).add(3, item_code);
                App.Current.DbPortal.ExecuteDataTableAsync("core_and", sqldel, pdel, new ResultHandler<DataTable>() {
                    @Override
                    public void handleMessage(Message msg) {
                        Result<DataTable> result = Value;
                        if (result.HasError) {
                            App.Current.showError(pn_so_after_storage_editor.this.getContext(), result.Error);
                            return;
                        }
                        resultDatatable = result.Value;
                        myAdapter = new MyAdapter(resultDatatable);
                        myAdapter.notifyDataSetChanged();
                        pn_so_after_storage_editor.this.txt_batch_cell.setAdapter(myAdapter);
                    }
                });

            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
        /*TextView item_code_textCell = (TextView) view.findViewById(R.id.txt_item_code_cell);
        String item_code = item_code_textCell.getText().toString().trim();
        TextView pc_number_textCell = (TextView) view.findViewById(R.id.txt_pc_number_cell);
        String pc_number = pc_number_textCell.getText().toString().trim();
        TextView sn_number_textCell = (TextView) view.findViewById(R.id.txt_sn_number_cell);
        String sn_number = sn_number_textCell.getText().toString().trim();
        String sqldel = "exec pn_so_after_storage_editor_main_delete ?,?,?";
        Parameters pdel = new Parameters().add(1, pc_number).add(2, sn_number).add(3, item_code);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sqldel, pdel, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> result = Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_storage_editor.this.getContext(), result.Error);
                    return;
                }

                myAdapter = new MyAdapter(result.Value);
                myAdapter.notifyDataSetChanged();
            }
        });*/
    }

    class MyAdapter extends BaseAdapter {
        private DataTable mDataTable;

        public MyAdapter(DataTable mDataTable) {
            this.mDataTable = mDataTable;
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = View.inflate(getContext(), R.layout.item_sales_batch_excessive, null);
                viewHolder = new ViewHolder();
                viewHolder.textCellItemCode = (TextView) view.findViewById(R.id.txt_item_code_cell);
                viewHolder.textCellItemName = (TextView) view.findViewById(R.id.txt_item_name_cell);
                viewHolder.textCellsnnumber = (TextView) view.findViewById(R.id.txt_sn_number_cell);
                viewHolder.txtCellshdate = (TextView) view.findViewById(R.id.txt_sh_date_cell);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            String item_code = mDataTable.Rows.get(i).getValue("item_code", "");
            String item_name = mDataTable.Rows.get(i).getValue("item_name", "");
            String sn_number = mDataTable.Rows.get(i).getValue("sn_number", "");
            String sh_date = mDataTable.Rows.get(i).getValue("sh_date", "");
            viewHolder.textCellItemCode.setText(item_code);
            viewHolder.textCellItemName.setText(item_name);
            viewHolder.textCellsnnumber.setText(sn_number);
            viewHolder.txtCellshdate.setText(sh_date);
            return view;
        }
    }


    class ViewHolder {

        private TextView textCellItemCode;
        private TextView textCellItemName;
        private TextView textCellsnnumber;
        private TextView txtCellshdate;
    }
};