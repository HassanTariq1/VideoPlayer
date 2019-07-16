package com.zacsolutions.videoplayer.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.zacsolutions.videoplayer.Activities.MusicPlayer;
import com.zacsolutions.videoplayer.Config;
import com.zacsolutions.videoplayer.R;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AudioService extends Service {
//    public AudioService() {
//    }

    MediaPlayer mediaPlayer;
    String uri;
  //  private RemoteViews remoteViews;

    private  final  String CHANNEL_ID="personal_notification";
    private  final  int NOTIFICATION_ID = 001;

    public class MyServiceBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

    private IBinder iBinder = new MyServiceBinder();

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
        uri = sharedPreferences.getString(Config.SHARED_PREF_AUDIO,null);

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
        SharedPreferences mobilePreference = AudioService.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        mobilePreference.edit().remove(Config.SHARED_PREF_AUDIO_Name).commit();
        mobilePreference.edit().remove(Config.SHARED_PREF_AUDIO).commit();
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();

        Log.d("Service", "Destroyed");

    }

    //    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("Service Demo", "In OnUnBind");
        return super.onUnbind(intent);
    }

    public void stopPrvious()
    {
        mediaPlayer.stop();
        mediaPlayer.release();
    }
    public void pausemusice() {
        mediaPlayer.pause();
    }
    public int getSeekbarlocation() {
        return mediaPlayer.getCurrentPosition();
    }

    public void resumemusice() {
         mediaPlayer.start();
    }
    public void seekto(int i)
    {
        mediaPlayer.seekTo(i);
    }
    public int getduration()
    {
        return mediaPlayer.getDuration();
    }
    public void bacgroundPlaying()
    {



        //remoteViews = new RemoteViews(getPackageName(),R.layout.custom_notification);
//        remoteViews.setImageViewResource(R.id.notif_icon,R.mipmap.ic_launcher);
//        remoteViews.setTextViewText(R.id.notif_title,"TEXT");
//        remoteViews.setProgressBar(R.id.progressBar,100,40,true);
//        remoteViews.setOnClickPendingIntent();



        File file = new File(uri);
        creatNotificationChannel();
        Intent landingactivity= new Intent(AudioService.this,MusicPlayer.class);
        landingactivity.putExtra("uri",uri);
        landingactivity.putExtra("name",file.getName());
        landingactivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent landingPendingintent = PendingIntent.getActivity(AudioService.this,0,landingactivity,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(AudioService.this,CHANNEL_ID);
        builder.setSmallIcon(R.drawable.music_audio);
        builder.setContentTitle("X-Player");
//        builder.setContentText("This is Simple Notification....");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);
        builder.setContentIntent(landingPendingintent);
        NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(AudioService.this);
        notificationCompat.notify(NOTIFICATION_ID,builder.build());
    }
    private void creatNotificationChannel()

    {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            CharSequence name = "Personal Notifications";
            String description = "Include ll personal Notifications";
            int imprtance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,name,imprtance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

}
