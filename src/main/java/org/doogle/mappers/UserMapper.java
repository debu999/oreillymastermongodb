package org.doogle.mappers;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.doogle.entity.UserEntity;
import org.doogle.model.User;
import org.eclipse.microprofile.graphql.Name;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "cdi")
public abstract class UserMapper {
    @Mapping(target = "id", expression = "java(fromObjectId(source.id))")
    @Named("fromEntity")
    public abstract User fromEntity(UserEntity source);

    @Mapping(target = "id", expression = "java(toObjectId(source.getId()))")
    @Named("toEntity")
    public abstract UserEntity toEntity(User source);

    @IterableMapping(qualifiedByName = "fromEntity")
    public abstract List<User> fromEntities(List<UserEntity> source);

    @IterableMapping(qualifiedByName = "toEntity")
    public abstract List<UserEntity> toEntities(List<User> source);

    public ObjectId toObjectId(String objectId) {
        return StringUtils.isNotBlank(objectId) ? new ObjectId(objectId) : null;
    }

    public String fromObjectId(ObjectId objectId) {
        return ObjectUtils.isNotEmpty(objectId) ? objectId.toString() : null;
    }
}
