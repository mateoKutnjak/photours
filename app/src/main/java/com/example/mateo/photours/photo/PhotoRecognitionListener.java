package com.example.mateo.photours.photo;

import com.example.mateo.photours.database.entities.Landmark;

import java.util.List;

public interface PhotoRecognitionListener {
    void photoRecognized(List<Landmark> results);
}
