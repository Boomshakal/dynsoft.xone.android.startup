package dynsoft.xone.android.wms;

import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import dynsoft.xone.android.adapter.PageTableAdapter;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.core.Workbench;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.data.SqlExpression;
import dynsoft.xone.android.start.pn_base;

public class pn_mgr extends pn_base {
	
	public pn_mgr(Context context) {
		super(context);
	}

    public EditText SearchBox;
    public ListView Matrix;
    public PageTableAdapter Adapter;
    public ImageButton RefreshButton;
    public ImageButton NewButton;
    public boolean RefreshOnLoad = true;

	@Override
    public void onPrepared() {
        super.onPrepared();

        this.SearchBox = (EditText) this.findViewById(R.id.txt_search);
        this.Matrix = (ListView)this.findViewById(R.id.matrix);
        this.RefreshButton = (ImageButton)this.findViewById(R.id.btn_refresh);
        this.NewButton = (ImageButton)this.findViewById(R.id.btn_new);
        
        if (this.SearchBox != null) {
        	this.SearchBox.setOnKeyListener(new OnKeyListener(){
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
						pn_mgr.this.Adapter.DataTable = null;
						pn_mgr.this.Adapter.notifyDataSetChanged();
						pn_mgr.this.refresh();
						return true;
					}
					return false;
				}
			});
        }
        
        if (this.Matrix != null) {
        	this.Matrix.setCacheColorHint(Color.TRANSPARENT);
            LinearLayout.LayoutParams lp_listview = new LinearLayout.LayoutParams(-1,-1);
            this.Matrix.setLayoutParams(lp_listview);
            this.Matrix.setOnItemClickListener(new OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (id < Adapter.DataTable.Rows.size()) {
                        DataRow row = (DataRow)Adapter.getItem((int)id);
                        pn_mgr.this.openItem(row);
                    } else {
                    	pn_mgr.this.refreshData(false);
                    }
                }
            });
            
            Adapter = new PageTableAdapter(this.getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (Adapter.DataTable != null) {
                        if (position < Adapter.DataTable.Rows.size()) {
                            if (convertView == Adapter.Footer) convertView = null;
                            return pn_mgr.this.getItemView(position, convertView, parent);
                        } else {
                            return Adapter.Footer;
                        }
                    }
                    return null;
                }
            };
            
            this.Adapter.PageIndex = 0;
            this.Adapter.PageSize = this.getPageSize();
            this.Matrix.setAdapter(Adapter);
            
            if (this.RefreshOnLoad) {
            	this.refreshData(false);
            }
        }
        
        if (this.RefreshButton != null) {
            this.RefreshButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_refresh_white"));
            this.RefreshButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                	pn_mgr.this.refresh();
                }
            });
        }
        
        if (this.NewButton != null) {
            this.NewButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_new_white"));
            this.NewButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                	pn_mgr.this.create();
                }
            });
        }
	}
	
	public void close()
    {
    	((Workbench)App.Current.Workbench).closePane(pn_mgr.this);
    }
	
	public void create()
	{}
	
    public void refresh()
    {
        this.Adapter.PageIndex = 0;
        this.refreshData(false);
    }
    
    public void refreshData(final boolean scroll)
    {
        String txt = this.SearchBox.getText().toString();
        int top = this.Adapter.PageSize*(this.Adapter.PageIndex+1);
        int start = (this.Adapter.PageSize*this.Adapter.PageIndex)+1;
        int end = this.Adapter.PageSize*(this.Adapter.PageIndex+1);
        SqlExpression expr = this.getSqlExpression(top, start, end, txt);
        if (expr != null) {
            if (this.Adapter.DataTable != null){
                if (this.Adapter.PageIndex == 0) {
                	this.Adapter.DataTable.Rows.clear();
                }
            }
            
            this.ProgressDialog.show();
            App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, expr.SQL, expr.Parameters, new ResultHandler<DataTable>(){
            	@Override 
            	public void handleMessage(Message msg){
            		pn_mgr.this.ProgressDialog.dismiss();
            		
            		Result<DataTable> r = this.Value;
            		if (r.HasError) {
            			App.Current.showError(pn_mgr.this.getContext(), r.Error);
    					return;
            		}
            		
            		if (pn_mgr.this.Adapter.DataTable == null) {
            			pn_mgr.this.Adapter.DataTable = r.Value;
                    	if (r.Value != null && r.Value.Rows.size() > 0) {
            				pn_mgr.this.Adapter.PageIndex += 1;
            				DataRow row = r.Value.Rows.get(0);
                			Integer lines_count = row.getValue("lines_count", Integer.class);
                			
                			String count = String.valueOf(lines_count);
                			String title = pn_mgr.this.Title + "(共" + count + "条)";
                			pn_mgr.this.Header.setTitleText(title);
                			
                			if (lines_count > pn_mgr.this.Adapter.PageSize) {
                    			pn_mgr.this.Adapter.FooterText.setText("点击还有更多...");
                			} else {
                				pn_mgr.this.Adapter.FooterText.setText("没有更多了");
                			}
            			} else {
            				String title = pn_mgr.this.Title + "(共0条)";
                			pn_mgr.this.Header.setTitleText(title);
                			pn_mgr.this.Adapter.FooterText.setText("没有更多了");
            			}
            		} else {
            			if (r.Value != null && r.Value.Rows.size() > 0) {
            				for (DataRow row : r.Value.Rows) {
                            	pn_mgr.this.Adapter.DataTable.Rows.add(row);
                            }
            				pn_mgr.this.Adapter.PageIndex += 1;
            				
            				DataRow row = r.Value.Rows.get(0);
                			Integer lines_count = row.getValue("lines_count", Integer.class);
                			
                			String count = String.valueOf(lines_count);
                			String title = pn_mgr.this.Title + "(共" + count + "条)";
                			pn_mgr.this.Header.setTitleText(title);
                			
                			if (lines_count > pn_mgr.this.Adapter.DataTable.Rows.size()) {
                    			pn_mgr.this.Adapter.FooterText.setText("点击还有更多...");
                			} else {
                				pn_mgr.this.Adapter.FooterText.setText("没有更多了");
                			}
            			} else {
            				pn_mgr.this.Adapter.FooterText.setText("没有更多了");
            			}
            		}
            		
                    pn_mgr.this.Adapter.notifyDataSetChanged();
                    
                    if (scroll) {
                    	pn_mgr.this.Matrix.smoothScrollToPosition(pn_mgr.this.Adapter.getCount());
                    }
            	}
            });
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
    { }
}
