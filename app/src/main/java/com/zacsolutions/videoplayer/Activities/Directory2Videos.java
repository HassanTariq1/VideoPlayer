package com.zacsolutions.videoplayer.Activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import com.zacsolutions.videoplayer.Adapters.All_Directory_Adapter_Grid;
import com.zacsolutions.videoplayer.Adapters.All_Directory_Adapter_list;
import com.zacsolutions.videoplayer.Adapters.All_Videos_Adapter_Grid;
import com.zacsolutions.videoplayer.Adapters.All_Videos_Adapter_list;
import com.zacsolutions.videoplayer.Adapters.RecyclerItemTouch;
import com.zacsolutions.videoplayer.All_Videos_Activity;
import com.zacsolutions.videoplayer.MainActivity;
import com.zacsolutions.videoplayer.PojoClass.DirectoryFile;
import com.zacsolutions.videoplayer.PojoClass.VideoFile;
import com.zacsolutions.videoplayer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class Directory2Videos extends AppCompatActivity
        implements
        //RecyclerItemTouch.OnItemClickListener,
        NavigationView.OnNavigationItemSelectedListener
   , All_Videos_Adapter_list.OnDeleteClickListener{
    static ArrayList<VideoFile> data = new ArrayList<>();
    boolean subfiles = false;
    RecyclerView rc_vedios;
    RecyclerView.LayoutManager layoutManager;
    All_Videos_Adapter_list adapter;
    All_Videos_Adapter_Grid adapter_grid;
    private InterstitialAd mInterstitialAd;
    String uri, title;
    ContentResolver contentResolver;
    DrawerLayout drawer;
    ImageView img_menu;
    boolean alreadygrid = false;
    private AdView mAdView;
    TextView tv_title;
    //    File root;
    ConstraintLayout cl_audio;
    ConstraintLayout cl_vedio;
    boolean thread = true;
    Handler handler;
    String[] spinner_array = {"Select","Play","Play as Audio", "Information","Share"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_audios);
        MobileAds.initialize(Directory2Videos.this, "ca-app-pub-7809883325778350/8307934276");
//        contentResolver=getContentResolver();
        Intent intent = getIntent();

        title = intent.getStringExtra("name");
        uri = intent.getStringExtra("uri");

        contentResolver = getContentResolver();
        handler=new Handler();
        //root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(Directory2Videos.this);
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
        tv_title.setText(title);
        img_menu = (ImageView) findViewById(R.id.img_menu);
        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!alreadygrid) {
                    floodDataGrid(data);
                    img_menu.setImageResource(R.drawable.list_white);
                    alreadygrid = true;
                } else if (alreadygrid) {
                    floodData(data);
                    img_menu.setImageResource(R.drawable.grid_white);
                    alreadygrid = false;
                }
            }
        });
        cl_vedio = findViewById(R.id.cl_videos);
        cl_vedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Directory2Videos.this, All_Videos_Activity.class);
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
                Intent intent = new Intent(Directory2Videos.this, AllAudios.class);
                startActivity(intent);
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        rc_vedios = (RecyclerView) findViewById(R.id.rc_directory);

        new Thread(new Runnable() {
            public void run() {
                while (thread) {
                    recursiveFunction(uri);
                }
                // finish();
            }
        }).start();
        // floodData(recursiveFunction(uri));
    }

    //    public ArrayList<VideoFile> printNamesToLogCat (Context context) {
