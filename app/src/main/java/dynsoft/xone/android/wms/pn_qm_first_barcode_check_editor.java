package dynsoft.xone.android.wms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.activity.AlbumActivity;
import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.util.Res;
import dynsoft.xone.android.zoom.PhotoView;
import dynsoft.xone.android.zoom.ViewPagerFixed;

public class pn_qm_first_barcode_check_editor extends pn_editor {

    private byte[] img;

    public pn_qm_first_barcode_check_editor(Context context) {
        super(context);
    }

    public ButtonTextCell txt_code;
    public TextCell txt_line1;
    public TextCell txt_line2;
    public ButtonTextCell txt_line4;
    public ButtonTextCell txt_line6;
    public ButtonTextCell txt_line9;
    private ImageView imageview;
    public ImageButton btn_prev;
    public ImageButton btn_next;
    private View parentView;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;


    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_first_barcode_check_editor, this, true);
        parentView = view;
        view.setLayoutParams(lp);
        //noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
    }

    @Override
    public void onPrepared() {

        super.onPrepared();

        this.txt_code = (ButtonTextCell) this.findViewById(R.id.txt_code);
        this.txt_line1 = (TextCell) this.findViewById(R.id.txt_line1);
        this.txt_line2 = (TextCell) this.findViewById(R.id.txt_line2);
        this.txt_line4 = (ButtonTextCell) this.findViewById(R.id.txt_line4);
        this.txt_line6 = (ButtonTextCell) this.findViewById(R.id.txt_line6);
//        this.txt_line8 = (ButtonTextCell) this.findViewById(R.id.txt_line8);
        this.txt_line9 = (ButtonTextCell) this.findViewById(R.id.txt_line9);

        this.btn_prev = (ImageButton) this.findViewById(R.id.btn_prev);
        this.btn_next = (ImageButton) this.findViewById(R.id.btn_next);
        this.imageview = (ImageView) this.findViewById(R.id.imageview);

        head_id = this.Parameters.get("head_id", 0);
        _order_code = this.Parameters.get("order_code", "");
        _rownum = this.Parameters.get("index", 0L);

        if (this.txt_code != null) {
            this.txt_code.setLabelText("巡检单号");
            this.txt_code.setReadOnly();
            this.txt_code.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_qm_first_barcode_check_editor.this.showList(false);
                }
            });
        }
        if (this.txt_line1 != null) {
            this.txt_line1.setLabelText("工单/线别");
            this.txt_line1.setReadOnly();
        }

        if (this.txt_line2 != null) {
            this.txt_line2.setLabelText("机型");
            this.txt_line2.setReadOnly();
        }

        if (this.txt_line4 != null) {
            this.txt_line4.setLabelText("标准值");
            this.txt_line4.setReadOnly();
			this.txt_line4.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_look_pic2"));
            this.txt_line4.Button.setVisibility(GONE);
        }


        if (this.txt_line6 != null) {
            this.txt_line6.setLabelText("条码");
            this.txt_line6.Button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					App.Current.Workbench.scanByCamera();
				}
            	
            });
        }

