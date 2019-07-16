package com.zacsolutions.videoplayer.Activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.zacsolutions.videoplayer.R;
import com.zacsolutions.videoplayer.ResizeSurfaceView;
import com.zacsolutions.videoplayer.MediaControllers.VideoControllerView;
import com.zacsolutions.videoplayer.Services.AudioService;
import com.zacsolutions.videoplayer.Services.VideoService;

import java.io.IOException;


public class Videoplayer extends Activity implements SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener,
        VideoControllerView.MediaPlayerControlListener,
        MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnCompletionListener {

    private final static String TAG = "MainActivity";
    ResizeSurfaceView mVideoSurface;
    MediaPlayer mMediaPlayer;
    private int mVideoWidth;
    VideoControllerView controller;
    private int mVideoHeight;
    private View mContentView;
    private View mLoadingView;
    private boolean mIsComplete;
    private String mCurrentVideo;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    Intent video_service_intent;
    private VideoService videoService;
    private boolean isServiceBound;
    private ServiceConnection serviceConnection;

    String urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_player);
//        AdView adView = new AdView(this);
//        adView.setAdSize(AdSize.BANNER);
//        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        MobileAds.initialize(this,"ca-app-pub-7809883325778350/8307934276");

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7809883325778350/8307934276");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }
        });

        Intent i = getIntent();
        if (i != null) {
            if (i.getStringExtra("URL") != null) {
                setTitle(i.getStringExtra("TITLE"));
                mCurrentVideo = i.getStringExtra("URL");
                 urls=i.getStringExtra("TITLE");
                //initializePlayer(Uri.parse(i.getStringExtra("URL")));


            } else {
                setTitle("Select Video");
                //initializePlayer(Uri.parse("http://fs.evonetbd.com/Media/Movies/English%20Movies/2017/Wonder%20Woman%20%20(2017)/Sample.mp4"));

            }

        }

        mVideoSurface = (ResizeSurfaceView) findViewById(R.id.videoSurface);
        mContentView = findViewById(R.id.video_container);
        mLoadingView = findViewById(R.id.loading);
        SurfaceHolder videoHolder = mVideoSurface.getHolder();
        videoHolder.addCallback(this);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        //(FrameLayout) findViewById(R.id.videoSurfaceContainer)
        controller = new VideoControllerView.Builder(this, this)
                .withVideoTitle(urls)
                .withVideoSurfaceView(mVideoSurface)//to enable toggle display controller view
                .canControlBrightness(true)
                .canControlVolume(true)
                .canSeekVideo(true)
                .exitIcon(R.drawable.back_circular_small)
                .pauseIcon(R.drawable.pause_nw)
                .playIcon(R.drawable.play_new)
                .shrinkIcon(R.drawable.rotate_new)
                .stretchIcon(R.drawable.rotate_new)
                .build((FrameLayout) findViewById(R.id.videoSurfaceContainer));//layout container that hold video play view

        mLoadingView.setVisibility(View.VISIBLE);
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(this, Uri.parse(mCurrentVideo));
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        mVideoSurface.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                controller.toggleControllerView();
                return false;
            }
        });
    }
    private void bindService() {
        if (serviceConnection == null) {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//                    AudioService.MyServiceBinder myServiceBinder=(AudioService.MyServiceBinder)iBinder;
                    VideoService.MyServiceBinder myServiceBinder = (VideoService.MyServiceBinder) iBinder;
                    videoService = myServiceBinder.getService();
                    isServiceBound = true;
//                    AudioService.
                    //  Mybindeer
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    isServiceBound = false;
                }
            };
        }
        bindService(video_service_intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        controller.show();
        return false;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        mVideoHeight = mp.getVideoHeight();
        mVideoWidth = mp.getVideoWidth();
        if (mVideoHeight > 0 && mVideoWidth > 0)
            mVideoSurface.adjustSize(mContentView.getWidth(), mContentView.getHeight(), mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mVideoWidth > 0 && mVideoHeight > 0)
            mVideoSurface.adjustSize(getDeviceWidth(this),getDeviceHeight(this),mVideoSurface.getWidth(), mVideoSurface.getHeight());
    }

    private void resetPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public static int getDeviceWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.widthPixels;
    }

    public static int getDeviceHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.heightPixels;
    }


    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
          resetPlayer();
    }
// End SurfaceHolder.Callback


// Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        //setup video controller view
        mLoadingView.setVisibility(View.GONE);
        mVideoSurface.setVisibility(View.VISIBLE);
        mMediaPlayer.start();
        mIsComplete = false;
    }
// End MediaPlayer.OnPreparedListener

    /**
     * Implement VideoMediaController.MediaPlayerControl
      */

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
         if(null != mMediaPlayer)
           return mMediaPlayer.getCurrentPosition();
        else
           return 0;
    }

    @Override
    public int getDuration() {
        if(null != mMediaPlayer)
            return mMediaPlayer.getDuration();
        else
            return 0;
    }

    @Override
    public boolean isPlaying() {
        if(null != mMediaPlayer)
            return mMediaPlayer.isPlaying();
        else
            return false;
    }

    @Override
    public boolean isComplete() {
        return mIsComplete;
    }

    @Override
    public void pause() {
        if(null != mMediaPlayer) {
            mMediaPlayer.pause();
        }

    }

    @Override
    public void seekTo(int i) {
        if(null != mMediaPlayer) {
            mMediaPlayer.seekTo(i);
        }
    }



    @Override
    public void start() {
        if(null != mMediaPlayer) {
            mMediaPlayer.start();
            mIsComplete = false;
        }
    }

    @Override
    public boolean isFullScreen() {
        return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? true : false;
    }

    @Override
    public void toggleFullScreen() {
       if(isFullScreen()){
           setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
       }else {
           setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
       }
    }

    @Override
    public void exit(boolean back) {
        if (back)
        {
            video_service_intent = new Intent(Videoplayer.this, VideoService.class);
            video_service_intent.putExtra("uri",urls);
            startService(video_service_intent);
            bindService();
           // videoService.seekto(mMediaPlayer.getCurrentPosition());
            resetPlayer();
            unbindService();
            finish();
        }
        else
        {
            resetPlayer();
            finish();
        }
    }
    private void unbindService() {
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
        }
    }
//    @Override
//    public void exit()
//    {
//        resetPlayer();
//        finish();
//    }

    public void showinter(){

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
                    finish();
        }


    }

    @Override
    public void onBackPressed() {
       // showinter();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mIsComplete = true;
    }


    // End VideoMediaController.MediaPlayerControl

}
