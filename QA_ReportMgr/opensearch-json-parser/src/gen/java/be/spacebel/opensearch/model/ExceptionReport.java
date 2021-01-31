package be.spacebel.opensearch.model;

import be.spacebel.opensearch.model.Exception;
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
  * OGC 06-121r9
 **/
@Schema(description="OGC 06-121r9")
public class ExceptionReport  {
  public enum TypeEnum {
    EXCEPTIONREPORT("ExceptionReport");

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
  private List<Exception> exceptions = null;
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

  public ExceptionReport type(TypeEnum type) {
    this.type = type;
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

  public ExceptionReport exceptions(List<Exception> exceptions) {
    this.exceptions = exceptions;
    return this;
  }

  public ExceptionReport addExceptionsItem(Exception exceptionsItem) {
    this.exceptions.add(exceptionsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExceptionReport {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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
