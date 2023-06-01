package org.doogle.mappers;

import org.doogle.entity.BookOrdersEntity;
import org.doogle.model.BookOrdersModel;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "jakarta")
public abstract class BookOrdersMapper {
    @ToModel
    @Named("fromBookEntity")
    public abstract BookOrdersModel fromBookEntity(BookOrdersEntity source);

    @ToEntity
    @Named("toBookEntity")
    public abstract BookOrdersEntity toBookEntity(BookOrdersModel source);

    @IterableMapping(qualifiedByName = "fromBookEntity")
    public abstract List<BookOrdersModel> fromBookEntities(List<BookOrdersEntity> source);

    @IterableMapping(qualifiedByName = "toBookEntity")
    public abstract List<BookOrdersEntity> toBookEntities(List<BookOrdersModel> source);
}
