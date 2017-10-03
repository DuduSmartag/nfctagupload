package com.usmani.android;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import com.usmani.android.ImageCache.ImageCacheParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


public class UIHelper {

	private static final String TAG=UIHelper.class.getName();
	
	public static ImageCache imageCache;
	private static final int IMAGE_CACHE_SIZE=500;
	
	/*public static void initializeBitmapCache(Context ctx,android.app.FragmentManager fragmentManager){
		ImageCacheParams icp=new ImageCacheParams(ctx,"mediacmscache");
		imageCache=ImageCache.getInstance(fragmentManager,icp);
	}*/

	public static Bitmap getImageFromCache(String url){
		if (imageCache==null){
			return null;
		}
		return imageCache.get(url);
	}

	public static void putImageInCache(String url,Bitmap bmp){
		if (imageCache==null){
			return;
		}
		imageCache.put(url,bmp);
	}
	
	public static void initializeBitmapCache(Context ctx,android.support.v4.app.FragmentManager fragmentManager){
		ImageCacheParams icp=new ImageCacheParams(ctx,"tripnotescache");
		imageCache=ImageCache.getInstance(fragmentManager,icp);
	}
	
	public static void removeFromBitmapCache(String key){
		if (imageCache!=null){
			imageCache.remove(key);
		}
	}
	
	public static Bitmap getRoundedBitmap(Bitmap bitmap) {
		int borderColor=-1;
		final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
	    final Canvas canvas = new Canvas(output);
	    
	    /*final int borderSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 2,
	            context.getResources().getDisplayMetrics());
	    final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 2,
	            context.getResources().getDisplayMetrics());*/
	    
	    final int borderSizePx=5,cornerSizePx=2;
	 
	    final int color = Color.WHITE;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	 
	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawOval(rectF, paint);
	 
	    paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	 
	    //bitmap.recycle();
	    
	    if (borderColor!=-1){
	    	//draw border
	    	paint.setColor(color);
	    	paint.setXfermode(null);
	    	//Paint.Style.FILL_AND_STROKE
	    	paint.setStyle(Paint.Style.STROKE);
	    	paint.setStrokeWidth((float) borderSizePx);
	    	//canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);
	    	canvas.drawOval(rectF,paint);
	    }
	    
	    return output;
	}
	
