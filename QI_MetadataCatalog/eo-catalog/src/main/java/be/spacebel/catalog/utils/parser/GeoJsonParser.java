package be.spacebel.catalog.utils.parser;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class GeoJsonParser {
	public static String getGeoJSONStringProperty(JSONObject object, String properties) {

		String value = StringUtils.EMPTY;
		if (object != null) {
			if (object.has(properties)) {
				value = (String) object.get(properties);
			}
		}
		return value;

	}

	public static JSONObject getGeoJSONObjectProperty(JSONObject object, String properties) {

		JSONObject value = null;
		if (object != null) {
			if (object.has(properties)) {
				value = (JSONObject) object.get(properties);
			}
		}
		return value;

	}

	public static JSONArray getGeoJSONArrayProperty(JSONObject object, String properties) {

		JSONArray value = null;
		if (object != null) {
			if (object.has(properties)) {
				value = (JSONArray) object.get(properties);
			}
		}
		return value;

	}

	public static int getGeoJSONIntProperty(JSONObject object, String properties) {
		int value = -1;
		if (object != null) {
			if (object.has(properties)) {
				value = (int) object.get(properties);
			}
		}
		return value;
	}

	public static boolean getGeoJSONBooleanProperty(JSONObject object, String properties) {
		boolean value = false;
		if (object != null) {
			if (object.has(properties)) {
				value = (boolean) object.get(properties);
			}
		}
		return value;
	}

	public static double getGeoJSONDoubleProperty(JSONObject object, String properties) {
		double value = -1.0;
		if (object != null) {
			if (object.has(properties)) {
				value = Double.parseDouble(String.valueOf(object.get(properties)));
			}
		}
		return value;
	}
	
	public static Object getGeoObjectProperty(JSONObject object, String properties) {
		
		return object.get(properties);
		
	}
}
