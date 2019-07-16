package com.zacsolutions.videoplayer.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zacsolutions.videoplayer.Activities.AllAudios;
import com.zacsolutions.videoplayer.Activities.MusicPlayer;
import com.zacsolutions.videoplayer.Activities.VideoAudioplayer;
import com.zacsolutions.videoplayer.Activities.Videoplayer;
import com.zacsolutions.videoplayer.All_Videos_Activity;
import com.zacsolutions.videoplayer.Config;
import com.zacsolutions.videoplayer.PojoClass.VideoFile;
import com.zacsolutions.videoplayer.R;
import com.zacsolutions.videoplayer.Services.AudioService;
import com.zacsolutions.videoplayer.Services.VideoService;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class All_Videos_Adapter_list extends RecyclerView.Adapter<All_Videos_Adapter_list.categriesViewHolder> {

    ArrayList<VideoFile> arrayList;
    Activity activity;
    All_Videos_Activity all_videos_activity = new All_Videos_Activity();
    String spinner_selected;
    String[] spinner_arry = new String[5];
    private OnDeleteClickListener onDeleteClickListener;
    MediaMetadataRetriever retriever;
    String Size_Width;
    String Size_Height;
    String file_name;
    String loaction;
    String size;
    String time2;

    public interface OnDeleteClickListener {
        void OnDeleteClickListener(int myNote);
    }

    public All_Videos_Adapter_list(ArrayList<VideoFile> arrayList, Activity activity, String[] spinner_arry, OnDeleteClickListener listener) {
        this.arrayList = arrayList;
        this.activity = activity;
        this.spinner_arry = spinner_arry;
        this.onDeleteClickListener = listener;
        retriever = new MediaMetadataRetriever();
    }


    @Override
    public categriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.video_item_list, parent, false);
        return new categriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull categriesViewHolder holder, final int position) {
        try {
            retriever.setDataSource(activity, Uri.parse(arrayList.get(position).getUri()));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
             time2 = getTime(Long.parseLong(time));
            holder.tv_duration.setText(time2);
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
//        retriever.setDataSource(activity, Uri.parse(arrayList.get(position).getUri()));
//        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//        String time2 = getTime(Long.parseLong(time));
//        holder.tv_duration.setText(time2);
//        holder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(activity,arrayList.get(position).getUri().toString(),Toast.LENGTH_SHORT).show();
//            }
//        });

        holder.tv_name.setText(arrayList.get(position).getName());
        Glide.with(activity)
                .load(Uri.fromFile(new File(arrayList.get(position).getUri())))
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, VideoService.class);
                activity.stopService(intent);

                SharedPreferences sharedPreferences =activity.getApplicationContext()
                        .getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Config.SHARED_PREF_Video,arrayList.get(position).getUri());
                //editor.putString(Config.SHARED_PREF_AUDIO_Name,title);
                editor.commit();

                Intent videoUrlIntent = new Intent(activity, Videoplayer.class);
                videoUrlIntent.putExtra("URL", arrayList.get(position).getUri());
                videoUrlIntent.putExtra("TITLE", arrayList.get(position).getName());
                activity.startActivity(videoUrlIntent);


            }
        });
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (activity, android.R.layout.simple_spinner_item,
                        spinner_arry);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        holder.spinner_options.setAdapter(spinnerArrayAdapter);

        holder.spinner_options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Notify the selected item text
                spinner_selected = (String) adapterView.getSelectedItem();
                // Toast.makeText(activity,spinner_selected,Toast.LENGTH_LONG).show();
                if (spinner_selected.equals("Delete")) {
//                    File file = new File(arrayList.get(position).getUri());
//                    boolean deleted = file.delete();
//                    file.deleteOnExit();
//                    Log.d("Delete", String.valueOf(deleted));
//
//                    if (!deleted) {
//                        boolean deleted2 = false;
//                        deleted2 = file.getAbsoluteFile().delete();
//                        Log.d("Delete", String.valueOf(deleted2));
//                        //                        if (!deleted2) {
////                            boolean deleted3 = activity.deleteFile(file.getPath());
////                            Log.d("Delete", String.valueOf(deleted3));
//                        // }
////                        if (!deleted2)
//
//                    //}
                    //}
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.OnDeleteClickListener(position);
                    }
                } else if (spinner_selected.equals("Play")) {
                    Intent videoUrlIntent = new Intent(activity, Videoplayer.class);
                    videoUrlIntent.putExtra("URL", arrayList.get(position).getUri());
                    videoUrlIntent.putExtra("TITLE", arrayList.get(position).getName());
                    activity.startActivity(videoUrlIntent);

                } else if (spinner_selected.equals("Play as Audio")) {
                    Intent videoUrlIntent = new Intent(activity, VideoAudioplayer.class);
                    videoUrlIntent.putExtra("name", arrayList.get(position).getName());
                    videoUrlIntent.putExtra("uri", arrayList.get(position).getUri());
                    activity.startActivity(videoUrlIntent);

                } else if (spinner_selected.equals("Information")) {
                    File file = new File(arrayList.get(position).getUri());
//                Log.d("Information", String.valueOf(file.getTotalSpace() * 0.000001));
//                Log.d("Information", String.valueOf(file.getAbsolutePath()));
//                Log.d("Information", String.valueOf(file.getName()));


                    //MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source

                    Size_Width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                    Size_Height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                    file_name = file.getName();
                    loaction = file.getAbsolutePath();
                    size = size(file.length());
                    ChoseOption();
//                    Log.d("info", Size_Width);
//                    Log.d("info", Size_Height);
//                    Log.d("info", file_name);
//                    Log.d("info", loaction);
//                    Log.d("info", size);


                } else if (spinner_selected.equals("Share")) {
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(arrayList.get(position).getUri()));
                    intent.setType("video/*");
                    activity.startActivity(intent);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class categriesViewHolder extends RecyclerView.ViewHolder {

        TextView tv_duration, tv_name;
        ImageView imageView;
        Spinner spinner_options;

        public categriesViewHolder(View itemView) {
            super(itemView);

            tv_duration = (TextView) itemView.findViewById(R.id.duration);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            imageView = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            spinner_options = (Spinner) itemView.findViewById(R.id.spinner);
        }

    }

    private String getTime(Long duration) {
        Long seconds = duration / 1000;
        Long minutes = seconds / 60;
        if (minutes < 1) {
            return seconds + " sec";
        } else {
            double hours = minutes / 60.0;
            if (hours < 1.0) {
                return minutes + " min";
            } else {
                //it is more than one hour;
                int hour = (int) hours;
                int minute = (int) (hours - hour * 1.0) * 60;

                return hour + ":" + minute;
            }
        }
    }

    public String size(long size) {
        String hrSize = "";
        Long k = size/1024;
        Long m = k / 1024;
        DecimalFormat dec = new DecimalFormat("0.0");

        if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else {
            hrSize = dec.format(size).concat(" KB");
        }
        return hrSize;
    }

    private void ChoseOption() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.vedio_info_box, null);
        builder.setView(dialogView);
        TextView tv_name, tv_time, tv_width, tv_hight, tv_size, tv_location;
        tv_name = dialogView.findViewById(R.id.tv_name);
        tv_time = dialogView.findViewById(R.id.tv_time);
        tv_width = dialogView.findViewById(R.id.tv_width);
        tv_hight = dialogView.findViewById(R.id.tv_hight);
        tv_size = dialogView.findViewById(R.id.tv_size);
        tv_location = dialogView.findViewById(R.id.tv_location);

        tv_name.setText(file_name);
        tv_time.setText(time2);
        tv_width.setText(Size_Width);
        tv_hight.setText(Size_Height);
        tv_size.setText(size);
        tv_location.setText(loaction);

        builder.setTitle("File Properties");

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false);

        builder.show();
    }
}
