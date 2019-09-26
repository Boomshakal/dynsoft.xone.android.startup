package dynsoft.xone.android.sbo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import dynsoft.xone.android.adapter.PageTableAdapter;
import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.base.BasePane;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.LabelCell;
import dynsoft.xone.android.control.NestedListView;
import dynsoft.xone.android.control.PaneCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataSet;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

public class oitm_editor extends BasePane {

    public oitm_editor(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ScrollView Scroll;
    public LinearLayout Body;
    public TextCell ItemCodeCell;
    public TextCell ItemNameCell;
    public TextCell FNameCell;
    public TextCell TypeCell;
    public TextCell GroupCell;
    public TextCell VendorCell;
    public LabelCell PriceCell;
    public LabelCell StockCell;
    public LabelCell BatchCell;
    public NestedListView PriceListView;
    public NestedListView StockListView;
    public NestedListView BatchListView;
    public TableAdapter PriceAdapter;
    public TableAdapter StockAdapter;
    public PageTableAdapter BatchAdapter;
    
    @Override
    public void onPrepared() {
        super.onPrepared();
        
        Scroll = (ScrollView)this.Elements.get("scroll").Object;
        Body = (LinearLayout)this.Elements.get("body").Object;
        ItemCodeCell = (TextCell)this.Elements.get("code").Object;
        ItemNameCell = (TextCell)this.Elements.get("name").Object;
        FNameCell = (TextCell)this.Elements.get("fname").Object;
        TypeCell = (TextCell)this.Elements.get("type").Object;
        GroupCell = (TextCell)this.Elements.get("group").Object;
        VendorCell = (TextCell)this.Elements.get("vendor").Object;
        PriceCell = (LabelCell)this.Elements.get("price").Object;
        StockCell = (LabelCell)this.Elements.get("stock").Object;
        BatchCell = (LabelCell)this.Elements.get("batch").Object;
        PriceListView = (NestedListView)this.Elements.get("pricelist").Object;
        StockListView = (NestedListView)this.Elements.get("stocklist").Object;
        BatchListView = (NestedListView)this.Elements.get("batchlist").Object;
        
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
        
        if (ItemCodeCell != null) {
            ItemCodeCell.setLabelText("编号");
        }
        
        if (ItemNameCell != null) {
            ItemNameCell.setLabelText("名称");
        }
        
        if (FNameCell != null) {
            FNameCell.setLabelText("外文名称");
        }
        
        if (TypeCell != null) {
            TypeCell.setLabelText("类型");
        }
        
        if (GroupCell != null) {
            GroupCell.setLabelText("物料组");
        }

        if (VendorCell != null) {
            VendorCell.setLabelText("供应商");
        }
        
        if (PriceCell != null) {
            PriceCell.setLabelText("价格");
        }
        
        if (StockCell != null) {
            StockCell.setLabelText("库存");
        }
        
        if (BatchCell != null) {
            BatchCell.setLabelText("批次");
        }
        
        if (this.PriceListView != null) {
            this.PriceAdapter = new TableAdapter(getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.oitm_price, null);
                        ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                        icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_opln_gray"));
                    }
                    
                    DataRow row = (DataRow)oitm_editor.this.PriceAdapter.getItem(position);
                    TextView num = (TextView)convertView.findViewById(R.id.num);
                    TextView name = (TextView)convertView.findViewById(R.id.name);
                    TextView price = (TextView)convertView.findViewById(R.id.price);
                    
                    num.setText(String.valueOf(position + 1));
                    name.setText(row.getValue("ListName", ""));
                    price.setText(App.formatNumber(row.getValue("Price"), "0.##"));
                    
                    return convertView;
                }
            };
            
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
            lp.height = App.dpToPx(180);
            this.PriceListView.ScrollView = this.Scroll;
            this.PriceListView.setLayoutParams(lp);
            this.PriceListView.setAdapter(this.PriceAdapter);
        }
        
        if (this.StockListView != null) {
            this.StockAdapter = new TableAdapter(getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.oitm_stock, null);
                        ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                        icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_owhs_gray"));
                    }
                    
                    DataRow row = (DataRow)oitm_editor.this.StockAdapter.getItem(position);
                    TextView num = (TextView)convertView.findViewById(R.id.num);
                    TextView name = (TextView)convertView.findViewById(R.id.name);
                    TextView price = (TextView)convertView.findViewById(R.id.qty);
                    
                    num.setText(String.valueOf(position + 1));
                    name.setText(row.getValue("WhsName", ""));
                    price.setText(App.formatNumber(row.getValue("Quantity"), "0.##"));
                    
                    return convertView;
                }
            };
            
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
            lp.height = App.dpToPx(180);
            this.StockListView.ScrollView = this.Scroll;
            this.StockListView.setLayoutParams(lp);
            this.StockListView.setAdapter(this.StockAdapter);
        }
        
        if (this.BatchListView != null) {
            this.BatchAdapter = new PageTableAdapter(getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (position < oitm_editor.this.BatchAdapter.DataTable.Rows.size()) {
                        if (convertView == this.Footer) {
                            convertView = null;
                        }
                        
                        if (convertView == null) {
                            convertView = LayoutInflater.from(getContext()).inflate(R.layout.oitm_batch, null);
                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_obtn_gray"));
                        }
                        
                        DataRow row = (DataRow)oitm_editor.this.BatchAdapter.getItem(position);
                        TextView num = (TextView)convertView.findViewById(R.id.num);
                        TextView name = (TextView)convertView.findViewById(R.id.name);
                        TextView price = (TextView)convertView.findViewById(R.id.qty);
                        
                        num.setText(String.valueOf(position + 1));
                        name.setText(row.getValue("BatchNum", ""));
                        price.setText(App.formatNumber(row.getValue("Quantity"), "0.##"));
                        
                        return convertView;
                    } else {
                        return this.Footer;
                    }
                }
            };
            
            this.BatchAdapter.PageSize = 10;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
            lp.height = App.dpToPx(250);
            this.BatchListView.setLayoutParams(lp);
            this.BatchListView.ScrollView = this.Scroll;
            this.BatchListView.setAdapter(this.BatchAdapter);
            this.BatchListView.setOnItemClickListener(new OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (id == oitm_editor.this.BatchAdapter.DataTable.Rows.size()) {
                        oitm_editor.this.refreshBatchList();
                    }
                }
            });
        }
        
        this.refreshData();
    }
    
    public void refreshData()
    {
        String itemCode = (String)this.Parameters.get("ItemCode");
        String sql = "select ItemCode,ItemName,FrgnName,(CASE ItemType "
                   + "when 'I' then '@/sbo_oitm_type_item' "
                   + "when 'L' then '@/sbo_oitm_type_labor' "
                   + "when 'T' then '@/sbo_oitm_type_travel' end) TypeName,OITB.ItmsGrpNam,OITM.CardCode,OCRD.CardName from OITM "
                   + "left join OITB on OITB.ItmsGrpCod=OITM.ItmsGrpCod left join OCRD on OCRD.CardCode=OITM.CardCode where ItemCode=?;"
                   + "select ListName,Price from ITM1 left join OPLN on OPLN.ListNum=ITM1.PriceList where ItemCode=?;"
                   + "select WhsName,OnHand Quantity from OITW left join OWHS on OWHS.WhsCode=OITW.WhsCode where ItemCode=?;";
        Parameters p = new Parameters().add(1, itemCode).add(2, itemCode).add(3, itemCode);
        Result<DataSet> r = App.Current.DbPortal.ExecuteDataSet(this.Connector, sql, p);
        if (r.Value != null) {
            DataRow row = null;
            if (r.Value.Tables.size() > 0 && r.Value.Tables.get(0).Rows.size() > 0) {
                row = r.Value.Tables.get(0).Rows.get(0);
                
                String title = this.Title + " - " + row.getValue("ItemCode").toString();
                this.Header.setTitleText(title);
                
                if (ItemCodeCell != null) {
                    ItemCodeCell.setContentText(row.getValue("ItemCode",""));
                }
                
                if (ItemNameCell != null) {
                    ItemNameCell.setContentText(row.getValue("ItemName",""));
                }
                
                if (FNameCell != null) {
                    FNameCell.setContentText(row.getValue("FrgnName",""));
                }
                
                if (TypeCell != null) {
                    String typeName = row.getValue("TypeName","");
                    typeName = App.Current.ResourceManager.getString(typeName);
                    TypeCell.setContentText(typeName);
                    TypeCell.setTag(row.getValue("ItemType"));
                }
                
                if (GroupCell != null) {
                    GroupCell.setContentText(row.getValue("ItmsGrpNam", ""));
                    GroupCell.setTag(row.getValue("ItmsGrpCod"));
                }
                
                if (VendorCell != null) {
                    VendorCell.setContentText(row.getValue("CardName", ""));
                    VendorCell.setTag(row.getValue("CardCode"));
                }
            }
            
            if (this.PriceAdapter != null) {
                if (r.Value.Tables.size() > 1) {
                    this.PriceAdapter.DataTable = r.Value.Tables.get(1);
                    this.PriceAdapter.notifyDataSetChanged();
                }
            }
            
            if (this.StockAdapter != null) {
                if (r.Value.Tables.size() > 2) {
                    this.StockAdapter.DataTable = r.Value.Tables.get(2);
                    this.StockAdapter.notifyDataSetChanged();
                }
            }
            
            if (this.BatchAdapter != null) {
                this.BatchAdapter.PageIndex = 0;
                this.BatchAdapter.DataTable = null;
                this.refreshBatchList();
            }
            
        } else {
            App.Current.showError(getContext(), r.Error);
        }
    }

    public void refreshBatchList()
    {
        String itemCode = (String)this.Parameters.get("ItemCode");
        int top = this.BatchAdapter.PageSize*(this.BatchAdapter.PageIndex+1);
        int start = (this.BatchAdapter.PageSize*this.BatchAdapter.PageIndex)+1;
        int end = this.BatchAdapter.PageSize*(this.BatchAdapter.PageIndex+1);
        
        String sql = "with temp as(select top (?) ROW_NUMBER() over(order by BatchNum) Number,* from ("
        + "select IBT1.BatchNum,IBT1.Quantity from IBT1 where ItemCode=? union "
        + "select OSRN.DistNumber BatchNum,1.0 Quantity from SRI1 left join OSRN on SRI1.ItemCode=OSRN.ItemCode "
        + "and SRI1.SysSerial=OSRN.SysNumber where OSRN.ItemCode=?) T) select * from temp where Number>=? and Number<=?";
        
        Parameters p = new Parameters().add(1,top).add(2, itemCode).add(3, itemCode).add(4,start).add(5,end);
        Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (r.Value != null && r.Value.Rows.size() > 0) {
            if (this.BatchAdapter.DataTable != null) {
                for (DataRow row : r.Value.Rows) {
                    this.BatchAdapter.DataTable.Rows.add(row);
                }
            } else {
                this.BatchAdapter.DataTable = r.Value;
            }
            
            this.BatchAdapter.PageIndex += 1;
        } else if (r.HasError) {
            App.Current.showError(getContext(), r.Error);
        }
        this.BatchAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void prev()
    {
        String itemCode = (String)this.Parameters.get("ItemCode");
        String sql = "select top 1 ItemCode from OITM where ItemCode<? order by ItemCode desc";
        Parameters p = new Parameters().add(1, itemCode);
        Result<String> r = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, String.class);
        if (r.Value != null) {
            this.Parameters.remove("ItemCode");
            this.Parameters.put("ItemCode", r.Value);
            this.refreshData();
        } else {
            App.Current.showError(getContext(), r.Error);
        }
    }
    
    @Override
    public void next()
    {
        String itemCode = (String)this.Parameters.get("ItemCode");
        String sql = "select top 1 ItemCode from OITM where ItemCode>?";
        Parameters p = new Parameters().add(1, itemCode);
        Result<String> r = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, String.class);
        if (r.Value != null) {
            this.Parameters.remove("ItemCode");
            this.Parameters.put("ItemCode", r.Value);
            this.refreshData();
        } else {
            App.Current.showError(getContext(), r.Error);
        }
    }
}
