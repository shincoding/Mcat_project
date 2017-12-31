package com.example.shinaegi.mcat;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shinaegi on 2017-12-30.
 */

public class TempData {

    public static ArrayList<Double> list_longitude = new ArrayList<Double>();
    public static ArrayList<Double> list_latitude = new ArrayList<Double>();
    public static ArrayList<String> list_messages = new ArrayList<String>();
    public static ArrayList<String> list_times = new ArrayList<String>();
    public static Double cur_longitude;
    public static Double cur_latitude;
    public static HashMap<String,Marker> hashMapMarker = new HashMap<>();
    public static HashMap<String,String> hashMapTime = new HashMap<>();

    public TempData(){

    }
}
