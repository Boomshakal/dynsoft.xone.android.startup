package dynsoft.xone.android.sopactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;

import dynsoft.xone.android.adapter.CloudAdapter;
import dynsoft.xone.android.adapter.WithdrawAdapter;
import dynsoft.xone.android.adapter.WithdrawBindAdapter;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataSet;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.retrofit.RetrofitDownUtil;
import dynsoft.xone.android.retrofit.deletecloud.GetIdBySNSuccessBean;
import dynsoft.xone.android.retrofit.deletecloud.IotDeviceBean;
import dynsoft.xone.android.retrofit.uploadcloud.IotServiceApi;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Administrator on 2018/12/11.
 */

public class WithdrawActivity extends Activity {
    private EditText editText;
    private Button button;
    private ProgressBar progressBar;
    private String edittext;
    private ListView listView;
    private ListView listViewBind;
    private ListView listViewChild;
    private ListView listviewCloud;
    private LinearLayout linearLayout;
    private LinearLayout linearLayoutWork;
    private LinearLayout linearLayoutChild;
    private LinearLayout linearLayoutCloud;
    private ButtonTextCell buttonTextCell;
    private DataTable dataTable;
    private WithdrawAdapter withdrawAdapter;
    private WithdrawBindAdapter withdrawBindAdapter;
    private WithdrawBindAdapter withdrawChildAdapter;
    private CloudAdapter cloudAdapter;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        editText = (EditText) findViewById(R.id.edittext);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        button = (Button) findViewById(R.id.button);
        listView = (ListView) findViewById(R.id.listview);
        listViewBind = (ListView) findViewById(R.id.listview_bind);
        listViewChild = (ListView) findViewById(R.id.listview_child);
        listviewCloud = (ListView) findViewById(R.id.listview_cloud);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        buttonTextCell = (ButtonTextCell) findViewById(R.id.button_text_cell);
        linearLayoutWork = (LinearLayout) findViewById(R.id.linearlayout_work);
        linearLayoutChild = (LinearLayout) findViewById(R.id.linearlayout_child);
        linearLayoutCloud = (LinearLayout) findViewById(R.id.linearlayout_cloud);
        init();
    }

    private void init() {
        buttonTextCell.setLabelText("��ƽ̨��Ʒ");
        buttonTextCell.Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCloud(buttonTextCell);
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    edittext = editText.getText().toString().replace("\n", "");
//                    initData();
                    loadListView();
                }
                return false;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edittext = editText.getText().toString().replace("\n", "");
                loadListView();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {     //����ɾ��
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Long work_id = dataTable.Rows.get(position).getValue("id", 0L);
                String task_order_code = dataTable.Rows.get(position).getValue("task_order_code", "");
                String sequence_name = dataTable.Rows.get(position).getValue("sequence_name", "");
                String to_seq_name = dataTable.Rows.get(position).getValue("to_seq_name", "");
                String lot_number = dataTable.Rows.get(position).getValue("lot_number", "");
                new AlertDialog.Builder(WithdrawActivity.this)
                        .setTitle("��ʾ")
                        .setMessage("ѡ�е�����" + lot_number + "��������Ϊ:" + task_order_code + ",��ǰ����Ϊ:" + sequence_name + ",����Ϊ:" + to_seq_name + ",�Ƿ�ȷ��ɾ��������¼?")
                        .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteWorkRecord(work_id);
                            }
                        })
                        .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editText.setText("");
                            }
                        })
                        .show();
                return false;
            }
        });
        //ɾ����ƽ̨������
        listviewCloud.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {     //����ɾ��
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textViewId = (TextView) view.findViewById(R.id.txt_id);
                TextView textViewSn = (TextView) view.findViewById(R.id.txt_sn);
                final String deviceId = textViewId.getText().toString();
                final String productSn = textViewSn.getText().toString();
                new AlertDialog.Builder(WithdrawActivity.this)
                        .setTitle("��ʾ")
                        .setMessage("ȷ��Ҫ����ƽ̨ɾ��" + productSn + "��������")
                        .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteFromCloud(deviceId);
                            }
                        })
                        .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editText.setText("");
                            }
                        })
                        .show();
                return false;
            }
        });
        listViewBind.setOnItemClickListener(new AdapterView.OnItemClickListener() {                    //��������к����ý�EditText
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textViewBind = (TextView) view.findViewById(R.id.txt_bind_lot_number);
                String textBind = textViewBind.getText().toString().trim();
                TextView textView = (TextView) view.findViewById(R.id.txt_lot_number);
                String trim = textView.getText().toString().trim();
                if (editText.getText().toString().equals(textBind)) {
                    editText.setText(trim);
                } else {
                    editText.setText(textBind);
                }
            }
        });
        listViewChild.setOnItemClickListener(new AdapterView.OnItemClickListener() {                    //��������к����ý�EditText
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textViewBind = (TextView) view.findViewById(R.id.txt_bind_lot_number);
                String textBind = textViewBind.getText().toString().trim();
                TextView textView = (TextView) view.findViewById(R.id.txt_lot_number);
                String trim = textView.getText().toString().trim();
                if (editText.getText().toString().equals(textBind)) {
                    editText.setText(trim);
                } else {
                    editText.setText(textBind);
                }
            }
        });
    }

    /**
     * ͨ���豸IDɾ������
     *
     * @param deviceId
     */
    private void deleteFromCloud(String deviceId) {
        Retrofit uploadCloudRetrofit = RetrofitDownUtil.getInstence().getUploadCloudRetrofit();
        IotServiceApi iotServiceApi = uploadCloudRetrofit.create(IotServiceApi.class);
        Call<ResponseBody> responseBodyCall = iotServiceApi.deleteDeviceById(productId, deviceId);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (response.code() == 200) {
                    App.Current.toastInfo(WithdrawActivity.this, "ɾ���豸�ɹ���");
                    checkCloud("");
                } else {
                    if (response.errorBody() != null) {
                        try {
                            App.Current.toastError(WithdrawActivity.this, "ɾ��ʧ�ܡ�" + response.errorBody().string());
                            Log.e("len", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        App.Current.toastError(WithdrawActivity.this, "ɾ��ʧ�ܡ�" + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                App.Current.showError(WithdrawActivity.this, "ɾ��ʧ��Failure��" + throwable.getMessage());
            }
        });
    }

    private void chooseCloud(final ButtonTextCell buttonTextCell) {
        String sql = "SELECT * FROM dbo.fm_cloud_application_product_id_transation";
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                final Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(WithdrawActivity.this, value.Error);
                } else {
                    if (value.Value != null && value.Value.Rows.size() > 0) {
                        ArrayList<String> names = new ArrayList<>();
                        for (DataRow row : value.Value.Rows) {
                            StringBuffer name = new StringBuffer();
                            name.append(row.getValue("product_name", ""));
                            names.add(name.toString());
                        }

                        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which >= 0) {
                                    DataRow row = value.Value.Rows.get(which);
                                    productId = row.getValue("product_id", "");
                                    String productName = row.getValue("product_name", "");
                                    buttonTextCell.setContentText(productName);
                                }
                                dialog.dismiss();
                            }
                        };
                        new AlertDialog.Builder(WithdrawActivity.this).setTitle("��ѡ��")
                                .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                                        (buttonTextCell.getContentText().toString()), listener)
                                .setNegativeButton("ȡ��", null).show();
                    }
                }
            }
        });
    }

    /**
     * ����ListView
     * install_parse_failed_inconsistent_certificates  ǩ����ͻ
     */
    private void loadListView() {
        if (TextUtils.isEmpty(edittext)) {
            App.Current.toastError(WithdrawActivity.this, "����ɨ�����룡");
        } else {
            //�鿴�ƶ��Ƿ����������
            checkCloud(edittext);
            String sql = "exec fm_get_work_list_and_bind_by_lot_number ?";
            Parameters p = new Parameters().add(1, edittext);
            App.Current.DbPortal.ExecuteDataSetAsync("core_and", sql, p, new ResultHandler<DataSet>() {
                @Override
                public void handleMessage(Message msg) {
                    Result<DataSet> value = Value;
                    if (value.HasError) {
                        App.Current.toastError(WithdrawActivity.this, value.Error);
                    } else {
                        if (value.Value != null && value.Value.Tables.size() > 2) {
                            DataTable dataTable1 = value.Value.Tables.get(0);
                            DataTable dataTable2 = value.Value.Tables.get(1);
                            DataTable dataTable3 = value.Value.Tables.get(2);
                            if (dataTable1.Rows.size() < 1) {
                                linearLayoutWork.setVisibility(View.GONE);
                            } else {
                                linearLayoutWork.setVisibility(View.VISIBLE);
                            }
                            if (dataTable2.Rows.size() < 1) {
                                linearLayout.setVisibility(View.GONE);
                            } else {
                                linearLayout.setVisibility(View.VISIBLE);
                            }
                            if (dataTable3.Rows.size() < 1) {
                                linearLayoutChild.setVisibility(View.GONE);
                            } else {
                                linearLayoutChild.setVisibility(View.VISIBLE);
                            }
                            if (dataTable1.Rows.size() > 0) {
                                dataTable = dataTable1;
                                if (withdrawAdapter == null) {
                                    withdrawAdapter = new WithdrawAdapter(dataTable, WithdrawActivity.this);
                                } else {
                                    withdrawAdapter.fresh(dataTable);
                                }
                                listView.setAdapter(withdrawAdapter);
                            }
                            if (dataTable2.Rows.size() > 0) {
                                if (withdrawBindAdapter == null) {
                                    withdrawBindAdapter = new WithdrawBindAdapter(dataTable2, WithdrawActivity.this);
                                } else {
                                    withdrawBindAdapter.fresh(dataTable2);
                                }
                                listViewBind.setAdapter(withdrawBindAdapter);
                            }
                            if (dataTable3.Rows.size() > 0) {
                                if (withdrawChildAdapter == null) {
                                    withdrawChildAdapter = new WithdrawBindAdapter(dataTable3, WithdrawActivity.this);
                                } else {
                                    withdrawChildAdapter.fresh(dataTable3);
                                }
                                listViewChild.setAdapter(withdrawChildAdapter);
                            }
                        }
                    }
                    editText.setText("");
                }
            });
        }

    }

    private void checkCloud(String edittext) {
        //���һ����ȡproduct_id �Ŀ� �Ȳ�ѯ�豸��ID
        //ͨ����ƷID���豸��SN�Ż�ȡ�豸ID
        Retrofit uploadCloudRetrofit = RetrofitDownUtil.getInstence().getUploadCloudRetrofit();
        IotServiceApi iotServiceApi = uploadCloudRetrofit.create(IotServiceApi.class);
//        IotDeviceBean.FilterBean filterBean = new IotDeviceBean.FilterBean("sn");
        ArrayList<String> filter = new ArrayList<>();
        filter.add("sn");
        filter.add("id");
        filter.add("name");
        filter.add("mac");
        IotDeviceBean.QueryBean.Filed1Bean filed1Bean = new IotDeviceBean.QueryBean.Filed1Bean(edittext);
        IotDeviceBean.QueryBean queryBean = new IotDeviceBean.QueryBean(filed1Bean);
        IotDeviceBean iotDeviceBean = new IotDeviceBean(filter, queryBean);
        Gson gson = new Gson();
        String s = gson.toJson(iotDeviceBean);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), s);
        Call<ResponseBody> deviceMessageBySN = iotServiceApi.getDeviceMessageBySN(productId, requestBody);
