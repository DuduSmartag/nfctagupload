package com.nfctagupload;

import android.os.Build;
import android.util.Log;

import com.modules.OfflineTag;
import com.usmani.android.RameezFileReader;
import com.usmani.android.RameezFileWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Rameez Usmani on 8/23/2017.
 */

public class OfflineTagUploader {
    private static final String TAG=OfflineTagUploader.class.getName();
    private static final String fileName="offline_tags.txt";
    private static final String tagsToSendFileName="tags_scanned_offline.txt";

    public volatile JSONArray queue=new JSONArray();
    public volatile JSONArray offlineQueue=new JSONArray();
    //private ArrayList<OfflineTag> mOfflineTags = new ArrayList<>();

    public OfflineTagUploader(){
    }

    private synchronized JSONObject readFile()
    throws Exception {
        Log.d(TAG,"readFile");
        JSONObject jobj=new JSONObject();
        File fd=new File(AppCache.getAppDirectoryPath(),fileName);
        Log.d(TAG,"Config file: "+fd.getPath());
        if (!fd.exists()){
            return jobj;
            //throw new Exception("File not found: "+fd.getPath());
        }
        byte[] buff= RameezFileReader.readFile(fd);
        String str=new String(buff,0,buff.length);
        Log.d(TAG,"Read json: "+str);
        JSONObject obj=new JSONObject(str);
        return obj;
    }

    private synchronized void writeFile()
    throws Exception {
        Log.d(TAG,"writeFile");
        File fd=new File(AppCache.getAppDirectoryPath(),fileName);
        Log.d(TAG,"Config file: "+fd.getPath());
        if (fd.exists()){
            fd.delete();
        }
        fd.createNewFile();
        JSONObject jobj=new JSONObject();
        jobj.put("tags",queue);
        String str=jobj.toString();
        Log.d(TAG,"Write json: "+str);
        RameezFileWriter.writeFile(fd,str.getBytes());
    }

    public synchronized void writeTempFile(String terminalUid, String tagID) throws Exception {
        Log.d(TAG,"writeTempFile");
        File fd=new File(AppCache.getAppDirectoryPath(),tagsToSendFileName);
        Log.d(TAG,"Config temp file: "+fd.getPath());
        if (fd.exists()){
            fd.delete();
        }
        fd.createNewFile();

        Date currentTime = Calendar.getInstance().getTime();
        //SimpleDateFormat postFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        //String dateStr = postFormat.format(currentTime);

        JSONObject newObj = new JSONObject();
        newObj.put("tagID",tagID);
        newObj.put("timeStamp",currentTime);
        newObj.put("terminalUid",terminalUid);
        offlineQueue.put(newObj);

        JSONObject jObj=new JSONObject();
        jObj.put("offline tags",offlineQueue);

        String str=jObj.toString();
        Log.d(TAG,"Write temp file json: "+str);
        RameezFileWriter.writeFile(fd,str.getBytes());
    }

    public JSONArray returnTempTags(){
        return offlineQueue;
    }

    public void clearTempTags(){
        offlineQueue = new JSONArray();
    }

    public synchronized void writeFileFromServer(JSONArray newQueue)
            throws Exception {
        Log.d(TAG,"write File to server");
        File fd=new File(AppCache.getAppDirectoryPath(),fileName);
        Log.d(TAG,"Config server file: "+fd.getPath());
        if (fd.exists()){
            fd.delete();
        }
        fd.createNewFile();
        JSONObject jObj=new JSONObject();
        jObj.put("tags",newQueue);
        String str=jObj.toString();
        Log.d(TAG,"Write server json: "+str);
        RameezFileWriter.writeFile(fd,str.getBytes());
    }

    public synchronized void addToQueue(String tag)
    throws Exception {
        Log.d(TAG,"addToQueue");
        queue.put(tag);
        writeFile();
    }

    public synchronized void removeFromQueue(int index)
    throws Exception {
        Log.d(TAG,"removeFromQueue: "+String.valueOf(index));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            queue.remove(index);
        }else{
            final List<JSONObject> objs = asList(queue);
            objs.remove(index);

            final JSONArray ja = new JSONArray();
            for (final JSONObject obj : objs) {
                ja.put(obj);
            }

            queue = ja;
        }
        writeFile();
    }

    public static List<JSONObject> asList(final JSONArray ja) {
        final int len = ja.length();
        final ArrayList<JSONObject> result = new ArrayList<JSONObject>(len);
        for (int i = 0; i < len; i++) {
            final JSONObject obj = ja.optJSONObject(i);
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }

    public synchronized boolean hasInQueue(String tg) {
        try {
            for (int a = 0; a < queue.length(); a++) {
                if (queue.getString(a).toLowerCase().compareTo(tg.toLowerCase()) == 0) {
                    return true;
                }
            }
        }catch(Exception ex){

        }
        return false;
    }

    public boolean started=false;

    public void startUploader()
    throws Exception {
        JSONObject jobj=readFile();
        if (jobj.has("tags")){
            queue=jobj.getJSONArray("tags");
        }
        started=true;
    }
}
