package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * 售后待发货新
 */

public class pn_so_after_sales_delivery_and_new extends pn_editor {

    private int line_id;
    private int counts;

    public pn_so_after_sales_delivery_and_new(Context context) {
        super(context);
    }


    public TextCell txt_code_cell;
    public TextCell txt_customer_name_cell;
    public TextCell txt_sn_number_cell;
    public TextCell txt_sn_number_display_cell;
    public TextCell txt_pc_number_cell;
    public TextCell txt_item_code_cell;
    public TextCell txt_item_name_cell;
    public TextCell txt_quantity_cell;
    public TextCell txt_sh_date_cell;
    public TextCell txt_type_cell;
    public TextCell txt_comment_cell;
    public ImageButton btn_prev;
    public ImageButton btn_next;

    public String code;
    public String customer_name;
    public String item_code;
    public String item_name;
    public String fh_type;
    public String quantity;
    public String sh_date;
    public String sn_number;

    // @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(
                R.layout.pn_so_after_sales_delivery_and_newl, this, true);
        view.setLayoutParams(lp);
    }

    //设置显示控件
    //@Override
    public void onPrepared() {

        super.onPrepared();

        code = this.Parameters.get("code", "");
        customer_name = this.Parameters.get("customer_name", "");
//        fh_type = this.Parameters.get("fh_type", "");
//        item_code = this.Parameters.get("item_code", "");
//        item_name = this.Parameters.get("item_name", "");
//        quantity = this.Parameters.get("qty", 0).toString();
//        sh_date = this.Parameters.get("sh_date", "");

        this.btn_prev = (ImageButton) this.findViewById(R.id.btn_prev);
        this.btn_next = (ImageButton) this.findViewById(R.id.btn_next);

        this.txt_code_cell = (TextCell) this
                .findViewById(R.id.txt_code_cell);
        if (this.txt_code_cell != null) {
            this.txt_code_cell.setLabelText("发货单号");
            this.txt_code_cell.setReadOnly();
            this.txt_code_cell.setContentText(code);
        }

        this.txt_customer_name_cell = (TextCell) this
                .findViewById(R.id.txt_customer_name_cell);
        if (this.txt_customer_name_cell != null) {
            this.txt_customer_name_cell.setLabelText("客户名称");
            this.txt_customer_name_cell.setReadOnly();
            this.txt_customer_name_cell.setContentText(customer_name);
        }

        this.txt_type_cell = (TextCell) this
                .findViewById(R.id.txt_type_cell);
        if (this.txt_type_cell != null) {
            this.txt_type_cell.setLabelText("发货类型");
            this.txt_type_cell.setReadOnly();
        }

        this.txt_sh_date_cell = (TextCell) this
                .findViewById(R.id.txt_sh_date_cell);
        if (this.txt_sh_date_cell != null) {
            this.txt_sh_date_cell.setLabelText("收货日期");
            this.txt_sh_date_cell.setReadOnly();
        }


        this.txt_item_code_cell = (TextCell) this
                .findViewById(R.id.txt_item_code_cell);
        if (this.txt_item_code_cell != null) {
            this.txt_item_code_cell.setLabelText("物料编码");
            this.txt_item_code_cell.setReadOnly();
        }

        this.txt_sn_number_display_cell = (TextCell) this
                .findViewById(R.id.txt_sn_number_display_cell);
        if (this.txt_sn_number_display_cell != null) {
            this.txt_sn_number_display_cell.setLabelText("SN_NO集");
            this.txt_sn_number_display_cell.setReadOnly();
        }

        this.txt_item_name_cell = (TextCell) this
                .findViewById(R.id.txt_item_name_cell);
        if (this.txt_item_name_cell != null) {
            this.txt_item_name_cell.setLabelText("物料名称");
            this.txt_item_name_cell.setReadOnly();
        }

        this.txt_quantity_cell = (TextCell) this
                .findViewById(R.id.txt_quantity_cell);
        if (this.txt_quantity_cell != null) {
            this.txt_quantity_cell.setLabelText("发货数量");
            this.txt_quantity_cell.setReadOnly();
        }

        this.txt_sn_number_cell = (TextCell) this
                .findViewById(R.id.txt_sn_number_cell);
        if (this.txt_sn_number_cell != null) {
            this.txt_sn_number_cell.setLabelText("条码");
            this.txt_sn_number_cell.setReadOnly();
        }
        this.txt_pc_number_cell = (TextCell) this
                .findViewById(R.id.txt_pc_number_cell);
        if (this.txt_pc_number_cell != null) {
            this.txt_pc_number_cell.setLabelText("批次");
            this.txt_pc_number_cell.setReadOnly();
        }

        this.txt_comment_cell = (TextCell) this
                .findViewById(R.id.txt_comment_cell);
        if (this.txt_comment_cell != null) {
            this.txt_comment_cell.setLabelText("备注");
            this.txt_comment_cell.setReadOnly();
        }

        if (this.btn_prev != null) {
            this.btn_prev.setImageBitmap(App.Current.ResourceManager.getImage("@/core_prev_white"));
            this.btn_prev.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_so_after_sales_delivery_and_new.this.prev();
                }
            });
        }
        if (this.btn_next != null) {
            this.btn_next.setImageBitmap(App.Current.ResourceManager.getImage("@/core_next_white"));
            this.btn_next.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_so_after_sales_delivery_and_new.this.next();
                }
            });
        }

        line_id = 1;
        loadItem(line_id);
    }

    private void loadItem(int i) {
        String sql = "exec fm_so_after_sales_delivery_and_new_load_item ?,?,?";
        Parameters p = new Parameters().add(1, code).add(2, customer_name).add(3, i);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                } else if (value.Value != null) {
                    fh_type = value.Value.getValue("fh_type", "");
                    item_code = value.Value.getValue("item_code", "");
                    item_name = value.Value.getValue("item_name", "");
                    quantity = value.Value.getValue("qty", 0).toString();
                    sh_date = value.Value.getValue("sh_date", "");
                    counts = value.Value.getValue("counts", 0);
                    sn_number = value.Value.getValue("sn_number", "");
                    txt_type_cell.setContentText(fh_type);
                    txt_sh_date_cell.setContentText(sh_date);
                    txt_item_code_cell.setContentText(item_code);
                    txt_item_name_cell.setContentText(item_name);
                    txt_quantity_cell.setContentText(quantity);
                    txt_sn_number_display_cell.setContentText(sn_number);
                }
            }
        });
    }

    public void prev() {
        if (line_id > 1) {
            line_id -= 1;
            this.loadItem(line_id);
        } else {
            App.Current.showError(pn_so_after_sales_delivery_and_new.this.getContext(), "已经是第一条。");
        }
    }

    public void next() {
        if (line_id < counts) {
            line_id += 1;
            this.loadItem(line_id);
        } else {
            App.Current.showError(pn_so_after_sales_delivery_and_new.this.getContext(), "已经是最后一条。");
        }
    }

    //@Override
    public void onScan(final String barcode) {

        final String bar_code = barcode.trim();
        if (bar_code.startsWith("CRQ:")) {
            if (bar_code.length() < 4) {
                App.Current.showError(pn_so_after_sales_delivery_and_new.this.getContext(), "");

                pn_so_after_sales_delivery_and_new.this.Header.setTitleText("请扫描有效的条码！！！！");
                return;
            }
            String pc_number = bar_code.substring(4, 23);
            pn_so_after_sales_delivery_and_new.this.txt_pc_number_cell.setContentText(pc_number.trim());
            loadItem_pc(pc_number);
        } else {
            String sn_number = bar_code.trim();
            pn_so_after_sales_delivery_and_new.this.txt_sn_number_cell.setContentText(sn_number.trim());
            loadItem_sn(sn_number);
        }
    }

    ///判断批次
    public void loadItem_pc(final String code) {
        this.ProgressDialog.show();

        String sql = "exec mm_after_delivery_get_pc_number ?,?,?,?,?,?";
//        Parameters p  =new Parameters().add(1,code).add(2,item_code).add(3,sh_date).add(4,fh_type).add(5,quantity).add(6,customer_name).add(7,this.code);
        Parameters p = new Parameters().add(1, code).add(2, item_code).add(3, sh_date).add(4, fh_type).add(5, quantity).add(6, customer_name);
        App.Current.DbPortal.ExecuteNonQueryAsync(this.Connector, sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_delivery_and_new.this.ProgressDialog.dismiss();

                Result<Integer> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_sales_delivery_and_new.this.getContext(), result.Error);
                    if (!result.Error.contains("确认是否继续")) {
                        pn_so_after_sales_delivery_and_new.this.txt_sn_number_cell.setContentText("");
                        pn_so_after_sales_delivery_and_new.this.txt_pc_number_cell.setContentText("");
                    }
                    return;
                }
            }
        });
    }

    ///判断条码
    public void loadItem_sn(final String code) {
        this.ProgressDialog.show();

        String sql = "exec mm_after_delivery_get_sn_number ?,?,?,?,?";
        Parameters p = new Parameters().add(1, code).add(2, item_code).add(3, sh_date).add(4, fh_type).add(5, customer_name);
        App.Current.DbPortal.ExecuteNonQueryAsync(this.Connector, sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_delivery_and_new.this.ProgressDialog.dismiss();

                Result<Integer> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_sales_delivery_and_new.this.getContext(), result.Error);
                    if (!result.Error.contains("确认是否继续")) {
                        pn_so_after_sales_delivery_and_new.this.txt_sn_number_cell.setContentText("");
                        pn_so_after_sales_delivery_and_new.this.txt_pc_number_cell.setContentText("");
                    }
                    return;
                }
            }
        });
    }

    @Override
    public void commit() {

        String pc_number = this.txt_pc_number_cell.getContentText().trim();
        String m_qty = this.txt_quantity_cell.getContentText().trim();
        String sn_number = this.txt_sn_number_cell.getContentText().trim();
        if ((pc_number == null || pc_number.length() == 0) && (sn_number == null || sn_number.length() == 0)) {
            App.Current.showError(this.getContext(), "批次和条码必须扫描一个才允许提交！");
            return;
        }
        if (m_qty == "0") {
            App.Current.showError(this.getContext(), "发货数量为0时不允许提交！");
            return;
        }

        String sql = " exec mm_after_delivery_head_PDA_commit ?,?,?,?,?,?,?";
        Parameters p = new Parameters();
        p.add(1, code).add(2, customer_name).add(3, pc_number).add(4, sn_number);
        p.add(5, sh_date).add(6, fh_type).add(7, item_code);
        App.Current.DbPortal.ExecuteNonQueryAsync(this.Connector, sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_delivery_and_new.this.ProgressDialog.dismiss();

                Result<Integer> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_sales_delivery_and_new.this.getContext(), result.Error);
                    return;
                }
                App.Current.toastInfo(pn_so_after_sales_delivery_and_new.this.getContext(), "提交成功！！！");
                pn_so_after_sales_delivery_and_new.this.txt_sn_number_cell.setContentText("");
                pn_so_after_sales_delivery_and_new.this.txt_pc_number_cell.setContentText("");
//                getqty();
                loadItem(line_id);
            }
        });
    }

    private void getqty() {
        this.ProgressDialog.show();

        String sql = "SELECT  ISNULL(qty,0)-ISNULL(close_quantity,0) AS  qty FROM mm_after_delivery_item WHERE head_id =(SELECT id FROM dbo.mm_after_delivery_head WHERE code=? ) AND item_code=? AND sh_date=?";
        Parameters p = new Parameters().add(1, code).add(2, item_code).add(3, sh_date);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_delivery_and_new.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_sales_delivery_and_new.this.getContext(), result.Error);
                    return;
                }

                DataRow row = result.Value;
                if (row == null) {
                    return;
                }
                pn_so_after_sales_delivery_and_new.this.txt_quantity_cell.setContentText(row.getValue("qty", 0).toString());
            }
        });
    }

}

