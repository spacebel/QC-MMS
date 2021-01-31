package be.spacebel.eoportal.client.model.data;

import be.spacebel.eoportal.client.business.ProcessManager;
import be.spacebel.eoportal.client.business.data.Constants;
import be.spacebel.eoportal.client.business.data.SearchResultError;
import be.spacebel.eoportal.client.business.data.SearchResultItem;
import be.spacebel.eoportal.client.business.data.SearchResultSet;
import be.spacebel.eoportal.client.util.FacesMessageUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * This class implements a paginator of search results
 *
 * @author mng
 */
public class Paginator implements Serializable {

    private static final long serialVersionUID = 1L;
    private String first;
    private String previous;
    private String next;
    private String last;
    private int page;
    private int backupPage;
    private int pageCount;
    private int pageSize;
    private int itemCount;
    private DataModel data;
    List<String> showFields;
    private String gmlFeatures;
    private ProcessManager processManager;
    private Configuration config;

    /*
     the variables are used only for Product search where total of results is returned
     */
    private boolean showPageInfo;
    private Map<String, String> inputParams;
    private String templateUrl;
    private int indexOffset;

    private static final Logger log = Logger.getLogger(Paginator.class);
    private static final String SERIES_SEARCH = "Series";
    private static final String START_INDEX = "os_startIndex";

    public Paginator(boolean reset) {
        //this.processManager = new ProcessManager();
        if (reset) {
            reset();
        }
    }

    public Paginator(String first, String previous, String next, String last, int itemCount,
            List<SearchResultItem> items, int pageSize, List<String> showFields, String gmlFeatures) {
        this.first = first;
        this.previous = previous;
        this.next = next;
        this.last = last;
        this.data = new DataModel(items);
        this.showFields = showFields;
        this.gmlFeatures = gmlFeatures;
        this.page = 1;
        this.backupPage = this.page;
        this.pageSize = pageSize;
        this.itemCount = itemCount;
        //this.processManager = new ProcessManager();

        this.showPageInfo = (itemCount > 0);

        if (itemCount > -1) {
            this.pageCount = (int) Math.ceil(itemCount * 1d / pageSize);
        } else {
            this.pageCount = 0;
        }
    }

    public void pageNavigate(String where) {
        log.debug("pageNavigate : " + where);
        try {
            if ("first".equals(where)) {
                if (StringUtils.isNotEmpty(this.first)) {
                    navigate(this.first);
                    this.page = 1;
                } else {
                    if (this.showPageInfo) {
                        this.page = 1;
                        navigate();
                    } else {
                        FacesMessageUtil.addErrorMessage("Could not find the URL of first page in the search response");
                    }
                }

//                if (this.showPageInfo) {
//                    this.page = 1;
//                    navigate();
//                } else {
//                    if (StringUtils.isNotEmpty(this.first)) {
//                        navigate(this.first);
//                        this.page = 1;
//                    } else {
//                        FacesMessageUtil.addErrorMessage("Could not find the URL of first page in the search response");
//                    }
//                }
            }

            if ("prev".equals(where)) {
                if (StringUtils.isNotEmpty(this.previous)) {
                    navigate(this.previous);
                    this.page -= 1;
                } else {
                    if (this.showPageInfo) {
                        this.page = this.page - 1;
                        navigate();
                    } else {
                        FacesMessageUtil.addErrorMessage("Could not find the URL of previous page in the search response");
                    }
                }

//                if (this.showPageInfo) {
//                    this.page = this.page - 1;
//                    navigate();
//                } else {
//                    if (StringUtils.isNotEmpty(this.previous)) {
//                        navigate(this.previous);
//                        this.page -= 1;
//                    } else {
//                        FacesMessageUtil.addErrorMessage("Could not find the URL of previous page in the search response");
//                    }
//                }
            }

            if ("next".equals(where)) {
                if (StringUtils.isNotEmpty(this.next)) {
                    navigate(this.next);
                    this.page += 1;
                } else {
                    if (this.showPageInfo) {
                        this.page = this.page + 1;
                        navigate();
                    } else {
                        FacesMessageUtil.addErrorMessage("Could not find the URL of next page in the search response");
                    }
                }

//                if (this.showPageInfo) {
//                    this.page = this.page + 1;
//                    navigate();
//                } else {
//                    if (StringUtils.isNotEmpty(this.next)) {
//                        navigate(this.next);
//                        this.page += 1;
//                    } else {
//                        FacesMessageUtil.addErrorMessage("Could not find the URL of next page in the search response");
//                    }
//                }
            }

            if ("last".equals(where)) {
                if (StringUtils.isNotEmpty(this.last)) {
                    navigate(this.last);
                    if (this.itemCount > -1) {
                        this.page = this.pageCount;
                    } else {
                        this.page += 1;
                    }
                } else {
                    if (this.showPageInfo) {
                        this.page = this.pageCount;
                        navigate();
                    } else {
                        FacesMessageUtil.addErrorMessage("Could not find the URL of previous page in the search response");
                    }
                }

//                if (this.showPageInfo) {
//                    this.page = this.pageCount;
//                    navigate();
//                } else {
//                    if (StringUtils.isNotEmpty(this.last)) {
//                        navigate(this.last);
//                        if (this.itemCount > -1) {
//                            this.page = this.pageCount;
//                        } else {
//                            this.page += 1;
//                        }
//                    } else {
//                        FacesMessageUtil.addErrorMessage("Could not find the URL of previous page in the search response");
//                    }
//                }
            }

            this.backupPage = this.page;
        } catch (IOException e) {
            log.error(e);
            FacesMessageUtil.addErrorMessage(e);
        }
    }

