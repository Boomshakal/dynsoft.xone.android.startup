package dynsoft.xone.android.wms;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

public class pn_so_after_maintenance_mgr extends pn_editor implements OnClickListener {

    public pn_so_after_maintenance_mgr(Context context) {
        super(context);
    }


    /*public TextCell txt_sn_number_cell; // sn编码
	public TextCell txt_item_code_cell; //物料编码
	public TextCell txt_item_name_cell; //物料名称*/
    public MyAdapter myAdapter;
    public ListView txt_item_main_cell;
    private Button txt_button;
    public TextCell txt_sn_number_cell;
    public TextCell txt_customer_name_cell; //客户名称
    public TextCell txt_item_code_cell;
    public TextCell txt_item_name_cell;
    public TextCell txt_code_cell;
    public String rs1 = "";
    private String sn_number;
    private int scan_count;
    private boolean checkbool;
    private DataRow _order_row;
    private DataRow _lot_row;
    private Integer _total = 0;
    private Long _rownum;
    private String work_order_code;
    private DataTable mDataTable;

    //设置对于的XML文件
    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(
                R.layout.pn_so_after_scanning, this, true);
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

        this.txt_button = (Button) this
                .findViewById(R.id.txt_button);
        txt_button.setOnClickListener(this);
        txt_button.setVisibility(GONE);

        this.txt_item_name_cell = (TextCell) this
                .findViewById(R.id.txt_item_name_cell);

        this.txt_item_code_cell = (TextCell) this
                .findViewById(R.id.txt_item_code_cell);
        txt_item_code_cell.setReadOnly();
        this.txt_customer_name_cell = (TextCell) this
                .findViewById(R.id.txt_customer_name_cell);
        txt_customer_name_cell.setReadOnly();

        this.txt_code_cell = (TextCell) this
                .findViewById(R.id.txt_code_cell);
        txt_code_cell.setReadOnly();

        this.txt_item_main_cell = (ListView) this.findViewById(R.id.txt_item_main_cell);

        if (this.txt_customer_name_cell != null) {
            this.txt_customer_name_cell.setLabelText("客户名称");
            this.txt_customer_name_cell.setReadOnly();
        }

        if (this.txt_item_name_cell != null) {
            this.txt_item_name_cell.setLabelText("物料名称");
            this.txt_item_name_cell.setReadOnly();
        }
        if (this.txt_item_code_cell != null) {
            this.txt_item_code_cell.setLabelText("物料编码");
            this.txt_item_code_cell.setReadOnly();
        }
        if (this.txt_sn_number_cell != null) {
            this.txt_sn_number_cell.setLabelText("sn编码");
            this.txt_sn_number_cell.setReadOnly();
        }

