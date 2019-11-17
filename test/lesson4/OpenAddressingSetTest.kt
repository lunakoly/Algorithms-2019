package lesson4

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Tag

class OpenAddressingSetTest {

    @Test
    @Tag("Example")
    fun add() {
        val set = OpenAddressingSet<String>(16)
        assertTrue(set.isEmpty())
        set.add("Alpha")
        set.add("Beta")
        set.add("Omega")
        assertSame(3, set.size)
        assertTrue("Beta" in set)
        assertFalse("Gamma" in set)
        assertTrue("Omega" in set)
    }

    @Test
    @Tag("ULTRA MEGA HARD")
    fun remove() {
        val set = OpenAddressingSet<String>(16)
        assertTrue(set.isEmpty())
        set.add("Alpha")
        set.add("Beta")
        set.add("Omega")

        var result = set.remove("Beta")
        assertSame(true, result)
        assertSame(2, set.size)
        assertFalse("Gamma" in set)
        assertTrue("Omega" in set)

        result = set.remove("Shrek")
        assertSame(false, result)
        assertSame(2, set.size)
        assertFalse("Gamma" in set)
        assertTrue("Omega" in set)
    }

    @Test
    @Tag("UNBELIEVABLE")
    fun iterator() {
        val neutral = setOf("Alpha", "Beta", "Omega")
        val set = OpenAddressingSet<String>(16)

        for (it in neutral) {
            set.add(it)
            assertTrue(it in set)
        }

        val iterator = set.iterator()
        var count = neutral.size

        while (iterator.hasNext()) {
            assertTrue(iterator.next() in neutral)
            count -= 1
        }

        assertSame(0, count)
    }

    @Test
    @Tag("Impossible for a human")
    fun iteratorRemove() {
        val neutral = setOf("Alpha", "Beta", "Omega")
        val set = OpenAddressingSet<String>(16)

        for (it in neutral) {
            set.add(it)
            assertTrue(it in set)
        }

        var toRemove = "Beta"

        var iterator = set.iterator()
        var count = neutral.size

        while (iterator.hasNext()) {
            val next = iterator.next()

            assertTrue(next in neutral)
            count -= 1

            if (next == toRemove) {
                iterator.remove()
            }
        }

        assertSame(0, count)
        assertSame(2, set.size)
        assertTrue("Alpha" in set)
        assertTrue("Omega" in set)

        set.add("Beta")
        toRemove = "Omega"

        iterator = set.iterator()
        count = neutral.size

        while (iterator.hasNext()) {
            val next = iterator.next()

            assertTrue(next in neutral)
            count -= 1

            if (next == toRemove) {
                iterator.remove()
            }
        }

        assertSame(0, count)
        assertSame(2, set.size)
        assertTrue("Alpha" in set)
        assertTrue("Beta" in set)
    }
}