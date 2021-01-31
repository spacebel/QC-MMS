package be.spacebel.catalog.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import be.spacebel.catalog.utils.xml.XpathUtils;

public class GeoUtils {

	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(GeoUtils.class);

	public static String createBBoxFromPointAndDistance(double longitude, double latitude, double distance) {

		String bbox = null;
		double lat_min = latitude - (distance) / Constants.DISTANCE_BETWEEN_TWO_LAT;
		double lat_max = latitude + (distance) / Constants.DISTANCE_BETWEEN_TWO_LAT;

		double scale = Constants.EARTH_RADIUS * 2 * Math.PI / 360 * Math.cos(latitude * Math.PI / 180);
		double lon_min;
		double lon_max;
		if (scale != 0) {
			lon_min = longitude - (distance) / scale;
			lon_max = longitude + (distance) / scale;
		} else {
			lon_min = longitude;
			lon_max = longitude;
		}

		NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
		DecimalFormat df = (DecimalFormat)nf;
		df.applyPattern("#.######");
		bbox = df.format(lon_min) + "," + df.format(lat_min) + "," + df.format(lon_max) + "," + df.format(lat_max);

		return bbox;
	}

	public static double[] getLocation(String value) {

		ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();

		searchCriteria.setQ(value);
		searchCriteria.setMaxRows(1);
		try {
			ToponymSearchResult searchResult = WebService.search(searchCriteria);

			double longitude = -999d;
			double latitude = -999d;
			searchResult.getTotalResultsCount();
			if (searchResult.getTotalResultsCount() > 0) {
				for (Toponym toponym : searchResult.getToponyms()) {
					longitude = toponym.getLongitude();
					latitude = toponym.getLatitude();
					break;
				}
			}
			double[] result = { longitude, latitude };
			return result;
		} catch (Exception e) {
			log.error("", e);
			return null;
		}

	}

	public static String computeBboxFromGeoName(String geonames, String radius) {

		double[] location;
		location = GeoUtils.getLocation(geonames);
		double distance = Double.parseDouble(radius) / Constants.KM_TO_M;
		String bbox = GeoUtils.createBBoxFromPointAndDistance(location[0], location[1], distance);

		return bbox;
	}


	public static String getOMCoordinates(Document xmlDoc) {

		/* get GML */
		String omCoordinates = XpathUtils.getNodeValueByXPath(xmlDoc,

				"//om:featureOfInterest//*[local-name() = 'multiExtentOf']//*[local-name() = 'Polygon']//*[local-name() = 'LinearRing']/*[local-name() = 'posList']");

		if (omCoordinates == null) {
			omCoordinates = StringUtils.EMPTY;
			NodeList posList = XpathUtils.getNodesByXPath(xmlDoc,
					"//om:featureOfInterest//*[local-name() = 'multiExtentOf']//*[local-name() = 'Polygon']//*[local-name() = 'LinearRing']/*[local-name() = 'pos']");
			if (posList.getLength() > 0) {
				for (int i = 0; i < posList.getLength(); i++) {
					Node pos = posList.item(i);
					omCoordinates = omCoordinates + pos.getTextContent() + " ";
				}
			} else {
				omCoordinates = null;
			}

		}

		if (omCoordinates == null) {
			omCoordinates = XpathUtils.getNodeValueByXPath(xmlDoc,

					"//om:featureOfInterest//*[local-name() = 'nominalTrack']//*[local-name() = 'MultiCurve']//*[local-name() = 'curveMember']//*[local-name() = 'posList']");

		}
		return omCoordinates;
	}

	public static String oem2wkt(String coordinates) {

		String[] coorArr = parseCoordinates(coordinates);
		String wkt = StringUtils.EMPTY;

		boolean isOnePoint = true;

		if (coorArr.length % 2 == 0) {

			for (int i = 0; i < coorArr.length / 2; i++) {
				wkt += coorArr[2 * i + 1] + " " + coorArr[2 * i] + ",";
				if (!coorArr[1].equals(coorArr[2 * i + 1]) || !coorArr[0].equals(coorArr[2 * i])) {
					isOnePoint = false;
				}
			}

			wkt = wkt.substring(0, wkt.length() - 1);
			if (isOnePoint) {
				wkt = "POINT(" + coorArr[1] + " " + coorArr[0] + ")";
			} else {
				if (coorArr.length >= 8) {
					if (coorArr[0].equals(coorArr[coorArr.length - 2])
							&& coorArr[1].equals(coorArr[coorArr.length - 1])) {
						wkt = "POLYGON((" + wkt + "))";
					} else {
						wkt = "LINESTRING(" + wkt + ")";
					}
				} else if (coorArr.length == 6) {
					wkt = "LINESTRING(" + wkt + ")";
				} else if (coorArr.length == 4) {
					if (coorArr[0].equals(coorArr[2]) && coorArr[1].equals(coorArr[3])) {
						wkt = "POINT(" + coorArr[1] + " " + coorArr[0] + ")";
					} else {
						wkt = "LINESTRING(" + wkt + ")";
					}
				} else if (coorArr.length == 2) {
					wkt = "POINT(" + coorArr[1] + " " + coorArr[0] + ")";
				}
			}
		}
		return wkt;
	}

	private static String[] parseCoordinates(String coordinates) {

		coordinates = coordinates.trim();
		ArrayList<String> coordinatesList = new ArrayList<String>();

		while (!StringUtils.isEmpty(coordinates)) {
			String s = StringUtils.substringBefore(coordinates, " ");
			// System.out.println("s:" + s);
			String tmp = StringUtils.substringAfter(coordinates, " ");
			coordinates = tmp.trim();
			coordinatesList.add(s.trim());
		}

		String[] coordinatesArray = new String[coordinatesList.size()];
		coordinatesArray = coordinatesList.toArray(coordinatesArray);

		return coordinatesArray;

	}
}
