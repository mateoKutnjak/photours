package com.example.mateo.photours;

import android.app.Application;

public class Global extends Application {

    public static final int ZERO = 0;
    public static final int ONE = 1;
//dddq
    public static final String CLOUD_VISION_API_KEY = "AIzaSyCh-7eNuvSOLM4qFnV5Z3z_UCBM25Mwx0s";
    public static final String SERVER_KEY = "AIzaSyCZypjPER2u8bHI65uv73DHE4B-rUoztpU";

    public static final String FILE_NAME = "temp.jpg";
    public static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    public static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    public static final int REQUEST_LOCATION = 1;
    public static final String TRAVEL_MODE_WALKING = "walking";

    public static final double MAP_ROUTE_FOCUS_BOTTOM_PADDING = 0.8;
    public static final double MAP_ROUTE_FOCUS_TOP_PADDING = 0.1;

    public static final int CLOUD_NUMBER_OF_RESULTS = 5;

    public static final String RESPONSE_NOT_RECOGNIZED = "Not recognized";

    public static final int ERROR_NO_ERROR = 0;
    public static final int ERROR_API_REQUEST = 1;
    public static final int ERROR_LANDMARK_NOT_RECOGNIZED = 2;
    public static final int ERROR_TIMEOUT_EXPIRED = 3;

    private static final String DB_NAME ="photoursDB";
    private static String DB_PATH = "";


    public static final String LIST_HEADER_TEXT_ROUTES = "Routes";
    public static final CharSequence LIST_HEADER_TEXT_HIDE_ROUTES = "Hide routes";
    public static final CharSequence LIST_HEADER_TEXT_SHOW_ROUTES = "Show routes";
}
