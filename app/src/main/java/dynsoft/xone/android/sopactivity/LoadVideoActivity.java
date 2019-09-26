package dynsoft.xone.android.sopactivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Formatter;
import java.util.Locale;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;

/**
 * Created by Administrator on 2017/12/8.
 */

public class LoadVideoActivity extends Activity {
    private VideoView videoview;
    private ImageView imageView;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    private String video_url;
    //������ת��Ϊʱ��
    StringBuilder mFormatBuilder;
    Formatter mFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//���ر���
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//����ȫ��
        setContentView(R.layout.activity_changesop);
        sharedPreferences = getApplication().getSharedPreferences("sop", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();
        videoview = (VideoView) findViewById(R.id.videoview);
        imageView = (ImageView) findViewById(R.id.image_view);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        initData();
        initListener();
    }

//    private void initWebView(int id) {
//        if()
//        webView = (WebView) findViewById(R.id.webview);
//        WebSettings settings = webView.getSettings();
//        webView.setWebChromeClient(new MyWebChromeClient());
//
//        //webView  ������Ƶ���������б���
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
////            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        settings.setJavaScriptEnabled(true);//֧��js
//        settings.setPluginState(WebSettings.PluginState.ON);// ֧�ֲ��
//        settings.setLoadsImagesAutomatically(true);  //֧���Զ�����ͼƬ
//
//
//        settings.setUseWideViewPort(true);  //��ͼƬ�������ʺ�webview�Ĵ�С  ��Ч
//        settings.setLoadWithOverviewMode(true); // ��������Ļ�Ĵ�С
//
//        webView.setWebChromeClient(new WebChromeClient() );
//        webView.loadUrl("http://192.168.0.103:8018/SOP/Video/" + id + ".mp4");// ��������
//
//    }

    private void initData() {
        String sql = "exec fm_sop_get_url ?,?,?";
        Parameters p = new Parameters().add(1, sharedPreferences.getInt("item_id", 0))
                .add(2, sharedPreferences.getInt("seq_id", 0))
                .add(3, sharedPreferences.getString("station", ""));
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(LoadVideoActivity.this, value.Error);
                    return;
                }
                if (value.Value != null) {
                    video_url = value.Value.getValue("video_url", "");
                    initVideoView(video_url);
                //      initWebView(id);
                }
            }
        });
    }

    private void initVideoView(final String video_url) {
         //       String path = "/storage/sdcard1/DCIM/VID_20170908_174048.mp4";
        final Uri uri = Uri.parse(video_url);
        //        videoview.suspend();
        videoview.setMediaController(new MediaController(LoadVideoActivity.this));
        MediaController mediaController = new MediaController(LoadVideoActivity.this);
        mediaController.setMediaPlayer(videoview);
        videoview.setVideoURI(uri);
        Log.e("len", "imageUrl:" + video_url);
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                progressBar.setVisibility(View.GONE);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            videoview.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {

                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            //��ʼ����-----��Ҫ��һЩ����(���磺��ʾ���ض��������ߵ�ǰ�����ٶ�)
                            progressBar.setVisibility(View.VISIBLE);
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            //���ٽ���   (���ؼ��ض��������߼����ٶ�)
                            progressBar.setVisibility(View.GONE);
                            break;
                    }
                    return true;
                }
            });
        }

        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    //ý��������ҵ��ˡ���ʱ����������ͷ�MediaPlayer ���󣬲�����new һ���µġ�
                    Toast.makeText(LoadVideoActivity.this,
                            "����������",
                            Toast.LENGTH_LONG).show();
                } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                    if (extra == MediaPlayer.MEDIA_ERROR_IO) {
                        //�ļ������ڻ���󣬻����粻�ɷ��ʴ���
                        Toast.makeText(LoadVideoActivity.this,
                                "�����ļ�����",
                                Toast.LENGTH_LONG).show();
                    } else if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
                        //��ʱ
                        Toast.makeText(LoadVideoActivity.this,
                                "���糬ʱ",
                                Toast.LENGTH_LONG).show();
                    }
                }
                finish();
                return false;
            }
        });

        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.setVideoURI(uri);
                videoview.start();
            }
        });
        videoview.seekTo(sharedPreferences.getInt(video_url, 0));
        videoview.start();
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoview != null && videoview.isPlaying()) {
            videoview.suspend();
        }
    }

    private void initListener() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit.putInt(video_url, videoview.getCurrentPosition());
                edit.commit();
                if (videoview != null) {
                    videoview.suspend();
                }
                finish();
            }
        });
    }
}