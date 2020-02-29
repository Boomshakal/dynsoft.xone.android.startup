package dynsoft.xone.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.HashMap;

import dynsoft.xone.android.activity.ECNActivity;
import dynsoft.xone.android.activity.FileOpenActivity;
import dynsoft.xone.android.control.TextChexkbox;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.PrintRequest;
import dynsoft.xone.android.data.PrintSetting;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.PrintHelper;

public class FirstItemAdapter extends BaseAdapter {
    private static final int ASKERROR = 1;
    private static final int INTENT = 2;
    private DataTable mDataTable;
    private Context mContext;
    private Activity activity;
    private String search;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ASKERROR:
                    String error = (String) msg.obj;
                    App.Current.toastError(mContext, error);
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    break;
                case INTENT:
                    String intentUrl = (String) msg.obj;
                    Intent intent = new Intent(mContext, FileOpenActivity.class);
                    intent.putExtra("url", intentUrl);
                    mContext.startActivity(intent);
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };
    private ProgressBar progressBar;

    public FirstItemAdapter(DataTable mDataTable, Context mContext, String search) {
        this.mDataTable = mDataTable;
        this.mContext = mContext;
        this.search = search;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return mDataTable.Rows.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_first_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textViewItemCode = (TextView) convertView.findViewById(R.id.text_item_code);
            viewHolder.textViewItemName = (TextView) convertView.findViewById(R.id.text_item_name);
            viewHolder.textViewQuantity = (TextView) convertView.findViewById(R.id.text_qiantity);
            viewHolder.textViewEcn = (TextView) convertView.findViewById(R.id.text_ecn);
            viewHolder.textViewCust = (TextView) convertView.findViewById(R.id.text_cust);
            viewHolder.textViewCustSn = (TextView) convertView.findViewById(R.id.text_cust_sn);
            viewHolder.textViewReItemCode = (TextView) convertView.findViewById(R.id.text_re_item_code);
            viewHolder.textViewWh = (TextView) convertView.findViewById(R.id.text_wh);
            viewHolder.linearlayoutWh = (LinearLayout) convertView.findViewById(R.id.linearlayout_wh);
            viewHolder.checkBoxConfirm = (CheckBox) convertView.findViewById(R.id.checkbox_comfirm);
            viewHolder.imageAppendix = (ImageView) convertView.findViewById(R.id.image_appendix);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearlayout);
            viewHolder.linearlayoutLink = (LinearLayout) convertView.findViewById(R.id.linearlayout_link);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final int head_id = mDataTable.Rows.get(position).getValue("HEAD_ID", 0);
        final int id = mDataTable.Rows.get(position).getValue("ID", 0);
        final String item_code = mDataTable.Rows.get(position).getValue("ITEM_CODE", "");
        String item_name = mDataTable.Rows.get(position).getValue("ITEM_NAME", "");
        float component_qty = mDataTable.Rows.get(position).getValue("COMPONENT_QTY", new BigDecimal(0)).floatValue();
        int is_confirm = mDataTable.Rows.get(position).getValue("is_confirm", 0);
        String sub_item_code = mDataTable.Rows.get(position).getValue("SUB_ITEM_CODE", "");
        String ref_wh = mDataTable.Rows.get(position).getValue("REF_WH", "");
        String bom_purpose = mDataTable.Rows.get(position).getValue("BOM_PURPOSE", "");
        final String cj_name = mDataTable.Rows.get(position).getValue("cj_name", "");
        String bom_ecn = mDataTable.Rows.get(position).getValue("BOM_ECN", "");
        int dc = mDataTable.Rows.get(position).getValue("DC", 0);
        String is_complete = mDataTable.Rows.get(position).getValue("IS_COMPLETE", "");   //是否已经审料
        int flag = mDataTable.Rows.get(position).getValue("flag", 0);
        viewHolder.textViewItemCode.setText(item_code);
        viewHolder.textViewItemName.setText(item_name);
        viewHolder.textViewQuantity.setText(String.valueOf(component_qty));
        viewHolder.textViewEcn.setText(bom_ecn);
        viewHolder.textViewReItemCode.setText(sub_item_code);
        viewHolder.textViewCust.setText(cj_name);
        viewHolder.textViewCustSn.setText(bom_purpose);
        viewHolder.textViewWh.setText(ref_wh);

        String[] refWhs = ref_wh.split(",");
        viewHolder.linearlayoutWh.removeAllViews();
        for (int i = 0; i < refWhs.length; i++) {
            if (!TextUtils.isEmpty(refWhs[i])) {
                TextChexkbox textChexkbox = new TextChexkbox(mContext);
                textChexkbox.setLabelText(refWhs[i]);
                if (viewHolder.linearlayoutWh.getChildCount() < refWhs.length) {
                    viewHolder.linearlayoutWh.addView(textChexkbox);
                }
            }
        }

        if (is_complete.equals("已审")) {
            viewHolder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.orange));
        } else {
            viewHolder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
        if (is_confirm == 1) {
            viewHolder.checkBoxConfirm.setChecked(true);
//            viewHolder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        } else if (is_confirm == 2) {
            viewHolder.checkBoxConfirm.setChecked(true);
            viewHolder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.megmeet_green));
        } else {
            viewHolder.checkBoxConfirm.setChecked(false);
//            viewHolder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
        if (flag == 1) {
            viewHolder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.royalblue));
        }


        //checkBox按钮 点一个更新一个
        viewHolder.checkBoxConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                boolean checked = viewHolder.checkBoxConfirm.isChecked();
                int checkInt = 0;
                if (checked) {
                    checkInt = 1;
                }
                String sql = "EXEC fm_update_ipqc_first_item_confirm ?,?,?,?";
                Parameters p = new Parameters().add(1, head_id).add(2, id).add(3, checkInt).add(4, search);
                App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
                    @Override
                    public void handleMessage(Message msg) {
                        Result<DataTable> value = Value;
                        if (value.HasError) {
                            App.Current.toastError(mContext, value.Error);
                        } else {
                            mDataTable = value.Value;

                            notifyDataSetChanged();
                        }
                    }
                });
            }
        });
        final View finalConvertView = convertView;

        viewHolder.linearlayoutLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(viewHolder.textViewEcn.getText().toString())) {
                    App.Current.toastError(mContext, "请选择正确的ECN");
                } else {
                    String sql = "SELECT unid FROM pdm_ecnflow_vv  WHERE formNumber = ?";
                    Parameters p = new Parameters().add(1, viewHolder.textViewEcn.getText().toString().trim());
                    App.Current.DbPortal.ExecuteRecordAsync("mgmt_plm_and", sql, p, new ResultHandler<DataRow>() {
                        @Override
                        public void handleMessage(Message msg) {
                            Result<DataRow> value = Value;
                            if (value.HasError) {
                                App.Current.toastError(mContext, value.Error);
                            } else if (value.Value != null) {
                                String unid = value.Value.getValue("unid", "");
                                Intent intent = new Intent(mContext, ECNActivity.class);
                                intent.putExtra("unid", unid);
                                mContext.startActivity(intent);
                            } else {
                                App.Current.toastError(mContext, "ECN没有找到");
                            }
                        }
                    });
                }
