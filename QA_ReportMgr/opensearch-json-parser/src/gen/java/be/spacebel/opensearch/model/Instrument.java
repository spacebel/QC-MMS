package be.spacebel.opensearch.model;


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

public class Instrument  {
  public enum TypeEnum {
    INSTRUMENT("Instrument");

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
  private String id = null;
  public enum SensorTypeEnum {
    OPTICAL("OPTICAL"),
    RADAR("RADAR"),
    ATMOSPHERIC("ATMOSPHERIC"),
    ALTIMETRIC("ALTIMETRIC"),
    LIMB("LIMB");

    private String value;

    SensorTypeEnum(String value) {
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
    public static SensorTypeEnum fromValue(String text) {
      for (SensorTypeEnum b : SensorTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(description = "")
  private SensorTypeEnum sensorType = null;
  
  @Schema(required = true, description = "")
  private String instrumentShortName = null;
  
  @Schema(description = "")
  private String description = null;
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

  public Instrument type(TypeEnum type) {
    this.type = type;
    return this;
  }

 /**
   * Get id
   * @return id
  **/
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Instrument id(String id) {
    this.id = id;
    return this;
  }

 /**
   * Get sensorType
   * @return sensorType
  **/
  @JsonProperty("sensorType")
  public String getSensorType() {
    if (sensorType == null) {
      return null;
    }
    return sensorType.getValue();
  }

  public void setSensorType(SensorTypeEnum sensorType) {
    this.sensorType = sensorType;
  }

  public Instrument sensorType(SensorTypeEnum sensorType) {
    this.sensorType = sensorType;
    return this;
  }

 /**
   * Get instrumentShortName
   * @return instrumentShortName
  **/
  @JsonProperty("instrumentShortName")
  public String getInstrumentShortName() {
    return instrumentShortName;
  }

  public void setInstrumentShortName(String instrumentShortName) {
    this.instrumentShortName = instrumentShortName;
  }

  public Instrument instrumentShortName(String instrumentShortName) {
    this.instrumentShortName = instrumentShortName;
    return this;
  }

 /**
   * Get description
   * @return description
  **/
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Instrument description(String description) {
    this.description = description;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Instrument {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    sensorType: ").append(toIndentedString(sensorType)).append("\n");
    sb.append("    instrumentShortName: ").append(toIndentedString(instrumentShortName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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
