package be.spacebel.eoportal.client.business;

import be.spacebel.eoportal.client.business.data.Constants;
import be.spacebel.eoportal.client.business.data.OpenSearchParameter;
import be.spacebel.eoportal.client.business.data.OpenSearchUrl;
import be.spacebel.eoportal.client.business.data.SearchResultSet;
import be.spacebel.eoportal.client.model.data.Configuration;
import be.spacebel.eoportal.client.parser.GeoJSONSearchResultParser;
import be.spacebel.eoportal.client.parser.XMLDocumentNamespaceResolver;
import be.spacebel.eoportal.client.parser.XMLParser;
import be.spacebel.eoportal.client.util.HTTPInvoker;
import be.spacebel.eoportal.client.util.Utility;
import be.spacebel.eoportal.client.util.XMLUtility;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class acts as the interface to communicate with the OSGW. It created the
 * opensearch request from user inputs, send the request to the OSGW and handle
 * the response before sending it to the presentation layer.
 *
 * @author mng
 */
public class ProcessManager implements Serializable {

    private static final String OS_URL_TAG = "Url";

    private static final String PARAM_TOKEN_OPEN = "{";
    private static final String PARAM_TOKEN_CLOSE = "}";
    private static final String OS_SEARCH_TYPE = "osSearchType";
    private static final String OS_TEMPLATE_URL = "osTemplateUrl";
    private static final String OS_INDEX_OFFSET = "osIndexOffset";

    private static final Logger LOG = Logger.getLogger(ProcessManager.class);
    /*
     List of prefix/namespace
     */
    private static Map<String, String> resourceNamespaces;
    /*
     List of token#namespace (e.g: searchTerm#http://a9.com/-/spec/opensearch/1.1/) / OpenSearchParameter
     */
    private static Map<String, OpenSearchParameter> resourceParameters;

    private final String resourceDir;
    private final XMLParser xmlParser;
    private Configuration config;

    public ProcessManager(Configuration config) throws IOException {
        LOG.debug("ProcessManager init.......");
        resourceDir = Utility.getAbsolutePath() + "data";
        xmlParser = new XMLParser();
        this.config = config;

        /*
         load resource parameters
         */
        Document resourceDoc = xmlParser.file2Document(
                resourceDir + "/" + Constants.OS_PARAMETERS_XML_FILE);
        loadResourceParameters(resourceDoc);

        LOG.debug("Done.");
    }    

    /**
     * Obtains OSDD from the given OSDD URL and then extracts the corresponding
     * dataset OpenSearch URL
     *
     * @param osddUrl
     * @param mimeType
     * @return
     * @throws IOException
     */
    public OpenSearchUrl getDatasetOpenSearchUrl(String osddUrl, String mimeType) throws IOException {
        LOG.debug("getDatasetOpenSearchUrl for mimeType:" + mimeType);
        return getOpenSearchUrl(osddUrl, true, mimeType);
    }

    private OpenSearchUrl getOpenSearchUrl(String osddUrl, boolean forProduct,
            String mimeType) throws IOException {
        LOG.debug(
                "getOpenSearchUrl(osddUrl = " + osddUrl + ", forProduct = " + forProduct + ")");
        Map<String, String> details = new HashMap<>();
        String osddContent = HTTPInvoker.invokeGET(osddUrl, details);
        OpenSearchUrl osUrl = null;
        if (details.get(Constants.HTTP_GET_DETAILS_ERROR_CODE) != null) {
            throw new IOException(
                    "The OpenSearch Description Document " + osddUrl + " is inaccessible!");
        } else {
            if (StringUtils.isNotEmpty(osddContent)) {
                Document osddDoc = xmlParser.stream2Document(osddContent);
                NodeList urls = osddDoc.getElementsByTagNameNS(Constants.OS_NAMESPACE,
                        OS_URL_TAG);

                for (int urlIdx = 0; urlIdx < urls.getLength(); urlIdx++) {
                    Node urlNode = urls.item(urlIdx);
                    String type = XMLUtility.getNodeAttValue(urlNode, "type");
                    String configuredType = config.getDataset().getDatasetSearchFormat();
                    if (mimeType != null) {
                        configuredType = mimeType;
                    }
                    LOG.debug("configuredType:" + mimeType);
                    if (StringUtils.equalsIgnoreCase(configuredType, type)) {
                        String rel = XMLUtility.getNodeAttValue(urlNode, "rel");
                        boolean found = false;
                        if (forProduct) {
                            if ("results".equalsIgnoreCase(rel)) {
                                found = true;
                            }
                        } else {
                            if ("collection".equalsIgnoreCase(rel)) {
                                found = true;
                            }
                        }
                        if (found) {
                            Map<String, String> osddNamespaces = XMLUtility.getNamespaces(
                                    osddDoc, true);
                            osUrl = parseUrl(urlNode, osddNamespaces);
                            break;
                        }
                    }
                }
            } else {
                throw new IOException(
                        "The OpenSearch Description Document from the  " + osddUrl + " is empty !");
            }
        }
        if (osUrl == null) {
            if (forProduct) {
                throw new IOException(
                        "OpenSearch Description Document from " + osddUrl + " does not contain template URL for dataset search (i.e. type=application/atom+xml,  rel=results).");
            } else {
                throw new IOException(
                        "OpenSearch Description Document from " + osddUrl + " does not contain template URL for dataset series search (i.e. type=application/atom+xml,  rel=collection).");
            }
        }
        return osUrl;
    }

