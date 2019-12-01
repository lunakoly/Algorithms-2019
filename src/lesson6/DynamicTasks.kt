@file:Suppress("UNUSED_PARAMETER")

package lesson6

import kotlin.math.max

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
 */
fun longestIncreasingSubSequence(list: List<Int>): List<Int> {
    TODO()
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
 */
fun shortestPathOnField(inputName: String): Int {
    TODO()
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5