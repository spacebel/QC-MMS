var SPBMapConfiguration = {
    transformation: {
        wgs84: 'EPSG:4326',
        webMercator: 'EPSG:3857'
    },
    center: [13.707047, 51.060086],
    extent: [-180, -85.06, 180, 85.06],
    zoom: 4,
    minZoom: 2,
    maxZoom: 20,
    styles: {
        normal: {
            fill: null,
            stroke: new ol.style.Stroke({
                color: [0, 0, 255, .4],
                width: 1
            }),
            zIndex: 1000
        },
        highlight: {
            fill: null,
            stroke: new ol.style.Stroke({
                color: [255, 255, 0, .6],
                width: 1
            }),
            zIndex: 3000
        },
        selection: {
            fill: null,
            stroke: new ol.style.Stroke({
                color: [255, 255, 0, 1],
                width: 3
            }),
            zIndex: 2000
        },
        dragBox: {
            fill: new ol.style.Fill({
                color: [255, 255, 255, .4]
            }),
            stroke: new ol.style.Stroke({
                color: [0, 0, 255, 1]
            })
        },
        aoi: {
            fill: new ol.style.Fill({
                color: [220, 142, 2, .5]
            }),
            stroke: new ol.style.Stroke({
                color: [220, 142, 2, 1]
            })
        }
    }
};

var SPB_Viewer3 = {};
/**
 * Spacebel Web Map Viewer Class constructor receives map viewer instance Id
 * main method is loadWMC(request)
 * 
 * @param instanceId
 * @returns
 */
SPB_Viewer3.MapViewer = function (instanceId) {

    /* Map Viewer instance Id */
    this.instanceId = instanceId;
    this.layerType = "builtIn";
    this.customWms = null;
    this.customWmsThumbnailLayer = null;
    this.layers = null;
    this.map = new Object();   
    this.vector = null;
    this.IPC = new SPB_Viewer3.IPC(this);
    this.AOI = new SPB_Viewer3.AOI(this);    
    this.footprintLayer = null;
    this.aoi = null;
    this.aoiFeatures = new ol.Collection();
    this.highlightFeatureArray = [];
    this.currentHighlightFeature = null;
    this.normalStyle = new ol.style.Style(SPBMapConfiguration.styles.normal);
    this.highlightStyle = new ol.style.Style(SPBMapConfiguration.styles.highlight);
    this.selectedStyle = new ol.style.Style(SPBMapConfiguration.styles.selection);
    this.dragBox = new ol.interaction.DragBox({
        condition: ol.events.condition.always,
        style: new ol.style.Style(SPBMapConfiguration.styles.dragBox)
    });
    this.geonamesBboxLayer = new ol.layer.Vector({
        source: new ol.source.Vector({
            wrapX: false
        }),
        style: new ol.style.Style(SPBMapConfiguration.styles.dragBox)
    });
    this.pointNormalStyle = new ol.style.Style({
        image: new ol.style.Circle({
            radius: 1,
            snapToPixel: false,
            fill: new ol.style.Fill({color: 'Blue'})
        }),
        zIndex: 1000
    });
    this.pointHighlightStyle = new ol.style.Style({
        image: new ol.style.Circle({
            radius: 1,
            snapToPixel: false,
            fill: new ol.style.Fill({color: 'Yellow'})
        }),
        zIndex: 3000
    });
    this.pointSelectedStyle = new ol.style.Style({
        image: new ol.style.Circle({
            radius: 3,
            snapToPixel: false,
            fill: new ol.style.Fill({color: 'Yellow'})
        }),
        zIndex: 2000
    });
};

