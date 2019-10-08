@file:Suppress("UNUSED_PARAMETER")

package lesson2

import java.io.File
import java.util.*
import kotlin.Exception
import kotlin.math.sqrt

/**
 * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
 * Простая
 *
 * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
 * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
 *
 * 201
 * 196
 * 190
 * 198
 * 187
 * 194
 * 193
 * 185
 *
 * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
 * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть раньше первого.
 * Вернуть пару из двух моментов.
 * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
 * Например, для приведённого выше файла результат должен быть Pair(3, 4)
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 *
 *   Time Complexity: Θ(n), n - number of numbers
 * Memory Complexity: Θ(n)
 *
 * It may be possible to solve the task with Θ(1) memory
 * but mglukhikh once told us during a lecture that there's
 * no way to implement an Θ(n) time algorithm for this task via
 * just searching for max and min. Let's prove the opposite
 */
fun optimizeBuyAndSell(inputName: String): Pair<Int, Int> {
    val prices = File(inputName).readLines()
        // linear
        .map {
            """\d+""".toRegex().find(it)
                ?: throw Exception("Incorrect format")

            it.toInt()
        }

    if (prices.isEmpty())
        throw Exception("Incorrect input")

    // minimum price that has been detected before
    // and including the i-th day
    val minPrice = IntArray(prices.size)
    // the day the min price has been detected
    val minPriceDay = IntArray(prices.size)

    // start from the first day and propagate further
    minPrice[0] = prices[0]
    minPriceDay[0] = 0

    // linear
    for (it in 1 until prices.size) {
        if (prices[it] < minPrice[it - 1]) {
            minPrice[it] = prices[it]
            minPriceDay[it] = it
        } else {
            minPrice[it] = minPrice[it - 1]
            minPriceDay[it] = minPriceDay[it - 1]
        }
    }

    // maximum price that has been detected since
    // and including the i-th day
    val maxPrice = IntArray(prices.size)
    // the day the max price has been detected
    val maxPriceDay = IntArray(prices.size)

    // start from the last day and propagate further backwards
    maxPrice[prices.lastIndex] = prices.last()
    maxPriceDay[prices.lastIndex] = prices.lastIndex

    // linear
    for (it in prices.size - 2 downTo 0) {
        if (prices[it] > maxPrice[it + 1]) {
            maxPrice[it] = prices[it]
            maxPriceDay[it] = it
        } else {
            maxPrice[it] = maxPrice[it + 1]
            maxPriceDay[it] = maxPriceDay[it + 1]
        }
    }

    var bestDifference = 0
    var bestPeriod = 1 to 1

    // linear
    for (it in prices.indices) {
        if (maxPrice[it] - minPrice[it] > bestDifference) {
            bestDifference = maxPrice[it] - minPrice[it]
            // days should start with 1
            bestPeriod = minPriceDay[it] + 1 to maxPriceDay[it] + 1
        }
    }

    return bestPeriod
}

/**
 * Задача Иосифа Флафия.
 * Простая
 *
 * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
 *
 * 1 2 3
 * 8   4
 * 7 6 5
 *
 * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
 * Человек, на котором остановился счёт, выбывает.
 *
 * 1 2 3
 * 8   4
 * 7 6 х
 *
 * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
 * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
 *
 * 1 х 3
 * 8   4
 * 7 6 Х
 *
 * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
 *
 * 1 Х 3
 * х   4
 * 7 6 Х
 *
 * 1 Х 3
 * Х   4
 * х 6 Х
 *
 * х Х 3
 * Х   4
 * Х 6 Х
 *
 * Х Х 3
 * Х   х
 * Х 6 Х
 *
 * Х Х 3
 * Х   Х
 * Х х Х
 *
 * Общий комментарий: решение из Википедии для этой задачи принимается,
 * но приветствуется попытка решить её самостоятельно.
 *
 *   Time Complexity: Θ(n), n - menNumber
 * Memory Complexity: Θ(1)
 */
