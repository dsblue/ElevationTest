package com.camgian;

/**
 * Created by npalmer on 5/22/2015.
 */
public class Coordinate {

    private Double latitude;
    private Double longitude;

    public Coordinate(Double lat, Double lon) {
        latitude = lat;
        longitude = lon;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
