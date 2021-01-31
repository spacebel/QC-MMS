package be.spacebel.eoportal.client.model;

import be.spacebel.eoportal.client.business.ProcessManager;
import be.spacebel.eoportal.client.business.data.Constants;
import be.spacebel.eoportal.client.business.data.GeonamesOption;
import be.spacebel.eoportal.client.business.data.OpenSearchParameter;
import be.spacebel.eoportal.client.business.data.OpenSearchUrl;
import be.spacebel.eoportal.client.business.data.SearchResultError;
import be.spacebel.eoportal.client.business.data.SearchResultItem;
import be.spacebel.eoportal.client.business.data.SearchResultProperty;
import be.spacebel.eoportal.client.business.data.SearchResultSet;
import be.spacebel.eoportal.client.model.LayoutBean.SearchMode;
import be.spacebel.eoportal.client.model.data.Paginator;
import be.spacebel.eoportal.client.model.data.RelatedPaginator;
import be.spacebel.eoportal.client.model.data.SearchData;
import be.spacebel.eoportal.client.parser.GeoJSONSearchResultParser;
import be.spacebel.eoportal.client.util.FacesMessageUtil;
import be.spacebel.eoportal.client.util.OpenSearchParameterComparator;
import be.spacebel.eoportal.client.util.Utility;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.extensions.model.fluidgrid.FluidGridItem;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

/**
 * A managed bean contains business logic, form values, getter and setter
 * methods that will be used in the JSF Search pages
 *
 * @author mng
 */
@ManagedBean(name = "searchBean")
@ViewScoped
public class SearchBean implements Serializable {

    private static final Logger LOG = Logger.getLogger(SearchBean.class);

    @ManagedProperty(value = "#{configBean}")
    private ConfigurationBean configBean;

    @ManagedProperty(value = "#{layoutBean}")
    private LayoutBean layoutBean;

    private ProcessManager processManager;
    private SearchData searchData;

    private String selectedProductUuid;
    private String selectedSeries;

    //private String presentItem;
    @PostConstruct
    public void SearchBean() {
        try {
            LOG.debug("Service Bean init..........");

            /*
             * initial bean
             */
            processManager = new ProcessManager(this.configBean.getConfiguration());

            if (StringUtils.isNotEmpty(configBean.getConfiguration().getOsddLocation())) {
                /*
                 * get service data
                 */
                searchData = new SearchData();
                // If the series selection is configured, then retrieved the available series
                if (configBean.getConfiguration().getSeries().isSeriesMenu()) {
                    processManager.loadCatalogInfo();
                }
                this.layoutBean.setDefaultSeriesView(
                        configBean.getConfiguration().getSeries().getView());
                this.layoutBean.setDefaultDatasetView(
                        configBean.getConfiguration().getDataset().getView());
                this.layoutBean.setInitView(
                        configBean.getConfiguration().getSeries().getView());

                LOG.debug("Done.");
            } else {
                FacesMessageUtil.addErrorMessage(
                        "Please configure OpenSearch Description Document (OSDD) Url of the Catalogue");
            }
        } catch (IOException ex) {
            LOG.error(ex);

            FacesMessageUtil.addErrorMessage(ex);
        }
    }

    /**
     * An action listener of Entry View button that'd be executed when the
     * button is clicked.
     */
    public void clickEntryView() {
        LOG.debug("clickEntryView()......");
        if (this.getLayoutBean().isRelatedMode()) {
            if (this.searchData.getSelectedRelatedProduct() != null) {
                LOG.debug("Selected related product is not empty.");
                this.onSelectRelatedProduct(this.searchData.getSelectedRelatedProduct());
            } else {
                LOG.debug("Selected related product is empty.");
                /*
                    get the first related product
                 */
                SearchResultItem first = null;
                if (this.searchData.getRelatedPaginator() != null
                        && this.searchData.getRelatedPaginator().getData() != null
                        && this.searchData.getRelatedPaginator().getData().getItems() != null
                        && this.searchData.getRelatedPaginator().getData().getItems().size() > 0) {
                    LOG.debug("not null");
                    first = this.searchData.getRelatedPaginator().getData().getItems().get(0);
                }

                if (first != null) {
                    this.onSelectRelatedProduct(first);
                } else {
                    LOG.debug("View entry in case of no related search result.");
                    this.getLayoutBean().showEntryView();
                }
            }
        } else {
            if (this.getLayoutBean().isDatasetMode()) {
                if (this.searchData.getSelectedDatasetItem() != null) {
                    LOG.debug("Selected dataset item is not empty.");
                    this.onSelectDatasetItem(this.searchData.getSelectedDatasetItem());
                } else {
                    LOG.debug("Selected dataset item is empty.");
                    SearchResultItem first = getFirstItem(
                            this.searchData.getDatasetPaginator());
                    if (first != null) {
                        this.onSelectDatasetItem(first);
                    } else {
                        LOG.debug("View entry in case of no dataset search result.");
                        this.getLayoutBean().showEntryView();
                    }
                }
            }
        }
    }

    public void activateSeriesTab() {
        this.getLayoutBean().activateSeriesTab();
    }

    public void activateDatasetTab() {
        this.getLayoutBean().activateDatasetTab();
        if (layoutBean.isDatasetInit() || layoutBean.isSeriesDetails()) {
            searchData.setShowDatasetFootprint(false);
        } else {
            searchData.setShowDatasetFootprint(true);
        }
    }

    public void activateRelatedTab() {
        this.layoutBean.toRelatedResults();
        this.getLayoutBean().activateRelatedTab();
        searchData.setShowRelatedFootprint(true);
    }

