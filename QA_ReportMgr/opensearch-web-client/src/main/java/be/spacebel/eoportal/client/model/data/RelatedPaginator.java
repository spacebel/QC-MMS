/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author mng
 */
public class RelatedPaginator implements Serializable {

    private final Logger log = Logger.getLogger(getClass());

    private String first;
    private String previous;
    private String next;
    private String last;
    private int itemCount;
    private int page;
    private int pageSize;
    private int pageCount;
    private int backupPage;

    //private int indexDetails;

    private DataModel data;
    private String relatedByProductId;
    private String jumpUrl;
    private boolean canJump;

    public RelatedPaginator() {
        init();
    }

    public RelatedPaginator(String first, String previous, String next, String last, int itemCount,
            List<SearchResultItem> items, int newPageSize, String newRelatedByProductId) {

        this.first = first;
        this.previous = previous;
        this.next = next;
        this.last = last;
        this.itemCount = itemCount;
        this.page = 1;
        this.backupPage = this.page;
        this.pageSize = newPageSize;

        this.data = new DataModel(items);

        if (itemCount > -1) {
            this.pageCount = (int) Math.ceil(itemCount * 1d / pageSize);
        } else {
            this.pageCount = 0;
        }

        this.relatedByProductId = newRelatedByProductId;
        //this.indexDetails = 0;

        this.jumpUrl = getPagingUrl();
        this.canJump = (jumpUrl != null && this.itemCount > 0 && jumpUrl.contains("startRecord="));
        // System.out.println("pageSize = " + pageSize + "; pageCount = " + pageCount + "; relatedLinks = " + relatedLinks);
    }

    public void pageNavigate(String where, ProcessManager processManager,
            Configuration config) {
        log.debug("Navigate to the " + where + " page");

        if ("first".equals(where)) {
            if (StringUtils.isNotEmpty(this.first)) {
                navigate(this.first, processManager, config);
                this.page = 1;
            } else {
                if (canJump) {
                    this.page = 1;
                    navigate(processManager, config);
                } else {
                    FacesMessageUtil.addErrorMessage("Could not find the URL of first page in the search response");
                }
            }
        }

        if ("prev".equals(where)) {
            if (StringUtils.isNotEmpty(this.previous)) {
                navigate(this.previous, processManager, config);
                this.page -= 1;
            } else {
                if (canJump) {
                    this.page = this.page - 1;
                    navigate(processManager, config);
                } else {
                    FacesMessageUtil.addErrorMessage("Could not find the URL of previous page in the search response");
                }
            }
        }

        if ("next".equals(where)) {
            if (StringUtils.isNotEmpty(this.next)) {
                navigate(this.next, processManager, config);
                this.page += 1;
            } else {
                if (canJump) {
                    this.page = this.page + 1;
                    navigate(processManager, config);
                } else {
                    FacesMessageUtil.addErrorMessage("Could not find the URL of next page in the search response");
                }
            }
        }

        if ("last".equals(where)) {
            if (StringUtils.isNotEmpty(this.last)) {
                navigate(this.last, processManager, config);

                if (this.itemCount > -1) {
                    this.page = this.pageCount;
                } else {
                    this.page += 1;
                }
            } else {
                if (canJump) {
                    this.page = this.pageCount;
                    navigate(processManager, config);
                } else {
                    FacesMessageUtil.addErrorMessage("Could not find the URL of previous page in the search response");
                }
            }
        }

        this.backupPage = this.page;

    }

//    public SearchResultItem detailsNavigate(String where,
//            ProcessManager processManager,
//            Configuration config) {
//        switch (where) {
//            case "prev":
//                if (indexDetails == 0) {
//                    pageNavigate(where, processManager, config);
//                    indexDetails = data.getItemCount() - 1;
//                } else {
//                    indexDetails = indexDetails - 1;
//                }
//                break;
//            case "next":
//                if (indexDetails == data.getItemCount() - 1) {
//                    pageNavigate(where, processManager, config);
//                    indexDetails = 0;
//                } else {
//                    indexDetails = indexDetails + 1;
//                }
//                break;
//        }
//        return data.getItems().get(indexDetails);
//    }

    public void jumpToPage(int targetPage, ProcessManager processManager, Configuration config) {
        this.page = targetPage;

        navigate(processManager, config);
    }

    private void navigate(ProcessManager processManager, Configuration config) {
        log.debug("Navigate to the current page");
        int startIndex = ((this.page - 1) * this.pageSize) + 1;

        String firstPart = StringUtils.substringBefore(jumpUrl, "startRecord=");
        String secondPart = StringUtils.substringAfter(jumpUrl, "startRecord=");
        String thirdPart = StringUtils.substringAfter(secondPart, "&");

        String navUrl = firstPart + "startRecord=" + startIndex;
        if (StringUtils.isNotEmpty(thirdPart)) {
            navUrl += "&" + thirdPart;
        }
        log.debug("Nav Url " + navUrl);

        navigate(navUrl, processManager, config);
    }

