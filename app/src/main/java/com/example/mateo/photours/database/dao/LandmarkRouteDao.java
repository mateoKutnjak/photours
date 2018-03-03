package com.example.mateo.photours.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.mateo.photours.database.entities.Landmark;
import com.example.mateo.photours.database.entities.LandmarkRoute;

import java.util.List;

@Dao
public interface LandmarkRouteDao {

    @Query("SELECT * FROM landmarkroute")
    List<LandmarkRoute> getAll();

    @Query("SELECT COUNT(*) from landmarkroute")
    int count();

    @Insert
    long insert(LandmarkRoute landmarkRoute);

    @Delete
    void delete(LandmarkRoute landmarkRoute);

    @Query("DELETE FROM landmarkroute")
    void clear();

    @Query("SELECT l.* FROM landmarkroute lr LEFT OUTER JOIN landmark l ON lr.landmarkId = l.uid WHERE lr.routeId = :routeId")
    List<Landmark> findLandmarksForRouteId(long routeId);
}
