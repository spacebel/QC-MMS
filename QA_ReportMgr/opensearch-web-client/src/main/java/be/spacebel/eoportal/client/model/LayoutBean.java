package be.spacebel.eoportal.client.model;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.log4j.Logger;

/**
 * A managed bean manages the Search mode and View mode of JSF Search pages
 *
 * @author mng
 */
@ManagedBean(name = "layoutBean")
@ViewScoped
public class LayoutBean implements Serializable {

    private static final Logger LOG = Logger.getLogger(LayoutBean.class);

    public enum ViewMode {

        init, seriesListView, seriesThumbnailsView, seriesDetails, datasetListView, datasetThumbnailsView, datasetDetails, datasetInit, relatedListView, relatedThumbnailsView, relatedDetails
    };

    public enum SearchMode {

        series, dataset, related
    };

    private ViewMode currentView;
    private SearchMode currentMode;

    private boolean seriesMode = false;
    private boolean datasetMode = false;
    private boolean relatedMode = false;

    private boolean thumbnailsView = false;
    private boolean listView = false;
    private boolean entryView = false;

    private boolean seriesAdvancedSearch = false;
    private boolean hasSeriesFilter = false;
    private boolean datasetAdvancedSearch = false;
    private boolean hasDatasetFilter = false;

    private int defaultSeriesView = 0;
    private int seriesView = 0;

    private int defaultDatasetView = 0;
    private int datasetView = 0;

    private int defaultRelatedView = 1;
    private int relatedView = 0;

    private boolean fullPage = false;

    private boolean outputToggle = false;
    private boolean showUndockedOutput = false;
    private boolean aoi = true;    

    private boolean visibleSeriesTab = false;
    private boolean visibleDatasetTab = false;
    private boolean visibleRelatedTab = false;

    private boolean activeSeriesTab = false;
    private boolean activeDatasetTab = false;
    private boolean activeRelatedTab = false;

    private ViewMode currentSeriesView;
    private ViewMode currentDatasetView;
    private ViewMode currentRelatedView;

    /**
     * Getter method
     *
     * @return
     */
    public SearchMode getCurrentSearchMode() {
        debug("CURRENT SEARCH MODE: " + this.currentMode);
        return currentMode;
    }

    /**
     * Setter method
     *
     * @param currentMode
     */
    public void setCurrentSearchMode(SearchMode currentMode) {
        this.currentMode = currentMode;
        debug("SET CURRENT SEARCH MODE: " + this.currentMode);
    }

    public LayoutBean() {
        debug("ManagerBean init..............");
        this.currentMode = SearchMode.series;
        this.currentView = ViewMode.init;
        this.currentSeriesView = ViewMode.init;
        this.currentDatasetView = ViewMode.datasetInit;
        this.currentRelatedView = ViewMode.relatedListView;

        this.seriesMode = true;
        this.datasetMode = false;
        this.relatedMode = false;

        this.seriesAdvancedSearch = false;
        this.hasSeriesFilter = false;
        this.datasetAdvancedSearch = true;
        this.hasDatasetFilter = false;

        this.visibleSeriesTab = true;
        this.visibleDatasetTab = false;
        this.visibleRelatedTab = false;

        this.activeSeriesTab = true;
        this.activeDatasetTab = false;
        this.activeRelatedTab = false;
    }

    /**
     * Set the initial view mode
     *
     * @param initView
     */
    public void setInitView(int initView) {
        switch (initView) {
            case 1:
                setListView(true);
                break;
            case 2:
                setThumbnailsView(true);
                break;
            case 3:
                setEntryView(true);
                break;
        }
    }

    /**
     * Getter method
     *
     * @return
     */
    public ViewMode getCurrentView() {
        debug("CURRENT VIEW: " + this.currentView);
        return this.currentView;
    }

    /**
     * Setter method
     *
     * @param currentView
     */
    public void setCurrentView(ViewMode currentView) {
        this.currentView = currentView;
        debug("Setting CURRENT VIEW: " + this.currentView);
    }

