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

public class pn_mm_splitlabel_editor extends pn_editor {

    public pn_mm_splitlabel_editor(Context context) {
        super(context);
    }

    public TextCell txt_vendor_no_cell;
    public TextCell txt_item_code_cell;
    public TextCell txt_vendor_name_cell;
    public TextCell txt_vendor_model_cell;
    public TextCell txt_org_code_cell;
    public TextCell txt_vendorlot_cell;
    public TextCell txt_quantity_cell;
    public TextCell txt_date_code_cell;
    public TextCell txt_lot_number_cell;
    public TextCell txt_uom_cell;


    @Override
    public void setContentView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(
                R.layout.pn_mm_splitlabel_editor, this, true);
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

        this.txt_item_code_cell = (TextCell) this.findViewById(R.id.txt_item_code_cell);
        this.txt_vendor_no_cell = (TextCell) this.findViewById(R.id.txt_vendor_no_cell);
        this.txt_vendor_name_cell = (TextCell) this.findViewById(R.id.txt_vendor_name_cell);
        this.txt_vendor_model_cell = (TextCell) this.findViewById(R.id.txt_vendor_model_cell);
        this.txt_org_code_cell = (TextCell) this.findViewById(R.id.txt_org_code_cell);
        this.txt_quantity_cell = (TextCell) this.findViewById(R.id.txt_quantity_cell);
        this.txt_date_code_cell = (TextCell) this.findViewById(R.id.txt_date_code_cell);
        this.txt_lot_number_cell = (TextCell) this.findViewById(R.id.txt_lot_number_cell);
        this.txt_vendorlot_cell=(TextCell)this.findViewById(R.id.txt_vendorlot_cell);
        this.txt_uom_cell =(TextCell) this.findViewById(R.id.txt_uom_cell);
        if (this.txt_uom_cell!=null)
        {
            this.txt_uom_cell.setLabelText("单位");
        }
        if (this.txt_vendor_no_cell != null) {
            this.txt_vendor_no_cell.setLabelText("厂商编号");
            this.txt_vendor_no_cell.setReadOnly();
        }

        if (this.txt_item_code_cell != null) {
            this.txt_item_code_cell.setLabelText("物料编码");
            this.txt_item_code_cell.setReadOnly();
        }
        if (this.txt_vendor_name_cell != null) {
            this.txt_vendor_name_cell.setLabelText("厂商名称");
            this.txt_vendor_name_cell.setReadOnly();
        }

        if (this.txt_vendor_model_cell != null) {
            this.txt_vendor_model_cell.setLabelText("厂家型号");
            this.txt_vendor_model_cell.setReadOnly();
        }
        if (this.txt_vendorlot_cell != null) {
            this.txt_vendorlot_cell.setLabelText("供应批次");
            this.txt_vendor_model_cell.setReadOnly();
        }
        if (this.txt_org_code_cell != null) {
            this.txt_org_code_cell.setLabelText("组织");
            this.txt_org_code_cell.setReadOnly();
        }

        if (this.txt_quantity_cell != null) {
            this.txt_quantity_cell.setLabelText("数量");
        }

        if (this.txt_date_code_cell != null) {
            this.txt_date_code_cell.setLabelText("D/C");
            this.txt_date_code_cell.setReadOnly();
        }
        if (this.txt_lot_number_cell != null) {
            this.txt_lot_number_cell.setLabelText("批次");
            this.txt_lot_number_cell.setReadOnly();
        }

        String lot_number = this.Parameters.get("lot_number", "");
        if (lot_number != null && lot_number.length() > 0) {
            this.loadLotNumber(lot_number);
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

        if (bar_code.startsWith("R:")
                || (bar_code.startsWith("R") && (bar_code.length() == 9 || bar_code
                .length() == 10)) || bar_code.length() == 8) {
            if (bar_code.startsWith("R:")) {
                this.txt_item_code_cell.setContentText(bar_code.substring(2,
                        bar_code.length()));
                this.loadVendorInfo();
            } else {
                this.txt_item_code_cell.setContentText(bar_code);
                this.loadVendorInfo();
            }
            return;
        }
    }

