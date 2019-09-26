package dynsoft.xone.android.qad;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import dynsoft.xone.android.base.BasePane;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.sbo.DocType;
import dynsoft.xone.android.sbo.Document;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

public class oiqc_editor extends BasePane {

    public oiqc_editor(Context context) {
		super(context);
		
	}

	public ScrollView Scroll;
    public LinearLayout Body;
    
    public TextCell BatchNumCell;
    public TextCell ItemCodeCell;
    public TextCell ItemNameCell;
    
    public TextCell WhsCodeCell;
    public TextCell SampleWhsCodeCell;
    public DecimalCell SampleQuantityCell;
    public TextCell CheckerCell;
    
    @Override
    public void onPrepared() {
        super.onPrepared();
        
        Scroll = (ScrollView)this.Elements.get("scroll").Object;
        Body = (LinearLayout)this.Elements.get("body").Object;
        
        this.BatchNumCell = (TextCell)this.Elements.get("batchnum").Object;
        this.ItemCodeCell = (TextCell)this.Elements.get("itemcode").Object;
        this.ItemNameCell = (TextCell)this.Elements.get("itemname").Object;
        this.WhsCodeCell = (TextCell)this.Elements.get("warehouse").Object;
        
        this.SampleWhsCodeCell = (TextCell)this.Elements.get("spwarehouse").Object;
        this.SampleQuantityCell = (DecimalCell)this.Elements.get("spquantity").Object;
        this.CheckerCell = (TextCell)this.Elements.get("checker").Object;
        
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
        
        if (this.BatchNumCell != null) {
            this.BatchNumCell.setLabelText("批次号");
        }
        
        if (this.ItemCodeCell != null) {
            this.ItemCodeCell.setLabelText("物料编号");
        }
        
        if (this.ItemNameCell != null) {
            this.ItemNameCell.setLabelText("物料名称");
            this.ItemNameCell.TextBox.setSingleLine();
            this.ItemNameCell.TextBox.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        }
        
        if (this.WhsCodeCell != null) {
            this.WhsCodeCell.setLabelText("待检仓库");
            String sql = "select value from config where code=? and [key]=?";
            Parameters p = new Parameters().add(1, "erp_csimc_oiqc_dflt_warehouse").add(2, "whscode");
            Result<String> r = App.Current.DbPortal.ExecuteScalar(App.Current.BookConnector, sql, p, String.class);
            if (r.Value != null) {
                this.WhsCodeCell.setContentText(r.Value);
            } else if (r.HasError) {
                App.Current.showError(getContext(), r.Error);
            }
        }
        
        if (this.SampleWhsCodeCell != null) {
            this.SampleWhsCodeCell.setLabelText("样品仓库");
            String sql = "select value from config where code=? and [key]=?";
            Parameters p = new Parameters().add(1, "erp_csimc_oiqc_dflt_sample_whscode").add(2, "whscode");
            Result<String> r = App.Current.DbPortal.ExecuteScalar(App.Current.BookConnector, sql, p, String.class);
            if (r.Value != null) {
                this.SampleWhsCodeCell.setContentText(r.Value);
            } else if (r.HasError) {
                App.Current.showError(getContext(), r.Error);
            }
        }
        
        if (this.SampleQuantityCell != null) {
            this.SampleQuantityCell.setLabelText("样品数量");
        }
        
        if (this.CheckerCell != null) {
            this.CheckerCell.setLabelText("检验人");
            this.CheckerCell.setContentText(App.Current.UserName);
        }
    }
   
    @Override
    public void onScan(String barcode)
    {
        if (barcode == null) return;
        
        String whscode = this.WhsCodeCell.getContentText().trim();
        if (whscode.length() == 0) {
            App.Current.showError(getContext(), "请扫描待检仓库编号。");
            return;
        }
        
        barcode = barcode.trim();
        if (barcode.length() < 16) {
            App.Current.showError(getContext(), barcode+"\n无效的物料批次条码。");
            return;
        }
        
        String itemcode = barcode.substring(0, barcode.length()-16);
        
        String sql="select OBTQ.ItemCode,OITM.ItemName,DistNumber,SUM(Quantity) Quantity,MnfSerial,LotNumber from OBTQ ";
        sql+="inner join OBTN on OBTN.ItemCode=OBTQ.ItemCode and OBTQ.SysNumber=OBTN.SysNumber ";
        sql+="inner join OITM on OITM.ItemCode=OBTQ.ItemCode ";
        sql+="where OBTN.ItemCode=? and OBTN.DistNumber=? and OBTQ.WhsCode=? ";
        sql+="group by OBTQ.ItemCode,ItemName,DistNumber,MnfSerial,LotNumber";

        Parameters p = new Parameters().add(1, itemcode).add(2, barcode).add(3, whscode);
        Result<DataRow> rr = App.Current.DbPortal.ExecuteRecord(this.Connector, sql, p);
        if (rr.Value != null) {
            DataRow row = rr.Value;
            String mnfSerial = row.getValue("MnfSerial", String.class);
            String lotNumber = row.getValue("LotNumber", String.class);
            
            if (mnfSerial != null && lotNumber != null && mnfSerial.length() > 0 && lotNumber.length() > 0) {
                String message = "该批次物料已经检验过：\n";
                message += "日期：" + mnfSerial + "\n";
                message += "检验人：" + lotNumber + "\n" ;
                App.Current.showError(getContext(), message);
                return;
            }
            
            BatchNumCell.TextBox.setTag(row);
            BatchNumCell.setContentText(row.getValue("DistNumber", String.class));
            ItemCodeCell.setContentText(row.getValue("ItemCode", String.class));
            ItemNameCell.setContentText(row.getValue("ItemName", String.class));
            SampleQuantityCell.TextBox.requestFocus();

        } else if (rr.HasError) {
            App.Current.showError(getContext(), rr.Error);
            return;
        } else {
            App.Current.showError(getContext(), "该批次条码不存在。");
            return;
        }
    }
    
