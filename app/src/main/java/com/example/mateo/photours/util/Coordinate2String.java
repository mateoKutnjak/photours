package com.example.mateo.photours.util;

import com.example.mateo.photours.database.entities.Landmark;

import java.util.ArrayList;
import java.util.List;

public class Coordinate2String {

    public List<String> coordinates;

    public Coordinate2String(List<Landmark> landmarks) {
        coordinates = new ArrayList<>();

        for(Landmark landmark : landmarks) {
            coordinates.add(String.valueOf(landmark.latitude) + "," + String.valueOf(landmark.longitude));
        }
    }

    public String getOriginParam() {
        return coordinates.get(0);
    }

    public String getDestParam() {
        return coordinates.get(coordinates.size()-1);
    }

    public String getWaypointsParam(boolean optimization) {
        if(coordinates.size() <= 2) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append(optimization ? "optimize:true|" : "");

        for(int i = 1; i < coordinates.size()-1; i++) {
            sb.append(coordinates.get(i) + "|");
        }

        if(sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }
}
