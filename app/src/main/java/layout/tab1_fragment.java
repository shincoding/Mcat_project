package layout;
import android.os.Handler;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import com.example.shinaegi.mcat.TempData;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
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
import android.widget.EditText;

import java.util.Calendar;

import com.example.shinaegi.mcat.MainActivity;
import com.example.shinaegi.mcat.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class tab1_fragment extends MapConfiguration {


    private FirebaseObject firebaseObj = new FirebaseObject();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1_fragment,container,false);
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


    /**
     * Handles a click on the menu option to get a place.
     *
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            if (mMap == null) {
                return true;
            }
            // To add a new Marker.
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

        super.onMapReady(map);
        // no need to add another callback because onMapReady would be called whether the app
        // is resumed (mGoogleApiClient.reconnect();)
        //firebaseObj.recalibrate();
        //firebaseObj.setMarkers(mMap);
        // Repeat on an interval
        final Handler handler = new Handler();
        final Runnable worker = new Runnable() {
            @Override
            public void run() {
                if (!on_paused)
                {
                    try {
                        firebaseObj.removeMarkers();
                    }
                    catch(Exception e){
                        Log.d("EXCEPTION(deletemrker):", e.getMessage());
                    }
                    try {
                        // @TODO Find why the following codes are necessary to be put in a runnable.
                        // there would be no need to re-call event listener every time frame.
                        // One hypothesis would be that recalibration is needed.

                        firebaseObj.recalibrate();
                        firebaseObj.setMarkers(mMap);
                    }
                    catch(Exception e){
                        Log.d("EXCEPTION(addmarker):", e.getMessage());
                    }

                    handler.postDelayed(this, 10000);
                }
            }
        };
        handler.post(worker);

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

                    firebaseObj.saveData(mGoogleApiClient, input_text);

                }
            }
        });
        dialog.setNegativeButton("Close", null);
        dialog.show();

    }




}