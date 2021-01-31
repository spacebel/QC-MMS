/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.spacebel.eoportal.client.model.data;

import be.spacebel.eoportal.client.business.data.Constants;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains all configurations of dataset search
 *
 * @author mng
 */
public class Dataset implements Serializable {

    private String defaultView;
    private String namespaces;
    private String blackList;
    private String[] selectedListViewAttributes;
    private String[] selectedMoreOptions;
    private String thumbnailWidth;
    private String thumbnailHeight;
    private String thumbnailService;
    private String simpleSearchHint;
    //private String simpleSearchTooltip;

    private List<String> orderedParameters;

    public Dataset() {
    }

    public void setDefaultValues() {
        setDefaultView("list-view");
    }

    public String getDefaultView() {
        return defaultView;
    }

    public void setDefaultView(String defaultView) {
        this.defaultView = defaultView;
    }

    public String getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(String namespaces) {
        this.namespaces = namespaces;
    }

    public String getBlackList() {
        return blackList;
    }

    public void setBlackList(String blackList) {
        this.blackList = blackList;
    }

    public String[] getSelectedListViewAttributes() {
        return selectedListViewAttributes;
    }

    public void setSelectedListViewAttributes(String[] selectedListViewAttributes) {
        this.selectedListViewAttributes = selectedListViewAttributes;
    }

    public List<String> getListViewAttributes() {
        return Arrays.asList(this.selectedListViewAttributes);
    }

    public String[] getSelectedMoreOptions() {
        return selectedMoreOptions;
    }

    public void setSelectedMoreOptions(String[] selectedMoreOptions) {
        this.selectedMoreOptions = selectedMoreOptions;
    }

    public List<String> getListMoreOptions() {
        return Arrays.asList(this.selectedMoreOptions);
    }

    public String getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailWidth(String thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }

    public String getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void setThumbnailHeight(String thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    public int getView() {
        if ("list-view".equalsIgnoreCase(this.defaultView)) {
            return 1;
        }

        if ("thumbnail-view".equalsIgnoreCase(this.defaultView)) {
            return 2;
        }

        if ("details-view".equalsIgnoreCase(this.defaultView)) {
            return 3;
        }

        return 0;
    }

    public String getThumbnailService() {
        return thumbnailService;
    }

    public void setThumbnailService(String thumbnailService) {
        this.thumbnailService = thumbnailService;
    }

    public String getSimpleSearchHint() {
        return simpleSearchHint;
    }

    public void setSimpleSearchHint(String simpleSearchHint) {
        this.simpleSearchHint = simpleSearchHint;
    }

    /*
     public String getSimpleSearchTooltip() {
     return simpleSearchTooltip;
     }

     public void setSimpleSearchTooltip(String simpleSearchTooltip) {
     this.simpleSearchTooltip = simpleSearchTooltip;
     }
     */
    public List<String> getOrderedParameters() {
        return orderedParameters;
    }

    public void setOrderedParameters(List<String> orderedParameters) {
        this.orderedParameters = orderedParameters;
    }

    public String getDatasetSearchFormat() {
        return Constants.GEO_JSON_MIME_TYPE;
    }

}
