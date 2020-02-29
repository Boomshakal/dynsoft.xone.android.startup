package dynsoft.xone.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;

/**
 * 首件测试页面
 */

public class FirstItemTestActivity extends Activity {
    private TextCell textCellTestCode;
    private TextCell textCellFirstCode;
    private TextCell textCellTaskOrderCode;
    private TextCell textCellItemCode;
    private TextCell textCellItemName;
    private ButtonTextCell buttonTextCellType;   //首件类型
    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private ListView listView1;
    private ListView listView2;
    private DataTable mDataTable1;
    private DataTable mDataTable2;
    private int backPressedTimes = 0;   //返回按钮按的次数
    private String first_code;
    private int listview_first_position;  //第一个listview的位置
    private int listview_second_position;  //第二个listview的位置
    private FirstItemTest1Adapter firstItemTest1Adapter;
    private String work_stage;
    private int sequence_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstitem_test);
        textCellTestCode = (TextCell) findViewById(R.id.text_cell_test_code);
        textCellFirstCode = (TextCell) findViewById(R.id.text_cell_first_code);
        textCellTaskOrderCode = (TextCell) findViewById(R.id.text_cell_task_order_code);
        textCellItemCode = (TextCell) findViewById(R.id.text_cell_item_code);
        textCellItemName = (TextCell) findViewById(R.id.text_cell_item_name);
        buttonTextCellType = (ButtonTextCell) findViewById(R.id.text_cell_type);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioButton1 = (RadioButton) findViewById(R.id.radiobutton_1);
        radioButton2 = (RadioButton) findViewById(R.id.radiobutton_2);
        radioButton3 = (RadioButton) findViewById(R.id.radiobutton_3);
        listView1 = (ListView) findViewById(R.id.listview_1);
        listView2 = (ListView) findViewById(R.id.listview_2);
        mDataTable1 = new DataTable();
        mDataTable2 = new DataTable();

        Intent intent = getIntent();
        first_code = intent.getStringExtra("first_code");
        String task_order_code = intent.getStringExtra("task_order_code");
        String item_code = intent.getStringExtra("item_code");
        String item_name = intent.getStringExtra("item_name");
        work_stage = intent.getStringExtra("work_stage");
        sequence_id = intent.getIntExtra("sequence_id", 0);

        if (textCellTestCode != null) {
            textCellTestCode.setLabelText("测试单号");
            textCellTestCode.setReadOnly();
            textCellTestCode.setVisibility(View.GONE);
        }

        if (textCellFirstCode != null) {
            textCellFirstCode.setLabelText("首件单号");
            textCellFirstCode.setReadOnly();
            textCellFirstCode.setContentText(first_code);
        }

        if (textCellTaskOrderCode != null) {
            textCellTaskOrderCode.setLabelText("生产任务");
            textCellTaskOrderCode.setReadOnly();
            textCellTaskOrderCode.setContentText(task_order_code);
        }

        if (textCellItemCode != null) {
            textCellItemCode.setLabelText("物料编码");
            textCellItemCode.setReadOnly();
            textCellItemCode.setContentText(item_code);
        }

        if (textCellItemName != null) {
            textCellItemName.setLabelText("物料名称");
            textCellItemName.setReadOnly();
            textCellItemName.setContentText(item_name);
        }

        if (buttonTextCellType != null) {
            buttonTextCellType.setLabelText("首件类型");
            buttonTextCellType.setReadOnly();
            buttonTextCellType.Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseType(buttonTextCellType);
                }
            });
        }

        initListView();    //设置ListView的数据
    }

    private void chooseType(final ButtonTextCell buttonTextCellType) {
        final ArrayList<String> names = new ArrayList<String>();
        names.add("新机种试产");
        names.add("每日开拉检查");
        names.add("设备故障调试");
        names.add("人员更换");
        names.add("新拖工生产");
        names.add("返工工单");
        names.add("ECN、物料代用执行");

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0) {
                    buttonTextCellType.setContentText(names.get(which));
                    dialog.dismiss();
                }
            }
        };
        new AlertDialog.Builder(FirstItemTestActivity.this).setTitle("请选择")
                .setSingleChoiceItems(names.toArray(new String[0]), names.indexOf
                        (buttonTextCellType.getContentText().toString()), listener)
                .setNegativeButton("取消", null).show();
    }

    private void initListView() {
        mDataTable1.Rows.removeAll(mDataTable1.Rows);
        mDataTable2.Rows.removeAll(mDataTable2.Rows);
        String sql = "exec fm_load_first_item_test_data_by_sequence ?,?,?,?";
        Parameters p = new Parameters().add(1, first_code).add(2, App.Current.UserID).add(3, sequence_id).add(4, textCellTaskOrderCode.getContentText());
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(FirstItemTestActivity.this, value.Error);
                } else if (value.Value != null && value.Value.Rows.size() > 0) {
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        String test_genre = value.Value.Rows.get(i).getValue("test_genre", "");
                        if (test_genre.equals("电性测试")) {
                            mDataTable2.Rows.add(value.Value.Rows.get(i));
                        } else {
                            mDataTable1.Rows.add(value.Value.Rows.get(i));
                        }
                    }
                    if (firstItemTest1Adapter == null) {
                        firstItemTest1Adapter = new FirstItemTest1Adapter(FirstItemTestActivity.this, mDataTable1, first_code);
                        listView1.setAdapter(firstItemTest1Adapter);
                    } else {
                        firstItemTest1Adapter.frash(mDataTable1);
                    }

                    FirstItemTest2Adapter firstItemTest2Adapter = new FirstItemTest2Adapter(FirstItemTestActivity.this, mDataTable2, first_code);
                    listView2.setAdapter(firstItemTest2Adapter);
