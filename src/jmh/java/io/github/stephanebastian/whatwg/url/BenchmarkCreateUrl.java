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
package io.github.stephanebastian.whatwg.url;

import io.github.stephanebastian.whatwg.url.impl.TestUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Thread)
public class BenchmarkCreateUrl {
  private List<Map<String, Object>> urls;
  private int urlIndex = 0;

  /**
   * we read urls from the test json file so we've got a variety of urls to process (various host:
   * domain, ipv4, ipv6, query, params, etc.)
   * 
   * @return
   */
  private Collection<Map<String, Object>> readUrls() {
    Collection<Object> tmp = TestUtils.readJsonFile("urltestdata.json");
    // remove comments
    tmp.removeIf(item -> !(item instanceof Map));
    Collection<Map<String, Object>> result = (Collection<Map<String, Object>>) (Object) tmp;
    result.removeIf(item -> item.containsKey("failure"));
    return result;
  }

  Map<String, Object> nextUrl() {
    urlIndex++;
    if (urlIndex >= urls.size()) {
      urlIndex = 0;
    }
    return urls.get(urlIndex);
  }

  @Setup(Level.Trial)
  public void beforeBenchmark() {
    urls = (List<Map<String, Object>>) readUrls();
    System.out.println("\n Before benchmark\n - urls size: " + urls.size());
  }

  @TearDown(Level.Trial)
  public void afterBenchmark() {
    System.out.println("\n After benchmark\n - urls size: " + urls.size());
  }

  private Url parse(Map<String, Object> map) {
    String input = (String) map.get("input");
    String base = (String) map.get("base");
    return Url.create(input, base);
  }

  @Benchmark
  public void doBenchmark(Blackhole blackhole) {
    blackhole.consume(parse(nextUrl()));
  }
}
