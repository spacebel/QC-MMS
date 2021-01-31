package be.spacebel.opensearch.model;

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
  * Offering as defined in OGC 14-055r2
 **/
@Schema(description="Offering as defined in OGC 14-055r2")
public class Offering  {
  
  @Schema(required = true, description = "")
  private String code = null;
  
  @Schema(description = "")
  private List<Object> operations = null;
  
  @Schema(description = "")
  private List<Object> contents = null;
  
  @Schema(description = "")
  private List<Object> styles = null;
 /**
   * Get code
   * @return code
  **/
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Offering code(String code) {
    this.code = code;
    return this;
  }

 /**
   * Get operations
   * @return operations
  **/
  @JsonProperty("operations")
  public List<Object> getOperations() {
    return operations;
  }

  public void setOperations(List<Object> operations) {
    this.operations = operations;
  }

  public Offering operations(List<Object> operations) {
    this.operations = operations;
    return this;
  }

  public Offering addOperationsItem(Object operationsItem) {
    this.operations.add(operationsItem);
    return this;
  }

 /**
   * Get contents
   * @return contents
  **/
  @JsonProperty("contents")
  public List<Object> getContents() {
    return contents;
  }

  public void setContents(List<Object> contents) {
    this.contents = contents;
  }

  public Offering contents(List<Object> contents) {
    this.contents = contents;
    return this;
  }

  public Offering addContentsItem(Object contentsItem) {
    this.contents.add(contentsItem);
    return this;
  }

 /**
   * Get styles
   * @return styles
  **/
  @JsonProperty("styles")
  public List<Object> getStyles() {
    return styles;
  }

  public void setStyles(List<Object> styles) {
    this.styles = styles;
  }

  public Offering styles(List<Object> styles) {
    this.styles = styles;
    return this;
  }

  public Offering addStylesItem(Object stylesItem) {
    this.styles.add(stylesItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Offering {\n");
    
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    operations: ").append(toIndentedString(operations)).append("\n");
    sb.append("    contents: ").append(toIndentedString(contents)).append("\n");
    sb.append("    styles: ").append(toIndentedString(styles)).append("\n");
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