    /**
     * An action listener of Thumbnail View button that'd be executed when the
     * button is clicked.
     */
    public void showThumbnailsView() {
        debug("Click thumbnail view");

        this.setThumbnailsView(true);

        switch (this.currentMode) {
            case related:
                this.currentView = ViewMode.relatedThumbnailsView;
                this.setRelatedView(2);
                this.setCurrentRelatedView(ViewMode.relatedThumbnailsView);
                break;
            case dataset:
                this.currentView = ViewMode.datasetThumbnailsView;
                this.setDatasetView(2);
                this.setCurrentDatasetView(ViewMode.datasetThumbnailsView);
                break;
            case series:
                this.currentView = ViewMode.seriesThumbnailsView;
                this.setSeriesView(2);
                this.setCurrentSeriesView(ViewMode.seriesThumbnailsView);

                this.setActiveSeriesTab(true);
                this.setActiveDatasetTab(false);
                this.setActiveRelatedTab(false);
                break;
        }
    }

    /**
     * An action listener of List View button that'd be executed when the button
     * is clicked.
     */
    public void showListView() {
        debug("Click list view");

        this.setListView(true);

        switch (this.currentMode) {
            case related:
                this.currentView = ViewMode.relatedListView;
                this.setRelatedView(1);
                this.setCurrentRelatedView(ViewMode.relatedListView);
                break;
            case dataset:
                this.currentView = ViewMode.datasetListView;
                this.setDatasetView(1);
                this.setCurrentDatasetView(ViewMode.datasetListView);
                break;
            case series:
                this.currentView = ViewMode.seriesListView;
                this.setSeriesView(1);
                this.setCurrentSeriesView(ViewMode.seriesListView);

                this.setActiveSeriesTab(true);
                this.setActiveDatasetTab(false);
                this.setActiveRelatedTab(false);
                break;
        }
    }

    /**
     * An action listener of Entry View button that'd be executed when the
     * button is clicked.
     */
    public void showEntryView() {
        debug("Click entry view");

        this.setEntryView(true);

        switch (this.currentMode) {
            case related:
                this.currentView = ViewMode.relatedDetails;
                this.setRelatedView(3);
                this.setCurrentRelatedView(ViewMode.relatedDetails);
                break;
            case dataset:
                this.currentView = ViewMode.datasetDetails;
                this.setDatasetView(3);
                this.setCurrentDatasetView(ViewMode.datasetDetails);
                break;
            case series:
                this.currentView = ViewMode.seriesDetails;
                this.setSeriesView(3);
                break;
        }
    }

    /**
     * An action listener of Close Results panel button that'd be executed when
     * the button is clicked.
     */
    public void closeUndockedOutput() {
        LOG.debug("closeUndockedOutput...........");
        setShowUndockedOutput(false);
        LOG.debug("this.showUndockedOutput = " + this.showUndockedOutput);
    }

    /**
     * Change to the series search results layout
     */
    public void toSeriesResults() {
        debug("toSeriesResults................");
        setShowUndockedOutput(true);
        this.setCurrentSearchMode(SearchMode.series);
        this.setSeriesMode(true);
        setSeriesView();
    }

    /**
     * Change to the series details layout
     *
     */
    public void toSeriesDetails() {
        debug("toSeriesDetails................");
        this.setCurrentSearchMode(SearchMode.series);
        this.setSeriesMode(true);

        // if (datasetSearch) {
        this.setCurrentView(ViewMode.datasetInit);
        this.setCurrentDatasetView(ViewMode.datasetInit);

        this.setEntryView(true);

        this.setActiveDatasetTab(true);
        this.setVisibleDatasetTab(true);
        this.setActiveSeriesTab(false);
        this.setActiveRelatedTab(false);
        this.setVisibleRelatedTab(false);
        /* } else {
         this.setCurrentView(ViewMode.seriesDetails);
         //this.setCurrentSeriesView(ViewMode.seriesDetails);
         this.setEntryView(true);
         this.setSeriesView(3);
         this.setActiveSeriesTab(true);
         this.setActiveDatasetTab(false);
            
         LOG.debug("Current dataset view: " + this.getCurrentDatasetView());
         }*/

    }

    public void toDatasetInit() {
        this.setCurrentSearchMode(SearchMode.dataset);
        this.setDatasetMode(true);

        this.setCurrentView(ViewMode.datasetInit);
        this.setCurrentDatasetView(ViewMode.datasetInit);

        this.setVisibleDatasetTab(true);
        this.setActiveDatasetTab(true);
        this.setActiveSeriesTab(false);
        this.setActiveRelatedTab(false);
    }

    /**
     * Change to the dataset search results layout
     */
    public void toDatasetResults() {
        debug("toDatasetResults................");
        setShowUndockedOutput(true);
        this.setCurrentSearchMode(SearchMode.dataset);
        this.setDatasetMode(true);
        setDatasetView();
        LOG.debug("ShowUndockedOutput = " + isShowUndockedOutput());
    }

