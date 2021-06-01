package dynsoft.xone.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.sopactivity.CardRegistrationActivity;
import dynsoft.xone.android.sopactivity.ProductionKanbanActivity;
import dynsoft.xone.android.zoom.PhotoView;
import dynsoft.xone.android.zoom.ViewPagerFixed;

/**
 * Created by Administrator on 2017/12/7.
 */

public class PreSopActivity extends Activity {
    private ViewPagerFixed viewPager;
    private int curPosition;
    private ListView listView;
    private ProgressBar progressBar;
    private ArrayList<String> image_urls;

    private TextView text_1;
    private TextView text_2;
    private TextView text_3;
    private boolean hasException;
    private MyAdapter myAdapter;
    private String taskOrderCode;  //传递过来的工单
    private ArrayList<String> chooseImages;
    private ItemSopAdapter itemSopAdapter;
    private TextView textview_1;        //选择SOP

//    private ArrayList<String> exception_types;
//    private String exception_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_sop_pre);
        viewPager = (ViewPagerFixed) findViewById(R.id.view_pager);
        textview_1 = (TextView) findViewById(R.id.textview_1);
        if (textview_1 != null) {
            textview_1.setTextColor(getResources().getColor(R.color.black));
            textview_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeImages();         //改变image_urls
                }
            });
        }

        text_1 = (TextView) findViewById(R.id.text_1);
        text_2 = (TextView) findViewById(R.id.text_2);
        text_3 = (TextView) findViewById(R.id.text_3);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        final Intent intent = getIntent();
        taskOrderCode = intent.getStringExtra("task_order_code");
        image_urls = new ArrayList<>();
        getImageUrl(taskOrderCode);
        initTextDatas();
//        initViewPager(taskOrderCode);
    }

    private void changeImages() {
        String sql = "exec fm_choose_a_sop_url ?";
        Parameters p = new Parameters().add(1, taskOrderCode);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(PreSopActivity.this, value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    createPopupWindow(value.Value);
                }
            }
        });
    }

    private void createPopupWindow(final DataTable value) {
        final PopupWindow popupWindow = new PopupWindow();
        View view = View.inflate(PreSopActivity.this, R.layout.popupwindow_sopactivity, null);
        ListView listView = (ListView) view.findViewById(R.id.listview);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        itemSopAdapter = new ItemSopAdapter(value);
        listView.setAdapter(itemSopAdapter);
        chooseImages = new ArrayList<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String imag_url = value.Rows.get(i).getValue("imag_url", "");
                if (chooseImages.contains(imag_url)) {
                    chooseImages.remove(imag_url);
                } else {
                    chooseImages.add(imag_url);
                }

                itemSopAdapter.notifyDataSetChanged();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chooseImages.size() > 0) {
                    initViewPager(chooseImages);
                    textview_1.setText(((curPosition % chooseImages.size()) + 1) + "/" + chooseImages.size());
                    popupWindow.dismiss();
                } else {
                    App.Current.toastInfo(PreSopActivity.this, "请先选择SOP页");
                }
            }
        });

        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 重置PopupWindow高度
        int screenHeigh = getResources().getDisplayMetrics().heightPixels;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        popupWindow.setHeight(Math.round(screenHeigh * 0.7f));
        popupWindow.setWidth(Math.round(screenWidth * 0.4f));
        popupWindow.setFocusable(true);
