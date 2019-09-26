package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.util.ArrayList;

import dynsoft.xone.android.blueprint.Demo_ad_escActivity;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.SwitchCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

public class pn_begin_stock_editor extends pn_editor {

    public pn_begin_stock_editor(Context context) {
        super(context);
    }

    public ButtonTextCell txt_vendor_no_cell;
    public TextCell txt_item_code_cell;
    public TextCell txt_vendor_name_cell;
    public ButtonTextCell txt_vendor_model_cell;
    public ButtonTextCell txt_org_code_cell;
    public TextCell txt_vendorlot_cell;
    public DecimalCell txt_quantity_cell;
    public TextCell txt_date_code_cell;
    public ButtonTextCell txt_location_cell;
    public TextCell txt_lot_number_cell;
    public SwitchCell chk_commit_print;
    public TextCell txt_warehouse_cell;


    @Override
    public void setContentView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(
                R.layout.pn_begin_stock_editor, this, true);
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

        this.txt_item_code_cell = (TextCell) this
                .findViewById(R.id.txt_item_code_cell);
        this.txt_vendor_no_cell = (ButtonTextCell) this
                .findViewById(R.id.txt_vendor_no_cell);
        this.txt_vendor_name_cell = (TextCell) this
                .findViewById(R.id.txt_vendor_name_cell);
        this.txt_vendor_model_cell = (ButtonTextCell) this
                .findViewById(R.id.txt_vendor_model_cell);
        this.txt_org_code_cell = (ButtonTextCell) this
                .findViewById(R.id.txt_org_code_cell);
        this.txt_quantity_cell = (DecimalCell) this
                .findViewById(R.id.txt_quantity_cell);
        this.txt_date_code_cell = (TextCell) this
                .findViewById(R.id.txt_date_code_cell);
        this.txt_location_cell = (ButtonTextCell) this
                .findViewById(R.id.txt_location_cell);
        this.txt_lot_number_cell = (TextCell) this
                .findViewById(R.id.txt_lot_number_cell);
        this.txt_warehouse_cell =(TextCell) this.findViewById(R.id.txt_warehouse_cell);
        this.chk_commit_print=(SwitchCell)this.findViewById(R.id.chk_commit_print);
        this.txt_vendorlot_cell=(TextCell)this.findViewById(R.id.txt_vendorlot_cell);

