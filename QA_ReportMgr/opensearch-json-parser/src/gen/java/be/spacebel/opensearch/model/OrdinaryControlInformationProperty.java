package be.spacebel.opensearch.model;

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

public class OrdinaryControlInformationProperty  {
  
  @Schema(description = "")
  private Boolean rastersComplete = null;
  
  @Schema(description = "")
  private Boolean calibrationMetadata = null;
  
  @Schema(description = "")
  private Boolean rastersRead = null;
  
  @Schema(description = "")
  private Boolean metadataRead = null;
  
  @Schema(description = "")
  private Integer bits = null;
  
  @Schema(description = "")
  private Integer columns = null;
  
  @Schema(description = "")
  private Integer rows = null;
  
  @Schema(description = "")
  private Integer channels = null;
  
  @Schema(description = "")
  private List<Object> bands = null;
  
  @Schema(description = "")
  private String format = null;
  
  @Schema(description = "")
  private Integer epsg = null;
  
  @Schema(description = "")
  private BigDecimal xGeoref = null;
  
  @Schema(description = "")
  private BigDecimal yGeoRef = null;
  
  @Schema(description = "")
  private BigDecimal xCellRes = null;
  
  @Schema(description = "")
  private BigDecimal yCellRes = null;
  public enum ResamplingEnum {
    CUBIC("CUBIC");

    private String value;

