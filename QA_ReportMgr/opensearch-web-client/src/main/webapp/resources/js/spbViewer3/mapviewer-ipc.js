/**
 * SpaceMapIPC -------------
 * 
 * @author Christophe Noel - Spacebel
 * @date March 2013 Inter Portlets Communication
 * 
 */

SPB_Viewer3.IPC = function (spbMap) {
    this.spbMap = spbMap;
    this.publishedTopics = new Array("map.drawn.aoi.rectangle","map.clean.geonames.box","map.drawn.aoi.polygon", "map.draw.point");
    this.subscribedTopics = new Array("map.import.features","map.remove.features",
            "map.select.feature", "map.zoom.selected.feature","map.highlight.feature", "map.unhighlight.feature", "map.unselect.feature", "map.import.wms",
            "map.import.wms.layer", "map.import.wfs", "map.tool.request");
}

SPB_Viewer3.IPC.prototype = {
    onSelectFeature: function (message) {
        this.printLog("onSelectFeature: " + message);
        this.spbMap.selectFeatures(message.data);
    },
    onZoomToSelectedFeature: function (message) {
        this.printLog("onZoomToSelectedFeature: " + message);
        this.spbMap.zoomToSelectedFeature(message.data.featureId);
    },
    onUnselectFeature: function (message) {
        this.spbMap.unactivateHighlight(message.data.layerName,
                message.data.fid);
    },
    onPublishFeatures: function (message) {        
        if (CommunicationUtil.isTarget(this.getMyInstanceId(), message)) {
            this.printLog("publish features from" + message.sender + " to " + message.targets + " - " + message.data.type);            
            this.spbMap.importFeatures(message.data.data, message.data.featureNs, message.data.featureElem);
        }        
    },
    onRemoveFeatures: function (message) {
        if (CommunicationUtil.isTarget(this.getMyInstanceId(), message)) {
            this.printLog("remove features from" + message.sender + " to " + message.targets + " - " + message.data.type);
            this.spbMap.removeFeatures();
        }        
    },
    onHighlightFeature: function (message) {
        this.printLog("onHighlightFeature: " + message);
        if (CommunicationUtil.isTarget(this.getMyInstanceId(), message)) {
            this.spbMap.highlightFeatures(message.data);
        }
    },
    onUnHighlightFeature: function (message) {
        this.printLog("onUnHighlightFeature: " + message);
        if (CommunicationUtil.isTarget(this.getMyInstanceId(), message)) {
            this.spbMap.unhighlightFeatures(message.data);
        }
    },
    onStopHighlightFeature: function (message) {
        if (CommunicationUtil.isTarget(this.getMyInstanceId(), message)) {
            this.spbMap.unactivateHighlight(message.data.layerName,
                    message.data.fid);
        }
    },
    onImportWMS: function (message) {
        this.printLog("checking import wms");
        if (CommunicationUtil.isTarget(this.getMyInstanceId(), message)) {
           this.printLog("on import WMS");
            if (message.data.layer == null) {
                PF('WMSPanelWidget').show();
                this.spbMap.WMS.loadLayersFromWMSCapabilities(message.data.url);
            } else {
                var layer = {};
                layer["url"] = message.data.url;
                layer["name"] = message.data.layer;
                layer["title"] = message.data.layer;
                if (message.data.title) {
                    layer["title"] = message.data.title;
                }
                this.spbMap.WMS.addWMSLayer(layer);
            }
        }
    },
    onToolRequest: function (message) {
        if (CommunicationUtil.isTarget('fedeoclient_webapp', message)) {
            PF('webMapPanelWidget').expand();
            this.spbMap.AOI.drawRectangleControl.activate();
        }
    },
    onImportWFS: function (message) {
        this.spbMap.WFS.loadLayersFromWFSCapabilities(message.data.url);

    },
    onComponentRestore: function (message) {
        // alert(JSON.stringify(message.targets));
        if (CommunicationUtil.isTarget(this.getMyInstanceId(), message)) {
            if (message.data.state.minimized === 'true') {
               this.printLog('minimized');
                PF('webMapPanelWidget').collapse();
            }
            this.spbMap.loadMapState(message.data.state);
        }

        // alert("reloaded");
        // addAOI(message.data.state,false);
        // if(CommunicationUtil.isTarget(this.spbMap.getInstanceId(),message))
        // {
        // this.spbMap.WMC.waitforLoaded(this,2000, 0, null, function()
        // {;});
        // }
    },
    getMyInstanceId: function () {
        return this.spbMap.getInstanceId();
    },
    getViewer: function () {
        return true;
    },
    onDiscoveryRequest: function (message) {
        CommunicationUtil.sendDiscoveryResponse(this.spbMap.getInstanceId(),
                this.publishedTopics, this.subscribedTopics);
    },
    onComponentStateRequest: function (message) {
        // alert("state requrest");
        // alert("onComponentStateRequest-"+this.spbMap.WMC.writeWMC());
        var wmcText = this.spbMap.WMC.writeWMC();
        // alert(wmcText);
        var stateId = message.data;
        var data = null;
        try {
            data = this.spbMap.AOI.createAOIState();
        }
        catch (err) {
        }
        var response = new Object();
        response.sender = this.getMyInstanceId();
        response.targets = message.sender;
        response.data = new Object();
        response.data.stateId = stateId;
        // response.data.stateData=data;
        response.data.stateData = new Object();
        response.data.stateData.wmc = wmcText;
        response.data.stateData.flayers = this.spbMap.getFeaturesLayers();
        response.data.stateData.aoi = data;
        response.data.stateData.minimized = PF('webMapPanelWidget').toggleStateHolder
                .attr('value');
        // alert(JSON.stringify(response));
        CommunicationUtil.sendStateResponse(response.data.stateId, this
                .getMyInstanceId(), response.data.stateData);
        // Liferay.fire("dashboard.state.response", response);
    },
    
     printLog: function (message){
        if( typeof console === 'undefined'){
            
        }else{
           //console.log( message );
        }        
    }
};