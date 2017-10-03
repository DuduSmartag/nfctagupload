package com.nfctagupload;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Rameez Usmani on 2/5/2017.
 */

public class PermissionsHelper {
    public static final String[] permissions=new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
    };

    public static final int PERMISSIONS_REQUEST_CODE=1245;

    public static boolean checkAndRequestPermissions(Activity thisActivity){
        return checkAndRequestPermissions(thisActivity,PERMISSIONS_REQUEST_CODE);
    }

    public static boolean checkAndRequestPermissions(Activity thisActivity,int requestCode){
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return false;
        }
        if (!hasAllPermissions(thisActivity)) {
            ActivityCompat.requestPermissions(thisActivity,permissions,requestCode);
            return true;
        }
        return false;
    }

    public static boolean hasAllPermissions(Context ctx){
        for (int a=0;a<permissions.length;a++){
            if (!hasPermission(permissions[a],ctx)){
                return false;
            }
        }
        return true;
    }

    public static boolean hasPermission(String permission,Context ctx){
        int permissionCheck = ContextCompat.checkSelfPermission(ctx,permission);
        if (permissionCheck==PackageManager.PERMISSION_GRANTED){
            return true;
        }else if (permissionCheck==PackageManager.PERMISSION_DENIED){
            return false;
        }
        return false;
    }
}
