package com.zacsolutions.videoplayer.PojoClass;

import java.io.File;

/**
 * Created by Pravinyo on 6/15/2017.
 */

public class DirectoryFile2 {
    private String mName;
    private String mUri;


    public DirectoryFile2(String url, String name){
        mName=name;
        mUri=url;
    }
    public String getName() {
        return mName;
    }
    public String getUri(){
        return mUri;
    }

}
