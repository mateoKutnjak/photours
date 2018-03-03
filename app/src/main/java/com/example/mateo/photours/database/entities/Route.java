package com.example.mateo.photours.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "route")
public class Route {

    @PrimaryKey(autoGenerate = true)
    public long uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "length")
    public double length;

    @ColumnInfo(name = "steps")
    public String steps;
}
