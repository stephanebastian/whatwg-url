package io.github.stephanebastian.whatwg.url.impl;

import io.github.stephanebastian.whatwg.url.Url;
import java.util.Collection;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class TestHost {
  static Collection<Map<String, Object>> invalidIpv4Data() {
    return TestUtils.readJsonFile("ipv4-invalid.json");
  }

  static Collection<Map<String, Object>> invalidIpv6Data() {
    return TestUtils.readJsonFile("ipv6-invalid.json");
  }

  static Collection<Map<String, Object>> validIpv4Data() {
    return TestUtils.readJsonFile("ipv4-valid.json");
  }

  static Collection<Map<String, Object>> validIpv6Data() {
    return TestUtils.readJsonFile("ipv6-valid.json");
  }

  /**
   * EXAMPLE.COM example.com (domain) EXAMPLE.COM (opaque host) example%2Ecom example%2Ecom (opaque
   * host) faß.example xn--fa-hia.example (domain) fa%C3%9F.example (opaque host) 0 0.0.0.0 (IPv4) 0
   * (opaque host) %30 %30 (opaque host) 0x 0x (opaque host) 0xffffffff 255.255.255.255 (IPv4)
   * 0xffffffff (opaque host) [0:0::1] [::1] (IPv6) [0:0::1%5D Failure [0:0::%31] 09 Failure 09
   * (opaque host) example.255 example.255 (opaque host) example^example Failure
   */
  @Test
  public void hostParser() {
    // isNotSpecial = false
    Assertions.assertThat(HostParser.parse("EXAMPLE.COM", false, error -> {
    })).isInstanceOf(Domain.class).hasToString("example.com");
    Assertions.assertThat(HostParser.parse("example%2Ecom", false, error -> {
    })).isInstanceOf(Domain.class).hasToString("example.com");
    Assertions.assertThat(HostParser.parse("faß.example", false, error -> {
    })).isInstanceOf(Domain.class).hasToString("xn--fa-hia.example");
    Assertions.assertThat(HostParser.parse("0", false, error -> {
    })).isInstanceOf(Ipv4Address.class).hasToString("0.0.0.0");
    Assertions.assertThat(HostParser.parse("%30", false, error -> {
    })).isInstanceOf(Ipv4Address.class).hasToString("0.0.0.0");
    Assertions.assertThat(HostParser.parse("0x", false, error -> {
    })).isInstanceOf(Ipv4Address.class).hasToString("0.0.0.0");
    Assertions.assertThat(HostParser.parse("0xffffffff", false, error -> {
    })).isInstanceOf(Ipv4Address.class).hasToString("255.255.255.255");
    Assertions.assertThat(HostParser.parse("[0:0::1]", false, error -> {
    })).isInstanceOf(Ipv6Address.class).hasToString("::1");
    // isNotSpecial = true
    Assertions.assertThat(HostParser.parse("EXAMPLE.COM", true, error -> {
    })).isInstanceOf(OpaqueHost.class).hasToString("EXAMPLE.COM");
    Assertions.assertThat(HostParser.parse("example%2Ecom", true, error -> {
    })).isInstanceOf(OpaqueHost.class).hasToString("example%2Ecom");
    Assertions.assertThat(HostParser.parse("faß.example", true, error -> {
    })).isInstanceOf(OpaqueHost.class).hasToString("fa%C3%9F.example");
    Assertions.assertThat(HostParser.parse("0", true, error -> {
    })).isInstanceOf(OpaqueHost.class).hasToString("0");
    Assertions.assertThat(HostParser.parse("%30", true, error -> {
    })).isInstanceOf(OpaqueHost.class).hasToString("%30");
    Assertions.assertThat(HostParser.parse("0x", true, error -> {
    })).isInstanceOf(OpaqueHost.class).hasToString("0x");
    Assertions.assertThat(HostParser.parse("0xffffffff", true, error -> {
    })).isInstanceOf(OpaqueHost.class).hasToString("0xffffffff");
    Assertions.assertThat(HostParser.parse("[0:0::1]", true, error -> {
    })).isInstanceOf(Ipv6Address.class).hasToString("::1");
    // failure
    Assertions.assertThatExceptionOfType(UrlException.class)
        .isThrownBy(() -> HostParser.parse("xn--", false, error -> {
        }));
  }

  @Test
  public void serializeUrlSearchParams() {
    Url url = Url.create("http://myhost.com?a=a2&b=b1&a=a1&c=c1");
    String result = url.searchParams().toString();
    Assertions.assertThat(result).isNotEmpty();
    Assertions.assertThat(result).isEqualTo("a=a2&b=b1&a=a1&c=c1");
  }

  @ParameterizedTest
  @MethodSource("invalidIpv4Data")
  public void testInvalidIpv4Data(Map<String, String> testData) {
    String ip = testData.get("ip");
    Assertions.assertThatExceptionOfType(UrlException.class).isThrownBy(() -> {
      ErrorHandler errorHandler = new ErrorHandler();
      Ipv4Address result = HostParser.parseIpv4(ip, errorHandler::error);
      Assertions.assertThat(!errorHandler.errors().isEmpty() || result == null).isTrue();
      Assertions.assertThat(errorHandler.errors().size()).isGreaterThan(0);
    });
  }

  @ParameterizedTest
  @MethodSource("invalidIpv6Data")
  public void testInvalidIpv6(Map<String, String> testData) {
    String ip = testData.get("ip");
    Assertions.assertThatExceptionOfType(UrlException.class)
        .isThrownBy(() -> HostParser.parseIpv6(ip));
  }

  @ParameterizedTest
  @MethodSource("validIpv4Data")
  public void testValidIpv4Data(Map<String, String> testData) {
    String ip = testData.get("ip");
    String ipExpected = testData.get("expected");
    Assertions.assertThatNoException().isThrownBy(() -> {
      ErrorHandler errorHandler = new ErrorHandler();
      Ipv4Address result = HostParser.parseIpv4(ip, errorHandler::error);
      Assertions.assertThat(result).isNotNull();
      // Commented out as those are not actual errors but rather validation
      // information. Errors are
      // thrown as exception
      // Assert.assertEquals(0, validationErrors.size());
      StringBuilder formattedIp = new StringBuilder();
      SerializerHelper.serializeHost(result, formattedIp);
      Assertions.assertThat(formattedIp.toString()).isEqualTo(ipExpected);
    });
  }

  @ParameterizedTest
  @MethodSource("validIpv6Data")
  public void testValidIpv6(Map<String, String> testData) {
    String ip = testData.get("ip");
    String ipExpected = testData.get("expected");
    Assertions.assertThatNoException().isThrownBy(() -> {
      Ipv6Address result = HostParser.parseIpv6(ip);
      Assertions.assertThat(result).isNotNull();
      StringBuilder formattedIp = new StringBuilder();
      SerializerHelper.serializeHost(result, formattedIp);
      Assertions.assertThat(formattedIp.toString()).isEqualTo(ipExpected);
    });
  }
}
