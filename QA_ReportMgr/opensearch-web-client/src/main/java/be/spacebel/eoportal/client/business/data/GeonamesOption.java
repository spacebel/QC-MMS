package be.spacebel.eoportal.client.business.data;

import java.io.Serializable;

/**
 * This class represents a GeoNames toponym.
 *
 * @author mng
 */
public class GeonamesOption implements Serializable {

    private String name;
    private double latitude;
    private double longitude;
    private String bbox;
    private String countryCode;
    private String countryName;

    public GeonamesOption() {

    }

    public GeonamesOption(String name, double latitude, double longitude, String countryCode, String countryName, double west, double east, double south, double north) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;

        StringBuilder sb = new StringBuilder();
        sb.append(west).append(",").append(south).append(",").append(east).append(",").append(north);
        this.bbox = sb.toString();

        this.countryCode = countryCode;
        this.countryName = countryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getBbox() {
        return bbox;
    }

    public void setBbox(String bbox) {
        this.bbox = bbox;
    }

    
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
