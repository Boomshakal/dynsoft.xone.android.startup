package dynsoft.xone.android.qad;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.base.BasePane;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.NestedListView;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.helper.ConvertHelper;
import dynsoft.xone.android.sbo.DocType;
import dynsoft.xone.android.sbo.Document;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

public class pige_editor extends BasePane {

    public pige_editor(Context context) {
		super(context);
	}
    
	public ScrollView Scroll;
    public LinearLayout Body;
    public TextCell WorkOrderCell;
    public ButtonTextCell FromWhsCodeCell;
    public ButtonTextCell ToWhsCodeCell;
    public TextCell LocCodeCell;
    public TextCell ItemCodeCell;
    public TextCell ItemNameCell;
    public TextCell LotCell;
    public DecimalCell QuantityCell;

    public ImageButton AddLineButton;
    public NestedListView LinesListView;
    public TableAdapter LinesAdapter;
    public TableAdapter FromWarehouseAdapter;
    public TableAdapter ToWarehouseAdapter;
    public TableAdapter LotAdapter;
    
    @Override
    public void onPrepared() {
        super.onPrepared();
        
        Scroll = (ScrollView)this.Elements.get("scroll").Object;
        Body = (LinearLayout)this.Elements.get("body").Object;
        
        this.WorkOrderCell = (TextCell)this.Elements.get("workorder").Object;
        this.FromWhsCodeCell = (ButtonTextCell)this.Elements.get("fromwarehouse").Object;
        this.ToWhsCodeCell = (ButtonTextCell)this.Elements.get("towarehouse").Object;
        this.LocCodeCell = (TextCell)this.Elements.get("loccode").Object;
        this.ItemCodeCell = (TextCell)this.Elements.get("itemcode").Object;
        this.ItemNameCell = (TextCell)this.Elements.get("itemname").Object;
        this.LotCell = (TextCell)this.Elements.get("lot").Object;
        this.QuantityCell = (DecimalCell)this.Elements.get("quantity").Object;
        this.AddLineButton = (ImageButton)this.Elements.get("add").Object;
        this.LinesListView = (NestedListView)this.Elements.get("lines").Object;
        
        if (Scroll != null) {
            LinearLayout.LayoutParams lp_scroll = new LinearLayout.LayoutParams(-1,-2);
            lp_scroll.weight = 1;
            Scroll.setLayoutParams(lp_scroll);
            Scroll.setBackgroundColor(Color.WHITE);
        }
        
        if (Body != null) {
            Body.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp_module = new LinearLayout.LayoutParams(-1,-2);
            int count = Body.getChildCount();
            for (int i=0; i<count; i++) {
                View child = Body.getChildAt(i);
                child.setLayoutParams(lp_module);
            }
        }
        
        if (this.WorkOrderCell != null) {
            this.WorkOrderCell.setLabelText("加工单ID");
        }
        
        if (this.FromWhsCodeCell != null) {
            this.FromWhsCodeCell.setLabelText("发货仓库");
            this.FromWhsCodeCell.Button.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View arg0) {
                    pige_editor.this.selectFromWarehouse();
                }
            });
        }

        if (this.ToWhsCodeCell != null) {
            this.ToWhsCodeCell.setLabelText("收货仓库");
            this.ToWhsCodeCell.Button.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View arg0) {
                    pige_editor.this.selectToWarehouse();
                }
            });
        }
        
        if (this.LocCodeCell != null) {
            this.LocCodeCell.setLabelText("库位");
        }
        
        if (this.LotCell != null) {
            this.LotCell.setLabelText("批号");
        }
        
        if (this.ItemCodeCell != null) {
            this.ItemCodeCell.setLabelText("物料编号");
        }
        
