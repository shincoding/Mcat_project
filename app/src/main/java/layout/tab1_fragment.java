package layout;
import android.os.Handler;
import java.util.Timer;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shinaegi.mcat.DatabaseHelper;
import com.example.shinaegi.mcat.MainActivity;
import com.example.shinaegi.mcat.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.TimerTask;

public class tab1_fragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    //Database
    DatabaseHelper myDb;




    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private final int mMaxEntries = 5;
    private String[] mLikelyPlaceNames = new String[mMaxEntries];
    private String[] mLikelyPlaceAddresses = new String[mMaxEntries];
    private String[] mLikelyPlaceAttributions = new String[mMaxEntries];
    private LatLng[] mLikelyPlaceLatLngs = new LatLng[mMaxEntries];
    MapView mMapView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1_fragment,container,false);
        //database!!
        myDb = new DatabaseHelper(getActivity());


        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */,
                         tab1_fragment.this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(tab1_fragment.this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Handles a click on the menu option to get a place.
     *
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            if (mLastKnownLocation != null) {
                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Longitude: " + String.valueOf(LocationServices.FusedLocationApi
                            .getLastLocation(mGoogleApiClient).getLongitude()));
                    Log.d(TAG, "Latitude: " + String.valueOf(LocationServices.FusedLocationApi
                            .getLastLocation(mGoogleApiClient).getLatitude()));
                }

            }

            showCurrentPlace();

        }
        return true;

    }
    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {

                return true;
            }

        });


        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        final Handler handler = new Handler();
        final Runnable worker = new Runnable() {
            @Override
            public void run() {
                Log.d("haha", "abcdef123");

                Cursor res = myDb.getAllData();

                if (res.getCount() != 0) {
                    while (res.moveToNext()) {

                        if (!MainActivity.list_latitude.contains(res.getDouble(res.getColumnIndex("longitude"))) || !MainActivity.list_longitude.contains(res.getDouble(res.getColumnIndex("latitude"))) || MainActivity.list_latitude.indexOf(res.getDouble(res.getColumnIndex("longitude"))) != MainActivity.list_longitude.indexOf(res.getDouble(res.getColumnIndex("latitude")))) {
                            Log.d("hehehe1", res.getString(0));
                            Log.d("hehehe2", res.getString(1));
                            Log.d("hehehe3", res.getString(2));
                            MainActivity.list_times.add(res.getString(4));
                            MainActivity.list_messages.add(res.getString(0));
                            Marker marker = mMap.addMarker(new MarkerOptions().title(res.getString(1)).position(new LatLng(res.getDouble(res.getColumnIndex("latitude")), res.getDouble(res.getColumnIndex("longitude")))).snippet(""));

                            MainActivity.list_latitude.add(res.getDouble(res.getColumnIndex("longitude")));
                            MainActivity.list_longitude.add(res.getDouble(res.getColumnIndex("latitude")));
                        }

                    }
                }

                res.moveToFirst();
                res.close();
                handler.postDelayed(this, 10000);

            }
        };
        handler.post(worker);

    }
    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        SupportMapFragment mapFragment = (SupportMapFragment)this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }





    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

    }
    public boolean onMarkerClick(final Marker marker) {
        Log.i("GoogleMapActivity", "onMarkerClick");
        return false;
    }
    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */


    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }
        openMessageDialog();
    }

    private void openMessageDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("MESSAGE");
        dialog.setMessage("Type in message");
        final EditText input = new EditText(getActivity());
        dialog.setView(input);
        dialog.setPositiveButton("Add",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialogInterface, int which){
                String input_text = String.valueOf(input.getText());

                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    MainActivity.cur_latitude = LocationServices.FusedLocationApi
                            .getLastLocation(mGoogleApiClient).getLatitude();

                    MainActivity.cur_longitude = LocationServices.FusedLocationApi
                            .getLastLocation(mGoogleApiClient).getLongitude();

                    Double longitude_info = LocationServices.FusedLocationApi
                            .getLastLocation(mGoogleApiClient).getLongitude();
                    Double latitude_info = LocationServices.FusedLocationApi
                            .getLastLocation(mGoogleApiClient).getLatitude();

                    Calendar calen = Calendar.getInstance();
                    String current_time = String.valueOf(calen.get(calen.YEAR)) + "/"
                    + String.valueOf(calen.get(calen.MONTH)+1) + "/"
                    + String.valueOf(calen.get(calen.DAY_OF_MONTH))
                    + "/" + String.valueOf(calen.get(calen.HOUR_OF_DAY))
                    + "/" + String.valueOf(calen.get(calen.MINUTE))
                    + "/" + String.valueOf(calen.get(calen.SECOND));
                    Log.d("hmhmhm", current_time);
                    myDb.insertData(input_text,longitude_info , latitude_info, current_time
                    );
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    mMap.addMarker(new MarkerOptions().title(input_text).position(new LatLng(latitude_info, longitude_info)).snippet(""));

                    Cursor res = myDb.getAllData();
                    if (res.getCount() == 0) {
                        builder.setCancelable(true);
                        builder.setTitle("DATA");
                        builder.setMessage("nop");
                        builder.show();
                    }



                    StringBuffer buffer = new StringBuffer();
                    while (res.moveToNext()) {
                        buffer.append("ID" + res.getString(0) + "\n");
                        buffer.append("Name" + res.getString(1) + "\n");
                        buffer.append("Surname" + res.getString(2) + "\n");
                        buffer.append("Marks" + res.getString(3) + "\n");
                    }

                    builder.setCancelable(true);
                    builder.setTitle("DATA");
                    builder.setMessage(buffer.toString());
                    builder.show();


                }
            }
        });
        dialog.setNegativeButton("Close", null);
        dialog.show();

    }



    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }
}