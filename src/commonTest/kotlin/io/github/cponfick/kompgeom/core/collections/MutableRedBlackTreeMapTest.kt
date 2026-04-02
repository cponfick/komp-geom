package io.github.cponfick.kompgeom.core.collections

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MutableRedBlackTreeMapTest {

  @Test
  fun `isEmpty returns true for new map`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map.isEmpty() shouldBe true
  }

  @Test
  fun `put adds element`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[1] = "1"
    map[1] shouldBe "1"
  }

  @Test
  fun `isEmpty returns false map with one element`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[5] = "five"
    map.isEmpty() shouldBe false
  }

  @Test
  fun `firstKey returns correct value when one element is in map`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[5] = "five"

    map.firstKey() shouldBe 5
  }

  @Test
  fun `firstKey returns least key when multiple elements are in map`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[5] = "five"
    map[3] = "three"
    map[7] = "seven"
    map[1] = "one"

    map.firstKey() shouldBe 1
  }

  @Test
  fun `lastKey returns correct value when one element is in map`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[5] = "five"
    map.lastKey() shouldBe 5
  }

  @Test
  fun `lastKey returns greatest key when multiple elements are in map`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[5] = "five"
    map[3] = "three"
    map[7] = "seven"
    map[1] = "one"

    map.lastKey() shouldBe 7
  }

  @Test
  fun `putAll puts elements into map`() {
    val actual = MutableRedBlackTreeMap<Int, String>()
    val inputMap = mapOf(1 to "one", 2 to "two", 3 to "three", 4 to "four", 5 to "five", 6 to "six")

    actual.putAll(inputMap)

    actual[1] shouldBe "one"
    actual[2] shouldBe "two"
    actual[3] shouldBe "three"
    actual[4] shouldBe "four"
    actual[5] shouldBe "five"
    actual[6] shouldBe "six"
    println(actual)
  }

  @Test
  fun `putAll unordered puts elements into map`() {
    val actual = MutableRedBlackTreeMap<Int, String>()
    val inputMap = mapOf(6 to "six", 5 to "five", 4 to "four", 3 to "three", 2 to "two", 1 to "one")

    actual.putAll(inputMap)
    actual[1] shouldBe "one"
    actual[2] shouldBe "two"
    actual[3] shouldBe "three"
    actual[4] shouldBe "four"
    actual[5] shouldBe "five"
    actual[6] shouldBe "six"
  }

  @Test
  fun `keys returns empty set for empty map`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map.keys shouldBe emptySet()
  }

  @Test
  fun `keys returns all keys in sorted order`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[3] = "three"
    map[1] = "one"
    map[5] = "five"
    map[2] = "two"
    map[4] = "four"

    map.keys.toList() shouldContainExactly listOf(1, 2, 3, 4, 5)
  }

  @Test
  fun `values returns empty collection for empty map`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map.values shouldBe emptyList()
  }

  @Test
  fun `values returns all values in key-sorted order`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[3] = "three"
    map[1] = "one"
    map[5] = "five"
    map[2] = "two"
    map[4] = "four"

    map.values.toList() shouldContainExactly listOf("one", "two", "three", "four", "five")
  }

  @Test
  fun `entries returns empty set for empty map`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map.entries shouldBe emptySet()
  }

  @Test
  fun `entries returns all entries`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[3] = "three"
    map[1] = "one"
    map[2] = "two"

    val entries = map.entries
    entries.map { it.key } shouldContainExactlyInAnyOrder listOf(1, 2, 3)
    entries.map { it.value } shouldContainExactlyInAnyOrder listOf("one", "two", "three")
  }

  @Test
  fun `entries reflects updated values`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[1] = "one"
    map[1] = "ONE"

    val entry = map.entries.single()
    entry.key shouldBe 1
    entry.value shouldBe "ONE"
  }

  @Test
  fun `size returns 0 for empty map`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map.size shouldBe 0
  }

  @Test
  fun `size increases with each new key`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[1] = "one"
    map.size shouldBe 1
    map[2] = "two"
    map.size shouldBe 2
    map[3] = "three"
    map.size shouldBe 3
  }

  @Test
  fun `size does not increase when updating existing key`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[1] = "one"
    map[1] = "ONE"
    map.size shouldBe 1
  }

  @Test
  fun `put returns null for new key`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map.put(1, "one") shouldBe null
  }

  @Test
  fun `put returns old value when updating existing key`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[1] = "one"
    map.put(1, "ONE") shouldBe "one"
  }

  @Test
  fun `containsKey returns false for empty map`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map.containsKey(1) shouldBe false
  }

  @Test
  fun `containsKey returns true for existing key`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[3] = "three"
    map[1] = "one"
    map[5] = "five"

    map.containsKey(1) shouldBe true
    map.containsKey(3) shouldBe true
    map.containsKey(5) shouldBe true
  }

  @Test
  fun `containsKey returns false for missing key`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[1] = "one"
    map.containsKey(2) shouldBe false
  }

  @Test
  fun `containsValue returns false for empty map`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map.containsValue("one") shouldBe false
  }

  @Test
  fun `containsValue returns true for existing value`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[1] = "one"
    map[2] = "two"
    map[3] = "three"

    map.containsValue("one") shouldBe true
    map.containsValue("two") shouldBe true
    map.containsValue("three") shouldBe true
  }

  @Test
  fun `containsValue returns false for missing value`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[1] = "one"
    map.containsValue("two") shouldBe false
  }

  @Test
  fun `clear empties the map`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[1] = "one"
    map.clear()
    map.size shouldBe 0
    map.isEmpty() shouldBe true
  }

  private fun populatedMap(): MutableRedBlackTreeMap<Int, String> {
    val map = MutableRedBlackTreeMap<Int, String>()
    map[2] = "two"
    map[4] = "four"
    map[6] = "six"
    map[8] = "eight"
    return map
  }

  @Test
  fun `lower returns greatest key strictly less than given key`() {
    val map = populatedMap()
    map.lower(5) shouldBe 4
    map.lower(6) shouldBe 4
    map.lower(9) shouldBe 8
  }

  @Test
  fun `lower returns null when no smaller key exists`() {
    val map = populatedMap()
    map.lower(2) shouldBe null
    map.lower(1) shouldBe null
  }

  @Test
  fun `lower returns null for empty map`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map.lower(5) shouldBe null
  }

  @Test
  fun `floor returns greatest key less than or equal to given key`() {
    val map = populatedMap()
    map.floor(6) shouldBe 6
    map.floor(5) shouldBe 4
    map.floor(9) shouldBe 8
  }

  @Test
  fun `floor returns null when no smaller or equal key exists`() {
    val map = populatedMap()
    map.floor(1) shouldBe null
  }

  @Test
  fun `floor returns null for empty map`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map.floor(5) shouldBe null
  }

  @Test
  fun `ceiling returns least key greater than or equal to given key`() {
    val map = populatedMap()
    map.ceiling(4) shouldBe 4
    map.ceiling(5) shouldBe 6
    map.ceiling(1) shouldBe 2
  }

  @Test
  fun `ceiling returns null when no greater or equal key exists`() {
    val map = populatedMap()
    map.ceiling(9) shouldBe null
  }

  @Test
  fun `ceiling returns null for empty map`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map.ceiling(5) shouldBe null
  }

  @Test
  fun `higher returns least key strictly greater than given key`() {
    val map = populatedMap()
    map.higher(5) shouldBe 6
    map.higher(4) shouldBe 6
    map.higher(1) shouldBe 2
  }

  @Test
  fun `higher returns null when no greater key exists`() {
    val map = populatedMap()
    map.higher(8) shouldBe null
    map.higher(9) shouldBe null
  }

  @Test
  fun `higher returns null for empty map`() {
    val map = MutableRedBlackTreeMap<Int, String>()
    map.higher(5) shouldBe null
  }
}