	public static void msbox(String str,String str2,Activity ctx){    	
        final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ctx);        
        dlgAlert.setTitle(str); 
        dlgAlert.setMessage(str2); 
        dlgAlert.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {            	
            }
        });
        dlgAlert.setCancelable(true);
        ctx.runOnUiThread(new Runnable(){
        	public void run(){
        		dlgAlert.create().show();
        	}
        });                
    }
	
	public static void makeLongToast(final String text,final Activity ctx){
		ctx.runOnUiThread(new Runnable(){
			public void run(){
				Toast.makeText(ctx, text,Toast.LENGTH_LONG).show();
			}
		});
	}
	
	public static int getPixelsFromDp(float dp,Context ctx){
		// Get the screen's density scale
		final float scale = ctx.getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		int px = (int) (dp * scale + 0.5f);
		return px;
	}
	
	public static void msbox(String str,String str2,final DialogInterface.OnClickListener ok,Activity ctx){    	
        final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ctx);        
        dlgAlert.setTitle(str); 
        dlgAlert.setMessage(str2); 
        dlgAlert.setPositiveButton("OK",ok);
        dlgAlert.setCancelable(true);
        ctx.runOnUiThread(new Runnable(){
        	public void run(){
        		dlgAlert.create().show();
        	}
        });                
    }
	
	public static void confirmBox(String title,String body,String yes,String no,final DialogInterface.OnClickListener yListener,final DialogInterface.OnClickListener nListener,Activity ctx){
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		            //Yes button clicked
		        	if (yListener!=null)
		        		yListener.onClick(dialog, which);
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		        	if (nListener!=null)
		        		nListener.onClick(dialog, which);
		            break;
		        }
		    }
		};

		final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title).setMessage(body).setPositiveButton(yes, dialogClickListener)
		    .setNegativeButton(no, dialogClickListener);
		ctx.runOnUiThread(new Runnable(){
        	public void run(){
        		builder.create().show();
        	}
        });
	}
	
	@SuppressLint("NewApi")
	private static void addInBitmapOptions(BitmapFactory.Options options,ImageCache cache) {
	    // inBitmap only works with mutable bitmaps, so force the decoder to
	    // return mutable bitmaps.
	    options.inMutable = true;
	    if (cache != null) {
	        // Try to find a bitmap to use for inBitmap.
	        Bitmap inBitmap = cache.getBitmapFromReusableSet(options);
	        if (inBitmap != null) {
	            // If a suitable bitmap has been found, set it as the value of
	            // inBitmap.
	            options.inBitmap = inBitmap;
	        }
	    }
	}

	public static Bitmap placeBitmapInMiddle(Bitmap bmp,Bitmap bmpMiddle){
        float x=(bmp.getWidth()/2);
        x-=(bmpMiddle.getWidth()/2);
        float y=(bmp.getHeight()/2);
        y-=(bmpMiddle.getHeight()/2);
        Log.d(TAG,String.valueOf(x)+","+String.valueOf(y));
        //bmp=bmp.copy(bmp.getConfig(),true);
        bmp=bmp.copy(Bitmap.Config.ARGB_8888,true);
        Log.d(TAG,"bmp converted to mutable");
        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(bmpMiddle, x,y,null);
        Log.d(TAG,"Drawn on canvas bmp");

        return bmp;
	}
	
	public static Bitmap getImageFromUrl(String strUrl,int width,int height,Bitmap bmpMiddle)
	throws Exception {
		Bitmap bmp=imageCache.get(strUrl);
		if (bmp==null){
			URL url = new URL(strUrl);
			Options options=decodeOptionsFromStream(url.openConnection().getInputStream(),width,height);
			Log.d("UIHelper","inSampleSize: "+String.valueOf(options.inSampleSize));
			Log.d("UIHelper","outWidth & outHeight: "+String.valueOf(options.outWidth)+","+String.valueOf(options.outHeight));			
			// If we're running on Honeycomb or newer, try to use inBitmap.
		    if (Utils.hasHoneycomb()) {
		        addInBitmapOptions(options,imageCache);
		    }			
			bmp=BitmapFactory.decodeStream(url.openConnection().getInputStream(),null,options);			
			if (bmpMiddle!=null){
				float x=(bmp.getWidth()/2);
				x-=(bmpMiddle.getWidth()/2);
				float y=(bmp.getHeight()/2);
				y-=(bmpMiddle.getHeight()/2);
				Log.d("BmpMiddle",String.valueOf(x)+","+String.valueOf(y));		
				//bmp=bmp.copy(bmp.getConfig(),true);
				bmp=bmp.copy(Bitmap.Config.ARGB_8888,true);
				Log.d("BmpMiddle","bmp converted to mutable");
				Canvas canvas = new Canvas(bmp);
				canvas.drawBitmap(bmpMiddle, x,y,null);
		        Log.d("BmpMiddle","Drawn on canvas bmp");
			}
//			if (imageCache.size()>=IMAGE_CACHE_SIZE){
//				Enumeration<String> str=imageCache.keys();
//				if (str.hasMoreElements()){
//					String u=str.nextElement();
//					imageCache.remove(u);
//					Log.d("UIHelper","Image cache removed: "+u);
//				}
//			}
			imageCache.put(strUrl,bmp);			
			Log.d("UIHelper","Image put in cache");			
		}else{
			Log.d("UIHelper","Image found in cache");
		}	
		System.gc();
		return bmp;
	}
	
	public static Bitmap getImageFromUrl2(String strUrl,int width,int height,Bitmap bmpMiddle)
	throws Exception {
		Bitmap bmp=imageCache.get(strUrl);
		if (bmp==null){
			URL url = new URL(strUrl);
			URLConnection conn=url.openConnection();
			InputStream iStream=conn.getInputStream();
			Options options=decodeOptionsFromStream(iStream,width,height);
			iStream.close();

			Log.d("UIHelper","inSampleSize: "+String.valueOf(options.inSampleSize));
			Log.d("UIHelper","outWidth & outHeight: "+String.valueOf(options.outWidth)+","+String.valueOf(options.outHeight));
			
			//original height / original width x new width = new height
			int origHeight=options.outHeight;
			//options.outHeight=(origHeight/options.outWidth) * width;
			Log.d("UIHelper","outWidth2 & outHeight2: "+String.valueOf(options.outWidth)+","+String.valueOf(options.outHeight));
			
			// If we're running on Honeycomb or newer, try to use inBitmap.
		    if (Utils.hasHoneycomb()) {
		        addInBitmapOptions(options,imageCache);
		    }			
		    conn=url.openConnection();
			iStream=conn.getInputStream();
			bmp=BitmapFactory.decodeStream(iStream,null,options);
			iStream.close();
			if (bmpMiddle!=null){
				float x=(bmp.getWidth()/2);
				x-=(bmpMiddle.getWidth()/2);
				float y=(bmp.getHeight()/2);
				y-=(bmpMiddle.getHeight()/2);
				Log.d("BmpMiddle",String.valueOf(x)+","+String.valueOf(y));		
				bmp=bmp.copy(bmp.getConfig(),true);
				Log.d("BmpMiddle","bmp converted to mutable");
				Canvas canvas = new Canvas(bmp);
				canvas.drawBitmap(bmpMiddle, x,y,null);
		        Log.d("BmpMiddle","Drawn on canvas bmp");
			}
//					if (imageCache.size()>=IMAGE_CACHE_SIZE){
//						Enumeration<String> str=imageCache.keys();
//						if (str.hasMoreElements()){
//							String u=str.nextElement();
//							imageCache.remove(u);
//							Log.d("UIHelper","Image cache removed: "+u);
//						}
//					}
			imageCache.put(strUrl,bmp);			
			Log.d("UIHelper","Image put in cache");			
		}else{
			Log.d("UIHelper","Image found in cache");
		}	
		System.gc();
		return bmp;
	}
	
	public static void displayImageFromUrl(String strUrl,final ImageView pmv,final Activity activity,int width,int height,final Bitmap bmpMiddle)
	throws Exception {
		Bitmap bmp=getImageFromUrl(strUrl, width, height, bmpMiddle);
//		if (bmp==null){			
//			URL url = new URL(strUrl);
//			Options options=decodeOptionsFromStream(url.openConnection().getInputStream(),width,height);
//			Log.d("UIHelper","inSampleSize: "+String.valueOf(options.inSampleSize));
//			bmp=BitmapFactory.decodeStream(url.openConnection().getInputStream(),null,options);
//			if (bmpMiddle!=null){
//				float x=(bmp.getWidth()/2);
//				x-=(bmpMiddle.getWidth()/2);
//				float y=(bmp.getHeight()/2);
//				y-=(bmpMiddle.getHeight()/2);
//				Log.d("BmpMiddle",String.valueOf(x)+","+String.valueOf(y));		
//				bmp=bmp.copy(bmp.getConfig(),true);
//				Log.d("BmpMiddle","bmp converted to mutable");
//				Canvas canvas = new Canvas(bmp);
//				canvas.drawBitmap(bmpMiddle, x,y,null);
//				
//		        //canvas.drawBitmap(bmpMiddle, new Matrix(), null);
//		        Log.d("BmpMiddle","Drawn on canvas bmp");
//			}
////			if (imageCache.size()>=IMAGE_CACHE_SIZE){
////				Enumeration<String> str=imageCache.keys();
////				if (str.hasMoreElements()){
////					String u=str.nextElement();
////					imageCache.remove(u);
////					Log.d("UIHelper","Image cache removed: "+u);
////				}
////			}
//			imageCache.put(strUrl,bmp);			
//			Log.d("UIHelper","Image put in cache");			
//		}else{
//			Log.d("UIHelper","Image found in cache");
//		}
		
		final Bitmap image=bmp;
		activity.runOnUiThread(new Runnable(){
			public void run(){
				try{
					pmv.setImageBitmap(image);
				}catch(Exception ex){					
				}
			}
		});	
		System.gc();
	}
	
	public static int getScreenWidth(Activity activity){
		Display display = activity.getWindowManager().getDefaultDisplay();
		return display.getWidth();
		/*Point size = new Point();		
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		return width;*/
	}
	
	public static int getScreenHeight(Activity activity){
		Display display = activity.getWindowManager().getDefaultDisplay();
		return display.getHeight();
		/*Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		return height;*/
	}

	public static float[] getDpMetrices(Context context){
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
		float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

		return new float[]{dpWidth,dpHeight,displayMetrics.density,displayMetrics.densityDpi};
	}
	
	public static Options decodeOptionsFromStream(InputStream is,int reqWidth, int reqHeight) {
		Log.d("UIHelper","Requested width: "+String.valueOf(reqWidth));
		Log.d("UIHelper","Request height: "+String.valueOf(reqHeight));
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeStream(is,null,options);
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return options;
	}
	
	public static Bitmap decodeSampledBitmapFromStream(InputStream is,int reqWidth, int reqHeight) {
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeStream(is,null,options);
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeStream(is,null,options);
	}
	
	public static Bitmap decodeSampledBitmapFromByteArray(byte[] is,int off,int len,int reqWidth, int reqHeight) {
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    //BitmapFactory.decodeStream(is,null,options);
	    BitmapFactory.decodeByteArray(is,off,len,options);

        if (reqWidth==-1 && reqHeight==-1){
            reqWidth=options.outWidth;
            reqHeight=options.outHeight;
        } else if (reqHeight==-1){
			int origWidth=options.outWidth;
			int origHeight=options.outHeight;
			reqHeight=((origHeight/origWidth)*reqWidth);
		}

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    //return BitmapFactory.decodeStream(is,null,options);
	    Log.d("UIHelper","Doing real decode: "+String.valueOf(len)+":"+String.valueOf(options.inSampleSize)+":"+String.valueOf(reqHeight)+":"+String.valueOf(options.outHeight)
	    		+":"+String.valueOf(reqWidth)+":"+String.valueOf(options.outWidth));
	    return BitmapFactory.decodeByteArray(is,off,len,options);
	}
	
	public static Bitmap decodeSampledBitmapFromDrawable(Resources is,int id,int reqWidth, int reqHeight) {
	    // First decode with inJustDecodeBounds=true to check dimensions
		Bitmap bmp=null;
		if (bmp==null){
		    BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    BitmapFactory.decodeResource(is,id,options);
		    // Calculate inSampleSize
		    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		    // Decode bitmap with inSampleSize set
		    options.inJustDecodeBounds = false;
		    bmp=BitmapFactory.decodeResource(is,id,options);
		}
	    return bmp;
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
            int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width=options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down
            // further.
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }
	
	public static int calculateInSampleSizeOld(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 2;	
	    if (height > reqHeight || width > reqWidth) {	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	    if (inSampleSize>4 && (reqHeight>60 || reqWidth>60)){
	    	inSampleSize=4;
	    }
	    //return inSampleSize;
	    return 4;
	}
	
//	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//		// Raw height and width of image
//		int height = options.outHeight;
//		int width = options.outWidth;
//		int inSampleSize = 1;		
//		
//		if (height > reqHeight || width > reqWidth) {
//			// Calculate ratios of height and width to requested height and width
//			int heightRatio = Math.round((float) height / (float) reqHeight);
//			int widthRatio = Math.round((float) width / (float) reqWidth);
//			// Choose the smallest ratio as inSampleSize value, this will guarantee
//			// a final image with both dimensions larger than or equal to the
//			// requested height and width.
//			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
//			/*if (inSampleSize<=1){
//				inSampleSize=2;
//			}*/
//		}		
//		return inSampleSize;
//	}
	
	public static void sendNotification(String msg,String title,int notId,int smallIcon,Intent i,Context ctx) {
		Log.d(TAG,"sendNotification: "+msg);
        NotificationManager mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, i,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
        .setSmallIcon(smallIcon)
        .setContentTitle(title)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(notId, mBuilder.build());
    }
	
	public static double coordinatesDistance(double lat1, double lon1, double lat2, double lon2, char unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		}else if (unit == 'N') {
			dist = dist * 0.8684;
		}
		return (dist);
	}
	
	public static double round(double unrounded, int precision, int roundingMode){
	    BigDecimal bd = new BigDecimal(unrounded);
	    BigDecimal rounded = bd.setScale(precision, roundingMode);
	    return rounded.doubleValue();
	}
		
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}
	
	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}
	
	@SuppressLint("NewApi")
	public static void setBackgroundDrawable(ImageView img,Drawable drBg){
		Log.d(TAG,"Api level: "+String.valueOf(Utils.apiLevel()));
		if (Utils.apiLevel()>=16){
			img.setBackground(drBg);
		}else{
			img.setBackgroundDrawable(drBg);
		}
	}
}
