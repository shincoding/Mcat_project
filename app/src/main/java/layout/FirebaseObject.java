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

    public void removeMarkers(){
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
