package be.spacebel.opensearch.model;

import be.spacebel.opensearch.model.OrdinaryControlInformationProperty;
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
  * Results of QCMMS check 3
 **/
@Schema(description="Results of QCMMS check 3")
public class OrdinaryControlInformation extends QualityMeasurement {
  
  @Schema(description = "")
  private OrdinaryControlInformationProperty level1 = null;
  
  @Schema(description = "")
  private OrdinaryControlInformationProperty level2 = null;
 /**
   * Get level1
   * @return level1
  **/
  @JsonProperty("level1")
  public OrdinaryControlInformationProperty getLevel1() {
    return level1;
  }

  public void setLevel1(OrdinaryControlInformationProperty level1) {
    this.level1 = level1;
  }

  public OrdinaryControlInformation level1(OrdinaryControlInformationProperty level1) {
    this.level1 = level1;
    return this;
  }

 /**
   * Get level2
   * @return level2
  **/
  @JsonProperty("level2")
  public OrdinaryControlInformationProperty getLevel2() {
    return level2;
  }

  public void setLevel2(OrdinaryControlInformationProperty level2) {
    this.level2 = level2;
  }

  public OrdinaryControlInformation level2(OrdinaryControlInformationProperty level2) {
    this.level2 = level2;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrdinaryControlInformation {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    level1: ").append(toIndentedString(level1)).append("\n");
    sb.append("    level2: ").append(toIndentedString(level2)).append("\n");
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
