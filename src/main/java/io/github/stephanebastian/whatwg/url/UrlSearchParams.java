package io.github.stephanebastian.whatwg.url;

import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * This interface represents <a href="https://url.spec.whatwg.org/#interface-urlsearchparams">the
 * URLSearchParams interface defined by the WhatWg specification</a>. <br>
 *
 * Although the spec support a public constructor, the current implementation does not provide one.
 * The main reason is that it doesn't seem relevant or appropriate for the java implementation. This
 * may be revisited in the future
 *
 * @author <a href="mail://stephane.bastian.dev@gmail.com">Stephane Bastian</a>
 */
public interface UrlSearchParams {
  /**
   * Append the specified parameter name and value to the list.
   *
   * @param name the parameter name
   * @param value the parameter value
   * @return a reference to this to support a fluent api
   */
  UrlSearchParams append(String name, String value);

  /**
   * Delete all parameter values whose name matches the given name
   *
   * @param name the parameter name
   * @return all values that have been deleted
   */
  Collection<String> delete(String name);

  /**
   * Delete all parameter name-value pairs whose name and value match the given name and value
   *
   * @param name the parameter name
   * @param value the parameter value
   * @return true if values where deleted, false otherwise
   */
  boolean delete(String name, String value);

  /**
   * List all entries, one by one, by calling the given consumer
   * 
   * @param consumer the bi-consumer to get each value
   * @return a reference to this to support a fluent api
   */
  UrlSearchParams entries(BiConsumer<String, String> consumer);

  /**
   * Return the value of the first name-value pair whose name match the given name
   *
   * @param name the parameter name
   * @return the parameter value or null
   *
   */
  String get(String name);

  /**
   * Return all parameter values whose name match the given name
   *
   * @param name the parameter name
   * @return matching values
   */
  Collection<String> getAll(String name);

  /**
   * Return whether the given parameter name exists.
   *
   * @param name the name of the parameter
   * @return a boolean value
   */
  boolean has(String name);

  /**
   * Return whether there is a name-value pair matching the given parameter name and value.
   *
   * @param name the parameter name
   * @param value the parameter value
   * @return a boolean value
   */
  boolean has(String name, String value);

  /**
   * If this list contains any name-value pair whose name matches the given name, then set the value
   * of the first item to the given value and remove others values. Otherwise, append the given name
   * and value to the list
   *
   * @param name the parameter name
   * @param value the parameter value
   * @return a reference to this to support a fluent api
   */
  UrlSearchParams set(String name, String value);

  /**
   * Return the number of parameters. The size getter steps are to return this’s list’s size.
   *
   * @return the number of parameters
   */
  int size();

  /**
   * Sort all name-value pairs by their names. The relative order between name-value pairs with
   * equal names is preserved
   *
   * @return a reference to this to support a fluent api
   */
  UrlSearchParams sort();
}
