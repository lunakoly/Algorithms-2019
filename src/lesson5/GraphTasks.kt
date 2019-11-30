@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson5

import lesson5.impl.GraphBuilder
import java.util.*

/**
 * Returns a list of edges that have
 * the vertex as their beginning
 *
 *   Time Complexity: O(number of edges)
 * Memory Complexity: O(number of edges)
 */
fun Graph.getDirections(vertex: Graph.Vertex): List<Graph.Edge> {
    return getConnections(vertex)
        .map { it.value }
}

/**
 * Returns a cycle or null. allowEdge may be used
 * to only select edges that satisfy a certain rule
 *
 *   Time Complexity: O(nm),    n - number of vertices
 * Memory Complexity: O(n + m), m - number of edges
 */
fun Graph.findCycleFrom(
    vertex: Graph.Vertex,
    allowEdge: (Graph.Edge) -> Boolean = { true }
): List<Pair<Graph.Edge, Graph.Vertex>>? {
    // O(number of edges)
    val history = ArrayList<Pair<Graph.Edge, Graph.Vertex>>()

    // O(number of vertices)
    val visited = mutableSetOf<Graph.Vertex>()

    // O(number of vertices)
    // because we actually perform dfs over vertices
    val stack = LinkedList<Triple<Graph.Edge, Graph.Vertex, Int>>()

    // initial iteration
    // O(number of edges)
    for (connection in getConnections(vertex)) {
        if (allowEdge(connection.value)) {
            stack.add(Triple(connection.value, connection.key, 0))
        }
    }

    fun addToHistory(edge: Graph.Edge, vertex: Graph.Vertex, index: Int) {
        if (history.size > index) {
            history[index] = edge to vertex
        } else {
            history.add(edge to vertex)
        }
    }

    // O(nm), n - number of vertices
    //        m - number of edges
    while (stack.isNotEmpty()) {
        val (edge, next, index) = stack.removeLast()
        addToHistory(edge, next, index)
        visited.add(next)

        // O(number of edges)
        for (connection in getConnections(next)) {
            if (connection.key == vertex) {
                if (index != 0 && allowEdge(connection.value)) {
                    addToHistory(connection.value, connection.key, index + 1)
                    return history.slice(0..index + 1)
                }
            } else if (connection.key !in visited && allowEdge(connection.value)) {
                // not sure about the complexity of visited, it may be anything
                stack.add(Triple(connection.value, connection.key, index + 1))
            }
        }
    }

    return null
}

/**
 * Эйлеров цикл.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему любой Эйлеров цикл.
 * Если в графе нет Эйлеровых циклов, вернуть пустой список.
 * Соседние дуги в списке-результате должны быть инцидентны друг другу,
 * а первая дуга в списке инцидентна последней.
 * Длина списка, если он не пуст, должна быть равна количеству дуг в графе.
 * Веса дуг никак не учитываются.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Вариант ответа: A, E, J, K, D, C, H, G, B, C, I, F, B, A
 *
 * Справка: Эйлеров цикл -- это цикл, проходящий через все рёбра
 * связного графа ровно по одному разу
 *
 *   Time Complexity: O(nm),    n - number of vertices
 * Memory Complexity: O(n + m), m - number of edges
 *                        I first implemented an algorithm myself
 *                        and it worked but had an inefficient complexity
 *                        so I went to wiki and checked a better solution.
 */
fun Graph.findEulerLoop(): List<Graph.Edge> {
    if (vertices.isEmpty()) {
        return emptyList()
    }

    // Θ(number of vertices)
    val verticesWithOddNumberOfNeighbours = vertices.filter {
        getConnections(it).size % 2 == 1
    }

    // an euler path exists <=> verticesWithOddNumberOfNeighbours.size > 2
    // but a cycle <=> verticesWithOddNumberOfNeighbours.size > 0
    if (verticesWithOddNumberOfNeighbours.isNotEmpty()) {
        return emptyList()
    }

    // check if the graph is connected:
    // iterate every vertex we can starting
    // from the first one and check mark it
    // as `found`. if there're some other
    // vertices left then graph is not connected

    // O(number of vertices)
    val foundVertices = mutableSetOf<Graph.Vertex>()

    // O(number of vertices)
    val verticesToCheck = LinkedList<Graph.Vertex>()
    verticesToCheck.add(vertices.first())

    // O(number of vertices)
    while (verticesToCheck.isNotEmpty()) {
        val next = verticesToCheck.removeFirst()
        foundVertices.add(next)

        for (neighbour in getNeighbors(next)) {
            if (neighbour !in foundVertices) {
                verticesToCheck.add(neighbour)
            }
        }
    }

    // Θ(number of vertices)
    for (each in vertices) {
        if (each !in foundVertices) {
            return emptyList()
        }
    }

    // O(number of edges)
    val deleted = mutableSetOf<Graph.Edge>()

    // Θ(number of edges)
    val result = Array<Graph.Edge>(edges.size) { edges.first() }
    var resultIndex = 0

    /**
     * Searches for cycles, removes them and
     * adds corresponding edges to result.
     * Also does recursion.
     *
     *   Time Complexity: O(nm),    n - number of vertices
     * Memory Complexity: O(n + m), m - number of edges
     *                    Wiki says that it's time complexity is
     *                    O(m) but I guess it doesn't take
     *                    loop searching into account
     */
    fun addAllCycles(vertex: Graph.Vertex) {
        do {
            // O(nm) time, O(n + m) memory
            val cycle = findCycleFrom(vertex) {
                it !in deleted
            }

            if (cycle != null) {
                // O(m)
                for (way in cycle) {
                    deleted.add(way.first)
                }

                // O(nm)
                for (it in 0 until cycle.size - 1) {
                    result[resultIndex] = cycle[it].first
                    resultIndex += 1

                    addAllCycles(cycle[it].second)
                }

                // if cycle != null => cycle.size > 0
                deleted.add(cycle.last().first)
                result[resultIndex] = cycle.last().first
                resultIndex += 1
            }
        } while (cycle != null)
    }

    addAllCycles(vertices.first())
    return result.toList()
}