////        String selection=MediaStore.Video.Media.DATA +" like?";
////        String[] selectionArgs=new String[]{title};
////       Cursor videocursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
////                parameters, selection, selectionArgs, MediaStore.Video.Media.DATE_TAKEN + " DESC");
////        Cursor c = context.getContentResolver().query(uri,
////                projection,
////                MediaStore.Audio.Media.DATA + " like ? ",
////                new String[]{"%yourFolderName%"}, // Put your device folder / file location here.
////                null);
////        Uri uri =Uri.parse(title);
//
//        Uri allvediouri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//        String[] projection = {MediaStore.Video.VideoColumns.DATA ,
//                MediaStore.Video.VideoColumns.DISPLAY_NAME,
//                MediaStore.Video.VideoColumns.DURATION };
//        Cursor c = context.getContentResolver().query(allvediouri, projection, MediaStore.Video.Media.DATA+" like ? ", new String[]{title}, null);
//
//    /*
//        String[] projection = { MediaStore.Video.VideoColumns.DATA ,
//                MediaStore.Video.VideoColumns.DISPLAY_NAME,
//                MediaStore.Video.VideoColumns.DURATION };*/
////        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
//        //Uri allvediouri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
////        String[] projection = {MediaStore.Video.VideoColumns.DATA,
////                MediaStore.Video.VideoColumns.TITLE, MediaStore.Audio.AudioColumns.ALBUM,
////                MediaStore.Audio.ArtistColumns.ARTIST,};
//
//
////        Cursor c = context.getContentResolver().query(allvediouri,
////                projection, MediaStore.Video.Media.DATA + " like ? ",
////                new String[]{title}, null);
//        VideoFile file;
//        if (c != null) {
//            while (c.moveToNext()) {
//                Log.d("VIDEO", c.getString(0));
//                file = new VideoFile(
//                        c.getString(0),
//                        c.getString(1),
//                        c.getLong(2));
//                Log.d("LIST",file.getName());
//                data.add(file);
//
//            }
//            c.close();
//            Collections.reverse(data);
//            return data;
//        }
//
//        return null;
//    }
    public void recursiveFunction(String dirPath) {
        final ArrayList<VideoFile> arrayList = new ArrayList<>();
        File f = new File(dirPath);
        final File[] files = f.listFiles();

        if (files != null) {
            for (int i = 0; i <= files.length - 1; i++) {
                if (files[i].isFile()) {
                    String mid = files[i].getName().substring(files[i].getName().lastIndexOf(".") + 1);
                    //String ext = file.getName().substring(mid + 1, file.getName().length());

                    if (mid.equals("mp4")
                            || mid.equals("mkv")
                            || mid.equals("avi")
                            || mid.equals("wmv")
                            || mid.equals("avchd")
                            || mid.equals("flv")
                            || mid.equals("swf")) {
                        // arrayList.add(new DirectoryFile(files[i].getAbsolutePath(), files[i].getName()));

                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
                        // retriever.setDataSource(Directory2Videos.this, Uri.fromFile(new File(files[i].getAbsolutePath())));
                        // String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

//                        MediaPlayer mp = MediaPlayer.create(getBaseContext(), Uri.parse(files[i].getAbsolutePath()));
//                        int duration = mp.getDuration();
                        long timeInMillisec = Long.parseLong("2");
                        Log.d("URIDirecotry", files[i].getAbsolutePath());
                        arrayList.add(new VideoFile(files[i].getAbsolutePath(), files[i].getName(), timeInMillisec));
                    }
                } else {
                    recursiveFunction(files[i].getAbsolutePath());
                }

            }

        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                floodData(arrayList);
            }
        });

        thread = false;

//        if (files != null) {
//            for (int i=0;i<=files.length-1;i++) {
//                if (files[i].isFile()) {
//                    String  mid = files[i].getName().substring(files[i].getName().lastIndexOf(".")+ 1);
//                    //String ext = file.getName().substring(mid + 1, file.getName().length());
//
//                    if (mid.equals("mp4")
//                            || mid.equals("mkv")
//                            || mid.equals("avi")
//                            || mid.equals("wmv")
//                            || mid.equals("avchd")
//                            || mid.equals("flv")
//                            || mid.equals("swf")) {
//                        // arrayList.add(new DirectoryFile(files[i].getAbsolutePath(), files[i].getName()));
//
//                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
////use one of overloaded setDataSource() functions to set your data source
//                       // retriever.setDataSource(Directory2Videos.this, Uri.fromFile(new File(files[i].getAbsolutePath())));
//                       // String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//
//                        MediaPlayer mp = MediaPlayer.create(this, Uri.parse(files[i].getAbsolutePath()));
//                        int duration = mp.getDuration();
//                        long timeInMillisec = Long.parseLong("2" );
//                        Log.d("URIDirecotry",files[i].getAbsolutePath());
//                      arrayList.add(new VideoFile(files[i].getAbsolutePath(),files[i].getName(),timeInMillisec));
//                    }
//                }else
//                {
//                    recursiveFunction(files[i].getAbsolutePath());
//                }
//
//            }
//
//        }

