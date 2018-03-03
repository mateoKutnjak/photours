package com.example.mateo.photours;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mateo.photours.adapters.ELVAdapter;
import com.example.mateo.photours.database.AppDatabase;
import com.example.mateo.photours.database.DatabaseInitializer;
import com.example.mateo.photours.database.entities.Landmark;
import com.example.mateo.photours.database.entities.Route;
import com.example.mateo.photours.util.Coordinate2String;
import com.example.mateo.photours.util.DirectionsJSONParser;
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
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";

    private AppDatabase db;

    private GoogleMap mMap;
    private ExpandableListView elv;
    private ELVAdapter adapter;
    private List<String> listCategories;
    private Map<String, List<String>> childMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Global.REQUEST_LOCATION);

        db = AppDatabase.getAppDatabase(getApplicationContext());
        DatabaseInitializer.populateSync(db);

        addMapFragment();
        fillRouteList();
        addFloatingActionButton();
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

    private void fillRouteList() {
        elv = (ExpandableListView) findViewById(R.id.expList);
        listCategories = new ArrayList<>();
        childMap = new HashMap<>();

        List<Route> routes = db.routeDao().getAll();
        listCategories = db.routeDao().getAllNames();

        for(int i = 0; i < routes.size(); i++) {
            List<Landmark> landmarks = db.landmarkRouteDao().findLandmarksForRouteId(routes.get(i).uid);
            List<String> landmarkNames = new ArrayList<>();

            for(Landmark landmark : landmarks) {
                landmarkNames.add(landmark.name);
            }

            childMap.put(listCategories.get(i), landmarkNames);
        }

        adapter = new ELVAdapter(listCategories, childMap, this);
        elv.setAdapter(adapter);

        elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                drawRoute((String) adapter.getGroup(groupPosition));
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
        } else {
            Toast.makeText(MapsActivity.this, "You have to accept to enjoy all app's services!", Toast.LENGTH_LONG).show();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }

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

        drawRoute((String) adapter.getGroup(Global.ZERO));
    }

    public void drawRoute(String routeName) {
        mMap.clear();

        Route route = db.routeDao().findByName(routeName);
        List<Landmark> landmarks = db.landmarkRouteDao().findLandmarksForRouteId(route.uid);

        downloadDirections(route, landmarks);

        List<MarkerOptions> markers = new ArrayList<>();

        for(Landmark landmark : landmarks) {
            LatLng point = new LatLng(landmark.latitude, landmark.longitude);

            markers.add(new MarkerOptions()
                    .position(point)
                    .title(landmark.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            mMap.addMarker(markers.get(markers.size()-1));
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);
    }

    private void downloadDirections(final Route route, List<Landmark> landmarks) {
        if(landmarks.size() < 2) {
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
                        db.routeDao().updateDistance(route.uid, (double)disDur.first / 1000);

                        ParserTask parserTask = new ParserTask();
                        parserTask.execute(response.toString());
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
                HashMap<String, String> headers = new HashMap<String,String>();
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

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(15);
                lineOptions.color(Color.GRAY);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }
}
