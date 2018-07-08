package com.eazy.firda.eazy.models;

/**
 * Created by firda on 3/1/2018.
 */

public class Book {

    private String id, name, status, carImage, ownerImage, startDate, endDate, location, date;

//    public Book(String status, String carImage, String startDate, String endDate, String location){
//        this.name = name;
//        this.status = status;
//        this.carImage = carImage;
//        this.ownerImage = ownerImage;
//        this.startDate = startDate;
//        this.endDate = endDate;
//        this.location = location;
//    }

    public Book(String name, String status, String carImage, String ownerImage, String startDate, String endDate, String location){
        this.name = name;
        this.status = status;
        this.carImage = carImage;
        this.ownerImage = ownerImage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
    }

    public Book(String name, String status, String carImage, String startDate, String endDate, String location){
        this.name = name;
        this.status = status;
        this.carImage = carImage;
        this.ownerImage = ownerImage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
    }

    public Book(String id, String name, String status, String date, String location){
        this.id = id;
        this.name = name;
        this.status = status;
        this.date = date;
        this.location = location;
    }

    public String getId(){return id;}

    public void setId(String id){this.id = id;}

    public String getName(){return name;}

    public void setName(String name){this.name = name;}

    public String getStatus(){return status;}

    public void setStatus(String status){this.status = status;}

    public String getCarImage(){return carImage;}

    public void setCarImage(String carImage){this.carImage = carImage;}

    public String getOwnerImage(){return ownerImage;}

    public void setOwnerImage(String ownerImage){this.ownerImage = ownerImage;}

    public String getStartDate(){return startDate;}

    public void setStartDate(String startDate){this.startDate = startDate;}

    public String getEndDate(){return endDate;}

    public void setEndDate(String endDate){this.endDate = endDate;}

    public String getLocation(){return location;}

    public void setLocation(String location){this.location = location;}

    public String getDate(){return date;}

    public void setDate(String date){this.date = date;}
}
