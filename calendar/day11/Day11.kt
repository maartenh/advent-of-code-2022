package day11

import Day
import Lines

class Day11 : Day() {
    override fun part1(input: Lines): Any {
        val monkeys = createMonkeys()

        val worryMod = monkeys.values.map { it.testDivisibleBy }.fold(1L) { acc, b -> acc * b }

        (1..20).forEach { round ->
            monkeys.keys.sorted().forEach { monkeyNumber ->
                monkeys[monkeyNumber]!!.playTurn(monkeys, true, worryMod)
            }
        }

        return monkeys.entries.map { it.key to it.value.inspectionCount }
            .sortedByDescending { it.second }
            .take(2)
            .map { it.second}
            .fold(1) { acc, b -> acc * b }
    }

    override fun part2(input: Lines): Any {
        val monkeys = createMonkeys()

        val worryMod = monkeys.values.map { it.testDivisibleBy }.fold(1L) { acc, b -> acc * b }

        (1..10000).forEach { round ->
            monkeys.keys.sorted().forEach { monkeyNumber ->
                monkeys[monkeyNumber]!!.playTurn(monkeys, false, worryMod)
            }
        }

        return monkeys.entries.map { it.key to it.value.inspectionCount }
            .sortedByDescending { it.second }
            .take(2)
            .map { it.second}
            .fold(1.toBigInteger()) { acc, b -> acc * b.toBigInteger() }
    }

    companion object {
        fun createMonkeys(): Map<Int, Monkey> =
            mapOf(
                0 to Monkey(
                    listOf(64),
                    { c -> c * 7 },
                    13,
                    1,
                    3
                ),
                1 to Monkey(
                    listOf(60, 84, 84, 65),
                    { c -> c + 7 },
                    19,
                    2,
                    7
                ),
                2 to Monkey(
                    listOf(52, 67, 74, 88, 51, 61),
                    { c -> c * 3 },
                    5,
                    5,
                    7
                ),
                3 to Monkey(
                    listOf(67, 72),
                    { c -> c + 3 },
                    2,
                    1,
                    2
                ),
                4 to Monkey(
                    listOf(80, 79, 58, 77, 68, 74, 98, 64),
                    { c -> c * c },
                    17,
                    6,
                    0
                ),
                5 to Monkey(
                    listOf(62, 53, 61, 89, 86),
                    { c -> c + 8 },
                    11,
                    4,
                    6
                ),
                6 to Monkey(
                    listOf(86, 89, 82),
                    { c -> c + 2 },
                    7,
                    3,
                    0
                ),
                7 to Monkey(
                    listOf(92, 81, 70, 96, 69, 84, 83),
                    { c -> c + 4 },
                    3,
                    4,
                    5
                ),
            )

    }
}

class Monkey(
    var items: List<Long>,
    val inspect: (Long) -> Long,
    val testDivisibleBy: Long,
    val trueTarget: Int,
    val falsetarget: Int
) {
    var inspectionCount = 0

    fun catch(item: Long) {
        items += item
    }

    fun playTurn(monkeys: Map<Int, Monkey>, applyRelief: Boolean, worryMod: Long) {
        items.forEach { item ->
            val newWorry = inspect(item)
            val finalWorry = if (applyRelief) { relief(newWorry) % worryMod } else newWorry % worryMod
            if (finalWorry % testDivisibleBy == 0L) {
                monkeys[trueTarget]!!.catch(finalWorry)
            } else {
                monkeys[falsetarget]!!.catch(finalWorry)
            }
        }

        inspectionCount += items.size
        items = listOf()
    }

    private fun relief(worry: Long): Long = worry / 3
}