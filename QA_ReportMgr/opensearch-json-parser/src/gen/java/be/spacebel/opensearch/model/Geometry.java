package be.spacebel.opensearch.model;

import be.spacebel.opensearch.model.LineString;
import be.spacebel.opensearch.model.MultiLineString;
import be.spacebel.opensearch.model.MultiPoint;
import be.spacebel.opensearch.model.MultiPolygon;
import be.spacebel.opensearch.model.Point;
import be.spacebel.opensearch.model.Polygon;
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

public class Geometry  {
  
  @Schema(required = true, description = "Array of Polygons")
 /**
   * Array of Polygons  
  **/
  private List<List<List<List<BigDecimal>>>> coordinates = new ArrayList<>();
  public enum TypeEnum {
    MULTIPOLYGON("MultiPolygon");

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
 /**
   * Array of Polygons
   * @return coordinates
  **/
  @JsonProperty("coordinates")
  public List<List<List<List<BigDecimal>>>> getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(List<List<List<List<BigDecimal>>>> coordinates) {
    this.coordinates = coordinates;
  }

  public Geometry coordinates(List<List<List<List<BigDecimal>>>> coordinates) {
    this.coordinates = coordinates;
    return this;
  }

  public Geometry addCoordinatesItem(List<List<List<BigDecimal>>> coordinatesItem) {
    this.coordinates.add(coordinatesItem);
    return this;
  }

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

  public Geometry type(TypeEnum type) {
    this.type = type;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Geometry {\n");
    
    sb.append("    coordinates: ").append(toIndentedString(coordinates)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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
