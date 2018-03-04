package com.example.mateo.photours;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ExpandableListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mateo.photours.adapters.ELVAdapter;
import com.example.mateo.photours.async.ParserTask;
import com.example.mateo.photours.database.AppDatabase;
import com.example.mateo.photours.database.DatabaseInitializer;
import com.example.mateo.photours.database.entities.Landmark;
import com.example.mateo.photours.database.entities.Route;
import com.example.mateo.photours.database.views.RouteView;
import com.example.mateo.photours.util.Coordinate2String;
import com.example.mateo.photours.util.JSONParser;
import com.example.mateo.photours.util.PermissionUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback{

    private static final String TAG = "MapsActivity";

    private AppDatabase db;

    private GoogleMap mMap;

    private ExpandableListView elv;
    private ELVAdapter adapter;
    private List<RouteView> listCategories;
    private Map<RouteView, List<String>> childMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Global.REQUEST_LOCATION);

        initDatabase();

        initELV();
        fillELV();

        addFloatingActionButton();
        addMapFragment();
    }

    private void initDatabase() {
        db = AppDatabase.getAppDatabase(getApplicationContext());
        DatabaseInitializer.populateSync(db);
    }

    private void addMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void addFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera();
            }
        });
    }

    private void initELV() {
        elv = (ExpandableListView) findViewById(R.id.expList);
        listCategories = db.routeDao().getAllWithoutSteps();
        childMap = new HashMap<>();
    }

    private void fillELV() {
        List<Route> routes = db.routeDao().getAll();

        for (int i = 0; i < routes.size(); i++) {
            List<String> landmarkNames = db.landmarkRouteDao().findLandmarkNamesForRouteId(routes.get(i).uid);
            childMap.put(listCategories.get(i), landmarkNames);
        }

        adapter = new ELVAdapter(listCategories, childMap, this);
        elv.setAdapter(adapter);

        elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                drawRoute(groupPosition);
                return false;
            }
        });

        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Landmark landmark = db.landmarkDao().findByName((String) adapter.getChild(groupPosition, childPosition));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(landmark.latitude, landmark.longitude)));
                return false;
            }
        });
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                Global.CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, Global.CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, Global.FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Global.CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            startActivity(new Intent(this, PhotoActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Global.CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, Global.CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
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
        drawRoute(Global.ZERO);

        downloadAllDirections();
        elv.deferNotifyDataSetChanged();
    }

    private void styleMap(int map_style) {
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style_aubergine_labels));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style.", e);
        }
    }

    private void drawRoute(int groupPosition) {
        mMap.clear();

        RouteView rv = (RouteView)adapter.getGroup(groupPosition);
        Route route = db.routeDao().findByName(rv.name);
        List<Landmark> landmarks = db.landmarkRouteDao().findLandmarksForRouteId(route.uid);

        downloadDirections(route, landmarks, groupPosition, true);
        List<MarkerOptions> markers = drawMarkers(landmarks);
        centerRouteOnMap(markers);
    }

    private List<MarkerOptions> drawMarkers(List<Landmark> landmarks) {
        List<MarkerOptions> markers = new ArrayList<>();

        for (Landmark landmark : landmarks) {
            LatLng point = new LatLng(landmark.latitude, landmark.longitude);

            markers.add(new MarkerOptions()
                    .position(point)
                    .title(landmark.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            mMap.addMarker(markers.get(markers.size() - 1));
        }
        return markers;
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

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);
    }

    private void downloadAllDirections() {
        List<Route> routes = db.routeDao().getAll();

        for(int i = 0; i < routes.size(); i++) {
            List<Landmark> landmarks = db.landmarkRouteDao().findLandmarksForRouteId(routes.get(i).uid);

            downloadDirections(routes.get(i), landmarks, i, false);
        }
    }

    private void updateEVL(int groupPosition) {
        listCategories.get(groupPosition).length = db.routeDao().findByName(((RouteView)adapter.getGroup(groupPosition)).name).length;
        listCategories.get(groupPosition).duration = db.routeDao().findByName(((RouteView)adapter.getGroup(groupPosition)).name).duration;
        elv.deferNotifyDataSetChanged();
    }

    private void downloadDirections(final Route route, List<Landmark> landmarks, final int groupPosition, final boolean draw) {
        if (landmarks.size() < 2) {
            throw new IllegalArgumentException();
        }

        Coordinate2String c2s = new Coordinate2String(landmarks);

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

        Log.d(TAG, "Request: " + builder.build().toString());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, builder.build().toString(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        Pair<Integer, Integer> disDur = JSONParser.getDistanceDurationFromDirections(response);
                        db.routeDao().updateDistance(route.uid, (double) disDur.first / 1000);
                        db.routeDao().updateDuration(route.uid, disDur.second);

                        updateEVL(groupPosition);

                        if(draw) {
                            ParserTask parserTask = new ParserTask(mMap);
                            parserTask.execute(response.toString());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: " + error
                                + "\nStatus Code " + error.networkResponse.statusCode
                                + "\nResponse Data " + error.networkResponse.data
                                + "\nCause " + error.getCause()
                                + "\nmessage" + error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsObjRequest);
    }
}
