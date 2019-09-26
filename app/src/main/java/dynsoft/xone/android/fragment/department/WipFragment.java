package dynsoft.xone.android.fragment.department;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.sopactivity.LocationCheckActivity;

/**
 * Created by Administrator on 2018/4/10.
 * WIP数据完成情况
 */

public class WipFragment extends Fragment {
    private ListView listView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_wip, null);
        listView = (ListView) view.findViewById(R.id.listview_wip);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        Log.e("len", "WIPOnCreate");
        initListViewData();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("len", "WIPOnStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("len", "WIPOnResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("len", "WIPOnPause");
    }

    private void initListViewData() {
        progressBar.setVisibility(View.VISIBLE);
        String sql = "exec fm_get_first_department_wip_data_and";
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getActivity(), value.Error);
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    WipAdapter wipAdapter = new WipAdapter(value.Value);
                    listView.setAdapter(wipAdapter);
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    class WipAdapter extends BaseAdapter {
        private DataTable dataTable;

        public WipAdapter(DataTable dataTable) {
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            WipViewHolder wipViewHolder;
            if (view == null) {
                view = View.inflate(getActivity(), R.layout.item_wip_fragment, null);
                wipViewHolder = new WipViewHolder();
                wipViewHolder.textView1 = (TextView) view.findViewById(R.id.text_view_1);
                wipViewHolder.textView2 = (TextView) view.findViewById(R.id.text_view_2);
                wipViewHolder.textView3 = (TextView) view.findViewById(R.id.text_view_3);
                wipViewHolder.textView4 = (TextView) view.findViewById(R.id.text_view_4);
                wipViewHolder.textView5 = (TextView) view.findViewById(R.id.text_view_5);
                wipViewHolder.textView6 = (TextView) view.findViewById(R.id.text_view_6);
                wipViewHolder.textView7 = (TextView) view.findViewById(R.id.text_view_7);
                view.setTag(wipViewHolder);
            } else {
                wipViewHolder = (WipViewHolder) view.getTag();
            }
            final String work_name = dataTable.Rows.get(i).getValue("work_name", "");
            int first_month = dataTable.Rows.get(i).getValue("first_month", 0);
            int second_month = dataTable.Rows.get(i).getValue("second_month", 0);
            int in_one_handred = dataTable.Rows.get(i).getValue("in_one_handred", 0);
            int in_two_handred = dataTable.Rows.get(i).getValue("in_two_handred", 0);
            int out_two_handred = dataTable.Rows.get(i).getValue("out_two_handred", 0);
            if ("A".equals(work_name)) {
                wipViewHolder.textView1.setText("PA1");
            } else if ("B".equals(work_name)) {
                wipViewHolder.textView1.setText("PA8");
            } else if("C".equals(work_name)) {
                wipViewHolder.textView1.setText("PLC");
            }else {
                wipViewHolder.textView1.setText("PA" + work_name);
            }
            wipViewHolder.textView2.setText(String.valueOf(first_month));
            wipViewHolder.textView3.setText(String.valueOf(second_month));
            wipViewHolder.textView4.setText(String.valueOf(in_one_handred));
            wipViewHolder.textView5.setText(String.valueOf(in_two_handred));
            wipViewHolder.textView6.setText(String.valueOf(out_two_handred));
            int count = first_month + second_month + in_one_handred + in_two_handred + out_two_handred;
            wipViewHolder.textView7.setText(String.valueOf(count));

            wipViewHolder.textView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toastTaskOrderCode(work_name, 0, 30);
                }
            });
            wipViewHolder.textView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toastTaskOrderCode(work_name, 30, 60);
                }
            });
            wipViewHolder.textView4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toastTaskOrderCode(work_name, 60, 100);
                }
            });
            wipViewHolder.textView5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toastTaskOrderCode(work_name, 100, 200);
                }
            });
            wipViewHolder.textView6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toastTaskOrderCode(work_name, 200, -1);
                }
            });
            return view;
        }
    }

    private void toastTaskOrderCode(final String work_name, final int startTime, final int endTime) {
        progressBar.setVisibility(View.VISIBLE);
        String sql = "exec fm_get_first_department_wip_item_and ?,?,?";
        final Parameters p = new Parameters().add(1, work_name).add(2, startTime).add(3, endTime);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getActivity(), value.Error);
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    ToasPopupwindow(work_name, value.Value, startTime, endTime);
                    progressBar.setVisibility(View.GONE);
                } else {
                    App.Current.toastError(getActivity(), "数量为0");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void ToasPopupwindow(String work_name, DataTable dataTable, int startTime, int endTime) {
        final PopupWindow popupWindow = new PopupWindow();
        View view = View.inflate(getActivity(), R.layout.popupwindow_wip_fragment, null);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        ListView listView = (ListView) view.findViewById(R.id.listview);

        String titleWorkName;
        if ("A".equals(work_name)) {
            titleWorkName = "PA1";
        } else if ("B".equals(work_name)) {
            titleWorkName = "PA8";
        } else if("C".equals(work_name)) {
            titleWorkName = "PLC";
        }else {
            titleWorkName = "PA" + work_name;
        }



        if (endTime > 1) {
            textView.setText(titleWorkName + "线" + startTime + "-" + endTime + "天WIP完成情况");
        } else {
            textView.setText(titleWorkName + "线" + startTime + "天以上WIP完成情况");
        }
        ItemAdapter itemAdapter = new ItemAdapter(dataTable);
        listView.setAdapter(itemAdapter);


        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 重置PopupWindow高度
        int screenHeigh = getResources().getDisplayMetrics().widthPixels;
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(Math.round(screenHeigh * 0.8f));
        popupWindow.setFocusable(true);
//                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.style_divider_color));
        popupWindow.showAtLocation(listView, Gravity.CENTER, 20, 30);
    }

    class WipViewHolder {
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private TextView textView4;
        private TextView textView5;
        private TextView textView6;
        private TextView textView7;
    }

    private class ItemAdapter extends BaseAdapter {
        private DataTable dataTable;

        public ItemAdapter(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        @Override
        public int getCount() {
            return dataTable.Rows.size();
        }

        @Override
        public Object getItem(int i) {
            return dataTable.Rows.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ItemViewHolder itemViewHolder;
            if (view == null) {
                view = View.inflate(getActivity(), R.layout.item_popupwindow_wip_fragment, null);
                itemViewHolder = new ItemViewHolder();
                itemViewHolder.textView1 = (TextView) view.findViewById(R.id.textview_1);
                itemViewHolder.textView2 = (TextView) view.findViewById(R.id.textview_2);
                itemViewHolder.textView3 = (TextView) view.findViewById(R.id.textview_3);
                view.setTag(itemViewHolder);
            } else {
                itemViewHolder = (ItemViewHolder) view.getTag();
            }
            float treasury_quantity = dataTable.Rows.get(i).getValue("treasury_quantity", new BigDecimal(0)).floatValue();
            int round = Math.round(treasury_quantity);
            itemViewHolder.textView1.setText((i + 1) + "、 工单：" + dataTable.Rows.get(i).getValue("task_order_code", ""));
            itemViewHolder.textView2.setText("机型：" + dataTable.Rows.get(i).getValue("item_name", ""));
            itemViewHolder.textView3.setText("未缴库数量：" + round);

            return view;
        }
    }

    class ItemViewHolder {
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
    }
}