//                    listView2.smoothScrollToPosition(listview_second_position);
                } else {
                    App.Current.toastError(FirstItemTestActivity.this, "加载数据出错！");
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (backPressedTimes > 0) {
                finish();
                backPressedTimes = 0;
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(FirstItemTestActivity.this)
                        .setTitle("询问").setMessage("是否保存？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                                if (checkedRadioButtonId > 0) {
                                    if (TextUtils.isEmpty(buttonTextCellType.getContentText())) {
                                        App.Current.toastError(FirstItemTestActivity.this, "请先选择首件类型");
                                    } else {
                                        RadioButton radioButton = (RadioButton) findViewById(checkedRadioButtonId);
                                        String trim = radioButton.getText().toString().trim();
                                        //更新状态
                                        String sql = "exec fm_update_first_item_test_status ?,?,?";
                                        Parameters p = new Parameters().add(1, trim).add(2, textCellFirstCode.getContentText()).add(3, buttonTextCellType.getContentText());
                                        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                                            @Override
                                            public void handleMessage(Message msg) {
                                                Result<Integer> value = Value;
                                                if (value.HasError) {
                                                    App.Current.toastError(FirstItemTestActivity.this, value.Error);
                                                } else {
                                                    App.Current.toastInfo(FirstItemTestActivity.this, "提交成功！");
                                                    finish();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    App.Current.toastError(FirstItemTestActivity.this, "请先选择最终判定结果！");
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                backPressedTimes += 1;
                            }
                        }).create();
                alertDialog.show();
            }


            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class FirstItemTest1Adapter extends BaseAdapter {
        private Context mContext;
        private DataTable mDataTable;
        private String firstCode;

        public FirstItemTest1Adapter(Context mContext, DataTable mDataTable, String firstCode) {
            this.mContext = mContext;
            this.mDataTable = mDataTable;
            this.firstCode = firstCode;
        }

        public void frash(DataTable dataTable) {
            this.mDataTable = dataTable;
            Log.e("len", listview_first_position + "position");
            notifyDataSetChanged();
            listView1.setSelection(listview_first_position);
        }

        @Override
        public int getCount() {
            return mDataTable.Rows.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = View.inflate(mContext, R.layout.item1_first_item_test, null);
                viewHolder.textViewType = (TextView) view.findViewById(R.id.text_type);
                viewHolder.textViewAsk = (TextView) view.findViewById(R.id.text_ask);
                viewHolder.textViewConfirm = (TextView) view.findViewById(R.id.text_confirm);
                viewHolder.editTextResult = (EditText) view.findViewById(R.id.edit_result);
                viewHolder.editTextExcept = (EditText) view.findViewById(R.id.edit_except);
                viewHolder.editTextJudge = (EditText) view.findViewById(R.id.edit_judge);
                viewHolder.editTextComment = (EditText) view.findViewById(R.id.edit_comment);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            final String test_genre = mDataTable.Rows.get(i).getValue("test_genre", "");
            String test_name = mDataTable.Rows.get(i).getValue("test_name", "");
//        String check_standard = mDataTable.Rows.get(i).getValue("check_standard", "");
            final String check_result = mDataTable.Rows.get(i).getValue("check_result", "");
            final String abnormal = mDataTable.Rows.get(i).getValue("abnormal", "");
            String judge = mDataTable.Rows.get(i).getValue("judge", "");
            final String comment = mDataTable.Rows.get(i).getValue("comment", "");
            String status = mDataTable.Rows.get(i).getValue("status", "");

            viewHolder.textViewType.setText(test_genre);
            viewHolder.textViewAsk.setText(test_name);
            viewHolder.editTextResult.setText(check_result);
            viewHolder.editTextJudge.setText(judge);
            viewHolder.editTextExcept.setText(abnormal);
            viewHolder.editTextComment.setText(comment);
            viewHolder.editTextJudge.setFocusable(false);
            viewHolder.editTextJudge.setFocusableInTouchMode(false);
            viewHolder.editTextJudge.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        final String chooseItems[] = {"OK", "NG", "NA"};
//                    final String chooseItems[] = {"OK"};
//                    if(builder == null) {
                        Log.e("len", chooseItems.toString());
//                    if(alertDialog == null) {
                        AlertDialog alertDialog = new AlertDialog.Builder(FirstItemTestActivity.this)
                                .setTitle("请选择")
                                .setSingleChoiceItems(chooseItems, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        viewHolder.editTextJudge.setText(chooseItems[i]);
                                        dialogInterface.dismiss();
                                    }
                                }).create();
                        if (!alertDialog.isShowing()) {
                            alertDialog.show();
                        }
                    }
                    return true;
                }
            });

            if (viewHolder.editTextResult.getTag() instanceof TextWatcher) {
                viewHolder.editTextResult.removeTextChangedListener((TextWatcher) viewHolder.editTextResult.getTag());
            }

            final int position = i;
            if (status.equals("草稿")) {
                viewHolder.textViewConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<Map<String, String>> commitItems = new ArrayList<>();
                        Map<String, String> commitItem = new HashMap<>();
                        commitItem.put("test_type", viewHolder.textViewType.getText().toString().trim());
                        commitItem.put("test_ask", viewHolder.textViewAsk.getText().toString().trim());
                        commitItem.put("test_result", viewHolder.editTextResult.getText().toString().trim());
                        commitItem.put("test_judge", viewHolder.editTextJudge.getText().toString().trim());
                        commitItem.put("test_except", viewHolder.editTextExcept.getText().toString().trim());
                        commitItem.put("test_comment", viewHolder.editTextComment.getText().toString().trim());
                        commitItem.put("work_stage", work_stage);
                        commitItems.add(commitItem);
                        String xml = XmlHelper.createXml("first_item", commitItem, "items", "item", commitItems);
                        String sql = "exec fm_insert_first_item_test_datas ?,?";                //保存信息
                        Parameters p = new Parameters().add(1, firstCode).add(2, xml);
                        Log.e("len", firstCode + "XML:" + xml);
                        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                            @Override
                            public void handleMessage(Message msg) {
                                listview_first_position = position;
                                Result<Integer> value = Value;
                                if (value.HasError) {
                                    App.Current.toastError(mContext, value.Error);
                                } else if (value.Value != null && value.Value > 0) {
                                    App.Current.toastInfo(mContext, "保存成功");
                                    initListView();
                                } else {
                                    App.Current.toastError(mContext, "保存失败");
                                }
                            }
                        });

                    }
                });
            }

            return view;
        }
    }

    class FirstItemTest2Adapter extends BaseAdapter {
        private Context mContext;
        private DataTable mDataTable;
        private String firstCode;

        public FirstItemTest2Adapter(Context mContext, DataTable mDataTable, String firstCode) {
            this.mContext = mContext;
            this.mDataTable = mDataTable;
            this.firstCode = firstCode;
        }

        public void frash(DataTable dataTable) {
            this.mDataTable = dataTable;
            notifyDataSetChanged();
            listView2.setSelection(listview_second_position);
        }


        @Override
        public int getCount() {
            return mDataTable.Rows.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = View.inflate(mContext, R.layout.item2_first_item_test, null);
                viewHolder.textViewTestType = (TextView) view.findViewById(R.id.text_type);
                viewHolder.textViewTestContent = (TextView) view.findViewById(R.id.text_item);
                viewHolder.textViewConfirm = (TextView) view.findViewById(R.id.text_confirm);
                viewHolder.checkBoxTest = (CheckBox) view.findViewById(R.id.checkbox_result);
                viewHolder.editViewComment = (EditText) view.findViewById(R.id.edit_comment);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            int is_selected = mDataTable.Rows.get(i).getValue("is_selected", 0);
            String test_genre = mDataTable.Rows.get(i).getValue("test_genre", "");
            String test_name = mDataTable.Rows.get(i).getValue("test_name", "");
            String comment = mDataTable.Rows.get(i).getValue("comment", "");
            String status = mDataTable.Rows.get(i).getValue("status", "");
            viewHolder.textViewTestType.setText(test_genre);
            viewHolder.textViewTestContent.setText(test_name);
            viewHolder.editViewComment.setText(comment);
            if (is_selected == 0) {
                viewHolder.checkBoxTest.setChecked(false);
            } else {
                viewHolder.checkBoxTest.setChecked(true);
            }
            final int position = i;
            if (status.equals("草稿")) {
                viewHolder.textViewConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<Map<String, String>> commitItems = new ArrayList<>();
                        Map<String, String> commitItem = new HashMap<>();
                        commitItem.put("test_type", viewHolder.textViewTestType.getText().toString().trim());
                        commitItem.put("test_ask", viewHolder.textViewTestContent.getText().toString().trim());
                        commitItem.put("checkbox_result", String.valueOf(viewHolder.checkBoxTest.isChecked() ? 1 : 0));
                        commitItem.put("text_comment", viewHolder.editViewComment.getText().toString().trim());
                        commitItem.put("work_stage", work_stage);
                        commitItems.add(commitItem);
                        String xml = XmlHelper.createXml("first_item", commitItem, "items", "item", commitItems);
                        String sql = "exec fm_insert_first_item_test_part2_datas ?,?";                //保存信息
                        Parameters p = new Parameters().add(1, firstCode).add(2, xml);
                        Log.e("len", firstCode + "XML:" + xml);
                        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
                            @Override
                            public void handleMessage(Message msg) {
                                listview_second_position = position;
                                Result<Integer> value = Value;
                                if (value.HasError) {
                                    App.Current.toastError(mContext, value.Error);
                                } else if (value.Value != null && value.Value > 0) {
                                    App.Current.toastInfo(mContext, "保存成功");
                                    initListView();
                                } else {
                                    App.Current.toastError(mContext, "保存失败");
                                }
                            }
                        });
                    }
                });
            }
            return view;
        }
    }

    class ViewHolder {
        private TextView textViewType;
        private TextView textViewAsk;
        private TextView textViewConfirm;
        private EditText editTextResult;
        private EditText editTextExcept;
        private EditText editTextJudge;
        private EditText editTextComment;
        private TextView textViewTestType;
        private TextView textViewTestContent;
        //            private TextView textViewConfirm;
        private CheckBox checkBoxTest;
        private EditText editViewComment;
    }

}
