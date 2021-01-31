package be.spacebel.catalog.utils;

/**
 * This class defines constants that are used by the application
 * 
 * @author tth
 *
 */
public class Constants {
	
	/**
	 * SOLR collections
	 */
	public static String SERIES = "cat";
	public static String DATASET = "dataset";
	

	/**
	 * Namespace constants
	 */
	public static String DC_NS = "http://purl.org/dc/elements/1.1/";
	public static String DCT_NS = "http://purl.org/dc/terms/";
	public static String GMD_NS = "http://www.isotc211.org/2005/gmd";
	public static String GMI_NS = "http://www.isotc211.org/2005/gmi";
	public static String GMX_NS = "http://www.isotc211.org/2005/gmx";
	public static String GML_NS = "http://www.opengis.net/gml/3.2";
	public static String GCO_NS = "http://www.isotc211.org/2005/gco";
	public static String ATOM_NS = "http://www.w3.org/2005/Atom";
	public static String OS_NS = "http://a9.com/-/spec/opensearch/1.1/";
	public static String XLINK_NS = "http://www.w3.org/1999/xlink";
	public static String GEORSS_NS = "http://www.georss.org/georss";
	public static String PARAM_NS = "http://a9.com/-/spec/opensearch/extensions/parameters/1.0/";
	public static String EOP_20_NS = "http://www.opengis.net/eop/2.0";
	public static String OPT_20_NS = "http://www.opengis.net/opt/2.0";
	public static String SAR_20_NS = "http://www.opengis.net/sar/2.0";
	public static String ALT_20_NS = "http://www.opengis.net/alt/2.0";
	public static String ATM_20_NS = "http://www.opengis.net/atm/2.0";
	public static String EOP_21_NS = "http://www.opengis.net/eop/2.1";
	public static String OPT_21_NS = "http://www.opengis.net/opt/2.1";
	public static String SAR_21_NS = "http://www.opengis.net/sar/2.1";
	public static String ALT_21_NS = "http://www.opengis.net/alt/2.1";
	public static String ATM_21_NS = "http://www.opengis.net/atm/2.1";
	public static String OM_NS = "http://www.opengis.net/om/2.0";
	public static String OWS_NS = "http://www.opengis.net/ows/2.0";
	public static String MEDIA_NS = "http://search.yahoo.com/mrss/";
	public static String SRU_NS = "http://a9.com/-/opensearch/extensions/sru/2.0/";
	public static String OM_PROFILE = "http://www.opengis.net/spec/EOMPOM/1.1";
	public static String CSV_PROFILE = "text/csv";

