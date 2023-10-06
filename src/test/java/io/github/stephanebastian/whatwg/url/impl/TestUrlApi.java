package io.github.stephanebastian.whatwg.url.impl;

import io.github.stephanebastian.whatwg.url.Url;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestUrlApi {
  @Test
  public void createEmptyUrl() {
    Url url = Url.create();
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hash()).isEmpty();
    Assertions.assertThat(url.host()).isEmpty();
    Assertions.assertThat(url.hostname()).isEmpty();
    Assertions.assertThat(url.href()).isEqualTo(":");
    Assertions.assertThat(url.origin()).isEqualTo("null");
    Assertions.assertThat(url.password()).isEmpty();
    Assertions.assertThat(url.pathname()).isEmpty();
    Assertions.assertThat(url.protocol()).isEqualTo(":");
    Assertions.assertThat(url.port()).isEqualTo("");
    Assertions.assertThat(url.search()).isEmpty();
    Assertions.assertThat(url.searchParams()).isNotNull();
    Assertions.assertThat(url.searchParams().size()).isEqualTo(0);
    Assertions.assertThat(url.username()).isEmpty();
  }

  @Test
  public void createValidUrl() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hash()).isEqualTo("#hash1");
    Assertions.assertThat(url.host()).isEqualTo("www.myurl.com");
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    Assertions.assertThat(url.href()).isEqualTo("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url.origin()).isEqualTo("http://www.myurl.com");
    Assertions.assertThat(url.password()).isEmpty();
    Assertions.assertThat(url.pathname()).isEqualTo("/path1");
    Assertions.assertThat(url.port()).isEqualTo("");
    Assertions.assertThat(url.protocol()).isEqualTo("http:");
    Assertions.assertThat(url.search()).isEqualTo("?a=1&b=2");
    Assertions.assertThat(url.searchParams()).isNotNull();
    Assertions.assertThat(url.searchParams().size()).isEqualTo(2);
    Assertions.assertThat(url.username()).isEmpty();
  }

  @Test
  public void createValidRelativeUrl() {
    Url url = Url.create("path1?a=1&b=2#hash1", "http://www.myurl.com/path2?c=3&d=2#hash2");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hash()).isEqualTo("#hash1");
    Assertions.assertThat(url.host()).isEqualTo("www.myurl.com");
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    Assertions.assertThat(url.href()).isEqualTo("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url.origin()).isEqualTo("http://www.myurl.com");
    Assertions.assertThat(url.password()).isEmpty();
    Assertions.assertThat(url.pathname()).isEqualTo("/path1");
    Assertions.assertThat(url.port()).isEqualTo("");
    Assertions.assertThat(url.protocol()).isEqualTo("http:");
    Assertions.assertThat(url.search()).isEqualTo("?a=1&b=2");
    Assertions.assertThat(url.searchParams()).isNotNull();
    Assertions.assertThat(url.searchParams().size()).isEqualTo(2);
    Assertions.assertThat(url.username()).isEmpty();
  }

  @Test
  public void setValidHash() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hash()).isEqualTo("#hash1");
    url.hash("hash2");
    Assertions.assertThat(url.hash()).isEqualTo("#hash2");
  }

  @Test
  public void setValidHost() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.host()).isEqualTo("www.myurl.com");
    url.host("anotherhost.io");
    Assertions.assertThat(url.host()).isEqualTo("anotherhost.io");
  }

  @Test
  public void setValidHostname() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.hostname("anotherhost.io");
    Assertions.assertThat(url.hostname()).isEqualTo("anotherhost.io");
  }

  @Test
  public void setValidHref() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.href("http://www.anotherurl.io/path2?c=3&d=4#hash2");
    Assertions.assertThat(url.hash()).isEqualTo("#hash2");
    Assertions.assertThat(url.host()).isEqualTo("www.anotherurl.io");
    Assertions.assertThat(url.hostname()).isEqualTo("www.anotherurl.io");
    Assertions.assertThat(url.href()).isEqualTo("http://www.anotherurl.io/path2?c=3&d=4#hash2");
    Assertions.assertThat(url.origin()).isEqualTo("http://www.anotherurl.io");
    Assertions.assertThat(url.password()).isEmpty();
    Assertions.assertThat(url.pathname()).isEqualTo("/path2");
    Assertions.assertThat(url.protocol()).isEqualTo("http:");
    Assertions.assertThat(url.search()).isEqualTo("?c=3&d=4");
    Assertions.assertThat(url.searchParams()).isNotNull();
    Assertions.assertThat(url.searchParams().size()).isEqualTo(2);
    Assertions.assertThat(url.username()).isEmpty();
  }

  @Test
  public void setValidPassword() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.password("password2");
    Assertions.assertThat(url.password()).isEqualTo("password2");
  }

  @Test
  public void setValidPathname() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.pathname("path2/path3");
    Assertions.assertThat(url.pathname()).isEqualTo("/path2/path3");
    url.pathname("/path4");
    Assertions.assertThat(url.pathname()).isEqualTo("/path4");
  }

  @Test
  public void setValidPort() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.port("443");
    Assertions.assertThat(url.port()).isEqualTo("443");
    url.port("80");
    Assertions.assertThat(url.port()).isEqualTo("");
  }

  @Test
  public void setValidProtocol() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.protocol("ftp");
    Assertions.assertThat(url.protocol()).isEqualTo("ftp:");
    Assertions.assertThat(url.href()).isEqualTo("ftp://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url.origin()).isEqualTo("ftp://www.myurl.com");
  }

  @Test
  public void setValidSearch() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.hostname()).isEqualTo("www.myurl.com");
    url.search("c=3&d=4");
    Assertions.assertThat(url.search()).isEqualTo("?c=3&d=4");
  }

  @Test
  public void setValidUsername() {
    Url url = Url.create("http://www.myurl.com/path1?a=1&b=2#hash1");
    Assertions.assertThat(url).isNotNull();
    Assertions.assertThat(url.username()).isEmpty();
    url.username("ausername");
    Assertions.assertThat(url.username()).isEqualTo("ausername");
  }
}
