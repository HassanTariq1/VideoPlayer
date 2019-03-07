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
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.zacsolutions.videoplayer.Activities.Videoplayer;
import com.zacsolutions.videoplayer.Adapters.All_Videos_Adapter_Grid;
import com.zacsolutions.videoplayer.Adapters.All_Videos_Adapter_list;
import com.zacsolutions.videoplayer.Adapters.RecyclerItemTouch;
import com.zacsolutions.videoplayer.PojoClass.VideoFile;

import java.io.File;
import java.util.ArrayList;

public class All_Videos_Activity extends AppCompatActivity implements  RecyclerItemTouch.OnItemClickListener {

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
    ContentResolver contentResolver;
    static ArrayList<VideoFile> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all__videos__recyler);
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
        rc_vedios=(RecyclerView)findViewById(R.id.rc_vedios);
        checkPermission();
    }

    private void ListStyleRecyclerView(ArrayList<VideoFile> Files)
    {
        layoutManager=new GridLayoutManager(All_Videos_Activity.this,1);
        rc_vedios.setLayoutManager(layoutManager);
        adapter=new All_Videos_Adapter_list(Files, All_Videos_Activity.this);
        rc_vedios.setAdapter(adapter);
        rc_vedios.addOnItemTouchListener(new RecyclerItemTouch(All_Videos_Activity.this, rc_vedios, this));
    }
    private void GridStyleRecyclerView(ArrayList<VideoFile> Files)
    {
        layoutManager=new GridLayoutManager(All_Videos_Activity.this,2);
        rc_vedios.setLayoutManager(layoutManager);
        adapter_grid=new All_Videos_Adapter_Grid(Files, All_Videos_Activity.this);
        rc_vedios.setAdapter(adapter_grid);
        rc_vedios.addOnItemTouchListener(new RecyclerItemTouch(All_Videos_Activity.this, rc_vedios, this));

    }
    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            // Launch the camera if the permission exists
            floodData(printNamesToLogCat(this));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    floodData(printNamesToLogCat(this));
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
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
    public static ArrayList<VideoFile> printNamesToLogCat(Context context) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Video.VideoColumns.DATA ,
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DURATION };
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);

        VideoFile file;
        if (c != null) {
            while (c.moveToNext()) {
                Log.d("VIDEO", c.getString(0));
                file = new VideoFile(
                        c.getString(0),
                        c.getString(1),
                        c.getLong(2));
                data.add(file);
            }
            c.close();
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

    @Override
    public void onItemClick(View view, int position)
    {
//        ConstraintLayout constraintLayout= (ConstraintLayout) view;
//       CardView cardView= (CardView) constraintLayout.getChildAt(0);
//       ConstraintLayout constraintLayout1=(ConstraintLayout)cardView.getChildAt(0);
//        if (view==view.findViewById(R.id.tv_name))
//        {
            Toast.makeText(All_Videos_Activity.this,data.get(position).getUri().toString(),Toast.LENGTH_SHORT).show();
            adpterclick(data.get(position).getUri(),data.get(position).getName());}
//         }

    @Override
    public void onLongItemClick(View view, int position)
    {
        ChoseOption(data.get(position).getUri());
        Toast.makeText(All_Videos_Activity.this,data.get(position).getUri(),Toast.LENGTH_SHORT).show();

    }
    private void ChoseOption(final String  uri)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(All_Videos_Activity.this);
        LayoutInflater inflater = All_Videos_Activity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialouge_layout, null);
        builder.setView(dialogView);
        final Button share,delete;
        share=dialogView.findViewById(R.id.btn_share);
        delete=dialogView.findViewById(R.id.btn_delete);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogbox(uri);
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
    private void dialogbox(final String  uri)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to delete?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {


                        contentResolver.delete(Uri.parse(uri), null, null);
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
}
