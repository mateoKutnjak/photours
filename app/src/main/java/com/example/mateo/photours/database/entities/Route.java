package com.example.mateo.photours.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.android.gms.maps.model.PolylineOptions;

@Entity(tableName = "route")
public class Route {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "name")

    private String name;

    @ColumnInfo(name = "length")
    private double length;

    @ColumnInfo(name = "landmarksNumber")
    private double landmarksNumber;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getLandmarksNumber() {
        return landmarksNumber;
    }

    public void setLandmarksNumber(double landmarksNumber) {
        this.landmarksNumber = landmarksNumber;
    }
}
