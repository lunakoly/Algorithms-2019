@file:Suppress("UNUSED_PARAMETER")

package lesson2

import java.io.File
import kotlin.Exception
import kotlin.math.max

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
 */
fun longestCommonSubstring(first: String, second: String): String {
    TODO()
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
 */
fun calcPrimesNumber(limit: Int): Int {
    TODO()
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
 */
fun baldaSearcher(inputName: String, words: Set<String>): Set<String> {
    TODO()
}