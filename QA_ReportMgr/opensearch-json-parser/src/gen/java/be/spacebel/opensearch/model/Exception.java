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
  * OGC 06-121r9
 **/
@Schema(description="OGC 06-121r9")
public class Exception  {
  
  @Schema(required = true, description = "Represents ows:exceptionCode")
 /**
   * Represents ows:exceptionCode  
  **/
  private String exceptionCode = null;
  
  @Schema(description = "Represents ows:exceptionText")
 /**
   * Represents ows:exceptionText  
  **/
  private String exceptionText = null;
  
  @Schema(description = "Represents ows:locator.")
 /**
   * Represents ows:locator.  
  **/
  private String locator = null;
 /**
   * Represents ows:exceptionCode
   * @return exceptionCode
  **/
  @JsonProperty("exceptionCode")
  public String getExceptionCode() {
    return exceptionCode;
  }

  public void setExceptionCode(String exceptionCode) {
    this.exceptionCode = exceptionCode;
  }

  public Exception exceptionCode(String exceptionCode) {
    this.exceptionCode = exceptionCode;
    return this;
  }

 /**
   * Represents ows:exceptionText
   * @return exceptionText
  **/
  @JsonProperty("exceptionText")
  public String getExceptionText() {
    return exceptionText;
  }

  public void setExceptionText(String exceptionText) {
    this.exceptionText = exceptionText;
  }

  public Exception exceptionText(String exceptionText) {
    this.exceptionText = exceptionText;
    return this;
  }

 /**
   * Represents ows:locator.
   * @return locator
  **/
  @JsonProperty("locator")
  public String getLocator() {
    return locator;
  }

  public void setLocator(String locator) {
    this.locator = locator;
  }

  public Exception locator(String locator) {
    this.locator = locator;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Exception {\n");
    
    sb.append("    exceptionCode: ").append(toIndentedString(exceptionCode)).append("\n");
    sb.append("    exceptionText: ").append(toIndentedString(exceptionText)).append("\n");
    sb.append("    locator: ").append(toIndentedString(locator)).append("\n");
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
