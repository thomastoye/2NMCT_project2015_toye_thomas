package be.thomastoye.findafrietkot.model;

import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties("extra")
public class Geocode {

    private double latitude;
    private double longitude;
    private String country;
    private String city;
    private String state;
    private String stateCode;
    private String zipcode;
    private String streetName;
    private String streetNumber;
    private String countryCode;
    private String formattedAddress;

    public Geocode() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getAddress() {
        StringBuffer res = new StringBuffer();
        if(streetName != null) {
            res.append(streetName);
            if(streetNumber != null) {
                res.append(" ");
                res.append(streetNumber);
            }
            res.append(", ");
        }

        if(zipcode != null) {
            res.append(zipcode);
            res.append(" ");
        }

        if(city != null) {
            res.append(city);
        }

        return res.toString();
    }
}