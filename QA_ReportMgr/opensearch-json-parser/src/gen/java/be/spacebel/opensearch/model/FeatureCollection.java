package be.spacebel.opensearch.model;

import be.spacebel.opensearch.model.ControlInformation;
import be.spacebel.opensearch.model.Exception;
import be.spacebel.opensearch.model.Feature;
import be.spacebel.opensearch.model.Properties;
import be.spacebel.opensearch.model.Queries;
import io.swagger.v3.oas.annotations.media.Schema;
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
  * GeoJSON FeatureCollection
 **/
@Schema(description="GeoJSON FeatureCollection")
public class FeatureCollection extends ControlInformation {
  
  @Schema(description = "")
  private String context = null;
  public enum TypeEnum {
    FEATURECOLLECTION("FeatureCollection");

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
  @Schema(required = true, description = "OGC 14-055r2")
 /**
   * OGC 14-055r2  
  **/
  private TypeEnum type = null;
  
  @Schema(required = true, description = "OGC 14-055r2")
 /**
   * OGC 14-055r2  
  **/
  private String id = null;
  
  @Schema(description = "")
  private List<Object> bbox = null;
  
  @Schema(description = "")
  private Properties properties = null;
  
  @Schema(required = true, description = "")
  private List<Feature> features = new ArrayList<>();
  
  @Schema(description = "")
  private List<Exception> exceptions = null;
 /**
   * Get @Context
   * @return @Context
  **/
  @JsonProperty("@context")
  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public FeatureCollection context(String context) {
    this.context = context;
    return this;
  }

 /**
   * OGC 14-055r2
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

  public FeatureCollection type(TypeEnum type) {
    this.type = type;
    return this;
  }

 /**
   * OGC 14-055r2
   * @return id
  **/
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public FeatureCollection id(String id) {
    this.id = id;
    return this;
  }

 /**
   * Get bbox
   * @return bbox
  **/
  @JsonProperty("bbox")
  public List<Object> getBbox() {
    return bbox;
  }

  public void setBbox(List<Object> bbox) {
    this.bbox = bbox;
  }

  public FeatureCollection bbox(List<Object> bbox) {
    this.bbox = bbox;
    return this;
  }

  public FeatureCollection addBboxItem(Object bboxItem) {
    this.bbox.add(bboxItem);
    return this;
  }

 /**
   * Get properties
   * @return properties
  **/
  @JsonProperty("properties")
  public Properties getProperties() {
    return properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  public FeatureCollection properties(Properties properties) {
    this.properties = properties;
    return this;
  }

 /**
   * Get features
   * @return features
  **/
  @JsonProperty("features")
  public List<Feature> getFeatures() {
    return features;
  }

  public void setFeatures(List<Feature> features) {
    this.features = features;
  }

  public FeatureCollection features(List<Feature> features) {
    this.features = features;
    return this;
  }

  public FeatureCollection addFeaturesItem(Feature featuresItem) {
    this.features.add(featuresItem);
    return this;
  }

 /**
   * Get exceptions
   * @return exceptions
  **/
  @JsonProperty("exceptions")
  public List<Exception> getExceptions() {
    return exceptions;
  }

  public void setExceptions(List<Exception> exceptions) {
    this.exceptions = exceptions;
  }

  public FeatureCollection exceptions(List<Exception> exceptions) {
    this.exceptions = exceptions;
    return this;
  }

  public FeatureCollection addExceptionsItem(Exception exceptionsItem) {
    this.exceptions.add(exceptionsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FeatureCollection {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    @Context: ").append(toIndentedString(context)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    bbox: ").append(toIndentedString(bbox)).append("\n");
    sb.append("    properties: ").append(toIndentedString(properties)).append("\n");
    sb.append("    features: ").append(toIndentedString(features)).append("\n");
    sb.append("    exceptions: ").append(toIndentedString(exceptions)).append("\n");
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
