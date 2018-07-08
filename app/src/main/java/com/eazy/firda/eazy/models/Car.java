package com.eazy.firda.eazy.models;

/**
 * Created by firda on 2/9/2018.
 */

public class Car {

    private String car_id, name, owner, location, carImage, city;
    private double rating, price;
    private float distance;
    private int trips, year, status;

    public Car(){}

    public Car(String car_id, String name, String owner, String carImage, int year, double price, double rating, String location, String city, int trips, float distance){
        this.car_id = car_id;
        this.name = name;
        this.owner = owner;
        this.carImage = carImage;
        this.year = year;
        this.price = price;
        this.rating = rating;
        this.location = location;
        this.city = city;
        this.trips = trips;
        this.distance = distance;
    }

    public Car(String car_id, String name, String owner, String carImage, int year, double price, double rating, String location, String city, int trips){
        this.car_id = car_id;
        this.name = name;
        this.owner = owner;
        this.carImage = carImage;
        this.year = year;
        this.price = price;
        this.rating = rating;
        this.location = location;
        this.city = city;
        this.trips = trips;
    }

    public Car(String car_id, String name, String carImage, int year, double price){
        this.car_id = car_id;
        this.name = name;
        this.year = year;
        this.carImage = carImage;
        this.price = price;
    }

    public Car(String carImage){
        this.carImage = carImage;
    }

    public Car(String carImage, String car_id){
        this.carImage = carImage;
        this.car_id = car_id;
    }

    public Car(String car_id, String name, String carImage, int year, double price, int status){
        this.car_id = car_id;
        this.name = name;
        this.year = year;
        this.carImage = carImage;
        this.price = price;
        this.status = status;
    }

    public String getCar_id() { return car_id; }

    public void setCar_id(String car_id) { this.car_id = car_id; }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getOwner(){
        return owner;
    }

    public void setOwner(String owner){
        this.owner = owner;
    }

    public String getCarImage(){
        return carImage;
    }

    public void setCarImage(String carImage){
        this.carImage = carImage;
    }

    public int getYear() { return year; }

    public  void setYear(int year){ this.year = year; }

    public double getPrice(){
        return price;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public double getRating(){
        return rating;
    }

    public void setRating(double rating){
        this.rating = rating;
    }

    public String getCity(){ return city; }

    public void setCity(String city){ this.city = city; }

    public String getLocation(){ return location; }

    public void setLocation(String location) { this.location = location; }

    public int getTrips() { return trips; }

    public void setTrips(int trips) { this.trips = trips; }

    public float getDistance(){ return distance; }

    public void setDistance(float distance) { this.distance = distance; }

    public int getStatus(){ return status; }

    public void setStatus(int status) {this.status = status; }
}
