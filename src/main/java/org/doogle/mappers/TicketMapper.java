package org.doogle.mappers;

import java.util.List;
import org.doogle.entity.BookOrdersEntity;
import org.doogle.entity.TicketEntity;
import org.doogle.model.BookOrdersModel;
import org.doogle.model.TicketModel;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "jakarta")
public abstract class TicketMapper {
    @ToModel
    @Named("fromTicketEntity")
    public abstract TicketModel fromTicketEntity(TicketEntity source);

    @ToEntity
    @Named("toTicketEntity")
    public abstract TicketEntity toTicketEntity(TicketModel source);

    @IterableMapping(qualifiedByName = "fromTicketEntity")
    public abstract List<TicketModel> fromTicketEntities(List<TicketEntity> source);

    @IterableMapping(qualifiedByName = "toTicketEntity")
    public abstract List<TicketEntity> toTicketEntities(List<TicketModel> source);

}
