package com.example.mateo.photours.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.mateo.photours.database.entities.Landmark;

import java.util.List;


@Dao
public interface LandmarkDao {

    @Query("SELECT * FROM landmark")
    List<Landmark> getAll();

    @Query("SELECT COUNT(*) from landmark")
    int countUsers();

    @Insert
    void insertAll(Landmark... landmarks);

    @Delete
    void delete(Landmark landmark);
}