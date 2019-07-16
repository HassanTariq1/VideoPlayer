package com.zacsolutions.videoplayer.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zacsolutions.videoplayer.Activities.Directories;
import com.zacsolutions.videoplayer.Activities.Directory2Videos;
import com.zacsolutions.videoplayer.PojoClass.DirectoryFile;
import com.zacsolutions.videoplayer.PojoClass.DirectoryFile2;
import com.zacsolutions.videoplayer.PojoClass.VideoFile;
import com.zacsolutions.videoplayer.R;

import java.io.File;
import java.util.ArrayList;

public class All_Directory_Adapter_Grid extends RecyclerView.Adapter<All_Directory_Adapter_Grid.categriesViewHolder>{

    ArrayList<DirectoryFile2> arrayList;
    Activity activity;
    public All_Directory_Adapter_Grid(ArrayList<DirectoryFile2> arrayList, Activity activity) {
        this.arrayList = arrayList;
        this.activity = activity;
    }


    @Override
    public categriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.directory_item_grid,parent,false);
        return new  categriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull categriesViewHolder holder, final int position)
    {
        holder.tv_name.setText(arrayList.get(position).getName());
//        Glide.with(activity)
//                .load(Uri.fromFile(new File(arrayList.get(position).getUri())))
//                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent videoUrlIntent = new Intent(activity, Directory2Videos.class);
                videoUrlIntent.putExtra("uri",arrayList.get(position).getUri());
                videoUrlIntent.putExtra("name",arrayList.get(position).getName());
                activity.startActivity(videoUrlIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class categriesViewHolder extends RecyclerView.ViewHolder{

        TextView tv_duration,tv_name;
        ImageView imageView;

        public categriesViewHolder(View itemView) {
            super(itemView);

            tv_duration=(TextView)itemView.findViewById(R.id.duration);
            tv_name=(TextView)itemView.findViewById(R.id.file_name);
            imageView=(ImageView)itemView.findViewById(R.id.thumbnail_image);
        }
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
}
