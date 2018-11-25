package com.example.mth.vildmad;

public class Plant {

    private String plantName;
    private String ID;
    private String owner;
    private Float lat;
    private Float lon;
    private String description;

    public Plant(String plantName, String ID, String owner, Float lat, Float lon, String description) {
        this.plantName = plantName;
        this.ID = ID;
        this.owner = owner;
        this.lat = lat;
        this.lon = lon;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Plant{" +
                "plantName='" + plantName + '\'' +
                ", ID='" + ID + '\'' +
                ", owner='" + owner + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", description='" + description + '\'' +
                '}';
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLon() {
        return lon;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
