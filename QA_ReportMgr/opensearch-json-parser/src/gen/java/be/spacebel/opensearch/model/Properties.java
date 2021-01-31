package be.spacebel.opensearch.model;

import be.spacebel.opensearch.model.Agent;
import be.spacebel.opensearch.model.Category;
import be.spacebel.opensearch.model.CommonProperties;
import be.spacebel.opensearch.model.Links;
import java.time.OffsetDateTime;
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

public class Properties extends CommonProperties {
  
  @Schema(required = true, description = "OGC 14-055r2")
 /**
   * OGC 14-055r2  
  **/
  private String lang = null;
  
  @Schema(description = "OGC 14-055r2")
 /**
   * OGC 14-055r2  
  **/
  private String subtitle = null;
  
  @Schema(description = "OGC 14-055r2")
 /**
   * OGC 14-055r2  
  **/
  private String creator = null;
  
  @Schema(required = true, description = "")
  private Links links = null;
  
  @Schema(description = "")
  private Agent generator = null;
 /**
   * OGC 14-055r2
   * @return lang
  **/
  @JsonProperty("lang")
  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public Properties lang(String lang) {
    this.lang = lang;
    return this;
  }

 /**
   * OGC 14-055r2
   * @return subtitle
  **/
  @JsonProperty("subtitle")
  public String getSubtitle() {
    return subtitle;
  }

  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }

  public Properties subtitle(String subtitle) {
    this.subtitle = subtitle;
    return this;
  }

 /**
   * OGC 14-055r2
   * @return creator
  **/
  @JsonProperty("creator")
  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public Properties creator(String creator) {
    this.creator = creator;
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

  public Properties links(Links links) {
    this.links = links;
    return this;
  }

 /**
   * Get generator
   * @return generator
  **/
  @JsonProperty("generator")
  public Agent getGenerator() {
    return generator;
  }

  public void setGenerator(Agent generator) {
    this.generator = generator;
  }

  public Properties generator(Agent generator) {
    this.generator = generator;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Properties {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    lang: ").append(toIndentedString(lang)).append("\n");
    sb.append("    subtitle: ").append(toIndentedString(subtitle)).append("\n");
    sb.append("    creator: ").append(toIndentedString(creator)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
    sb.append("    generator: ").append(toIndentedString(generator)).append("\n");
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