fun josephTask(menNumber: Int, choiceInterval: Int): Int {
    // menNumber = 1
    var previous = 1

    for (it in 2..menNumber) {
        previous = (choiceInterval - 1 + previous) % it + 1
    }

    return previous
}

/**
 * Наибольшая общая подстрока.
 * Средняя
 *
 * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
 * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
 * Если общих подстрок нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 * Если имеется несколько самых длинных общих подстрок одной длины,
 * вернуть ту из них, которая встречается раньше в строке first.
 *
 *   Time Complexity: Θ(mn), m - length of the first string
 * Memory Complexity: Θ(mn), n - length of the second string
 */
fun longestCommonSubstringOld(first: String, second: String): String {
    if (first.isEmpty() || second.isEmpty())
        return ""

    // Θ(m * n)
    // counts[i][j] - the number of matching characters met starting
    // from first[i] and second[j] (including them) and moving back
    // e. g. for "abcxxx" and "kkcxxi" counts[4][4] = 3 ("cxx")
    //              --^          --^
    val counts = Array(first.length) { IntArray(second.length) }

    // bestEnd points to the end of the longest common substring
    // first is the index of the last character in the first string
    // and second is the index of the last character in the second one.
    // It's not necessary to store both of them but I decided to do it
    // for the sake of readability. It's easier to catch up the idea if end
    // is fully defined, not partially (imho)
    var bestEnd = 0 to 0
    // same as counts[bestEnd]. Used to improve readability
    var bestLength = 0

    // linear
    for (j in second.indices) {
        if (first[0] == second[j]) {
            counts[0][j] = 1
        } else {
            counts[0][j] = 0
        }
    }

    // linear
    for (i in 1 until first.length) {
        if (first[i] == second[0]) {
            counts[i][0] = 1
        } else {
            counts[i][0] = 0
        }
    }

    // Θ(mn)
    for (i in 1 until first.length) {
        for (j in 1 until second.length) {
            if (first[i] == second[j]) {
                counts[i][j] = counts[i - 1][j - 1] + 1
            } else {
                counts[i][j] = 0
            }
        }
    }

    // Θ(mn)
    for (i in first.indices) {
        for (j in second.indices) {
            if (bestLength < counts[i][j]) {
                bestLength = counts[i][j]
                bestEnd = i to j
            }
        }
    }

    return first.substring(bestEnd.first - (bestLength - 1), bestEnd.first + 1)
}

/**
 * Наибольшая общая подстрока.
 * Средняя
 *
 * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
 * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
 * Если общих подстрок нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 * Если имеется несколько самых длинных общих подстрок одной длины,
 * вернуть ту из них, которая встречается раньше в строке first.
 *
 *   Time Complexity: Θ(mn),        m - length of the first string
 * Memory Complexity: Θ(min{m, n}), n - length of the second string
 */
fun longestCommonSubstring(first: String, second: String): String {
    if (first.isEmpty() || second.isEmpty())
        return ""

    // for min{m, n}
    if (first.length < second.length)
        return longestCommonSubstring(second, first)

    // instead of storing the whole counts[][] table
    // as we did in the previous implementation we now
    // only care about the last (1) and the previous (0) line.
    // additionally we add a bias at the beginning (counting[0] = 0)
    // to simplify further calculations.
    val counts = Array(2) { IntArray(second.length + 1) }
    // index of the end of the longest common substring
    // in the second string. In counting[j] j = 1 corresponds to
    // the first character of the second string whereas in bestEndJ
    // j = 0 does. Don't ask me why I made it this way
    var bestEndJ = 0
    // same as for the previous implementation. It's here to make
    // the code a bit more readable
    var bestLength = 0

    // linear
    // clear the main line
    for (j in 0..second.length) {
        counts[1][j] = 0
    }

    // Θ(mn)
    for (i in first.indices) {
        // copy the main line into the
        // temporary one
        for (j in 0..second.length) {
            counts[0][j] = counts[1][j]
        }

        for (j in second.indices) {
            if (first[i] == second[j]) {
                counts[1][j + 1] = counts[0][j] + 1

                if (bestLength < counts[1][j + 1]) {
                    bestLength = counts[1][j + 1]
                    bestEndJ = j
                }
            } else {
                counts[1][j + 1] = 0
            }
        }
    }

    return second.substring(bestEndJ - (bestLength - 1), bestEndJ + 1)
}

