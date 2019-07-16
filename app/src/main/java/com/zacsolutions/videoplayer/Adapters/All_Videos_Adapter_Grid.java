package com.zacsolutions.videoplayer.Adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
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
import com.zacsolutions.videoplayer.All_Videos_Activity;
import com.zacsolutions.videoplayer.PojoClass.VideoFile;
import com.zacsolutions.videoplayer.R;
import com.zacsolutions.videoplayer.Activities.Videoplayer;

import java.io.File;
import java.util.ArrayList;

public class All_Videos_Adapter_Grid extends RecyclerView.Adapter<All_Videos_Adapter_Grid.categriesViewHolder> {

    ArrayList<VideoFile> arrayList;
    Activity activity;
    String spinner_selected;
    String[] spinner_arry = new String[5];

    public All_Videos_Adapter_Grid(ArrayList<VideoFile> arrayList, Activity activity, String[] spinner_arry) {
        this.arrayList = arrayList;
        this.activity = activity;
        this.spinner_arry = spinner_arry;
    }


    @Override
    public categriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.video_item_grid, parent, false);
        return new categriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull categriesViewHolder holder, final int position) {
        holder.tv_duration.setText(getTime(arrayList.get(position).getDuration()));
        holder.tv_name.setText(arrayList.get(position).getName());

        Glide.with(activity)
                .load(Uri.fromFile(new File(arrayList.get(position).getUri())))
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                if (spinner_selected.equals("Delete"))
                {

                } else if (spinner_selected.equals("Play")) {
                    Intent videoUrlIntent = new Intent(activity, Videoplayer.class);
                    videoUrlIntent.putExtra("URL", arrayList.get(position).getUri());
                    videoUrlIntent.putExtra("TITLE", arrayList.get(position).getName());
                    activity.startActivity(videoUrlIntent);

                } else if (spinner_selected.equals("Play as Audio"))
                {


                } else if (spinner_selected.equals("Information")) {


                } else if (spinner_selected.equals("Share")) {
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(arrayList.get(position).getUri()));
                    intent.setType("video/*");
                    activity.startActivity(intent);
                }
                //   Toast.makeText(activity,spinner_selected,Toast.LENGTH_LONG).show();

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
            tv_name = (TextView) itemView.findViewById(R.id.file_name);
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
}
