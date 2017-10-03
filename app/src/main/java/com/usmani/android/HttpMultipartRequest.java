package com.usmani.android;

import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Hashtable; 
 
public class HttpMultipartRequest
{
	//private static final String BOUNDARY = "----------V2ymHFg03ehbqgZCaKO6jy";
	private static final String BOUNDARY="----------ydxdqdjimrsdamseeldsrefqnkgvhgyu";
 
	private byte[] postBytes = null;
	//private String url = null;
 
	public HttpMultipartRequest(String url, Hashtable<String,String> params, String fileField, String fileName, String fileType, byte[] fileBytes) throws Exception{
		//this.url = url; 
		String boundary = getBoundaryString(); 
		String boundaryMessage = getBoundaryMessage(boundary, params, fileField, fileName, fileType); 
		String endBoundary = "\r\n--" + boundary + "--\r\n"; 
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		bos.write(boundaryMessage.getBytes()); 
		bos.write(fileBytes); 
		bos.write(endBoundary.getBytes()); 
		this.postBytes = bos.toByteArray(); 
		bos.close();
	}
	
	public byte[] getBytesToPost(){
		return postBytes;
	}
 
	public String getBoundaryString(){
		return BOUNDARY;
	}
 
	public String getBoundaryMessage(String boundary, Hashtable<String,String> params, String fileField, String fileName, String fileType)	{
		StringBuffer res = new StringBuffer("--").append(boundary).append("\r\n"); 
		Enumeration<String> keys = params.keys(); 
		while(keys.hasMoreElements())
		{
			String key = (String)keys.nextElement();
			String value = (String)params.get(key); 
			res.append("Content-Disposition: form-data; name=\"").append(key).append("\"\r\n")    
				.append("\r\n").append(value).append("\r\n")
				//.append("\r\n").append(HttpUtil.encodeString(value)).append("\r\n")
				.append("--").append(boundary).append("\r\n");
		}
		res.append("Content-Disposition: form-data; name=\"").append(fileField).append("\"; filename=\"").append(fileName).append("\"\r\n") 
			.append("Content-Type: ").append(fileType).append("\r\n\r\n");
 
		return res.toString();
	}
}
