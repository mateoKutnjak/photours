package com.example.mateo.photours.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.example.mateo.photours.Global;
import com.example.mateo.photours.database.entities.Landmark;
import com.example.mateo.photours.util.PackageManagerUtils;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CloudAPI {

    private final static String TAG = "CloudAPI";

    private Uri photoUri;
    private Context context;
    private List<Landmark> results;
    private PhotoRecognitionListener listener;
    private Bitmap photoBitmap;
    private int errorCode;

    public CloudAPI(Context context, PhotoRecognitionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void init() {
        photoUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", getCameraFile());

        if (photoUri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri),
                                1200);

                callCloudVision(bitmap);
                //bitmap = setImage(getCameraFile().getPath(), 1200, 1200);
                photoBitmap = setImage(getCameraFile().getPath(), 1200, 1200);
                //photoBitmap = setImage(MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri), 1200, 1200);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
        }
    }

    public Bitmap setImage(String path, final int targetWidth, final int targetHeight) {
        Bitmap bitmap = null;
        int orientation_val = 0;
// Get exif orientation
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            if (orientation == 6) {
                orientation_val = 90;
            }
            else if (orientation == 3) {
                orientation_val = 180;
            }
            else if (orientation == 8) {
                orientation_val = 270;
            }
        }
        catch (Exception e) {
        }

        try {
// First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

// Adjust extents
            int sourceWidth, sourceHeight;
            if (orientation_val == 90 || orientation_val == 270) {
                sourceWidth = options.outHeight;
                sourceHeight = options.outWidth;
            } else {
                sourceWidth = options.outWidth;
                sourceHeight = options.outHeight;
            }

// Calculate the maximum required scaling ratio if required and load the bitmap
            if (sourceWidth > targetWidth || sourceHeight > targetHeight) {
                float widthRatio = (float)sourceWidth / (float)targetWidth;
                float heightRatio = (float)sourceHeight / (float)targetHeight;
                float maxRatio = Math.max(widthRatio, heightRatio);
                options.inJustDecodeBounds = false;
                options.inSampleSize = (int)maxRatio;
                bitmap = BitmapFactory.decodeFile(path, options);
            } else {
                bitmap = BitmapFactory.decodeFile(path);
            }

// Rotate the bitmap if required
            if (orientation_val > 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation_val);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }

// Re-scale the bitmap if necessary
            sourceWidth = bitmap.getWidth();
            sourceHeight = bitmap.getHeight();
            if (sourceWidth != targetWidth || sourceHeight != targetHeight) {
                float widthRatio = (float)sourceWidth / (float)targetWidth;
                float heightRatio = (float)sourceHeight / (float)targetHeight;
                float maxRatio = Math.max(widthRatio, heightRatio);
                sourceWidth = (int)((float)sourceWidth / maxRatio);
                sourceHeight = (int)((float)sourceHeight / maxRatio);
                bitmap = Bitmap.createScaledBitmap(bitmap, sourceWidth,     sourceHeight, true);
            }
        } catch (Exception e) {
        }
        return bitmap;
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {

            AsyncTask at = this;

            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(Global.CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = context.getPackageName();
                                    visionRequest.getRequestHeaders().set(Global.ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(context.getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(Global.ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature feature = new Feature();
                            feature.setType("LANDMARK_DETECTION");
                            feature.setMaxResults(10);
                            add(feature);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();

                    errorCode = Global.ERROR_NO_ERROR;
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                errorCode = Global.ERROR_API_REQUEST;
                return "Cloud Vision API request failed. Check logs for details.";
            }

            @Override
            protected void onPreExecute() {
                new CountDownTimer(10000, 10000) {
                    public void onTick(long millisUntilFinished) {
                        // You can monitor the progress here as well by changing the onTick() time
                    }

                    public void onFinish() {
                        // stop async task if not in progress
                        if (at.getStatus() == AsyncTask.Status.RUNNING) {
                            at.cancel(false);
                        }
                    }
                }.start();
            }

            protected void onPostExecute(String result) {
                Log.d(TAG, "Response:" + result);

                if(result.equals(Global.RESPONSE_NOT_RECOGNIZED)) {
                    errorCode = Global.ERROR_LANDMARK_NOT_RECOGNIZED;
                }
                listener.photoRecognized(results, errorCode);
            }

            @Override
            protected void onCancelled(String response) {
                errorCode = Global.ERROR_TIMEOUT_EXPIRED;
                listener.photoRecognized(null, errorCode);
                Log.d(TAG, "Response cancelled:" + response);
            }
        }.execute();
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "";

        List<EntityAnnotation> labels = response.getResponses().get(0).getLandmarkAnnotations();
        results = new ArrayList<>();

        if (labels != null) {
            for(int i = 0; i < labels.size(); i ++) {
                Landmark landmark = new Landmark();

                landmark.cloudLabel = labels.get(i).getDescription();
                landmark.latitude = labels.get(i).getLocations().get(0).getLatLng().getLatitude();
                landmark.longitude = labels.get(i).getLocations().get(0).getLatLng().getLongitude();

                results.add(landmark);
                message = String.format("%s\n", labels.get(i).getDescription());
            }
        } else {
            message += "Not recognized";
        }

        return message;
    }

    private File getCameraFile() {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, Global.FILE_NAME);
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    public List<Landmark> getResults() {
        return results;
    }

    public Bitmap getBitmap() {
        return photoBitmap;
    }
}
