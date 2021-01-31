package be.spacebel.opensearch.model;

import java.math.BigDecimal;

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

public class AcquisitionAngles  {
  
  @Schema(description = "")
  private BigDecimal illuminationAzimuthAngle = null;
  
  @Schema(description = "")
  private BigDecimal illuminationZenithAngle = null;
  
  @Schema(description = "")
  private BigDecimal illuminationElevationAngle = null;
  
  @Schema(description = "")
  private BigDecimal incidenceAngle = null;
  
  @Schema(description = "")
  private BigDecimal minimumIncidenceAngle = null;
  
  @Schema(description = "")
  private BigDecimal maximumIncidenceAngle = null;
  
  @Schema(description = "")
  private BigDecimal incidenceAngleVariation = null;
  
  @Schema(description = "")
  private BigDecimal acrossTrackIncidenceAngle = null;
  
  @Schema(description = "")
  private BigDecimal alongTrackIncidenceAngle = null;
  
  @Schema(description = "")
  private BigDecimal instrumentAzimuthAngle = null;
  
  @Schema(description = "")
  private BigDecimal instrumentZenithAngle = null;
  
  @Schema(description = "")
  private BigDecimal instrumentElevationAngle = null;
  
  @Schema(description = "")
  private BigDecimal pitch = null;
  
  @Schema(description = "")
  private BigDecimal roll = null;
  
  @Schema(description = "")
  private BigDecimal yaw = null;
 /**
   * Get illuminationAzimuthAngle
   * @return illuminationAzimuthAngle
  **/
  @JsonProperty("illuminationAzimuthAngle")
  public BigDecimal getIlluminationAzimuthAngle() {
    return illuminationAzimuthAngle;
  }

  public void setIlluminationAzimuthAngle(BigDecimal illuminationAzimuthAngle) {
    this.illuminationAzimuthAngle = illuminationAzimuthAngle;
  }

  public AcquisitionAngles illuminationAzimuthAngle(BigDecimal illuminationAzimuthAngle) {
    this.illuminationAzimuthAngle = illuminationAzimuthAngle;
    return this;
  }

 /**
   * Get illuminationZenithAngle
   * @return illuminationZenithAngle
  **/
  @JsonProperty("illuminationZenithAngle")
  public BigDecimal getIlluminationZenithAngle() {
    return illuminationZenithAngle;
  }

  public void setIlluminationZenithAngle(BigDecimal illuminationZenithAngle) {
    this.illuminationZenithAngle = illuminationZenithAngle;
  }

  public AcquisitionAngles illuminationZenithAngle(BigDecimal illuminationZenithAngle) {
    this.illuminationZenithAngle = illuminationZenithAngle;
    return this;
  }

 /**
   * Get illuminationElevationAngle
   * @return illuminationElevationAngle
  **/
  @JsonProperty("illuminationElevationAngle")
  public BigDecimal getIlluminationElevationAngle() {
    return illuminationElevationAngle;
  }

  public void setIlluminationElevationAngle(BigDecimal illuminationElevationAngle) {
    this.illuminationElevationAngle = illuminationElevationAngle;
  }

  public AcquisitionAngles illuminationElevationAngle(BigDecimal illuminationElevationAngle) {
    this.illuminationElevationAngle = illuminationElevationAngle;
    return this;
  }

 /**
   * Get incidenceAngle
   * @return incidenceAngle
  **/
  @JsonProperty("incidenceAngle")
  public BigDecimal getIncidenceAngle() {
    return incidenceAngle;
  }

  public void setIncidenceAngle(BigDecimal incidenceAngle) {
    this.incidenceAngle = incidenceAngle;
  }

  public AcquisitionAngles incidenceAngle(BigDecimal incidenceAngle) {
    this.incidenceAngle = incidenceAngle;
    return this;
  }

