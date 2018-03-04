package com.example.mateo.photours.database.views;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class RouteView {

    @PrimaryKey
    public long uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "length")
    public double length;

    @ColumnInfo(name = "duration")
    public int duration;
}
