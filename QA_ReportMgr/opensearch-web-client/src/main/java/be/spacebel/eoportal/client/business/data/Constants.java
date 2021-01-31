package be.spacebel.eoportal.client.business.data;

import java.io.Serializable;

/**
 * This class contains the constants are using in the application
 *
 * @author mng
 */
public class Constants implements Serializable {

    public static final String CURRENT_VERSION = "2.1";

    private static final long serialVersionUID = 1L;

    public static final String OS_NAMESPACE = "http://a9.com/-/spec/opensearch/1.1/";
    public static final String OS_PARAM_NAMESPACE = "http://a9.com/-/spec/opensearch/extensions/parameters/1.0/";
    public static final String TIME_NAMESPACE = "http://a9.com/-/opensearch/extensions/time/1.0/";

    public static final String DATE_TYPE = "date";
    public static final String TIME_START = "start";
    public static final String TIME_END = "end";

    public static final String GEO_JSON_MIME_TYPE = "application/geo+json";

    public static final String OS_PARAMETERS_XML_FILE = "os-parameters.xml";
    public static final String HTTP_GET_DETAILS_ERROR_CODE = "errorCode";
    public static final String HTTP_GET_DETAILS_ERROR_MSG = "errorMsg";

    public static final String OS_RESPONSE = "osResponse";
    public static final String OS_TOTAL_RESULTS = "totalResults";

    public static final String OS_FIRSTPAGE = "firstPage";
    public static final String OS_PREVIOUSPAGE = "previousPage";
    public static final String OS_NEXTPAGE = "nextPage";
    public static final String OS_LASTPAGE = "lastPage";
    public static final String OS_OSDD_URL = "osddUrl";

    public static String DATEFORMAT = "yyyy-MM-dd";

    public static final String AUTO_COMPLETE_OPTION_SEPARATOR = "#####";
    public static final String AUTO_COMPLETE_LIST = "open-list";

    public static final String NO_BBOX = "NO_BBOX";

    public static final String THUMBNAIL_SIZE = "___THUMBNAIL_SIZE___";
    public static final String SERIES_SEARCH = "Series";
    public static final String DATASET_SEARCH = "Dataset";
    public static final String RELATED_SEARCH = "Related";

    public static final String DEGRADEDANCILLARY = "http://qcmms.esa.int/quality-indicators/#degradedAncillaryDataPercentageMetric";
    public static final String SENSORQUALITYMETRIC = "http://qcmms.esa.int/quality-indicators/#sensorQualityMetric";
    public static final String RADIOMETRICQUALITYMETRIC = "http://qcmms.esa.int/quality-indicators/#radiometricQualityMetric";
    public static final String DEGRADEDDATAPERCENTAGEMETRIC = "http://qcmms.esa.int/quality-indicators/#degradedDataPercentageMetric";
    public static final String GEOMETRICQUALITYMETRIC = "http://qcmms.esa.int/quality-indicators/#geometricQualityMetric";
    public static final String GENERALQUALITYMETRIC = "http://qcmms.esa.int/quality-indicators/#generalQualityMetric";
    public static final String FORMATCORRECTNESSMETRIC = "http://qcmms.esa.int/quality-indicators/#formatCorrectnessMetric";
    public static final String FEASIBILITYCONTROLMETRIC = "http://qcmms.esa.int/quality-indicators/#feasibilityControlMetric";
    public static final String DELIVERYCONTROLMETRIC = "http://qcmms.esa.int/quality-indicators/#deliveryControlMetric";
    public static final String ORDINARYCONTROLMETRIC = "http://qcmms.esa.int/quality-indicators/#ordinaryControlMetric";
    public static final String DETAILEDCONTROLMETRIC = "http://qcmms.esa.int/quality-indicators/#detailedControlMetric";

    public static final int INDENT_LEVEL = 20;
    public static final int INDENT_ICON_WIDTH = 14;
    public static final int INDENT_TEXT_SPACE = 3;

}
