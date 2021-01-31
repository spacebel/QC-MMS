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
  * RFC4287 §3.2 and OGC 14-055r2 §7.1.1.7, §7.1.8
 **/
@Schema(description="RFC4287 §3.2 and OGC 14-055r2 §7.1.1.7, §7.1.8")
public class Agent  {
  public enum TypeEnum {
    AGENT("Agent"),
    PERSON("Person"),
    ORGANIZATION("Organization");

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
  private String name = null;
  
  @Schema(description = "")
  private String email = null;
  
  @Schema(description = "")
  private String uri = null;
  
  @Schema(description = "OGC 14-055r2 §7.1.8")
 /**
   * OGC 14-055r2 §7.1.8  
  **/
  private String title = null;
  
  @Schema(description = "OGC 14-055r2 §7.1.8")
 /**
   * OGC 14-055r2 §7.1.8  
  **/
  private String version = null;
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

  public Agent type(TypeEnum type) {
    this.type = type;
    return this;
  }

 /**
   * Get name
   * @return name
  **/
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Agent name(String name) {
    this.name = name;
    return this;
  }

 /**
   * Get email
   * @return email
  **/
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Agent email(String email) {
    this.email = email;
    return this;
  }

 /**
   * Get uri
   * @return uri
  **/
  @JsonProperty("uri")
  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public Agent uri(String uri) {
    this.uri = uri;
    return this;
  }

 /**
   * OGC 14-055r2 §7.1.8
   * @return title
  **/
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Agent title(String title) {
    this.title = title;
    return this;
  }

 /**
   * OGC 14-055r2 §7.1.8
   * @return version
  **/
  @JsonProperty("version")
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Agent version(String version) {
    this.version = version;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Agent {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
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
