package com.example.mateo.photours.database;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.mateo.photours.database.entities.Landmark;
import com.example.mateo.photours.database.entities.LandmarkRoute;
import com.example.mateo.photours.database.entities.Route;

public class DatabaseInitializer {

    public static void populateAsync(final AppDatabase db) {
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    public static void populateSync(@NonNull final AppDatabase db) {
        populateWithInitData(db);
    }

    private static long addLandmark(final AppDatabase db, final String name, final double latitude, final double longitude, final String message) {
        Landmark landmark = new Landmark();

        landmark.name = name;
        landmark.latitude = latitude;
        landmark.longitude = longitude;
        landmark.message = message;

        return db.landmarkDao().insert(landmark);
    }

    private static long addRoute(final AppDatabase db, final String name, final double length) {
        Route route = new Route();

        route.name = name;
        route.length = length;

        return db.routeDao().insert(route);
    }

    private static long addLandmarkRoute(final AppDatabase db, final long landmarkId, final long routeId) {
        LandmarkRoute landmarkRoute = new LandmarkRoute();

        landmarkRoute.landmarkId = landmarkId;
        landmarkRoute.routeId = routeId;

        return db.landmarkRouteDao().insert(landmarkRoute);
    }

    private static void populateWithInitData(final AppDatabase db) {
        db.landmarkDao().clear();
        db.routeDao().clear();
        db.landmarkRouteDao().clear();

        long landmarkId_zagrebCathedral = addLandmark(db, "Zagreb cathedral", 45.814436, 15.979879, "1000 years old");
        long landmarkId_croatianNationalTheatre = addLandmark(db, "Croatian national theater", 45.809406, 15.969945, "Coming here with hobos");
        long landmarkId_lotrscakTower = addLandmark(db, "Lotrscak tower", 45.814626, 15.973275, "bum bum");

        long routeId_famousLandmarks = addRoute(db, "Famous landmarks", 0.0);
        long routeId_interestingLandmarks = addRoute(db, "Interesting landmarks", 0.0);

        addLandmarkRoute(db, landmarkId_zagrebCathedral, routeId_famousLandmarks);
        addLandmarkRoute(db, landmarkId_croatianNationalTheatre, routeId_famousLandmarks);

        addLandmarkRoute(db, landmarkId_croatianNationalTheatre, routeId_interestingLandmarks);
        addLandmarkRoute(db, landmarkId_lotrscakTower, routeId_interestingLandmarks);
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;

        PopulateDbAsync(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(Void... params) {
            populateWithInitData(mDb);
            return null;
        }
    }
}