    /**
     * Replace OpenSearch parameters of the template URL by the corresponding
     * values and then invoke the URL
     *
     * @param parameters
     * @param templateUrl
     * @param indexOffset
     * @param searchType
     * @return
     * @throws IOException
     */
    public Map<String, String> search(Map<String, String> parameters,
            String templateUrl, int indexOffset) throws IOException {
        LOG.debug("Enter search(templateUrl = " + templateUrl + ")");
        Map<String, String> result = null;

//        if (LOG.isDebugEnabled()) {
//            LOG.debug("OS params:");
//            for (Map.Entry<String, String> entry : parameters.entrySet()) {
//                LOG.debug(entry.getKey() + "=" + entry.getValue());
//            }
//        }
        String osUrl = fillParamValuesToOpenSearchUrl(templateUrl, indexOffset,
                parameters);
        LOG.debug("osUrl = " + osUrl);

        if (StringUtils.isNotEmpty(osUrl)) {
            String strResultHML = processOpenSearch(osUrl);
            result = new HashMap<>();
            result.put(Constants.OS_RESPONSE, strResultHML);
            result.put(OS_SEARCH_TYPE, Constants.DATASET_SEARCH);
            if (StringUtils.isNotEmpty(templateUrl)) {
                result.put(OS_TEMPLATE_URL, templateUrl);
            }
            if (indexOffset != -1) {
                result.put(OS_INDEX_OFFSET, "" + indexOffset);
            }
        }
        return result;
    }

    /**
     * Get results from the given page URL and then parse the results into
     * SearchResultSet object; This method is executed to navigate the pages of
     * search results
     *
     * @param pageUrl
     * @param showFields
     * @param config
     * @return
     * @throws IOException
     */
    public SearchResultSet navigatePage(String pageUrl,
            List<String> showFields, Configuration config) throws IOException {
        String result = processOpenSearch(pageUrl);
        if (StringUtils.isNotEmpty(result)) {
            return parseSearchResults(result, showFields, config);
        }

        return null;
    }

    /**
     * Replace OpenSearch parameters of the template URL by the corresponding
     * values; invoke the URL and then parse the results into SearchResultSet
     * object; This method is executed to navigate the pages of search results
     *
     * @param parameters
     * @param templateUrl
     * @param indexOffset
     * @param showFields
     * @param config
     * @return
     * @throws IOException
     */
    public SearchResultSet navigatePage(Map<String, String> parameters,
            String templateUrl, int indexOffset,
            List<String> showFields, Configuration config) throws IOException {
        String osUrl = fillParamValuesToOpenSearchUrl(templateUrl, indexOffset,
                parameters);
        LOG.debug("osUrl = " + osUrl);

        if (StringUtils.isNotEmpty(osUrl)) {
            String result = processOpenSearch(osUrl);
            if (StringUtils.isNotEmpty(result)) {
                return parseSearchResults(result, showFields, config);
            }
        }

        return null;
    }

    /**
     * Parse XML based text search results into SearchResultSet
     *
     * @param source
     * @param showFields
     * @param config
     * @return a SearchResultSet object
     */
    public SearchResultSet parseSearchResults(String source,
            List<String> showFields, Configuration config) {
        GeoJSONSearchResultParser jsonSearchResultParser = new GeoJSONSearchResultParser(
                showFields, config);
        return jsonSearchResultParser.buildResultSetFromJson(source);
    }