    /**
     * An action listener of Advanced Search button that'd be executed when the
     * button is clicked.
     *
     * @param searchType - Search type: Related or Dataset
     */
    public void doAdvancedSearch(String searchType) {
        LOG.debug("doAdvancedSearch(" + searchType + ")");
        try {
            Map<String, String> inputParams = getCommonParamsValue();

            if (StringUtils.isNotEmpty(searchData.getBbox())) {
                LOG.debug("BBOX FOUND !! " + searchData.getBbox() + "-");
                inputParams.put("geo_box", searchData.getBbox());
            }

            Map<String, String> dateParams = new HashMap<>();
            switch (searchType) {
                case Constants.RELATED_SEARCH:
                    getFormParametersValues(inputParams,
                            searchData.getRelatedOpenSearchUrl(), dateParams);

                    validateStarEndDate(dateParams);

                    // reset
                    this.searchData.setRelatedPaginator(new RelatedPaginator());
                    this.searchData.setSelectedRelatedProduct(null);

                    Map<String, String> result = processManager.search(inputParams,
                            searchData.getRelatedOpenSearchUrl().getTemplateUrl(),
                            searchData.getRelatedOpenSearchUrl().getIndexOffset());

                    if (result != null) {
                        LOG.debug("handle related product search results");

                        String osResponse = result.get(Constants.OS_RESPONSE);
                        if (StringUtils.isNotEmpty(osResponse)) {
                            SearchResultSet relatedRs = processManager.parseSearchResults(
                                    osResponse,
                                    configBean.getConfiguration().getDataset().getListViewAttributes(),
                                    configBean.getConfiguration());

                            handleRelatedSearchResults(relatedRs, searchData.getSelectedDatasetItem().getProductId(), false);

                        } else {
                            LOG.debug("No related product search result");

                            FacesMessageUtil.addInfoMessage("search.noresult");
                            this.searchData.setRelatedPaginator(new RelatedPaginator());
                        }
                    } else {
                        LOG.debug("related product search result is null");
                    }

                    LOG.debug("change to related product search results view.");
                    if (this.getLayoutBean().isRelatedDetails()) {
                        LOG.debug("isEntryView = true");
                        SearchResultItem first = getFirstItem(this.searchData.getRelatedPaginator());
                        if (first != null) {
                            this.onSelectRelatedProduct(first);
                        } else {
                            LOG.debug("View entry in case of no related product search result.");
                            this.getLayoutBean().showEntryView();
                        }
                    } else {
                        LOG.debug("clear selected item because of a new search");
                        // clear selected item because of a new search
                        this.searchData.setSelectedRelatedProduct(null);
                    }

                    break;
                default:
                    getFormParametersValues(inputParams,
                            searchData.getDatasetOpenSearchUrl(), dateParams);

                    validateStarEndDate(dateParams);

                    // clean the selected dataset item
                    this.searchData.setSelectedDatasetItem(null);
                    //this.searchData.setDatasetPresentItem(null);

                    /*
                        Clean related results
                     */
                    this.searchData.setRelatedPaginator(new RelatedPaginator());
                    this.searchData.setSelectedRelatedProduct(null);

                    /*
                    * get selected collection
                     */
                    String colId = null;
                    if (this.searchData.getSelectedSeriesItem() != null) {
                        colId = this.searchData.getSelectedSeriesItem().getProductId();
                        LOG.debug("Selected collection: " + colId);
                    }

                    if (StringUtils.isNotEmpty(colId)) {
                        inputParams.put("eo_parentIdentifier", colId);
                        result = processManager.search(inputParams,
                                searchData.getDatasetOpenSearchUrl().getTemplateUrl(),
                                searchData.getDatasetOpenSearchUrl().getIndexOffset());

                        if (result != null) {
                            LOG.debug("handle product search results");
                            handleSearchResult(result, inputParams,
                                    searchData.getDatasetOpenSearchUrl().getTemplateUrl(),
                                    searchData.getDatasetOpenSearchUrl().getIndexOffset());
                        } else {
                            LOG.debug("product search result is null");
                        }

                    } else {
                        LOG.warn("error detected");
                        FacesMessageUtil.addWarningMessage("Please select one collection.");
                    }

                    LOG.debug("change to product search results view.");
                    this.getLayoutBean().toDatasetResults();
                    toFirstEntryView();
            }
        } catch (SearchException e) {
            FacesMessageUtil.addErrorMessageWithDetails(e.getTitle(), e.getDetail());
        } catch (IOException e) {
            LOG.debug("error detected");
            FacesMessageUtil.addErrorMessageWithDetails("unexpectederror",
                    e.getMessage());
        }
    }

    public void reset(String searchType) {
        LOG.debug("reset(" + searchType + ")");
        switch (searchType) {
            case Constants.RELATED_SEARCH:
                reset(searchData.getRelatedOpenSearchUrl());
                break;
            default:
                reset(searchData.getDatasetOpenSearchUrl());
        }
    }

    private void reset(OpenSearchUrl osUrl) {
        this.searchData.setBbox(null);
        if (osUrl.getParameters() != null) {
            for (OpenSearchParameter osParam : osUrl.getParameters()) {
                osParam.setFormValue(null);
                osParam.setSelectedOption(null);
            }
        }
    }

    /**
     * Action listener for the selection performed on the fixed selectonemenu
     */
    public void onSelectSeriesFromMenu() {
        String selection = this.getSelectedSeries();
        SearchResultItem dummySearchResultItem = new SearchResultItem(
                Constants.SERIES_SEARCH);
        dummySearchResultItem.setProductId(selection);
        this.searchData.setSelectedSeriesItem(dummySearchResultItem);
        this.searchData.setSupportedDatasetSearch(true);
        LOG.debug("loading osdd");
        this.loadDatasetOpenSearchUrl();
        LOG.debug("change to collection details view.");
        this.getLayoutBean().toSeriesDetails();

        /*
            Clean related results
         */
        this.searchData.setRelatedPaginator(new RelatedPaginator());
        this.searchData.setSelectedRelatedProduct(null);

    }

    /**
     * An action listener that'd be executed to show the dataset details
     *
     * @param selectedItem - Selected dataset item
     */
    public void onSelectDatasetItem(SearchResultItem selectedItem) {
        LOG.debug("On select dataset item");
        this.searchData.setSelectedDatasetItem(selectedItem);
        //searchData.setDatasetPresentItem(selectedItem);
        lookupRelatedProducts(selectedItem);
        LOG.debug("change to product details view.");
        this.getLayoutBean().toDatasetDetails();
    }

