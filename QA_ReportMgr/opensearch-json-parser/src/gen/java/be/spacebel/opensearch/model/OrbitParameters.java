package be.spacebel.opensearch.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

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
public class OrbitParameters  {
  public enum OrbitDirectionEnum {
    ASCENDING("ASCENDING"),
    DESCENDING("DESCENDING");

    private String value;

    OrbitDirectionEnum(String value) {
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
    public static OrbitDirectionEnum fromValue(String text) {
      for (OrbitDirectionEnum b : OrbitDirectionEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(description = "")
  private OrbitDirectionEnum orbitDirection = null;
  public enum LastOrbitDirectionEnum {
    ASCENDING("ASCENDING"),
    DESCENDING("DESCENDING");

    private String value;

    LastOrbitDirectionEnum(String value) {
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
    public static LastOrbitDirectionEnum fromValue(String text) {
      for (LastOrbitDirectionEnum b : LastOrbitDirectionEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(description = "")
  private LastOrbitDirectionEnum lastOrbitDirection = null;
  
  @Schema(description = "")
  private Integer orbitDuration = null;
  
  @Schema(description = "")
  private OffsetDateTime ascendingNodeDate = null;
  
  @Schema(description = "")
  private BigDecimal ascendingNodeLongitude = null;
  
  @Schema(description = "")
  private Integer orbitNumber = null;
  
  @Schema(description = "")
  private BigDecimal lastOrbitNumber = null;
 /**
   * Get orbitDirection
   * @return orbitDirection
  **/
  @JsonProperty("orbitDirection")
  public String getOrbitDirection() {
    if (orbitDirection == null) {
      return null;
    }
    return orbitDirection.getValue();
  }

  public void setOrbitDirection(OrbitDirectionEnum orbitDirection) {
    this.orbitDirection = orbitDirection;
  }

  public OrbitParameters orbitDirection(OrbitDirectionEnum orbitDirection) {
    this.orbitDirection = orbitDirection;
    return this;
  }

 /**
   * Get lastOrbitDirection
   * @return lastOrbitDirection
  **/
  @JsonProperty("lastOrbitDirection")
  public String getLastOrbitDirection() {
    if (lastOrbitDirection == null) {
      return null;
    }
    return lastOrbitDirection.getValue();
  }

  public void setLastOrbitDirection(LastOrbitDirectionEnum lastOrbitDirection) {
    this.lastOrbitDirection = lastOrbitDirection;
  }

  public OrbitParameters lastOrbitDirection(LastOrbitDirectionEnum lastOrbitDirection) {
    this.lastOrbitDirection = lastOrbitDirection;
    return this;
  }

 /**
   * Get orbitDuration
   * @return orbitDuration
  **/
  @JsonProperty("orbitDuration")
  public Integer getOrbitDuration() {
    return orbitDuration;
  }

  public void setOrbitDuration(Integer orbitDuration) {
    this.orbitDuration = orbitDuration;
  }

  public OrbitParameters orbitDuration(Integer orbitDuration) {
    this.orbitDuration = orbitDuration;
    return this;
  }

 /**
   * Get ascendingNodeDate
   * @return ascendingNodeDate
  **/
  @JsonProperty("ascendingNodeDate")
  public OffsetDateTime getAscendingNodeDate() {
    return ascendingNodeDate;
  }

  public void setAscendingNodeDate(OffsetDateTime ascendingNodeDate) {
    this.ascendingNodeDate = ascendingNodeDate;
  }

  public OrbitParameters ascendingNodeDate(OffsetDateTime ascendingNodeDate) {
    this.ascendingNodeDate = ascendingNodeDate;
    return this;
  }

 /**
   * Get ascendingNodeLongitude
   * @return ascendingNodeLongitude
  **/
  @JsonProperty("ascendingNodeLongitude")
  public BigDecimal getAscendingNodeLongitude() {
    return ascendingNodeLongitude;
  }

  public void setAscendingNodeLongitude(BigDecimal ascendingNodeLongitude) {
    this.ascendingNodeLongitude = ascendingNodeLongitude;
  }

  public OrbitParameters ascendingNodeLongitude(BigDecimal ascendingNodeLongitude) {
    this.ascendingNodeLongitude = ascendingNodeLongitude;
    return this;
  }

 /**
   * Get orbitNumber
   * minimum: 0
   * @return orbitNumber
  **/
  @JsonProperty("orbitNumber")
  public Integer getOrbitNumber() {
    return orbitNumber;
  }

  public void setOrbitNumber(Integer orbitNumber) {
    this.orbitNumber = orbitNumber;
  }

  public OrbitParameters orbitNumber(Integer orbitNumber) {
    this.orbitNumber = orbitNumber;
    return this;
  }

 /**
   * Get lastOrbitNumber
   * @return lastOrbitNumber
  **/
  @JsonProperty("lastOrbitNumber")
  public BigDecimal getLastOrbitNumber() {
    return lastOrbitNumber;
  }

  public void setLastOrbitNumber(BigDecimal lastOrbitNumber) {
    this.lastOrbitNumber = lastOrbitNumber;
  }

  public OrbitParameters lastOrbitNumber(BigDecimal lastOrbitNumber) {
    this.lastOrbitNumber = lastOrbitNumber;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrbitParameters {\n");
    
    sb.append("    orbitDirection: ").append(toIndentedString(orbitDirection)).append("\n");
    sb.append("    lastOrbitDirection: ").append(toIndentedString(lastOrbitDirection)).append("\n");
    sb.append("    orbitDuration: ").append(toIndentedString(orbitDuration)).append("\n");
    sb.append("    ascendingNodeDate: ").append(toIndentedString(ascendingNodeDate)).append("\n");
    sb.append("    ascendingNodeLongitude: ").append(toIndentedString(ascendingNodeLongitude)).append("\n");
    sb.append("    orbitNumber: ").append(toIndentedString(orbitNumber)).append("\n");
    sb.append("    lastOrbitNumber: ").append(toIndentedString(lastOrbitNumber)).append("\n");
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