    private String processOpenSearch(String osUrl) throws IOException {

        LOG.debug("processOpenSearch.osUrl = " + osUrl);

        Map<String, String> errorDetails = new HashMap<>();
        LOG.debug("Searching URL :" + osUrl);
        String strResponse = HTTPInvoker.invokeGET(osUrl, errorDetails);
        LOG.debug("response -----------");

        //log.debug(strResponse);
        if (errorDetails.get(Constants.HTTP_GET_DETAILS_ERROR_CODE) != null) {
            String errorCode = errorDetails.get(Constants.HTTP_GET_DETAILS_ERROR_CODE);
            String errorMsg = errorDetails.get(Constants.HTTP_GET_DETAILS_ERROR_MSG);

            String err = "";
            if (StringUtils.isNotEmpty(errorMsg)) {
                err = err + errorMsg;
            }
            if (StringUtils.isNotEmpty(errorCode)) {
                err = err + "(" + errorCode + ")";
            }
            throw new IOException(err);
        }
        return strResponse;
    }

    private String fillParamValuesToOpenSearchUrl(String opensearchTemplateURL,
            int indexOffset,
            Map<String, String> params) {
        String strStartIndex = null;
        if (params.containsKey("startIndex") && StringUtils.isNotEmpty(params.get(
                "startIndex"))) {
            try {
                /*
                 * calculate startIndex to correspond to the back-end. Default
                 * start value of startIndex is 1.
                 */
                int startIndex = Integer.parseInt(params.get("startIndex"));
                if (indexOffset < 1) {
                    strStartIndex = Integer.toString(startIndex - 1);
                }
                if (indexOffset > 1) {
                    strStartIndex = Integer.toString(startIndex + (indexOffset - 1));
                }
            } catch (NumberFormatException e) {
            }

        }
        LOG.debug("Opensearch URL template:" + opensearchTemplateURL);
        String[] tokens = StringUtils.substringsBetween(opensearchTemplateURL,
                PARAM_TOKEN_OPEN, PARAM_TOKEN_CLOSE);
        // LOG.debug("List of opensearch parameters:");
        if (tokens != null) {
            for (String token : tokens) {
                String fullToken = PARAM_TOKEN_OPEN + token + PARAM_TOKEN_CLOSE;
                // LOG.debug("fullToken : " + fullToken);

                String cleanToken = token.replace("?", "");
                cleanToken = cleanToken.replace(":", "_");
                cleanToken = StringUtils.trim(cleanToken);
                // TODO review this hack (cnl, 06 may 2015):
                // to Lower case because the case Username and Password did not
                // work !
                if (cleanToken.equals("wsse_Username")) {
                    cleanToken = "wsse_username";
                }
                if (cleanToken.equals("wsse_Password")) {
                    cleanToken = "wsse_password";
                }

                // LOG.debug("cleanToken : " + cleanToken);

                /*
                 * replace the token with user input value if the value is not
                 * empty
                 */
                LOG.debug("replace " + fullToken + " - cleantoken " + cleanToken + " - "
                        + params.get(cleanToken));
                if (StringUtils.isNotEmpty(params.get(cleanToken))) {
                    // LOG.debug(cleanToken + "=" + params.get(cleanToken));
                    if ("startIndex".equals(cleanToken) && StringUtils.isNotEmpty(
                            strStartIndex)) {
                        /*
                         * replace startIndex with the value that was calculated
                         * above
                         */
                        opensearchTemplateURL = StringUtils.replace(opensearchTemplateURL,
                                fullToken, strStartIndex);
                    } else {
                        // LOG.debug("replace "+ fullToken +
                        // " - cleantoken "+cleanToken +
                        // " - "+params.get(cleanToken));
                        opensearchTemplateURL = StringUtils.replace(opensearchTemplateURL,
                                fullToken, params.get(cleanToken));
                    }
                } else {
                    /*
                     * leave empty value for the param
                     */
                    opensearchTemplateURL = StringUtils.replace(opensearchTemplateURL,
                            fullToken,
                            "");
                }
            }
        }
        return opensearchTemplateURL;
    }

