package dynsoft.xone.android.fragment.department;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.DTDHandler;

import java.math.BigDecimal;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2018/4/10.
 * OQC����ʵʱ��������
 */

public class OqcFragment extends Fragment {
    private ListView listView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_oqc, null);
        listView = (ListView) view.findViewById(R.id.listview_oqc_fragment);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        initView();
        return view;
    }

    private void initView() {
        progressBar.setVisibility(View.VISIBLE);
        String sql = "exec p_fm_get_first_department_and";
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(getActivity(), value.Error);
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (value.Value != null) {
                    progressBar.setVisibility(View.GONE);
                    OqcAdapter oqcAdapter = new OqcAdapter(value.Value);
                    listView.setAdapter(oqcAdapter);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private String removeSmallNumber(String plan_quantity) {
        if (plan_quantity.indexOf(".") > 0) {
            plan_quantity = plan_quantity.replaceAll("0+?$", "");//ȥ�������0
            plan_quantity = plan_quantity.replaceAll("[.]$", "");//�����һλ��.��ȥ��
        }
        return plan_quantity;
    }

    class OqcAdapter extends BaseAdapter {
        private DataTable dataTable;

        public OqcAdapter(DataTable dataTable) {
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
            final OqcViewHolder oqcViewHolder;
            if (view == null) {
                view = View.inflate(getActivity(), R.layout.item_oqc_fragment, null);
                oqcViewHolder = new OqcViewHolder();
                oqcViewHolder.textView1 = (TextView) view.findViewById(R.id.text_view_1);
                oqcViewHolder.textView2 = (TextView) view.findViewById(R.id.text_view_2);
                oqcViewHolder.textView3 = (TextView) view.findViewById(R.id.text_view_3);
                oqcViewHolder.textView4 = (TextView) view.findViewById(R.id.text_view_4);
                oqcViewHolder.textView5 = (TextView) view.findViewById(R.id.text_view_5);
                oqcViewHolder.textView6 = (TextView) view.findViewById(R.id.text_view_6);
                view.setTag(oqcViewHolder);
            } else {
                oqcViewHolder = (OqcViewHolder) view.getTag();
            }
            //�ܼ�������
            int test_count = dataTable.Rows.get(i).getValue("test_count", 0);
            //���ϸ�����
            int unqualified_count = dataTable.Rows.get(i).getValue("unqualified_count", 0);
            final String lineName = dataTable.Rows.get(i).getValue("line_name", "");
            //����Ա
            String tester = dataTable.Rows.get(i).getValue("tester", "");
            String line_leader = dataTable.Rows.get(i).getValue("line_leader", "");
            long rownumber = dataTable.Rows.get(i).getValue("rownumber", 0L);
            oqcViewHolder.textView1.setText(lineName);
            oqcViewHolder.textView2.setText(removeSmallNumber(String.valueOf(test_count)));
            oqcViewHolder.textView3.setText(removeSmallNumber(String.valueOf(unqualified_count)));
            oqcViewHolder.textView4.setText(tester);
            oqcViewHolder.textView5.setText("");
            oqcViewHolder.textView6.setText(line_leader);
            oqcViewHolder.textView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //�ͼ���
                    toastOqcQiantity(lineName, "");
                }
            });
            if(unqualified_count > 0) {
                badQiantity(lineName, "���ϸ�", oqcViewHolder.textView5);
            }
            oqcViewHolder.textView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //���ϸ���
                    toastBadQiantity(lineName, "���ϸ�");
//                    toastOqcQiantity(lineName, "���ϸ�");
                }
            });
