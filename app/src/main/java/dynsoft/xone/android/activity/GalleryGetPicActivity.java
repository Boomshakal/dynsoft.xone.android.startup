package dynsoft.xone.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dynsoft.xone.android.core.R;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.util.Bimp;
import dynsoft.xone.android.util.PublicWay;
import dynsoft.xone.android.zoom.PhotoView;
import dynsoft.xone.android.zoom.ViewPagerFixed;

public class GalleryGetPicActivity extends Activity {
    private Intent intent;
    private Button back_bt;
    private Button send_bt;
    private Button del_bt;
    private TextView positionTextView;
    private int position;
    private int location = 0;

    private ArrayList<View> listViews = null;
    private ViewPagerFixed pager;
    private MyPageAdapter adapter;

    public List<Bitmap> bmp = new ArrayList<Bitmap>();
    public List<String> drr = new ArrayList<String>();
    public List<String> del = new ArrayList<String>();

    private Context mContext;

    RelativeLayout photo_relativeLayout;
    private int id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_camera_gallery);
        PublicWay.activityList.add(this);
        mContext = this;
        back_bt = (Button) findViewById(R.id.gallery_back);
        send_bt = (Button) findViewById(R.id.send_button);
        del_bt = (Button) findViewById(R.id.gallery_del);
        back_bt.setOnClickListener(new BackListener());
        send_bt.setOnClickListener(new GallerySendListener());
        del_bt.setOnClickListener(new DelListener());
        back_bt.setVisibility(View.INVISIBLE
        );
        intent = getIntent();
        Bundle bundle = intent.getExtras();
        position = Integer.parseInt(intent.getStringExtra("position"));
        isShowOkBt();

        pager = (ViewPagerFixed) findViewById(R.id.gallery01);
        pager.setOnPageChangeListener(pageChangeListener);
        for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
            initListViews(Bimp.tempSelectBitmap.get(i).getBitmap());
        }

        adapter = new MyPageAdapter(listViews);
        pager.setAdapter(adapter);
        pager.setPageMargin((int) getResources().getDimensionPixelOffset(R.dimen.ui_10_dip));
        int id = intent.getIntExtra("ID", 0);
        pager.setCurrentItem(id);
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        public void onPageSelected(int arg0) {
            location = arg0;
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void initListViews(Bitmap bm) {
        if (listViews == null)
            listViews = new ArrayList<View>();
        PhotoView img = new PhotoView(this);
        img.setBackgroundColor(0xff000000);
        img.setImageBitmap(bm);
        img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        listViews.add(img);
    }

    private class BackListener implements OnClickListener {

        public void onClick(View v) {
            intent.setClass(GalleryGetPicActivity.this, ImageGetPicFile.class);
            startActivity(intent);
        }
    }

    private class DelListener implements OnClickListener {

        public void onClick(View v) {
            if (listViews.size() == 1) {
                Bimp.tempSelectBitmap.clear();
                Bimp.max = 0;
                send_bt.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
                Intent intent = new Intent("data.broadcast.action");
                sendBroadcast(intent);
                finish();
            } else {
                Bimp.tempSelectBitmap.remove(location);
                Bimp.max--;
                pager.removeAllViews();
                listViews.remove(location);
                adapter.setListViews(listViews);
                send_bt.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class GallerySendListener implements OnClickListener {
        public void onClick(View v) {
            Link link = new Link("pane://x:code=pn_qm_ipqc_first_sure_editor");
            id = getIntent().getIntExtra("pic_id", 0);
            link.Parameters.add("id", id);
            Log.e("len", "确定按钮");
//			link.Parameters.add("order_code", index);
            link.Open(v, mContext, null);
            finish();
            //intent.setClass(mContext,App.Current.Workbench.getClass());
            //startActivity(intent);
        }

    }

    public void isShowOkBt() {
        if (Bimp.tempSelectBitmap.size() > 0) {
            send_bt.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
            send_bt.setPressed(true);
            send_bt.setClickable(true);
            send_bt.setTextColor(Color.WHITE);
        } else {
            send_bt.setPressed(false);
            send_bt.setClickable(false);
            send_bt.setTextColor(Color.parseColor("#E1E0DE"));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (position == 1) {
                this.finish();
//				intent.setClass(GalleryGetPicActivity.this, AlbumGetPicActivity.class);
//				startActivity(intent);
            } else if (position == 2) {
                this.finish();
                intent.setClass(GalleryGetPicActivity.this, ShowAllPhoto.class);
                startActivity(intent);
            }
        }
        return true;
    }

    class MyPageAdapter extends PagerAdapter {

        private ArrayList<View> listViews;

        private int size;

        public MyPageAdapter(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public void setListViews(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public int getCount() {
            return size;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
        }

        public void finishUpdate(View arg0) {
        }

        public Object instantiateItem(View arg0, int arg1) {
            try {
                ((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

            } catch (Exception e) {
            }
            return listViews.get(arg1 % size);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }
}
