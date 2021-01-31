package be.spacebel.eoportal.client.model;

import be.spacebel.eoportal.client.business.data.Constants;
import be.spacebel.eoportal.client.model.data.Configuration;
import be.spacebel.eoportal.client.parser.XMLParser;
import be.spacebel.eoportal.client.util.FacesMessageUtil;
import be.spacebel.eoportal.client.util.Utility;
import be.spacebel.eoportal.client.util.XMLUtility;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A managed bean contains business logic, form values, getter and setter
 * methods that will be used in the JSF configuration pages
 *
 * @author mng
 */
@ManagedBean(name = "configBean")
@ViewScoped
public class ConfigurationBean implements Serializable {

    private static ResourceBundle systemResource;
    private static final Logger LOG = Logger.getLogger(ConfigurationBean.class);

    private Configuration defaultConfiguration;
    private Configuration configuration;
    private List<String> datasetBlacklist;

    @PostConstruct
    public void ConfigurationBean() {
        LOG.debug("init ConfigurationBean");
        boolean isDockerDeployment = false;

        String resourceDir;

        String dockerDeployment = System.getenv("IS_DOCKER_DEPLOYMENT");
        if (StringUtils.isNotEmpty(dockerDeployment) && dockerDeployment.equalsIgnoreCase("true")) {
            isDockerDeployment = true;
        }

        if (isDockerDeployment) {
            String configDir = System.getenv("CONFIG_DIR");
            if (StringUtils.isNotEmpty(configDir)) {
                if (!Files.exists(Paths.get(configDir))) {
                    configDir = null;
                }
            }

            if (StringUtils.isEmpty(configDir)) {
                configDir = "/config";
            }

            resourceDir = configDir + "/xml/";

            try {
                systemResource = new PropertyResourceBundle(new FileInputStream(configDir + "/properties/resources.properties"));
            } catch (IOException ex) {
                FacesMessageUtil.addErrorMessage(ex);
            }

        } else {
            resourceDir = Utility.getAbsolutePath() + "data/";
            systemResource = PropertyResourceBundle.getBundle("resources");
        }

        loadDefaultConfiguration(resourceDir);
        LOG.debug(this.defaultConfiguration.getSeries().isSeriesMenu());
        // Removed Liferay: NO PREFERENCe from DB only default prefs
        this.configuration = new Configuration(this.defaultConfiguration);
        LOG.debug(this.configuration.getSeries().isSeriesMenu());
        LOG.debug("--");
        loadParameters(resourceDir);

        backwardCompatible();

        this.datasetBlacklist = getBlacklist(this.configuration.getDataset().getNamespaces(),
                this.configuration.getDataset().getBlackList());

        // Fix voila report dir in case of docker deployment
        if (isDockerDeployment) {
            configuration.setVoilaReportsDir("/voila/reports");
        }
    }

    private void loadParameters(String resourceDir) {
        List<String> parameters = getParameters(resourceDir);

        // load dataset parameters
        if (this.configuration.getDataset().getOrderedParameters() == null) {
            this.configuration.getDataset().setOrderedParameters(parameters);
        } else {
            if (this.configuration.getDataset().getOrderedParameters().size() != parameters.size()) {
                this.configuration.getDataset().setOrderedParameters(
                        synchroniseParameters(parameters,
                                this.configuration.getDataset().getOrderedParameters()));
            }
        }
    }

    private void backwardCompatible() {
        boolean isUpgraded = false;
        if (StringUtils.isEmpty(this.configuration.getVersion())
                || !Constants.CURRENT_VERSION.equals(this.configuration.getVersion())) {
            isUpgraded = true;
            this.configuration.setVersion(Constants.CURRENT_VERSION);
        }

        if (isUpgraded) {
            LOG.debug("Upgrade case.");
            this.configuration.getDataset().setBlackList(this.defaultConfiguration.getDataset().getBlackList());
        }
    }

    private List<String> synchroniseParameters(List<String> resourceList, List<String> configuredList) {
        LOG.debug("Synchronise parameters");
        List<String> synchronisedList = new ArrayList<>();

        for (String cParam : configuredList) {
            if (resourceList.contains(cParam)) {
                synchronisedList.add(cParam);
            } else {
                LOG.debug("Parameter " + cParam + " doesn't exist anymore.");
            }
        }

        for (String rParam : resourceList) {
            if (!configuredList.contains(rParam)) {
                LOG.debug("New parameter: " + rParam);
                synchronisedList.add(rParam);
            }
        }

        return synchronisedList;
    }

