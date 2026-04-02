package io.github.cponfick.kompgeom.core.collections

/**
 * A mutable variant of [SortedMap] that supports insertion and removal of entries while maintaining
 * sorted key order.
 *
 * @param K The type of keys maintained by this map.
 * @param V The type of mapped values.
 * @see SortedMap
 */
public interface MutableSortedMap<K, V> : SortedMap<K, V>, MutableMap<K, V>
