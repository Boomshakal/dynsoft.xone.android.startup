package dynsoft.xone.android.qad;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.base.BasePane;
import dynsoft.xone.android.control.NestedListView;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.sbo.DocType;
import dynsoft.xone.android.sbo.Document;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

public class pign_editor extends BasePane {

    public pign_editor(Context context) {
		super(context);
	}

	public ScrollView Scroll;
    public LinearLayout Body;
    public TextCell WhsCodeCell;
    public TextCell WorkOrderCell;
    public TextCell ItemCodeCell;
    public TextCell ItemNameCell;
    public NestedListView LinesListView;
    public TableAdapter LinesAdapter;
    
    @Override
    public void onPrepared() {
        super.onPrepared();
        
        Scroll = (ScrollView)this.Elements.get("scroll").Object;
        Body = (LinearLayout)this.Elements.get("body").Object;
        this.WhsCodeCell = (TextCell)this.Elements.get("warehouse").Object;
        this.WorkOrderCell = (TextCell)this.Elements.get("workorder").Object;
        this.ItemCodeCell = (TextCell)this.Elements.get("itemcode").Object;
        this.ItemNameCell = (TextCell)this.Elements.get("itemname").Object;
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
            Parameters p = new Parameters().add(1, "erp_csimc_pign_dflt_warehouse").add(2, "whscode");
            Result<String> r = App.Current.DbPortal.ExecuteScalar(App.Current.BookConnector, sql, p, String.class);
            if (r.Value != null) {
                this.WhsCodeCell.setContentText(r.Value);
            } else if (r.HasError) {
                App.Current.showError(getContext(), r.Error);
            }
        }
        
        if (this.WorkOrderCell != null) {
            this.WorkOrderCell.setLabelText("加工单ID");
        }
        
        if (this.ItemCodeCell != null) {
            this.ItemCodeCell.setLabelText("产品编号");
        }
        
        if (this.ItemNameCell != null) {
            this.ItemNameCell.setLabelText("产品名称");
            this.ItemNameCell.TextBox.setSingleLine();
            this.ItemNameCell.TextBox.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        }

        if (this.LinesListView != null) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
            lp.height = App.dpToPx(300);
            this.LinesListView.setLayoutParams(lp);
            this.LinesListView.ScrollView = this.Scroll;
            this.LinesAdapter = new TableAdapter(getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.pign_line, null);
                    }
                    
                    DataRow row = (DataRow)pign_editor.this.LinesAdapter.getItem(position);
                    TextView num = (TextView)convertView.findViewById(R.id.num);
                    TextView batchnum = (TextView)convertView.findViewById(R.id.batchnum);
                    
                    num.setText(String.valueOf(position + 1));
                    batchnum.setText(row.getValue("Lot", "") + "    " + row.getValue("CLot", ""));
                    
                    return convertView;
                }
            };
            
            this.LinesListView.setAdapter(this.LinesAdapter);
            
            String sql = "select '' Lot,'' CLot where 1=0";
            Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql);
            if (r.Value != null) {
                this.LinesAdapter.DataTable = r.Value;
                this.LinesAdapter.notifyDataSetChanged();
            } else if (r.HasError) {
                App.Current.showDebug(getContext(), r.Error);
            }
        }
    }
    
