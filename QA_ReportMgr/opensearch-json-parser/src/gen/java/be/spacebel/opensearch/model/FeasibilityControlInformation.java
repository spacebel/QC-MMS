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
  * Results of QCMMS check 1
 **/
@Schema(description="Results of QCMMS check 1")
public class FeasibilityControlInformation extends QualityMeasurement {
  
  @Schema(description = "")
  private OffsetDateTime searchDate = null;
  
  @Schema(description = "")
  private OffsetDateTime searchDateUpdate = null;
 /**
   * Get searchDate
   * @return searchDate
  **/
  @JsonProperty("searchDate")
  public OffsetDateTime getSearchDate() {
    return searchDate;
  }

  public void setSearchDate(OffsetDateTime searchDate) {
    this.searchDate = searchDate;
  }

  public FeasibilityControlInformation searchDate(OffsetDateTime searchDate) {
    this.searchDate = searchDate;
    return this;
  }

 /**
   * Get searchDateUpdate
   * @return searchDateUpdate
  **/
  @JsonProperty("searchDateUpdate")
  public OffsetDateTime getSearchDateUpdate() {
    return searchDateUpdate;
  }

  public void setSearchDateUpdate(OffsetDateTime searchDateUpdate) {
    this.searchDateUpdate = searchDateUpdate;
  }

  public FeasibilityControlInformation searchDateUpdate(OffsetDateTime searchDateUpdate) {
    this.searchDateUpdate = searchDateUpdate;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FeasibilityControlInformation {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    searchDate: ").append(toIndentedString(searchDate)).append("\n");
    sb.append("    searchDateUpdate: ").append(toIndentedString(searchDateUpdate)).append("\n");
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
