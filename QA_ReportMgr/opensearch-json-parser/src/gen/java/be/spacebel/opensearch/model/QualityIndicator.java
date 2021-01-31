package be.spacebel.opensearch.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

public class QualityIndicator extends QualityMeasurement {
  
  @Schema(description = "")
  private OffsetDateTime searchDate = null;
  
  @Schema(description = "")
  private OffsetDateTime searchDateUpdate = null;
  public enum StatusEnum {
    WAITING("WAITING"),
    IN_PROGRESS("IN_PROGRESS"),
    FINISHED("FINISHED"),
    NOT_AVAILABLE("NOT_AVAILABLE");

    private String value;

    StatusEnum(String value) {
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
    public static StatusEnum fromValue(String text) {
      for (StatusEnum b : StatusEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(description = "")
  private StatusEnum status = null;
  
  @Schema(description = "")
  private Boolean complete = null;
  
  @Schema(description = "")
  private OffsetDateTime date = null;
  
  @Schema(description = "")
  private String filename = null;
  
  @Schema(description = "")
  private OrdinaryControlInformationProperty level1 = null;
  
  @Schema(description = "")
  private OrdinaryControlInformationProperty level2 = null;
  
  @Schema(description = "")
  private String attribute3 = null;
  
  protected Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> any() {
      return this.additionalProperties;
    }

    @JsonAnySetter
    public void set(String name, Object value) {
      this.additionalProperties.put(name, value);
    }

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

  public QualityIndicator searchDate(OffsetDateTime searchDate) {
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

  public QualityIndicator searchDateUpdate(OffsetDateTime searchDateUpdate) {
    this.searchDateUpdate = searchDateUpdate;
    return this;
  }

 /**
   * Get status
   * @return status
  **/
  @JsonProperty("status")
  public String getStatus() {
    if (status == null) {
      return null;
    }
    return status.getValue();
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public QualityIndicator status(StatusEnum status) {
    this.status = status;
    return this;
  }

 /**
   * Get complete
   * @return complete
  **/
  @JsonProperty("complete")
  public Boolean isisComplete() {
    return complete;
  }

  public void setComplete(Boolean complete) {
    this.complete = complete;
  }

  public QualityIndicator complete(Boolean complete) {
    this.complete = complete;
    return this;
  }

 /**
   * Get date
   * @return date
  **/
  @JsonProperty("date")
  public OffsetDateTime getDate() {
    return date;
  }

  public void setDate(OffsetDateTime date) {
    this.date = date;
  }

  public QualityIndicator date(OffsetDateTime date) {
    this.date = date;
    return this;
  }

 /**
   * Get filename
   * @return filename
  **/
  @JsonProperty("filename")
  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public QualityIndicator filename(String filename) {
    this.filename = filename;
    return this;
  }

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

  public QualityIndicator level1(OrdinaryControlInformationProperty level1) {
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

  public QualityIndicator level2(OrdinaryControlInformationProperty level2) {
    this.level2 = level2;
    return this;
  }

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

  public QualityIndicator attribute3(String attribute3) {
    this.attribute3 = attribute3;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QualityIndicator {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    searchDate: ").append(toIndentedString(searchDate)).append("\n");
    sb.append("    searchDateUpdate: ").append(toIndentedString(searchDateUpdate)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    complete: ").append(toIndentedString(complete)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    filename: ").append(toIndentedString(filename)).append("\n");
    sb.append("    level1: ").append(toIndentedString(level1)).append("\n");
    sb.append("    level2: ").append(toIndentedString(level2)).append("\n");
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