    private void navigate(String navUrl, ProcessManager processManager,
            Configuration config) {
        // workaround
        if (!navUrl.contains("?")) {
            navUrl = navUrl.replaceAll("&startRecord=", "?startRecord=");
        }
        try {
            SearchResultSet rs = processManager
                    .retrieveRelatedProducts(navUrl, config, config.getDataset().getListViewAttributes());
            if (rs != null) {
                SearchResultError error = rs.getSearchResultError();
                if (error != null) {
                    log.debug("ERROR: " + error.getErrorCode() + " ; " + error.getErrorMessage());
                    throw new IOException(error.getErrorMessage() + " (" + error.getErrorCode() + ")");
                } else {
                    if (rs.getItems().isEmpty()) {
                        log.debug("NO RESULTS");
                        this.data = new DataModel();
                        this.first = null;
                        this.previous = null;
                        this.next = null;
                        this.last = null;
                        FacesMessageUtil.addInfoMessage("No products");
                    } else {
                        this.data = new DataModel(rs.getItems());
                        this.data.setGmlFeatures(rs.getGmlFeatures());

                        this.first = rs.getAttributes().get(Constants.OS_FIRSTPAGE);
                        log.debug("first = " + first);
                        this.previous = rs.getAttributes().get(Constants.OS_PREVIOUSPAGE);
                        log.debug("previous = " + previous);
                        this.next = rs.getAttributes().get(Constants.OS_NEXTPAGE);
                        log.debug("next = " + next);
                        this.last = rs.getAttributes().get(Constants.OS_LASTPAGE);
                        log.debug("last = " + last);

                        int total = -1;
                        try {
                            String strTotal = rs.getAttributes().get(Constants.OS_TOTAL_RESULTS);
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

                        this.jumpUrl = getPagingUrl();
                        this.canJump = (jumpUrl != null && this.itemCount > 0 && jumpUrl.contains("startRecord="));
                    }
                }
            } else {
                throw new IOException("Empty response");
            }

        } catch (IOException e) {
            init();
            FacesMessageUtil.addErrorMessageWithDetails("unexpectederror", e.getMessage());
        }

    }

    public String getRecordInfo() {
        // Results 1-10 of 23 or Results 1-10
        if (this.data != null
                && this.data.getItems() != null
                && !this.data.getItems().isEmpty()) {

            StringBuilder strBuf = new StringBuilder();
            int beginItemIndex = Math.max((this.page - 1) * pageSize, 0) + 1;
            int endItemIndex = (beginItemIndex - 1) + this.data.getItems().size();

            //strBuf.append("Results ");            
            strBuf.append("Related products ");

            strBuf.append(beginItemIndex);
            strBuf.append("-");
            strBuf.append(endItemIndex);
            strBuf.append(" of ").append(this.itemCount);

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

            strBuf.append("Related product ");
            strBuf.append(beginItemIndex + index);
            strBuf.append(" of ").append(this.itemCount);

            return strBuf.toString();
        } else {
            return null;
        }
    }       

    private String getPagingUrl() {
        if (StringUtils.isNotEmpty(next)) {
            return next;
        }
        if (StringUtils.isNotEmpty(previous)) {
            return previous;
        }
        if (StringUtils.isNotEmpty(last)) {
            return last;
        }
        if (StringUtils.isNotEmpty(first)) {
            return first;
        }
        return null;
    }

    public String getFirst() {
        return first;
    }

    public String getPrevious() {
        return previous;
    }

    public String getNext() {
        return next;
    }

    public String getLast() {
        return last;
    }

    public boolean isNextDetails() {
        return true;
        //return ((indexDetails < (data.getItemCount() - 1)) || isNext());
    }

    public boolean isPreviousDetails() {
        //return ((indexDetails > 0) || isPrevious());
        return true;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

//    public int getIndexDetails() {
//        return indexDetails;
//    }
//
//    public void setIndexDetails(int indexDetails) {
//        this.indexDetails = indexDetails;
//    }

    public DataModel getData() {
        return data;
    }

    public void setData(DataModel data) {
        this.data = data;
    }

    public String getRelatedByProductId() {
        return relatedByProductId;
    }

    public void setRelatedByProductId(String relatedByProductId) {
        this.relatedByProductId = relatedByProductId;
    }

    public void toBackupPage() {
        this.page = this.backupPage;
    }

    private void init() {
        this.page = 0;
        this.pageCount = 0;
        this.itemCount = 0;
        this.first = null;
        this.previous = null;
        this.next = null;
        this.last = null;

        this.relatedByProductId = null;
        this.data = null;
        this.data = null;
        this.page = 0;
        this.pageCount = 0;
    }

}
