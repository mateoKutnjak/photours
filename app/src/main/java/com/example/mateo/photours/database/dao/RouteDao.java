package com.example.mateo.photours.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.mateo.photours.database.entities.Route;
import com.example.mateo.photours.database.views.RouteView;

import java.util.List;

@Dao
public interface RouteDao {

    @Query("SELECT * FROM route")
    List<Route> getAll();

    @Query("SELECT COUNT(*) from route")
    int countRoutes();

    @Insert
    long insert(Route route);

    @Delete
    void delete(Route route);

    @Query("DELETE FROM route")
    void clear();

    @Query("SELECT * FROM route WHERE name = :name")
    Route findByName(String name);

    @Query("SELECT name FROM route")
    List<String> getAllNames();

    @Query("UPDATE route SET length = :distance WHERE uid = :uid")
    void updateDistance(long uid, double distance);

    @Query("UPDATE route SET duration = :duration WHERE uid = :uid")
    void updateDuration(long uid, int duration);

    @Query("UPDATE route SET steps = :steps WHERE uid = :uid")
    void updateSteps(long uid, String steps);

    @Query("SELECT steps FROM route WHERE uid = :uid")
    String getSteps(long uid);

    @Query("SELECT uid, name, length, duration FROM route")
    List<Route> getAllWithoutSteps();

    @Query("SELECT COUNT(*) FROM route WHERE steps IS NOT NULL AND uid = :uid")
    int hasSteps(long uid);
}