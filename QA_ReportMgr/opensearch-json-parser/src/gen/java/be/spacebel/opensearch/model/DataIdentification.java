package be.spacebel.opensearch.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Subset of Properties
 *
 */
@Schema(description = "Subset of Properties")
public class DataIdentification {

    protected Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> any() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void set(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Schema(description = "")
    private String parentIdentifier = null;

    @Schema(description = "")
    private String collection = null;

    @Schema(description = "")
    private String doi = null;

    @Schema(required = true, description = "")
    private String title = null;

    @Schema(required = true, description = "")
    private String identifier = null;

    @Schema(required = true, description = "")
    private String date = null;

    @Schema(description = "")
    private String _abstract = null;

    @Schema(description = "")
    private Object distribution = null;

    @Schema(description = "")
    private Object provenance = null;

    @Schema(description = "")
    private Object wasUsedBy = null;

    @Schema(required = true, description = "")
    private OffsetDateTime updated = null;

    @Schema(description = "")
    private Object additionalAttributes = null;

    /**
     * Get parentIdentifier
     *
     * @return parentIdentifier
  *
     */
    @JsonProperty("parentIdentifier")
    public String getParentIdentifier() {
        return parentIdentifier;
    }

    public void setParentIdentifier(String parentIdentifier) {
        this.parentIdentifier = parentIdentifier;
    }

    public DataIdentification parentIdentifier(String parentIdentifier) {
        this.parentIdentifier = parentIdentifier;
        return this;
    }

    /**
     * Get collection
     *
     * @return parentIdentifier
  *
     */
    @JsonProperty("collection")
    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public DataIdentification collection(String collection) {
        this.collection = collection;
        return this;
    }

    /**
     * Get doi
     *
     * @return doi
  *
     */
    @JsonProperty("doi")
    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public DataIdentification doi(String doi) {
        this.doi = doi;
        return this;
    }

    /**
     * Get title
     *
     * @return title
  *
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DataIdentification title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Get identifier
     *
     * @return identifier
  *
     */
    @JsonProperty("identifier")
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public DataIdentification identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    /**
     * Get date
     *
     * @return date
  *
     */
    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public DataIdentification date(String date) {
        this.date = date;
        return this;
    }

    /**
     * Get _abstract
     *
     * @return _abstract
  *
     */
    @JsonProperty("abstract")
    public String getAbstract() {
        return _abstract;
    }

    public void setAbstract(String _abstract) {
        this._abstract = _abstract;
    }

    public DataIdentification _abstract(String _abstract) {
        this._abstract = _abstract;
        return this;
    }

    /**
     * Get distribution
     *
     * @return distribution
  *
     */
    @JsonProperty("distribution")
    public Object getDistribution() {
        return distribution;
    }

    public void setDistribution(Object distribution) {
        this.distribution = distribution;
    }

    public DataIdentification distribution(Object distribution) {
        this.distribution = distribution;
        return this;
    }

    /**
     * Get provenance
     *
     * @return provenance
  *
     */
    @JsonProperty("provenance")
    public Object getProvenance() {
        return provenance;
    }

    public void setProvenance(Object provenance) {
        this.provenance = provenance;
    }

    public DataIdentification provenance(Object provenance) {
        this.provenance = provenance;
        return this;
    }

    /**
     * Get wasUsedBy
     *
     * @return wasUsedBy
  *
     */
    @JsonProperty("wasUsedBy")
    public Object getWasUsedBy() {
        return wasUsedBy;
    }

    public void setWasUsedBy(Object wasUsedBy) {
        this.wasUsedBy = wasUsedBy;
    }

    public DataIdentification wasUsedBy(Object wasUsedBy) {
        this.wasUsedBy = wasUsedBy;
        return this;
    }

    /**
     * Get updated
     *
     * @return updated
  *
     */
    @JsonProperty("updated")
    public OffsetDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(OffsetDateTime updated) {
        this.updated = updated;
    }

    public DataIdentification updated(OffsetDateTime updated) {
        this.updated = updated;
        return this;
    }

    /**
     * Get additionalAttributes
     *
     * @return additionalAttributes
  *
     */
    @JsonProperty("additionalAttributes")
    public Object getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(Object additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

    public DataIdentification additionalAttributes(Object additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DataIdentification {\n");

        sb.append("    parentIdentifier: ").append(toIndentedString(parentIdentifier)).append("\n");
        sb.append("    doi: ").append(toIndentedString(doi)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    identifier: ").append(toIndentedString(identifier)).append("\n");
        sb.append("    date: ").append(toIndentedString(date)).append("\n");
        sb.append("    _abstract: ").append(toIndentedString(_abstract)).append("\n");
        sb.append("    distribution: ").append(toIndentedString(distribution)).append("\n");
        sb.append("    provenance: ").append(toIndentedString(provenance)).append("\n");
        sb.append("    wasUsedBy: ").append(toIndentedString(wasUsedBy)).append("\n");
        sb.append("    updated: ").append(toIndentedString(updated)).append("\n");
        sb.append("    additionalAttributes: ").append(toIndentedString(additionalAttributes)).append("\n");
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
