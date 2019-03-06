package com.zacsolutions.videoplayer.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zacsolutions.videoplayer.PojoClass.VideoFile;
import com.zacsolutions.videoplayer.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Pravinyo on 6/15/2017.
 */

public class FileAdapter extends ArrayAdapter<VideoFile> {

    Context mContext;
    public FileAdapter(@NonNull Context context, ArrayList<VideoFile> videoFiles) {
        super(context,0,videoFiles);
        mContext=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item,parent,false);

        }

        VideoFile currentVideo = getItem(position);

        String name=currentVideo.getName();
        String uri = currentVideo.getUri();
        Long duration = currentVideo.getDuration();


//        try {
//            imageView.setImageBitmap(retriveVideoFrameFromVideo(uri));
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.thumbnail_image);


        //imageView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(uri, MediaStore.Video.Thumbnails.MINI_KIND));

        TextView fileName = (TextView) listItemView.findViewById(R.id.file_name);
        fileName.setText(name);

        TextView fileDuration = (TextView) listItemView.findViewById(R.id.duration);
        fileDuration.setText(getTime(duration));

        TextView fileUri = (TextView) listItemView.findViewById(R.id.uri);
        fileUri.setText(uri);
        final ImageButton img= (ImageButton)listItemView.findViewById(R.id.menuu);
                img.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

                PopupMenu pop= new PopupMenu(mContext,img);
                pop.inflate(R.menu.menu_item);
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
                Toast.makeText(mContext,"Click on menu",Toast.LENGTH_SHORT).show();

        return false;
    }
});


    }
});
        Glide.with(mContext)
                .load(Uri.fromFile(new File(uri)))
                .into(imageView);

        return listItemView;
    }

    private String getTime(Long duration) {
        Long seconds = duration/1000;
        Long minutes = seconds/60;
        if(minutes<1){
            return seconds+" sec";
        }else{
            double hours = minutes/60.0;
            if(hours<1.0){
                return minutes+" min";
            }else{
                //it is more than one hour;
                int hour = (int)hours;
                int minute = (int)(hours-hour*1.0)*60;

                return hour+":"+minute;
            }
        }
    }

//    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable
//    {
//        Bitmap bitmap = null;
//        MediaMetadataRetriever mediaMetadataRetriever = null;
//        try
//        {
//            mediaMetadataRetriever = new MediaMetadataRetriever();
//            if (Build.VERSION.SDK_INT >= 14)
//                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
//            else
//                mediaMetadataRetriever.setDataSource(videoPath);
//            //   mediaMetadataRetriever.setDataSource(videoPath);
//            bitmap = mediaMetadataRetriever.getFrameAtTime();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());
//
//        } finally {
//            if (mediaMetadataRetriever != null) {
//                mediaMetadataRetriever.release();
//            }
//        }
//        return bitmap;
//    }
}
