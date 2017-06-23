package layout;

/**
 * Created by shinaegi on 2017-06-23.
 */

public class MarkerData {

    public String text;
    public Double longitude;
    public Double latitude;
    public String time;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public MarkerData() {
    }

    public MarkerData(String text, Double longitude, Double latitude, String time) {
        this.text = text;
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
    }
}

