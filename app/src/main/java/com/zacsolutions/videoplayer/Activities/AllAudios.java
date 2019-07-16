package com.zacsolutions.videoplayer.Activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.zacsolutions.videoplayer.Adapters.All_Audio_Adapter_Grid;
import com.zacsolutions.videoplayer.Adapters.All_Audio_Adapter_list;
import com.zacsolutions.videoplayer.Adapters.RecyclerItemTouch;
import com.zacsolutions.videoplayer.All_Videos_Activity;
import com.zacsolutions.videoplayer.Config;
import com.zacsolutions.videoplayer.PojoClass.VideoFile;
import com.zacsolutions.videoplayer.R;
import com.zacsolutions.videoplayer.Services.AudioService;

import java.util.ArrayList;
import java.util.Collections;

public class AllAudios extends AppCompatActivity implements  RecyclerItemTouch.OnItemClickListener , NavigationView.OnNavigationItemSelectedListener{
    static ArrayList<VideoFile> data = new ArrayList<>();
    RecyclerView rc_vedios;
    RecyclerView.LayoutManager layoutManager;
    All_Audio_Adapter_list adapter;
    All_Audio_Adapter_Grid adapter_grid;
    private InterstitialAd mInterstitialAd;
    String uri,title;
    ContentResolver contentResolver;
    DrawerLayout drawer;
    ImageView img_menu;
    boolean alreadygrid=false;
    private AdView mAdView;
    TextView tv_title;
    ConstraintLayout cl_directory;
    ConstraintLayout cl_vedio;
    private AudioService audioService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_audios);

        MobileAds.initialize(AllAudios.this,"ca-app-pub-7809883325778350/8307934276");
        contentResolver=getContentResolver();
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(AllAudios.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
//                Intent videoUrlIntent = new Intent(AllAudios.this, Videoplayer.class);
//                videoUrlIntent.putExtra("URL",uri);
//                videoUrlIntent.putExtra("TITLE",title);
//                startActivity(videoUrlIntent);
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        cl_directory=findViewById(R.id.cl_folders);
        cl_directory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllAudios.this,Directories.class);
                startActivity(intent);
            }
        });
        cl_vedio=findViewById(R.id.cl_videos);
        cl_vedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AllAudios.this, All_Videos_Activity.class);
                startActivity(intent);
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tv_title=(TextView)findViewById(R.id.title);
        tv_title.setText("All Videos");
        img_menu=(ImageView)findViewById(R.id.img_menu);
        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!alreadygrid){
                    floodDataGrid(printNamesToLogCat(AllAudios.this));
                    img_menu.setImageResource(R.drawable.list_white);
                    alreadygrid=true;
                }else if (alreadygrid)
                {
                    floodData(printNamesToLogCat(AllAudios.this));
                    img_menu.setImageResource(R.drawable.grid_white);
                    alreadygrid=false;
                }
            }
        });
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.LEFT))
                {
                    drawer.closeDrawer(Gravity.LEFT);
                } else
                {
                    drawer.openDrawer(Gravity.LEFT);
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        rc_vedios=(RecyclerView)findViewById(R.id.rc_directory);
        floodData(printNamesToLogCat(this));

    }
    public static ArrayList<VideoFile> printNamesToLogCat(Context context) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Audio.AudioColumns.DATA ,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DURATION };
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);

        VideoFile file;
        if (c != null) {
            while (c.moveToNext()) {
                Log.d("AUDIO", c.getString(0));
                file = new VideoFile(
                        c.getString(0),
                        c.getString(1),
                        c.getLong(2));
                data.add(file);
            }
            c.close();
            Collections.reverse(data);
            return data;
        }

        return null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onItemClick(View view, int position) {
        adpterclick(data.get(position).getUri(),data.get(position).getName());
    }
    public void adpterclick(String Url,String Title)
    {
        uri=Url;
        title=Title;
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {

            Intent intent = new Intent(AllAudios.this,AudioService.class);
            stopService(intent);

            SharedPreferences sharedPreferences = AllAudios.this.getApplicationContext()
                    .getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Config.SHARED_PREF_AUDIO,uri);
            editor.putString(Config.SHARED_PREF_AUDIO_Name,title);
            editor.commit();
            Intent videoUrlIntent = new Intent(AllAudios.this,MusicPlayer.class);
//            videoUrlIntent.putExtra("name",title);
//            videoUrlIntent.putExtra("uri",uri);
            startActivity(videoUrlIntent);
        }


    }
    @Override
    public void onLongItemClick(View view, int position) {

    }
    private void floodData(ArrayList<VideoFile> Files) {
        if(Files != null){
            ListStyleRecyclerView(Files);
        }
    }
    private void floodDataGrid(ArrayList<VideoFile> Files) {
        if(Files != null){
            GridStyleRecyclerView(Files);
        }
    }
    private void ListStyleRecyclerView(ArrayList<VideoFile> Files)
    {
        layoutManager=new GridLayoutManager(AllAudios.this,1);
        rc_vedios.setLayoutManager(layoutManager);
        adapter=new All_Audio_Adapter_list(Files, AllAudios.this);
        rc_vedios.setAdapter(adapter);
        rc_vedios.addOnItemTouchListener(new RecyclerItemTouch(AllAudios.this, rc_vedios, this));
    }
    private void GridStyleRecyclerView(ArrayList<VideoFile> Files)
    {
        layoutManager=new GridLayoutManager(AllAudios.this,3);
        rc_vedios.setLayoutManager(layoutManager);
        adapter_grid=new All_Audio_Adapter_Grid(Files, AllAudios.this);
        rc_vedios.setAdapter(adapter_grid);
        rc_vedios.addOnItemTouchListener(new RecyclerItemTouch(AllAudios.this, rc_vedios, this));

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
