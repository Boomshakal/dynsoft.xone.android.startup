package dynsoft.xone.android.sopactivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/11/29.
 */

public class EmployeeMessageActivity extends Activity {
    private EditText editText;
    private ImageView imageView;
    private TextView textViewUserCode;
    private TextView textViewEntryTime;
    private ListView listViewSkill;
    private StringBuffer _sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_message);
        editText = (EditText) findViewById(R.id.edittext);
        imageView = (ImageView) findViewById(R.id.imageview);
        textViewEntryTime = (TextView) findViewById(R.id.textview_entrytime);
        textViewUserCode = (TextView) findViewById(R.id.textview_usercode);
        listViewSkill = (ListView) findViewById(R.id.listview_skill);
        _sb = new StringBuffer();
        initEdittextKeyListener();
    }

    private void initEdittextKeyListener() {
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    getCardMessage(editText.getText().toString().replace("\n", "").trim());
                    return true;
                }
                return false;
            }
        });


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                if (s.length() == 10) {
                    getCardMessage(s);
                }
            }
        });
    }

    private void getCardMessage(String cardNumber) {     //获取打卡信息
        String sql = "exec fm_get_card_message_and ?";
        Parameters p = new Parameters().add(1, cardNumber);
        editText.setText("");
        App.Current.DbPortal.ExecuteDataTableAsync("core_hr_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
//                    App.Current.toastError(EmployeeMessageActivity.this, value.Error);
                    editText.setText("");
                    editText.setHint("请将焦点放在这里，然后打卡");
                } else {
                    if (value.Value != null && value.Value.Rows.size() > 0) {
                        byte[] img = value.Value.Rows.get(0).getValue("img", new byte[10]);
                        String user_code = value.Value.Rows.get(0).getValue("user_code", "");
                        Date entry_time = value.Value.Rows.get(0).getValue("entry_time", new Date(System.currentTimeMillis()));
                        String format = new SimpleDateFormat("yyyy-MM-dd").format(entry_time);
                        Glide.with(EmployeeMessageActivity.this).load(img).into(imageView);
                        textViewEntryTime.setText(format);
                        textViewUserCode.setText(user_code);
                        MessageAdapter messageAdapter = new MessageAdapter(value.Value);
                        listViewSkill.setAdapter(messageAdapter);
                        editText.setText("");
                        editText.setHint("请将焦点放在这里，然后打卡");
                    } else {
                        App.Current.toastError(EmployeeMessageActivity.this, "卡片不属于本系统！");
                        editText.setText("");
                        editText.setHint("请将焦点放在这里，然后打卡");
                    }
                }
            }
        });
    }

    class MessageAdapter extends BaseAdapter {
        private DataTable dataTable;

        public MessageAdapter(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        @Override
        public int getCount() {
            return dataTable.Rows.size();
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
            MessageViewHolder messageViewHolder;
            if(view == null) {
                view = View.inflate(EmployeeMessageActivity.this, R.layout.item_employee_skill, null);
                messageViewHolder = new MessageViewHolder();
                messageViewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout);
                messageViewHolder.textViewSkill = (TextView) view.findViewById(R.id.textview_skill);
                messageViewHolder.textViewSkilltype = (TextView) view.findViewById(R.id.textview_skilltype);
                view.setTag(messageViewHolder);
            } else {
                messageViewHolder = (MessageViewHolder) view.getTag();
            }

            String skillType = dataTable.Rows.get(i).getValue("skill_type", "");String skill = dataTable.Rows.get(i).getValue("skills", "");
            Log.e("len", skillType + "**" + skill);
            if (TextUtils.isEmpty(skill)) {
                messageViewHolder.linearLayout.setVisibility(View.GONE);
            }
            messageViewHolder.textViewSkill.setText(skill);
            messageViewHolder.textViewSkilltype.setText(skillType);
            return view;
        }
    }

    class MessageViewHolder{
        private LinearLayout linearLayout;
        private TextView textViewSkilltype;
        private TextView textViewSkill;
    }
}
