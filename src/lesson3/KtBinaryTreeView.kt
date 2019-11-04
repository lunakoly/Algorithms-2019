package lesson3

/**
 * Represents a view to a KtBinaryTree.
 * I couldn't find a solution to not make
 * Node public
 */
class KtBinaryTreeView<T : Comparable<T>>(
    root: Node<T>?,
    private val fromElement: T?,
    private val toElement: T?,
    private val satisfiesFromIfPresent: (T, T) -> Boolean,
    private val satisfiesToIfPresent: (T, T) -> Boolean
) : KtBinaryTreeBackend<T>() {
    init {
        this.root = root
    }

    /**
     * Recalculates the size each time it
     * gets called
     */
    override val size: Int
        get() {
            // recalculate size
            val iterator = iterator()
            var theSize = 0

            while (iterator.hasNext()) {
                val value = iterator.next()
                theSize += 1
            }

            return theSize
        }

    /**
     * Introduces null checking
     */
    override fun satisfiesFrom(value: T) = fromElement == null || satisfiesFromIfPresent(fromElement, value)

    /**
     * Introduces null checking
     */
    override fun satisfiesTo(value: T) = toElement == null || satisfiesToIfPresent(value, toElement)
}