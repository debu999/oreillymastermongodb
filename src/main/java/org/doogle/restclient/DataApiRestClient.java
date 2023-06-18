package org.doogle.restclient;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.doogle.model.FindDocumentModel;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/app/data-gpeqh/endpoint/data/v1/action")
@RegisterRestClient(configKey = "mongo-data-api")
@ClientHeaderParam(name = "Content-Type", value = "application/json")
@ClientHeaderParam(name = "Access-Control-Request-Headers", value = "*")
//@ClientHeaderParam(name = "api-key", value = "{get-api-key}")
@ClientHeaderParam(name = "api-key", value = "${mongodb.data.apikey}")
//@ClientHeaderParam(name = "Accept", value = "application/ejson")
public interface DataApiRestClient {
    //
//    @ConfigProperty(name = "mongodb.data.apikey")
//    String apiKey;
    @POST
    @Path("/{action}")
//    @Produces(MediaType.APPLICATION_JSON)
    String performAction(@PathParam("action") String action, FindDocumentModel payload);

//    default String getApiKey() {
//        return apiKey;
//    }

}
