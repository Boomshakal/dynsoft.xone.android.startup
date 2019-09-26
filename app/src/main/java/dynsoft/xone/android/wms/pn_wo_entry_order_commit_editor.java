package dynsoft.xone.android.wms;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.SwitchCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.PromptCallback;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.Book;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.start.FrmLogin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml.Encoding;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

public class pn_wo_entry_order_commit_editor extends pn_editor {

    private String task_order_code;
    private String item_code;

    public pn_wo_entry_order_commit_editor(Context context) {
        super(context);
    }

    public TextCell task_work_code_cell; //工单号
    public TextCell txt_item_code_cell; // 产品编码
    public TextCell txt_item_name_cell; //机型
    public TextCell txt_org_code_cell;//组织
    public TextCell txt_quantity_cell;//缴库数量
    public TextCell txt_date_code_cell; //周期
    public SwitchCell chk_commit_print;//打印批次号
    public TextCell txt_warehouse_cell;//缴库库位
    public TextCell txt_snno_cell;//产品序列号
    public TextCell txt_size_cell;//外箱尺寸
    public TextCell txt_net_weight_cell;//单片净重
    public TextCell txt_gross_weight_cell;//毛重
    public String lot_number;
    private DataRow _rowv;
    private int scan_count;
    private boolean checkbool;
    public int count1=0;
    public int count2=0;
    public int count3=-2;
    public int flage = 1;
    public int qtyy = 0;
    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(
                R.layout.pn_wo_entry_order_commit_editor, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        this.txt_quantity_cell
                .onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onPrepared() {

        super.onPrepared();
        scan_count = 0;
        checkbool = true;
        this.txt_item_code_cell = (TextCell) this
                .findViewById(R.id.txt_item_code_cell);
        this.task_work_code_cell = (TextCell) this
                .findViewById(R.id.task_work_code_cell);
        this.txt_item_name_cell = (TextCell) this
                .findViewById(R.id.txt_item_name_cell);
        this.txt_org_code_cell = (TextCell) this
                .findViewById(R.id.txt_org_code_cell);
        this.txt_quantity_cell = (TextCell) this
                .findViewById(R.id.txt_quantity_cell);
        this.txt_date_code_cell = (TextCell) this
                .findViewById(R.id.txt_date_code_cell);
        this.txt_snno_cell = (TextCell) this
                .findViewById(R.id.txt_snno_cell);
        this.txt_warehouse_cell = (TextCell) this.findViewById(R.id.txt_warehouse_cell);
        this.chk_commit_print = (SwitchCell) this.findViewById(R.id.chk_commit_print);
        this.txt_size_cell = (TextCell) this.findViewById(R.id.txt_size_cell);
        this.txt_net_weight_cell = (TextCell) this.findViewById(R.id.txt_net_weight_cell);
        this.txt_gross_weight_cell = (TextCell) this.findViewById(R.id.txt_gross_weight_cell);
        if (this.task_work_code_cell != null) {
            this.task_work_code_cell.setLabelText("任务单号");
            this.task_work_code_cell.setReadOnly();
        }
        if (this.txt_item_code_cell != null) {
            this.txt_item_code_cell.setLabelText("物料编码");
            this.txt_item_code_cell.setReadOnly();
        }
        if (this.txt_snno_cell != null) {
            this.txt_snno_cell.setReadOnly();
            this.txt_snno_cell.setBackgroundColor(Color.parseColor("#FFE4B5"));
        }
        if (this.txt_item_name_cell != null) {
            this.txt_item_name_cell.setLabelText("机型名称");
            this.txt_item_name_cell.setReadOnly();
        }
        if (this.txt_warehouse_cell != null) {
            //this.txt_warehouse_cell.setLabelText("库位");
            this.txt_warehouse_cell.setReadOnly();
        }

        if (this.txt_org_code_cell != null) {
            this.txt_org_code_cell.setLabelText("组织");
            this.txt_org_code_cell.setReadOnly();
        }
        if (this.txt_quantity_cell != null) {
            this.txt_quantity_cell.setLabelText("缴库数量");
            this.txt_quantity_cell.setReadOnly();
        }
        if (this.txt_date_code_cell != null) {
            this.txt_date_code_cell.setLabelText("D/C");
            TimeZone zone = TimeZone.getTimeZone("Asia/Shanghai");
            Calendar cal = Calendar.getInstance(zone);
            int c = cal.get(Calendar.WEEK_OF_YEAR);
            int y = cal.get(Calendar.YEAR);
            DecimalFormat df = new DecimalFormat("00");
            String week = df.format(c);
            this.txt_date_code_cell.setContentText(String.valueOf(y).substring(2, 4) + week);
            this.txt_date_code_cell.setReadOnly();
        }
        if (this.chk_commit_print != null) {
            this.chk_commit_print.CheckBox.setTextColor(Color.BLACK);
            this.chk_commit_print.CheckBox.setChecked(false);
            this.chk_commit_print.CheckBox.setText("提交并打印入库单");
        }
        if (this.txt_warehouse_cell != null) {
            this.txt_warehouse_cell.setLabelText("缴库库存");
        }
        if (this.txt_snno_cell != null) {
            this.txt_snno_cell.setLabelText("产品序列号");
        }
        if (this.txt_size_cell != null) {
            this.txt_size_cell.setLabelText("还剩多少未缴库");
        }
        if (this.txt_net_weight_cell != null) {
            this.txt_net_weight_cell.setLabelText("单片净重(KG)");
        }
        if (this.txt_gross_weight_cell != null) {
            this.txt_gross_weight_cell.setLabelText("毛重(KG)");
        }
    }

    public void AfterScan(String new_prd_no, String qty) {
        String old_prd_no = this.txt_item_code_cell.getContentText();
        String wip_nos = this.task_work_code_cell.getContentText();
        String entry_qty = this.txt_quantity_cell.getContentText();
        BigDecimal q1 = new BigDecimal(qty);
        String lot_numbers = this.txt_snno_cell.getContentText().trim();
        if (lot_numbers.contains(lot_number)) {
            App.Current.showError(this.getContext(), "请检查批次号，批次号不允许重复扫描！");
            return;
        }
        if (wip_nos == null || wip_nos.length() == 0) {
            checkbool = true;
            loadworkdtl(lot_number);
            if (!checkbool) {
                return;
            }
            ;
            this.txt_quantity_cell.setContentText(qty);
        } else {
            if (!new_prd_no.equals(old_prd_no)) {
                App.Current.showError(this.getContext(), "请检查批次号，产品编码和上一次不一样！");
                return;
            }
            checkbool = true;
            loadworkdtl(lot_number);
            if (!checkbool) {
                return;
            }
            ;
            BigDecimal q2 = new BigDecimal(entry_qty);
            BigDecimal q3 = q1.add(q2);
            this.txt_quantity_cell.setContentText(App.formatNumber(q3, "0.##"));
        }
        if (lot_numbers.length() > 0) {
            lot_numbers += ", ";
        }
        this.txt_snno_cell.setContentText(lot_numbers + lot_number);
        scan_count = scan_count + 1;
        this.txt_warehouse_cell.setBackgroundColor(Color.parseColor("#F5F5DC"));
        this.txt_warehouse_cell.setContentText("已扫描箱数:" + String.valueOf(scan_count) + "箱");
        get_count();
        get_count_close(qtyy);
    }

    public void loadTaskLot(String sn_no) {
        String sql = "exec p_mm_wo_order_entry_get_item_from_sn ?";
        Parameters p = new Parameters().add(1, sn_no);
        final Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (r.HasError) {
            App.Current.showError(this.getContext(), r.Error);
        }

        if (r.Value != null && r.Value.Rows.size() > 0) {
            if (r.Value.Rows.size() > 1) {
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which >= 0) {
                            DataRow row = r.Value.Rows.get(which);
                            String itemcode = row.getValue("item_code", "");
                            String qty = App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##");
                            lot_number = row.getValue("lot_number", "");
                            AfterScan(itemcode, qty);
                        }
                        dialog.dismiss();
                    }
                };

                final TableAdapter adapter = new TableAdapter(this.getContext()) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        DataRow row = (DataRow) r.Value.Rows.get(position);
                        if (convertView == null) {
                            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_lot_number, null);
                            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
                        }