	/**
	 * Queryables constants
	 */
	public static String BBOX_PARAM = "bbox";
	public static String GEOMETRY_PARAM = "geometry";
	public static String CLASSIFIED_AS_PARAM = "classifiedAs";
	public static String INSTRUMENT_PARAM = "instrument";
	public static String ITEM_PER_PAGE_PARAM = "maximumRecords";
	public static String LAT_PARAM = "lat";
	public static String LON_PARAM = "lon";
	public static String GEONAME_PARAM = "name";
	public static String ORGANISATION_NAME_PARAM = "organisationName";
	public static String PLATFORM_PARAM = "platform";
	public static String PUBLISHER_PARAM = "publisher";
	public static String QUERY_PARAM = "query";
	public static String RADIUS_PARAM = "radius";
	public static String START_INDEX_PARAM = "startRecord";
	public static String START_PAGE_PARAM = "startPage";
	public static String SUBJECT_PARAM = "subject";
	public static String UID_PARAM = "uid";
	public static String PARENT_ID_PARAM = "parentIdentifier";
	public static String START_DATE_PARAM = "startDate";
	public static String TITLE_PARAM = "title";
	public static String TYPE_PARAM = "type";
	public static String END_DATE_PARAM = "endDate";
	public static String HTTP_ACCEPT_PARAM = "httpAccept";
	public static String RECORD_SCHEMA_PARAM = "recordSchema";
	public static String PRODUCTION_STATUS_PARAM = "productionStatus";
	public static String ACQ_TYPE_PARAM = "acquisitionType";
	public static String ORBIT_DIRECTION_PARAM = "orbitDirection";
	public static String ORBIT_NUMBER_PARAM = "orbitNumber";
	public static String ORBIT_TYPE_PARAM = "orbitType";
	public static String TRACK_PARAM = "track";
	public static String FRAME_PARAM = "frame";
	public static String SWATH_IDENTIFIER_PARAM = "swathIdentifier";
	public static String CLOUD_COVER_PARAM = "cloudCover";
	public static String SNOW_COVER_PARAM = "snowCover";
	public static String LOWEST_LOCATION_PARAM = "lowestLocation";
	public static String HIGHEST_LOCATION_PARAM = "highestLocation";
	public static String PRODUCT_VERSION_PARAM = "productVersion";
	public static String PRO_QUAL_STATUS_PARAM = "productQualityStatus";
	public static String PRO_QUAL_DEG = "productQualityDegradation";
	public static String PRO_QUAL_DEG_TAG_PARAM = "productQualityDegradationTag";
	public static String PROCESSOR_NAME_PARAM = "processorName";
	public static String PROCESSING_CENTER_PARAM = "processingCenter";
	public static String PROCESSING_LEVEL_PARAM = "processingLevel";
	public static String NATIVE_PRODUCT_FORMAT_PARAM = "nativeProductFormat";
	public static String COMPOSITE_TYPE_PARAM = "compositeType";
	public static String CREATION_DATE_PARAM = "creationDate";
	public static String MODIFICATION_DATE_PARAM = "modificationDate";
	public static String PROCESSING_DATE_PARAM = "processingDate";
	public static String SENSOR_MODE_PARAM = "sensorMode";
	public static String SENSOR_TYPE_PARAM = "sensorType";
	public static String ARCHIVING_CENTER_PARAM = "archivingCenter";
	public static String PROCESSING_MODE_PARAM = "processingMode";
	public static String AVAILABILITY_TIME_PARAM = "availabilityTime";
	public static String ACQ_STATION_PARAM = "acquisitionStation";
	public static String ACQ_SUBTYPE_PARAM = "acquisitionSubType";
	public static String START_TIME_ASC_NODE_PARAM = "startTimeFromAscendingNode";
	public static String COMP_TIME_ASC_NODE_PARAM = "completionTimeFromAscendingNode";
	public static String ILLU_ZEN_ANGLE_PARAM = "illuminationZenithAngle";
	public static String ILLU_AZI_ANGLE_PARAM = "illuminationAzimuthAngle";
	public static String ILLU_ELE_ANGLE_PARAM = "illuminationElevationAngle";
	public static String POLAR_MODE_PARAM = "polarisationMode";
	public static String POLAR_CHANNELS_PARAM = "polarisationChannels";
	public static String ANTENNA_LOOK_DIR_PARAM = "antennaLookDirection";
	public static String MIN_INCI_ANGLE_PARAM = "minimumIncidenceAngle";
	public static String MAX_INCI_ANGLE_PARAM = "maximumIncidenceAngle";
	public static String DOPPLER_FREQ_PARAM = "dopplerFrequency";
	public static String INCI_ANGLE_PARAM = "incidenceAngleVariation";
	public static String PRODUCT_TYPE_PARAM = "productType";
	public static String PLATFORM_SERIAL_ID_PARAM = "platformSerialIdentifier";
	public static String DOI_PARAM = "doi";
	public static String RESOLUTION_PARAM = "resolution";
	public static String SPECTRAL_RANGE_PARAM = "spectralRange";
	public static String WAVRE_LENGTH_PARAM = "wavelengths";
	public static String PROFILE_PARAM = "profile";
	public static String PRIORITY_PARAM = "priority";
	public static String SPECIFICATION_TITLE_PARAM = "specificationTitle";
	public static String DEGREE_PARAM = "degree";
	
	public static String USE_LIMITATION = "useLimitation";
	public static String OTHER_CONSTRAINT = "otherConstraint";
	public static String DEGRADED_DATA_PERCENTAGE_METRIC = "degradedDataPercentageMetric";
	public static String DEGRADED_ANCILLARY_DATA_PERCENTAGE_METRIC = "degradedAncillaryDataPercentageMetric";
	public static String FORMAT_CORRECTNESS_METRIC = "formatCorrectnessMetric";
	public static String GENERAL_QUALITY_METRIC = "generalQualityMetric";
	public static String GEOMETRIC_QUALITY_METRIC = "geometricQualityMetric";
	public static String RADIOMETRIC_QUALITY_METRIC = "radiometricQualityMetric";
	public static String SENSOR_QUALITY_METRIC = "sensorQualityMetric";
	public static String FEASIBILITY_CONTROL_METRIC = "feasibilityControlMetric";
	public static String DELIVERY_CONTROL_METRIC = "deliveryControlMetric";
	public static String ORDINARY_CONTROL_METRIC = "ordinaryControlMetric";
	public static String DETAILED_CONTROL_METRIC = "detailedControlMetric";
        
        public static String HARMONIZATION_CONTROL_METRIC = "harmonizationControlMetric";
        public static String IP_FOR_LP_INFORMATION_METRIC = "ipForLpInformationMetric";
        public static String LP_INTERPRETATION_METRIC = "lpInterpretationMetric";
        public static String LP_METADATA_CONTROL_METRIC = "lpMetadataControlMetric";
        public static String LP_ORDINARY_CONTROL_METRIC = "lpOrdinaryControlMetric";
        public static String LP_THEMATIC_VALIDATION_METRIC = "lpThematicValidationMetric";        
	
	/**
	 * Default value constants
	 */

	public static String DEFAULT_ITEM_PER_PAGE = "10";
	public static String DEFAULT_START_INDEX = "1";

