package be.spacebel.opensearch.model;

import be.spacebel.opensearch.model.Link;
import io.swagger.v3.oas.annotations.media.Schema;
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
 * OGC 14-055r2
 *
 */
@Schema(description = "OGC 14-055r2")
public class Links {

    public enum TypeEnum {
        LINKS("Links");

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

    @Schema(description = "OGC 14-055r2")
    /**
     * OGC 14-055r2  
  *
     */
    private List<Link> data = null;

    @Schema(description = "OGC 14-055r2")
    /**
     * OGC 14-055r2  
  *
     */
    private List<Link> previews = null;

    @Schema(description = "OGC 14-055r2")
    /**
     * OGC 14-055r2  
  *
     */
    private List<Link> up = null;

    @Schema(description = "OGC 14-055r2")
    /**
     * OGC 14-055r2  
  *
     */
    private List<Link> profiles = null;

    @Schema(description = "OGC 14-055r2")
    /**
     * OGC 14-055r2  
  *
     */
    private List<Link> alternates = null;

    @Schema(description = "OGC 14-055r2")
    /**
     * OGC 14-055r2  
  *
     */
    private List<Link> via = null;

    @Schema(description = "OGC 14-055r2")
    /**
     * OGC 14-055r2  
  *
     */
    private List<Link> related = null;

    @Schema(description = "")
    private List<Link> first = null;

    @Schema(description = "")
    private List<Link> last = null;

    @Schema(description = "")
    private List<Link> previous = null;

    @Schema(description = "")
    private List<Link> next = null;

    @Schema(description = "")
    private List<Link> search = null;

    @Schema(description = "")
    private List<Link> describedby = null;

    @Schema(description = "")
    private List<Link> qualityReport = null;

    /**
     * Get type
     *
     * @return type
  *
     */
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

    public Links type(TypeEnum type) {
        this.type = type;
        return this;
    }

    /**
     * OGC 14-055r2
     *
     * @return data
  *
     */
    @JsonProperty("data")
    public List<Link> getData() {
        return data;
    }

    public void setData(List<Link> data) {
        this.data = data;
    }

    public Links data(List<Link> data) {
        this.data = data;
        return this;
    }

    public Links addDataItem(Link dataItem) {
        this.data.add(dataItem);
        return this;
    }

    /**
     * OGC 14-055r2
     *
     * @return previews
  *
     */
    @JsonProperty("previews")
    public List<Link> getPreviews() {
        return previews;
    }

    public void setPreviews(List<Link> previews) {
        this.previews = previews;
    }

    public Links previews(List<Link> previews) {
        this.previews = previews;
        return this;
    }

    public Links addPreviewsItem(Link previewsItem) {
        this.previews.add(previewsItem);
        return this;
    }

    /**
     * OGC 14-055r2
     *
     * @return up
  *
     */
    @JsonProperty("up")
    public List<Link> getUp() {
        return up;
    }

    public void setUp(List<Link> up) {
        this.up = up;
    }

    public Links up(List<Link> up) {
        this.up = up;
        return this;
    }

    public Links addUpItem(Link upItem) {
        this.up.add(upItem);
        return this;
    }

    /**
     * OGC 14-055r2
     *
     * @return profiles
  *
     */
    @JsonProperty("profiles")
    public List<Link> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Link> profiles) {
        this.profiles = profiles;
    }

    public Links profiles(List<Link> profiles) {
        this.profiles = profiles;
        return this;
    }

    public Links addProfilesItem(Link profilesItem) {
        this.profiles.add(profilesItem);
        return this;
    }

    /**
     * OGC 14-055r2
     *
     * @return alternates
  *
     */
    @JsonProperty("alternates")
    public List<Link> getAlternates() {
        return alternates;
    }

    public void setAlternates(List<Link> alternates) {
        this.alternates = alternates;
    }

    public Links alternates(List<Link> alternates) {
        this.alternates = alternates;
        return this;
    }

    public Links addAlternatesItem(Link alternatesItem) {
        this.alternates.add(alternatesItem);
        return this;
    }

    /**
     * OGC 14-055r2
     *
     * @return via
  *
     */
    @JsonProperty("via")
    public List<Link> getVia() {
        return via;
    }

    public void setVia(List<Link> via) {
        this.via = via;
    }

    public Links via(List<Link> via) {
        this.via = via;
        return this;
    }

    public Links addViaItem(Link viaItem) {
        this.via.add(viaItem);
        return this;
    }