    public void save()
    {
        final String lot = (String)this.BatchNumCell.getContentText();
        if (lot == null) {
            App.Current.showInfo(getContext(), "缺少批次号，不能提交。");
            return;
        }
        
        final String itemcode = this.ItemCodeCell.getContentText().trim();
        if (itemcode == null || itemcode.length() == 0) {
            App.Current.showInfo(getContext(), "缺少物料编号，不能提交。");
            return;
        }
        
        final String whscode = this.WhsCodeCell.getContentText().trim();
        if (whscode == null || whscode.length() == 0) {
            App.Current.showInfo(getContext(), "缺少待检仓库，不能提交。");
            return;
        }
        
        final String checker = this.CheckerCell.getContentText().trim();
        if (checker == null || checker.length() == 0) {
            App.Current.showInfo(getContext(), "缺少检验人，不能提交。");
            return;
        }
        
        final String sp_whscode = this.SampleWhsCodeCell.getContentText().trim();
        final String sp_quantity = this.SampleQuantityCell.getContentText().trim();
        
        final BigDecimal sampleQuantity = App.parseDecimal(sp_quantity, BigDecimal.ZERO);
        if(SampleQuantityCell.TextBox.getTag() != null) {
            BigDecimal lotQuantity = (BigDecimal)SampleQuantityCell.TextBox.getTag();
            if (sampleQuantity.compareTo(lotQuantity) > 0) {
                App.Current.showInfo(getContext(), "输入的样品数" + sampleQuantity.toString() + "大于该批次的待检数" + lotQuantity.toString() + "，不能提交。");
                return;
            }
        }

        App.Current.question(getContext(), "提交后不能修改，确定要提交吗？", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String checkdate = App.formatCalendar(Calendar.getInstance(), "yyyy-MM-dd hh:mm");
                
                String sql = "update obtn set MnfSerial=?,lotNumber=? where ItemCode=? and DistNumber=?";
                Parameters p = new Parameters().add(1, checkdate).add(2, checker).add(3, itemcode).add(4, lot);
                Result<Integer> rr = App.Current.DbPortal.ExecuteNonQuery(oiqc_editor.this.Connector, sql, p);
                if (rr.Value != null && rr.Value > 0) {
                    
                    if (sp_whscode != null && sp_whscode.length() > 0) {
                        BigDecimal qty = App.parseDecimal(sp_quantity, BigDecimal.ZERO);
                        if (qty.compareTo(BigDecimal.ZERO)>0) {
                            
                            //如果指定了留样库位，而且样品数大于0，则自动生成库存转移，将样品转移到样品仓
                            
                            Document doc = new Document(DocType.OWTR);
                            String docDate = App.formatCalendar(Calendar.getInstance(), "yyyy-MM-dd");
                            String taxDate = App.formatCalendar(Calendar.getInstance(), "yyyy-MM-dd");
                            
                            Map<String, String> body = new HashMap<String, String>();
                            //body.put("DocDate", docDate);
                            //body.put("TaxDate", taxDate);
                            body.put("FromWarehouse", whscode);
                            body.put("U_UserID",App.Current.UserID);
                            doc.setBody(body);
                            
                            Map<String, String> line = new HashMap<String, String>();
                            line.put("LineNum", "0");
                            line.put("ItemCode", itemcode);
                            line.put("Quantity", sampleQuantity.toString());
                            line.put("WarehouseCode", sp_whscode);
                            doc.addLine(line);
                            
                            Map<String, String> batch = new HashMap<String, String>();
                            batch.put("BaseLineNumber", "0");
                            batch.put("BatchNumber", lot);
                            batch.put("Quantity", sampleQuantity.toString());
                            doc.addBatch(batch);

                            Result<String> r = doc.save();
                            if (r.HasError) {
                                App.Current.showError(oiqc_editor.this.getContext(), "转移该批次物料到样品仓时出错：" + r.Error);
                                return;
                            }
                        }
                    }
                    
                    BatchNumCell.setContentText("");
                    ItemCodeCell.setContentText("");
                    ItemNameCell.setContentText("");
                    SampleQuantityCell.setContentText("");
                    CheckerCell.setContentText("");
                    
                } else if (rr.HasError) {
                    App.Current.showError(oiqc_editor.this.getContext(), rr.Error);
                    return;
                }                
            }
        });
    }
}