/**
 * Число простых чисел в интервале
 * Простая
 *
 * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
 * Если limit <= 1, вернуть результат 0.
 *
 * Справка: простым считается число, которое делится нацело только на 1 и на себя.
 * Единица простым числом не считается.
 *
 *   Time Complexity: Θ(n * sqrt(n)), n - number
 * Memory Complexity: Θ(1)
 */
fun calcPrimesNumber(limit: Int): Int {
    if (limit <= 1)
        return 0

    /**
     * Returns true if the number is prime
     *
     *   Time Complexity: Θ(sqrt(n)), n - number
     * Memory Complexity: Θ(1)
     */
    fun isPrime(number: Int): Boolean {
        if (number == 1)
            return false

        for (it in 2..sqrt(number.toFloat()).toInt())
            if (number % it == 0)
                return false

        return true
    }

    var count = 0

    // Θ(n * sqrt(n))
    for (it in 2..limit) {
        if (isPrime(it))
            count += 1
    }

    return count
}

/**
 * A tree that represents a set of words. Same idea as
 * for prefix trees but for single characters instead of
 * prefixes. (I probably should have used a prefix tree but
 * it had been too late by the time I realized it)
 */
class CharTree(private val value: Char) {
    /**
     * Subtrees
     */
    private val children = mutableMapOf<Char, CharTree>()
    /**
     * If true than there's a word that
     * ends with this node's value
     */
    private var isEnd = false
    /**
     * Count of strings in all subtrees (a string ends with a
     * node with isEnd = true). This field exists for
     * optimization purposes
     */
    private var count: Int = 0

    /**
     * Adds a new string into the structure
     *
     *   Time Complexity: Θ(n), n - length of the string
     * Memory Complexity: Θ(n)
     */
    fun add(string: String, index: Int = 0) {
        count += 1

        if (index == string.length) {
            isEnd = true
            return
        }

        // Θ(1)
        var next = children[string[index]]

        if (next == null) {
            next = CharTree(string[index])
            children[string[index]] = next
        }

        // one add per each character
        next.add(string, index + 1)
    }

    /**
     * Returns true if it managed to remove a string
     * from the structure
     *
     *   Time Complexity: O(n), n - length of the string
     * Memory Complexity: Θ(1)
     */
    fun tryRemove(string: String, index: Int = 0): Boolean {
        if (index == string.length) {
            if (isEnd) {
                count -= 1
                isEnd = false
                return true
            }

            return false
        }

        val next = children[string[index]]
            ?: return false

        val removed = next.tryRemove(string, index + 1)

        if (removed) {
            count -= 1
        }

        if (next.count == 0) {
            children.remove(string[index])
        }

        return removed
    }

    /**
     * Creates a set based on this tree contents
     *
     *   Time Complexity: Θ(n), n - number of nodes
     * Memory Complexity: Θ(n)
     */
    fun toSet(): Set<String> {
        // dfs
        val result = mutableSetOf<String>()
        val nodes = LinkedList<CharTree>()
        // I definitely should have used prefix trees
        val prefixes = LinkedList<String>()

        nodes.add(this)
        prefixes.add("")

        while (nodes.size > 0) {
            val node = nodes.removeLast()
            val prefix = prefixes.removeLast()

            if (node.isEnd) {
                result.add(prefix)
            }

            for (it in node.children) {
                nodes.add(it.value)
                prefixes.add(prefix + it.value.value)
            }
        }

        return result
    }

    /**
     * For debugging
     */
    fun print(prefix: String = "-") {
        for (it in children.keys) {
            kotlin.io.print("$prefix $it (${children[it]?.count})")

            if (children[it]?.isEnd == true) {
                kotlin.io.print(" <-")
            }

            println()
            children[it]?.print("$prefix-")
        }
    }
}

