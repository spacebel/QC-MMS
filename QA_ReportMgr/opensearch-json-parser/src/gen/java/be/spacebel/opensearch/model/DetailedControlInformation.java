package be.spacebel.opensearch.model;

import be.spacebel.opensearch.model.QualityMeasurement;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

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
  * Results of QCMMS check 4
 **/
@Schema(description="Results of QCMMS check 4")
public class DetailedControlInformation extends QualityMeasurement {
  
  @Schema(description = "")
  private String attribute3 = null;
 /**
   * Get attribute3
   * @return attribute3
  **/
  @JsonProperty("attribute3")
  public String getAttribute3() {
    return attribute3;
  }

  public void setAttribute3(String attribute3) {
    this.attribute3 = attribute3;
  }

  public DetailedControlInformation attribute3(String attribute3) {
    this.attribute3 = attribute3;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DetailedControlInformation {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    attribute3: ").append(toIndentedString(attribute3)).append("\n");
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