//            oqcViewHolder.textView5.setText("");
//            if (rownumber > 0) {
//                oqcViewHolder.textView5.setBackgroundColor(getResources().getColor(R.color.red));
//            } else {
//                oqcViewHolder.textView5.setBackgroundColor(getResources().getColor(R.color.megmeet_green));
//            }
            return view;
        }
    }

    private void badQiantity(String lineName, String status, final TextView textView) {
        String sql = "exec fm_first_oqc_task_item_datas ?,?";
        Parameters p = new Parameters().add(1, lineName).add(2, status);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                } else if (value.Value != null && value.Value.Rows.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    StringBuilder textString = new StringBuilder();
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        //�ͼ����
                        String name = value.Value.Rows.get(i).getValue("name", "");
                        //�ͼ���
                        float test_quantity = value.Value.Rows.get(i).getValue("test_quantity", new BigDecimal(0)).floatValue();
                        //����ԭ��
                        String others = value.Value.Rows.get(i).getValue("Others", "");
                        stringBuilder.append((i + 1) + "���ͼ����:" + name +
                                "�ͼ���" + removeSmallNumber(String.valueOf(test_quantity)) + "����" + others + "\n\n");
                        String[] split = others.split("��");
                        String substring = split[0].substring(2);
                        textString.append((i + 1) + "��" + name + "," + substring + "\n");
                    }
                    textView.setText(textString.toString());
                }
            }
        });
    }

    private void toastOqcQiantity(String lineName, String status) {
        progressBar.setVisibility(View.VISIBLE);
        String sql = "exec fm_first_oqc_task_item_datas ?,?";
        Parameters p = new Parameters().add(1, lineName).add(2, status);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    progressBar.setVisibility(View.GONE);
                    App.Current.toastError(getActivity(), value.Error);
                } else if (value.Value != null) {
                    progressBar.setVisibility(View.GONE);
                    ToastPopupWindow(value.Value);
                } else {
                    progressBar.setVisibility(View.GONE);
                    App.Current.toastError(getActivity(), "��ǰ�ȱ��ͼ�����Ϊ0");
                }
            }
        });
    }

    private void ToastPopupWindow(DataTable value) {
        final PopupWindow popupWindow = new PopupWindow();
        View view = View.inflate(getActivity(), R.layout.popupwindow_wip_fragment, null);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        ListView listView = (ListView) view.findViewById(R.id.listview);
        for (int i = 0; i < value.Rows.size(); i++) {
            if (textView != null) {
                textView.setText(value.Rows.get(0).getValue("line_name", "") + "���ͼ���");
            }
        }

        ItemAdapter itemAdapter = new ItemAdapter(value);
        listView.setAdapter(itemAdapter);

        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // ����PopupWindow�߶�
        int screenHeigh = getResources().getDisplayMetrics().widthPixels;
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(Math.round(screenHeigh * 0.9f));
        popupWindow.setFocusable(true);
//                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.style_divider_color));
        popupWindow.showAtLocation(listView, Gravity.CENTER, 20, 30);
    }

    private void toastBadQiantity(String workLine, String status) {
        String sql = "exec fm_first_oqc_task_item_datas ?,?";
        Parameters p = new Parameters().add(1, workLine).add(2, status);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(getActivity(), value.Error);
                } else if (value.Value != null && value.Value.Rows.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        //�ͼ����
                        String name = value.Value.Rows.get(i).getValue("name", "");
                        //�ͼ���
                        float test_quantity = value.Value.Rows.get(i).getValue("test_quantity", new BigDecimal(0)).floatValue();
                        //����ԭ��
                        String others = value.Value.Rows.get(i).getValue("Others", "");
                        stringBuilder.append((i + 1) + "���ͼ����:" + name +
                                "�ͼ���" + removeSmallNumber(String.valueOf(test_quantity)) + "����" + others + "\n\n");
                    }
                    App.Current.showInfo(getActivity(), stringBuilder.toString());
                } else {
                    App.Current.toastError(getActivity(), "����Ĳ��ϸ�����Ϊ0");
                }
            }
        });
    }

    class OqcViewHolder {
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private TextView textView4;
        private TextView textView5;
        private TextView textView6;
    }

    class ItemAdapter extends BaseAdapter {
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
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ItemViewHolder itemViewHolder;
            if (view == null) {
                view = View.inflate(getActivity(), R.layout.popupwindow_item_oqc_fragment, null);
                itemViewHolder = new ItemViewHolder();
                itemViewHolder.textView1 = (TextView) view.findViewById(R.id.textview_1);
                itemViewHolder.textView2 = (TextView) view.findViewById(R.id.textview_2);
                itemViewHolder.textView3 = (TextView) view.findViewById(R.id.textview_3);
                itemViewHolder.textView4 = (TextView) view.findViewById(R.id.textview_4);
                view.setTag(itemViewHolder);
            } else {
                itemViewHolder = (ItemViewHolder) view.getTag();
            }
            String task_order_code = dataTable.Rows.get(i).getValue("task_order_code", "");
            String item_code = dataTable.Rows.get(i).getValue("item_code", "");
            String item_name = dataTable.Rows.get(i).getValue("item_name", "");
            float test_quantity = dataTable.Rows.get(i).getValue("test_quantity", new BigDecimal(0)).floatValue();
            itemViewHolder.textView1.setText("������" + task_order_code);
            itemViewHolder.textView2.setText("���ϱ��룺" + item_code);
            itemViewHolder.textView3.setText("���ͣ�" + item_name);
            itemViewHolder.textView4.setText("������" + String.valueOf(Math.round(test_quantity)));
            return view;
        }
    }

    class ItemViewHolder {
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private TextView textView4;
    }
}
