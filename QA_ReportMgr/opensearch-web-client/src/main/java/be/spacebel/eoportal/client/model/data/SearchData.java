package be.spacebel.eoportal.client.model.data;

import be.spacebel.eoportal.client.business.data.GeonamesOption;
import be.spacebel.eoportal.client.business.data.OpenSearchUrl;
import be.spacebel.eoportal.client.business.data.SearchResultItem;
import be.spacebel.eoportal.client.business.data.SearchResultSet;
import java.io.Serializable;

/**
 * This class represents search data. It is used to contains all information to
 * display on the JSF Search pages
 *
 * @author mng
 */
public class SearchData implements Serializable {

    private static final long serialVersionUID = 1L;

    private OpenSearchUrl datasetOpenSearchUrl;
    private OpenSearchUrl relatedOpenSearchUrl;

    private SearchResultSet datasetSearchResultSet;

    private Paginator datasetPaginator;
    private RelatedPaginator relatedPaginator;

    private SearchResultItem selectedSeriesItem;
    private boolean supportedDatasetSearch;

    private String datasetParentId;
    private String datasetParentTitle;
    private SearchResultItem selectedDatasetItem;

    private SearchResultItem selectedRelatedProduct;

    private String aoi;
    private String bbox;    

    private String datasetGMLFeatures;
    private String relatedGMLFeatures;

    private String datasetSelectedFeature;
    private String relatedSelectedFeature;

    private boolean showDatasetFootprint;
    private boolean showRelatedFootprint;

    private GeonamesOption selectedGeoname;

    public SearchData() {
        this.datasetOpenSearchUrl = new OpenSearchUrl();
        this.relatedOpenSearchUrl = new OpenSearchUrl();
        this.aoi = "";
        this.bbox = "";
        this.supportedDatasetSearch = false;
    }

    public SearchResultSet getDatasetSearchResultSet() {
        return datasetSearchResultSet;
    }

    public void setDatasetSearchResultSet(SearchResultSet datasetSearchResultSet) {
        this.datasetSearchResultSet = datasetSearchResultSet;
    }

    public OpenSearchUrl getDatasetOpenSearchUrl() {
        return datasetOpenSearchUrl;
    }

    public void setDatasetOpenSearchUrl(OpenSearchUrl datasetOpenSearchUrl) {
        this.datasetOpenSearchUrl = datasetOpenSearchUrl;
    }

    public OpenSearchUrl getRelatedOpenSearchUrl() {
        return relatedOpenSearchUrl;
    }

    public void setRelatedOpenSearchUrl(OpenSearchUrl relatedOpenSearchUrl) {
        this.relatedOpenSearchUrl = relatedOpenSearchUrl;
    }

    public SearchResultItem getSelectedSeriesItem() {
        return selectedSeriesItem;
    }

    public void setSelectedSeriesItem(SearchResultItem selectedSeriesItem) {
        this.selectedSeriesItem = selectedSeriesItem;
    }

    public boolean isSupportedDatasetSearch() {
        return supportedDatasetSearch;
    }

    public void setSupportedDatasetSearch(boolean supportedDatasetSearch) {
        this.supportedDatasetSearch = supportedDatasetSearch;
    }

    public String getDatasetParentId() {
        return datasetParentId;
    }

    public void setDatasetParentId(String datasetParentId) {
        this.datasetParentId = datasetParentId;
    }

    public String getDatasetParentTitle() {
        return datasetParentTitle;
    }

    public void setDatasetParentTitle(String datasetParentTitle) {
        this.datasetParentTitle = datasetParentTitle;
    }

    public SearchResultItem getSelectedDatasetItem() {
        return selectedDatasetItem;
    }

    public void setSelectedDatasetItem(SearchResultItem selectedDatasetItem) {
        this.selectedDatasetItem = selectedDatasetItem;
    }

    public Paginator getDatasetPaginator() {
        return datasetPaginator;
    }

    public void setDatasetPaginator(Paginator datasetPaginator) {
        this.datasetPaginator = datasetPaginator;
    }

    public String getDatasetGMLFeatures() {
        return datasetGMLFeatures;
    }

    public void setDatasetGMLFeatures(String datasetGMLFeatures) {
        this.datasetGMLFeatures = datasetGMLFeatures;
    }

    public String getDatasetSelectedFeature() {
        return datasetSelectedFeature;
    }

    public void setDatasetSelectedFeature(String datasetSelectedFeature) {
        this.datasetSelectedFeature = datasetSelectedFeature;
    }

    public String getBbox() {
        return bbox;
    }

    public void setBbox(String bbox) {
        this.bbox = bbox;
    }

    public String getAoi() {
        return aoi;
    }

    public void setAoi(String aoi) {
        this.aoi = aoi;
    }   

    public GeonamesOption getSelectedGeoname() {
        return selectedGeoname;
    }

    public void setSelectedGeoname(GeonamesOption selectedGeoname) {
        this.selectedGeoname = selectedGeoname;
    }

    public boolean isShowDatasetFootprint() {
        return showDatasetFootprint;
    }

    public void setShowDatasetFootprint(boolean showDatasetFootprint) {
        this.showDatasetFootprint = showDatasetFootprint;
    }

    public SearchResultItem getSelectedRelatedProduct() {
        return selectedRelatedProduct;
    }

    public void setSelectedRelatedProduct(SearchResultItem selectedRelatedProduct) {
        this.selectedRelatedProduct = selectedRelatedProduct;
    }

    public RelatedPaginator getRelatedPaginator() {
        return relatedPaginator;
    }

    public void setRelatedPaginator(RelatedPaginator relatedPaginator) {
        this.relatedPaginator = relatedPaginator;
    }

    public String getRelatedGMLFeatures() {
        return relatedGMLFeatures;
    }

    public void setRelatedGMLFeatures(String relatedGMLFeatures) {
        this.relatedGMLFeatures = relatedGMLFeatures;
    }

    public String getRelatedSelectedFeature() {
        return relatedSelectedFeature;
    }

    public void setRelatedSelectedFeature(String relatedSelectedFeature) {
        this.relatedSelectedFeature = relatedSelectedFeature;
    }

    public boolean isShowRelatedFootprint() {
        return showRelatedFootprint;
    }

    public void setShowRelatedFootprint(boolean showRelatedFootprint) {
        this.showRelatedFootprint = showRelatedFootprint;
    }

}
