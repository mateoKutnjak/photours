package com.example.mateo.photours;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mateo.photours.adapters.ELVAdapter;
import com.example.mateo.photours.async.DirectionsListener;
import com.example.mateo.photours.async.ParserTask;
import com.example.mateo.photours.database.AppDatabase;
import com.example.mateo.photours.database.DBAsync;
import com.example.mateo.photours.database.DatabaseInitializer;
import com.example.mateo.photours.database.entities.Landmark;
import com.example.mateo.photours.database.entities.Route;
import com.example.mateo.photours.database.views.RouteView;
import com.example.mateo.photours.photo.CloudAPI;
import com.example.mateo.photours.photo.PhotoRecognitionListener;
import com.example.mateo.photours.util.CameraUtil;
import com.example.mateo.photours.util.Coordinate2String;
import com.example.mateo.photours.util.PermissionUtils;
import com.example.mateo.photours.util.StepsParser;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        PhotoRecognitionListener,
        DirectionsListener {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;

<<<<<<< HEAD
        private List<Landmark> currentLandmarks;
        private List<Marker> currentMarkers;
        private PolylineOptions directionsPO;

        private int currentGroupPosition;
        private RouteView currentRV;
        private ExpandableListView elv;
        private ELVAdapter adapter;
        private List<RouteView> listCategories;
        private Map<RouteView, List<String>> childMap;

        private CloudAPI cloudAPI;
=======
    private List<Landmark> currentLandmarks;
    private List<Marker> currentMarkers;
    private PolylineOptions directionsPO;

    private int currentGroupPosition;
    private RouteView currentRV;
    private ExpandableListView elv;
    private ELVAdapter adapter;
    private List<RouteView> listCategories;
    private Map<RouteView, List<String>> childMap;

    private CloudAPI cloudAPI;
>>>>>>> nemam_komentara

    private DBAsync db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
                setContentView(R.layout.activity_maps);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Global.REQUEST_LOCATION);

                initCloudAPI();
        initDatabase();

                initELV();
                fillELV();

                addCameraFAB();
                addMapFragment();

                addInfoFAB();
    }
        private void initCloudAPI() {
        cloudAPI = new CloudAPI(this, this);
    }
        private void initDatabase() {
=======
        setContentView(R.layout.activity_maps);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Global.REQUEST_LOCATION);

        initCloudAPI();
        initDatabase();

        initELV();
        fillELV();

        addCameraFAB();
        addMapFragment();

        addInfoFAB();
    }

    private void initCloudAPI() {
        cloudAPI = new CloudAPI(this, this);
    }

    private void initDatabase() {
>>>>>>> nemam_komentara
        db = DBAsync.getDatabaseInstance(this);
        DatabaseInitializer.populate(db);
    }

    private void initELV() {
<<<<<<< HEAD
                elv = (ExpandableListView) findViewById(R.id.expList);
                List<Route> routes = db.routeADAO.getAll();
                listCategories = new ArrayList<>();

                for(int i = 0; i < routes.size(); i++
=======
        elv = (ExpandableListView) findViewById(R.id.expList);
        List<Route> routes = db.routeADAO.getAll();
        listCategories = new ArrayList<>();

        for (int i = 0; i < routes.size(); i++
>>>>>>> nemam_komentara
                ) {
            RouteView rv = new RouteView();
            rv.uid = routes.get(i).uid;
            rv.name = routes.get(i).name;
            rv.length = routes.get(i).length;
            rv.duration = routes.get(i).duration;
<<<<<<< HEAD
                        rv.visited = db.landmarkRouteADAO.countVisitedLandmarksForRouteId(rv.uid, true);
                        rv.totalLandmarks = db.landmarkRouteADAO.countForRouteId(rv.uid);

                        listCategories.add(rv);
        }
                childMap = new HashMap<>();
    }

    private void fillELV() {
                List<Route> routes = db.routeADAO.getAll();

        for (int i = 0; i < routes.size(); i++) {
                        List<String> landmarkNames = db.landmarkRouteADAO.findLandmarkNamesForRouteId(routes.get(i).uid);
                                    childMap.put(listCategories.get(i), landmarkNames);
        }
                adapter = new ELVAdapter(listCategories, childMap, this);
                elv.setAdapter(adapter);
                elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
=======
            rv.visited = db.landmarkRouteADAO.countVisitedLandmarksForRouteId(rv.uid, true);
            rv.totalLandmarks = db.landmarkRouteADAO.countForRouteId(rv.uid);

            listCategories.add(rv);
        }
        childMap = new HashMap<>();
    }

    private void fillELV() {
        List<Route> routes = db.routeADAO.getAll();

        for (int i = 0; i < routes.size(); i++) {
            List<String> landmarkNames = db.landmarkRouteADAO.findLandmarkNamesForRouteId(routes.get(i).uid);
            childMap.put(listCategories.get(i), landmarkNames);
        }
        adapter = new ELVAdapter(listCategories, childMap, this);
        elv.setAdapter(adapter);
        elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
>>>>>>> nemam_komentara
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                selectRoute(groupPosition);
                return false;
            }
        });

        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
