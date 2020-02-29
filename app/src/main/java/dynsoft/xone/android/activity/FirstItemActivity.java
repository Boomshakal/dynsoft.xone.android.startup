package dynsoft.xone.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dynsoft.xone.android.adapter.FirstItemAdapter;
import dynsoft.xone.android.adapter.FirstItemNewAdapter;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.util.DrawableUtil;
import oracle.jdbc.driver.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;

public class FirstItemActivity extends Activity {
    private final static int RQ_SCAN_BY_CAMERA = 1;
    private static final int SCANRESULT = 123;
    private ListView listViewTop;
    private ListView listViewSecond;
    private LinearLayout linearLayoutBom;   // 设置BOM版本变更区域宽度
    private TextCell textCellTaskOrderCode;
    private TextCell textCellItemName;
    private TextCell textCellPlanQuantity;
    private EditText editText;
    private ImageView imageView;          //隐藏ECN
    private int id;
    private int counts;
    private int org_id;
    private long item_id;
    private String task_order_code;
    private String item_name;
    private String plan_quantity;
    private Connection conn;
    private CallableStatement stmt;
    private FirstItemAdapter firstItemAdapter;
    private DataTable dataTable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_item);
        listViewTop = (ListView) findViewById(R.id.listview_1);
        listViewSecond = (ListView) findViewById(R.id.listview_2);
        linearLayoutBom = (LinearLayout) findViewById(R.id.linearlayout_bom);
        textCellTaskOrderCode = (TextCell) findViewById(R.id.text_cell_task_order);
        textCellItemName = (TextCell) findViewById(R.id.text_cell_item_name);
        textCellPlanQuantity = (TextCell) findViewById(R.id.text_cell_plan_quantity);
        editText = (EditText) findViewById(R.id.edittext);
        dataTable = new DataTable();
        imageView = (ImageView) findViewById(R.id.imageview);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.up));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linearLayoutBom.getVisibility() == View.GONE) {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.up));
                    linearLayoutBom.setVisibility(View.VISIBLE);
                } else {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.down));
                    linearLayoutBom.setVisibility(View.GONE);
                }
            }
        });
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        counts = intent.getIntExtra("counts", 0);
        org_id = intent.getIntExtra("org_id", 0);
        item_id = intent.getLongExtra("item_id", 0);
        task_order_code = intent.getStringExtra("task_order_code");
        item_name = intent.getStringExtra("item_name");
        plan_quantity = intent.getStringExtra("plan_quantity");
        WindowManager windowManager = getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (width * 0.7), height / 10 * 3);
        linearLayoutBom.setLayoutParams(layoutParams);

        if (textCellTaskOrderCode != null) {
            textCellTaskOrderCode.setLabelText("生产任务");
            textCellTaskOrderCode.setReadOnly();
            textCellTaskOrderCode.setContentText(task_order_code);
        }

        if (textCellItemName != null) {
            textCellItemName.setLabelText("机型");
            textCellItemName.setReadOnly();
            textCellItemName.setContentText(item_name);
        }

        if (textCellPlanQuantity != null) {
            textCellPlanQuantity.setLabelText("批量");
            textCellPlanQuantity.setReadOnly();
            textCellPlanQuantity.setContentText(String.valueOf(plan_quantity));
        }

        loadTopItems();
        loadItems("");
