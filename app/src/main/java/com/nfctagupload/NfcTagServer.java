package com.nfctagupload;

import android.util.Log;

import com.usmani.android.HttpUtil;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rameez Usmani on 8/16/2017.
 */

public class NfcTagServer {

    private static final String TAG=NfcTagServer.class.getName();

    public static String sendTagToApi(String terminalId,String tagId,NfcTagUploadConfig config, String currentTime)
    throws Exception{
        String url=config.baseURI+"api/EnterEvent";
        Log.d(TAG,"Url: "+url);
        byte[] buff=null;
        //String jsonStr="{\"TerminalUid\":\""+terminalId+"\",\"wristbandId\":\""+tagId+"\",\"isEntrance\":"+String.valueOf(config.isEntrance)+"}";
        String jsonStr="{\"TerminalUid\":\""+terminalId+"\",\"wristbandId\":\""+tagId+"\",\"isEntrance\":"+String.valueOf(config.isEntrance)+",\"timeStamp\":\""+currentTime+"\"}";
        Log.d(TAG,"jsonStr: "+jsonStr);
        buff=jsonStr.getBytes();
        String body="";

        try{
            body=HttpUtil.postJsonDataAndGetResponse(url,buff);
        }catch(Exception ex){
            //body=ex.getMessage();
            body="";
        }

        return body;
    }

    public static void keepAlive(String terminalId,String appName,String appVersion,NfcTagUploadConfig config)
            throws Exception{
        String url=config.keepAliveURI;
        //String url="https://devkeepalive.smartagid.com/api/KeepAlive";
        Log.d(TAG,"Url: "+url);
        byte[] buff=null;
        String jsonStr="{\"TerminalUid\":\""+terminalId+"\",\"AppName\":\""+appName+"\",\"AppVersion\":\""+appVersion+"\"}";
        Log.d(TAG,"jsonStr: "+jsonStr);
        buff=jsonStr.getBytes();
        String body= HttpUtil.postJsonDataAndGetResponse(url,buff);
    }
}