        if (this.txt_vendor_no_cell != null) {
            this.txt_vendor_no_cell.setLabelText("厂商编号");
            // this.txt_shipment_code_cell.setReadOnly();
            // this.txt_vendor_no_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_light"));
            this.txt_vendor_no_cell.Button
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String item_num = pn_begin_stock_editor.this.txt_item_code_cell
                                    .getContentText().toString().trim();
                            if (item_num != null && item_num.length() != 0) {
                                pn_begin_stock_editor.this.loadVendorInfo();
                            } else {
                                App.Current.showWarning(
                                        pn_begin_stock_editor.this.getContext(),
                                        "请先输入物料编码！");
                            }
                        }
                    });
        }

        if (this.txt_vendor_model_cell != null) {
            this.txt_vendor_model_cell.setLabelText("型号");
            //this.txt_shipment_code_cell.setReadOnly();
            //this.txt_vendor_no_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_light"));
            this.txt_vendor_model_cell.Button
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String item_num = pn_begin_stock_editor.this.txt_item_code_cell
                                    .getContentText().toString().trim();
                            if (item_num != null && item_num.length() != 0) {
                                pn_begin_stock_editor.this.loaditemmodel();
                            } else {
                                App.Current.showWarning(
                                        pn_begin_stock_editor.this.getContext(),
                                        "请先输入物料编码！");
                            }
                        }
                    });
        }

        if (this.txt_item_code_cell != null) {
            this.txt_item_code_cell.setLabelText("物料编码");
            this.txt_item_code_cell.setContentText("R");
            // this.txt_item_code_cell.setReadOnly();
        }
        if (this.txt_vendor_name_cell != null) {
            this.txt_vendor_name_cell.setLabelText("厂商名称");
            this.txt_vendor_name_cell.setReadOnly();
        }
        if (this.txt_warehouse_cell != null) {
            this.txt_warehouse_cell.setLabelText("库位");
        }
        //if (this.txt_vendor_model_cell != null) {
        //this.txt_vendor_model_cell.setLabelText("厂家型号");
        // this.txt_vendor_model_cell.setReadOnly();
        //}
        if (this.txt_vendorlot_cell != null) {
            this.txt_vendorlot_cell.setLabelText("供应批次");
            // this.txt_vendor_model_cell.setReadOnly();
        }
        if (this.txt_org_code_cell != null) {
            this.txt_org_code_cell.setLabelText("组织");
            // this.txt_org_code_cell.setReadOnly();
            this.txt_org_code_cell.setContentText("E");
            this.txt_org_code_cell.Button
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pn_begin_stock_editor.this.loadOrgCode();

                        }
                    });
        }

        if (this.txt_quantity_cell != null) {
            this.txt_quantity_cell.setLabelText("数量");
            //20161217 仓库要求锁定数量不允许修改。
            this.txt_quantity_cell.setReadOnly();

        }

        if (this.txt_date_code_cell != null) {
            this.txt_date_code_cell.setLabelText("D/C");
            // this.txt_date_code_cell.setReadOnly();
        }
        if (this.txt_lot_number_cell != null) {
            this.txt_lot_number_cell.setLabelText("批次");
            this.txt_lot_number_cell.setReadOnly();
        }
        if (this.chk_commit_print != null) {
            this.chk_commit_print.CheckBox.setTextColor(Color.BLACK);
            this.chk_commit_print.CheckBox.setChecked(true);
            this.chk_commit_print.CheckBox.setText("提交并打印");
        }
        if (this.txt_location_cell != null) {
            this.txt_location_cell.setLabelText("储位");
            this.txt_location_cell.setReadOnly();
            this.txt_location_cell.Button
                    .setImageBitmap(App.Current.ResourceManager
                            .getImage("@/core_close_light"));
            this.txt_location_cell.Button
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pn_begin_stock_editor.this.txt_location_cell
                                    .setContentText("");
                        }
                    });
        }

        String lot_number = this.Parameters.get("lot_number", "");
        if (lot_number != null && lot_number.length() > 0) {
            this.loadLotNumber(lot_number);
        }

        String item_code = this.Parameters.get("item_code", "");
        if (item_code != null && item_code.length() > 0) {
            this.txt_item_code_cell.setContentText(item_code);
        }
    }

    @Override
    public void onScan(final String barcode) {
        final String bar_code = barcode.trim();

        // 扫描发运单条码

        // 扫描批次条码
        if (bar_code.startsWith("CRQ:")) {
            int pos = bar_code.indexOf("-");
            String lot = bar_code.substring(4, pos);
            this.txt_lot_number_cell.setContentText(lot);
            this.loadLotNumber(bar_code.substring(4, bar_code.length()));
            loadVendorInfo();
            loaditemmodel();
            return;
        }

        if (bar_code.startsWith("QTY:")) {
            String qty = bar_code.substring(4, bar_code.length());
            this.txt_quantity_cell.setContentText(qty);
            return;
        }

        if (bar_code.startsWith("Q:")) {
            String qty = bar_code.substring(2, bar_code.length());
            this.txt_quantity_cell.setContentText(qty);
            return;
        }

        if (bar_code.startsWith("L:")) {
            String loc = bar_code.substring(2, bar_code.length());

            String locs = this.txt_location_cell.getContentText().trim();
            if (locs.contains(loc)){
                return;
            }
            if (locs.length() > 0){
                locs += ", ";
            }
            this.txt_location_cell.setContentText(locs+loc);
            return;
        }
        if (bar_code.startsWith("R:")
                || (bar_code.startsWith("R") && (bar_code.length() == 9 || bar_code
                .length() == 10)) || bar_code.length() == 8) {
            if (bar_code.startsWith("R:")) {
                this.txt_item_code_cell.setContentText(bar_code.substring(2,
                        bar_code.length()));
                this.loadVendorInfo();
                loaditemmodel();
            } else {
                this.txt_item_code_cell.setContentText(bar_code);
                this.loadVendorInfo();
                loaditemmodel();

            }
            return;
        }
    }

    public void loadLotNumber(String lotNumber) {
        String[] lot = lotNumber.split("-");
        String batchlot = pn_begin_stock_editor.this.txt_lot_number_cell.getContentText().trim();
        String vendorno = lot[0].substring(3, 9);
        pn_begin_stock_editor.this.txt_vendor_no_cell.setContentText(vendorno);
        pn_begin_stock_editor.this.txt_quantity_cell.setContentText(lot[2]
                .toString());
        pn_begin_stock_editor.this.txt_date_code_cell.setContentText(lot[3]
                .toString());
        pn_begin_stock_editor.this.txt_item_code_cell.setContentText(lot[1]
                .toString());
        String sql = "exec p_get_lot_orgcode ?";
        Parameters p = new Parameters().add(1, batchlot);
        Result<DataRow> ri = App.Current.DbPortal.ExecuteRecord(this.Connector,
                sql, p);
        if (ri.HasError) {
            App.Current.showError(this.getContext(), ri.Error);
            this.txt_org_code_cell.setContentText("");
            this.txt_location_cell.setContentText("");
            this.txt_vendorlot_cell.setContentText("");
            return;
        }
        pn_begin_stock_editor.this.txt_org_code_cell.setContentText(ri.Value.getValue("org_code", ""));
        //pn_begin_stock_editor.this.txt_location_cell.setContentText(ri.Value.getValue("location_code", ""));
        pn_begin_stock_editor.this.txt_vendorlot_cell.setContentText(ri.Value.getValue("vendor_lot", ""));
    }

    public void loadLocation(String location) {
        String sql = "select id,code,name from mm_location where code=?";
        Parameters p = new Parameters().add(1, location);

        Result<DataRow> ri = App.Current.DbPortal.ExecuteRecord(this.Connector,
                sql, p);
        if (ri.HasError) {
            App.Current.showError(this.getContext(), ri.Error);
            this.txt_location_cell.setContentText("");
            return;
        }

        String loc_code = ri.Value.getValue("code", String.class);
        this.txt_location_cell.setContentText(loc_code);
    }

    public void loadOrgCode() {

        String sql = "SELECT code FROM dbo.mm_organization WHERE code <>'IMO' order by code";
        final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql);
        if (result.HasError) {
            App.Current.showError(this.getContext(), result.Error);
            return;
        }
        if (result.Value != null) {

            ArrayList<String> names = new ArrayList<String>();
            for (DataRow row : result.Value.Rows) {
                String name = row.getValue("code", "");
                names.add(name);
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which >= 0) {
                        DataRow row = result.Value.Rows.get(which);
                        pn_begin_stock_editor.this.txt_org_code_cell.setContentText(row.getValue(
                                "code", ""));
                    }
                    dialog.dismiss();
                }
            };
            new AlertDialog.Builder(pn_begin_stock_editor.this.getContext()).setTitle("选择组织")
                    .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(pn_begin_stock_editor.this.txt_org_code_cell.getContentText().toString()), listener)
                    .setNegativeButton("取消", null).show();
        }
    }
    public void loadVendorInfo() {

        String sql = "SELECT  VENDOR_NO,VENDOR_NAME,VENDOR_SITE_CODE,MANUFACTURER_NAME,PRIMARY_VENDOR_ITEM  FROM v_mm_item_vendor  WHERE   ITEM_NO=?  AND  (ISNULL(?,'')=''  OR VENDOR_NO=?)";
        final String item_num = this.txt_item_code_cell.getContentText().trim();
        String lotnumber=this.txt_lot_number_cell.getContentText().trim();
        String vendorno = this.txt_vendor_no_cell.getContentText().trim();
        if (lotnumber==null || lotnumber.length()==0)
        {
            vendorno="";
        }
        Parameters p = new Parameters().add(1, item_num).add(2, vendorno)
                .add(3, vendorno);
        final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(
                this.Connector, sql, p);
        if (result.HasError) {
            App.Current.showError(this.getContext(), result.Error);
            return;
        }
        if (result.Value != null && result.Value.Rows.size()>0) {
            if (result.Value.Rows.size() > 1) {
                ArrayList<String> names = new ArrayList<String>();
                for (DataRow row : result.Value.Rows) {
                    String name = row.getValue("VENDOR_NO", "") + " "
                            + row.getValue("VENDOR_SITE_CODE", "") + " \n"
                            + row.getValue("MANUFACTURER_NAME", "") + " "
                            + row.getValue("PRIMARY_VENDOR_ITEM", "");
                    names.add(name);
                }

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which >= 0) {
                            DataRow row = result.Value.Rows.get(which);

                            txt_vendor_name_cell.setContentText(row.getValue(
                                    "VENDOR_NAME", ""));
                            txt_vendor_model_cell.setContentText(row.getValue(
                                    "PRIMARY_VENDOR_ITEM", ""));
                            txt_vendor_no_cell.setContentText(row.getValue(
                                    "VENDOR_NO", ""));
                        }
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(pn_begin_stock_editor.this.getContext())
                        .setTitle("选择厂商")
                        .setSingleChoiceItems(names.toArray(new String[0]), 0,
                                listener).setNegativeButton("取消", null).show();
            } else  {
                txt_vendor_name_cell.setContentText(result.Value.Rows.get(0).getValue(
                        "VENDOR_NAME", ""));
                txt_vendor_model_cell.setContentText(result.Value.Rows.get(0).getValue(
                        "PRIMARY_VENDOR_ITEM", ""));
                txt_vendor_no_cell.setContentText(result.Value.Rows.get(0).getValue(
                        "VENDOR_NO", ""));
            }
        } else
        {
            txt_vendor_name_cell.setContentText("深圳麦格米特电气股份有限公司");
            txt_vendor_model_cell.setContentText("/");
            txt_vendor_no_cell.setContentText("530000");
        }
    }


    public void loaditemmodel() {

        String sql = "SELECT  model  From  MM_ITEM_MODEL a left join mm_item b on a.item_id = b.id where b.code =? ";
        final String item_num = this.txt_item_code_cell.getContentText().trim();

        Parameters p = new Parameters().add(1, item_num);
        final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(
                this.Connector, sql, p);
        if (result.HasError) {
            App.Current.showError(this.getContext(), result.Error);
            return;
        }
        if (result.Value != null && result.Value.Rows.size()>0) {
            if (result.Value.Rows.size() > 1) {
                ArrayList<String> names = new ArrayList<String>();
                for (DataRow row : result.Value.Rows) {
                    String name = row.getValue("model", "") ;
                    names.add(name);
                }

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which >= 0) {
                            DataRow row = result.Value.Rows.get(which);
                            txt_vendor_model_cell.setContentText(row.getValue(
                                    "model", ""));

                        }
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(pn_begin_stock_editor.this.getContext())
                        .setTitle("选择型号")
                        .setSingleChoiceItems(names.toArray(new String[0]), 0,
                                listener).setNegativeButton("取消", null).show();
            } else  {
                txt_vendor_model_cell.setContentText(result.Value.Rows.get(0).getValue(
                        "model", ""));

            }
        }
    }



    @Override
    public void commit() {

		/*
		 * @lotnumber nvarchar(240),
		 *
		 * @item_num nvarchar(50),
		 *
		 * @vendor_num nvarchar(50),
		 *
		 * @vendor_model nvarchar(50),
		 *
		 * @date_code nvarchar(50),
		 *
		 * @org_code nvarchar(50),
		 *
		 * @location_code nvarchar(50),
		 *
		 * @quantity decimal(10,5) ,
		 *
		 * @user_id int,
		 *
		 * @vendorlot nvarchar(240),
		 *
		 * @printcount int
		 */
        String sql = "exec p_mm_init_stock ?,?,?,?,?,?,?,?,?,?,?,? ";
        String itemnum = this.txt_item_code_cell.getContentText().trim();
        String lotnumber = this.txt_lot_number_cell.getContentText().trim();
        String vendorno = this.txt_vendor_no_cell.getContentText().trim();
        String vendormodel = this.txt_vendor_model_cell.getContentText().trim();
        String orgcode = this.txt_org_code_cell.getContentText().trim();
        String datecode = this.txt_date_code_cell.getContentText().trim();
        String qty = this.txt_quantity_cell.getContentText().trim();
        String locationcode = this.txt_location_cell.getContentText().trim();
        String vendorlot = this.txt_vendorlot_cell.getContentText().trim();
        String warcode=this.txt_warehouse_cell.getContentText().trim();
        if (itemnum == null || itemnum.length() == 0) {
            App.Current.showError(this.getContext(), "物料编码为空不能提交！");
            return;
        }
        if (vendorno == null || vendorno.length() == 0) {
            App.Current.showError(this.getContext(), "厂商编码为空不能提交！");
            return;
        }
        if (vendormodel == null || vendormodel.length() == 0) {
            App.Current.showError(this.getContext(), "厂商型号为空不能提交！");
            return;
        }
        if (orgcode == null || orgcode.length() == 0) {
            App.Current.showError(this.getContext(), "组织为空不能提交！");
            return;
        }

        if (datecode == null || datecode.length() == 0) {
            App.Current.showError(this.getContext(), "D/C为空不能提交！");
            return;
        }
        if (qty == null || qty.length() == 0) {
            App.Current.showError(this.getContext(), "数量为空不能提交！");
            return;
        }
        if (locationcode == null || locationcode.length() == 0) {
            App.Current.showError(this.getContext(), "储位为空不能提交！");
            return;
        }
        if (warcode == null || warcode.length() == 0) {
            App.Current.showError(this.getContext(), "库位为空不能提交！");
            return;
        }

        Parameters p = new Parameters().add(1, lotnumber).add(2, itemnum).add(3, vendorno);
        p.add(4, vendormodel).add(5, datecode).add(6, orgcode).add(7, locationcode).add(8, qty);
        p.add(9, App.Current.UserID).add(10, vendorlot).add(11, 1).add(12, warcode);
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_begin_stock_editor.this.ProgressDialog.dismiss();

                Result<DataTable> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_begin_stock_editor.this.getContext(),result.Error);
                    return;
                }

                DataTable dt = result.Value;
                App.Current.toastInfo(pn_begin_stock_editor.this.getContext(), "提交成功");
                if (pn_begin_stock_editor.this.chk_commit_print.CheckBox.isChecked()) {
                    if (dt.Rows.size() > 0) {
                        printLabel(dt.Rows.get(0));
                    }
                }
                //pn_begin_stock_editor.this.txt_org_code_cell.setContentText(org_code);
                clear();
            }
        });
    }

    public void printLabel(final DataRow row) {

        //App.Current.prompt(this.getContext(), "请输入数量", 500, new PromptCallback(){
        //@Override
        //public void onReturn(String result) {

        //	}
        //});

        //Intent intent = new Intent();
        //intent.setClass(this.getContext(), frm_item_lot_printer_init.class);
        //intent.putExtra("org_code", row.getValue("org_code", ""));
        //intent.putExtra("item_code", row.getValue("meg_pn", ""));
        //intent.putExtra("vendor_model", row.getValue("vendor_pn", ""));
        //intent.putExtra("lot_number", row.getValue("m_batch_num", ""));
        //intent.putExtra("vendor_lot", row.getValue("v_batch_num", ""));
        //intent.putExtra("date_code", row.getValue("date_code", ""));
        //intent.putExtra("quantity", App.formatNumber(
        //	row.getValue("quantity", BigDecimal.ZERO), "0.####"));
        //intent.putExtra("ut", row.getValue("ut", ""));
        //intent.putExtra("usercode", App.Current.UserCode);

        //this.getContext().startActivity(intent);


        final String items[] = {"霍尼韦尔", "芝柯"};

        AlertDialog dialog1 = new AlertDialog.Builder(App.Current.Workbench).setTitle("请选择打印机")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       
                        if(i == 1) {



String sql = "exec get_pint_date";
							App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, new ResultHandler<DataRow>() {
								@Override
								public void handleMessage(Message msg) {
									Result<DataRow> value = Value;
									if (value.HasError) {
										App.Current.toastError(pn_begin_stock_editor.this.getContext(), value.Error);
										return;
									}
									if (value.Value != null) {
										Intent intent = new Intent();
										intent.setClass(App.Current.Workbench, Demo_ad_escActivity.class);
										String cur_date = value.Value.getValue("cur_date", "");
										intent.putExtra("cur_date", cur_date);
										   intent.putExtra("org_code", row.getValue("org_code", ""));
                        intent.putExtra("item_code", row.getValue("meg_pn", ""));
                        intent.putExtra("vendor_model", row.getValue("vendor_pn", ""));
                        intent.putExtra("lot_number", row.getValue("m_batch_num", ""));
                        intent.putExtra("vendor_lot", row.getValue("v_batch_num", ""));
                        intent.putExtra("date_code", row.getValue("date_code", ""));
                        intent.putExtra("quantity", App.formatNumber(
                                row.getValue("quantity", BigDecimal.ZERO), "0.####"));
                        intent.putExtra("ut", row.getValue("ut", ""));
                        intent.putExtra("usercode", App.Current.UserCode);
										App.Current.Workbench.startActivity(intent);
									}
								}
							});

                        } else {
							 Intent intent = new Intent();

                            intent.setClass(App.Current.Workbench, frm_item_lot_printer.class);
                        intent.putExtra("org_code", row.getValue("org_code", ""));
                        intent.putExtra("item_code", row.getValue("meg_pn", ""));
                        intent.putExtra("vendor_model", row.getValue("vendor_pn", ""));
                        intent.putExtra("lot_number", row.getValue("m_batch_num", ""));
                        intent.putExtra("vendor_lot", row.getValue("v_batch_num", ""));
                        intent.putExtra("date_code", row.getValue("date_code", ""));
                        intent.putExtra("quantity", App.formatNumber(
                                row.getValue("quantity", BigDecimal.ZERO), "0.####"));
                        intent.putExtra("ut", row.getValue("ut", ""));
                        intent.putExtra("usercode", App.Current.UserCode);

                        App.Current.Workbench.startActivity(intent);
                        }
                    }
                }).create();
        dialog1.show();


    }

    public void clear() {
        this.txt_vendor_no_cell.setContentText("");
        this.txt_item_code_cell.setContentText("");
        this.txt_vendor_model_cell.setContentText("");
        this.txt_lot_number_cell.setContentText("");
        this.txt_vendor_name_cell.setContentText("");
        this.txt_date_code_cell.setContentText("");
        this.txt_org_code_cell.setContentText("");
        this.txt_vendorlot_cell.setContentText("");
        this.txt_quantity_cell.setContentText("");
    }
}
