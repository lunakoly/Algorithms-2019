@file:Suppress("UNUSED_PARAMETER")

package lesson1

import java.io.File


/**
 * Throws an Exception if the condition fails
 */
inline fun ensure(condition: () -> Boolean) {
    if (!condition()) {
        throw Exception("Incorrect format")
    }
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
        .map {
            val match = "(\\d\\d):(\\d\\d):(\\d\\d) (AM|PM)".toRegex().find(it)
                ?: throw Exception("Incorrect format")

            val hours = match.groupValues[1].toInt()
            val minutes = match.groupValues[2].toInt()
            val seconds = match.groupValues[3].toInt()
            val period = match.groupValues[4]

            ensure { hours in 0..12 }
            ensure { minutes in 0..59 }
            ensure { seconds in 0..59 }

            Time(hours, minutes, seconds, period) to it
        }
        .sortedWith(
            // `% 12` because `12 AM` is actually `0 AM`
            compareBy({ it.first.period }, { it.first.hours % 12 }, { it.first.minutes }, { it.first.seconds })
        ).joinToString("\n") {
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
 */
fun sortAddresses(inputName: String, outputName: String) {
    val text = File(inputName).readLines()
        .map {
            "(\\S+ \\S+) - (\\S+) (\\d+)".toRegex().find(it)
                ?: throw Exception("Incorrect format")
        }
        .groupBy(
            keySelector = { it.groupValues[2] to it.groupValues[3] },
            valueTransform = { it.groupValues[1] }
        )
        .mapValues {
            it.value.sorted()
        }
        .toSortedMap(
            compareBy({ it.first }, { it.second.toInt() })
        )
        .map {
            it.key.first + " " + it.key.second + " - " + it.value.joinToString(", ")
        }
        .joinToString("\n")

    File(outputName).writeText(text)
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
    val text = File(inputName).readLines()
        .map {
            it.toFloat()
        }
        .sorted()
        .joinToString("\n")

    File(outputName).writeText(text)
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
 */
fun sortSequence(inputName: String, outputName: String) {
    val numbers = File(inputName).readLines()
        .map { it.toInt() }

    var counts = numbers
        .groupingBy { it }
        .eachCount()

    val maxValue = counts.values
        .max()
        ?: 0

    counts = counts.filter { it.value == maxValue }

    val target = counts.keys
        .min()
        ?: 0

    val reducedNumbers = mutableListOf<Int>()
    val appendix = mutableListOf<Int>()

    numbers.forEach {
        if (it != target) {
            reducedNumbers.add(it)
        } else {
            appendix.add(it)
        }
    }

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

