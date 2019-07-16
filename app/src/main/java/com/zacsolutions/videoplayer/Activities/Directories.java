package com.zacsolutions.videoplayer.Activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.zacsolutions.videoplayer.Adapters.All_Audio_Adapter_Grid;
import com.zacsolutions.videoplayer.Adapters.All_Audio_Adapter_list;
import com.zacsolutions.videoplayer.Adapters.All_Directory_Adapter_Grid;
import com.zacsolutions.videoplayer.Adapters.All_Directory_Adapter_list;
import com.zacsolutions.videoplayer.Adapters.RecyclerItemTouch;
import com.zacsolutions.videoplayer.All_Videos_Activity;
import com.zacsolutions.videoplayer.PojoClass.DirectoryFile;
import com.zacsolutions.videoplayer.PojoClass.DirectoryFile2;
import com.zacsolutions.videoplayer.PojoClass.VideoFile;
import com.zacsolutions.videoplayer.R;

import java.io.File;
import java.lang.Object;
import java.util.ArrayList;
import java.util.Collections;

public class Directories extends AppCompatActivity implements
//        RecyclerItemTouch.OnItemClickListener,
        NavigationView.OnNavigationItemSelectedListener
{

    static ArrayList<DirectoryFile> data = new ArrayList<>();
    static ArrayList<DirectoryFile2> subdata = new ArrayList<>();

    boolean subfiles=false;
    RecyclerView rc_vedios;
    RecyclerView.LayoutManager layoutManager;
    All_Directory_Adapter_list adapter;
    All_Directory_Adapter_Grid adapter_grid;
    private InterstitialAd mInterstitialAd;
    String uri, title;
    ContentResolver contentResolver;
    DrawerLayout drawer;
    ImageView img_menu;
    boolean alreadygrid = false;
    private AdView mAdView;
    TextView tv_title;
    File root;
    ConstraintLayout cl_audio;
    ConstraintLayout cl_vedio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directories);


        MobileAds.initialize(Directories.this, "ca-app-pub-7809883325778350/8307934276");
//        contentResolver=getContentResolver();

        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(Directories.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
//                Intent videoUrlIntent = new Intent(AllAudios.this, Videoplayer.class);
//                videoUrlIntent.putExtra("URL",uri);
//                videoUrlIntent.putExtra("TITLE",title);
//                startActivity(videoUrlIntent);
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tv_title = (TextView) findViewById(R.id.title);
        tv_title.setText("Video Directories");
        img_menu = (ImageView) findViewById(R.id.img_menu);
        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!alreadygrid) {
                    floodDataGrid(fromallviedo(Directories.this));
                    img_menu.setImageResource(R.drawable.list_white);
                    alreadygrid = true;
                } else if (alreadygrid) {
                    floodData(fromallviedo(Directories.this));
                    img_menu.setImageResource(R.drawable.grid_white);
                    alreadygrid = false;
                }
            }
        });
        cl_vedio = findViewById(R.id.cl_videos);
        cl_vedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Directories.this, All_Videos_Activity.class);
                startActivity(intent);
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
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT);
                } else {
                    drawer.openDrawer(Gravity.LEFT);
                }
            }
        });
        cl_audio = findViewById(R.id.cl_audios);
        cl_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Directories.this, AllAudios.class);
                startActivity(intent);
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        rc_vedios = (RecyclerView) findViewById(R.id.rc_directory);
        floodData(fromallviedo(this));
    }

    private void floodData(ArrayList<DirectoryFile2> Files) {
        if (Files != null) {
            ListStyleRecyclerView(Files);
        }
    }

    private void floodDataGrid(ArrayList<DirectoryFile2> Files) {
        if (Files != null) {
            GridStyleRecyclerView(Files);
        }
    }

    private void ListStyleRecyclerView(ArrayList<DirectoryFile2> Files) {
        layoutManager = new GridLayoutManager(Directories.this, 1);
        rc_vedios.setLayoutManager(layoutManager);
        adapter = new All_Directory_Adapter_list(Files, Directories.this);
        rc_vedios.setAdapter(adapter);
       // rc_vedios.addOnItemTouchListener(new RecyclerItemTouch(Directories.this, rc_vedios, this));
    }

    private void GridStyleRecyclerView(ArrayList<DirectoryFile2> Files) {
        layoutManager = new GridLayoutManager(Directories.this, 3);
        rc_vedios.setLayoutManager(layoutManager);
        adapter_grid = new All_Directory_Adapter_Grid(Files, Directories.this);
        rc_vedios.setAdapter(adapter_grid);
     //   rc_vedios.addOnItemTouchListener(new RecyclerItemTouch(Directories.this, rc_vedios, this));

    }

    public static ArrayList<DirectoryFile2> fromallviedo(Context context) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Video.VideoColumns.DATA ,
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DURATION };
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
        ArrayList<String> name = new ArrayList<String>();
