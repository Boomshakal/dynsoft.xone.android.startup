package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;

public class pn_so_after_sales_delivery_and extends pn_editor implements OnClickListener {

    public pn_so_after_sales_delivery_and(Context context) {
        super(context);
    }

    public ListView txt_item_main_cell;
    public TextCell txt_customer_name_cell; //客户名称
    public TextCell txt_sn_number_cell;
    public TextCell txt_item_code_cell;
    public TextCell txt_item_name_cell;
    public TextCell txt_quantity_cell;
    public TextCell txt_storage_number_cell;
    public TextCell txt_comment_cell; //备注
    public Button txt_button;
    public Button btn_all_delivery;
    public TextCell txt_code_cell;
    public String code;
    private int sn_items_id;
    private MyAdapter myAdapter;
    public DataTable resultDatatable;
    public DataTable resultDatatable1;
    private ScrollView scrollView;
    private TextCell txt_pc_number_cell;
    private TextCell txt_pc_quantity_cell;
    private String customer_name;
    private String batch_comment;
    private int scan_count;
    private boolean checkbool;
    private DataRow _order_row;
    private DataRow _lot_row;
    private Integer _total = 0;
    private Long _rownum;
    private String work_order_code;

    //设置对于的XML文件
    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(
                R.layout.pn_so_after_sales_delivery_and, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {

        super.onPrepared();

        code = this.Parameters.get("code", "");
        customer_name = this.Parameters.get("customer_name", "");
        scan_count = 0;
        checkbool = true;

        this.txt_code_cell = (TextCell) this
                .findViewById(R.id.txt_code_cell);

        this.txt_customer_name_cell = (TextCell) this
                .findViewById(R.id.txt_customer_name_cell);
        txt_customer_name_cell.setReadOnly();

        this.txt_pc_number_cell = (TextCell) this
                .findViewById(R.id.txt_pc_number_cell);
        txt_pc_number_cell.setReadOnly();
        txt_pc_number_cell.setVisibility(GONE);

        this.txt_sn_number_cell = (TextCell) this
                .findViewById(R.id.txt_sn_number_cell);

        this.txt_item_code_cell = (TextCell) this
                .findViewById(R.id.txt_item_code_cell);

        this.txt_item_name_cell = (TextCell) this
                .findViewById(R.id.txt_item_name_cell);

        this.txt_pc_quantity_cell = (TextCell) this
                .findViewById(R.id.txt_pc_quantity_cell);
        txt_pc_quantity_cell.setVisibility(GONE);


        this.txt_storage_number_cell = (TextCell) this
                .findViewById(R.id.txt_storage_number_cell);
        txt_storage_number_cell.setReadOnly();


        this.txt_quantity_cell = (TextCell) this
                .findViewById(R.id.txt_quantity_cell);
        txt_quantity_cell.setReadOnly();


        this.txt_comment_cell = (TextCell) this
                .findViewById(R.id.txt_comment_cell);

        this.txt_button = (Button) this
                .findViewById(R.id.txt_button);
        txt_button.setOnClickListener(this);
        txt_button.setVisibility(GONE);


        this.btn_all_delivery = (Button) this
                .findViewById(R.id.btn_all_delivery);
        btn_all_delivery.setOnClickListener(this);

        scrollView = (ScrollView) findViewById(R.id.scroll);

        this.txt_item_main_cell = (ListView) this.findViewById(R.id.txt_item_main_cell);
        txt_item_main_cell.setOnTouchListener(new OnTouchListener() {
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


        if (this.txt_customer_name_cell != null) {
            this.txt_customer_name_cell.setLabelText("客户名称");
            this.txt_customer_name_cell.setReadOnly();
        }

        pn_so_after_sales_delivery_and.this.resultDatatable1 = null;

        if (this.txt_sn_number_cell != null) {
            this.txt_sn_number_cell.setLabelText("sn编码");
            this.txt_sn_number_cell.setReadOnly();
        }


        if (this.txt_item_code_cell != null) {
            this.txt_item_code_cell.setLabelText("物料编码");
            this.txt_item_code_cell.setReadOnly();
        }

        if (this.txt_pc_number_cell != null) {
            this.txt_pc_number_cell.setLabelText("批次号");
        }

        if (this.txt_item_name_cell != null) {
            this.txt_item_name_cell.setLabelText("物料名称");
            this.txt_item_name_cell.setReadOnly();
        }

        if (this.txt_pc_quantity_cell != null) {
            this.txt_pc_quantity_cell.setLabelText("批次所含数量");
            this.txt_pc_quantity_cell.setReadOnly();
        }

        if (this.txt_quantity_cell != null) {
            this.txt_quantity_cell.setLabelText("剩余未发数量");
            this.txt_quantity_cell.setReadOnly();
        }


        if (this.txt_storage_number_cell != null) {
            this.txt_storage_number_cell.setLabelText("所在储位");
            this.txt_customer_name_cell.setReadOnly();
        }


        if (this.txt_comment_cell != null) {
            this.txt_comment_cell.setLabelText("备注");
        }

        if (this.txt_code_cell != null) {
            this.txt_code_cell.setLabelText("单号");
            this.txt_code_cell.setContentText(code);
        }

        String sql = "exec pn_so_after_sales_delivery_and_select_3 ?";
        String code1 = pn_so_after_sales_delivery_and.this.code;
        Parameters p = new Parameters().add(1, code1);
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_delivery_and.this.ProgressDialog.dismiss();

                Result<DataTable> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), result.Error);
                    return;
                }
                DataTable row = result.Value;
                if (row == null) {
                    App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), "该单待发物料为空");

