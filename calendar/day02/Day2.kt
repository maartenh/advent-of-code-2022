package day02

import Day
import Lines

class Day2 : Day() {
    override fun part1(input: Lines): Any {
        return input
            .mapNotNull { lineScore[it] }
            .sum()
    }

    override fun part2(input: Lines): Any {
        return input
            .mapNotNull { decrypt[it] }
            .mapNotNull { lineScore[it] }
            .sum()
    }

    val lineScore = mapOf(
        "A X" to 1 + 3,
        "A Y" to 2 + 6,
        "A Z" to 3 + 0,
        "B X" to 1 + 0,
        "B Y" to 2 + 3,
        "B Z" to 3 + 6,
        "C X" to 1 + 6,
        "C Y" to 2 + 0,
        "C Z" to 3 + 3,
    )

    val decrypt = mapOf(
        "A X" to "A Z",
        "A Y" to "A X",
        "A Z" to "A Y",
        "B X" to "B X",
        "B Y" to "B Y",
        "B Z" to "B Z",
        "C X" to "C Y",
        "C Y" to "C Z",
        "C Z" to "C X",
    )
}