package com.example.mateo.photours.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Route.class,
                parentColumns = "id",
                childColumns = "routeId",
                onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Route.class,
                parentColumns = "id",
                childColumns = "routeId",
                onDelete = ForeignKey.CASCADE)
})

public class LandmarkRoute {

    @ColumnInfo(name = "routeId")
    private int routeId;

    @ColumnInfo(name = "landmarkId")
    private int landmarkId;

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getLandmarkId() {
        return landmarkId;
    }

    public void setLandmarkId(int landmarkId) {
        this.landmarkId = landmarkId;
    }
}
