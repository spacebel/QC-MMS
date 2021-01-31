package be.spacebel.opensearch.model;

import be.spacebel.opensearch.model.Agent;
import be.spacebel.opensearch.model.Category;
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

public class CommonProperties  {
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
  
  @Schema(required = true, description = "OGC 14-055r2")
 /**
   * OGC 14-055r2  
  **/
  private String title = null;
  
  @Schema(required = true, description = "OGC 14-055r2")
 /**
   * OGC 14-055r2  
  **/
  private OffsetDateTime updated = null;
  
  @Schema(description = "OGC 14-055r2")
 /**
   * OGC 14-055r2  
  **/
  private String date = null;
  
  @Schema(description = "OGC 14-055r2")
 /**
   * OGC 14-055r2  
  **/
  private String publisher = null;
  
  @Schema(description = "OGC 14-055r2")
 /**
   * OGC 14-055r2  
  **/
  private String rights = null;
  
  @Schema(description = "OGC 14-055r2")
 /**
   * OGC 14-055r2  
  **/
  private List<Agent> authors = null;
  
  @Schema(description = "OGC 14-055r2")
 /**
   * OGC 14-055r2  
  **/
  private List<Category> categories = null;
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

  public CommonProperties type(TypeEnum type) {
    this.type = type;
    return this;
  }

 /**
   * OGC 14-055r2
   * @return title
  **/
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public CommonProperties title(String title) {
    this.title = title;
    return this;
  }

 /**
   * OGC 14-055r2
   * @return updated
  **/
  @JsonProperty("updated")
  public OffsetDateTime getUpdated() {
    return updated;
  }

  public void setUpdated(OffsetDateTime updated) {
    this.updated = updated;
  }

  public CommonProperties updated(OffsetDateTime updated) {
    this.updated = updated;
    return this;
  }

 /**
   * OGC 14-055r2
   * @return date
  **/
  @JsonProperty("date")
  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public CommonProperties date(String date) {
    this.date = date;
    return this;
  }

 /**
   * OGC 14-055r2
   * @return publisher
  **/
  @JsonProperty("publisher")
  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public CommonProperties publisher(String publisher) {
    this.publisher = publisher;
    return this;
  }

 /**
   * OGC 14-055r2
   * @return rights
  **/
  @JsonProperty("rights")
  public String getRights() {
    return rights;
  }

  public void setRights(String rights) {
    this.rights = rights;
  }

  public CommonProperties rights(String rights) {
    this.rights = rights;
    return this;
  }

 /**
   * OGC 14-055r2
   * @return authors
  **/
  @JsonProperty("authors")
  public List<Agent> getAuthors() {
    return authors;
  }

  public void setAuthors(List<Agent> authors) {
    this.authors = authors;
  }

  public CommonProperties authors(List<Agent> authors) {
    this.authors = authors;
    return this;
  }

  public CommonProperties addAuthorsItem(Agent authorsItem) {
    this.authors.add(authorsItem);
    return this;
  }

 /**
   * OGC 14-055r2
   * @return categories
  **/
  @JsonProperty("categories")
  public List<Category> getCategories() {
    return categories;
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
  }

  public CommonProperties categories(List<Category> categories) {
    this.categories = categories;
    return this;
  }

  public CommonProperties addCategoriesItem(Category categoriesItem) {
    this.categories.add(categoriesItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CommonProperties {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    updated: ").append(toIndentedString(updated)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    publisher: ").append(toIndentedString(publisher)).append("\n");
    sb.append("    rights: ").append(toIndentedString(rights)).append("\n");
    sb.append("    authors: ").append(toIndentedString(authors)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
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
