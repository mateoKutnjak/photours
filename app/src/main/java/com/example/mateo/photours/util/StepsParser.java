package com.example.mateo.photours.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class StepsParser {

    public static String encode(PolylineOptions po) {
        StringBuilder sb = new StringBuilder();

        for(LatLng latlng : po.getPoints()) {
            sb.append(latlng.latitude).append(",").append(latlng.longitude).append(":");
        }

        if(sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }

    public static PolylineOptions decode(String encoded) {
        PolylineOptions po = new PolylineOptions();
        String[] latlngs = encoded.split(":");

        for(String latlng : latlngs) {
            String[] s = latlng.split(",");
            po.add(new LatLng(
                            Double.parseDouble(s[0]),
                            Double.parseDouble(s[1])));
        }
        return po;
    }
}
