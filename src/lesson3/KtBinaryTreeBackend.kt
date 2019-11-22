package lesson3

import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.max

// Attention: comparable supported but comparator is not
abstract class KtBinaryTreeBackend<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    protected var root: Node<T>? = null

    class Node<T>(val value: T) {

        var left: Node<T>? = null

        var right: Node<T>? = null
    }

    /**
     * Helper for the View implementation
     * Returns true if an element satisfies the
     * lefter bound
     */
    protected abstract fun satisfiesFrom(value: T): Boolean

    /**
     * Helper for the View implementation
     * Returns true if an element satisfies the
     * righter bound
     */
    protected abstract fun satisfiesTo(value: T): Boolean

    /**
     * Helper for the View implementation
     * Returns true if an element satisfies the
     * righter bound
     */
    protected fun satisfiesRange(value: T) = satisfiesFrom(value) && satisfiesTo(value)

    override fun add(element: T): Boolean {
        require(satisfiesRange(element)) { "Can't add an element that doesn't satisfy the range" }

        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)

        if (comparison == 0) {
            return false
        }

        val newNode = Node(element)

        when {
            closest == null -> root = newNode

            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }

            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }

        return true
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    private fun checkInvariant(node: Node<T>): Boolean {
        if (node.left != null && (node.left!!.value >= node.value || !checkInvariant(node.left!!))) return false
        return node.right == null || node.right!!.value > node.value && checkInvariant(node.right!!)
    }

    override fun height(): Int = height(root)

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    /**
     * Visualizes a subtree
     *
     *   Time Complexity: O(n), n - number of nodes
     * Memory Complexity: Θ(1)
     */
    private fun visualize(prefix: String, direction: String, start: Node<T>?) {
        if (start != null && satisfiesRange(start.value)) {
            visualize("$prefix---", "/", start.right)
            println("$prefix $direction ${start.value}")
            visualize("$prefix---", "\\", start.left)
        }
    }

    /**
     * Useful for debugging
     *
     *   Time Complexity: O(n), n - number of nodes
     * Memory Complexity: Θ(1)
     */
    override fun visualize() {
        val theRoot = root

        if (theRoot == null) {
            print("Visualizing: NONE")
            return
        }

        println("Visualizing:")
        visualize("-", "", theRoot)
    }

    /**
     * Same as find but also returns the parent element
     *
     *   Time Complexity: O(logn), n - number of nodes
     * Memory Complexity: O(logn)
     *                  because comparison variable (Θ(1)) will
     *                  be created at each step - O(logn) times
     */
    private fun findWithParent(previous: Node<T>, next: Node<T>, value: T): Pair<Node<T>, Node<T>> {
        val comparison = value.compareTo(next.value)
        return when {
            comparison == 0 -> previous to next
            comparison < 0 -> next.left?.let { findWithParent(next, it, value) } ?: previous to next
            else -> next.right?.let { findWithParent(next, it, value) } ?: previous to next
        }
    }

    /**
     * Returns the most left node
     *
     *   Time Complexity: O(logn), n - number of nodes
     * Memory Complexity: Θ(1)
     */
    private fun leftmostWithParent(previous: Node<T>, next: Node<T>): Pair<Node<T>, Node<T>> {
        var last = previous
        var current = next

        while (current.left != null) {
            last = current
            current = current.left!!
        }

        return last to current
    }

    /**
     * Returns the most right node
     *
     *   Time Complexity: O(logn), n - number of nodes
     * Memory Complexity: Θ(1)
     */
    private fun rightmostWithParent(previous: Node<T>, next: Node<T>): Pair<Node<T>, Node<T>> {
        var last = previous
        var current = next

        while (current.right != null) {
            last = current
            current = current.right!!
        }

        return last to current
    }

    /**
     * Removes an element from the tree
     * and returns a pair (Success, Replacement)
     *
     *   Time Complexity: O(logn), n - number of nodes
     * Memory Complexity: Θ(logn)
     */
    protected open fun erase(element: T): Pair<Boolean, Node<T>?> {
        val root = root ?: return false to null

        // O(logn) for both the time and memory
        val (parent, target) = findWithParent(root, root, element)

        if (element.compareTo(target.value) != 0) {
            return false to null
        }

        // to not write !!
        var targetLeft = target.left
        var targetRight = target.right
        // the node to replace target with
        var replacement: Node<T>? = null

        // let's find the leftmost node of the right subtree
        // and the rightmost node of the left subtree
        // and use it as a replacement

        // O(logn) time for both branches
        if (targetRight != null) {
            val (holder, leftmost) = leftmostWithParent(targetRight, targetRight)
            replacement = leftmost

            // special cases
            if (leftmost == targetRight) {
                targetRight = leftmost.right
            } else {
                holder.left = leftmost.right
            }
        } else if (targetLeft != null) {
            val (holder, rightmost) = rightmostWithParent(targetLeft, targetLeft)
            replacement = rightmost

            // special cases
            if (rightmost == targetLeft) {
                targetLeft = rightmost.left
            } else {
                holder.right = rightmost.left
            }
        }

        // configure replacement
        if (replacement != null) {
            replacement.left = targetLeft
            replacement.right = targetRight
        }

        // replace target with the replacement
        when (target) {
            root -> this.root = replacement
            parent.left -> parent.left = replacement
            else -> parent.right = replacement
        }

        return true to replacement
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     *
     *   Time Complexity: O(logn), n - number of nodes
     * Memory Complexity: Θ(logn)
     */
    override fun remove(element: T) = erase(element).first

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    protected open fun find(start: Node<T>, value: T): Node<T> {
        var current = start

        // I had to rewrite this function because
        // of the maximum stack exceeded error message

        do {
            val comparison = value.compareTo(current.value)

            if (comparison < 0) {
                current = current.left ?: return current
            } else if (comparison > 0) {
                current = current.right ?: return current
            }
        } while (comparison != 0)

        return current
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null &&
                satisfiesRange(closest.value) &&
                element.compareTo(closest.value) == 0
    }

    inner class BinaryTreeIterator internal constructor() : MutableIterator<T> {
        /**
         * Stack of nodes to be checked
         * Used for next()
         */
        private val nodes = LinkedList<Node<T>>()
        /**
         * Used for deleting
         */
        private var current: Node<T>? = null
        /**
         * When met stops iterating.
         * The last element satisfiesTo() returns
         * true for
         */
        private var lastAllowed: Node<T>? = null

        init {
            // prepare for dfs
            val theRoot = root

            if (theRoot != null) {
                nodes.add(theRoot)

                // find the nearest to the end node
                // and it'll be the lastAllowed one

                // path to the last allowed node
                // O(logn) memory
                val path = LinkedList<Node<T>>()
                path.add(theRoot)

                // O(logn) time
                do {
                    val current = path.last

                    if (satisfiesTo(current.value)) {
                        path.add(current.right ?: break)
                    } else {
                        path.add(current.left ?: break)
                    }
                } while (true)

                // remove the ones that don't satisfy the range
                // O(logn) time
                for (it in path.reversed()) {
                    if (!satisfiesTo(it.value)) {
                        path.removeLast()
                    } else {
                        break
                    }
                }

                if (path.isNotEmpty()) {
                    lastAllowed = path.last
                }
            }
        }

        /**
         * Проверка наличия следующего элемента
         * Средняя
         *
         *   Time Complexity: Θ(1)
         * Memory Complexity: Θ(1)
         */
        override fun hasNext() = nodes.isNotEmpty()

        /**
         * Поиск следующего элемента
         * Средняя
         *
         *   Time Complexity: O(n), n - number of nodes to check left
         * Memory Complexity: Θ(1)
         */
        override fun next(): T {
            if (nodes.size == 0)
                throw NoSuchElementException()

            while (nodes.size > 0) {
                val last = nodes.removeLast()

                var wasLeftVisited = false

                // if there's something to the left
                // but current has already become bigger
                // then it.
                if (current != null && last.left != null) {
                    wasLeftVisited = current!!.value >= last.left!!.value
                }

                if (last.left != null && !wasLeftVisited) {
                    nodes.add(last)
                    nodes.add(last.left!!)
                } else {
                    current = last

                    if (last.right != null) {
                        nodes.add(last.right!!)
                    }

                    if (satisfiesRange(last.value)) {
                        if (lastAllowed == last) {
                            // so that will stop further
                            // hasNext()
                            nodes.clear()
                        }

                        return last.value
                    }
                }
            }

            throw NoSuchElementException()
        }

        /**
         * Удаление следующего элемента
         * Сложная
         *
         *   Time Complexity: O(logn), n - number of nodes
         * Memory Complexity: Θ(logn)
         */
        override fun remove() {
            // by the time we delete an element
            // all the nodes that construct the path to
            // the current node have already been checked
            // and so - not present in that nodes stack.
            val previous = current

            if (previous != null) {
                // despite the fact that erase() will need to find
                // the parent of previous I still think there's no
                // point to try to do some optimizations here.
                // even if we don't search for the parent the complexity
                // will stay the same - O(logn). Because nodes doesn't
                // really store the whole path (lefter shoulders only)
                // we can't use it to determine the parent node and thus
                // need to create one more list (mirroring nodes) for
                // links to parents. so I prefer saving that memory
                val (_, replacement) = erase(previous.value)

                if (replacement != null && previous.value < replacement.value) {
                    // I assume it's Θ(1)
                    val end = nodes.lastOrNull()

                    // special cases
                    if (end != replacement) {
                        // otherwise it will be added
                        // to nodes twice. it's easy to see
                        // that it may only be the last element
                        // if present in nodes
                        if (end == replacement.right) {
                            nodes.removeLast()
                        }

                        // sometimes it's already in
                        // nodes and sometimes it's not.
                        // it's easy to see that it may only
                        // be the last element in nodes
                        nodes.add(replacement)
                    }
                }
            } else {
                throw IllegalStateException()
            }
        }
    }

    override fun iterator(): MutableIterator<T> = BinaryTreeIterator()

    override fun comparator(): Comparator<in T>? = null

    /**
     * Helps to initialize a subtree.
     */
    private fun lessOrEquals(first: T, second: T) = first <= second

    /**
     * Helps to initialize a subtree
     */
    private fun less(first: T, second: T) = first < second

    /**
     * Найти множество всех элементов в диапазоне [fromElement, toElement)
     * Очень сложная
     *
     *   Time Complexity: Θ(1)
     * Memory Complexity: Θ(1)
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> =
        KtBinaryTreeView(root, fromElement, toElement, ::lessOrEquals, ::less)

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     *
     *   Time Complexity: Θ(1)
     * Memory Complexity: Θ(1)
     */
    override fun headSet(toElement: T): SortedSet<T> =
        KtBinaryTreeView(root, null, toElement, ::lessOrEquals, ::less)

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     *
     *   Time Complexity: Θ(1)
     * Memory Complexity: Θ(1)
     */
    override fun tailSet(fromElement: T): SortedSet<T> =
        KtBinaryTreeView(root, fromElement, null, ::lessOrEquals, ::lessOrEquals)

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }
}
