package dynsoft.xone.android.sbo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
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

public class ocrd_editor extends BasePane {

    public ocrd_editor(Context context) {
		super(context);
	}

	public ScrollView Scroll;
    public LinearLayout Body;
    public TextCell CardCodeCell;
    public TextCell CardNameCell;
    public TextCell FNameCell;
    public TextCell TypeCell;
    public TextCell GroupCell;
    public TextCell CurrencyCell;
    public TextCell AddressCell;
    public LabelCell ContactListCell;
    public LabelCell AddressListCell;
    public NestedListView ContactListView;
    public NestedListView AddressListView;
    public TableAdapter ContactAdapter;
    public TableAdapter AddressAdapter;
    
    @Override
    public void onPrepared() {
        super.onPrepared();
        
        Scroll = (ScrollView)this.Elements.get("scroll").Object;
        Body = (LinearLayout)this.Elements.get("body").Object;
        CardCodeCell = (TextCell)this.Elements.get("code").Object;
        CardNameCell = (TextCell)this.Elements.get("name").Object;
        FNameCell = (TextCell)this.Elements.get("fname").Object;
        TypeCell = (TextCell)this.Elements.get("type").Object;
        GroupCell = (TextCell)this.Elements.get("group").Object;
        CurrencyCell = (TextCell)this.Elements.get("currency").Object;
        AddressCell = (TextCell)this.Elements.get("address").Object;
        ContactListCell = (LabelCell)this.Elements.get("contact").Object;
        AddressListCell = (LabelCell)this.Elements.get("addr").Object;
        ContactListView = (NestedListView)this.Elements.get("contactlist").Object;
        AddressListView = (NestedListView)this.Elements.get("addrlist").Object;
        
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
        
        if (CardCodeCell != null) {
            CardCodeCell.setLabelText("编号");
        }
        
        if (CardNameCell != null) {
            CardNameCell.setLabelText("名称");
        }
        
        if (FNameCell != null) {
            FNameCell.setLabelText("外文名称");
        }
        
        if (TypeCell != null) {
            TypeCell.setLabelText("类型");
        }
        
        if (GroupCell != null) {
            GroupCell.setLabelText("分组");
        }

        if (CurrencyCell != null) {
            CurrencyCell.setLabelText("货币");
        }
        
        if (AddressCell != null) {
            AddressCell.setLabelText("单位地址");
        }
        
        if (ContactListCell != null) {
            ContactListCell.setLabelText("联系人");
        }
        
        if (AddressListCell != null) {
            AddressListCell.setLabelText("地址列表");
        }
        
        if (this.ContactListCell != null) {
            this.ContactAdapter = new TableAdapter(getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    final DataRow row = (DataRow)ocrd_editor.this.ContactAdapter.getItem(position);
                    
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.ocrd_ocpr, null);
                        ImageView imgIcon = (ImageView)convertView.findViewById(R.id.icon);
                        imgIcon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_ocpr_gray"));
                    }

                    ImageButton imgPhone = (ImageButton)convertView.findViewById(R.id.phone);
                    ImageButton imgMessage = (ImageButton)convertView.findViewById(R.id.message);
                    
                    imgPhone.setImageBitmap(App.Current.ResourceManager.getImage("@/core_phone_gray"));
                    imgMessage.setImageBitmap(App.Current.ResourceManager.getImage("@/core_message_gray"));
                    
                    imgPhone.setOnClickListener(new OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            String mobile = row.getValue("Cellolar", "");
                            if (mobile != null && mobile.length() > 0) {
                                Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + mobile));
                                ocrd_editor.this.getContext().startActivity(intent);
                            }
                        }
                    });
                    imgMessage.setOnClickListener(new OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            String mobile = row.getValue("Cellolar", "");
                            if (mobile != null && mobile.length() > 0) {
                                Uri uri=Uri.parse("smsto:"+mobile);  
                                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                it.setType("vnd.android-dir/mms-sms");
                                it.putExtra("address", mobile);
                                ocrd_editor.this.getContext().startActivity(it);
                            }
                        }
                    });
                    
                    TextView num = (TextView)convertView.findViewById(R.id.num);
                    TextView name = (TextView)convertView.findViewById(R.id.name);
                    TextView pos = (TextView)convertView.findViewById(R.id.position);
                    TextView mobile = (TextView)convertView.findViewById(R.id.mobile);
                    
                    num.setText(String.valueOf(position + 1));
                    name.setText(row.getValue("Name", ""));
                    pos.setText(row.getValue("Position", ""));
                    mobile.setText(row.getValue("Cellolar", ""));
                    
                    return convertView;
                }
            };
            
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
            lp.height = App.dpToPx(150);
            this.ContactListView.ScrollView = this.Scroll;
            this.ContactListView.setLayoutParams(lp);
            this.ContactListView.setAdapter(this.ContactAdapter);
        }
        
        if (this.AddressListCell != null) {
            this.AddressAdapter = new TableAdapter(getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.ocrd_addr, null);
                        ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                        icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_addr_gray"));
                    }
                    
                    DataRow row = (DataRow)ocrd_editor.this.AddressAdapter.getItem(position);
                    TextView num = (TextView)convertView.findViewById(R.id.num);
                    TextView addr = (TextView)convertView.findViewById(R.id.addr);
                    
                    num.setText(String.valueOf(position + 1));
                    addr.setText(row.getValue("Address", ""));
                    
                    return convertView;
                }
            };
            
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
            lp.height = App.dpToPx(150);
            this.AddressListView.ScrollView = this.Scroll;
            this.AddressListView.setLayoutParams(lp);
            this.AddressListView.setAdapter(this.AddressAdapter);
        }
        
        this.refreshData();
    }
    
    public void refreshData()
    {
        String cardCode = (String)this.Parameters.get("CardCode");
        String sql = "select CardCode,CardName,CardFName,CardType,(case CardType "
                    + "when 'C' then '@/sbo_ocrd_type_customer' "
                    + "when 'S' then '@/sbo_ocrd_type_vendor' "
                    + "when 'L' then '@/sbo_ocrd_type_lead' end) TypeName,"
                    + "CardFName,OCRG.GroupCode,OCRG.GroupName,Currency,OCRN.CurrName,Address from OCRD "
                    + "left join OCRG on OCRG.GroupCode=OCRD.GroupCode "
                    + "left join OCRN on OCRN.CurrCode=OCRD.Currency where CardCode=?; "
                    + "select CntctCode,Name,Position,Cellolar,Title from OCPR where CardCode=?; "
                    + "select Address from CRD1 where CardCode=?";
        Parameters p = new Parameters().add(1, cardCode).add(2, cardCode).add(3, cardCode);
        Result<DataSet> r = App.Current.DbPortal.ExecuteDataSet(this.Connector, sql, p);
        if (r.Value != null) {
            DataRow row = null;
            if (r.Value.Tables.size() > 0 && r.Value.Tables.get(0).Rows.size() > 0) {
                row = r.Value.Tables.get(0).Rows.get(0);
                
                String title = this.Title + " - " + row.getValue("CardCode").toString();
                this.Header.setTitleText(title);
                
                if (CardCodeCell != null) {
                    CardCodeCell.setContentText(row.getValue("CardCode",""));
                }
                
                if (CardNameCell != null) {
                    CardNameCell.setContentText(row.getValue("CardName",""));
                }
                
                if (FNameCell != null) {
                    FNameCell.setContentText(row.getValue("CardFName",""));
                }
                
                if (TypeCell != null) {
                    String typeName = row.getValue("TypeName","");
                    typeName = App.Current.ResourceManager.getString(typeName);
                    TypeCell.setContentText(typeName);
                    TypeCell.setTag(row.getValue("CardType"));
                }
                
                if (GroupCell != null) {
                    GroupCell.setContentText(row.getValue("GroupName", ""));
                    GroupCell.setTag(row.getValue("GroupCode"));
                }
                
                if (CurrencyCell != null) {
                    CurrencyCell.setContentText(row.getValue("CurrName", ""));
                    CurrencyCell.setTag(row.getValue("Currency"));
                }
                
                if (AddressCell != null) {
                    AddressCell.setContentText(row.getValue("Address", ""));
                }
            }
            
            if (this.ContactAdapter != null) {
                if (r.Value.Tables.size() > 1) {
                    this.ContactAdapter.DataTable = r.Value.Tables.get(1);
                    this.ContactAdapter.notifyDataSetChanged();
                }
            }
            
            if (this.AddressAdapter != null) {
                if (r.Value.Tables.size() > 2) {
                    this.AddressAdapter.DataTable = r.Value.Tables.get(2);
                    this.AddressAdapter.notifyDataSetChanged();
                }
            }

        } else {
            App.Current.showError(getContext(), r.Error);
        }
    }
    
    @Override
    public void prev()
    {
        String itemCode = (String)this.Parameters.get("CardCode");
        String sql = "select top 1 CardCode from OCRD where CardCode<? order by CardCode desc";
        Parameters p = new Parameters().add(1, itemCode);
        Result<String> r = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, String.class);
        if (r.Value != null) {
            this.Parameters.remove("CardCode");
            this.Parameters.put("CardCode", r.Value);
            this.refreshData();
        } else {
            App.Current.showError(getContext(), r.Error);
        }
    }
    
    @Override
    public void next()
    {
        String itemCode = (String)this.Parameters.get("CardCode");
        String sql = "select top 1 CardCode from OCRD where CardCode>?";
        Parameters p = new Parameters().add(1, itemCode);
        Result<String> r = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, String.class);
        if (r.Value != null) {
            this.Parameters.remove("CardCode");
            this.Parameters.put("CardCode", r.Value);
            this.refreshData();
        } else {
            App.Current.showError(getContext(), r.Error);
        }
    }
}