    private OpenSearchParameter mergeParameterDetails(
            OpenSearchParameter osddParam, OpenSearchParameter resourceParam) {
        OpenSearchParameter osParam;
        if (resourceParam != null) {
            osParam = cloneOpenSearchParameter(resourceParam);
            LOG.debug("The parameter exists in the resource: " + osParam.getValue());
            if (osddParam != null) {
                LOG.debug("The parameter exists in the osdd too: " + osParam.getValue());

                if (osddParam.getHelp() != null && osddParam.getHelp().length() > 0) {
                    osParam.setHelp(osddParam.getHelp());
                }

                if (osddParam.getOptions() != null) {
                    osParam.setOptions(osddParam.getOptions());
                }
                if (StringUtils.isNotEmpty(osddParam.getPattern())) {
                    osParam.setPattern(osddParam.getPattern());
                }

                if (StringUtils.isNotEmpty(osddParam.getpMinInclusive())) {
                    LOG.debug("minInclusive: " + osddParam.getpMinInclusive());
                    if (Constants.DATE_TYPE.equals(osParam.getType())) {
                        String minInclusive = getInclusiveDate(osddParam.getpMinInclusive());
                        LOG.debug("date minInclusive: " + minInclusive);
                        osParam.setMinDate(minInclusive);
                        osParam.setFormValue(minInclusive);
                    } else {
                        try {
                            double num = Double.parseDouble(osddParam.getpMinInclusive());
                            osParam.setMinInclusive(num);
                            osParam.setHasMinInclusive(true);
                        } catch (NumberFormatException e) {
                        }
                    }
                    osParam.setpMinInclusive(osddParam.getpMinInclusive());
                }

                if (StringUtils.isNotEmpty(osddParam.getpMaxInclusive())) {
                    LOG.debug("maxInclusive: " + osddParam.getpMaxInclusive());
                    if (Constants.DATE_TYPE.equals(osParam.getType())) {
                        String maxInclusive = getInclusiveDate(osddParam.getpMaxInclusive());
                        LOG.debug("date maxInclusive: " + maxInclusive);
                        osParam.setMaxDate(maxInclusive);
                        osParam.setFormValue(maxInclusive);
                    } else {
                        try {
                            double num = Double.parseDouble(osddParam.getpMaxInclusive());
                            osParam.setMaxInclusive(num);
                            osParam.setHasMaxInclusive(true);
                        } catch (NumberFormatException e) {
                        }
                    }
                    osParam.setpMaxInclusive(osddParam.getpMaxInclusive());
                } else {
                    if (Constants.DATE_TYPE.equals(osParam.getType())) {
                        /*
                         * set the max date = current date
                         */
                        osParam.setMaxDate(Utility.dateFormat.format(
                                System.currentTimeMillis()));
                    }
                }
            }
        } else {
            osParam = cloneOpenSearchParameter(osddParam);
            LOG.debug(
                    "The parameter does not exist in the resource but exists in the OSDD: " + osParam.getValue());
            if (StringUtils.isNotEmpty(osddParam.getpMinInclusive())) {
                LOG.debug("minInclusive: " + osddParam.getpMinInclusive());
                if (Constants.DATE_TYPE.equals(osParam.getType())) {
                    String minInclusive = getInclusiveDate(osddParam.getpMinInclusive());
                    LOG.debug("date minInclusive: " + minInclusive);
                    osParam.setMinDate(minInclusive);
                    osParam.setFormValue(minInclusive);
                } else {
                    try {
                        double num = Double.parseDouble(osddParam.getpMinInclusive());
                        osParam.setMinInclusive(num);
                        osParam.setHasMinInclusive(true);
                    } catch (NumberFormatException e) {
                    }
                }
                osParam.setpMinInclusive(osddParam.getpMinInclusive());
            }

            if (StringUtils.isNotEmpty(osddParam.getpMaxInclusive())) {
                LOG.debug("maxInclusive: " + osddParam.getpMaxInclusive());
                if (Constants.DATE_TYPE.equals(osParam.getType())) {
                    String maxInclusive = getInclusiveDate(osddParam.getpMaxInclusive());
                    LOG.debug("date maxInclusive: " + maxInclusive);
                    osParam.setMaxDate(maxInclusive);
                    osParam.setFormValue(maxInclusive);
                } else {
                    try {
                        double num = Double.parseDouble(osddParam.getpMaxInclusive());
                        osParam.setMaxInclusive(num);
                        osParam.setHasMaxInclusive(true);
                    } catch (NumberFormatException e) {
                    }
                }
                osParam.setpMaxInclusive(osddParam.getpMaxInclusive());
            } else {
                if (Constants.DATE_TYPE.equals(osParam.getType())) {
                    /*
                     * set the max date = current date
                     */
                    osParam.setMaxDate(Utility.dateFormat.format(
                            System.currentTimeMillis()));
                }
            }
        }
        return osParam;
    }

