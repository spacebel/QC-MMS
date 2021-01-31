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
  * dqv:QualityMeasurement
 **/
@Schema(description="dqv:QualityMeasurement")
public class QualityMeasurement  {
  public enum TypeEnum {
    QUALITYMEASUREMENT("QualityMeasurement");

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
  public enum IsMeasurementOfEnum {
    DEGRADEDANCILLARYDATAPERCENTAGEMETRIC("http://qcmms.esa.int/quality-indicators/#degradedAncillaryDataPercentageMetric"),
    SENSORQUALITYMETRIC("http://qcmms.esa.int/quality-indicators/#sensorQualityMetric"),
    RADIOMETRICQUALITYMETRIC("http://qcmms.esa.int/quality-indicators/#radiometricQualityMetric"),
    DEGRADEDDATAPERCENTAGEMETRIC("http://qcmms.esa.int/quality-indicators/#degradedDataPercentageMetric"),
    GEOMETRICQUALITYMETRIC("http://qcmms.esa.int/quality-indicators/#geometricQualityMetric"),
    GENERALQUALITYMETRIC("http://qcmms.esa.int/quality-indicators/#generalQualityMetric"),
    FORMATCORRECTNESSMETRIC("http://qcmms.esa.int/quality-indicators/#formatCorrectnessMetric"),
    FEASIBILITYCONTROLMETRIC("http://qcmms.esa.int/quality-indicators/#feasibilityControlMetric"),
    DELIVERYCONTROLMETRIC("http://qcmms.esa.int/quality-indicators/#deliveryControlMetric"),
    ORDINARYCONTROLMETRIC("http://qcmms.esa.int/quality-indicators/#ordinaryControlMetric"),
    DETAILEDCONTROLMETRIC("http://qcmms.esa.int/quality-indicators/#detailedControlMetric"),
    HARMONIZATIONCONTROLMETRIC("http://qcmms.esa.int/quality-indicators/#harmonizationControlMetric"),
    IPFORLPINFORMATIONMETRIC("http://qcmms.esa.int/quality-indicators/#ipForLpInformationMetric"),
    LPINTERPRETATIONMETRIC("http://qcmms.esa.int/quality-indicators/#lpInterpretationMetric"),
    LPMETADATACONTROLMETRIC("http://qcmms.esa.int/quality-indicators/#lpMetadataControlMetric"),
    LPORDINARYCONTROLMETRIC("http://qcmms.esa.int/quality-indicators/#lpOrdinaryControlMetric"),
    LPTHEMATICVALIDATIONMETRIC("http://qcmms.esa.int/quality-indicators/#lpThematicValidationMetric");
    

    private String value;

    IsMeasurementOfEnum(String value) {
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
    public static IsMeasurementOfEnum fromValue(String text) {
      for (IsMeasurementOfEnum b : IsMeasurementOfEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(required = true, description = "dqv:isMeasurementOf")
 /**
   * dqv:isMeasurementOf  
  **/
  private IsMeasurementOfEnum isMeasurementOf = null;
  
  @Schema(required = true, description = "dqv:value")
 /**
   * dqv:value  
  **/
  private Object value = null;
  
  @Schema(description = "prov:generatedAtTime")
 /**
   * prov:generatedAtTime  
  **/
  private OffsetDateTime generatedAtTime = null;
  
  
  private String lineage;
  
  /**
   * lineage
   * @return lineage
  **/
  @JsonProperty("lineage")
  public Object getLineage() {
    return lineage;
  }

  public void setLineage(String newLineage) {
    this.lineage = newLineage;
  }

  public QualityMeasurement lineage(String newLineage) {
    this.lineage = newLineage;
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

  public QualityMeasurement type(TypeEnum type) {
    this.type = type;
    return this;
  }

 /**
   * dqv:isMeasurementOf
   * @return isMeasurementOf
  **/
  @JsonProperty("isMeasurementOf")
  public String getIsMeasurementOf() {
    if (isMeasurementOf == null) {
      return null;
    }
    return isMeasurementOf.getValue();
  }

  public void setIsMeasurementOf(IsMeasurementOfEnum isMeasurementOf) {
    this.isMeasurementOf = isMeasurementOf;
  }

  public QualityMeasurement isMeasurementOf(IsMeasurementOfEnum isMeasurementOf) {
    this.isMeasurementOf = isMeasurementOf;
    return this;
  }

 /**
   * dqv:value
   * @return value
  **/
  @JsonProperty("value")
  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public QualityMeasurement value(Object value) {
    this.value = value;
    return this;
  }

 /**
   * prov:generatedAtTime
   * @return generatedAtTime
  **/
  @JsonProperty("generatedAtTime")
  public OffsetDateTime getGeneratedAtTime() {
    return generatedAtTime;
  }

  public void setGeneratedAtTime(OffsetDateTime generatedAtTime) {
    this.generatedAtTime = generatedAtTime;
  }

  public QualityMeasurement generatedAtTime(OffsetDateTime generatedAtTime) {
    this.generatedAtTime = generatedAtTime;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QualityMeasurement {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    isMeasurementOf: ").append(toIndentedString(isMeasurementOf)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    generatedAtTime: ").append(toIndentedString(generatedAtTime)).append("\n");
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
