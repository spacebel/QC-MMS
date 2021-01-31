package be.spacebel.opensearch.api;

import be.spacebel.opensearch.model.FeatureCollection;
import java.time.OffsetDateTime;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.ext.multipart.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * FedEO Earth Observation Catalog
 *
 * <p>FedEO provides interoperable access, following ISO/OGC interface guidelines, to Earth Observation metadata. You can try all HTTP operations described in this OpenAPI 3.0 specification.
 *
 */
@Path("/")
public interface SearchApi  {

    /**
     * Dataset search (rel&#x3D;\&quot;results\&quot;)
     *
     * The endpoint returns information about the *FedEO* dataset matching specific filtering criteria such as organisation, platform, instrument, title, keyword and lists the datasets . 
     *
     */
    @GET
    @Path("/eo-catalog/series/{parentIdentifier}/datasets")
    @Produces({ "application/geo+json" })
    @Operation(summary = "Dataset search (rel=\"results\")", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "An array of dataset series", content = @Content(schema = @Schema(implementation = FeatureCollection.class))),
        @ApiResponse(responseCode = "400", description = "Bad request (invalid parameter)"),
        @ApiResponse(responseCode = "415", description = "Unsupported Media Type."),
        @ApiResponse(responseCode = "500", description = "Unexpected error") })
    public FeatureCollection eoCatalogSeriesParentIdentifierDatasetsGet(@PathParam("parentIdentifier") String parentIdentifier, @QueryParam("httpAccept")String httpAccept, @QueryParam("maximumRecords")Integer maximumRecords, @QueryParam("startRecord")Integer startRecord, @QueryParam("startPage")Integer startPage, @QueryParam("query")String query, @QueryParam("startDate")OffsetDateTime startDate, @QueryParam("endDate")OffsetDateTime endDate, @QueryParam("bbox")String bbox, @QueryParam("lat")Double lat, @QueryParam("lon")Double lon, @QueryParam("name")String name, @QueryParam("radius")Double radius, @QueryParam("geometry")String geometry, @QueryParam("uid")String uid, @QueryParam("platform")String platform, @QueryParam("platformSerialIdentifier")String platformSerialIdentifier, @QueryParam("instrument")String instrument, @QueryParam("sensorType")String sensorType, @QueryParam("resolution")Double resolution, @QueryParam("creationDate")OffsetDateTime creationDate, @QueryParam("modificationDate")OffsetDateTime modificationDate, @QueryParam("productionStatus")String productionStatus, @QueryParam("acquisitionType")String acquisitionType, @QueryParam("orbitNumber")Integer orbitNumber, @QueryParam("orbitDirection")String orbitDirection, @QueryParam("track")String track, @QueryParam("frame")String frame, @QueryParam("swathIdentifier")String swathIdentifier, @QueryParam("lowestLocation")Double lowestLocation, @QueryParam("highestLocation")Double highestLocation, @QueryParam("productVersion")String productVersion, @QueryParam("productQualityStatus")String productQualityStatus, @QueryParam("productQualityDegradationTag")String productQualityDegradationTag, @QueryParam("processorName")String processorName, @QueryParam("processingCenter")String processingCenter, @QueryParam("processingDate")OffsetDateTime processingDate, @QueryParam("sensorMode")String sensorMode, @QueryParam("archivingCenter")String archivingCenter, @QueryParam("processingMode")String processingMode, @QueryParam("acquisitionStation")String acquisitionStation, @QueryParam("acquisitionSubType")String acquisitionSubType, @QueryParam("startTimeFromAscendingNode")Double startTimeFromAscendingNode, @QueryParam("completionTimeFromAscendingNode")Double completionTimeFromAscendingNode, @QueryParam("illuminationAzimuthAngle")Double illuminationAzimuthAngle, @QueryParam("illuminationZenithAngle")Double illuminationZenithAngle, @QueryParam("illuminationElevationAngle")Double illuminationElevationAngle, @QueryParam("productType")String productType, @QueryParam("processingLevel")String processingLevel);

    @GET
    @Path("/eo-catalog/series/{parentIdentifier}/datasets/{datasetId}")
    @Produces({ "application/geo+json" })
    @Operation(summary = "", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "An array of dataset series", content = @Content(schema = @Schema(implementation = FeatureCollection.class))),
        @ApiResponse(responseCode = "400", description = "Bad request (invalid parameter)"),
        @ApiResponse(responseCode = "404", description = "Dataset not found"),
        @ApiResponse(responseCode = "500", description = "Unexpected error") })
    public FeatureCollection search(@PathParam("parentIdentifier") String parentIdentifier, @PathParam("datasetId") String datasetId, @QueryParam("httpAccept")String httpAccept);
}
