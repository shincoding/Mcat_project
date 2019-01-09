package layout;

/**
 * Created by shinaegi on 2017-12-30.
 */

import android.util.Log;

import com.example.shinaegi.mcat.MainActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.shinaegi.mcat.TempData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

public class FirebaseObject {

    private DatabaseReference mDatabase;

    public FirebaseObject() {
        recalibrate();
    }

    public void recalibrate(){
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void setMarkers(GoogleMap map){

        final GoogleMap googleMap = map;
        mDatabase.getDatabase().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*

                    @TODO implement the function using the following procedure:

                    Run a Firebase query to select data that are above the current time and
                    within the latitude and longitude bound.

                    Run it first with all time period for initialization purpose.


                    What about markers that have been deleted? Keep track of deleted markers by
                    augmented tree with:
                    1. country
                    2. city
                    3. district


                 */

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //add marker in the screen.
                    MarkerData markerData = snapshot.getValue(MarkerData.class);
                    String key = snapshot.getKey();
                    if(markerData != null) {
                        if (!TempData.hashMapTime.containsKey(key) &&
                                markerData.latitude <= TempData.cur_latitude + 00.0002000 && markerData.latitude >= TempData.cur_latitude - 00.0002000 && markerData.longitude <= TempData.cur_longitude + 00.0002000 && markerData.longitude >= TempData.cur_longitude - 00.0002000) {

                            Marker marker = googleMap.addMarker(new MarkerOptions().title(markerData.text).position(new LatLng(markerData.latitude, markerData.longitude)).snippet(markerData.text));
                            TempData.hashMapMarker.put(snapshot.getKey(), marker);
                            TempData.hashMapTime.put(snapshot.getKey(), markerData.time);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void addBackMarkers() {
        /*
            @TODO implement the function using the following procedure:

            Traditional method: transversing through all teh deleted markers.
            This would be a linear time complexity.



            Better solution:

              Keep track of an augmented tree, where each node would either be:

                1. country
                2. city
                3. district

              Keep track of a dictionary for country, city, and district.


              From the list of markers that are deleted, transverse down the augmented tree
              (country -> city -> district)

              For the specified district node, transverse through its children and compare with the
              current location radius.

              Why do this?
                    Right now, the current place is specific to the user's GPS position.
                    However, in the future, there may be features where users can share their
                    locations for others to see. Thus, having a more flexible solution would
                    improve the system. If some deleted nodes are in a different continent,
                    do not need to transverse through them

         */
    }

    public void removeMarkers(){

        /*

            @TODO implement the function using the following procedure:

            When removing markers, we need to store them into a data structure.
            Otherwise, we will not be able to retrieve those marker information as
            "update functions" will check only recently-added markers.

            Implementation:

              Keep track of an augmented tree, where each node would either be:

                1. country
                2. city
                3. district

              Keep track of a dictionary for country, city, and district.

              When removing a marker, first check its geographical information using OpenStreetMap.
              Then, go through its location of country, city, and district,
              and insert the location as a node under its corresponding district.
              Note that if any of its geographical info does not exist in the data structures,
              insert them first.


         */
        Set<String> set_of_keys =  TempData.hashMapTime.keySet();

        ArrayList<String> keys_deleted = new ArrayList<String>();
        for (String key : set_of_keys) {
            if (TempData.hashMapMarker.get(key).getPosition().latitude > TempData.cur_latitude + 00.0002000 || TempData.hashMapMarker.get(key).getPosition().latitude < TempData.cur_latitude - 00.0002000 || TempData.hashMapMarker.get(key).getPosition().longitude > TempData.cur_longitude + 00.0002000 && TempData.hashMapMarker.get(key).getPosition().longitude < TempData.cur_longitude - 00.0002000) {
                keys_deleted.add(key);
            }
        }
        for (String key : keys_deleted){
            //remove location marker.
            Log.d("THE KEY:", "ABC" + TempData.hashMapMarker.get(key).getSnippet());
            Log.d("THE KEY:", "DEF" + TempData.hashMapTime.get(key));

            Marker m = TempData.hashMapMarker.get(key);
            TempData.hashMapTime.remove(key);
            m.remove();
        }

    }

    public void saveData(GoogleApiClient googleAPiClient, String input_text){
        Double longitude_info = LocationServices.FusedLocationApi
                .getLastLocation(googleAPiClient).getLongitude();
        Double latitude_info = LocationServices.FusedLocationApi
                .getLastLocation(googleAPiClient).getLatitude();

        Calendar calen = Calendar.getInstance();
        String current_time = String.valueOf(calen.get(calen.YEAR)) + "-"
                + String.valueOf(calen.get(calen.MONTH)+1) + "-"
                + String.valueOf(calen.get(calen.DAY_OF_MONTH))
                + " " + String.valueOf(calen.get(calen.HOUR_OF_DAY))
                + ":" + String.valueOf(calen.get(calen.MINUTE))
                + ":" + String.valueOf(calen.get(calen.SECOND));
        MarkerData temp = new MarkerData(input_text, longitude_info, latitude_info, current_time);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.push().setValue(temp);
    }
}
