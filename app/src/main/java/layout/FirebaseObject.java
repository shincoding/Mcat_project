package layout;

/**
 * Created by shinaegi on 2017-12-30.
 */

import com.example.shinaegi.mcat.MainActivity;
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
}