                        TextView num = (TextView) convertView.findViewById(R.id.num);
                        TextView txt_lot_number = (TextView) convertView.findViewById(R.id.txt_lot_number);
                        TextView txt_quantity = (TextView) convertView.findViewById(R.id.txt_quantity);

                        num.setText(String.valueOf(position + 1));
                        String lot_number = row.getValue("lot_number", "");
                        String qty = row.getValue("task_order_code", "") + "," + row.getValue("item_code", "") + ", " + App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##");

                        txt_lot_number.setText(lot_number);
                        txt_quantity.setText(qty);

                        return convertView;
                    }
                };

                adapter.DataTable = r.Value;
                adapter.notifyDataSetChanged();

                new AlertDialog.Builder(this.getContext())
                        .setTitle("选择入库批次")
                        .setSingleChoiceItems(adapter, 0, listener)
                        .setNegativeButton("取消", null).show();
            } else {
                DataRow row = r.Value.Rows.get(0);
                String itemcode = row.getValue("item_code", "");
                String qty = App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##");
                lot_number = row.getValue("lot_number", "");
                AfterScan(itemcode, qty);
            }
        } else {
            App.Current.showError(this.getContext(), "非法条码，请扫描正确的二维码！");
        }
    }

    @Override
    public void onScan(final String barcode) {
        final String bar_code = barcode.trim();
        // 扫描发运单条码
        if (bar_code.startsWith("CRQ:")) {
            String tempbar_code = bar_code.substring(4, bar_code.length());
            lot_number = tempbar_code.split("-")[0];
            String new_prd_no = tempbar_code.split("-")[1];
            String qty = tempbar_code.split("-")[2];
            qtyy = Integer.valueOf(tempbar_code.split("-")[2]);
            AfterScan(new_prd_no, qty);


        } else {
            loadTaskLot(bar_code);
        }
    }

    public void loadworkdtl(String p_lot_number) {
        String sql = "exec p_mm_wo_get_work_dtl_b  ?";
        Parameters p = new Parameters().add(1, p_lot_number);
        Result<DataRow> ri = App.Current.DbPortal.ExecuteRecord(this.Connector,
                sql, p);
        if (ri.HasError) {
            App.Current.showError(this.getContext(), ri.Error);
            checkbool = false;
            clear();
            return;
        }
        if (ri.Value != null) {
            if (_rowv != null) {
                if (!_rowv.getValue("code", "").equals(ri.Value.getValue("code", ""))) {
                    App.Current.showError(this.getContext(), "扫描的批次不属于此工单" + _rowv.getValue("code", ""));
                    checkbool = false;
                    return;
                }
            }
            item_code = ri.Value.getValue("item_code", "");
            this.txt_org_code_cell.setContentText(ri.Value.getValue("org_code", "") + "," + ri.Value.getValue("warehouse_code", ""));
            this.txt_item_name_cell.setContentText(ri.Value.getValue("item_name", ""));
            this.txt_item_code_cell.setContentText(item_code);
            this.txt_warehouse_cell.setContentText("");
            task_order_code = ri.Value.getValue("code", "");
            this.task_work_code_cell.setContentText(task_order_code);
            if (!task_order_code.toUpperCase().contains("Z")) {
                this.txt_size_cell.setContentText(ri.Value.getValue("outer_box_size", ""));
                float net_weight = ri.Value.getValue("net_weight", new BigDecimal(0)).floatValue();
                float gross_weight = ri.Value.getValue("gross_weight", new BigDecimal(0)).floatValue();
                if (net_weight == 0) {
                    this.txt_net_weight_cell.setContentText("");
                } else {
                    this.txt_net_weight_cell.setContentText(String.valueOf(net_weight));
                }
                if (gross_weight == 0) {
                    this.txt_gross_weight_cell.setContentText("");
                } else {
                    this.txt_gross_weight_cell.setContentText(String.valueOf(gross_weight));
                }
            }
            _rowv = ri.Value;

        } else {
            App.Current.showError(this.getContext(), "找不到编号为" + p_lot_number + "的生产任务。");
            checkbool = false;
            return;
        }
    }

    public void get_count() {
        String sql1 = "select \n" +
                "t0.code,t0.work_order_id,t0.create_time,t5.code+' - '+t5.name create_user,t7.code work_order_code,\n" +
                "t0.update_time,t6.code+' - '+t6.name update_user,t0.organization_id,t0.factory_id,t2.code+' - '+t2.name factory_name,t1.code org_code,\n" +
                "t0.warehouse_id,t3.code warehouse_code,t0.item_id,t4.code item_code,t4.name item_name,t0.plan_quantity,\n" +
                "t0.completed_quantity,t0.scrapped_quantity,t0.release_time,t0.plan_issue_date,plan_start_date,t0.plan_complete_date,\n" +
                "t0.actual_start_date,t0.actual_complete_date,t0.issue_order_count,t0.status,t0.priority,t0.comment,t0.process_id,t8.code+' - '+t8.name process_name\n" +
                "FROM dbo.mm_wo_task_order_head t0\n" +
                "INNER JOIN dbo.mm_organization t1 ON t1.id=t0.organization_id\n" +
                "LEFT JOIN dbo.mm_partner t2 ON t2.id=t0.factory_id\n" +
                "LEFT JOIN dbo.mm_warehouse t3 ON t3.id=t0.warehouse_id\n" +
                "INNER JOIN mm_item t4 ON t4.id=t0.item_id\n" +
                "INNER JOIN dbo.core_user t5 ON t5.id=t0.create_user\n" +
                "LEFT JOIN dbo.core_user t6 ON t6.id=t0.update_user\n" +
                "INNER JOIN mm_wo_work_order_head t7 ON t7.id=t0.work_order_id\n" +
                "left join fm_process t8 on t8.id=t0.process_id\n" +
                "where t0.code = ?";
        Parameters p1 = new Parameters().add(1,task_order_code);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql1, p1, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_wo_entry_order_commit_editor.this.getContext(), result.Error);
                    return;
                }

                DataRow row = result.Value;
                if (row == null) {
                    Log.e("LZH2011",task_order_code);
                    App.Current.showError(pn_wo_entry_order_commit_editor.this.getContext(), "找不到对应数目。");
                    return;
                }
                else{
                    count1 = result.Value.getValue("plan_quantity",new BigDecimal(0)).intValue();
                }
            }

            });
    }



    @SuppressLint("HandlerLeak")
    public void get_count_close(int qty) {
        String sql1 = "SELECT  b.code AS task_order_code ,\n" +
                "        SUM(j.quantity) quantity\n" +
                "       --(select  top 1 pack_sn_no from dbo.mm_wo_pack_lot where lot_number =j.lot_number )  pack_sn_no\n" +
                "FROM    mm_wo_entry_order_head a\n" +
                "        LEFT JOIN dbo.mm_wo_task_order_head b ON b.id = a.task_order_id\n" +
                "        LEFT JOIN dbo.mm_wo_work_order_head c ON c.id = a.work_order_id\n" +
                "        LEFT JOIN dbo.mm_partner d ON d.id = a.factory_id\n" +
                "        LEFT JOIN dbo.core_user e ON e.id = a.create_user\n" +
                "        LEFT JOIN dbo.mm_item f ON f.id = a.item_id\n" +
                "        LEFT JOIN dbo.mm_warehouse g ON g.id = a.warehouse_id\n" +
                "        LEFT JOIN dbo.mm_organization h ON h.id = a.organization_id\n" +
                "--left join dbo.core_user i on i.id=a.tester_id\n" +
                "        LEFT JOIN dbo.mm_wo_entry_order_item j\n" +
                "        LEFT JOIN dbo.core_user k ON j.store_keeper = k.id \n" +
                "--left join  (select  distinct lot_number ,pack_sn_no from mm_wo_pack_lot)  t2 on j.lot_number=t2.lot_number\n" +
                "        LEFT JOIN qm_fqc_head m ON m.code = j.fqc_code ON j.head_id = a.id\n" +
                "        LEFT JOIN dbo.core_user i ON i.id = m.tester_id\n" +
                "WHERE   b.code = ?\n" +
                "GROUP BY b.code";
        Parameters p1 = new Parameters().add(1,task_order_code);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql1, p1, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_wo_entry_order_commit_editor.this.getContext(), result.Error);
                    return;
                }

                DataRow row = result.Value;
                if (row != null) {

                    count2 = result.Value.getValue("quantity",new BigDecimal(0)).intValue();
                }


                Log.e("lzh111",String.valueOf(count3));
                    if(count3==-2) {
                        count3 = count1 - count2 -qtyy;
                        txt_size_cell.setContentText(String.valueOf(count3));
                    }else{
                        count3 =count3-qtyy;
                        txt_size_cell.setContentText(String.valueOf(count3));
                    }


                }

        });
    }








    @Override
    public void commit() {
        //更新mm_item表
        if (task_order_code != null && !task_order_code.toUpperCase().contains("Z")) {
            if (TextUtils.isEmpty(txt_size_cell.getContentText())) {
                App.Current.toastError(getContext(), "请输入外箱尺寸");
            } else if (TextUtils.isEmpty(txt_net_weight_cell.getContentText())) {
                App.Current.toastError(getContext(), "请输入单片净重");
            } else if (TextUtils.isEmpty(txt_gross_weight_cell.getContentText())) {
                App.Current.toastError(getContext(), "请输入毛重");
            } else {
                String sql_update = "exec fm_update_mm_item_and ?,?,?,?";
                Parameters p_update = new Parameters().add(1, txt_net_weight_cell.getContentText().trim())
                        .add(2, txt_gross_weight_cell.getContentText().trim()).add(3, txt_size_cell.getContentText().trim()).add(4, item_code);
                App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql_update, p_update, new ResultHandler<Integer>() {
                    @Override
                    public void handleMessage(Message msg) {
                        Result<Integer> value = Value;
                        if (value.HasError) {
                            App.Current.toastError(getContext(), value.Error);
                        } else {
                            if (value.Value > 0) {
                                App.Current.toastInfo(pn_wo_entry_order_commit_editor.this.getContext(), "物料更新成功");
                            } else {
                                App.Current.toastInfo(pn_wo_entry_order_commit_editor.this.getContext(), "更新物料信息失败");
                            }
                        }
                        commitAndPrint();
                    }
                });
            }
        } else {
            commitAndPrint();
        }
    }

    private void commitAndPrint() {
        String sql = "exec p_mm_wo_entry_order_pda_create_b  ?,?,?,?,?,?,? ";
        if (_rowv == null) {
            App.Current.showError(this.getContext(), "请扫描需要缴库批次号！");
            return;
        }
        String lotnumbers = this.txt_snno_cell.getContentText();
        Connection conn = App.Current.DbPortal.CreateConnection(pn_wo_entry_order_commit_editor.this.Connector);
        CallableStatement stmt;
        try {
            stmt = conn.prepareCall(sql);
            stmt.setObject(1, _rowv.getValue("task_order_id", 0L));
            stmt.setObject(2, App.Current.UserID);
            stmt.setObject(3, App.Current.BranchID);
            stmt.setObject(4, 1);
            stmt.setObject(5, "");
            stmt.setObject(6, lotnumbers);
            stmt.registerOutParameter(7, Types.VARCHAR);
            stmt.execute();
            String val = stmt.getString(7);
            if (val != null) {
                Result<String> rs = XmlHelper.parseResult(val);
                if (rs.HasError) {
                    App.Current.showError(pn_wo_entry_order_commit_editor.this.getContext(), rs.Error);
                    return;
                }
                App.Current.toastInfo(pn_wo_entry_order_commit_editor.this.getContext(), "缴库提交成功");
                clear();
                scan_count = 0;
                _rowv = null;
                if (pn_wo_entry_order_commit_editor.this.chk_commit_print.CheckBox.isChecked()) {
                    //
                    printLabel(rs.Value);
                }
            }
        } catch (SQLException e) {
            App.Current.showInfo(pn_wo_entry_order_commit_editor.this.getContext(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void printLabel(String head_id) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("id", head_id);
        App.Current.Print("mm_wo_entry_order", "打印缴库单", parameters);
    }

    public void clear() {
        this.task_work_code_cell.setContentText("");
        this.txt_item_name_cell.setContentText("");
        this.txt_item_code_cell.setContentText("");
        this.txt_warehouse_cell.setContentText("");
        this.txt_org_code_cell.setContentText("");
        this.txt_quantity_cell.setContentText("");
        this.txt_snno_cell.setContentText("");
        this.txt_size_cell.setContentText("");
        this.txt_net_weight_cell.setContentText("");
        this.txt_gross_weight_cell.setContentText("");
    }
}
