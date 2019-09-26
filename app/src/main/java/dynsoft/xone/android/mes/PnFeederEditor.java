package dynsoft.xone.android.mes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.base.BasePane;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

public class PnFeederEditor extends BasePane {

    public PnFeederEditor(Context context) {
		super(context);
	}

	public ScrollView Scroll;
    public LinearLayout Body;
    
    public ButtonTextCell DeviceCell;
    public TextCell ItemCodeCell;
    public TextCell FeederCodeCell;
    public TextCell FeederNameCell;
    public TextCell PartCodeCell;
    public TableAdapter DeviceAdapter;
    
    @Override
    public void onPrepared() {
        super.onPrepared();
        
        Scroll = (ScrollView)this.Elements.get("scroll").Object;
        Body = (LinearLayout)this.Elements.get("body").Object;
        
        DeviceCell = (ButtonTextCell)this.Elements.get("device").Object;
        ItemCodeCell = (TextCell)this.Elements.get("itemcode").Object;
        FeederCodeCell = (TextCell)this.Elements.get("feedercode").Object;
        FeederNameCell = (TextCell)this.Elements.get("feedername").Object;
        PartCodeCell = (TextCell)this.Elements.get("partcode").Object;
        
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
        
        this.DeviceCell.setContentText(this.Parameters.get("device").toString());
        this.DeviceCell.setOnKeyListener(null);
        this.DeviceCell.Button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                PnFeederEditor.this.selectDevice();
            }
        });
        
        this.ItemCodeCell.setContentText(this.Parameters.get("itemcode").toString());
        this.ItemCodeCell.setOnKeyListener(null);
        
        this.FeederCodeCell.setContentText(this.Parameters.get("feedercode").toString());
        this.FeederNameCell.setContentText(this.Parameters.get("feedername").toString());
        this.PartCodeCell.setContentText(this.Parameters.get("partcode").toString());
    }
    
    private void selectDevice()
    {
        if (this.DeviceAdapter == null) {
            this.DeviceAdapter = new TableAdapter(getContext()){

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    DataRow row = (DataRow)PnFeederEditor.this.DeviceAdapter.getItem(position);
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
            AlertDialog dialog = new AlertDialog.Builder(getContext())
            .setTitle("选择设备")
            .setSingleChoiceItems(this.DeviceAdapter, 0, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which < PnFeederEditor.this.DeviceAdapter.DataTable.Rows.size()) {
                        DataRow row = (DataRow)((AlertDialog)dialog).getListView().getAdapter().getItem(which);
                        if (row != null) {
                            PnFeederEditor.this.DeviceCell.setContentText(row.getValue("device", String.class));
                        }
                        dialog.dismiss();
                    }
                }
            })
            .setNegativeButton("取消", null).create();
            dialog.show();
        }
    }
    
    @Override
    public void onScan(String barcode)
    {
        if (barcode == null) return;
        
        barcode = barcode.trim();
        if (barcode.length() == 0) return;
        
        if (barcode.length() < 16) {
            String device = this.DeviceCell.getContentText().trim();
            String sql = "select top 1 feedername from feeder where device=? and feedercode=?";
            Parameters p = new Parameters().add(1, device).add(2, barcode);
            Result<DataRow> r = App.Current.DbPortal.ExecuteRecord(this.Connector, sql, p);
            if (r.HasError) {
                App.Current.showError(getContext(), "查询位置编号出错：" + r.Error);
                return;
            }
            
            FeederCodeCell.setContentText(barcode);
            if (r.Value != null) {
                FeederNameCell.setContentText(r.Value.getValue("feedername", String.class));
            } else {
                FeederNameCell.setContentText("");
            }
            FeederNameCell.TextBox.requestFocus();

            return;
        }
        
        String isnew = this.Parameters.get("isnew").toString();
        if (isnew.equals("True")) {
            if (barcode.length() > 16) {
                String partcode = barcode.substring(0,barcode.length()-16);
                PartCodeCell.setContentText(partcode);
                return;
            }
        }
    }
    
    @Override
    public void save()
    {
        final String device = DeviceCell.getContentText();
        if (device== null || device.length() == 0) {
            App.Current.showError(getContext(), "缺少设备编号。");
            return;
        }
        
        final String itemcode = ItemCodeCell.getContentText();
        if (itemcode== null || itemcode.length() == 0) {
            App.Current.showError(getContext(), "缺少产品编号。");
            return;
        }
        
        final String partcode = PartCodeCell.getContentText();
        if (partcode== null || partcode.length() == 0) {
            App.Current.showError(getContext(), "缺少物料编号。");
            return;
        }
        
        final String feedercode = FeederCodeCell.getContentText();
        if (feedercode== null || feedercode.length() == 0) {
            App.Current.showError(getContext(), "缺少位置编号。");
            return;
        }
        
        final String feedername = FeederNameCell.getContentText();
//        if (feedername== null || feedername.length() == 0) {
//            App.Current.showError(getContext(), "缺少位置名称。");
//            return;
//        }
        
        String isnew = this.Parameters.get("isnew").toString();
        if (isnew.equals("True")) {
            
            String sql= "select count(*) from feeder where device=? and itemcode=? and partcode=?";
            Parameters p = new Parameters().add(1, device).add(2, itemcode).add(3, partcode);
            Result<Integer> r = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, Integer.class);
            if (r.Value != null && r.Value > 0) {
                App.Current.question(PnFeederEditor.this.getContext(), "该物料已经存在，如果继续保存，则会修改其上料位置。确定要继续保存吗？", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String sql = "update feeder set feedercode=?,feedername=? where device=? and itemcode=? and partcode=?";
                        Parameters p = new Parameters().add(1, feedercode).add(2, feedername).add(3, device).add(4, itemcode).add(5, partcode);
                        Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(PnFeederEditor.this.Connector, sql, p);
                        if (r.HasError) {
                            App.Current.showError(PnFeederEditor.this.getContext(), r.Error);
                            return;
                        } else if (r.Value > 0) {
                            App.Current.showInfo(PnFeederEditor.this.getContext(), "修改成功。");
                            return;
                        }
					}
				});
            } else {
                sql = "insert into feeder(device,itemcode,partcode,feedercode,feedername)values(?,?,?,?,?)";
                p = p.clearAll().add(1, device).add(2, itemcode).add(3, partcode).add(4, feedercode).add(5, feedername);
                r = App.Current.DbPortal.ExecuteNonQuery(this.Connector, sql, p);
                if (r.HasError) {
                    App.Current.showError(PnFeederEditor.this.getContext(), r.Error);
                    return;
                } 
                
                if (r.Value != null && r.Value > 0) {
                    App.Current.showInfo(getContext(), "保存成功。");
                    PartCodeCell.setContentText("");
                    FeederCodeCell.setContentText("");
                    FeederNameCell.setContentText("");
                }
            }
        } else {
            String oldDevice = this.Parameters.get("device", "");
            String oldItemCode = this.Parameters.get("itemcode", "");
            String oldPartCode = this.Parameters.get("partcode", "");
            
            String sql = "update feeder set device=?,itemcode=?,partcode=?,feedercode=?,feedername=? where device=? and itemcode=? and partcode=?";
            Parameters p = new Parameters().add(1, device).add(2, itemcode).add(3, partcode).add(4, feedercode).add(5, feedername).add(6, oldDevice).add(7, oldItemCode).add(8, oldPartCode);
            Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(this.Connector, sql, p);
            if (r.HasError) {
                App.Current.showError(getContext(), r.Error);
                return;
            }

            if (r.Value != null && r.Value > 0) {
                App.Current.showInfo(getContext(), "修改成功。");
            }
        }
    }
}
