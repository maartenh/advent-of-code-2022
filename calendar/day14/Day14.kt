package day14

import Day
import Lines
import tools.Parser
import tools.int
import tools.list
import tools.map
import tools.run
import tools.seq
import tools.string

class Day14 : Day() {
    override fun part1(input: Lines): Any {
        val paths = input.map { run(path(), it) }

        val rocks = paths.flatMap { it.allPoints() }.toSet()
        val sand = mutableSetOf<Point>()

        val rangeX = (rocks.minOf { it.x}) .. (rocks.maxOf { it.x })
        val rangeY = 0 .. rocks.maxOf { it.y }

        allSand@ while (true) {
            var location = Point(500, 0)

            singleUnit@ while (true) {
                val nextLocation = listOf(
                    Point(location.x, location.y + 1),
                    Point(location.x - 1, location.y + 1),
                    Point(location.x + 1, location.y + 1),
                ).firstOrNull { it !in rocks && it !in sand }

                if (nextLocation == null) {
                    sand.add(location)
                    break@singleUnit
                } else if (nextLocation.x in rangeX && nextLocation.y in rangeY) {
                    location = nextLocation
                    continue@singleUnit
                } else {
                    break@allSand
                }
            }

        }

        return sand.size
    }

    override fun part2(input: Lines): Any {
        val paths = input.map { run(path(), it) }

        val rocks = paths.flatMap { it.allPoints() }.toSet()
        val sand = mutableSetOf<Point>()

        val floorY = rocks.maxOf { it.y } + 2

        allSand@ while (true) {
            var location = Point(500, 0)

            singleUnit@ while (true) {
                val nextLocation = listOf(
                    Point(location.x, location.y + 1),
                    Point(location.x - 1, location.y + 1),
                    Point(location.x + 1, location.y + 1),
                ).firstOrNull { it !in rocks && it !in sand && it.y < floorY }

                if (nextLocation == null) {
                    sand.add(location)
                    if (location == Point(500, 0)) {
                        break@allSand
                    }
                    break@singleUnit
                } else {
                    location = nextLocation
                    continue@singleUnit
                }
            }

        }

        return sand.size
    }

    data class Point(val x: Int, val y: Int)
    data class Path(val points: List<Point>) {
        fun allPoints(): List<Point> =
            points.windowed(2).flatMap {
                val rangeX = if (it[0].x <it[1].x) { it[0].x..it[1].x } else { it[1].x..it[0].x }
                val rangeY = if (it[0].y <it[1].y) { it[0].y..it[1].y } else { it[1].y..it[0].y }
                rangeX.flatMap { x ->
                    rangeY.map {y ->
                        Point(x, y)
                    }
                }
            }.distinct()

    }

    private fun path(): Parser<Path> =
        point().list(string(" -> ")).map { Path(it) }
    private fun point(): Parser<Point> =
        (int() seq string(",") seq int()).map { Point(it.first.first, it.second) }
}

