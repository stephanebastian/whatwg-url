package io.github.stephanebastian.whatwg.url.impl;

import io.github.stephanebastian.whatwg.url.Url;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestUrlSearchParamsApi {
  @Test
  public void append() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2");
    url.searchParams().append("c", "3");
    Assertions.assertThat(url.href()).isEqualTo("http://www.myurl.com/path1?a=1&b=2&c=3");
    url.searchParams().append("b", "4");
    Assertions.assertThat(url.href()).isEqualTo("http://www.myurl.com/path1?a=1&b=2&c=3&b=4");
    url.searchParams().append("a", "5");
    Assertions.assertThat(url.href()).isEqualTo("http://www.myurl.com/path1?a=1&b=2&c=3&b=4&a=5");
  }

  @Test
  public void deleteByName() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2");
    url.searchParams().delete("a");
    Assertions.assertThat(url.href()).isEqualTo("http://www.myurl.com/path1?b=2");
  }

  @Test
  public void deleteByNameAndValue() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2&a=2");
    url.searchParams().delete("a", "2");
    Assertions.assertThat(url.href()).isEqualTo("http://www.myurl.com/path1?a=1&b=2");
    url.searchParams().delete("a", "1");
    Assertions.assertThat(url.href()).isEqualTo("http://www.myurl.com/path1?b=2");
    url.searchParams().delete("b", "2");
    Assertions.assertThat(url.href()).isEqualTo("http://www.myurl.com/path1");
  }

  @Test
  public void entries() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2&a=2");
    List<String[]> paramsCollector = new ArrayList<>();
    url.searchParams().entries((name, value) -> {
      paramsCollector.add(new String[] {name, value});
    });
    Assertions.assertThat(paramsCollector.size()).isEqualTo(3);
    Assertions.assertThat(paramsCollector.get(0)[0]).isEqualTo("a");
    Assertions.assertThat(paramsCollector.get(0)[1]).isEqualTo("1");
    Assertions.assertThat(paramsCollector.get(1)[0]).isEqualTo("b");
    Assertions.assertThat(paramsCollector.get(1)[1]).isEqualTo("2");
    Assertions.assertThat(paramsCollector.get(2)[0]).isEqualTo("a");
    Assertions.assertThat(paramsCollector.get(2)[1]).isEqualTo("2");
  }

  @Test
  public void get() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2&a=2");
    Assertions.assertThat(url.searchParams().get("a")).isEqualTo("1");
    Assertions.assertThat(url.searchParams().get("b")).isEqualTo("2");
  }

  @Test
  public void getAll() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2&a=2");
    Assertions.assertThat(url.searchParams().getAll("a")).isEqualTo(Arrays.asList("1", "2"));
    Assertions.assertThat(url.searchParams().getAll("b")).isEqualTo(Arrays.asList("2"));
  }

  @Test
  public void hasName() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2&a=2");
    Assertions.assertThat(url.searchParams().has("a")).isTrue();
    Assertions.assertThat(url.searchParams().has("b")).isTrue();
    Assertions.assertThat(url.searchParams().has("c")).isFalse();
  }

  @Test
  public void hasNameAndValue() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2&a=2");
    Assertions.assertThat(url.searchParams().has("a", "1")).isTrue();
    Assertions.assertThat(url.searchParams().has("a", "2")).isTrue();
    Assertions.assertThat(url.searchParams().has("a", "3")).isFalse();
    Assertions.assertThat(url.searchParams().has("e", "1")).isFalse();
  }

  @Test
  public void set() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2&a=2");
    url.searchParams().set("a", "5");
    Assertions.assertThat(url.href()).isEqualTo("http://www.myurl.com/path1?a=5&b=2");
    url.searchParams().set("c", "4");
    Assertions.assertThat(url.href()).isEqualTo("http://www.myurl.com/path1?a=5&b=2&c=4");
  }

  @Test
  public void size() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2&a=2");
    Assertions.assertThat(url.searchParams().size()).isEqualTo(3);
  }

  @Test
  public void sort() {
    Url url = Url.create("http://www.myurl.com/path1?z=26&a=3&b=2&a=1");
    url.searchParams().sort();
    Assertions.assertThat(url.href()).isEqualTo("http://www.myurl.com/path1?a=3&a=1&b=2&z=26");
  }
}