SPB_Viewer3.MapViewer.prototype = {
    /* getInstanceId return the component instance id */
    getInstanceId: function () {
        return this.instanceId;
    },
    init: function () {
        var mapViewerObj = this;

        //console.log(customWmsInfo);

        var aoiSource = new ol.source.Vector({
            features: this.aoiFeatures
        });
        this.aoi = new ol.layer.Vector({
            source: aoiSource,
            style: new ol.style.Style(SPBMapConfiguration.styles.aoi)
        });
        this.footprintLayer = new ol.layer.Vector({
            source: new ol.source.Vector({
                wrapX: false
            })
                    /*,
                     style: this.normalStyle*/
        });        

        var osmLayer = new ol.layer.Tile({
            title: 'OpenStreetMap',
            type: 'base',
            visible: true,
            source: new ol.source.OSM({
                wrapX: false
            })
        });

        this.layers = [
            new ol.layer.Group({
                'title': 'Map layers',
                layers: [osmLayer]
            }),
            this.footprintLayer,
            this.geonamesBboxLayer,
            this.aoi];

        var mousePositionControl = new ol.control.MousePosition({
            coordinateFormat: ol.coordinate.createStringXY(4),
            projection: SPBMapConfiguration.transformation.wgs84,
            target: document.getElementById('mousePosition' + this.instanceId),
            undefinedHTML: '&nbsp;'
        });

        this.map = new ol.Map({
            /*
             controls: ol.control.defaults().extend(
             [new ol.control.FullScreen()]),
             */
            controls: ol.control.defaults().extend([
                mousePositionControl
            ]),
            interactions: ol.interaction.defaults().extend(
                    [new ol.interaction.DragRotateAndZoom()]),
            target: "spacemap" + this.instanceId,
            layers: this.layers,
            view: new ol.View({
                /*projection: 'EPSG:4326',*/
                /*center: ol.proj.fromLonLat([10.41, 40.82]),*/
                center: ol.proj.transform(SPBMapConfiguration.center, SPBMapConfiguration.transformation.wgs84, SPBMapConfiguration.transformation.webMercator),
                minZoom: SPBMapConfiguration.minZoom,
                maxZoom: SPBMapConfiguration.maxZoom,
                zoom: SPBMapConfiguration.zoom,
                extent: ol.proj.transformExtent(SPBMapConfiguration.extent, SPBMapConfiguration.transformation.wgs84, SPBMapConfiguration.transformation.webMercator)
            })
        });

        /*var extent = new ol.interaction.Extent({
         condition: ol.events.condition.platformModifierKeyOnly
         });
         this.map.addInteraction(extent);
         */

        var layerSwitcher = new ol.control.LayerSwitcher({
            tipLabel: 'Légende' // Optional label for button
        });
        this.map.addControl(layerSwitcher);
        /**
         * Deprecated cesium terrain, see https://groups.google.com/forum/#!topic/cesium-dev/grGcRfRfOwA 
         var terrainProvider = new Cesium.CesiumTerrainProvider({
         url: '//assets.agi.com/stk-terrain/world'
         });
         scene.terrainProvider = terrainProvider;
         */
        this.initAOI();

        /*
         this.map.on("click", function (evt) {
         mapViewerObj.cleanGeonamesBox();
         mapViewerObj.cleanAOI();
         });
         */
        this.map.on("click", function (evt) {
            var pixel = evt.pixel;
            mapViewerObj.printLog(pixel);
            mapViewerObj.onClickFeature(pixel);

        });

        /*
         var mapObj = this.map;
         
         this.map.on('pointermove', function (evt) {
         if (evt.dragging) {
         return;
         }
         if(evt.originalEvent){
         console.log("originalEvent");
         var pixel = mapObj.getEventPixel(evt.originalEvent);
         if(pixel){
         mapViewerObj.onMouseOverFeature(pixel);
         }else{
         console.log("NO pixel");
         }
         //var pixel = evt.pixel;            
         }else{
         console.log("NOT originalEvent");
         }
         
         
         });
         */

    },
    initAOI: function () {
        try {
            var mapViewerObj = this;
            this.map.addInteraction(mapViewerObj.dragBox);
            mapViewerObj.dragBox.on("boxend", function (evt) {
                mapViewerObj.printLog("boxend................");
                var feature = new ol.Feature;
                feature.setStyle(new ol.style.Style(SPBMapConfiguration.styles.aoi));
                feature.setGeometry(mapViewerObj.dragBox.getGeometry());

                mapViewerObj.printLog(mapViewerObj.dragBox.getGeometry());

                mapViewerObj.aoi.setSource(new ol.source.Vector({
                    features: [feature],
                    wrapX: false
                }));
                mapViewerObj.processAOI(feature);
            });

            mapViewerObj.dragBox.on("boxstart", function (evt) {
                mapViewerObj.printLog("boxstart................");
                mapViewerObj.cleanAOI();
                mapViewerObj.cleanGeonamesBox();
            });
        } catch (e) {
            this.printLog(e);
        }
    },
    /**
     * Import any features layer
     * 
     * @param data :
     *            text data     
     * @param featureNs :
     *            optional gml feature namesapce
     * @param featureElem :
     *            optional gml feature element name
     */
    importFeatures: function (data, featureNs, featureElem) {
        try {
            //console.log("Call importFeatures");
            //console.log(data);
            var gmlOptions = {
                axisOrientation: 'neu',
                featureType: 'Feature',
                featureNS: 'http://www.esa.int/xml/mapviewer/features'
            };
            if (featureElem === null) {
                featureElem = 'Feature';
            }
            if (featureNs !== null) {
                gmlOptions = {
                    axisOrientation: 'neu',
                    featureType: featureElem,
                    featureNS: featureNs
                };
            }

            var gmlFormat = new ol.format.GML3(gmlOptions);
            var features = gmlFormat.readFeatures(data, {
                dataProjection: SPBMapConfiguration.transformation.wgs84,
                featureProjection: SPBMapConfiguration.transformation.webMercator
            });

            this.printLog(features);

            if (features !== null) {
                // clear highlight array
                this.highlightFeatureArray = [];
                this.currentHighlightFeature = null;

                this.footprintLayer.getSource().clear();
                //console.log("Clean all previous features.");
                for (var i = 0; i < features.length; i++) {
                    var feature = features[i];
                    if (feature.getGeometry() instanceof ol.geom.MultiPoint || feature.getGeometry() instanceof ol.geom.Point) {
                        feature.setStyle(this.pointNormalStyle);
                        this.printLog("This is a MultiPoint feature.");
                    } else {
                        feature.setStyle(this.normalStyle);
                        this.printLog("This is not a MultiPoint feature.");
                    }
                    this.printLog(feature);
                    this.footprintLayer.getSource().addFeature(feature);
                    this.createFeatureQuicklookMap(feature);
                }
            } else {
                //console.log("No features.");
            }
        } catch (e) {
            this.printLog(e);
        }
    },
    removeFeatures: function () {
        try {
            this.footprintLayer.getSource().clear();
        } catch (e) {
            this.printLog(e);
        }
    },
    onClickFeature: function (pixel) {
        this.selectFeatureByPixel(pixel);
    },
    onMouseOverFeature: function (pixel) {
        this.selectFeatureByPixel(pixel);
    },
    selectFeatureByPixel: function (pixel) {
        this.printLog("select feature by pixel " + pixel);

        var matchedFeatures = [];

        this.map.forEachFeatureAtPixel(pixel, function (feature, layer) {
            if (feature) {
                matchedFeatures.push(feature);
            }
        });

        var selectedFeature = null;
        if (matchedFeatures.length > 0) {
            if (matchedFeatures.length === 1) {
                selectedFeature = matchedFeatures[0];
                this.printLog("Only one matched feature." + selectedFeature.getId());
            } else {
                this.printLog("Number of matched feature: " + matchedFeatures.length);
                var fIdx = -1;
                var foundFeatureIds = [];
                for (var i = 0, ii = matchedFeatures.length; i < ii; ++i) {
                    var found = false;
                    for (var k = 0, kk = this.highlightFeatureArray.length; k < kk; ++k) {
                        if (matchedFeatures[i].getId() === this.highlightFeatureArray[k].getId()) {
                            this.printLog("Found the feature: " + matchedFeatures[i].getId() + " in the highlight list.");
                            found = true;
                            break;
                        }
                    }

                    if (found === false) {
                        fIdx = i;
                        this.printLog("The feature: " + matchedFeatures[i].getId() + " isn't in the highlight list. Highlight it.");
                        break;
                    } else {
                        foundFeatureIds.push(matchedFeatures[i].getId());
                        this.printLog("Continue to check: ");
                    }

                }

                if (matchedFeatures.length === foundFeatureIds.length) {
                    this.printLog("Found all features in highlight list");

                    for (var i = 0, ii = foundFeatureIds.length; i < ii; ++i) {
                        this.printLog("Highlight length = " + this.highlightFeatureArray.length);
                        for (var k = 0, kk = this.highlightFeatureArray.length; k < kk; ++k) {
                            if (this.highlightFeatureArray[k]) {
                                if (this.highlightFeatureArray[k].getId() === foundFeatureIds[i]) {
                                    this.highlightFeatureArray.splice(k, 1);
                                    break;
                                }
                            } else {
                                this.printLog(this.highlightFeatureArray[k]);
                            }

                        }
                    }
                    this.printLog("Highlight list after removing: ");
                    for (var k = 0, kk = this.highlightFeatureArray.length; k < kk; ++k) {
                        this.printLog(this.highlightFeatureArray[k].getId());
                    }

                    for (var i = 0, ii = matchedFeatures.length; i < ii; ++i) {
                        if (this.currentHighlightFeature) {
                            if (matchedFeatures[i].getId() !== this.currentHighlightFeature.getId()) {
                                fIdx = i;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }

                if (fIdx === -1) {
                    fIdx = 0;
                }

                this.printLog("feature index " + fIdx);
                selectedFeature = matchedFeatures[fIdx];
            }
        }

        if (selectedFeature) {
            this.printLog(selectedFeature);

            this.printLog("has selected feature");

            this.highlightFeatureArray.push(selectedFeature);
            this.currentHighlightFeature = selectedFeature;

            if (this.footprintLayer.getSource()) {

                var seriesSearchResultsPanel = document.getElementById(this.getInstanceId() + "_seriesSearchResultsPanel");
                if (seriesSearchResultsPanel) {
                    this.printLog("Series search results.");
                    window[this.getInstanceId() + "_selectSeriesItem"](selectedFeature.getId());
                } else {
                    var seriesDetailsPanel = document.getElementById(this.getInstanceId() + "_seriesDetailsPanel");
                    if (seriesDetailsPanel) {
                        this.printLog("Series details.");
                        //viewSeriesItemDetails(selectedFeature.getId());
                    } else {
                        var datasetSearchResultsPanel = document.getElementById(this.getInstanceId() + "_datasetSearchResultsPanel");
                        if (datasetSearchResultsPanel) {
                            this.printLog("Dataset search results.");
                            window[this.getInstanceId() + "_selectDatasetItem"](selectedFeature.getId());
                        } else {
                            var datasetDetailsPanel = document.getElementById(this.getInstanceId() + "_datasetDetailsPanel");
                            if (datasetDetailsPanel) {
                                this.printLog("Dataset details.");
                                viewDatasetItemDetails(selectedFeature.getId());
                            } else {
                                var relatedSearchResultsPanel = document.getElementById(this.getInstanceId() + "_relatedSearchResultsPanel");
                                if (relatedSearchResultsPanel) {
                                    this.printLog("Related results.");
                                    window[this.getInstanceId() + "_selectRelatedProduct"](selectedFeature.getId());
                                } else {
                                    var relatedDetailsPanel = document.getElementById(this.getInstanceId() + "_relatedDetailsPanel");
                                    if (relatedDetailsPanel) {
                                        this.printLog("Related details.");
                                        viewRelatedItemDetails(selectedFeature.getId());
                                    } else {
                                        this.printLog("Unknow panel.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
    ,
    footprintToNormalStyle: function (keepSelected) {
        var features = this.footprintLayer.getSource().getFeatures();
        var modifiedFeatures = [];

        for (var i = 0; i < features.length; i++) {
            var feature = features[i];
            var style = feature.getStyle();
            if (style.getImage()) {
                this.printLog("This is a point");
                if (style.getImage() instanceof ol.style.Circle) {
                    this.footprintLayer.getSource().removeFeature(feature);
                    var circleImage = style.getImage();
                    if (keepSelected === true && circleImage.getRadius() === 3) {
                        this.printLog("This is a selected point.");
                    } else {
                        this.printLog("This is a normal point.");
                        feature.setStyle(this.pointNormalStyle);
                    }
                    modifiedFeatures.push(feature);
                }
            } else {
                this.printLog("This isn't a point");
                this.footprintLayer.getSource().removeFeature(feature);
                if (keepSelected === true && feature.getStyle().getStroke().getWidth() === 3) {
                    this.printLog("This is a selected feature.");
                } else {
                    this.printLog("This is a normal feature.");
                    feature.setStyle(this.normalStyle);
                }
                modifiedFeatures.push(feature);
            }
        }

        if (modifiedFeatures.length > 0) {
            for (var i = 0; i < modifiedFeatures.length; i++) {
                this.footprintLayer.getSource().addFeature(modifiedFeatures[i]);
            }
        }
    },
    setElementBackground: function (sElem, sBackground) {
        try {
            if (!!window.chrome && !!window.chrome.webstore) {
                // Chrome
                this.printLog("Chrome");
                sElem.style.background = "linear-gradient(" + sBackground + ")";
            } else if (typeof InstallTrigger !== 'undefined') {
                // Firefox
                sElem.style.background = "-moz-linear-gradient(" + sBackground + ")";
            } else if (/constructor/i.test(window.HTMLElement) || (function (p) {
                return p.toString() === "[object SafariRemoteNotification]";
            })(!window['safari'] || safari.pushNotification)) {
                // Safari            
                sElem.style.background = "-webkit-linear-gradient(" + sBackground + ")";
            } else if ((!!window.opr && !!opr.addons) || !!window.opera || navigator.userAgent.indexOf(' OPR/') >= 0) {
                // Opera
                sElem.style.background = "-o-linear-gradient(" + sBackground + ")";
            } else {
                sElem.style.background = "linear-gradient(" + sBackground + ")";
            }
        } catch (e) {
            this.printLog(e);
            sElem.style.backgroundColor = "linear-gradient(" + sBackground + ")";
        }

    },
    changeCssClass: function (htmlElement, oldClass, newClass) {
        try {
            var elemClasses = htmlElement.classList;
            if (elemClasses) {
                if (elemClasses.contains(oldClass)) {
                    elemClasses.remove(oldClass);
                }

                if (elemClasses.contains(newClass)) {
                    this.printLog("Class " + newClass + " already exist.");
                } else {
                    elemClasses.add(newClass);
                }
            }
        } catch (e) {
            this.printLog(e);

        }
    },
    selectFeatures: function (featureData) {
        try {
            var featureLayer = this.footprintLayer;
            /*
             * put back to normal style for unselected features
             */
            if (featureData.unselectedFeatures) {
                var unselectedArray = featureData.unselectedFeatures.split("#");
                for (var i = 0; i < unselectedArray.length; i++) {
                    var feature = this.getFootprintFeatureById(unselectedArray[i]);
                    if (feature) {
                        featureLayer.getSource().removeFeature(feature);
                        if (feature.getGeometry() instanceof ol.geom.MultiPoint || feature.getGeometry() instanceof ol.geom.Point) {
                            feature.setStyle(this.pointNormalStyle);
                        } else {
                            feature.setStyle(this.normalStyle);
                        }

                        featureLayer.getSource().addFeature(feature);
                    }
                }
            }

            if (featureData.selectedFeatures) {
                var selectedArray = featureData.selectedFeatures.split("#");
                for (var i = 0; i < selectedArray.length; i++) {
                    var feature = this.getFootprintFeatureById(selectedArray[i]);
                    if (feature) {
                        featureLayer.getSource().removeFeature(feature);
                        if (feature.getGeometry() instanceof ol.geom.MultiPoint || feature.getGeometry() instanceof ol.geom.Point) {
                            feature.setStyle(this.pointSelectedStyle);
                        } else {
                            feature.setStyle(this.selectedStyle);
                        }
                        featureLayer.getSource().addFeature(feature);
                        this.createFeatureQuicklookMap(feature);
                    }
                }
            }
        } catch (e) {
            this.printLog(e);
        }
    },
    highlightFeatures: function (featureData) {
        try {
            var featureLayer = this.footprintLayer;
            /*
             * put back to normal style for unhighlight features
             */
            if (featureData.unhighlightFeatures) {
                var unselectedArray = featureData.unhighlightFeatures.split("#");
                for (var i = 0; i < unselectedArray.length; i++) {
                    var feature = this.getFootprintFeatureById(unselectedArray[i]);
                    if (feature) {
                        featureLayer.getSource().removeFeature(feature);
                        if (feature.getGeometry() instanceof ol.geom.MultiPoint || feature.getGeometry() instanceof ol.geom.Point) {
                            feature.setStyle(this.pointNormalStyle);
                        } else {
                            feature.setStyle(this.normalStyle);
                        }
                        featureLayer.getSource().addFeature(feature);
                    }
                }
            }
            /*
             * highlight the features
             */
            if (featureData.highlightFeatures) {
                var selectedArray = featureData.highlightFeatures.split("#");
                for (var i = 0; i < selectedArray.length; i++) {
                    var feature = this.getFootprintFeatureById(selectedArray[i]);
                    if (feature) {
                        featureLayer.getSource().removeFeature(feature);
                        if (feature.getGeometry() instanceof ol.geom.MultiPoint || feature.getGeometry() instanceof ol.geom.Point) {
                            feature.setStyle(this.pointHighlightStyle);
                        } else {
                            feature.setStyle(this.highlightStyle);
                        }
                        featureLayer.getSource().addFeature(feature);
                    }
                }
            }

            /*
             * put back the selected features
             */
            if (featureData.selectedFeatures) {
                var selectedArray = featureData.selectedFeatures.split("#");
                for (var i = 0; i < selectedArray.length; i++) {
                    var feature = this.getFootprintFeatureById(selectedArray[i]);
                    if (feature) {
                        featureLayer.getSource().removeFeature(feature);
                        if (feature.getGeometry() instanceof ol.geom.MultiPoint || feature.getGeometry() instanceof ol.geom.Point) {
                            feature.setStyle(this.pointSelectedStyle);
                        } else {
                            feature.setStyle(this.selectedStyle);
                        }
                        featureLayer.getSource().addFeature(feature);
                    }
                }
            }
        } catch (e) {
            this.printLog(e);
        }
    },
    unhighlightFeatures: function (featureData) {
        try {
            var featureLayer = this.footprintLayer;
            /*
             * put back to normal style for unhighlight features
             */
            if (featureData.unhighlightFeatures) {
                var unselectedArray = featureData.unhighlightFeatures.split("#");
                for (var i = 0; i < unselectedArray.length; i++) {
                    var feature = this.getFootprintFeatureById(unselectedArray[i]);
                    if (feature) {
                        featureLayer.getSource().removeFeature(feature);
                        if (feature.getGeometry() instanceof ol.geom.MultiPoint || feature.getGeometry() instanceof ol.geom.Point) {
                            feature.setStyle(this.pointNormalStyle);
                        } else {
                            feature.setStyle(this.normalStyle);
                        }
                        featureLayer.getSource().addFeature(feature);
                    }
                }
            }

            /*
             * put back the selected features
             */
            if (featureData.selectedFeatures) {
                var selectedArray = featureData.selectedFeatures.split("#");
                for (var i = 0; i < selectedArray.length; i++) {
                    var feature = this.getFootprintFeatureById(selectedArray[i]);
                    if (feature) {
                        featureLayer.getSource().removeFeature(feature);
                        if (feature.getGeometry() instanceof ol.geom.MultiPoint || feature.getGeometry() instanceof ol.geom.Point) {
                            feature.setStyle(this.pointSelectedStyle);
                        } else {
                            feature.setStyle(this.selectedStyle);
                        }
                        featureLayer.getSource().addFeature(feature);
                    }
                }
            }
        } catch (e) {
            this.printLog(e);
        }
    },
    zoomToSelectedFeature: function (featureId) {
        try {
            var feature = this.getFootprintFeatureById(featureId);
            if (feature !== null) {
                var gExtend = feature.getGeometry().getExtent();
                //var size = this.map.getSize();

                //var sizeWidth = (50 / 100) * size[0];
                //var sizeHeight = (50 / 100) * (size[1] - 520);
                //this.map.getView().fit(gExtend, [sizeWidth, sizeHeight]);               
                this.map.getView().fit(gExtend, this.map.getSize(), {padding: [100, 100, 420, 200], maxZoom: 20});
            }
        } catch (e) {
            this.printLog(e);
        }
    },
    getFootprintFeatureById: function (featureId) {
        var matchFeature = null;
        if (this.footprintLayer) {
            if (this.footprintLayer.getSource()) {
                matchFeature = this.footprintLayer.getSource().getFeatureById(featureId);
            }
        }
        return matchFeature;
    },
    processAOI: function (selectedFeature) {
        try {
            var newFeature = selectedFeature.clone();
            var gmlOptions = {
                featureType: "Feature",
                featureNS: "http://www.esa.int/xml/mapviewer/features"
            };
            var gml = new ol.format.GML3(gmlOptions);
            this.printLog("GML:");
            this.printLog(gml);
            var writeOptions = {
                dataProjection: ol.proj.get(SPBMapConfiguration.transformation.wgs84),
                featureProjection: ol.proj.get(SPBMapConfiguration.transformation.webMercator)
            };
            var strGML = gml.writeFeatures([newFeature], writeOptions);
            this.printLog("strGML:");
            this.printLog(strGML);
            var fGeometry = newFeature.getGeometry();
            fGeometry = fGeometry.transform(SPBMapConfiguration.transformation.webMercator, SPBMapConfiguration.transformation.wgs84);
            var fExtent = fGeometry.getExtent();
            var bbox = fExtent.toString();
            this.printLog("bbox:");
            this.printLog(bbox);
            var data = new Object();
            data.aoi = strGML;
            data.bbox = bbox;
            data.rectangle = strGML;
            data.old = strGML;
            this.sendAOIMessage(data);
        } catch (e) {
            this.printLog(e);
        }
    },
    cleanAOI: function () {
        try {
            this.printLog("cleanAOI.....");
            var data = new Object();
            data.aoi = "";
            data.bbox = "";
            data.rectangle = "";
            data.old = "";
            this.aoi.getSource().clear();
            this.sendAOIMessage(data);
        } catch (e) {
            this.printLog(e);
        }
    },
    sendAOIMessage: function (receivedData) {
        try {
            var data = new Object();
            data.aoi = receivedData.rectangle;
            data.bbox = receivedData.bbox;
            data.old = receivedData.old;
            CommunicationUtil.sendMessageToAll("map.drawn.aoi.rectangle", this.instanceId, data);
        } catch (e) {
            this.printLog(e);
        }
    },
    toggleAOI: function () {
        try {
            this.dragBox.setActive(!this.dragBox.getActive());
        } catch (e) {
            this.printLog(e);
        }
    },
    drawGeonameBox: function (selectedBBOX) {
        try {
            if (selectedBBOX) {
                var coordinates = selectedBBOX.split(",");
                if (typeof coordinates !== 'undefined' && coordinates.length === 4) {
                    // clean AOI
                    this.cleanAOI();

                    /*
                     *  BBOX(x1,y1,x2,y2) ==> POLYGON(x1 y1, x1 y2, x2 y2, x2 y1, x1 y1)
                     */
                    var selectedBboxJson = [[[Number(coordinates[0]), Number(coordinates[1])], [Number(coordinates[0]), Number(coordinates[3])], [Number(coordinates[2]), Number(coordinates[3])], [Number(coordinates[2]), Number(coordinates[1])], [Number(coordinates[0]), Number(coordinates[1])]]];
                    this.printLog("selectedBboxJson");
                    this.printLog(selectedBboxJson);
                    var gPolygon = new ol.geom.Polygon(selectedBboxJson);
                    gPolygon.transform(SPBMapConfiguration.transformation.wgs84, SPBMapConfiguration.transformation.webMercator);
                    var gFeature = new ol.Feature({
                        geometry: gPolygon
                    });
                    gFeature.setStyle(new ol.style.Style(SPBMapConfiguration.styles.aoi));

                    this.geonamesBboxLayer.getSource().clear();
                    this.geonamesBboxLayer.getSource().addFeature(gFeature);

                    var size = this.map.getSize();
                    var sizeWidth = (30 / 100) * size[0];
                    var sizeHeight = (30 / 100) * size[1];
                    this.printLog(sizeWidth);
                    this.printLog(sizeHeight);
                    var gExtend = gFeature.getGeometry().getExtent();
                    this.map.getView().fit(gExtend, [sizeWidth, sizeHeight]);
                    var data = new Object();
                    data.aoi = "";
                    data.bbox = selectedBBOX;
                    data.rectangle = "";
                    data.old = "";
                    this.sendAOIMessage(data);
                }
            }
        } catch (e) {
            this.printLog(e);
        }
    },
    cleanGeonamesBox: function () {
        try {
            this.geonamesBboxLayer.getSource().clear();
            var data = new Object();
            CommunicationUtil.sendMessageToAll("map.clean.geonames.box", this.instanceId, data);
        } catch (e) {
            this.printLog(e);
        }
    },
    setVisibleLayer: function (layerTitle) {
        try {
            this.layers.forEach(function (pLayer) {
                if (pLayer !== 'undefined' && pLayer instanceof ol.layer.Group) {
                    pLayer.getLayers().forEach(function (cLayer) {
                        if (cLayer !== 'undefined') {
                            var lyrTitle = cLayer.get('title');
                            if (lyrTitle && lyrTitle === layerTitle) {
                                cLayer.setVisible(true);
                            } else {
                                cLayer.setVisible(false);
                            }
                        }
                    });
                }
            });
        } catch (e) {
            this.printLog(e);
        }
    },
    createFeatureQuicklookMap: function (quicklookFeature) {
        this.printLog("createFeatureQuicklookMap");
        try {
            var mapTarget = "quicklookMap" + quicklookFeature.getId();
            var quicklookMapDiv = document.getElementById(mapTarget);
            if (quicklookMapDiv) {
                this.printLog(quicklookMapDiv);

                var vectorLayer = new ol.layer.Vector({
                    source: new ol.source.Vector({
                        wrapX: false
                    })
                });

                var quicklookMap = new ol.Map({
                    layers: [this.customWmsThumbnailLayer, vectorLayer],
                    target: mapTarget,
                    controls: [],
                    interactions: ol.interaction.defaults({mouseWheelZoom: false}),
                });

                vectorLayer.getSource().addFeature(quicklookFeature);
                var gExtend = quicklookFeature.getGeometry().getExtent();
                quicklookMap.getView().fit(gExtend, quicklookMap.getSize());
            }
        } catch (e) {
            this.printLog(e);
        }

    },
    createAllFeaturesQuicklookMap: function () {
        this.printLog("createAllFeaturesQuicklookMap");
        try {
            if (this.footprintLayer.getSource()) {
                var features = this.footprintLayer.getSource().getFeatures();
                if (features) {
                    for (var i = 0; i < features.length; i++) {
                        this.createFeatureQuicklookMap(features[i]);
                    }
                }
            }
        } catch (e) {
            this.printLog(e);
        }
    },
    printLog: function (message) {
        try {
            if (typeof console === 'undefined') {

            } else {
                //console.log(message);
            }
        } catch (e) {
            this.printLog(e);
        }
    },
    objToString: function (obj) {
        var str = '';
        for (var p in obj) {
            if (obj.hasOwnProperty(p)) {
                str += p + '::' + obj[p] + '\n';
            }
        }
        return str;
    }
}