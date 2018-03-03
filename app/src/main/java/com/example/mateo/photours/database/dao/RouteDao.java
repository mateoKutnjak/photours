package com.example.mateo.photours.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.mateo.photours.database.entities.Landmark;
import com.example.mateo.photours.database.entities.Route;

import java.util.List;

@Dao
public interface RouteDao {

    @Query("SELECT * FROM route")
    List<Route> getAll();

    @Query("SELECT COUNT(*) from route")
    int countRoutes();

    @Insert
    void insertAll(Route... routes);

    @Delete
    void delete(Route route);
}