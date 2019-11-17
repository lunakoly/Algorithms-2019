package lesson4

@Suppress("UNCHECKED_CAST")
class OpenAddressingSet<T : Any>(private val bits: Int) : AbstractMutableSet<T>() {
    init {
        require(bits in 2..31)
    }

    private val capacity = 1 shl bits

    private val storage = Array<Any?>(capacity) { null }

    override var size: Int = 0

    private fun T.startingIndex(): Int {
        // 0x7FFFFFFF is 31 ones
        // 0x7FFFFFFF shr (31 - bits)
        // returns the `bits`-long mask of 1
        return hashCode() and (0x7FFFFFFF shr (31 - bits))
    }

    override fun contains(element: T): Boolean {
        var index = element.startingIndex()
        var current = storage[index]
        var checked = 0

        while (current != null && checked < capacity) {
            if (current == element) {
                return true
            }
            index = (index + 1) % capacity
            current = storage[index]
            checked += 1
        }
        return false
    }

    override fun add(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = startingIndex
        var current = storage[index]
        while (current != null) {
            if (current == element) {
                return false
            }
            index = (index + 1) % capacity
            check(index != startingIndex) { "Table is full" }
            current = storage[index]
        }
        storage[index] = element
        size++
        return true
    }

    /**
     * Для этой задачи пока нет тестов, но вы можете попробовать привести решение и добавить к нему тесты
     *
     *   Time Complexity: O(n), n = capacity
     * Memory Complexity: Θ(1)
     */
    override fun remove(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = startingIndex
        var current = storage[index]

        while (current != null) {
            if (current == element) {
                storage[index] = null
                size -= 1
                return true
            }

            index = (index + 1) % capacity

            if (index == startingIndex) {
                return false
            }

            current = storage[index]
        }

        return false
    }

    /**
     * Для этой задачи пока нет тестов, но вы можете попробовать привести решение и добавить к нему тесты
     */
    override fun iterator() = object : MutableIterator<T> {
        private var currentIndex = 0
        private var nextIndex = 0

        private var current: T? = null

        init {
            while (
                nextIndex < capacity &&
                storage[nextIndex] == null
            ) {
                nextIndex += 1
            }
        }

        /**
         * True if next() can return one more element
         *
         *   Time Complexity: Θ(1)
         * Memory Complexity: Θ(1)
         */
        override fun hasNext() = nextIndex < capacity

        /**
         * Returns the next element.
         *
         *   Time Complexity: O(n), n = capacity
         * Memory Complexity: Θ(1)
         */
        override fun next(): T {
            check(nextIndex < capacity) { "There's no more elements left!" }

            currentIndex = nextIndex
            current = storage[nextIndex]!! as T
            nextIndex += 1

            // storage[lookAheadIndex] will never be
            // null if lookAheadIndex < capacity
            // O(capacity)
            while (
                nextIndex < capacity &&
                storage[nextIndex] == null
            ) {
                nextIndex += 1
            }

            return current!!
        }

        /**
         * Removes the current element
         *
         *   Time Complexity: Θ(1)
         * Memory Complexity: Θ(1)
         */
        override fun remove() {
            if (current != null) {
                storage[currentIndex] = null
                size -= 1
            }
        }
    }
}