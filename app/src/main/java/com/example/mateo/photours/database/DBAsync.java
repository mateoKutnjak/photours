package com.example.mateo.photours.database;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.example.mateo.photours.database.entities.Landmark;
import com.example.mateo.photours.database.entities.LandmarkRoute;
import com.example.mateo.photours.database.entities.Route;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class DBAsync {

    private AppDatabase db;

    public LandmarkAsyncDAO landmarkADAO = new LandmarkAsyncDAO();
    public RouteAsyncDAO routeADAO = new RouteAsyncDAO();
    public LandmarkRouteAsyncDAO landmarkRouteADAO = new LandmarkRouteAsyncDAO();

    private static DBAsync INSTANCE;

    public static DBAsync getDatabaseInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DBAsync(
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
//                            .allowMainThreadQueries()
//                            .fallbackToDestructiveMigration()
                            .build()
            );

        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private DBAsync(AppDatabase db) {
        this.db = db;
    }

    public class LandmarkAsyncDAO {

        public List<Landmark> getAll() {
            try {
                return new AsyncTask<Void, Void, List<Landmark>>() {
                    @Override
                    protected List<Landmark> doInBackground(Void... voids) {
                        return db.landmarkDao().getAll();
                    }


                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public long insert(final Landmark l) {
            try {
                return new AsyncTask<Void, Void, Long>() {
                    @Override
                    protected Long doInBackground(Void... voids) {
                        return db.landmarkDao().insert(l);
                    }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return -1;
        }

        public void deleteLandmark(final Landmark l) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    db.landmarkDao().delete(l);
                    return null;
                }
            }.execute();
        }

        public void clear() {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    db.landmarkDao().clear();
                    return null;
                }
            }.execute();
        }

        public int count() {
            try {
                return new AsyncTask<Void, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(Void... voids) {
                        return db.landmarkDao().count();
                    }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return -1;
        }

        public Landmark findByName(final String name) {
            try {
                return new AsyncTask<Void, Void, Landmark>() {
                    @Override
                    protected Landmark doInBackground(Void... voids) {
                        return db.landmarkDao().findByName(name);
                    }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public boolean isVisitedByName(final String name) {
            try {
                return new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        return db.landmarkDao().isVisitedByName(name);
                    }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return false;
        }

        public void setVisitedById(final long uid, final boolean value) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    db.landmarkDao().setVisitedById(uid, value);
                    return null;
                }
            }.execute();
        }
    }

    public class RouteAsyncDAO {

        public List<Route> getAll() {
            try {
                return new AsyncTask<Void, Void, List<Route>>() {
                    @Override
                    protected List<Route> doInBackground(Void... voids) {
                        return db.routeDao().getAll();
                    }


                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public long insert(final Route r) {
            try {
                return new AsyncTask<Void, Void, Long>() {
                    @Override
                    protected Long doInBackground(Void... voids) {
                        return db.routeDao().insert(r);
                    }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return -1;
        }

        public void delete(final Route r) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    db.routeDao().delete(r);
                    return null;
                }
            }.execute();
        }

        public void clear() {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    db.routeDao().clear();
                    return null;
                }
            }.execute();
        }

        public int count() {
            try {
                return new AsyncTask<Void, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(Void... voids) {
                        return db.routeDao().countRoutes();
                    }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return -1;
        }

        public Route findByName(final String name) {
            try {
                return new AsyncTask<Void, Void, Route>() {
                    @Override
                    protected Route doInBackground(Void... voids) {
                        return db.routeDao().findByName(name);
                    }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public List<String> getAllNames() {
            try {
                return new AsyncTask<Void, Void, List<String>>() {
                    @Override
                    protected List<String> doInBackground(Void... voids) {
                        return db.routeDao().getAllNames();
                    }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void updateDistance(final long uid, final double distance) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    db.routeDao().updateDistance(uid, distance);
                    return null;
                }
            }.execute();
        }

        public void updateDuration(final long uid, final int duration) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    db.routeDao().updateDuration(uid, duration);
                    return null;
                }
            }.execute();
        }

        public void updateSteps(final long uid, final String steps) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    db.routeDao().updateSteps(uid, steps);
                    return null;
                }
            }.execute();
        }

        public String getSteps(final long uid) {
            try {
                return new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        return db.routeDao().getSteps(uid);
                    }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public List<Route> getAllWithoutSteps() {
            try {
                return new AsyncTask<Void, Void, List<Route>>() {
                    @Override
                    protected List<Route> doInBackground(Void... voids) {
                        return db.routeDao().getAllWithoutSteps();
                    }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public int hasSteps(final long uid) {
            try {
                return new AsyncTask<Void, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(Void... voids) {
                        return db.routeDao().hasSteps(uid);
                    }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public class LandmarkRouteAsyncDAO {

        public List<LandmarkRoute> getAll() {
            try {
                return new AsyncTask<Void, Void, List<LandmarkRoute>>() {
                    @Override
                    protected List<LandmarkRoute> doInBackground(Void... voids) {
                        return db.landmarkRouteDao().getAll();
                    }


                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public long insert(final LandmarkRoute lr) {
            try {
                return new AsyncTask<Void, Void, Long>() {
                    @Override
                    protected Long doInBackground(Void... voids) {
                        return db.landmarkRouteDao().insert(lr);
                    }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return -1;
        }

        public void delete(final Route r) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    db.routeDao().delete(r);
                    return null;
                }
            }.execute();
        }

        public void clear() {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    db.landmarkRouteDao().clear();
                    return null;
                }
            }.execute();
        }

        public int countForRouteId(final long routeId) {
            try {
                return new AsyncTask<Void, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(Void... voids) {
                        return db.landmarkRouteDao().countForRouteId(routeId);
                    }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return -1;
        }

        public List<Landmark> findLandmarksForRouteId(final long routeId) {
            try {
                return new AsyncTask<Void, Void, List<Landmark>>() {
                    @Override
                    protected List<Landmark> doInBackground(Void... voids) {
                        return db.landmarkRouteDao().findLandmarksForRouteId(routeId);
                    }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public List<String> findLandmarkNamesForRouteId(final long routeId) {
            try {
                return new AsyncTask<Void, Void, List<String>>() {
                    @Override
                    protected List<String> doInBackground(Void... voids) {
                        return db.landmarkRouteDao().findLandmarkNamesForRouteId(routeId);
                    }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public int countVisitedLandmarksForRouteId(final long routeId, final boolean visited) {
            try {
                return new AsyncTask<Void, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(Void... voids) {
                        return db.landmarkRouteDao().countVisitedLandmarksForRouteId(routeId, visited);
                    }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return -1;
        }
    }
}
