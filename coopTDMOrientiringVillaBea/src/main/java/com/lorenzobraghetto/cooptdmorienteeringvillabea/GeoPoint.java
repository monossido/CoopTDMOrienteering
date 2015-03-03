package com.lorenzobraghetto.cooptdmorienteeringvillabea;

import android.location.Location;

import org.mapsforge.core.model.LatLong;

import java.io.Serializable;

public class GeoPoint implements Serializable {

    private final int img_id;
    private double lat;
    private double lon;
    private String name;
    private String hint;
    private String code;

    public GeoPoint(double lat, double lon, String name, String code, String hint, int img_id) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.code = code;
        this.hint = hint;
        this.img_id = img_id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getName() {
        return name;
    }

    public String getHint() {
        return hint;
    }

    public boolean hasHint() {
        return hint.length() != 0;
    }

    public String getCode() {
        return code;
    }

    public LatLong getLatLon() {
        return new LatLong(lat, lon);
    }

    public Location getLocation() {
        Location temp = new Location("mioProvider");
        temp.setLatitude(lat);
        temp.setLongitude(lon);
        return temp;
    }

    public int getImg_id() {
        return img_id;
    }
}