	/**
	 * File Name constants
	 */

	public static String ATOM_TEMPLATE = "atom.xml";
	public static String EOP_TEMPLATE = "eop.xml";
	public static String OPT_TEMPLATE = "opt.xml";
	public static String SAR_TEMPLATE = "sar.xml";
	public static String OSDD_TEMPLATE = "osdd.xml";
	public static String OSDD_SERIES_TEMPLATE = "osdd-series.xml";
	public static String OSDD_SAR_SERIES_TEMPLATE = "osdd-sar-series.xml";
	public static String OSDD_OPT_SERIES_TEMPLATE = "osdd-opt-series.xml";
	public static String ERROR_500_TEMPLATE = "error500.xml";
	public static String ERROR_400_TEMPLATE = "error400.xml";
	public static String GMD_2_GMI_XSL = "gmd2gmi.xsl";
	public static String EUM_GMI_2_GMI_XSL = "eumatsatgmi2gmi.xsl";
	public static String GMI_2_GMD_XSL = "gmi2gmd.xsl";
	public static String OM10_2_OM11_XSL = "omv10ToOmv11.xsl";
	public static String OM11_2_OM10_XSL = "omv11ToOmv10.xsl";
	public static String ATOM_2_OM_XSL = "atomToOm.xsl";

	/**
	 * Server info constants
	 */
	public static String SERVER_URL = "serverURL";
	public static String REQUEST_URL = "requestURL";
	public static String QUERY_STRING = "queryString";
	public static String SERIES_BASE_PATH = "series";
	public static String OSDD_CONTEXT_PATH = "description";
	

	/**
	 * Mimetype constants
	 *
	 */
	public static final String ATOM_MIME_TYPE = "application/atom+xml";
	public static final String OSDD_MIME_TYPE = "application/opensearchdescription+xml";
	public static final String TEXT_XML_MIME_TYPE = "text/xml";
	public static final String TEXT_HTML_MIME_TYPE = "text/html";
	public static final String YAML_MIME_TYPE = "text/yaml";
	public static final String APPLICATION_PDF_MIME_TYPE = "application/pdf";
	public static final String APPLICATION_WORD_MIME_TYPE = "application/msword";
	public static final String APPLICATION_ZIP_MIME_TYPE = "application/zip";	 
	public static final String ISO_19139_MIME_TYPE = "application/vnd.iso.19139+xml";
	public static final String ISO_19139_2_MIME_TYPE = "application/vnd.iso.19139-2+xml";	
	public static final String JPEG_MIME_TYPE = "image/jpeg";
	public static final String PNG_MIME_TYPE = "image/png";
	public static final String TIFF_MIME_TYPE = "image/tiff";
	public static final String GIF_MIME_TYPE = "image/gif";
	public static final String EOP_GML_MIME_TYPE = "application/gml+xml";
	public static final String BINARY_MIME_TYPE = "application/x-binary";
	public static final String GEOJSON_MIME_TYPE = "application/geo+json";
	public static final String OPENAPI_MIME_TYPE = "application/openapi+json;version=3.0";
	public static final String EOP_OM_MIME_TYPE = "application/gml+xml;profile=http://www.opengis.net/spec/EOMPOM/1.1";
	public static final String EOP_OM10_MIME_TYPE = "application/gml+xml;profile=http://www.opengis.net/spec/EOMPOM/1.0";
        
        public static final String JUPYTER_NOTEBOOK_MIME_TYPE="application/x-ipynb+json";
        public static final String ISO_19157_2_MIME_TYPE="application/vnd.iso.19157-2";

	/**
	 * Schema constants
	 *
	 */
	public static final String GMI_SCHEMA = "gmi";
	public static final String GMD_SCHEMA = "gmd";
	public static final String OM_SCHEMA = "om";
	public static final String OM10_SCHEMA = "om10";
	public static final String GEOJSON_SCHEMA = "geoJSON";

	/**
	 * Constants for geo computation
	 */
	public static final int EARTH_RADIUS = 6371;
	public static final int DISTANCE_BETWEEN_TWO_LAT = 111;
	public static final int DEFAULT_SEARCH_PRIORITY = 9999;
	public static final int KM_TO_M = 1000;

	/***
	 * Sensor Type
	 */
	public static final String OPTICAL_SENSOR_TYPE = "OPTICAL";
	public static final String RADAR_SENSOR_TYPE = "RADAR";
	public static final String ALT_SENSOR_TYPE = "ALTIMETRIC";
	public static final String ATM_SENSOR_TYPE = "ATMOSPHERIC";
	public static final String LIMB_SENSOR_TYPE = "LIMB";

	/***
	 * HTTP Operation
	 */

	public static final String HTTP_GET = "GET";
	public static final String HTTP_POST = "POST";
	public static final String HTTP_DELETE = "DELETE";
	public static final String HTTP_OPTIONS = "OPTIONS";
	public static final String HTTP_HEAD = "HEAD";
}
