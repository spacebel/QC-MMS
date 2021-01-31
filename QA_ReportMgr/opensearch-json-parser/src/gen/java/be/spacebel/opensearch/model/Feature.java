package be.spacebel.opensearch.model;

import be.spacebel.opensearch.model.Properties_;
import io.swagger.v3.oas.annotations.media.Schema;
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

/**
  * GeoJSON Feature
 **/
@Schema(description="GeoJSON Feature")
public class Feature  {
  public enum TypeEnum {
    FEATURE("Feature");

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
  @Schema(required = true, description = "")
  private TypeEnum type = null;
  
  @Schema(required = true, description = "")
  private String id = null;
  
  @Schema(description = "")
  private Object geometry = null;
  
  @Schema(description = "")
  private List<BigDecimal> bbox = null;
  
  @Schema(required = true, description = "")
  private Properties_ properties = null;
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

  public Feature type(TypeEnum type) {
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

  public Feature id(String id) {
    this.id = id;
    return this;
  }

 /**
   * Get geometry
   * @return geometry
  **/
  @JsonProperty("geometry")
  public Object getGeometry() {
    return geometry;
  }

  public void setGeometry(Object geometry) {
    this.geometry = geometry;
  }

  public Feature geometry(Object geometry) {
    this.geometry = geometry;
    return this;
  }

 /**
   * Get bbox
   * @return bbox
  **/
  @JsonProperty("bbox")
  public List<BigDecimal> getBbox() {
    return bbox;
  }

  public void setBbox(List<BigDecimal> bbox) {
    this.bbox = bbox;
  }

  public Feature bbox(List<BigDecimal> bbox) {
    this.bbox = bbox;
    return this;
  }

  public Feature addBboxItem(BigDecimal bboxItem) {
    this.bbox.add(bboxItem);
    return this;
  }

 /**
   * Get properties
   * @return properties
  **/
  @JsonProperty("properties")
  public Properties_ getProperties() {
    return properties;
  }

  public void setProperties(Properties_ properties) {
    this.properties = properties;
  }

  public Feature properties(Properties_ properties) {
    this.properties = properties;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Feature {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    geometry: ").append(toIndentedString(geometry)).append("\n");
    sb.append("    bbox: ").append(toIndentedString(bbox)).append("\n");
    sb.append("    properties: ").append(toIndentedString(properties)).append("\n");
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
