package be.spacebel.opensearch.model;

import be.spacebel.opensearch.model.QualityIndicator;
import be.spacebel.opensearch.model.QualityInformationDef;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
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
  * Extended QualityInformation defined in OGC 17-003
 **/
@Schema(description="Extended QualityInformation defined in OGC 17-003")
public class QualityInformation extends QualityInformationDef {
  
  @Schema(description = "")
  private List<QualityIndicator> qualityIndicators = null;
 /**
   * Get qualityIndicators
   * @return qualityIndicators
  **/
  @JsonProperty("qualityIndicators")
  public List<QualityIndicator> getQualityIndicators() {
    return qualityIndicators;
  }

  public void setQualityIndicators(List<QualityIndicator> qualityIndicators) {
    this.qualityIndicators = qualityIndicators;
  }

  public QualityInformation qualityIndicators(List<QualityIndicator> qualityIndicators) {
    this.qualityIndicators = qualityIndicators;
    return this;
  }

  public QualityInformation addQualityIndicatorsItem(QualityIndicator qualityIndicatorsItem) {
    this.qualityIndicators.add(qualityIndicatorsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QualityInformation {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    qualityIndicators: ").append(toIndentedString(qualityIndicators)).append("\n");
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
