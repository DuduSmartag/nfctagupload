package com.usmani.android;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class AndroidHelper {
	
	private static final String TAG="com.usmani.android.AndroidHelper";
	
	public static void callNumber(String num,Context context){
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + num));
		context.startActivity(intent);
	}
	
	public static void openEmail(String num,String subject,String msg,Context context){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL,new String[]{num});
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, msg);
		Intent mailer = Intent.createChooser(intent, null);
		context.startActivity(mailer);
	}
	
	public static boolean isServiceRunning(String serviceName,Context context) {
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	    	Log.d(TAG,service.service.getClassName());
	        if (serviceName.equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}