    private void loadDefaultConfiguration(String resourceDir) {
        String newOsddUrl = getSystemResource("osdd.url",
                "https://qcmms-cat.spacebel.be/eo-catalog/description?httpAccept=application/opensearchdescription%2Bxml");
        String rowsPerPage = getSystemResource("rows.per.page", "10");
        String identifierPrefix = getSystemResource("default.identifier.prefix", "");
        String previewImageOrder = getSystemResource("preview.image.order", 
                "mediaQuicklook,image,mediaThumbnail,preview,icon");

        String staticMapServiceUrl = getSystemResource("static.map.service.url", 
                "http://open.mapquestapi.com/staticmap/v4/getmap");
        String staticMapServiceUniqueKey = getSystemResource("static.map.service.unique.key", "");
        String geonamesAccount = getSystemResource("geonames.username", "");
        String geonamesRadius = getSystemResource("geonames.radius", "100000");
        String earthObservationPortalUrl = getSystemResource("earth.observation.portal.url", "https://eoportal.org");

        this.defaultConfiguration = new Configuration(newOsddUrl);
        this.defaultConfiguration.setVersion(Constants.CURRENT_VERSION);

        try {
            this.defaultConfiguration.setRowsPerPage(Integer.parseInt(rowsPerPage));
        } catch (NumberFormatException e) {
            this.defaultConfiguration.setRowsPerPage(10);
        }
        
        this.defaultConfiguration.setIdentifierPrefix(identifierPrefix);
        this.defaultConfiguration.setPreviewImageOrder(previewImageOrder);

        //this.defaultConfiguration.setBingMapKey(bingMapKey);
        this.defaultConfiguration.setStaticMapServiceUrl(staticMapServiceUrl);
        this.defaultConfiguration.setStaticMapServiceUniqueKey(staticMapServiceUniqueKey);
        this.defaultConfiguration.setGeonamesAccount(geonamesAccount);

        try {
            this.defaultConfiguration.setGeonamesRadius(Double.parseDouble(geonamesRadius));
        } catch (NumberFormatException e) {
            this.defaultConfiguration.setGeonamesRadius(100000);
        }
        this.defaultConfiguration.setEarthObservationPortalUrl(earthObservationPortalUrl);

        /*
            Voila report
         */
        this.defaultConfiguration.setVoilaReportsDir(getSystemResource("reports.directory", ""));
        this.defaultConfiguration.setVoilaReportFileNamePattern(
                getSystemResource("report.file.name.pattern", "QCMMS_Report_{ParentIdentifier}_{ProductIdentifier}.ipynb"));
        this.defaultConfiguration.setVoilaReportUrl(getSystemResource("report.base.url", ""));
        String freshReport = getSystemResource("fresh.report", "true");
        LOG.debug("Rresh report: " + freshReport);
        this.defaultConfiguration.setFreshReport(Boolean.parseBoolean(freshReport));

        /**
         * ***************************
         */
        /* Series values                                */
        /**
         * ************************
         */
        String defaultNamespaces = getDefaultNamespaces(resourceDir);

        // Added series menu as a static list of parent identifiers
        String seriesMenuList = getSystemResource("series.menu.list", null);
        List<String> menuList = Arrays.asList(seriesMenuList.split(","));
        this.defaultConfiguration.getSeries().setSeriesMenuList(menuList);
        try {
            this.defaultConfiguration.getSeries()
                    .setMenuOptionRegex(getSystemResource("series.menu.option.regex"));
        } catch (Exception e) {

        }       

        /**
         * ***************************
         */
        /* Dataset values                                */
        /**
         * ***************************
         */
        String datasetDefaultView = getSystemResource("dataset.default.view", "list-view");
        String datasetBlacklistParams = getSystemResource("dataset.blacklist.parameter", 
                "geo:uid,geo:lat,geo:lon,geo:name,geo:radius,dc:type,dc:publisher,semantic:classifiedAs,os:language");
        String datasetListViewAttributes = getSystemResource("dataset.listview.shown.attributes", 
                "seriesId,identifier,startDate,endDate,platform,instrument,sensor,productType,orbitNumber");
        String datasetMoreOptions = getSystemResource("dataset.more.options", "describedby,alternate,via");
        String datasetThumbnailWidth = getSystemResource("dataset.thumbnail.width", "285px");
        String datasetThumbnailHeight = getSystemResource("dataset.thumbnail.height", "250px");
        String datasetThumbnailService = getSystemResource("thumbnail.service", "no");

        this.defaultConfiguration.getDataset().setDefaultView(datasetDefaultView);
        this.defaultConfiguration.getDataset().setBlackList(datasetBlacklistParams);
        this.defaultConfiguration.getDataset().setNamespaces(defaultNamespaces);
        this.defaultConfiguration.getDataset().setSelectedListViewAttributes(datasetListViewAttributes.split(","));
        this.defaultConfiguration.getDataset().setSelectedMoreOptions(datasetMoreOptions.split(","));
        this.defaultConfiguration.getDataset().setThumbnailWidth(datasetThumbnailWidth);
        this.defaultConfiguration.getDataset().setThumbnailHeight(datasetThumbnailHeight);
        this.defaultConfiguration.getDataset().setThumbnailService(datasetThumbnailService);
    }