<<<<<<< HEAD
                                Landmark landmark = db.landmarkADAO.findByName((String) adapter.getChild(groupPosition, childPosition));
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(landmark.latitude, landmark.longitude)));
                                currentMarkers.get(childPosition).showInfoWindow();
=======
                Landmark landmark = db.landmarkADAO.findByName((String) adapter.getChild(groupPosition, childPosition));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(landmark.latitude, landmark.longitude)));
                currentMarkers.get(childPosition).showInfoWindow();
>>>>>>> nemam_komentara
                return false;
            }
        });
    }
<<<<<<< HEAD
        private void addCameraFAB() {
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
=======

    private void addCameraFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
>>>>>>> nemam_komentara
            @Override
            public void onClick(View view) {
                CameraUtil.startCamera(MapsActivity.this);
            }
        });
    }

    private void addMapFragment() {
<<<<<<< HEAD
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
=======
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
>>>>>>> nemam_komentara
    }

    private void addInfoFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabClose);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
<<<<<<< HEAD
                CoordinatorLayout cl = (CoordinatorLayout)findViewById(R.id.infoCoordLayout);
                LinearLayout ll = (LinearLayout)findViewById(R.id.infoLinLayout);
                                cl.setVisibility(CoordinatorLayout.INVISIBLE);
=======
                CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.infoCoordLayout);
                LinearLayout ll = (LinearLayout) findViewById(R.id.infoLinLayout);
                cl.setVisibility(CoordinatorLayout.INVISIBLE);
>>>>>>> nemam_komentara
                ll.setVisibility(LinearLayout.INVISIBLE);
            }
        });
    }

    private void updateStatusBar() {
        TextView tv = (TextView) findViewById(R.id.statusView);
        tv.setText(currentRV.name);
    }

    @Override
<<<<<<< HEAD
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == Global.CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
=======
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Global.CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
>>>>>>> nemam_komentara
            cloudAPI.init();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Global.CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, Global.CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    CameraUtil.startCamera(this);
                }
                Log.d(TAG, "Please allow us to use your camera :) ");
                break;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        styleMap(R.raw.map_style_aubergine_labels);
        selectRoute(Global.ZERO);
<<<<<<< HEAD
                downloadAllDirections();
                elv.deferNotifyDataSetChanged();
=======
        downloadAllDirections();
        elv.deferNotifyDataSetChanged();
