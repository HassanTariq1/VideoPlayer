package com.zacsolutions.videoplayer.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.zacsolutions.videoplayer.PojoClass.Main_Video_Pojo;
import com.zacsolutions.videoplayer.R;

import java.util.ArrayList;

public class VideoAudioplayer extends AppCompatActivity {

    MediaController mediaController;
    VideoView vv;
    String id;
    String uriString;
    ArrayList<Main_Video_Pojo> arrylist_video= new  ArrayList<>();
    String Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_audio_player);
        Intent intent=getIntent();

        Name=intent.getStringExtra("name");
        uriString=intent.getStringExtra("uri");
        mediaController =new  MediaController(this);
        vv = (VideoView)findViewById(R.id.main_videoView);

        vv.setVideoURI(Uri.parse(uriString));
        vv.setMediaController(mediaController);
        vv.setZOrderOnTop(true);
        vv.start();
     //   initnextrecycler();
    }
//    private void initnextrecycler()
//    {
//        RecyclerView main_recycler=(RecyclerView)findViewById(R.id.next_rec);
//        GridLayoutManager main_layoutmanger=new GridLayoutManager(Videoplayer.this,1);
//        main_recycler.setLayoutManager(main_layoutmanger);
//        main_recycler.setHasFixedSize(true);
//        Next_Videos_Adapter Main_adapter=new Next_Videos_Adapter(arrylist_video,Videoplayer.this,spinner_array);
//        main_recycler.setAdapter(Main_adapter);
//
//    }
}