    public void jumpToPage(int targetPage) {
        this.page = targetPage;
        log.debug("Jump to page: " + this.page);
        try {
            navigate();
            this.backupPage = this.page;
        } catch (IOException e) {
            log.error(e);
            FacesMessageUtil.addErrorMessage(e);
        }
    }

    public String getPageInfo() {
        //log.debug("getPageInfo...........");

        // Page 1 of 3 or Page 1
        if (data != null
                && this.data.getItems() != null
                && !this.data.getItems().isEmpty()) {
            StringBuilder strBuf = new StringBuilder();
            strBuf.append("Page ").append(this.page);
            if (this.itemCount > -1) {
                strBuf.append(" of ").append(this.pageCount);
            }
            return strBuf.toString();
        } else {
            return null;
        }

    }

    public String getRecordInfo() {
        //log.debug("getPageInfo...........");

        // Results 1-10 of 23 or Results 1-10
        if (data != null
                && this.data.getItems() != null
                && !this.data.getItems().isEmpty()) {
            StringBuilder strBuf = new StringBuilder();
            int beginItemIndex = Math.max((this.page - 1) * pageSize, 0) + 1;
            int endItemIndex = (beginItemIndex - 1) + this.data.getItems().size();
            //strBuf.append("Results ");
            strBuf.append("Products ");
            strBuf.append(beginItemIndex);
            strBuf.append("-");
            strBuf.append(endItemIndex);
            if (this.itemCount > -1) {
                strBuf.append(" of ").append(this.itemCount);
            }
            return strBuf.toString();
        } else {
            return null;
        }
    }

    public String getRecordDetailsInfo(int index) {
        //log.debug("getRecordDetailsInfo..........." + index);
        if (data != null
                && this.data.getItems() != null
                && !this.data.getItems().isEmpty()) {
            StringBuilder strBuf = new StringBuilder();
            int beginItemIndex = Math.max((this.page - 1) * pageSize, 0) + 1;
            //strBuf.append("Result ");
            strBuf.append("Product ");
            strBuf.append(beginItemIndex + index);
            if (this.itemCount > -1) {
                strBuf.append(" of ").append(this.itemCount);
                //strBuf.append("/").append(this.itemCount);
            }
            return strBuf.toString();
        } else {
            return null;
        }
    }

    private void navigate(String url) throws IOException {
        log.debug("Navigating to:" + url);
        SearchResultSet searchRS = getProcessManager().navigatePage(url, this.showFields, getConfig());
        handleSearchResults(searchRS);
    }

