package dynsoft.xone.android.wms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
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

import com.bumptech.glide.load.resource.transcode.BitmapBytesTranscoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.activity.AlbumGetPicActivity;
import dynsoft.xone.android.activity.FirstItemActivity;
import dynsoft.xone.android.activity.FirstItemNewActivity;
import dynsoft.xone.android.activity.FirstItemTestActivity;
import dynsoft.xone.android.activity.GalleryGetPicActivity;
import dynsoft.xone.android.adapter.GradviewImageAdapter;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.util.Bimp;
import dynsoft.xone.android.util.ImageItem;
import oracle.jdbc.driver.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;

public class pn_qm_ipqc_first_sure_editor extends pn_editor {
    private ButtonTextCell buttonTextCell1;
    private ButtonTextCell textCell1;
    private TextCell textCell2;
    private TextCell textCell3;
    private TextCell textCell4;
    private TextCell textCell5;
    private TextCell textCell6;
    private TextCell textCell7;
    private TextCell textCell8;
    private ImageButton imageLoadItem;
    private ImageButton imageFlow;
    private GridView noScrollgridview;
    private ArrayList<byte[]> imageDatas;
    private GradviewImageAdapter gradviewImageAdapter;

    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    public static Bitmap bimap;
    private GridAdapter adapter;
    private int id;
    private int counts;
    private int sequence_id;
    private String work_stage;

    public pn_qm_ipqc_first_sure_editor(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        super.setContentView();
        LayoutParams layoutParams = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_ipqc_first_sure_editor, this, true);
        view.setLayoutParams(layoutParams);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();

        imageDatas = new ArrayList<>();
        buttonTextCell1 = (ButtonTextCell) findViewById(R.id.btc_confirm_code);
        textCell1 = (ButtonTextCell) findViewById(R.id.tc_check_code);
        textCell2 = (TextCell) findViewById(R.id.tc_task_order);
        textCell3 = (TextCell) findViewById(R.id.tc_plan_quantity);
        textCell4 = (TextCell) findViewById(R.id.tc_item_code);
        textCell5 = (TextCell) findViewById(R.id.tc_item_name);
        textCell6 = (TextCell) findViewById(R.id.tc_cust_sn);
        textCell7 = (TextCell) findViewById(R.id.tc_segment);
        textCell8 = (TextCell) findViewById(R.id.tc_sn);
        imageLoadItem = (ImageButton) findViewById(R.id.btn_load_item);     //载入明细
        imageFlow = (ImageButton) findViewById(R.id.btn_flow);               //发起流程
        noScrollgridview = (GridView) findViewById(R.id.noscrollgridview);

        id = this.Parameters.get("id", 0);
        counts = this.Parameters.get("counts", 0);
        String sn = this.Parameters.get("sn", "");
        String code = this.Parameters.get("code", "");
        String check_code = this.Parameters.get("check_code", "");
        final String task_code = this.Parameters.get("task_code", "");
        String item_code = this.Parameters.get("item_code", "");
        String item_name = this.Parameters.get("item_name", "");
        final String org_code = this.Parameters.get("org_code", "");
        int plan_quantity = this.Parameters.get("plan_quantity", new BigDecimal(0)).intValue();

