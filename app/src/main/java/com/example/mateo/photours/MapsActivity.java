package com.example.mateo.photours;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.location.Location;
import android.location.LocationManager;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Global.REQUEST_LOCATION);

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
        listView = (ListView)findViewById(R.id.list);

        TypedArray allRoutes = getResources().obtainTypedArray(R.array.allRoutes);
        TypedArray route = null;

        List<String> routeNames = new ArrayList<>();

        for (int i = 0; i < allRoutes.length(); i++) {
            int id = allRoutes.getResourceId(i, Global.ZERO);

            if (id > 0) {
                route = getResources().obtainTypedArray(id);
                routeNames.add(route.getString(Global.ROUTE_NAME_INDEX));
                route.recycle();
            }
        }
        allRoutes.recycle();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, routeNames.toArray(new String[routeNames.size()]));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?>adapter, View v, int position, long id) {
                drawRoute(position);
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
                            this, R.raw.map_style_retro_labels));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }

        drawRoute(Global.ZERO);
    }

    public void drawRoute(int positionClicked) {
        mMap.clear();

        String url = "https://maps.googleapis.com/maps/api/directions/json";

//        Uri.Builder builder = new Uri.Builder();
//        builder.scheme("https")
//                .authority("maps.googleapis.com")
//                .appendPath("maps")
//                .appendPath("api")
//                .appendPath("directions")
//                .appendPath("json")
//                .appendQueryParameter("origin", "Toronto")
//                .appendQueryParameter("destination", "Montreal")
//                .appendQueryParameter("key", "AIzaSyCZypjPER2u8bHI65uv73DHE4B-rUoztpU")
//                .appendQueryParameter("sensor", "true");
//
//        Log.d("a", "Request: " + builder.build().toString());
//
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (Request.Method.GET, builder.build().toString(), null, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("a", "Response: " + response.toString());
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("a", "Error: " + error
//                                + "\nStatus Code " + error.networkResponse.statusCode
//                                + "\nResponse Data " + error.networkResponse.data
//                                + "\nCause " + error.getCause()
//                                + "\nmessage" + error.getMessage());
//                    }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String,String>();
//                //headers.put("Content-Type", "application/json; charset=utf-8");
//                return headers;
//            }
//            @Override
//            public String getBodyContentType() {
//                return "application/json";
//            }
//        };
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//        queue.add(jsObjRequest);


        TypedArray allRoutes = getResources().obtainTypedArray(R.array.allRoutes);

        int id = allRoutes.getResourceId(positionClicked, Global.ZERO);
        TypedArray route = getResources().obtainTypedArray(id);
        allRoutes.recycle();

        TypedArray landmark = null;

//        int originID = route.getResourceId(Global.ZERO, Integer.MAX_VALUE);
//        int destinationID = route.getResourceId(route.length()-1, Integer.MAX_VALUE);
//
//        TypedArray originTA = route.getResources().obtainTypedArray(originID);
//        TypedArray destinationTA = route.getResources().obtainTypedArray(destinationID);
//
//        LatLng origin = new LatLng(originTA.getFloat(1, 0), originTA.getFloat(2, 0));
//        LatLng destination = new LatLng(destinationTA.getFloat(1, 0), destinationTA.getFloat(2, 0));
//        GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
//                .from(origin)
//                .to(destination)
//                .transportMode(TransportMode.WALKING)
//                .execute(new DirectionCallback() {
//                    @Override
//                    public void onDirectionSuccess(Direction direction, String rawBody) {
//                        // Do something here
//                    }
//
//                    @Override
//                    public void onDirectionFailure(Throwable t) {
//                        // Do something here
//                    }
//                });

        for (int i = 0; i < route.length(); i++) {
            int landmarkID = route.getResourceId(i, Global.ZERO);

            if (landmarkID > 0) {
                landmark = getResources().obtainTypedArray(landmarkID);

                String name = landmark.getString(Global.LANDMARK_NAME_INDEX);
                double latitude = landmark.getFloat(Global.LANDMARK_LATITUDE_INDEX, Global.ZERO);
                double longitude = landmark.getFloat(Global.LANDMARK_LONGITUDE_INDEX, Global.ZERO);

                LatLng point = new LatLng(latitude, longitude);

                mMap.addMarker(new MarkerOptions()
                        .position(point)
                        .title(name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                if(i == Global.ONE) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                }

                landmark.recycle();
            }
        }
        route.recycle();
    }
}
