package be.spacebel.opensearch.model;

import be.spacebel.opensearch.model.AcquisitionAngles;
import be.spacebel.opensearch.model.OrbitParameters;
import be.spacebel.opensearch.model.TemporalInformation;
import be.spacebel.opensearch.model.VerticalSpatialDomain;
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

public class AcquisitionParameters extends TemporalInformation {
  
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
  public enum AcquisitionTypeEnum {
    NOMINAL("NOMINAL"),
    CALIBRATION("CALIBRATION"),
    OTHER("OTHER");

    private String value;

    AcquisitionTypeEnum(String value) {
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
    public static AcquisitionTypeEnum fromValue(String text) {
      for (AcquisitionTypeEnum b : AcquisitionTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(required = true, description = "")
  private AcquisitionTypeEnum acquisitionType = null;
  
  @Schema(description = "")
  private String acquisitionSubType = null;
  
  @Schema(description = "")
  private Integer startTimeFromAscendingNode = null;
  
  @Schema(description = "")
  private Integer completionTimeFromAscendingNode = null;
  
  @Schema(description = "")
  private Integer relativeOrbitNumber = null;
  
  @Schema(description = "")
  private String wrsLongitude = null;
  
  @Schema(description = "")
  private String wrsLatitude = null;
  
  @Schema(description = "")
  private String tileId = null;
  
  @Schema(description = "")
  private BigDecimal groundTrackUncertainty = null;
  
  @Schema(description = "")
  private Integer cycleNumber = null;
  public enum AntennaLookDirectionEnum {
    LEFT("LEFT"),
    RIGHT("RIGHT");

    private String value;

    AntennaLookDirectionEnum(String value) {
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
    public static AntennaLookDirectionEnum fromValue(String text) {
      for (AntennaLookDirectionEnum b : AntennaLookDirectionEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(description = "")
  private AntennaLookDirectionEnum antennaLookDirection = null;
  
  @Schema(description = "")
  private String acquisitionStation = null;
  
  @Schema(description = "")
  private AcquisitionAngles acquisitionAngles = null;
  
  @Schema(description = "")
  private String operationalMode = null;
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

  public AcquisitionParameters highestLocation(String highestLocation) {
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

  public AcquisitionParameters lowestLocation(String lowestLocation) {
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

  public AcquisitionParameters locationUnit(LocationUnitEnum locationUnit) {
    this.locationUnit = locationUnit;
    return this;
  }

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

  public AcquisitionParameters orbitDirection(OrbitDirectionEnum orbitDirection) {
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

  public AcquisitionParameters lastOrbitDirection(LastOrbitDirectionEnum lastOrbitDirection) {
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

  public AcquisitionParameters orbitDuration(Integer orbitDuration) {
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

  public AcquisitionParameters ascendingNodeDate(OffsetDateTime ascendingNodeDate) {
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

  public AcquisitionParameters ascendingNodeLongitude(BigDecimal ascendingNodeLongitude) {
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

  public AcquisitionParameters orbitNumber(Integer orbitNumber) {
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

  public AcquisitionParameters lastOrbitNumber(BigDecimal lastOrbitNumber) {
    this.lastOrbitNumber = lastOrbitNumber;
    return this;
  }

 /**
   * Get acquisitionType
   * @return acquisitionType
  **/
  @JsonProperty("acquisitionType")
  public String getAcquisitionType() {
    if (acquisitionType == null) {
      return null;
    }
    return acquisitionType.getValue();
  }

  public void setAcquisitionType(AcquisitionTypeEnum acquisitionType) {
    this.acquisitionType = acquisitionType;
  }

  public AcquisitionParameters acquisitionType(AcquisitionTypeEnum acquisitionType) {
    this.acquisitionType = acquisitionType;
    return this;
  }

 /**
   * Get acquisitionSubType
   * @return acquisitionSubType
  **/
  @JsonProperty("acquisitionSubType")
  public String getAcquisitionSubType() {
    return acquisitionSubType;
  }

  public void setAcquisitionSubType(String acquisitionSubType) {
    this.acquisitionSubType = acquisitionSubType;
  }

  public AcquisitionParameters acquisitionSubType(String acquisitionSubType) {
    this.acquisitionSubType = acquisitionSubType;
    return this;
  }

 /**
   * Get startTimeFromAscendingNode
   * minimum: 0
   * @return startTimeFromAscendingNode
  **/
  @JsonProperty("startTimeFromAscendingNode")
  public Integer getStartTimeFromAscendingNode() {
    return startTimeFromAscendingNode;
  }

  public void setStartTimeFromAscendingNode(Integer startTimeFromAscendingNode) {
    this.startTimeFromAscendingNode = startTimeFromAscendingNode;
  }

  public AcquisitionParameters startTimeFromAscendingNode(Integer startTimeFromAscendingNode) {
    this.startTimeFromAscendingNode = startTimeFromAscendingNode;
    return this;
  }

 /**
   * Get completionTimeFromAscendingNode
   * minimum: 0
   * @return completionTimeFromAscendingNode
  **/
  @JsonProperty("completionTimeFromAscendingNode")
  public Integer getCompletionTimeFromAscendingNode() {
    return completionTimeFromAscendingNode;
  }

  public void setCompletionTimeFromAscendingNode(Integer completionTimeFromAscendingNode) {
    this.completionTimeFromAscendingNode = completionTimeFromAscendingNode;
  }

  public AcquisitionParameters completionTimeFromAscendingNode(Integer completionTimeFromAscendingNode) {
    this.completionTimeFromAscendingNode = completionTimeFromAscendingNode;
    return this;
  }

 /**
   * Get relativeOrbitNumber
   * @return relativeOrbitNumber
  **/
  @JsonProperty("relativeOrbitNumber")
  public Integer getRelativeOrbitNumber() {
    return relativeOrbitNumber;
  }

  public void setRelativeOrbitNumber(Integer relativeOrbitNumber) {
    this.relativeOrbitNumber = relativeOrbitNumber;
  }

  public AcquisitionParameters relativeOrbitNumber(Integer relativeOrbitNumber) {
    this.relativeOrbitNumber = relativeOrbitNumber;
    return this;
  }

 /**
   * Get wrsLongitude
   * @return wrsLongitude
  **/
  @JsonProperty("wrsLongitude")
  public String getWrsLongitude() {
    return wrsLongitude;
  }

  public void setWrsLongitude(String wrsLongitude) {
    this.wrsLongitude = wrsLongitude;
  }

  public AcquisitionParameters wrsLongitude(String wrsLongitude) {
    this.wrsLongitude = wrsLongitude;
    return this;
  }

 /**
   * Get wrsLatitude
   * @return wrsLatitude
  **/
  @JsonProperty("wrsLatitude")
  public String getWrsLatitude() {
    return wrsLatitude;
  }

  public void setWrsLatitude(String wrsLatitude) {
    this.wrsLatitude = wrsLatitude;
  }

  public AcquisitionParameters wrsLatitude(String wrsLatitude) {
    this.wrsLatitude = wrsLatitude;
    return this;
  }

 /**
   * Get tileId
   * @return tileId
  **/
  @JsonProperty("tileId")
  public String getTileId() {
    return tileId;
  }

  public void setTileId(String tileId) {
    this.tileId = tileId;
  }

  public AcquisitionParameters tileId(String tileId) {
    this.tileId = tileId;
    return this;
  }

 /**
   * Get groundTrackUncertainty
   * @return groundTrackUncertainty
  **/
  @JsonProperty("groundTrackUncertainty")
  public BigDecimal getGroundTrackUncertainty() {
    return groundTrackUncertainty;
  }

  public void setGroundTrackUncertainty(BigDecimal groundTrackUncertainty) {
    this.groundTrackUncertainty = groundTrackUncertainty;
  }

  public AcquisitionParameters groundTrackUncertainty(BigDecimal groundTrackUncertainty) {
    this.groundTrackUncertainty = groundTrackUncertainty;
    return this;
  }

 /**
   * Get cycleNumber
   * minimum: 0
   * @return cycleNumber
  **/
  @JsonProperty("cycleNumber")
  public Integer getCycleNumber() {
    return cycleNumber;
  }

  public void setCycleNumber(Integer cycleNumber) {
    this.cycleNumber = cycleNumber;
  }

  public AcquisitionParameters cycleNumber(Integer cycleNumber) {
    this.cycleNumber = cycleNumber;
    return this;
  }

 /**
   * Get antennaLookDirection
   * @return antennaLookDirection
  **/
  @JsonProperty("antennaLookDirection")
  public String getAntennaLookDirection() {
    if (antennaLookDirection == null) {
      return null;
    }
    return antennaLookDirection.getValue();
  }

  public void setAntennaLookDirection(AntennaLookDirectionEnum antennaLookDirection) {
    this.antennaLookDirection = antennaLookDirection;
  }

  public AcquisitionParameters antennaLookDirection(AntennaLookDirectionEnum antennaLookDirection) {
    this.antennaLookDirection = antennaLookDirection;
    return this;
  }

 /**
   * Get acquisitionStation
   * @return acquisitionStation
  **/
  @JsonProperty("acquisitionStation")
  public String getAcquisitionStation() {
    return acquisitionStation;
  }

  public void setAcquisitionStation(String acquisitionStation) {
    this.acquisitionStation = acquisitionStation;
  }

  public AcquisitionParameters acquisitionStation(String acquisitionStation) {
    this.acquisitionStation = acquisitionStation;
    return this;
  }

 /**
   * Get acquisitionAngles
   * @return acquisitionAngles
  **/
  @JsonProperty("acquisitionAngles")
  public AcquisitionAngles getAcquisitionAngles() {
    return acquisitionAngles;
  }

  public void setAcquisitionAngles(AcquisitionAngles acquisitionAngles) {
    this.acquisitionAngles = acquisitionAngles;
  }

  public AcquisitionParameters acquisitionAngles(AcquisitionAngles acquisitionAngles) {
    this.acquisitionAngles = acquisitionAngles;
    return this;
  }

 /**
   * Get operationalMode
   * @return operationalMode
  **/
  @JsonProperty("operationalMode")
  public String getOperationalMode() {
    return operationalMode;
  }

  public void setOperationalMode(String operationalMode) {
    this.operationalMode = operationalMode;
  }

  public AcquisitionParameters operationalMode(String operationalMode) {
    this.operationalMode = operationalMode;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AcquisitionParameters {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    highestLocation: ").append(toIndentedString(highestLocation)).append("\n");
    sb.append("    lowestLocation: ").append(toIndentedString(lowestLocation)).append("\n");
    sb.append("    locationUnit: ").append(toIndentedString(locationUnit)).append("\n");
    sb.append("    orbitDirection: ").append(toIndentedString(orbitDirection)).append("\n");
    sb.append("    lastOrbitDirection: ").append(toIndentedString(lastOrbitDirection)).append("\n");
    sb.append("    orbitDuration: ").append(toIndentedString(orbitDuration)).append("\n");
    sb.append("    ascendingNodeDate: ").append(toIndentedString(ascendingNodeDate)).append("\n");
    sb.append("    ascendingNodeLongitude: ").append(toIndentedString(ascendingNodeLongitude)).append("\n");
    sb.append("    orbitNumber: ").append(toIndentedString(orbitNumber)).append("\n");
    sb.append("    lastOrbitNumber: ").append(toIndentedString(lastOrbitNumber)).append("\n");
    sb.append("    acquisitionType: ").append(toIndentedString(acquisitionType)).append("\n");
    sb.append("    acquisitionSubType: ").append(toIndentedString(acquisitionSubType)).append("\n");
    sb.append("    startTimeFromAscendingNode: ").append(toIndentedString(startTimeFromAscendingNode)).append("\n");
    sb.append("    completionTimeFromAscendingNode: ").append(toIndentedString(completionTimeFromAscendingNode)).append("\n");
    sb.append("    relativeOrbitNumber: ").append(toIndentedString(relativeOrbitNumber)).append("\n");
    sb.append("    wrsLongitude: ").append(toIndentedString(wrsLongitude)).append("\n");
    sb.append("    wrsLatitude: ").append(toIndentedString(wrsLatitude)).append("\n");
    sb.append("    tileId: ").append(toIndentedString(tileId)).append("\n");
    sb.append("    groundTrackUncertainty: ").append(toIndentedString(groundTrackUncertainty)).append("\n");
    sb.append("    cycleNumber: ").append(toIndentedString(cycleNumber)).append("\n");
    sb.append("    antennaLookDirection: ").append(toIndentedString(antennaLookDirection)).append("\n");
    sb.append("    acquisitionStation: ").append(toIndentedString(acquisitionStation)).append("\n");
    sb.append("    acquisitionAngles: ").append(toIndentedString(acquisitionAngles)).append("\n");
    sb.append("    operationalMode: ").append(toIndentedString(operationalMode)).append("\n");
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