//        if (this.txt_line8 != null) {
//            this.txt_line8.setLabelText("测试值");
//            this.txt_line8.Button.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    loadSelectValue(_order_row.getValue("dic_name", ""), pn_qm_first_barcode_check_editor.this.txt_line8);
//                }
//            });
//        }
        if (this.txt_line9 != null) {
            this.txt_line9.setLabelText("检验结果");
            this.txt_line9.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadSelectValue("IPQC检验结果", pn_qm_first_barcode_check_editor.this.txt_line9);
                }
            });
        }


		/*if (this.btnpic1 != null) {
            this.btnpic1.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					App.Current.Workbench.onCamera();
				}
			});
		}

		if (this.btnpic2 != null) {
			this.btnpic2.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					 App.Current.Workbench.onPictrue();
				}
			});
		}*/

        if (this.btn_prev != null) {
            this.btn_prev.setImageBitmap(App.Current.ResourceManager.getImage("@/core_prev_white"));
            this.btn_prev.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_qm_first_barcode_check_editor.this.prev();
                }
            });
        }
        Res.init(this.getContext());
        parentView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_ipqc_patrol_record_editor, this, true);
        if (this.btn_next != null) {
            this.btn_next.setImageBitmap(App.Current.ResourceManager.getImage("@/core_next_white"));
            this.btn_next.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_qm_first_barcode_check_editor.this.next();
                }
            });
        }
        if (_rownum == null) {
            _rownum = 1L;
        }
        this.txt_code.setContentText(_order_code);
        this.loadItem(_rownum);
    }

    private Long _rownum;
    private int head_id;
    private String _order_code;
    private Integer _total = 0;
    private int line_id;
    private DataRow _order_row;
    private Integer standpic_flag;


    public void prev() {
        if (_rownum > 1) {
            this.loadItem(_rownum - 1);
        } else {
            App.Current.showError(pn_qm_first_barcode_check_editor.this.getContext(), "已经是第一条。");
        }
    }

    public void next() {
        if (_rownum < _total) {
            this.loadItem(_rownum + 1);
        } else {
            App.Current.showError(pn_qm_first_barcode_check_editor.this.getContext(), "已经是最后一条。");
        }
    }

    @Override
    public void onScan(final String barcode) {
        final String bar_code = barcode.trim();

        //扫描批次条码
        if (bar_code.startsWith("CRQ:")) {
            String str = bar_code.substring(4, bar_code.length());
            String[] arr = str.split("-");
            if (arr.length > 2) {
                String lot_number = arr[0].trim();
                this.txt_line6.setContentText(lot_number);

            }
     
        }
        else if (bar_code.startsWith("TO:"))
        {
            String task_order_code = bar_code.substring(3, bar_code.length());
            long task_order_id =_order_row.getValue("task_order_id",0L);
            CheckItemNo(task_order_code,task_order_id);
        	
        }
        else if (bar_code.startsWith("C:")) {
            String lot = bar_code.substring(2, bar_code.length());
            this.txt_line6.setContentText(lot);
        } else {
        	
        	String sn_no = bar_code;
			String sn_nos = this.txt_line6.getContentText().trim();
			if (sn_nos.contains(sn_no)){
				return;
			}
			
			if (sn_nos.length() > 0){
				sn_nos += ", ";
			}
			
            this.txt_line6.setContentText(sn_nos+sn_no);
            
            
        }


    }

    public void loadItem(long index) {
        this.ProgressDialog.show();

        String sql = "exec p_qm_first_barcode_check_get_item ?,?,?";
        Parameters p = new Parameters().add(1, App.Current.UserID).add(2, head_id).add(3, index);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_qm_first_barcode_check_editor.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_qm_first_barcode_check_editor.this.getContext(), result.Error);
                    return;
                }

                DataRow row = result.Value;
                if (row == null) {
                    App.Current.showError(pn_qm_first_barcode_check_editor.this.getContext(), "找不到条码首件检查任务。");
                    pn_qm_first_barcode_check_editor.this.clearAll();
                    pn_qm_first_barcode_check_editor.this.Header.setTitleText("条码首件检查(找不到条码首件检查任务)");
                    return;
                }

                _order_row = row;
                _total = row.getValue("total", Integer.class);
                _rownum = row.getValue("rownum", Long.class);
                if (_total > 0) {
                    pn_qm_first_barcode_check_editor.this.Header.setTitleText("条码首件检查(" + String.valueOf(_total) + "条检查任务)");
                } else {
                    pn_qm_first_barcode_check_editor.this.Header.setTitleText("条码首件检查(无条码首件检查任务)");
                }

                pn_qm_first_barcode_check_editor.this.txt_code.setTag(row);

                String code = row.getValue("code", "") + ",第" + String.valueOf(_rownum) + "条检查";
                pn_qm_first_barcode_check_editor.this.txt_code.setContentText(code);
                pn_qm_first_barcode_check_editor.this.txt_line1.setContentText(row.getValue("task_order_code", "") + "," + row.getValue("work_line", ""));
                pn_qm_first_barcode_check_editor.this.txt_line2.setContentText(row.getValue("item_name", ""));
                pn_qm_first_barcode_check_editor.this.txt_line4.setContentText(row.getValue("standards", ""));
                img = row.getValue("img", new byte[0]);
                Log.e("len", img.length + "***" + img.toString());
                Glide.with(pn_qm_first_barcode_check_editor.this.getContext()).load(img).error(R.drawable.null_pic).into(imageview);
            }
        });
    }
    
    public void CheckItemNo(String task_order_code,long task_order_id) {
        this.ProgressDialog.show();

        String sql = "exec p_qm_ipqc_patrol_get_itemno ?,?";
        Parameters p = new Parameters().add(1, task_order_code).add(2, task_order_id);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_qm_first_barcode_check_editor.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_qm_first_barcode_check_editor.this.getContext(), result.Error);
                    return;
                }

                DataRow row = result.Value;
                if (row == null) {
                    App.Current.showError(pn_qm_first_barcode_check_editor.this.getContext(), "找不到料号。");
                    return;
                }
                String  result_value =row.getValue("result_value","");
                String  item_no =row.getValue("item_no","");
                 if (result_value.equals("NG"))
                 {
                	 App.Current.showError(pn_qm_first_barcode_check_editor.this.getContext(), "SMD组件不存在此工单中。");
                     App.Current.playSound(R.raw.hook);
                 
                 }

                 pn_qm_first_barcode_check_editor.this.txt_line9.setContentText(result_value);
              
           
            }
        });
    }


    public void showList(boolean forLine) {
        if (_order_row == null) {
            App.Current.showError(this.getContext(), "没有发料申请数据。");
            return;
        }

        int line_id = 0;
        if (forLine) {
            line_id = _order_row.getValue("line_id", 0);
        }

        String sql = "exec p_mm_wo_get_transaction_list ?,?,?,?";
        Parameters p = new Parameters()
                .add(1, _order_row.getValue("type", ""))
                .add(2, _order_row.getValue("id", 0L))
                .add(3, line_id)
                .add(4, App.Current.UserID);

        final Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (r.HasError) {
            App.Current.showError(this.getContext(), r.Error);
            return;
        }

        if (r.Value != null && r.Value.Rows.size() > 0) {
            final TableAdapter adapter = new TableAdapter(this.getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    DataRow row = (DataRow) r.Value.Rows.get(position);
                    if (convertView == null) {
                        convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.ln_wo_transaction, null);
                        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
                        icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
                    }

                    TextView num = (TextView) convertView.findViewById(R.id.num);
                    TextView txt_item_code = (TextView) convertView.findViewById(R.id.txt_item_code);
                    TextView txt_item_name = (TextView) convertView.findViewById(R.id.txt_item_name);
                    TextView txt_lot_number = (TextView) convertView.findViewById(R.id.txt_lot_number);
                    TextView txt_quantity = (TextView) convertView.findViewById(R.id.txt_quantity);

                    num.setText(String.valueOf(position + 1));

                    txt_item_code.setText(row.getValue("item_code", "") + ", " + App.formatDateTime(row.getValue("create_time"), "MM-dd HH:mm:ss"));
                    txt_item_name.setText(row.getValue("item_name", ""));
                    txt_lot_number.setText(row.getValue("lot_number", "") + ", " + row.getValue("date_code", ""));
                    txt_quantity.setText(row.getValue("warehouse_code", "") + ", " + App.formatNumber(row.getValue("quantity", BigDecimal.ZERO), "0.##") + " " + row.getValue("uom_code", ""));

                    return convertView;
                }
            };

            adapter.DataTable = r.Value;
            adapter.notifyDataSetChanged();

            new AlertDialog.Builder(this.getContext())
                    .setTitle("查看发料记录")
                    .setSingleChoiceItems(adapter, 0, null)
                    .setNegativeButton("取消", null).show();
        } else {
            App.Current.showError(this.getContext(), "没有数据。");
            return;
        }
    }

    public int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    @Override
    public void commit() {
        if (_order_row == null) {
            App.Current.showError(this.getContext(), "没有发料申请数据，不能提交。");
            return;
        }

        final String lot_number = this.txt_line6.getContentText();

        final String result_value = this.txt_line9.getContentText();
        if (result_value == null || result_value.length() == 0) {
            App.Current.showError(this.getContext(), "没有输入检查结果。");
            return;
        }

//        final String check_value = this.txt_line8.getContentText();

        App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

//                for (int j = 0; j < Bimp.tempSelectBitmap.size(); j++) {
//                    int size = getBitmapSize(Bimp.tempSelectBitmap.get(j).getBitmap());
//                    String filename = Bimp.tempSelectBitmap.get(j).imagePath;
//                    File file = new File(filename);
//                    String imagePath = Bimp.tempSelectBitmap.get(j).getImagePath();
//                    Toast.makeText(getContext(), imagePath, Toast.LENGTH_SHORT).show();
//                    try {
////						new FileInputStream(new File(imagePath));
////						InputStream str = Bitmap2IS(Bimp.tempSelectBitmap.get(j).getBitmap());
//                        FileInputStream str = new FileInputStream(filename);
//                        String isql = "insert into core_attachment (owner,[group],name,size,data,createtime) select ?,?,?,?,?,getdate()";
//                        Connection conn2 = App.Current.DbPortal.CreateConnection(pn_qm_first_barcode_check_editor.this.Connector);
//                        PreparedStatement ps;
//                        try {
//                            ps = conn2.prepareStatement(isql);
//                            ps.setString(1, "qm_ipqc_patrol_record_editor_" + String.valueOf(_order_row.getValue("line_id", 0L)));
//                            ps.setString(2, "qm_ipqc_patrol_record_editor");
//                            ps.setString(3, "图片" + String.valueOf(j));
//                            ps.setString(4, String.valueOf(size));
//                            ps.setBinaryStream(5, str, str.available());
//                            ps.execute();
//
//                        } catch (SQLException e1) {
//                            App.Current.showInfo(pn_qm_first_barcode_check_editor.this.getContext(), e1.getMessage());
//                            e1.printStackTrace();
//                            return;
//                        }
//                    } catch (Exception e) {
//                        App.Current.showError(pn_qm_first_barcode_check_editor.this.getContext(), "转换失败" + e.getMessage());
//                        e.printStackTrace();
//                        return;
//                    }
//
//                }
                Map<String, String> entry = new HashMap<String, String>();
//                entry.put("code", _order_row.getValue("code", ""));
//                entry.put("create_user", App.Current.UserID);
                entry.put("head_id", String.valueOf(_order_row.getValue("id", 0L)));
                Log.e("len", "line_id:" + _order_row.getValue("line_id", 0));
                entry.put("line_id", String.valueOf(_order_row.getValue("line_id", 0)));
                entry.put("lot_number", lot_number);
//                entry.put("check_value", check_value);      //测试值
                entry.put("result_value", result_value);    //检验结果
                entry.put("standard", txt_line4.getContentText().trim());    //标准值

                ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
                entries.add(entry);

                //生成XML数据，并传给存储过程
                String xml = XmlHelper.createXml("patrol_records", null, null, "patrol_record", entries);
                String sql = "exec p_qm_first_barcode_check_create ?,?";
                Parameters p = new Parameters().add(1, xml).add(2, "");
                App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                    @Override
                    public void handleMessage(Message msg) {
                        Result<Integer> value = Value;
                        if (value.HasError) {
                            App.Current.showError(pn_qm_first_barcode_check_editor.this.getContext(), value.Error);
                            return;
                        }
                        App.Current.toastInfo(pn_qm_first_barcode_check_editor.this.getContext(), "提交成功");
                        _order_row = null;
                        pn_qm_first_barcode_check_editor.this.clear();
                        pn_qm_first_barcode_check_editor.this.loadItem(_rownum);
                    }
                });
            }
        });
    }

    private InputStream Bitmap2IS(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
        return sbs;
    }

    public void clear() {
        this.txt_line9.setContentText("");
        this.txt_line6.setContentText("");
        //this.Init();
    }

    public void clearAll() {
        this.txt_line9.setContentText("");
        this.txt_line6.setContentText("");
        this.txt_line1.setContentText("");
        this.txt_line2.setContentText("");
        this.txt_line4.setContentText("");
        this.txt_code.setContentText("");
    }

    public void loadSelectValue(String lookup_type, final ButtonTextCell txt) {

        String sql = "SELECT lookup_code FROM core_data_keyword where lookup_type =? and lookup_code <> 'NA'";
        Parameters p = new Parameters().add(1, lookup_type);
        final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (result.HasError) {
            App.Current.showError(this.getContext(), result.Error);
            return;
        }
        if (result.Value != null) {

            ArrayList<String> names = new ArrayList<String>();
            for (DataRow row : result.Value.Rows) {
                String name = row.getValue("lookup_code", "");
                names.add(name);
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which >= 0) {
                        DataRow row = result.Value.Rows.get(which);
                        txt.setContentText(row.getValue(
                                "lookup_code", ""));
                    }
                    dialog.dismiss();
                }
            };
            new AlertDialog.Builder(pn_qm_first_barcode_check_editor.this.getContext()).setTitle("请选择")
                    .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(txt.getContentText().toString()), listener)
                    .setNegativeButton("取消", null).show();
        }
    }
}