    ResamplingEnum(String value) {
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
    public static ResamplingEnum fromValue(String text) {
      for (ResamplingEnum b : ResamplingEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(description = "")
  private ResamplingEnum resampling = null;
  
  @Schema(description = "")
  private Boolean characteristicsCompliant = null;
 /**
   * Get rastersComplete
   * @return rastersComplete
  **/
  @JsonProperty("rastersComplete")
  public Boolean isisRastersComplete() {
    return rastersComplete;
  }

  public void setRastersComplete(Boolean rastersComplete) {
    this.rastersComplete = rastersComplete;
  }

  public OrdinaryControlInformationProperty rastersComplete(Boolean rastersComplete) {
    this.rastersComplete = rastersComplete;
    return this;
  }

 /**
   * Get calibrationMetadata
   * @return calibrationMetadata
  **/
  @JsonProperty("calibrationMetadata")
  public Boolean isisCalibrationMetadata() {
    return calibrationMetadata;
  }

  public void setCalibrationMetadata(Boolean calibrationMetadata) {
    this.calibrationMetadata = calibrationMetadata;
  }

  public OrdinaryControlInformationProperty calibrationMetadata(Boolean calibrationMetadata) {
    this.calibrationMetadata = calibrationMetadata;
    return this;
  }

 /**
   * Get rastersRead
   * @return rastersRead
  **/
  @JsonProperty("rastersRead")
  public Boolean isisRastersRead() {
    return rastersRead;
  }

  public void setRastersRead(Boolean rastersRead) {
    this.rastersRead = rastersRead;
  }

  public OrdinaryControlInformationProperty rastersRead(Boolean rastersRead) {
    this.rastersRead = rastersRead;
    return this;
  }

 /**
   * Get metadataRead
   * @return metadataRead
  **/
  @JsonProperty("metadataRead")
  public Boolean isisMetadataRead() {
    return metadataRead;
  }

  public void setMetadataRead(Boolean metadataRead) {
    this.metadataRead = metadataRead;
  }

  public OrdinaryControlInformationProperty metadataRead(Boolean metadataRead) {
    this.metadataRead = metadataRead;
    return this;
  }

 /**
   * Get bits
   * @return bits
  **/
  @JsonProperty("bits")
  public Integer getBits() {
    return bits;
  }

  public void setBits(Integer bits) {
    this.bits = bits;
  }

  public OrdinaryControlInformationProperty bits(Integer bits) {
    this.bits = bits;
    return this;
  }

 /**
   * Get columns
   * @return columns
  **/
  @JsonProperty("columns")
  public Integer getColumns() {
    return columns;
  }

  public void setColumns(Integer columns) {
    this.columns = columns;
  }

  public OrdinaryControlInformationProperty columns(Integer columns) {
    this.columns = columns;
    return this;
  }

 /**
   * Get rows
   * @return rows
  **/
  @JsonProperty("rows")
  public Integer getRows() {
    return rows;
  }

  public void setRows(Integer rows) {
    this.rows = rows;
  }

  public OrdinaryControlInformationProperty rows(Integer rows) {
    this.rows = rows;
    return this;
  }

 /**
   * Get channels
   * @return channels
  **/
  @JsonProperty("channels")
  public Integer getChannels() {
    return channels;
  }

  public void setChannels(Integer channels) {
    this.channels = channels;
  }

  public OrdinaryControlInformationProperty channels(Integer channels) {
    this.channels = channels;
    return this;
  }

 /**
   * Get bands
   * @return bands
  **/
  @JsonProperty("bands")
  public List<Object> getBands() {
    return bands;
  }

  public void setBands(List<Object> bands) {
    this.bands = bands;
  }

  public OrdinaryControlInformationProperty bands(List<Object> bands) {
    this.bands = bands;
    return this;
  }

  public OrdinaryControlInformationProperty addBandsItem(Object bandsItem) {
    this.bands.add(bandsItem);
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

  public OrdinaryControlInformationProperty format(String format) {
    this.format = format;
    return this;
  }

 /**
   * Get epsg
   * @return epsg
  **/
  @JsonProperty("epsg")
  public Integer getEpsg() {
    return epsg;
  }

  public void setEpsg(Integer epsg) {
    this.epsg = epsg;
  }

  public OrdinaryControlInformationProperty epsg(Integer epsg) {
    this.epsg = epsg;
    return this;
  }

 /**
   * Get xGeoref
   * @return xGeoref
  **/
  @JsonProperty("xGeoref")
  public BigDecimal getXGeoref() {
    return xGeoref;
  }

  public void setXGeoref(BigDecimal xGeoref) {
    this.xGeoref = xGeoref;
  }

  public OrdinaryControlInformationProperty xGeoref(BigDecimal xGeoref) {
    this.xGeoref = xGeoref;
    return this;
  }

 /**
   * Get yGeoRef
   * @return yGeoRef
  **/
  @JsonProperty("yGeoRef")
  public BigDecimal getYGeoRef() {
    return yGeoRef;
  }

  public void setYGeoRef(BigDecimal yGeoRef) {
    this.yGeoRef = yGeoRef;
  }

  public OrdinaryControlInformationProperty yGeoRef(BigDecimal yGeoRef) {
    this.yGeoRef = yGeoRef;
    return this;
  }

 /**
   * Get xCellRes
   * @return xCellRes
  **/
  @JsonProperty("xCellRes")
  public BigDecimal getXCellRes() {
    return xCellRes;
  }

  public void setXCellRes(BigDecimal xCellRes) {
    this.xCellRes = xCellRes;
  }

  public OrdinaryControlInformationProperty xCellRes(BigDecimal xCellRes) {
    this.xCellRes = xCellRes;
    return this;
  }

 /**
   * Get yCellRes
   * @return yCellRes
  **/
  @JsonProperty("yCellRes")
  public BigDecimal getYCellRes() {
    return yCellRes;
  }

  public void setYCellRes(BigDecimal yCellRes) {
    this.yCellRes = yCellRes;
  }

  public OrdinaryControlInformationProperty yCellRes(BigDecimal yCellRes) {
    this.yCellRes = yCellRes;
    return this;
  }

 /**
   * Get resampling
   * @return resampling
  **/
  @JsonProperty("resampling")
  public String getResampling() {
    if (resampling == null) {
      return null;
    }
    return resampling.getValue();
  }

  public void setResampling(ResamplingEnum resampling) {
    this.resampling = resampling;
  }

  public OrdinaryControlInformationProperty resampling(ResamplingEnum resampling) {
    this.resampling = resampling;
    return this;
  }

 /**
   * Get characteristicsCompliant
   * @return characteristicsCompliant
  **/
  @JsonProperty("characteristicsCompliant")
  public Boolean isisCharacteristicsCompliant() {
    return characteristicsCompliant;
  }

  public void setCharacteristicsCompliant(Boolean characteristicsCompliant) {
    this.characteristicsCompliant = characteristicsCompliant;
  }

  public OrdinaryControlInformationProperty characteristicsCompliant(Boolean characteristicsCompliant) {
    this.characteristicsCompliant = characteristicsCompliant;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrdinaryControlInformationProperty {\n");
    
    sb.append("    rastersComplete: ").append(toIndentedString(rastersComplete)).append("\n");
    sb.append("    calibrationMetadata: ").append(toIndentedString(calibrationMetadata)).append("\n");
    sb.append("    rastersRead: ").append(toIndentedString(rastersRead)).append("\n");
    sb.append("    metadataRead: ").append(toIndentedString(metadataRead)).append("\n");
    sb.append("    bits: ").append(toIndentedString(bits)).append("\n");
    sb.append("    columns: ").append(toIndentedString(columns)).append("\n");
    sb.append("    rows: ").append(toIndentedString(rows)).append("\n");
    sb.append("    channels: ").append(toIndentedString(channels)).append("\n");
    sb.append("    bands: ").append(toIndentedString(bands)).append("\n");
    sb.append("    format: ").append(toIndentedString(format)).append("\n");
    sb.append("    epsg: ").append(toIndentedString(epsg)).append("\n");
    sb.append("    xGeoref: ").append(toIndentedString(xGeoref)).append("\n");
    sb.append("    yGeoRef: ").append(toIndentedString(yGeoRef)).append("\n");
    sb.append("    xCellRes: ").append(toIndentedString(xCellRes)).append("\n");
    sb.append("    yCellRes: ").append(toIndentedString(yCellRes)).append("\n");
    sb.append("    resampling: ").append(toIndentedString(resampling)).append("\n");
    sb.append("    characteristicsCompliant: ").append(toIndentedString(characteristicsCompliant)).append("\n");
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
