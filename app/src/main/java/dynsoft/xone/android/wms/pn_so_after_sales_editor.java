package dynsoft.xone.android.wms;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.core.Workbench;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.DbPortal;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class pn_so_after_sales_editor extends pn_editor implements OnClickListener {

    public pn_so_after_sales_editor(Context context) {
        super(context);
    }

    private Button txt_b1;
    public ButtonTextCell txt_sn_number_cell; // sn编码
    public TextCell txt_item_code_cell; //物料编码
    public TextCell txt_item_name_cell; //物料名称
    public TextCell txt_user_cell; //操作用户
    public TextCell txt_express_cell; //快递单号
    public ButtonTextCell txt_express_comp_cell; //快递公司
    public TextCell txt_comment_cell; //备注
    public TextCell txt_customer_name; //客户名称
    public TextCell txt_field_status;//现场维修
    public ButtonTextCell txt_return_type;//退货类型
    private CheckBox checkbox_print;//打印标签
    private CheckBox is_history;
    private String is_history_key = "";
    Date now_t = new Date(System.currentTimeMillis());
    String now_time = new SimpleDateFormat("yyyy-MM-dd").format(now_t);


    private int scan_count;
    private boolean checkbool;
    private DataRow _order_row;
    private DataRow _lot_row;
    private Integer _total = 0;
    private Long _rownum;
    private String work_order_code;


    //设置对于的XML文件
    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(
                R.layout.pn_so_after_sales_editor, this, true);
        view.setLayoutParams(lp);
    }

    //  ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>();
    //  final String [] nItems = {"卷带","吸取中心偏移","送料不良","少配件","保养","感应灯不亮","抛料"};

    //设置显示控件
    @Override
    public void onPrepared() {

        super.onPrepared();

        scan_count = 0;
        checkbool = true;
        this.txt_sn_number_cell = (ButtonTextCell) this
                .findViewById(R.id.txt_sn_number_cell);

        this.txt_item_code_cell = (TextCell) this
                .findViewById(R.id.txt_item_code_cell);

        this.txt_item_name_cell = (TextCell) this
                .findViewById(R.id.txt_item_name_cell);

        this.txt_express_cell = (TextCell) this
                .findViewById(R.id.txt_express_cell);

        this.txt_express_comp_cell = (ButtonTextCell) this
                .findViewById(R.id.txt_express_comp_cell);

        this.txt_customer_name = (TextCell) this
                .findViewById(R.id.txt_customer_name);

        this.txt_field_status = (TextCell) this
                .findViewById(R.id.txt_field_status);

        this.txt_return_type = (ButtonTextCell) this
                .findViewById(R.id.txt_return_type);


        this.txt_b1 = (Button) this.findViewById(R.id.txt_b1);
        txt_b1.setOnClickListener(this);
        txt_b1.setVisibility(GONE);

        this.checkbox_print = (CheckBox) this.findViewById(R.id.checkbox_print);
        this.is_history = (CheckBox) this.findViewById(R.id.is_history);

        if (this.txt_sn_number_cell != null) {
            this.txt_sn_number_cell.setLabelText("SN编码");
            this.txt_sn_number_cell.setReadOnly();
            this.txt_sn_number_cell.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_close_light"));
            this.txt_sn_number_cell.Button.setOnClickListener(v -> pn_so_after_sales_editor.this.txt_sn_number_cell.setContentText(""));
        }

        if (this.txt_item_code_cell != null) {
            this.txt_item_code_cell.setLabelText("物料编码");
            //this.txt_item_code_cell.setReadOnly();
        }

        if (this.txt_express_cell != null) {
            this.txt_express_cell.setLabelText("快递单号");
            this.txt_express_cell.setReadOnly();
        }

        if (this.txt_express_comp_cell != null) {
            this.txt_express_comp_cell.setLabelText("快递公司");
            this.txt_express_comp_cell.setReadOnly();
            this.txt_express_comp_cell.Button.setOnClickListener(v -> choose_express_comp(this.txt_express_comp_cell));
//            this.txt_express_comp_cell.setOnClickListener(v -> get_express_comp("", new ResultHandler<String>() {
//                @Override
//                public void handleMessage(Message msg) {
//                    Result<String> result = this.Value;
//                    pn_so_after_sales_editor.this.txt_express_comp_cell.setContentText(result.Value);
//                }
//            }));
        }
        if (this.txt_customer_name != null) {
            this.txt_customer_name.setLabelText("客户名称");

        }

        if (this.txt_field_status != null) {
            this.txt_field_status.setLabelText("现场维修");
            this.txt_field_status.setReadOnly();
            this.txt_field_status.setContentText("否");
        }


        if (this.txt_item_name_cell != null) {
            this.txt_item_name_cell.setLabelText("物料名称");
            //this.txt_item_name_cell.setReadOnly();
        }


        this.txt_user_cell = (TextCell) this.findViewById(R.id.txt_user_cell);

        if (this.txt_user_cell != null) {
            this.txt_user_cell.setLabelText("售后人员");
            this.txt_user_cell.setReadOnly();
            this.txt_user_cell.setContentText(App.Current.UserCode);

        }

        if (this.txt_return_type != null) {
            this.txt_return_type.setLabelText("退货类型");
            this.txt_return_type.setReadOnly();
            setclicklisten(this.txt_return_type);
        }


        this.txt_comment_cell = (TextCell) this
                .findViewById(R.id.txt_comment_cell);

        if (this.txt_comment_cell != null) {
            this.txt_comment_cell.setLabelText("备注");
            //this.txt_comment_cell.setReadOnly();
        }

//    buttonPrint.setOnClickListener(new OnClickListener() {
//           @Override
//          public void onClick(View view) {
//
//              Map<String, String> parameters = new HashMap<String, String>();
//             parameters.put("item_code", txt_item_code_cell.getContentText());
//               parameters.put("sn_number",txt_sn_number_cell.getContentText());
//              parameters.put("customer_name",txt_customer_name.getContentText());
//               parameters.put("item_name",txt_item_name_cell.getContentText());
//				parameters.put("create_date",now_time);
//              App.Current.Print("mm_after_sales_PDA", "打印产品标签", parameters);
//          }
//       });

    }

    private void choose_express_comp(final ButtonTextCell buttonTextCell) {
        final String[] chooseItems = {"顺丰", "百世", "跨越", "德邦", "申通", "中通", "圆通", "韵达", "京东", "安能", "其他"};
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("请选择")
                .setSingleChoiceItems(chooseItems, 0, (dialogInterface, i) -> {
                    buttonTextCell.setContentText(chooseItems[i]);
                    dialogInterface.dismiss();
                }).create();
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    private void setclicklisten(ButtonTextCell buttontextcell) {
        buttontextcell.setReadOnly();
        buttontextcell.Button.setOnClickListener(v -> choose_return_type(buttontextcell));
    }

    private void choose_return_type(final ButtonTextCell buttonTextCell) {
        final String[] chooseItems = {"7天无理由未使用", "7天无理由已安装", "7天无理由已使用", "维修退货", "品质问题退货"};
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("请选择")
                .setSingleChoiceItems(chooseItems, 0, (dialogInterface, i) -> {
                    buttonTextCell.setContentText(chooseItems[i]);
                    dialogInterface.dismiss();
                }).create();
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }


    private static float getSimilarityRatio(String str, String target) {

        int d[][]; // 矩阵
        int n = str.length();
        int m = target.length();
        int i; // 遍历str的
        int j; // 遍历target的
        char ch1; // str的
        char ch2; // target的
        int temp; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if (n == 0 || m == 0) {
            return 0;
        }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++) { // 初始化第一列
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++) { // 初始化第一行
            d[0][j] = j;
        }

        for (i = 1; i <= n; i++) { // 遍历str
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++) {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2 || ch1 == ch2 + 32 || ch1 + 32 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), d[i - 1][j - 1] + temp);
            }
        }

        return (1 - (float) d[n][m] / Math.max(str.length(), target.length())) * 100F;
    }


    //扫条码
    @Override
    public void onScan(final String barcode) {
        String bar_code = barcode.trim();

        final String sn_number = this.txt_sn_number_cell.getContentText().trim();
        if (sn_number.length() == 0) {
            this.txt_sn_number_cell.setContentText(bar_code.toString());
            this.loadItem(bar_code);
        } else if (getSimilarityRatio(bar_code, sn_number) > 68) {
            Log.e("len", Float.toString(getSimilarityRatio(bar_code, sn_number)));
            App.Current.showError(this.getContext(), "请扫描快递单号！");

        } else {
            this.txt_express_cell.setContentText(bar_code.toString());
            get_express_comp(bar_code, new ResultHandler<String>() {
                        @Override
                        public void handleMessage(Message msg) {
                            Result<String> result = this.Value;
                            pn_so_after_sales_editor.this.txt_express_comp_cell.setContentText(result.Value);
                        }
                    }
            );
        }

    }

    private Result<String> send_http(String code) {
        Result<String> comp_name = new Result<String>();
        OkHttpClient client = new OkHttpClient();
        String url = "https://www.kuaidi100.com/autonumber/autoComNum?text=" + code;

//            FormEncodingBuilder builder = new FormEncodingBuilder();
//            builder.add("Secret", "GEZVMQJTHEYDSVBU");
//            Request request = new Request.Builder().url(url).post(builder.build()).build();
        Request request = new Request.Builder()
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Safari/537.36")
                .url(url).build();
        try {
            Response response = client.newCall(request).execute();//发送请求

            if (response.isSuccessful()) {

                String result = response.body().string();

                Gson gson = new Gson();
                HashMap res = gson.fromJson(result, HashMap.class);

                ArrayList list = (ArrayList) res.get("auto");
                if (list.size() == 0) {
                    comp_name.Value = "";
                } else {
                    StringMap comp_info = (StringMap) list.get(0);
                    comp_name.Value = Objects.requireNonNull(comp_info.get("name")).toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            comp_name.HasError = Boolean.FALSE;
            comp_name.Value = "";
        }
        return comp_name;
    }

    private void get_express_comp(String code, final ResultHandler<String> handler) {
        new Thread(() -> {
            if (handler != null) {
                handler.Value = send_http(code);
                handler.sendEmptyMessage(0);
            }
        }).start();
    }


    ///根据SN编号带出物料编码和物料名称
    public void loadItem(String code) {
        this.ProgressDialog.show();

        String sql = "exec fm_get_after_sales_data_from_sn ?";
        Parameters p = new Parameters().add(1, code);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_editor.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_sales_editor.this.getContext(), result.Error);
                    return;
                }

                DataRow row = result.Value;
                if (row == null) {
                    App.Current.showError(pn_so_after_sales_editor.this.getContext(), "找不到该SN的对应的信息");

                    pn_so_after_sales_editor.this.Header.setTitleText("找不到该SN的对应的信息");
                    return;
                }
                pn_so_after_sales_editor.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
                pn_so_after_sales_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
                pn_so_after_sales_editor.this.txt_customer_name.setContentText(row.getValue("customer_name", ""));
                now_time = row.getValue("cur_date", "");


            }
        });
    }

    /////
    //提交操作
    ////
    @Override
    public void commit() {
        //将feeder标记成送修状态，并在待维修记录中增加记录


        final String sn_number = this.txt_sn_number_cell.getContentText().trim();
        if (sn_number == null || sn_number.length() == 0) {
            App.Current.showError(this.getContext(), "SN信息不能为空！");
            return;
        }


        String item_code = this.txt_item_code_cell.getContentText().trim();
        //if (item_code == null || item_code.length() == 0) {
        //	App.Current.showError(this.getContext(), "物料信息不能为空！");
        //	return;
        //}

        String item_name = this.txt_item_name_cell.getContentText().trim();
        //if (item_name == null || item_name.length() == 0) {
        //	App.Current.showError(this.getContext(), "物料信息不能为空！");
        //	return;
        //}

        String customer_name = this.txt_customer_name.getContentText().trim();
        if (customer_name == null || customer_name.length() == 0) {
            App.Current.showError(this.getContext(), "客户名称不能为空！");
            return;
        }

        String field_status = this.txt_field_status.getContentText().trim();

        String user = this.txt_user_cell.getContentText().trim();

        String comment = this.txt_comment_cell.getContentText().trim();
        String express = this.txt_express_cell.getContentText().trim();
        String express_comp = this.txt_express_comp_cell.getContentText().trim();
        String return_type = this.txt_return_type.getContentText().trim();


        if (user == null || user.length() == 0) {
            App.Current.showError(this.getContext(), "收货人员信息不能为空！");
            return;
        }

        if (TextUtils.isEmpty(this.txt_return_type.getContentText().trim())) {
            App.Current.showError(this.getContext(), "请选择退货类型！");
            return;
        }

        if (is_history.isChecked()) {
            String sql_history = "exec is_exists_mm_ar_after_receiving_items ?";
            Parameters p_history = new Parameters();
            p_history.add(1, sn_number);
            Result<Integer> result_history = App.Current.DbPortal.ExecuteScalar(this.Connector, sql_history, p_history, Integer.class);
            if (result_history.HasError) {
                App.Current.showError(pn_so_after_sales_editor.this.getContext(), result_history.Error);
                return;
            }
            if (result_history.Value > 0) {
                App.Current.showError(this.getContext(), "该物料已经进入修改流程！");
                return;
            }
            is_history_key = "历史物料";
        } else {

        }

        //String sql = " exec mm_after_receiving_isnert_1 ?,?,?,?,?,?,?,?";
        String sql = "mm_after_receiving_isnert_1_20181031 ?,?,?,?,?,?,?,?,?,?,?";

        System.out.print(item_code);
        System.out.print(sql);
        Parameters p = new Parameters();
        p.add(1, sn_number).add(2, item_code).add(3, item_name)
                .add(4, user).add(5, comment).add(6, customer_name)
                .add(7, field_status).add(8, is_history_key)
                .add(9, express).add(10, express_comp).add(11, return_type);
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_editor.this.ProgressDialog.dismiss();

                Result<DataTable> value = Value;

                if (value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                    return;
                }

                App.Current.toastInfo(pn_so_after_sales_editor.this.getContext(), "售后收货登记记录成功");

                if (checkbox_print.isChecked()) {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("item_code", txt_item_code_cell.getContentText());
                    parameters.put("sn_numner", txt_sn_number_cell.getContentText());
                    parameters.put("customer_name", txt_customer_name.getContentText());
                    parameters.put("item_name", txt_item_name_cell.getContentText());
                    parameters.put("sh_date", now_time);
                    App.Current.Print("store_from_after_sales_and", "售后收货外箱标签", parameters);
                } else {

                }

                clear();

            }
        });
    }

    //打印操作
    public void printLabel() {
        //Map<String, String> parameters = new HashMap<String, String>();
        //parameters.put("item_code", this.Item_code);
        //App.Current.Print("mm_item_identifying_label", "打印物料标识牌", parameters);
    }

    //清空数据
    public void clear() {

        this.txt_sn_number_cell.setContentText("");
        this.txt_item_code_cell.setContentText("");
        this.txt_item_name_cell.setContentText("");
        this.txt_customer_name.setContentText("");
        this.txt_field_status.setContentText("");
        this.txt_express_cell.setContentText("");
        this.is_history.setChecked(false);
    }

    @Override
    public void onClick(View view) {
        App.Current.Workbench.onShake();
    }
}
