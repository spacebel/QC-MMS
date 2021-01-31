package be.spacebel.catalog.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

public class BundleUtils {
	// TODO this class should be replace with Spring boot config:
	// https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html
	// not yet done because all classes that use that configuration need to be
	// declared as @Service, @Component or @Controller to be able to load @Value
	// properties

	/*
	 * the properties of file resources.properties
	 */
	public static String DATSET_ORIGINAL_KEY = "dataset.original";
	public static String ICON_BASE_URL_KEY = "icon.base.url";
	public static String DEFAULT_MIMETYPE = "mimeType.default";

	/*
	 * the properties of file parameters.properties
	 */
	public static String ATOM_AUTHOR_NAME_KEY = "atom.author.name";
	public static String ATOM_AUTHOR_EMAIL_NAME_KEY = "atom.author.email";
	public static String ATOM_GENERATOR_KEY = "atom.generator";
	public static String ATOM_TITLE_KEY = "atom.title";
	public static String ATOM_RIGHTS_KEY = "atom.rights";
	public static String RADIUS_DEFAULT_VALUE = "radius.fixed.default.value";
	public static String FACET_COUNT = "facet.count";
	public static String OSDD_SHORT_NAME = "osdd.shortName";
	public static String OSDD_DESC = "osdd.description";
	public static String OSDD_TAGS = "osdd.tags";

	/*
	 * the properties of file messages.properties
	 */
	public static String INTERNAL_SERVER_ERROR = "error.internal";
	public static String INVALID_PARAM_ERROR = "error.invalid.parameter";
	public static String INVALID_PARAM_VALUE_ERROR = "error.invalid.parameter.value";
	public static String MISSING_PARAM_VALUE_ERROR = "error.missing.parameter";
	public static String INVALID_MIMETYPE_ERROR = "error.invalid.mimetype";
	public static String INVALID_METADATA_ERROR = "error.invalid.metadata";
	public static String SERIES_ID_NOT_FOUND_ERROR = "error.series.id.notfound";
	public static String SERIES_UNSEARCHABLE_ERROR = "error.series.id.unsearchable";
	public static String DATASET_ID_NOT_FOUND_ERROR = "error.dataset.id.notfound";

	private static Properties resources;
	private static Properties parameters;
	private static Properties messages;
	private static Properties icons;
	private static Properties optParameters;
	private static Properties sarParameters;
	private static Properties eopParameters;
	private static Properties facetMappings;
	private static Properties eo2SolrMappings;
	private static Properties eo2SolrMappingsEnumParam;
	private static Properties eo2SolrMappingsEnumParam4Series;
	private static Map<String, String> resourceExtensions;

	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(BundleUtils.class);

	static {
		try {
			loadResource();
		} catch (Exception e) {
			log.error("", e);
		}
	}

	public static void loadResource() throws Exception {

		String propertiesDir = "/apps/catalogue/config-instance-2"  + File.separator +  "properties" ;
		propertiesDir = System.getenv("PROPS_DIR");		
		resources = readPropertiesFile(propertiesDir + File.separator + "resources.properties");
		parameters = readPropertiesFile(propertiesDir + File.separator + "parameters.properties");
		messages = readPropertiesFile(propertiesDir + File.separator + "messages.properties");
		icons = readPropertiesFile(propertiesDir + File.separator + "icons.properties");
		optParameters = readPropertiesFile(propertiesDir + File.separator + "opt-parameters.properties");
		sarParameters = readPropertiesFile(propertiesDir + File.separator + "sar-parameters.properties");
		eopParameters = readPropertiesFile(propertiesDir + File.separator + "eop-parameters.properties");
		facetMappings = readPropertiesFile(propertiesDir + File.separator + "facet-mappings.properties");
		eo2SolrMappings = readPropertiesFile(propertiesDir + File.separator + "eoParameter2Solr.properties");
		eo2SolrMappingsEnumParam = readPropertiesFile(propertiesDir + File.separator + "eoEnumParameter2Solr.properties");
		eo2SolrMappingsEnumParam4Series = readPropertiesFile(propertiesDir + File.separator + "eoEnumParameter2Solr4Series.properties");

		/*
		 * get all configured resource extensions from resourceExtensions.properties
		 */
		Properties rsExt =  readPropertiesFile(propertiesDir + File.separator + "resourceExtensions.properties");
		resourceExtensions = new HashMap<>();
		for (Object o : rsExt.keySet()) {
			String key = (String) o;
			String value = rsExt.getProperty(key);
			resourceExtensions.put(key, value);
		}
	}

