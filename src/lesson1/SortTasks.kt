@file:Suppress("UNUSED_PARAMETER")

package lesson1

import java.io.File


/**
 * Returns null if the condition fails
 * and true otherwise.
 *
 * As pointed out by mglukhikh, I could have used
 * `require` instead by I find it more beautiful
 * to write the condition via a lambda.
 */
inline fun ensure(condition: () -> Boolean): Boolean? {
    if (!condition()) {
        return null
    }
    return true
}

/**
 * Throws an Exception with the specified message
 * if this is null.
 *
 * Used in pair with ensure() to throw an exception
 */
infix fun Boolean?.or(right: () -> String) {
    this ?: throw Exception(right())
}

/**
 * Сортировка времён
 *
 * Простая
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
 * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
 *
 * Пример:
 *
 * 01:15:19 PM
 * 07:26:57 AM
 * 10:00:03 AM
 * 07:56:14 PM
 * 01:15:19 PM
 * 12:40:31 AM
 *
 * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
 * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
 *
 * 12:40:31 AM
 * 07:26:57 AM
 * 10:00:03 AM
 * 01:15:19 PM
 * 01:15:19 PM
 * 07:56:14 PM
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 *
 *   Time Complexity: O(nlogn)
 * Memory Complexity: *probably* Θ(n), n - the number of lines
 */
fun sortTimes(inputName: String, outputName: String) {
    // just a helper for passing tuple-4
    data class Time(
        var hours: Int,
        var minutes: Int,
        var seconds: Int,
        var period: String
    )

    val text = File(inputName).readLines()
        // linear time & memory
        .map {
            val match = "(\\d\\d):(\\d\\d):(\\d\\d) (AM|PM)".toRegex().find(it)
                ?: throw Exception("Incorrect format")

            val hours = match.groupValues[1].toInt()
            val minutes = match.groupValues[2].toInt()
            val seconds = match.groupValues[3].toInt()
            val period = match.groupValues[4]

            ensure { hours in 0..12 } or { "Incorrect format" }
            ensure { minutes in 0..59 } or { "Incorrect format" }
            ensure { seconds in 0..59 } or { "Incorrect format" }

            Time(hours, minutes, seconds, period) to it
        }
        // O(nlogn) time & Θ(1) memory if qsort is used in place
        .sortedWith(
            // `% 12` because `12 AM` is actually `0 AM`
            compareBy({ it.first.period }, { it.first.hours % 12 }, { it.first.minutes }, { it.first.seconds })
        )
        // linear time
        .joinToString("\n") {
            it.second
        }

    File(outputName).writeText(text)
}

/**
 * Сортировка адресов
 *
 * Средняя
 *
 * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
 * где они прописаны. Пример:
 *
 * Петров Иван - Железнодорожная 3
 * Сидоров Петр - Садовая 5
 * Иванов Алексей - Железнодорожная 7
 * Сидорова Мария - Садовая 5
 * Иванов Михаил - Железнодорожная 7
 *
 * Людей в городе может быть до миллиона.
 *
 * Вывести записи в выходной файл outputName,
 * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
 * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
 *
 * Железнодорожная 3 - Петров Иван
 * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
 * Садовая 5 - Сидоров Петр, Сидорова Мария
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 *
 *   Time Complexity: Θ(K * V), K - number of addresses
 * Memory Complexity: Θ(K * V), V - average number of people per address.
 *                  Eventually, K * V ~= n - number of lines
 */
fun sortAddresses(inputName: String, outputName: String) {
    val text = File(inputName).readLines()
        // linear time & memory
        .map {
            "(\\S+ \\S+) - (\\S+) (\\d+)".toRegex().find(it)
                ?: throw Exception("Incorrect format")
        }
        // linear time + in fact linear memory because no new data is created
        // only the one we already have is reshaped into another data structure
        // but lets assume K - the number of addresses and V - average number of
        // people per address. then it'll be K * V
        .groupBy(
            keySelector = { it.groupValues[2] to it.groupValues[3] },
            valueTransform = { it.groupValues[1] }
        )
        // Θ(K)
        .mapValues {
            // O(V)
            it.value.sorted()
        }
        // O(KlogK) for time and Θ(1) for memory if qsort is used in place
        .toSortedMap(
            compareBy({ it.first }, { it.second.toInt() })
        )
        // Θ(K)
        .map {
            // Θ(V)
            it.key.first + " " + it.key.second + " - " + it.value.joinToString(", ")
        }
        // Θ(K)
        .joinToString("\n")

    File(outputName).writeText(text)
}

