package day01

import Day
import Lines
import java.lang.Integer.min

class Day1 : Day() {
    override fun part1(input: Lines): Any {
        val maxCarried = input
            .splitAt { it.isEmpty() }
            .map { list ->
                list.sumOf { it.toInt() }
            }
            .max()

        return maxCarried
    }

    override fun part2(input: Lines): Any {
        return input
            .splitAt { it.isEmpty() }
            .map { list ->
                list.sumOf { it.toInt() }
            }
            .sortedDescending()
            .take(3)
            .sum()
    }

    fun <E> List<E>.splitAt(predicate: (E) -> Boolean): List<List<E>> {
        val partitions = mutableListOf<List<E>>()

        var source = this
        while (source.isNotEmpty()) {
            val part = source.takeWhile { !predicate(it) }
            source = source.subList(min(part.size + 1, source.size), source.size)

            if (part.isNotEmpty()) {
                partitions.add(part)
            }
        }

        return partitions.toList()
   }
}