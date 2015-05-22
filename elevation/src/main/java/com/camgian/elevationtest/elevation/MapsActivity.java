package com.camgian.elevationtest.elevation;

import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;
import com.camgian.Coordinate;
import com.camgian.DownloadService;
import com.camgian.ElevationManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;

public class MapsActivity extends FragmentActivity implements OnMapClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private static final String TAG = "MapsActivity";

    private FragmentActivity context;
    private ElevationManager elevationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        //testDownload();
        //new ElevationManager(this).getElevation(new Coordinate(0.0,0.0));

        elevationManager = new ElevationManager(this);

        mMap.setOnMapClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        LatLng sydney = new LatLng(-33.867, 151.206);

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        mMap.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));

    }

    void testDownload () {
        DownloadReceiver receiver = new DownloadReceiver(new Handler());

        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("data.worldwind.arc.nasa.gov")
                    .appendPath("wms")
                    .appendQueryParameter("service", "wms")
                    .appendQueryParameter("request", "GetCapabilities");

            String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test1.txt";

            DownloadService.startDownload(this, receiver, builder.build().toString(), filename);

            builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("data.worldwind.arc.nasa.gov")
                    .appendPath("wms")
                    .appendQueryParameter("service", "wms")
                    .appendQueryParameter("request", "GetMap")
                    .appendQueryParameter("version", "1.3.0")
                    .appendQueryParameter("crs", "CRS:84")
                    .appendQueryParameter("layers", "bmng200407")
                    .appendQueryParameter("styles", "")
                    .appendQueryParameter("width", "512")
                    .appendQueryParameter("height", "512")
                    .appendQueryParameter("format", "image/jpeg")
                    .appendQueryParameter("transparent", "TRUE")
                    .appendQueryParameter("bgcolor", "0x000000")
                    .appendQueryParameter("bbox", "162.0,-18.0,180.0,0.0");

            filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.jpeg";

            DownloadService.startDownload(this, receiver, builder.build().toString(), filename);

            builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("data.worldwind.arc.nasa.gov")
                    .appendPath("elev")
                    .appendQueryParameter("service", "wms")
                    .appendQueryParameter("request", "GetMap")
                    .appendQueryParameter("version", "1.3.0")
                    .appendQueryParameter("crs", "CRS:84")
                    .appendQueryParameter("layers", "mergedSrtm")
                    .appendQueryParameter("styles", "")
                    .appendQueryParameter("width", "512")
                    .appendQueryParameter("height", "512")
                    .appendQueryParameter("format", "application/bil16")
                    //.appendQueryParameter("bbox", "162.0,-18.0,180.0,0.0")
                    .appendQueryParameter("bbox", "100.0,-100.0,190.0,-40.0");

            filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/elevation.bin";

            DownloadService.startDownload(this, receiver, builder.build().toString(), filename);


        } catch (Exception e) {

        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Toast toast = Toast.makeText(this, latLng.toString(), Toast.LENGTH_SHORT);
        toast.show();

        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        Log.v(TAG, bounds.toString());


        elevationManager.getElevation(latLng.latitude, latLng.longitude);
    }

    /**
     *
     */
    private class DownloadReceiver extends ResultReceiver {
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            String filename = resultData.getString("filename");
            if (resultCode == DownloadService.DOWNLOAD_COMPLETE_ERROR) {
                Toast toast = Toast.makeText(context, filename, Toast.LENGTH_SHORT);
                toast.show();
            }
            if (resultCode == DownloadService.DOWNLOAD_COMPLETE_OK) {
                Toast toast = Toast.makeText(context, filename, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