/**
 * Минимальное остовное дерево.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему минимальное остовное дерево.
 * Если есть несколько минимальных остовных деревьев с одинаковым числом дуг,
 * вернуть любое из них. Веса дуг не учитывать.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ:
 *
 *      G    H
 *      |    |
 * A -- B -- C -- D
 * |    |    |
 * E    F    I
 * |
 * J ------------ K
 *
 *   Time Complexity: O(nm),    n - number of vertices
 * Memory Complexity: O(n + m), m - number of edges
 */
fun Graph.minimumSpanningTree(): Graph {
    // O(number of edges)
    val deleted = mutableSetOf<Graph.Edge>()

    // technically we're searching for cycles and removing them.
    // so we remove each cycle only ones and all cycles sum up
    // to the whole number of edges in total (so it must be
    // O(number of edges) but as with the previous task
    // I suppose it's actually O(nm) & O(n + m))
    for (vertex in vertices) {
        do {
            // O(nm)
            val cycle = findCycleFrom(vertex) {
                it !in deleted
            }

            // remove the cycle
            if (cycle != null) {
                deleted.add(cycle.first().first)
            }
        } while (cycle != null)
    }

    return GraphBuilder().apply {
        // Θ(number of vertices)
        for (each in vertices) {
            addVertex(each.name)
        }

        // Θ(number of edges)
        for (each in edges) {
            // is there a point in trying to analyze the complexity
            // of API-driven declaration style? I mean, if the implementation
            // is encapsulated and we can't know for sure (in general)
            // is it really important to analyze a `partial` complexity?
            // we can't rely on this `partial complexity` either because
            // the implementation may have been optimized for certain actions
            // (e. g. relying on separate Edge instances for each direction)
            // and so we can't know what public operations are cheaper to use
            // in our algorithm
            if (each in deleted) {
                continue
            }

            val begin = get(each.begin.name)
            val end = get(each.end.name)

            if (begin == null || end == null) {
                throw Exception("This should never happen")
            }

            addConnection(begin, end, each.weight)
        }
    }.build()
}

/**
 * Максимальное независимое множество вершин в графе без циклов.
 * Сложная
 *
 * Дан граф без циклов (получатель), например
 *
 *      G -- H -- J
 *      |
 * A -- B -- D
 * |         |
 * C -- F    I
 * |
 * E
 *
 * Найти в нём самое большое независимое множество вершин и вернуть его.
 * Никакая пара вершин в независимом множестве не должна быть связана ребром.
 *
 * Если самых больших множеств несколько, приоритет имеет то из них,
 * в котором вершины расположены раньше во множестве this.vertices (начиная с первых).
 *
 * В данном случае ответ (A, E, F, D, G, J)
 *
 * Если на входе граф с циклами, бросить IllegalArgumentException
 *
 * Эта задача может быть зачтена за пятый и шестой урок одновременно
 *
 *   Time Complexity: O(min{nm, n^2}), n - number of vertices
 * Memory Complexity: O(n + m),        m - number of edges
 */
