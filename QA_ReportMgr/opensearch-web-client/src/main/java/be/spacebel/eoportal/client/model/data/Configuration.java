/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.spacebel.eoportal.client.model.data;

import java.io.Serializable;

/**
 * This class contains all configurations that will be stored into the
 * PortletPreferences
 *
 * @author mng
 */
public class Configuration implements Serializable {

    private String version;
    private String osddLocation;
    private String datasetOsddTemplateUrl;    
    private int rowsPerPage;
    /*private int yearRange;*/
    private String identifierPrefix;
    private Series series;
    private Dataset dataset;

    private String previewImageOrder;
    private String staticMapServiceUrl;
    private String staticMapServiceUniqueKey;
    private String geonamesAccount;
    private double geonamesRadius;
    private String earthObservationPortalUrl;

    private String voilaReportsDir;
//    private String voilaReportTemplate;
//    private String voilaReportProductIdToBeReplaced;
//    private String voilaReportParentIdToBeReplaced;
    private String voilaReportFileNamePattern;
    private String voilaReportUrl;
    private boolean freshReport;

    public Configuration() {       
    }

    public Configuration(String osddLocation) {
        this.osddLocation = osddLocation;
        this.series = new Series();
        this.dataset = new Dataset();       
    }

    public Configuration(Configuration newConfig) {

        this.version = newConfig.getVersion();
        this.osddLocation = newConfig.getOsddLocation();      
        this.rowsPerPage = newConfig.getRowsPerPage();
        this.identifierPrefix = newConfig.getIdentifierPrefix();

        this.series = new Series();
        this.series.setSeriesMenuList(newConfig.getSeries().getSeriesMenuList());
        this.series.setMenuOptionRegex(newConfig.getSeries().getMenuOptionRegex());
        //this.series.setSimpleSearchTooltip(newConfig.getSeries().getSimpleSearchTooltip());

        this.dataset = new Dataset();
        this.dataset.setDefaultView(newConfig.getDataset().getDefaultView());
        this.dataset.setNamespaces(newConfig.getDataset().getNamespaces());
        this.dataset.setBlackList(newConfig.getDataset().getBlackList());
        this.dataset.setSelectedListViewAttributes(newConfig.getDataset().getSelectedListViewAttributes());
        this.dataset.setSelectedMoreOptions(newConfig.getDataset().getSelectedMoreOptions());
        this.dataset.setThumbnailWidth(newConfig.getDataset().getThumbnailWidth());
        this.dataset.setThumbnailHeight(newConfig.getDataset().getThumbnailHeight());
        this.dataset.setThumbnailService(newConfig.getDataset().getThumbnailService());

        this.previewImageOrder = newConfig.getPreviewImageOrder();
        this.staticMapServiceUrl = newConfig.getStaticMapServiceUrl();
        this.staticMapServiceUniqueKey = newConfig.getStaticMapServiceUniqueKey();
        this.geonamesAccount = newConfig.getGeonamesAccount();
        this.geonamesRadius = newConfig.getGeonamesRadius();

        this.earthObservationPortalUrl = newConfig.getEarthObservationPortalUrl();

        this.voilaReportsDir = newConfig.getVoilaReportsDir();
//        this.voilaReportTemplate = newConfig.getVoilaReportTemplate();
//        this.voilaReportParentIdToBeReplaced = newConfig.getVoilaReportParentIdToBeReplaced();
//        this.voilaReportProductIdToBeReplaced = newConfig.getVoilaReportProductIdToBeReplaced();
        this.voilaReportFileNamePattern = newConfig.getVoilaReportFileNamePattern();
        this.voilaReportUrl = newConfig.getVoilaReportUrl();
        this.freshReport = newConfig.isFreshReport();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOsddLocation() {
        return osddLocation;
    }

    public void setOsddLocation(String osddLocation) {
        this.osddLocation = osddLocation;
    }

    public String getDatasetOsddTemplateUrl() {
        return datasetOsddTemplateUrl;
    }

    public void setDatasetOsddTemplateUrl(String datasetOsddTemplateUrl) {
        this.datasetOsddTemplateUrl = datasetOsddTemplateUrl;
    }    

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public int getRowsPerPage() {
        return rowsPerPage;
    }

    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public String getIdentifierPrefix() {
        return identifierPrefix;
    }

    public void setIdentifierPrefix(String identifierPrefix) {
        this.identifierPrefix = identifierPrefix;
    }

    public boolean isLayoutDocked() {
        return false;
    }

    public String getStaticMapServiceUrl() {
        return staticMapServiceUrl;
    }

    public void setStaticMapServiceUrl(String staticMapServiceUrl) {
        this.staticMapServiceUrl = staticMapServiceUrl;
    }

    public String getStaticMapServiceUniqueKey() {
        return staticMapServiceUniqueKey;
    }

    public void setStaticMapServiceUniqueKey(String staticMapServiceUniqueKey) {
        this.staticMapServiceUniqueKey = staticMapServiceUniqueKey;
    }

    public String getGeonamesAccount() {
        return geonamesAccount;
    }

    public void setGeonamesAccount(String geonamesAccount) {
        this.geonamesAccount = geonamesAccount;
    }

    public double getGeonamesRadius() {
        return geonamesRadius;
    }

    public void setGeonamesRadius(double geonamesRadius) {
        this.geonamesRadius = geonamesRadius;
    }

    public String getPreviewImageOrder() {
        return previewImageOrder;
    }

    public void setPreviewImageOrder(String previewImageOrder) {
        this.previewImageOrder = previewImageOrder;
    }

    public String getEarthObservationPortalUrl() {
        return earthObservationPortalUrl;
    }

    public void setEarthObservationPortalUrl(String earthObservationPortalUrl) {
        this.earthObservationPortalUrl = earthObservationPortalUrl;
    }

    public String getVoilaReportsDir() {
        return voilaReportsDir;
    }

    public void setVoilaReportsDir(String voilaReportsDir) {
        this.voilaReportsDir = voilaReportsDir;
    }

    public String getVoilaReportFileNamePattern() {
        return voilaReportFileNamePattern;
    }

    public void setVoilaReportFileNamePattern(String voilaReportFileNamePattern) {
        this.voilaReportFileNamePattern = voilaReportFileNamePattern;
    }

    public String getVoilaReportUrl() {
        return voilaReportUrl;
    }

    public void setVoilaReportUrl(String voilaReportUrl) {
        this.voilaReportUrl = voilaReportUrl;
    }

    public boolean isFreshReport() {
        return freshReport;
    }

    public void setFreshReport(boolean freshReport) {
        this.freshReport = freshReport;
    }

}
