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

import io.github.stephanebastian.whatwg.url.Url;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class TestUrl {
  private static void assertUrlProperties(Url url, Map<String, String> expectedProperties) {
    for (String name : expectedProperties.keySet()) {
      String expectedValue = expectedProperties.get(name);
      switch (name) {
        case "hash":
          Assertions.assertThat(url.hash()).isEqualTo(expectedValue);
          break;
        case "host":
          Assertions.assertThat(url.host()).isEqualTo(expectedValue);
          break;
        case "hostname":
          Assertions.assertThat(url.hostname()).isEqualTo(expectedValue);
          break;
        case "href":
          Assertions.assertThat(url.href()).isEqualTo(expectedValue);
          break;
        case "pathname":
          Assertions.assertThat(url.pathname()).isEqualTo(expectedValue);
          break;
        case "password":
          Assertions.assertThat(url.password()).isEqualTo(expectedValue);
          break;
        case "port":
          Assertions.assertThat(url.port()).isEqualTo(expectedValue);
          break;
        case "protocol":
          Assertions.assertThat(url.protocol()).isEqualTo(expectedValue);
          break;
        case "search":
          Assertions.assertThat(url.search()).isEqualTo(expectedValue);
          break;
        case "username":
          Assertions.assertThat(url.username()).isEqualTo(expectedValue);
          break;
        default:
          Assertions.fail("unknown property " + name);
          break;
      }
    }
  }

  static Collection<Map<String, Object>> testSettersData() {
    Map<String, Object> tmp = TestUtils.readJsonFile("setters_tests.json");
    Collection<Map<String, Object>> result = new ArrayList<>();
    tmp.forEach((name, value) -> {
      if (!name.equals("comment")) {
        Collection<Map<String, Object>> subObjects = (Collection<Map<String, Object>>) value;
        subObjects.forEach(subObject -> {
          subObject.put("setterName", name);
          result.add(subObject);
        });
      }
    });
    return result;
  }

  static Collection<Map<String, Object>> idnaTestData() {
    Collection<Object> result = TestUtils.readJsonFile("IdnaTestV2.json");
    // remove non-map entries
    result.removeIf(next -> !(next instanceof Map));
    return (Collection<Map<String, Object>>) (Object) result;
  }

  static Collection<Map<String, Object>> toAsciiTestData() {
    Collection<Object> result = TestUtils.readJsonFile("toascii.json");
    // remove non-map entries
    result.removeIf(next -> !(next instanceof Map));
    return (Collection<Map<String, Object>>) (Object) result;
  }

  static Collection<Map<String, Object>> urlParserTestData() {
    Collection<Object> result = TestUtils.readJsonFile("urltestdata.json");
    // remove non-map entries
    result.removeIf(next -> !(next instanceof Map));
    return (Collection<Map<String, Object>>) (Object) result;
  }

  static String encodeUrl(String value) {
    try {
      return URLEncoder.encode(value, "utf-8");
    } catch (Exception e) {
    }
    return value;
  }

  @ParameterizedTest
  @MethodSource("toAsciiTestData")
  public void idnaToASCII(Map<String, Object> testData) {
    String input = (String) testData.get("input");
    String output = (String) testData.get("output");
    // percent encode input so that ?, :, /, , #, \\
    String encodedInput = encodeUrl(input);
    // let's create a url from the input domain
    String urlInput = "https://" + input + "/x";
    if (output == null) {
      Assertions.assertThatException().isThrownBy(() -> Url.create(urlInput));
    } else {
      Url url = Url.create(urlInput);
      Assertions.assertThat(url).isNotNull();
      Assertions.assertThat(url.host()).isEqualTo(output);
      Assertions.assertThat(url.hostname()).isEqualTo(output);
      Assertions.assertThat(url.protocol()).isEqualTo("https:");
      Assertions.assertThat(url.pathname()).isEqualTo("/x");
      Assertions.assertThat(url.href()).isEqualTo("https://" + output + "/x");
    }
  }

  @ParameterizedTest
  @MethodSource("idnaTestData")
  public void idnaToASCIIV2(Map<String, Object> testData) {
    String input = (String) testData.get("input");
    String output = (String) testData.get("output");
    String comment = (String) testData.get("comment");
    String encodedInput = encodeUrl(input);
    if (comment != null && comment.contains("(ignored)")) {
      return;
    }
    // let's create a url from the input domain
    String urlInput = "https://" + encodedInput + "/x";
    if (output == null) {
      Assertions.assertThatException().isThrownBy(() -> Url.create(urlInput));
    } else {
      Url url = Url.create(urlInput);
      Assertions.assertThat(url).isNotNull();
      Assertions.assertThat(url.host()).isEqualTo(output);
      Assertions.assertThat(url.hostname()).isEqualTo(output);
      Assertions.assertThat(url.protocol()).isEqualTo("https:");
      Assertions.assertThat(url.pathname()).isEqualTo("/x");
      Assertions.assertThat(url.href()).isEqualTo("https://" + output + "/x");
    }
  }

  /**
   * Here is the format of the input: "input": "http://example\t.\norg", "base":
   * "http://example.org/foo/bar", "href": "http://example.org/", "origin": "http://example.org",
   * "protocol": "http:", "username": "", "password": "", "host": "example.org", "hostname":
   * "example.org", "port": "", "pathname": "/", "search": "", "hash": ""
   *
   * @param testData
   */
  @ParameterizedTest
  @MethodSource("urlParserTestData")
  public void urlParser(Map<String, Object> testData) {
    String input = (String) testData.get("input");
    String base = (String) testData.get("base");
    String hash = (String) testData.get("hash");
    String hostname = (String) testData.get("hostname");
    String host = (String) testData.get("host");
    String href = (String) testData.get("href");
    String origin = (String) testData.get("origin");
    String port = (String) testData.get("port");
    String protocol = (String) testData.get("protocol");
    String username = (String) testData.get("username");
    String pathname = (String) testData.get("pathname");
    String password = (String) testData.get("password");
    String search = (String) testData.get("search");
    String searchParam = (String) testData.get("searchParams");
    boolean failure = (Boolean) testData.get("failure") == Boolean.TRUE;
    Url parsedUrl = null;
    try {
      parsedUrl = Url.create(input, base);
    } catch (Throwable t) {
      if (!failure) {
        Assertions.fail("error", t);
      }
    }
    if (!failure) {
      Assertions.assertThat(parsedUrl).isNotNull();
      Assertions.assertThat(parsedUrl.protocol()).isEqualTo(protocol);
      Assertions.assertThat(parsedUrl.host()).isEqualTo(host);
      Assertions.assertThat(parsedUrl.port()).isEqualTo(port);
      Assertions.assertThat(parsedUrl.hostname()).isEqualTo(hostname);
      Assertions.assertThat(parsedUrl.username()).isEqualTo(username);
      Assertions.assertThat(parsedUrl.password()).isEqualTo(password);
      Assertions.assertThat(parsedUrl.pathname()).isEqualTo(pathname);
      Assertions.assertThat(parsedUrl.search()).isEqualTo(search);
      Assertions.assertThat(parsedUrl.hash()).isEqualTo(hash);
      Assertions.assertThat(parsedUrl.href()).isEqualTo(href);
      if (origin != null) {
        Assertions.assertThat(parsedUrl.origin()).isEqualTo(origin);
      }
      if (searchParam != null) {
        Assertions.assertThat(parsedUrl.searchParams().toString()).isEqualTo(searchParam);
      }
    }
  }

  @ParameterizedTest
  @MethodSource("testSettersData")
  public void testSetters(Map<String, Object> testData) {
    String comment = (String) testData.get("comment");
    String setterName = (String) testData.get("setterName");
    String href = (String) testData.get("href");
    String newValue = (String) testData.get("new_value");
    Map<String, String> expected = (Map<String, String>) testData.get("expected");
    // parse the url
    Url parsedUrl = Url.create(href);
    Assertions.assertThat(parsedUrl).isNotNull();
    switch (setterName) {
      case "hash":
        parsedUrl.hash(newValue);
        break;
      case "host":
        parsedUrl.host(newValue);
        break;
      case "href":
        parsedUrl.href(newValue);
        break;
      case "hostname":
        parsedUrl.hostname(newValue);
        break;
      case "pathname":
        parsedUrl.pathname(newValue);
        break;
      case "password":
        parsedUrl.password(newValue);
        break;
      case "port":
        parsedUrl.port(newValue);
        break;
      case "protocol":
        parsedUrl.protocol(newValue);
        break;
      case "search":
        parsedUrl.search(newValue);
        break;
      case "username":
        parsedUrl.username(newValue);
        break;
      default:
        Assertions.fail("unknown setter name " + setterName);
        break;
    }
    assertUrlProperties(parsedUrl, expected);
  }
}
