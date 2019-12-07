@file:Suppress("UNUSED_PARAMETER")

package lesson6

import java.io.File
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Runs usual lcs but without remembering
 * the whole table and only returns it's last row.
 *
 *   Time Complexity: Θ(nm), n -  first.length
 * Memory Complexity: Θ(n),  m - second.length
 */
fun longestCommonSubsequenceLengthsRow(first: String, second: String): IntArray {
    // Θ(first.length)
    if (first.isEmpty() || second.isEmpty()) {
        return IntArray(first.length) { 0 }
    }

    // Θ(first.length)
    val row = IntArray(first.length)

    if (first.first() == second.first()) {
        row[0] = 1
    }

    // initialize the first row
    // Θ(first.length)
    for (it in 1 until row.size) {
        if (first[it] == second.first()) {
            row[it] = 1
        } else {
            row[it] = row[it - 1]
        }
    }

    // Θ(first.length * second.length)
    for (that in 1 until second.length) {
        // Θ(first.length)
        val newRow = IntArray(first.length)

        if (first.first() == second[that]) {
            newRow[0] = 1
        } else {
            newRow[0] = row[0]
        }

        // Θ(first.length)
        for (it in 1 until row.size) {
            if (first[it] == second[that]) {
                newRow[it] = row[it - 1] + 1
            } else {
                newRow[it] = max(newRow[it - 1], row[it])
            }
        }

        // Θ(first.length)
        for (it in row.indices) {
            row[it] = newRow[it]
        }
    }

    return row
}

/**
 * Наибольшая общая подпоследовательность.
 * Средняя
 *
 * Дано две строки, например "nematode knowledge" и "empty bottle".
 * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
 * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
 * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
 * Если общей подпоследовательности нет, вернуть пустую строку.
 * Если есть несколько самых длинных общих подпоследовательностей, вернуть любую из них.
 * При сравнении подстрок, регистр символов *имеет* значение.
 *
 *   Time Complexity: Θ(nm),        n - first.length
 * Memory Complexity: Θ(min{n, m}), m - second.length
 */
fun longestCommonSubSequence(first: String, second: String): String {
    // Hirschberg

    if (first.isEmpty() || second.isEmpty()) {
        return ""
    }

    // a trick for lowering potential memory usage
    // because longestCommonSubsequenceLengthsRow
    // takes Θ(length) of it's first argument
    if (first.length > second.length) {
        return longestCommonSubSequence(second, first)
    }

    // since we always divide second
    // by 2 we have length = 1 as a special
    // case
    // Θ(first.length)
    if (second.length == 1) {
        for (it in first) {
            if (it == second.first()) {
                return it.toString()
            }
        }

        return ""
    }

    // handy aliases

    // Θ(first.length + second.length) for summary
    val secondLowerPart = second.substring(0 until second.length / 2)
    val secondUpperPart = second.substring(second.length / 2)
    val secondUpperPartReversed = secondUpperPart.reversed()
    val firstReversed = first.reversed()

    // size == first.length
    // Θ(nm) time
    val lowerPartRow = longestCommonSubsequenceLengthsRow(first, secondLowerPart)
    val upperPartRow = longestCommonSubsequenceLengthsRow(firstReversed, secondUpperPartReversed)

    // find max combination
    // start with |"f1f2f3..."
    // | - separator, fi - chars of `first`
    var max = upperPartRow.last()
    var maxIndex = 0

    // check "...fn"|
    if (lowerPartRow.last() > max) {
        max = lowerPartRow.last()
        maxIndex = first.length
    }

    // check everything in between
    for (it in 1 until first.length) {
        val newer = lowerPartRow[it - 1] + upperPartRow[first.length - it - 1]

        if (newer > max) {
            max = newer
            maxIndex = it
        }
    }

    return longestCommonSubSequence(
        first.substring(0 until maxIndex),
        secondLowerPart
    ) + longestCommonSubSequence(
        first.substring(maxIndex),
        secondUpperPart
    )
}

/**
 * Наибольшая возрастающая подпоследовательность
 * Сложная
 *
 * Дан список целых чисел, например, [2 8 5 9 12 6].
 * Найти в нём самую длинную возрастающую подпоследовательность.
 * Элементы подпоследовательности не обязаны идти подряд,
 * но должны быть расположены в исходном списке в том же порядке.
 * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
 * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
 * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
 *
 *   Time Complexity: Θ(n), n - size of the list
 * Memory Complexity: Θ(n)
 */
fun longestIncreasingSubSequence(list: List<Int>): List<Int> {
    if (list.isEmpty()) {
        return emptyList()
    }

    if (list.size == 1) {
        return listOf(list.first())
    }

    // length of the longest possible
    // sequence that i-th number can be
    // a part of
    // Θ(n)
    val lengths = IntArray(list.size) { 1 }
    // previous number needed to
    // reconstruct the chain
    // Θ(n)
    val previous = IntArray(list.size) { -1 }

    // Θ(n^2)
    for (i in 1 until list.size) {
        var maxFound = list[0] < list[i]
        var max = 0

        // check all previous and find the
        // one that's less than list[i] but
        // has the max calculated length
        // Θ(n)
        for (j in 0 until i) {
            if (
                list[j] < list[i] &&
                lengths[max] < lengths[j]
            ) {
                maxFound = true
                max = j
            }
        }

        // make i point to it as a previous
        // node
        if (maxFound) {
            previous[i] = max
            lengths[i] = lengths[max] + 1
        }
    }

    // O(n)
    val sequence = LinkedList<Int>()
    // index of the last number of the longest
    // sequence
    var index = 0

    // find index
    for (it in 1 until lengths.size) {
        if (lengths[index] < lengths[it]) {
            index = it
        }
    }

    // O(n)
    while (index >= 0) {
        sequence.add(list[index])
        index = previous[index]
    }

    // Θ(n)
    return sequence.reversed()
}

/**
 * Самый короткий маршрут на прямоугольном поле.
 * Средняя
 *
 * В файле с именем inputName задано прямоугольное поле:
 *
 * 0 2 3 2 4 1
 * 1 5 3 4 6 2
 * 2 6 2 5 1 3
 * 1 4 3 2 6 2
 * 4 2 3 1 5 0
 *
 * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
 * В каждой клетке записано некоторое натуральное число или нуль.
 * Необходимо попасть из верхней левой клетки в правую нижнюю.
 * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
 * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
 *
 * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
 *
 *   Time Complexity: Θ(nm), n - width
 * Memory Complexity: Θ(nm), m - height
 */
fun shortestPathOnField(inputName: String): Int {
    // Θ(width * height)
    val field = ArrayList<IntArray>()

    // Θ(width * height)
    File(inputName).forEachLine { line ->
        // Θ(width)
        field.add(line.split(' ').map { it.toInt() }.toIntArray())
    }

    if (
        field.isEmpty() ||
        field.first().isEmpty()
    ) {
        return 0
    }

    val width = field.first().size
    val height = field.size

    // top row
    // Θ(width)
    for (j in 1 until width) {
        field[0][j] += field[0][j - 1]
    }

    // left column
    // Θ(height)
    for (i in 1 until height) {
        field[i][0] += field[i - 1][0]
    }

    // Θ(width * height)
    for (i in 1 until height) {
        for (j in 1 until width) {
            val sumWithUpper = field[i - 1][j] + field[i][j]
            val sumWithLefter = field[i][j - 1] + field[i][j]
            val sumWithDiagonal = field[i - 1][j - 1] + field[i][j]
            field[i][j] = min(min(sumWithLefter, sumWithUpper), sumWithDiagonal)
        }
    }

    return field[height - 1][width - 1]
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5