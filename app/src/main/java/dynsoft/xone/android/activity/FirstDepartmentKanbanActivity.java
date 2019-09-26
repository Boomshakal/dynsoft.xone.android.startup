package dynsoft.xone.android.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.fragment.department.FirstDepartmentAdapter;
import dynsoft.xone.android.zoom.ViewPagerFixed;

/**
 * Created by Administrator on 2018/4/9.
 */

public class FirstDepartmentKanbanActivity extends FragmentActivity {
    private TextView textViewTime;
    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private ViewPagerFixed viewPager;
//    private ImageView imageViewLeft;
//    private ImageView imageViewRight;
    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            int currentItem = viewPager.getCurrentItem();
//            if(currentItem == 2) {
//                viewPager.setCurrentItem(0);
//            } else {
//                viewPager.setCurrentItem(currentItem + 1);
//            }
            firstDepartmentAdapter.notifyDataSetChanged();
            handler.postDelayed(runnable, 10 * 60 * 1000);
        }
    };
    private FirstDepartmentAdapter firstDepartmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanban_firstdepartment);
        handler = new Handler();
        textViewTime = (TextView) findViewById(R.id.textview_time);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioButton1 = (RadioButton) findViewById(R.id.radiobutton_1);
        radioButton2 = (RadioButton) findViewById(R.id.radiobutton_2);
        radioButton3 = (RadioButton) findViewById(R.id.radiobutton_3);
        viewPager = (ViewPagerFixed) findViewById(R.id.viewpager);
//        imageViewLeft = (ImageView) findViewById(R.id.imageview_left);
//        imageViewRight = (ImageView) findViewById(R.id.imageview_right);
//        imageViewLeft.setImageBitmap(App.Current.ResourceManager.getImage("@/core_left_gray"));
//        imageViewRight.setImageBitmap(App.Current.ResourceManager.getImage("@/core_right_gray"));
        initTime();
        initRadioGroup();
        initViewPager();
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }

    private void initRadioGroup() {
        setRadioGroupSelected(0);
        radioButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(0);
                }
            }
        });
        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewPager.getCurrentItem() != 1) {
                    viewPager.setCurrentItem(1);
                }
            }
        });
        radioButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewPager.getCurrentItem() != 2) {
                    viewPager.setCurrentItem(2);
                }
            }
        });
    }

    private void initViewPager() {
        firstDepartmentAdapter = new FirstDepartmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(firstDepartmentAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setRadioGroupSelected(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(new Date());
        textViewTime.setText("Ê±¼ä:" + format);
    }

    public void setRadioGroupSelected(int position) {
        if(radioGroup != null && radioGroup.getChildCount() > 1) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                if(i == position) {
                    radioGroup.getChildAt(i).setSelected(true);
                } else {
                    radioGroup.getChildAt(i).setSelected(false);
                }
            }
        } else {

        }
    }
}
