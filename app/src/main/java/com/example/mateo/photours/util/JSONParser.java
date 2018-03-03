package com.example.mateo.photours.util;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {

    public static Pair<Integer, Integer> getDistanceDurationFromDirections (JSONObject jsonObject) {

        int totalDistance = 0;
        int totalDuration = 0;

        try {
            JSONArray routes = jsonObject.getJSONArray("routes");
            JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");

            for(int i = 0; i < legs.length(); i++) {
                JSONObject distance = legs.getJSONObject(i).getJSONObject("distance");
                JSONObject duration = legs.getJSONObject(i).getJSONObject("duration");

                totalDistance =+ distance.getInt("value");
                totalDuration =+ duration.getInt("value");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Pair<Integer, Integer>(totalDistance, totalDuration);
    }
}