    private OpenSearchParameter cloneOpenSearchParameter(
            OpenSearchParameter osParam) {
        OpenSearchParameter newOsParam = new OpenSearchParameter();
        newOsParam.setIndex(osParam.getIndex());
        newOsParam.setName(osParam.getName());
        newOsParam.setValue(osParam.getValue());
        newOsParam.setFormValue(osParam.getFormValue());
        newOsParam.setLabel(osParam.getLabel());
        newOsParam.setHelp(osParam.getHelp());
        newOsParam.setOrder(osParam.getOrder());
        newOsParam.setType(osParam.getType());
        newOsParam.setNamespace(osParam.getNamespace());
        newOsParam.setOptions(osParam.getOptions());
        newOsParam.setPattern(osParam.getPattern());
        newOsParam.setMinInclusive(osParam.getMinInclusive());
        newOsParam.setMaxInclusive(osParam.getMaxInclusive());
        newOsParam.setHasMinInclusive(osParam.isHasMinInclusive());
        newOsParam.setHasMaxInclusive(osParam.isHasMaxInclusive());
        newOsParam.setRequired(osParam.isRequired());
        newOsParam.setShow(osParam.isShow());
        newOsParam.setMaxDate(osParam.getMaxDate());
        newOsParam.setMinDate(osParam.getMinDate());
        return newOsParam;
    }

    private static void loadResourceParameters(Document resourceDoc) throws IOException {
        resourceNamespaces = XMLUtility.getNamespaces(resourceDoc, true);

        LOG.debug(
                "********************************************************************");
        LOG.debug("LOAD RESOURCE PARAMS");
        LOG.debug(
                "********************************************************************");
        for (Map.Entry<String, String> entry : resourceNamespaces.entrySet()) {
            LOG.debug(entry.getKey() + "===" + entry.getValue());
        }

        resourceParameters = new HashMap<String, OpenSearchParameter>();
        /*
         * get list of parameters
         */
        NodeList params = resourceDoc.getElementsByTagNameNS(
                Constants.OS_PARAM_NAMESPACE, "Parameter");
        if (params.getLength() > 0) {
            for (int pIdx = 0; pIdx < params.getLength(); pIdx++) {
                Node paramNode = params.item(pIdx);

                String value = XMLUtility.getNodeAttValue(paramNode, "value");
                if (StringUtils.isNotEmpty(value)) {
                    OpenSearchParameter osParam = new OpenSearchParameter();
                    osParam.setValue(value);
                    String prefix = StringUtils.substringBefore(value, ":");
                    String ns = resourceNamespaces.get(prefix);
                    osParam.setNamespace(ns);

                    String token = StringUtils.substringAfter(value, ":");
                    LOG.debug("prefix = " + prefix + ", token = " + token);
                    osParam.setName(XMLUtility.getNodeAttValue(paramNode, "name"), value);
                    osParam.setLabel(XMLUtility.getNodeAttValue(paramNode, "label"), value);
                    osParam.setType(XMLUtility.getNodeAttValue(paramNode, "type"));
                    osParam.setHelp(XMLUtility.getNodeAttValue(paramNode, "title"));

                    osParam.setOrder(pIdx + 1);

                    /*
                     * get list of options
                     */
                    NodeList opChildren = ((Element) paramNode)
                            .getElementsByTagNameNS(Constants.OS_PARAM_NAMESPACE, "Option");
                    if (opChildren.getLength() > 0) {
                        osParam.setOptions(new HashMap<>());
                        for (int idx = 0; idx < opChildren.getLength(); idx++) {
                            Node opChild = opChildren.item(idx);
                            String key = XMLUtility.getNodeAttValue(opChild, "value");
                            String val = XMLUtility.getNodeAttValue(opChild, "label");
                            if (StringUtils.isNotEmpty(key)) {
                                osParam.getOptions().put(key,
                                        StringUtils.isNotEmpty(val) ? val : key);
                            }
                        }
                    }
                    LOG.debug("Resource parameters: " + osParam.toString());
                    resourceParameters.put((token + "#" + ns), osParam);
                } else {
                    LOG.debug("Parameter value of parameter " + pIdx + " is empty.");
                }
            }
        }
    }

