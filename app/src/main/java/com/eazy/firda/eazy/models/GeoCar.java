package com.eazy.firda.eazy.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by firda on 2/25/2018.
 */

public class GeoCar implements ClusterItem {

    private LatLng mPosition;
    private String image;
    private String carName;
    private String ownerName;
    private String address;
    private String rating;
    private String carId;
    private String information;

    public GeoCar(double lat, double lng, String information){
        mPosition = new LatLng(lat, lng);
        this.information = information;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {this.address = address; }

    public String getRating() { return rating;}

    public void setRating(String rating) {this.rating = rating; }

    public String getCarId() { return carId; }

    public void setCarId(String carId) {this.carId = carId; }

    public String getInformation(){ return information;}

    public void setInformation(String information) {this.information = information; }

    @Override
    public String getTitle() {
        return carName;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
