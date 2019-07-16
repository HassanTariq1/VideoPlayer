package com.zacsolutions.videoplayer.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zacsolutions.videoplayer.Config;

import java.io.IOException;

public class VideoService extends Service {


    MediaPlayer mediaPlayer;
    String uri;

//    public VideoService() {
//    }

    private  final  String CHANNEL_ID="personal_notification";
    private  final  int NOTIFICATION_ID = 001;

    public class MyServiceBinder extends Binder {
        public VideoService getService() {
            return VideoService.this;
        }
    }
    private IBinder iBinder = new VideoService.MyServiceBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Service Demo", "In OnBind");
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service", "Created");
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        uri = sharedPreferences.getString(Config.SHARED_PREF_Video,null);

        mediaPlayer = new MediaPlayer();
        // uri = intent.getStringExtra("uri");
        if (uri!=null) {
            try {
                mediaPlayer.setDataSource(uri);
                mediaPlayer.prepare();
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    player.setSeekBar(mediaPlayer.getDuration());
//                    mediaPlayer.start();
//                }
//            });
                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        onDestroy();// finish current activity
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "Started");

        return START_NOT_STICKY;

    }


    @Override
    public void onDestroy() {

        mediaPlayer.stop();
        mediaPlayer.release();
        super.onDestroy();

        Log.d("Service", "Destroyed");

    }
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("Service Demo", "In OnUnBind");
        return super.onUnbind(intent);
    }
    public void seekto(int i)
    {
        mediaPlayer.seekTo(i);
    }
}
