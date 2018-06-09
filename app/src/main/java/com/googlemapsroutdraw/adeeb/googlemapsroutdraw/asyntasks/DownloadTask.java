package com.googlemapsroutdraw.adeeb.googlemapsroutdraw.asyntasks;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.googlemapsroutdraw.adeeb.googlemapsroutdraw.AppUtils;

/**
 * Created by Fizza Azam on 5/2/2016.
 */
// Fetches data from url passed

    // call like this
    //      DownloadTask downloadTask = new DownloadTask(map);

       // downloadTask.execute(url);

public class DownloadTask extends AsyncTask<String, Void, String> {

    private GoogleMap map;

    public DownloadTask(GoogleMap map){
        this.map = map;
    }

    // Downloading data in non-ui thread
    @Override
    protected String doInBackground(String... url) {

        // For storing data from web service
        String data = "";

        try{
            // Fetching the data from web service
            data = AppUtils.downloadUrl(url[0]);
        }catch(Exception e){
            //Log.d("Background Task",e.toString());
        }
        return data;
    }

    // Executes in UI thread, after the execution of
    // doInBackground()
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        ParserTask parserTask = new ParserTask(map);

        // Invokes the thread for parsing the JSON data
        parserTask.execute(result);
    }
}
