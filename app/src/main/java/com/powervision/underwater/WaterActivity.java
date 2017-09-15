package com.powervision.underwater;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.library.FFmpegError;
import com.example.library.FFmpegListener;
import com.example.library.FFmpegPlayer;
import com.example.library.FFmpegStreamInfo;
import com.example.library.MGLSurfaceView;
import com.example.library.NotPlayingException;

/**
 * 遥控器主类
 */
public class WaterActivity extends Activity implements FFmpegListener{
    private static final String TAG = "WaterActivity";
    //图传地址
    private static final String URL_PATH="rtsp://192.168.1.100/live";
    //解码类
    private FFmpegPlayer mVideoPlay;
    //播放控件
    private MGLSurfaceView mVideoView;
    //背景图
    private ImageView imgBg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setScreenArrts();
        findViewById();
        initVideoView();
    }

    /**
     * 实例化控件
     */
    private void findViewById() {
        mVideoView=findViewById(R.id.videoView);
        imgBg=findViewById(R.id.imgBg);
    }

    /**
     * 实例化图传
     */
    private void initVideoView() {
        mVideoPlay=FFmpegPlayer.getInstance(getBaseContext());
        mVideoPlay.setMpegListener(this);
        mVideoPlay.connServer(URL_PATH);
        mVideoPlay.setGlSurfaceView(mVideoView);
        mVideoPlay.setVrmode(1);
       // mVideoPlay.setDebugLevel(1,10);
    }

    /**
     * 隐藏navigationbar
     */
    private void setScreenArrts() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                getWindow().getDecorView().setSystemUiVisibility(5895);
                getWindow().getDecorView().requestFocus();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mVideoPlay.stop();
        super.onDestroy();

    }

    @Override
    public void onFFDataSourceLoaded(FFmpegError fFmpegError, FFmpegStreamInfo[] fFmpegStreamInfos) {
      if (fFmpegError!=null){
         String format="conn... %s";
         String message=String.format(format,fFmpegError.getMessage());
          Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
          Log.i(TAG, "onFFDataSourceLoaded: ="+fFmpegError.getMessage());
      }
        Log.i(TAG, "onFFDataSourceLoaded: ");
    }

    @Override
    public void onFFResume(NotPlayingException e) {
        Log.i(TAG, "onFFResume: ");
    }

    @Override
    public void onFFPause(NotPlayingException e) {
        Log.i(TAG, "onFFPause: ");
    }

    @Override
    public void onFFStop() {
        Log.i(TAG, "onFFStop: ");
    }

    @Override
    public void onFFUpdateTime(long l, long l1, boolean b) {
        Log.i(TAG, "onFFUpdateTime: ");
    }

    @Override
    public void onFFSeeked(NotPlayingException e) {
        Log.i(TAG, "onFFSeeked: ");
    }

    @Override
    public void onFFConnStatus(int status) {
        switch (status){
            case 0:
                Log.e("conn","conn ok");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imgBg.setVisibility(View.GONE);
                    }
                });
                break;
            case 1:
                Log.e("conn","reconn...");
                break;
            default:
                break;
        }
        Log.i(TAG, "onFFConnStatus: ");
    }

    @Override
    public void onFFRenderStatus(int i) {
        Log.i(TAG, "onFFRenderStatus: ");
    }

    @Override
    public void onFFRecordStatus(int i) {
        Log.i(TAG, "onFFRecordStatus: ");
    }

    @Override
    public void onFrameUpdate(int i) {
        Log.i(TAG, "onFrameUpdate: ");
    }

    @Override
    public void onConnectTimeout() {
        Log.i(TAG, "onConnectTimeout: ");
    }
}