    /**
     * OGC 14-055r2
     *
     * @return related
  *
     */
    @JsonProperty("related")
    public List<Link> getRelated() {
        return related;
    }

    public void setRelated(List<Link> related) {
        this.related = related;
    }

    public Links related(List<Link> related) {
        this.related = related;
        return this;
    }

    public Links addRelatedItem(Link relatedItem) {
        this.related.add(relatedItem);
        return this;
    }

    /**
     * Get first
     *
     * @return first
  *
     */
    @JsonProperty("first")
    public List<Link> getFirst() {
        return first;
    }

    public void setFirst(List<Link> first) {
        this.first = first;
    }

    public Links first(List<Link> first) {
        this.first = first;
        return this;
    }

    public Links addFirstItem(Link firstItem) {
        this.first.add(firstItem);
        return this;
    }

    /**
     * Get last
     *
     * @return last
  *
     */
    @JsonProperty("last")
    public List<Link> getLast() {
        return last;
    }

    public void setLast(List<Link> last) {
        this.last = last;
    }

    public Links last(List<Link> last) {
        this.last = last;
        return this;
    }

    public Links addLastItem(Link lastItem) {
        this.last.add(lastItem);
        return this;
    }

    /**
     * Get previous
     *
     * @return previous
  *
     */
    @JsonProperty("previous")
    public List<Link> getPrevious() {
        return previous;
    }

    public void setPrevious(List<Link> previous) {
        this.previous = previous;
    }

    public Links previous(List<Link> previous) {
        this.previous = previous;
        return this;
    }

    public Links addPreviousItem(Link previousItem) {
        this.previous.add(previousItem);
        return this;
    }

    /**
     * Get next
     *
     * @return next
  *
     */
    @JsonProperty("next")
    public List<Link> getNext() {
        return next;
    }

    public void setNext(List<Link> next) {
        this.next = next;
    }

    public Links next(List<Link> next) {
        this.next = next;
        return this;
    }

    public Links addNextItem(Link nextItem) {
        this.next.add(nextItem);
        return this;
    }

    /**
     * Get search
     *
     * @return search
  *
     */
    @JsonProperty("search")
    public List<Link> getSearch() {
        return search;
    }

    public void setSearch(List<Link> search) {
        this.search = search;
    }

    public Links search(List<Link> search) {
        this.search = search;
        return this;
    }

    public Links addSearchItem(Link searchItem) {
        this.search.add(searchItem);
        return this;
    }

    /**
     * Get describedby
     *
     * @return describedby
  *
     */
    @JsonProperty("describedby")
    public List<Link> getDescribedby() {
        return describedby;
    }

    public void setDescribedby(List<Link> describedby) {
        this.describedby = describedby;
    }

    public Links describedby(List<Link> describedby) {
        this.describedby = describedby;
        return this;
    }

    public Links addDescribedbyItem(Link describedbyItem) {
        this.describedby.add(describedbyItem);
        return this;
    }

    /**
     * Get qualityReport
     *
     * @return qualityReport
  *
     */
    @JsonProperty("qualityReport")
    public List<Link> getQualityReport() {
        return qualityReport;
    }

    public void setQualityReport(List<Link> qualityReport) {
        this.qualityReport = qualityReport;
    }

    public Links qualityReport(List<Link> qualityReport) {
        this.qualityReport = qualityReport;
        return this;
    }

    public Links addQualityReportItem(Link qualityReportItem) {
        this.qualityReport.add(qualityReportItem);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Links {\n");

        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    data: ").append(toIndentedString(data)).append("\n");
        sb.append("    previews: ").append(toIndentedString(previews)).append("\n");
        sb.append("    up: ").append(toIndentedString(up)).append("\n");
        sb.append("    profiles: ").append(toIndentedString(profiles)).append("\n");
        sb.append("    alternates: ").append(toIndentedString(alternates)).append("\n");
        sb.append("    via: ").append(toIndentedString(via)).append("\n");
        sb.append("    related: ").append(toIndentedString(related)).append("\n");
        sb.append("    first: ").append(toIndentedString(first)).append("\n");
        sb.append("    last: ").append(toIndentedString(last)).append("\n");
        sb.append("    previous: ").append(toIndentedString(previous)).append("\n");
        sb.append("    next: ").append(toIndentedString(next)).append("\n");
        sb.append("    search: ").append(toIndentedString(search)).append("\n");
        sb.append("    describedby: ").append(toIndentedString(describedby)).append("\n");
        sb.append("    qualityReport: ").append(toIndentedString(qualityReport)).append("\n");
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
