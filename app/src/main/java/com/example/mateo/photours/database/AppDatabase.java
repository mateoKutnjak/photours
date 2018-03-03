package com.example.mateo.photours.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.mateo.photours.database.dao.LandmarkDao;
import com.example.mateo.photours.database.dao.RouteDao;
import com.example.mateo.photours.database.entities.Landmark;
import com.example.mateo.photours.database.entities.Route;

@Database(entities = {Landmark.class, Route.class}, version = 1, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract LandmarkDao landmarkDao();
    public abstract RouteDao routeDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}