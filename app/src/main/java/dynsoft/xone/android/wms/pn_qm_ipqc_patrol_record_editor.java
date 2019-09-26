package dynsoft.xone.android.wms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.activity.AlbumActivity;
import dynsoft.xone.android.activity.GalleryActivity;
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
import dynsoft.xone.android.util.Bimp;
import dynsoft.xone.android.util.Res;
import dynsoft.xone.android.zoom.PhotoView;
import dynsoft.xone.android.zoom.ViewPagerFixed;

public class pn_qm_ipqc_patrol_record_editor extends pn_editor {

    public pn_qm_ipqc_patrol_record_editor(Context context) {
        super(context);
    }

    public ButtonTextCell txt_code;
    public TextCell txt_line1;
    public TextCell txt_line2;
    public TextCell txt_line3;
    public ButtonTextCell txt_line4;
    public TextCell txt_line5;
    public ButtonTextCell txt_line6;
    public ButtonTextCell txt_line8;
    public ButtonTextCell txt_line9;
    private TextCell textCellCheck;
    public ImageButton btn_prev;
    public ImageButton btn_next;
    private GridView noScrollgridview;
    private View parentView;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    public static Bitmap bimap;
    private GridAdapter adapter;


    @Override
    public void setContentView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_ipqc_patrol_record_editor, this, true);
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
        this.txt_line3 = (TextCell) this.findViewById(R.id.txt_line3);
        this.txt_line4 = (ButtonTextCell) this.findViewById(R.id.txt_line4);
        this.txt_line5 = (TextCell) this.findViewById(R.id.txt_line5);
        this.txt_line6 = (ButtonTextCell) this.findViewById(R.id.txt_line6);
        this.txt_line8 = (ButtonTextCell) this.findViewById(R.id.txt_line8);
        this.txt_line9 = (ButtonTextCell) this.findViewById(R.id.txt_line9);
        this.textCellCheck = (TextCell) this.findViewById(R.id.check_record);

        this.btn_prev = (ImageButton) this.findViewById(R.id.btn_prev);
        this.btn_next = (ImageButton) this.findViewById(R.id.btn_next);

        if (this.txt_code != null) {
            this.txt_code.setLabelText("巡检单号");
            this.txt_code.setReadOnly();
            this.txt_code.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_qm_ipqc_patrol_record_editor.this.showList(false);
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

        if (this.txt_line3 != null) {
            this.txt_line3.setLabelText("检验项目");
            this.txt_line3.setReadOnly();
        }


        if (this.txt_line4 != null) {
            this.txt_line4.setLabelText("标准值");
            this.txt_line4.setReadOnly();
            this.txt_line4.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_look_pic2"));
            this.txt_line4.Button.setVisibility(GONE);
            this.txt_line4.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadstandpic(line_id);
                }
            });
        }

        if (this.txt_line5 != null) {
            this.txt_line5.setLabelText("工具/制品处理");
            this.txt_line5.setReadOnly();
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

        if (this.txt_line8 != null) {
            this.txt_line8.setLabelText("测试值");
            this.txt_line8.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadSelectValue(_order_row.getValue("dic_name", ""), pn_qm_ipqc_patrol_record_editor.this.txt_line8);
                }
            });
        }
        if (this.txt_line9 != null) {
            this.txt_line9.setLabelText("检验结果");
            this.txt_line9.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadSelectValue("IPQC检验结果", pn_qm_ipqc_patrol_record_editor.this.txt_line9);
                }
            });
        }

        if (textCellCheck != null) {
            textCellCheck.setLabelText("检查记录");
            textCellCheck.setReadOnly();
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
                    pn_qm_ipqc_patrol_record_editor.this.prev();
                }
            });
        }
        Res.init(this.getContext());

        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.icon_addpic_unfocused);
        parentView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_ipqc_patrol_record_editor, this, true);
        Init();
        if (this.btn_next != null) {
            this.btn_next.setImageBitmap(App.Current.ResourceManager.getImage("@/core_next_white"));
            this.btn_next.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_qm_ipqc_patrol_record_editor.this.next();
                }
            });
        }
        _order_id = this.Parameters.get("order_id", 0L);
        _order_code = this.Parameters.get("order_code", "");
        _rownum = this.Parameters.get("index", 0L);
        if (_rownum == null) {
            _rownum = 1L;
        }
        this.txt_code.setContentText(_order_code);
        this.loadItem(_rownum);
    }


    public void Init() {

        pop = new PopupWindow(pn_qm_ipqc_patrol_record_editor.this.getContext());

        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.item_popupwindows, null);

        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        pop.setWidth(LayoutParams.MATCH_PARENT);
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view
                .findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                App.Current.Workbench.photo();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(pn_qm_ipqc_patrol_record_editor.this.getContext(), AlbumActivity.class);
                intent.putExtra("order_id", _order_id);
                intent.putExtra("index", _rownum);
                pn_qm_ipqc_patrol_record_editor.this.getContext().startActivity(intent);
                App.Current.Workbench.overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();

            }
        });
        bt3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(pn_qm_ipqc_patrol_record_editor.this.getContext());

        if (Bimp.tempSelectBitmap.size() > 0) {
            adapter.update();
        }
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    Log.i("ddddddd", "----------");
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(pn_qm_ipqc_patrol_record_editor.this.getContext(), R.anim.activity_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(pn_qm_ipqc_patrol_record_editor.this.getContext(),
                            GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    App.Current.Workbench.startActivity(intent);
                }
            }
        });

    }

    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if (Bimp.tempSelectBitmap.size() == 9) {
                return 9;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max >= Bimp.tempSelectBitmap.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            Bimp.max += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            }).start();
        }
    }


    private Long _rownum;
    private Long _order_id;
    private String _order_code;
    private Integer _total = 0;
    private long line_id;
    private DataRow _order_row;
    private Integer standpic_flag;


    public void prev() {
        if (_rownum > 1) {
            this.loadItem(_rownum - 1);
        } else {
            App.Current.showError(pn_qm_ipqc_patrol_record_editor.this.getContext(), "已经是第一条。");
        }
    }

    public void next() {
        if (_rownum < _total) {
            this.loadItem(_rownum + 1);
        } else {
            App.Current.showError(pn_qm_ipqc_patrol_record_editor.this.getContext(), "已经是最后一条。");
        }
    }

    @Override
    public void onRefrsh() {
        Init();
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

        } else if (bar_code.startsWith("TO:")) {
            String task_order_code = bar_code.substring(3, bar_code.length());
            long task_order_id = _order_row.getValue("task_order_id", 0L);
            CheckItemNo(task_order_code, task_order_id);

        } else if (bar_code.startsWith("C:")) {
            String lot = bar_code.substring(2, bar_code.length());
            this.txt_line6.setContentText(lot);
        } else {

            String sn_no = bar_code;
            String sn_nos = this.txt_line6.getContentText().trim();
            if (sn_nos.contains(sn_no)) {
                return;
            }

            if (sn_nos.length() > 0) {
                sn_nos += ", ";
            }

            this.txt_line6.setContentText(sn_nos + sn_no);


        }


    }

    public void loadItem(long index) {
        this.ProgressDialog.show();

        String sql = "exec p_qm_ipqc_patrol_record_get_item ?,?,?";
        Parameters p = new Parameters().add(1, App.Current.UserID).add(2, _order_id).add(3, index);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_qm_ipqc_patrol_record_editor.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_qm_ipqc_patrol_record_editor.this.getContext(), result.Error);
                    return;
                }

                DataRow row = result.Value;
                if (row == null) {
                    App.Current.showError(pn_qm_ipqc_patrol_record_editor.this.getContext(), "找不到巡检任务。");
                    pn_qm_ipqc_patrol_record_editor.this.clearAll();
                    pn_qm_ipqc_patrol_record_editor.this.Header.setTitleText("工单巡检(无巡检任务)");
                    return;
                }

                _order_row = row;
                line_id = _order_row.getValue("line_id", 0L);
                _total = row.getValue("total", Integer.class);
                _rownum = row.getValue("rownum", Long.class);
                standpic_flag = row.getValue("standpic_flag", 0);
                if (standpic_flag == 1) {
                    pn_qm_ipqc_patrol_record_editor.this.txt_line4.Button.setVisibility(View.VISIBLE);
                } else {
                    pn_qm_ipqc_patrol_record_editor.this.txt_line4.Button.setVisibility(GONE);
                }
                if (_total > 0) {
                    pn_qm_ipqc_patrol_record_editor.this.Header.setTitleText("工单巡检(" + String.valueOf(_total) + "条巡检任务)");
                } else {
                    pn_qm_ipqc_patrol_record_editor.this.Header.setTitleText("工单巡检(无巡检任务)");
                }

                pn_qm_ipqc_patrol_record_editor.this.txt_code.setTag(row);

                String code = row.getValue("code", "") + ",第" + String.valueOf(_rownum) + "条巡检";
                pn_qm_ipqc_patrol_record_editor.this.txt_code.setContentText(code);
                pn_qm_ipqc_patrol_record_editor.this.txt_line1.setContentText(row.getValue("task_order_code", "") + "," + row.getValue("work_line", ""));
                pn_qm_ipqc_patrol_record_editor.this.txt_line2.setContentText(row.getValue("item_name", ""));
                pn_qm_ipqc_patrol_record_editor.this.txt_line3.setContentText(row.getValue("project_name", ""));
                pn_qm_ipqc_patrol_record_editor.this.txt_line4.setContentText(row.getValue("standards", ""));
                pn_qm_ipqc_patrol_record_editor.this.txt_line5.setContentText(row.getValue("tools_name", "") + "," + row.getValue("products_processing", ""));
                pn_qm_ipqc_patrol_record_editor.this.textCellCheck.setContentText(row.getValue("record", ""));

            }
        });
    }

    public void CheckItemNo(String task_order_code, long task_order_id) {
        this.ProgressDialog.show();

        String sql = "exec p_qm_ipqc_patrol_get_itemno ?,?";
        Parameters p = new Parameters().add(1, task_order_code).add(2, task_order_id);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_qm_ipqc_patrol_record_editor.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_qm_ipqc_patrol_record_editor.this.getContext(), result.Error);
                    return;
                }

                DataRow row = result.Value;
                if (row == null) {
                    App.Current.showError(pn_qm_ipqc_patrol_record_editor.this.getContext(), "找不到料号。");
                    return;
                }
                String result_value = row.getValue("result_value", "");
                String item_no = row.getValue("item_no", "");
                if (result_value.equals("NG")) {
                    App.Current.showError(pn_qm_ipqc_patrol_record_editor.this.getContext(), "SMD组件不存在此工单中。");
                    App.Current.playSound(R.raw.hook);

                }

                pn_qm_ipqc_patrol_record_editor.this.txt_line8.setContentText(item_no);
                pn_qm_ipqc_patrol_record_editor.this.txt_line9.setContentText(result_value);


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

    public void loadstandpic(long line_id) {
        this.ProgressDialog.show();
        String sql = "exec p_mm_get_ipqc_patrol_image ?";
        final Parameters p = new Parameters().add(1, line_id);
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_qm_ipqc_patrol_record_editor.this.ProgressDialog.dismiss();

                Result<DataTable> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_qm_ipqc_patrol_record_editor.this.getContext(), result.Error);
                    return;
                }
                DataTable tab = result.Value;
                ArrayList<byte[]> arrayList = new ArrayList<byte[]>();
                for (DataRow rw : tab.Rows) {
                    byte[] xx = rw.getValue("image1", byte[].class);
                    arrayList.add(xx);
                }
                if (arrayList.size() > 0) {
                    View view = View.inflate(getContext(), R.layout.patrol_alert_dialog, null);
                    ViewPagerFixed viewPager = (ViewPagerFixed) view.findViewById(R.id.viewpager);
                    viewPager.setAdapter(new MyVPAdapter(arrayList));
                    AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.show();
                    Window window = dialog.getWindow();
                    window.getDecorView().setPadding(0, 0, 0, 0);
                    window.setGravity(Gravity.CENTER);
                    window.setContentView(view);
                    WindowManager.LayoutParams lp = window.getAttributes();
                    lp.width = WindowManager.LayoutParams.FILL_PARENT;
                    lp.height = WindowManager.LayoutParams.FILL_PARENT;
                    window.setAttributes(lp);
                } else {
                    Toast.makeText(getContext(), "没有参考图片", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class MyVPAdapter extends PagerAdapter {
        private ArrayList<byte[]> arrayList;

        public MyVPAdapter(ArrayList<byte[]> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView view = new PhotoView(getContext());
            view.setImageBitmap(getBitmapFromByte(arrayList.get(position)));
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            ImageView imageView = (ImageView) object;
            container.removeView(imageView);
        }
    }

    public Bitmap getBitmapFromByte(byte[] temp) {
        if (temp != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        } else {
            return null;
        }
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

        final String check_value = this.txt_line8.getContentText();

        App.Current.question(this.getContext(), "确定要提交吗？", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                for (int j = 0; j < Bimp.tempSelectBitmap.size(); j++) {
                    int size = getBitmapSize(Bimp.tempSelectBitmap.get(j).getBitmap());
                    String filename = Bimp.tempSelectBitmap.get(j).imagePath;
                    File file = new File(filename);
                    String imagePath = Bimp.tempSelectBitmap.get(j).getImagePath();
                    Toast.makeText(getContext(), imagePath, Toast.LENGTH_SHORT).show();
                    try {
//						new FileInputStream(new File(imagePath));
//						InputStream str = Bitmap2IS(Bimp.tempSelectBitmap.get(j).getBitmap());
                        FileInputStream str = new FileInputStream(filename);
                        String isql = "insert into core_attachment (owner,[group],name,size,data,createtime) select ?,?,?,?,?,getdate()";
                        Connection conn2 = App.Current.DbPortal.CreateConnection(pn_qm_ipqc_patrol_record_editor.this.Connector);
                        PreparedStatement ps;
                        try {
                            ps = conn2.prepareStatement(isql);
                            ps.setString(1, "qm_ipqc_patrol_record_editor_" + String.valueOf(_order_row.getValue("line_id", 0L)));
                            ps.setString(2, "qm_ipqc_patrol_record_editor");
                            ps.setString(3, "图片" + String.valueOf(j));
                            ps.setString(4, String.valueOf(size));
                            ps.setBinaryStream(5, str, str.available());
                            ps.execute();

                        } catch (SQLException e1) {
                            App.Current.showInfo(pn_qm_ipqc_patrol_record_editor.this.getContext(), e1.getMessage());
                            e1.printStackTrace();
                            return;
                        }
                    } catch (Exception e) {
                        App.Current.showError(pn_qm_ipqc_patrol_record_editor.this.getContext(), "转换失败" + e.getMessage());
                        e.printStackTrace();
                        return;
                    }

                }
                Map<String, String> entry = new HashMap<String, String>();
                entry.put("code", _order_row.getValue("code", ""));
                entry.put("create_user", App.Current.UserID);
                entry.put("head_id", String.valueOf(_order_row.getValue("id", 0L)));
                entry.put("line_id", String.valueOf(_order_row.getValue("line_id", 0)));
                entry.put("lot_number", lot_number);
                entry.put("check_value", check_value);
                entry.put("result_value", result_value);

                ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
                entries.add(entry);

                //生成XML数据，并传给存储过程
                String xml = XmlHelper.createXml("patrol_record", null, null, "patrol_record", entries);
                String sql = "exec p_qm_ipqc_patrol_record_create ?,?";
                Connection conn = App.Current.DbPortal.CreateConnection(pn_qm_ipqc_patrol_record_editor.this.Connector);
                CallableStatement stmt;
                try {
                    stmt = conn.prepareCall(sql);
                    stmt.setObject(1, xml);
                    stmt.registerOutParameter(2, Types.VARCHAR);
                    stmt.execute();

                    String val = stmt.getString(2);
                    if (val != null) {
                        Result<String> rs = XmlHelper.parseResult(val);
                        if (rs.HasError) {
                            App.Current.showError(pn_qm_ipqc_patrol_record_editor.this.getContext(), rs.Error);
                            return;
                        }
                        App.Current.toastInfo(pn_qm_ipqc_patrol_record_editor.this.getContext(), "提交成功");
                        _order_row = null;
                        pn_qm_ipqc_patrol_record_editor.this.clear();
                        pn_qm_ipqc_patrol_record_editor.this.loadItem(_rownum);
                    }
                } catch (SQLException e) {
                    App.Current.showInfo(pn_qm_ipqc_patrol_record_editor.this.getContext(), e.getMessage());
                    e.printStackTrace();

                    pn_qm_ipqc_patrol_record_editor.this.clear();
                    pn_qm_ipqc_patrol_record_editor.this.loadItem(_rownum);
                }
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
        this.txt_line8.setContentText("");
        this.txt_line9.setContentText("");
        this.txt_line6.setContentText("");
        Bimp.tempSelectBitmap.clear();
        Bimp.max = 0;
        adapter.notifyDataSetChanged();
        //this.Init();
    }

    public void clearAll() {
        this.txt_line8.setContentText("");
        this.txt_line9.setContentText("");
        this.txt_line6.setContentText("");
        this.txt_line1.setContentText("");
        this.txt_line2.setContentText("");
        this.txt_line3.setContentText("");
        this.txt_line4.setContentText("");
        this.txt_line5.setContentText("");
        this.txt_code.setContentText("");
    }

    public void loadSelectValue(String lookup_type, final ButtonTextCell txt) {

        String sql = "SELECT lookup_code FROM core_data_keyword where lookup_type =?";
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
            new AlertDialog.Builder(pn_qm_ipqc_patrol_record_editor.this.getContext()).setTitle("请选择")
                    .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf(txt.getContentText().toString()), listener)
                    .setNegativeButton("取消", null).show();
        }
    }
}
