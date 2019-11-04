package lesson3

// Attention: comparable supported but comparator is not
open class KtBinaryTree<T : Comparable<T>> : KtBinaryTreeBackend<T>() {
//    /**
//     * Caches the size value
//     */
//    override var size = 0
//        protected set

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
     * Helper for the View implementation
     * Returns true if an element satisfies the
     * lefter bound
     */
    override fun satisfiesFrom(value: T) = true

    /**
     * Helper for the View implementation
     * Returns true if an element satisfies the
     * righter bound
     */
    override fun satisfiesTo(value: T) = true

//    /**
//     * Updates the size cached value
//     */
//    override fun add(element: T): Boolean {
//        val succeed = super.add(element)
//
//        if (succeed) {
//            size++
//        }
//
//        return succeed
//    }

//    /**
//     * Updates the size cached value
//     */
//    override fun erase(element: T): Pair<Boolean, Node<T>?> {
//        val result = erase(element)
//
//        if (result.first) {
//            size--
//        }
//
//        return result
//    }
}
