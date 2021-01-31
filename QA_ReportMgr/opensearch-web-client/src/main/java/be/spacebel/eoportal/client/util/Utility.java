/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.spacebel.eoportal.client.util;

import be.spacebel.eoportal.client.business.data.Constants;
import be.spacebel.eoportal.client.business.data.SearchResultItem;
import be.spacebel.eoportal.client.parser.XMLParser;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * An utility class
 *
 * @author mng
 */
public class Utility implements Serializable {

    private static final String xmlDeclaration = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final Logger log = Logger.getLogger(Utility.class);

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATEFORMAT);
    private static final String SLASH = "/";

    public static String removeXMLDeclaration(String inputString) {
        String result = inputString;
        if (result != null) {
            if (result.startsWith(xmlDeclaration)) {
                result = result.substring(xmlDeclaration.length());
            }
            if (result.startsWith(System.getProperty("line.separator"))) {
                result = result.substring(System.getProperty("line.separator")
                        .length());
            }
            if (result.startsWith("\r\n")) {
                result = result.substring("\r\n".length());
            }
        }
        return result;
    }

    public static boolean isDateInRange(String date, String minDate, String maxDate) {
        log.debug("isDateInRange(date = " + date + ", minDate = " + minDate + ", maxDate = "
                + maxDate);
        boolean isOk = true;
        try {
            Date currentDate = dateFormat.parse(date);
            if (StringUtils.isNotEmpty(minDate) && StringUtils.isNotEmpty(maxDate)) {
                Date minInclusiveDate = dateFormat.parse(minDate);
                Date maxInclusiveDate = dateFormat.parse(maxDate);
                if ((currentDate.after(minInclusiveDate) || currentDate.equals(maxInclusiveDate))
                        && (currentDate.before(maxInclusiveDate)
                        || currentDate.equals(maxInclusiveDate))) {
                    isOk = true;
                } else {
                    isOk = false;
                }
            } else {
                if (StringUtils.isNotEmpty(minDate)) {
                    Date minInclusiveDate = dateFormat.parse(minDate);
                    if (currentDate.after(minInclusiveDate)) {
                        isOk = true;
                    } else {
                        isOk = false;
                    }
                } else {
                    if (StringUtils.isNotEmpty(maxDate)) {
                        Date maxInclusiveDate = dateFormat.parse(maxDate);
                        if (currentDate.before(maxInclusiveDate)) {
                            isOk = true;
                        } else {
                            isOk = false;
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        log.debug("isOk = " + isOk);
        return isOk;
    }

    public static String getAbsolutePath() {
        String path = "";
        try {
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            path = ctx.getRealPath("/");
        } catch (Exception e) {
            log.error(e);
        }
        return path;
    }

    public static String getPortalRequestParameter(String paramName) {
        log.debug("getPortalRequestParameter(paramName = " + paramName + ")");
        HttpServletRequest request = getOriginalRequest();
        return request.getParameter(paramName);
    }

    public static Map<String, String> getPortalRequestParameters() {
        log.debug("getPortalRequestParameters()");

        HttpServletRequest request = getOriginalRequest();

        Map<String, String> parameters = new HashMap<String, String>();

        Map params = request.getParameterMap();

        if (params != null) {
            for (Object key : params.keySet()) {
                Object objValue = params.get(key);
                String value = null;
                if (objValue != null) {
                    if (objValue instanceof String[]) {
                        for (String v : (String[]) objValue) {
                            if (StringUtils.isNotEmpty(v)) {
                                value = v;
                                break;
                            }
                        }
                    } else {
                        if (objValue instanceof String) {
                            value = (String) objValue;
                        }
                    }

                } else {

                }
                if (StringUtils.isNotEmpty(value)) {
                    log.debug(key + "=" + value);
                    parameters.put((String) key, value);
                }
            }
        }

        return parameters;
    }

    public static HttpServletRequest getOriginalRequest() {
        ExternalContext context = FacesContext.getCurrentInstance()
                .getExternalContext();
        Object requestObj = context.getRequest();
        if (requestObj instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) requestObj;
            return request;
        }
        return null;
    }

    public static String mergeGmlFeatures(String collectionGmlFeatures, String productGmlFeatures,
            String gmlEnvelope) {
        log.debug("Enter mergeGmlFeatures");
        log.debug("gmlEnvelope = " + gmlEnvelope);
        log.debug("collectionGmlFeatures = " + collectionGmlFeatures);
        log.debug("productGmlFeatures = " + productGmlFeatures);
        log.debug("=======================================================================");
        String gml = productGmlFeatures;
        try {
            XMLParser xmlParser = new XMLParser();
            Document features = xmlParser.stream2Document(gmlEnvelope);
            Document colFeaturesDoc = xmlParser.stream2Document(collectionGmlFeatures);
            Document proFeaturesDoc = xmlParser.stream2Document(productGmlFeatures);
            /*
             * insert collection features to gmlEnvelope document
             */
            NodeList colFeaturesList = colFeaturesDoc.getElementsByTagNameNS(
                    "http://www.opengis.net/gml", "featureMember");
            log.debug("Number of collection features: " + colFeaturesList.getLength());
            for (int i = 0; i < colFeaturesList.getLength(); i++) {
                features.getDocumentElement().appendChild(
                        features.importNode(colFeaturesList.item(i), true));
            }
            log.debug("Feature DOM after insert collection features ======================== ");
            log.debug(xmlParser.serializeDOM(features));
            /*
             * insert product features to gmlEnvelope document
             */
            NodeList proFeaturesList = proFeaturesDoc.getElementsByTagNameNS(
                    "http://www.opengis.net/gml", "featureMember");
            log.debug("Number of product features: " + proFeaturesList.getLength());

            for (int i = 0; i < proFeaturesList.getLength(); i++) {
                features.getDocumentElement().appendChild(
                        features.importNode(proFeaturesList.item(i), true));
            }
            gml = xmlParser.serializeDOM(features);
            log.debug("mergeGmlFeatures.gml = " + gml);
        } catch (Exception e) {
            log.debug(e);
        }

        return gml;
    }

    public static String hoursToDays(String hoursStr) {
        log.debug("hoursToDays(" + hoursStr + ")");
        double doubleHours = Double.parseDouble(hoursStr);
        if (doubleHours >= 24) {
            StringBuilder sb = new StringBuilder();
            long hours = (long) doubleHours;
            long days = hours / 24;
            long months = 0;
            long years = 0;

            if (days >= 30) {
                months = days / 30;
                days = days % 30;
                if (months >= 12) {
                    years = months / 12;
                    months = months % 12;
                }
            }

            if (years == 1) {
                sb.append(years).append(" year ");
            }
            if (years > 1) {
                sb.append(years).append(" years ");
            }

            if (months == 1) {
                sb.append(months).append(" month ");
            }
            if (months > 1) {
                sb.append(months).append(" months ");
            }

            if (days == 1) {
                sb.append(days).append(" day ");
            }
            if (days > 1) {
                sb.append(days).append(" days ");
            }
            return sb.toString().trim();
        } else {
            String hourText = (doubleHours > 1) ? " hours" : " hour";
            return (hoursStr + hourText);
        }
    }

    public static String getCenter(String lineStr) {
        log.debug("getCenter(" + lineStr + ")");
        List<Double> coordinates = strToDoubles(lineStr);

        if (coordinates.size() >= 4) {
            double lat1 = coordinates.get(0);
            double lon1 = coordinates.get(1);
            double lat2 = coordinates.get(coordinates.size() - 2);
            double lon2 = coordinates.get(coordinates.size() - 1);
            log.debug("[lat1,lon1] =  [" + lat1 + ", " + lon1 + "]; " + "[lat2,lon2] =  [" + lat2 + ", " + lon2 + "]");

            double dLon = Math.toRadians(lon2 - lon1);

            //convert to radians
            lat1 = Math.toRadians(lat1);
            lat2 = Math.toRadians(lat2);
            lon1 = Math.toRadians(lon1);

            double Bx = Math.cos(lat2) * Math.cos(dLon);
            double By = Math.cos(lat2) * Math.sin(dLon);
            double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
            double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

            return Math.toDegrees(lat3) + "," + Math.toDegrees(lon3);
        } else {
            log.debug("Line " + lineStr + " is invalid.");
            return "";
        }
    }

    public static String getPolygonBounds(String polygonStr) {
        log.debug("getPolygonBounds(" + polygonStr + ")");

        String bbox = "";
        List<Double> coordinates = strToDoubles(polygonStr);
        if (coordinates.size() % 2 == 0) {
            List<Double> xpoints = new ArrayList<Double>();
            List<Double> ypoints = new ArrayList<Double>();

            for (int i = 0; i < (coordinates.size() - 2); i += 2) {
                xpoints.add(coordinates.get(i));
                ypoints.add(coordinates.get(i + 1));
            }

            if (xpoints.size() > 1 && ypoints.size() > 1) {
                double boundsMinX = xpoints.get(0);
                double boundsMaxX = xpoints.get(0);
                double boundsMinY = ypoints.get(0);
                double boundsMaxY = ypoints.get(0);
                for (int i = 1; i < xpoints.size(); i++) {
                    double x = xpoints.get(i);
                    double y = ypoints.get(i);
                    if (x < boundsMinX) {
                        boundsMinX = x;
                    }
                    if (x > boundsMaxX) {
                        boundsMaxX = x;
                    }
                    if (y < boundsMinY) {
                        boundsMinY = y;
                    }
                    if (y > boundsMaxY) {
                        boundsMaxY = y;
                    }
                }
                bbox = boundsMinX + "," + boundsMinY + "," + boundsMaxX + "," + boundsMaxY;
            }
        } else {
            log.debug("Polygon " + polygonStr + " is invalid.");
        }
        return bbox;
    }

    private static List<Double> strToDoubles(String str) {
        String[] strArr = str.split(" ");
        List<Double> doubleList = new ArrayList<Double>();
        if (strArr != null && strArr.length >= 2) {
            for (String xy : strArr) {
                if (StringUtils.isNotEmpty(xy)) {
                    xy = StringUtils.trimToEmpty(xy);
                    if (StringUtils.isNotEmpty(xy)) {
                        try {
                            doubleList.add(Double.parseDouble(xy));
                        } catch (NumberFormatException e) {

                        }
                    }
                }
            }
        }
        return doubleList;
    }

    public static String reverseCoordinates(String coordinatesStr) {
        String[] coordinates = coordinatesStr.split(" ");
        String str = "";
        if ((coordinates.length % 2) == 0) {
            for (int i = 0; i < coordinates.length; i += 2) {
                str += coordinates[i + 1] + " " + coordinates[i] + " ";
            }
        }
        return StringUtils.trimToNull(str);
    }

    public static String validateCoordinates(String coordinatesStr) {
        log.debug("validateCoordinates(" + coordinatesStr + ")");
        List<Double> coordinates = strToDoubles(coordinatesStr);
        StringBuilder sb = new StringBuilder();
        log.debug("Number of points: " + coordinates.size());
        if (coordinates.size() % 2 == 0) {
            for (int i = 0; i < coordinates.size(); i++) {
                double latlon = coordinates.get(i);
                if (i == 0 || (i % 2 == 0)) {
                    /*
                     in case of lat
                     */
                    if (latlon > 85.06) {
                        latlon = 85.06;
                    }

                    if (latlon < -85.06) {
                        latlon = -85.06;
                    }
                }

                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(latlon);
            }
        } else {
            log.debug("Number of points of should be even.");
        }
        return sb.toString();
    }

    public static String createBBoxFromPointAndDistance(double longitude, double latitude, double distance) {
        log.debug("createBBoxFromPointAndDistance(longitude=" + longitude + ", latitude = " + latitude + ", distance = " + distance + ")");
        int EARTH_RADIUS = 6371;
        int DISTANCE_BETWEEN_TWO_LAT = 111;
        int KM_TO_M = 1000;

        distance = distance / KM_TO_M;

        double lat_min = latitude - (distance) / DISTANCE_BETWEEN_TWO_LAT;
        double lat_max = latitude + (distance) / DISTANCE_BETWEEN_TWO_LAT;

        double scale = EARTH_RADIUS * 2 * Math.PI / 360 * Math.cos(latitude * Math.PI / 180);
        double lon_min;
        double lon_max;
        if (scale != 0) {
            lon_min = longitude - (distance) / scale;
            lon_max = longitude + (distance) / scale;
        } else {
            lon_min = longitude;
            lon_max = longitude;
        }

        DecimalFormat df = new DecimalFormat("#.######");
        String bbox = df.format(lon_min) + "," + df.format(lat_min) + "," + df.format(lon_max) + "," + df.format(lat_max);

        log.debug("bbox=" + bbox);
        return bbox;
    }

    public static boolean matchParameter(String paramNS, String paramToken, String comparedNS, String comparedToken) {
        log.debug("matchParameter(paramNS = " + paramNS + ", paramToken = " + paramToken + ", comparedNS = " + comparedNS + ", comparedToken = " + comparedToken + ")");
        if (StringUtils.isEmpty(paramNS) || StringUtils.isEmpty(paramToken) || StringUtils.isEmpty(comparedNS) || StringUtils.isEmpty(comparedToken)) {
            return false;
        }

        if (paramNS.equalsIgnoreCase(comparedNS)) {
            String pValue = paramToken;
            if (StringUtils.contains(pValue, ":")) {
                pValue = StringUtils.substringAfterLast(pValue, ":");
            }
            log.debug("pValue = " + pValue);
            if (comparedToken.equalsIgnoreCase(pValue)) {
                log.debug("matchParameter = true");
                return true;
            }
        }
        return false;
    }

    public static String getStaticImageUrl(SearchResultItem selectedItem, String mapUrl, String mapKey) {
        String imageUrl = "";
        //String style = "color:0x0000ff%7Cwidth:3%7Cfill:0xffff00%7C";
        String style = "color:0x00ff00%7Cwidth:2%7C";
        String type = "&type=map&imagetype=jpeg";

        String polygonStr = getPropertyValue(selectedItem, "georssPolygon1");
        if (StringUtils.isNotEmpty(polygonStr)) {
            String bbox = getPropertyValue(selectedItem, "georssBox");
            if (StringUtils.isNotEmpty(bbox)) {
                bbox = validateFeature(reverseCoordinates(bbox));
            } else {
                bbox = getPolygonBounds(reverseCoordinates(polygonStr));
            }

            if (StringUtils.isNotEmpty(bbox)) {
                StringBuilder url = new StringBuilder(mapUrl);
                url.append("?key=").append(mapKey);
                url.append("&size=").append(Constants.THUMBNAIL_SIZE).append(type);
                url.append("&bestfit=").append(bbox);
                url.append("&polygon=").append(style).append(validateFeature(reverseCoordinates(polygonStr)));
                imageUrl = url.toString();
            }
        }

        if (StringUtils.isEmpty(imageUrl)) {
            String line = getPropertyValue(selectedItem, "georssLine");
            if (StringUtils.isNotEmpty(line)) {
                //String center = Utility.getCenter(line);
                String bbox = getPropertyValue(selectedItem, "georssBox");
                if (StringUtils.isNotEmpty(bbox)) {
                    bbox = validateFeature(reverseCoordinates(bbox));
                } else {
                    bbox = validateFeature(reverseCoordinates(line));
                }
                if (StringUtils.isNotEmpty(bbox)) {
                    StringBuilder url = new StringBuilder(mapUrl);
                    url.append("?key=").append(mapKey);
                    url.append("&size=").append(Constants.THUMBNAIL_SIZE).append(type);
                    //url.append("&center=").append(center);
                    //url.append("&zoom=").append("3");
                    url.append("&bestfit=").append(bbox);
                    url.append("&polyline=").append(style).append(validateFeature(reverseCoordinates(line)));
                    imageUrl = url.toString();
                }
            }
        }

        if (StringUtils.isEmpty(imageUrl)) {
            String point = getPropertyValue(selectedItem, "georssPoint");
            if (StringUtils.isNotEmpty(point)) {
                StringBuilder url = new StringBuilder(mapUrl);
                url.append("?key=").append(mapKey);
                url.append("&size=").append(Constants.THUMBNAIL_SIZE).append(type);
                point = validateFeature(reverseCoordinates(point));
                url.append("&center=").append(point);
                url.append("&zoom=").append("3");
                url.append("&polyline=").append(style).append(point);
                imageUrl = url.toString();
            }
        }

        if (StringUtils.isEmpty(imageUrl)) {
            String bbox = getPropertyValue(selectedItem, "georssBox");
            if (StringUtils.isNotEmpty(bbox)) {
                StringBuilder url = new StringBuilder(mapUrl);
                url.append("?key=").append(mapKey);
                url.append("&size=").append(Constants.THUMBNAIL_SIZE).append(type);
                bbox = validateFeature(reverseCoordinates(bbox));
                url.append("&bestfit=").append(bbox);
                url.append("&polygon=").append(style).append(bbox);
                imageUrl = url.toString();
            }
        }
        return imageUrl;
    }

    public static String getThumbnailDivAttributes(String id, String style) {
        return " id=\"" + id + "\"  style=\"" + style + "\"";
    }

    private static String validateFeature(String strCoordinates) {
        String[] strArr = strCoordinates.split(" ");
        if (strArr != null && strArr.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String str : strArr) {
                String s = StringUtils.trimToEmpty(str);
                if (StringUtils.isNotEmpty(s)) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(s);
                }
            }
            return sb.toString();
        } else {
            return strCoordinates;
        }

    }

    public static String getPropertyValue(SearchResultItem item, String proName) {
        String value = "";

        if (item != null && item.getProperties() != null && item.getProperties().get(proName) != null) {
            value = item.getProperties().get(proName).getValue();
        }
        return value;
    }

    public static String validateUrl(String url) {
        if (StringUtils.isNotEmpty(url)) {
            url = url.replace(" ", "%20");
            /*
             validate the url
             */
            try {
                URI uri = new URI(url);
            } catch (URISyntaxException e) {
                log.debug("The following image Url is not valid: " + url);
                url = "";
            }
        }
        return url;
    }

    public static String escapeSingleQuote(String url) {
        url = validateUrl(url);
        if (StringUtils.isNotEmpty(url)) {
            url = url.replaceAll("'", "\\\\'");
        }
        return url;
    }

    public static String removeLastSlashs(String path) {
        if (StringUtils.isNotEmpty(path)) {
            while (true) {
                if (path.endsWith(SLASH)) {
                    path = path.substring(0, (path.length() - 1));
                } else {
                    break;
                }
            }
        }
        return path;
    }

    public static String removeFirstSlashs(String path) {
        if (StringUtils.isNotEmpty(path)) {
            while (true) {
                if (path.startsWith(SLASH)) {
                    path = path.substring(1);
                } else {
                    break;
                }
            }
        }
        return path;
    }

    public static String trimSlashs(String path) {
        path = removeFirstSlashs(path);
        path = removeLastSlashs(path);
        return path;
    }

//    public static void toHtml(int level, List<KeyValueProperty> items, StringBuilder sb, int startIndent, boolean collapse) {
//        if (collapse && items.size() > 1) {
//            sb.append("<div>");
//            for (int i = 0; i < items.size(); i++) {
//                KeyValueProperty item = items.get(i);
//
//                if (item.getChilds().size() > 0) {
//                    sb.append("<div>");
//                }
//
//                String label = toLabel(item.getKey());
//                int margin = (level * 20) + startIndent;
//                sb.append("<span class=\"details-text-label\" style=\"margin-left:").append(margin).append("px;\">")
//                        .append(label)
//                        .append(": </span>");
//                if (item.getChilds().isEmpty()) {
//                    sb.append(item.getValue());
//                    if (i == 0) {
//                        sb.append("<i class=\"fa fa-fw fa-plus-square-o\" \n"
//                                + "                   onclick=\"fedeoclient_webapp_slideToggle(this)\" \n"
//                                + "                   title=\"View more details\"/>");
//                        sb.append("<div class=\"collapse-expand-contents\"").append(">");
//                    }
//                } else {
//                    sb.append("<i class=\"fa fa-fw fa-plus-square-o\" \n"
//                            + "                   onclick=\"fedeoclient_webapp_slideToggle(this)\" \n"
//                            + "                   title=\"View more details\"/>");
//                    sb.append("<div class=\"collapse-expand-contents\"").append(">");
//                    toHtml((level + 1), item.getChilds(), sb, startIndent, true);
//                    sb.append("</div>");
//                    sb.append("</div>");
//                }
//                if (i >= (items.size() - 1)) {
//                    sb.append("</div>");
//                } else {
//                    if (i > 0) {
//                        sb.append("<br/>");
//                    }
//                }
//            }
//            sb.append("</div>");
//        } else {
//            for (KeyValueProperty item : items) {
//                if (sb.length() > 0 && item.getChilds().isEmpty()) {
//                    sb.append("<br/>");
//                }
//
//                String label = toLabel(item.getKey());
//
//                if (item.getChilds().size() > 0) {
//                    sb.append("<div>");
//                }
//                int margin = (level * 20) + startIndent;
//                sb.append("<span class=\"details-text-label\" style=\"margin-left:").append(margin).append("px;\">")
//                        .append(label)
//                        .append(": </span>");
//
//                if (item.getChilds().isEmpty()) {
//                    sb.append(item.getValue());
//                } else {
//                    sb.append("<i class=\"fa fa-fw fa-plus-square-o\" \n"
//                            + "                   onclick=\"fedeoclient_webapp_slideToggle(this)\" \n"
//                            + "                   title=\"View more details\"/>");
//                    sb.append("<div class=\"collapse-expand-contents\"").append(">");
//                    toHtml((level + 1), item.getChilds(), sb, startIndent, true);
//                    sb.append("</div>");
//                    sb.append("</div>");
//                }
//            }
//        }
//    }
//
//    private static String toLabel(String key) {
//        return StringUtils.capitalize(key).replaceAll("_", " ");
//    }
    public static String toLabel(String str) {
        if (StringUtils.isNotEmpty(str)) {
            String[] strArray = StringUtils.splitByCharacterTypeCamelCase(str);
            if (strArray != null && strArray.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < strArray.length; i++) {
                    String s;
                    if (i == 0) {
                        s = StringUtils.capitalize(strArray[i]);
                    } else {
                        s = StringUtils.uncapitalize(strArray[i]);
                    }
                    sb.append(s);
                    if (i < (strArray.length - 1)) {
                        sb.append(" ");
                    }
                }
                return sb.toString().replaceAll("_", " ");
            }
            return StringUtils.capitalize(str).replaceAll("_", " ");
        }
        return StringUtils.EMPTY;
    }

    public static String addLink(String value) {
        if (StringUtils.isNotEmpty(value)) {
            if (StringUtils.startsWithIgnoreCase(value, "http")
                    || StringUtils.startsWithIgnoreCase(value, "https")) {
                return "<a href=\"" + value + "\" target=\"_blank\" class=\"href-icon\">" + value + "</a>";
            }
        }
        return value;
    }
}
