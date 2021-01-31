package be.spacebel.opensearch.model;

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
  * Subset of ProductInformation
 **/
@Schema(description="Subset of ProductInformation")
public class ProcessingInformation  {
  public enum ProcessingLevelEnum {
    _1A("1A"),
    _1B("1B"),
    _1C("1C"),
    _2("2"),
    _3("3");

    private String value;

    ProcessingLevelEnum(String value) {
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
    public static ProcessingLevelEnum fromValue(String text) {
      for (ProcessingLevelEnum b : ProcessingLevelEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(description = "")
  private ProcessingLevelEnum processingLevel = null;
  
  @Schema(description = "")
  private String processorName = null;
  
  @Schema(description = "")
  private String processorVersion = null;
  
  @Schema(description = "Codelist")
 /**
   * Codelist  
  **/
  private String processingCenter = null;
  
  @Schema(description = "")
  private OffsetDateTime processingDate = null;
  
  @Schema(description = "Codelist")
 /**
   * Codelist  
  **/
  private String processingMode = null;
  
  @Schema(description = "")
  private String compositeType = null;
  
  @Schema(description = "")
  private String format = null;
  
  @Schema(description = "")
  private String productContentsType = null;
  
  @Schema(description = "")
  private String processingMethod = null;
  
  @Schema(description = "")
  private String processingMethodVersion = null;
 /**
   * Get processingLevel
   * @return processingLevel
  **/
  @JsonProperty("processingLevel")
  public String getProcessingLevel() {
    if (processingLevel == null) {
      return null;
    }
    return processingLevel.getValue();
  }

  public void setProcessingLevel(ProcessingLevelEnum processingLevel) {
    this.processingLevel = processingLevel;
  }

  public ProcessingInformation processingLevel(ProcessingLevelEnum processingLevel) {
    this.processingLevel = processingLevel;
    return this;
  }

 /**
   * Get processorName
   * @return processorName
  **/
  @JsonProperty("processorName")
  public String getProcessorName() {
    return processorName;
  }

  public void setProcessorName(String processorName) {
    this.processorName = processorName;
  }

  public ProcessingInformation processorName(String processorName) {
    this.processorName = processorName;
    return this;
  }

 /**
   * Get processorVersion
   * @return processorVersion
  **/
  @JsonProperty("processorVersion")
  public String getProcessorVersion() {
    return processorVersion;
  }

  public void setProcessorVersion(String processorVersion) {
    this.processorVersion = processorVersion;
  }

  public ProcessingInformation processorVersion(String processorVersion) {
    this.processorVersion = processorVersion;
    return this;
  }

 /**
   * Codelist
   * @return processingCenter
  **/
  @JsonProperty("processingCenter")
  public String getProcessingCenter() {
    return processingCenter;
  }

  public void setProcessingCenter(String processingCenter) {
    this.processingCenter = processingCenter;
  }

  public ProcessingInformation processingCenter(String processingCenter) {
    this.processingCenter = processingCenter;
    return this;
  }

 /**
   * Get processingDate
   * @return processingDate
  **/
  @JsonProperty("processingDate")
  public OffsetDateTime getProcessingDate() {
    return processingDate;
  }

  public void setProcessingDate(OffsetDateTime processingDate) {
    this.processingDate = processingDate;
  }

  public ProcessingInformation processingDate(OffsetDateTime processingDate) {
    this.processingDate = processingDate;
    return this;
  }

 /**
   * Codelist
   * @return processingMode
  **/
  @JsonProperty("processingMode")
  public String getProcessingMode() {
    return processingMode;
  }

  public void setProcessingMode(String processingMode) {
    this.processingMode = processingMode;
  }

  public ProcessingInformation processingMode(String processingMode) {
    this.processingMode = processingMode;
    return this;
  }

 /**
   * Get compositeType
   * @return compositeType
  **/
  @JsonProperty("compositeType")
  public String getCompositeType() {
    return compositeType;
  }

  public void setCompositeType(String compositeType) {
    this.compositeType = compositeType;
  }

  public ProcessingInformation compositeType(String compositeType) {
    this.compositeType = compositeType;
    return this;
  }

 /**
   * Get format
   * @return format
  **/
  @JsonProperty("format")
  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public ProcessingInformation format(String format) {
    this.format = format;
    return this;
  }

 /**
   * Get productContentsType
   * @return productContentsType
  **/
  @JsonProperty("productContentsType")
  public String getProductContentsType() {
    return productContentsType;
  }

  public void setProductContentsType(String productContentsType) {
    this.productContentsType = productContentsType;
  }

  public ProcessingInformation productContentsType(String productContentsType) {
    this.productContentsType = productContentsType;
    return this;
  }

 /**
   * Get processingMethod
   * @return processingMethod
  **/
  @JsonProperty("processingMethod")
  public String getProcessingMethod() {
    return processingMethod;
  }

  public void setProcessingMethod(String processingMethod) {
    this.processingMethod = processingMethod;
  }

  public ProcessingInformation processingMethod(String processingMethod) {
    this.processingMethod = processingMethod;
    return this;
  }

 /**
   * Get processingMethodVersion
   * @return processingMethodVersion
  **/
  @JsonProperty("processingMethodVersion")
  public String getProcessingMethodVersion() {
    return processingMethodVersion;
  }

  public void setProcessingMethodVersion(String processingMethodVersion) {
    this.processingMethodVersion = processingMethodVersion;
  }

  public ProcessingInformation processingMethodVersion(String processingMethodVersion) {
    this.processingMethodVersion = processingMethodVersion;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProcessingInformation {\n");
    
    sb.append("    processingLevel: ").append(toIndentedString(processingLevel)).append("\n");
    sb.append("    processorName: ").append(toIndentedString(processorName)).append("\n");
    sb.append("    processorVersion: ").append(toIndentedString(processorVersion)).append("\n");
    sb.append("    processingCenter: ").append(toIndentedString(processingCenter)).append("\n");
    sb.append("    processingDate: ").append(toIndentedString(processingDate)).append("\n");
    sb.append("    processingMode: ").append(toIndentedString(processingMode)).append("\n");
    sb.append("    compositeType: ").append(toIndentedString(compositeType)).append("\n");
    sb.append("    format: ").append(toIndentedString(format)).append("\n");
    sb.append("    productContentsType: ").append(toIndentedString(productContentsType)).append("\n");
    sb.append("    processingMethod: ").append(toIndentedString(processingMethod)).append("\n");
    sb.append("    processingMethodVersion: ").append(toIndentedString(processingMethodVersion)).append("\n");
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
