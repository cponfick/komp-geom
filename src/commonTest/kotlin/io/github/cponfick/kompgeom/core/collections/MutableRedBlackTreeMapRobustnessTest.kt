package io.github.cponfick.kompgeom.core.collections

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertFailsWith

class MutableRedBlackTreeMapRobustnessTest {
  @Test
  fun `empty and singleton boundaries`() {
    val map = MutableRedBlackTreeMap<Int, String?>()
    map.firstKey() shouldBe null
    map.lastKey() shouldBe null
    for (key in listOf(-1, 0, 1)) {
      map.lower(key) shouldBe null
      map.floor(key) shouldBe null
      map.ceiling(key) shouldBe null
      map.higher(key) shouldBe null
    }
    map[0] = null
    map.size shouldBe 1
    map.containsKey(0) shouldBe true
    map.firstKey() shouldBe 0
    map.lastKey() shouldBe 0
    map.floor(0) shouldBe 0
    map.ceiling(0) shouldBe 0
    map.lower(0) shouldBe null
    map.higher(0) shouldBe null
    map.hasValidRedBlackInvariants() shouldBe true
    map.remove(0) shouldBe null
    map.isEmpty() shouldBe true
    map.hasValidRedBlackInvariants() shouldBe true
  }

  @Test
  fun `comparator equal keys refer to the same mapping`() {
    val map = MutableRedBlackTreeMap<RankedKey, Int>()
    val first = RankedKey(2, "first")
    val equivalent = RankedKey(2, "second")
    map.put(first, 10) shouldBe null
    map.put(equivalent, 20) shouldBe 10
    map.size shouldBe 1
    map.keys.single() shouldBe first
    map.containsKey(equivalent) shouldBe true
    map[equivalent] shouldBe 20
    map.floor(equivalent) shouldBe first
    map.remove(equivalent) shouldBe 20
    map.isEmpty() shouldBe true
  }

  @Test
  fun `descending comparator defines neighbors and traversal`() {
    val map = MutableRedBlackTreeMap<Int, Int>(compareByDescending { it })
    for (key in listOf(1, 5, 3)) map[key] = key
    map.keys.toList() shouldContainExactly listOf(5, 3, 1)
    map.firstKey() shouldBe 5
    map.lastKey() shouldBe 1
    map.floor(4) shouldBe 5
    map.ceiling(4) shouldBe 3
    map.lower(3) shouldBe 5
    map.higher(3) shouldBe 1
    map.hasValidRedBlackInvariants() shouldBe true
  }

  @Test
  fun `custom comparator supports non comparable keys`() {
    data class Key(val id: Int)
    val map = MutableRedBlackTreeMap<Key, String>(compareBy { it.id })
    map[Key(3)] = "three"
    map[Key(1)] = "one"
    map[Key(2)] = "two"
    map.keys.map { it.id } shouldContainExactly listOf(1, 2, 3)
    map.floor(Key(2)) shouldBe Key(2)
    map.remove(Key(1)) shouldBe "one"
    map.hasValidRedBlackInvariants() shouldBe true
  }

  @Test
  fun `null keys are rejected and nullable values are allowed`() {
    val map =
      MutableRedBlackTreeMap<Int?, String?>(Comparator { a, b -> (a ?: 0).compareTo(b ?: 0) })
    assertFailsWith<IllegalArgumentException> { map[null] = "null" }
    assertFailsWith<IllegalArgumentException> { map[null] }
    assertFailsWith<IllegalArgumentException> { map.remove(null) }
    assertFailsWith<IllegalArgumentException> { map.floor(null) }
    map[1] = null
    map.containsKey(1) shouldBe true
  }

  @Test
  fun `natural ordering rejects non comparable keys`() {
    data class Key(val id: Int)
    val map = MutableRedBlackTreeMap<Key, Int>()
    assertFailsWith<IllegalArgumentException> { map[Key(1)] = 1 }
    map.isEmpty() shouldBe true
  }

  @Test
  fun `insert and delete across different shapes`() {
    val orders = listOf((0..63).toList(), (63 downTo 0).toList(), (0..63).shuffled(Random(151)))
    for (order in orders) {
      val map = MutableRedBlackTreeMap<Int, Int>()
      for (key in order) {
        map[key] = key
        map.hasValidRedBlackInvariants() shouldBe true
      }
      for (key in listOf(31, 0, 63, 16, 32) + order) {
        map.remove(key)
        map.hasValidRedBlackInvariants() shouldBe true
      }
      map.isEmpty() shouldBe true
    }
  }

  @Test
  fun `randomized operations agree with sorted reference`() {
    for (seed in 0..3) {
      val random = Random(seed)
      val map = MutableRedBlackTreeMap<Int, Int?>()
      val reference = mutableMapOf<Int, Int?>()
      repeat(600) {
        val key = random.nextInt(-30, 31)
        when (random.nextInt(4)) {
          0,
          1 -> {
            val value = if (random.nextInt(5) == 0) null else random.nextInt()
            map.put(key, value) shouldBe reference.put(key, value)
          }
          2 -> map.remove(key) shouldBe reference.remove(key)
          else -> {
            map.containsKey(key) shouldBe reference.containsKey(key)
            map[key] shouldBe reference[key]
          }
        }
        val keys = reference.keys.sorted()
        map.hasValidRedBlackInvariants() shouldBe true
        map.size shouldBe keys.size
        map.keys.toList() shouldContainExactly keys
        map.entries.map { it.key to it.value } shouldContainExactly keys.map { it to reference[it] }
        map.values.toList() shouldContainExactly keys.map { reference[it] }
        map.firstKey() shouldBe keys.firstOrNull()
        map.lastKey() shouldBe keys.lastOrNull()
        for (probe in listOf(-31, key, 31)) {
          map.lower(probe) shouldBe keys.lastOrNull { it < probe }
          map.floor(probe) shouldBe keys.lastOrNull { it <= probe }
          map.ceiling(probe) shouldBe keys.firstOrNull { it >= probe }
          map.higher(probe) shouldBe keys.firstOrNull { it > probe }
        }
      }
    }
  }

  @Test
  fun `iterators remove current mapping and reject external structural changes`() {
    val map = MutableRedBlackTreeMap<Int, Int>()
    for (key in 0..20) map[key] = key
    val iterator = map.entries.iterator()
    for (key in 0..20) {
      iterator.hasNext() shouldBe true
      iterator.next().key shouldBe key
      if (key % 2 == 0) {
        iterator.remove()
        map.hasValidRedBlackInvariants() shouldBe true
        assertFailsWith<IllegalStateException> { iterator.remove() }
      }
    }
    iterator.hasNext() shouldBe false
    map.keys.toList() shouldContainExactly (1..20 step 2).toList()
    assertFailsWith<NoSuchElementException> { iterator.next() }

    val keys = map.keys.iterator()
    keys.next() shouldBe 1
    map[1] = 100 // A value replacement is not a structural change.
    keys.next() shouldBe 3
    map[22] = 22
    assertFailsWith<ConcurrentModificationException> { keys.hasNext() }
    assertFailsWith<ConcurrentModificationException> { keys.next() }
    assertFailsWith<ConcurrentModificationException> { keys.remove() }
  }

  private data class RankedKey(val rank: Int, val label: String) : Comparable<RankedKey> {
    override fun compareTo(other: RankedKey): Int = rank.compareTo(other.rank)
  }
}
