package be.spacebel.opensearch.model;

import be.spacebel.opensearch.model.CoverageDescription;
import be.spacebel.opensearch.model.ProcessingInformation;
import be.spacebel.opensearch.model.QualityInformation;
import java.math.BigDecimal;
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

public class ProductInformation extends ProcessingInformation {
  
  @Schema(description = "")
  private BigDecimal cloudCover = null;
  
  @Schema(description = "")
  private BigDecimal snowCover = null;
  public enum TypeEnum {
    PRODUCTINFORMATION("ProductInformation");

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
  private String productType = null;
  
  @Schema(description = "")
  private Integer size = null;
  
  @Schema(description = "")
  private String productVersion = null;
  public enum StatusSubTypeEnum {
    ON_LINE("ON-LINE"),
    OFF_LINE("OFF-LINE");

    private String value;

    StatusSubTypeEnum(String value) {
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
    public static StatusSubTypeEnum fromValue(String text) {
      for (StatusSubTypeEnum b : StatusSubTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(description = "")
  private StatusSubTypeEnum statusSubType = null;
  
  @Schema(description = "")
  private QualityInformation qualityInformation = null;
  
  @Schema(description = "")
  private String statusDetail = null;
  
  @Schema(required = true, description = "")
  private OffsetDateTime availabilityTime = null;
  
  @Schema(description = "")
  private String timeliness = null;
  
  @Schema(description = "")
  private String productGroupId = null;
  
  @Schema(description = "")
  private String archivingCenter = null;
  
  @Schema(description = "")
  private String referenceSystemIdentifier = null;
  
  @Schema(description = "")
  private OffsetDateTime archivingDate = null;
 /**
   * Get cloudCover
   * @return cloudCover
  **/
  @JsonProperty("cloudCover")
  public BigDecimal getCloudCover() {
    return cloudCover;
  }

  public void setCloudCover(BigDecimal cloudCover) {
    this.cloudCover = cloudCover;
  }

  public ProductInformation cloudCover(BigDecimal cloudCover) {
    this.cloudCover = cloudCover;
    return this;
  }

 /**
   * Get snowCover
   * @return snowCover
  **/
  @JsonProperty("snowCover")
  public BigDecimal getSnowCover() {
    return snowCover;
  }

  public void setSnowCover(BigDecimal snowCover) {
    this.snowCover = snowCover;
  }

  public ProductInformation snowCover(BigDecimal snowCover) {
    this.snowCover = snowCover;
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

  public ProductInformation type(TypeEnum type) {
    this.type = type;
    return this;
  }

 /**
   * Get productType
   * @return productType
  **/
  @JsonProperty("productType")
  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public ProductInformation productType(String productType) {
    this.productType = productType;
    return this;
  }

 /**
   * Get size
   * @return size
  **/
  @JsonProperty("size")
  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public ProductInformation size(Integer size) {
    this.size = size;
    return this;
  }

 /**
   * Get productVersion
   * @return productVersion
  **/
  @JsonProperty("productVersion")
  public String getProductVersion() {
    return productVersion;
  }

  public void setProductVersion(String productVersion) {
    this.productVersion = productVersion;
  }

  public ProductInformation productVersion(String productVersion) {
    this.productVersion = productVersion;
    return this;
  }

 /**
   * Get statusSubType
   * @return statusSubType
  **/
  @JsonProperty("statusSubType")
  public String getStatusSubType() {
    if (statusSubType == null) {
      return null;
    }
    return statusSubType.getValue();
  }

  public void setStatusSubType(StatusSubTypeEnum statusSubType) {
    this.statusSubType = statusSubType;
  }

  public ProductInformation statusSubType(StatusSubTypeEnum statusSubType) {
    this.statusSubType = statusSubType;
    return this;
  }

 /**
   * Get qualityInformation
   * @return qualityInformation
  **/
  @JsonProperty("qualityInformation")
  public QualityInformation getQualityInformation() {
    return qualityInformation;
  }

  public void setQualityInformation(QualityInformation qualityInformation) {
    this.qualityInformation = qualityInformation;
  }

  public ProductInformation qualityInformation(QualityInformation qualityInformation) {
    this.qualityInformation = qualityInformation;
    return this;
  }

 /**
   * Get statusDetail
   * @return statusDetail
  **/
  @JsonProperty("statusDetail")
  public String getStatusDetail() {
    return statusDetail;
  }

  public void setStatusDetail(String statusDetail) {
    this.statusDetail = statusDetail;
  }

  public ProductInformation statusDetail(String statusDetail) {
    this.statusDetail = statusDetail;
    return this;
  }

 /**
   * Get availabilityTime
   * @return availabilityTime
  **/
  @JsonProperty("availabilityTime")
  public OffsetDateTime getAvailabilityTime() {
    return availabilityTime;
  }

  public void setAvailabilityTime(OffsetDateTime availabilityTime) {
    this.availabilityTime = availabilityTime;
  }

  public ProductInformation availabilityTime(OffsetDateTime availabilityTime) {
    this.availabilityTime = availabilityTime;
    return this;
  }

 /**
   * Get timeliness
   * @return timeliness
  **/
  @JsonProperty("timeliness")
  public String getTimeliness() {
    return timeliness;
  }

  public void setTimeliness(String timeliness) {
    this.timeliness = timeliness;
  }

  public ProductInformation timeliness(String timeliness) {
    this.timeliness = timeliness;
    return this;
  }

 /**
   * Get productGroupId
   * @return productGroupId
  **/
  @JsonProperty("productGroupId")
  public String getProductGroupId() {
    return productGroupId;
  }

  public void setProductGroupId(String productGroupId) {
    this.productGroupId = productGroupId;
  }

  public ProductInformation productGroupId(String productGroupId) {
    this.productGroupId = productGroupId;
    return this;
  }

 /**
   * Get archivingCenter
   * @return archivingCenter
  **/
  @JsonProperty("archivingCenter")
  public String getArchivingCenter() {
    return archivingCenter;
  }

  public void setArchivingCenter(String archivingCenter) {
    this.archivingCenter = archivingCenter;
  }

  public ProductInformation archivingCenter(String archivingCenter) {
    this.archivingCenter = archivingCenter;
    return this;
  }

 /**
   * Get referenceSystemIdentifier
   * @return referenceSystemIdentifier
  **/
  @JsonProperty("referenceSystemIdentifier")
  public String getReferenceSystemIdentifier() {
    return referenceSystemIdentifier;
  }

  public void setReferenceSystemIdentifier(String referenceSystemIdentifier) {
    this.referenceSystemIdentifier = referenceSystemIdentifier;
  }

  public ProductInformation referenceSystemIdentifier(String referenceSystemIdentifier) {
    this.referenceSystemIdentifier = referenceSystemIdentifier;
    return this;
  }

 /**
   * Get archivingDate
   * @return archivingDate
  **/
  @JsonProperty("archivingDate")
  public OffsetDateTime getArchivingDate() {
    return archivingDate;
  }

  public void setArchivingDate(OffsetDateTime archivingDate) {
    this.archivingDate = archivingDate;
  }

  public ProductInformation archivingDate(OffsetDateTime archivingDate) {
    this.archivingDate = archivingDate;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProductInformation {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    cloudCover: ").append(toIndentedString(cloudCover)).append("\n");
    sb.append("    snowCover: ").append(toIndentedString(snowCover)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    productType: ").append(toIndentedString(productType)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    productVersion: ").append(toIndentedString(productVersion)).append("\n");
    sb.append("    statusSubType: ").append(toIndentedString(statusSubType)).append("\n");
    sb.append("    qualityInformation: ").append(toIndentedString(qualityInformation)).append("\n");
    sb.append("    statusDetail: ").append(toIndentedString(statusDetail)).append("\n");
    sb.append("    availabilityTime: ").append(toIndentedString(availabilityTime)).append("\n");
    sb.append("    timeliness: ").append(toIndentedString(timeliness)).append("\n");
    sb.append("    productGroupId: ").append(toIndentedString(productGroupId)).append("\n");
    sb.append("    archivingCenter: ").append(toIndentedString(archivingCenter)).append("\n");
    sb.append("    referenceSystemIdentifier: ").append(toIndentedString(referenceSystemIdentifier)).append("\n");
    sb.append("    archivingDate: ").append(toIndentedString(archivingDate)).append("\n");
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
