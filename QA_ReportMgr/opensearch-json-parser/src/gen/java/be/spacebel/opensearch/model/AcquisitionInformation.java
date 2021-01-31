package be.spacebel.opensearch.model;

import be.spacebel.opensearch.model.AcquisitionParameters;
import be.spacebel.opensearch.model.Instrument;
import be.spacebel.opensearch.model.InstrumentParameters;
import be.spacebel.opensearch.model.Platform;

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

public class AcquisitionInformation  {
  public enum TypeEnum {
    ACQUISITIONINFORMATION("AcquisitionInformation");

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
  private Platform platform = null;
  
  @Schema(description = "")
  private Instrument instrument = null;
  
  @Schema(description = "")
  private InstrumentParameters instrumentParameters = null;
  
  @Schema(description = "")
  private AcquisitionParameters acquisitionParameters = null;
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

  public AcquisitionInformation type(TypeEnum type) {
    this.type = type;
    return this;
  }

 /**
   * Get platform
   * @return platform
  **/
  @JsonProperty("platform")
  public Platform getPlatform() {
    return platform;
  }

  public void setPlatform(Platform platform) {
    this.platform = platform;
  }

  public AcquisitionInformation platform(Platform platform) {
    this.platform = platform;
    return this;
  }

 /**
   * Get instrument
   * @return instrument
  **/
  @JsonProperty("instrument")
  public Instrument getInstrument() {
    return instrument;
  }

  public void setInstrument(Instrument instrument) {
    this.instrument = instrument;
  }

  public AcquisitionInformation instrument(Instrument instrument) {
    this.instrument = instrument;
    return this;
  }

 /**
   * Get instrumentParameters
   * @return instrumentParameters
  **/
  @JsonProperty("instrumentParameters")
  public InstrumentParameters getInstrumentParameters() {
    return instrumentParameters;
  }

  public void setInstrumentParameters(InstrumentParameters instrumentParameters) {
    this.instrumentParameters = instrumentParameters;
  }

  public AcquisitionInformation instrumentParameters(InstrumentParameters instrumentParameters) {
    this.instrumentParameters = instrumentParameters;
    return this;
  }

 /**
   * Get acquisitionParameters
   * @return acquisitionParameters
  **/
  @JsonProperty("acquisitionParameters")
  public AcquisitionParameters getAcquisitionParameters() {
    return acquisitionParameters;
  }

  public void setAcquisitionParameters(AcquisitionParameters acquisitionParameters) {
    this.acquisitionParameters = acquisitionParameters;
  }

  public AcquisitionInformation acquisitionParameters(AcquisitionParameters acquisitionParameters) {
    this.acquisitionParameters = acquisitionParameters;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AcquisitionInformation {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    platform: ").append(toIndentedString(platform)).append("\n");
    sb.append("    instrument: ").append(toIndentedString(instrument)).append("\n");
    sb.append("    instrumentParameters: ").append(toIndentedString(instrumentParameters)).append("\n");
    sb.append("    acquisitionParameters: ").append(toIndentedString(acquisitionParameters)).append("\n");
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