//        if (this.ItemNameCell != null) {
//            this.ItemNameCell.setLabelText("产品名称");
//            this.ItemNameCell.TextBox.setSingleLine();
//            this.ItemNameCell.TextBox.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
//        }

        if (this.QuantityCell != null) {
            this.QuantityCell.setLabelText("数量");
        }
        
        if (this.AddLineButton != null) {
            this.AddLineButton.setBackgroundColor(Color.TRANSPARENT);
            this.AddLineButton.setPadding(0, 0, 0, 0);
            this.AddLineButton.setScaleType(ScaleType.FIT_CENTER);
            this.AddLineButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_down_white"));
            this.AddLineButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    pige_editor.this.addLine();
                }
            });
        }
        
        if (this.LinesListView != null) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
            lp.height = App.dpToPx(300);
            this.LinesListView.setLayoutParams(lp);
            this.LinesListView.ScrollView = this.Scroll;
            this.LinesListView.setOnItemClickListener(new OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                    DataRow row = pige_editor.this.LinesAdapter.DataTable.Rows.get(index);
                    if (row != null) {
                        pige_editor.this.onScan(row.getValue("BatchNum", String.class));
                    }
                }
            });
            
            this.LinesAdapter = new TableAdapter(getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.pige_line, null);
                    }
                    
                    DataRow row = (DataRow)pige_editor.this.LinesAdapter.getItem(position);
                    TextView num = (TextView)convertView.findViewById(R.id.num);
                    TextView itemcode = (TextView)convertView.findViewById(R.id.itemcode);
                    TextView quantity = (TextView)convertView.findViewById(R.id.quantity);
                    TextView batchnum = (TextView)convertView.findViewById(R.id.batchnum);
                    
                    num.setText(String.valueOf(position + 1));
                    itemcode.setText(row.getValue("ItemCode", ""));
                    quantity.setText(App.formatNumber(row.getValue("Quantity"), "0.##"));
                    
                    String batchNum = row.getValue("BatchNum", "");
                    batchnum.setText(row.getValue("BatchNum", ""));
                    
                    return convertView;
                }
            };
            
            this.LinesListView.setAdapter(this.LinesAdapter);
            
            String sql = "select 0 LineNum,'' ItemCode,0.0 Quantity,'' BatchNum,0 SysNumber,'' WhsCode where 1=0";
            Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql);
            if (r.Value != null) {
                this.LinesAdapter.DataTable = r.Value;
                this.LinesAdapter.notifyDataSetChanged();
            } else if (r.HasError) {
                App.Current.showDebug(getContext(), r.Error);
            }
        }
        
        if (this.LotAdapter == null) {
            this.LotAdapter = new TableAdapter(getContext()){

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    DataRow row = (DataRow)pige_editor.this.LotAdapter.getItem(position);
                    if (convertView == null) {
                        convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.qad_owhs_line, null);
                    }

                    TextView num = (TextView)convertView.findViewById(R.id.num);
                    TextView device = (TextView)convertView.findViewById(R.id.warehouse);
                    
                    num.setText(String.valueOf(position + 1));
                    String number = row.getValue("DistNumber", String.class);
                    device.setText(number);

                    return convertView;
                }
            };
        }
    }
    
    private void selectFromWarehouse()
    {
        if (this.FromWarehouseAdapter == null) {
            this.FromWarehouseAdapter = new TableAdapter(getContext()){

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    DataRow row = (DataRow)pige_editor.this.FromWarehouseAdapter.getItem(position);
                    if (convertView == null) {
                        convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.qad_owhs_line, null);
                    }

                    TextView num = (TextView)convertView.findViewById(R.id.num);
                    TextView device = (TextView)convertView.findViewById(R.id.warehouse);
                    
                    num.setText(String.valueOf(position + 1));
                    device.setText(row.getValue("WhsName", String.class));

                    return convertView;
                }
            };
        }
        
        String sql = "select WhsCode,WhsName from OWHS";
        Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql);
        if (r.Value != null) {
            this.FromWarehouseAdapter.DataTable = r.Value;
            AlertDialog dialog = new AlertDialog.Builder(getContext())
            .setTitle("选择仓库")
            .setSingleChoiceItems(this.FromWarehouseAdapter, 0, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which < pige_editor.this.FromWarehouseAdapter.DataTable.Rows.size()) {
                        DataRow row = (DataRow)((AlertDialog)dialog).getListView().getAdapter().getItem(which);
                        if (row != null) {
                            pige_editor.this.FromWhsCodeCell.setContentText(row.getValue("WhsCode", String.class));
                        }
                        dialog.dismiss();
                    }
                }
            })
            .setNegativeButton("取消", null).create();
            dialog.show();
        }
    }
    
    private void selectToWarehouse()
    {
        if (this.ToWarehouseAdapter == null) {
            this.ToWarehouseAdapter = new TableAdapter(getContext()){

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    DataRow row = (DataRow)pige_editor.this.ToWarehouseAdapter.getItem(position);
                    if (convertView == null) {
                        convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.qad_owhs_line, null);
                    }

                    TextView num = (TextView)convertView.findViewById(R.id.num);
                    TextView device = (TextView)convertView.findViewById(R.id.warehouse);
                    
                    num.setText(String.valueOf(position + 1));
                    device.setText(row.getValue("WhsName", String.class));

                    return convertView;
                }
            };
        }
        
        String sql = "select WhsCode,WhsName from OWHS";
        Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql);
        if (r.Value != null) {
            this.ToWarehouseAdapter.DataTable = r.Value;
            AlertDialog dialog = new AlertDialog.Builder(getContext())
            .setTitle("选择仓库")
            .setSingleChoiceItems(this.ToWarehouseAdapter, 0, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which < pige_editor.this.ToWarehouseAdapter.DataTable.Rows.size()) {
                        DataRow row = (DataRow)((AlertDialog)dialog).getListView().getAdapter().getItem(which);
                        if (row != null) {
                            pige_editor.this.ToWhsCodeCell.setContentText(row.getValue("WhsCode", String.class));
                        }
                        dialog.dismiss();
                    }
                }
            })
            .setNegativeButton("取消", null).create();
            dialog.show();
        }
    }
    
    public void showLots()
    {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
        .setTitle("批次")
        .setSingleChoiceItems(this.LotAdapter, 0, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which < pige_editor.this.LotAdapter.DataTable.Rows.size()) {
                    DataRow row = (DataRow)((AlertDialog)dialog).getListView().getAdapter().getItem(which);
                    if (row != null) {
                        //pige_editor.this.ToWhsCodeCell.setContentText(row.getValue("WhsCode", String.class));
                    }
                    dialog.dismiss();
                }
            }
        })
        .setNegativeButton("取消", null).create();
        dialog.show();
    }
        
    @Override
    public void onScan(final String content)
    {
        if (content == null) return;
        
        DataTable curr_lines = this.LinesAdapter.DataTable;
        if (curr_lines == null) {
            App.Current.showError(getContext(), "明细行数据异常，请重新打开。");
            return;
        }

        final String barcode = content.trim();
        
        if (barcode.startsWith("WO-")) {
            String str = barcode.substring(3, barcode.length());
            WorkOrderCell.setContentText(str);
            return;
        }
        
        String workorder = this.WorkOrderCell.getContentText().trim();
        if (workorder.length() == 0) {
            App.Current.showError(getContext(), "请先输入或扫描加工单ID。");
            return;
        }
        
        final String fromWhsCode = this.FromWhsCodeCell.getContentText().trim();
        final String toWhsCode = this.ToWhsCodeCell.getContentText().trim();
        
        if (fromWhsCode == null || fromWhsCode.length() == 0) {
            App.Current.showError(getContext(), "请先选择发货仓库");
            return;
        }
        
        if (toWhsCode == null|| toWhsCode.length() == 0) {
            App.Current.showError(getContext(), "请先选择收货仓库");
            return;
        }
        
        if (barcode.length() < 16) {
            this.LocCodeCell.setContentText(barcode);
            return;
        }
        
        final String itemcode = barcode.substring(0, barcode.length()-16);
        //final String docnum = barcode.substring(barcode.length()-16,barcode.length() - 8).replace("*", "");
        
        String sql = "select ManSerNum,ManBtchNum from OITM where ItemCode=?";
        Parameters p = new Parameters().add(1, itemcode);
        Result<DataRow> rr = App.Current.DbPortal.ExecuteRecord(this.Connector, sql, p);
        if (rr.HasError) {
            App.Current.showInfo(getContext(), rr.Error);
            return;
        }
        
        if (rr.Value == null) {
            App.Current.showInfo(getContext(), "该物料编号在条码系统中找不到。");
            return;
        }
        
        sql = "select wod_qty_req from pub.wod_det where wod_lot=? and wod_part=?";
        p.clearAll().add(1, workorder).add(2, itemcode);
        Result<BigDecimal> rb = App.Current.DbPortal.ExecuteScalar("erp_csimc_and", sql, p, BigDecimal.class);
        if (rb.HasError) {
            App.Current.showInfo(getContext(), "查询加工单明细出错："+rb.Error);
            return;
        }
        
        if (rb.Value == null || rb.Value.compareTo(BigDecimal.ZERO) == 0) {
            App.Current.showInfo(getContext(), "加工单"+workorder+"没有"+itemcode+"这个物料");
            return;
        }
        
        final BigDecimal quantity = rb.Value;
        
        sql = "select OBTN.ItemCode,OBTN.SysNumber,OBTN.DistNumber,OBTN.LotNumber,OBTQ.Quantity,OBTN.InDate from OBTQ ";
        sql += "inner join OBTN on OBTQ.ItemCode=OBTN.ItemCode and OBTQ.SysNumber=OBTN.SysNumber ";
        sql += "where OBTN.ItemCode=? and Quantity>0 and OBTQ.WhsCode in ('CK-YL-01','CK-YL-02') order by OBTN.DistNumber ";
        p.clearAll().add(1, itemcode);
        Result<DataTable> rt = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (rt.HasError) {
            App.Current.showInfo(getContext(), "查询批次信息出错：" + rt.Error);
            return;
        }
        
        if (rt.Value == null || rt.Value.Rows.size() == 0) {
            App.Current.showInfo(getContext(), "物料"+itemcode+"没有可用库存。");
            return;
        }
        
        String firstlot = rt.Value.Rows.get(0).getValue("DistNumber", String.class);
        if (barcode.equals(firstlot) == false) {
            boolean exists = false;
            for (DataRow row : rt.Value.Rows) {
                String lot = row.getValue("DistNumber", String.class);
                if (barcode.equals(lot)) {
                    exists = true;
                    break;
                }
            }
            
            if (exists) {
                this.LotAdapter.DataTable = rt.Value;
                this.LotAdapter.notifyDataSetChanged();
                this.showLots();
            } else {
                App.Current.showInfo(getContext(), "该批次号不存在，");
                return;
            }
        } else {
            sql="select sum(Quantity) from WTR1 inner join OWTR on OWTR.DocEntry=WTR1.DocEntry where OWTR.U_BaseDocType='WO' and WTR1.ItemCode=? and WTR1.U_Lot=?";
            p = p.clearAll().add(1, itemcode).add(2, barcode);
            Result<BigDecimal> rs = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, BigDecimal.class);
            if (rs.HasError) {
                App.Current.showError(getContext(), "查询该条码是否已配料时出错：" + rs.Error);
                return;
            }
            
            if (rs.Value != null && rs.Value.compareTo(BigDecimal.ZERO) > 0) {
                App.Current.question(getContext(), "该批次条码已配了"+App.formatNumber(rs.Value, "0.##")+"到车间，确定还要再配吗？", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        
                        DataRow row = null;
                        for (DataRow dr : pige_editor.this.LinesAdapter.DataTable.Rows) {
                            String lot = dr.getValue("BatchNum", String.class);
                            if (lot != null && barcode.equals(lot) == true) {
                                row = dr;
                                ItemCodeCell.TextBox.setTag(row);
                                QuantityCell.setContentText(App.formatNumber(row.getValue("Quantity"), "0.##"));
                                break;
                            }
                        }
                        
                        pige_editor.this.ItemCodeCell.setContentText(itemcode);
                        pige_editor.this.LotCell.setContentText(barcode);
                        //pige_editor.this.QuantityCell.setContentText(App.formatNumber(quantity, "0.##"));
                    }
                });
            } else {
                DataRow row = null;
                for (DataRow dr : pige_editor.this.LinesAdapter.DataTable.Rows) {
                    String lot = dr.getValue("BatchNum", String.class);
                    if (lot != null && barcode.equals(lot) == true) {
                        row = dr;
                        ItemCodeCell.TextBox.setTag(row);
                        QuantityCell.setContentText(App.formatNumber(row.getValue("Quantity"), "0.##"));
                        break;
                    }
                }
                
                this.ItemCodeCell.setContentText(itemcode);
                this.LotCell.setContentText(barcode);
                //this.QuantityCell.setContentText(App.formatNumber(rb.Value, "0.##"));
            }
        }
    }
    
    private void addLine()
    {       
        final String workorder = WorkOrderCell.getContentText();
        if (workorder == null || workorder.length() == 0) {
            App.Current.showError(getContext(), "缺少加工单ID。");
            return;
        }
        
        final String fromWhsCode = this.FromWhsCodeCell.getContentText().trim();
        final String toWhsCode = this.ToWhsCodeCell.getContentText().trim();
        
        if (fromWhsCode == null || fromWhsCode.length() == 0) {
            App.Current.showError(getContext(), "请先选择发货仓库");
            return;
        }
        
        if (toWhsCode == null|| toWhsCode.length() == 0) {
            App.Current.showError(getContext(), "请先选择收货仓库");
            return;
        }
        
        final String itemcode = ItemCodeCell.getContentText();
        if (itemcode == null || itemcode.length() == 0) {
            App.Current.showError(getContext(), "缺少物料编号。");
            return;
        }
        
        final String batchnum = LotCell.getContentText();
        if (batchnum == null || batchnum.length() == 0) {
            App.Current.showError(getContext(), "缺少批次编号。");
            return;
        }
        
        final String loccode = LocCodeCell.getContentText();
        if (loccode == null || loccode.length() == 0) {
            App.Current.showError(getContext(), "缺少库位编号。");
            return;
        }
        
        final String quantity = QuantityCell.getContentText();
        BigDecimal qty = ConvertHelper.StringToBigDecimal(quantity, BigDecimal.ZERO);
        if (qty == BigDecimal.ZERO) {
            App.Current.showError(getContext(), "请输入数量。");
            return;
        }
        
        String sql = "select ManSerNum,ManBtchNum,DfltWH,U_Loc from OITM where ItemCode=?";
        Parameters p = new Parameters().add(1, itemcode);
        Result<DataRow> rr = App.Current.DbPortal.ExecuteRecord(this.Connector, sql, p);
        if (rr.HasError) {
            App.Current.showInfo(getContext(), "查询物料信息出错：" + rr.Error);
            return;
        }
        
        DataRow row_oitm = rr.Value;
        if (row_oitm == null) {
            App.Current.showInfo(getContext(), "该物料编号在条码系统中找不到。");
            return;
        }
        
        String dfltloc = row_oitm.getValue("U_Loc", String.class);
        if (dfltloc != null && dfltloc.length() > 0) {
            if (dfltloc.equals(loccode) == false) {
                App.Current.showInfo(getContext(), "物料编号和库位编号不匹配。");
                return;
            }
        }
        
        sql = "select SysNumber,MnfSerial,LotNumber from OBTN where ItemCode=? and DistNumber=?";
        p = p.clearAll().add(1, itemcode).add(2, batchnum);
        rr = App.Current.DbPortal.ExecuteRecord(this.Connector, sql, p);
        if (rr.HasError) {
            App.Current.showError(getContext(), "查询批次检验信息出错："+rr.Error);
            return;
        }
        
        DataRow row_obtn = rr.Value;
        if (row_obtn == null) {
            App.Current.showError(getContext(), "该批次物料不存在。");
            return;
        }
        
        int sysNumber = row_obtn.getValue("SysNumber", Integer.class);
        String mnfSerial = row_obtn.getValue("MnfSerial", String.class);
        String lotNumber = row_obtn.getValue("LotNumber", String.class);
        if (mnfSerial == null || lotNumber == null || mnfSerial.length() == 0 || lotNumber.length() == 0) {
            App.Current.showError(getContext(), "该批次物料还没有检验。");
            return;
        }
        
        sql = "select wod_qty_req,wod_op from pub.wod_det where wod_lot=? and wod_part=?";
        p.clearAll().add(1, workorder).add(2, itemcode);
        Result<DataRow> rb = App.Current.DbPortal.ExecuteRecord("erp_csimc_and", sql, p);
        if (rb.HasError) {
            App.Current.showInfo(getContext(), "查询加工单明细出错："+rb.Error);
            return;
        }
        
        if (rb.Value == null) {
            App.Current.showInfo(getContext(), "加工单"+workorder+"没有"+itemcode+"这个物料");
            return;
        }
        
        BigDecimal qty_req = rb.Value.getValue("wod_qty_req", BigDecimal.class);
        String wo_op = rb.Value.getValue("wod_op").toString();
        
        if (qty_req == null || qty_req.compareTo(BigDecimal.ZERO) == 0) {
            App.Current.showInfo(getContext(), "加工单"+workorder+"没有"+itemcode+"这个物料");
            return;
        }
        
        if (wo_op.equals("100") == false && qty.compareTo(qty_req) > 0) {
            App.Current.showInfo(getContext(), "配料数量大于加工单需求量。");
        }
        
        if (this.LinesAdapter.DataTable != null) {
            DataRow row = (DataRow)this.ItemCodeCell.TextBox.getTag();
            if (row == null) {
                
                row = this.LinesAdapter.DataTable.NewRow();
                row.setValue("LineNum", this.LinesAdapter.DataTable.Rows.size()+1);
                row.setValue("ItemCode", itemcode);
                row.setValue("BatchNum", batchnum);
                row.setValue("SysNumber", sysNumber);
                row.setValue("ManSerNum", row_oitm.getValue("ManSerNum", String.class));
                row.setValue("ManBtchNum", row_oitm.getValue("ManBtchNum", String.class));
                this.LinesAdapter.DataTable.Rows.add(row);
            } else {
                row.setValue("ItemCode", itemcode);
                row.setValue("BatchNum", batchnum);
                row.setValue("SysNumber", sysNumber);
                row.setValue("ManSerNum", row_oitm.getValue("ManSerNum", String.class));
                row.setValue("ManBtchNum", row_oitm.getValue("ManBtchNum", String.class));
            }
            
            row.setValue("Quantity", qty);
            this.LinesAdapter.notifyDataSetChanged();
            
            ItemCodeCell.TextBox.setTag(null);
            ItemCodeCell.setContentText("");
            QuantityCell.setContentText("");
            LotCell.setContentText("");
        }
    }
    @Override
    public void save()
    {
        final String fromWhsCode = this.FromWhsCodeCell.getContentText().trim();
        if (fromWhsCode == null || fromWhsCode.length() == 0) {
            App.Current.showInfo(getContext(), "缺少发货仓库编号，不能提交。");
            return;
        }
        
        final String toWhsCode = this.ToWhsCodeCell.getContentText().trim();
        if (toWhsCode == null || toWhsCode.length() == 0) {
            App.Current.showInfo(getContext(), "缺少收货仓库编号，不能提交。");
            return;
        }
        
        final String workorder = this.WorkOrderCell.getContentText().trim();
        if (workorder == null || workorder.length() == 0) {
            App.Current.showInfo(getContext(), "缺少加工单ID，不能提交。");
            return;
        }
        
        String lot = this.LotCell.getContentText().trim();
        String itemcode = this.ItemCodeCell.getContentText().trim();
        if (lot != null && lot.length() > 0 && itemcode != null && itemcode.length() > 0) {
            this.addLine();
        }
        
        if (this.LinesAdapter.DataTable == null || this.LinesAdapter.DataTable.Rows.size() == 0) {
            App.Current.showInfo(getContext(), "没有明细行，不能提交。");
            return;
        }
        
        App.Current.question(getContext(), "单据提交后不能修改，确定要提交吗？", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Document doc = new Document(DocType.OWTR);

                Map<String, String> body = new HashMap<String, String>();
                //body.put("DocDate", docDate);
                //body.put("TaxDate", taxDate);
                body.put("FromWarehouse", fromWhsCode);
                body.put("U_BaseDocType", "WO");
                body.put("U_BaseDocNum", workorder);
                body.put("U_UserID", App.Current.UserID);
                body.put("U_UserCode", App.Current.UserCode);
                body.put("U_UserName", App.Current.UserName);
                
                doc.setBody(body);
                
                if (pige_editor.this.LinesAdapter.DataTable != null) {
                    int lineNum = 0;
                    for (DataRow row : pige_editor.this.LinesAdapter.DataTable.Rows) {
                        
                        String itemcode = row.getValue("ItemCode", String.class);
                        String quantity = row.getValue("Quantity").toString();
                        String batchNum = row.getValue("BatchNum", String.class);
                        
                        Map<String, String> line = new HashMap<String, String>();
                        line.put("LineNum", String.valueOf(lineNum));
                        line.put("ItemCode", itemcode);
                        line.put("WarehouseCode", toWhsCode);
                        line.put("Quantity", quantity);
                        line.put("U_Lot", batchNum);
                        doc.addLine(line);
                        
                        String manSerial = row.getValue("ManSerNum", "N");
                        String manBatch = row.getValue("ManBtchNum", "N");
                        if (manSerial.equals("Y")) {
                            if (batchNum != null && batchNum.length() > 0) {
                                doc.addSerial(String.valueOf(lineNum), batchNum, false);
                            }
                        } else if (manBatch.equals("Y")) {
                            if (batchNum != null && batchNum.length() > 0) {
                                Map<String, String> batch = new HashMap<String, String>();
                                batch.put("BaseLineNumber", String.valueOf(lineNum));
                                batch.put("BatchNumber", batchNum);
                                batch.put("Quantity", quantity);
                                //batch.put("InternalSerialNumber", batchNum);
                                doc.addBatch(batch);
                            }
                        }
                        
                        lineNum++;
                    }
                }
                
                Result<String> result = doc.save();
                if (result.Value != null) {
                    
                    App.Current.showInfo(pige_editor.this.getContext(), "提交成功！单据号："+result.Value);
                    
                    WorkOrderCell.setContentText("");
                    ItemCodeCell.TextBox.setTag(null);
                    ItemCodeCell.setContentText("");
                    LotCell.setContentText("");
                    QuantityCell.setContentText("");
                    
                    pige_editor.this.LinesAdapter.DataTable.Rows.clear();
                    pige_editor.this.LinesAdapter.notifyDataSetChanged();
                    
                } else if (result.HasError) {
                    App.Current.showError(pige_editor.this.getContext(), "提交失败！" + result.Error);
                }
            }
        });
    }

}