 /**
   * Get minimumIncidenceAngle
   * @return minimumIncidenceAngle
  **/
  @JsonProperty("minimumIncidenceAngle")
  public BigDecimal getMinimumIncidenceAngle() {
    return minimumIncidenceAngle;
  }

  public void setMinimumIncidenceAngle(BigDecimal minimumIncidenceAngle) {
    this.minimumIncidenceAngle = minimumIncidenceAngle;
  }

  public AcquisitionAngles minimumIncidenceAngle(BigDecimal minimumIncidenceAngle) {
    this.minimumIncidenceAngle = minimumIncidenceAngle;
    return this;
  }

 /**
   * Get maximumIncidenceAngle
   * @return maximumIncidenceAngle
  **/
  @JsonProperty("maximumIncidenceAngle")
  public BigDecimal getMaximumIncidenceAngle() {
    return maximumIncidenceAngle;
  }

  public void setMaximumIncidenceAngle(BigDecimal maximumIncidenceAngle) {
    this.maximumIncidenceAngle = maximumIncidenceAngle;
  }

  public AcquisitionAngles maximumIncidenceAngle(BigDecimal maximumIncidenceAngle) {
    this.maximumIncidenceAngle = maximumIncidenceAngle;
    return this;
  }

 /**
   * Get incidenceAngleVariation
   * @return incidenceAngleVariation
  **/
  @JsonProperty("incidenceAngleVariation")
  public BigDecimal getIncidenceAngleVariation() {
    return incidenceAngleVariation;
  }

  public void setIncidenceAngleVariation(BigDecimal incidenceAngleVariation) {
    this.incidenceAngleVariation = incidenceAngleVariation;
  }

  public AcquisitionAngles incidenceAngleVariation(BigDecimal incidenceAngleVariation) {
    this.incidenceAngleVariation = incidenceAngleVariation;
    return this;
  }

 /**
   * Get acrossTrackIncidenceAngle
   * @return acrossTrackIncidenceAngle
  **/
  @JsonProperty("acrossTrackIncidenceAngle")
  public BigDecimal getAcrossTrackIncidenceAngle() {
    return acrossTrackIncidenceAngle;
  }

  public void setAcrossTrackIncidenceAngle(BigDecimal acrossTrackIncidenceAngle) {
    this.acrossTrackIncidenceAngle = acrossTrackIncidenceAngle;
  }

  public AcquisitionAngles acrossTrackIncidenceAngle(BigDecimal acrossTrackIncidenceAngle) {
    this.acrossTrackIncidenceAngle = acrossTrackIncidenceAngle;
    return this;
  }

 /**
   * Get alongTrackIncidenceAngle
   * @return alongTrackIncidenceAngle
  **/
  @JsonProperty("alongTrackIncidenceAngle")
  public BigDecimal getAlongTrackIncidenceAngle() {
    return alongTrackIncidenceAngle;
  }

  public void setAlongTrackIncidenceAngle(BigDecimal alongTrackIncidenceAngle) {
    this.alongTrackIncidenceAngle = alongTrackIncidenceAngle;
  }

  public AcquisitionAngles alongTrackIncidenceAngle(BigDecimal alongTrackIncidenceAngle) {
    this.alongTrackIncidenceAngle = alongTrackIncidenceAngle;
    return this;
  }

 /**
   * Get instrumentAzimuthAngle
   * @return instrumentAzimuthAngle
  **/
  @JsonProperty("instrumentAzimuthAngle")
  public BigDecimal getInstrumentAzimuthAngle() {
    return instrumentAzimuthAngle;
  }

  public void setInstrumentAzimuthAngle(BigDecimal instrumentAzimuthAngle) {
    this.instrumentAzimuthAngle = instrumentAzimuthAngle;
  }

  public AcquisitionAngles instrumentAzimuthAngle(BigDecimal instrumentAzimuthAngle) {
    this.instrumentAzimuthAngle = instrumentAzimuthAngle;
    return this;
  }

