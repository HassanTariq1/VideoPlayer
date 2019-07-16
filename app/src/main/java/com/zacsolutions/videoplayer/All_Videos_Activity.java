package com.zacsolutions.videoplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.zacsolutions.videoplayer.Activities.AllAudios;
import com.zacsolutions.videoplayer.Activities.Directories;
import com.zacsolutions.videoplayer.Activities.Videoplayer;
import com.zacsolutions.videoplayer.Adapters.All_Videos_Adapter_Grid;
import com.zacsolutions.videoplayer.Adapters.All_Videos_Adapter_list;
import com.zacsolutions.videoplayer.Adapters.RecyclerItemTouch;
import com.zacsolutions.videoplayer.PojoClass.VideoFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class All_Videos_Activity extends AppCompatActivity
        implements
        //RecyclerItemTouch.OnItemClickListener ,
        NavigationView.OnNavigationItemSelectedListener,
    All_Videos_Adapter_list.OnDeleteClickListener
{

    private static final int REQUEST_STORAGE_PERMISSION = 2 ;

    TextView tv_title;
    RecyclerView rc_vedios;
    RecyclerView.LayoutManager layoutManager;
    All_Videos_Adapter_list adapter;
    All_Videos_Adapter_Grid adapter_grid;
    ImageView img_menu;
    boolean alreadygrid=false;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    String uri,title;
    String from;
    DrawerLayout Drawer;
    ContentResolver contentResolver;
    DrawerLayout drawer;
    ConstraintLayout cl_directory;
    static ArrayList<VideoFile> data = new ArrayList<>();
    String[] spinner_array = {"Select","Play","Play as Audio", "Information","Share"};


    private static final String  READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int STORAGE_PERMISION_REQUEST_CODE = 1234;

    ConstraintLayout cl_audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_start);
        MobileAds.initialize(All_Videos_Activity.this,"ca-app-pub-7809883325778350/8307934276");
         contentResolver=getContentResolver();
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(All_Videos_Activity.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                Intent videoUrlIntent = new Intent(All_Videos_Activity.this, Videoplayer.class);
                videoUrlIntent.putExtra("URL",uri);
                videoUrlIntent.putExtra("TITLE",title);
                startActivity(videoUrlIntent);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
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
                    floodDataGrid(printNamesToLogCat(All_Videos_Activity.this));
                    img_menu.setImageResource(R.drawable.list_white);
                    alreadygrid=true;
                }else if (alreadygrid)
                {
                    floodData(printNamesToLogCat(All_Videos_Activity.this));
                    img_menu.setImageResource(R.drawable.grid_white);
                    alreadygrid=false;
                }
            }
        });
        cl_directory=findViewById(R.id.cl_folders);
        cl_directory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(All_Videos_Activity.this, Directories.class);
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
        cl_audio=findViewById(R.id.cl_audios);
        cl_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(All_Videos_Activity.this, AllAudios.class);
                startActivity(intent);
            }
        });


       // checkPermission();
        getLocationPermission();
    }
    private void getLocationPermission()
    {
//        Log.d(TAG,"getting location permissions");
        String[] permission={Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                READ_STORAGE)== PackageManager.PERMISSION_GRANTED)
        {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    WRITE_STORAGE)==PackageManager.PERMISSION_GRANTED)
            {
                floodData(printNamesToLogCat(this));


            }else
            {
                ActivityCompat.requestPermissions(this,permission,
                        STORAGE_PERMISION_REQUEST_CODE);
            }
        }else
        {
            ActivityCompat.requestPermissions(this,permission,
                    STORAGE_PERMISION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        Log.d(TAG,"permission called");
//        mLoactionPermissionGranted=false;
        switch (requestCode)
        {
            case STORAGE_PERMISION_REQUEST_CODE:
            {
                if (grantResults.length>0)

                {
                    for (int i=0;i<grantResults.length;i++)
                    {
                        if (grantResults[i]!=PackageManager.PERMISSION_GRANTED)
                        {
//                            Log.d(TAG,"permission failed");
//                            mLoactionPermissionGranted=false;
                            super.onBackPressed();
                            Toast.makeText(All_Videos_Activity.this,"Please Give Permissions to Proceed",Toast.LENGTH_LONG).show();
//                            finish();
                            data.clear();
                            getLocationPermission();
                        }else
                        {

                            floodData(printNamesToLogCat(this));
                        }
                    }

                }
            }
        }
    }
    private void ListStyleRecyclerView(ArrayList<VideoFile> Files)
    {
        layoutManager=new GridLayoutManager(All_Videos_Activity.this,1);
        rc_vedios.setLayoutManager(layoutManager);
        adapter=new All_Videos_Adapter_list(Files, All_Videos_Activity.this,spinner_array,All_Videos_Activity.this);
        rc_vedios.setAdapter(adapter);
        //rc_vedios.addOnItemTouchListener(new RecyclerItemTouch(All_Videos_Activity.this, rc_vedios, this));
    }
    private void GridStyleRecyclerView(ArrayList<VideoFile> Files)
    {
        layoutManager=new GridLayoutManager(All_Videos_Activity.this,2);
        rc_vedios.setLayoutManager(layoutManager);
        adapter_grid=new All_Videos_Adapter_Grid(Files, All_Videos_Activity.this,spinner_array);
        rc_vedios.setAdapter(adapter_grid);
       // rc_vedios.addOnItemTouchListener(new RecyclerItemTouch(All_Videos_Activity.this, rc_vedios, this));

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        // Called when you request permission to read and write to external storage
//        switch (requestCode) {
//            case REQUEST_STORAGE_PERMISSION: {
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // If you get permission, launch the camera
//                    floodData(printNamesToLogCat(this));
//                } else {
//                    // If you do not get permission, show a Toast
//                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
//                }
//                break;
//            }
//        }
//    }
    private void floodData(ArrayList<VideoFile> Files) {
        if(Files != null){
            ListStyleRecyclerView(Files);
           // Toast.makeText(All_Videos_Activity.this,String.valueOf(Files.size()),Toast.LENGTH_SHORT).show();
        }
    }
    private void floodDataGrid(ArrayList<VideoFile> Files) {
        if(Files != null){
            GridStyleRecyclerView(Files);
        }
    }
    public static ArrayList<VideoFile> printNamesToLogCat(Context context) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Video.VideoColumns.DATA ,
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DURATION };
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);

        VideoFile file;
        if (c != null) {
            while (c.moveToNext()) {
//                Log.d("VIDEO", c.getString(0));
                file = new VideoFile(
                        c.getString(0),
                        c.getString(1),
                        c.getLong(2));
                data.add(file);
//                Log.d("URI",c.getString(0));
             //   Log.d("VIDEO", fileuri);
            }
            c.close();
            Collections.reverse(data);
            return data;
        }

        return null;
    }
    public void adpterclick(String Url,String Title)
    {
      uri=Url;
      title=Title;
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {

            Intent videoUrlIntent = new Intent(All_Videos_Activity.this,Videoplayer.class);
            videoUrlIntent.putExtra("URL",uri);
            videoUrlIntent.putExtra("TITLE",uri);
            startActivity(videoUrlIntent);
        }


    }

