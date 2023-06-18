package org.doogle.mappers;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.doogle.entity.UserEntity;
import org.doogle.model.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "jakarta")
public abstract class UserMapper {
    @ToModel
    @Named("fromUserEntity")
    public abstract User fromUserEntity(UserEntity source);

    @ToEntity
    @Named("toUserEntity")
    public abstract UserEntity toUserEntity(User source);

    @IterableMapping(qualifiedByName = "fromUserEntity")
    public abstract List<User> fromUserEntities(List<UserEntity> source);

    @IterableMapping(qualifiedByName = "toUserEntity")
    public abstract List<UserEntity> toUserEntities(List<User> source);
}
