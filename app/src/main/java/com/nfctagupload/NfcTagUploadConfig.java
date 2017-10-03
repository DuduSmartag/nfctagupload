package com.nfctagupload;

import android.content.Context;
import android.util.Log;

import com.usmani.android.RameezFileReader;
import com.usmani.android.RameezFileWriter;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by Rameez Usmani on 8/15/2017.
 */

public class NfcTagUploadConfig {

    private static final String TAG=NfcTagUploadConfig.class.getName();

    public int closeInterval=5;
    public int keepAliveInterval=5;
    public int internetTimeout=5;
    public String baseURI="https://calyxac.smartagid.com/";
    public String keepAliveURI="https://devkeepalive.smartagid.com/api/KeepAlive";
    public boolean isDebug=true;
    public boolean isEntrance=true;

    private static final String fileName="nfc_tag_upload_config.txt";

    public void writeToFile(Context ctx)
    throws Exception{
        JSONObject jobj=new JSONObject();
        jobj.put("closeInterval",closeInterval);
        jobj.put("keepAliveInterval",keepAliveInterval);
        jobj.put("internetTimeout",internetTimeout);
        jobj.put("baseURI",baseURI);
        jobj.put("keepAliveURI",keepAliveURI);
        jobj.put("isEntrance",isEntrance);

        String str=jobj.toString();
        Log.d(TAG,"config json: "+str);

        //File fd=new File(ctx.getFilesDir(),fileName);
        File fd=new File(AppCache.getAppDirectoryPath(),fileName);
        Log.d(TAG,"Config file: "+fd.getPath());
        if (fd.exists()){
            fd.delete();
        }
        fd.createNewFile();
        RameezFileWriter.writeFile(fd,str.getBytes());
    }

    public static NfcTagUploadConfig readConfig(Context ctx)
    throws Exception{
        NfcTagUploadConfig config=new NfcTagUploadConfig();
        //File fd=new File(ctx.getFilesDir(),fileName);
        File fd=new File(AppCache.getAppDirectoryPath(),fileName);
        Log.d(TAG,"Config file: "+fd.getPath());
        if (!fd.exists()){
            return config;
        }
        byte[] buff= RameezFileReader.readFile(fd);
        if (buff==null){
            return config;
        }
        String str=new String(buff,0,buff.length);
        Log.d(TAG,"Read json: "+str);
        JSONObject obj=new JSONObject(str);
        config.closeInterval=obj.getInt("closeInterval");
        config.keepAliveInterval=obj.getInt("keepAliveInterval");
        config.internetTimeout=obj.getInt("internetTimeout");
        config.baseURI=obj.getString("baseURI");
        config.keepAliveURI=obj.getString("keepAliveURI");
        config.isEntrance=obj.getBoolean("isEntrance");

        return config;
    }
}