	/**
	 * get all from file eoEnumParameter2Solr4Series.properties
	 *
	 * @return
	 */
	public static HashMap<String, String> getEo2SolrMappings4Series() {
		HashMap<String, String> eo2SolrFieldMappings = new HashMap<String, String>();
		Iterator<Object> iter;
		iter = eo2SolrMappingsEnumParam4Series.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			String value = eo2SolrMappingsEnumParam4Series.getProperty(key);
			eo2SolrFieldMappings.put(key,value);
		}
		return eo2SolrFieldMappings;
	}

	/**
	 * get all from file eoParameter2Solr.properties
	 *
	 * @return
	 */
	public static HashMap<String, String> getEo2SolrMappings(boolean enumParam) {
		HashMap<String, String> eo2SolrFieldMappings = new HashMap<String, String>();
		Iterator<Object> iter;

		if (enumParam) {
			iter = eo2SolrMappingsEnumParam.keySet().iterator();
			while (iter.hasNext()) {
				String key = (String)  iter.next();
				String value = eo2SolrMappingsEnumParam.getProperty(key);
				eo2SolrFieldMappings.put(key, value);
			}
		} else {
			iter = eo2SolrMappings.keySet().iterator();
			while (iter.hasNext()) {
				String key = (String)  iter.next();
				String value = eo2SolrMappings.getProperty(key);
				eo2SolrFieldMappings.put(key, value);
			}
		}

		return eo2SolrFieldMappings;
	}

	/**
	 * get property value by key from file facet-mappings.properties
	 *
	 * @param key
	 * @return
	 */
	public static String getFacetMapping(String key) {
		String value = null;
		try {
			value = facetMappings.getProperty(key).trim();

			log.debug("getFacetMapping(" + key + "=" + value + ")");
		} catch (Exception ex) {
			if (log.isDebugEnabled()) {
				ex.printStackTrace();
			}
		}
		return value;
	}

	/**
	 * Get mapping for facet fields
	 *
	 * @return mapping for facet field
	 */
	public static Properties getFacetMappings() {
		return facetMappings;
	}

	/**
	 * get property value by key from file icons.properties
	 *
	 * @param key
	 * @return
	 */
	public static String getIcon(String key) {
		String value = null;
		try {
			value = icons.getProperty(key).trim();

			log.debug("getIcons(" + key + "=" + value + ")");
		} catch (Exception ex) {
			if (log.isDebugEnabled()) {
				ex.printStackTrace();
			}
		}
		return value;
	}

	public static Properties getIconsResource() {

		return icons;
	}

	/**
	 * get property value by key from file resources.properties
	 *
	 * @param key
	 * @return
	 */
	public static String getResource(String key) {
		String value = null;
		try {
			value = resources.getProperty(key).trim();
			log.debug("getResource(" + key + "=" + value + ")");
		} catch (Exception ex) {
			if (log.isDebugEnabled()) {
				ex.printStackTrace();
			}
		}
		return value;
	}

	/**
	 * get property value by key from file parameters.properties,
	 * opt-parameters.properties, sar-parameters.properties
	 *
	 * @param key
	 * @return
	 */
	public static String getEOPParam(String key) {

		String value = null;
		try {
			value = eopParameters.getProperty(key).trim();
		} catch (Exception ex) {

		}
		if (value == null) {
			try {
				value = optParameters.getProperty(key).trim();
			} catch (Exception ex) {

			}
		}
		if (value == null) {

			try {
				value = sarParameters.getProperty(key).trim();
			} catch (Exception ex) {

			}
		}
		log.debug("getEOPParam(" + key + "=" + value + ")");

		return value;
	}
	
	/**
	 * get property value by key from file parameters.properties,
	 * opt-parameters.properties, sar-parameters.properties
	 *
	 * @param key
	 * @return
	 */
	public static String getTypeOfEOPParam(String key) {
		
		String keyType = key + ".value.type";

		String value = null;
		try {
			value = eopParameters.getProperty(keyType).trim();
		} catch (Exception ex) {

		}
		if (value == null) {
			try {
				value = optParameters.getProperty(keyType).trim();
			} catch (Exception ex) {

			}
		}
		if (value == null) {

			try {
				value = sarParameters.getProperty(keyType).trim();
			} catch (Exception ex) {

			}
		}
		log.debug("getEOPParam(" + keyType + "=" + value + ")");

		return value;
	}

	/**
	 * get property value by key from file parameters.properties
	 *
	 * @param key
	 * @return
	 */
	public static String getParam(String key) {
		String value = null;
		try {
			value = parameters.getProperty(key).trim();
			log.debug("getParam(" + key + "=" + value + ")");
		} catch (Exception ex) {

		}
		return value;
	}

	/**
	 * get property value by key from file messages.properties
	 *
	 * @param key
	 * @return
	 */
	public static String getMessage(String key) {
		String value = null;
		try {
			value = messages.getProperty(key).trim();
			log.debug("getMessage(" + key + "=" + value + ")");
		} catch (Exception ex) {

		}
		return value;
	}

	public static Map<String, String> getResourceExtensions() {
		return resourceExtensions;
	}

	public static Properties getOptParameters() {
		return optParameters;
	}

	public static Properties getSarParameters() {
		return sarParameters;
	}

	public static Properties getEopParameters() {
		return eopParameters;
	}

	private static Properties readPropertiesFile(String fileName) throws Exception {
		FileInputStream f = new FileInputStream(fileName);
		Properties properties = new Properties();
		properties.load(f);
		f.close();
		return properties;
	}
}