    public void onSelectDatasetItem(final String datasetUuid) {
        LOG.debug("Select dataset " + datasetUuid);
        boolean found = false;
        if (searchData.getDatasetPaginator().getData() != null
                && searchData.getDatasetPaginator().getData().getItems() != null) {
            for (SearchResultItem item : searchData.getDatasetPaginator().getData().getItems()) {
                if (item.getUuid().equals(datasetUuid)) {
                    //searchData.setDatasetPresentItem(item);
                    this.searchData.setSelectedDatasetItem(item);
                    LOG.debug("found the item");
                    found = true;
                    lookupRelatedProducts(item);
                    break;
                }
            }
        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("datasetUuid", datasetUuid);
        context.addCallbackParam("found", found);
    }

    private void lookupRelatedProducts(SearchResultItem selectedItem) {
        LOG.debug("Look up related products of " + selectedItem.getProductId());
        /*
            get all related product of selected product
         */
        this.searchData.setRelatedPaginator(new RelatedPaginator());
        this.searchData.setSelectedRelatedProduct(null);
        this.layoutBean.setVisibleRelatedTab(false);

        if (StringUtils.isNotEmpty(selectedItem.getRelatedProductsLink())) {
            LOG.debug("Related products link " + selectedItem.getRelatedProductsLink());
            try {
                SearchResultSet relatedRs = processManager
                        .retrieveRelatedProducts(selectedItem.getRelatedProductsLink(),
                                configBean.getConfiguration(),
                                configBean.getConfiguration().getDataset().getListViewAttributes());

                handleRelatedSearchResults(relatedRs, selectedItem.getProductId(), true);

            } catch (IOException e) {
                LOG.error(e);
                FacesMessageUtil.addErrorMessageWithDetails("unexpectederror",
                        e.getMessage());
            }
        } else {
            LOG.debug("No related products link");
        }
    }

    private void handleRelatedSearchResults(SearchResultSet relatedRs, String relatedByProductId, boolean loadOSDD) {
        SearchResultError error = relatedRs.getSearchResultError();
        if (error != null) {
            showError(error);
        } else {
            if (relatedRs.getItems().isEmpty()) {
                LOG.debug("NO RESULTS");
                FacesMessageUtil.addInfoMessage("No related products");
            } else {
                LOG.debug("HAS RESULTS");
                int total = -1;
                try {
                    String strTotalResults = relatedRs.getAttributes()
                            .get(Constants.OS_TOTAL_RESULTS);
                    if (StringUtils.isNotEmpty(strTotalResults)) {
                        total = Integer.parseInt(strTotalResults);
                    }
                } catch (NumberFormatException e) {
                    LOG.debug(e.getMessage());
                }

                int itemsPerPage = 10;
                try {
                    String pageSize = relatedRs.getAttributes()
                            .get("itemsPerPage");
                    if (StringUtils.isNotEmpty(pageSize)) {
                        itemsPerPage = Integer.parseInt(pageSize);
                    }
                } catch (NumberFormatException e) {
                    LOG.debug(e.getMessage());
                }

                String firstUrl = relatedRs.getAttributes().get(Constants.OS_FIRSTPAGE);
                LOG.debug("First related Url " + firstUrl);

                String previousUrl = relatedRs.getAttributes().get(Constants.OS_PREVIOUSPAGE);
                LOG.debug("Previous related Url " + previousUrl);

                String nextUrl = relatedRs.getAttributes().get(Constants.OS_NEXTPAGE);
                LOG.debug("Next related Url " + nextUrl);

                String lastUrl = relatedRs.getAttributes().get(Constants.OS_LASTPAGE);
                LOG.debug("Last related Url " + lastUrl);

                if (loadOSDD) {
                    String osddUrl = relatedRs.getAttributes().get(Constants.OS_OSDD_URL);
                    LOG.debug("Related OSDD Url " + osddUrl);
                    if (StringUtils.isNotEmpty(osddUrl)) {
                        loadRelatedOpenSearchUrl(osddUrl);
                    }
                }

                RelatedPaginator relatedPaginator = new RelatedPaginator(firstUrl, previousUrl, nextUrl, lastUrl, total,
                        relatedRs.getItems(), itemsPerPage, relatedByProductId);

                relatedPaginator.getData().setGmlFeatures(relatedRs.getGmlFeatures());

                this.searchData.setRelatedPaginator(relatedPaginator);
                this.layoutBean.setVisibleRelatedTab(true);
            }
        }
    }

    public void onSelectRelatedProduct(SearchResultItem selectedProduct) {
        LOG.debug("On select related product");
        this.searchData.setSelectedRelatedProduct(selectedProduct);
        LOG.debug("change to related product details view.");
        this.getLayoutBean().toRelatedDetails();
    }

    public void onSelectRelatedProduct(final String relatedProductUuid) {
        LOG.debug("Select related product " + relatedProductUuid);
        boolean found = false;
        if (searchData.getRelatedPaginator() != null && searchData.getRelatedPaginator().getData().getItems() != null) {
            for (SearchResultItem item : searchData.getRelatedPaginator().getData().getItems()) {
                if (item.getUuid().equals(relatedProductUuid)) {
                    this.searchData.setSelectedRelatedProduct(item);
                    LOG.debug("found the related product");
                    found = true;
                    break;
                }
            }
        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("relatedProductUuid", relatedProductUuid);
        context.addCallbackParam("found", found);
    }

    /**
     * An action listener that'd be executed to show the target page of dataset
     * search results
     *
     * @param where - The target page
     */
    public void datasetPageNavigate(String where) {
        if (this.searchData.getDatasetPaginator() != null) {
            this.searchData.getDatasetPaginator().pageNavigate(where);
            this.searchData.setSelectedDatasetItem(null);
        }
    }

    /**
     * An action listener that'd be executed to show the next or previous
     * dataset item details
     *
     * @param target - The target (next or prev)
     */
    public void navigateDatasetItemDetails(String target) {
        LOG.debug("Navigate dataset item details: " + target);
        //int index = searchData.getDatasetPresentItem().getIndex();
        int index = searchData.getSelectedDatasetItem().getIndex();

        LOG.debug("current index: " + index);
        int itemCount = searchData.getDatasetPaginator().getData().getItemCount();
        LOG.debug("Number of records of the current page: " + itemCount);

        if ("next".equals(target)) {
            LOG.debug("go next");
            if ((index + 1) >= itemCount) {
                LOG.debug("approach the last record of the current page");
                if (StringUtils.isNotEmpty(searchData.getDatasetPaginator().getNext())) {
                    LOG.debug("Has the next page, continue.");
                    searchData.getDatasetPaginator().pageNavigate("next");
                    index = 0;
                } else {
                    LOG.debug("Has no next page, stop.");
                }
            } else {
                LOG.debug("continue normal");
                index += 1;
            }
        }
        if ("prev".equals(target)) {
            LOG.debug("back previous");
            if (index <= 0) {
                LOG.debug("approach the first record of the current page");
                if (StringUtils.isNotEmpty(
                        searchData.getDatasetPaginator().getPrevious())) {
                    LOG.debug("Has the previous page, continue.");
                    searchData.getDatasetPaginator().pageNavigate("prev");
                    index = searchData.getDatasetPaginator().getData().getItemCount() - 1;
                } else {
                    LOG.debug("Has no previous page, stop.");
                }
            } else {
                index -= 1;
                LOG.debug("back normal");
            }
        }
        LOG.debug("navigated index: " + index);

        if (searchData.getDatasetPaginator().getData() != null
                && searchData.getDatasetPaginator().getData().getItems() != null) {
            try {
                SearchResultItem selectedItem = searchData
                        .getDatasetPaginator()
                        .getData()
                        .getItems().get(index);
                this.onSelectDatasetItem(selectedItem);
                //this.searchData.setSelectedDatasetItem(selectedItem);
            } catch (IndexOutOfBoundsException e) {
                LOG.debug("Navigate dataset item details error: " + e.getMessage());
            }
        }

    }

    public void datasetJumpToPage(final Integer targetPage) {
        LOG.debug("Dataset --> jump to page: " + targetPage);
        if (targetPage < 1) {
            searchData.getDatasetPaginator().toBackupPage();
            FacesMessageUtil.addErrorMessage(
                    "Page number should be greater or equal 1.");
        } else {
            if (searchData.getDatasetPaginator() != null
                    && searchData.getDatasetPaginator().getData() != null
                    && searchData.getDatasetPaginator().getData().getItems() != null) {
                searchData.getDatasetPaginator().jumpToPage(targetPage);
            } else {
                LOG.debug("Don't jump.");
            }
        }
    }

    public void relatedPageNavigate(String where) {
        if (this.searchData.getRelatedPaginator() != null) {
            this.searchData.getRelatedPaginator().pageNavigate(where, processManager, configBean.getConfiguration());
            this.searchData.setSelectedRelatedProduct(null);
        }
    }

    public void navigateRelatedProductDetails(String target) {
        LOG.debug("Navigate related product details: " + target);

        //int index = searchData.getDatasetPresentItem().getIndex();
        int index = searchData.getSelectedRelatedProduct().getIndex();

        LOG.debug("current index: " + index);
        int itemCount = searchData.getRelatedPaginator().getData().getItemCount();
        LOG.debug("Number of records of the current page: " + itemCount);

        if ("next".equals(target)) {
            LOG.debug("go next");
            if ((index + 1) >= itemCount) {
                LOG.debug("approach the last record of the current page");
                if (StringUtils.isNotEmpty(searchData.getRelatedPaginator().getNext())) {
                    LOG.debug("Has the next page, continue.");
                    searchData.getRelatedPaginator().pageNavigate("next", processManager, configBean.getConfiguration());
                    index = 0;
                } else {
                    LOG.debug("Has no next page, stop.");
                }
            } else {
                LOG.debug("continue normal");
                index += 1;
            }
        }
        if ("prev".equals(target)) {
            LOG.debug("back previous");
            if (index <= 0) {
                LOG.debug("approach the first record of the current page");
                if (StringUtils.isNotEmpty(
                        searchData.getRelatedPaginator().getPrevious())) {
                    LOG.debug("Has the previous page, continue.");
                    searchData.getRelatedPaginator().pageNavigate("prev", processManager, configBean.getConfiguration());
                    index = searchData.getRelatedPaginator().getData().getItemCount() - 1;
                } else {
                    LOG.debug("Has no previous page, stop.");
                }
            } else {
                index -= 1;
                LOG.debug("back normal");
            }
        }
        LOG.debug("navigated index: " + index);

        if (searchData.getRelatedPaginator().getData() != null
                && searchData.getRelatedPaginator().getData().getItems() != null) {
            try {
                SearchResultItem selectedItem = searchData
                        .getRelatedPaginator()
                        .getData()
                        .getItems().get(index);
                this.onSelectRelatedProduct(selectedItem);
                //this.searchData.setSelectedDatasetItem(selectedItem);
            } catch (IndexOutOfBoundsException e) {
                LOG.debug("Navigate related product item details error: " + e.getMessage());
            }
        }
        /*
        this.searchData.setSelectedRelatedProduct(this.searchData
                .getRelatedPaginator()
                .detailsNavigate(target, processManager, configBean.getConfiguration()));
         */
    }

    public void relatedJumpToPage(final Integer targetPage) {
        LOG.debug("Related --> jump to page: " + targetPage);
        if (targetPage < 1) {
            FacesMessageUtil.addErrorMessage(
                    "Page number should be greater or equal 1.");
        } else {
            if (searchData.getRelatedPaginator() != null) {
                if (searchData.getRelatedPaginator().getPageCount() < targetPage) {
                    FacesMessageUtil.addErrorMessage("Page number should be less than " + searchData.getRelatedPaginator().getPageCount());
                } else {
                    searchData.getRelatedPaginator().jumpToPage(targetPage, processManager, configBean.getConfiguration());
                }
            }
        }
    }

    public void onSwitchAdvancedIcon(final Boolean hasFilter) {
        String sType = "Series";
        if (!layoutBean.isActiveSeriesTab()) {
            sType = "Dataset";
        }
        LOG.debug(
                "Switch advanced icon, sType = " + sType + ", hasFilter = " + hasFilter);

        if (layoutBean.isActiveSeriesTab()) {
            if (hasFilter) {
                this.layoutBean.setHasSeriesFilter(true);
            } else {
                this.layoutBean.setHasSeriesFilter(false);
            }

        } else {
            if (hasFilter) {
                this.layoutBean.setHasDatasetFilter(true);
            } else {
                this.layoutBean.setHasDatasetFilter(false);
            }

        }
    }

    /**
     * Compose the GML Features of the current search results to display as the
     * footprints
     *
     * @param searchType - Search type: Series or Dataset
     */
    public void obtainGmlFeatures(String searchType) {
        LOG.debug("obtainGmlFeatures(" + searchType + ")");
        boolean isSuccess = false;
        if ("Related".equalsIgnoreCase(searchType)) {
            LOG.debug("Looking for the Related GML Feature");
            if (searchData.getRelatedPaginator() != null
                    && searchData.getRelatedPaginator().getData() != null
                    && StringUtils.isNotEmpty(searchData.getRelatedPaginator().getData().getGmlFeatures())) {

                this.searchData.setRelatedGMLFeatures(
                        searchData.getRelatedPaginator().getData().getGmlFeatures().replaceAll(
                                "http://www.opengis.net/gml/3.2",
                                "http://www.opengis.net/gml"));
                this.searchData.setRelatedSelectedFeature("");
                isSuccess = true;
                LOG.debug("Related GML: " + this.searchData.getRelatedGMLFeatures());
            }
        } else {
            LOG.debug("Looking for the Dataset GML Feature");
            if (searchData.getDatasetPaginator() != null && StringUtils.isNotEmpty(
                    searchData.getDatasetPaginator().getGmlFeatures())) {
                this.searchData.setDatasetGMLFeatures(
                        searchData.getDatasetPaginator().getGmlFeatures().replaceAll(
                                "http://www.opengis.net/gml/3.2",
                                "http://www.opengis.net/gml"));
                this.searchData.setDatasetSelectedFeature("");
                isSuccess = true;
                LOG.debug("Dataset GML: " + this.searchData.getDatasetGMLFeatures());
            }
        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("searchType", searchType);
        context.addCallbackParam("success", isSuccess);
    }

    public void onToggleUndockedOutput() {
        LOG.debug(
                "onToggleUndockedOutput: showUndockedOutput = " + layoutBean.isShowUndockedOutput());

        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("open", layoutBean.isShowUndockedOutput());
    }

    /**
     * An action listener that'd be executed to highlight/unhighlight a
     * footprint
     *
     * @param searchType - Search type: Series or Dataset
     */
    public void showOnMap(String searchType) {
        LOG.debug(" Show on map: " + searchType);
        String selectedUuid = "";
        //String hasFootPrint = "";
        boolean isSuccess = false;
        String prevSelectedFeature = "";

        if (this.searchData != null) {
            if ("Related".equalsIgnoreCase(searchType)) {
                prevSelectedFeature = this.searchData.getRelatedSelectedFeature();

                if (this.searchData.getSelectedRelatedProduct() != null) {
                    selectedUuid = this.searchData.getSelectedRelatedProduct().getUuid();

                    if (this.searchData.getSelectedRelatedProduct().isHasFootprint()) {
                        this.searchData.setRelatedSelectedFeature(selectedUuid);
                        isSuccess = true;
                    }
                }
            } else {
                prevSelectedFeature = this.searchData.getDatasetSelectedFeature();

                if (this.searchData.getSelectedDatasetItem() != null) {
                    selectedUuid = this.searchData.getSelectedDatasetItem().getUuid();

                    if (this.searchData.getSelectedDatasetItem().isHasFootprint()) {
                        this.searchData.setDatasetSelectedFeature(selectedUuid);
                        isSuccess = true;
                    }
                }

            }
            LOG.debug("selectedUuid = " + selectedUuid);

        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("success", isSuccess);
        context.addCallbackParam("selectedUuid", selectedUuid);
        context.addCallbackParam("prevSelectedUuid", prevSelectedFeature);
    }

    public void cleanBbox() {
        this.searchData.setBbox(null);
    }

    /**
     * Get the paginator of the current search mode (series or dataset)
     *
     * @return the corresponding paginator of the current search mode
     */
    public Paginator getPaginator() {
        if (this.layoutBean.getCurrentSearchMode() == SearchMode.dataset) {
            // todo
            return this.searchData.getDatasetPaginator();
        }
        return null;
    }

    private SearchResultItem getFirstItem(Paginator page) {
        LOG.debug("getFirstItem:");
        if (page != null && page.getData() != null && page.getData().getItems() != null && page.getData().getItems().size() > 0) {
            LOG.debug("not null");
            return page.getData().getItems().get(0);
        }
        LOG.debug("is null");
        return null;
    }

    private SearchResultItem getFirstItem(RelatedPaginator page) {
        LOG.debug("getFirstItem:");
        if (page != null && page.getData() != null && page.getData().getItems() != null && page.getData().getItems().size() > 0) {
            LOG.debug("not null");
            return page.getData().getItems().get(0);
        }
        LOG.debug("is null");
        return null;
    }

    private Map<String, String> getCommonParamsValue() {
        Map<String, String> params = new HashMap<>();
        /*
         * set default values for common parameters
         */
        params.put("os_count",
                "" + this.configBean.getConfiguration().getRowsPerPage());
        params.put("sru_recordSchema", "server-choice");
        params.put("startIndex", "1");
        return params;
    }

    private void handleSearchResult(Map<String, String> result,
            Map<String, String> inputParams, String templateUrl, int indexOffset) {
        try {
            LOG.debug("handle search result");
            String osResponse = result.get(Constants.OS_RESPONSE);
            //log.debug(osResponse);
            if (StringUtils.isNotEmpty(osResponse)) {
                // LOG.debug(osResponse);

                searchData.setDatasetSearchResultSet(
                        processManager.parseSearchResults(
                                osResponse,
                                configBean.getConfiguration().getDataset().getListViewAttributes(),
                                configBean.getConfiguration()));

                SearchResultError error = searchData.getDatasetSearchResultSet().getSearchResultError();
                if (error != null) {
                    showError(error);
                    searchData.setDatasetPaginator(null);
                } else {
                    if (searchData.getDatasetSearchResultSet().getItems().isEmpty()) {
                        LOG.debug("NO RESULTS");
                        FacesMessageUtil.addInfoMessage("search.noresult");
                        searchData.setDatasetPaginator(new Paginator(true));
                    } else {
                        LOG.debug("HAS RESULTS");
                        int total = -1;
                        try {
                            String strTotalResults = searchData.getDatasetSearchResultSet().getAttributes().get(
                                    Constants.OS_TOTAL_RESULTS);
                            if (StringUtils.isNotEmpty(strTotalResults)) {
                                total = Integer.parseInt(strTotalResults);
                            }
                        } catch (NumberFormatException e) {
                            LOG.debug(e.getMessage());
                        }
                        LOG.debug("this line is executed");

                        String firstPageUrl = searchData.getDatasetSearchResultSet().getAttributes().get(
                                Constants.OS_FIRSTPAGE);
                        String previousPageUrl = searchData.getDatasetSearchResultSet().getAttributes().get(
                                Constants.OS_PREVIOUSPAGE);
                        String nextPageUrl = searchData.getDatasetSearchResultSet().getAttributes().get(
                                Constants.OS_NEXTPAGE);
                        String lastPageUrl = searchData.getDatasetSearchResultSet().getAttributes().get(
                                Constants.OS_LASTPAGE);

                        Paginator paginator = new Paginator(firstPageUrl, previousPageUrl,
                                nextPageUrl, lastPageUrl,
                                total, searchData.getDatasetSearchResultSet().getItems(),
                                configBean.getConfiguration().getRowsPerPage(),
                                configBean.getConfiguration().getDataset().getListViewAttributes(),
                                searchData.getDatasetSearchResultSet().getGmlFeatures());

                        paginator.setProcessManager(processManager);
                        paginator.setConfig(this.configBean.getConfiguration());

                        paginator.setInputParams(inputParams);
                        paginator.setTemplateUrl(templateUrl);
                        paginator.setIndexOffset(indexOffset);

                        searchData.setDatasetPaginator(paginator);
                    }
                }

            } else {
                LOG.debug("No search result");
                FacesMessageUtil.addInfoMessage("search.noresult");
                searchData.setDatasetPaginator(new Paginator(true));

            }
        } catch (Exception e) {
            FacesMessageUtil.addErrorMessage(e);
            //return;
        }
    }

    private void showError(SearchResultError error) {
        LOG.debug("ERROR: " + error.getErrorCode() + " ; " + error.getErrorMessage());
        String errMsg = "";
        if (StringUtils.isNotEmpty(error.getExceptionCode())) {
            errMsg = error.getExceptionCode() + ": ";
        }
        if (StringUtils.isNotEmpty(error.getErrorMessage())) {
            errMsg = errMsg + error.getErrorMessage();
        }
        if (StringUtils.isNotEmpty(error.getErrorCode())) {
            errMsg = errMsg + "(" + error.getErrorCode() + ")";
        }
        if (StringUtils.isNotEmpty(errMsg)) {
            FacesMessageUtil.addErrorMessage(errMsg);
        } else {
            FacesMessageUtil.addErrorMessage("error.duringprocess");
        }
    }

    private void getFormParametersValues(Map<String, String> params,
            OpenSearchUrl osUrl, Map<String, String> dateParams) throws SearchException {
        if (osUrl.getParameters() != null) {
            for (OpenSearchParameter osParam : osUrl.getParameters()) {
                osParam.validate();
                LOG.debug(osParam.getName() + "=" + osParam.getFormValue());
                if (StringUtils.isNotEmpty(osParam.getFormValue())) {
                    if (Constants.DATE_TYPE.equals(osParam.getType())) {
                        String strDate = StringUtils.trim(osParam.getFormValue());
                        if (strDate.length() == "yyyy-MM-dd HH:mm:ss".length() && strDate.contains(
                                " ")) {
                            strDate = StringUtils.replace(strDate, " ", "T") + "Z";
                            params.put(osParam.getName(), strDate);
                        } else {
                            if (strDate.length() == "yyyy-MM-dd".length()) {
                                if (Utility.matchParameter(osParam.getNamespace(),
                                        osParam.getValue(), Constants.TIME_NAMESPACE,
                                        Constants.TIME_END)) {
                                    strDate += "T23:59:59Z";
                                    dateParams.put(Constants.TIME_END, strDate);
                                    /*} if ("time_end".equals(osParam.getName())) {*/

                                } else {
                                    strDate += "T00:00:00Z";
                                    if (Utility.matchParameter(osParam.getNamespace(),
                                            osParam.getValue(), Constants.TIME_NAMESPACE,
                                            Constants.TIME_START)) {
                                        dateParams.put(Constants.TIME_START, strDate);
                                    }
                                }
                                params.put(osParam.getName(), strDate);
                            } else {
                                LOG.debug("Value of " + osParam.getLabel() + " is invalid.");
                            }
                        }
                        LOG.debug(osParam.getName() + " = " + strDate);
                    } else {
                        params.put(osParam.getName(), osParam.getFormValue());
                    }
                }
            }
        }
    }

    private void loadDatasetOpenSearchUrl() {
        try {
            /*
             * get current values and put them in a hashmap
             */
            String productId = null;
            if (this.searchData.getSelectedSeriesItem() != null) {
                LOG.debug("Series selection is not null.");
                productId = this.searchData.getSelectedSeriesItem().getProductId();
                LOG.debug("productId = " + productId);
            }

            String selSeries = productId;

            if (StringUtils.isNotEmpty(selSeries)) {
                LOG.debug("load specific product search Url: " + selSeries);

                String searchDatasetOsddURL = null;
                if (StringUtils.isNotEmpty(configBean.getConfiguration().getDatasetOsddTemplateUrl())
                        && configBean.getConfiguration().getDatasetOsddTemplateUrl().contains("{eo:parentIdentifier}")) {
                    LOG.debug("Has dataset OSDD URL " + configBean.getConfiguration().getDatasetOsddTemplateUrl());
                    searchDatasetOsddURL = configBean.getConfiguration().getDatasetOsddTemplateUrl().replaceAll("\\{eo:parentIdentifier}", selSeries);
                    LOG.debug("searchDatasetOsddURL = " + searchDatasetOsddURL);
                } else {
                    String catalogEndpoint = StringUtils.substringBeforeLast(configBean.getConfiguration().getOsddLocation(), "/description");
                    searchDatasetOsddURL = catalogEndpoint + "/series/" + selSeries + "/description";
                }

                LOG.debug("searchDatasetOsddURL: " + searchDatasetOsddURL);
                if (StringUtils.isNotEmpty(searchDatasetOsddURL)) {
                    OpenSearchUrl osUrl = processManager.getDatasetOpenSearchUrl(
                            searchDatasetOsddURL,
                            configBean.getConfiguration().getDataset().getDatasetSearchFormat());
                    if (osUrl != null) {
                        if (osUrl.getParameters() != null) {

                            OpenSearchParameter pStartDate = null;
                            OpenSearchParameter pEndDate = null;
                            int count = 1;

                            for (OpenSearchParameter osParam : osUrl.getParameters()) {

                                if (Utility.matchParameter(osParam.getNamespace(),
                                        osParam.getValue(), Constants.TIME_NAMESPACE,
                                        Constants.TIME_START)) {
                                    pStartDate = osParam;
                                }

                                if (Utility.matchParameter(osParam.getNamespace(),
                                        osParam.getValue(), Constants.TIME_NAMESPACE,
                                        Constants.TIME_END)
                                        && StringUtils.isNotEmpty(osParam.getMaxDate())) {
                                    pEndDate = osParam;
                                }

                                String paramKey;
                                if (osParam.getValue().indexOf(":") > 0) {
                                    String localName = StringUtils.substringAfter(
                                            osParam.getValue(), ":");
                                    paramKey = localName + "#" + osParam.getNamespace();
                                } else {
                                    paramKey = osParam.getValue() + "#" + Constants.OS_NAMESPACE;
                                }

                                LOG.debug("paramKey = " + paramKey);
                                if (configBean.getDatasetBlacklist().contains(paramKey)) {
                                    LOG.debug(
                                            "This param is in the blacklist. Do not show it: " + osParam.getName());
                                    osParam.setShow(false);
                                }

                                // set order of parameter
                                count = setParamOrder(osParam,
                                        configBean.getConfiguration().getDataset().getOrderedParameters(),
                                        count);

                            }

                            checkDate(pStartDate, pEndDate, false);

                            /*
                             display filter icon if any parameter has default value
                             */
                            layoutBean.cleanDatasetFilterValues();

                            for (OpenSearchParameter osParam : osUrl.getParameters()) {
                                if (StringUtils.isNotEmpty(osParam.getFormValue())) {
                                    layoutBean.setHasDatasetFilter(true);
                                    //layoutBean.addDatasetFilterValues(false);
                                    break;
                                }
                            }

                            OpenSearchParameterComparator.sort(osUrl.getParameters());

                        }
                        searchData.setDatasetOpenSearchUrl(osUrl);
                        searchData.setDatasetParentId(selSeries);

                        if (this.searchData.getSelectedSeriesItem().getProperties() != null) {
                            SearchResultProperty titleProp = this.searchData.getSelectedSeriesItem().getProperties().get(
                                    "title");
                            if (titleProp != null) {
                                searchData.setDatasetParentTitle(titleProp.getValue());
                            }
                        }
                    }
                } else {
                    LOG.error(
                            "The search product OSDD Url of the selected collection " + selSeries + " is empty !");
                }
            }

        } catch (IOException e) {
            LOG.error(e);
            FacesMessageUtil.addErrorMessageWithDetails("unexpectederror",
                    e.getMessage());
        }
    }

    private void loadRelatedOpenSearchUrl(String osddUrl) {
        try {
            OpenSearchUrl osUrl = processManager
                    .getDatasetOpenSearchUrl(osddUrl,
                            configBean.getConfiguration().getDataset().getDatasetSearchFormat());
            if (osUrl != null) {
                if (osUrl.getParameters() != null) {

                    OpenSearchParameter pStartDate = null;
                    OpenSearchParameter pEndDate = null;
                    int count = 1;

                    for (OpenSearchParameter osParam : osUrl.getParameters()) {
                        if (Utility.matchParameter(osParam.getNamespace(),
                                osParam.getValue(), Constants.TIME_NAMESPACE,
                                Constants.TIME_START)) {
                            pStartDate = osParam;
                        }

                        if (Utility.matchParameter(osParam.getNamespace(),
                                osParam.getValue(), Constants.TIME_NAMESPACE,
                                Constants.TIME_END)
                                && StringUtils.isNotEmpty(osParam.getMaxDate())) {
                            pEndDate = osParam;
                        }

                        String paramKey;
                        if (osParam.getValue().indexOf(":") > 0) {
                            String localName = StringUtils.substringAfter(
                                    osParam.getValue(), ":");
                            paramKey = localName + "#" + osParam.getNamespace();
                        } else {
                            paramKey = osParam.getValue() + "#" + Constants.OS_NAMESPACE;
                        }

                        LOG.debug("paramKey = " + paramKey);
                        if (configBean.getDatasetBlacklist().contains(paramKey)) {
                            LOG.debug("This param is in the blacklist. Do not show it: " + osParam.getName());
                            osParam.setShow(false);
                        }

                        // set order of parameter
                        count = setParamOrder(osParam,
                                configBean.getConfiguration().getDataset().getOrderedParameters(),
                                count);

                    }

                    checkDate(pStartDate, pEndDate, false);

                    OpenSearchParameterComparator.sort(osUrl.getParameters());

                }
                searchData.setRelatedOpenSearchUrl(osUrl);
            }

        } catch (IOException e) {
            LOG.error(e);
            FacesMessageUtil.addErrorMessageWithDetails("unexpectederror",
                    e.getMessage());
        }
    }

    private int setParamOrder(OpenSearchParameter osParam,
            List<String> orderedParameters, int count) {
        String orderParam = osParam.getLabel() + " (" + osParam.getValue() + ")";
        LOG.debug("Ordered param: " + orderParam);

        int paramIndex = orderedParameters.indexOf(orderParam);
        LOG.debug("Param index: " + paramIndex);

        if (paramIndex > -1) {
            osParam.setOrder(paramIndex);
        } else {
            osParam.setOrder(200 + count);
            count++;
        }
        return count;
    }

    /**
     * Build the context menu for More Options button
     *
     * @param item - Selected series/dataset item
     * @param type - Series or Dataset
     * @return Primefaces MenuModel
     */
    public MenuModel buildMenuModel(SearchResultItem item, String type) {
        MenuModel menu = new DefaultMenuModel();

        String mediaQuicklook = Utility.getPropertyValue(item, "mediaQuicklook");
        if (StringUtils.isNotEmpty(mediaQuicklook)) {
            menu.addElement(buildMenuItem("Media quicklook", mediaQuicklook,
                    "Media quicklook", "fa fa-fw fa-image"));
        }

        String mediaThumbnail = Utility.getPropertyValue(item, "mediaThumbnail");
        if (StringUtils.isNotEmpty(mediaThumbnail)) {
            menu.addElement(buildMenuItem("Media thumbnail", mediaThumbnail,
                    "Media thumbnail", "fa fa-fw fa-image"));

        }

        String mediaCloud = Utility.getPropertyValue(item, "mediaCloud");
        if (StringUtils.isNotEmpty(mediaCloud)) {
            menu.addElement(buildMenuItem("Media cloud mask", mediaCloud,
                    "Media cloud mask", "fa fa-fw fa-cloud"));

        }

        /*
         Check if the following additional elements are to be accessible to a user via a "More" button 
         */
        List<String> extLinksOptions = this.configBean.getConfiguration().getDataset().getListMoreOptions();

        if (extLinksOptions != null) {
            if (extLinksOptions.contains("describedby")) {
                buildMenuItemFromExternalLinks(menu, item.getProperties().get(
                        "describedbyLinks"));
            }
            if (extLinksOptions.contains("alternate")) {
                buildMenuItemFromExternalLinks(menu, item.getProperties().get(
                        "alternateLinks"));
            }
            if (extLinksOptions.contains("via")) {
                buildMenuItemFromExternalLinks(menu,
                        item.getProperties().get("viaLinks"));
            }
            if (extLinksOptions.contains("qualityReport")) {
                buildMenuItemFromExternalLinks(menu,
                        item.getProperties().get("qualityReportLinks"));
            }
            if (extLinksOptions.contains("id")) {
                String productIdLink = Utility.getPropertyValue(item, "productIdLink");
                if (StringUtils.isNotEmpty(productIdLink)) {
                    menu.addElement(
                            buildMenuItem("GeoJSON metadata (application/geo+json)",
                                    productIdLink, "GeoJSON metadata (application/geo+json)", "fa fa-fw fa-save"));
                }
            }
            if (extLinksOptions.contains("voilaReport")
                    && StringUtils.isNotEmpty(item.getVoilaReportLink())) {
                menu.addElement(buildMenuItem("View QCMMS Report (text/html)",
                        item.getVoilaReportLink(),
                        "View QCMMS Report (text/html)", "spb-report-icon"));
            }
        }

        menu.generateUniqueIds();
        return menu;
    }

    private void buildMenuItemFromExternalLinks(MenuModel menu,
            SearchResultProperty extLinkProperty) {
        if (extLinkProperty != null) {
            List<Map<String, SearchResultProperty>> list = extLinkProperty
                    .getGroups();
            if (list != null) {
                for (Map<String, SearchResultProperty> map : list) {
                    if (map != null) {
                        String url = "";
                        String menuLabel = "";
                        String menuIcon = "fa fa-fw fa-save";
                        String label = "";

                        if (map.get("menuIcon") != null) {
                            menuIcon = map.get("menuIcon").getValue();
                        }
                        if (map.get("menuLabel") != null) {
                            menuLabel = map.get("menuLabel").getValue();
                        }
                        if (map.get("link") != null) {
                            url = map.get("link").getValue();
                        }
                        if (map.get("label") != null) {
                            label = map.get("label").getValue();
                        }

                        if (StringUtils.isNotEmpty(url)) {
                            String title = "";
                            if (map.get("title") != null) {
                                title = map.get("title").getValue();
                            }

                            if (StringUtils.isNotEmpty(title)) {
                                menuLabel = title;
                            }

                            if (StringUtils.isNotEmpty(label)) {
                                menuLabel += " (" + label + ")";
                            }

                            menu.addElement(buildMenuItem(menuLabel, url, title, menuIcon));
                        }
                    }
                }
            }
        }
    }

    private DefaultMenuItem buildMenuItem(String label, String url, String title,
            String icon) {
        // LOG.debug("label = " + label + ", url = " + url);
        DefaultMenuItem menuItem = new DefaultMenuItem(label);
        menuItem.setIcon(icon);
        menuItem.setOnclick("window.open('" + url + "' ,'_blank')");
        menuItem.setTitle(title);
        return menuItem;
    }

    private void checkDate(OpenSearchParameter pStartDate,
            OpenSearchParameter pEndDate, boolean series) {
        String currentDate = Utility.dateFormat.format(new Date());

        /**
         * validate the max/min date
         */
        try {
            Date today = Utility.dateFormat.parse(currentDate);
            if (pEndDate != null
                    && StringUtils.isNotEmpty(pEndDate.getMaxDate())) {
                Date maxDate = Utility.dateFormat.parse(pEndDate.getMaxDate());
                if (maxDate.after(today)) {
                    pEndDate.setMaxDate(currentDate);
                }
            }

            if (pStartDate != null
                    && StringUtils.isNotEmpty(pStartDate.getMinDate())) {
                Date minDate = Utility.dateFormat.parse(pStartDate.getMinDate());
                if (minDate.after(today)) {
                    pStartDate.setMinDate(currentDate);
                }
            }
        } catch (ParseException e) {
            LOG.debug(e);
        }

        if (pStartDate != null && pEndDate != null) {
            LOG.debug("Start & end date are not null");

            if (StringUtils.isNotEmpty(pEndDate.getMaxDate())) {
                LOG.debug("End max date: " + pEndDate.getMaxDate());
                pStartDate.setMaxDate(pEndDate.getMaxDate());
            } else {
                LOG.debug("No end max date.");
                pEndDate.setMaxDate(currentDate);
                pStartDate.setMaxDate(currentDate);
            }

            if (StringUtils.isNotEmpty(pStartDate.getMinDate())) {
                LOG.debug("Start min date: " + pStartDate.getMinDate());
                pEndDate.setMinDate(pStartDate.getMinDate());
            }

            if (series) {
                /*
                 remove the default value for start/end dates of series
                 */
                pEndDate.setFormValue(null);
                pStartDate.setFormValue(null);
            }
        } else {
            LOG.debug("Either start or end date is null");

            if (pStartDate != null) {
                LOG.debug("Start date is not null");
                pStartDate.setMaxDate(currentDate);
            }

            if (pEndDate != null) {
                LOG.debug("End date is not null");
                pEndDate.setMinDate(currentDate);
            }
        }

        if (pStartDate != null) {
            LOG.debug("pStartDate.getMinDate(): " + pStartDate.getMinDate());
            LOG.debug("pStartDate.getMaxDate(): " + pStartDate.getMaxDate());
            LOG.debug("pStartDate.getFormValue(): " + pStartDate.getFormValue());
        }

        if (pEndDate != null) {
            LOG.debug("pEndDate.getMinDate(): " + pEndDate.getMinDate());
            LOG.debug("pEndDate.getMaxDate(): " + pEndDate.getMaxDate());
            LOG.debug("pEndDate.getFormValue(): " + pEndDate.getFormValue());
        }
    }

    public String seriesItemClass(String uuid) {
        //log.debug("get css class of series item " + uuid);

        if (this.searchData.getSelectedSeriesItem() != null && uuid.equals(
                this.searchData.getSelectedSeriesItem().getUuid())) {
            //log.debug("selected");
            return "selected";
        }

        //log.debug("normal");
        return "normal";
    }

    public String datasetItemClass(String uuid) {
        if (this.searchData.getSelectedDatasetItem() != null && uuid.equals(
                this.searchData.getSelectedDatasetItem().getUuid())) {
            return "selected";
        }
        return "normal";
    }

    public String relatedProductClass(String uuid) {
        if (this.searchData.getSelectedRelatedProduct() != null && uuid.equals(
                this.searchData.getSelectedRelatedProduct().getUuid())) {
            return "selected";
        }
        return "normal";
    }

    public void keepSessionAlive() {
        if (LOG.isDebugEnabled()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LOG.debug("Poll to keep session alive at " + formatter.format(new Date(
                    System.currentTimeMillis())));
        }

        HttpServletRequest req = Utility.getOriginalRequest();
        req.getSession();
    }

    private void validateStarEndDate(Map<String, String> params)
            throws SearchException {
        String timeStart = params.get(Constants.TIME_START);
        String timeEnd = params.get(Constants.TIME_END);
        if (StringUtils.isNotEmpty(timeStart)
                && StringUtils.isNotEmpty(timeEnd)) {
            SimpleDateFormat formatter = new SimpleDateFormat(
                    Constants.DATEFORMAT);
            try {
                Date startDate = formatter.parse(timeStart);
                Date endDate = formatter.parse(timeEnd);
                if (startDate.after(endDate)) {
                    throw new SearchException("Validation Error",
                            "Start Date should be less than End Date.");
                }
            } catch (ParseException e) {
                throw new SearchException("Validation Error",
                        "The date is not valid. It should follow the format: " + Constants.DATEFORMAT);
            }
        }
    }

    private void toFirstEntryView() {
        LOG.debug("toFirstEntryView()");
        if (this.getLayoutBean().isEntryView()) {
            LOG.debug("isEntryView = true");
            SearchResultItem first = getFirstItem(
                    this.searchData.getDatasetPaginator());
            if (first != null) {
                this.onSelectDatasetItem(first);
            } else {
                LOG.debug("View entry in case of no dataset search result.");
                this.getLayoutBean().showEntryView();
            }
        } else {
            LOG.debug("clear selected dataset because of a new search");
            // clear selected collection/dataset because of a new search
            this.searchData.setSelectedDatasetItem(null);
        }
    }

    /**
     * Get the current timezone which is used for the calendar in the search
     * input
     *
     * @return The TimeZone
     */
    public TimeZone getTimeZone() {
        FacesContext context = FacesContext.getCurrentInstance();
        Locale locale = context.getExternalContext().getRequestLocale();
        Calendar calendar = Calendar.getInstance(locale);
        TimeZone clientTimeZone = calendar.getTimeZone();
        return clientTimeZone;
    }

    public List<GeonamesOption> geonameSearch(String query) {
        LOG.debug("geonameSearch( query = " + query + ")");

        List<GeonamesOption> filteredValues = new ArrayList<>();

        try {
            WebService.setUserName(
                    this.configBean.getConfiguration().getGeonamesAccount());
            ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
            //searchCriteria.setNameStartsWith(query);
            searchCriteria.setQ(query);
            searchCriteria.setStyle(org.geonames.Style.FULL);
            List<String> checklist = new ArrayList<>();
            ToponymSearchResult searchResult = WebService.search(searchCriteria);
            for (Toponym toponym : searchResult.getToponyms()) {
                String value = toponym.getLongitude() + ";" + toponym.getLatitude();
                if (checklist.contains(value)) {
                    LOG.debug("Place name is ready in the list: " + value);
                } else {
                    GeonamesOption option = new GeonamesOption();
                    option.setName(toponym.getName());
                    option.setLatitude(toponym.getLatitude());
                    option.setLongitude(toponym.getLongitude());
                    option.setCountryCode(toponym.getCountryCode());
                    option.setCountryName(toponym.getCountryName());
                    setBbox(option, toponym.getBoundingBox());
                    filteredValues.add(option);
                    checklist.add(value);
                }
            }
        } catch (Exception e) {
            LOG.debug(e);
        }

        return filteredValues;

    }

    private void setBbox(GeonamesOption option, org.geonames.BoundingBox bbox) {
        if (bbox != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(bbox.getWest()).append(",").append(bbox.getSouth()).append(",").append(
                    bbox.getEast()).append(",").append(bbox.getNorth());
            option.setBbox(sb.toString());
            LOG.debug("Bbox of " + option.getCountryName() + ": " + option.getBbox());
        } else {
            LOG.debug("Bbox of " + option.getCountryName() + " is null ");
        }

    }

    public void onGeonameSelect(SelectEvent event) {
        LOG.debug("onGeonameSelect: " + event.getObject());
        Object obj = event.getObject();
        if (obj instanceof GeonamesOption) {
            LOG.debug("This is a GeonamesOption");
            GeonamesOption option = (GeonamesOption) obj;
            LOG.debug("geo option: " + option.getBbox());
            if (option.getBbox() != null && !option.getBbox().equalsIgnoreCase(
                    Constants.NO_BBOX)) {
                this.searchData.setBbox(option.getBbox());
            } else {
                this.searchData.setBbox(Utility.createBBoxFromPointAndDistance(
                        option.getLongitude(), option.getLatitude(),
                        this.configBean.getConfiguration().getGeonamesRadius()));
            }

        }
    }

    public List<FluidGridItem> getEmptyFluidItems() {
        return new ArrayList<>();
    }

    public List<String> iteratePropertyValues(SearchResultItem selectedItem,
            String propName) {
        LOG.debug("iteratePropertyValues(propName = " + propName + ")");
        List<String> values = new ArrayList<>();

        if (selectedItem == null || selectedItem.getProperties() == null) {
            return values;
        }

        int count = 1;
        while (true) {
            String name = propName + count;
            SearchResultProperty srProp = selectedItem.getProperties().get(name);
            if (srProp != null) {
                String value = srProp.getValue();
                LOG.debug(name + " = " + value);
                if (StringUtils.isNotEmpty(value)) {
                    values.add(value);
                }
                count++;
            } else {
                LOG.debug("stop iterating");
                break;
            }
        }
        return values;
    }

    public boolean hasProperties(SearchResultItem selectedItem, String properties) {
        //log.debug("hasProperties(properties = " + properties + ")");
        if (selectedItem == null || selectedItem.getProperties() == null || StringUtils.isEmpty(
                properties)) {
            return false;
        }
        String[] listProps = properties.split(",");
        boolean has = false;
        for (String prop : listProps) {
            //log.debug("property = " + prop);
            SearchResultProperty srProp = selectedItem.getProperties().get(prop);
            if (srProp != null) {
                String value = srProp.getValue();
                if (StringUtils.isNotEmpty(value)) {
                    has = true;
                    break;
                }
            }
        }
        return has;
    }

    public String jsonToHtml(Object jsonObject, int startIndent) {
        GeoJSONSearchResultParser geoJsonParser = new GeoJSONSearchResultParser(null, configBean.getConfiguration());
        return geoJsonParser.jsonToHtml(jsonObject, startIndent, true);
    }

    public String jsonMapToHtml(Map<String, Object> map, int startIndent) {
        GeoJSONSearchResultParser geoJsonParser = new GeoJSONSearchResultParser(null, configBean.getConfiguration());
        if (map != null && !map.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                sb.append(geoJsonParser.jsonToHtml(key, value, startIndent));
            }
            return sb.toString();
        }
        return "";
    }

    public String qualityIndicatorMeasurementLabel(String qualityIndicatorMeasurement) {
        if (qualityIndicatorMeasurement.contains("#")) {
            return Utility.toLabel(StringUtils.substringAfterLast(qualityIndicatorMeasurement, "#"));
        }
        return Utility.toLabel(qualityIndicatorMeasurement);
    }

    public long getPollIntervalInMilliseconds() {
        int interval = 30; // 30'
        LOG.debug("Configured interval : " + interval);
        // the interval has been deducted 20 seconds to make sure the poll will be executed on time before the session expires
        long intervalInMilliseconds = ((interval * 60) - 20) * 1000;
        LOG.debug("Interval in milliseconds: " + intervalInMilliseconds);

        return intervalInMilliseconds;
    }

    /**
     * Getter method
     *
     * @return
     */
    public ConfigurationBean getConfigBean() {
        return configBean;
    }

    /**
     * Setter method
     *
     * @param configBean
     */
    public void setConfigBean(ConfigurationBean configBean) {
        this.configBean = configBean;
    }

    /**
     * Getter method
     *
     * @return
     */
    public SearchData getSearchData() {
        return searchData;
    }

    /**
     * Setter method
     *
     * @param searchData
     */
    public void setSearchData(SearchData searchData) {
        this.searchData = searchData;
    }

    /**
     * Getter method
     *
     * @return LayoutBean
     */
    public LayoutBean getLayoutBean() {
        return layoutBean;
    }

    /**
     * Setter method
     *
     * @param layoutBean
     */
    public void setLayoutBean(LayoutBean layoutBean) {
        this.layoutBean = layoutBean;
    }

    public String getSelectedProductUuid() {
        return selectedProductUuid;
    }

    public String getSelectedSeries() {
        return selectedSeries;
    }

    public void setSelectedSeries(String selectedSeries) {
        this.selectedSeries = selectedSeries;
    }

    public SearchResultItem getPresentItem() {
        if (layoutBean.isDatasetDetails() && this.getSearchData().getSelectedDatasetItem() != null) {
            return this.getSearchData().getSelectedDatasetItem();
        }
        if ((layoutBean.isDatasetInit() || layoutBean.isSeriesDetails())
                && this.getSearchData().getSelectedSeriesItem() != null) {
            return this.getSearchData().getSelectedSeriesItem();
        }
        return null;
    }

    public Converter getCalenderConverter() {
        return new Converter() {
            @Override
            public Object getAsObject(
                    FacesContext context,
                    UIComponent component,
                    String value) {
                return value;
            }

            @Override
            public String getAsString(
                    FacesContext context,
                    UIComponent component,
                    Object value) {
                return value.toString();
            }

        };
    }

}
