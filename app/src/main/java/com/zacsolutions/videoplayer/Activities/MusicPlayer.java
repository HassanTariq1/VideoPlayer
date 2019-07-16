package com.zacsolutions.videoplayer.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zacsolutions.videoplayer.All_Videos_Activity;
import com.zacsolutions.videoplayer.Config;
import com.zacsolutions.videoplayer.R;
import com.zacsolutions.videoplayer.Services.AudioService;

import java.io.File;
import java.io.IOException;

public class MusicPlayer extends AppCompatActivity implements View.OnClickListener {
    Button play, pause, stop;
    // MediaPlayer mediaPlayer;
    int pausePosition;
    SeekBar seekBar;
    Handler handler;
    Runnable runnable;
    String name, uri;
    TextView tv_name;
    ConstraintLayout cl_vedios;
    Intent audio_service_intent;
    private boolean isServiceBound;
    private ServiceConnection serviceConnection;
    private AudioService audioService;
    boolean playing;
    ImageView img_back;
    boolean background = false;
    private final String CHANNEL_ID = "personal_notification";
    private final int NOTIFICATION_ID = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        play = (Button) findViewById(R.id.play);
        pause = (Button) findViewById(R.id.pause);
        stop = (Button) findViewById(R.id.stop);
        tv_name = findViewById(R.id.tv_name);
        img_back = findViewById(R.id.img_back);
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        uri = sharedPreferences.getString(Config.SHARED_PREF_AUDIO, null);
        name = sharedPreferences.getString(Config.SHARED_PREF_AUDIO_Name, null);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!background) {
                    background = true;
                    img_back.setImageResource(R.drawable.headphones_blue);
                } else if (background) {
                    background = false;
                    img_back.setImageResource(R.drawable.headphones_grey);
                }

            }
        });
//    cl_vedios=findViewById(R.id.cl_videos);
//    cl_vedios.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        Intent intent = new Intent(MusicPlayer.this, All_Videos_Activity.class);
//        startActivity(intent);
//      }
//    });

        handler = new Handler();
        Intent intent = getIntent();
        //  SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

//        name = intent.getStringExtra("name");
//        uri = intent.getStringExtra("uri");
        audio_service_intent = new Intent(MusicPlayer.this, AudioService.class);
//        audio_service_intent.putExtra("uri",uri);
        startService(audio_service_intent);
        tv_name.setText(name);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);
        bindService();

        //seekBar.setEnabled(false);
//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        try {
//            mediaPlayer.setDataSource(uri);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            mediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                seekBar.setMax(mediaPlayer.getDuration());
//                mediaPlayer.start();
//                playCycle();
//            }
//        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if (b) {
                    playing = false;
                    audioService.seekto(i);
                    playing = true;
                    playCycle();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                if (!playing) {
                    audioService.resumemusice();
                    playing = true;
                    seekBar.setEnabled(true);
                }
//                if (mediaPlayer == null) {
//                    mediaPlayer = new MediaPlayer();
//                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                    try {
//                        mediaPlayer.setDataSource(uri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        mediaPlayer.prepare();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//          mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mymusic);
//                    mediaPlayer.start();
//                } else if (!mediaPlayer.isPlaying()) {
//
//                    mediaPlayer.seekTo(pausePosition);
//                    mediaPlayer.start();
//                }
//                playCycle();
                break;
            case R.id.pause:
                if (isServiceBound) {
                    audioService.pausemusice();
                    playing = false;
                    seekBar.setEnabled(false);
                }
//                unbindService();
//
                break;
            case R.id.stop:
//                if (mediaPlayer != null) {
//                    mediaPlayer.stop();
//                    mediaPlayer = null;
//                    //   seekBar.setEnabled(false);
//                }
                break;
        }
    }

    public void playCycle() {
        if (isServiceBound) {

            seekBar.setProgress(audioService.getSeekbarlocation());
            if (audioService.getduration() == audioService.getSeekbarlocation()) {
                playing = false;
                seekBar.setEnabled(false);
                unbindService();
                stopService(audio_service_intent);
                finish();
            }

            new Thread(new Runnable() {
                public void run() {
                    while (playing) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        playCycle();
                    }
                    // finish();
                }
            }).start();
        }

    }

    @Override
    public void onBackPressed() {
//        mediaPlayer.pause();
        // unbindService();
        if (background) {
            unbindService();
            audioService.bacgroundPlaying();
        } else {
            unbindService();
            stopService(audio_service_intent);
        }

        super.onBackPressed();


    }

    private void bindService() {
        if (serviceConnection == null) {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//                    AudioService.MyServiceBinder myServiceBinder=(AudioService.MyServiceBinder)iBinder;
                    AudioService.MyServiceBinder myServiceBinder = (AudioService.MyServiceBinder) iBinder;
                    audioService = myServiceBinder.getService();
                    isServiceBound = true;
                    playing = true;
                    playCycle();
                    seekBar.setMax(audioService.getduration());
//                    AudioService.
                    //  Mybindeer
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    isServiceBound = false;
                    playing = false;
                }
            };
        }
        bindService(audio_service_intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
            playing = false;
        }
    }
//    public void setSeekBar(int i)
//    {
//        seekBar.setMax(i);
//    }
}