//                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.style_divider_color));
        popupWindow.showAtLocation(listView, Gravity.CENTER, 20, 30);
    }

    private void initTextDatas() {
        String sql = "SELECT t1.code  item_code, t0.plan_quantity FROM dbo.mm_wo_task_order_head t0 LEFT JOIN mm_item t1 ON t0.item_id = t1.id WHERE t0.code = ?";
        Parameters p = new Parameters().add(1, taskOrderCode);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PreSopActivity.this, value.Error);
                } else {
                    if (value.Value != null) {
                        String item_code = value.Value.getValue("item_code", "");
                        int plan_quantity = value.Value.getValue("plan_quantity", new BigDecimal(0)).intValue();
                        text_1.setText(taskOrderCode);
                        text_2.setText(item_code);
                        text_3.setText(String.valueOf(plan_quantity));
                    }
                }
            }
        });
    }

    private String removeSmallNumber(String plan_quantity) {
        if (plan_quantity.indexOf(".") > 0) {
            plan_quantity = plan_quantity.replaceAll("0+?$", "");//去掉多余的0
            plan_quantity = plan_quantity.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return plan_quantity;
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        progressBar.setVisibility(View.VISIBLE);
//        image_urls = intent.getStringArrayListExtra("image_urls");
//        checkLightStatus();
//        initTextDatas();
////        initViewPager(id, page_count);
//        initViewPager(image_urls);
//        myAdapter.reflash(image_urls);
//        initSlidingMenu();
//    }

    private void getImageUrl(final String taskOrderCode) {
        String sql = "exec fm_get_image_by_task_order_code ?";
        Parameters p = new Parameters().add(1, taskOrderCode);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastError(PreSopActivity.this, value.Error);
                } else {
                    if (value.Value.Rows.size() > 0) {
                        for (int i = 0; i < value.Value.Rows.size(); i++) {
                            image_urls.add(value.Value.Rows.get(i).getValue("image_url", ""));
                        }
                        Log.e("len", taskOrderCode + "**" + image_urls.toString());
                        initViewPager(image_urls);
                    } else {
                        App.Current.toastError(PreSopActivity.this, "SOP加载失败!");
                        finish();
                    }
                }
            }
        });
    }

    private void initViewPager(final ArrayList<String> dataTable) {
        myAdapter = new MyAdapter(dataTable);
        viewPager.setOffscreenPageLimit(1);// 预加载
//        viewPager.setCurrentItem(0);
        viewPager.setAdapter(myAdapter);
        progressBar.setVisibility(View.GONE);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                curPosition = position;
                textview_1.setText(((curPosition % dataTable.size()) + 1) + "/" + dataTable.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                if (curPosition == image_urls.size() - 1 && state == 1) {
//                    viewPager.setCurrentItem(0, true);
//                }
            }
        });
    }

    class MyAdapter extends PagerAdapter {
        private ArrayList<String> image_urls;

        public MyAdapter(ArrayList<String> image_urls) {
            this.image_urls = image_urls;
        }

        public void reflash(ArrayList<String> image_urls) {
            this.image_urls = image_urls;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (image_urls == null) {
                return 0;
            } else {
                return image_urls.size() <= 1 ? image_urls.size() : Short.MAX_VALUE;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(PreSopActivity.this, R.layout.sop_viewpager_item, null);
//            view.setTag(position);
            VideoView videoView = (VideoView) view.findViewById(R.id.videoview);
            PhotoView photoView = (PhotoView) view.findViewById(R.id.photoview);
//            ImageView photoView = (ImageView) view.findViewById(R.id.photoview);
            if (false) {
//                handler.removeCallbacks(runnable);
//                //如果是视频
////                Uri parse = Uri.parse("http://192.168.127.6:8080/aa.mp4");
////                String path = "/storage/sdcard1/DCIM/VID_20170908_174048.mp4";
//                String path = "http://192.168.127.6:8080/aa.mp4";
//                Uri parse = Uri.parse(path);
//                Log.e("len", "path: " + path);
//
//                videoView.setVisibility(View.VISIBLE);
//
//                //设置视频控制器
//                videoView.setMediaController(new MediaController(SopActivity.this));
//
//                videoView.setVideoURI(parse);
//                //播放完成回调
////                videoView.setOnCompletionListener( new MyPlayerOnCompletionListener());
//                videoView.start();
            } else {
                //如果是图片
                Picasso.with(PreSopActivity.this).load(image_urls.get(position % image_urls.size())).memoryPolicy(MemoryPolicy.NO_CACHE).error(getResources().getDrawable(R.drawable.null_pic)).into(photoView);
//                photoView.setImageDrawable(getResources().getDrawable(picResource.get(position)));
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
//            View view = (View)object;
//            int currentPage = getCurrentPagerIdx(); // Get current page index
//            if(currentPage == (Integer)view.getTag()){
//                return POSITION_NONE;
//            }else{
//                return POSITION_UNCHANGED;
//            }
            return POSITION_NONE;
        }
    }

    private int getCurrentPagerIdx() {
        return curPosition;
    }

    public Bitmap getBitmapFromByte(byte[] temp) {
        if (temp != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        } else {
            return null;
        }
    }

    class ListAdapter extends BaseAdapter {
        private ArrayList<String> classesName;

        public ListAdapter(ArrayList<String> classesName) {
            this.classesName = classesName;
        }

        @Override
        public int getCount() {
            return classesName.size();
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
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = View.inflate(getBaseContext(), R.layout.sop_activity_listview, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view.findViewById(R.id.textview);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            String[] split = classesName.get(position).split("[.]");
            if (split.length > 1) {
                String justClassName = split[split.length - 1];
                String s = justClassName.toLowerCase();
                int string = getResources().getIdentifier(s, "string", getPackageName());
                viewHolder.textView.setText(getResources().getString(string));
            }
//            viewHolder.textView.setText(App.Current.listTitles.get(position));
            return view;
        }
    }

    class ViewHolder {
        private TextView textView;
    }

    class ItemSopAdapter extends BaseAdapter {
        private DataTable dataTable;

        public ItemSopAdapter(DataTable dataTable) {
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
            ItemSopViewHolder itemSopViewHolder;
            if (view == null) {
                view = View.inflate(PreSopActivity.this, R.layout.item_listview_popupwindow_sop, null);
                itemSopViewHolder = new ItemSopViewHolder();
                itemSopViewHolder.textView1 = (TextView) view.findViewById(R.id.textview_1);
                itemSopViewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout);
                itemSopViewHolder.textView2 = (TextView) view.findViewById(R.id.textview_2);
                itemSopViewHolder.textView3 = (TextView) view.findViewById(R.id.textview_3);
                view.setTag(itemSopViewHolder);
            } else {
                itemSopViewHolder = (ItemSopViewHolder) view.getTag();
            }
            String sheet_name = dataTable.Rows.get(i).getValue("sheet_name", "");
            String attribute1 = dataTable.Rows.get(i).getValue("attribute1", "");
            String attribute2 = dataTable.Rows.get(i).getValue("attribute2", "");
            String image_url = dataTable.Rows.get(i).getValue("imag_url", "");
            itemSopViewHolder.textView1.setText(sheet_name);
            itemSopViewHolder.textView2.setText(attribute1);
            itemSopViewHolder.textView3.setText(attribute2);

            if (chooseImages.contains(image_url)) {
                itemSopViewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.deepskyblue));
            } else {
                itemSopViewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.white));
            }
            return view;
        }
    }

    class ItemSopViewHolder {
        private LinearLayout linearLayout;
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
    }

    private ArrayList<String> getClasses(Context mContext, String packageName) {
        ArrayList<String> classes = new ArrayList<String>();
        classes.add("dynsoft.xone.android.sopactivity.CardRegistrationActivity");
        classes.add("dynsoft.xone.android.sopactivity.LoadVideoActivity");
        classes.add("dynsoft.xone.android.sopactivity.LocationCheckActivity");
        classes.add("dynsoft.xone.android.sopactivity.OQCKanbanActivity");
        classes.add("dynsoft.xone.android.sopactivity.ProductionKanbanActivity");
        classes.add("dynsoft.xone.android.sopactivity.CheckReportActivity");
        classes.add("dynsoft.xone.android.sopactivity.ScanTestActivity");
        classes.add("dynsoft.xone.android.sopactivity.SetParameterActivity");
        classes.add("dynsoft.xone.android.sopactivity.EmployeeMessageActivity");
        classes.add("dynsoft.xone.android.sopactivity.WithdrawActivity");
//        try {
//            String packageCodePath = mContext.getPackageCodePath();
//            DexFile df = new DexFile(packageCodePath);
//            String regExp = "^" + packageName + ".\\w+$";
//            for (Enumeration iter = df.entries(); iter.hasMoreElements(); ) {
//                String className = (String) iter.nextElement();
//                if (className.matches(regExp)) {
//                    classes.add(className);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return classes;
    }
}