//        VideoFile file;
        if (c != null) {
            while (c.moveToNext()) {
//                Log.d("VIDEO", c.getString(0));
//                file = new VideoFile(
//                        c.getString(0),
//                        c.getString(1),
//                        c.getLong(2));
                File file1 = new File(c.getString(0));
                String filename =file1.getParentFile().getName();
                Log.d("Absolutpath",file1.getParentFile().getAbsolutePath());
                if (name.size()==0)
                {
                    name.add(filename);
                    subdata.add(new DirectoryFile2(file1.getParentFile().getAbsolutePath(),filename));
                    //  add_to_cart.setBackgroundResource(R.color.redeasytoget);
                }   else
                {
                    for (int i=0;i<name.size();)
                    {
                        if (filename.equals(name.get(i)))
                        {
                            break;
                        }
                        else if (i==name.size()-1)
                        {
                            name.add(filename);
                            subdata.add(new DirectoryFile2(file1.getParentFile().getAbsolutePath(),filename));
//                            add_to_cart.setBackgroundResource(R.color.redeasytoget);
                            break;
                        }
                        else
                        {
                            i++;
                        }
                    }
                }

            }
            c.close();
            Collections.reverse(subdata);
            return subdata;
        }

        return null;
    }


    public ArrayList<DirectoryFile> printNamesToLogCat(Context context, File root) {

        File[] files = root.listFiles();
       // Toast.makeText(Directories.this,String.valueOf(files.length),Toast.LENGTH_SHORT).show();
        data.clear();
        Log.d("Files",root.getName()+"\t"+files[0].getName());
        if (files != null) {

//            for (File file : files) {
            for (int i=0;i<=files.length-1;i++){

                if (files[i].isDirectory()){
                   if (recusivefilecheck(files[i]))
                   {
                       data.add(new DirectoryFile(files[i].getAbsolutePath(), files[i].getName(), files[i]));
                   }
                }

//                boolean isVideo=recursiveFunction(files[i]);
//                if (isVideo) {
//                    data.add(new DirectoryFile(files[i].getAbsolutePath(), files[i].getName(),files[i]));
//                }
            }
            return data;
        }
        return null;
    }




    private boolean recusivefilecheck(File dirPath)
    {








        File[] files = dirPath.listFiles();
        if (files != null) {
            for (int i=0;i<=files.length-1;i++) {

                if(files[i].isDirectory())
                {
                    if (recursiveFunction(files[i]))
                    {
                     return true;
                    }
                }
                else {
                    String mid=getFileExtension(files[i]);
                    if (mid.equals(".mp4")
                            || mid.equals(".mkv")
                            || mid.equals(".avi")
                            || mid.equals(".wmv")
                            || mid.equals(".avchd")
                            || mid.equals(".flv")
                            || mid.equals(".swf"))
                        {
                            return true;
                    }
                }

            }

        }
        return false;

    }

    public static String getFileExtension(File file) {
        String extension = "";

        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                extension = name.substring(name.lastIndexOf("."));
            }
        } catch (Exception e) {
            extension = "";
        }

        return extension;

    }




    public boolean recursiveFunction(File dirPath) {
      //  ArrayList<DirectoryFile> arrayList = new ArrayList<>();
//        File f = new File(dirPath);
        boolean exist=false;
        File[] files = dirPath.listFiles();

        if (files != null) {
            for (int i=0;i<=files.length-1;i++) {

                if(files[i].isDirectory())
                {
                    //Log.d("isVideo",files[i].getName());
                    //Log.d("isVideo",files[i].getName());
                    exist = recursiveFunction(files[i]);
                    break;
                }
                else {
                    String mid=getFileExtension(files[i]);
                    //Log.d("extensionFile",dirPath+"/"+mid);
                    //String  mid = files[i].getName().substring(files[i].getName().lastIndexOf(".")+ 1);
                    //String ext = file.getName().substring(mid + 1, file.getName().length());
                    //Log.d("Extension",mid);
                    if (mid.equals(".mp4")
                            || mid.equals(".mkv")
                            || mid.equals(".avi")
                            || mid.equals(".wmv")
                            || mid.equals(".avchd")
                            || mid.equals(".flv")
                            || mid.equals(".swf")) {
                       // arrayList.add(new DirectoryFile(files[i].getAbsolutePath(), files[i].getName()));
                        //Log.d("fileExist",mid);
                        //Log.d("fileExist",dirPath.getName());
                      exist=true;
                      break;
                    }
                }

            }

        }
        return exist;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

//    @Override
//    public void onItemClick(View view, int position) {
//        adpterclick(subdata.get(position).getUri(),data.get(position).getName());
//    }
//    public void adpterclick(String Url,String Title)
//    {
//        uri=Url;
//        title=Title;
//        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        } else {
//
//            Intent videoUrlIntent = new Intent(Directories.this,Directory2Videos.class);
//            videoUrlIntent.putExtra("name",title);
//            videoUrlIntent.putExtra("uri",uri);
//            startActivity(videoUrlIntent);
//        }
//
//
//    }
//    @Override
//    public void onLongItemClick(View view, int position) {
//
//    }

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
