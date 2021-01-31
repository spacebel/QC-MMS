package be.spacebel.opensearch.model;

import io.swagger.v3.oas.annotations.media.Schema;
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

/**
  * Subset of ProductInformation
 **/
@Schema(description="Subset of ProductInformation")
public class CoverageDescription  {
  
  @Schema(description = "")
  private BigDecimal cloudCover = null;
  
  @Schema(description = "")
  private BigDecimal snowCover = null;
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

  public CoverageDescription cloudCover(BigDecimal cloudCover) {
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

  public CoverageDescription snowCover(BigDecimal snowCover) {
    this.snowCover = snowCover;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CoverageDescription {\n");
    
    sb.append("    cloudCover: ").append(toIndentedString(cloudCover)).append("\n");
    sb.append("    snowCover: ").append(toIndentedString(snowCover)).append("\n");
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
