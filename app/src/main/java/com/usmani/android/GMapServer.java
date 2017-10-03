package com.usmani.android;

import org.json.JSONArray;
import org.json.JSONObject;

public class GMapServer {
	private static String SERVER_URL="http://maps.googleapis.com/maps/api/geocode/json?sensor=false";
	
	public static String getAddress(LocationEntity ldo)
	throws Exception {
		final String url=SERVER_URL+"&latlng="+String.valueOf(ldo.latitude)+","+String.valueOf(ldo.longitude);
		
		String body=HttpUtil.getHttpResponseBody(url);
		JSONObject jobj=new JSONObject(body);
		String addr="";
		String status=jobj.getString("status");
		if (status.compareTo("OK")!=0){
			throw new Exception("Some error in geocoding");
		}		
		JSONArray arrResult=jobj.getJSONArray("results");
		JSONObject obj=arrResult.getJSONObject(0);
		addr=obj.getString("formatted_address");
		
		return addr;
	}
	
	public static LocationEntity getLocation(String address)
	throws Exception {
		String url=SERVER_URL+"&address="+HttpUtil.encodeString(address);
		String body=HttpUtil.getHttpResponseBody(url);
		JSONObject jobj=new JSONObject(body);
		LocationEntity ldo=new LocationEntity();
		String status=jobj.getString("status");
		if (status.compareTo("OK")!=0){
			throw new Exception("Some error in geocoding");
		}
		
		JSONArray arrResult=jobj.getJSONArray("results");
		JSONObject obj=arrResult.getJSONObject(0);
		JSONObject objGeom=obj.getJSONObject("geometry");
		JSONObject objLoc=objGeom.getJSONObject("location");
		
		//ldo.latitude=objLoc.getString("lat");
		//ldo.longitude=objLoc.getString("lng");
		ldo.latitude=objLoc.getDouble("lat");
		ldo.longitude=objLoc.getDouble("lng");
		
		return ldo;
	}
}
