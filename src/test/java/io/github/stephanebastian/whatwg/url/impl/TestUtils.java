/*
 * Copyright 2023 - Stephane Bastian - stephane.bastian.dev@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package io.github.stephanebastian.whatwg.url.impl;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import io.github.stephanebastian.whatwg.url.Url;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.*;
import java.util.*;

public class TestUtils {
  public static <T> T readJsonFile(String name) {
    InputStream is = TestUtils.class.getClassLoader().getResourceAsStream(name);
    Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
    try (JsonReader jsonReader = new JsonReader(reader)) {
      jsonReader.setLenient(true);
      return (T) readValue(jsonReader);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Object readValue(JsonReader jsonReader) {
    try {
      if (jsonReader.hasNext()) {
        JsonToken nextToken = jsonReader.peek();
        if (JsonToken.BEGIN_OBJECT.equals(nextToken)) {
          return readObject(jsonReader);
        } else if (JsonToken.BEGIN_ARRAY.equals(nextToken)) {
          return readArray(jsonReader);
        } else if (JsonToken.BOOLEAN.equals(nextToken)) {
          return jsonReader.nextBoolean();
        } else if (JsonToken.STRING.equals(nextToken)) {
          return jsonReader.nextString();
        } else if (JsonToken.NUMBER.equals(nextToken)) {
          return jsonReader.nextDouble();
        } else if (JsonToken.NULL.equals(nextToken)) {
          jsonReader.nextNull();
          return null;
        } else {
          throw new RuntimeException("inconsistent state while reading json");
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  private static Map<String, Object> readObject(JsonReader jsonReader) {
    Map<String, Object> map = new HashMap<>();
    try {
      jsonReader.beginObject();
      while (jsonReader.peek() != JsonToken.END_OBJECT) {
        map.put(jsonReader.nextName(), readValue(jsonReader));
      }
      jsonReader.endObject();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return map;
  }

  private static Collection<Object> readArray(JsonReader jsonReader) {
    Collection<Object> result = new ArrayList<>();
    try {
      jsonReader.beginArray();
      while (jsonReader.peek() != JsonToken.END_ARRAY) {
        result.add(readValue(jsonReader));
      }
      jsonReader.endArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  public static void main(String[] ars) throws Exception {
    String in = "https://퀬-?\uD99B\uDCD2.\u200Cૅ\uDB67\uDE24۴/x";
    Url out = Url.create(in);
    System.out.println(out);
    System.out.println("\"%3Fa=b&c=d\"");
  }
}
