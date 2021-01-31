package be.spacebel.catalog.models;

import org.w3c.dom.Document;

/**
 * This class is a simple wrapper around the two elements of the response of a catalog query:
 * - the XML element
 * - the number of matches
 * 
 * @author Benoit Denis
 *
 */
public class CatalogResponse {
	
	private final Document responseDoc;
	private final String geoJsonResponse;
	private final int numberOfMatch;
	
	public CatalogResponse(Document responseDoc, String geoJsonResponse, int numberOfMatch) {
		super();
		this.responseDoc = responseDoc;
		this.numberOfMatch = numberOfMatch;
		this.geoJsonResponse = geoJsonResponse;
	}
	
	public Document getResponseDoc() {
		return responseDoc;
	}
	public int getNumberOfMatch() {
		return numberOfMatch;
	}

	public String getGeoJsonResponse() {
		return geoJsonResponse;
	}
	
	

}
