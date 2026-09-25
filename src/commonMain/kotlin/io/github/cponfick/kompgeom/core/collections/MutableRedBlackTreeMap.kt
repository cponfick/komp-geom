package io.github.cponfick.kompgeom.core.collections

/**
 * A [MutableSortedMap] implementation backed by a Red-Black tree.
 *
 * Keys are ordered by [comparator]. The default comparator uses the natural ordering of [K]. A
 * comparator that returns zero for unequal keys cannot represent both keys: inserting the latter
 * replaces the former. Lookups still require key equality, so map equality remains symmetric and
 * follows the `Map` contract.
 *
 * Provides O(log n) time for [get], [put], [remove], [containsKey], [floor], [ceiling], [higher],
 * [lower], [firstKey], and [lastKey] operations.
 *
 * @param K The type of keys maintained by this map.
 * @param V The type of mapped values.
 * @param comparator The ordering used by the tree. Defaults to the natural ordering of [K].
 */
public class MutableRedBlackTreeMap<K : Comparable<K>, V>(
  private val comparator: Comparator<in K> = Comparator { a, b -> a.compareTo(b) }
) : MutableSortedMap<K, V> {

  private var root: Node? = null
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
    var result: K? = null
    var current = root
    while (current != null) {
      val cmp = comparator.compare(key, current.key)
      if (cmp > 0) {
        result = current.key
        current = current.right
      } else {
        current = current.left
      }
    }
    return result
  }

  override fun floor(key: K): K? {
    var result: K? = null
    var current = root
    while (current != null) {
      val cmp = comparator.compare(key, current.key)
      when {
        cmp == 0 -> return current.key
        cmp > 0 -> {
          result = current.key
          current = current.right
        }
        else -> current = current.left
      }
    }
    return result
  }

  override fun ceiling(key: K): K? {
    var result: K? = null
    var current = root
    while (current != null) {
      val cmp = comparator.compare(key, current.key)
      when {
        cmp == 0 -> return current.key
        cmp < 0 -> {
          result = current.key
          current = current.left
        }
        else -> current = current.right
      }
    }
    return result
  }

  override fun higher(key: K): K? {
    var result: K? = null
    var current = root
    while (current != null) {
      val cmp = comparator.compare(key, current.key)
      if (cmp < 0) {
        result = current.key
        current = current.left
      } else {
        current = current.right
      }
    }
    return result
  }

  override val size: Int
    get() = _size

  override fun isEmpty(): Boolean = root == null

  override fun containsKey(key: K): Boolean = getNode(key) != null

  override fun containsValue(value: V): Boolean = containsValue(root, value)

  private fun containsValue(node: Node?, value: V): Boolean {
    node ?: return false
      return node.value == value || containsValue(node.left, value) || containsValue(node.right, value)
  }

  override fun get(key: K): V? = getNode(key)?.value

  private fun getNode(key: K): Node? {
    val node = findNodeByOrder(key) ?: return null
    return node.takeIf { it.key == key }
  }

  private fun findNodeByOrder(key: K): Node? {
    var x = root
    while (x != null) {
      val cmp = comparator.compare(key, x.key)
      x =
        when {
          cmp < 0 -> x.left
          cmp > 0 -> x.right
          else -> return x
        }
    }
    return null
  }

  override val keys: MutableSet<K>
    get() = KeySet()

  override val values: MutableCollection<V>
    get() = Values()

  override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
    get() = EntrySet()

  private fun inOrderKeys(): MutableList<K> {
    val result = mutableListOf<K>()
    fun collect(node: Node?) {
      node ?: return
      collect(node.left)
      result.add(node.key)
      collect(node.right)
    }
    collect(root)
    return result
  }

  private inner class KeySet : AbstractMutableSet<K>() {
    override val size: Int
      get() = _size

    override fun add(element: K): Boolean = throw UnsupportedOperationException()

    override fun contains(element: K): Boolean = containsKey(element)

    override fun iterator(): MutableIterator<K> = KeyIterator()

    override fun remove(element: K): Boolean {
      if (!containsKey(element)) return false
      this@MutableRedBlackTreeMap.remove(element)
      return true
    }

    override fun clear() {
      this@MutableRedBlackTreeMap.clear()
    }
  }

  private inner class Values : AbstractMutableCollection<V>() {
    override val size: Int
      get() = _size

    override fun add(element: V): Boolean = throw UnsupportedOperationException()

    override fun contains(element: V): Boolean = containsValue(element)

    override fun iterator(): MutableIterator<V> = ValueIterator()

    override fun clear() {
      this@MutableRedBlackTreeMap.clear()
    }
  }

  private inner class EntrySet : AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {
    override val size: Int
      get() = _size

    override fun add(element: MutableMap.MutableEntry<K, V>): Boolean =
      throw UnsupportedOperationException()

    override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean {
      val node = getNode(element.key) ?: return false
      return node.value == element.value
    }

    override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> = EntryIterator()

    override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean {
      val node = getNode(element.key) ?: return false
      if (node.value != element.value) return false
      this@MutableRedBlackTreeMap.remove(element.key)
      return true
    }

    override fun clear() {
      this@MutableRedBlackTreeMap.clear()
    }
  }

  private abstract inner class TreeIterator<T> : MutableIterator<T> {
    protected val orderedKeys = inOrderKeys()
    protected var cursor = 0
    private var lastReturned = -1

    override fun hasNext(): Boolean = cursor < orderedKeys.size

    protected fun advanceKey(): K {
      if (!hasNext()) throw NoSuchElementException()
      lastReturned = cursor
      return orderedKeys[cursor++]
    }

    override fun remove() {
      check(lastReturned >= 0) { "Call next() before remove()" }
      this@MutableRedBlackTreeMap.remove(orderedKeys[lastReturned])
      lastReturned = -1
    }
  }

  private inner class KeyIterator : TreeIterator<K>() {
    override fun next(): K = advanceKey()
  }

  private inner class ValueIterator : TreeIterator<V>() {
    override fun next(): V = getNode(advanceKey())!!.value
  }

  private inner class EntryIterator : TreeIterator<MutableMap.MutableEntry<K, V>>() {
    override fun next(): MutableMap.MutableEntry<K, V> = LiveEntry(getNode(advanceKey())!!)
  }

  /**
   * An entry remains attached to its tree node. If that node is removed, the entry becomes a
   * detached snapshot: its key and value do not change, and [setValue] no longer changes the map.
   */
  private inner class LiveEntry(private val node: Node) : MutableMap.MutableEntry<K, V> {
    override val key: K
      get() = node.key

    override val value: V
      get() = node.value

    override fun setValue(newValue: V): V {
      val old = node.value
      if (!containsNode(root, node)) return old
      node.value = newValue
      return old
    }

    private fun containsNode(current: Node?, target: Node): Boolean {
      current ?: return false
        return current === target || containsNode(current.left, target) || containsNode(current.right, target)
    }

    override fun hashCode(): Int = key.hashCode() xor value.hashCode()

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other is MutableMap.MutableEntry<*, *>) return key == other.key && value == other.value
      if (other is Map.Entry<*, *>) return key == other.key && value == other.value
      return false
    }

    override fun toString(): String = "$key=$value"
  }

  override fun put(key: K, value: V): V? {
    val existingNode = findNodeByOrder(key)
    val old = existingNode?.takeIf { it.key == key }?.value
    root = put(root, key, value)
    root?.color = BLACK
    if (existingNode == null) _size++
    return old
  }

  override fun remove(key: K): V? {
    val existingNode = getNode(key) ?: return null
    val old = existingNode.value
    if (!isRed(root?.left) && !isRed(root?.right)) root?.color = RED
    root = delete(root!!, key)
    root?.color = BLACK
    _size--
    return old
  }

  override fun putAll(from: Map<out K, V>) {
    from.forEach { put(it.key, it.value) }
  }

  override fun clear() {
    root = null
    _size = 0
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
    h.color = !h.color
    h.left!!.color = !h.left!!.color
    h.right!!.color = !h.right!!.color
  }

  private fun balance(h: Node): Node {
    var n = h
    if (isRed(n.right) && !isRed(n.left)) n = rotateLeft(n)
    if (isRed(n.left) && isRed(n.left?.left)) n = rotateRight(n)
    if (isRed(n.left) && isRed(n.right)) flipColors(n)
    return n
  }

  private fun moveRedLeft(h: Node): Node {
    var n = h
    flipColors(n)
    if (isRed(n.right?.left)) {
      n.right = rotateRight(n.right!!)
      n = rotateLeft(n)
      flipColors(n)
    }
    return n
  }

  private fun moveRedRight(h: Node): Node {
    var n = h
    flipColors(n)
    if (isRed(n.left?.left)) {
      n = rotateRight(n)
      flipColors(n)
    }
    return n
  }

  private fun min(h: Node): Node {
    var x = h
    while (x.left != null) x = x.left!!
    return x
  }

  private fun deleteMin(h: Node): Node? {
    if (h.left == null) return h.right
    var n = h
    if (!isRed(n.left) && !isRed(n.left?.left)) n = moveRedLeft(n)
    n.left = deleteMin(n.left!!)
    return balance(n)
  }

  private fun delete(h: Node, key: K): Node? {
    var n = h
    if (comparator.compare(key, n.key) < 0) {
      if (!isRed(n.left) && !isRed(n.left?.left)) n = moveRedLeft(n)
      n.left = delete(n.left!!, key)
      return balance(n)
    }
    return deleteRight(n, key)
  }

  private fun deleteRight(h: Node, key: K): Node? {
    var n = h
    if (isRed(n.left)) n = rotateRight(n)
    if (comparator.compare(key, n.key) == 0 && n.right == null) return null
    if (!isRed(n.right) && !isRed(n.right?.left)) n = moveRedRight(n)
    if (comparator.compare(key, n.key) == 0) {
      // Move the successor node instead of copying its fields. An entry referring to the
      // removed node must not unexpectedly start referring to the successor key.
      val successor = min(n.right!!)
      val right = deleteMin(n.right!!)
      successor.left = n.left
      successor.right = right
      successor.color = n.color
      n = successor
    } else {
      n.right = delete(n.right!!, key)
    }
    return balance(n)
  }

  private fun put(h: Node?, key: K, value: V): Node {
    if (h == null) return Node(key, value)

    val cmp = comparator.compare(key, h.key)
    when {
      cmp < 0 -> h.left = put(h.left, key, value)
      cmp > 0 -> h.right = put(h.right, key, value)
      else -> h.value = value
    }

    // Fix-up strategy (LLRB specific order)
    return balance(h)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other is MutableSortedMap<*, *>) return sameEntries(other)
    if (other is Map<*, *>) return sameEntries(other)
    return false
  }

  private fun sameEntries(other: Map<*, *>): Boolean {
    return size == other.size &&
      entries.all { entry ->
        other.entries.any { otherEntry ->
          entry.key == otherEntry.key && entry.value == otherEntry.value
        }
      }
  }

  override fun hashCode(): Int {
    var h = 0
    fun addHash(node: Node?) {
      node ?: return
      addHash(node.left)
      h += node.key.hashCode() xor node.value.hashCode()
      addHash(node.right)
    }
    addHash(root)
    return h
  }

  override fun toString(): String {
    val sb = StringBuilder("{")
    var first = true
    fun append(node: Node?) {
      node ?: return
      append(node.left)
      if (!first) sb.append(", ")
      sb.append("${node.key}=${node.value}")
      first = false
      append(node.right)
    }
    append(root)
    sb.append("}")
    return sb.toString()
  }

  private companion object {
    const val RED = true
    const val BLACK = false
  }
}
