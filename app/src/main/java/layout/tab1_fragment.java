package layout;
import android.location.LocationManager;
import android.os.Handler;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;
import java.util.Timer;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
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

import java.util.HashMap;
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
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    LocationRequest mLocationRequest;


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

    public boolean bool_test = false;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private DatabaseReference mDatabase;
    Boolean on_paused = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1_fragment,container,false);

        // Creating an instance of Firebase data
        mDatabase = FirebaseDatabase.getInstance().getReference();


        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        //Creating new Api Client
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
    public void onPause()
    {
        super.onPause();
        on_paused = true;
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        on_paused = false;
        mGoogleApiClient.reconnect();
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
                //Checking permission
                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Longitude: " + String.valueOf(LocationServices.FusedLocationApi
                            .getLastLocation(mGoogleApiClient).getLongitude()));
                    Log.d(TAG, "Latitude: " + String.valueOf(LocationServices.FusedLocationApi
                            .getLastLocation(mGoogleApiClient).getLatitude()));
                }

            }

            if (mMap == null) {
                return true;
            }
            addNewMarker();

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

        //Update current location
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            MainActivity.cur_latitude = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient).getLatitude();

            MainActivity.cur_longitude = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient).getLongitude();

        }

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        final Handler handler = new Handler();
        final Runnable worker = new Runnable() {
            @Override
            public void run() {
                if (on_paused)
                {
                    return;
                }


                Log.d("qwerpy8wfv", String.valueOf(MainActivity.cur_longitude));
                Log.d("qwerpy8wfv2", String.valueOf(MainActivity.cur_latitude));
                Set<String> set_of_keys =  MainActivity.hashMapTime.keySet();
                try {
                    ArrayList<String> keys_deleted = new ArrayList<String>();
                    for (String key : set_of_keys) {
                        if (MainActivity.hashMapMarker.get(key).getPosition().latitude > MainActivity.cur_latitude + 00.0002000 || MainActivity.hashMapMarker.get(key).getPosition().latitude < MainActivity.cur_latitude - 00.0002000 || MainActivity.hashMapMarker.get(key).getPosition().longitude > MainActivity.cur_longitude + 00.0002000 && MainActivity.hashMapMarker.get(key).getPosition().longitude < MainActivity.cur_longitude - 00.0002000) {
                            //int the_key = MainActivity.list_longitude.lastIndexOf(MainActivity.hashMapMarker.get(key).getPosition().latitude);
                            keys_deleted.add(key);

                            //MainActivity.hashMapMarker.remove(key);


                        }
                    }
                    for (String key : keys_deleted){
                        //Marker m = MainActivity.hashMapMarker.get(key);
                        Log.d("THE KEY:", "FSDAF");

                        Log.d("THE KEY:", "ABC" + MainActivity.hashMapMarker.get(key).getSnippet());
                        Log.d("THE KEY:", "DEF" + MainActivity.hashMapTime.get(key));

                        Marker m = MainActivity.hashMapMarker.get(key);
                        MainActivity.hashMapTime.remove(key);
                        m.remove();
                    }
                }
                catch(Exception e){
                    Log.d("EXCEPTION(deletemrker):", e.getMessage());
                }

//                Cursor res = myDb.getAllData();


                try {

                    mDatabase.getDatabase().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                MarkerData markerData = snapshot.getValue(MarkerData.class);
                                String key = snapshot.getKey();
                                if(markerData != null) {
                                    if (!MainActivity.hashMapTime.containsKey(key) &&
                                            markerData.latitude <= MainActivity.cur_latitude + 00.0002000 && markerData.latitude >= MainActivity.cur_latitude - 00.0002000 && markerData.longitude <= MainActivity.cur_longitude + 00.0002000 && markerData.longitude >= MainActivity.cur_longitude - 00.0002000) {

                                        Log.d("asjflkj32ipf", String.valueOf(markerData.longitude));
                                        Log.d("asjflk2131j32ipf", String.valueOf(markerData.text));

                                        Marker marker = mMap.addMarker(new MarkerOptions().title(markerData.text).position(new LatLng(markerData.latitude, markerData.longitude)).snippet(markerData.text));
                                        MainActivity.hashMapMarker.put(snapshot.getKey(), marker);
                                        MainActivity.hashMapTime.put(snapshot.getKey(), markerData.time);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                catch(Exception e){
                    Log.d("EXCEPTION(addmarker):", e.getMessage());
                }

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
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second



        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }

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

    public void onLocationChanged(Location location) {
        MainActivity.cur_longitude = location.getLatitude();
        MainActivity.cur_longitude = location.getLongitude();

    }



    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        Log.d("qwerpy8wfv", String.valueOf(MainActivity.cur_latitude));

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
//        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            MainActivity.cur_latitude = LocationServices.FusedLocationApi
//                    .getLastLocation(mGoogleApiClient).getLatitude();
//
//            MainActivity.cur_longitude = LocationServices.FusedLocationApi
//                    .getLastLocation(mGoogleApiClient).getLongitude();
//
//        }

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


    private void addNewMarker(){

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
                    MarkerData temp = new MarkerData(input_text, longitude_info, latitude_info, current_time);
                    mDatabase.push().setValue(temp);

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
        Log.d("qwerpy8wfv", String.valueOf(MainActivity.cur_latitude));

        if (mMap == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            MainActivity.cur_latitude = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient).getLatitude();

            MainActivity.cur_longitude = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient).getLongitude();
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