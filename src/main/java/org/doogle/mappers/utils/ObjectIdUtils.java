package org.doogle.mappers.utils;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;


public class ObjectIdUtils {
    public static ObjectId toObjectId(String objectId) {
        return StringUtils.isNotBlank(objectId) ? new ObjectId(objectId) : null;
    }

    public static String fromObjectId(ObjectId objectId) {
        return ObjectUtils.isNotEmpty(objectId) ? objectId.toString() : null;
    }
}