//        Uri urivideos = MediaStore.Video.Media.getContentUri(dirPath);
//        String[] projection = { MediaStore.Video.VideoColumns.DATA ,
//                MediaStore.Video.VideoColumns.DISPLAY_NAME,
//                MediaStore.Video.VideoColumns.DURATION };
//       // Cursor c = Directory2Videos.this.getContentResolver().query(urivideos, projection, null, null, null);
//        Cursor mediaCursor = getContentResolver().query( MediaStore.Files.getContentUri("external"),
//                null,
//                MediaStore.Images.Media.DATA + " like ? ",
//                new String[] {uri},
//                null);
//        VideoFile file;
//        if (mediaCursor != null) {
//            while (mediaCursor.moveToNext()) {
////                Log.d("VIDEO", c.getString(0));
//                file = new VideoFile(
//                        mediaCursor.getString(0),
//                        mediaCursor.getString(1),
//                        mediaCursor.getLong(2));
//                data.add(file);
//                Log.d("DATA","1"+mediaCursor.getString(0));
//                Log.d("DATA","2"+mediaCursor.getString(1));
//                Log.d("DATA","3"+mediaCursor.getString(2));
//                Log.d("DATA","4"+mediaCursor.getString(3));
//                Log.d("DATA","6"+mediaCursor.getString(4));
//                //   Log.d("VIDEO", fileuri);
//            }
//            mediaCursor.close();
//            Collections.reverse(data);
//            return data;
//        }
        // return arrayList;
    }

    private void floodData(ArrayList<VideoFile> Files) {
        if (Files != null) {
            ListStyleRecyclerView(Files);
        }
    }

    private void floodDataGrid(ArrayList<VideoFile> Files) {
        if (Files != null) {
            GridStyleRecyclerView(Files);
        }
    }

    private void ListStyleRecyclerView(ArrayList<VideoFile> Files) {
        layoutManager = new GridLayoutManager(Directory2Videos.this, 1);
        rc_vedios.setLayoutManager(layoutManager);
        adapter = new All_Videos_Adapter_list(Files, Directory2Videos.this,spinner_array,Directory2Videos.this);
        rc_vedios.setAdapter(adapter);
        // rc_vedios.addOnItemTouchListener(new RecyclerItemTouch(Directory2Videos.this, rc_vedios, this));
    }

    private void GridStyleRecyclerView(ArrayList<VideoFile> Files) {
        layoutManager = new GridLayoutManager(Directory2Videos.this, 3);
        rc_vedios.setLayoutManager(layoutManager);
        adapter_grid = new All_Videos_Adapter_Grid(Files, Directory2Videos.this,spinner_array);
        rc_vedios.setAdapter(adapter_grid);
       //   rc_vedios.addOnItemTouchListener(new RecyclerItemTouch(Directory2Videos.this, rc_vedios));

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

//    @Override
//    public void onItemClick(View view, int position) {
//
//    }
//
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

    @Override
    public void OnDeleteClickListener(int myNote) {
        deletfile( myNote);
    }
    public  void  deletfile(int position){
        File file = new File(data.get(position).getUri());
        boolean deleted = file.delete();
        file.deleteOnExit();
        Log.d("Delete", String.valueOf(deleted));

        if (!deleted) {
            boolean deleted2 = false;
            deleted2 = file.getAbsoluteFile().delete();
            Log.d("Delete", String.valueOf(deleted2));
        }
    }
}