    /**
     * Change to the dataset details layout
     */
    public void toDatasetDetails() {
        debug("toDatasetDetails................");
        this.setCurrentSearchMode(SearchMode.dataset);
        this.setDatasetMode(true);
        this.setCurrentView(ViewMode.datasetDetails);
        this.setCurrentDatasetView(ViewMode.datasetDetails);
        this.setEntryView(true);
        this.setDatasetView(3);
    }

    /**
     * Change to related results layout
     */
    public void toRelatedResults() {
        debug("toRelatedResults................");
        setShowUndockedOutput(true);
        this.setCurrentSearchMode(SearchMode.related);
        this.setRelatedMode(true);
        setRelatedView(0);
        setRelatedView();

        this.setVisibleRelatedTab(true);
        this.setActiveRelatedTab(true);
        this.setActiveSeriesTab(false);
        this.setActiveDatasetTab(false);
    }

    /**
     * Change to related details layout
     */
    public void toRelatedDetails() {
        debug("toRelatedDetails................");
        this.setCurrentSearchMode(SearchMode.related);
        this.setRelatedMode(true);
        this.setCurrentView(ViewMode.relatedDetails);
        this.setCurrentRelatedView(ViewMode.relatedDetails);
        this.setEntryView(true);
        this.setRelatedView(3);
    }

    public void activateSeriesTab() {
        this.setCurrentSearchMode(SearchMode.series);
        this.setSeriesMode(true);
        if (ViewMode.seriesListView == this.currentSeriesView) {
            setListView(true);
            this.setCurrentView(ViewMode.seriesListView);
        } else {
            setThumbnailsView(true);
            this.setCurrentView(ViewMode.seriesThumbnailsView);
        }
        this.setActiveSeriesTab(true);
        this.setActiveDatasetTab(false);
        this.setActiveRelatedTab(false);
    }

    public void activateDatasetTab() {
        if (this.currentDatasetView == ViewMode.datasetInit) {
            toSeriesDetails();
        } else {
            this.setCurrentSearchMode(SearchMode.dataset);
            this.setDatasetMode(true);
            this.setCurrentView(this.currentDatasetView);

            switch (this.currentDatasetView) {
                case datasetListView:
                    setListView(true);
                    break;
                case datasetThumbnailsView:
                    setThumbnailsView(true);
                    break;
                case datasetDetails:
                    setEntryView(true);
                    break;
            }
        }

        this.setActiveSeriesTab(false);
        this.setActiveDatasetTab(true);
        this.setActiveRelatedTab(false);

    }

    public void activateRelatedTab() {
        this.setCurrentSearchMode(SearchMode.related);
        this.setRelatedMode(true);
        this.setCurrentView(this.currentRelatedView);

        switch (this.currentRelatedView) {
            case relatedListView:
                setListView(true);
                break;
            case relatedThumbnailsView:
                setThumbnailsView(true);
                break;
            case relatedDetails:
                setEntryView(true);
                break;
        }

        this.setActiveRelatedTab(true);
        this.setActiveSeriesTab(false);
        this.setActiveDatasetTab(false);

    }

    /**
     * Getter method
     *
     * @return true or false
     */
    public boolean isInit() {
        return this.currentView == ViewMode.init;
    }

    /**
     * Getter method
     *
     * @return true or false
     */
    public boolean isSeriesMode() {
        debug("isSeriesMode : " + this.seriesMode);
        return this.seriesMode;
    }

    /**
     * Getter method
     *
     * @return true or false
     */
    public boolean isDatasetMode() {
        debug("isDatasetMode : " + this.datasetMode);
        return this.datasetMode;
    }

    /**
     * Getter method
     *
     * @return true or false
     */
    public boolean isThumbnailsView() {
        debug("isThumbnailsView : " + this.thumbnailsView);
        return this.thumbnailsView;
    }

    /**
     * Getter method
     *
     * @return true or false
     */
    public boolean isListView() {
        debug("isListView : " + this.listView);
        return this.listView;
    }

    /**
     * Getter method
     *
     * @return true or false
     */
    public boolean isEntryView() {
        debug("isEntryView : " + this.entryView);
        return this.entryView;
    }

    /**
     * Getter method
     *
     * @return true or false
     */
    public boolean isSeriesListView() {
        debug("isSeriesListView: " + this.currentView);
        return this.currentView == ViewMode.seriesListView;
    }