    private void navigate() throws IOException {
        log.debug("Navigate to page " + this.page);

        int startIndex = ((this.page - 1) * this.pageSize) + 1;
        log.debug("Navigate with start index: " + startIndex);
        this.inputParams.put(START_INDEX, Integer.toString(startIndex));

        SearchResultSet searchRS = getProcessManager()
                .navigatePage(inputParams,
                        templateUrl, indexOffset, this.showFields, getConfig());
        handleSearchResults(searchRS);
    }

    private void handleSearchResults(SearchResultSet searchRS) throws IOException {
        if (searchRS != null) {
            SearchResultError error = searchRS.getSearchResultError();
            if (error != null) {
                log.debug("ERROR: " + error.getErrorCode() + " ; " + error.getErrorMessage());
                reset();
                throw new IOException(error.getErrorMessage() + " (" + error.getErrorCode() + ")");
            } else {
                if (searchRS.getItems().isEmpty()) {
                    log.debug("NO RESULTS");
                    this.data = new DataModel();
                    this.first = null;
                    this.previous = null;
                    this.next = null;
                    this.last = null;
                    FacesMessageUtil.addInfoMessage("search.noresult");
                } else {
                    log.debug("HAS RESULTS");
                    this.data = new DataModel(searchRS.getItems());
                    this.first = searchRS.getAttributes().get(Constants.OS_FIRSTPAGE);
                    this.previous = searchRS.getAttributes().get(Constants.OS_PREVIOUSPAGE);
                    this.next = searchRS.getAttributes().get(Constants.OS_NEXTPAGE);
                    this.last = searchRS.getAttributes().get(Constants.OS_LASTPAGE);

                    if (StringUtils.isNotEmpty(searchRS.getGmlFeatures())) {
                        this.gmlFeatures = searchRS.getGmlFeatures();
                    }

                    int total = -1;
                    try {
                        String strTotal = searchRS.getAttributes().get(Constants.OS_TOTAL_RESULTS);
                        if (StringUtils.isNotEmpty(strTotal)) {
                            total = Integer.parseInt(strTotal);
                        }
                    } catch (NumberFormatException e) {
                        log.debug(e.getMessage());
                    }
                    this.itemCount = total;
                    if (itemCount > 1) {
                        this.pageCount = (int) Math.ceil(itemCount * 1d / pageSize);
                    } else {
                        this.pageCount = 0;
                    }

                    this.showPageInfo = (itemCount > 0);
                }
            }
        } else {
            log.debug("ERROR: SearchResultSet is null.");
            reset();
            throw new IOException("Search gave an empty response.");
        }
    }

    public void toBackupPage() {
        this.page = this.backupPage;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public DataModel getData() {
        return data;
    }

    public void setData(DataModel data) {
        this.data = data;
    }

    public String getGmlFeatures() {
        return gmlFeatures;
    }

    public void setGmlFeatures(String gmlFeatures) {
        this.gmlFeatures = gmlFeatures;
    }

    public ProcessManager getProcessManager() {
        if (this.processManager == null) {
            log.debug("processManager is null");
            try {
                this.processManager = new ProcessManager(this.config);
            } catch (IOException e) {
                log.debug(e);
            }
        }
        return processManager;
    }

    public void setProcessManager(ProcessManager processManager) {
        this.processManager = processManager;
    }

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    public boolean isShowPageInfo() {
        return showPageInfo;
    }

    public void setShowPageInfo(boolean showPageInfo) {
        this.showPageInfo = showPageInfo;
    }

    public Map<String, String> getInputParams() {
        return inputParams;
    }

    public void setInputParams(Map<String, String> inputParams) {
        this.inputParams = inputParams;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    public int getIndexOffset() {
        return indexOffset;
    }

    public void setIndexOffset(int indexOffset) {
        this.indexOffset = indexOffset;
    }

    private void reset() {
        this.data = null;
        this.page = 0;
        this.pageCount = 0;
        this.itemCount = 0;
        this.first = null;
        this.previous = null;
        this.next = null;
        this.last = null;
        this.gmlFeatures = null;
        this.inputParams = null;
        this.templateUrl = null;
    }
}
