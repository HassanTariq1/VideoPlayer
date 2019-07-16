package com.zacsolutions.videoplayer.PojoClass;

public class Main_Video_Pojo
{
    String id;
    int video_url;
    int channel_url;
    String video_name;
    String video_details;
    String video_lenth;

    public Main_Video_Pojo(String id, int video_url, int channel_url, String video_name, String video_details, String video_lenth) {
        this.video_url = video_url;
        this.channel_url = channel_url;
        this.video_name = video_name;
        this.video_details = video_details;
        this.video_lenth = video_lenth;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVideo_url() {
        return video_url;
    }

    public void setVideo_url(int video_url) {
        this.video_url = video_url;
    }

    public int getChannel_url() {
        return channel_url;
    }

    public void setChannel_url(int channel_url) {
        this.channel_url = channel_url;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getVideo_details() {
        return video_details;
    }

    public void setVideo_details(String video_details) {
        this.video_details = video_details;
    }

    public String getVideo_lenth() {
        return video_lenth;
    }

    public void setVideo_lenth(String video_lenth) {
        this.video_lenth = video_lenth;
    }
}
