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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import dalvik.system.DexFile;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.sopactivity.CardRegistrationActivity;
import dynsoft.xone.android.sopactivity.LocationCheckActivity;
import dynsoft.xone.android.sopactivity.ProductionKanbanActivity;
import dynsoft.xone.android.zoom.PhotoView;
import dynsoft.xone.android.zoom.ViewPagerFixed;

/**
 * Created by Administrator on 2017/12/7.
 */

public class SopActivity extends Activity {
    private ViewPagerFixed viewPager;
    private int curPosition;
    private ListView listView;
    private ImageView imageviewLight;
    private ProgressBar progressBar;
    private TextView textview_1;

    private TextView text_1;
    private TextView text_2;
    private TextView text_3;
    private TextView text_4;
    private TextView text_5;
    private LinearLayout linearlayout_worker;     //修改工作人员点击事件
    private boolean hasException;
    private SlidingMenu menu;
    private ArrayList<String> image_urls;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    private MyAdapter myAdapter;
    private String name;
    private String code;
    private String work_line;
    private int work_line_id;
    private long head_id;
    private ArrayList<String> chooseImages;
    private ItemSopAdapter itemSopAdapter;

//    private ArrayList<String> exception_types;
//    private String exception_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_sop);
//        hideBottomUIMenu();
        sharedPreferences = getApplication().getSharedPreferences("sop", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();
        head_id = sharedPreferences.getLong("head_id", 0L);
        viewPager = (ViewPagerFixed) findViewById(R.id.view_pager);
        imageviewLight = (ImageView) findViewById(R.id.imageview_light);
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
        text_4 = (TextView) findViewById(R.id.text_4);
        text_5 = (TextView) findViewById(R.id.text_5);
        linearlayout_worker = (LinearLayout) findViewById(R.id.linearlayout_worker);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        final Intent intent = getIntent();
        image_urls = intent.getStringArrayListExtra("image_urls");

        if (linearlayout_worker != null) {
            linearlayout_worker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(SopActivity.this, SetParameterActivity.class);
//                    startActivity(intent);
                    Intent intent1 = new Intent(SopActivity.this, MesLightActivity.class);
//                    intent1.putExtra("has_exception", hasException);
//                    intent1.putStringArrayListExtra("exception_types", exception_types);
                    startActivity(intent1);
                }
            });
        }

        imageviewLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SopActivity.this, MesLightActivity.class);
