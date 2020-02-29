package dynsoft.xone.android.adapter;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GradviewImageAdapter extends BaseAdapter {
    private ArrayList<byte[]> images;
    private Context context;

    public GradviewImageAdapter(ArrayList<byte[]> images, Context context) {
        this.images = images;
        this.context = context;
    }

    public void fresh(ArrayList<byte[]> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width / 2 - 10, 0);
        imageView.setLayoutParams(layoutParams);
        Glide.with(context).load(images.get(position)).into(imageView);
        return null;
    }
}
