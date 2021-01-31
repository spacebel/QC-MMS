package be.spacebel.opensearch.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

public class WavelengthInformation  {
  public enum TypeEnum {
    WAVELENGTHINFORMATION("WavelengthInformation");

    private String value;

    TypeEnum(String value) {
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
    public static TypeEnum fromValue(String text) {
      for (TypeEnum b : TypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(description = "")
  private TypeEnum type = null;
  
  @Schema(description = "")
  private List<BigDecimal> discreteWavelengths = null;
  
  @Schema(description = "")
  private BigDecimal endWavelength = null;
  public enum SpectralRangeEnum {
    INFRARED("INFRARED"),
    NIR("NIR"),
    SWIR("SWIR"),
    MWIR("MWIR"),
    LWIR("LWIR"),
    FIR("FIR"),
    UV("UV"),
    VISIBLE("VISIBLE"),
    MICROWAVE("MICROWAVE"),
    OTHER("OTHER");

    private String value;

    SpectralRangeEnum(String value) {
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
    public static SpectralRangeEnum fromValue(String text) {
      for (SpectralRangeEnum b : SpectralRangeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(description = "")
  private SpectralRangeEnum spectralRange = null;
  
  @Schema(description = "")
  private BigDecimal startWavelength = null;
  
  @Schema(description = "")
  private BigDecimal wavelengthResolution = null;
 /**
   * Get type
   * @return type
  **/
  @JsonProperty("type")
  public String getType() {
    if (type == null) {
      return null;
    }
    return type.getValue();
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public WavelengthInformation type(TypeEnum type) {
    this.type = type;
    return this;
  }

 /**
   * Get discreteWavelengths
   * @return discreteWavelengths
  **/
  @JsonProperty("discreteWavelengths")
  public List<BigDecimal> getDiscreteWavelengths() {
    return discreteWavelengths;
  }

  public void setDiscreteWavelengths(List<BigDecimal> discreteWavelengths) {
    this.discreteWavelengths = discreteWavelengths;
  }

  public WavelengthInformation discreteWavelengths(List<BigDecimal> discreteWavelengths) {
    this.discreteWavelengths = discreteWavelengths;
    return this;
  }

  public WavelengthInformation addDiscreteWavelengthsItem(BigDecimal discreteWavelengthsItem) {
    this.discreteWavelengths.add(discreteWavelengthsItem);
    return this;
  }

 /**
   * Get endWavelength
   * minimum: 0
   * @return endWavelength
  **/
  @JsonProperty("endWavelength")
  public BigDecimal getEndWavelength() {
    return endWavelength;
  }

  public void setEndWavelength(BigDecimal endWavelength) {
    this.endWavelength = endWavelength;
  }

  public WavelengthInformation endWavelength(BigDecimal endWavelength) {
    this.endWavelength = endWavelength;
    return this;
  }

 /**
   * Get spectralRange
   * @return spectralRange
  **/
  @JsonProperty("spectralRange")
  public String getSpectralRange() {
    if (spectralRange == null) {
      return null;
    }
    return spectralRange.getValue();
  }

  public void setSpectralRange(SpectralRangeEnum spectralRange) {
    this.spectralRange = spectralRange;
  }

  public WavelengthInformation spectralRange(SpectralRangeEnum spectralRange) {
    this.spectralRange = spectralRange;
    return this;
  }

 /**
   * Get startWavelength
   * minimum: 0
   * @return startWavelength
  **/
  @JsonProperty("startWavelength")
  public BigDecimal getStartWavelength() {
    return startWavelength;
  }

  public void setStartWavelength(BigDecimal startWavelength) {
    this.startWavelength = startWavelength;
  }

  public WavelengthInformation startWavelength(BigDecimal startWavelength) {
    this.startWavelength = startWavelength;
    return this;
  }

 /**
   * Get wavelengthResolution
   * @return wavelengthResolution
  **/
  @JsonProperty("wavelengthResolution")
  public BigDecimal getWavelengthResolution() {
    return wavelengthResolution;
  }

  public void setWavelengthResolution(BigDecimal wavelengthResolution) {
    this.wavelengthResolution = wavelengthResolution;
  }

  public WavelengthInformation wavelengthResolution(BigDecimal wavelengthResolution) {
    this.wavelengthResolution = wavelengthResolution;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WavelengthInformation {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    discreteWavelengths: ").append(toIndentedString(discreteWavelengths)).append("\n");
    sb.append("    endWavelength: ").append(toIndentedString(endWavelength)).append("\n");
    sb.append("    spectralRange: ").append(toIndentedString(spectralRange)).append("\n");
    sb.append("    startWavelength: ").append(toIndentedString(startWavelength)).append("\n");
    sb.append("    wavelengthResolution: ").append(toIndentedString(wavelengthResolution)).append("\n");
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
