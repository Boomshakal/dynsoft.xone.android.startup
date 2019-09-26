package dynsoft.xone.android.sbo;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import dynsoft.xone.android.adapter.PageTableAdapter;
import dynsoft.xone.android.base.BasePane;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.SqlExpression;

public class obj_mgr extends BasePane {

    public obj_mgr(Context context) {
		super(context);

	}

	public LinearLayout FilterLayout;
    public EditText SearchTextBox;
    public ImageButton SearchButton1;
    public ImageButton SearchButton2;
    public LinearLayout BodyLayout;
    public ListView ListView;
    public PageTableAdapter Adapter;
    
    @Override
    public void onScan(String barcode)
    {
        if (SearchTextBox != null && barcode != null) {
            SearchTextBox.setText(barcode);
            SearchTextBox.setSelection(SearchTextBox.getText().length());
            Adapter.PageIndex = 0;
            this.refreshData(false);
        }
    }
    
    @Override
    public void onPrepared() {
        super.onPrepared();
        
        FilterLayout = (LinearLayout)this.Elements.get("filter").Object;
        BodyLayout = (LinearLayout)this.Elements.get("body").Object;
        ListView = (ListView)this.Elements.get("listview").Object;
        
        if (FilterLayout != null) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
            FilterLayout.setLayoutParams(lp);
            
            View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.mgr_filter, null);
            view.setLayoutParams(lp);

            SearchTextBox = (EditText)view.findViewById(R.id.txtSearch);
            
            OnClickListener listener = new OnClickListener(){
                @Override
                public void onClick(View v) {
                    Adapter.PageIndex = 0;
                    obj_mgr.this.refreshData(false);
                }
            };
            
            SearchButton1  = (ImageButton)view.findViewById(R.id.btnSearch1);
            SearchButton1.setImageBitmap(App.Current.ResourceManager.getImage("@/core_search_gray"));
            SearchButton1.setOnClickListener(listener);
            
            SearchButton2  = (ImageButton)view.findViewById(R.id.btnSearch2);
            SearchButton2.setImageBitmap(App.Current.ResourceManager.getImage("@/core_search_gray"));
            SearchButton2.setOnClickListener(listener);
            
            FilterLayout.addView(view);
        }
        
        if (BodyLayout != null) {
            LinearLayout.LayoutParams lp_scroll = new LinearLayout.LayoutParams(-1,-2);
            lp_scroll.weight = 1;
            BodyLayout.setLayoutParams(lp_scroll);
            BodyLayout.setBackgroundColor(Color.WHITE);
        }
        
        if (ListView != null) {
            LinearLayout.LayoutParams lp_listview = new LinearLayout.LayoutParams(-1,-1);
            ListView.setLayoutParams(lp_listview);
            ListView.setOnItemClickListener(new OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (id < Adapter.DataTable.Rows.size()) {
                        DataRow row = (DataRow)Adapter.getItem((int)id);
                        obj_mgr.this.openItem(row);
                    } else {
                        obj_mgr.this.refreshData(false);
                    }
                }
            });
            
            Adapter = new PageTableAdapter(this.getContext()){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (Adapter.DataTable != null) {
                        if (position < Adapter.DataTable.Rows.size()) {
                            if (convertView == Adapter.Footer) convertView = null;
                            return obj_mgr.this.getItemView(position, convertView, parent);
                        } else {
                            return Adapter.Footer;
                        }
                    }
                    return null;
                }
            };
            
            Adapter.PageIndex = 0;
            Adapter.PageSize = this.getPageSize();
            ListView.setAdapter(Adapter);
            this.refreshData(false);
        }
    }

    @Override
    public void refresh()
    {
        this.Adapter.PageIndex = 0;
        this.refreshData(false);
    }
    
    public void refreshData(boolean scroll)
    {
        String txt = SearchTextBox.getText().toString();
        int top = Adapter.PageSize*(Adapter.PageIndex+1);
        int start = (Adapter.PageSize*Adapter.PageIndex)+1;
        int end = Adapter.PageSize*(Adapter.PageIndex+1);
        SqlExpression expr = this.getSqlExpression(top, start, end, txt);
        if (expr != null) {
            if (Adapter.DataTable != null){
                if (Adapter.PageIndex == 0) {
                    Adapter.DataTable.Rows.clear();
                }
            }
            
            Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, expr.SQL, expr.Parameters);
            if (r.Value != null && r.Value.Rows.size() > 0) {
                if (Adapter.DataTable != null) {
                    for (DataRow row : r.Value.Rows) {
                        Adapter.DataTable.Rows.add(row);
                    }
                } else {
                    Adapter.DataTable = r.Value;
                }
                
                Adapter.PageIndex += 1;
                Adapter.notifyDataSetChanged();
                
                if (scroll) {
                    ListView.smoothScrollToPosition(Adapter.getCount());
                }
            } else if (r.HasError) {
                App.Current.showError(getContext(), r.Error);
            }
            
            Adapter.notifyDataSetChanged();
        }
    }
    
    public View getItemView(int position, View convertView, ViewGroup parent)
    {
        return null;
    }
    
    public int getPageSize()
    {
        return 10;
    }
    
    public SqlExpression getSqlExpression(int top, int start, int end, String search)
    {
        return null;
    }
    
    public void openItem(DataRow row)
    {
    }
}
