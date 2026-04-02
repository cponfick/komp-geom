package io.github.cponfick.kompgeom.core.collections

/**
 * A map that maintains its entries sorted by key according to a [Comparator].
 *
 * Extends [Map] with neighbor queries ([floor], [ceiling], [higher], [lower]) and extremum access
 * ([firstKey], [lastKey]) for finding adjacent keys.
 *
 * @param K The type of keys maintained by this map.
 * @param V The type of mapped values.
 * @see MutableSortedMap
 */
public interface SortedMap<K, out V> : Map<K, V> {

  /**
   * Returns the least key in this map, or `null` if the map is empty.
   *
   * @return The least key, or `null` if the map is empty.
   */
  public fun firstKey(): K?

  /**
   * Returns the greatest key in this map, or `null` if the map is empty.
   *
   * @return The greatest key, or `null` if the map is empty.
   */
  public fun lastKey(): K?

  /**
   * Returns the greatest key strictly less than the given [key], or `null` if there is no such key.
   *
   * @param key The reference key.
   * @return The greatest key strictly less than [key], or `null`.
   */
  public fun lower(key: K): K?

  /**
   * Returns the greatest key less than or equal to the given [key], or `null` if there is no such
   * key.
   *
   * @param key The reference key.
   * @return The greatest key less than or equal to [key], or `null`.
   */
  public fun floor(key: K): K?

  /**
   * Returns the least key greater than or equal to the given [key], or `null` if there is no such
   * key.
   *
   * @param key The reference key.
   * @return The least key greater than or equal to [key], or `null`.
   */
  public fun ceiling(key: K): K?

  /**
   * Returns the least key strictly greater than the given [key], or `null` if there is no such key.
   *
   * @param key The reference key.
   * @return The least key strictly greater than [key], or `null`.
   */
  public fun higher(key: K): K?
}
