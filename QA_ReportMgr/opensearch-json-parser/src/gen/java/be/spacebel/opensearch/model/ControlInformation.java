package be.spacebel.opensearch.model;

import be.spacebel.opensearch.model.Queries;

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

public class ControlInformation  {
  
  @Schema(required = true, description = "")
  private Integer totalResults = null;
  
  @Schema(required = true, description = "")
  private Integer startIndex = null;
  
  @Schema(required = true, description = "")
  private Integer itemsPerPage = null;
  
  @Schema(description = "")
  private Queries queries = null;
 /**
   * Get totalResults
   * minimum: 0
   * @return totalResults
  **/
  @JsonProperty("totalResults")
  public Integer getTotalResults() {
    return totalResults;
  }

  public void setTotalResults(Integer totalResults) {
    this.totalResults = totalResults;
  }

  public ControlInformation totalResults(Integer totalResults) {
    this.totalResults = totalResults;
    return this;
  }

 /**
   * Get startIndex
   * minimum: 0
   * @return startIndex
  **/
  @JsonProperty("startIndex")
  public Integer getStartIndex() {
    return startIndex;
  }

  public void setStartIndex(Integer startIndex) {
    this.startIndex = startIndex;
  }

  public ControlInformation startIndex(Integer startIndex) {
    this.startIndex = startIndex;
    return this;
  }

 /**
   * Get itemsPerPage
   * minimum: 0
   * @return itemsPerPage
  **/
  @JsonProperty("itemsPerPage")
  public Integer getItemsPerPage() {
    return itemsPerPage;
  }

  public void setItemsPerPage(Integer itemsPerPage) {
    this.itemsPerPage = itemsPerPage;
  }

  public ControlInformation itemsPerPage(Integer itemsPerPage) {
    this.itemsPerPage = itemsPerPage;
    return this;
  }

 /**
   * Get queries
   * @return queries
  **/
  @JsonProperty("queries")
  public Queries getQueries() {
    return queries;
  }

  public void setQueries(Queries queries) {
    this.queries = queries;
  }

  public ControlInformation queries(Queries queries) {
    this.queries = queries;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ControlInformation {\n");
    
    sb.append("    totalResults: ").append(toIndentedString(totalResults)).append("\n");
    sb.append("    startIndex: ").append(toIndentedString(startIndex)).append("\n");
    sb.append("    itemsPerPage: ").append(toIndentedString(itemsPerPage)).append("\n");
    sb.append("    queries: ").append(toIndentedString(queries)).append("\n");
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
