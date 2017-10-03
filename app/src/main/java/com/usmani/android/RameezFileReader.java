package com.usmani.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class RameezFileReader {
	
	private static int BUFFER_SIZE=2048;
	
	public static String readStringData(InputStream inputStream)
	throws Exception{
		byte[] buffer = new byte[BUFFER_SIZE];
		StringBuilder result=new StringBuilder();
		while (true) {
			int bytesRead = inputStream.read( buffer, 0, BUFFER_SIZE );
			if (bytesRead == -1)
				break;
			result.append(new String(buffer,0,bytesRead));
		}
		return result.toString();
	}
	
	public static byte[] readData(InputStream inputStream)
	throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[BUFFER_SIZE];
		while (true) {
			int bytesRead = inputStream.read( buffer, 0, BUFFER_SIZE );
			if (bytesRead == -1)
				break;
			byteArrayOutputStream.write( buffer, 0, bytesRead );
		}
		byteArrayOutputStream.flush();
		buffer=null;
		byte[] result = byteArrayOutputStream.toByteArray();
		byteArrayOutputStream.close();
		inputStream.close();
		return result;
	}
	
	public static byte[] readFile(File file)
	throws Exception {
		InputStream inputStream = null;
		inputStream = new FileInputStream(file);
		return readData(inputStream);
	}			
	
	public static byte[] readFile(String filePath)
	throws Exception {
		InputStream inputStream = null;
		inputStream = new FileInputStream(filePath);
		return readData(inputStream);
	}
	
	public static byte[] readFileFromContent(Uri filePath,Context act)
	throws Exception{
		/*try{
            Log.d("RameezFileReader","Quering for "+filePath.toString());
            Cursor cr=act.getContentResolver().query(filePath,null,null,null,null);
            Log.d("RameezFileReader","Cursor for "+filePath.toString());
            int cCount=cr.getColumnCount();
            Log.d("RameezFileReader","Column count: "+String.valueOf(cCount));
            for (int a=0;a<cCount;a++){
                Log.d("RameezFileReader","Column("+String.valueOf(a)+"): "+cr.getColumnName(a));
            }
            cr.close();
        }catch(Exception ex){
            Log.e("RameezFileReader","Exception: "+ex.getMessage());
        }*/

		InputStream inputStream=null;
		inputStream=act.getContentResolver().openInputStream(filePath);
		return readData(inputStream);
	}
	
	public static byte[] readFile(Uri filePath,Context ctx)
	throws Exception {		
		if (filePath.getScheme().contains("content")){
			return readFileFromContent(filePath,ctx);
		}	
		//assume it is file scheme
		return readFile(new File(filePath.getPath()));		
	}
}
