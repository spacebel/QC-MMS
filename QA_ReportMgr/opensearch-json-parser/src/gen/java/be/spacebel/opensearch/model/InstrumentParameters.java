package be.spacebel.opensearch.model;

import be.spacebel.opensearch.model.WavelengthInformation;
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

public class InstrumentParameters  {
  public enum TypeEnum {
    INSTRUMENTPARAMETERS("InstrumentParameters");

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
  private String operationalMode = null;
  
  @Schema(description = "")
  private String swathIdentifier = null;
  public enum PolarisationModeEnum {
    S("S"),
    D("D"),
    T("T"),
    Q("Q"),
    UNDEFINED("UNDEFINED");

    private String value;

    PolarisationModeEnum(String value) {
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
    public static PolarisationModeEnum fromValue(String text) {
      for (PolarisationModeEnum b : PolarisationModeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(description = "Sar")
 /**
   * Sar  
  **/
  private PolarisationModeEnum polarisationMode = null;
  
  @Schema(description = "Sar")
 /**
   * Sar  
  **/
  private String polarisationChannels = null;
  
  @Schema(description = "")
  private BigDecimal resolution = null;
  
  @Schema(description = "Atm, Lmb")
 /**
   * Atm, Lmb  
  **/
  private BigDecimal verticalResolution = null;
  
  @Schema(description = "")
  private List<WavelengthInformation> waveLengths = null;
  public enum MeasurementTypeEnum {
    ABSORPTION("ABSORPTION"),
    EMISSION("EMISSION");

    private String value;

    MeasurementTypeEnum(String value) {
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
    public static MeasurementTypeEnum fromValue(String text) {
      for (MeasurementTypeEnum b : MeasurementTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(description = "")
  private MeasurementTypeEnum measurementType = null;
  
  @Schema(description = "Sar")
 /**
   * Sar  
  **/
  private BigDecimal dopplerFrequency = null;
  
  @Schema(description = "Alt")
 /**
   * Alt  
  **/
  private List<BigDecimal> samplingRates = null;
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

  public InstrumentParameters type(TypeEnum type) {
    this.type = type;
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

  public InstrumentParameters operationalMode(String operationalMode) {
    this.operationalMode = operationalMode;
    return this;
  }

 /**
   * Get swathIdentifier
   * @return swathIdentifier
  **/
  @JsonProperty("swathIdentifier")
  public String getSwathIdentifier() {
    return swathIdentifier;
  }

  public void setSwathIdentifier(String swathIdentifier) {
    this.swathIdentifier = swathIdentifier;
  }

  public InstrumentParameters swathIdentifier(String swathIdentifier) {
    this.swathIdentifier = swathIdentifier;
    return this;
  }

 /**
   * Sar
   * @return polarisationMode
  **/
  @JsonProperty("polarisationMode")
  public String getPolarisationMode() {
    if (polarisationMode == null) {
      return null;
    }
    return polarisationMode.getValue();
  }

  public void setPolarisationMode(PolarisationModeEnum polarisationMode) {
    this.polarisationMode = polarisationMode;
  }

  public InstrumentParameters polarisationMode(PolarisationModeEnum polarisationMode) {
    this.polarisationMode = polarisationMode;
    return this;
  }

 /**
   * Sar
   * @return polarisationChannels
  **/
  @JsonProperty("polarisationChannels")
  public String getPolarisationChannels() {
    return polarisationChannels;
  }

  public void setPolarisationChannels(String polarisationChannels) {
    this.polarisationChannels = polarisationChannels;
  }

  public InstrumentParameters polarisationChannels(String polarisationChannels) {
    this.polarisationChannels = polarisationChannels;
    return this;
  }

 /**
   * Get resolution
   * @return resolution
  **/
  @JsonProperty("resolution")
  public BigDecimal getResolution() {
    return resolution;
  }

  public void setResolution(BigDecimal resolution) {
    this.resolution = resolution;
  }

  public InstrumentParameters resolution(BigDecimal resolution) {
    this.resolution = resolution;
    return this;
  }

 /**
   * Atm, Lmb
   * @return verticalResolution
  **/
  @JsonProperty("verticalResolution")
  public BigDecimal getVerticalResolution() {
    return verticalResolution;
  }

  public void setVerticalResolution(BigDecimal verticalResolution) {
    this.verticalResolution = verticalResolution;
  }

  public InstrumentParameters verticalResolution(BigDecimal verticalResolution) {
    this.verticalResolution = verticalResolution;
    return this;
  }

 /**
   * Get waveLengths
   * @return waveLengths
  **/
  @JsonProperty("waveLengths")
  public List<WavelengthInformation> getWaveLengths() {
    return waveLengths;
  }

  public void setWaveLengths(List<WavelengthInformation> waveLengths) {
    this.waveLengths = waveLengths;
  }

  public InstrumentParameters waveLengths(List<WavelengthInformation> waveLengths) {
    this.waveLengths = waveLengths;
    return this;
  }

  public InstrumentParameters addWaveLengthsItem(WavelengthInformation waveLengthsItem) {
    this.waveLengths.add(waveLengthsItem);
    return this;
  }

 /**
   * Get measurementType
   * @return measurementType
  **/
  @JsonProperty("measurementType")
  public String getMeasurementType() {
    if (measurementType == null) {
      return null;
    }
    return measurementType.getValue();
  }

  public void setMeasurementType(MeasurementTypeEnum measurementType) {
    this.measurementType = measurementType;
  }

  public InstrumentParameters measurementType(MeasurementTypeEnum measurementType) {
    this.measurementType = measurementType;
    return this;
  }

 /**
   * Sar
   * minimum: 0
   * @return dopplerFrequency
  **/
  @JsonProperty("dopplerFrequency")
  public BigDecimal getDopplerFrequency() {
    return dopplerFrequency;
  }

  public void setDopplerFrequency(BigDecimal dopplerFrequency) {
    this.dopplerFrequency = dopplerFrequency;
  }

  public InstrumentParameters dopplerFrequency(BigDecimal dopplerFrequency) {
    this.dopplerFrequency = dopplerFrequency;
    return this;
  }

 /**
   * Alt
   * @return samplingRates
  **/
  @JsonProperty("samplingRates")
  public List<BigDecimal> getSamplingRates() {
    return samplingRates;
  }

  public void setSamplingRates(List<BigDecimal> samplingRates) {
    this.samplingRates = samplingRates;
  }

  public InstrumentParameters samplingRates(List<BigDecimal> samplingRates) {
    this.samplingRates = samplingRates;
    return this;
  }

  public InstrumentParameters addSamplingRatesItem(BigDecimal samplingRatesItem) {
    this.samplingRates.add(samplingRatesItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InstrumentParameters {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    operationalMode: ").append(toIndentedString(operationalMode)).append("\n");
    sb.append("    swathIdentifier: ").append(toIndentedString(swathIdentifier)).append("\n");
    sb.append("    polarisationMode: ").append(toIndentedString(polarisationMode)).append("\n");
    sb.append("    polarisationChannels: ").append(toIndentedString(polarisationChannels)).append("\n");
    sb.append("    resolution: ").append(toIndentedString(resolution)).append("\n");
    sb.append("    verticalResolution: ").append(toIndentedString(verticalResolution)).append("\n");
    sb.append("    waveLengths: ").append(toIndentedString(waveLengths)).append("\n");
    sb.append("    measurementType: ").append(toIndentedString(measurementType)).append("\n");
    sb.append("    dopplerFrequency: ").append(toIndentedString(dopplerFrequency)).append("\n");
    sb.append("    samplingRates: ").append(toIndentedString(samplingRates)).append("\n");
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
