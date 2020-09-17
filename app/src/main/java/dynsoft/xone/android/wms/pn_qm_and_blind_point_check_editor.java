package dynsoft.xone.android.wms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dynsoft.xone.android.activity.AlbumActivity;
import dynsoft.xone.android.activity.GalleryActivity;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.util.Bimp;
import dynsoft.xone.android.util.Res;

/**
 * Created by Administrator on 2018/6/12.
 */

public class pn_qm_and_blind_point_check_editor extends pn_editor {
    private View view;
    private TextCell text_cell_1;
    private ButtonTextCell button_text_cell_2;
    private TextCell text_cell_3;
    private ButtonTextCell button_text_cell_4;
    private ButtonTextCell button_text_cell_5;
    private ButtonTextCell text_cell_6;
    private TextCell text_cell_7;
    private TextCell text_cell_8;
    private ButtonTextCell text_cell_9;
    private DataRow _order_row;


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;

    private Integer id;

    public ImageButton btn_prev;
    public ImageButton btn_next;
    private GridView noScrollgridview;
    private View parentView;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    public static Bitmap bimap;
    private GridAdapter adapter;

    public pn_qm_and_blind_point_check_editor(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        sharedPreferences = App.Current.Workbench.getSharedPreferences("ipqc_point_check", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();

        LayoutParams lp = new LayoutParams(-1, -1);
        view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_win_blind_point_check_editor, this, true);
        Log.e(getContext().getPackageName(), "setContentView()");
        view.setLayoutParams(lp);
        //noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();

        text_cell_1 = (TextCell) findViewById(R.id.text_cell_1);
        button_text_cell_2 = (ButtonTextCell) findViewById(R.id.button_text_cell_2);
        text_cell_3 = (TextCell) findViewById(R.id.text_cell_3);
        button_text_cell_4 = (ButtonTextCell) findViewById(R.id.button_text_cell_4);
        button_text_cell_5 = (ButtonTextCell) findViewById(R.id.button_text_cell_5);
        text_cell_6 = (ButtonTextCell) findViewById(R.id.text_cell_6);
        text_cell_7 = (TextCell) findViewById(R.id.text_cell_7);
        text_cell_8 = (TextCell) findViewById(R.id.text_cell_8);
        text_cell_9 = (ButtonTextCell) findViewById(R.id.button_text_cell_9);

        id = this.Parameters.get("id", 0);
        //任务单号
        String code = this.Parameters.get("code", "");
        String item_code = this.Parameters.get("item_code", "");


        if (text_cell_1 != null) {
            text_cell_1.setLabelText("点检编号");
            text_cell_1.setReadOnly();
        }

        if (button_text_cell_2 != null) {
            button_text_cell_2.setLabelText("点检时间");
            button_text_cell_2.setReadOnly();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
            String chooseDate = simpleDateFormat.format(new Date());
            button_text_cell_2.setContentText(chooseDate);
//            button_text_cell_2.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_gray"));
//            button_text_cell_2.Button.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    TimePickerView timePickerView = new TimePickerView(getContext(), TimePickerView.Type.YEAR_MONTH_DAY);
//                    timePickerView.setCyclic(true);
//                    timePickerView.setTime(new Date());//获取当前时间
////                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
////                    Date format = null;
////                    try {
////                        format = simpleDateFormat.parse(button_text_cell_2.getContentText());
////                    } catch (ParseException e) {
////                        e.printStackTrace();
////                    }
////                    timePickerView.setTime(format);
//
//                    timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
//                        @Override
//                        public void onTimeSelect(Date date) {
//
//                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
//                            String chooseDate = simpleDateFormat.format(date);
//                            button_text_cell_2.setContentText(chooseDate);
//                        }
//                    });
//                    timePickerView.show();
//                }
//            });
        }


        if (text_cell_3 != null) {
            text_cell_3.setLabelText("点检员");
            text_cell_3.setContentText(App.Current.UserCode);
            text_cell_3.setReadOnly();
        }


        if (button_text_cell_4 != null) {
            button_text_cell_4.setLabelText("生产任务");
            button_text_cell_4.setReadOnly();
            button_text_cell_4.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_gray"));
            button_text_cell_4.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadComfirmName(button_text_cell_4);
                }
            });
            if (!TextUtils.isEmpty(code)) {
                button_text_cell_4.setContentText(code);
            } else {
                code = sharedPreferences.getString("code", "");
                button_text_cell_4.setContentText(code);
            }
        }

        if (button_text_cell_5 != null) {
            button_text_cell_5.setLabelText("线体");
            button_text_cell_5.setReadOnly();
            button_text_cell_5.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_gray"));
            button_text_cell_5.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadWorkLineName(button_text_cell_5);
                }
            });
        }

        if (text_cell_6 != null) {
            text_cell_6.setLabelText("整机料号");
            text_cell_6.setContentText(item_code);
            this.text_cell_6.Button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    App.Current.Workbench.scanByCamera();
                }

            });
            if (!TextUtils.isEmpty(item_code)) {
                text_cell_6.setContentText(item_code);
            } else {
                item_code = sharedPreferences.getString("item_code", "");
                text_cell_6.setContentText(item_code);
            }
        }

        if (text_cell_7 != null) {
            text_cell_7.setLabelText("盲点问题点");
        }
        if (text_cell_8 != null) {
            text_cell_8.setLabelText("处理结果");
        }
        if (text_cell_9 != null) {
            text_cell_9.setLabelText("是否通过");
            setclicklisten(text_cell_9);
        }


        Res.init(this.getContext());

        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.icon_addpic_unfocused);
        parentView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_ipqc_patrol_record_editor, this, true);
        Init();

        Log.e("len", "ID: " + id);
        loadItem(id);
    }

    private void setclicklisten(ButtonTextCell buttontextcell) {
        buttontextcell.setReadOnly();
        buttontextcell.Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseOkAndNg(buttontextcell);
            }
        });
    }

    private void chooseOkAndNg(final ButtonTextCell buttonTextCell) {
        final String[] chooseItems = {"PASS", "FAIL"};
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("请选择")
                .setSingleChoiceItems(chooseItems, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buttonTextCell.setContentText(chooseItems[i]);
                        dialogInterface.dismiss();
                    }
                }).create();
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    public void Init() {

        pop = new PopupWindow(pn_qm_and_blind_point_check_editor.this.getContext());

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
                Intent intent = new Intent(pn_qm_and_blind_point_check_editor.this.getContext(), AlbumActivity.class);
                intent.putExtra("order_id", id);
                intent.putExtra("index", 1);
                pn_qm_and_blind_point_check_editor.this.getContext().startActivity(intent);
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
        adapter = new GridAdapter(pn_qm_and_blind_point_check_editor.this.getContext());

        if (Bimp.tempSelectBitmap.size() > 0) {
            adapter.update();
        }
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    Log.i("ddddddd", "----------");
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(pn_qm_and_blind_point_check_editor.this.getContext(), R.anim.activity_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(pn_qm_and_blind_point_check_editor.this.getContext(),
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

    //    private void chooseResult(final ButtonTextCell button_text_cell_4) {
//        final ArrayList<String> result = new ArrayList<String>();
//        result.add("A");
//        result.add("B");
//        result.add("C");
//
//        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (which >= 0) {
//                    String time = result.get(which);
//                    button_text_cell_4.setContentText(time);
//                }
//                dialog.dismiss();
//            }
//        };
//        new AlertDialog.Builder(pn_qm_win_ipqc_point_check_editor.this.getContext()).setTitle("请选择")
//                .setSingleChoiceItems(result.toArray(new String[0]), result.indexOf(button_text_cell_4.getContentText()), listener)
//                .setNegativeButton("取消", null).show();
//    }
    @Override
    public void onRefrsh() {
        Init();
    }

    private void loadComfirmName(final ButtonTextCell button_text_cell_4) {
        Link link = new Link("pane://x:code=pn_qm_and_blind_point_check_record_parameter_mgr");
//        link.Parameters.add("textcell", textcell_1);
        link.Open(null, getContext(), null);
        this.close();
    }

    private void loadWorkLineName(final ButtonTextCell text) {
        String sql = "exec p_qm_sop_work_line_items ?,?";
        Parameters p = new Parameters().add(1, App.Current.UserID).add(2, button_text_cell_4.getContentText().replace("\n", ""));
        final Result<DataTable> result = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (result.HasError) {
            App.Current.showError(this.getContext(), result.Error);
            return;
        }
        if (result.Value != null) {
            if (result.Value.Rows.size() == 1) {
                DataRow row = result.Value.Rows.get(0);
                button_text_cell_5.setContentText(row.getValue("name", ""));
                edit.putInt("work_line_id", row.getValue("id", 0));
                edit.commit();
            } else {
                ArrayList<String> names = new ArrayList<String>();
                for (DataRow row : result.Value.Rows) {
                    StringBuffer name = new StringBuffer();
                    name.append(row.getValue("name", ""));
                    names.add(name.toString());
                }

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which >= 0) {
                            DataRow row = result.Value.Rows.get(which);
                            StringBuffer name = new StringBuffer();
                            name.append(row.getValue("name", ""));
                            text.setContentText(name.toString());
                            button_text_cell_5.setContentText(row.getValue("name", ""));
                            edit.putInt("work_line_id", row.getValue("id", 0));
                            edit.commit();
                        }
                        dialog.dismiss();
                    }
                };
                new AlertDialog.Builder(pn_qm_and_blind_point_check_editor.this.getContext()).setTitle("请选择")
                        .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                                (text.getContentText().toString()), listener)
                        .setNegativeButton("取消", null).show();
            }
        } else {
            App.Current.toastInfo(getContext(), "工单有误！");
        }
    }

    private void loadItem(Integer id) {
        this.ProgressDialog.show();

        String sql = "exec get_qm_blind_point_check_editor ?";
        Parameters p = new Parameters().add(1, id);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {

            @Override
            public void handleMessage(Message msg) {
                pn_qm_and_blind_point_check_editor.this.ProgressDialog.dismiss();
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    return;
                }
                _order_row = value.Value;
                if (value.Value != null) {
                    String code = value.Value.getValue("code", "");
                    String checker = value.Value.getValue("checker", "");
                    String line_name = value.Value.getValue("line_name", "");
                    String task_code = value.Value.getValue("task_code", "");

                    String checked_equip = value.Value.getValue("checked_equip", "");
                    String n_standard = value.Value.getValue("n_standard", "");
                    String n_actual = value.Value.getValue("n_actual", "");
                    String problem = value.Value.getValue("problem", "");
                    String processed_results = value.Value.getValue("processed_results", "");
                    String result = value.Value.getValue("result", "");


                    Date check_date = value.Value.getValue("check_date", new Date());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String format_date = simpleDateFormat.format(check_date);

                    text_cell_1.setContentText(code);
                    button_text_cell_2.setContentText(format_date);
                    text_cell_3.setContentText(checker);
                    button_text_cell_4.setContentText(task_code);
                    button_text_cell_5.setContentText(line_name);
                    text_cell_6.setContentText(checked_equip);
                    text_cell_7.setContentText(problem);
                    text_cell_8.setContentText(processed_results);
                    text_cell_9.setContentText(result);
                }
//                else {
//                    close();
//                }
            }
        });
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
        super.commit();


        if (TextUtils.isEmpty(button_text_cell_5.getContentText())) {
            App.Current.toastInfo(getContext(), "请选择线体！");
            App.Current.playSound(R.raw.error);
        } else if (TextUtils.isEmpty(button_text_cell_2.getContentText())) {
            App.Current.toastInfo(getContext(), "请选择检验日期！");
            App.Current.playSound(R.raw.error);
        } else if (TextUtils.isEmpty(button_text_cell_4.getContentText())) {
            App.Current.toastInfo(getContext(), "请选择生产任务！");
            App.Current.playSound(R.raw.error);
        } else if (TextUtils.isEmpty(text_cell_6.getContentText())) {
            App.Current.toastInfo(getContext(), "请输入设备编号！");
            App.Current.playSound(R.raw.error);
        } else if (TextUtils.isEmpty(text_cell_7.getContentText())) {
            App.Current.toastInfo(getContext(), "请输入盲点问题点");
            App.Current.playSound(R.raw.error);
        } else if (TextUtils.isEmpty(text_cell_8.getContentText())) {
            App.Current.toastInfo(getContext(), "请输入处理结果");
            App.Current.playSound(R.raw.error);
        } else if (TextUtils.isEmpty(text_cell_9.getContentText())) {
            App.Current.toastInfo(getContext(), "请选择是否通过");
            App.Current.playSound(R.raw.error);
        } else {
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
                            Connection conn2 = App.Current.DbPortal.CreateConnection(pn_qm_and_blind_point_check_editor.this.Connector);
                            PreparedStatement ps;
                            try {
                                ps = conn2.prepareStatement(isql);
                                ps.setString(1, "pn_qm_and_blind_point_check_editor_" + sharedPreferences.getString("code", ""));
                                ps.setString(2, "pn_qm_and_blind_point_check_editor");
                                ps.setString(3, "图片" + String.valueOf(j));
                                ps.setString(4, String.valueOf(size));
                                ps.setBinaryStream(5, str, str.available());
                                ps.execute();

                            } catch (SQLException e1) {
                                App.Current.showInfo(pn_qm_and_blind_point_check_editor.this.getContext(), e1.getMessage());
                                e1.printStackTrace();
                                return;
                            }
                        } catch (Exception e) {
                            App.Current.showError(pn_qm_and_blind_point_check_editor.this.getContext(), "转换失败" + e.getMessage());
                            e.printStackTrace();
                            return;
                        }

                    }
                    String sql = "exec p_qm_and_blind_point_check_create ?,?,?,?,?,?,?,?,?";
                    Parameters p = new Parameters().add(1, button_text_cell_2.getContentText()).add(2, text_cell_3.getContentText()).add(3, button_text_cell_4.getContentText())
                            .add(4, button_text_cell_5.getContentText()).add(5, text_cell_6.getContentText())
                            .add(6, text_cell_7.getContentText()).add(7, text_cell_8.getContentText()).add(8, text_cell_9.getContentText())
                            .add(9, id);
                    App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                        @Override
                        public void handleMessage(Message msg) {
                            Result<Integer> value = Value;
                            if (value.HasError) {
                                App.Current.showError(getContext(), value.Error);
                                return;
                            }
                            if (value.Value != null) {
                                int value1 = value.Value;
                                if (value1 > 0) {
                                    App.Current.toastInfo(getContext(), "提交成功");
                                    App.Current.playSound(R.raw.pass);
                                    Log.e("len", "ID: " + id);
                                    if (id != 0) {
                                        loadItem(id);
                                    } else {
                                        close();
                                    }

                                } else {
                                    App.Current.toastInfo(getContext(), "提交失败1");
                                    App.Current.playSound(R.raw.error);
                                }
                            } else {
                                App.Current.toastInfo(getContext(), "提交失败2");
                                App.Current.playSound(R.raw.error);
                            }
                        }
                    });
                }
            });
        }
    }

    public boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
