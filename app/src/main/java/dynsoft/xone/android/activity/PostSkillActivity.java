package dynsoft.xone.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.tts.loopj.LogHandler;

import java.util.ArrayList;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/8/27.
 */

public class PostSkillActivity extends Activity {
    private static final int SCROLLTIME = 2 * 60 * 1000;
    private LinearLayout linearLayoutTitle;
    private LinearLayout linearLayoutLow;
    private LinearLayout linearLayoutHigh;
    private ListView listviewContent;
    private TextView textviewTitle;

    private TextView textView0;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private android.widget.LinearLayout.LayoutParams layoutParams;
    private Intent intent;
    private String value;
    private ArrayList<String> stringTitles;
    private PostKillAdapter postKillAdapter;
    private boolean hasReturnRate;
    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            scrollListView();
            handler.postDelayed(runnable, SCROLLTIME);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_skill);
        handler = new Handler();
        linearLayoutTitle = (LinearLayout) findViewById(R.id.linearlayout_title);
        linearLayoutHigh = (LinearLayout) findViewById(R.id.linearlayout_return_high);
        linearLayoutLow = (LinearLayout) findViewById(R.id.linearlayout_return_low);
        listviewContent = (ListView) findViewById(R.id.listview_content);
        textviewTitle = (TextView) findViewById(R.id.textview_title);
        textView0 = (TextView) findViewById(R.id.textview_0);
        textView1 = (TextView) findViewById(R.id.textview_1);
        textView2 = (TextView) findViewById(R.id.textview_2);
        textView3 = (TextView) findViewById(R.id.textview_3);
        textView4 = (TextView) findViewById(R.id.textview_4);
        stringTitles = new ArrayList<String>();
        intent = getIntent();
        value = intent.getStringExtra("value");
        textviewTitle.setText(value + "现场领班、组长岗位技能统计表");
        initTitle();
        initBottom();
        handler.post(runnable);
    }

    private void initBottom() {
        String sql = "select * from post_skill_desc order by id";
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PostSkillActivity.this, value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 5) {
                    textView0.setText(value.Value.Rows.get(0).getValue("description", ""));
                    textView1.setText(value.Value.Rows.get(1).getValue("description", ""));
                    textView2.setText(value.Value.Rows.get(2).getValue("description", ""));
                    textView3.setText(value.Value.Rows.get(3).getValue("description", ""));
                    textView4.setText(value.Value.Rows.get(4).getValue("description", ""));
                }
            }
        });
    }

    private void initTitle() {

        String sql = "exec get_post_skill_title_and ?";
        Parameters p = new Parameters().add(1, value);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PostSkillActivity.this, value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    //判断是否需要退货率
                    String skill_name = value.Value.Rows.get(value.Value.Rows.size() - 1).getValue("skill_name", "");
                    if ("退货率".equals(skill_name)) {  //需要退货率
                        hasReturnRate = true;
                        linearLayoutHigh.setVisibility(View.VISIBLE);
                        linearLayoutLow.setVisibility(View.VISIBLE);
                    } else {
                        hasReturnRate = false;
                        linearLayoutHigh.setVisibility(View.GONE);
                        linearLayoutLow.setVisibility(View.GONE);
                    }

                    for (int i = -2; i < value.Value.Rows.size(); i++) {
                        if (i > -1) {
                            String postType = value.Value.Rows.get(i).getValue("skill_name", "");
                            stringTitles.add(postType);
                        }
                        if (i == -2) {
                            layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                        } else if (i == -1) {
                            layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f);
                        } else if (i == value.Value.Rows.size() - 1) {
                            layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f);
                        } else {
                            layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f);
                        }
                        TextView textView = new TextView(PostSkillActivity.this);
                        textView.setLayoutParams(layoutParams);
//                        textView.setLineSpacing(1.2f, 1.2f);//设置行间距
                        textView.setTextColor(Color.BLACK);
                        textView.setGravity(Gravity.CENTER);
                        textView.setSingleLine();
                        textView.setPadding(10, 10, 10, 10);
                        textView.setBackgroundResource(R.drawable.textview_border);
                        textView.setFocusable(true);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                        textView.setMarqueeRepeatLimit(1);
                        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                        if (i == -2) {
                            textView.setText("序号");
                        } else if (i == -1) {
                            textView.setText("姓名/技能");
                        } else if (i > -1) {
                            textView.setText(value.Value.Rows.get(i).getValue("skill_name", ""));
                        }
