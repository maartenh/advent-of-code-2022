package day04

import Day
import Lines

class Day4 : Day() {
    override fun part1(input: Lines): Any {
        return input
            .map(String::toSetPair)
            .count {
                it[0].containsAll(it[1]) || it[1].containsAll(it[0])
            }
    }

    override fun part2(input: Lines): Any {
        return input
            .map(String::toSetPair)
            .count {
                it[0].intersect(it[1]).isNotEmpty()
            }
    }

}

fun String.toSetPair(): List<Set<Int>> {
    val parts = split(',', '-')
    return listOf(
        (parts[0].toInt() .. parts[1].toInt()).toSet(),
        (parts[2].toInt() .. parts[3].toInt()).toSet()
    )
}
