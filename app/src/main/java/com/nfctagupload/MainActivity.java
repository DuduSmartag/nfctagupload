package com.nfctagupload;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.modules.SettingModule;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.usmani.android.HttpUtil;
import com.usmani.android.UIHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends CommonActivity {

    private static final String TAG=MainActivity.class.getName();



    private NfcAdapter mNfcAdapter;
    private String terminalUid="";

    private boolean processingTag=false;
    private OfflineTagUploader offlineUploader=new OfflineTagUploader();

    private ImageView imageView;
    private SettingModule mSettingModule;

    private Target target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        getSettingFromServer();

        if (!PermissionsHelper.checkAndRequestPermissions(this)){
            canDoAfterPermissions();
        }


    }

    private void findViews(){
        imageView = (ImageView)findViewById(R.id.img_main_logo);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case PermissionsHelper.PERMISSIONS_REQUEST_CODE:
                if (!PermissionsHelper.checkAndRequestPermissions(this)){
                    canDoAfterPermissions();
                }
                return;
        }
    }

    @Override
    public void onBackPressed(){
        Log.d(TAG,"backPressed");
    }

    private void canDoAfterPermissions(){
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            UIHelper.makeLongToast("NFC not supported",this);
        }else{
            if (!mNfcAdapter.isEnabled()) {
                UIHelper.makeLongToast("NFC is disabled.",this);
            }
        }
        try {
//            WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            String address;
            address = getMacAddress();
            if (address!=null){
                terminalUid=address;
                Log.d(TAG,"MAC address: "+address);
            }
        } catch(Exception ex){
            Log.e(TAG,"Error in getting wifi: "+ex.getMessage());
        }
        loadAndWriteConfig();
        handleIntent(getIntent());
        doKeepAlive();
        try {
            offlineUploader.startUploader();
            //startOfflineThread();
        }catch(Exception ex){
            Log.e(TAG,"Exception in startUploader: "+ex.getMessage());
            UIHelper.makeLongToast("Exception in startUploader: "+ex.getMessage(),this);
        }

        //sendTagToServer("11223344");
    }

    private void startOfflineThread(){
        Thread thr=new Thread(){
            public void run(){
                while(offlineUploader.started) {
                    for (int a=0;a<offlineUploader.queue.length();a++) {
                        try {
                            String nextTag=offlineUploader.queue.getString(a);
                            Log.d(TAG,"next tag: "+nextTag);
                            String bd=NfcTagServer.sendTagToApi(terminalUid,nextTag,AppCache.config);
                            Log.d(TAG,"Next tag uploaded: "+bd);
                            if (bd.compareTo("")!=0){
                                offlineUploader.removeFromQueue(a);
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "Exception in offline thread: " + ex.getMessage());
                        }
                    }

                    try{
                        Thread.sleep(10000);
                    }catch(Exception ex){}
                }
            }
        };
        thr.start();
    }

    private void loadAndWriteConfig(){
        try{
            AppCache.config=NfcTagUploadConfig.readConfig(getApplicationContext());
        }catch(Exception ex){
            Log.e(TAG,"Exception in reading config: "+ex.getMessage());
            AppCache.config=new NfcTagUploadConfig();
        }

        try {
            AppCache.config.writeToFile(getApplicationContext());
        }catch(Exception ex){
            Log.e(TAG,"Exception: "+ex.getMessage());
        }

        HttpUtil.timeout=AppCache.config.internetTimeout*1000;
    }

    @Override
    protected void onResume() {
        super.onResume();
        keepAlive=true;
        processingTag=false;
        if (mNfcAdapter!=null) {
            try {
                /**
                 * It's important, that the activity is in the foreground (resumed). Otherwise
                 * an IllegalStateException is thrown.
                 */
                setupForegroundDispatch(this, mNfcAdapter);
            } catch (Exception ex) {
                UIHelper.makeLongToast("Error in setupForegroundDispatch: " + ex.getMessage(), this);
            }
        }
    }

    @Override
    public void onDestroy(){
        keepAlive=false;
        processingTag=false;
        if (mNfcAdapter!=null) {
            /**
             * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
             */
            stopForegroundDispatch(this, mNfcAdapter);
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding {@link BaseActivity} requesting to stop the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        try {
            adapter.disableForegroundDispatch(activity);
        }catch(Exception ex){
            Log.e(TAG,"error in disableForegroundDispatch: "+ex.getMessage());
        }
    }

    private byte[] tgId;

    private Intent intentStatus;

    private void sendTagToServer(final String str){
        Thread thr=new Thread(){
            public void run(){
                showProgressBar();
                try{
                    //String bd=NfcTagServer.sendTagToApi("A434D9474A9B",str,AppCache.config);
                    ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork  = connManager.getActiveNetworkInfo();
                    String bd;
                    if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI && activeNetwork.isConnected()) {
                        if ((terminalUid == null) || (terminalUid.equalsIgnoreCase(""))){
                            terminalUid = getMacAddress();
                        }
                        checkIfNeedToSendTempFile(offlineUploader.returnTempTags());
                        bd=NfcTagServer.sendTagToApi(terminalUid,str,AppCache.config);
                    }else{
                        try {
                            offlineUploader.writeTempFile(terminalUid,str);
                            bd = "successfully";
                        }catch (Exception e){
                            bd = "Offline and could not write to local file";
                        }
                    }

                    Log.d(TAG,"Response: "+bd);
                    intentStatus=new Intent(MainActivity.this,StatusActivity.class);
                    //UIHelper.makeLongToast("Tag id is: "+str,MainActivity.this);
                    if (bd.contains("successfully") || (bd.compareTo("")==0 && offlineUploader.hasInQueue(str))){
                        intentStatus.putExtra("body",bd);
                        intentStatus.putExtra("status",true);
                    }
                    /*else if (bd.compareTo("")==0){
                        Log.d(TAG,"No internet: "+str);
                        //offlineUploader.addToQueue(str);
                        try{

                        }catch(Exception ex){

                        }
                    }*/
                    else{
                        if (bd.compareTo("")==0){
                            bd="Could not find terminal data";
                        }else {
                            try {
                                JSONObject obj = new JSONObject(bd);
                                if (obj.has("Message")) {
                                    bd = obj.getString("Message");
                                }
                            } catch (Exception ex) {
                            }
                        }
                        intentStatus.putExtra("body",bd);
                        intentStatus.putExtra("status",false);
                    }
                    closeProgressBar();
                    runOnUiThread(new Runnable(){
                        public void run(){
                            startActivity(intentStatus);
                        }
                    });
                }catch(Exception ex){
                    processingTag=false;
                    closeProgressBar();
                    UIHelper.makeLongToast("Error in sending to server",MainActivity.this);
                }
            }
        };
        thr.start();
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        //UIHelper.makeLongToast("on handleIntent",this);
        try {
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                if (processingTag){
                    UIHelper.makeLongToast("Already processing a tag. Please attach tag after few seconds...",this);
                    return;
                }
                processingTag=true;
                //UIHelper.makeLongToast("TAG discovered", this);
                String type = intent.getType();
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if (tag.getId().length == 0) {
                    //UIHelper.makeLongToast("Unable to get id of tag", this);
                } else {
                    tgId = tag.getId();
                    //UIHelper.makeLongToast("Id of tag is " + String.valueOf(tgId.length) + " bytes long", this);
                    String str="";
                    for (int a=0;a<tgId.length;a++){
                        byte b=tgId[a];
                        str+=String.format("%02x",b);
                    }
                    //UIHelper.makeLongToast("String id is " + str + " ...sending to server", MainActivity.this);
                    sendTagToServer(str);
                }
            }
        }catch(Exception ex){
            processingTag=false;
            Log.e(TAG,"Exception in handleIntent: "+ex.getMessage());
            UIHelper.makeLongToast("Error: "+ex.getMessage(),this);
        }
    }

    private boolean keepAlive=true;

    private void doKeepAlive(){
        Thread thr=new Thread(){
            public void run(){
                while(keepAlive){
                    try{
                        Thread.sleep(AppCache.config.keepAliveInterval*1000);
                    }catch(Exception ex){

                    }

                    try{
                        //UIHelper.makeLongToast("Sending keep alive",MainActivity.this);
                        //NfcTagServer.keepAlive("A434D9474A9B","NfcTagUpload","1.0",AppCache.config);
                        NfcTagServer.keepAlive(terminalUid,"NfcTagUpload","1.0",AppCache.config);
                    }catch(Exception ex){
                        Log.e(TAG,"doKeepAlive Exception: "+ex.getMessage());
                    }
                }
            }
        };
        thr.start();
    }

    private void getSettingFromServer(){
        String url = "http://echo.jsontest.com/key/value/one/two";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,"Volley Response: " + response.toString());

                        //test
                        mSettingModule = new SettingModule("http://i.imgur.com/DvpvklR.png", false);

                        getImageByUrl(mSettingModule.getLogoPath());
                        if(mSettingModule.getNeedToUpdateNFCFile()){
                            updateLocalNFCFile();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        getImageByUrl("");
                        Log.d(TAG, "Volley Error: " + error.getMessage());
                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    private void updateLocalNFCFile(){
        String url = "http://echo.jsontest.com/key/value/one/two";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,"Volley Response: " + response.toString());
                        JSONArray queueFromServer = new JSONArray();
                        try {
                            offlineUploader.writeFileFromServer(queueFromServer);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d(TAG, "Volley Error: " + error.getMessage());
                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    private void getImageByUrl(String urlStr){
        if (urlStr.equalsIgnoreCase("")) {
            Picasso.with(this).load(R.drawable.confirm).into(imageView);

            File logoFile = new File(AppCache.getAppDirectoryPath()+"logo_image.png");
            if (logoFile.exists()) {
                Picasso.with(this).load(logoFile).into(imageView);
            }
        }else {
//            target = new Target() {
//                @Override
//                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//                    Thread thread = new Thread(){
//                        @Override
//                        public void run() {
//                            File file = new File(AppCache.getAppDirectoryPath() + "logo_image.png");
//                            if (file.exists()){
//                                file.delete();
//                            }
//                            try {
//                                file.createNewFile();
//                                FileOutputStream ostream = new FileOutputStream(file);
//                                bitmap.compress(Bitmap.CompressFormat.PNG, 80, ostream);
//                                ostream.flush();
//                                ostream.close();
//                            } catch (IOException e) {
//                                Log.e("IOException", e.getLocalizedMessage());
//                            }
//                        }
//                    };
//                    thread.start();
//                }
//
//                @Override
//                public void onBitmapFailed(Drawable errorDrawable) {
//
//                }
//
//                @Override
//                public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                }
//            };
//            Picasso.with(this).load(urlStr).into(target);

            Picasso.with(this).load(urlStr).into(imageView);
        }
    }

    private void checkIfNeedToSendTempFile(final JSONArray jsonArray){
        if ((jsonArray == null) || (jsonArray.length()==0))
            return;

        String url = "";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        offlineUploader.clearTempTags();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Error: " + error.getMessage());
            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                String stringJson = jsonArray.toString();
                Log.d(TAG, "checkIfNeedToSendTempFile json body: " + stringJson);
                return stringJson.getBytes();
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private String getMacAddress(){
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //if (address == "02:00:00:00:00:00") {
            address = getWifiMacAddress();
            //}
        }
        return address;
    }

    public static String getWifiMacAddress() {
        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)){
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac==null){
                    return "";
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length()>0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
}