        if (imageLoadItem != null) {
            imageLoadItem.setImageBitmap(App.Current.ResourceManager.getImage("@/core_acl_white"));
//            imageLoadItem.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    loadItem();
//                }
//            });
            imageLoadItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_qm_ipqc_first_sure_editor.this.ProgressDialog.show();
                    if (id > 0) {
                        //加载首件确认的item，判断 如果当前的item为空，复制审料的数据过去
                        String sql = "exec fm_load_first_confirm_item_by_check2 ?,?";
                        Parameters p = new Parameters().add(1, id).add(2, textCell4.getContentText());   //第二个参数是物料编码，是为了获取物料ID
                        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
                            @Override
                            public void handleMessage(Message msg) {
                                pn_qm_ipqc_first_sure_editor.this.ProgressDialog.dismiss();
                                Result<DataRow> value = Value;
                                if (value.HasError) {
                                    App.Current.toastError(getContext(), value.Error);
                                } else if (value.Value != null) {
                                    int org_id = value.Value.getValue("org_id", 0);
                                    int counts = value.Value.getValue("counts", 0);
                                    long item_id = value.Value.getValue("item_id", 0L);
                                    if (counts < 1) {
                                        //没有首件的item表数据，从Oracle的bom里面获取
                                        loadItemFromOracle(org_id, item_id);
                                    } else {
                                        Intent intent = new Intent(App.Current.Workbench, FirstItemActivity.class);
                                        intent.putExtra("id", id);
                                        intent.putExtra("counts", counts);
                                        intent.putExtra("org_id", org_id);
                                        intent.putExtra("item_id", item_id);
                                        intent.putExtra("task_order_code", textCell2.getContentText());
                                        intent.putExtra("item_name", textCell5.getContentText());
                                        intent.putExtra("plan_quantity", textCell3.getContentText());
                                        App.Current.Workbench.startActivity(intent);
                                    }
//                                    Intent intent = new Intent(App.Current.Workbench, FirstItemActivity.class);
//                                    Log.e("len", id + "***" + org_id);
//                                    intent.putExtra("id", id);
//                                    intent.putExtra("counts", counts);
//                                    intent.putExtra("org_id", org_id);
//                                    intent.putExtra("item_id", item_id);
//                                    intent.putExtra("task_order_code", textCell2.getContentText());
//                                    intent.putExtra("item_name", textCell5.getContentText());
//                                    intent.putExtra("plan_quantity", textCell3.getContentText());
//                                    App.Current.Workbench.startActivity(intent);
                                } else {

                                }
                            }
                        });
                    } else {
                        Intent intent = new Intent(App.Current.Workbench, FirstItemNewActivity.class);
                        intent.putExtra("check_code", buttonTextCell1.getContentText());
                        App.Current.Workbench.startActivity(intent);
//                        Intent intent = new Intent(App.Current.Workbench, QbWebActivity.class);
//                        intent.putExtra("check_code", buttonTextCell1.getContentText());
//                        App.Current.Workbench.startActivity(intent);
                    }
                }
            });
        }

        if (imageFlow != null) {
            imageFlow.setImageBitmap(App.Current.ResourceManager.getImage("@/core_start_flow_white"));
//            imageLoadItem.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    loadItem();
//                }
//            });
            imageFlow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startFlow();
                }
            });
        }

        if (buttonTextCell1 != null) {
            buttonTextCell1.setLabelText("审料单号");
            buttonTextCell1.setReadOnly();
            buttonTextCell1.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Link link = new Link("pane://x:code=pn_first_item_sure_parameter_mgr");
                    link.Open(pn_qm_ipqc_first_sure_editor.this, pn_qm_ipqc_first_sure_editor.this.getContext(), null);
                    close();
                }
            });
        }

        if (textCell1 != null) {
            textCell1.setLabelText("确认单编码");
            textCell1.setReadOnly();
            textCell1.setButtonImage(App.Current.ResourceManager.getImage("@/core_right_light"));
            textCell1.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), FirstItemTestActivity.class);
                    intent.putExtra("first_code", textCell1.getContentText());
                    intent.putExtra("task_order_code", textCell2.getContentText());
                    intent.putExtra("item_code", textCell4.getContentText());
                    intent.putExtra("item_name", textCell5.getContentText());
                    intent.putExtra("work_stage", work_stage);
                    intent.putExtra("sequence_id", sequence_id);
                    App.Current.Workbench.startActivity(intent);
                }
            });
        }

        if (textCell2 != null) {
            textCell2.setLabelText("生产任务");
            textCell2.setReadOnly();
        }

        if (textCell3 != null) {
            textCell3.setLabelText("批量");
            textCell3.setReadOnly();
        }

        if (textCell4 != null) {
            textCell4.setLabelText("物料编码");
            textCell4.setReadOnly();
        }

        if (textCell5 != null) {
            textCell5.setLabelText("机型名称");
        }

        if (textCell6 != null) {
            textCell6.setLabelText("段别");
            textCell6.setReadOnly();
        }

        if (textCell7 != null) {
            textCell7.setLabelText("客户");
            textCell7.setReadOnly();
        }

        if (textCell8 != null) {
            textCell8.setLabelText("SN");
            textCell8.setReadOnly();
        }

        buttonTextCell1.setContentText(code);
        textCell2.setContentText(task_code);
        textCell3.setContentText(String.valueOf(plan_quantity));
        textCell4.setContentText(item_code);
        textCell5.setContentText(item_name);
        textCell8.setContentText(sn);

        if (id > 0) {                                //通过ipqc首件表的id获取数据
            String sql = "exec fm_get_ipqc_first_sure_by_head_id ?";
            Parameters p = new Parameters().add(1, id);
            App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
                @Override
                public void handleMessage(Message msg) {
                    Result<DataRow> value = Value;
                    if (value.HasError) {
                        App.Current.toastError(getContext(), value.Error);
                    } else {
                        if (value.Value != null) {
                            String code = value.Value.getValue("code", "");  //确认单编码
                            String check_code = value.Value.getValue("check_code", "");  //审料单号
                            String task_code = value.Value.getValue("task_code", "");  //生产任务
                            String item_code = value.Value.getValue("item_code", "");  //物料编码
                            String item_name = value.Value.getValue("item_name", "");  //机型名称
                            //段别
                            work_stage = value.Value.getValue("work_stage", "");
                            sequence_id = value.Value.getValue("sequence_id", 0);
                            String cust_sn = value.Value.getValue("cust_sn", "");  //客户
                            int plan_quantity = value.Value.getValue("plan_quantity", new BigDecimal(0)).intValue();
                            byte[] image1 = value.Value.getValue("sn_img1", new byte[0]);
                            byte[] image2 = value.Value.getValue("sn_img2", new byte[0]);
                            buttonTextCell1.setContentText(check_code);
                            textCell1.setContentText(code);
                            textCell2.setContentText(task_code);
                            textCell3.setContentText(String.valueOf(plan_quantity));
                            textCell4.setContentText(item_code);
                            textCell5.setContentText(item_name);
                            textCell6.setContentText(work_stage);
                            textCell7.setContentText(cust_sn);
                            if (image1.length > 2) {
                                ImageItem imageItem = new ImageItem();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(image1, 0, image1.length);
                                imageItem.setBitmap(bitmap);
                                Bimp.tempSelectBitmap.add(imageItem);
//                                imageDatas.add(image1);
                            }
                            if (image2.length > 2) {
                                ImageItem imageItem = new ImageItem();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(image2, 0, image2.length);
                                imageItem.setBitmap(bitmap);
                                Bimp.tempSelectBitmap.add(imageItem);
//                                imageDatas.add(image2);
                            }
                            adapter.notifyDataSetChanged();
//                            Drawable drawable = App.Current.Workbench.getResources().getDrawable(R.drawable.icon_addpic_unfocused);
////                            if(imageDatas.size() < 1) {
////                                Drawable drawable = App.Current.Workbench.getResources().getDrawable(R.drawable.icon_addpic_unfocused);
////                                byte[] bytes = drawableToByte(drawable);
////                                imageDatas.add(bytes);
////                            }
//                            gradviewImageAdapter = new GradviewImageAdapter(imageDatas, pn_qm_ipqc_first_sure_editor.this.getContext());
//                            noScrollgridview.setNumColumns(2);
//                            noScrollgridview.setAdapter(gradviewImageAdapter);
                        }
                    }

                }
            });
        }
        init();
    }

    @Override
    public void close() {
        super.close();
        Bimp.tempSelectBitmap.removeAll(Bimp.tempSelectBitmap);
    }

    private void loadItemFromOracle(final int org_id, final long item_id) {
        Connection conn = null;
        CallableStatement stmt;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.21:1609:PROD1", "apps", "apps");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String sql = "{call pkg_getrecord.p_rtn_bomdtlpz(?,?,?,?)}";
            stmt = conn.prepareCall(sql);
            stmt.setInt(1, (int) item_id);
            stmt.setInt(2, org_id);
            stmt.setInt(3, 30);

            stmt.registerOutParameter(4, OracleTypes.CURSOR);
            stmt.execute();
            ResultSet cursor = ((OracleCallableStatement) stmt).getCursor(4);
            ResultSetMetaData metaData = cursor.getMetaData();
            int columnCount = metaData.getColumnCount();
            Map<String, String> headMap = new HashMap<>();
            headMap.put("task_order_code", textCell2.getContentText().trim());
            headMap.put("first_item_code", textCell1.getContentText().trim());
            ArrayList<Map<String, String>> itemArray = new ArrayList<>();
            ;
            while (cursor.next()) {
                Map<String, String> item = new HashMap<>();
                String item_code = cursor.getString("ITEM_CODE");
                String item_name = cursor.getString("ITEM_NAME");
                String bom_purpose = cursor.getString("BOM_PURPOSE");
                String sub_item = cursor.getString("SUB_ITEM");
                String ref_wh = cursor.getString("REF_WH");
                String change_notice = cursor.getString("CHANGE_NOTICE");
                String key_id = cursor.getString("KEYID");
                String parent_id = cursor.getString("PATENTID");
                String unid = cursor.getString("UNID");
                int component_quantity = cursor.getInt("COMPONENT_QUANTITY");
                item.put("item_code", item_code);
                item.put("item_name", item_name);
                item.put("bom_purpose", bom_purpose);
                item.put("sub_item", sub_item);
                item.put("ref_wh", ref_wh);
                item.put("change_notice", change_notice);
                item.put("unid", unid);
                item.put("key_id", key_id);
                item.put("parent_id", parent_id);
                item.put("component_quantity", String.valueOf(component_quantity));
                itemArray.add(item);
//                String item_name = cursor.getString("ITEM_NAME");
             }

            String xml = XmlHelper.createXml("root", headMap, "items", "item", itemArray);

            //把xml数据变成参数，带人存储过程，插入首件的item表
            String bomSql = "exec fm_insert_item_from_bom ?";
            Parameters bomP = new Parameters().add(1, xml);
            App.Current.DbPortal.ExecuteNonQueryAsync(pn_qm_ipqc_first_sure_editor.this.Connector, bomSql, bomP, new ResultHandler<Integer>() {
                @Override
                public void handleMessage(Message msg) {
                    Result<Integer> value = Value;
                    if (value.HasError) {
                        App.Current.toastError(getContext(), value.Error);
                    } else if (value.Value > 0) {
                        Intent intent = new Intent(App.Current.Workbench, FirstItemActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("counts", counts);
                        intent.putExtra("org_id", org_id);
                        intent.putExtra("item_id", item_id);
                        intent.putExtra("task_order_code", textCell2.getContentText());
                        intent.putExtra("item_name", textCell5.getContentText());
                        intent.putExtra("plan_quantity", textCell3.getContentText());
                        App.Current.Workbench.startActivity(intent);
                    } else {

                    }
                }
            });
//            int start = 0;
//            for (int i = 0; i < xml.length(); i++) {
//                if (i % 2000 == 0) {
//                    Log.e("len", "XML:" + xml.substring(start, i));
//                    start = i;
//                } else if (i % 2000 != 0 && i == xml.length() - 200) {
//                    Log.e("len", "XMLEND:" + xml.substring(i));
//                }
//            }
        } catch (SQLException e) {
            e.printStackTrace();
            App.Current.showInfo(getContext(), e.getMessage() + "111111");
        }
    }


    @Override
    public void commit() {
        super.commit();
//        for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
//            String imagePath = Bimp.tempSelectBitmap.get(i).imagePath;
//        }
        if (Bimp.tempSelectBitmap.size() == 2) {
            App.Current.question(App.Current.Workbench, "是否确定提交？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String sql = "exec fm_update_ipqc_confirm_pic2 ?,?,?,?";
                    Parameters p = new Parameters().add(1, textCell1.getContentText())
                            .add(2, getStringFromFile(Bimp.tempSelectBitmap.get(0).imagePath))
                            .add(3, getStringFromFile(Bimp.tempSelectBitmap.get(1).imagePath))
                            .add(4, App.Current.UserID);
                    App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                        @Override
                        public void handleMessage(Message msg) {
                            Result<Integer> value = Value;
                            if (value.HasError) {
                                App.Current.toastError(getContext(), value.Error);
                            } else {
//                        App.Current.toastInfo(getContext(), "提交成功！");
                                Bimp.tempSelectBitmap.removeAll(Bimp.tempSelectBitmap);
                                close();
//                                startFlow();    //发起流程
                            }
                        }
                    });
                }
            });
        } else {
            App.Current.toastError(getContext(), "请拍两张SN照片");
        }
    }

    private void startFlow() {
        Link link = new Link("pane://x:code=pn_first_item_sure_start_flow_editor");
        link.Parameters.put("id", id);
        link.Parameters.put("code", buttonTextCell1.getContentText());
        link.Parameters.put("first_code", textCell1.getContentText());
        link.Parameters.put("task_order_code", textCell2.getContentText());
        link.Parameters.put("plan_quantity", textCell3.getContentText());
        link.Parameters.put("item_code", textCell4.getContentText());
        link.Parameters.put("item_name", textCell5.getContentText());
        link.Open(pn_qm_ipqc_first_sure_editor.this, pn_qm_ipqc_first_sure_editor.this.getContext(), null);
        close();
//        final PopupWindow popupWindow = new PopupWindow();
//        View view = View.inflate(getContext(), R.layout.first_item_choose_next, null);
//        popupWindow.setContentView(view);
//        final ButtonTextCell buttonTextCellIe = (ButtonTextCell) view.findViewById(R.id.buttontextcell_ie);
//        final ButtonTextCell buttontextcellTe = (ButtonTextCell) view.findViewById(R.id.buttontextcell_te);
//        final ButtonTextCell buttonTextCellPqe = (ButtonTextCell) view.findViewById(R.id.buttontextcell_pqe);
//        final ButtonTextCell buttonTextCellRe = (ButtonTextCell) view.findViewById(R.id.buttontextcell_re);
//        TextView textViewConfirm = (TextView) view.findViewById(R.id.confirm);
//        TextView textViewCancel = (TextView) view.findViewById(R.id.cancel);
//
//        buttonTextCellIe.setLabelText("IE");
//        buttonTextCellIe.Button.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        buttonTextCellRe.setLabelText("RE");
//        buttonTextCellRe.Button.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        buttonTextCellPqe.setLabelText("PQE");
//        buttonTextCellPqe.Button.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        buttontextcellTe.setLabelText("TE");
//        buttonTextCellIe.Button.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        textViewConfirm.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(buttonTextCellIe.getContentText())) {
//                    App.Current.toastError(getContext(), "请先选择IE");
//                } else if (TextUtils.isEmpty(buttontextcellTe.getContentText())) {
//                    App.Current.toastError(getContext(), "请先选择TE");
//                } else if (TextUtils.isEmpty(buttonTextCellRe.getContentText())) {
//                    App.Current.toastError(getContext(), "请先选择RE");
//                } else if (TextUtils.isEmpty(buttonTextCellPqe.getContentText())) {
//                    App.Current.toastError(getContext(), "请先选择PQE");
//                } else {
//                    startFlow();
//                    popupWindow.dismiss();
//                }
//            }
//        });
//
//        textViewCancel.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//            }
//        });
    }


    private byte[] getStringFromFile(String fileUrl) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(fileUrl));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while (-1 != (len = fileInputStream.read(bytes))) {
                byteArrayOutputStream.write(bytes, 0, len);
            }
            byteArrayOutputStream.close();
            fileInputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onRefrsh() {
        super.onRefrsh();
        adapter.update();
    }

    public void init() {

        pop = new PopupWindow(pn_qm_ipqc_first_sure_editor.this.getContext());

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
                Intent intent = new Intent(pn_qm_ipqc_first_sure_editor.this.getContext(), AlbumGetPicActivity.class);
                intent.putExtra("id", id);
//                intent.putExtra("index", _rownum);
                pn_qm_ipqc_first_sure_editor.this.getContext().startActivity(intent);
                App.Current.Workbench.overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
                close();

            }
        });
        bt3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(pn_qm_ipqc_first_sure_editor.this.getContext());

        if (Bimp.tempSelectBitmap.size() > 0) {
            adapter.update();
        }
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(pn_qm_ipqc_first_sure_editor.this.getContext(), R.anim.activity_translate_in));
                    pop.showAtLocation(pn_qm_ipqc_first_sure_editor.this, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(pn_qm_ipqc_first_sure_editor.this.getContext(),
                            GalleryGetPicActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    intent.putExtra("pic_id", id);
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
            if (Bimp.tempSelectBitmap.size() == 2) {
                return 2;
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
                if (position == 2) {
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


}
