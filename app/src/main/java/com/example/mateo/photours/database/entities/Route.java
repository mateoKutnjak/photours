package com.example.mateo.photours.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.android.gms.maps.model.PolylineOptions;

@Entity(tableName = "route")
public class Route {

    @PrimaryKey(autoGenerate = true)
    public long uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "length")
    public double length;
}
