package day03

import Day
import Lines

class Day3 : Day() {
    override fun part1(input: Lines): Any {
        return input
            .mapNotNull { line ->
                val comp1 = line.substring(0, line.length / 2)
                val comp2 = line.substring(line.length / 2, line.length)
                val doubleItemType = comp1.first { comp2.contains(it) }
                priority[doubleItemType]
            }
            .sum()
    }

    override fun part2(input: Lines): Any {
        return input
            .chunked(3)
            .mapNotNull { triple ->
                val commonItemType = triple[0].first {
                    triple[1].contains(it) && triple[2].contains(it)
                }
                priority[commonItemType]
            }
            .sum()
    }

    private val priority = run {
        val priorities = mutableMapOf<Char, Int>()
        for ((index, type) in ('a'..'z').withIndex()) {
            priorities.put(type, index + 1)
        }
        for ((index, type) in ('A'..'Z').withIndex()) {
            priorities.put(type, index + 27)
        }
        priorities.toMap()
    }
}