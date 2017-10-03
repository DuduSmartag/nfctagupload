package com.usmani.android;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class DeviceHelper {
	
	private static String getHumanReadableSize(long free){
		Log.d("DeviceHelper","free: "+String.valueOf(free));
		String suffix="b";
        double ffree=free;
        if (ffree>1024){
        	ffree=ffree/1024; //kb
        	suffix="kb";
        	if (ffree>1024){
        		ffree=ffree/1024; //mb
            	suffix="mb";
        		if (ffree>1024){
        			ffree=ffree/1024; //gb
                	suffix="gb";
        		}
        	}
        }
        Log.d("Device",String.valueOf(ffree));
        String strF=String.valueOf(ffree);
		String[] vals=strF.split("\\.");
		Log.d("DeviceHelper","Vals length: "+String.valueOf(vals.length));
		if (vals.length>1){
			String s=vals[1];
			Log.d("DeviceHelper","part: "+s);
			if(s.length()>2){
				s=s.substring(0,2);
				Log.d("DeviceHelper","Substr: "+s);
			}
			strF=vals[0]+"."+s;
			Log.d("DeviceHelper","New :"+strF);
		}
		return strF+suffix;
	}
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static long getFreeSize(StatFs statFs){
		long free=0;
		long blockSize=0L;
		long blockCount=0L;
		if (android.os.Build.VERSION.SDK_INT>=18){
			blockCount=statFs.getAvailableBlocksLong();
			blockSize=statFs.getBlockSizeLong();
		}else{
			blockCount=statFs.getAvailableBlocks();
			blockSize=statFs.getBlockSize();
		}
		free=blockSize*blockCount;
		return free;
	}
	
	@SuppressWarnings("deprecation")
	public static String getFreeMemory(){
	    StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long free=getFreeSize(statFs);
        return getHumanReadableSize(free);
	}
	
	@SuppressWarnings("deprecation")
	public static String getFreeSDMemory(){
		Log.d("DeviceHelper","SDCard: "+Environment.getExternalStorageState());
		Log.d("DeviceHelper","pics Pub: "+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
	    StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
	    long free=getFreeSize(statFs);
        return getHumanReadableSize(free);
	}
}