//        /**
//         * 从oracle里面获取BOM数据，生成首件的ITEM表
//         */
//        if (counts < 1) {
//            loadItemsFromOracle();
////            loadItems("");
//        } else {
////            loadItems("");
//        }

        new DrawableUtil(editText, new DrawableUtil.OnDrawableListener() {
            @Override
            public void onLeft(View v, Drawable left) {       //扫描
                scannerBack();
            }

            @Override
            public void onRight(View v, Drawable right) {
                String edittext = editText.getText().toString();
                loadItems(edittext);
            }
        });
    }

    private void loadItemsFromOracle() {
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
            stmt.setInt(2, 140);
            stmt.setInt(3, 30);

            stmt.registerOutParameter(4, OracleTypes.CURSOR);
            stmt.execute();
            ResultSet cursor = ((OracleCallableStatement) stmt).getCursor(4);
            ResultSetMetaData metaData = cursor.getMetaData();
            int columnCount = metaData.getColumnCount();
            Map<String, String> headMap = new HashMap<>();
            headMap.put("task_order_code", textCellTaskOrderCode.getContentText().trim());
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
                item.put("item_code", item_code);
                item.put("item_name", item_name);
                item.put("bom_purpose", bom_purpose);
                item.put("sub_item", item_code);
                item.put("ref_wh", ref_wh);
                item.put("change_notice", change_notice);
                itemArray.add(item);
//                String item_name = cursor.getString("ITEM_NAME");
            }

            String xml = XmlHelper.createXml("root", headMap, "items", "item", itemArray);
            int start = 0;
        } catch (SQLException e) {
            e.printStackTrace();
            App.Current.showInfo(FirstItemActivity.this, e.getMessage() + "111111");
        }
    }

    private void scannerBack() {

//        PackageInfo packageInfo;
//        try {
//            packageInfo = this.getPackageManager().getPackageInfo("com.google.zxing.client.android", 0);
//        } catch (PackageManager.NameNotFoundException e) {
//            packageInfo = null;
//        }
//
//        if (packageInfo == null) {
//            Dialog dialog = new AlertDialog.Builder(this)
//                    .setTitle("错误")
//                    .setMessage("没有安装 Barcode Scanner")
//                    .setPositiveButton("确定", null).create();
//            dialog.show();
//
//        } else {
//            App.Current.playSound(R.raw.shake_beep);
//            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
//            intent.setPackage("com.google.zxing.client.android");
//            startActivityForResult(intent, RQ_SCAN_BY_CAMERA);
////            App.Current.Workbench.scanByCamera();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == SCANRESULT) {
            //处理扫描结果（在界面上显示）
            if (null != intent) {
                Bundle bundle = intent.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(FirstItemActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }

//        if (requestCode == RQ_SCAN_BY_CAMERA) {
//            if (resultCode == -1) {
//                String contents = intent.getStringExtra("SCAN_RESULT");
//                if (contents.startsWith("CRQ:") || contents.startsWith("CQR:")) {
//                    String barcode = contents.substring(4);
//                    String[] split = barcode.split("-");
//                    if (split.length > 2) {
//                        contents = split[1];
//                    }
//                }
//                editText.setText(contents);
//                loadItems(contents);
//            }
//        }
    }

    /**
     * 获取ECN单号的变更
     */
    private void loadTopItems() {
        final DataTable tb = new DataTable();

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.21:1609:PROD1", "apps", "apps");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String sql = "{call pkg_getrecord.p_rtn_ecndtl(?,?,?,?)}";
            stmt = conn.prepareCall(sql);
            stmt.setObject(1, item_id);
            stmt.setObject(2, org_id);
            stmt.setObject(3, 10);
            stmt.registerOutParameter(4, OracleTypes.CURSOR);
            stmt.execute();
            ResultSet cursor = ((OracleCallableStatement) stmt).getCursor(4);
            ResultSetMetaData metaData = cursor.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (cursor.next()) {
                DataRow rw = new DataRow();

                rw.setValue("id", cursor.getString("ID"));
                rw.setValue("change_notice", cursor.getString("CHANGE_NOTICE"));
                rw.setValue("implementation_date", cursor.getString("IMPLEMENTATION_DATE"));
                rw.setValue("new_item_revision", cursor.getString("NEW_ITEM_REVISION"));
                rw.setValue("plan_level", cursor.getString("PLAN_LEVEL"));
                rw.setValue("item_num", cursor.getString("ITEM_NUM"));
                tb.Rows.add(rw);
            }

            //给listViewTop设置值
            FirstItemNewAdapter firstItemNewAdapter = new FirstItemNewAdapter(tb, FirstItemActivity.this);
            listViewTop.setAdapter(firstItemNewAdapter);

        } catch (Exception e) {
        }
    }

    private void PrepareParameters(PreparedStatement statement, Parameters parameters) throws SQLException {
        Iterator<Map.Entry<Object, Object>> iterator = parameters.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<Object, Object> entry = iterator.next();
            statement.setObject((Integer)entry.getKey(), entry.getValue());
        }
    }

    public void loadItems(final String edittext) {
        String sql = "exec fm_load_first_item_by_head_id4 ?,?";
        Parameters p = new Parameters().add(1, id).add(2, edittext);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {

            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(FirstItemActivity.this, value.Error);
                } else if (value.Value != null && value.Value.Rows.size() > 0) {
                    dataTable.Rows.removeAll(dataTable.Rows);
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        dataTable.Rows.add(value.Value.Rows.get(i));
                    }
                    if (firstItemAdapter == null) {
                        firstItemAdapter = new FirstItemAdapter(dataTable, FirstItemActivity.this, edittext);
                        listViewSecond.setAdapter(firstItemAdapter);
                    } else {
                        firstItemAdapter.notifyDataSetChanged();
                    }
                } else {
                    App.Current.toastError(FirstItemActivity.this, "搜索有误，请查询。");
                }
            }
        });
    }
}
