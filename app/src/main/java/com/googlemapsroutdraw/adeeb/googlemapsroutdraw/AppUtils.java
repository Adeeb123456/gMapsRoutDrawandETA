package com.googlemapsroutdraw.adeeb.googlemapsroutdraw;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.googlemapsroutdraw.adeeb.AppConstants;
import com.googlemapsroutdraw.adeeb.googlemapsroutdraw.Callbacks.CurrentLocationCallback;
import com.googlemapsroutdraw.adeeb.googlemapsroutdraw.model.CustomLatLong;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Fizza Azam on 4/29/2016.
 */
public class AppUtils {

    public static Dialog animDialog;
    public static CountDownTimer waitingTimer;
    public static int waitCount = 0;


    public static String IMAGE_ARRAY[] =  {"jpg", "jpeg", "png"};

    /**
     * Hide Keyboard
     *
     * @param activity
     * @param pEditText
     */
    public static void hideSoftKeyboard(Activity activity, EditText pEditText) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(pEditText.getWindowToken(), 0);
    }

    public static void hideSoftKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e){

        }

    }

    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            // Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public static CustomLatLong points;
    public static LocationListener listener;

    public static void getCurrentLocation(final Context pContext, final CurrentLocationCallback pCurrentLocationCallback) {
//        final MyProgressDialog mProgressDialog = new MyProgressDialog(pContext, "Getting Current Location...");
//        mProgressDialog.show();
        final LocationManager locationManager = (LocationManager) pContext.getSystemService(Context.LOCATION_SERVICE);
        String provider = LocationManager.NETWORK_PROVIDER;

        boolean enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!enabled) {
//            mProgressDialog.dismiss();
            Toast.makeText(pContext, "Location Services Not Active, Please enable it", Toast.LENGTH_SHORT).show();

            return;
        }

        listener = new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
