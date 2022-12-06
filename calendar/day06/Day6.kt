package day06

import Day
import Lines

class Day6 : Day() {
    override fun part1(input: Lines): Any {
        return input.map { findMarker(it, 4) }
    }

    override fun part2(input: Lines): Any {
        return input.map { findMarker(it, 14) }
    }

    private fun findMarker(data: String, markerSize: Int): Int {
        return data
            .windowed(markerSize)
            .withIndex()
            .find {
                it.value.toSet().size == markerSize
            }!!
            .index + markerSize
    }
}