>>>>>>> nemam_komentara
    }

    private void styleMap(int map_style) {
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, map_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style.", e);
        }
    }

    private void selectRoute(int groupPosition) {
        currentGroupPosition = groupPosition;

        mMap.clear();
<<<<<<< HEAD
                currentRV = (RouteView)adapter.getGroup(groupPosition);
                currentLandmarks = db.landmarkRouteADAO.findLandmarksForRouteId(currentRV.uid);
=======
        currentRV = (RouteView) adapter.getGroup(groupPosition);
        currentLandmarks = db.landmarkRouteADAO.findLandmarksForRouteId(currentRV.uid);

>>>>>>> nemam_komentara

        
        
        String steps = db.routeADAO.getSteps(currentRV.uid);

        if (steps == null) {
            downloadDirections(currentGroupPosition);
        } else {
<<<<<<< HEAD
                        directionsPO = StepsParser.decode(steps);
                        directionsPO.color(Color.GRAY);
            directionsPO.width(15);
                        mMap.addPolyline(directionsPO);
        }

                List<MarkerOptions> markerOpts = drawMarkers();
                centerRouteOnMap(markerOpts);
    }

    private List<MarkerOptions> drawMarkers() {
                List<MarkerOptions> markersOpts = new ArrayList<>();
                currentMarkers = new ArrayList<>();

        for (Landmark landmark : currentLandmarks) {
            LatLng point = new LatLng(landmark.latitude, landmark.longitude);
                        markersOpts.add(new MarkerOptions()
=======
            directionsPO = StepsParser.decode(steps);
            directionsPO.color(Color.GRAY);
            directionsPO.width(15);
            mMap.addPolyline(directionsPO);
        }

        List<MarkerOptions> markerOpts = drawMarkers();
        centerRouteOnMap(markerOpts);
    }

    private List<MarkerOptions> drawMarkers() {
        List<MarkerOptions> markersOpts = new ArrayList<>();
        currentMarkers = new ArrayList<>();

        for (Landmark landmark : currentLandmarks) {
            LatLng point = new LatLng(landmark.latitude, landmark.longitude);
            markersOpts.add(new MarkerOptions()
>>>>>>> nemam_komentara
                    .position(point)
                    .title(landmark.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(
                            landmark.visited ? BitmapDescriptorFactory.HUE_YELLOW : BitmapDescriptorFactory.HUE_AZURE)));
<<<<<<< HEAD
                        Marker marker = mMap.addMarker(markersOpts.get(markersOpts.size() - 1));
                        currentMarkers.add(marker);
=======
            Marker marker = mMap.addMarker(markersOpts.get(markersOpts.size() - 1));
            currentMarkers.add(marker);
>>>>>>> nemam_komentara
        }
        return markersOpts;
    }

    private void centerRouteOnMap(List<MarkerOptions> markers) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.1);

        LatLng newSoutheast = new LatLng(bounds.southwest.latitude - (bounds.northeast.latitude - bounds.southwest.latitude) * Global.MAP_ROUTE_FOCUS_BOTTOM_PADDING, bounds.southwest.longitude);
        LatLng newNorthEast = new LatLng(bounds.northeast.latitude + (bounds.northeast.latitude - bounds.southwest.latitude) * Global.MAP_ROUTE_FOCUS_TOP_PADDING, bounds.northeast.longitude);
        LatLngBounds bounds2 = new LatLngBounds(newSoutheast, newNorthEast);