/**
 * Represents a vertex of the letters graph
 */
class CharVertex(private val value: Char) {
    /**
     * Neighbours
     */
    private val children = LinkedList<CharVertex>()

    /**
     * Creates a bidirectional edge between
     * the two vertices
     */
    infix fun bind(other: CharVertex) {
        this.children.add(other)
        other.children.add(this)
    }

    /**
     * Removes words that are present in the graph
     * from the CharTree
     *
     *   Time Complexity: O(mn), m - number of vertices
     * Memory Complexity: Θ(m),  n - average string length
     */
    fun scan(wordsTree: CharTree) {
        // dfs
        // we don't wont to visit the letter that
        // has already been used in our path
        // so the visiting history is unique for any path
        val visited = LinkedList<MutableSet<CharVertex>>()
        val vertices = LinkedList<CharVertex>()
        val prefixes = LinkedList<String>()

        vertices.add(this)
        prefixes.add(value.toString())
        visited.add(mutableSetOf())

        // Θ(m)
        while (vertices.size > 0) {
            val history = visited.removeLast()
            val vertex = vertices.removeLast()
            val prefix = prefixes.removeLast()

            // O(n), n - length of prefix
            wordsTree.tryRemove(prefix)

            for (each in vertex.children) {
                if (!history.contains(each)) {
                    vertices.add(each)
                    prefixes.add(prefix + each.value)
                    visited.add((history + vertex) as MutableSet<CharVertex>)
                }
            }
        }
    }
}

/**
 * Балда
 * Сложная
 *
 * В файле с именем inputName задана матрица из букв в следующем формате
 * (отдельные буквы в ряду разделены пробелами):
 *
 * И Т Ы Н
 * К Р А Н
 * А К В А
 *
 * В аргументе words содержится множество слов для поиска, например,
 * ТРАВА, КРАН, АКВА, НАРТЫ, РАК.
 *
 * Попытаться найти каждое из слов в матрице букв, используя правила игры БАЛДА,
 * и вернуть множество найденных слов. В данном случае:
 * ТРАВА, КРАН, АКВА, НАРТЫ
 *
 * И т Ы Н     И т ы Н
 * К р а Н     К р а н
 * А К в а     А К В А
 *
 * Все слова и буквы -- русские или английские, прописные.
 * В файле буквы разделены пробелами, строки -- переносами строк.
 * Остальные символы ни в файле, ни в словах не допускаются.
 *
 *   Time Complexity: O(l^2 k), l - number letters in table
 * Memory Complexity: Θ(l),     k - average string length
 */
fun baldaSearcher(inputName: String, words: Set<String>): Set<String> {
    val wordsTree = CharTree(' ')

    // linear for the number of words
    for (it in words) {
        wordsTree.add(it)
    }

    // proportional to the size of the table
    val letters = File(inputName).readLines()
        .map {
            it.split(" ")
                .map { that -> CharVertex(that[0]) }
                .toTypedArray()
        }
        .toTypedArray()

    // linear
    for (i in 1 until letters.size) {
        letters[i].first() bind letters[i - 1].first()
        letters[i].last() bind letters[i - 1].last()
    }

    // linear
    for (j in 1 until letters[0].size) {
        letters.first()[j] bind letters.first()[j - 1]
        letters.last()[j] bind letters.last()[j - 1]
    }

    // Θ(mn)
    for (i in 1 until letters.size) {
        for (j in 1 until letters[i].size) {
            letters[i][j] bind letters[i][j - 1]
            letters[i][j] bind letters[i - 1][j]
        }
    }

    // Θ(m^2 n^2 k) time, where k - average string length
    for (i in letters.indices) {
        for (j in letters[i].indices) {
            letters[i][j].scan(wordsTree)
        }
    }

//    wordsTree.print()
//    println()
//
//    println("To set: " + wordsTree.toSet())

    return words - wordsTree.toSet()
}