        if (this.txt_code_cell != null) {
            this.txt_code_cell.setLabelText("收货单号");
            this.txt_code_cell.setReadOnly();
        }

    }

    //扫条码
    @Override
    public void onScan(final String barcode) {
        String bar_code = barcode.trim();
        this.txt_sn_number_cell.setContentText(bar_code.toString());
        this.loadItem(bar_code);
    }


    ///根据SN编号带出物料编码和物料名称
    public void loadItem(String code) {
        this.ProgressDialog.show();
        String sql = "exec pn_so_after_scanning_load ?";
        String sql1 = "exec pn_so_after_scanning_load2 ?";
        Parameters p = new Parameters().add(1, code);
        Parameters p1 = new Parameters().add(1, code);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_maintenance_mgr.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_maintenance_mgr.this.getContext(), result.Error);
                    return;
                }

                DataRow row = result.Value;
                if (row == null) {
                    App.Current.showError(pn_so_after_maintenance_mgr.this.getContext(), "找不到该物料的对应的信息");

                    pn_so_after_maintenance_mgr.this.Header.setTitleText("找不到该物料的对应的信息");
                    clear();
                    return;
                }
				/*pn_so_after_sales_batch_show.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
				pn_so_after_sales_batch_show.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));*/
                pn_so_after_maintenance_mgr.this.txt_customer_name_cell.setContentText(row.getValue("customer_name", ""));
                pn_so_after_maintenance_mgr.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
                pn_so_after_maintenance_mgr.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
                pn_so_after_maintenance_mgr.this.txt_sn_number_cell.setContentText(row.getValue("sn_number", ""));
                pn_so_after_maintenance_mgr.this.txt_code_cell.setContentText(row.getValue("code", ""));
                //pn_so_after_storage_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
            }
        });

        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql1, p1, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_maintenance_mgr.this.ProgressDialog.dismiss();

                Result<DataTable> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_maintenance_mgr.this.getContext(), result.Error);
                    return;
                }
                if (result.Value == null) {
                    App.Current.showError(pn_so_after_maintenance_mgr.this.getContext(), "系统没有找到该物料的待检记录");

                    pn_so_after_maintenance_mgr.this.Header.setTitleText("系统没有找到该物料的待检记录");
                    clear();
                    return;
                }

                myAdapter = new MyAdapter(result.Value);
                pn_so_after_maintenance_mgr.this.txt_item_main_cell.setAdapter(myAdapter);
				/*pn_so_after_sales_batch_show.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
				pn_so_after_sales_batch_show.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));*/
                //mDataTable = result.Value;
                //myAdapter = new MyAdapter(mDataTable);
                //txt_item_main_cell.setAdapter(myAdapter);
                //pn_so_after_storage_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));


            }
        });
    }

    @Override
    public void commit() {

        sn_number = this.txt_sn_number_cell.getContentText();
        if (TextUtils.isEmpty(sn_number)) {
            App.Current.showError(pn_so_after_maintenance_mgr.this.getContext(), "请扫描物料");
            pn_so_after_maintenance_mgr.this.Header.setTitleText("请扫描物料");
            return;
        }

        final String sn_number = this.txt_sn_number_cell.getContentText().trim();
        String sql = "exec pn_so_after_sales_scanning_delete ?";
        Parameters p = new Parameters();
        p.add(1, sn_number);
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_maintenance_mgr.this.ProgressDialog.dismiss();
                Result<DataTable> result = this.Value;

                if (result.HasError) {
                    App.Current.showError(pn_so_after_maintenance_mgr.this.getContext(), result.Error);
                    return;
                }
                if (result.Value == null) {
                    App.Current.showError(pn_so_after_maintenance_mgr.this.getContext(), "没有返回物料");

                    pn_so_after_maintenance_mgr.this.Header.setTitleText("没有返回物料");
                    clear();
                    return;
                }
                for (int i = 0; i < result.Value.Rows.size(); i++) {
                    if (result.Value.Rows.size()<0){
                        return;
                    }
                    DataRow rs = result.Value.Rows.get(i);
                    rs1 = rs.getValue("sn_number").toString();
                }
                pn_so_after_maintenance_mgr.this.ProgressDialog.dismiss();
                if (rs1.equals("")) {
                    App.Current.toastInfo(pn_so_after_maintenance_mgr.this.getContext(), "该批次已经全部检查");
                    clear();
                    return;
                } else {
                    App.Current.toastInfo(pn_so_after_maintenance_mgr.this.getContext(), "物料完成检查");
                }
                clear();
            }
        });
    }

    //清空数据
    public void clear() {

        this.txt_customer_name_cell.setContentText("");
        this.txt_item_code_cell.setContentText("");
        this.txt_item_name_cell.setContentText("");
        this.txt_code_cell.setContentText("");
        if (!(rs1.equals(""))) {
            String sql3 = "exec pn_so_after_scanning_load2 ?";
            Parameters p3 = new Parameters().add(1, rs1);
            App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql3, p3, new ResultHandler<DataTable>() {
                @Override
                public void handleMessage(Message msg) {
                    pn_so_after_maintenance_mgr.this.ProgressDialog.dismiss();

                    Result<DataTable> result = this.Value;
                    if (result.HasError) {
                        App.Current.showError(pn_so_after_maintenance_mgr.this.getContext(), result.Error);
                        return;
                    }
                    if (result.Value == null) {
                        App.Current.showError(pn_so_after_maintenance_mgr.this.getContext(), "找不到该料号的对应的信息");

                        pn_so_after_maintenance_mgr.this.Header.setTitleText("找不到该料号号的对应的信息");
                        clear();
                        return;
                    }
                    myAdapter =new MyAdapter(result.Value);
                    pn_so_after_maintenance_mgr.this.txt_item_main_cell.setAdapter(myAdapter);


				/*pn_so_after_sales_batch_show.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
				pn_so_after_sales_batch_show.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));*/

                    //pn_so_after_storage_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
                }
            });
        }

        this.txt_sn_number_cell.setContentText("");
    }

    @Override
    public void onClick(View view) {
        App.Current.Workbench.onShake();
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
                view = View.inflate(getContext(), R.layout.item_sales_so_after_scanning, null);
                viewHolder = new ViewHolder();
                viewHolder.textCellItemCode = (TextView) view.findViewById(R.id.txt_item_code_cell);
                viewHolder.textCellItemName = (TextView) view.findViewById(R.id.txt_item_name_cell);
                viewHolder.textCellSnNumber = (TextView) view.findViewById(R.id.txt_sn_number_cell);
                viewHolder.textCellCustomerName = (TextView) view.findViewById(R.id.txt_customer_name_cell);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            String item_code = mDataTable.Rows.get(i).getValue("item_code", "");
            String item_name = mDataTable.Rows.get(i).getValue("item_name", "");
            String sn_number = mDataTable.Rows.get(i).getValue("sn_number", "");
            String customer_name = mDataTable.Rows.get(i).getValue("customer_name", "");
            viewHolder.textCellItemCode.setText(item_code);
            viewHolder.textCellItemName.setText(item_name);
            viewHolder.textCellSnNumber.setText(sn_number);
            viewHolder.textCellCustomerName.setText(customer_name);
            return view;
        }
    }


    class ViewHolder {
        private TextView textCellSnNumber;
        private TextView textCellItemCode;
        private TextView textCellItemName;
        private TextView textCellCustomerName;
    }

}