//    private void refreshReceivedQuantity()
//    {
//        String workorder = this.WorkOrderCell.getContentText().trim();
//        if (workorder != null && workorder.length() > 0) {
//            return;
//        }
//        
//        String sql = "select sum(Quantity) Quantity from IGN1 inner join OIGN on OIGN.DocEntry=IGN1.DocEntry where OIGN.U_BaseDocType='WO' and OIGN.U_BaseDocNum=?";
//        Parameters p = new Parameters().add(1, workorder);
//        Result<BigDecimal> r = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, BigDecimal.class);
//        if (r.HasError) {
//            App.Current.showError(getContext(), r.Error);
//            return;
//        }
//        
//        if (r.Value != null) {
//            this.ReceivedQtyCell.setContentText(App.formatNumber(r.Value, "0.##"));
//        }
//    }
//   
    
    @Override
    public void onScan(String barcode)
    {
        if (barcode == null) return;
        
        DataTable curr_lines = this.LinesAdapter.DataTable;
        if (curr_lines == null) {
            App.Current.showError(getContext(), "明细行数据异常，请重新打开。");
            return;
        }

        barcode = barcode.trim();
        if (barcode.startsWith("P")==false && barcode.startsWith("02") && barcode.startsWith("04") == false) {
            App.Current.showError(getContext(), barcode+"是无效的产品条码。");
            return;
        }
        
        //检查该条码是否已进行过终检
        String sql = "select top 1 lot,clot,w.workorder,wo.itemcode,wo.itemname,w.result from work w inner join workorder wo on w.workorder=wo.code where (w.lot=? or w.clot=?) and w.[proc]='GX-FQC' order by w.id desc";
        Parameters p = new Parameters().add(1, barcode).add(2, barcode);
        Result<DataRow> rr = App.Current.DbPortal.ExecuteRecord("mes_and", sql, p);
        if (rr.HasError) {
            App.Current.showError(getContext(), "查询该条码信息时出错：" + rr.Error);
            return;
        }
        
        if (rr.Value == null) {
            App.Current.showError(getContext(), "条码"+barcode+"未进行终检，不能收货。");
            return;
        }
        
        String lot = rr.Value.getValue("lot", String.class);
        String clot = rr.Value.getValue("clot", String.class);
        String workorder = rr.Value.getValue("workorder", String.class);
        String itemcode = rr.Value.getValue("itemcode", String.class);
        String itemname = rr.Value.getValue("itemname", String.class);
        String result = rr.Value.getValue("result", String.class);
        
        if (result.equals("PASS") == false) {
            App.Current.showError(getContext(), "条码"+barcode+"终检结果不良，不能收货。");
            return;
        }
        
        String curr_workorder = this.WorkOrderCell.getContentText().trim();
        if (curr_workorder.length() > 0 && curr_workorder.equals(workorder) == false) {
            App.Current.showError(getContext(), "条码"+barcode+"属于另外一个加工单，不能一起收货。");
            return;
        }
        
        sql = "select top 1 U_Lot from IGN1 inner join OIGN on OIGN.DocEntry=IGN1.DocEntry where (U_Lot=? or U_CLot=?) and U_BaseDocType='WO'";
        p.clearAll().add(1, clot).add(2, clot);
        Result<String> rs = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, String.class);
        if (rs.HasError) {
            App.Current.showError(getContext(), "查询该条码是否已收货时出错：" + rs.Error);
            return;
        }
        
        if (rs.Value != null && rs.Value.length() > 0) {
            App.Current.showError(getContext(), "该条码已入库，不能再收货。");
            return;
        }
        

        for (DataRow dr : this.LinesAdapter.DataTable.Rows) {
            String bnum = dr.getValue("CLot", String.class);
            if (bnum != null && clot.equals(bnum) == true) {
                App.Current.showInfo(getContext(), "该条码已经扫描过。");
                return;
            }
        }
        
        if (curr_workorder == null || curr_workorder.length() == 0) {
            WorkOrderCell.setContentText(workorder);
            ItemCodeCell.setContentText(itemcode);
            ItemNameCell.setContentText(itemname);
        }
        
        DataRow row = this.LinesAdapter.DataTable.NewRow();
        row.setValue("Lot", lot);
        row.setValue("CLot", clot);
        this.LinesAdapter.DataTable.Rows.add(row);
        this.LinesAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void save()
    {
        final String whscode = this.WhsCodeCell.getContentText().trim();
        if (whscode == null || whscode.length() == 0) {
            App.Current.showInfo(getContext(), "缺少仓库编号，不能提交。");
            return;
        }
        
        final String workorder = this.WorkOrderCell.getContentText().trim();
        if (workorder == null || workorder.length() == 0) {
            App.Current.showInfo(getContext(), "缺少加工单ID，不能提交。");
            return;
        }
        
        final String itemcode = this.ItemCodeCell.getContentText().trim();
        if (itemcode == null || itemcode.length() == 0) {
            App.Current.showInfo(getContext(), "缺少物料编号，不能提交。");
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
                
                Map<String, String> body = new HashMap<String, String>();
                //body.put("DocDate", docDate);
                //body.put("TaxDate", taxDate);
                body.put("U_BaseDocType", "WO");
                body.put("U_BaseDocNum", workorder);
                body.put("U_UserID", App.Current.UserID);
                body.put("U_UserCode", App.Current.UserCode);
                body.put("U_UserName", App.Current.UserName);
                
                doc.setBody(body);
                
                if (pign_editor.this.LinesAdapter.DataTable != null) {
                    int lineNum = 0;
                    for (DataRow row : pign_editor.this.LinesAdapter.DataTable.Rows) {
                        String lot = row.getValue("Lot", String.class);
                        String clot = row.getValue("CLot", String.class);
                        Map<String, String> line = new HashMap<String, String>();
                        line.put("LineNum", String.valueOf(lineNum));
                        line.put("ItemCode", itemcode);
                        line.put("Quantity", "1");
                        line.put("WarehouseCode", whscode);
                        line.put("U_Lot", lot);
                        line.put("U_CLot", clot);
                        
                        doc.addLine(line);
                        doc.addSerial(String.valueOf(lineNum), clot, false);
                        
                        lineNum++;
                    }
                }
                
                Result<String> result = doc.save();
                if (result.Value != null) {
                    
                    App.Current.showInfo(pign_editor.this.getContext(), "提交成功！单据号："+result.Value);
                    
                    WorkOrderCell.setContentText("");
                    ItemCodeCell.setContentText("");
                    ItemNameCell.setContentText("");
                    pign_editor.this.LinesAdapter.DataTable.Rows.clear();
                    pign_editor.this.LinesAdapter.notifyDataSetChanged();
                    
                } else if (result.HasError) {
                    App.Current.showError(pign_editor.this.getContext(), "提交失败！" + result.Error);
                }
            }
        });
    }
}