    private String getSystemResource(String key, String defaultValue) {
        String value = getSystemResource(key);
        if (StringUtils.isNotEmpty(key)) {
            return value;
        } else {
            return defaultValue;
        }
    }

    private String getSystemResource(String name) {
        try {
            String value = systemResource.getString(name);
            if (StringUtils.isNotEmpty(value)) {
                value = StringUtils.trim(value);
            }
            return value;
        } catch (MissingResourceException | NullPointerException | ClassCastException e) {
            return null;
        }
    }

    private String getDefaultNamespaces(String resourceDir) {
        StringBuilder sb = new StringBuilder();
        XMLParser xmlParser = new XMLParser();
        String xslFile = resourceDir + Constants.OS_PARAMETERS_XML_FILE;
        LOG.debug("xslFile = " + xslFile);

        Document osParamsDoc = xmlParser.file2Document(xslFile);

        NamedNodeMap atts = osParamsDoc.getDocumentElement().getAttributes();
        if (atts != null) {
            for (int i = 0; i < atts.getLength(); i++) {
                Node node = atts.item(i);
                String prefix = node.getNodeName().trim();
                String ns = node.getNodeValue();
                if (StringUtils.startsWithIgnoreCase(prefix, "xmlns:")) {
                    sb.append(StringUtils.substringAfter(prefix, ":"));
                    sb.append("=");
                    sb.append(ns);
                    if (i < (atts.getLength() - 1)) {
                        sb.append("\r\n");
                    }
                }
            }
        }

        return sb.toString();
    }

    private List<String> getBlacklist(String namespaces, String params) {
        LOG.debug("getBlacklist()");
        LOG.debug("namespaces: " + namespaces);
        LOG.debug("params: " + params);
        List<String> blacklist = new ArrayList<>();

        try {
            if (StringUtils.isNotEmpty(namespaces) && StringUtils.isNotEmpty(params)) {
                /* parse namespaces */
                BufferedReader reader = new BufferedReader(new StringReader(namespaces));
                String line = null;
                Map<String, String> nsMap = new HashMap<>();
                while ((line = reader.readLine()) != null) {
                    LOG.debug("NS line: " + line);
                    if (line.indexOf("=") > 0) {
                        String prefix = StringUtils.substringBefore(line, "=");
                        LOG.debug("prefix: " + prefix);
                        String ns = StringUtils.substringAfter(line, "=");
                        LOG.debug("ns: " + ns);
                        nsMap.put(prefix, ns);
                    }
                }
                /* parse parameters */
                String[] paramList = params.split(",");
                for (String param : paramList) {
                    String[] preParam = param.split(":");
                    if (preParam.length == 2) {
                        String blParam = preParam[1] + "#" + nsMap.get(preParam[0]);
                        LOG.debug("blParam = " + blParam);
                        blacklist.add(blParam);
                    }
                }
            }
        } catch (IOException e) {
            LOG.error(e);
        }
        return blacklist;
    }

    private List<String> getParameters(String resourceDir) {
        XMLParser xmlParser = new XMLParser();
        Document resourceDoc = xmlParser.file2Document(resourceDir + Constants.OS_PARAMETERS_XML_FILE);

        List<String> parameters = new ArrayList<>();
        /*
         * get list of parameters
         */
        NodeList params = resourceDoc.getElementsByTagNameNS(Constants.OS_PARAM_NAMESPACE, "Parameter");
        if (params.getLength() > 0) {
            for (int pIdx = 0; pIdx < params.getLength(); pIdx++) {
                Node paramNode = params.item(pIdx);

                String value = XMLUtility.getNodeAttValue(paramNode, "value");
                String label = XMLUtility.getNodeAttValue(paramNode, "label");

                if (StringUtils.isNotEmpty(value) && StringUtils.isNotEmpty(label)) {
                    String param = label + " (" + value + ")";
                    parameters.add(param);
                }
            }
        }

        return parameters;
    }

    //////////////////////////////////////////////////
    /**
     * Getter method
     *
     * @return Configuration object
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Setter method
     *
     * @param newConfig
     */
    public void setConfiguration(Configuration newConfig) {
        this.configuration = newConfig;
    }

    /**
     * Obtains the dataset black list
     *
     * @return The dataset black list
     */
    public List<String> getDatasetBlacklist() {
        return datasetBlacklist;
    }

}
