package com.googlemapsroutdraw.adeeb.googlemapsroutdraw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.googlemapsroutdraw.adeeb.googlemapsroutdraw.asyntasks.DownloadTask;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    private void setNavigationPath(LatLng origin, LatLng dest) {
        String url = getDirectionsUrl(origin, dest);
// give google map instance so to draw route
        GoogleMap map=null;

        DownloadTask downloadTask = new DownloadTask(map);

        downloadTask.execute(url);

    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {


        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

}
