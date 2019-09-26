package dynsoft.xone.android.mes;

import java.math.BigDecimal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.base.BasePane;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.NestedListView;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.link.PaneLinker;
import dynsoft.xone.android.sbo.odoc_editor;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.link.Link;

public class PnFeederManager extends BasePane {

    public PnFeederManager(Context context) {
		super(context);
	}

	public ScrollView Scroll;
    public LinearLayout Body;
    
    public ButtonTextCell DeviceCell;
    public ButtonTextCell ItemCodeCell;
    public NestedListView LinesListView;
    public TableAdapter LinesAdapter;
    public TableAdapter DeviceAdapter;
    public TableAdapter ItemCodeAdapter;
        
    @Override
    public void onPrepared() {
        super.onPrepared();
        
        Scroll = (ScrollView)this.Elements.get("scroll").Object;
        Body = (LinearLayout)this.Elements.get("body").Object;
        
        this.DeviceCell = (ButtonTextCell)this.Elements.get("device").Object;
        this.ItemCodeCell = (ButtonTextCell)this.Elements.get("itemcode").Object;
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
        
        if (this.DeviceCell != null) {
            this.DeviceCell.setLabelText("贴片机");
            this.DeviceCell.Button.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    PnFeederManager.this.selectDevice();
                }
            });
        }
        
        if (this.ItemCodeCell != null) {
            this.ItemCodeCell.setLabelText("产品");
            this.ItemCodeCell.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View arg0) {
                    PnFeederManager.this.selectItemCode();
                }
            });
        }
        
        if (this.LinesListView != null) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
            lp.height = App.dpToPx(300);
            this.LinesListView.setLayoutParams(lp);
            this.LinesListView.ScrollView = this.Scroll;
            this.LinesAdapter = new TableAdapter(this.getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.feeder_line, null);
                    }
                    
                    DataRow row = (DataRow)PnFeederManager.this.LinesAdapter.getItem(position);
                    TextView num = (TextView)convertView.findViewById(R.id.num);
                    TextView feeder = (TextView)convertView.findViewById(R.id.feedername);
                    TextView part = (TextView)convertView.findViewById(R.id.partcode);
                    
                    num.setText(String.valueOf(position + 1));
                    feeder.setText("位置: " + row.getValue("feedername","") + ", " + row.getValue("feedercode",""));
                    part.setText("物料：" + row.getValue("partcode", ""));
                    
                    return convertView;
                }
            };
            
            this.LinesListView.setAdapter(this.LinesAdapter);
            this.LinesListView.setOnItemClickListener(new OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    
                    DataRow row = (DataRow)PnFeederManager.this.LinesAdapter.getItem(arg2);
                    if (row != null) {
                        Link link = new Link("pane://x:code=mes_and_feeder_editor");
                        link.Parameters.add(PaneLinker.KEY_XFLAG, -1);
                        link.Parameters.add("device", row.getValue("device"));
                        link.Parameters.add("itemcode", row.getValue("itemcode"));
                        link.Parameters.add("partcode", row.getValue("partcode"));
                        link.Parameters.add("feedercode", row.getValue("feedercode"));
                        link.Parameters.add("feedername", row.getValue("feedername"));
                        link.Parameters.add("isnew", "False");
                        link.Open(PnFeederManager.this, PnFeederManager.this.getContext(), null);
                    }
                    
                }
            });
        }
    }
    
    @Override
    public void refresh() {
        this.refreshLines();
    }

    private void selectDevice()
    {
        if (this.DeviceAdapter == null) {
            this.DeviceAdapter = new TableAdapter(this.getContext()){

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    DataRow row = (DataRow)PnFeederManager.this.DeviceAdapter.getItem(position);
                    if (convertView == null) {
                        convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.feeder_device_line, null);
                    }

                    TextView num = (TextView)convertView.findViewById(R.id.num);
                    TextView device = (TextView)convertView.findViewById(R.id.device);
                    
                    num.setText(String.valueOf(position + 1));
                    device.setText(row.getValue("device", String.class));

                    return convertView;
                }
            };
        }
        
        String sql = "select distinct device from feeder";
        Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql);
        if (r.Value != null) {
            this.DeviceAdapter.DataTable = r.Value;
            AlertDialog dialog = new AlertDialog.Builder(this.getContext())
            .setTitle("选择设备")
            .setSingleChoiceItems(this.DeviceAdapter, 0, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which < PnFeederManager.this.DeviceAdapter.DataTable.Rows.size()) {
                        DataRow row = (DataRow)((AlertDialog)dialog).getListView().getAdapter().getItem(which);
                        if (row != null) {
                            PnFeederManager.this.DeviceCell.setContentText(row.getValue("device", String.class));
                            PnFeederManager.this.refreshLines();
                        }
                        dialog.dismiss();
                    }
                }
            })
            .setNegativeButton("取消", null).create();
            dialog.show();
        }
    }
    
    private void selectItemCode()
    {
        if (this.ItemCodeAdapter == null) {
            this.ItemCodeAdapter = new TableAdapter(this.getContext()){

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    DataRow row = (DataRow)PnFeederManager.this.ItemCodeAdapter.getItem(position);
                    if (convertView == null) {
                        convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.feeder_itemcode_line, null);
                    }

                    TextView num = (TextView)convertView.findViewById(R.id.num);
                    TextView device = (TextView)convertView.findViewById(R.id.itemcode);
                    
                    num.setText(String.valueOf(position + 1));
                    device.setText(row.getValue("itemcode", String.class));

                    return convertView;
                }
            };
        }
        
        String sql = "select distinct itemcode from feeder where device=?";
        Parameters p = new Parameters().add(1, this.DeviceCell.getContentText().trim());
        Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (r.Value != null) {
            this.ItemCodeAdapter.DataTable = r.Value;
            AlertDialog dialog = new AlertDialog.Builder(this.getContext())
            .setTitle("选择产品")
            .setSingleChoiceItems(this.ItemCodeAdapter, 0, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which < PnFeederManager.this.ItemCodeAdapter.DataTable.Rows.size()) {
                        DataRow row = (DataRow)((AlertDialog)dialog).getListView().getAdapter().getItem(which);
                        if (row != null) {
                            PnFeederManager.this.ItemCodeCell.setContentText(row.getValue("itemcode", String.class));
                            PnFeederManager.this.refreshLines();
                        }
                        dialog.dismiss();
                    }
                }
            })
            .setNegativeButton("取消", null).create();
            dialog.show();
        }
    }
    
    private void refreshLines()
    {
        String device = this.DeviceCell.getContentText().trim();
        String itemcode = this.ItemCodeCell.getContentText().trim();
        
        String sql = "select * from feeder where device=? and itemcode=?";
        Parameters p = new Parameters().add(1, device).add(2,itemcode);
        Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (r.Value != null) {
            this.LinesAdapter.DataTable = r.Value;
            this.LinesAdapter.notifyDataSetChanged();
        } else if (r.HasError) {
            App.Current.showError(this.getContext(), r.Error);
        }
    }

    @Override
    public void create() {

        String device = this.DeviceCell.getContentText().trim();
        if (device == null || device.length() == 0) {
            App.Current.showError(this.getContext(), "请先选择贴片机。");
            return;
        }
        
        String itemcode = this.ItemCodeCell.getContentText().trim();
        if (itemcode == null || itemcode.length() == 0) {
            App.Current.showError(this.getContext(), "请先选择产品编号。");
            return;
        }
        
        Link link = new Link("pane://x:code=mes_and_feeder_editor");
        link.Parameters.add(PaneLinker.KEY_XFLAG, -1);
        link.Parameters.add("device", device);
        link.Parameters.add("itemcode", itemcode);
        link.Parameters.add("partcode", "");
        link.Parameters.add("feedercode", "");
        link.Parameters.add("feedername", "");
        link.Parameters.add("isnew", "True");
        link.Open(PnFeederManager.this, PnFeederManager.this.getContext(), null);
    }
    
    
}
