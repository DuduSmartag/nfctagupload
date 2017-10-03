package com.usmani.android;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Hashtable;

import android.util.Log;

public class HttpUtil {
	
	private static String MULTIPART_BOUNDARY="----------ydxdqdjimrsdamseeldsrefqnkgvhgyu";
	private static String MULTIPART_HEADER="multipart/form-data; boundary="+MULTIPART_BOUNDARY;
	
	public static String HttpLog="";

    public static int timeout=5000;
	
	public static String encodeString(String unEncodedString){
		return URLUTF8Encoder.encode(unEncodedString);
	}
		
    public static HttpURLConnection getHttpConnection(String u,boolean doInput,boolean doOutput,boolean useCaches)
    throws Exception{
    	URL url=new URL(u);
    	HttpLog+="url created.\n";
    	try{
    		HttpURLConnection conn=(HttpURLConnection)url.openConnection();
    		HttpLog+="openConnection() called.\n";
    		if (conn==null){
    			throw new Exception("Conn in getHttpConnection is null");
    		}
    		HttpLog+="conn is not null.\n";

            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);

    		try{
    			conn.setDoInput(doInput);
    			conn.setDoOutput(doOutput);
    		}catch(Exception ex){}
    		
    		HttpLog+="setDoIO called.\n";


    		
    		return conn;
    	}catch(Exception ex){
    		throw new Exception("Error in url.openConnection(): "+ex.getMessage());
    	}        
    }
    
    public static String encodeUTF8Data(String name,String val){
    	return name+"="+URLUTF8Encoder.encode(val);
    }
    
    public static byte[] getHttpResponseBytes(String url)
    throws Exception{
    	HttpURLConnection sconn = getHttpConnection(url,true,false,false);
        InputStream is=sconn.getInputStream();
        byte[] buff=new byte[1024];
        ByteArrayOutputStream bor=new ByteArrayOutputStream();        
        int bread=0;
        while((bread=is.read(buff,0,buff.length))!=-1){        	
        	bor.write(buff,0,bread);        	
        }
        buff=null;
        is.close();
        sconn.disconnect();
        byte[] byteBuff=bor.toByteArray();
        bor.close();
        return byteBuff;
    }
    
    public static String getHttpResponseBody(String url)
    throws Exception{

    	HttpURLConnection sconn = getHttpConnection(url,true,false,false);
    	HttpLog+="getHttpConnection() called.\n";
    	
    	if (sconn==null){
    		throw new Exception("sconn is null");    		
    	}    	
    	
    	HttpLog+="sconn is not null: "+sconn.getRequestMethod()+".\n";  	
    	
    	InputStream is=null;
    	try{
    		is=sconn.getInputStream();
    	}catch(Exception ex){
    		//Log.e("HttpUtil","Getting is error");
    		//is=sconn.getErrorStream();
    		throw new Exception("Error in getInputStream(): "+ex.getMessage());
    	}
    	
    	HttpLog+="getInputStream() called.\n";
    	
    	int rCode=200;
    	
    	try{
    		rCode=sconn.getResponseCode();   
    		Log.d("HttpUtil","responsecode: "+String.valueOf(rCode));
    	}catch(Exception ex){
    		throw new Exception("Error in getResponseCode(): "+ex.getMessage());
    	}
    	
    	HttpLog+="getResponseCode() "+String.valueOf(rCode)+".\n";
    	
    	if (rCode!=200){
    		throw new Exception("Status code is "+String.valueOf(rCode));
    	}	
    	
        //InputStream is=sconn.getInputStream();
        //HttpLog+="getInputStream() called.\n";
        
        if (is==null){
        	rCode=sconn.getResponseCode();
        	//if (rCode!=200){
        	throw new Exception("InputStream is null and status code is "+String.valueOf(rCode));
        	//}
        }
        HttpLog+="InputStream is not null.\n";
        
        byte[] buff=new byte[1024];
        String body="";
        int bread=0;
        
        try{
        	while((bread=is.read(buff,0,1024))!=-1){
        		body+=new String(buff,0,bread);
        	}
        	HttpLog+="body created.\n";
        }catch(Exception ex){
        	throw new Exception("Error in is.read(): "+ex.getMessage());
        }
        
        try{
        	is.close();
        }catch(Exception ex){
        	throw new Exception("Error in is.close(): "+ex.getMessage());
        }
        
        try{
        	sconn.disconnect();
        }catch(Exception ex){}
        return body;
    }

    public static String postDataAndGetResponse(String url,Hashtable<String,String> formData)
    throws Exception {

        //int valCount=formData.size();
        String data="";                
        Enumeration<String> en=formData.keys();
        
        while(en.hasMoreElements()){
            String objKey=en.nextElement();
            String objVal=formData.get(objKey);
            data+=encodeUTF8Data(objKey,objVal);
            data+="&";
        }
        Log.d("HttpUtil",data);
        return postDataAndGetResponse(url,data);
    }

    public static String postDataAndGetResponse(String url,String data)
    throws Exception {
        return postDataAndGetResponse(url,data.getBytes());
    }
    
    public static String postDataAndGetResponse(String url,byte[] data)
    throws Exception {

    	HttpURLConnection sconn=getHttpConnection(url,true,true,false);
    	sconn.setRequestMethod("POST");
    	sconn.setRequestProperty("Cache-Control","no-cache");
    	sconn.setRequestProperty("Connection","keep-alive");
    	sconn.setRequestProperty("Pragma","no-cache");   	
    	sconn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
    	sconn.setRequestProperty("Accept","text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
    	sconn.setRequestProperty("User-Agent","Android");   	
    	
        OutputStream os=sconn.getOutputStream();
        os.write(data);
        InputStream is=sconn.getInputStream();
        byte[] buff=new byte[1024];
        String body="";
        int bread=0;
        while((bread=is.read(buff,0,1024))!=-1){
            body+=new String(buff,0,bread);
        }
        os.close();
        is.close();
        return body;
    }
    
    public static String postMultipartDataAndGetResponse(String url,Hashtable<String,String> formData,HttpFile hf)
    throws Exception {    	
    	HttpMultipartRequest hmr=new HttpMultipartRequest(url,formData,hf.fileField,hf.fileName,hf.fileType,hf.data);
    	return postMultipartDataAndGetResponse(url,hmr.getBytesToPost());
    }   	    
    	    
    public static String postMultipartDataAndGetResponse(String url,byte[] data)
    throws Exception {
    	HttpURLConnection sconn=getHttpConnection(url,true,true,false);
    	sconn.setRequestMethod("POST");
    	sconn.setRequestProperty("Cache-Control","no-cache");
    	sconn.setRequestProperty("Connection","keep-alive");
    	sconn.setRequestProperty("Pragma","no-cache");   	
    	sconn.setRequestProperty("Content-Type",MULTIPART_HEADER);
    	sconn.setRequestProperty("Accept","text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
    	sconn.setRequestProperty("User-Agent","Android");
    	sconn.setRequestProperty("Content-Type",MULTIPART_HEADER);
    	
        OutputStream os=sconn.getOutputStream();
        os.write(data);
        InputStream is=sconn.getInputStream();
        byte[] buff=new byte[1024];
        String body="";
        int bread=0;
        while((bread=is.read(buff,0,1024))!=-1){
            body+=new String(buff,0,bread,"UTF-8");
        }
        os.close();
        is.close();
        return body;
    }

    public static String postJsonDataAndGetResponse(String url,byte[] data)
            throws Exception {

        HttpURLConnection sconn=getHttpConnection(url,true,true,false);
        sconn.setRequestMethod("POST");
        sconn.setRequestProperty("Cache-Control","no-cache");
        sconn.setRequestProperty("Connection","keep-alive");
        sconn.setRequestProperty("Pragma","no-cache");
        sconn.setRequestProperty("Content-Type","application/json");
        sconn.setRequestProperty("Accept","text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
        sconn.setRequestProperty("User-Agent","Android");

        OutputStream os=sconn.getOutputStream();
        os.write(data);
        InputStream is=null;
        try{
            is=sconn.getInputStream();
        }catch(Exception ex){
            try {
                is = sconn.getErrorStream();
            }catch(Exception ex2){
                throw new Exception("Error in stream");
            }
        }
        byte[] buff=new byte[1024];
        String body="";
        int bread=0;
        while((bread=is.read(buff,0,1024))!=-1){
            body+=new String(buff,0,bread);
        }
        os.close();
        is.close();
        return body;
    }

}
