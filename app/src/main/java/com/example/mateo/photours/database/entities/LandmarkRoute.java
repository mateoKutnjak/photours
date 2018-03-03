package com.example.mateo.photours.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "landmarkroute")

public class LandmarkRoute {

    @PrimaryKey(autoGenerate = true)
    public long uid;

    @ForeignKey(entity = Route.class, parentColumns = "uid", childColumns = "routeId", onDelete = ForeignKey.CASCADE)
    public long routeId;

    @ForeignKey(entity = Landmark.class, parentColumns = "uid", childColumns = "landmarkId", onDelete = ForeignKey.CASCADE)
    public long landmarkId;
}
