package dynsoft.xone.android.fragment.secondkanban;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/5/27.
 */

public class EachNumberFragment extends Fragment {
    private String[] titles = {"工序", "8:00-9:00", "9:00-10:00", "10:00-11:00", "11:00-12:00", "13:20-14:20",
            "14:20-15:20", "15:20-16:20", "16:20-17:20", "18:00-19:00", "19:00-20:00", "20:00-21:00", "21:00-22:00"};
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private TextView textView6;
    private TextView textView7;
    private TextView textView8;
    private TextView textView9;
    private TextView textView10;
    private TextView textView11;
    private TextView textView12;
    private TextView textView13;
    private TextView textViewUpdate;
    private ImageView imageViewUpdate;

    private ListView listView;
    private int id;
    private String workLine;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_each_number, null);

        Log.e("len", getActivity().getPackageName() + "onCreate");
        Intent intent = getActivity().getIntent();
        id = intent.getIntExtra("work_line_id", 0);
        workLine = intent.getStringExtra("work_line");

        textView1 = (TextView) view.findViewById(R.id.textview_1);
        textView2 = (TextView) view.findViewById(R.id.textview_2);
        textView3 = (TextView) view.findViewById(R.id.textview_3);
        textView4 = (TextView) view.findViewById(R.id.textview_4);
        textView5 = (TextView) view.findViewById(R.id.textview_5);
        textView6 = (TextView) view.findViewById(R.id.textview_6);
        textView7 = (TextView) view.findViewById(R.id.textview_7);
        textView8 = (TextView) view.findViewById(R.id.textview_8);
        textView9 = (TextView) view.findViewById(R.id.textview_9);
        textView10 = (TextView) view.findViewById(R.id.textview_10);
        textView11 = (TextView) view.findViewById(R.id.textview_11);
        textView12 = (TextView) view.findViewById(R.id.textview_12);
        textView13 = (TextView) view.findViewById(R.id.textview_13);
        listView = (ListView) view.findViewById(R.id.listview);
        textViewUpdate = (TextView) view.findViewById(R.id.textview_update);
        imageViewUpdate = (ImageView) view.findViewById(R.id.imageview_update);
        if (imageViewUpdate != null) {
            imageViewUpdate.setImageDrawable(getResources().getDrawable(R.drawable.fresh_blue));
            imageViewUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAnimation(imageViewUpdate);
                    initListView();
                    Log.e("len", "LISTTTTT");
                }
            });
        }
        if (textView1 != null) {
            textView1.setText(titles[0]);
        }
        if (textView2 != null) {
            textView2.setText(titles[1]);
        }
        if (textView3 != null) {
            textView3.setText(titles[2]);
        }
        if (textView4 != null) {
            textView4.setText(titles[3]);
        }
        if (textView5 != null) {
            textView5.setText(titles[4]);
        }
        if (textView6 != null) {
            textView6.setText(titles[5]);
        }
        if (textView7 != null) {
            textView7.setText(titles[6]);
        }
        if (textView8 != null) {
            textView8.setText(titles[7]);
        }
        if (textView9 != null) {
            textView9.setText(titles[8]);
        }
        if (textView10 != null) {
            textView10.setText(titles[9]);
        }
        if (textView11 != null) {
            textView11.setText(titles[10]);
        }
        if (textView12 != null) {
            textView12.setText(titles[11]);
        }
        if (textView13 != null) {
            textView13.setText(titles[12]);
        }
        initListView();
        return view;
    }

    private void setAnimation(ImageView imageViewFresh) {
        Animation animation = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
//        animation.setFillAfter(true);//设置为true，动画转化结束后被应用
        imageViewFresh.startAnimation(animation);//开始动画
    }

    private void initListView() {
        String sql = "exec p_get_second_kanban_second_data ?";
        Parameters p = new Parameters().add(1, workLine);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if(value.HasError) {
                    App.Current.showError(getActivity(), value.Error);
                    return;
                }
                if(value.Value != null && value.Value.Rows.size() > 0) {
                    EachNumberAdapter eachNumberAdapter = new EachNumberAdapter(value.Value);
                    listView.setAdapter(eachNumberAdapter);
                }
            }
        });
        textViewUpdate.setText("上次更新时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    class EachNumberAdapter extends BaseAdapter {
        private DataTable dataTable;

        public EachNumberAdapter(DataTable dataTable) {
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
            EachNumberViewHolder eachNumberViewHolder;
            if(view == null) {
                view = View.inflate(getActivity(), R.layout.item_each_number_fragment, null);
                eachNumberViewHolder = new EachNumberViewHolder();
                eachNumberViewHolder.textView1 = (TextView) view.findViewById(R.id.textview_1);
                eachNumberViewHolder.textView2 = (TextView) view.findViewById(R.id.textview_2);
                eachNumberViewHolder.textView3 = (TextView) view.findViewById(R.id.textview_3);
                eachNumberViewHolder.textView4 = (TextView) view.findViewById(R.id.textview_4);
                eachNumberViewHolder.textView5 = (TextView) view.findViewById(R.id.textview_5);
                eachNumberViewHolder.textView6 = (TextView) view.findViewById(R.id.textview_6);
                eachNumberViewHolder.textView7 = (TextView) view.findViewById(R.id.textview_7);
                eachNumberViewHolder.textView8 = (TextView) view.findViewById(R.id.textview_8);
                eachNumberViewHolder.textView9 = (TextView) view.findViewById(R.id.textview_9);
                eachNumberViewHolder.textView10 = (TextView) view.findViewById(R.id.textview_10);
                eachNumberViewHolder.textView11 = (TextView) view.findViewById(R.id.textview_11);
                eachNumberViewHolder.textView12 = (TextView) view.findViewById(R.id.textview_12);
                eachNumberViewHolder.textView13 = (TextView) view.findViewById(R.id.textview_13);
                view.setTag(eachNumberViewHolder);
            } else {
                eachNumberViewHolder = (EachNumberViewHolder) view.getTag();
            }
            String name = dataTable.Rows.get(i).getValue("name", "");
            int time1 = dataTable.Rows.get(i).getValue("08:00-09:00", 0);
            int time2 = dataTable.Rows.get(i).getValue("09:00-10:00", 0);
            int time3 = dataTable.Rows.get(i).getValue("10:00-11:00", 0);
            int time4 = dataTable.Rows.get(i).getValue("11:00-12:00", 0);
            int time5 = dataTable.Rows.get(i).getValue("13:20-14:20", 0);
            int time6 = dataTable.Rows.get(i).getValue("14:20-15:20", 0);
            int time7 = dataTable.Rows.get(i).getValue("15:20-16:20", 0);
            int time8 = dataTable.Rows.get(i).getValue("16:20-17:20", 0);
            int time9 = dataTable.Rows.get(i).getValue("18:00-19:00", 0);
            int time10 = dataTable.Rows.get(i).getValue("19:00-20:00", 0);
            int time11 = dataTable.Rows.get(i).getValue("20:00-21:00", 0);
            int time12 = dataTable.Rows.get(i).getValue("21:00-22:00", 0);
            eachNumberViewHolder.textView1.setText(name);
            eachNumberViewHolder.textView2.setText(String.valueOf(time1));
            eachNumberViewHolder.textView3.setText(String.valueOf(time2));
            eachNumberViewHolder.textView4.setText(String.valueOf(time3));
            eachNumberViewHolder.textView5.setText(String.valueOf(time4));
            eachNumberViewHolder.textView6.setText(String.valueOf(time5));
            eachNumberViewHolder.textView7.setText(String.valueOf(time6));
            eachNumberViewHolder.textView8.setText(String.valueOf(time7));
            eachNumberViewHolder.textView9.setText(String.valueOf(time8));
            eachNumberViewHolder.textView10.setText(String.valueOf(time9));
            eachNumberViewHolder.textView11.setText(String.valueOf(time10));
            eachNumberViewHolder.textView12.setText(String.valueOf(time11));
            eachNumberViewHolder.textView13.setText(String.valueOf(time12));
            return view;
        }

        class EachNumberViewHolder {
            private TextView textView1;
            private TextView textView2;
            private TextView textView3;
            private TextView textView4;
            private TextView textView5;
            private TextView textView6;
            private TextView textView7;
            private TextView textView8;
            private TextView textView9;
            private TextView textView10;
            private TextView textView11;
            private TextView textView12;
            private TextView textView13;
        }
    }
}