    /**
     * Getter method
     *
     * @return true or false
     */
    public boolean isSeriesThumbnailsView() {
        debug("isSeriesThumbnailsView: " + this.currentView);
        return this.currentView == ViewMode.seriesThumbnailsView;
    }

    /**
     * Getter method
     *
     * @return true or false
     */
    public boolean isSeriesDetails() {
        debug("isSeriesDetails: " + this.currentView);
        return this.currentView == ViewMode.seriesDetails;
    }

    public boolean isDatasetInit() {
        debug("isDatasetInit: " + this.currentView);
        return this.currentView == ViewMode.datasetInit;
    }

    /**
     * Getter method
     *
     * @return true or false
     */
    public boolean isDatasetListView() {
        debug("isDatasetListView: " + this.currentView);
        return this.currentView == ViewMode.datasetListView;
    }

    /**
     * Getter method
     *
     * @return true or false
     */
    public boolean isDatasetThumbnailsView() {
        debug("isDatasetThumbnailsView: " + this.currentView);
        return this.currentView == ViewMode.datasetThumbnailsView;
    }

    /**
     * Getter method
     *
     * @return true or false
     */
    public boolean isDatasetDetails() {
        debug("isDatasetDetails: " + this.currentView);
        return this.currentView == ViewMode.datasetDetails;
    }

    /**
     * Setter method
     *
     * @param currentMode - Current Search mode
     */
    public void setCurrentMode(SearchMode currentMode) {
        this.currentMode = currentMode;
    }

    /**
     * Setter method
     *
     * @param newSeriesMode
     */
    public void setSeriesMode(boolean newSeriesMode) {
        this.seriesMode = newSeriesMode;
        this.datasetMode = !newSeriesMode;
        this.relatedMode = !newSeriesMode;
    }

    /**
     * Setter method
     *
     * @param newDatasetMode
     */
    public void setDatasetMode(boolean newDatasetMode) {
        this.datasetMode = newDatasetMode;
        this.seriesMode = !newDatasetMode;
        this.relatedMode = !newDatasetMode;
    }

    public void setRelatedMode(boolean newRelatedMode) {
        this.relatedMode = newRelatedMode;
        this.seriesMode = !newRelatedMode;
        this.datasetMode = !newRelatedMode;
    }

    public boolean isSeriesAdvancedSearch() {
        return seriesAdvancedSearch;
    }

    public void setSeriesAdvancedSearch(boolean seriesAdvancedSearch) {
        this.seriesAdvancedSearch = seriesAdvancedSearch;
    }

    public boolean isDatasetAdvancedSearch() {
        return datasetAdvancedSearch;
    }

    public void setDatasetAdvancedSearch(boolean datasetAdvancedSearch) {
        this.datasetAdvancedSearch = datasetAdvancedSearch;
    }

    public boolean isHasSeriesFilter() {
        return hasSeriesFilter;
    }

    public void setHasSeriesFilter(boolean hasSeriesFilter) {
        this.hasSeriesFilter = hasSeriesFilter;
    }

    public boolean isHasDatasetFilter() {
        return hasDatasetFilter;
    }

    public void setHasDatasetFilter(boolean hasDatasetFilter) {
        this.hasDatasetFilter = hasDatasetFilter;
    }

    public void cleanDatasetFilterValues() {
        this.hasDatasetFilter = false;
    }

    /**
     * Setter method
     *
     * @param newThumbnailsView
     */
    public void setThumbnailsView(boolean newThumbnailsView) {
        debug("setThumbnailsView = " + newThumbnailsView);
        if (newThumbnailsView) {
            this.thumbnailsView = true;
            this.listView = false;
            this.entryView = false;
        } else {
            this.thumbnailsView = false;
        }

    }

    /**
     * Setter method
     *
     * @param newListView
     */
    public void setListView(boolean newListView) {
        debug("setListView = " + newListView);
        if (newListView) {
            this.listView = true;
            this.thumbnailsView = false;
            this.entryView = false;
        } else {
            this.listView = false;
        }
    }

    /**
     * Setter method
     *
     * @param newEntryView
     */
    public void setEntryView(boolean newEntryView) {
        debug("setEntryView = " + newEntryView);
        if (newEntryView) {
            this.entryView = true;
            this.listView = false;
            this.thumbnailsView = false;
        } else {
            this.entryView = false;
        }
    }

    /**
     * Getter method
     *
     * @return
     */
    public int getDefaultSeriesView() {
        return defaultSeriesView;
    }

    /**
     * Setter method
     *
     * @param defaultSeriesView
     */
    public void setDefaultSeriesView(int defaultSeriesView) {
        this.defaultSeriesView = defaultSeriesView;
    }

