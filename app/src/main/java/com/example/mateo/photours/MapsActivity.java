package com.example.mateo.photours;

import android.Manifest;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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

        drawRoute(Global.ZERO);
    }

    public void drawRoute(int positionClicked) {
        mMap.clear();

        TypedArray allRoutes = getResources().obtainTypedArray(R.array.allRoutes);

        int id = allRoutes.getResourceId(positionClicked, Global.ZERO);
        TypedArray route = getResources().obtainTypedArray(id);
        allRoutes.recycle();

        TypedArray landmark = null;

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
                        .title(name));

                if(i == Global.ONE) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                }

                landmark.recycle();
            }
        }
        route.recycle();
    }
}
