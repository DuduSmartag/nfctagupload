package com.usmani.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class RameezFileWriter {
	
	private static int BUFFER_SIZE=2048;
	
	public static void writeStringData(OutputStream outputStream,String str)
	throws Exception{
//		byte[] buffer = new byte[BUFFER_SIZE];
//		StringBuilder result=new StringBuilder();
//		while (true) {
//			int bytesRead = inputStream.read( buffer, 0, BUFFER_SIZE );
//			if (bytesRead == -1)
//				break;
//			result.append(new String(buffer,0,bytesRead));
//		}
//		return result.toString();
	}
	
	public static void writeData(OutputStream outputStream,byte[] buffer)
	throws Exception {
		outputStream.write(buffer);
//		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//		byte[] buffer = new byte[BUFFER_SIZE];
//		while (true) {
//			int bytesRead = inputStream.read( buffer, 0, BUFFER_SIZE );
//			if (bytesRead == -1)
//				break;
//			byteArrayOutputStream.write( buffer, 0, bytesRead );
//		}
//		byteArrayOutputStream.flush();
//		byte[] result = byteArrayOutputStream.toByteArray();
//		byteArrayOutputStream.close();
//		inputStream.close();
//		return result;
	}
	
	public static void writeFile(File file,byte[] buffer)
	throws Exception {
		FileOutputStream outputStream = null;
		outputStream = new FileOutputStream(file);
		writeData(outputStream,buffer);
		outputStream.close();
	}			
	
	public static void writeFile(String filePath,byte[] buffer)
	throws Exception {
		Log.d("RameezFileWriter","writeFile: "+filePath);
		File fl=new File(filePath);
		if (fl.exists()){
			Log.d("RameezFileWriter","Exists");
		}else{
			if (fl.createNewFile()){
				Log.d("RameezFileWriter","File created");
			}else{
				Log.d("RameezFileWriter","File not created");
			}
		}
		FileOutputStream outputStream = null;
		//outputStream = new FileOutputStream(filePath);
		outputStream = new FileOutputStream(fl);
		writeData(outputStream,buffer);
		outputStream.close();
	}
	
	public static void writeFileToContent(Uri filePath,Context act,byte[] buffer)
	throws Exception{
		OutputStream outputStream=null;
		outputStream=act.getContentResolver().openOutputStream(filePath);
		writeData(outputStream,buffer);
	}
	
	public static void writeFile(Uri filePath,Context ctx,byte[] buffer)
	throws Exception {		
		if (filePath.getScheme().contains("content")){
			writeFileToContent(filePath,ctx,buffer);
		}	
		//assume it is file scheme
		writeFile(new File(filePath.getPath()),buffer);		
	}
}
