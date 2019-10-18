package lesson3

import java.util.*

class Trie : AbstractMutableSet<String>(), MutableSet<String> {
    override var size: Int = 0
        private set

    private class Node {
        val children: MutableMap<Char, Node> = linkedMapOf()
    }

    private var root = Node()

    override fun clear() {
        root.children.clear()
        size = 0
    }

    private fun String.withZero() = this + 0.toChar()

    private fun findNode(element: String): Node? {
        var current = root
        for (char in element) {
            current = current.children[char] ?: return null
        }
        return current
    }

    override fun contains(element: String): Boolean =
        findNode(element.withZero()) != null

    override fun add(element: String): Boolean {
        var current = root
        var modified = false
        for (char in element.withZero()) {
            val child = current.children[char]
            if (child != null) {
                current = child
            } else {
                modified = true
                val newChild = Node()
                current.children[char] = newChild
                current = newChild
            }
        }
        if (modified) {
            size++
        }
        return modified
    }

    override fun remove(element: String): Boolean {
        val current = findNode(element) ?: return false
        if (current.children.remove(0.toChar()) != null) {
            size--
            return true
        }
        return false
    }

    /**
     * Итератор для префиксного дерева
     * Сложная
     *
     * Memory Complexity: Θ(n), n - number of nodes in the tree
     */
    override fun iterator(): MutableIterator<String> = object : MutableIterator<String> {
        /**
         * Stack of prefixes of nodes to be checked
         * Used for DFS.
         */
        private val prefixes = LinkedList<String>()
        /**
         * Stack of nodes to be checked
         * Used for DFS.
         */
        private val nodes = LinkedList<Node>()
        /**
         * The last value returned from
         * next(). Used for deleting
         */
        private var current: String? = null

        init {
            // prepare for dfs
            nodes.add(root)
            prefixes.add("")
        }

        /**
         *   Time Complexity: Θ(1)
         * Memory Complexity: Θ(1)
         */
        override fun hasNext() = nodes.isNotEmpty()

        /**
         *   Time Complexity: Θ(n), n - number of nodes to check left
         * Memory Complexity: Θ(1)
         */
        override fun next(): String {
            // linear
            while (nodes.size > 0) {
                val node = nodes.removeLast()
                val prefix = prefixes.removeLast()

                for (each in node.children) {
                    if (each.key == 0.toChar()) {
                        current = prefix
                        return prefix
                    } else {
                        nodes.add(each.value)
                        prefixes.add(prefix + each.key)
                    }
                }
            }

            throw NoSuchElementException()
        }

        /**
         *   Time Complexity: Θ(n), n - length of the current string
         * Memory Complexity: Θ(n)
         */
        override fun remove() {
            val string = current?.withZero()
                ?: throw NoSuchElementException()

            val path = Array(string.length) { root }
            var previous = root

            // linear
            for (it in string.indices) {
                previous = previous.children[string[it]]
                    ?: throw Exception("Inner state error")
                path[it] = previous
            }

            // remove \0 with actually means removing
            // the whole word
            path[path.lastIndex - 1].children.remove(0.toChar())
            size--

            // by the time we delete an element
            // all the nodes that construct the path to
            // the \0 node have already been checked
            // and so - not present in that nodes stack.
            // that makes it safe to delete them during the
            // cleanup

            // linear
            // additional cleanup
            for (it in path.lastIndex - 1 downTo 1) {
                if (path[it].children.isEmpty()) {
                    path[it - 1].children.remove(string[it])
                } else {
                    break
                }
            }
        }
    }
}