    /**
     * Getter method
     *
     * @return
     */
    public int getDefaultDatasetView() {
        return defaultDatasetView;
    }

    /**
     * Setter method
     *
     * @param defaultDatasetView
     */
    public void setDefaultDatasetView(int defaultDatasetView) {
        this.defaultDatasetView = defaultDatasetView;
    }

    /**
     * Getter method
     *
     * @return
     */
    public int getSeriesView() {
        return seriesView;
    }

    /**
     * Setter method
     *
     * @param seriesView
     */
    public void setSeriesView(int seriesView) {
        this.seriesView = seriesView;
    }

    /**
     * Getter method
     *
     * @return
     */
    public int getDatasetView() {
        return datasetView;
    }

    /**
     * Setter method
     *
     * @param datasetView
     */
    public void setDatasetView(int datasetView) {
        this.datasetView = datasetView;
    }
    
    public boolean isFullPage() {
        return fullPage;
    }

    public void setFullPage(boolean fullPage) {
        this.fullPage = fullPage;
    }

    public boolean isOutputToggle() {
        return outputToggle;
    }

    public void setOutputToggle(boolean outputToggle) {
        this.outputToggle = outputToggle;
    }

    public boolean isShowUndockedOutput() {
        return showUndockedOutput;
    }

    public void setShowUndockedOutput(boolean showUndockedOutput) {
        LOG.debug("setShowUndockedOutput: " + showUndockedOutput);
        this.showUndockedOutput = showUndockedOutput;
    }

    private void setSeriesView() {
        LOG.debug("setSeriesView.seriesView = " + this.seriesView);
        switch (this.seriesView) {
            case 0:
                switch (this.defaultSeriesView) {
                    case 1:
                        setCurrentView(ViewMode.seriesListView);
                        setCurrentSeriesView(ViewMode.seriesListView);
                        setListView(true);
                        break;
                    case 2:
                        setCurrentView(ViewMode.seriesThumbnailsView);
                        setCurrentSeriesView(ViewMode.seriesThumbnailsView);
                        setThumbnailsView(true);
                        break;
                    case 3:
                        setCurrentView(ViewMode.seriesDetails);
                        setCurrentSeriesView(ViewMode.seriesDetails);
                        setEntryView(true);
                        break;
                }
                break;
            case 1:
                setCurrentView(ViewMode.seriesListView);
                setListView(true);
                break;
            case 2:
                setCurrentView(ViewMode.seriesThumbnailsView);
                setCurrentSeriesView(ViewMode.seriesThumbnailsView);
                setThumbnailsView(true);
                break;
            case 3:
                setCurrentView(ViewMode.seriesDetails);
                setCurrentSeriesView(ViewMode.seriesDetails);
                setEntryView(true);
                break;
        }
    }

    private void setDatasetView() {
        switch (this.datasetView) {
            case 0:
                switch (this.defaultDatasetView) {
                    case 1:
                        setCurrentView(ViewMode.datasetListView);
                        setCurrentDatasetView(ViewMode.datasetListView);
                        setListView(true);
                        break;
                    case 2:
                        setCurrentView(ViewMode.datasetThumbnailsView);
                        setCurrentDatasetView(ViewMode.datasetThumbnailsView);
                        setThumbnailsView(true);
                        break;
                    case 3:
                        setCurrentView(ViewMode.datasetDetails);
                        setCurrentDatasetView(ViewMode.datasetDetails);
                        setEntryView(true);
                        break;
                }
                break;
            case 1:
                setCurrentView(ViewMode.datasetListView);
                setListView(true);
                break;
            case 2:
                setCurrentView(ViewMode.datasetThumbnailsView);
                setThumbnailsView(true);
                break;
            case 3:
                setCurrentView(ViewMode.datasetDetails);
                setEntryView(true);
                break;
        }
    }

    private void setRelatedView() {
        switch (this.relatedView) {
            case 0:
                switch (this.defaultRelatedView) {
                    case 1:
                        setCurrentView(ViewMode.relatedListView);
                        setCurrentRelatedView(ViewMode.relatedListView);
                        setListView(true);
                        break;
                    case 2:
                        setCurrentView(ViewMode.relatedThumbnailsView);
                        setCurrentRelatedView(ViewMode.relatedThumbnailsView);
                        setThumbnailsView(true);
                        break;
                    case 3:
                        setCurrentView(ViewMode.relatedDetails);
                        setCurrentRelatedView(ViewMode.relatedDetails);
                        setEntryView(true);
                        break;
                }
                break;
            case 1:
                setCurrentView(ViewMode.relatedListView);
                setListView(true);
                break;
            case 2:
                setCurrentView(ViewMode.relatedThumbnailsView);
                setThumbnailsView(true);
                break;
            case 3:
                setCurrentView(ViewMode.relatedDetails);
                setEntryView(true);
                break;
        }
    }

