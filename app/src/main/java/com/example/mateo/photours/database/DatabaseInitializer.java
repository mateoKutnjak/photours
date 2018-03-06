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

    //public static void populateSync(@NonNull final AppDatabase db) {
        //populateWithInitData(db);
    //}

    private static long addLandmark(final DBAsync db, final String name, final String cloudLabel, final double latitude, final double longitude, final String message) {
        Landmark landmark = new Landmark();

        landmark.name = name;
        landmark.cloudLabel = cloudLabel;
        landmark.latitude = latitude;
        landmark.longitude = longitude;
        landmark.message = message;

        return db.landmarkADAO.insert(landmark);
    }

    private static long addRoute(final DBAsync db, final String name, final double length) {
        Route route = new Route();

        route.name = name;
        route.length = length;

        return db.routeADAO.insert(route);
    }

    private static long addLandmarkRoute(final DBAsync db, final long landmarkId, final long routeId) {
        LandmarkRoute landmarkRoute = new LandmarkRoute();

        landmarkRoute.landmarkId = landmarkId;
        landmarkRoute.routeId = routeId;

        return db.landmarkRouteADAO.insert(landmarkRoute);
    }

    private static void populateWithInitData(final DBAsync db) {
        db.landmarkADAO.clear();
        db.routeADAO.clear();
        db.landmarkRouteADAO.clear();

        long landmarkId_zagrebCathedral = addLandmark(db, "Zagreb cathedral", "Zagreb Cathedral",45.814548, 15.979477, "1000 years old");
        long landmarkId_croatianNationalTheatre = addLandmark(db, "Croatian national theater", "Croatian National Theatre in Zagreb", 45.809664, 15.969938, "Coming here with hobos");
        long landmarkId_lotrscakTower = addLandmark(db, "St. Mark's church", "St. Mark's Church",45.816382, 15.973630, "bum bum");
        long landmarkId_banJelacicMonument = addLandmark(db, "Ban Jelacic monument", "Josip Jelačić Statue", 45.813174, 15.977312, "Nademo se kod konja");

        long routeId_famousLandmarks = addRoute(db, "Famous landmarks", 0.0);
        long routeId_interestingLandmarks = addRoute(db, "Interesting landmarks", 0.0);
        long routeId_crazyLandmarks = addRoute(db, "Crazy landmarks", 0.0);

        addLandmarkRoute(db, landmarkId_zagrebCathedral, routeId_famousLandmarks);
        addLandmarkRoute(db, landmarkId_lotrscakTower, routeId_famousLandmarks);
        addLandmarkRoute(db, landmarkId_banJelacicMonument, routeId_famousLandmarks);
        addLandmarkRoute(db, landmarkId_croatianNationalTheatre, routeId_famousLandmarks);

        addLandmarkRoute(db, landmarkId_croatianNationalTheatre, routeId_interestingLandmarks);
        addLandmarkRoute(db, landmarkId_lotrscakTower, routeId_interestingLandmarks);

        addLandmarkRoute(db, landmarkId_lotrscakTower, routeId_crazyLandmarks);
        addLandmarkRoute(db, landmarkId_banJelacicMonument, routeId_crazyLandmarks);
    }

    public static void populate(@NonNull DBAsync dba) {
        populateWithInitData(dba);
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;

        PopulateDbAsync(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //populateWithInitData(mDb);
            return null;
        }
    }
}
