package org.doogle.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.doogle.model.DocumentResponse;
import org.doogle.model.FindDocumentModel;
import org.doogle.restclient.DataApiRestClient;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

@ApplicationScoped
@Path("/data-api")
@GraphQLApi
public class DataResource {

    @RestClient
    DataApiRestClient dataApiRestClient;

    @Inject
    ObjectMapper mapper;

    @POST
    @Path("/action/{action}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<DocumentResponse> performActionRestApi(String action, FindDocumentModel payload) throws JsonProcessingException {

        String response = dataApiRestClient.performAction(action, payload);
        Log.info(response);
        DocumentResponse docResponse = mapper.readValue(response, DocumentResponse.class);

        return RestResponse.ResponseBuilder.ok(docResponse, MediaType.APPLICATION_JSON).build();
    }

    @Query("action")
    public String performActionGraphQL(String action, FindDocumentModel payload) throws JsonProcessingException {
        String response = dataApiRestClient.performAction(action, payload);
        Log.info(response);
        return response;
    }
}
