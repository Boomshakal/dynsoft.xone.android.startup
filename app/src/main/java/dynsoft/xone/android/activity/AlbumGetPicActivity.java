package dynsoft.xone.android.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import dynsoft.xone.android.adapter.AlbumGridViewAdapter;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.util.AlbumHelper;
import dynsoft.xone.android.util.Bimp;
import dynsoft.xone.android.util.ImageBucket;
import dynsoft.xone.android.util.ImageItem;
import dynsoft.xone.android.util.PublicWay;
import dynsoft.xone.android.wms.pn_qm_ipqc_patrol_record_editor;

/**
 * 这个是进入相册显示所有图片的界面
 *
 * @author king
 * @QQ:595163260
 * @version  下午11:47:15
 */
public class AlbumGetPicActivity extends Activity {
	private GridView gridView;
	private TextView tv;
	private AlbumGridViewAdapter gridImageAdapter;
	//完成按钮
	private Button okButton;
	// 返回按钮
	private Button back;
	// 取消按钮
	private Button cancel;
	private Intent intent;
	// 预览按钮
	private Button preview;
	private Context mContext;
	private ArrayList<ImageItem> dataList;
	private AlbumHelper helper;
	public static List<ImageBucket> contentList;
	public static Bitmap bitmap;
	private int id;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_album);
		PublicWay.activityList.add(this);
		mContext = this;
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		registerReceiver(broadcastReceiver, filter);
		bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.plugin_camera_no_pictures);
		init();
		initListener();
		isShowOkBt();
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			//mContext.unregisterReceiver(this);
			// TODO Auto-generated method stub
			gridImageAdapter.notifyDataSetChanged();
		}
	};

	private class PreviewListener implements OnClickListener {
		public void onClick(View v) {
			if (Bimp.tempSelectBitmap.size() > 0) {
				intent.putExtra("position", "1");
				intent.setClass(AlbumGetPicActivity.this, GalleryActivity.class);
				startActivity(intent);
			}
		}

	}

	private class AlbumSendListener implements OnClickListener {
		public void onClick(View v) {
			overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
//			intent.setClass(mContext, pn_qm_ipqc_patrol_record_editor.class);
			//startActivity(intent);
//			pn_qm_ipqc_patrol_record_editor.GridAdapter g = p.new GridAdapter(mContext);
//			g.notifyDataSetChanged();
			pn_qm_ipqc_patrol_record_editor p = new pn_qm_ipqc_patrol_record_editor(mContext);
//			App.Current.Workbench.showPane(p);
//			p.Init();
			//final String id = AlbumActivity.this.getIntent().getStringExtra("order_id");
			//final String index = AlbumActivity.this.getIntent().getStringExtra("index");
			Link link = new Link("pane://x:code=pn_qm_ipqc_first_sure_editor");
			id = getIntent().getIntExtra("id", 0);
			link.Parameters.add("id", id);
//			link.Parameters.add("order_code", index);
			link.Open(v, mContext, null);

			onRefrsh();
			finish();
//			App.Current.Workbench.closePane(p);
		}

	}

	public void onRefrsh() {
		//Workbench.this.playBeepSound(R.raw.scan_beep);
		int index = App.Current.Workbench.ViewPager.getCurrentItem();
		if (index > -1) {
			Pane pane = App.Current.Workbench.PaneAdapter.PaneList.get(index);
			if (pane != null) {
				pane.onRefrsh();
			}
		}
	}

	private class BackListener implements OnClickListener {
		public void onClick(View v) {
			intent.setClass(AlbumGetPicActivity.this, ImageFile.class);
			startActivity(intent);
		}
	}

	private class CancelListener implements OnClickListener {
		public void onClick(View v) {
			Bimp.tempSelectBitmap.clear();
			final String index = AlbumGetPicActivity.this.getIntent().getStringExtra("index");
			Link link = new Link("pane://x:code=pn_qm_ipqc_first_sure_editor");
			id = getIntent().getIntExtra("id", 0);
			link.Parameters.add("id", id);
//			link.Parameters.add("order_code", index);
			link.Open(v, mContext, null);
			finish();
		}
	}


	private void init() {
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		contentList = helper.getImagesBucketList(false);
		dataList = new ArrayList<ImageItem>();
		for(int i = 0; i<contentList.size(); i++){
			dataList.addAll( contentList.get(i).imageList );
		}

		back = (Button) findViewById(R.id.back);
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(new CancelListener());
		back.setOnClickListener(new BackListener());
		preview = (Button) findViewById(R.id.preview);
		preview.setOnClickListener(new PreviewListener());
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		gridView = (GridView) findViewById(R.id.myGrid);
		gridImageAdapter = new AlbumGridViewAdapter(this,dataList,
				Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);
		tv = (TextView) findViewById(R.id.myText);
		gridView.setEmptyView(tv);
		okButton = (Button) findViewById(R.id.ok_button);
		okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size()
				+ "/"+PublicWay.num+")");
		back.setVisibility(View.GONE);
	}

	private void initListener() {

		gridImageAdapter
				.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(final ToggleButton toggleButton,
											int position, boolean isChecked,Button chooseBt) {
						if (Bimp.tempSelectBitmap.size() >= PublicWay.num) {
							toggleButton.setChecked(false);
							chooseBt.setVisibility(View.GONE);
							if (!removeOneData(dataList.get(position))) {
								Toast.makeText(AlbumGetPicActivity.this, "超出可选图片张数",
										Toast.LENGTH_SHORT).show();
							}
							return;
						}
						if (isChecked) {
							chooseBt.setVisibility(View.VISIBLE);
							Bimp.tempSelectBitmap.add(dataList.get(position));
							okButton.setText("完成" +"(" + Bimp.tempSelectBitmap.size()
									+ "/"+PublicWay.num+")");
						} else {
							Bimp.tempSelectBitmap.remove(dataList.get(position));
							chooseBt.setVisibility(View.GONE);
							okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
						}
						isShowOkBt();
					}
				});

		okButton.setOnClickListener(new AlbumSendListener());

	}

	private boolean removeOneData(ImageItem imageItem) {
		if (Bimp.tempSelectBitmap.contains(imageItem)) {
			Bimp.tempSelectBitmap.remove(imageItem);
			okButton.setText("完成"+"(" +Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
			return true;
		}
		return false;
	}

	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
			preview.setPressed(true);
			okButton.setPressed(true);
			preview.setClickable(true);
			okButton.setClickable(true);
			okButton.setTextColor(Color.WHITE);
			preview.setTextColor(Color.WHITE);
		} else {
			okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
			preview.setPressed(false);
			preview.setClickable(false);
			okButton.setPressed(false);
			okButton.setClickable(false);
			okButton.setTextColor(Color.parseColor("#E1E0DE"));
			preview.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			intent.setClass(AlbumGetPicActivity.this, ImageGetPicFile.class);
			startActivity(intent);
		}
		return false;

	}
	@Override
	protected void onRestart() {
		isShowOkBt();
		super.onRestart();
	}
}
