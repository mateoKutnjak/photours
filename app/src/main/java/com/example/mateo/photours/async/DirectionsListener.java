package com.example.mateo.photours.async;

import com.google.android.gms.maps.model.PolylineOptions;

public interface DirectionsListener {
    void updateDirections(PolylineOptions po);
}
