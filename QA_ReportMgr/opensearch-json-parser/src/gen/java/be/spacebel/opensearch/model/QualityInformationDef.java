package be.spacebel.opensearch.model;

import java.math.BigDecimal;

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

public class QualityInformationDef  {
  public enum QualityStatusEnum {
    NOMINAL("NOMINAL"),
    DEGRADED("DEGRADED");

    private String value;

    QualityStatusEnum(String value) {
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
    public static QualityStatusEnum fromValue(String text) {
      for (QualityStatusEnum b : QualityStatusEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(description = "")
  private QualityStatusEnum qualityStatus = null;
  
  @Schema(description = "Percentage")
 /**
   * Percentage  
  **/
  private BigDecimal qualityDegradation = null;
  
  @Schema(description = "")
  private String qualityDegradationTag = null;
  public enum QualityDegradationQuotationModeEnum {
    AUTOMATIC("AUTOMATIC"),
    MANUAL("MANUAL");

    private String value;

    QualityDegradationQuotationModeEnum(String value) {
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
    public static QualityDegradationQuotationModeEnum fromValue(String text) {
      for (QualityDegradationQuotationModeEnum b : QualityDegradationQuotationModeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(description = "")
  private QualityDegradationQuotationModeEnum qualityDegradationQuotationMode = null;
 /**
   * Get qualityStatus
   * @return qualityStatus
  **/
  @JsonProperty("qualityStatus")
  public String getQualityStatus() {
    if (qualityStatus == null) {
      return null;
    }
    return qualityStatus.getValue();
  }

  public void setQualityStatus(QualityStatusEnum qualityStatus) {
    this.qualityStatus = qualityStatus;
  }

  public QualityInformationDef qualityStatus(QualityStatusEnum qualityStatus) {
    this.qualityStatus = qualityStatus;
    return this;
  }

 /**
   * Percentage
   * @return qualityDegradation
  **/
  @JsonProperty("qualityDegradation")
  public BigDecimal getQualityDegradation() {
    return qualityDegradation;
  }

  public void setQualityDegradation(BigDecimal qualityDegradation) {
    this.qualityDegradation = qualityDegradation;
  }

  public QualityInformationDef qualityDegradation(BigDecimal qualityDegradation) {
    this.qualityDegradation = qualityDegradation;
    return this;
  }

 /**
   * Get qualityDegradationTag
   * @return qualityDegradationTag
  **/
  @JsonProperty("qualityDegradationTag")
  public String getQualityDegradationTag() {
    return qualityDegradationTag;
  }

  public void setQualityDegradationTag(String qualityDegradationTag) {
    this.qualityDegradationTag = qualityDegradationTag;
  }

  public QualityInformationDef qualityDegradationTag(String qualityDegradationTag) {
    this.qualityDegradationTag = qualityDegradationTag;
    return this;
  }

 /**
   * Get qualityDegradationQuotationMode
   * @return qualityDegradationQuotationMode
  **/
  @JsonProperty("qualityDegradationQuotationMode")
  public String getQualityDegradationQuotationMode() {
    if (qualityDegradationQuotationMode == null) {
      return null;
    }
    return qualityDegradationQuotationMode.getValue();
  }

  public void setQualityDegradationQuotationMode(QualityDegradationQuotationModeEnum qualityDegradationQuotationMode) {
    this.qualityDegradationQuotationMode = qualityDegradationQuotationMode;
  }

  public QualityInformationDef qualityDegradationQuotationMode(QualityDegradationQuotationModeEnum qualityDegradationQuotationMode) {
    this.qualityDegradationQuotationMode = qualityDegradationQuotationMode;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QualityInformationDef {\n");
    
    sb.append("    qualityStatus: ").append(toIndentedString(qualityStatus)).append("\n");
    sb.append("    qualityDegradation: ").append(toIndentedString(qualityDegradation)).append("\n");
    sb.append("    qualityDegradationTag: ").append(toIndentedString(qualityDegradationTag)).append("\n");
    sb.append("    qualityDegradationQuotationMode: ").append(toIndentedString(qualityDegradationQuotationMode)).append("\n");
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
