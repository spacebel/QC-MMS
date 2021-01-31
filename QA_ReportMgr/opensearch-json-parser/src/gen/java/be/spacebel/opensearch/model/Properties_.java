package be.spacebel.opensearch.model;

import be.spacebel.opensearch.model.AcquisitionInformation;
import be.spacebel.opensearch.model.DataIdentification;
import be.spacebel.opensearch.model.Links;
import be.spacebel.opensearch.model.Offering;
import be.spacebel.opensearch.model.ProductInformation;
import java.time.OffsetDateTime;
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

public class Properties_ extends DataIdentification {
  public enum TypeEnum {
    PROPERTIES("Properties");

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
  private String productId = null;
  public enum StatusEnum {
    ARCHIVED("ARCHIVED"),
    PLANNED("PLANNED"),
    ACQUIRED("ACQUIRED"),
    CANCELLED("CANCELLED"),
    FAILED("FAILED"),
    POTENTIAL("POTENTIAL"),
    REJECTED("REJECTED"),
    QUALITYDEGRADED("QUALITYDEGRADED");

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
  @Schema(required = true, description = "")
  private StatusEnum status = null;
  
  @Schema(required = true, description = "")
  private List<AcquisitionInformation> acquisitionInformation = new ArrayList<>();
  
  @Schema(description = "")
  private ProductInformation productInformation = null;
  
  @Schema(required = true, description = "")
  private Links links = null;
  
  @Schema(description = "")
  private String kind = null;
  
  @Schema(description = "OGC 14-055r2")
 /**
   * OGC 14-055r2  
  **/
  private List<Offering> offerings = null;
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

  public Properties_ type(TypeEnum type) {
    this.type = type;
    return this;
  }

 /**
   * Get productId
   * @return productId
  **/
  @JsonProperty("productId")
  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public Properties_ productId(String productId) {
    this.productId = productId;
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

  public Properties_ status(StatusEnum status) {
    this.status = status;
    return this;
  }

 /**
   * Get acquisitionInformation
   * @return acquisitionInformation
  **/
  @JsonProperty("acquisitionInformation")
  public List<AcquisitionInformation> getAcquisitionInformation() {
    return acquisitionInformation;
  }

  public void setAcquisitionInformation(List<AcquisitionInformation> acquisitionInformation) {
    this.acquisitionInformation = acquisitionInformation;
  }

  public Properties_ acquisitionInformation(List<AcquisitionInformation> acquisitionInformation) {
    this.acquisitionInformation = acquisitionInformation;
    return this;
  }

  public Properties_ addAcquisitionInformationItem(AcquisitionInformation acquisitionInformationItem) {
    this.acquisitionInformation.add(acquisitionInformationItem);
    return this;
  }

 /**
   * Get productInformation
   * @return productInformation
  **/
  @JsonProperty("productInformation")
  public ProductInformation getProductInformation() {
    return productInformation;
  }

  public void setProductInformation(ProductInformation productInformation) {
    this.productInformation = productInformation;
  }

  public Properties_ productInformation(ProductInformation productInformation) {
    this.productInformation = productInformation;
    return this;
  }

 /**
   * Get links
   * @return links
  **/
  @JsonProperty("links")
  public Links getLinks() {
    return links;
  }

  public void setLinks(Links links) {
    this.links = links;
  }

  public Properties_ links(Links links) {
    this.links = links;
    return this;
  }

 /**
   * Get kind
   * @return kind
  **/
  @JsonProperty("kind")
  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public Properties_ kind(String kind) {
    this.kind = kind;
    return this;
  }

 /**
   * OGC 14-055r2
   * @return offerings
  **/
  @JsonProperty("offerings")
  public List<Offering> getOfferings() {
    return offerings;
  }

  public void setOfferings(List<Offering> offerings) {
    this.offerings = offerings;
  }

  public Properties_ offerings(List<Offering> offerings) {
    this.offerings = offerings;
    return this;
  }

  public Properties_ addOfferingsItem(Offering offeringsItem) {
    this.offerings.add(offeringsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Properties_ {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    productId: ").append(toIndentedString(productId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    acquisitionInformation: ").append(toIndentedString(acquisitionInformation)).append("\n");
    sb.append("    productInformation: ").append(toIndentedString(productInformation)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
    sb.append("    kind: ").append(toIndentedString(kind)).append("\n");
    sb.append("    offerings: ").append(toIndentedString(offerings)).append("\n");
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
