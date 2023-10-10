package io.github.stephanebastian.whatwg.url;

import io.github.stephanebastian.whatwg.url.impl.UrlImpl;
import java.util.Collection;

/**
 * This is the main interface of the project. It closely follows
 * <a href="https://url.spec.whatwg.org/#url-class">the URL interface defined by the WhatWg
 * specification</a>.<br>
 * <br>
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
   * Return whether the given url can be parsed
   *
   * @param url the url to parse
   * @return true if the url can be parsed, false otherwise
   */
  static boolean canParse(String url) {
    return canParse(url, null);
  }

  /**
   * Return whether the given url and baseUrl can be parsed
   *
   * @param url the url to parse
   * @param baseUrl the baseUrl
   * @return true if the url can be parsed, false otherwise
   */
  static boolean canParse(String url, String baseUrl) {
    try {
      create(url, baseUrl);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

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
   * @exception ValidationException if the parse returns a failure
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
   * @exception ValidationException if the parse returns a failure
   */
  static Url create(String input, String baseUrl) {
    return UrlImpl.create(input, baseUrl);
  }

  /**
   * Return the hash property.
   *
   * @return an empty string or '#' followed by the url fragment
   *
   */
  String hash();

  /**
   * Set the hash property.
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
   * Return a Json representation of the url
   *
   * @return the json representation
   */
  String toJSON();

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

  /**
   * Return a collection of validation errors reported when parsing the raw url and/or setting properties
   * Note that this method is not explicitly specified by the WhatWg Url standard
   *
   * @return a collection of validation errors
   */
  Collection<ValidationError> validationErrors();
}