<<<<<<< HEAD
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds2, width, height, padding);
                mMap.animateCamera(cu);
    }

    private void downloadAllDirections() {
                List<Route> routes = db.routeADAO.getAll();
=======
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds2, width, height, padding);
        mMap.animateCamera(cu);
    }

    private void downloadAllDirections() {
        List<Route> routes = db.routeADAO.getAll();
>>>>>>> nemam_komentara

        for (int i = 0; i < routes.size(); i++) {
            downloadDirections(i);
        }
    }

    private void updateEVL(int groupPosition, int distance, int duration) {
<<<<<<< HEAD
        RouteView rv = (RouteView)adapter.getGroup(groupPosition);
                listCategories.get(groupPosition).length = distance;
        listCategories.get(groupPosition).duration = duration;
        listCategories.get(groupPosition).visited = db.landmarkRouteADAO.countVisitedLandmarksForRouteId(rv.uid, true);
        listCategories.get(groupPosition).totalLandmarks = db.landmarkRouteADAO.countForRouteId(rv.uid);
                db.routeADAO.updateDistance(rv.uid, distance);
        db.routeADAO.updateDuration(rv.uid, duration);
                elv.deferNotifyDataSetChanged();
    }

    private void downloadDirections(final int groupPosition) {
        final RouteView rv = (RouteView)adapter.getGroup(groupPosition);
                List<Landmark> landmarks;
        if(groupPosition != currentGroupPosition) {
=======
        RouteView rv = (RouteView) adapter.getGroup(groupPosition);
        listCategories.get(groupPosition).length = distance;
        listCategories.get(groupPosition).duration = duration;
        listCategories.get(groupPosition).visited = db.landmarkRouteADAO.countVisitedLandmarksForRouteId(rv.uid, true);
        listCategories.get(groupPosition).totalLandmarks = db.landmarkRouteADAO.countForRouteId(rv.uid);
        db.routeADAO.updateDistance(rv.uid, distance);
        db.routeADAO.updateDuration(rv.uid, duration);
        elv.deferNotifyDataSetChanged();
    }

    private void downloadDirections(final int groupPosition) {
        final RouteView rv = (RouteView) adapter.getGroup(groupPosition);
        List<Landmark> landmarks;
        if (groupPosition != currentGroupPosition) {
>>>>>>> nemam_komentara
            landmarks = db.landmarkRouteADAO.findLandmarksForRouteId(rv.uid);
        } else {
            landmarks = currentLandmarks;
        }

        if (landmarks.size() < 2) {
            throw new IllegalArgumentException();
        }

        Coordinate2String c2s = new Coordinate2String(landmarks);
<<<<<<< HEAD
                Uri.Builder builder = new Uri.Builder();
=======
        Uri.Builder builder = new Uri.Builder();
>>>>>>> nemam_komentara
        builder.scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("directions")
                .appendPath("json")
                .appendQueryParameter("origin", c2s.getOriginParam())
                .appendQueryParameter("destination", c2s.getDestParam())
                .appendQueryParameter("mode", Global.TRAVEL_MODE_WALKING)
                .appendQueryParameter("waypoints", c2s.getWaypointsParam(true))
                .appendQueryParameter("key", Global.SERVER_KEY)
                .appendQueryParameter("sensor", "true");
<<<<<<< HEAD
                Log.d(TAG, "Request: " + builder.build().toString());
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, builder.build().toString(), null, new Response.Listener<JSONObject>() {
                                        @Override
=======
        Log.d(TAG, "Request: " + builder.build().toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, builder.build().toString(), null, new Response.Listener<JSONObject>() {
                    @Override
>>>>>>> nemam_komentara
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        ParserTask parserTask = new ParserTask(MapsActivity.this);
                        parserTask.execute(response.toString());

                        updateEVL(groupPosition, parserTask.getTotalDistance(), parserTask.getTotalDuration());
                    }
                }, new Response.ErrorListener() {
<<<<<<< HEAD
                                        @Override
=======
                    @Override
>>>>>>> nemam_komentara
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: " + error
                                + "\nStatus Code " + error.networkResponse.statusCode
                                + "\nResponse Data " + error.networkResponse.data
                                + "\nCause " + error.getCause()
                                + "\nmessage" + error.getMessage());
                    }
                }) {
<<<<<<< HEAD
                        @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                                return headers;
            }
                        @Override
=======
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                return headers;
            }

            @Override
>>>>>>> nemam_komentara
            public String getBodyContentType() {
                return "application/json";
            }
        };
<<<<<<< HEAD
                RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsObjRequest);
    }
        @Override
=======
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsObjRequest);
    }

    @Override
>>>>>>> nemam_komentara
    public void photoRecognized(List<Landmark> results, int errorCode) {
        String toastText = null;

        List<Landmark> allLandmarks = db.landmarkADAO.getAll();

        switch (errorCode) {
            case Global.ERROR_API_REQUEST:
                toastText = "Connection error. Try again.";
                break;
            case Global.ERROR_LANDMARK_NOT_RECOGNIZED:
                toastText = "Landmark not recognized. Try again";
                break;
            case Global.ERROR_TIMEOUT_EXPIRED:
                toastText = "Timeout expired. Try again.";
                break;
            case Global.ERROR_NO_ERROR:
                for (Landmark landmark : allLandmarks) {
                    for (Landmark result : results) {
                        if (landmark.cloudLabel.equals(result.cloudLabel)) {
<<<<<<< HEAD
                                                        setLandmarkVisited(landmark);
                                                        createPopup(landmark);
=======
                            setLandmarkVisited(landmark);
                            createPopup(landmark);
>>>>>>> nemam_komentara

                            refreshListCategories();
                            adapter.refresh(listCategories, childMap);
                            refreshMap();

                            return;
                        }
                    }
                }
                toastText = results.get(0).cloudLabel + " (not on the map)";
        }
        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
    }