//                Toast.makeText(mContext, item_code + "厂家：" + cj_name, Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.imageAppendix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出单选对话框，选择之后进入查看文档界面
                toastChooseDialog(viewHolder.textViewItemCode.getText().toString(), finalConvertView);
            }
        });

        int childCount = viewHolder.linearlayoutWh.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final TextChexkbox childAt = (TextChexkbox) viewHolder.linearlayoutWh.getChildAt(i);
            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    childAt.setChecked(true);
                }
            });
        }
        return convertView;
    }

    private void toastChooseDialog(String itemCode, final View view) {
        String sql = "exec p_mes_get_doc_from ?,?";
        Parameters p = new Parameters().add(1, itemCode).add(2, "IPQC公用账号");
        App.Current.DbPortal.ExecuteDataTableAsync("mgmt_plm_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(mContext, value.Error);
                } else if (value.Value != null && value.Value.Rows.size() > 0) {
                    createPopupwindow(value.Value, view);
                } else {
                    App.Current.toastError(mContext, "没有附件");
                }
            }
        });
    }

    private void createPopupwindow(final DataTable value, final View viewParent) {
        PopupWindow popupWindow = new PopupWindow();
        View view = View.inflate(mContext, R.layout.item_choose_first_item, null);
        ListView listView = (ListView) view.findViewById(R.id.listview);
        progressBar = (ProgressBar) view.findViewById(R.id.progrssbar);
        FirstItemPopupwindowAdapter firstItemPopupwindowAdapter = new FirstItemPopupwindowAdapter(value, mContext);
        listView.setAdapter(firstItemPopupwindowAdapter);
        popupWindow.setContentView(view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                String attachunid = value.Rows.get(position).getValue("attachunid", "");
                String attachdbpath = value.Rows.get(position).getValue("attachdbpath", "");
                String Attachments = value.Rows.get(position).getValue("Attachments", "");
                downLoadFileFromPlm(attachunid, attachdbpath, Attachments);
            }
        });
        WindowManager windowManager = App.Current.Workbench.getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        popupWindow.setWidth(width / 2);
        popupWindow.setHeight(height / 2);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(viewParent, Gravity.CENTER, 0, 0);
    }

