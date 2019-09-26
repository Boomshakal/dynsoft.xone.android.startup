package dynsoft.xone.android.mes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import dynsoft.xone.android.base.BasePane;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

public class PnFeedEditor extends BasePane {

    public PnFeedEditor(Context context) {
		super(context);
	}

	public ScrollView Scroll;
    public LinearLayout Body;
    
    public TextCell WorkOrderCell;
    public TextCell WorkPlaceCell;
    public TextCell DeviceCell;
    public TextCell FeederCell;
    public TextCell LotCell;
    public TextCell ItemCodeCell;
    public TextCell ItemNameCell;
    public TextCell QuantityCell;
    
    @Override
    public void onPrepared() {
        super.onPrepared();
        
        Scroll = (ScrollView)this.Elements.get("scroll").Object;
        Body = (LinearLayout)this.Elements.get("body").Object;
        WorkOrderCell = (TextCell)this.Elements.get("workorder").Object;
        WorkPlaceCell = (TextCell)this.Elements.get("workplace").Object;
        DeviceCell = (TextCell)this.Elements.get("device").Object;
        FeederCell = (TextCell)this.Elements.get("feeder").Object;
        LotCell = (TextCell)this.Elements.get("lot").Object;
        ItemCodeCell = (TextCell)this.Elements.get("itemcode").Object;
        ItemNameCell = (TextCell)this.Elements.get("itemname").Object;
        QuantityCell = (TextCell)this.Elements.get("quantity").Object;
        
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
    }
    
    @Override
    public void onScan(String barcode)
    {
        if (barcode == null) return;
        
        barcode = barcode.trim();
        if (barcode.length() == 0) return;
        
        if (barcode.startsWith("GW-")) {
            String sql = "select top 1 devicecode from workplacedevice where workplace=?";
            Parameters p =new Parameters().add(1, barcode);
            Result<String> rs = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, String.class);
            if (rs.HasError) {
                App.Current.showError(getContext(), "��ѯ��λ��Ϣ����" + rs.Error);
                return;
            }
            
            if (rs.Value != null && rs.Value.length() > 0) {
                WorkPlaceCell.setContentText(barcode);
                DeviceCell.setContentText(rs.Value);
            } else {
                App.Current.showError(getContext(), "�ù�λû��ָ����������Ƭ����");
                WorkPlaceCell.setContentText("");
                DeviceCell.setContentText("");
                return;
            }

            return;
        }
        
        if (barcode.startsWith("WO-")) {
            String workorder = barcode.substring(3, barcode.length());
            String sql = "select code from workorder where code=?";
            Parameters p =new Parameters().add(1, workorder);
            Result<String> rs = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, String.class);
            if (rs.HasError) {
                App.Current.showError(getContext(), "��ѯ�ӹ�����Ϣ����" + rs.Error);
                return;
            }
            
            if (rs.Value != null && rs.Value.length() > 0) {
                WorkOrderCell.setContentText(workorder);
            } else {
                App.Current.showError(getContext(), "�ӹ���"+workorder+"��û���´�ӹ�����");
                return;
            }
            
