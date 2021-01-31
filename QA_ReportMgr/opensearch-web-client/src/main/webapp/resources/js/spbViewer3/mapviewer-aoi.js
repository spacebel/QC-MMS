/**
 * Spacemap-AOI.js defines Class AOI 
 * It defines and manages a specialized vector layer used to define the area of interest
 * It implements controls for drawing Polygons, Points and modify the drawn figures.
 * It also send an event message when an AOI is drawn (the AOI is export in GMLv3).
 * This layer should always be on top of other layers.
 * 
 * @author Christophe Noel, Spacebel 
 */

/**
 * Constructor
 */
SPB_Viewer3.AOI = function (spbMap) {
    this.spbMap = spbMap;

    this.drawRectangleControl = null;
    this.drawPolyControl = null;
    this.drawPointControl = null;
    this.aoi_items = 0;
    this.vlayer = null;
    this.totransferValue = null;
    this.movControl = null;
    this.selectControl = null;
    this.modify = null;

}

SPB_Viewer3.AOI.prototype = {
    postConstruct: function () {
        // nothing to do
    },
    drawEvent: function (evt) {
        this.printLog("draw event");
        if (spbMap.aoiFeatures.getArray() != null && spbMap.aoiFeatures.getArray().length > 0) {
            spbMap.aoiFeatures.remove(spbMap.aoiFeatures.getArray()[0]);
        }
        var cfeature = new String(evt.feature.getGeometry().clone());
        this.printLog("createOIGML");

        var data = this.createAOIGML(evt.feature);
        document.getElementById('olAOI').value = cfeature;
        //console.debug(evt);
        //this.unactivateDrawBox();
        this.sendAOIMessage(data);
        //this.sendAOIPolygonMessage(data);


    },
    activateDrawBox: function () {
        var map = this.spbMap.map;
        maxPoints = 2;
        geometryFunction = function (coordinates, geometry) {
            if (!geometry) {
                geometry = new ol.geom.Polygon(null);
            }
            var start = coordinates[0];
            var end = coordinates[1];
            geometry.setCoordinates([
                [start, [start[0], end[1]], end, [end[0], start[1]], start]
            ]);
            return geometry;
        };

        this.spbMap.draw = new ol.interaction.Draw({
            features: this.spbMap.aoiFeatures,
            type: /** @type {ol.geom.GeometryType} */ ('LineString'),
            geometryFunction: geometryFunction,
            maxPoints: maxPoints
        });
        map.addInteraction(this.spbMap.draw);
        this.spbMap.draw.on('drawend', this.drawEvent.bind(this));
        /** activate modify */
        this.modify = new ol.interaction.Modify({
            features: this.spbMap.aoiFeatures,
            // the SHIFT key must be pressed to delete vertices, so
            // that new vertices can be drawn at the same position
            // of existing vertices
            deleteCondition: function (event) {
                return ol.events.condition.shiftKeyOnly(event) &&
                        ol.events.condition.singleClick(event);
            }
        });
        map.addInteraction(this.modify);

    },
    unactivateDrawBox: function () {
        this.spbMap.map.removeInteraction(this.spbMap.draw);
    },
    /**
     * In case the feature is modified the AOI message is resent with new
     * coordinates
     */
    onFeatureModifiedEvent: function (evt) {
        var cfeature = new String(evt.feature.geometry.clone());
        // set the AOI hidden field into projection
        // use by the service
        // bug fix: redraw to force AOI display
        this.spbMap.map.getLayersByName('AOI')[0].redraw();
        var data = this.createAOIGML(evt.feature);
        this.sendAOIMessage(data);
        this.sendAOIPolygonMessage(data);
    },
    /**
     * Send the AOi in the topic viewer.publish.aoi.gml
     * 
     * @param featureGML
     *            the gml
     */

    sendAOIMessage: function (receivedData) {
        var data = new Object();
        data.aoi = receivedData.rectangle;
        data.bbox = receivedData.bbox;
        data.old = receivedData.old;

        // alert(featureGML);

        // alert(JSON.stringify(featureGML));
        CommunicationUtil.sendMessageToAll("map.drawn.aoi.rectangle",
                this.spbMap.instanceId, data);
    },
    /**
     * Send the AOi in the topic viewer.publish.aoi.gml
     * 
     * @param featureGML
     *            the gml
     */

    sendAOIPolygonMessage: function (receivedData) {
        var data = new Object();
        data.aoi = receivedData.aoi;
        data.old = receivedData.old;

        data.bbox = receivedData.bbox;

        // alert(featureGML);

        // alert(JSON.stringify(featureGML));
        CommunicationUtil.sendMessageToAll("map.drawn.aoi.polygon",
                this.spbMap.instanceId, data);
    },
    /**
     * createAOIGML Read the Polygon coordinates in olAOI hidden input field and
     * creates "SSE" GML (syntax using schema above TODO should extend this to
     * include any number of polygons
     * 
     * @return GML representation of the AOI
     */
    createAOIGML: function (singleAOIFeature) {
        this.printLog("inside");

        var gmlOptions = {
            featureType: "Feature",
            featureNS: "http://www.esa.int/xml/mapviewer/features"
        };
        var g = new ol.format.GML3(gmlOptions);
        this.printLog(g);


        var writeOptions = {
            dataProjection: ol.proj.get("EPSG:4326"),
            featureProjection: ol.proj.get("EPSG:3857")
        };
        var str = g.writeFeatures([singleAOIFeature], writeOptions);
        //this.printLog(str);
        //var bounds = singleAOIFeature.getGeometry().getBounds();

        //var boundsFeature = new OpenLayers.Feature.Vector(bounds.toGeometry());
        //var boundsGML = g.write(boundsFeature);

        /** old AOI format for demonstration purpose with WPS */
        var bbox = singleAOIFeature.clone().getGeometry().transform("EPSG:3857", "EPSG:4326").getExtent().toString();
        this.printLog("bbox");
        this.printLog(bbox);

        /** end old AOI */

        //alert(str + "\n"+boundsGML);
        var data = new Object();
        data.aoi = str;
        data.bbox = bbox;
        data.rectangle = str;
        data.old = str;
        //alert(data.old);
        //console.debug("polygon aoi:"+data.aoi);
        //console.debug("polygon bbox: "+data.bbox);
        //console.debug(bounds.toGeometry());
        //console.debug("rectangle conversion " + boundsGML );


        return data;
    },
    /**
     * Delete the AOI
     */
    removeAOI: function () {
        // first destroy the features
        if (spbMap.aoiFeatures.getArray() != null && spbMap.aoiFeatures.getArray().length > 0) {
            spbMap.aoiFeatures.remove(spbMap.aoiFeatures.getArray()[0]);
        }

        var data = new Object();
        data.aoi = "";
        data.bbox = "";
        CommunicationUtil.sendMessageToAll("map.drawn.aoi.rectangle",
                this.spbMap.instanceId, data);
    },
    
    printLog: function (message){
        if( typeof console === 'undefined'){
            
        }else{
            //console.log( message );
        }        
    }

}