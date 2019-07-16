package com.zacsolutions.videoplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zacsolutions.videoplayer.Activities.Videoplayer;
import com.zacsolutions.videoplayer.Adapters.FileAdapter;
import com.zacsolutions.videoplayer.PojoClass.VideoFile;

import java.util.ArrayList;

public class BrowseDirectoryActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 2 ;

    TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_directory);

        tv_title=(TextView)findViewById(R.id.title);
        tv_title.setText("All Videos");
        checkPermission();
    }

    private void floodData(ArrayList<VideoFile> Files) {
        if(Files != null){
            FileAdapter adapter = new FileAdapter(this,Files);
            final ListView listView = (ListView) findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //String url = listView.getItemAtPosition(position).toString();

                    LinearLayout parentLinearLayout = (LinearLayout) view;

                    LinearLayout innerLinearLayout = (LinearLayout) parentLinearLayout.getChildAt(1);


                    String name= ((TextView)innerLinearLayout.getChildAt(0)).getText().toString();
                    String url =((TextView)innerLinearLayout.getChildAt(2)).getText().toString();
                    Toast.makeText(getApplicationContext(),url, Toast.LENGTH_SHORT).show();

                    Intent videoUrlIntent = new Intent(getBaseContext(), Videoplayer.class);
                    videoUrlIntent.putExtra("URL",url);
                    videoUrlIntent.putExtra("TITLE",name);

                    startActivity(videoUrlIntent);
                }
            });



        }
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


    @Override
    public void onBackPressed() {
        finish();
    }
}