//                intent1.putExtra("has_exception", hasException);
//                intent1.putStringArrayListExtra("exception_types", exception_types);
                startActivity(intent1);
            }
        });

        checkLightStatus();      //判断当前是否有异常未处理
        initTextDatas();
        initViewPager(image_urls);
        initSlidingMenu();
    }

    private void changeImages() {
        String sql = "exec fm_choose_a_sop_url ?";
        Parameters p = new Parameters().add(1, head_id);
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.showError(SopActivity.this, value.Error);
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
        View view = View.inflate(SopActivity.this, R.layout.popupwindow_sopactivity, null);
        ListView listView = (ListView) view.findViewById(R.id.listview);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        itemSopAdapter = new ItemSopAdapter(value);
        listView.setAdapter(itemSopAdapter);
        chooseImages = new ArrayList<String>();
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
            	if(chooseImages.size() > 0) {
            	    initViewPager(chooseImages);
            	    textview_1.setText(((curPosition % chooseImages.size()) + 1) + "/" + chooseImages.size());
            	    popupWindow.dismiss();
            	} else {
            	    App.Current.toastInfo(SopActivity.this, "请先选择SOP页");
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

    private void checkLightStatus() {
        String sql = "exec p_fm_light_exception_get_and ?";
        Parameters p = new Parameters().add(1, getMacAddress());
        Log.e("len", "GETMAC:" + getMacAddress());
        App.Current.DbPortal.ExecuteDataTableAsync("core_and", sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataTable> value = Value;
                if (value.HasError) {
                    App.Current.toastInfo(SopActivity.this, value.Error);
                    return;
                }
                if (value.Value != null && value.Value.Rows.size() > 0) {
                    for (int i = 0; i < value.Value.Rows.size(); i++) {
                        String status = value.Value.Rows.get(i).getValue("status", "");
                        if ("呼叫".equals(status)) {              //有异常申请
                            hasException = true;
                            imageviewLight.setSelected(true);
                        }
                    }
                    if (!hasException) {
                        imageviewLight.setSelected(false);
                    }
                } else {
                    hasException = false;
                    imageviewLight.setSelected(false);
                }
            }
        });
    }

    private String getMacAddress() {
        String strMacAddr = null;
        try {
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip)
                    .getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return strMacAddr;
    }

    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    protected static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Enumeration en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = (InetAddress) en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }
                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }


    private void initTextDatas() {
        String work_code = sharedPreferences.getString("work_code", "");
        String sql = "select * from fm_worker where code = ?";
        Parameters p = new Parameters().add(1, work_code);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(SopActivity.this, value.Error);
                    return;
                }
                if (value.Value != null) {
                    name = value.Value.getValue("name", "");
                    code = value.Value.getValue("code", "");
                    text_3.setText(code + "-" + name);
                }
            }
        });
        String segment = sharedPreferences.getString("segment", "");
        String task_order = sharedPreferences.getString("task_order", "");
        String production = sharedPreferences.getString("item_code", "");
        String plan_quantity = sharedPreferences.getString("plan_quantity", "");
        text_1.setText(task_order);
        text_2.setText(removeSmallNumber(plan_quantity));
        text_4.setText(production);
        text_5.setText(segment);
    }

    private String removeSmallNumber(String plan_quantity) {
        if (plan_quantity.indexOf(".") > 0) {
            plan_quantity = plan_quantity.replaceAll("0+?$", "");//去掉多余的0
            plan_quantity = plan_quantity.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return plan_quantity;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        progressBar.setVisibility(View.VISIBLE);
        image_urls = intent.getStringArrayListExtra("image_urls");
        checkLightStatus();
        initTextDatas();
//        initViewPager(id, page_count);
        initViewPager(image_urls);
        myAdapter.reflash(image_urls);
        initSlidingMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int sopposition = sharedPreferences.getInt("sopposition", 0);
        viewPager.setCurrentItem(sopposition);
        checkLightStatus();
        Log.e("len", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        edit.putInt("sopposition", curPosition);
        edit.commit();

    }

    private void initSlidingMenu() {
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.RIGHT);
        // 设置触摸屏幕的模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setShadowWidthRes(R.dimen.activity_sop_margin);
//        menu.setShadowDrawable(R.drawable.shadow);
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //窗口的宽度
        int screenWidth = dm.widthPixels;
        // 设置滑动菜单视图的宽度
        menu.setBehindOffset(screenWidth * 3 / 4);
        // 设置渐入渐出效果的值
        menu.setFadeDegree(0.35f);
        /**
         * SLIDING_WINDOW will include the Title/ActionBar in the content
         * section of the SlidingMenu, while SLIDING_CONTENT does not.
         */
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //为侧滑菜单设置布局
        menu.setMenu(R.layout.slidingmenu_activity_sop);
        listView = (ListView) menu.findViewById(R.id.listview);
        final ArrayList<String> classes = getClasses(getApplicationContext(), "dynsoft.xone.android.sopactivity");
        ListAdapter listAdapter = new ListAdapter(classes);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                menu.toggle(false);
                String activityName = classes.get(position);
                Class clazz = null;
                try {
                    if (activityName.contains("ScanTestActivity") && TextUtils.isEmpty(sharedPreferences.getString("work_code", ""))) {
                        App.Current.showInfo(SopActivity.this, "请先设置作业员");
                    } else if (activityName.contains("ProductionKanbanActivity")) {     //点击生产看板
                        String org_name = sharedPreferences.getString("org_name", "");
                        work_line = sharedPreferences.getString("segment", "");
                        work_line_id = sharedPreferences.getInt("work_line_id", 0);
                        if (org_name.contains("生产一部")) {                     //生产一部看板
                            startActivity(FirstKanbanActivity.class);
                        } else if (org_name.contains("生产二部")) {              //生产二部看板
                            startActivity(ProductionKanbanActivity.class);
                        } else if (org_name.contains("生产三部")) {               //生产三部看板
                            startActivity(ThirdKanbanActivity.class);
                        } else if (org_name.contains("怡和")) {               //怡和看板
                            startActivity(IKAHEKanbanActivity.class);
                        }else if (org_name.contains("焊机")) {
                            startActivity(WeldKanbanActivity.class);
                        }  else {
                            startActivity(ThirdKanbanActivity.class);
                        }
                    } 
//                    else if (activityName.contains("LocationCheckActivity")) {
//                        String org_name = sharedPreferences.getString("org_name", "");
//                        if(org_name.contains("SMT")) {
//                            startActivity(LocationCheckSMTActivity.class);
//                        } else {
//                            startActivity(LocationCheckActivity.class);
//                        }
//                    } 
                    else if (activityName.contains("CardRegistrationActivity")) {
                        if (sharedPreferences.getString("production", "").contains("关键岗位")) {
                            startActivity(CardRegistrationKeyActivity.class);
                        } else {
                            startActivity(CardRegistrationActivity.class);
                        }
                    } else {
                        clazz = Class.forName(activityName);
                        Intent intent = new Intent(SopActivity.this, clazz);
                        startActivity(intent);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
//        menu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
//
//            @Override
//            public void onOpened() {
//                handler.removeCallbacks(runnable);
//                isViewPagerScroll = false;
//            }
//        });
//
//        menu.setOnClosedListener(new SlidingMenu.OnClosedListener() {
//            @Override
//            public void onClosed() {
//                changeTime = sharedPreferences.getInt("seconds", 15);
//                handler.postDelayed(runnable, changeTime * 1000);
//                isViewPagerScroll = true;
//            }
//        });
    }

    public void startActivity(Class clazz) {
        Intent intent = new Intent(SopActivity.this, clazz);
        intent.putExtra("work_line_id", work_line_id);
        intent.putExtra("work_line", work_line);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (menu.isMenuShowing()) {
            menu.toggle(false);
        } else {
            finish();
        }
    }

    private void initViewPager(final ArrayList<String> image_urls) {
        myAdapter = new MyAdapter(image_urls);
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
                textview_1.setText(((curPosition % image_urls.size()) + 1) + "/" + image_urls.size());
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
            return image_urls.size() <= 1 ? image_urls.size() : Short.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(SopActivity.this, R.layout.sop_viewpager_item, null);
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
                Log.e("len", "PATH:" + image_urls.get(position % image_urls.size()));
            	Picasso.with(SopActivity.this).load(image_urls.get(position % image_urls.size())).error(getResources().getDrawable(R.drawable.null_pic)).into(photoView);
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
                view = View.inflate(SopActivity.this, R.layout.item_listview_popupwindow_sop, null);
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
