package com.example.mateo.photours;

import android.app.Application;
import android.os.Environment;

import java.io.File;

/**
 * Created by mateo on 25/02/2018.
 */

public class Global extends Application {

    public static final int ZERO = 0;
    public static final int ONE = 1;

    public static final String CLOUD_VISION_API_KEY = "AIzaSyCh-7eNuvSOLM4qFnV5Z3z_UCBM25Mwx0s";
    public static final String FILE_NAME = "temp.jpg";
    public static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    public static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    public static final int LANDMARK_NAME_INDEX = 0;
    public static final int LANDMARK_LATITUDE_INDEX = 1;
    public static final int LANDMARK_LONGITUDE_INDEX = 2;

    public static final int ROUTE_NAME_INDEX = 0;
}
