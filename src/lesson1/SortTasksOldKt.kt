/*
* This file contains different ideas I considered
* but decided not to use. I preferred finding a suitable place
* for them rather than completely deleting everything.
*/

@file:Suppress("unused")

package lesson1

import java.io.File

/**
 * Number of characters that a numeric representation
 * of the time occupies. For `HH:MM:SS TT` the representation
 * will be `1THHMMSS` which is TIME_NUMERICAL_REPRESENTATION_LENGTH long.
 */
const val TIME_NUMERICAL_REPRESENTATION_LENGTH = 8

/**
 * Given a `HH:MM:SS TT`-like string returns a `1THHMMSS`-like int.
 */
fun getTimeNumericalRepresentation(it: String): Int {
    ensure { "\\d\\d:\\d\\d:\\d\\d (?:AM|PM)".toRegex() matches it } or { "Incorrect format" }

    val numeric = if (it.contains("AM")) {
        "0:" + it.subSequence(0, it.length - 3)
    } else {
        "1:" + it.subSequence(0, it.length - 3)
    }

    val parts = numeric.split(":")

    ensure { parts[0].toInt() in 0..23 } or { "Incorrect format" }
    ensure { parts[1].toInt() in 0..59 } or { "Incorrect format" }
    ensure { parts[2].toInt() in 0..59 } or { "Incorrect format" }

    // this 1 is here to save a possible leading zero
    val simplified = "1" + parts.joinToString("")
    return simplified.toInt()
}

/**
 * Given a `1THHMMSS`-like int returns a `HH:MM:SS TT`-like string.
 * The int MUST be a valid number representation.
 */
fun getTimeStringRepresentation(it: Int): String {
    val parts = "1(\\d)(\\d\\d)(\\d\\d)(\\d\\d)".toRegex().find(it.toString())
    val (t, y, m, d) = parts!!.destructured
    val string = "$y:$m:$d "

    return if (t == "1")
        string + "PM"
    else
        string + "AM"
}

/**
 * Sorts integers with Θ(N * limit)
 */
fun radixSort(elements: IntArray, limit: Int): IntArray {
    var intermediate = elements
    var digitRetrievingMultiplier = 1

    for (it in 1..limit) {
        val count = IntArray(10) { 0 }

        for (each in intermediate) {
            count[each / digitRetrievingMultiplier % 10]++
        }

        for (i in 1 until count.size) {
            count[i] += count[i - 1]
        }

        val new = IntArray(intermediate.size)

        for (each in intermediate.reversed()) {
            new[count[each / digitRetrievingMultiplier % 10] - 1] = each
            count[each / digitRetrievingMultiplier % 10]--
        }

        intermediate = new
        digitRetrievingMultiplier *= 10
    }

    return intermediate
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
fun sortTimesOld(inputName: String, outputName: String) {
    val simplified = File(inputName).readLines().map {
        getTimeNumericalRepresentation(it)
    }

    val sorted = radixSort(simplified.toIntArray(), TIME_NUMERICAL_REPRESENTATION_LENGTH)

    val text = sorted.joinToString("\n") {
        getTimeStringRepresentation(it)
    }

    File(outputName).writeText(text)
}