    private OpenSearchUrl parseUrl(Node urlNode,
            Map<String, String> osddNamespaces) throws IOException {

        LOG.debug(
                "********************************************************************");
        LOG.debug("osddNamespaces");
        LOG.debug(
                "********************************************************************");
        for (Map.Entry<String, String> entry : osddNamespaces.entrySet()) {
            LOG.debug(entry.getKey() + "===" + entry.getValue());
        }
        LOG.debug(
                "********************************************************************");
        /*
         * get list of parameters: value (e.g: searchTerm)/OpenSearchParameter
         */
        Map<String, OpenSearchParameter> osddParameters = new HashMap<>();
        NodeList params = ((Element) urlNode).getElementsByTagNameNS(
                Constants.OS_PARAM_NAMESPACE, "Parameter");
        if (params.getLength() > 0) {
            for (int pIdx = 0; pIdx < params.getLength(); pIdx++) {
                Node paramNode = params.item(pIdx);
                String valueWithBracket = XMLUtility.getNodeAttValue(paramNode, "value");
                if (StringUtils.isNotEmpty(valueWithBracket)) {
                    String valueWithoutBracket = StringUtils.substringBetween(
                            valueWithBracket, PARAM_TOKEN_OPEN, PARAM_TOKEN_CLOSE);
                    if (StringUtils.isNotEmpty(valueWithoutBracket)) {
                        valueWithoutBracket = StringUtils.replace(valueWithoutBracket, "?",
                                "");

                        OpenSearchParameter osParam = new OpenSearchParameter();
                        osParam.setIndex(Integer.toString(pIdx + 1));
                        osParam.setValue(valueWithoutBracket);
                        osParam.setName(XMLUtility.getNodeAttValue(paramNode, "name"),
                                valueWithoutBracket);

                        if (valueWithoutBracket.indexOf(":") > 0) {
                            String prefix = StringUtils.substringBefore(valueWithoutBracket,
                                    ":");
                            String ns = osddNamespaces.get(prefix);
                            osParam.setNamespace(ns);
                        } else {
                            osParam.setNamespace(Constants.OS_NAMESPACE);
                        }

                        osParam.setLabel(valueWithoutBracket);

                        String tooltip = XMLUtility.getNodeAttValue(paramNode, "title");
                        if (StringUtils.isNotEmpty(tooltip)) {
                            osParam.setHelp(tooltip);
                        }
                        osParam.setType(XMLUtility.getNodeAttValue(paramNode, "type"));
                        String pattern = XMLUtility.getNodeAttValue(paramNode, "pattern");
                        if (StringUtils.isNotEmpty(pattern)) {
                            osParam.setPattern(pattern);
                        }
                        String minInclusive = XMLUtility.getNodeAttValue(paramNode,
                                "minInclusive");
                        if (StringUtils.isNotEmpty(minInclusive)) {
                            osParam.setpMinInclusive(minInclusive);
                        }
                        String maxInclusive = XMLUtility.getNodeAttValue(paramNode,
                                "maxInclusive");
                        if (StringUtils.isNotEmpty(maxInclusive)) {
                            osParam.setpMaxInclusive(maxInclusive);
                        }
                        /*
                         * get list of options
                         */
                        NodeList opChildren = ((Element) paramNode)
                                .getElementsByTagNameNS(Constants.OS_PARAM_NAMESPACE,
                                        "Option");
                        if (opChildren.getLength() > 0) {
                            osParam.setOptions(new HashMap<String, String>());
                            for (int idx = 0; idx < opChildren.getLength(); idx++) {
                                Node opChild = opChildren.item(idx);
                                String key = XMLUtility.getNodeAttValue(opChild, "value");
                                String val = XMLUtility.getNodeAttValue(opChild, "label");
                                if (StringUtils.isNotEmpty(key)) {
                                    osParam.getOptions().put(key,
                                            StringUtils.isNotEmpty(val) ? val : key);
                                }
                            }
                        }

                        LOG.debug("osdd param key: " + valueWithoutBracket);
                        osddParameters.put(valueWithoutBracket, osParam);
                        LOG.debug(osParam.toString());
                    } else {
                        LOG.debug(
                                "Parameter value should be placed between {}:" + valueWithBracket);
                    }
                } else {
                    LOG.debug("Parameter value is empty.");
                }
            }
        }

        String templateUrl = XMLUtility.getNodeAttValue(urlNode, "template");
        LOG.debug("Original templateUrl: " + templateUrl);

        OpenSearchUrl openSearchUrl = new OpenSearchUrl();
        if (StringUtils.isNotEmpty(templateUrl)) {
            String[] tokens = StringUtils.substringsBetween(templateUrl,
                    PARAM_TOKEN_OPEN, PARAM_TOKEN_CLOSE);
            if (tokens != null) {
                int index = 1;
                for (final String token : tokens) {
                    boolean isRequired = !(StringUtils.endsWith(token, "?"));
                    String cleanToken = StringUtils.replace(token, "?", "");
                    LOG.debug("cleanToken = " + cleanToken);
                    OpenSearchParameter osddParam = osddParameters.get(cleanToken);

                    String resourceParamKey;
                    String paramNS;
                    if (cleanToken.indexOf(":") > 0) {
                        String prefix = StringUtils.substringBefore(cleanToken, ":");
                        paramNS = osddNamespaces.get(prefix);
                        String localName = StringUtils.substringAfter(cleanToken, ":");
                        resourceParamKey = localName + "#" + paramNS;
                    } else {
                        paramNS = Constants.OS_NAMESPACE;
                        resourceParamKey = cleanToken + "#" + paramNS;
                    }
                    LOG.debug("resourceParamKey = " + resourceParamKey);

                    OpenSearchParameter resourceParam = resourceParameters.get(
                            resourceParamKey);

                    OpenSearchParameter osParam;
                    if (osddParam == null && resourceParam == null) {
                        LOG.debug(
                                "The param does not exist in OSDD nor in the resource: " + cleanToken);
                        osParam = new OpenSearchParameter();
                        osParam.setValue(cleanToken);
                        osParam.setName(RandomStringUtils.randomAlphabetic(20) + index);
                        osParam.setLabel(cleanToken);
                        osParam.setNamespace(paramNS);
                    } else {
                        osParam = mergeParameterDetails(osddParam, resourceParam);
                    }
                    osParam.setIndex("" + index);
                    osParam.setRequired(isRequired);

                    String replacement = PARAM_TOKEN_OPEN + osParam.getName() + PARAM_TOKEN_CLOSE;
                    String searchString = PARAM_TOKEN_OPEN + token + PARAM_TOKEN_CLOSE;

                    templateUrl = StringUtils.replace(templateUrl, searchString,
                            replacement);
                    openSearchUrl.getParameters().add(osParam);
                    if (Constants.OS_NAMESPACE.equalsIgnoreCase(osParam.getNamespace()) && "os_searchTerms".equals(
                            osParam.getName())) {
                        openSearchUrl.setSupportTextSearch(true);
                        openSearchUrl.setTextSearchTitle(osParam.getHelp());
                    }
                    /*
                     if ("geo_box".equals(osParam.getName())) {
                     String placeNameKey = "placeName#" + Constants.OS_NAMESPACE;
                     LOG.debug("placeNameKey = " + placeNameKey);
                     OpenSearchParameter placeNameParam = resourceParameters.get(placeNameKey);
                     if (placeNameParam != null) {
                     LOG.debug("Place name param is not null.");
                     openSearchUrl.getParameters().add(placeNameParam);
                     }
                     }
                     */
                    index++;
                }
            }
            LOG.debug("Updated templateUrl: " + templateUrl);
            openSearchUrl.setTemplateUrl(templateUrl);

            /*
             * set indexOffset of the url if exist
             */
            String indexOffset = XMLUtility.getNodeAttValue(urlNode, "indexOffset");
            if (StringUtils.isNotEmpty(indexOffset)) {
                try {
                    openSearchUrl.setIndexOffset(Integer.parseInt(indexOffset));
                } catch (NumberFormatException e) {
                }
            }
            /*
             * set pageOffset of the url if exist
             */
            String pageOffset = XMLUtility.getNodeAttValue(urlNode, "pageOffset");
            if (StringUtils.isNotEmpty(pageOffset)) {
                try {
                    openSearchUrl.setPageOffset(Integer.parseInt(pageOffset));
                } catch (NumberFormatException e) {
                }
            }
        } else {
            throw new IOException(
                    "The template attribute of <Url> element which has type = application/atom+xml should not be empty.");
        }
        return openSearchUrl;
    }