                    pn_so_after_sales_delivery_and.this.Header.setTitleText("该单待发物料为空");
                    clear();
                    return;
                }
                resultDatatable = result.Value;
                Log.e("len", "RESULT:::" + resultDatatable.Rows.size());
                myAdapter = new MyAdapter(resultDatatable);
                myAdapter.notifyDataSetChanged();
                pn_so_after_sales_delivery_and.this.txt_item_main_cell.setAdapter(pn_so_after_sales_delivery_and.this.myAdapter);

            }
        });
    }
    //扫条码
    @Override
    public void onScan(final String barcode) {
        final String bar_code = barcode.trim();
        if (bar_code.equals("")) {
            App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), "没有填写sn编码");

            pn_so_after_sales_delivery_and.this.Header.setTitleText("没有填写sn编码");
            return;
        } else if (bar_code.startsWith("CRQ:")) {
            if (bar_code.length() < 4) {
                App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), "sn编码非法");

                pn_so_after_sales_delivery_and.this.Header.setTitleText("sn编码非法");
                return;
            }
            String pc_number = bar_code.substring(4, 23);
            pn_so_after_sales_delivery_and.this.txt_pc_number_cell.setContentText(pc_number.trim());
            loadItem1(pc_number);
        } else {
            pn_so_after_sales_delivery_and.this.txt_sn_number_cell.setContentText(bar_code);
            loadItem(bar_code);
        }
    }


    ///根据SN编号带出物料编码和物料名称
    public void loadItem(String code) {
        this.ProgressDialog.show();
        if (pn_so_after_sales_delivery_and.this.resultDatatable1 != null && pn_so_after_sales_delivery_and.this.resultDatatable1.Rows.size() > 0) {
            pn_so_after_sales_delivery_and.this.resultDatatable1.Rows.clear();
        }
        String sql1 = "exec pn_so_after_sales_delivery_and_select_1 ?,?";//已经完成
        String sql2 = "exec pn_so_after_sales_delivery_and_select_3 ?";
        String code1 = pn_so_after_sales_delivery_and.this.code;
        Parameters p1 = new Parameters().add(1, code).add(2, code1);
        Parameters p2 = new Parameters().add(1, code1);
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql1, p1, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_delivery_and.this.ProgressDialog.dismiss();

                Result<DataTable> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), result.Error);
                    return;
                }
                if (result.Value != null && result.Value.Rows.size() == 0) {
                    App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), "系统找不到该sn编码物料");

                    pn_so_after_sales_delivery_and.this.Header.setTitleText("系统找不到sn编码物料");
                    clear();
                    return;
                } else {
                    String item_code = result.Value.Rows.get(0).getValue("item_code").toString().trim();
                    pn_so_after_sales_delivery_and.this.txt_item_code_cell.setContentText(item_code);
                    String item_name = result.Value.Rows.get(0).getValue("item_name").toString().trim();
                    pn_so_after_sales_delivery_and.this.txt_item_name_cell.setContentText(item_name);
                    String customer_name = result.Value.Rows.get(0).getValue("customer_name").toString().trim();
                    pn_so_after_sales_delivery_and.this.txt_customer_name_cell.setContentText(customer_name);
                    int quantity = result.Value.Rows.get(0).getValue("quantity", 0);
                    pn_so_after_sales_delivery_and.this.txt_quantity_cell.setContentText(String.valueOf(quantity));
                    String storage_number = result.Value.Rows.get(0).getValue("storage_number").toString().trim();
                    pn_so_after_sales_delivery_and.this.txt_storage_number_cell.setContentText(storage_number);
                    pn_so_after_sales_delivery_and.this.sn_items_id = (Integer) result.Value.Rows.get(0).getValue("id");
                }
            }
        });
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql2, p2, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_delivery_and.this.ProgressDialog.dismiss();

                Result<DataTable> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), result.Error);
                    return;
                }
                DataTable row = result.Value;

                if (row == null) {
                    App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), "该单待发物料为空");

                    pn_so_after_sales_delivery_and.this.Header.setTitleText("该单待发物料为空");
                    clear();
                    return;
                }
                pn_so_after_sales_delivery_and.this.myAdapter = new MyAdapter(result.Value);
                pn_so_after_sales_delivery_and.this.txt_item_main_cell.setAdapter(myAdapter);
            }
        });
    }

    //根据批次带出一批物料
    public void loadItem1(final String code) {
        this.ProgressDialog.show();
        Log.e("len", "result:::1111111");
        String sql = "exec pn_so_after_sales_delivery_and_select_4 ?,?";
        String sql1 = "exec pn_so_after_sales_delivery_and_select_5 ?";
        String code1 = pn_so_after_sales_delivery_and.this.code;
        Parameters p1 = new Parameters().add(1, code);
        Parameters p = new Parameters().add(1, code).add(2, code1);
        Log.e("len", code + "****" + code1);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_delivery_and.this.ProgressDialog.dismiss();

                Result<DataRow> result = Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), result.Error);
                    return;
                }
                if (result.Value == null) {
                    App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), "系统没有找到该批次");
                    pn_so_after_sales_delivery_and.this.Header.setTitleText("系统没有找到该批次");
                    return;
                }
                String is_return = result.Value.getValue("is_return", "").trim();
                Log.e("len", code + "IS_RETURN:" + is_return);
                if (result.Value.getValue("is_return","").trim().equals("NO")){
                  App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), "该批次已登记不返还");
                  pn_so_after_sales_delivery_and.this.Header.setTitleText("该批次已登记不返还");
                  return;
                }
                if (!(result.Value.getValue("customer_name", "").trim().equals(customer_name))) {
                    App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), "批次客户不是发货客户");
                    pn_so_after_sales_delivery_and.this.Header.setTitleText("批次客户不是发货客户");
                    return;
                }
                int pc_quantity = result.Value.getValue("quantity", 0);
                pn_so_after_sales_delivery_and.this.txt_pc_quantity_cell.setContentText(String.valueOf(pc_quantity));
                pn_so_after_sales_delivery_and.this.txt_storage_number_cell.setContentText(result.Value.getValue("storage_number", ""));
                pn_so_after_sales_delivery_and.this.txt_customer_name_cell.setContentText(result.Value.getValue("customer_name", ""));
                pn_so_after_sales_delivery_and.this.txt_item_name_cell.setContentText(result.Value.getValue("item_name", ""));
                pn_so_after_sales_delivery_and.this.txt_item_code_cell.setContentText(result.Value.getValue("item_code", ""));
                int all_quantity = result.Value.getValue("all_quantity", 0);
                pn_so_after_sales_delivery_and.this.txt_quantity_cell.setContentText(String.valueOf(all_quantity));
                pn_so_after_sales_delivery_and.this.txt_comment_cell.setContentText(result.Value.getValue("comment", ""));
                txt_pc_number_cell.setVisibility(VISIBLE);
                txt_pc_quantity_cell.setVisibility(VISIBLE);
            }
        });
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql1, p1, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_delivery_and.this.ProgressDialog.dismiss();

                Result<DataTable> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), result.Error);
                    return;
                }
                if (result.Value != null && result.Value.Rows.size() == 0) {
                    App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), "该批次没有物料");
                    pn_so_after_sales_delivery_and.this.Header.setTitleText("该批次没有物料");
                    clear();
                    pn_so_after_sales_delivery_and.this.txt_pc_number_cell.setContentText("");
                    return;
                }
                if (result.Value == null) {
                    App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), "系统没有查询到物料信息");
                    pn_so_after_sales_delivery_and.this.Header.setTitleText("系统没有查询到物料信息");
                    clear();
                    pn_so_after_sales_delivery_and.this.txt_pc_number_cell.setContentText("");
                    return;
                } else {
                    for (int x = 0;x<result.Value.Rows.size();x++){
                        Log.e("len", code + "xxxxxxxws:" + String.valueOf(x)+"ssss"+String.valueOf(result.Value.Rows.get(x).getValue("sn_items_id",0)));
                    }
                    resultDatatable1 = result.Value;
                }
            }
        });
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
                view = View.inflate(getContext(), R.layout.delivery_and_info, null);
                viewHolder = new ViewHolder();
                viewHolder.textCellSnNumber = (TextView) view.findViewById(R.id.txt_sn_number_cell);
                viewHolder.textCellItemCode = (TextView) view.findViewById(R.id.txt_item_code_cell);
                viewHolder.textCellItemName = (TextView) view.findViewById(R.id.txt_item_name_cell);
                viewHolder.textCellStorageNumber = (TextView) view.findViewById(R.id.txt_storage_number_cell);
                viewHolder.textCellSnItemsId = (TextView) view.findViewById(R.id.txt_sn_items_id_cell);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            String sn_number = mDataTable.Rows.get(i).getValue("sn_number", "");
            String item_code = mDataTable.Rows.get(i).getValue("item_code", "");
            String item_name = mDataTable.Rows.get(i).getValue("item_name", "");
            String storage_number = mDataTable.Rows.get(i).getValue("storage_number", "");
            String sn_items_id = String.valueOf(mDataTable.Rows.get(i).getValue("sn_items_id", 0));
            viewHolder.textCellSnNumber.setText(sn_number);
            viewHolder.textCellItemCode.setText(item_code);
            viewHolder.textCellItemName.setText(item_name);
            viewHolder.textCellStorageNumber.setText(storage_number);
            viewHolder.textCellSnItemsId.setText(sn_items_id);
            return view;
        }
    }


    class ViewHolder {
        private TextView textCellSnNumber;
        private TextView textCellItemCode;
        private TextView textCellItemName;
        private TextView textCellStorageNumber;
        private TextView textCellSnItemsId;
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_all_delivery:

                AlertDialog.Builder builder = new AlertDialog.Builder(pn_so_after_sales_delivery_and.this.getContext());
                builder.setTitle("剩余未发货： " + pn_so_after_sales_delivery_and.this.txt_quantity_cell.getContentText().toString() + "  确定结束发货吗");
                builder.setMessage("剩余未发货： " + pn_so_after_sales_delivery_and.this.txt_quantity_cell.getContentText().toString() + "  确定结束发货吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String code1 = pn_so_after_sales_delivery_and.this.code;
                        String sqldel = "exec pn_so_after_sales_delivery_and_finish ?";
                        Parameters pdel = new Parameters().add(1, code1);
                        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sqldel, pdel, new ResultHandler<Integer>() {
                            @Override
                            public void handleMessage(Message msg) {
                                Result<Integer> result = this.Value;
                                if (result.HasError) {
                                    App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), result.Error);
                                    return;
                                }
                                App.Current.toastInfo(pn_so_after_sales_delivery_and.this.getContext(), "已经结束该单发货。");
                                pn_so_after_sales_delivery_and.this.txt_sn_number_cell.setContentText("");
                                pn_so_after_sales_delivery_and.this.txt_item_code_cell.setContentText("");
                                pn_so_after_sales_delivery_and.this.txt_item_name_cell.setContentText("");
                                pn_so_after_sales_delivery_and.this.txt_customer_name_cell.setContentText("");
                                pn_so_after_sales_delivery_and.this.txt_comment_cell.setContentText("");
                                pn_so_after_sales_delivery_and.this.txt_storage_number_cell.setContentText("");
                                pn_so_after_sales_delivery_and.this.txt_code_cell.setContentText("");
                                pn_so_after_sales_delivery_and.this.txt_quantity_cell.setContentText("");
                                pn_so_after_sales_delivery_and.this.txt_pc_quantity_cell.setContentText("");
                                txt_pc_number_cell.setVisibility(GONE);
                                txt_pc_quantity_cell.setVisibility(GONE);
                                resultDatatable.Rows.clear();
                                myAdapter = new MyAdapter(resultDatatable);
                                myAdapter.notifyDataSetChanged();
                                pn_so_after_sales_delivery_and.this.txt_item_main_cell.setAdapter(myAdapter);
                                pn_so_after_sales_delivery_and.this.code = "";
                            }
                        });
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();

                break;

            case R.id.txt_button:
                App.Current.Workbench.onShake();
                break;
        }
    }

    @Override
    public void commit() {
        if (resultDatatable1 != null) {
            ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
            for (DataRow rw : resultDatatable1.Rows) {
                Map<String, String> entry = new HashMap<String, String>();
                entry.put("sn_number", String.valueOf(rw.getValue("sn_number_code", "").trim()));
                entry.put("sn_items_id", String.valueOf(rw.getValue("sn_items_id", "")));
                entry.put("comment", pn_so_after_sales_delivery_and.this.txt_comment_cell.getContentText().trim());
                entry.put("code", pn_so_after_sales_delivery_and.this.code);
                entry.put("pc_number", pn_so_after_sales_delivery_and.this.txt_pc_number_cell.getContentText().trim());
                entries.add(entry);
            }
            String xml = XmlHelper.createXml("so_returns", null, null, "so_return", entries);
            Log.e("len", "result:::" + xml);
            String sqlb = "exec mm_after_delivery_head_add_batch_PDA_20190111 ?";
            Parameters pb = new Parameters().add(1, xml);
            App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sqlb, pb, new ResultHandler<DataRow>() {
                @Override
                public void handleMessage(Message msg) {
                    pn_so_after_sales_delivery_and.this.ProgressDialog.dismiss();

                    Result<DataRow> result = this.Value;
                    if (result.HasError) {
                        App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(),result.Error);
                        return;
                    }
                    int x = result.Value.getValue("quantity",0);
                    if (x == 0){
                        App.Current.toastInfo(pn_so_after_sales_delivery_and.this.getContext(), "已经结束该单发货。");
                        exists();
                    }else{App.Current.toastInfo(pn_so_after_sales_delivery_and.this.getContext(), "该批次物料已发货。");
                        pn_so_after_sales_delivery_and.this.resultDatatable1.Rows.clear();
                        pn_so_after_sales_delivery_and.this.txt_pc_number_cell.setContentText("");
                        pn_so_after_sales_delivery_and.this.txt_pc_quantity_cell.setContentText("");
                        pn_so_after_sales_delivery_and.this.txt_quantity_cell.setContentText(String.valueOf(x));
                        clear1();
                    }
                }
            });
            return;
        }

        String sn_number = this.txt_sn_number_cell.getContentText().trim();
        String comment = this.txt_comment_cell.getContentText().trim();
        if (sn_number.equals("")) {
            App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), "没有填写sn编码");
            pn_so_after_sales_delivery_and.this.Header.setTitleText("没有填写sn编码");
            return;
        }
        String sql = "exec mm_after_delivery_head_add_PDA ?,?,?,?";
        Parameters p = new Parameters();
        p.add(1, sn_number).add(2, comment).add(3, pn_so_after_sales_delivery_and.this.code).add(4, pn_so_after_sales_delivery_and.this.sn_items_id);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_delivery_and.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), result.Error);
                    return;
                }
                DataRow row = result.Value;
                int quantity = row.getValue("quantity", 0);
                if (quantity == 0){
                    exists();
                }
                else{
                pn_so_after_sales_delivery_and.this.txt_quantity_cell.setContentText(String.valueOf(quantity));
                App.Current.toastInfo(pn_so_after_sales_delivery_and.this.getContext(), "该物料已发货。");
                clear();}
            }
        });
    }

    public  void exists (){
        String code1 = pn_so_after_sales_delivery_and.this.code;
        String sqldel = "exec pn_so_after_sales_delivery_and_finish ?";
        Parameters pdel = new Parameters().add(1, code1);
        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sqldel, pdel, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                Result<Integer> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), result.Error);
                    return;
                }
                App.Current.toastInfo(pn_so_after_sales_delivery_and.this.getContext(), "已经结束该单发货。");
                pn_so_after_sales_delivery_and.this.txt_sn_number_cell.setContentText("");
                pn_so_after_sales_delivery_and.this.txt_item_code_cell.setContentText("");
                pn_so_after_sales_delivery_and.this.txt_item_name_cell.setContentText("");
                pn_so_after_sales_delivery_and.this.txt_customer_name_cell.setContentText("");
                pn_so_after_sales_delivery_and.this.txt_comment_cell.setContentText("");
                pn_so_after_sales_delivery_and.this.txt_storage_number_cell.setContentText("");
                pn_so_after_sales_delivery_and.this.txt_code_cell.setContentText("");
                pn_so_after_sales_delivery_and.this.txt_quantity_cell.setContentText("");
                pn_so_after_sales_delivery_and.this.txt_pc_quantity_cell.setContentText("");
                txt_pc_number_cell.setVisibility(GONE);
                txt_pc_quantity_cell.setVisibility(GONE);
                resultDatatable.Rows.clear();
                myAdapter = new MyAdapter(resultDatatable);
                myAdapter.notifyDataSetChanged();
                pn_so_after_sales_delivery_and.this.txt_item_main_cell.setAdapter(myAdapter);
                pn_so_after_sales_delivery_and.this.code = "";
            }
        });
    }
    //清空数据
    public void clear() {
        this.txt_sn_number_cell.setContentText("");
        this.txt_item_code_cell.setContentText("");
        this.txt_item_name_cell.setContentText("");
        this.txt_customer_name_cell.setContentText("");
        this.txt_comment_cell.setContentText("");
        this.txt_storage_number_cell.setContentText("");

        String sql = "exec pn_so_after_sales_delivery_and_select_3 ?";
        Parameters p = new Parameters().add(1, pn_so_after_sales_delivery_and.this.code);
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_delivery_and.this.ProgressDialog.dismiss();

                Result<DataTable> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), result.Error);
                    return;
                }
                DataTable row = result.Value;
                pn_so_after_sales_delivery_and.this.myAdapter = new MyAdapter(result.Value);
                pn_so_after_sales_delivery_and.this.txt_item_main_cell.setAdapter(myAdapter);
            }
        });
    }

    public void clear1() {
        this.txt_sn_number_cell.setContentText("");
        this.txt_item_code_cell.setContentText("");
        this.txt_item_name_cell.setContentText("");
        this.txt_comment_cell.setContentText("");
        this.txt_storage_number_cell.setContentText("");
        String sql = "exec pn_so_after_sales_delivery_and_select_3 ?";
        Parameters p = new Parameters().add(1, pn_so_after_sales_delivery_and.this.code);
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_delivery_and.this.ProgressDialog.dismiss();

                Result<DataTable> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_sales_delivery_and.this.getContext(), result.Error);
                    return;
                }
                DataTable row = result.Value;
                pn_so_after_sales_delivery_and.this.myAdapter = new MyAdapter(result.Value);
                pn_so_after_sales_delivery_and.this.txt_item_main_cell.setAdapter(myAdapter);
            }
        });
    }

}


