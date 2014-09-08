package com.misabelleeli.pacers_bikeshare;

import java.io.Serializable;

/**
 * Created by Lee on 2014-07-14.
 */
public class Station implements Comparable<Station>, Serializable {
    private double latitude;
    private double longitude;
    private String name;
    private String address;
    private int bikes;
    private int docks;
    private float distance;
    private boolean favorite;

    public Station(String name, String address, int bikes, int docks, float distance, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.bikes = bikes;
        this.docks = docks;
        this.distance = distance;
        this.favorite = false;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBikes(int bikes) {
        this.bikes = bikes;
    }

    public void setDocks(int docks) {
        this.docks = docks;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setLatitude(double latitude){this.latitude = latitude;}

    public void setLongtitude(double longitude) {this.longitude = longitude;}

    public int getBikes() {return bikes;}

    public double getLatitude(){return latitude;}

    public double getLongtitude() {return longitude;}

    public int getDocks() {
        return docks;
    }

    public float getDistance() {
        return distance;
    }

    public String getAddress() {
        return address;
    }

    public boolean getFavorite() {
        return favorite;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    @Override
    public int compareTo(Station station) {
        //Ascending order by distance
        int dist = Math.round(station.getDistance()*10);
        return Math.round(this.distance*10) - dist;
    }
}
