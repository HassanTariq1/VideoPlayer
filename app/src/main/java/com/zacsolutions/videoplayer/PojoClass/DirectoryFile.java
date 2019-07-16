package com.zacsolutions.videoplayer.PojoClass;

import java.io.File;

/**
 * Created by Pravinyo on 6/15/2017.
 */

public class DirectoryFile {
    private String mName;
    private String mUri;
    private File file;

    public DirectoryFile(String url, String name,File file){
        mName=name;
        mUri=url;
        this.file=file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getName() {
        return mName;
    }


    public String getUri(){
        return mUri;
    }

}
