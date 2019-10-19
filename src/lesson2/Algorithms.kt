@file:Suppress("UNUSED_PARAMETER")

package lesson2

import java.io.File
import java.util.*
import kotlin.Exception

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
 * Memory Complexity: Θ(1)
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

    // Θ(1)
    fun delta(period: Pair<Int, Int>) = prices[period.second] - prices[period.first]

    // buyIndex to sellIndex
    var bestPeriod = 0 to 0
    // when to buy
    var minIndex = 0

    // linear
    for (it in 1 until prices.size) {
        if (prices[it] < prices[minIndex]) {
            minIndex = it
        }

        val newTrailingPeriod = minIndex to it

        if (delta(newTrailingPeriod) > delta(bestPeriod)) {
            bestPeriod = newTrailingPeriod
        }
    }

    return bestPeriod.first + 1 to bestPeriod.second + 1
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
@Suppress("unused")
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
 *   Time Complexity: O(n^2), n - number
 * Memory Complexity: Θ(n)
 */
fun calcPrimesNumber(limit: Int): Int {
    if (limit <= 1)
        return 0

    // isPrime[i] == true <=> i - prime
    // isPrime[0] and isPrime[1] are not used
    // and left here for readability
    val isPrime = BooleanArray(limit + 1) { true }
    // next always points to the next prime
    var next = 2

    // O(n^2) because I can't tell for sure
    // how many prime numbers are there
    while (next < isPrime.size) {
        // mark all multiples of next as non-prime
        // O(n) time
        for (it in next + next until isPrime.size step next) {
            isPrime[it] = false
        }

        // search the next prime starting with it
        val checkFrom = next + 1
        // if we won't find the next prime we'll
        // use the out-of-range index to stop the outer while
        next = isPrime.size

        // find the next prime
        // O(n) time
        for (it in checkFrom until isPrime.size) {
            if (isPrime[it]) {
                next = it
                break
            }
        }
    }

    var count = 0

    // count primes in array
    // O(n) time
    for (it in 2 until isPrime.size) {
        if (isPrime[it]) {
            count++
        }
    }

    return count
}

/**
 * A prefix tree. A separate instance of CharTree is used to
 * represent each possible prefix extension (for a word of length n
 * there will be n separate instances of CharTree each presenting
 * a different prefix [+1 CharTree for the root])
 */
class CharTree(private val prefix: String = "") {
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
            next = CharTree(string.substring(0, index + 1))
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
     * Returns the total number of words that start
     * with the given prefix
     *
     *   Time Complexity: O(n), n - length of the prefix
     * Memory Complexity: Θ(1)
     */
    fun countFor(string: String, index: Int = 0): Int {
        if (index == string.length) {
            return count
        }

        val next = children[string[index]]
            ?: return 0

        return next.countFor(string, index + 1)
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

        nodes.add(this)

        while (nodes.size > 0) {
            val node = nodes.removeLast()

            if (node.isEnd) {
                result.add(node.prefix)
            }

            for (it in node.children) {
                nodes.add(it.value)
            }
        }

        return result
    }

    /**
     * For debugging
     */
    @Suppress("MemberVisibilityCanBePrivate", "unused")
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
                val furtherPrefix = prefix + each.value

                if (
                    !history.contains(each) &&
                    wordsTree.countFor(furtherPrefix) > 0
                ) {
                    vertices.add(each)
                    prefixes.add(furtherPrefix)
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
    if (words.isEmpty())
        return emptySet()

    val wordsTree = CharTree()

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

    if (letters.isEmpty())
        return emptySet()

    // linear
    for (i in 1 until letters.size) {
        if (letters[i - 1].size != letters[i].size) {
            throw Exception("Table of symbols must be rectangular")
        }
    }

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

    return words - wordsTree.toSet()
}