//    @Override
//    public void onItemClick(View view, int position)
//    {
////        ConstraintLayout constraintLayout= (ConstraintLayout) view;
////       CardView cardView= (CardView) constraintLayout.getChildAt(0);
////       ConstraintLayout constraintLayout1=(ConstraintLayout)cardView.getChildAt(0);
////        if (view==view.findViewById(R.id.tv_name))
////        {
//            Toast.makeText(All_Videos_Activity.this,data.get(position).getUri().toString(),Toast.LENGTH_SHORT).show();
//            adpterclick(data.get(position).getUri(),data.get(position).getName());}
////         }
//
//    @Override
//    public void onLongItemClick(View view, int position)
//    {
//       // ChoseOption(data.get(position).getUri(),data.get(position).getName());
//
//    }
    private void ChoseOption(final String  uri,final String name)
    {   //  Toast.makeText(All_Videos_Activity.this,uri,Toast.LENGTH_SHORT).show();
        final AlertDialog.Builder builder = new AlertDialog.Builder(All_Videos_Activity.this);
        LayoutInflater inflater = All_Videos_Activity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialouge_layout, null);
        builder.setView(dialogView);
        final Button share,delete,btn_properties;
        share=dialogView.findViewById(R.id.btn_share);
        delete=dialogView.findViewById(R.id.btn_delete);
        btn_properties=dialogView.findViewById(R.id.btn_properties);
        btn_properties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Cursor returnCursor = getContentResolver().query(Uri.parse(uri), null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                btn_properties.setText(returnCursor.getString(nameIndex)+returnCursor.getString(sizeIndex));
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogbox(uri,name);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uri));
                intent.setType("video/*");
                startActivity(intent);
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i)
//            {
//
//            }
//        });
//        builder.setCancelable(false);

        builder.show();
    }
    private void dialogbox(final String  uri, final String name)
    {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to delete?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
//                        File file = null;
//                      //  file = new File(String.valueOf(Uri.parse(uri)));
//                        File myFile = new File(uri);
//                        myFile.getAbsolutePath();
   //                     boolean myFile = new File(uri).getAbsoluteFile().delete();
//                        String path = uri; // "file:///mnt/sdcard/FileName.mp3"
//                        File file = null;
//                        try {
//                            file = new File(new URI(uri));
//                        } catch (URISyntaxException e) {
//                            e.printStackTrace();
//                        }
//                        String dir = getFilesDir().getAbsolutePath();
//                        File f0 = new File(dir, name);
//                        boolean d0 = f0.delete();
                        // contentResolver.delete(Uri.parse(uri),null,null);
//
//                        File file = new File(uri);
//                        file = new File(file.getAbsolutePath());
//                      //  file.delete();
//                        if (file.exists())
//                        {
//                            //File file = new File(new URI(path));
//                            file.delete();
//                        } else
//                            {
//                                Toast.makeText(All_Videos_Activity.this,"File Don't Exist",Toast.LENGTH_LONG).show();
//                            }
//
//                        contentResolver.delete(Uri.parse(uri), null, null);
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        alertDialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
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

    @Override
    public void OnDeleteClickListener(int myNote)
    {

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