    private String getInclusiveDate(String inclusiveDate) {
        try {
            String dateStr = inclusiveDate.substring(0, 10);
            Utility.dateFormat.parse(dateStr);
            LOG.debug("inclusiveDate = " + dateStr);
            return dateStr;
        } catch (ParseException e) {
            LOG.error(
                    "Error: The format of date parameters should start with yyyy-MM-dd (e.g. 2008-12-10 or 2008-12-10T00:00:00...)");
            return null;
        }
    }

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    /**
     * Retrieve the list of series and OSDD URL of dataset
     *
     * @throws IOException
     */
    public void loadCatalogInfo() throws IOException {
        LOG.debug("loadCatalogInfo()");
        List<String> seriesList = new ArrayList<>();

        Map<String, String> details = new HashMap<>();
        String osddContent = HTTPInvoker.invokeGET(config.getOsddLocation(), details);
        if (details.get(Constants.HTTP_GET_DETAILS_ERROR_CODE) != null) {
            throw new IOException(
                    "The OpenSearch Description Document " + config.getOsddLocation() + " is inaccessible!");
        } else {
            if (StringUtils.isNotEmpty(osddContent)) {
                String filterRegex = config.getSeries().getMenuOptionRegex();
                XPath path = XPathFactory.newInstance().newXPath();
                Document osddDoc = xmlParser.stream2Document(osddContent);
                path.setNamespaceContext(new XMLDocumentNamespaceResolver(osddDoc));

                /**
                 * load series list
                 */
                try {
                    String exp = "./os:OpenSearchDescription/os:Url[@rel='results' and @type='" + Constants.GEO_JSON_MIME_TYPE + "']";
                    Node urlNode = (Node) path.evaluate(exp, osddDoc, XPathConstants.NODE);
                    if (urlNode != null) {
                        exp = "./param:Parameter[@name='parentIdentifier']/param:Option/@value";
                        try {
                            NodeList list = (NodeList) path.evaluate(exp, urlNode, XPathConstants.NODESET);
                            Pattern p = null;
                            if (StringUtils.isNotEmpty(filterRegex)) {
                                p = Pattern.compile(filterRegex);
                            }
                            for (int i = 0; i < list.getLength(); i++) {
                                String value = list.item(i).getNodeValue();
                                if (p != null) {
                                    Matcher matcher = p.matcher(value);
                                    if (matcher.matches()) {
                                        LOG.debug("Adding entry that matches regex " + value);
                                        seriesList.add(value);
                                    }
                                } else {
                                    LOG.debug("Adding entry without filter " + value);
                                    seriesList.add(value);
                                }
                            }
                        } catch (XPathExpressionException ex) {
                            java.util.logging.Logger.getLogger(
                                    ProcessManager.class.getName()).log(Level.SEVERE,
                                    null, ex);
                        }
                    }
                } catch (XPathExpressionException ex) {
                    java.util.logging.Logger.getLogger(
                            ProcessManager.class.getName()).log(Level.SEVERE,
                            null, ex);
                }

                /*
                    get dataset OSDD URL template
                 */
                try {
                    String exp = "./os:OpenSearchDescription/os:Url[@rel='search' and @type='application/opensearchdescription+xml']";
                    Node urlNode = (Node) path.evaluate(exp, osddDoc, XPathConstants.NODE);
                    if (urlNode != null) {
                        String urlTemplate = XMLUtility.getNodeAttValue(urlNode, "template");
                        LOG.debug("Dataset OSDD URL = " + urlTemplate);
                        config.setDatasetOsddTemplateUrl(urlTemplate);
                    }
                } catch (XPathExpressionException ex) {
                    java.util.logging.Logger.getLogger(
                            ProcessManager.class.getName()).log(Level.SEVERE,
                            null, ex);
                }
            } else {
                throw new IOException(
                        "The OpenSearch Description Document from the  " + config.getOsddLocation() + " is empty !");
            }
        }

        if (!seriesList.isEmpty()) {
            config.getSeries().setSeriesMenuList(seriesList);
        } else {
            throw new IOException("No possible collection is returned by OSDD of the Catalogue.");
        }
    }