            return;
        }
        
        String workplace = this.WorkPlaceCell.getContentText().trim();
        String workorder = this.WorkOrderCell.getContentText().trim();
        
        if (workplace == null || workplace.length() == 0) {
            App.Current.showError(getContext(), "���������ɨ�蹤λ��š�");
            return;
        }
        
        if (workorder == null || workorder.length() == 0) {
            App.Current.showError(getContext(), "���������ɨ��ӹ�����š�");
            return;
        }
        
        if (barcode.length() > 20) {
            String icode = barcode.substring(0, barcode.length()-16);
            
            String sql = "select partcode from feeder where device=(select top 1 devicecode from workplacedevice where workplace=?) and itemcode=(select itemcode from workorder where code=?) and partcode=?";
            Parameters p =new Parameters().add(1, workplace).add(2, workorder).add(3, icode);
            Result<String> rs = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, String.class);
            if (rs.HasError) {
                App.Current.showError(getContext(), "������ϵͳ��ѯ������Ϣ����" + rs.Error);
                return;
            }
            
            if (rs.Value != null && rs.Value.length() > 0) {

                sql = "select pt_part,(pt_desc1+pt_desc2) 'pt_desc' from pub.pt_mstr where pt_part=?";
                p = new Parameters().add(1, icode);
                Result<DataRow> rr = App.Current.DbPortal.ExecuteRecord("erp_csimc_and", sql, p);
                if (rr.HasError) {
                    App.Current.showError(getContext(), "��QADϵͳ��ѯ������Ϣ����" + rr.Error);
                }
                
                if (rr.Value != null) {
                    LotCell.setContentText(barcode);
                    ItemCodeCell.setContentText(rr.Value.getValue("pt_part", String.class));
                    ItemNameCell.setContentText(rr.Value.getValue("pt_desc", String.class));
                    QuantityCell.TextBox.requestFocus();
                    
                }
                
                return;
            } else {
                App.Current.showError(getContext(), "��Ʒ�����������嵥û������" + icode);
                LotCell.setContentText("");
                ItemCodeCell.setContentText("");
                ItemNameCell.setContentText("");
                return;
            }
        }
        
        String itemcode =  this.ItemCodeCell.getContentText().trim();
        if (itemcode == null || itemcode.length() == 0) {
            App.Current.showError(getContext(), "���������ɨ�����ϱ�š�");
            return;
        }
        
        if (barcode.length() > 10 && barcode.length() <20) {

            String sql = "select count(*) from feeder where device=(select top 1 devicecode from workplacedevice where workplace=?) and itemcode=(select itemcode from workorder where code=?) and partcode=? and feedercode=?";
            Parameters p = new Parameters().add(1, workplace).add(2, workorder).add(3, itemcode).add(4, barcode);
            Result<Integer> r = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, Integer.class);
            if (r.HasError) {
                App.Current.showError(getContext(), r.Error);
                return;
            } 
            
            if (r.Value == null || r.Value == 0) {
                App.Current.showError(getContext(), "���Ϻ�λ��û�ж�Ӧ��ϵ��");
                FeederCell.setContentText("");
                return;
            } else {
                FeederCell.setContentText(barcode);
            }
        }
    }
    
    @Override
    public void save()
    {
        final String workorder = WorkOrderCell.getContentText();
        if (workorder== null || workorder.length() == 0) {
            App.Current.showError(getContext(), "��ɨ��ӹ�����š�");
            return;
        }
        
        final String workplace = WorkPlaceCell.getContentText();
        if (workplace== null || workplace.length() == 0) {
            App.Current.showError(getContext(), "��ɨ�蹤λ��š�");
            return;
        }
        
        final String feeder = FeederCell.getContentText();
        if (feeder== null || feeder.length() == 0) {
            App.Current.showError(getContext(), "����ɨ����������š�");
            return;
        }
        
        final String lot = LotCell.getContentText();
        if (lot== null || lot.length() == 0) {
            App.Current.showError(getContext(), "����ɨ�������������롣");
            return;
        }
        
        final String itemcode = ItemCodeCell.getContentText();
        if (itemcode== null || itemcode.length() == 0) {
            App.Current.showError(getContext(), "ȱ�����ϱ�š�");
            return;
        }
        
        String sql = "select count(*) from feeder where device=(select top 1 devicecode from workplacedevice where workplace=?) and itemcode=(select itemcode from workorder where code=?) and partcode=? and feedercode=?";
        Parameters p = new Parameters().add(1, workplace).add(2, workorder).add(3, itemcode).add(4, feeder);
        Result<Integer> r = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, Integer.class);
        if (r.HasError) {
            App.Current.showError(getContext(), r.Error);
            return;
        } 
        
        if (r.Value == null || r.Value == 0) {
            App.Current.showError(getContext(), "���Ϻ�λ��û�ж�Ӧ��ϵ���޷����档");
            FeederCell.setContentText("");
            return;
        }
        
        final String quantity = QuantityCell.getContentText();
        if (quantity== null || quantity.length() == 0) {
            App.Current.showError(getContext(), "������������");
            return;
        }
        
        final String itemname = ItemNameCell.getContentText();
        
        sql = "select count(*) from feed where workorder=? and feeder=? and lot=?";
        p = new Parameters().add(1, workorder).add(2, feeder).add(3, lot);
        Result<Integer> rs = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, Integer.class);
        if (rs.Value > 0) {
            App.Current.question(getContext(), "�����������Ѿ��Ǽǹ��������ύ��", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    PnFeedEditor.this.insert(lot, feeder, workorder, workplace, itemcode, itemname, quantity);
                }
            });
        } else {
            this.insert(lot, feeder, workorder, workplace, itemcode, itemname, quantity);
        }
    }
    
    private void insert(String lot, String feeder, String workorder, String workplace, String itemcode, String itemname, String quantity)
    {
        String sql = "insert into feed(lot,feeder,workorder,workplace,itemcode,itemname,quantity,crttime,crtuserid,crtusername)values(?,?,?,?,?,?,?,getdate(),?,?)";
        Parameters p = new Parameters().add(1, lot).add(2, feeder).add(3, workorder).add(4, workplace).add(5, itemcode).add(6,itemname).add(7, quantity).add(8,App.Current.UserID).add(9,App.Current.UserName);
        Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(this.Connector, sql, p);
        if (r.Value != null && r.Value > 0) {
            FeederCell.setContentText("");
            LotCell.setContentText("");
            ItemCodeCell.setContentText("");
            ItemNameCell.setContentText("");
            QuantityCell.setContentText("");
            
        } else if (r.HasError) {
            App.Current.showError(getContext(), r.Error);
        }
    }
}
