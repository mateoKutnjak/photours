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

    private AppDatabase db;

    private GoogleMap mMap;

    // List that contains current route
    private List<Landmark> currentLandmarks;
    // List that contains markers for current route
    private List<Marker> currentMarkers;
    // Draw route on map, list of latitude, longitude of landmarks of route
    private PolylineOptions directionsPO;
    // Integer value that represents groups on expendable list for routes
    private int currentGroupPosition;
    // Contains name of the currently selected route, time and number of landmarks in it
    private RouteView currentRV;
    // Part of the UI that contains groups/routes
    private ExpandableListView elv;
    // Helper object that fetches data for ListView
    private ELVAdapter adapter;
    // The main routes with which we create expendable list
    private List<RouteView> listCategories;
    // Open map that returns list of strings with landmarks
    private Map<RouteView, List<String>> childMap;
    // Object that recognizes picture
    private CloudAPI cloudAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // from which xml we are getting layout (activity_maps.xml)
        setContentView(R.layout.activity_maps);
        // ask for permission for location
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Global.REQUEST_LOCATION);

        // init photo recognition and database manipulation
        initCloudAPI();
        initDatabase();

        // initialize expendable ListView
        initELV();
        // fill listView with groups and childs
        fillELV();

        // add camera button
        addCameraFAB();
        // add map in app
        addMapFragment();

        // close the recognized photo pop-up
        addInfoFAB();
    }
    // Create cloudApi object
    private void initCloudAPI() {
        cloudAPI = new CloudAPI(this, this);
    }
    // create database object and initialize hardcoded data (landmarks and routes)
    private void initDatabase() {
        db = AppDatabase.getAppDatabase(getApplicationContext());
        DatabaseInitializer.populateSync(db);
    }

    private void initELV() {
        // fetch expendable list view from layout
        elv = (ExpandableListView) findViewById(R.id.expList);
        // fetch all routs except steps (points on map on which we draw route
        List<Route> routes = db.routeDao().getAllWithoutSteps();
        // route of landmark categories
        listCategories = new ArrayList<>();

        // create new object (routeView) not to include steps
        for(int i = 0; i < routes.size(); i++) {
            RouteView rv = new RouteView();
            rv.uid = routes.get(i).uid;
            rv.name = routes.get(i).name;
            rv.length = routes.get(i).length;
            rv.duration = routes.get(i).duration;
            // how much landmarks has been visited
            rv.visited = db.landmarkRouteDao().countVisitedLandmarksForRouteId(rv.uid, true);
            // count all landmarks on route
            rv.totalLandmarks = db.landmarkRouteDao().countForRouteId(rv.uid);

            // add in listCategories routeView
            listCategories.add(rv);
        }
        // save child of groups (landmarks) as String
        childMap = new HashMap<>();
    }

    private void fillELV() {
        // fetch all routes from databease
        List<Route> routes = db.routeDao().getAll();

        for (int i = 0; i < routes.size(); i++) {
            // fetch the name of the landmark which is contained in such route
            List<String> landmarkNames = db.landmarkRouteDao().findLandmarkNamesForRouteId(routes.get(i).uid);
            // populate child map with landmark names, dictionary that has routeView as key and
            // list of strings (landmark names) as values
            childMap.put(listCategories.get(i), landmarkNames);
        }
        // save all data that ELV uses (data structure behind ELV)
        adapter = new ELVAdapter(listCategories, childMap, this);
        // connect expendableListView with adapter
        elv.setAdapter(adapter);
        // define behavior what happens when user clicks group
        elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            // index for selected group
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                selectRoute(groupPosition);
                return false;
            }
        });

        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // fetch currently select landmark from db
                Landmark landmark = db.landmarkDao().findByName((String) adapter.getChild(groupPosition, childPosition));
                // move camera that centers marker
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(landmark.latitude, landmark.longitude)));
                // show text for marker
                currentMarkers.get(childPosition).showInfoWindow();
                return false;
            }
        });
    }
    // what happens when user presses camera
    private void addCameraFAB() {
        // show camera button from activity_maps.xml
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        // set listener on fab object that waits user action
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraUtil.startCamera(MapsActivity.this);
            }
        });
    }

    private void addMapFragment() {
        // generic object that contains mapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        // draw map on that fragmet
        mapFragment.getMapAsync(this);
    }

    private void addInfoFAB() {
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fabClose);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoordinatorLayout cl = (CoordinatorLayout)findViewById(R.id.infoCoordLayout);
                LinearLayout ll = (LinearLayout)findViewById(R.id.infoLinLayout);
                // close Info layout after clicking on X
                cl.setVisibility(CoordinatorLayout.INVISIBLE);
                ll.setVisibility(LinearLayout.INVISIBLE);
            }
        });
    }

    private void updateStatusBar() {
        TextView tv = (TextView)findViewById(R.id.statusView);
        tv.setText(currentRV.name);
    }

    @Override
    // what happens when user takes photo
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if user allows permission to use camera and the picture has been taken initialize cloudAPI
        if (requestCode == Global.CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
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
        // method that downloads directions (steps from json) for all routes
        downloadAllDirections();
        // refresh elv
        elv.deferNotifyDataSetChanged();
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
        // get RouteView that contains data for certain group
        currentRV = (RouteView)adapter.getGroup(groupPosition);
        // fetch Landmarks in current route from database
        currentLandmarks = db.landmarkRouteDao().findLandmarksForRouteId(currentRV.uid);

        //updateStatusBar();

        // check if route hasn't steps in it, download steps and draw else draw steps
        if(db.routeDao().hasSteps(currentRV.uid) == 0) {
            downloadDirections(currentGroupPosition);
        } else {
            // polyline that can be drawn on map
            directionsPO = StepsParser.decode(db.routeDao().getSteps(currentRV.uid));
            // configure polyline options
            directionsPO.color(Color.GRAY);
            directionsPO.width(15);
            // draw polyline on map
            mMap.addPolyline(directionsPO);
        }

        // draw markers on map, check if something is visited or not
        List<MarkerOptions> markerOpts = drawMarkers();
        // draw  whole on map
        centerRouteOnMap(markerOpts);
    }

    private List<MarkerOptions> drawMarkers() {
        // create new empty list that contains marker options (info about marker)
        List<MarkerOptions> markersOpts = new ArrayList<>();
        // create list of marker positions (long, lat)
        currentMarkers = new ArrayList<>();

        for (Landmark landmark : currentLandmarks) {
            LatLng point = new LatLng(landmark.latitude, landmark.longitude);
            // define marker and all its options from current route
            markersOpts.add(new MarkerOptions()
                    .position(point)
                    .title(landmark.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(
                            landmark.visited ? BitmapDescriptorFactory.HUE_YELLOW : BitmapDescriptorFactory.HUE_AZURE)));
            //  fetch last marker from markerOpts and draw it on mmap
            Marker marker = mMap.addMarker(markersOpts.get(markersOpts.size() - 1));
            // list that contains all markers that are currently shown
            currentMarkers.add(marker);
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
        // what are we going to draw on map
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds2, width, height, padding);
        // Center route after it's been drawn
        mMap.animateCamera(cu);
    }

    private void downloadAllDirections() {
        // fetch all routes from database
        List<Route> routes = db.routeDao().getAll();

        for(int i = 0; i < routes.size(); i++) {
            downloadDirections(i);
        }
    }

    private void updateEVL(int groupPosition, int distance, int duration) {
        RouteView rv = (RouteView)adapter.getGroup(groupPosition);
        // refresh group parameters (distance, duration, visited/all)
        listCategories.get(groupPosition).length = distance;
        listCategories.get(groupPosition).duration = duration;
        listCategories.get(groupPosition).visited = db.landmarkRouteDao().countVisitedLandmarksForRouteId(rv.uid, true);
        listCategories.get(groupPosition).totalLandmarks = db.landmarkRouteDao().countForRouteId(rv.uid);
        // update database with distance and duration
        db.routeDao().updateDistance(rv.uid, distance);
        db.routeDao().updateDuration(rv.uid, duration);
        // refresh expendable list
        elv.deferNotifyDataSetChanged();
    }

    private void downloadDirections(final int groupPosition) {
        final RouteView rv = (RouteView)adapter.getGroup(groupPosition);
        // landmarks of current route that has been selected
        List<Landmark> landmarks;
        if(groupPosition != currentGroupPosition) {
            landmarks = db.landmarkRouteDao().findLandmarksForRouteId(rv.uid);
        } else {
            landmarks = currentLandmarks;
        }

        if (landmarks.size() < 2) {
            throw new IllegalArgumentException();
        }

        Coordinate2String c2s = new Coordinate2String(landmarks);
        // list of landmarks is parsed into get request
        Uri.Builder builder = new Uri.Builder();
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
        // send request and get log of that request
        Log.d(TAG, "Request: " + builder.build().toString());
        // json object that we sand as request
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, builder.build().toString(), null, new Response.Listener<JSONObject>() {
                    // what happens if we get resoponse 201 or 200
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        ParserTask parserTask = new ParserTask(MapsActivity.this);
                        parserTask.execute(response.toString());

                        updateEVL(groupPosition, parserTask.getTotalDistance(), parserTask.getTotalDuration());
                    }
                }, new Response.ErrorListener() {
                    // what happens if we got error response
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: " + error
                                + "\nStatus Code " + error.networkResponse.statusCode
                                + "\nResponse Data " + error.networkResponse.data
                                + "\nCause " + error.getCause()
                                + "\nmessage" + error.getMessage());
                    }
                }) {
            // nwe write our own headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
            // application /json
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        // queue that collects requests
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsObjRequest);
    }
    // what happens if photo has been recognized (listener for recognized photo calls this)
    @Override
    public void photoRecognized(List<Landmark> results) {
        for(Landmark currLandmark : currentLandmarks) {
            for(Landmark result : results) {
                if(currLandmark.cloudLabel.equals(result.cloudLabel)) {
                    // if photo has landmark for certain route, set that landmark as visited
                    setLandmarkVisited(currLandmark);
                    // create Popup that creates photo and info of recognized photo
                    createPopup(currLandmark);
                }
            }
        }

        refreshListCategories();
        adapter.refresh(listCategories, childMap);
        refreshMap();
    }
    // create popUp that contains photo and info
    private void createPopup(Landmark currLandmark) {
        CoordinatorLayout cl = (CoordinatorLayout)findViewById(R.id.infoCoordLayout);
        LinearLayout ll = (LinearLayout)findViewById(R.id.infoLinLayout);

        ImageView iv = (ImageView)findViewById(R.id.infoImageView);
        TextView tv = (TextView)findViewById(R.id.infoTextView);

        //TextView tv2 = (TextView)findViewById(R.id.infoTextView2)

        // fetch data for image and text for recognized landmark
        iv.setImageBitmap(cloudAPI.getBitmap());
        tv.setText(currLandmark.name + " " + currLandmark.message);
        // set visibility for that layout (show popUp)
        cl.setVisibility(CoordinatorLayout.VISIBLE);
        ll.setVisibility(LinearLayout.VISIBLE);
    }

    private void setLandmarkVisited(Landmark currLandmark) {
        // set in database that certain landmark has been visited
        db.landmarkDao().setVisitedById(currLandmark.uid, true);
    }

    private void refreshMap() {
        // clear google map (all markers and routes)
        mMap.clear();
        // connect RouteView with adapter and group (current group and its route)
        RouteView rv = (RouteView)adapter.getGroup(currentGroupPosition);
        // fetch all landmarks that are located on choosen route
        currentLandmarks = db.landmarkRouteDao().findLandmarksForRouteId(rv.uid);
        // addPolyline that represents route steps
        mMap.addPolyline(directionsPO);
        // draw markers for landmarks contained on route
        drawMarkers();
    }

    private void refreshListCategories() {
        // create list of routes that are fetched from database without steps
        List<Route> routes = db.routeDao().getAllWithoutSteps();
        // create new list fo groups
        listCategories = new ArrayList<>();

        for(int i = 0; i < routes.size(); i++) {
            // create RouteView object from fetched database data that doesent contain all steps
            RouteView rv = new RouteView();
            rv.uid = routes.get(i).uid;
            rv.name = routes.get(i).name;
            rv.length = routes.get(i).length;
            rv.duration = routes.get(i).duration;
            // count number of visited landmarks
            rv.visited = db.landmarkRouteDao().countVisitedLandmarksForRouteId(rv.uid, true);
            rv.totalLandmarks = db.landmarkRouteDao().countForRouteId(rv.uid);
            // add routeView element to listCategories
            listCategories.add(rv);
        }

        // for each element in routes (list with routes from db)
        for (int i = 0; i < routes.size(); i++) {
            // create list full of strings that contains landmark names with ids from database
            List<String> landmarkNames = db.landmarkRouteDao().findLandmarkNamesForRouteId(routes.get(i).uid);
            // create childMap which contains landmarks from list
            childMap.put(listCategories.get(i), landmarkNames);
        }
    }

    // function that updates directions
    @Override
    public void updateDirections(PolylineOptions po) {
        // inherit direction Po from new polyline
        directionsPO = po;
        // add new polyline to Google Maps
        mMap.addPolyline(directionsPO);
        // call savePolyline method with polyline argument that as given to updateDirections func
        savePolyline(po);
    }
    // function that saves given polylines to database
    private void savePolyline(PolylineOptions po) {
        if(db.routeDao().hasSteps(currentRV.uid) == 0) {
            // if there are no steps in current route in db, update database wih steps
            db.routeDao().updateSteps(currentRV.uid, StepsParser.encode(po));
        }
    }
}
