package be.spacebel.eoportal.client.business.data;

import be.spacebel.opensearch.model.QualityIndicator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;

/**
 * This class represents a search results item
 *
 * @author mng
 */
public class SearchResultItem implements Serializable {

    private static final String GML_IDENTIFIER = "GML_IDENTIFIER";
    private static final long serialVersionUID = 1L;
    private Map<String, SearchResultProperty> properties;
    private Map<String, String> attributes;
    private boolean selectedForBasket;
    private boolean selectedForProcessing = true;
    private String productId;
    private boolean showProductId;
    private String parentId;
    private boolean showParentId;
    private String status;
    private String previewUrl;
    private String thumbnailDivId;
    private String thumbnailDivStyle;
    private String thumbnailDivClass;
    private String thumbnailCellClass;

    private String gmlNode;
    // backward compatibility
    private String opOutputXML;

    private String uuid;
    private int index;

    boolean hasFootprint;
    private List<String> viaLinks;
    private boolean eoProductAndCollection;
    private boolean hasAcquisitionParameter;
    private boolean hasProductInfo;

    private String additionalAttributes;

    /*
    List<KeyValueProperty> additionalAttributes;
     */
    private List<QualityIndicator> qualityIndicators;

    private String relatedProductsLink;
    private String voilaReportLink;
    private String jupyterNotebookReportLink;
    private String isoReportLink;

    public SearchResultItem(String searchType) {
        properties = new LinkedHashMap<>();
        attributes = new HashMap<>();
        uuid = searchType + "_" + UUID.randomUUID().toString();

    }

    public void setSelectedForBasket(boolean selectedForBasket) {
        this.selectedForBasket = selectedForBasket;
        this.selectedForProcessing = selectedForBasket;
    }

    public void addProperty(String key, SearchResultProperty property) {
        if (properties.containsKey(key)) {
            if (property.getValue() == null || StringUtils.isEmpty(property.getValue().trim())) {
                properties.put(key, property);
            }
        } else {
            properties.put(key, property);
        }
    }

    public List<String> getPropertyKeys() {
        return new ArrayList<>(properties.keySet());
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof SearchResultItem) {
            SearchResultItem sri = (SearchResultItem) obj;
            if (this.uuid.equals(sri.getUuid())) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
        return hash;
    }

    /* GETTERS AND SETTERS */
    public Map<String, SearchResultProperty> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, SearchResultProperty> properties) {
        this.properties = properties;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSelectedForProcessing() {
        return selectedForProcessing;
    }

    public void setSelectedForProcessing(boolean selectedForProcessing) {
        this.selectedForProcessing = selectedForProcessing;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getOpOutputXML() {
        return opOutputXML;
    }

    public void setOpOutputXML(String opOutputXML) {
        this.opOutputXML = opOutputXML;
    }

    public String getProductId() {
        return productId;
    }

    public String getShortProductId() {
        String shortId = this.productId;
        if (this.productId != null && this.productId.length() > 45) {
            String firstPart = this.productId.substring(0, 21);
            String lastPart = this.productId.substring((this.productId.length() - 21),
                    this.productId.length());
            shortId = firstPart + "..." + lastPart;
        }
        return shortId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getGmlNode() {
        return gmlNode;
    }

    public void setGmlNode(String newGmlNode) {
        if (StringUtils.isNotEmpty(this.uuid)) {
            newGmlNode = newGmlNode.replaceAll(GML_IDENTIFIER, this.uuid);
        }
        this.gmlNode = newGmlNode;
    }

    public boolean isSelectedForBasket() {
        return selectedForBasket;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isShowProductId() {
        return showProductId;
    }

    public void setShowProductId(boolean showProductId) {
        this.showProductId = showProductId;
    }

    public boolean isShowParentId() {
        return showParentId;
    }

    public void setShowParentId(boolean showParentId) {
        this.showParentId = showParentId;
    }

    public int getIndex() {
        return index;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getThumbnailDivId() {
        return thumbnailDivId;
    }

    public void setThumbnailDivId(String thumbnailDivId) {
        this.thumbnailDivId = thumbnailDivId;
    }

    public String getThumbnailDivStyle() {
        return thumbnailDivStyle;
    }

    public void setThumbnailDivStyle(String thumbnailDivStyle) {
        this.thumbnailDivStyle = thumbnailDivStyle;
    }

    public String getThumbnailDivClass() {
        return thumbnailDivClass;
    }

    public void setThumbnailDivClass(String thumbnailDivClass) {
        this.thumbnailDivClass = thumbnailDivClass;
    }

    public String getThumbnailCellClass() {
        return thumbnailCellClass;
    }

    public void setThumbnailCellClass(String thumbnailCellClass) {
        this.thumbnailCellClass = thumbnailCellClass;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isHasFootprint() {
        return hasFootprint;
    }

    public void setHasFootprint(boolean hasFootprint) {
        this.hasFootprint = hasFootprint;
    }

    public List<String> getViaLinks() {
        return viaLinks;
    }

    public void setViaLinks(List<String> viaLinks) {
        this.viaLinks = viaLinks;
    }

    public boolean isHasViaLinks() {
        return (this.viaLinks != null && this.viaLinks.size() > 0);
    }

    public String getRelatedProductsLink() {
        return relatedProductsLink;
    }

    public void setRelatedProductsLink(String relatedProductsLink) {
        this.relatedProductsLink = relatedProductsLink;
    }

    public boolean isEoProductAndCollection() {
        return eoProductAndCollection;
    }

    public void setEoProductAndCollection(boolean eoProductAndCollection) {
        this.eoProductAndCollection = eoProductAndCollection;
    }

    public boolean isHasAcquisitionParameter() {
        return hasAcquisitionParameter;
    }

    public void setHasAcquisitionParameter(boolean hasAcquisitionParameter) {
        this.hasAcquisitionParameter = hasAcquisitionParameter;
    }

    public boolean isHasProductInfo() {
        return hasProductInfo;
    }

    public void setHasProductInfo(boolean hasProductInfo) {
        this.hasProductInfo = hasProductInfo;
    }

    public String getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(String additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

    public List<QualityIndicator> getQualityIndicators() {
        return qualityIndicators;
    }

    public void setQualityIndicators(List<QualityIndicator> qualityIndicators) {
        this.qualityIndicators = qualityIndicators;
    }

    public String getVoilaReportLink() {
        return voilaReportLink;
    }

    public void setVoilaReportLink(String voilaReportLink) {
        this.voilaReportLink = voilaReportLink;
    }

    public String getJupyterNotebookReportLink() {
        return jupyterNotebookReportLink;
    }

    public void setJupyterNotebookReportLink(String jupyterNotebookReportLink) {
        this.jupyterNotebookReportLink = jupyterNotebookReportLink;
    }

    public String getIsoReportLink() {
        return isoReportLink;
    }

    public void setIsoReportLink(String isoReportLink) {
        this.isoReportLink = isoReportLink;
    }

}