//                mProgressDialog.dismiss();
                pCurrentLocationCallback.onFailure("Location not found");
            }

            @Override
            public void onLocationChanged(Location location) {
//                mProgressDialog.dismiss();
                /*&& ActivityCompat.checkSelfPermission(pContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/
                if (ActivityCompat.checkSelfPermission(pContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.removeUpdates(listener);
                points = new CustomLatLong();
                points.setLatitude(location.getLatitude());
                points.setLongitude(location.getLongitude());
                pCurrentLocationCallback.onSuccess(points);
                points=null;
            }
        };

        locationManager.requestLocationUpdates(provider, 0, 0,listener );
    }
    public static void addressToLocationgeoCoding(Context pContext , String address, CurrentLocationCallback pCallback){

        Geocoder geocoder = new Geocoder(pContext, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(address, 1);
        } catch (IOException e) {
            String errorMessage = "Service not available";
            Log.e("TAG", errorMessage, e);
        }


        CustomLatLong points = new CustomLatLong();

        if(addresses == null ){
            pCallback.onFailure("No location found with this address!");
        }
        else
        {
            if(addresses.size()!=0){
                points.setLatitude(addresses.get(0).getLatitude());
                points.setLongitude(addresses.get(0).getLongitude());

                pCallback.onSuccess(points);
            }else{
                pCallback.onFailure("No location found with this address!");
            }

        }
    }

    public static String getNewPath(final Activity context, Uri uri) {
        if( uri == null ) {
            return null;
        }

        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor;
        if(Build.VERSION.SDK_INT > 19)
        {
            String wholeID = DocumentsContract.getDocumentId(uri);
            String id = wholeID.split(":")[1];
            String sel = MediaStore.Images.Media._ID + "=?";

            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, sel, new String[]{ id }, null);
        }
        else
        {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
        }
        String path = null;
        try
        {
            if (cursor!=null) {
                int column_index = cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index).toString();
                cursor.close();
            }
            else{
                uri.getPath();
            }
        }
        catch(NullPointerException e) {

        }
        return path;
    }

    public static boolean isImageType(String filePath) {

        String fileType = filePath.substring(filePath.lastIndexOf(".")+1);
        for (int i = 0; i < IMAGE_ARRAY.length; i++) {
            if(IMAGE_ARRAY[i].equalsIgnoreCase(fileType)){
                return true;
            }
        }
        return false;
    }

    public static Bitmap decodeFileWithOriginalSize(String path) {
        try {
            File f = new File(path);

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = 1;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    public static long getCurrentUnixTime(){

        Date d1 = new Date();
        Date d2 = new Date();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sm = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        String strDate = sm.format(c.getTime());
        try {
            d1 = sm.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SimpleDateFormat sm1 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        sm1.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            d2 = sm1.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long differenceGMT = d2.getTime() - d1.getTime();
        System.out.println("Difference Time: " + differenceGMT);

        long unixTime = System.currentTimeMillis();
        System.out.println("Unix Time: " + unixTime);

        unixTime = unixTime + differenceGMT;

        long  resultantTime = unixTime/1000L;
        System.out.println("Final Time: " + resultantTime);

        return resultantTime;
    }

    /**
     *
     * @param pContext
     * @param pDialogTitle
     * @param pMsg
     * @param pButtonName
     * @param pPositiveBtnClickListener
     */
    public static void showDialog(Context pContext, String pDialogTitle, String pMsg, String pButtonName,
                                  DialogInterface.OnClickListener pPositiveBtnClickListener) {
        try {
                AlertDialog.Builder dialog = new AlertDialog.Builder(pContext)
                        .setTitle(pDialogTitle).setMessage(pMsg)
                        .setPositiveButton(pButtonName, pPositiveBtnClickListener);
                dialog.setCancelable(false);
                dialog.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param pContext
     * @param pDialogTitle
     * @param pMsg
     * @param pPositiveButtonName
     * @param pPositiveBtnClickListener
     * @param pNegativeButtonName
     * @param pNegativeBtnClickListener
     */
    public static void showDialog(Context pContext, String pDialogTitle, String pMsg, String pPositiveButtonName,
                                  DialogInterface.OnClickListener pPositiveBtnClickListener, String pNegativeButtonName, DialogInterface.OnClickListener pNegativeBtnClickListener) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(pContext)
                    .setTitle(pDialogTitle).setMessage(pMsg)
                    .setPositiveButton(pPositiveButtonName, pPositiveBtnClickListener)
                    .setNegativeButton(pNegativeButtonName, pNegativeBtnClickListener);
            dialog.setCancelable(false);
            dialog.show();
        } catch (WindowManager.BadTokenException e) {
        }
    }

    /**
     *
     * @param path
     * @param imageViewHW
     * @return
     */
    public static Bitmap decodeFile(String path, ImageView imageViewHW) {
        try {
            File f = new File(path);
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            if (imageViewHW != null) {
                scale = AppUtils.calculateInSampleSize(o, imageViewHW.getBackground().getIntrinsicWidth(), imageViewHW.getBackground().getIntrinsicHeight());

            } else {
                final int REQUIRED_SIZE = 200;
                scale = AppUtils.calculateInSampleSize(o, REQUIRED_SIZE, REQUIRED_SIZE);
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (OutOfMemoryError e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Activity context, final Uri uri) {

        String url = uri.toString();

        if (url.startsWith("content://com.google.android.apps.photos.content")){
            String filePath = "";

            File myDir = new File(AppConstants.EXTERNAL_FILE_PHOTO_DIRECTORY);
            if (myDir.exists()) {
                myDir.delete();
            }
            myDir.mkdirs();

            File f = new File(AppConstants.EXTERNAL_FILE_PHOTO_DIRECTORY, AppConstants.EXTERNAL_FILE_PATH);

            if (f.exists())
                f.delete();

            Bitmap bitmap = null;
            InputStream is = null;
            //		if (url.startsWith("content://com.google.android.apps.photos.content")){
            try {
                is = context.getContentResolver().openInputStream(Uri.parse(url));

                OutputStream os = new FileOutputStream(f);
                bitmap = BitmapFactory.decodeStream(is);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                is.close();
                os.close();
                filePath = f.getAbsolutePath();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch(Exception e ){

            }
            return filePath;

        }
        else{
            return AppUtils.getPath(uri, context);
        }
    }

    /**
     * Get Uri and convert filePath to String
     *
     * @param uri
     * @param activity
     * @return
     */
    public static String getPath(Uri uri, Activity activity) {
        try {
            String[] projection = { MediaStore.MediaColumns.DATA };
            @SuppressWarnings("deprecation")
            Cursor cursor = activity.managedQuery(uri, projection, null, null, null);

            if (cursor!=null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
            else{
                return uri.getPath();
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param pContext
     * @param message
     */
    public static void showStatusDialog(Context pContext, String message){
        AppUtils.showDialog(pContext, pContext.getResources().getString(R.string.app_name), message, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    public static void playNotificationSound(final Context context){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
            final MediaPlayer mp = MediaPlayer.create(context, notification);
            mp.start();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mp != null && mp.isPlaying()){
                        mp.stop();
                        mp.release();

                    }
                }
            },3000);

        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void getCurrentNavigationLocation(final Context pContext, final CurrentLocationCallback pCurrentLocationCallback) {
      //  final MyProgressDialog mProgressDialog = new MyProgressDialog(pContext, "Getting Current Location...");
      //  mProgressDialog.show();
        final LocationManager locationManager = (LocationManager) pContext.getSystemService(Context.LOCATION_SERVICE);
        String provider = LocationManager.NETWORK_PROVIDER;

        boolean enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!enabled) {
         //   mProgressDialog.dismiss();
            Toast.makeText(pContext, "Location Services Not Active, Please enable it", Toast.LENGTH_SHORT).show();

            return;
        }

        listener = new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
               // mProgressDialog.dismiss();
                pCurrentLocationCallback.onFailure("Location not found");
            }

            @Override
            public void onLocationChanged(Location location) {
                //mProgressDialog.dismiss();
                /*&& ActivityCompat.checkSelfPermission(pContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/
                if (ActivityCompat.checkSelfPermission(pContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.removeUpdates(listener);
                points = new CustomLatLong();
                points.setLatitude(location.getLatitude());
                points.setLongitude(location.getLongitude());
                pCurrentLocationCallback.onSuccess(points);
                points=null;
            }
        };

        locationManager.requestLocationUpdates(provider, 0, 0,listener );
    }

    public static void playChatSound(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
            final MediaPlayer mp = MediaPlayer.create(context, notification);
            mp.setLooping(false);
            mp.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mp != null && mp.isPlaying()) {
                        mp.stop();
                        mp.release();

                    }
                }
            }, 1000);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String  getCurrentVersion(Context context){
            PackageManager pm = context.getPackageManager();
            PackageInfo pInfo = null;

            try {
                pInfo =  pm.getPackageInfo(context.getPackageName(),0);

            } catch (PackageManager.NameNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if(pInfo != null){
                return pInfo.versionName;
            }
           return null;



    }

    public static String calculateETA(double speedInMeters,double distance) {

        double time;
        double speedPersec;
        double distanceInKm;
        distanceInKm=distance;
        speedPersec=speedInMeters;
        time = (distance / (speedPersec));


        int days = (int) (time / 24);

        int hours = (int) (time - (days * 24));

        int minutes = (int) (time * 60 - ((days * 24 * 60) + (hours * 60)));


        //String toString = ("ETA: " + days + " days " + hours + " hours " + minutes + " minutes ");
        return timeConversion((int) time);
    }


    private static String timeConversion(int totalSeconds) {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        String time="";

        if(seconds!=0&&minutes!=0&&hours!=0){
            time=hours + " hrs " + minutes + " min" + seconds + " sec";
        }else if(seconds!=0&&minutes!=0){
            time=minutes + " min " + seconds + " se";
        }else if(seconds!=0){
            time=seconds + " sec";
        }

        return time;
    }



    public static double distanceInKm(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }



    public static float distancebetween(double startLatitude,double  startLongitude,double endLatitude,
                                         double endLongitude){
        float[] results = new float[1];
        Location.distanceBetween(startLatitude, startLongitude,
                endLatitude, endLongitude, results);
        float distance = results[0];

        return distance;
    }


    public final static double AVERAGE_RADIUS_OF_EARTH = 6371;
    public static float calculateDistanceOnRoad(double userLat, double userLng, double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                (Math.cos(Math.toRadians(userLat))) *
                        (Math.cos(Math.toRadians(venueLat))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (float) (Math.round(AVERAGE_RADIUS_OF_EARTH * c));

    }

    }
