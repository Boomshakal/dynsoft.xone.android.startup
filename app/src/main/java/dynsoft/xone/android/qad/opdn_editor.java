package dynsoft.xone.android.qad;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.text.TextUtils;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import dynsoft.xone.android.adapter.PageTableAdapter;
import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.base.BasePane;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DateCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.NestedListView;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.control.ToolBarCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.helper.ConvertHelper;
import dynsoft.xone.android.mes.PnFeedEditor;
import dynsoft.xone.android.sbo.odoc_editor;
import dynsoft.xone.android.sbo.Document;
import dynsoft.xone.android.sbo.DocType;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

public class opdn_editor extends BasePane {

    public opdn_editor(Context context) {
		super(context);
	}

	public ScrollView Scroll;
    public LinearLayout Body;
    public TextCell WhsCodeCell;
    public TextCell DocNumCell;
    public TextCell CardCodeCell;
    public TextCell CardNameCell;
    public TextCell ItemCodeCell;
    public TextCell ItemNameCell;
    public TextCell BatchNumCell;
    public DecimalCell QuantityCell;
    public ToolBarCell CommandCell;
    public ImageButton AddLineButton;
    public NestedListView LinesListView;
    public TableAdapter LinesAdapter;
    
    @Override
    public void onPrepared() {
        super.onPrepared();
        
        Scroll = (ScrollView)this.Elements.get("scroll").Object;
        Body = (LinearLayout)this.Elements.get("body").Object;
        this.WhsCodeCell = (TextCell)this.Elements.get("warehouse").Object;
        this.DocNumCell = (TextCell)this.Elements.get("docnum").Object;
        this.CardCodeCell = (TextCell)this.Elements.get("cardcode").Object;
        //this.CardNameCell = (TextCell)this.Elements.get("cardname").Object;
        this.ItemCodeCell = (TextCell)this.Elements.get("itemcode").Object;
        //this.ItemNameCell = (TextCell)this.Elements.get("itemname").Object;
        this.BatchNumCell = (TextCell)this.Elements.get("batchnum").Object;
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
        
        if (this.WhsCodeCell != null) {
            this.WhsCodeCell.setLabelText("收货仓库");
            
            String sql = "select value from config where code=? and [key]=?";
            Parameters p = new Parameters().add(1, "erp_csimc_opdn_dflt_warehouse").add(2, "whscode");
            Result<String> r = App.Current.DbPortal.ExecuteScalar(App.Current.BookConnector, sql, p, String.class);
            if (r.Value != null) {
                this.WhsCodeCell.setContentText(r.Value);
            } else if (r.HasError) {
                App.Current.showError(getContext(), r.Error);
            }
        }
        
        if (this.DocNumCell != null) {
            this.DocNumCell.setLabelText("采购单号");
        }
        
        if (this.CardCodeCell != null) {
            this.CardCodeCell.setLabelText("供应商");
        }
        
        if (this.CardNameCell != null) {
            this.CardNameCell.setLabelText("名称");
            this.CardNameCell.TextBox.setKeyListener(null);
            this.CardNameCell.TextBox.setFocusable(false);
            
            this.CardNameCell.TextBox.setSingleLine();
            this.CardNameCell.TextBox.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        }
        
        if (this.ItemCodeCell != null) {
            this.ItemCodeCell.setLabelText("物料编号");
        }
        
        if (this.ItemNameCell != null) {
            this.ItemNameCell.setLabelText("物料名称");
            this.ItemNameCell.TextBox.setSingleLine();
            this.ItemNameCell.TextBox.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        }
        
        if (this.QuantityCell != null) {
            this.QuantityCell.setLabelText("数量");
        }
        
        if (this.BatchNumCell != null) {
            this.BatchNumCell.setLabelText("批次号");
        }
        
        if (this.CommandCell != null) {
            this.CommandCell.initButtonLayout();
        }
        
        if (this.AddLineButton != null) {
            this.AddLineButton.setBackgroundColor(Color.TRANSPARENT);
            this.AddLineButton.setPadding(0, 0, 0, 0);
            this.AddLineButton.setScaleType(ScaleType.FIT_CENTER);
            this.AddLineButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_down_white"));
            this.AddLineButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    opdn_editor.this.addLine();
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
                    DataRow row = opdn_editor.this.LinesAdapter.DataTable.Rows.get(index);
                    if (row != null) {
                        String batchnum = row.getValue("BatchNum", String.class);
                        opdn_editor.this.onScan(batchnum);
                    }
                }
            });
            
            this.LinesAdapter = new TableAdapter(getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.qad_opdn_line, null);
                        ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                        icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
                    }
                    
                    DataRow row = (DataRow)opdn_editor.this.LinesAdapter.getItem(position);
                    TextView num = (TextView)convertView.findViewById(R.id.num);
                    TextView itemcode = (TextView)convertView.findViewById(R.id.itemcode);
                    TextView quantity = (TextView)convertView.findViewById(R.id.quantity);
                    TextView batchnum = (TextView)convertView.findViewById(R.id.batchnum);
                    
                    num.setText(row.getValue("LineNum").toString());
                    itemcode.setText("编号: " + row.getValue("ItemCode", ""));
                    quantity.setText("数量: " + App.formatNumber(row.getValue("Quantity", BigDecimal.class), "0.##"));
                    batchnum.setText(row.getValue("BatchNum", ""));
                    
                    if (batchnum.getText().length() > 0) {
                        batchnum.setVisibility(View.VISIBLE);
                    } else {
                        batchnum.setVisibility(View.GONE);
                    }
                    
                    return convertView;
                }
            };
            
            this.LinesListView.setAdapter(this.LinesAdapter);
            
            String sql = "select 0 LineNum,0 OrderLine,'' ItemCode,'' ItemName,'' BatchNum,'' ManSerNum,'' ManBtchNum,cast(0 as decimal(19,6)) Quantity where 1=0";
            Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql);
            if (r.Value != null) {
                this.LinesAdapter.DataTable = r.Value;
                this.LinesAdapter.notifyDataSetChanged();
            } else if (r.HasError) {
                App.Current.showDebug(getContext(), r.Error);
            }
        }
    }
   
    
    @Override
    public void onScan(final String content)
    {
        if (content == null) return;
        
        final String barcode = content.trim();
        
        if (barcode.length() < 16) return;
        
        DataTable curr_lines = this.LinesAdapter.DataTable;
        if (curr_lines == null) {
            App.Current.showError(getContext(), "明细行数据异常，请重新打开。");
            return;
        }
        
        final String itemcode = barcode.substring(0, barcode.length()-16);
        final String docnum = barcode.substring(barcode.length()-16,barcode.length() - 8).replace("*", "");
        
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

        String curr_docnum = this.DocNumCell.getContentText().trim();
        DataRow curr_docrow = (DataRow)this.DocNumCell.TextBox.getTag();
        
        if (curr_docrow != null && curr_lines != null && curr_lines.Rows.size() > 0) {
            if (curr_docnum.compareTo(docnum) !=0 ) {
                App.Current.showError(getContext(), "该条码不属于当前采购订单。");
                return;
            }
        }
        
        sql = "select sum(Quantity) from IGN1 inner join OIGN on OIGN.DocEntry=IGN1.DocEntry where OIGN.U_BaseDocType='PO' and IGN1.ItemCode=? and IGN1.U_Lot=?";
        p = p.clearAll().add(1, itemcode).add(2, barcode);
        Result<BigDecimal> rs = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, BigDecimal.class);
        if (rs.HasError) {
            App.Current.showError(getContext(), "查询该条码是否已收货时出错：" + rs.Error);
            return;
        }
        
        if (rs.Value != null && rs.Value.compareTo(BigDecimal.ZERO)>0) {
            App.Current.question(getContext(), "该批次条码已收了"+App.formatNumber(rs.Value, "0.##")+"，确定还要再收吗？", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    opdn_editor.this.queryBarcode(barcode, docnum, itemcode);
                }
            });
        } else {
            opdn_editor.this.queryBarcode(barcode, docnum, itemcode);
        }
    }
    
    private void queryBarcode(String barcode, String docnum, String itemcode)
    {
        DataRow row = null;
        for (DataRow dr : opdn_editor.this.LinesAdapter.DataTable.Rows) {
            String lot = dr.getValue("BatchNum", String.class);
            if (lot != null && barcode.equals(lot) == true) {
                row = dr;
                break;
            }
        }
        
        if (row == null) {
            String sql = "select top 1 po_nbr,po_sched,po_vend,po_ord_date,po_due_date,po_contract,po_buyer,vd_sort 'vd_name',vd_curr,gl_base_curr,exr_rate,exr_rate2,pod_line,pod_part,pt_loc,(pt_desc1+pt_desc2) 'pt_desc' from pub.pod_det ";
            sql+="inner join pub.po_mstr on po_nbr=pod_nbr ";
            sql+="inner join pub.pt_mstr on pt_part=pod_part ";
            sql+="inner join pub.vd_mstr on po_vend=vd_addr and po_domain=vd_domain ";
            sql+="inner join pub.gl_ctrl on po_domain=gl_domain ";
            sql+="left join pub.exr_rate on exr_curr1=gl_base_curr and exr_curr2=vd_curr and exr_start_date<=sysdate() and sysdate()<=exr_end_date ";
            sql+="where pod_nbr=? and pod_part=?";
            Parameters p = new Parameters().add(1, docnum).add(2, itemcode);
            Result<DataRow> rr = App.Current.DbPortal.ExecuteRecord("erp_csimc_and", sql, p);
            if (rr.Value != null) {
                row = rr.Value;
                DocNumCell.TextBox.setTag(row);
                DocNumCell.setContentText(row.getValue("po_nbr", String.class));
                CardCodeCell.setContentText(row.getValue("po_vend", String.class));
            } else if (rr.HasError) {
                App.Current.showError(opdn_editor.this.getContext(), rr.Error);
                return;
            } else {
                App.Current.showError(opdn_editor.this.getContext(), "条码包含的采购订单或物料编号不存在。");
                return;
            }
            
            BatchNumCell.setContentText(barcode);
            ItemCodeCell.setContentText(row.getValue("pod_part", String.class));
            QuantityCell.TextBox.requestFocus();
            
        } else {
            ItemCodeCell.setTag(row);
            BatchNumCell.setContentText(barcode);
            ItemCodeCell.setContentText(itemcode);
            QuantityCell.setContentText(row.getValue("Quantity", BigDecimal.class).toString());
            QuantityCell.TextBox.requestFocus();
        }
    }
    
    private void addLine()
    {
        final String docnum = DocNumCell.getContentText();
        if (docnum == null || docnum.length() == 0) {
            App.Current.showError(getContext(), "缺少采购单号。");
            return;
        }
        
        DataRow row_po = (DataRow)DocNumCell.TextBox.getTag();
        if (row_po == null) {
            App.Current.showError(getContext(), "缺少采购订单数据。");
            return;
        }
        
        final String cardcode = CardCodeCell.getContentText();
        if (cardcode == null || cardcode.length() == 0) {
            App.Current.showError(getContext(), "缺少供应商编号。");
            return;
        }
        
        final String itemcode = ItemCodeCell.getContentText();
        if (itemcode == null || itemcode.length() == 0) {
            App.Current.showError(getContext(), "缺少物料编号。");
            return;
        }
        
        String sql = "select ManSerNum,ManBtchNum from OITM where ItemCode=?";
        Parameters p = new Parameters().add(1, itemcode);
        Result<DataRow> row_oitm = App.Current.DbPortal.ExecuteRecord(this.Connector, sql, p);
        if (row_oitm.HasError) {
            App.Current.showInfo(getContext(), row_oitm.Error);
            return;
        }
        
        if (row_oitm.Value == null) {
            App.Current.showInfo(getContext(), "该物料编号在条码系统中找不到。");
            return;
        }
        
        final String quantity = QuantityCell.getContentText();
        BigDecimal qty = ConvertHelper.StringToBigDecimal(quantity, BigDecimal.ZERO);
        if (qty == BigDecimal.ZERO) {
            App.Current.showError(getContext(), "请输入数量。");
            return;
        }
        
        //检查输入的数量是否超过采购订单的未收数量
//        String sql = "";
//        Parameters p = new Parameters();
//        boolean sched = row_po.getValue("po_sched", Boolean.class);
//        if (sched) { //日程订单
//            sql = "select top 1 (ifnull(max(schd_cum_qty),0)-pod_qty_rcvd) 'quantity' from pub.pod_det "
//                   + "left join pub.schd_det on schd_type=4 and schd_nbr=pod_nbr and schd_line=pod_line and rtrim(pod_curr_rlse_id,';')=schd_rlse_id "
//                   + "where schd_nbr=? and schd_line=? and schd_date<=sysdate() group by pod_nbr,pod_line,pod_qty_rcvd";
//            p.add(1, docnum).add(2, row_po.getValue("pod_line"));
//        } else { //普通订单
//            sql = "select (pod_qty_ord-pod_qty_rcvd) as 'quantity' "
//                + " from pub.pod_det where pod_nbr=? and pod_line=?";
//            p.add(1, docnum).add(2, row_po.getValue("pod_line"));
//        }
//        
//        Result<BigDecimal> r = App.Current.DbPortal.ExecuteScalar("erp_csimc_and", sql, p, BigDecimal.class);
//        if (r.HasError || r.Value == null) {
//            App.Current.showError(getContext(), r.Error);
//            return;
//        }
//        
//        if (r.Value == BigDecimal.ZERO) {
//            App.Current.showError(getContext(), "采购订单中该物料已经收完。");
//            return;
//        }
//        
//        if (qty.compareTo(r.Value) > 0) {
//            App.Current.showError(getContext(), "输入数量大于采购订单的未收数量（"+App.formatNumber(r.Value, "0.##")+"）。");
//            return;
//        }
        
        final String batchnum = BatchNumCell.getContentText();

        if (this.LinesAdapter.DataTable != null) {
            DataRow row = (DataRow)this.ItemCodeCell.getTag();
            if (row == null) {
                
                row = this.LinesAdapter.DataTable.NewRow();
                row.setValue("LineNum", this.LinesAdapter.DataTable.Rows.size()+1);
                row.setValue("OrderLine", row_po.getValue("pod_line"));
                row.setValue("ItemCode", itemcode);
                row.setValue("BatchNum", batchnum);
                row.setValue("Quantity", qty);
                row.setValue("ManSerNum", row_oitm.Value.getValue("ManSerNum", String.class));
                row.setValue("ManBtchNum", row_oitm.Value.getValue("ManBtchNum", String.class));
                this.LinesAdapter.DataTable.Rows.add(row);
            } else {
                row.setValue("OrderLine", row_po.getValue("pod_line"));
                row.setValue("ItemCode", itemcode);
                row.setValue("BatchNum", batchnum);
                row.setValue("Quantity", qty);
                row.setValue("ManSerNum", row_oitm.Value.getValue("ManSerNum", String.class));
                row.setValue("ManBtchNum", row_oitm.Value.getValue("ManBtchNum", String.class));
            }

            this.LinesAdapter.notifyDataSetChanged();
            
            ItemCodeCell.TextBox.setTag(null);
            ItemCodeCell.setContentText("");
            QuantityCell.setContentText("");
            BatchNumCell.setContentText("");
        }
    }
    
    @Override
    public void save()
    {
        final String whscode = this.WhsCodeCell.getContentText().trim();
        if (whscode == null || whscode.length() == 0) {
            App.Current.showInfo(getContext(), "缺少收货仓库，不能提交。");
            return;
        }
        
        final String docnum = this.DocNumCell.getContentText().trim();
        if (docnum == null || docnum.length() == 0) {
            App.Current.showInfo(getContext(), "缺少采购单号，不能提交。");
            return;
        }
        
        final DataRow row_po = (DataRow)this.DocNumCell.TextBox.getTag();
        if (row_po == null) {
            App.Current.showInfo(getContext(), "缺少采购单数据，不能提交。");
            return;
        }
        
        final String cardcode = this.CardCodeCell.getContentText().trim();
        if (cardcode == null || cardcode.length() == 0) {
            App.Current.showInfo(getContext(), "缺少供应商编号，不能提交。");
            return;
        }
        
        if (this.LinesAdapter.DataTable == null || this.LinesAdapter.DataTable.Rows.size() == 0) {
            App.Current.showInfo(getContext(), "没有明细行，不能提交。");
            return;
        }
        
        App.Current.question(getContext(), "单据提交后不能修改，确定要提交吗？", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Document doc = new Document(DocType.OIGN);
                String docDate = App.formatCalendar(Calendar.getInstance(), "yyyy-MM-dd");
                String taxDate = App.formatCalendar(Calendar.getInstance(), "yyyy-MM-dd");
                String number = row_po.getValue("po_nbr", String.class);
                String cardCode = row_po.getValue("po_vend", String.class);
                String cardName = row_po.getValue("vd_name", String.class);
                String orderDate = App.formatDateTime(row_po.getValue("po_ord_date"), "yyyy-MM-dd");
                String dueDate = App.formatDateTime(row_po.getValue("po_due_date"), "yyyy-MM-dd");
                String contract = row_po.getValue("po_contract", String.class);
                String buyer = row_po.getValue("po_buyer", String.class);
                
                Map<String, String> body = new HashMap<String, String>();
                body.put("DocDate", docDate);
                body.put("TaxDate", taxDate);
                body.put("CardCode", cardCode);
                body.put("CardName", cardName);
                body.put("U_BaseDocType", "PO");
                body.put("U_BaseDocDate", orderDate);
                body.put("U_BaseDueDate", dueDate);
                body.put("U_BaseDocNum", number);
                body.put("U_BaseContract", contract);
                body.put("U_BasePerson", buyer);
                
                doc.setBody(body);
                
                if (opdn_editor.this.LinesAdapter.DataTable != null) {
                    int lineNum = 0;
                    for (DataRow row : opdn_editor.this.LinesAdapter.DataTable.Rows) {

                        String itemcode = row.getValue("ItemCode", String.class);
                        String quantity = row.getValue("Quantity").toString();
                        String batchNum = row.getValue("BatchNum", String.class);
                        
                        Map<String, String> line = new HashMap<String, String>();
                        line.put("LineNum", String.valueOf(lineNum));
                        line.put("ItemCode", itemcode);
                        line.put("WarehouseCode", whscode);
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
                                doc.addBatch(batch);
                            }
                        }
                        
                        lineNum++;
                    }
                }
                
                Result<String> result = doc.save();
                if (result.Value != null) {
                    
                    DocNumCell.TextBox.setTag(null);
                    DocNumCell.setContentText("");
                    CardCodeCell.setContentText("");
                    ItemCodeCell.TextBox.setTag(null);
                    ItemCodeCell.setContentText("");
                    QuantityCell.setContentText("");
                    BatchNumCell.setContentText("");
                    
                    App.Current.showInfo(opdn_editor.this.getContext(), "提交成功。");
                    opdn_editor.this.LinesAdapter.DataTable.Rows.clear();
                    opdn_editor.this.LinesAdapter.notifyDataSetChanged();
                    
                } else if (result.HasError) {
                    App.Current.showError(opdn_editor.this.getContext(), "提交失败！" + result.Error);
                }
            }
        });
    }
}
