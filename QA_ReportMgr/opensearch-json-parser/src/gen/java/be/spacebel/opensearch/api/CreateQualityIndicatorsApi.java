package be.spacebel.opensearch.api;

import be.spacebel.opensearch.model.QualityInformation;

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
public interface CreateQualityIndicatorsApi  {

    @POST
    @Path("/eo-catalog/series/{parentIdentifier}/datasets/{datasetId}/productInformation/qualityInformation")
    @Consumes({ "application/geo+json" })
    @Operation(summary = "", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Quality indicators have been created."),
        @ApiResponse(responseCode = "400", description = "Bad request. Indicator is already exsisted."),
        @ApiResponse(responseCode = "500", description = "Unexpected error") })
    public void createQualityIndicators(QualityInformation body, @PathParam("parentIdentifier") String parentIdentifier, @PathParam("datasetId") String datasetId);
}
