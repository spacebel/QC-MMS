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

public class Platform  {
  public enum TypeEnum {
    PLATFORM("Platform");

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
  
  @Schema(required = true, description = "")
  private String platformShortName = null;
  
  @Schema(description = "")
  private String platformSerialIdentifier = null;
  public enum OrbitTypeEnum {
    GEO("GEO"),
    LEO("LEO");

    private String value;

    OrbitTypeEnum(String value) {
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
    public static OrbitTypeEnum fromValue(String text) {
      for (OrbitTypeEnum b : OrbitTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(description = "")
  private OrbitTypeEnum orbitType = null;
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

  public Platform type(TypeEnum type) {
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

  public Platform id(String id) {
    this.id = id;
    return this;
  }

 /**
   * Get platformShortName
   * @return platformShortName
  **/
  @JsonProperty("platformShortName")
  public String getPlatformShortName() {
    return platformShortName;
  }

  public void setPlatformShortName(String platformShortName) {
    this.platformShortName = platformShortName;
  }

  public Platform platformShortName(String platformShortName) {
    this.platformShortName = platformShortName;
    return this;
  }

 /**
   * Get platformSerialIdentifier
   * @return platformSerialIdentifier
  **/
  @JsonProperty("platformSerialIdentifier")
  public String getPlatformSerialIdentifier() {
    return platformSerialIdentifier;
  }

  public void setPlatformSerialIdentifier(String platformSerialIdentifier) {
    this.platformSerialIdentifier = platformSerialIdentifier;
  }

  public Platform platformSerialIdentifier(String platformSerialIdentifier) {
    this.platformSerialIdentifier = platformSerialIdentifier;
    return this;
  }

 /**
   * Get orbitType
   * @return orbitType
  **/
  @JsonProperty("orbitType")
  public String getOrbitType() {
    if (orbitType == null) {
      return null;
    }
    return orbitType.getValue();
  }

  public void setOrbitType(OrbitTypeEnum orbitType) {
    this.orbitType = orbitType;
  }

  public Platform orbitType(OrbitTypeEnum orbitType) {
    this.orbitType = orbitType;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Platform {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    platformShortName: ").append(toIndentedString(platformShortName)).append("\n");
    sb.append("    platformSerialIdentifier: ").append(toIndentedString(platformSerialIdentifier)).append("\n");
    sb.append("    orbitType: ").append(toIndentedString(orbitType)).append("\n");
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
