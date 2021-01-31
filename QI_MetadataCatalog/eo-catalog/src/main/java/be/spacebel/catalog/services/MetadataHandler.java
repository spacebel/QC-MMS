package be.spacebel.catalog.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import be.spacebel.catalog.utils.Constants;
import be.spacebel.catalog.utils.xml.XPathNamespaceContext;
import be.spacebel.catalog.utils.xml.XpathUtils;

/**
 * This class provide functions to process series metadata
 *
 * @author tth
 *
 */
@Service
public class MetadataHandler {

	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(MetadataHandler.class);

	private XMLService xmlService;

	@Autowired
	public MetadataHandler(XMLService xmlService){
		this.xmlService = xmlService;
	}

	/**
	 * Read the string that represents series metadata from the input stream
	 *
	 * @param is
	 *            : input stream
	 * @return string that contains the series metadata
	 */
	public String getMetataAsStringFromInputStream(InputStream is) throws Exception {
		StringBuilder sb = new StringBuilder();
		String line;
		try(InputStreamReader isr = new InputStreamReader(is); BufferedReader br = new BufferedReader(isr) ) {

			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		}
		return sb.toString();
	}

	/**
	 * Save the content of the input stream to a file
	 *
	 * @param uploadedInputStream
	 *            : input stream contains the content to be saved
	 * @param serverLocation
	 *            : file location
	 */
	public void saveMetadata2File(InputStream uploadedInputStream, String serverLocation) throws Exception {
		try(OutputStream outpuStream = new FileOutputStream(new File(serverLocation))) {
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				outpuStream.write(bytes, 0, read);
			}
			outpuStream.flush();
		}
	}

	/**
	 * Validate if the inputed metadata is valid
	 *
	 * @param metadata
	 *            metadata to be validated
	 * @param metadataType
	 *            metadata type
	 * @return
	 */
	public boolean isValid(String metadata, String metadataType) {
		try {
			boolean isValid = false;

			String hierarchyLevel = getHierarchyLevel(metadata, "/gmi:MI_Metadata/gmd:hierarchyLevel/gmd:MD_ScopeCode");
			if (hierarchyLevel == null) {
				hierarchyLevel = getHierarchyLevel(metadata, "/gmd:MD_Metadata/gmd:hierarchyLevel/gmd:MD_ScopeCode");
			}
			if (hierarchyLevel != null) {
				if (hierarchyLevel.equals("series")) {
					isValid = true;
				}

			}

			return isValid;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Get HierarchyLevel of ISO metadata
	 *
	 * @param metadata
	 *            : ISO metadata
	 * @param hierarchyLevelXpath
	 *            : Xpath to HierarchyLevel element
	 * @return value of HierarchyLevel element
	 */
	private String getHierarchyLevel(String metadata, String hierarchyLevelXpath) {
		String hierarchyLevel = null;
		Document xmlDoc = xmlService.stream2Document(metadata);

		hierarchyLevel = XpathUtils.getNodeValueByXPath(xmlDoc, hierarchyLevelXpath);

		if (hierarchyLevel == null || StringUtils.isEmpty(hierarchyLevel)) {

			hierarchyLevel = XpathUtils.getNodeAttributeValueByXPath(xmlDoc, hierarchyLevelXpath,
					"codeListValue");
		}

		return hierarchyLevel;
	}

	}