fun Graph.largestIndependentVertexSet(): Set<Graph.Vertex> {
    if (vertices.isEmpty()) {
        return emptySet()
    }

    // look for cycles
    // O(mn)
    for (vertex in vertices) {
        // O(nm)
        val cycle = findCycleFrom(vertex)

        // remove the cycle
        if (cycle != null) {
            throw IllegalArgumentException("Cycle's been found")
        }
    }

    /**
     * Look for the biggest set of independent
     * vertices inside a single component
     *
     *   Time Complexity: O(n), n - number of vertices
     * Memory Complexity: O(n)
     */
    fun fillAttempts(
        visited: MutableSet<Graph.Vertex>,
        first: Graph.Vertex
    ): MutableSet<Graph.Vertex> {
        // ] `attempt` - a probable result
        // since we treat the component as a tree,
        // we can associate each vertex with some
        // height-level. let's make the first attempt
        // contain vertices with (2n + 1) level values
        // and tge second one - with (2n). then we'll just
        // select the biggest one

        // O(number of vertices)
        val firstAttempt = mutableSetOf<Graph.Vertex>()
        // O(number of vertices)
        val secondAttempt = mutableSetOf<Graph.Vertex>()

        // O(number of vertices)
        val queue = LinkedList<Pair<Graph.Vertex, Boolean>>()
        queue.add(first to true)

        // bfs
        // O(number of vertices)
        while (queue.isNotEmpty()) {
            val (next, shouldAccept) = queue.removeFirst()
            visited.add(next)

            if (shouldAccept) {
                firstAttempt.add(next)
            } else {
                secondAttempt.add(next)
            }

            for (it in getNeighbors(next)) {
                if (it !in visited) {
                    queue.add(it to !shouldAccept)
                }
            }
        }

        return if (firstAttempt.size >= secondAttempt.size) {
            firstAttempt
        } else {
            secondAttempt
        }
    }

    /**
     * Find the requested set of vertices across
     * all components
     *
     *   Time Complexity: O(n^2), n - number of vertices
     * Memory Complexity: O(n)
     */
    fun findVertices(): MutableSet<Graph.Vertex> {
        // O(number of vertices)
        val visited = mutableSetOf<Graph.Vertex>()
        // O(number of vertices)
        val result = mutableSetOf<Graph.Vertex>()

        // O(n^2), n - number of vertices
        for (it in vertices) {
            if (it !in visited) {
                // O(number of vertices)
                result.addAll(fillAttempts(visited, it))
            }
        }

        return result
    }

    return findVertices()
}

/**
 * Наидлиннейший простой путь.
 * Сложная
 *
 * Дан граф (получатель). Найти в нём простой путь, включающий максимальное количество рёбер.
 * Простым считается путь, вершины в котором не повторяются.
 * Если таких путей несколько, вернуть любой из них.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ: A, E, J, K, D, C, H, G, B, F, I
 *
 *   Time Complexity: O(n^2), n - number of vertices
 * Memory Complexity: O(n)
 */
fun Graph.longestSimplePath(): Path {
    /**
     * Searches the longest simple path starting
     * from a particular vertex
     *
     *   Time Complexity: O(n), n - number of vertices
     * Memory Complexity: O(n)
     */
    fun findLongestSimplePath(first: Graph.Vertex): List<Graph.Vertex> {
        // the actual longest path
        // O(number of vertices)
        val longestHistory = LinkedList<Graph.Vertex>()
        // when we backtrack we need to update a bunch of
        // values. `changesIndex` points to the first
        // cell that needs to be updated
        var changesIndex = 0

        // the history until (and including) the current
        // vertex
        // O(number of vertices)
        val history = ArrayList<Graph.Vertex>()

        // dfs stack
        // O(number of vertices)
        val stack = LinkedList<Pair<Graph.Vertex, Int>>()
        stack.add(first to 0)

        // used to update changesIndex
        var previousIndex = 0

        // dfs, O(number of vertices)
        while (stack.isNotEmpty()) {
            val (next, index) = stack.removeLast()

            // add to history
            if (index >= history.size) {
                history.add(next)
            } else {
                history[index] = next
            }

            // reduce changesIndex
            if (previousIndex > index) {
                changesIndex = index
            }

            // if history is longer than longestHistory
            if (index >= longestHistory.size) {
                // remove all above changesIndex
                // O(number of vertices)
                while (longestHistory.size > changesIndex) {
                    longestHistory.removeLast()
                }

                // add all newer values
                // O(number of vertices)
                while (index >= longestHistory.size) {
                    longestHistory.add(history[longestHistory.size])
                }

                changesIndex = index
            }

            previousIndex = index

            for (neighbour in getNeighbors(next)) {
                if (neighbour !in history.slice(0..index)) {
                    stack.add(neighbour to index + 1)
                }
            }
        }

        return longestHistory
    }

    // longest of longests from the function above
    // O(number of vertices)
    var longestHistory = listOf<Graph.Vertex>()

    // O(n^2)
    for (vertex in vertices) {
        // O(number of vertices)
        val history = findLongestSimplePath(vertex)

        if (history.size > longestHistory.size) {
            longestHistory = history
        }
    }

    // turn history into path
    // (because I hand't known that Path exists
    // and I'm not liable to refactor the code
    // right now)

    if (longestHistory.isEmpty()) {
        return Path()
    }

    var path = Path(longestHistory.first())

    // O(number of vertices)
    for (it in 1 until longestHistory.size) {
        path = Path(path, this, longestHistory[it])
    }

    return path
}