package io.github.stephanebastian.whatwg.url;

import io.github.stephanebastian.whatwg.url.impl.UrlException;
import io.github.stephanebastian.whatwg.url.impl.UrlImpl;
import java.util.function.Consumer;

/**
 * This is the main interface of the project. It closely follows
 * <a href="https://url.spec.whatwg.org/#url-class">the URL interface defined by the WhatWg
 * specification</a>.<br/>
 * <br/>
 * To get an instance of a URL you can:
 * <ul>
 *   <li>Create a blank new url by calling UrlBuilder.create()</li>
 *   <li>Create a url from a string url by calling UrlBuilder.create("http://www.myurl.com")</li>
 *   <li>Create a url from a string url and a base Url by calling
 *   UrlBuilder.create("http://www.myurl.com", "http://www.mybaseurl.com")</li>
 *   <li>Create a url from a string url and a base Url as well as an error handler by calling
 *   UrlBuilder.create("http://www.myurl.com", "http://www.mybaseurl.com",
 *   error -&gt; {// do something with the error})</li>
 * </ul>
 *
 * @author <a href="mail://stephane.bastian.dev@gmail.com">Stephane Bastian</a>
 */
public interface Url {
  /**
   * Create a new empty Url
   *
   * @return a new Url
   */
  static Url create() {
    return create(null, null);
  }

  /**
   * Create a new Url from the specified input
   *
   * @param input the input to parse and create an Url from
   * @return a new Url
   */
  static Url create(String input) {
    return create(input, null);
  }

  /**
   * Create a new Url from the specified input and base url
   *
   * @param input the input to parse and create an Url from
   * @param baseUrl the base url
   * @return a new Url
   */
  static Url create(String input, String baseUrl) {
    return create(input, baseUrl, null);
  }

  /**
   * Create a new Url from the specified input, base url and error handler. <br/>
   * Note that this method is not defined by the spec but provide an easy way <br/>
   * of reporting potential errors
   *
   * @param input the input to parse and create an Url from
   * @param baseUrl the base url
   * @param errorHandler the error handler to register
   * @return new Url
   */
  static Url create(String input, String baseUrl, Consumer<String> errorHandler) {
    try {
      return UrlImpl.create(input, baseUrl);
    } catch (UrlException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Return the hash property.
   *
   * @return an empty string or '#' followed by the url fragment
   *
   */
  String hash();

  /**
   * Sets the hash property.
   *
   * @param value the hash to set
   * @return a reference to this to support a fluent api
   */
  Url hash(String value);

  /**
   * Return the host.
   *
   * @return the host
   */
  String host();

  /**
   * Set the host.
   *
   * @param value the host to set
   * @return a reference to this to support a fluent api
   */
  Url host(String value);

  /**
   * Return the hostname.
   *
   * @return the hostname
   */
  String hostname();

  /**
   * Set the hostname.
   *
   * @param value the hostname to set
   * @return a reference to this to support a fluent api
   */
  Url hostname(String value);

  /**
   * Return the href.
   *
   * @return the href
   */
  String href();

  /**
   * Set the href.
   *
   * @param value the href to set
   * @return a reference to this to support a fluent api
   */
  Url href(String value);

  /**
   * Return the serialized representation of this url origin
   *
   * @return the origin
   */
  String origin();

  /**
   * Return the password.
   *
   * @return the password
   */
  String password();

  /**
   * Set the password.
   *
   * @param value the password to set
   * @return a reference to this to support a fluent api
   */
  Url password(String value);

  /**
   * Return the pathname.
   *
   * @return the pathname
   */
  String pathname();

  /**
   * Set the pathname.
   *
   * @param value the pathname to set
   * @return a reference to this to support a fluent api
   */
  Url pathname(String value);

  /**
   * Return the port.
   *
   * @return the port
   */
  String port();

  /**
   * Set the port.
   *
   * @param value the port to set
   * @return a reference to this to support a fluent api
   */
  Url port(String value);

  /**
   * Return the protocol.
   *
   * @return the protocol
   */
  String protocol();

  /**
   * Set the protocol.
   *
   * @param value the protocol to set
   * @return a reference to this to support a fluent api
   */
  Url protocol(String value);

  /**
   * Return the search property.
   *
   * @return the search property
   */
  String search();

  /**
   * Set the search property.
   *
   * @param value the value to set
   * @return a reference to this to support a fluent api
   */
  Url search(String value);

  /**
   * Return the searchParams property.
   *
   * @return the {@link UrlSearchParams}
   */
  UrlSearchParams searchParams();

  /**
   * Return the username.
   *
   * @return the username
   */
  String username();

  /**
   * Set the username property.
   *
   * @param value the username to set
   * @return a reference to this to support a fluent api
   */
  Url username(String value);
}
