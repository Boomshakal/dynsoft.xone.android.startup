package dynsoft.xone.android.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import dynsoft.xone.android.adapter.FolderGetPicAdapter;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.util.Bimp;
import dynsoft.xone.android.util.PublicWay;

public class ImageGetPicFile extends Activity {

	private FolderGetPicAdapter folderAdapter;
	private Button bt_cancel;
	private Context mContext;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_image_file);
		PublicWay.activityList.add(this);
		mContext = this;
		bt_cancel = (Button) findViewById(R.id.cancel);
		bt_cancel.setOnClickListener(new CancelListener());
		GridView gridView = (GridView) findViewById(R.id.fileGridView);
		TextView textView = (TextView) findViewById(R.id.headerTitle);
		textView.setText(R.string.photo);
		folderAdapter = new FolderGetPicAdapter(this);
		gridView.setAdapter(folderAdapter);
	}

	private class CancelListener implements OnClickListener {// 取消按钮的监�?
		public void onClick(View v) {
			//清空选择的图�?
			Bimp.tempSelectBitmap.clear();
			Intent intent = new Intent();
			intent.setClass(mContext, App.Current.Workbench.getClass());
			startActivity(intent);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			Intent intent = new Intent();
//			intent.setClass(mContext, App.Current.Workbench.getClass());
//			startActivity(intent);
			finish();
		}
		
		return true;
	}

}
