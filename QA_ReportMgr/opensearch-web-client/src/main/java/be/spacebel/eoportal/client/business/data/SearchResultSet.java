package be.spacebel.eoportal.client.business.data;

import be.spacebel.eoportal.client.util.XMLUtility;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class represents a list of search results items
 *
 * @author mng
 */
public class SearchResultSet implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(SearchResultSet.class);

    private String serviceId;

    private SearchResultItem common;
    private List<SearchResultItem> items;
    private Map<String, String> attributes;
    private String gmlEnveloppe;
    private boolean gmlComplete;
    private SearchResultError searchResultError;

    public static final String NEXTRECORD = "nextRecord";

    public SearchResultSet() {
        items = new ArrayList<>();
        attributes = new HashMap<>();
    }

    public void addItem(SearchResultItem item) {
        item.setIndex(items.size());
        items.add(item);
    }

    public List<String> getLabels() {
        List<String> list = new ArrayList<>();
        if (!items.isEmpty()) {
            for (String s : items.get(0).getProperties().keySet()) {
                list.add(s);
            }
        }
        return list;
    }

    /**
     * @return dynamically generate the GML features for the content of the
     * result set used for both search results and the basket
     */
    public String getGmlFeatures() {
        String result = "";
        try {
            if (!getItems().isEmpty() && StringUtils.isNotBlank(gmlEnveloppe)) {
                //log.debug("GML ENVELOPE -------- " + gmlEnveloppe);
                if (gmlComplete) {
                    result = gmlEnveloppe;
                } else {
                    Node features = XMLUtility.buildNode(gmlEnveloppe);

                    for (SearchResultItem item : getItems()) {
                        Node importNode = XMLUtility.buildNode(item
                                .getGmlNode());
                        log.debug("GML : " + item.getProductId());
                        importNode = features.getOwnerDocument().importNode(
                                importNode, true);
                        Element element = (Element) features;
                        element.getElementsByTagNameNS("http://www.opengis.net/gml", "featureMembers").item(0).appendChild(importNode);
                    }
                    result = XMLUtility.getNodeContent(features);
                    log.debug("GML result for getGMLfeatures");
                    //log.debug(result);
                }
            }
        } catch (Exception e) {

        }
        return result;
    }

    public boolean statusesAvailable() {
        boolean avail = false;
        for (SearchResultItem item : items) {
            if (StringUtils.isNotBlank(item.getStatus())) {
                avail = true;
                break;
            }
        }
        return avail;
    }

    public String serialize() {
        return new JSONSerializer().deepSerialize(this);
    }

    public static SearchResultSet deserialize(String serializedSet) {
        return new JSONDeserializer<SearchResultSet>()
                .deserialize(serializedSet);
    }

    /* GETTERS AND SETTERS */
    public String getGmlEnveloppe() {
        return gmlEnveloppe;
    }

    public void setGmlEnveloppe(String gmlEnveloppe) {
        this.gmlEnveloppe = gmlEnveloppe;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public SearchResultItem getCommon() {
        return common;
    }

    public void setCommon(SearchResultItem common) {
        this.common = common;
    }

    public List<SearchResultItem> getItems() {
        return items;
    }

    public void setItems(List<SearchResultItem> items) {
        this.items = items;
    }

    public SearchResultError getSearchResultError() {
        return searchResultError;
    }

    public void setSearchResultError(SearchResultError searchResultError) {
        this.searchResultError = searchResultError;
    }

    public boolean isGmlComplete() {
        return gmlComplete;
    }

    public void setGmlComplete(boolean gmlComplete) {
        this.gmlComplete = gmlComplete;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
