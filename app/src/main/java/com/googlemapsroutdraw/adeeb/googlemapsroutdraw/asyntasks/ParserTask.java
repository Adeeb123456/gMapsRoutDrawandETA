package com.googlemapsroutdraw.adeeb.googlemapsroutdraw.asyntasks;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.googlemapsroutdraw.adeeb.AppConstants;
import com.googlemapsroutdraw.adeeb.googlemapsroutdraw.AppUtils;
import com.googlemapsroutdraw.adeeb.googlemapsroutdraw.parcer.DirectionsJSONParser;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Fizza Azam on 5/2/2016.
 */
public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {

    private GoogleMap map;

    public ParserTask(GoogleMap map){
        this.map = map;
    }


    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try{
            jObject = new JSONObject(jsonData[0]);
            DirectionsJSONParser parser = new DirectionsJSONParser();

            // Starts parsing data
            routes = parser.parse(jObject);
        }catch(Exception e){
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;
        MarkerOptions markerOptions = new MarkerOptions();
        double distance=0;
        try{
        // Traversing through all the routes
        if (result != null && result.size() > 0){
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                LatLng lastLAtlon;
                HashMap<String,String> pointNext=null;
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);




                    if(j<path.size()-1)
                         pointNext = path.get(j+1);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    double latNext = Double.parseDouble(pointNext.get("lat"));
                    double lngNext = Double.parseDouble(pointNext.get("lng"));

                    LatLng position = new LatLng(lat, lng);

                    LatLng positionNext = new LatLng(latNext, lngNext);
                    try {
                       distance += SphericalUtil.computeDistanceBetween(position, positionNext);

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.RED);
            }
          //  Log.d("distance","dis map cordinates "+distance);
            AppConstants.ETA.ETA= AppUtils.calculateETA(AppConstants.ETA.speed,distance);
            sendBroadCastETA(AppConstants.ETA.ETA);
            //Log.d("distance","eta "+AppConstants.ETA.ETA);
            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void sendBroadCastETA(String etaValue){
        try{
            Intent intent = new Intent(AppConstants.ETA.ETAIntentFilter);
            intent.putExtra(AppConstants.ETA.ETAKey, etaValue);
            // put your all data using put extra

            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