<<<<<<< HEAD
        private void createPopup(Landmark currLandmark) {
        CoordinatorLayout cl = (CoordinatorLayout)findViewById(R.id.infoCoordLayout);
        LinearLayout ll = (LinearLayout)findViewById(R.id.infoLinLayout);
=======
    private void createPopup(Landmark currLandmark) {
        CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.infoCoordLayout);
        LinearLayout ll = (LinearLayout) findViewById(R.id.infoLinLayout);
>>>>>>> nemam_komentara

        ImageView iv = (ImageView) findViewById(R.id.infoImageView);
        TextView tv = (TextView) findViewById(R.id.infoTextView);

<<<<<<< HEAD
        
                iv.setImageBitmap(cloudAPI.getBitmap());
        tv.setText(currLandmark.name + " " + currLandmark.message);
                cl.setVisibility(CoordinatorLayout.VISIBLE);
=======

        iv.setImageBitmap(cloudAPI.getBitmap());
        tv.setText(currLandmark.name + " " + currLandmark.message);
        cl.setVisibility(CoordinatorLayout.VISIBLE);
>>>>>>> nemam_komentara
        ll.setVisibility(LinearLayout.VISIBLE);
    }

    private void setLandmarkVisited(Landmark currLandmark) {
<<<<<<< HEAD
                db.landmarkADAO.setVisitedById(currLandmark.uid, true);
    }

    private void refreshMap() {
                mMap.clear();
                RouteView rv = (RouteView)adapter.getGroup(currentGroupPosition);
                currentLandmarks = db.landmarkRouteADAO.findLandmarksForRouteId(rv.uid);
                mMap.addPolyline(directionsPO);
                drawMarkers();
    }

    private void refreshListCategories() {
                List<Route> routes = db.routeADAO.getAllWithoutSteps();
                listCategories = new ArrayList<>();

        for(int i = 0; i < routes.size(); i++) {
                        RouteView rv = new RouteView();
=======
        db.landmarkADAO.setVisitedById(currLandmark.uid, true);
    }

    private void refreshMap() {
        mMap.clear();
        RouteView rv = (RouteView) adapter.getGroup(currentGroupPosition);
        currentLandmarks = db.landmarkRouteADAO.findLandmarksForRouteId(rv.uid);
        mMap.addPolyline(directionsPO);
        drawMarkers();
    }

    private void refreshListCategories() {
        List<Route> routes = db.routeADAO.getAllWithoutSteps();
        listCategories = new ArrayList<>();

        for (int i = 0; i < routes.size(); i++) {
            RouteView rv = new RouteView();
>>>>>>> nemam_komentara
            rv.uid = routes.get(i).uid;
            rv.name = routes.get(i).name;
            rv.length = routes.get(i).length;
            rv.duration = routes.get(i).duration;
<<<<<<< HEAD
                        rv.visited = db.landmarkRouteADAO.countVisitedLandmarksForRouteId(rv.uid, true);
            rv.totalLandmarks = db.landmarkRouteADAO.countForRouteId(rv.uid);
                        listCategories.add(rv);
        }

                for (int i = 0; i < routes.size(); i++) {
                        List<String> landmarkNames = db.landmarkRouteADAO.findLandmarkNamesForRouteId(routes.get(i).uid);
                        childMap.put(listCategories.get(i), landmarkNames);
        }
    }

        @Override
    public void updateDirections(PolylineOptions po) {
                directionsPO = po;
                mMap.addPolyline(directionsPO);
                savePolyline(po);
    }
        private void savePolyline(PolylineOptions po) {
        if(db.routeADAO.hasSteps(currentRV.uid) == 0) {
                        db.routeADAO.updateSteps(currentRV.uid, StepsParser.encode(po));
=======
            rv.visited = db.landmarkRouteADAO.countVisitedLandmarksForRouteId(rv.uid, true);
            rv.totalLandmarks = db.landmarkRouteADAO.countForRouteId(rv.uid);
            listCategories.add(rv);
        }

        for (int i = 0; i < routes.size(); i++) {
            List<String> landmarkNames = db.landmarkRouteADAO.findLandmarkNamesForRouteId(routes.get(i).uid);
            childMap.put(listCategories.get(i), landmarkNames);
        }
    }

    @Override
    public void updateDirections(PolylineOptions po) {
        directionsPO = po;
        mMap.addPolyline(directionsPO);
        savePolyline(po);
    }

    private void savePolyline(PolylineOptions po) {
        if (db.routeADAO.hasSteps(currentRV.uid) == 0) {
            db.routeADAO.updateSteps(currentRV.uid, StepsParser.encode(po));
>>>>>>> nemam_komentara
        }
    }
}