    public void loadLotNumber(String lotNumber) {
        String[] lot = lotNumber.split("-");
        String batchlot = pn_mm_splitlabel_editor.this.txt_lot_number_cell.getContentText().trim();
        String vendorno = lot[0].substring(3, 9);
        pn_mm_splitlabel_editor.this.txt_vendor_no_cell.setContentText(vendorno);
        pn_mm_splitlabel_editor.this.txt_quantity_cell.setContentText(lot[2]
                .toString());
        pn_mm_splitlabel_editor.this.txt_date_code_cell.setContentText(lot[3]
                .toString());
        pn_mm_splitlabel_editor.this.txt_item_code_cell.setContentText(lot[1]
                .toString());
        String sql = "exec p_get_lot_orgcode ?";
        Parameters p = new Parameters().add(1, batchlot);
        Result<DataRow> ri = App.Current.DbPortal.ExecuteRecord(this.Connector,
                sql, p);
        if (ri.HasError) {
            App.Current.showError(this.getContext(), ri.Error);
            this.txt_org_code_cell.setContentText("");
            this.txt_vendorlot_cell.setContentText("");
            this.txt_uom_cell.setContentText("");
            return;
        }
        pn_mm_splitlabel_editor.this.txt_org_code_cell.setContentText(ri.Value.getValue("org_code", ""));
        pn_mm_splitlabel_editor.this.txt_vendorlot_cell.setContentText(ri.Value.getValue("vendor_lot", ""));
        pn_mm_splitlabel_editor.this.txt_uom_cell.setContentText(ri.Value.getValue("uomcode", ""));
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
                        pn_mm_splitlabel_editor.this.txt_org_code_cell.setContentText(row.getValue(
                                "code", ""));
                    }
                    dialog.dismiss();
                }
            };
            new AlertDialog.Builder(pn_mm_splitlabel_editor.this.getContext()).setTitle("选择组织")
                    .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(pn_mm_splitlabel_editor.this.txt_org_code_cell.getContentText().toString()), listener)
                    .setNegativeButton("取消", null).show();
        }
    }

    public void loadVendorInfo() {

        String sql = "SELECT VENDOR_NO,VENDOR_NAME,MANUFACTURER_NAME,PRIMARY_VENDOR_ITEM FROM v_mm_item_vendor WHERE vendor_id is not null and ORG_ID=137 AND ITEM_NO=?  AND  (? IS NULL OR VENDOR_NO=?)";
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
                            + row.getValue("VENDOR_NAME", "") + " \n"
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
                            // _txtBook.setTag(books[which]);
                            // txtBook.setText(books[which].Name);
                        }
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(pn_mm_splitlabel_editor.this.getContext())
                        .setTitle("选择厂商")
                        .setSingleChoiceItems(names.toArray(new String[0]), 0,
                                listener).setNegativeButton("取消", null).show();
            } else {
                txt_vendor_name_cell.setContentText(result.Value.Rows.get(0).getValue(
                        "VENDOR_NAME", ""));
                txt_vendor_model_cell.setContentText(result.Value.Rows.get(0).getValue(
                        "PRIMARY_VENDOR_ITEM", ""));
                txt_vendor_no_cell.setContentText(result.Value.Rows.get(0).getValue(
                        "VENDOR_NO", ""));
            }
        }
    }

    @Override
    public void commit() {

        final String itemnum = this.txt_item_code_cell.getContentText().trim();
        final String lotnumber = this.txt_lot_number_cell.getContentText().trim();
        String vendorno = this.txt_vendor_no_cell.getContentText().trim();
        final String vendormodel = this.txt_vendor_model_cell.getContentText().trim();
        final String orgcode = this.txt_org_code_cell.getContentText().trim();
        final String datecode = this.txt_date_code_cell.getContentText().trim();
        final String qty = this.txt_quantity_cell.getContentText().trim();
        final String vendorlot = this.txt_vendorlot_cell.getContentText().trim();
        final String ut=this.txt_uom_cell.getContentText().trim();
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
										App.Current.toastError(pn_mm_splitlabel_editor.this.getContext(), value.Error);
										return;
									}
									if (value.Value != null) {
										Intent intent = new Intent();
										intent.setClass(App.Current.Workbench, Demo_ad_escActivity.class);
										String cur_date = value.Value.getValue("cur_date", "");
										intent.putExtra("cur_date", cur_date);
										        intent.putExtra("org_code", orgcode);
                        intent.putExtra("item_code",itemnum );
                        intent.putExtra("vendor_model", vendormodel);
                        intent.putExtra("lot_number", lotnumber);
                        intent.putExtra("vendor_lot", vendorlot);
                        intent.putExtra("date_code", datecode);
                        intent.putExtra("quantity", App.formatNumber(App.parseDecimal(qty, BigDecimal.ZERO), "0.####"));
                        intent.putExtra("ut", ut);
                        intent.putExtra("usercode", App.Current.UserCode);
										App.Current.Workbench.startActivity(intent);
									}
								}
							});


                        } else {
							
                        Intent intent = new Intent();
                            intent.setClass(App.Current.Workbench, frm_item_lot_printer.class);
                         intent.putExtra("org_code", orgcode);
                        intent.putExtra("item_code",itemnum );
                        intent.putExtra("vendor_model", vendormodel);
                        intent.putExtra("lot_number", lotnumber);
                        intent.putExtra("vendor_lot", vendorlot);
                        intent.putExtra("date_code", datecode);
                        intent.putExtra("quantity", App.formatNumber(App.parseDecimal(qty, BigDecimal.ZERO), "0.####"));
                        intent.putExtra("ut", ut);
                        intent.putExtra("usercode", App.Current.UserCode);
                        App.Current.Workbench.startActivity(intent);
						}
                       
                    }
                }).create();
        dialog1.show();

        //Intent intent = new Intent();
        //intent.setClass(this.getContext(), frm_item_lot_printer_init.class);
        //intent.putExtra("org_code", orgcode);
        //intent.putExtra("item_code",itemnum );
        //intent.putExtra("vendor_model", vendormodel);
        //intent.putExtra("lot_number", lotnumber);
        //intent.putExtra("vendor_lot", vendorlot);
        //intent.putExtra("date_code", datecode);
        //intent.putExtra("quantity", App.formatNumber(App.parseDecimal(qty, BigDecimal.ZERO), "0.####"));
        //intent.putExtra("ut", ut);
        //intent.putExtra("user_code", App.Current.UserCode);

        //this.getContext().startActivity(intent);
    }

    public void printLabel(DataRow row) {

    }

    public void clear() {
        this.txt_vendor_no_cell.setContentText("");
        this.txt_item_code_cell.setContentText("");
        this.txt_vendor_model_cell.setContentText("");
        this.txt_lot_number_cell.setContentText("");
        this.txt_vendor_name_cell.setContentText("");
        // this.txt_date_code_cell.setContentText("");
    }
}
