package org.doogle.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.doogle.entity.TicketEntity;
import org.doogle.mappers.TicketMapper;
import org.doogle.model.TicketModel;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;
@GraphQLApi
@ApplicationScoped
public class TicketResource {

  @Inject
  MeterRegistry registry;
  @Inject
  ObjectMapper mapper;
  @Inject
  TicketMapper ticketMapper;

  @Query("Ticket")
  public Uni<List<TicketModel>> fetchTickets() {
    registry.counter("fetchTicketGraphQL", "type", "methodcall").increment();
    return TicketEntity.getAll().log("ALL_TICKETS")
        .map(b -> ticketMapper.fromTicketEntities(b));
  }

  @Mutation("createUpdateTicket")
  public Uni<TicketModel> createUpdateTicket(TicketModel ticket) {
    registry.counter("createUpdateTicket", "type", "methodcall").increment();
    return Uni.createFrom().item(ticket)
        .map(t -> ticketMapper.toTicketEntity(t))
        .flatMap(TicketEntity::persistOrUpdateTicketEntity).log()
        .map(te -> ticketMapper.fromTicketEntity(te));
  }

  @Mutation("deleteTicket")
  public Uni<TicketModel> deleteTicket(TicketModel ticket) {
    registry.counter("deleteTicket", "type", "methodcall").increment();
    return Uni.createFrom().item(ticket)
        .map(t -> ticketMapper.toTicketEntity(t))
        .flatMap(te -> TicketEntity.deleteTicketEntity(te.getId())).log()
        .map(ticketEntity -> ticketMapper.fromTicketEntity(ticketEntity)).log("DELETED_TICKET");
  }

  @Mutation("editChangeStreamPreAndPostImages")
  public Uni<String> editChangeStreamPreAndPostImages(Boolean enabled) {
    registry.counter("editChangeStreamPreAndPostImages", "type", "methodcall").increment();
    return TicketEntity.editChangeStreamPreAndPostImages(enabled);
  }


}
