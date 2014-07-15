package com.misabelleeli.pacers_bikeshare;

/**
 * Created by Lee on 2014-07-14.
 */
public class Station {
    private String address;
    private int bikes;
    private int docks;
    private float distance;
    private boolean favorite;

    public Station(String address, int bikes, int docks, float distance) {

        this.address = address;
        this.bikes = bikes;
        this.docks = docks;
        this.distance = distance;
        this.favorite = false;
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

    public int getBikes() {

        return bikes;
    }

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

}
