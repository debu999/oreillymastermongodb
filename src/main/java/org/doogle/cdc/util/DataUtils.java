/*
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.doogle.cdc.util;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.DataException;

import java.util.Map;


public class DataUtils {

//  public static Map<String, Object> convertStruct(Object structOrMapOrArray)
//  {
//    if(structOrMapOrArray instanceof Struct)
//    {
//      List<Field> fieldList = ((Struct) structOrMapOrArray).schema().fields();
//      return fieldList.stream().collect(toMap(Field::name,
//          DataUtils::convertField));
//    } else if (structOrMapOrArray instanceof Map) {
//      Map<String, Object> dict = (Map<String, Object>) structOrMapOrArray;
//      return dict.entrySet().stream().collect(toMap(e->e.getKey(), e->{}))
//    }
//  }

  private static Object convertField(Field f) {
    return null;
  }

  public static Object getField(Object structOrMap, String fieldName) {
    validate(structOrMap, fieldName);

    Object field;
    if (structOrMap instanceof Struct) {
      field = ((Struct) structOrMap).get(fieldName);
    } else if (structOrMap instanceof Map) {
      field = ((Map<?, ?>) structOrMap).get(fieldName);
      if (field == null) {
        throw new DataException(String.format("Unable to find nested field '%s'", fieldName));
      }
      return field;
    } else {
      throw new DataException(String.format(
            "Argument not a Struct or Map. Cannot get field '%s' from %s.",
            fieldName,
            structOrMap
      ));
    }
    if (field == null) {
      throw new DataException(
            String.format("The field '%s' does not exist in %s.", fieldName, structOrMap));
    }
    return field;
  }

  public static Object getNestedFieldValue(Object structOrMap, String fieldName) {
    validate(structOrMap, fieldName);

    try {
      Object innermost = structOrMap;
      // Iterate down to final struct
      for (String name : fieldName.split("\\.")) {
        innermost = getField(innermost, name);
      }
      return innermost;
    } catch (DataException e) {
      throw new DataException(
            String.format("The field '%s' does not exist in %s.", fieldName, structOrMap),
            e
      );
    }
  }

  public static Field getNestedField(Schema schema, String fieldName) {
    validate(schema, fieldName);

    final String[] fieldNames = fieldName.split("\\.");
    try {
      Field innermost = schema.field(fieldNames[0]);
      // Iterate down to final schema
      for (int i = 1; i < fieldNames.length; ++i) {
        innermost = innermost.schema().field(fieldNames[i]);
      }
      return innermost;
    } catch (DataException e) {
      throw new DataException(
            String.format("Unable to get field '%s' from schema %s.", fieldName, schema),
            e
      );
    }
  }

  private static void validate(Object o, String fieldName) {
    if (o == null) {
      throw new ConnectException("Attempted to extract a field from a null object.");
    }
    if (StringUtils.isBlank(fieldName)) {
      throw new ConnectException("The field to extract cannot be null or empty.");
    }
  }
}