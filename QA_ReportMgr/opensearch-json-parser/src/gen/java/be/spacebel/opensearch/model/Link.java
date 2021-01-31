package be.spacebel.opensearch.model;

import io.swagger.v3.oas.annotations.media.Schema;

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
  * OGC 14-055r2
 **/
@Schema(description="OGC 14-055r2")
public class Link  {
  
  @Schema(required = true, description = "")
  private String href = null;
  
  @Schema(description = "MIME type")
 /**
   * MIME type  
  **/
  private String type = null;
  
  @Schema(description = "")
  private String title = null;
  
  @Schema(description = "")
  private Integer length = null;
  
  @Schema(description = "RFC-3066")
 /**
   * RFC-3066  
  **/
  private String lang = null;
 /**
   * Get href
   * @return href
  **/
  @JsonProperty("href")
  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public Link href(String href) {
    this.href = href;
    return this;
  }

 /**
   * MIME type
   * @return type
  **/
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Link type(String type) {
    this.type = type;
    return this;
  }

 /**
   * Get title
   * @return title
  **/
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Link title(String title) {
    this.title = title;
    return this;
  }

 /**
   * Get length
   * minimum: 0
   * @return length
  **/
  @JsonProperty("length")
  public Integer getLength() {
    return length;
  }

  public void setLength(Integer length) {
    this.length = length;
  }

  public Link length(Integer length) {
    this.length = length;
    return this;
  }

 /**
   * RFC-3066
   * @return lang
  **/
  @JsonProperty("lang")
  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public Link lang(String lang) {
    this.lang = lang;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Link {\n");
    
    sb.append("    href: ").append(toIndentedString(href)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    length: ").append(toIndentedString(length)).append("\n");
    sb.append("    lang: ").append(toIndentedString(lang)).append("\n");
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
