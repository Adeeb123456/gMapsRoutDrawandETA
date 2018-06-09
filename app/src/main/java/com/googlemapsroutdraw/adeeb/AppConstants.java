package com.googlemapsroutdraw.adeeb;

import android.os.Environment;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by adeeb on 6/10/2018.
 */

public class AppConstants {
    public static class ETA{
        public static String ETA="";
        public static double speed=
                7.1111111111;  // average speed in us 70 miles per hours , converted to meter per sec
        // 40miles per hr ,
        public static String ETAIntentFilter="EtaIntent";
        public static String ETAKey="keyETA";
        public static double distance=0;
        public static LatLng latLngDestinLocation=null;


    }


    public final static String EXTERNAL_FILE_PHOTO_DIRECTORY = Environment.getExternalStorageDirectory()+"/"+"Consumer/cache";
    public final static String EXTERNAL_FILE_PATH = "profile_temp.jpg";
}