    public boolean isAoi() {
        return aoi;
    }

    public void setAoi(boolean aoi) {
        this.aoi = aoi;
    }

    /**
     * An action listener of AOI button that'd be executed when the button is
     * clicked.
     */
    public void activeAOI() {
        setAoi(true);
    }

    /**
     * An action listener of Pan button that'd be executed when the button is
     * clicked.
     */
    public void deactiveAOI() {
        setAoi(false);
    }    

    public boolean isVisibleSeriesTab() {
        return visibleSeriesTab;
    }

    public void setVisibleSeriesTab(boolean visibleSeriesTab) {
        this.visibleSeriesTab = visibleSeriesTab;
    }

    public boolean isVisibleDatasetTab() {
        return visibleDatasetTab;
    }

    public void setVisibleDatasetTab(boolean visibleDatasetTab) {
        this.visibleDatasetTab = visibleDatasetTab;
    }

    public boolean isActiveSeriesTab() {
        return activeSeriesTab;
    }

    public void setActiveSeriesTab(boolean activeSeriesTab) {
        this.activeSeriesTab = activeSeriesTab;
    }

    public boolean isActiveDatasetTab() {
        return activeDatasetTab;
    }

    public void setActiveDatasetTab(boolean activeDatasetTab) {
        this.activeDatasetTab = activeDatasetTab;
    }

    public ViewMode getCurrentSeriesView() {
        return currentSeriesView;
    }

    public void setCurrentSeriesView(ViewMode currentSeriesView) {
        this.currentSeriesView = currentSeriesView;
    }

    public ViewMode getCurrentDatasetView() {
        return currentDatasetView;
    }

    public void setCurrentDatasetView(ViewMode currentDatasetView) {
        this.currentDatasetView = currentDatasetView;
    }

    public boolean isVisibleRelatedTab() {
        return visibleRelatedTab;
    }

    public void setVisibleRelatedTab(boolean visibleRelatedTab) {
        this.visibleRelatedTab = visibleRelatedTab;
    }

    public boolean isActiveRelatedTab() {
        return activeRelatedTab;
    }

    public void setActiveRelatedTab(boolean activeRelatedTab) {
        this.activeRelatedTab = activeRelatedTab;
    }

    public ViewMode getCurrentRelatedView() {
        return currentRelatedView;
    }

    public void setCurrentRelatedView(ViewMode currentRelatedView) {
        this.currentRelatedView = currentRelatedView;
    }

    /**
     * Getter method
     *
     * @return true or false
     */
    public boolean isRelatedListView() {
        debug("isRelatedListView: " + this.currentView);
        return this.currentView == ViewMode.relatedListView;
    }

    public boolean isRelatedThumbnailsView() {
        debug("isRelatedThumbnailsView: " + this.currentView);
        return this.currentView == ViewMode.relatedThumbnailsView;
    }

    public boolean isRelatedDetails() {
        debug("isRelatedDetails: " + this.currentView);
        return this.currentView == ViewMode.relatedDetails;
    }

    public boolean isRelatedMode() {
        return relatedMode;
    }

    public int getDefaultRelatedView() {
        return defaultRelatedView;
    }

    public void setDefaultRelatedView(int defaultRelatedView) {
        this.defaultRelatedView = defaultRelatedView;
    }

    public int getRelatedView() {
        return relatedView;
    }

    public void setRelatedView(int relatedView) {
        this.relatedView = relatedView;
    }

    private void debug(String debugMsg) {
        // LOG.debug(debugMsg);
    }

    public String getSeriesToggleIcon() {
        LOG.debug("hasSeriesFilter = " + hasSeriesFilter);

        if (hasSeriesFilter) {
            return "fa fa-fw fa-filter";
        } else {
            return "fa fa-fw fa-sliders";
        }
    }

    public String getDatasetToggleIcon() {
        LOG.debug("hasDatasetFilter = " + hasDatasetFilter);

        if (hasDatasetFilter) {
            return "fa fa-fw fa-filter";
        } else {
            return "fa fa-fw fa-sliders";
        }
    }
}
