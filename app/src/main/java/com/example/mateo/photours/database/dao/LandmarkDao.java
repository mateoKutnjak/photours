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
    int count();

    @Insert
    long insert(Landmark landmark);

    @Delete
    void delete(Landmark landmark);

    @Query("DELETE FROM landmark")
    void clear();

    @Query("SELECT * FROM landmark WHERE name = :name")
    Landmark findByName(String name);
}