fun radixStringSort(elements: Array<String>, limit: Int): Array<String> {
    for (it in 0 until limit) {
        val count = IntArray(10) { 0 }

        for (that in elements) {
            val symbol = that.getOrElse(that.length - 1 - it) { '0' }
            count[symbol - '0']++
        }

        for (that in 1 until count.size) {
            count[that] += count[that - 1]
        }

        val sorted = Array(elements.size) { "" }

        for (that in elements.reversed()) {
            val symbol = that.getOrElse(that.length - 1 - it) { '0' }
            count[symbol - '0']--
            sorted[count[symbol - '0']] = that
        }

        for (that in sorted.indices) {
            elements[that] = sorted[that]
        }
    }

    return elements
}

/**
 * Сортировка температур
 *
 * Средняя
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
 * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
 * Например:
 *
 * 24.7
 * -12.6
 * 121.3
 * -98.4
 * 99.5
 * -12.6
 * 11.0
 *
 * Количество строк в файле может достигать ста миллионов.
 * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
 * Повторяющиеся строки сохранить. Например:
 *
 * -98.4
 * -12.6
 * -12.6
 * 11.0
 * 24.7
 * 99.5
 * 121.3
 */
fun sortTemperatures(inputName: String, outputName: String) {
    val positives = mutableListOf<String>()
    val negatives = mutableListOf<String>()

    File(inputName).readLines()
        .forEach {
            val match = """-?(\d+)\.(\d)""".toRegex().find(it)
                ?: throw Exception("Incorrect format")

            val target = if (it.startsWith('-')) negatives else positives
            target.add(match.groupValues[1] + match.groupValues[2])
        }

    var positivesArray = positives.toTypedArray()
    var negativesArray = negatives.toTypedArray()

    // for some reason arrays get passed by value (?!)
    positivesArray = radixStringSort(positivesArray, 4)
    negativesArray = radixStringSort(negativesArray, 4)

    fun toNumber(it: String): String {
        var first = it.substring(0, it.length - 1)
        var second = it.substring(it.length - 1)

        if (first.isEmpty()) {
            first = "0"
        }

        if (second.isEmpty()) {
            second = "0"
        }

        return "$first.$second"
    }

    val negativesPart = negativesArray.reversedArray().joinToString("\n") {
        "-" + toNumber(it)
    }

    val positivesPart = positivesArray.joinToString("\n") {
        toNumber(it)
    }

    val parts = listOf(negativesPart, positivesPart)
        .filter { it.isNotEmpty() }
        .joinToString("\n")

    File(outputName).writeText(parts)
}

/**
 * Сортировка последовательности
 *
 * Средняя
 * (Задача взята с сайта acmp.ru)
 *
 * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
 *
 * 1
 * 2
 * 3
 * 2
 * 3
 * 1
 * 2
 *
 * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
 * а если таких чисел несколько, то найти минимальное из них,
 * и после этого переместить все такие числа в конец заданной последовательности.
 * Порядок расположения остальных чисел должен остаться без изменения.
 *
 * 1
 * 3
 * 3
 * 1
 * 2
 * 2
 * 2
 *
 *   Time Complexity: Θ(n), n - the number of lines
 * Memory Complexity: Θ(n)
 */
fun sortSequence(inputName: String, outputName: String) {
    // linear
    val numbers = File(inputName).readLines()
        .map { it.toInt() }

    // linear
    var counts = numbers
        .groupingBy { it }
        .eachCount()

    // linear
    val maxValue = counts.values
        .max()
        ?: 0

    // linear
    counts = counts.filter { it.value == maxValue }

    // linear
    val target = counts.keys
        .min()
        ?: 0

    // linear
    val reducedNumbers = mutableListOf<Int>()
    val appendix = mutableListOf<Int>()

    // linear
    numbers.forEach {
        if (it != target) {
            reducedNumbers.add(it)
        } else {
            appendix.add(it)
        }
    }

    // linear
    val text = (reducedNumbers + appendix)
        .joinToString("\n")

    File(outputName).writeText(text)
}

/**
 * Соединить два отсортированных массива в один
 *
 * Простая
 *
 * Задан отсортированный массив first и второй массив second,
 * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
 * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
 *
 * first = [4 9 15 20 28]
 * second = [null null null null null 1 3 9 13 18 23]
 *
 * Результат: second = [1 3 4 9 9 13 15 20 23 28]
 *
 *   Time Complexity: Θ(n), n - length of the second array
 * Memory Complexity: Θ(1)
 */
fun <T : Comparable<T>> mergeArrays(first: Array<T>, second: Array<T?>) {
    var secondIndex = first.size
    var firstIndex = 0

    for (it in second.indices) {
        if (
            secondIndex >= second.size ||
            firstIndex < first.size && first[firstIndex] <= second[secondIndex]!!
        ) {
            second[it] = first[firstIndex++]
        } else {
            second[it] = second[secondIndex++]
        }
    }
}

