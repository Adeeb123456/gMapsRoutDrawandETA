package com.googlemapsroutdraw.adeeb.googlemapsroutdraw.Callbacks;


import com.googlemapsroutdraw.adeeb.googlemapsroutdraw.model.CustomLatLong;

public interface CurrentLocationCallback {
    public void onSuccess(CustomLatLong result);
    public void onFailure(String result);
    
}
