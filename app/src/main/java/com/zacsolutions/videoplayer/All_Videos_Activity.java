package com.zacsolutions.videoplayer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class All_Videos_Activity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 2 ;

    TextView tv_title;
    RecyclerView rc_vedios;
    RecyclerView.LayoutManager layoutManager;
    All_Videos_Adapter_list adapter;
    All_Videos_Adapter_Grid adapter_grid;
    ImageView img_menu;
    boolean alreadygrid=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all__videos__recyler);

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
    }
    private void GridStyleRecyclerView(ArrayList<VideoFile> Files)
    {
        layoutManager=new GridLayoutManager(All_Videos_Activity.this,2);
        rc_vedios.setLayoutManager(layoutManager);
        adapter_grid=new All_Videos_Adapter_Grid(Files, All_Videos_Activity.this);
        rc_vedios.setAdapter(adapter_grid);
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
        ArrayList<VideoFile> data = new ArrayList<>();
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

}