package be.spacebel.opensearch.model;

import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
  * Subset of AcquisitionParameters
 **/
@Schema(description="Subset of AcquisitionParameters")
public class VerticalSpatialDomain  {
  
  @Schema(description = "")
  private String highestLocation = null;
  
  @Schema(description = "")
  private String lowestLocation = null;
  public enum LocationUnitEnum {
    BAR("bar"),
    M("m");

    private String value;

    LocationUnitEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    @JsonCreator
    public static LocationUnitEnum fromValue(String text) {
      for (LocationUnitEnum b : LocationUnitEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(description = "")
  private LocationUnitEnum locationUnit = null;
 /**
   * Get highestLocation
   * @return highestLocation
  **/
  @JsonProperty("highestLocation")
  public String getHighestLocation() {
    return highestLocation;
  }

  public void setHighestLocation(String highestLocation) {
    this.highestLocation = highestLocation;
  }

  public VerticalSpatialDomain highestLocation(String highestLocation) {
    this.highestLocation = highestLocation;
    return this;
  }

 /**
   * Get lowestLocation
   * @return lowestLocation
  **/
  @JsonProperty("lowestLocation")
  public String getLowestLocation() {
    return lowestLocation;
  }

  public void setLowestLocation(String lowestLocation) {
    this.lowestLocation = lowestLocation;
  }

  public VerticalSpatialDomain lowestLocation(String lowestLocation) {
    this.lowestLocation = lowestLocation;
    return this;
  }

 /**
   * Get locationUnit
   * @return locationUnit
  **/
  @JsonProperty("locationUnit")
  public String getLocationUnit() {
    if (locationUnit == null) {
      return null;
    }
    return locationUnit.getValue();
  }

  public void setLocationUnit(LocationUnitEnum locationUnit) {
    this.locationUnit = locationUnit;
  }

  public VerticalSpatialDomain locationUnit(LocationUnitEnum locationUnit) {
    this.locationUnit = locationUnit;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VerticalSpatialDomain {\n");
    
    sb.append("    highestLocation: ").append(toIndentedString(highestLocation)).append("\n");
    sb.append("    lowestLocation: ").append(toIndentedString(lowestLocation)).append("\n");
    sb.append("    locationUnit: ").append(toIndentedString(locationUnit)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private static String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
