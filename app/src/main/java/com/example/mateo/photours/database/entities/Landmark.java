package com.example.mateo.photours.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "landmark")

public class Landmark {

    @PrimaryKey(autoGenerate = true)
    public long uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "message")
    public String message;
}