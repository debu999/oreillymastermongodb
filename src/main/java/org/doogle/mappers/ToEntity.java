package org.doogle.mappers;

import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "id", expression = "java(org.doogle.mappers.utils.ObjectIdUtils.toObjectId(source.getId()))")
public @interface ToEntity {
}