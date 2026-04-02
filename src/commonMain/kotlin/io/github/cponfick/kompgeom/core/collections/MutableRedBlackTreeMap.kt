package io.github.cponfick.kompgeom.core.collections

/**
 * A [MutableSortedMap] implementation backed by a Red-Black tree.
 *
 * Provides O(log n) time for [get], [put], [remove], [containsKey], [floor], [ceiling], [higher],
 * [lower], [firstKey], and [lastKey] operations.
 *
 * @param K The type of keys maintained by this map.
 * @param V The type of mapped values.
 */
public class MutableRedBlackTreeMap<K : Comparable<K>, V> : MutableSortedMap<K, V> {

  private var root: Node? = null
  private var putOldValue: V? = null
  private var _size: Int = 0

  override fun firstKey(): K? {
    var current = root
    while (current?.left != null) {
      current = current.left
    }
    return current?.key
  }

  override fun lastKey(): K? {
    var current = root
    while (current?.right != null) {
      current = current.right
    }
    return current?.key
  }

  override fun lower(key: K): K? {
    TODO("Not yet implemented")
  }

  override fun floor(key: K): K? {
    TODO("Not yet implemented")
  }

  override fun ceiling(key: K): K? {
    TODO("Not yet implemented")
  }

  override fun higher(key: K): K? {
    TODO("Not yet implemented")
  }

  override val size: Int
    get() = _size

  override fun isEmpty(): Boolean = root == null

  override fun containsKey(key: K): Boolean = get(key) != null

  override fun containsValue(value: V): Boolean = containsValue(root, value)

  private fun containsValue(node: Node?, value: V): Boolean {
    node ?: return false
    if (node.value == value) return true
    return containsValue(node.left, value) || containsValue(node.right, value)
  }

  override fun get(key: K): V? {
    var x = root
    while (x != null) {
      val cmp = key.compareTo(x.key)
      x =
        when {
          cmp < 0 -> x.left
          cmp > 0 -> x.right
          else -> return x.value
        }
    }
    return null
  }

  override val keys: MutableSet<K>
    get() {
      val result = mutableSetOf<K>()
      collectKeys(root, result)
      return result
    }

  override val values: MutableCollection<V>
    get() {
      val result = mutableListOf<V>()
      collectValues(root, result)
      return result
    }

  override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
    get() {
      val result = mutableSetOf<MutableMap.MutableEntry<K, V>>()
      collectEntries(root, result)
      return result
    }

  private fun collectKeys(node: Node?, dest: MutableSet<K>) {
    node ?: return
    collectKeys(node.left, dest)
    dest.add(node.key)
    collectKeys(node.right, dest)
  }

  private fun collectValues(node: Node?, dest: MutableList<V>) {
    node ?: return
    collectValues(node.left, dest)
    dest.add(node.value)
    collectValues(node.right, dest)
  }

  private fun collectEntries(node: Node?, dest: MutableSet<MutableMap.MutableEntry<K, V>>) {
    node ?: return
    collectEntries(node.left, dest)
    dest.add(Entry(node.key, node.value))
    collectEntries(node.right, dest)
  }

  private class Entry<K, V>(override val key: K, override var value: V) :
    MutableMap.MutableEntry<K, V> {
    override fun setValue(newValue: V): V {
      val old = value
      value = newValue
      return old
    }

    override fun hashCode(): Int = key.hashCode() xor value.hashCode()

    override fun equals(other: Any?): Boolean {
      if (other !is Map.Entry<*, *>) return false
      return key == other.key && value == other.value
    }

    override fun toString(): String = "$key=$value"
  }

  override fun put(key: K, value: V): V? {
    root = put(root, key, value)
    root?.color = BLACK
    val old = putOldValue
    putOldValue = null
    if (old == null) _size++
    return old
  }

  override fun remove(key: K): V? {
    TODO("Not yet implemented")
  }

  override fun putAll(from: Map<out K, V>) {
    from.forEach { put(it.key, it.value) }
  }

  override fun clear() {
    TODO("Not yet implemented")
  }

  private fun isRed(x: Node?): Boolean = x?.color == RED

  private inner class Node(
    var key: K,
    var value: V,
    var left: Node? = null,
    var right: Node? = null,
    var color: Boolean = RED,
  )

  private fun rotateLeft(h: Node): Node {
    val x = h.right!!
    h.right = x.left
    x.left = h
    x.color = h.color
    h.color = RED
    return x
  }

  private fun rotateRight(h: Node): Node {
    val x = h.left!!
    h.left = x.right
    x.right = h
    x.color = h.color
    h.color = RED
    return x
  }

  private fun flipColors(h: Node) {
    h.color = RED
    h.left?.color = BLACK
    h.right?.color = BLACK
  }

  private fun put(h: Node?, key: K, value: V): Node {
    if (h == null) return Node(key, value)

    val cmp = key.compareTo(h.key)
    when {
      cmp < 0 -> h.left = put(h.left, key, value)
      cmp > 0 -> h.right = put(h.right, key, value)
      else -> {
        putOldValue = h.value
        h.value = value
      }
    }

    // Fix-up strategy (LLRB specific order)
    var n = h
    if (isRed(n.right) && !isRed(n.left)) n = rotateLeft(n)
    if (isRed(n.left) && isRed(n.left?.left)) n = rotateRight(n)
    if (isRed(n.left) && isRed(n.right)) flipColors(n)

    return n
  }

  private companion object {
    const val RED = true
    const val BLACK = false
  }
}
