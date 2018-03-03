package com.example.mateo.photours.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.mateo.photours.database.entities.Route;

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
}