 /**
   * Get instrumentZenithAngle
   * @return instrumentZenithAngle
  **/
  @JsonProperty("instrumentZenithAngle")
  public BigDecimal getInstrumentZenithAngle() {
    return instrumentZenithAngle;
  }

  public void setInstrumentZenithAngle(BigDecimal instrumentZenithAngle) {
    this.instrumentZenithAngle = instrumentZenithAngle;
  }

  public AcquisitionAngles instrumentZenithAngle(BigDecimal instrumentZenithAngle) {
    this.instrumentZenithAngle = instrumentZenithAngle;
    return this;
  }

 /**
   * Get instrumentElevationAngle
   * @return instrumentElevationAngle
  **/
  @JsonProperty("instrumentElevationAngle")
  public BigDecimal getInstrumentElevationAngle() {
    return instrumentElevationAngle;
  }

  public void setInstrumentElevationAngle(BigDecimal instrumentElevationAngle) {
    this.instrumentElevationAngle = instrumentElevationAngle;
  }

  public AcquisitionAngles instrumentElevationAngle(BigDecimal instrumentElevationAngle) {
    this.instrumentElevationAngle = instrumentElevationAngle;
    return this;
  }

 /**
   * Get pitch
   * @return pitch
  **/
  @JsonProperty("pitch")
  public BigDecimal getPitch() {
    return pitch;
  }

  public void setPitch(BigDecimal pitch) {
    this.pitch = pitch;
  }

  public AcquisitionAngles pitch(BigDecimal pitch) {
    this.pitch = pitch;
    return this;
  }

 /**
   * Get roll
   * @return roll
  **/
  @JsonProperty("roll")
  public BigDecimal getRoll() {
    return roll;
  }

  public void setRoll(BigDecimal roll) {
    this.roll = roll;
  }

  public AcquisitionAngles roll(BigDecimal roll) {
    this.roll = roll;
    return this;
  }

 /**
   * Get yaw
   * @return yaw
  **/
  @JsonProperty("yaw")
  public BigDecimal getYaw() {
    return yaw;
  }

  public void setYaw(BigDecimal yaw) {
    this.yaw = yaw;
  }

  public AcquisitionAngles yaw(BigDecimal yaw) {
    this.yaw = yaw;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AcquisitionAngles {\n");
    
    sb.append("    illuminationAzimuthAngle: ").append(toIndentedString(illuminationAzimuthAngle)).append("\n");
    sb.append("    illuminationZenithAngle: ").append(toIndentedString(illuminationZenithAngle)).append("\n");
    sb.append("    illuminationElevationAngle: ").append(toIndentedString(illuminationElevationAngle)).append("\n");
    sb.append("    incidenceAngle: ").append(toIndentedString(incidenceAngle)).append("\n");
    sb.append("    minimumIncidenceAngle: ").append(toIndentedString(minimumIncidenceAngle)).append("\n");
    sb.append("    maximumIncidenceAngle: ").append(toIndentedString(maximumIncidenceAngle)).append("\n");
    sb.append("    incidenceAngleVariation: ").append(toIndentedString(incidenceAngleVariation)).append("\n");
    sb.append("    acrossTrackIncidenceAngle: ").append(toIndentedString(acrossTrackIncidenceAngle)).append("\n");
    sb.append("    alongTrackIncidenceAngle: ").append(toIndentedString(alongTrackIncidenceAngle)).append("\n");
    sb.append("    instrumentAzimuthAngle: ").append(toIndentedString(instrumentAzimuthAngle)).append("\n");
    sb.append("    instrumentZenithAngle: ").append(toIndentedString(instrumentZenithAngle)).append("\n");
    sb.append("    instrumentElevationAngle: ").append(toIndentedString(instrumentElevationAngle)).append("\n");
    sb.append("    pitch: ").append(toIndentedString(pitch)).append("\n");
    sb.append("    roll: ").append(toIndentedString(roll)).append("\n");
    sb.append("    yaw: ").append(toIndentedString(yaw)).append("\n");
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
