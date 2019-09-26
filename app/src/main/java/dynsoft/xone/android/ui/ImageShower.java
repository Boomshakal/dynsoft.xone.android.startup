package dynsoft.xone.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;

/**
 * Created by Administrator on 2018/11/19.
 */

public class ImageShower extends Activity {
    private ImageView imageView;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageshower);
        imageView = (ImageView) findViewById(R.id.imageview_shower);
        intent = getIntent();
        String photo_path = intent.getStringExtra("photo_path");
        Log.e("len", "Photo" + photo_path);
        File file = new File(photo_path);
        if (file != null && file.exists()) {
            Picasso.with(this).load(file).into(imageView);
        } else {
            App.Current.toastError(this, "文件丢失，请重新拍照！");
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        finish();
        return true;
    }
}
