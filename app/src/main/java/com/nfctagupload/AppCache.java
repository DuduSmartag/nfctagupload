package com.nfctagupload;

import android.os.Environment;

/**
 * Created by Rameez Usmani on 8/15/2017.
 */

public class AppCache {
    public static NfcTagUploadConfig config=new NfcTagUploadConfig();
    public static String rootPath= Environment.getExternalStorageDirectory().getPath();

    public static String getAppDirectoryPath(){
        String path=rootPath+"/";
        return path;
    }
}