//                        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                        linearLayoutTitle.addView(textView, (i + 2), layoutParams);
                    }

                    initListView();
                }
            }
        });
    }

    private void initListView() {
        String sql = "exec fm_get_post_skill_score_from_org_name_and ?";
        Parameters p = new Parameters().add(1, value);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {

            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PostSkillActivity.this, value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    postKillAdapter = new PostKillAdapter(value.Value);
                    listviewContent.setAdapter(postKillAdapter);
                } else {

                }
            }
        });
    }

    private void scrollListView() {
        int childCount = listviewContent.getChildCount();
        int count = listviewContent.getCount();
        int lastVisiblePosition = listviewContent.getLastVisiblePosition();
        if (lastVisiblePosition >= count - 1) {
            listviewContent.smoothScrollToPosition(0);
        } else {
//            listView.smoothScrollToPosition(lastVisiblePosition + childCount - 1);
            listviewContent.smoothScrollByOffset(childCount - 1);
        }
    }

    class PostKillAdapter extends BaseAdapter {
        private DataTable dataTable;

        public PostKillAdapter(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        @Override
        public int getCount() {
            return dataTable == null ? 0 : dataTable.Rows.size();
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
//            ViewHolder viewHolder;
//            if (view == null) {
//                view = View.inflate(PostSkillActivity.this, R.layout.item_post_skill, null);
//                viewHolder = new ViewHolder();
//                viewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.item_post_linearlayout);
//                view.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) view.getTag();
//            }

            view = View.inflate(PostSkillActivity.this, R.layout.item_post_skill, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.item_post_linearlayout);
            view.setTag(viewHolder);
//            if (viewHolder.linearLayout.getChildCount() < stringTitles.size()) {
            for (int j = -2; j < stringTitles.size(); j++) {
                if (j == -2) {
                    layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                } else if (j == -1) {
                    layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 3f);
                } else if (j == stringTitles.size() - 1) {
                    layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 3f);
                } else {
                    layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2f);
                }
//                    if (j == -1) {
//                        layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 3f);
//                    } else {
//                        layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2f);
//                    }
                if (hasReturnRate) {
                    if (j < 0) {       //序号和姓名
                        TextView textView = new TextView(PostSkillActivity.this);
                        textView.setLayoutParams(layoutParams);
//                        textView.setLineSpacing(1.2f, 1.2f);//设置行间距
                        textView.setTextColor(Color.BLACK);
                        textView.setGravity(Gravity.CENTER);
                        textView.setLines(1);
                        textView.setPadding(10, 10, 10, 10);
                        textView.setBackgroundResource(R.drawable.textview_border);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                        if (j == -2) {
                            textView.setText((i + 1) + "");
                        } else if (j == -1) {
                            textView.setText(dataTable.Rows.get(i).getValue("worker_name", ""));
                        } else {
                            textView.setText(dataTable.Rows.get(i).getValue("备注", ""));
                        }
//                        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                        viewHolder.linearLayout.addView(textView, j + 2, layoutParams);
                    } else if (j < stringTitles.size() - 2) {                 //技能等级
                        ImageView imageview = new ImageView(PostSkillActivity.this);
                        imageview.setLayoutParams(layoutParams);
//                        textView.setLineSpacing(1.2f, 1.2f);//设置行间距

                        imageview.setPadding(10, 10, 10, 10);

                        String value = dataTable.Rows.get(i).getValue(stringTitles.get(j), "");
//                    if (i < stringTitles.size() - 1) {
                        if (value.equals("0")) {
                            imageview.setImageResource(R.drawable.post_1);
                        } else if (value.equals("1")) {
                            imageview.setImageResource(R.drawable.post_2);
                        } else if (value.equals("2")) {
                            imageview.setImageResource(R.drawable.post_3);
                        } else if (value.equals("3")) {
                            imageview.setImageResource(R.drawable.post_4);
                        } else if (value.equals("4")) {
                            imageview.setImageResource(R.drawable.post_5);
                        } else if (value.equals("考试")) {
                            imageview.setImageResource(R.drawable.post_test);
                        } else {
                            imageview.setImageResource(R.drawable.post_null);
                        }
                        imageview.setBackgroundResource(R.drawable.imageview_border);
//                        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
//                    }
                        viewHolder.linearLayout.addView(imageview, j + 2, layoutParams);
                    } else if (j == stringTitles.size() - 2) {      //备注
                        TextView textView = new TextView(PostSkillActivity.this);
                        textView.setLayoutParams(layoutParams);
//                        textView.setLineSpacing(1.2f, 1.2f);//设置行间距
                        textView.setTextColor(Color.BLACK);
                        textView.setGravity(Gravity.CENTER);
                        textView.setLines(1);
                        textView.setPadding(10, 10, 10, 10);
                        textView.setBackgroundResource(R.drawable.textview_border);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                        textView.setText(dataTable.Rows.get(i).getValue("备注", ""));
//                        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                        viewHolder.linearLayout.addView(textView, j + 2, layoutParams);
//                        viewHolder.linearLayout.addView(textView, stringTitles.size() - 1, layoutParams);
                    } else if (j == stringTitles.size() - 1) {                          //退货率
                        ImageView imageview = new ImageView(PostSkillActivity.this);
                        imageview.setLayoutParams(layoutParams);
//                        textView.setLineSpacing(1.2f, 1.2f);//设置行间距

                        imageview.setPadding(10, 10, 10, 10);

                        if (i < dataTable.Rows.size()) {
                            String value = dataTable.Rows.get(i).getValue(stringTitles.get(j), "");
                            if (value.equals("0")) {
                                imageview.setImageResource(R.drawable.sigin_green);
                            } else if (value.equals("1")) {
                                imageview.setImageResource(R.drawable.sigin_red);
                            } else {
                                imageview.setImageResource(R.drawable.sigin_null);
                            }
                            imageview.setBackgroundResource(R.drawable.imageview_border);
//                        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                            viewHolder.linearLayout.addView(imageview, j + 2, layoutParams);
//                            viewHolder.linearLayout.addView(imageview, stringTitles.size(), layoutParams);

                        }
                    }
                } else {
                    if (j < 0) {       //序号和姓名
                        TextView textView = new TextView(PostSkillActivity.this);
                        textView.setLayoutParams(layoutParams);
//                        textView.setLineSpacing(1.2f, 1.2f);//设置行间距
                        textView.setTextColor(Color.BLACK);
                        textView.setGravity(Gravity.CENTER);
                        textView.setLines(1);
                        textView.setPadding(10, 10, 10, 10);
                        textView.setBackgroundResource(R.drawable.textview_border);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                        if (j == -2) {
                            textView.setText((i + 1) + "");
                        } else if (j == -1) {
                            textView.setText(dataTable.Rows.get(i).getValue("worker_name", ""));
                        } else {
                            textView.setText(dataTable.Rows.get(i).getValue("备注", ""));
                        }
//                        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                        viewHolder.linearLayout.addView(textView, j + 2, layoutParams);
                    } else if (j < stringTitles.size() - 1) {                 //技能等级
                        ImageView imageview = new ImageView(PostSkillActivity.this);
                        imageview.setLayoutParams(layoutParams);
//                        textView.setLineSpacing(1.2f, 1.2f);//设置行间距

                        imageview.setPadding(10, 10, 10, 10);

                        String value = dataTable.Rows.get(i).getValue(stringTitles.get(j), "");
//                    if (i < stringTitles.size() - 1) {
                        if (value.equals("0")) {
                            imageview.setImageResource(R.drawable.post_1);
                        } else if (value.equals("1")) {
                            imageview.setImageResource(R.drawable.post_2);
                        } else if (value.equals("2")) {
                            imageview.setImageResource(R.drawable.post_3);
                        } else if (value.equals("3")) {
                            imageview.setImageResource(R.drawable.post_4);
                        } else if (value.equals("4")) {
                            imageview.setImageResource(R.drawable.post_5);
                        } else if (value.equals("考试")) {
                            imageview.setImageResource(R.drawable.post_test);
                        } else {
                            imageview.setImageResource(R.drawable.post_null);
                        }
                        imageview.setBackgroundResource(R.drawable.imageview_border);
//                        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
//                    }
                        viewHolder.linearLayout.addView(imageview, j + 2, layoutParams);
                    } else if (j == stringTitles.size() - 1) {      //备注
                        TextView textView = new TextView(PostSkillActivity.this);
                        textView.setLayoutParams(layoutParams);
//                        textView.setLineSpacing(1.2f, 1.2f);//设置行间距
                        textView.setTextColor(Color.BLACK);
                        textView.setGravity(Gravity.CENTER);
                        textView.setLines(1);
                        textView.setPadding(10, 10, 10, 10);
                        textView.setBackgroundResource(R.drawable.textview_border);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                        textView.setText(dataTable.Rows.get(i).getValue("备注", ""));
//                        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                        viewHolder.linearLayout.addView(textView, j + 2, layoutParams);
//                        viewHolder.linearLayout.addView(textView, stringTitles.size() - 1, layoutParams);
                    }
// else if (j == stringTitles.size() - 1) {                          //退货率
//                        ImageView imageview = new ImageView(PostSkillActivity.this);
//                        imageview.setLayoutParams(layoutParams);
////                        textView.setLineSpacing(1.2f, 1.2f);//设置行间距
//
//                        imageview.setPadding(10, 10, 10, 10);
//
//                        if (i < dataTable.Rows.size()) {
//                            String value = dataTable.Rows.get(i).getValue(stringTitles.get(j), "");
//                            if (value.equals("0")) {
//                                imageview.setImageResource(R.drawable.sigin_green);
//                            } else if (value.equals("1")) {
//                                imageview.setImageResource(R.drawable.sigin_red);
//                            } else {
//                                imageview.setImageResource(R.drawable.sigin_null);
//                            }
//                            imageview.setBackgroundResource(R.drawable.imageview_border);
////                        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
//                            viewHolder.linearLayout.addView(imageview, j + 2, layoutParams);
////                            viewHolder.linearLayout.addView(imageview, stringTitles.size(), layoutParams);
//
//                        }
//                    }
                }

            }
//            }
            return view;
        }
    }

    class ViewHolder {
        private LinearLayout linearLayout;
        private TextView textViewWorkerName;
        private TextView textViewPlugin;
        private TextView textViewAppeaPosition;
        private TextView textViewWaveSolder;
        private TextView textViewRepairWeld;
        private TextView textViewHighPressure;
        private TextView textViewIct;
        private TextView textViewAoi;
        private TextView textViewFunctionTest;
        private TextView textViewPackage;
        private TextView textViewUltrasonic;
        private TextView textViewSubBoardMachine;
        private TextView textViewDispenser;
        private TextView textView5S;
        private TextView textViewLeanProduction;
        private TextView textViewComment;
    }
}