//        Call<ResponseBody> deviceMessageBySN = iotServiceApi.getDeviceMessageBySN(productId);
        deviceMessageBySN.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                try {
                    if (response.code() == 200) {
                        Gson gson = new Gson();
                        String responseString = response.body().string();
                        GetIdBySNSuccessBean getIdBySNSuccessBean = gson.fromJson(responseString, GetIdBySNSuccessBean.class);
                        if (getIdBySNSuccessBean.getCount() > 0) {   //��ʾ��ѯ�����豸
                            linearLayoutCloud.setVisibility(View.VISIBLE);
                            cloudAdapter = new CloudAdapter(getIdBySNSuccessBean, WithdrawActivity.this);
                            listviewCloud.setAdapter(cloudAdapter);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                App.Current.toastError(WithdrawActivity.this, throwable.getMessage());
            }
        });
    }

    private void initData() {
        if (TextUtils.isEmpty(edittext)) {
            App.Current.toastError(WithdrawActivity.this, "����ɨ������");
        } else {
            //���ҵ�ǰ������ʲôλ��
            String sql = "exec fm_search_lot_number_and ?";
            Parameters p = new Parameters().add(1, edittext.replace("\n", ""));
            App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {

                @Override
                public void handleMessage(Message msg) {
                    Result<DataRow> value = Value;
                    if (value.HasError) {
                        App.Current.showError(WithdrawActivity.this, value.Error);
                    } else {
                        if (value.Value != null) {     //��ȡ����ǰ��������µ���ҵ��¼�� Ȼ�󵯳�����ʾ��ǰ�������û�ѡ���Ƿ�ɾ��
                            Long id = value.Value.getValue("id", 0L);           //fm_work��id
                            String seq_name = value.Value.getValue("seq_name", "");     //��������
                            String to_seq_name = value.Value.getValue("to_seq_name", "");     //��������
                            String task_order_code = value.Value.getValue("task_order_code", "");     //��������
                            ToastAlertDialog(id, seq_name, to_seq_name, task_order_code);
                        } else {
                            App.Current.showError(WithdrawActivity.this, "û�в��ҵ�����" + edittext + "����ҵ��¼");
                        }
                    }
                }
            });
        }
    }

    private void ToastAlertDialog(final Long id, String seq_name, String to_seq_name, String task_order_code) {
        new AlertDialog.Builder(WithdrawActivity.this)
                .setTitle("��ʾ")
                .setMessage("����" + edittext + "��������Ϊ:" + task_order_code + ",��ǰ����Ϊ:" + seq_name + ",����Ϊ:" + to_seq_name + ",�Ƿ�ȷ��ɾ��������¼?")
                .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteWorkRecord(id);
                    }
                })
                .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editText.setText("");
                    }
                })
                .show();
    }

    private void deleteWorkRecord(Long id) {
        String sql = "exec p_fm_work_delete ?,?";
        Parameters p = new Parameters().add(1, id).add(2, App.Current.UserID);
        progressBar.setVisibility(View.VISIBLE);
        editText.setText("");
        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                progressBar.setVisibility(View.GONE);
                Result<Integer> value = Value;
                if (value.HasError) {
                    App.Current.showError(WithdrawActivity.this, value.Error);
                } else {
                    if (value.Value != null) {
                        App.Current.toastInfo(WithdrawActivity.this, "ɾ���ɹ���");
                    } else {
                        Log.e("len", "value.Value == NULL");
                    }
                }
            }
        });
    }
}