//    public void loadItems(final String edittext, int head_id, int id, int check_id) {
//        String sql = "exec fm_update_ipqc_first_item_confirm ?,?,?,?";
//        Parameters p = new Parameters().add(1, head_id).add(2, id).add(3, check_id).add(4, edittext);
//        Log.e("len", "head_id: " + id);
//        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
//            @Override
//            public void handleMessage(Message msg) {
//                Result<DataTable> value = Value;
//                if (value.HasError) {
//                    App.Current.toastError(mContext, value.Error);
//                } else if (value.Value != null && value.Value.Rows.size() > 0) {
//                    for (int i = 0; i < value.Value.Rows.size(); i++) {
//                        Log.e("len", "first:" + value.Value.Rows.get(i).getValue("item_code", ""));
//                    }
//                    mDataTable = value.Value;
//                    notifyDataSetChanged();
//                }
//            }
//        });
//    }


    private void downLoadFileFromPlm(final String attachunid, final String attachdbpath, final String attachments) {
        //保存打印设置

//                request.Parameters.put("formValues:", jsonDatas);
//        request.Parameters.put("TaskUser", text_cell_check_user.getContentText());
//                request.Parameters.put("esd_date", esd_date);
//                request.Parameters.put("ipqc", ipqc_code);
//                request.Parameters.put("line_man", line_man);
//                request.Parameters.put("line_man_code", line_man_code);
//                request.Parameters.put("department", department);
//                Log.e("len", id + "**" + request.toString());
//                Log.e("len", "JSON:" + stringBuffer.toString());
        new Thread() {
            @Override
            public void run() {
                PrintSetting printSetting = new PrintSetting();
                printSetting.Server = "株洲条码打印服务器102";
                printSetting.Url = "http://192.168.0.102:6683";
                printSetting.Printer = "#15";
                PrintHelper.savePrintServer(mContext, printSetting);

                PrintRequest request = new PrintRequest();
                request.Server = "http://192.168.0.102:6683";
                request.Printer = "#15";
                request.Code = "start_get_file";

                //准备打印参数
                request.Parameters = new HashMap<>();

                request.Parameters.put("attachunid", attachunid);
                request.Parameters.put("attachdbpath", attachdbpath);
                request.Parameters.put("attachments", attachments);
                Result<String> result = PrintHelper.print(request);
                if (result.HasError) {
                    Message message = new Message();
                    message.what = ASKERROR;
                    message.obj = result.Error;
                    handler.sendMessage(message);
                } else {
                    if (result.Value != null) {
                        String trim = result.Value.trim();
                        Message message = new Message();
                        message.what = INTENT;
                        message.obj = trim;
                        handler.sendMessage(message);
//                        Intent intent = new Intent(mContext, FileOpenActivity.class);
//                        intent.putExtra("url", trim);
//                        mContext.startActivity(intent);
                    } else {
                        Message message = new Message();
                        message.what = ASKERROR;
                        message.obj = "NULL";
                        handler.sendMessage(message);
                    }
                }
            }
        }.start();
    }

    class ViewHolder {
        private TextView textViewItemCode;
        private TextView textViewItemName;
        private TextView textViewQuantity;
        private TextView textViewEcn;
        private TextView textViewReItemCode;
        private TextView textViewWh;
        private TextView textViewCustSn;
        private TextView textViewCust;
        private CheckBox checkBoxConfirm;
        private ImageView imageAppendix;
        private LinearLayout linearLayout;
        private LinearLayout linearlayoutLink;    //ECN链接
        private LinearLayout linearlayoutWh;      //位号点击
    }
}
