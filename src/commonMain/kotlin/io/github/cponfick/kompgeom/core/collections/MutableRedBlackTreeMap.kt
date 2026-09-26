package io.github.cponfick.kompgeom.core.collections

/**
 * A [MutableSortedMap] implementation backed by a Red-Black tree.
 *
 * Keys are ordered by [comparator]. By default, keys use their natural ordering. Keys comparing as
 * zero identify the same mapping; replacing its value retains the original key. For
 * interoperability with ordinary maps, the comparator should be consistent with key equality. The
 * comparator must impose a stable total order while keys are in the map; do not mutate keys in ways
 * that change their ordering. Null values are supported, but null keys are not.
 *
 * [firstKey], [lastKey] and neighbor queries return `null` when no matching key exists. Iterators
 * traverse in key order and are fail-fast on structural modifications other than their own
 * [MutableIterator.remove]; value replacements do not invalidate iterators. Iteration takes O(n)
 * time and O(log n) auxiliary space. [containsValue] takes O(n) time. [get], [put], [remove],
 * [containsKey], [floor], [ceiling], [higher], [lower], [firstKey], and [lastKey] take O(log n).
 *
 * @param K The type of keys maintained by this map.
 * @param V The type of mapped values.
 * @param comparator The ordering used by the tree. Defaults to natural ordering (keys must then
 *   implement [Comparable]); supply a comparator for other key types.
 */
public class MutableRedBlackTreeMap<K, V>(
  private val comparator: Comparator<in K> = Comparator { a, b -> naturalCompare(a, b) }
) : MutableSortedMap<K, V> {

  private var root: Node? = null
  private var _size: Int = 0
  private var modCount: Int = 0

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
    requireNotNull(key) { NULL_KEY_MESSAGE }
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
    requireNotNull(key) { NULL_KEY_MESSAGE }
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
    requireNotNull(key) { NULL_KEY_MESSAGE }
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
    requireNotNull(key) { NULL_KEY_MESSAGE }
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

  override fun containsValue(value: V): Boolean = values.any { it == value }

  override fun get(key: K): V? = getNode(key)?.value

  private fun getNode(key: K): Node? {
    requireNotNull(key) { NULL_KEY_MESSAGE }
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
    private val pending = ArrayDeque<Node>()
    private var expectedModCount = modCount
    private var lastReturned: Node? = null

    init {
      pushLeft(root)
    }

    private fun pushLeft(start: Node?) {
      var node = start
      while (node != null) {
        pending.addLast(node)
        node = node.left
      }
    }

    protected fun advance(): Node {
      checkForModification()
      if (pending.isEmpty()) throw NoSuchElementException()
      val node = pending.removeLast()
      pushLeft(node.right)
      lastReturned = node
      return node
    }

    private fun checkForModification() {
      if (modCount != expectedModCount) throw ConcurrentModificationException()
    }

    override fun hasNext(): Boolean {
      checkForModification()
      return pending.isNotEmpty()
    }

    override fun remove() {
      checkForModification()
      val node = lastReturned ?: throw IllegalStateException("Call next() before remove()")
      this@MutableRedBlackTreeMap.remove(node.key)
      // Deletion can rotate or transplant nodes that were already on the traversal stack.
      // Rebuild the path to the next key in the updated tree.
      pending.clear()
      var current = root
      while (current != null) {
        if (comparator.compare(current.key, node.key) > 0) {
          pending.addLast(current)
          current = current.left
        } else {
          current = current.right
        }
      }
      expectedModCount = modCount
      lastReturned = null
    }
  }

  private inner class KeyIterator : TreeIterator<K>() {
    override fun next(): K = advance().key
  }

  private inner class ValueIterator : TreeIterator<V>() {
    override fun next(): V = advance().value
  }

  private inner class EntryIterator : TreeIterator<MutableMap.MutableEntry<K, V>>() {
    override fun next(): MutableMap.MutableEntry<K, V> = LiveEntry(advance())
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
      if (getNode(node.key) === node) node.value = newValue
      return old
    }

    override fun hashCode(): Int = key.hashCode() xor value.hashCode()

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      val entry = other as? Map.Entry<*, *> ?: return false
      return key == entry.key && value == entry.value
    }

    override fun toString(): String = "$key=$value"
  }

  override fun put(key: K, value: V): V? {
    requireNotNull(key) { NULL_KEY_MESSAGE }
    // Also validate natural ordering for the first key, before adding it to an empty tree.
    if (root == null) comparator.compare(key, key)
    val existingNode = getNode(key)
    val old = existingNode?.value
    root = put(root, key, value)
    root?.color = BLACK
    if (existingNode == null) {
      _size++
      modCount++
    }
    return old
  }

  override fun remove(key: K): V? {
    val existingNode = getNode(key) ?: return null
    val old = existingNode.value
    if (!isRed(root?.left) && !isRed(root?.right)) root?.color = RED
    root = delete(root!!, key)
    root?.color = BLACK
    _size--
    modCount++
    return old
  }

  override fun putAll(from: Map<out K, V>) {
    from.forEach { put(it.key, it.value) }
  }

  override fun clear() {
    if (root != null) modCount++
    root = null
    _size = 0
  }

  // Internal diagnostic for cross-platform invariant tests.
  internal fun hasValidRedBlackInvariants(): Boolean {
    if (isRed(root)) return false
    var nodes = 0
    fun blackHeight(node: Node?, lower: K?, upper: K?): Int {
      node ?: return 1
      nodes++
      if (
        (lower != null && comparator.compare(node.key, lower) <= 0) ||
          (upper != null && comparator.compare(node.key, upper) >= 0) ||
          (isRed(node) && (isRed(node.left) || isRed(node.right)))
      )
        return -1
      val left = blackHeight(node.left, lower, node.key)
      val right = blackHeight(node.right, node.key, upper)
      if (left < 0 || left != right) return -1
      return left + if (isRed(node)) 0 else 1
    }
    return blackHeight(root, null, null) > 0 && nodes == _size
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
    val map = other as? Map<*, *> ?: return false
    return sameEntries(map)
  }

  private fun sameEntries(other: Map<*, *>): Boolean {
    return size == other.size &&
      entries.all { entry ->
        other.entries.any { otherEntry ->
          entry.key == otherEntry.key && entry.value == otherEntry.value
        }
      }
  }

  override fun hashCode(): Int = entries.sumOf { it.hashCode() }

  override fun toString(): String = entries.joinToString(prefix = "{", postfix = "}")

  private companion object {
    const val NULL_KEY_MESSAGE = "Null keys are not supported"
    const val RED = true
    const val BLACK = false

    @Suppress("UNCHECKED_CAST")
    fun <K> naturalCompare(a: K, b: K): Int =
      (a as? Comparable<K>
          ?: throw IllegalArgumentException("Keys need a comparator or natural ordering"))
        .compareTo(b)
  }
}