    public SearchResultSet retrieveRelatedProducts(String relatedProductsLink, Configuration config, List<String> showFields) throws IOException {
        LOG.debug("Retrieve related products from the URL " + relatedProductsLink);
        GeoJSONSearchResultParser jsonSearchResultParser = new GeoJSONSearchResultParser(showFields, config);

        String result = processOpenSearch(relatedProductsLink);

        return jsonSearchResultParser.buildResultSetFromJson(result);
    }

    private SearchResultSet retrieveRelatedProducts(List<String> viaLinks, Configuration config, List<String> showFields) throws IOException {
        LOG.debug("Retrieve related products");
        GeoJSONSearchResultParser jsonSearchResultParser = new GeoJSONSearchResultParser(showFields, config);
        SearchResultSet rs = new SearchResultSet();

        for (String link : viaLinks) {
            LOG.debug("Related product Url " + link);
            String result = processOpenSearch(link);
            LOG.debug("Result: " + result);
            SearchResultSet oneRs = jsonSearchResultParser.buildResultSetFromJson(result);
            rs.getItems().addAll(oneRs.getItems());
            if (StringUtils.isEmpty(rs.getGmlEnveloppe())) {
                rs.setGmlEnveloppe(oneRs.getGmlEnveloppe());
            }
        }
        rs.getAttributes().put("totalResults", String.valueOf(rs.getItems().size()));
        rs.getAttributes().put("osSearchType", "Datasets");
        rs.getAttributes().put("noScroller", ",no");

        return rs;
    }

}
