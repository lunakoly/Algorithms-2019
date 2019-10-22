package lesson3

import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.max

// Attention: comparable supported but comparator is not
class KtBinaryTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private var root: Node<T>? = null

    override var size = 0
        private set

    private class Node<T>(val value: T) {

        var left: Node<T>? = null

        var right: Node<T>? = null
    }

    override fun add(element: T): Boolean {
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
        size++
        return true
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    override fun height(): Int = height(root)

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    /**
     * Visualizes a subtree
     *
     *   Time Complexity: O(logn), n - number of nodes
     * Memory Complexity: Θ(1)
     */
    private fun visualize(prefix: String, direction: String, start: Node<T>?) {
        if (start != null) {
            visualize("$prefix---", "/", start.right)
            println("$prefix $direction ${start.value}")
            visualize("$prefix---", "\\", start.left)
        }
    }

    /**
     * Useful for debugging
     *
     *   Time Complexity: O(logn), n - number of nodes
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
    private fun erase(element: T): Pair<Boolean, Node<T>?> {
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

        size--
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

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
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

    inner class BinaryTreeIterator internal constructor() : MutableIterator<T> {
        /**
         * Stack of nodes to be checked
         * Used for next()
         */
        private val nodes = LinkedList<Node<T>>()
        /**
         * Determines if a node has been checked
         * Used for next()
         */
        private val visited = mutableSetOf<Node<T>>()
        /**
         * Used for deleting
         */
        private var current: Node<T>? = null

        init {
            // prepare for dfs
            val theRoot = root

            if (theRoot != null) {
                nodes.add(theRoot)
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

                val lefter = last.left
                val righter = last.right

                if (lefter != null && !visited.contains(lefter)) {
                    nodes.add(last)
                    nodes.add(lefter)
                } else if (!visited.contains(last)) {
                    visited.add(last)

                    if (righter != null) {
                        nodes.add(righter)
                    }

                    current = last
                    return last.value
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
                val (_, replacement) = erase(previous.value)

                if (replacement != null && !visited.contains(replacement)) {
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
                throw NoSuchElementException()
            }
        }
    }

    override fun iterator(): MutableIterator<T> = BinaryTreeIterator()

    override fun comparator(): Comparator<in T>? = null

    /**
     * Найти множество всех элементов в диапазоне [fromElement, toElement)
     * Очень сложная
     *
     *   Time Complexity: O(n),    n - number of nodes
     * Memory Complexity: Θ(logn)
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        var start = root ?: return emptySet<T>().toSortedSet()

        // sort of a dfs search
        // O(logn) memory
        val nodes = LinkedList<Node<T>>()
        val visited = mutableSetOf<Node<T>>()
        val result = mutableSetOf<T>().toSortedSet()

        // find th first element. nodes will contain
        // the route to the fist element
        // O(logn) time
        do {
            val comparison = fromElement.compareTo(start.value)
            nodes.add(start)

            if (comparison < 0) {
                start = start.left ?: break
            } else if (comparison > 0) {
                start = start.right ?: break
            }
        } while (comparison != 0)

        // check if it is really satisfies our range
        // and is not smaller than fromElement.
        // otherwise we remove every trailing element
        // less than fromElement
        // O(logn) time
        while (nodes.size > 0) {
            val last = nodes.last()

            if (last.value < fromElement) {
                nodes.removeLast()
            } else {
                break
            }
        }

        // no suitable nodes left
        if (nodes.size == 0) {
            return emptySet<T>().toSortedSet()
        }

        // the idea is to walk through the tree like:
        //    *         *         *         *
        //   * -       + *       + *       + *
        //  *   -     +   -     +   *     +   *
        // *   - -   +   - -   +   - -   +   * -
        // check all bottom-left paths and after
        // that make a step to every righter bottom-left 'sub-path'

        // sort of a dfs.
        // O(n)
        while (nodes.size > 0) {
            val last = nodes.removeLast()

            val lefter = last.left
            val righter = last.right

            // haven't checked the lefter
            if (lefter != null && !visited.contains(lefter)) {
                // we always contain the route from to the lefter
                nodes.add(last)
                nodes.add(lefter)
            } else if (!visited.contains(last)) {
                // since we've already checked all elements
                // < last.value, we know that only
                // the bigger ones left in the tree but
                // we don't need them
                if (last.value >= toElement) {
                    break
                }

                visited.add(last)

                if (righter != null) {
                    nodes.add(righter)
                }

                result.add(last.value)
            }
        }

        return result
    }

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     *
     *   Time Complexity: O(n),    n - number of nodes
     * Memory Complexity: Θ(logn)
     */
    override fun headSet(toElement: T): SortedSet<T> {
        if (root == null)
            return emptySet<T>().toSortedSet()
        return subSet(first(), toElement)
    }

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     *
     *   Time Complexity: O(n),    n - number of nodes
     * Memory Complexity: Θ(logn)
     */
    override fun tailSet(fromElement: T): SortedSet<T> {
        if (root == null)
            return emptySet<T>().toSortedSet()

        val result = subSet(fromElement, last())
        result.add(last())
        return result
    }

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
